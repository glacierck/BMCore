// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.sentence;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ConvolutionalSentenceModelDecoder
{
    private String inputNetworkSettingFile;
    private int internalFeatureDimension;
    private int externalFeatureDimension;
    private int featureDimension;
    private int numberOfLayer;
    private int numberOfTopK;
    private int[] windowSize;
    private int[] featureMap;
    private boolean isIgnoreAlphabetNumber;
    private boolean isSigmoid;
    private HashMap<String, double[]> lookupTable;
    private ArrayList<double[][][]> weightConvolution;
    private ArrayList<double[][]> biasConvolution;
    private double[][] weightHO;
    private double[] biasOutput;
    private String[] tagset;
    private HashMap<String, Integer> tagIndex;
    String type;
    int offset;
    double[][] input;
    double[][][] convolution;
    double[][][] kMax;
    int[][][] kMaxIndex;
    double[] topOutput;
    double[] output;
    double[][][] weight;
    double[][] bias;
    int[] dynamicK;
    int[] dimension;
    int numberOfTopOutput;
    String[] sample;
    int length;
    int halfwindow;
    int shift;
    double optVal;
    int optIndex;
    
    public ConvolutionalSentenceModelDecoder(final String inputNetworkSettingFile) {
        this.inputNetworkSettingFile = null;
        this.internalFeatureDimension = -1;
        this.externalFeatureDimension = -1;
        this.featureDimension = -1;
        this.numberOfLayer = 2;
        this.numberOfTopK = 3;
        this.windowSize = null;
        this.featureMap = null;
        this.isIgnoreAlphabetNumber = true;
        this.isSigmoid = false;
        this.lookupTable = null;
        this.weightConvolution = null;
        this.biasConvolution = null;
        this.tagIndex = null;
        this.type = null;
        this.offset = 0;
        this.input = null;
        this.convolution = null;
        this.kMax = null;
        this.kMaxIndex = null;
        this.topOutput = null;
        this.output = null;
        this.weight = null;
        this.bias = null;
        this.dynamicK = null;
        this.dimension = null;
        this.numberOfTopOutput = 0;
        this.sample = null;
        this.length = 0;
        this.halfwindow = 0;
        this.shift = 0;
        this.optVal = 0.0;
        this.optIndex = 0;
        this.inputNetworkSettingFile = inputNetworkSettingFile;
    }
    
    public void readPara() {
        try {
            final FileInputStream fis = new FileInputStream(this.inputNetworkSettingFile);
            final ObjectInputStream ois = new ObjectInputStream(fis);
            final ConvolutionalSentenceModelSetting networkSetting = (ConvolutionalSentenceModelSetting)ois.readObject();
            this.internalFeatureDimension = networkSetting.getInternalFeatureDimension();
            this.externalFeatureDimension = networkSetting.getExternalFeatureDimension();
            this.featureDimension = networkSetting.getFeatureDimension();
            this.numberOfLayer = networkSetting.getNumberOfLayer();
            this.numberOfTopK = networkSetting.getNumberOfTopK();
            this.windowSize = networkSetting.getWindowSize();
            this.featureMap = networkSetting.getFeatureMap();
            this.isIgnoreAlphabetNumber = networkSetting.isIgnoreAlphabetNumber();
            this.lookupTable = networkSetting.getLookupTable();
            this.weightConvolution = networkSetting.getWeightConvolution();
            this.biasConvolution = networkSetting.getBiasConvolution();
            this.weightHO = networkSetting.getWeightHO();
            this.biasOutput = networkSetting.getBiasOutput();
            this.tagset = networkSetting.getTagset();
            ois.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.tagIndex = new HashMap<String, Integer>();
        for (int i = 0; i < this.tagset.length; ++i) {
            this.tagIndex.put(this.tagset[i], i);
        }
        this.dimension = new int[this.numberOfLayer];
        if (this.featureDimension % 2 != 0) {
            System.out.println("The feature dimension should better be the power of 2 such as 32, 64, 128 ... ");
            System.exit(0);
        }
        this.dimension[0] = this.featureDimension / 2;
        for (int L = 1; L < this.numberOfLayer; ++L) {
            if (this.dimension[L - 1] % 2 == 0) {
                this.dimension[L] = this.dimension[L - 1] / 2;
            }
            else {
                System.out.println("The feature dimension should better be the power of 2 such as 32, 64, 128 ... ");
                System.exit(0);
            }
        }
        this.numberOfTopOutput = this.numberOfTopK * this.dimension[this.numberOfLayer - 1] * this.featureMap[this.numberOfLayer - 1];
    }
    
    public String decodeSentence(String sentence) {
        sentence = sentence.trim();
        this.sample = sentence.split("\\s+");
        this.length = this.sample.length;
        this.input = new double[this.length][this.featureDimension];
        for (int p = 0; p < this.length; ++p) {
            this.input[p] = this.getFeature(this.sample[p]);
        }
        this.dynamicK = new int[this.numberOfLayer];
        for (int L = 0; L < this.numberOfLayer; ++L) {
            this.weight = this.weightConvolution.get(L);
            this.bias = this.biasConvolution.get(L);
            this.halfwindow = this.windowSize[L] / 2;
            if (L == 0) {
                this.convolution = new double[this.featureMap[L]][this.length][this.dimension[L]];
                for (int f = 0; f < this.featureMap[L]; ++f) {
                    for (int p2 = 0; p2 < this.length; ++p2) {
                        for (int d = 0; d < this.featureDimension; ++d) {
                            for (int w = 0; w < this.windowSize[L]; ++w) {
                                this.offset = p2 + w - this.halfwindow;
                                if (this.offset >= 0 && this.offset < this.length) {
                                    final double[] array = this.convolution[f][p2];
                                    final int n = d / 2;
                                    array[n] += this.input[this.offset][d] * this.weight[f][d][w];
                                }
                            }
                            final double[] array2 = this.convolution[f][p2];
                            final int n2 = d / 2;
                            array2[n2] += this.bias[f][d];
                        }
                    }
                }
            }
            else {
                this.convolution = new double[this.featureMap[L]][this.dynamicK[L - 1]][this.dimension[L]];
                for (int f = 0; f < this.featureMap[L]; ++f) {
                    for (int p2 = 0; p2 < this.dynamicK[L - 1]; ++p2) {
                        for (int d = 0; d < this.dimension[L - 1]; ++d) {
                            for (int m = 0; m < this.featureMap[L - 1]; ++m) {
                                for (int w2 = 0; w2 < this.windowSize[L]; ++w2) {
                                    this.offset = p2 + w2 - this.halfwindow;
                                    if (this.offset >= 0 && this.offset < this.dynamicK[L - 1]) {
                                        final double[] array3 = this.convolution[f][p2];
                                        final int n3 = d / 2;
                                        array3[n3] += this.kMax[m][this.offset][d] * this.weight[f][d][w2];
                                    }
                                }
                                final double[] array4 = this.convolution[f][p2];
                                final int n4 = d / 2;
                                array4[n4] += this.bias[f][d];
                            }
                        }
                    }
                }
            }
            if (L == this.numberOfLayer - 1) {
                this.dynamicK[L] = this.numberOfTopK;
            }
            else {
                this.dynamicK[L] = Math.max(this.numberOfTopK, this.length * (this.numberOfLayer - (L + 1)) / this.numberOfLayer);
            }
            this.kMax = new double[this.featureMap[L]][this.dynamicK[L]][this.dimension[L]];
            this.kMaxIndex = new int[this.featureMap[L]][this.dynamicK[L]][this.dimension[L]];
            if (L == 0) {
                this.kMaxPooling(this.convolution, this.kMax, this.kMaxIndex, this.featureMap[L], this.dynamicK[L], this.dimension[L], this.length);
            }
            else {
                this.kMaxPooling(this.convolution, this.kMax, this.kMaxIndex, this.featureMap[L], this.dynamicK[L], this.dimension[L], this.dynamicK[L - 1]);
            }
            for (int f = 0; f < this.featureMap[L]; ++f) {
                for (int d2 = 0; d2 < this.dimension[L]; ++d2) {
                    for (int k = 0; k < this.dynamicK[L]; ++k) {
                        if (this.isSigmoid) {
                            this.kMax[f][k][d2] = this.sigmoid(this.kMax[f][k][d2]);
                        }
                        else {
                            this.kMax[f][k][d2] = this.hardTanh(this.kMax[f][k][d2]);
                        }
                    }
                }
            }
        }
        this.shift = 0;
        this.topOutput = new double[this.numberOfTopOutput];
        for (int f2 = 0; f2 < this.featureMap[this.numberOfLayer - 1]; ++f2) {
            for (int d3 = 0; d3 < this.dimension[this.numberOfLayer - 1]; ++d3) {
                for (int i = 0; i < this.numberOfTopK; ++i) {
                    this.topOutput[this.shift] = this.kMax[f2][i][d3];
                    ++this.shift;
                }
            }
        }
        this.output = new double[this.tagset.length];
        for (int d4 = 0; d4 < this.numberOfTopOutput; ++d4) {
            final double[] output = this.output;
            final int n5 = 0;
            output[n5] += this.topOutput[d4] * this.weightHO[0][d4];
        }
        final double[] output2 = this.output;
        final int n6 = 0;
        output2[n6] += this.biasOutput[0];
        this.optVal = this.output[0];
        this.optIndex = 0;
        for (int t = 1; t < this.tagset.length; ++t) {
            for (int d3 = 0; d3 < this.numberOfTopOutput; ++d3) {
                final double[] output3 = this.output;
                final int n7 = t;
                output3[n7] += this.topOutput[d3] * this.weightHO[t][d3];
            }
            final double[] output4 = this.output;
            final int n8 = t;
            output4[n8] += this.biasOutput[t];
            if (this.output[t] > this.optVal) {
                this.optVal = this.output[t];
                this.optIndex = t;
            }
        }
        return this.type = this.tagset[this.optIndex];
    }
    
    private void kMaxPooling(final double[][][] convolution, final double[][][] kMax, final int[][][] kMaxIndex, final int featureMap, final int dynamicK, final int dimension, final int prevDynamicK) {
        int index = 0;
        double tempValue = 0.0;
        int tempIndex = 0;
        for (int f = 0; f < featureMap; ++f) {
            for (int d = 0; d < dimension; ++d) {
                for (int k = 0; k < prevDynamicK; ++k) {
                    if (k < dynamicK) {
                        kMax[f][k][d] = convolution[f][k][d];
                        kMaxIndex[f][k][d] = k;
                        for (index = k; index > 0; --index) {
                            if (kMax[f][index][d] <= kMax[f][index - 1][d]) {
                                break;
                            }
                            tempValue = kMax[f][index - 1][d];
                            kMax[f][index - 1][d] = kMax[f][index][d];
                            kMax[f][index][d] = tempValue;
                            tempIndex = kMaxIndex[f][index - 1][d];
                            kMaxIndex[f][index - 1][d] = kMaxIndex[f][index][d];
                            kMaxIndex[f][index][d] = tempIndex;
                        }
                    }
                    else if (convolution[f][k][d] > kMax[f][dynamicK - 1][d]) {
                        kMax[f][dynamicK - 1][d] = convolution[f][k][d];
                        kMaxIndex[f][dynamicK - 1][d] = k;
                        for (index = dynamicK - 1; index > 0 && kMax[f][index][d] > kMax[f][index - 1][d]; --index) {
                            tempValue = kMax[f][index - 1][d];
                            kMax[f][index - 1][d] = kMax[f][index][d];
                            kMax[f][index][d] = tempValue;
                            tempIndex = kMaxIndex[f][index - 1][d];
                            kMaxIndex[f][index - 1][d] = kMaxIndex[f][index][d];
                            kMaxIndex[f][index][d] = tempIndex;
                        }
                    }
                }
            }
        }
        for (int f = 0; f < featureMap; ++f) {
            for (int d = 0; d < dimension; ++d) {
                for (int k = 0; k < dynamicK; ++k) {
                    for (index = k; index > 0 && kMaxIndex[f][index][d] < kMaxIndex[f][index - 1][d]; --index) {
                        tempValue = kMax[f][index - 1][d];
                        kMax[f][index - 1][d] = kMax[f][index][d];
                        kMax[f][index][d] = tempValue;
                        tempIndex = kMaxIndex[f][index - 1][d];
                        kMaxIndex[f][index - 1][d] = kMaxIndex[f][index][d];
                        kMaxIndex[f][index][d] = tempIndex;
                    }
                }
            }
        }
    }
    
    static String getBeginning() {
        return "\u2560";
    }
    
    static String getEnding() {
        return "\u2563";
    }
    
    static String getUnknown() {
        return "\u25a1";
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
    
    public int getInternalFeatureDimension() {
        return this.internalFeatureDimension;
    }
    
    public int getExternalFeatureDimension() {
        return this.externalFeatureDimension;
    }
}
