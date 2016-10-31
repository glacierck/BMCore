package flow;

import cn.edu.fudan.flow.WordSegmentor;
import cn.edu.fudan.lang.IntermediateResult;

public class WordSegmentorStart {
	public static void main(String[] args) {
		
		String preprocessFile = "conf/Preprocess.properties";
		String wordSegmentorFile = "conf/WordSegmentor.properties";
		WordSegmentor wordSegmentor = new WordSegmentor(preprocessFile, wordSegmentorFile);
		String sentence  = "【中国农业银行】李嘉欣在上海市国权路举栗子：2015年世界旅游小姐比赛山东赛区冠军总决赛在威海举行，鸭梨山大啊。";
		IntermediateResult intermediateResult = wordSegmentor.segmentation(sentence);
		System.out.println("原句：" + sentence);
        System.out.println("分词：" + intermediateResult.getConstriants());
        System.out.println(intermediateResult.toString());       
        
        sentence = "及格率为36.344555%。今天是8月10日，温度8度。我的体重是55.23公斤。房子的大小为九十五点八平方米。我的邮箱是zxqingcn@fudan.edu.cn。";
        intermediateResult = wordSegmentor.segmentation(sentence);
		System.out.println("原句：" + sentence);
        System.out.println("分词：" + intermediateResult.getConstriants());
        System.out.println(intermediateResult.toString());
        
//        sentence = "IBM今天是2015-08-30，气温25摄氏度，阴有雨。Google发布了Word2Vector工具，请点击http://zhidao.163.com/question/57067131.html。";
        sentence = "我是曹操，新手机号码为18576704846，旧号已停用，常联系！"; // 天气预报
        intermediateResult = wordSegmentor.segmentation(sentence);
		System.out.println("原句：" + sentence);
        System.out.println("分词：" + intermediateResult.getConstriants());
        System.out.println(intermediateResult.toString());
	}
}
