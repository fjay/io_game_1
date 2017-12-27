package org.io.competition.stat.game.three;

import com.xiaoleilu.hutool.lang.Filter;
import org.io.competition.stat.TaskService;
import org.io.competition.stat.game.BrandService;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import org.team4u.kit.core.lang.Pair;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

public class Task3Service implements TaskService {

    private BrandService brandService;

    public Task3Service(BrandService brandService) {
        this.brandService = brandService;
    }

    public static void main(String[] args) throws IOException {
        long begin = System.currentTimeMillis();
        String line = null;
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream("/home/roger/io/matchs_ok.txt"));
        byte[] rest = null;
        byte[] arr = new byte[1024*3];
        int len = 0;
        int beginPos = 0;
        while((len = bis.read(arr)) != -1){
            List<String> lines = new ArrayList<>();
            for(int i= 0 ; i < len; i++){
                if (arr[i] == '\n'){
                    if(rest != null){
                        lines.add(new String(rest) + new String(arr,beginPos,i - beginPos));
                        rest = null;
                    } else {
                        lines.add(new String(arr,beginPos,i - beginPos));
                    }
                    beginPos = i + 1;
                }
            }
            if(beginPos < arr.length){
                //有剩余
                rest = new byte[len - beginPos];
                System.arraycopy(arr, beginPos, rest, 0, rest.length);
            }
            beginPos = 0;
        }
        System.out.println("final use time  : " + (System.currentTimeMillis() - begin));
    }

    @Override
    public List<String> run(String recordPath) throws Exception {
        long begin = System.currentTimeMillis();
        String line = null;
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(recordPath));
        byte[] arr = new byte[2048];
        int len = 0;
        int beginPos = 0;
        while((len = bis.read(arr, beginPos, 2048 - beginPos)) != -1){
            List<String> lines = new ArrayList<>();
            for(int i= 0 ; i < len; i++){
                if (arr[i] == '\n'){
                    lines.add(new String(arr,beginPos,i - beginPos));
                    beginPos = i + 1;
                }
            }
            if(beginPos < len){
                //有剩余
                System.arraycopy(arr, beginPos, arr, 0, len - beginPos);

            }
            beginPos = len - beginPos;
        }
        
//        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(recordPath)));
//        while ((line = reader.readLine()) != null) {
//            ArrayList<Integer> temp = new ArrayList<>(10);
//            for (int j = 0; j < line.length(); j++) {
//                if (line.charAt(j) == ' ') {
//                    temp.add(j);
//                }
//            }
//
//            Integer order = brandService.getOrder(line.substring(0, temp.get(temp.size() - 4)));
//            if (order == null) {
//                return null;
//            }

//            String date = line.substring(temp.get(temp.size() - 1) + 1);
//            Integer amount = Integer.valueOf(line.substring(temp.get(temp.size() - 2) + 1, temp.get(temp.size() - 1)));
//        }


        System.out.println("final use time  : " + (System.currentTimeMillis() - begin));
        return new ArrayList<>();
    }
}