/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Net.server.movement.MovementAngle
 *  Net.server.movement.MovementFlyingBlock
 *  Net.server.movement.MovementNew1
 *  Net.server.movement.MovementOffsetX
 */
package Server.channel.handler;

import Net.server.maps.AnimatedMapleMapObject;
import Net.server.movement.LifeMovement;
import Net.server.movement.LifeMovementFragment;
import Net.server.movement.MovementAngle;
import Net.server.movement.MovementBase;
import Net.server.movement.MovementFlyingBlock;
import Net.server.movement.MovementJump;
import Net.server.movement.MovementNew1;
import Net.server.movement.MovementNew2;
import Net.server.movement.MovementNormal;
import Net.server.movement.MovementOffsetX;
import Net.server.movement.MovementStartFallDown;
import Net.server.movement.MovementStatChange;
import Net.server.movement.MovementTeleport;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.data.MaplePacketReader;

public class MovementParse {
    public static final Logger log = LoggerFactory.getLogger("Movement");

    public static List<LifeMovementFragment> parseMovement(MaplePacketReader slea, int kind) {
        ArrayList<LifeMovementFragment> res = new ArrayList<LifeMovementFragment>();
        int numCommands = slea.readShort();
        String packet = slea.toString(true);
        try {
            block15: for (int i = 0; i < numCommands; ++i) {
                byte command = slea.readByte();
                switch (command) {
                    case 0: 
                    case 8: 
                    case 15: 
                    case 17: 
                    case 19: 
                    case 72: 
                    case 73: 
                    case 74: 
                    case 75: 
                    case 76: 
                    case 77: 
                    case 94: 
                    case 108: {
                        short x = slea.readShort();
                        short y = slea.readShort();
                        short vx = slea.readShort();
                        short vy = slea.readShort();
                        short fh = slea.readShort();
                        short footStart = command == 15 || command == 17 ? slea.readShort() : (short)0;
                        short xoffset = slea.readShort();
                        short yoffset = slea.readShort();
                        short unk1 = slea.readShort();
                        byte moveAction = slea.readByte();
                        short elapse = slea.readShort();
                        byte forcedStop = slea.readByte();
                        MovementNormal mn = new MovementNormal(command, elapse, moveAction, forcedStop);
                        mn.setPosition(new Point(x, y));
                        mn.setPixelsPerSecond(new Point(vx, vy));
                        mn.setFH(fh);
                        mn.setFootStart(footStart);
                        mn.setOffset(new Point(xoffset, yoffset));
                        mn.setUnk1(unk1);
                        res.add(mn);
                        continue block15;
                    }
                    case 1: 
                    case 2: 
                    case 18: 
                    case 21: 
                    case 22: 
                    case 24: 
                    case 62: 
                    case 65: 
                    case 66: 
                    case 67: 
                    case 68: 
                    case 69: 
                    case 70: 
                    case 99: {
                        short vx = slea.readShort();
                        short vy = slea.readShort();
                        short footStart = 0;
                        if (command == 21 || command == 22) {
                            footStart = slea.readShort();
                        }
                        short xoffset = 0;
                        short yoffset = 0;
                        if (command == 62) {
                            xoffset = slea.readShort();
                            yoffset = slea.readShort();
                        }
                        byte moveAction = slea.readByte();
                        short elapse = slea.readShort();
                        byte forcedStop = slea.readByte();
                        MovementJump mj = new MovementJump(command, elapse, moveAction, forcedStop);
                        mj.setPixelsPerSecond(new Point(vx, vy));
                        mj.setFootStart(footStart);
                        mj.setOffset(new Point(xoffset, yoffset));
                        res.add(mj);
                        continue block15;
                    }
                    case 3: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 7: 
                    case 9: 
                    case 10: 
                    case 11: 
                    case 13: 
                    case 26: 
                    case 27: 
                    case 53: 
                    case 54: 
                    case 55: 
                    case 59: 
                    case 83: 
                    case 84: 
                    case 85: 
                    case 87: 
                    case 89: 
                    case 111: {
                        short x = slea.readShort();
                        short y = slea.readShort();
                        short fh = slea.readShort();
                        int unk2 = slea.readInt();
                        byte moveAction = slea.readByte();
                        short elapse = slea.readShort();
                        byte forcedStop = slea.readByte();
                        MovementTeleport mt = new MovementTeleport(command, elapse, moveAction, forcedStop);
                        mt.setPosition(new Point(x, y));
                        mt.setFH(fh);
                        mt.setUnk2(unk2);
                        res.add(mt);
                        continue block15;
                    }
                    case 12: {
                        res.add(new MovementStatChange(command, slea.readByte()));
                        continue block15;
                    }
                    case 14: 
                    case 16: {
                        short fh = slea.readShort();
                        short vx = slea.readShort();
                        short vy = slea.readShort();
                        byte moveAction = slea.readByte();
                        short elapse = slea.readShort();
                        byte forcedStop = slea.readByte();
                        MovementStartFallDown msfd = new MovementStartFallDown(command, elapse, moveAction, forcedStop);
                        msfd.setFH(fh);
                        msfd.setPixelsPerSecond(new Point(vx, vy));
                        res.add(msfd);
                        continue block15;
                    }
                    case 23: 
                    case 102: 
                    case 103: {
                        short x = slea.readShort();
                        short y = slea.readShort();
                        short vx = slea.readShort();
                        short vy = slea.readShort();
                        byte moveAction = slea.readByte();
                        short elapse = slea.readShort();
                        byte forcedStop = slea.readByte();
                        MovementFlyingBlock um = new MovementFlyingBlock((int)command, (int)elapse, (int)moveAction, forcedStop);
                        um.setPosition(new Point(x, y));
                        um.setPixelsPerSecond(new Point(vx, vy));
                        res.add((LifeMovementFragment)um);
                        continue block15;
                    }
                    case 29: {
                        int unk3 = slea.readInt();
                        byte moveAction = slea.readByte();
                        short elapse = slea.readShort();
                        byte forcedStop = slea.readByte();
                        MovementNew1 mn1 = new MovementNew1((int)command, (int)elapse, (int)moveAction, forcedStop);
                        mn1.setUnk3(unk3);
                        res.add((LifeMovementFragment)mn1);
                        continue block15;
                    }
                    case 30: 
                    case 42: {
                        int unk2 = slea.readInt();
                        byte moveAction = slea.readByte();
                        short elapse = slea.readShort();
                        byte forcedStop = slea.readByte();
                        MovementNew2 mn2 = new MovementNew2(command, elapse, moveAction, forcedStop);
                        mn2.setUnk2(unk2);
                        res.add(mn2);
                        continue block15;
                    }
                    case 31: 
                    case 32: 
                    case 33: 
                    case 34: 
                    case 35: 
                    case 36: 
                    case 37: 
                    case 38: 
                    case 39: 
                    case 40: 
                    case 41: 
                    case 43: 
                    case 44: 
                    case 45: 
                    case 46: 
                    case 47: 
                    case 48: 
                    case 49: 
                    case 51: 
                    case 52: 
                    case 56: 
                    case 58: 
                    case 60: 
                    case 61: 
                    case 63: 
                    case 64: 
                    case 78: 
                    case 79: 
                    case 81: 
                    case 86: 
                    case 88: 
                    case 90: 
                    case 91: 
                    case 92: 
                    case 93: 
                    case 95: 
                    case 96: 
                    case 97: 
                    case 98: 
                    case 100: 
                    case 101: 
                    case 104: 
                    case 105: 
                    case 106: 
                    case 107: {
                        byte moveAction = slea.readByte();
                        short elapse = slea.readShort();
                        byte forcedStop = slea.readByte();
                        res.add(new MovementBase(command, moveAction, elapse, forcedStop));
                        continue block15;
                    }
                    case 50: {
                        short x = slea.readShort();
                        short y = slea.readShort();
                        short vx = slea.readShort();
                        short vy = slea.readShort();
                        short xoffset = slea.readShort();
                        byte moveAction = slea.readByte();
                        short elapse = slea.readShort();
                        byte forcedStop = slea.readByte();
                        MovementOffsetX mox = new MovementOffsetX((int)command, (int)elapse, (int)moveAction, forcedStop);
                        mox.setPosition(new Point(x, y));
                        mox.setPixelsPerSecond(new Point(vx, vy));
                        mox.setOffset(new Point(xoffset, 0));
                        res.add((LifeMovementFragment)mox);
                        continue block15;
                    }
                    case 57: 
                    case 71: 
                    case 110: {
                        short x = slea.readShort();
                        short y = slea.readShort();
                        short vx = slea.readShort();
                        short vy = slea.readShort();
                        short fh = slea.readShort();
                        byte moveAction = slea.readByte();
                        short elapse = slea.readShort();
                        byte forcedStop = slea.readByte();
                        MovementAngle ma = new MovementAngle((int)command, (int)elapse, (int)moveAction, forcedStop);
                        ma.setPosition(new Point(x, y));
                        ma.setPixelsPerSecond(new Point(vx, vy));
                        ma.setFH(fh);
                        res.add((LifeMovementFragment)ma);
                        continue block15;
                    }
                    default: {
                        byte moveAction = slea.readByte();
                        short elapse = slea.readShort();
                        byte forcedStop = slea.readByte();
                        res.add(new MovementBase(command, moveAction, elapse, forcedStop));
                        continue block15;
                    }
                }
            }
            byte bVal = slea.readByte();
            slea.skip(bVal >> 1);
            if ((bVal & 1) != 0) {
                slea.skip(1);
            }
            if (numCommands != res.size()) {
                log.error(MovementParse.getKindName(kind) + " 循環次數[" + numCommands + "]和實際上獲取的循環次數[" + res.size() + "]不符" + packet);
                return null;
            }
            return res;
        }
        catch (Exception e) {
            log.error(MovementParse.getKindName(kind) + "封包解析出錯：" + packet, e);
            return null;
        }
    }

    public static void updatePosition(List<LifeMovementFragment> movement, AnimatedMapleMapObject target, int yoffset) {
        if (movement == null) {
            return;
        }
        int lastMoveTime = 0;
        for (LifeMovementFragment move : movement) {
            if (!(move instanceof LifeMovement)) continue;
            if (move instanceof MovementNormal) {
                Point position = ((MovementNormal)move).getPosition();
                position.y += yoffset;
                target.setPosition(position);
                target.setHomeFH(target.getCurrentFH());
                target.setCurrentFh(((MovementNormal)move).getFH());
            }
            target.setStance(((LifeMovement)move).getMoveAction());
            lastMoveTime += ((LifeMovement)move).getElapse();
        }
        target.setLastMoveTime(lastMoveTime);
    }

    public static String getKindName(int kind) {
        return switch (kind) {
            case 1 -> "玩家";
            case 2 -> "怪物";
            case 3 -> "寵物";
            case 4 -> "召喚獸";
            case 5 -> "寶貝龍";
            case 6 -> "萌獸";
            case 7 -> "花狐";
            case 8 -> "人型花狐";
            case 9 -> "機器人";
            case 10 -> "NPC";
            default -> "未知kind";
        };
    }
}

