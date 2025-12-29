/*
 * Decompiled with CFR 0.152.
 */
package Client.inventory;

import Client.inventory.Equip;
import Config.configs.ServerConfig;
import Config.constants.ItemConstants;
import Net.server.MapleItemInformationProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tools.Pair;
import tools.Randomizer;

public final class NirvanaFlame {
    private static final double[] NORMAL_RATES = new double[]{1.0, 2.0, 3.65, 5.35, 7.3, 8.8, 10.25, 11.7, 13.15};
    private static final double[] SPECIAL_RATES = new double[]{1.0, 2.0, 3.0, 4.4, 6.05, 8.0, 10.25, 11.7, 13.17};
    private int str;
    private int dex;
    private int _int;
    private int luk;
    private int hp;
    private int mp;
    private int pad;
    private int mad;
    private int reqLevel;
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
    private long flag;

    public NirvanaFlame() {
        this.reset();
    }

    public NirvanaFlame(NirvanaFlame n) {
        this.str = n.str;
        this.dex = n.dex;
        this._int = n._int;
        this.luk = n.luk;
        this.hp = n.hp;
        this.mp = n.mp;
        this.pad = n.pad;
        this.mad = n.mad;
        this.pdd = n.pdd;
        this.mdd = n.mdd;
        this.acc = n.acc;
        this.avoid = n.avoid;
        this.hands = n.hands;
        this.speed = n.speed;
        this.jump = n.jump;
        this.bossDamage = n.bossDamage;
        this.ignorePDR = n.ignorePDR;
        this.totalDamage = n.totalDamage;
        this.allStat = n.allStat;
        this.flag = n.flag;
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
        this.flag = 0L;
    }

    public static long randomStateFromJson(Equip equip, int nfId) {
        Pair<Integer, Integer> pair;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (nfId == 0) {
            if (ii.isCash(equip.getItemId())) {
                return 0L;
            }
            if (!(ItemConstants.類型.口袋道具(equip.getItemId()) || ItemConstants.類型.墜飾(equip.getItemId()) || ItemConstants.類型.武器(equip.getItemId()) || ItemConstants.類型.腰帶(equip.getItemId()) || ItemConstants.類型.帽子(equip.getItemId()) || ItemConstants.類型.臉飾(equip.getItemId()) || ItemConstants.類型.眼飾(equip.getItemId()) || ItemConstants.類型.上衣(equip.getItemId()) || ItemConstants.類型.套服(equip.getItemId()) || ItemConstants.類型.褲裙(equip.getItemId()) || ItemConstants.類型.鞋子(equip.getItemId()) || ItemConstants.類型.耳環(equip.getItemId()) || ItemConstants.類型.手套(equip.getItemId()) || ItemConstants.類型.披風(equip.getItemId()))) {
                return 0L;
            }
        }
        int statCountMin = 1;
        int statCountMax = equip.getReqLevel() < 30 ? 2 : (equip.getReqLevel() < 90 ? 3 : (equip.getReqLevel() < 110 ? 3 : 4));
        boolean isBossReward = ii.isBossReward(equip.getItemId());
        int statCount = isBossReward || equip.isMvpEquip() ? 4 : Randomizer.rand(statCountMin, statCountMax);
        EnumMap<EquipExFlag, Integer> statProps = new EnumMap<EquipExFlag, Integer>(EquipExFlag.class);
        statProps.put(EquipExFlag.FLAGEx_iSTR, 70);
        statProps.put(EquipExFlag.FLAGEx_iDEX, 70);
        statProps.put(EquipExFlag.FLAGEx_iINT, 70);
        statProps.put(EquipExFlag.FLAGEx_iLUK, 70);
        statProps.put(EquipExFlag.FLAGEx_iSTR_DEX, 70);
        statProps.put(EquipExFlag.FLAGEx_iSTR_INT, 70);
        statProps.put(EquipExFlag.FLAGEx_iSTR_LUK, 70);
        statProps.put(EquipExFlag.FLAGEx_iDEX_INT, 70);
        statProps.put(EquipExFlag.FLAGEx_iDEX_LUK, 70);
        statProps.put(EquipExFlag.FLAGEx_iINT_LUK, 70);
        statProps.put(EquipExFlag.FLAGEx_iMAXHP, 65);
        statProps.put(EquipExFlag.FLAGEx_iMAXMP, 65);
        if (equip.getReqLevel() >= 50) {
            statProps.put(EquipExFlag.FLAGEx_iREQLEVEL, 30);
        }
        statProps.put(EquipExFlag.FLAGEx_iPDD, 70);
        if (ItemConstants.類型.武器(equip.getItemId())) {
            if (ItemConstants.類型.魔法武器(equip.getItemId())) {
                statProps.put(EquipExFlag.FLAGEx_iMAD, 70);
            } else {
                statProps.put(EquipExFlag.FLAGEx_iPAD, 70);
            }
            if (equip.getReqLevel() >= 130) {
                statProps.put(EquipExFlag.FLAGEx_iBDR, 10);
                statProps.put(EquipExFlag.FLAGEx_iDAMR, 10);
            }
        } else {
            statProps.put(EquipExFlag.FLAGEx_iPAD, 60);
            statProps.put(EquipExFlag.FLAGEx_iMAD, 60);
            statProps.put(EquipExFlag.FLAGEx_iSPEED, 50);
            statProps.put(EquipExFlag.FLAGEx_iJUMP, 50);
        }
        statProps.put(EquipExFlag.FLAGEx_iSTATR, 10);
        Pair<Integer, Integer> tierLimit = new Pair<Integer, Integer>(1, 3);
        int[] tierRate = new int[]{60, 39, 1};
        switch (nfId) {
            case 2048716: 
            case 2048724: {
                tierRate = new int[]{20, 30, 36, 14};
                if (!ServerConfig.KMS_NirvanaFlameTier) break;
                pair = tierLimit;
                pair.left = (Integer)pair.left + 2;
                pair = tierLimit;
                pair.right = (Integer)pair.right + 3;
                break;
            }
            case 2048717: 
            case 2048721: 
            case 2048723: {
                tierRate = new int[]{29, 45, 25, 1};
                if (ServerConfig.KMS_NirvanaFlameTier) {
                    pair = tierLimit;
                    pair.left = (Integer)pair.left + 3;
                    pair = tierLimit;
                    pair.right = (Integer)pair.right + 4;
                    break;
                }
                pair = tierLimit;
                pair.left = (Integer)pair.left + 1;
                break;
            }
            default: {
                if (nfId != 2048761 && nfId != 5064502 && !ServerConfig.KMS_NirvanaFlameTier) break;
                pair = tierLimit;
                pair.left = (Integer)pair.left + 2;
                pair = tierLimit;
                pair.right = (Integer)pair.right + 2;
                if (!isBossReward) break;
                pair = tierLimit;
                pair.left = (Integer)pair.left + 2;
                pair = tierLimit;
                pair.right = (Integer)pair.right + 2;
            }
        }
        if (equip.isMvpEquip()) {
            pair = tierLimit;
            pair.left = (Integer)pair.left + 2;
            pair = tierLimit;
            pair.right = (Integer)pair.right + 2;
        }
        HashMap<EquipExFlag, Integer> nfStats = new HashMap<EquipExFlag, Integer>();
        long flag = 0L;
        List<EquipExFlag> list = Arrays.asList(EquipExFlag.values());
        while (nfStats.size() < statCount) {
            Collections.shuffle(list);
            EquipExFlag equipExFlag = list.get(Randomizer.nextInt(list.size()));
            if (!statProps.containsKey((Object)equipExFlag) || nfStats.containsKey((Object)equipExFlag) || !Randomizer.isSuccess((Integer)statProps.get((Object)equipExFlag))) continue;
            int tier = ServerConfig.KMS_NirvanaFlameTier && nfStats.isEmpty() && (nfId == 2048717 || nfId == 2048721 || nfId == 2048723) ? (Integer)tierLimit.right : NirvanaFlame.randomTier(tierRate, tierLimit);
            nfStats.put(equipExFlag, Math.min(9, tier));
        }
        for (Map.Entry entry : nfStats.entrySet()) {
            flag = flag * 1000L + (long)(((EquipExFlag)((Object)entry.getKey())).getType() + (Integer)entry.getValue());
        }
        equip.setFlameFlag(flag);
        return flag;
    }

    public static void randomState(Equip equip, int nfId) {
        Pair<Integer, Integer> pair;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (nfId == 0) {
            if (ii.isCash(equip.getItemId())) {
                return;
            }
            if (!(ItemConstants.類型.口袋道具(equip.getItemId()) || ItemConstants.類型.墜飾(equip.getItemId()) || ItemConstants.類型.圖騰(equip.getItemId()) || ItemConstants.類型.武器(equip.getItemId()) || ItemConstants.類型.腰帶(equip.getItemId()) || ItemConstants.類型.帽子(equip.getItemId()) || ItemConstants.類型.臉飾(equip.getItemId()) || ItemConstants.類型.眼飾(equip.getItemId()) || ItemConstants.類型.上衣(equip.getItemId()) || ItemConstants.類型.套服(equip.getItemId()) || ItemConstants.類型.褲裙(equip.getItemId()) || ItemConstants.類型.鞋子(equip.getItemId()) || ItemConstants.類型.耳環(equip.getItemId()) || ItemConstants.類型.手套(equip.getItemId()) || ItemConstants.類型.披風(equip.getItemId()))) {
                return;
            }
        }
        int statCountMin = 1;
        int statCountMax = equip.getReqLevel() < 30 ? 2 : (equip.getReqLevel() < 90 ? 3 : (equip.getReqLevel() < 110 ? 3 : 4));
        boolean isBossReward = ii.isBossReward(equip.getItemId());
        int statCount = isBossReward || equip.isMvpEquip() ? 4 : Randomizer.rand(statCountMin, statCountMax);
        EnumMap<EquipExFlag, Integer> statProps = new EnumMap<EquipExFlag, Integer>(EquipExFlag.class);
        statProps.put(EquipExFlag.FLAGEx_iSTR, 70);
        statProps.put(EquipExFlag.FLAGEx_iDEX, 70);
        statProps.put(EquipExFlag.FLAGEx_iINT, 70);
        statProps.put(EquipExFlag.FLAGEx_iLUK, 70);
        statProps.put(EquipExFlag.FLAGEx_iSTR_DEX, 70);
        statProps.put(EquipExFlag.FLAGEx_iSTR_INT, 70);
        statProps.put(EquipExFlag.FLAGEx_iSTR_LUK, 70);
        statProps.put(EquipExFlag.FLAGEx_iDEX_INT, 70);
        statProps.put(EquipExFlag.FLAGEx_iDEX_LUK, 70);
        statProps.put(EquipExFlag.FLAGEx_iINT_LUK, 70);
        statProps.put(EquipExFlag.FLAGEx_iMAXHP, 65);
        statProps.put(EquipExFlag.FLAGEx_iMAXMP, 65);
        if (equip.getReqLevel() >= 50) {
            statProps.put(EquipExFlag.FLAGEx_iREQLEVEL, 30);
        }
        statProps.put(EquipExFlag.FLAGEx_iPDD, 70);
        if (ItemConstants.類型.武器(equip.getItemId())) {
            if (ItemConstants.類型.魔法武器(equip.getItemId())) {
                statProps.put(EquipExFlag.FLAGEx_iMAD, 70);
            } else {
                statProps.put(EquipExFlag.FLAGEx_iPAD, 70);
            }
            if (equip.getReqLevel() >= 130) {
                statProps.put(EquipExFlag.FLAGEx_iBDR, 10);
                statProps.put(EquipExFlag.FLAGEx_iDAMR, 10);
            }
        } else {
            statProps.put(EquipExFlag.FLAGEx_iPAD, 60);
            statProps.put(EquipExFlag.FLAGEx_iMAD, 60);
            statProps.put(EquipExFlag.FLAGEx_iSPEED, 50);
            statProps.put(EquipExFlag.FLAGEx_iJUMP, 50);
        }
        statProps.put(EquipExFlag.FLAGEx_iSTATR, 10);
        Pair<Integer, Integer> tierLimit = new Pair<Integer, Integer>(1, 3);
        int[] tierRate = new int[]{60, 39, 1};
        switch (nfId) {
            case 2048716: 
            case 2048724: {
                tierRate = new int[]{20, 30, 36, 14};
                if (!ServerConfig.KMS_NirvanaFlameTier) break;
                pair = tierLimit;
                pair.left = (Integer)pair.left + 2;
                pair = tierLimit;
                pair.right = (Integer)pair.right + 3;
                break;
            }
            case 2048717: 
            case 2048721: 
            case 2048723: {
                tierRate = new int[]{29, 45, 25, 1};
                if (ServerConfig.KMS_NirvanaFlameTier) {
                    pair = tierLimit;
                    pair.left = (Integer)pair.left + 3;
                    pair = tierLimit;
                    pair.right = (Integer)pair.right + 4;
                    break;
                }
                pair = tierLimit;
                pair.left = (Integer)pair.left + 1;
                break;
            }
            default: {
                if (nfId != 2048761 && nfId != 5064502 && !ServerConfig.KMS_NirvanaFlameTier) break;
                pair = tierLimit;
                pair.left = (Integer)pair.left + 2;
                pair = tierLimit;
                pair.right = (Integer)pair.right + 2;
                if (!isBossReward) break;
                pair = tierLimit;
                pair.left = (Integer)pair.left + 2;
                pair = tierLimit;
                pair.right = (Integer)pair.right + 2;
            }
        }
        if (equip.isMvpEquip()) {
            pair = tierLimit;
            pair.left = (Integer)pair.left + 2;
            pair = tierLimit;
            pair.right = (Integer)pair.right + 2;
        }
        HashMap<EquipExFlag, Integer> nfStats = new HashMap<EquipExFlag, Integer>();
        long flag = 0L;
        List<EquipExFlag> list = Arrays.asList(EquipExFlag.values());
        while (nfStats.size() < statCount) {
            Collections.shuffle(list);
            EquipExFlag equipExFlag = list.get(Randomizer.nextInt(list.size()));
            if (!statProps.containsKey((Object)equipExFlag) || nfStats.containsKey((Object)equipExFlag) || !Randomizer.isSuccess((Integer)statProps.get((Object)equipExFlag))) continue;
            int tier = ServerConfig.KMS_NirvanaFlameTier && nfStats.isEmpty() && (nfId == 2048717 || nfId == 2048721 || nfId == 2048723) ? (Integer)tierLimit.right : NirvanaFlame.randomTier(tierRate, tierLimit);
            nfStats.put(equipExFlag, Math.min(9, tier));
        }
        for (Map.Entry entry : nfStats.entrySet()) {
            flag = flag * 1000L + (long)(((EquipExFlag)((Object)entry.getKey())).getType() + (Integer)entry.getValue());
        }
        equip.setFlameFlag(flag);
    }

    private static int randomTier(int[] tierRate, Pair<Integer, Integer> tierLimit) {
        int tier = tierLimit.getLeft() - 1;
        int randomRate = Randomizer.nextInt(100);
        int rRate = 100;
        for (int rate : tierRate) {
            if (tier >= tierLimit.getRight()) break;
            if (rRate > randomRate) {
                ++tier;
            }
            rRate -= rate;
        }
        return tier;
    }

    public void resetEquipExStats(Equip equip) {
        long lFlag = this.flag;
        this.reset();
        this.flag = lFlag;
        Equip normalEquip = MapleItemInformationProvider.getInstance().getEquipById(equip.getItemId());
        do {
            int nFlag = (int)(lFlag % 1000L);
            lFlag /= 1000L;
            EquipExFlag exFlag = EquipExFlag.getByType(nFlag / 10 * 10);
            if (exFlag == null) continue;
            int value = NirvanaFlame.getExStat(normalEquip, exFlag, nFlag % 10);
            switch (exFlag.ordinal()) {
                case 0: {
                    this.str += value;
                    break;
                }
                case 1: {
                    this.dex += value;
                    break;
                }
                case 2: {
                    this._int += value;
                    break;
                }
                case 3: {
                    this.luk += value;
                    break;
                }
                case 4: {
                    this.str += value;
                    this.dex += value;
                    break;
                }
                case 5: {
                    this.str += value;
                    this._int += value;
                    break;
                }
                case 6: {
                    this.str += value;
                    this.luk += value;
                    break;
                }
                case 7: {
                    this.dex += value;
                    this._int += value;
                    break;
                }
                case 8: {
                    this.dex += value;
                    this.luk += value;
                    break;
                }
                case 9: {
                    this._int += value;
                    this.luk += value;
                    break;
                }
                case 10: {
                    this.hp += value;
                    break;
                }
                case 11: {
                    this.mp += value;
                    break;
                }
                case 12: {
                    this.reqLevel += value;
                    break;
                }
                case 13: {
                    this.pdd += value;
                    break;
                }
                case 14: {
                    this.pad += value;
                    break;
                }
                case 15: {
                    this.mad += value;
                    break;
                }
                case 16: {
                    this.speed += value;
                    break;
                }
                case 17: {
                    this.jump += value;
                    break;
                }
                case 18: {
                    this.bossDamage += value;
                    break;
                }
                case 19: {
                    this.totalDamage += value;
                    break;
                }
                case 20: {
                    this.allStat += value;
                }
            }
        } while (lFlag > 0L);
    }

    public static int getExStat(Equip equip, EquipExFlag stat, int grade) {
        switch (stat.ordinal()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 13: {
                return (equip.getReqLevel() / 20 + 1) * grade;
            }
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: {
                return (equip.getReqLevel() / 40 + 1) * grade;
            }
            case 10: 
            case 11: {
                return Math.max(3, equip.getReqLevel() / 10 * 30) * grade;
            }
            case 12: {
                return grade * 5;
            }
            case 16: 
            case 17: 
            case 19: 
            case 20: {
                return grade;
            }
            case 18: {
                return grade * 2;
            }
            case 14: 
            case 15: {
                if (ItemConstants.類型.武器(equip.getItemId())) {
                    short base = stat == EquipExFlag.FLAGEx_iPAD ? equip.getPad() : equip.getMad();
                    int level = equip.getReqLevel() / 40 + 1;
                    double rate = MapleItemInformationProvider.getInstance().isBossReward(equip.getItemId()) ? SPECIAL_RATES[grade - 1] : NORMAL_RATES[grade - 1];
                    return (int)Math.ceil((double)(base * level) * rate / 100.0);
                }
                return grade;
            }
        }
        return 0;
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

    public int getReqLevel() {
        return this.reqLevel;
    }

    public long getFlag() {
        return this.flag;
    }

    public void setFlag(long flag) {
        this.flag = flag;
    }

    public int getStatTier(EquipExFlag exFlag) {
        if (exFlag == null) {
            return 0;
        }
        long lFlag = this.flag;
        do {
            int nFlag = (int)(lFlag % 1000L);
            lFlag /= 1000L;
            if (exFlag != EquipExFlag.getByType(nFlag / 10 * 10)) continue;
            return nFlag % 10;
        } while (lFlag > 0L);
        return 0;
    }

    public void setStatTier(EquipExFlag exFlag, int tier) {
        if (exFlag == null || tier <= 0) {
            return;
        }
        long lFlag = this.flag;
        this.flag = 0L;
        do {
            int nFlag = (int)(lFlag % 1000L);
            lFlag /= 1000L;
            if (exFlag == EquipExFlag.getByType(nFlag / 10 * 10)) {
                nFlag = exFlag.getType() + tier;
            }
            this.flag = this.flag * 1000L + (long)nFlag;
        } while (lFlag > 0L);
    }

    public void transmitStat1(EquipExFlag src, EquipExFlag drt) {
        if (src == drt) {
            return;
        }
        long lFlag = this.flag;
        this.flag = 0L;
        do {
            int nFlag = (int)(lFlag % 1000L);
            lFlag /= 1000L;
            EquipExFlag exFlag = EquipExFlag.getByType(nFlag / 10 * 10);
            if (exFlag == null) continue;
            if (exFlag == src) {
                nFlag = drt.getType() + nFlag % 10;
            } else if (exFlag == drt) {
                nFlag = src.getType() + nFlag % 10;
            }
            this.flag = this.flag * 1000L + (long)nFlag;
        } while (lFlag > 0L);
    }

    public void transmitStat(EquipExFlag src, EquipExFlag drt) {
        if (src == drt) {
            return;
        }
        LinkedList<Pair<EquipExFlag, EquipExFlag>> exFlags = new LinkedList<Pair<EquipExFlag, EquipExFlag>>();
        if (!(EquipExFlag.FLAGEx_iSTR != src && EquipExFlag.FLAGEx_iDEX != src || EquipExFlag.FLAGEx_iDEX != drt && EquipExFlag.FLAGEx_iSTR != drt)) {
            exFlags.add(new Pair<EquipExFlag, EquipExFlag>(EquipExFlag.FLAGEx_iSTR_INT, EquipExFlag.FLAGEx_iDEX_INT));
            exFlags.add(new Pair<EquipExFlag, EquipExFlag>(EquipExFlag.FLAGEx_iSTR_LUK, EquipExFlag.FLAGEx_iDEX_LUK));
        } else if (!(EquipExFlag.FLAGEx_iSTR != src && EquipExFlag.FLAGEx_iINT != src || EquipExFlag.FLAGEx_iINT != drt && EquipExFlag.FLAGEx_iSTR != drt)) {
            exFlags.add(new Pair<EquipExFlag, EquipExFlag>(EquipExFlag.FLAGEx_iSTR_DEX, EquipExFlag.FLAGEx_iDEX_INT));
            exFlags.add(new Pair<EquipExFlag, EquipExFlag>(EquipExFlag.FLAGEx_iSTR_LUK, EquipExFlag.FLAGEx_iINT_LUK));
        } else if (!(EquipExFlag.FLAGEx_iSTR != src && EquipExFlag.FLAGEx_iLUK != src || EquipExFlag.FLAGEx_iLUK != drt && EquipExFlag.FLAGEx_iSTR != drt)) {
            exFlags.add(new Pair<EquipExFlag, EquipExFlag>(EquipExFlag.FLAGEx_iSTR_DEX, EquipExFlag.FLAGEx_iDEX_LUK));
            exFlags.add(new Pair<EquipExFlag, EquipExFlag>(EquipExFlag.FLAGEx_iSTR_INT, EquipExFlag.FLAGEx_iINT_LUK));
        } else if (!(EquipExFlag.FLAGEx_iDEX != src && EquipExFlag.FLAGEx_iINT != src || EquipExFlag.FLAGEx_iINT != drt && EquipExFlag.FLAGEx_iDEX != drt)) {
            exFlags.add(new Pair<EquipExFlag, EquipExFlag>(EquipExFlag.FLAGEx_iSTR_DEX, EquipExFlag.FLAGEx_iSTR_INT));
            exFlags.add(new Pair<EquipExFlag, EquipExFlag>(EquipExFlag.FLAGEx_iDEX_LUK, EquipExFlag.FLAGEx_iINT_LUK));
        } else if (!(EquipExFlag.FLAGEx_iDEX != src && EquipExFlag.FLAGEx_iLUK != src || EquipExFlag.FLAGEx_iLUK != drt && EquipExFlag.FLAGEx_iDEX != drt)) {
            exFlags.add(new Pair<EquipExFlag, EquipExFlag>(EquipExFlag.FLAGEx_iSTR_DEX, EquipExFlag.FLAGEx_iSTR_LUK));
            exFlags.add(new Pair<EquipExFlag, EquipExFlag>(EquipExFlag.FLAGEx_iDEX_INT, EquipExFlag.FLAGEx_iINT_LUK));
        } else if (!(EquipExFlag.FLAGEx_iINT != src && EquipExFlag.FLAGEx_iLUK != src || EquipExFlag.FLAGEx_iLUK != drt && EquipExFlag.FLAGEx_iINT != drt)) {
            exFlags.add(new Pair<EquipExFlag, EquipExFlag>(EquipExFlag.FLAGEx_iSTR_INT, EquipExFlag.FLAGEx_iSTR_LUK));
            exFlags.add(new Pair<EquipExFlag, EquipExFlag>(EquipExFlag.FLAGEx_iDEX_INT, EquipExFlag.FLAGEx_iDEX_LUK));
        }
        long srcFlag = this.flag;
        long drtFlag = 0L;
        do {
            int nFlag = (int)(srcFlag % 1000L);
            srcFlag /= 1000L;
            EquipExFlag exFlag = EquipExFlag.getByType(nFlag / 10 * 10);
            if (exFlag == null) continue;
            if (exFlag == src) {
                nFlag = drt.getType() + nFlag % 10;
            } else if (exFlag == drt) {
                nFlag = src.getType() + nFlag % 10;
            } else {
                for (Pair pair : exFlags) {
                    if (exFlag == pair.getLeft()) {
                        nFlag = ((EquipExFlag)((Object)pair.getRight())).getType() + nFlag % 10;
                        break;
                    }
                    if (exFlag != pair.getRight()) continue;
                    nFlag = ((EquipExFlag)((Object)pair.getLeft())).getType() + nFlag % 10;
                    break;
                }
            }
            drtFlag = drtFlag * 1000L + (long)nFlag;
        } while (srcFlag > 0L);
        this.flag = drtFlag;
    }

    public static Map<Integer, Integer> getExStat(Equip equip, long flag) {
        HashMap<Integer, Integer> statProps = new HashMap<Integer, Integer>();
        long lFlag = flag;
        do {
            int grade = (int)(lFlag % 10L);
            int statFlag = (int)(lFlag % 1000L / 10L);
            int code = 0;
            switch (EquipExFlag.getByType(statFlag * 10).ordinal()) {
                case 0: {
                    code = 0;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 20 + 1) * grade : (equip.getReqLevel() / 20 + 1) * grade);
                    break;
                }
                case 1: {
                    code = 1;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 20 + 1) * grade : (equip.getReqLevel() / 20 + 1) * grade);
                    break;
                }
                case 2: {
                    code = 2;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 20 + 1) * grade : (equip.getReqLevel() / 20 + 1) * grade);
                    break;
                }
                case 3: {
                    code = 3;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 20 + 1) * grade : (equip.getReqLevel() / 20 + 1) * grade);
                    break;
                }
                case 13: {
                    code = 13;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 20 + 1) * grade : (equip.getReqLevel() / 20 + 1) * grade);
                    break;
                }
                case 4: {
                    code = 0;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 40 + 1) * grade : (equip.getReqLevel() / 40 + 1) * grade);
                    code = 1;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 40 + 1) * grade : (equip.getReqLevel() / 40 + 1) * grade);
                    break;
                }
                case 5: {
                    code = 0;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 40 + 1) * grade : (equip.getReqLevel() / 40 + 1) * grade);
                    code = 2;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 40 + 1) * grade : (equip.getReqLevel() / 40 + 1) * grade);
                    break;
                }
                case 6: {
                    code = 0;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 40 + 1) * grade : (equip.getReqLevel() / 40 + 1) * grade);
                    code = 3;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 40 + 1) * grade : (equip.getReqLevel() / 40 + 1) * grade);
                    break;
                }
                case 7: {
                    code = 2;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 40 + 1) * grade : (equip.getReqLevel() / 40 + 1) * grade);
                    code = 1;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 40 + 1) * grade : (equip.getReqLevel() / 40 + 1) * grade);
                    break;
                }
                case 8: {
                    code = 3;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 40 + 1) * grade : (equip.getReqLevel() / 40 + 1) * grade);
                    code = 1;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 40 + 1) * grade : (equip.getReqLevel() / 40 + 1) * grade);
                    break;
                }
                case 9: {
                    code = 2;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 40 + 1) * grade : (equip.getReqLevel() / 40 + 1) * grade);
                    code = 3;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (equip.getReqLevel() / 40 + 1) * grade : (equip.getReqLevel() / 40 + 1) * grade);
                    break;
                }
                case 10: {
                    code = 10;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + Math.max(3, equip.getReqLevel() / 10 * 30) * grade : Math.max(3, equip.getReqLevel() / 10 * 30) * grade);
                    break;
                }
                case 11: {
                    code = 11;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + Math.max(3, equip.getReqLevel() / 10 * 30) * grade : Math.max(3, equip.getReqLevel() / 10 * 30) * grade);
                    break;
                }
                case 12: {
                    code = 12;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + grade * 5 : grade * 5);
                    break;
                }
                case 16: {
                    code = 19;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + grade : grade);
                    break;
                }
                case 17: {
                    code = 20;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + grade : grade);
                    break;
                }
                case 19: {
                    code = 23;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + grade : grade);
                    break;
                }
                case 20: {
                    code = 24;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + grade : grade);
                    break;
                }
                case 18: {
                    code = 21;
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + grade * 2 : grade * 2);
                    break;
                }
                case 14: {
                    double rate;
                    int level;
                    short base;
                    code = 17;
                    if (ItemConstants.類型.武器(equip.getItemId())) {
                        base = EquipExFlag.getByType(statFlag * 10) == EquipExFlag.FLAGEx_iPAD ? equip.getPad() : equip.getMad();
                        level = equip.getReqLevel() / 40 + 1;
                        rate = MapleItemInformationProvider.getInstance().isBossReward(equip.getItemId()) ? SPECIAL_RATES[grade - 1] : NORMAL_RATES[grade - 1];
                        statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (int)Math.ceil((double)(base * level) * rate / 100.0) : (int)Math.ceil((double)(base * level) * rate / 100.0));
                        break;
                    }
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + grade : grade);
                    break;
                }
                case 15: {
                    double rate;
                    int level;
                    short base;
                    code = 18;
                    if (ItemConstants.類型.武器(equip.getItemId())) {
                        base = EquipExFlag.getByType(statFlag * 10) == EquipExFlag.FLAGEx_iPAD ? equip.getPad() : equip.getMad();
                        level = equip.getReqLevel() / 40 + 1;
                        rate = MapleItemInformationProvider.getInstance().isBossReward(equip.getItemId()) ? SPECIAL_RATES[grade - 1] : NORMAL_RATES[grade - 1];
                        statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + (int)Math.ceil((double)(base * level) * rate / 100.0) : (int)Math.ceil((double)(base * level) * rate / 100.0));
                        break;
                    }
                    statProps.put(code, statProps.get(code) != null ? (Integer)statProps.get(code) + grade : grade);
                }
            }
        } while ((lFlag /= 1000L) > 0L);
        return statProps;
    }

    public static enum EquipExFlag {
        FLAGEx_iSTR(0),
        FLAGEx_iDEX(10),
        FLAGEx_iINT(20),
        FLAGEx_iLUK(30),
        FLAGEx_iSTR_DEX(40),
        FLAGEx_iSTR_INT(50),
        FLAGEx_iSTR_LUK(60),
        FLAGEx_iDEX_INT(70),
        FLAGEx_iDEX_LUK(80),
        FLAGEx_iINT_LUK(90),
        FLAGEx_iMAXHP(100),
        FLAGEx_iMAXMP(110),
        FLAGEx_iREQLEVEL(120),
        FLAGEx_iPDD(130),
        FLAGEx_iPAD(170),
        FLAGEx_iMAD(180),
        FLAGEx_iSPEED(190),
        FLAGEx_iJUMP(200),
        FLAGEx_iBDR(210),
        FLAGEx_iDAMR(230),
        FLAGEx_iSTATR(240);

        private final int type;
        private static final Map<Integer, EquipExFlag> Flags;

        private EquipExFlag(int type) {
            this.type = type;
        }

        public static EquipExFlag getByType(int n) {
            return Flags.get(n);
        }

        public final int getType() {
            return this.type;
        }

        static {
            Flags = new HashMap<Integer, EquipExFlag>();
            for (EquipExFlag flag : EquipExFlag.values()) {
                Flags.put(flag.type, flag);
            }
        }
    }
}

