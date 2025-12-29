/*
 * Decompiled with CFR 0.152.
 */
package Server.login;

import Plugin.provider.MapleData;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataTool;
import Server.login.JobType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tools.Triple;

public class LoginInformationProvider {
    private static LoginInformationProvider instance;
    protected final List<String> ForbiddenName = new ArrayList<String>();
    protected final List<String> Curse = new ArrayList<String>();
    protected final Map<Triple<Integer, Integer, Integer>, List<Integer>> makeCharInfo = new HashMap<Triple<Integer, Integer, Integer>, List<Integer>>();

    protected LoginInformationProvider() {
        MapleDataProvider prov = MapleDataProviderFactory.getEtc();
        MapleData nameData = prov.getData("ForbiddenName.img");
        for (MapleData data : nameData.getChildren()) {
            this.ForbiddenName.add(MapleDataTool.getString(data));
        }
        nameData = prov.getData("Curse.img");
        for (MapleData data : nameData.getChildren()) {
            this.Curse.add(MapleDataTool.getString(data).split(",")[0]);
            this.ForbiddenName.add(MapleDataTool.getString(data).split(",")[0]);
        }
        MapleData infoData = prov.getData("MakeCharInfo.img");
        for (MapleData dat : infoData) {
            if (!dat.getName().matches("^\\d+$") && !dat.getName().equals("000_1") && !dat.getName().equals("000_3")) continue;
            int type = dat.getName().equals("000_1") ? JobType.冒險家.type : (dat.getName().equals("000_3") ? JobType.開拓者.type : Integer.parseInt(dat.getName()));
            for (MapleData d : dat) {
                int gender;
                if (d.getName().startsWith("male")) {
                    gender = 0;
                } else {
                    if (!d.getName().startsWith("female")) continue;
                    gender = 1;
                }
                for (MapleData da : d) {
                    Triple<Integer, Integer, Integer> key = new Triple<Integer, Integer, Integer>(gender, Integer.parseInt(da.getName()), type);
                    List our = this.makeCharInfo.computeIfAbsent(key, k -> new ArrayList());
                    for (MapleData dd : da) {
                        if (dd.getName().equalsIgnoreCase("color")) {
                            for (MapleData dda : dd.getChildren()) {
                                for (MapleData ddd : dda.getChildren()) {
                                    our.add(MapleDataTool.getInt(ddd, -1));
                                }
                            }
                            continue;
                        }
                        if (dd.getName().equals("name")) continue;
                        our.add(MapleDataTool.getInt(dd, -1));
                    }
                }
            }
        }
    }

    public static LoginInformationProvider getInstance() {
        if (instance == null) {
            instance = new LoginInformationProvider();
        }
        return instance;
    }

    public boolean isForbiddenName(String in) {
        for (String name : this.ForbiddenName) {
            if (!in.toLowerCase().contains(name.toLowerCase())) continue;
            return true;
        }
        return false;
    }

    public boolean isCurseMsg(String in) {
        for (String name : this.Curse) {
            if (!in.toLowerCase().contains(name.toLowerCase())) continue;
            return true;
        }
        return false;
    }

    public boolean isEligibleItem(int gender, int val, int job, int item) {
        if (item < 0) {
            return false;
        }
        Triple<Integer, Integer, Integer> key = new Triple<Integer, Integer, Integer>(gender, val, job);
        List<Integer> our = this.makeCharInfo.get(key);
        return our != null && our.contains(item);
    }
}

