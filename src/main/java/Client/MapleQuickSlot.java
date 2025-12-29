/*
 * Decompiled with CFR 0.152.
 */
package Client;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import tools.Pair;
import tools.data.MaplePacketLittleEndianWriter;

public class MapleQuickSlot
implements Serializable {
    private static final long serialVersionUID = 9179541993413738569L;
    private final List<Pair<Integer, Integer>> quickslot;
    private boolean changed = false;

    public MapleQuickSlot() {
        this.quickslot = new ArrayList<Pair<Integer, Integer>>();
    }

    public MapleQuickSlot(List<Pair<Integer, Integer>> quickslots) {
        this.quickslot = quickslots;
    }

    public List<Pair<Integer, Integer>> Layout() {
        this.changed = true;
        return this.quickslot;
    }

    public void unchanged() {
        this.changed = false;
    }

    public void resetQuickSlot() {
        this.changed = true;
        this.quickslot.clear();
    }

    public void addQuickSlot(int index, int key) {
        this.changed = true;
        this.quickslot.add(new Pair<Integer, Integer>(index, key));
    }

    public int getKeyByIndex(int index) {
        for (Pair<Integer, Integer> p : this.quickslot) {
            if (p.getLeft() != index) continue;
            return p.getRight();
        }
        return -1;
    }

    public void writeData(MaplePacketLittleEndianWriter mplew) {
        mplew.write(this.quickslot.isEmpty() ? 0 : 1);
        if (this.quickslot.isEmpty()) {
            return;
        }
        this.quickslot.sort(Comparator.comparing(Pair::getLeft));
        for (Pair<Integer, Integer> qs : this.quickslot) {
            mplew.writeInt(qs.getRight());
        }
    }

    public void saveQuickSlots(Connection con, int charid) throws SQLException {
        if (!this.changed) {
            return;
        }
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM quickslot WHERE characterid = ?");){
            ps.setInt(1, charid);
            ps.execute();
            if (this.quickslot.isEmpty()) {
                return;
            }
            boolean first = true;
            StringBuilder query = new StringBuilder();
            for (Pair<Integer, Integer> q : this.quickslot) {
                if (first) {
                    first = false;
                    query.append("INSERT INTO quickslot VALUES (");
                } else {
                    query.append(",(");
                }
                query.append("DEFAULT,");
                query.append(charid).append(",");
                query.append(q.getLeft()).append(",");
                query.append(q.getRight()).append(")");
            }
            try (PreparedStatement pse = con.prepareStatement(query.toString());){
                pse.execute();
            }
        }
    }
}

