package com.vipsfin.competition.stat;

import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.io.IoUtil;
import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import org.iq80.leveldb.DB;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Jay Wu
 */
public class Task {

    protected Trie<Result> brandTrie = new Trie<>();
    protected DB db;
    private static char[] array = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            .toCharArray();
    private static String numStr = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final Log log = LogFactory.get();
    protected Map<Integer, Integer> brandNames;
    protected Map<String, Integer> dBrandNames;

    protected int BATCH_SIZE = 100;

    protected BoundedPriorityQueue<Result> queue = new BoundedPriorityQueue<>(
            40,
            (o1, o2) -> {
                int level1 = -o1.getCount().compareTo(o2.getCount());
                if (level1 == 0) {
                    int level2 = -o1.getAmount().compareTo(o2.getAmount());

                    if (level2 == 0) {
                        return o1.getOrder().compareTo(o2.getOrder());
                    }

                    return level2;
                }

                return level1;
            }
    );

    //10进制转为其他进制，除留取余，逆序排列
    public static String tenToN(long number, int N) {
        Long rest = number;
        Stack<Character> stack = new Stack<Character>();
        StringBuilder result = new StringBuilder(0);
        while (rest != 0) {
            stack.add(array[new Long((rest % N)).intValue()]);
            rest = rest / N;
        }
        for (; !stack.isEmpty(); ) {
            result.append(stack.pop());
        }
        return result.length() == 0 ? "0" : result.toString();
    }

    //byte 数组与 int 的相互转换
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

//    public void loadData3(String path) {
//        AtomicLong counter = new AtomicLong();
//        // 东方亮 工艺细致 VIP_SH 487855247 2015-4-4
//        long a = System.currentTimeMillis();
//
//        FileUtil.readUtf8Lines(FileUtil.file(path), (LineHandler) line -> {
//            if (counter.incrementAndGet() % 100000 == 0) {
//                long b = System.currentTimeMillis();
//                log.info(b - a);
//                log.info(results());
//            }
//
//            Trie<Result>.Node brandNode = brandTrie.findNode(line);
//
//            if (brandNode == null || !brandNode.isWordEnding()) {
//                return;
//            }
//
//            String[] temp = line.split(" ");
//            int pos = temp.length;
//            String date = temp[--pos];
//            String amount = temp[--pos];
//            String location = temp[--pos];
//
//            brandNode.getValue().addAmount(new BigDecimal(amount));
//
//            Trie.Node dataNode = dataTrie.insertAndGetLastNode(date + "_" + brandNode.getValue().getOrder(), 1);
//            if (dataNode.getCount() == 1) {
//                brandNode.getValue().addCount();
//            }
//
//            queue.remove(brandNode.getValue());
//            queue.offer(brandNode.getValue());
//        });
//    }
private Trie<BigDecimal> dataTrie = new Trie<>();

    public Integer getOrder(String brand) {
        byte[] value = db.get(brand.getBytes());
        if (value == null) {
            return null;
        }

        return byteArrayToInt(value);
    }

    public Integer getOrderFast(String brand) {
        Integer order = brandNames.get(brand.hashCode());
        if (order == null) {
            return null;
        }

        if (order == -1) {
            return dBrandNames.get(brand);
        }
        return order;
    }

    private Map<Integer, BigDecimal[]> amountMap = new HashMap<>();
    private Map<String, Boolean> countMap = new HashMap<>();

    public void loadBrand(String path) {
        AtomicInteger orderCounter = new AtomicInteger();

        File file = FileUtil.file(path);
//        File dbFile = new File(file.getParentFile().getAbsolutePath() + "/index");
//        boolean dbExist = dbFile.exists();
//
//        Options options = new Options();
//        options.createIfMissing(true);
//        try {
//            db = new Iq80DBFactory().open(dbFile, options);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        FileUtil.readUtf8Lines(file, (LineHandler) line -> {
//            if (!dbExist) {
//                db.put(line.getBytes(), intToByteArray(orderCounter.getAndIncrement()));
//
//                if (orderCounter.get() % 100000 == 0) {
//                    log.info(orderCounter.get());
//                    log.info(System.currentTimeMillis());
//                }
//            }
//        });

        brandNames = new HashMap<>();
        dBrandNames = new HashMap<>();

        orderCounter.set(0);
        FileUtil.readUtf8Lines(file, (LineHandler) line -> {
            orderCounter.incrementAndGet();
            // TODO Checksum
            if (brandNames.containsKey(line.hashCode())) {
                brandNames.put(line.hashCode(), -1);

                dBrandNames.put(line, orderCounter.get());
            } else {
                brandNames.put(line.hashCode(), orderCounter.get());
            }
        });

        log.info(brandNames.size() + "-" + dBrandNames.size());
    }

    public void split(String path) {
        log.info("Start to split");
        AtomicLong counter = new AtomicLong();
        File file = FileUtil.file(path);

        int fileSize = 50;
        Writer[] writers = new Writer[fileSize];
        for (int i = 0; i < writers.length; i++) {
            writers[i] = FileUtil.getWriter(file.getParentFile().getAbsolutePath() + "/s/" + i + ".txt",
                    Charset.defaultCharset(), true);
        }

        FileUtil.readUtf8Lines(FileUtil.file(path), (LineHandler) line -> {
            String[] temp = line.split(" ");
            int pos = temp.length;
            String date = temp[--pos];
            String amount = temp[--pos];
            String location = temp[--pos];
            String desc = temp[--pos];

            StringBuilder brand = new StringBuilder();
            for (int i = 0; i < pos; i++) {
                brand.append(temp[i]);
                if (i < pos - 1) {
                    brand.append(" ");
                }
            }

            String brandKey = brand.toString();
            Integer order = getOrderFast(brandKey);
            if (order == null) {
                return;
            }

            int index = Math.abs(brandKey.hashCode()) % fileSize;
            try {
                writers[index].append(date.replace("-", ""))
                        .append(",")
                        .append(brandKey)
                        .append(",")
                        .append(amount)
                        .append(",")
                        .append(order.toString())
                        .append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            counter.incrementAndGet();

            if (counter.get() % 1000000 == 0) {
                log.info(brandKey + "_" + counter.get());
            }
        });

        for (Writer writer : writers) {
            IoUtil.close(writer);
        }
    }

    public void loadData(String path) {
        log.info("Start to loadData");

        File file = FileUtil.file(path);
        final AtomicLong a = new AtomicLong(System.currentTimeMillis());
        AtomicLong counter = new AtomicLong();


        // 东方亮 工艺细致 VIP_SH 487855247 2015-4-4
        FileUtil.readUtf8Lines(file, new LineHandler() {
            @Override
            public void handle(String line) {
                if (counter.incrementAndGet() % 500000 == 0) {
                    long b = System.currentTimeMillis();
                    log.info((b - a.get()) + "");
                    a.set(b);
                }

                String[] temp = line.split(",");
                int date = Integer.valueOf(temp[0]);
                String brandKey = temp[1];
                String amount = temp[2];
                int order = Integer.valueOf(temp[3]);

                String orderKey = tenToN(order, 62);
                String dateKey = tenToN(date, 62);
                String key = orderKey + dateKey;

                BigDecimal[] amountAndCount = amountMap.get(order);

                if (amountAndCount == null) {
                    amountAndCount = new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO};
                    amountMap.put(order, amountAndCount);
                }

                BigDecimal totalAmount = amountAndCount[0];
                BigDecimal totalCount = amountAndCount[1];
//                Trie<BigDecimal>.Node orderNode = dataTrie.insertAndGetLastNode(orderKey, 0);
//                Trie<BigDecimal>.Node dateNode = dataTrie.insertAndGetLastNode(dateKey, orderNode, 1);
                // 去除不重复+1
                if (countMap.put(key, true) == null) {
                    totalCount = totalCount.add(new BigDecimal(1));
                    amountAndCount[1] = totalCount;
                }

                totalAmount = totalAmount.add(new BigDecimal(Integer.valueOf(amount)));
                amountAndCount[0] = totalAmount;

                Result result = new Result()
                        .setName(brandKey)
                        .setOrder(order)
                        .setAmount(totalAmount)
                        .setCount(totalCount.longValue());
                queue.remove(result);
                queue.offer(result);
            }
        });
    }

    public void count() {

    }

    public List<Result> result() {
        return queue.toList();
    }

    public void sort(Result result) {
        queue.offer(result);
    }

    public static class Result {
        private String name;
        private Integer order = 0;
        private BigDecimal amount = new BigDecimal(0);
        private Long count = 0L;

        public String getName() {
            return name;
        }

        public Result setName(String name) {
            this.name = name;
            return this;
        }

        public BigDecimal addAmount(BigDecimal newValue) {
            amount = amount.add(newValue);
            return amount;
        }


        public BigDecimal getAmount() {
            return amount;
        }

        public Result setAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Long addCount() {
            return count++;
        }

        public Long getCount() {
            return count;
        }

        public Result setCount(Long count) {
            this.count = count;
            return this;
        }

        public Integer getOrder() {
            return order;
        }

        public Result setOrder(Integer order) {
            this.order = order;
            return this;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Result result = (Result) o;
            return Objects.equals(order, result.order);
        }

        @Override
        public int hashCode() {

            return Objects.hash(order);
        }
    }
}