/*
 * Decompiled with CFR 0.152.
 */
package Client.inventory;

public enum EnchantScrollFlag {
    物攻(1),
    魔攻(2),
    力量(4),
    敏捷(8),
    智力(16),
    幸運(32),
    物防(64),
    魔防(128),
    Hp(256),
    Mp(512),
    命中(1024),
    迴避(2048),
    跳躍(4096),
    速度(8192),
    手技(16384);

    private final int value;

    public final boolean check(int mask) {
        return (mask & this.value) != 0;
    }

    public final int getValue() {
        return this.value;
    }

    private EnchantScrollFlag(int value) {
        this.value = value;
    }
}

