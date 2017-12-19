package com.vipsfin.competition.stat;

import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import com.xiaoleilu.hutool.util.StrUtil;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

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
    protected Trie dataTrie = new Trie();
    protected DB db;
    private static char[] array = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            .toCharArray();
    private static String numStr = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final Log log = LogFactory.get();
    protected Result[] results;
    protected Map<Integer, Integer> brandNames;
    protected Map<String, Integer> dBrandNames;
    protected BoundedPriorityQueue<SortResult> queue = new BoundedPriorityQueue<>(
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
//                System.out.println(b - a);
//                System.out.println(results());
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

    public void loadBrand(String path) {
        AtomicInteger orderCounter = new AtomicInteger();

        File file = FileUtil.file(path);
        File dbFile = new File(file.getParentFile().getAbsolutePath() + "/index");
        boolean dbExist = dbFile.exists();

        Options options = new Options();
        options.createIfMissing(true);
        try {
            db = new Iq80DBFactory().open(dbFile, options);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileUtil.readUtf8Lines(file, (LineHandler) line -> {
            if (!dbExist) {
                db.put(line.getBytes(), intToByteArray(orderCounter.getAndIncrement()));

                if (orderCounter.get() % 100000 == 0) {
                    System.out.println(orderCounter.get());
                    System.out.println(System.currentTimeMillis());
                }
            }
        });

        brandNames = new HashMap<>();
        dBrandNames = new HashMap<>();

        orderCounter.set(0);
        FileUtil.readUtf8Lines(file, (LineHandler) line -> {
            // TODO Checksum
            if (brandNames.containsKey(line.hashCode())) {
                brandNames.put(line.hashCode(), -1);

                dBrandNames.put(line, orderCounter.get());
            } else {
                brandNames.put(line.hashCode(), orderCounter.get());
            }
        });

        System.out.println(brandNames.size() + "-" + dBrandNames.size());
    }

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

    public void split(String path) {
        log.info("Start to split");
        AtomicLong counter = new AtomicLong();
        File file = FileUtil.file(path);

        Writer[] writers = new Writer[100];
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

//            int index = Math.abs(brandKey.hashCode()) % 100;
//            try {
//                writers[index].append(date.replace("-", "")).append(",").append(brandKey).append("\n");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            counter.incrementAndGet();

            if (counter.get() % 1000000 == 0) {
                System.out.println(brandKey + "_" + counter.get());
            }
        });
    }

    public void loadData(String path) {
        log.info("Start to loadData");

        File file = FileUtil.file(path);
        final AtomicLong a = new AtomicLong(System.currentTimeMillis());
        AtomicLong counter = new AtomicLong();

        // 东方亮 工艺细致 VIP_SH 487855247 2015-4-4
        FileUtil.readUtf8Lines(file, (LineHandler) line -> {
            if (counter.incrementAndGet() % 100000 == 0) {
                long b = System.currentTimeMillis();
                System.out.println(b - a.get());
                System.out.println(queue.toList());
                a.set(b);
            }

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

            // count +
            String brandKey = brand.toString();
            Integer order = getOrderFast(brandKey);
            if (order == null) {
                return;
            }

            Result result = results[order];
            if (result == null) {
                result = new Result();
                results[order] = result;
            }

            result.addAmount(new BigDecimal(amount));

            String key = tenToN(Integer.valueOf(date.replace("-", "")), 62) + tenToN(order, 62);
            Trie.Node dataNode = dataTrie.insertAndGetLastNode(key, 1);
            if (dataNode.getCount() == 1) {
                result.addCount();
            }

            SortResult sortResult = new SortResult(result).setOrder(order);
            queue.remove(sortResult);
            queue.offer(sortResult);
        });
    }

    public void count() {

    }

    public List<SortResult> result() {
        return queue.toList();
    }

    public void sort(SortResult result) {
        queue.offer(result);
    }

    public static class Result {
        private BigDecimal amount = new BigDecimal(0);
        private Long count = 0L;

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

        public static Result d(String value) {
            if (StrUtil.isBlank(value)) {
                return null;
            }

            String[] temp = value.split("_");
            return new Result().setAmount(new BigDecimal(temp[0]))
                    .setCount(Long.valueOf(temp[1]));
        }

        @Override
        public String toString() {
            return count + "";
        }

        public String s() {
            return amount + "_" + count;
        }
    }

    public static class SortResult extends Result {

        private Integer order;

        public SortResult(Result result) {
            this.setCount(result.getCount());
            this.setAmount(result.getAmount());
        }

        public Integer getOrder() {
            return order;
        }

        public SortResult setOrder(Integer order) {
            this.order = order;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SortResult that = (SortResult) o;
            return Objects.equals(order, that.order);
        }

        @Override
        public int hashCode() {

            return Objects.hash(order);
        }
    }
}