// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.recognizer;

import top.vk.core.lang.Item;
import top.vk.core.util.ACPatternMatcher;
import top.vk.core.util.ReplaceResult;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonRecognizer implements Recognizer
{
    private ACPatternMatcher matcher;
    private String familyname;
    private String title;
    private String patternString;
    private Pattern personPattern;
    private int type;
    private boolean isCaseSensitive;
    
    public PersonRecognizer(final File file, final boolean isCaseSensitive) throws IOException {
        this.familyname = "([\u738b\u674e\u5f20\u5218\u9648\u6768\u9ec4\u8d75\u5434\u5468\u5f90\u5b59\u9a6c\u6731\u80e1\u90ed\u4f55\u9ad8\u6797\u7f57\u90d1\u6881\u8c22\u5b8b\u5510\u8bb8\u97e9\u51af\u9093\u66f9\u5f6d\u66fe\u8427\u7530\u8463\u6f58\u8881\u4e8e\u848b\u8521\u4f59\u675c\u53f6\u7a0b\u82cf\u9b4f\u5415\u4e01\u4efb\u6c88\u59da\u5362\u59dc\u5d14\u949f\u8c2d\u9646\u6c6a\u8303\u91d1\u77f3\u5ed6\u8d3e\u590f\u97e6\u5085\u65b9\u767d\u90b9\u5b5f\u718a\u79e6\u90b1\u6c5f\u5c39\u859b\u960e\u6bb5\u96f7\u4faf\u9f99\u53f2\u9676\u9ece\u8d3a\u987e\u6bdb\u90dd\u9f9a\u90b5\u4e07\u94b1\u4e25\u8983\u6b66\u6234\u83ab\u5b54\u5411\u6c64\u6d2a]|\u516c\u5b59|\u8bf8\u845b)";
        this.title = "(\u535a\u58eb|(\u526f)?\u6559\u6388|\u533b\u751f|\u6cd5\u5b98|\u5f8b\u5e08|\u4f1a\u8ba1|\u7ecf\u7406|\u8463\u4e8b|\u7406\u4e8b|(\u526f)?\u603b\u88c1|(\u526f)?\u603b\u76d1|\u8001\u5e08|\u8001\u677f|\u5e08\u5085|\u5927\u5e08|(\u526f)?\u4e3b\u4efb|(\u526f)?\u4e3b\u5e2d|(\u526f)?\u603b\u7edf|(\u526f)?\u603b\u7f16|\u516c\u5b50|\u516c\u4e3b|\u5927\u4fa0|\u5c11\u4fa0|\u5973\u58eb|\u5c0f\u59d0|\u592b\u4eba|\u5148\u751f|\u5c45\u58eb|(\u526f)?\u53f8\u4ee4|\u53c2\u8c0b|\u5c06\u519b|\u79d8\u4e66|(\u526f)?\u4e66\u8bb0|(\u526f)?[\u90e8\u5dde\u7701\u5e02\u5385\u5904\u53bf\u4e61\u9547\u6751\u79d1\u9662\u519b\u5e08\u65c5\u56e2\u8425\u8fde\u6392\u961f]\u957f|[\u5927\u4e0a\u4e2d\u5c11][\u5c06\u6821\u5c09]|[\u4e2d\u4e0b]\u58eb)";
        this.patternString = String.valueOf(this.familyname) + this.title;
        this.personPattern = null;
        this.type = Item.PERSON;
        this.isCaseSensitive = true;
        this.isCaseSensitive = isCaseSensitive;
        this.matcher = new ACPatternMatcher();
        this.init(file);
        this.personPattern = Pattern.compile(this.patternString, 2);
    }
    
    public void loadFile(final File file) throws IOException {
        BufferedReader fin = null;
        fin = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
        String buf;
        do {
            buf = fin.readLine();
            if (buf == null) {
                break;
            }
            if (buf.charAt(0) == '#') {
                continue;
            }
            String key;
            if (this.isCaseSensitive) {
                key = buf.trim();
            }
            else {
                key = buf.trim().toLowerCase();
            }
            this.matcher.insertToTree(key, key);
        } while (buf != null);
        fin.close();
    }
    
    public void init() {
        this.matcher.initFailedPointer();
    }
    
    public void init(final File file) throws IOException {
        this.loadFile(file);
        this.init();
    }
    
    public String cleanSentence(final String sentence) {
        final ArrayList<ReplaceResult> results = this.matcher.match(sentence);
        final StringBuilder ret = new StringBuilder();
        int nowPointer = 0;
        for (int i = 0; i < sentence.length(); ++i) {
            while (nowPointer < results.size() && results.get(nowPointer).getStart() < i) {
                ++nowPointer;
            }
            if (nowPointer < results.size() && results.get(nowPointer).getStart() == i) {
                final ReplaceResult item = results.get(nowPointer);
                ret.append(item.getValue());
                i += item.getKey().length() - 1;
            }
            else {
                ret.append(sentence.charAt(i));
            }
        }
        return ret.toString();
    }
    
    @Override
    public void recognize(final String sentence, final ArrayList<Item> items) {
        final ArrayList<ReplaceResult> results = this.matcher.match(sentence);
        int nowPointer = 0;
        for (int i = 0; i < sentence.length(); ++i) {
            while (nowPointer < results.size() && results.get(nowPointer).getStart() < i) {
                ++nowPointer;
            }
            Item toAdd = null;
            while (nowPointer < results.size() && results.get(nowPointer).getStart() == i) {
                final ReplaceResult item = results.get(nowPointer);
                final Item generalItem = new Item(i, item.getKey().length() + i, item.getKey(), this.type);
                if (toAdd == null) {
                    toAdd = generalItem;
                }
                else if (toAdd.getWord().length() < generalItem.getWord().length()) {
                    toAdd = generalItem;
                }
                ++nowPointer;
            }
            if (toAdd != null) {
                items.add(toAdd);
                i += toAdd.getWord().length() - 1;
            }
        }
        Item item2 = null;
        Matcher re_matcher = null;
        re_matcher = this.personPattern.matcher(sentence);
        while (re_matcher.find()) {
            item2 = new Item(re_matcher.start(), re_matcher.end(), re_matcher.group(), this.type);
            items.add(item2);
        }
    }
    
    @Override
    public ArrayList<Item> recognize(final String sentence) {
        final ArrayList<Item> items = new ArrayList<Item>();
        final ArrayList<ReplaceResult> results = this.matcher.match(sentence);
        int nowPointer = 0;
        for (int i = 0; i < sentence.length(); ++i) {
            while (nowPointer < results.size() && results.get(nowPointer).getStart() < i) {
                ++nowPointer;
            }
            Item toAdd = null;
            while (nowPointer < results.size() && results.get(nowPointer).getStart() == i) {
                final ReplaceResult item = results.get(nowPointer);
                final Item generalItem = new Item(i, item.getKey().length() + i, item.getKey(), this.type);
                if (toAdd == null) {
                    toAdd = generalItem;
                }
                else if (toAdd.getWord().length() < generalItem.getWord().length()) {
                    toAdd = generalItem;
                }
                ++nowPointer;
            }
            if (toAdd != null) {
                items.add(toAdd);
                i += toAdd.getWord().length() - 1;
            }
        }
        Item item2 = null;
        Matcher re_matcher = null;
        re_matcher = this.personPattern.matcher(sentence);
        while (re_matcher.find()) {
            item2 = new Item(re_matcher.start(), re_matcher.end(), re_matcher.group(), this.type);
            items.add(item2);
        }
        return items;
    }
}
