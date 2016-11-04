// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.recognizer;

import top.vk.core.lang.Item;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrencyRecognizer implements Recognizer
{
    private String digit;
    private String patternString;
    private Pattern currencyPattern;
    private int type;
    
    public CurrencyRecognizer() {
        this.digit = "((([0-9\u96f6\u70b9\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u58f9\u8cb3\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u767e\u4edf\u5343\u842c\u4e07\u4ebf\u5146]+[\u5706\u5143\u89d2\u6bdb\u5206]{1})+)|(([1-9][0-9]{0,2}([,\uff0c][0-9]{3})+|[0-9]+)(\\.[0-9]{1,4})?))";
        this.patternString = "(((\u4eba\u6c11\u5e01|RMB|CNY|¥|\uffe5|\u7f8e\u5143|\u7f8e\u91d1|USD|$|\u82f1\u78c5|GBP|£|\u20a4|\u6b27\u5143|ERU|\u20ac|\u65e5\u5143|JPY|\u6e2f\u5143|HKD|HK$)" + this.digit + "(\u5143)?)|" + "(" + this.digit + "(\u4eba\u6c11\u5e01|RMB|CNY|¥|\uffe5|\u7f8e\u5143|\u7f8e\u91d1|USD|$|\u82f1\u78c5|GBP|£|\u20a4|\u6b27\u5143|ERU|\u20ac|\u65e5\u5143|JPY|\u6e2f\u5143|HKD|HK$)" + ")|" + "(" + "([0-9\u96f6\u70b9\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u58f9\u8cb3\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u767e\u4edf\u5343\u842c\u4e07\u4ebf\u5146]+[\u5706\u5143\u89d2\u6bdb\u5206]{1})+" + ")|" + "(([1-9][0-9]{0,2}([,\uff0c][0-9]{3})+|[0-9]+)(\\.[0-9]{1,4})?\u5143)" + ")";
        this.currencyPattern = null;
        this.type = Item.CURRENCY;
        this.currencyPattern = Pattern.compile(this.patternString, 2);
    }
    
    @Override
    public void recognize(final String sentence, final ArrayList<Item> items) {
        Item item = null;
        Matcher matcher = null;
        matcher = this.currencyPattern.matcher(sentence);
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
        matcher = this.currencyPattern.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        return items;
    }
}
