/*
 * Decompiled with CFR 0.152.
 */
package Client.stat;

import java.io.Serializable;

public class MapleHyperStats
implements Serializable {
    private int position;
    private int skillid;
    private int skilllevel;

    public MapleHyperStats(int pos, int skill, int level) {
        this.position = pos;
        this.skillid = skill;
        this.skilllevel = level;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getSkillid() {
        return this.skillid;
    }

    public void setSkillid(int skill) {
        this.skillid = skill;
    }

    public int getSkillLevel() {
        return this.skilllevel;
    }

    public void setSkillLevel(int level) {
        this.skilllevel = level;
    }
}

