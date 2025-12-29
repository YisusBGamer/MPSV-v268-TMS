/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

public enum SummonMovementType {
    STOP(0),
    WALK(1),
    WALK_RANDOM(2),
    FLY(3),
    FLY_RANDOM(4),
    SMART(5),
    FIX_V_MOVE(6),
    WALK_SMART(7),
    WALK_CLONE(8),
    FLY_CLONE(9),
    WALK_HANG(10),
    JAGUAR(11),
    FLY_JAGUAR(12),
    固定一段距離(13),
    固定跟隨攻擊(14),
    UNKNOWN_16(16),
    UNKNOWN_17(17);

    private final int val;

    private SummonMovementType(int val) {
        this.val = val;
    }

    public int getValue() {
        return this.val;
    }
}

