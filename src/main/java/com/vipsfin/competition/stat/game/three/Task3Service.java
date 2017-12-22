package com.vipsfin.competition.stat.game.three;

import com.vipsfin.competition.stat.TaskService;
import com.vipsfin.competition.stat.game.BrandService;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;

import java.io.*;
import java.util.*;

public class Task3Service implements TaskService {

    private BrandService service;

    public Task3Service(BrandService brandService) {
        this.service = brandService;
    }

    @Override
    public List<String> run(String recordPath) throws Exception {
//        //split file
        String line = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(recordPath)));

        Writer[] writers = new Writer[10];
        StringBuilder[] sbs = new StringBuilder[10];


        for (int i = 0; i < writers.length; i++) {
            writers[i] = new BufferedWriter(new FileWriter("e:/project/io/temp3/" + i + ".txt"));
            sbs[i] = new StringBuilder(4096);
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
            String date = arr[arr.length - 1];
            String num = arr[arr.length - 2];
            int index = i % 10;
            StringBuilder s = sbs[index];
            s.append(i);
            s.append("@");
            s.append(date);
            s.append(":");
            s.append(num);
            s.append("\n");
            if (s.length() >= 4000) {
                writers[index].write(s.toString());
                s.delete(0, s.length());
            }

        }
        for (int i = 0; i < 10; i++) {
            StringBuilder s = sbs[i];
            if (s.length() > 0) {
                writers[i].write(s.toString());
            }
            writers[i].flush();
            writers[i].close();
        }
        sbs = null;
        reader.close();
        this.service.clear();
        //read file to calculate
        Map<String, List> count = new HashMap<String, List>();
        IOComparator comp = new IOComparator(count);
        BoundedPriorityQueue<Object[]> queue = new BoundedPriorityQueue<Object[]>(40, comp);
        Object[] mixTimes = null;
        for (int i = 0; i < 26; i++) {
            //计算出现次数的
            Map<String, Integer> map = new HashMap<String, Integer>();
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("e:/project/io/temp3/" + i + ".txt")));
            while ((line = r.readLine()) != null) {
                if ("".equals(line)) {
                    continue;
                }
                String[] arr = line.split(":");
                String brandName = arr[0].split("@")[0];
                if (map.containsKey(arr[0])) {
                    map.put(arr[0], map.get(arr[0]) + 1);
                } else {
                    map.put(arr[0], 1);
                }
                List list = count.get(brandName);
                if (list != null) {
                    list.add(arr[1]);
                } else {
                    List l = new ArrayList(1);
                    l.add(arr[1]);
                    count.put(brandName, l);
                }
            }
            r.close();
            Map<String, Integer> filter = new HashMap<>();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String brandName = entry.getKey().split("@")[0];
                if (filter.get(brandName) != null) {
                    if (filter.get(brandName) < entry.getValue()) {
                        filter.put(brandName, entry.getValue());
                    }
                } else {
                    filter.put(brandName, entry.getValue());
                }
            }

            for (Map.Entry<String, Integer> entry : filter.entrySet()) {
                if (queue.size() < 40) {
                    queue.offer(new Object[]{entry.getKey(), entry.getValue()});
                    mixTimes = queue.peek();
                } else {
                    if (entry.getValue() > (Integer) mixTimes[1]) {
                        Object[] o = queue.poll();
                        queue.offer(new Object[]{entry.getKey(), entry.getValue()});
                        count.remove(o[0]);
                        mixTimes = queue.peek();
                    } else if (entry.getValue() == (Integer) mixTimes[1]) {
                        Object[] a = new Object[]{entry.getKey(), entry.getValue()};
                        if (comp.compare(a, mixTimes) < 0) {
                            Object[] t = queue.poll();
                            count.remove(t[0]);
                            queue.offer(a);
                            mixTimes = queue.peek();
                        } else {
                            count.remove(entry.getKey());
                        }
                    } else {
                        count.remove(entry.getKey());
                    }
                }
            }
        }

        List<String> lists = new ArrayList<>(40);
        int i = 39;
        while (!queue.isEmpty()) {
            Object[] l = queue.poll();
            lists.add(service.getName(Integer.parseInt(l[0].toString())));
            Collections.reverse(lists);
        }
        return lists;
    }

    private class IOComparator implements Comparator<Object[]> {

        private Map<String, List> count;

        public IOComparator(Map<String, List> count) {
            this.count = count;
        }


        @Override
        public int compare(Object[] o1, Object[] o2) {
            if ((Integer) o1[1] == (Integer) o2[1]) {
                List list1 = count.get(o1[0]);
                List list2 = count.get(o2[0]);
                Long result1 = 0l;
                Long result2 = 0l;
                for (int i = 0; i < Math.max(list1.size(), list2.size()); i++) {
                    if (i < list1.size()) {
                        result1 += Long.parseLong(list1.get(i).toString());
                    }
                    if (i < list2.size()) {
                        result2 += Long.parseLong(list2.get(i).toString());
                    }
                }
                if (list1.size() > 1) {
                    list1.clear();
                    list1.add(result1);
                }
                if (list2.size() > 1) {
                    list2.clear();
                    list2.add(result2);
                }
                Long result = result1 - result2;
                if (result == 0l) {
                    return -1 * Integer.parseInt(o1[0].toString()) - Integer.parseInt(o2[0].toString());
                } else {
                    return result > 0 ? -1 : 1;
                }
            } else {
                return -1 * ((Integer) o1[1] - (Integer) o2[1]);
            }
        }
    }
}
