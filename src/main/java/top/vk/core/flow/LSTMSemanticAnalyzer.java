// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.flow;

import top.vk.core.dnn.WindowConvolutionNetworkDecoder;
import top.vk.core.lang.IntermediateResult;
import top.vk.core.lang.Item;
import top.vk.core.lang.MeasureItem;
import top.vk.core.lang.Result;
import top.vk.core.lstm.BiLSTMwithTransitionDecoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

public class LSTMSemanticAnalyzer
{
    private String preprocessFile;
    private String posTaggerFile;
    private String semanticAnalyerFile;
    private Preprocess preprocess;
    private WindowConvolutionNetworkDecoder posTagger;
    private BiLSTMwithTransitionDecoder semanticAnalyzer;
    private String delimiter;
    private IntermediateResult intermediateResult;
    private String[] words;
    private String[] preparedSentence;
    private String[] tokens;
    private boolean[] isSet;
    private int index;
    private int length;
    private Item item;
    private MeasureItem measureItem;
    private Iterator<Item> it;
    private HashMap<Integer, Integer> wordIndex;
    private int shift;
    private int position;
    private int type;
    private String description;
    private String[] labels;
    private Result result;
    private String eventType;
    
    public LSTMSemanticAnalyzer(final String preprocessFile, final String posTaggerFile, final String semanticAnalyerFile) {
        this.preprocessFile = null;
        this.posTaggerFile = null;
        this.semanticAnalyerFile = null;
        this.preprocess = null;
        this.posTagger = null;
        this.semanticAnalyzer = null;
        this.delimiter = "/";
        this.intermediateResult = null;
        this.words = null;
        this.preparedSentence = null;
        this.tokens = null;
        this.isSet = null;
        this.index = -1;
        this.length = 0;
        this.item = null;
        this.measureItem = null;
        this.it = null;
        this.wordIndex = null;
        this.shift = 0;
        this.position = -1;
        this.type = -1;
        this.description = null;
        this.labels = null;
        this.eventType = null;
        this.preprocessFile = preprocessFile;
        this.posTaggerFile = posTaggerFile;
        this.preprocess = new Preprocess(preprocessFile);
        final Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(posTaggerFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        (this.posTagger = new WindowConvolutionNetworkDecoder(prop.getProperty("inputNetworkSettingFile"))).setCharacterLevel(Boolean.parseBoolean(prop.getProperty("isCharacterLevel")));
        this.posTagger.setResultWithTag(Boolean.parseBoolean(prop.getProperty("isResultWithTag")));
        this.posTagger.setSegmentation(Boolean.parseBoolean(prop.getProperty("isSegmentation")));
        this.posTagger.setStandard(Boolean.parseBoolean(prop.getProperty("isStandard")));
        this.posTagger.readPara();
        try {
            prop.load(new FileInputStream(semanticAnalyerFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.semanticAnalyzer = new BiLSTMwithTransitionDecoder(prop.getProperty("inputNetworkSettingFile"));
    }
    
    public Result semanticAnalyzing(String sentence) {
        sentence = this.preprocess.normalize(sentence);
        sentence = this.preprocess.replaceSlang(sentence);
        (this.intermediateResult = this.preprocess.processForPosTagging(sentence)).setConstriants(this.posTagger.decodeSentence(sentence, this.intermediateResult.getConstriants()));
        this.tokens = this.intermediateResult.getConstriants().split("\\s+");
        this.length = this.tokens.length;
        this.words = new String[this.length];
        this.preparedSentence = new String[this.length];
        this.wordIndex = new HashMap<Integer, Integer>();
        this.isSet = new boolean[this.length];
        this.shift = 0;
        for (int p = 0; p < this.length; ++p) {
            this.index = this.tokens[p].lastIndexOf(this.delimiter);
            this.words[p] = this.tokens[p].substring(0, this.index);
            this.preparedSentence[p] = this.tokens[p].substring(this.index + 1);
            this.wordIndex.put(this.shift, p);
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
                this.preparedSentence[this.position] = String.valueOf(this.measureItem.getExplanation()) + " " + this.preparedSentence[this.position] + " " + this.measureItem.getExplanation();
            }
            else {
                this.type = this.item.getType();
                this.description = Item.deType(this.type);
                if (this.wordIndex.get(this.item.getStart()) == null) {
                    continue;
                }
                this.position = this.wordIndex.get(this.item.getStart());
                this.isSet[this.position] = true;
                if (this.type == Item.EMAIL || this.type == Item.URL || this.type == Item.DATE || this.type == Item.PERCENT || this.type == Item.MEASURE || this.type == Item.TIME || this.type == Item.CURRENCY || this.type == Item.PERIOD || this.type == Item.CELLPHONE || this.type == Item.LANDLINE || this.type == Item.FOREIGN || this.type == Item.DIGIT) {
                    this.preparedSentence[this.position] = String.valueOf(this.description) + " " + this.preparedSentence[this.position] + " " + this.description;
                }
                else {
                    this.preparedSentence[this.position] = String.valueOf(this.words[this.position]) + " " + this.preparedSentence[this.position] + " " + this.description;
                }
            }
        }
        for (int p = 0; p < this.length; ++p) {
            if (!this.isSet[p]) {
                this.preparedSentence[p] = String.valueOf(this.words[p]) + " " + this.preparedSentence[p] + " " + "N/A";
            }
        }
        this.labels = this.semanticAnalyzer.decodeSentence(this.preparedSentence);
        return this.result = new Result(this.eventType, this.intermediateResult, this.words, this.labels);
    }
    
    public String getPreprocessFile() {
        return this.preprocessFile;
    }
    
    public String getPosTaggerFile() {
        return this.posTaggerFile;
    }
    
    public String getSemanticAnalyerFile() {
        return this.semanticAnalyerFile;
    }
}
