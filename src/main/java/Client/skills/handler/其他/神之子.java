/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.其他;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.skills.ExtraSkill;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Opcode.header.OutHeader;
import Packet.MaplePacketCreator;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import tools.data.MaplePacketLittleEndianWriter;
import tools.data.MaplePacketReader;

public class 神之子
extends AbstractSkillHandler {
    public 神之子() {
        this.jobs = new MapleJob[]{MapleJob.神之子JR, MapleJob.神之子10100, MapleJob.神之子10110, MapleJob.神之子10111, MapleJob.神之子};
        for (Field field : Config.constants.skills.神之子.class.getDeclaredFields()) {
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
        if (chr.getLevel() >= 100) {
            int[] skillIds;
            Skill skil;
            List<Integer> jobSkills = SkillFactory.getSkillsByJob(10100);
            jobSkills.addAll(SkillFactory.getSkillsByJob(10110));
            jobSkills.addAll(SkillFactory.getSkillsByJob(10111));
            jobSkills.addAll(SkillFactory.getSkillsByJob(10112));
            for (int i : jobSkills) {
                skil = SkillFactory.getSkill(i);
                if (skil == null || skil.isInvisible() || !skil.isFourthJob() || chr.getSkillLevel(skil) > 0 || chr.getMasterLevel(skil) > 0 || skil.getMasterLevel() <= 0) continue;
                applier.skillMap.put(i, new SkillEntry(0, (byte)skil.getMasterLevel(), SkillFactory.getDefaultSExpiry(skil)));
            }
            for (int i : skillIds = new int[]{0x5F5E5EE, 100000282, 0x5F5E5EF, 100001265, 100001266, 100001268, 100000279, 100000267, 100001261, 100001274, 100001272, 100001283, 100001005}) {
                if (chr.getLevel() < 200 && i == 100001005 || (skil = SkillFactory.getSkill(i)) == null || !skil.canBeLearnedBy(chr.getJobWithSub()) || chr.getSkillLevel(skil) > 0) continue;
                applier.skillMap.put(i, new SkillEntry((byte)skil.getMaxLevel(), (byte)skil.getMaxLevel(), -1L));
            }
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 101000102: {
                return 101000101;
            }
            case 101000202: {
                return 101000201;
            }
            case 0x606AAAA: {
                return 101100201;
            }
            case 101110104: {
                return 101110102;
            }
            case 101110201: {
                return 101110200;
            }
            case 101110204: {
                return 101110203;
            }
            case 101120101: {
                return 101120100;
            }
            case 101120103: {
                return 101120102;
            }
            case 101120105: 
            case 101120106: {
                return 101120104;
            }
            case 101120200: {
                return 101121200;
            }
            case 101120203: {
                return 101120202;
            }
            case 101120205: 
            case 101120206: {
                return 101120204;
            }
            case 100000276: 
            case 100000277: {
                return 100000267;
            }
            case 400011024: 
            case 400011025: {
                return 400011015;
            }
            case 400011040: 
            case 400011041: 
            case 400011042: 
            case 400011043: 
            case 400011044: 
            case 400011045: 
            case 400011046: {
                return 400011039;
            }
            case 400011099: 
            case 400011100: 
            case 400011101: {
                return 400011098;
            }
            case 400011135: {
                return 400011134;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 100001005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 0x5F5E5EF: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                statups.put(SecondaryStat.IndieMAD, effect.getInfo().get((Object)MapleStatInfo.indieMad));
                statups.put(SecondaryStat.IndiePDD, effect.getInfo().get((Object)MapleStatInfo.indiePdd));
                statups.put(SecondaryStat.IndieJump, effect.getInfo().get((Object)MapleStatInfo.indieJump));
                statups.put(SecondaryStat.IndieSpeed, effect.getInfo().get((Object)MapleStatInfo.indieSpeed));
                statups.put(SecondaryStat.IndieBooster, effect.getInfo().get((Object)MapleStatInfo.indieBooster));
                statups.put(SecondaryStat.IndieAsrR, effect.getInfo().get((Object)MapleStatInfo.indieTerR));
                statups.put(SecondaryStat.IndieTerR, effect.getInfo().get((Object)MapleStatInfo.indieAsrR));
                statups.put(SecondaryStat.ZeroAuraStr, 1);
                return 1;
            }
            case 100000276: {
                statups.put(SecondaryStat.TimeFastABuff, 1);
                return 1;
            }
            case 100000277: {
                statups.put(SecondaryStat.TimeFastBBuff, 1);
                return 1;
            }
            case 101120109: {
                statups.put(SecondaryStat.PowerTransferGauge, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 100001272: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.ReviveOnce, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 100001274: {
                statups.put(SecondaryStat.NotDamaged, 1);
                return 1;
            }
            case 100001261: {
                monsterStatus.put(MonsterStatus.AddDamSkill2, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.TotalDamParty, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 101120110: {
                monsterStatus.put(MonsterStatus.Lifting, 300);
                monsterStatus.put(MonsterStatus.Freeze, 1);
                return 1;
            }
            case 101110103: {
                monsterStatus.put(MonsterStatus.MultiPMDR, effect.getInfo().get((Object)MapleStatInfo.y));
                monsterStatus.put(MonsterStatus.PDR, effect.getInfo().get((Object)MapleStatInfo.y));
                monsterStatus.put(MonsterStatus.MDR, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 101120207: {
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 400011015: {
                effect.getInfo().put(MapleStatInfo.cooltimeMS, effect.getInfo().get((Object)MapleStatInfo.cooltime));
                effect.getInfo().put(MapleStatInfo.cooltime, 0);
                statups.put(SecondaryStat.IndiePMdR, effect.getInfo().get((Object)MapleStatInfo.indiePMdR));
                statups.put(SecondaryStat.IndieBooster, effect.getInfo().get((Object)MapleStatInfo.indieBooster));
                statups.put(SecondaryStat.IndieCooltimeReduce, effect.getInfo().get((Object)MapleStatInfo.q));
                monsterStatus.put(MonsterStatus.Speed, effect.getInfo().get((Object)MapleStatInfo.y));
                return 1;
            }
            case 400011039: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                statups.put(SecondaryStat.IndieKeydown, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 400011015) {
            SecondaryStatValueHolder mbsvh = chr.getBuffStatValueHolder(400011015);
            if (mbsvh != null) {
                applier.effect.applyToMonster(chr, mbsvh.getLeftTime());
                if (!chr.isSkillCooling(400011015)) {
                    chr.registerSkillCooldown(applier.effect.getSourceId(), applier.effect.getInfo().get((Object)MapleStatInfo.cooltimeMS) * 1000, true);
                }
            }
            return 0;
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 0x5F5E5EE: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 0x5F5E5EF: {
                if (applyto.getBuffedValue(SecondaryStat.ZeroAuraStr) != null) {
                    applier.overwrite = false;
                    applier.localstatups.clear();
                }
                return 1;
            }
            case 101120109: {
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.ImmuneBarrier);
                if (mbsvh != null) {
                    applier.buffz = Math.min(applyto.getBuffedIntValue(SecondaryStat.ImmuneBarrier), applier.effect.getX() * applyto.getStat().getCurrentMaxHP() / 100);
                    applier.duration = mbsvh.getLeftTime();
                    applier.localstatups.put(SecondaryStat.ImmuneBarrier, applier.buffz);
                    return 1;
                }
                applier.buffz = applier.effect.getX() * applyto.getStat().getCurrentMaxHP() / 100;
                applier.localstatups.put(SecondaryStat.ImmuneBarrier, applier.buffz);
                return 1;
            }
            case 100001274: {
                if (applyto.getLevel() >= 200) {
                    SkillFactory.getSkill(100001281).getEffect(1).applyTo(applyto);
                }
                return 1;
            }
            case 100000276: {
                applyto.reduceAllSkillCooldown(4000, true);
                applier.localstatups.put(SecondaryStat.TimeFastABuff, Math.min(10, applyto.getBuffedIntValue(SecondaryStat.TimeFastABuff) + 1));
                return 1;
            }
            case 100000277: {
                applyto.reduceAllSkillCooldown(4000, true);
                applier.localstatups.put(SecondaryStat.TimeFastBBuff, Math.min(10, applyto.getBuffedIntValue(SecondaryStat.TimeFastBBuff) + applier.effect.getX()));
                return 1;
            }
            case 400011039: {
                if (!applier.primary) {
                    return 0;
                }
                applier.overwrite = false;
                applier.startChargeTime = System.currentTimeMillis();
                applier.localstatups.put(SecondaryStat.IndieNotDamaged, (int)applier.startChargeTime);
                applier.localstatups.put(SecondaryStat.IndieKeydown, (int)applier.startChargeTime);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        if (applier.effect != null && applier.effect.getSourceId() == 400011015 && applier.totalDamage > 0L) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(OutHeader.LP_MobTimeResist.getValue());
            mplew.writeInt(applyto.getObjectId());
            mplew.writeInt(1);
            mplew.writeInt(400011024);
            mplew.writeShort(100);
            mplew.writeInt(applyfrom.getId());
            mplew.write(1);
            applyfrom.getMap().broadcastMessage(applyfrom, mplew.getPacket(), true);
        }
        if (!applyfrom.isBeta()) {
            MapleStatEffect skillEffect21 = applyfrom.getSkillEffect(101120207);
            if (applier.totalDamage > 0L && skillEffect21 != null) {
                skillEffect21.applyMonsterEffect(applyfrom, applyto, skillEffect21.getMobDebuffDuration(applyfrom));
            }
        } else {
            if (applier.effect != null && applier.effect.getSourceId() == 101000101) {
                ExtraSkill eskill = new ExtraSkill(101000102, applyfrom.getPosition());
                eskill.Value = 1;
                eskill.FaceLeft = applyfrom.isFacingLeft() ? 0 : 1;
                applyfrom.getClient().announce(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), Collections.singletonList(eskill)));
            }
            MapleStatEffect skillEffect22 = applyfrom.getSkillEffect(101120110);
            if (applier.totalDamage > 0L && skillEffect22 != null) {
                skillEffect22.applyMonsterEffect(applyfrom, applyto, skillEffect22.getMobDebuffDuration(applyfrom));
            }
            MapleStatEffect skillEffect23 = applyfrom.getSkillEffect(101110103);
            if (applier.totalDamage > 0L && skillEffect23 != null) {
                skillEffect23.applyMonsterEffect(applyfrom, applyto, skillEffect23.getMobDebuffDuration(applyfrom));
            }
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        MapleStatEffect skillEffect22;
        int n10;
        MapleStatEffect skillEffect21;
        MapleStatEffect skillEffect20;
        if (player.isBeta() && applier.effect != null && applier.effect.getSourceId() == 101000101) {
            ExtraSkill eskill = new ExtraSkill(101000102, player.getPosition());
            eskill.Value = 1;
            eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
            player.getClient().announce(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), Collections.singletonList(eskill)));
        }
        if (applier.effect != null && applier.totalDamage > 0L && (skillEffect20 = player.getSkillEffect(100000267)) != null && (skillEffect21 = player.getSkillEffect(n10 = player.isBeta() ? 100000277 : 100000276)) != null) {
            skillEffect21.applyTo(player);
        }
        if (applier.effect != null && applier.effect.getSourceId() == 101120105 && (skillEffect22 = player.getSkillEffect(101120104)) != null) {
            skillEffect22.applyTo(player, player.getPosition());
        }
        return 1;
    }
}

