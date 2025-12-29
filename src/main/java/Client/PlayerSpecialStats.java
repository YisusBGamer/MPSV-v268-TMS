/*
 * Decompiled with CFR 0.152.
 */
package Client;

import Client.force.MapleForceAtom;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerSpecialStats
implements Serializable {
    private static final long serialVersionUID = 9179541993413738569L;
    private final HashMap<Integer, AtomicInteger> fieldSkillCounters = new HashMap();
    private final AtomicInteger forceCounter = new AtomicInteger();
    private int cardStack;
    private int moonCycle;
    private int hayatoPoint;
    private int pp;
    private int aranCombo;
    private int mindBreakCount;
    private transient int cylinder;
    private transient int bullet;
    private transient int maxbullet;
    private transient int hurtHP;
    private int jaguarSkillID;
    private int angelReborn;
    private int maelstromMoboid;
    private MapleForceAtom guidedArrow;
    private int remoteDice;
    private long lastShadowBiteTime;
    private int shadowBite;
    private int adeleCharge;
    private int maliceCharge;
    private int shadowHP;
    private int poolMakerCount;
    private int hoYoungRune;
    private int hoYoungScroll;
    private int hoYoungState1;
    private int hoYoungState2;
    private int hoYoungState3;
    private int flameBeads;
    private int pureBeads;
    private int galeBeads;
    private int abyssBeads;
    private int erosions;

    public void resetSpecialStats() {
        this.forceCounter.set(0);
        this.cardStack = 0;
        this.moonCycle = 0;
        this.hayatoPoint = 0;
        this.pp = 0;
        this.aranCombo = 0;
        this.cylinder = 0;
        this.bullet = 0;
        this.maxbullet = 0;
        this.remoteDice = -1;
        this.shadowHP = 0;
        this.flameBeads = 0;
        this.pureBeads = 0;
        this.galeBeads = 0;
        this.abyssBeads = 0;
        this.erosions = 0;
        this.lastShadowBiteTime = -1L;
        this.shadowBite = 0;
        this.adeleCharge = 0;
        this.mindBreakCount = 0;
        this.maliceCharge = 0;
    }

    public int getMindBreakCount() {
        return this.mindBreakCount;
    }

    public void setMindBreakCount(int count) {
        this.mindBreakCount = count;
    }

    public void addMindBreakCount(int val) {
        this.mindBreakCount += val;
    }

    public int getErosions() {
        return this.erosions;
    }

    public void setErosions(int erosions) {
        this.erosions = erosions;
    }

    public void addErosions(int n) {
        this.erosions += n;
        this.erosions = Math.min(1000, Math.max(0, this.erosions));
    }

    public int getFlameBeads() {
        return this.flameBeads;
    }

    public int getPureBeads() {
        return this.pureBeads;
    }

    public void addPureBeads(int val) {
        this.pureBeads += val;
        this.pureBeads = Math.max(0, this.pureBeads);
    }

    public void setPureBeads(int aAc) {
        this.pureBeads = aAc;
    }

    public void setFlameBeads(int aDb) {
        this.flameBeads = aDb;
    }

    public void addFlameBeads(int n) {
        this.flameBeads += n;
        this.flameBeads = Math.max(0, this.flameBeads);
    }

    public int getGaleBeads() {
        return this.galeBeads;
    }

    public void setGaleBeads(int aDc) {
        this.galeBeads = aDc;
    }

    public void addGaleBeads(int n) {
        this.galeBeads += n;
        this.galeBeads = Math.max(0, this.galeBeads);
    }

    public int getAbyssBeads() {
        return this.abyssBeads;
    }

    public void setAbyssBeads(int aDd) {
        this.abyssBeads = aDd;
    }

    public void addAbyssBeads(int n) {
        this.abyssBeads += n;
        this.abyssBeads = Math.max(0, this.abyssBeads);
    }

    public void addShadowBite(long expiration, int count) {
        if (System.currentTimeMillis() - this.lastShadowBiteTime >= expiration) {
            this.shadowBite = 0;
            this.lastShadowBiteTime = System.currentTimeMillis();
        }
        this.shadowBite += count;
    }

    public int getShadowBite() {
        return this.shadowBite;
    }

    public void setAdeleCharge(int val) {
        this.adeleCharge = val;
    }

    public int getAdeleCharge() {
        return this.adeleCharge;
    }

    public void setMaliceCharge(int val) {
        this.maliceCharge = val;
    }

    public int getMaliceCharge() {
        return this.maliceCharge;
    }

    public int getFieldSkillCounter(int skillID) {
        return this.fieldSkillCounters.computeIfAbsent(skillID, k -> new AtomicInteger(0)).get();
    }

    public void setFieldSkillCounter(int skillID, int amount) {
        if (amount < 0) {
            amount = 0;
        }
        this.fieldSkillCounters.computeIfAbsent(skillID, k -> new AtomicInteger(0)).set(amount);
    }

    public int gainFieldSkillCounter(int skillID) {
        AtomicInteger ai = this.fieldSkillCounters.computeIfAbsent(skillID, k -> new AtomicInteger(0));
        if (ai.get() == Integer.MAX_VALUE) {
            ai.set(0);
        }
        return ai.incrementAndGet();
    }

    public int gainFieldSkillCounter(int skillID, int amount) {
        AtomicInteger ai = this.fieldSkillCounters.computeIfAbsent(skillID, k -> new AtomicInteger(0));
        if (ai.get() + amount == Integer.MAX_VALUE || ai.get() + amount < 0) {
            ai.set(0);
        }
        return ai.addAndGet(amount);
    }

    public void resetFieldSkillCounters() {
        this.fieldSkillCounters.clear();
    }

    public int getForceCounter() {
        return this.forceCounter.get();
    }

    public void setForceCounter(int amount) {
        if (amount < 0) {
            amount = 0;
        }
        this.forceCounter.set(amount);
    }

    public int gainForceCounter() {
        if (this.forceCounter.get() == Integer.MAX_VALUE) {
            this.forceCounter.set(0);
        }
        return this.forceCounter.incrementAndGet();
    }

    public void gainForceCounter(int amount) {
        if (this.forceCounter.get() + amount == Integer.MAX_VALUE || this.forceCounter.get() + amount < 0) {
            this.forceCounter.set(0);
        }
        this.forceCounter.addAndGet(amount);
    }

    public int getCardStack() {
        if (this.cardStack < 0) {
            this.cardStack = 0;
        }
        return this.cardStack;
    }

    public void setCardStack(int amount) {
        this.cardStack = amount;
    }

    public void gainCardStack() {
        ++this.cardStack;
    }

    public int getMoonCycle() {
        ++this.moonCycle;
        if (this.moonCycle > 1) {
            this.moonCycle = 0;
        }
        return this.moonCycle;
    }

    public void addHayatoPoint(int n) {
        this.hayatoPoint = Math.max(0, Math.min(1000, this.hayatoPoint + n));
    }

    public void gainHayatoPoint(int mode) {
        this.hayatoPoint = Math.min(1000, this.hayatoPoint + (mode == 1 ? 5 : 2));
    }

    public int getHayatoPoint() {
        return this.hayatoPoint;
    }

    public void setHayatoPoint(int jianqi) {
        this.hayatoPoint = Math.min(1000, jianqi);
    }

    public void gainPP(int pp) {
        this.pp = Math.min(30, Math.max(0, this.pp + pp));
    }

    public int getPP() {
        return this.pp;
    }

    public void setPP(int pp) {
        this.pp = Math.min(30, pp);
    }

    public int getAranCombo() {
        return this.aranCombo;
    }

    public void setAranCombo(int aranCombo) {
        this.aranCombo = aranCombo;
    }

    public int getCylinder() {
        return this.cylinder;
    }

    public void setCylinder(int cylinder) {
        this.cylinder = cylinder;
    }

    public int getBullet() {
        return this.bullet;
    }

    public void setBullet(int bullet) {
        this.bullet = bullet;
    }

    public int getMaxBullet() {
        return this.maxbullet;
    }

    public void setMaxBullet(int maxBullet) {
        this.maxbullet = maxBullet;
    }

    public int getHurtHP() {
        return this.hurtHP;
    }

    public void setHurtHP(int hurtHP) {
        this.hurtHP = hurtHP;
    }

    public void setJaguarSkillID(int jaguarSkillID) {
        this.jaguarSkillID = jaguarSkillID;
    }

    public int getJaguarSkillID() {
        return this.jaguarSkillID;
    }

    public int getAngelReborn() {
        return this.angelReborn;
    }

    public void resetAngelReborn() {
        this.angelReborn = 0;
    }

    public void gainAngelReborn() {
        ++this.angelReborn;
    }

    public void setMaelstromMoboid(int maelstromMoboid) {
        this.maelstromMoboid = maelstromMoboid;
    }

    public int getMaelstromMoboid() {
        return this.maelstromMoboid;
    }

    public void setGuidedArrow(MapleForceAtom guidedArrow) {
        this.guidedArrow = guidedArrow;
    }

    public MapleForceAtom getGuidedArrow() {
        return this.guidedArrow;
    }

    public int getRemoteDice() {
        return this.remoteDice;
    }

    public void setRemoteDice(int remoteDice) {
        this.remoteDice = remoteDice;
    }

    public void setShadowHP(int shadowHP) {
        this.shadowHP = shadowHP;
    }

    public int getShadowHP() {
        return this.shadowHP;
    }

    public int getPoolMakerCount() {
        return this.poolMakerCount;
    }

    public void setPoolMakerCount(int poolMakerCount) {
        this.poolMakerCount = poolMakerCount;
    }

    public int getHoYoungRune() {
        return this.hoYoungRune;
    }

    public void setHoYoungRune(int hoYoungRune) {
        this.hoYoungRune = hoYoungRune;
    }

    public int getHoYoungScroll() {
        return this.hoYoungScroll;
    }

    public void setHoYoungScroll(int hoYoungScroll) {
        this.hoYoungScroll = hoYoungScroll;
    }

    public int getHoYoungState1() {
        return this.hoYoungState1;
    }

    public void setHoYoungState1(int hoYoungState1) {
        this.hoYoungState1 = hoYoungState1;
    }

    public int getHoYoungState2() {
        return this.hoYoungState2;
    }

    public void setHoYoungState2(int hoYoungState2) {
        this.hoYoungState2 = hoYoungState2;
    }

    public int getHoYoungState3() {
        return this.hoYoungState3;
    }

    public void setHoYoungState3(int hoYoungState3) {
        this.hoYoungState3 = hoYoungState3;
    }

    public void gainHoYoungRune(int diff) {
        this.hoYoungRune = Math.max(0, Math.min(100, this.hoYoungRune + diff));
    }

    public void gainHoYoungScroll(int diff) {
        this.hoYoungScroll = Math.max(0, Math.min(900, this.hoYoungScroll + diff));
    }
}

