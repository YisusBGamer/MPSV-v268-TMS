/*
 * Decompiled with CFR 0.152.
 */
package Plugin.provider.json;

import Plugin.provider.MapleData;
import Plugin.provider.MapleDataDirectoryEntry;
import Plugin.provider.MapleDataFileEntry;
import Plugin.provider.MapleDataProvider;
import Plugin.provider.json.JsonDomMapleData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONFileDataProvider
implements MapleDataProvider {
    private static final Logger log = LoggerFactory.getLogger(JSONFileDataProvider.class);
    private final File root;
    private final MapleDataDirectoryEntry rootForNavigation;
    private List<JSONFileDataProvider> subJsonFiles;
    private String subStr;

    public JSONFileDataProvider(File fileIn) {
        this(fileIn, "");
    }

    public JSONFileDataProvider(File fileIn, String subStr) {
        this.root = fileIn;
        this.rootForNavigation = new MapleDataDirectoryEntry(fileIn.getName(), null);
        this.subStr = subStr;
        this.fillMapleDataEntitys(this.root, this.rootForNavigation);
        this.subJsonFiles = new ArrayList<JSONFileDataProvider>();
        if (subStr.isEmpty()) {
            for (File file : fileIn.getParentFile().listFiles()) {
                if (!file.isDirectory() || file.getPath().equalsIgnoreCase(fileIn.getPath()) || !file.getName().replaceAll("\\d+", "").toLowerCase().equals(fileIn.getName().toLowerCase())) continue;
                String sub = file.getName();
                sub = sub.substring(0, sub.lastIndexOf(".")).replace(fileIn.getName().substring(0, fileIn.getName().lastIndexOf(".")), "");
                JSONFileDataProvider data = new JSONFileDataProvider(file, sub);
                this.subJsonFiles.add(data);
                this.rootForNavigation.addAll(data.getRoot());
            }
        }
    }

    private void fillMapleDataEntitys(File lroot, MapleDataDirectoryEntry wzdir) {
        for (File file : lroot.listFiles()) {
            String fileName = file.getName();
            if (file.isDirectory() && !fileName.endsWith(".img")) {
                MapleDataDirectoryEntry newDir = new MapleDataDirectoryEntry(fileName, wzdir);
                wzdir.addDirectory(newDir);
                this.fillMapleDataEntitys(file, newDir);
                continue;
            }
            if (!fileName.endsWith(".json")) continue;
            wzdir.addFile(new MapleDataFileEntry(fileName.substring(0, fileName.length() - 5), wzdir, this.subStr));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MapleData getData(String path) {
        File dataFile = new File(this.root, path + ".json");
        File imageDataDir = new File(this.root.getName().replaceAll("(\\.[Ww][Zz])+$", ""), path);
        LinkedList<String> segments = new LinkedList<String>(Arrays.asList(path.split("/")));
        Iterator it = segments.iterator();
        StringBuilder imgPath = new StringBuilder();
        while (it.hasNext()) {
            if (imgPath.length() > 0) {
                imgPath.append("/");
            }
            imgPath.append((String)it.next());
            it.remove();
            dataFile = new File(this.root, String.valueOf(imgPath) + ".json");
            if (!dataFile.exists()) continue;
            imageDataDir = new File(this.root.getName().replaceAll("(\\.[Ww][Zz])+$", ""), imgPath.toString());
            break;
        }
        StringBuilder childPath = new StringBuilder();
        for (String segment : segments) {
            if (childPath.length() > 0) {
                childPath.append("/");
            }
            childPath.append(segment);
        }
        MapleData domMapleData = null;
        if (dataFile.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(dataFile);
            }
            catch (FileNotFoundException e) {
                log.error("Datafile " + path + " does not exist in " + this.root.getAbsolutePath(), e);
                throw new RuntimeException("Datafile " + path + " does not exist in " + this.root.getAbsolutePath());
            }
            try {
                JsonDomMapleData imgNode = new JsonDomMapleData(fis, imageDataDir.getParentFile());
                domMapleData = imgNode.getChildren().get(0);
            }
            finally {
                try {
                    fis.close();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return domMapleData;
    }

    @Override
    public MapleDataDirectoryEntry getRoot() {
        return this.rootForNavigation;
    }
}

