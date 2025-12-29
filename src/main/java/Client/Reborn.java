/*
 * Decompiled with CFR 0.152.
 */
package Client;

import Client.MapleCharacter;
import Client.SecondaryStat;
import Config.configs.ServerConfig;
import Net.server.MapleItemInformationProvider;
import Net.server.buffs.MapleStatEffect;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Reborn {
    private static final Map<Integer, Map<SecondaryStat, Integer>> LEVEL_DATA = new LinkedHashMap<Integer, Map<SecondaryStat, Integer>>(40);
    public static final int REBORN_BUFF_ITEM = 2023519;

    private static void inc(Map<SecondaryStat, Integer> map, SecondaryStat stat, int val) {
        map.merge(stat, val, (a, b) -> a + b);
    }

    public static EnumMap<SecondaryStat, Integer> getStatups(int level) {
        EnumMap<SecondaryStat, Integer> map = new EnumMap<SecondaryStat, Integer>(SecondaryStat.class);
        if (level <= 0) {
            return map;
        }
        Reborn.inc(map, SecondaryStat.IndieSTR, 200 * level);
        Reborn.inc(map, SecondaryStat.IndieDEX, 200 * level);
        Reborn.inc(map, SecondaryStat.IndieINT, 200 * level);
        Reborn.inc(map, SecondaryStat.IndieLUK, 200 * level);
        Reborn.inc(map, SecondaryStat.IndieMHP, 1000 * level);
        Reborn.inc(map, SecondaryStat.IndieMMP, 1000 * level);
        switch (level) {
            default: {
                Reborn.inc(map, SecondaryStat.IndiePMdR, 20);
            }
            case 29: {
                Reborn.inc(map, SecondaryStat.IndieBooster, -1);
            }
            case 28: {
                Reborn.inc(map, SecondaryStat.IgnoreMobpdpR, 30);
            }
            case 27: {
                Reborn.inc(map, SecondaryStat.IndiePADR, 30);
                Reborn.inc(map, SecondaryStat.IndieMADR, 30);
            }
            case 26: {
                Reborn.inc(map, SecondaryStat.IndieBDR, 30);
            }
            case 24: 
            case 25: {
                Reborn.inc(map, SecondaryStat.IndieCr, 10);
            }
            case 23: {
                Reborn.inc(map, SecondaryStat.IndieDamR, 10);
            }
            case 22: {
                Reborn.inc(map, SecondaryStat.IndieCD, 10);
            }
            case 21: {
                Reborn.inc(map, SecondaryStat.IndieBooster, -1);
            }
            case 20: {
                Reborn.inc(map, SecondaryStat.IndieCD, 2);
            }
            case 19: {
                Reborn.inc(map, SecondaryStat.IndieCD, 2);
            }
            case 18: {
                Reborn.inc(map, SecondaryStat.IndieCD, 2);
            }
            case 17: {
                Reborn.inc(map, SecondaryStat.IndieCD, 2);
            }
            case 16: {
                Reborn.inc(map, SecondaryStat.IndieCD, 2);
            }
            case 15: {
                Reborn.inc(map, SecondaryStat.IndieBDR, 4);
            }
            case 14: {
                Reborn.inc(map, SecondaryStat.IndieBDR, 4);
            }
            case 13: {
                Reborn.inc(map, SecondaryStat.IndieBDR, 4);
            }
            case 12: {
                Reborn.inc(map, SecondaryStat.IndieBDR, 4);
            }
            case 11: {
                Reborn.inc(map, SecondaryStat.IndieBDR, 4);
            }
            case 10: {
                Reborn.inc(map, SecondaryStat.IndieIgnoreMobpdpR, 5);
            }
            case 9: {
                Reborn.inc(map, SecondaryStat.IndieIgnoreMobpdpR, 5);
            }
            case 8: {
                Reborn.inc(map, SecondaryStat.IndieIgnoreMobpdpR, 5);
            }
            case 7: {
                Reborn.inc(map, SecondaryStat.IndieIgnoreMobpdpR, 5);
            }
            case 6: {
                Reborn.inc(map, SecondaryStat.IndieIgnoreMobpdpR, 5);
            }
            case 5: {
                Reborn.inc(map, SecondaryStat.IndieIgnoreMobpdpR, 5);
                Reborn.inc(map, SecondaryStat.IndieCr, 5);
            }
            case 4: {
                Reborn.inc(map, SecondaryStat.IndieBDR, 5);
            }
            case 3: {
                Reborn.inc(map, SecondaryStat.IndieBooster, -1);
                Reborn.inc(map, SecondaryStat.IndiePAD, 50);
                Reborn.inc(map, SecondaryStat.IndieMAD, 50);
            }
            case 2: {
                Reborn.inc(map, SecondaryStat.IndiePAD, 50);
                Reborn.inc(map, SecondaryStat.IndieMAD, 50);
            }
            case 1: 
        }
        return map;
    }

    private static MapleStatEffect getEffect(int level) {
        return MapleItemInformationProvider.getInstance().getItemEffect(2023519);
    }

    public static void giveRebornBuff(MapleCharacter chr) {
        if (!ServerConfig.EnableRebornBuff) {
            return;
        }
        chr.dispelEffect(2023519);
        int level = chr.getReborns();
        if (level <= 0) {
            return;
        }
        MapleStatEffect effect = Reborn.getEffect(level);
        effect.applyTo(chr, true);
    }
}

