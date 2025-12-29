/*
 * Decompiled with CFR 0.152.
 */
package Net.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StructItemOption {
    public static final List<String> types = new LinkedList<String>();
    public final Map<String, Integer> data = new HashMap<String, Integer>();
    public int optionType;
    public int reqLevel;
    public int opID;
    public String face;
    public String opString;

    public int get(String type) {
        return this.data.get(type) != null ? this.data.get(type) : 0;
    }

    public String toString() {
        return this.toString(false);
    }

    public String toString(boolean info) {
        String ret = this.opString;
        for (Map.Entry<String, Integer> entry : this.data.entrySet()) {
            ret = ((String)ret).replace("#" + entry.getKey(), entry.getValue().toString());
        }
        if (info) {
            ret = (String)ret + "(" + this.opID + ") optionType: " + this.optionType + " reqLevel:" + this.reqLevel + " face:" + this.face;
        }
        return ret;
    }
}

