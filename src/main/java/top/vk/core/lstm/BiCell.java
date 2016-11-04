// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lstm;

import java.io.Serializable;
import java.util.Random;

public class BiCell implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int inputUnits;
    private double[] weightCell;
    private double biasCell;
    private int numberOfInput;
    private BiBlock block;
    private double[] state;
    private double[] inputNetCell;
    private double[] outputCell;
    private double[][] inputs;
    private int length;
    private double[] derivState;
    private double[] derivInputGate;
    private double[] derivOutputGate;
    private double[] derivForgetGate;
    private double[] derivInputNetCell;
    private double[] deriveWeightCell;
    private double derivBiasCell;
    private double[][] derivInput;
    private boolean isSigmoid;
    
    public BiCell(final BiBlock block, final int inputUnits, final int numberOfInput) {
        this.isSigmoid = false;
        this.block = block;
        this.inputUnits = inputUnits;
        this.numberOfInput = numberOfInput;
        this.init();
    }
    
    private void init() {
        this.biasCell = 0.0;
        this.weightCell = new double[this.numberOfInput];
        final Random randomgen = new Random();
        for (int i = 0; i < this.numberOfInput; ++i) {
            this.weightCell[i] = (randomgen.nextDouble() - 0.5) * 2.0 / this.numberOfInput;
        }
    }
    
    public void reset(final int length, final double[][] inputs) {
        this.length = length;
        this.inputs = inputs;
        this.state = new double[length];
        this.inputNetCell = new double[length];
        this.outputCell = new double[length];
    }
    
    public void resetDeriv() {
        this.derivState = new double[this.length];
        this.derivInputGate = new double[this.length];
        this.derivOutputGate = new double[this.length];
        this.derivForgetGate = new double[this.length];
        this.derivInputNetCell = new double[this.length];
        this.deriveWeightCell = new double[this.numberOfInput];
        this.derivBiasCell = 0.0;
        this.derivInput = new double[this.length][this.inputUnits];
    }
    
    public void clean() {
        this.derivState = null;
        this.derivInputGate = null;
        this.derivOutputGate = null;
        this.derivForgetGate = null;
        this.derivInputNetCell = null;
        this.deriveWeightCell = null;
        this.derivBiasCell = 0.0;
        this.derivInput = null;
    }
    
    public void computeNetCellInpute(final int p) {
        for (int d = 0; d < this.numberOfInput; ++d) {
            final double[] inputNetCell = this.inputNetCell;
            inputNetCell[p] += this.weightCell[d] * this.inputs[p][d];
        }
        final double[] inputNetCell2 = this.inputNetCell;
        inputNetCell2[p] += this.biasCell;
        if (this.isSigmoid) {
            this.inputNetCell[p] = BiNonlinear.sigmoid(this.inputNetCell[p]);
        }
        else {
            this.inputNetCell[p] = BiNonlinear.hardTanh(this.inputNetCell[p]);
        }
    }
    
    public void computeCellState(final int p) {
        if (p == 0) {
            this.state[p] = this.block.getInputGate(p) * this.inputNetCell[p];
        }
        else {
            this.state[p] = this.block.getForgetGate(p) * this.state[p - 1] + this.block.getInputGate(p) * this.inputNetCell[p];
        }
    }
    
    public void computeCellOutput(final int p) {
        this.outputCell[p] = this.block.getOutputGate(p) * this.state[p];
    }
    
    public void computeDeriv(final double[] derivOutputCell) {
        for (int p = 0; p < this.length; ++p) {
            this.derivState[p] = derivOutputCell[p] * this.block.getOutputGate(p);
            this.derivOutputGate[p] = derivOutputCell[p] * this.state[p];
            if (p != 0) {
                this.derivForgetGate[p] = this.derivState[p] * this.state[p - 1];
            }
            this.derivInputGate[p] = this.derivState[p] * this.inputNetCell[p];
            this.derivInputNetCell[p] = this.derivState[p] * this.block.getInputGate(p);
            if (this.isSigmoid) {
                this.derivInputNetCell[p] = this.derivInputNetCell[p] * this.inputNetCell[p] * (1.0 - this.inputNetCell[p]);
            }
            else if (this.inputNetCell[p] >= 1.0 || this.inputNetCell[p] <= -1.0) {
                this.derivInputNetCell[p] = 0.0;
            }
            this.derivBiasCell += this.derivInputNetCell[p];
            for (int d = 0; d < this.numberOfInput; ++d) {
                final double[] deriveWeightCell = this.deriveWeightCell;
                final int n = d;
                deriveWeightCell[n] += this.derivInputNetCell[p] * this.inputs[p][d];
                if (d < this.inputUnits) {
                    final double[] array = this.derivInput[p];
                    final int n2 = d;
                    array[n2] += this.derivInputNetCell[p] * this.weightCell[d];
                }
            }
        }
        this.derivBiasCell /= this.length;
    }
    
    public void update(final double learningRate) {
        this.biasCell += learningRate * this.derivBiasCell;
        for (int i = 0; i < this.numberOfInput; ++i) {
            final double[] weightCell = this.weightCell;
            final int n = i;
            weightCell[n] += learningRate * this.deriveWeightCell[i];
        }
    }
    
    public void regularize(double regularizationRate) {
        regularizationRate = 1.0 - regularizationRate;
        this.biasCell *= regularizationRate;
        for (int i = 0; i < this.numberOfInput; ++i) {
            this.weightCell[i] *= regularizationRate;
        }
    }
    
    public void setInputs(final double[][] inputs) {
        this.inputs = inputs;
    }
    
    public double getOutputCell(final int p) {
        if (p < 0 || p >= this.length) {
            return 0.0;
        }
        return this.outputCell[p];
    }
    
    public double[] getDerivInputGate() {
        return this.derivInputGate;
    }
    
    public double[] getDerivOutputGate() {
        return this.derivOutputGate;
    }
    
    public double[] getDerivForgetGate() {
        return this.derivForgetGate;
    }
    
    public double[][] getDerivInput() {
        return this.derivInput;
    }
}
