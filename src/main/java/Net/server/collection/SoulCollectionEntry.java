/*
 * Decompiled with CFR 0.152.
 */
package Net.server.collection;

import java.util.HashMap;
import java.util.Map;

public class SoulCollectionEntry {
    private int soulSkill;
    private int soulSkillH;
    private final Map<Integer, Integer> items = new HashMap<Integer, Integer>();

    public Map<Integer, Integer> getItems() {
        return this.items;
    }

    public int getSoulSkill() {
        return this.soulSkill;
    }

    public void setSoulSkill(int soulSkill) {
        this.soulSkill = soulSkill;
    }

    public int getSoulSkillH() {
        return this.soulSkillH;
    }

    public void setSoulSkillH(int soulSkillH) {
        this.soulSkillH = soulSkillH;
    }
}

