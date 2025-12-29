/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.AutobanManager$ExpirationEntry
 *  Server.world.WorldBroadcastService
 *  lombok.Generated
 */
package Net.server;

import Client.MapleClient;
import Config.configs.ServerConfig;
import Config.constants.enums.UserChatMessageType;
import Net.server.AutobanManager;
import Packet.MaplePacketCreator;
import Server.world.WorldBroadcastService;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutobanManager
implements Runnable {
    private static final Logger log = LoggerFactory.getLogger("AutobanManager");
    private static final int AUTOBAN_POINTS = 5000;
    private static final AutobanManager instance = new AutobanManager();
    private final Map<Integer, Integer> points = new HashMap<Integer, Integer>();
    private final Map<Integer, List<String>> reasons = new HashMap<Integer, List<String>>();
    private final Set<ExpirationEntry> expirations = new TreeSet<ExpirationEntry>();
    private final ReentrantLock lock = new ReentrantLock(true);

    public void autoban(MapleClient c, String reason) {
        if (c.getPlayer() == null) {
            return;
        }
        if (c.getPlayer().isGm()) {
            c.getPlayer().dropMessage(5, "[警告] A/b 觸發: " + reason);
        } else if (ServerConfig.WORLD_AUTOBAN) {
            this.addPoints(c, 5000, 0L, reason);
        } else {
            WorldBroadcastService.getInstance().broadcastGMMessage(MaplePacketCreator.spouseMessage(UserChatMessageType.淺紫, "[GM消息] 玩家: " + c.getPlayer().getName() + " ID: " + c.getPlayer().getId() + " (等級 " + c.getPlayer().getLevel() + ") 遊戲操作異常. (原因: " + reason + ")"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPoints(MapleClient c, int points, long expiration, String reason) {
        this.lock.lock();
        try {
            int acc = c.getPlayer().getAccountID();
            List<String> reasonList;
            if (this.points.containsKey(acc)) {
                int SavedPoints = this.points.get(acc);
                if (SavedPoints >= 5000) {
                    return;
                }
                this.points.put(acc, SavedPoints + points);
                reasonList = this.reasons.get(acc);
                reasonList.add(reason);
            } else {
                this.points.put(acc, points);
                reasonList = new LinkedList<String>();
                reasonList.add(reason);
                this.reasons.put(acc, reasonList);
            }
            if (this.points.get(acc) >= 5000) {
                log.info("[作弊] 玩家 " + c.getPlayer().getName() + " A/b 觸發 " + reason);
                if (c.getPlayer().isGm()) {
                    c.getPlayer().dropMessage(5, "[警告] A/b 觸發 : " + reason);
                    return;
                }
                StringBuilder sb = new StringBuilder("A/b ");
                sb.append(c.getPlayer().getName());
                sb.append(" (IP ");
                sb.append(c.getSessionIPAddress());
                sb.append("): ");
                for (String s : this.reasons.get(acc)) {
                    sb.append(s);
                    sb.append(", ");
                }
                WorldBroadcastService.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(0, " <" + c.getPlayer().getName() + "> 被系統封號 (原因: " + reason + ")"));
                c.getPlayer().ban(sb.toString(), false, true, false);
            } else if (expiration > 0L) {
                this.expirations.add(new ExpirationEntry(System.currentTimeMillis() + expiration, acc, points));
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        for (ExpirationEntry e : this.expirations) {
            if (e.time <= now) {
                this.points.put(e.acc, this.points.get(e.acc) - e.points);
                continue;
            }
            return;
        }
    }

    @Generated
    public static AutobanManager getInstance() {
        return instance;
    }

    private static class ExpirationEntry implements Comparable<ExpirationEntry> {

        public final long time;
        public final int acc;
        public final int points;

        public ExpirationEntry(long time, int acc, int points) {
            this.time = time;
            this.acc = acc;
            this.points = points;
        }

        @Override
        public int compareTo(ExpirationEntry o) {
            return (int) (time - o.time);
        }

        @Override
        public boolean equals(Object oth) {
            if (!(oth instanceof ExpirationEntry ee)) {
                return false;
            }
            return (time == ee.time && points == ee.points && acc == ee.acc);
        }
    }
}

