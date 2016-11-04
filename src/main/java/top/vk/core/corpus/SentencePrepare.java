// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.corpus;

import top.vk.core.flow.PosTagger;
import top.vk.core.lang.IntermediateResult;
import top.vk.core.lang.Item;
import top.vk.core.lang.MeasureItem;
import top.vk.core.util.CheckPunctuation;

import java.io.*;
import java.util.*;

public class SentencePrepare
{
    private String preprocessFile;
    private String posTaggerFile;
    private String sentenceCorpusFile;
    private String negativeCorpusFile;
    private String vocaburaryFile;
    private String corpusFile;
    private String labelFile;
    private PosTagger posTagger;
    private ArrayList<String> sentences;
    private HashSet<String> vocaburary;
    private HashSet<String> label;
    private String[] samples;
    private boolean isDebug;
    private double percent;
    private int count;
    private String unknownLabel;
    private String delimiter;
    
    public SentencePrepare(final String preprocessFile, final String posTaggerFile, final String sentenceCorpusFile, final String negativeCorpusFile, final String corpusFile, final String vocaburaryFile, final String labelFile) {
        this.preprocessFile = null;
        this.posTaggerFile = null;
        this.sentenceCorpusFile = null;
        this.negativeCorpusFile = null;
        this.vocaburaryFile = null;
        this.corpusFile = null;
        this.labelFile = null;
        this.posTagger = null;
        this.sentences = null;
        this.vocaburary = null;
        this.label = null;
        this.samples = null;
        this.isDebug = false;
        this.percent = 0.8;
        this.count = 0;
        this.unknownLabel = "E_UNKNOWN";
        this.delimiter = "/";
        this.preprocessFile = preprocessFile;
        this.posTaggerFile = posTaggerFile;
        this.sentenceCorpusFile = sentenceCorpusFile;
        this.negativeCorpusFile = negativeCorpusFile;
        this.corpusFile = corpusFile;
        this.vocaburaryFile = vocaburaryFile;
        this.labelFile = labelFile;
    }
    
    public void prepare() {
        this.posTagger = new PosTagger(this.preprocessFile, this.posTaggerFile);
        this.sentences = new ArrayList<String>();
        this.vocaburary = new HashSet<String>();
        this.label = new HashSet<String>();
        String line = null;
        String event = null;
        int num = 0;
        IntermediateResult intermediateResult = null;
        int length = 0;
        String[] words = null;
        HashMap<Integer, Integer> wordIndex = null;
        boolean[] isSet = null;
        int shift = 0;
        Item item = null;
        MeasureItem measureItem = null;
        Iterator<Item> it = null;
        int type = -1;
        int position = -1;
        String description = null;
        String result = null;
        int index = -1;
        String character = null;
        String sentence = null;
        try {
            final FileInputStream fis = new FileInputStream(this.sentenceCorpusFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            num = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.equals("") && !line.equals(" ")) {
                    ++num;
                    index = line.indexOf(" ");
                    if (index == -1) {
                        System.out.println("Please check the file of " + this.sentenceCorpusFile + " at line " + num + ".");
                    }
                    event = line.substring(0, index).trim();
                    if (!this.label.contains(event)) {
                        this.label.add(event);
                    }
                    sentence = line.substring(index + 1);
                    if (!CheckPunctuation.isPunctation(sentence.substring(sentence.length() - 1))) {
                        sentence = String.valueOf(sentence) + "\u3002";
                    }
                    intermediateResult = this.posTagger.posTagging(sentence);
                    words = intermediateResult.getConstriants().split("\\s+");
                    length = words.length;
                    wordIndex = new HashMap<Integer, Integer>();
                    isSet = new boolean[length];
                    shift = 0;
                    for (int p = 0; p < length; ++p) {
                        wordIndex.put(shift, p);
                        index = words[p].indexOf(this.delimiter);
                        words[p] = words[p].substring(0, index);
                        shift += words[p].length();
                    }
                    it = intermediateResult.getItemList().iterator();
                    while (it.hasNext()) {
                        item = it.next();
                        if (item instanceof MeasureItem) {
                            measureItem = (MeasureItem)item;
                            if (wordIndex.get(measureItem.getStart()) == null) {
                                continue;
                            }
                            position = wordIndex.get(measureItem.getStart());
                            isSet[position] = true;
                            words[position] = measureItem.getExplanation();
                        }
                        else {
                            type = item.getType();
                            description = Item.deType(type);
                            if (wordIndex.get(item.getStart()) == null) {
                                continue;
                            }
                            position = wordIndex.get(item.getStart());
                            if (type != Item.EMAIL && type != Item.URL && type != Item.DATE && type != Item.PERCENT && type != Item.MEASURE && type != Item.TIME && type != Item.CURRENCY && type != Item.PERIOD && type != Item.CELLPHONE && type != Item.LANDLINE && type != Item.FOREIGN && type != Item.DIGIT) {
                                continue;
                            }
                            isSet[position] = true;
                            words[position] = description;
                        }
                    }
                    result = event;
                    for (int p = 0; p < length; ++p) {
                        if (isSet[p]) {
                            result = String.valueOf(result) + " " + words[p];
                            if (!this.vocaburary.contains(words[p])) {
                                this.vocaburary.add(words[p]);
                            }
                        }
                        else {
                            for (int i = 0; i < words[p].length(); ++i) {
                                character = words[p].substring(i, i + 1);
                                result = String.valueOf(result) + " " + character;
                                if (!this.vocaburary.contains(character)) {
                                    this.vocaburary.add(character);
                                }
                            }
                        }
                    }
                    if (this.isDebug) {
                        System.out.println(String.valueOf(line) + "\r\n" + result + "\r\n");
                    }
                    this.sentences.add(result);
                }
            }
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (!this.label.contains(this.unknownLabel)) {
            this.label.add(this.unknownLabel);
        }
        try {
            final FileInputStream fis = new FileInputStream(this.negativeCorpusFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            num = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.equals("") && !line.equals(" ")) {
                    ++num;
                    event = this.unknownLabel;
                    sentence = line;
                    if (!CheckPunctuation.isPunctation(sentence.substring(sentence.length() - 1))) {
                        sentence = String.valueOf(sentence) + "\u3002";
                    }
                    intermediateResult = this.posTagger.posTagging(sentence);
                    words = intermediateResult.getConstriants().split("\\s+");
                    length = words.length;
                    wordIndex = new HashMap<Integer, Integer>();
                    isSet = new boolean[length];
                    shift = 0;
                    for (int p = 0; p < length; ++p) {
                        wordIndex.put(shift, p);
                        index = words[p].indexOf(this.delimiter);
                        words[p] = words[p].substring(0, index);
                        shift += words[p].length();
                    }
                    it = intermediateResult.getItemList().iterator();
                    while (it.hasNext()) {
                        item = it.next();
                        if (item instanceof MeasureItem) {
                            measureItem = (MeasureItem)item;
                            if (wordIndex.get(measureItem.getStart()) == null) {
                                continue;
                            }
                            position = wordIndex.get(measureItem.getStart());
                            isSet[position] = true;
                            words[position] = measureItem.getExplanation();
                        }
                        else {
                            type = item.getType();
                            description = Item.deType(type);
                            if (wordIndex.get(item.getStart()) == null) {
                                continue;
                            }
                            position = wordIndex.get(item.getStart());
                            if (type != Item.EMAIL && type != Item.URL && type != Item.DATE && type != Item.PERCENT && type != Item.MEASURE && type != Item.TIME && type != Item.CURRENCY && type != Item.PERIOD && type != Item.CELLPHONE && type != Item.LANDLINE && type != Item.FOREIGN && type != Item.DIGIT) {
                                continue;
                            }
                            isSet[position] = true;
                            words[position] = description;
                        }
                    }
                    result = event;
                    for (int p = 0; p < length; ++p) {
                        if (isSet[p]) {
                            result = String.valueOf(result) + " " + words[p];
                            if (!this.vocaburary.contains(words[p])) {
                                this.vocaburary.add(words[p]);
                            }
                        }
                        else {
                            for (int i = 0; i < words[p].length(); ++i) {
                                character = words[p].substring(i, i + 1);
                                result = String.valueOf(result) + " " + character;
                                if (!this.vocaburary.contains(character)) {
                                    this.vocaburary.add(character);
                                }
                            }
                        }
                    }
                    if (this.isDebug) {
                        System.out.println(String.valueOf(line) + "\r\n" + result + "\r\n");
                    }
                    this.sentences.add(result);
                }
            }
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.samples = new String[this.sentences.size()];
        for (int j = 0; j < this.sentences.size(); ++j) {
            this.samples[j] = this.sentences.get(j);
        }
        this.sentences.clear();
    }
    
    public void shuffle() {
        this.count = this.samples.length;
        System.out.println("We have " + this.count + " samples (positive plus negative) in the corpus.");
        final int totalTimes = (int)(this.count * this.percent);
        int times = 0;
        String temp = null;
        final Random randomgen = new Random();
        int random1 = -1;
        int random2 = -1;
        while (times < totalTimes) {
            for (random1 = (int)(this.count * randomgen.nextFloat()), random2 = (int)(this.count * randomgen.nextFloat()); random1 == random2; random2 = (int)(this.count * randomgen.nextFloat())) {}
            temp = this.samples[random1];
            this.samples[random1] = this.samples[random2];
            this.samples[random2] = temp;
            ++times;
        }
    }
    
    public void write() {
        try {
            final FileOutputStream fos = new FileOutputStream(this.corpusFile);
            final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            for (int i = 0; i < this.samples.length; ++i) {
                osw.write(String.valueOf(this.samples[i]) + "\r\n");
            }
            osw.flush();
            osw.close();
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            final FileOutputStream fos = new FileOutputStream(this.vocaburaryFile);
            final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            final Iterator<String> it = this.vocaburary.iterator();
            while (it.hasNext()) {
                osw.write(String.valueOf(it.next()) + "\r\n");
            }
            osw.flush();
            osw.close();
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            final FileOutputStream fos = new FileOutputStream(this.labelFile);
            final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            System.out.println("We have " + this.label.size() + " labels (positive plus negative) in the corpus.");
            final Iterator<String> it = this.label.iterator();
            while (it.hasNext()) {
                osw.write(String.valueOf(it.next()) + "\r\n");
            }
            osw.flush();
            osw.close();
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
