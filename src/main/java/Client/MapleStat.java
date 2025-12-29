/*
 * Decompiled with CFR 0.152.
 */
package Client;

public enum MapleStat {
    SKIN(1L),
    FACE(2L),
    HAIR(4L),
    PET_LOCKER_SN(8L),
    LEVEL(16L),
    JOB(32L),
    STR(64L),
    DEX(128L),
    INT(256L),
    LUK(512L),
    HP(1024L),
    MAX_HP(2048L),
    MP(4096L),
    MAX_MP(8192L),
    AVAILABLE_AP(16384L),
    AVAILABLE_SP(32768L),
    EXP(65536L),
    POPULARITY(131072L),
    MONEY(262144L),
    FATIGUE(524288L),
    CHARISMA(0x100000L),
    INSIGHT(0x200000L),
    WILL(0x400000L),
    CRAFT(0x800000L),
    SENSE(0x1000000L),
    CHARM(0x2000000L),
    TRAIT_LIMIT(0x4000000L),
    BATTLE_EXP(0x10000000L),
    BATTLE_RANK(0x20000000L),
    BATTLE_POINTS(0x40000000L),
    ICE_GAGE(0x20000000L),
    VIRTUE(0x40000000L),
    RECOVERY_POTION_WITH_HP(0x4000400L),
    RECOVERY_POTION_WITH_MP(0x4001000L),
    RECOVERY_POTION_WITH_HP_MP(0x4001400L),
    TEMP_EXP(0x100000000L),
    GENDER(0x200000000L),
    TODAYS_TRAITS(0x4000000), //今日獲得
    性別(0x200000000L),
    PET(1572872),
    RECOVERY_POTION_WITH_HPMP(67113984);

    private final long i;

    private MapleStat(long i) {
        this.i = i;
    }

    public static MapleStat getByValue(long value) {
        for (MapleStat stat : MapleStat.values()) {
            if (stat.i != value) continue;
            return stat;
        }
        return null;
    }

    public long getValue() {
        return this.i;
    }

    public enum Temp {

        力量(0x1),
        敏捷(0x2),
        智力(0x4),
        幸運(0x8),
        物攻(0x10),
        魔攻(0x20),
        物防(0x40),
        魔防(0x80),
        命中(0x100),
        迴避(0x200),
        速度(0x400),
        跳躍(0x800);
        private final int i;

        Temp(int i) {
            this.i = i;
        }

        public int getValue() {
            return i;
        }
    }
}

