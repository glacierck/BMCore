// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.util;

public class CheckPunctuation
{
    static String punctuation;
    
    static {
        CheckPunctuation.punctuation = ",\uff0c!\uff01?\uff1f:\uff1a;\uff1b\u3002\u3001\u2018\u2019\u201c\u201d[]{}()\uff08\uff09[]{\u3010\u3011\u300c\u300d<>`~@#$%^&*+=-_\uff0d|\"\\/";
    }
    
    public static boolean isPunctation(final String ch) {
        return CheckPunctuation.punctuation.contains(ch);
    }
}
