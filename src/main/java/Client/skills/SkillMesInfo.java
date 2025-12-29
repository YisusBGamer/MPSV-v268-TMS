/*
 * Decompiled with CFR 0.152.
 */
package Client.skills;

public enum SkillMesInfo {
    Null,
    blind,
    seal,
    dot,
    cold,
    stun,
    restrict,
    elementalWeaken,
    attackLimit,
    freeze,
    incTargetEXP,
    lifting,
    homing,
    darkness,
    incapacitate,
    incTargetReward,
    polymorph,
    amplifyDamage,
    mindControl,
    haste,
    reduceTargetDam,
    buffLimit,
    incTargetPDP,
    incTargetMeso,
    reduceTargetPDP,
    slow,
    reduceTargetMDP,
    reduceTargetACC,
    incTargetMDP;


    public static SkillMesInfo getInfo(String Name) {
        if (Name.equals("reduceTargetACC ")) {
            Name = "reduceTargetACC";
        }
        SkillMesInfo info = Null;
        for (SkillMesInfo value : SkillMesInfo.values()) {
            if (!value.name().equals(Name)) continue;
            info = value;
        }
        return info;
    }
}

