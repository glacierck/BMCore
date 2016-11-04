// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lstm;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class BiLSTMwithTransitionDecoder
{
    private int inputUnits;
    private int outputUnits;
    private int numberOfBlock;
    private int numberOfCell;
    private int numberOfInput;
    private BiLSTMObject lstm;
    private double[][] transition;
    private ArrayList<BiBlock> lstm_lr;
    private ArrayList<BiBlock> lstm_rl;
    private BiOutputLayer outputlayer;
    private String unknown;
    private int vocabularyDimension;
    private int posLabelDimension;
    private int preprocessLabelDimension;
    private HashMap<String, double[]> vocabularyLookupTable;
    private HashMap<String, double[]> posLabelLookupTable;
    private HashMap<String, double[]> preprocessLabelLookupTable;
    private String[] labelset;
    private HashMap<String, Integer> labelIndex;
    private String inputNetworkSettingFile;
    private BiInput[] instance;
    private int length;
    private ArrayList<double[]> input;
    private String[] tokens;
    private BiBlock block;
    private BiCell cell;
    private double[] inInput;
    private double[][] outputCell_lr;
    private double[][] outputCell_rl;
    private double[][] output;
    private int outputCellUnits;
    private double[][] inputs_lr;
    private double[][] inputs_rl;
    private double[] feature;
    private int shift;
    private boolean isDebug;
    private double[][] optVal;
    private int[][] optRec;
    private String[] optSequence;
    private double optValue;
    private double temp;
    private int optTag;
    
    public BiLSTMwithTransitionDecoder(final String inputNetworkSettingFile) {
        this.unknown = "\u25a1";
        this.isDebug = false;
        this.optVal = null;
        this.optRec = null;
        this.optSequence = null;
        this.optValue = -1.7976931348623157E308;
        this.temp = 0.0;
        this.optTag = -1;
        this.inputNetworkSettingFile = inputNetworkSettingFile;
        this.readPara();
    }
    
    public void readPara() {
        try {
            final FileInputStream fis = new FileInputStream(this.inputNetworkSettingFile);
            final ObjectInputStream ois = new ObjectInputStream(fis);
            final BiLSTMwithTransitionSetting networkSetting = (BiLSTMwithTransitionSetting)ois.readObject();
            this.inputUnits = networkSetting.getInputUnits();
            this.outputUnits = networkSetting.getOutputUnits();
            this.numberOfBlock = networkSetting.getNumberOfBlock();
            this.numberOfCell = networkSetting.getNumberOfCell();
            this.numberOfInput = networkSetting.getNumberOfInput();
            this.lstm = networkSetting.getLstm();
            this.transition = networkSetting.getTransition();
            this.outputlayer = networkSetting.getOutputlayer();
            this.vocabularyDimension = networkSetting.getVocabularyDimension();
            this.posLabelDimension = networkSetting.getPosLabelDimension();
            this.preprocessLabelDimension = networkSetting.getPreprocessLabelDimension();
            this.vocabularyLookupTable = networkSetting.getVocabularyLookupTable();
            this.posLabelLookupTable = networkSetting.getPosLabelLookupTable();
            this.preprocessLabelLookupTable = networkSetting.getPreprocessLabelLookupTable();
            this.labelset = networkSetting.getLabelset();
            this.labelIndex = networkSetting.getLabelIndex();
            this.outputCellUnits = this.numberOfBlock * this.numberOfCell;
            this.inInput = new double[this.numberOfBlock * this.numberOfCell];
            this.lstm_lr = this.lstm.getLstm_lr();
            this.lstm_rl = this.lstm.getLstm_rl();
            ois.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String[] decodeSentence(final String[] sentence) {
        this.length = sentence.length;
        this.instance = new BiInput[this.length];
        this.inputs_lr = new double[this.length][this.numberOfInput];
        this.inputs_rl = new double[this.length][this.numberOfInput];
        for (int b = 0; b < this.numberOfBlock; ++b) {
            this.lstm_lr.get(b).reset(this.length, this.inputs_lr);
            this.lstm_rl.get(b).reset(this.length, this.inputs_rl);
        }
        this.outputCell_lr = new double[this.length][this.outputCellUnits];
        this.outputCell_rl = new double[this.length][this.outputCellUnits];
        this.outputlayer.reset(this.length, this.outputCell_lr, this.outputCell_rl);
        for (int p = 0; p < this.length; ++p) {
            this.tokens = sentence[p].split("\\s+");
            if (this.isDebug) {
                for (int i = 0; i < this.tokens.length; ++i) {
                    System.out.print(String.valueOf(this.tokens[i]) + " ");
                }
                System.out.println("");
            }
            this.input = new ArrayList<double[]>();
            if (this.tokens.length < 3) {
                System.out.println("Check the corpus at " + (p + 1) + " line.");
            }
            if (this.vocabularyLookupTable.get(this.tokens[0]) == null) {
                System.out.println("Vocabulary missing at " + (p + 1) + " line.");
            }
            if (this.posLabelLookupTable.get(this.tokens[1]) == null) {
                System.out.println("Pos-tagging label missing at " + (p + 1) + " line.");
            }
            if (this.preprocessLabelLookupTable.get(this.tokens[2]) == null) {
                System.out.println("Preprocessing label missing at " + (p + 1) + " line.");
            }
            if (this.vocabularyLookupTable.containsKey(this.tokens[0])) {
                this.input.add(this.vocabularyLookupTable.get(this.tokens[0]));
            }
            else {
                this.input.add(this.vocabularyLookupTable.get(this.unknown));
            }
            this.input.add(this.posLabelLookupTable.get(this.tokens[1]));
            this.input.add(this.preprocessLabelLookupTable.get(this.tokens[2]));
            this.instance[p] = new BiInput(this.input);
        }
        for (int p = 0; p < this.length; ++p) {
            this.input = this.instance[p].getExInput();
            if (p == 0) {
                for (int d = 0; d < this.outputCellUnits; ++d) {
                    this.inInput[d] = 0.0;
                }
            }
            else {
                for (int d = 0; d < this.outputCellUnits; ++d) {
                    this.inInput[d] = this.outputCell_lr[p - 1][d];
                }
            }
            this.shift = 0;
            for (int f = 0; f < this.input.size(); ++f) {
                this.feature = this.input.get(f);
                for (int d2 = 0; d2 < this.feature.length; ++d2) {
                    this.inputs_lr[p][this.shift] = this.feature[d2];
                    ++this.shift;
                }
            }
            for (int d = 0; d < this.outputCellUnits; ++d) {
                this.inputs_lr[p][this.shift] = this.inInput[d];
                ++this.shift;
            }
            if (this.isDebug) {
                for (int d = 0; d < this.numberOfInput; ++d) {
                    System.out.println("(" + p + "|" + d + "):" + this.inputs_lr[p][d]);
                }
            }
            for (int b2 = 0; b2 < this.numberOfBlock; ++b2) {
                this.lstm_lr.get(b2).computeOutput(p);
            }
            for (int b2 = 0; b2 < this.numberOfBlock; ++b2) {
                this.block = this.lstm_lr.get(b2);
                for (int c = 0; c < this.numberOfCell; ++c) {
                    this.cell = this.block.getCells().get(c);
                    this.outputCell_lr[p][b2 * this.numberOfCell + c] = this.cell.getOutputCell(p);
                }
            }
        }
        for (int p = 0; p < this.length; ++p) {
            this.input = this.instance[this.length - p - 1].getExInput();
            if (p == 0) {
                for (int d = 0; d < this.outputCellUnits; ++d) {
                    this.inInput[d] = 0.0;
                }
            }
            else {
                for (int d = 0; d < this.outputCellUnits; ++d) {
                    this.inInput[d] = this.outputCell_rl[p - 1][d];
                }
            }
            this.shift = 0;
            for (int f = 0; f < this.input.size(); ++f) {
                this.feature = this.input.get(f);
                for (int d2 = 0; d2 < this.feature.length; ++d2) {
                    this.inputs_rl[p][this.shift] = this.feature[d2];
                    ++this.shift;
                }
            }
            for (int d = 0; d < this.outputCellUnits; ++d) {
                this.inputs_rl[p][this.shift] = this.inInput[d];
                ++this.shift;
            }
            if (this.isDebug) {
                for (int d = 0; d < this.numberOfInput; ++d) {
                    System.out.println("(" + p + "|" + d + "): " + this.inputs_rl[p][d]);
                }
            }
            for (int b2 = 0; b2 < this.numberOfBlock; ++b2) {
                this.lstm_rl.get(b2).computeOutput(p);
            }
            for (int b2 = 0; b2 < this.numberOfBlock; ++b2) {
                this.block = this.lstm_rl.get(b2);
                for (int c = 0; c < this.numberOfCell; ++c) {
                    this.cell = this.block.getCells().get(c);
                    this.outputCell_rl[p][b2 * this.numberOfCell + c] = this.cell.getOutputCell(p);
                }
            }
        }
        this.outputlayer.computeOutput();
        this.output = this.outputlayer.getOutput();
        this.optVal = new double[this.length][this.outputUnits];
        this.optRec = new int[this.length][this.outputUnits];
        for (int j = 0; j < this.length; ++j) {
            for (int k = 0; k < this.outputUnits; ++k) {
                this.optVal[j][k] = -1.7976931348623157E308;
                this.optRec[j][k] = 0;
            }
        }
        for (int p = 0; p < this.length; ++p) {
            for (int t = 0; t < this.outputUnits; ++t) {
                if (p == 0) {
                    this.optVal[p][t] = this.output[p][t] + this.transition[t][this.outputUnits];
                }
                else {
                    for (int prev = 0; prev < this.outputUnits; ++prev) {
                        this.temp = this.optVal[p - 1][prev] + this.transition[prev][t] + this.output[p][t];
                        if (this.temp > this.optVal[p][t]) {
                            this.optVal[p][t] = this.temp;
                            this.optRec[p][t] = prev;
                        }
                    }
                }
            }
        }
        this.optSequence = new String[this.length];
        this.optValue = -1.7976931348623157E308;
        this.optTag = -1;
        for (int t2 = 0; t2 < this.outputUnits; ++t2) {
            if (this.optVal[this.length - 1][t2] > this.optValue) {
                this.optValue = this.optVal[this.length - 1][t2];
                this.optTag = t2;
            }
        }
        this.optSequence[this.length - 1] = this.labelset[this.optTag];
        for (int j = this.length - 1; j > 0; --j) {
            this.optTag = this.optRec[j][this.optTag];
            this.optSequence[j - 1] = this.labelset[this.optTag];
        }
        return this.optSequence;
    }
    
    public int getInputUnits() {
        return this.inputUnits;
    }
    
    public int getVocabularyDimension() {
        return this.vocabularyDimension;
    }
    
    public int getPosLabelDimension() {
        return this.posLabelDimension;
    }
    
    public int getPreprocessLabelDimension() {
        return this.preprocessLabelDimension;
    }
    
    public HashMap<String, Integer> getLabelIndex() {
        return this.labelIndex;
    }
    
    public int getNumberOfInput() {
        return this.numberOfInput;
    }
}
