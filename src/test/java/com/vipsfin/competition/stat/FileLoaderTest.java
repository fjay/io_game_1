package com.vipsfin.competition.stat;

import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * @author Jay Wu
 */
public class FileLoaderTest {

    @Test
    public void load() {
        List<String> brands = FileUtil.readUtf8Lines("brand_name_ok.txt");
        System.out.println(brands.size());
    }

    @Test
    public void x() {
        BoundedPriorityQueue<Integer> queue = new BoundedPriorityQueue<>(3, (o1, o2) -> -o1.compareTo(o2));
        queue.offer(3);
        queue.offer(1);
        queue.offer(2);
        queue.offer(10);
        queue.offer(0);

        System.out.println(queue.toList());
    }

    @Test
    public void count() {
        Trie trie = new Trie();
        System.out.println(trie.insert("a"));
        System.out.println(trie.insert("ab"));
        System.out.println(trie.insert("abcd"));

        System.out.println("----");

        System.out.println(trie.startWith("abc"));
        System.out.println(trie.contains("abc"));
        System.out.println(trie.contains("abcd"));
        System.out.println(trie.findNode("abcde").getCh());
    }

    @Test
    public void result() throws IOException {
        Task task = new Task();
        task.loadBrand("/Users/fjay/Documents/work/vip/code/game/io/test/brand_name.txt");
        task.loadData("/Users/fjay/Documents/work/vip/code/game/io/test/matchs.txt");
        task.count();

        System.out.println(task.result());
    }

    @Test
    public void loadBrand() {
        Task task = new Task();
        long a = System.currentTimeMillis();
        task.loadBrand("F:\\name\\brand_name.txt");
        long b = System.currentTimeMillis();
        System.out.println(b - a);
        System.out.println(task.getOrder("fNWn cuVkPQMaEDOgQ"));
    }

    @Test
    public void loadData() {
        long a = System.currentTimeMillis();
        Task task = new Task();
        task.loadBrand("F:\\name\\brand_name.txt");
        task.split("F:\\matchs.txt");
        long b = System.currentTimeMillis();
        System.out.println(b - a);
    }

    @Test
    public void trie() {
        DoubleArrayTrie trie = new DoubleArrayTrie();
//       trie.build("");
    }

    @Test
    public void read() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(FileUtil.file("F:\\name\\brand_name.txt"), "r");
        randomAccessFile.seek(100000000l);

        int hasRead = 0;
        byte[] buff = new byte[100];
        hasRead = randomAccessFile.read(buff);
        System.out.println(new String(buff, 0, hasRead));
    }
}
