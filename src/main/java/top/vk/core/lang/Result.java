// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lang;

public class Result
{
    private String evevtType;
    private IntermediateResult intermediateResult;
    private String[] words;
    private String[] lables;
    
    public Result(final String evevtType, final IntermediateResult intermediateResult, final String[] words, final String[] lables) {
        this.evevtType = evevtType;
        this.intermediateResult = intermediateResult;
        this.words = words;
        this.lables = lables;
    }
    
    public IntermediateResult intermediateResult() {
        return this.intermediateResult;
    }
    
    public void setIntermediateResult(final IntermediateResult intermediateResult) {
        this.intermediateResult = intermediateResult;
    }
    
    public String[] getWords() {
        return this.words;
    }
    
    public void setWords(final String[] words) {
        this.words = words;
    }
    
    public String[] getLables() {
        return this.lables;
    }
    
    public void setLables(final String[] lables) {
        this.lables = lables;
    }
    
    public String getEvevtType() {
        return this.evevtType;
    }
    
    public void setEvevtType(final String evevtType) {
        this.evevtType = evevtType;
    }
}
