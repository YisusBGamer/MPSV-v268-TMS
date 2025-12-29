/*
 * Decompiled with CFR 0.152.
 */
package Client.stat;

import Client.MapleCharacter;
import Client.stat.PlayerStats;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

abstract class RecalcableStats {
    static final Logger log = PlayerStats.log;
    int wdef;
    int speed;
    int jump;
    short critRate;
    short criticalDamage;
    int percent_wdef;
    int incMaxHPR;
    int incMaxMPR;
    int incStrR;
    int incDexR;
    int incIntR;
    int incLukR;
    int incPadR;
    int incMadR;
    double ignoreMobpdpR;
    double incDamR;
    int bossDamageR;
    int localstr;
    int localdex;
    int localluk;
    int localint;
    int addmaxhp;
    int addmaxmp;
    int indieStrFX;
    int indieDexFX;
    int indieLukFX;
    int indieIntFX;
    int indieMhpFX;
    int indieMmpFX;
    int indiePadFX;
    int indieMadFX;
    int mad;
    int pad;
    int recoverHP;
    int recoverMP;
    int mpconReduce;
    int incMesoProp;
    int reduceCooltime;
    int itemRecoveryUP;
    int skillRecoveryUP;
    int incBuffTime;
    int incAllskill;
    int asr;
    int terR;
    int pvpDamage;
    int incMaxDF;
    double incRewardProp;
    int weaponId;
    int incAttackCount;
    Map<Integer, List<Integer>> equipmentBonusExps;

    RecalcableStats() {
    }

    void resetLocalStats() {
        this.wdef = 0;
        this.addmaxhp = 0;
        this.addmaxmp = 0;
        this.localdex = 0;
        this.localint = 0;
        this.localstr = 0;
        this.localluk = 0;
        this.indieDexFX = 0;
        this.indieIntFX = 0;
        this.indieStrFX = 0;
        this.indieLukFX = 0;
        this.indieMhpFX = 0;
        this.indieMmpFX = 0;
        this.speed = 0;
        this.jump = 0;
        this.asr = 0;
        this.terR = 0;
        this.percent_wdef = 0;
        this.incMaxHPR = 0;
        this.incMaxMPR = 0;
        this.incStrR = 0;
        this.incDexR = 0;
        this.incIntR = 0;
        this.incLukR = 0;
        this.incPadR = 0;
        this.incMadR = 0;
        this.ignoreMobpdpR = 0.0;
        this.critRate = 0;
        this.criticalDamage = 0;
        this.incDamR = 0.0;
        this.bossDamageR = 0;
        this.mad = 0;
        this.pad = 0;
        this.pvpDamage = 0;
        this.recoverHP = 0;
        this.recoverMP = 0;
        this.mpconReduce = 0;
        this.incMesoProp = 0;
        this.reduceCooltime = 0;
        this.incRewardProp = 0.0;
        this.equipmentBonusExps = new LinkedHashMap<Integer, List<Integer>>();
        this.incBuffTime = 0;
        this.incMaxDF = 0;
        this.incAllskill = 0;
        this.weaponId = 0;
        this.incAttackCount = 0;
    }

    abstract void recalcLocalStats(boolean var1, MapleCharacter var2);
}

