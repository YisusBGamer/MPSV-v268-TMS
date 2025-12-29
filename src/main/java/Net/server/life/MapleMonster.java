/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.SkillCustomInfo
 *  Net.server.fieldskill.FieldSkill
 *  Net.server.fieldskill.FieldSkillFactory
 *  Net.server.life.MapleMonster$PartyAttackerEntry
 *  Packet.CField
 *  Server.BossEventHandler.Seren
 *  SwordieX.field.fieldeffect.FieldEffect
 *  connection.packet.FieldPacket
 */
package Net.server.life;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MapleExpStat;
import Client.MapleTraitType;
import Client.MonsterEffectHolder;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.force.MapleForceAtom;
import Client.force.MapleForceFactory;
import Client.inventory.MapleInventory;
import Client.inventory.MaplePet;
import Client.skills.Skill;
import Client.skills.SkillFactory;
import Client.stat.DeadDebuff;
import Client.status.MonsterStatus;
import Config.configs.ServerConfig;
import Config.constants.GameConstants;
import Config.constants.JobConstants;
import Net.server.SkillCustomInfo;
import Net.server.Timer;
import Net.server.buffs.MapleStatEffect;
import Net.server.buffs.MapleStatEffectFactory;
import Net.server.factory.MobCollectionFactory;
import Net.server.fieldskill.FieldSkill;
import Net.server.fieldskill.FieldSkillFactory;
import Net.server.life.AbstractLoadedMapleLife;
import Net.server.life.Element;
import Net.server.life.ElementalEffectiveness;
import Net.server.life.ForcedMobStat;
import Net.server.life.MapleLifeFactory;
import Net.server.life.MapleMonster;
import Net.server.life.MapleMonsterInformationProvider;
import Net.server.life.MapleMonsterStats;
import Net.server.life.MobSkillFactory;
import Net.server.life.MonsterDropEntry;
import Net.server.life.MonsterListener;
import Net.server.maps.MapleFoothold;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Net.server.movement.LifeMovementFragment;
import Net.server.movement.MovementNormal;
import Opcode.header.OutHeader;
import Packet.BuffPacket;
import Packet.CField;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import Packet.MobPacket;
import Plugin.script.binding.ScriptEvent;
import Server.BossEventHandler.Seren;
import Server.channel.ChannelServer;
import Server.channel.handler.MovementParse;
import SwordieX.client.party.Party;
import SwordieX.client.party.PartyMember;
import SwordieX.field.fieldeffect.FieldEffect;
import SwordieX.world.World;
import connection.packet.FieldPacket;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import tools.DateUtil;
import tools.Pair;
import tools.Randomizer;
import tools.Triple;

public class MapleMonster
extends AbstractLoadedMapleLife {
    private final Map<MonsterStatus, List<MonsterEffectHolder>> effects = new LinkedHashMap<MonsterStatus, List<MonsterEffectHolder>>();
    private long lastSpecialAttackTime = System.currentTimeMillis();
    private long lastSeedCountedTime = System.currentTimeMillis();
    private long lastStoneTime = System.currentTimeMillis();
    private final long lastSpawnBlindMobtime = System.currentTimeMillis();
    private ScheduledFuture<?> schedule = null;
    private final List<AttackerEntry> attackers = new LinkedList<AttackerEntry>();
    private final Map<Pair<Integer, Integer>, Integer> skillsUsed = new HashMap<Pair<Integer, Integer>, Integer>();
    private final Map<String, Integer> aggroRank = new HashMap<String, Integer>();
    private Map<Integer, Long> usedSkills;
    private MapleMonsterStats stats;
    private ForcedMobStat forcedMobStat = null;
    private long hp;
    private long nextKill = 0L;
    private long lastDropTime = 0L;
    private final long lastUpdateController = 0L;
    private long barrier = 0L;
    private int mp;
    private final boolean shouldDropItem = false;
    private final boolean killed = false;
    private final boolean isseperated = false;
    private final boolean isMobGroup = false;
    private boolean isSkillForbid = false;
    private boolean useSpecialSkill = false;
    private byte carnivalTeam = (byte)-1;
    private byte phase = 0;
    private final byte bigbangCount = 0;
    private boolean demianChangePhase = false;
    private MapleMap map;
    private WeakReference<MapleMonster> sponge = new WeakReference<MapleMonster>(null);
    private int linkoid = 0;
    private int lastNode = -1;
    private final int highestDamageChar = 0;
    private int linkCID = 0;
    private WeakReference<MapleCharacter> controller = new WeakReference<MapleCharacter>(null);
    private boolean fake = false;
    private boolean dropsDisabled = false;
    private boolean controllerHasAggro = false;
    private boolean controllerKnowsAboutAggro;
    private boolean spawnRevivesDisabled = false;
    private ScriptEvent eventInstance;
    private MonsterListener listener = null;
    private final byte[] reflectpack = null;
    private byte[] nodepack = null;
    private ScheduledFuture deadBound = null;
    private int stolen = -1;
    private boolean shouldDropAssassinsMark = true;
    private long changeTime = 0L;
    private int reduceDamageType = -1;
    private int followChrID;
    private int StigmaType;
    private int TotalStigma;
    private int eliteType;
    private long lastMove;
    private int triangulation = 0;
    private int patrolScopeX1;
    private int patrolScopeX2;
    private long lastSmiteTime = 0L;
    private long lastLucidSmiteTime = 0L;
    private int zoneDataType = 0;
    private final ReentrantLock effectLock = new ReentrantLock();
    private int seperateSoulSrcOID = 0;
    private final List<Integer> reviveMobs = new ArrayList<Integer>();
    private final ReentrantLock hpLock = new ReentrantLock();
    private double reduceDamageR = 0.0;
    private final ReentrantLock controllerLock = new ReentrantLock();
    private short appearType;
    private boolean newSpawn = true;
    private final transient Map<Integer, SkillCustomInfo> customInfo = new LinkedHashMap<Integer, SkillCustomInfo>();
    private boolean steal;
    private boolean soul;
    private long maxHP;
    private int maxMP;
    private final List<Integer> eliteMobActive = new ArrayList<Integer>();
    private double hpLimitPercent;
    private int lastKill;
    private byte animation;
    private boolean spongeMob;
    private int mobCtrlSN;
    private final List<Integer> spawnList = new ArrayList<Integer>();
    private List<Integer> willHplist = new ArrayList<Integer>();
    private long transTime;
    private int scale;
    private int showMobId = 0;
    private int rewardSprinkleCount = 0;
    private int bUnk = 0;
    private int bUnk1 = 0;
    private int owner = -1;
    private int eliteGrade = -1;
    private long shield = 0L;
    private long shieldmax = 0L;
    private int nextSkill = 0;
    private int nextSkillLvl = 0;
    private final int freezingOverlap = 0;
    private final int curseBound = 0;
    private String specialtxt;
    public long lastObstacleTime = System.currentTimeMillis();
    public long lastBWBliThunder = System.currentTimeMillis();
    public long lastBWThunder = System.currentTimeMillis();
    public long lastLaserTime = System.currentTimeMillis();
    public long lastRedObstacleTime = System.currentTimeMillis();
    public long lastSpearTime = System.currentTimeMillis();
    public long lastDistotionTime = System.currentTimeMillis();
    public long lastCapTime = 0L;
    public long astObstacleTime = System.currentTimeMillis();
    public long lastChainTime = System.currentTimeMillis();
    public long lastThunderTime = System.currentTimeMillis();
    public long lastEyeTime = System.currentTimeMillis();
    public long DrgonPower = System.currentTimeMillis();
    public long dropAttack = System.currentTimeMillis();
    public long dropAttack_II = System.currentTimeMillis();
    public long dropAttack_III = System.currentTimeMillis();
    public long dropAttack_IV = System.currentTimeMillis();
    public long fieldSpawn = System.currentTimeMillis();
    public long fieldSpawn_II = System.currentTimeMillis();
    public long fieldSpawn_III = System.currentTimeMillis();
    public long fieldSpawn_IV = System.currentTimeMillis();
    public long skillAttack = System.currentTimeMillis();
    public long skillAttack_II = System.currentTimeMillis();
    public long skillAttack_III = System.currentTimeMillis();
    public long skillAttack_IV = System.currentTimeMillis();
    public long changePhase = System.currentTimeMillis();
    public long changePhase_II = System.currentTimeMillis();
    public long changePhase_III = System.currentTimeMillis();
    public long changePhase_IV = System.currentTimeMillis();
    public long lastDropAttackTime = System.currentTimeMillis();
    private MapleInventory[] inventory;
    private transient Pair<Long, List<LifeMovementFragment>> lastres;
    private final boolean extreme = false;
    private boolean willSpecialPattern = false;
    private long eliteHp = 0L;
    private int SerenTimetype;
    private int SerenNoonTotalTime;
    private int SerenSunSetTotalTime;
    private int SerenMidNightSetTotalTime;
    private int SerenDawnSetTotalTime;
    private int SerenNoonNowTime;
    private int SerenSunSetNowTime;
    private int SerenMidNightSetNowTime;
    private int SerenDawnSetNowTime;

    public boolean isSkillForbid() {
        return this.isSkillForbid;
    }

    public void setSkillForbid(boolean isSkillForbid) {
        this.isSkillForbid = isSkillForbid;
    }

    public int getStigmaType() {
        return this.StigmaType;
    }

    public int getTotalStigma() {
        return this.TotalStigma;
    }

    public void setTotalStigma(int TotalStigma) {
        this.TotalStigma = TotalStigma;
    }

    public String getSpecialtxt() {
        return this.specialtxt;
    }

    public void setSpecialtxt(String specialtxt) {
        this.specialtxt = specialtxt;
    }

    public long getShield() {
        return this.shield;
    }

    public void setShield(long l, boolean b, long shield) {
        this.shield = shield;
    }

    public long getShieldmax() {
        return this.shieldmax;
    }

    public void setShieldmax(long shieldmax) {
        this.shieldmax = shieldmax;
    }

    public int getShieldPercent() {
        return (int)Math.ceil((double)this.shield * 100.0 / (double)this.shieldmax);
    }

    public MapleMonster(int id, MapleMonsterStats stats) {
        super(id);
        this.initWithStats(stats);
    }

    public MapleMonster(MapleMonster monster) {
        super(monster);
        this.initWithStats(monster.stats);
    }

    public long getBarrier() {
        return this.barrier;
    }

    public void setBarrier(long barrier) {
        this.barrier = barrier;
    }

    public void initWithStats(MapleMonsterStats stats) {
        this.setStance(5);
        this.stats = stats;
        this.hp = stats.getHp();
        this.mp = stats.getMp();
        this.maxHP = stats.getHp();
        this.maxMP = stats.getMp();
        this.scale = 100;
        if (stats.getSkillSize() > 0) {
            this.usedSkills = new HashMap<Integer, Long>();
        }
    }

    public List<AttackerEntry> getAttackers() {
        return this.attackers;
    }

    public boolean canNextSmite() {
        if (System.currentTimeMillis() > this.lastSmiteTime) {
            this.lastSmiteTime = System.currentTimeMillis() + 110000L;
            return true;
        }
        return false;
    }

    public boolean canNextLucidSmite() {
        if (System.currentTimeMillis() > this.lastLucidSmiteTime) {
            this.lastLucidSmiteTime = System.currentTimeMillis() + 90000L;
            return true;
        }
        return false;
    }

    public void checkMobZoneState() {
        if (this.getStats().getMobZone().size() > 0 && this.zoneDataType > 0 && this.getHPPercent() < (this.getStats().getMobZone().size() - this.zoneDataType) * 100 / this.getStats().getMobZone().size()) {
            this.setZoneDataType(this.zoneDataType + 1);
        }
    }

    public void checkMobZone(MapleCharacter player) {
        if (this.zoneDataType > 0) {
            int min = Math.min(this.zoneDataType, this.getStats().getMobZone().size());
            if (min <= 0) {
                return;
            }
            Rectangle rectangle = MapleStatEffectFactory.calculateBoundingBox(this.getPosition(), this.isFacingLeft(), this.getStats().getMobZone().get(min - 1).getLeft(), this.getStats().getMobZone().get(min - 1).getRight(), 0);
            if (rectangle.contains(player.getPosition()) && this.zoneDataType < 3) {
                if (player.getMobZoneState() <= 0) {
                    player.setMobZoneState(this.zoneDataType);
                    player.getClient().announce(BuffPacket.giveMobZoneState(player, this.getObjectId()));
                }
            } else if (player.getMobZoneState() > 0) {
                List<SecondaryStat> singletonList = Collections.singletonList(SecondaryStat.MobZoneState);
                player.setMobZoneState(0);
                player.getClient().announce(BuffPacket.temporaryStatReset(singletonList, player));
            }
        }
    }

    public final long getLastSkillUsed(int skillId) {
        if (this.usedSkills.containsKey(skillId)) {
            return this.usedSkills.get(skillId);
        }
        return 0L;
    }

    public final void setLastSkillUsed(Integer skillId, long now, long cooltime) {
        switch (skillId) {
            case 140: {
                this.usedSkills.put(skillId, now + (cooltime << 1));
                this.usedSkills.put(141, now);
                break;
            }
            case 141: {
                this.usedSkills.put(skillId, now + (cooltime << 1));
                this.usedSkills.put(140, now + cooltime);
                break;
            }
            case 143: {
                this.usedSkills.put(skillId, now + (cooltime << 1));
                this.usedSkills.put(144, now + cooltime);
                this.usedSkills.put(145, now + cooltime);
                break;
            }
            case 144: {
                this.usedSkills.put(skillId, now + (cooltime << 1));
                this.usedSkills.put(143, now + cooltime);
                this.usedSkills.put(145, now + cooltime);
                break;
            }
            case 145: {
                this.usedSkills.put(skillId, now + (cooltime << 1));
                this.usedSkills.put(143, now + cooltime);
                this.usedSkills.put(144, now + cooltime);
                break;
            }
            case 228: {
                this.usedSkills.put(skillId, now + 30000L);
                break;
            }
            default: {
                this.usedSkills.put(skillId, now + cooltime);
            }
        }
    }

    public int getZoneDataType() {
        return this.zoneDataType;
    }

    public void setZoneDataType(int zoneDataType) {
        this.zoneDataType = zoneDataType;
        if (this.map != null) {
            this.map.broadcastMessage(MobPacket.changeMonsterZone(this.getObjectId(), zoneDataType), this.getPosition());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void healHPMP(long hpHeal, int mpHeal) {
        this.hpLock.lock();
        try {
            int arg9999;
            if (this.hp + hpHeal > this.maxHP) {
                hpHeal = this.maxHP - this.hp;
            }
            if ((arg9999 = this.mp + mpHeal) > this.maxMP) {
                mpHeal = this.maxMP - this.mp;
            }
            this.hp = Math.min(this.hp + hpHeal, this.maxHP);
            int arg10000 = this.mp + mpHeal;
            this.mp = Math.min(arg10000, this.maxMP);
            this.map.broadcastMessage(MobPacket.MobDamaged(this, 0, hpHeal, false), this.getPosition());
        }
        finally {
            this.hpLock.unlock();
        }
        MapleMonster sponge = (MapleMonster)this.sponge.get();
        if (sponge != null) {
            sponge.healHPMP(hpHeal, mpHeal);
        }
    }

    public Map<MonsterStatus, List<MonsterEffectHolder>> getEffects() {
        this.effectLock.lock();
        try {
            Map<MonsterStatus, List<MonsterEffectHolder>> map = this.effects;
            return map;
        }
        finally {
            this.effectLock.unlock();
        }
    }

    public Map<MonsterStatus, List<MonsterEffectHolder>> getAllEffects() {
        this.effectLock.lock();
        try {
            LinkedHashMap<MonsterStatus, List<MonsterEffectHolder>> linkedHashMap = new LinkedHashMap<MonsterStatus, List<MonsterEffectHolder>>(this.effects);
            return linkedHashMap;
        }
        finally {
            this.effectLock.unlock();
        }
    }

    public List<MonsterEffectHolder> getIndieEffectHolder(MonsterStatus stat) {
        return this.getIndieEffectHolder(-1, stat);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MonsterEffectHolder> getIndieEffectHolder(int chrID, MonsterStatus stat) {
        this.effectLock.lock();
        try {
            ArrayList<MonsterEffectHolder> list = new ArrayList<MonsterEffectHolder>();
            if (this.effects.containsKey(stat)) {
                for (MonsterEffectHolder meh : this.effects.get(stat)) {
                    if (chrID >= 0 && meh.fromChrID != chrID) continue;
                    list.add(meh);
                }
            }
            ArrayList<MonsterEffectHolder> arrayList = list;
            return arrayList;
        }
        finally {
            this.effectLock.unlock();
        }
    }

    public MonsterEffectHolder getEffectHolder(MonsterStatus stat) {
        return this.getEffectHolder(0, stat);
    }

    public MonsterEffectHolder getEffectHolder(int chrID, MonsterStatus stat) {
        return this.getEffectHolder(chrID, stat, -1);
    }

    public MonsterEffectHolder getEffectHolder(int skillID) {
        return this.getEffectHolder(0, skillID);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MonsterEffectHolder getEffectHolder(int chrID, int skillID) {
        this.effectLock.lock();
        try {
            MonsterEffectHolder monsterEffectHolder = this.effects.values().stream().flatMap(Collection::stream).filter(meh -> (meh.fromChrID == chrID || chrID < 0) && meh.sourceID == skillID).findFirst().orElse(null);
            return monsterEffectHolder;
        }
        finally {
            this.effectLock.unlock();
        }
    }

    public MonsterEffectHolder getEffectHolder(MonsterStatus stat, int skillID) {
        return this.getEffectHolder(0, stat, skillID);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MonsterEffectHolder getEffectHolder(int chrID, MonsterStatus stat, int skillID) {
        this.effectLock.lock();
        try {
            MonsterEffectHolder monsterEffectHolder = this.effects.get(stat) == null ? null : (MonsterEffectHolder)this.effects.get(stat).stream().filter(meh -> !(meh.fromChrID != chrID && chrID > 0 || meh.sourceID != skillID && skillID != -1)).findFirst().orElse(null);
            return monsterEffectHolder;
        }
        finally {
            this.effectLock.unlock();
        }
    }

    public Map<MonsterStatus, MonsterEffectHolder> getMonsterHolderMap(Map<MonsterStatus, Integer> status) {
        EnumMap<MonsterStatus, MonsterEffectHolder> holderMap = new EnumMap<MonsterStatus, MonsterEffectHolder>(MonsterStatus.class);
        for (Map.Entry<MonsterStatus, Integer> entry : status.entrySet()) {
            MonsterEffectHolder meh = this.getEffectHolder(entry.getKey(), (int)entry.getValue());
            if (meh == null && entry.getKey().isDefault()) {
                meh = new MonsterEffectHolder(0, 0, 0);
            }
            if (meh == null) continue;
            holderMap.put(entry.getKey(), meh);
        }
        return holderMap;
    }

    public boolean isBuffed(MonsterStatus status) {
        return this.getEffectHolder(status) != null;
    }

    public void removeEffect(List<MonsterStatus> statusList) {
        this.removeEffect(statusList, 0, -1);
    }

    public void removeEffect(int chrID, int skillID) {
        this.removeEffect(Collections.emptyList(), chrID, skillID);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeEffect(List<MonsterStatus> statusList, int chrID, int skillID) {
        this.effectLock.lock();
        try {
            EnumMap<MonsterStatus, MonsterEffectHolder> statups = null;
            for (Map.Entry<MonsterStatus, List<MonsterEffectHolder>> entry : this.effects.entrySet()) {
                if (!statusList.isEmpty() && !statusList.contains(entry.getKey())) continue;
                if (statups == null) {
                    statups = new EnumMap<MonsterStatus, MonsterEffectHolder>(MonsterStatus.class);
                }
                Iterator<MonsterEffectHolder> iterator = entry.getValue().iterator();
                while (iterator.hasNext()) {
                    MonsterEffectHolder holder = iterator.next();
                    if (chrID > 0 && holder.fromChrID != chrID || skillID != -1 && holder.sourceID != skillID) continue;
                    iterator.remove();
                    statups.put(entry.getKey(), holder);
                }
            }
            if (statups != null && !statups.isEmpty()) {
                this.cancelStatus(statups, true);
            }
        }
        finally {
            this.effectLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerEffect(EnumMap<MonsterStatus, MonsterEffectHolder> enumMap) {
        this.effectLock.lock();
        try {
            EnumMap<MonsterStatus, MonsterEffectHolder> statups = null;
            for (Map.Entry<MonsterStatus, MonsterEffectHolder> entry : enumMap.entrySet()) {
                if (!this.effects.containsKey(entry.getKey())) continue;
                if (statups == null) {
                    statups = new EnumMap<MonsterStatus, MonsterEffectHolder>(MonsterStatus.class);
                }
                if (entry.getKey().isIndieStat()) {
                    Iterator iterator = this.effects.computeIfAbsent(entry.getKey(), k -> new LinkedList()).iterator();
                    while (iterator.hasNext()) {
                        MonsterEffectHolder holder = (MonsterEffectHolder)iterator.next();
                        if (holder.fromChrID != entry.getValue().fromChrID || holder.sourceID != entry.getValue().sourceID) continue;
                        iterator.remove();
                        statups.put(entry.getKey(), holder);
                    }
                    continue;
                }
                statups.put(entry.getKey(), entry.getValue());
            }
            if (statups != null && !statups.isEmpty()) {
                this.cancelStatus(statups, true);
            }
            for (Map.Entry<MonsterStatus, MonsterEffectHolder> entry : enumMap.entrySet()) {
                this.effects.computeIfAbsent(entry.getKey(), k -> new LinkedList()).add(entry.getValue());
            }
        }
        finally {
            this.effectLock.unlock();
        }
    }

    private void cancelStatus(EnumMap<MonsterStatus, MonsterEffectHolder> statups, boolean b) {
        for (Map.Entry<MonsterStatus, MonsterEffectHolder> entry : statups.entrySet()) {
            if (!entry.getKey().isIndieStat()) {
                this.effects.remove(entry.getKey());
                continue;
            }
            this.effects.get(entry.getKey()).remove(entry.getValue());
            if (!this.effects.get(entry.getKey()).isEmpty()) continue;
            this.effects.remove(entry.getKey());
        }
        if (statups.containsKey(MonsterStatus.SeperateSoulP)) {
            this.seperateSoulSrcOID = 0;
        }
        if (this.map != null && this.getMap().getCharactersSize() > 0) {
            this.map.broadcastMessage(MobPacket.cancelMonsterStatus(this, statups), this.getPosition());
        }
    }

    private void dotDamage(MapleCharacter chr, long damage) {
        if (chr != null) {
            this.damage(chr, 0, damage, true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void checkEffectExpiration() {
        LinkedList<MonsterEffectHolder> burnedstartups = null;
        this.effectLock.lock();
        try {
            EnumMap<MonsterStatus, MonsterEffectHolder> statups = null;
            for (Map.Entry<MonsterStatus, List<MonsterEffectHolder>> entry : this.effects.entrySet()) {
                Iterator<MonsterEffectHolder> iterator = entry.getValue().iterator();
                while (iterator.hasNext()) {
                    MonsterEffectHolder holder2 = iterator.next();
                    if (holder2.getLeftTime() <= 0L) {
                        if (statups == null) {
                            statups = new EnumMap<MonsterStatus, MonsterEffectHolder>(MonsterStatus.class);
                        }
                        statups.put(entry.getKey(), holder2);
                        iterator.remove();
                        continue;
                    }
                    if (entry.getKey() != MonsterStatus.Burned) continue;
                    if (burnedstartups == null) {
                        burnedstartups = new LinkedList<MonsterEffectHolder>();
                    }
                    if (!holder2.canNextDot()) continue;
                    burnedstartups.add(holder2);
                }
            }
            if (statups != null && !statups.isEmpty()) {
                this.cancelStatus(statups, true);
            }
        }
        finally {
            this.effectLock.unlock();
        }
        if (burnedstartups != null && !burnedstartups.isEmpty()) {
            burnedstartups.forEach(holder -> this.dotDamage(this.map.getPlayerObject(holder.fromChrID), holder.dotDamage));
        }
    }

    public MapleMonsterStats getStats() {
        return this.stats;
    }

    public boolean isBoss() {
        return this.stats.isBoss() || this.eliteGrade >= 0;
    }

    public int getAnimationTime(String name) {
        return this.stats.getAnimationTime(name);
    }

    public void disableDrops() {
        this.dropsDisabled = true;
    }

    public boolean dropsDisabled() {
        return this.dropsDisabled;
    }

    public final void disableSpawnRevives() {
        this.spawnRevivesDisabled = true;
    }

    public final boolean spawnRevivesDisabled() {
        return this.spawnRevivesDisabled;
    }

    public final Map<String, Integer> getHitParts() {
        return this.stats.getHitParts();
    }

    public int getMobLevel() {
        if (this.forcedMobStat != null) {
            return this.forcedMobStat.getLevel();
        }
        return this.stats.getLevel();
    }

    public long getHp() {
        return this.hp;
    }

    public void setHp(long hp) {
        this.hp = hp;
    }

    public ForcedMobStat getForcedMobStat() {
        return this.forcedMobStat;
    }

    public void setForcedMobStat(ForcedMobStat ostat) {
        this.forcedMobStat = ostat;
    }

    public long getMobMaxHp() {
        return this.maxHP;
    }

    public long getMaxHP() {
        return this.maxHP;
    }

    public void setMaxHP(long maxHP) {
        this.maxHP = maxHP;
    }

    public int getMp() {
        return this.mp;
    }

    public void setMp(int mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    public int getMobMaxMp() {
        return this.maxMP;
    }

    public int getMaxMP() {
        return this.maxMP;
    }

    public void setMaxMP(int maxMP) {
        this.maxMP = maxMP;
    }

    public long getMobExp() {
        if (this.forcedMobStat != null) {
            return this.forcedMobStat.getExp();
        }
        return this.stats.getExp();
    }

    public void changeLevelmod(int newLevel, int mutiplier) {
        this.setForcedMobStat(newLevel);
    }

    public MapleMonster getSponge() {
        return (MapleMonster)this.sponge.get();
    }

    public void setSponge(MapleMonster mob) {
        this.sponge = new WeakReference<MapleMonster>(mob);
        if (this.linkoid <= 0) {
            this.linkoid = mob.getObjectId();
        }
    }

    public void sendMobZone(int reduceDamageType) {
        this.getMap().broadcastMessage(MobPacket.showMonsterPhaseChange(this.getObjectId(), reduceDamageType), this.getPosition());
        this.getMap().broadcastMessage(MobPacket.changeMonsterZone(this.getObjectId(), reduceDamageType), this.getPosition());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void damage(MapleCharacter from, int lastSkill, long damage, boolean notKill) {
        if (from == null || damage <= 0L || !this.isAlive()) {
            return;
        }
        this.hpLock.lock();
        try {
            if (notKill && this.hp <= 1L) {
                return;
            }
            AttackerEntry attacker = from.getParty() != null ? new PartyAttackerEntry(from.getParty().getId()) : new SingleAttackerEntry(from);
            boolean replaced = false;
            for (AttackerEntry aentry : this.getAttackers()) {
                if (!aentry.equals(attacker)) continue;
                attacker = aentry;
                replaced = true;
                break;
            }
            if (!replaced) {
                this.attackers.add(attacker);
            }
            damage = (long)((double)damage * ((100.0 - Math.max(0.0, Math.min(this.reduceDamageR, 99.0))) / 100.0));
            if (this.hpLimitPercent > 0.0) {
                damage = Math.min((long)Math.max(0.0, (double)this.hp - (double)this.getMobMaxHp() * this.hpLimitPercent), damage);
            }
            if (notKill && this.hp < damage) {
                damage = this.hp - 1L;
            }
            long rDamage = Math.max(0L, Math.min(damage, this.hp));
            MapleStatEffect effect = from.getEffectForBuffStat(SecondaryStat.Thaw);
            if (effect != null && !effect.isSkill()) {
                rDamage = 1L;
            }
            attacker.addDamage(from, rDamage);
            if (this.stats.getSelfD() != -1) {
                this.hp -= rDamage;
                if (this.hp > 0L) {
                    for (AttackerEntry mattacker : this.getAttackers()) {
                        for (int cattacker : mattacker.getAttackers()) {
                            MapleCharacter chr = this.map.getPlayerObject(cattacker);
                            if (chr == null) continue;
                            chr.send(MobPacket.showMonsterHP(this.getObjectId(), this.getHPPercent()));
                        }
                    }
                }
                if (this.hp < (long)this.stats.getSelfDHp()) {
                    this.map.killMonster(this, from, false, false, this.stats.getSelfD(), lastSkill);
                }
                if (this.getHPPercent() <= 30 && (this.getId() == 8880100 || this.getId() == 8880110)) {
                    if (!this.demianChangePhase) {
                        this.map.showWeatherEffectNotice("戴米安已經完全陷入黑暗.", 216, 30000000);
                        this.map.broadcastMessage(MobPacket.ChangePhaseDemian(this, 79));
                        this.demianChangePhase = true;
                        Timer.MapTimer.getInstance().schedule(() -> this.map.killMonster(this), 5000L);
                    }
                    return;
                }
            } else {
                int oldValue;
                MapleStatEffect eff;
                MapleMonster sponge = (MapleMonster)this.sponge.get();
                if (!(this.hp <= 0L || this.getStats().isInvincible() && this.getId() != 9400080)) {
                    this.hp -= rDamage;
                }
                if (this.eventInstance != null) {
                    this.eventInstance.getHooks().playerHit(from, this, damage);
                } else {
                    ScriptEvent em = from.getEventInstance();
                    if (em != null) {
                        em.getHooks().playerHit(from, this, damage);
                    }
                }
                if (sponge == null) {
                    switch (this.stats.getHPDisplayType()) {
                        case 0: {
                            this.map.broadcastMessage(FieldPacket.fieldEffect((FieldEffect)FieldEffect.mobHPTagFieldEffect((MapleMonster)this)), this.getPosition());
                            break;
                        }
                        case 1: {
                            this.map.broadcastMessage(from, MobPacket.MobDamaged(this, 1, damage, true), true);
                            break;
                        }
                        case 2: {
                            this.map.broadcastMessage(MobPacket.showMonsterHP(this.getObjectId(), this.getHPPercent()));
                            from.mulung_EnergyModify(true);
                            break;
                        }
                        case 3: {
                            for (AttackerEntry mattacker : this.getAttackers()) {
                                for (int cattacker : mattacker.getAttackers()) {
                                    MapleCharacter chr = this.map.getPlayerObject(cattacker);
                                    if (chr == null) continue;
                                    chr.getClient().announce(MobPacket.showMonsterHP(this.getObjectId(), this.getHPPercent()));
                                }
                            }
                            break;
                        }
                        default: {
                            this.map.broadcastMessage(MobPacket.showMonsterHP(this.getObjectId(), this.getHPPercent()));
                        }
                    }
                }
                if (this.hp <= 0L) {
                    SecondaryStatValueHolder holder;
                    if (this.stats.getHPDisplayType() == 0) {
                        this.map.broadcastMessage(FieldPacket.fieldEffect((FieldEffect)FieldEffect.mobHPTagFieldEffect((int)this.getId(), (long)-1L, (long)1L, (byte)1, (byte)5)), this.getPosition());
                    }
                    if ((holder = from.getBuffStatValueHolder(SecondaryStat.RunePurification)) != null && holder.value == 1) {
                        from.send(MaplePacketCreator.objSkillEffect(this.getObjectId(), 80002888, from.getId(), new Point(0, 0)));
                        MapleForceAtom force = MapleForceFactory.getInstance().getMapleForce(from, from.getEffectForBuffStat(SecondaryStat.RunePurification), 1, this.getObjectId());
                        force.setForcedTarget(this.getPosition());
                        from.send(ForcePacket.forceAtomCreate(force));
                        holder.z = Math.min(holder.z + 25, 1000);
                    }
                    this.lastKill = from.getId();
                    from.getCheatTracker().gainMultiKill();
                    from.getCheatTracker().setLastKillMobOid(this.getObjectId());
                    from.getKillMonsterExp().addAndGet(this.stats.getExp());
                    this.map.killMonster(this, from, true, false, (byte)1, lastSkill);
                    if (from.getKeyValue("DamageGetKillCount") == "true") {
                        from.getClient().outPacket(OutHeader.LP_GetKillCount.getValue(), new Object[0]);
                    }
                    this.sponge.clear();
                }
                if ((eff = from.getSkillEffect(60030241)) == null) {
                    eff = from.getSkillEffect(80003015);
                }
                if (eff != null) {
                    boolean apply = false;
                    if (this.hp <= 0L) {
                        int killCount = (Integer)from.getTempValues().getOrDefault("事前準備擊殺", 0) + 1;
                        if (killCount >= eff.getX()) {
                            killCount = 0;
                            apply = true;
                        }
                        from.getTempValues().put("事前準備擊殺", killCount);
                    } else if (this.isBoss()) {
                        int attackCount = (Integer)from.getTempValues().getOrDefault("事前準備攻擊", 0) + 1;
                        if (attackCount >= eff.getU()) {
                            attackCount = 0;
                            apply = true;
                        }
                        from.getTempValues().put("事前準備攻擊", attackCount);
                    }
                    if (apply) {
                        SecondaryStatValueHolder mbsvh = from.getBuffStatValueHolder(80003018);
                        if (mbsvh == null) {
                            SkillFactory.getSkill(80003018).getEffect(eff.getLevel()).applyTo(from);
                        } else {
                            oldValue = mbsvh.value;
                            if (mbsvh.value < eff.getW() - 1) {
                                ++mbsvh.value;
                            } else if (from.isSkillCooling(eff.getSourceId())) {
                                if (mbsvh.value != eff.getW()) {
                                    mbsvh.value = eff.getW();
                                }
                            } else {
                                from.dispelEffect(80003018);
                                eff.applyTo(from);
                            }
                            if (oldValue != mbsvh.value) {
                                from.send(BuffPacket.giveBuff(from, mbsvh.effect, Collections.singletonMap(SecondaryStat.NALinkSkill, mbsvh.sourceID)));
                            }
                        }
                    }
                }
                if ((eff = from.getSkillEffect(400031066)) != null) {
                    boolean apply = false;
                    if (this.hp <= 0L) {
                        int killCount = (Integer)from.getTempValues().getOrDefault("掌握痛苦擊殺", 0) + 1;
                        if (killCount >= eff.getQ()) {
                            killCount = 0;
                            apply = true;
                        }
                        from.getTempValues().put("掌握痛苦擊殺", killCount);
                    } else if (this.isBoss()) {
                        int attackCount = (Integer)from.getTempValues().getOrDefault("掌握痛苦攻擊", 0) + 1;
                        if (attackCount >= eff.getQ2()) {
                            attackCount = 0;
                            apply = true;
                        }
                        from.getTempValues().put("掌握痛苦攻擊", attackCount);
                    }
                    if (apply) {
                        SecondaryStatValueHolder mbsvh = from.getBuffStatValueHolder(400031066);
                        if (mbsvh == null) {
                            eff.applyTo(from, true);
                        } else {
                            oldValue = mbsvh.value;
                            if (mbsvh.value < eff.getU() - 1) {
                                ++mbsvh.value;
                            } else if (mbsvh.value != eff.getU()) {
                                mbsvh.value = eff.getU();
                            }
                            if (oldValue != mbsvh.value) {
                                from.send(BuffPacket.giveBuff(from, mbsvh.effect, Collections.singletonMap(SecondaryStat.NAOminousStream, mbsvh.sourceID)));
                            }
                        }
                    }
                }
                if (sponge != null) {
                    sponge.damage(from, lastSkill, rDamage, notKill);
                }
                this.checkMobZoneState();
            }
            this.startDropItemSchedule();
        }
        finally {
            this.hpLock.unlock();
        }
    }

    public int getHPPercent() {
        return (int)Math.ceil((double)this.hp * 100.0 / (double)this.getMobMaxHp());
    }

    public void killed() {
        if (this.listener != null) {
            this.listener.monsterKilled();
        }
        this.listener = null;
        if (this.deadBound != null) {
            this.deadBound.cancel(false);
        }
        this.deadBound = null;
        this.hp = 0L;
    }

    private void giveExpToCharacter(MapleCharacter attacker, long baseEXP, int partyMenberSize, int recallRingEXP, int lastSkill, boolean lastKill) {
        if (baseEXP > 0L) {
            attacker.getTrait(MapleTraitType.charisma).addExp(this.stats.getCharismaEXP(), attacker);
            this.handleExp(attacker, baseEXP, partyMenberSize, recallRingEXP, lastKill);
            attacker.getStat().checkEquipLevels(attacker, baseEXP);
            if (lastKill) {
                MobCollectionFactory.tryCollect(attacker, this);
            }
        }
        attacker.mobKilled(this.getId(), lastSkill);
        if (Math.abs(this.getMobLevel() - attacker.getLevel()) <= 20) {
            attacker.mobKilled(9101025, lastSkill);
        }
        if (this.isEliteMob()) {
            attacker.mobKilled(9101067, lastSkill);
        }
        if (this.getEliteType() == 2) {
            attacker.mobKilled(9101064, lastSkill);
        }
        if (this.getMap() != null && this.getMap().getBreakTimeFieldStep() > 0 && !this.isBoss()) {
            attacker.mobKilled(9101114, lastSkill);
        }
        if (this.getMap() != null && this.getMap().getBarrier() > 0 && !this.isBoss()) {
            attacker.mobKilled(9101084, lastSkill);
        }
        if (attacker.getLevel() <= 200 && this.getMobLevel() >= attacker.getLevel() - 10 && this.getMobLevel() <= attacker.getLevel() + 10 || attacker.getLevel() > 200 && this.getMobLevel() >= 200) {
            String date = DateUtil.getFormatDate(System.currentTimeMillis(), "yyyyMMdd");
            if (!date.equals(attacker.getQuestInfo(502117, "date"))) {
                attacker.updateOneQuestInfo(502117, "date", date);
                attacker.updateOneQuestInfo(502117, "count", "0");
            }
            attacker.updateOneQuestInfo(502117, "count", String.valueOf(Integer.valueOf(attacker.getQuestInfo(502117, "count")) + 1));
        }
    }

    public void handleExp(MapleCharacter chr, long gain, int partyMenberSize, int recallRingId, boolean lastKill) {
        int n;
        int n2;
        Object marrChr;
        int runeCurseRate;
        if (ServerConfig.WORLD_BANGAINEXP) {
            chr.dropMessage(6, "管理員禁止了經驗獲取。");
            return;
        }
        if (chr.getEventInstance() != null && chr.getEventInstance().isPractice()) {
            return;
        }
        if (this.map == null) {
            return;
        }
        AtomicLong totalExp = new AtomicLong(0L);
        AtomicLong expLost = new AtomicLong(0L);
        int diseaseType = 0;
        int decExpR = 100;
        DeadDebuff deadBuff = DeadDebuff.getDebuff(chr, -1);
        if (deadBuff != null) {
            diseaseType = 2;
            decExpR = Math.max(0, decExpR - deadBuff.DecExpR);
        }
        if (chr.getRuneUseCooldown() <= 0 && (runeCurseRate = this.map.getRuneCurseRate()) > 0) {
            diseaseType = 1;
            decExpR = Math.max(0, decExpR - runeCurseRate);
        }
        if (!this.isBoss()) {
            int lvGap;
            if (this.map.getBarrier() > 0 || this.map.getBarrierArc() > 0 || this.map.getBarrierAut() > 0) {
                lvGap = Math.abs(chr.getLevel() - this.getMobLevel());
                if (lvGap >= 0 && lvGap < 5) {
                    gain = Math.round((double)gain * 1.1);
                } else if (lvGap > 5 && lvGap < 10) {
                    gain = Math.round((double)gain * 1.05);
                }
            }
            if (Math.abs(chr.getLevel() - this.getMobLevel()) > 10) {
                gain = this.getMobLevel() > chr.getLevel() ? ((lvGap = this.getMobLevel() - chr.getLevel()) <= 20 ? Math.round((double)(gain * (long)(110 - lvGap)) / 100.0) : (lvGap <= 35 ? Math.round((double)(gain * (long)(70 - 4 * (lvGap - 21))) / 100.0) : Math.round((double)gain * 0.1))) : ((lvGap = chr.getLevel() - this.getMobLevel()) <= 20 ? Math.round((double)gain * (100.0 - Math.ceil((lvGap - 10) / 2)) / 100.0) : (lvGap <= 40 ? Math.round((double)(gain * (long)(110 - lvGap)) / 100.0) : Math.round((double)gain * 0.7)));
            }
        }
        gain = gain * (long)ChannelServer.getInstance(this.map.getChannel()).getBaseExpRate() / 100L;
        if (chr.getBuffedValue(SecondaryStat.Curse) != null) {
            gain = gain * 50L / 100L;
        }
        if (gain < 0L) {
            gain = 0L;
        }
        long eachGain = gain * (long)decExpR / 100L;
        totalExp.addAndGet(eachGain);
        expLost.addAndGet(gain - eachGain);
        long mobExp = Math.min(gain, Integer.MAX_VALUE);
        EnumMap<MapleExpStat, Object> expStatup = new EnumMap<MapleExpStat, Object>(MapleExpStat.class);
        int expRate = 0;
        if (partyMenberSize >= 1) {
            partyMenberSize = Math.min(partyMenberSize, 6);
            expRate += 5 * (partyMenberSize * (3 + (1 + partyMenberSize) / 2)) - 20;
            if (this.stats.getPartyBonusRate() > 0) {
                expRate += this.stats.getPartyBonusRate() * Math.min(4, partyMenberSize);
            } else if (this.map.getPartyBonusRate() > 0) {
                expRate += this.map.getPartyBonusRate() * Math.min(4, partyMenberSize);
            }
        }
        long eachExp = Math.max(Math.min((long)expRate * mobExp / 100L, Integer.MAX_VALUE), 0L);
        eachGain = eachExp * (long)decExpR / 100L;
        totalExp.addAndGet(eachGain);
        expLost.addAndGet(eachExp - eachGain);
        if (eachExp > 0L) {
            expStatup.put(MapleExpStat.組隊額外經驗值, Long.valueOf(eachExp));
        }
        expRate = 0;
        if (chr.getMarriageId() > 0 && (marrChr = this.map.getPlayerObject(chr.getMarriageId())) != null) {
            expRate = 100;
        }
        eachExp = Math.max(Math.min((long)expRate * mobExp / 100L, Integer.MAX_VALUE), 0L);
        eachGain = eachExp * (long)decExpR / 100L;
        totalExp.addAndGet(eachGain);
        expLost.addAndGet(eachExp - eachGain);
        if (eachExp > 0L) {
            expStatup.put(MapleExpStat.結婚紅利經驗值, Long.valueOf(eachExp));
        }
        expRate = 0;
        for (Map.Entry entry : chr.getStat().getEquipmentBonusExps().entrySet()) {
            expRate += ((Integer)((List)entry.getValue()).get(!chr.getFairys().containsKey(entry.getKey()) ? 0 : chr.getFairys().get(entry.getKey()).getLeft())).intValue();
        }
        eachExp = Math.max(Math.min((long)expRate * mobExp / 100L, Integer.MAX_VALUE), 0L);
        eachGain = eachExp * (long)decExpR / 100L;
        totalExp.addAndGet(eachGain);
        expLost.addAndGet(eachExp - eachGain);
        if (eachExp > 0L) {
            expStatup.put(MapleExpStat.道具裝備紅利經驗值, Long.valueOf(eachExp));
        }
        expRate = 0;
        if (chr.haveItem(5420008)) {
            expRate = chr.isRedMvp() ? (expRate += 200) : (chr.isDiamondMvp() ? (expRate += 150) : (chr.isGoldMvp() ? (expRate += 100) : (chr.isSilverMvp() ? (expRate += 50) : (expRate += 30))));
        }
        eachExp = Math.max(Math.min((long)expRate * mobExp / 100L, Integer.MAX_VALUE), 0L);
        eachGain = eachExp * (long)decExpR / 100L;
        totalExp.addAndGet(eachGain);
        expLost.addAndGet(eachExp - eachGain);
        if (eachExp > 0L) {
            expStatup.put(MapleExpStat.高級服務贈送經驗值, Long.valueOf(eachExp));
        }
        eachExp = Math.min(Math.max(mobExp * (long)chr.getStat().plusExpRate / 100L, 0L), Integer.MAX_VALUE);
        eachGain = eachExp * (long)decExpR / 100L;
        totalExp.addAndGet(eachGain);
        expLost.addAndGet(eachExp - eachGain);
        if (eachExp > 0L) {
            expStatup.put(MapleExpStat.秘藥額外經驗值, Long.valueOf(eachExp));
        }
        expRate = 0;
        Skill skil = SkillFactory.getSkill(20021110);
        int n3 = chr.getTotalSkillLevel(skil);
        if (n3 > 0 && JobConstants.is精靈遊俠(chr.getJob())) {
            expRate += skil.getEffect(n3).getExpR();
        } else {
            skil = SkillFactory.getSkill(80001040);
            int n4 = chr.getTotalSkillLevel(skil);
            if (n4 > 0) {
                expRate += skil.getEffect(n4).getExpR();
            }
        }
        skil = SkillFactory.getSkill(71000711);
        int n5 = chr.getTotalSkillLevel(skil);
        if (n5 > 0) {
            expRate += skil.getEffect(n5).getExpR();
        }
        if ((n2 = chr.getTotalSkillLevel(skil = SkillFactory.getSkill(131000016))) > 0) {
            expRate += skil.getEffect(n2).getExpR();
        }
        if ((n = chr.getTotalSkillLevel(skil = SkillFactory.getSkill(42111003))) > 0) {
            expRate += skil.getEffect(n).getExpR();
        }
        eachExp = Math.max(Math.min((int)(((double)expRate + (double)chr.getStat().expRPerM / 100.0) * (double)mobExp) / 100, Integer.MAX_VALUE), 0);
        eachGain = eachExp * (long)decExpR / 100L;
        totalExp.addAndGet(eachGain);
        expLost.addAndGet(eachExp - eachGain);
        if (chr.getSkillLevel(80000420) > 0 && eachExp > 0L) {
            expStatup.put(MapleExpStat.極限屬性經驗值, Long.valueOf(eachExp));
        }
        expRate = 0;
        MonsterEffectHolder ms = this.getEffectHolder(MonsterStatus.Showdown);
        if (ms != null && ms.sourceID == 4121017) {
            expRate += ms.value;
        }
        expRate = (int)((double)expRate + (chr.getEXPMod() * 100.0 - 100.0));
        expRate += chr.getStat().expBuff;
        eachExp = Math.max(Math.min((long)(expRate += chr.getStat().getIncEXPr()) * mobExp / 100L, Integer.MAX_VALUE), 0L);
        eachGain = eachExp * (long)decExpR / 100L;
        totalExp.addAndGet(eachGain);
        expLost.addAndGet(eachExp - eachGain);
        if (eachExp > 0L) {
            expStatup.put(MapleExpStat.加持獎勵經驗值, Long.valueOf(eachExp));
        }
        expRate = 0;
        int BuringFieldStep = Math.max(0, this.map.getBreakTimeFieldStep()) * 10;
        eachExp = Math.max(Math.min((long)(expRate += BuringFieldStep) * mobExp / 100L, Integer.MAX_VALUE), 0L);
        eachGain = eachExp * (long)decExpR / 100L;
        totalExp.addAndGet(eachGain);
        expLost.addAndGet(eachExp - eachGain);
        if (eachExp > 0L) {
            expStatup.put(MapleExpStat.燃燒場地獎勵經驗, new Pair<Long, Integer>(eachExp, BuringFieldStep));
        }
        expRate = 0;
        if (recallRingId > 0) {
            expRate += 80;
        }
        eachExp = Math.max(Math.min((long)expRate * mobExp / 100L, Integer.MAX_VALUE), 0L);
        eachGain = eachExp * (long)decExpR / 100L;
        totalExp.addAndGet(eachGain);
        expLost.addAndGet(eachExp - eachGain);
        if (eachExp > 0L) {
            expStatup.put(MapleExpStat.道具名經驗值, new Pair<Long, Integer>(eachExp, recallRingId));
        }
        expRate = 0;
        for (MaplePet pet : chr.getSpawnPets()) {
            if (pet == null || pet.getAddSkill() != 6) continue;
            expRate += expRate == 0 ? 5 : (expRate == 5 ? 7 : 8);
        }
        expRate = Math.min(20, expRate);
        eachExp = Math.max(Math.min((long)expRate * mobExp / 100L, Integer.MAX_VALUE), 0L);
        eachGain = eachExp * (long)decExpR / 100L;
        totalExp.addAndGet(eachGain);
        expLost.addAndGet(eachExp - eachGain);
        if (eachExp > 0L) {
            expStatup.put(MapleExpStat.寵物訓練紅利經驗值, Integer.valueOf((int)eachExp));
        }
        expRate = Math.max(ChannelServer.getInstance(this.map.getChannel()).getDoubleExp() - 1, 0) * 100;
        if (chr.getLevel() < 10 && JobConstants.notNeedSPSkill(chr.getJob())) {
            expRate = 0;
        }
        eachExp = Math.max(Math.min((long)expRate * mobExp / 100L, Integer.MAX_VALUE), 0L);
        eachGain = eachExp * (long)decExpR / 100L;
        totalExp.addAndGet(eachGain);
        expLost.addAndGet(eachExp - eachGain);
        if (eachExp > 0L) {
            expStatup.put(MapleExpStat.活動獎勵經驗值2, Long.valueOf(eachExp));
        }
        expRate = Math.max(ChannelServer.getInstance(this.map.getChannel()).getExpRate() - 1, 0);
        if (chr.getLevel() < 10 && JobConstants.notNeedSPSkill(chr.getJob())) {
            expRate = 0;
        }
        eachExp = Math.max(Math.min((long)expRate * mobExp, Integer.MAX_VALUE), 0L);
        eachGain = eachExp * (long)decExpR / 100L;
        totalExp.addAndGet(eachGain);
        expLost.addAndGet(eachExp - eachGain);
        if (eachExp > 0L) {
            expStatup.put(MapleExpStat.伺服器加持經驗值, Integer.valueOf(expRate * 100));
        }
        expRate = 0;
        ms = this.getEffectHolder(MonsterStatus.ErdaRevert);
        if (ms != null) {
            expRate += 150;
        }
        eachExp = Math.max(Math.min((long)expRate * mobExp / 100L, Integer.MAX_VALUE), 0L);
        eachGain = eachExp * (long)decExpR / 100L;
        totalExp.addAndGet(eachGain);
        expLost.addAndGet(eachExp - eachGain);
        if (eachExp > 0L) {
            expStatup.put(MapleExpStat.艾爾達斯還原追加經驗值, Integer.valueOf((int)eachExp));
        }
        if (totalExp.get() < 0L) {
            totalExp.set(Integer.MAX_VALUE);
        }
        chr.gainExp(totalExp.get(), false, false, lastKill);
        chr.send(MaplePacketCreator.showGainExp(mobExp, true, false, diseaseType, -expLost.get(), expStatup));
    }

    public void killBy(MapleCharacter killer) {
        if (this.eventInstance != null) {
            this.eventInstance.getHooks().mobDied(this, killer);
            this.eventInstance = null;
            return;
        }
        if (killer != null && killer.getEventInstance() != null) {
            killer.getEventInstance().getHooks().mobDied(this, killer);
        }
    }

    public void killGainExp(MapleCharacter chr, int lastSkill) {
        long mobExp = this.getMobExp();
        List<AttackerEntry> list = this.getAttackers();
        for (AttackerEntry attackEntry : list) {
            if (attackEntry == null) continue;
            long baseExp = (long)Math.ceil((double)mobExp * (0.2 * (double)(chr != null && attackEntry.contains(chr) ? 1 : 0) + 0.8 * ((double)attackEntry.getDamage() / (double)this.getMobMaxHp())));
            if (ServerConfig.TESPIA && ServerConfig.MULTIPLAYER_TEST) {
                assert (chr != null);
                if (chr.getMap() == null || chr.getMap().getMapObjectsInRange(chr.getPosition(), chr.getRange(), Collections.singletonList(MapleMapObjectType.PLAYER)).size() < 2) {
                    chr.dropMessage(-1, "由於需要測試多人BUG, 測試服必須附近有其他玩家才能獲得經驗。");
                    if (!chr.isIntern()) continue;
                }
            }
            attackEntry.killedMob(chr != null && chr.getClient() != null ? chr.getClient().getWorld() : null, this, baseExp, lastSkill);
        }
    }

    public void spawnRevives() {
        List<Integer> toSpawn = this.stats.getRevives();
        if (toSpawn == null || this.getLinkCID() > 0 || this.spawnRevivesDisabled) {
            return;
        }
        AbstractLoadedMapleLife spongy = null;
        long spongyHp = 0L;
        this.getId();
        switch (this.getId()) {
            case 8820103: 
            case 8820104: 
            case 8820105: 
            case 8820106: {
                if (!toSpawn.contains(this.getId() - 79)) {
                    toSpawn.add(this.getId() - 79);
                }
            }
            case 8820003: 
            case 8820004: 
            case 8820005: 
            case 8820006: {
                for (int n : toSpawn) {
                    MapleMonster mob = MapleLifeFactory.getMonster(n);
                    if (mob == null) continue;
                    mob.setPosition(this.getPosition());
                    mob.setLinkOid(this.getLinkOid());
                    if (this.dropsDisabled()) {
                        mob.disableDrops();
                    }
                    this.map.spawnMonster(mob, -2, true);
                }
                break;
            }
            case 6160003: 
            case 8820002: 
            case 8820102: 
            case 8820115: 
            case 8820116: 
            case 8820117: 
            case 8820118: 
            case 8820213: 
            case 8820214: 
            case 8820215: 
            case 8820216: 
            case 8820217: 
            case 8820218: 
            case 8820219: 
            case 8820220: 
            case 8820221: 
            case 8820222: 
            case 8820223: 
            case 8820224: 
            case 8820225: 
            case 8820226: 
            case 8820227: 
            case 8840000: 
            case 8850011: 
            case 8950000: 
            case 8950001: 
            case 8950100: 
            case 8950101: {
                break;
            }
            case 0x866E86: 
            case 8810119: 
            case 0x866E88: 
            case 8810121: {
                Iterator<Integer> iterator = toSpawn.iterator();
                while (iterator.hasNext()) {
                    int n = iterator.next();
                    MapleMonster mob = MapleLifeFactory.getMonster(n);
                    mob.setPosition(this.getPosition());
                    if (this.dropsDisabled()) {
                        mob.disableDrops();
                    }
                    switch (mob.getId()) {
                        case 8810119: 
                        case 0x866E88: 
                        case 8810121: 
                        case 8810122: {
                            spongy = mob;
                            ((MapleMonster)spongy).setSpongeMob(true);
                        }
                    }
                }
                if (spongy == null || this.map.getMobObjectByID(spongy.getId()) != null) break;
                this.map.spawnMonster((MapleMonster)spongy, -2);
                for (MapleMapObject mapleMapObject : this.map.getMonsters()) {
                    MapleMonster mons = (MapleMonster)mapleMapObject;
                    if (mons.getObjectId() == spongy.getObjectId() || mons.getSponge() != this && mons.getLinkOid() != this.getObjectId()) continue;
                    mons.setSponge((MapleMonster)spongy);
                }
                break;
            }
            case 8820300: 
            case 8820301: 
            case 8820302: 
            case 8820303: {
                MapleMonster linkMob = MapleLifeFactory.getMonster(this.getId() - 190);
                if (linkMob != null) {
                    toSpawn = linkMob.getStats().getRevives();
                }
            }
            case 8820108: 
            case 8820109: {
                ArrayList<MapleMonster> arrayList = new ArrayList<MapleMonster>();
                block25: for (int i : toSpawn) {
                    MapleMonster mob = MapleLifeFactory.getMonster(i);
                    assert (mob != null);
                    mob.setPosition(this.getPosition());
                    if (this.dropsDisabled()) {
                        mob.disableDrops();
                    }
                    switch (mob.getId()) {
                        case 8820109: 
                        case 8820300: 
                        case 8820301: 
                        case 8820302: 
                        case 8820303: 
                        case 8820304: {
                            spongy = mob;
                            ((MapleMonster)spongy).setSpongeMob(true);
                            continue block25;
                        }
                    }
                    if (mob.isFirstAttack()) {
                        spongyHp += mob.getMobMaxHp();
                    }
                    arrayList.add(mob);
                }
                if (spongy == null || this.map.getMobObjectByID(spongy.getId()) != null) break;
                if (spongyHp > 0L) {
                    ((MapleMonster)spongy).setHp(spongyHp);
                    ((MapleMonster)spongy).getStats().setHp(spongyHp);
                }
                this.map.spawnMonster((MapleMonster)spongy, -2);
                for (MapleMonster i : arrayList) {
                    this.map.spawnMonster(i, -2);
                    i.setSponge((MapleMonster)spongy);
                }
                break;
            }
            case 8880100: 
            case 8880101: 
            case 8880110: 
            case 8880111: {
                if (this.getId() != 8880111 && this.getId() != 8880101 || this.getCustomValue0(8880111) != 1L) break;
                this.addSkillCustomInfo(8880112, 99L);
                if (this.getCustomValue0(8880112) < 1000000000L) break;
                this.getMap().broadcastMessage(MobPacket.demianRunaway(this, (byte)1, MobSkillFactory.getMobSkill(214, 14), 0));
                this.setCustomInfo(8880111, 2, 0);
                break;
            }
            case 8810026: 
            case 8810130: 
            case 8820008: 
            case 8820009: 
            case 8820010: 
            case 8820011: 
            case 8820012: 
            case 8820013: {
                ArrayList<MapleMonster> mobs = new ArrayList<MapleMonster>();
                long maxHP = 0L;
                block27: for (int i : toSpawn) {
                    MapleMonster mob = MapleLifeFactory.getMonster(i);
                    mob.setPosition(this.getPosition());
                    if (this.dropsDisabled()) {
                        mob.disableDrops();
                    }
                    switch (mob.getId()) {
                        case 8810018: 
                        case 0x866E86: 
                        case 8810122: 
                        case 8820009: 
                        case 8820010: 
                        case 8820011: 
                        case 8820012: 
                        case 8820013: 
                        case 8820014: {
                            spongy = mob;
                            ((MapleMonster)spongy).setSpongeMob(true);
                            continue block27;
                        }
                        case 8820002: 
                        case 8820003: 
                        case 8820004: 
                        case 8820005: 
                        case 8820006: {
                            maxHP += mob.getMaxHP();
                        }
                    }
                    mobs.add(mob);
                }
                if (spongy == null || this.map.getMobObjectByID(spongy.getId()) != null) break;
                if (maxHP > 0L && ((MapleMonster)spongy).getMaxHP() != maxHP) {
                    ((MapleMonster)spongy).getStats().setChange(true);
                    ((MapleMonster)spongy).changeHP(maxHP);
                }
                this.map.spawnMonster((MapleMonster)spongy, -2);
                for (MapleMonster i : mobs) {
                    this.map.spawnMonster(i, -2);
                    i.setSponge((MapleMonster)spongy);
                }
                break;
            }
            case 8820304: {
                MapleMonster linkMob_1 = MapleLifeFactory.getMonster(this.getId() - 190);
                if (linkMob_1 != null) {
                    toSpawn = linkMob_1.getStats().getRevives();
                }
            }
            case 8820014: 
            case 8820101: 
            case 8820200: 
            case 8820201: 
            case 8820202: 
            case 8820203: 
            case 8820204: 
            case 8820205: 
            case 8820206: 
            case 8820207: 
            case 8820208: 
            case 8820209: 
            case 8820210: 
            case 8820211: {
                for (int i : toSpawn) {
                    MapleMonster mob = MapleLifeFactory.getMonster(i);
                    mob.setPosition(this.getPosition());
                    if (this.dropsDisabled()) {
                        mob.disableDrops();
                    }
                    this.map.spawnMonster(mob, -2);
                }
                break;
            }
            default: {
                for (int i : toSpawn) {
                    MapleMonster mob = MapleLifeFactory.getMonster(i);
                    if (mob == null) continue;
                    mob.setPosition(this.getPosition());
                    if (this.dropsDisabled()) {
                        mob.disableDrops();
                    }
                    this.map.spawnRevives(mob, this.getObjectId());
                }
            }
        }
    }

    public boolean isAlive() {
        return this.hp > 0L;
    }

    public byte getCarnivalTeam() {
        return this.carnivalTeam;
    }

    public void setCarnivalTeam(byte team) {
        this.carnivalTeam = team;
    }

    public MapleCharacter getController() {
        this.controllerLock.lock();
        try {
            MapleCharacter mapleCharacter = (MapleCharacter)this.controller.get();
            return mapleCharacter;
        }
        finally {
            this.controllerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateController(List<MapleCharacter> characters) {
        if (this.getController() != null || this.hp <= 0L) {
            return;
        }
        this.controllerLock.lock();
        try {
            int mobControlledSize = -1;
            MapleCharacter newController = null;
            for (MapleCharacter chr : characters) {
                if (chr.isHidden() || this.getDwOwnerID() > 0 && this.getDwOwnerID() != chr.getId() || chr.getMap() != this.map || chr.getMobControlledSize() >= mobControlledSize && mobControlledSize != -1 || !(chr.getPosition().distance(this.getPosition()) < (double)this.getRange())) continue;
                mobControlledSize = chr.getMobControlledSize();
                newController = chr;
            }
            if (newController != null) {
                this.setController(newController);
            }
        }
        finally {
            this.controllerLock.unlock();
        }
    }

    public void switchController(MapleCharacter newController) {
        if (!this.controllerHasAggro && this.isAlive()) {
            MapleCharacter controller = this.getController();
            if (controller != null) {
                if (controller == newController) {
                    this.controllerHasAggro = true;
                    return;
                }
                this.removeController(controller);
            }
            this.controllerLock.lock();
            try {
                this.controllerHasAggro = true;
                this.setController(newController);
            }
            finally {
                this.controllerLock.unlock();
            }
        }
    }

    public void setController(MapleCharacter newController) {
        if (this.stats.isDefenseMob()) {
            return;
        }
        this.controller = new WeakReference<MapleCharacter>(newController);
        newController.controlMonster(this);
        newController.getClient().announce(MobPacket.monsterChangeControllerNew(this, newController));
        newController.getClient().announce(MobPacket.monsterChangeController(this, 1, 0));
        if (this.isBoss()) {
            this.changeAggroTip(newController);
        }
    }

    public void removeController(MapleCharacter chr) {
        this.controllerLock.lock();
        try {
            if (chr == this.controller.get()) {
                this.controller = new WeakReference<MapleCharacter>(null);
                this.controllerHasAggro = false;
                chr.controlMonsterRemove(this);
                chr.getClient().announce(MobPacket.monsterChangeControllerNew(this, chr));
                chr.getClient().announce(MobPacket.monsterChangeController(this, 0, 0));
            }
        }
        finally {
            this.controllerLock.unlock();
        }
    }

    public void changeAggroTip(MapleCharacter newController) {
        StringBuilder sb = new StringBuilder();
    }

    public boolean checkAggro(boolean bl2) {
        boolean bl4 = false;
        boolean bl5 = false;
        boolean bl3 = System.currentTimeMillis() - 0L > 3000L;
        boolean bl6 = bl3;
        if (bl3 && (!this.isControllerKnowsAboutAggro() || bl2 && !this.getAggroRank().isEmpty())) {
            MapleCharacter controller = this.getController();
            ArrayList<Map.Entry<String, Integer>> arrayList = new ArrayList<Map.Entry<String, Integer>>(this.aggroRank.entrySet());
            arrayList.sort((entry, entry2) -> (Integer)entry2.getValue() - (Integer)entry.getValue());
            for (Map.Entry<String, Integer> entry3 : arrayList) {
                boolean bl7;
                MapleCharacter newController = this.map.getCharacterByName(entry3.getKey());
                if (newController == null) {
                    this.aggroRank.put(entry3.getKey(), 0);
                    continue;
                }
                boolean bl = bl7 = newController.getPosition().distance(this.getPosition()) < 317.0;
                if (newController.isAlive() && this.getController() == newController && bl7) {
                    bl4 = true;
                    continue;
                }
                if (!newController.isAlive() || bl4 || !bl7) continue;
                controller = this.map.getCharacterByName(entry3.getKey());
                bl4 = true;
            }
            if (controller != null && controller != this.getController()) {
                this.switchController(controller);
                bl5 = true;
            }
        }
        return bl5;
    }

    public void addListener(MonsterListener listener) {
        this.listener = listener;
    }

    public boolean isControllerHasAggro() {
        return this.controllerHasAggro;
    }

    public void setControllerHasAggro(boolean controllerHasAggro) {
        this.controllerHasAggro = controllerHasAggro;
    }

    public boolean isControllerKnowsAboutAggro() {
        return this.controllerKnowsAboutAggro;
    }

    public void setControllerKnowsAboutAggro(boolean controllerKnowsAboutAggro) {
        this.controllerKnowsAboutAggro = controllerKnowsAboutAggro;
    }

    public long getChangeTime() {
        return this.changeTime;
    }

    public void setChangeTime(long changeTime) {
        this.changeTime = changeTime;
    }

    public int getReduceDamageType() {
        return this.reduceDamageType;
    }

    public void setReduceDamageType(int reduceDamageType) {
        this.reduceDamageType = reduceDamageType;
    }

    public int getFollowChrID() {
        return this.followChrID;
    }

    public void setFollowChrID(int followChrID) {
        this.followChrID = followChrID;
    }

    public boolean isEliteMob() {
        return this.eliteGrade >= 0;
    }

    public int getEliteGrade() {
        return this.eliteGrade;
    }

    public void setEliteGrade(int monsterType) {
        this.eliteGrade = monsterType;
    }

    public int getEliteType() {
        return this.eliteType;
    }

    public void setEliteType(int eliteType) {
        this.eliteType = eliteType;
    }

    public void sendStatus(MapleClient client) {
        if (ServerConfig.CHANNEL_APPLYMONSTERSTATUS) {
            return;
        }
        if (this.reflectpack != null) {
            client.announce(this.reflectpack);
        }
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.announce(MobPacket.spawnMonster(this));
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.announce(MobPacket.killMonster(this.getObjectId(), this.getAnimation()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.stats.getName());
        sb.append(" Level=");
        sb.append(this.stats.getLevel());
        if (this.forcedMobStat != null) {
            sb.append("→");
            sb.append(this.forcedMobStat.getLevel());
        }
        sb.append("信息: ");
        sb.append(this.getHp());
        sb.append("/");
        sb.append(this.getMobMaxHp());
        sb.append("Hp, ");
        sb.append(this.getMp());
        sb.append("/");
        sb.append(this.getMobMaxMp());
        sb.append("Mp");
        sb.append("||仇恨目標: ");
        MapleCharacter chr = (MapleCharacter)this.controller.get();
        sb.append(chr != null ? chr.getName() : "無");
        return sb.toString();
    }

    public String toMoreString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.stats.getName());
        sb.append("(");
        sb.append(this.getId());
        sb.append(") Level=");
        sb.append(this.stats.getLevel());
        if (this.forcedMobStat != null) {
            sb.append("→");
            sb.append(this.forcedMobStat.getLevel());
        }
        sb.append(" pos(");
        sb.append(this.getPosition().x);
        sb.append(",");
        sb.append(this.getPosition().y);
        sb.append(") 信息: ");
        sb.append(this.getHp());
        sb.append("/");
        sb.append(this.getMobMaxHp());
        sb.append("Hp, ");
        sb.append(this.getMp());
        sb.append("/");
        sb.append(this.getMobMaxMp());
        sb.append("Mp, oid: ");
        sb.append(this.getObjectId());
        sb.append(", def").append(this.forcedMobStat == null ? this.stats.getPDRate() : this.forcedMobStat.getPDRate());
        sb.append("||仇恨目標: ");
        MapleCharacter chr = (MapleCharacter)this.controller.get();
        sb.append(chr != null ? chr.getName() : "無");
        return sb.toString();
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.MONSTER;
    }

    @Override
    public int getRange() {
        return GameConstants.maxViewRange();
    }

    public ScriptEvent getEventInstance() {
        return this.eventInstance;
    }

    public void setEventInstance(ScriptEvent eventInstance) {
        this.eventInstance = eventInstance;
        if (eventInstance == null && this.deadBound != null) {
            this.deadBound.cancel(true);
            this.deadBound = null;
        }
    }

    public boolean isMobile() {
        return this.stats.isMobile();
    }

    public ElementalEffectiveness getEffectiveness(Element e) {
        return this.stats.getEffectiveness(e);
    }

    public void setDeadBound(Runnable command, long period) {
        if (this.deadBound != null) {
            this.deadBound.cancel(true);
        }
        long repeatTime = 0L;
        this.deadBound = Timer.EtcTimer.getInstance().register(command, repeatTime);
    }

    public void setTempEffectiveness(Element e, long milli) {
        this.stats.setEffectiveness(e, ElementalEffectiveness.虛弱);
        Timer.EtcTimer.getInstance().schedule(() -> this.stats.removeEffectiveness(e), milli);
    }

    public boolean isFake() {
        return this.fake;
    }

    public void setFake(boolean fake) {
        this.fake = fake;
    }

    public MapleMap getMap() {
        return this.map;
    }

    public void setMap(MapleMap map) {
        this.map = map;
        this.startDropItemSchedule();
    }

    public List<Triple<Integer, Integer, Integer>> getSkills() {
        return this.stats.getSkills();
    }

    public int getSkillSize() {
        return this.stats.getSkillSize();
    }

    public boolean isFirstAttack() {
        return this.stats.isFirstAttack();
    }

    public int getBuffToGive() {
        return this.stats.getBuffToGive();
    }

    public int getStolen() {
        return this.stolen;
    }

    public void setStolen(int s) {
        this.stolen = s;
    }

    public int getLinkOid() {
        return this.linkoid;
    }

    public void setLinkOid(int lo) {
        this.linkoid = lo;
    }

    public int getLastNode() {
        return this.lastNode;
    }

    public void setLastNode(int lastNode) {
        this.lastNode = lastNode;
    }

    public boolean checkLastMove() {
        return System.currentTimeMillis() - this.lastMove > 8000L && this.lastMove > 0L;
    }

    public void setLastMove() {
        this.lastMove = System.currentTimeMillis();
    }

    public int getRewardSprinkleCount() {
        return this.rewardSprinkleCount;
    }

    public void setRewardSprinkleCount(int count) {
        this.rewardSprinkleCount = count;
    }

    public void cancelDropItem() {
        this.lastDropTime = 0L;
        this.rewardSprinkleCount = 0;
    }

    public void startDropItemSchedule() {
        this.cancelDropItem();
        if ((!this.stats.isRewardSprinkle() || this.stats.getRewardSprinkleCount() <= 0) && this.stats.getDropItemPeriod() <= 0 || !this.isAlive()) {
            return;
        }
        this.rewardSprinkleCount = this.stats.getRewardSprinkleCount();
        this.lastDropTime = System.currentTimeMillis();
    }

    public boolean shouldDrop(long now) {
        return this.lastDropTime > 0L && this.isAlive() && (!this.stats.isRewardSprinkle() || this.rewardSprinkleCount > 0) && this.lastDropTime + (long)(this.stats.isRewardSprinkle() ? 2 : this.stats.getDropItemPeriod()) * 1000L < now;
    }

    public void doDropItem(long now) {
        if (!this.isAlive() || this.stats.isRewardSprinkle() && this.rewardSprinkleCount <= 0) {
            this.cancelDropItem();
            return;
        }
        ArrayList<Integer> drops = new ArrayList<Integer>();
        for (MonsterDropEntry mde : MapleMonsterInformationProvider.getInstance().retrieveDrop(this.getId())) {
            if (this.map != null && !mde.channels.isEmpty() && !mde.channels.contains(this.map.getChannel()) || mde.itemId <= 0 || Randomizer.nextInt(999999) >= mde.chance) continue;
            drops.add(mde.itemId);
        }
        if (9300061 == this.getId()) {
            drops.add(4001101);
        }
        if (this.isAlive() && this.map != null) {
            for (int i = 0; i < drops.size(); ++i) {
                Collections.shuffle(drops);
                int rewardSprinkleSpeed = this.stats == null ? 0 : this.stats.getRewardSprinkleSpeed();
                this.map.spawnAutoDrop((Integer)drops.get(0), this.getPosition(), rewardSprinkleSpeed, rewardSprinkleSpeed > 0 ? this.getObjectId() : 0);
            }
        }
        if (drops.isEmpty()) {
            return;
        }
        this.lastDropTime = now;
    }

    public byte[] getNodePacket() {
        return this.nodepack;
    }

    public void setNodePacket(byte[] np) {
        this.nodepack = np;
    }

    public void registerKill(long next) {
        this.nextKill = System.currentTimeMillis() + next;
    }

    public boolean shouldKill(long now) {
        return this.nextKill > 0L && now > this.nextKill;
    }

    public int getLinkCID() {
        return this.linkCID;
    }

    public void setLinkCID(int lc) {
        this.linkCID = lc;
    }

    public int getMobFH() {
        MapleFoothold fh = this.getMap().getFootholds().findBelow(this.getPosition());
        if (fh != null) {
            return fh.getId();
        }
        return 0;
    }

    public Map<String, Integer> getAggroRank() {
        return this.aggroRank;
    }

    public int getTriangulation() {
        return this.triangulation;
    }

    public void setTriangulation(int triangulation) {
        this.triangulation = triangulation;
    }

    public int getPatrolScopeX1() {
        return this.patrolScopeX1;
    }

    public void setPatrolScopeX1(int patrolScopeX1) {
        this.patrolScopeX1 = patrolScopeX1;
    }

    public int getPatrolScopeX2() {
        return this.patrolScopeX2;
    }

    public void setPatrolScopeX2(int patrolScopeX2) {
        this.patrolScopeX2 = patrolScopeX2;
    }

    public int getSeperateSoulSrcOID() {
        return this.seperateSoulSrcOID;
    }

    public void setSeperateSoulSrcOID(int seperateSoulSrcOID) {
        this.seperateSoulSrcOID = seperateSoulSrcOID;
    }

    public List<Integer> getReviveMobs() {
        return this.reviveMobs;
    }

    public short getAppearType() {
        return this.newSpawn ? (short)this.appearType : (short)-1;
    }

    public void setAppearType(short appearType) {
        this.appearType = appearType;
    }

    public boolean isSteal() {
        return this.steal;
    }

    public void setSteal(boolean steal) {
        this.steal = steal;
    }

    public void setSoul(boolean soul) {
        this.soul = soul;
    }

    public boolean isSoul() {
        return this.soul;
    }

    public void setNewSpawn(boolean newSpawn) {
        this.newSpawn = newSpawn;
    }

    public void changeHP(long hp) {
        this.maxHP = hp;
        this.hp = hp;
    }

    public void changeBaseHp(int mobId, int x, int y, long hp) {
        MapleMonster monster = MapleLifeFactory.getMonster(mobId);
        if (monster != null) {
            monster.changeHP(hp);
        }
    }

    public List<Integer> getEliteMobActive() {
        return this.eliteMobActive;
    }

    public void setForcedMobStat(int level) {
        double rate = Math.max((double)level * 1.0 / (double)this.stats.getLevel(), 1.0);
        this.forcedMobStat = new ForcedMobStat(this.stats, level, rate);
        if (this.getMobMaxHp() == this.stats.getHp()) {
            this.changeHP(Math.round(!this.stats.isBoss() ? (double)GameConstants.getMonsterHP(level) : (double)this.stats.getHp() * rate));
        }
    }

    public void setForcedMobStat(int level, double rate) {
        this.forcedMobStat = new ForcedMobStat(this.stats, level, rate);
        if (this.getMobMaxHp() == this.stats.getHp()) {
            this.changeHP(Math.round(!this.stats.isBoss() ? (double)GameConstants.getMonsterHP(level) : (double)this.stats.getHp() * rate));
        }
    }

    public void setForcedMobStat(MapleMonsterStats stats) {
        this.forcedMobStat = new ForcedMobStat(stats, stats.getLevel(), 1.0);
        if (this.getMobMaxHp() == stats.getHp()) {
            this.changeHP(stats.getHp());
        }
    }

    public void setChangeHP(long l) {
        this.changeHP(l);
    }

    public void changeBaseHp(long l) {
        this.setChangeHP(l);
    }

    public void setReduceDamageR(double reduceDamageR) {
        this.reduceDamageR = reduceDamageR;
    }

    public double getHpLimitPercent() {
        return this.hpLimitPercent;
    }

    public void setHpLimitPercent(double aEe) {
        this.hpLimitPercent = aEe;
    }

    public int getLastKill() {
        return this.lastKill;
    }

    public void setLastKill(int lastKill) {
        this.lastKill = lastKill;
    }

    public void setAnimation(byte animation) {
        this.animation = animation;
    }

    public byte getAnimation() {
        return this.animation;
    }

    public void setSpongeMob(boolean spongeMob) {
        this.spongeMob = spongeMob;
    }

    public boolean isSpongeMob() {
        return this.spongeMob;
    }

    public void setMobCtrlSN(int mobCtrlSN) {
        this.mobCtrlSN = mobCtrlSN;
    }

    public int getMobCtrlSN() {
        return this.mobCtrlSN;
    }

    public long getTransTime() {
        return this.transTime;
    }

    public void setTransTime(long transTime) {
        this.transTime = transTime;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getScale() {
        return this.scale;
    }

    public void setShowMobId(int id) {
        this.showMobId = id;
    }

    public int getShowMobId() {
        return this.showMobId;
    }

    public boolean isShouldDropAssassinsMark() {
        return this.shouldDropAssassinsMark;
    }

    public void setShouldDropAssassinsMark(boolean b) {
        this.shouldDropAssassinsMark = b;
    }

    public void setUnk(int b) {
        this.bUnk = b;
    }

    public int getUnk() {
        return this.bUnk;
    }

    public void setUnk1(int b) {
        this.bUnk1 = b;
    }

    public int getUnk1() {
        return this.bUnk1;
    }

    public Pair<Long, List<LifeMovementFragment>> getLastRes() {
        return this.lastres;
    }

    public void setLastRes(Pair<Long, List<LifeMovementFragment>> lastres) {
        this.lastres = lastres;
    }

    public void move(List<LifeMovementFragment> lastres) {
        this.lastres = new Pair<Long, List<LifeMovementFragment>>(System.currentTimeMillis(), lastres);
        MovementParse.updatePosition(lastres, this, 0);
        this.getMap().objectMove(0, this, MobPacket.moveMonster(this.getObjectId(), false, -1, 0, 0, (short)0, Collections.emptyList(), Collections.emptyMap(), Collections.emptyMap(), 0, 0, this.getPosition(), new Point(0, 0), lastres));
    }

    public void move(Point pos) {
        MovementNormal alm = new MovementNormal(0, 500, 4, (byte)0);
        alm.setPosition(new Point(pos));
        alm.setFH((short)this.getMap().getFootholds().findBelow(alm.getPosition()).getId());
        alm.setPixelsPerSecond(new Point(0, 0));
        alm.setOffset(new Point(0, 0));
        this.move(Collections.singletonList(alm));
    }

    public List<Integer> getWillHplist() {
        return this.willHplist;
    }

    public void setWillHplist(List<Integer> willHplist) {
        this.willHplist = willHplist;
    }

    public byte getPhase() {
        return this.phase;
    }

    public void setPhase(byte phase) {
        this.phase = phase;
    }

    public long setLastSpecialAttackTime(long lastSpecialAttackTime) {
        this.lastSpecialAttackTime = lastSpecialAttackTime;
        return lastSpecialAttackTime;
    }

    public ScheduledFuture<?> getSchedule() {
        return this.schedule;
    }

    public void setSchedule(ScheduledFuture<?> schedule) {
        this.schedule = schedule;
    }

    public long getLastStoneTime() {
        return this.lastStoneTime;
    }

    public void setLastStoneTime(long lastStoneTime) {
        this.lastStoneTime = lastStoneTime;
    }

    public long getLastSeedCountedTime() {
        return this.lastSeedCountedTime;
    }

    public void setLastSeedCountedTime(long lastSeedCountedTime) {
        this.lastSeedCountedTime = lastSeedCountedTime;
    }

    public int getOwner() {
        return this.owner;
    }

    public void setOwner(int id) {
        this.owner = id;
    }

    public long getLastSpecialAttackTime() {
        return this.lastSpecialAttackTime;
    }

    public boolean isUseSpecialSkill() {
        return this.useSpecialSkill;
    }

    public void setUseSpecialSkill(boolean useSpecialSkill) {
        this.useSpecialSkill = useSpecialSkill;
    }

    public int getNextSkill() {
        return this.nextSkill;
    }

    public void setNextSkill(int nextSkill) {
        this.nextSkill = nextSkill;
    }

    public int getNextSkillLvl() {
        return this.nextSkillLvl;
    }

    public void setNextSkillLvl(int nextSkillLvl) {
        this.nextSkillLvl = nextSkillLvl;
    }

    public long getCustomValue0(int skillid) {
        if (this.customInfo.containsKey(skillid)) {
            return this.customInfo.get(skillid).getValue();
        }
        return 0L;
    }

    public Long getCustomValue(int skillid) {
        if (this.customInfo.containsKey(skillid)) {
            return this.customInfo.get(skillid).getValue();
        }
        return null;
    }

    public void removeCustomInfo(int skillid) {
        this.customInfo.remove(skillid);
    }

    public void setCustomInfo(int skillid, int value, int time) {
        if (this.getCustomValue(skillid) != null) {
            this.removeCustomInfo(skillid);
        }
        this.customInfo.put(skillid, new SkillCustomInfo((long)value, (long)time));
    }

    public double bonusHp() {
        short level = this.stats.getLevel();
        double bonus = 1.0;
        if (level >= 200 && level <= 210) {
            bonus = 1.5;
        } else if (level >= 211 && level <= 220) {
            bonus = 2.0;
        } else if (level >= 221 && level <= 230) {
            bonus = 2.5;
        } else if (level >= 231 && level <= 240) {
            bonus = 3.0;
        } else if (level >= 241) {
            bonus = 3.5;
        }
        if (this.stats.getId() >= 9833070 && this.stats.getId() <= 9833099) {
            bonus = 1.0;
        }
        if (this.stats.isBoss()) {
            switch (this.stats.getId()) {
                case 8644650: 
                case 8644655: 
                case 8645009: {
                    bonus *= 15.0;
                    break;
                }
                case 8880405: 
                case 0x878118: 
                case 8880409: 
                case 8880410: {
                    bonus *= 10.0;
                    break;
                }
                default: {
                    bonus *= 2.0;
                }
            }
        }
        Objects.requireNonNull(this);
        return 1.0;
    }

    public boolean isWillSpecialPattern() {
        return this.willSpecialPattern;
    }

    public void setWillSpecialPattern(boolean willSpecialPattern) {
        this.willSpecialPattern = willSpecialPattern;
    }

    public long getEliteHp() {
        return this.eliteHp;
    }

    public void setEliteHp(long eliteHp) {
        this.eliteHp = eliteHp;
    }

    public int getSerenNoonTotalTime() {
        return this.SerenNoonTotalTime;
    }

    public void setSerenNoonTotalTime(int SerenNoonTotalTime) {
        this.SerenNoonTotalTime = SerenNoonTotalTime;
    }

    public int getSerenSunSetTotalTime() {
        return this.SerenSunSetTotalTime;
    }

    public void setSerenSunSetTotalTime(int SerenSunSetTotalTime) {
        this.SerenSunSetTotalTime = SerenSunSetTotalTime;
    }

    public int getSerenMidNightSetTotalTime() {
        return this.SerenMidNightSetTotalTime;
    }

    public void setSerenMidNightSetTotalTime(int SerenMidNightSetTotalTime) {
        this.SerenMidNightSetTotalTime = SerenMidNightSetTotalTime;
    }

    public int getSerenDawnSetTotalTime() {
        return this.SerenDawnSetTotalTime;
    }

    public void setSerenDawnSetTotalTime(int SerenDawnSetTotalTime) {
        this.SerenDawnSetTotalTime = SerenDawnSetTotalTime;
    }

    public int getSerenNoonNowTime() {
        return this.SerenNoonNowTime;
    }

    public void setSerenNoonNowTime(int SerenNoonNowTime) {
        this.SerenNoonNowTime = SerenNoonNowTime;
    }

    public int getSerenSunSetNowTime() {
        return this.SerenSunSetNowTime;
    }

    public void setSerenSunSetNowTime(int SerenSunSetNowTime) {
        this.SerenSunSetNowTime = SerenSunSetNowTime;
    }

    public int getSerenMidNightSetNowTime() {
        return this.SerenMidNightSetNowTime;
    }

    public void setSerenMidNightSetNowTime(int SerenMidNightSetNowTime) {
        this.SerenMidNightSetNowTime = SerenMidNightSetNowTime;
    }

    public int getSerenDawnSetNowTime() {
        return this.SerenDawnSetNowTime;
    }

    public void setSerenDawnSetNowTime(int SerenDawnSetNowTime) {
        this.SerenDawnSetNowTime = SerenDawnSetNowTime;
    }

    public int getSerenTimetype() {
        return this.SerenTimetype;
    }

    public void setSerenTimetype(int SerenTimetype) {
        this.SerenTimetype = SerenTimetype;
    }

    public void ResetSerenTime(boolean show) {
        this.SerenTimetype = 1;
        this.SerenNoonNowTime = 110;
        this.SerenNoonTotalTime = 110;
        this.SerenSunSetNowTime = 110;
        this.SerenSunSetTotalTime = 110;
        this.SerenMidNightSetNowTime = 30;
        this.SerenMidNightSetTotalTime = 30;
        this.SerenDawnSetNowTime = 110;
        this.SerenDawnSetTotalTime = 110;
        if (show) {
            this.getMap().broadcastMessage(Seren.SerenTimer((int)0, (int[])new int[]{360000, this.SerenNoonTotalTime, this.SerenSunSetTotalTime, this.SerenMidNightSetTotalTime, this.SerenDawnSetTotalTime}));
        }
    }

    public void AddSerenTotalTimeHandler(int type, int add, int turn) {
        this.getMap().broadcastMessage(Seren.SerenTimer((int)1, (int[])new int[]{this.SerenNoonTotalTime, this.SerenSunSetTotalTime, this.SerenMidNightSetTotalTime, this.SerenDawnSetTotalTime, turn}));
    }

    public void AddSerenTimeHandler(int type, int add) {
        int nowtime = 0;
        switch (type) {
            case 1: {
                this.SerenNoonNowTime += add;
                break;
            }
            case 2: {
                this.SerenSunSetNowTime += add;
                for (MapleCharacter chr : this.getMap().getAllChracater()) {
                    if (!chr.isAlive() || chr.getBuffedValue(SecondaryStat.NotDamaged) != null || chr.getBuffedValue(SecondaryStat.NotDamaged) != null) continue;
                    int minushp = (int)(-chr.getStat().getCurrentMaxHp() / 100L);
                    chr.addHP(minushp);
                    chr.getClient().getSession().writeAndFlush(CField.showEffect((MapleCharacter)chr, (int)0, (int)minushp, (int)36, (int)0, (int)0, (byte)0, (boolean)true, null, null, null, null));
                }
                break;
            }
            case 3: {
                this.SerenMidNightSetNowTime += add;
                break;
            }
            case 4: {
                this.SerenDawnSetNowTime += add;
            }
        }
        nowtime = type == 4 ? this.SerenDawnSetNowTime : (type == 3 ? this.SerenMidNightSetNowTime : (type == 2 ? this.SerenSunSetNowTime : this.SerenNoonNowTime));
        MapleMonster seren = null;
        int[] serens = new int[]{8880603, 8880607, 8880609, 8880612};
        for (int ids : serens) {
            seren = this.getMap().getMobObjectByID(ids);
            if (seren != null) break;
        }
        if (nowtime == 3) {
            Iterator<MapleMonster> minushp = this.getMap().getAllMonster().iterator();
            while (minushp.hasNext()) {
                MapleMonster mob = (MapleMonster)minushp.next();
                if (mob.getId() != seren.getId() + 1) continue;
                this.getMap().killMonsterType(mob, 2);
            }
        }
        if (nowtime <= 0 && seren != null) {
            Point pos = seren.getPosition();
            this.getMap().broadcastMessage(Seren.SerenTimer((int)2, (int[])new int[]{1}));
            this.setCustomInfo(8880603, 1, 0);
            this.getMap().broadcastMessage(Seren.SerenChangePhase((String)("Mob/" + seren.getId() + ".img/skill3"), (int)0, (MapleMonster)seren));
            for (MapleMonster mob : this.getMap().getAllMonster()) {
                if (mob.getId() != seren.getId() && mob.getId() != 8880605 && mob.getId() != 8880606 && mob.getId() != 8880611) continue;
                this.getMap().killMonsterType(mob, 2);
            }
            ++this.SerenTimetype;
            if (this.SerenTimetype > 4) {
                this.SerenTimetype = 1;
            }
            switch (this.SerenTimetype) {
                case 1: {
                    this.addHp(this.shield, false);
                    this.shield = -1L;
                    this.shieldmax = -1L;
                    this.getMap().broadcastMessage(FieldPacket.fieldEffect((FieldEffect)FieldEffect.mobHPTagFieldEffect((MapleMonster)this)));
                    this.getMap().broadcastMessage(MobPacket.mobBarrier(this));
                    this.SerenNoonNowTime = this.SerenNoonTotalTime;
                    break;
                }
                case 2: {
                    this.SerenSunSetNowTime = this.SerenSunSetTotalTime;
                    break;
                }
                case 3: {
                    this.SerenMidNightSetNowTime = this.SerenMidNightSetTotalTime;
                    break;
                }
                case 4: {
                    this.SerenDawnSetNowTime = this.SerenDawnSetTotalTime;
                }
            }
            Timer.MapTimer.getInstance().schedule(() -> {
                this.getMap().broadcastMessage(CField.ClearObstacles());
                FieldSkillFactory.getInstance();
                this.getMap().broadcastMessage(CField.useFieldSkill((FieldSkill)FieldSkillFactory.getFieldSkill((int)100024, (int)1)));
            }, 500L);
            Timer.MapTimer.getInstance().schedule(() -> {
                int nextid = type == 4 ? 8880607 : (type == 3 ? 8880603 : (type == 2 ? 8880612 : 8880609));
                this.getMap().spawnMonsterOnGroundBelow(Objects.requireNonNull(MapleLifeFactory.getMonster(nextid)), pos);
                this.getMap().spawnMonsterOnGroundBelow(Objects.requireNonNull(MapleLifeFactory.getMonster(nextid + 1)), new Point(-49, 305));
                if (nextid == 8880603) {
                    this.getMap().spawnMonsterOnGroundBelow(Objects.requireNonNull(MapleLifeFactory.getMonster(8880605)), pos);
                    MapleMonster totalseren = this.getMap().getMobObjectByID(8880602);
                    if (totalseren != null) {
                        totalseren.gainShield(totalseren.getStats().getHp() * 15L / 100L, totalseren.getShield() <= 0L, 0);
                    }
                }
                this.getMap().broadcastMessage(Seren.SerenTimer((int)2, (int[])new int[]{0}));
                this.setCustomInfo(8880603, 0, 0);
                this.getMap().broadcastMessage(Seren.SerenChangeBackground((int)this.SerenTimetype));
            }, 3560L);
        }
    }

    public final void addHp(long hp, boolean brodcast) {
        this.hp = this.getHp() + hp;
        if (this.hp > this.getStats().getHp()) {
            this.hp = this.getStats().getHp();
        }
        if (brodcast) {
            this.getMap().broadcastMessage(FieldPacket.fieldEffect((FieldEffect)FieldEffect.mobHPTagFieldEffect((MapleMonster)this)));
        }
        if (this.hp <= 0L) {
            this.map.killMonster(this, (MapleCharacter)this.controller.get(), true, false, (byte)1, 0);
        }
    }

    public void gainShield(long energy, boolean first, int delayremove) {
        this.shield += energy;
        if (first) {
            this.shield = energy;
            this.shieldmax = energy;
            if (delayremove > 0) {
                Timer.EtcTimer.getInstance().schedule(() -> {
                    this.shield = 0L;
                    this.shieldmax = 0L;
                    this.getMap().broadcastMessage(MobPacket.mobBarrier(this));
                }, (long)delayremove * 1000L);
            }
        }
        this.getMap().broadcastMessage(MobPacket.mobBarrier(this));
    }

    public void setShield(long shield) {
        this.shield = shield;
    }

    public void addSkillCustomInfo(int skillid, long value) {
        this.customInfo.put(skillid, new SkillCustomInfo(this.getCustomValue0(skillid) + value, 0L));
    }

    public void setStigmaType(int rand) {
    }

    public void DemainChangePhase(MapleCharacter from) {
        if (!this.demianChangePhase) {
            this.map.showWeatherEffectNotice("戴米安已經完全陷入黑暗.", 216, 30000000);
            this.map.broadcastMessage(MobPacket.ChangePhaseDemian(this, 79));
            this.demianChangePhase = true;
            if (from.getEventInstance() != null) {
                from.getEventInstance().getHooks().mobDied(this, from);
            }
            Timer.MapTimer.getInstance().schedule(() -> this.map.killMonsterType(this, 0), 6000L);
        }
    }

    public boolean isDemianChangePhase() {
        return this.demianChangePhase;
    }

    public void setDemianChangePhase(boolean demianChangePhase) {
        this.demianChangePhase = demianChangePhase;
    }

    private static final class SingleAttackerEntry
    implements AttackerEntry {
        private final int chrid;
        private long damage = 0L;

        public SingleAttackerEntry(MapleCharacter from) {
            this.chrid = from.getId();
        }

        @Override
        public void addDamage(MapleCharacter from, long damage) {
            if (this.chrid == from.getId()) {
                this.damage += damage;
            }
        }

        @Override
        public List<Integer> getAttackers() {
            return Collections.singletonList(this.chrid);
        }

        @Override
        public boolean contains(MapleCharacter chr) {
            if (chr == null) {
                return false;
            }
            return this.chrid == chr.getId();
        }

        @Override
        public long getDamage() {
            return this.damage;
        }

        @Override
        public void killedMob(World world, MapleMonster monster, long baseExp, int lastSkill) {
            MapleCharacter chr = monster.getMap().getPlayerObject(this.chrid);
            if (chr != null && chr.isAlive()) {
                monster.giveExpToCharacter(chr, baseExp, 1, 0, 0, this.chrid == monster.getLastKill());
            }
        }

        public int hashCode() {
            return this.chrid;
        }

        public boolean equals(Object obj) {
            return this == obj || obj != null && this.getClass() == obj.getClass() && this.chrid == ((SingleAttackerEntry)obj).chrid;
        }
    }

    public static interface AttackerEntry {
        public List<Integer> getAttackers();

        public void addDamage(MapleCharacter var1, long var2);

        public long getDamage();

        public boolean contains(MapleCharacter var1);

        public void killedMob(World var1, MapleMonster var2, long var3, int var5);
    }

    private static final class OnePartyAttacker {

        public int partyID;
        public long damage;

        public OnePartyAttacker(int partyID, long damage) {
            this.partyID = partyID;
            this.damage = damage;
        }
    }
    private static class PartyAttackerEntry implements AttackerEntry {

        private final Map<Integer, OnePartyAttacker> attackers = new HashMap<>(6);
        private final int partyid;
        private long totDamage = 0;

        public PartyAttackerEntry(int partyid) {
            this.partyid = partyid;
        }

        @Override
        public List<Integer> getAttackers() {
            return new ArrayList<>(attackers.keySet());
        }

        @Override
        public boolean contains(MapleCharacter chr) {
            if (chr == null) {
                return false;
            }
            return attackers.containsKey(chr.getId());
        }

        @Override
        public long getDamage() {
            return totDamage;
        }

        @Override
        public void addDamage(MapleCharacter from, long damage) {
            OnePartyAttacker oldPartyAttacker = attackers.get(from.getId());
            if (oldPartyAttacker != null) {
                oldPartyAttacker.damage += damage;
                oldPartyAttacker.partyID = from.getParty().getId();
            } else {
                OnePartyAttacker onePartyAttacker = new OnePartyAttacker(from.getParty().getId(), damage);
                attackers.put(from.getId(), onePartyAttacker);
            }
            totDamage += damage;
        }

        @Override
        public void killedMob(World world, MapleMonster monster, long baseExp, int lastSkill) {
            MapleCharacter pchr;
            long iexp;
            Party party = world == null ? null : world.getPartybyId(this.partyid);
            double addedPartyLevel = 0, levelMod;
            int recallRingId = 0;
            List<MapleCharacter> memberApplicable = new ArrayList<>();
            if (party != null) {
                for (PartyMember pm : party.getMembers()) {
                    if (pm == null || !pm.isOnline()) {
                        continue;
                    }
                    pchr = pm.getChr();
                    if (pchr != null) {
                        if (pm.getChannel() != monster.getMap().getChannel() || pm.getFieldID() != monster.getMap().getId()) {
                            continue;
                        }
                        if (recallRingId == 0 && pchr.getStat().getRecallRingId() > 0) {
                            recallRingId = pchr.getStat().getRecallRingId();
                        }
                        int lvGap = Math.abs(monster.getMobLevel() - pchr.getLevel());
                        if (pchr.isAlive() && (pchr.getId() == monster.getLastKill() || lvGap < 40 && pchr.getCheatTracker().isAttacking())) {
                            memberApplicable.add(pchr);
                        }
                    }
                }
            }
            List<MapleCharacter> expApplicable = new ArrayList<>();
            Map<MapleCharacter, Double> damageDealtMap = new HashMap<>();
            for (MapleCharacter playerObject : memberApplicable) {
                boolean isChrLvGap = false;
                for (PartyMember pm : party.getMembers()) {
                    if (!pm.isOnline() || playerObject.getId() == pm.getCharID()) {
                        continue;
                    }
                    if (Math.abs(pm.getLevel() - playerObject.getLevel()) <= 5) {
                        isChrLvGap = true;
                        break;
                    }
                }
                long damage = 0;
                for (final Map.Entry<Integer, OnePartyAttacker> entry : this.attackers.entrySet()) {
                    if (entry.getKey() == playerObject.getId()) {
                        damage = entry.getValue().damage;
                        break;
                    }
                }
                if (playerObject.getMap() != monster.getMap()) {
                    continue;
                }
                if (damage <= 0 && Math.abs(monster.getMobLevel() - playerObject.getLevel()) > 5 && !isChrLvGap && playerObject.getId() != monster.getLastKill()) {
                    continue;
                }
                expApplicable.add(playerObject);
                addedPartyLevel += playerObject.getLevel();
                damageDealtMap.put(playerObject, (double) damage / this.totDamage);
            }
            if (expApplicable.isEmpty()) {
                return;
            }
            Map<MapleCharacter, ExpMap> expMap = new HashMap<>();
            for (final MapleCharacter expReceiver : expApplicable) {
                double damageDealt = (damageDealtMap.get(expReceiver) != null) ? damageDealtMap.get(expReceiver) : 0.0;
                levelMod = 0.8 * expReceiver.getLevel() / addedPartyLevel;
                iexp = Math.round(baseExp * (0.2 * damageDealt + levelMod));
                expMap.put(expReceiver, new ExpMap(iexp, (byte) expApplicable.size(), 0, 0, expApplicable.size() > 1 ? recallRingId : 0));
            }
            ExpMap expmap;
            for (Map.Entry<MapleCharacter, ExpMap> expReceiver : expMap.entrySet()) {
                expmap = expReceiver.getValue();
                monster.giveExpToCharacter(expReceiver.getKey(), expmap.exp, expmap.ptysize, expmap.RecallRingId, lastSkill, expReceiver.getKey().getId() == monster.getLastKill());
            }
        }

        @Override
        public int hashCode() {
            return 31 + partyid;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj != null && getClass() == obj.getClass() && partyid == ((PartyAttackerEntry) obj).partyid;
        }


        private static final class ExpMap {

            private final long exp;
            private final byte ptysize;
            private final int Class_Bonus_EXP;
            private final int Premium_Bonus_EXP;
            private final int RecallRingId;

            public ExpMap(final long exp, final byte ptysize, final int Class_Bonus_EXP, final int Premium_Bonus_EXP, final int RecallRingId) {
                this.exp = exp;
                this.ptysize = ptysize;
                this.Class_Bonus_EXP = Class_Bonus_EXP;
                this.Premium_Bonus_EXP = Premium_Bonus_EXP;
                this.RecallRingId = RecallRingId;
            }
        }
        //</editor-fold>
    }
}

