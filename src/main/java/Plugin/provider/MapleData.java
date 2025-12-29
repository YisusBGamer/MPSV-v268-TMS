/*
 * Decompiled with CFR 0.152.
 */
package Plugin.provider;

import Plugin.provider.MapleDataEntity;
import Plugin.provider.MapleDataType;
import java.util.List;

public interface MapleData
extends MapleDataEntity,
Iterable<MapleData> {
    public MapleData getChildByPath(String var1);

    public List<MapleData> getChildren();

    public Object getData();

    public MapleDataType getType();
}

