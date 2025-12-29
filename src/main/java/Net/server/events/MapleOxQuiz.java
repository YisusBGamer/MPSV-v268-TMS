/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.events.MapleOxQuizFactory
 *  Net.server.events.MapleOxQuizFactory$MapleOxQuizEntry
 *  SwordieX.field.ClockPacket
 *  connection.packet.FieldPacket
 */
package Net.server.events;

import Client.MapleCharacter;
import Client.MapleStat;
import Net.server.Timer;
import Net.server.events.MapleEvent;
import Net.server.events.MapleEventType;
import Net.server.events.MapleOxQuizFactory;
import Net.server.maps.MapleMap;
import Packet.MaplePacketCreator;
import SwordieX.field.ClockPacket;
import connection.packet.FieldPacket;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import tools.Pair;

public class MapleOxQuiz
extends MapleEvent {
    private ScheduledFuture<?> oxSchedule;
    private ScheduledFuture<?> oxSchedule2;
    private int timesAsked = 0;
    private boolean finished = false;

    public MapleOxQuiz(int channel, MapleEventType type) {
        super(channel, type);
    }

    @Override
    public void finished(MapleCharacter chr) {
    }

    private void resetSchedule() {
        if (this.oxSchedule != null) {
            this.oxSchedule.cancel(false);
            this.oxSchedule = null;
        }
        if (this.oxSchedule2 != null) {
            this.oxSchedule2.cancel(false);
            this.oxSchedule2 = null;
        }
    }

    @Override
    public void onMapLoad(MapleCharacter chr) {
        super.onMapLoad(chr);
        if (chr.getMapId() == this.type.mapids[0] && !chr.isGm()) {
            chr.canTalk(false);
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.getMap(0).getPortal("join00").setPortalState(false);
        this.resetSchedule();
        this.timesAsked = 0;
    }

    @Override
    public void unreset() {
        super.unreset();
        this.getMap(0).getPortal("join00").setPortalState(true);
        this.resetSchedule();
    }

    @Override
    public void startEvent() {
        this.sendQuestion();
        this.finished = false;
    }

    public void sendQuestion() {
        this.sendQuestion(this.getMap(0));
    }

    public void sendQuestion(MapleMap toSend) {
        Map.Entry question = MapleOxQuizFactory.getInstance().grabRandomQuestion();
        if (this.oxSchedule2 != null) {
            this.oxSchedule2.cancel(false);
        }
        this.oxSchedule2 = Timer.EventTimer.getInstance().schedule(() -> {
            int number = 0;
            for (MapleCharacter mc : toSend.getCharacters()) {
                if (!mc.isGm() && mc.isAlive()) continue;
                ++number;
            }
            if (toSend.getCharactersSize() - number <= 1 || this.timesAsked == 10) {
                toSend.broadcastMessage(MaplePacketCreator.serverNotice(6, "The event has ended"));
                this.unreset();
                for (MapleCharacter chr : toSend.getCharacters()) {
                    if (chr == null || chr.isGm() || !chr.isAlive()) continue;
                    chr.canTalk(true);
                    MapleOxQuiz.givePrize(chr);
                    this.warpBack(chr);
                }
                this.finished = true;
                return;
            }
            toSend.broadcastMessage(MaplePacketCreator.showOXQuiz((Integer)((Pair)question.getKey()).left, (Integer)((Pair)question.getKey()).right, true));
            toSend.broadcastMessage(FieldPacket.clock((ClockPacket)ClockPacket.secondsClock((long)10L)));
        }, 10000L);
        if (this.oxSchedule != null) {
            this.oxSchedule.cancel(false);
        }
        this.oxSchedule = Timer.EventTimer.getInstance().schedule(() -> {
            if (this.finished) {
                return;
            }
            toSend.broadcastMessage(MaplePacketCreator.showOXQuiz((Integer)((Pair)question.getKey()).left, (Integer)((Pair)question.getKey()).right, false));
            ++this.timesAsked;
            for (MapleCharacter chr : toSend.getCharacters()) {
                if (chr == null || chr.isGm() || !chr.isAlive()) continue;
                if (!this.isCorrectAnswer(chr, ((MapleOxQuizFactory.MapleOxQuizEntry)question.getValue()).getAnswer())) {
                    chr.getStat().setHp(0, chr);
                    chr.updateSingleStat(MapleStat.HP, 0L);
                    continue;
                }
                chr.gainExp(3000L, true, true, false);
            }
            this.sendQuestion();
        }, 20000L);
    }

    private boolean isCorrectAnswer(MapleCharacter chr, int answer) {
        double x = chr.getPosition().getX();
        double y = chr.getPosition().getY();
        if (x > -234.0 && y > -26.0 && answer == 0 || x < -234.0 && y > -26.0 && answer == 1) {
            chr.dropMessage(6, "[Ox Quiz] Correct!");
            return true;
        }
        chr.dropMessage(6, "[Ox Quiz] Incorrect!");
        return false;
    }
}

