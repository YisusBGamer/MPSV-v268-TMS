/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.maps.ForceAtomObject
 *  Packet.AdelePacket
 */
package Client.skills.handler.超新星;

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
import Net.server.maps.ForceAtomObject;
import Packet.AdelePacket;
import Packet.MaplePacketCreator;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import tools.Pair;
import tools.data.MaplePacketReader;

public class 凱殷
extends AbstractSkillHandler {
    public 凱殷() {
        this.jobs = new MapleJob[]{MapleJob.凱殷, MapleJob.凱殷1轉, MapleJob.凱殷2轉, MapleJob.凱殷3轉, MapleJob.凱殷4轉};
        for (Field field : Config.constants.skills.凱殷.class.getDeclaredFields()) {
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
        for (int i : ss = new int[]{60031000, 60031005}) {
            Skill skil;
            if (chr.getLevel() < 200 && i == 60031005 || (skil = SkillFactory.getSkill(i)) == null || chr.getSkillLevel(skil) > 0) continue;
            applier.skillMap.put(i, new SkillEntry(1, skil.getMaxMasterLevel(), -1L));
        }
        return -1;
    }

    @Override
    public int getLinkedSkillID(int skillId) {
        switch (skillId) {
            case 63141502: 
            case 63141503: {
                return 63141500;
            }
            case 63141000: {
                return 63121002;
            }
            case 500004140: {
                return 400031061;
            }
            case 500004141: {
                return 400031065;
            }
            case 500004142: {
                return 400031062;
            }
            case 500004143: {
                return 400031066;
            }
            case 63141004: 
            case 63141005: 
            case 63141006: {
                return 63121006;
            }
            case 63001001: 
            case 63001100: {
                return 63001000;
            }
            case 63001003: 
            case 63001005: {
                return 63001002;
            }
            case 63001009: {
                return 63001006;
            }
            case 63100100: 
            case 63101003: 
            case 63101100: {
                return 63100002;
            }
            case 63100104: 
            case 63101104: {
                return 63101004;
            }
            case 63101006: {
                return 63101005;
            }
            case 63111002: {
                return 63110001;
            }
            case 63110103: 
            case 63111004: 
            case 63111005: 
            case 63111103: 
            case 63111104: 
            case 63111105: 
            case 63111106: {
                return 63111003;
            }
            case 63111010: {
                return 63111009;
            }
            case 63111012: 
            case 63111013: {
                return 63110011;
            }
            case 63120102: 
            case 63121102: 
            case 63121103: {
                return 63121002;
            }
            case 63121005: {
                return 63121004;
            }
            case 63121007: {
                return 63121006;
            }
            case 63120140: 
            case 63121041: 
            case 63121140: 
            case 63121141: {
                return 63121040;
            }
            case 400031063: 
            case 400031064: {
                return 400031062;
            }
        }
        return -1;
    }

    @Override
    public int onSkillLoad(Map<SecondaryStat, Integer> statups, Map<MonsterStatus, Integer> monsterStatus, MapleStatEffect effect) {
        switch (effect.getSourceId()) {
            case 60031005: {
                effect.setRangeBuff(true);
                effect.getInfo().put(MapleStatInfo.time, effect.getDuration() * 1000);
                statups.put(SecondaryStat.MaxLevelBuff, effect.getX());
                return 1;
            }
            case 60030241: 
            case 80003015: {
                statups.put(SecondaryStat.IndieDamR, effect.getY());
                return 1;
            }
            case 80003018: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.NALinkSkill, 1);
                return 1;
            }
            case 63001002: {
                effect.getInfo().put(MapleStatInfo.time, effect.getSubTime());
                statups.put(SecondaryStat.DarkSight, effect.getLevel());
                return 1;
            }
            case 63101001: {
                statups.put(SecondaryStat.NADragonEnchant, 1);
                return 1;
            }
            case 63101005: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.NABrutalPang, 1);
                return 1;
            }
            case 63101010: {
                statups.put(SecondaryStat.Booster, effect.getInfo().get((Object)MapleStatInfo.x));
                return 1;
            }
            case 63111009: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.NARemainIncense, 1);
                return 1;
            }
            case 63121008: {
                effect.getInfo().put(MapleStatInfo.time, effect.getY());
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                return 1;
            }
            case 63121044: {
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                statups.put(SecondaryStat.IndieStance, effect.getInfo().get((Object)MapleStatInfo.indieStance));
                statups.put(SecondaryStat.IndiePADR, effect.getInfo().get((Object)MapleStatInfo.indiePadR));
                statups.put(SecondaryStat.NovaArcherIncanation, 1);
                return 1;
            }
            case 400031062: {
                statups.put(SecondaryStat.IndieDamR, effect.getInfo().get((Object)MapleStatInfo.indieDamR));
                statups.put(SecondaryStat.IndieStance, effect.getInfo().get((Object)MapleStatInfo.indieStance));
                statups.put(SecondaryStat.NAThanatosDescent, 1);
                return 1;
            }
            case 400031064: {
                effect.getInfo().put(MapleStatInfo.cooltime, 180);
                statups.put(SecondaryStat.IndieNotDamaged, 1);
                return 1;
            }
            case 400031066: {
                effect.getInfo().put(MapleStatInfo.time, 2100000000);
                statups.put(SecondaryStat.NAOminousStream, 1);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onSkillUse(MaplePacketReader slea, MapleClient c, MapleCharacter chr, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 63001003: {
                MapleStatEffect effect = chr.getSkillEffect(63001002);
                if (effect != null) {
                    applier.effect = effect;
                }
                return 1;
            }
            case 63101001: {
                if (chr.getSpecialStat().getMaliceCharge() < 100) {
                    chr.dropMessage(5, "沒有準備任何的惡意之石。");
                    return 0;
                }
                if (chr.hasBuffSkill(63101001)) {
                    chr.dropMessage(5, "已經是主導狀態。");
                    return 0;
                }
                chr.handleMaliceCharge(-100);
                return 1;
            }
            case 63101005: {
                if (chr.hasBuffSkill(applier.effect.getSourceId())) {
                    Map<Integer, ForceAtomObject> swordsMap = chr.getForceAtomObjects();
                    ArrayList<ForceAtomObject> removeList = new ArrayList<ForceAtomObject>();
                    Iterator<Map.Entry<Integer, ForceAtomObject>> iterator = swordsMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Integer, ForceAtomObject> sword = iterator.next();
                        if (sword.getValue().SkillId != 63101006) continue;
                        removeList.add(sword.getValue());
                        iterator.remove();
                    }
                    if (!removeList.isEmpty()) {
                        chr.getMap().broadcastMessage(AdelePacket.ForceAtomObjectRemove((int)chr.getId(), removeList, (int)1), chr.getPosition());
                    }
                    chr.dispelEffect(applier.effect.getSourceId());
                    return 0;
                }
                return 1;
            }
            case 63111009: {
                if (chr.hasBuffSkill(applier.effect.getSourceId())) {
                    chr.dispelEffect(applier.effect.getSourceId());
                    return 0;
                }
                return 1;
            }
            case 63121002: {
                Pair skillInfo;
                int timeout;
                int maxValue;
                MapleStatEffect effect = chr.getSkillEffect(63121002);
                if (effect != null) {
                    maxValue = effect.getW();
                    timeout = effect.getU() * 1000;
                    skillInfo = (Pair)chr.getTempValues().get("MultiSkill63121002");
                    if (skillInfo != null) {
                        Pair pair = skillInfo;
                        pair.left = (Integer)pair.left - 1;
                        if ((Integer)skillInfo.left < 0) {
                            skillInfo.left = 0;
                        }
                    } else {
                        return 0;
                    }
                    skillInfo.right = System.currentTimeMillis();
                } else {
                    return 0;
                }
                chr.getTempValues().put("MultiSkill63121002", skillInfo);
                chr.send(MaplePacketCreator.multiSkillInfo(63121002, (Integer)skillInfo.left, maxValue, timeout));
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onApplyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, SkillClassApplier applier) {
        switch (applier.effect.getSourceId()) {
            case 60031000: {
                applyto.changeMap(applier.effect.getX(), 0);
                return 1;
            }
            case 400031062: {
                applyfrom.cancelSkillCooldown(400031064);
                return 1;
            }
            case 400031064: {
                if (!applier.primary && !applier.passive) {
                    return 0;
                }
                applyfrom.dispelEffect(400031062);
                return 1;
            }
            case 400031066: {
                if (applier.primary && !applier.passive) {
                    SecondaryStatValueHolder mbsvh = applyfrom.getBuffStatValueHolder(400031066);
                    if (mbsvh != null) {
                        ForceAtomObject sword = new ForceAtomObject(1, 18, 0, applyfrom.getId(), 0, 400031066);
                        sword.EnableDelay = 990;
                        sword.Expire = mbsvh.effect.getS2() * 1000 + mbsvh.value * mbsvh.effect.getS() * 1000;
                        Point pt = new Point(applyfrom.getPosition());
                        sword.Position = new Point(pt.x - 102, pt.y - 456);
                        sword.ObjPosition = new Point(pt.x, pt.y);
                        applyfrom.dispelEffect(400031066);
                        applyfrom.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)applyfrom.getId(), Collections.singletonList(sword), (int)0), applyfrom.getPosition());
                    }
                    return 0;
                }
                return 1;
            }
            case 400031061: 
            case 400031065: {
                if (applyfrom.getCooldownLeftTime(applier.effect.getSourceId()) == 0) {
                    int mpCon = applier.effect.getMpCon();
                    applyfrom.addMP(-mpCon);
                }
            }
            case 63111007: 
            case 63121004: 
            case 63121006: {
                applyfrom.cancelSkillCooldown(63001002);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int onAfterAttack(MapleCharacter player, SkillClassApplier applier) {
        if (applier.totalDamage > 0L && (applier.ai.skillId == 63001000 || applier.ai.skillId == 63100002 || applier.ai.skillId == 63110001)) {
            ExtraSkill eskill = new ExtraSkill(63001001, new Point(applier.ai.mobAttackInfo.get((int)0).hitX, applier.ai.mobAttackInfo.get((int)0).hitY));
            eskill.Value = 1;
            eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
            player.send(MaplePacketCreator.RegisterExtraSkill(applier.ai.skillId, Collections.singletonList(eskill)));
        }
        if (applier.totalDamage > 0L && this.containsJob(applier.ai.skillId / 10000)) {
            SecondaryStatValueHolder mbsvh;
            MapleStatEffect effect = player.getSkillEffect(63120000);
            if (effect != null || (effect = player.getSkillEffect(63101001)) != null) {
                player.handleMaliceCharge(effect.getX());
            }
            if ((mbsvh = player.getBuffStatValueHolder(SecondaryStat.NABrutalPang)) != null && mbsvh.effect != null) {
                int attackCount = (Integer)player.getTempValues().getOrDefault("龍炸裂攻擊次數", 0) + 1;
                player.getTempValues().put("龍炸裂攻擊次數", attackCount);
                if (attackCount >= 5) {
                    player.getTempValues().put("龍炸裂攻擊次數", 0);
                    Map<Integer, ForceAtomObject> swordsMap = player.getForceAtomObjects();
                    ForceAtomObject sword = null;
                    LinkedList<Integer> objList = new LinkedList<Integer>();
                    for (int i = 0; i < 3; ++i) {
                        for (ForceAtomObject obj : swordsMap.values()) {
                            if (obj.SkillId != 63101006 || objList.contains(obj.Idx)) continue;
                            objList.add(obj.Idx);
                            break;
                        }
                        if (objList.size() < i + 1) {
                            sword = new ForceAtomObject(player.getSpecialStat().gainForceCounter(), 17, i, player.getId(), 0, 63101006);
                            sword.Position = new Point(0, 50);
                            sword.ObjPosition = new Point(0, 0);
                            sword.Expire = mbsvh.effect.getW() * 1000;
                            sword.ValueList.add(1);
                            swordsMap.put(sword.Idx, sword);
                            break;
                        }
                        sword = null;
                    }
                    if (sword != null) {
                        player.getMap().broadcastMessage(AdelePacket.ForceAtomObject((int)player.getId(), Collections.singletonList(sword), (int)0), player.getPosition());
                    }
                }
            }
        }
        if (applier.totalDamage > 0L && player.getBuffStatValueHolder(SecondaryStat.NABrutalPang) != null && System.currentTimeMillis() - (Long)player.getTempValues().getOrDefault("龍炸裂攻擊冷卻", 0L) >= 3000L) {
            Map<Integer, ForceAtomObject> swordsMap = player.getForceAtomObjects();
            boolean attack = false;
            for (ForceAtomObject sword : swordsMap.values()) {
                if (sword.SkillId != 63101006) continue;
                player.getMap().broadcastMessage(AdelePacket.ForceAtomObjectAttack((int)player.getId(), (int)sword.Idx, (int)1), player.getPosition());
                attack = true;
            }
            if (attack) {
                player.getTempValues().put("龍炸裂攻擊冷卻", System.currentTimeMillis());
            }
        }
        return 1;
    }
}

