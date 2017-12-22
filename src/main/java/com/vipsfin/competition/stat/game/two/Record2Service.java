package com.vipsfin.competition.stat.game.two;

import com.vipsfin.competition.stat.game.BrandService;
import com.vipsfin.competition.stat.util.Stopwatch;
import com.vipsfin.competition.stat.util.Util;
import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import javafx.util.Pair;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Record2Service {
    private final Log log = LogFactory.get();

    private BrandService brandService;

    public Record2Service(BrandService brandService) {
        this.brandService = brandService;
    }

    public BoundedPriorityQueue<Result2> sort(String path) {
        log.info("Loading {}", path);

        Stopwatch stopwatch = Stopwatch.create().start();
        AtomicLong counter = new AtomicLong();
        BoundedPriorityQueue<Result2> queue = newQueue();
        File file = FileUtil.file(path);

        FileUtil.readUtf8Lines(file, new LineHandler() {
            // brand -> (date -> amount)
            private Map<Integer, Map<Integer, BigDecimal>> dataMap = new HashMap<>();

            @Override
            public void handle(String line) {
                counter.incrementAndGet();

                String[] temp = line.split(",");
                Integer date = Integer.valueOf(temp[0]);
                Integer brandOrder = Integer.valueOf(temp[1]);
                Integer amount = Integer.valueOf(temp[2]);

                Map<Integer, BigDecimal> dateAmountMap = dataMap.computeIfAbsent(brandOrder, k -> new HashMap<>());
                BigDecimal totalAmount = dateAmountMap.computeIfAbsent(date, k -> BigDecimal.ZERO);
                totalAmount = totalAmount.add(new BigDecimal(amount));
                dateAmountMap.put(date, totalAmount);

                Result2 result = new Result2()
                        .setOrder(brandOrder)
                        .setAmount(totalAmount)
                        .setCount((long) dateAmountMap.size());

                queue.remove(result);
                queue.offer(result);
            }
        });

        stopwatch.stop();
        log.info("Loaded {} size:{}, duration:{}", path, counter.get(), stopwatch.duration());
        return queue;
    }

    public List<File> split(String path, int writerBufferLength, int fileSize) {
        File file = FileUtil.file(path);
        String basePath = file.getParentFile().getAbsolutePath() + "/r";
        log.info("Loading {}", path);

        List<File> files = Util.split(path, basePath, writerBufferLength, fileSize,
                parameters -> {
                    String line = parameters[0];

                    String[] temp = line.split(" ");
                    int pos = temp.length;
                    String date = temp[--pos];
                    Integer amount = Integer.valueOf(temp[--pos]);
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
                    Integer order = brandService.getOrder(brandKey);
                    if (order == null) {
                        return null;
                    }

                    String record = date.replace("-", "") +
                            "," +
                            order +
                            "," +
                            amount +
                            "\n";
                    int index = order % fileSize;
                    return new Pair<>(index, record);
                });

        brandService.clear();

        return files;
    }

    public BoundedPriorityQueue<Result2> newQueue() {
        return new BoundedPriorityQueue<>(
                40,
                (o1, o2) -> {
                    try {
                        int level1 = -o1.getCount().compareTo(o2.getCount());
                        if (level1 == 0) {
                            int level2 = -o1.getAmount().compareTo(o2.getAmount());

                            if (level2 == 0) {
                                return o1.getOrder().compareTo(o2.getOrder());
                            }

                            return level2;
                        }

                        return level1;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
        );
    }
}