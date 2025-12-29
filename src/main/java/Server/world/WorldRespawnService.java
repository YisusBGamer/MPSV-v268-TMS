/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.inventory.PetDataFactory
 */
package Server.world;

import Client.MapleCharacter;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.force.MapleForceFactory;
import Client.inventory.MapleInventoryType;
import Client.inventory.MaplePet;
import Client.inventory.PetDataFactory;
import Client.skills.handler.冒險家.弓手類別.神射手;
import Client.skills.handler.冒險家.法師類別.主教;
import Client.skills.handler.冒險家.法師類別.冰雷大魔導士;
import Client.skills.handler.冒險家.海盜類別.拳霸;
import Client.stat.DeadDebuff;
import Config.configs.ServerConfig;
import Config.constants.JobConstants;
import Net.server.MapleItemInformationProvider;
import Net.server.Timer;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Net.server.maps.MapleRandomPortal;
import Net.server.maps.MapleRuneStone;
import Opcode.header.OutHeader;
import Packet.BuffPacket;
import Packet.EffectPacket;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import Server.channel.ChannelServer;
import Server.world.World;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Randomizer;
import tools.data.MaplePacketLittleEndianWriter;

public class WorldRespawnService {
    private static final Logger log = LoggerFactory.getLogger(WorldRespawnService.class);

    private WorldRespawnService() {
        Integer[] chs = ChannelServer.getAllInstance().toArray(new Integer[0]);
        for (int i = 1; i <= chs.length; ++i) {
            Timer.WorldTimer.getInstance().register(new Respawn(i), 1000L);
        }
    }

    public static WorldRespawnService getInstance() {
        return SingletonHolder.instance;
    }

    public static void handleMap(MapleMap map, int numTimes, int size, long now) {
        if (map.getItemsSize() > 0) {
            map.checkMapItemExpire(now);
        }
        for (MapleMapObject obj : map.getAllRandomPortalThreadsafe()) {
            MapleRandomPortal portal = (MapleRandomPortal)obj;
            if (portal.getStartTime() + portal.getDuration() > now) continue;
            map.disappearMapObject(obj);
        }
        if (size > 0 || map.getId() == 931000500) {
            map.handleMapObject();
            if (map.getCharacters().isEmpty()) {
                map.killAllMonsters(true);
                map.respawn(false, now);
            } else {
                map.respawn(true, now);
            }
            boolean hurt = map.canHurt(now);
            for (MapleCharacter chr : map.getCharacters()) {
                WorldRespawnService.handleCharacter(chr, numTimes, hurt, now);
            }
            if (map.getMobsSize() > 0) {
                for (MapleMonster mons : map.getMonsters()) {
                    if (mons.isAlive() && mons.shouldKill(now)) {
                        map.killMonster(mons);
                    }
                    if (!mons.isAlive()) continue;
                    mons.checkEffectExpiration();
                    map.updateMonsterController(mons);
                }
            }
        }
        if (size > 0) {
            long runeTime;
            if (map.getRunesSize() > 0 && (runeTime = System.currentTimeMillis() - ((MapleRuneStone)map.getAllRuneThreadsafe().getFirst()).getSpawnTime()) >= 240000L) {
                if (runeTime >= 300000L) {
                    if (numTimes % 10 == 0) {
                        int curseStage = (int)(runeTime / 300000L);
                        if ((curseStage = Math.min(4, Math.max(1, curseStage))) != map.getCurseRune().getCurseStage()) {
                            map.getCurseRune().setCurseStage(curseStage);
                            map.showRuneCurseStage();
                        }
                    }
                } else if (runeTime >= 290000L) {
                    int curseTime = 10 - (int)((runeTime - 290000L) / 1000L);
                    map.broadcastRuneCurseMessage(MaplePacketCreator.sendRuneCurseMsg("需要解放輪來解開精英Boss的詛咒！！\\n" + curseTime + "秒後地圖上開始精英Boss的詛咒！！"));
                } else if (runeTime <= 242000L) {
                    map.broadcastRuneCurseMessage(MaplePacketCreator.sendRuneCurseMsg("需要解放輪來解開精英Boss的詛咒！！\\n稍後就會開始菁英Boss的詛咒！！"));
                }
            }
            if (numTimes % 60 == 0) {
                map.updateBreakTimeField();
            }
            if (map.getOwner() != -1 && numTimes % 60 == 0) {
                long ownerTime = System.currentTimeMillis() - map.getOwnerStartTime();
                if (ownerTime >= 1800000L) {
                    map.setOwner(-1);
                } else if (ownerTime >= 1500000L) {
                    int ownerSurplusTime = 5 - (int)((ownerTime - 1500000L) / 60000L);
                    map.broadcastMessage(EffectPacket.showCombustionMessage("#fn哥德 ExtraBold##fs26#          " + ownerSurplusTime + "分鐘後解除防搶圖！！   ", 4000, -100));
                }
            }
        } else if (map.getBreakTimeFieldStep() > ServerConfig.MAX_BREAKTIMEFIELD_STEP || !map.isBreakTimeField() && map.getBreakTimeFieldStep() > 0) {
            map.setBreakTimeFieldStep(!map.isBreakTimeField() ? 0 : ServerConfig.MAX_BREAKTIMEFIELD_STEP);
        }
        if (numTimes % 30 == 0 && map.getAreaBroadcastMobId() > 0) {
            map.broadcastAreaMob(2);
        }
    }

    public static void handleCharacter(MapleCharacter chr, int numTimes, boolean hurt, long now) {
        MapleStatEffect effect;
        SecondaryStatValueHolder mbsvh;
        if (chr == null) {
            return;
        }
        if (numTimes % 5 == 0) {
            DeadDebuff.getDebuff(chr, 1);
        }
        if (numTimes % 3 == 0 && JobConstants.is冒險家法師(chr.getJob()) && (mbsvh = chr.getBuffStatValueHolder(SecondaryStat.Infinity)) != null) {
            ++mbsvh.value;
            chr.send(BuffPacket.giveBuff(chr, mbsvh.effect, Collections.singletonMap(SecondaryStat.Infinity, mbsvh.effect.getSourceId())));
            chr.addHPMP(mbsvh.effect.getY(), mbsvh.effect.getY());
        }
        if (JobConstants.is火毒(chr.getJob())) {
            if (numTimes % 4 == 0 && (effect = chr.getSkillEffect(2100009)) != null) {
                effect.unprimaryPassiveApplyTo(chr);
            }
        } else if (JobConstants.is主教(chr.getJob())) {
            if (numTimes % 5 == 0 && (effect = chr.getSkillEffect(2300009)) != null) {
                effect.unprimaryPassiveApplyTo(chr);
            }
            chr.checkTownPortalLeave();
        } else if (JobConstants.is神射手(chr.getJob())) {
            神射手.sendSnipeStatSet(chr);
        } else if (JobConstants.is拳霸(chr.getJob())) {
            拳霸.sendViperMark(chr);
        } else if (JobConstants.is夜光(chr.getJob()) && chr.isAlive()) {
            effect = chr.getSkillEffect(27110007);
            if (effect != null && chr.getStat().getLifeTidal() != chr.getBuffedIntValue(SecondaryStat.LifeTidal)) {
                effect.unprimaryPassiveApplyTo(chr);
            }
        } else if (JobConstants.is惡魔殺手(chr.getJob()) && chr.isAlive()) {
            if (numTimes % 4 == 0 && chr.getEffectForBuffStat(SecondaryStat.InfinityForce) != null && chr.getSkillEffect(31121054) != null && chr.isSkillCooling(31121054)) {
                chr.reduceSkillCooldown(31121054, 2000);
            }
        } else if (JobConstants.is狂豹獵人(chr.getJob())) {
            int min;
            String keyValue;
            if (chr.getEffectForBuffStat(SecondaryStat.JaguarSummoned) != null && chr.getCheatTracker().canNextPantherAttackS()) {
                chr.getClient().announce(MaplePacketCreator.openPantherAttack(false));
            }
            if (chr.getSkillEffect(33110014) != null && (keyValue = chr.getKeyValue("JaguarCount")) != null && (min = Math.min(6, Integer.valueOf(keyValue))) != chr.getBuffedIntValue(SecondaryStat.JaguarCount)) {
                chr.getSkillEffect(33110014).unprimaryPassiveApplyTo(chr);
            }
        } else if (JobConstants.is爆拳槍神(chr.getJob())) {
            effect = chr.getEffectForBuffStat(SecondaryStat.RWBarrier);
            if (effect != null) {
                effect.unprimaryPassiveApplyTo(chr);
            }
        } else if (JobConstants.is凱內西斯(chr.getJob())) {
            if (chr.getEffectForBuffStat(SecondaryStat.KinesisPsychicOver) != null) {
                chr.handlePPCount(2);
            }
        } else if (JobConstants.is伊利恩(chr.getJob()) && chr.getEffectForBuffStat(SecondaryStat.LefGloryWing) != null) {
            List<MapleMapObject> mapObjectsInRange = chr.getMap().getMapObjectsInRange(chr.getPosition(), 742.0, Collections.singletonList(MapleMapObjectType.MONSTER));
            List<Integer> moboids = mapObjectsInRange.stream().map(MapleMapObject::getObjectId).collect(Collectors.toList());
            effect = chr.getSkillEffect(152120017);
            if (effect != null) {
                chr.getMap().broadcastMessage(chr, ForcePacket.forceAtomCreate(MapleForceFactory.getInstance().getMapleForce(chr, effect, 0, moboids, chr.getPosition())), true);
            }
        } else if (JobConstants.is煉獄巫師(chr.getJob())) {
            effect = chr.getEffectForBuffStat(SecondaryStat.BMageAuraYellow);
            if (effect != null) {
                effect.applyToParty(chr.getMap(), chr);
            }
        } else if (JobConstants.is神之子(chr.getJob())) {
            effect = chr.getEffectForBuffStat(SecondaryStat.ZeroAuraStr);
            if (effect != null) {
                effect.applyToParty(chr.getMap(), chr);
            }
        } else if (JobConstants.is卡蒂娜(chr.getJob()) && (mbsvh = chr.getBuffStatValueHolder(SecondaryStat.WeaponVarietyFinale)) != null && mbsvh.effect != null && mbsvh.value < 3 && (effect = chr.getSkillEffect(mbsvh.effect.getSourceId())) != null && System.currentTimeMillis() - mbsvh.startTime >= (long)effect.getX() * 1000L) {
            mbsvh.value = Math.min(3, mbsvh.value + 1);
            mbsvh.startTime = System.currentTimeMillis();
            chr.send(BuffPacket.giveBuff(chr, effect, Collections.singletonMap(SecondaryStat.WeaponVarietyFinale, effect.getSourceId())));
        }
        if (numTimes % 4 == 0) {
            冰雷大魔導士.handleIceReiki(chr);
        }
        主教.handlePassive(chr, numTimes);
        if (chr.getBuffedValue(SecondaryStat.FinalCut) != null && chr.getCheatTracker().canNext絕殺刃()) {
            chr.getCheatTracker().setNext絕殺刃(0L);
            effect = chr.getSkillEffect(4341002);
            if (effect != null) {
                effect.applyBuffEffect(chr, chr, effect.getBuffDuration(chr), false, false, true, chr.getPosition());
            }
        }
        if (chr.getBuffedValue(SecondaryStat.GuidedBullet) != null && chr.getMap().getMobObject(chr.getLinkMobObjectID()) == null) {
            chr.setLinkMobObjectID(0);
            chr.dispelEffect(SecondaryStat.GuidedBullet);
        }
        if (chr.getBuffedValue(SecondaryStat.Curse) != null && chr.getMap().getMobObject(chr.getLinkMobObjectID()) == null) {
            chr.setLinkMobObjectID(0);
            chr.dispelEffect(SecondaryStat.Curse);
        }
        if (chr.getBuffedValue(SecondaryStat.Shadower_Assassination) != null && chr.getMap().getMobObject(chr.getBuffedIntZ(SecondaryStat.Shadower_Assassination)) == null) {
            chr.dispelEffect(SecondaryStat.Shadower_Assassination);
        }
        if (chr.getStat().mpcon_eachSecond != 0 && chr.getStat().getMp() <= 0) {
            if (chr.getBuffedValue(SecondaryStat.BMageAuraYellow) != null) {
                chr.dispelEffect(SecondaryStat.BMageAuraYellow);
            } else if (chr.getBuffedValue(SecondaryStat.IceAura) != null) {
                chr.dispelEffect(SecondaryStat.IceAura);
            } else if (chr.getBuffedValue(SecondaryStat.FireAura) != null) {
                chr.dispelEffect(SecondaryStat.FireAura);
            }
        }
        if (hurt && chr.isAlive() && chr.getInventory(MapleInventoryType.EQUIPPED).findById(chr.getMap().getProtectItem()) == null) {
            int n3;
            Integer value = chr.getBuffedValue(SecondaryStat.Thaw);
            int n = n3 = value == null ? 0 : value;
            if (chr.getMap().getDecHP() > 0) {
                chr.addHPMP(-Math.max(0, chr.getMap().getDecHP() - n3), 0, false, false);
            }
            if (chr.getMap().getDecHPr() > 0) {
                chr.addHPMP(-chr.getMap().getDecHPr(), 0);
            }
        }
        if (chr.isAlive()) {
            List<MapleMapObject> objs;
            chr.doHealPerTime();
            if (chr.getLevel() >= 200) {
                chr.check5thJobQuest();
            }
            if (chr.canFairy(now)) {
                chr.doFairy();
            }
            if (chr.canExpiration(now)) {
                chr.expirationTask(false);
            }
            if (numTimes % 5 == 0) {
                MapleStatEffect eff;
                chr.checkFairy();
                if (chr.getBuffStatValueHolder(80011248) != null) {
                    chr.addHPMP(-5, 0);
                }
                if ((eff = chr.getSkillEffect(162120037)) != null && chr.getBuffStatValueHolder(162110007) != null) {
                    chr.addHPMP(eff.getW(), 0);
                }
            }
            if (numTimes % 2 == 0 && chr.getBuffStatValueHolder(80001756) != null && (objs = chr.getMap().getMonstersInRange(chr.getPosition(), 196.0)) != null && objs.size() > 0) {
                MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                mplew.writeShort(OutHeader.LP_UserRandAreaAttackRequest.getValue());
                mplew.writeInt(80001762);
                mplew.writeInt(1);
                mplew.writePosInt(objs.get(0).getPosition());
                mplew.writeInt(objs.get(0).getObjectId());
                mplew.writeInt(500);
                chr.send(mplew.getPacket());
            }
        }
        if (numTimes % 2 == 0 && chr.getInnerStormValue() > 0) {
            chr.checkInnerStormValue();
        }
        if (chr.getBuffedValue(SecondaryStat.ChangeFoxMan) == null) {
            if (chr.getBuffStatValueHolder(SecondaryStat.BlessEnsenble, 42101023) != null) {
                chr.dispelBuff(42101023);
            }
            if (chr.getBuffStatValueHolder(SecondaryStat.BlessEnsenble, 42121023) != null) {
                chr.dispelBuff(42121023);
            }
        }
        if ((mbsvh = chr.getBuffStatValueHolder(SecondaryStat.FoxBless)) != null) {
            boolean dispel;
            if (chr.getId() == mbsvh.fromChrID) {
                dispel = chr.getBuffedValue(SecondaryStat.ChangeFoxMan) == null;
            } else {
                MapleCharacter fromChr;
                boolean bl = dispel = chr.getParty() == null || chr.getParty().getPartyMemberByID(mbsvh.fromChrID) == null || (fromChr = chr.getMap().getPlayerObject(mbsvh.fromChrID)) == null || fromChr.getBuffedValue(SecondaryStat.ChangeFoxMan) == null;
            }
            if (dispel) {
                chr.dispelEffect(SecondaryStat.FoxBless);
            }
        }
        if (numTimes % 7 == 0 && chr.getMount() != null && chr.getMount().canTire(now)) {
            chr.getMount().increaseFatigue();
        }
        if (ServerConfig.JMS_SOULWEAPON_SYSTEM && numTimes % 10 == 0 && chr.checkSoulWeapon() && now - 600000L >= chr.getLastFullSoulMP() && chr.getSoulMP() > 0) {
            chr.addSoulMP(-Randomizer.rand(10, 11));
        }
        if (numTimes % 60 == 0) {
            for (MaplePet pet : chr.getSummonedPets()) {
                int newFullness;
                if (MapleItemInformationProvider.getInstance().getLimitedLife(pet.getPetItemId()) > 0) {
                    pet.setSecondsLeft(Math.max(pet.getSecondsLeft() - 60, 0));
                    if (pet.getSecondsLeft() == 0) {
                        chr.unequipSpawnPet(pet, true, (byte)2);
                        return;
                    }
                }
                if ((newFullness = pet.getFullness() - PetDataFactory.getHunger((int)pet.getPetItemId())) <= 5) {
                    pet.setFullness(15);
                    chr.unequipSpawnPet(pet, true, (byte)1);
                    continue;
                }
                pet.setFullness(newFullness);
                chr.petUpdateStats(pet, true);
            }
        }
    }

    private static class Respawn
    implements Runnable {
        private final ChannelServer cserv;
        private int numTimes = 0;

        public Respawn(int ch) {
            this.cserv = ChannelServer.getInstance(ch);
        }

        @Override
        public void run() {
            ++this.numTimes;
            long now = System.currentTimeMillis();
            if (this.numTimes % 60 == 0) {
                World.updateHoliday();
            }
            if (!this.cserv.hasFinishedShutdown()) {
                for (MapleMap map : this.cserv.getMapFactory().getAllLoadedMaps()) {
                    WorldRespawnService.handleMap(map, this.numTimes, map.getCharacters().size(), now);
                }
            }
        }
    }

    private static class SingletonHolder {
        protected static final WorldRespawnService instance = new WorldRespawnService();

        private SingletonHolder() {
        }
    }
}

