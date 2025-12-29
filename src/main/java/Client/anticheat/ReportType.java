/*
 * Decompiled with CFR 0.152.
 */
package Client.anticheat;

public enum ReportType {
    Hacking(0, "hack"),
    Botting(1, "bot"),
    Scamming(2, "scam"),
    FakeGM(3, "fake"),
    Advertising(5, "ad");

    public final byte i;
    public final String theId;

    private ReportType(int i, String theId) {
        this.i = (byte)i;
        this.theId = theId;
    }

    public static ReportType getById(int z) {
        for (ReportType t : ReportType.values()) {
            if (t.i != z) continue;
            return t;
        }
        return null;
    }

    public static ReportType getByString(String z) {
        for (ReportType t : ReportType.values()) {
            if (!z.contains(t.theId)) continue;
            return t;
        }
        return null;
    }
}

