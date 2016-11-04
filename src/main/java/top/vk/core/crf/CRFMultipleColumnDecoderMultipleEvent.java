// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.crf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class CRFMultipleColumnDecoderMultipleEvent
{
    private String parameterFile;
    private String templateFile;
    private String tagFile;
    private boolean is2gram;
    private boolean isDebug;
    private int numberOfColumn;
    private HashMap<String, Double> unigram;
    private HashMap<String, Double> bigram;
    private String[] tagset;
    private ArrayList<String> templates;
    HashMap<String, String[]> labels;
    
    public CRFMultipleColumnDecoderMultipleEvent(final String confFile) {
        this.parameterFile = null;
        this.templateFile = null;
        this.tagFile = null;
        this.is2gram = true;
        this.isDebug = false;
        this.numberOfColumn = 5;
        this.unigram = new HashMap<String, Double>();
        this.bigram = new HashMap<String, Double>();
        this.tagset = null;
        this.templates = new ArrayList<String>();
        this.labels = null;
        final Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(confFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.tagFile = prop.getProperty("labelFile");
        this.templateFile = prop.getProperty("templateFile");
        this.parameterFile = prop.getProperty("parameterFile");
        this.is2gram = Boolean.parseBoolean(prop.getProperty("is2gram"));
        this.numberOfColumn = Integer.parseInt(prop.getProperty("numberOfColumn"));
    }
    
    public void init() {
        this.labels = new HashMap<String, String[]>();
        try {
            final FileInputStream fis = new FileInputStream(this.tagFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            String line = null;
            String tags = "";
            boolean isFirstLine = true;
            String key = null;
            String[] tokens = null;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll(" ", "");
                line = line.trim();
                if (!line.equals("") && !line.equals(" ")) {
                    if (isFirstLine) {
                        key = line;
                        isFirstLine = false;
                    }
                    else {
                        tags = String.valueOf(tags) + line + " ";
                    }
                }
                else {
                    isFirstLine = true;
                    tags = tags.trim();
                    tokens = tags.split("\\s+");
                    this.labels.put(key, tokens);
                    tags = "";
                }
            }
            tags = tags.trim();
            tokens = tags.split("\\s+");
            this.labels.put(key, tokens);
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            final FileInputStream fis = new FileInputStream(this.templateFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            String line = null;
            String[] tokens2 = null;
            String str = null;
            while ((line = br.readLine()) != null) {
                if (!line.equals("") && !line.startsWith("#")) {
                    if (line.startsWith("U")) {
                        tokens2 = line.split(":");
                        str = tokens2[1].replaceAll("[%x\\[\\]]", "");
                        tokens2 = str.split("[,/]");
                        str = "Unigram";
                        for (int i = 0; i < tokens2.length; ++i) {
                            str = String.valueOf(str) + " " + tokens2[i];
                        }
                        this.templates.add(str);
                    }
                    else {
                        if (!line.startsWith("B")) {
                            continue;
                        }
                        tokens2 = line.split(":");
                        str = tokens2[1].replaceAll("[%x\\[\\]]", "");
                        tokens2 = str.split("[,/]");
                        str = "Bigram";
                        for (int i = 0; i < tokens2.length; ++i) {
                            str = String.valueOf(str) + " " + tokens2[i];
                        }
                        this.templates.add(str);
                    }
                }
            }
            if (this.isDebug) {
                System.out.println("The file of template has been imported.");
                System.out.println("Templates: " + this.templates + "\r\n");
            }
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            final FileInputStream fis = new FileInputStream(this.parameterFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            String line = null;
            String[] tokens2 = null;
            while ((line = br.readLine()) != null) {
                if (!line.equals("") && !line.equals(" ")) {
                    tokens2 = line.split(":");
                    if (tokens2[0].startsWith("U")) {
                        this.unigram.put(tokens2[0], Double.valueOf(tokens2[1]));
                    }
                    else {
                        if (!tokens2[0].startsWith("B")) {
                            continue;
                        }
                        this.bigram.put(tokens2[0], Double.valueOf(tokens2[1]));
                    }
                }
            }
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (this.isDebug) {
            Iterator<Map.Entry<String, Double>> it = null;
            Map.Entry<String, Double> entry = null;
            it = this.unigram.entrySet().iterator();
            while (it.hasNext()) {
                entry = it.next();
                System.out.println(String.valueOf(entry.getKey()) + ":  " + entry.getValue());
            }
            it = this.bigram.entrySet().iterator();
            while (it.hasNext()) {
                entry = it.next();
                System.out.println(String.valueOf(entry.getKey()) + ":  " + entry.getValue());
            }
            System.out.println("");
        }
    }
    
    public String[] decodeSentence(final String[][] sent, final String eventType) {
        if (this.numberOfColumn != sent.length) {
            System.out.println(sent.length);
        }
        this.tagset = this.labels.get(eventType);
        Iterator<String> tit = null;
        String[] tokens = null;
        String pattern = null;
        int row = -1;
        int col = -1;
        int cur = -1;
        boolean record = true;
        final int length = sent[0].length;
        final double[][] optVal = new double[this.tagset.length][length];
        final int[][] optRec = new int[this.tagset.length][length];
        for (int i = 0; i < this.tagset.length; ++i) {
            for (int j = 0; j < length; ++j) {
                optVal[i][j] = 0.0;
                optRec[i][j] = 0;
            }
        }
        for (int i = 0; i < length; ++i) {
            for (int lb = 0; lb < this.tagset.length; ++lb) {
                if (i == 0) {
                    if (this.is2gram) {
                        pattern = "B \u2560 " + this.tagset[lb];
                        if (this.bigram.containsKey(pattern)) {
                            final double[] array = optVal[lb];
                            final int n = i;
                            array[n] += this.bigram.get(pattern);
                        }
                    }
                    tit = this.templates.iterator();
                    while (tit.hasNext()) {
                        tokens = tit.next().split("\\s+");
                        if (tokens[0].equals("Unigram")) {
                            pattern = "U";
                            record = true;
                            for (int k = 1; k < tokens.length; k += 2) {
                                row = Integer.valueOf(tokens[k]);
                                col = Integer.valueOf(tokens[k + 1]);
                                cur = i + row;
                                if (cur < 0 || cur > length - 1) {
                                    record = false;
                                    break;
                                }
                                pattern = String.valueOf(pattern) + " " + row + " " + col + " " + sent[col][cur];
                            }
                            if (!record) {
                                continue;
                            }
                            pattern = String.valueOf(pattern) + " " + this.tagset[lb];
                            if (!this.unigram.containsKey(pattern)) {
                                continue;
                            }
                            final double[] array2 = optVal[lb];
                            final int n2 = i;
                            array2[n2] += this.unigram.get(pattern);
                        }
                    }
                }
                else {
                    double unisum = 0.0;
                    tit = this.templates.iterator();
                    while (tit.hasNext()) {
                        tokens = tit.next().split("\\s");
                        if (tokens[0].equals("Unigram")) {
                            pattern = "U";
                            record = true;
                            for (int l = 1; l < tokens.length; l += 2) {
                                row = Integer.valueOf(tokens[l]);
                                col = Integer.valueOf(tokens[l + 1]);
                                cur = i + row;
                                if (cur < 0 || cur > length - 1) {
                                    record = false;
                                    break;
                                }
                                pattern = String.valueOf(pattern) + " " + row + " " + col + " " + sent[col][cur];
                            }
                            if (!record) {
                                continue;
                            }
                            pattern = String.valueOf(pattern) + " " + this.tagset[lb];
                            if (!this.unigram.containsKey(pattern)) {
                                continue;
                            }
                            unisum += this.unigram.get(pattern);
                        }
                    }
                    double max = -4.9E-324;
                    double local = 0.0;
                    int trace = 0;
                    for (int prev = 0; prev < this.tagset.length; ++prev) {
                        int bisum = 0;
                        tit = this.templates.iterator();
                        while (tit.hasNext()) {
                            tokens = tit.next().split("\\s+");
                            if (tokens[0].equals("Bigram")) {
                                pattern = "B";
                                record = true;
                                for (int m = 1; m < tokens.length; m += 2) {
                                    row = Integer.valueOf(tokens[m]);
                                    col = Integer.valueOf(tokens[m + 1]);
                                    cur = i + row;
                                    if (cur < 0 || cur > length - 1) {
                                        record = false;
                                        break;
                                    }
                                    pattern = String.valueOf(pattern) + " " + row + " " + col + " " + sent[col][cur];
                                }
                                if (!record) {
                                    continue;
                                }
                                pattern = String.valueOf(pattern) + " " + this.tagset[prev] + " " + this.tagset[lb];
                                if (!this.bigram.containsKey(pattern)) {
                                    continue;
                                }
                                bisum += (int)(Object)this.bigram.get(pattern);
                            }
                        }
                        if (this.is2gram) {
                            pattern = "B " + this.tagset[prev] + " " + this.tagset[lb];
                            if (this.bigram.containsKey(pattern)) {
                                bisum += (int)(Object)this.bigram.get(pattern);
                            }
                        }
                        local = unisum + bisum + optVal[prev][i - 1];
                        if (local > max) {
                            max = local;
                            trace = prev;
                        }
                    }
                    optVal[lb][i] = max;
                    optRec[lb][i] = trace;
                }
            }
        }
        if (this.isDebug) {
            for (int i = 0; i < this.tagset.length; ++i) {
                for (int j = 0; j < length; ++j) {
                    System.out.print("( ");
                    System.out.printf("%.2f", optVal[i][j]);
                    System.out.print(" | " + optRec[i][j] + " | " + sent[0][j] + " )  ");
                }
                System.out.println("");
            }
        }
        double optValue = optVal[0][length - 1];
        int optLable = 0;
        for (int k2 = 1; k2 < this.tagset.length; ++k2) {
            if (optVal[k2][length - 1] > optValue) {
                optValue = optVal[k2][length - 1];
                optLable = k2;
            }
        }
        int previous = optLable;
        String optSequence = this.tagset[optLable];
        for (int i2 = length - 1; i2 > 0; --i2) {
            previous = optRec[previous][i2];
            optSequence = String.valueOf(this.tagset[previous]) + " " + optSequence;
        }
        final String[] labels = optSequence.split("\\s+");
        if (this.isDebug) {
            for (int i3 = 0; i3 < length; ++i3) {
                System.out.print(String.valueOf(sent[0][i3]) + "/" + labels[i3] + " ");
            }
            System.out.println("\r\n");
        }
        return labels;
    }
    
    static String getOther() {
        return "O";
    }
    
    public int getNumberOfColumn() {
        return this.numberOfColumn;
    }
}
