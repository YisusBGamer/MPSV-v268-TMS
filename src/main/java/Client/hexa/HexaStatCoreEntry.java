/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package Client.hexa;

import java.util.HashMap;
import java.util.Map;
import lombok.Generated;

public class HexaStatCoreEntry {
    private final int type;
    private final int maxLevel;
    private final String stat;
    private Map<Integer, Integer> levelAddStat = new HashMap<Integer, Integer>();

    public HexaStatCoreEntry(int type, int maxLevel, String stat, Map<Integer, Integer> levelAddStat) {
        this.type = type;
        this.maxLevel = maxLevel;
        this.stat = stat;
        this.levelAddStat = levelAddStat;
    }

    @Generated
    public int getType() {
        return this.type;
    }

    @Generated
    public int getMaxLevel() {
        return this.maxLevel;
    }

    @Generated
    public String getStat() {
        return this.stat;
    }

    @Generated
    public Map<Integer, Integer> getLevelAddStat() {
        return this.levelAddStat;
    }
}

