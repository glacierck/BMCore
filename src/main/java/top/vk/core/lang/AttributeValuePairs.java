// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lang;

public class AttributeValuePairs
{
    private String attribute;
    private String value;
    
    public AttributeValuePairs(final String attribute, final String value) {
        this.attribute = attribute;
        this.value = value;
    }
    
    public String getAttribute() {
        return this.attribute;
    }
    
    public void setAttribute(final String attribute) {
        this.attribute = attribute;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
}
