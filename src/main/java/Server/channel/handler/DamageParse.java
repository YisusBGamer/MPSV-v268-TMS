/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  SwordieX.client.party.Party
 *  SwordieX.client.party.PartyMember
 */
package Server.channel.handler;

import Client.MapleCharacter;
import Client.MonsterEffectHolder;
import Client.PlayerSpecialStats;
import Client.SecondaryStat;
import Client.SecondaryStatValueHolder;
import Client.force.MapleForceAtom;
import Client.force.MapleForceFactory;
import Client.inventory.Equip;
import Client.inventory.Item;
import Client.inventory.MapleInventoryType;
import Client.inventory.MapleWeapon;
import Client.skills.ExtraSkill;
import Client.skills.Skill;
import Client.skills.SkillFactory;
import Client.skills.handler.AbstractSkillHandler;
import Client.skills.handler.SkillClassApplier;
import Client.skills.handler.SkillClassFetcher;
import Client.stat.PlayerStats;
import Client.status.MonsterStatus;
import Config.configs.ServerConfig;
import Config.constants.JobConstants;
import Config.constants.SkillConstants;
import Net.auth.Auth;
import Net.server.MapleInventoryManipulator;
import Net.server.Timer;
import Net.server.buffs.MapleStatEffect;
import Net.server.life.Element;
import Net.server.life.MapleMonster;
import Net.server.life.MapleMonsterStats;
import Net.server.life.MobSkill;
import Net.server.maps.MapleMap;
import Net.server.maps.MapleMapObject;
import Net.server.maps.MapleMapObjectType;
import Net.server.maps.MapleSummon;
import Opcode.header.InHeader;
import Opcode.header.OutHeader;
import Packet.BuffPacket;
import Packet.ForcePacket;
import Packet.MaplePacketCreator;
import Packet.SummonPacket;
import Server.channel.handler.AttackInfo;
import Server.channel.handler.AttackMobInfo;
import SwordieX.client.party.Party;
import SwordieX.client.party.PartyMember;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;
import tools.Randomizer;
import tools.data.MaplePacketReader;

public class DamageParse {
    private static final Logger log = LoggerFactory.getLogger("AttackParse");

    public static AttackInfo parseAttack(InHeader header, MaplePacketReader lea, MapleCharacter chr) {
        AttackInfo ai = new AttackInfo();
        try {
            int x;
            int idk5;
            int boxIdk2;
            switch (header) {
                case CP_UserMeleeAttack: {
                    ai.attackHeader = OutHeader.LP_UserMeleeAttack;
                    break;
                }
                case CP_UserShootAttack: {
                    ai.attackHeader = OutHeader.LP_UserShootAttack;
                    break;
                }
                case UserNonTargetForceAtomAttack: 
                case CP_UserMagicAttack: {
                    ai.attackHeader = OutHeader.LP_UserMagicAttack;
                }
            }
            if (header == InHeader.CP_UserShootAttack) {
                boolean bl = ai.boxAttack = lea.readByte() != 0;
            }
            if (header == InHeader.UserNonTargetForceAtomAttack) {
                lea.readInt();
                lea.readInt();
                lea.readInt();
                lea.readInt();
                lea.readInt();
            }
            ai.fieldKey = lea.readByte();
            ai.numAttackedAndDamage = lea.readByte();
            ai.mobCount = (byte)(ai.numAttackedAndDamage >>> 4 & 0xF);
            ai.hits = (byte)(ai.numAttackedAndDamage & 0xF);
            ai.skillId = lea.readInt();
            ai.skllv = lea.readInt();
            switch (header) {
                case CP_UserMagicAttack: 
                case CP_UserBodyAttack: 
                case CP_UserAreaDotAttack: 
                case UserSpotlightAttack: {
                    break;
                }
                default: {
                    ai.addAttackProc = lea.readByte();
                }
            }
            lea.readInt();
            lea.readInt();
            lea.readInt();
            DamageParse.attackBonusRecv(lea, ai);
            DamageParse.calcAttackPosition(lea, chr, ai);
            int skillID = ai.skillId;
            if (SkillConstants.isKeyDownSkill(skillID) || SkillConstants.isSuperNovaSkill(skillID) || skillID == 11121055 || skillID == 400051334) {
                ai.keyDown = lea.readInt();
            }
            if (SkillConstants.isRushBombSkill(skillID)) {
                ai.grenadeId = lea.readInt();
            }
            if (SkillConstants.isZeroSkill(skillID)) {
                ai.zero = lea.readByte();
            }
            if (SkillConstants.isUsercloneSummonedAbleSkill(skillID)) {
                ai.bySummonedID = lea.readInt();
            }
            switch (skillID) {
                case 80002823: 
                case 400031010: 
                case 400041019: {
                    lea.readInt();
                    lea.readInt();
                    break;
                }
                case 12000026: 
                case 12100028: 
                case 0xB8C8CC: 
                case 12120010: 
                case 80001836: {
                    lea.readInt();
                }
            }
            ai.buckShot = lea.readByte();
            ai.someMask = lea.readByte();
            if (header == InHeader.CP_UserShootAttack) {
                int idk3 = lea.readInt();
                boolean bl = ai.isJablin = lea.readByte() != 0;
                if (ai.boxAttack) {
                    int boxIdk1 = lea.readInt();
                    boxIdk2 = lea.readShort();
                    short s = lea.readShort();
                }
            }
            switch (header) {
                case CP_UserMeleeAttack: 
                case CP_UserShootAttack: 
                case CP_UserBodyAttack: 
                case CP_UserAreaDotAttack: {
                    short maskie = lea.readShort();
                    ai.display = maskie & 0xFF;
                    ai.direction = maskie >>> 8 & 0xFF;
                    break;
                }
                default: {
                    ai.display = lea.readByte();
                    ai.direction = lea.readByte();
                }
            }
            ai.requestTime = lea.readInt();
            ai.attackActionType = lea.readByte();
            if (SkillConstants.isEvanForceSkill(skillID)) {
                ai.idk0 = lea.readByte();
            }
            if (skillID == 80001915 || skillID == 36111010) {
                idk5 = lea.readInt();
                x = lea.readInt();
                boxIdk2 = lea.readInt();
            }
            ai.attackSpeed = lea.readByte();
            ai.tick = lea.readInt();
            if (skillID == 33000036) {
                lea.readInt();
            }
            if (skillID == 80011561 || skillID == 80002463 || skillID == 80001762 || skillID == 80002212) {
                lea.readInt();
            }
            if (skillID != 2121003) {
                idk5 = lea.readInt();
            }
            if (ai.tick > 0 && header == InHeader.CP_UserMeleeAttack || header == InHeader.CP_UserAreaDotAttack) {
                ai.finalAttackLastSkillID = lea.readInt();
                if (skillID > 0 && ai.finalAttackLastSkillID > 0) {
                    ai.finalAttackByte = lea.readByte();
                }
            }
            if (header == InHeader.CP_UserShootAttack) {
                int bulletSlot = lea.readInt();
                ai.cashSlot = lea.readShort();
                byte idk = lea.readByte();
                lea.skip(8);
            }
            switch (skillID) {
                case 5111009: {
                    ai.ignorePCounter = lea.readByte() != 0;
                    break;
                }
                case 25111005: {
                    ai.spiritCoreEnhance = lea.readInt();
                    break;
                }
                case 80003365: {
                    lea.readPosInt();
                    break;
                }
                case 400011124: 
                case 400011125: 
                case 400011126: {
                    if (skillID > 0 && ai.finalAttackLastSkillID == 0) {
                        lea.readByte();
                    }
                    lea.readInt();
                    break;
                }
                case 23121011: 
                case 80001913: {
                    if (ai.addAttackProc <= 0) break;
                    lea.readByte();
                }
            }
            if (header == InHeader.UserNonTargetForceAtomAttack) {
                lea.readInt();
            }
            for (int i = 0; i < ai.mobCount; ++i) {
                int j;
                AttackMobInfo mai = new AttackMobInfo();
                mai.mobId = lea.readInt();
                mai.hitAction = lea.readByte();
                mai.left = lea.readByte();
                mai.idk3 = lea.readByte();
                mai.forceActionAndLeft = lea.readByte();
                mai.frameIdx = lea.readByte();
                mai.templateID = lea.readInt();
                mai.calcDamageStatIndexAndDoomed = lea.readByte();
                mai.hitX = lea.readShort();
                mai.hitY = lea.readShort();
                mai.oldPosX = lea.readShort();
                mai.oldPosY = lea.readShort();
                if (header == InHeader.CP_UserMagicAttack) {
                    mai.hpPerc = lea.readByte();
                    mai.magicInfo = skillID == 80001835 ? (short)lea.readByte() : lea.readShort();
                } else {
                    mai.idk6 = lea.readShort();
                }
                lea.readInt();
                lea.readInt();
                lea.readByte();
                mai.damages = new long[ai.hits];
                for (j = 0; j < ai.hits; ++j) {
                    mai.damages[j] = lea.readLong();
                }
                mai.mobUpDownYRange = lea.readInt();
                lea.readInt();
                lea.readInt();
                if (skillID == 37111005 || skillID == 175001003) {
                    lea.skip(1);
                } else if (skillID == 400021029) {
                    lea.readByte();
                    lea.readInt();
                }
                if (skillID == 142120001 || skillID == 142120002 || skillID == 142110003 || skillID == 142110015) {
                    lea.skip(8);
                }
                mai.type = lea.readByte();
                mai.currentAnimationName = "";
                if (mai.type == 1) {
                    mai.currentAnimationName = lea.readMapleAsciiString();
                    lea.readMapleAsciiString();
                    mai.animationDeltaL = lea.readInt();
                    mai.hitPartRunTimesSize = lea.readInt();
                    if (mai.hitAction == -1) {
                        mai.hitPartRunTimes = new String[mai.hitPartRunTimesSize];
                        for (j = 0; j < mai.hitPartRunTimesSize; ++j) {
                            mai.hitPartRunTimes[j] = lea.readMapleAsciiString();
                        }
                    }
                } else if (mai.type == 2) {
                    mai.currentAnimationName = lea.readMapleAsciiString();
                    lea.readMapleAsciiString();
                    mai.animationDeltaL = lea.readInt();
                }
                lea.readByte();
                lea.readShort();
                lea.readShort();
                lea.readPos();
                lea.readShort();
                lea.readShort();
                lea.readByte();
                lea.readByte();
                lea.readInt();
                lea.readByte();
                lea.readInt();
                lea.readInt();
                int count = lea.readInt();
                for (int c = 0; c < count; ++c) {
                    lea.readLong();
                }
                lea.readInt();
                ai.mobAttackInfo.add(mai);
            }
            if (skillID == 61121052 || skillID == 36121052 || skillID == 80001362 || SkillConstants.isScreenCenterAttackSkill(skillID)) {
                ai.ptTarget = lea.readPos();
            } else if (SkillConstants.isSuperNovaSkill(skillID)) {
                ai.ptAttackRefPoint = lea.readPos();
            } else if (skillID == 101000102) {
                ai.idkPos = lea.readPos();
            } else if (skillID == 400031016 || SkillConstants.sub_140E6E0B0(3221019, skillID) || skillID == 400041024 || skillID == 80002452 || SkillConstants.sub_140E62810(skillID)) {
                ai.idkPos = lea.readPos();
            } else if (skillID == 400011132) {
                ai.idkPos = lea.readPos();
            } else {
                boolean v1221 = SkillConstants.sub_140E5D870(skillID);
                if ((chr.getJob() == 1400 || chr.getJob() - 1410 <= 2) && v1221) {
                    lea.readShort();
                    lea.readShort();
                } else {
                    if (skillID == 40011289) {
                        ai.x = lea.readShort();
                        ai.y = lea.readShort();
                    } else if (skillID == 40011290) {
                        ai.x = lea.readShort();
                        ai.y = lea.readShort();
                    } else if (skillID == 41111001 || skillID == 41111017 || skillID == 41121015) {
                        ai.x = lea.readShort();
                        ai.y = lea.readShort();
                    } else if (header == InHeader.CP_UserMeleeAttack) {
                        ai.x = lea.readShort();
                        ai.y = lea.readShort();
                    }
                    if (skillID == 400021044) {
                        ai.option = lea.readInt();
                    } else if (skillID == 27121052 || skillID == 80001837) {
                        ai.x = lea.readShort();
                        ai.y = lea.readShort();
                    } else if (skillID == 2121003) {
                        int size = lea.readByte();
                        lea.skip(4);
                        int[] mists = new int[size];
                        for (int i = 0; i < size; ++i) {
                            mists[i] = lea.readInt();
                        }
                        ai.mists = mists;
                    } else if (skillID == 2111003) {
                        byte force = lea.readByte();
                        short forcedXSh = lea.readShort();
                        short forcedYSh = lea.readShort();
                        ai.force = force;
                        ai.forcedXSh = forcedXSh;
                        ai.forcedYSh = forcedYSh;
                    } else if (skillID == 2121052) {
                        ai.position = lea.readPos();
                        lea.readInt();
                        lea.readInt();
                        ai.skillposition = lea.readPosInt();
                        lea.skip(36);
                    } else if (skillID == 80001835) {
                        int sizeB = lea.readByte();
                        int[] idkArr2 = new int[sizeB];
                        short[] shortArr2 = new short[sizeB];
                        for (int i = 0; i < sizeB; ++i) {
                            idkArr2[i] = lea.readInt();
                            shortArr2[i] = lea.readShort();
                        }
                        short delay = lea.readShort();
                        ai.mists = idkArr2;
                        ai.shortArr = shortArr2;
                        ai.delay = delay;
                    }
                    if (header == InHeader.CP_UserAreaDotAttack) {
                        ai.pos = lea.readPos();
                    }
                    if (SkillConstants.isAranFallingStopSkill(skillID)) {
                        ai.fh = lea.readByte();
                    }
                    if (header == InHeader.CP_UserShootAttack && skillID / 1000000 == 33) {
                        ai.bodyRelMove = lea.readPos();
                    }
                    if (skillID == 21121029 || skillID == 37121052) {
                        ai.teleportPt = lea.readPos();
                    }
                    if (header == InHeader.CP_UserShootAttack && SkillConstants.isKeydownSkillRectMoveXY(skillID)) {
                        ai.keyDownRectMoveXY = lea.readPos();
                    }
                    if (skillID == 61121105 || skillID == 61121222 || skillID == 24121052) {
                        ai.Vx = lea.readShort();
                        for (int i = 0; i < ai.Vx; ++i) {
                            x = lea.readShort();
                            short y = lea.readShort();
                        }
                    } else if (skillID == 101120104) {
                        lea.readShort();
                        lea.readShort();
                        lea.readShort();
                    } else if (skillID == 14111006 && ai.grenadeId != 0) {
                        ai.grenadePos = lea.readPos();
                    } else if (skillID == 80001914) {
                        ai.fh = lea.readByte();
                    } else if (header == InHeader.CP_UserShootAttack && SkillConstants.isZeroSkill(skillID) && lea.available() >= 4L) {
                        ai.position = lea.readPos();
                    } else if (skillID == 400021077) {
                        lea.readInt();
                        lea.readInt();
                        ai.skillposition = lea.readPos();
                        lea.readByte();
                    }
                    if (header == InHeader.CP_UserMagicAttack) {
                        short forcedX = lea.readShort();
                        short forcedY = lea.readShort();
                        boolean dragon = lea.readByte() != 0 && JobConstants.is龍魔導士(chr.getJob());
                        ai.forcedX = forcedX;
                        ai.forcedY = forcedY;
                        if (dragon) {
                            short rcDstRight = lea.readShort();
                            short rectRight = lea.readShort();
                            short x2 = lea.readShort();
                            short y = lea.readShort();
                            lea.readByte();
                            lea.readByte();
                            lea.readByte();
                            ai.rcDstRight = rcDstRight;
                            ai.rectRight = rectRight;
                            ai.x = x2;
                            ai.y = y;
                        }
                    }
                }
            }
            return ai;
        }
        catch (Exception e) {
            log.error("Error parseAttack, skill:" + ai.skillId, e);
            return null;
        }
    }

    private static void attackBonusRecv(MaplePacketReader lea, AttackInfo ai) {
        lea.readByte();
        ai.starSlot = lea.readShort();
        lea.readInt();
        lea.readByte();
        lea.readByte();
        lea.readByte();
        lea.readInt();
        ai.position = lea.readPosInt();
        lea.readInt();
        int startPointer = lea.readInt();
        int endPointer = lea.readInt();
        int count = (endPointer - startPointer) / 4;
        for (int i = 0; i < count; ++i) {
            lea.readInt();
        }
        boolean isUseKeyMap = lea.readBool();
        lea.readByte();
        lea.readInt();
        lea.readInt();
        lea.readLong();
        lea.readLong();
        lea.readLong();
        lea.readByte();
        int count2 = Math.min(15, lea.readInt());
        for (int i = 0; i < count2; ++i) {
            lea.readInt();
        }
        lea.readInt();
        lea.readInt();
        lea.readInt();
        lea.readInt();
        lea.readInt();
        lea.readMapleAsciiString();
        lea.readInt();
    }

    public static void calcAttackPosition(MaplePacketReader lea, MapleCharacter chr, AttackInfo ai) {
        lea.readInt();
        if (lea.readByte() != 0) {
            int type;
            if (ai == null) {
                ai = new AttackInfo();
            }
            lea.readInt();
            block22: while ((type = lea.readInt()) > 0) {
                switch (type) {
                    case 1: {
                        int i;
                        if (lea.readByte() == 0) break;
                        lea.readInt();
                        lea.readInt();
                        lea.readInt();
                        lea.readByte();
                        int len = lea.readByte();
                        for (i = 0; i < len; i = (int)((byte)(i + 1))) {
                            lea.readInt();
                        }
                        continue block22;
                    }
                    case 2: {
                        if (lea.readByte() == 0) break;
                        lea.readByte();
                        lea.readByte();
                        lea.readInt();
                        lea.readInt();
                        lea.readBool();
                        lea.readInt();
                        lea.readInt();
                        lea.skip(16);
                        break;
                    }
                    case 3: {
                        if (lea.readByte() == 0) break;
                        lea.readByte();
                        lea.readInt();
                        break;
                    }
                    case 4: {
                        if (!lea.readBool()) break;
                        ai.rect = lea.readRect();
                        ai.skillposition = lea.readPosInt();
                        lea.readLong();
                        lea.readInt();
                        break;
                    }
                    case 5: 
                    case 6: 
                    case 10: 
                    case 11: 
                    case 12: 
                    case 13: 
                    case 14: 
                    case 22: 
                    case 23: {
                        lea.readByte();
                        break;
                    }
                    case 7: {
                        if (lea.readByte() == 0) break;
                        ai.skillposition = lea.readPosInt();
                        lea.readByte();
                        lea.readByte();
                        lea.readByte();
                        lea.readByte();
                        ai.left = lea.readByte() != 0;
                        break;
                    }
                    case 8: {
                        if (lea.readByte() == 0) break;
                        lea.readInt();
                        lea.readInt();
                        lea.readInt();
                        lea.readInt();
                        break;
                    }
                    case 9: {
                        if (lea.readByte() == 0) break;
                        lea.readInt();
                        lea.readInt();
                        lea.readInt();
                        lea.readInt();
                        lea.readInt();
                        lea.readInt();
                        break;
                    }
                    case 15: {
                        int i;
                        if (!lea.readBool()) break;
                        int len = lea.readInt();
                        for (i = 0; i < len; ++i) {
                            lea.readInt();
                            lea.readInt();
                            lea.readInt();
                            lea.readInt();
                        }
                        lea.readInt();
                        lea.readInt();
                        lea.readInt();
                        lea.readInt();
                        break;
                    }
                    case 19: {
                        if (!lea.readBool()) break;
                        ai.unInt1 = lea.readInt();
                        lea.readInt();
                        lea.readInt();
                        break;
                    }
                    case 20: {
                        int i;
                        if (!lea.readBool()) break;
                        int len = lea.readInt();
                        for (i = 0; i < len; ++i) {
                            lea.readInt();
                        }
                        continue block22;
                    }
                    case 24: {
                        if (!lea.readBool()) break;
                        lea.readInt();
                        lea.readInt();
                        lea.readInt();
                        lea.readInt();
                        lea.readPosInt();
                        lea.readPosInt();
                        break;
                    }
                    case 25: {
                        int i;
                        if (!lea.readBool()) break;
                        int size = lea.readInt();
                        for (i = 0; i < size; ++i) {
                            ai.skillSpawnInfo.add(new Pair<Integer, Point>(lea.readInt(), lea.readPosInt()));
                        }
                        lea.readByte();
                        break;
                    }
                    case 29: 
                    case 43: {
                        if (!lea.readBool()) break;
                        lea.readInt();
                        break;
                    }
                    case 34: {
                        int i;
                        if (!lea.readBool()) break;
                        int size = lea.readInt();
                        for (i = 0; i < size; ++i) {
                            lea.readInt();
                            lea.readInt();
                        }
                        continue block22;
                    }
                    case 37: {
                        if (!lea.readBool()) break;
                        lea.readInt();
                        ai.unInt1 = lea.readInt();
                        lea.readInt();
                        lea.readInt();
                        lea.readInt();
                        ai.skillposition = lea.readPosInt();
                        ai.pos = lea.readPosInt();
                        lea.readInt();
                        lea.readBool();
                        lea.readByte();
                        lea.readByte();
                        break;
                    }
                    case 39: {
                        if (!lea.readBool()) break;
                        lea.readInt();
                        lea.readInt();
                        break;
                    }
                    case 42: {
                        int i;
                        if (!lea.readBool()) break;
                        int size = lea.readInt();
                        for (i = 0; i < size; ++i) {
                            lea.readInt();
                        }
                        continue block22;
                    }
                    case 45: {
                        if (!lea.readBool()) break;
                        lea.readInt();
                        lea.readInt();
                        lea.readByte();
                        lea.readInt();
                        lea.readInt();
                        break;
                    }
                    case 48: 
                    case 49: {
                        int i;
                        if (!lea.readBool()) break;
                        int size = lea.readInt();
                        for (i = 0; i < size; ++i) {
                            lea.readLong();
                        }
                        break;
                    }
                }
            }
            lea.readInt();
            int unk340 = 0;
            int unk338 = 0;
            int v8 = 0;
            int result = 0;
            DamageParse.sub_140CA6750(lea, unk340 - unk338);
            if (unk338 != unk340) {
                do {
                    lea.readByte();
                } while (++v8 < (result = unk340 - unk338));
            }
        }
    }

    public static void sub_140CA6750(MaplePacketReader oPacket, int a2) {
        int v3 = 2 * a2 ^ a2 >> 31;
        if (v3 >= 128) {
            do {
                oPacket.readByte();
            } while ((v3 >>= 7) >= 128);
        }
        oPacket.readByte();
    }

    public static AttackInfo parseSummonAttack(MaplePacketReader slea, MapleCharacter chr) {
        AttackInfo ai = new AttackInfo();
        try {
            int skillId;
            ai.attackType = AttackInfo.AttackType.SummonedAttack;
            ai.lastAttackTickCount = slea.readInt();
            int summonSkill = slea.readInt();
            ai.skillId = slea.readInt();
            if (ai.skillId == 0) {
                ai.skillId = summonSkill;
            }
            slea.skip(1);
            slea.readInt();
            switch (ai.skillId) {
                case 152100001: 
                case 152110001: {
                    slea.readInt();
                }
            }
            slea.readByte();
            slea.readByte();
            slea.readInt();
            String attackTypeString = slea.readMapleAsciiString();
            ai.display = slea.readByte();
            ai.numAttackedAndDamage = slea.readByte();
            ai.mobCount = (byte)(ai.numAttackedAndDamage >>> 4 & 0xF);
            ai.hits = (byte)(ai.numAttackedAndDamage & 0xF);
            slea.readByte();
            int n = 26 + ai.mobCount * (28 + (ai.hits << 3) + 14) + 4;
            if (ai.skillId == 35111002) {
                if (slea.available() > (long)n) {
                    slea.readInt();
                    slea.readInt();
                    slea.readInt();
                } else {
                    ai.unInt1 = 1;
                }
            }
            ai.position = slea.readPos();
            ai.skillposition = slea.readPos();
            slea.readByte();
            slea.readInt();
            switch (ai.skillId) {
                case 5221022: 
                case 5221027: 
                case 0x4FAAA4: 
                case 14121003: 
                case 154121041: 
                case 400041000: 
                case 400041007: {
                    break;
                }
                default: {
                    slea.readInt();
                }
            }
            ai.starSlot = slea.readShort();
            if (ai.starSlot > 0) {
                slea.readInt();
            }
            if ((skillId = slea.readInt()) > 0 && SkillFactory.getSkill(skillId) != null) {
                ai.skillId = skillId;
            }
            if (ai.skillId == 0x4FAAA4) {
                ai.skillposition = slea.readPosInt();
            }
            for (byte i = 0; i < ai.mobCount; i = (byte)(i + 1)) {
                AttackMobInfo mai = new AttackMobInfo();
                mai.mobId = slea.readInt();
                mai.templateID = slea.readInt();
                mai.hitAction = slea.readByte();
                mai.left = slea.readByte();
                mai.idk3 = slea.readByte();
                mai.forceActionAndLeft = slea.readByte();
                mai.frameIdx = slea.readByte();
                mai.templateID = slea.readInt();
                mai.calcDamageStatIndexAndDoomed = slea.readByte();
                mai.hitX = slea.readShort();
                mai.hitY = slea.readShort();
                mai.oldPosX = slea.readShort();
                mai.oldPosY = slea.readShort();
                mai.idk6 = slea.readShort();
                slea.readShort();
                slea.readShort();
                slea.readInt();
                slea.readInt();
                slea.readByte();
                mai.damages = new long[ai.hits];
                for (byte j = 0; j < ai.hits; j = (byte)(j + 1)) {
                    long damage = slea.readLong();
                    if (chr.isDebug()) {
                        chr.dropMessage(6, "[Summon Attack] Mob OID: " + mai.mobId + " Idx:" + (j + 1) + " - Damage: " + damage);
                    }
                    mai.damages[j] = damage;
                }
                mai.mobUpDownYRange = slea.readInt();
                slea.readByte();
                slea.readByte();
                slea.readInt();
                slea.readInt();
                slea.readInt();
                slea.readPos();
                slea.skip(2);
                slea.skip(1);
                slea.skip(4);
                slea.readInt();
                int count = slea.readInt();
                for (int c = 0; c < count; ++c) {
                    slea.readLong();
                }
                slea.read(4);
                ai.mobAttackInfo.add(mai);
            }
            return ai;
        }
        catch (Exception e) {
            log.error("Error parseSummonAttack, skill:" + ai.skillId, e);
            return null;
        }
    }

    public static boolean applyAttackCooldown(MapleStatEffect effect, MapleCharacter chr, int skillid, boolean isChargeSkill, boolean isBuff, boolean energy) {
        int cooldownTime = effect.getCooldown(chr);
        if (cooldownTime == 0) {
            cooldownTime = SkillFactory.getSkill(SkillConstants.getLinkedAttackSkill(skillid)).getEffect(chr.getTotalSkillLevel(skillid)).getCooldown(chr);
        }
        if (cooldownTime > 0) {
            if (chr.isSkillCooling(skillid) && !isChargeSkill && !isBuff && !SkillConstants.isNoDelaySkill(skillid)) {
                chr.dropMessage(5, "技能由於冷卻時間限制，暫時無法使用。");
                chr.getClient().sendEnableActions();
                return false;
            }
            chr.registerSkillCooldown(skillid, System.currentTimeMillis(), cooldownTime);
        }
        return true;
    }

    public static void afterAttack(MapleStatEffect attackEffect, MapleCharacter player, long totalDamage, Point pos, AttackInfo ai, boolean passive) {
        List<MapleMapObject> mobs;
        AbstractSkillHandler handler;
        PlayerStats stats = player.getStat();
        PlayerSpecialStats specialStats = player.getSpecialStat();
        short job = player.getJob();
        int hpHeal = 0;
        int mpHeal = 0;
        for (Pair<Integer, Integer> pair : stats.hpRecover_onAttack.values()) {
            if (totalDamage <= 0L || !Randomizer.isSuccess((Integer)pair.right)) continue;
            hpHeal += (Integer)pair.left * stats.getCurrentMaxHP() / 100;
        }
        if (totalDamage > 0L && player.checkInnerStormValue()) {
            player.modifyInnerStormValue(1);
        }
        if (totalDamage > 0L) {
            MapleStatEffect addSkillEffect;
            Skill skill;
            int add_skillId = 80002890;
            if (!(attackEffect != null && attackEffect.getSourceId() == add_skillId || (skill = SkillFactory.getSkill(add_skillId)) == null || (addSkillEffect = skill.getEffect(1)) == null || player.getBuffStatValueHolder(add_skillId) == null || player.isSkillCooling(add_skillId))) {
                player.registerSkillCooldown(add_skillId, addSkillEffect.getCooldown(player), true);
                ExtraSkill eskill = new ExtraSkill(add_skillId, player.getPosition());
                eskill.Value = 1;
                eskill.FaceLeft = player.isFacingLeft() ? 0 : 1;
                player.send(MaplePacketCreator.RegisterExtraSkill(add_skillId, Collections.singletonList(eskill)));
            }
        }
        AbstractSkillHandler abstractSkillHandler = handler = attackEffect == null ? null : attackEffect.getSkillHandler();
        if (attackEffect == null) {
            handler = SkillClassFetcher.getHandlerByJob(player.getJobWithSub());
        }
        int handleRes = -1;
        if (handler != null) {
            SkillClassApplier applier = new SkillClassApplier();
            applier.effect = attackEffect;
            applier.totalDamage = totalDamage;
            applier.pos = pos;
            applier.passive = passive;
            applier.hpHeal = hpHeal;
            applier.mpHeal = mpHeal;
            applier.ai = ai;
            handleRes = handler.onAfterAttack(player, applier);
            if (handleRes == 0) {
                return;
            }
            if (handleRes == 1) {
                attackEffect = applier.effect;
                totalDamage = applier.totalDamage;
                pos = applier.pos;
                passive = applier.passive;
                hpHeal = applier.hpHeal;
                mpHeal = applier.mpHeal;
                ai = applier.ai;
            }
        }
        if (hpHeal > 0 || mpHeal > 0) {
            player.addHPMP(Math.min(hpHeal, stats.getCurrentMaxHP() * stats.hpRecover_limit / 100), mpHeal, false, true);
        }
        if (attackEffect != null && !SkillConstants.isNoApplyAttack(attackEffect.getSourceId())) {
            attackEffect.attackApplyTo(player, passive, ai.skillposition);
        }
        if (attackEffect != null && attackEffect.getSourceId() == 400001036) {
            player.reduceSkillCooldown(400001036, attackEffect.getX() * ai.mobCount * 1000);
        }
        MapleStatEffect eff = player.getEffectForBuffStat(SecondaryStat.GuidedArrow);
        if (totalDamage > 0L && eff != null && !(mobs = player.getMap().getMapObjectsInRange(player.getPosition(), 500.0, Collections.singletonList(MapleMapObjectType.MONSTER))).isEmpty()) {
            MapleMap map = player.getMap();
            int id = attackEffect.getX();
            int key = player.getId();
            int objectID = mobs.get(Randomizer.nextInt(mobs.size())).getObjectId();
            map.broadcastMessage(player, ForcePacket.showGuidedArrow(id, key, objectID), true);
        }
    }

    public static void calcDamage(AttackInfo ai, MapleCharacter chr, int attackCount, MapleStatEffect effect) {
        MapleMap map = chr.getMap();
        int mobCount = 1;
        if (chr.isDebug()) {
            chr.dropDebugMessage(0, "[Attack] " + String.valueOf(effect != null ? effect : "Normal"));
        }
        if (effect != null) {
            mobCount = effect.getMobCount(chr);
        }
        if (chr.getBuffedValue(SecondaryStat.BuckShot) != null) {
            attackCount = Math.min(15, attackCount * 3);
        }
        if (chr.getBuffedValue(SecondaryStat.AdrenalinBoost) != null) {
            attackCount = Math.min(15, attackCount + 2);
            mobCount = Math.min(15, mobCount + 5);
        }
        if (ai.skillId == 400021018) {
            attackCount = Math.min(15, attackCount + 2);
        }
        if (ai.skillId == 15121001 && chr.getBuffedZ(SecondaryStat.IgnoreTargetDEF) != null) {
            attackCount += chr.getBuffedIntZ(SecondaryStat.IgnoreTargetDEF);
        }
        attackCount += chr.getStat().incAttackCount;
        if (JobConstants.is夜光(chr.getJob()) && ai.skillId > 0) {
            MapleStatEffect eff = chr.getEffectForBuffStat(SecondaryStat.Larkness);
            int type = ai.skillId % 1000 / 100;
            if (eff != null) {
                switch (eff.getSourceId()) {
                    case 20040216: {
                        if (type != 1) break;
                        attackCount = Math.min(15, attackCount * 2);
                        break;
                    }
                    case 20040217: {
                        if (type != 2) break;
                        attackCount = Math.min(15, attackCount * 2);
                        break;
                    }
                    case 20040219: {
                        attackCount = Math.min(15, attackCount * 2);
                    }
                }
            }
        }
        if (chr.getBuffedValue(SecondaryStat.Enrage) != null) {
            mobCount = 1;
        }
        if (!ServerConfig.SERVER_VERIFY_DAMAGE) {
            mobCount = 15;
        }
        if (ai.mobAttackInfo.size() > mobCount) {
            chr.dropMessage(9, "[攻擊檢測] " + String.valueOf(effect != null ? effect : "普通攻擊") + " 目標數量:" + ai.mobAttackInfo.size() + " 超過伺服器計算數量:" + mobCount);
        }
        int idx = 0;
        for (AttackMobInfo mai : ai.mobAttackInfo) {
            MapleMonster monster = map.getMobObject(mai.mobId);
            if (monster == null || monster.getStats().isInvincible()) continue;
            long calcedMaxDamage = ServerConfig.SERVER_VERIFY_DAMAGE ? (long)chr.getCalcDamage().calcDamage(chr, ai, ++idx, monster, monster.getStats().isBoss()) : 10000000000000L;
            MapleMonsterStats stats = monster.getStats();
            long fixDamage = stats.getFixedDamage();
            MonsterEffectHolder holder = monster.getEffectHolder(MonsterStatus.JaguarBleeding);
            if (ai.skillId == 33000036 && holder != null) {
                attackCount = holder.value;
            }
            if (!ServerConfig.SERVER_VERIFY_DAMAGE) {
                attackCount = 15;
            }
            if (mai.damages.length > attackCount) {
                if (switch (ai.skillId) {
                    case 2121052, 3100010, 3101009, 3120017, 3120022, 4100011, 4100012, 0x3E9393, 4120018, 0x3EDDD3, 12000026, 12001020, 12100020, 12110020, 0xB8C8CC, 12120006, 12120010, 13101022, 13101027, 13110022, 13110027, 13120003, 13120010, 13121055, 14000027, 14000028, 20031209, 20031210, 24100003, 24120002, 25100010, 25120115, 31221001, 31221014, 35101002, 35110017, 36001005, 36110004, 36111004, 61101002, 61110211, 61120007, 61121217, 65111007, 65111100, 65121011, 142110011, 152001001, 152110004, 152120001, 152120002, 152120017, 400011058, 400011059, 400011124, 400011125, 400011126, 400011127, 400021001, 400031020, 400031022, 400041009, 400041010, 400041022, 400041023, 400051007, 400051013 -> true;
                    default -> false;
                }) {
                    attackCount = mai.damages.length;
                }
            }
            chr.getClient().outPacket(OutHeader.LP_UseAttack.getValue(), 0);
            ai.hits = (byte)attackCount;
            for (int i = 0; i < mai.damages.length; ++i) {
                long damage = mai.damages[i];
                if (fixDamage != -1L) {
                    if (stats.getOnlyNoramlAttack()) {
                        damage = ai.skillId != 0 ? 0L : fixDamage;
                        calcedMaxDamage = Math.max(fixDamage, calcedMaxDamage);
                    } else {
                        damage = fixDamage;
                        calcedMaxDamage = Math.max(fixDamage, calcedMaxDamage);
                    }
                } else if (stats.getOnlyNoramlAttack()) {
                    long l = damage = ai.skillId != 0 ? 0L : Math.min(damage, 0L);
                }
                if (ai.skillId == 80001770 && !monster.isBoss()) {
                    calcedMaxDamage = damage;
                }
                if (damage <= calcedMaxDamage || chr.isDebug()) {
                    // empty if block
                }
                mai.damages[i] = damage;
            }
        }
    }

    public static void applyAttack(AttackInfo ai, Skill theSkill, MapleCharacter player, MapleStatEffect attackEffect, boolean passive) {
        SecondaryStatValueHolder summonHolder;
        SecondaryStatValueHolder holder;
        MapleStatEffect effect;
        boolean noBullet;
        boolean bl = noBullet = ai.starSlot == 0 || JobConstants.noBulletJob(player.getJob());
        if (!noBullet && player.getBuffedValue(SecondaryStat.SoulArrow) == null && player.getBuffedValue(SecondaryStat.NoBulletConsume) == null && player.getSkillEffect(4110016) == null && player.getSkillEffect(5200016) == null && player.getSkillEffect(13100028) == null && player.getSkillEffect(14110031) == null && player.getSkillEffect(3100011) == null && player.getSkillEffect(3200014) == null && player.getSkillEffect(33100017) == null && ai.skillId != 95001000) {
            short bulletConsume = 0;
            if (attackEffect != null) {
                bulletConsume = (short)attackEffect.getBulletConsume();
            }
            if (bulletConsume == 0) {
                bulletConsume = ai.hits;
            }
            if (bulletConsume > 0 && !MapleInventoryManipulator.removeFromSlot(player.getClient(), MapleInventoryType.USE, ai.starSlot, bulletConsume, false, true)) {
                return;
            }
        }
        if (attackEffect != null && attackEffect.getSourceId() == 80011273 && Auth.checkPermission("MVPEquip_1113220")) {
            Item eq = null;
            for (Item item : player.getInventory(MapleInventoryType.EQUIPPED).listById(1113220)) {
                if (!((Equip)item).isMvpEquip()) continue;
                eq = (Equip)item;
                break;
            }
            if (eq != null) {
                int enhanceNum;
                int cooldown;
                int growSize;
                int moveCount;

                boolean forever;
                boolean bl2 = forever = eq.getExpiration() < 0L;
                if (!forever && !player.isSilverMvp() || ((Equip)eq).getStarForceLevel() < 15) {
                    enhanceNum = 1;
                    cooldown = 0;
                    growSize = 100;
                    moveCount = 10;
                } else if (!forever && !player.isGoldMvp() || ((Equip)eq).getStarForceLevel() < 20) {
                    enhanceNum = 15;
                    cooldown = 0;
                    growSize = 150;
                    moveCount = 15;
                } else if (!forever && !player.isDiamondMvp() || ((Equip)eq).getStarForceLevel() < 25) {
                    enhanceNum = 20;
                    cooldown = 0;
                    growSize = 300;
                    moveCount = 20;
                } else {
                    enhanceNum = 25;
                    cooldown = 0;
                    growSize = 750;
                    moveCount = 30;
                }
                Point p = player.getPosition().getLocation();
                boolean isFacingLeft = player.isFacingLeft();
                Rectangle box = attackEffect.calculateBoundingBox(p, isFacingLeft);
                box.grow(growSize, growSize);
                for (MapleMapObject mmo : player.getMap().getMonstersInRect(box)) {
                    MapleMonster mob = (MapleMonster)mmo;
                    if (mob.isBoss() || mob.isEliteMob() || mob.getStats().isIgnoreMoveImpact()) continue;
                    if (moveCount-- <= 0) break;
                    mob.move(p);
                }
            }
        }
        MapleMap map = player.getMap();
        MapleMapObject mob = null;
        if (!ai.mobAttackInfo.isEmpty() && (effect = player.getSkillEffect(80002762)) != null && effect.makeChanceResult(player)) {
            for (AttackMobInfo mai : ai.mobAttackInfo) {
                MapleMonster monster = map.getMobObject(mai.mobId);
                if (monster == null || mob != null && ((MapleMonster)mob).getMobMaxMp() >= monster.getMobMaxMp()) continue;
                mob = monster;
            }
            if (mob != null) {
                player.getTempValues().put("實戰的知識OID", mob.getObjectId());
                effect.unprimaryApplyTo(player, player.getPosition(), true);
            }
        }
        if (ai.skillId == 400021067) {
            player.getTempValues().put("冰雪之精神攻擊數量", ai.mobAttackInfo.size() == 1);
        }
        passive = ai.passive || passive;
        long totalDamage = 0L;
        boolean comboChecked = false;
        for (AttackMobInfo mai : ai.mobAttackInfo) {
            MapleStatEffect eff;
            AbstractSkillHandler handler;
            MapleMonster monster = map.getMobObject(mai.mobId);
            if (monster == null) continue;
            MapleStatEffect effect11 = player.getSkillEffect(80011158);
            if (effect11 != null) {
                LinkedList<MonsterStatus> toRemove = new LinkedList<MonsterStatus>();
                toRemove.add(MonsterStatus.PImmune);
                toRemove.add(MonsterStatus.MImmune);
                toRemove.add(MonsterStatus.PCounter);
                toRemove.add(MonsterStatus.MCounter);
                if (toRemove.stream().anyMatch(stat -> monster.getEffects().containsKey(stat))) {
                    monster.removeEffect(toRemove);
                    effect11.unprimaryPassiveApplyTo(player);
                }
            }
            monster.switchController(player);
            long damage = 0L;
            byte numDamage = 0;
            for (long dmg : mai.damages) {
                if (numDamage < ai.hits) {
                    damage += dmg;
                }
                numDamage = (byte)(numDamage + 1);
            }
            if (theSkill != null && !theSkill.isIgnoreCounter() && !theSkill.isVSkill() && player.getBuffedValue(SecondaryStat.IgnoreAllCounter) == null && player.getBuffedValue(SecondaryStat.IgnoreAllImmune) == null) {
                if (monster.getEffectHolder(MonsterStatus.PCounter) != null && player.getBuffedValue(SecondaryStat.IgnorePImmune) == null && (ai.isCloseRangeAttack || ai.isRangedAttack)) {
                    player.addHPMP(-5000, 0, false, true);
                } else if (monster.getEffectHolder(MonsterStatus.MCounter) != null && ai.isMagicAttack) {
                    player.addHPMP(-5000, 0, false, true);
                }
            }
            totalDamage += damage;
            MapleForceFactory mff = MapleForceFactory.getInstance();
            PlayerStats stats = player.getStat();
            int n4 = 0;
            int n5 = 0;
            for (Pair<Integer, Integer> pair : stats.getHPRecoverItemOption().values()) {
                if (!Randomizer.isSuccess((Integer)pair.right)) continue;
                n4 += ((Integer)pair.left).intValue();
            }
            for (Pair<Integer, Integer> pair : stats.getMPRecoverItemOption().values()) {
                if (!Randomizer.isSuccess((Integer)pair.right)) continue;
                n5 += ((Integer)pair.left).intValue();
            }
            AbstractSkillHandler abstractSkillHandler = handler = attackEffect == null ? null : attackEffect.getSkillHandler();
            if (handler == null) {
                handler = SkillClassFetcher.getHandlerByJob(player.getJobWithSub());
            }
            int handleRes = -1;
            if (handler != null) {
                SkillClassApplier applier = new SkillClassApplier();
                applier.ai = ai;
                applier.theSkill = theSkill;
                applier.effect = attackEffect;
                applier.passive = passive;
                handleRes = handler.onAttack(player, monster, applier);
                if (handleRes == 0) continue;
                if (handleRes == 1) {
                    passive = applier.passive;
                }
            }
            if (JobConstants.is惡魔(player.getJob())) {
                eff = player.getEffectForBuffStat(SecondaryStat.VampiricTouch);
                Party party = player.getParty();
                if (eff != null) {
                    if (party != null) {
                        Rectangle rect = attackEffect.calculateBoundingBox(player.getPosition(), player.isFacingLeft());
                        for (PartyMember member : party.getMembers()) {
                            if (member.getCharID() == player.getId() || member.getChr() == null || member.getChr().getMap() != player.getMap() || !rect.contains(member.getChr().getPosition()) || !member.getChr().getCheatTracker().canNextVampiricTouch()) continue;
                            member.getChr().addHPMP(Math.min(member.getChr().getStat().getCurrentMaxHP() * eff.getW() / 100, (int)Math.min(125000L, (long)eff.getX() * totalDamage / 100L)), 0, false, true);
                        }
                    }
                    if (player.getCheatTracker().canNextVampiricTouch()) {
                        n4 += (int)Math.min((long)stats.getCurrentMaxHP() * (long)eff.getW() / 100L, (long)eff.getX() * totalDamage / 100L);
                    }
                }
                n5 = 0;
            }
            if (n4 > 0 || n5 > 0) {
                player.addHPMP(Math.min(n4, stats.getCurrentMaxHP() * stats.hpRecover_limit / 100), Math.min(n5, stats.mpRecover_limit), false, n4 > 0);
            }
            if (JobConstants.is幻影俠盜(player.getJob())) {
                eff = player.getSkillEffect(24120002);
                int maxJS = 40;
                if (eff == null) {
                    eff = player.getSkillEffect(24100003);
                    maxJS = 20;
                }
                if (eff != null && attackEffect != null && attackEffect.getSourceId() != 24120055 && attackEffect.getSourceId() != 24120002 && attackEffect.getSourceId() != 24100003 && eff.makeChanceResult(player) && Randomizer.nextInt(100) < player.getStat().critRate) {
                    player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mff.getMapleForce(player, eff, 0)), true);
                    if (player.getJudgementStack() < maxJS) {
                        player.incJudgementStack();
                        player.updateJudgementStack();
                    }
                }
            }
            if (JobConstants.is惡魔殺手(player.getJob()) && attackEffect != null) {
                switch (attackEffect.getSourceId()) {
                    case 31000004: 
                    case 31001006: 
                    case 31001007: 
                    case 31001008: 
                    case 400011007: 
                    case 400011008: 
                    case 400011009: 
                    case 400011018: {
                        int dfheal = Randomizer.nextInt(5) + 1;
                        player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mff.getMapleForce(player, attackEffect, monster.getObjectId())), true);
                        eff = player.getSkillEffect(31110009);
                        if (eff != null && eff.makeChanceResult(player)) {
                            dfheal *= 2;
                            player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mff.getMapleForce(player, attackEffect, monster.getObjectId())), true);
                        }
                        player.addHPMP(0, dfheal, false);
                        player.handleForceGain(dfheal);
                        break;
                    }
                }
            }
            if (JobConstants.is凱撒(player.getJob())) {
                player.getSkillEffect(61121105);
                if (attackEffect != null && (attackEffect.getSourceId() == 61121105 || attackEffect.getSourceId() == 61121222)) {
                    attackEffect.applyAffectedArea(player, monster.getPosition());
                }
            }
            if (JobConstants.is凱內西斯(player.getJob())) {
                int n15;
                MapleStatEffect skillEffect6 = player.getSkillEffect(142110011);
                if (totalDamage > 0L && skillEffect6 != null && skillEffect6.makeChanceResult(player) && (n15 = attackEffect != null ? attackEffect.getSourceId() : 0) != 142110011 && n15 != 142001000 && n15 != 142100000 && n15 != 142110000 && n15 != 142121030 && n15 != 142101009 && n15 != 142120002 && n15 != 142001002 && n15 != 142101003 && n15 != 142111007 && n15 != 142121005) {
                    boolean n16 = false;
                    player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mff.getMapleForce(player, skillEffect6, 0)), true);
                }
            }
            if (JobConstants.is伊利恩(player.getJob())) {
                MapleStatEffect skillEffect7;
                if (attackEffect != null && (attackEffect.getSourceId() == 152100002 || attackEffect.getSourceId() == 152110002)) {
                    eff = null;
                    if (player.getSkillEffect(152120013) != null) {
                        eff = player.getSkillEffect(152120013);
                    } else if (player.getSkillEffect(152110010) != null) {
                        eff = player.getSkillEffect(152110010);
                    } else if (player.getSkillEffect(152100012) != null) {
                        eff = player.getSkillEffect(152100012);
                    }
                    if (eff != null) {
                        int effLevel = eff.getLevel();
                        eff = SkillFactory.getSkill(152000010).getEffect(effLevel);
                        if (eff != null) {
                            eff.applyMonsterEffect(player, monster, eff.getMobDebuffDuration(player));
                        }
                    }
                }
                if (attackEffect != null && attackEffect.getSourceId() == 152120001 && (skillEffect7 = player.getSkillEffect(152120002)) != null) {
                    List<Integer> moboids = player.getMap().getMapObjectsInRange(monster.getPosition(), 500.0, Collections.singletonList(MapleMapObjectType.MONSTER)).stream().map(MapleMapObject::getObjectId).collect(Collectors.toList());
                    player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mff.getMapleForce(player, skillEffect7, 0, moboids, monster.getPosition())), true);
                }
            }
            if (JobConstants.is虎影(player.getJob()) && attackEffect != null && attackEffect.getSourceId() == 164001001) {
                player.getMap().broadcastMessage(player, ForcePacket.forceAtomCreate(mff.getMapleForce(player, attackEffect, monster.getObjectId(), Collections.emptyList(), monster.getPosition())), true);
            }
            if (monster.isAlive()) {
                eff = player.getSkillEffect(80011158);
                if (eff != null && (monster.isBuffed(MonsterStatus.PImmune) || monster.isBuffed(MonsterStatus.MImmune) || monster.isBuffed(MonsterStatus.PCounter) || monster.isBuffed(MonsterStatus.MCounter))) {
                    monster.removeEffect(Arrays.asList(MonsterStatus.PImmune, MonsterStatus.MImmune, MonsterStatus.PCounter, MonsterStatus.MCounter));
                    eff.unprimaryPassiveApplyTo(player);
                }
                if ((eff = player.getSkillEffect(80011159)) != null && (monster.isBuffed(MonsterStatus.PowerUp) || monster.isBuffed(MonsterStatus.MagicUp) || monster.isBuffed(MonsterStatus.PGuardUp) || monster.isBuffed(MonsterStatus.MGuardUp) || monster.isBuffed(MonsterStatus.HardSkin))) {
                    monster.removeEffect(Arrays.asList(MonsterStatus.PowerUp, MonsterStatus.MagicUp, MonsterStatus.PGuardUp, MonsterStatus.MGuardUp, MonsterStatus.HardSkin));
                    eff.unprimaryPassiveApplyTo(player);
                }
                if (damage > 0L) {
                    SecondaryStatValueHolder holder2 = player.getBuffStatValueHolder(SecondaryStat.ErdaStack);
                    if (holder2 != null && holder2.value < 6 && Randomizer.isSuccess(holder2.effect.getX())) {
                        ++holder2.value;
                        player.send(BuffPacket.giveBuff(player, holder2.effect, Collections.singletonMap(SecondaryStat.ErdaStack, holder2.sourceID)));
                    }
                    DamageParse.applyMonsterEffect(attackEffect, player, monster, totalDamage);
                    monster.damage(player, ai.skillId, damage, false);
                }
            }
            if (!(monster.isAlive() || comboChecked || player.isStopComboKill())) {
                player.dropComboKillBall(monster.getPosition());
                comboChecked = true;
            }
            DamageParse.applyAttackEffect(attackEffect, player, monster, totalDamage);
            if (player.inEvent() || player.isOverMobLevelTip() || monster.isBoss() || Math.abs(player.getLevel() - monster.getMobLevel()) <= 20) continue;
            player.setOverMobLevelTip(true);
            player.dropSpecialTopMsg("狩獵不在等級範圍內的怪物時，經驗值與楓幣獲得量會大幅減少。", 3, 20, 20, 0);
        }
        if (totalDamage > 0L && ai.skillId > 0 && !SkillConstants.isPassiveAttackSkill(ai.skillId)) {
            int finalSkillId = player.getStat().getFinalAttackSkill();
            block3 : switch (finalSkillId) {
                case 5120021: {
                    switch (SkillConstants.getLinkedAttackSkill(ai.skillId)) {
                        case 5001002: 
                        case 5111009: 
                        case 5121007: 
                        case 400051042: 
                        case 400051070: {
                            break block3;
                        }
                    }
                    finalSkillId = 0;
                    break;
                }
                case 5310004: {
                    if (ai.attackType != AttackInfo.AttackType.SummonedAttack) break;
                    finalSkillId = 0;
                }
            }
            MapleStatEffect skillEffect = player.getSkillEffect(finalSkillId);
            if (player.isDebug()) {
                player.dropMessageIfAdmin(5, "開始處理終極攻擊, SkillID:" + ai.skillId + ",FinalSkillID:" + finalSkillId + ",Effect:" + String.valueOf(skillEffect));
            }
            if (finalSkillId > 0 && finalSkillId != ai.skillId && skillEffect != null) {
                boolean suc = finalSkillId == 5310004 || skillEffect.makeChanceResult(player);
                Item item = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(JobConstants.is神之子(player.getJob()) && player.isBeta() ? -10 : -11));
                MapleWeapon wt = item == null ? MapleWeapon.沒有武器 : MapleWeapon.getByItemID(item.getItemId());
                if (wt != MapleWeapon.沒有武器) {
                    LinkedList<Integer> oids = new LinkedList<Integer>();
                    if (suc) {
                        for (AttackMobInfo mai : ai.mobAttackInfo) {
                            oids.add(mai.mobId);
                        }
                    }
                    player.getClient().announce(MaplePacketCreator.FinalAttack(player, player.getCheatTracker().getFinalAttackTime(), suc, ai.skillId, finalSkillId, wt.getWeaponType(), oids));
                }
            }
        }
        if (totalDamage > 0L && player.getBuffStatValueHolder(80011248) != null) {
            player.addShieldHP(totalDamage > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)totalDamage);
        }
        if ((holder = player.getBuffStatValueHolder(SecondaryStat.RunePurification)) != null && holder.value == 1 && (summonHolder = player.getBuffStatValueHolder(SecondaryStat.IndieBuffIcon, 80002888)) != null) {
            if (holder.z >= 1000) {
                holder.value = 2;
                summonHolder.startTime = holder.startTime = System.currentTimeMillis();
                holder.schedule.cancel(true);
                summonHolder.schedule.cancel(true);
                summonHolder.schedule = holder.schedule = Timer.BuffTimer.getInstance().schedule(new MapleStatEffect.CancelEffectAction(player, holder.effect, holder.startTime, new EnumMap<SecondaryStat, Integer>(holder.effect.getStatups())), holder.localDuration);
                MapleSummon summon = player.getSummonBySkillID(80002888);
                if (summon != null) {
                    summon.setCreateTime(holder.startTime);
                }
            }
            player.send(BuffPacket.giveBuff(player, null, Collections.singletonMap(SecondaryStat.RunePurification, 80002888)));
        }
        player.monsterMultiKill();
        DamageParse.afterAttack(attackEffect, player, totalDamage, ai.rangedAttackPos, ai, passive);
        player.setLastAttackSkillId(ai.skillId);
        player.getCheatTracker().checkAttack(ai.skillId, ai.lastAttackTickCount);
    }

    public static void applyMonsterEffect(MapleStatEffect effect, MapleCharacter applyfrom, MapleMonster applyto, long totalDamage) {
        int debuffDuration;
        SecondaryStatValueHolder holder;
        MapleForceFactory mmf = MapleForceFactory.getInstance();
        if (JobConstants.is暗夜行者(applyfrom.getJob())) {
            MapleSummon summonBySkillID;
            MapleStatEffect skillEffect14;
            MapleStatEffect effecForBuffStat6 = applyfrom.getEffectForBuffStat(SecondaryStat.ElementDarkness);
            if (effecForBuffStat6 != null && applyto.isAlive() && effect != null && effecForBuffStat6.applyMonsterEffect(applyfrom, applyto, effecForBuffStat6.getDotTime(applyfrom) * 1000) && (skillEffect14 = applyfrom.getSkillEffect(14120009)) != null) {
                skillEffect14.unprimaryPassiveApplyTo(applyfrom);
            }
            MapleStatEffect effecForBuffStat7 = applyfrom.getEffectForBuffStat(SecondaryStat.NightWalkerBat);
            if (effect != null && effect.getBulletCount() > 1 && effecForBuffStat7 != null && applyto.isAlive() && applyfrom.getSummonCountBySkill(14000027) > 0 && (effecForBuffStat7.makeChanceResult(applyfrom) || applyto.getEffectHolder(applyto.getId(), 14001021) != null && effecForBuffStat7.makeChanceResult(applyfrom)) && (summonBySkillID = applyfrom.getSummonBySkillID(14000027)) != null) {
                MapleStatEffect nj = summonBySkillID.getEffect();
                boolean n6 = false;
                MapleForceAtom a = mmf.getMapleForce(applyfrom, nj, 0);
                a.setForcedTarget(summonBySkillID.getPosition());
                applyfrom.getMap().broadcastMessage(applyfrom, SummonPacket.summonSkill(applyfrom.getId(), summonBySkillID.getObjectId(), 0), true);
                applyfrom.getMap().broadcastMessage(applyfrom, ForcePacket.forceAtomCreate(a), true);
                applyfrom.removeSummon(summonBySkillID, 0);
            }
        }
        if (effect != null && applyto.isAlive() && totalDamage > 0L && (holder = applyfrom.getBuffStatValueHolder(SecondaryStat.ErdaRevert)) != null) {
            holder.effect.applyMonsterEffect(applyfrom, applyto, effect.getMobDebuffDuration(applyfrom));
        }
        if (effect != null && (debuffDuration = effect.getMobDebuffDuration(applyfrom)) > 0 && debuffDuration != 2100000000 && !effect.getMonsterStatus().isEmpty() && effect.getMobCount() > 0 && effect.getAttackCount() > 0) {
            effect.applyMonsterEffect(applyfrom, applyto, debuffDuration);
        }
    }

    public static void applyAttackEffect(MapleStatEffect effect, MapleCharacter applyfrom, MapleMonster applyto, long totalDamage) {
        MapleMonster mobObject;
        MapleStatEffect eff;
        AbstractSkillHandler handler;
        AbstractSkillHandler abstractSkillHandler = handler = effect == null ? null : effect.getSkillHandler();
        if (handler == null) {
            handler = SkillClassFetcher.getHandlerByJob(applyfrom.getJobWithSub());
        }
        int handleRes = -1;
        if (handler != null) {
            SkillClassApplier applier = new SkillClassApplier();
            applier.effect = effect;
            applier.totalDamage = totalDamage;
            handleRes = handler.onApplyAttackEffect(applyfrom, applyto, applier);
            if (handleRes == 0) {
                return;
            }
            if (handleRes == 1) {
                effect = applier.effect;
                totalDamage = applier.totalDamage;
            }
        }
        if (effect != null && applyto.isAlive()) {
            Skill skill = SkillFactory.getSkill(effect.getSourceId());
            MonsterEffectHolder meh2 = applyto.getEffectHolder(MonsterStatus.BahamutLightElemAddDam);
            if (meh2 != null && skill != null && skill.getId() != meh2.sourceID && skill.getElement() == Element.神聖) {
                applyto.removeEffect(Collections.singletonList(MonsterStatus.BahamutLightElemAddDam));
            }
        }
        if (!applyfrom.isSkillCooling(80002770) && (eff = applyfrom.getSkillEffect(80002770)) != null && (applyto.getObjectId() == applyfrom.getBuffedIntZ(SecondaryStat.NoviceMagicianLink) || applyto.getObjectId() == applyfrom.getBuffedIntZ(SecondaryStat.Shadower_Assassination) || applyto.getAllEffects().values().stream().flatMap(Collection::stream).collect(Collectors.toCollection(LinkedList::new)).stream().anyMatch(meh -> meh != null && !(meh.effect instanceof MobSkill)))) {
            eff.applyTo(applyfrom);
        }
        if (JobConstants.is冒險家法師(applyfrom.getJob())) {
            MapleStatEffect skillEffect2;
            int n3;
            int n2 = applyfrom.getJob() == 212 ? (n3 = 2120010) : (applyfrom.getJob() == 222 ? (n3 = 2220010) : (applyfrom.getJob() == 232 ? (n3 = 2320011) : (n3 = 0)));
            int n4 = n3;
            if (n2 > 0 && totalDamage > 0L && (skillEffect2 = applyfrom.getSkillEffect(n4)) != null && skillEffect2.makeChanceResult(applyfrom)) {
                skillEffect2.unprimaryPassiveApplyTo(applyfrom);
            }
        }
        if ((applyto.getEffectHolder(MonsterStatus.SeperateSoulP) != null || applyto.getEffectHolder(MonsterStatus.SeperateSoulC) != null) && applyto.getSeperateSoulSrcOID() > 0 && (mobObject = applyfrom.getMap().getMobObject(applyto.getSeperateSoulSrcOID())) != null) {
            mobObject.damage(applyfrom, effect != null ? effect.getSourceId() : 0, totalDamage, false);
        }
        if (!applyto.isAlive()) {
            MapleStatEffect eff1;
            Skill skil;
            eff = applyfrom.getSkillEffect(160010001);
            if (eff == null) {
                eff = applyfrom.getSkillEffect(80003058);
            }
            if (eff != null && !applyfrom.isSkillCooling(eff.getSourceId()) && (skil = SkillFactory.getSkill(80003070)) != null && (eff1 = skil.getEffect(eff.getLevel())) != null) {
                SecondaryStatValueHolder mbsvh = applyfrom.getBuffStatValueHolder(SecondaryStat.AMLinkSkill);
                if (mbsvh == null) {
                    eff1.unprimaryApplyTo(applyfrom, applyfrom.getPosition(), true);
                } else {
                    mbsvh.value = Math.min(mbsvh.value + 1, eff.getX());
                    if (mbsvh.value >= eff.getX()) {
                        applyfrom.dispelEffect(SecondaryStat.AMLinkSkill);
                        eff.unprimaryApplyTo(applyfrom, applyfrom.getPosition(), true);
                    } else {
                        mbsvh.startTime = System.currentTimeMillis();
                        applyfrom.send(BuffPacket.giveBuff(applyfrom, mbsvh.effect, Collections.singletonMap(SecondaryStat.AMLinkSkill, mbsvh.effect.getSourceId())));
                    }
                }
            }
        }
        if (!applyto.isAlive() && applyfrom.checkSoulWeapon()) {
            applyfrom.handleSoulMP(applyto);
            applyfrom.checkSoulState(false);
        }
    }
}

