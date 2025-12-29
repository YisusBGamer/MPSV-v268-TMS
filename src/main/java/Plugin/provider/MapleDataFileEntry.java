/*
 * Decompiled with CFR 0.152.
 */
package Plugin.provider;

import Plugin.provider.MapleDataEntity;
import Plugin.provider.MapleDataEntry;

public final class MapleDataFileEntry
extends MapleDataEntry {
    private long offset;
    private final String subStr;

    public MapleDataFileEntry(String name, MapleDataEntity parent, String subStr) {
        this(name, 0, 0, 0L, parent, subStr);
    }

    public MapleDataFileEntry(String name, int size, int checksum, long offset, MapleDataEntity parent, String subStr) {
        super(name, size, checksum, offset, parent);
        this.offset = offset;
        this.subStr = subStr;
    }

    @Override
    public long getOffset() {
        return this.offset;
    }

    @Override
    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String getSub() {
        return this.subStr;
    }
}

