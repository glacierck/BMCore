package flow;

import cn.edu.fudan.flow.CRFSemanticAnalyzer;
import cn.edu.fudan.lang.EventWrapper;
import cn.edu.fudan.lang.IntermediateResult;
import cn.edu.fudan.lang.Result;

public class CRFSemanticAnalyzerStart {
	public static void main(String arg[]) { 
		Result result = null;
	    IntermediateResult intermediateResult = null;
	    String semanticAnalysisResult = null;
	    String[] words = null;
	    String[] labels = null;
		String preprocessFile = "conf/Preprocess.properties";
		String posTaggerFile = "conf/PosTagger.properties";
		String confFile = "crf/crfDecoder.properties";
		String eventKeywordFile = "crf/eventKeywords.utf8";
		String attributeKeywords = "crf/attributeKeywords.utf8";
		String eventType = "约会";
		
		EventWrapper wrapper = new EventWrapper(eventKeywordFile, attributeKeywords);
		
		CRFSemanticAnalyzer semanticAnalyzer = new CRFSemanticAnalyzer(preprocessFile, posTaggerFile, confFile);
		String sentence  = "明天下午六点在牧羊传奇和洪老板吃个火锅吧。";
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
        
        sentence = "这周五一起回复旦大学吃烧烤吧！";
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

//		sentence = "帮我定明天广州到北京的飞机票。";
		sentence = "【东方航空】从白云机场到浦东机场T1，MU5320航班，起飞时间：2016-02-25□20：15。旅客：刘备，票号：781-8534455323。为避免耽误您的行程，建议国内航班至少提前60分钟、国际航班至少提前90分钟到达机场办理乘机手续。";

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

