/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.其他;

import Client.MapleCharacter;
import Client.MapleJob;
import Client.MonsterEffectHolder;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.status.MonsterStatus;
import Net.server.MapleStatInfo;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.life.MobSkill;
import Net.server.maps.MapleMapObject;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class 管理員
extends AbstractSkillHandler {
    public 管理員() {
        this.jobs = new MapleJob[]{MapleJob.管理員};
        for (Field field : Config.constants.skills.管理員.class.getDeclaredFields()) {
            try {
                this.skills.add(field.getInt(field.getName()));
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 9001000: {
                effect.setRangeBuff(true);
                effect.setHpR(1.0);
                return 1;
            }
            case 9001001: {
                effect.setRangeBuff(true);
                statups.put(SecondaryStat.Speed, effect.getSpeed());
                statups.put(SecondaryStat.Jump, effect.getJump());
                return 1;
            }
            case 9001002: {
                effect.setRangeBuff(true);
                statups.put(SecondaryStat.HolySymbol, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 9001003: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.IndiePAD, effect.getInfo().get((Object)MapleStatInfo.indiePad));
                statups.put(SecondaryStat.IndieMAD, effect.getInfo().get((Object)MapleStatInfo.indieMad));
                statups.put(SecondaryStat.IndieMHPR, effect.getInfo().get((Object)MapleStatInfo.indieMhpR));
                statups.put(SecondaryStat.IndieMMPR, effect.getInfo().get((Object)MapleStatInfo.indieMmpR));
                statups.put(SecondaryStat.PDD, effect.getInfo().get((Object)MapleStatInfo.pdd));
                statups.put(SecondaryStat.Speed, effect.getInfo().get((Object)MapleStatInfo.speed));
                return 1;
            }
            case 9001004: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.DarkSight, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 9001008: {
                effect.setRangeBuff(true);
                statups.put(SecondaryStat.MaxHP, effect.getInfo().get((Object)MapleStatInfo.x));
                statups.put(SecondaryStat.MaxMP, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyTo(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 9001000: {
                Rectangle rect = applier.effect.calculateBoundingBox(applyfrom.getPosition());
                for (MapleCharacter chr : applyfrom.getMap().getCharactersInRect(rect)) {
                    List<SecondaryStatValueHolder> mbsvhs = new LinkedList<>();
                    for (Map.Entry<SecondaryStat, List<SecondaryStatValueHolder>> entry : chr.getAllEffects().entrySet()) {
                        entry.getValue().stream().filter(mbsvh -> mbsvh.effect instanceof MobSkill).forEach(mbsvhs::add);
                    }
                    if (mbsvhs.size() > 0) {
                        mbsvhs.forEach(mbsvh -> chr.cancelEffect(mbsvh.effect, mbsvh.startTime));
                    }
                }
                for (MapleMapObject obj : applyfrom.getMap().getMonstersInRect(rect)) {
                    MapleMonster mob = (MapleMonster)obj;
                    LinkedList skills = new LinkedList();
                    for (List<MonsterEffectHolder> mehs : mob.getAllEffects().values()) {
                        mehs.stream().filter(meh -> meh.effect instanceof MobSkill && !skills.contains(meh.effect.getSourceId())).forEach(meh -> skills.add(meh.effect.getSourceId()));
                    }
                    Iterator<List<MonsterEffectHolder>> iterator = skills.iterator();
                    while (iterator.hasNext()) {
                        int skill = (Integer)((Object)iterator.next());
                        mob.removeEffect(0, skill);
                    }
                }
                return 1;
            }
            case 9001005: {
                for (MapleCharacter chr : applyfrom.getMap().getCharactersInRect(applier.effect.calculateBoundingBox(applyfrom.getPosition()))) {
                    if (applyfrom == chr || chr.isAlive()) continue;
                    chr.heal();
                }
                return 1;
            }
        }
        return -1;
    }
}

