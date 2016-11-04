// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.lang;

import java.util.ArrayList;
import java.util.Iterator;

public class Event
{
    private String type;
    private ArrayList<AttributeValuePairs> items;
    private static String eventDescription;
    
    public Event(final String type, final ArrayList<AttributeValuePairs> items) {
        this.type = type;
        this.items = items;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public ArrayList<AttributeValuePairs> getItems() {
        return this.items;
    }
    
    public void setItems(final ArrayList<AttributeValuePairs> items) {
        this.items = items;
    }
    
    @Override
    public String toString() {
        String result = String.valueOf(Event.eventDescription) + ": " + this.type + "\r\n";
        final Iterator<AttributeValuePairs> it = this.items.iterator();
        AttributeValuePairs pairs = null;
        while (it.hasNext()) {
            pairs = it.next();
            result = String.valueOf(result) + pairs.getAttribute() + ": " + pairs.getValue() + "\r\n";
        }
        return result;
    }
    
    public static String getEventDescription() {
        return Event.eventDescription;
    }
    
    public static void setEventDescription(final String eventDescription) {
        Event.eventDescription = eventDescription;
    }
}
