package flow;

import cn.edu.fudan.flow.PosTagger;
import cn.edu.fudan.lang.IntermediateResult;

public class PosTaggerStart {
	public static void main(String[] args) {
		
		String preprocessFile = "conf/Preprocess.properties";
		String posTaggerFile = "conf/PosTagger.properties";
		PosTagger posTagger = new PosTagger(preprocessFile, posTaggerFile);
		String sentence  = "【中国农业银行】秦始皇举栗子，2015年世界旅游小姐比赛山东赛区冠军总决赛在威海举行，鸭梨山大啊";
		IntermediateResult intermediateResult = posTagger.posTagging(sentence);
		System.out.println("原句：" + sentence);
        System.out.println("词性：" + intermediateResult.getConstriants());
        System.out.println(intermediateResult.toString());
        
        sentence = "及格率为36.344555%。今天是5月16日。我的体重是55.23公斤。今天温度5摄氏度。房子的大小为九十五点八平方米。我的邮箱是zhengxq@fudan.edu.cn。";
        intermediateResult = posTagger.posTagging(sentence);
		System.out.println("原句：" + sentence);
        System.out.println("词性：" + intermediateResult.getConstriants());
        System.out.println(intermediateResult.toString());
        
//        sentence = "他们说：上海虹桥,今天05/09气温25摄氏度。上世纪五十年代iPhone6S 。我100%很好。";
        sentence = "【水果网】您购买的苹果已经下单成功，明天12点将准时送达目的地香港国际机场。账单号201020231101。";
        intermediateResult = posTagger.posTagging(sentence);
		System.out.println("原句：" + sentence);
        System.out.println("词性：" + intermediateResult.getConstriants());
        System.out.println(intermediateResult.toString());
	}

}
