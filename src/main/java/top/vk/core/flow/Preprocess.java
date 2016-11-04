// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.flow;

import top.vk.core.lang.IntermediateResult;
import top.vk.core.lang.Item;
import top.vk.core.lang.MeasureItem;
import top.vk.core.lang.SlangItem;
import top.vk.core.recognizer.*;
import top.vk.core.util.CheckPunctuation;
import top.vk.core.util.ChineseNormalize;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class Preprocess {
    private static String DigitLabel = "D";
    private static String EmailTag = "FW";
    private static String UrlTag = "FW";
    private static String DateTag = "NT";
    private static String PercentTag;
    private static String MeasureTag;
    private static String TimeTag;
    private static String CurrencyTag;
    private static String PeriodTag;
    private static String CellphoneTag;
    private static String LandlineTag;
    private static String DomainTag;
    private static String IdiomTag;
    private static String FoodTag;
    private static String SceneTag;
    private static String TitleTag;
    private static String DeviceTag;
    private static String OrganizationTag;
    private static String LocationTag;
    private static String PersonTag;
    private static String ForeignTag;
    private static String DigitTag;
    private static String PunctuationTag;
    private static String CONNECTOR;
    private String confFile = null;
    private String domainFile = null;
    private String idiomFile = null;
    private String foodFile = null;
    private String sceneFile = null;
    private String titleFile = null;
    private String deviceFile = null;
    private String slangFile = null;
    private String organizationFile = null;
    private String nerRecogizerFile = null;
    private String personFile = null;
    private String symbol = "O";
    private boolean isBIOES = true;
    private EmailRecognizer emailRecognizer = null;
    private URLRecognizer urlRecognizer = null;
    private DateRecognizer dateRecognizer = null;
    private TimeRecognizer timeRecognizer = null;
    private PeriodRecognizer periodRecognizer = null;
    private MeasureRecognizer measureRecognizer = null;
    private PercentRecognizer percentRecognizer = null;
    private CellphoneRecognizer cellphoneRecognizer = null;
    private LandlineRecognizer phoneRecognizer = null;
    private CurrencyRecognizer currencyRecognizer = null;
    private DomainRecognizer domainRecognizer = null;
    private IdiomRecognizer idiomRecognizer = null;
    private FoodRecognizer foodRecognizer = null;
    private SceneRecognizer sceneRecognizer = null;
    private TitleRecognizer titleRecognizer = null;
    private DeviceRecognizer deviceRecognizer = null;
    private SlangRecognizer slangRecognizer = null;
    private OrganizationRecognizer organizationRecognizer = null;
    private LocationRecognizer locationRecognizer = null;
    private PersonRecognizer personRecognizer = null;
    private NerRecognizer nerRecognizer = null;
    private ForeignRecognizer foreignRecognizer = null;
    private DigitRecognizer digitRecognizer = null;
    private PunctuationRecognizer punctuationRecognizer = null;
    private String replaceBlank = "□";
    private boolean isCaseSensitive = true;
    private boolean isDeleteAllBlank = true;

    static {
        PercentTag = DigitLabel;
        MeasureTag = "M";
        TimeTag = "NT";
        CurrencyTag = DigitLabel;
        PeriodTag = "NT";
        CellphoneTag = DigitLabel;
        LandlineTag = DigitLabel;
        DomainTag = "NR";
        IdiomTag = "I";
        FoodTag = "NR";
        SceneTag = "NR";
        TitleTag = "NR";
        DeviceTag = "NR";
        OrganizationTag = "NR";
        LocationTag = "NR";
        PersonTag = "NR";
        ForeignTag = "FW";
        DigitTag = DigitLabel;
        PunctuationTag = "PU";
        CONNECTOR = "_";
    }

    public Preprocess(String confFile) {
        this.confFile = confFile;
        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream(confFile));
        } catch (IOException var13) {
            var13.printStackTrace();
        }

        this.isCaseSensitive = Boolean.parseBoolean(prop.getProperty("isCaseSensitive"));
        this.domainFile = prop.getProperty("domainFile");
        this.idiomFile = prop.getProperty("idiomFile");
        this.foodFile = prop.getProperty("foodFile");
        this.sceneFile = prop.getProperty("sceneFile");
        this.titleFile = prop.getProperty("titleFile");
        this.deviceFile = prop.getProperty("deviceFile");
        this.slangFile = prop.getProperty("slangFile");
        this.organizationFile = prop.getProperty("organizationFile");
        this.personFile = prop.getProperty("personFile");
        this.nerRecogizerFile = prop.getProperty("nerRecogizerFile");
        this.isBIOES = Boolean.parseBoolean(prop.getProperty("isBIOES"));
        this.isDeleteAllBlank = Boolean.parseBoolean(prop.getProperty("isDeleteAllBlank"));
        this.emailRecognizer = new EmailRecognizer();
        this.urlRecognizer = new URLRecognizer();
        this.dateRecognizer = new DateRecognizer();
        this.measureRecognizer = new MeasureRecognizer();
        this.timeRecognizer = new TimeRecognizer();
        this.percentRecognizer = new PercentRecognizer();
        this.cellphoneRecognizer = new CellphoneRecognizer();
        this.phoneRecognizer = new LandlineRecognizer();
        this.currencyRecognizer = new CurrencyRecognizer();
        this.periodRecognizer = new PeriodRecognizer();

        try {
            this.domainRecognizer = new DomainRecognizer(new File(this.domainFile), this.isCaseSensitive);
        } catch (IOException var12) {
            var12.printStackTrace();
        }

        try {
            this.idiomRecognizer = new IdiomRecognizer(new File(this.idiomFile), this.isCaseSensitive);
        } catch (IOException var11) {
            var11.printStackTrace();
        }

        try {
            this.foodRecognizer = new FoodRecognizer(new File(this.foodFile), this.isCaseSensitive);
        } catch (IOException var10) {
            var10.printStackTrace();
        }

        try {
            this.sceneRecognizer = new SceneRecognizer(new File(this.sceneFile), this.isCaseSensitive);
        } catch (IOException var9) {
            var9.printStackTrace();
        }

        try {
            this.titleRecognizer = new TitleRecognizer(new File(this.titleFile), this.isCaseSensitive);
        } catch (IOException var8) {
            var8.printStackTrace();
        }

        try {
            this.deviceRecognizer = new DeviceRecognizer(new File(this.deviceFile), this.isCaseSensitive);
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        try {
            this.slangRecognizer = new SlangRecognizer(new File(this.slangFile), this.isCaseSensitive);
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        try {
            this.organizationRecognizer = new OrganizationRecognizer(new File(this.organizationFile), this.isCaseSensitive);
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        this.locationRecognizer = new LocationRecognizer();

        try {
            this.personRecognizer = new PersonRecognizer(new File(this.personFile), this.isCaseSensitive);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        this.nerRecognizer = new NerRecognizer(this.nerRecogizerFile);
        this.foreignRecognizer = new ForeignRecognizer();
        this.digitRecognizer = new DigitRecognizer();
        this.punctuationRecognizer = new PunctuationRecognizer();
    }

    public String normalize(String sentence) {
        sentence = sentence.trim();
        if(this.isCaseSensitive) {
            sentence = ChineseNormalize.normalize(sentence);
        } else {
            sentence = ChineseNormalize.normalize(sentence, false);
        }

        if(!CheckPunctuation.isPunctation(sentence.substring(sentence.length() - 1))) {
            sentence = sentence + "。";
        }

        if(!this.isDeleteAllBlank) {
            sentence = sentence.replaceAll("\\s+", this.replaceBlank);
        } else {
            char[] ch = sentence.toCharArray();
            sentence = "";

            for(int p = 0; p < ch.length; ++p) {
                if(p != 0 && p != ch.length - 1) {
                    if(ch[p] == 32) {
                        if(this.isLetter(ch[p - 1]) && this.isLetter(ch[p + 1])) {
                            sentence = sentence + this.replaceBlank;
                        }
                    } else {
                        sentence = sentence + ch[p];
                    }
                } else {
                    sentence = sentence + ch[p];
                }
            }
        }

        return sentence;
    }

    public String replaceSlang(String sentence) {
        ArrayList slangList = new ArrayList();
        SlangItem slangItem = null;
        this.slangRecognizer.recognize(sentence, slangList);

        for(int i = slangList.size() - 1; i >= 0; --i) {
            slangItem = (SlangItem)slangList.get(i);
            sentence = sentence.substring(0, slangItem.getStart()) + slangItem.getExplanation() + sentence.substring(slangItem.getEnd());
        }

        slangList.clear();
        return sentence;
    }

    public IntermediateResult processForSegmentation(String sentence) {
        int length = sentence.length();
        String[] labels = new String[length];
        String constraints = "";

        int indexBlank;
        for(indexBlank = 0; indexBlank < sentence.length(); ++indexBlank) {
            labels[indexBlank] = this.symbol;
        }

        for(indexBlank = sentence.indexOf(this.replaceBlank); indexBlank != -1; indexBlank = sentence.indexOf(this.replaceBlank, indexBlank + 1)) {
            labels[indexBlank] = "S";
        }

        ArrayList rawList = new ArrayList();
        ArrayList itemList = new ArrayList();
        this.emailRecognizer.recognize(sentence, rawList);
        this.urlRecognizer.recognize(sentence, rawList);
        this.dateRecognizer.recognize(sentence, rawList);
        this.percentRecognizer.recognize(sentence, rawList);
        this.measureRecognizer.recognize(sentence, rawList);
        this.timeRecognizer.recognize(sentence, rawList);
        this.periodRecognizer.recognize(sentence, rawList);
        this.currencyRecognizer.recognize(sentence, rawList);
        this.cellphoneRecognizer.recognize(sentence, rawList);
        this.phoneRecognizer.recognize(sentence, rawList);
        this.domainRecognizer.recognize(sentence, rawList);
        this.idiomRecognizer.recognize(sentence, rawList);
        this.foodRecognizer.recognize(sentence, rawList);
        this.sceneRecognizer.recognize(sentence, rawList);
        this.titleRecognizer.recognize(sentence, rawList);
        this.deviceRecognizer.recognize(sentence, rawList);
        this.organizationRecognizer.recognize(sentence, rawList);
        this.locationRecognizer.recognize(sentence, rawList);
        this.personRecognizer.recognize(sentence, rawList);
        this.nerRecognizer.recognize(sentence, rawList);
        this.foreignRecognizer.recognize(sentence, rawList);
        this.digitRecognizer.recognize(sentence, rawList);
        this.punctuationRecognizer.recognize(sentence, rawList);
        Iterator it = rawList.iterator();
        Item item = null;
        MeasureItem measureItem = null;

        while(true) {
            int intermediateResult;
            do {
                label74:
                do {
                    while(it.hasNext()) {
                        item = (Item)it.next();
                        if(item instanceof MeasureItem) {
                            measureItem = (MeasureItem)item;
                            continue label74;
                        }

                        if(labels[item.getStart()].equals(this.symbol) && labels[item.getEnd() - 1].equals(this.symbol)) {
                            if(item.getStart() == item.getEnd() - 1) {
                                if(this.isBIOES) {
                                    labels[item.getStart()] = "S";
                                } else {
                                    labels[item.getStart()] = "B";
                                }
                            } else {
                                labels[item.getStart()] = "B";

                                for(intermediateResult = item.getStart() + 1; intermediateResult < item.getEnd() - 1; ++intermediateResult) {
                                    labels[intermediateResult] = "I";
                                }

                                if(this.isBIOES) {
                                    labels[item.getEnd() - 1] = "E";
                                } else {
                                    labels[item.getEnd() - 1] = "I";
                                }
                            }

                            itemList.add(item);
                        }
                    }

                    for(intermediateResult = 0; intermediateResult < length - 1; ++intermediateResult) {
                        constraints = constraints + labels[intermediateResult] + " ";
                    }

                    constraints = constraints + labels[length - 1];
                    IntermediateResult var12 = new IntermediateResult(constraints, itemList);
                    rawList = null;
                    return var12;
                } while(!labels[measureItem.getStart()].equals(this.symbol));
            } while(!labels[measureItem.getEnd() - 1].equals(this.symbol));

            if(measureItem.getStart() == measureItem.getEnd() - 1) {
                if(this.isBIOES) {
                    labels[measureItem.getStart()] = "S";
                } else {
                    labels[measureItem.getStart()] = "B";
                }
            } else {
                labels[measureItem.getStart()] = "B";

                for(intermediateResult = measureItem.getStart() + 1; intermediateResult < measureItem.getEnd() - 1; ++intermediateResult) {
                    labels[intermediateResult] = "I";
                }

                if(this.isBIOES) {
                    labels[measureItem.getEnd() - 1] = "E";
                } else {
                    labels[measureItem.getEnd() - 1] = "I";
                }
            }

            itemList.add(measureItem);
        }
    }

    public IntermediateResult processForPosTagging(String sentence) {
        String tag = null;
        int length = sentence.length();
        String[] labels = new String[length];
        String constraints = "";

        int indexBlank;
        for(indexBlank = 0; indexBlank < length; ++indexBlank) {
            labels[indexBlank] = this.symbol;
        }

        for(indexBlank = sentence.indexOf(this.replaceBlank); indexBlank != -1; indexBlank = sentence.indexOf(this.replaceBlank, indexBlank + 1)) {
            labels[indexBlank] = "S" + CONNECTOR + PunctuationTag;
        }

        ArrayList rawList = new ArrayList();
        ArrayList itemList = new ArrayList();
        this.emailRecognizer.recognize(sentence, rawList);
        this.urlRecognizer.recognize(sentence, rawList);
        this.dateRecognizer.recognize(sentence, rawList);
        this.percentRecognizer.recognize(sentence, rawList);
        this.measureRecognizer.recognize(sentence, rawList);
        this.timeRecognizer.recognize(sentence, rawList);
        this.periodRecognizer.recognize(sentence, rawList);
        this.currencyRecognizer.recognize(sentence, rawList);
        this.cellphoneRecognizer.recognize(sentence, rawList);
        this.phoneRecognizer.recognize(sentence, rawList);
        this.domainRecognizer.recognize(sentence, rawList);
        this.idiomRecognizer.recognize(sentence, rawList);
        this.foodRecognizer.recognize(sentence, rawList);
        this.sceneRecognizer.recognize(sentence, rawList);
        this.titleRecognizer.recognize(sentence, rawList);
        this.deviceRecognizer.recognize(sentence, rawList);
        this.organizationRecognizer.recognize(sentence, rawList);
        this.locationRecognizer.recognize(sentence, rawList);
        this.personRecognizer.recognize(sentence, rawList);
        this.nerRecognizer.recognize(sentence, rawList);
        this.foreignRecognizer.recognize(sentence, rawList);
        this.digitRecognizer.recognize(sentence, rawList);
        this.punctuationRecognizer.recognize(sentence, rawList);
        Iterator it = rawList.iterator();
        Item item = null;
        MeasureItem measureItem = null;

        while(true) {
            int intermediateResult;
            do {
                label158:
                do {
                    while(it.hasNext()) {
                        item = (Item)it.next();
                        if(item instanceof MeasureItem) {
                            tag = CONNECTOR + MeasureTag;
                            measureItem = (MeasureItem)item;
                            continue label158;
                        }

                        if(labels[item.getStart()].equals(this.symbol) && labels[item.getEnd() - 1].equals(this.symbol)) {
                            if(item.getType() == Item.EMAIL) {
                                tag = CONNECTOR + EmailTag;
                            }

                            if(item.getType() == Item.URL) {
                                tag = CONNECTOR + UrlTag;
                            }

                            if(item.getType() == Item.DATE) {
                                tag = CONNECTOR + DateTag;
                            }

                            if(item.getType() == Item.PERCENT) {
                                tag = CONNECTOR + PercentTag;
                            }

                            if(item.getType() == Item.TIME) {
                                tag = CONNECTOR + TimeTag;
                            }

                            if(item.getType() == Item.CURRENCY) {
                                tag = CONNECTOR + CurrencyTag;
                            }

                            if(item.getType() == Item.PERIOD) {
                                tag = CONNECTOR + PeriodTag;
                            }

                            if(item.getType() == Item.CELLPHONE) {
                                tag = CONNECTOR + CellphoneTag;
                            }

                            if(item.getType() == Item.LANDLINE) {
                                tag = CONNECTOR + LandlineTag;
                            }

                            if(item.getType() == Item.DOMAIN) {
                                tag = CONNECTOR + DomainTag;
                            }

                            if(item.getType() == Item.IDIOM) {
                                tag = CONNECTOR + IdiomTag;
                            }

                            if(item.getType() == Item.FOOD) {
                                tag = CONNECTOR + FoodTag;
                            }

                            if(item.getType() == Item.SCENE) {
                                tag = CONNECTOR + SceneTag;
                            }

                            if(item.getType() == Item.TITLE) {
                                tag = CONNECTOR + TitleTag;
                            }

                            if(item.getType() == Item.DEVICE) {
                                tag = CONNECTOR + DeviceTag;
                            }

                            if(item.getType() == Item.ORGANIZATION) {
                                tag = CONNECTOR + OrganizationTag;
                            }

                            if(item.getType() == Item.LOCATION) {
                                tag = CONNECTOR + LocationTag;
                            }

                            if(item.getType() == Item.PERSON) {
                                tag = CONNECTOR + PersonTag;
                            }

                            if(item.getType() == Item.FOREIGN) {
                                tag = CONNECTOR + ForeignTag;
                            }

                            if(item.getType() == Item.DIGIT) {
                                tag = CONNECTOR + DigitTag;
                            }

                            if(item.getType() == Item.PUNCTUATION) {
                                tag = CONNECTOR + PunctuationTag;
                            }

                            if(item.getStart() == item.getEnd() - 1) {
                                if(this.isBIOES) {
                                    labels[item.getStart()] = "S" + tag;
                                } else {
                                    labels[item.getStart()] = "B" + tag;
                                }
                            } else {
                                labels[item.getStart()] = "B" + tag;

                                for(intermediateResult = item.getStart() + 1; intermediateResult < item.getEnd() - 1; ++intermediateResult) {
                                    labels[intermediateResult] = "I" + tag;
                                }

                                if(this.isBIOES) {
                                    labels[item.getEnd() - 1] = "E" + tag;
                                } else {
                                    labels[item.getEnd() - 1] = "I" + tag;
                                }
                            }

                            itemList.add(item);
                        }
                    }

                    for(intermediateResult = 0; intermediateResult < length - 1; ++intermediateResult) {
                        constraints = constraints + labels[intermediateResult] + " ";
                    }

                    constraints = constraints + labels[length - 1];
                    IntermediateResult var13 = new IntermediateResult(constraints, itemList);
                    rawList = null;
                    return var13;
                } while(!labels[measureItem.getStart()].equals(this.symbol));
            } while(!labels[measureItem.getEnd() - 1].equals(this.symbol));

            if(measureItem.getStart() == measureItem.getEnd() - 1) {
                if(this.isBIOES) {
                    labels[measureItem.getStart()] = "S" + tag;
                } else {
                    labels[measureItem.getStart()] = "B" + tag;
                }
            } else {
                labels[measureItem.getStart()] = "B" + tag;

                for(intermediateResult = measureItem.getStart() + 1; intermediateResult < measureItem.getEnd() - 1; ++intermediateResult) {
                    labels[intermediateResult] = "I" + tag;
                }

                if(this.isBIOES) {
                    labels[measureItem.getEnd() - 1] = "E" + tag;
                } else {
                    labels[measureItem.getEnd() - 1] = "I" + tag;
                }
            }

            itemList.add(measureItem);
        }
    }

    public IntermediateResult processForNerRecognition(String sentence) {
        String tag = null;
        int length = sentence.length();
        String[] labels = new String[length];
        String constraints = "";

        for(int rawList = 0; rawList < length; ++rawList) {
            labels[rawList] = this.symbol;
        }

        ArrayList var12 = new ArrayList();
        ArrayList itemList = new ArrayList();
        this.emailRecognizer.recognize(sentence, var12);
        this.urlRecognizer.recognize(sentence, var12);
        this.dateRecognizer.recognize(sentence, var12);
        this.percentRecognizer.recognize(sentence, var12);
        this.measureRecognizer.recognize(sentence, var12);
        this.timeRecognizer.recognize(sentence, var12);
        this.periodRecognizer.recognize(sentence, var12);
        this.currencyRecognizer.recognize(sentence, var12);
        this.cellphoneRecognizer.recognize(sentence, var12);
        this.phoneRecognizer.recognize(sentence, var12);
        this.domainRecognizer.recognize(sentence, var12);
        this.idiomRecognizer.recognize(sentence, var12);
        this.foodRecognizer.recognize(sentence, var12);
        this.sceneRecognizer.recognize(sentence, var12);
        this.titleRecognizer.recognize(sentence, var12);
        this.deviceRecognizer.recognize(sentence, var12);
        this.organizationRecognizer.recognize(sentence, var12);
        this.locationRecognizer.recognize(sentence, var12);
        this.personRecognizer.recognize(sentence, var12);
        this.foreignRecognizer.recognize(sentence, var12);
        this.digitRecognizer.recognize(sentence, var12);
        this.punctuationRecognizer.recognize(sentence, var12);
        Iterator it = var12.iterator();
        Item item = null;
        boolean type = true;

        while(true) {
            while(true) {
                int intermediateResult;
                int var13;
                do {
                    do {
                        do {
                            if(!it.hasNext()) {
                                for(intermediateResult = 0; intermediateResult < length - 1; ++intermediateResult) {
                                    constraints = constraints + labels[intermediateResult] + " ";
                                }

                                constraints = constraints + labels[length - 1];
                                IntermediateResult var14 = new IntermediateResult(constraints, itemList);
                                var12 = null;
                                return var14;
                            }

                            item = (Item)it.next();
                        } while(!labels[item.getStart()].equals(this.symbol));
                    } while(!labels[item.getEnd() - 1].equals(this.symbol));

                    itemList.add(item);
                    var13 = item.getType();
                } while(var13 != Item.TITLE && var13 != Item.ORGANIZATION && var13 != Item.LOCATION && var13 != Item.PERSON);

                if(item.getType() == Item.TITLE) {
                    tag = CONNECTOR + "PER";
                }

                if(item.getType() == Item.ORGANIZATION) {
                    tag = CONNECTOR + "ORG";
                }

                if(item.getType() == Item.LOCATION) {
                    tag = CONNECTOR + "LOC";
                }

                if(item.getType() == Item.PERSON) {
                    tag = CONNECTOR + "PER";
                }

                if(item.getStart() == item.getEnd() - 1) {
                    if(this.isBIOES) {
                        labels[item.getStart()] = "S" + tag;
                    } else {
                        labels[item.getStart()] = "B" + tag;
                    }
                } else {
                    labels[item.getStart()] = "B" + tag;

                    for(intermediateResult = item.getStart() + 1; intermediateResult < item.getEnd() - 1; ++intermediateResult) {
                        labels[intermediateResult] = "I" + tag;
                    }

                    if(this.isBIOES) {
                        labels[item.getEnd() - 1] = "E" + tag;
                    } else {
                        labels[item.getEnd() - 1] = "I" + tag;
                    }
                }
            }
        }
    }

    public String getConfFile() {
        return this.confFile;
    }

    public void setConfFile(String confFile) {
        this.confFile = confFile;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public boolean isBIOES() {
        return this.isBIOES;
    }

    public void setBIOES(boolean isBIOES) {
        this.isBIOES = isBIOES;
    }

    public boolean isLetter(char ch) {
        return ch == 48 || ch == 49 || ch == 50 || ch == 51 || ch == 52 || ch == 53 || ch == 54 || ch == 55 || ch == 56 || ch == 57 || ch == 97 || ch == 98 || ch == 99 || ch == 100 || ch == 101 || ch == 102 || ch == 103 || ch == 104 || ch == 105 || ch == 106 || ch == 107 || ch == 108 || ch == 109 || ch == 110 || ch == 111 || ch == 112 || ch == 113 || ch == 114 || ch == 115 || ch == 116 || ch == 117 || ch == 118 || ch == 119 || ch == 120 || ch == 121 || ch == 122 || ch == 65 || ch == 66 || ch == 67 || ch == 68 || ch == 69 || ch == 70 || ch == 71 || ch == 72 || ch == 73 || ch == 74 || ch == 75 || ch == 76 || ch == 77 || ch == 78 || ch == 79 || ch == 80 || ch == 81 || ch == 82 || ch == 83 || ch == 84 || ch == 85 || ch == 86 || ch == 87 || ch == 88 || ch == 89 || ch == 90;
    }
}