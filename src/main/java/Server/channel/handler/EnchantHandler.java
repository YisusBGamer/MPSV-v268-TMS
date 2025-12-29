/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.inventory.EnchantScrollEntry
 *  Config.constants.enums.ScrollIconType
 *  Config.constants.enums.ScrollOptionType
 *  Config.constants.enums.SpellTraceScrollType
 */
package Server.channel.handler;

import Client.inventory.EnchantScrollEntry;
import Client.inventory.EnchantScrollFlag;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.MapleWeapon;
import Config.constants.ItemConstants;
import Config.constants.enums.ScrollIconType;
import Config.constants.enums.ScrollOptionType;
import Config.constants.enums.SpellTraceScrollType;
import Net.server.MapleItemInformationProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EnchantHandler {
    public static ArrayList<EnchantScrollEntry> getScrollList(Equip equip) {
        ArrayList<EnchantScrollEntry> ret = new ArrayList<EnchantScrollEntry>();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.getSlots(equip.getItemId()) + equip.getTotalHammer() <= 0) {
            return ret;
        }
        if (equip.getRestUpgradeCount() > 0) {
            if (ItemConstants.類型.武器(equip.getItemId()) || ItemConstants.類型.心臟(equip.getItemId()) || MapleWeapon.雙刀.check(equip.getItemId())) {
                HashMap<EnchantScrollFlag, Integer> stats = new HashMap<EnchantScrollFlag, Integer>();
                stats.put(EnchantScrollFlag.物攻, 5);
                ret.add(new EnchantScrollEntry("武器攻擊力卷軸", 100, ScrollIconType.卷軸_100Percentage, SpellTraceScrollType.一般卷軸, ScrollOptionType.力量卷軸100, 160, stats));
                stats = new HashMap();
                stats.put(EnchantScrollFlag.物攻, 5);
                ret.add(new EnchantScrollEntry("武器攻擊力卷軸", 70, ScrollIconType.卷軸_70Percentage, SpellTraceScrollType.一般卷軸, ScrollOptionType.力量卷軸70, 200, stats));
                stats = new HashMap();
                stats.put(EnchantScrollFlag.物攻, 5);
                ret.add(new EnchantScrollEntry("武器攻擊力卷軸", 30, ScrollIconType.卷軸_30Percentage, SpellTraceScrollType.一般卷軸, ScrollOptionType.力量卷軸30, 250, stats));
                stats = new HashMap();
                stats.put(EnchantScrollFlag.物攻, 5);
                ret.add(new EnchantScrollEntry("武器攻擊力卷軸", 15, ScrollIconType.卷軸_15Percentage, SpellTraceScrollType.一般卷軸, ScrollOptionType.力量卷軸15, 320, stats));
            } else {
                HashMap<EnchantScrollFlag, Integer> stats = new HashMap<EnchantScrollFlag, Integer>();
                stats.put(EnchantScrollFlag.物攻, 5);
                ret.add(new EnchantScrollEntry("攻擊力卷軸", 100, ScrollIconType.卷軸_100Percentage, SpellTraceScrollType.一般卷軸, ScrollOptionType.力量卷軸100, 160, stats));
                stats = new HashMap();
                stats.put(EnchantScrollFlag.物攻, 5);
                ret.add(new EnchantScrollEntry("攻擊力卷軸", 70, ScrollIconType.卷軸_70Percentage, SpellTraceScrollType.一般卷軸, ScrollOptionType.力量卷軸70, 200, stats));
                stats = new HashMap();
                stats.put(EnchantScrollFlag.物攻, 5);
                ret.add(new EnchantScrollEntry("攻擊力卷軸", 30, ScrollIconType.卷軸_30Percentage, SpellTraceScrollType.一般卷軸, ScrollOptionType.力量卷軸30, 250, stats));
                stats = new HashMap();
                stats.put(EnchantScrollFlag.物攻, 5);
                ret.add(new EnchantScrollEntry("攻擊力卷軸", 15, ScrollIconType.卷軸_15Percentage, SpellTraceScrollType.一般卷軸, ScrollOptionType.力量卷軸15, 320, stats));
            }
        }
        if (equip.getRestUpgradeCount() != ii.getSlots(equip.getItemId()) + equip.getTotalHammer()) {
            ret.add(new EnchantScrollEntry("回真卷軸", 100, ScrollIconType.回真卷軸, SpellTraceScrollType.回真卷軸, ScrollOptionType.回真卷軸, 12000, new HashMap()));
            ret.add(new EnchantScrollEntry("亞克回真卷軸", 100, ScrollIconType.亞克回真卷軸, SpellTraceScrollType.回真卷軸, ScrollOptionType.亞克回真卷軸, 24000, new HashMap()));
        }
        if (equip.getCurrentUpgradeCount() + equip.getRestUpgradeCount() != ii.getSlots(equip.getItemId()) + equip.getTotalHammer()) {
            ret.add(new EnchantScrollEntry("純白的卷軸", 100, ScrollIconType.純白的卷軸, SpellTraceScrollType.純白的卷軸, ScrollOptionType.純白的卷軸, 20000, new HashMap()));
        }
        if (equip.getTotalHammer() > 0) {
            ret.add(new EnchantScrollEntry("黃金鐵鎚", 100, ScrollIconType.黃金鐵鎚, SpellTraceScrollType.黃金鐵鎚, ScrollOptionType.黃金鐵鎚, 24000, new HashMap()));
        }
        return ret;
    }

    public static long getStarForceMeso(int equiplevel, int enhanced, boolean superior) {
        if (superior) {
            return EnchantHandler.getSuperiorStarForceMeso(equiplevel);
        }
        long[] sfMeso = equiplevel < 100 ? new long[]{41000L, 81000L, 121000L, 161000L, 201000L, 241000L, 281000L, 321000L} : (equiplevel < 110 ? new long[]{54200L, 107500L, 160700L, 214000L, 267200L, 320400L, 373700L, 426900L, 480200L, 533400L} : (equiplevel < 120 ? new long[]{70100L, 139200L, 208400L, 277500L, 346600L, 415700L, 484800L, 554000L, 623100L, 692200L, 5602100L, 7085400L, 8794500L, 10742400L, 12941800L} : (equiplevel < 130 ? new long[]{88900L, 176800L, 264600L, 352500L, 440400L, 528300L, 616200L, 704000L, 791900L, 879800L, 7122300L, 9008200L, 11181100L, 13657700L, 16454100L, 19586000L, 23069100L, 26918600L, 31149300L, 35776100L} : (equiplevel < 140 ? new long[]{110800L, 220500L, 330300L, 440000L, 549800L, 659600L, 769300L, 879100L, 988800L, 1098600L, 8895400L, 11250800L, 13964700L, 17057900L, 20550500L, 24462200L, 28812500L, 33620400L, 38904500L, 44683300L, 50974700L, 57796700L, 65166700L, 73102200L, 81620200L} : new long[]{136000L, 271000L, 406000L, 541000L, 676000L, 811000L, 946000L, 1081000L, 1216000L, 1351000L, 10940700L, 13837700L, 17175800L, 20980200L, 25275900L, 30087200L, 35437900L, 41351400L, 47850600L, 54985200L, 62696400L, 71087200L, 80152000L, 89912300L, 100389000L}))));
        return sfMeso.length + 1 < enhanced ? -1L : sfMeso[enhanced];
    }

    public static long getSuperiorStarForceMeso(int equiplevel) {
        long[] sfMeso = new long[]{55832200L, 55832200L, 55832200L, 55832200L, 55832200L, 55832200L};
        equiplevel = equiplevel >= 0 && equiplevel <= 109 ? 0 : (equiplevel >= 110 && equiplevel <= 119 ? 1 : (equiplevel >= 120 && equiplevel <= 129 ? 2 : (equiplevel >= 130 && equiplevel <= 139 ? 3 : (equiplevel >= 140 && equiplevel <= 149 ? 4 : 5))));
        return sfMeso[equiplevel];
    }

    public static Map<EnchantScrollFlag, Integer> getEnchantScrollList(Item item) {
        HashMap<EnchantScrollFlag, Integer> ret = new HashMap<EnchantScrollFlag, Integer>();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int statusFlag = 0;
        int reqJob = ii.getReqJob(item.getItemId());
        int reqLevel = ii.getReqLevel(item.getItemId());
        Equip nEquip = (Equip)item;
        int watk = nEquip.getPad() + nEquip.getStarForce().getPad();
        int matk = nEquip.getMad() + nEquip.getStarForce().getMad();
        int pdd = nEquip.getTotalPdd();
        if ((reqJob & 1) != 0) {
            statusFlag |= EnchantScrollFlag.力量.getValue();
            statusFlag |= EnchantScrollFlag.敏捷.getValue();
        }
        if ((reqJob & 2) != 0) {
            statusFlag |= EnchantScrollFlag.智力.getValue();
            statusFlag |= EnchantScrollFlag.幸運.getValue();
        }
        if ((reqJob & 4) != 0) {
            statusFlag |= EnchantScrollFlag.敏捷.getValue();
            statusFlag |= EnchantScrollFlag.力量.getValue();
        }
        if ((reqJob & 8) != 0) {
            statusFlag |= EnchantScrollFlag.幸運.getValue();
            statusFlag |= EnchantScrollFlag.敏捷.getValue();
        }
        if ((reqJob & 0x10) != 0) {
            statusFlag |= EnchantScrollFlag.力量.getValue();
            statusFlag |= EnchantScrollFlag.敏捷.getValue();
        }
        if (reqJob <= 0) {
            statusFlag |= EnchantScrollFlag.力量.getValue();
            statusFlag |= EnchantScrollFlag.敏捷.getValue();
            statusFlag |= EnchantScrollFlag.智力.getValue();
            statusFlag |= EnchantScrollFlag.幸運.getValue();
        }
        byte enhance = nEquip.getStarForceLevel();
        if (ii.isSuperiorEquip(item.getItemId())) {
            switch (enhance) {
                case 0: {
                    if (EnchantScrollFlag.力量.check(statusFlag)) {
                        ret.put(EnchantScrollFlag.力量, 19);
                    }
                    if (EnchantScrollFlag.敏捷.check(statusFlag)) {
                        ret.put(EnchantScrollFlag.敏捷, 19);
                    }
                    if (EnchantScrollFlag.智力.check(statusFlag)) {
                        ret.put(EnchantScrollFlag.智力, 19);
                    }
                    if (!EnchantScrollFlag.幸運.check(statusFlag)) break;
                    ret.put(EnchantScrollFlag.幸運, 19);
                    break;
                }
                case 1: {
                    if (EnchantScrollFlag.力量.check(statusFlag)) {
                        ret.put(EnchantScrollFlag.力量, 20);
                    }
                    if (EnchantScrollFlag.敏捷.check(statusFlag)) {
                        ret.put(EnchantScrollFlag.敏捷, 20);
                    }
                    if (EnchantScrollFlag.智力.check(statusFlag)) {
                        ret.put(EnchantScrollFlag.智力, 20);
                    }
                    if (!EnchantScrollFlag.幸運.check(statusFlag)) break;
                    ret.put(EnchantScrollFlag.幸運, 20);
                    break;
                }
                case 2: {
                    if (EnchantScrollFlag.力量.check(statusFlag)) {
                        ret.put(EnchantScrollFlag.力量, 22);
                    }
                    if (EnchantScrollFlag.敏捷.check(statusFlag)) {
                        ret.put(EnchantScrollFlag.敏捷, 22);
                    }
                    if (EnchantScrollFlag.智力.check(statusFlag)) {
                        ret.put(EnchantScrollFlag.智力, 22);
                    }
                    if (!EnchantScrollFlag.幸運.check(statusFlag)) break;
                    ret.put(EnchantScrollFlag.幸運, 22);
                    break;
                }
                case 3: {
                    if (EnchantScrollFlag.力量.check(statusFlag)) {
                        ret.put(EnchantScrollFlag.力量, 25);
                    }
                    if (EnchantScrollFlag.敏捷.check(statusFlag)) {
                        ret.put(EnchantScrollFlag.敏捷, 25);
                    }
                    if (EnchantScrollFlag.智力.check(statusFlag)) {
                        ret.put(EnchantScrollFlag.智力, 25);
                    }
                    if (!EnchantScrollFlag.幸運.check(statusFlag)) break;
                    ret.put(EnchantScrollFlag.幸運, 25);
                    break;
                }
                case 4: {
                    if (EnchantScrollFlag.力量.check(statusFlag)) {
                        ret.put(EnchantScrollFlag.力量, 29);
                    }
                    if (EnchantScrollFlag.敏捷.check(statusFlag)) {
                        ret.put(EnchantScrollFlag.敏捷, 29);
                    }
                    if (EnchantScrollFlag.智力.check(statusFlag)) {
                        ret.put(EnchantScrollFlag.智力, 29);
                    }
                    if (!EnchantScrollFlag.幸運.check(statusFlag)) break;
                    ret.put(EnchantScrollFlag.幸運, 29);
                    break;
                }
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: {
                    ret.put(EnchantScrollFlag.物攻, enhance + 4);
                    ret.put(EnchantScrollFlag.魔攻, enhance + 4);
                    break;
                }
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: {
                    ret.put(EnchantScrollFlag.物攻, 15 + 2 * (enhance - 10));
                    ret.put(EnchantScrollFlag.魔攻, 15 + 2 * (enhance - 10));
                }
            }
            return ret;
        }
        int max = 0;
        switch (enhance) {
            case 0: 
            case 1: 
            case 2: {
                max = 5;
                break;
            }
            case 3: 
            case 4: {
                max = 10;
                break;
            }
            case 5: 
            case 6: 
            case 7: {
                max = 15;
                break;
            }
            case 8: {
                max = 20;
                break;
            }
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: {
                max = 25;
                break;
            }
            case 14: {
                max = 30;
            }
        }
        if (ItemConstants.類型.武器(item.getItemId()) || MapleWeapon.雙刀.check(item.getItemId())) {
            int allStats;

            allStats = enhance >= 0 && enhance < 5 ? 2 : (enhance >= 5 && enhance < 15 ? 3 : (reqLevel >= 200 ? 15 : (reqLevel >= 160 ? 13 : 11)));
            if (EnchantScrollFlag.力量.check(statusFlag)) {
                ret.put(EnchantScrollFlag.力量, allStats);
            }
            if (EnchantScrollFlag.敏捷.check(statusFlag)) {
                ret.put(EnchantScrollFlag.敏捷, allStats);
            }
            if (EnchantScrollFlag.智力.check(statusFlag)) {
                ret.put(EnchantScrollFlag.智力, allStats);
            }
            if (EnchantScrollFlag.幸運.check(statusFlag)) {
                ret.put(EnchantScrollFlag.幸運, allStats);
            }
            ret.put(EnchantScrollFlag.Hp, max);
            ret.put(EnchantScrollFlag.Mp, max);
            if (enhance < 15) {
                ret.put(EnchantScrollFlag.物攻, (int)Math.floor((double)watk / 50.0) + 1);
                ret.put(EnchantScrollFlag.魔攻, (int)Math.floor((double)matk / 50.0) + 1);
            } else {
                int value = 0;
                switch (enhance) {
                    case 15: {
                        if (reqLevel >= 200) {
                            value = 13;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 9;
                            break;
                        }
                        value = 8;
                        break;
                    }
                    case 16: {
                        if (reqLevel >= 200) {
                            value = 13;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 9;
                            break;
                        }
                        value = 9;
                        break;
                    }
                    case 17: {
                        if (reqLevel >= 200) {
                            value = 14;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 10;
                            break;
                        }
                        value = 9;
                        break;
                    }
                    case 18: {
                        if (reqLevel >= 200) {
                            value = 14;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 11;
                            break;
                        }
                        value = 10;
                        break;
                    }
                    case 19: {
                        if (reqLevel >= 200) {
                            value = 15;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 12;
                            break;
                        }
                        value = 11;
                        break;
                    }
                    case 20: {
                        if (reqLevel >= 200) {
                            value = 16;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 13;
                            break;
                        }
                        value = 12;
                        break;
                    }
                    case 21: {
                        if (reqLevel >= 200) {
                            value = 17;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 14;
                            break;
                        }
                        value = 13;
                        break;
                    }
                    case 22: {
                        if (reqLevel >= 200) {
                            value = 34;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 32;
                            break;
                        }
                        value = 31;
                        break;
                    }
                    case 23: {
                        if (reqLevel >= 200) {
                            value = 35;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 33;
                            break;
                        }
                        value = 32;
                        break;
                    }
                    case 24: {
                        value = reqLevel >= 200 ? 36 : (reqLevel >= 160 ? 34 : 33);
                    }
                }
                ret.put(EnchantScrollFlag.物攻, value);
                ret.put(EnchantScrollFlag.魔攻, value);
            }
        } else {
            int allStats;

            allStats = enhance >= 0 && enhance < 5 ? 2 : (enhance >= 5 && enhance < 15 ? 3 : (reqLevel >= 200 ? 15 : (reqLevel >= 160 ? 13 : 11)));
            if (EnchantScrollFlag.力量.check(statusFlag)) {
                ret.put(EnchantScrollFlag.力量, allStats);
            }
            if (EnchantScrollFlag.敏捷.check(statusFlag)) {
                ret.put(EnchantScrollFlag.敏捷, allStats);
            }
            if (EnchantScrollFlag.智力.check(statusFlag)) {
                ret.put(EnchantScrollFlag.智力, allStats);
            }
            if (EnchantScrollFlag.幸運.check(statusFlag)) {
                ret.put(EnchantScrollFlag.幸運, allStats);
            }
            if (!(ItemConstants.類型.臉飾(item.getItemId()) || ItemConstants.類型.眼飾(item.getItemId()) || ItemConstants.類型.耳環(item.getItemId()))) {
                ret.put(EnchantScrollFlag.Hp, max);
            }
            if (enhance >= 15) {
                int value = 0;
                switch (enhance) {
                    case 15: {
                        if (reqLevel >= 200) {
                            value = 12;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 10;
                            break;
                        }
                        value = 9;
                        break;
                    }
                    case 16: {
                        if (reqLevel >= 200) {
                            value = 13;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 11;
                            break;
                        }
                        value = 10;
                        break;
                    }
                    case 17: {
                        if (reqLevel >= 200) {
                            value = 14;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 12;
                            break;
                        }
                        value = 11;
                        break;
                    }
                    case 18: {
                        if (reqLevel >= 200) {
                            value = 15;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 13;
                            break;
                        }
                        value = 12;
                        break;
                    }
                    case 19: {
                        if (reqLevel >= 200) {
                            value = 16;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 14;
                            break;
                        }
                        value = 13;
                        break;
                    }
                    case 20: {
                        if (reqLevel >= 200) {
                            value = 17;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 15;
                            break;
                        }
                        value = 14;
                        break;
                    }
                    case 21: {
                        if (reqLevel >= 200) {
                            value = 19;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 17;
                            break;
                        }
                        value = 16;
                        break;
                    }
                    case 22: {
                        if (reqLevel >= 200) {
                            value = 21;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 19;
                            break;
                        }
                        value = 18;
                        break;
                    }
                    case 23: {
                        if (reqLevel >= 200) {
                            value = 23;
                            break;
                        }
                        if (reqLevel >= 160) {
                            value = 21;
                            break;
                        }
                        value = 20;
                        break;
                    }
                    case 24: {
                        value = reqLevel >= 200 ? 25 : (reqLevel >= 160 ? 23 : 22);
                    }
                }
                ret.put(EnchantScrollFlag.物攻, value);
                ret.put(EnchantScrollFlag.魔攻, value);
            } else if (ItemConstants.類型.手套(item.getItemId())) {
                int value = 0;
                switch (enhance) {
                    case 4: 
                    case 6: 
                    case 8: 
                    case 10: 
                    case 12: {
                        value = 1;
                        break;
                    }
                    case 13: {
                        if (reqLevel < 200) break;
                        value = 1;
                        break;
                    }
                    case 14: {
                        value = reqLevel >= 200 ? 1 : 2;
                    }
                }
                if ((reqJob & 2) == 0) {
                    ret.put(EnchantScrollFlag.物攻, value);
                }
                if ((reqJob & 2) != 0 || reqJob <= 0) {
                    ret.put(EnchantScrollFlag.魔攻, value);
                }
            }
        }
        int stat = pdd;
        int addStat = 0;
        for (int i = 0; i < enhance + 1; ++i) {
            addStat = (int)Math.ceil((double)stat / 20.0);
            stat += addStat;
        }
        return ret;
    }
}

