/*
 * Decompiled with CFR 0.152.
 */
package Client.stat;

import Client.MapleCharacter;
import Client.MapleTraitType;
import Client.MonsterFamiliar;
import Client.SecondaryStat;
import Client.inventory.EnhanceResultType;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.MapleInventory;
import Client.inventory.MapleInventoryType;
import Client.inventory.MapleWeapon;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.stat.RecalcableStats;
import Config.constants.GameConstants;
import Config.constants.ItemConstants;
import Config.constants.JobConstants;
import Config.constants.SkillConstants;
import Net.server.MapleItemInformationProvider;
import Net.server.MapleStatInfo;
import Net.server.StructItemOption;
import Net.server.StructSetItem;
import Net.server.StructSetItemStat;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.Element;
import Net.server.quest.MapleQuest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import tools.Pair;

public class EquipRecalcableStats
extends RecalcableStats {
    private static final int[] allJobs = new int[]{0, 10000000, 20000000, 20010000, 20020000, 20030000, 20040000, 20050000, 30000000, 30010000, 30020000, 40010000, 40020000, 50000000, 60000000, 60010000, 60020000, 100000000, 110000000, 130000000, 140000000, 150000000, 150010000};
    final List<Equip> durabilityHandling = new ArrayList<Equip>();
    final List<Equip> equipLevelHandling = new ArrayList<Equip>();
    final List<Equip> sealedEquipHandling = new ArrayList<Equip>();
    final Map<Integer, Pair<Integer, Integer>> hpRecover_itemOption = new HashMap<Integer, Pair<Integer, Integer>>();
    final Map<Integer, Pair<Integer, Integer>> mpRecover_itemOption = new HashMap<Integer, Pair<Integer, Integer>>();
    final List<Integer> equipSummons = new ArrayList<Integer>();
    final Map<Integer, Integer> skillsIncrement = new HashMap<Integer, Integer>();
    final Map<Integer, Integer> equipmentSkills = new HashMap<Integer, Integer>();
    final Map<Integer, Pair<Integer, Integer>> ignoreDAM = new HashMap<Integer, Pair<Integer, Integer>>();
    final Map<Integer, Pair<Integer, Integer>> ignoreDAMr = new HashMap<Integer, Pair<Integer, Integer>>();
    final Map<Integer, Pair<Integer, Integer>> DAMreflect = new HashMap<Integer, Pair<Integer, Integer>>();
    final EnumMap<Element, Integer> elemBoosts = new EnumMap(Element.class);
    int localmaxhp_;
    int localmaxmp_;
    int recallRingId;
    int element_def;
    int element_ice;
    int element_fire;
    int element_light;
    int element_psn;
    int passivePlus;
    MapleWeapon wt;
    int harvestingTool;
    boolean canFish;
    boolean canFishVIP;
    int starForce;
    int arc;
    int aut;
    int levelBonus;
    int questBonus;
    int expCardRate;
    int dropCardRate;
    int weaponAttack;

    @Override
    void resetLocalStats() {
        super.resetLocalStats();
        this.durabilityHandling.clear();
        this.equipLevelHandling.clear();
        this.sealedEquipHandling.clear();
        this.hpRecover_itemOption.clear();
        this.mpRecover_itemOption.clear();
        this.equipSummons.clear();
        this.skillsIncrement.clear();
        this.equipmentSkills.clear();
        this.elemBoosts.clear();
        this.DAMreflect.clear();
        this.ignoreDAMr.clear();
        this.ignoreDAM.clear();
        this.wt = MapleWeapon.沒有武器;
        this.element_fire = 100;
        this.element_ice = 100;
        this.element_light = 100;
        this.element_psn = 100;
        this.element_def = 100;
        this.passivePlus = 0;
        this.harvestingTool = 0;
        this.canFish = false;
        this.canFishVIP = false;
        this.recallRingId = 0;
        this.starForce = 0;
        this.arc = 0;
        this.questBonus = 1;
        this.levelBonus = 0;
        this.dropCardRate = 100;
        this.expCardRate = 100;
        this.weaponAttack = 0;
        this.localmaxhp_ = 0;
        this.localmaxmp_ = 0;
    }

    @Override
    void recalcLocalStats(boolean firstLogin, MapleCharacter player) {
        MapleStatEffect itemEffect;
        MapleInventory iv;
        int activeNickItemID;
        StructItemOption soc;
        this.resetLocalStats();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        ArrayList<Integer> jokerToSetItems = new ArrayList<Integer>();
        HashMap<Integer, List> setHandling = new HashMap<Integer, List>();
        Equip weapon = (Equip)player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(JobConstants.is神之子(player.getJob()) && player.isBeta() ? -10 : -11));
        Equip shield = (Equip)player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10);
        if (weapon == null) {
            this.wt = MapleWeapon.沒有武器;
        } else {
            this.weaponId = weapon.getItemId();
            this.wt = MapleWeapon.getByItemID(weapon.getItemId());
            if (this.weaponId == 1402224) {
                ++this.incAttackCount;
            }
            this.weaponAttack = ItemConstants.類型.魔法武器(this.weaponId) ? (int)weapon.getTotalMad() : (int)weapon.getTotalPad();
        }
        boolean wuxing = JobConstants.is陰陽師(player.getJob()) && player.getSkillLevel(40020000) > 0;
        boolean blood = JobConstants.is惡魔復仇者(player.getJob()) && player.getSkillLevel(30010242) > 0;
        HashMap<Integer, SkillEntry> sData = new HashMap<Integer, SkillEntry>();
        for (Item item1 : player.getInventory(MapleInventoryType.EQUIPPED).newList()) {
            Integer n;
            Map<String, Integer> eqstat;
            Equip equip = (Equip)item1;
            int itemId = equip.getItemId();
            if (equip.getPosition() == -5200 || equip.getPosition() == -32 && player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-33) == null || equip.getPosition() == -33 && player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-32) == null || player.getLevel() < equip.getTotalReqLevel() || EnhanceResultType.EQUIP_MARK.check(equip.getEnchantBuff())) continue;
            if (player.getBuffStatValueHolder(SecondaryStat.DispelItemOption) == null) {
                Pair<Integer, Integer> ix = this.handleEquipAdditions(ii, player, firstLogin, sData, itemId);
                if (ix != null) {
                    this.localmaxhp_ += ix.getLeft().intValue();
                    this.localmaxmp_ += ix.getRight().intValue();
                }
                int[] potentials = new int[]{0, 0, 0, 0, 0, 0, equip.getSoulOption()};
                if (equip.getState(false) >= 17) {
                    potentials[0] = equip.getPotential(1, false);
                    potentials[1] = equip.getPotential(2, false);
                    potentials[2] = equip.getPotential(3, false);
                }
                if (equip.getState(true) >= 17) {
                    potentials[3] = equip.getPotential(1, true);
                    potentials[4] = equip.getPotential(2, true);
                    potentials[5] = equip.getPotential(3, true);
                }
                for (int i : potentials) {
                    if (i <= 0) continue;
                    int itemReqLevel = ii.getReqLevel(itemId);
                    List<StructItemOption> potentialInfo = ii.getPotentialInfo(i);
                    soc = potentialInfo.get(Math.max(0, Math.min(potentialInfo.size() - 1, (itemReqLevel - 1) / 10)));
                    if (soc == null) continue;
                    this.localmaxhp_ += soc.get("incMHP");
                    if (wuxing) {
                        this.localmaxhp_ += soc.get("incMHP");
                    } else {
                        this.localmaxmp_ += soc.get("incMMP");
                    }
                    this.handleItemOption(itemId, soc, player, firstLogin, sData);
                }
                if (equip.getSocketState() >= 19) {
                    int[] nArray;
                    int[] nArray2 = nArray = new int[]{equip.getSocket1(), equip.getSocket2(), equip.getSocket3()};
                    int n3 = nArray2.length;
                    for (int i = 0; i < n3; ++i) {
                        int i2 = nArray2[i];
                        if (i2 <= 0 || (soc = ii.getSocketInfo(i2)) == null) continue;
                        this.localmaxhp_ += soc.get("incMHP");
                        if (wuxing) {
                            this.localmaxhp_ += soc.get("incMHP");
                        } else {
                            this.localmaxmp_ += soc.get("incMMP");
                        }
                        this.handleItemOption(itemId, soc, player, firstLogin, sData);
                    }
                }
                if (equip.getDurability() > 0) {
                    this.durabilityHandling.add(equip);
                }
            }
            if (JobConstants.is神之子(player.getJob()) && (player.isBeta() && equip.getPosition() == -11 || !player.isBeta() && equip.getPosition() == -10)) continue;
            if (itemId / 10000 == 171) {
                this.arc += equip.getARC();
                this.aut += equip.getAut();
                this.indieStrFX += equip.getTotalStr();
                this.indieDexFX += equip.getTotalDex();
                this.indieIntFX += equip.getTotalInt();
                this.indieLukFX += equip.getTotalLuk();
                this.indieMhpFX += equip.getTotalHp();
                this.pad += equip.getTotalPad();
                this.mad += equip.getTotalMad();
                this.wdef += equip.getTotalPdd();
                continue;
            }
            if (equip.getPosition() == -11 && ItemConstants.類型.魔法武器(itemId) && (eqstat = ii.getItemBaseInfo(itemId)) != null) {
                if (eqstat.containsKey("incRMAF")) {
                    this.element_fire = eqstat.get("incRMAF");
                }
                if (eqstat.containsKey("incRMAI")) {
                    this.element_ice = eqstat.get("incRMAI");
                }
                if (eqstat.containsKey("incRMAL")) {
                    this.element_light = eqstat.get("incRMAL");
                }
                if (eqstat.containsKey("incRMAS")) {
                    this.element_psn = eqstat.get("incRMAS");
                }
                if (eqstat.containsKey("elemDefault")) {
                    this.element_def = eqstat.get("elemDefault");
                }
            }
            this.localmaxhp_ = blood ? (this.localmaxhp_ += equip.getTotalHp() / 2) : (this.localmaxhp_ += equip.getTotalHp());
            if (wuxing) {
                this.localmaxhp_ += equip.getTotalMp();
            } else {
                this.localmaxmp_ += equip.getTotalMp();
            }
            if (ItemConstants.類型.機器人(itemId) && equip.getAndroid() != null && player.getAndroid() == null) {
                player.setAndroid(equip.getAndroid());
            }
            player.getTrait(MapleTraitType.craft).addLocalExp(equip.getTotalHands());
            this.starForce += equip.getStarForceLevel();
            this.localdex += equip.getTotalDex();
            this.localint += equip.getTotalInt();
            this.localstr += equip.getTotalStr();
            this.localluk += equip.getTotalLuk();
            this.mad += equip.getTotalMad();
            this.pad += equip.getTotalPad();
            this.wdef += equip.getTotalPdd();
            this.speed += equip.getTotalSpeed();
            this.jump += equip.getTotalJump();
            this.pvpDamage += equip.getPVPDamage();
            this.bossDamageR += equip.getTotalBossDamage();
            this.addIgnoreMobpdpR(equip.getTotalIgnorePDR());
            this.incDamR += (double)equip.getTotalTotalDamage();
            short allstat = equip.getTotalAllStat();
            if (allstat > 0) {
                this.incStrR += allstat;
                this.incDexR += allstat;
                this.incIntR += allstat;
                this.incLukR += allstat;
            }
            if (itemId / 1000 == 1099) {
                this.incMaxDF += equip.getTotalMp();
            }
            switch (itemId) {
                case 1112127: 
                case 1112917: 
                case 1112918: {
                    this.recallRingId = itemId;
                    break;
                }
                default: {
                    List<Integer> bonusExps = ii.getBonusExps(itemId);
                    if (bonusExps == null || bonusExps.isEmpty()) break;
                    this.equipmentBonusExps.put(Math.abs(equip.getPosition()), bonusExps);
                }
            }
            this.incMaxHPR += ii.getItemIncMHPr(itemId);
            if (wuxing) {
                this.incMaxHPR += ii.getItemIncMMPr(itemId);
            } else {
                this.incMaxMPR += ii.getItemIncMMPr(itemId);
            }
            int summonid = ItemConstants.getEquipSummon(itemId);
            if (summonid > 0) {
                this.equipSummons.add(summonid);
            }
            if (ii.isEpicItem(itemId) || ii.isJokerToSetItem(itemId)) {
                jokerToSetItems.add(itemId);
            }
            if ((n = ii.getSetItemID(itemId)) != null && n > 0) {
                setHandling.computeIfAbsent(n, k -> new ArrayList()).add(itemId);
            }
            if (equip.getIncSkill() > 0 && ii.getEquipSkills(itemId) != null) {
                for (int skillId : ii.getEquipSkills(itemId)) {
                    Skill skil;
                    if (equip.getIncSkill() != skillId || (skil = SkillFactory.getSkill(skillId)) == null) continue;
                    this.skillsIncrement.merge(skil.getId(), 1, (a, b) -> a + b);
                }
            }
            for (Pair<Integer, Integer> skillEntry : ii.getEquipmentSkills(itemId)) {
                Skill skil = SkillFactory.getSkill(skillEntry.getLeft());
                int value = ii.getEquipmentSkillsFixLevel(itemId);
                if (skil == null || value <= 0) continue;
                this.equipmentSkills.merge(skil.getId(), value, (a, b) -> Math.min(a + b, (Integer)skillEntry.getRight()));
            }
            if (ItemConstants.getMaxLevel(itemId) > 0 && (GameConstants.getStatFromWeapon(itemId) == null ? equip.getEquipLevel() <= ItemConstants.getMaxLevel(itemId) : equip.getEquipLevel() < ItemConstants.getMaxLevel(itemId))) {
                this.equipLevelHandling.add(equip);
            }
            if (!equip.isSealedEquip()) continue;
            this.sealedEquipHandling.add(equip);
        }
        switch (this.wt) {
            case 弓: {
                Item arrowSlot = player.getInventory(MapleInventoryType.USE).getArrowSlot(player.getLevel());
                if (arrowSlot == null || player.getBuffedValue(SecondaryStat.SoulArrow) != null) break;
                this.pad += ii.getWatkForProjectile(arrowSlot.getItemId());
                break;
            }
            case 弩: {
                Item crossbowSlot = player.getInventory(MapleInventoryType.USE).getCrossbowSlot(player.getLevel());
                if (crossbowSlot == null || player.getBuffedValue(SecondaryStat.SoulArrow) != null) break;
                this.pad += ii.getWatkForProjectile(crossbowSlot.getItemId());
                break;
            }
            case 拳套: {
                Item dartsSlot = player.getInventory(MapleInventoryType.USE).getDartsSlot(player.getLevel());
                if (dartsSlot == null) break;
                this.pad += ii.getWatkForProjectile(dartsSlot.getItemId());
                break;
            }
            case 火槍: {
                Item bulletSlot = player.getInventory(MapleInventoryType.USE).getBulletSlot(player.getLevel());
                if (bulletSlot == null) break;
                this.pad += ii.getWatkForProjectile(bulletSlot.getItemId());
                break;
            }
        }
        if (player.getSummonedFamiliar() != null) {
            MonsterFamiliar summonedFamiliar = player.getSummonedFamiliar();
            for (int i = 0; i < 3; ++i) {
                int option = summonedFamiliar.getOption(i);
                if (option <= 0 || (soc = ii.getFamiliar_option().get(option).get(Math.max(summonedFamiliar.getGrade(), 0))) == null) continue;
                this.localmaxhp_ += soc.get("incMHP");
                if (wuxing) {
                    this.localmaxhp_ += soc.get("incMMP");
                } else {
                    this.localmaxmp_ += soc.get("incMMP");
                }
                this.handleItemOption(summonedFamiliar.getFamiliar(), soc, player, firstLogin, sData);
            }
        }
        if (!((activeNickItemID = player.getActiveNickItemID()) <= 0 || (iv = player.getInventory(MapleInventoryType.SETUP)) == null || iv.findById(activeNickItemID) == null || ii.isNickSkillTimeLimited(activeNickItemID) && iv.findById(activeNickItemID).getExpiration() <= System.currentTimeMillis() || (itemEffect = ii.getNickItemEffect(player.getActiveNickItemID())) == null || itemEffect.getInfo() == null)) {
            this.addmaxhp += itemEffect.getInfo().getOrDefault((Object)MapleStatInfo.mhpX, 0).intValue();
            this.addmaxmp += itemEffect.getInfo().getOrDefault((Object)MapleStatInfo.mmpX, 0).intValue();
        }
        player.getStat().handleProfessionTool(player);
        if (JobConstants.is神之子(player.getJob())) {
            String data = player.getQuestNAdd(MapleQuest.getInstance(41907)).getCustomData();
            if (this.wt != MapleWeapon.沒有武器 && data != null && !data.equals("0")) {
                setHandling.computeIfAbsent(Integer.valueOf(data), k -> new ArrayList()).add(this.weaponId);
            }
        }
        TreeMap<Integer, Integer> treeMap = new TreeMap<Integer, Integer>((n1, n2) -> n2.compareTo((Integer)n1));
        for (Map.Entry entry : setHandling.entrySet()) {
            StructSetItem ssi = ii.getSetItem((Integer)entry.getKey());
            if (ssi == null) continue;
            int reqlevel = 0;
            for (int id : ssi.itemIDs) {
                reqlevel = Math.max(ii.getReqLevel(id), reqlevel);
            }
            treeMap.put((Integer)entry.getKey(), reqlevel);
        }
        List<Map.Entry<Integer, Integer>> list1 = new ArrayList<>(treeMap.entrySet());
        list1.sort((e1, e2) -> ((Integer)e2.getValue()).compareTo((Integer)e1.getValue()));
        Iterator entry = jokerToSetItems.iterator();
        while (entry.hasNext()) {
            int id = (Integer)entry.next();
            for (Map.Entry entry2 : list1) {
                int setId2 = (Integer)entry2.getKey();
                ArrayList<Integer> list2 = new ArrayList<Integer>((Collection)setHandling.get(setId2));
                boolean b2 = false;
                if (list2.size() >= 3) {
                    Iterator value = ((List)setHandling.get(setId2)).iterator();
                    block26: while (value.hasNext()) {
                        int id3 = (Integer)value.next();
                        StructSetItem ssi = ii.getSetItem(setId2);
                        if (ssi == null || b2) continue;
                        for (int id4 : ssi.itemIDs) {
                            if (id3 == id4 || id3 == id || id4 / 10000 != id / 10000) continue;
                            list2.add(id);
                            b2 = true;
                            continue block26;
                        }
                    }
                }
                setHandling.put(setId2, list2);
            }
        }
        list1.clear();
        ArrayList<Integer> list2 = new ArrayList<Integer>();
        for (Map.Entry setHandlingEntry : setHandling.entrySet()) {
            int n = (Integer)setHandlingEntry.getKey();
            List itemids = (List)setHandlingEntry.getValue();
            StructSetItem ssi = ii.getSetItem(n);
            if (ssi == null) continue;
            if (itemids.size() >= ssi.completeCount) {
                list2.add(n);
            }
            for (Map.Entry<Integer, StructSetItemStat> entry3 : new LinkedHashMap<Integer, StructSetItemStat>(ssi.setItemStat).entrySet()) {
                StructSetItemStat stat = entry3.getValue();
                if (entry3.getKey() > itemids.size()) continue;
                this.localstr += stat.incSTR + stat.incAllStat;
                this.localdex += stat.incDEX + stat.incAllStat;
                this.localint += stat.incINT + stat.incAllStat;
                this.localluk += stat.incLUK + stat.incAllStat;
                this.pad += stat.incPAD;
                this.mad += stat.incMAD;
                this.speed += stat.incSpeed;
                this.localmaxhp_ += stat.incMHP;
                this.incMaxHPR += stat.incMHPr;
                if (wuxing) {
                    this.localmaxhp_ += stat.incMMP;
                    this.incMaxHPR += stat.incMMPr;
                } else {
                    this.localmaxmp_ += stat.incMMP;
                    this.incMaxMPR += stat.incMMPr;
                }
                this.wdef += stat.incPDD;
                if (stat.skillId > 0 && stat.skillLevel > 0) {
                    sData.put(stat.skillId, new SkillEntry(stat.skillLevel, 0, -1L, 0, 0, (byte)-1));
                }
                if (stat.option1 > 0 && stat.option1Level > 0 && (soc = ii.getPotentialInfo(stat.option1).get(stat.option1Level)) != null) {
                    this.localmaxhp_ += soc.get("incMHP");
                    if (wuxing) {
                        this.localmaxhp_ += soc.get("incMMP");
                    } else {
                        this.localmaxmp_ += soc.get("incMMP");
                    }
                    this.handleItemOption(entry3.getKey(), soc, player, firstLogin, sData);
                }
                if (stat.option2 <= 0 || stat.option2Level <= 0 || (soc = ii.getPotentialInfo(stat.option2).get(stat.option2Level)) == null) continue;
                this.localmaxhp_ += soc.get("incMHP");
                if (wuxing) {
                    this.localmaxhp_ += soc.get("incMMP");
                } else {
                    this.localmaxmp_ += soc.get("incMMP");
                }
                this.handleItemOption(entry3.getKey(), soc, player, firstLogin, sData);
            }
        }
        list2.clear();
        int extraExpRate = 100;
        block30: for (Item item : player.getInventory(MapleInventoryType.CASH).newList()) {
            if (item.getItemId() / 10000 == 521) {
                int rate = ii.getExpCardRate(item.getItemId());
                if (item.getItemId() == 5210009 || rate <= 0) continue;
                if (item.getItemId() / 1000 != 5212 && !ii.isExpOrDropCardTime(item.getItemId()) || player.getLevel() < ii.getExpCardMinLevel(item.getItemId()) || player.getLevel() > ii.getExpCardMaxLevel(item.getItemId()) || item.getExpiration() <= System.currentTimeMillis()) {
                    if (item.getExpiration() != -1L) continue;
                    player.dropMessage(5, ii.getName(item.getItemId()) + "屬性錯誤，經驗值加成無效。");
                    if (!player.isIntern()) continue;
                }
                switch (item.getItemId()) {
                    case 5212000: 
                    case 5212001: 
                    case 5212002: 
                    case 5212003: 
                    case 5212004: 
                    case 5212005: 
                    case 5212006: 
                    case 5212007: 
                    case 5212008: {
                        extraExpRate = (int)((double)extraExpRate * ((double)rate / 100.0));
                        break;
                    }
                    default: {
                        if (this.expCardRate >= rate) continue block30;
                        this.expCardRate = rate;
                    }
                }
                continue;
            }
            if (this.dropCardRate == 100 && item.getItemId() / 10000 == 536) {
                if (item.getItemId() < 5360000 || item.getItemId() >= 5360100) continue;
                if (!ii.isExpOrDropCardTime(item.getItemId()) || item.getExpiration() <= System.currentTimeMillis()) {
                    if (item.getExpiration() != -1L) continue;
                    player.dropMessage(5, ii.getName(item.getItemId()) + "屬性錯誤，掉寶機率加成無效。");
                    if (!player.isIntern()) continue;
                }
                this.dropCardRate = 200;
                continue;
            }
            if (item.getItemId() == 5590001) {
                this.levelBonus = 10;
                continue;
            }
            if (this.levelBonus == 0 && item.getItemId() == 5590000) {
                this.levelBonus = 5;
                continue;
            }
            if (item.getItemId() == 5710000) {
                this.questBonus = 2;
                continue;
            }
            if (item.getItemId() == 5340000) {
                this.canFish = true;
                continue;
            }
            if (item.getItemId() != 5340001) continue;
            this.canFish = true;
            this.canFishVIP = true;
        }
        this.expCardRate = Math.max(extraExpRate, this.expCardRate);
        for (Item item : player.getInventory(MapleInventoryType.ETC).list()) {
            switch (item.getItemId()) {
                case 4030003: {
                    break;
                }
                case 4030004: {
                    break;
                }
            }
        }
        if (firstLogin && player.getLevel() >= 30) {
            int[] skills;
            for (int skillId : skills = new int[]{1085, 1087, 1179}) {
                for (int allJob : allJobs) {
                    if (JobConstants.getBeginner(player.getJob()) == allJob || player.getSkillEntry(skillId + allJob) == null) continue;
                    sData.put(skillId + allJob, new SkillEntry(0, 0, -1L));
                }
                sData.put(SkillConstants.getSkillByJob(skillId, player.getJob()), new SkillEntry(-1, 0, -1L));
            }
        }
        player.changeSkillLevel_Skip(sData, false);
    }

    private Pair<Integer, Integer> handleEquipAdditions(MapleItemInformationProvider ii, MapleCharacter chra, boolean first_login, Map<Integer, SkillEntry> sData, int itemId) {
        Map<String, ?> additions = ii.getEquipAdditions(itemId);
        if (additions == null) {
            return null;
        }
        int localmaxhp_x = 0;
        int localmaxmp_x = 0;
        int skillid = 0;
        int skilllevel = 0;
        for (Map.Entry add : additions.entrySet()) {
            switch ((String)add.getKey()) {
                case "elemboost": {
                    String craft = null;
                    String elemVol = null;
                    try {
                        Object craftObj = ((Map)((Map)add.getValue()).get("con")).get("craft");
                        craft = craftObj instanceof String ? (String)craftObj : (craftObj == null ? null : craftObj.toString());
                        elemVol = (String)((Map)add.getValue()).get("elemVol");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (elemVol == null || craft != null && chra.getTrait(MapleTraitType.craft).getLocalTotalExp() < Integer.parseInt(craft)) break;
                    int value = Integer.parseInt(elemVol.substring(1));
                    Element key = Element.getFromChar(elemVol.charAt(0));
                    if (this.elemBoosts.get((Object)key) != null) {
                        value += this.elemBoosts.get((Object)key).intValue();
                    }
                    this.elemBoosts.put(key, value);
                    break;
                }
                case "mobcategory": {
                    Integer damage = (Integer)((Map)add.getValue()).get("damage");
                    if (damage == null) break;
                    this.incDamR += (double)damage.intValue();
                    break;
                }
                case "critical": {
                    boolean canJob = true;
                    boolean canLevel = true;
                    int prob = 0;
                    int damage = 0;
                    if (add.getValue() instanceof Map<?, ?>) {
                        for (Map.Entry<String, ?> entry : ((Map<String, ?>) add.getValue()).entrySet()) {
                            switch ((String)entry.getKey()) {
                                case "con": {
                                    Map subentry = (Map)entry.getValue();
                                    if (subentry.containsKey("job")) {
                                        canJob = subentry.containsValue(chra.getJob());
                                        break;
                                    }
                                    if (!subentry.containsKey("lv")) break;
                                    canLevel = chra.getLevel() >= (Integer)subentry.get("lv");
                                    break;
                                }
                                case "prob": {
                                    prob = (Integer)entry.getValue();
                                    break;
                                }
                                case "damage": {
                                    try {
                                        damage = Integer.parseInt(entry.getValue().toString());
                                        break;
                                    }
                                    catch (ClassCastException e) {
                                        log.error("讀取damage錯誤, Itemid: " + itemId, e);
                                    }
                                }
                            }
                        }
                    }
                    if (!canJob || !canLevel) break;
                    this.critRate = (short)(this.critRate + prob);
                    this.criticalDamage = (short)(this.criticalDamage + damage);
                    break;
                }
                case "boss": {
                    break;
                }
                case "mobdie": 
                case "skill": {
                    if (!(add.getValue() instanceof Map)) break;
                    try {
                        Map v = (Map)add.getValue();
                        if (((Map)add.getValue()).containsKey("con")) {
                            String craft = (String)((Map)((Map)add.getValue()).get("con")).get("craft");
                            if (chra.getTrait(MapleTraitType.craft).getLocalTotalExp() < Integer.parseInt(craft)) break;
                        }
                        if (((Map)add.getValue()).containsKey("id")) {
                            String id = (String)((Map)add.getValue()).get("id");
                            skillid = Integer.parseInt(id);
                        }
                        if (!((Map)add.getValue()).containsKey("level")) break;
                        String level = (String)((Map)add.getValue()).get("level");
                        skilllevel = Integer.parseInt(level);
                    }
                    catch (Exception exception) {}
                    break;
                }
            }
        }
        if (skillid != 0 && skilllevel != 0) {
            sData.put(skillid, new SkillEntry((byte)skilllevel, 0, -1L));
        }
        return new Pair<Integer, Integer>(localmaxhp_x, localmaxmp_x);
    }

    private void handleItemOption(int sourceid, StructItemOption soc, MapleCharacter chra, boolean first_login, Map<Integer, SkillEntry> sData) {
        this.localstr += soc.get("incSTR");
        this.localdex += soc.get("incDEX");
        this.localint += soc.get("incINT");
        this.localluk += soc.get("incLUK");
        if (soc.get("incSTRlv") > 0) {
            this.localstr += chra.getLevel() / 10 * soc.get("incSTRlv");
        }
        if (soc.get("incDEXlv") > 0) {
            this.localdex += chra.getLevel() / 10 * soc.get("incDEXlv");
        }
        if (soc.get("incINTlv") > 0) {
            this.localint += chra.getLevel() / 10 * soc.get("incINTlv");
        }
        if (soc.get("incLUKlv") > 0) {
            this.localluk += chra.getLevel() / 10 * soc.get("incLUKlv");
        }
        this.speed += soc.get("incSpeed");
        this.jump += soc.get("incJump");
        this.pad += soc.get("incPAD");
        if (soc.get("incPADlv") > 0) {
            this.pad += chra.getLevel() / 10 * soc.get("incPADlv");
        }
        this.mad += soc.get("incMAD");
        if (soc.get("incMADlv") > 0) {
            this.mad += chra.getLevel() / 10 * soc.get("incMADlv");
        }
        this.wdef += soc.get("incPDD");
        this.incStrR += soc.get("incSTRr");
        this.incDexR += soc.get("incDEXr");
        this.incIntR += soc.get("incINTr");
        this.incLukR += soc.get("incLUKr");
        this.incMaxHPR += soc.get("incMHPr");
        this.incMaxMPR += soc.get("incMMPr");
        this.incPadR += soc.get("incPADr");
        this.incMadR += soc.get("incMADr");
        this.percent_wdef += soc.get("incPDDr");
        this.critRate = (short)(this.critRate + soc.get("incCr"));
        if (soc.get("boss") > 0) {
            this.bossDamageR += soc.get("incDAMr");
        } else {
            this.incDamR += (double)soc.get("incDAMr");
        }
        this.recoverHP += soc.get("RecoveryHP");
        this.recoverMP += soc.get("RecoveryMP");
        if (soc.get("HP") > 0) {
            this.hpRecover_itemOption.put(sourceid, new Pair<Integer, Integer>(soc.get("HP"), soc.get("prop")));
        }
        if (soc.get("MP") > 0 && !JobConstants.isNotMpJob(chra.getJob())) {
            this.mpRecover_itemOption.put(sourceid, new Pair<Integer, Integer>(soc.get("MP"), soc.get("prop")));
        }
        this.addIgnoreMobpdpR(soc.get("ignoreTargetDEF"));
        if (soc.get("ignoreDAM") > 0) {
            this.ignoreDAM.put(sourceid, new Pair<Integer, Integer>(soc.get("ignoreDAM"), soc.get("prop")));
        }
        this.incAllskill += soc.get("incAllskill");
        if (soc.get("ignoreDAMr") > 0) {
            this.ignoreDAMr.put(sourceid, new Pair<Integer, Integer>(soc.get("ignoreDAMr"), soc.get("prop")));
        }
        this.skillRecoveryUP += soc.get("RecoveryUP");
        this.itemRecoveryUP += soc.get("RecoveryUP");
        this.criticalDamage = (short)(this.criticalDamage + soc.get("incCriticaldamageMin"));
        this.criticalDamage = (short)(this.criticalDamage + soc.get("incCriticaldamageMax"));
        this.terR += soc.get("incTerR");
        this.asr += soc.get("incAsrR");
        if (soc.get("DAMreflect") > 0) {
            this.DAMreflect.put(sourceid, new Pair<Integer, Integer>(soc.get("DAMreflect"), soc.get("prop")));
        }
        this.mpconReduce += soc.get("mpconReduce");
        this.reduceCooltime += soc.get("reduceCooltime");
        this.incMesoProp += soc.get("incMesoProp");
        this.incRewardProp += (double)soc.get("incRewardProp") / 100.0;
        this.passivePlus += soc.get("passivePlus");
        this.incBuffTime += soc.get("bufftimeR");
        if (soc.get("skillID") > 0) {
            sData.put(SkillConstants.getSkillByJob(soc.get("skillID"), chra.getJob()), new SkillEntry(1, 0, -1L));
        }
    }

    private void addIgnoreMobpdpR(int val) {
        this.ignoreMobpdpR += (100.0 - this.ignoreMobpdpR) * ((double)val / 100.0);
    }
}

