/*
 * Decompiled with CFR 0.152.
 */
package Client.status;

import Server.Buffstat;
import java.io.Serializable;

public enum MonsterStatus implements Serializable,
Buffstat
{
    IndiePDR(true),
    IndieMDR(true),
    IndieAddFinalDamSkill(true),
    IndieSlow(true),
    IndieTriangleFormation(true),
    IndieSixthTriangleFormation(true),
    IndieUnk1(true),
    IndieUnk2(true),
    IndieEnd(true),
    PAD,
    PDR,
    MAD,
    MDR,
    ACC,
    EVA,
    Speed,
    Stun,
    Freeze,
    BeforeFreeze,
    Poison,
    Seal,
    Darkness,
    PowerUp,
    MagicUp,
    PGuardUp,
    MGuardUp,
    PImmune,
    MImmune,
    Web,
    HardSkin,
    Ambush,
    Venom,
    Blind,
    SealSkill,
    Dazzle,
    PCounter,
    MCounter,
    RiseByToss,
    BodyPressure,
    Weakness,
    Showdown,
    DevilCry,
    MagicCrash,
    DamagedElemAttr,
    TotalDamParty,
    HitCriDamR,
    Fatality,
    Lifting,
    DeadlyCharge,
    Smite,
    AddDamSkill,
    Incizing,
    DodgeBodyAttack,
    DebuffHealing,
    AddDamSkill2,
    CUserLocal_BodyAttack,
    TempMoveAbility,
    FixDamRBuff,
    GhostDisposition,
    ElementDarkness,
    AreaInstallByHit,
    BMageDebuff,
    JaguarProvoke,
    JaguarBleeding,
    PinkbeanFlowerPot,
    BattlePvP_Helena_Mark,
    PsychicLock,
    PsychicLockCoolTime,
    PsychicGroundMark,
    PowerImmune,
    MultiPMDR,
    ElementResetBySummon,
    BahamutLightElemAddDam,
    LefDebuff,
    BossPropPlus,
    MultiDamSkill,
    RWLiftPress,
    RWChoppingHammer,
    MobAggro,
    ActionState,
    AreaPDR,
    BuffControl,
    IgnoreFreeze,
    BattlePvP_Ryude_Frozen,
    DamR,
    ContinuousHeal,
    HiddenPullingDebuff,
    CurseTransition,
    WindBreakerPinpointPierce,
    Morph,
    MobLock,
    LWGathering,
    KinesisLawOfGravity,
    TargetPlus,
    ReviveOnce,
    BuffFlag,
    HolyShell,
    ZeroCriticalBind,
    Panic,
    ReduceFinalDamage,
    Stalking,
    ChangeMobAction,
    KannaAddAttack,
    ErdaRevert,
    SummonGhost,
    TimeBomb,
    AddEffect,
    Invincible(true),
    Explosion,
    HangOver,
    Sleep,
    LevelInc,
    AfterImage,
    Burned(true),
    BalogDisable(true),
    ExchangeAttack(true),
    AddBuffStat(true),
    LinkTeam(true),
    SoulExplosion(true),
    SeperateSoulP(true),
    SeperateSoulC(true),
    TrueSight(true),
    Ember(true),
    Unk_3(true),
    StatResetSkill(true),
    NEWUNK96(true),
    NEWUNK97(true),
    NEWUNK98(true),
    NEWUNK99(true),
    NEWUNK100(true),
    NEWUNK101(true),
    NEWUNK102(true),
    Laser(true),
    Unk_163_Add_107(true),
    NEWUNK132(true),
    NEWUNK133(true);

    private static final long serialVersionUID = 0L;
    private final int position;
    private final int flag;
    private final boolean isDefault;
    private final int value;
    private boolean stacked;

    private MonsterStatus() {
        this.position = this.ordinal() >> 5;
        this.flag = this.ordinal();
        this.value = 1 << 31 - (this.flag & 0x1F);
        this.isDefault = false;
        this.setStacked(this.name().startsWith("Indie"));
    }

    private MonsterStatus(boolean isDefault) {
        this.position = this.ordinal() >> 5;
        this.flag = this.ordinal();
        this.value = 1 << 31 - (this.flag & 0x1F);
        this.isDefault = isDefault;
        this.setStacked(this.name().startsWith("Indie"));
    }

    private MonsterStatus(int flag) {
        this.position = flag >> 5;
        this.flag = flag;
        this.value = 1 << 31 - (flag & 0x1F);
        this.isDefault = false;
        this.setStacked(this.name().startsWith("Indie"));
    }

    private MonsterStatus(int flag, boolean isDefault) {
        this.position = flag >> 5;
        this.flag = flag;
        this.value = 1 << 31 - (flag & 0x1F);
        this.isDefault = isDefault;
        this.setStacked(this.name().startsWith("Indie"));
    }

    public int getFlag() {
        return this.flag;
    }

    public boolean isStacked() {
        return this.stacked;
    }

    public void setStacked(boolean stacked) {
        this.stacked = stacked;
    }

    public static int genericSkill(MonsterStatus stat) {
        switch (stat.ordinal()) {
            case 16: {
                return 90001001;
            }
            case 15: {
                return 90001002;
            }
            case 19: {
                return 90001003;
            }
            case 35: {
                return 90001004;
            }
            case 20: {
                return 90001005;
            }
            case 42: {
                return 1111007;
            }
            case 21: {
                return 4121003;
            }
            case 31: {
                return 5211004;
            }
            case 30: {
                return 4121004;
            }
            case 108: {
                return 36110005;
            }
        }
        return 0;
    }

    @Override
    public int getPosition() {
        return this.position;
    }

    public boolean isDefault() {
        return this.isDefault;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    public Integer getOrder() {
        return this.position;
    }

    public boolean isIndieStat() {
        return this.ordinal() < PAD.ordinal() || this == Burned;
    }

    public String toString() {
        return this.name() + "(0x" + Integer.toHexString(this.value) + ", " + this.position + ")";
    }
}

