/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.英雄團;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.force.MapleForceFactory;
import Client.skills.ExtraSkill;
import Client.skills.Skill;
import Client.skills.SkillEntry;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Config.constants.SkillConstants;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Net.server.maps.MapleSummon;
import Opcode.header.OutHeader;
import Packet.EffectPacket;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import Packet.SummonPacket;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import tools.data.MaplePacketLittleEndianWriter;
import tools.data.MaplePacketReader;

public class 龍魔導士
extends AbstractSkillHandler {
    public 龍魔導士() {
        this.jobs = new MapleJob[]{MapleJob.龍魔導士, MapleJob.龍魔導士1轉, MapleJob.龍魔導士2轉, MapleJob.龍魔導士3轉, MapleJob.龍魔導士4轉};
        for (Field field : Config.constants.skills.龍魔導士.class.getDeclaredFields()) {
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
        for (int i : ss = new int[]{20011293, 20011005, 20010194}) {
            Skill skil;
            if (chr.getLevel() < 200 && i == 20011005 || (skil = SkillFactory.getSkill(i)) == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 22201501: {
                return 22201500;
            }
            case 22201000: 
            case 22201001: {
                return 22170060;
            }
            case 500004084: {
                return 400021012;
            }
            case 500004085: {
                return 400021046;
            }
            case 500004086: {
                return 400021073;
            }
            case 500004087: {
                return 400021095;
            }
            case 20010022: {
                return 80001000;
            }
            case 22110013: 
            case 22110014: 
            case 22110022: 
            case 22110023: 
            case 22110024: 
            case 22110025: {
                return 22111012;
            }
            case 22140013: 
            case 22140014: 
            case 22140015: 
            case 22140022: 
            case 22140023: 
            case 22140024: {
                return 22141012;
            }
            case 22170064: 
            case 22170066: 
            case 22170067: 
            case 22170093: 
            case 22170094: {
                return 22171063;
            }
            case 22171083: {
                return 22171080;
            }
            case 400021013: 
            case 400021014: 
            case 400021015: 
            case 400021016: {
                return 400021012;
            }
            case 400020046: 
            case 400020051: {
                return 400021046;
            }
            case 22170061: {
                return 22170060;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 20011005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 22001012: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.MagicGuard, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 22171082: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 22170072: {
                statups.put(SecondaryStat.Stance, effect.getInfo().get((Object)MapleStatInfo.prop));
                return 1;
            }
            case 22171080: {
                effect.getInfo().put(MapleStatInfo.time, effect.getInfo().get((Object)MapleStatInfo.y) * 1000);
                statups.put(SecondaryStat.NotDamaged, 1);
                statups.put(SecondaryStat.NewFlying, 1);
                statups.put(SecondaryStat.RideVehicleExpire, 1939007);
                return 1;
            }
            case 22140019: {
                statups.put(SecondaryStat.MagicResistance, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 22141016: {
                statups.put(SecondaryStat.ElementalReset, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 22110013: {
                monsterStatus.put(MonsterStatus.Weakness, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        MapleForceFactory forceFactory = MapleForceFactory.getInstance();
        switch (applier.effect.getSourceId()) {
            case 22170064: {
                MapleStatEffect skillEffect9 = chr.getSkillEffect(22170093);
                if (skillEffect9 != null) {
                    skillEffect9.applyTo(chr);
                }
                return 1;
            }
            case 22141017: 
            case 22170070: {
                Map<Integer, Point> wreckagesMap = chr.getWreckagesMap();
                ArrayList<Integer> oids = new ArrayList<Integer>();
                for (MapleMapObject monster : chr.getMap().getMapObjectsInRange(chr.getPosition(), 500.0, Collections.singletonList(MapleMapObjectType.MONSTER))) {
                    oids.add(monster.getObjectId());
                    if (oids.size() < applier.effect.getMobCount()) continue;
                    break;
                }
                if (!wreckagesMap.isEmpty() && !oids.isEmpty()) {
                    chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(forceFactory.getMapleForce(chr, applier.effect, oids, wreckagesMap.values())), true);
                    MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                    mplew.writeShort(OutHeader.LP_DEL_WRECKAGE.getValue());
                    mplew.writeInt(chr.getId());
                    mplew.writeInt(wreckagesMap.size());
                    mplew.write(0);
                    mplew.write(0);
                    wreckagesMap.keySet().forEach(mplew::writeInt);
                    chr.getMap().broadcastMessage(chr, mplew.getPacket(), true);
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 20011293: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 22171080: {
                return 1;
            }
            case 22171082: {
                if (applyfrom.getJob() / 1000 != applyto.getJob() / 1000) {
                    return 0;
                }
                applyto.dispelEffect(21121053);
                applyto.dispelEffect(22171082);
                applyto.dispelEffect(27121053);
                applyto.dispelEffect(23121053);
                applyto.dispelEffect(24121053);
                applyto.dispelEffect(25121132);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        MapleForceFactory mmf = MapleForceFactory.getInstance();
        if (applier.effect != null && SkillConstants.eD(applier.effect.getSourceId())) {
            MapleStatEffect eff = applyfrom.getSkillEffect(22141017);
            if (applyfrom.getSkillEffect(22170070) != null) {
                eff = applyfrom.getSkillEffect(22170070);
            }
            if (eff != null && applyfrom.getEvanWreckages().size() < eff.getX()) {
                int addWreckages = applyfrom.addWreckages(new Point(applyto.getPosition()), eff.getDuration());
                MapleMap map = applyfrom.getMap();
                int id = applyfrom.getId();
                Point position = applyto.getPosition();
                int n7 = eff.getDuration() / 1000;
                int sourceid = eff.getSourceId();
                int size = applyfrom.getEvanWreckages().size();
                byte[] packet = EffectPacket.DragonWreckage(id, position, n7, addWreckages, sourceid, 1, size);
                map.broadcastMessage(applyfrom, packet, true);
            }
        }
        MapleSummon summon = applyfrom.getSummonBySkillID(400021073);
        if (applier.effect != null && summon != null && this.containsJob(applier.effect.getSourceId() / 10000) && summon.getAcState1() < summon.getEffect().getX()) {
            summon.setAcState1(Math.min(summon.getAcState1() + 1, summon.getEffect().getX()));
            applyfrom.getMap().broadcastMessage(applyfrom, ForcePacket.forceAtomCreate(mmf.getMapleForce(applyfrom, summon.getEffect(), applyto.getObjectId(), 0, null, applyto.getPosition())), true);
            applyfrom.getMap().broadcastMessage(applyfrom, SummonPacket.SummonedStateChange(summon, 2, summon.getAcState1(), 0), true);
            if (summon.getAcState1() >= summon.getEffect().getX()) {
                MapleMap map2 = applyfrom.getMap();
                map2.broadcastMessage(applyfrom, SummonPacket.SummonMagicCircleAttack(summon, 10, summon.getPosition()), true);
            }
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (applier.effect != null) {
            MapleStatEffect skillEffect14;
            if (applier.effect.getSourceId() == 400021012) {
                LinkedList<ExtraSkill> eskills = new LinkedList<ExtraSkill>();
                for (int i = 0; i < 4; ++i) {
                    ExtraSkill eskill = new ExtraSkill(400021013 + i, player.getPosition());
                    eskill.Value = 1;
                    eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
                    eskills.add(eskill);
                }
                player.getClient().announce(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), eskills));
            }
            if (SkillConstants.i0(applier.effect.getSourceId()) && (skillEffect14 = player.getSkillEffect(22110016)) != null && player.getBuffStatValueHolder(22110016) == null) {
                skillEffect14.applyTo(player);
            }
        }
        return 1;
    }
}

