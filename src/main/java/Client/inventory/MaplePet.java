/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.inventory.PetFlag
 */
package Client.inventory;

import Client.inventory.MapleInventoryIdentifier;
import Client.inventory.PetFlag;
import Database.DatabaseLoader;
import Net.server.MapleItemInformationProvider;
import Net.server.movement.LifeMovement;
import Net.server.movement.LifeMovementFragment;
import Net.server.movement.MovementNormal;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.awt.Point;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaplePet
implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(MaplePet.class);
    private static final long serialVersionUID = 9179541993413738569L;
    private final int[] excluded = new int[10];
    private String name;
    private int Fh = 0;
    private int stance = 0;
    private final int uniqueid;
    private final int petitemid;
    private int secondsLeft = 0;
    private int color = -1;
    private int addSkill = 0;
    private Point pos;
    private byte fullness = (byte)100;
    private byte level = (byte)2;
    private byte summoned = 0;
    private short inventoryPosition = 0;
    private short closeness = 0;
    private short flags = 0;
    private boolean changed = false;
    private boolean canPickup = true;
    private final int[] buffIds = new int[]{0, 0};

    public MaplePet(int petitemid, int uniqueid) {
        this.petitemid = petitemid;
        this.uniqueid = uniqueid;
        for (int i = 0; i < this.excluded.length; ++i) {
            this.excluded[i] = 0;
        }
    }

    private MaplePet(int petitemid, int uniqueid, short inventorypos) {
        this.petitemid = petitemid;
        this.uniqueid = uniqueid;
        this.inventoryPosition = inventorypos;
        for (int i = 0; i < this.excluded.length; ++i) {
            this.excluded[i] = 0;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static MaplePet loadFromDb(int itemid, int petid, short inventorypos) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            MaplePet ret = new MaplePet(itemid, petid, inventorypos);
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM pets WHERE petid = ?");){
                ps.setInt(1, petid);
                try (ResultSet rs = ps.executeQuery();){
                    if (!rs.next()) {
                        rs.close();
                        ps.close();
                        MaplePet maplePet = null;
                        return maplePet;
                    }
                    ret.setName(rs.getString("name"));
                    ret.setCloseness(rs.getShort("closeness"));
                    ret.setLevel(rs.getByte("level"));
                    ret.setFullness(rs.getByte("fullness"));
                    ret.setSecondsLeft(rs.getInt("seconds"));
                    ret.setFlags(rs.getShort("flags"));
                    ret.setColor(rs.getByte("color"));
                    ret.setAddSkill(rs.getInt("addSkill"));
                    String[] list = rs.getString("excluded").split(",");
                    for (int i = 0; i < ret.excluded.length; ++i) {
                        ret.excluded[i] = Integer.parseInt(list[i]);
                    }
                    ret.changed = false;
                }
            }
            MaplePet maplePet = ret;
            return maplePet;
        }
        catch (SQLException ex) {
            log.error("加載寵物訊息出錯", ex);
            return null;
        }
    }

    public static final MaplePet createPet(int itemid) {
        return MaplePet.createPet(itemid, -1);
    }

    public static MaplePet createPet(int itemid, int uniqueid) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        return MaplePet.createPet(itemid, ii.getName(itemid), 1, 0, 100, uniqueid, ii.getLimitedLife(itemid), ii.getPetFlagInfo(itemid), -1);
    }

    public static MaplePet createPet(int itemid, String name, int level, int closeness, int fullness, int uniqueid, int secondsLeft, short flag, int color) {
        if (uniqueid <= -1) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement pse = con.prepareStatement("INSERT INTO pets (petid, name, level, closeness, fullness, seconds, flags, color) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");){
            pse.setInt(1, uniqueid);
            pse.setString(2, name);
            pse.setByte(3, (byte)level);
            pse.setShort(4, (short)closeness);
            pse.setByte(5, (byte)fullness);
            pse.setInt(6, secondsLeft);
            pse.setShort(7, flag);
            pse.setInt(8, color);
            pse.executeUpdate();
        }
        catch (SQLException ex) {
            log.error("創建寵物訊息出錯", ex);
            return null;
        }
        MaplePet pet = new MaplePet(itemid, uniqueid);
        pet.setName(name);
        pet.setLevel(level);
        pet.setFullness(fullness);
        pet.setCloseness(closeness);
        pet.setFlags(flag);
        pet.setSecondsLeft(secondsLeft);
        return pet;
    }

    public void saveToDb() {
        if (!this.changed) {
            return;
        }
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            try (PreparedStatement ps = con.prepareStatement("UPDATE pets SET name = ?, level = ?, closeness = ?, fullness = ?, seconds = ?, flags = ?, excluded = ?, color = ?, addSkill = ? WHERE petid = ?");){
                ps.setString(1, this.name);
                ps.setByte(2, this.getLevel());
                ps.setShort(3, this.closeness);
                ps.setByte(4, this.fullness);
                ps.setInt(5, this.secondsLeft);
                ps.setShort(6, this.flags);
                StringBuilder list = new StringBuilder();
                for (int anExcluded : this.excluded) {
                    list.append(anExcluded);
                    list.append(",");
                }
                String newlist = list.toString();
                ps.setString(7, newlist.substring(0, newlist.length() - 1));
                ps.setInt(8, this.color);
                ps.setInt(9, this.addSkill);
                ps.setInt(10, this.uniqueid);
                ps.executeUpdate();
            }
            this.changed = false;
        }
        catch (SQLException ex) {
            log.error("儲存寵物訊息出錯", ex);
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.changed = true;
    }

    public boolean getSummoned() {
        return this.summoned > 0;
    }

    public void setSummoned(int summoned) {
        this.summoned = (byte)summoned;
    }

    public byte getSummonedValue() {
        return this.summoned;
    }

    public short getInventoryPosition() {
        return this.inventoryPosition;
    }

    public void setInventoryPosition(short inventorypos) {
        this.inventoryPosition = inventorypos;
    }

    public int getUniqueId() {
        return this.uniqueid;
    }

    public short getCloseness() {
        return this.closeness;
    }

    public void setCloseness(int closeness) {
        closeness = Math.max(1, closeness);
        this.closeness = (short)closeness;
        this.changed = true;
    }

    public byte getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = (byte)Math.max(1, level);
        this.changed = true;
    }

    public byte getFullness() {
        return this.fullness;
    }

    public void setFullness(int fullness) {
        this.fullness = (byte)fullness;
        this.changed = true;
    }

    public short getFlags() {
        return this.flags;
    }

    public void setFlags(int fffh) {
        this.flags = (short)fffh;
        this.changed = true;
    }

    public int getFh() {
        return this.Fh;
    }

    public void setFh(int Fh) {
        this.Fh = Fh;
    }

    public Point getPos() {
        return this.pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public byte getType() {
        return 3;
    }

    public int getStance() {
        return this.stance;
    }

    public void setStance(int stance) {
        this.stance = stance;
    }

    public int getPetItemId() {
        return this.petitemid;
    }

    public boolean canConsume(int itemId) {
        MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
        for (int petId : mii.getItemEffect(itemId).getPetsCanConsume()) {
            if (petId != this.petitemid) continue;
            return true;
        }
        return false;
    }

    public void updatePosition(List<LifeMovementFragment> movement) {
        for (LifeMovementFragment move : movement) {
            if (!(move instanceof LifeMovement)) continue;
            if (move instanceof MovementNormal) {
                this.setPos(((MovementNormal)move).getPosition());
                this.setFh(((MovementNormal)move).getFH());
            }
            this.setStance(((LifeMovement)move).getMoveAction());
        }
    }

    public int getSecondsLeft() {
        return this.secondsLeft;
    }

    public void setSecondsLeft(int sl) {
        this.secondsLeft = sl;
        this.changed = true;
    }

    public int[] getBuffSkills() {
        return this.buffIds;
    }

    public int getBuffSkill(int index) {
        return this.buffIds[index];
    }

    public void setBuffSkill(int index, int id) {
        this.buffIds[index] = id;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
        this.changed = true;
    }

    public int getAddSkill() {
        return this.addSkill;
    }

    public void setAddSkill(int addSkill) {
        this.addSkill = addSkill;
        this.changed = true;
    }

    public void clearExcluded() {
        for (int i = 0; i < this.excluded.length; ++i) {
            this.excluded[i] = 0;
        }
        this.changed = true;
    }

    public List<Integer> getExcluded() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int anExcluded : this.excluded) {
            if (anExcluded <= 0 || !PetFlag.PET_IGNORE_PICKUP.check((int)this.flags)) continue;
            list.add(anExcluded);
        }
        return list;
    }

    public void addExcluded(int i, int itemId) {
        if (i < this.excluded.length) {
            this.excluded[i] = itemId;
            this.changed = true;
        }
    }

    public boolean isCanPickup() {
        return this.canPickup;
    }

    public void setCanPickup(boolean can) {
        this.canPickup = can;
    }
}

