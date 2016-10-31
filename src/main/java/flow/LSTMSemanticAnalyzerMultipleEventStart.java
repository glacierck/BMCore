package flow;

import cn.edu.fudan.flow.LSTMSemanticAnalyzerMultipleEvent;
import cn.edu.fudan.lang.EventWrapper;
import cn.edu.fudan.lang.IntermediateResult;
import cn.edu.fudan.lang.Result;

public class LSTMSemanticAnalyzerMultipleEventStart {
	public static void main(String arg[]) { 
		Result result = null;
	    IntermediateResult intermediateResult = null;
	    String semanticAnalysisResult = null;
	    String[] words = null;
	    String[] labels = null;
		String preprocessFile = "conf/Preprocess.properties";
		String posTaggerFile = "conf/PosTagger.properties";
		String confFile = "lstm/bi_lstm_multiple.properties";
		String eventKeywordFile = "lstm/eventKeywords.utf8";
		String attributeKeywords = "lstm/attributeKeywords.utf8";
		String sentenceModelSettingFile = "model/sentenceModel.class";
		String unknown = "E_UNKNOWN";
		String unknownNotice = "结果：未知事件。";
		
		EventWrapper wrapper = new EventWrapper(eventKeywordFile, attributeKeywords);
		
		LSTMSemanticAnalyzerMultipleEvent semanticAnalyzer = new LSTMSemanticAnalyzerMultipleEvent(preprocessFile, posTaggerFile, sentenceModelSettingFile, confFile);
		String sentence="";
		/* sentence  = "温馨提示：截至03月10日16时57分，您当月已使用流量1674.28MB，其中套餐外流量1024.27MB，按0.0003元/KB收取。";
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
        
        sentence = "接上级的会议通知，要求4月7日（周五）上午9点在第三会议室召开部门会议请您参会。";
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
        */
        sentence = "您预订的香港机场到哥打基纳巴卢机场的机票已购票完成，2015-09-01，香港国际机场到哥打基纳巴卢机场的航班AK236。订单号ysk150818194243087e7de0，请至少提前三小时到达机场凭购票时所填证件办理登机手续。";
//        sentence = "【水果网】您购买的苹果已经下单成功，明天12点将准时送达地香港国际机场。账单号201020231101。";
//        sentence = "麻烦帮我查一下我的飞机到点起飞没有，订单号ysk150818194243087e7de0。";
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


		sentence = "预订明天广州到深圳的飞机票。";
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

