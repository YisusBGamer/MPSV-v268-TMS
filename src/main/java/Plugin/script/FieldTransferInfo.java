/*
 * Decompiled with CFR 0.152.
 */
package Plugin.script;

import Client.MapleCharacter;
import Net.server.maps.MapleMap;

public class FieldTransferInfo {
    private int fieldId;
    private int portal;
    private boolean init = true;
    private boolean field;

    public int getFieldId() {
        return this.fieldId;
    }

    public void setFieldId(int fieldId) {
        this.init = false;
        this.fieldId = fieldId;
    }

    public int getPortal() {
        return this.portal;
    }

    public void setPortal(int portal) {
        this.portal = portal;
    }

    public boolean isInit() {
        return this.init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public boolean isField() {
        return this.field;
    }

    public void setField(boolean field) {
        this.field = field;
    }

    public void warp(MapleCharacter chr) {
        this.setInit(true);
        chr.changeMap(this.getFieldId(), this.getPortal());
    }

    public void warp(MapleMap field) {
        this.setInit(true);
        for (MapleCharacter chr : field.getAllCharactersThreadsafe()) {
            chr.changeMap(this.getFieldId(), this.getPortal());
        }
    }
}

