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

    private static Map<Integer, Integer> nameOrderLevel1Cache = new HashMap<>();
    private static Map<String, Integer> nameOrderLevel2Cache = new HashMap<>();
    private static DB nameOrderLevel3Cache;
    private static DB orderNameCache;

    private static int size;

//    public static Trie<BigDecimal> load(String path) {
//        Trie<BigDecimal> brandTrie = new Trie<>();
//        AtomicInteger counter = new AtomicInteger(0);
//
//        FileUtil.readUtf8Lines(FileUtil.file(path), (LineHandler) line -> {
//            String[] temp = line.split(",");
//            Trie<BigDecimal>.Node node = brandTrie.insertAndGetLastNode(temp[1], 0);
//            node.setOrder(Integer.valueOf(temp[0]));
//            node.setValue(BigDecimal.ZERO);
//        });
//
//        log.info("Loaded {}, size: {}", path, counter.get());
//        return brandTrie;
//    }

    public static List<File> split(String path, int fileSize) {
        File file = FileUtil.file(path);
        String basePath = file.getParentFile().getAbsolutePath() + "/n";
        AtomicInteger orderCounter = new AtomicInteger();

        return Util.split(path, basePath, fileSize, parameters -> {
            String line = parameters[0];
            int index = Math.abs(line.hashCode()) % fileSize;
            return new Pair<>(index, orderCounter.incrementAndGet() + "," + line + "\n");
        });
    }


    public static void load(String path) throws IOException {

        File file = FileUtil.file(path);
        String basePath = file.getParentFile().getAbsolutePath();

        FileUtil.mkdir(basePath);
        File level2CacheFile = new File(basePath + "/n");
        File orderNameCacheFile = new File(basePath + "/o");
        boolean isLevel2CacheFileExists = level2CacheFile.exists();

        Options options = new Options();
        options.createIfMissing(true);

        nameOrderLevel3Cache = Iq80DBFactory.factory.open(level2CacheFile, options);
        orderNameCache = Iq80DBFactory.factory.open(orderNameCacheFile, options);

        AtomicInteger counter = new AtomicInteger(0);
        FileUtil.readUtf8Lines(file, (LineHandler) line -> {
            int order = counter.incrementAndGet();
            int level1Key = line.hashCode();

            if (nameOrderLevel1Cache.put(level1Key, order) != null) {
                nameOrderLevel1Cache.put(level1Key, -1);
            }

            if (!isLevel2CacheFileExists) {
                nameOrderLevel3Cache.put(line.getBytes(), Util.intToByteArray(order));
                orderNameCache.put(Util.intToByteArray(order), line.getBytes());
            }

            if (counter.get() % 1000000 == 0) {
                log.info("Loading {}, size: {}", path, counter.get());
            }
        });

        size = counter.get();
        log.info("Loaded {}, total:{}, level1:{}", path, size, nameOrderLevel1Cache.size());
    }

    public static Integer getOrder(String brand) {
        Integer value = nameOrderLevel1Cache.get(brand.hashCode());
        if (value == null) {
            return null;
        }

        if (value == -1) {
            value = nameOrderLevel2Cache.get(brand);
            if (value == null) {
                byte[] valueOfLevel2 = nameOrderLevel3Cache.get(brand.getBytes());
                if (valueOfLevel2 == null) {
                    return null;
                }

                value = Util.byteArrayToInt(valueOfLevel2);
                nameOrderLevel2Cache.put(brand, value);
            }
        }

        return value;
    }

    public static String getName(Integer order) {
        return new String(orderNameCache.get(Util.intToByteArray(order)));
    }

    public static void clear() {
        nameOrderLevel1Cache.clear();
    }

    public static void close() {
        IoUtil.close(nameOrderLevel3Cache);
        IoUtil.close(orderNameCache);
    }

    public static int getSize() {
        return size;
    }
}