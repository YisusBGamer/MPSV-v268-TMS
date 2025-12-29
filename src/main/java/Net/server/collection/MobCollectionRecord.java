/*
 * Decompiled with CFR 0.152.
 */
package Net.server.collection;

import Net.server.collection.MobCollectionReward;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tools.Pair;

public class MobCollectionRecord {
    public int recordID;
    public final List<Pair<Integer, Integer>> rewards = new ArrayList<Pair<Integer, Integer>>();
    public final Map<Integer, MobCollectionReward> mobCollectionRewards = new HashMap<Integer, MobCollectionReward>();
}

