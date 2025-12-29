/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  SwordieX.overseas.extraequip.ExtraEquipResult
 *  connection.packet.OverseasPacket
 */
package Client;

import Client.MapleClient;
import Client.inventory.FamiliarCard;
import Config.configs.ServerConfig;
import Net.server.MapleItemInformationProvider;
import Net.server.StructItemOption;
import Net.server.maps.AnimatedMapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Net.server.movement.LifeMovement;
import Net.server.movement.LifeMovementFragment;
import SwordieX.overseas.extraequip.ExtraEquipResult;
import connection.OutPacket;
import connection.packet.OverseasPacket;
import java.awt.Point;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import tools.Randomizer;

public final class MonsterFamiliar
extends AnimatedMapleMapObject
implements Serializable {
    private static final long serialVersionUID = 795419937713738569L;
    private final int id;
    private final int familiar;
    private final int accountid;
    private final int characterid;
    private int exp;
    private String name;
    private short fh = 0;
    private byte grade;
    private byte level;
    private int skill;
    private int option1;
    private int option2;
    private int option3;
    private double pad;
    private byte flag = (byte)8;
    private boolean summoned = false;
    private boolean lock = false;

    public MonsterFamiliar(int id, int familiar, int accountid, int characterid, String name, byte grade, byte level, int exp, int skill, int option1, int option2, int option3, boolean summoned, boolean lock) {
        this.id = id;
        this.familiar = familiar;
        this.accountid = accountid;
        this.characterid = characterid;
        this.name = name;
        this.grade = (byte)Math.min(Math.max(0, grade), 4);
        this.level = (byte)Math.min(Math.max(1, level), 5);
        this.exp = exp;
        this.skill = skill;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.setStance(0);
        this.setPosition(new Point(0, 0));
        this.summoned = summoned;
        this.setLock(lock);
    }

    public MonsterFamiliar(int accountid, int characterid, int familiar, FamiliarCard mf) {
        this.id = Randomizer.nextInt();
        this.accountid = accountid;
        this.characterid = characterid;
        this.familiar = familiar;
        this.name = "";
        this.grade = mf.getGrade();
        this.level = mf.getLevel();
        this.skill = mf.getSkill() > 0 ? mf.getSkill() : Randomizer.rand(800, 904) + 1;
        this.pad = MapleItemInformationProvider.getInstance().getFamiliarTable_pad().get(this.getGrade()).get(this.level - 1).floatValue();
        if (mf.getOption1() > 0 || mf.getOption2() > 0 || mf.getOption3() > 0) {
            this.option1 = mf.getOption1();
            this.option2 = mf.getOption2();
            this.option3 = mf.getOption3();
        } else {
            this.initOptions();
        }
    }

    public void initPad() {
        this.pad = MapleItemInformationProvider.getInstance().getFamiliarTable_pad().get(this.getGrade()).get(this.level - 1).floatValue();
    }

    public int getOption(int i) {
        switch (i) {
            case 0: {
                return this.option1;
            }
            case 1: {
                return this.option2;
            }
            case 2: {
                return this.option3;
            }
        }
        return 0;
    }

    public int setOption(int i, int option) {
        switch (i) {
            case 0: {
                this.option1 = option;
            }
            case 1: {
                this.option2 = option;
            }
            case 2: {
                this.option3 = option;
            }
        }
        return 0;
    }

    public void initOptions() {
        LinkedList<LinkedList<StructItemOption>> options = new LinkedList<LinkedList<StructItemOption>>(MapleItemInformationProvider.getInstance().getFamiliar_option().values());
        int incDAMrCount = 0;
        for (int i = 0; i < 3; ++i) {
            Collections.shuffle(options);
            for (List list : options) {
                if (list.size() < this.level) continue;
                StructItemOption option = (StructItemOption)list.get(this.level - 1);
                if (option.opID / 10000 != this.grade) continue;
                if (ServerConfig.familiarIncDAMrHard && option.opString.contains("最終傷害")) {
                    if (!Randomizer.isSuccess(40 - 19 * incDAMrCount)) continue;
                    incDAMrCount = (byte)(incDAMrCount + 1);
                }
                this.setOption(i, option.opID);
            }
        }
    }

    public void gainExp(int exp) {
        this.exp += exp;
        while (this.exp >= 100) {
            this.level = (byte)(this.level + 1);
            this.exp -= 100;
        }
        if (this.level >= 5) {
            this.level = (byte)5;
            this.exp = 0;
        }
    }

    public void updateGrade() {
        this.grade = (byte)(this.grade + 1);
        this.level = 1;
        if (this.grade >= 4) {
            this.grade = (byte)4;
        }
    }

    public double getPad() {
        return this.pad;
    }

    public int getCharacterid() {
        return this.characterid;
    }

    public void setFh(short fh) {
        this.fh = fh;
    }

    public int getGrade() {
        return this.grade;
    }

    public void setGrade(byte grade) {
        this.grade = grade;
    }

    public byte getLevel() {
        return this.level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public int getExp() {
        return this.exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setSkill(int skill) {
        this.skill = skill;
    }

    public void setOption1(int option1) {
        this.option1 = option1;
    }

    public void setOption2(int option2) {
        this.option2 = option2;
    }

    public void setOption3(int option3) {
        this.option3 = option3;
    }

    public int getSkill() {
        return this.skill;
    }

    public void setSkill(short skill) {
        this.skill = skill;
    }

    public int getOption1() {
        return this.option1;
    }

    public void setOption1(short option1) {
        this.option1 = option1;
    }

    public int getOption2() {
        return this.option2;
    }

    public void setOption2(short option2) {
        this.option2 = option2;
    }

    public int getOption3() {
        return this.option3;
    }

    public void setOption3(short option3) {
        this.option3 = option3;
    }

    public int getFamiliar() {
        return this.familiar;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public short getFh() {
        return this.fh;
    }

    public void setFh(int f) {
        this.fh = (short)f;
    }

    @Override
    public int getRange() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        if (client.getPlayer() == null || client.getPlayer().getId() != this.characterid) {
            client.write(OverseasPacket.extraEquipResult((ExtraEquipResult)ExtraEquipResult.spawnFamiliar((int)this.accountid, (int)this.characterid, (boolean)false, (MonsterFamiliar)this, (Point)this.getPosition(), (boolean)false)));
        }
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        if (client.getPlayer() == null || client.getPlayer().getId() != this.characterid) {
            client.write(OverseasPacket.extraEquipResult((ExtraEquipResult)ExtraEquipResult.removeFamiliar((int)this.characterid, (boolean)false)));
        }
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.FAMILIAR;
    }

    public void updatePosition(List<LifeMovementFragment> movement) {
        for (LifeMovementFragment move : movement) {
            if (!(move instanceof LifeMovement)) continue;
            this.setStance(((LifeMovement)move).getMoveAction());
        }
    }

    public FamiliarCard createFamiliarCard() {
        return new FamiliarCard((short)this.skill, this.level, this.grade, this.option1, this.option2, this.option3);
    }

    public boolean isSummoned() {
        return this.summoned;
    }

    public void setSummoned(boolean summoned) {
        this.summoned = summoned;
    }

    public byte getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = (byte)flag;
    }

    public boolean hasFlag(int flag) {
        return (this.flag | (byte)flag) != 0;
    }

    public void addFlag(int flag) {
        this.flag = (byte)(this.flag | (byte)flag);
    }

    public void removeFlag(int flag) {
        this.flag = (byte)(this.flag & (byte)(~flag));
    }

    public boolean isLock() {
        return this.lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
        this.flag = lock ? (byte)(this.flag | 0x10) : (byte)(this.flag & 0xFFFFFFEF);
    }

    public void encode(OutPacket outPacket) {
        outPacket.encodeLong(this.id);
        outPacket.encodeInt(2);
        outPacket.encodeInt(this.familiar);
        outPacket.encodeString(this.name, 15);
        outPacket.encodeByte(0);
        outPacket.encodeShort(this.level);
        outPacket.encodeShort(this.skill);
        outPacket.encodeShort(this.exp);
        outPacket.encodeShort(0);
        outPacket.encodeShort(this.level);
        for (int i = 0; i < 3; ++i) {
            outPacket.encodeShort(this.getOption(i));
        }
        outPacket.encodeByte(this.flag);
        outPacket.encodeByte(this.grade);
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);
        outPacket.encodeInt(0);
    }
}

