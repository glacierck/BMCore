// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lang;

public class Item
{
    public static int EMAIL;
    public static int URL;
    public static int DATE;
    public static int PERCENT;
    public static int MEASURE;
    public static int TIME;
    public static int PERIOD;
    public static int CURRENCY;
    public static int CELLPHONE;
    public static int LANDLINE;
    public static int DOMAIN;
    public static int IDIOM;
    public static int FOOD;
    public static int SCENE;
    public static int TITLE;
    public static int DEVICE;
    public static int SLANG;
    public static int ORGANIZATION;
    public static int LOCATION;
    public static int PERSON;
    public static int FOREIGN;
    public static int DIGIT;
    public static int PUNCTUATION;
    private int start;
    private int end;
    private String word;
    private int type;
    
    static {
        Item.EMAIL = 0;
        Item.URL = 1;
        Item.DATE = 2;
        Item.PERCENT = 3;
        Item.MEASURE = 4;
        Item.TIME = 5;
        Item.PERIOD = 6;
        Item.CURRENCY = 7;
        Item.CELLPHONE = 8;
        Item.LANDLINE = 9;
        Item.DOMAIN = 10;
        Item.IDIOM = 11;
        Item.FOOD = 12;
        Item.SCENE = 13;
        Item.TITLE = 14;
        Item.DEVICE = 15;
        Item.SLANG = 16;
        Item.ORGANIZATION = 17;
        Item.LOCATION = 18;
        Item.PERSON = 19;
        Item.FOREIGN = 20;
        Item.DIGIT = 21;
        Item.PUNCTUATION = 22;
    }
    
    public static String deType(final int type) {
        String typeString = null;
        switch (type) {
            case 0: {
                typeString = "EMAIL";
                break;
            }
            case 1: {
                typeString = "URL";
                break;
            }
            case 2: {
                typeString = "DATE";
                break;
            }
            case 3: {
                typeString = "PERCENT";
                break;
            }
            case 4: {
                typeString = "MEASURE";
                break;
            }
            case 5: {
                typeString = "TIME";
                break;
            }
            case 6: {
                typeString = "PERIOD";
                break;
            }
            case 7: {
                typeString = "CURRENCY";
                break;
            }
            case 8: {
                typeString = "CELLPHONE";
                break;
            }
            case 9: {
                typeString = "LANDLINE";
                break;
            }
            case 10: {
                typeString = "DOMAIN";
                break;
            }
            case 11: {
                typeString = "IDIOM";
                break;
            }
            case 12: {
                typeString = "FOOD";
                break;
            }
            case 13: {
                typeString = "SCENE";
                break;
            }
            case 14: {
                typeString = "TITLE";
                break;
            }
            case 15: {
                typeString = "DEVICE";
                break;
            }
            case 16: {
                typeString = "SLANG";
                break;
            }
            case 17: {
                typeString = "ORGANIZATION";
                break;
            }
            case 18: {
                typeString = "LOCATION";
                break;
            }
            case 19: {
                typeString = "PERSON";
                break;
            }
            case 20: {
                typeString = "FOREIGN";
                break;
            }
            case 21: {
                typeString = "DIGIT";
                break;
            }
            case 22: {
                typeString = "PUNCTUATION";
                break;
            }
            default: {
                typeString = "N/A";
                break;
            }
        }
        return typeString;
    }
    
    public Item(final int start, final int end, final String word, final int type) {
        this.start = -1;
        this.end = -1;
        this.word = null;
        this.type = -1;
        this.start = start;
        this.end = end;
        this.word = word;
        this.type = type;
    }
    
    public int getStart() {
        return this.start;
    }
    
    public void setStart(final int start) {
        this.start = start;
    }
    
    public int getEnd() {
        return this.end;
    }
    
    public void setEnd(final int end) {
        this.end = end;
    }
    
    public String getWord() {
        return this.word;
    }
    
    public void setWord(final String word) {
        this.word = word;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
}
