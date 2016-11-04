// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.corpus;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

public class LookupTableGeneratorStart
{
    public static void main(final String[] args) {
        final Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("conf/LookupTabelGenerator.properties"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        final String inputFile = prop.getProperty("embeddingTextFile");
        final String outputFile = prop.getProperty("embeddingFile");
        final HashMap<String, double[]> lookuptable = new HashMap<String, double[]>();
        final int dimension = Integer.parseInt(prop.getProperty("dimension"));
        final String tokenFile = prop.getProperty("tokenFile");
        double[] feature = null;
        double divisor = 0.0;
        final boolean isDebug = true;
        final boolean isNormalized = false;
        try {
            final FileInputStream fis = new FileInputStream(inputFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            String line = null;
            String[] tokens = null;
            int num = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.equals("")) {
                    ++num;
                    tokens = line.split("\\s+");
                    if (tokens.length != dimension + 1) {
                        System.out.println("Check the embeddings at the line " + num + " (the dimensionality is " + (tokens.length - 1) + "): " + line);
                        System.exit(0);
                    }
                    else {
                        feature = new double[dimension];
                        for (int i = 1; i < dimension + 1; ++i) {
                            feature[i - 1] = Double.parseDouble(tokens[i]);
                        }
                        if (isNormalized) {
                            divisor = 0.0;
                            for (int d = 0; d < dimension; ++d) {
                                divisor += Math.pow(feature[d], 2.0);
                            }
                            divisor = Math.sqrt(divisor);
                            for (int d = 0; d < dimension; ++d) {
                                feature[d] /= divisor;
                            }
                        }
                        lookuptable.put(tokens[0], feature);
                    }
                }
            }
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        try {
            final FileInputStream fis = new FileInputStream(tokenFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            String line = null;
            final Random randomgen = new Random();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!lookuptable.containsKey(line)) {
                    System.out.println("Message: the token \"" + line + "\" is missing in the embeddings.");
                    feature = new double[dimension];
                    for (int j = 0; j < dimension; ++j) {
                        feature[j] = (randomgen.nextDouble() - 0.5) * 2.0 / dimension;
                    }
                    if (isNormalized) {
                        divisor = 0.0;
                        for (int d2 = 0; d2 < dimension; ++d2) {
                            divisor += Math.pow(feature[d2], 2.0);
                        }
                        divisor = Math.sqrt(divisor);
                        for (int d2 = 0; d2 < dimension; ++d2) {
                            feature[d2] /= divisor;
                        }
                    }
                    lookuptable.put(line, feature);
                }
            }
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        try {
            final FileOutputStream fos = new FileOutputStream(outputFile);
            final ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(lookuptable);
            oos.close();
            fos.close();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        System.out.println("The size of lookup table: " + lookuptable.size());
        System.out.println("The lookup table has been written into the file " + outputFile + ".");
        if (isDebug) {
            feature = lookuptable.get("\u25a1");
            System.out.print("\u25a1 (" + feature.length + "): ");
            for (int d3 = 0; d3 < dimension; ++d3) {
                System.out.print(String.valueOf(feature[d3]) + " ");
            }
            System.out.println(" ");
            feature = lookuptable.get("\u2560");
            System.out.print("\u2560 (" + feature.length + "): ");
            for (int d3 = 0; d3 < dimension; ++d3) {
                System.out.print(String.valueOf(feature[d3]) + " ");
            }
            System.out.println(" ");
            feature = lookuptable.get("\u2563");
            System.out.print("\u2563 (" + feature.length + "): ");
            for (int d3 = 0; d3 < dimension; ++d3) {
                System.out.print(String.valueOf(feature[d3]) + " ");
            }
        }
    }
}
