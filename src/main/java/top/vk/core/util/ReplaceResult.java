// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.util;

public class ReplaceResult
{
    private String key;
    private String value;
    private int start;
    
    public ReplaceResult(final String key, final String value, final int start) {
        this.key = key;
        this.value = value;
        this.start = start;
    }
    
    public ReplaceResult() {
        this.key = null;
        this.value = null;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public int getStart() {
        return this.start;
    }
}
