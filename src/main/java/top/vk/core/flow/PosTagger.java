// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.flow;

import top.vk.core.dnn.WindowConvolutionNetworkDecoder;
import top.vk.core.lang.IntermediateResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PosTagger
{
    private String preprocessFile;
    private String posTaggerFile;
    private Preprocess preprocess;
    private WindowConvolutionNetworkDecoder posTagger;
    private IntermediateResult intermediateResult;
    
    public PosTagger(final String preprocessFile, final String posTaggerFile) {
        this.preprocessFile = null;
        this.posTaggerFile = null;
        this.preprocess = null;
        this.posTagger = null;
        this.intermediateResult = null;
        this.preprocessFile = preprocessFile;
        this.posTaggerFile = posTaggerFile;
        this.preprocess = new Preprocess(preprocessFile);
        final Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(posTaggerFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        (this.posTagger = new WindowConvolutionNetworkDecoder(prop.getProperty("inputNetworkSettingFile"))).setCharacterLevel(Boolean.parseBoolean(prop.getProperty("isCharacterLevel")));
        this.posTagger.setResultWithTag(Boolean.parseBoolean(prop.getProperty("isResultWithTag")));
        this.posTagger.setSegmentation(Boolean.parseBoolean(prop.getProperty("isSegmentation")));
        this.posTagger.setStandard(Boolean.parseBoolean(prop.getProperty("isStandard")));
        this.posTagger.readPara();
    }
    
    public IntermediateResult posTagging(String sentence) {
        sentence = this.preprocess.normalize(sentence);
        sentence = this.preprocess.replaceSlang(sentence);
        (this.intermediateResult = this.preprocess.processForPosTagging(sentence)).setConstriants(this.posTagger.decodeSentence(sentence, this.intermediateResult.getConstriants()));
        return this.intermediateResult;
    }
    
    public String getPreprocessFile() {
        return this.preprocessFile;
    }
    
    public void setPreprocessFile(final String preprocessFile) {
        this.preprocessFile = preprocessFile;
    }
    
    public String getPosTaggerFile() {
        return this.posTaggerFile;
    }
    
    public void setPosTaggerFile(final String posTaggerFile) {
        this.posTaggerFile = posTaggerFile;
    }
}
