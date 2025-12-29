/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package SwordieX.client.character.avatar;

import Client.MapleCharacter;
import Config.constants.JobConstants;
import SwordieX.client.character.CharacterStat;
import SwordieX.client.character.avatar.AvatarLook;
import connection.OutPacket;
import lombok.Generated;
import tools.data.MaplePacketLittleEndianWriter;

public class AvatarData {
    private CharacterStat characterStat;
    private AvatarLook avatarLook;
    private AvatarLook secondAvatarLook;

    public AvatarData(MapleCharacter chr) {
        this.characterStat = new CharacterStat(chr);
        this.avatarLook = new AvatarLook(chr, false);
        this.secondAvatarLook = new AvatarLook(chr, true);
    }

    public void encode(MaplePacketLittleEndianWriter mplew) {
        OutPacket outPacket = new OutPacket();
        this.encode(outPacket);
        mplew.write(outPacket.getData());
    }

    public void encode(OutPacket outPacket) {
        this.characterStat.encode(outPacket);
        outPacket.encodeInt(8);
        outPacket.encodeInt(0);
        outPacket.encodeLong(0L);
        outPacket.encodeInt(0);
        outPacket.encodeInt(0);
        outPacket.encodeInt(0);
        outPacket.encodeByte(false);
        outPacket.encodeByte(false);
        outPacket.encodeString("");
        outPacket.encodeByte(false);
        outPacket.encodeString("");
        outPacket.encodeLong(0L);
        outPacket.encodeByte(0);
        this.avatarLook.encode(outPacket, true);
        if (JobConstants.is神之子(this.getCharacterStat().getJob())) {
            this.secondAvatarLook.encode(outPacket, true);
        }
    }

    @Generated
    public void setCharacterStat(CharacterStat characterStat) {
        this.characterStat = characterStat;
    }

    @Generated
    public void setAvatarLook(AvatarLook avatarLook) {
        this.avatarLook = avatarLook;
    }

    @Generated
    public void setSecondAvatarLook(AvatarLook secondAvatarLook) {
        this.secondAvatarLook = secondAvatarLook;
    }

    @Generated
    public CharacterStat getCharacterStat() {
        return this.characterStat;
    }

    @Generated
    public AvatarLook getAvatarLook() {
        return this.avatarLook;
    }

    @Generated
    public AvatarLook getSecondAvatarLook() {
        return this.secondAvatarLook;
    }
}

