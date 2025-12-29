/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.inventory.MapleRing
 */
package Client.inventory;

import Client.inventory.Equip;
import Client.inventory.FamiliarCard;
import Client.inventory.Item;
import Client.inventory.MapleAndroid;
import Client.inventory.MapleInventoryIdentifier;
import Client.inventory.MapleInventoryType;
import Client.inventory.MaplePet;
import Client.inventory.MapleRing;
import Config.constants.ItemConstants;
import Database.DatabaseLoader;
import Net.server.MapleItemInformationProvider;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tools.Pair;

public enum ItemLoader {
    裝備道具(0, false),
    倉庫道具(1, true),
    現金道具(2, true),
    僱傭道具(5, false),
    送貨道具(6, false),
    拍賣道具(8, false),
    MTS_TRANSFER(9, false),
    釣魚道具(10, false);

    private final int value;
    private final boolean account;

    private ItemLoader(int value, boolean account) {
        this.value = value;
        this.account = account;
    }

    public int getValue() {
        return this.value;
    }

    public Map<Long, Pair<Item, MapleInventoryType>> loadItems(boolean login, long id) throws SQLException {
        LinkedHashMap<Long, Pair<Item, MapleInventoryType>> items = new LinkedHashMap<Long, Pair<Item, MapleInventoryType>>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT *, familiarcard.grade AS mfgrade, familiarcard.skill AS mfskill, familiarcard.level AS mflevel FROM `inventoryitems` LEFT JOIN `inventoryequipment` USING(`inventoryitemid`) LEFT JOIN `familiarcard` USING(`inventoryitemid`) WHERE `type` = ? AND `");
        switch (this.ordinal()) {
            case 1: 
            case 2: {
                query.append("accountid");
                break;
            }
            case 5: {
                query.append("auction_id");
                break;
            }
            default: {
                query.append("characterid");
            }
        }
        query.append("` = ?");
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(query.toString());){
            if (login) {
                query.append(" AND `inventorytype` = ");
                query.append(MapleInventoryType.EQUIPPED.getType());
            }
            ps.setInt(1, this.value);
            ps.setLong(2, id);
            try (ResultSet rs = ps.executeQuery();){
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                while (rs.next()) {
                    if (!ii.itemExists(rs.getInt("itemid"))) continue;
                    MapleInventoryType mit = MapleInventoryType.getByType(rs.getByte("inventorytype"));
                    if (mit.equals((Object)MapleInventoryType.EQUIP) && ii.isCash(rs.getInt("itemid"))) {
                        mit = MapleInventoryType.DECORATION;
                    }
                    if (mit.equals((Object)MapleInventoryType.EQUIP) || mit.equals((Object)MapleInventoryType.EQUIPPED) || mit.equals((Object)MapleInventoryType.DECORATION)) {
                        Equip equip = new Equip(rs.getInt("itemid"), rs.getShort("position"), rs.getInt("sn"), rs.getInt("flag"), rs.getShort("espos"));
                        equip.setItemSkin(rs.getInt("itemSkin"));
                        if (!login && equip.getPosition() != -55) {
                            equip.setMvpEquip(rs.getByte("mvpEquip") == 1);
                            equip.setQuantity((short)1);
                            equip.setInventoryId(rs.getLong("inventoryitemid"));
                            equip.setOwner(rs.getString("owner"));
                            equip.setExpiration(rs.getLong("expiredate"));
                            equip.setRestUpgradeCount(rs.getByte("upgradeslots"));
                            equip.setCurrentUpgradeCount(rs.getByte("level"));
                            equip.setStr(rs.getShort("str"));
                            equip.setDex(rs.getShort("dex"));
                            equip.setInt(rs.getShort("int"));
                            equip.setLuk(rs.getShort("luk"));
                            equip.setHp(rs.getShort("hp"));
                            equip.setMp(rs.getShort("mp"));
                            equip.setPad(rs.getShort("watk"));
                            equip.setMad(rs.getShort("matk"));
                            equip.setPdd(rs.getShort("wdef"));
                            equip.setMdd(rs.getShort("mdef"));
                            equip.setAcc(rs.getShort("acc"));
                            equip.setAvoid(rs.getShort("avoid"));
                            equip.setHands(rs.getShort("hands"));
                            equip.setSpeed(rs.getShort("speed"));
                            equip.setJump(rs.getShort("jump"));
                            equip.setViciousHammer(rs.getByte("ViciousHammer"));
                            equip.setPlatinumHammer(rs.getByte("PlatinumHammer"));
                            equip.setItemEXP(rs.getLong("itemEXP"));
                            equip.setGMLog(rs.getString("GM_Log"));
                            equip.setDurability(rs.getInt("durability"));
                            equip.setStarForceLevel(rs.getByte("starForce"));
                            equip.setState(rs.getByte("state"), false);
                            equip.setPotential1(rs.getInt("potential1"));
                            equip.setPotential2(rs.getInt("potential2"));
                            equip.setPotential3(rs.getInt("potential3"));
                            equip.setState(rs.getByte("addState"), true);
                            equip.setPotential4(rs.getInt("potential4"));
                            equip.setPotential5(rs.getInt("potential5"));
                            equip.setPotential6(rs.getInt("potential6"));
                            equip.setGiftFrom(rs.getString("sender"));
                            equip.setIncSkill(rs.getInt("incSkill"));
                            equip.setPVPDamage(rs.getShort("pvpDamage"));
                            equip.setCharmEXP(rs.getShort("charmEXP"));
                            equip.setSocket1(rs.getInt("itemSlot1"));
                            equip.setSocket2(rs.getInt("itemSlot2"));
                            equip.setSocket3(rs.getInt("itemSlot3"));
                            equip.setEnchantBuff(rs.getShort("enhanctBuff"));
                            equip.setYggdrasilWisdom(rs.getShort("yggdrasilWisdom"));
                            equip.setFinalStrike(rs.getShort("finalStrike") > 0);
                            equip.setBossDamage(rs.getShort("bossDamage"));
                            equip.setIgnorePDR(rs.getShort("ignorePDR"));
                            equip.setTotalDamage(rs.getShort("totalDamage"));
                            equip.setAllStat(rs.getShort("allStat"));
                            equip.setKarmaCount(rs.getShort("karmaCount"));
                            equip.setFlameFlag(rs.getLong("nirvanaFlame"));
                            equip.setSealedLevel(equip.isSealedEquip() ? (byte)Math.max(1, rs.getByte("sealedlevel")) : (byte)0);
                            equip.setSealedExp(rs.getLong("sealedExp"));
                            equip.setSoulOptionID(rs.getShort("soulname"));
                            equip.setSoulSocketID(rs.getShort("soulenchanter"));
                            equip.setSoulOption(rs.getShort("soulpotential"));
                            equip.setSoulSkill(rs.getInt("soulskill"));
                            equip.setARC(rs.getShort("arc"));
                            equip.setARCExp(rs.getInt("arcexp"));
                            equip.setARCLevel(rs.getShort("arclevel"));
                            equip.setAut(rs.getShort("aut"));
                            equip.setAutExp(rs.getInt("autexp"));
                            equip.setAutLevel(rs.getShort("autlevel"));
                            if (equip.getCharmEXP() < 0) {
                                equip.setCharmEXP(ii.getEquipById(equip.getItemId()).getCharmEXP());
                            }
                            if (equip.getBossDamage() <= 0 && ii.getBossDamageRate(equip.getItemId()) > 0) {
                                equip.setBossDamage((short)ii.getBossDamageRate(equip.getItemId()));
                            }
                            if (equip.getIgnorePDR() <= 0 && ii.getIgnoreMobDmageRate(equip.getItemId()) > 0) {
                                equip.setIgnorePDR((short)ii.getIgnoreMobDmageRate(equip.getItemId()));
                            }
                            if (equip.getTotalDamage() <= 0 && ii.getTotalDamage(equip.getItemId()) > 0) {
                                equip.setTotalDamage((short)ii.getTotalDamage(equip.getItemId()));
                            }
                            if (equip.getPotential1() == 0 && ii.getOption(equip.getItemId(), 1) > 0) {
                                equip.setPotential1(ii.getOption(equip.getItemId(), 1));
                            }
                            if (equip.getPotential2() == 0 && ii.getOption(equip.getItemId(), 2) > 0) {
                                equip.setPotential2(ii.getOption(equip.getItemId(), 2));
                            }
                            if (equip.getPotential3() == 0 && ii.getOption(equip.getItemId(), 3) > 0) {
                                equip.setPotential3(ii.getOption(equip.getItemId(), 3));
                            }
                            if (equip.getItemSkin() > 0 && !ii.itemExists(equip.getItemSkin())) {
                                equip.setItemSkin(0);
                            }
                            if (equip.getSN() > -1) {
                                MapleAndroid android;
                                if (ItemConstants.類型.特效裝備(rs.getInt("itemid"))) {
                                    MapleRing ring = MapleRing.loadFromDb((int)equip.getSN(), (boolean)mit.equals((Object)MapleInventoryType.EQUIPPED));
                                    if (ring != null) {
                                        equip.setRing(ring);
                                    }
                                } else if (ItemConstants.類型.機器人(equip.getItemId()) && (android = MapleAndroid.loadFromDb(equip.getItemId(), equip.getSN())) != null) {
                                    equip.setAndroid(android);
                                }
                            }
                            if (equip.hasSetOnlyId()) {
                                equip.setSN(MapleInventoryIdentifier.getInstance());
                            }
                        }
                        items.put(rs.getLong("inventoryitemid"), new Pair<Item, MapleInventoryType>(equip.copy(), mit));
                        continue;
                    }
                    Item item = new Item(rs.getInt("itemid"), rs.getShort("position"), rs.getShort("quantity"), rs.getInt("flag"), rs.getInt("sn"), rs.getShort("espos"));
                    item.setOwner(rs.getString("owner"));
                    item.setInventoryId(rs.getLong("inventoryitemid"));
                    item.setExpiration(rs.getLong("expiredate"));
                    item.setGMLog(rs.getString("GM_Log"));
                    item.setGiftFrom(rs.getString("sender"));
                    item.setExtendSlot(rs.getShort("extendSlot"));
                    if (ItemConstants.getFamiliarByItemID(item.getItemId()) > 0) {
                        item.setFamiliarCard(new FamiliarCard(rs.getShort("mfskill"), rs.getByte("mflevel"), rs.getByte("mfgrade"), rs.getInt("option1"), rs.getInt("option2"), rs.getInt("option3")));
                    }
                    if (ItemConstants.類型.寵物(item.getItemId())) {
                        if (item.getSN() > -1) {
                            MaplePet pet = MaplePet.loadFromDb(item.getItemId(), item.getSN(), item.getPosition());
                            if (pet != null) {
                                item.setPet(pet);
                                pet.setInventoryPosition(item.getPosition());
                            }
                        } else {
                            item.setPet(MaplePet.createPet(item.getItemId(), MapleInventoryIdentifier.getInstance()));
                            item.getPet().setInventoryPosition(item.getPosition());
                        }
                    }
                    items.put(rs.getLong("inventoryitemid"), new Pair<Item, MapleInventoryType>(item.copy(), mit));
                }
            }
        }
        return items;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveItems(Connection con, List<Pair<Item, MapleInventoryType>> items, long id) throws SQLException {
        PreparedStatement ps = null;
        PreparedStatement pse = null;
        PreparedStatement psf = null;
        boolean needclose = false;
        try {
            if (con == null) {
                needclose = true;
                con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();
            }
            String query = String.format("DELETE FROM `inventoryitems` WHERE `type` = ? AND `%s` = ?", this == 拍賣道具 ? "auction_id" : (this.account ? "accountid" : "characterid"));
            ps = con.prepareStatement(query);
            ps.setInt(1, this.value);
            ps.setLong(2, id);
            ps.executeUpdate();
            ps.close();
            if (items == null) {
                return;
            }
            ps = con.prepareStatement("INSERT INTO `inventoryitems` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
            pse = con.prepareStatement("INSERT INTO `inventoryequipment` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            psf = con.prepareStatement("INSERT INTO `familiarcard` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?)");
            for (Pair<Item, MapleInventoryType> pair : items) {
                int i;
                Item item = pair.getLeft();
                MapleInventoryType mit = pair.getRight();
                ps.setInt(1, this.value);
                if (this == 拍賣道具) {
                    ps.setString(2, null);
                    ps.setString(3, null);
                    ps.setLong(4, id);
                } else {
                    ps.setString(2, this.account ? null : String.valueOf(id));
                    ps.setString(3, this.account ? String.valueOf(id) : null);
                    ps.setNull(4, -5);
                }
                ps.setInt(5, item.getItemId());
                ps.setInt(6, mit.getType());
                ps.setInt(7, item.getPosition());
                ps.setInt(8, item.getQuantity());
                ps.setString(9, item.getOwner());
                ps.setString(10, item.getGMLog());
                if (item.getPet() != null) {
                    ps.setInt(11, Math.max(item.getSN(), item.getPet().getUniqueId()));
                } else {
                    ps.setInt(11, item.getSN());
                }
                ps.setInt(12, item.getAttribute());
                ps.setLong(13, item.getTrueExpiration());
                ps.setString(14, item.getGiftFrom());
                ps.setInt(15, item.getESPos());
                ps.setInt(16, item.getExtendSlot());
                ps.executeUpdate();
                if (item.getFamiliarCard() != null && ItemConstants.getFamiliarByItemID(item.getItemId()) > 0) {
                    i = 0;
                    ResultSet rs = ps.getGeneratedKeys();
                    if (!rs.next()) {
                        throw new RuntimeException("[saveItems] 保存道具失敗.");
                    }
                    psf.setLong(++i, rs.getLong(1));
                    rs.close();
                    FamiliarCard fc = item.getFamiliarCard();
                    psf.setByte(++i, fc.getLevel());
                    psf.setByte(++i, fc.getGrade());
                    psf.setShort(++i, fc.getSkill());
                    psf.setInt(++i, fc.getOption1());
                    psf.setInt(++i, fc.getOption2());
                    psf.setInt(++i, fc.getOption3());
                    psf.executeUpdate();
                }
                if (!mit.equals((Object)MapleInventoryType.EQUIP) && !mit.equals((Object)MapleInventoryType.EQUIPPED) && !mit.equals((Object)MapleInventoryType.DECORATION)) continue;
                i = 0;
                try (ResultSet rs = ps.getGeneratedKeys();){
                    if (!rs.next()) {
                        throw new RuntimeException("[saveItems] 保存道具失敗.");
                    }
                    pse.setLong(++i, rs.getLong(1));
                }
                Equip equip = (Equip)item;
                pse.setInt(++i, equip.getRestUpgradeCount());
                pse.setInt(++i, equip.getCurrentUpgradeCount());
                pse.setInt(++i, equip.getStr());
                pse.setInt(++i, equip.getDex());
                pse.setInt(++i, equip.getInt());
                pse.setInt(++i, equip.getLuk());
                pse.setInt(++i, equip.getHp());
                pse.setInt(++i, equip.getMp());
                pse.setInt(++i, equip.getPad());
                pse.setInt(++i, equip.getMad());
                pse.setInt(++i, equip.getPdd());
                pse.setInt(++i, equip.getMdd());
                pse.setInt(++i, equip.getAcc());
                pse.setInt(++i, equip.getAvoid());
                pse.setInt(++i, equip.getHands());
                pse.setInt(++i, equip.getSpeed());
                pse.setInt(++i, equip.getJump());
                pse.setInt(++i, equip.getViciousHammer());
                pse.setInt(++i, equip.getPlatinumHammer());
                pse.setLong(++i, equip.getItemEXP());
                pse.setInt(++i, equip.getDurability());
                pse.setByte(++i, equip.getState(false));
                pse.setByte(++i, equip.getStarForceLevel());
                pse.setInt(++i, equip.getPotential1());
                pse.setInt(++i, equip.getPotential2());
                pse.setInt(++i, equip.getPotential3());
                pse.setByte(++i, equip.getState(true));
                pse.setInt(++i, equip.getPotential4());
                pse.setInt(++i, equip.getPotential5());
                pse.setInt(++i, equip.getPotential6());
                pse.setInt(++i, equip.getIncSkill());
                pse.setShort(++i, equip.getCharmEXP());
                pse.setShort(++i, equip.getPVPDamage());
                pse.setInt(++i, 0);
                pse.setInt(++i, equip.getSocket1());
                pse.setInt(++i, equip.getSocket2());
                pse.setInt(++i, equip.getSocket3());
                pse.setInt(++i, equip.getItemSkin());
                pse.setLong(++i, 0L);
                pse.setInt(++i, equip.getEnchantBuff());
                pse.setInt(++i, equip.getReqLevel());
                pse.setInt(++i, equip.getYggdrasilWisdom());
                pse.setInt(++i, equip.getFinalStrike() ? 1 : 0);
                pse.setInt(++i, equip.getBossDamage());
                pse.setInt(++i, equip.getIgnorePDR());
                pse.setInt(++i, equip.getTotalDamage());
                pse.setInt(++i, equip.getAllStat());
                pse.setInt(++i, equip.getKarmaCount());
                pse.setLong(++i, equip.getFlameFlag());
                pse.setInt(++i, equip.getSealedLevel());
                pse.setLong(++i, equip.getSealedExp());
                pse.setShort(++i, equip.getSoulOptionID());
                pse.setShort(++i, equip.getSoulSocketID());
                pse.setShort(++i, equip.getSoulOption());
                pse.setInt(++i, equip.getSoulSkill());
                pse.setShort(++i, equip.getARC());
                pse.setInt(++i, equip.getArcExp());
                pse.setInt(++i, equip.getARCLevel());
                pse.setShort(++i, equip.getAut());
                pse.setInt(++i, equip.getAutExp());
                pse.setInt(++i, equip.getAutLevel());
                pse.setByte(++i, (byte)(equip.isMvpEquip(false) ? 1 : 0));
                pse.executeUpdate();
            }
        }
        finally {
            if (ps != null) {
                ps.close();
            }
            if (pse != null) {
                pse.close();
            }
            if (psf != null) {
                psf.close();
            }
            if (needclose && con != null) {
                con.close();
            }
        }
    }
}

