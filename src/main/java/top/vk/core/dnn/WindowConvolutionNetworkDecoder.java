// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.dnn;

import top.vk.core.util.ChineseNormalize;

import java.io.*;
import java.util.HashMap;

public class WindowConvolutionNetworkDecoder
{
    private String inputNetworkSettingFile;
    private String outputTaggingFile;
    private String inputSentenceFile;
    private int featureDimension;
    private int windowSize;
    private int featureMap;
    private int halfwindow;
    private HashMap<String, double[]> lookupTable;
    private double[][][] weightConvolution;
    private double[][] biasConvolution;
    private double[][] weightHO;
    private double[] biasOutput;
    private double[][] transition;
    private String[] tagset;
    private boolean isIgnoreAlphabetNumber;
    private boolean isAverage;
    private boolean isCharacterLevel;
    private HashMap<String, Integer> tagIndex;
    private boolean isResultWithTag;
    private boolean isSegmentation;
    private boolean isStandard;
    private String delimiter;
    
    public WindowConvolutionNetworkDecoder(final String inputNetworkSettingFile) {
        this.inputNetworkSettingFile = null;
        this.outputTaggingFile = null;
        this.inputSentenceFile = null;
        this.featureDimension = 100;
        this.windowSize = 5;
        this.featureMap = 5;
        this.halfwindow = 2;
        this.lookupTable = null;
        this.weightConvolution = null;
        this.biasConvolution = null;
        this.weightHO = null;
        this.biasOutput = null;
        this.transition = null;
        this.tagset = null;
        this.isIgnoreAlphabetNumber = true;
        this.isAverage = false;
        this.isCharacterLevel = true;
        this.tagIndex = null;
        this.isResultWithTag = false;
        this.isSegmentation = true;
        this.isStandard = true;
        this.delimiter = "/";
        this.inputNetworkSettingFile = inputNetworkSettingFile;
    }
    
    public void readPara() {
        try {
            final FileInputStream fis = new FileInputStream(this.inputNetworkSettingFile);
            final ObjectInputStream ois = new ObjectInputStream(fis);
            final WindowConvolutionNetworkSetting networkSetting = (WindowConvolutionNetworkSetting)ois.readObject();
            this.featureDimension = networkSetting.getFeatureDimension();
            this.windowSize = networkSetting.getWindowSize();
            this.featureMap = networkSetting.getFeatureMap();
            this.isIgnoreAlphabetNumber = networkSetting.isIgnoreAlphabetNumber();
            this.lookupTable = networkSetting.getLookupTable();
            this.weightConvolution = networkSetting.getWeightConvolution();
            this.biasConvolution = networkSetting.getBiasConvolution();
            this.weightHO = networkSetting.getWeightHO();
            this.biasOutput = networkSetting.getBiasOutput();
            this.transition = networkSetting.getTransition();
            this.tagset = networkSetting.getTagset();
            this.isAverage = networkSetting.isAverage();
            ois.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.halfwindow = (this.windowSize - 1) / 2;
        this.tagIndex = new HashMap<String, Integer>();
        for (int t = 0; t < this.tagset.length; ++t) {
            this.tagIndex.put(this.tagset[t], t);
        }
    }
    
    public void decode() {
        this.decodeFile(this.inputSentenceFile, this.outputTaggingFile);
    }
    
    public void decodeFile(final String inputFile, final String outputFile) {
        int length = 0;
        double[][][] input = null;
        double[][][] convolution = null;
        double[][] combine = null;
        double[][] output = null;
        double[][] optVal = null;
        int[][] optRec = null;
        double temp = 0.0;
        String[] optSequence = null;
        double optValue = -1.7976931348623157E308;
        try {
            final FileOutputStream fos = new FileOutputStream(outputFile);
            final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            final FileInputStream fis = new FileInputStream(inputFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            boolean space = false;
            String[] sentence = null;
            String line = null;
            while ((line = br.readLine()) != null) {
                if (this.isCharacterLevel) {
                    line = line.replaceAll(" ", "");
                }
                line = line.trim();
                if (!line.equals("")) {
                    line = ChineseNormalize.normalize(line, false);
                    sentence = this.seperate(sentence, line);
                    length = sentence.length;
                    optVal = null;
                    optRec = null;
                    temp = 0.0;
                    optSequence = null;
                    optValue = -1.7976931348623157E308;
                    input = new double[length][this.windowSize][this.featureDimension];
                    convolution = new double[length][this.featureMap][this.featureDimension];
                    combine = new double[length][this.featureDimension];
                    output = new double[length][this.tagset.length];
                    for (int p = 0; p < length; ++p) {
                        for (int w = 0; w < this.windowSize; ++w) {
                            final int shift = p - this.halfwindow + w;
                            if (w == this.halfwindow) {
                                input[p][w] = this.getFeature(sentence[shift]);
                            }
                            else if (w < this.halfwindow) {
                                if (shift < 0) {
                                    input[p][w] = this.getFeature(getBeginning());
                                }
                                else {
                                    input[p][w] = this.getFeature(sentence[shift]);
                                }
                            }
                            else if (w > this.halfwindow) {
                                if (shift > length - 1) {
                                    input[p][w] = this.getFeature(getEnding());
                                }
                                else {
                                    input[p][w] = this.getFeature(sentence[shift]);
                                }
                            }
                        }
                    }
                    for (int p = 0; p < length; ++p) {
                        for (int f = 0; f < this.featureMap; ++f) {
                            for (int d = 0; d < this.featureDimension; ++d) {
                                for (int w2 = 0; w2 < this.windowSize; ++w2) {
                                    final double[] array = convolution[p][f];
                                    final int n = d;
                                    array[n] += input[p][w2][d] * this.weightConvolution[f][d][w2];
                                }
                                final double[] array2 = convolution[p][f];
                                final int n2 = d;
                                array2[n2] += this.biasConvolution[f][d];
                                convolution[p][f][d] = this.sigmoid(convolution[p][f][d]);
                            }
                        }
                    }
                    for (int p = 0; p < length; ++p) {
                        for (int f = 0; f < this.featureMap; ++f) {
                            for (int d = 0; d < this.featureDimension; ++d) {
                                final double[] array3 = combine[p];
                                final int n3 = d;
                                array3[n3] += convolution[p][f][d];
                            }
                        }
                    }
                    if (this.isAverage) {
                        for (int p = 0; p < length; ++p) {
                            for (int d2 = 0; d2 < this.featureDimension; ++d2) {
                                combine[p][d2] /= this.featureMap;
                            }
                        }
                    }
                    for (int p = 0; p < length; ++p) {
                        for (int t = 0; t < this.tagset.length; ++t) {
                            for (int d = 0; d < this.featureDimension; ++d) {
                                final double[] array4 = output[p];
                                final int n4 = t;
                                array4[n4] += combine[p][d] * this.weightHO[t][d];
                            }
                            final double[] array5 = output[p];
                            final int n5 = t;
                            array5[n5] += this.biasOutput[t];
                        }
                    }
                    optVal = new double[length][this.tagset.length];
                    optRec = new int[length][this.tagset.length];
                    for (int i = 0; i < length; ++i) {
                        for (int j = 0; j < this.tagset.length; ++j) {
                            optVal[i][j] = -1.7976931348623157E308;
                            optRec[i][j] = 0;
                        }
                    }
                    temp = 0.0;
                    for (int p = 0; p < length; ++p) {
                        for (int t = 0; t < this.tagset.length; ++t) {
                            if (p == 0) {
                                optVal[p][t] = output[p][t] + this.transition[t][this.tagset.length];
                            }
                            else {
                                for (int prev = 0; prev < this.tagset.length; ++prev) {
                                    temp = optVal[p - 1][prev] + this.transition[prev][t] + output[p][t];
                                    if (temp > optVal[p][t]) {
                                        optVal[p][t] = temp;
                                        optRec[p][t] = prev;
                                    }
                                }
                            }
                        }
                    }
                    optSequence = new String[length];
                    optValue = -1.7976931348623157E308;
                    int optTag = -1;
                    for (int t = 0; t < this.tagset.length; ++t) {
                        if (optVal[length - 1][t] > optValue) {
                            optValue = optVal[length - 1][t];
                            optTag = t;
                        }
                    }
                    optSequence[length - 1] = this.tagset[optTag];
                    for (int k = length - 1; k > 0; --k) {
                        optTag = optRec[k][optTag];
                        optSequence[k - 1] = this.tagset[optTag];
                    }
                    if (space) {
                        osw.write("\r\n");
                    }
                    for (int k = 0; k < length; ++k) {
                        osw.write(String.valueOf(sentence[k]) + " " + optSequence[k] + "\r\n");
                    }
                    if (space) {
                        continue;
                    }
                    space = true;
                }
            }
            br.close();
            isr.close();
            fis.close();
            osw.flush();
            osw.close();
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String decodeSentence(String sent) {
        if (this.isCharacterLevel) {
            sent = sent.replaceAll(" ", "");
        }
        sent = sent.trim();
        if (!sent.equals("")) {
            String[] sentence = null;
            if (this.isStandard) {
                sent = ChineseNormalize.normalize(sent, false);
            }
            sentence = this.seperate(sentence, sent);
            final int length = sentence.length;
            final double[][][] input = new double[length][this.windowSize][this.featureDimension];
            final double[][][] convolution = new double[length][this.featureMap][this.featureDimension];
            final double[][] combine = new double[length][this.featureDimension];
            final double[][] output = new double[length][this.tagset.length];
            double[][] optVal = null;
            int[][] optRec = null;
            double temp = 0.0;
            String[] optSequence = null;
            double optValue = -1.7976931348623157E308;
            for (int p = 0; p < length; ++p) {
                for (int w = 0; w < this.windowSize; ++w) {
                    final int shift = p - this.halfwindow + w;
                    if (w == this.halfwindow) {
                        input[p][w] = this.getFeature(sentence[shift]);
                    }
                    else if (w < this.halfwindow) {
                        if (shift < 0) {
                            input[p][w] = this.getFeature(getBeginning());
                        }
                        else {
                            input[p][w] = this.getFeature(sentence[shift]);
                        }
                    }
                    else if (w > this.halfwindow) {
                        if (shift > length - 1) {
                            input[p][w] = this.getFeature(getEnding());
                        }
                        else {
                            input[p][w] = this.getFeature(sentence[shift]);
                        }
                    }
                }
            }
            for (int p = 0; p < length; ++p) {
                for (int f = 0; f < this.featureMap; ++f) {
                    for (int d = 0; d < this.featureDimension; ++d) {
                        for (int w2 = 0; w2 < this.windowSize; ++w2) {
                            final double[] array = convolution[p][f];
                            final int n = d;
                            array[n] += input[p][w2][d] * this.weightConvolution[f][d][w2];
                        }
                        final double[] array2 = convolution[p][f];
                        final int n2 = d;
                        array2[n2] += this.biasConvolution[f][d];
                        convolution[p][f][d] = this.sigmoid(convolution[p][f][d]);
                    }
                }
            }
            for (int p = 0; p < length; ++p) {
                for (int f = 0; f < this.featureMap; ++f) {
                    for (int d = 0; d < this.featureDimension; ++d) {
                        final double[] array3 = combine[p];
                        final int n3 = d;
                        array3[n3] += convolution[p][f][d];
                    }
                }
            }
            if (this.isAverage) {
                for (int p = 0; p < length; ++p) {
                    for (int d2 = 0; d2 < this.featureDimension; ++d2) {
                        combine[p][d2] /= this.featureMap;
                    }
                }
            }
            for (int p = 0; p < length; ++p) {
                for (int t = 0; t < this.tagset.length; ++t) {
                    for (int d = 0; d < this.featureDimension; ++d) {
                        final double[] array4 = output[p];
                        final int n4 = t;
                        array4[n4] += combine[p][d] * this.weightHO[t][d];
                    }
                    final double[] array5 = output[p];
                    final int n5 = t;
                    array5[n5] += this.biasOutput[t];
                }
            }
            optVal = new double[length][this.tagset.length];
            optRec = new int[length][this.tagset.length];
            for (int i = 0; i < length; ++i) {
                for (int j = 0; j < this.tagset.length; ++j) {
                    optVal[i][j] = -1.7976931348623157E308;
                    optRec[i][j] = 0;
                }
            }
            temp = 0.0;
            for (int p = 0; p < length; ++p) {
                for (int t = 0; t < this.tagset.length; ++t) {
                    if (p == 0) {
                        optVal[p][t] = output[p][t] + this.transition[t][this.tagset.length];
                    }
                    else {
                        for (int prev = 0; prev < this.tagset.length; ++prev) {
                            temp = optVal[p - 1][prev] + this.transition[prev][t] + output[p][t];
                            if (temp > optVal[p][t]) {
                                optVal[p][t] = temp;
                                optRec[p][t] = prev;
                            }
                        }
                    }
                }
            }
            optSequence = new String[length];
            optValue = -1.7976931348623157E308;
            int optTag = -1;
            for (int t = 0; t < this.tagset.length; ++t) {
                if (optVal[length - 1][t] > optValue) {
                    optValue = optVal[length - 1][t];
                    optTag = t;
                }
            }
            optSequence[length - 1] = this.tagset[optTag];
            for (int k = length - 1; k > 0; --k) {
                optTag = optRec[k][optTag];
                optSequence[k - 1] = this.tagset[optTag];
            }
            String taggingResult = "";
            if (this.isResultWithTag) {
                for (int l = 0; l < length - 1; ++l) {
                    taggingResult = String.valueOf(taggingResult) + sentence[l] + this.delimiter + optSequence[l] + " ";
                }
                taggingResult = String.valueOf(taggingResult) + sentence[length - 1] + this.delimiter + optSequence[length - 1];
            }
            else {
                for (int l = 0; l < length - 1; ++l) {
                    if (optSequence[l].startsWith("S") || optSequence[l].startsWith("E")) {
                        if (this.isSegmentation) {
                            taggingResult = String.valueOf(taggingResult) + sentence[l] + " ";
                        }
                        else {
                            taggingResult = String.valueOf(taggingResult) + sentence[l] + this.delimiter + optSequence[l].substring(2) + " ";
                        }
                    }
                    else {
                        taggingResult = String.valueOf(taggingResult) + sentence[l];
                    }
                }
                if (this.isSegmentation) {
                    taggingResult = String.valueOf(taggingResult) + sentence[length - 1];
                }
                else {
                    taggingResult = String.valueOf(taggingResult) + sentence[length - 1] + this.delimiter + optSequence[length - 1].substring(2);
                }
            }
            return taggingResult;
        }
        return "";
    }
    
    public String decodeSentence(String sent, final String tags) {
        int prevIndex = -1;
        final String[] labels = tags.split("\\s+");
        if (this.isCharacterLevel) {
            sent = sent.replaceAll(" ", "");
        }
        sent = sent.trim();
        if (!sent.equals("")) {
            String[] sentence = null;
            if (this.isStandard) {
                sent = ChineseNormalize.normalize(sent, false);
            }
            sentence = this.seperate(sentence, sent);
            final int length = sentence.length;
            if (labels.length != length) {
                System.out.println("The length of tags is not equal to that of sentence.");
                System.exit(0);
            }
            final double[][][] input = new double[length][this.windowSize][this.featureDimension];
            final double[][][] convolution = new double[length][this.featureMap][this.featureDimension];
            final double[][] combine = new double[length][this.featureDimension];
            final double[][] output = new double[length][this.tagset.length];
            double[][] optVal = null;
            int[][] optRec = null;
            double temp = 0.0;
            String[] optSequence = null;
            double optValue = -1.7976931348623157E308;
            for (int p = 0; p < length; ++p) {
                for (int w = 0; w < this.windowSize; ++w) {
                    final int shift = p - this.halfwindow + w;
                    if (w == this.halfwindow) {
                        input[p][w] = this.getFeature(sentence[shift]);
                    }
                    else if (w < this.halfwindow) {
                        if (shift < 0) {
                            input[p][w] = this.getFeature(getBeginning());
                        }
                        else {
                            input[p][w] = this.getFeature(sentence[shift]);
                        }
                    }
                    else if (w > this.halfwindow) {
                        if (shift > length - 1) {
                            input[p][w] = this.getFeature(getEnding());
                        }
                        else {
                            input[p][w] = this.getFeature(sentence[shift]);
                        }
                    }
                }
            }
            for (int p = 0; p < length; ++p) {
                for (int f = 0; f < this.featureMap; ++f) {
                    for (int d = 0; d < this.featureDimension; ++d) {
                        for (int w2 = 0; w2 < this.windowSize; ++w2) {
                            final double[] array = convolution[p][f];
                            final int n = d;
                            array[n] += input[p][w2][d] * this.weightConvolution[f][d][w2];
                        }
                        final double[] array2 = convolution[p][f];
                        final int n2 = d;
                        array2[n2] += this.biasConvolution[f][d];
                        convolution[p][f][d] = this.sigmoid(convolution[p][f][d]);
                    }
                }
            }
            for (int p = 0; p < length; ++p) {
                for (int f = 0; f < this.featureMap; ++f) {
                    for (int d = 0; d < this.featureDimension; ++d) {
                        final double[] array3 = combine[p];
                        final int n3 = d;
                        array3[n3] += convolution[p][f][d];
                    }
                }
            }
            if (this.isAverage) {
                for (int p = 0; p < length; ++p) {
                    for (int d2 = 0; d2 < this.featureDimension; ++d2) {
                        combine[p][d2] /= this.featureMap;
                    }
                }
            }
            for (int p = 0; p < length; ++p) {
                for (int t = 0; t < this.tagset.length; ++t) {
                    for (int d = 0; d < this.featureDimension; ++d) {
                        final double[] array4 = output[p];
                        final int n4 = t;
                        array4[n4] += combine[p][d] * this.weightHO[t][d];
                    }
                    final double[] array5 = output[p];
                    final int n5 = t;
                    array5[n5] += this.biasOutput[t];
                }
            }
            optVal = new double[length][this.tagset.length];
            optRec = new int[length][this.tagset.length];
            for (int i = 0; i < length; ++i) {
                for (int j = 0; j < this.tagset.length; ++j) {
                    optVal[i][j] = -1.7976931348623157E308;
                    optRec[i][j] = 0;
                }
            }
            temp = 0.0;
            for (int p = 0; p < length; ++p) {
                if (p == 0) {
                    for (int t = 0; t < this.tagset.length; ++t) {
                        optVal[p][t] = output[p][t] + this.transition[t][this.tagset.length];
                    }
                }
                else if (!labels[p - 1].equals(getOther())) {
                    prevIndex = this.tagIndex.get(labels[p - 1]);
                    for (int t = 0; t < this.tagset.length; ++t) {
                        optVal[p][t] = optVal[p - 1][prevIndex] + this.transition[prevIndex][t] + output[p][t];
                        optRec[p][t] = prevIndex;
                    }
                }
                else {
                    for (int t = 0; t < this.tagset.length; ++t) {
                        for (int prev = 0; prev < this.tagset.length; ++prev) {
                            temp = optVal[p - 1][prev] + this.transition[prev][t] + output[p][t];
                            if (temp > optVal[p][t]) {
                                optVal[p][t] = temp;
                                optRec[p][t] = prev;
                            }
                        }
                    }
                }
            }
            optSequence = new String[length];
            optValue = -1.7976931348623157E308;
            int optTag = -1;
            for (int t = 0; t < this.tagset.length; ++t) {
                if (optVal[length - 1][t] > optValue) {
                    optValue = optVal[length - 1][t];
                    optTag = t;
                }
            }
            optSequence[length - 1] = this.tagset[optTag];
            for (int k = length - 1; k > 0; --k) {
                optTag = optRec[k][optTag];
                optSequence[k - 1] = this.tagset[optTag];
            }
            String taggingResult = "";
            if (this.isResultWithTag) {
                for (int l = 0; l < length - 1; ++l) {
                    taggingResult = String.valueOf(taggingResult) + sentence[l] + this.delimiter + optSequence[l] + " ";
                }
                taggingResult = String.valueOf(taggingResult) + sentence[length - 1] + this.delimiter + optSequence[length - 1];
            }
            else {
                for (int l = 0; l < length - 1; ++l) {
                    if (optSequence[l].startsWith("S") || optSequence[l].startsWith("E")) {
                        if (this.isSegmentation) {
                            taggingResult = String.valueOf(taggingResult) + sentence[l] + " ";
                        }
                        else {
                            taggingResult = String.valueOf(taggingResult) + sentence[l] + this.delimiter + optSequence[l].substring(2) + " ";
                        }
                    }
                    else {
                        taggingResult = String.valueOf(taggingResult) + sentence[l];
                    }
                }
                if (this.isSegmentation) {
                    taggingResult = String.valueOf(taggingResult) + sentence[length - 1];
                }
                else {
                    taggingResult = String.valueOf(taggingResult) + sentence[length - 1] + this.delimiter + optSequence[length - 1].substring(2);
                }
            }
            return taggingResult;
        }
        return "";
    }
    
    private String[] seperate(String[] sentence, final String line) {
        if (this.isCharacterLevel) {
            final char[] chars = line.toCharArray();
            sentence = new String[chars.length];
            for (int p = 0; p < chars.length; ++p) {
                sentence[p] = String.valueOf(chars[p]);
            }
        }
        else {
            final String[] words = line.split("\\s+");
            sentence = new String[words.length];
            for (int p = 0; p < words.length; ++p) {
                sentence[p] = words[p];
            }
        }
        return sentence;
    }
    
    public double hardTanh(final double value) {
        if (value < -1.0) {
            return -1.0;
        }
        if (value > 1.0) {
            return 1.0;
        }
        return value;
    }
    
    public double sigmoid(final double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
    
    static String getOther() {
        return "O";
    }
    
    static String getBeginning() {
        return "\u2560";
    }
    
    static String getEnding() {
        return "\u2563";
    }
    
    public String getInputNetworkSettingFile() {
        return this.inputNetworkSettingFile;
    }
    
    public void setInputNetworkSettingFile(final String inputNetworkSettingFile) {
        this.inputNetworkSettingFile = inputNetworkSettingFile;
    }
    
    public String getOutputTaggingFile() {
        return this.outputTaggingFile;
    }
    
    public void setOutputTaggingFile(final String outputTaggingFile) {
        this.outputTaggingFile = outputTaggingFile;
    }
    
    public String getInputSentenceFile() {
        return this.inputSentenceFile;
    }
    
    public void setInputSentenceFile(final String inputSentenceFile) {
        this.inputSentenceFile = inputSentenceFile;
    }
    
    public boolean isCharacterLevel() {
        return this.isCharacterLevel;
    }
    
    public void setCharacterLevel(final boolean isCharacterLevel) {
        this.isCharacterLevel = isCharacterLevel;
    }
    
    public double[] getFeature(final String ch) {
        if (this.isIgnoreAlphabetNumber) {
            return this.getFeatureIgnoreAlphabetNumber(ch);
        }
        return this.getFeatureWithAlphabetNumber(ch);
    }
    
    public double[] getFeatureWithAlphabetNumber(final String ch) {
        if (ch.equals("A") || ch.equals("\uff21") || ch.equals("a") || ch.equals("\uff41")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("B") || ch.equals("\uff22") || ch.equals("b") || ch.equals("\uff42")) {
            return this.lookupTable.get("b");
        }
        if (ch.equals("C") || ch.equals("\uff23") || ch.equals("c") || ch.equals("\uff43")) {
            return this.lookupTable.get("c");
        }
        if (ch.equals("D") || ch.equals("\uff24") || ch.equals("d") || ch.equals("\uff44")) {
            return this.lookupTable.get("d");
        }
        if (ch.equals("E") || ch.equals("\uff25") || ch.equals("e") || ch.equals("\uff45")) {
            return this.lookupTable.get("e");
        }
        if (ch.equals("F") || ch.equals("\uff26") || ch.equals("f") || ch.equals("\uff46")) {
            return this.lookupTable.get("f");
        }
        if (ch.equals("G") || ch.equals("\uff27") || ch.equals("g") || ch.equals("\uff47")) {
            return this.lookupTable.get("g");
        }
        if (ch.equals("H") || ch.equals("\uff28") || ch.equals("h") || ch.equals("\uff48")) {
            return this.lookupTable.get("h");
        }
        if (ch.equals("I") || ch.equals("\uff29") || ch.equals("i") || ch.equals("\uff49")) {
            return this.lookupTable.get("i");
        }
        if (ch.equals("J") || ch.equals("\uff2a") || ch.equals("j") || ch.equals("\uff4a")) {
            return this.lookupTable.get("j");
        }
        if (ch.equals("K") || ch.equals("\uff2b") || ch.equals("k") || ch.equals("\uff4b")) {
            return this.lookupTable.get("k");
        }
        if (ch.equals("L") || ch.equals("\uff2c") || ch.equals("l") || ch.equals("\uff4c")) {
            return this.lookupTable.get("l");
        }
        if (ch.equals("M") || ch.equals("\uff2d") || ch.equals("m") || ch.equals("\uff4d")) {
            return this.lookupTable.get("m");
        }
        if (ch.equals("N") || ch.equals("\uff2e") || ch.equals("n") || ch.equals("\uff4e")) {
            return this.lookupTable.get("n");
        }
        if (ch.equals("O") || ch.equals("\uff2f") || ch.equals("o") || ch.equals("\uff4f")) {
            return this.lookupTable.get("o");
        }
        if (ch.equals("P") || ch.equals("\uff30") || ch.equals("p") || ch.equals("\uff50")) {
            return this.lookupTable.get("p");
        }
        if (ch.equals("Q") || ch.equals("\uff31") || ch.equals("q") || ch.equals("\uff51")) {
            return this.lookupTable.get("q");
        }
        if (ch.equals("R") || ch.equals("\uff32") || ch.equals("r") || ch.equals("\uff52")) {
            return this.lookupTable.get("r");
        }
        if (ch.equals("S") || ch.equals("\uff33") || ch.equals("s") || ch.equals("\uff53")) {
            return this.lookupTable.get("s");
        }
        if (ch.equals("T") || ch.equals("\uff34") || ch.equals("t") || ch.equals("\uff54")) {
            return this.lookupTable.get("t");
        }
        if (ch.equals("U") || ch.equals("\uff35") || ch.equals("u") || ch.equals("\uff55")) {
            return this.lookupTable.get("u");
        }
        if (ch.equals("V") || ch.equals("\uff36") || ch.equals("v") || ch.equals("\uff56")) {
            return this.lookupTable.get("v");
        }
        if (ch.equals("W") || ch.equals("\uff37") || ch.equals("w") || ch.equals("\uff57")) {
            return this.lookupTable.get("w");
        }
        if (ch.equals("X") || ch.equals("\uff38") || ch.equals("x") || ch.equals("\uff58")) {
            return this.lookupTable.get("x");
        }
        if (ch.equals("Y") || ch.equals("\uff39") || ch.equals("y") || ch.equals("\uff59")) {
            return this.lookupTable.get("y");
        }
        if (ch.equals("Z") || ch.equals("\uff3a") || ch.equals("z") || ch.equals("\uff5a")) {
            return this.lookupTable.get("z");
        }
        if (ch.equals("0") || ch.equals("\uff10")) {
            return this.lookupTable.get("0");
        }
        if (ch.equals("1") || ch.equals("\uff11")) {
            return this.lookupTable.get("1");
        }
        if (ch.equals("2") || ch.equals("\uff12")) {
            return this.lookupTable.get("2");
        }
        if (ch.equals("3") || ch.equals("\uff13")) {
            return this.lookupTable.get("3");
        }
        if (ch.equals("4") || ch.equals("\uff14")) {
            return this.lookupTable.get("4");
        }
        if (ch.equals("5") || ch.equals("\uff15")) {
            return this.lookupTable.get("5");
        }
        if (ch.equals("6") || ch.equals("\uff16")) {
            return this.lookupTable.get("6");
        }
        if (ch.equals("7") || ch.equals("\uff17")) {
            return this.lookupTable.get("7");
        }
        if (ch.equals("8") || ch.equals("\uff18")) {
            return this.lookupTable.get("8");
        }
        if (ch.equals("9") || ch.equals("\uff19")) {
            return this.lookupTable.get("9");
        }
        if (this.lookupTable.containsKey(ch)) {
            return this.lookupTable.get(ch);
        }
        return this.lookupTable.get("\u25a1");
    }
    
    public double[] getFeatureIgnoreAlphabetNumber(final String ch) {
        if (ch.equals("A") || ch.equals("\uff21") || ch.equals("a") || ch.equals("\uff41")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("B") || ch.equals("\uff22") || ch.equals("b") || ch.equals("\uff42")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("C") || ch.equals("\uff23") || ch.equals("c") || ch.equals("\uff43")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("D") || ch.equals("\uff24") || ch.equals("d") || ch.equals("\uff44")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("E") || ch.equals("\uff25") || ch.equals("e") || ch.equals("\uff45")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("F") || ch.equals("\uff26") || ch.equals("f") || ch.equals("\uff46")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("G") || ch.equals("\uff27") || ch.equals("g") || ch.equals("\uff47")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("H") || ch.equals("\uff28") || ch.equals("h") || ch.equals("\uff48")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("I") || ch.equals("\uff29") || ch.equals("i") || ch.equals("\uff49")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("J") || ch.equals("\uff2a") || ch.equals("j") || ch.equals("\uff4a")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("K") || ch.equals("\uff2b") || ch.equals("k") || ch.equals("\uff4b")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("L") || ch.equals("\uff2c") || ch.equals("l") || ch.equals("\uff4c")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("M") || ch.equals("\uff2d") || ch.equals("m") || ch.equals("\uff4d")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("N") || ch.equals("\uff2e") || ch.equals("n") || ch.equals("\uff4e")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("O") || ch.equals("\uff2f") || ch.equals("o") || ch.equals("\uff4f")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("P") || ch.equals("\uff30") || ch.equals("p") || ch.equals("\uff50")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("Q") || ch.equals("\uff31") || ch.equals("q") || ch.equals("\uff51")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("R") || ch.equals("\uff32") || ch.equals("r") || ch.equals("\uff52")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("S") || ch.equals("\uff33") || ch.equals("s") || ch.equals("\uff53")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("T") || ch.equals("\uff34") || ch.equals("t") || ch.equals("\uff54")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("U") || ch.equals("\uff35") || ch.equals("u") || ch.equals("\uff55")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("V") || ch.equals("\uff36") || ch.equals("v") || ch.equals("\uff56")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("W") || ch.equals("\uff37") || ch.equals("w") || ch.equals("\uff57")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("X") || ch.equals("\uff38") || ch.equals("x") || ch.equals("\uff58")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("Y") || ch.equals("\uff39") || ch.equals("y") || ch.equals("\uff59")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("Z") || ch.equals("\uff3a") || ch.equals("z") || ch.equals("\uff5a")) {
            return this.lookupTable.get("a");
        }
        if (ch.equals("0") || ch.equals("\uff10")) {
            return this.lookupTable.get("0");
        }
        if (ch.equals("1") || ch.equals("\uff11")) {
            return this.lookupTable.get("0");
        }
        if (ch.equals("2") || ch.equals("\uff12")) {
            return this.lookupTable.get("0");
        }
        if (ch.equals("3") || ch.equals("\uff13")) {
            return this.lookupTable.get("0");
        }
        if (ch.equals("4") || ch.equals("\uff14")) {
            return this.lookupTable.get("0");
        }
        if (ch.equals("5") || ch.equals("\uff15")) {
            return this.lookupTable.get("0");
        }
        if (ch.equals("6") || ch.equals("\uff16")) {
            return this.lookupTable.get("0");
        }
        if (ch.equals("7") || ch.equals("\uff17")) {
            return this.lookupTable.get("0");
        }
        if (ch.equals("8") || ch.equals("\uff18")) {
            return this.lookupTable.get("0");
        }
        if (ch.equals("9") || ch.equals("\uff19")) {
            return this.lookupTable.get("0");
        }
        if (this.lookupTable.containsKey(ch)) {
            return this.lookupTable.get(ch);
        }
        return this.lookupTable.get("\u25a1");
    }
    
    public boolean isResultWithTag() {
        return this.isResultWithTag;
    }
    
    public void setResultWithTag(final boolean isResultWithTag) {
        this.isResultWithTag = isResultWithTag;
    }
    
    public void setSegmentation(final boolean isSegmentation) {
        this.isSegmentation = isSegmentation;
    }
    
    public boolean isStandard() {
        return this.isStandard;
    }
    
    public void setStandard(final boolean isStandard) {
        this.isStandard = isStandard;
    }
}
