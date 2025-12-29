/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.末日反抗軍;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.maps.MapleSummon;
import Packet.SummonPacket;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.Map;
import tools.data.MaplePacketReader;

public class 惡魔殺手
extends AbstractSkillHandler {
    public 惡魔殺手() {
        this.jobs = new MapleJob[]{MapleJob.惡魔殺手1轉, MapleJob.惡魔殺手2轉, MapleJob.惡魔殺手3轉, MapleJob.惡魔殺手4轉};
        for (Field field : Config.constants.skills.惡魔殺手.class.getDeclaredFields()) {
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
        Skill skil;
        super.baseSkills(chr, applier);
        if (chr.getLevel() >= 10 && (skil = SkillFactory.getSkill(30010111)) != null && chr.getSkillLevel(skil) <= 0) {
            applier.skillMap.put(skil.getId(), new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 31141501: 
            case 31141502: 
            case 31141503: {
                return 31141500;
            }
            case 31141000: 
            case 31141001: {
                return 31121001;
            }
            case 500004104: {
                return 400011006;
            }
            case 500004105: {
                return 400011057;
            }
            case 500004106: {
                return 400011077;
            }
            case 500004107: {
                return 400011110;
            }
            case 31001006: 
            case 31001007: 
            case 31001008: {
                return 31000004;
            }
            case 31121010: {
                return 31121000;
            }
            case 400011007: 
            case 400011008: 
            case 400011009: 
            case 400011018: {
                return 400011006;
            }
            case 400011078: {
                return 400011077;
            }
            case 400011111: 
            case 400011137: {
                return 400011110;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 31001001: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 31101002: 
            case 31111001: 
            case 31121006: {
                monsterStatus.put(MonsterStatus.Seal, 1);
                return 1;
            }
            case 31101003: {
                statups.put(SecondaryStat.PowerGuard, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.Freeze, 1);
                return 1;
            }
            case 31111003: {
                effect.setHpR((double)effect.getX() / 100.0);
                return 1;
            }
            case 31121001: {
                monsterStatus.put(MonsterStatus.Speed, effect.getX());
                return 1;
            }
            case 31121003: {
                statups.put(SecondaryStat.DevilCry, 1);
                effect.getInfo().put(MapleStatInfo.prop, 100);
                monsterStatus.put(MonsterStatus.Showdown, effect.getInfo().get((Object)MapleStatInfo.w));
                monsterStatus.put(MonsterStatus.MDR, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.PDR, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.MAD, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.PAD, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.ACC, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 31121005: {
                statups.put(SecondaryStat.DamR, effect.getInfo().get((Object)MapleStatInfo.damR));
                statups.put(SecondaryStat.DevilishPower, effect.getLevel());
                statups.put(SecondaryStat.IndieMHPR, effect.getInfo().get((Object)MapleStatInfo.indieMhpR));
                return 1;
            }
            case 31121007: {
                statups.put(SecondaryStat.InfinityForce, 1);
                return 1;
            }
            case 31121002: {
                statups.put(SecondaryStat.VampiricTouch, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 31121053: {
                effect.setPartyBuff(true);
                return 1;
            }
            case 31121054: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.ShadowPartner, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400011006: {
                statups.put(SecondaryStat.IndieCr, effect.getInfo().get((Object)MapleStatInfo.indieCr));
                statups.put(SecondaryStat.IndiePMdR, effect.getInfo().get((Object)MapleStatInfo.indiePMdR));
                return 1;
            }
            case 400011057: {
                effect.getInfo().put(MapleStatInfo.attackCount, effect.getInfo().get((Object)MapleStatInfo.attackCount) + 3);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 400011077) {
            chr.getSkillEffect(400011078).applyTo(chr, new Point(chr.getPosition().x + 100, chr.getPosition().y));
            return 1;
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 31121005) {
            if (applier.passive) {
                return 0;
            }
            if (applyfrom.getSkillLevel(31120046) > 0) {
                applier.maskedDuration = applier.duration * 20 / 100;
                applier.maskedstatups.put(SecondaryStat.IndieIgnorePCounter, 1);
                applier.maskedstatups.put(SecondaryStat.IgnorePImmune, 1);
            }
            return 1;
        }
        return -1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleSummon summonBySkillID3;
        MapleSummon summonBySkillID2;
        if (applier.effect != null && applier.totalDamage > 0L && this.containsJob(player.getJobWithSub()) && applier.effect.getSourceId() == 31121052) {
            applier.mpHeal = 50;
        }
        if ((summonBySkillID2 = player.getSummonBySkillID(400011077)) != null && applier.totalDamage > 0L && player.getCheatTracker().canNextAllRocket(400011077, 3000)) {
            player.getClient().announce(SummonPacket.SummonedAssistAttackRequest(player.getId(), summonBySkillID2.getObjectId(), 0));
        }
        if ((summonBySkillID3 = player.getSummonBySkillID(400011078)) != null && applier.totalDamage > 0L && player.getCheatTracker().canNextAllRocket(400011078, 5000)) {
            player.getClient().announce(SummonPacket.SummonedAssistAttackRequest(player.getId(), summonBySkillID3.getObjectId(), 0));
        }
        return 1;
    }
}

