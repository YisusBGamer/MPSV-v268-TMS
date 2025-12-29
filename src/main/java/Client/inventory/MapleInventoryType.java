/*
 * Decompiled with CFR 0.152.
 */
package Client.inventory;

public enum MapleInventoryType {
    UNDEFINED(0, "未定義"),
    EQUIP(1, "裝備"),
    USE(2, "消耗"),
    SETUP(3, "裝飾"),
    ETC(4, "其他"),
    CASH(5, "特殊"),
    DECORATION(6, "時裝"),
    CASH_PACKAGE(9, "特殊包裹"),
    ELAB(36, "ES"),
    EQUIPPED(-1, "穿戴");

    final String name;
    final byte type;

    private MapleInventoryType(int type, String name) {
        this.type = (byte)type;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static MapleInventoryType getByType(byte type) {
        for (MapleInventoryType l : MapleInventoryType.values()) {
            if (l.getType() != type) continue;
            return l;
        }
        throw new IllegalArgumentException("Invalid InventoryType:" + type);
    }

    public static MapleInventoryType getByWZName(String name) {
        switch (name) {
            case "Install": {
                return SETUP;
            }
            case "Consume": {
                return USE;
            }
            case "Etc": {
                return ETC;
            }
            case "Eqp": {
                return EQUIP;
            }
            case "Cash": {
                return CASH;
            }
            case "Pet": {
                return CASH;
            }
        }
        return UNDEFINED;
    }

    public byte getType() {
        return this.type;
    }

    public short getBitfieldEncoding() {
        return (short)(2 << this.type);
    }
}

