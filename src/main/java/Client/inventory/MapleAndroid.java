/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Packet.AndroidPacket
 */
package Client.inventory;

import Client.MapleCharacter;
import Client.inventory.MapleInventoryIdentifier;
import Database.DatabaseLoader;
import Net.server.MapleItemInformationProvider;
import Net.server.StructAndroid;
import Net.server.movement.LifeMovement;
import Net.server.movement.LifeMovementFragment;
import Net.server.movement.MovementNormal;
import Packet.AndroidPacket;
import Packet.PacketHelper;
import com.alibaba.druid.pool.DruidPooledConnection;
import connection.OutPacket;
import java.awt.Point;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Randomizer;
import tools.data.MaplePacketLittleEndianWriter;

public class MapleAndroid
implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(MapleAndroid.class);
    private static final long serialVersionUID = 9179541993413738569L;
    private final int uniqueid;
    private final int itemid;
    private int Fh = 0;
    private int stance = 0;
    private int skin;
    private int hair;
    private int face;
    private int gender;
    private int type;
    private String name;
    private boolean antennaUsed;
    private long shopTime;
    private Point pos = new Point(0, 0);
    private boolean changed = false;

    public MapleAndroid(int itemid, int uniqueid) {
        this.itemid = itemid;
        this.uniqueid = uniqueid;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static MapleAndroid loadFromDb(int itemid, int uniqueid) {
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            MapleAndroid ret = new MapleAndroid(itemid, uniqueid);
            PreparedStatement ps = con.prepareStatement("SELECT * FROM androids WHERE uniqueid = ?");
            ps.setInt(1, uniqueid);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                MapleAndroid mapleAndroid = null;
                return mapleAndroid;
            }
            int type = rs.getInt("type");
            int gender = rs.getInt("gender");
            boolean fix = false;
            if (type < 1) {
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                StructAndroid aInfo = ii.getAndroidInfo(type = ii.getAndroidType(itemid));
                if (aInfo == null) {
                    MapleAndroid mapleAndroid = null;
                    return mapleAndroid;
                }
                gender = aInfo.gender;
                fix = true;
            }
            ret.setType(type);
            ret.setGender(gender);
            ret.setSkin(rs.getInt("skin"));
            ret.setHair(rs.getInt("hair"));
            ret.setFace(rs.getInt("face"));
            ret.setName(rs.getString("name"));
            ret.setAntennaUsed(rs.getByte("antennaUsed") == 1);
            ret.setShopTime(rs.getTimestamp("shopTime") == null ? -1L : rs.getTimestamp("shopTime").getTime());
            ret.changed = fix;
            rs.close();
            ps.close();
            MapleAndroid mapleAndroid = ret;
            return mapleAndroid;
        }
        catch (SQLException ex) {
            log.error("加載機器人信息出錯", ex);
            return null;
        }
    }

    public static MapleAndroid create(int itemid, int uniqueid) {
        int type;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        StructAndroid aInfo = ii.getAndroidInfo(type = ii.getAndroidType(itemid));
        if (aInfo == null) {
            return null;
        }
        int gender = aInfo.gender;
        int skin = aInfo.skin.get(Randomizer.nextInt(aInfo.skin.size()));
        int hair = aInfo.hair.get(Randomizer.nextInt(aInfo.hair.size()));
        int face = aInfo.face.get(Randomizer.nextInt(aInfo.face.size()));
        if (uniqueid <= -1) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement pse = con.prepareStatement("INSERT INTO androids (uniqueid, type, gender, skin, hair, face, name) VALUES (?, ?, ?, ?, ?, ?, ?)");
            pse.setInt(1, uniqueid);
            pse.setInt(2, type);
            pse.setInt(3, gender);
            pse.setInt(4, skin);
            pse.setInt(5, hair);
            pse.setInt(6, face);
            pse.setString(7, "機器人");
            pse.executeUpdate();
            pse.close();
        }
        catch (SQLException ex) {
            log.error("創建機器人信息出錯", ex);
            return null;
        }
        MapleAndroid and = new MapleAndroid(itemid, uniqueid);
        and.setType(type);
        and.setGender(gender);
        and.setSkin(skin);
        and.setHair(hair);
        and.setFace(face);
        and.setName("機器人");
        and.setAntennaUsed(false);
        and.setShopTime(-1L);
        return and;
    }

    public void saveToDb() {
        this.saveToDb(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveToDb(Connection con) {
        if (!this.changed) {
            return;
        }
        boolean needclose = false;
        try {
            if (con == null) {
                needclose = true;
                con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
            }
            PreparedStatement ps = con.prepareStatement("UPDATE androids SET type = ?, gender = ?, skin = ?, hair = ?, face = ?, name = ?, antennaUsed = ?, shopTime = ? WHERE uniqueid = ?");
            ps.setInt(1, this.type);
            ps.setInt(2, this.gender);
            ps.setInt(3, this.skin);
            ps.setInt(4, this.hair);
            ps.setInt(5, this.face);
            ps.setString(6, this.name);
            ps.setByte(7, (byte)(this.antennaUsed ? 1 : 0));
            if (this.shopTime >= 0L) {
                ps.setTimestamp(8, new Timestamp(this.shopTime));
            } else {
                ps.setNull(8, 93);
            }
            ps.setInt(9, this.uniqueid);
            ps.executeUpdate();
            ps.close();
            this.changed = false;
        }
        catch (SQLException ex) {
            log.error("保存機器人信息出錯", ex);
        }
        finally {
            if (needclose) {
                try {
                    con.close();
                }
                catch (SQLException e) {
                    log.error("保存機器人信息出錯", e);
                }
            }
        }
    }

    public int getItemId() {
        return this.itemid;
    }

    public int getUniqueId() {
        return this.uniqueid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String n) {
        this.name = n;
        this.changed = true;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int t) {
        this.type = t;
        this.changed = true;
    }

    public int getGender() {
        return this.gender;
    }

    public void setGender(int g) {
        this.gender = g;
        this.changed = true;
    }

    public int getSkin() {
        return this.skin;
    }

    public void setSkin(int s) {
        this.skin = s;
        this.changed = true;
    }

    public int getHair() {
        return this.hair;
    }

    public void setHair(int h) {
        this.hair = h;
        this.changed = true;
    }

    public int getFace() {
        return this.face;
    }

    public void setFace(int f) {
        this.face = f;
        this.changed = true;
    }

    public boolean isAntennaUsed() {
        return this.antennaUsed;
    }

    public void setAntennaUsed(boolean a) {
        this.antennaUsed = a;
        this.changed = true;
    }

    public long getShopTime() {
        return this.shopTime;
    }

    public void setShopTime(long s) {
        this.shopTime = s;
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

    public int getStance() {
        return this.stance;
    }

    public void setStance(int stance) {
        this.stance = stance;
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

    public void showEmotion(MapleCharacter chr, String emotion) {
        int speak;
        byte animation = 0;
        switch (emotion) {
            case "hello": {
                speak = 0;
                animation = (byte)Randomizer.rand(1, 17);
                break;
            }
            case "levelup": {
                speak = 1;
                animation = (byte)Randomizer.rand(1, 17);
                break;
            }
            case "dead": {
                speak = 2;
                animation = (byte)Randomizer.rand(1, 17);
                break;
            }
            case "bye": {
                speak = 3;
                break;
            }
            case "job": {
                speak = 4;
                break;
            }
            case "alert": {
                speak = 5;
                animation = (byte)(!Randomizer.isSuccess(10) ? 1 : 0);
                if (animation != 1) break;
                return;
            }
            default: {
                return;
            }
        }
        chr.getMap().broadcastMessage(AndroidPacket.showAndroidEmotion((int)chr.getId(), (int)speak, (int)animation));
    }

    public void encodeAndroidLook(MaplePacketLittleEndianWriter mplew) {
        OutPacket outPacket = new OutPacket();
        this.encodeAndroidLook(outPacket);
        mplew.write(outPacket.getData());
    }

    public void encodeAndroidLook(OutPacket outPacket) {
        outPacket.encodeShort(this.getSkin() >= 2000 ? this.getSkin() - 2000 : this.getSkin());
        outPacket.encodeInt(this.getHair());
        outPacket.encodeInt(this.getFace());
        outPacket.encodeString(this.getName());
        outPacket.encodeInt(this.isAntennaUsed() ? 2892000 : 0);
        outPacket.encodeLong(PacketHelper.getTime(this.getShopTime() < 0L ? -2L : this.getShopTime()));
        outPacket.encodeInt(0);
    }
}

