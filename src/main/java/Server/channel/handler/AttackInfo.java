/*
 * Decompiled with CFR 0.152.
 */
package Server.channel.handler;

import Net.server.movement.LifeMovementFragment;
import Opcode.header.OutHeader;
import Server.channel.handler.AttackMobInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import tools.Pair;

public class AttackInfo {
    public int skill;
    public int charge = 0;
    public int lastAttackTickCount;
    public List<AttackMobInfo> allDamage;
    public Point position;
    public Point chain;
    public Point plusPosition = new Point();
    public Point plusPosition2;
    public Point plusPosition3;
    public Point attackPosition;
    public int display = 0;
    public Point attackPosition2;
    public Point attackPosition3;
    public Rectangle acrossPosition;
    public int facingleft = 0;
    public int count = 0;
    public int subAttackType;
    public int subAttackUnk;
    public byte hits;
    public byte targets;
    public byte tbyte = 0;
    public byte speed = 0;
    public byte animation;
    public byte plusPos;
    public short AOE;
    public short slot;
    public short csstar;
    public boolean real = true;
    public boolean across = false;
    public boolean Aiming = false;
    public byte attacktype = 0;
    public boolean isLink = false;
    public byte isBuckShot = 0;
    public byte isShadowPartner = 0;
    public byte nMoveAction = (byte)-1;
    public byte rlType;
    public byte bShowFixedDamage = 0;
    public int item;
    public int skilllevel = 0;
    public int asist;
    public int summonattack;
    public List<Point> mistPoints = new ArrayList<Point>();
    public List<Pair<Integer, Integer>> attackObjects = new ArrayList<Pair<Integer, Integer>>();
    public int skillId;
    public boolean move = false;
    public boolean passive = false;
    public Point skillposition;
    public int direction;
    public int stance;
    public short starSlot;
    public short cashSlot;
    public byte mobCount;
    public byte numAttackedAndDamage;
    public byte unk;
    public byte zeroUnk;
    public byte ef;
    public int skllv;
    public List<LifeMovementFragment> movei;
    public boolean isCloseRangeAttack = false;
    public boolean isRangedAttack = false;
    public boolean isMagicAttack = false;
    public int unInt1;
    public Point rangedAttackPos;
    public AttackType attackType;
    public int summonMobCount;
    public int raytheonPike;
    public OutHeader attackHeader;
    public boolean boxAttack;
    public byte fieldKey;
    public byte addAttackProc;
    public byte zero;
    public int grenadeId;
    public int keyDown;
    public int bySummonedID;
    public byte buckShot;
    public int extraData;
    public byte someMask;
    public boolean isJablin;
    public boolean left;
    public short attackAction;
    public int requestTime;
    public byte attackActionType;
    public byte idk0;
    public byte attackSpeed;
    public int tick;
    public int finalAttackLastSkillID;
    public byte finalAttackByte;
    public boolean ignorePCounter;
    public int spiritCoreEnhance;
    public final List<AttackMobInfo> mobAttackInfo = new ArrayList<AttackMobInfo>();
    public Point ptTarget = new Point();
    public short x;
    public short y;
    public short forcedX;
    public short forcedY;
    public short rcDstRight;
    public short rectRight;
    public int option;
    public int[] mists;
    public byte force;
    public short forcedXSh;
    public short forcedYSh;
    public short[] shortArr;
    public short delay;
    public Point ptAttackRefPoint = new Point();
    public Point idkPos = new Point();
    public Point pos = new Point();
    public byte fh;
    public Point keyDownRectMoveXY;
    public Point teleportPt = new Point();
    public Point bodyRelMove;
    public short Vx;
    public Point grenadePos = new Point();
    public List<Pair<Integer, Point>> skillSpawnInfo = new LinkedList<Pair<Integer, Point>>();
    public List<Pair<Integer, Integer>> skillTargetList = new LinkedList<Pair<Integer, Integer>>();
    public Rectangle rect = new Rectangle();

    public static enum AttackType {
        MeleeAttack,
        BodyAttack,
        AreaDotAttack,
        MagicAttack,
        ShootAttack,
        SummonedAttack;

    }
}

