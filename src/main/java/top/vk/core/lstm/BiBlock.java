// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lstm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class BiBlock implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int inputUnits;
    private int numberOfCell;
    private int numberOfInput;
    private int length;
    private double[] weightInputGate;
    private double[] weightForgetGate;
    private double[] weightOutputGate;
    private double biasInputGate;
    private double biasForgetGate;
    private double biasOutputGate;
    private ArrayList<BiCell> cells;
    private double[] inputGate;
    private double[] forgetGate;
    private double[] outputGate;
    private double[][] inputs;
    private BiCell cell;
    private double[] derivInputGate;
    private double[] derivForgetGate;
    private double[] derivOutputGate;
    private double[] derivWeightInputGate;
    private double[] derivWeightForgetGate;
    private double[] derivWeightOutputGate;
    private double derivBiasInputGate;
    private double derivBiasForgetGate;
    private double derivBiasOutputGate;
    private double[][] derivInputs;
    private double[] derivInputGateFromCell;
    private double[] derivForgetGateFromCell;
    private double[] derivOutputGateFromCell;
    private double[][] derivInputFromCell;
    
    public BiBlock(final int inputUnits, final int numberOfCell, final int numberOfInput) {
        this.inputUnits = inputUnits;
        this.numberOfCell = numberOfCell;
        this.numberOfInput = numberOfInput;
        this.init();
    }
    
    private void init() {
        this.biasInputGate = 0.0;
        this.biasForgetGate = -1.0;
        this.biasOutputGate = 1.0;
        this.weightInputGate = new double[this.numberOfInput];
        this.weightForgetGate = new double[this.numberOfInput];
        this.weightOutputGate = new double[this.numberOfInput];
        final Random randomgen = new Random();
        for (int i = 0; i < this.numberOfInput; ++i) {
            this.weightInputGate[i] = (randomgen.nextDouble() - 0.5) * 2.0 / this.numberOfInput;
            this.weightForgetGate[i] = (randomgen.nextDouble() - 0.5) * 2.0 / this.numberOfInput;
            this.weightOutputGate[i] = (randomgen.nextDouble() - 0.5) * 2.0 / this.numberOfInput;
        }
        this.cells = new ArrayList<BiCell>();
        for (int i = 0; i < this.numberOfCell; ++i) {
            this.cell = new BiCell(this, this.inputUnits, this.numberOfInput);
            this.cells.add(this.cell);
        }
    }
    
    public void reset(final int length, final double[][] inputs) {
        this.length = length;
        this.inputs = inputs;
        this.inputGate = new double[length];
        this.forgetGate = new double[length];
        this.outputGate = new double[length];
        for (int i = 0; i < this.numberOfCell; ++i) {
            (this.cell = this.cells.get(i)).reset(length, inputs);
        }
    }
    
    public void resetDeriv() {
        for (int i = 0; i < this.numberOfCell; ++i) {
            (this.cell = this.cells.get(i)).resetDeriv();
        }
        this.derivWeightInputGate = new double[this.numberOfInput];
        this.derivWeightForgetGate = new double[this.numberOfInput];
        this.derivWeightOutputGate = new double[this.numberOfInput];
        this.derivBiasInputGate = 0.0;
        this.derivBiasForgetGate = 0.0;
        this.derivBiasOutputGate = 0.0;
        this.derivInputs = new double[this.length][this.inputUnits];
        this.derivInputGate = new double[this.length];
        this.derivForgetGate = new double[this.length];
        this.derivOutputGate = new double[this.length];
    }
    
    public void clean() {
        for (int i = 0; i < this.numberOfCell; ++i) {
            this.cells.get(i).clean();
        }
        this.derivWeightInputGate = null;
        this.derivWeightForgetGate = null;
        this.derivWeightOutputGate = null;
        this.derivBiasInputGate = 0.0;
        this.derivBiasForgetGate = 0.0;
        this.derivBiasOutputGate = 0.0;
        this.derivInputs = null;
        this.derivInputGate = null;
        this.derivForgetGate = null;
        this.derivOutputGate = null;
    }
    
    public void computeOutput(final int p) {
        this.computeGate(p);
        for (int c = 0; c < this.numberOfCell; ++c) {
            (this.cell = this.cells.get(c)).computeNetCellInpute(p);
            this.cell.computeCellState(p);
            this.cell.computeCellOutput(p);
        }
    }
    
    private void computeGate(final int p) {
        for (int d = 0; d < this.numberOfInput; ++d) {
            final double[] inputGate = this.inputGate;
            inputGate[p] += this.weightInputGate[d] * this.inputs[p][d];
            final double[] forgetGate = this.forgetGate;
            forgetGate[p] += this.weightForgetGate[d] * this.inputs[p][d];
            final double[] outputGate = this.outputGate;
            outputGate[p] += this.weightOutputGate[d] * this.inputs[p][d];
        }
        final double[] inputGate2 = this.inputGate;
        inputGate2[p] += this.biasInputGate;
        final double[] forgetGate2 = this.forgetGate;
        forgetGate2[p] += this.biasForgetGate;
        final double[] outputGate2 = this.outputGate;
        outputGate2[p] += this.biasOutputGate;
        this.inputGate[p] = BiNonlinear.sigmoid(this.inputGate[p]);
        this.forgetGate[p] = BiNonlinear.sigmoid(this.forgetGate[p]);
        this.outputGate[p] = BiNonlinear.sigmoid(this.outputGate[p]);
    }
    
    public void computeDeriv() {
        for (int c = 0; c < this.numberOfCell; ++c) {
            this.cell = this.cells.get(c);
            this.derivInputGateFromCell = this.cell.getDerivInputGate();
            this.derivForgetGateFromCell = this.cell.getDerivForgetGate();
            this.derivOutputGateFromCell = this.cell.getDerivOutputGate();
            this.derivInputFromCell = this.cell.getDerivInput();
            for (int p = 0; p < this.length; ++p) {
                final double[] derivInputGate = this.derivInputGate;
                final int n = p;
                derivInputGate[n] += this.derivInputGateFromCell[p];
                final double[] derivForgetGate = this.derivForgetGate;
                final int n2 = p;
                derivForgetGate[n2] += this.derivForgetGateFromCell[p];
                final double[] derivOutputGate = this.derivOutputGate;
                final int n3 = p;
                derivOutputGate[n3] += this.derivOutputGateFromCell[p];
                for (int i = 0; i < this.inputUnits; ++i) {
                    final double[] array = this.derivInputs[p];
                    final int n4 = i;
                    array[n4] += this.derivInputFromCell[p][i];
                }
            }
        }
        for (int p2 = 0; p2 < this.length; ++p2) {
            this.derivInputGate[p2] = this.derivInputGate[p2] * this.inputGate[p2] * (1.0 - this.inputGate[p2]) / this.numberOfCell;
            this.derivForgetGate[p2] = this.derivForgetGate[p2] * this.forgetGate[p2] * (1.0 - this.forgetGate[p2]) / this.numberOfCell;
            this.derivOutputGate[p2] = this.derivOutputGate[p2] * this.outputGate[p2] * (1.0 - this.outputGate[p2]) / this.numberOfCell;
            this.derivBiasInputGate += this.derivInputGate[p2];
            this.derivBiasForgetGate += this.derivForgetGate[p2];
            this.derivBiasOutputGate += this.derivOutputGate[p2];
        }
        this.derivBiasInputGate /= this.length;
        this.derivBiasForgetGate /= this.length;
        this.derivBiasOutputGate /= this.length;
        for (int p2 = 0; p2 < this.length; ++p2) {
            for (int d = 0; d < this.numberOfInput; ++d) {
                final double[] derivWeightInputGate = this.derivWeightInputGate;
                final int n5 = d;
                derivWeightInputGate[n5] += this.derivInputGate[p2] * this.inputs[p2][d];
                final double[] derivWeightForgetGate = this.derivWeightForgetGate;
                final int n6 = d;
                derivWeightForgetGate[n6] += this.derivForgetGate[p2] * this.inputs[p2][d];
                final double[] derivWeightOutputGate = this.derivWeightOutputGate;
                final int n7 = d;
                derivWeightOutputGate[n7] += this.derivOutputGate[p2] * this.inputs[p2][d];
                if (d < this.inputUnits) {
                    final double[] array2 = this.derivInputs[p2];
                    final int n8 = d;
                    array2[n8] += this.derivInputGate[p2] * this.weightInputGate[d];
                    final double[] array3 = this.derivInputs[p2];
                    final int n9 = d;
                    array3[n9] += this.derivForgetGate[p2] * this.weightForgetGate[d];
                    final double[] array4 = this.derivInputs[p2];
                    final int n10 = d;
                    array4[n10] += this.derivOutputGate[p2] * this.weightOutputGate[d];
                }
            }
        }
    }
    
    public void update(final double learningRate) {
        for (int c = 0; c < this.numberOfCell; ++c) {
            (this.cell = this.cells.get(c)).update(learningRate);
        }
        this.biasInputGate += learningRate * this.derivBiasInputGate;
        this.biasForgetGate += learningRate * this.derivBiasForgetGate;
        this.biasOutputGate += learningRate * this.derivBiasOutputGate;
        for (int i = 0; i < this.numberOfInput; ++i) {
            final double[] weightInputGate = this.weightInputGate;
            final int n = i;
            weightInputGate[n] += learningRate * this.derivWeightInputGate[i];
            final double[] weightForgetGate = this.weightForgetGate;
            final int n2 = i;
            weightForgetGate[n2] += learningRate * this.derivWeightForgetGate[i];
            final double[] weightOutputGate = this.weightOutputGate;
            final int n3 = i;
            weightOutputGate[n3] += learningRate * this.derivWeightOutputGate[i];
        }
    }
    
    public void regularize(double regularizationRate) {
        for (int c = 0; c < this.numberOfCell; ++c) {
            (this.cell = this.cells.get(c)).regularize(regularizationRate);
        }
        regularizationRate = 1.0 - regularizationRate;
        this.biasInputGate *= regularizationRate;
        this.biasForgetGate *= regularizationRate;
        this.biasOutputGate *= regularizationRate;
        for (int i = 0; i < this.numberOfInput; ++i) {
            this.weightInputGate[i] *= regularizationRate;
            this.weightForgetGate[i] *= regularizationRate;
            this.weightOutputGate[i] *= regularizationRate;
        }
    }
    
    public double getInputGate(final int p) {
        return this.inputGate[p];
    }
    
    public double getForgetGate(final int p) {
        return this.forgetGate[p];
    }
    
    public double getOutputGate(final int p) {
        return this.outputGate[p];
    }
    
    public ArrayList<BiCell> getCells() {
        return this.cells;
    }
    
    public double[][] getDerivInputs() {
        return this.derivInputs;
    }
}
