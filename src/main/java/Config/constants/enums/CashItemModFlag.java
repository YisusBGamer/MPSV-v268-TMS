/*
 * Decompiled with CFR 0.152.
 */
package Config.constants.enums;

public enum CashItemModFlag {
    ITEM_ID(1L),
    COUNT(2L),
    PRICE(4L),
    BONUS(8L),
    PRIORITY(16L),
    PERIOD(32L),
    MAPLE_POINT(64L),
    MESO(128L),
    FOR_PREMIUM_USER(256L),
    COMMODITY_GENDER(512L),
    ON_SALE(1024L),
    CLASS(2048L),
    LIMIT(4096L),
    PB_CASH(8192L),
    PB_POINT(16384L),
    PB_GIFT(32768L),
    PACKAGE_SN(65536L),
    REQ_POP(131072L),
    REQ_LEV(262144L),
    TERM_START(524288L),
    TERM_END(0x100000L),
    REFUNDABLE(0x200000L),
    BOMB_SALE(0x400000L),
    CATEGORY_INFO(0x800000L),
    WORLD_LIMIT(0x1000000L),
    TOKEN(0x2000000L),
    LIMIT_MAX(0x4000000L),
    CHECK_QUEST_ID(0x8000000L),
    ORIGINAL_PRICE(0x10000000L),
    DISCOUNT(0x20000000L),
    DISCOUNT_RATE(0x40000000L),
    MILEAGE_INFO(Integer.MIN_VALUE),
    ZERO(0x100000000L),
    CHECK_QUEST_ID_2(0x200000000L),
    UNK34(0x400000000L),
    UNK35(0x800000000L),
    UNK36(0x1000000000L),
    COUPON_TYPE(0x2000000000L),
    UNK38(0x4000000000L),
    UNK39(0x8000000000L),
    UNK40(0x10000000000L),
    UNK41(0x20000000000L),
    UNK42(0x40000000000L),
    UNK43(0x80000000000L),
    UNK44(0x100000000000L);

    private final long value;

    private CashItemModFlag(long value) {
        this.value = value;
    }

    public long getValue() {
        return this.value;
    }

    public boolean contains(long flags) {
        return (flags & this.value) != 0L;
    }
}

