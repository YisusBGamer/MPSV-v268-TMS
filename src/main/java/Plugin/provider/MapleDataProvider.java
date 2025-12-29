/*
 * Decompiled with CFR 0.152.
 */
package Plugin.provider;

import Plugin.provider.MapleData;
import Plugin.provider.MapleDataDirectoryEntry;

public interface MapleDataProvider {
    public MapleData getData(String var1);

    public MapleDataDirectoryEntry getRoot();
}

