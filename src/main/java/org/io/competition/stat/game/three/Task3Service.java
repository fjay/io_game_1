package org.io.competition.stat.game.three;

import com.xiaoleilu.hutool.lang.Filter;
import org.io.competition.stat.TaskService;
import org.io.competition.stat.game.BrandService;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

public class Task3Service implements TaskService {

    private BrandService service;

    public Task3Service(BrandService brandService) {
        this.service = brandService;
    }

    @Override
    public List<String> run(String recordPath) throws Exception {
//        //split file
        long begin = System.currentTimeMillis();
        String line = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(recordPath)));

        DataOutputStream[] writers = new DataOutputStream[10];
        //StringBuilder[] sbs = new StringBuilder[10];


        for (int i = 0; i < writers.length; i++) {
            writers[i] = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("/home/roger/io/temp3/" + i + ".txt")));
        }


        while ((line = reader.readLine()) != null) {
            String[] arr = line.split("\\s");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < arr.length - 4; i++) {
                if (i > 0) {
                    sb.append(" ");
                }
                sb.append(arr[i]);
            }
            String brandName = sb.toString();
            Integer i = this.service.getOrder(brandName);
            if(i == null){
                continue;
            }
            sb.delete(0, sb.length());
            int index = i % 10;
            DataOutputStream dos = writers[index];
            String dateStr = arr[arr.length - 1];
            String[] datearr = dateStr.split("-");
            sb.append(datearr[0]);
            if(datearr.length == 1){
                sb.append("0");
            }
            sb.append(datearr[1]);
            if(datearr.length == 2){
                sb.append("0");
            }
            sb.append(datearr[2]);
            Integer date = Integer.parseInt(sb.toString());
            Long num = Long.parseLong(arr[arr.length - 2]);
            dos.writeInt(i);
            dos.writeInt(date);
            dos.writeLong(num);
        }
        for (int i = 0; i < 10; i++) {
            writers[i].flush();
            writers[i].close();
        }
        reader.close();
        this.service.clear();
        System.out.println(System.currentTimeMillis() - begin);
        //read file to calculate
        Map<Integer, BigDecimal> count = new HashMap<Integer, BigDecimal>();
        IOComparator comp = new IOComparator(count);
        BoundedPriorityQueue<Integer[]> queue = new BoundedPriorityQueue<Integer[]>(40, comp);
        for (int i = 0; i < 10; i++) {
            //计算出现次数的
            Map<Integer, Map<Integer,Integer>> map = new HashMap<Integer, Map<Integer,Integer>>();
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream("/home/roger/io/temp3/" + i + ".txt")));
            while (dis.available() > 0) {
                if ("".equals(line)) {
                    continue;
                }
                int brandName = dis.readInt();
                int date = dis.readInt();
                long num = dis.readLong();
                Map<Integer, Integer> m = map.computeIfAbsent(brandName, c -> {
                    return new HashMap<Integer, Integer>();
                });
                m.compute(date, (k,v)->{
                    if(v == null){
                        return 1;
                    } else {
                        return v + 1;
                    }
                });
                count.compute(brandName,(k,v)->{
                   if(v == null){
                       return new BigDecimal(num);
                   } else {
                       return v.add(new BigDecimal(num));
                   }
                });
            }
            dis.close();
            Map<Integer, Integer> filter = new HashMap<>();
            for (Map.Entry<Integer, Map<Integer,Integer>> entry : map.entrySet()) {
                Integer max = entry.getValue().entrySet().stream().max(new Comparator<Map.Entry<Integer, Integer>>() {
                    @Override
                    public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                        return o1.getValue().compareTo(o2.getValue());
                    }
                }).get().getValue();
                filter.put(entry.getKey(),max);
            }

            for (Map.Entry<Integer, Integer> entry : filter.entrySet()) {
                if(queue.size() < 40){
                    queue.offer(new Integer[]{entry.getKey(), entry.getValue()});
                } else {
                    Integer[] last = queue.peek();
                    queue.offer(new Integer[]{entry.getKey(), entry.getValue()});
                    if(queue.peek()[0] == last[0]){
                        count.remove(last[0]);
                    } else {
                        count.remove(entry.getKey());
                    }
                }
            }
        }

        List<String> lists = new ArrayList<>(40);
        while (!queue.isEmpty()) {
            lists.add(service.getName(queue.poll()[0]));
            Collections.reverse(lists);
        }
        System.out.println(System.currentTimeMillis() - begin);
        return lists;
    }


    private class IOComparator implements Comparator<Integer[]> {

        private Map<Integer, BigDecimal> count;

        public IOComparator(Map<Integer, BigDecimal> count) {
            this.count = count;
        }


        @Override
        public int compare(Integer[] o1, Integer[] o2) {
            if (o1[1] == o2[1]) {
                BigDecimal a = count.get(o1[0]);
                BigDecimal b = count.get(o2[0]);
                if (a.equals(b)) {
                    return Integer.parseInt(o1[0].toString()) - Integer.parseInt(o2[0].toString());
                } else {
                    return a.compareTo(b) > 0 ? -1 : 1;
                }
            } else {
                return o2[1] - o1[1];
            }
        }
    }
}
