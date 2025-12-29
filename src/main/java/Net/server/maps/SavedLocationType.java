/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

public enum SavedLocationType {
    FREE_MARKET(0),
    MULUNG_TC(1),
    WORLDTOUR(2),
    FLORINA(3),
    FISHING(4),
    RICHIE(5),
    EVENT(6),
    AMORIA(7),
    CHRISTMAS(8),
    TURNEGG(9),
    BPReturn(10),
    CRYSTALGARDEN(11);

    private final int index;

    private SavedLocationType(int index) {
        this.index = index;
    }

    public static SavedLocationType fromString(String Str) {
        return SavedLocationType.valueOf(Str);
    }

    public int getValue() {
        return this.index;
    }
}

