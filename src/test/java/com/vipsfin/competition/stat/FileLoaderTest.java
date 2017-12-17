package com.vipsfin.competition.stat;

import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.lang.BoundedPriorityQueue;
import org.junit.Test;

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
    public void result() {
        Task task = new Task();
        task.loadBrand("brand_name_ok.txt");
        task.loadData("bisai_1.txt");
        task.count();
        System.out.println(task.result());
    }
}
