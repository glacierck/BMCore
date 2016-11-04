// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lang;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class EventWrapper
{
    private String eventKeywordFile;
    private String attributeKeywordFile;
    private HashMap<String, String> eventKeywords;
    private HashMap<String, String> attributeKeywords;
    private String delimiter;
    private String otherTag;
    private String eventDescription;
    
    public EventWrapper(final String eventKeywordFile, final String attributeKeywordFile) {
        this.eventKeywordFile = null;
        this.attributeKeywordFile = null;
        this.eventKeywords = null;
        this.attributeKeywords = null;
        this.delimiter = "/";
        this.otherTag = "O";
        this.eventDescription = "\u4e8b\u4ef6";
        this.eventKeywordFile = eventKeywordFile;
        this.attributeKeywordFile = attributeKeywordFile;
        this.eventKeywords = new HashMap<String, String>();
        this.attributeKeywords = new HashMap<String, String>();
        try {
            final FileInputStream fis = new FileInputStream(eventKeywordFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            String line = null;
            String[] tokens = null;
            int num = 0;
            while ((line = br.readLine()) != null) {
                ++num;
                line = line.trim();
                tokens = line.split("\\s+");
                if (tokens.length != 2) {
                    System.out.println("Check the file of event keywords at the line " + num + ".");
                }
                else {
                    this.eventKeywords.put(tokens[0], tokens[1]);
                }
            }
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (this.eventKeywords.containsKey("EVENT")) {
            Event.setEventDescription(this.eventKeywords.get("EVENT"));
        }
        else {
            Event.setEventDescription(this.eventDescription);
        }
        try {
            final FileInputStream fis = new FileInputStream(attributeKeywordFile);
            final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            final BufferedReader br = new BufferedReader(isr);
            String line = null;
            String[] tokens = null;
            int num = 0;
            while ((line = br.readLine()) != null) {
                ++num;
                line = line.trim();
                tokens = line.split("\\s+");
                if (tokens.length != 2) {
                    System.out.println("Check the file of attribute keywords at the line " + num + ".");
                }
                else {
                    this.attributeKeywords.put(tokens[0], tokens[1]);
                }
            }
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Event wrap(String semanticAnalysisResult, final String eventType) {
        semanticAnalysisResult = semanticAnalysisResult.trim();
        final String[] tokens = semanticAnalysisResult.split("\\s+");
        String[] pairs = null;
        String type = eventType;
        String prevWord = null;
        String prevLabel = null;
        int index = -1;
        final ArrayList<AttributeValuePairs> items = new ArrayList<AttributeValuePairs>();
        AttributeValuePairs item = null;
        for (int i = 0; i < tokens.length; ++i) {
            index = tokens[i].lastIndexOf(this.delimiter);
            pairs = new String[] { tokens[i].substring(0, index), tokens[i].substring(index + 1) };
            if (!pairs[1].equals(this.otherTag)) {
                if (prevLabel != null) {
                    if (prevLabel.equals(pairs[1])) {
                        prevWord = String.valueOf(prevWord) + pairs[0];
                    }
                    else {
                        if (this.attributeKeywords.containsKey(prevLabel)) {
                            item = new AttributeValuePairs(this.attributeKeywords.get(prevLabel), prevWord);
                        }
                        else {
                            item = new AttributeValuePairs(prevLabel, prevWord);
                        }
                        items.add(item);
                        prevLabel = pairs[1];
                        prevWord = pairs[0];
                    }
                }
                else {
                    prevLabel = pairs[1];
                    prevWord = pairs[0];
                }
                if (i == tokens.length - 1 && prevLabel != null) {
                    if (this.attributeKeywords.containsKey(prevLabel)) {
                        item = new AttributeValuePairs(this.attributeKeywords.get(prevLabel), prevWord);
                    }
                    else {
                        item = new AttributeValuePairs(prevLabel, prevWord);
                    }
                    items.add(item);
                }
            }
            else if (prevLabel != null) {
                if (this.attributeKeywords.containsKey(prevLabel)) {
                    item = new AttributeValuePairs(this.attributeKeywords.get(prevLabel), prevWord);
                }
                else {
                    item = new AttributeValuePairs(prevLabel, prevWord);
                }
                items.add(item);
                prevLabel = null;
            }
        }
        if (this.eventKeywords.containsKey(type)) {
            type = this.eventKeywords.get(type);
        }
        if (type == null) {
            type = this.eventKeywords.get("UNKNOWN");
        }
        return new Event(type, items);
    }
    
    public String getEventKeywordFile() {
        return this.eventKeywordFile;
    }
    
    public void setEventKeywordFile(final String eventKeywordFile) {
        this.eventKeywordFile = eventKeywordFile;
    }
    
    public String getAttributeKeywordFile() {
        return this.attributeKeywordFile;
    }
    
    public void setAttributeKeywordFile(final String attributeKeywordFile) {
        this.attributeKeywordFile = attributeKeywordFile;
    }
}
