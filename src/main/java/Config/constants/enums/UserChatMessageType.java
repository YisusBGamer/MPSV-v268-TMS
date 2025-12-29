/*
 * Decompiled with CFR 0.152.
 */
package Config.constants.enums;

public enum UserChatMessageType {
    普通(0),
    密語(1),
    隊伍群組(2),
    好友群組(3),
    公會群組(4),
    聯盟群組(5),
    遊戲描述(6),
    提示(7),
    通知(8),
    公告(9),
    管理員對話(10),
    系統(11),
    頻道喇叭(12),
    世界喇叭(13, true),
    世界喇叭_公會技能(14, true),
    道具喇叭(15, true),
    超性能擴音器(16, true),
    紫(17),
    淺黃(18),
    抽獎喇叭_世界(19, true),
    灰喇叭(20, true),
    黃(21),
    青(22),
    黑_黃(23),
    道具訊息(24),
    粉(25),
    藍加粉(5),
    粉喇叭(27, true),
    方塊洗洗樂(28),
    暗黃(29),
    淺紫(30),
    白(32),
    黑_暗黃(34),
    紅(35),
    骷髏喇叭(37, true),
    黑_紅喇叭(38, true),
    黃_綠喇叭(39, true),
    黃_紅喇叭(40, true),
    黑_粉喇叭(41, true),
    黑_黃喇叭(42, true),
    黃_紅喇叭2(43, true),
    白_紅喇叭(44, true),
    世界廣播(44),
    白2(45),
    白3(46),
    黑_綠喇叭(47, true),
    黑_紅喇叭2(48, true),
    黑_黃喇叭2(49, true),
    白4(50);

    private final int type;
    private final boolean isSpeaker;

    private UserChatMessageType(int type) {
        this(type, false);
    }

    private UserChatMessageType(int type, boolean isSpeaker) {
        this.type = type;
        this.isSpeaker = isSpeaker;
    }

    public static UserChatMessageType getByType(int type) {
        for (UserChatMessageType cType : UserChatMessageType.values()) {
            if (cType.getType() != type) continue;
            return cType;
        }
        return null;
    }

    public short getType() {
        return (short)this.type;
    }

    public boolean isSpeaker() {
        return this.isSpeaker;
    }

    public String getMsg(String msg) {
        if (!this.isSpeaker() || !msg.contains(":")) {
            return msg;
        }
        return msg.substring(0, msg.indexOf(":")) + " " + msg.substring(msg.indexOf(":"));
    }
}

