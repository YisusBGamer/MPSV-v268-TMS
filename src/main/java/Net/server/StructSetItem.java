/*
 * Decompiled with CFR 0.152.
 */
package Net.server;

import Net.server.StructSetItemStat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StructSetItem {
    public final Map<Integer, StructSetItemStat> setItemStat = new LinkedHashMap<Integer, StructSetItemStat>();
    public final List<Integer> itemIDs = new ArrayList<Integer>();
    public int setItemID;
    public byte completeCount;
    public String setItemName;

    public Map<Integer, StructSetItemStat> getSetItemStats() {
        return new LinkedHashMap<Integer, StructSetItemStat>(this.setItemStat);
    }
}

