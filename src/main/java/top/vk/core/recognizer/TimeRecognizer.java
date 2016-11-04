// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.recognizer;

import top.vk.core.lang.Item;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeRecognizer implements Recognizer
{
    private String hour;
    private String second;
    private String patternString1;
    private Pattern timePattern1;
    private String patternString2;
    private Pattern timePattern2;
    private int type;
    
    public TimeRecognizer() {
        this.hour = "(((0)?[0-9]|1[0-9]|2[0-3])|(\u5341?[\u4e24\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d]|\u96f6|\u4e8c\u5341[\u4e00\u4e8c\u4e09]))";
        this.second = "([0-5]?[0-9]|[\u4e24\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d]|[\u4e00\u4e8c\u4e09\u56db\u4e94]?\u5341[\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d]?)";
        this.patternString1 = "(([\u4eca\u660e]?[\u4e0a\u4e0b\u4e2d\u6b63]\u5348)|[\u5348\u665a\u591c]\u95f4|[\u65e9\u665a]\u4e0a|[\u4eca\u660e\u508d]?\u665a|(\u6df1)?\u591c|(\u51cc)?\u6668|\u5348(\u65f6)?|\u65e9|[ap]m(\\.)\\s)?" + this.hour + "(([\uff1a:]" + this.second + ")|((\u70b9|\u65f6)(\u6574|\u949f|\u534a)?" + "((" + this.second + "\u5206)|([123\u4e24\u4e00\u4e8c\u4e09]\u523b))?" + "))" + "(([\uff1a:]" + this.second + ")|(" + this.second + "\u79d2))?" + "(\\s[ap]m(\\.))?";
        this.timePattern1 = null;
        this.patternString2 = "[\u4eca\u660e]?[\u4e0a\u4e0b\u4e2d\u6b63]\u5348(\u524d|\u540e|\u524d\u540e)?";
        this.timePattern2 = null;
        this.type = Item.TIME;
        this.timePattern1 = Pattern.compile(this.patternString1, 2);
        this.timePattern2 = Pattern.compile(this.patternString2, 2);
    }
    
    @Override
    public void recognize(final String sentence, final ArrayList<Item> items) {
        Item item = null;
        Matcher matcher = null;
        matcher = this.timePattern1.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        matcher = this.timePattern2.matcher(sentence);
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
        matcher = this.timePattern1.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        matcher = this.timePattern2.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        return items;
    }
}
