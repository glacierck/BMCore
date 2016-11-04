// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.recognizer;

import top.vk.core.lang.Item;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailRecognizer implements Recognizer
{
    private String patternString;
    private Pattern emailPattern;
    private int type;
    
    public EmailRecognizer() {
        this.patternString = "([a-zA-Z0-9_\\.\\-])+@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+";
        this.emailPattern = null;
        this.type = Item.EMAIL;
        this.emailPattern = Pattern.compile(this.patternString, 2);
    }
    
    @Override
    public void recognize(final String sentence, final ArrayList<Item> items) {
        Item item = null;
        Matcher matcher = null;
        matcher = this.emailPattern.matcher(sentence);
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
        matcher = this.emailPattern.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        return items;
    }
}
