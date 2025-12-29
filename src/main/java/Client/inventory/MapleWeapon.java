/*
 * Decompiled with CFR 0.152.
 */
package Client.inventory;

import Config.constants.JobConstants;

public enum MapleWeapon {
    沒有武器(1.43f, Attribute.PhysicalMelee, 0),
    閃亮克魯(1.2f, Attribute.Magic, 212),
    調節器(1.3f, Attribute.PhysicalMelee, 213),
    龍息射手(1.3f, Attribute.PhysicalMelee, 214),
    靈魂射手(1.7f, Attribute.PhysicalRanged, 222),
    魔劍(1.3f, Attribute.PhysicalMelee, 232),
    能量劍(1.3125f, Attribute.PhysicalMelee, 242),
    記憶長杖(1.34f, Attribute.Magic, 252),
    ESP限製器(1.2f, Attribute.Magic, 262),
    鎖鍊(1.3f, Attribute.PhysicalMelee, 272),
    魔法護腕(1.3f, Attribute.Magic, 282),
    仙扇(1.3f, Attribute.PhysicalMelee, 292),
    單手劍(1.2f, Attribute.PhysicalMelee, 302),
    單手斧(1.2f, Attribute.PhysicalMelee, 312),
    單手棍(1.2f, Attribute.PhysicalMelee, 322),
    短劍(1.3f, Attribute.PhysicalMelee, 332),
    雙刀(1.3f, Attribute.PhysicalMelee, 342),
    手杖(1.3f, Attribute.PhysicalMelee, 362),
    短杖(1.0f, Attribute.Magic, 372),
    長杖(1.0f, Attribute.Magic, 382),
    雙手劍(1.34f, Attribute.PhysicalMelee, 402),
    武拳(1.7f, Attribute.PhysicalMelee, 403),
    環刃(1.3f, Attribute.PhysicalMelee, 404),
    雙手斧(1.34f, Attribute.PhysicalMelee, 412),
    雙手棍(1.34f, Attribute.PhysicalMelee, 422),
    槍(1.49f, Attribute.PhysicalMelee, 432),
    矛(1.49f, Attribute.PhysicalMelee, 442),
    弓(1.3f, Attribute.PhysicalRanged, 452),
    弩(1.35f, Attribute.PhysicalRanged, 462),
    拳套(1.75f, Attribute.PhysicalRanged, 472),
    指虎(1.7f, Attribute.PhysicalMelee, 482),
    火槍(1.5f, Attribute.PhysicalRanged, 492),
    雙弩槍(1.3f, Attribute.PhysicalRanged, 522),
    加農砲(1.5f, Attribute.PhysicalRanged, 532),
    太刀(1.25f, Attribute.PhysicalMelee, 542),
    扇子(1.35f, Attribute.Magic, 552),
    琉(1.49f, Attribute.PhysicalMelee, 562),
    璃(1.34f, Attribute.PhysicalMelee, 572),
    重拳槍(1.7f, Attribute.PhysicalMelee, 582),
    古代之弓(1.3f, Attribute.PhysicalRanged, 592);

    private final float damageMultiplier;
    private final Attribute attribute;
    private final int weaponType;

    private MapleWeapon(float maxDamageMultiplier, Attribute attribute, int weaponType) {
        this.damageMultiplier = maxDamageMultiplier;
        this.attribute = attribute;
        this.weaponType = weaponType;
    }

    public float getMaxDamageMultiplier(int job) {
        if (this == 沒有武器 && !JobConstants.is冒險家海盜(job)) {
            return 0.0f;
        }
        if ((JobConstants.is冒險家法師(job) || JobConstants.is烈焰巫師(job)) && (this == 短杖 || this == 長杖)) {
            return 1.2f;
        }
        if (JobConstants.is英雄(job) && (this == 雙手劍 || this == 雙手斧)) {
            return 1.44f;
        }
        if (JobConstants.is英雄(job) && (this == 單手劍 || this == 單手斧)) {
            return 1.3f;
        }
        return this.damageMultiplier;
    }

    public Attribute getAttribute() {
        return this.attribute;
    }

    public int getBaseMastery() {
        switch (this.attribute.ordinal()) {
            case 1: {
                return 15;
            }
            default: {
                return 20;
            }
            case 2: 
        }
        return 25;
    }

    public int getWeaponType() {
        return this.weaponType;
    }

    public boolean isTwoHand() {
        return this.weaponType / 100 == 4 || this.weaponType / 100 == 5;
    }

    public boolean check(int itemId) {
        if (itemId / 1000000 != 1) {
            return false;
        }
        return this.weaponType == itemId / 1000 % 1000;
    }

    public static MapleWeapon getByItemID(int itemId) {
        if (itemId / 1000000 != 1) {
            return 沒有武器;
        }
        int cat = itemId / 1000 % 1000;
        for (MapleWeapon type : MapleWeapon.values()) {
            if (cat != type.getWeaponType()) continue;
            return type;
        }
        return 沒有武器;
    }

    public static enum Attribute {
        PhysicalMelee,
        PhysicalRanged,
        Magic;

    }
}

