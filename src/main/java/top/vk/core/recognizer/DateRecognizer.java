// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.recognizer;

import top.vk.core.lang.Item;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateRecognizer implements Recognizer
{
    private String patternString1;
    private Pattern datePattern1;
    private String patternString2;
    private Pattern datePattern2;
    private String patternString3;
    private Pattern datePattern3;
    private int type;
    
    public DateRecognizer() {
        this.patternString1 = "(\u516c\u5143\u524d|\u516c\u5143\u540e|\u516c\u5143|BC\\s|AC\\s)?(([0-9]{2,4}\u5e74([1-9]|1[0-2]|0[1-9])\u6708((3[01])|([021]?[0-9]))[\u65e5\u53f7])|(([0-9]{2,4}\u5e74)?([1-9]|1[0-2]|0[1-9])\u6708(((3[01])|([021]?[0-9]))[\u65e5\u53f7])?)|([0-9]{2,4}\u5e74)|(((3[01])|([021]?[0-9]))[\u65e5])|([0-9]{2,4}[\\-|\\.|/]([1-9]|1[0-2]|0[1-9])[\\-|\\.|/]((3[01])|([021]?[0-9])))|(([1-9]|1[0-2]|0[1-9])/((3[01])|([021]?[0-9])))|([12][0-9]{3}(0[1-9]|1[012])([012][1-9]|3[01])))";
        this.datePattern1 = null;
        this.patternString2 = "((\u516c\u5143\u524d|\u516c\u5143\u540e|\u516c\u5143|BC\\s|AC\\s)?(([\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u3007\u96f6]{2,4}\u5e74([\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341]|\u5341[\u4e00\u4e8c])\u6708([\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d]|\u5341([\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d])?|\u4e8c\u5341([\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d])?|\u4e09\u5341(\u4e00)?)[\u65e5\u53f7])|(([\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u3007\u96f6]{2,4}\u5e74)?([\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341]|\u5341[\u4e00\u4e8c])\u6708(([\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d]|\u5341([\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d])?|\u4e8c\u5341([\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d])?|\u4e09\u5341(\u4e00)?)[\u65e5\u53f7])?)|([\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u3007\u96f6]{2,4}\u5e74)|(([\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d]|\u5341([\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d])?|\u4e8c\u5341([\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d])?|\u4e09\u5341(\u4e00)?)[\u65e5])))";
        this.datePattern2 = null;
        this.patternString3 = "((\u6bcf|\u4e0a|\u4e0b|\u8fd9)?\u5468\u672b|\u6bcf[\u65e5\u5929\u5e74\u5468\u6708]|(\u661f\u671f|\u793c\u62dc)\u5929|(\u6bcf|\u4e0a|\u4e0b|\u8fd9)(\u661f\u671f|\u793c\u62dc|\u5468)[\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u65e5]?|(\u6bcf|\u4e0a|\u4e0b|\u8fd9)?(\u661f\u671f|\u793c\u62dc|\u5468)[\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u65e5]|[\u4eca\u660e\u540e][\u65e5\u5929\u5e74]|[\u9f20\u725b\u864e\u5154\u9f99\u86c7\u9a6c\u7f8a\u7334\u9e21\u72d7\u732a]\u5e74)";
        this.datePattern3 = null;
        this.type = Item.DATE;
        this.datePattern1 = Pattern.compile(this.patternString1, 2);
        this.datePattern2 = Pattern.compile(this.patternString2, 2);
        this.datePattern3 = Pattern.compile(this.patternString3, 2);
    }
    
    @Override
    public void recognize(final String sentence, final ArrayList<Item> items) {
        Item item = null;
        Matcher matcher = null;
        matcher = this.datePattern1.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        matcher = this.datePattern2.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        matcher = this.datePattern3.matcher(sentence);
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
        matcher = this.datePattern1.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        matcher = this.datePattern2.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        matcher = this.datePattern3.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        return items;
    }
}
