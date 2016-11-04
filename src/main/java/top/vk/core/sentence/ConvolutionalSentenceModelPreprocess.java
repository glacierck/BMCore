// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.sentence;

import top.vk.core.flow.PosTagger;
import top.vk.core.lang.IntermediateResult;
import top.vk.core.lang.Item;
import top.vk.core.lang.MeasureItem;
import top.vk.core.util.CheckPunctuation;

import java.util.HashMap;
import java.util.Iterator;

public class ConvolutionalSentenceModelPreprocess
{
    private PosTagger posTagger;
    private IntermediateResult intermediateResult;
    private int length;
    private int index;
    private int position;
    private int type;
    private String description;
    private String character;
    private HashMap<Integer, Integer> wordIndex;
    private String[] words;
    private boolean[] isSet;
    private int shift;
    private Item item;
    private MeasureItem measureItem;
    private Iterator<Item> it;
    private String delimiter;
    
    public ConvolutionalSentenceModelPreprocess(final String preprocessFile, final String posTaggerFile) {
        this.posTagger = null;
        this.intermediateResult = null;
        this.length = 0;
        this.index = -1;
        this.position = -1;
        this.type = -1;
        this.description = null;
        this.character = null;
        this.wordIndex = null;
        this.words = null;
        this.isSet = null;
        this.shift = 0;
        this.item = null;
        this.measureItem = null;
        this.it = null;
        this.delimiter = "/";
        this.posTagger = new PosTagger(preprocessFile, posTaggerFile);
    }
    
    public String preprocess(String sentence) {
        if (!CheckPunctuation.isPunctation(sentence.substring(sentence.length() - 1))) {
            sentence = String.valueOf(sentence) + "\u3002";
        }
        this.intermediateResult = this.posTagger.posTagging(sentence);
        this.words = this.intermediateResult.getConstriants().split("\\s+");
        this.length = this.words.length;
        this.wordIndex = new HashMap<Integer, Integer>();
        this.isSet = new boolean[this.length];
        this.shift = 0;
        for (int p = 0; p < this.length; ++p) {
            this.wordIndex.put(this.shift, p);
            this.index = this.words[p].indexOf(this.delimiter);
            this.words[p] = this.words[p].substring(0, this.index);
            this.shift += this.words[p].length();
        }
        this.it = this.intermediateResult.getItemList().iterator();
        while (this.it.hasNext()) {
            this.item = this.it.next();
            if (this.item instanceof MeasureItem) {
                this.measureItem = (MeasureItem)this.item;
                if (this.wordIndex.get(this.measureItem.getStart()) == null) {
                    continue;
                }
                this.position = this.wordIndex.get(this.measureItem.getStart());
                this.isSet[this.position] = true;
                this.words[this.position] = this.measureItem.getExplanation();
            }
            else {
                this.type = this.item.getType();
                this.description = Item.deType(this.type);
                if (this.wordIndex.get(this.item.getStart()) == null) {
                    continue;
                }
                this.position = this.wordIndex.get(this.item.getStart());
                if (this.type != Item.EMAIL && this.type != Item.URL && this.type != Item.DATE && this.type != Item.PERCENT && this.type != Item.MEASURE && this.type != Item.TIME && this.type != Item.CURRENCY && this.type != Item.PERIOD && this.type != Item.CELLPHONE && this.type != Item.LANDLINE && this.type != Item.FOREIGN && this.type != Item.DIGIT) {
                    continue;
                }
                this.isSet[this.position] = true;
                this.words[this.position] = this.description;
            }
        }
        if (this.isSet[0]) {
            sentence = this.words[0];
        }
        else {
            for (int i = 0; i < this.words[0].length(); ++i) {
                this.character = this.words[0].substring(i, i + 1);
                sentence = String.valueOf(sentence) + " " + this.character;
            }
        }
        for (int p = 1; p < this.length; ++p) {
            if (this.isSet[p]) {
                sentence = String.valueOf(sentence) + " " + this.words[p];
            }
            else {
                for (int j = 0; j < this.words[p].length(); ++j) {
                    this.character = this.words[p].substring(j, j + 1);
                    sentence = String.valueOf(sentence) + " " + this.character;
                }
            }
        }
        return sentence;
    }
}
