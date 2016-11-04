// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.recognizer;

import top.vk.core.lang.Item;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PunctuationRecognizer implements Recognizer
{
    private String patternString;
    private Pattern punctuationPattern;
    private int type;
    
    public PunctuationRecognizer() {
        this.patternString = "[\uff0c\uff01\uff1f\uff1a\uff1b\u3002\u3001\u2018\u2019\u201c\u201d\\[\uff08{\u3010\u300c\uff09}\u3011\u300d\\]]";
        this.punctuationPattern = null;
        this.type = Item.PUNCTUATION;
        this.punctuationPattern = Pattern.compile(this.patternString, 2);
    }
    
    @Override
    public void recognize(final String sentence, final ArrayList<Item> items) {
        Item item = null;
        Matcher matcher = null;
        matcher = this.punctuationPattern.matcher(sentence);
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
        matcher = this.punctuationPattern.matcher(sentence);
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            items.add(item);
        }
        return items;
    }
}
