/*
 * Decompiled with CFR 0.152.
 */
package Plugin.provider;

import Plugin.provider.MapleDataEntity;
import Plugin.provider.MapleDataEntry;
import Plugin.provider.MapleDataFileEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapleDataDirectoryEntry
extends MapleDataEntry {
    private final List<MapleDataDirectoryEntry> subdirs = new ArrayList<MapleDataDirectoryEntry>();
    private final List<MapleDataFileEntry> files = new ArrayList<MapleDataFileEntry>();
    private final Map<String, MapleDataEntry> entries = new HashMap<String, MapleDataEntry>();

    public MapleDataDirectoryEntry(String name, MapleDataEntity parent) {
        this(name, 0, 0, 0L, parent);
    }

    public MapleDataDirectoryEntry(String name, int size, int checksum, long offset, MapleDataEntity parent) {
        super(name, size, checksum, offset, parent);
    }

    public void addDirectory(MapleDataDirectoryEntry dir) {
        this.subdirs.add(dir);
        this.entries.put(dir.getName(), dir);
    }

    public void addFile(MapleDataFileEntry fileEntry) {
        this.files.add(fileEntry);
        this.entries.put(fileEntry.getName(), fileEntry);
    }

    public List<MapleDataDirectoryEntry> getSubdirectories() {
        return Collections.unmodifiableList(this.subdirs);
    }

    public List<MapleDataFileEntry> getFiles() {
        return Collections.unmodifiableList(this.files);
    }

    public MapleDataEntry getEntry(String name) {
        return this.entries.get(name);
    }

    public void addAll(MapleDataDirectoryEntry root) {
        MapleDataEntry entry;
        for (MapleDataDirectoryEntry dir : root.getSubdirectories()) {
            entry = this.getEntry(dir.getName());
            if (entry != null && entry instanceof MapleDataDirectoryEntry) {
                ((MapleDataDirectoryEntry)entry).addAll(dir);
                continue;
            }
            this.addDirectory(dir);
        }
        for (MapleDataFileEntry f : root.getFiles()) {
            entry = this.getEntry(f.getName());
            if (entry instanceof MapleDataFileEntry) continue;
            this.addFile(f);
        }
    }
}

