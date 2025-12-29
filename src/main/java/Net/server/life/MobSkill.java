/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.status.MonsterStatusEffect
 *  Config.constants.enums.MonsterSkillType
 *  Net.server.life.MobSkill$1
 *  Net.server.life.MobSkill$2
 *  Packet.CField
 *  Packet.WillPacket
 *  Server.BossEventHandler.Angel
 *  Server.BossEventHandler.Belem
 *  Server.BossEventHandler.Seren
 *  SwordieX.field.fieldeffect.FieldEffect
 *  connection.packet.FieldPacket
 */
package Net.server.life;

import Client.MapleCharacter;
import Client.MapleClient;
import Client.MonsterEffectHolder;
import Client.SecondaryStat;
import Client.status.MonsterStatus;
import Client.status.MonsterStatusEffect;
import Config.configs.Config;
import Config.constants.GameConstants;
import Config.constants.enums.MonsterSkillType;
import Net.server.Timer;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.BanishInfo;
import Net.server.life.MapleLifeFactory;
import Net.server.life.MapleMonster;
import Net.server.life.MobSkill;
import Net.server.life.MobSkillFactory;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Opcode.header.OutHeader;
import Packet.CField;
import Packet.MobPacket;
import Packet.WillPacket;
import Server.BossEventHandler.Angel;
import Server.BossEventHandler.Belem;
import Server.BossEventHandler.Seren;
import Server.BossEventHandler.spider.spider;
import SwordieX.field.fieldeffect.FieldEffect;
import connection.packet.FieldPacket;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tools.Pair;
import tools.Randomizer;
import tools.Triple;
import tools.data.MaplePacketLittleEndianWriter;

public class MobSkill
extends MapleStatEffect {
    private final int skillId;
    private final int skillLevel;
    private int mobMpCon;
    private int summonEffect;
    private int mobHp;
    private int x;
    private int y;
    private int spawnEffect;
    int sandCount;
    private long duration;
    private long cooltime;
    private float prop;
    private short limit;
    private List<Integer> toSummon = new ArrayList<Integer>();
    private Point lt;
    private Point rb;
    private Point lt2;
    private Point rb2;
    private boolean summonOnce;
    private int areaSequenceDelay;
    private int skillAfter;
    private int force;
    private int forcex;
    private int w;
    private int z;
    private int emotion = -1;
    Map<SecondaryStat, Pair<Integer, Integer>> diseases = new EnumMap<SecondaryStat, Pair<Integer, Integer>>(SecondaryStat.class);
    List<SecondaryStat> cancels = new ArrayList<SecondaryStat>();
    List<Pair<MonsterStatus, MonsterStatusEffect>> stats = new ArrayList<Pair<MonsterStatus, MonsterStatusEffect>>();
    boolean allchr = false;
    List<Triple<Point, Integer, List<Rectangle>>> datas;
    private List<Point> fixedPos = new ArrayList<Point>();
    private int afterAttackCount;

    public MobSkill(int skillId, int level) {
        this.skillId = skillId;
        this.skillLevel = level;
    }

    public void setAfterAttackCount(int afterAttackCount) {
        this.afterAttackCount = afterAttackCount;
    }

    public void setSummonOnce(boolean o) {
        this.summonOnce = o;
    }

    public boolean isSummonOnce() {
        return this.summonOnce;
    }

    public void setSummons(List<Integer> toSummon) {
        this.toSummon = toSummon;
    }

    public void setMobHp(int mobHp) {
        this.mobHp = mobHp;
    }

    public int getSkillId() {
        return this.skillId;
    }

    @Override
    public int getProp() {
        return (int)(this.prop * 100.0f);
    }

    public void setProp(float prop) {
        this.prop = prop;
    }

    public void setLtRb(Point lt, Point rb) {
        this.lt = lt;
        this.rb = rb;
    }

    public void setLtRb2(Point lt2, Point rb2) {
        this.lt2 = lt2;
        this.rb2 = rb2;
    }

    public boolean checkCurrentBuff(MapleCharacter player, MapleMonster monster) {
        boolean stop = false;
        switch (this.skillId) {
            case 100: 
            case 110: 
            case 150: {
                stop = monster.isBuffed(MonsterStatus.PowerUp);
                break;
            }
            case 101: 
            case 111: 
            case 151: {
                stop = monster.isBuffed(MonsterStatus.MagicUp);
                break;
            }
            case 102: 
            case 112: 
            case 152: {
                stop = monster.isBuffed(MonsterStatus.PGuardUp);
                break;
            }
            case 103: 
            case 113: 
            case 153: {
                stop = monster.isBuffed(MonsterStatus.MGuardUp);
                break;
            }
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: {
                stop = monster.isBuffed(MonsterStatus.HardSkin) || monster.isBuffed(MonsterStatus.MImmune) || monster.isBuffed(MonsterStatus.PCounter) || monster.isBuffed(MonsterStatus.PImmune) || monster.isBuffed(MonsterStatus.MCounter) || monster.isBuffed(MonsterStatus.Dazzle) || monster.isBuffed(MonsterStatus.SealSkill);
                break;
            }
            case 200: {
                stop = player.getMap().getMobSizeByID() >= this.limit;
            }
        }
        return stop |= monster.isBuffed(MonsterStatus.MagicCrash);
    }

    public int getSkillLevel() {
        return this.skillLevel;
    }

    public void applyEffect(MapleCharacter player, MapleMonster monster, Integer effectAfter, Object isFacingLeft) {
        boolean b = this.getLt() != null && this.getRb() != null;
        Rectangle rectangle = this.calculateBoundingBox(monster.getPosition(), monster.isFacingLeft());
        Point pos = new Point(monster.getPosition());
        EnumMap stats = new EnumMap(MonsterStatus.class);
        LinkedList reflection = new LinkedList();
        int level = this.getLevel();
        block0 : switch (this.skillId) {
            case 13: {
                monster.getMap().broadcastMessage(MobPacket.setAfterAttack(monster.getObjectId(), 8, 1, player, (player.getLarknessDiraction() & 1) != 0));
                break;
            }
            case 46: {
                monster.getMap().broadcastMessage(MobPacket.AfterAttack(monster, this.skillId, this.skillLevel, (player.getLarknessDiraction() & 1) != 0, 1, 1));
                break;
            }
            case 61: {
                this.x = (player.getLarknessDiraction() & 1) != 0 ? -600 : 600;
                monster.getMap().broadcastMessage(MobPacket.AfterAttack(monster, this.skillId, this.skillLevel, (player.getLarknessDiraction() & 1) != 0, 1, 7));
                monster.getMap().broadcastMessage(MobPacket.TeleportMonster(monster, false, 16, new Point(monster.getPosition().x + this.x, 17)));
                break;
            }
            case 105: {
                if (b) {
                    List<MapleMapObject> objects = monster.getMap().getMapObjectsInRect(rectangle, Collections.singletonList(MapleMapObjectType.MONSTER));
                    for (MapleMapObject mons : objects) {
                        if (!monster.isAlive() || mons.getObjectId() == monster.getObjectId() || ((MapleMonster)mons).isSpongeMob()) continue;
                        player.getMap().killMonster((MapleMonster)mons, player, true, false, (byte)1, 0);
                        monster.healHPMP(this.getX(), this.getY());
                        break block0;
                    }
                    break;
                }
                if (!monster.isAlive() || monster.isSpongeMob()) break;
                monster.healHPMP(this.getX(), this.getY());
                break;
            }
            case 114: {
                if (b) {
                    List<MapleMapObject> objects = monster.getMap().getMapObjectsInRect(rectangle, Collections.singletonList(MapleMapObjectType.MONSTER));
                    for (MapleMapObject mons : objects) {
                        if (!((MapleMonster)mons).isAlive() || ((MapleMonster)mons).isSpongeMob()) continue;
                        ((MapleMonster)mons).healHPMP(this.getX(), this.getY());
                        monster.getMap().broadcastMessage(MobPacket.MobAffected(mons.getObjectId(), this.skillId), mons.getPosition());
                    }
                    break;
                }
                if (!monster.isAlive() || monster.isSpongeMob()) break;
                monster.healHPMP(this.getX(), this.getY());
                break;
            }
            case 127: {
                List<MapleMapObject> objects = monster.getMap().getMapObjectsInRect(rectangle, Collections.singletonList(MapleMapObjectType.PLAYER));
                for (MapleMapObject object : objects) {
                    ((MapleCharacter)object).removeBuffs(true);
                }
                break;
            }
            case 129: {
                BanishInfo info;
                List<BanishInfo> infos = monster.getStats().getBanishInfo();
                if (infos.isEmpty() || (info = infos.get(Randomizer.nextInt(infos.size()))) == null) break;
                for (MapleCharacter chr : this.getPlayersInRange(monster, player)) {
                    if (chr.hasBlockedInventory()) continue;
                    chr.changeMapBanish(info.getMap(), info.getPortal(), info.getMsg());
                }
                break;
            }
            case 131: {
                pos = monster.getMap().getRandomPos(monster.getPosition());
                switch (level) {
                    case 25: 
                    case 26: 
                    case 27: {
                        pos = new Point(400, -30);
                        rectangle = this.calculateBoundingBox(pos);
                    }
                }
                break;
            }
            case 170: {
                switch (level) {
                    case 62: {
                        player.getMap().broadcastMessage(CField.useFieldSkill((int)100007, (int)1));
                        player.getMap().killMonster(8880506);
                        player.getMap().killMonster(8880506);
                        break;
                    }
                    case 63: {
                        player.getMap().broadcastMessage(CField.useFieldSkill((int)100007, (int)2));
                        player.getMap().killMonster(8880506);
                        player.getMap().killMonster(8880506);
                        break;
                    }
                    case 64: {
                        player.getMap().broadcastMessage(CField.useFieldSkill((int)100015, (int)1));
                        player.getMap().killMonster(8880506);
                        player.getMap().killMonster(8880506);
                        break;
                    }
                    case 65: {
                        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                        mplew.writeShort(OutHeader.MONSTER_BARRIER.getValue());
                        mplew.writeInt(monster.getObjectId());
                        mplew.writeInt(100);
                        mplew.writeLong(25000000L);
                        player.send(mplew.getPacket());
                        break;
                    }
                    case 66: {
                        MaplePacketLittleEndianWriter mplew2 = new MaplePacketLittleEndianWriter();
                        mplew2.writeShort(OutHeader.MONSTER_BARRIER_EFFECT.getValue());
                        mplew2.writeInt(monster.getObjectId());
                        mplew2.write(1);
                        mplew2.writeMapleAsciiString("UI/UIWindow8.img/BlackMageShield/mobEffect");
                        mplew2.writeInt(1);
                        mplew2.write(1);
                        mplew2.writeMapleAsciiString("Sound/MobEtc.img/BlackMageShield");
                        mplew2.write(1);
                        mplew2.writeMapleAsciiString("UI/UIWindow8.img/BlackMageShield/mobEffect0");
                        mplew2.writeInt(-1);
                        mplew2.writeShort(0);
                        player.send(mplew2.getPacket());
                        break;
                    }
                    case 81: {
                        player.send(Angel.AngelXLaser((MapleCharacter)player));
                        player.send(Angel.AngelXLaser((MapleCharacter)player));
                    }
                }
                break;
            }
            case 136: {
                switch (level) {
                    case 26: {
                        player.getMap().broadcastMessage(CField.useFieldSkill((int)100012, (int)1));
                        player.getMap().showWeatherEffectNotice("破滅之眼狂追敵人", 265, 4000);
                    }
                }
                break;
            }
            case 176: {
                switch (level) {
                    case 25: 
                    case 26: {
                        player.getMap().broadcastMessage(player, MobPacket.MobAttackBlock(monster.getObjectId(), 0), true);
                        player.getMap().broadcastMessage(player, MobPacket.showMobSkillDelay(monster.getObjectId(), this, effectAfter + 100, Collections.emptyList()), true);
                        break;
                    }
                    case 27: {
                        player.getMap().broadcastMessage(player, MobPacket.MobAttackBlock(monster.getObjectId(), 0), true);
                        player.getMap().broadcastMessage(player, MobPacket.showMobSkillDelay(monster.getObjectId(), this, effectAfter + 100, Collections.emptyList()), true);
                        break;
                    }
                    case 70: {
                        player.getMap().showScreenEffect("Skill/MobSkill.img/176/level/40/screen");
                    }
                }
                break;
            }
            case 200: {
                for (Integer mobId : this.getSummons()) {
                    MapleMonster toSpawn = MapleLifeFactory.getMonster(GameConstants.getCustomSpawnID(monster.getId(), mobId));
                    if (toSpawn == null) continue;
                    switch (monster.getMap().getId()) {
                        case 220080001: 
                        case 230040420: {
                            pos = monster.getMap().getRandomPos(monster.getPosition());
                        }
                    }
                    monster.getMap().spawnMonsterWithEffect(toSpawn, this.getSummonEffect(), pos);
                }
                break;
            }
            case 201: {
                if (level == 199) {
                    MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                    mplew.writeShort(OutHeader.LUCID_DO_SKILL.getValue());
                    mplew.writeInt(238);
                    mplew.writeInt(4);
                    int x = Randomizer.rand(1, 360);
                    int y = Randomizer.rand(1, 360);
                    int z = Randomizer.rand(1, 360);
                    mplew.writeInt(3);
                    mplew.writeInt(2);
                    mplew.writeInt(2640);
                    mplew.writeInt(104);
                    mplew.writeInt(x);
                    mplew.writeInt(1);
                    mplew.writeInt(2675);
                    mplew.writeInt(100);
                    mplew.writeInt(y);
                    mplew.writeInt(1);
                    mplew.writeInt(2689);
                    mplew.writeInt(101);
                    mplew.writeInt(z);
                    player.send(mplew.getPacket());
                    effectAfter = 0;
                    int n5 = 0;
                    boolean b3 = false;
                    int n6 = 0;
                    for (Integer summon : this.getSummons()) {
                        n5 += player.getMap().getMobSizeByID(summon);
                    }
                    if (this.getLimit() > 0 && n5 >= this.getLimit()) break;
                    block91: for (Integer toSummon : this.getSummons()) {
                        MapleMonster monster2 = MapleLifeFactory.getMonster(GameConstants.getCustomSpawnID(monster.getId(), toSummon));
                        if (monster2 == null) continue;
                        boolean b4 = true;
                        int n8 = (int)monster.getPosition().getX();
                        int n9 = (int)monster.getPosition().getY();
                        boolean n11 = false;
                        int n12 = 35;
                        switch (toSummon) {
                            case 8950007: 
                            case 8950107: {
                                n8 = -404;
                                n9 = -400;
                                monster2.setStance(2);
                                break;
                            }
                            case 8950003: 
                            case 8950103: {
                                n8 = 423;
                                n9 = -400;
                                break;
                            }
                            case 8950004: 
                            case 8950104: {
                                n8 = 505;
                                n9 = -230;
                                break;
                            }
                            case 8950005: 
                            case 8950105: {
                                n8 = -514;
                                n9 = -230;
                                monster2.setStance(2);
                                break;
                            }
                            case 8900000: 
                            case 8900001: 
                            case 8900002: 
                            case 8900100: 
                            case 8900101: 
                            case 8900102: {
                                effectAfter = 1;
                                break;
                            }
                            case 8920000: 
                            case 8920001: 
                            case 8920002: 
                            case 8920003: 
                            case 8920100: 
                            case 8920101: 
                            case 8920102: 
                            case 8920103: {
                                if (System.currentTimeMillis() - monster.getTransTime() <= 30000L) continue block91;
                                monster2.setTransTime(System.currentTimeMillis());
                                monster.setTransTime(System.currentTimeMillis());
                                effectAfter = 1;
                                break;
                            }
                            default: {
                                if (this.getFixedPos().isEmpty() || this.getFixedPos().size() <= n6) break;
                                n8 = this.getFixedPos().get((int)n6).x;
                                n9 = this.getFixedPos().get((int)n6).y;
                            }
                        }
                        ++n6;
                        if (level >= 213 && level <= 222 || level >= 225 && level <= 228 || level >= 201 && level <= 209 || level == 196 || level == 223) {
                            Point randPoint = monster.getMap().getRandomPoint();
                            n8 = randPoint.x;
                            n9 = randPoint.y;
                        }
                        monster.getMap().spawnMonsterWithEffect(monster2, this.getSummonEffect(), new Point(n8, n9));
                        if (effectAfter == 0) continue;
                        monster2.setHp(monster.getHp());
                        monster2.setCurrentFh(monster.getCurrentFH());
                        monster2.setStance(monster.getStance());
                        if (monster.getEventInstance() != null) continue;
                    }
                    if (effectAfter == 0) break;
                    player.getMap().killMonster(monster, player, false, true, (byte)3, 0);
                    break;
                }
            }
            case 211: 
            case 217: 
            case 227: 
            case 238: 
            case 241: {
                player.send(MobPacket.showMobSkillDelay(monster.getObjectId(), this, effectAfter, Collections.emptyList()));
                break;
            }
            case 226: {
                ArrayList<Rectangle> list = new ArrayList<Rectangle>();
                for (int i = 0; i < 3; ++i) {
                    int z = Randomizer.rand(170, 250);
                    list.add(this.calculateBoundingBox(new Point(monster.getPosition().x + (monster.isFacingLeft() ? -z : z) * (2 * i - 1), monster.getPosition().y), monster.isFacingLeft()));
                }
                player.getClient().announce(MobPacket.showMobSkillDelay(monster.getObjectId(), this, effectAfter, list));
                break;
            }
            case 223: {
                b = false;
                break;
            }
            case 231: {
                List<Rectangle> rectangles = player.getMap().getRandRect();
                rectangles.clear();
                int abs = Math.abs(130);
                for (int j = 0; j < 10; ++j) {
                    int z2 = Randomizer.rand(abs - 10, abs - 10);
                    rectangles.add(this.calculateBoundingBox(new Point(j % 2 == 0 ? -654 + z2 * (j / 2) : 651 - z2 * (j / 2), monster.getPosition().y), monster.isFacingLeft()));
                }
                Collections.shuffle(rectangles);
                player.getMap().broadcastMessage(MobPacket.showMobSkillDelay(monster.getObjectId(), this, this.skillAfter, rectangles));
                Belem.BanBanClockEffect((MapleCharacter)player);
                break;
            }
            case 242: {
                switch (this.skillLevel) {
                    case 1: 
                    case 2: 
                    case 3: {
                        int id = monster.getId() == 8880321 || monster.getId() == 8880322 ? monster.getId() - 18 : (monster.getId() == 8880323 || monster.getId() == 8880324 ? monster.getId() - 22 : (monster.getId() == 8880353 || monster.getId() == 8880354 ? monster.getId() - 12 : monster.getId() - 8));
                        int size = Randomizer.rand(17, 23);
                        List<Triple<Integer, Integer, Integer>> idx;

                        idx = new ArrayList<Triple<Integer, Integer, Integer>>() {
                            {
                                for (int i = 1; i <= size; i++) {
                                    add(new Triple<>(i, (1800 * (1 + (i / 6))), -650 + (130 * Randomizer.rand(1, 9))));
                                }
                            }
                        };
                        player.getMap().broadcastMessage(WillPacket.WillSpiderAttack((int)id, (int)this.skillId, (int)this.skillLevel, (int)0, (List)idx));
                        break;
                    }
                    case 4: {
                        int id = 0;
                        if (monster.getId() == 8880300 || monster.getId() == 8880326) {
                            id = monster.getMap().getId() == 450008150 ? (monster.getTruePosition().y < 0 ? 8880304 : 8880303) : (monster.getTruePosition().y < 0 ? 8880344 : 8880343);
                        } else if (monster.getId() == 8880327 || monster.getId() == 8880328) {
                            id = monster.getId() - 26;
                            if (player.getMapId() == 450008250 || player.getMapId() == 450008850) {
                                int n = id = player.getMapId() == 450008250 ? 8880301 : 8880341;
                                if (Randomizer.nextBoolean()) {
                                    player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, -492, -370}));
                                    player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, -8, -370}));
                                    player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, 501, -370}));
                                } else {
                                    player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, -300, -370}));
                                    player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, 300, -370}));
                                }
                            } else if (player.getMapId() == 450008350 || player.getMapId() == 450008950) {
                                int n = id = player.getMapId() == 450008250 ? 8880302 : 8880342;
                                if (Randomizer.nextBoolean()) {
                                    player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, -300, -400}));
                                } else {
                                    player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, -415, -400}));
                                    player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, 300, -400}));
                                }
                            }
                        } else if (monster.getId() == 8880355 || monster.getId() == 8880356) {
                            id = monster.getId() - 12;
                        }
                        monster.setLastSkillUsed(this.skillId, System.currentTimeMillis(), this.getInterval());
                        if (id != 8880341 && id != 8880342 && id != 8880302 && id != 8880301) {
                            if (Randomizer.nextBoolean()) {
                                player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, -250, -370}));
                                player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, 250, -370}));
                            } else {
                                player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, -470, -440}));
                                player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, 470, -440}));
                            }
                        }
                        if (monster.getTruePosition().y < 0) {
                            player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{1, id, 300, 100, -690, -2634, 695, -2019}));
                            break;
                        }
                        player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{1, id, 300, 100, -690, -455, 695, 500}));
                        break;
                    }
                    case 5: {
                        int type = Randomizer.nextInt(2);
                        monster.getMap().broadcastMessage(FieldPacket.fieldEffect((FieldEffect)FieldEffect.mobHPTagFieldEffect((MapleMonster)monster)));
                        monster.getMap().broadcastMessage(WillPacket.WillSpiderAttack((int)monster.getId(), (int)this.skillId, (int)this.skillLevel, (int)type, null));
                        Timer.MobTimer.getInstance().schedule(() -> {
                            if (monster.getId() == 8880300 || monster.getId() == 8880343) {
                                MapleMonster will1 = monster.getMap().getMobObjectByID(8880303);
                                MapleMonster will2 = monster.getMap().getMobObjectByID(8880304);
                                long hp1 = 0L;
                                long hp2 = 0L;
                                if (will1 != null) {
                                    hp1 = will1.getHp();
                                }
                                if (will2 != null) {
                                    hp2 = will2.getHp();
                                }
                                long newhp = Math.max(hp1, hp2);
                                monster.setHp(newhp);
                                if (will1 != null) {
                                    will1.setHp(newhp);
                                }
                                if (will2 != null) {
                                    will2.setHp(newhp);
                                }
                                monster.getMap().broadcastMessage(WillPacket.setWillHp(monster.getWillHplist(), (MapleMap)monster.getMap(), (int)monster.getId(), (int)(monster.getId() + 3), (int)(monster.getId() + 4)));
                            }
                            ArrayList<MapleMonster> mists = new ArrayList<MapleMonster>();
                            ArrayList<MapleCharacter> targets = new ArrayList<MapleCharacter>();
                            for (MapleMonster mi : monster.getMap().getAllMonster()) {
                                if (mi.getSkills() == null || mi.getSkillSize() != 400031039 && mi.getSkillSize() != 400031040) continue;
                                mists.add(mi);
                            }
                            for (MapleCharacter chr : monster.getMap().getAllCharactersThreadsafe()) {
                                boolean add = true;
                                chr.getClient().getSession().writeAndFlush(CField.showWillEffect((MapleCharacter)chr, (int)1, (int)this.skillId, (int)this.skillLevel));
                                for (MapleMonster mist4 : mists) {
                                    if (!mist4.getBounds().contains(chr.getTruePosition())) continue;
                                    add = false;
                                }
                                if (!add) continue;
                                targets.add(chr);
                                int reduce = 0;
                                if (chr.getEffectForBuffStat(SecondaryStat.IndieAllHitDamR) != null) {
                                    reduce = chr.getBuffedValue(SecondaryStat.IndieAllHitDamR);
                                } else if (chr.getEffectForBuffStat(SecondaryStat.IndieAllHitDamR) != null) {
                                    reduce = -chr.getBuffedValue(SecondaryStat.IndieAllHitDamR).intValue();
                                }
                                if (type == 0 && chr.getTruePosition().y > -455 && chr.getTruePosition().y < 300) {
                                    chr.addHP(-chr.getStat().getCurrentMaxHp() * (long)(100 - reduce) / 100L);
                                    continue;
                                }
                                if (type != 1 || chr.getTruePosition().y <= -2500 || chr.getTruePosition().y >= -1800) continue;
                                chr.addHP(-chr.getStat().getCurrentMaxHp() * (long)(100 - reduce) / 100L);
                            }
                            Timer.MobTimer.getInstance().schedule(() -> {
                                double hppercent = (double)monster.getHp() * 100.0 / (double)monster.getStats().getHp();
                                if (hppercent <= 66.6 && monster.getWillHplist().contains(666)) {
                                    MobSkillFactory.getMobSkill(242, 4).applyEffect(player, monster, this.skillId, isFacingLeft);
                                } else if (hppercent <= 33.3 && monster.getWillHplist().contains(333)) {
                                    MobSkillFactory.getMobSkill(242, 4).applyEffect(player, monster, this.skillId, isFacingLeft);
                                } else if (hppercent <= 0.3 && monster.getWillHplist().contains(3)) {
                                    MobSkillFactory.getMobSkill(242, 4).applyEffect(player, monster, this.skillId, isFacingLeft);
                                }
                            }, 1000L);
                        }, 3000L);
                        break;
                    }
                    case 6: {
                        break;
                    }
                    case 7: {
                        player.getMap().showWeatherEffectNotice("謊言之鏡會扭曲攻擊,請注意於鏡子上出現的裂痕吧", 245, 26000);
                        monster.getMap().broadcastMessage(WillPacket.willUseSpecial());
                        List<Integer> idss;
                        idss = new ArrayList<Integer>() {
                            {
                                for (int i = 0; i < 9; ++i) {
                                    this.add(i);
                                }
                            }
                        };

                        for (MapleMonster mob : monster.getMap().getAllMonster()) {
                            mob.getMap().broadcastMessage(MobPacket.BlockAttack(mob, (List<Integer>)idss));
                            mob.setUseSpecialSkill(true);
                            mob.setSkillForbid(true);
                            mob.setNextSkill(0);
                            mob.setNextSkillLvl(0);
                        }
                        Timer.MobTimer.getInstance().schedule(() -> MobSkillFactory.getMobSkill(242, 14).applyEffect(player, monster, this.getSkillId(), isFacingLeft), 5000L);
                        break;
                    }
                    case 8: {
                        monster.getMap().broadcastMessage(WillPacket.willStun());
                        player.getMap().showWeatherEffectNotice("就是現在,需要在威口無防備的狀態下攻擊.", 245, 12000);
                        monster.setNextSkill(0);
                        monster.setNextSkillLvl(0);
                        for (MapleMonster mist4 : monster.getMap().getAllMonster()) {
                            if (mist4.getSkillSize() != 242) continue;
                            monster.getMap().broadcastMessage(CField.removeMist((MapleMonster)mist4));
                            monster.getMap().removeMapObject(mist4);
                        }
                        if (monster.getId() == 8880300 || monster.getId() == 8880343) {
                            MapleMonster will2;
                            MapleMonster will1 = monster.getMap().getMobObjectByID(monster.getId() + 3);
                            if (will1 != null) {
                                monster.getMap().broadcastMessage(MobPacket.forcedSkillAction(will1.getObjectId(), 3, false));
                            }
                            if ((will2 = monster.getMap().getMobObjectByID(monster.getId() + 4)) != null) {
                                monster.getMap().broadcastMessage(MobPacket.forcedSkillAction(will2.getObjectId(), 3, false));
                            }
                        } else {
                            monster.getMap().broadcastMessage(MobPacket.forcedSkillAction(monster.getObjectId(), 2, false));
                        }
                        Timer.MapTimer.getInstance().schedule(() -> {
                            for (MapleMonster mob : monster.getMap().getAllMonster()) {
                                mob.getMap().broadcastMessage(MobPacket.BlockAttack(mob, new ArrayList<Integer>()));
                                mob.setSkillForbid(false);
                                mob.setUseSpecialSkill(false);
                            }
                        }, 10000L);
                        break;
                    }
                    case 9: {
                        this.diseases.put(SecondaryStat.BossWill_Infection, new Pair<Integer, Integer>(1, 7000));
                        player.setSkillCustomInfo(24219, Randomizer.rand(1, Integer.MAX_VALUE), 0L);
                        player.setSkillCustomInfo(24209, 1L, 0L);
                        player.setSkillCustomInfo(24220, 0L, 3000L);
                        player.getMap().broadcastMessage(WillPacket.posion((MapleCharacter)player, (int)((int)player.getSkillCustomValue0(24219)), (int)0, (int)0, (int)0));
                        Timer.MapTimer.getInstance().schedule(() -> {
                            if (player.getSkillCustomValue0(24219) > 0L) {
                                player.getMap().broadcastMessage(WillPacket.removePoison((MapleCharacter)player, (int)((int)player.getSkillCustomValue0(24219))));
                            }
                        }, 7000L);
                        break;
                    }
                    case 10: {
                        player.getMap().broadcastMessage(WillPacket.WillSpiderAttack((int)monster.getId(), (int)this.skillId, (int)this.skillLevel, (int)1, null));
                        break;
                    }
                    case 11: {
                        player.getMap().broadcastMessage(WillPacket.WillSpiderAttack((int)monster.getId(), (int)this.skillId, (int)this.skillLevel, (int)1, null));
                        break;
                    }
                    case 12: {
                        player.modifyMoonlightValue(4);
                        int id = 0;
                        if (monster.getId() == 8880325 || monster.getId() == 8880326) {
                            id = monster.getMap().getId() == 450008150 ? (monster.getTruePosition().y < 0 ? 8880304 : 8880303) : (monster.getTruePosition().y < 0 ? 8880344 : 8880343);
                        } else if (monster.getId() == 8880327 || monster.getId() == 8880328) {
                            id = monster.getId() - 26;
                            if (player.getMapId() == 450008250 || player.getMapId() == 450008850) {
                                int n = id = player.getMapId() == 450008250 ? 8880301 : 8880341;
                                if (Randomizer.nextBoolean()) {
                                    player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, -492, -370}));
                                    player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, -8, -370}));
                                    player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, 501, -370}));
                                } else {
                                    player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, -300, -370}));
                                    player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, 300, -370}));
                                }
                            } else if (player.getMapId() == 450008350 || player.getMapId() == 450008950) {
                                int n = id = player.getMapId() == 450008250 ? 8880302 : 8880342;
                                if (Randomizer.nextBoolean()) {
                                    player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, -300, -400}));
                                } else {
                                    player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, -415, -400}));
                                    player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, 300, -400}));
                                }
                            }
                        } else if (monster.getId() == 8880355 || monster.getId() == 8880356) {
                            id = monster.getId() - 12;
                        }
                        monster.setLastSkillUsed(this.skillId, System.currentTimeMillis(), this.getInterval());
                        if (id != 8880341 && id != 8880342 && id != 8880302 && id != 8880301) {
                            if (Randomizer.nextBoolean()) {
                                player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, -250, -370}));
                                player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, 250, -370}));
                            } else {
                                player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, -470, -440}));
                                player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{0, id, 470, -440}));
                            }
                        }
                        if (monster.getTruePosition().y < 0) {
                            player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{1, id, 300, 100, -690, -2634, 695, -2019}));
                            break;
                        }
                        player.getMap().broadcastMessage(WillPacket.createBulletEyes((int[])new int[]{1, id, 300, 100, -690, -455, 695, 500}));
                        monster.getMap().broadcastMessage(WillPacket.setWillHp(monster.getWillHplist(), (MapleMap)monster.getMap(), (int)((MapleMonster)player.getMap().getMonsters().getFirst()).getId(), (int)(((MapleMonster)player.getMap().getMonsters().getFirst()).getId() + 1), (int)(((MapleMonster)player.getMap().getMonsters().getFirst()).getId() - 3)));
                        break;
                    }
                    case 13: {
                        int webSize = monster.getMap().getAllspider().size();
                        ArrayList<Integer> a2 = new ArrayList<Integer>();
                        if (webSize >= 67) break;
                        int i5 = 0;
                        boolean respawn = false;
                        for (spider web : monster.getMap().getAllspider()) {
                            a2.add(web.getNum());
                        }
                        Collections.sort(a2);
                        for (Integer liw : a2) {
                            if (liw != i5) {
                                monster.getMap().spawnspider(new spider());
                                respawn = true;
                                break;
                            }
                            ++i5;
                        }
                        if (i5 >= 67 || respawn) break;
                        monster.getMap().spawnspider(new spider());
                        break;
                    }
                    case 14: {
                        if (monster.getId() != 8880301 && monster.getId() != 8880341) {
                            return;
                        }
                        monster.setWillSpecialPattern(true);
                        int a = Randomizer.rand(0, 1);
                        this.willSpider(monster.getController(), monster, a, false);
                        Timer.MapTimer.getInstance().schedule(() -> this.willSpider(monster.getController(), monster, a == 0 ? 1 : 0, true), 11000L);
                        Timer.MapTimer.getInstance().schedule(() -> {
                            if (monster.isWillSpecialPattern()) {
                                MobSkillFactory.getMobSkill(242, 8).applyEffect(player, monster, this.skillId, isFacingLeft);
                                if (monster.getHPPercent() <= 50 && monster.getWillHplist().contains(500)) {
                                    monster.setWillHplist(new ArrayList<Integer>());
                                    monster.getWillHplist().add(3);
                                } else if (monster.getHPPercent() <= 3 && monster.getWillHplist().contains(3)) {
                                    monster.setWillHplist(new ArrayList<Integer>());
                                } else {
                                    monster.setWillHplist(new ArrayList<Integer>());
                                    monster.getWillHplist().add(500);
                                    monster.getWillHplist().add(3);
                                }
                            } else {
                                for (MapleMonster mob : monster.getMap().getAllMonster()) {
                                    mob.getMap().broadcastMessage(MobPacket.BlockAttack(mob, new ArrayList<Integer>()));
                                    mob.setUseSpecialSkill(false);
                                    mob.setSkillForbid(false);
                                }
                            }
                            monster.getMap().broadcastMessage(WillPacket.setWillHp(monster.getWillHplist()));
                            monster.setWillSpecialPattern(false);
                        }, 30000L);
                        break;
                    }
                    case 15: {
                        for (MapleCharacter mapleCharacter : monster.getMap().getAllCharactersThreadsafe()) {
                            monster.getMap().broadcastMessage(CField.showWillEffect((MapleCharacter)mapleCharacter, (int)0, (int)this.skillId, (int)this.skillLevel));
                        }
                        Timer.MobTimer.getInstance().schedule(() -> {
                            for (MapleCharacter chr : monster.getMap().getAllCharactersThreadsafe()) {
                                chr.getClient().getSession().writeAndFlush(WillPacket.teleport());
                                chr.getClient().getSession().writeAndFlush(WillPacket.WillSpiderAttack((int)monster.getId(), (int)this.skillId, (int)this.skillLevel, (int)Randomizer.nextInt(2), null));
                            }
                        }, 3000L);
                    }
                }
            }
            case 45: {
                this.x = monster.getPosition().getLocation().x;
                monster.getMap().broadcastMessage(MobPacket.TeleportMonster(monster, false, 12, new Point(monster.getPosition().x + this.x, 16)));
                break;
            }
            case 50: {
                monster.getMap().broadcastMessage(MobPacket.TeleportMonster(monster, true, 1, new Point(monster.getPosition().x, 16)));
                monster.getMap().broadcastMessage(MobPacket.setAttackZakumArm(monster.getObjectId(), 7));
                monster.getMap().showWeatherEffectNotice("戴米安必須在釋放腐化世界樹的力量之前對其造成重大傷害來阻止他。", 216, 2000);
                monster.getMap().broadcastMessage(MobPacket.demianRunaway(monster, (byte)0, MobSkillFactory.getMobSkill(214, 14), 10000));
                monster.setCustomInfo(8880111, 1, 0);
                monster.setNextSkill(0);
                monster.setNextSkillLvl(0);
                monster.setSkillForbid(true);
                monster.getMap().broadcastMessage(MobPacket.demianRunaway(monster, (byte)1, MobSkillFactory.getMobSkill(214, 14), 10000));
                this.diseases.put(SecondaryStat.Lapidification, new Pair<Integer, Integer>(16, 10000));
                this.allchr = true;
                Timer.MobTimer.getInstance().schedule(() -> {
                    if (monster.isAlive()) {
                        boolean suc = monster.getCustomValue0(8880111) != 2L;
                        monster.removeCustomInfo(8880111);
                        monster.removeCustomInfo(8880112);
                        if (suc) {
                            for (MapleCharacter chr : monster.getMap().getAllChracater()) {
                                chr.getPercentDamage(monster, this.skillId, this.skillLevel, 200, false);
                            }
                            Timer.MobTimer.getInstance().schedule(() -> {
                                if (monster.isAlive()) {
                                    monster.getMap().broadcastMessage(MobPacket.setAttackZakumArm(monster.getObjectId(), 8));
                                    monster.getMap().broadcastMessage(MobPacket.demianRunaway(monster, (byte)1, MobSkillFactory.getMobSkill(214, 14), 10000));
                                    monster.setSkillForbid(false);
                                }
                            }, 500L);
                        } else {
                            monster.getMap().broadcastMessage(MobPacket.setAttackZakumArm(monster.getObjectId(), 8));
                            monster.setSkillForbid(false);
                        }
                    }
                }, 10000L);
                break;
            }
            case 51: {
                monster.getMap().broadcastMessage(MobPacket.demianRunaway(monster, (byte)1, this, 0));
                break;
            }
            case 213: {
                break;
            }
            case 214: {
                long time = 0L;
                if (this.skillLevel != 13) break;
                time = 30000L;
                if (monster.getId() >= 8800130 && monster.getId() <= 8800137) {
                    Timer.MapTimer.getInstance().schedule(() -> {
                        MapleMonster zakum = monster.getMap().getMobObjectByID(8800102);
                        if (zakum != null && zakum.getPhase() <= 2) {
                            monster.getMap().killMonster(monster.getId());
                            MapleMonster part = MapleLifeFactory.getMonster(monster.getId() - 27);
                            part.setF(1);
                            part.setFh(1);
                            part.setStance(5);
                            part.setPhase((byte)1);
                            monster.getMap().spawnMonsterWithEffectBelow(part, monster.getPosition(), -2);
                        }
                    }, time);
                }
                monster.getMap().broadcastMessage(MobPacket.demianRunaway(monster, (byte)1, MobSkillFactory.getMobSkill(214, 14), 10000));
                break;
            }
            case 237: {
                break;
            }
            case 246: {
                this.datas = new ArrayList<Triple<Point, Integer, List<Rectangle>>>();
                for (int ii = 0; ii < 7; ++ii) {
                    ArrayList<Rectangle> rectz = new ArrayList<Rectangle>();
                    int[] randXs = new int[]{0, 280, -560, 560, -280, -840, 840};
                    int t = Randomizer.nextInt(randXs.length);
                    int randX = randXs[t];
                    int delay = (t + 1) * 250;
                    int[][][] rectXs = new int[][][]{new int[][]{{-75, 50}, {13, -50}, {83, 72}, {83, 72}}, new int[][]{{-81, 90}, {-59, -20}, {-25, 13}, {123, 31}, {138, -54}}, new int[][]{{-78, 28}, {-13, -50}, {42, 81}, {75, -18}, {133, 4}}, new int[][]{{-75, 50}, {13, -60}, {83, 72}, {83, 72}}, new int[][]{{-81, 90}, {-59, -20}, {-25, 13}, {123, 31}, {138, -54}}, new int[][]{{-78, 28}, {-13, -50}, {42, 81}, {75, -18}, {133, 4}}, new int[][]{{-78, 28}, {-13, -50}, {42, 81}, {75, -18}, {133, 4}}};
                    int[][] rectX = rectXs[t];
                    for (int i6 = 0; i6 < rectX.length; ++i6) {
                        rectz.add(new Rectangle(rectX[i6][0], -80, rectX[i6][1], 640));
                    }
                    this.datas.add(new Triple(new Point(randX, -260), delay, rectz));
                }
                break;
            }
            case 247: {
                break;
            }
            case 186: {
                break;
            }
            case 248: 
            case 249: {
                switch (this.skillLevel) {
                    case 1: {
                        this.diseases.put(SecondaryStat.BlackMageCreate, new Pair<Integer, Integer>(4, 6000));
                        break;
                    }
                    case 2: {
                        this.diseases.put(SecondaryStat.BlackMageDestroy, new Pair<Integer, Integer>(10, 6000));
                    }
                }
                if (player.getBuffedValue(SecondaryStat.BlackMageDestroy) != null) {
                    if (player.getBuffedValue(SecondaryStat.BlackMageCreate) == null) break;
                    player.setDeathCount((byte)(player.getDeathCount() - 1));
                    player.send(CField.BlackMageDeathCountEffect());
                    player.dispelDebuffs();
                    if (player.getDeathCount() > 0) {
                        player.addHP(-player.getStat().getCurrentMaxHp() * 30L / 100L);
                        if (!player.isAlive()) break;
                        MobSkillFactory.getMobSkill(120, 39).applyEffect(player, monster, this.skillLevel, isFacingLeft);
                        break;
                    }
                    player.addHP(-player.getStat().getCurrentMaxHp());
                    break;
                }
            }
            case 252: {
                break;
            }
            case 262: {
                break;
            }
            case 263: {
                Seren.spawnAttackByMoon((MapleClient)player.getClient(), (int)((MapleMonster)player.getMap().getMonsters().getFirst()).getObjectId());
                break;
            }
            case 264: {
                player.getMap().broadcastMessage(Seren.SerenRazerAttack((MapleCharacter)player, (MapleMonster)monster, (int)this.skillLevel, (int)(this.skillLevel == 1 ? 1800 : 1500)));
                break;
            }
            case 265: {
                monster.setCustomInfo(monster.getId(), 25, 0);
                monster.getMap().broadcastMessage(MobPacket.SpeakingMonster(monster, 4, 0));
                monster.getMap().broadcastMessage(MobPacket.HillaDrainStart(monster.getObjectId()));
                for (int k = 0; k < 8; ++k) {
                    int posx = monster.getPosition().x + Randomizer.rand(-500, 500);
                    if (monster.getMap().getLeft() > posx) {
                        posx = monster.getMap().getLeft() + 50;
                    } else if (monster.getMap().getRight() < posx) {
                        posx = monster.getMap().getRight() - 50;
                    }
                    monster.getMap().spawnMonsterWithEffect(MapleLifeFactory.getMonster(8870106), 43, new Point(posx, monster.getPosition().y));
                }
                break;
            }
            case 268: {
                player.send(CField.useFieldSkill((int)100025, (int)13));
                break;
            }
            default: {
                player.dropMessage(-15, "MobSkill_IDCase:" + this.skillId);
                if (!Config.isDevelop()) break;
                player.dropMessage(5, "未經過處理MOB_Skillid : " + this.skillId + " sub : " + level);
            }
        }
        if (b) {
            for (MapleMapObject o : monster.getMap().getMapObjectsInRect(rectangle, Collections.singletonList(MapleMapObjectType.MONSTER))) {
                this.applyMobSkillEffect((MapleMonster)o, this.getDuration());
            }
        } else {
            this.applyMobSkillEffect(monster, this.getDuration());
        }
        if (!this.getStatups().isEmpty()) {
            if (b) {
                for (MapleMapObject o : monster.getMap().getMapObjectsInRect(rectangle, Collections.singletonList(MapleMapObjectType.PLAYER))) {
                    this.unprimaryPassiveApplyTo((MapleCharacter)o);
                }
                return;
            }
            this.unprimaryPassiveApplyTo(player);
        }
    }

    private boolean applyMobSkillEffect(MapleMonster monster, int n) {
        if (monster != null && monster.isAlive() && !this.getMonsterStatus().isEmpty() && !monster.isBuffed(MonsterStatus.MagicCrash) && !monster.isBuffed(MonsterStatus.Freeze)) {
            EnumMap<MonsterStatus, MonsterEffectHolder> statups = new EnumMap<MonsterStatus, MonsterEffectHolder>(MonsterStatus.class);
            for (Map.Entry<MonsterStatus, Integer> entry : this.getMonsterStatus().entrySet()) {
                MonsterEffectHolder holder = new MonsterEffectHolder(-1, 1, System.currentTimeMillis(), n, this);
                holder.value = entry.getValue();
                holder.moboid = monster.getSeperateSoulSrcOID();
                statups.put(entry.getKey(), holder);
            }
            if (!statups.isEmpty()) {
                monster.registerEffect(statups);
                LinkedHashMap<MonsterStatus, Integer> writeStatups = new LinkedHashMap<MonsterStatus, Integer>();
                for (MonsterStatus stat : statups.keySet()) {
                    writeStatups.put(stat, -1);
                }
                monster.getMap().broadcastMessage(MobPacket.mobStatSet(monster, writeStatups), monster.getPosition());
            }
            return !statups.isEmpty();
        }
        return false;
    }

    @Override
    public int getSourceId() {
        return this.skillId;
    }

    @Override
    public int getLevel() {
        return this.skillLevel;
    }

    public int getMobMpCon() {
        return this.mobMpCon;
    }

    public void setMobMpCon(int mobMpCon) {
        this.mobMpCon = mobMpCon;
    }

    public List<Integer> getSummons() {
        return Collections.unmodifiableList(this.toSummon);
    }

    public int getSummonEffect() {
        return this.summonEffect;
    }

    public void setSummonEffect(int summonEffect) {
        this.summonEffect = summonEffect;
    }

    public int getMobHp() {
        return this.mobHp;
    }

    @Override
    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getDuration() {
        return (int)this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getCoolTime() {
        return this.cooltime;
    }

    public void setCoolTime(long cooltime) {
        this.cooltime = cooltime;
    }

    @Override
    public Point getLt() {
        return this.lt;
    }

    @Override
    public Point getRb() {
        return this.rb;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(short limit) {
        this.limit = limit;
    }

    @Override
    public boolean makeChanceResult() {
        return (double)this.prop >= 1.0 || Math.random() < (double)this.prop;
    }

    public int getAreaSequenceDelay() {
        return this.areaSequenceDelay;
    }

    public void setAreaSequenceDelay(int areaSequenceDelay) {
        this.areaSequenceDelay = areaSequenceDelay;
    }

    public int getSkillAfter() {
        return this.skillAfter;
    }

    public void setSkillAfter(int skillAfter) {
        this.skillAfter = skillAfter;
    }

    public int getForce() {
        return this.force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public int getForcex() {
        return this.forcex;
    }

    public void setForcex(int forcex) {
        this.forcex = forcex;
    }

    private List<MapleCharacter> getPlayersInRange(MapleMonster monster, MapleCharacter player) {
        Rectangle bounds = this.calculateBoundingBox(monster.getPosition(), monster.isFacingLeft());
        ArrayList<MapleCharacter> players = new ArrayList<MapleCharacter>();
        players.add(player);
        return monster.getMap().getPlayersInRectAndInList(bounds, players);
    }

    private List<MapleMapObject> getObjectsInRange(MapleMonster monster, MapleMapObjectType objectType) {
        Rectangle bounds = this.calculateBoundingBox(monster.getPosition(), monster.isFacingLeft());
        return monster.getMap().getMapObjectsInRect(bounds, Collections.singletonList(objectType));
    }

    @Override
    public Point getLt2() {
        return this.lt2;
    }

    @Override
    public Point getRb2() {
        return this.rb2;
    }

    public void setW(int w) {
        this.w = w;
    }

    @Override
    public int getW() {
        return this.w;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    @Override
    public int getAttackCount() {
        return 0;
    }

    public void setEmotion(int b) {
        this.emotion = b;
    }

    public int getEmotion() {
        return this.emotion;
    }

    public void willSpider(MapleCharacter player, MapleMonster monster, int type, boolean solo) {
        ArrayList<Pair<Integer, Integer>> spider2 = new ArrayList<Pair<Integer, Integer>>();
        if (type == 0) {
            spider2.add(new Pair<Integer, Integer>(1200, -480));
            spider2.add(new Pair<Integer, Integer>(1200, -80));
            spider2.add(new Pair<Integer, Integer>(1200, 320));
            spider2.add(new Pair<Integer, Integer>(2800, -320));
            spider2.add(new Pair<Integer, Integer>(2800, 80));
            spider2.add(new Pair<Integer, Integer>(2800, 480));
            spider2.add(new Pair<Integer, Integer>(4400, -550));
            spider2.add(new Pair<Integer, Integer>(4400, -150));
            spider2.add(new Pair<Integer, Integer>(4400, 250));
            spider2.add(new Pair<Integer, Integer>(7000, -470));
            spider2.add(new Pair<Integer, Integer>(7000, -70));
            spider2.add(new Pair<Integer, Integer>(7000, 330));
            spider2.add(new Pair<Integer, Integer>(8600, -320));
            spider2.add(new Pair<Integer, Integer>(8600, 80));
            spider2.add(new Pair<Integer, Integer>(8600, 480));
            spider2.add(new Pair<Integer, Integer>(10200, -150));
            spider2.add(new Pair<Integer, Integer>(10200, 250));
            spider2.add(new Pair<Integer, Integer>(10200, 650));
        } else {
            spider2.add(new Pair<Integer, Integer>(1200, -480));
            spider2.add(new Pair<Integer, Integer>(1200, -80));
            spider2.add(new Pair<Integer, Integer>(1200, 320));
            spider2.add(new Pair<Integer, Integer>(2800, -320));
            spider2.add(new Pair<Integer, Integer>(2800, 80));
            spider2.add(new Pair<Integer, Integer>(2800, 480));
            spider2.add(new Pair<Integer, Integer>(4400, -550));
            spider2.add(new Pair<Integer, Integer>(4400, -150));
            spider2.add(new Pair<Integer, Integer>(4400, 250));
            spider2.add(new Pair<Integer, Integer>(7000, -480));
            spider2.add(new Pair<Integer, Integer>(7000, -80));
            spider2.add(new Pair<Integer, Integer>(7000, 320));
            spider2.add(new Pair<Integer, Integer>(8600, -320));
            spider2.add(new Pair<Integer, Integer>(8600, 80));
            spider2.add(new Pair<Integer, Integer>(8600, 480));
            spider2.add(new Pair<Integer, Integer>(10200, -550));
            spider2.add(new Pair<Integer, Integer>(10200, -150));
            spider2.add(new Pair<Integer, Integer>(10200, 250));
        }
        if (solo) {
            spider2.add(new Pair<Integer, Integer>(12800, -480));
            spider2.add(new Pair<Integer, Integer>(12800, -80));
            spider2.add(new Pair<Integer, Integer>(12800, 320));
            spider2.add(new Pair<Integer, Integer>(14400, -320));
            spider2.add(new Pair<Integer, Integer>(14400, 80));
            spider2.add(new Pair<Integer, Integer>(14400, 480));
            spider2.add(new Pair<Integer, Integer>(16000, -550));
            spider2.add(new Pair<Integer, Integer>(16000, -150));
            spider2.add(new Pair<Integer, Integer>(16000, 250));
        }
        if (spider2.size() > 0) {
            player.getMap().broadcastMessage(WillPacket.WillSpiderAttackPaten((int)monster.getObjectId(), (int)type, spider2));
        }
    }

    @Override
    public String toString() {
        MonsterSkillType fy = MonsterSkillType.getById((int)this.getSourceId());
        return "MobSkill:" + (fy == null ? "Unknow" : fy.name()) + "[" + this.getSourceId() + "] Level:" + this.getLevel();
    }

    public void setFixedPos(List<Point> fixedPos) {
        this.fixedPos = fixedPos;
    }

    public List<Point> getFixedPos() {
        return this.fixedPos;
    }

    public int getSpawnEffect() {
        return this.spawnEffect;
    }
}

