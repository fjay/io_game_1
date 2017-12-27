package org.io.competition.stat.game.one;

import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import org.io.competition.stat.game.BrandService;
import org.io.competition.stat.util.Stopwatch;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Jay Wu
 */
public class Record1Service {
    private final Log log = LogFactory.get();

    private BrandService brandService;

    public Record1Service(BrandService brandService) {
        this.brandService = brandService;
    }


    private SynchronousQueue<List<String>> q = new SynchronousQueue<List<String>>();

    public BoundedPriorityQueue<Map.Entry<Integer, BigDecimal>> sort(String path) {
        log.info("Loading {}", path);

        Stopwatch stopwatch = Stopwatch.create().start();
        AtomicLong counter = new AtomicLong();
        BoundedPriorityQueue<Map.Entry<Integer, BigDecimal>> queue = newQueue();

        RecordLineHandler lineHandler = newRecordLineHandler(counter);


        ProcessLineThread t = new ProcessLineThread(lineHandler, q);
        t.start();
        //FileUtil.readUtf8Lines(FileUtil.file(path), lineHandler);
        try{
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
            byte[] rest = null;
            byte[] arr = new byte[10 * 1024 * 1024];
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
                q.put(lines);
            }
        } catch(Exception e){

        }

        try {
            q.put(new ArrayList<>());
            t.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.close();

        for (Map.Entry<Integer, BigDecimal> entry : lineHandler.getRecordAmountMap().entrySet()) {
            queue.offer(entry);
        }

        stopwatch.stop();
        log.info("Loaded {} size:{}, duration:{}", path, counter.get(), stopwatch.duration());
        return queue;
    }

    protected RecordLineHandler newRecordLineHandler(AtomicLong counter) {
        return new RecordLineHandler(brandService, counter);
    }

    public BoundedPriorityQueue<Map.Entry<Integer, BigDecimal>> newQueue() {
        return new BoundedPriorityQueue<>(
                40,
                (o1, o2) -> {
                    int result = -o1.getValue().compareTo(o2.getValue());

                    if (result == 0) {
                        return o1.getKey().compareTo(o2.getKey());
                    }

                    return result;
                }
        );
    }

    private class ProcessLineThread extends Thread{

        private RecordLineHandler lineHandler;
        private SynchronousQueue<List<String>> q;
        private boolean run = true;

        public ProcessLineThread(RecordLineHandler lineHandler, SynchronousQueue<List<String>> q) {
            this.lineHandler = lineHandler;
            this.q = q;
        }

        @Override
        public void run() {
            try{
                while(run){
                    List<String> lists = q.take();
                    for(String line : lists){
                        this.lineHandler.handle(line);
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            }

        }

        public void close(){
            this.run =false;
        }
    }
}