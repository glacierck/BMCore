package lstm;

import cn.edu.fudan.lstm.BiLSTMwithTransition;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class BiLSTMwithTransitionStart {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("lstm/bi_lstm.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int inputUnits = Integer.parseInt(prop.getProperty("inputUnits"));
		int outputUnits = Integer.parseInt(prop.getProperty("outputUnits"));
		int numberOfBlock = Integer.parseInt(prop.getProperty("numberOfBlock"));
		int numberOfCell = Integer.parseInt(prop.getProperty("numberOfCell"));
		int learningTimes = Integer.parseInt(prop.getProperty("learningTimes"));
		double learningRate = Double.parseDouble(prop.getProperty("learningRate"));
		double regularizationRate = Double.parseDouble(prop.getProperty("regularizationRate"));
		double errorLimit = Double.parseDouble(prop.getProperty("errorLimit"));
		int vocabularyDimension = Integer.parseInt(prop.getProperty("vocabularyDimension"));
		int posLabelDimension = Integer.parseInt(prop.getProperty("posLabelDimension"));
		int preprocessLabelDimension = Integer.parseInt(prop.getProperty("preprocessLabelDimension"));

		String corpusFile = prop.getProperty("corpusFile");
		String vocabularyFile = prop.getProperty("vocabularyFile");
		String semanticLabelFile = prop.getProperty("semanticLabelFile");
		String posLabelFile = prop.getProperty("posLabelFile");
		String preprocessLabelFile = prop.getProperty("preprocessLabelFile");
		String outputNetworkSettingFile = prop.getProperty("outputNetworkSettingFile");

		if ((vocabularyDimension + posLabelDimension + preprocessLabelDimension) != inputUnits) {
			System.out
					.println("Check the units of input, and it should be "
							+ (vocabularyDimension + posLabelDimension + preprocessLabelDimension)
							+ ".");
			System.exit(0);
		}

		BiLSTMwithTransition lstm = new BiLSTMwithTransition(inputUnits, outputUnits, numberOfBlock,
				numberOfCell, learningTimes, learningRate, regularizationRate,
				errorLimit, vocabularyDimension, posLabelDimension,
				preprocessLabelDimension, corpusFile, vocabularyFile,
				semanticLabelFile, posLabelFile, preprocessLabelFile,
				outputNetworkSettingFile);
		
		System.out.println("A LSTM has been constructed." + "\r\n");
		
		Date beginTime = new Date(System.currentTimeMillis());
		lstm.train();
		Date finishTime = new Date(System.currentTimeMillis());
		long between = finishTime.getTime() - beginTime.getTime();
		System.out.println("The learning process takes " + between + " millisecond" + "\r\n");
		
		lstm.writePara();
	}
}
