/*
 * Decompiled with CFR 0.152.
 */
package Client.inventory;

public enum EnhanceResultType {
    NO_DESTROY((short) 1),
    UPGRADE_TIER((short) 2),
    SCROLL_SUCCESS((short) 4),
    EQUIP_MARK((short) 128);

    private final short value;

    public final boolean check(int n) {
        return (n & this.value) != 0;
    }

    public final short getValue() {
        return this.value;
    }

    private EnhanceResultType(short value) {
        this.value = value;
    }
}

