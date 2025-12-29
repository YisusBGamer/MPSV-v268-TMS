/*
 * Decompiled with CFR 0.152.
 */
package Net.server.events;

public enum MapleEventType {
    Coconut("椰子比賽", new int[]{109080000}),
    CokePlay("CokePlay", new int[]{109080010}),
    Fitness("向高地", new int[]{109040000, 109040001, 109040002, 109040003, 109040004}),
    OlaOla("上樓~上樓", new int[]{109030001, 109030002, 109030003}),
    OxQuiz("OX問答", new int[]{109020001}),
    Survival("", new int[]{809040000, 809040100}),
    Snowball("雪球賽", new int[]{109060000});

    public final String desc;
    public final int[] mapids;

    private MapleEventType(String desc, int[] mapids) {
        this.desc = desc;
        this.mapids = mapids;
    }

    public static MapleEventType getByString(String splitted) {
        for (MapleEventType t : MapleEventType.values()) {
            if (!t.name().equalsIgnoreCase(splitted)) continue;
            return t;
        }
        return null;
    }
}

