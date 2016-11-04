// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.recognizer;

import top.vk.core.lang.Item;
import top.vk.core.lang.SlangItem;
import top.vk.core.util.ACPatternMatcher;
import top.vk.core.util.ReplaceResult;

import java.io.*;
import java.util.ArrayList;

public class SlangRecognizer implements Recognizer
{
    private ACPatternMatcher matcher;
    private int type;
    private boolean isCaseSensitive;
    
    public SlangRecognizer(final File file, final boolean isCaseSensitive) throws IOException {
        this.type = Item.SLANG;
        this.isCaseSensitive = true;
        this.isCaseSensitive = isCaseSensitive;
        this.matcher = new ACPatternMatcher();
        this.init(file);
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
            final String[] bufArray = buf.split("[ ]");
            final String key = bufArray[0];
            final String value = bufArray[1];
            if (this.isCaseSensitive) {
                this.matcher.insertToTree(key.trim(), value.trim());
            }
            else {
                this.matcher.insertToTree(key.trim().toLowerCase(), value.trim().toLowerCase());
            }
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
                final SlangItem slangItem = new SlangItem(i, item.getKey().length() + i, item.getKey(), this.type);
                slangItem.setExplanation(item.getValue());
                if (toAdd == null) {
                    toAdd = slangItem;
                }
                else if (toAdd.getWord().length() < slangItem.getWord().length()) {
                    toAdd = slangItem;
                }
                ++nowPointer;
            }
            if (toAdd != null) {
                items.add(toAdd);
                i += toAdd.getWord().length() - 1;
            }
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
                final SlangItem slangItem = new SlangItem(i, item.getKey().length() + i, item.getKey(), this.type);
                slangItem.setExplanation(item.getValue());
                if (toAdd == null) {
                    toAdd = slangItem;
                }
                else if (toAdd.getWord().length() < slangItem.getWord().length()) {
                    toAdd = slangItem;
                }
                ++nowPointer;
            }
            if (toAdd != null) {
                items.add(toAdd);
                i += toAdd.getWord().length() - 1;
            }
        }
        return items;
    }
}
