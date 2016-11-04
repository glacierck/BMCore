// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lstm;

import java.util.ArrayList;

public class BiInput
{
    private ArrayList<double[]> exInput;
    
    public BiInput(final ArrayList<double[]> exInput) {
        this.exInput = exInput;
    }
    
    public ArrayList<double[]> getExInput() {
        return this.exInput;
    }
    
    public void setExInput(final ArrayList<double[]> exInput) {
        this.exInput = exInput;
    }
}
