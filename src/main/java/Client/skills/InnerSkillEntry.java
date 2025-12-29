/*
 * Decompiled with CFR 0.152.
 */
package Client.skills;

public class InnerSkillEntry {
    private final int skillId;
    private final int skillLevel;
    private final byte position;
    private final byte rank;
    private final boolean temp;

    public InnerSkillEntry(int skillId, int skillevel, byte position, byte rank, boolean temp) {
        this.skillId = skillId;
        this.skillLevel = skillevel;
        this.position = position;
        this.rank = rank;
        this.temp = temp;
    }

    public int getSkillId() {
        return this.skillId;
    }

    public int getSkillLevel() {
        return this.skillLevel;
    }

    public byte getPosition() {
        return this.position;
    }

    public byte getRank() {
        return this.rank;
    }

    public boolean isTemp() {
        return this.temp;
    }
}

