// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.sentence;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ConvolutionalSentenceModel
{
    private String corpusFile;
    private String tokenFile;
    private String labelFile;
    private boolean isReadEmbedding;
    private String embeddingFile;
    private boolean isExternalFeature;
    private String externalFeatureFile;
    private boolean isReadNetworkSetting;
    private String inputNetworkSettingFile;
    private String outputNetworkSettingFile;
    private String echoFile;
    private int internalFeatureDimension;
    private int externalFeatureDimension;
    private int featureDimension;
    private int numberOfLayer;
    private int numberOfTopK;
    private int[] windowSize;
    private int[] featureMap;
    private int[] dynamicK;
    private int[] dimension;
    private long learningTimes;
    private double learningRate;
    private double beginRate;
    private double minLearningRate;
    private double regularizationRate;
    private double errorLimit;
    private boolean isIgnoreAlphabetNumber;
    private boolean isShrink;
    private boolean isSigmoid;
    private HashMap<String, double[]> lookupTable;
    private ArrayList<double[][][]> weightConvolution;
    private ArrayList<double[][]> biasConvolution;
    private double[][] weightHO;
    private double[] biasOutput;
    private String[] tagset;
    private int numberOfTopOutput;
    private double constant;
    private double totalError;
    private double shrinkRate;
    private HashMap<String, Integer> tagIndex;
    private boolean isDebug;
    
    public ConvolutionalSentenceModel() {
        this.corpusFile = null;
        this.tokenFile = null;
        this.labelFile = null;
        this.isReadEmbedding = false;
        this.embeddingFile = null;
        this.isExternalFeature = false;
        this.externalFeatureFile = null;
        this.isReadNetworkSetting = false;
        this.inputNetworkSettingFile = null;
        this.outputNetworkSettingFile = null;
        this.echoFile = null;
        this.internalFeatureDimension = 100;
        this.externalFeatureDimension = 0;
        this.featureDimension = 100;
        this.numberOfLayer = 2;
        this.numberOfTopK = 3;
        this.windowSize = null;
        this.featureMap = null;
        this.dynamicK = null;
        this.dimension = null;
        this.learningTimes = 100L;
        this.learningRate = 0.05;
        this.beginRate = 0.05;
        this.minLearningRate = 0.01;
        this.regularizationRate = 1.0E-4;
        this.errorLimit = 0.005;
        this.isIgnoreAlphabetNumber = true;
        this.isShrink = true;
        this.isSigmoid = false;
        this.lookupTable = new HashMap<String, double[]>();
        this.weightConvolution = null;
        this.biasConvolution = null;
        this.numberOfTopOutput = 0;
        this.constant = 2.38;
        this.totalError = 1.0;
        this.shrinkRate = 1.0;
        this.tagIndex = new HashMap<String, Integer>();
        this.isDebug = false;
    }
    
    public void readFiles() {
        try {
            final FileInputStream fis = new FileInputStream(this.labelFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            String line = null;
            String tags = "";
            int tagnum = 0;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll(" ", "");
                line = line.trim();
                if (!line.equals("") && !line.equals(" ")) {
                    ++tagnum;
                    tags = String.valueOf(tags) + line + " ";
                }
            }
            this.tagset = new String[tagnum];
            final String[] tokens = tags.split("\\s+");
            for (int i = 0; i < this.tagset.length; ++i) {
                this.tagset[i] = tokens[i];
                this.tagIndex.put(this.tagset[i], i);
            }
            line = null;
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void initPara() {
        if (this.featureDimension != this.internalFeatureDimension + this.externalFeatureDimension) {
            System.out.println("Check the dimension values that should be equal to the sum of internalFeatureDimension and externalFeatureDimension.");
            System.exit(0);
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
        if (!this.isReadNetworkSetting) {
            final Random randomgen = new Random();
            if (this.isReadEmbedding) {
                try {
                    final FileInputStream fis = new FileInputStream(this.embeddingFile);
                    final ObjectInputStream ois = new ObjectInputStream(fis);
                    this.lookupTable = (HashMap<String, double[]>)ois.readObject();
                    ois.close();
                    fis.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                final double[] feature = this.lookupTable.get(getBeginning());
                if (this.featureDimension != feature.length) {
                    System.out.println("Check the embedding file. The dimension used in the embedding file is not equal to the value of featureDimension.");
                    System.exit(0);
                }
            }
            else if (!this.isExternalFeature) {
                try {
                    final FileInputStream fis = new FileInputStream(this.tokenFile);
                    final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                    final BufferedReader br = new BufferedReader(isr);
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        line = line.replaceAll(" ", "");
                        line = line.trim();
                        if (!line.equals("") && !line.equals(" ")) {
                            final double[] feature2 = new double[this.internalFeatureDimension];
                            for (int i = 0; i < this.internalFeatureDimension; ++i) {
                                feature2[i] = (randomgen.nextDouble() - 0.5) * 2.0;
                            }
                            this.lookupTable.put(line, feature2);
                        }
                    }
                    if (!this.lookupTable.containsKey(getUnknown())) {
                        final double[] feature2 = new double[this.internalFeatureDimension];
                        for (int i = 0; i < this.internalFeatureDimension; ++i) {
                            feature2[i] = (randomgen.nextDouble() - 0.5) * 2.0;
                        }
                        this.lookupTable.put(getUnknown(), feature2);
                    }
                    if (!this.lookupTable.containsKey("a")) {
                        final double[] feature2 = new double[this.internalFeatureDimension];
                        for (int i = 0; i < this.internalFeatureDimension; ++i) {
                            feature2[i] = (randomgen.nextDouble() - 0.5) * 2.0;
                        }
                        this.lookupTable.put("a", feature2);
                    }
                    if (!this.lookupTable.containsKey("0")) {
                        final double[] feature2 = new double[this.internalFeatureDimension];
                        for (int i = 0; i < this.internalFeatureDimension; ++i) {
                            feature2[i] = (randomgen.nextDouble() - 0.5) * 2.0;
                        }
                        this.lookupTable.put("0", feature2);
                    }
                    br.close();
                    isr.close();
                    fis.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    final FileInputStream fis = new FileInputStream(this.externalFeatureFile);
                    final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                    final BufferedReader br = new BufferedReader(isr);
                    String line = null;
                    String[] tokens = null;
                    while ((line = br.readLine()) != null) {
                        tokens = line.split("\\s+");
                        if (tokens.length > 0 && !tokens[0].equals("")) {
                            if (tokens.length != this.externalFeatureDimension + 1) {
                                System.out.println("Check the external feature file. The dimension used in the externalFeatureFile file is not equal to the value of externalFeatureDimension.");
                                System.exit(0);
                            }
                            final double[] feature3 = new double[this.featureDimension];
                            for (int j = 0; j < this.featureDimension; ++j) {
                                if (j < this.internalFeatureDimension) {
                                    feature3[j] = (randomgen.nextDouble() - 0.5) * 2.0 / this.internalFeatureDimension;
                                }
                                else {
                                    feature3[j] = Double.parseDouble(tokens[j - this.internalFeatureDimension + 1]);
                                }
                            }
                            this.lookupTable.put(tokens[0], feature3);
                        }
                    }
                    br.close();
                    isr.close();
                    fis.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            double denominator = 0.0;
            double[][][] weight = null;
            double[][] bias = null;
            this.weightConvolution = new ArrayList<double[][][]>();
            this.biasConvolution = new ArrayList<double[][]>();
            for (int L2 = 0; L2 < this.numberOfLayer; ++L2) {
                denominator = Math.sqrt(this.windowSize[L2]);
                weight = new double[this.featureMap[L2]][this.dimension[L2] * 2][this.windowSize[L2]];
                bias = new double[this.featureMap[L2]][this.dimension[L2] * 2];
                for (int f = 0; f < this.featureMap[L2]; ++f) {
                    for (int d = 0; d < this.dimension[L2] * 2; ++d) {
                        bias[f][d] = (randomgen.nextDouble() - 0.5) * 2.0 / denominator - 0.1;
                        for (int w = 0; w < this.windowSize[L2]; ++w) {
                            weight[f][d][w] = (randomgen.nextDouble() - 0.5) / denominator;
                        }
                    }
                }
                this.weightConvolution.add(weight);
                this.biasConvolution.add(bias);
            }
            denominator = Math.sqrt(this.numberOfTopOutput);
            this.weightHO = new double[this.tagset.length][this.numberOfTopOutput];
            for (int t = 0; t < this.tagset.length; ++t) {
                for (int d2 = 0; d2 < this.numberOfTopOutput; ++d2) {
                    this.weightHO[t][d2] = (randomgen.nextDouble() - 0.5) * this.constant / denominator;
                }
            }
            this.biasOutput = new double[this.tagset.length];
            for (int t = 0; t < this.tagset.length; ++t) {
                this.biasOutput[t] = (randomgen.nextDouble() - 0.5) / denominator;
            }
        }
        else {
            try {
                final FileInputStream fis2 = new FileInputStream(this.inputNetworkSettingFile);
                final ObjectInputStream ois2 = new ObjectInputStream(fis2);
                final ConvolutionalSentenceModelSetting networkSetting = (ConvolutionalSentenceModelSetting)ois2.readObject();
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
                ois2.close();
                fis2.close();
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            for (int k = 0; k < this.tagset.length; ++k) {
                this.tagIndex.put(this.tagset[k], k);
            }
        }
        this.beginRate = this.learningRate;
    }
    
    public void learning() {
        try {
            final FileOutputStream fos = new FileOutputStream(this.echoFile);
            final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write("Network setting: \r\n");
            osw.write("  corpusFile: " + this.corpusFile + "\r\n");
            osw.write("  tokenFile: " + this.tokenFile + "\r\n");
            osw.write("  labelFile: " + this.labelFile + "\r\n");
            osw.write("  isReadEmbedding: " + this.isReadEmbedding + "\r\n");
            osw.write("  embeddingFile: " + this.embeddingFile + "\r\n");
            osw.write("  isExternalFeature: " + this.isExternalFeature + "\r\n");
            osw.write("  externalFeatureFile: " + this.externalFeatureFile + "\r\n");
            osw.write("  isReadNetworkSetting: " + this.isReadNetworkSetting + "\r\n");
            osw.write("  inputNetworkSettingFile: " + this.inputNetworkSettingFile + "\r\n");
            osw.write("  outputNetworkSettingFile: " + this.outputNetworkSettingFile + "\r\n");
            osw.write("  echoFile: " + this.echoFile + "\r\n");
            osw.write("  featureDimension: " + this.featureDimension + "\r\n");
            osw.write("  internalFeatureDimension: " + this.internalFeatureDimension + "\r\n");
            osw.write("  externalFeatureDimension: " + this.externalFeatureDimension + "\r\n");
            osw.write("  numberOfLayer: " + this.numberOfLayer + "\r\n");
            osw.write("  numberOfTopK: " + this.numberOfTopK + "\r\n");
            osw.write("  windowSize: " + this.windowSize[0]);
            for (int L = 1; L < this.numberOfLayer; ++L) {
                osw.write("/" + this.windowSize[L]);
            }
            osw.write("\r\n");
            osw.write("  featureMap: " + this.featureMap[0]);
            for (int L = 1; L < this.numberOfLayer; ++L) {
                osw.write("/" + this.featureMap[L]);
            }
            osw.write("\r\n");
            osw.write("  isIgnoreAlphabetNumber: " + this.isIgnoreAlphabetNumber + "\r\n");
            osw.write("  learningRate: " + this.learningRate + "\r\n");
            osw.write("  regularizationRate: " + this.regularizationRate + "\r\n");
            osw.write("  errorLimit: " + this.errorLimit + "\r\n");
            osw.write("  learningTimes: " + this.learningTimes + "\r\n");
            osw.write("  The size of tagset: " + this.tagset.length + "\r\n");
            osw.write("  The size of the lookup table: " + this.lookupTable.size() + "\r\n");
            System.out.println("  The size of tagset: " + this.tagset.length);
            System.out.println("  The size of the lookup table: " + this.lookupTable.size());
            int times = 0;
            int offset = 0;
            double[][] input = null;
            final ArrayList<double[][][]> convolutions = new ArrayList<double[][][]>();
            final ArrayList<double[][][]> kMaxes = new ArrayList<double[][][]>();
            final ArrayList<int[][][]> kMaxIndexes = new ArrayList<int[][][]>();
            double[][][] convolution = null;
            double[][][] kMax = null;
            int[][][] kMaxIndex = null;
            double[] topOutput = null;
            double[] output = null;
            double[][][] weight = null;
            double[][] bias = null;
            double[] derivOutput = null;
            double[][] derivWeightHO = null;
            double[] deriveTopOutput = null;
            double[][][] derivConvolution = null;
            double[][][] derivKMax = null;
            double[][] derivBiasConvolution = null;
            double[][][] derivWeightConvolution = null;
            double[][] derivInput = null;
            String[] sample = null;
            String line = null;
            String type = null;
            int length = 0;
            int index = -1;
            int halfwindow = 0;
            int shift = 0;
            double optVal = 0.0;
            int optIndex = 0;
            double exp = 0.0;
            while (times <= this.learningTimes && this.totalError > this.errorLimit) {
                int totalCount = 0;
                int correctCount = 0;
                osw.write("\r\n");
                osw.write("The current learning time " + times + " (Sentence model with dynamic-k pooling layer.): " + "\r\n");
                System.out.print("\r\n");
                System.out.println("The current learning time: " + times);
                try {
                    final FileInputStream fis = new FileInputStream(this.corpusFile);
                    final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                    final BufferedReader br = new BufferedReader(isr);
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (!line.equals("") && !line.equals(" ")) {
                            ++totalCount;
                            convolutions.clear();
                            kMaxes.clear();
                            kMaxIndexes.clear();
                            index = line.indexOf(" ");
                            if (index != -1) {
                                type = line.substring(0, index);
                                sample = line.substring(index + 1).split("\\s+");
                                length = sample.length;
                                input = new double[length][this.featureDimension];
                                for (int p = 0; p < length; ++p) {
                                    input[p] = this.getFeature(sample[p]);
                                }
                                this.dynamicK = new int[this.numberOfLayer];
                                for (int L2 = 0; L2 < this.numberOfLayer; ++L2) {
                                    weight = this.weightConvolution.get(L2);
                                    bias = this.biasConvolution.get(L2);
                                    halfwindow = this.windowSize[L2] / 2;
                                    if (L2 == 0) {
                                        convolution = new double[this.featureMap[L2]][length][this.dimension[L2]];
                                        for (int f = 0; f < this.featureMap[L2]; ++f) {
                                            for (int p2 = 0; p2 < length; ++p2) {
                                                for (int d = 0; d < this.featureDimension; ++d) {
                                                    for (int w = 0; w < this.windowSize[L2]; ++w) {
                                                        offset = p2 + w - halfwindow;
                                                        if (offset >= 0 && offset < length) {
                                                            final double[] array = convolution[f][p2];
                                                            final int n = d / 2;
                                                            array[n] += input[offset][d] * weight[f][d][w];
                                                        }
                                                    }
                                                    final double[] array2 = convolution[f][p2];
                                                    final int n2 = d / 2;
                                                    array2[n2] += bias[f][d];
                                                }
                                            }
                                        }
                                        if (this.isDebug) {
                                            for (int f = 0; f < this.featureMap[L2]; ++f) {
                                                for (int p2 = 0; p2 < length; ++p2) {
                                                    for (int d = 0; d < this.dimension[L2]; ++d) {
                                                        System.out.println(String.valueOf(this.dimension[L2]) + " (" + f + "/" + p2 + "/" + d + "): " + convolution[f][p2][d]);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else {
                                        convolution = new double[this.featureMap[L2]][this.dynamicK[L2 - 1]][this.dimension[L2]];
                                        for (int f = 0; f < this.featureMap[L2]; ++f) {
                                            for (int p2 = 0; p2 < this.dynamicK[L2 - 1]; ++p2) {
                                                for (int d = 0; d < this.dimension[L2 - 1]; ++d) {
                                                    for (int m = 0; m < this.featureMap[L2 - 1]; ++m) {
                                                        for (int w2 = 0; w2 < this.windowSize[L2]; ++w2) {
                                                            offset = p2 + w2 - halfwindow;
                                                            if (offset >= 0 && offset < this.dynamicK[L2 - 1]) {
                                                                final double[] array3 = convolution[f][p2];
                                                                final int n3 = d / 2;
                                                                array3[n3] += kMax[m][offset][d] * weight[f][d][w2];
                                                            }
                                                        }
                                                        final double[] array4 = convolution[f][p2];
                                                        final int n4 = d / 2;
                                                        array4[n4] += bias[f][d];
                                                    }
                                                }
                                            }
                                        }
                                        if (this.isDebug) {
                                            for (int f = 0; f < this.featureMap[L2]; ++f) {
                                                for (int p2 = 0; p2 < this.dynamicK[L2 - 1]; ++p2) {
                                                    for (int d = 0; d < this.dimension[L2]; ++d) {
                                                        System.out.println(String.valueOf(this.dimension[L2]) + " (" + f + "/" + p2 + "/" + d + "): " + convolution[f][p2][d]);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    convolutions.add(convolution);
                                    if (L2 == this.numberOfLayer - 1) {
                                        this.dynamicK[L2] = this.numberOfTopK;
                                    }
                                    else {
                                        this.dynamicK[L2] = Math.max(this.numberOfTopK, length * (this.numberOfLayer - (L2 + 1)) / this.numberOfLayer);
                                    }
                                    kMax = new double[this.featureMap[L2]][this.dynamicK[L2]][this.dimension[L2]];
                                    kMaxIndex = new int[this.featureMap[L2]][this.dynamicK[L2]][this.dimension[L2]];
                                    if (L2 == 0) {
                                        this.kMaxPooling(convolution, kMax, kMaxIndex, this.featureMap[L2], this.dynamicK[L2], this.dimension[L2], length);
                                    }
                                    else {
                                        this.kMaxPooling(convolution, kMax, kMaxIndex, this.featureMap[L2], this.dynamicK[L2], this.dimension[L2], this.dynamicK[L2 - 1]);
                                    }
                                    for (int f = 0; f < this.featureMap[L2]; ++f) {
                                        for (int d2 = 0; d2 < this.dimension[L2]; ++d2) {
                                            for (int k = 0; k < this.dynamicK[L2]; ++k) {
                                                if (this.isSigmoid) {
                                                    kMax[f][k][d2] = this.sigmoid(kMax[f][k][d2]);
                                                }
                                                else {
                                                    kMax[f][k][d2] = this.hardTanh(kMax[f][k][d2]);
                                                }
                                            }
                                        }
                                    }
                                    kMaxes.add(kMax);
                                    kMaxIndexes.add(kMaxIndex);
                                }
                                shift = 0;
                                topOutput = new double[this.numberOfTopOutput];
                                for (int f2 = 0; f2 < this.featureMap[this.numberOfLayer - 1]; ++f2) {
                                    for (int d3 = 0; d3 < this.dimension[this.numberOfLayer - 1]; ++d3) {
                                        for (int i = 0; i < this.numberOfTopK; ++i) {
                                            topOutput[shift] = kMax[f2][i][d3];
                                            ++shift;
                                        }
                                    }
                                }
                                output = new double[this.tagset.length];
                                for (int d4 = 0; d4 < this.numberOfTopOutput; ++d4) {
                                    final double[] array5 = output;
                                    final int n5 = 0;
                                    array5[n5] += topOutput[d4] * this.weightHO[0][d4];
                                }
                                final double[] array6 = output;
                                final int n6 = 0;
                                array6[n6] += this.biasOutput[0];
                                optVal = output[0];
                                optIndex = 0;
                                for (int t = 1; t < this.tagset.length; ++t) {
                                    for (int d3 = 0; d3 < this.numberOfTopOutput; ++d3) {
                                        final double[] array7 = output;
                                        final int n7 = t;
                                        array7[n7] += topOutput[d3] * this.weightHO[t][d3];
                                    }
                                    final double[] array8 = output;
                                    final int n8 = t;
                                    array8[n8] += this.biasOutput[t];
                                    if (output[t] > optVal) {
                                        optVal = output[t];
                                        optIndex = t;
                                    }
                                }
                                if (this.isDebug) {
                                    for (int t = 0; t < this.tagset.length; ++t) {
                                        System.out.println(String.valueOf(t) + ": " + output[t]);
                                    }
                                }
                                if (this.tagIndex.get(type) == optIndex) {
                                    ++correctCount;
                                }
                                else {
                                    derivOutput = new double[this.tagset.length];
                                    output[0] = Math.pow(2.718281828459045, output[0]);
                                    exp = output[0];
                                    for (int t = 1; t < this.tagset.length; ++t) {
                                        output[t] = Math.pow(2.718281828459045, output[t]);
                                        exp += output[t];
                                    }
                                    for (int t = 0; t < this.tagset.length; ++t) {
                                        if (this.tagIndex.get(type) == t) {
                                            derivOutput[t] = 1.0 - output[t] / exp;
                                        }
                                        else {
                                            derivOutput[t] = 0.0 - output[t] / exp;
                                        }
                                    }
                                    for (int t = 0; t < this.tagset.length; ++t) {
                                        if (!this.isShrink) {
                                            final double[] biasOutput = this.biasOutput;
                                            final int n9 = t;
                                            biasOutput[n9] += this.learningRate * derivOutput[t];
                                        }
                                        else {
                                            this.biasOutput[t] = (1.0 - this.regularizationRate * this.learningRate) * this.biasOutput[t] + this.learningRate * derivOutput[t];
                                        }
                                    }
                                    derivWeightHO = new double[this.tagset.length][this.numberOfTopOutput];
                                    deriveTopOutput = new double[this.numberOfTopOutput];
                                    for (int t = 0; t < this.tagset.length; ++t) {
                                        for (int d3 = 0; d3 < this.numberOfTopOutput; ++d3) {
                                            final double[] array9 = derivWeightHO[t];
                                            final int n10 = d3;
                                            array9[n10] += topOutput[d3] * derivOutput[t];
                                            final double[] array10 = deriveTopOutput;
                                            final int n11 = d3;
                                            array10[n11] += derivOutput[t] * this.weightHO[t][d3];
                                        }
                                    }
                                    for (int t = 0; t < this.tagset.length; ++t) {
                                        for (int d3 = 0; d3 < this.numberOfTopOutput; ++d3) {
                                            if (!this.isShrink) {
                                                final double[] array11 = this.weightHO[t];
                                                final int n12 = d3;
                                                array11[n12] += this.learningRate * derivWeightHO[t][d3];
                                            }
                                            else {
                                                this.weightHO[t][d3] = (1.0 - this.regularizationRate * this.learningRate) * this.weightHO[t][d3] + this.learningRate * derivWeightHO[t][d3];
                                            }
                                        }
                                    }
                                    kMax = kMaxes.get(this.numberOfLayer - 1);
                                    kMaxIndex = kMaxIndexes.get(this.numberOfLayer - 1);
                                    derivBiasConvolution = new double[this.featureMap[this.numberOfLayer - 1]][this.dimension[this.numberOfLayer - 2]];
                                    derivConvolution = new double[this.featureMap[this.numberOfLayer - 1]][this.dynamicK[this.numberOfLayer - 2]][this.dimension[this.numberOfLayer - 1]];
                                    shift = 0;
                                    for (int f2 = 0; f2 < this.featureMap[this.numberOfLayer - 1]; ++f2) {
                                        for (int d3 = 0; d3 < this.dimension[this.numberOfLayer - 1]; ++d3) {
                                            for (int i = 0; i < this.numberOfTopK; ++i) {
                                                if (this.isSigmoid) {
                                                    derivConvolution[f2][kMaxIndex[f2][i][d3]][d3] = deriveTopOutput[shift] * kMax[f2][i][d3] * (1.0 - kMax[f2][i][d3]);
                                                }
                                                else if (kMax[f2][i][d3] < 1.0 && kMax[f2][i][d3] > -1.0) {
                                                    derivConvolution[f2][kMaxIndex[f2][i][d3]][d3] = deriveTopOutput[shift];
                                                }
                                                final double[] array12 = derivBiasConvolution[f2];
                                                final int n13 = d3;
                                                array12[n13] += derivConvolution[f2][kMaxIndex[f2][i][d3]][d3];
                                                ++shift;
                                            }
                                            derivBiasConvolution[f2][d3 * 2] = derivBiasConvolution[f2][d3] / this.numberOfTopK / this.featureMap[this.numberOfLayer - 2] / 2.0;
                                            derivBiasConvolution[f2][d3 * 2 + 1] = derivBiasConvolution[f2][d3 * 2];
                                        }
                                    }
                                    kMax = kMaxes.get(this.numberOfLayer - 2);
                                    weight = this.weightConvolution.get(this.numberOfLayer - 1);
                                    bias = this.biasConvolution.get(this.numberOfLayer - 1);
                                    derivKMax = new double[this.featureMap[this.numberOfLayer - 2]][this.dynamicK[this.numberOfLayer - 2]][this.dimension[this.numberOfLayer - 2]];
                                    derivWeightConvolution = new double[this.featureMap[this.numberOfLayer - 1]][this.dimension[this.numberOfLayer - 2]][this.windowSize[this.numberOfLayer - 1]];
                                    for (int f2 = 0; f2 < this.featureMap[this.numberOfLayer - 1]; ++f2) {
                                        for (int p3 = 0; p3 < this.dynamicK[this.numberOfLayer - 2]; ++p3) {
                                            for (int d2 = 0; d2 < this.dimension[this.numberOfLayer - 2]; ++d2) {
                                                for (int j = 0; j < this.featureMap[this.numberOfLayer - 2]; ++j) {
                                                    for (int w = 0; w < this.windowSize[this.numberOfLayer - 1]; ++w) {
                                                        offset = p3 + w - halfwindow;
                                                        if (offset >= 0 && offset < this.dynamicK[this.numberOfLayer - 2]) {
                                                            final double[] array13 = derivKMax[j][offset];
                                                            final int n14 = d2;
                                                            array13[n14] += derivConvolution[f2][p3][d2 / 2] * weight[f2][d2][w];
                                                            final double[] array14 = derivWeightConvolution[f2][d2];
                                                            final int n15 = w;
                                                            array14[n15] += derivConvolution[f2][p3][d2 / 2] * kMax[j][offset][d2];
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    for (int f2 = 0; f2 < this.featureMap[this.numberOfLayer - 1]; ++f2) {
                                        for (int d3 = 0; d3 < this.dimension[this.numberOfLayer - 2]; ++d3) {
                                            for (int w3 = 0; w3 < this.windowSize[this.numberOfLayer - 1]; ++w3) {
                                                if (!this.isShrink) {
                                                    final double[] array15 = weight[f2][d3];
                                                    final int n16 = w3;
                                                    array15[n16] += this.learningRate * derivWeightConvolution[f2][d3][w3];
                                                }
                                                else {
                                                    weight[f2][d3][w3] = (1.0 - this.regularizationRate * this.learningRate) * weight[f2][d3][w3] + this.learningRate * derivWeightConvolution[f2][d3][w3];
                                                }
                                            }
                                            if (!this.isShrink) {
                                                final double[] array16 = bias[f2];
                                                final int n17 = d3;
                                                array16[n17] += this.learningRate * derivBiasConvolution[f2][d3];
                                            }
                                            else {
                                                bias[f2][d3] = (1.0 - this.regularizationRate * this.learningRate) * bias[f2][d3] + this.learningRate * derivBiasConvolution[f2][d3];
                                            }
                                        }
                                    }
                                    for (int L2 = this.numberOfLayer - 2; L2 > 0; --L2) {
                                        kMax = kMaxes.get(L2);
                                        kMaxIndex = kMaxIndexes.get(L2);
                                        derivBiasConvolution = new double[this.featureMap[L2]][this.dimension[L2 - 1]];
                                        derivConvolution = new double[this.featureMap[L2]][this.dynamicK[L2 - 1]][this.dimension[L2]];
                                        for (int f = 0; f < this.featureMap[L2]; ++f) {
                                            for (int d2 = 0; d2 < this.dimension[L2]; ++d2) {
                                                for (int k = 0; k < this.dynamicK[L2]; ++k) {
                                                    if (this.isSigmoid) {
                                                        derivConvolution[f][kMaxIndex[f][k][d2]][d2] = derivKMax[f][k][d2] * kMax[f][k][d2] * (1.0 - kMax[f][k][d2]);
                                                    }
                                                    else if (kMax[f][k][d2] < 1.0 && kMax[f][k][d2] > -1.0) {
                                                        derivConvolution[f][kMaxIndex[f][k][d2]][d2] = derivKMax[f][k][d2];
                                                    }
                                                    derivConvolution[f][kMaxIndex[f][k][d2]][d2] = derivKMax[f][k][d2] * kMax[f][k][d2] * (1.0 - kMax[f][k][d2]);
                                                    final double[] array17 = derivBiasConvolution[f];
                                                    final int n18 = d2;
                                                    array17[n18] += derivConvolution[f][kMaxIndex[f][k][d2]][d2];
                                                }
                                                derivBiasConvolution[f][d2 * 2] = derivBiasConvolution[f][d2] / this.dynamicK[L2] / this.featureMap[L2 - 1] / 2.0;
                                                derivBiasConvolution[f][d2 * 2 + 1] = derivBiasConvolution[f][d2 * 2];
                                            }
                                        }
                                        kMax = kMaxes.get(L2 - 1);
                                        weight = this.weightConvolution.get(L2);
                                        bias = this.biasConvolution.get(L2);
                                        derivKMax = new double[this.featureMap[L2 - 1]][this.dynamicK[L2 - 1]][this.dimension[L2 - 1]];
                                        derivWeightConvolution = new double[this.featureMap[L2]][this.dimension[L2 - 1]][this.windowSize[L2]];
                                        for (int f = 0; f < this.featureMap[L2]; ++f) {
                                            for (int p2 = 0; p2 < this.dynamicK[L2 - 1]; ++p2) {
                                                for (int d = 0; d < this.dimension[L2 - 1]; ++d) {
                                                    for (int m = 0; m < this.featureMap[L2 - 1]; ++m) {
                                                        for (int w2 = 0; w2 < this.windowSize[L2]; ++w2) {
                                                            offset = p2 + w2 - halfwindow;
                                                            if (offset >= 0 && offset < this.dynamicK[L2 - 1]) {
                                                                final double[] array18 = derivKMax[m][offset];
                                                                final int n19 = d;
                                                                array18[n19] += derivConvolution[f][p2][d / 2] * weight[f][d][w2];
                                                                final double[] array19 = derivWeightConvolution[f][d];
                                                                final int n20 = w2;
                                                                array19[n20] += derivConvolution[f][p2][d / 2] * kMax[m][offset][d];
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        for (int f = 0; f < this.featureMap[L2]; ++f) {
                                            for (int d2 = 0; d2 < this.dimension[L2 - 1]; ++d2) {
                                                for (int w4 = 0; w4 < this.windowSize[L2]; ++w4) {
                                                    if (!this.isShrink) {
                                                        final double[] array20 = weight[f][d2];
                                                        final int n21 = w4;
                                                        array20[n21] += this.learningRate * derivWeightConvolution[f][d2][w4];
                                                    }
                                                    else {
                                                        weight[f][d2][w4] = (1.0 - this.regularizationRate * this.learningRate) * weight[f][d2][w4] + this.learningRate * derivWeightConvolution[f][d2][w4];
                                                    }
                                                }
                                                if (!this.isShrink) {
                                                    final double[] array21 = bias[f];
                                                    final int n22 = d2;
                                                    array21[n22] += this.learningRate * derivBiasConvolution[f][d2];
                                                }
                                                else {
                                                    bias[f][d2] = (1.0 - this.regularizationRate * this.learningRate) * bias[f][d2] + this.learningRate * derivBiasConvolution[f][d2];
                                                }
                                            }
                                        }
                                    }
                                    kMax = kMaxes.get(0);
                                    kMaxIndex = kMaxIndexes.get(0);
                                    derivBiasConvolution = new double[this.featureMap[0]][this.featureDimension];
                                    derivConvolution = new double[this.featureMap[0]][length][this.dimension[0]];
                                    for (int f2 = 0; f2 < this.featureMap[0]; ++f2) {
                                        for (int d3 = 0; d3 < this.dimension[0]; ++d3) {
                                            for (int i = 0; i < this.dynamicK[0]; ++i) {
                                                if (this.isSigmoid) {
                                                    derivConvolution[f2][kMaxIndex[f2][i][d3]][d3] = derivKMax[f2][i][d3] * kMax[f2][i][d3] * (1.0 - kMax[f2][i][d3]);
                                                }
                                                else if (kMax[f2][i][d3] < 1.0 && kMax[f2][i][d3] > -1.0) {
                                                    derivConvolution[f2][kMaxIndex[f2][i][d3]][d3] = derivKMax[f2][i][d3];
                                                }
                                                final double[] array22 = derivBiasConvolution[f2];
                                                final int n23 = d3;
                                                array22[n23] += derivConvolution[f2][kMaxIndex[f2][i][d3]][d3];
                                            }
                                            derivBiasConvolution[f2][d3 * 2] = derivBiasConvolution[f2][d3] / this.dynamicK[0] / 2.0;
                                            derivBiasConvolution[f2][d3 * 2 + 1] = derivBiasConvolution[f2][d3 * 2];
                                        }
                                    }
                                    derivInput = new double[length][this.featureDimension];
                                    weight = this.weightConvolution.get(0);
                                    bias = this.biasConvolution.get(0);
                                    derivWeightConvolution = new double[this.featureMap[0]][this.featureDimension][this.windowSize[0]];
                                    for (int f2 = 0; f2 < this.featureMap[0]; ++f2) {
                                        for (int p3 = 0; p3 < length; ++p3) {
                                            for (int d2 = 0; d2 < this.featureDimension; ++d2) {
                                                for (int w4 = 0; w4 < this.windowSize[0]; ++w4) {
                                                    offset = p3 + w4 - halfwindow;
                                                    if (offset >= 0 && offset < length) {
                                                        final double[] array23 = derivInput[offset];
                                                        final int n24 = d2;
                                                        array23[n24] += derivConvolution[f2][p3][d2 / 2] * weight[f2][d2][w4];
                                                        final double[] array24 = derivWeightConvolution[f2][d2];
                                                        final int n25 = w4;
                                                        array24[n25] += derivConvolution[f2][p3][d2 / 2] * input[offset][d2];
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    for (int f2 = 0; f2 < this.featureMap[0]; ++f2) {
                                        for (int d3 = 0; d3 < this.featureDimension; ++d3) {
                                            for (int w3 = 0; w3 < this.windowSize[0]; ++w3) {
                                                if (!this.isShrink) {
                                                    final double[] array25 = weight[f2][d3];
                                                    final int n26 = w3;
                                                    array25[n26] += this.learningRate * derivWeightConvolution[f2][d3][w3];
                                                }
                                                else {
                                                    weight[f2][d3][w3] = (1.0 - this.regularizationRate * this.learningRate) * weight[f2][d3][w3] + this.learningRate * derivWeightConvolution[f2][d3][w3];
                                                }
                                            }
                                            if (!this.isShrink) {
                                                final double[] array26 = bias[f2];
                                                final int n27 = d3;
                                                array26[n27] += this.learningRate * derivBiasConvolution[f2][d3];
                                            }
                                            else {
                                                bias[f2][d3] = (1.0 - this.regularizationRate * this.learningRate) * bias[f2][d3] + this.learningRate * derivBiasConvolution[f2][d3];
                                            }
                                        }
                                    }
                                    for (int p = 0; p < length; ++p) {
                                        for (int d3 = 0; d3 < this.featureDimension; ++d3) {
                                            if (!this.isShrink) {
                                                final double[] array27 = input[p];
                                                final int n28 = d3;
                                                array27[n28] += this.learningRate * derivInput[p][d3];
                                            }
                                            else {
                                                input[p][d3] = (1.0 - this.regularizationRate * this.learningRate) * input[p][d3] + this.learningRate * derivInput[p][d3];
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                System.out.println("Check the " + totalCount + " sample.");
                                System.exit(0);
                            }
                        }
                    }
                    br.close();
                    isr.close();
                    fis.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                this.shrinkRate = 1.0 - this.regularizationRate;
                this.learningRate = this.beginRate * ((this.learningTimes - times) / this.learningTimes);
                if (this.learningRate < this.minLearningRate) {
                    this.learningRate = this.minLearningRate;
                }
                this.totalError = 1.0 - correctCount / totalCount;
                osw.write("The correct count: " + correctCount + "\r\n");
                osw.write("The total count: " + totalCount + "\r\n");
                osw.write("The tagging precision: " + (1.0 - this.totalError) + "\r\n");
                osw.write("The learning rate: " + this.learningRate + "\r\n");
                System.out.println("The correct count: " + correctCount);
                System.out.println("The total count: " + totalCount);
                System.out.println("The tagging precision: " + (1.0 - this.totalError));
                System.out.println("The learning rate: " + this.learningRate + "\r\n");
                ++times;
            }
            osw.flush();
            osw.close();
            fos.close();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    
    public void writePara() {
        final ConvolutionalSentenceModelSetting networkSetting = new ConvolutionalSentenceModelSetting(this.internalFeatureDimension, this.externalFeatureDimension, this.featureDimension, this.numberOfLayer, this.numberOfTopK, this.windowSize, this.featureMap, this.isIgnoreAlphabetNumber, this.lookupTable, this.weightConvolution, this.biasConvolution, this.weightHO, this.biasOutput, this.tagset);
        try {
            final FileOutputStream fos = new FileOutputStream(this.outputNetworkSettingFile);
            final ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(networkSetting);
            oos.close();
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
        if (this.isDebug) {
            for (int f = 0; f < featureMap; ++f) {
                for (int d = 0; d < dimension; ++d) {
                    for (int k = 0; k < dynamicK; ++k) {
                        System.out.println("(" + f + "/" + d + "/" + k + "):" + kMax[f][k][d] + "/" + kMaxIndex[f][k][d]);
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
        if (this.isDebug) {
            for (int f = 0; f < featureMap; ++f) {
                for (int d = 0; d < dimension; ++d) {
                    for (int k = 0; k < dynamicK; ++k) {
                        System.out.println("(" + f + "/" + d + "/" + k + "):" + kMax[f][k][d] + "/" + kMaxIndex[f][k][d]);
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
    
    public void setCorpusFile(final String corpusFile) {
        this.corpusFile = corpusFile;
    }
    
    public void setTokenFile(final String tokenFile) {
        this.tokenFile = tokenFile;
    }
    
    public void setLabelFile(final String labelFile) {
        this.labelFile = labelFile;
    }
    
    public void setReadEmbedding(final boolean isReadEmbedding) {
        this.isReadEmbedding = isReadEmbedding;
    }
    
    public void setEmbeddingFile(final String embeddingFile) {
        this.embeddingFile = embeddingFile;
    }
    
    public void setExternalFeature(final boolean isExternalFeature) {
        this.isExternalFeature = isExternalFeature;
    }
    
    public void setExternalFeatureFile(final String externalFeatureFile) {
        this.externalFeatureFile = externalFeatureFile;
    }
    
    public void setReadNetworkSetting(final boolean isReadNetworkSetting) {
        this.isReadNetworkSetting = isReadNetworkSetting;
    }
    
    public void setInputNetworkSettingFile(final String inputNetworkSettingFile) {
        this.inputNetworkSettingFile = inputNetworkSettingFile;
    }
    
    public void setOutputNetworkSettingFile(final String outputNetworkSettingFile) {
        this.outputNetworkSettingFile = outputNetworkSettingFile;
    }
    
    public void setEchoFile(final String echoFile) {
        this.echoFile = echoFile;
    }
    
    public void setInternalFeatureDimension(final int internalFeatureDimension) {
        this.internalFeatureDimension = internalFeatureDimension;
    }
    
    public void setExternalFeatureDimension(final int externalFeatureDimension) {
        this.externalFeatureDimension = externalFeatureDimension;
    }
    
    public void setFeatureDimension(final int featureDimension) {
        this.featureDimension = featureDimension;
    }
    
    public void setNumberOfLayer(final int numberOfLayer) {
        this.numberOfLayer = numberOfLayer;
    }
    
    public void setNumberOfTopK(final int numberOfTopK) {
        this.numberOfTopK = numberOfTopK;
    }
    
    public void setWindowSize(final int[] windowSize) {
        this.windowSize = windowSize;
    }
    
    public void setFeatureMap(final int[] featureMap) {
        this.featureMap = featureMap;
    }
    
    public void setLearningTimes(final long learningTimes) {
        this.learningTimes = learningTimes;
    }
    
    public void setLearningRate(final double learningRate) {
        this.learningRate = learningRate;
    }
    
    public void setRegularizationRate(final double regularizationRate) {
        this.regularizationRate = regularizationRate;
    }
    
    public void setErrorLimit(final double errorLimit) {
        this.errorLimit = errorLimit;
    }
    
    public void setIgnoreAlphabetNumber(final boolean isIgnoreAlphabetNumber) {
        this.isIgnoreAlphabetNumber = isIgnoreAlphabetNumber;
    }
    
    public double getShrinkRate() {
        return this.shrinkRate;
    }
}
