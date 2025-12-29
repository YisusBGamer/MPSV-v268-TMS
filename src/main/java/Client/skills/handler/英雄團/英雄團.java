/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.英雄團;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.SecondaryStat;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.skills.handler.SkillClassFetcher;
import Client.status.MonsterStatus;
import Config.constants.JobConstants;
import Config.constants.skills.通用V核心;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import java.lang.reflect.Field;
import java.util.Map;
import tools.data.MaplePacketReader;

public class 英雄團
extends AbstractSkillHandler {
    public 英雄團() {
        for (Field field : 通用V核心.英雄團通用.class.getDeclaredFields()) {
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
        return JobConstants.is英雄團(jobWithSub);
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 400001025: 
            case 400001026: 
            case 400001027: 
            case 400001028: 
            case 400001029: 
            case 400001030: {
                return 400001024;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 400001025: 
            case 400001026: 
            case 400001027: 
            case 400001028: 
            case 400001029: {
                statups.put(SecondaryStat.FreudBlessing, effect.getSourceId() % 10 - 4);
                statups.put(SecondaryStat.IndieCooltimeReduce, effect.getInfo().get((Object)MapleStatInfo.indieCooltimeReduce));
                statups.put(SecondaryStat.IndieStance, effect.getInfo().get((Object)MapleStatInfo.indieStance));
                statups.put(SecondaryStat.IndieAllStat, effect.getInfo().get((Object)MapleStatInfo.indieAllStat));
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                statups.put(SecondaryStat.IndieMAD, effect.getInfo().get((Object)MapleStatInfo.indieMad));
                statups.put(SecondaryStat.IndieBDR, effect.getInfo().get((Object)MapleStatInfo.indieBDR));
                return 1;
            }
            case 400001030: {
                statups.clear();
                statups.put(SecondaryStat.FreudBlessing, 6);
                statups.put(SecondaryStat.IndieCooltimeReduce, effect.getInfo().get((Object)MapleStatInfo.indieCooltimeReduce));
                statups.put(SecondaryStat.IndieStance, effect.getInfo().get((Object)MapleStatInfo.indieStance));
                statups.put(SecondaryStat.IndieAllStat, effect.getInfo().get((Object)MapleStatInfo.indieAllStat));
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                statups.put(SecondaryStat.IndieMAD, effect.getInfo().get((Object)MapleStatInfo.indieMad));
                statups.put(SecondaryStat.IndieBDR, effect.getInfo().get((Object)MapleStatInfo.indieBDR));
                statups.put(SecondaryStat.NotDamaged, 1);
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 400001024: 
            case 400001025: 
            case 400001026: 
            case 400001027: 
            case 400001028: 
            case 400001029: {
                MapleStatEffect eff = chr.getSkillEffect(400001024);
                if (eff != null && !chr.isSkillCooling(eff.getSourceId())) {
                    chr.registerSkillCooldown(eff.getSourceId(), eff.getCooldown(chr), true);
                }
                return 1;
            }
            case 400001030: {
                MapleStatEffect eff = chr.getSkillEffect(400001024);
                if (eff == null) {
                    return 1;
                }
                if (!chr.isSkillCooling(eff.getSourceId())) {
                    chr.registerSkillCooldown(eff.getSourceId(), eff.getY() * 1000, true);
                    return 1;
                }
                if (chr.isSkillCooling(eff.getSourceId())) {
                    return 0;
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 400001025: 
            case 400001026: 
            case 400001027: 
            case 400001028: 
            case 400001029: 
            case 400001030: {
                applyto.dispelEffect(SecondaryStat.FreudBlessing);
                return 1;
            }
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

