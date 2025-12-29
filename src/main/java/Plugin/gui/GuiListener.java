/*
 * Decompiled with CFR 0.152.
 */
package Plugin.gui;

import Client.MapleCharacter;

public interface GuiListener {
    public void playerRegistered(int var1);

    public void playerTalked(int var1, int var2, String var3);

    public void updatePlayer(MapleCharacter var1);

    public void onlineStatusChanged(int var1, int var2);
}

