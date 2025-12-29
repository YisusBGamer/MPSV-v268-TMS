/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package Plugin.provider.loaders;

import Config.configs.Config;
import Config.constants.ServerConstants;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataDirectoryEntry;
import Plugin.provider.MapleDataFileEntry;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.loaders.msData.SkillStringInfo;
import SwordieX.util.Util;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringData {
    private static final Logger log = LoggerFactory.getLogger(StringData.class);
    protected static final List<Integer> npcTrunks = new LinkedList<Integer>();
    private static MapleDataProvider datasource;
    public static Map<Integer, SkillStringInfo> skillString;
    public static Map<Integer, String> mobStrings;
    public static Map<Integer, String> npcStrings;
    public static Map<Integer, String> itemStrings;
    public static Map<Integer, String> questStrings;
    public static Map<Integer, String> skillStrings;
    public static Map<Integer, String> mapStrings;
    private static MapleDataProvider questDatasource;

    public static Map<Integer, String> getItemStrings() {
        return itemStrings;
    }

    public static Map<Integer, String> getMapStrings() {
        return mapStrings;
    }

    public static Map<Integer, String> getMobStrings() {
        return mobStrings;
    }

    public static Map<Integer, String> getNpcStrings() {
        return npcStrings;
    }

    public static Map<Integer, String> getQuestStrings() {
        return questStrings;
    }

    public static void loadItemStringsFromWz() {
        String[] files;
        log.info("Started loading item strings from wz.");
        long start = System.currentTimeMillis();
        String wzDir = ServerConstants.WZ_DIR + "/String.wz/";
        for (String fileDir : files = new String[]{"Cash", "Consume", "Eqp", "Ins", "Pet", "Etc"}) {
            for (MapleData topNode : datasource.getData(fileDir + ".img")) {
                if (!fileDir.equalsIgnoreCase("eqp") && !fileDir.equalsIgnoreCase("etc")) {
                    int id = Integer.parseInt(topNode.getName());
                    MapleData nameNode = topNode.getChildByPath("name");
                    if (nameNode == null) continue;
                    String name = "";
                    if (nameNode.getData() != null) {
                        name = nameNode.getData().toString();
                    }
                    itemStrings.put(id, name);
                    continue;
                }
                if (fileDir.equalsIgnoreCase("etc")) {
                    for (MapleData category : topNode.getChildren()) {
                        int id = 0;
                        if (category.getChildByPath("name") == null) continue;
                        String name = category.getChildByPath("name").getData().toString();
                        itemStrings.put(id, name);
                    }
                    continue;
                }
                for (MapleData n : topNode.getChildren()) {
                    for (MapleData category : n.getChildren()) {
                        int id = Integer.parseInt(category.getName());
                        MapleData nameNode = category.getChildByPath("name");
                        if (nameNode == null) continue;
                        String name = nameNode.getData().toString();
                        itemStrings.put(id, name);
                    }
                }
            }
        }
        log.info(String.format("Loaded item strings from wz in %dms.", System.currentTimeMillis() - start));
    }

    public static void loadSkillStringsFromWz() {
        log.info("Started loading skill strings from wz.");
        long start = System.currentTimeMillis();
        for (MapleData mainNode : datasource.getData("Skill.img")) {
            MapleData hNode;
            MapleData descNode;
            MapleData bookNameNode = mainNode.getChildByPath("bookName");
            if (bookNameNode != null) continue;
            SkillStringInfo ssi = new SkillStringInfo();
            MapleData nameNode = mainNode.getChildByPath("name");
            if (nameNode != null) {
                if (nameNode.getData() == null) {
                    ssi.setName("");
                } else {
                    ssi.setName(nameNode.getData().toString());
                }
            }
            if ((descNode = mainNode.getChildByPath("desc")) != null) {
                if (descNode.getData() == null) {
                    ssi.setDesc("");
                } else {
                    ssi.setDesc(descNode.getData().toString());
                }
            }
            if ((hNode = mainNode.getChildByPath("h")) != null) {
                if (hNode.getData() == null) {
                    ssi.setH("");
                } else {
                    ssi.setH(hNode.getData().toString());
                }
            } else {
                MapleData h1Node = mainNode.getChildByPath("h1");
                if (h1Node != null) {
                    if (h1Node.getData() == null) {
                        ssi.setH("");
                    } else {
                        ssi.setH(h1Node.getData().toString());
                    }
                }
            }
            skillString.put(Integer.parseInt(mainNode.getName()), ssi);
        }
        log.info(String.format("Loaded skill strings in %dms.", System.currentTimeMillis() - start));
    }

    public static void loadMobStringsFromWz() {
        log.info("Started loading mob strings from wz.");
        long start = System.currentTimeMillis();
        for (MapleData mainNode : datasource.getData("Mob.img")) {
            int id = Integer.parseInt(mainNode.getName());
            for (MapleData infoNode : mainNode.getChildren()) {
                String name = infoNode.getName();
                String value = "";
                if (infoNode.getData() != null) {
                    value = infoNode.getData().toString();
                }
                switch (name) {
                    case "name": {
                        StringData.getMobStrings().put(id, value);
                    }
                }
            }
        }
        log.info(String.format("Loaded mob strings in %dms.", System.currentTimeMillis() - start));
    }

    public static void loadNpcStringsFromWz() {
        log.info("Started loading npc strings from wz.");
        long start = System.currentTimeMillis();
        for (MapleData mainNode : datasource.getData("Npc.img")) {
            int id = Integer.parseInt(mainNode.getName());
            for (MapleData infoNode : mainNode.getChildren()) {
                String name = infoNode.getName();
                String value = "";
                if (infoNode.getData() != null) {
                    value = infoNode.getData().toString();
                }
                switch (name) {
                    case "name": {
                        StringData.getNpcStrings().put(id, value);
                    }
                }
            }
        }
        log.info(String.format("Loaded npc strings in %dms.", System.currentTimeMillis() - start));
    }

    public static void loadMapStringsFromWz() {
        log.info("Started loading quest strings from wz.");
        long start = System.currentTimeMillis();
        for (MapleData areaNode : datasource.getData("Map.img")) {
            for (MapleData mainNode : areaNode.getChildren()) {
                int id = Integer.parseInt(mainNode.getName());
                String mapName = "Unk";
                String streetName = "Unk";
                for (MapleData infoNode : mainNode.getChildren()) {
                    String name = infoNode.getName();
                    String value = "";
                    if (infoNode.getData() != null) {
                        value = infoNode.getData().toString();
                    }
                    switch (name) {
                        case "mapName": {
                            mapName = value;
                            break;
                        }
                        case "streetName": {
                            streetName = value;
                        }
                    }
                }
                StringData.getMapStrings().put(id, String.format("%s : %s", streetName, mapName));
            }
        }
        log.info(String.format("Loaded map strings in %dms.", System.currentTimeMillis() - start));
    }

    private static void loadQuestStringsFromWz() {
        log.info("Started loading quest strings from wz.");
        long start = System.currentTimeMillis();
        for (MapleDataDirectoryEntry topDir : questDatasource.getRoot().getSubdirectories()) {
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                try {
                    int id;
                    MapleData mainNode = questDatasource.getData(topDir.getName() + "/" + iFile.getName());
                    String nodeName = mainNode.getName().replace(".img", "");
                    try {
                        id = Integer.parseInt(nodeName);
                    }
                    catch (NumberFormatException e) {
                        log.warn("Skipping file with non-numeric name: " + nodeName);
                        continue;
                    }
                    String questName = "";
                    MapleData qiNode = mainNode.getChildByPath("QuestData");
                    if (qiNode != null) {
                        for (MapleData infoNode : qiNode.getChildren()) {
                            String name = infoNode.getName();
                            String value = "";
                            if (infoNode.getData() != null) {
                                value = infoNode.getData().toString();
                            }
                            if (!"name".equals(name)) continue;
                            questName = value;
                        }
                    }
                    StringData.getQuestStrings().put(id, String.format("%s", questName));
                }
                catch (Exception e) {
                    log.error("Error processing file: " + iFile.getName(), e);
                }
            }
        }
        log.info(String.format("Loaded quest strings in %dms.", System.currentTimeMillis() - start));
    }

    public static Map<Integer, SkillStringInfo> getSkillString() {
        return skillString;
    }

    public static void generateDatFiles() {
        log.info("Started generating string data.");
        long start = System.currentTimeMillis();
        datasource = MapleDataProviderFactory.getString();
        questDatasource = MapleDataProviderFactory.getQuest();
        StringData.loadSkillStringsFromWz();
        StringData.loadItemStringsFromWz();
        StringData.loadMobStringsFromWz();
        StringData.loadNpcStringsFromWz();
        StringData.loadMapStringsFromWz();
        StringData.loadQuestStringsFromWz();
        StringData.saveSkillStrings(ServerConstants.DAT_DIR + "/strings");
        StringData.saveItemStrings(ServerConstants.DAT_DIR + "/strings");
        StringData.saveMobStrings(ServerConstants.DAT_DIR + "/strings");
        StringData.saveNpcStrings(ServerConstants.DAT_DIR + "/strings");
        StringData.saveMapStrings(ServerConstants.DAT_DIR + "/strings");
        StringData.saveQuestStrings(ServerConstants.DAT_DIR + "/strings");
        log.info(String.format("Completed generating string data in %dms.", System.currentTimeMillis() - start));
    }

    private static void saveQuestStrings(String dir) {
        Util.makeDirIfAbsent(dir);
        File file = new File(dir + "/quests.dat");
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));){
            dataOutputStream.writeInt(questStrings.size());
            for (Map.Entry<Integer, String> entry : questStrings.entrySet()) {
                int id = entry.getKey();
                String name = entry.getValue();
                dataOutputStream.writeInt(id);
                dataOutputStream.writeUTF(name);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveSkillStrings(String dir) {
        Util.makeDirIfAbsent(dir);
        File file = new File(dir + "/skills.dat");
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));){
            dataOutputStream.writeInt(StringData.getSkillString().size());
            for (Map.Entry<Integer, SkillStringInfo> entry : StringData.getSkillString().entrySet()) {
                int id = entry.getKey();
                SkillStringInfo ssi = entry.getValue();
                dataOutputStream.writeInt(id);
                dataOutputStream.writeUTF(ssi.getName() == null ? "" : ssi.getName());
                dataOutputStream.writeUTF(ssi.getDesc());
                dataOutputStream.writeUTF(ssi.getH());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadSkillStrings() {
        long start = System.currentTimeMillis();
        File file = new File(ServerConstants.DAT_DIR + "/strings/skills.dat");
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));){
            int size = dataInputStream.readInt();
            for (int i = 0; i < size; ++i) {
                int id = dataInputStream.readInt();
                SkillStringInfo ssi = new SkillStringInfo();
                ssi.setName(dataInputStream.readUTF());
                ssi.setDesc(dataInputStream.readUTF());
                ssi.setH(dataInputStream.readUTF());
                StringData.getSkillString().put(id, ssi);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        log.info(String.format("Loaded skill strings from data file in %dms.", System.currentTimeMillis() - start));
    }

    private static void saveItemStrings(String dir) {
        Util.makeDirIfAbsent(dir);
        File file = new File(dir + "/items.dat");
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));){
            dataOutputStream.writeInt(itemStrings.size());
            for (Map.Entry<Integer, String> entry : itemStrings.entrySet()) {
                int id = entry.getKey();
                String ssi = entry.getValue();
                dataOutputStream.writeInt(id);
                dataOutputStream.writeUTF(ssi);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadItemStrings() {
        long start = System.currentTimeMillis();
        File file = new File(ServerConstants.DAT_DIR + "/strings/items.dat");
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));){
            int size = dataInputStream.readInt();
            for (int i = 0; i < size; ++i) {
                int id = dataInputStream.readInt();
                String name = dataInputStream.readUTF();
                itemStrings.put(id, name);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        log.info(String.format("Loaded item strings from data file in %dms.", System.currentTimeMillis() - start));
    }

    public static void loadQuestStrings() {
        long start = System.currentTimeMillis();
        File file = new File(ServerConstants.DAT_DIR + "/strings/quests.dat");
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));){
            int size = dataInputStream.readInt();
            for (int i = 0; i < size; ++i) {
                int id = dataInputStream.readInt();
                String name = dataInputStream.readUTF();
                questStrings.put(id, name);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        log.info(String.format("Loaded quest strings from data file in %dms.", System.currentTimeMillis() - start));
    }

    private static void saveMobStrings(String dir) {
        Util.makeDirIfAbsent(dir);
        File file = new File(dir + "/mobs.dat");
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));){
            dataOutputStream.writeInt(StringData.getMobStrings().size());
            for (Map.Entry<Integer, String> entry : StringData.getMobStrings().entrySet()) {
                int id = entry.getKey();
                String name = entry.getValue();
                dataOutputStream.writeInt(id);
                dataOutputStream.writeUTF(name);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadMobStrings() {
        long start = System.currentTimeMillis();
        File file = new File(ServerConstants.DAT_DIR + "/strings/mobs.dat");
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));){
            int size = dataInputStream.readInt();
            for (int i = 0; i < size; ++i) {
                int id = dataInputStream.readInt();
                String name = dataInputStream.readUTF();
                StringData.getMobStrings().put(id, name);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        log.info(String.format("Loaded mob strings from data file in %dms.", System.currentTimeMillis() - start));
    }

    private static void saveNpcStrings(String dir) {
        Util.makeDirIfAbsent(dir);
        File file = new File(dir + "/npcs.dat");
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));){
            dataOutputStream.writeInt(StringData.getNpcStrings().size());
            for (Map.Entry<Integer, String> entry : StringData.getNpcStrings().entrySet()) {
                int id = entry.getKey();
                String name = entry.getValue();
                dataOutputStream.writeInt(id);
                dataOutputStream.writeUTF(name);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadNpcStrings() {
        long start = System.currentTimeMillis();
        File file = new File(ServerConstants.DAT_DIR + "/strings/npcs.dat");
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));){
            int size = dataInputStream.readInt();
            for (int i = 0; i < size; ++i) {
                int id = dataInputStream.readInt();
                String name = dataInputStream.readUTF();
                StringData.getNpcStrings().put(id, name);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        log.info(String.format("Loaded npc strings from data file in %dms.", System.currentTimeMillis() - start));
    }

    private static void saveMapStrings(String dir) {
        Util.makeDirIfAbsent(dir);
        File file = new File(dir + "/maps.dat");
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));){
            dataOutputStream.writeInt(StringData.getMapStrings().size());
            for (Map.Entry<Integer, String> entry : StringData.getMapStrings().entrySet()) {
                int id = entry.getKey();
                String name = entry.getValue();
                dataOutputStream.writeInt(id);
                dataOutputStream.writeUTF(name);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadMapStrings() {
        long start = System.currentTimeMillis();
        File file = new File(ServerConstants.DAT_DIR + "/strings/maps.dat");
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));){
            int size = dataInputStream.readInt();
            for (int i = 0; i < size; ++i) {
                int id = dataInputStream.readInt();
                String name = dataInputStream.readUTF();
                StringData.getMapStrings().put(id, name);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        log.info(String.format("Loaded item strings from data file in %dms.", System.currentTimeMillis() - start));
    }

    public static void main(String[] args) {
        Config.load();
        MapleDataProviderFactory.init();
        StringData.generateDatFiles();
    }

    public static SkillStringInfo getSkillStringById(int id) {
        return StringData.getSkillString().getOrDefault(id, null);
    }

    public static String getItemStringById(int id) {
        return StringData.getItemStrings().getOrDefault(id, null);
    }

    public static String getMobStringById(int id) {
        return StringData.getMobStrings().getOrDefault(id, null);
    }

    public static String getNpcStringById(int id) {
        return StringData.getNpcStrings().getOrDefault(id, null);
    }

    public static String getMapStringById(int id) {
        return StringData.getMapStrings().getOrDefault(id, null);
    }

    public static String getQuestStringById(int id) {
        return StringData.getQuestStrings().getOrDefault(id, null);
    }

    public static Map<Integer, String> getItemStringByName(String query) {
        query = query.toLowerCase();
        HashMap<Integer, String> res = new HashMap<Integer, String>();
        for (Map.Entry<Integer, String> entry : itemStrings.entrySet()) {
            String ssName;
            int id = entry.getKey();
            String name = entry.getValue();
            if (name == null || !(ssName = name.toLowerCase()).contains(query)) continue;
            res.put(id, name);
        }
        return res;
    }

    public static Map<Integer, String> getQuestStringByName(String query) {
        query = query.toLowerCase();
        HashMap<Integer, String> res = new HashMap<Integer, String>();
        for (Map.Entry<Integer, String> entry : questStrings.entrySet()) {
            String ssName;
            int id = entry.getKey();
            String name = entry.getValue();
            if (name == null || !(ssName = name.toLowerCase()).contains(query)) continue;
            res.put(id, name);
        }
        return res;
    }

    public static Map<Integer, SkillStringInfo> getSkillStringByName(String query) {
        HashMap<Integer, SkillStringInfo> res = new HashMap<Integer, SkillStringInfo>();
        for (Map.Entry<Integer, SkillStringInfo> entry : StringData.getSkillString().entrySet()) {
            String ssName;
            int id = entry.getKey();
            SkillStringInfo ssi = entry.getValue();
            if (ssi.getName() == null || !(ssName = ssi.getName().toLowerCase()).contains(query)) continue;
            res.put(id, ssi);
        }
        return res;
    }

    public static Map<Integer, String> getMobStringByName(String query) {
        query = query.toLowerCase();
        HashMap<Integer, String> res = new HashMap<Integer, String>();
        for (Map.Entry<Integer, String> entry : StringData.getMobStrings().entrySet()) {
            String ssName;
            int id = entry.getKey();
            String name = entry.getValue();
            if (name == null || !(ssName = name.toLowerCase()).contains(query)) continue;
            res.put(id, name);
        }
        return res;
    }

    public static Map<Integer, String> getNpcStringByName(String query) {
        query = query.toLowerCase();
        HashMap<Integer, String> res = new HashMap<Integer, String>();
        for (Map.Entry<Integer, String> entry : StringData.getNpcStrings().entrySet()) {
            String ssName;
            int id = entry.getKey();
            String name = entry.getValue();
            if (name == null || !(ssName = name.toLowerCase()).contains(query)) continue;
            res.put(id, name);
        }
        return res;
    }

    public static Map<Integer, String> getMapStringByName(String query) {
        query = query.toLowerCase();
        HashMap<Integer, String> res = new HashMap<Integer, String>();
        for (Map.Entry<Integer, String> entry : StringData.getMapStrings().entrySet()) {
            String ssName;
            int id = entry.getKey();
            String name = entry.getValue();
            if (name == null || !(ssName = name.toLowerCase()).contains(query)) continue;
            res.put(id, name);
        }
        return res;
    }

    public static void clear() {
        StringData.getSkillString().clear();
        StringData.getItemStrings().clear();
        StringData.getMobStrings().clear();
        StringData.getNpcStrings().clear();
        StringData.getMapStrings().clear();
    }

    public static void load() {
        StringData.loadItemStrings();
        StringData.loadSkillStrings();
        StringData.loadMobStrings();
        StringData.loadNpcStrings();
        StringData.loadMapStrings();
        StringData.loadQuestStrings();
    }

    public static void generateTextFiles() {
        PrintWriter pw;
        String fileName;
        StringData.load();
        StringBuilder sb = new StringBuilder();
        TreeMap<Integer, SkillStringInfo> sortedSkillTree = new TreeMap<Integer, SkillStringInfo>(Comparator.comparingInt(Integer::intValue));
        ArrayList<Map<Integer, String>> mapList = new ArrayList<Map<Integer, String>>();
        mapList.add(StringData.getMobStrings());
        mapList.add(StringData.getNpcStrings());
        mapList.add(StringData.getMapStrings());
        String[] names = new String[]{"Mob", "Npc", "Map"};
        sortedSkillTree.putAll(StringData.getSkillString());
        for (Map.Entry entry : sortedSkillTree.entrySet()) {
            sb.append(entry.getKey()).append(" - ").append(((SkillStringInfo)entry.getValue()).getName()).append("\r\n");
        }
        try (PrintWriter pw2 = new PrintWriter(new FileWriter(new File(ServerConstants.RESOURCES_DIR + "/Skill.txt")));){
            pw2.println(sb);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        TreeMap<Integer, String> sortedTree = new TreeMap<>(Comparator.comparingInt(Integer::intValue));
        int i = 0;
        for (Map map : mapList) {
            sb = new StringBuilder();
            sortedTree.clear();
            sortedTree.putAll(map);
            fileName = names[i++] + ".txt";
            for (Map.Entry entry : sortedTree.entrySet()) {
                sb.append(entry.getKey()).append(" - ").append((String)entry.getValue()).append("\r\n");
            }
            try {
                pw = new PrintWriter(new FileWriter(new File(ServerConstants.RESOURCES_DIR + "/" + fileName)));
                try {
                    pw.println(sb);
                }
                finally {
                    pw.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        names = new String[]{"Eqp", "Use", "Ins", "Etc", "Cash"};
        mapList.clear();
        for (int j = 0; j < names.length; ++j) {
            mapList.add(new TreeMap(Comparator.comparingInt(Integer::intValue)));
        }
        for (Map.Entry<Integer, String> entry : itemStrings.entrySet()) {
            ((Map)mapList.get(Math.max(0, entry.getKey() / 1000000 - 1))).put(entry.getKey(), entry.getValue());
        }
        i = 0;
        for (Map<Integer, String> map : mapList) {
            sb = new StringBuilder();
            fileName = names[i++] + ".txt";
            for (Map.Entry entry : map.entrySet()) {
                sb.append(entry.getKey()).append(" - ").append((String)entry.getValue()).append("\r\n");
            }
            try {
                pw = new PrintWriter(new FileWriter(new File(ServerConstants.RESOURCES_DIR + "/" + fileName)));
                try {
                    pw.println(sb);
                }
                finally {
                    pw.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Generated
    public static List<Integer> getNpcTrunks() {
        return npcTrunks;
    }

    @Generated
    public static MapleDataProvider getDatasource() {
        return datasource;
    }

    @Generated
    public static Map<Integer, String> getSkillStrings() {
        return skillStrings;
    }

    static {
        skillString = new HashMap<Integer, SkillStringInfo>();
        mobStrings = new HashMap<Integer, String>();
        npcStrings = new HashMap<Integer, String>();
        itemStrings = new HashMap<Integer, String>();
        questStrings = new HashMap<Integer, String>();
        skillStrings = new HashMap<Integer, String>();
        mapStrings = new HashMap<Integer, String>();
    }
}

