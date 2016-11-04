// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lstm;

import java.io.Serializable;
import java.util.Random;

public class BiOutputLayer implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int outputUnits;
    private double[][] weightOutput;
    private double[] biasOutput;
    private int outputCellUnits;
    private double[][] outputCell_lr;
    private double[][] outputCell_rl;
    private double[][] output;
    private double[][] derivWeightOutput;
    private double[] derivBiasOutput;
    private double[][] derivOutputCell_lr;
    private double[][] derivOutputCell_rl;
    private int length;
    
    public BiOutputLayer(final int numberOfBlock, final int numberOfCell, final int outputUnits) {
        this.outputCellUnits = numberOfBlock * numberOfCell;
        this.outputUnits = outputUnits;
        this.weightOutput = new double[outputUnits][this.outputCellUnits * 2];
        this.biasOutput = new double[outputUnits];
        this.init();
    }
    
    private void init() {
        final Random randomgen = new Random();
        for (int t = 0; t < this.outputUnits; ++t) {
            for (int c = 0; c < this.outputCellUnits * 2; ++c) {
                this.weightOutput[t][c] = (randomgen.nextDouble() - 0.5) / this.outputCellUnits;
            }
        }
        for (int t = 0; t < this.outputUnits; ++t) {
            this.biasOutput[t] = 0.0;
        }
    }
    
    public void reset(final int length, final double[][] outputCell_lr, final double[][] outputCell_rl) {
        this.length = length;
        this.outputCell_lr = outputCell_lr;
        this.outputCell_rl = outputCell_rl;
        this.output = new double[length][this.outputUnits];
    }
    
    public void resetDeive() {
        this.derivBiasOutput = new double[this.outputUnits];
        this.derivWeightOutput = new double[this.outputUnits][this.outputCellUnits * 2];
        this.derivOutputCell_lr = new double[this.outputCellUnits][this.length];
        this.derivOutputCell_rl = new double[this.outputCellUnits][this.length];
    }
    
    public void clean() {
        this.derivBiasOutput = null;
        this.derivWeightOutput = null;
        this.derivOutputCell_lr = null;
        this.derivOutputCell_rl = null;
    }
    
    public void computeOutput() {
        for (int p = 0; p < this.length; ++p) {
            for (int t = 0; t < this.outputUnits; ++t) {
                for (int c = 0; c < this.outputCellUnits * 2; ++c) {
                    if (c < this.outputCellUnits) {
                        final double[] array = this.output[p];
                        final int n = t;
                        array[n] += this.weightOutput[t][c] * this.outputCell_lr[p][c];
                    }
                    else {
                        final double[] array2 = this.output[p];
                        final int n2 = t;
                        array2[n2] += this.weightOutput[t][c] * this.outputCell_rl[this.length - p - 1][c - this.outputCellUnits];
                    }
                }
                final double[] array3 = this.output[p];
                final int n3 = t;
                array3[n3] += this.biasOutput[t];
            }
        }
    }
    
    public void computeDeriv(final double[][] derivOutput) {
        for (int p = 0; p < this.length; ++p) {
            for (int t = 0; t < this.outputUnits; ++t) {
                final double[] derivBiasOutput = this.derivBiasOutput;
                final int n = t;
                derivBiasOutput[n] += derivOutput[p][t];
                for (int c = 0; c < this.outputCellUnits * 2; ++c) {
                    if (c < this.outputCellUnits) {
                        final double[] array = this.derivWeightOutput[t];
                        final int n2 = c;
                        array[n2] += derivOutput[p][t] * this.outputCell_lr[p][c];
                        final double[] array2 = this.derivOutputCell_lr[c];
                        final int n3 = p;
                        array2[n3] += derivOutput[p][t] * this.weightOutput[t][c];
                    }
                    else {
                        final double[] array3 = this.derivWeightOutput[t];
                        final int n4 = c;
                        array3[n4] += derivOutput[p][t] * this.outputCell_rl[this.length - p - 1][c - this.outputCellUnits];
                        final double[] array4 = this.derivOutputCell_rl[c - this.outputCellUnits];
                        final int n5 = this.length - p - 1;
                        array4[n5] += derivOutput[p][t] * this.weightOutput[t][c];
                    }
                }
            }
        }
        for (int t2 = 0; t2 < this.outputUnits; ++t2) {
            this.derivBiasOutput[t2] /= this.length;
        }
    }
    
    public void update(final double learningRate) {
        for (int t = 0; t < this.outputUnits; ++t) {
            final double[] biasOutput = this.biasOutput;
            final int n = t;
            biasOutput[n] += learningRate * this.derivBiasOutput[t];
            for (int c = 0; c < this.outputCellUnits * 2; ++c) {
                final double[] array = this.weightOutput[t];
                final int n2 = c;
                array[n2] += learningRate * this.derivWeightOutput[t][c];
            }
        }
    }
    
    public void regularize(double regularizationRate) {
        regularizationRate = 1.0 - regularizationRate;
        for (int t = 0; t < this.outputUnits; ++t) {
            this.biasOutput[t] *= regularizationRate;
            for (int c = 0; c < this.outputCellUnits * 2; ++c) {
                this.weightOutput[t][c] *= regularizationRate;
            }
        }
    }
    
    public double[][] getOutput() {
        return this.output;
    }
    
    public double[][] getDerivOutputCell_lr() {
        return this.derivOutputCell_lr;
    }
    
    public double[][] getDerivOutputCell_rl() {
        return this.derivOutputCell_rl;
    }
}
