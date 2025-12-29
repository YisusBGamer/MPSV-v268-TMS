/*
 * Decompiled with CFR 0.152.
 */
package Net.server.maps;

import Net.server.maps.MapleReactorStats;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataTool;
import java.util.HashMap;
import java.util.Map;
import tools.Pair;
import tools.StringUtil;

public class MapleReactorFactory {
    private static final MapleDataProvider data = MapleDataProviderFactory.getReactor();
    private static final Map<Integer, MapleReactorStats> reactorStats = new HashMap<Integer, MapleReactorStats>();
    protected static final Map<Integer, String> actionNames = new HashMap<Integer, String>();

    public static MapleReactorStats getReactor(int rid) {
        MapleReactorStats stats = reactorStats.get(rid);
        if (stats == null) {
            int infoId = rid;
            MapleData reactorData = data.getData(StringUtil.getLeftPaddedStr(infoId + ".img", '0', 11));
            MapleData link = reactorData.getChildByPath("info/link");
            if (link != null) {
                infoId = MapleDataTool.getIntConvert("info/link", reactorData);
                stats = reactorStats.get(infoId);
            }
            if (stats == null) {
                MapleData reactorD;
                stats = new MapleReactorStats();
                reactorData = data.getData(StringUtil.getLeftPaddedStr(infoId + ".img", '0', 11));
                if (reactorData == null) {
                    return stats;
                }
                boolean canTouch = MapleDataTool.getInt("info/activateByTouch", reactorData, 0) > 0;
                boolean areaSet = false;
                boolean foundState = false;
                byte i = 0;
                while ((reactorD = reactorData.getChildByPath(String.valueOf(i))) != null) {
                    MapleData reactorInfoData_ = reactorD.getChildByPath("event");
                    if (reactorInfoData_ != null && reactorInfoData_.getChildByPath("0") != null) {
                        MapleData reactorInfoData = reactorInfoData_.getChildByPath("0");
                        Pair<Integer, Integer> reactItem = null;
                        int type = MapleDataTool.getIntConvert("type", reactorInfoData);
                        if (type == 100) {
                            reactItem = new Pair<Integer, Integer>(MapleDataTool.getIntConvert("0", reactorInfoData), MapleDataTool.getIntConvert("1", reactorInfoData, 1));
                            if (!areaSet) {
                                stats.setTL(MapleDataTool.getPoint("lt", reactorInfoData));
                                stats.setBR(MapleDataTool.getPoint("rb", reactorInfoData));
                                areaSet = true;
                            }
                        }
                        foundState = true;
                        stats.addState(i, type, reactItem, (byte)MapleDataTool.getIntConvert("state", reactorInfoData), MapleDataTool.getIntConvert("timeOut", reactorInfoData_, -1), (byte)(canTouch ? 2 : (MapleDataTool.getIntConvert("2", reactorInfoData, 0) > 0 || reactorInfoData.getChildByPath("clickArea") != null || type == 9 ? 1 : 0)));
                    } else {
                        stats.addState(i, 999, null, (byte)(foundState ? -1 : i + 1), 0, (byte)0);
                    }
                    i = (byte)(i + 1);
                }
                reactorStats.put(infoId, stats);
                if (rid != infoId) {
                    reactorStats.put(rid, stats);
                }
            } else {
                reactorStats.put(rid, stats);
            }
        }
        return stats;
    }

    public static String getAction(int rid) {
        if (!actionNames.containsKey(rid)) {
            String action = MapleDataTool.getString(data.getData(StringUtil.getLeftPaddedStr(rid + ".img", '0', 11)));
            actionNames.put(rid, action);
        }
        return actionNames.get(rid);
    }
}

