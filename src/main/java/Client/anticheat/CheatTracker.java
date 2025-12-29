/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.MapleCharacterUtil
 *  Client.anticheat.CheatingOffenseEntry
 *  Client.anticheat.CheatingOffensePersister
 *  Server.world.WorldBroadcastService
 */
package Client.anticheat;

import Client.MapleAntiMacro;
import Client.MapleCharacter;
import Client.MapleCharacterUtil;
import Client.anticheat.CheatingOffense;
import Client.anticheat.CheatingOffenseEntry;
import Client.anticheat.CheatingOffensePersister;
import Client.skills.SkillFactory;
import Config.constants.SkillConstants;
import Config.constants.enums.UserChatMessageType;
import Net.server.AutobanManager;
import Net.server.maps.MapleRuneStone;
import Packet.MaplePacketCreator;
import Packet.PacketHelper;
import Server.world.WorldBroadcastService;
import Server.world.WorldFindService;
import java.awt.Point;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Randomizer;
import tools.StringUtil;

public class CheatTracker {
    private static final Logger log = LoggerFactory.getLogger(CheatTracker.class);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock rL = this.lock.readLock();
    private final Lock wL = this.lock.writeLock();
    private final Map<CheatingOffense, CheatingOffenseEntry> offenses = new EnumMap<CheatingOffense, CheatingOffenseEntry>(CheatingOffense.class);
    private Integer ownerid;
    private volatile long lastAttackTime = 0L;
    private volatile int lastAttackTickCount = 0;
    private volatile byte tickResetCount = 0;
    private volatile long Server_ClientAtkTickDiff = 0L;
    private volatile long lastDamage = 0L;
    private volatile long takingDamageSince;
    private volatile int numSequentialDamage = 0;
    private volatile long lastDamageTakenTime = 0L;
    private volatile byte numZeroDamageTaken = 0;
    private volatile int numSameDamage = 0;
    private volatile Point lastMonsterMove;
    private volatile int monsterMoveCount;
    private volatile int attacksWithoutHit = 0;
    private volatile byte dropsPerSecond = 0;
    private volatile long lastDropTime = 0L;
    private volatile byte msgsPerSecond = 0;
    private volatile long lastMsgTime = 0L;
    private ScheduledFuture<?> invalidationTask;
    private volatile int gm_message = 0;
    private volatile int lastTickCount = 0;
    private volatile int tickSame = 0;
    private volatile int inMapTimeCount = 0;
    private volatile int lastPickupkCount = 0;
    private volatile long lastSmegaTime = 0L;
    private volatile long lastBBSTime = 0L;
    private volatile long lastASmegaTime = 0L;
    private volatile long lastMZDTime = 0L;
    private volatile long lastCraftTime = 0L;
    private volatile long lastSaveTime = 0L;
    private volatile long lastLieDetectorTime = 0L;
    private volatile long lastPickupkTime = 0L;
    private volatile long lastlogonTime;
    private volatile int numSequentialFamiliarAttack = 0;
    private volatile long familiarSummonTime = 0L;
    private volatile int lastAttackSkill;
    private volatile long next絕殺刃;
    private volatile long next死神契約;
    private volatile long nextBonusAttack;
    private volatile long nextShadowDodge;
    private volatile long nextAegisSystem;
    private volatile int lastChannelTick;
    private volatile long lastEnterChannel;
    private volatile long mapChangeTime;
    public volatile int inMapAttackMinutes;
    private volatile long nextVampiricTouch;
    private volatile int shadowBat;
    private volatile long next追縱火箭;
    private volatile long nextElementalFocus;
    private volatile long nextPantherAttack;
    private volatile long nextHealHPMP = 0L;
    private volatile long nextHealHPMPS = 0L;
    private volatile long lastRecoveryPowerTime;
    private final AtomicInteger monsterCombo = new AtomicInteger(1);
    private final Map<Integer, Long> bgn = new HashMap<Integer, Long>();
    private final AtomicInteger multiKill = new AtomicInteger(0);
    private final AtomicInteger lastKillMobOid = new AtomicInteger(0);
    private volatile long next幻影分身符;
    private volatile long next蝶夢;

    public CheatTracker(Integer ownerid) {
        this.start(ownerid);
    }

    private MapleCharacter getPlayer() {
        return WorldFindService.getInstance().findCharacterById(this.ownerid);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void checkAttack(int skillId, int tickcount) {
        this.wL.lock();
        try {
            long STime_TC;
            this.updateTick(tickcount);
            int AtkDelay = SkillConstants.getAttackDelay(skillId, SkillFactory.getSkill(skillId));
            this.lastAttackTime = System.currentTimeMillis();
            if (skillId != 64101009 && skillId != 64111013 && skillId != 64121020) {
                this.lastAttackSkill = skillId;
            }
            if (this.lastAttackTime - this.mapChangeTime > 0L) {
                this.inMapAttackMinutes = (int)((this.lastAttackTime - this.mapChangeTime) / 60000L);
                MapleCharacter player = this.getPlayer();
                if (player != null) {
                    MapleRuneStone rune;
                    System.out.println("開始檢測 - 是否檢測: " + !player.isInTownMap() + " 是否有怪物: " + player.getMap().getMobsSize() + " 是否在活動地圖: " + (player.getEventInstance() != null));
                    if (this.inMapAttackMinutes >= 60) {
                        WorldBroadcastService.getInstance().broadcastGMMessage(MaplePacketCreator.spouseMessage(UserChatMessageType.淺紫, "[GM訊息] " + player.getName() + " ID: " + player.getId() + " (等級 " + player.getLevel() + ") 在地圖: " + player.getMapId() + " 打怪時間超過1小時，該玩家可能是在掛機打怪。"));
                    }
                    ++this.inMapTimeCount;
                    if (player.getRuneUseCooldown() <= 0 && player.getMap() != null && (rune = player.getMap().getCurseRune()) != null && rune.getCurseStage() > 0 && this.inMapAttackMinutes >= 1 && this.inMapTimeCount >= Randomizer.nextInt(10) && rune.getCurseRate() > Randomizer.nextInt(1000)) {
                        this.inMapTimeCount = 0;
                        if (MapleAntiMacro.startAnti(null, player, (byte)0)) {
                            log.info("[作弊] " + player.getName() + " (等級 " + player.getLevel() + ") 在詛咒" + rune.getCurseStage() + "階段的地圖: " + player.getMapId() + " 打怪時間超過 1 分鐘，系統啟動測謊機系統。");
                            WorldBroadcastService.getInstance().broadcastGMMessage(MaplePacketCreator.spouseMessage(UserChatMessageType.淺紫, "[GM訊息] " + player.getName() + " ID: " + player.getId() + " (等級 " + player.getLevel() + ") 在詛咒" + rune.getCurseStage() + "階段地圖: " + player.getMapId() + " 打怪時間超過 1 分鐘，系統啟動測謊機系統。"));
                        }
                    }
                }
            }
            this.tickResetCount = (byte)(this.tickResetCount + 1);
            if (this.tickResetCount >= (AtkDelay <= 200 ? (byte)1 : 4)) {
                this.tickResetCount = 0;
            }
            if (this.Server_ClientAtkTickDiff - (STime_TC = this.lastAttackTime - (long)tickcount) > 1000L) {
                MapleCharacter player = this.getPlayer();
                if (player != null && player.isAdmin()) {
                    player.dropMessage(-5, "攻擊速度異常2 技能: " + skillId + " 當前: " + (this.Server_ClientAtkTickDiff - STime_TC));
                }
                this.registerOffense(CheatingOffense.FASTATTACK2, "攻擊速度異常。");
            }
            this.updateTick(tickcount);
            this.multiKill.set(0);
        }
        finally {
            this.wL.unlock();
        }
    }

    public void resetInMapTime() {
        this.inMapTimeCount = 0;
        this.mapChangeTime = PacketHelper.getTime(System.currentTimeMillis());
    }

    public void checkPVPAttack(int skillId) {
        int AtkDelay = SkillConstants.getAttackDelay(skillId, skillId == 0 ? null : SkillFactory.getSkill(skillId));
        long STime_TC = System.currentTimeMillis() - this.lastAttackTime;
        if (STime_TC < (long)AtkDelay) {
            this.registerOffense(CheatingOffense.FASTATTACK, "攻擊速度異常。");
        }
        this.lastAttackTime = System.currentTimeMillis();
    }

    public long getLastAttack() {
        return this.lastAttackTime;
    }

    public void checkTakeDamage(int damage) {
        ++this.numSequentialDamage;
        this.lastDamageTakenTime = System.currentTimeMillis();
        if (this.lastDamageTakenTime - this.takingDamageSince / 500L < (long)this.numSequentialDamage) {
            this.registerOffense(CheatingOffense.FAST_TAKE_DAMAGE, "掉血次數異常。");
        }
        if (this.lastDamageTakenTime - this.takingDamageSince > 4500L) {
            this.takingDamageSince = this.lastDamageTakenTime;
            this.numSequentialDamage = 0;
        }
        if (damage == 0) {
            this.numZeroDamageTaken = (byte)(this.numZeroDamageTaken + 1);
            if (this.numZeroDamageTaken >= 50) {
                this.numZeroDamageTaken = 0;
                this.registerOffense(CheatingOffense.HIGH_AVOID, "迴避率過高。");
            }
        } else if (damage != -1) {
            this.numZeroDamageTaken = 0;
        }
    }

    public void resetTakeDamage() {
        this.numZeroDamageTaken = 0;
    }

    public void checkSameDamage(long dmg, double expected) {
        MapleCharacter player = this.getPlayer();
        if (dmg > 2000L && this.lastDamage == dmg && player != null && (player.getLevel() < 190 || (double)dmg > expected * 2.0)) {
            ++this.numSameDamage;
            if (this.numSameDamage > 5) {
                this.registerOffense(CheatingOffense.SAME_DAMAGE, this.numSameDamage + " 次，攻擊傷害 " + dmg + "，預期傷害 " + expected + " [等級: " + player.getLevel() + "，職業: " + player.getJob() + "]");
                this.numSameDamage = 0;
            }
        } else {
            this.lastDamage = dmg;
            this.numSameDamage = 0;
        }
    }

    public void checkHighDamage(int eachd, double maxDamagePerHit, int mobId, int skillId) {
        MapleCharacter player = this.getPlayer();
        if ((double)eachd > maxDamagePerHit && maxDamagePerHit > 2000.0 && player != null) {
            this.registerOffense(CheatingOffense.HIGH_DAMAGE, "[傷害: " + eachd + "，預計傷害: " + maxDamagePerHit + "，怪物ID: " + mobId + "] [職業: " + player.getJob() + "，等級: " + player.getLevel() + "，技能: " + skillId + "]");
            if ((double)eachd > maxDamagePerHit * 2.0) {
                this.registerOffense(CheatingOffense.HIGH_DAMAGE_2, "[傷害: " + eachd + "，預計傷害: " + maxDamagePerHit + "，怪物ID: " + mobId + "] [職業: " + player.getJob() + "，等級: " + player.getLevel() + "，技能: " + skillId + "]");
            }
        }
    }

    public void checkMoveMonster(Point pos) {
        if (pos.equals(this.lastMonsterMove)) {
            ++this.monsterMoveCount;
            if (this.monsterMoveCount > 10) {
                this.registerOffense(CheatingOffense.MOVE_MONSTERS, "吸怪 座標: " + pos.x + ", " + pos.y);
                this.monsterMoveCount = 0;
            }
        } else {
            this.lastMonsterMove = pos;
            this.monsterMoveCount = 1;
        }
    }

    public void resetFamiliarAttack() {
        this.familiarSummonTime = System.currentTimeMillis();
        this.numSequentialFamiliarAttack = 0;
    }

    public boolean checkFamiliarAttack() {
        ++this.numSequentialFamiliarAttack;
        if ((System.currentTimeMillis() - this.familiarSummonTime) / 1000L < (long)this.numSequentialFamiliarAttack) {
            this.registerOffense(CheatingOffense.FAST_SUMMON_ATTACK, "召喚獸攻擊速度異常。");
            return false;
        }
        return true;
    }

    public void checkPickup(int count, boolean pet) {
        if (System.currentTimeMillis() - this.lastPickupkTime < 1000L) {
            ++this.lastPickupkCount;
            MapleCharacter player = this.getPlayer();
            if (this.lastPickupkCount >= count && player != null && !player.isGm()) {
                log.info("[作弊] " + player.getName() + " (等級 " + player.getLevel() + ") " + (pet ? "寵物" : "角色") + "拾取次數過多: " + this.lastPickupkCount + "，伺服器斷開他的連接。");
                player.getClient().disconnect(true, false);
                if (player.getClient() != null && player.getClient().getSession().isActive()) {
                    player.getClient().getSession().close();
                }
            }
        } else {
            this.lastPickupkCount = 0;
        }
        this.lastPickupkTime = System.currentTimeMillis();
    }

    public void checkDrop() {
        this.checkDrop(false);
    }

    public void checkDrop(boolean dc) {
        if (System.currentTimeMillis() - this.lastDropTime < 1000L) {
            this.dropsPerSecond = (byte)(this.dropsPerSecond + 1);
            MapleCharacter player = this.getPlayer();
            if (this.dropsPerSecond >= (dc ? (byte)32 : 16) && player != null && !player.isGm()) {
                if (dc) {
                    player.getClient().disconnect(true, false);
                    if (player.getClient().getSession().isActive()) {
                        player.getClient().getSession().close();
                    }
                    log.info("[作弊] " + player.getName() + " (等級 " + player.getLevel() + ") 丟棄次數過多: " + this.dropsPerSecond + "，伺服器斷開他的連接。");
                } else {
                    player.getClient().setMonitored(true);
                }
            }
        } else {
            this.dropsPerSecond = 0;
        }
        this.lastDropTime = System.currentTimeMillis();
    }

    public void checkMsg() {
        if (System.currentTimeMillis() - this.lastMsgTime < 1000L) {
            this.msgsPerSecond = (byte)(this.msgsPerSecond + 1);
            MapleCharacter player = this.getPlayer();
            if (this.msgsPerSecond > 10 && player != null && !player.isGm()) {
                player.getClient().disconnect(true, false);
                if (player.getClient().getSession().isActive()) {
                    player.getClient().getSession().close();
                }
                log.info("[作弊] " + player.getName() + " (等級 " + player.getLevel() + ") 發送訊息過多: " + this.msgsPerSecond + "，伺服器斷開他的連接。");
            }
        } else {
            this.msgsPerSecond = 0;
        }
        this.lastMsgTime = System.currentTimeMillis();
    }

    public int getAttacksWithoutHit() {
        return this.attacksWithoutHit;
    }

    public void setAttacksWithoutHit(boolean increase) {
        this.attacksWithoutHit = increase ? ++this.attacksWithoutHit : 0;
    }

    public void registerOffense(CheatingOffense offense) {
        this.registerOffense(offense, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerOffense(CheatingOffense offense, String param) {
        MapleCharacter chrhardref = this.getPlayer();
        if (chrhardref == null || !offense.isEnabled() || chrhardref.isGm()) {
            return;
        }
        CheatingOffenseEntry entry = null;
        this.rL.lock();
        try {
            entry = this.offenses.get((Object)offense);
        }
        finally {
            this.rL.unlock();
        }
        if (entry != null && entry.isExpired()) {
            this.expireEntry(entry);
            entry = null;
            this.gm_message = 0;
        }
        if (entry == null) {
            entry = new CheatingOffenseEntry(offense, chrhardref.getId());
        }
        if (param != null) {
            entry.setParam(param);
        }
        entry.incrementCount();
        if (offense.shouldAutoban(entry.getCount())) {
            byte type = offense.getBanType();
            if (type == 1) {
                AutobanManager.getInstance().autoban(chrhardref.getClient(), StringUtil.makeEnumHumanReadable(offense.name()));
            } else if (type == 2) {
                chrhardref.getClient().disconnect(true, false);
                if (chrhardref.getClient().getSession().isActive()) {
                    chrhardref.getClient().getSession().close();
                }
                log.info("[作弊] " + chrhardref.getName() + " (等級:" + chrhardref.getLevel() + " 職業:" + chrhardref.getJob() + ") 伺服器斷開他的連接。原因: " + StringUtil.makeEnumHumanReadable(offense.name()) + (String)(param == null ? "" : " - " + param));
            }
            this.gm_message = 0;
            return;
        }
        this.wL.lock();
        try {
            this.offenses.put(offense, entry);
        }
        finally {
            this.wL.unlock();
        }
        if (offense == CheatingOffense.SAME_DAMAGE) {
            ++this.gm_message;
            if (this.gm_message % 100 == 0) {
                log.info("[作弊] " + MapleCharacterUtil.makeMapleReadable((String)chrhardref.getName()) + " ID: " + chrhardref.getId() + " (等級 " + chrhardref.getLevel() + ") 使用非法程式! " + StringUtil.makeEnumHumanReadable(offense.name()) + (String)(param == null ? "" : " - " + param));
                WorldBroadcastService.getInstance().broadcastGMMessage(MaplePacketCreator.spouseMessage(UserChatMessageType.淺紫, "[GM訊息] " + MapleCharacterUtil.makeMapleReadable((String)chrhardref.getName()) + " ID: " + chrhardref.getId() + " (等級 " + chrhardref.getLevel() + ") 使用非法程式! " + StringUtil.makeEnumHumanReadable(offense.name()) + (String)(param == null ? "" : " - " + param)));
            }
            if (this.gm_message >= 20 && chrhardref.getLevel() < (offense == CheatingOffense.SAME_DAMAGE ? 180 : 190)) {
                Timestamp chrCreated = chrhardref.getChrCreated();
                long time = System.currentTimeMillis();
                if (chrCreated != null) {
                    time = chrCreated.getTime();
                }
                if (time + 1296000000L >= System.currentTimeMillis()) {
                    AutobanManager.getInstance().autoban(chrhardref.getClient(), StringUtil.makeEnumHumanReadable(offense.name()) + " 超過500次 " + (String)(param == null ? "" : " - " + param));
                } else {
                    this.gm_message = 0;
                    WorldBroadcastService.getInstance().broadcastGMMessage(MaplePacketCreator.spouseMessage(UserChatMessageType.淺紫, "[GM訊息] " + MapleCharacterUtil.makeMapleReadable((String)chrhardref.getName()) + " ID: " + chrhardref.getId() + " (等級 " + chrhardref.getLevel() + ") 使用非法程式! " + StringUtil.makeEnumHumanReadable(offense.name()) + (String)(param == null ? "" : " - " + param)));
                    log.info("[GM訊息] " + MapleCharacterUtil.makeMapleReadable((String)chrhardref.getName()) + " ID: " + chrhardref.getId() + " (等級 " + chrhardref.getLevel() + ") 使用非法程式! " + StringUtil.makeEnumHumanReadable(offense.name()) + (String)(param == null ? "" : " - " + param));
                }
            }
        }
        CheatingOffensePersister.getInstance().persistEntry(entry);
    }

    public void updateTick(int newTick) {
        this.wL.lock();
        try {
            if (newTick <= this.lastTickCount) {
                MapleCharacter player = this.getPlayer();
                if (this.tickSame >= 30 && player != null && !player.isGm()) {
                    log.info("[作弊] " + player.getName() + " (等級 " + player.getLevel() + ") 更新Tick次數過多: " + this.tickSame);
                } else {
                    ++this.tickSame;
                }
            } else {
                this.tickSame = 0;
            }
            this.lastTickCount = newTick;
        }
        finally {
            this.wL.unlock();
        }
    }

    public boolean canSmega() {
        MapleCharacter player = this.getPlayer();
        if (this.lastSmegaTime > System.currentTimeMillis() && player != null && !player.isGm()) {
            return false;
        }
        this.lastSmegaTime = System.currentTimeMillis();
        return true;
    }

    public boolean canAvatarSmega() {
        MapleCharacter player = this.getPlayer();
        if (this.lastASmegaTime > System.currentTimeMillis() && player != null && !player.isGm()) {
            return false;
        }
        this.lastASmegaTime = System.currentTimeMillis();
        return true;
    }

    public boolean canBBS() {
        MapleCharacter player = this.getPlayer();
        if (this.lastBBSTime + 60000L > System.currentTimeMillis() && player != null && !player.isGm()) {
            return false;
        }
        this.lastBBSTime = System.currentTimeMillis();
        return true;
    }

    public boolean canMZD() {
        MapleCharacter player = this.getPlayer();
        if (this.lastMZDTime > System.currentTimeMillis() && player != null && !player.isGm()) {
            return false;
        }
        this.lastMZDTime = System.currentTimeMillis();
        return true;
    }

    public boolean canCraftMake() {
        MapleCharacter player = this.getPlayer();
        if (this.lastCraftTime + 1000L > System.currentTimeMillis() && player != null) {
            return false;
        }
        this.lastCraftTime = System.currentTimeMillis();
        return true;
    }

    public boolean canSaveDB() {
        if (this.lastSaveTime + 180000L > System.currentTimeMillis() && this.getPlayer() != null) {
            return false;
        }
        this.lastSaveTime = System.currentTimeMillis();
        return true;
    }

    public int getlastSaveTime() {
        if (this.lastSaveTime <= 0L) {
            this.lastSaveTime = System.currentTimeMillis();
        }
        return (int)((this.lastSaveTime + 180000L - System.currentTimeMillis()) / 1000L);
    }

    public boolean canLieDetector() {
        if (this.lastLieDetectorTime + 300000L > System.currentTimeMillis() && this.getPlayer() != null) {
            return false;
        }
        this.lastLieDetectorTime = System.currentTimeMillis();
        return true;
    }

    public long getLastlogonTime() {
        if (this.lastlogonTime <= 0L || this.getPlayer() == null) {
            this.lastlogonTime = System.currentTimeMillis();
        }
        return this.lastlogonTime;
    }

    public void expireEntry(CheatingOffenseEntry coe) {
        this.wL.lock();
        try {
            this.offenses.remove((Object)coe.getOffense());
        }
        finally {
            this.wL.unlock();
        }
    }

    public int getPoints() {
        CheatingOffenseEntry[] offenses_copy;
        int ret = 0;
        this.rL.lock();
        try {
            offenses_copy = this.offenses.values().toArray(new CheatingOffenseEntry[this.offenses.size()]);
        }
        finally {
            this.rL.unlock();
        }
        for (CheatingOffenseEntry entry : offenses_copy) {
            if (entry.isExpired()) {
                this.expireEntry(entry);
                continue;
            }
            ret += entry.getPoints();
        }
        return ret;
    }

    public Map<CheatingOffense, CheatingOffenseEntry> getOffenses() {
        return Collections.unmodifiableMap(this.offenses);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getSummary() {
        StringBuilder ret = new StringBuilder();
        ArrayList<CheatingOffenseEntry> offenseList = new ArrayList<CheatingOffenseEntry>();
        this.rL.lock();
        try {
            for (CheatingOffenseEntry entry : this.offenses.values()) {
                if (entry.isExpired()) continue;
                offenseList.add(entry);
            }
        }
        finally {
            this.rL.unlock();
        }
        offenseList.sort((o1, o2) -> Integer.compare(o2.getPoints(), o1.getPoints()));
        int to = Math.min(offenseList.size(), 4);
        for (int x = 0; x < to; ++x) {
            ret.append(StringUtil.makeEnumHumanReadable(((CheatingOffenseEntry)offenseList.get(x)).getOffense().name()));
            ret.append(": ");
            ret.append(((CheatingOffenseEntry)offenseList.get(x)).getCount());
            if (x == to - 1) continue;
            ret.append(" ");
        }
        return ret.toString();
    }

    public void dispose() {
        if (this.invalidationTask != null) {
            this.invalidationTask.cancel(false);
        }
        this.invalidationTask = null;
    }

    public final void start(Integer ownerid) {
        this.ownerid = ownerid;
        this.takingDamageSince = System.currentTimeMillis();
    }

    public int getLastAttackSkill() {
        return this.lastAttackSkill;
    }

    public boolean canNext絕殺刃() {
        return this.next絕殺刃 > 0L && this.next絕殺刃 <= System.currentTimeMillis();
    }

    public final void setNext絕殺刃(long me) {
        this.next絕殺刃 = me;
    }

    public boolean canNext死神契約() {
        if (this.next死神契約 <= System.currentTimeMillis()) {
            this.next死神契約 = System.currentTimeMillis() + 9000L;
            return true;
        }
        return false;
    }

    public boolean canNextBonusAttack(long time) {
        if (this.nextBonusAttack > System.currentTimeMillis()) {
            return false;
        }
        this.nextBonusAttack = System.currentTimeMillis() + time;
        return true;
    }

    public void setLastAttackTime() {
        this.lastAttackTime = System.currentTimeMillis();
    }

    public boolean canNextShadowDodge() {
        if (this.nextShadowDodge <= System.currentTimeMillis()) {
            this.nextShadowDodge = System.currentTimeMillis() + 5000L;
            return true;
        }
        return false;
    }

    public boolean canNextAegisSystem() {
        if (this.nextAegisSystem <= System.currentTimeMillis()) {
            this.nextAegisSystem = System.currentTimeMillis() + 1500L;
            return true;
        }
        return false;
    }

    public int getFinalAttackTime() {
        this.lastChannelTick += (int)(System.currentTimeMillis() - this.lastEnterChannel);
        this.lastEnterChannel = System.currentTimeMillis();
        return this.lastChannelTick;
    }

    public void setLastChannelTick(int lastChannelTick) {
        this.lastChannelTick = lastChannelTick;
    }

    public void setLastEnterChannel() {
        this.lastEnterChannel = System.currentTimeMillis();
    }

    public boolean canNextVampiricTouch() {
        if (this.nextVampiricTouch <= System.currentTimeMillis()) {
            this.nextVampiricTouch = System.currentTimeMillis() + 5000L;
            return true;
        }
        return false;
    }

    public void addShadowBat() {
        ++this.shadowBat;
    }

    public boolean canSpawnShadowBat() {
        if (this.shadowBat >= 3) {
            this.shadowBat = 0;
            return true;
        }
        return false;
    }

    public boolean canNext追縱火箭() {
        if (this.next追縱火箭 <= System.currentTimeMillis()) {
            this.next追縱火箭 = System.currentTimeMillis() + 5000L;
            return true;
        }
        return false;
    }

    public final boolean canNextElementalFocus() {
        if (this.nextElementalFocus <= System.currentTimeMillis()) {
            this.nextElementalFocus = System.currentTimeMillis() + 700L;
            return true;
        }
        return false;
    }

    public boolean canNextPantherAttack() {
        if (this.nextPantherAttack <= System.currentTimeMillis()) {
            this.nextPantherAttack = System.currentTimeMillis() + 10000L;
            return true;
        }
        return false;
    }

    public boolean canNextPantherAttackS() {
        return this.nextPantherAttack <= System.currentTimeMillis();
    }

    public boolean canNextHealHPMP() {
        if (this.nextHealHPMP <= System.currentTimeMillis()) {
            this.nextHealHPMP = System.currentTimeMillis() + 4000L;
            return true;
        }
        return false;
    }

    public void setNextHealHPMPS(long time) {
        this.nextHealHPMPS = System.currentTimeMillis() + time;
    }

    public boolean canNextHealHPMPS() {
        return this.nextHealHPMPS <= System.currentTimeMillis();
    }

    public boolean canNextRecoverPower(boolean overload) {
        if (this.lastRecoveryPowerTime <= System.currentTimeMillis()) {
            this.lastRecoveryPowerTime = System.currentTimeMillis() + (long)(overload ? 2000 : 4000);
            return true;
        }
        return false;
    }

    public int gainMonsterCombo() {
        if (System.currentTimeMillis() - this.lastAttackTime > 10000L) {
            this.clearMonsterCombo();
        }
        return this.monsterCombo.incrementAndGet();
    }

    public int getMonsterCombo() {
        if (System.currentTimeMillis() - this.lastAttackTime > 10000L) {
            this.monsterCombo.set(1);
        }
        return this.monsterCombo.get();
    }

    public void clearMonsterCombo() {
        this.monsterCombo.set(0);
    }

    public void gainMultiKill() {
        this.multiKill.incrementAndGet();
    }

    public boolean canNextAllRocket(int skillId, int ms) {
        Long n3 = this.bgn.get(skillId);
        if (n3 != null && n3 > System.currentTimeMillis()) {
            return false;
        }
        this.bgn.put(skillId, System.currentTimeMillis() + (long)ms);
        return true;
    }

    public int getMultiKill() {
        return this.multiKill.get();
    }

    public void setLastKillMobOid(int lastKillMobOid) {
        this.lastKillMobOid.set(lastKillMobOid);
    }

    public int getLastKillMobOid() {
        return this.lastKillMobOid.get();
    }

    public boolean canNext幻影分身符() {
        if (this.next幻影分身符 <= System.currentTimeMillis()) {
            this.next幻影分身符 = System.currentTimeMillis() + 1500L;
            return true;
        }
        return false;
    }

    public boolean canNext蝶梦() {
        if (this.next蝶夢 <= System.currentTimeMillis()) {
            this.next蝶夢 = System.currentTimeMillis() + 1000L;
            return true;
        }
        return false;
    }

    public boolean isAttacking() {
        return this.lastAttackTime != 0L && System.currentTimeMillis() - this.lastAttackTime < 5000L;
    }
}

