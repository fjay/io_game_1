package com.vipsfin.competition.stat;

/**
 * @author Jay Wu
 */
public class SimpleTrie<V> {

    /**
     * 所有Ascii
     */
    private final static int SIZE = 128 - 40;
    //除根节点外其他所有子节点的数目
    private int numNode = 0;
    //树的深度即最长字符串的长度
    private int depth = 0;
    //字典树的根
    private TrieNode root = new TrieNode();

    public TrieNode insertAndGetLastNode(String str) {
        str = str.toLowerCase();//不区分大小写，转为小写
        char[] letters = str.toCharArray();//转成字符数组
        TrieNode node = this.root;//先从父节点开始
        for (char c : letters) {
            int pos = c - ' ';//得到应存son[]中的索引
            if (node.son[pos] == null) {//此字符不存在
                node.son[pos] = new TrieNode();
                node.son[pos].value = c;
                node.son[pos].numPass = 1;
                this.numNode++;
            } else {//此字符已经存入
                node.son[pos].numPass++;
            }
            node = node.son[pos];//继续为下一下字符做准备
        }

        node.isEnd = true;//标记：有字符串到了此节点已结束
        node.numEnd++;//这个字符串重复次数
        if (letters.length > this.depth) {//记录树的深度
            this.depth = letters.length;
        }

        return node;
    }

    public static class TrieNode {

        private TrieNode[] son = new TrieNode[SIZE];

        // 有多少字符串经过或到达这个节点,即节点字符出现的次数
        private int numPass = 0;
        // 有多少字符串通过这个节点并到此结束的数量
        private int numEnd = 0;
        // 是否有结束节点
        private boolean isEnd = false;
        // 节点的值
        private char value;

        public int getNumPass() {
            return numPass;
        }

        public TrieNode setNumPass(int numPass) {
            this.numPass = numPass;
            return this;
        }

        public int getNumEnd() {
            return numEnd;
        }

        public TrieNode setNumEnd(int numEnd) {
            this.numEnd = numEnd;
            return this;
        }

        public boolean isEnd() {
            return isEnd;
        }

        public TrieNode setEnd(boolean end) {
            isEnd = end;
            return this;
        }

        public char getValue() {
            return value;
        }

        public TrieNode setValue(char value) {
            this.value = value;
            return this;
        }
    }
}