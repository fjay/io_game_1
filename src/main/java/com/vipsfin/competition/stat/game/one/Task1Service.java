package com.vipsfin.competition.stat.game.one;

import com.vipsfin.competition.stat.TaskService;
import com.vipsfin.competition.stat.game.BrandService;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Task1Service implements TaskService{

    private BrandService brandService;

    public Task1Service(BrandService brandService) {
        this.brandService = brandService;
    }

    @Override
    public List<String> run(String recordPath) throws Exception {
        long time = System.currentTimeMillis();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(recordPath)));
        String line = null;
        Map<Integer,Long> map = new HashMap<Integer, Long>();
        StringBuilder sa = new StringBuilder(8);
        while((line = reader.readLine()) != null){
            String[] arr = line.split("\\s");
            String[] ar = arr[arr.length - 1].split("-");
            sa.append(ar[0]);
            if(ar[1].length() == 1){
                sa.append("0");
            }
            sa.append(ar[1]);
            if(ar[2].length() == 1){
                sa.append("0");
            }
            sa.append(ar[2]);
            Integer date = Integer.parseInt(sa.toString());
            sa.delete(0,sa.length());
            Long num = Long.parseLong(arr[arr.length - 2]);
            String c = arr[arr.length - 3];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < arr.length - 4; i++) {
                if (i > 0) {
                    sb.append(" ");
                }
                sb.append(arr[i]);
            }
            String brandName = sb.toString();
            Integer id = brandService.getOrder(brandName);
            if(id == null){
                continue;
            }
            if(c.equals("VIP_NH")){
                if(date >= 20110101 && date <= 20161231){
                    if(map.containsKey(id)){
                        map.put(id, map.get(id) + num);
                    } else {
                        map.put(id, num);
                    }
                }
            }
        }
        brandService.clear();
        BoundedPriorityQueue<Integer> queue = new BoundedPriorityQueue<Integer>(40, new comp(map));
        for(Map.Entry entry : map.entrySet()){
            queue.offer((Integer) entry.getKey());
        }

        List<String> list = new ArrayList<String>(40);
        while(!queue.isEmpty()){
            list.add(brandService.getName((Integer)queue.poll()));
        }
        Collections.reverse(list);
        return list;
    }

    public class comp implements Comparator<Integer> {

        private Map<Integer, Long> count;

        public comp(Map<Integer, Long> count){
            this.count = count;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            long result = count.get(o1) - count.get(o2);
            if(result == 0){
                return o1 > o2 ? -1 : 1 ;
            }
            return  result > 0 ? -1 : 1;
        }
    }

}
