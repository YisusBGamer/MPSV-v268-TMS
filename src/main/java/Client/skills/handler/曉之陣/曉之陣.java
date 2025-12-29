/*
 * Decompiled with CFR 0.152.
 */
package Client.skills.handler.曉之陣;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.skills.handler.SkillClassFetcher;
import Config.constants.JobConstants;
import Config.constants.skills.通用V核心;
import Net.server.life.MapleMonster;
import java.awt.Point;
import java.lang.reflect.Field;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class 曉之陣
extends AbstractSkillHandler {
    public 曉之陣() {
        for (Field field : 通用V核心.曉之陣通用.class.getDeclaredFields()) {
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
        return JobConstants.is曉の陣(jobWithSub);
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 400001032: 
            case 400001033: 
            case 400001034: 
            case 400001035: {
                return 400001031;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        if (applier.effect.getSourceId() == 400001031) {
            int id2;
            int[] array6 = new int[]{400001032, 400001033, 400001034, 400001035};
            int id1 = 400001032 + Randomizer.nextInt(4);
            for (int l = 400001032; l <= 400001035; ++l) {
                chr.dispelEffect(l);
            }
            while ((id2 = 400001032 + Randomizer.nextInt(4)) == id1) {
            }
            applier.effect = chr.getSkillEffect(id1);
            chr.getSkillEffect(id2).applyTo(chr, new Point(chr.getPosition().x + 50, chr.getPosition().y));
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

