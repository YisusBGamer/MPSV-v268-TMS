/*
 * Decompiled with CFR 0.152.
 */
package Plugin.script;

import Config.constants.enums.ScriptType;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.script.Bindings;
import javax.script.Invocable;

public class ScriptInfo {
    private ScriptType scriptType;
    private Bindings bindings;
    private int parentID;
    private String scriptName;
    private Invocable invocable;
    private final Lock lock = new ReentrantLock();
    private final Queue<Object> responses = new LinkedList<Object>();
    private int objectID;
    private String fileDir;
    private boolean isActive;

    public ScriptInfo(ScriptType scriptType, Bindings bindings, int parentID, String scriptName) {
        this.scriptType = scriptType;
        this.parentID = parentID;
        this.scriptName = scriptName;
        this.bindings = bindings;
    }

    public ScriptInfo(ScriptType scriptType, Bindings bindings, int parentID, String scriptName, Invocable invocable) {
        this.scriptType = scriptType;
        this.bindings = bindings;
        this.parentID = parentID;
        this.scriptName = scriptName;
        this.invocable = invocable;
    }

    public ScriptType getScriptType() {
        return this.scriptType;
    }

    public void setScriptType(ScriptType scriptType) {
        this.scriptType = scriptType;
    }

    public Bindings getBindings() {
        return this.bindings;
    }

    public void setBindings(Bindings bindings) {
        this.bindings = bindings;
    }

    public int getParentID() {
        return this.parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    public String getScriptName() {
        return this.scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public Invocable getInvocable() {
        return this.invocable;
    }

    public void setInvocable(Invocable invocable) {
        this.invocable = invocable;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public void reset() {
        this.setScriptType(null);
        this.setBindings(null);
        this.setParentID(0);
        this.setScriptName("");
        this.setInvocable(null);
        this.addResponse(null);
        this.setObjectID(0);
        this.setActive(false);
    }

    public int getObjectID() {
        return this.objectID;
    }

    public void setObjectID(int objectID) {
        this.objectID = objectID;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addResponse(Object response) {
        if (response == null) {
            this.responses.clear();
        }
        this.responses.add(response);
        Lock lock = this.lock;
        synchronized (lock) {
            this.lock.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object awaitResponse() {
        if (this.responses.size() > 0) {
            return this.responses.poll();
        }
        Lock lock = this.lock;
        synchronized (lock) {
            try {
                this.lock.wait();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
        return this.responses.poll();
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public String getFileDir() {
        return this.fileDir;
    }
}

