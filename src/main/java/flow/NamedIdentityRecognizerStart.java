package flow;

import cn.edu.fudan.flow.NamedIdentityRecognizer;
import cn.edu.fudan.lang.IntermediateResult;

public class NamedIdentityRecognizerStart {
	public static void main(String[] args) {
		
		String preprocessFile = "conf/Preprocess.properties";
		String nerRecogizerFile = "conf/NerRecognizer.properties";
		NamedIdentityRecognizer nerRecognizer = new NamedIdentityRecognizer(preprocessFile, nerRecogizerFile);
		String sentence  = "【中国农业银行】李嘉欣在上海市国权路举栗子：2015年世界旅游小姐比赛山东赛区冠军总决赛在威海举行，鸭梨山大啊。";
		IntermediateResult result = nerRecognizer.recognize(sentence);
		System.out.println("原句：" + sentence);
        System.out.println("识别：" + result.getConstriants());
        System.out.println(result.toString());
        
        sentence = "上世纪五十年代，辞旧迎新之际，国务院总理李鹏今天上午来到北京石景山发电总厂考察，向广大企业职工表示节日的祝贺，向将要在节日期间坚守工作岗位的同志们表示慰问。";
        result = nerRecognizer.recognize(sentence);
		System.out.println("原句：" + sentence);
        System.out.println("识别：" + result.getConstriants());
        System.out.println(result.toString());
        
        sentence = "气象台28日5时35分发布大雾黄色预警：今上午前浦东新区南部、崇明、奉贤、松江、青浦、宝山、金山将有能见度小于500米的浓雾。";
        result = nerRecognizer.recognize(sentence);
		System.out.println("原句：" + sentence);
        System.out.println("识别：" + result.getConstriants());
        System.out.println(result.toString());
	}

}
