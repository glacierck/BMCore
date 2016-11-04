// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.util;

import java.util.HashMap;

class TrieTreeNode
{
    HashMap<Character, TrieTreeNode> children;
    char c;
    TrieTreeNode parent;
    TrieTreeNode fail;
    String key;
    String data;
    
    TrieTreeNode(final char c) {
        this.c = c;
        this.children = new HashMap<Character, TrieTreeNode>();
        this.fail = null;
    }
}
