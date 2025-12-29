/*
 * Decompiled with CFR 0.152.
 */
package Client.inventory;

import Client.inventory.EnchantScrollFlag;
import Client.inventory.Equip;
import Config.constants.ItemConstants;
import Net.server.MapleItemInformationProvider;
import Server.channel.handler.EnchantHandler;
import java.util.Map;

public class StarForce {
    private int str;
    private int dex;
    private int _int;
    private int luk;
    private int hp;
    private int mp;
    private int pad;
    private int mad;
    private int pdd;
    private int mdd;
    private int acc;
    private int avoid;
    private int hands;
    private int speed;
    private int jump;
    private int bossDamage;
    private int ignorePDR;
    private int totalDamage;
    private int allStat;
    private byte level;

    public StarForce() {
        this.reset();
    }

    public StarForce(StarForce s) {
        this.str = s.str;
        this.dex = s.dex;
        this._int = s._int;
        this.luk = s.luk;
        this.hp = s.hp;
        this.mp = s.mp;
        this.pad = s.pad;
        this.mad = s.mad;
        this.pdd = s.pdd;
        this.mdd = s.mdd;
        this.acc = s.acc;
        this.avoid = s.avoid;
        this.hands = s.hands;
        this.speed = s.speed;
        this.jump = s.jump;
        this.bossDamage = s.bossDamage;
        this.ignorePDR = s.ignorePDR;
        this.totalDamage = s.totalDamage;
        this.allStat = s.allStat;
        this.level = s.level;
    }

    public void reset() {
        this.str = 0;
        this.dex = 0;
        this._int = 0;
        this.luk = 0;
        this.hp = 0;
        this.mp = 0;
        this.pad = 0;
        this.mad = 0;
        this.pdd = 0;
        this.mdd = 0;
        this.acc = 0;
        this.avoid = 0;
        this.hands = 0;
        this.speed = 0;
        this.jump = 0;
        this.bossDamage = 0;
        this.ignorePDR = 0;
        this.totalDamage = 0;
        this.allStat = 0;
        this.level = 0;
    }

    public void resetEquipStats(Equip equip) {
        int nLevel = this.level;
        this.reset();
        block16: for (int i = 0; i < nLevel; ++i) {
            Map<EnchantScrollFlag, Integer> enchantMap = EnchantHandler.getEnchantScrollList(equip);
            this.level = (byte)(this.level + 1);
            if (enchantMap.containsKey((Object)EnchantScrollFlag.力量)) {
                this.str += enchantMap.get((Object)EnchantScrollFlag.力量).intValue();
            }
            if (enchantMap.containsKey((Object)EnchantScrollFlag.敏捷)) {
                this.dex += enchantMap.get((Object)EnchantScrollFlag.敏捷).intValue();
            }
            if (enchantMap.containsKey((Object)EnchantScrollFlag.智力)) {
                this._int += enchantMap.get((Object)EnchantScrollFlag.智力).intValue();
            }
            if (enchantMap.containsKey((Object)EnchantScrollFlag.幸運)) {
                this.luk += enchantMap.get((Object)EnchantScrollFlag.幸運).intValue();
            }
            if (enchantMap.containsKey((Object)EnchantScrollFlag.Hp)) {
                this.hp += enchantMap.get((Object)EnchantScrollFlag.Hp).intValue();
            }
            if (enchantMap.containsKey((Object)EnchantScrollFlag.Mp)) {
                this.mp += enchantMap.get((Object)EnchantScrollFlag.Mp).intValue();
            }
            if (enchantMap.containsKey((Object)EnchantScrollFlag.物攻)) {
                this.pad += enchantMap.get((Object)EnchantScrollFlag.物攻).intValue();
            }
            if (enchantMap.containsKey((Object)EnchantScrollFlag.魔攻)) {
                this.mad += enchantMap.get((Object)EnchantScrollFlag.魔攻).intValue();
            }
            if (enchantMap.containsKey((Object)EnchantScrollFlag.物防)) {
                this.pdd += enchantMap.get((Object)EnchantScrollFlag.物防).intValue();
            }
            if (enchantMap.containsKey((Object)EnchantScrollFlag.魔防)) {
                this.mdd += enchantMap.get((Object)EnchantScrollFlag.魔防).intValue();
            }
            if (enchantMap.containsKey((Object)EnchantScrollFlag.命中)) {
                this.acc += enchantMap.get((Object)EnchantScrollFlag.命中).intValue();
            }
            if (enchantMap.containsKey((Object)EnchantScrollFlag.迴避)) {
                this.avoid += enchantMap.get((Object)EnchantScrollFlag.迴避).intValue();
            }
            if (enchantMap.containsKey((Object)EnchantScrollFlag.手技)) {
                this.hands += enchantMap.get((Object)EnchantScrollFlag.手技).intValue();
            }
            if (enchantMap.containsKey((Object)EnchantScrollFlag.速度)) {
                this.speed += enchantMap.get((Object)EnchantScrollFlag.速度).intValue();
            }
            if (enchantMap.containsKey((Object)EnchantScrollFlag.跳躍)) {
                this.jump += enchantMap.get((Object)EnchantScrollFlag.跳躍).intValue();
            }
            if (!equip.isMvpEquip()) continue;
            if (MapleItemInformationProvider.getInstance().isCash(equip.getItemId()) || ItemConstants.類型.圖騰(equip.getItemId())) {
                switch (this.level) {
                    case 6: {
                        this.pad += 5;
                        this.mad += 5;
                        this.hp += 500;
                        this.mp += 500;
                        break;
                    }
                    case 8: {
                        this.pad += 10;
                        this.mad += 10;
                        this.hp += 500;
                        this.mp += 500;
                        break;
                    }
                    case 10: {
                        this.pad += 10;
                        this.mad += 10;
                        this.hp += 1000;
                        this.mp += 1000;
                        break;
                    }
                    case 15: {
                        this.pad += 10;
                        this.mad += 10;
                        this.hp += 1500;
                        this.mp += 1500;
                        break;
                    }
                    case 20: {
                        this.pad += 10;
                        this.mad += 10;
                        this.hp += 2500;
                        this.mp += 2500;
                        break;
                    }
                    case 25: {
                        this.pad += 10;
                        this.mad += 10;
                        this.hp += 4000;
                        this.mp += 4000;
                    }
                }
                continue;
            }
            switch (this.level) {
                case 6: {
                    this.ignorePDR += 2;
                    this.totalDamage += 2;
                    this.allStat += 2;
                    continue block16;
                }
                case 8: {
                    this.ignorePDR += 2;
                    this.totalDamage += 2;
                    this.allStat += 2;
                    continue block16;
                }
                case 10: {
                    this.ignorePDR += 3;
                    this.totalDamage += 3;
                    this.allStat += 3;
                    continue block16;
                }
                case 15: {
                    this.ignorePDR += 3;
                    this.totalDamage += 3;
                    this.allStat += 3;
                    continue block16;
                }
                case 20: {
                    this.ignorePDR += 5;
                    this.totalDamage += 10;
                    this.allStat += 5;
                    continue block16;
                }
                case 25: {
                    this.ignorePDR += 10;
                    this.totalDamage += 10;
                    this.allStat += 5;
                }
            }
        }
    }

    public int getStr() {
        return this.str;
    }

    public int getDex() {
        return this.dex;
    }

    public int getInt() {
        return this._int;
    }

    public int getLuk() {
        return this.luk;
    }

    public int getHp() {
        return this.hp;
    }

    public int getMp() {
        return this.mp;
    }

    public int getPad() {
        return this.pad;
    }

    public int getMad() {
        return this.mad;
    }

    public int getPdd() {
        return this.pdd;
    }

    public int getMdd() {
        return this.mdd;
    }

    public int getAcc() {
        return this.acc;
    }

    public int getAvoid() {
        return this.avoid;
    }

    public int getHands() {
        return this.hands;
    }

    public int getSpeed() {
        return this.speed;
    }

    public int getJump() {
        return this.jump;
    }

    public int getBossDamage() {
        return this.bossDamage;
    }

    public int getIgnorePDR() {
        return this.ignorePDR;
    }

    public int getTotalDamage() {
        return this.totalDamage;
    }

    public int getAllStat() {
        return this.allStat;
    }

    public byte getLevel() {
        return this.level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }
}

