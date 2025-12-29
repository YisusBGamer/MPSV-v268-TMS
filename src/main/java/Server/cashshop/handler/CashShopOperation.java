/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  SwordieX.client.party.PartyMember
 *  SwordieX.client.party.PartyResult
 *  connection.packet.WvsContext
 */
package Server.cashshop.handler;

import Client.MapleCharacter;
import Client.MapleClient;
import Config.configs.ServerConfig;
import Config.constants.ServerConstants;
import Handler.EnterCashShopHandler;
import Opcode.header.OutHeader;
import Packet.MTSCSPacket;
import Packet.MaplePacketCreator;
import Server.cashshop.CashShopServer;
import Server.channel.ChannelServer;
import Server.world.CharacterTransfer;
import Server.world.World;
import SwordieX.client.party.PartyMember;
import SwordieX.client.party.PartyResult;
import connection.packet.WvsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.data.MaplePacketLittleEndianWriter;
import tools.data.MaplePacketReader;

public class CashShopOperation {
    private static final Logger log = LoggerFactory.getLogger(CashShopOperation.class);

    public static void LeaveCS(MaplePacketReader slea, MapleClient c, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        ChannelServer toch = ChannelServer.getInstance(c.getChannel());
        String ipTcp = ServerConstants.getIPv4Address();
        short port = (short)toch.getPort();
        World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), c.getChannel());
        CashShopServer.getPlayerStorage().deregisterPlayer(chr);
        c.updateLoginState(1, ipTcp);
        c.announce(MaplePacketCreator.getChannelChange(c, port));
        chr.fixOnlineTime();
        chr.saveToDB(true, true);
        c.setPlayer(null);
        c.setReceiving(false);
    }

    public static void EnterCS(CharacterTransfer transfer, MapleClient c) {
        PartyMember pm;
        if (transfer == null) {
            c.getSession().close();
            return;
        }
        MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, c, false);
        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());
        if (!c.CheckIPAddress()) {
            c.getSession().close();
            log.info("商城檢測連接 - 2 " + !c.CheckIPAddress());
            return;
        }
        byte state = c.getLoginState();
        boolean allowLogin = false;
        if (state == 3 && !World.isCharacterListConnected(c.loadCharacterNames(c.getWorldId()))) {
            allowLogin = true;
        }
        if (!allowLogin) {
            c.setPlayer(null);
            c.getSession().close();
            log.info("商城檢測連接 - 3 " + !allowLogin);
            return;
        }
        if (chr.getParty() != null && (pm = chr.getParty().getPartyMemberByID(chr.getId())) != null) {
            pm.setFieldID(0);
            pm.setChannel(0);
            chr.getParty().broadcast(WvsContext.partyResult((PartyResult)PartyResult.setMemberData((PartyMember)pm, (int)3)));
            chr.getParty().updateFull();
        }
        EnterCashShopHandler.Start(c);
    }

    public static void openRechargeWeb(MapleClient c) {
        c.announce(MTSCSPacket.RechargeWeb("https://nxpay.nexon.com/cash/Pay.aspx?id=Q7sSfrBRJO6ynRBkuFS1waNHc2KDzei02wNIpbbRuujG7dHuBhfZMCEWIXXzqqXbq9sYa6eCOAw3ze4g23qFv8T3u+CvHBrMr3bDweFe7Fk=&channel=MAPL&type=1"));
    }

    public static void enterCashShop(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.ENTER_CASH_SHOP.getValue());
        mplew.writeZeroBytes(28);
        c.announce(mplew.getPacket());
    }

    public static void CheckMileageRequest(MapleClient c) {
        c.announce(MTSCSPacket.showMileageInfo(c));
    }

    public static void CSUpdate(MapleClient c) {
        c.announce(MTSCSPacket.CashShopQueryCashResult(c.getPlayer()));
        c.announce(CashShopOperation.keyUI(c));
    }

    public static void doCSPackets(MapleClient c) {
        c.announce(MTSCSPacket.loadLockerDone(c));
        c.announce(MTSCSPacket.CashShopQueryCashResult(c.getPlayer()));
        c.getPlayer().getCashInventory().checkExpire(c);
    }

    public static byte[] keyUI(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.CASH_SHOP_KEY_UI.getValue());
        mplew.writeInt(-1);
        mplew.writeInt(-1);
        mplew.writeInt(c.getPlayer().getMileage());
        return mplew.getPacket();
    }

    public static void CashShopEventMessage(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(OutHeader.LP_BroadcastMsg.getValue());
        mplew.write(26);
        mplew.write(1);
        mplew.writeMapleAsciiString(ServerConfig.Cash_Shop_Message);
        mplew.writeInt(0);
        mplew.writeInt(0);
        c.announce(mplew.getPacket());
    }
}

