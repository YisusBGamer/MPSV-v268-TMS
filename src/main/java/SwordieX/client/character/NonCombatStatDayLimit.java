/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package SwordieX.client.character;

import SwordieX.util.FileTime;
import connection.OutPacket;
import lombok.Generated;

public class NonCombatStatDayLimit {
    private short charisma;
    private short charm;
    private short insight;
    private short will;
    private short craft;
    private short sense;
    private FileTime lastUpdateCharmByCashPR;
    private byte charmByCashPR;

    public NonCombatStatDayLimit(short charisma, short charm, byte charmByCashPR, short insight, short will, short craft, short sense, FileTime lastUpdateCharmByCashPR) {
        this.charisma = charisma;
        this.charm = charm;
        this.charmByCashPR = charmByCashPR;
        this.insight = insight;
        this.will = will;
        this.craft = craft;
        this.sense = sense;
        this.lastUpdateCharmByCashPR = lastUpdateCharmByCashPR;
    }

    public NonCombatStatDayLimit() {
        this((short)0, (short)0, (byte)0, (short)0, (short)0,(short) 0,(short) 0, FileTime.fromType(FileTime.Type.ZERO_TIME));
    }

    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(this.getCharisma());
        outPacket.encodeInt(this.getInsight());
        outPacket.encodeInt(this.getWill());
        outPacket.encodeInt(this.getCraft());
        outPacket.encodeInt(this.getSense());
        outPacket.encodeInt(this.getCharm());
        outPacket.encodeBoolean(false);
        outPacket.encodeArr("00 40 E0 FD 3B 37 4F 01");
        outPacket.encodeInt(20250225);
    }

    @Generated
    public void setCharisma(short charisma) {
        this.charisma = charisma;
    }

    @Generated
    public void setCharm(short charm) {
        this.charm = charm;
    }

    @Generated
    public void setInsight(short insight) {
        this.insight = insight;
    }

    @Generated
    public void setWill(short will) {
        this.will = will;
    }

    @Generated
    public void setCraft(short craft) {
        this.craft = craft;
    }

    @Generated
    public void setSense(short sense) {
        this.sense = sense;
    }

    @Generated
    public void setLastUpdateCharmByCashPR(FileTime lastUpdateCharmByCashPR) {
        this.lastUpdateCharmByCashPR = lastUpdateCharmByCashPR;
    }

    @Generated
    public void setCharmByCashPR(byte charmByCashPR) {
        this.charmByCashPR = charmByCashPR;
    }

    @Generated
    public short getCharisma() {
        return this.charisma;
    }

    @Generated
    public short getCharm() {
        return this.charm;
    }

    @Generated
    public short getInsight() {
        return this.insight;
    }

    @Generated
    public short getWill() {
        return this.will;
    }

    @Generated
    public short getCraft() {
        return this.craft;
    }

    @Generated
    public short getSense() {
        return this.sense;
    }

    @Generated
    public FileTime getLastUpdateCharmByCashPR() {
        return this.lastUpdateCharmByCashPR;
    }

    @Generated
    public byte getCharmByCashPR() {
        return this.charmByCashPR;
    }
}

