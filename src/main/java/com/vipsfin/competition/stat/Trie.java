package com.vipsfin.competition.stat;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jay Wu
 */
public class Trie<V> {

    // The root character is an arbitrarily picked
    // character chosen for the root node.
    private final char rootCharacter = '\0';
    private Node root = new Node(rootCharacter);

    public class Node {
        private char ch;
        private long count = 0;
        private boolean isWordEnding = false;
        private V value;

        private Map<Character, Node> children = new HashMap<>();

        Node(char ch) {
            this.ch = ch;
        }

        void addChild(Node node, char c) {
            children.put(c, node);
        }

        public synchronized long addCount(long numInserts) {
            return count += numInserts;
        }

        public V getValue() {
            return value;
        }

        public Node setValue(V value) {
            this.value = value;
            return this;
        }

        public long getCount() {
            return count;
        }

        public boolean isWordEnding() {
            return isWordEnding;
        }

        public char getCh() {
            return ch;
        }
    }

    // Returns true if the string being inserted
    // startWith a prefix already in the trie
    public boolean insert(String key, long numInserts) {

        if (key == null) throw new IllegalArgumentException("Null not permitted in trie");
        if (numInserts <= 0) throw new IllegalArgumentException("numInserts has to be greater than zero");

        Node node = root;
        boolean createdNewNode = false;
        boolean isPrefix = false;

        // Process each character one at a time
        for (int i = 0; i < key.length(); ++i) {

            char ch = key.charAt(i);
            Node nextNode = node.children.get(ch);

            // The next character in this string does not yet exist in trie
            if (nextNode == null) {

                nextNode = new Node(ch);
                node.addChild(nextNode, ch);
                createdNewNode = true;

                // Next character exists in trie.
            } else {
                if (nextNode.isWordEnding)
                    isPrefix = true;
            }

            node = nextNode;
            node.count += numInserts;

        }

        // The root itself is not a word ending. It is simply a placeholder.
        if (node != root) {
            node.isWordEnding = true;
        }

        return isPrefix || !createdNewNode;

    }

    // Returns true if the string being inserted
    // startWith a prefix already in the trie
    public boolean insert(String key) {
        return insert(key, 1);
    }

    public Node findNode(String key) {
        if (key == null)
            throw new IllegalArgumentException("Null not permitted");

        Node now = root;
        Node prev = null;
        for (int i = 0; i < key.length(); i++) {
            char ch = key.charAt(i);
            Node nextNode = now.children.get(ch);
            if (nextNode == null) {
                break;
            } else {
                prev = now;
                now = nextNode;
            }
        }

        return now.isWordEnding ? now : prev;
    }

    public Node insertAndGetLastNode(String key, long numInserts) {
        if (key == null) throw new IllegalArgumentException("Null not permitted in trie");
        if (numInserts <= 0) throw new IllegalArgumentException("numInserts has to be greater than zero");

        Node node = root;
        // Process each character one at a time
        for (int i = 0; i < key.length(); ++i) {

            char ch = key.charAt(i);
            Node nextNode = node.children.get(ch);

            // The next character in this string does not yet exist in trie
            if (nextNode == null) {

                nextNode = new Node(ch);
                node.addChild(nextNode, ch);
                // Next character exists in trie.
            }

            node = nextNode;
            node.count += numInserts;

        }

        // The root itself is not a word ending. It is simply a placeholder.
        if (node != root) {
            node.isWordEnding = true;
        }

        return node;
    }

    // This delete function allows you to delete keys from the trie
    // (even those which were not previously inserted into the trie).
    // This means that it may be the case that you delete a prefix which
    // cuts off the access to numerous other strings starting with
    // that prefix.
    public boolean delete(String key, int numDeletions) {

        // We cannot delete something that doesn't exist
        if (!startWith(key)) return false;

        if (numDeletions <= 0)
            throw new IllegalArgumentException("numDeletions has to be positive");

        Node node = root;
        for (int i = 0; i < key.length(); i++) {

            char ch = key.charAt(i);
            Node curNode = node.children.get(ch);
            curNode.count -= numDeletions;

            // Cut this edge if the current node has a count <= 0
            // This means that all the prefixes below this point are inaccessible
            if (curNode.count <= 0) {
                node.children.remove(ch);
                curNode.children = null;
                curNode = null;
                return true;
            }

            node = curNode;

        }
        return true;
    }

    public boolean delete(String key) {
        return delete(key, 1);
    }

    public Node getLastNode(String key) {
        if (key == null)
            throw new IllegalArgumentException("Null not permitted");

        Node node = root;

        // Dig down into trie until we reach the bottom or stop
        // early because the string we're looking for doesn't exist
        for (int i = 0; i < key.length(); i++) {
            char ch = key.charAt(i);
            if (node == null) return null;
            node = node.children.get(ch);
        }

        return node;
    }

    public boolean contains(String key) {
        Node node = getLastNode(key);

        if (node != null) {
            return node.isWordEnding;
        }

        return false;
    }


    // Returns true if this string is contained inside the trie
    public boolean startWith(String key) {
        return count(key) != 0;
    }

    // Returns the count of a particular prefix
    public long count(String key) {
        Node node = getLastNode(key);

        if (node != null) {
            return node.count;
        }

        return 0;
    }

    // Recursively clear the trie freeing memory to help GC
    private void clear(Node node) {

        if (node == null) return;

        for (Character ch : node.children.keySet()) {
            Node nextNode = node.children.get(ch);
            clear(nextNode);
            nextNode = null;
        }

        node.children.clear();
        node.children = null;

    }

    // Clear the trie
    public void clear() {

        root.children = null;
        root = new Node(rootCharacter);

    }
}