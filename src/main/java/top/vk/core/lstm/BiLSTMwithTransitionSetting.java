// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lstm;

import java.io.Serializable;
import java.util.HashMap;

public class BiLSTMwithTransitionSetting implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int inputUnits;
    private int outputUnits;
    private int numberOfBlock;
    private int numberOfCell;
    private int numberOfInput;
    private BiLSTMObject lstm;
    private BiOutputLayer outputlayer;
    private int vocabularyDimension;
    private int posLabelDimension;
    private int preprocessLabelDimension;
    private HashMap<String, double[]> vocabularyLookupTable;
    private HashMap<String, double[]> posLabelLookupTable;
    private HashMap<String, double[]> preprocessLabelLookupTable;
    private double[][] transition;
    private String[] labelset;
    private HashMap<String, Integer> labelIndex;
    
    public BiLSTMwithTransitionSetting(final int inputUnits, final int outputUnits, final int numberOfBlock, final int numberOfCell, final int numberOfInput, final BiLSTMObject lstm, final double[][] transition, final BiOutputLayer outputlayer, final int vocabularyDimension, final int posLabelDimension, final int preprocessLabelDimension, final HashMap<String, double[]> vocabularyLookupTable, final HashMap<String, double[]> posLabelLookupTable, final HashMap<String, double[]> preprocessLabelLookupTable, final String[] labelset, final HashMap<String, Integer> labelIndex) {
        this.inputUnits = inputUnits;
        this.outputUnits = outputUnits;
        this.numberOfBlock = numberOfBlock;
        this.numberOfCell = numberOfCell;
        this.numberOfInput = numberOfInput;
        this.lstm = lstm;
        this.outputlayer = outputlayer;
        this.transition = transition;
        this.vocabularyDimension = vocabularyDimension;
        this.posLabelDimension = posLabelDimension;
        this.preprocessLabelDimension = preprocessLabelDimension;
        this.vocabularyLookupTable = vocabularyLookupTable;
        this.posLabelLookupTable = posLabelLookupTable;
        this.preprocessLabelLookupTable = preprocessLabelLookupTable;
        this.labelset = labelset;
        this.labelIndex = labelIndex;
    }
    
    public int getInputUnits() {
        return this.inputUnits;
    }
    
    public void setInputUnits(final int inputUnits) {
        this.inputUnits = inputUnits;
    }
    
    public int getOutputUnits() {
        return this.outputUnits;
    }
    
    public void setOutputUnits(final int outputUnits) {
        this.outputUnits = outputUnits;
    }
    
    public int getNumberOfBlock() {
        return this.numberOfBlock;
    }
    
    public void setNumberOfBlock(final int numberOfBlock) {
        this.numberOfBlock = numberOfBlock;
    }
    
    public int getNumberOfCell() {
        return this.numberOfCell;
    }
    
    public void setNumberOfCell(final int numberOfCell) {
        this.numberOfCell = numberOfCell;
    }
    
    public int getNumberOfInput() {
        return this.numberOfInput;
    }
    
    public void setNumberOfInput(final int numberOfInput) {
        this.numberOfInput = numberOfInput;
    }
    
    public BiLSTMObject getLstm() {
        return this.lstm;
    }
    
    public void setLstm(final BiLSTMObject lstm) {
        this.lstm = lstm;
    }
    
    public double[][] getTransition() {
        return this.transition;
    }
    
    public void setTransition(final double[][] transition) {
        this.transition = transition;
    }
    
    public BiOutputLayer getOutputlayer() {
        return this.outputlayer;
    }
    
    public void setOutputlayer(final BiOutputLayer outputlayer) {
        this.outputlayer = outputlayer;
    }
    
    public int getVocabularyDimension() {
        return this.vocabularyDimension;
    }
    
    public void setVocabularyDimension(final int vocabularyDimension) {
        this.vocabularyDimension = vocabularyDimension;
    }
    
    public int getPosLabelDimension() {
        return this.posLabelDimension;
    }
    
    public void setPosLabelDimension(final int posLabelDimension) {
        this.posLabelDimension = posLabelDimension;
    }
    
    public int getPreprocessLabelDimension() {
        return this.preprocessLabelDimension;
    }
    
    public void setPreprocessLabelDimension(final int preprocessLabelDimension) {
        this.preprocessLabelDimension = preprocessLabelDimension;
    }
    
    public HashMap<String, double[]> getVocabularyLookupTable() {
        return this.vocabularyLookupTable;
    }
    
    public void setVocabularyLookupTable(final HashMap<String, double[]> vocabularyLookupTable) {
        this.vocabularyLookupTable = vocabularyLookupTable;
    }
    
    public HashMap<String, double[]> getPosLabelLookupTable() {
        return this.posLabelLookupTable;
    }
    
    public void setPosLabelLookupTable(final HashMap<String, double[]> posLabelLookupTable) {
        this.posLabelLookupTable = posLabelLookupTable;
    }
    
    public HashMap<String, double[]> getPreprocessLabelLookupTable() {
        return this.preprocessLabelLookupTable;
    }
    
    public void setPreprocessLabelLookupTable(final HashMap<String, double[]> preprocessLabelLookupTable) {
        this.preprocessLabelLookupTable = preprocessLabelLookupTable;
    }
    
    public String[] getLabelset() {
        return this.labelset;
    }
    
    public void setLabelset(final String[] labelset) {
        this.labelset = labelset;
    }
    
    public HashMap<String, Integer> getLabelIndex() {
        return this.labelIndex;
    }
    
    public void setLabelIndex(final HashMap<String, Integer> labelIndex) {
        this.labelIndex = labelIndex;
    }
}
