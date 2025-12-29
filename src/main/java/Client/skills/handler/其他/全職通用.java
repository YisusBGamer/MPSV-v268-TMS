/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.其他;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.skills.handler.SkillClassFetcher;
import Client.status.MonsterStatus;
import Config.constants.skills.通用V核心;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Opcode.header.OutHeader;
import java.lang.reflect.Field;
import java.util.Map;
import tools.Randomizer;
import tools.data.MaplePacketLittleEndianWriter;
import tools.data.MaplePacketReader;

public class 全職通用
extends AbstractSkillHandler {
    public 全職通用() {
        for (Field field : 通用V核心.class.getDeclaredFields()) {
            try {
                this.skills.add(field.getInt(field.getName()));
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean containsJob(int jobWithSub) {
        return true;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 400001040: 
            case 400001041: {
                return 400001039;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 400001001: {
                statups.put(SecondaryStat.IndieBuffIcon, 1);
                return 1;
            }
            case 400001039: {
                effect.getInfo().put(MapleStatInfo.time, 50000);
                return 1;
            }
            case 400001002: {
                effect.setOverTime(true);
                statups.put(SecondaryStat.SharpEyes, (effect.getInfo().get((Object)MapleStatInfo.x) << 8) + effect.getInfo().get((Object)MapleStatInfo.y) + effect.getInfo().get((Object)MapleStatInfo.criticaldamageMax));
                return 1;
            }
            case 400001003: {
                effect.setOverTime(true);
                statups.put(SecondaryStat.MaxHP, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.MaxMP, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400001004: {
                effect.setOverTime(true);
                statups.put(SecondaryStat.CombatOrders, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400001005: {
                effect.setOverTime(true);
                statups.clear();
                statups.put(SecondaryStat.AdvancedBless, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.IndiePMdR, effect.getInfo().get((Object)MapleStatInfo.emad));
                statups.put(SecondaryStat.IndieMMP, effect.getInfo().get((Object)MapleStatInfo.indieMmp));
                return 1;
            }
            case 400001006: {
                effect.setOverTime(true);
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400001008: {
                monsterStatus.put(MonsterStatus.Freeze, 1);
                return 1;
            }
            case 400001020: {
                statups.put(SecondaryStat.HolySymbol, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 400001042: {
                statups.put(SecondaryStat.IndieMMP, effect.getInfo().get((Object)MapleStatInfo.damR));
                statups.put(SecondaryStat.IndieStatR, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 80002633: {
                effect.getInfo().put(MapleStatInfo.time, 10000);
                statups.put(SecondaryStat.BlackMageWeaponCreation, 1);
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                return 1;
            }
            case 80002632: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.BlackMageWeaponDestruction, 1);
                statups.put(SecondaryStat.IndiePMdR, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 80002644: {
                statups.put(SecondaryStat.BlackMageDestroy, 1);
                return 1;
            }
            case 400001059: {
                effect.getInfo().put(MapleStatInfo.time, 10000);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 400001007) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeOpcode(OutHeader.LP_RandomTeleportKey);
            mplew.write(Randomizer.nextInt(255));
            c.announce(mplew.getPacket());
            return 1;
        }
        return -1;
    }

    @Override
    public int onAfterRegisterEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 400001020) {
            SecondaryStatValueHolder mbsvh = applyto.getBuffStatValueHolder(SecondaryStat.HolySymbol, applier.effect.getSourceId());
            if (mbsvh != null) {
                mbsvh.DropRate = applier.effect.getV();
            }
            return 1;
        }
        return -1;
    }

    @Override
    public int onAttack(MapleCharacter player, MapleMonster monster, SkillClassApplier applier) {
        AbstractSkillHandler holder = SkillClassFetcher.getHandlerByJob(player.getJobWithSub());
        if (holder == this) {
            return -1;
        }
        return holder.onAttack(player, monster, applier);
    }

    @Override
    public int onApplyMonsterEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        AbstractSkillHandler holder = SkillClassFetcher.getHandlerByJob(applyfrom.getJobWithSub());
        if (holder == this) {
            return -1;
        }
        return holder.onApplyMonsterEffect(applyfrom, applyto, applier);
    }

    @Override
    public int onApplyAttackEffect(MapleCharacter applyfrom, MapleMonster applyto, SkillClassApplier applier) {
        AbstractSkillHandler holder = SkillClassFetcher.getHandlerByJob(applyfrom.getJobWithSub());
        if (holder == this) {
            return -1;
        }
        return holder.onApplyAttackEffect(applyfrom, applyto, applier);
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        AbstractSkillHandler holder = SkillClassFetcher.getHandlerByJob(player.getJobWithSub());
        if (holder == this) {
            return -1;
        }
        return holder.onAfterAttack(player, applier);
    }
}

