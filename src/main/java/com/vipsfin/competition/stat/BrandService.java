package com.vipsfin.competition.stat;

import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.io.IoUtil;
import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import javafx.util.Pair;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BrandService {

    private static final Log log = LogFactory.get();

    private Map<Integer, Integer> nameOrderLevel1Cache = new HashMap<>();
    private Map<String, Integer> nameOrderLevel2Cache = new HashMap<>();
    private DB orderNameCache;

    private int size;

    public List<File> split(String path, int fileSize) {
        File file = FileUtil.file(path);
        String basePath = file.getParentFile().getAbsolutePath() + "/n";
        AtomicInteger orderCounter = new AtomicInteger();

        return Util.split(path, basePath, fileSize, parameters -> {
            String line = parameters[0];
            int index = Math.abs(line.hashCode()) % fileSize;
            return new Pair<>(index, orderCounter.incrementAndGet() + "," + line + "\n");
        });
    }

    public void load(String path) throws IOException {
        File file = FileUtil.file(path);
        String basePath = file.getParentFile().getAbsolutePath();

        FileUtil.mkdir(basePath);
        File orderNameCacheFile = new File(basePath + "/o");
        boolean isCacheFileExists = orderNameCacheFile.exists();

        Options options = new Options();
        options.createIfMissing(true);

        orderNameCache = Iq80DBFactory.factory.open(orderNameCacheFile, options);

        AtomicInteger counter = new AtomicInteger(0);
        FileUtil.readUtf8Lines(file, (LineHandler) line -> {
            int order = counter.incrementAndGet();
            int level1Key = line.hashCode();

            if (nameOrderLevel1Cache.put(level1Key, order) != null) {
                nameOrderLevel1Cache.put(level1Key, -1);
                nameOrderLevel2Cache.put(line, order);
            }

            if (!isCacheFileExists) {
                orderNameCache.put(Util.intToByteArray(order), line.getBytes());
            }

            if (counter.get() % 1000000 == 0) {
                log.info("Loading {}, size: {}", path, counter.get());
            }
        });

        size = counter.get();
        log.info("Loaded {}, total:{}, level1:{}",
                path, size, nameOrderLevel1Cache.size(), nameOrderLevel2Cache.size());

        counter.set(0);
        FileUtil.readUtf8Lines(file, (LineHandler) line -> {
            if (counter.incrementAndGet() % 1000000 == 0) {
                log.info("Loading level2 {}, counter: {}", path, counter.get());
            }

            Integer value = getOrder(line);

            if (value == null) {
                nameOrderLevel2Cache.put(line, counter.get());
            }
        });

        log.info("Loaded {}, total:{}, level1:{}, level2:{}",
                path, size, nameOrderLevel1Cache.size(), nameOrderLevel2Cache.size());
    }

    public Integer getOrder(String brand) {
        Integer value = nameOrderLevel1Cache.get(brand.hashCode());
        if (value == null) {
            return null;
        }

        if (value == -1) {
            value = nameOrderLevel2Cache.get(brand);
        }

        return value;
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
        nameOrderLevel2Cache.clear();
        nameOrderLevel1Cache.clear();
    }

    public void close() {
        IoUtil.close(orderNameCache);
    }

    public int getSize() {
        return size;
    }
}