/*
 * Decompiled with CFR 0.152.
 */
package Client;

public class MapleUnionEntry {
    private int type = 1;
    private final int characterId;
    private final int level;
    private final int job;
    private int rotate;
    private int boardIndex = -1;
    private int local;
    private final String name;

    public MapleUnionEntry(int chrid, String name, int level, int job) {
        this.characterId = chrid;
        this.level = level;
        this.job = job;
        this.name = name;
    }

    public int getLocal() {
        return this.local;
    }

    public void setLocal(int lv) {
        this.local = lv;
    }

    public int getRotate() {
        return this.rotate;
    }

    public void setRotate(int lt) {
        this.rotate = lt;
    }

    public String getName() {
        return this.name;
    }

    public int getBoardIndex() {
        return this.boardIndex;
    }

    public void setBoardIndex(int lu) {
        this.boardIndex = lu;
    }

    public int getJob() {
        return this.job;
    }

    public int getLevel() {
        return this.level;
    }

    public int getCharacterId() {
        return this.characterId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

