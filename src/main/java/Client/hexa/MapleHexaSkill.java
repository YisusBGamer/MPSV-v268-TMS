/*
 * Decompiled with CFR 0.152.
 */
package Client.hexa;

public class MapleHexaSkill {
    private final int id;
    private int skilllv;

    public MapleHexaSkill(int id, int skilllv) {
        this.id = id;
        this.skilllv = skilllv;
    }

    public int getId() {
        return this.id;
    }

    public int getSkilllv() {
        return this.skilllv;
    }

    public void setSkilllv(int skilllv) {
        this.skilllv = skilllv;
    }
}

