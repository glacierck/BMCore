// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class ACPatternMatcher
{
    private TrieTreeNode root;
    
    public void insertToTree(final String key, final String data) {
        TrieTreeNode now = this.root;
        for (int i = 0; i < key.length(); ++i) {
            final char nowChar = key.charAt(i);
            if (!now.children.containsKey(nowChar)) {
                final TrieTreeNode tmpNode = new TrieTreeNode(nowChar);
                tmpNode.parent = now;
                now.children.put(nowChar, tmpNode);
            }
            now = now.children.get(nowChar);
        }
        now.data = data;
        now.key = key;
    }
    
    public void initFailedPointer() {
        final Queue<TrieTreeNode> queue = new ArrayDeque<TrieTreeNode>();
        queue.add(this.root);
        while (!queue.isEmpty()) {
            final TrieTreeNode now = queue.remove();
            for (final Character childChar : now.children.keySet()) {
                final TrieTreeNode child = now.children.get(childChar);
                if (now == this.root) {
                    child.fail = this.root;
                }
                else {
                    TrieTreeNode pre;
                    for (pre = now.fail; pre != null; pre = pre.fail) {
                        if (pre.children.containsKey(childChar)) {
                            child.fail = pre.children.get(childChar);
                            break;
                        }
                    }
                    if (pre == null) {
                        child.fail = this.root;
                    }
                }
                queue.add(child);
            }
        }
        this.root.fail = this.root;
    }
    
    public ArrayList<ReplaceResult> match(final String s) {
        TrieTreeNode now = this.root;
        final ArrayList<ReplaceResult> ret = new ArrayList<ReplaceResult>();
        for (int i = 0; i < s.length(); ++i) {
            while (now != this.root && !now.children.containsKey(s.charAt(i))) {
                now = now.fail;
            }
            if (now.children.containsKey(s.charAt(i))) {
                now = now.children.get(s.charAt(i));
                if (now.key != null) {
                    ret.add(new ReplaceResult(now.key, now.data, i - now.key.length() + 1));
                }
            }
        }
        return ret;
    }
    
    public ACPatternMatcher() {
        this.root = new TrieTreeNode('#');
    }
}
