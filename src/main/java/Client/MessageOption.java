/*
 * Decompiled with CFR 0.152.
 */
package Client;

import Client.MapleExpStat;
import Client.MapleQuestStatus;
import java.util.Arrays;
import java.util.Map;

public class MessageOption {
    private int _objectId;
    private int _amount;
    private String _text;
    private String _text2;
    private int _combo;
    private byte _mode;
    private byte _color;
    private boolean _bOnQuest;
    private byte _nQuestBonusRate;
    private int _diseaseType;
    private long _expLost;
    private long _mask;
    private int _intExp;
    private byte _byteExp;
    private long _longExp;
    private long _longGain;
    private short _job;
    private int[] _int_data;
    private Map<MapleExpStat, Object> _expGainData;
    private MapleQuestStatus _questStatus;

    public void setObjectId(int value) {
        this._objectId = value;
    }

    public int getObjectId() {
        return this._objectId;
    }

    public void setAmount(int value) {
        this._amount = value;
    }

    public int getAmount() {
        return this._amount;
    }

    public void setText(String value) {
        this._text = value;
    }

    public String getText() {
        return this._text;
    }

    public void setText2(String value) {
        this._text2 = value;
    }

    public String getText2() {
        return this._text2;
    }

    public void setCombo(int value) {
        this._combo = value;
    }

    public int getCombo() {
        return this._combo;
    }

    public void setMode(int value) {
        this._mode = (byte)value;
    }

    public byte getMode() {
        return this._mode;
    }

    public void setColor(int value) {
        this._color = (byte)value;
    }

    public byte getColor() {
        return this._color;
    }

    public void setOnQuest(boolean value) {
        this._bOnQuest = value;
    }

    public boolean getOnQuest() {
        return this._bOnQuest;
    }

    public void setDiseaseType(int value) {
        this._diseaseType = value;
    }

    public int getDiseaseType() {
        return this._diseaseType;
    }

    public void setExpLost(long value) {
        this._expLost = value;
    }

    public long getExpLost() {
        return this._expLost;
    }

    public void setMask(long value) {
        this._mask = value;
    }

    public long getMask() {
        return this._mask;
    }

    public void setIntExp(int value) {
        this._intExp = value;
    }

    public long getIntExp() {
        return this._intExp;
    }

    public void setLongExp(long value) {
        this._longExp = value;
    }

    public long getLongExp() {
        return this._longExp;
    }

    public void setLongGain(long value) {
        this._longGain = value;
    }

    public long getLongGain() {
        return this._longGain;
    }

    public void setJob(short value) {
        this._job = value;
    }

    public short getJob() {
        return this._job;
    }

    public void setIntegerData(int[] value) {
        this._int_data = Arrays.copyOf(value, value.length);
    }

    public int[] getIntegerData() {
        return this._int_data;
    }

    public void setExpGainData(Map<MapleExpStat, Object> value) {
        this._expGainData = value;
    }

    public Map<MapleExpStat, Object> getExpGainData() {
        return this._expGainData;
    }

    public void setQuestStatus(MapleQuestStatus value) {
        this._questStatus = value;
    }

    public MapleQuestStatus getQuestStatus() {
        return this._questStatus;
    }
}

