// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.flow;

import top.vk.core.dnn.WindowConvolutionNetworkDecoder;
import top.vk.core.lang.IntermediateResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class WordSegmentor
{
    private String preprocessFile;
    private String wordSegmentorFile;
    private Preprocess preprocess;
    private WindowConvolutionNetworkDecoder wordsegmentor;
    private IntermediateResult intermediateResult;
    
    public WordSegmentor(final String preprocessFile, final String wordSegmentorFile) {
        this.preprocessFile = null;
        this.wordSegmentorFile = null;
        this.preprocess = null;
        this.wordsegmentor = null;
        this.intermediateResult = null;
        this.preprocessFile = preprocessFile;
        this.wordSegmentorFile = wordSegmentorFile;
        this.preprocess = new Preprocess(preprocessFile);
        final Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(wordSegmentorFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        (this.wordsegmentor = new WindowConvolutionNetworkDecoder(prop.getProperty("inputNetworkSettingFile"))).setCharacterLevel(Boolean.parseBoolean(prop.getProperty("isCharacterLevel")));
        this.wordsegmentor.setResultWithTag(Boolean.parseBoolean(prop.getProperty("isResultWithTag")));
        this.wordsegmentor.setSegmentation(Boolean.parseBoolean(prop.getProperty("isSegmentation")));
        this.wordsegmentor.setStandard(Boolean.parseBoolean(prop.getProperty("isStandard")));
        this.wordsegmentor.readPara();
    }
    
    public IntermediateResult segmentation(String sentence) {
        sentence = this.preprocess.normalize(sentence);
        sentence = this.preprocess.replaceSlang(sentence);
        (this.intermediateResult = this.preprocess.processForSegmentation(sentence)).setConstriants(this.wordsegmentor.decodeSentence(sentence, this.intermediateResult.getConstriants()));
        return this.intermediateResult;
    }
    
    public String getPreprocessFile() {
        return this.preprocessFile;
    }
    
    public void setPreprocessFile(final String preprocessFile) {
        this.preprocessFile = preprocessFile;
    }
    
    public String getWordSegmentorFile() {
        return this.wordSegmentorFile;
    }
    
    public void setWordSegmentorFile(final String wordSegmentorFile) {
        this.wordSegmentorFile = wordSegmentorFile;
    }
}
