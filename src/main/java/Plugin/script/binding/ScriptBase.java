/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Server.world.WorldBroadcastService
 *  lombok.Generated
 */
package Plugin.script.binding;

import Client.MapleCharacter;
import Client.inventory.Item;
import Config.constants.enums.UserChatMessageType;
import Database.tools.SqlTool;
import Net.server.MapleItemInformationProvider;
import Packet.MaplePacketCreator;
import Packet.UIPacket;
import Server.channel.ChannelServer;
import Server.world.WorldBroadcastService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.DateUtil;

public class ScriptBase {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ScriptBase.class);
    private final Map<String, Object> props = new ConcurrentHashMap<String, Object>();

    public void broadcastNotice(String message) {
        this.broadcastPlayerNotice(9, "[公告事項]" + message);
    }

    public void broadcastNoticeWithoutPrefix(String message) {
        this.broadcastPlayerNotice(9, message);
    }

    public void broadcastPlayerNotice(int type, String message) {
        WorldBroadcastService.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(type, message));
        WorldBroadcastService.getInstance().broadcastMessage(MaplePacketCreator.spouseMessage(UserChatMessageType.getByType(type), message));
    }

    public void broadcastPopupSay(int npcid, int time, String msg, String sound) {
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                mch.getClient().announce(UIPacket.addPopupSay(npcid, time, msg, sound));
            }
        }
    }

    public void broadcastItemMessage(String name, String message, Item item, int color) {
        String itemName = item.getName();
        String prefix = "";
        if (name != null) {
            prefix = name;
        }
        WorldBroadcastService.getInstance().broadcastMessage(MaplePacketCreator.gachaponMsg(prefix + message + "[" + itemName + "]", item));
    }

    public void customSqlInsert(String sql, Object ... values) {
        SqlTool.update(sql, values);
    }

    public int customSqlUpdate(String sql, Object ... values) {
        return SqlTool.executeUpdate(sql, values);
    }

    public List<Map<String, Object>> customSqlResult(String sql, Object ... values) {
        return SqlTool.customSqlResult(sql, values);
    }

    public void addItemReward(int itemId, int quantity, String message, int duration) {
        int type = 1;
        if (MapleItemInformationProvider.getInstance().isCash(itemId)) {
            type = 2;
        }
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                mch.addReward(true, DateUtil.getNextDayTime(0), duration <= 0 ? 0L : DateUtil.getNextDayTime(duration) - 60000L, type, quantity, itemId, message);
                mch.updateReward();
                mch.dropMessage(1, "收到管理員發來的禮物，請到左側獎勵箱領收。");
            }
        }
    }

    public void setGlobalVariable(String key, Object value) {
        if (value == null || "".equals(value)) {
            this.props.remove(key);
        } else {
            this.props.put(key, value);
        }
    }

    public Object getGlobalVariable(String key) {
        return this.props.get(key);
    }

    public void debug(Object s) {
        log.info(s.toString());
    }
}

