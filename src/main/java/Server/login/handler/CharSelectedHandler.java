/*
 * Decompiled with CFR 0.152.
 */
package Server.login.handler;

import Client.MapleClient;
import Config.constants.ServerConstants;
import Packet.MaplePacketCreator;
import Server.channel.ChannelServer;
import Server.login.JobType;
import Server.login.LoginServer;
import Server.world.World;
import tools.data.MaplePacketReader;

public class CharSelectedHandler {
    private static boolean loginFailCount(MapleClient c) {
        c.loginAttempt = (short)(c.loginAttempt + 1);
        return c.loginAttempt > 5;
    }

    public static void handlePacket(MaplePacketReader slea, MapleClient c) {
        JobType jobt;
        int charId = slea.readInt();
        if (c.getPlayer() != null) {
            c.getSession().close();
            return;
        }
        if (!c.isLoggedIn() || CharSelectedHandler.loginFailCount(c) || !c.login_Auth(charId)) {
            c.sendEnableActions();
            return;
        }
        if (ChannelServer.getInstance(c.getChannel()) == null || c.getWorldId() != 0) {
            c.getSession().close();
            return;
        }
        int job = c.getCharacterJob(charId);
        if (!(job <= -1 || (jobt = JobType.getByJob(job)) != null && ServerConstants.isOpenJob(jobt.name()))) {
            c.dropMessage("該職業暫未開放,敬請期待!");
            c.sendEnableActions();
            return;
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }
        String ip = c.getSessionIPAddress();
        LoginServer.putLoginAuth(charId, ip.substring(ip.indexOf(47) + 1), c.getTempIP(), c.getChannel(), c.getMac());
        c.updateLoginState(1, ip);
        World.clearChannelChangeDataByAccountId(c.getAccID());
        c.announce(MaplePacketCreator.getServerIP(ChannelServer.getInstance(c.getChannel()).getPort(), charId));
    }
}

