package corpus;

import cn.edu.fudan.corpus.SemanticCorpusPrepare;

/**
* 三（1） 训练基于CRF的中文语义分析模型
 * preprocessFile : conf/Preprocess.properties  指向预处理模块的配置文件路径，该配置文件参数详见3.1.2节
 * posTaggerFile : conf/PosTagger.properties 指向中文词性标注模块的配置文件路径，该配置文件参数详见3.3.2节
* */
public class SemanticCorpusPrepareStart {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String preprocessFile = "conf/Preprocess.properties";
		String posTaggerFile = "conf/PosTagger.properties";
//		String sentenceFile = "crf/multiple_event_sentence.utf8"; // single_event_sentence.utf8(指向存储将作为语义分析模型训练样本原始语句的文件路径。)
//		String outputFile = "crf/multiple_event_sample.utf8";
//		boolean isMultiEvent = true;//多事件时为true
//		boolean hasHeadingEventType = true;//多事件时为true

		String sentenceFile = "crf/single_event_sentence.utf8"; // single_event_sentence.utf8(指向存储将作为语义分析模型训练样本原始语句的文件路径。)
		String outputFile = "crf/single_event_sample.utf8";//指向保存原始语句转化成训练样本格式的文件路径。
		boolean isMultiEvent = false;//多事件时为true
		boolean hasHeadingEventType = false;//多事件时为true
		
		SemanticCorpusPrepare semanticCorpusPrepare = new SemanticCorpusPrepare(preprocessFile, posTaggerFile, sentenceFile, outputFile, isMultiEvent, hasHeadingEventType);
		semanticCorpusPrepare.prepare();
		System.out.println("The corpus \""+ outputFile + "\" for semantic role labelling has been successfully prepared.");
	}

}
