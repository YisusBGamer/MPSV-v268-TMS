/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.末日反抗軍;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.skills.ExtraSkill;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Packet.MaplePacketCreator;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tools.data.MaplePacketReader;

public class 爆拳槍神
extends AbstractSkillHandler {
    public 爆拳槍神() {
        this.jobs = new MapleJob[]{MapleJob.爆拳槍神1轉, MapleJob.爆拳槍神2轉, MapleJob.爆拳槍神3轉, MapleJob.爆拳槍神4轉};
        for (Field field : Config.constants.skills.爆拳槍神.class.getDeclaredFields()) {
            try {
                this.skills.add(field.getInt(field.getName()));
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int baseSkills(MapleCharacter chr, SkillClassApplier applier) {
        int[] ss;
        super.baseSkills(chr, applier);
        for (int i : ss = new int[]{37001001, 37000010}) {
            Skill skil = SkillFactory.getSkill(i);
            if (chr.getLevel() < i / 10000 || skil == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 37120055: 
            case 37120056: 
            case 37120057: 
            case 37120058: 
            case 37120059: {
                return 37121052;
            }
            case 37000008: 
            case 37000011: 
            case 37000012: 
            case 37000013: {
                return 37001002;
            }
            case 37100002: {
                return 37101001;
            }
            case 37110004: {
                return 37111003;
            }
            case 37110001: 
            case 37110002: {
                return 37111000;
            }
            case 37120014: 
            case 37120015: 
            case 37120016: 
            case 37120017: 
            case 37120018: 
            case 37120019: 
            case 37120023: {
                return 37121004;
            }
            case 37120022: 
            case 37120024: {
                return 37121003;
            }
            case 37100008: {
                return 37101000;
            }
            case 37000009: {
                return 37001000;
            }
            case 37110011: {
                return 37111005;
            }
            case 37000005: {
                return 37001004;
            }
            case 37120001: {
                return 37121000;
            }
            case 37000010: {
                return 37001001;
            }
            case 37120013: {
                return 37120008;
            }
            case 37110010: {
                return 37110007;
            }
            case 37100009: {
                return 37100007;
            }
            case 400011019: {
                return 400011017;
            }
            case 400010028: {
                return 400011028;
            }
            case 400011092: 
            case 400011093: 
            case 400011094: 
            case 400011095: 
            case 400011096: 
            case 400011097: 
            case 400011103: {
                return 400011091;
            }
            case 400011117: 
            case 400011133: {
                return 400011116;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 37000011: 
            case 37000012: 
            case 37000013: {
                effect.getInfo().put(MapleStatInfo.time, 7000);
                statups.put(SecondaryStat.RWOverHeat, 1);
                return 1;
            }
            case 37000006: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.RWBarrier, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 37000009: 
            case 37001004: 
            case 37100008: 
            case 37120014: {
                return 1;
            }
            case 37000010: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.RWCylinder, 1);
                return 1;
            }
            case 37100002: 
            case 37101001: 
            case 37110004: 
            case 37111003: {
                statups.put(SecondaryStat.RWMovingEvar, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 37110009: 
            case 37120012: {
                statups.put(SecondaryStat.RWCombination, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 37121052: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.RWMagnumBlow, 1);
                return 1;
            }
            case 37121054: {
                statups.put(SecondaryStat.RWMaximizeCannon, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 37121004: {
                effect.getInfo().put(MapleStatInfo.time, 3500);
                statups.put(SecondaryStat.NotDamaged, 1);
                monsterStatus.put(MonsterStatus.Freeze, 1);
                return 1;
            }
            case 37001001: {
                effect.getInfo().put(MapleStatInfo.time, -1);
                statups.put(SecondaryStat.RWCylinder, 1);
                return 1;
            }
            case 37101003: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 37110001: 
            case 37110002: 
            case 37111000: {
                monsterStatus.put(MonsterStatus.AddDamSkill, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.RWChoppingHammer, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 37121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400011017: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 37001001: 
            case 37101001: 
            case 37111000: 
            case 37111003: {
                return 0;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 37000006: {
                int value = applyto.getBuffedIntValue(SecondaryStat.RWBarrier);
                if (applyto.getBuffedValue(SecondaryStat.RWBarrier) != null) {
                    if (!applier.primary) {
                        value -= value * applier.effect.getY() / 100 + applier.effect.getZ();
                    }
                } else {
                    int shield = applyto.getHurtHP() * applier.effect.getX() / 10;
                    value = Math.max(0, shield < value * applier.effect.getY() / 100 + applier.effect.getZ() ? 0 : shield);
                }
                applier.localstatups.put(SecondaryStat.RWBarrier, Math.max(0, value));
                if (value <= 0) {
                    applier.overwrite = false;
                    applier.localstatups.clear();
                }
                return 1;
            }
            case 37100002: 
            case 37101001: 
            case 37110004: 
            case 37111003: {
                if (!applier.primary) {
                    applier.duration = 1500;
                }
                return 1;
            }
            case 37000011: 
            case 37000012: 
            case 37000013: 
            case 37001002: {
                applyto.dispelEffect(SecondaryStat.RWOverHeat);
                applyto.handleCylinder(-8);
                if (applyto.getBuffedIntValue(SecondaryStat.RWMaximizeCannon) > 0) {
                    applier.duration = 1000;
                }
                return 1;
            }
            case 37000010: {
                if (!applier.passive) {
                    applyto.handleAmmoClip(8);
                }
                return 1;
            }
            case 37121052: {
                if (!applier.primary) {
                    return 0;
                }
                if (!applier.passive) {
                    return 1;
                }
                int value = applyto.getBuffedIntValue(SecondaryStat.RWMagnumBlow);
                if (value < applier.effect.getSubTime()) {
                    applier.localstatups.put(SecondaryStat.RWMagnumBlow, Math.min(value + 1, applier.effect.getSubTime()));
                    return 1;
                }
                return 0;
            }
            case 37110009: {
                int n34 = applyto.getBuffedIntValue(SecondaryStat.RWCombination) + 1;
                applier.localstatups.put(SecondaryStat.CombatFrenzy, Math.min(applier.effect.getX(), n34));
                if (n34 >= 6) {
                    applier.localstatups.put(SecondaryStat.IndieBooster, 1);
                }
                return 1;
            }
            case 37120012: {
                int n35 = applyto.getBuffedIntValue(SecondaryStat.RWCombination) + 1;
                applier.localstatups.put(SecondaryStat.RWCombination, Math.min(applier.effect.getX(), n35));
                applier.localstatups.put(SecondaryStat.IndieCr, Math.min(applier.effect.getQ() * n35, applier.effect.getQ() * applier.effect.getX()));
                if (n35 >= 6) {
                    applier.localstatups.put(SecondaryStat.IndieBooster, 1);
                }
                return 1;
            }
            case 37121053: {
                if (applyfrom.getJob() / 1000 != applyto.getJob() / 1000) {
                    return 0;
                }
                applyto.dispelEffect(31221053);
                applyto.dispelEffect(37121053);
                applyto.dispelEffect(32121053);
                applyto.dispelEffect(33121053);
                applyto.dispelEffect(35121053);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (applier.effect != null) {
            switch (applier.effect.getSourceId()) {
                case 37000011: 
                case 37000012: 
                case 37000013: 
                case 37001002: {
                    LinkedList<ExtraSkill> eskills;
                    List<Integer> exList = null;
                    exList = applier.effect.getSourceId() == 37000013 ? Arrays.asList(37000008, 37000009, 37100009, 37110010, 37120013) : (applier.effect.getSourceId() == 37000012 ? Arrays.asList(37000008, 37000009, 37100009, 37110010) : (applier.effect.getSourceId() == 37000011 ? Arrays.asList(37000008, 37000009, 37100009) : Arrays.asList(37000008, 37000009)));
                    if (exList != null) {
                        eskills = new LinkedList();
                        for (int skill : exList) {
                            ExtraSkill eskill = new ExtraSkill(skill, player.getPosition());
                            eskill.Value = 1;
                            eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
                            eskills.add(eskill);
                        }
                        player.getClient().announce(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), eskills));
                    }
                    player.handleCylinder(-8);
                    MapleStatEffect skillEffect15 = player.getSkillEffect(37000010);
                    if (skillEffect15 == null) break;
                    skillEffect15.unprimaryPassiveApplyTo(player);
                    break;
                }
                case 37000009: 
                case 37100008: 
                case 37120014: {
                    if (player.getBuffedIntValue(SecondaryStat.RWOverHeat) > 0) break;
                    player.handleCylinder(1);
                    MapleStatEffect skillEffect14 = player.getSkillEffect(37000010);
                    if (skillEffect14 == null) break;
                    skillEffect14.unprimaryPassiveApplyTo(player);
                    return 1;
                }
                case 37000005: 
                case 37001004: 
                case 37120015: 
                case 37120016: 
                case 37120017: 
                case 37120018: 
                case 37120019: {
                    MapleStatEffect skillEffect14;
                    player.handleAmmoClip(-1);
                    if (player.getSpecialStat().getBullet() <= 0) {
                        player.dispelEffect(SecondaryStat.RWCylinder);
                        player.handleAmmoClip(8);
                    }
                    if ((skillEffect14 = player.getSkillEffect(37000010)) == null) break;
                    skillEffect14.unprimaryPassiveApplyTo(player);
                    return 1;
                }
                case 37121052: {
                    player.dispelEffect(SecondaryStat.RWMagnumBlow);
                    if (player.getCylinder() <= 0) break;
                    ExtraSkill eskill = new ExtraSkill(37000007, player.getPosition());
                    eskill.Value = 1;
                    eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
                    player.getClient().announce(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), Collections.singletonList(eskill)));
                    break;
                }
                case 37121004: {
                    player.handleAmmoClip(8);
                    MapleStatEffect skillEffect17 = player.getSkillEffect(37000010);
                    if (skillEffect17 != null) {
                        skillEffect17.unprimaryPassiveApplyTo(player);
                    }
                    ExtraSkill eskill = new ExtraSkill(37120023, player.getPosition());
                    eskill.Value = 1;
                    eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
                    player.getClient().announce(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), Collections.singletonList(eskill)));
                    break;
                }
                case 37121000: {
                    List<Integer> exList = Arrays.asList(37000008, 37000009);
                    LinkedList<ExtraSkill> eskills = new LinkedList<ExtraSkill>();
                    for (int skill : exList) {
                        ExtraSkill eskill = new ExtraSkill(skill, player.getPosition());
                        eskill.Value = 1;
                        eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
                        eskills.add(eskill);
                    }
                    player.getClient().announce(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), eskills));
                    if (player.getBuffStatValueHolder(400011017) == null) break;
                    player.getClient().announce(MaplePacketCreator.userBonusAttackRequest(400011019, 0, Collections.emptyList()));
                    break;
                }
                case 37110001: 
                case 37111000: {
                    player.getSkillEffect(37110002).applyAffectedArea(player, player.getPosition());
                    break;
                }
                case 37001000: 
                case 37101000: 
                case 37120002: 
                case 37121003: 
                case 400011028: {
                    ExtraSkill eskill = new ExtraSkill(37000007, player.getPosition());
                    eskill.Value = 1;
                    eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
                    player.getClient().announce(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), Collections.singletonList(eskill)));
                    if (player.getBuffStatValueHolder(400011017) == null) break;
                    player.getClient().announce(MaplePacketCreator.userBonusAttackRequest(400011019, 0, Collections.emptyList()));
                    break;
                }
            }
            switch (applier.effect.getSourceId()) {
                case 37100002: 
                case 37110001: 
                case 37110004: {
                    MapleStatEffect l801 = player.getSkillEffect(37100006);
                    if (l801 == null) break;
                    if (player.getSkillEffect(37120011) != null) {
                        l801 = player.getSkillEffect(37120011);
                    }
                    player.handleAmmoClip(l801.getW());
                    MapleStatEffect skillEffect18 = player.getSkillEffect(37000010);
                    if (skillEffect18 == null) break;
                    skillEffect18.unprimaryPassiveApplyTo(player);
                    break;
                }
                case 37001000: 
                case 37101000: 
                case 37121004: {
                    MapleStatEffect l802 = player.getSkillEffect(37110009);
                    if (l802 == null) break;
                    if (player.getSkillEffect(37120012) != null) {
                        l802 = player.getSkillEffect(37120012);
                    }
                    l802.unprimaryPassiveApplyTo(player);
                    break;
                }
            }
        }
        return 1;
    }
}

