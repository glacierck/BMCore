// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lstm;

import java.io.Serializable;
import java.util.ArrayList;

public class BiLSTMObject implements Serializable
{
    private static final long serialVersionUID = 1L;
    private ArrayList<BiBlock> lstm_lr;
    private ArrayList<BiBlock> lstm_rl;
    
    public BiLSTMObject(final ArrayList<BiBlock> lstm_lr, final ArrayList<BiBlock> lstm_rl) {
        this.lstm_lr = lstm_lr;
        this.lstm_rl = lstm_rl;
    }
    
    public ArrayList<BiBlock> getLstm_lr() {
        return this.lstm_lr;
    }
    
    public void setLstm_lr(final ArrayList<BiBlock> lstm_lr) {
        this.lstm_lr = lstm_lr;
    }
    
    public ArrayList<BiBlock> getLstm_rl() {
        return this.lstm_rl;
    }
    
    public void setLstm_rl(final ArrayList<BiBlock> lstm_rl) {
        this.lstm_rl = lstm_rl;
    }
}
