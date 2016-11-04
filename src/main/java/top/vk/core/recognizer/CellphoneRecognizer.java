// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.recognizer;

import top.vk.core.lang.Item;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CellphoneRecognizer implements Recognizer
{
    private String patternString;
    private Pattern cellphonePattern;
    private int type;
    private String previous;
    private String follow;
    private int start;
    private int end;
    private int length;
    
    public CellphoneRecognizer() {
        this.patternString = "(\\+[0-9]{1,4}-)?((13[0-9])|(15[0|3|6|7|8|9])|(18[8|9]))(-)?[0-9]{4}(-)?[0-9]{4}";
        this.cellphonePattern = null;
        this.type = Item.CELLPHONE;
        this.previous = null;
        this.follow = null;
        this.start = -1;
        this.end = -1;
        this.length = -1;
        this.cellphonePattern = Pattern.compile(this.patternString, 2);
    }
    
    @Override
    public void recognize(final String sentence, final ArrayList<Item> items) {
        Item item = null;
        Matcher matcher = null;
        matcher = this.cellphonePattern.matcher(sentence);
        this.length = sentence.length();
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            this.start = matcher.start();
            this.end = matcher.end();
            if (this.start > 0) {
                this.previous = sentence.substring(this.start - 1, this.start);
            }
            else {
                this.previous = null;
            }
            if (this.end < this.length) {
                this.follow = sentence.substring(this.end, this.end + 1);
            }
            else {
                this.follow = null;
            }
            if ((this.previous == null || (!this.previous.equals("0") && !this.previous.equals("1") && !this.previous.equals("2") && !this.previous.equals("3") && !this.previous.equals("4") && !this.previous.equals("5") && !this.previous.equals("6") && !this.previous.equals("7") && !this.previous.equals("8") && !this.previous.equals("9"))) && (this.follow == null || (!this.follow.equals("0") && !this.follow.equals("1") && !this.follow.equals("2") && !this.follow.equals("3") && !this.follow.equals("4") && !this.follow.equals("5") && !this.follow.equals("6") && !this.follow.equals("7") && !this.follow.equals("8") && !this.follow.equals("9")))) {
                items.add(item);
            }
        }
    }
    
    @Override
    public ArrayList<Item> recognize(final String sentence) {
        final ArrayList<Item> items = new ArrayList<Item>();
        Item item = null;
        Matcher matcher = null;
        matcher = this.cellphonePattern.matcher(sentence);
        this.length = sentence.length();
        while (matcher.find()) {
            item = new Item(matcher.start(), matcher.end(), matcher.group(), this.type);
            this.start = matcher.start();
            this.end = matcher.end();
            if (this.start > 0) {
                this.previous = sentence.substring(this.start - 1, this.start);
            }
            else {
                this.previous = null;
            }
            if (this.end < this.length) {
                this.follow = sentence.substring(this.end, this.end + 1);
            }
            else {
                this.follow = null;
            }
            if ((this.previous == null || (!this.previous.equals("0") && !this.previous.equals("1") && !this.previous.equals("2") && !this.previous.equals("3") && !this.previous.equals("4") && !this.previous.equals("5") && !this.previous.equals("6") && !this.previous.equals("7") && !this.previous.equals("8") && !this.previous.equals("9"))) && (this.follow == null || (!this.follow.equals("0") && !this.follow.equals("1") && !this.follow.equals("2") && !this.follow.equals("3") && !this.follow.equals("4") && !this.follow.equals("5") && !this.follow.equals("6") && !this.follow.equals("7") && !this.follow.equals("8") && !this.follow.equals("9")))) {
                items.add(item);
            }
        }
        return items;
    }
}
