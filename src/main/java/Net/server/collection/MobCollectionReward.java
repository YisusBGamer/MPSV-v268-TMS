/*
 * Decompiled with CFR 0.152.
 */
package Net.server.collection;

import Net.server.collection.MobCollectionGroup;
import java.util.HashMap;
import java.util.Map;

public class MobCollectionReward {
    public int recordID;
    public int rewardID;
    public int rewardCount;
    public final Map<Integer, MobCollectionGroup> rewardGroup = new HashMap<Integer, MobCollectionGroup>();
}

