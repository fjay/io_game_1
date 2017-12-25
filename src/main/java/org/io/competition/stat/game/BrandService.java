package org.io.competition.stat.game;

import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.io.IoUtil;
import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import it.unimi.dsi.bits.TransformationStrategies;
import it.unimi.dsi.fastutil.io.BinIO;
import it.unimi.dsi.sux4j.mph.GOV4Function;
import org.io.competition.stat.util.Stopwatch;
import org.io.competition.stat.util.Util;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jay Wu
 */
@Service
public class BrandService {

    private static final Log log = LogFactory.get();

    private GOV4Function<CharSequence> nameOrderCache;
    private DB orderNameCache;

    public void load(String path) throws Exception {
        log.info("Loading {}", path);
        File file = FileUtil.file(path);

        loadOrderNameCache(file);
        loadNameOrderCache(file);
    }

    public Integer getOrder(String brand) {
        long order = nameOrderCache.getLong(brand);
        if (order == -1) {
            return null;
        }

        return Long.valueOf(order).intValue();
    }

    public String getName(Integer order) {
        byte[] value = orderNameCache.get(Util.intToByteArray(order));

        if (value == null) {
            log.error("order:{}", order);
            return null;
        }

        return new String(value);
    }

    public void clear() {
    }

    public void close() {
        IoUtil.close(orderNameCache);
    }

    public long getSize() {
        return nameOrderCache.size64();
    }

    private void loadNameOrderCache(File brandFile) throws Exception {

        String basePath = brandFile.getParentFile().getAbsolutePath();

        FileUtil.mkdir(basePath);
        File orderNameCacheFile = new File(basePath + "/o");
        FileUtil.mkdir(basePath);

        boolean isCacheFileExists = orderNameCacheFile.exists();

        if (orderNameCache == null) {
            Options options = new Options();
            options.createIfMissing(true);

            orderNameCache = Iq80DBFactory.factory.open(orderNameCacheFile, options);
        }

        Stopwatch stopwatch = Stopwatch.create().start();

        if (!isCacheFileExists) {
            AtomicInteger counter = new AtomicInteger(0);

            FileUtil.readUtf8Lines(brandFile, (LineHandler) line -> {
                orderNameCache.put(Util.intToByteArray(counter.getAndIncrement()), line.getBytes());

                if (counter.get() % 1000000 == 0) {
                    log.info("Loading {}, size: {}", brandFile.getAbsolutePath(), counter.get());
                }
            });
        }

        stopwatch.stop();

        log.info("loadNameOrderCache {}, total:{}, duration:{}",
                brandFile.getAbsolutePath(), getSize(), stopwatch.duration());
    }

    private void loadOrderNameCache(File brandFile) throws Exception {
        File cacheFile = new File(brandFile.getAbsolutePath() + ".order");
        boolean isCacheFileExists = cacheFile.exists();
        Stopwatch stopwatch = Stopwatch.create().start();

        if (!isCacheFileExists) {
            nameOrderCache = new GOV4Function.Builder<CharSequence>()
                    .keys(FileUtil.readUtf8Lines(brandFile))
                    .transform(TransformationStrategies.utf16())
                    .signed(32)
                    .build();
            BinIO.storeObject(nameOrderCache, cacheFile);
        } else {
            //noinspection unchecked
            nameOrderCache = (GOV4Function<CharSequence>) BinIO.loadObject(cacheFile);
        }

        stopwatch.stop();
        log.info("loadOrderNameCache {}, total:{}, duration:{}",
                brandFile.getAbsolutePath(), getSize(), stopwatch.duration());
    }
}