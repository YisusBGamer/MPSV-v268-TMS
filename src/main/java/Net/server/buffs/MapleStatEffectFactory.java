/*
 * Decompiled with CFR 0.152.
 */
package Net.server.buffs;

import Client.MapleCharacter;
import Client.MapleTraitType;
import Client.SecondaryStat;
import Client.inventory.MapleInventoryType;
import Client.skills.Skill;
import Client.skills.SkillInfo;
import Client.skills.SkillMesInfo;
import Client.skills.handler.AbstractSkillHandler;
import Client.status.MonsterStatus;
import Config.constants.GameConstants;
import Config.constants.ItemConstants;
import Config.constants.JobConstants;
import Config.constants.SkillConstants;
import Net.server.MapleOverrideData;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataTool;
import Plugin.provider.MapleDataType;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.CaltechEval;
import tools.Pair;
import tools.Triple;

public class MapleStatEffectFactory {
    private static Logger log = LoggerFactory.getLogger(MapleStatEffectFactory.class);

    public static MapleStatEffect loadSkillEffectFromData(Skill skill, MapleData source, int skillid, boolean isHit, boolean overtime, boolean isSummon, int level, String variables, boolean notRemoved, boolean notIncBuffDuration) {
        return MapleStatEffectFactory.loadFromData(skill, source, skillid, isHit, overtime, isSummon, level, variables, notRemoved, notIncBuffDuration);
    }

    public static MapleStatEffect loadItemEffectFromData(MapleData source, int itemid) {
        return MapleStatEffectFactory.loadFromData(null, source, -itemid, false, false, false, 1, null, false, false);
    }

    private static void addBuffStatPairToListIfNotZero(Map<SecondaryStat, Integer> list, SecondaryStat buffstat, Integer val) {
        if (val != 0) {
            list.put(buffstat, val);
        }
    }

    public static int parseEval(String data, int level) {
        String variables = "x";
        Object dddd = data.toLowerCase().replace(variables, String.valueOf(level));
        if (((String)dddd).charAt(0) == '-') {
            dddd = ((String)dddd).charAt(1) == 'u' || ((String)dddd).charAt(1) == 'd' ? "n(" + ((String)dddd).substring(1) + ")" : "n" + ((String)dddd).substring(1);
        } else if (((String)dddd).charAt(0) == '=') {
            dddd = ((String)dddd).substring(1);
        }
        return (int)new CaltechEval((String)dddd).evaluate();
    }

    private static int parseEval(String path, MapleData source, int def, String variables, int level) {
        return MapleStatEffectFactory.parseEval(path, source, def, variables, level, "");
    }

    private static int parseEval(String path, MapleData source, int def, String variables, int level, String d) {
        return (int)MapleStatEffectFactory.parseEvalDouble(path, source, def, variables, level, d);
    }

    private static double parseEvalDouble(String path, MapleData source, int def, String variables, int level) {
        return MapleStatEffectFactory.parseEvalDouble(path, source, def, variables, level, "");
    }

    private static double parseEvalDouble(String path, MapleData source, int def, String variables, int level, String d) {
        Object dddd;
        if (variables == null) {
            return MapleDataTool.getIntConvert(path, source, def);
        }
        if (d.isEmpty()) {
            MapleData dd = source.getChildByPath(path);
            if (dd == null) {
                return def;
            }
            if (dd.getType() != MapleDataType.STRING) {
                return MapleDataTool.getIntConvert(path, source, def);
            }
            dddd = MapleDataTool.getString(dd).toLowerCase().replace("\r\n", "");
        } else {
            dddd = d;
        }
        dddd = ((String)dddd).replace(variables, String.valueOf(level));
        if (((String)dddd).isEmpty()) {
            return 0.0;
        }
        if (((String)dddd).charAt(0) == '-') {
            dddd = ((String)dddd).charAt(1) == 'u' || ((String)dddd).charAt(1) == 'd' ? "n(" + ((String)dddd).substring(1) + ")" : "n" + ((String)dddd).substring(1);
        } else if (((String)dddd).charAt(0) == '=') {
            dddd = ((String)dddd).substring(1);
        } else if (((String)dddd).endsWith("y")) {
            dddd = ((String)dddd).substring(4).replace("y", String.valueOf(level));
        } else if (((String)dddd).contains("%")) {
            dddd = ((String)dddd).replace("%", "/100");
        }
        return new CaltechEval((String)dddd).evaluate();
    }

    private static MapleStatEffect loadFromData(Skill skillObj, MapleData source, int sourceid, boolean isHit, boolean overTime, boolean isSummon, int level, String variables, boolean notRemoved, boolean notIncBuffDuration) {
        MapleData ltc;
        MapleData lt3d;
        MapleData lt2d;
        MapleData ltd;
        int dd;
        MapleStatEffect ret = new MapleStatEffect();
        ret.setSourceid(sourceid);
        ret.setLevel((byte)level);
        ret.setHit(isHit);
        if (source == null) {
            return ret;
        }
        EnumMap<MapleStatInfo, Integer> info = new EnumMap<MapleStatInfo, Integer>(MapleStatInfo.class);
        EnumMap<MapleStatInfo, Double> infoDouble = new EnumMap<MapleStatInfo, Double>(MapleStatInfo.class);
        for (MapleStatInfo i : MapleStatInfo.values()) {
            try {
                double val = i.isSpecial() ? MapleStatEffectFactory.parseEvalDouble(i.name().substring(0, i.name().length() - 1), source, i.getDefault(), variables, level, MapleOverrideData.getOverrideValue(sourceid, i.name())) : MapleStatEffectFactory.parseEvalDouble(i.name(), source, i.getDefault(), variables, level, MapleOverrideData.getOverrideValue(sourceid, i.name()));
                if (val % 1.0 != 0.0) {
                    infoDouble.put(i, val);
                }
                info.put(i, (int)val);
            }
            catch (Exception e) {
                log.error("加載技能數據出錯，id:" + sourceid + ", msi: " + String.valueOf((Object)i), e);
            }
        }
        ret.setInfo(info);
        ret.setInfoD(infoDouble);
        ret.setHpR((double)MapleStatEffectFactory.parseEval("hpR", source, 0, variables, level, MapleOverrideData.getOverrideValue(sourceid, "hpR")) / 100.0);
        ret.setMpR((double)MapleStatEffectFactory.parseEval("mpR", source, 0, variables, level, MapleOverrideData.getOverrideValue(sourceid, "mpR")) / 100.0);
        ret.setIgnoreMob((short)MapleStatEffectFactory.parseEval("ignoreMobpdpR", source, 0, variables, level, MapleOverrideData.getOverrideValue(sourceid, "ignoreMobpdpR")));
        ret.setThaw((short)MapleStatEffectFactory.parseEval("thaw", source, 0, variables, level));
        ret.setInterval(MapleStatEffectFactory.parseEval("interval", source, 0, variables, level));
        ret.setExpinc(MapleStatEffectFactory.parseEval("expinc", source, 0, variables, level));
        ret.setExp(MapleStatEffectFactory.parseEval("exp", source, 0, variables, level, MapleOverrideData.getOverrideValue(sourceid, "exp")));
        ret.setMorphId(MapleStatEffectFactory.parseEval("morph", source, 0, variables, level, MapleOverrideData.getOverrideValue(sourceid, "morph")));
        ret.setCosmetic(MapleStatEffectFactory.parseEval("cosmetic", source, 0, variables, level));
        ret.setSlotCount((byte)MapleStatEffectFactory.parseEval("slotCount", source, 0, variables, level));
        ret.setSlotPerLine((byte)MapleStatEffectFactory.parseEval("slotPerLine", source, 0, variables, level));
        ret.setPreventslip((byte)MapleStatEffectFactory.parseEval("preventslip", source, 0, variables, level));
        ret.setUseLevel((short)MapleStatEffectFactory.parseEval("useLevel", source, 0, variables, level));
        ret.setImmortal((byte)MapleStatEffectFactory.parseEval("immortal", source, 0, variables, level));
        ret.setType((byte)MapleStatEffectFactory.parseEval("type", source, 0, variables, level));
        ret.setBs((byte)MapleStatEffectFactory.parseEval("bs", source, 0, variables, level));
        ret.setIndiePdd((short)MapleStatEffectFactory.parseEval("indiePdd", source, 0, variables, level, MapleOverrideData.getOverrideValue(sourceid, "indiePdd")));
        ret.setIndieMdd((short)MapleStatEffectFactory.parseEval("indieMdd", source, 0, variables, level, MapleOverrideData.getOverrideValue(sourceid, "indieMdd")));
        ret.setExpBuff(MapleStatEffectFactory.parseEval("expBuff", source, 0, variables, level, MapleOverrideData.getOverrideValue(sourceid, "expBuff")));
        ret.setCashup(MapleStatEffectFactory.parseEval("cashBuff", source, 0, variables, level, MapleOverrideData.getOverrideValue(sourceid, "cashBuff")));
        ret.setItemup(MapleStatEffectFactory.parseEval("itemupbyitem", source, 0, variables, level));
        ret.setMesoup(MapleStatEffectFactory.parseEval("mesoupbyitem", source, 0, variables, level));
        ret.setBerserk(MapleStatEffectFactory.parseEval("berserk", source, 0, variables, level, MapleOverrideData.getOverrideValue(sourceid, "berserk")));
        ret.setBerserk2(MapleStatEffectFactory.parseEval("berserk2", source, 0, variables, level, MapleOverrideData.getOverrideValue(sourceid, "berserk2")));
        ret.setBooster(MapleStatEffectFactory.parseEval("booster", source, 0, variables, level, MapleOverrideData.getOverrideValue(sourceid, "booster")));
        ret.setLifeId((short)MapleStatEffectFactory.parseEval("lifeId", source, 0, variables, level));
        ret.setInflation((short)MapleStatEffectFactory.parseEval("inflation", source, 0, variables, level));
        ret.setImhp((short)MapleStatEffectFactory.parseEval("imhp", source, 0, variables, level, MapleOverrideData.getOverrideValue(sourceid, "imhp")));
        ret.setImmp((short)MapleStatEffectFactory.parseEval("immp", source, 0, variables, level, MapleOverrideData.getOverrideValue(sourceid, "immp")));
        ret.setIllusion(MapleStatEffectFactory.parseEval("illusion", source, 0, variables, level));
        ret.setConsumeOnPickup(MapleStatEffectFactory.parseEval("consumeOnPickup", source, 0, variables, level));
        if (ret.getConsumeOnPickup() == 1 && MapleStatEffectFactory.parseEval("party", source, 0, variables, level) > 0) {
            ret.setConsumeOnPickup(2);
        }
        ret.setRecipe(MapleStatEffectFactory.parseEval("recipe", source, 0, variables, level));
        ret.setRecipeUseCount((byte)MapleStatEffectFactory.parseEval("recipeUseCount", source, 0, variables, level));
        ret.setRecipeValidDay((byte)MapleStatEffectFactory.parseEval("recipeValidDay", source, 0, variables, level));
        ret.setReqSkillLevel((byte)MapleStatEffectFactory.parseEval("reqSkillLevel", source, 0, variables, level));
        ret.setEffectedOnAlly((byte)MapleStatEffectFactory.parseEval("effectedOnAlly", source, 0, variables, level));
        ret.setEffectedOnEnemy((byte)MapleStatEffectFactory.parseEval("effectedOnEnemy", source, 0, variables, level));
        ret.setMoneyCon(MapleStatEffectFactory.parseEval("moneyCon", source, 0, variables, level));
        int x = ret.getX();
        ret.setMoveTo(MapleStatEffectFactory.parseEval("moveTo", source, x > 100000000 && x <= 999999999 ? x : -1, variables, level));
        int charColor = 0;
        String cColor = MapleDataTool.getString("charColor", source, null);
        if (cColor != null) {
            charColor |= Integer.parseInt("0x" + cColor.substring(0, 2));
            charColor |= Integer.parseInt("0x" + cColor.substring(2, 4) + "00");
            charColor |= Integer.parseInt("0x" + cColor.substring(4, 6) + "0000");
            charColor |= Integer.parseInt("0x" + cColor.substring(6, 8) + "000000");
        }
        ret.setCharColor(charColor);
        EnumMap<MapleTraitType, Integer> traits = new EnumMap<MapleTraitType, Integer>(MapleTraitType.class);
        for (MapleTraitType t : MapleTraitType.values()) {
            int expz = MapleStatEffectFactory.parseEval(t.name() + "EXP", source, 0, variables, level);
            if (expz == 0) continue;
            traits.put(t, expz);
        }
        ret.setTraits(traits);
        ArrayList<SecondaryStat> cure = new ArrayList<SecondaryStat>(5);
        if (MapleStatEffectFactory.parseEval("poison", source, 0, variables, level) > 0) {
            cure.add(SecondaryStat.Poison);
        }
        if (MapleStatEffectFactory.parseEval("seal", source, 0, variables, level) > 0) {
            cure.add(SecondaryStat.Seal);
        }
        if (MapleStatEffectFactory.parseEval("darkness", source, 0, variables, level) > 0) {
            cure.add(SecondaryStat.Darkness);
        }
        if (MapleStatEffectFactory.parseEval("weakness", source, 0, variables, level) > 0) {
            cure.add(SecondaryStat.Weakness);
        }
        if (MapleStatEffectFactory.parseEval("curse", source, 0, variables, level) > 0) {
            cure.add(SecondaryStat.Curse);
        }
        if (MapleStatEffectFactory.parseEval("painmark", source, 0, variables, level) > 0) {
            cure.add(SecondaryStat.PainMark);
        }
        ret.setCureDebuffs(cure);
        ArrayList<Integer> petsCanConsume = new ArrayList<Integer>();
        int i = 0;
        while ((dd = MapleStatEffectFactory.parseEval(String.valueOf(i), source, 0, variables, level)) > 0) {
            petsCanConsume.add(dd);
            ++i;
        }
        ret.setPetsCanConsume(petsCanConsume);
        MapleData mdd = source.getChildByPath("0");
        if (mdd != null && mdd.getChildren().size() > 0) {
            ret.setMobSkill((short)MapleStatEffectFactory.parseEval("mobSkill", mdd, 0, variables, level));
            ret.setMobSkillLevel((short)MapleStatEffectFactory.parseEval("level", mdd, 0, variables, level));
        } else {
            ret.setMobSkill((short)0);
            ret.setMobSkillLevel((short)0);
        }
        MapleData pd = source.getChildByPath("randomPickup");
        if (pd != null) {
            ArrayList<Integer> randomPickup = new ArrayList<Integer>();
            for (MapleData p : pd) {
                randomPickup.add(MapleDataTool.getInt(p));
            }
            ret.setRandomPickup(randomPickup);
        }
        if ((ltd = source.getChildByPath("lt")) != null) {
            ret.setLt((Point)ltd.getData());
            ret.setRb((Point)source.getChildByPath("rb").getData());
        }
        if ((lt2d = source.getChildByPath("lt2")) != null) {
            ret.setLt2((Point)lt2d.getData());
            ret.setRb2((Point)source.getChildByPath("rb2").getData());
        }
        if ((lt3d = source.getChildByPath("lt3")) != null) {
            ret.setLt3((Point)lt3d.getData());
            ret.setRb3((Point)source.getChildByPath("rb3").getData());
        }
        if ((ltc = source.getChildByPath("con")) != null) {
            ArrayList<Pair<Integer, Integer>> availableMap = new ArrayList<Pair<Integer, Integer>>();
            for (MapleData ltb : ltc) {
                availableMap.add(new Pair<Integer, Integer>(MapleDataTool.getInt("sMap", ltb, 0), MapleDataTool.getInt("eMap", ltb, 999999999)));
            }
            ret.setAvailableMap(availableMap);
        }
        int totalprob = 0;
        MapleData lta = source.getChildByPath("reward");
        if (lta != null) {
            ret.setRewardMeso(MapleStatEffectFactory.parseEval("meso", lta, 0, variables, level));
            MapleData ltz = lta.getChildByPath("case");
            if (ltz != null) {
                ArrayList<Triple<Integer, Integer, Integer>> rewardItem = new ArrayList<Triple<Integer, Integer, Integer>>();
                for (MapleData lty : ltz) {
                    rewardItem.add(new Triple<Integer, Integer, Integer>(MapleDataTool.getInt("id", lty, 0), MapleDataTool.getInt("count", lty, 0), MapleDataTool.getInt("prop", lty, 0)));
                    totalprob += MapleDataTool.getInt("prob", lty, 0);
                }
                ret.setRewardItem(rewardItem);
            }
        } else {
            ret.setRewardMeso(0);
        }
        ret.setTotalprob(totalprob);
        if (ret.isSkill()) {
            int priceUnit = ret.getInfo().get((Object)MapleStatInfo.priceUnit);
            if (priceUnit > 0) {
                int price = ret.getInfo().get((Object)MapleStatInfo.price);
                int extendPrice = ret.getInfo().get((Object)MapleStatInfo.extendPrice);
                ret.getInfo().put(MapleStatInfo.price, price * priceUnit);
                ret.getInfo().put(MapleStatInfo.extendPrice, extendPrice * priceUnit);
            }
            switch (sourceid) {
                case 1100002: 
                case 1120013: 
                case 1200002: 
                case 1300002: 
                case 2111007: 
                case 2211007: 
                case 2311007: 
                case 3100001: 
                case 3120008: 
                case 3200001: 
                case 21100010: 
                case 21120012: 
                case 22000015: 
                case 22170072: 
                case 23100006: 
                case 23120012: 
                case 32111016: 
                case 33001007: 
                case 33001008: 
                case 33001009: 
                case 33001010: 
                case 33001011: 
                case 33001012: 
                case 33001013: 
                case 33001014: 
                case 33001015: 
                case 33100009: 
                case 33120011: {
                    ret.getInfo().put(MapleStatInfo.mobCount, 6);
                    break;
                }
                case 31220007: {
                    ret.getInfo().put(MapleStatInfo.attackCount, 2);
                    break;
                }
                case 27101100: {
                    ret.getInfo().put(MapleStatInfo.attackCount, 4);
                    break;
                }
                case 36001005: {
                    ret.getInfo().put(MapleStatInfo.attackCount, 4);
                    break;
                }
                case 61101002: 
                case 61110211: {
                    ret.getInfo().put(MapleStatInfo.attackCount, 3);
                    break;
                }
                case 61120007: 
                case 61121217: {
                    ret.getInfo().put(MapleStatInfo.attackCount, 5);
                    break;
                }
                case 400041087: {
                    ret.getInfo().put(MapleStatInfo.attackCount, 8);
                    break;
                }
                case 13101022: 
                case 13110022: 
                case 13120003: 
                case 13121055: {
                    ret.getInfo().put(MapleStatInfo.attackCount, 6);
                }
            }
        }
        if (!ret.isSkill() && ret.getInfo().get((Object)MapleStatInfo.time) > -1) {
            ret.setOverTime(true);
        } else {
            if (ret.getInfo().get((Object)MapleStatInfo.time) < 1000) {
                ret.getInfo().put(MapleStatInfo.time, (long)ret.getInfo().get((Object)MapleStatInfo.time).intValue() * 1000L >= Integer.MAX_VALUE ? Integer.MAX_VALUE : ret.getInfo().get((Object)MapleStatInfo.time) * 1000);
            }
            ret.setOverTime(overTime || ret.isMorph() || ret.is戒指技能() || ret.getSummonMovementType() != null);
            ret.setNotRemoved(notRemoved);
            ret.setNotIncBuffDuration(notIncBuffDuration);
        }
        EnumMap<MonsterStatus, Integer> monsterStatus = new EnumMap<MonsterStatus, Integer>(MonsterStatus.class);
        EnumMap<SecondaryStat, Integer> statups = new EnumMap<SecondaryStat, Integer>(SecondaryStat.class);
        AbstractSkillHandler handler = ret.getSkillHandler();
        int handleRes = -1;
        if (handler != null && (handleRes = handler.onSkillLoad(statups, monsterStatus, ret)) == 0) {
            return ret;
        }
        if (handleRes == -1 && ret.isOverTime() && ret.getSummonMovementType() == null) {
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.STR, ret.getInfo().get((Object)MapleStatInfo.str));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.INT, ret.getInfo().get((Object)MapleStatInfo.int_));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.DEX, ret.getInfo().get((Object)MapleStatInfo.dex));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.LUK, ret.getInfo().get((Object)MapleStatInfo.luk));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.PAD, ret.getInfo().get((Object)MapleStatInfo.pad));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.PDD, ret.getInfo().get((Object)MapleStatInfo.pdd));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.MAD, ret.getInfo().get((Object)MapleStatInfo.mad));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.ACC, ret.getInfo().get((Object)MapleStatInfo.acc));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.EVA, ret.getInfo().get((Object)MapleStatInfo.eva));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.EVAR, ret.getInfo().get((Object)MapleStatInfo.evaR));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.Craft, 0);
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.Speed, sourceid == 32001016 ? ret.getInfo().get((Object)MapleStatInfo.x) : ret.getInfo().get((Object)MapleStatInfo.speed));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.Jump, ret.getInfo().get((Object)MapleStatInfo.jump));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.EMHP, ret.getInfo().get((Object)MapleStatInfo.emhp));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.EMMP, ret.getInfo().get((Object)MapleStatInfo.emmp));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.EPAD, ret.getInfo().get((Object)MapleStatInfo.epad));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.EMAD, ret.getInfo().get((Object)MapleStatInfo.emad));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.EPDD, ret.getInfo().get((Object)MapleStatInfo.epdd));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.Booster, ret.getBooster());
            if (sourceid != 33111007) {
                MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.MaxHP, ret.getInfo().get((Object)MapleStatInfo.mhpR));
            }
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.MaxMP, ret.getInfo().get((Object)MapleStatInfo.mmpR));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.Thaw, Integer.valueOf(ret.getThaw()));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.MesoUpByItem, ItemConstants.getModifier(Math.abs(ret.getSourceId()), ret.getMesoup()));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.DefenseState, ret.getIllusion());
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.DojangBerserk, ret.getBerserk2());
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.RepeatEffect, ret.getBerserk());
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.ExpBuffRate, ret.getExpBuff());
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.Inflation, ret.getInflation());
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.DropRate, ItemConstants.getModifier(Math.abs(ret.getSourceId()), ret.getItemup()));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.DropRate, ret.getInfo().get((Object)MapleStatInfo.dropRate));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.PlusExpRate, ret.getInfo().get((Object)MapleStatInfo.plusExpRate));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndiePAD, ret.getInfo().get((Object)MapleStatInfo.indiePad));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieMAD, ret.getInfo().get((Object)MapleStatInfo.indieMad));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndiePDD, ret.getInfo().get((Object)MapleStatInfo.indiePdd));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieMHP, Integer.valueOf(ret.getImhp()));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieMMP, Integer.valueOf(ret.getImmp()));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieMHP, ret.getInfo().get((Object)MapleStatInfo.indieMhp));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieMMP, ret.getInfo().get((Object)MapleStatInfo.indieMmp));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieACC, ret.getInfo().get((Object)MapleStatInfo.indieAcc));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieEVA, ret.getInfo().get((Object)MapleStatInfo.indieEva));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieJump, ret.getInfo().get((Object)MapleStatInfo.indieJump));
            if (sourceid != 35001002 && sourceid != 35120000) {
                MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieSpeed, ret.getInfo().get((Object)MapleStatInfo.indieSpeed));
            }
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieAllStat, ret.getInfo().get((Object)MapleStatInfo.indieAllStat));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieEXP, ret.getInfo().get((Object)MapleStatInfo.indieExp));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieBooster, ret.getInfo().get((Object)MapleStatInfo.indieBooster));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieSTR, ret.getInfo().get((Object)MapleStatInfo.indieSTR));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieDEX, ret.getInfo().get((Object)MapleStatInfo.indieDEX));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieINT, ret.getInfo().get((Object)MapleStatInfo.indieINT));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieLUK, ret.getInfo().get((Object)MapleStatInfo.indieLUK));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieDamR, ret.getInfo().get((Object)MapleStatInfo.indieDamR));
            MapleStatEffectFactory.addBuffStatPairToListIfNotZero(statups, SecondaryStat.IndieCr, ret.getInfo().get((Object)MapleStatInfo.indieCr));
        }
        if (skillObj != null) {
            if (skillObj.isInfo(SkillInfo.dotType)) {
                MapleStatEffectFactory.addDebuffStatPairToListIfNotZero(monsterStatus, MonsterStatus.Burned, ret.getInfo().get((Object)MapleStatInfo.dot), ret.getInfo().get((Object)MapleStatInfo.dot));
            }
            if (skillObj.isInfo(SkillInfo.dot)) {
                MapleStatEffectFactory.addDebuffStatPairToListIfNotZero(monsterStatus, MonsterStatus.Burned, ret.getInfo().get((Object)MapleStatInfo.dot), ret.getInfo().get((Object)MapleStatInfo.dot));
            }
            if (skillObj.getMesInfo(SkillMesInfo.stun)) {
                MapleStatEffectFactory.addDebuffStatPairToListIfNotZero(monsterStatus, MonsterStatus.Stun, 1, 1);
            }
            if (skillObj.getMesInfo(SkillMesInfo.darkness)) {
                MapleStatEffectFactory.addDebuffStatPairToListIfNotZero(monsterStatus, MonsterStatus.Blind, 1, 1);
            }
            if (skillObj.getMesInfo(SkillMesInfo.seal)) {
                MapleStatEffectFactory.addDebuffStatPairToListIfNotZero(monsterStatus, MonsterStatus.Seal, 1, 1);
            }
            if (skillObj.getMesInfo(SkillMesInfo.cold)) {
                MapleStatEffectFactory.addDebuffStatPairToListIfNotZero(monsterStatus, MonsterStatus.Speed, ret.getInfo().get((Object)MapleStatInfo.s), ret.getInfo().get((Object)MapleStatInfo.s));
            }
            if (skillObj.getMesInfo(SkillMesInfo.freeze)) {
                MapleStatEffectFactory.addDebuffStatPairToListIfNotZero(monsterStatus, MonsterStatus.Freeze, 1, 1);
            }
            if (skillObj.getMesInfo(SkillMesInfo.slow)) {
                MapleStatEffectFactory.addDebuffStatPairToListIfNotZero(monsterStatus, MonsterStatus.Speed, ret.getInfo().get((Object)MapleStatInfo.x), ret.getInfo().get((Object)MapleStatInfo.x));
            }
        }
        MapleStatEffectFactory.addDebuffStatPairToListIfNotZero(monsterStatus, MonsterStatus.Showdown, ret.getInfo().get((Object)MapleStatInfo.expR), ret.getInfo().get((Object)MapleStatInfo.expR));
        if (handleRes == -1) {
            if (ret.isSkill()) {
                switch (sourceid) {
                    case 80001079: {
                        statups.put(SecondaryStat.CarnivalAttack, ret.info.get((Object)MapleStatInfo.damage));
                        break;
                    }
                    case 80001080: {
                        statups.put(SecondaryStat.CarnivalDefence, ret.info.get((Object)MapleStatInfo.x) + ret.info.get((Object)MapleStatInfo.x) * 1000);
                        break;
                    }
                    case 80001081: {
                        statups.put(SecondaryStat.CarnivalExp, ret.info.get((Object)MapleStatInfo.x));
                        break;
                    }
                    case 80011247: {
                        ret.info.put(MapleStatInfo.time, 1000);
                        statups.put(SecondaryStat.BodyRectGuardPrepare, 1);
                        break;
                    }
                    case 80011248: {
                        statups.clear();
                        statups.put(SecondaryStat.IndiePDD, 500);
                        statups.put(SecondaryStat.IndieMDF, 500);
                        statups.put(SecondaryStat.DawnShield_ExHP, 0);
                        break;
                    }
                    case 80011249: {
                        statups.put(SecondaryStat.DawnShield_WillCare, ret.getInfo().get((Object)MapleStatInfo.x));
                        break;
                    }
                    case 80011993: {
                        ret.info.put(MapleStatInfo.time, 2100000000);
                        ret.info.put(MapleStatInfo.mpCon, 0);
                        statups.put(SecondaryStat.ErdaStack, 1);
                        break;
                    }
                    case 80003342: {
                        statups.put(SecondaryStat.KaringDoolAdvantage, 1);
                        break;
                    }
                    case 80001479: {
                        statups.put(SecondaryStat.IndiePADR, ret.getInfo().get((Object)MapleStatInfo.x));
                        statups.put(SecondaryStat.IndieMADR, ret.getInfo().get((Object)MapleStatInfo.y));
                        break;
                    }
                    case 80001461: {
                        statups.put(SecondaryStat.IndiePADR, ret.getInfo().get((Object)MapleStatInfo.x));
                        break;
                    }
                    case 80001089: 
                    case 80001242: {
                        statups.put(SecondaryStat.NewFlying, 1);
                        break;
                    }
                    case 80011513: {
                        statups.put(SecondaryStat.DamAbsorbShield, ret.info.get((Object)MapleStatInfo.x));
                        statups.put(SecondaryStat.IndieEXP, ret.info.get((Object)MapleStatInfo.y));
                        break;
                    }
                    case 9101008: {
                        statups.put(SecondaryStat.MaxHP, ret.getInfo().get((Object)MapleStatInfo.x));
                        statups.put(SecondaryStat.MaxMP, ret.getInfo().get((Object)MapleStatInfo.y));
                        break;
                    }
                    case 9101002: {
                        statups.put(SecondaryStat.HolySymbol, ret.getInfo().get((Object)MapleStatInfo.x));
                        break;
                    }
                    case 80001034: 
                    case 80001035: 
                    case 80001036: {
                        statups.put(SecondaryStat.Event, 1);
                        break;
                    }
                    case 9101003: {
                        statups.clear();
                        statups.put(SecondaryStat.IndiePAD, ret.getInfo().get((Object)MapleStatInfo.indiePad));
                        statups.put(SecondaryStat.IndieMAD, ret.getInfo().get((Object)MapleStatInfo.indieMad));
                        statups.put(SecondaryStat.IndieMHPR, ret.getInfo().get((Object)MapleStatInfo.indieMhpR));
                        statups.put(SecondaryStat.IndieMMPR, ret.getInfo().get((Object)MapleStatInfo.indieMmpR));
                        statups.put(SecondaryStat.PDD, ret.getInfo().get((Object)MapleStatInfo.pdd));
                        statups.put(SecondaryStat.Speed, ret.getInfo().get((Object)MapleStatInfo.speed));
                        break;
                    }
                    case 9101000: {
                        ret.setHpR(1.0);
                        break;
                    }
                    case 80001428: {
                        statups.clear();
                        statups.put(SecondaryStat.IndieAsrR, ret.getInfo().get((Object)MapleStatInfo.indieAsrR));
                        statups.put(SecondaryStat.IndieStance, ret.getInfo().get((Object)MapleStatInfo.indieStance));
                        statups.put(SecondaryStat.DotHealHPPerSecond, ret.getInfo().get((Object)MapleStatInfo.dotHealHPPerSecondR));
                        statups.put(SecondaryStat.DotHealMPPerSecond, ret.getInfo().get((Object)MapleStatInfo.dotHealMPPerSecondR));
                        break;
                    }
                    case 80001430: {
                        statups.clear();
                        statups.put(SecondaryStat.IndieBooster, ret.getInfo().get((Object)MapleStatInfo.indieBooster));
                        statups.put(SecondaryStat.IndieDamR, ret.getInfo().get((Object)MapleStatInfo.indieDamR));
                        break;
                    }
                    case 80001432: 
                    case 80001754: {
                        statups.clear();
                        statups.put(SecondaryStat.IndieDamR, ret.getInfo().get((Object)MapleStatInfo.indieDamR));
                        break;
                    }
                    case 80002902: {
                        ret.setOverTime(false);
                        statups.clear();
                        statups.put(SecondaryStat.Blind, 1);
                        break;
                    }
                    case 80001752: 
                    case 80001756: {
                        statups.put(SecondaryStat.RandAreaAttack, ret.getInfo().get((Object)MapleStatInfo.x));
                        break;
                    }
                    case 80001753: 
                    case 80001757: 
                    case 80001761: {
                        statups.clear();
                        statups.put(SecondaryStat.IndieJump, ret.getInfo().get((Object)MapleStatInfo.indieJump));
                        statups.put(SecondaryStat.IndieSpeed, ret.getInfo().get((Object)MapleStatInfo.indieSpeed));
                        statups.put(SecondaryStat.IndieNotDamaged, 1);
                        statups.put(SecondaryStat.Inflation, ret.getInfo().get((Object)MapleStatInfo.x));
                        break;
                    }
                    case 80001875: {
                        statups.put(SecondaryStat.FixCoolTime, ret.getInfo().get((Object)MapleStatInfo.fixCoolTime));
                        break;
                    }
                    case 80001876: {
                        statups.put(SecondaryStat.RideVehicle, 1939006);
                        break;
                    }
                    case 80002280: {
                        statups.put(SecondaryStat.IndieEXP, ret.getInfo().get((Object)MapleStatInfo.indieExp));
                        break;
                    }
                    case 80002281: {
                        statups.put(SecondaryStat.MesoUp, 100);
                        break;
                    }
                    case 80002282: {
                        statups.put(SecondaryStat.RuneStoneNoTime, 1);
                        break;
                    }
                    case 80002888: {
                        statups.put(SecondaryStat.RunePurification, 1);
                        break;
                    }
                    case 80002889: {
                        statups.put(SecondaryStat.IndieBuffIcon, 1);
                        break;
                    }
                    case 80002890: {
                        statups.put(SecondaryStat.RuneContagion, 1);
                        break;
                    }
                    case 80001371: {
                        statups.put(SecondaryStat.IndieMHPR, ret.getInfo().get((Object)MapleStatInfo.indieMhpR));
                        statups.put(SecondaryStat.IndieMMPR, ret.getInfo().get((Object)MapleStatInfo.indieMmpR));
                        statups.put(SecondaryStat.IndieBDR, ret.getInfo().get((Object)MapleStatInfo.x));
                        break;
                    }
                    case 80001312: 
                    case 80001313: 
                    case 80001314: 
                    case 80001315: {
                        statups.put(SecondaryStat.RideVehicle, 1932187 + (sourceid - 80001312));
                        break;
                    }
                    case 80001155: {
                        statups.put(SecondaryStat.IndieDamR, ret.getInfo().get((Object)MapleStatInfo.indieDamR));
                        break;
                    }
                    case 80011158: {
                        statups.clear();
                        statups.put(SecondaryStat.IndiePADR, ret.getInfo().get((Object)MapleStatInfo.x));
                        break;
                    }
                    case 80001218: {
                        statups.put(SecondaryStat.SoulSkillDamageUp, ret.getX());
                        break;
                    }
                    case 90001006: {
                        monsterStatus.put(MonsterStatus.Freeze, 1);
                        ret.getInfo().put(MapleStatInfo.time, ret.getInfo().get((Object)MapleStatInfo.time) * 2);
                        break;
                    }
                    case 9101004: {
                        ret.getInfo().put(MapleStatInfo.time, 0);
                        statups.put(SecondaryStat.DarkSight, ret.getInfo().get((Object)MapleStatInfo.x));
                        break;
                    }
                    case 23111004: {
                        statups.put(SecondaryStat.AddAttackCount, ret.getInfo().get((Object)MapleStatInfo.x));
                        break;
                    }
                    case 36101002: {
                        statups.put(SecondaryStat.CriticalBuff, ret.getInfo().get((Object)MapleStatInfo.x));
                        break;
                    }
                    case 36121004: {
                        statups.put(SecondaryStat.Stance, ret.getInfo().get((Object)MapleStatInfo.x));
                        statups.put(SecondaryStat.IgnoreTargetDEF, ret.getInfo().get((Object)MapleStatInfo.y));
                        break;
                    }
                    case 9001020: 
                    case 9101020: {
                        monsterStatus.put(MonsterStatus.Seal, 1);
                        break;
                    }
                    case 90001002: {
                        monsterStatus.put(MonsterStatus.Speed, ret.getInfo().get((Object)MapleStatInfo.x));
                        break;
                    }
                    case 80011540: {
                        monsterStatus.put(MonsterStatus.Smite, 1);
                        break;
                    }
                    case 90001003: {
                        monsterStatus.put(MonsterStatus.Poison, 1);
                        break;
                    }
                    case 90001005: {
                        monsterStatus.put(MonsterStatus.Seal, 1);
                    }
                }
                if (JobConstants.is零轉職業(sourceid / 10000)) {
                    switch (sourceid % 10000) {
                        case 99: 
                        case 104: {
                            monsterStatus.put(MonsterStatus.Speed, 1);
                            ret.getInfo().put(MapleStatInfo.time, ret.getInfo().get((Object)MapleStatInfo.time) * 2);
                            break;
                        }
                        case 103: {
                            monsterStatus.put(MonsterStatus.Stun, 1);
                            break;
                        }
                        case 1001: {
                            if (ret.is潛入()) {
                                statups.put(SecondaryStat.Sneak, ret.getInfo().get((Object)MapleStatInfo.x));
                                break;
                            }
                            statups.put(SecondaryStat.Regen, ret.getInfo().get((Object)MapleStatInfo.x));
                            break;
                        }
                        case 1010: {
                            ret.getInfo().put(MapleStatInfo.time, 2100000000);
                            statups.put(SecondaryStat.DojangInvincible, 1);
                            statups.put(SecondaryStat.NotDamaged, 1);
                            break;
                        }
                        case 1011: {
                            statups.put(SecondaryStat.DojangBerserk, ret.getInfo().get((Object)MapleStatInfo.x));
                            break;
                        }
                        case 1026: 
                        case 1142: {
                            ret.getInfo().put(MapleStatInfo.time, 2100000000);
                            statups.put(SecondaryStat.Flying, 1);
                            break;
                        }
                        case 8001: {
                            statups.put(SecondaryStat.SoulArrow, ret.getInfo().get((Object)MapleStatInfo.x));
                            break;
                        }
                        case 8002: {
                            statups.put(SecondaryStat.SharpEyes, (ret.getInfo().get((Object)MapleStatInfo.x) << 8) + ret.getInfo().get((Object)MapleStatInfo.y) + ret.getInfo().get((Object)MapleStatInfo.criticaldamageMax));
                            break;
                        }
                        case 8003: {
                            statups.put(SecondaryStat.MaxHP, ret.getInfo().get((Object)MapleStatInfo.x));
                            statups.put(SecondaryStat.MaxMP, ret.getInfo().get((Object)MapleStatInfo.x));
                            break;
                        }
                        case 8004: {
                            statups.put(SecondaryStat.CombatOrders, ret.getInfo().get((Object)MapleStatInfo.x));
                            break;
                        }
                        case 8005: {
                            statups.clear();
                            statups.put(SecondaryStat.AdvancedBless, ret.getInfo().get((Object)MapleStatInfo.x));
                            statups.put(SecondaryStat.IndieMHP, ret.getInfo().get((Object)MapleStatInfo.indieMhp));
                            statups.put(SecondaryStat.IndieMMP, ret.getInfo().get((Object)MapleStatInfo.indieMmp));
                            break;
                        }
                        case 8006: {
                            statups.put(SecondaryStat.Booster, ret.getInfo().get((Object)MapleStatInfo.x));
                            break;
                        }
                        case 169: {
                            statups.put(SecondaryStat.PreReviveOnce, 1);
                            ret.getInfo().put(MapleStatInfo.time, 2100000000);
                            ret.setOverTime(true);
                        }
                    }
                }
            } else {
                switch (sourceid) {
                    case 2022746: 
                    case 2022747: 
                    case 2022823: {
                        statups.clear();
                        statups.put(SecondaryStat.RepeatEffect, 1);
                        int value = sourceid == 2022746 ? 5 : (sourceid == 2022747 ? 10 : 12);
                        statups.put(SecondaryStat.IndiePAD, value);
                        statups.put(SecondaryStat.IndieMAD, value);
                        break;
                    }
                    case 2003596: {
                        statups.put(SecondaryStat.IndieBDR, ret.getInfo().get((Object)MapleStatInfo.indieBDR));
                        break;
                    }
                    case 2023632: {
                        statups.put(SecondaryStat.IndieBDR, ret.getInfo().get((Object)MapleStatInfo.indieBDR));
                    }
                }
            }
        }
        if (ret.getSummonMovementType() != null || isSummon) {
            statups.put(SecondaryStat.IndieBuffIcon, 1);
        }
        if (SkillConstants.is召喚獸戒指(sourceid)) {
            ret.getInfo().put(MapleStatInfo.time, 2100000000);
        }
        if (ret.isMorph()) {
            statups.put(SecondaryStat.Morph, ret.getMorph());
            if (ret.is凱撒終極型態() || ret.is凱撒超終極型態()) {
                statups.put(SecondaryStat.Stance, ret.getInfo().get((Object)MapleStatInfo.prop));
                statups.put(SecondaryStat.CriticalBuff, ret.getInfo().get((Object)MapleStatInfo.cr));
                statups.put(SecondaryStat.IndieDamR, ret.getInfo().get((Object)MapleStatInfo.indieDamR));
                statups.put(SecondaryStat.IndieBooster, ret.getInfo().get((Object)MapleStatInfo.indieBooster));
            }
        }
        if (ret.is超越攻擊狀態()) {
            statups.clear();
            ret.getInfo().put(MapleStatInfo.time, 15000);
            statups.put(SecondaryStat.Exceed, 1);
        }
        ret.setStatups(statups);
        ret.setMonsterStatus(monsterStatus);
        return ret;
    }

    public static int parseMountInfo(MapleCharacter player, int skillid) {
        if (skillid == 80001000 || SkillConstants.is騎乘技能(skillid)) {
            if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-123) != null && player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-124) != null) {
                return player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-123).getItemId();
            }
            return MapleStatEffectFactory.parseMountInfo_Pure(player, skillid);
        }
        return GameConstants.getMountItem(skillid, player);
    }

    static int parseMountInfo_Pure(MapleCharacter player, int skillid) {
        if (skillid == 80001000 || SkillConstants.is騎乘技能(skillid)) {
            if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-18) != null && player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-19) != null) {
                return player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-18).getItemId();
            }
            return 0;
        }
        return GameConstants.getMountItem(skillid, player);
    }

    public static int makeHealHP(double rate, double stat, double lowerfactor, double upperfactor) {
        return (int)(Math.random() * (double)((int)(stat * upperfactor * rate) - (int)(stat * lowerfactor * rate) + 1) + (double)((int)(stat * lowerfactor * rate)));
    }

    public static Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft, Point lt, Point rb, int range) {
        Rectangle rect;
        if (lt == null || rb == null) {
            rect = new Rectangle((facingLeft ? -200 - range : 0) + posFrom.x, -100 - range + posFrom.y, 200 + range, 100 + range);
        } else {
            Point myrb;
            Point mylt;
            if (facingLeft) {
                mylt = new Point(lt.x + posFrom.x - range, lt.y + posFrom.y);
                myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
            } else {
                myrb = new Point(lt.x * -1 + posFrom.x + range, rb.y + posFrom.y);
                mylt = new Point(rb.x * -1 + posFrom.x, lt.y + posFrom.y);
            }
            rect = new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
        }
        if (rect.width < 0) {
            int x = rect.x;
            rect.x += rect.width;
            rect.width = x - rect.x;
        }
        if (rect.height < 0) {
            int y = rect.y;
            rect.y += rect.height;
            rect.height = y - rect.y;
        }
        return rect;
    }

    private static void addDebuffStatPairToListIfNotZero(Map<MonsterStatus, Integer> list, MonsterStatus buffstat, Integer val, Integer x) {
        if (!(val == 0 || list.containsKey(buffstat) && list.get(buffstat) != 0)) {
            list.put(buffstat, x);
        }
    }
}

