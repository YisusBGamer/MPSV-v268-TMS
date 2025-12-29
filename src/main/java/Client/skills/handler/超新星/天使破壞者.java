/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.超新星;

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
import Net.server.maps.MapleSummon;
import Packet.ForcePacket;
import Packet.SummonPacket;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 天使破壞者
extends AbstractSkillHandler {
    public 天使破壞者() {
        this.jobs = new MapleJob[]{MapleJob.天使破壞者, MapleJob.天使破壞者1轉, MapleJob.天使破壞者2轉, MapleJob.天使破壞者3轉, MapleJob.天使破壞者4轉};
        for (Field field : Config.constants.skills.天使破壞者.class.getDeclaredFields()) {
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
        for (int i : ss = new int[]{60011005, 60011216, 60011218, 60011220, 60011222}) {
            Skill skil;
            if (chr.getLevel() < 200 && i == 60011005 || (skil = SkillFactory.getSkill(i)) == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 65141501: 
            case 65141502: {
                return 65141500;
            }
            case 65141000: {
                return 65121101;
            }
            case 500004148: {
                return 400051011;
            }
            case 500004149: {
                return 400051018;
            }
            case 500004150: {
                return 400051046;
            }
            case 500004151: {
                return 400051072;
            }
            case 65001004: {
                return 65001001;
            }
            case 65101006: {
                return 65101100;
            }
            case 65111007: {
                return 65111100;
            }
            case 65121007: 
            case 65121008: {
                return 65121101;
            }
            case 65121012: {
                return 65121003;
            }
            case 65120011: {
                return 65121011;
            }
            case 400051019: 
            case 400051020: 
            case 400051027: {
                return 400051018;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 60011005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 65101002: {
                statups.put(SecondaryStat.PowerTransferGauge, effect.getInfo().get((Object)MapleStatInfo.y) * 1000);
                return 1;
            }
            case 65121004: {
                statups.put(SecondaryStat.SoulGazeCriDamR, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 65121011: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.AngelicBursterSoulSeeker, 1);
                return 1;
            }
            case 65121053: {
                statups.put(SecondaryStat.IndieStance, effect.getInfo().get((Object)MapleStatInfo.indieStance));
                statups.put(SecondaryStat.Stance, 0);
                statups.put(SecondaryStat.EnrageCr, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.IndieTerR, effect.getInfo().get((Object)MapleStatInfo.terR));
                statups.put(SecondaryStat.IndieAsrR, effect.getInfo().get((Object)MapleStatInfo.asrR));
                return 1;
            }
            case 65121054: {
                statups.put(SecondaryStat.IndieBDR, effect.getInfo().get((Object)MapleStatInfo.indieBDR));
                statups.put(SecondaryStat.IndieIgnoreMobpdpR, effect.getInfo().get((Object)MapleStatInfo.indieIgnoreMobpdpR));
                statups.put(SecondaryStat.SoulExalt, 1);
                return 1;
            }
            case 400051011: {
                statups.put(SecondaryStat.EnergyBust, 1);
                return 1;
            }
            case 65121002: {
                monsterStatus.put(MonsterStatus.Fatality, 1);
                return 1;
            }
            case 65101006: 
            case 65101100: {
                monsterStatus.put(MonsterStatus.Explosion, 1);
                return 1;
            }
            case 65120006: {
                effect.getInfo().put(MapleStatInfo.time, 5000);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 400051018: {
                statups.put(SecondaryStat.FifthSpotLight, effect.getLevel());
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        MapleForceFactory forceFactory = MapleForceFactory.getInstance();
        if (applier.effect.getSourceId() == 65111100) {
            applier.pos = slea.readPos();
            List<Integer> oids = IntStream.range(0, slea.readByte()).mapToObj(i -> slea.readInt()).collect(Collectors.toList());
            chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(forceFactory.getMapleForce(chr, applier.effect, 0, oids)), true);
            chr.handelAngelReborn(applier.effect);
            return 1;
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 60011220: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 65101002: {
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.PowerTransferGauge);
                if (mbsvh != null) {
                    int value = Math.min(applyto.getBuffedIntValue(SecondaryStat.PowerTransferGauge), 99999);
                    applier.duration = mbsvh.getLeftTime();
                    applier.localstatups.put(SecondaryStat.PowerTransferGauge, value);
                }
                return 1;
            }
            case 65121011: {
                if (applyto.getBuffedValue(SecondaryStat.AngelicBursterSoulSeeker) != null) {
                    applier.overwrite = false;
                    applier.localstatups.clear();
                }
                return 1;
            }
            case 65121101: {
                int min = Math.min(applyto.getBuffedIntValue(SecondaryStat.Trinity) / applier.effect.getX() + 1, 4);
                if (min > 3) {
                    return 0;
                }
                applier.localstatups.put(SecondaryStat.Trinity, min * applier.effect.getX());
                return 1;
            }
            case 400051011: {
                if (!applier.primary) {
                    applyto.dispelEffect(400051011);
                    return 0;
                }
                return 1;
            }
            case 400051018: {
                if (!applier.primary) {
                    return 0;
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleForceFactory mmf = MapleForceFactory.getInstance();
        MapleStatEffect effecForBuffStat11 = applyfrom.getEffectForBuffStat(SecondaryStat.AngelicBursterSoulSeeker);
        if (applier.totalDamage > 0L && effecForBuffStat11 != null && effecForBuffStat11.makeChanceResult(applyfrom) && applier.effect != null && applier.effect.getSourceId() != 60011216 && applier.effect.getSourceId() != 65111007) {
            applyfrom.getMap().broadcastMessage(applyfrom, ForcePacket.forceAtomCreate(mmf.getMapleForce(applyfrom, effecForBuffStat11, 0, Collections.singletonList(applyto.getObjectId()))), true);
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect effecForBuffStat12 = player.getEffectForBuffStat(SecondaryStat.PowerTransferGauge);
        if (applier.totalDamage > 0L && effecForBuffStat12 != null) {
            player.setBuffStatValue(SecondaryStat.PowerTransferGauge, 65101002, (int)Math.min((long)player.getBuffedIntValue(SecondaryStat.PowerTransferGauge) + applier.totalDamage * (long)effecForBuffStat12.getY() / 100L, 99999L));
            effecForBuffStat12.unprimaryPassiveApplyTo(player);
        }
        player.handelAngelReborn(applier.effect);
        MapleSummon summonBySkillID4 = player.getSummonBySkillID(400051046);
        if (applier.totalDamage > 0L & applier.effect != null && applier.effect.getSourceId() != 400051046 && summonBySkillID4 != null && player.getCheatTracker().canNextAllRocket(400051046, 1500)) {
            player.getClient().announce(SummonPacket.SummonedAssistAttackRequest(player.getId(), summonBySkillID4.getObjectId(), Randomizer.nextBoolean() ? 10 : 11));
        }
        return 1;
    }
}

