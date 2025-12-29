/*
 * Decompiled with CFR 0.152.
 */
package Net.server.life;

import java.util.LinkedHashSet;
import java.util.Set;

public class MonsterGlobalDropEntry {
    public byte dropType;
    public int id;
    public int itemId;
    public int chance;
    public int Minimum;
    public int Maximum;
    public int continent;
    public int questid;
    public int minMobLevel;
    public int maxMobLevel;
    public int period;
    public boolean onlySelf;
    public Set<Integer> channels = new LinkedHashSet<Integer>();
    public String addFrom;

    public MonsterGlobalDropEntry(int id, int itemId, int chance, int continent, byte dropType, int Minimum, int Maximum, int questid, int minMobLevel, int maxMobLevel, boolean onlySelf, int period, String addFrom) {
        this.id = id;
        this.itemId = itemId;
        this.chance = chance;
        this.dropType = dropType;
        this.continent = continent;
        this.questid = questid;
        this.Minimum = Minimum;
        this.Maximum = Maximum;
        this.minMobLevel = minMobLevel;
        this.maxMobLevel = maxMobLevel;
        this.onlySelf = onlySelf;
        this.period = period;
        this.addFrom = addFrom;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MonsterGlobalDropEntry that = (MonsterGlobalDropEntry)o;
        return this.itemId == that.itemId;
    }

    public int hashCode() {
        return this.itemId;
    }
}

