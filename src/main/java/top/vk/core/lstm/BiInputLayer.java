// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lstm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class BiInputLayer
{
    private ArrayList<String[][]> samples;
    private int size;
    private int index;
    private BiInputOutputPair[] instance;
    private int inputUnits;
    private int outputUnits;
    private String[][] sample;
    private ArrayList<double[]> input;
    private double[][] derivEmbedding;
    private String unknown;
    private int vocabularyDimension;
    private int posLabelDimension;
    private int preprocessLabelDimension;
    private String corpusFile;
    private String vocabularyFile;
    private String semanticLabelFile;
    private String posLabelFile;
    private String preprocessLabelFile;
    private String[] labelset;
    private HashMap<String, Integer> labelIndex;
    private HashMap<String, double[]> vocabularyLookupTable;
    private HashMap<String, double[]> posLabelLookupTable;
    private HashMap<String, double[]> preprocessLabelLookupTable;
    private boolean isDebug;
    private int shift;
    private double[] embedding;
    
    public BiInputLayer(final int inputUnits, final int outputUnits, final int vocabularyDimension, final int posLabelDimension, final int preprocessLabelDimension, final String corpusFile, final String vocabularyFile, final String semanticLabelFile, final String posLabelFile, final String preprocessLabelFile) {
        this.unknown = "\u25a1";
        this.isDebug = false;
        this.shift = 0;
        this.embedding = null;
        this.inputUnits = inputUnits;
        this.outputUnits = outputUnits;
        this.vocabularyDimension = vocabularyDimension;
        this.posLabelDimension = posLabelDimension;
        this.preprocessLabelDimension = preprocessLabelDimension;
        this.corpusFile = corpusFile;
        this.vocabularyFile = vocabularyFile;
        this.semanticLabelFile = semanticLabelFile;
        this.posLabelFile = posLabelFile;
        this.preprocessLabelFile = preprocessLabelFile;
        this.init();
        this.initEmbedding();
    }
    
    private void initEmbedding() {
        final Random randomgen = new Random();
        this.vocabularyLookupTable = new HashMap<String, double[]>();
        try {
            final FileInputStream fis = new FileInputStream(this.vocabularyFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll(" ", "");
                line = line.trim();
                if (!line.equals("") && !line.equals(" ")) {
                    final double[] feature = new double[this.vocabularyDimension];
                    for (int i = 0; i < this.vocabularyDimension; ++i) {
                        feature[i] = (randomgen.nextDouble() - 0.5) * 2.0 / this.inputUnits;
                    }
                    this.vocabularyLookupTable.put(line, feature);
                }
            }
            System.out.println("The size of vocabularies is (" + this.vocabularyLookupTable.size() + "|" + this.vocabularyDimension + ") tokens.");
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.posLabelLookupTable = new HashMap<String, double[]>();
        try {
            final FileInputStream fis = new FileInputStream(this.posLabelFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll(" ", "");
                line = line.trim();
                if (!line.equals("") && !line.equals(" ")) {
                    final double[] feature = new double[this.posLabelDimension];
                    for (int i = 0; i < this.posLabelDimension; ++i) {
                        feature[i] = (randomgen.nextDouble() - 0.5) * 2.0 / this.inputUnits;
                    }
                    this.posLabelLookupTable.put(line, feature);
                }
            }
            System.out.println("The size of pos-tagging labels is (" + this.posLabelLookupTable.size() + "|" + this.posLabelDimension + ") tokens.");
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.preprocessLabelLookupTable = new HashMap<String, double[]>();
        try {
            final FileInputStream fis = new FileInputStream(this.preprocessLabelFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll(" ", "");
                line = line.trim();
                if (!line.equals("") && !line.equals(" ")) {
                    final double[] feature = new double[this.preprocessLabelDimension];
                    for (int i = 0; i < this.preprocessLabelDimension; ++i) {
                        feature[i] = (randomgen.nextDouble() - 0.5) * 2.0 / this.inputUnits;
                    }
                    this.preprocessLabelLookupTable.put(line, feature);
                }
            }
            System.out.println("The size of preprocessing labels is (" + this.preprocessLabelLookupTable.size() + "|" + this.preprocessLabelDimension + ") tokens.");
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            final FileInputStream fis = new FileInputStream(this.semanticLabelFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            String line = null;
            String labels = "";
            int count = 0;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll(" ", "");
                line = line.trim();
                if (!line.equals("") && !line.equals(" ")) {
                    ++count;
                    labels = String.valueOf(labels) + line + " ";
                }
            }
            this.labelset = new String[count];
            this.labelIndex = new HashMap<String, Integer>();
            final String[] tokens = labels.split("\\s+");
            for (int j = 0; j < this.labelset.length; ++j) {
                this.labelset[j] = tokens[j];
                this.labelIndex.put(this.labelset[j], j);
            }
            line = null;
            br.close();
            isr.close();
            fis.close();
            System.out.println("The size of labels is (" + this.labelIndex.size() + "|" + count + ") tokens.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (this.outputUnits != this.labelIndex.size()) {
            System.out.println("Check the units of output, and it should be " + this.labelIndex.size() + ".");
            System.exit(0);
        }
    }
    
    private void init() {
        this.samples = new ArrayList<String[][]>();
        try {
            final FileInputStream fis = new FileInputStream(this.corpusFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            String line = null;
            int length = 0;
            Iterator<String> its = null;
            ArrayList<String> sequence = new ArrayList<String>();
            String[] tokens = null;
            String input = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.equals("")) {
                    sequence.add(line);
                    ++length;
                }
                else {
                    this.sample = new String[2][length];
                    length = 0;
                    its = sequence.iterator();
                    while (its.hasNext()) {
                        line = its.next();
                        tokens = line.split("\\s+");
                        input = tokens[0];
                        for (int i = 2; i < tokens.length; ++i) {
                            input = String.valueOf(input) + " " + tokens[i];
                        }
                        this.sample[0][length] = input;
                        this.sample[1][length] = tokens[1];
                        ++length;
                    }
                    if (length > 1) {
                        this.samples.add(this.sample);
                    }
                    length = 0;
                    sequence = new ArrayList<String>();
                }
            }
            this.sample = new String[2][length];
            length = 0;
            its = sequence.iterator();
            while (its.hasNext()) {
                line = its.next();
                tokens = line.split("\\s+");
                input = tokens[0];
                for (int i = 2; i < tokens.length; ++i) {
                    input = String.valueOf(input) + " " + tokens[i];
                }
                this.sample[0][length] = input;
                this.sample[1][length] = tokens[1];
                ++length;
            }
            if (length > 1) {
                this.samples.add(this.sample);
            }
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.size = this.samples.size();
        this.index = 0;
    }
    
    public boolean hasNext() {
        return this.index < this.size;
    }
    
    public BiInputOutputPair[] next() {
        this.sample = this.samples.get(this.index);
        ++this.index;
        this.instance = new BiInputOutputPair[this.sample[0].length];
        String[] tokens = null;
        this.derivEmbedding = new double[this.sample[0].length][this.inputUnits];
        for (int p = 0; p < this.sample[0].length; ++p) {
            this.input = new ArrayList<double[]>();
            tokens = this.sample[0][p].split("\\s+");
            if (this.isDebug) {
                if (tokens.length < 3) {
                    System.out.println("Check the corpus: " + this.index + " sample, " + (p + 1) + " line.");
                }
                if (this.vocabularyLookupTable.get(tokens[0]) == null) {
                    System.out.println("Vocabulary missing " + this.sample[0][p] + " at " + this.index + " sample, " + (p + 1) + " line.");
                }
                if (this.posLabelLookupTable.get(tokens[1]) == null) {
                    System.out.println("Pos-tagging lable missing " + this.sample[0][p] + " at " + this.index + " sample, " + (p + 1) + " line.");
                }
                if (this.preprocessLabelLookupTable.get(tokens[2]) == null) {
                    System.out.println("Preprocesing lable missing " + this.sample[0][p] + " at " + this.index + " sample, " + (p + 1) + " line.");
                }
            }
            if (this.vocabularyLookupTable.containsKey(tokens[0])) {
                this.input.add(this.vocabularyLookupTable.get(tokens[0]));
            }
            else {
                this.input.add(this.vocabularyLookupTable.get(this.unknown));
            }
            this.input.add(this.posLabelLookupTable.get(tokens[1]));
            this.input.add(this.preprocessLabelLookupTable.get(tokens[2]));
            if (this.isDebug && this.labelIndex.get(this.sample[1][p]) == null) {
                System.out.println(this.sample[1][p]);
            }
            this.instance[p] = new BiInputOutputPair(this.input, this.labelIndex.get(this.sample[1][p]));
        }
        return this.instance;
    }
    
    public void setDerivEmbedding(final double[][] derivEmbedding) {
        this.derivEmbedding = derivEmbedding;
    }
    
    public void update(final double learningRate) {
        for (int p = 0; p < this.instance.length; ++p) {
            this.shift = 0;
            this.input = this.instance[p].getExInput();
            for (int f = 0; f < this.input.size(); ++f) {
                this.embedding = this.input.get(f);
                for (int d = 0; d < this.embedding.length; ++d) {
                    final double[] embedding = this.embedding;
                    final int n = d;
                    embedding[n] += learningRate * this.derivEmbedding[p][this.shift];
                    ++this.shift;
                }
            }
        }
    }
    
    public void reset() {
        this.index = 0;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public String[] getLabelset() {
        return this.labelset;
    }
    
    public HashMap<String, Integer> getLabelIndex() {
        return this.labelIndex;
    }
    
    public HashMap<String, double[]> getVocabularyLookupTable() {
        return this.vocabularyLookupTable;
    }
    
    public HashMap<String, double[]> getPosLabelLookupTable() {
        return this.posLabelLookupTable;
    }
    
    public HashMap<String, double[]> getPreprocessLabelLookupTable() {
        return this.preprocessLabelLookupTable;
    }
}
