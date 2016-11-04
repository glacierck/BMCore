// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lstm;

import java.util.ArrayList;

public class BiInputOutputPair
{
    private ArrayList<double[]> exInput;
    private int exOutput;
    
    public BiInputOutputPair(final ArrayList<double[]> exInput, final int exOutput) {
        this.exInput = exInput;
        this.exOutput = exOutput;
    }
    
    public ArrayList<double[]> getExInput() {
        return this.exInput;
    }
    
    public void setExInput(final ArrayList<double[]> exInput) {
        this.exInput = exInput;
    }
    
    public int getExOutput() {
        return this.exOutput;
    }
    
    public void setExOutput(final int exOutput) {
        this.exOutput = exOutput;
    }
}
