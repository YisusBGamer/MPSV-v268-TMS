/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Plugin.provider.wz.MsFileDataProvider
 *  tools.wzlib.Ms_Entry
 */
package Plugin.provider.wz;

import Plugin.provider.wz.MsFileDataProvider;
import Plugin.provider.wz.WzFileMapleData;
import Plugin.provider.wz.WzIMGFile;
import tools.wzlib.Ms_Entry;

public class MsFileMapleData
extends WzFileMapleData {
    MsFileDataProvider msData;
    public final Ms_Entry msEntry;

    public MsFileMapleData(WzIMGFile file, String wzFile, String parent, long entryOffset, MsFileDataProvider msData, Ms_Entry msEntry) {
        super(file, wzFile, parent, entryOffset);
        this.msData = msData;
        this.msEntry = msEntry;
    }
}

