/*
 * Decompiled with CFR 0.152.
 */
package Client;

import java.io.Serializable;

public class CardData
implements Serializable {
    private static final long serialVersionUID = 2550550428979893978L;
    public final int chrId;
    public final short job;
    public final int level;

    public CardData(int cid, int level, short job) {
        this.chrId = cid;
        this.level = level;
        this.job = job;
    }

    public String toString() {
        return "角色ID: " + this.chrId + " 職業ID: " + this.job + " 等級: " + this.level;
    }
}

