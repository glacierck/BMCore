// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lang;

import java.util.ArrayList;
import java.util.Iterator;

public class IntermediateResult
{
    private String constriants;
    private ArrayList<Item> itemList;
    
    public IntermediateResult(final String constriants, final ArrayList<Item> itemList) {
        this.constriants = null;
        this.itemList = null;
        this.constriants = constriants;
        this.itemList = itemList;
    }
    
    public IntermediateResult() {
        this.constriants = null;
        this.itemList = null;
        this.constriants = "";
        this.itemList = new ArrayList<Item>();
    }
    
    public String getConstriants() {
        return this.constriants;
    }
    
    public void setConstriants(final String constriants) {
        this.constriants = constriants;
    }
    
    public ArrayList<Item> getItemList() {
        return this.itemList;
    }
    
    public void setItemList(final ArrayList<Item> itemList) {
        this.itemList = itemList;
    }
    
    @Override
    public String toString() {
        String result = "";
        Item item = null;
        SlangItem slangItem = null;
        MeasureItem measureItem = null;
        final Iterator<Item> it = this.itemList.iterator();
        while (it.hasNext()) {
            item = it.next();
            if (item instanceof SlangItem) {
                slangItem = (SlangItem)item;
                result = String.valueOf(result) + slangItem.getStart() + "-" + slangItem.getEnd() + ": \t" + Item.deType(slangItem.getType()) + "\t" + slangItem.getWord() + "\t" + slangItem.getExplanation() + "\r\n";
            }
            else if (item instanceof MeasureItem) {
                measureItem = (MeasureItem)item;
                result = String.valueOf(result) + measureItem.getStart() + "-" + measureItem.getEnd() + ": \t" + Item.deType(measureItem.getType()) + "\t" + measureItem.getWord() + "\t" + measureItem.getExplanation() + "\r\n";
            }
            else {
                if (item.getType() == Item.PUNCTUATION) {
                    continue;
                }
                result = String.valueOf(result) + item.getStart() + "-" + item.getEnd() + ": \t" + Item.deType(item.getType()) + "\t" + item.getWord() + "\r\n";
            }
        }
        return result;
    }
}
