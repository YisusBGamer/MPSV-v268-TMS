/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.皇家騎士團;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.force.MapleForceFactory;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Packet.MaplePacketCreator;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import tools.data.MaplePacketReader;

public class 烈焰巫師
extends AbstractSkillHandler {
    public 烈焰巫師() {
        this.jobs = new MapleJob[]{MapleJob.烈焰巫師1轉, MapleJob.烈焰巫師2轉, MapleJob.烈焰巫師3轉, MapleJob.烈焰巫師4轉};
        for (Field field : Config.constants.skills.皇家騎士團_技能群組.烈焰巫師.class.getDeclaredFields()) {
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
        super.baseSkills(chr, applier);
        Skill skill = SkillFactory.getSkill(0x989778);
        if (skill != null && chr.getSkillLevel(skill) <= 0) {
            applier.skillMap.put(skill.getId(), new SkillEntry(1, skill.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 12141501: 
            case 12141502: {
                return 12141500;
            }
            case 12141000: 
            case 12141001: 
            case 12141002: 
            case 12141003: 
            case 12141004: 
            case 12141005: 
            case 12141006: {
                return 12120007;
            }
            case 500004064: {
                return 400021004;
            }
            case 500004065: {
                return 400021042;
            }
            case 500004066: {
                return 400021072;
            }
            case 500004067: {
                return 400021092;
            }
            case 12000026: {
                return 12001020;
            }
            case 12100028: {
                return 12100020;
            }
            case 0xB8C8CC: {
                return 12110020;
            }
            case 12120010: 
            case 12120017: {
                return 12120006;
            }
            case 12120011: {
                return 12121001;
            }
            case 12121016: {
                return 12121005;
            }
            case 12120012: {
                return 12121003;
            }
            case 12120013: 
            case 12120014: {
                return 12121004;
            }
            case 400021043: 
            case 400021044: 
            case 400021045: {
                return 400021042;
            }
            case 12111029: {
                return 12111023;
            }
            case 12121055: {
                return 12121054;
            }
            case 12100029: {
                return 12101024;
            }
            case 400021093: {
                return 400021092;
            }
            case 12001027: {
                return 12001028;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 12121052: {
                statups.put(SecondaryStat.NotDamaged, 1);
                effect.getInfo().put(MapleStatInfo.time, 5000);
                return 1;
            }
            case 12121004: {
                statups.put(SecondaryStat.IndiePDDR, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 12101024: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.AttackCountX, 3);
                return 1;
            }
            case 12101025: {
                effect.getInfo().put(MapleStatInfo.time, 100000);
                statups.put(SecondaryStat.ReturnTeleport, 1);
                return 1;
            }
            case 12101022: {
                effect.setMpR((double)effect.getInfo().get((Object)MapleStatInfo.x).intValue() / 100.0);
                return 1;
            }
            case 12000022: 
            case 12100026: 
            case 0xB8C8C8: 
            case 12120007: {
                statups.put(SecondaryStat.IndieMAD, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 12121005: {
                effect.getInfo().put(MapleStatInfo.time, 30000);
                statups.put(SecondaryStat.IndieBooster, effect.getInfo().get((Object)MapleStatInfo.indieBooster));
                return 1;
            }
            case 12111023: {
                effect.getInfo().put(MapleStatInfo.time, 1800000);
                statups.put(SecondaryStat.SiphonVitality, 1);
                return 1;
            }
            case 12111029: {
                effect.getInfo().put(MapleStatInfo.time, 3000);
                statups.put(SecondaryStat.NotDamaged, 1);
                return 1;
            }
            case 12121003: {
                statups.put(SecondaryStat.DamageReduce, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 12120013: 
            case 12120014: {
                statups.put(SecondaryStat.IgnoreTargetDEF, 1);
                return 1;
            }
            case 12100031: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 12121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 12121054: {
                effect.getInfo().put(MapleStatInfo.time, 15000);
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400021072: {
                effect.getInfo().put(MapleStatInfo.time, 2005032704);
                statups.put(SecondaryStat.IndieCheckTimeByClient, 1);
                statups.put(SecondaryStat.IndieHitDamR, 8);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        MapleForceFactory forceFactory = MapleForceFactory.getInstance();
        switch (applier.effect.getSourceId()) {
            case 12101025: {
                boolean b;
                Point p = slea.readPos();
                slea.readPos();
                slea.skip(1);
                boolean bl = b = slea.readByte() > 0;
                if (b) {
                    slea.skip(11);
                    Point old = slea.readPos();
                    chr.setPosition(p);
                    chr.getMap().objectMove(-1, chr, null);
                    chr.removeSummon(12101025);
                    c.announce(MaplePacketCreator.userTeleport(false, 2, chr.getId(), old));
                }
                return 1;
            }
            case 12111022: {
                applier.pos = slea.readPos();
                slea.readByte();
                slea.readShort();
                MapleMonster mobObject = chr.getMap().getMonsterByOid(slea.readInt());
                if (mobObject == null) {
                    c.announce(MaplePacketCreator.sendSkillUseResult(false, 0));
                    return 0;
                }
                chr.getSpecialStat().setMaelstromMoboid(mobObject.getId());
                return 1;
            }
            case 12120013: 
            case 12120014: {
                chr.getSkillEffect(12121004).applyTo(chr);
                return 1;
            }
            case 400021044: {
                List<Integer> oids = IntStream.range(0, slea.readByte()).mapToObj(i -> slea.readInt()).collect(Collectors.toList());
                forceFactory.getMapleForce(chr, applier.effect, 0, oids);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 12101024: {
                applier.buffz = applyto.getBuffedIntZ(SecondaryStat.FlameDischarge);
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.FlameDischarge);
                if (applier.passive && mbsvh != null) {
                    applier.buffz = applier.primary ? Math.max(0, applier.buffz - 2) : Math.min(6, applier.buffz + 1);
                    applier.duration = mbsvh.getLeftTime();
                }
                return 1;
            }
            case 12120013: {
                applier.localstatups.put(SecondaryStat.IgnoreTargetDEF, applyto.getTotalSkillLevel(12121004));
                applyto.dispelEffect(12120014);
                return 1;
            }
            case 12120014: {
                applier.localstatups.put(SecondaryStat.IgnoreTargetDEF, applyto.getTotalSkillLevel(12121004));
                applyto.dispelEffect(12120013);
                return 1;
            }
            case 12121053: {
                if (applyfrom.getJob() / 1000 != applyto.getJob() / 1000 || applyto.getJob() / 1000 == 5) {
                    return 0;
                }
                applyto.dispelEffect(11121053);
                applyto.dispelEffect(12121053);
                applyto.dispelEffect(13121053);
                applyto.dispelEffect(14121053);
                applyto.dispelEffect(15121053);
                applyto.dispelEffect(51121053);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleStatEffect effecForBuffStat4 = applyfrom.getEffectForBuffStat(SecondaryStat.FlameDischarge);
        if (applier.totalDamage > 0L && effecForBuffStat4 != null && applyto != null && applyto.isAlive()) {
            effecForBuffStat4.applyMonsterEffect(applyfrom, applyto, effecForBuffStat4.getDotTime(applyfrom) * 1000);
            if (applyfrom.getSkillEffect(400021042) != null) {
                effecForBuffStat4.unprimaryPassiveApplyTo(applyfrom);
            }
        }
        return 1;
    }
}

