/*
 * Decompiled with CFR 0.152.
 */
package Net.server.events;

import Client.MapleCharacter;
import Net.server.Timer;
import Net.server.events.MapleEvent;
import Net.server.events.MapleEventType;
import Net.server.maps.MapleMap;
import Packet.MaplePacketCreator;
import java.util.concurrent.ScheduledFuture;

public class MapleSnowball
extends MapleEvent {
    private final MapleSnowballs[] balls = new MapleSnowballs[2];

    public MapleSnowball(int channel, MapleEventType type) {
        super(channel, type);
    }

    @Override
    public void finished(MapleCharacter chr) {
    }

    @Override
    public void unreset() {
        super.unreset();
        for (int i = 0; i < 2; ++i) {
            this.getSnowBall(i).resetSchedule();
            this.resetSnowBall(i);
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.makeSnowBall(0);
        this.makeSnowBall(1);
    }

    @Override
    public void startEvent() {
        for (int i = 0; i < 2; ++i) {
            MapleSnowballs ball = this.getSnowBall(i);
            ball.broadcast(this.getMap(0), 0);
            ball.setInvis(false);
            ball.broadcast(this.getMap(0), 5);
            this.getMap(0).broadcastMessage(MaplePacketCreator.enterSnowBall());
        }
    }

    public void resetSnowBall(int teamz) {
        this.balls[teamz] = null;
    }

    public void makeSnowBall(int teamz) {
        this.resetSnowBall(teamz);
        this.balls[teamz] = new MapleSnowballs(teamz);
    }

    public MapleSnowballs getSnowBall(int teamz) {
        return this.balls[teamz];
    }

    public static class MapleSnowballs {
        private final int team;
        private int position = 0;
        private int startPoint = 0;
        private boolean invis = true;
        private boolean hittable = true;
        private int snowmanhp = 7500;
        private ScheduledFuture<?> snowmanSchedule = null;

        public MapleSnowballs(int team_) {
            this.team = team_;
        }

        public static void hitSnowball(MapleCharacter chr) {
            int team = chr.getPosition().y > -80 ? 0 : 1;
            MapleSnowball sb = (MapleSnowball)chr.getClient().getChannelServer().getEvent(MapleEventType.Snowball);
            MapleSnowballs ball = sb.getSnowBall(team);
            if (ball != null && !ball.isInvis()) {
                boolean snowman;
                boolean bl = snowman = chr.getPosition().x < -360 && chr.getPosition().x > -560;
                if (!snowman) {
                    int damage = (Math.random() < 0.01 || chr.getPosition().x > ball.getLeftX() && chr.getPosition().x < ball.getRightX()) && ball.isHittable() ? 10 : 0;
                    chr.getMap().broadcastMessage(MaplePacketCreator.hitSnowBall(team, damage, 0, 1));
                    if (damage == 0) {
                        if (Math.random() < 0.2) {
                            chr.send(MaplePacketCreator.leftKnockBack());
                            chr.getClient().sendEnableActions();
                        }
                    } else {
                        ball.setPositionX(ball.getPosition() + 1);
                        if (ball.getPosition() == 255 || ball.getPosition() == 511 || ball.getPosition() == 767) {
                            ball.setStartPoint(chr.getMap());
                            chr.getMap().broadcastMessage(MaplePacketCreator.rollSnowball(4, sb.getSnowBall(0), sb.getSnowBall(1)));
                        } else if (ball.getPosition() == 899) {
                            MapleMap map = chr.getMap();
                            for (int i = 0; i < 2; ++i) {
                                sb.getSnowBall(i).setInvis(true);
                                map.broadcastMessage(MaplePacketCreator.rollSnowball(i + 2, sb.getSnowBall(0), sb.getSnowBall(1)));
                            }
                            chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "Congratulations! Team " + (team == 0 ? "Story" : "Maple") + " has won the Snowball Event!"));
                            for (MapleCharacter chrz : chr.getMap().getCharacters()) {
                                if (team == 0 && chrz.getPosition().y > -80 || team == 1 && chrz.getPosition().y <= -80) {
                                    MapleEvent.givePrize(chrz);
                                }
                                sb.warpBack(chrz);
                            }
                            sb.unreset();
                        } else if (ball.getPosition() < 899) {
                            chr.getMap().broadcastMessage(MaplePacketCreator.rollSnowball(4, sb.getSnowBall(0), sb.getSnowBall(1)));
                            ball.setInvis(false);
                        }
                    }
                } else if (ball.getPosition() < 899) {
                    int damage = 15;
                    if (Math.random() < 0.3) {
                        damage = 0;
                    }
                    if (Math.random() < 0.05) {
                        damage = 45;
                    }
                    chr.getMap().broadcastMessage(MaplePacketCreator.hitSnowBall(team + 2, damage, 0, 0));
                    ball.setSnowmanHP(ball.getSnowmanHP() - damage);
                    if (damage > 0) {
                        chr.getMap().broadcastMessage(MaplePacketCreator.rollSnowball(0, sb.getSnowBall(0), sb.getSnowBall(1)));
                        if (ball.getSnowmanHP() <= 0) {
                            ball.setSnowmanHP(7500);
                            MapleSnowballs oBall = sb.getSnowBall(team == 0 ? 1 : 0);
                            oBall.setHittable(false);
                            MapleMap map = chr.getMap();
                            oBall.broadcast(map, 4);
                            oBall.snowmanSchedule = Timer.EventTimer.getInstance().schedule(() -> {
                                oBall.setHittable(true);
                                oBall.broadcast(map, 5);
                            }, 10000L);
                        }
                    }
                }
            }
        }

        public void resetSchedule() {
            if (this.snowmanSchedule != null) {
                this.snowmanSchedule.cancel(false);
                this.snowmanSchedule = null;
            }
        }

        public int getTeam() {
            return this.team;
        }

        public int getPosition() {
            return this.position;
        }

        public void setPositionX(int pos) {
            this.position = pos;
        }

        public void setStartPoint(MapleMap map) {
            ++this.startPoint;
            this.broadcast(map, this.startPoint);
        }

        public boolean isInvis() {
            return this.invis;
        }

        public void setInvis(boolean i) {
            this.invis = i;
        }

        public boolean isHittable() {
            return this.hittable && !this.invis;
        }

        public void setHittable(boolean b) {
            this.hittable = b;
        }

        public int getSnowmanHP() {
            return this.snowmanhp;
        }

        public void setSnowmanHP(int shp) {
            this.snowmanhp = shp;
        }

        public void broadcast(MapleMap map, int message) {
            for (MapleCharacter chr : map.getCharacters()) {
                chr.send(MaplePacketCreator.snowballMessage(this.team, message));
            }
        }

        public int getLeftX() {
            return this.position * 3 + 175;
        }

        public int getRightX() {
            return this.getLeftX() + 275;
        }
    }
}

