/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.market.MarketEngine$ItemEntry
 */
package Net.server.market;

import Client.MapleCharacter;
import Database.DatabaseLoader;
import Net.server.market.MarketEngine;
import Server.channel.ChannelServer;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MarketEngine {


    private final List<ItemEntry> items = new LinkedList<ItemEntry>();
    private final Map<Integer, String> names = new LinkedHashMap<Integer, String>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addItem(int itemId, int quantity, int price, int charid) {
        List<ItemEntry> list = this.items;
        synchronized (list) {
            for (ItemEntry ie : this.items) {
                if (ie.getId() != itemId || ie.getOwner() != charid || ie.getPrice() != price) continue;
                ie.setQuantity(ie.getQuantity() + quantity);
                return;
            }
        }
        ItemEntry ie = new ItemEntry();
        ie.setId(itemId);
        ie.setQuantity(quantity);
        ie.setOwner(charid);
        ie.setPrice(price);
        List<ItemEntry> list2 = this.items;
        synchronized (list2) {
            this.items.add(ie);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeItem(int itemId, int quantity, int charid) {
        List<ItemEntry> list = this.items;
        synchronized (list) {
            for (int i = 0; i < this.items.size(); ++i) {
                ItemEntry ie = this.items.get(i);
                if (ie.getOwner() != charid || ie.getId() != itemId || ie.getQuantity() < quantity) continue;
                if (ie.getQuantity() == quantity) {
                    this.items.remove(ie);
                    continue;
                }
                ie.setQuantity(ie.getQuantity() - quantity);
            }
        }
    }

    public ItemEntry getItem(int position) {
        return this.items.get(position);
    }

    public List<ItemEntry> getItems() {
        return this.items;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public String getCharacterName(int charId) {
        if (this.names.get(charId) != null) {
            return this.names.get(charId);
        }
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            for (MapleCharacter mc : cs.getPlayerStorage().getAllCharacters()) {
                if (mc.getId() != charId) continue;
                this.names.put(charId, mc.getName());
                return mc.getName();
            }
        }
        try (DruidPooledConnection con = DatabaseLoader.DatabaseConnectionEx.getInstance().getConnection();){
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
            ps.setInt(1, charId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return "No user";
            String name = rs.getString("name");
            this.names.put(charId, name);
            String string = name;
            return string;
        }
        catch (SQLException e) {
            return "SQL Error fixmepl0x";
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        String ret = "";
        List<ItemEntry> list = this.items;
        synchronized (list) {
            for (ItemEntry ie : this.items) {
                ret = (String)ret + "#v" + ie.getId() + "# 價格: #b" + ie.getPrice() + "#k賣家: #b" + this.getCharacterName(ie.getOwner()) + "#k\\r\\n";
            }
        }
        return ret;
    }

    public static class ItemEntry {

        private int quantity;
        private int id;
        private int price;
        private int owner;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getOwner() {
            return owner;
        }

        public void setOwner(int owner) {
            this.owner = owner;
        }
    }
}

