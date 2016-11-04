// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.recognizer;

import top.vk.core.lang.Item;

import java.util.ArrayList;

public interface Recognizer
{
    void recognize(final String p0, final ArrayList<Item> p1);
    
    ArrayList<Item> recognize(final String p0);
}
