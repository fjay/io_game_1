package com.vipsfin.competition.stat;

import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import org.junit.Test;

import java.io.IOException;

public class RecordServiceTest {

    @Test
    public void split() throws IOException {
        BrandService.load("/Users/fjay/Documents/work/vip/code/game/io/test/brand_name.txt");
        System.out.println(RecordService.split("/Users/fjay/Documents/work/vip/code/game/io/test/matchs.txt", 50));
    }

    @Test
    public void x() {
        long a = System.currentTimeMillis();
        BoundedPriorityQueue<Result2> resultQueue = RecordService.newQueue();

        for (int i = 0; i < 2; i++) {
            BoundedPriorityQueue<Result2> tempQueue = RecordService.sort("/Users/fjay/Documents/work/vip/code/game/io/test/r/" + i + ".txt");

            for (Result2 result2 : tempQueue.toList()) {
                resultQueue.offer(result2);
            }
        }

        long b = System.currentTimeMillis();
        System.out.println(b - a);

        System.out.println(resultQueue.toList());
    }
}