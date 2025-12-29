/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package SwordieX.client.character.avatar;

import Client.MapleCharacter;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.MapleInventory;
import Client.inventory.MapleInventoryType;
import Client.inventory.MapleWeapon;
import Config.constants.JobConstants;
import connection.InPacket;
import connection.OutPacket;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Generated;
import tools.data.ByteArrayByteStream;
import tools.data.ByteStream;
import tools.data.MaplePacketLittleEndianWriter;
import tools.data.MaplePacketReader;

public class AvatarLook {
    private int gender;
    private int skin;
    private int face;
    private int hair;
    private int job;
    private final Map<Byte, Integer> equips = new LinkedHashMap<Byte, Integer>();
    private final Map<Byte, Integer> maskedEquips = new LinkedHashMap<Byte, Integer>();
    private final Map<Byte, Integer> equipMixColors = new LinkedHashMap<Byte, Integer>();
    private final Map<Byte, Integer> totemEquips = new LinkedHashMap<Byte, Integer>();
    private int weaponStickerId;
    private int weaponId;
    private int subWeaponId;
    private int ear;
    private boolean drawElfEar;
    private final int[] pets = new int[3];
    private int defFaceAcc;
    private boolean isSecondLook;
    private String name;

    public AvatarLook() {
    }

    public AvatarLook(MapleCharacter chr, boolean second) {
        Item cWeapon;
        boolean zero = JobConstants.is神之子(chr.getJob());
        this.gender = zero && second ? (byte)1 : chr.getGender();
        this.skin = second ? chr.getSecondSkinColor() : chr.getSkinColor();
        this.face = second ? chr.getSecondFace() : chr.getFace();
        this.job = chr.getJob();
        this.hair = second ? chr.getSecondHair() : chr.getHair();
        LinkedHashMap<Byte, Integer> angelEquip = new LinkedHashMap<Byte, Integer>();
        MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);
        boolean angel = JobConstants.is天使破壞者(chr.getJob());
        for (Item item : equip.newList()) {
            byte pos;
            int itemID = item.getItemId();
            if (item instanceof Equip && ((Equip)item).getItemSkin() > 0) {
                itemID = ((Equip)item).getItemSkin();
            }
            if (angel && second && item.getPosition() <= -1300 && item.getPosition() > -1310) {
                byte b809 = (byte)(-item.getPosition() - 1300);
                switch (b809) {
                    case 7: {
                        angelEquip.put((byte)5, itemID);
                        break;
                    }
                    case 0: {
                        angelEquip.put((byte)1, itemID);
                        break;
                    }
                    default: {
                        angelEquip.put(b809, itemID);
                    }
                }
            }
            if (item.getPosition() <= -5000 && item.getPosition() >= -5002) {
                pos = 120;
                this.totemEquips.putIfAbsent(pos, itemID);
            }
            if (item.getPosition() < -128) continue;
            pos = (byte)(-item.getPosition());
            if (pos < 100 && this.equips.get(pos) == null) {
                if (second && angel && (pos >= 1 && pos <= 9 || pos == 13)) continue;
                this.equips.put(pos, itemID);
                continue;
            }
            if ((pos > 100 || pos == -128) && pos != 111) {
                pos = (byte)(pos == -128 ? 28 : pos - 100);
                if (second && angel && (pos >= 1 && pos <= 9 || pos == 13)) continue;
                if (this.equips.get(pos) != null) {
                    this.maskedEquips.put(pos, this.equips.get(pos));
                }
                this.equips.put(pos, itemID);
                continue;
            }
            if (this.equips.get(pos) == null) continue;
            this.maskedEquips.put(pos, itemID);
        }
        if (angel && second) {
            if (!angelEquip.containsKey((byte)5)) {
                if (chr.getKeyValue("Longcoat") == null) {
                    chr.setKeyValue("Longcoat", "1051291");
                }
                this.equips.put((byte)5, Integer.valueOf(chr.getKeyValue("Longcoat")));
                this.maskedEquips.put((byte)5, Integer.valueOf(chr.getKeyValue("Longcoat")));
            } else {
                this.equips.put((byte)5, (Integer)angelEquip.get((byte)5));
                this.maskedEquips.clear();
            }
            this.equips.putAll(angelEquip);
            this.maskedEquips.putAll(angelEquip);
        }
        if (zero) {
            int itemId = 0;
            if (this.equips.containsKey((byte)10)) {
                itemId = this.equips.remove((byte)10);
            }
            if (second) {
                this.equips.remove((byte)11);
                if (itemId > 0) {
                    this.equips.put((byte)11, itemId);
                }
            }
        }
        this.weaponStickerId = (cWeapon = equip.getItem((short)-111)) != null ? cWeapon.getItemId() : 0;
        Item weapon = equip.getItem((short)(zero && second ? -10 : -11));
        this.weaponId = weapon != null ? weapon.getItemId() : 0;
        Item subWeapon = equip.getItem((short)-10);
        this.subWeaponId = !zero && subWeapon != null ? subWeapon.getItemId() : 0;
        String questInfo = chr.getQuestInfo(7784, "sw");
        if (questInfo == null) {
            questInfo = "0";
        }
        int nEar = Integer.parseInt(questInfo);
        this.ear = JobConstants.getEar(chr.getJob(), nEar);
        this.drawElfEar = nEar != 0;
        for (int i = 0; i < this.pets.length; ++i) {
            this.pets[i] = !second && chr.getSpawnPet(i) != null ? chr.getSpawnPet(i).getPetItemId() : 0;
        }
        this.defFaceAcc = chr.getDecorate();
        this.isSecondLook = second;
        this.name = chr.getName();
    }

    public void encode(MaplePacketLittleEndianWriter mplew, boolean mega) {
        OutPacket outPacket = new OutPacket();
        this.encode(outPacket, mega);
        mplew.write(outPacket.getData());
    }

    public void encode(OutPacket outPacket, boolean mega) {
        outPacket.encodeByte(this.getGender());
        outPacket.encodeByte(this.getSkin());
        outPacket.encodeInt(0);
        outPacket.encodeInt(this.getFace());
        outPacket.encodeInt(this.getJob());
        outPacket.encodeByte(mega ? 0 : 1);
        outPacket.encodeInt(this.getHair());
        for (Map.Entry<Byte, Integer> entry : this.equips.entrySet()) {
            outPacket.encodeByte(entry.getKey());
            outPacket.encodeInt(entry.getValue());
        }
        outPacket.encodeByte(255);
        for (Map.Entry<Byte, Integer> entry : this.maskedEquips.entrySet()) {
            outPacket.encodeByte(entry.getKey());
            outPacket.encodeInt(entry.getValue());
        }
        outPacket.encodeByte(255);
        for (Map.Entry<Byte, Integer> entry : this.totemEquips.entrySet()) {
            outPacket.encodeByte(entry.getKey());
            outPacket.encodeInt(entry.getValue());
        }
        outPacket.encodeByte(255);
        outPacket.encodeInt(this.getWeaponStickerId());
        outPacket.encodeInt(this.getWeaponId());
        outPacket.encodeInt(this.getSubWeaponId());
        outPacket.encodeInt(this.ear);
        outPacket.encodeInt(0);
        outPacket.encodeBoolean(this.drawElfEar);
        outPacket.encodeInt(0);
        for (int pet : this.pets) {
            outPacket.encodeInt((int)pet);
        }
        outPacket.encodeArr(new byte[148]);
        if (JobConstants.hasDecorate(this.job)) {
            outPacket.encodeInt(this.defFaceAcc);
        } else if (JobConstants.is神之子(this.job) || JobConstants.is天使破壞者(this.job)) {
            outPacket.encodeBoolean(this.isSecondLook);
        }
        outPacket.encodeInt(154819);
        outPacket.encodeString(this.name, 15);
        outPacket.encodeArr(new byte[5]);
    }

    public void decode(MaplePacketReader slea) {
        byte[] now = new byte[]{};
        ByteStream bs = slea.getByteStream();
        if (bs instanceof ByteArrayByteStream) {
            now = ((ByteArrayByteStream)bs).getNowBytes();
        }
        InPacket inPacket = new InPacket(now);
        this.decode(inPacket);
        slea.skip(inPacket.readerIndex());
    }

    public void decode(InPacket inPacket) {
        this.gender = inPacket.decodeByte();
        this.skin = inPacket.decodeByte();
        this.face = inPacket.decodeInt();
        this.job = inPacket.decodeInt();
        inPacket.decodeByte();
        this.hair = inPacket.decodeInt();
        int i = inPacket.decodeByte();
        while (i != -1) {
            this.equips.put((byte)i, inPacket.decodeInt());
            i = inPacket.decodeByte();
        }
        i = inPacket.decodeByte();
        while (i != -1) {
            this.maskedEquips.put((byte)i, inPacket.decodeInt());
            i = inPacket.decodeByte();
        }
        i = inPacket.decodeByte();
        while (i != -1) {
            this.equipMixColors.put((byte)i, inPacket.decodeInt());
            i = inPacket.decodeByte();
        }
        i = inPacket.decodeByte();
        while (i != -1) {
            this.totemEquips.put((byte)i, inPacket.decodeInt());
            i = inPacket.decodeByte();
        }
        this.weaponStickerId = inPacket.decodeInt();
        this.weaponId = inPacket.decodeInt();
        this.subWeaponId = inPacket.decodeInt();
        this.ear = inPacket.decodeInt();
        inPacket.decodeInt();
        this.drawElfEar = inPacket.decodeByte() != 0;
        inPacket.decodeInt();
        for (i = 0; i < 3; ++i) {
            this.pets[i] = inPacket.decodeInt();
        }
        if (JobConstants.hasDecorate(this.job)) {
            this.defFaceAcc = inPacket.decodeInt();
        } else if (JobConstants.is神之子(this.job) || JobConstants.is天使破壞者(this.job)) {
            this.isSecondLook = inPacket.decodeByte() != 0;
        }
        inPacket.decodeInt();
        inPacket.decodeArr(5);
    }

    public byte[] getPackedCharacterLook() {
        int[] equipArr = new int[11];
        for (byte i = 0; i < equipArr.length; i = (byte)(i + 1)) {
            equipArr[i] = this.equips.getOrDefault(i, 0);
        }
        int visibleWeaponId = this.weaponStickerId != 0 ? this.weaponStickerId : this.weaponId;
        int weaponType = 0;
        for (MapleWeapon type : MapleWeapon.values()) {
            if (type.getWeaponType() == visibleWeaponId / 1000 % 1000) break;
            if (++weaponType <= 36) continue;
            weaponType = 0;
            break;
        }
        byte[] data = new byte[120];
        this.encodeArrIndex(data, 0, this.gender & 1);
        this.encodeArrIndex(data, 0, (this.skin & 0x3FF) << 1);
        this.encodeArrIndex(data, 1, ((this.face > 0 ? this.face % 1000 : -1) & 0x3FF) << 3);
        this.encodeArrIndex(data, 2, (this.face / 1000 % 10 & 0xF) << 5);
        this.encodeArrIndex(data, 3, this.hair / 10000 == 4 ? 2 : 0);
        this.encodeArrIndex(data, 3, ((this.hair > 0 ? this.hair % 1000 : -1) & 0x3FF) << 2);
        this.encodeArrIndex(data, 4, (this.hair / 1000 % 10 & 0xF) << 4);
        this.encodeArrIndex(data, 5, (equipArr[1] > 0 ? equipArr[1] % 1000 : -1) & 0x3FF);
        this.encodeArrIndex(data, 6, (equipArr[1] / 1000 % 10 & 7) << 2);
        this.encodeArrIndex(data, 6, ((this.defFaceAcc > 0 ? this.defFaceAcc % 1000 : -1) & 0x3FF) << 5);
        this.encodeArrIndex(data, 7, (this.defFaceAcc / 1000 % 10 & 3) << 7);
        this.encodeArrIndex(data, 8, ((equipArr[3] > 0 ? equipArr[3] % 1000 : -1) & 0x3FF) << 1);
        this.encodeArrIndex(data, 9, (equipArr[3] / 1000 % 10 & 3) << 3);
        this.encodeArrIndex(data, 9, ((equipArr[4] > 0 ? equipArr[4] % 1000 : -1) & 0x3FF) << 5);
        this.encodeArrIndex(data, 10, (equipArr[4] / 1000 % 10 & 3) << 7);
        this.encodeArrIndex(data, 11, equipArr[5] / 10000 == 105 ? 2 : 0);
        this.encodeArrIndex(data, 11, ((equipArr[5] > 0 ? equipArr[5] % 1000 : -1) & 0x3FF) << 2);
        this.encodeArrIndex(data, 12, (equipArr[5] / 1000 % 10 & 0xF) << 4);
        this.encodeArrIndex(data, 13, (equipArr[6] > 0 ? equipArr[6] % 1000 : -1) & 0x3FF);
        this.encodeArrIndex(data, 14, (equipArr[6] / 1000 % 10 & 3) << 2);
        this.encodeArrIndex(data, 14, ((equipArr[7] > 0 ? equipArr[7] % 1000 : -1) & 0x3FF) << 4);
        this.encodeArrIndex(data, 15, (equipArr[7] / 1000 % 10 & 3) << 6);
        this.encodeArrIndex(data, 16, (equipArr[8] > 0 ? equipArr[8] % 1000 : -1) & 0x3FF);
        this.encodeArrIndex(data, 17, (equipArr[8] / 1000 % 10 & 3) << 2);
        this.encodeArrIndex(data, 17, ((equipArr[9] > 0 ? equipArr[9] % 1000 : -1) & 0x3FF) << 4);
        this.encodeArrIndex(data, 18, (equipArr[9] / 1000 % 10 & 3) << 6);
        this.encodeArrIndex(data, 19, equipArr[10] > 0 ? (equipArr[10] / 10000 == 109 ? 1 : 3 - (equipArr[10] / 10000 == 134 ? 1 : 0)) : 0);
        this.encodeArrIndex(data, 19, ((equipArr[10] > 0 ? equipArr[10] % 1000 : -1) & 0x3FF) << 2);
        this.encodeArrIndex(data, 20, (equipArr[10] / 1000 % 10 & 0xF) << 4);
        this.encodeArrIndex(data, 21, visibleWeaponId / 10000 == 170 ? 1 : 0);
        this.encodeArrIndex(data, 21, ((visibleWeaponId > 0 ? visibleWeaponId % 1000 : -1) & 0x3FF) << 1);
        this.encodeArrIndex(data, 22, (visibleWeaponId / 1000 % 10 & 3) << 3);
        this.encodeArrIndex(data, 22, weaponType << 5);
        this.encodeArrIndex(data, 119, 24);
        return data;
    }

    private void encodeArrIndex(byte[] arr, int index, int value) {
        int n = index;
        arr[n] = (byte)(arr[n] | (byte)(value & 0xFF));
        if (value > 255 && index < arr.length - 1) {
            int n2 = index + 1;
            arr[n2] = (byte)(arr[n2] | (byte)(value >>> 8 & 0xFF));
        }
    }

    @Generated
    public void setGender(int gender) {
        this.gender = gender;
    }

    @Generated
    public void setSkin(int skin) {
        this.skin = skin;
    }

    @Generated
    public void setFace(int face) {
        this.face = face;
    }

    @Generated
    public void setHair(int hair) {
        this.hair = hair;
    }

    @Generated
    public void setJob(int job) {
        this.job = job;
    }

    @Generated
    public void setWeaponStickerId(int weaponStickerId) {
        this.weaponStickerId = weaponStickerId;
    }

    @Generated
    public void setWeaponId(int weaponId) {
        this.weaponId = weaponId;
    }

    @Generated
    public void setSubWeaponId(int subWeaponId) {
        this.subWeaponId = subWeaponId;
    }

    @Generated
    public void setEar(int ear) {
        this.ear = ear;
    }

    @Generated
    public void setDrawElfEar(boolean drawElfEar) {
        this.drawElfEar = drawElfEar;
    }

    @Generated
    public void setDefFaceAcc(int defFaceAcc) {
        this.defFaceAcc = defFaceAcc;
    }

    @Generated
    public void setSecondLook(boolean isSecondLook) {
        this.isSecondLook = isSecondLook;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public int getGender() {
        return this.gender;
    }

    @Generated
    public int getSkin() {
        return this.skin;
    }

    @Generated
    public int getFace() {
        return this.face;
    }

    @Generated
    public int getHair() {
        return this.hair;
    }

    @Generated
    public int getJob() {
        return this.job;
    }

    @Generated
    public Map<Byte, Integer> getEquips() {
        return this.equips;
    }

    @Generated
    public Map<Byte, Integer> getMaskedEquips() {
        return this.maskedEquips;
    }

    @Generated
    public Map<Byte, Integer> getEquipMixColors() {
        return this.equipMixColors;
    }

    @Generated
    public Map<Byte, Integer> getTotemEquips() {
        return this.totemEquips;
    }

    @Generated
    public int getWeaponStickerId() {
        return this.weaponStickerId;
    }

    @Generated
    public int getWeaponId() {
        return this.weaponId;
    }

    @Generated
    public int getSubWeaponId() {
        return this.subWeaponId;
    }

    @Generated
    public int getEar() {
        return this.ear;
    }

    @Generated
    public boolean isDrawElfEar() {
        return this.drawElfEar;
    }

    @Generated
    public int[] getPets() {
        return this.pets;
    }

    @Generated
    public int getDefFaceAcc() {
        return this.defFaceAcc;
    }

    @Generated
    public boolean isSecondLook() {
        return this.isSecondLook;
    }

    @Generated
    public String getName() {
        return this.name;
    }
}

