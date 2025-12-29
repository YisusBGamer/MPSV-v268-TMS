/*
 * Decompiled with CFR 0.152.
 */
package Client;

import Client.MapleStat;

public enum MapleTraitType {
    charisma(500, MapleStat.CHARISMA),
    insight(500, MapleStat.INSIGHT),
    will(500, MapleStat.WILL),
    craft(500, MapleStat.CRAFT),
    sense(500, MapleStat.SENSE),
    charm(5000, MapleStat.CHARM);

    private final int limit;
    private final MapleStat stat;

    private MapleTraitType(int type, MapleStat theStat) {
        this.limit = type;
        this.stat = theStat;
    }

    public static MapleTraitType getByQuestName(String q) {
        String qq = q.substring(0, q.length() - 3);
        for (MapleTraitType t : MapleTraitType.values()) {
            if (!t.name().equals(qq)) continue;
            return t;
        }
        return null;
    }

    public int getLimit() {
        return this.limit;
    }

    public MapleStat getStat() {
        return this.stat;
    }
}

