// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.corpus;

import top.vk.core.dnn.WindowConvolutionNetworkDecoder;
import top.vk.core.flow.Preprocess;
import top.vk.core.lang.IntermediateResult;
import top.vk.core.lang.Item;
import top.vk.core.lang.MeasureItem;
import top.vk.core.util.CheckPunctuation;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

public class SemanticCorpusPrepare
{
    private String preprocessFile;
    private String posTaggerFile;
    private String sentenceFile;
    private String outputFile;
    private Preprocess preprocess;
    private WindowConvolutionNetworkDecoder posTagger;
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
    private String line;
    private String eventType;
    private boolean isStart;
    private boolean isMultiEvent;
    private boolean hasHeadingEventType;
    
    public SemanticCorpusPrepare(final String preprocessFile, final String posTaggerFile, final String sentenceFile, final String outputFile, final boolean isMultiEvent, final boolean hasHeadingEventType) {
        this.preprocessFile = null;
        this.posTaggerFile = null;
        this.sentenceFile = null;
        this.outputFile = null;
        this.preprocess = null;
        this.posTagger = null;
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
        this.line = null;
        this.eventType = null;
        this.isStart = true;
        this.isMultiEvent = true;
        this.hasHeadingEventType = true;
        this.sentenceFile = sentenceFile;
        this.outputFile = outputFile;
        this.preprocessFile = preprocessFile;
        this.posTaggerFile = posTaggerFile;
        this.isMultiEvent = isMultiEvent;
        this.hasHeadingEventType = hasHeadingEventType;
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
    }
    
    public void prepare() {
        int count = 0;
        try {
            final FileInputStream fis = new FileInputStream(this.sentenceFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            final FileOutputStream fos = new FileOutputStream(this.outputFile);
            final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            this.isStart = true;
            while ((this.line = br.readLine()) != null) {
                this.line = this.line.trim();
                if (!this.line.equals("") && !this.line.equals(" ")) {
                    ++count;
                    if (this.hasHeadingEventType) {
                        this.index = this.line.indexOf(" ");
                        if (this.index != -1) {
                            this.eventType = this.line.substring(0, this.index);
                            this.line = this.line.substring(this.index + 1);
                        }
                        else {
                            System.out.println("Chech the sentence at the line + " + count + ".");
                            System.exit(0);
                        }
                    }
                    if (!CheckPunctuation.isPunctation(this.line.substring(this.line.length() - 1))) {
                        this.line = String.valueOf(this.line) + "\u3002";
                    }
                    this.line = this.preprocess.normalize(this.line);
                    this.line = this.preprocess.replaceSlang(this.line);
                    (this.intermediateResult = this.preprocess.processForPosTagging(this.line)).setConstriants(this.posTagger.decodeSentence(this.line, this.intermediateResult.getConstriants()));
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
                            this.preparedSentence[this.position] = String.valueOf(this.measureItem.getExplanation()) + " O " + this.preparedSentence[this.position] + " " + this.measureItem.getExplanation() + " " + this.words[this.position];
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
                                this.preparedSentence[this.position] = String.valueOf(this.description) + " O " + this.preparedSentence[this.position] + " " + this.description + " " + this.words[this.position];
                            }
                            else {
                                this.preparedSentence[this.position] = String.valueOf(this.words[this.position]) + " O " + this.preparedSentence[this.position] + " " + this.description + " " + this.words[this.position];
                            }
                        }
                    }
                    if (this.isStart) {
                        this.isStart = false;
                    }
                    else {
                        osw.write("\r\n");
                    }
                    if (this.hasHeadingEventType) {
                        osw.write(String.valueOf(this.eventType) + "\r\n");
                    }
                    else if (this.isMultiEvent) {
                        osw.write("EventType\r\n");
                    }
                    for (int p = 0; p < this.length; ++p) {
                        if (!this.isSet[p]) {
                            this.preparedSentence[p] = String.valueOf(this.words[p]) + " O " + this.preparedSentence[p] + " " + "N/A" + " " + this.words[p];
                        }
                        osw.write(String.valueOf(this.preparedSentence[p]) + "\r\n");
                    }
                }
            }
            osw.flush();
            osw.close();
            fos.close();
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getPreprocessFile() {
        return this.preprocessFile;
    }
    
    public String getPosTaggerFile() {
        return this.posTaggerFile;
    }
}
