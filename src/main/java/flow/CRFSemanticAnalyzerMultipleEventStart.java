package flow;

import cn.edu.fudan.flow.CRFSemanticAnalyzerMultipleEvent;
import cn.edu.fudan.lang.EventWrapper;
import cn.edu.fudan.lang.IntermediateResult;
import cn.edu.fudan.lang.Result;

public class CRFSemanticAnalyzerMultipleEventStart {
	public static void main(String arg[]) { 
		Result result = null;
	    IntermediateResult intermediateResult = null;
	    String semanticAnalysisResult = null;
	    String[] words = null;
	    String[] labels = null;
		String preprocessFile = "conf/Preprocess.properties";
		String posTaggerFile = "conf/PosTagger.properties";
		String confFile = "crf/crfDecoder_multiple.properties";
		String eventKeywordFile = "crf/eventKeywords.utf8";
		String attributeKeywords = "crf/attributeKeywords.utf8";
		String sentenceModelSettingFile = "model/sentenceModel.class";
		String unknown = "E_UNKNOWN";
		String unknownNotice = "结果：未知事件。";
		
		EventWrapper wrapper = new EventWrapper(eventKeywordFile, attributeKeywords);
		
		CRFSemanticAnalyzerMultipleEvent semanticAnalyzer = new CRFSemanticAnalyzerMultipleEvent(preprocessFile, posTaggerFile, sentenceModelSettingFile, confFile);
		String sentence  = "温馨提示：截至03月10日16时57分，您当月已使用流量1674.28MB，其中套餐外流量1024.27MB，按0.0003元/KB收取。";
		result =  semanticAnalyzer.semanticAnalyzing(sentence);
		if (result.getEvevtType().equals(unknown)) {
			System.out.println("原句：" + sentence);
			System.out.println(unknownNotice);
		}
		else {
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
	        System.out.println(wrapper.wrap(semanticAnalysisResult, result.getEvevtType()));
		}
		
        
        sentence = "您工商银行账户2345于2016年1月21日消费人民币201.35元，余额为234774.21元。";
        result =  semanticAnalyzer.semanticAnalyzing(sentence);
        if (result.getEvevtType().equals(unknown)) {
			System.out.println("原句：" + sentence);
			System.out.println(unknownNotice);
		}
		else {
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
	        System.out.println(wrapper.wrap(semanticAnalysisResult, result.getEvevtType()));
		}
        
        sentence = "[艺龙]确认：订单347313849赵云1月29日至31日全季酒店（上海张江店），双床房1间总价￥606.00；地址：上海市浦东新区张衡路1717号（近伽利略路）021-68587777；保留到18点；取消订单回短信\"QXQX4\"。";
        result = semanticAnalyzer.semanticAnalyzing(sentence);
        if (result.getEvevtType().equals(unknown)) {
			System.out.println("原句：" + sentence);
			System.out.println(unknownNotice);
		}
		else {
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
	        System.out.println(wrapper.wrap(semanticAnalysisResult, result.getEvevtType()));
		}
        
//        sentence = "【东方航空】从白云机场到浦东机场T1，MU5320航班，起飞时间：2016-02-25□20：15。旅客：刘备，票号：781-8534455323。为避免耽误您的行程，建议国内航班至少提前60分钟、国际航班至少提前90分钟到达机场办理乘机手续。";
        sentence = "帮我预定明天广州到北京的飞机票";
        result =  semanticAnalyzer.semanticAnalyzing(sentence);
        if (result.getEvevtType().equals(unknown)) {
			System.out.println("原句：" + sentence);
			System.out.println(unknownNotice);
		}
		else {
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
	        System.out.println(wrapper.wrap(semanticAnalysisResult, result.getEvevtType()));
		}
        
        sentence = "订票系统真是烂倒家了，怎么都上不去。";
        result =  semanticAnalyzer.semanticAnalyzing(sentence);
        if (result.getEvevtType().equals(unknown)) {
			System.out.println("原句：" + sentence);
			System.out.println(unknownNotice);
		}
		else {
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
	        System.out.println(wrapper.wrap(semanticAnalysisResult, result.getEvevtType()));
		}
        
	}
}

