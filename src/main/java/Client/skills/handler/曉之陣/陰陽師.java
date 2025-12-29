/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.曉之陣;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleJob;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.force.MapleForceFactory;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.MapleInventoryType;
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
import Net.server.maps.MapleSummon;
import Packet.BuffPacket;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import Packet.MobPacket;
import Packet.SummonPacket;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 陰陽師
extends AbstractSkillHandler {
    public 陰陽師() {
        this.jobs = new MapleJob[]{MapleJob.陰陽師, MapleJob.陰陽師1轉, MapleJob.陰陽師2轉, MapleJob.陰陽師3轉, MapleJob.陰陽師4轉};
        for (Field field : Config.constants.skills.陰陽師.class.getDeclaredFields()) {
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
        for (int i : ss = new int[]{40020000, 40020001, 40020109, 40021005, 40021227}) {
            Skill skil;
            if (chr.getLevel() < 200 && i == 40021005 || (skil = SkillFactory.getSkill(i)) == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(skil.getMaxLevel(), skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    public boolean is紫扇仰波(int skillId) {
        int linkedSkillID = this.getLinkedSkillID(skillId);
        return linkedSkillID == 42001000 || linkedSkillID == 42100024 || linkedSkillID == 42110013 || linkedSkillID == 42120025;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 42141501: 
            case 42141502: {
                return 42141500;
            }
            case 500004188: {
                return 400021017;
            }
            case 500004189: {
                return 400021054;
            }
            case 500004190: {
                return 400021078;
            }
            case 500004191: {
                return 400021114;
            }
            case 42001007: {
                return 42001002;
            }
            case 42101020: 
            case 42101021: 
            case 42101022: 
            case 42101023: {
                return 42101002;
            }
            case 42121021: 
            case 42121022: 
            case 42121023: {
                return 42120011;
            }
            case 42100010: {
                return 42101001;
            }
            case 42001005: 
            case 42001006: {
                return 42001000;
            }
            case 42120026: 
            case 42120027: 
            case 42120028: {
                return 42120025;
            }
            case 42111110: 
            case 42111111: 
            case 42111112: {
                return 42110013;
            }
            case 42101100: 
            case 42101101: 
            case 42101102: {
                return 42100024;
            }
            case 42120000: {
                return 42121102;
            }
            case 42111113: {
                return 42111004;
            }
            case 42111101: 
            case 42111102: {
                return 42111100;
            }
            case 42121105: {
                return 42121104;
            }
            case 42121101: {
                return 42121100;
            }
            case 42121103: {
                return 42121005;
            }
            case 42001101: {
                return 42000000;
            }
            case 400021018: {
                return 400021017;
            }
            case 400021079: 
            case 400021080: 
            case 400021081: {
                return 400021078;
            }
            case 400021115: {
                return 400021114;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 40021005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 42101002: {
                statups.put(SecondaryStat.ChangeFoxMan, 1);
                return 1;
            }
            case 42101021: 
            case 42121021: {
                statups.put(SecondaryStat.FireBarrier, 3);
                return 1;
            }
            case 42101023: 
            case 42121023: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.Stance, 0);
                statups.put(SecondaryStat.IgnoreTargetDEF, 0);
                statups.put(SecondaryStat.BlessEnsenble, 5);
                return 1;
            }
            case 42101022: 
            case 42121022: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.FoxBless, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 42121104: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.KannaSiksinAutoAttack, effect.getLevel());
                return 1;
            }
            case 42111004: {
                monsterStatus.put(MonsterStatus.PAD, effect.getInfo().get((Object)MapleStatInfo.x));
                monsterStatus.put(MonsterStatus.MAD, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 42101003: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 42121053: {
                effect.setPartyBuff(true);
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                return 1;
            }
            case 400021017: {
                statups.put(SecondaryStat.ReduceMP, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 42101005: {
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 42121004: {
                monsterStatus.put(MonsterStatus.Freeze, 1);
                return 1;
            }
            case 42100024: {
                monsterStatus.put(MonsterStatus.Burned, 1);
                return 1;
            }
            case 42120025: {
                monsterStatus.put(MonsterStatus.Burned, 4);
                return 1;
            }
            case 42121052: {
                monsterStatus.put(MonsterStatus.Freeze, 1);
                return 1;
            }
            case 42121054: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.AntiEvilShield, effect.getLevel());
                return 1;
            }
            case 400021078: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.GhostLiberationStack, 0);
                return 1;
            }
            case 400021054: {
                statups.put(SecondaryStat.IndieAsrR, effect.getW());
                return 1;
            }
            case 400021114: {
                statups.put(SecondaryStat.KannaFifthAttract, 1);
                return 1;
            }
            case 400021115: {
                effect.getInfo().put(MapleStatInfo.time, effect.getInfo().get((Object)MapleStatInfo.attackDelay));
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 42111100: {
                Point pos = slea.readPos();
                MapleSummon summon = chr.getSummonBySkillID(42111103);
                if (summon != null) {
                    int ownerId = summon.getOwnerId();
                    int objectId = summon.getObjectId();
                    chr.getMap().broadcastMessage(SummonPacket.SummonedForceReturn(ownerId, objectId, pos), chr.getPosition());
                    chr.getMap().broadcastMessage(SummonPacket.SummonedForceMove(summon, 42111100, applier.effect.getLevel(), pos), chr.getPosition());
                    chr.getMap().disappearMapObject(summon);
                    chr.removeSummon(summon);
                    for (int i = 0; i < 2; ++i) {
                        summon = chr.getSummonBySkillID(42111101 + i);
                        if (summon == null) continue;
                        chr.getMap().broadcastMessage(SummonPacket.SummonedForceMove(summon, summon.getSkillId(), summon.getSkillLevel(), pos), chr.getPosition());
                        chr.getMap().broadcastMessage(SummonPacket.SummonMagicCircleAttack(summon, 9, pos), chr.getPosition());
                    }
                }
                return 1;
            }
            case 400021017: {
                chr.getSkillEffect(400021018).applyTo(chr);
                return 1;
            }
            case 42101002: {
                if (chr.getHaku() != null) {
                    if (chr.getSkillEffect(42120011) != null) {
                        chr.getHaku().setSpecialState(2);
                    }
                    chr.getHaku().setState(2);
                    chr.getHaku().update(chr);
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyTo(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 400021114) {
            applier.cooldown = 0;
            return 1;
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 40021227: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 42101020: 
            case 42121020: {
                applyto.addHPMP(applyto.getStat().getCurrentMaxHP() * applier.effect.getHp() / 100, 0, false);
                return 1;
            }
            case 42101021: 
            case 42121021: {
                if (applier.primary || applyto.getBuffedValue(SecondaryStat.FireBarrier) == null) {
                    return 1;
                }
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.FireBarrier);
                if (mbsvh != null) {
                    applier.duration = mbsvh.getLeftTime();
                }
                int value = applyto.getBuffedIntValue(SecondaryStat.FireBarrier) - 1;
                applier.localstatups.put(SecondaryStat.FireBarrier, Math.max(0, value));
                if (value <= 0) {
                    applier.overwrite = false;
                    applier.localstatups.clear();
                }
                return 1;
            }
            case 42101023: 
            case 42121023: {
                int n = applier.effect.getSourceId() == 42101023 ? 3 : 5;
                int count = 0;
                if (applyfrom.getParty() != null) {
                    count = applyfrom.getParty().getCharsInSameField(applyfrom).size();
                } else if (applyfrom == applyto) {
                    count = 1;
                }
                Integer value = applyto.getBuffedValue(SecondaryStat.BlessEnsenble);
                if (value != null) {
                    if ((count *= n) == value) {
                        return 0;
                    }
                    applier.localstatups.put(SecondaryStat.BlessEnsenble, count);
                    if (count <= 0) {
                        applier.overwrite = false;
                        applier.localstatups.clear();
                    }
                } else {
                    if (count <= 0) {
                        return 0;
                    }
                    applier.localstatups.put(SecondaryStat.BlessEnsenble, count * n);
                }
                return 1;
            }
            case 42101022: 
            case 42121022: {
                Item item = applyfrom.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-5200);
                if (item != null) {
                    Equip fan = (Equip)item;
                    applier.localstatups.put(SecondaryStat.FoxBless, (int)Math.floor((double)(fan.getTotalMad() * applier.effect.getStatups().get(SecondaryStat.FoxBless)) / 100.0));
                } else {
                    applier.localstatups.put(SecondaryStat.FoxBless, 0);
                }
                return 1;
            }
            case 42111103: {
                applier.localstatups.clear();
                return 1;
            }
            case 42121054: {
                if (applier.att) {
                    return 0;
                }
                return 1;
            }
            case 42121053: {
                if (applyfrom.getJob() / 1000 != applyto.getJob() / 1000) {
                    return 0;
                }
                applyto.dispelEffect(41121053);
                applyto.dispelEffect(42121053);
                return 1;
            }
            case 400021078: {
                if (applier.primary && !applier.passive) {
                    applier.localstatups.put(SecondaryStat.GhostLiberationStack, Math.min(5, applyto.getBuffedIntValue(SecondaryStat.GhostLiberationStack) + 1));
                    return 1;
                }
                return 0;
            }
            case 400021080: {
                if (applier.primary && !applier.passive) {
                    SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.GhostLiberationStack);
                    mbsvh.value = 0;
                    applyto.send(BuffPacket.giveBuff(applyto, mbsvh.effect, Collections.singletonMap(SecondaryStat.GhostLiberationStack, mbsvh.effect.getSourceId())));
                    return 1;
                }
                return 0;
            }
            case 400021054: {
                if (applier.att) {
                    return 0;
                }
                if (applier.passive) {
                    applyto.addHPMP(applier.effect.getW(), applier.effect.getY());
                }
                return 1;
            }
            case 400021114: {
                if (applier.att) {
                    return 0;
                }
                if (applyto.getSummonBySkillID(applier.effect.getSourceId()) != null) {
                    applyto.dispelEffect(applier.effect.getSourceId());
                    return 0;
                }
                SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.KannaFifthAttract);
                if (mbsvh != null && mbsvh.sourceID == 0) {
                    mbsvh.z = 0;
                }
                applier.maskedstatups.put(SecondaryStat.IndieNotDamaged, 1);
                applier.maskedstatups.put(SecondaryStat.IndieIgnorePCounter, 1);
                applier.maskedDuration = 2000;
                applier.b7 = false;
                applier.buffz = 0;
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        if (applier.effect != null) {
            MapleStatEffect skillEffect;
            if (applyfrom.getSkillEffect(42001101) != null && (this.is紫扇仰波(applier.effect.getSourceId()) || applier.effect.getSourceId() == 42101005 || applier.effect.getSourceId() == 42121002) && Randomizer.isSuccess(applyfrom.getSkillEffect(42001101).getW()) && applyfrom.getMap().getAffectedAreaObject(applyfrom.getId(), 42001101).size() < 3) {
                applyfrom.getSkillEffect(42001101).applyAffectedArea(applyfrom, applyto.getPosition());
            }
            if (applier.effect.getSourceId() == 42101005) {
                applier.effect.applyAffectedArea(applyfrom, applyto.getPosition());
            }
            if (applyto.isAlive() && applier.effect.getSourceId() != 42110002 && (skillEffect = applyfrom.getSkillEffect(42110002)) != null && skillEffect.makeChanceResult(applyfrom)) {
                applyfrom.getMap().broadcastMessage(applyfrom, ForcePacket.forceAtomCreate(MapleForceFactory.getInstance().getMapleForce(applyfrom, skillEffect, 0, applyto.getObjectId(), null, applyto.getPosition())), true);
            }
        }
        if (!applyto.isAlive()) {
            if (applyfrom.getSkillLevel(42100007) > 0 && (applier.effect == null || applier.effect != null && applier.effect.getSourceId() != 42100007)) {
                applyfrom.getClient().announce(MobPacket.enableSoulBomb(500, applyto.getPosition()));
            }
            if (applyfrom.getSkillLevel(42110008) > 0) {
                MapleStatEffect eff = applyfrom.getSkillEffect(42110008);
                int toHeal = 0;
                toHeal = applyto.isBoss() ? eff.getX() : applyfrom.getSkillLevel(42110008);
                applyfrom.addHPMP((int)((double)toHeal / 100.0 * (double)applyfrom.getStat().getCurrentMaxHP()), 0, false, true);
            }
        }
        return 1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (applier.effect != null) {
            switch (applier.effect.getSourceId()) {
                case 42001100: 
                case 42100000: 
                case 42110000: 
                case 42120001: {
                    if ((applier.ai.display & 0x10) == 0) break;
                    player.dispelEffect(applier.effect.getSourceId());
                }
            }
            if (!(!this.is紫扇仰波(applier.effect.getSourceId()) && applier.effect.getSourceId() != 42121000 || player.getSkillEffect(400021078) == null || player.isSkillCooling(400021078) || player.isSkillCooling(400021080))) {
                ExtraSkill eskill = new ExtraSkill(400021078, player.getPosition());
                eskill.Value = 1;
                eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
                player.send(MaplePacketCreator.RegisterExtraSkill(applier.effect.getSourceId(), Collections.singletonList(eskill)));
            }
            if (applier.effect.getSourceId() == 400021017) {
                player.addHPMP(applier.effect.getY(), 0);
            }
        }
        return 1;
    }

    @Override
    public int onAfterCancelEffect(MapleCharacter player, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 42111101: 
            case 42111102: {
                if (!applier.overwrite) {
                    player.removeSummon(42111103);
                }
                return 1;
            }
        }
        return -1;
    }
}

