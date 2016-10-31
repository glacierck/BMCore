package dnn;


import cn.edu.fudan.dnn.WindowConvolutionNetworkDecoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * @author Xiaoqing Zheng - Fudan University
 *
 */

public class WindowConvolutionNetworkDecoderStart {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("conf/windowConvolutionNetworkDecoder.properties"));
		} catch (IOException e) { 
			e.printStackTrace();
		}
	
		WindowConvolutionNetworkDecoder decoder = new WindowConvolutionNetworkDecoder(prop.getProperty("inputNetworkSettingFile"));
		decoder.setInputSentenceFile(prop.getProperty("inputSentenceFile"));
		decoder.setOutputTaggingFile(prop.getProperty("outputTaggingFile"));
		decoder.setCharacterLevel(Boolean.parseBoolean(prop.getProperty("isCharacterLevel")));
		decoder.setResultWithTag(Boolean.parseBoolean(prop.getProperty("isResultWithTag")));
		decoder.setSegmentation(Boolean.parseBoolean(prop.getProperty("isSegmentation")));
		decoder.setStandard(Boolean.parseBoolean(prop.getProperty("isStandard")));
		
		decoder.readPara();
		
		System.out.println("Decoder setting: ");
		System.out.println("  inputNetworkSettingFile: " + prop.getProperty("inputNetworkSettingFile"));
		System.out.println("  isCharacterLevel: "+ Boolean.parseBoolean(prop.getProperty("isCharacterLevel")));
		System.out.println("  isResultWithTag: "+ Boolean.parseBoolean(prop.getProperty("isResultWithTag")));

		Date beginTime = new Date(System.currentTimeMillis());
//		decoder.decode();
		Date finishTime = new Date(System.currentTimeMillis());
		long between = finishTime.getTime() - beginTime.getTime();
		System.out.println("\r\n" + "The decoding process takes " + between + " millisecond");

		
		String result = decoder.decodeSentence("我是李四，换号码我一直很谨慎，号码是自己选的，要对自己负责，还要便于大家记忆，18576704846，移动荣誉出品，符合中国人的口味，我以后都用它！");
		System.out.println(result);
		result = decoder.decodeSentence("小丽，你好，我是摩西，原来的号码15389241334不用了，从现在开始使用新号码13710590539，请保存。");
		System.out.println(result);
		
		String sent = null;
		
//		String constraints = "O O B E S O B E O O O O O O S O O O B E S O O";
//		sent = "我是李四，换号码我一直很谨慎，号码是自己选的。";
//		result = decoder.decodeSentence(sent, constraints);
//		System.out.println(result);
		
		sent = "2015世界旅游小姐大赛山东赛区冠军总决赛在威海举行。当日，来自山东省各地的30名佳丽选手通过泳装、旗袍和晚礼展示等环节拼比角逐，鞠炎珍获冠军殊荣， 夏伟、宋一凡分获亚军和季军。她们将代表山东参加今年12月5日在沈阳举行的2015世界旅游小姐中国年度冠军总决赛。";
		result = decoder.decodeSentence(sent);
		System.out.println(result);
		
		sent = "我是复旦大学的学生。";
		result = decoder.decodeSentence(sent);
		System.out.println(result);
	}
}
