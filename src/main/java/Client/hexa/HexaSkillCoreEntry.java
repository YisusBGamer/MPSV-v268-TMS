/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package Client.hexa;

import lombok.Generated;

public class HexaSkillCoreEntry {
    private final int id;
    private final int skillId;
    private final int type;
    private final int maxLevel;

    public HexaSkillCoreEntry(int id, int skillid, int type, int maxLevel) {
        this.id = id;
        this.skillId = skillid;
        this.type = type;
        this.maxLevel = maxLevel;
    }

    @Generated
    public int getId() {
        return this.id;
    }

    @Generated
    public int getSkillId() {
        return this.skillId;
    }

    @Generated
    public int getType() {
        return this.type;
    }

    @Generated
    public int getMaxLevel() {
        return this.maxLevel;
    }
}

