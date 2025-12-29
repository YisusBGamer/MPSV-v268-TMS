/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.shops.HiredFisher
 *  Net.server.shops.HiredMerchant
 *  Server.world.CheaterData
 *  Server.world.WorldAllianceService
 *  Server.world.WorldBroadcastService
 *  Server.world.WorldGuildService
 *  Server.world.WorldMessengerService
 */
package Server.world;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Config.configs.ServerConfig;
import Config.constants.enums.Holiday;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.MobSkill;
import Net.server.shops.HiredFisher;
import Net.server.shops.HiredMerchant;
import Packet.BuffPacket;
import Server.auction.AuctionServer;
import Server.cashshop.CashShopServer;
import Server.channel.ChannelServer;
import Server.channel.PlayerStorage;
import Server.world.CharacterTransfer;
import Server.world.CheaterData;
import Server.world.WorldAllianceService;
import Server.world.WorldBroadcastService;
import Server.world.WorldBuddyService;
import Server.world.WorldFindService;
import Server.world.WorldGuildService;
import Server.world.WorldMessengerService;
import com.github.heqiao2010.lunar.LunarCalendar;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class World {
    public static final int CASH_SHOP_CHANNEL = -10;
    public static final int AUCTION_CHANNEL = -20;
    public static final int WORLD_TOTAL = 0;
    private static int lastCheckHolidayDay = 0;
    private static Holiday holiday = Holiday.None;

    public static void init() {
        WorldFindService.getInstance();
        WorldBroadcastService.getInstance();
        WorldAllianceService.getInstance();
        WorldBuddyService.getInstance();
        WorldGuildService.getInstance();
        WorldMessengerService.getInstance();
    }

    public static String getStatus() {
        StringBuilder ret = new StringBuilder();
        int totalUsers = 0;
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            ret.append("頻道 ");
            ret.append(cs.getChannel());
            ret.append(": ");
            int channelUsers = cs.getConnectedClients();
            totalUsers += channelUsers;
            ret.append(channelUsers);
            ret.append(" 玩家\n");
        }
        ret.append("總計在線: ");
        ret.append(totalUsers);
        ret.append("\n");
        return ret.toString();
    }

    public static void updateHoliday() {
        LocalDate currentDate = LocalDate.now();
        if (lastCheckHolidayDay == currentDate.getDayOfMonth()) {
            return;
        }
        lastCheckHolidayDay = currentDate.getDayOfMonth();
        for (Holiday d : Holiday.values()) {
            if (d == Holiday.None || !World.isHolidayWeek(currentDate, d)) continue;
            holiday = d;
            break;
        }
    }

    public static Holiday getHoliday() {
        return holiday;
    }

    private static boolean isHolidayWeek(LocalDate date, Holiday holiday) {
        return World.isDateBetween(date.getYear(), date, holiday) || World.isDateBetween(date.getYear() + 1, date, holiday);
    }

    private static boolean isDateBetween(int year, LocalDate date, Holiday holiday) {
        LocalDate holidayDate;
        if (holiday.isLunar()) {
            Calendar calendar = LunarCalendar.lunar2Solar(year, holiday.getMonth(), holiday.getDayOfMonth(), false);
            holidayDate = LocalDate.of(calendar.get(1), calendar.get(2) + 1, calendar.get(5));
        } else {
            holidayDate = LocalDate.of(year, holiday.getMonth(), holiday.getDayOfMonth());
        }
        LocalDate startOfWeek = holidayDate.minusDays(14L);
        LocalDate endOfWeek = holidayDate.plusDays(14L);
        return date.isEqual(startOfWeek) || date.isEqual(endOfWeek) || date.isAfter(startOfWeek) && date.isBefore(endOfWeek);
    }

    public static Map<Integer, Integer> getConnected() {
        LinkedHashMap<Integer, Integer> ret = new LinkedHashMap<Integer, Integer>();
        int total = 0;
        for (ChannelServer ch : ChannelServer.getAllInstances()) {
            int chOnline = ch.getConnectedClients();
            ret.put(ch.getChannel(), chOnline);
            total += chOnline;
        }
        int csOnline = CashShopServer.getConnectedClients();
        ret.put(-10, csOnline);
        ret.put(0, total += csOnline);
        return ret;
    }

    public static int getTotalConnected() {
        Map<Integer, Integer> connected = World.getConnected();
        return connected.get(0);
    }

    public static List<CheaterData> getCheaters() {
        ArrayList<CheaterData> allCheaters = new ArrayList<CheaterData>();
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            allCheaters.addAll(cs.getCheaters());
        }
        Collections.sort(allCheaters);
        return allCheaters.subList(0, 20);
    }

    public static List<CheaterData> getReports() {
        ArrayList<CheaterData> allCheaters = new ArrayList<CheaterData>();
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            allCheaters.addAll(cs.getReports());
        }
        Collections.sort(allCheaters);
        return allCheaters.subList(0, 20);
    }

    public static boolean isConnected(String charName) {
        return WorldFindService.getInstance().findChannel(charName) > 0;
    }

    public static void toggleMegaphoneMuteState() {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            cs.toggleMegaphoneMuteState();
        }
    }

    public static void clearChannelChangeDataByAccountId(int accountid) {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            World.getStorage(cs.getChannel()).deregisterPendingPlayerByAccountId(accountid);
        }
        World.getStorage(-20).deregisterPendingPlayerByAccountId(accountid);
        World.getStorage(-10).deregisterPendingPlayerByAccountId(accountid);
    }

    public static void ChannelChange_Data(CharacterTransfer Data2, int characterid, int toChannel) {
        World.getStorage(toChannel).registerPendingPlayer(Data2, characterid);
    }

    public static boolean isCharacterListConnected(List<String> charNames) {
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (String name : charNames) {
                if (!cserv.isConnected(name)) continue;
                return true;
            }
        }
        return false;
    }

    public static String getAllowLoginTip(List<String> charNames) {
        StringBuilder ret = new StringBuilder("賬號下其他角色在遊戲: ");
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (String name : charNames) {
                if (!cserv.isConnected(name)) continue;
                ret.append(name);
                ret.append(" ");
            }
        }
        return ret.toString();
    }

    public static boolean hasMerchant(int accountID) {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            if (!cs.containsMerchant(accountID)) continue;
            return true;
        }
        return false;
    }

    public static boolean hasMerchant(int accountID, int characterID) {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            if (!cs.containsMerchant(accountID, characterID)) continue;
            return true;
        }
        return false;
    }

    public static HiredMerchant getMerchant(int accountID, int characterID) {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            if (!cs.containsMerchant(accountID, characterID)) continue;
            return cs.getHiredMerchants(accountID, characterID);
        }
        return null;
    }

    public static PlayerStorage getStorage(int channel) {
        if (channel == -20) {
            return AuctionServer.getInstance().getPlayerStorage();
        }
        if (channel == -10) {
            return CashShopServer.getPlayerStorage();
        }
        return ChannelServer.getInstance(channel).getPlayerStorage();
    }

    public static boolean isChannelAvailable(int ch) {
        return ChannelServer.getInstance(ch) != null && ChannelServer.getInstance(ch).getPlayerStorage() != null && ChannelServer.getInstance(ch).getPlayerStorage().getConnectedClients() < ServerConfig.LOGIN_USERLIMIT;
    }

    public static boolean hasFisher(int accountID, int characterID) {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            if (!cs.containsFisher(accountID, characterID)) continue;
            return true;
        }
        return false;
    }

    public static HiredFisher getFisher(int accountID, int characterID) {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            if (!cs.containsFisher(accountID, characterID)) continue;
            return cs.getHiredFisher(accountID, characterID);
        }
        return null;
    }

    public static class TemporaryStat {
        public static final Map<Integer, Map<SecondaryStat, List<SecondaryStatValueHolder>>> TemporaryStats = new LinkedHashMap<Integer, Map<SecondaryStat, List<SecondaryStatValueHolder>>>();

        public static boolean IsSaveStat(SecondaryStat stat, SecondaryStatValueHolder mbsvh) {
            if (mbsvh.effect instanceof MobSkill) {
                return false;
            }
            switch (stat) {
                case IndieEXP: 
                case ExpBuffRate: 
                case PlusExpRate: {
                    return mbsvh.sourceID < 0;
                }
            }
            return TemporaryStat.IsNotRemoveSaveStat(stat, mbsvh);
        }

        public static boolean IsNotRemoveSaveStat(SecondaryStat stat, SecondaryStatValueHolder mbsvh) {
            if (mbsvh.effect instanceof MobSkill) {
                return false;
            }
            switch (mbsvh.sourceID) {
                case 2310013: 
                case 25120017: {
                    return true;
                }
                case 2321055: {
                    return stat == SecondaryStat.HeavensDoorNotTime;
                }
            }
            switch (stat) {
                case RuneStoneNoTime: 
                case ViperTimeLeap: {
                    return true;
                }
            }
            return false;
        }

        public static void SaveData(MapleCharacter chr) {
            Map effects = TemporaryStats.computeIfAbsent(chr.getId(), k -> new LinkedHashMap());
            chr.getEffects().forEach((key, value) -> {
                value.stream().filter((mbsvh) -> {
                    return IsSaveStat(key, mbsvh);
                }).forEach((mbsvh) -> {
                    ((List)effects.computeIfAbsent(key, (k) -> {
                        return new LinkedList();
                    })).add(mbsvh);
                });
            });


            if (effects.isEmpty()) {
                TemporaryStats.remove(chr.getId());
            }
        }

        public static void LoadData(MapleCharacter chr) {
            if (!TemporaryStats.containsKey(chr.getId())) {
                return;
            }
            LinkedHashMap<Integer, List> stats = new LinkedHashMap<Integer, List>();
            TemporaryStats.remove(chr.getId()).forEach((mapleBuffStat, v) -> v.forEach(mbsvh -> {
                if (mbsvh.getLeftTime() >= 5000 && mbsvh.schedule != null && !mbsvh.schedule.isCancelled() && !mbsvh.schedule.isDone()) {
                    chr.getEffects().computeIfAbsent((SecondaryStat)mapleBuffStat, k -> new LinkedList()).add(mbsvh);
                    if (mbsvh.CancelAction != null) {
                        mbsvh.CancelAction.changeTarget(chr);
                    }
                    stats.computeIfAbsent(mbsvh.sourceID, k -> new LinkedList()).add(mapleBuffStat);
                }
            }));

            stats.forEach((key, value) -> {
                chr.send(BuffPacket.giveBuff(chr, (MapleStatEffect)null, (Map)value.stream().collect(Collectors.toMap((stat) -> {
                    return stat;
                }, (stat) -> {
                    return key;
                }, (a, b) -> {
                    return b;
                }, LinkedHashMap::new))));
            });
        }

        public static void CancelStat(int cid, List<SecondaryStat> stats, MapleStatEffect effect, long startTime) {
            if (!TemporaryStats.containsKey(cid)) {
                return;
            }
            Map<SecondaryStat, List<SecondaryStatValueHolder>> effects = TemporaryStats.get(cid);
            stats.stream().filter(effects::containsKey).forEach(stat -> {
                List holderList = (List)effects.get(stat);
                Iterator holderIterator = holderList.iterator();
                while (holderIterator.hasNext()) {
                    SecondaryStatValueHolder mbsvh = (SecondaryStatValueHolder)holderIterator.next();
                    if ((!mbsvh.effect.sameSource(effect) || mbsvh.startTime != startTime && startTime != -1L) && (mbsvh.effect.sameSource(effect) || stat.canStack())) continue;
                    mbsvh.cancel();
                    holderIterator.remove();
                }
                if (holderList.isEmpty()) {
                    effects.remove(stat);
                }
            });
            if (effects.isEmpty()) {
                TemporaryStats.remove(cid);
            }
        }
    }

    public static class Client {
        private static final Set<MapleClient> clients = new HashSet<MapleClient>();

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public static void addClient(MapleClient c) {
            Class<World> clazz = World.class;
            synchronized (World.class) {
                clients.add(c);
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public static boolean isStuck(MapleClient c, int accId) {
            Class<World> clazz = World.class;
            synchronized (World.class) {
                MapleClient stuckClient = null;
                for (MapleClient client : Client.getClients()) {
                    if (client == c || client.getAccID() != accId) continue;
                    stuckClient = client;
                    break;
                }
                if (stuckClient == null) {
                    // ** MonitorExit[var2_2] (shouldn't be in output)
                    return true;
                }
                if (stuckClient.getSession() == null) {
                    clients.remove(stuckClient);
                    // ** MonitorExit[var2_2] (shouldn't be in output)
                    return true;
                }
                stuckClient.getSession().close();
                // ** MonitorExit[var2_2] (shouldn't be in output)
                return false;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public static boolean removeClient(MapleClient c) {
            Class<World> clazz = World.class;
            synchronized (World.class) {
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return clients.remove(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public static ArrayList<MapleClient> getClients() {
            Class<World> clazz = World.class;
            synchronized (World.class) {
                // ** MonitorExit[var0] (shouldn't be in output)
                return new ArrayList<MapleClient>(clients);
            }
        }
    }
}

