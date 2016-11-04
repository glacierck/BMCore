// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.dnn;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class WindowConvolutionNetwork
{
    private String corpusFile;
    private String tokenFile;
    private String labelFile;
    private String embeddingFile;
    private String externalFeatureFile;
    private String inputNetworkSettingFile;
    private String outputNetworkSettingFile;
    private String echoFile;
    private boolean isReadNetworkSetting;
    private boolean isReadEmbedding;
    private boolean isExternalFeature;
    private double learningRate;
    private double beginRate;
    private double minLearningRate;
    private double regularizationRate;
    private double errorLimit;
    private long learningTimes;
    private int internalFeatureDimension;
    private int externalFeatureDimension;
    private int featureDimension;
    private int windowSize;
    private int featureMap;
    private boolean isIgnoreAlphabetNumber;
    private HashMap<String, double[]> lookupTable;
    private double[][][] weightConvolution;
    private double[][] biasConvolution;
    private double[][] weightHO;
    private double[] biasOutput;
    private double[][] transition;
    private String[] tagset;
    private double constant;
    private double totalError;
    private double shrinkRate;
    private HashMap<String, Integer> tagIndex;
    private boolean isAverage;
    private boolean isDebug;
    
    public WindowConvolutionNetwork() {
        this.corpusFile = null;
        this.tokenFile = null;
        this.labelFile = null;
        this.embeddingFile = null;
        this.externalFeatureFile = null;
        this.inputNetworkSettingFile = null;
        this.outputNetworkSettingFile = null;
        this.echoFile = null;
        this.isReadNetworkSetting = false;
        this.isReadEmbedding = false;
        this.isExternalFeature = false;
        this.learningRate = 0.05;
        this.beginRate = 0.05;
        this.minLearningRate = 0.01;
        this.regularizationRate = 1.0E-4;
        this.errorLimit = 0.005;
        this.learningTimes = 100L;
        this.internalFeatureDimension = 100;
        this.externalFeatureDimension = 0;
        this.featureDimension = 100;
        this.windowSize = 5;
        this.featureMap = 5;
        this.isIgnoreAlphabetNumber = true;
        this.lookupTable = new HashMap<String, double[]>();
        this.weightConvolution = null;
        this.biasConvolution = null;
        this.constant = 2.38;
        this.totalError = 1.0;
        this.shrinkRate = 1.0;
        this.tagIndex = new HashMap<String, Integer>();
        this.isAverage = true;
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
            System.out.println("Check the dimension values.");
            System.exit(0);
        }
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
                                feature2[i] = (randomgen.nextDouble() - 0.5) * 2.0 / this.internalFeatureDimension;
                            }
                            this.lookupTable.put(line, feature2);
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
            double denominator = Math.sqrt(this.windowSize);
            this.weightConvolution = new double[this.featureMap][this.featureDimension][this.windowSize];
            for (int f = 0; f < this.featureMap; ++f) {
                for (int d = 0; d < this.featureDimension; ++d) {
                    for (int w = 0; w < this.windowSize; ++w) {
                        this.weightConvolution[f][d][w] = (randomgen.nextDouble() - 0.5) * 2.0 * this.constant / denominator;
                    }
                }
            }
            this.biasConvolution = new double[this.featureMap][this.featureDimension];
            for (int f = 0; f < this.featureMap; ++f) {
                for (int d = 0; d < this.featureDimension; ++d) {
                    this.biasConvolution[f][d] = randomgen.nextDouble() - 2.0;
                }
            }
            if (this.isAverage) {
                denominator = Math.sqrt(this.featureDimension);
            }
            else {
                denominator = Math.sqrt(this.featureDimension * this.featureMap);
            }
            this.weightHO = new double[this.tagset.length][this.featureDimension];
            for (int t = 0; t < this.tagset.length; ++t) {
                for (int d = 0; d < this.featureDimension; ++d) {
                    this.weightHO[t][d] = (randomgen.nextDouble() - 0.5) * 2.0 * this.constant / denominator;
                }
            }
            this.biasOutput = new double[this.tagset.length];
            for (int t = 0; t < this.tagset.length; ++t) {
                this.biasOutput[t] = (randomgen.nextDouble() - 0.5) * 2.0 * 0.2;
            }
            this.transition = new double[this.tagset.length][this.tagset.length + 1];
            for (int k = 0; k < this.tagset.length; ++k) {
                for (int l = 0; l < this.tagset.length + 1; ++l) {
                    this.transition[k][l] = (randomgen.nextDouble() - 0.5) * 2.0 * this.constant / denominator;
                }
            }
        }
        else {
            try {
                final FileInputStream fis2 = new FileInputStream(this.inputNetworkSettingFile);
                final ObjectInputStream ois2 = new ObjectInputStream(fis2);
//                cn.edu.fudan.dnn.WindowConvolutionNetworkSetting temp =(cn.edu.fudan.dnn.WindowConvolutionNetworkSetting)ois2.readObject();
//                final int internalFeatureDimension =temp.getInternalFeatureDimension();
//                final int externalFeatureDimension =temp.getExternalFeatureDimension();
//                final int featureDimension =temp.getFeatureDimension();
//                final int windowSize =temp.getWindowSize();
//                final int featureMap =temp.getFeatureMap();
//                final boolean isIgnoreAlphabetNumber =temp.isIgnoreAlphabetNumber();
//                final boolean isAverage =temp.isAverage();
//                final HashMap<String, double[]> lookupTable =temp.getLookupTable();
//                final double[][][] weightConvolution =temp.getWeightConvolution();
//                final double[][] biasConvolution =temp.getBiasConvolution();
//                final double[][] weightHO =temp.getWeightHO();
//                final double[] biasOutput =temp.getBiasOutput();
//                final double[][] transition =temp.getTransition();
//                final String[] tagset =temp.getTagset();
//                final WindowConvolutionNetworkSetting networkSetting = new WindowConvolutionNetworkSetting(internalFeatureDimension,externalFeatureDimension,
//                        featureDimension,windowSize,featureMap,isIgnoreAlphabetNumber,isAverage,lookupTable,
//                        weightConvolution,biasConvolution,weightHO,biasOutput,transition,tagset);
                final WindowConvolutionNetworkSetting networkSetting =(WindowConvolutionNetworkSetting)ois2.readObject();
                this.internalFeatureDimension = networkSetting.getInternalFeatureDimension();
                this.externalFeatureDimension = networkSetting.getExternalFeatureDimension();
                this.featureDimension = networkSetting.getFeatureDimension();
                this.windowSize = networkSetting.getWindowSize();
                this.featureMap = networkSetting.getFeatureMap();
                this.isIgnoreAlphabetNumber = networkSetting.isIgnoreAlphabetNumber();
                this.isAverage = networkSetting.isAverage();
                this.lookupTable = networkSetting.getLookupTable();
                this.weightConvolution = networkSetting.getWeightConvolution();
                this.biasConvolution = networkSetting.getBiasConvolution();
                this.weightHO = networkSetting.getWeightHO();
                this.biasOutput = networkSetting.getBiasOutput();
                this.transition = networkSetting.getTransition();
                this.tagset = networkSetting.getTagset();
                ois2.close();
                fis2.close();
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            for (int m = 0; m < this.tagset.length; ++m) {
                this.tagIndex.put(this.tagset[m], m);
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
            osw.write("  windowSize: " + this.windowSize + "\r\n");
            osw.write("  featureMap: " + this.featureMap + "\r\n");
            osw.write("  isIgnoreAlphabetNumber: " + this.isIgnoreAlphabetNumber + "\r\n");
            osw.write("  isAverage: " + this.isAverage + "\r\n");
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
            double[][][] input = null;
            double[][][] convolution = null;
            double[][] combine = null;
            double[][] output = null;
            double[][] optVal = null;
            int[][] optRec = null;
            double temp = 0.0;
            String[] optSequence = null;
            double optValue = -1.7976931348623157E308;
            boolean isWrong = false;
            double[][] derivDelta = null;
            double[][] derivTrans = null;
            double[] derivBiasOutput = null;
            double[][] derivCombine = null;
            double[][] derivWeightHO = null;
            double[][] derivBiasConvolution = null;
            double[][][] derivConvolution = null;
            double[][][] derivInput = null;
            double[][][] derivWeightConvolution = null;
            while (times <= this.learningTimes && this.totalError > this.errorLimit) {
                final int halfwindow = (this.windowSize - 1) / 2;
                int totalCount = 0;
                int correctCount = 0;
                osw.write("\r\n");
                osw.write("The current learning time " + times + " (Convolutional window model with multiple feature maps): " + "\r\n");
                System.out.print("\r\n");
                System.out.println("The current learning time: " + times);
                try {
                    final FileInputStream fis = new FileInputStream(this.corpusFile);
                    final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                    final BufferedReader br = new BufferedReader(isr);
                    String[][] sample = null;
                    String line = null;
                    String[] tokens = null;
                    Iterator<String> it = null;
                    ArrayList<String> cell = new ArrayList<String>();
                    int num = 0;
                    boolean isNormal = true;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (!line.equals("") && !line.equals(" ")) {
                            ++num;
                            tokens = line.split("\\s+");
                            if (tokens.length == 2) {
                                cell.add(tokens[0]);
                                cell.add(tokens[1]);
                            }
                            else {
                                isNormal = false;
                            }
                            tokens = null;
                        }
                        if (line.equals("") || line.equals(" ")) {
                            if (!isNormal) {
                                isNormal = true;
                                num = 0;
                                cell = new ArrayList<String>();
                            }
                            else {
                                sample = new String[2][num];
                                it = cell.iterator();
                                offset = 0;
                                while (it.hasNext()) {
                                    sample[0][offset] = it.next();
                                    sample[1][offset] = it.next();
                                    ++offset;
                                }
                                final int length = sample[0].length;
                                if (this.isDebug) {
                                    for (int p = 0; p < length; ++p) {
                                        System.out.print(String.valueOf(sample[0][p]) + "/" + sample[1][p] + " ");
                                    }
                                    System.out.print("\r\n");
                                }
                                input = new double[length][this.windowSize][this.featureDimension];
                                convolution = new double[length][this.featureMap][this.featureDimension];
                                combine = new double[length][this.featureDimension];
                                output = new double[length][this.tagset.length];
                                for (int p = 0; p < length; ++p) {
                                    for (int w = 0; w < this.windowSize; ++w) {
                                        final int shift = p - halfwindow + w;
                                        if (w == halfwindow) {
                                            input[p][w] = this.getFeature(sample[0][shift]);
                                        }
                                        else if (w < halfwindow) {
                                            if (shift < 0) {
                                                input[p][w] = this.getFeature(getBeginning());
                                            }
                                            else {
                                                input[p][w] = this.getFeature(sample[0][shift]);
                                            }
                                        }
                                        else if (w > halfwindow) {
                                            if (shift > length - 1) {
                                                input[p][w] = this.getFeature(getEnding());
                                            }
                                            else {
                                                input[p][w] = this.getFeature(sample[0][shift]);
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
                                if (this.isDebug) {
                                    for (int p = 0; p < length; ++p) {
                                        for (int d2 = 0; d2 < this.featureDimension; ++d2) {
                                            System.out.println(combine[p][d2]);
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
                                if (this.isDebug) {
                                    for (int p = 0; p < length; ++p) {
                                        for (int t = 0; t < this.tagset.length; ++t) {}
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
                                isWrong = false;
                                for (int k = 0; k < length; ++k) {
                                    if (optSequence[k].equals(sample[1][k])) {
                                        ++correctCount;
                                    }
                                    else {
                                        isWrong = true;
                                    }
                                }
                                totalCount += length;
                                if (isWrong) {
                                    derivDelta = new double[length][this.tagset.length];
                                    derivTrans = new double[this.tagset.length][this.tagset.length + 1];
                                    for (int p2 = 0; p2 < length; ++p2) {
                                        if (!optSequence[p2].equals(sample[1][p2])) {
                                            final double[] array6 = derivDelta[p2];
                                            final int intValue = this.tagIndex.get(sample[1][p2]);
                                            ++array6[intValue];
                                            final double[] array7 = derivDelta[p2];
                                            final int intValue2 = this.tagIndex.get(optSequence[p2]);
                                            --array7[intValue2];
                                            if (p2 == 0) {
                                                final double[] array8 = derivTrans[this.tagIndex.get(sample[1][p2])];
                                                final int length2 = this.tagset.length;
                                                ++array8[length2];
                                                final double[] array9 = derivTrans[this.tagIndex.get(optSequence[p2])];
                                                final int length3 = this.tagset.length;
                                                --array9[length3];
                                            }
                                        }
                                        if (p2 > 0 && (!optSequence[p2].equals(sample[1][p2]) || !optSequence[p2 - 1].equals(sample[1][p2 - 1]))) {
                                            final double[] array10 = derivTrans[this.tagIndex.get(sample[1][p2 - 1])];
                                            final int intValue3 = this.tagIndex.get(sample[1][p2]);
                                            ++array10[intValue3];
                                            final double[] array11 = derivTrans[this.tagIndex.get(optSequence[p2 - 1])];
                                            final int intValue4 = this.tagIndex.get(optSequence[p2]);
                                            --array11[intValue4];
                                        }
                                    }
                                    for (int k = 0; k < this.tagset.length; ++k) {
                                        for (int l = 0; l < this.tagset.length + 1; ++l) {
                                            final double[] array12 = this.transition[k];
                                            final int n6 = l;
                                            array12[n6] += this.learningRate * derivTrans[k][l];
                                        }
                                    }
                                    derivBiasOutput = new double[this.tagset.length];
                                    for (int t = 0; t < this.tagset.length; ++t) {
                                        for (int p3 = 0; p3 < length; ++p3) {
                                            final double[] array13 = derivBiasOutput;
                                            final int n7 = t;
                                            array13[n7] += derivDelta[p3][t];
                                        }
                                    }
                                    for (int t = 0; t < this.tagset.length; ++t) {
                                        final double[] biasOutput = this.biasOutput;
                                        final int n8 = t;
                                        biasOutput[n8] += this.learningRate * derivBiasOutput[t];
                                    }
                                    derivCombine = new double[length][this.featureDimension];
                                    derivWeightHO = new double[this.tagset.length][this.featureDimension];
                                    for (int p2 = 0; p2 < length; ++p2) {
                                        for (int t2 = 0; t2 < this.tagset.length; ++t2) {
                                            for (int d3 = 0; d3 < this.featureDimension; ++d3) {
                                                final double[] array14 = derivCombine[p2];
                                                final int n9 = d3;
                                                array14[n9] += derivDelta[p2][t2] * this.weightHO[t2][d3];
                                                final double[] array15 = derivWeightHO[t2];
                                                final int n10 = d3;
                                                array15[n10] += derivDelta[p2][t2] * combine[p2][d3];
                                            }
                                        }
                                    }
                                    for (int t = 0; t < this.tagset.length; ++t) {
                                        for (int d = 0; d < this.featureDimension; ++d) {
                                            final double[] array16 = this.weightHO[t];
                                            final int n11 = d;
                                            array16[n11] += this.learningRate * derivWeightHO[t][d];
                                        }
                                    }
                                    derivConvolution = new double[length][this.featureMap][this.featureDimension];
                                    for (int p2 = 0; p2 < length; ++p2) {
                                        for (int f2 = 0; f2 < this.featureMap; ++f2) {
                                            for (int d3 = 0; d3 < this.featureDimension; ++d3) {
                                                derivConvolution[p2][f2][d3] = derivCombine[p2][d3] * convolution[p2][f2][d3] * (1.0 - convolution[p2][f2][d3]);
                                            }
                                        }
                                    }
                                    derivBiasConvolution = new double[this.featureMap][this.featureDimension];
                                    for (int f = 0; f < this.featureMap; ++f) {
                                        for (int d = 0; d < this.featureDimension; ++d) {
                                            for (int p4 = 0; p4 < length; ++p4) {
                                                final double[] array17 = derivBiasConvolution[f];
                                                final int n12 = d;
                                                array17[n12] += derivConvolution[p4][f][d];
                                            }
                                        }
                                    }
                                    for (int f = 0; f < this.featureMap; ++f) {
                                        for (int d = 0; d < this.featureDimension; ++d) {
                                            final double[] array18 = this.biasConvolution[f];
                                            final int n13 = d;
                                            array18[n13] += this.learningRate * derivBiasConvolution[f][d];
                                        }
                                    }
                                    derivInput = new double[length][this.windowSize][this.featureDimension];
                                    derivWeightConvolution = new double[this.featureMap][this.featureDimension][this.windowSize];
                                    for (int p2 = 0; p2 < length; ++p2) {
                                        for (int f2 = 0; f2 < this.featureMap; ++f2) {
                                            for (int d3 = 0; d3 < this.featureDimension; ++d3) {
                                                for (int w3 = 0; w3 < this.windowSize; ++w3) {
                                                    final double[] array19 = derivWeightConvolution[f2][d3];
                                                    final int n14 = w3;
                                                    array19[n14] += derivConvolution[p2][f2][d3] * input[p2][w3][d3];
                                                    final double[] array20 = derivInput[p2][w3];
                                                    final int n15 = d3;
                                                    array20[n15] += derivConvolution[p2][f2][d3] * this.weightConvolution[f2][d3][w3];
                                                }
                                            }
                                        }
                                    }
                                    for (int f = 0; f < this.featureMap; ++f) {
                                        for (int d = 0; d < this.featureDimension; ++d) {
                                            for (int w2 = 0; w2 < this.windowSize; ++w2) {
                                                final double[] array21 = this.weightConvolution[f][d];
                                                final int n16 = w2;
                                                array21[n16] += this.learningRate * derivWeightConvolution[f][d][w2];
                                            }
                                        }
                                    }
                                    for (int p2 = 0; p2 < length; ++p2) {
                                        for (int w4 = 0; w4 < this.windowSize; ++w4) {
                                            for (int d3 = 0; d3 < this.internalFeatureDimension; ++d3) {
                                                final double[] array22 = input[p2][w4];
                                                final int n17 = d3;
                                                array22[n17] += this.learningRate * derivInput[p2][w4][d3];
                                            }
                                        }
                                    }
                                }
                                num = 0;
                                cell = new ArrayList<String>();
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
                for (int m = 0; m < this.tagset.length; ++m) {
                    for (int j2 = 0; j2 < this.tagset.length + 1; ++j2) {
                        this.transition[m][j2] *= this.shrinkRate;
                    }
                }
                for (int f3 = 0; f3 < this.featureMap; ++f3) {
                    for (int d4 = 0; d4 < this.featureDimension; ++d4) {
                        this.biasConvolution[f3][d4] *= this.shrinkRate;
                        for (int w5 = 0; w5 < this.windowSize; ++w5) {
                            this.weightConvolution[f3][d4][w5] *= this.shrinkRate;
                        }
                    }
                }
                for (int t3 = 0; t3 < this.tagset.length; ++t3) {
                    this.biasOutput[t3] *= this.shrinkRate;
                    for (int d4 = 0; d4 < this.featureDimension; ++d4) {
                        this.weightHO[t3][d4] *= this.shrinkRate;
                    }
                }
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
        final WindowConvolutionNetworkSetting networkSetting = new WindowConvolutionNetworkSetting(this.internalFeatureDimension, this.externalFeatureDimension, this.featureDimension, this.windowSize, this.featureMap, this.isIgnoreAlphabetNumber, this.isAverage, this.lookupTable, this.weightConvolution, this.biasConvolution, this.weightHO, this.biasOutput, this.transition, this.tagset);
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
    
    public String getCorpusFile() {
        return this.corpusFile;
    }
    
    public void setCorpusFile(final String corpusFile) {
        this.corpusFile = corpusFile;
    }
    
    public String getTokenFile() {
        return this.tokenFile;
    }
    
    public void setTokenFile(final String tokenFile) {
        this.tokenFile = tokenFile;
    }
    
    public String getLabelFile() {
        return this.labelFile;
    }
    
    public void setLabelFile(final String labelFile) {
        this.labelFile = labelFile;
    }
    
    public String getEmbeddingFile() {
        return this.embeddingFile;
    }
    
    public void setEmbeddingFile(final String embeddingFile) {
        this.embeddingFile = embeddingFile;
    }
    
    public boolean isReadEmbedding() {
        return this.isReadEmbedding;
    }
    
    public void setReadEmbedding(final boolean isReadEmbedding) {
        this.isReadEmbedding = isReadEmbedding;
    }
    
    public String getExternalFeatureFile() {
        return this.externalFeatureFile;
    }
    
    public void setExternalFeatureFile(final String externalFeatureFile) {
        this.externalFeatureFile = externalFeatureFile;
    }
    
    public String getInputNetworkSettingFile() {
        return this.inputNetworkSettingFile;
    }
    
    public void setInputNetworkSettingFile(final String inputNetworkSettingFile) {
        this.inputNetworkSettingFile = inputNetworkSettingFile;
    }
    
    public String getOutputNetworkSettingFile() {
        return this.outputNetworkSettingFile;
    }
    
    public void setOutputNetworkSettingFile(final String outputNetworkSettingFile) {
        this.outputNetworkSettingFile = outputNetworkSettingFile;
    }
    
    public String getEchoFile() {
        return this.echoFile;
    }
    
    public void setEchoFile(final String echoFile) {
        this.echoFile = echoFile;
    }
    
    public boolean isReadNetworkSetting() {
        return this.isReadNetworkSetting;
    }
    
    public void setReadNetworkSetting(final boolean isReadNetworkSetting) {
        this.isReadNetworkSetting = isReadNetworkSetting;
    }
    
    public boolean isExternalFeature() {
        return this.isExternalFeature;
    }
    
    public void setExternalFeature(final boolean isExternalFeature) {
        this.isExternalFeature = isExternalFeature;
    }
    
    public double getLearningRate() {
        return this.learningRate;
    }
    
    public void setLearningRate(final double learningRate) {
        this.learningRate = learningRate;
    }
    
    public double getRegularizationRate() {
        return this.regularizationRate;
    }
    
    public void setRegularizationRate(final double regularizationRate) {
        this.regularizationRate = regularizationRate;
    }
    
    public double getErrorLimit() {
        return this.errorLimit;
    }
    
    public void setErrorLimit(final double errorLimit) {
        this.errorLimit = errorLimit;
    }
    
    public long getLearningTimes() {
        return this.learningTimes;
    }
    
    public void setLearningTimes(final long learningTimes) {
        this.learningTimes = learningTimes;
    }
    
    public void setFeatureDimension(final int featureDimension) {
        this.featureDimension = featureDimension;
    }
    
    public int getInternalFeatureDimension() {
        return this.internalFeatureDimension;
    }
    
    public void setInternalFeatureDimension(final int internalFeatureDimension) {
        this.internalFeatureDimension = internalFeatureDimension;
    }
    
    public int getExternalFeatureDimension() {
        return this.externalFeatureDimension;
    }
    
    public void setExternalFeatureDimension(final int externalFeatureDimension) {
        this.externalFeatureDimension = externalFeatureDimension;
    }
    
    public int getWindowSize() {
        return this.windowSize;
    }
    
    public void setWindowSize(final int windowSize) {
        this.windowSize = windowSize;
    }
    
    public int getFeatureMap() {
        return this.featureMap;
    }
    
    public void setFeatureMap(final int featureMap) {
        this.featureMap = featureMap;
    }
    
    public int getFeatureDimension() {
        return this.featureDimension;
    }
    
    public boolean isIgnoreAlphabetNumber() {
        return this.isIgnoreAlphabetNumber;
    }
    
    public void setIgnoreAlphabetNumber(final boolean isIgnoreAlphabetNumber) {
        this.isIgnoreAlphabetNumber = isIgnoreAlphabetNumber;
    }
    
    public boolean isAverage() {
        return this.isAverage;
    }
    
    public void setAverage(final boolean isAverage) {
        this.isAverage = isAverage;
    }
    
    static String getBeginning() {
        return "\u2560";
    }
    
    static String getEnding() {
        return "\u2563";
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
}
