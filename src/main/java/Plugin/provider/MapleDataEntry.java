/*
 * Decompiled with CFR 0.152.
 */
package Plugin.provider;

import Plugin.provider.MapleDataEntity;

public class MapleDataEntry
implements MapleDataEntity {
    private final String name;
    private final int size;
    private final int checksum;
    private final MapleDataEntity parent;
    private long offset;

    MapleDataEntry(String name, int size, int checksum, long offset, MapleDataEntity parent) {
        this.name = name;
        this.size = size;
        this.checksum = checksum;
        this.offset = offset;
        this.parent = parent;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getSize() {
        return this.size;
    }

    public int getChecksum() {
        return this.checksum;
    }

    public long getOffset() {
        return this.offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    @Override
    public MapleDataEntity getParent() {
        return this.parent;
    }

    @Override
    public String getPath() {
        MapleDataEntity mde;
        MapleDataEntity ode = this;
        String path = this.getName();
        while ((mde = ode.getParent()) != ode && mde != null) {
            ode = mde;
            if (mde.getName().isEmpty()) continue;
            path = mde.getName() + "/" + (String)path;
        }
        return path;
    }
}

