package dnn;


import cn.edu.fudan.dnn.WindowConvolutionNetwork;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * @author Xiaoqing Zheng - Fudan University
 *
 */

public class WindowConvolutionNetworkStart {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("conf/windowConvolutionNetwork.properties"));
		} catch (IOException e) { 
			e.printStackTrace();
		}
	
		WindowConvolutionNetwork network = new WindowConvolutionNetwork();
		
        network.setCorpusFile(prop.getProperty("corpusFile"));
        network.setTokenFile(prop.getProperty("tokenFile"));
        network.setLabelFile(prop.getProperty("labelFile"));
        network.setReadEmbedding(Boolean.parseBoolean(prop.getProperty("isReadEmbedding")));
        network.setEmbeddingFile(prop.getProperty("embeddingFile"));
        network.setExternalFeature(Boolean.parseBoolean(prop.getProperty("isExternalFeature")));
        network.setExternalFeatureFile((prop.getProperty("externalFeatureFile"))); 
		network.setReadNetworkSetting(Boolean.parseBoolean(prop.getProperty("isReadNetworkSetting")));
        network.setInputNetworkSettingFile(prop.getProperty("inputNetworkSettingFile"));
        network.setOutputNetworkSettingFile(prop.getProperty("outputNetworkSettingFile"));
        network.setEchoFile(prop.getProperty("echoFile"));
        network.setFeatureDimension(Integer.parseInt(prop.getProperty("featureDimension")));
		network.setInternalFeatureDimension(Integer.parseInt(prop.getProperty("internalFeatureDimension")));
		network.setExternalFeatureDimension(Integer.parseInt(prop.getProperty("externalFeatureDimension")));
		network.setWindowSize(Integer.parseInt(prop.getProperty("windowSize")));
		network.setFeatureMap(Integer.parseInt(prop.getProperty("featureMap")));
		network.setIgnoreAlphabetNumber(Boolean.parseBoolean(prop.getProperty("isIgnoreAlphabetNumber")));
//		network.setHiddenUnits(Integer.parseInt(prop.getProperty("hiddenUnits")));
		network.setLearningRate(Double.parseDouble(prop.getProperty("learningRate")));
		network.setRegularizationRate(Double.parseDouble(prop.getProperty("regularizationRate"))); 
		network.setErrorLimit(Double.parseDouble(prop.getProperty("errorLimit")));
		network.setLearningTimes(Integer.parseInt(prop.getProperty("learningTimes")));
		network.setAverage(Boolean.parseBoolean(prop.getProperty("isAverage")));

		network.readFiles();
		network.initPara();
		
		System.out.println("Network setting: ");
		System.out.println("  corpusFile: " + prop.getProperty("corpusFile"));
		System.out.println("  tokenFile: " + prop.getProperty("tokenFile"));
		System.out.println("  labelFile: " + prop.getProperty("labelFile"));
		System.out.println("  isReadEmbedding: "+ Boolean.parseBoolean(prop.getProperty("isReadEmbedding")));
		System.out.println("  embeddingFile: " + prop.getProperty("embeddingFile"));
		System.out.println("  isExternalFeature: "+ Boolean.parseBoolean(prop.getProperty("isExternalFeature")));
		System.out.println("  externalFeatureFile: " + prop.getProperty("externalFeatureFile"));
		System.out.println("  isReadNetworkSetting: "+ Boolean.parseBoolean(prop.getProperty("isReadNetworkSetting")));
		System.out.println("  inputNetworkSettingFile: " + prop.getProperty("inputNetworkSettingFile"));
		System.out.println("  outputNetworkSettingFile: " + prop.getProperty("outputNetworkSettingFile"));
		System.out.println("  echoFile: " + prop.getProperty("echoFile"));
		System.out.println("  featureDimension: " + Integer.parseInt(prop.getProperty("featureDimension")));
		System.out.println("  internalFeatureDimension: " + Integer.parseInt(prop.getProperty("internalFeatureDimension")));
		System.out.println("  externalFeatureDimension: " + Integer.parseInt(prop.getProperty("externalFeatureDimension")));
		System.out.println("  windowSize: " + Integer.parseInt(prop.getProperty("windowSize")));
		System.out.println("  featureMap: " + Integer.parseInt(prop.getProperty("featureMap")));
		System.out.println("  isIgnoreAlphabetNumber: "+ Boolean.parseBoolean(prop.getProperty("isIgnoreAlphabetNumber")));
//		System.out.println("  hiddenUnits: " + Integer.parseInt(prop.getProperty("hiddenUnits")));
		System.out.println("  learningRate: " + Double.parseDouble(prop.getProperty("learningRate")));
		System.out.println("  regularizationRate: " + Double.parseDouble(prop.getProperty("regularizationRate")));
		System.out.println("  errorLimit: " + Double.parseDouble(prop.getProperty("errorLimit")));
		System.out.println("  learningTimes: " + Integer.parseInt(prop.getProperty("learningTimes")));
		System.out.println("  isAverage: " + Boolean.parseBoolean(prop.getProperty("isAverage")));
		
		Date beginTime = new Date(System.currentTimeMillis());
		network.learning();
		Date finishTime = new Date(System.currentTimeMillis());
		long between = finishTime.getTime() - beginTime.getTime();
		System.out.println("\r\n" + "The learning process takes " + between + " millisecond");
		
		network.writePara();
	}

}
