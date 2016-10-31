package sentence;

import cn.edu.fudan.sentence.ConvolutionalSentenceModelDecoder;
import cn.edu.fudan.sentence.ConvolutionalSentenceModelPreprocess;

public class ConvolutionalSentenceModelDecoderStart {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String preprocessFile = "conf/Preprocess.properties";
		String posTaggerFile = "conf/PosTagger.properties";
		String inputNetworkSettingFile = "model/sentenceModel.class";
		ConvolutionalSentenceModelPreprocess preprocess = new ConvolutionalSentenceModelPreprocess(preprocessFile, posTaggerFile);
		ConvolutionalSentenceModelDecoder decoder = new ConvolutionalSentenceModelDecoder(inputNetworkSettingFile);
		decoder.readPara();
		
		String sentence = "您工商银行账户2345于2016年1月21日消费人民币201.35元，余额为234774.21元。"; // 付款提示
		String result = decoder.decodeSentence(preprocess.preprocess(sentence));
		System.out.println(result + ": " + sentence);
		
		sentence = "今晚九点去张江镇吃烤羊腿或者扇贝王都可以啊！"; // 约会短信
		result = decoder.decodeSentence(preprocess.preprocess(sentence));
		System.out.println(result + ": " + sentence);
		
		sentence = "气象台27日6时：阴天，今天上午转阴有小雨，明天阴有雨。偏东风2-3级，明天转偏北风3-4级。今天最高温度6度，明天最低温度6度。"; // 天气预报
//		sentence = "我是曹操，新手机号码为18576704846，旧号已停用，常联系！"; // 天气预报
		result = decoder.decodeSentence(preprocess.preprocess(sentence));
		System.out.println(result + ": " + sentence);

		sentence = "预定明天广州到上海的飞机票"; //
		result = decoder.decodeSentence(preprocess.preprocess(sentence));
		System.out.println(result + ": " + sentence);
	}
}
