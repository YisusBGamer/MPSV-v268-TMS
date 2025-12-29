/*
 * Decompiled with CFR 0.152.
 */
package Config.constants.enums;

public enum ScriptParam {
    Normal(0),
    NoEsc(1),
    PlayerAsSpeaker(3),
    OverrideSpeakerID(4),
    FlipSpeaker(8),
    PlayerAsSpeakerFlip(16),
    BoxChat(32),
    NPC_N(64),
    LargeBoxChat(128),
    Replace(256);

    private final Integer value;

    private ScriptParam(Integer value) {
        this.value = value;
    }

    public final Integer getValue() {
        return this.value;
    }

    public final boolean check(int n) {
        return (n & this.value) != 0;
    }
}

