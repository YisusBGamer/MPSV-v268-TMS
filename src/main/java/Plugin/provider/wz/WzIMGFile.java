/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Plugin.provider.wz.MsFileDataProvider
 *  tools.wzlib.Ms_Entry
 */
package Plugin.provider.wz;

import Plugin.provider.MapleDataEntity;
import Plugin.provider.MapleDataFileEntry;
import Plugin.provider.MapleDataType;
import Plugin.provider.wz.MsFileDataProvider;
import Plugin.provider.wz.MsFileMapleData;
import Plugin.provider.wz.WzFileMapleData;
import tools.wzlib.Ms_Entry;

public class WzIMGFile {
    private final MapleDataFileEntry file;
    private final WzFileMapleData root;

    public WzIMGFile(String path, MapleDataFileEntry file) {
        this(path, file, null);
    }

    public WzIMGFile(String path, MapleDataFileEntry file, MsFileDataProvider msData) {
        this.file = file;
        this.root = msData == null ? new WzFileMapleData(this, path, "", file.getOffset()) : new MsFileMapleData(this, path, "", file.getOffset(), msData, new Ms_Entry("", 0, 0, 0, 0, 0, 0, 0, null));
        this.root.setName(file.getName());
        this.root.setType(MapleDataType.EXTENDED);
    }

    public MapleDataEntity getParent() {
        return this.file.getParent();
    }

    public long getOffset() {
        return this.file.getOffset();
    }

    public WzFileMapleData getRoot() {
        return this.root;
    }
}

