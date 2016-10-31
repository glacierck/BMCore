package lstm;

import cn.edu.fudan.lstm.BiLSTMwithTransitionDecoder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

public class BiLSTMwithTransitionDecoderStart {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("lstm/lstmDecoder.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		BiLSTMwithTransitionDecoder decoder = new BiLSTMwithTransitionDecoder(prop.getProperty("inputNetworkSettingFile"));
		String corpusFile = prop.getProperty("corpusFile");

		String[] sentence = null;
		ArrayList<String> parser = new ArrayList<String>();
		String[] words = null;
		String[] labels = null;
		boolean isImportantOnly = false;

		try {
			FileInputStream fis = new FileInputStream(corpusFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			int count = 0;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (!line.equals("") && !line.equals(" ")) {
					parser.add(line);
					count++;
				} 
				else {
					sentence = new String[count];
					words = new String[count];
					for (int p = 0; p < count; p++) {
						sentence[p] = parser.get(p);
						words[p] = sentence[p].substring(sentence[p].lastIndexOf(" ") + 1);
					}
					labels = decoder.decodeSentence(sentence);
					for (int p = 0; p < count; p++) {
						if (isImportantOnly) {
							if (!labels[p].equals("O")) {
								System.out.println(words[p] + " / " + labels[p]);
							}
						}
						else {
							System.out.println(words[p] + " / " + labels[p]);
						}
					}
					System.out.println("");
					count = 0;
					parser.clear();

				}
			}
			
			sentence = new String[count];
			words = new String[count];
			for (int p = 0; p < count; p++) {
				sentence[p] = parser.get(p);
				words[p] = sentence[p].substring(sentence[p].lastIndexOf(" ") + 1);
			}
			labels = decoder.decodeSentence(sentence);
			for (int p = 0; p < count; p++) {
				if (isImportantOnly) {
					if (!labels[p].equals("O")) {
						System.out.println(words[p] + " / " + labels[p]);
					}
				}
				else {
					System.out.println(words[p] + " / " + labels[p]);
				}
			}
			System.out.println("");
			count = 0;
			parser.clear();
			
			br.close();
			isr.close();
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
