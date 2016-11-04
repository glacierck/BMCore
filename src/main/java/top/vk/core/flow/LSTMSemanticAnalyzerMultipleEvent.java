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
import top.vk.core.sentence.ConvolutionalSentenceModelDecoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class LSTMSemanticAnalyzerMultipleEvent
{
    private String preprocessFile;
    private String posTaggerFile;
    private String confFile;
    private Preprocess preprocess;
    private WindowConvolutionNetworkDecoder posTagger;
    private ConvolutionalSentenceModelDecoder sentenceModel;
    private BiLSTMwithTransitionDecoder semanticAnalyzer;
    private String sentenceModelSettingFile;
    private String delimiter;
    private String otherTag;
    private IntermediateResult intermediateResult;
    private String[] words;
    private String[] preparedSentence;
    private String[][] sent;
    private int numberOfColumn;
    private String[] tokens;
    private boolean[] isSet;
    private boolean[] isReplace;
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
    private String sentenceModelInput;
    private String character;
    private String eventType;
    private Result result;
    private String unkown;
    private HashMap<String, BiLSTMwithTransitionDecoder> semanticAnalyzers;
    
    public LSTMSemanticAnalyzerMultipleEvent(final String preprocessFile, final String posTaggerFile, final String sentenceModelSettingFile, final String confFile) {
        this.preprocessFile = null;
        this.posTaggerFile = null;
        this.confFile = null;
        this.preprocess = null;
        this.posTagger = null;
        this.sentenceModel = null;
        this.semanticAnalyzer = null;
        this.sentenceModelSettingFile = null;
        this.delimiter = "/";
        this.otherTag = "O";
        this.intermediateResult = null;
        this.words = null;
        this.preparedSentence = null;
        this.sent = null;
        this.numberOfColumn = 5;
        this.tokens = null;
        this.isSet = null;
        this.isReplace = null;
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
        this.sentenceModelInput = null;
        this.character = null;
        this.eventType = null;
        this.result = null;
        this.unkown = "E_UNKNOWN";
        this.semanticAnalyzers = null;
        this.preprocessFile = preprocessFile;
        this.posTaggerFile = posTaggerFile;
        this.sentenceModelSettingFile = sentenceModelSettingFile;
        this.preprocess = new Preprocess(preprocessFile);
        Properties prop = new Properties();
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
        (this.sentenceModel = new ConvolutionalSentenceModelDecoder(sentenceModelSettingFile)).readPara();
        prop = new Properties();
        try {
            prop.load(new FileInputStream(confFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.semanticAnalyzers = new HashMap<String, BiLSTMwithTransitionDecoder>();
        String eventName = null;
        final Iterator<Map.Entry<Object, Object>> it = prop.entrySet().iterator();
        while (it.hasNext()) {
            eventName = (String) it.next().getKey();
            this.semanticAnalyzers.put(eventName, new BiLSTMwithTransitionDecoder(prop.getProperty(eventName)));
        }
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
        this.isReplace = new boolean[this.length];
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
                this.isReplace[this.position] = true;
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
                    this.isReplace[this.position] = true;
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
        this.sent = new String[this.numberOfColumn][this.length];
        for (int p = 0; p < this.length; ++p) {
            this.tokens = this.preparedSentence[p].split("\\s+");
            for (int c = 0; c < this.numberOfColumn - 1; ++c) {
                if (c <= 1) {
                    if (c == 0) {
                        this.sent[c][p] = this.tokens[c];
                    }
                    else {
                        this.sent[c][p] = this.otherTag;
                    }
                }
                else {
                    this.sent[c][p] = this.tokens[c - 1];
                }
            }
        }
        this.sentenceModelInput = "";
        for (int p = 0; p < this.length; ++p) {
            if (this.isReplace[p]) {
                this.sentenceModelInput = String.valueOf(this.sentenceModelInput) + " " + this.sent[0][p];
            }
            else {
                for (int i = 0; i < this.words[p].length(); ++i) {
                    this.character = this.words[p].substring(i, i + 1);
                    this.sentenceModelInput = String.valueOf(this.sentenceModelInput) + " " + this.character;
                }
            }
        }
        this.eventType = this.sentenceModel.decodeSentence(this.sentenceModelInput);
        if (this.eventType.equals(this.unkown)) {
            this.result = new Result(this.unkown, null, null, null);
        }
        else {
            this.semanticAnalyzer = this.semanticAnalyzers.get(this.eventType);
            this.labels = this.semanticAnalyzer.decodeSentence(this.preparedSentence);
            this.result = new Result(this.eventType, this.intermediateResult, this.words, this.labels);
        }
        return this.result;
    }
    
    public String getPreprocessFile() {
        return this.preprocessFile;
    }
    
    public String getPosTaggerFile() {
        return this.posTaggerFile;
    }
    
    public String getConfFile() {
        return this.confFile;
    }
    
    public String getSentenceModelSettingFile() {
        return this.sentenceModelSettingFile;
    }
}
