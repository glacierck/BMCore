// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.recognizer;

import top.vk.core.lang.Item;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DigitRecognizer implements Recognizer
{
    private String patternString;
    private Pattern digitPattern;
    private int type;
    
    public DigitRecognizer() {
        this.patternString = "((([1-9][0-9]{0,2}([,\uff0c][0-9]{3})+|[0-9]+)(\\.[0-9]+)?)|([\u4e24\u53cc\u96f6\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u58f9\u8cb3\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u767e\u4edf\u5343\u842c\u4e07\u4ebf\u5146\u51e0\u591a]{2,}))";
        this.digitPattern = null;
        this.type = Item.DIGIT;
        this.digitPattern = Pattern.compile(this.patternString, 2);
    }
    
    @Override
    public void recognize(final String sentence, final ArrayList<Item> items) {
        Item item = null;
        Matcher matcher = null;
        matcher = this.digitPattern.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
    }
    
    @Override
    public ArrayList<Item> recognize(final String sentence) {
        final ArrayList<Item> items = new ArrayList<Item>();
        Item item = null;
        Matcher matcher = null;
        matcher = this.digitPattern.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        return items;
    }
}
