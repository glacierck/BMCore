// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.flow;

import top.vk.core.dnn.WindowConvolutionNetworkDecoder;
import top.vk.core.lang.IntermediateResult;
import top.vk.core.lang.Item;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class NamedIdentityRecognizer
{
    private String preprocessFile;
    private String nerRecogizerFile;
    private Preprocess preprocess;
    private WindowConvolutionNetworkDecoder nerRecognizer;
    private IntermediateResult intermediateResult;
    
    public NamedIdentityRecognizer(final String preprocessFile, final String nerRecogizerFile) {
        this.preprocessFile = null;
        this.nerRecogizerFile = null;
        this.preprocess = null;
        this.nerRecognizer = null;
        this.intermediateResult = null;
        this.preprocessFile = preprocessFile;
        this.nerRecogizerFile = nerRecogizerFile;
        this.preprocess = new Preprocess(preprocessFile);
        final Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(nerRecogizerFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        (this.nerRecognizer = new WindowConvolutionNetworkDecoder(prop.getProperty("inputNetworkSettingFile"))).setCharacterLevel(Boolean.parseBoolean(prop.getProperty("isCharacterLevel")));
        this.nerRecognizer.setResultWithTag(Boolean.parseBoolean(prop.getProperty("isResultWithTag")));
        this.nerRecognizer.setSegmentation(Boolean.parseBoolean(prop.getProperty("isSegmentation")));
        this.nerRecognizer.setStandard(Boolean.parseBoolean(prop.getProperty("isStandard")));
        this.nerRecognizer.readPara();
    }
    
    public IntermediateResult recognize(String sentence) {
        sentence = this.preprocess.normalize(sentence);
        sentence = this.preprocess.replaceSlang(sentence);
        (this.intermediateResult = this.preprocess.processForNerRecognition(sentence)).setConstriants(this.nerRecognizer.decodeSentence(sentence, this.intermediateResult.getConstriants()));
        return this.intermediateResult = this.postProcess(this.intermediateResult.getConstriants(), this.intermediateResult.getItemList());
    }
    
    public String getPreprocessFile() {
        return this.preprocessFile;
    }
    
    public void setPreprocessFile(final String preprocessFile) {
        this.preprocessFile = preprocessFile;
    }
    
    public String getNerRecogizerFile() {
        return this.nerRecogizerFile;
    }
    
    public void setNerRecogizerFile(final String nerRecogizerFile) {
        this.nerRecogizerFile = nerRecogizerFile;
    }
    
    public IntermediateResult postProcess(final String nerResult, final ArrayList<Item> items) {
        final String[] tokens = nerResult.split("\\s+");
        String word = "";
        int start = -1;
        boolean isConsistancy = true;
        final String delimiter = "/";
        final String connector = "_";
        final String locationTag = "LOC";
        final String personTag = "PER";
        final String organizationTag = "ORG";
        final String otherTag = "O";
        final int length = tokens.length;
        final String[] sentence = new String[length];
        for (int i = 0; i < length; ++i) {
            final String[] pairs = tokens[i].split(delimiter);
            sentence[i] = pairs[0];
            final String tag = pairs[1];
            if (!tag.equals(otherTag)) {
                if (tag.startsWith("S")) {
                    if (start == -1) {
                        word = pairs[0];
                        if (tag.substring(tag.indexOf(connector) + 1).equals(locationTag)) {
                            items.add(new Item(i, i + 1, word, Item.LOCATION));
                        }
                        if (tag.substring(tag.indexOf(connector) + 1).equals(personTag)) {
                            items.add(new Item(i, i + 1, word, Item.PERSON));
                        }
                        if (tag.substring(tag.indexOf(connector) + 1).equals(organizationTag)) {
                            items.add(new Item(i, i + 1, word, Item.ORGANIZATION));
                        }
                    }
                    else {
                        isConsistancy = false;
                    }
                }
                if (tag.startsWith("B")) {
                    word = pairs[0];
                    start = i;
                    isConsistancy = true;
                }
                if (tag.startsWith("I") && start != -1) {
                    word = String.valueOf(word) + pairs[0];
                }
                if (tag.startsWith("E") && isConsistancy) {
                    word = String.valueOf(word) + pairs[0];
                    if (tag.substring(tag.indexOf(connector) + 1).equals(locationTag)) {
                        items.add(new Item(start, i + 1, word, Item.LOCATION));
                    }
                    if (tag.substring(tag.indexOf(connector) + 1).equals(personTag)) {
                        items.add(new Item(start, i + 1, word, Item.PERSON));
                    }
                    if (tag.substring(tag.indexOf(connector) + 1).equals(organizationTag)) {
                        items.add(new Item(start, i + 1, word, Item.ORGANIZATION));
                    }
                }
            }
        }
        final ArrayList<Item> itemList = new ArrayList<Item>();
        final Iterator<Item> it = items.iterator();
        Item item = null;
        String constraints = "";
        final String[] labels = new String[length];
        for (int p = 0; p < length; ++p) {
            labels[p] = otherTag;
        }
        while (it.hasNext()) {
            item = it.next();
            if (labels[item.getStart()].equals(otherTag) && labels[item.getEnd() - 1].equals(otherTag)) {
                final String tag = String.valueOf(connector) + Item.deType(item.getType());
                if (item.getStart() == item.getEnd() - 1) {
                    labels[item.getStart()] = "S" + tag;
                }
                else {
                    labels[item.getStart()] = "B" + tag;
                    for (int p = item.getStart() + 1; p < item.getEnd() - 1; ++p) {
                        labels[p] = "I" + tag;
                    }
                    labels[item.getEnd() - 1] = "E" + tag;
                }
                itemList.add(item);
            }
        }
        for (int p = 0; p < length; ++p) {
            if (labels[p].startsWith(otherTag)) {
                constraints = String.valueOf(constraints) + sentence[p] + delimiter + otherTag + " ";
            }
            if (labels[p].startsWith("S")) {
                constraints = String.valueOf(constraints) + sentence[p] + delimiter + labels[p].substring(2) + " ";
            }
            if (labels[p].startsWith("B")) {
                constraints = String.valueOf(constraints) + sentence[p];
            }
            if (labels[p].startsWith("I")) {
                constraints = String.valueOf(constraints) + sentence[p];
            }
            if (labels[p].startsWith("E")) {
                constraints = String.valueOf(constraints) + sentence[p] + delimiter + labels[p].substring(2) + " ";
            }
        }
        constraints = constraints.trim();
        final IntermediateResult intermediateResult = new IntermediateResult(constraints, itemList);
        return intermediateResult;
    }
}
