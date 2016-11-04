// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.recognizer;

import top.vk.core.lang.Item;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PeriodRecognizer implements Recognizer
{
    private String digit;
    private String patternString1;
    private Pattern periodPattern1;
    private String patternString2;
    private Pattern periodPattern2;
    private String patternString3;
    private Pattern periodPattern3;
    private int type;
    
    public PeriodRecognizer() {
        this.digit = "((([0-9*]+)|([\u7b2c\u6570]?[\u4e24\u96f6\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u58f9\u8cb3\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u767e\u4edf\u5343\u842c\u4e07\u4ebf\u5146]{1,}))[\u4e2a\u591a\u51e0]?)";
        this.patternString1 = "(([\u672c\u4e0a\u4e0b]|\u5341[\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d]|[\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d])\u4e16\u7eaa([\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d]{1,2}\u5341\u5e74\u4ee3)?([\u65e9\u4e2d\u540e]\u671f)?)";
        this.periodPattern1 = null;
        this.patternString2 = "((" + this.digit + "\u5c0f\u65f6" + this.digit + "\u5206" + ")" + "|" + "(" + this.digit + "\u5206" + this.digit + "\u79d2" + ")" + ")";
        this.periodPattern2 = null;
        this.patternString3 = "((" + this.digit + "(\u4e16\u7eaa|\u7532\u5b50|(\u5149)?\u5e74(\u4ee3)?|\u8f7d|\u5b66\u671f|\u6708|\u5b63\u5ea6|\u5468|\u661f\u671f|\u65e5|(\u5468)?\u5929|\u5c0f\u65f6|\u5206\u949f|\u79d2|[\u6beb\u5fae\u7eb3]\u79d2)[\u96f6\u591a]?" + ")+)";
        this.periodPattern3 = null;
        this.type = Item.PERIOD;
        this.periodPattern1 = Pattern.compile(this.patternString1, 2);
        this.periodPattern2 = Pattern.compile(this.patternString2, 2);
        this.periodPattern3 = Pattern.compile(this.patternString3, 2);
    }
    
    @Override
    public void recognize(final String sentence, final ArrayList<Item> items) {
        Item item = null;
        Matcher matcher = null;
        matcher = this.periodPattern1.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        matcher = this.periodPattern2.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        matcher = this.periodPattern3.matcher(sentence);
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
        matcher = this.periodPattern1.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        matcher = this.periodPattern2.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        matcher = this.periodPattern3.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        return items;
    }
}
