/*
 * Decompiled with CFR 0.152.
 */
package Server.login;

public enum JobInfoFlag {
    臉型(1),
    髮型(2),
    臉飾(4),
    耳朵(8),
    尾巴(16),
    帽子(32),
    衣服(64),
    褲裙(128),
    披風(256),
    鞋子(512),
    手套(1024),
    武器(2048),
    副手(4096);

    private final int value;

    private JobInfoFlag(int value) {
        this.value = value;
    }

    public int getVelue() {
        return this.value;
    }

    public boolean check(int x) {
        return (this.value & x) != 0;
    }
}

