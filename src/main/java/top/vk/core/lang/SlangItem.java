// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lang;

public class SlangItem extends Item
{
    private String explanation;
    
    public SlangItem(final int start, final int end, final String word, final int type) {
        super(start, end, word, type);
    }
    
    public String getExplanation() {
        return this.explanation;
    }
    
    public void setExplanation(final String explanation) {
        this.explanation = explanation;
    }
}
