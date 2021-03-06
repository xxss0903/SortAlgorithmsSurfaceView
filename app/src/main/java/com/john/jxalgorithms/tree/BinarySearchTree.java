package com.john.jxalgorithms.tree;

/**
 * Created by John on 2016/9/7.
 */
public class BinarySearchTree <Key extends Comparable<Key>, Value>{
    private Node root;

    private class Node{
        private Key key;
        private Value val;
        private Node left, right;
        private int N;

        public Node(Key key, Value val, int N){
            this.key = key;
            this.val = val;
            this.N = N; // 节点数量
        }
    }

    public int size(){
        return size(root);
    }

    private int size(Node node){
        if(node == null)
            return 0;
        return node.N;  // 返回这个节点的数量，这个数量N是通过每次添加节点进行添加的
    }

    public Value get(Key key){
        return get(root, key);
    }

    private Value get(Node node, Key key) {
        if(node == null)
            return null;
        int cmp = key.compareTo(node.key);
        if(cmp < 0)
            return get(node.left, key);
        else if(cmp > 0)
            return get(node.right, key);
        else
            return node.val;
    }

    public void put(Key key, Value val){
root = put(root, key, val);
    }

    private Node put(Node node, Key key, Value val) {
        if(node == null)
            return new Node(key, val, 1);
        int cmp = key.compareTo(node.key);
        if(cmp < 0)
            node.left = put(node.left, key, val);
        else if(cmp > 0)
            node.right = put(node.right, key, val);
        else
            node.val = val;
        node.N = size(node.left) + size(node.right) + 1;
        return node;
    }


}
