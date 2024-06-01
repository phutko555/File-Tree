package com.efimchick.ifmo.io.filetree;

import java.io.File;

public class Node {
    File key;
    Long value;
    Node left;
    Node right;

    Node(File key, Long value){
        this.key = key;
        this.value = value;
        right = null;
        left = null;
    }
}
