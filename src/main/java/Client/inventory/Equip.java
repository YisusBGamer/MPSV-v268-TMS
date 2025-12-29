/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Client.inventory.EquipStats
 *  Client.inventory.MapleRing
 *  Client.inventory.SocketFlag
 *  Config.constants.ItemConstants$方塊
 *  Config.constants.ItemConstants$方塊$CubeType
 *  Server.world.WorldBroadcastService
 *  connection.packet.OverseasPacket
 */
package Client.inventory;

import Client.MapleCharacter;
import Client.inventory.EnhanceResultType;
import Client.inventory.EquipBaseStat;
import Client.inventory.EquipSpecialStat;
import Client.inventory.EquipStats;
import Client.inventory.Item;
import Client.inventory.ItemAttribute;
import Client.inventory.MapleAndroid;
import Client.inventory.MapleInventoryType;
import Client.inventory.MapleRing;
import Client.inventory.MapleWeapon;
import Client.inventory.NirvanaFlame;
import Client.inventory.SocketFlag;
import Client.inventory.StarForce;
import Config.configs.ServerConfig;
import Config.constants.GameConstants;
import Config.constants.ItemConstants;
import Config.constants.JobConstants;
import Net.auth.Auth;
import Net.server.MapleInventoryManipulator;
import Net.server.MapleItemInformationProvider;
import Net.server.StructItemOption;
import Packet.EffectPacket;
import Packet.InventoryPacket;
import Packet.MaplePacketCreator;
import Server.world.WorldBroadcastService;
import SwordieX.util.FileTime;
import connection.OutPacket;
import connection.packet.OverseasPacket;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tools.DateUtil;
import tools.Pair;
import tools.Randomizer;

public class Equip
extends Item
implements Serializable {
    public static final long ARMOR_RATIO = 350000L;
    public static final long WEAPON_RATIO = 700000L;
    private static final long serialVersionUID = -4385634094556865314L;
    private byte restUpgradeCount = 0;
    private byte currentUpgradeCount = 0;
    private byte vicioushammer = 0;
    private byte platinumhammer = 0;
    private byte state = 0;
    private byte addState;
    private short enchantBuff = 0;
    private short reqLevel = (short)-1;
    private short yggdrasilWisdom = 0;
    private short bossDamage = 0;
    private short ignorePDR = 0;
    private short totalDamage = 0;
    private short allStat = 0;
    private short karmaCount = (short)-1;
    private boolean finalStrike = false;
    private short str = 0;
    private short dex = 0;
    private short _int = 0;
    private short luk = 0;
    private short hp = 0;
    private short mp = 0;
    private short pad = 0;
    private short mad = 0;
    private short pdd = 0;
    private short mdd = 0;
    private short acc = 0;
    private short avoid = 0;
    private short hands = 0;
    private short speed = 0;
    private short jump = 0;
    private short charmExp = 0;
    private short pvpDamage = 0;
    private int durability = -1;
    private int incSkill = -1;
    private int potential1 = 0;
    private int potential2 = 0;
    private int potential3 = 0;
    private int potential4 = 0;
    private int potential5 = 0;
    private int potential6 = 0;
    private int socket1 = -1;
    private int socket2 = -1;
    private int socket3 = -1;
    private int itemSkin = 0;
    private MapleRing ring = null;
    private MapleAndroid android = null;
    private int lockSlot = 0;
    private short lockId = 0;
    private byte sealedLevel = 0;
    private long sealedExp = 0L;
    private long itemEXP = 0L;
    private short soulOptionID;
    private short soulSocketID;
    private short soulOption;
    private int soulSkill = 0;
    private Map<EquipStats, Long> statsTest = new LinkedHashMap<EquipStats, Long>();
    private int iIncReq;
    private NirvanaFlame nirvanaFlame = new NirvanaFlame();
    private StarForce starForce = new StarForce();
    private int failCount = 0;
    private int ARCExp = 1;
    private short ARC;
    private short ARCLevel = 1;
    private int autExp = 1;
    private short aut;
    private short autLevel = 1;
    private boolean mvpEquip = false;

    public Equip(int id, short position, int sn, int flag, short espos) {
        super(id, position, (short)1, flag, sn, espos);
    }

    public enum ScrollResult {

        失敗, 成功, 消失
    }

    @Override
    public Item copy() {
        Equip ret = new Equip(this.getItemId(), this.getPosition(), this.getSN(), this.getAttribute(), this.getESPos());
        ret.mvpEquip = this.mvpEquip;
        ret.str = this.str;
        ret.dex = this.dex;
        ret._int = this._int;
        ret.luk = this.luk;
        ret.hp = this.hp;
        ret.mp = this.mp;
        ret.mad = this.mad;
        ret.mdd = this.mdd;
        ret.pad = this.pad;
        ret.pdd = this.pdd;
        ret.acc = this.acc;
        ret.avoid = this.avoid;
        ret.hands = this.hands;
        ret.speed = this.speed;
        ret.jump = this.jump;
        ret.restUpgradeCount = this.restUpgradeCount;
        ret.currentUpgradeCount = this.currentUpgradeCount;
        ret.itemEXP = this.itemEXP;
        ret.durability = this.durability;
        ret.vicioushammer = this.vicioushammer;
        ret.platinumhammer = this.platinumhammer;
        ret.state = this.state;
        ret.addState = this.addState;
        ret.potential1 = this.potential1;
        ret.potential2 = this.potential2;
        ret.potential3 = this.potential3;
        ret.potential4 = this.potential4;
        ret.potential5 = this.potential5;
        ret.potential6 = this.potential6;
        ret.charmExp = this.charmExp;
        ret.pvpDamage = this.pvpDamage;
        ret.incSkill = this.incSkill;
        ret.socket1 = this.socket1;
        ret.socket2 = this.socket2;
        ret.socket3 = this.socket3;
        ret.itemSkin = this.itemSkin;
        ret.enchantBuff = this.enchantBuff;
        ret.reqLevel = this.reqLevel;
        ret.yggdrasilWisdom = this.yggdrasilWisdom;
        ret.finalStrike = this.finalStrike;
        ret.bossDamage = this.bossDamage;
        ret.ignorePDR = this.ignorePDR;
        ret.totalDamage = this.totalDamage;
        ret.allStat = this.allStat;
        ret.karmaCount = this.karmaCount;
        ret.statsTest = this.statsTest;
        ret.setGMLog(this.getGMLog());
        ret.setGiftFrom(this.getGiftFrom());
        ret.setOwner(this.getOwner());
        ret.setQuantity(this.getQuantity());
        ret.setExpiration(this.getTrueExpiration());
        ret.setInventoryId(this.getInventoryId());
        ret.lockSlot = this.lockSlot;
        ret.lockId = this.lockId;
        ret.sealedLevel = this.sealedLevel;
        ret.sealedExp = this.sealedExp;
        ret.soulOptionID = this.soulOptionID;
        ret.soulSocketID = this.soulSocketID;
        ret.soulOption = this.soulOption;
        ret.soulSkill = this.soulSkill;
        ret.nirvanaFlame = new NirvanaFlame(this.nirvanaFlame);
        ret.nirvanaFlame.resetEquipExStats(ret);
        ret.starForce = new StarForce(this.starForce);
        ret.starForce.resetEquipStats(ret);
        ret.ARC = this.ARC;
        ret.ARCExp = this.ARCExp;
        ret.ARCLevel = this.ARCLevel;
        ret.aut = this.aut;
        ret.autExp = this.autExp;
        ret.autLevel = this.autLevel;
        ret.mvpEquip = this.mvpEquip;
        return ret;
    }

    public Item inherit(Equip srcEquip, Equip decEquip) {
        this.str = (short)(this.str + (short)(srcEquip.str - decEquip.str));
        this.dex = (short)(this.dex + (short)(srcEquip.dex - decEquip.dex));
        this._int = (short)(this._int + (short)(srcEquip._int - decEquip._int));
        this.luk = (short)(this.luk + (short)(srcEquip.luk - decEquip.luk));
        this.hp = (short)(this.hp + (short)(srcEquip.hp - decEquip.hp));
        this.mp = (short)(this.mp + (short)(srcEquip.mp - decEquip.mp));
        this.mad = (short)(this.mad + (short)(srcEquip.mad - decEquip.mad));
        this.mdd = (short)(this.mdd + (short)(srcEquip.mdd - decEquip.mdd));
        this.pad = (short)(this.pad + (short)(srcEquip.pad - decEquip.pad));
        this.pdd = (short)(this.pdd + (short)(srcEquip.pdd - decEquip.pdd));
        this.acc = (short)(this.acc + (short)(srcEquip.acc - decEquip.acc));
        this.avoid = (short)(this.avoid + (short)(srcEquip.avoid - decEquip.avoid));
        this.hands = (short)(this.hands + (short)(srcEquip.hands - decEquip.hands));
        this.speed = (short)(this.speed + (short)(srcEquip.speed - decEquip.speed));
        this.jump = (short)(this.jump + (short)(srcEquip.jump - decEquip.jump));
        this.restUpgradeCount = srcEquip.restUpgradeCount;
        this.currentUpgradeCount = srcEquip.currentUpgradeCount;
        this.itemEXP = srcEquip.itemEXP;
        this.durability = srcEquip.durability;
        this.vicioushammer = srcEquip.vicioushammer;
        this.platinumhammer = srcEquip.platinumhammer;
        this.charmExp = srcEquip.charmExp;
        this.pvpDamage = srcEquip.pvpDamage;
        this.incSkill = srcEquip.incSkill;
        this.enchantBuff = srcEquip.enchantBuff;
        this.reqLevel = srcEquip.reqLevel;
        this.yggdrasilWisdom = srcEquip.yggdrasilWisdom;
        this.finalStrike = srcEquip.finalStrike;
        this.bossDamage = srcEquip.bossDamage;
        this.ignorePDR = srcEquip.ignorePDR;
        this.totalDamage = srcEquip.totalDamage;
        this.allStat = srcEquip.allStat;
        this.karmaCount = srcEquip.karmaCount;
        this.nirvanaFlame = new NirvanaFlame(srcEquip.nirvanaFlame);
        this.nirvanaFlame.resetEquipExStats(this);
        this.starForce = new StarForce(srcEquip.starForce);
        this.starForce.resetEquipStats(this);
        this.soulOptionID = srcEquip.soulOptionID;
        this.soulSocketID = srcEquip.soulSocketID;
        this.soulOption = srcEquip.soulOption;
        this.sealedLevel = srcEquip.sealedLevel;
        this.sealedExp = srcEquip.sealedExp;
        this.setGiftFrom(this.getGiftFrom());
        this.copyPotential(srcEquip);
        return this;
    }

    @Override
    public byte getType() {
        return 1;
    }

    public Equip copyPotential(Equip equip) {
        this.potential1 = equip.potential1;
        this.potential2 = equip.potential2;
        this.potential3 = equip.potential3;
        this.potential4 = equip.potential4;
        this.potential5 = equip.potential5;
        this.potential6 = equip.potential6;
        this.state = equip.state;
        this.addState = equip.addState;
        return this;
    }

    public byte getRestUpgradeCount() {
        return this.restUpgradeCount;
    }

    public void setRestUpgradeCount(byte restUpgradeCount) {
        this.restUpgradeCount = restUpgradeCount;
    }

    public short getTotalStr() {
        return (short)(this.str + this.starForce.getStr() + this.nirvanaFlame.getStr());
    }

    public short getSF_Str() {
        return (short)(this.starForce.getStr() + this.nirvanaFlame.getStr());
    }

    public short getStr() {
        return this.str;
    }

    public void setStr(short str) {
        if (str < 0) {
            str = 0;
        }
        this.str = str;
    }

    public short getTotalDex() {
        return (short)(this.dex + this.starForce.getDex() + this.nirvanaFlame.getDex());
    }

    public short getSF_Dex() {
        return (short)(this.starForce.getDex() + this.nirvanaFlame.getDex());
    }

    public short getDex() {
        return this.dex;
    }

    public void setDex(short dex) {
        if (dex < 0) {
            dex = 0;
        }
        this.dex = dex;
    }

    public short getTotalInt() {
        return (short)(this._int + this.starForce.getInt() + this.nirvanaFlame.getInt());
    }

    public short getSF_Int() {
        return (short)(this.starForce.getInt() + this.nirvanaFlame.getInt());
    }

    public short getInt() {
        return this._int;
    }

    public void setInt(short _int) {
        if (_int < 0) {
            _int = 0;
        }
        this._int = _int;
    }

    public short getTotalLuk() {
        return (short)(this.luk + this.starForce.getLuk() + this.nirvanaFlame.getLuk());
    }

    public short getSF_Luk() {
        return (short)(this.starForce.getLuk() + this.nirvanaFlame.getLuk());
    }

    public short getLuk() {
        return this.luk;
    }

    public void setLuk(short luk) {
        if (luk < 0) {
            luk = 0;
        }
        this.luk = luk;
    }

    public short getTotalHp() {
        return (short)(this.hp + this.starForce.getHp() + this.nirvanaFlame.getHp());
    }

    public short getSF_Hp() {
        return (short)(this.starForce.getHp() + this.nirvanaFlame.getHp());
    }

    public short getHp() {
        return this.hp;
    }

    public void setHp(short hp) {
        if (hp < 0) {
            hp = 0;
        }
        this.hp = hp;
    }

    public short getTotalMp() {
        return (short)(this.mp + this.starForce.getMp() + this.nirvanaFlame.getMp());
    }

    public short getSF_Mp() {
        return (short)(this.starForce.getMp() + this.nirvanaFlame.getMp());
    }

    public short getMp() {
        return this.mp;
    }

    public void setMp(short mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    public short getTotalPad() {
        return (short)(this.pad + this.starForce.getPad() + this.nirvanaFlame.getPad());
    }

    public short getSF_Pad() {
        return (short)(this.starForce.getPad() + this.nirvanaFlame.getPad());
    }

    public short getPad() {
        return this.pad;
    }

    public void setPad(short pad) {
        if (pad < 0) {
            pad = 0;
        }
        this.pad = pad;
    }

    public short getTotalMad() {
        return (short)(this.mad + this.starForce.getMad() + this.nirvanaFlame.getMad());
    }

    public short getMad() {
        return this.mad;
    }

    public short getSF_Mad() {
        return (short)(this.starForce.getMad() + this.nirvanaFlame.getMad());
    }

    public void setMad(short mad) {
        if (mad < 0) {
            mad = 0;
        }
        this.mad = mad;
    }

    public short getTotalPdd() {
        return (short)(this.pdd + this.starForce.getPdd() + this.nirvanaFlame.getPdd());
    }

    public short getSF_Pdd() {
        return (short)(this.starForce.getPdd() + this.nirvanaFlame.getPdd());
    }

    public short getPdd() {
        return this.pdd;
    }

    public void setPdd(short pdd) {
        if (pdd < 0) {
            pdd = 0;
        }
        this.pdd = pdd;
    }

    public short getTotalMdd() {
        return (short)(this.mdd + this.starForce.getMdd() + this.nirvanaFlame.getMdd());
    }

    public short getSF_Mdd() {
        return (short)(this.starForce.getMdd() + this.nirvanaFlame.getMdd());
    }

    public short getMdd() {
        return this.mdd;
    }

    public void setMdd(short mdd) {
        if (mdd < 0) {
            mdd = 0;
        }
        this.mdd = mdd;
    }

    public short getTotalAcc() {
        return (short)(this.acc + this.starForce.getAcc() + this.nirvanaFlame.getAcc());
    }

    public short getAcc() {
        return this.acc;
    }

    public void setAcc(short acc) {
        if (acc < 0) {
            acc = 0;
        }
        this.acc = acc;
    }

    public short getTotalAvoid() {
        return (short)(this.avoid + this.starForce.getAvoid() + this.nirvanaFlame.getAvoid());
    }

    public short getAvoid() {
        return this.avoid;
    }

    public void setAvoid(short avoid) {
        if (avoid < 0) {
            avoid = 0;
        }
        this.avoid = avoid;
    }

    public short getTotalHands() {
        return (short)(this.hands + this.starForce.getHands() + this.nirvanaFlame.getHands());
    }

    public short getSF_Hands() {
        return (short)(this.starForce.getHands() + this.nirvanaFlame.getHands());
    }

    public short getHands() {
        return this.hands;
    }

    public void setHands(short hands) {
        if (hands < 0) {
            hands = 0;
        }
        this.hands = hands;
    }

    public short getTotalSpeed() {
        return (short)(this.speed + this.starForce.getSpeed() + this.nirvanaFlame.getSpeed());
    }

    public short getSpeed() {
        return this.speed;
    }

    public short getSF_Speed() {
        return (short)(this.starForce.getSpeed() + this.nirvanaFlame.getSpeed());
    }

    public void setSpeed(short speed) {
        if (speed < 0) {
            speed = 0;
        }
        this.speed = speed;
    }

    public short getTotalJump() {
        return (short)(this.jump + this.starForce.getJump() + this.nirvanaFlame.getJump());
    }

    public short getSF_Jump() {
        return (short)(this.starForce.getJump() + this.nirvanaFlame.getJump());
    }

    public short getJump() {
        return this.jump;
    }

    public void setJump(short jump) {
        if (jump < 0) {
            jump = 0;
        }
        this.jump = jump;
    }

    public byte getCurrentUpgradeCount() {
        return this.currentUpgradeCount;
    }

    public void setCurrentUpgradeCount(byte currentUpgradeCount) {
        this.currentUpgradeCount = currentUpgradeCount;
    }

    public byte getViciousHammer() {
        return this.vicioushammer;
    }

    public void setViciousHammer(byte ham) {
        this.vicioushammer = ham;
    }

    public byte getPlatinumHammer() {
        return this.platinumhammer;
    }

    public void setPlatinumHammer(byte ham) {
        this.platinumhammer = ham;
    }

    public byte getTotalHammer() {
        return (byte)(this.vicioushammer + this.platinumhammer);
    }

    public long getItemEXP() {
        return this.itemEXP;
    }

    public void setItemEXP(long itemEXP) {
        if (itemEXP < 0L) {
            itemEXP = 0L;
        }
        this.itemEXP = itemEXP;
    }

    public long getEquipExp() {
        if (this.itemEXP <= 0L) {
            return 0L;
        }
        if (ItemConstants.類型.武器(this.getItemId())) {
            return this.itemEXP / 700000L;
        }
        return this.itemEXP / 350000L;
    }

    public long getEquipExpForLevel() {
        if (this.getEquipExp() <= 0L) {
            return 0L;
        }
        long expz = this.getEquipExp();
        for (int i = this.getBaseLevel(); i <= ItemConstants.getMaxLevel(this.getItemId()) && expz >= (long)ItemConstants.getExpForLevel(i, this.getItemId()); expz -= (long)ItemConstants.getExpForLevel(i, this.getItemId()), ++i) {
        }
        return expz;
    }

    public long getExpPercentage() {
        if (this.getEquipLevel() < this.getBaseLevel() || this.getEquipLevel() > ItemConstants.getMaxLevel(this.getItemId()) || ItemConstants.getExpForLevel(this.getEquipLevel(), this.getItemId()) <= 0) {
            return 0L;
        }
        return this.getEquipExpForLevel() * 100L / (long)ItemConstants.getExpForLevel(this.getEquipLevel(), this.getItemId());
    }

    public int getEquipLevel() {
        int fixLevel = MapleItemInformationProvider.getInstance().getEquipmentSkillsFixLevel(this.getItemId());
        if (fixLevel > 0) {
            return fixLevel;
        }
        int maxLevel = ItemConstants.getMaxLevel(this.getItemId());
        int levelz = this.getBaseLevel();
        if (this.getEquipExp() <= 0L) {
            return Math.min(levelz, maxLevel);
        }
        long expz = this.getEquipExp();
        for (int i = levelz; i < maxLevel && expz >= (long)ItemConstants.getExpForLevel(i, this.getItemId()); expz -= (long)ItemConstants.getExpForLevel(i, this.getItemId()), ++i) {
            ++levelz;
        }
        return levelz;
    }

    public int getBaseLevel() {
        return GameConstants.getStatFromWeapon(this.getItemId()) == null ? 1 : 0;
    }

    @Override
    public void setQuantity(short quantity) {
        if (quantity < 0 || quantity > 1) {
            throw new RuntimeException("設置裝備的數量錯誤 欲設置的數量： " + quantity + " (道具ID: " + this.getItemId() + ")");
        }
        super.setQuantity(quantity);
    }

    public int getDurability() {
        return this.durability;
    }

    public void setDurability(int dur) {
        this.durability = dur;
    }

    public int getPotential(int pos, boolean add) {
        switch (pos) {
            case 1: {
                if (add) {
                    return this.potential4;
                }
                return this.potential1;
            }
            case 2: {
                if (add) {
                    return this.potential5;
                }
                return this.potential2;
            }
            case 3: {
                if (add) {
                    return this.potential6;
                }
                return this.potential3;
            }
        }
        return 0;
    }

    public void setPotential(int potential, int pos, boolean add) {
        switch (pos) {
            case 1: {
                if (add) {
                    this.potential4 = potential;
                    break;
                }
                this.potential1 = potential;
                break;
            }
            case 2: {
                if (add) {
                    this.potential5 = potential;
                    break;
                }
                this.potential2 = potential;
                break;
            }
            case 3: {
                if (add) {
                    this.potential6 = potential;
                    break;
                }
                this.potential3 = potential;
            }
        }
    }

    public int getPotential1() {
        return this.potential1;
    }

    public void setPotential1(int en) {
        this.potential1 = en;
    }

    public int getPotential2() {
        return this.potential2;
    }

    public void setPotential2(int en) {
        this.potential2 = en;
    }

    public int getPotential3() {
        return this.potential3;
    }

    public void setPotential3(int en) {
        this.potential3 = en;
    }

    public int getPotential4() {
        return this.potential4;
    }

    public void setPotential4(int en) {
        this.potential4 = en;
    }

    public int getPotential5() {
        return this.potential5;
    }

    public void setPotential5(int en) {
        this.potential5 = en;
    }

    public int getPotential6() {
        return this.potential6;
    }

    public void setPotential6(int en) {
        this.potential6 = en;
    }

    public byte getState(boolean add) {
        if (ServerConfig.DISABLE_POTENTIAL) {
            return 0;
        }
        if (add) {
            return this.addState;
        }
        return this.state;
    }

    public void setState(byte en, boolean add) {
        if (ServerConfig.DISABLE_POTENTIAL) {
            en = 0;
        }
        if (add) {
            this.addState = en;
        } else {
            this.state = en;
        }
    }

    public void initAllState() {
        this.initState(false);
        this.initState(true);
    }

    public void initState(boolean useAddPot) {
        int v3;
        int v2;
        int v1;
        int ret = 0;
        if (!useAddPot) {
            v1 = this.potential1;
            v2 = this.potential2;
            v3 = this.potential3;
        } else {
            v1 = this.potential4;
            v2 = this.potential5;
            v3 = this.potential6;
        }
        if (v1 >= 40000 || v2 >= 40000 || v3 >= 40000) {
            ret = 20;
        } else if (v1 >= 30000 || v2 >= 30000 || v3 >= 30000) {
            ret = 19;
        } else if (v1 >= 20000 || v2 >= 20000 || v3 >= 20000) {
            ret = 18;
        } else if (v1 >= 1 || v2 >= 1 || v3 >= 1) {
            ret = 17;
        } else if (v1 == -20 || v2 == -20 || v1 == -4 || v2 == -4) {
            ret = 4;
        } else if (v1 == -19 || v2 == -19 || v1 == -3 || v2 == -3) {
            ret = 3;
        } else if (v1 == -18 || v2 == -18 || v1 == -2 || v2 == -2) {
            ret = 2;
        } else if (v1 == -17 || v2 == -17 || v1 == -1 || v2 == -1) {
            ret = 1;
        } else if (v1 < 0 || v2 < 0 || v3 < 0) {
            return;
        }
        this.setState((byte)ret, useAddPot);
    }

    public void resetPotential_Fuse(boolean half, int potentialState) {
        potentialState = -potentialState;
        if (Randomizer.nextInt(100) < 4) {
            potentialState -= Randomizer.nextInt(100) < 4 ? 2 : 1;
        }
        this.setPotential1(potentialState);
        this.setPotential2(Randomizer.nextInt(half ? 5 : 10) == 0 ? potentialState : 0);
        this.setPotential3(0);
        this.initState(false);
    }

    public void renewPotential(boolean add) {
        this.renewPotential(0, add);
    }

    public void renewPotential(int rank, boolean add) {
        this.renewPotential(rank, false, add);
    }

    public void renewPotential(boolean third, boolean add) {
        this.renewPotential(0, third, add);
    }

    public void renewPotential(int rank, boolean third, boolean add) {
        int state = switch (rank) {
            case 1 -> -17;
            case 2 -> -18;
            case 3 -> -19;
            case 4 -> -20;
            default -> Randomizer.nextInt(100) < 4 ? (Randomizer.nextInt(100) < 4 ? -19 : -18) : -17;
        };
        boolean b3 = this.getState(add) != 0 && this.getPotential(3, add) != 0 || third;
        this.setPotential(state, 1, add);
        this.setPotential(Randomizer.nextInt(10) <= 1 || b3 ? state : 0, 2, add);
        this.setPotential(0, 3, add);
        this.initState(add);
    }

    public boolean useCube(int cubeId, MapleCharacter player) {
        return this.useCube(cubeId, player, 0);
    }

    public boolean useCube(int cubeId, MapleCharacter player, int lockslot) {
        return this.useCube((short)0, 0, cubeId, player, lockslot);
    }

    public boolean useCube(short opcode, int action, int cubeId, MapleCharacter player) {
        return this.useCube(opcode, action, cubeId, player, 0);
    }

    public boolean useCube(short opcode, int action, int cubeId, MapleCharacter player, int lockslot) {
        if (player.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() >= 1) {
            int cubeTpye = ItemConstants.方塊.getDefaultPotentialFlag((int)cubeId);
            boolean isBonus = ItemConstants.方塊.CubeType.附加潛能.check(cubeTpye);
            if (!ItemConstants.方塊.canUseCube((Equip)this, (int)cubeId)) {
                player.dropMessage(5, "你無法對這個物品使用這個方塊。");
                return false;
            }
            switch (cubeId) {
                case 3994895: 
                case 3996222: 
                case 5062017: 
                case 5062019: 
                case 5062020: 
                case 5062021: 
                case 5062024: 
                case 5062026: 
                case 5062030: 
                case 5062032: {
                    break;
                }
                default: {
                    long meso = ItemConstants.方塊.getCubeNeedMeso((Equip)this);
                    if (player.getMeso() < meso) {
                        player.dropMessage(5, "您沒有足夠的楓幣。");
                        player.sendEnableActions();
                        return false;
                    }
                    player.gainMeso(-meso, false);
                }
            }
            if (this.getState(isBonus) >= 17 && this.getState(isBonus) <= 20) {
                byte oldState = 0;
                int rateIndex = oldState - ((oldState = this.getState(isBonus)) < 17 ? 1 : 17);
                int stateRate = rateIndex >= 0 && rateIndex <= 2 ? ServerConfig.CHANNEL_RATE_POTENTIALLEVEL * ItemConstants.方塊.getCubeRankUpRate((int)cubeId)[rateIndex] : 0;
                if (EnhanceResultType.UPGRADE_TIER.check(this.getEnchantBuff())) {
                    stateRate = 1000000;
                    if (player.isAdmin()) {
                        player.dropMessage(-6, "裝備自帶100%潛能等級提升成功率");
                    }
                } else if (player.isAdmin() && player.isInvincible() && this.getState(isBonus) < 20 && stateRate < 1000000) {
                    stateRate = 1000000;
                }
                boolean isMemorial = false;
                switch (cubeId) {
                    case 3994895: 
                    case 5062010: 
                    case 5062017: 
                    case 5062019: 
                    case 5062020: 
                    case 5062021: 
                    case 5062024: 
                    case 5062026: 
                    case 5062030: 
                    case 5062032: 
                    case 5062090: 
                    case 5062500: 
                    case 5062503: {
                        isMemorial = true;
                    }
                }
                int debris = ItemConstants.方塊.getCubeDebris((int)cubeId);
                if (debris > 0 && !MapleInventoryManipulator.addById(player.getClient(), debris, 1, "Cube on " + DateUtil.getCurrentDate())) {
                    return false;
                }
                if (isMemorial) {
                    int lines;
                    Object pots = "";
                    if (cubeId == 5062020 || cubeId == 0x4D3DD4) {
                        int i;
                        byte newState = this.getState(isBonus);
                        ArrayList<Integer> newPots = new ArrayList<Integer>();
                        for (i = 0; i < 2; ++i) {
                            this.renewPotential(stateRate, cubeTpye, lockslot);
                            this.magnify();
                            newPots.add(this.getPotential(1, isBonus));
                            newPots.add(this.getPotential(2, isBonus));
                            if (this.getPotential(3, isBonus) > 0) {
                                newPots.add(this.getPotential(3, isBonus));
                            }
                            if (i != 0) continue;
                            byte state = this.getState(isBonus);
                            this.setState(newState, isBonus);
                            newState = state;
                        }
                        lines = newPots.size() / 2;
                        this.setState(newState, isBonus);
                        for (i = 0; i < newPots.size(); ++i) {
                            pots = (String)pots + String.valueOf(newPots.get(i));
                            if (i >= newPots.size() - 1) continue;
                            pots = (String)pots + ",";
                        }
                        if (cubeId == 5062024) {
                            player.send(InventoryPacket.showHyunPotentialResult(newPots));
                        } else {
                            player.write(OverseasPacket.getHexaCubeRes((short)opcode, (int)action, newPots));
                        }
                    } else {
                        lines = this.getPotential(2, isBonus) != 0 ? 3 : 2;
                        for (int i = 0; i < lines; ++i) {
                            pots = (String)pots + this.getPotential(i + 1, isBonus);
                            if (i >= lines - 1) continue;
                            pots = (String)pots + ",";
                        }
                        this.renewPotential(stateRate, cubeTpye, lockslot);
                        this.magnify();
                        if (cubeId == 5062017 || cubeId == 5062030) {
                            player.write(OverseasPacket.getAnimusCubeRes((short)opcode, (int)action, (int)cubeId, (Item)this));
                        } else {
                            player.send(InventoryPacket.showCubeResetResult(this.getPosition(), this, cubeId, player.getInventory(MapleInventoryType.CASH).findById(cubeId).getPosition()));
                        }
                    }
                    player.updateOneInfo(52998, "c", String.valueOf(lines), false);
                    player.updateOneInfo(52998, "i", String.valueOf(this.getItemId()), false);
                    player.updateOneInfo(52998, "o", (String)pots, false);
                    player.updateOneInfo(52998, "p", String.valueOf(this.getPosition()), false);
                    player.updateOneInfo(52998, "u", String.valueOf(cubeId), false);
                    player.updateOneInfo(52998, "s", String.valueOf(oldState), false);
                } else if (cubeId == 5062026) {
                    StructItemOption pot;
                    MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    int reqLevel = ii.getReqLevel(this.getItemId()) / 10;
                    LinkedList<List<StructItemOption>> pots = new LinkedList<List<StructItemOption>>(ii.getAllPotentialInfo().values());
                    do {
                        if (reqLevel < 20) continue;
                        reqLevel = 19;
                    } while ((pot = (StructItemOption)((List)pots.get(Randomizer.nextInt(pots.size()))).get(reqLevel)) == null || pot.reqLevel / 10 > reqLevel || !ItemConstants.方塊.optionTypeFits((int)pot.optionType, (int)this.getItemId()) || !ItemConstants.方塊.potentialIDFits((int)pot.opID, (int)this.getState(isBonus), (int)lockslot) || !ItemConstants.方塊.isAllowedPotentialStat((Equip)this, (int)pot.opID, (boolean)isBonus, (boolean)ItemConstants.方塊.CubeType.點商光環.check(cubeTpye)) || ItemConstants.方塊.CubeType.去掉無用潛能.check(cubeTpye) && (!ItemConstants.方塊.CubeType.去掉無用潛能.check(cubeTpye) || ItemConstants.方塊.isUselessPotential((StructItemOption)pot)));
                    this.setPotential(pot.opID, lockslot, isBonus);
                } else {
                    this.renewPotential(stateRate, cubeTpye, lockslot);
                    this.magnify();
                    if (cubeId == 5062019 || cubeId == 5062021) {
                        player.write(OverseasPacket.getTmsCubeRes((short)opcode, (int)action, (int)0));
                    } else if (cubeId != 3994895) {
                        player.send(InventoryPacket.showCubeResult(player.getId(), oldState < this.getState(isBonus), cubeId, this.getPosition(), Math.max(0, player.getItemQuantity(cubeId) - 1), this.copy()));
                        player.forceUpdateItem(this);
                        player.getMap().broadcastMessage(InventoryPacket.showPotentialReset(player.getId(), oldState < this.getState(isBonus), cubeId, debris, this.getItemId()));
                    }
                }
                if (oldState < this.getState(isBonus) && this.getState(isBonus) == 20) {
                    MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    StringBuilder msg = new StringBuilder();
                    msg.append(player.getName()).append("使用").append(ii.getName(cubeId));
                    String eqName = "{" + ii.getName(this.getItemId()) + "}";
                    msg.append("將").append(eqName).append("的");
                    if (isBonus) {
                        msg.append("附加潛能");
                    }
                    msg.append("等級提升為傳說.");
                    if (player.isIntern()) {
                        msg.append("（管理員此訊息僅自己可見）");
                        player.send(MaplePacketCreator.gachaponMsg(msg.toString(), this));
                    } else {
                        WorldBroadcastService.getInstance().broadcastMessage(MaplePacketCreator.gachaponMsg(msg.toString(), this));
                    }
                    player.send(EffectPacket.showIncubatorEffect(-1, 0, "Effect/BasicEff/Event1/Best"));
                    player.getMap().broadcastMessage(player, EffectPacket.showIncubatorEffect(player.getId(), 0, "Effect/BasicEff/Event1/Best"), false);
                }
                return true;
            }
            player.dropMessage(5, "請確認您要重置的道具具有潛能屬性。");
        }
        return false;
    }

    public boolean magnify() {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int reqLevel = ii.getReqLevel(this.getItemId()) / 10;
        LinkedList<List<StructItemOption>> pots = new LinkedList<List<StructItemOption>>(ii.getAllPotentialInfo().values());
        boolean isBonus = this.getState(false) >= 17 || this.getState(false) <= 0;
        int lockedLine = 0;
        int locked = Math.abs(this.getPotential(1, isBonus)) % 1000000;
        if (locked >= 100000) {
            lockedLine = locked / 100000;
            locked %= 100000;
        } else {
            locked = 0;
        }
        int lines = this.getPotential(2, isBonus) != 0 ? 3 : 2;
        int new_state = this.getState(isBonus) + 16;
        if (new_state > 20) {
            new_state = 20;
        } else if (new_state < 17) {
            new_state = 17;
        }
        int cubeType = Math.abs(this.getPotential(3, isBonus));
        this.setPotential(0, 3, isBonus);
        boolean twins = ItemConstants.方塊.CubeType.前兩條相同.check(cubeType);
        for (int i = 1; i <= lines; ++i) {
            StructItemOption pot;
            if (i == lockedLine) {
                this.setPotential(locked, lockedLine, isBonus);
                continue;
            }
            do {
                if (reqLevel < 20) continue;
                reqLevel = 19;
            } while ((pot = (StructItemOption)((List)pots.get(Randomizer.nextInt(pots.size()))).get(reqLevel)) == null || pot.reqLevel / 10 > reqLevel || !ItemConstants.方塊.optionTypeFits((int)pot.optionType, (int)this.getItemId()) || !ItemConstants.方塊.potentialIDFits((int)pot.opID, (int)new_state, (int)(ItemConstants.方塊.CubeType.對等.check(cubeType) ? 1 : i)) || !ItemConstants.方塊.isAllowedPotentialStat((Equip)this, (int)pot.opID, (boolean)isBonus, (boolean)ItemConstants.方塊.CubeType.點商光環.check(cubeType)) || ItemConstants.方塊.CubeType.去掉無用潛能.check(cubeType) && (!ItemConstants.方塊.CubeType.去掉無用潛能.check(cubeType) || ItemConstants.方塊.isUselessPotential((StructItemOption)pot)));
            if (i == 1 && twins) {
                this.setPotential(pot.opID, 2, isBonus);
            }
            if (i == 2 && twins) continue;
            this.setPotential(pot.opID, i, isBonus);
        }
        this.initState(isBonus);
        return true;
    }

    public List<StructItemOption> getFitOptionList(int size) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int level = ii.getReqLevel(this.getItemId()) / 10;
        level = level >= 20 ? 19 : level;
        LinkedList<List<StructItemOption>> linkedList = new LinkedList<List<StructItemOption>>(ii.getAllPotentialInfo().values());
        int state = this.getState(false) + 16;
        if (state > 20) {
            state = 20;
        } else if (state < 17) {
            state = 17;
        }
        int n5 = Math.abs(this.getPotential(3, false));
        ArrayList<StructItemOption> arrayList = new ArrayList<StructItemOption>(6);
        for (int i2 = 1; i2 <= size; ++i2) {
            boolean bl2 = false;
            while (!bl2) {
                StructItemOption itemOption = (StructItemOption)((List)linkedList.get(Randomizer.nextInt(linkedList.size()))).get(level);
                if (itemOption == null || GameConstants.isAboveA(itemOption.opID) || !GameConstants.optionTypeFits(itemOption.optionType, this.getItemId()) || !GameConstants.isBlockedPotential(this, itemOption.opID, false, ItemConstants.方塊.CubeType.點商光環.check(n5)) || !GameConstants.potentialIDFits(itemOption.opID, state, ItemConstants.方塊.CubeType.對等.check(n5) ? 1 : i2) || ItemConstants.方塊.CubeType.去掉無用潛能.check(n5) && (!ItemConstants.方塊.CubeType.去掉無用潛能.check(n5) || ItemConstants.方塊.isUselessPotential((StructItemOption)itemOption))) continue;
                arrayList.add(itemOption);
                bl2 = true;
            }
        }
        return arrayList;
    }

    public void renewPotential(int defaultRate, int flag, int lockSlot) {
        int rank;
        boolean threeLine;
        boolean bonus;
        block13: {
            block12: {
                int miracleRate = 1;
                bonus = ItemConstants.方塊.CubeType.附加潛能.check(flag);
                threeLine = this.getPotential(3, bonus) > 0;
                int n = rank = Randomizer.nextInt(1000000) < defaultRate * miracleRate ? 1 : 0;
                if (ItemConstants.方塊.CubeType.等級下降.check(flag) && rank == 0) {
                    int n2 = rank = Randomizer.nextInt(1000000) < (defaultRate + 200000) * miracleRate ? -1 : 0;
                }
                if (ItemConstants.方塊.CubeType.前兩條相同.check(flag)) {
                    flag -= Randomizer.nextInt(10) <= 5 ? ItemConstants.方塊.CubeType.前兩條相同.getValue() : 0;
                }
                if (this.getState(bonus) + rank < 17) break block12;
                if (this.getState(bonus) + rank <= (!ItemConstants.方塊.CubeType.傳說.check(flag) ? (!ItemConstants.方塊.CubeType.罕見.check(flag) ? (!ItemConstants.方塊.CubeType.稀有.check(flag) ? 17 : 18) : 19) : 20)) break block13;
            }
            rank = 0;
        }
        this.setState((byte)(this.getState(bonus) + rank - 16), bonus);
        if (lockSlot != 0 && lockSlot <= 3) {
            this.setPotential(-(lockSlot * 100000 + this.getPotential(lockSlot, bonus)), 1, bonus);
        } else {
            this.setPotential(-this.getState(bonus), 1, bonus);
        }
        if (ItemConstants.方塊.CubeType.調整潛能條數.check(flag)) {
            this.setPotential(Randomizer.nextInt(10) <= 2 ? -this.getState(bonus) : (byte)0, 2, bonus);
        } else if (threeLine) {
            this.setPotential(-this.getState(bonus), 2, bonus);
        } else {
            this.setPotential(0, 2, bonus);
        }
        this.setPotential(-flag, 3, bonus);
        if (ItemConstants.方塊.CubeType.洗後無法交易.check(flag)) {
            this.addAttribute(ItemAttribute.TradeBlock.getValue());
        }
        this.initState(bonus);
    }

    public void setNewArcInfo(int job) {
        this.ARCLevel = 1;
        this.ARCExp = 1;
        this.recalcArcStat(job);
    }

    public void recalcArcStat(int job) {
        int n = this.ARCLevel + 2;
        this.ARC = (short)(10 * n);
        if (JobConstants.is惡魔復仇者(job)) {
            this.hp = (short)(2100 * n);
        } else if (JobConstants.is傑諾(job)) {
            this.str = (short)(48 * n);
            this.dex = (short)(48 * n);
            this.luk = (short)(48 * n);
        } else {
            switch (JobConstants.getJobBranch(job)) {
                case 1: {
                    this.str = (short)(100 * n);
                    break;
                }
                case 2: {
                    this._int = (short)(100 * n);
                    break;
                }
                case 3: {
                    this.dex = (short)(100 * n);
                    break;
                }
                case 4: {
                    this.luk = (short)(100 * n);
                    break;
                }
                case 5: {
                    if (JobConstants.is拳霸(job) || JobConstants.is隱月(job) || JobConstants.is重砲指揮官(job) || JobConstants.is閃雷悍將(job) || JobConstants.is亞克(job)) {
                        this.str = (short)(100 * n);
                        break;
                    }
                    this.dex = (short)(100 * n);
                }
            }
        }
    }

    public void setNewAutInfo(int job) {
        this.autLevel = 1;
        this.autExp = 1;
        this.recalcAutStat(job);
    }

    public void recalcAutStat(int job) {
        short n = this.autLevel;
        this.aut = (short)(10 * n);
        if (JobConstants.is惡魔復仇者(job)) {
            this.hp = (short)(6300 + 4200 * n);
        } else if (JobConstants.is傑諾(job)) {
            this.str = (short)(144 + 96 * n);
            this.dex = (short)(144 + 96 * n);
            this.luk = (short)(144 + 96 * n);
        } else {
            switch (JobConstants.getJobBranch(job)) {
                case 1: {
                    this.str = (short)(300 + 200 * n);
                    break;
                }
                case 2: {
                    this._int = (short)(300 + 200 * n);
                    break;
                }
                case 3: {
                    this.dex = (short)(300 + 200 * n);
                    break;
                }
                case 4: {
                    this.luk = (short)(300 + 200 * n);
                    break;
                }
                case 5: {
                    if (JobConstants.is拳霸(job) || JobConstants.is隱月(job) || JobConstants.is重砲指揮官(job) || JobConstants.is閃雷悍將(job) || JobConstants.is亞克(job)) {
                        this.str = (short)(300 + 200 * n);
                        break;
                    }
                    this.dex = (short)(300 + 200 * n);
                }
            }
        }
    }

    public int getIncSkill() {
        return this.incSkill;
    }

    public void setIncSkill(int inc) {
        this.incSkill = inc;
    }

    public short getCharmEXP() {
        return this.charmExp;
    }

    public void setCharmEXP(short s) {
        this.charmExp = s;
    }

    public short getPVPDamage() {
        return this.pvpDamage;
    }

    public void setPVPDamage(short p) {
        this.pvpDamage = p;
    }

    public MapleRing getRing() {
        if (!ItemConstants.類型.特效裝備(this.getItemId()) || this.getSN() <= 0) {
            return null;
        }
        if (this.ring == null) {
            this.ring = MapleRing.loadFromDb((int)this.getSN(), (this.getPosition() < 0 ? 1 : 0) != 0);
        }
        return this.ring;
    }

    public void setRing(MapleRing ring) {
        this.ring = ring;
    }

    public MapleAndroid getAndroid() {
        if (this.getItemId() / 10000 != 166 || this.getSN() <= 0) {
            return null;
        }
        if (this.android == null) {
            this.android = MapleAndroid.loadFromDb(this.getItemId(), this.getSN());
        }
        return this.android;
    }

    public void setAndroid(MapleAndroid android) {
        this.android = android;
        if (android != null && this.getSN() != android.getUniqueId() && android.getUniqueId() > 0) {
            this.setSN(android.getUniqueId());
        }
    }

    public short getSocketState() {
        boolean isSocketItem = false;
        short flag = 0;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        boolean bl = ServerConfig.ALL_SOCKET ? !ii.isCash(this.getItemId()) : (isSocketItem = ii.isActivatedSocketItem(this.getItemId()) || this.socket1 >= 0);
        if (isSocketItem) {
            flag = (short)(flag | SocketFlag.可以鑲嵌.getValue());
            if (this.socket1 == -1) {
                this.setSocket1(0);
            }
            if (this.socket1 != -1) {
                flag = (short)(flag | SocketFlag.已打孔01.getValue());
            }
            if (this.socket2 != -1) {
                flag = (short)(flag | SocketFlag.已打孔02.getValue());
            }
            if (this.socket3 != -1) {
                flag = (short)(flag | SocketFlag.已打孔03.getValue());
            }
            if (this.socket1 > 0) {
                flag = (short)(flag | SocketFlag.已鑲嵌01.getValue());
            }
            if (this.socket2 > 0) {
                flag = (short)(flag | SocketFlag.已鑲嵌02.getValue());
            }
            if (this.socket3 > 0) {
                flag = (short)(flag | SocketFlag.已鑲嵌03.getValue());
            }
        }
        return flag;
    }

    public int getSocket1() {
        return this.socket1;
    }

    public void setSocket1(int socket) {
        this.socket1 = socket;
    }

    public int getSocket2() {
        return this.socket2;
    }

    public void setSocket2(int socket) {
        this.socket2 = socket;
    }

    public int getSocket3() {
        return this.socket3;
    }

    public void setSocket3(int socket) {
        this.socket3 = socket;
    }

    public int getItemSkin() {
        return this.itemSkin;
    }

    public void setItemSkin(int id) {
        this.itemSkin = id;
    }

    public short getEnchantBuff() {
        return this.enchantBuff;
    }

    public void setEnchantBuff(short enchantBuff) {
        if (enchantBuff < 0) {
            enchantBuff = 0;
        }
        this.enchantBuff = enchantBuff;
    }

    public short getReqLevel() {
        if (this.reqLevel == -1) {
            this.reqLevel = (short)MapleItemInformationProvider.getInstance().getReqLevel(this.getItemId());
        }
        return this.reqLevel;
    }

    public short getTotalReqLevel() {
        return (short)Math.max(1, this.getReqLevel() - this.nirvanaFlame.getReqLevel());
    }

    public void setReqLevel(short reqLevel) {
        if (reqLevel < 0) {
            reqLevel = 0;
        }
        this.reqLevel = reqLevel;
    }

    public byte getDownLevel() {
        return (byte)(MapleItemInformationProvider.getInstance().getReqLevel(this.getItemId()) - this.getReqLevel());
    }

    public byte getTotalDownLevel() {
        return (byte)(MapleItemInformationProvider.getInstance().getReqLevel(this.getItemId()) - this.getTotalReqLevel());
    }

    public short getYggdrasilWisdom() {
        return this.yggdrasilWisdom;
    }

    public void setYggdrasilWisdom(short yggdrasilWisdom) {
        if (yggdrasilWisdom < 0) {
            yggdrasilWisdom = 0;
        }
        this.yggdrasilWisdom = yggdrasilWisdom;
    }

    public boolean getFinalStrike() {
        return this.finalStrike;
    }

    public void setFinalStrike(boolean finalStrike) {
        this.finalStrike = finalStrike;
    }

    public short getTotalBossDamage() {
        return (short)(this.bossDamage + this.starForce.getBossDamage() + this.nirvanaFlame.getBossDamage());
    }

    public short getBossDamage() {
        return this.bossDamage;
    }

    public void setBossDamage(short bossDamage) {
        if (bossDamage < 0) {
            bossDamage = 0;
        }
        this.bossDamage = bossDamage;
    }

    public short getTotalIgnorePDR() {
        return (short)(this.ignorePDR + this.starForce.getIgnorePDR() + this.nirvanaFlame.getIgnorePDR());
    }

    public short getIgnorePDR() {
        return this.ignorePDR;
    }

    public void setIgnorePDR(short ignorePDR) {
        if (ignorePDR < 0) {
            ignorePDR = 0;
        }
        this.ignorePDR = ignorePDR;
    }

    public short getTotalTotalDamage() {
        return (short)(this.totalDamage + this.starForce.getTotalDamage() + this.nirvanaFlame.getTotalDamage());
    }

    public short getTotalDamage() {
        return this.totalDamage;
    }

    public void setTotalDamage(short totalDamage) {
        if (totalDamage < 0) {
            totalDamage = 0;
        }
        this.totalDamage = totalDamage;
    }

    public short getTotalAllStat() {
        return (short)(this.allStat + this.starForce.getAllStat() + this.nirvanaFlame.getAllStat());
    }

    public short getAllStat() {
        return this.allStat;
    }

    public void setAllStat(short allStat) {
        if (allStat < 0) {
            allStat = 0;
        }
        this.allStat = allStat;
    }

    public short getKarmaCount() {
        return this.karmaCount;
    }

    public void setKarmaCount(short karmaCount) {
        this.karmaCount = karmaCount;
    }

    public Map<EquipStats, Long> getStatsTest() {
        return this.statsTest;
    }

    public int getEquipFlag() {
        int flag = 0;
        if (this.getRestUpgradeCount() > 0) {
            flag |= EquipStats.可使用捲軸次數.getValue();
        }
        if (this.getCurrentUpgradeCount() > 0) {
            flag |= EquipStats.捲軸強化次數.getValue();
        }
        if (this.getTotalStr() > 0) {
            flag |= EquipStats.力量.getValue();
        }
        if (this.getTotalDex() > 0) {
            flag |= EquipStats.敏捷.getValue();
        }
        if (this.getTotalInt() > 0) {
            flag |= EquipStats.智力.getValue();
        }
        if (this.getTotalLuk() > 0) {
            flag |= EquipStats.幸運.getValue();
        }
        if (this.getTotalHp() > 0) {
            flag |= EquipStats.MaxHP.getValue();
        }
        if (this.getTotalMp() > 0) {
            flag |= EquipStats.MaxMP.getValue();
        }
        if (this.getTotalPad() > 0) {
            flag |= EquipStats.攻擊力.getValue();
        }
        if (this.getTotalMad() > 0) {
            flag |= EquipStats.魔力.getValue();
        }
        if (this.getTotalPdd() > 0) {
            flag |= EquipStats.防禦力.getValue();
        }
        if (this.getTotalHands() > 0) {
            flag |= EquipStats.靈敏度.getValue();
        }
        if (this.getTotalSpeed() > 0) {
            flag |= EquipStats.移動速度.getValue();
        }
        if (this.getTotalJump() > 0) {
            flag |= EquipStats.跳躍力.getValue();
        }
        if (this.getCAttribute() > 0) {
            flag |= EquipStats.狀態.getValue();
        }
        if (this.getIncSkill() > 0) {
            flag |= EquipStats.裝備技能.getValue();
        }
        if (this.isSealedEquip()) {
            if (this.getSealedLevel() > 0) {
                flag |= EquipStats.裝備等級.getValue();
            }
            if (this.getSealedExp() > 0L) {
                flag |= EquipStats.裝備經驗.getValue();
            }
        } else {
            if (this.getEquipLevel() > 0) {
                flag |= EquipStats.裝備等級.getValue();
            }
            if (this.getExpPercentage() > 0L) {
                flag |= EquipStats.裝備經驗.getValue();
            }
        }
        if (this.getDurability() > 0) {
            flag |= EquipStats.耐久度.getValue();
        }
        if (this.getTotalHammer() > 0) {
            flag |= EquipStats.鎚子.getValue();
        }
        if (this.getPVPDamage() > 0) {
            flag |= EquipStats.大亂鬥傷害.getValue();
        }
        if (this.getDownLevel() > 0) {
            flag |= EquipStats.套用等級減少.getValue();
        }
        if (this.getEnchantBuff() > 0) {
            flag |= EquipStats.ENHANCT_BUFF.getValue();
        }
        if (this.getiIncReq() > 0) {
            flag |= EquipStats.REQUIRED_LEVEL.getValue();
        }
        if (this.getYggdrasilWisdom() > 0) {
            flag |= EquipStats.YGGDRASIL_WISDOM.getValue();
        }
        if (this.getFinalStrike()) {
            flag |= EquipStats.FINAL_STRIKE.getValue();
        }
        if (this.getTotalBossDamage() > 0) {
            flag |= EquipStats.BOSS傷.getValue();
        }
        if (this.getTotalIgnorePDR() > 0) {
            flag |= EquipStats.無視防禦.getValue();
        }
        return flag;
    }

    public int getEquipBaseFlag() {
        int flag = 0;
        if (this.getStr() > 0) {
            flag |= EquipBaseStat.力量.getFlag();
        }
        if (this.getDex() > 0) {
            flag |= EquipBaseStat.敏捷.getFlag();
        }
        if (this.getInt() > 0) {
            flag |= EquipBaseStat.智力.getFlag();
        }
        if (this.getLuk() > 0) {
            flag |= EquipBaseStat.幸運.getFlag();
        }
        if (this.getHp() > 0) {
            flag |= EquipBaseStat.MaxHP.getFlag();
        }
        if (this.getMp() > 0) {
            flag |= EquipBaseStat.MaxMP.getFlag();
        }
        if (this.getPad() > 0) {
            flag |= EquipBaseStat.攻擊力.getFlag();
        }
        if (this.getMad() > 0) {
            flag |= EquipBaseStat.魔力.getFlag();
        }
        if (this.getPdd() > 0) {
            flag |= EquipBaseStat.防禦力.getFlag();
        }
        if (this.getHands() > 0) {
            flag |= EquipBaseStat.靈敏度.getFlag();
        }
        if (this.getSpeed() > 0) {
            flag |= EquipBaseStat.移動速度.getFlag();
        }
        if (this.getJump() > 0) {
            flag |= EquipBaseStat.跳躍力.getFlag();
        }
        return flag;
    }

    public int getEquipSFBaseFlag() {
        int flag = 0;
        if (this.getSF_Str() > 0) {
            flag |= EquipBaseStat.力量.getFlag();
        }
        if (this.getSF_Dex() > 0) {
            flag |= EquipBaseStat.敏捷.getFlag();
        }
        if (this.getSF_Int() > 0) {
            flag |= EquipBaseStat.智力.getFlag();
        }
        if (this.getSF_Luk() > 0) {
            flag |= EquipBaseStat.幸運.getFlag();
        }
        if (this.getSF_Hp() > 0) {
            flag |= EquipBaseStat.MaxHP.getFlag();
        }
        if (this.getSF_Mp() > 0) {
            flag |= EquipBaseStat.MaxMP.getFlag();
        }
        if (this.getSF_Pad() > 0) {
            flag |= EquipBaseStat.攻擊力.getFlag();
        }
        if (this.getSF_Mad() > 0) {
            flag |= EquipBaseStat.魔力.getFlag();
        }
        if (this.getSF_Pdd() > 0) {
            flag |= EquipBaseStat.防禦力.getFlag();
        }
        if (this.getSF_Hands() > 0) {
            flag |= EquipBaseStat.靈敏度.getFlag();
        }
        if (this.getSF_Speed() > 0) {
            flag |= EquipBaseStat.移動速度.getFlag();
        }
        if (this.getSF_Jump() > 0) {
            flag |= EquipBaseStat.跳躍力.getFlag();
        }
        return flag;
    }

    public int getEquipSpecialFlag() {
        int flag = 0;
        if (this.getRestUpgradeCount() > 0) {
            flag |= EquipSpecialStat.可使用捲軸次數.getFlag();
        }
        if (this.getCurrentUpgradeCount() > 0) {
            flag |= EquipSpecialStat.捲軸強化次數.getFlag();
        }
        if (this.getCAttribute() > 0) {
            flag |= EquipSpecialStat.狀態.getFlag();
        }
        if (this.getIncSkill() > 0) {
            flag |= EquipSpecialStat.裝備技能.getFlag();
        }
        if (this.isSealedEquip()) {
            if (this.getSealedLevel() > 0) {
                flag |= EquipSpecialStat.裝備等級.getFlag();
            }
            if (this.getSealedExp() > 0L) {
                flag |= EquipSpecialStat.裝備經驗.getFlag();
            }
        } else {
            if (this.getEquipLevel() > 0) {
                flag |= EquipSpecialStat.裝備等級.getFlag();
            }
            if (this.getExpPercentage() > 0L) {
                flag |= EquipSpecialStat.裝備經驗.getFlag();
            }
        }
        if (this.getDurability() > 0) {
            flag |= EquipSpecialStat.耐久度.getFlag();
        }
        if (this.getTotalHammer() > 0) {
            flag |= EquipSpecialStat.鎚子.getFlag();
        }
        if (this.getDownLevel() > 0) {
            flag |= EquipSpecialStat.套用等級減少.getFlag();
        }
        if (this.getEnchantBuff() > 0) {
            flag |= EquipSpecialStat.ENHANCT_BUFF.getFlag();
        }
        if (this.getiIncReq() > 0) {
            flag |= EquipSpecialStat.REQUIRED_LEVEL.getFlag();
        }
        if (this.getYggdrasilWisdom() > 0) {
            flag |= EquipSpecialStat.YGGDRASIL_WISDOM.getFlag();
        }
        if (this.getFinalStrike()) {
            flag |= EquipSpecialStat.FINAL_STRIKE.getFlag();
        }
        if (this.getTotalBossDamage() > 0) {
            flag |= EquipSpecialStat.BOSS傷.getFlag();
        }
        if (this.getTotalIgnorePDR() > 0) {
            flag |= EquipSpecialStat.無視防禦.getFlag();
        }
        if (this.getTotalTotalDamage() > 0) {
            flag |= EquipSpecialStat.總傷害.getFlag();
        }
        if (this.getTotalAllStat() > 0) {
            flag |= EquipSpecialStat.全屬性.getFlag();
        }
        flag |= EquipSpecialStat.剪刀次數.getFlag();
        if (this.getFlameFlag() != 0L) {
            flag |= EquipSpecialStat.輪迴星火.getFlag();
        }
        return flag |= EquipSpecialStat.星力強化.getFlag();
    }

    public void setLockPotential(int slot, short id) {
        this.lockSlot = slot;
        this.lockId = id;
    }

    public int getLockSlot() {
        return this.lockSlot;
    }

    public int getLockId() {
        return this.lockId;
    }

    public boolean isSealedEquip() {
        return GameConstants.isSealedEquip(this.getItemId());
    }

    public byte getSealedLevel() {
        return this.sealedLevel;
    }

    public void setSealedLevel(byte level) {
        this.sealedLevel = level;
    }

    public void gainSealedExp(long gain) {
        this.sealedExp += gain;
    }

    public long getSealedExp() {
        return this.sealedExp;
    }

    public void setSealedExp(long exp) {
        this.sealedExp = exp;
    }

    public short getSoulOptionID() {
        return this.soulOptionID;
    }

    public void setSoulOptionID(short soulname) {
        this.soulOptionID = soulname;
    }

    public short getSoulSocketID() {
        return this.soulSocketID;
    }

    public void setSoulSocketID(short soulenchanter) {
        this.soulSocketID = soulenchanter;
    }

    public short getSoulOption() {
        return this.soulOption;
    }

    public void setSoulOption(short soulpotential) {
        this.soulOption = soulpotential;
    }

    public int getSoulSkill() {
        return this.soulSkill;
    }

    public void setSoulSkill(int skillid) {
        this.soulSkill = skillid;
    }

    public int getiIncReq() {
        return this.iIncReq;
    }

    public NirvanaFlame getNirvanaFlame() {
        return this.nirvanaFlame;
    }

    public void setNirvanaFlame(NirvanaFlame nirvanaFlame) {
        this.nirvanaFlame = nirvanaFlame;
        nirvanaFlame.resetEquipExStats(this);
    }

    public long getFlameFlag() {
        return this.nirvanaFlame.getFlag();
    }

    public void setFlameFlag(long flag) {
        this.nirvanaFlame.setFlag(flag);
        this.nirvanaFlame.resetEquipExStats(this);
    }

    public StarForce getStarForce() {
        return this.starForce;
    }

    public void setStarForce(StarForce starForce) {
        this.starForce = starForce;
        starForce.resetEquipStats(this);
    }

    public byte getStarForceLevel() {
        return this.starForce.getLevel();
    }

    public void setStarForceLevel(byte level) {
        if (this.starForce.getLevel() <= level) {
            this.setFailCount(0);
        }
        this.starForce.setLevel(level);
        this.starForce.resetEquipStats(this);
    }

    public byte getEnhance() {
        return this.getStarForceLevel();
    }

    public void setEnhance(byte level) {
        this.setStarForceLevel(level);
    }

    public int getFailCount() {
        return this.failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public short getARC() {
        return this.ARC;
    }

    public void setARC(short ARC) {
        this.ARC = ARC;
    }

    public int getArcExp() {
        return this.ARCExp;
    }

    public void setARCExp(int ARCExp) {
        this.ARCExp = ARCExp;
    }

    public short getARCLevel() {
        return this.ARCLevel;
    }

    public void setARCLevel(short ARCLevel) {
        this.ARCLevel = ARCLevel;
    }

    public int getReqJob() {
        return MapleItemInformationProvider.getInstance().getReqJob(this.getItemId());
    }

    public int getReqSpecJob() {
        return MapleItemInformationProvider.getInstance().getReqSpecJob(this.getItemId());
    }

    public void transmit(int itemID) {
        this.transmit(itemID, 0);
    }

    public void transmit(int itemID, int jobId) {
        this.transmit(itemID, jobId, true);
    }

    public void transmit(int itemID, int jobId, boolean resetStat) {
        if (!ItemConstants.類型.裝備(itemID)) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Equip normalEquip = ii.getEquipById(this.getItemId());
        if (normalEquip == null) {
            return;
        }
        short addStr = (short)(this.str - normalEquip.str);
        short addDex = (short)(this.dex - normalEquip.dex);
        short addInt = (short)(this._int - normalEquip._int);
        short addLuk = (short)(this.luk - normalEquip.luk);
        short addHp = (short)(this.hp - normalEquip.hp);
        short addMp = (short)(this.mp - normalEquip.mp);
        short addMad = (short)(this.mad - normalEquip.mad);
        short addMdd = (short)(this.mdd - normalEquip.mdd);
        short addPad = (short)(this.pad - normalEquip.pad);
        short addPdd = (short)(this.pdd - normalEquip.pdd);
        short addAcc = (short)(this.acc - normalEquip.acc);
        short addAvoid = (short)(this.avoid - normalEquip.avoid);
        short addHands = (short)(this.hands - normalEquip.hands);
        short addSpeed = (short)(this.speed - normalEquip.speed);
        short addJump = (short)(this.jump - normalEquip.jump);
        normalEquip = ii.getEquipById(itemID);
        if (normalEquip == null) {
            return;
        }
        int srcReqJob = this.getReqJob();
        this.setItemId(itemID);
        int dstReqJob = normalEquip.getReqJob();
        if (srcReqJob != dstReqJob && srcReqJob > 0 && dstReqJob > 0) {
            short tempValue;
            boolean 劍士2 = true;
            int 法師2 = 2;
            int 弓箭手2 = 4;
            int 盜賊2 = 8;
            int 海盜2 = 16;
            if ((srcReqJob & 2) != (dstReqJob & 2)) {
                tempValue = addPad;
                addPad = addMad;
                addMad = tempValue;
                this.nirvanaFlame.transmitStat(NirvanaFlame.EquipExFlag.FLAGEx_iPAD, NirvanaFlame.EquipExFlag.FLAGEx_iMAD);
                for (int i = 1; i <= 6; ++i) {
                    int pot = this.getPotential(i > 3 ? i - 3 : i, i > 3);
                    if (pot >= 60000 || pot <= 0) continue;
                    if (pot == 32051 || pot == 32052 || pot == 42051 || pot == 42052) {
                        this.setPotential(pot + 2, i > 3 ? i - 3 : i, i > 3);
                        continue;
                    }
                    if (pot == 32053 || pot == 32054 || pot == 42053 || pot == 42054) {
                        this.setPotential(pot - 2, i > 3 ? i - 3 : i, i > 3);
                        continue;
                    }
                    Map<String, Integer> data = ii.getPotentialInfo((int)pot).get((int)0).data;
                    if (data.containsKey("incPAD") || data.containsKey("incPADr") || data.containsKey("incPADlv")) {
                        this.setPotential(pot + 1, i > 3 ? i - 3 : i, i > 3);
                        continue;
                    }
                    if (!data.containsKey("incMAD") && !data.containsKey("incMADr") && !data.containsKey("incMADlv")) continue;
                    this.setPotential(pot - 1, i > 3 ? i - 3 : i, i > 3);
                }
            }
            Pair<Object, Object> statName = new Pair<Object, Object>(null, null);
            if ((srcReqJob & 1) != 0) {
                statName.left = new String[]{"STR", "DEX"};
            } else if ((srcReqJob & 2) != 0) {
                statName.left = new String[]{"INT", "LUK"};
            } else if ((srcReqJob & 4) != 0) {
                statName.left = new String[]{"DEX", "STR"};
            } else if ((srcReqJob & 8) != 0) {
                statName.left = new String[]{"LUK", "DEX"};
            } else if ((srcReqJob & 0x10) != 0) {
                boolean isDexPirate;
                boolean bl = isDexPirate = MapleWeapon.靈魂射手.check(this.getItemId()) || MapleWeapon.火槍.check(this.getItemId()) || JobConstants.isDexPirate(this.getReqSpecJob() * 100);
                if (!(isDexPirate || ItemConstants.類型.武器(this.getItemId()) || ItemConstants.類型.副手(this.getItemId()))) {
                    isDexPirate = JobConstants.isDexPirate(jobId);
                }
                statName.left = isDexPirate ? new String[]{"DEX", "STR"} : new String[]{"STR", "DEX"};
            }
            if ((dstReqJob & 1) != 0) {
                statName.right = new String[]{"STR", "DEX"};
            } else if ((dstReqJob & 2) != 0) {
                statName.right = new String[]{"INT", "LUK"};
            } else if ((dstReqJob & 4) != 0) {
                statName.right = new String[]{"DEX", "STR"};
            } else if ((dstReqJob & 8) != 0) {
                statName.right = new String[]{"LUK", "DEX"};
            } else if ((dstReqJob & 0x10) != 0) {
                boolean isDexPirate;
                boolean bl = isDexPirate = MapleWeapon.靈魂射手.check(itemID) || MapleWeapon.火槍.check(itemID) || JobConstants.isDexPirate(normalEquip.getReqSpecJob() * 100);
                if (!(isDexPirate || ItemConstants.類型.武器(itemID) || ItemConstants.類型.副手(itemID))) {
                    isDexPirate = JobConstants.isDexPirate(jobId);
                }
                statName.right = isDexPirate ? new String[]{"DEX", "STR"} : new String[]{"STR", "DEX"};
            }
            if (!(statName.left == null || statName.right == null || ((String[])statName.left)[0].equals(((String[])statName.right)[0]) && ((String[])statName.left)[1].equals(((String[])statName.right)[1]))) {
                if (("STR".equals(((String[])statName.left)[0]) || "DEX".equals(((String[])statName.left)[0])) && ("STR".equals(((String[])statName.right)[0]) || "DEX".equals(((String[])statName.right)[0]))) {
                    this.nirvanaFlame.transmitStat(NirvanaFlame.EquipExFlag.FLAGEx_iSTR, NirvanaFlame.EquipExFlag.FLAGEx_iDEX);
                    tempValue = addStr;
                    addStr = addDex;
                    addDex = tempValue;
                    ((String[])statName.left)[1] = null;
                } else if (("STR".equals(((String[])statName.left)[0]) || "INT".equals(((String[])statName.left)[0])) && ("STR".equals(((String[])statName.right)[0]) || "INT".equals(((String[])statName.right)[0]))) {
                    this.nirvanaFlame.transmitStat(NirvanaFlame.EquipExFlag.FLAGEx_iSTR, NirvanaFlame.EquipExFlag.FLAGEx_iINT);
                    this.nirvanaFlame.transmitStat(NirvanaFlame.EquipExFlag.FLAGEx_iDEX, NirvanaFlame.EquipExFlag.FLAGEx_iLUK);
                    tempValue = addStr;
                    addStr = addInt;
                    addInt = tempValue;
                    tempValue = addDex;
                    addDex = addLuk;
                    addLuk = tempValue;
                } else if (("STR".equals(((String[])statName.left)[0]) || "LUK".equals(((String[])statName.left)[0])) && ("STR".equals(((String[])statName.right)[0]) || "LUK".equals(((String[])statName.right)[0]))) {
                    this.nirvanaFlame.transmitStat(NirvanaFlame.EquipExFlag.FLAGEx_iSTR, NirvanaFlame.EquipExFlag.FLAGEx_iLUK);
                    tempValue = addStr;
                    addStr = addLuk;
                    addLuk = tempValue;
                    ((String[])statName.left)[1] = null;
                } else if (("DEX".equals(((String[])statName.left)[0]) || "INT".equals(((String[])statName.left)[0])) && ("DEX".equals(((String[])statName.right)[0]) || "INT".equals(((String[])statName.right)[0]))) {
                    this.nirvanaFlame.transmitStat(NirvanaFlame.EquipExFlag.FLAGEx_iDEX, NirvanaFlame.EquipExFlag.FLAGEx_iINT);
                    this.nirvanaFlame.transmitStat(NirvanaFlame.EquipExFlag.FLAGEx_iSTR, NirvanaFlame.EquipExFlag.FLAGEx_iLUK);
                    tempValue = addDex;
                    addDex = addInt;
                    addInt = tempValue;
                    tempValue = addStr;
                    addStr = addLuk;
                    addLuk = tempValue;
                } else if (("DEX".equals(((String[])statName.left)[0]) || "LUK".equals(((String[])statName.left)[0])) && ("DEX".equals(((String[])statName.right)[0]) || "LUK".equals(((String[])statName.right)[0]))) {
                    this.nirvanaFlame.transmitStat(NirvanaFlame.EquipExFlag.FLAGEx_iDEX, NirvanaFlame.EquipExFlag.FLAGEx_iLUK);
                    tempValue = addDex;
                    addDex = addLuk;
                    addLuk = tempValue;
                    if ("DEX".equals(((String[])statName.left)[0])) {
                        this.nirvanaFlame.transmitStat(NirvanaFlame.EquipExFlag.FLAGEx_iSTR, NirvanaFlame.EquipExFlag.FLAGEx_iDEX);
                        tempValue = addStr;
                        addStr = addDex;
                        addDex = tempValue;
                        ((String[])statName.left)[1] = "DEX";
                    } else {
                        this.nirvanaFlame.transmitStat(NirvanaFlame.EquipExFlag.FLAGEx_iSTR, NirvanaFlame.EquipExFlag.FLAGEx_iLUK);
                        tempValue = addStr;
                        addStr = addLuk;
                        addLuk = tempValue;
                        ((String[])statName.left)[1] = "LUK";
                    }
                    ((String[])statName.right)[1] = "STR";
                } else if (("INT".equals(((String[])statName.left)[0]) || "LUK".equals(((String[])statName.left)[0])) && ("INT".equals(((String[])statName.right)[0]) || "LUK".equals(((String[])statName.right)[0]))) {
                    this.nirvanaFlame.transmitStat(NirvanaFlame.EquipExFlag.FLAGEx_iINT, NirvanaFlame.EquipExFlag.FLAGEx_iLUK);
                    tempValue = addInt;
                    addInt = addLuk;
                    addLuk = tempValue;
                    if ("INT".equals(((String[])statName.left)[0])) {
                        this.nirvanaFlame.transmitStat(NirvanaFlame.EquipExFlag.FLAGEx_iINT, NirvanaFlame.EquipExFlag.FLAGEx_iDEX);
                        tempValue = addInt;
                        addInt = addDex;
                        addDex = tempValue;
                    } else {
                        this.nirvanaFlame.transmitStat(NirvanaFlame.EquipExFlag.FLAGEx_iLUK, NirvanaFlame.EquipExFlag.FLAGEx_iDEX);
                        tempValue = addLuk;
                        addLuk = addDex;
                        addDex = tempValue;
                    }
                    ((String[])statName.left)[1] = ((String[])statName.left)[0];
                    ((String[])statName.right)[1] = "DEX";
                }
                for (int i = 0; i < 2; ++i) {
                    if (((String[])statName.left)[i] == null) continue;
                    int moveValue = 0;
                    switch (((String[])statName.left)[i]) {
                        case "STR": {
                            moveValue = 4;
                            break;
                        }
                        case "DEX": {
                            moveValue = 3;
                            break;
                        }
                        case "INT": {
                            moveValue = 2;
                            break;
                        }
                        case "LUK": {
                            moveValue = 1;
                        }
                    }
                    switch (((String[])statName.right)[i]) {
                        case "STR": {
                            moveValue -= 4;
                            break;
                        }
                        case "DEX": {
                            moveValue -= 3;
                            break;
                        }
                        case "INT": {
                            moveValue -= 2;
                            break;
                        }
                        case "LUK": {
                            --moveValue;
                        }
                    }
                    if (moveValue == 0) continue;
                    for (int j = 1; j <= 6; ++j) {
                        int pot = this.getPotential(j > 3 ? j - 3 : j, j > 3);
                        switch (pot) {
                            case 30047: {
                                pot = 30041;
                                break;
                            }
                            case 30048: {
                                pot = 30043;
                                break;
                            }
                            case 40047: {
                                pot = 40042;
                                break;
                            }
                            case 40048: {
                                pot = 40044;
                            }
                        }
                        if (pot >= 70000 || pot <= 0) continue;
                        Map<String, Integer> data = ii.getPotentialInfo((int)pot).get((int)0).data;
                        if (data.containsKey("inc" + ((String[])statName.left)[i]) || data.containsKey("inc" + ((String[])statName.left)[i] + "r") || data.containsKey("inc" + ((String[])statName.left)[i] + "lv")) {
                            this.setPotential(pot + moveValue, j > 3 ? j - 3 : j, j > 3);
                            continue;
                        }
                        if (!data.containsKey("inc" + ((String[])statName.right)[i]) && !data.containsKey("inc" + ((String[])statName.right)[i] + "r") && !data.containsKey("inc" + ((String[])statName.right)[i] + "lv")) continue;
                        this.setPotential(pot - moveValue, j > 3 ? j - 3 : j, j > 3);
                    }
                }
            }
        }
        this.str = (short)(normalEquip.str + addStr);
        this.dex = (short)(normalEquip.dex + addDex);
        this._int = (short)(normalEquip._int + addInt);
        this.luk = (short)(normalEquip.luk + addLuk);
        this.hp = (short)(normalEquip.hp + addHp);
        this.mp = (short)(normalEquip.mp + addMp);
        this.mad = (short)(normalEquip.mad + addMad);
        this.mdd = (short)(normalEquip.mdd + addMdd);
        this.pad = (short)(normalEquip.pad + addPad);
        this.pdd = (short)(normalEquip.pdd + addPdd);
        this.acc = (short)(normalEquip.acc + addAcc);
        this.avoid = (short)(normalEquip.avoid + addAvoid);
        this.hands = (short)(normalEquip.hands + addHands);
        this.speed = (short)(normalEquip.speed + addSpeed);
        this.jump = (short)(normalEquip.jump + addJump);
        if (resetStat) {
            this.nirvanaFlame.resetEquipExStats(this);
            this.starForce.resetEquipStats(this);
        }
    }

    public void setMvpEquip(boolean isMvpEquip) {
        this.mvpEquip = isMvpEquip;
    }

    public boolean isMvpEquip() {
        return this.isMvpEquip(true);
    }

    public boolean isMvpEquip(boolean checkPermission) {
        return (!checkPermission || Auth.checkPermission("MVPEquip")) && this.mvpEquip;
    }

    public short getAut() {
        return this.aut;
    }

    public void setAut(short aut) {
        this.aut = aut;
    }

    public int getAutExp() {
        return this.autExp;
    }

    public void setAutExp(int autExp) {
        this.autExp = autExp;
    }

    public short getAutLevel() {
        return this.autLevel;
    }

    public void setAutLevel(short autLevel) {
        this.autLevel = autLevel;
    }

    @Override
    public void encode(OutPacket outPacket) {
        int unkFlag;
        int sfFlag;
        int i;
        super.encode(outPacket);
        boolean hasUniqueId = this.encodeBaseRaw(outPacket);
        boolean isCashItem = MapleItemInformationProvider.getInstance().isCash(this.getItemId());
        outPacket.encodeByte(0);
        this.encodeEquipBase(outPacket);
        outPacket.encodeString(this.isMvpEquip() ? "ＭＶＰ" : this.getOwner(), 15);
        outPacket.encodeByte(this.getState(true) > 0 && this.getState(true) < 17 ? this.getState(false) | 0x20 : this.getState(false));
        outPacket.encodeByte(this.getStarForceLevel());
        for (i = 1; i <= 3; ++i) {
            outPacket.encodeShort(this.getPotential(i, false) <= 0 ? 0 : this.getPotential(i, false));
        }
        for (int j = 1; j <= 3; ++j) {
            outPacket.encodeShort(this.getState(true) > 0 && this.getState(true) < 17 ? (j == 1 ? (int)this.getState(true) : 0) : this.getPotential(j, true));
        }
        outPacket.encodeShort(isCashItem ? 0 : this.getItemSkin() % 10000);
        outPacket.encodeShort(this.getSocketState());
        if (!hasUniqueId) {
            outPacket.encodeLong(this.getSN());
        }
        outPacket.encodeLong(0L);
        outPacket.encodeFT(FileTime.fromType(FileTime.Type.ZERO_TIME));
        outPacket.encodeInt(0);
        for (i = 0; i < 3; ++i) {
            outPacket.encodeInt(0);
        }
        outPacket.encodeLong(0L);
        outPacket.encodeInt(0);
        outPacket.encodeInt(0);
        outPacket.encodeShort(this.getSoulOptionID());
        outPacket.encodeShort(this.getSoulSocketID());
        outPacket.encodeShort(this.getSoulOption());
        if (ItemConstants.類型.秘法符文(this.getItemId())) {
            outPacket.encodeShort(this.getARC());
            outPacket.encodeInt(this.getArcExp());
            outPacket.encodeShort(this.getARCLevel());
        }
        if (ItemConstants.類型.真實符文(this.getItemId())) {
            outPacket.encodeShort(this.getAut());
            outPacket.encodeInt(this.getAutExp());
            outPacket.encodeShort(this.getAutLevel());
        }
        outPacket.encodeShort(-1);
        outPacket.encodeFT(FileTime.fromType(FileTime.Type.MAX_TIME));
        outPacket.encodeFT(FileTime.fromType(FileTime.Type.ZERO_TIME));
        outPacket.encodeFT(FileTime.fromType(FileTime.Type.MAX_TIME));
        outPacket.encodeInt(0);
        outPacket.encodeInt(0);
        outPacket.encodeInt(0);
        if (ItemConstants.類型.機器人(this.getItemId())) {
            if (this.getAndroid() != null) {
                this.getAndroid().encodeAndroidLook(outPacket);
            } else {
                outPacket.encodeArr(new byte[28]);
            }
        }
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);
        if (this.getPosition() < 0 && EnhanceResultType.EQUIP_MARK.check(this.getEnchantBuff())) {
            sfFlag = 0;
            unkFlag = 0;
        } else {
            sfFlag = this.getEquipSFBaseFlag();
            unkFlag = 0;
        }
        this.encodeEquipCalcStat(outPacket, unkFlag, 2);
        outPacket.encodeByte(sfFlag > 0);
        if (sfFlag > 0) {
            this.encodeEquipCalcStat(outPacket, sfFlag, 1);
        }
        outPacket.encodeInt(isCashItem ? this.getItemSkin() : 0);
        outPacket.encodeFT(FileTime.fromType(FileTime.Type.ZERO_TIME));
        outPacket.encodeLong(1L);
        outPacket.encodeArr(new byte[80]);
        outPacket.encodeInt(6);
    }

    public void encodeEquipBase(OutPacket outPacket) {
        int exFlag;
        int baseFlag;
        if (this.getPosition() < 0 && EnhanceResultType.EQUIP_MARK.check(this.getEnchantBuff())) {
            baseFlag = 0;
            exFlag = 0;
        } else {
            baseFlag = this.getEquipBaseFlag();
            exFlag = this.getEquipSpecialFlag();
        }
        this.encodeEquipCalcStat(outPacket, baseFlag, 0);
        outPacket.encodeInt(exFlag);
        if (EquipSpecialStat.可使用捲軸次數.check(exFlag)) {
            outPacket.encodeByte(this.getRestUpgradeCount());
        }
        if (EquipSpecialStat.捲軸強化次數.check(exFlag)) {
            outPacket.encodeByte(this.getCurrentUpgradeCount());
        }
        if (EquipSpecialStat.狀態.check(exFlag)) {
            outPacket.encodeInt(this.getCAttribute());
        }
        if (EquipSpecialStat.裝備技能.check(exFlag)) {
            outPacket.encodeByte(this.getIncSkill() > 0);
        }
        if (EquipSpecialStat.裝備等級.check(exFlag)) {
            if (this.isSealedEquip()) {
                outPacket.encodeByte(this.getSealedLevel());
            } else {
                outPacket.encodeByte(Math.max(this.getBaseLevel(), this.getEquipLevel()));
            }
        }
        if (EquipSpecialStat.裝備經驗.check(exFlag)) {
            if (this.isSealedEquip()) {
                outPacket.encodeLong(this.getSealedExp());
            } else {
                outPacket.encodeLong(this.getExpPercentage() * 100000L);
            }
        }
        if (EquipSpecialStat.耐久度.check(exFlag)) {
            outPacket.encodeInt(this.getDurability());
        }
        if (EquipSpecialStat.鎚子.check(exFlag)) {
            outPacket.encodeShort(this.getViciousHammer());
            outPacket.encodeShort(this.getPlatinumHammer());
        }
        if (EquipSpecialStat.套用等級減少.check(exFlag)) {
            outPacket.encodeByte(this.getDownLevel());
        }
        if (EquipSpecialStat.ENHANCT_BUFF.check(exFlag)) {
            outPacket.encodeShort(this.getEnchantBuff());
        }
        if (EquipSpecialStat.DURABILITY_SPECIAL.check(exFlag)) {
            outPacket.encodeInt(0);
        }
        if (EquipSpecialStat.REQUIRED_LEVEL.check(exFlag)) {
            outPacket.encodeByte(this.getiIncReq());
        }
        if (EquipSpecialStat.YGGDRASIL_WISDOM.check(exFlag)) {
            outPacket.encodeByte(this.getYggdrasilWisdom());
        }
        if (EquipSpecialStat.FINAL_STRIKE.check(exFlag)) {
            outPacket.encodeByte(this.getFinalStrike());
        }
        if (EquipSpecialStat.BOSS傷.check(exFlag)) {
            outPacket.encodeByte(this.getTotalBossDamage());
        }
        if (EquipSpecialStat.無視防禦.check(exFlag)) {
            outPacket.encodeByte(this.getTotalIgnorePDR());
        }
        if (EquipSpecialStat.總傷害.check(exFlag)) {
            outPacket.encodeByte(this.getTotalTotalDamage());
        }
        if (EquipSpecialStat.全屬性.check(exFlag)) {
            outPacket.encodeByte(this.getTotalAllStat());
        }
        if (EquipSpecialStat.剪刀次數.check(exFlag)) {
            outPacket.encodeByte(-1);
        }
        if (EquipSpecialStat.輪迴星火.check(exFlag)) {
            outPacket.encodeLong(this.getFlameFlag());
        }
        if (EquipSpecialStat.星力強化.check(exFlag)) {
            outPacket.encodeInt(255);
        }
    }

    public void encodeEquipCalcStat(OutPacket outPacket, int baseFlag, int type) {
        outPacket.encodeInt(baseFlag);
        if (EquipBaseStat.力量.check(baseFlag)) {
            outPacket.encodeShort(type == 0 ? this.getStr() : this.getSF_Str());
        }
        if (EquipBaseStat.敏捷.check(baseFlag)) {
            outPacket.encodeShort(type == 0 ? this.getDex() : this.getSF_Dex());
        }
        if (EquipBaseStat.智力.check(baseFlag)) {
            outPacket.encodeShort(type == 0 ? this.getInt() : this.getSF_Int());
        }
        if (EquipBaseStat.幸運.check(baseFlag)) {
            outPacket.encodeShort(type == 0 ? this.getLuk() : this.getSF_Luk());
        }
        if (EquipBaseStat.MaxHP.check(baseFlag)) {
            outPacket.encodeShort(ItemConstants.類型.秘法符文(this.getItemId()) || ItemConstants.類型.真實符文(this.getItemId()) ? this.getTotalHp() / 10 : (int)this.getTotalHp());
        }
        if (EquipBaseStat.MaxMP.check(baseFlag)) {
            outPacket.encodeShort(type == 0 ? this.getMp() : this.getSF_Mp());
        }
        if (EquipBaseStat.攻擊力.check(baseFlag)) {
            outPacket.encodeShort(type == 0 ? this.getPad() : this.getSF_Pad());
        }
        if (EquipBaseStat.魔力.check(baseFlag)) {
            outPacket.encodeShort(type == 0 ? this.getMad() : this.getSF_Mad());
        }
        if (EquipBaseStat.防禦力.check(baseFlag)) {
            outPacket.encodeShort(type == 0 ? this.getPdd() : this.getSF_Pdd());
        }
        if (EquipBaseStat.魔法防禦力.check(baseFlag)) {
            outPacket.encodeShort(type == 0 ? this.getMdd() : this.getSF_Mdd());
        }
        if (EquipBaseStat.靈敏度.check(baseFlag)) {
            outPacket.encodeShort(type == 0 ? this.getHands() : this.getSF_Hands());
        }
        if (EquipBaseStat.移動速度.check(baseFlag)) {
            outPacket.encodeShort(type == 0 ? this.getSpeed() : this.getSF_Speed());
        }
        if (EquipBaseStat.跳躍力.check(baseFlag)) {
            outPacket.encodeShort(type == 0 ? this.getJump() : this.getSF_Jump());
        }
    }
}

