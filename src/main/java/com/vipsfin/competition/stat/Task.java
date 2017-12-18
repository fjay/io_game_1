package com.vipsfin.competition.stat;

import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Jay Wu
 */
public class Task {

    protected Trie<Result> brandTrie = new Trie<>();
    protected Trie dataTrie = new Trie();
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

    public void loadBrand(String path) throws IOException {
        AtomicLong orderCounter = new AtomicLong();
        SimpleTrie trie = new SimpleTrie();

        Options options = new Options();
        options.createIfMissing(true);
        DB db = new Iq80DBFactory().open(FileUtil.file("/Users/fjay/Documents/work/vip/code/game/game1_of_io/db"), options);

        List<String> list = new ArrayList<>();
        BufferedWriter writer = FileUtil.getWriter("F:\\name\\brand_name.txt", Charset.defaultCharset(), true);
        FileUtil.readUtf8Lines(FileUtil.file(path), (LineHandler) line -> {
            orderCounter.incrementAndGet();
//            db.put(line.getBytes(), new Result().setOrder(orderCounter.getAndIncrement()).s().getBytes());

//            Trie<Result>.Node node = brandTrie.insertAndGetLastNode(line, 1);
//            if (node.getValue() == null) {
//                node.setValue();
//            }
        });

        RandomAccessFile randomAccessFile = new RandomAccessFile(FileUtil.file("F:\\name\\brand_name.txt"), "rw");

        FileUtil.readUtf8Lines(FileUtil.file(path), (LineHandler) line -> {
            StringBuilder y = new StringBuilder();
            for (int i = 0; i < line.length(); i++) {
                char ch = line.charAt(i);
                int x = ch - ' ';
                y.append(x);
            }

            long pos = Long.valueOf(y.toString()) % orderCounter.longValue();
            try {
                randomAccessFile.seek(pos);
                randomAccessFile.writeLong(1l);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        System.out.println(orderCounter.longValue());
    }


    public void loadData(String path) {
        // 东方亮 工艺细致 VIP_SH 487855247 2015-4-4
        FileUtil.readUtf8Lines(FileUtil.file(path), (LineHandler) line -> {
            Trie<Result>.Node brandNode = brandTrie.findNode(line);

            if (brandNode == null || !brandNode.isWordEnding()) {
                return;
            }

            String[] temp = line.split(" ");
            int pos = temp.length;
            String date = temp[--pos];
            String amount = temp[--pos];
            String location = temp[--pos];

            brandNode.getValue().addAmount(new BigDecimal(amount));

            Trie.Node dataNode = dataTrie.insertAndGetLastNode(date + "_" + brandNode.getValue().getOrder(), 1);
            if (dataNode.getCount() == 1) {
                brandNode.getValue().addCount();
            }

            queue.remove(brandNode.getValue());
            queue.offer(brandNode.getValue());
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
        private BigDecimal amount = new BigDecimal(0);
        private Long count = 0L;
        private Long order = 0L;

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

        public Long getOrder() {
            return order;
        }

        public Result setOrder(Long order) {
            this.order = order;
            return this;
        }

        @Override
        public String toString() {
            return name;
        }

        public static Result d(String value) {
            String[] temp = value.split("_");
            return new Result().setAmount(new BigDecimal(temp[0]))
                    .setCount(Long.valueOf(temp[1]))
                    .setOrder(Long.valueOf(temp[2]));
        }

        public String s() {
            return amount + "_" + count + "_" + order;
        }
    }
}