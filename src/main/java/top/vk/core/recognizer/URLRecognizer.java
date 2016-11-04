// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.recognizer;

import top.vk.core.lang.Item;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLRecognizer implements Recognizer
{
    private String patternString1;
    private Pattern URLPattern1;
    private String patternString2;
    private Pattern URLPattern2;
    private String patternString3;
    private Pattern URLPattern3;
    private int type;
    
    public URLRecognizer() {
        this.patternString1 = "(((http(s)?|ftp|file)[\uff1a:]//)|(www.))([\\w\\-]+\\.)+[\\w\\-]+(/[\\w\\-]+)+([\\w\\-\\.\\?\\+=\uff1f%&]+)?";
        this.URLPattern1 = null;
        this.patternString2 = "(((http(s)?|ftp|file)[\uff1a:]//)|(www.))([\\w\\-]+\\.)+([\\w\\-]+)(/)?";
        this.URLPattern2 = null;
        this.patternString3 = "((([a-zA-Z0-9]+)\\.(com|edu|org|int|mil|net|biz|info|pro|name|museum|coop|aero|xxx|idv)(\\.[a-zA-Z]{2})?((/[\\w\\-]+)+([\\w\\-\\.\\?\\+=\uff1f%&]+)?(/)?)?)|(\\d+\\.\\d+\\.\\d+\\.\\d+))";
        this.URLPattern3 = null;
        this.type = Item.URL;
        this.URLPattern1 = Pattern.compile(this.patternString1, 2);
        this.URLPattern2 = Pattern.compile(this.patternString2, 2);
        this.URLPattern3 = Pattern.compile(this.patternString3, 2);
    }
    
    @Override
    public void recognize(final String sentence, final ArrayList<Item> items) {
        Item item = null;
        Matcher matcher = null;
        matcher = this.URLPattern1.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        matcher = this.URLPattern2.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        matcher = this.URLPattern3.matcher(sentence);
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
        matcher = this.URLPattern1.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        matcher = this.URLPattern2.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        matcher = this.URLPattern3.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        return items;
    }
}
