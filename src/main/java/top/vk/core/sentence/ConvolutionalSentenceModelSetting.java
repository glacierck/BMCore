// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.sentence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ConvolutionalSentenceModelSetting implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int internalFeatureDimension;
    private int externalFeatureDimension;
    private int featureDimension;
    private int numberOfLayer;
    private int numberOfTopK;
    private int[] windowSize;
    private int[] featureMap;
    private boolean isIgnoreAlphabetNumber;
    private HashMap<String, double[]> lookupTable;
    private ArrayList<double[][][]> weightConvolution;
    private ArrayList<double[][]> biasConvolution;
    private double[][] weightHO;
    private double[] biasOutput;
    private String[] tagset;
    
    public ConvolutionalSentenceModelSetting(final int internalFeatureDimension, final int externalFeatureDimension, final int featureDimension, final int numberOfLayer, final int numberOfTopK, final int[] windowSize, final int[] featureMap, final boolean isIgnoreAlphabetNumber, final HashMap<String, double[]> lookupTable, final ArrayList<double[][][]> weightConvolution, final ArrayList<double[][]> biasConvolution, final double[][] weightHO, final double[] biasOutput, final String[] tagset) {
        this.internalFeatureDimension = -1;
        this.externalFeatureDimension = -1;
        this.featureDimension = -1;
        this.numberOfLayer = 2;
        this.numberOfTopK = 3;
        this.windowSize = null;
        this.featureMap = null;
        this.isIgnoreAlphabetNumber = true;
        this.lookupTable = null;
        this.weightConvolution = null;
        this.biasConvolution = null;
        this.internalFeatureDimension = internalFeatureDimension;
        this.externalFeatureDimension = externalFeatureDimension;
        this.featureDimension = featureDimension;
        this.numberOfLayer = numberOfLayer;
        this.numberOfTopK = numberOfTopK;
        this.windowSize = windowSize;
        this.featureMap = featureMap;
        this.isIgnoreAlphabetNumber = isIgnoreAlphabetNumber;
        this.lookupTable = lookupTable;
        this.weightConvolution = weightConvolution;
        this.biasConvolution = biasConvolution;
        this.weightHO = weightHO;
        this.biasOutput = biasOutput;
        this.tagset = tagset;
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
    
    public int getFeatureDimension() {
        return this.featureDimension;
    }
    
    public void setFeatureDimension(final int featureDimension) {
        this.featureDimension = featureDimension;
    }
    
    public int[] getWindowSize() {
        return this.windowSize;
    }
    
    public void setWindowSize(final int[] windowSize) {
        this.windowSize = windowSize;
    }
    
    public int[] getFeatureMap() {
        return this.featureMap;
    }
    
    public void setFeatureMap(final int[] featureMap) {
        this.featureMap = featureMap;
    }
    
    public boolean isIgnoreAlphabetNumber() {
        return this.isIgnoreAlphabetNumber;
    }
    
    public void setIgnoreAlphabetNumber(final boolean isIgnoreAlphabetNumber) {
        this.isIgnoreAlphabetNumber = isIgnoreAlphabetNumber;
    }
    
    public HashMap<String, double[]> getLookupTable() {
        return this.lookupTable;
    }
    
    public void setLookupTable(final HashMap<String, double[]> lookupTable) {
        this.lookupTable = lookupTable;
    }
    
    public ArrayList<double[][][]> getWeightConvolution() {
        return this.weightConvolution;
    }
    
    public void setWeightConvolution(final ArrayList<double[][][]> weightConvolution) {
        this.weightConvolution = weightConvolution;
    }
    
    public ArrayList<double[][]> getBiasConvolution() {
        return this.biasConvolution;
    }
    
    public void setBiasConvolution(final ArrayList<double[][]> biasConvolution) {
        this.biasConvolution = biasConvolution;
    }
    
    public double[][] getWeightHO() {
        return this.weightHO;
    }
    
    public void setWeightHO(final double[][] weightHO) {
        this.weightHO = weightHO;
    }
    
    public double[] getBiasOutput() {
        return this.biasOutput;
    }
    
    public void setBiasOutput(final double[] biasOutput) {
        this.biasOutput = biasOutput;
    }
    
    public String[] getTagset() {
        return this.tagset;
    }
    
    public void setTagset(final String[] tagset) {
        this.tagset = tagset;
    }
    
    public static long getSerialversionuid() {
        return 1L;
    }
    
    public int getNumberOfLayer() {
        return this.numberOfLayer;
    }
    
    public void setNumberOfLayer(final int numberOfLayer) {
        this.numberOfLayer = numberOfLayer;
    }
    
    public int getNumberOfTopK() {
        return this.numberOfTopK;
    }
    
    public void setNumberOfTopK(final int numberOfTopK) {
        this.numberOfTopK = numberOfTopK;
    }
}
