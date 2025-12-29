/*
 * Decompiled with CFR 0.152.
 */
package Client.inventory;

public enum EquipSpecialStat {
    可使用捲軸次數(0),
    捲軸強化次數(1),
    狀態(2),
    裝備技能(3),
    裝備等級(4),
    裝備經驗(5),
    耐久度(6),
    鎚子(7),
    套用等級減少(8),
    ENHANCT_BUFF(9),
    DURABILITY_SPECIAL(10),
    REQUIRED_LEVEL(11),
    YGGDRASIL_WISDOM(12),
    FINAL_STRIKE(13),
    BOSS傷(14),
    無視防禦(15),
    總傷害(16),
    全屬性(17),
    剪刀次數(18),
    輪迴星火(19),
    星力強化(20);

    private final int value;

    private EquipSpecialStat(int value) {
        this.value = value;
    }

    public int getFlag() {
        return 1 << this.value;
    }

    public int getValue() {
        return this.value;
    }

    public final boolean check(int flag) {
        return (flag & this.getFlag()) != 0;
    }
}

