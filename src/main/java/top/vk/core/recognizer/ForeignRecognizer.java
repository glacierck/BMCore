// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.recognizer;

import top.vk.core.lang.Item;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForeignRecognizer implements Recognizer
{
    private String patternString;
    private Pattern foreignLanguagePattern;
    private int type;
    
    public ForeignRecognizer() {
        this.patternString = "(([0-9]+)?[A-Za-z]+([a-zA-Z0-9]+)?)";
        this.foreignLanguagePattern = null;
        this.type = Item.FOREIGN;
        this.foreignLanguagePattern = Pattern.compile(this.patternString, 2);
    }
    
    @Override
    public void recognize(final String sentence, final ArrayList<Item> items) {
        Item item = null;
        Matcher matcher = null;
        matcher = this.foreignLanguagePattern.matcher(sentence);
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
        matcher = this.foreignLanguagePattern.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        return items;
    }
}
