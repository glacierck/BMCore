package flow;

import cn.edu.fudan.flow.LSTMSemanticAnalyzer;
import cn.edu.fudan.lang.EventWrapper;
import cn.edu.fudan.lang.IntermediateResult;
import cn.edu.fudan.lang.Result;

public class LSTMSemanticAnalyzerStart {
public static void main(String[] args) {
	    Result result = null;
	    IntermediateResult intermediateResult = null;
	    String semanticAnalysisResult = null;
	    String[] words = null;
	    String[] labels = null;
		String preprocessFile = "conf/Preprocess.properties";//中文分词之前会对输入句子进行必要的预处理（包括全角转半角、繁体转简体、数字和字母统一表示形式等）
		String posTaggerFile = "conf/PosTagger.properties";
		String semanticAnalyerFile = "lstm/lstmDecoder.properties";
		String eventKeywordFile = "lstm/eventKeywords.utf8";
		String attributeKeywords = "lstm/attributeKeywords.utf8";
		String eventType = "约会";
		
		EventWrapper wrapper = new EventWrapper(eventKeywordFile, attributeKeywords);
		
		LSTMSemanticAnalyzer semanticAnalyzer = new LSTMSemanticAnalyzer(preprocessFile, posTaggerFile, semanticAnalyerFile);
		String sentence  = "举栗子，下周五一起回复旦大学吃烧烤吧！";
		result =  semanticAnalyzer.semanticAnalyzing(sentence);
		intermediateResult = result.intermediateResult();
		words = result.getWords();
		labels = result.getLables();
		System.out.println("原句：" + sentence);
        System.out.println("词性：" + intermediateResult.getConstriants());
        semanticAnalysisResult = "";
        for (int p = 0; p < labels.length; p++) {
        	semanticAnalysisResult += words[p] + "/" + labels[p] + " ";
        }
        System.out.print("语义：" + semanticAnalysisResult + "\r\n");
        System.out.println(wrapper.wrap(semanticAnalysisResult, eventType));
        
        sentence = "不如明天一起去喜多屋吃个自助，叫上公司金老板。";
        result = semanticAnalyzer.semanticAnalyzing(sentence);
        intermediateResult = result.intermediateResult();
		words = result.getWords();
		labels = result.getLables();
		System.out.println("原句：" + sentence);
        System.out.println("词性：" + intermediateResult.getConstriants());
        semanticAnalysisResult = "";
        for (int p = 0; p < labels.length; p++) {
        	semanticAnalysisResult += words[p] + "/" + labels[p] + " ";
        }
        System.out.print("语义：" + semanticAnalysisResult + "\r\n");
        System.out.println(wrapper.wrap(semanticAnalysisResult, eventType));
        
//        sentence = "明天下午六点在牧羊传奇和洪老板吃个火锅吧。";
	sentence = "IBM今天是2015-08-30，气温25摄氏度，阴有雨。Google发布了Word2Vector工具，请点击http://zhidao.163.com/question/57067131.html。";

	result = semanticAnalyzer.semanticAnalyzing(sentence);
        intermediateResult = result.intermediateResult();
		words = result.getWords();
		labels = result.getLables();
		System.out.println("原句：" + sentence);
        System.out.println("词性：" + intermediateResult.getConstriants());
        semanticAnalysisResult = "";
        for (int p = 0; p < labels.length; p++) {
        	semanticAnalysisResult += words[p] + "/" + labels[p] + " ";
        }
        System.out.print("语义：" + semanticAnalysisResult + "\r\n");
        System.out.println(wrapper.wrap(semanticAnalysisResult, eventType));
	}
}
