/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package SwordieX.client.character;

import Client.MapleCharacter;
import Client.MapleTraitType;
import Config.constants.JobConstants;
import SwordieX.client.character.Burning;
import SwordieX.client.character.ExtendSP;
import SwordieX.client.character.NonCombatStatDayLimit;
import SwordieX.client.character.avatar.AvatarLook;
import SwordieX.util.FileTime;
import connection.OutPacket;
import lombok.Generated;
import tools.data.MaplePacketLittleEndianWriter;

public class CharacterStat {
    private int characterId;
    private int characterIdForLog;
    private int worldIdForLog;
    private String name;
    private int gender;
    private int skin;
    private int face;
    private int hair;
    private int level;
    private int job;
    private int str;
    private int dex;
    private int int_;
    private int luk;
    private int hp;
    private int maxHp;
    private int mp;
    private int maxMp;
    private int ap;
    private ExtendSP extendSP;
    private int sp;
    private long exp;
    private int pop;
    private int wp;
    private int gachExp;
    private long posMap;
    private int portal;
    private int subJob;
    private int defFaceAcc;
    private int fatigue;
    private int lastFatigueUpdateTime;
    private int charismaExp;
    private int insightExp;
    private int willExp;
    private int craftExp;
    private int senseExp;
    private int charmExp;
    private NonCombatStatDayLimit nonCombatStatDayLimit;
    private int pvpExp;
    private int pvpGrade;
    private int pvpPoint;
    private int pvpModeLevel;
    private int pvpModeType;
    private int eventPoint;
    private Burning burning;
    private CharacterStat characterStat;
    private AvatarLook avatarLook;
    private AvatarLook secondAvatarLook;

    public CharacterStat(MapleCharacter chr) {
        this.characterId = chr.getId();
        this.characterIdForLog = chr.getId();
        this.worldIdForLog = chr.getWorld();
        this.name = chr.getName();
        this.gender = chr.getGender();
        this.skin = chr.getSkinColor();
        this.face = chr.getFace();
        this.hair = chr.getHair();
        this.level = chr.getLevel();
        this.job = chr.getJob();
        this.str = chr.getStat().str;
        this.dex = chr.getStat().dex;
        this.int_ = chr.getStat().int_;
        this.luk = chr.getStat().luk;
        this.hp = chr.getStat().hp;
        this.maxHp = chr.getStat().getMaxHp();
        this.mp = chr.getStat().mp;
        this.maxMp = chr.getStat().getMaxMp();
        this.ap = chr.getRemainingAp();
        this.extendSP = new ExtendSP(chr);
        this.sp = chr.getRemainingSp();
        this.exp = chr.getExp();
        this.pop = chr.getFame();
        this.wp = chr.getWeaponPoint();
        this.gachExp = chr.getGachExp();
        this.posMap = chr.getMapId();
        this.portal = chr.getInitialSpawnpoint();
        this.subJob = chr.getSubcategory();
        this.defFaceAcc = chr.getDecorate();
        this.fatigue = chr.getFatigue();
        this.lastFatigueUpdateTime = FileTime.currentTime().toYYMMDDHHintValue();
        this.charismaExp = chr.getTrait(MapleTraitType.charisma).getTotalExp();
        this.insightExp = chr.getTrait(MapleTraitType.insight).getTotalExp();
        this.willExp = chr.getTrait(MapleTraitType.will).getTotalExp();
        this.craftExp = chr.getTrait(MapleTraitType.craft).getTotalExp();
        this.senseExp = chr.getTrait(MapleTraitType.sense).getTotalExp();
        this.charmExp = chr.getTrait(MapleTraitType.charm).getTotalExp();
        this.nonCombatStatDayLimit = new NonCombatStatDayLimit();
        this.pvpExp = chr.getStat().pvpExp;
        this.pvpGrade = chr.getStat().pvpRank;
        this.pvpPoint = chr.getBattlePoints();
        this.pvpModeLevel = 6;
        this.pvpModeType = 7;
        this.eventPoint = 0;
        this.burning = new Burning(chr.getBurningChrType(), chr.getBurningChrType() > 0 ? 10 : 0, chr.getBurningChrType() == 2 ? 130 : (chr.getBurningChrType() == 1 ? 150 : (chr.getBurningChrType() == 3 ? 200 : 0)), chr.getBurningChrTime() > 0L ? FileTime.currentTime() : FileTime.fromType(FileTime.Type.PLAIN_ZERO), chr.getBurningChrTime() > 0L ? FileTime.fromLong(chr.getBurningChrTime()) : FileTime.fromType(FileTime.Type.ZERO_TIME));
    }

    public int getInt() {
        return this.int_;
    }

    public void setInt(int inte) {
        this.int_ = inte;
    }

    public long getPosMap() {
        return this.posMap == 0L ? 931000000L : this.posMap;
    }

    public void encode(MaplePacketLittleEndianWriter mplew) {
        OutPacket outPacket = new OutPacket();
        this.encode(outPacket);
        mplew.write(outPacket.getData());
    }

    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(this.getCharacterId());
        outPacket.encodeInt(this.getCharacterIdForLog());
        outPacket.encodeInt(this.getWorldIdForLog());
        outPacket.encodeString(this.getName(), 15);
        outPacket.encodeByte(this.getGender());
        outPacket.encodeByte(0);
        outPacket.encodeByte(this.getSkin());
        outPacket.encodeInt(0);
        outPacket.encodeInt(this.getFace());
        outPacket.encodeInt(this.getHair());
        outPacket.encodeInt(this.getLevel());
        outPacket.encodeShort(this.getJob());
        outPacket.encodeShort(this.getStr());
        outPacket.encodeShort(this.getDex());
        outPacket.encodeShort(this.getInt());
        outPacket.encodeShort(this.getLuk());
        outPacket.encodeInt(this.getHp());
        outPacket.encodeInt(this.getMaxHp());
        outPacket.encodeInt(this.getMp());
        outPacket.encodeInt(this.getMaxMp());
        outPacket.encodeShort(this.getAp());
        if (JobConstants.isSeparatedSpJob(this.getJob())) {
            this.getExtendSP().encode(outPacket);
        } else {
            outPacket.encodeShort(this.getSp());
        }
        outPacket.encodeLong(this.getExp());
        outPacket.encodeInt(this.getPop());
        outPacket.encodeInt(this.getWp());
        outPacket.encodeLong(this.getGachExp());
        outPacket.encodeArr("40 54 CA 24 AF 86 DB 01");
        outPacket.encodeInt((int)this.getPosMap());
        outPacket.encodeByte(this.getPortal());
        outPacket.encodeShort(this.getSubJob());
        if (JobConstants.hasDecorate(this.getJob())) {
            outPacket.encodeInt(this.getDefFaceAcc());
        }
        outPacket.encodeByte(0);
        outPacket.encodeArr("00 40 E0 FD 3B 37 4F 01");
        outPacket.encodeInt(this.getCharismaExp());
        outPacket.encodeInt(this.getInsightExp());
        outPacket.encodeInt(this.getWillExp());
        outPacket.encodeInt(this.getCraftExp());
        outPacket.encodeInt(this.getSenseExp());
        outPacket.encodeInt(this.getCharmExp());
        this.getNonCombatStatDayLimit().encode(outPacket);
        outPacket.encodeInt(0);
        outPacket.encodeByte(10);
        outPacket.encodeInt(0);
        outPacket.encodeByte(6);
        outPacket.encodeByte(7);
        outPacket.encodeInt(0);
        outPacket.encodeInt(0);
        outPacket.encodeArr("45 4B DB 01 90 1E A2 C3");
        outPacket.encodeArr("00 80 05 BB 46 E6 17 02");
        outPacket.encodeArr("00 40 E0 FD 3B 37 4F 01");
        outPacket.encodeInt(0);
        outPacket.encodeInt(0);
        outPacket.encodeInt(0);
        outPacket.encodeByte(0);
        outPacket.encodeInt(0);
        outPacket.encodeArr(new byte[25]);
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);
    }

    @Generated
    public void setCharacterId(int characterId) {
        this.characterId = characterId;
    }

    @Generated
    public void setCharacterIdForLog(int characterIdForLog) {
        this.characterIdForLog = characterIdForLog;
    }

    @Generated
    public void setWorldIdForLog(int worldIdForLog) {
        this.worldIdForLog = worldIdForLog;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setGender(int gender) {
        this.gender = gender;
    }

    @Generated
    public void setSkin(int skin) {
        this.skin = skin;
    }

    @Generated
    public void setFace(int face) {
        this.face = face;
    }

    @Generated
    public void setHair(int hair) {
        this.hair = hair;
    }

    @Generated
    public void setLevel(int level) {
        this.level = level;
    }

    @Generated
    public void setJob(int job) {
        this.job = job;
    }

    @Generated
    public void setStr(int str) {
        this.str = str;
    }

    @Generated
    public void setDex(int dex) {
        this.dex = dex;
    }

    @Generated
    public void setInt_(int int_) {
        this.int_ = int_;
    }

    @Generated
    public void setLuk(int luk) {
        this.luk = luk;
    }

    @Generated
    public void setHp(int hp) {
        this.hp = hp;
    }

    @Generated
    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    @Generated
    public void setMp(int mp) {
        this.mp = mp;
    }

    @Generated
    public void setMaxMp(int maxMp) {
        this.maxMp = maxMp;
    }

    @Generated
    public void setAp(int ap) {
        this.ap = ap;
    }

    @Generated
    public void setExtendSP(ExtendSP extendSP) {
        this.extendSP = extendSP;
    }

    @Generated
    public void setSp(int sp) {
        this.sp = sp;
    }

    @Generated
    public void setExp(long exp) {
        this.exp = exp;
    }

    @Generated
    public void setPop(int pop) {
        this.pop = pop;
    }

    @Generated
    public void setWp(int wp) {
        this.wp = wp;
    }

    @Generated
    public void setGachExp(int gachExp) {
        this.gachExp = gachExp;
    }

    @Generated
    public void setPosMap(long posMap) {
        this.posMap = posMap;
    }

    @Generated
    public void setPortal(int portal) {
        this.portal = portal;
    }

    @Generated
    public void setSubJob(int subJob) {
        this.subJob = subJob;
    }

    @Generated
    public void setDefFaceAcc(int defFaceAcc) {
        this.defFaceAcc = defFaceAcc;
    }

    @Generated
    public void setFatigue(int fatigue) {
        this.fatigue = fatigue;
    }

    @Generated
    public void setLastFatigueUpdateTime(int lastFatigueUpdateTime) {
        this.lastFatigueUpdateTime = lastFatigueUpdateTime;
    }

    @Generated
    public void setCharismaExp(int charismaExp) {
        this.charismaExp = charismaExp;
    }

    @Generated
    public void setInsightExp(int insightExp) {
        this.insightExp = insightExp;
    }

    @Generated
    public void setWillExp(int willExp) {
        this.willExp = willExp;
    }

    @Generated
    public void setCraftExp(int craftExp) {
        this.craftExp = craftExp;
    }

    @Generated
    public void setSenseExp(int senseExp) {
        this.senseExp = senseExp;
    }

    @Generated
    public void setCharmExp(int charmExp) {
        this.charmExp = charmExp;
    }

    @Generated
    public void setNonCombatStatDayLimit(NonCombatStatDayLimit nonCombatStatDayLimit) {
        this.nonCombatStatDayLimit = nonCombatStatDayLimit;
    }

    @Generated
    public void setPvpExp(int pvpExp) {
        this.pvpExp = pvpExp;
    }

    @Generated
    public void setPvpGrade(int pvpGrade) {
        this.pvpGrade = pvpGrade;
    }

    @Generated
    public void setPvpPoint(int pvpPoint) {
        this.pvpPoint = pvpPoint;
    }

    @Generated
    public void setPvpModeLevel(int pvpModeLevel) {
        this.pvpModeLevel = pvpModeLevel;
    }

    @Generated
    public void setPvpModeType(int pvpModeType) {
        this.pvpModeType = pvpModeType;
    }

    @Generated
    public void setEventPoint(int eventPoint) {
        this.eventPoint = eventPoint;
    }

    @Generated
    public void setBurning(Burning burning) {
        this.burning = burning;
    }

    @Generated
    public void setCharacterStat(CharacterStat characterStat) {
        this.characterStat = characterStat;
    }

    @Generated
    public void setAvatarLook(AvatarLook avatarLook) {
        this.avatarLook = avatarLook;
    }

    @Generated
    public void setSecondAvatarLook(AvatarLook secondAvatarLook) {
        this.secondAvatarLook = secondAvatarLook;
    }

    @Generated
    public int getCharacterId() {
        return this.characterId;
    }

    @Generated
    public int getCharacterIdForLog() {
        return this.characterIdForLog;
    }

    @Generated
    public int getWorldIdForLog() {
        return this.worldIdForLog;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public int getGender() {
        return this.gender;
    }

    @Generated
    public int getSkin() {
        return this.skin;
    }

    @Generated
    public int getFace() {
        return this.face;
    }

    @Generated
    public int getHair() {
        return this.hair;
    }

    @Generated
    public int getLevel() {
        return this.level;
    }

    @Generated
    public int getJob() {
        return this.job;
    }

    @Generated
    public int getStr() {
        return this.str;
    }

    @Generated
    public int getDex() {
        return this.dex;
    }

    @Generated
    public int getInt_() {
        return this.int_;
    }

    @Generated
    public int getLuk() {
        return this.luk;
    }

    @Generated
    public int getHp() {
        return this.hp;
    }

    @Generated
    public int getMaxHp() {
        return this.maxHp;
    }

    @Generated
    public int getMp() {
        return this.mp;
    }

    @Generated
    public int getMaxMp() {
        return this.maxMp;
    }

    @Generated
    public int getAp() {
        return this.ap;
    }

    @Generated
    public ExtendSP getExtendSP() {
        return this.extendSP;
    }

    @Generated
    public int getSp() {
        return this.sp;
    }

    @Generated
    public long getExp() {
        return this.exp;
    }

    @Generated
    public int getPop() {
        return this.pop;
    }

    @Generated
    public int getWp() {
        return this.wp;
    }

    @Generated
    public int getGachExp() {
        return this.gachExp;
    }

    @Generated
    public int getPortal() {
        return this.portal;
    }

    @Generated
    public int getSubJob() {
        return this.subJob;
    }

    @Generated
    public int getDefFaceAcc() {
        return this.defFaceAcc;
    }

    @Generated
    public int getFatigue() {
        return this.fatigue;
    }

    @Generated
    public int getLastFatigueUpdateTime() {
        return this.lastFatigueUpdateTime;
    }

    @Generated
    public int getCharismaExp() {
        return this.charismaExp;
    }

    @Generated
    public int getInsightExp() {
        return this.insightExp;
    }

    @Generated
    public int getWillExp() {
        return this.willExp;
    }

    @Generated
    public int getCraftExp() {
        return this.craftExp;
    }

    @Generated
    public int getSenseExp() {
        return this.senseExp;
    }

    @Generated
    public int getCharmExp() {
        return this.charmExp;
    }

    @Generated
    public NonCombatStatDayLimit getNonCombatStatDayLimit() {
        return this.nonCombatStatDayLimit;
    }

    @Generated
    public int getPvpExp() {
        return this.pvpExp;
    }

    @Generated
    public int getPvpGrade() {
        return this.pvpGrade;
    }

    @Generated
    public int getPvpPoint() {
        return this.pvpPoint;
    }

    @Generated
    public int getPvpModeLevel() {
        return this.pvpModeLevel;
    }

    @Generated
    public int getPvpModeType() {
        return this.pvpModeType;
    }

    @Generated
    public int getEventPoint() {
        return this.eventPoint;
    }

    @Generated
    public Burning getBurning() {
        return this.burning;
    }

    @Generated
    public CharacterStat getCharacterStat() {
        return this.characterStat;
    }

    @Generated
    public AvatarLook getAvatarLook() {
        return this.avatarLook;
    }

    @Generated
    public AvatarLook getSecondAvatarLook() {
        return this.secondAvatarLook;
    }
}

