/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.末日反抗軍;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.MonsterEffectHolder;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Config.constants.JobConstants;
import Config.constants.SkillConstants;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.quest.MapleQuest;
import Opcode.Opcode.EffectOpcode;
import Packet.EffectPacket;
import Packet.MaplePacketCreator;
import Packet.MobPacket;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 狂豹獵人
extends AbstractSkillHandler {
    public 狂豹獵人() {
        this.jobs = new MapleJob[]{MapleJob.狂豹獵人1轉, MapleJob.狂豹獵人2轉, MapleJob.狂豹獵人3轉, MapleJob.狂豹獵人4轉};
        for (Field field : Config.constants.skills.狂豹獵人.class.getDeclaredFields()) {
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
        for (int i : ss = new int[]{30001061, 30001062, 33001001, 33001007, 33001016, 33001025, 33001006}) {
            Skill skil = SkillFactory.getSkill(i);
            if (chr.getJob() < i / 10000 || skil == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(11, skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 33141000: 
            case 33141001: 
            case 33141002: 
            case 33141003: 
            case 33141004: 
            case 33141005: 
            case 33141006: {
                return 33121114;
            }
            case 33141501: 
            case 33141502: 
            case 33141503: {
                return 33141500;
            }
            case 500004116: {
                return 400031005;
            }
            case 500004117: {
                return 400031012;
            }
            case 500004118: {
                return 400031032;
            }
            case 500004119: {
                return 400031046;
            }
            case 33001205: {
                return 33001105;
            }
            case 33101213: {
                return 33101113;
            }
            case 33111212: {
                return 33111112;
            }
            case 33121019: 
            case 33121214: 
            case 33121220: {
                return 33121114;
            }
            case 33000035: 
            case 33000036: 
            case 33000038: 
            case 33001008: 
            case 33001009: 
            case 33001010: 
            case 33001011: 
            case 33001012: 
            case 33001013: 
            case 33001014: 
            case 33001015: {
                return 33001007;
            }
            case 33100016: 
            case 33101215: {
                return 33101115;
            }
            case 33120018: {
                return 33121017;
            }
            case 33001039: 
            case 33001202: {
                return 33001102;
            }
            case 33120056: 
            case 33121255: {
                return 33121155;
            }
            case 33000037: {
                return 33001016;
            }
            case 33110016: {
                return 33111015;
            }
            case 400031013: 
            case 400031014: {
                return 400031012;
            }
            case 400031033: {
                return 400031032;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 33111007: {
                statups.put(SecondaryStat.BeastForm, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.Speed, effect.getInfo().get((Object)MapleStatInfo.z));
                return 1;
            }
            case 33101005: {
                statups.put(SecondaryStat.HowlingAttackDamage, effect.getInfo().get((Object)MapleStatInfo.z));
                return 1;
            }
            case 33121013: {
                statups.put(SecondaryStat.IndieAllStat, effect.getInfo().get((Object)MapleStatInfo.indieAllStat));
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 33001007: 
            case 33001008: 
            case 33001009: 
            case 33001010: 
            case 33001011: 
            case 33001012: 
            case 33001013: 
            case 33001014: 
            case 33001015: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.JaguarSummoned, effect.getInfo().get((Object)MapleStatInfo.criticaldamageMin) << 8 + effect.getInfo().get((Object)MapleStatInfo.asrR));
                monsterStatus.put(MonsterStatus.Stun, 2);
                return 1;
            }
            case 33121054: {
                statups.put(SecondaryStat.FinalAttackProp, effect.getInfo().get((Object)MapleStatInfo.prop));
                return 1;
            }
            case 33111011: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.DrawBack, 1);
                return 1;
            }
            case 33001025: {
                monsterStatus.put(MonsterStatus.DodgeBodyAttack, 1);
                monsterStatus.put(MonsterStatus.JaguarProvoke, 6271772);
                return 1;
            }
            case 33000036: {
                monsterStatus.put(MonsterStatus.JaguarBleeding, 1);
                return 1;
            }
            case 33101115: {
                monsterStatus.put(MonsterStatus.Stun, 2);
                return 1;
            }
            case 33121017: {
                monsterStatus.put(MonsterStatus.Freeze, 1);
                monsterStatus.put(MonsterStatus.Smite, 1);
                return 1;
            }
            case 33121004: {
                statups.put(SecondaryStat.SharpEyes, (effect.getInfo().get((Object)MapleStatInfo.x) << 8) + effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 33121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400031005: {
                statups.put(SecondaryStat.TempSecondaryStat, effect.getLevel());
                return 1;
            }
            case 400031012: {
                statups.put(SecondaryStat.NotDamaged, 1);
                return 1;
            }
            case 400031032: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.WildGrenade, 0);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 30001061: {
                int mobID = slea.readInt();
                MapleMonster mob = chr.getMap().getMonsterByOid(mobID);
                if (mob != null) {
                    boolean success = mob.getId() >= 9304000 && mob.getId() <= 9305000;
                    chr.getMap().broadcastMessage(chr, EffectPacket.encodeUserEffect(chr, applier.effect.getSourceId(), EffectOpcode.UserEffect_SkillUse, chr.getLevel(), applier.effect.getLevel(), (byte)(success ? 1 : 0)), chr.getPosition());
                    if (success) {
                        chr.getQuestNAdd(MapleQuest.getInstance(111112)).setCustomData(String.valueOf((mob.getId() % 10 + 1) * 10));
                        chr.getMap().killMonster(mob, chr, true, false, (byte)1, 0);
                        chr.dispelEffect(SecondaryStat.RideVehicle);
                        c.announce(MobPacket.showResults(mob.getObjectId(), true));
                        c.announce(MaplePacketCreator.updateJaguar(chr));
                    } else {
                        chr.dropMessage(5, "怪物體力過高，捕抓失敗。");
                        c.announce(MobPacket.showResults(mobID, false));
                    }
                }
                c.sendEnableActions();
                return 1;
            }
            case 400031014: {
                chr.getSkillEffect(33001001).applyTo(chr);
                return 1;
            }
            case 400031012: {
                c.announce(MaplePacketCreator.userBonusAttackRequest(400031012, 0, Collections.emptyList()));
                c.announce(MaplePacketCreator.userBonusAttackRequest(400031013, 0, Collections.emptyList()));
                return 1;
            }
            case 33000037: 
            case 33001016: 
            case 33001025: 
            case 33100016: 
            case 33101115: 
            case 33101215: 
            case 33110016: 
            case 33111015: 
            case 33121017: 
            case 33121255: {
                chr.getSpecialStat().setJaguarSkillID(applier.effect.getSourceId());
                c.announce(MaplePacketCreator.美洲豹攻擊效果(applier.effect.getSourceId()));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 33111007: {
                MapleStatEffect eff = applyto.getSkillEffect(33120043);
                if (eff != null) {
                    applier.localstatups.put(SecondaryStat.BeastForm, applier.effect.getZ() + eff.getZ());
                }
                if (applyto.getSkillEffect(33120045) != null) {
                    applier.localstatups.put(SecondaryStat.IndieBooster, applier.effect.getIndieBooster() - 1);
                }
                return 1;
            }
            case 33110014: {
                String keyValue = applyto.getKeyValue("JaguarCount");
                if (keyValue == null || !JobConstants.is狂豹獵人(applyto.getJob())) {
                    return 0;
                }
                applier.localstatups.put(SecondaryStat.JaguarCount, Math.min(6, Integer.valueOf(keyValue)));
                return 1;
            }
            case 33001007: 
            case 33001008: 
            case 33001009: 
            case 33001010: 
            case 33001011: 
            case 33001012: 
            case 33001013: 
            case 33001014: 
            case 33001015: {
                if (applier.duration < 2100000000) {
                    applier.b7 = false;
                    applier.localstatups.remove(SecondaryStat.JaguarSummoned);
                    return 1;
                }
                applyto.dispelEffect(SecondaryStat.JaguarSummoned);
                applier.localstatups.put(SecondaryStat.JaguarSummoned, 2078);
                applier.duration = 2100000000;
                return 1;
            }
            case 33121053: {
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
            case 400031005: {
                ArrayList<Integer> list = new ArrayList<Integer>();
                int[] skills = new int[]{33001008, 33001009, 33001010, 33001011, 33001012, 33001013, 33001014, 33001015};
                while (true) {
                    int skillid = skills[Randomizer.nextInt(skills.length)];
                    int mountid = Integer.parseInt(applyfrom.getInfoQuest(111112)) / 10 + 33001006;
                    if (list.contains(skillid) || mountid == skillid) continue;
                    list.add(skillid);
                    if (list.size() >= applier.effect.getY()) break;
                }
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    Point randomPos;
                    int skill = (Integer)iterator.next();
                    do {
                        randomPos = applyto.getMap().getRandomPos(applyto.getPosition());
                    } while (!applier.effect.calculateBoundingBox(applyto.getPosition(), applyto.isFacingLeft()).contains(randomPos));
                    applyfrom.getSkillEffect(skill).applyTo(applyfrom, applyfrom, applier.primary, randomPos, applier.duration, applier.passive);
                }
                return 1;
            }
            case 400031032: {
                int n44 = applyto.getBuffedIntValue(SecondaryStat.WildGrenade) + (applier.primary && applier.passive ? 1 : -1);
                SecondaryStatValueHolder buffStatValueHolder31 = applyto.getBuffStatValueHolder(SecondaryStat.WildGrenade);
                if (n44 < 0 || applier.primary && buffStatValueHolder31 != null && System.currentTimeMillis() < buffStatValueHolder31.startTime + (long)applier.effect.getT() * 1000L && applier.primary && applier.passive || n44 > applier.effect.getZ()) {
                    return 0;
                }
                applier.duration = 2100000000;
                applier.localstatups.put(SecondaryStat.WildGrenade, n44);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MonsterEffectHolder x1089;
        if (applyfrom.getEffectForBuffStat(SecondaryStat.JaguarSummoned) != null && applyfrom.getCheatTracker().canNextPantherAttack()) {
            applyfrom.getClient().announce(MaplePacketCreator.openPantherAttack(true));
        }
        if ((x1089 = applyto.getEffectHolder(MonsterStatus.JaguarBleeding)) != null && applier.effect != null && SkillConstants.eH(applier.effect.getSourceId())) {
            applyfrom.getClient().announce(MaplePacketCreator.userBonusAttackRequest(33000036, x1089.value, Collections.singletonList(applyto.getObjectId())));
        }
        if (applier.effect != null && SkillConstants.eF(applier.effect.getSourceId()) && applier.effect.makeChanceResult(applyfrom)) {
            MapleStatEffect cc = SkillFactory.getSkill(33000036).getEffect(1);
            cc.applyMonsterEffect(applyfrom, applyto, cc.getMobDebuffDuration(applyfrom));
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect skillEffect = player.getSkillEffect(400031032);
        if (applier.effect != null && skillEffect != null && applier.effect.getSourceId() == 400031032) {
            skillEffect.applyBuffEffect(player, player, 0, true, false, false, null);
        }
        return 1;
    }
}

