/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  SwordieX.enums.BaseStat$1
 *  SwordieX.enums.Stat
 */
package SwordieX.enums;

import SwordieX.enums.BaseStat;
import SwordieX.enums.Stat;

public enum BaseStat {
    unk,
    str,
    strR,
    dex,
    dexR,
    inte,
    intR,
    luk,
    lukR,
    mdf,
    pad,
    padR,
    mad,
    madR,
    pdd,
    pddR,
    mdd,
    mddR,
    mhp,
    mhpR,
    mmp,
    mmpR,
    cr,
    addCrOnBoss,
    crDmg,
    fd,
    damR,
    bd,
    nbd,
    ied,
    asr,
    ter,
    eleAttrResistance,
    acc,
    accR,
    eva,
    evaR,
    jump,
    speed,
    expR,
    comboKillOrbExpR,
    dropR,
    dropRMulti,
    mesoR,
    mesoRMulti,
    booster,
    stance,
    mastery,
    damageOver,
    allStat,
    allStatR,
    hpRecovery,
    mpRecovery,
    incAllSkill,
    strLv,
    dexLv,
    intLv,
    lukLv,
    summonTimeR,
    buffTimeR,
    runeBuffTimerR,
    noCoolProp,
    reduceCooltimeR,
    costhpR,
    costmpR,
    hpDrain,
    mpDrain,
    recoveryUp,
    mpconReduce,
    padLv,
    madLv,
    mhpLv,
    mmpLv,
    dmgReduce,
    magicGuard,
    invincibleAfterRevive,
    shopDiscountR,
    pqShopDiscountR,
    arc;



    public static BaseStat getFromStat(Stat s) {
        switch (s) {
            case str:
                return str;
            case dex:
                return dex;
            case inte:
                return inte;
            case luk:
                return luk;
            case mhp:
                return mhp;
            case mmp:
                return mmp;
            default:
                return unk;
        }
    }

    public BaseStat getRateVar() {
        switch (this.ordinal()) {
            case 1: {
                return strR;
            }
            case 3: {
                return dexR;
            }
            case 5: {
                return intR;
            }
            case 7: {
                return lukR;
            }
            case 10: {
                return padR;
            }
            case 12: {
                return madR;
            }
            case 14: {
                return pddR;
            }
            case 16: {
                return mddR;
            }
            case 18: {
                return mhpR;
            }
            case 20: {
                return mmpR;
            }
            case 33: {
                return accR;
            }
            case 35: {
                return evaR;
            }
            case 41: {
                return dropRMulti;
            }
            case 43: {
                return mesoRMulti;
            }
        }
        return null;
    }

    public BaseStat getLevelVar() {
        switch (this.ordinal()) {
            case 1: {
                return strLv;
            }
            case 3: {
                return dexLv;
            }
            case 5: {
                return intLv;
            }
            case 7: {
                return lukLv;
            }
            case 10: {
                return padLv;
            }
            case 12: {
                return madLv;
            }
            case 18: {
                return mhpLv;
            }
            case 20: {
                return mmpLv;
            }
        }
        return null;
    }

    public Stat toStat() {
        switch (this.ordinal()) {
            case 1: {
                return Stat.str;
            }
            case 3: {
                return Stat.dex;
            }
            case 5: {
                return Stat.inte;
            }
            case 7: {
                return Stat.luk;
            }
            case 18: {
                return Stat.mhp;
            }
            case 20: {
                return Stat.mmp;
            }
        }
        return null;
    }

    public boolean isNonAdditiveStat() {
        return this == fd || this == ied;
    }
}

