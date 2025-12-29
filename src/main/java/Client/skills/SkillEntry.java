/*
 * Decompiled with CFR 0.152.
 */
package Client.skills;

public class SkillEntry {
    public byte position;
    public long expiration;
    public int skillevel;
    public int masterlevel;
    public int teachId;
    public int teachTimes;
    public byte rank;

    public SkillEntry(int skillevel, int masterlevel, long expiration) {
        this.skillevel = skillevel;
        this.masterlevel = masterlevel;
        this.expiration = expiration;
        this.teachId = 0;
        this.teachTimes = 0;
        this.position = (byte)-1;
    }

    public SkillEntry(int skillevel, int masterlevel, long expiration, int teachId, int teachTimes) {
        this.skillevel = skillevel;
        this.masterlevel = masterlevel;
        this.expiration = expiration;
        this.teachId = teachId;
        this.teachTimes = teachTimes;
        this.position = (byte)-1;
    }

    public SkillEntry(int skillevel, int masterlevel, long expiration, int teachId, int teachTimes, byte position) {
        this.skillevel = skillevel;
        this.masterlevel = masterlevel;
        this.expiration = expiration;
        this.teachId = teachId;
        this.teachTimes = teachTimes;
        this.position = position;
    }

    public String toString() {
        return this.skillevel + ":" + this.masterlevel;
    }
}

