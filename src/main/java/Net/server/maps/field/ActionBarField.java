/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Config.constants.enums.ActionBarResultType
 */
package Net.server.maps.field;

import Client.MapleCharacter;
import Config.constants.enums.ActionBarResultType;
import Net.server.maps.MapleMap;
import Opcode.header.OutHeader;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataTool;
import java.util.HashMap;
import java.util.Map;
import tools.data.MaplePacketLittleEndianWriter;

public class ActionBarField
extends MapleMap {
    public static void init() {
        for (MapleData zj : MapleDataProviderFactory.getEtc().getData("ActionBar.img").getChildByPath("ActionBar")) {
            if ("info".equals(zj.getName())) continue;
            int b = MapleDataTool.getInt("info/fieldType", zj, 0);
            int int1 = Integer.parseInt(zj.getName());
            MapleFieldActionBar akq = new MapleFieldActionBar(int1, b);
            for (MapleData zj6 : zj) {
                if ("info".equals(zj6.getName())) continue;
                int int2 = Integer.parseInt(zj6.getName());
                akq.getSkills().put(int2, new AKR(int2, MapleDataTool.getInt("id", zj6, 0), MapleDataTool.getString("type", zj6, "event"), MapleDataTool.getInt("useOnce", zj6, 0) > 0, MapleDataTool.getInt("usableCount", zj6, -1)));
            }
            MapleFieldActionBar.infos.put(int1, akq);
        }
    }

    public ActionBarField(int mapid, int channel, int returnMapId, float monsterRate) {
        super(mapid, channel, returnMapId, monsterRate);
    }

    @Override
    public void userEnterField(MapleCharacter chr) {
        super.userEnterField(chr);
        MapleFieldActionBar bm = MapleFieldActionBar.createActionBar(22);
        if (bm != null) {
            chr.setActionBar(bm);
            MaplePacketLittleEndianWriter hh = new MaplePacketLittleEndianWriter();
            hh.writeOpcode(OutHeader.LP_ActionBarResult);
            hh.writeInt(ActionBarResultType.Create_Result.getValue());
            hh.writeInt(chr.getActionBar().pq);
            chr.send(hh.getPacket());
        }
    }

    @Override
    public void userLeaveField(MapleCharacter chr) {
        super.userLeaveField(chr);
        MaplePacketLittleEndianWriter hh = new MaplePacketLittleEndianWriter();
        hh.writeOpcode(OutHeader.LP_ActionBarResult);
        hh.writeInt(ActionBarResultType.Remove_Result.getValue());
        hh.writeInt(chr.getActionBar().pq);
        chr.send(hh.getPacket());
        chr.setActionBar(null);
    }

    public static final class MapleFieldActionBar {
        public static final Map<Integer, MapleFieldActionBar> infos = new HashMap<Integer, MapleFieldActionBar>();
        public final int pq;
        private final int amt;
        private final Map<Integer, AKR> skills = new HashMap<Integer, AKR>();

        public MapleFieldActionBar(int pq, int amt) {
            this.pq = pq;
            this.amt = amt;
        }

        public static MapleFieldActionBar createActionBar(int n) {
            MapleFieldActionBar res = null;
            MapleFieldActionBar actionBar = infos.get(n);
            if (actionBar != null) {
                MapleFieldActionBar akq4 = new MapleFieldActionBar(actionBar.pq, actionBar.amt);
                actionBar.skills.forEach((n2, akr) -> akq4.skills.put((Integer)n2, new AKR(akr.index, akr.id, akr.type, akr.useOnce, akr.usableCount)));
                res = akq4;
            }
            return res;
        }

        public Map<Integer, AKR> getSkills() {
            return this.skills;
        }
    }

    public static final class AKR {
        final int index;
        final int id;
        final String type;
        final boolean useOnce;
        int usableCount;

        public AKR(int index, int pq, String type, boolean amv, int amw) {
            this.index = index;
            this.id = pq;
            this.type = type;
            this.useOnce = amv;
            this.usableCount = amw;
        }
    }
}

