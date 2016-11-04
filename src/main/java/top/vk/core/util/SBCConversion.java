// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.util;

public class SBCConversion
{
    static final char SBC_CHAR_START = '\uff01';
    static final char SBC_CHAR_END = '\uff5e';
    static final int CONVERT_STEP = 65248;
    static final char SBC_SPACE = '\u3000';
    static final char DBC_SPACE = ' ';
    
    public static String SBCToDBC(final String str) {
        if (str == null) {
            return str;
        }
        final StringBuilder buf = new StringBuilder(str.length());
        final char[] sent = str.toCharArray();
        for (int i = 0; i < str.length(); ++i) {
            if (sent[i] >= '\uff01' && sent[i] <= '\uff5e') {
                buf.append((char)(sent[i] - '\ufee0'));
            }
            else if (sent[i] == '\u3000') {
                buf.append(' ');
            }
            else {
                buf.append(sent[i]);
            }
        }
        return buf.toString();
    }
}
