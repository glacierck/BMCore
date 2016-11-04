// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lstm;

public class BiNonlinear
{
    public static double sigmoid(final double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
    
    public static double hardTanh(final double value) {
        if (value < -1.0) {
            return -1.0;
        }
        if (value > 1.0) {
            return 1.0;
        }
        return value;
    }
}
