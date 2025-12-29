/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Plugin.provider.nx.NXFileDataProvider
 *  Plugin.provider.wz.WzFileDataProvider
 */
package Plugin.provider;

import Config.configs.Config;
import Database.DatabaseLoader;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataEntity;
import Plugin.provider.MapleDataFileEntry;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.json.JSONFileDataProvider;
import Plugin.provider.nx.NXFileDataProvider;
import Plugin.provider.wz.WzFileDataProvider;
import Plugin.provider.wz.WzFileMapleData;
import Plugin.provider.wz.WzIMGFile;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.data.BufferedRandomAccessFile;
import tools.data.MaplePacketReader;
import tools.data.RandomAccessByteStream;

public final class MapleDataProviderFactory {
    private static final Logger log = LoggerFactory.getLogger(MapleDataProviderFactory.class);
    public static String WZPATH = Config.getProperty("wzpath", "Data");
    private static final Map<File, MapleDataProvider> CACHE = new HashMap<File, MapleDataProvider>();
    public static String HOTFIX_DATA_PATH = Config.getProperty("HotfixDataPath", "Hotfix/Data.wz");
    private static Map<String, MapleData> HotfixDataMap = null;
    private static String HotfixCheck = null;
    private static final Lock hotfixLoadLock = new ReentrantLock(true);

    public static void init() {
        File in = new File(WZPATH);
        if (!in.exists()) {
            System.err.print("請輸入Wz所在的位置:");
            WZPATH = DatabaseLoader.scanner.next().replace("\\", "\\\\");
            Config.setProperty("wzpath", WZPATH);
            MapleDataProviderFactory.init();
        }
    }

    private static MapleDataProvider getWZ(String name) {
        Object fileData;
        File in;
        block16: {
            File checkFile;
            in = new File(WZPATH, name);
            if (!(in.exists() || (in = new File(WZPATH + File.separator + name + ".wz")).exists() || (in = new File(WZPATH + File.separator + name + ".nx")).exists())) {
                throw new RuntimeException("檔案不存在" + in.getPath());
            }
            if (CACHE.containsKey(in)) {
                return CACHE.get(in);
            }
            if (!in.getName().endsWith(".wz") && in.isDirectory() && name.matches("^([A-Za-z]+)$")) {
                checkFile = new File(in.getPath() + File.separator + in.getName() + ".wz");
                if (!checkFile.exists() && !(checkFile = new File(in.getPath() + File.separator + in.getName() + ".nx")).exists()) {
                    throw new RuntimeException("檔案不存在" + checkFile.getPath());
                }
            } else {
                checkFile = in;
            }
            if (checkFile.isDirectory()) {
                fileData = new JSONFileDataProvider(in);
            } else {
                try (BufferedRandomAccessFile raf = new BufferedRandomAccessFile(checkFile, "r");){
                    MaplePacketReader lea = new MaplePacketReader(new RandomAccessByteStream(raf));
                    String magic = lea.readAsciiString(4);
                    raf.close();
                    if (magic.equalsIgnoreCase("PKG1")) {
                        fileData = new WzFileDataProvider(in);
                        break block16;
                    }
                    if (magic.equalsIgnoreCase("PKG4")) {
                        fileData = new NXFileDataProvider(in);
                        break block16;
                    }
                    throw new RuntimeException("不支援這個" + magic + "格式檔案" + checkFile.getPath());
                }
                catch (Exception e) {
                    throw new RuntimeException("讀取檔案時出錯", e);
                }
            }
        }
        CACHE.put(in, (MapleDataProvider) fileData);
        return (MapleDataProvider)fileData;
    }

    public static MapleDataProvider getEffect() {
        return MapleDataProviderFactory.getWZ("Effect");
    }

    public static MapleDataProvider getItem() {
        return MapleDataProviderFactory.getWZ("Item");
    }

    public static MapleDataProvider getCharacter() {
        return MapleDataProviderFactory.getWZ("Character");
    }

    public static MapleDataProvider getSkill() {
        return MapleDataProviderFactory.getWZ("Skill");
    }

    public static MapleDataProvider getString() {
        return MapleDataProviderFactory.getWZ("String");
    }

    public static MapleDataProvider getEtc() {
        return MapleDataProviderFactory.getWZ("Etc");
    }

    public static MapleDataProvider getMob() {
        return MapleDataProviderFactory.getWZ("Mob");
    }

    public static MapleDataProvider getNpc() {
        return MapleDataProviderFactory.getWZ("Npc");
    }

    public static MapleDataProvider getMap() {
        return MapleDataProviderFactory.getWZ("Map");
    }

    public static MapleDataProvider getReactor() {
        return MapleDataProviderFactory.getWZ("Reactor");
    }

    public static MapleDataProvider getQuest() {
        return MapleDataProviderFactory.getWZ("Quest");
    }

    public static void loadHotfixData() {
        HotfixDataMap = new LinkedHashMap<String, MapleData>();
        File file = new File(HOTFIX_DATA_PATH);
        if (!file.exists()) {
            log.info("Hotfix檔案不存在, 忽略Hotfix資料{}", (Object)file.getPath());
            return;
        }
        try (FileInputStream in = new FileInputStream(file);){
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[1048576000];
            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                digest.update(buffer, 0, len);
            }
            String sha1 = new BigInteger(1, digest.digest()).toString(16);
            int length = 40 - ((String)sha1).length();
            if (length > 0) {
                for (int i = 0; i < length; ++i) {
                    sha1 = "0" + (String)sha1;
                }
            }
            HotfixCheck = sha1;
        }
        catch (Exception e) {
            e.getStackTrace();
        }
        try (BufferedRandomAccessFile raf = new BufferedRandomAccessFile(file, "r");){
            MaplePacketReader lea = new MaplePacketReader(new RandomAccessByteStream(raf));
            byte magic = lea.readByte();
            raf.close();
            if (magic == 115) {
                WzIMGFile img = new WzIMGFile(file.getPath(), new MapleDataFileEntry("", null, ""));
                for (MapleData dat : img.getRoot().getChildren()) {
                    String s = dat.getPath();
                    MapleDataEntity dd = dat.getParent();
                    String name = dat.getName().replace("\\", "/");
                    if (dat instanceof WzFileMapleData) {
                        ((WzFileMapleData)dat).setName(name.substring(name.lastIndexOf("/") + 1));
                    }
                    HotfixDataMap.put(name, dat);
                }
            } else {
                System.err.println("Data.wz檔案不是正確的Hotfix檔案:" + file.getPath());
            }
        }
        catch (Exception e) {
            System.err.println("讀取檔案時出錯:" + file.getPath());
        }
    }

    public static Map<String, MapleData> getHotfixDatas() {
        hotfixLoadLock.lock();
        try {
            if (HotfixDataMap == null) {
                MapleDataProviderFactory.loadHotfixData();
            }
            LinkedHashMap<String, MapleData> linkedHashMap = new LinkedHashMap<String, MapleData>(HotfixDataMap);
            return linkedHashMap;
        }
        finally {
            hotfixLoadLock.unlock();
        }
    }

    public static String getHotfixCheck() {
        hotfixLoadLock.lock();
        try {
            if (HotfixDataMap == null) {
                MapleDataProviderFactory.loadHotfixData();
            }
            String string = HotfixCheck;
            return string;
        }
        finally {
            hotfixLoadLock.unlock();
        }
    }
}

