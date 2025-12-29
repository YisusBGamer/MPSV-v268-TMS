/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.BuddyList$BuddyAddResult
 *  Client.BuddyList$BuddyOperation
 *  Client.BuddylistEntry
 *  Packet.BuddyListPacket
 *  Server.world.WorldBuddyService$1
 *  SwordieX.enums.GroupMessageType
 *  connection.packet.FieldPacket
 */
package Server.world;

import Client.BuddyList;
import Client.BuddylistEntry;
import Client.MapleCharacter;
import Client.inventory.Item;
import Packet.BuddyListPacket;
import Server.channel.ChannelServer;
import Server.world.WorldBuddyService;
import Server.world.WorldFindService;
import SwordieX.enums.GroupMessageType;
import connection.packet.FieldPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldBuddyService {
    private static final Logger log = LoggerFactory.getLogger(WorldBuddyService.class);

    private WorldBuddyService() {
    }

    public static WorldBuddyService getInstance() {
        return SingletonHolder.instance;
    }

    public void buddyChat(int[] recipientCharacterIds, int cidFrom, String nameFrom, String chatText, Item item) {
        for (int characterId : recipientCharacterIds) {
            MapleCharacter chr;
            int ch = WorldFindService.getInstance().findChannel(characterId);
            if (ch <= 0 || (chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(characterId)) == null || !chr.getBuddylist().containsVisible(cidFrom)) continue;
            if (item == null) {
                chr.getClient().write(FieldPacket.groupMessage((GroupMessageType)GroupMessageType.Buddy, (MapleCharacter)chr, (String)chatText));
                continue;
            }
            chr.getClient().write(FieldPacket.itemLinkedGroupMessage((GroupMessageType)GroupMessageType.Buddy, (MapleCharacter)chr, (String)chatText, (Item)item));
        }
    }

    private void updateBuddies(int characterId, int channel, int[] buddies, boolean offline) {
        for (int buddy : buddies) {
            int mcChannel;
            BuddylistEntry ble;
            MapleCharacter chr;
            int ch = WorldFindService.getInstance().findChannel(buddy);
            if (ch <= 0 || (chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(buddy)) == null || (ble = chr.getBuddylist().get(characterId)) == null || !ble.isVisible()) continue;
            if (offline) {
                ble.setChannel(-1);
                mcChannel = -1;
            } else {
                ble.setChannel(channel);
                mcChannel = channel - 1;
            }
            chr.send(BuddyListPacket.updateBuddyChannel((int)ble.getCharacterId(), (int)mcChannel, (String)ble.getName()));
        }
    }

    public void buddyChanged(int chrId, int chrIdFrom, String name, int channel, BuddyList.BuddyOperation operation, String group) {
        int ch = WorldFindService.getInstance().findChannel(chrId);
        if (ch > 0) {
            MapleCharacter addChar = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(chrId);
            if (addChar != null) {
                BuddyList buddylist = addChar.getBuddylist();
                switch (operation) {
                    case 添加好友:
                        if (buddylist.contains(chrIdFrom)) {
                            buddylist.put(new BuddylistEntry(name, chrIdFrom, group, channel, true));
                            addChar.getClient().announce(BuddyListPacket.updateBuddylist(buddylist.getBuddies()));
                        }
                        break;
                    case 刪除好友:
                        if (buddylist.contains(chrIdFrom)) {
                            buddylist.remove(chrIdFrom);
                            addChar.getClient().announce(BuddyListPacket.updateBuddylist(buddylist.getBuddies()));
                        }
                        break;
                }
            }
        }
    }

    public BuddyList.BuddyAddResult requestBuddyAdd(String addName, int channelFrom, int chrIdFrom, String nameFrom, int levelFrom, int jobFrom) {
        MapleCharacter addChar;
        int ch = WorldFindService.getInstance().findChannel(chrIdFrom);
        if (ch > 0 && (addChar = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(addName)) != null) {
            BuddyList buddylist = addChar.getBuddylist();
            if (buddylist.isFull()) {
                return BuddyList.BuddyAddResult.好友列表已滿;
            }
            if (!buddylist.contains(chrIdFrom)) {
                buddylist.addBuddyRequest(addChar.getClient(), chrIdFrom, nameFrom, channelFrom, levelFrom, jobFrom);
            } else if (buddylist.containsVisible(chrIdFrom)) {
                return BuddyList.BuddyAddResult.已經是好友關係;
            }
        }
        return BuddyList.BuddyAddResult.添加好友成功;
    }

    public void loggedOn(int chrId, int channel, int[] buddies) {
        this.updateBuddies(chrId, channel, buddies, false);
    }

    public void loggedOff(int chrId, int channel, int[] buddies) {
        this.updateBuddies(chrId, channel, buddies, true);
    }

    private static class SingletonHolder {
        protected static final WorldBuddyService instance = new WorldBuddyService();

        private SingletonHolder() {
        }
    }
}

