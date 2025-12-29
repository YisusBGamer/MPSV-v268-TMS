/*
 * Decompiled with CFR 0.152.
 */
package Client;

public class VCoreSkillEntry {
    private int level;
    private int exp;
    private int slot;
    private final int vcoreid;
    private final int skill1;
    private final int skill2;
    private final int skill3;
    private long dateExpire;
    private int index;

    public VCoreSkillEntry(int vcoreid, int level, int exp, int skill1, int skill2, int skill3, long dateExpire, int slot, int index) {
        this.vcoreid = vcoreid;
        this.level = level;
        this.exp = exp;
        this.skill1 = skill1;
        this.skill2 = skill2;
        this.skill3 = skill3;
        this.dateExpire = dateExpire;
        this.slot = slot;
        this.index = index;
    }

    public int getType() {
        return this.vcoreid / 10000000 - 1;
    }

    public void gainExp(int gain) {
        this.exp += gain;
    }

    public void levelUP() {
        ++this.level;
    }

    public int getSkill(int slot) {
        switch (slot) {
            case 1: {
                return this.skill1;
            }
            case 2: {
                return this.skill2;
            }
            case 3: {
                return this.skill3;
            }
        }
        return 0;
    }

    public int getVcoreid() {
        return this.vcoreid;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return this.exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getSkill1() {
        return this.skill1;
    }

    public int getSkill2() {
        return this.skill2;
    }

    public int getSkill3() {
        return this.skill3;
    }

    public int getSlot() {
        return this.slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public long getDateExpire() {
        return this.dateExpire;
    }

    public void setDateExpire(long dateExpire) {
        this.dateExpire = dateExpire;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

