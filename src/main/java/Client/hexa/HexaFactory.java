/*
 * Decompiled with CFR 0.152.
 */
package Client.hexa;

import Client.hexa.HexaSkillCoreEntry;
import Client.hexa.HexaStatCoreEntry;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataTool;
import java.util.HashMap;
import java.util.Map;

public class HexaFactory {
    private static final MapleData hexaCoreData = MapleDataProviderFactory.getEtc().getData("HexaCore.img");
    private static final Map<Integer, HexaSkillCoreEntry> hexaSkillList = new HashMap<Integer, HexaSkillCoreEntry>();
    private static final Map<Integer, HexaStatCoreEntry> hexaMainStatList = new HashMap<Integer, HexaStatCoreEntry>();
    private static final Map<Integer, HexaStatCoreEntry> hexaAdditionalStatList = new HashMap<Integer, HexaStatCoreEntry>();

    public static void loadAllHexaSkill() {
        MapleData hexaSkillData = hexaCoreData.getChildByPath("hexaSkill");
        if (hexaSkillData != null) {
            for (MapleData c : hexaSkillData.getChildByPath("coreData")) {
                hexaSkillList.put(Integer.parseInt(c.getName()), new HexaSkillCoreEntry(Integer.parseInt(c.getName()), MapleDataTool.getInt("0", c.getChildByPath("connectSkill"), 0), MapleDataTool.getInt("type", c, 0), MapleDataTool.getInt("maxLevel", c, 0)));
            }
        } else {
            System.out.println("載入hexaSkill失敗");
        }
        MapleData hexaStatData = hexaCoreData.getChildByPath("hexaStat");
        if (hexaStatData != null) {
            Object stat;
            HashMap<Integer, Integer> levelData;
            MapleData main = hexaStatData.getChildByPath("stat").getChildByPath("main");
            MapleData additional = hexaStatData.getChildByPath("stat").getChildByPath("additional");
            int maxLevel = MapleDataTool.getInt("maxLevel", main, 0);
            for (MapleData c : main.getChildByPath("type")) {
                levelData = new HashMap<Integer, Integer>();
                for (MapleData level : c.getChildByPath("level")) {
                    levelData.put(Integer.parseInt(level.getName()), MapleDataTool.getInt(level.getChildren().get(0), 0));
                }
                stat = c.getChildByPath("level").getChildByPath("0").getChildren().get(0).getName();
                hexaMainStatList.put(Integer.parseInt(c.getName()), new HexaStatCoreEntry(Integer.parseInt(c.getName()), maxLevel, (String)stat, levelData));
            }
            maxLevel = MapleDataTool.getInt("maxLevel", additional, 0);
            for (MapleData c : additional.getChildByPath("type")) {
                levelData = new HashMap();
                for (MapleData level : c.getChildByPath("level")) {
                    levelData.put(Integer.parseInt(level.getName()), MapleDataTool.getInt(level.getChildren().get(0), 0));
                }
                stat = c.getChildByPath("level").getChildByPath("0").getChildren().get(0).getName();
                hexaAdditionalStatList.put(Integer.parseInt(c.getName()), new HexaStatCoreEntry(Integer.parseInt(c.getName()), maxLevel, (String)stat, levelData));
            }
        } else {
            System.out.println("載入hexaSkill失敗");
        }
    }

    public static HexaSkillCoreEntry getHexaSkills(int id) {
        return hexaSkillList.get(id);
    }

    public static HexaStatCoreEntry getHexaMainStats(int type) {
        return hexaMainStatList.get(type);
    }

    public static HexaStatCoreEntry getAdditionalHexaStats(int type) {
        return hexaAdditionalStatList.get(type);
    }
}

