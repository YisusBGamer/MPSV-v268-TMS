/*
 * Decompiled with CFR 0.152.
 */
package Plugin.script;

import Config.constants.enums.NpcMessageType;
import Plugin.script.NpcScriptInfo;
import java.util.ArrayList;
import java.util.List;

public class ScriptMemory {
    private List<NpcScriptInfo> memory = new ArrayList<NpcScriptInfo>();
    private int position = -1;
    private int sayIndex = 0;

    public boolean hasBack() {
        if (this.position < 0) {
            return false;
        }
        NpcScriptInfo nsi = this.memory.get(this.position);
        if (nsi == null) {
            return false;
        }
        return nsi.isNextPossible();
    }

    public boolean hasNext() {
        if (this.position + 1 >= this.memory.size()) {
            return false;
        }
        return this.memory.get(this.position + 1) != null;
    }

    public NpcScriptInfo decrementAndGet() {
        if (this.position <= 0) {
            return null;
        }
        return this.memory.get(--this.position);
    }

    public NpcScriptInfo getAndDecrement() {
        return this.memory.get(this.position--);
    }

    public NpcScriptInfo get() {
        if (this.memory.isEmpty() || this.position < 0 || this.position >= this.memory.size()) {
            return null;
        }
        return this.memory.get(this.position);
    }

    public NpcScriptInfo getAndIncrement() {
        return this.memory.get(this.position++);
    }

    public NpcScriptInfo incrementAndGet() {
        if (this.position + 1 >= this.memory.size()) {
            return null;
        }
        return this.memory.get(++this.position);
    }

    public void add(NpcScriptInfo nsi) {
        this.memory.add(nsi);
        if (nsi.getMessageType() == NpcMessageType.Say) {
            nsi.setIndex(++this.sayIndex);
        }
        ++this.position;
    }

    public void clear() {
        this.position = -1;
        this.sayIndex = 0;
        this.memory.clear();
    }
}

