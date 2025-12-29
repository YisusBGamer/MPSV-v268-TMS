/*
 * Decompiled with CFR 0.152.
 */
package Net.server.commands;

public enum PlayerRank {
    普通('@', 0, "Player"),
    MVP銅牌I('@', 1, "BronzeIMvp"),
    MVP銅牌II('@', 2, "BronzeIIMvp"),
    MVP銅牌III('@', 3, "BronzeIIIMvp"),
    MVP銅牌IV('@', 4, "BronzeIVMvp"),
    MVP銀牌('@', 5, "SilverMvp"),
    MVP金牌('@', 6, "GoldMvp"),
    MVP鑽石('@', 7, "DiamondMvp"),
    MVP紅鑽('@', 8, "RedMvp"),
    實習管理員('!', "/", 1001, "Intern"),
    遊戲管理員('!', "/", 1002, "GM"),
    超級管理員('!', "/", 1003, "SuperGM"),
    伺服管理員('!', "/", 1004, "Admin");

    private final char commandPrefix;
    private final String fullWidthCommandPrefix;
    private final int level;
    private final String folderName;

    private PlayerRank(char ch, int level, String folderName) {
        this(ch, null, level, folderName);
    }

    private PlayerRank(char ch, String fw, int level, String folderName) {
        this.commandPrefix = ch;
        this.fullWidthCommandPrefix = fw;
        this.level = level;
        this.folderName = folderName;
    }

    public char getCommandPrefix() {
        return this.commandPrefix;
    }

    public String getFullWidthCommandPrefix() {
        return this.fullWidthCommandPrefix;
    }

    public int getLevel() {
        return this.level;
    }

    public int getSpLevel() {
        return this.isGm() ? this.level - 實習管理員.getLevel() + 1 : this.level;
    }

    public boolean isGm() {
        return this.level >= 實習管理員.getLevel();
    }

    public String getFolderName() {
        return this.folderName;
    }

    public static PlayerRank getByLevel(int level) {
        for (PlayerRank i : PlayerRank.values()) {
            if (i.getLevel() != level) continue;
            return i;
        }
        return 普通;
    }

    public static PlayerRank getByFolderName(String name) {
        for (PlayerRank i : PlayerRank.values()) {
            if (!i.getFolderName().equalsIgnoreCase(name)) continue;
            return i;
        }
        return 普通;
    }

    public static PlayerRank getByCommandPrefix(char ch) {
        for (PlayerRank i : PlayerRank.values()) {
            if (i.getCommandPrefix() != ch && !String.valueOf(ch).equals(i.getFullWidthCommandPrefix())) continue;
            return i;
        }
        return null;
    }
}

