// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.recognizer;

import top.vk.core.lang.Item;
import top.vk.core.lang.MeasureItem;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeasureRecognizer implements Recognizer
{
    private String digit;
    private String digit1;
    private String patternString1;
    private Pattern datePattern1;
    private String patternString2;
    private Pattern datePattern2;
    private String patternString3;
    private Pattern datePattern3;
    private String patternString4;
    private Pattern datePattern4;
    private String patternString5;
    private Pattern datePattern5;
    private String patternString6;
    private Pattern datePattern6;
    private int type;
    
    public MeasureRecognizer() {
        this.digit = "(([0-9\u96f6\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u58f9\u8cb3\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u767e\u4edf\u5343\u842c\u4e07\u4ebf\u5146]+([0-9\u96f6\u70b9\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u58f9\u8cb3\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u767e\u4edf\u5343\u842c\u4e07\u4ebf\u5146]+)?)|(([1-9][0-9]{0,2}([,\uff0c][0-9]{3})+|[0-9]+)(\\.[0-9]+)?))";
        this.digit1 = "(([0-9\u96f6\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u58f9\u8cb3\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4edf\u5343\u842c\u4e07\u4ebf\u5146]+([0-9\u96f6\u70b9\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u58f9\u8cb3\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u767e\u4edf\u5343\u842c\u4e07\u4ebf\u5146]+)?)|(([1-9][0-9]{0,2}([,\uff0c][0-9]{3})+|[0-9]+)(\\.[0-9]+)?))";
        this.patternString1 = String.valueOf(this.digit) + "[\u51e0\u591a\u6765]?(mb|gb|tb|pb){1}";
        this.datePattern1 = null;
        this.patternString2 = String.valueOf(this.digit) + "[\u51e0\u591a\u6765]?(\u5428|t|ton|\u516c\u65a4|\u338f|\u5343\u514b|\u514b|g|\u6beb\u514b|\u338e|\u5e02\u65a4|\u4e24|\u5e02\u4e24|\u94b1|\u9551|\u76ce\u53f8|\u514b\u62c9|ct){1}";
        this.datePattern2 = null;
        this.patternString3 = String.valueOf(this.digit) + "[\u51e0\u591a\u6765]?(\u516c\u9877|\u5e73\u65b9\u5343\u7c73|\u4ea9|\u82f1\u4ea9|\u5e73\u65b9\u7c73|\u33a1|m2|\u5e73\u65b9\u5206\u7c73|dm2|\u5e73\u65b9\u5398\u7c73|cm2|\u5e73\u65b9\u6beb\u7c73|mm2|\u5e73\u65b9\u82f1\u5c3a|\u5e73\u65b9\u82f1\u5bf8){1}";
        this.datePattern3 = null;
        this.patternString4 = String.valueOf(this.digit) + "[\u51e0\u591a\u6765]?(\u6876|\u5347|L|\u6beb\u5347|mL|\u52a0\u4ed1|\u7f8e\u52a0\u4ed1|\u82f1\u52a0\u4ed1|gal|\u7acb\u65b9\u7c73|m3|\u7acb\u65b9\u5206\u7c73|dm3|\u7acb\u65b9\u5398\u7c73|cm3|\u7acb\u65b9\u6beb\u7c73|mm3|\u7acb\u65b9\u82f1\u5c3a|\u7acb\u65b9\u82f1\u5bf8){1}";
        this.datePattern4 = null;
        this.patternString5 = String.valueOf(this.digit1) + "[\u51e0\u591a\u6765]?(\u5ea6|\u6444\u6c0f\u5ea6|\u6444\u6c0f\u6e29\u5ea6|\u2103|\u534e\u6c0f\u5ea6|\u534e\u6c0f\u6e29\u5ea6|\u2109|\u5f00\u5c14\u6587){1}";
        this.datePattern5 = null;
        this.patternString6 = String.valueOf(this.digit) + "[\u51e0\u591a\u6765]?(\u5c81|\u7c73|m|\u5206\u7c73|dm|\u5398\u7c73|cm|\u339d|\u6beb\u7c73|mm|\u339c|\u5fae\u7c73|\u7eb3\u7c73|\u5149\u5e74|\u516c\u91cc|km|\u91cc|\u4e08|\u5c3a|\u5bf8|\u82f1\u91cc|\u7801|\u82f1\u5bf8|\u82f1\u5c3a){1}";
        this.datePattern6 = null;
        this.type = Item.MEASURE;
        this.datePattern1 = Pattern.compile(this.patternString1, 2);
        this.datePattern2 = Pattern.compile(this.patternString2, 2);
        this.datePattern3 = Pattern.compile(this.patternString3, 2);
        this.datePattern4 = Pattern.compile(this.patternString4, 2);
        this.datePattern5 = Pattern.compile(this.patternString5, 2);
        this.datePattern6 = Pattern.compile(this.patternString6, 2);
    }
    
    @Override
    public void recognize(final String sentence, final ArrayList<Item> items) {
        MeasureItem item = null;
        Matcher matcher = null;
        matcher = this.datePattern1.matcher(sentence);
        while (matcher.find()) {
            item = new MeasureItem(matcher.start(), matcher.end(), matcher.group(), this.type);
            item.setExplanation("DATASIZE");
            items.add(item);
        }
        matcher = this.datePattern2.matcher(sentence);
        while (matcher.find()) {
            item = new MeasureItem(matcher.start(), matcher.end(), matcher.group(), this.type);
            item.setExplanation("WEIGHT");
            items.add(item);
        }
        matcher = this.datePattern3.matcher(sentence);
        while (matcher.find()) {
            item = new MeasureItem(matcher.start(), matcher.end(), matcher.group(), this.type);
            item.setExplanation("SQUARE");
            items.add(item);
        }
        matcher = this.datePattern4.matcher(sentence);
        while (matcher.find()) {
            item = new MeasureItem(matcher.start(), matcher.end(), matcher.group(), this.type);
            item.setExplanation("VOLUMN");
            items.add(item);
        }
        matcher = this.datePattern5.matcher(sentence);
        while (matcher.find()) {
            item = new MeasureItem(matcher.start(), matcher.end(), matcher.group(), this.type);
            item.setExplanation("TEMPERATURE");
            items.add(item);
        }
        matcher = this.datePattern6.matcher(sentence);
        while (matcher.find()) {
            item = new MeasureItem(matcher.start(), matcher.end(), matcher.group(), this.type);
            item.setExplanation("LENGTH");
            items.add(item);
        }
    }
    
    @Override
    public ArrayList<Item> recognize(final String sentence) {
        final ArrayList<Item> items = new ArrayList<Item>();
        MeasureItem item = null;
        Matcher matcher = null;
        matcher = this.datePattern1.matcher(sentence);
        while (matcher.find()) {
            item = new MeasureItem(matcher.start(), matcher.end(), matcher.group(), this.type);
            item.setExplanation("DATASIZE");
            items.add(item);
        }
        matcher = this.datePattern2.matcher(sentence);
        while (matcher.find()) {
            item = new MeasureItem(matcher.start(), matcher.end(), matcher.group(), this.type);
            item.setExplanation("WEIGHT");
            items.add(item);
        }
        matcher = this.datePattern3.matcher(sentence);
        while (matcher.find()) {
            item = new MeasureItem(matcher.start(), matcher.end(), matcher.group(), this.type);
            item.setExplanation("SQUARE");
            items.add(item);
        }
        matcher = this.datePattern4.matcher(sentence);
        while (matcher.find()) {
            item = new MeasureItem(matcher.start(), matcher.end(), matcher.group(), this.type);
            item.setExplanation("VOLUMN");
            items.add(item);
        }
        matcher = this.datePattern5.matcher(sentence);
        while (matcher.find()) {
            item = new MeasureItem(matcher.start(), matcher.end(), matcher.group(), this.type);
            item.setExplanation("TEMPERATURE");
            items.add(item);
        }
        matcher = this.datePattern6.matcher(sentence);
        while (matcher.find()) {
            item = new MeasureItem(matcher.start(), matcher.end(), matcher.group(), this.type);
            item.setExplanation("LENGTH");
            items.add(item);
        }
        return items;
    }
}
