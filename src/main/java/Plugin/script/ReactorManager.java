/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package Plugin.script;

import Database.DatabaseLoader;
import Database.mapper.ReactorDropEntryMapper;
import Database.tools.SqlTool;
import Net.server.maps.ReactorDropEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReactorManager {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ReactorManager.class);
    private static final ReactorManager instance = new ReactorManager();
    private final Map<Integer, List<ReactorDropEntry>> drops = new HashMap<Integer, List<ReactorDropEntry>>();

    public List<ReactorDropEntry> getDrops(int reactorId) {
        return this.drops.get(reactorId);
    }

    public void clearDrops() {
        this.drops.clear();
        this.loadDrops();
    }

    public void loadDrops() {
        DatabaseLoader.DatabaseConnection.domain(con -> {
            List<Integer> droppers = SqlTool.queryAndGetList(con, "SELECT DISTINCT `dropperid` FROM `zdata_reactordrops`", rs -> rs.getInt("dropperid"));
            for (int dropperid : droppers) {
                List<ReactorDropEntry> dropEntries = SqlTool.queryAndGetList(con, "SELECT * FROM `zdata_reactordrops` WHERE `dropperid` = ?", new ReactorDropEntryMapper(), dropperid);
                this.drops.put(dropperid, dropEntries);
            }
            return null;
        }, "讀取反應堆爆率數據出錯");
    }

    @Generated
    public static ReactorManager getInstance() {
        return instance;
    }
}

