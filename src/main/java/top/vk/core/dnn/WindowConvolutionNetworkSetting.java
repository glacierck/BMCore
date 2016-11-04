// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.dnn;

import java.io.Serializable;
import java.util.HashMap;

public class WindowConvolutionNetworkSetting implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int internalFeatureDimension;
    private int externalFeatureDimension;
    private int featureDimension;
    private int windowSize;
    private int featureMap;
    private boolean isIgnoreAlphabetNumber;
    private boolean isAverage;
    private HashMap<String, double[]> lookupTable;
    private double[][][] weightConvolution;
    private double[][] biasConvolution;
    private double[][] weightHO;
    private double[] biasOutput;
    private double[][] transition;
    private String[] tagset;
    
    public WindowConvolutionNetworkSetting(final int internalFeatureDimension, final int externalFeatureDimension, final int featureDimension, final int windowSize, final int featureMap, final boolean isIgnoreAlphabetNumber, final boolean isAverage, final HashMap<String, double[]> lookupTable, final double[][][] weightConvolution, final double[][] biasConvolution, final double[][] weightHO, final double[] biasOutput, final double[][] transition, final String[] tagset) {
        this.internalFeatureDimension = -1;
        this.externalFeatureDimension = -1;
        this.featureDimension = -1;
        this.windowSize = -1;
        this.featureMap = -1;
        this.isIgnoreAlphabetNumber = true;
        this.isAverage = true;
        this.lookupTable = null;
        this.weightConvolution = null;
        this.biasConvolution = null;
        this.internalFeatureDimension = internalFeatureDimension;
        this.externalFeatureDimension = externalFeatureDimension;
        this.featureDimension = featureDimension;
        this.windowSize = windowSize;
        this.featureMap = featureMap;
        this.isIgnoreAlphabetNumber = isIgnoreAlphabetNumber;
        this.isAverage = isAverage;
        this.lookupTable = lookupTable;
        this.weightConvolution = weightConvolution;
        this.biasConvolution = biasConvolution;
        this.weightHO = weightHO;
        this.biasOutput = biasOutput;
        this.transition = transition;
        this.tagset = tagset;
    }
    
    public int getInternalFeatureDimension() {
        return this.internalFeatureDimension;
    }
    
    public int getExternalFeatureDimension() {
        return this.externalFeatureDimension;
    }
    
    public int getFeatureDimension() {
        return this.featureDimension;
    }
    
    public int getWindowSize() {
        return this.windowSize;
    }
    
    public int getFeatureMap() {
        return this.featureMap;
    }
    
    public HashMap<String, double[]> getLookupTable() {
        return this.lookupTable;
    }
    
    public double[][][] getWeightConvolution() {
        return this.weightConvolution;
    }
    
    public double[][] getBiasConvolution() {
        return this.biasConvolution;
    }
    
    public double[][] getWeightHO() {
        return this.weightHO;
    }
    
    public double[] getBiasOutput() {
        return this.biasOutput;
    }
    
    public double[][] getTransition() {
        return this.transition;
    }
    
    public String[] getTagset() {
        return this.tagset;
    }
    
    public boolean isIgnoreAlphabetNumber() {
        return this.isIgnoreAlphabetNumber;
    }
    
    public boolean isAverage() {
        return this.isAverage;
    }
}
