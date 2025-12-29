/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Server.world.PlayerBuffValueHolder
 */
package Server.world;

import Client.MapleCoolDownValueHolder;
import Server.world.PlayerBuffValueHolder;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerBuffStorage
implements Serializable {
    private static final long serialVersionUID = -5428338713000883808L;
    private static final Map<Integer, List<PlayerBuffValueHolder>> buffs = new ConcurrentHashMap<Integer, List<PlayerBuffValueHolder>>();
    private static final Map<Integer, List<MapleCoolDownValueHolder>> coolDowns = new ConcurrentHashMap<Integer, List<MapleCoolDownValueHolder>>();

    public static void addBuffsToStorage(int chrid, List<PlayerBuffValueHolder> toStore) {
        buffs.put(chrid, toStore);
    }

    public static void addCooldownsToStorage(int chrid, List<MapleCoolDownValueHolder> toStore) {
        coolDowns.put(chrid, toStore);
    }

    public static List<PlayerBuffValueHolder> getBuffsFromStorage(int chrid) {
        return buffs.remove(chrid);
    }

    public static List<MapleCoolDownValueHolder> getCooldownsFromStorage(int chrid) {
        return coolDowns.remove(chrid);
    }
}

