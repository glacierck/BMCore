package corpus;

import cn.edu.fudan.corpus.SentencePrepare;
/**
 * 一、产生句子分类模型训练所需要的文件：包括正负样本的文件（顺序随机打乱）；字符表；标签文件
* */
public class SentencePrepareStart {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String preprocessFile = "conf/Preprocess.properties";
		String posTaggerFile = "conf/PosTagger.properties";
		String sentenceCorpusFile = "sentence/sample_sentence.utf8";
		String negativeCorpusFile = "sentence/sample_negative.utf8";
		String corpusFile = "sentence/sentence_corpus.utf8";//指定训练样本的文件路径。
		String vocaburaryFile = "sentence/token.utf8";//指定训练样本中出现字符的字符表。
		String labelFile = "sentence/classificationLabel.utf8";//指定任务标签集合的文件路径.

		SentencePrepare prepare = new SentencePrepare(preprocessFile,
				posTaggerFile, sentenceCorpusFile, negativeCorpusFile,
				corpusFile, vocaburaryFile, labelFile);
		
		prepare.prepare();
		prepare.shuffle();
		prepare.write();

	}

}
