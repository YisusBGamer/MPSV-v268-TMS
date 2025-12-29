/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package Plugin.provider.loaders;

import Config.configs.Config;
import Config.constants.ServerConstants;
import Net.server.MapleStatInfo;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataDirectoryEntry;
import Plugin.provider.MapleDataFileEntry;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.MapleDataProviderFactory;
import SwordieX.client.character.skills.Skill;
import SwordieX.util.Util;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillData {
    private static final Logger log = LoggerFactory.getLogger(SkillData.class);
    private static MapleDataProvider datasource;
    private static Map<Integer, Skill> skills;

    /*
     * Could not resolve type clashes
     */
    public static void saveSkills(String dir) {
        Util.makeDirIfAbsent(dir);
        File file = new File(String.format("%s/skills.dat", dir));
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            return;
        }
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));){
            dataOutputStream.writeInt(skills.size());
            for (Skill skill : skills.values()) {
                dataOutputStream.writeInt(skill.getId());
                dataOutputStream.writeInt(skill.getMasterLevel());
                dataOutputStream.writeInt(skill.getFixLevel());
                dataOutputStream.writeInt(skill.getHyper());
                dataOutputStream.writeBoolean(skill.isPsd());
                dataOutputStream.writeBoolean(skill.isCanNotStealableSkill());
                dataOutputStream.writeBoolean(skill.isInvisible());
                dataOutputStream.writeBoolean(skill.isPetAutoBuff());
                dataOutputStream.writeBoolean(skill.isNotRemoved());
                dataOutputStream.writeUTF(skill.getElemAttr());
                dataOutputStream.writeInt(skill.getCommon().size());
                for (Map.Entry<MapleStatInfo, String> dat : skill.getCommon().entrySet()) {
                    dataOutputStream.writeUTF(dat.getKey().name());
                    dataOutputStream.writeUTF(dat.getValue());
                }
                dataOutputStream.writeInt(skill.getPVPcommon().size());
                for (Map.Entry<MapleStatInfo, String> dat : skill.getPVPcommon().entrySet()) {
                    dataOutputStream.writeUTF(dat.getKey().name());
                    dataOutputStream.writeUTF(dat.getValue());
                }
                dataOutputStream.writeInt(skill.getInfo().size());
                for (Map.Entry<String, String> dat : skill.getInfo().entrySet()) {
                    dataOutputStream.writeUTF((String)dat.getKey());
                    dataOutputStream.writeUTF(dat.getValue());
                }
                dataOutputStream.writeInt(skill.getInfo2().size());
                for (Map.Entry<String, String> dat : skill.getInfo2().entrySet()) {
                    dataOutputStream.writeUTF((String)dat.getKey());
                    dataOutputStream.writeUTF(dat.getValue());
                }
                dataOutputStream.writeInt(skill.getPsdSkills().size());
                Iterator<Integer> iterator = skill.getPsdSkills().iterator();
                while (iterator.hasNext()) {
                    int dat = (Integer)iterator.next();
                    dataOutputStream.writeInt(dat);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadSkill(File file) {
        if (!file.exists()) {
            SkillData.loadDatFromWz();
        }
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));){
            int maxcount = dataInputStream.readInt();
            for (int count = 0; count < maxcount; ++count) {
                int skillid = dataInputStream.readInt();
                Skill skill = new Skill(skillid);
                skill.setMasterLevel(dataInputStream.readInt());
                skill.setFixLevel(dataInputStream.readInt());
                skill.setHyper(dataInputStream.readInt());
                skill.setPsd(dataInputStream.readBoolean());
                skill.setCanNotStealableSkill(dataInputStream.readBoolean());
                skill.setInvisible(dataInputStream.readBoolean());
                skill.setPetAutoBuff(dataInputStream.readBoolean());
                skill.setNotRemoved(dataInputStream.readBoolean());
                skill.setElemAttr(dataInputStream.readUTF());
                int commonSize = dataInputStream.readInt();
                for (int j = 0; j < commonSize; ++j) {
                    String key = dataInputStream.readUTF();
                    String val = dataInputStream.readUTF();
                    try {
                        skill.getCommon().put(MapleStatInfo.valueOf(key), val);
                        continue;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                int commonPVPSize = dataInputStream.readInt();
                for (int j = 0; j < commonPVPSize; ++j) {
                    String key = dataInputStream.readUTF();
                    String val = dataInputStream.readUTF();
                    try {
                        skill.getPVPcommon().put(MapleStatInfo.valueOf(key), val);
                        continue;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                int infoSize = dataInputStream.readInt();
                for (int j = 0; j < infoSize; ++j) {
                    String key = dataInputStream.readUTF();
                    String val = dataInputStream.readUTF();
                    try {
                        skill.getInfo().put(key, val);
                        continue;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                int info2Size = dataInputStream.readInt();
                for (int j = 0; j < info2Size; ++j) {
                    String key = dataInputStream.readUTF();
                    String val = dataInputStream.readUTF();
                    try {
                        skill.getInfo2().put(key, val);
                        continue;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                int psdSkillsSize = dataInputStream.readInt();
                for (int j = 0; j < psdSkillsSize; ++j) {
                    int key = dataInputStream.readInt();
                    try {
                        skill.getPsdSkills().add(key);
                        continue;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                skills.put(skillid, skill);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadSkillsFromWz() {
        HashMap<String, String> ss = new HashMap<String, String>();
        HashMap<String, String> ii = new HashMap<String, String>();
        HashMap<String, String> ii2 = new HashMap<String, String>();
        HashMap ww = new HashMap();
        MapleDataDirectoryEntry root = datasource.getRoot();
        for (MapleDataFileEntry topDir : root.getFiles()) {
            if (topDir.getName().contains("Dragon") || topDir.getName().contains("MobSkill") || topDir.getName().contains("AbyssExpedition") || topDir.getName().contains("Roguelike") || topDir.getName().length() > 10) continue;
            for (MapleData data : datasource.getData(topDir.getName())) {
                if (!data.getName().equals("skill")) continue;
                for (MapleData skillData : data.getChildren()) {
                    if (skillData == null) continue;
                    int skillid = Integer.parseInt(skillData.getName());
                    Skill oSkill = new Skill(skillid);
                    block70: for (MapleData skill : skillData.getChildren()) {
                        switch (skill.getName()) {
                            case "PVPcommon": {
                                for (MapleData dat : skill.getChildren()) {
                                    if (!ss.containsKey(dat.getName())) {
                                        ss.put(dat.getName(), "");
                                    }
                                    try {
                                        oSkill.getPVPcommon().put(MapleStatInfo.valueOf(dat.getName()), dat.getData().toString());
                                    }
                                    catch (Exception exception) {}
                                }
                                continue block70;
                            }
                            case "common": {
                                for (MapleData dat : skill.getChildren()) {
                                    if (!ss.containsKey(dat.getName())) {
                                        ss.put(dat.getName(), "");
                                    }
                                    try {
                                        oSkill.getCommon().put(MapleStatInfo.valueOf(dat.getName()), dat.getData().toString());
                                    }
                                    catch (Exception exception) {}
                                }
                                continue block70;
                            }
                            case "info": {
                                for (MapleData dat : skill.getChildren()) {
                                    if (!ii.containsKey(dat.getName())) {
                                        ii.put(dat.getName(), "");
                                    }
                                    try {
                                        oSkill.getInfo().put(dat.getName(), dat.getData().toString());
                                    }
                                    catch (Exception exception) {}
                                }
                                continue block70;
                            }
                            case "info2": {
                                for (MapleData dat : skill.getChildren()) {
                                    if (!ii2.containsKey(dat.getName())) {
                                        ii2.put(dat.getName(), "");
                                    }
                                    try {
                                        oSkill.getInfo2().put(dat.getName(), dat.getData().toString());
                                    }
                                    catch (Exception exception) {}
                                }
                                continue block70;
                            }
                            case "psd": {
                                oSkill.setPsd(!skill.getData().equals("0"));
                                break;
                            }
                            case "psdSkill": {
                                for (MapleData dat : skill.getChildren()) {
                                    try {
                                        oSkill.getPsdSkills().add(Integer.parseInt(dat.getName()));
                                    }
                                    catch (Exception exception) {}
                                }
                                continue block70;
                            }
                            case "invisible": {
                                oSkill.setInvisible(!skill.getData().equals("0"));
                                break;
                            }
                            case "masterLevel": {
                                oSkill.setMasterLevel(Integer.parseInt(skill.getData().toString()));
                                break;
                            }
                            case "fixLevel": {
                                oSkill.setFixLevel(Integer.parseInt(skill.getData().toString()));
                                break;
                            }
                            case "notRemoved": {
                                oSkill.setNotRemoved(!skill.getData().equals("0"));
                                break;
                            }
                            case "canNotStealableSkill": {
                                oSkill.setCanNotStealableSkill(!skill.getData().equals("0"));
                                break;
                            }
                            case "isPetAutoBuff": {
                                oSkill.setPetAutoBuff(!skill.getData().equals("0"));
                                break;
                            }
                            case "hyper": {
                                oSkill.setHyper(Integer.parseInt(skill.getData().toString()));
                                break;
                            }
                            case "elemAttr": {
                                oSkill.setElemAttr(skill.getData().toString());
                                break;
                            }
                            case "weapon": {
                                ArrayList<Integer> sks;
                                Map ws;
                                if (!ww.containsKey(skill.getData().toString())) {
                                    ws = new HashMap();
                                    sks = new ArrayList<Integer>();
                                    sks.add(skillid);
                                    ws.put(topDir.getName(), sks);
                                    ww.put(skill.getData().toString(), ws);
                                    break;
                                }
                                ws = (Map)ww.get(skill.getData().toString());
                                if (!ws.containsKey(topDir.getName())) {
                                    sks = new ArrayList();
                                    sks.add(skillid);
                                    ws.put(topDir.getName(), sks);
                                    break;
                                }
                                ((List)ws.get(topDir.getName())).add(skillid);
                                break;
                            }
                            case "subWeapon": {
                                break;
                            }
                            case "icon": 
                            case "iconMouseOver": 
                            case "iconDisabled": 
                            case "effect": 
                            case "effect0": 
                            case "hit": 
                            case "mob": 
                            case "special": 
                            case "affected": 
                            case "affected0": 
                            case "req": 
                            case "reqLev": 
                            case "skillType": 
                            case "disabledDuringAction": 
                            case "footholdInstallSummoned": 
                            case "footholdAffectedArea": 
                            case "excl": 
                            case "skillList": 
                            case "action": 
                            case "preloadEff": {
                                break;
                            }
                        }
                    }
                    skills.put(skillid, oSkill);
                }
            }
        }
    }

    public static void loadAllSkills() {
        long start = System.currentTimeMillis();
        String dir = ServerConstants.DAT_DIR + "/skills/skills.dat";
        File f = new File(dir);
        SkillData.loadSkill(f);
    }

    public static void generateDatFiles() {
        System.out.println("Started generating skill data.");
        long start = System.currentTimeMillis();
        SkillData.loadSkillsFromWz();
        SkillData.saveSkills(ServerConstants.DAT_DIR + "/skills");
        System.out.println(String.format("Completed generating skill data in %dms.", System.currentTimeMillis() - start));
        System.out.println("Started loading skill common data.");
        start = System.currentTimeMillis();
        System.out.println(String.format("Completed loaded skill common data in %dms.", System.currentTimeMillis() - start));
    }

    public static void main(String[] args) {
        Config.load();
        MapleDataProviderFactory.init();
        SkillData.loadDatFromWz();
    }

    public static void loadDatFromWz() {
        datasource = MapleDataProviderFactory.getSkill();
        SkillData.generateDatFiles();
    }

    public static void clear() {
        SkillData.getSkills().clear();
    }

    @Generated
    public static Map<Integer, Skill> getSkills() {
        return skills;
    }

    static {
        skills = new HashMap<Integer, Skill>();
    }
}

