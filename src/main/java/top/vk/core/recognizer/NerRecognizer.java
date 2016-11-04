// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.recognizer;

import top.vk.core.dnn.WindowConvolutionNetworkDecoder;
import top.vk.core.lang.Item;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class NerRecognizer implements Recognizer
{
    private WindowConvolutionNetworkDecoder nerRecogizer;
    private String nerResult;
    private String locationTag;
    private String personTag;
    private String organizationTag;
    private String otherTag;
    private String delimiter;
    private String connector;
    private String[] tokens;
    private String[] pairs;
    private String word;
    private String tag;
    private int start;
    private boolean isConsistancy;
    
    public NerRecognizer(final String nerRecogizerFile) {
        this.locationTag = "LOC";
        this.personTag = "PER";
        this.organizationTag = "ORG";
        this.otherTag = "O";
        this.delimiter = "/";
        this.connector = "_";
        this.start = -1;
        this.isConsistancy = true;
        this.init(nerRecogizerFile);
    }
    
    public void init(final String nerRecogizerFile) {
        final Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(nerRecogizerFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        (this.nerRecogizer = new WindowConvolutionNetworkDecoder(prop.getProperty("inputNetworkSettingFile"))).setCharacterLevel(Boolean.parseBoolean(prop.getProperty("isCharacterLevel")));
        this.nerRecogizer.setResultWithTag(Boolean.parseBoolean(prop.getProperty("isResultWithTag")));
        this.nerRecogizer.setSegmentation(Boolean.parseBoolean(prop.getProperty("isSegmentation")));
        this.nerRecogizer.setStandard(Boolean.parseBoolean(prop.getProperty("isStandard")));
        this.nerRecogizer.readPara();
    }
    
    @Override
    public void recognize(final String sentence, final ArrayList<Item> items) {
        this.nerResult = this.nerRecogizer.decodeSentence(sentence);
        this.tokens = this.nerResult.split("\\s+");
        this.word = "";
        this.start = -1;
        this.isConsistancy = true;
        for (int i = 0; i < this.tokens.length; ++i) {
            this.pairs = this.tokens[i].split(this.delimiter);
            this.tag = this.pairs[1];
            if (!this.tag.equals(this.otherTag)) {
                if (this.tag.startsWith("S")) {
                    if (this.start == -1) {
                        this.word = this.pairs[0];
                        if (this.tag.substring(this.tag.indexOf(this.connector) + 1).equals(this.locationTag)) {
                            items.add(new Item(i, i + 1, this.word, Item.LOCATION));
                        }
                        if (this.tag.substring(this.tag.indexOf(this.connector) + 1).equals(this.personTag)) {
                            items.add(new Item(i, i + 1, this.word, Item.PERSON));
                        }
                        if (this.tag.substring(this.tag.indexOf(this.connector) + 1).equals(this.organizationTag)) {
                            items.add(new Item(i, i + 1, this.word, Item.ORGANIZATION));
                        }
                    }
                    else {
                        this.isConsistancy = false;
                    }
                }
                if (this.tag.startsWith("B")) {
                    this.word = this.pairs[0];
                    this.start = i;
                    this.isConsistancy = true;
                }
                if (this.tag.startsWith("I") && this.start != -1) {
                    this.word = String.valueOf(this.word) + this.pairs[0];
                }
                if (this.tag.startsWith("E") && this.isConsistancy) {
                    this.word = String.valueOf(this.word) + this.pairs[0];
                    if (this.tag.substring(this.tag.indexOf(this.connector) + 1).equals(this.locationTag)) {
                        items.add(new Item(this.start, i + 1, this.word, Item.LOCATION));
                    }
                    if (this.tag.substring(this.tag.indexOf(this.connector) + 1).equals(this.personTag)) {
                        items.add(new Item(this.start, i + 1, this.word, Item.PERSON));
                    }
                    if (this.tag.substring(this.tag.indexOf(this.connector) + 1).equals(this.organizationTag)) {
                        items.add(new Item(this.start, i + 1, this.word, Item.ORGANIZATION));
                    }
                }
            }
        }
    }
    
    @Override
    public ArrayList<Item> recognize(final String sentence) {
        final ArrayList<Item> items = new ArrayList<Item>();
        this.nerResult = this.nerRecogizer.decodeSentence(sentence);
        this.tokens = this.nerResult.split("\\s+");
        this.word = "";
        this.start = -1;
        this.isConsistancy = true;
        for (int i = 0; i < this.tokens.length; ++i) {
            this.pairs = this.tokens[i].split(this.delimiter);
            this.tag = this.pairs[1];
            if (!this.tag.equals(this.otherTag)) {
                if (this.tag.startsWith("S")) {
                    if (this.start == -1) {
                        this.word = this.pairs[0];
                        if (this.tag.substring(this.tag.indexOf(this.connector) + 1).equals(this.locationTag)) {
                            items.add(new Item(i, i + 1, this.word, Item.LOCATION));
                        }
                        if (this.tag.substring(this.tag.indexOf(this.connector) + 1).equals(this.personTag)) {
                            items.add(new Item(i, i + 1, this.word, Item.PERSON));
                        }
                        if (this.tag.substring(this.tag.indexOf(this.connector) + 1).equals(this.organizationTag)) {
                            items.add(new Item(i, i + 1, this.word, Item.ORGANIZATION));
                        }
                    }
                    else {
                        this.isConsistancy = false;
                    }
                }
                if (this.tag.startsWith("B")) {
                    this.word = this.pairs[0];
                    this.start = i;
                    this.isConsistancy = true;
                }
                if (this.tag.startsWith("I") && this.start != -1) {
                    this.word = String.valueOf(this.word) + this.pairs[0];
                }
                if (this.tag.startsWith("E") && this.isConsistancy) {
                    this.word = String.valueOf(this.word) + this.pairs[0];
                    if (this.tag.substring(this.tag.indexOf(this.connector) + 1).equals(this.locationTag)) {
                        items.add(new Item(this.start, i + 1, this.word, Item.LOCATION));
                    }
                    if (this.tag.substring(this.tag.indexOf(this.connector) + 1).equals(this.personTag)) {
                        items.add(new Item(this.start, i + 1, this.word, Item.PERSON));
                    }
                    if (this.tag.substring(this.tag.indexOf(this.connector) + 1).equals(this.organizationTag)) {
                        items.add(new Item(this.start, i + 1, this.word, Item.ORGANIZATION));
                    }
                }
            }
        }
        return items;
    }
}
