/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.events.MapleCoconut$MapleCoconuts
 *  SwordieX.field.ClockPacket
 *  connection.packet.FieldPacket
 */
package Net.server.events;

import Client.MapleCharacter;
import Net.server.Timer;
import Net.server.events.MapleCoconut;
import Net.server.events.MapleEvent;
import Net.server.events.MapleEventType;
import Packet.MaplePacketCreator;
import SwordieX.field.ClockPacket;
import connection.packet.FieldPacket;
import java.util.LinkedList;
import java.util.List;

public class MapleCoconut
extends MapleEvent {
    private final List<MapleCoconuts> coconuts = new LinkedList<MapleCoconuts>();
    private final int[] coconutscore = new int[2];
    private int countBombing = 0;
    private int countFalling = 0;
    private int countStopped = 0;

    public MapleCoconut(int channel, MapleEventType type) {
        super(channel, type);
    }

    @Override
    public void finished(MapleCharacter chr) {
    }

    @Override
    public void reset() {
        super.reset();
        this.resetCoconutScore();
    }

    @Override
    public void unreset() {
        super.unreset();
        this.resetCoconutScore();
        this.setHittable(false);
    }

    @Override
    public void onMapLoad(MapleCharacter chr) {
        super.onMapLoad(chr);
        chr.send(MaplePacketCreator.coconutScore(this.getCoconutScore()));
    }

    public MapleCoconuts getCoconut(int id) {
        if (id >= this.coconuts.size()) {
            return null;
        }
        return this.coconuts.get(id);
    }

    public List<MapleCoconuts> getAllCoconuts() {
        return this.coconuts;
    }

    public void setHittable(boolean hittable) {
        for (MapleCoconuts nut : this.coconuts) {
            nut.setHittable(hittable);
        }
    }

    public int getBombings() {
        return this.countBombing;
    }

    public void bombCoconut() {
        --this.countBombing;
    }

    public int getFalling() {
        return this.countFalling;
    }

    public void fallCoconut() {
        --this.countFalling;
    }

    public int getStopped() {
        return this.countStopped;
    }

    public void stopCoconut() {
        --this.countStopped;
    }

    public int[] getCoconutScore() {
        return this.coconutscore;
    }

    public int getMapleScore() {
        return this.coconutscore[0];
    }

    public int getStoryScore() {
        return this.coconutscore[1];
    }

    public void addMapleScore() {
        this.coconutscore[0] = this.coconutscore[0] + 1;
    }

    public void addStoryScore() {
        this.coconutscore[1] = this.coconutscore[1] + 1;
    }

    public void resetCoconutScore() {
        this.coconutscore[0] = 0;
        this.coconutscore[1] = 0;
        this.countBombing = 80;
        this.countFalling = 401;
        this.countStopped = 20;
        this.coconuts.clear();
        for (int i = 0; i < 506; ++i) {
            this.coconuts.add(new MapleCoconuts());
        }
    }

    @Override
    public void startEvent() {
        this.reset();
        this.setHittable(true);
        this.getMap(0).broadcastMessage(MaplePacketCreator.serverNotice(5, "The event has started!!"));
        this.getMap(0).broadcastMessage(MaplePacketCreator.hitCoconut(true, 0, 0));
        this.getMap(0).broadcastMessage(FieldPacket.clock((ClockPacket)ClockPacket.secondsClock((long)300L)));
        Timer.EventTimer.getInstance().schedule(() -> {
            if (this.getMapleScore() == this.getStoryScore()) {
                this.bonusTime();
            } else {
                for (MapleCharacter chr : this.getMap(0).getCharacters()) {
                    if (chr.getTeam() == (this.getMapleScore() > this.getStoryScore() ? 0 : 1)) {
                        chr.send(MaplePacketCreator.showEffect("event/coconut/victory"));
                        chr.send(MaplePacketCreator.playSound("Coconut/Victory"));
                        continue;
                    }
                    chr.send(MaplePacketCreator.showEffect("event/coconut/lose"));
                    chr.send(MaplePacketCreator.playSound("Coconut/Failed"));
                }
                this.warpOut();
            }
        }, 300000L);
    }

    public void bonusTime() {
        this.getMap(0).broadcastMessage(FieldPacket.clock((ClockPacket)ClockPacket.secondsClock((long)60L)));
        Timer.EventTimer.getInstance().schedule(() -> {
            if (this.getMapleScore() == this.getStoryScore()) {
                for (MapleCharacter chr : this.getMap(0).getCharacters()) {
                    chr.send(MaplePacketCreator.showEffect("event/coconut/lose"));
                    chr.send(MaplePacketCreator.playSound("Coconut/Failed"));
                }
                this.warpOut();
            } else {
                for (MapleCharacter chr : this.getMap(0).getCharacters()) {
                    if (chr.getTeam() == (this.getMapleScore() > this.getStoryScore() ? 0 : 1)) {
                        chr.send(MaplePacketCreator.showEffect("event/coconut/victory"));
                        chr.send(MaplePacketCreator.playSound("Coconut/Victory"));
                        continue;
                    }
                    chr.send(MaplePacketCreator.showEffect("event/coconut/lose"));
                    chr.send(MaplePacketCreator.playSound("Coconut/Failed"));
                }
                this.warpOut();
            }
        }, 60000L);
    }

    public void warpOut() {
        this.setHittable(false);
        Timer.EventTimer.getInstance().schedule(() -> {
            for (MapleCharacter chr : this.getMap(0).getCharacters()) {
                if (this.getMapleScore() > this.getStoryScore() && chr.getTeam() == 0 || this.getStoryScore() > this.getMapleScore() && chr.getTeam() == 1) {
                    MapleCoconut.givePrize(chr);
                }
                this.warpBack(chr);
            }
            this.unreset();
        }, 10000L);
    }
    public static class MapleCoconuts {

        private int hits = 0;
        private boolean hittable = false;
        private boolean stopped = false;
        private long hittime = System.currentTimeMillis();

        public void hit() {
            this.hittime = System.currentTimeMillis() + 1000; // test
            hits++;
        }

        public int getHits() {
            return hits;
        }

        public void resetHits() {
            hits = 0;
        }

        public boolean isHittable() {
            return hittable;
        }

        public void setHittable(boolean hittable) {
            this.hittable = hittable;
        }

        public boolean isStopped() {
            return stopped;
        }

        public void setStopped(boolean stopped) {
            this.stopped = stopped;
        }

        public long getHitTime() {
            return hittime;
        }
    }
}

