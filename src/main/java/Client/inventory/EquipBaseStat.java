/*
 * Decompiled with CFR 0.152.
 */
package Client.inventory;

public enum EquipBaseStat {
    力量(0),
    敏捷(1),
    智力(2),
    幸運(3),
    MaxHP(4),
    MaxMP(5),
    攻擊力(6),
    魔力(7),
    防禦力(8),
    魔法防禦力(9),
    靈敏度(10),
    移動速度(11),
    跳躍力(12);

    private final int value;

    private EquipBaseStat(int i) {
        this.value = i;
    }

    public int getFlag() {
        return 1 << this.value;
    }

    public int getValue() {
        return this.value;
    }

    public boolean check(int flag) {
        return (flag & this.getFlag()) != 0;
    }
}

