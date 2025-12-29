/*
 * Decompiled with CFR 0.152.
 */
package Plugin.script;

import Client.inventory.Item;
import Config.constants.enums.NpcMessageType;
import Config.constants.enums.ScriptParam;
import java.util.List;

public class NpcScriptInfo {
    private List<Item> items;
    private int[] options;
    private int[] options2;
    private byte speakerType = (byte)3;
    private int overrideSpeakerTemplateID = 0;
    private short param;
    private byte color = 0;
    private String text;
    private NpcMessageType messageType;
    private String[] images;
    private int srcBeauty;
    private int drtBeauty;
    private int srcBeauty2;
    private int drtBeauty2;
    private long min;
    private long max;
    private int col;
    private int line;
    private int itemID;
    private String defaultText;
    private long defaultNumber;
    private byte type;
    private int time;
    private String title;
    private String problemText;
    private String hintText;
    private int quizType;
    private int answer;
    private int correctAnswers;
    private int remaining;
    private byte secondLookValue;
    private int dlgType;
    private int defaultSelect;
    private String[] selectText;
    private int objectID;
    private int templateID;
    private int innerOverrideSpeakerTemplateID;
    private boolean prevPossible;
    private boolean nextPossible;
    private int delay;
    private int unk;
    private boolean bUnk;
    private int index = 0;

    public NpcScriptInfo deepCopy() {
        NpcScriptInfo nsi = new NpcScriptInfo();
        nsi.items = this.items;
        if (this.options != null) {
            nsi.options = new int[this.options.length];
            System.arraycopy(this.options, 0, nsi.options, 0, this.options.length);
        }
        if (this.options2 != null) {
            nsi.options2 = new int[this.options2.length];
            System.arraycopy(this.options2, 0, nsi.options2, 0, this.options2.length);
        }
        nsi.speakerType = this.speakerType;
        nsi.overrideSpeakerTemplateID = this.overrideSpeakerTemplateID;
        nsi.param = this.param;
        nsi.color = this.color;
        nsi.text = this.text;
        nsi.messageType = this.messageType;
        if (this.images != null) {
            nsi.images = (String[])this.images.clone();
        }
        nsi.srcBeauty = this.srcBeauty;
        nsi.drtBeauty = this.drtBeauty;
        nsi.srcBeauty2 = this.srcBeauty2;
        nsi.drtBeauty2 = this.drtBeauty2;
        nsi.min = this.min;
        nsi.max = this.max;
        nsi.col = this.col;
        nsi.line = this.line;
        nsi.itemID = this.itemID;
        nsi.defaultText = this.defaultText;
        nsi.defaultNumber = this.defaultNumber;
        nsi.type = this.type;
        nsi.time = this.time;
        nsi.title = this.title;
        nsi.problemText = this.problemText;
        nsi.hintText = this.hintText;
        nsi.quizType = this.quizType;
        nsi.answer = this.answer;
        nsi.correctAnswers = this.correctAnswers;
        nsi.remaining = this.remaining;
        nsi.secondLookValue = this.secondLookValue;
        nsi.dlgType = this.dlgType;
        nsi.defaultSelect = this.defaultSelect;
        nsi.selectText = this.selectText;
        nsi.objectID = this.objectID;
        nsi.templateID = this.templateID;
        nsi.prevPossible = this.prevPossible;
        nsi.nextPossible = this.nextPossible;
        nsi.delay = this.delay;
        nsi.unk = this.unk;
        nsi.bUnk = this.bUnk;
        nsi.index = this.index;
        return nsi;
    }

    public byte getSpeakerType() {
        return this.speakerType;
    }

    public void setSpeakerType(int speakerType) {
        this.speakerType = (byte)speakerType;
    }

    public int getOverrideSpeakerTemplateID() {
        return this.overrideSpeakerTemplateID;
    }

    public void setOverrideSpeakerTemplateID(int overrideSpeakerTemplateID) {
        this.overrideSpeakerTemplateID = overrideSpeakerTemplateID;
    }

    public short getParam() {
        return this.param;
    }

    public void addParam(ScriptParam param) {
        this.addParam(param.getValue());
    }

    public void addParam(int param) {
        this.param = (short)(this.param | (short)param);
    }

    public void removeParam(ScriptParam param) {
        this.removeParam(param.getValue());
    }

    public void removeParam(int param) {
        this.param = (short)(this.param & (short)(~param));
    }

    public void setParam(int param) {
        this.param = (short)param;
    }

    public byte getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = (byte)color;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setMessageType(NpcMessageType messageType) {
        this.messageType = messageType;
    }

    public NpcMessageType getMessageType() {
        return this.messageType;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String[] getImages() {
        return this.images;
    }

    public void setSrcBeauty(int srcBeauty) {
        this.srcBeauty = srcBeauty;
    }

    public int getSrcBeauty() {
        return this.srcBeauty;
    }

    public void setDrtBeauty(int drtBeauty) {
        this.drtBeauty = drtBeauty;
    }

    public int getDrtBeauty() {
        return this.drtBeauty;
    }

    public void setSrcBeauty2(int srcBeauty) {
        this.srcBeauty2 = srcBeauty;
    }

    public int getSrcBeauty2() {
        return this.srcBeauty2;
    }

    public void setDrtBeauty2(int drtBeauty) {
        this.drtBeauty2 = drtBeauty;
    }

    public int getDrtBeauty2() {
        return this.drtBeauty2;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getMin() {
        return this.min;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public long getMax() {
        return this.max;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getCol() {
        return this.col;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getLine() {
        return this.line;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getItemID() {
        return this.itemID;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public String getDefaultText() {
        return this.defaultText;
    }

    public long getDefaultNumber() {
        return this.defaultNumber;
    }

    public void setDefaultNumber(long defaultNumber) {
        this.defaultNumber = defaultNumber;
    }

    public byte getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = (byte)type;
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProblemText() {
        return this.problemText;
    }

    public void setProblemText(String problemText) {
        this.problemText = problemText;
    }

    public String getHintText() {
        return this.hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    public int getQuizType() {
        return this.quizType;
    }

    public void setQuizType(int quizType) {
        this.quizType = quizType;
    }

    public int getAnswer() {
        return this.answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public int getCorrectAnswers() {
        return this.correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getRemaining() {
        return this.remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public byte getSecondLookValue() {
        return this.secondLookValue;
    }

    public void setSecondLookValue(int vale) {
        this.secondLookValue = (byte)vale;
    }

    public int getDlgType() {
        return this.dlgType;
    }

    public void setDlgType(int dlgType) {
        this.dlgType = dlgType;
    }

    public void setDefaultSelect(int defaultSelect) {
        this.defaultSelect = defaultSelect;
    }

    public int getDefaultSelect() {
        return this.defaultSelect;
    }

    public void setSelectText(String[] selectText) {
        this.selectText = selectText;
    }

    public String[] getSelectText() {
        return this.selectText;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return this.items;
    }

    public void setOptions(int[] options) {
        this.options = options;
    }

    public int[] getOptions() {
        return this.options;
    }

    public void setOptions2(int[] options) {
        this.options2 = options;
    }

    public int[] getOptions2() {
        return this.options2;
    }

    public boolean hasParam(ScriptParam param) {
        return param.check(this.getParam());
    }

    public int getObjectID() {
        return this.objectID;
    }

    public void setObjectID(int objectID) {
        this.objectID = objectID;
    }

    public int getTemplateID() {
        return this.templateID;
    }

    public void setTemplateID(int templateID) {
        this.templateID = templateID;
    }

    public void setInnerOverrideSpeakerTemplateID(int innerOverrideSpeakerTemplateID) {
        if (innerOverrideSpeakerTemplateID > 0) {
            this.addParam(ScriptParam.OverrideSpeakerID);
        } else {
            this.removeParam(ScriptParam.OverrideSpeakerID);
        }
        this.innerOverrideSpeakerTemplateID = innerOverrideSpeakerTemplateID;
    }

    public boolean isPrevPossible() {
        return this.prevPossible;
    }

    public void setPrevPossible(boolean prevPossible) {
        this.prevPossible = prevPossible;
    }

    public boolean isNextPossible() {
        return this.nextPossible;
    }

    public void setNextPossible(boolean nextPossible) {
        this.nextPossible = nextPossible;
    }

    public int getDelay() {
        return this.delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getUnk() {
        return this.unk;
    }

    public void setUnk(int unk) {
        this.unk = unk;
    }

    public boolean isUnk() {
        return this.bUnk;
    }

    public void setBUnk(boolean unk) {
        this.bUnk = unk;
    }

    public int getInnerOverrideSpeakerTemplateID() {
        return this.innerOverrideSpeakerTemplateID;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

