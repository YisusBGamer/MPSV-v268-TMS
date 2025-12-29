/*
 * Decompiled with CFR 0.152.
 */
package Server.channel.handler;

public class AttackMobInfo {
    public int mobId;
    public byte hitAction;
    public byte left;
    public byte idk3;
    public byte foreAction;
    public byte frameIdx;
    public byte calcDamageStatIndex;
    public short hitX;
    public short hitY;
    public short oldPosX;
    public short oldPosY;
    public short hpPerc;
    public long[] damages;
    public int mobUpDownYRange;
    public byte type;
    public String currentAnimationName;
    public int animationDeltaL;
    public String[] hitPartRunTimes;
    public int templateID;
    public short idk6;
    public boolean isResWarriorLiftPress;
    public int idkInt;
    public byte byteIdk1;
    public byte byteIdk2;
    public byte byteIdk3;
    public byte byteIdk4;
    public byte byteIdk5;
    public int psychicLockInfo;
    public byte rocketRushInfo;
    public byte forceActionAndLeft;
    public byte calcDamageStatIndexAndDoomed;
    public int hitPartRunTimesSize;
    public short magicInfo;

    public AttackMobInfo deepCopy() {
        AttackMobInfo mai = new AttackMobInfo();
        mai.mobId = this.mobId;
        mai.hitAction = this.hitAction;
        mai.left = this.left;
        mai.idk3 = this.idk3;
        mai.forceActionAndLeft = this.forceActionAndLeft;
        mai.frameIdx = this.frameIdx;
        mai.calcDamageStatIndex = this.calcDamageStatIndex;
        mai.hitX = this.hitX;
        mai.hitY = this.hitY;
        mai.oldPosX = this.oldPosX;
        mai.oldPosY = this.oldPosY;
        mai.hpPerc = this.hpPerc;
        mai.damages = new long[this.damages.length];
        if (this.damages != null && this.damages.length > 0) {
            System.arraycopy(this.damages, 0, mai.damages, 0, this.damages.length);
        }
        mai.mobUpDownYRange = this.mobUpDownYRange;
        mai.animationDeltaL = this.animationDeltaL;
        if (this.hitPartRunTimes != null && this.hitPartRunTimes.length > 0) {
            System.arraycopy(this.hitPartRunTimes, 0, mai.hitPartRunTimes, 0, this.hitPartRunTimes.length);
        }
        mai.templateID = this.templateID;
        mai.idk6 = this.idk6;
        mai.isResWarriorLiftPress = this.isResWarriorLiftPress;
        mai.idkInt = this.idkInt;
        mai.byteIdk1 = this.byteIdk1;
        mai.byteIdk2 = this.byteIdk2;
        mai.byteIdk3 = this.byteIdk3;
        mai.byteIdk4 = this.byteIdk4;
        mai.byteIdk5 = this.byteIdk5;
        mai.psychicLockInfo = this.psychicLockInfo;
        mai.rocketRushInfo = this.rocketRushInfo;
        return mai;
    }
}

