/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.skills.Skill;
import Client.skills.SkillFactory;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MapleMonster;
import Net.server.life.MobSkill;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Packet.MaplePacketCreator;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.EnumMap;
import java.util.concurrent.ScheduledFuture;

public class MapleAffectedArea
extends MapleMapObject {
    private final Rectangle area;
    private final boolean isMobMist;
    private final int skilllevel;
    private final int ownerId;
    private MapleStatEffect effect;
    private MobSkill skill;
    private boolean poisonMist;
    private boolean facingLeft;
    private boolean needHandle;
    private int skillDelay;
    private int areaType;
    private int healCount;
    private int healHPr;
    private int subtype;
    private int force;
    private int forcex;
    private int duration;
    private int skillID;
    private ScheduledFuture<?> schedule = null;
    private ScheduledFuture<?> poisonSchedule = null;
    private Point ownerPosition;
    private MapleCharacter owner;
    private final long startTime = System.currentTimeMillis();
    private long cancelTime = 0L;
    public boolean BUnk1 = false;
    public boolean BUnk2 = false;

    public MapleAffectedArea(Rectangle area, MapleMonster mob, MobSkill skill, Point position) {
        this.area = area;
        this.ownerId = mob.getObjectId();
        this.skill = skill;
        this.skillID = skill.getSourceId();
        this.skilllevel = skill.getLevel();
        this.isMobMist = true;
        this.poisonMist = true;
        this.areaType = 0;
        this.skillDelay = 0;
        this.force = skill.getForce();
        this.forcex = skill.getForcex();
        this.setPosition(position);
        this.owner = null;
        this.duration = skill.getDuration();
        this.facingLeft = !mob.isFacingLeft();
        this.needHandle = true;
    }

    public MapleAffectedArea(Rectangle area, MapleCharacter owner, MapleStatEffect effect, Point point) {
        this.owner = owner;
        this.area = area;
        this.ownerPosition = owner.getPosition();
        this.ownerId = owner.getId();
        this.areaType = 0;
        this.effect = effect;
        this.skillID = effect.getSourceId();
        this.skillDelay = 0;
        this.isMobMist = false;
        this.poisonMist = false;
        this.healCount = 0;
        this.needHandle = !effect.getStatups().isEmpty() || !effect.getMonsterStatus().isEmpty();
        this.skilllevel = effect.getLevel();
        this.facingLeft = !owner.isFacingLeft();
        this.duration = effect.getSummonDuration(owner);
        this.setPosition(point);
        switch (effect.getSourceId()) {
            case 12121005: 
            case 21121068: 
            case 22161003: 
            case 35121010: 
            case 42111004: 
            case 162101010: 
            case 162121018: 
            case 162121043: {
                this.skillDelay = 2;
                break;
            }
            case 162111000: 
            case 162111003: {
                this.skillDelay = 2;
                this.needHandle = true;
                break;
            }
            case 2221055: {
                this.skillDelay = 2;
                this.BUnk2 = true;
                break;
            }
            case 33111013: {
                this.skillDelay = 5;
                break;
            }
            case 33121016: {
                this.skillDelay = 8;
                break;
            }
            case 4221006: 
            case 22170093: 
            case 155121006: {
                this.skillDelay = 3;
                break;
            }
            case 151121041: {
                this.BUnk1 = true;
                this.skillDelay = 3;
                break;
            }
            case 32121006: {
                this.areaType = 0;
                this.BUnk2 = true;
                break;
            }
            case 36121007: {
                this.areaType = 5;
                this.needHandle = true;
                break;
            }
            case 2201009: {
                this.skillDelay = 3;
                this.poisonMist = true;
                this.needHandle = false;
                break;
            }
            case 131001207: {
                this.skillDelay = 12;
                break;
            }
            case 2111003: {
                this.poisonMist = true;
                break;
            }
            case 24121052: {
                this.needHandle = false;
                break;
            }
            case 400010010: 
            case 400020002: {
                this.skillDelay = 3;
                this.poisonMist = true;
                break;
            }
            case 42121005: {
                this.skillDelay = 10;
                break;
            }
            case 42001101: {
                this.skillDelay = 2;
                this.poisonMist = true;
                break;
            }
            case 61121105: 
            case 400021040: 
            case 400040008: 
            case 400041008: {
                this.duration = 10000;
                this.poisonMist = true;
                break;
            }
            case 400021031: {
                this.duration = 300;
                this.poisonMist = true;
                break;
            }
            case 101120104: {
                this.skillDelay = 16;
                break;
            }
            case 2311011: {
                this.healCount = effect.getY();
                this.needHandle = false;
                break;
            }
            case 2321015: {
                int intValue = owner.getStat().getTotalInt();
                this.duration = effect.getQ();
                if (intValue >= effect.getS2()) {
                    this.duration += intValue / effect.getS2() * effect.getQ2();
                }
                this.duration = effect.calcSummonDuration(this.duration * 1000, owner);
                this.healHPr = effect.getU2();
                if (intValue >= effect.getDot()) {
                    this.healHPr += intValue / effect.getDot() * effect.getW2();
                }
                this.skillDelay = 10;
                this.BUnk2 = true;
                this.needHandle = false;
                break;
            }
            case 400051026: {
                this.duration = owner.getSkillEffect(400051024).getU2() * 1000;
                this.poisonMist = true;
                break;
            }
            case 400051076: {
                this.healHPr = effect.getX();
                break;
            }
            case 2111013: {
                this.skillDelay = 14;
                this.duration = effect.getS() * 1000;
                this.poisonMist = true;
                this.BUnk2 = true;
            }
        }
    }

    public MapleAffectedArea(Rectangle area, MapleCharacter owner) {
        this.area = area;
        this.ownerId = owner.getId();
        this.effect = new MapleStatEffect();
        this.skilllevel = 30;
        this.areaType = 0;
        this.isMobMist = false;
        this.poisonMist = false;
        this.skillDelay = 10;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.AFFECTED_AREA;
    }

    public Skill getSourceSkill() {
        return SkillFactory.getSkill(this.effect.getSourceId());
    }

    public boolean isMobMist() {
        return this.isMobMist;
    }

    public boolean isPoisonMist() {
        return this.poisonMist;
    }

    public int getHealCount() {
        return this.healCount;
    }

    public void setHealCount(int count) {
        this.healCount = count;
    }

    public int getHealHPR() {
        return this.healHPr;
    }

    public void setHealHPR(int hpr) {
        this.healHPr = hpr;
    }

    public int getAreaType() {
        return this.areaType;
    }

    public void setAreaType(int areaType) {
        this.areaType = areaType;
    }

    public int getSkillDelay() {
        return this.skillDelay;
    }

    public void setSkillDelay(int skillDelay) {
        this.skillDelay = skillDelay;
    }

    public int getSkillLevel() {
        return this.skilllevel;
    }

    public int getOwnerId() {
        return this.ownerId;
    }

    public MobSkill getMobSkill() {
        return this.skill;
    }

    public Rectangle getArea() {
        return this.area;
    }

    public MapleStatEffect getEffect() {
        return this.effect;
    }

    public Point getOwnerPosition() {
        return this.ownerPosition;
    }

    public boolean isFacingLeft() {
        return this.facingLeft;
    }

    public int getSubtype() {
        return this.subtype;
    }

    public void setSubtype(int subtype) {
        this.subtype = subtype;
    }

    public int getForce() {
        return this.force;
    }

    public int getForcex() {
        return this.forcex;
    }

    @Override
    public int getRange() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void sendSpawnData(MapleClient c) {
        c.announce(MaplePacketCreator.spawnMist(this));
    }

    @Override
    public void sendDestroyData(MapleClient c) {
        c.announce(MaplePacketCreator.removeMist(this.getObjectId(), false));
    }

    public boolean makeChanceResult() {
        return this.effect.makeChanceResult();
    }

    public MapleCharacter getOwner() {
        return this.owner;
    }

    public boolean shouldCancel(long now) {
        return this.cancelTime > 0L && this.cancelTime <= now;
    }

    public long getCancelTask() {
        return this.cancelTime;
    }

    public void setCancelTask(long cancelTask) {
        this.cancelTime = System.currentTimeMillis() + cancelTask;
    }

    public int getLeftTime() {
        if (this.skillID == 400021031) {
            return (int)(-(this.startTime + (long)this.duration - System.currentTimeMillis()));
        }
        return (int)(this.startTime + (long)this.duration - System.currentTimeMillis());
    }

    public void setSchedule(ScheduledFuture<?> schedule) {
        this.schedule = schedule;
    }

    public void setPoisonSchedule(ScheduledFuture<?> poisonSchedule) {
        this.poisonSchedule = poisonSchedule;
    }

    public final void cancel() {
        if (this.poisonSchedule != null) {
            this.poisonSchedule.cancel(true);
            this.poisonSchedule = null;
        }
        if (this.schedule != null) {
            this.schedule.cancel(true);
            this.schedule = null;
        }
    }

    public boolean isNeedHandle() {
        return this.needHandle;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getSkillID() {
        return this.skillID;
    }

    public void handleEffect(MapleCharacter chr, int numTimes) {
        MapleCharacter fchr;
        if (this.getSkillID() == 2111013) {
            return;
        }
        if (this.isMobMist()) {
            if (this.getSkillID() == 131 && chr.getPosition().y > -20) {
                int x = this.getMobSkill().getX();
                if (x < 50) {
                    chr.addHPMP(-10, 0);
                } else {
                    chr.addHPMP(-x, 0, false, true);
                }
            }
            return;
        }
        if (numTimes > -2 && (fchr = chr.getMap().getPlayerObject(this.getOwnerId())) != null && (fchr == chr || fchr.getParty() != null && fchr.getParty() == chr.getParty()) && this.area.getBounds().contains(chr.getPosition())) {
            if (this.getSkillID() == 36121007) {
                fchr.reduceAllSkillCooldown(4000, true);
            } else if (this.getSkillID() == 162111000 || this.getSkillID() == 162111003) {
                MapleStatEffect effect;
                int skil;
                if (this.getSkillID() == 162111003) {
                    if (numTimes < 0 || numTimes % this.getEffect().getZ() == 0) {
                        chr.addHPMP(this.getEffect().getHp(), 0);
                    }
                    skil = 162111004;
                } else {
                    skil = 162111001;
                }
                if (chr.getBuffStatValueHolder(skil) == null && (effect = fchr.getSkillEffect(skil)) != null) {
                    int duration = effect.calcBuffDuration(fchr == chr ? effect.getDuration() : this.getEffect().getX() * 1000, fchr);
                    effect.applyTo(fchr, chr, duration, false, false, true, this.getPosition());
                }
            } else if (chr.getBuffStatValueHolder(this.getSkillID()) == null && (162121043 != this.getSkillID() || fchr == chr)) {
                this.getEffect().applyTo(fchr, chr, this.getLeftTime(), false, false, true, this.getPosition());
            }
            return;
        }
        SecondaryStatValueHolder mbsvh = chr.getBuffStatValueHolder(this.getSkillID());
        if (mbsvh != null) {
            EnumMap<SecondaryStat, Integer> status = new EnumMap<SecondaryStat, Integer>(mbsvh.effect.getStatups());
            status.remove(SecondaryStat.IndieBuffIcon);
            chr.cancelEffect(mbsvh.effect, false, mbsvh.startTime, status);
        }
    }

    public void handleMonsterEffect(MapleMap map, int numTimes) {
        MapleCharacter chr;
        MapleStatEffect effect;
        if (!this.isMobMist() && (effect = this.getEffect()) != null && (chr = map.getPlayerObject(this.getOwnerId())) != null && !effect.getMonsterStatus().isEmpty()) {
            for (MapleMonster monster : map.getMonsters()) {
                if (numTimes > -2 && this.getArea().contains(monster.getPosition())) {
                    if (monster.getSponge() != null) continue;
                    effect.applyMonsterEffect(chr, monster, effect.getMobDebuffDuration(chr));
                    continue;
                }
                if (this.skillID != 4121015) continue;
                monster.removeEffect(this.ownerId, this.skillID);
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        return this.effect.calculateBoundingBox(this.getPosition());
    }
}

