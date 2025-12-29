/*
 * Decompiled with CFR 0.152.
 */
package Config.constants.enums;

public enum UIReviveType {
    UIReviveType_None(-1),
    UIReviveType_Normal(0),
    UIReviveType_UsingPartyPoint(1),
    UIReviveType_SoulDungeon(2),
    UIReviveType_MagnusNormalHard(3),
    UIReviveType_OnUIDeathCountInfo(4),
    UIReviveType_SoulStone(5),
    UIReviveType_UpgradeTombItem(6),
    UIReviveType_PremiumUser(7),
    UIReviveType_UpgradeTombItemOfCashBuffEvent(8),
    UIReviveType_UpgradeTombItemMaplePoint(9),
    UIReviveType_CombatAndroid(11),
    UIReviveType_PremiumUser2(12),
    UIReviveType_Nemesis2(13),
    UIReviveType_Nemesis(14);

    private final int type;

    private UIReviveType(int type) {
        this.type = type;
    }

    public final int getType() {
        return this.type;
    }
}

