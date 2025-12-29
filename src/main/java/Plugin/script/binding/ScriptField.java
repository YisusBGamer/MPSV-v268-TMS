/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Plugin.script.binding.ScriptMob
 *  Server.BossEventHandler.Angel
 *  Server.BossEventHandler.BlackMage
 *  Server.BossEventHandler.Caning
 *  Server.BossEventHandler.Demian.Demian
 *  Server.BossEventHandler.Dusk.Dusk
 *  Server.BossEventHandler.Jin.JinHillah
 *  Server.BossEventHandler.Lucid
 *  Server.BossEventHandler.Seren
 *  Server.BossEventHandler.Will
 *  Server.BossEventHandler.kalos
 *  SwordieX.field.ClockPacket
 *  SwordieX.field.fieldeffect.FieldEffect
 *  connection.packet.FieldPacket
 *  lombok.Generated
 */
package Plugin.script.binding;

import Client.MapleCharacter;
import Net.server.life.MapleLifeFactory;
import Net.server.life.MapleMonster;
import Net.server.maps.MapleMap;
import Opcode.header.OutHeader;
import Packet.MaplePacketCreator;
import Plugin.script.binding.ScriptEvent;
import Plugin.script.binding.ScriptMob;
import Plugin.script.binding.ScriptPlayer;
import Server.BossEventHandler.Angel;
import Server.BossEventHandler.BlackMage;
import Server.BossEventHandler.Caning;
import Server.BossEventHandler.Demian.Demian;
import Server.BossEventHandler.Dusk.Dusk;
import Server.BossEventHandler.Jin.JinHillah;
import Server.BossEventHandler.Lucid;
import Server.BossEventHandler.Seren;
import Server.BossEventHandler.Will;
import Server.BossEventHandler.kalos;
import SwordieX.field.ClockPacket;
import SwordieX.field.fieldeffect.FieldEffect;
import connection.packet.FieldPacket;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.data.MaplePacketLittleEndianWriter;

public class ScriptField {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ScriptField.class);
    private final MapleMap map;
    private List<MapleCharacter> characters;

    public ScriptField(MapleMap map) {
        this.map = map;
        this.characters = new ArrayList<MapleCharacter>(map.getCharacters());
    }

    public void reset() {
        this.getMap().killAllMonsters(false);
        this.getMap().reloadReactors();
        this.getMap().resetNPCs();
        this.getMap().resetSpawns();
        this.getMap().resetPortals();
        this.getMap().removeDrops();
        this.getMap().setUserFirstEnter(false);
    }

    public int getMonsterSize() {
        return this.getMap().getMonsters().size();
    }

    public void startDojangRandTimer(int sec, int wait) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.CTX_Event_Field_Timer.getValue());
        mplew.write(7);
        mplew.write(1);
        mplew.writeInt(sec);
        mplew.writeInt(wait);
        this.getMap().broadcastMessage(mplew.getPacket());
    }

    public void spawnRune(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_RuneStoneAppear.getValue());
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(2);
        mplew.writeInt(type);
        mplew.writeInt((int)((MapleCharacter)this.getMap().getAllChracater().getFirst()).getPosition().getX());
        mplew.writeInt((int)((MapleCharacter)this.getMap().getAllChracater().getFirst()).getPosition().getY());
        mplew.write(0);
        this.getMap().broadcastMessage(mplew.getPacket());
    }

    public void reset(int level) {
        this.getMap().resetPQ(level);
    }

    public void overrideFieldLimit(int var) {
        this.getMap().setFieldLimit(var);
    }

    public void showWeatherEffectNotice(String msg, int type, int duration) {
        this.getMap().showWeatherEffectNotice(msg, type, duration);
    }

    public void changeBGM(String name) {
        FieldPacket.fieldEffect((FieldEffect)FieldEffect.changeBGM((String)name, (int)0, (int)0, (int)0));
    }

    public void clearMobs() {
        this.getMap().killAllMonsters(true);
    }

    public void clearDrops() {
        this.getMap().removeDrops();
    }

    public void createObtacleAtom(int count, int type1, int type2, int DamageRang, int SpeedRang) {
        this.getMap().getAllChracater().forEach(chr -> chr.send(MaplePacketCreator.createObtacleAtom(count, type1, type2, this.getMap())));
    }

    public void destroyTempNpc(int npcId) {
        this.getMap().removeNpc(npcId);
    }

    public void spawnTempNpc(int npcId, int x, int y) {
        this.getMap().spawnNpc(npcId, new Point(x, y));
    }

    public int getId() {
        return this.getMap().getId();
    }

    public int getInstanceId() {
        return this.getMap().getInstanceId();
    }

    public int getNumPlayersInArea(int id) {
        return this.getMap().getNumPlayersInArea(id);
    }

    public int getPlayerCount() {
        return this.getMap().getAllChracater().size();
    }

    public ScriptMob makeMob(int mobId) {
        MapleMonster Mob = MapleLifeFactory.getMonster(mobId);
        return new ScriptMob(Mob);
    }

    public void spawnMob(int mobId, int x, int y) {
        MapleMonster Mob = MapleLifeFactory.getMonster(mobId);
        this.getMap().spawnMonsterOnGroundBelow(Mob, new Point(x, y));
    }

    public void spawnMob(ScriptMob Mob, int x, int y) {
        this.getMap().spawnMonsterOnGroundBelow(Mob.getMob(), new Point(x, y));
    }

    public void portalEffect(String name, int i) {
        this.getMap().showPortalEffect(name, i);
    }

    public void resetMobsSpawns() {
        this.getMap().resetSpawns();
    }

    public void screenEffect(String name) {
        this.getMap().showScreenEffect(name);
    }

    public void scriptProgressMessage(String msg) {
        this.getMap().showScriptProgressMessage(msg);
    }

    public void setNoSpawn(boolean value) {
        this.getMap().setSpawns(value);
    }

    public void showTimer(double seconds) {
        long sec = (long)Math.ceil(seconds * 1000.0);
        this.getMap().getAllChracater().forEach(chr -> chr.getClient().announce(FieldPacket.clock((ClockPacket)ClockPacket.secondsClock((long)sec))));
    }

    public void closeTimer() {
        this.getMap().getAllChracater().forEach(chr -> chr.getClient().announce(FieldPacket.clock((ClockPacket)ClockPacket.secondsClock((long)-1L))));
    }

    public List<ScriptPlayer> getPlayers() {
        return this.getMap().getAllChracater().stream().map(ScriptPlayer::new).collect(Collectors.toList());
    }

    public void endFieldEvent() {
        this.getMap().endFieldEvent();
    }

    public void getName() {
        this.getMap().getMapName();
    }

    public void setReactorState(String name, byte state) {
        this.getMap().setReactorState(name, state);
    }

    public int getReactorStateId(String var1) {
        return this.getMap().getReactorStat(var1);
    }

    public int getEventMobCount() {
        return this.getMap().getMonsters().size();
    }

    public int getEventMobCountById(int mobId) {
        List<MapleMonster> monsters = this.getMap().getMonsters();
        int count = 0;
        for (MapleMonster monster : monsters) {
            if (monster.getId() != mobId) continue;
            ++count;
        }
        return count;
    }

    public void blowWeather(int itemId, String msg, int time) {
        this.getMap().startMapEffect(msg, itemId, time);
    }

    public void blowWeather(int itemId, String msg) {
        this.getMap().startMapEffect(msg, itemId, 3000);
    }

    public ScriptEvent getEvent() {
        return this.getMap().getEvent();
    }

    public void setEvent(ScriptEvent event) {
        this.getMap().setEvent(event);
    }

    public List<MapleCharacter> getCharacters() {
        return this.getMap().getCharacters();
    }

    public void startFieldEvent() {
        for (MapleCharacter chr : this.map.getAllCharactersThreadsafe()) {
            chr.getMap().startFieldEvent();
        }
    }

    public void startDemianField() {
        for (MapleCharacter chr : this.getCharacters()) {
            Demian.start((MapleCharacter)chr);
        }
    }

    public void startLucidField() {
        for (MapleCharacter chr : this.getCharacters()) {
            Lucid.start((MapleCharacter)chr);
        }
    }

    public void startDuskField() {
        for (MapleCharacter chr : this.getCharacters()) {
            Dusk.start((MapleCharacter)chr);
        }
    }

    public void startJinField() {
        for (MapleCharacter chr : this.getCharacters()) {
            JinHillah.start((MapleCharacter)chr);
        }
    }

    public void startAngelField() {
        for (MapleCharacter chr : this.getCharacters()) {
            Angel.start((MapleCharacter)chr);
        }
    }

    public void startWillField() {
        for (MapleCharacter chr : this.getCharacters()) {
            Will.start((MapleCharacter)chr);
        }
    }

    public void startSerenField() {
        for (MapleCharacter chr : this.getCharacters()) {
            Seren.start((MapleCharacter)chr, (MapleMonster)((MapleMonster)chr.getMap().getMonsters().getFirst()));
        }
    }

    public void startKalosField() {
        for (MapleCharacter chr : this.getCharacters()) {
            kalos.start((MapleCharacter)chr);
        }
    }

    public void startBlackMageField() {
        for (MapleCharacter chr : this.getCharacters()) {
            BlackMage.start((MapleCharacter)chr);
        }
    }

    public void startBlackMageField_II() {
        for (MapleCharacter chr : this.getCharacters()) {
            BlackMage.start2((MapleCharacter)chr);
        }
    }

    public void startBlackMageField_III() {
        for (MapleCharacter chr : this.getCharacters()) {
            BlackMage.start3((MapleCharacter)chr);
        }
    }

    public void startBlackMageField_IV() {
        for (MapleCharacter chr : this.getCharacters()) {
            BlackMage.start4((MapleCharacter)chr);
        }
    }

    public void StartKarNingField() {
        for (MapleCharacter player : this.getCharacters()) {
            Caning.startEvent_Field((MapleCharacter)player);
        }
    }

    public void StartFieldMessage(int type, String message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_BlowWeather.getValue());
        mplew.write(0);
        mplew.writeInt(type);
        mplew.writeMapleAsciiString(message);
        mplew.write(15);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        for (MapleCharacter chr : this.getCharacters()) {
            chr.send(mplew.getPacket());
        }
    }

    public void showMoviePath(String intro, String path, String animation, int str) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_FieldEffect.getValue());
        mplew.write(75);
        mplew.writeMapleAsciiString(intro);
        mplew.writeMapleAsciiString(path);
        mplew.write(1);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.write(0);
        mplew.writeMapleAsciiString(animation);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(str);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        for (MapleCharacter chr : this.getCharacters()) {
            chr.send(mplew.getPacket());
        }
    }

    public void showMoviePath(String intro, String path, int str) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_FieldEffect.getValue());
        mplew.write(75);
        mplew.writeMapleAsciiString(intro);
        mplew.writeMapleAsciiString(path);
        mplew.write(1);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(2);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(1);
        mplew.write(0);
        mplew.writeInt(49);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(str);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        for (MapleCharacter chr : this.getCharacters()) {
            chr.send(mplew.getPacket());
        }
    }

    public void playSound(String SoundPath, int str) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_FieldEffect.getValue());
        mplew.write(7);
        mplew.writeMapleAsciiString(SoundPath);
        mplew.writeInt(str);
        mplew.writeInt(0);
        mplew.writeInt(0);
        for (MapleCharacter chr : this.getCharacters()) {
            chr.send(mplew.getPacket());
        }
    }

    public void DemianSpace(boolean open) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.BOSS_DEMIAN_KEYMAP_SPACE.getValue());
        if (open) {
            mplew.writeInt(13);
            mplew.writeInt(13);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeInt(1);
            mplew.write(0);
            mplew.writeInt(80001974);
            mplew.writeInt(1);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeShort(0);
            mplew.write(0);
            for (MapleCharacter chr : this.getCharacters()) {
                chr.send(mplew.getPacket());
                chr.send(Demian.unkDemian());
            }
        } else {
            mplew.writeInt(0);
            for (MapleCharacter chr : this.getCharacters()) {
                chr.send(mplew.getPacket());
            }
        }
    }

    @Generated
    public MapleMap getMap() {
        return this.map;
    }
}

