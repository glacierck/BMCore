// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.crf;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

public class ConditionalRandomField
{
    String tagFile;
    String templateFile;
    String corpusFile;
    String parameterFile;
    int numberOfColumn;
    int learningTimes;
    double errorLimit;
    int times;
    double errorRate;
    int totalNumber;
    int wrongNumber;
    boolean is2gram;
    boolean isDebug;
    String[] tagset;
    ArrayList<String> templates;
    ArrayList<String[][]> sentences;
    HashMap<String, Integer> bigram;
    HashMap<String, Integer> unigram;
    
    public ConditionalRandomField(final String confFile) {
        this.tagFile = null;
        this.templateFile = null;
        this.corpusFile = null;
        this.parameterFile = null;
        this.numberOfColumn = 2;
        this.learningTimes = 1;
        this.errorLimit = 0.0;
        this.times = 0;
        this.errorRate = 1.0;
        this.totalNumber = 0;
        this.wrongNumber = 0;
        this.is2gram = true;
        this.isDebug = false;
        this.tagset = null;
        this.templates = new ArrayList<String>();
        this.sentences = new ArrayList<String[][]>();
        this.bigram = new HashMap<String, Integer>();
        this.unigram = new HashMap<String, Integer>();
        final Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(confFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.corpusFile = prop.getProperty("corpusFile");
        this.tagFile = prop.getProperty("labelFile");
        this.templateFile = prop.getProperty("templateFile");
        this.parameterFile = prop.getProperty("parameterFile");
        this.numberOfColumn = Integer.parseInt(prop.getProperty("numberOfColumn"));
        this.learningTimes = Integer.parseInt(prop.getProperty("learningTimes"));
        this.errorLimit = Double.parseDouble(prop.getProperty("errorLimit"));
        this.is2gram = Boolean.parseBoolean(prop.getProperty("is2gram"));
    }
    
    public ConditionalRandomField(final String tagFile, final String templateFile, final String corpusFile, final String parameterFile, final int numberOfColumn, final int learningTimes, final double errorLimit) {
        this.tagFile = null;
        this.templateFile = null;
        this.corpusFile = null;
        this.parameterFile = null;
        this.numberOfColumn = 2;
        this.learningTimes = 1;
        this.errorLimit = 0.0;
        this.times = 0;
        this.errorRate = 1.0;
        this.totalNumber = 0;
        this.wrongNumber = 0;
        this.is2gram = true;
        this.isDebug = false;
        this.tagset = null;
        this.templates = new ArrayList<String>();
        this.sentences = new ArrayList<String[][]>();
        this.bigram = new HashMap<String, Integer>();
        this.unigram = new HashMap<String, Integer>();
        this.tagFile = tagFile;
        this.templateFile = templateFile;
        this.corpusFile = corpusFile;
        this.parameterFile = parameterFile;
        this.numberOfColumn = numberOfColumn;
        this.learningTimes = learningTimes;
        this.errorLimit = errorLimit;
    }
    
    public void init() {
        try {
            final FileInputStream fis = new FileInputStream(this.tagFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            String line = null;
            String tags = "";
            int tagnum = 0;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll(" ", "");
                line = line.trim();
                if (!line.equals("") && !line.equals(" ")) {
                    ++tagnum;
                    tags = String.valueOf(tags) + line + " ";
                }
            }
            this.tagset = new String[tagnum];
            final String[] tokens = tags.split("\\s+");
            for (int i = 0; i < this.tagset.length; ++i) {
                this.tagset[i] = tokens[i];
            }
            line = null;
            System.out.println("The file of tags has been imported.");
            if (this.isDebug) {
                for (int i = 0; i < this.tagset.length; ++i) {
                    System.out.println(this.tagset[i]);
                }
                System.out.println("");
            }
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
                line = line.replaceAll(" ", "");
                line = line.trim();
                if (!line.equals("") && !line.startsWith("#")) {
                    if (line.startsWith("U")) {
                        tokens2 = line.split(":");
                        str = tokens2[1].replaceAll("[%x\\[\\]]", "");
                        tokens2 = str.split("[,/]");
                        str = "Unigram";
                        for (int j = 0; j < tokens2.length; ++j) {
                            str = String.valueOf(str) + " " + tokens2[j];
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
                        for (int j = 0; j < tokens2.length; ++j) {
                            str = String.valueOf(str) + " " + tokens2[j];
                        }
                        this.templates.add(str);
                    }
                }
            }
            System.out.println("The file of template has been imported.");
            if (this.isDebug) {
                System.out.println("Templates: " + this.templates + "\r\n");
            }
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void run() {
        System.out.println("Training is running... \r\n");
        Integer featureValue = 0;
        for (int t = 1; t < this.learningTimes; ++t) {
            this.times = t;
            this.totalNumber = 0;
            if (this.errorRate < this.errorLimit) {
                System.out.println("The error rate reached the defined value.");
                break;
            }
            System.out.println("The training time: " + t);
            try {
                final FileInputStream fis = new FileInputStream(this.corpusFile);
                final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                final BufferedReader br = new BufferedReader(isr);
                String line = null;
                String[] tokens = null;
                ArrayList<String> cell = new ArrayList<String>();
                Iterator<String> it = null;
                String[][] sent = null;
                int num = 0;
                int row = -1;
                int col = -1;
                int cur = -1;
                String pattern = null;
                boolean record = true;
                Iterator<String> tit = null;
                while ((line = br.readLine()) != null) {
                    if (!line.equals("") && !line.equals(" ")) {
                        tokens = line.split("\\s+");
                        if (tokens.length != this.numberOfColumn) {
                            System.out.println("The number of items is not equal to the number of column.");
                            System.exit(0);
                        }
                        for (int coln = 0; coln < this.numberOfColumn; ++coln) {
                            cell.add(tokens[coln]);
                        }
                        ++num;
                    }
                    else {
                        if (!line.equals("") && !line.equals(" ")) {
                            continue;
                        }
                        this.totalNumber += num;
                        sent = new String[this.numberOfColumn][num];
                        it = cell.iterator();
                        int pos = 0;
                        while (it.hasNext()) {
                            for (int coln2 = 0; coln2 < this.numberOfColumn; ++coln2) {
                                sent[coln2][pos] = it.next();
                            }
                            ++pos;
                        }
                        if (num <= 0) {
                            continue;
                        }
                        num = 0;
                        cell = new ArrayList<String>();
                        final int length = sent[0].length;
                        int[][][] opt = new int[this.tagset.length][length][2];
                        for (int i = 0; i < this.tagset.length; ++i) {
                            for (int j = 0; j < length; ++j) {
                                opt[i][j][0] = 0;
                                opt[i][j][1] = 0;
                            }
                        }
                        for (int i = 0; i < length; ++i) {
                            for (int lb = 0; lb < this.tagset.length; ++lb) {
                                if (i == 0) {
                                    if (this.is2gram) {
                                        pattern = "B \u2560 " + this.tagset[lb];
                                        if (this.bigram.containsKey(pattern)) {
                                            final int[] array = opt[lb][i];
                                            final int n = 0;
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
                                            final int[] array2 = opt[lb][i];
                                            final int n2 = 0;
                                            array2[n2] += this.unigram.get(pattern);
                                        }
                                    }
                                }
                                else {
                                    int unisum = 0;
                                    tit = this.templates.iterator();
                                    while (tit.hasNext()) {
                                        tokens = tit.next().split("\\s+");
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
                                    int max = -2147483647;
                                    int local = 0;
                                    int trace = -1;
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
                                                bisum += this.bigram.get(pattern);
                                            }
                                        }
                                        if (this.is2gram) {
                                            pattern = "B " + this.tagset[prev] + " " + this.tagset[lb];
                                            if (this.bigram.containsKey(pattern)) {
                                                bisum += this.bigram.get(pattern);
                                            }
                                        }
                                        local = unisum + bisum + opt[prev][i - 1][0];
                                        if (local > max) {
                                            max = local;
                                            trace = prev;
                                        }
                                    }
                                    opt[lb][i][0] = max;
                                    opt[lb][i][1] = trace;
                                }
                            }
                        }
                        if (this.isDebug) {
                            for (int i = 0; i < this.tagset.length; ++i) {
                                for (int j = 0; j < length; ++j) {
                                    System.out.print("( " + opt[i][j][0] + " | " + opt[i][j][1] + " | " + sent[0][j] + " )  ");
                                }
                                System.out.println("");
                            }
                        }
                        int optValue = opt[0][length - 1][0];
                        int optLable = 0;
                        for (int k = 1; k < this.tagset.length; ++k) {
                            if (opt[k][length - 1][0] > optValue) {
                                optValue = opt[k][length - 1][0];
                                optLable = k;
                            }
                        }
                        int previous = optLable;
                        String optSequence = this.tagset[optLable];
                        for (int i2 = length - 1; i2 > 0; --i2) {
                            previous = opt[previous][i2][1];
                            optSequence = String.valueOf(this.tagset[previous]) + " " + optSequence;
                        }
                        if (this.isDebug) {
                            System.out.print("\r\n");
                            System.out.println("Current taining time: " + t);
                        }
                        final String[] lables = optSequence.split("\\s+");
                        if (this.isDebug) {
                            System.out.println("The optimal sequence: " + optSequence);
                            for (int i3 = 0; i3 < length; ++i3) {
                                System.out.print(String.valueOf(sent[0][i3]) + "/" + lables[i3] + " ");
                            }
                            System.out.print("\r\n");
                            for (int i3 = 0; i3 < length; ++i3) {
                                System.out.print(String.valueOf(sent[0][i3]) + "/" + sent[1][i3] + " ");
                            }
                            System.out.print("\r\n");
                        }
                        opt = null;
                        final String[] labels = optSequence.split("\\s+");
                        if (lables.length != length) {
                            System.exit(0);
                        }
                        String pattern2 = "";
                        String pattern3 = "";
                        for (int i4 = 0; i4 < length; ++i4) {
                            if (!lables[i4].equals(sent[1][i4])) {
                                ++this.wrongNumber;
                                if (this.isDebug) {
                                    System.out.print(String.valueOf(sent[0][i4]) + ": " + sent[1][i4] + "/" + lables[i4] + "  ");
                                }
                                if (i4 == 0) {
                                    pattern2 = "B \u2560 " + lables[i4];
                                    pattern3 = "B \u2560 " + sent[1][i4];
                                    if (this.bigram.containsKey(pattern2)) {
                                        featureValue = this.bigram.get(pattern2);
                                        this.bigram.put(pattern2, featureValue - 1);
                                    }
                                    else {
                                        this.bigram.put(pattern2, -1);
                                    }
                                    if (this.bigram.containsKey(pattern3)) {
                                        featureValue = this.bigram.get(pattern3);
                                        this.bigram.put(pattern3, featureValue + 1);
                                    }
                                    else {
                                        this.bigram.put(pattern3, 1);
                                    }
                                }
                                tit = this.templates.iterator();
                                while (tit.hasNext()) {
                                    tokens = tit.next().split("\\s+");
                                    if (tokens[0].equals("Unigram")) {
                                        pattern2 = "U";
                                        pattern3 = "U";
                                        record = true;
                                        for (int k2 = 1; k2 < tokens.length; k2 += 2) {
                                            row = Integer.valueOf(tokens[k2]);
                                            col = Integer.valueOf(tokens[k2 + 1]);
                                            cur = i4 + row;
                                            if (cur < 0 || cur > length - 1) {
                                                record = false;
                                                break;
                                            }
                                            pattern2 = String.valueOf(pattern2) + " " + row + " " + col + " " + sent[col][cur];
                                            pattern3 = String.valueOf(pattern3) + " " + row + " " + col + " " + sent[col][cur];
                                        }
                                        if (!record) {
                                            continue;
                                        }
                                        pattern2 = String.valueOf(pattern2) + " " + labels[i4];
                                        pattern3 = String.valueOf(pattern3) + " " + sent[1][i4];
                                        if (this.unigram.containsKey(pattern2)) {
                                            featureValue = this.unigram.get(pattern2);
                                            this.unigram.put(pattern2, featureValue - 1);
                                        }
                                        else {
                                            this.unigram.put(pattern2, -1);
                                        }
                                        if (this.unigram.containsKey(pattern3)) {
                                            featureValue = this.unigram.get(pattern3);
                                            this.unigram.put(pattern3, featureValue + 1);
                                        }
                                        else {
                                            this.unigram.put(pattern3, 1);
                                        }
                                    }
                                    else {
                                        if (!tokens[0].equals("Bigram") || i4 < 1) {
                                            continue;
                                        }
                                        pattern2 = "B";
                                        pattern3 = "B";
                                        record = true;
                                        for (int k2 = 1; k2 < tokens.length; k2 += 2) {
                                            row = Integer.valueOf(tokens[k2]);
                                            col = Integer.valueOf(tokens[k2 + 1]);
                                            cur = i4 + row;
                                            if (cur < 0 || cur > length - 1) {
                                                record = false;
                                                break;
                                            }
                                            pattern2 = String.valueOf(pattern2) + " " + row + " " + col + " " + sent[col][cur];
                                            pattern3 = String.valueOf(pattern3) + " " + row + " " + col + " " + sent[col][cur];
                                        }
                                        if (record) {
                                            pattern2 = String.valueOf(pattern2) + " " + labels[i4 - 1] + " " + labels[i4];
                                            pattern3 = String.valueOf(pattern3) + " " + sent[1][i4 - 1] + " " + sent[1][i4];
                                            if (this.bigram.containsKey(pattern2)) {
                                                featureValue = this.bigram.get(pattern2);
                                                this.bigram.put(pattern2, featureValue - 1);
                                            }
                                            else {
                                                this.bigram.put(pattern2, -1);
                                            }
                                            if (this.bigram.containsKey(pattern3)) {
                                                featureValue = this.bigram.get(pattern3);
                                                this.bigram.put(pattern3, featureValue + 1);
                                            }
                                            else {
                                                this.bigram.put(pattern3, 1);
                                            }
                                        }
                                        if (!this.is2gram) {
                                            continue;
                                        }
                                        pattern2 = "B " + labels[i4 - 1] + " " + labels[i4];
                                        pattern3 = "B " + sent[1][i4 - 1] + " " + sent[1][i4];
                                        if (this.bigram.containsKey(pattern2)) {
                                            featureValue = this.bigram.get(pattern2);
                                            this.bigram.put(pattern2, featureValue - 1);
                                        }
                                        else {
                                            this.bigram.put(pattern2, -1);
                                        }
                                        if (this.bigram.containsKey(pattern3)) {
                                            featureValue = this.bigram.get(pattern3);
                                            this.bigram.put(pattern3, featureValue + 1);
                                        }
                                        else {
                                            this.bigram.put(pattern3, 1);
                                        }
                                    }
                                }
                            }
                        }
                        if (!this.isDebug) {
                            continue;
                        }
                        System.out.println("");
                        System.out.print("\r\n");
                    }
                }
                br.close();
                isr.close();
                fis.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            this.errorRate = this.wrongNumber / this.totalNumber;
            System.out.println("The error rate: " + this.errorRate + " (" + this.wrongNumber + "/" + this.totalNumber + ")" + "\r\n");
            this.wrongNumber = 0;
        }
        try {
            final FileOutputStream fos = new FileOutputStream(this.parameterFile);
            final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            Iterator<String> it2 = this.unigram.keySet().iterator();
            String key = null;
            while (it2.hasNext()) {
                key = it2.next();
                osw.write(String.valueOf(key) + ": " + this.unigram.get(key) + "\r\n");
            }
            it2 = this.bigram.keySet().iterator();
            while (it2.hasNext()) {
                key = it2.next();
                osw.write(String.valueOf(key) + ": " + this.bigram.get(key) + "\r\n");
            }
            osw.flush();
            osw.close();
            fos.close();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        System.out.println("Training is completed.");
    }
    
    public boolean isDebug() {
        return this.isDebug;
    }
    
    public void setDebug(final boolean isDebug) {
        this.isDebug = isDebug;
    }
}
