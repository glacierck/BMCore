// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lstm;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class BiLSTM
{
    private int inputUnits;
    private int outputUnits;
    private int numberOfBlock;
    private int numberOfCell;
    private int numberOfInput;
    private int outputCellUnits;
    private int learningTimes;
    private double errorLimit;
    private double learningRate;
    private double regularizationRate;
    private BiInputLayer inputlayer;
    private BiOutputLayer outputlayer;
    private ArrayList<BiBlock> lstm_lr;
    private ArrayList<BiBlock> lstm_rl;
    private ArrayList<double[]> input;
    private ArrayList<double[]> exInput;
    private double[] inInput;
    private double[][] outputCell_lr;
    private double[][] outputCell_rl;
    private int[] exOutput;
    private double[][] output;
    private boolean isDebug;
    private boolean isRegularization;
    private boolean isSoftmax;
    int vocabularyDimension;
    int posLabelDimension;
    int preprocessLabelDimension;
    private String corpusFile;
    private String vocabularyFile;
    private String semanticLabelFile;
    private String posLabelFile;
    private String preprocessLabelFile;
    private String outputNetworkSettingFile;
    private String[] labelset;
    private double[][] inputs_lr;
    private double[][] inputs_rl;
    double[] feature;
    int shift;
    
    public BiLSTM(final int inputUnits, final int outputUnits, final int numberOfBlock, final int numberOfCell, final int learningTimes, final double learningRate, final double regularizationRate, final double errorLimit, final int vocabularyDimension, final int posLabelDimension, final int preprocessLabelDimension, final String corpusFile, final String vocabularyFile, final String semanticLabelFile, final String posLabelFile, final String preprocessLabelFile, final String outputNetworkSettingFile) {
        this.learningTimes = 100;
        this.errorLimit = 0.01;
        this.learningRate = 0.01;
        this.regularizationRate = 1.0E-4;
        this.isDebug = false;
        this.isRegularization = false;
        this.isSoftmax = true;
        this.feature = null;
        this.inputUnits = inputUnits;
        this.outputUnits = outputUnits;
        this.numberOfBlock = numberOfBlock;
        this.numberOfCell = numberOfCell;
        this.numberOfInput = inputUnits + numberOfBlock * numberOfCell;
        this.learningTimes = learningTimes;
        this.learningRate = learningRate;
        this.regularizationRate = regularizationRate;
        this.errorLimit = errorLimit;
        this.vocabularyDimension = vocabularyDimension;
        this.posLabelDimension = posLabelDimension;
        this.preprocessLabelDimension = preprocessLabelDimension;
        this.corpusFile = corpusFile;
        this.vocabularyFile = vocabularyFile;
        this.semanticLabelFile = semanticLabelFile;
        this.posLabelFile = posLabelFile;
        this.preprocessLabelFile = preprocessLabelFile;
        this.outputNetworkSettingFile = outputNetworkSettingFile;
        this.outputCellUnits = numberOfBlock * numberOfCell;
        this.init();
    }
    
    private void init() {
        this.lstm_lr = new ArrayList<BiBlock>();
        for (int b = 0; b < this.numberOfBlock; ++b) {
            this.lstm_lr.add(new BiBlock(this.inputUnits, this.numberOfCell, this.numberOfInput));
        }
        this.lstm_rl = new ArrayList<BiBlock>();
        for (int b = 0; b < this.numberOfBlock; ++b) {
            this.lstm_rl.add(new BiBlock(this.inputUnits, this.numberOfCell, this.numberOfInput));
        }
        this.inputlayer = new BiInputLayer(this.inputUnits, this.outputUnits, this.vocabularyDimension, this.posLabelDimension, this.preprocessLabelDimension, this.corpusFile, this.vocabularyFile, this.semanticLabelFile, this.posLabelFile, this.preprocessLabelFile);
        this.inInput = new double[this.numberOfBlock * this.numberOfCell];
        this.outputlayer = new BiOutputLayer(this.numberOfBlock, this.numberOfCell, this.outputUnits);
        this.labelset = this.inputlayer.getLabelset();
    }
    
    public void train() {
        int currentTimes = 1;
        double totalError = 1.0;
        int predCount = 0;
        BiInputOutputPair[] sample = null;
        System.out.println("The size of samples in the corpus: " + this.inputlayer.getSize() + "\r\n");
        double[][] derivOutput = null;
        double[][] derivOutputCell_lr = null;
        double[][] derivOutputCell_rl = null;
        double[][] derivInput = null;
        double[][] derivEmbedding = null;
        boolean isCorrect = true;
        double max = 0.0;
        int tag = -1;
        double exp = 1.0;
        while (currentTimes <= this.learningTimes && totalError > this.errorLimit) {
            System.out.println("The current learing time: " + currentTimes);
            totalError = 0.0;
            predCount = 0;
            this.inputlayer.reset();
            while (this.inputlayer.hasNext()) {
                sample = this.inputlayer.next();
                final int length = sample.length;
                this.inputs_lr = new double[length][this.numberOfInput];
                this.inputs_rl = new double[length][this.numberOfInput];
                for (int b = 0; b < this.numberOfBlock; ++b) {
                    BiBlock block = this.lstm_lr.get(b);
                    block.reset(length, this.inputs_lr);
                    block.resetDeriv();
                    block = this.lstm_rl.get(b);
                    block.reset(length, this.inputs_rl);
                    block.resetDeriv();
                }
                this.outputCell_lr = new double[length][this.outputCellUnits];
                this.outputCell_rl = new double[length][this.outputCellUnits];
                this.outputlayer.reset(length, this.outputCell_lr, this.outputCell_rl);
                this.outputlayer.resetDeive();
                this.exOutput = new int[length];
                isCorrect = true;
                for (int p = 0; p < length; ++p) {
                    ++predCount;
                    this.input = new ArrayList<double[]>();
                    this.exInput = sample[p].getExInput();
                    this.exOutput[p] = sample[p].getExOutput();
                    for (int f = 0; f < this.exInput.size(); ++f) {
                        this.input.add(this.exInput.get(f));
                    }
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
                    this.input.add(this.inInput);
                    if (this.isDebug) {
                        for (int f = 0; f < this.input.size(); ++f) {
                            this.feature = this.input.get(f);
                            for (int d2 = 0; d2 < this.input.get(f).length; ++d2) {
                                System.out.print(String.valueOf(this.feature[d2]) + " ");
                            }
                        }
                        System.out.print(String.valueOf(this.exOutput[p]) + " ");
                    }
                    this.shift = 0;
                    for (int f = 0; f < this.input.size(); ++f) {
                        this.feature = this.input.get(f);
                        for (int d2 = 0; d2 < this.feature.length; ++d2) {
                            this.inputs_lr[p][this.shift] = this.feature[d2];
                            ++this.shift;
                        }
                    }
                    if (this.isDebug) {
                        for (int d = 0; d < this.numberOfInput; ++d) {
                            System.out.println("[" + currentTimes + "|" + predCount + "]: (" + p + "/" + d + ") " + this.inputs_lr[p][d] + " ");
                        }
                    }
                    for (int b2 = 0; b2 < this.numberOfBlock; ++b2) {
                        this.lstm_lr.get(b2).computeOutput(p);
                    }
                    for (int b2 = 0; b2 < this.numberOfBlock; ++b2) {
                        final BiBlock block = this.lstm_lr.get(b2);
                        for (int c = 0; c < this.numberOfCell; ++c) {
                            final BiCell cell = block.getCells().get(c);
                            this.outputCell_lr[p][b2 * this.numberOfCell + c] = cell.getOutputCell(p);
                        }
                    }
                    if (this.isDebug) {
                        for (int d = 0; d < this.outputCellUnits; ++d) {
                            System.out.println("[" + currentTimes + "|" + predCount + "]: (" + p + "/" + d + ") " + this.outputCell_lr[p][d] + " ");
                        }
                    }
                }
                for (int p = 0; p < length; ++p) {
                    this.input = new ArrayList<double[]>();
                    this.exInput = sample[length - p - 1].getExInput();
                    for (int f = 0; f < this.exInput.size(); ++f) {
                        this.input.add(this.exInput.get(f));
                    }
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
                    this.input.add(this.inInput);
                    this.shift = 0;
                    for (int f = 0; f < this.input.size(); ++f) {
                        this.feature = this.input.get(f);
                        for (int d2 = 0; d2 < this.feature.length; ++d2) {
                            this.inputs_rl[p][this.shift] = this.feature[d2];
                            ++this.shift;
                        }
                    }
                    if (this.isDebug) {
                        for (int d = 0; d < this.numberOfInput; ++d) {
                            System.out.println("[" + currentTimes + "|" + predCount + "]: (" + p + "/" + d + ") " + this.inputs_rl[p][d] + " ");
                        }
                    }
                    for (int b2 = 0; b2 < this.numberOfBlock; ++b2) {
                        this.lstm_rl.get(b2).computeOutput(p);
                    }
                    for (int b2 = 0; b2 < this.numberOfBlock; ++b2) {
                        final BiBlock block = this.lstm_rl.get(b2);
                        for (int c = 0; c < this.numberOfCell; ++c) {
                            final BiCell cell = block.getCells().get(c);
                            this.outputCell_rl[p][b2 * this.numberOfCell + c] = cell.getOutputCell(p);
                        }
                    }
                    if (this.isDebug) {
                        for (int d = 0; d < this.outputCellUnits; ++d) {
                            System.out.println("[" + currentTimes + "|" + predCount + "]: (" + p + "/" + d + ") " + this.outputCell_rl[p][d] + " ");
                        }
                    }
                }
                this.outputlayer.computeOutput();
                if (this.isDebug) {
                    double[][] output = null;
                    output = this.outputlayer.getOutput();
                    for (int p2 = 0; p2 < length; ++p2) {
                        for (int t = 0; t < this.outputUnits; ++t) {
                            System.out.print("[" + currentTimes + "]: (" + p2 + "/" + t + "): " + output[p2][t] + " ");
                        }
                        System.out.println("");
                    }
                }
                this.output = this.outputlayer.getOutput();
                derivOutput = new double[length][this.outputUnits];
                for (int p = 0; p < length; ++p) {
                    max = this.output[p][0];
                    tag = 0;
                    for (int t2 = 1; t2 < this.outputUnits; ++t2) {
                        if (this.output[p][t2] > max) {
                            max = this.output[p][t2];
                            tag = t2;
                        }
                    }
                    if (this.isDebug) {
                        System.out.println("[" + p + "|" + this.labelset[tag] + "|" + this.labelset[this.exOutput[p]] + "]");
                    }
                    if (tag != this.exOutput[p]) {
                        ++totalError;
                        isCorrect = false;
                        if (this.isSoftmax) {
                            this.output[p][0] = Math.pow(2.718281828459045, this.output[p][0]);
                            exp = this.output[p][0];
                            for (int t2 = 1; t2 < this.outputUnits; ++t2) {
                                this.output[p][t2] = Math.pow(2.718281828459045, this.output[p][t2]);
                                exp += this.output[p][t2];
                            }
                            for (int t2 = 0; t2 < this.outputUnits; ++t2) {
                                if (t2 == this.exOutput[p]) {
                                    derivOutput[p][t2] = 1.0 - this.output[p][t2] / exp;
                                }
                                else {
                                    derivOutput[p][t2] = 0.0 - this.output[p][t2] / exp;
                                }
                            }
                        }
                        else {
                            for (int t2 = 0; t2 < this.outputUnits; ++t2) {
                                if (t2 == this.exOutput[p]) {
                                    derivOutput[p][t2] = 1.0 - this.output[p][t2];
                                }
                                else {
                                    derivOutput[p][t2] = 0.0 - this.output[p][t2];
                                }
                            }
                        }
                    }
                }
                if (this.isDebug) {
                    System.out.println("");
                }
                if (!isCorrect) {
                    this.outputlayer.computeDeriv(derivOutput);
                    derivOutputCell_lr = this.outputlayer.getDerivOutputCell_lr();
                    derivOutputCell_rl = this.outputlayer.getDerivOutputCell_rl();
                    derivEmbedding = new double[length][this.inputUnits];
                    this.shift = 0;
                    for (int b = 0; b < this.numberOfBlock; ++b) {
                        final BiBlock block = this.lstm_lr.get(b);
                        for (int c2 = 0; c2 < this.numberOfCell; ++c2) {
                            final BiCell cell = block.getCells().get(c2);
                            cell.computeDeriv(derivOutputCell_lr[this.shift]);
                            ++this.shift;
                        }
                        block.computeDeriv();
                        derivInput = block.getDerivInputs();
                        for (int p2 = 0; p2 < length; ++p2) {
                            for (int i = 0; i < this.inputUnits; ++i) {
                                final double[] array = derivEmbedding[p2];
                                final int n = i;
                                array[n] += derivInput[p2][i];
                            }
                        }
                    }
                    this.shift = 0;
                    for (int b = 0; b < this.numberOfBlock; ++b) {
                        final BiBlock block = this.lstm_rl.get(b);
                        for (int c2 = 0; c2 < this.numberOfCell; ++c2) {
                            final BiCell cell = block.getCells().get(c2);
                            cell.computeDeriv(derivOutputCell_rl[this.shift]);
                            ++this.shift;
                        }
                        block.computeDeriv();
                        derivInput = block.getDerivInputs();
                        for (int p2 = 0; p2 < length; ++p2) {
                            for (int i = 0; i < this.inputUnits; ++i) {
                                final double[] array2 = derivEmbedding[p2];
                                final int n2 = i;
                                array2[n2] += derivInput[length - p2 - 1][i];
                            }
                        }
                    }
                    this.inputlayer.setDerivEmbedding(derivEmbedding);
                    this.outputlayer.update(this.learningRate);
                    for (int b = 0; b < this.numberOfBlock; ++b) {
                        final BiBlock block = this.lstm_lr.get(b);
                        block.update(this.learningRate);
                    }
                    for (int b = 0; b < this.numberOfBlock; ++b) {
                        final BiBlock block = this.lstm_rl.get(b);
                        block.update(this.learningRate);
                    }
                    this.inputlayer.update(this.learningRate);
                }
            }
            if (this.isRegularization) {
                this.outputlayer.regularize(this.regularizationRate);
                for (int b = 0; b < this.numberOfBlock; ++b) {
                    final BiBlock block = this.lstm_lr.get(b);
                    block.regularize(this.regularizationRate);
                }
                for (int b = 0; b < this.numberOfBlock; ++b) {
                    final BiBlock block = this.lstm_rl.get(b);
                    block.regularize(this.regularizationRate);
                }
            }
            totalError /= predCount;
            if (this.isDebug) {
                System.out.println("");
            }
            System.out.println("The current error is: " + totalError + " with " + predCount + " predictions." + "\r\n");
            ++currentTimes;
        }
        this.outputlayer.clean();
        for (int b = 0; b < this.numberOfBlock; ++b) {
            this.lstm_lr.get(b).clean();
        }
        for (int b = 0; b < this.numberOfBlock; ++b) {
            this.lstm_rl.get(b).clean();
        }
    }
    
    public void writePara() {
        final BiLSTMObject lstm = new BiLSTMObject(this.lstm_lr, this.lstm_rl);
        final BiLSTMSetting networkSetting = new BiLSTMSetting(this.inputUnits, this.outputUnits, this.numberOfBlock, this.numberOfCell, this.numberOfInput, lstm, this.outputlayer, this.vocabularyDimension, this.posLabelDimension, this.preprocessLabelDimension, this.inputlayer.getVocabularyLookupTable(), this.inputlayer.getPosLabelLookupTable(), this.inputlayer.getPreprocessLabelLookupTable(), this.labelset, this.inputlayer.getLabelIndex());
        try {
            final FileOutputStream fos = new FileOutputStream(this.outputNetworkSettingFile);
            final ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(networkSetting);
            oos.close();
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("The parameters of LSTM have been written into the disk.");
    }
}
