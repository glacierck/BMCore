package corpus;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

public class LookupTableGeneratorStart {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(
					"conf/LookupTabelGenerator.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		String inputFile = prop.getProperty("embeddingTextFile");
		String outputFile = prop.getProperty("embeddingFile");
		HashMap<String, double[]> lookuptable = new HashMap<String, double[]>();
		int dimension = Integer.parseInt(prop.getProperty("dimension"));
		String tokenFile = prop.getProperty("tokenFile");
		double[] feature = null;
		double divisor = 0.0d;
		boolean isDebug = true;
		boolean isNormalized = false;

		try {
			FileInputStream fis = new FileInputStream(inputFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);

			String line = null;
			String[] tokens = null;
			int num = 0;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (!line.equals("")) {
					num++;
					tokens = line.split("\\s+");
					if (tokens.length != (dimension + 1)) {
						System.out.println("Check the embeddings at the line "
								+ num + " (the dimensionality is "
								+ (tokens.length - 1) + "): " + line);
						System.exit(0);
					} else {
						feature = new double[dimension];
						for (int i = 1; i < dimension + 1; i++) {
							feature[i - 1] = Double.parseDouble(tokens[i]);

						}
						if (isNormalized) {
							// Normalize
							divisor = 0.0d;
							for (int d = 0; d < dimension; d++) {
								divisor += Math.pow(feature[d], 2);
							}
							divisor = Math.sqrt(divisor);
							for (int d = 0; d < dimension; d++) {
								feature[d] = feature[d] / divisor;
							}
						}
						lookuptable.put(tokens[0], feature);
					}
				}

			}
			br.close();
			isr.close();
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Supplement the special tokens
		try {
			FileInputStream fis = new FileInputStream(tokenFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);

			String line = null;
			Random randomgen = new Random();
			
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (!lookuptable.containsKey(line)) {
					System.out.println("Message: the token \"" + line + "\" is missing in the embeddings.");
					feature = new double[dimension];
					for (int i = 0; i < dimension; i++) {
						feature[i] = (randomgen.nextDouble() - 0.5d) * 2 / dimension;
					}
					if (isNormalized) {
						// Normalize
						divisor = 0.0d;
						for (int d = 0; d < dimension; d++) {
							divisor += Math.pow(feature[d], 2);
						}
						divisor = Math.sqrt(divisor);
						for (int d = 0; d < dimension; d++) {
							feature[d] = feature[d] / divisor;
						}
					}
					lookuptable.put(line, feature);
				}
			}
			br.close();
			isr.close();
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			FileOutputStream fos = new FileOutputStream(outputFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(lookuptable);
			oos.close();
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("The size of lookup table: " + lookuptable.size());
		System.out.println("The lookup table has been written into the file " + outputFile + ".");
		
		if (isDebug) {
			feature = lookuptable.get("□");
			System.out.print("□ (" + feature.length + "): ");
			for (int d = 0; d < dimension; d++) {
				System.out.print(feature[d] + " ");
			}
			System.out.println(" ");
			feature = lookuptable.get("╠");
			System.out.print("╠ (" + feature.length + "): ");
			for (int d = 0; d < dimension; d++) {
				System.out.print(feature[d] + " ");
			}
			System.out.println(" ");
			feature = lookuptable.get("╣");
			System.out.print("╣ (" + feature.length + "): ");
			for (int d = 0; d < dimension; d++) {
				System.out.print(feature[d] + " ");
			}
			
		}
	}

}
