/*
 * Decompiled with CFR 0.152.
 */
package Client.inventory;

import Client.inventory.InventoryException;
import Client.inventory.Item;
import Client.inventory.MapleInventoryType;
import Config.configs.ServerConfig;
import Config.constants.ItemConstants;
import Net.server.MapleItemInformationProvider;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MapleInventory
implements Iterable<Item>,
Serializable {
    private static final long serialVersionUID = -7238868473236710891L;
    private final Map<Short, Item> inventory = new LinkedHashMap<Short, Item>();
    private final MapleInventoryType type;
    private short slotLimit = 0;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    public static final int MAX_SLOT_LIMIT = 128;

    public MapleInventory(MapleInventoryType type) {
        this.type = type;
    }

    public Item getItem(short slot) {
        this.lock.readLock().lock();
        try {
            Item item = this.inventory.get(slot);
            return item;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public Map<Short, Item> getInventory() {
        this.lock.readLock().lock();
        try {
            Map<Short, Item> map = this.inventory;
            return map;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public short getTrueSlotLimit() {
        return this.slotLimit;
    }

    public short getSlotLimit() {
        if (ServerConfig.DEFAULT_MAX_SLOT) {
            return 128;
        }
        return this.getTrueSlotLimit();
    }

    public void setSlotLimit(short slot) {
        if (slot > 128 || this.type == MapleInventoryType.CASH || this.type == MapleInventoryType.DECORATION) {
            slot = (short)128;
        }
        this.slotLimit = slot;
    }

    public MapleInventoryType getType() {
        return this.type;
    }

    public List<Item> newList() {
        this.lock.readLock().lock();
        try {
            if (this.inventory.size() <= 0) {
                List<Item> list = Collections.emptyList();
                return list;
            }
            LinkedList<Item> linkedList = new LinkedList<Item>(this.inventory.values());
            return linkedList;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public void addFromDB(Item item) {
        this.lock.writeLock().lock();
        try {
            if (item.getPosition() < 0 && !this.type.equals((Object)MapleInventoryType.EQUIPPED)) {
                return;
            }
            if (item.getPosition() > 0 && this.type.equals((Object)MapleInventoryType.EQUIPPED)) {
                return;
            }
            this.inventory.put(item.getPosition(), item);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public void removeItem(short slot) {
        this.removeItem(slot, (short)1, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeItem(short slot, short quantity, boolean allowZero) {
        this.lock.writeLock().lock();
        try {
            Item item = this.inventory.get(slot);
            if (item == null) {
                return;
            }
            item.setQuantity((short)(item.getQuantity() - quantity));
            if (item.getQuantity() < 0) {
                item.setQuantity((short)0);
            }
            if (item.getQuantity() == 0 && !allowZero) {
                this.removeSlot(slot);
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public void removeSlot(short slot) {
        this.lock.writeLock().lock();
        try {
            this.inventory.remove(slot);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public Collection<Item> list() {
        this.lock.readLock().lock();
        try {
            Collection<Item> collection = this.inventory.values();
            return collection;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean isFull() {
        return this.inventory.size() >= this.slotLimit;
    }

    public boolean isFull(int margin) {
        return this.inventory.size() + margin >= this.slotLimit;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int listSize() {
        this.lock.readLock().lock();
        try {
            int n = 0;
            for (Item item : this.list()) {
                if (item.getPosition() <= 10000) continue;
                ++n;
            }
            int n2 = this.inventory.size() - n;
            return n2;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public short getNextFreeSlot() {
        this.lock.readLock().lock();
        try {
            if (this.isFull()) {
                short s = -1;
                return s;
            }
            for (short i = 1; i <= this.slotLimit; i = (short)(i + 1)) {
                if (this.inventory.containsKey(i)) continue;
                short s = i;
                return s;
            }
            short s = -1;
            return s;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public short getNumFreeSlot() {
        this.lock.readLock().lock();
        try {
            if (this.isFull()) {
                short s = 0;
                return s;
            }
            short free = 0;
            for (short i = 1; i <= this.slotLimit; i = (short)(i + 1)) {
                if (this.inventory.containsKey(i)) continue;
                free = (short)(free + 1);
            }
            short s = free;
            return s;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public void addSlot(short slot) {
        this.slotLimit = (short)(this.slotLimit + slot);
        if (this.slotLimit > 128) {
            this.slotLimit = (short)128;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Item findById(int itemId) {
        this.lock.readLock().lock();
        try {
            for (Item item : this.inventory.values()) {
                if (item.getItemId() != itemId) continue;
                Item item2 = item;
                return item2;
            }
            return null;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public Item findBySN(long sn, int itemId) {
        Item item = this.findByLiSN(sn);
        if (item.getItemId() != itemId) {
            return null;
        }
        return item;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Item findByLiSN(long sn) {
        this.lock.readLock().lock();
        try {
            for (Item item : this.inventory.values()) {
                if ((long)item.getSN() != sn) continue;
                Item item2 = item;
                return item2;
            }
            return null;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int countById(int itemId) {
        this.lock.readLock().lock();
        try {
            int possesed = 0;
            for (Item item : this.inventory.values()) {
                if (item.getItemId() != itemId) continue;
                possesed += item.getQuantity();
            }
            int n = possesed;
            return n;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Item> listById(int itemId) {
        this.lock.readLock().lock();
        try {
            ArrayList<Item> ret = new ArrayList<Item>();
            for (Item item : this.inventory.values()) {
                if (item.getItemId() != itemId) continue;
                ret.add(item);
            }
            if (ret.size() > 1) {
                Collections.sort(ret);
            }
            ArrayList<Item> arrayList = ret;
            return arrayList;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Item> listBySN(int sn) {
        this.lock.readLock().lock();
        try {
            ArrayList<Item> ret = new ArrayList<Item>();
            for (Item item : this.inventory.values()) {
                if (item.getSN() <= 0 || item.getSN() != sn) continue;
                ret.add(item);
            }
            if (ret.size() > 1) {
                Collections.sort(ret);
            }
            ArrayList<Item> arrayList = ret;
            return arrayList;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Integer> listIds() {
        this.lock.readLock().lock();
        try {
            ArrayList<Integer> ret = new ArrayList<Integer>();
            for (Item item : this.inventory.values()) {
                if (ret.contains(item.getItemId())) continue;
                ret.add(item.getItemId());
            }
            if (ret.size() > 1) {
                Collections.sort(ret);
            }
            ArrayList<Integer> arrayList = ret;
            return arrayList;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public short addItem(Item item) {
        this.lock.writeLock().lock();
        try {
            short slotId = this.getNextFreeSlot();
            if (slotId < 0) {
                short s = -1;
                return s;
            }
            this.inventory.put(slotId, item);
            item.setPosition(slotId);
            short s = slotId;
            return s;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void move(short sSlot, short dSlot, short slotMax) {
        this.lock.writeLock().lock();
        try {
            Item source = this.inventory.get(sSlot);
            Item target = this.inventory.get(dSlot);
            if (source == null) {
                throw new InventoryException("Trying to move empty slot");
            }
            if (target == null) {
                if (dSlot < 0 && !this.type.equals((Object)MapleInventoryType.EQUIPPED)) {
                    return;
                }
                if (dSlot > 0 && this.type.equals((Object)MapleInventoryType.EQUIPPED)) {
                    return;
                }
                source.setPosition(dSlot);
                this.inventory.put(dSlot, source);
                this.inventory.remove(sSlot);
            } else if (target.getItemId() == source.getItemId() && !ItemConstants.類型.可充值道具(source.getItemId()) && target.getOwner().equals(source.getOwner()) && target.getExpiration() == source.getExpiration() && target.getFamiliarCard() == null && source.getFamiliarCard() == null) {
                if (this.type.getType() == MapleInventoryType.EQUIP.getType() || this.type.getType() == MapleInventoryType.CASH.getType() || this.type.getType() == MapleInventoryType.DECORATION.getType()) {
                    this.swap(target, source);
                } else if (source.getQuantity() + target.getQuantity() > slotMax) {
                    source.setQuantity((short)(source.getQuantity() + target.getQuantity() - slotMax));
                    target.setQuantity(slotMax);
                } else {
                    target.setQuantity((short)(source.getQuantity() + target.getQuantity()));
                    this.inventory.remove(sSlot);
                }
            } else {
                this.swap(target, source);
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void swap(Item source, Item target) {
        this.lock.writeLock().lock();
        try {
            this.inventory.remove(source.getPosition());
            this.inventory.remove(target.getPosition());
            short swapPos = source.getPosition();
            source.setPosition(target.getPosition());
            target.setPosition(swapPos);
            this.inventory.put(source.getPosition(), source);
            this.inventory.put(target.getPosition(), target);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public void removeExtendedSlot(int slot) {
        this.lock.writeLock().lock();
        try {
            this.inventory.values().removeIf(item -> item.getPosition() / 10000 - 1 == slot);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public void removeAll() {
        this.lock.writeLock().lock();
        try {
            this.inventory.clear();
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Integer> listSkillSkinIds() {
        this.lock.readLock().lock();
        try {
            ArrayList<Integer> ret = new ArrayList<Integer>();
            for (Item item : this.inventory.values()) {
                if (!item.isSkillSkin() || ret.contains(item.getItemId())) continue;
                ret.add(item.getItemId());
            }
            ArrayList<Integer> arrayList = ret;
            return arrayList;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Item getArrowSlot(int level) {
        this.lock.readLock().lock();
        try {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            LinkedList<Item> list = new LinkedList<Item>();
            for (Item item : this.inventory.values()) {
                if (item.getPosition() > 128) continue;
                list.add(item);
            }
            list.sort(Comparator.naturalOrder());
            for (Item item : list) {
                if (!ItemConstants.類型.弓矢(item.getItemId()) || level < ii.getReqLevel(item.getItemId())) continue;
                Item item2 = item;
                return item2;
            }
            return null;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Item getCrossbowSlot(int level) {
        this.lock.readLock().lock();
        try {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            LinkedList<Item> list = new LinkedList<Item>();
            for (Item item : this.inventory.values()) {
                if (item.getPosition() > 128) continue;
                list.add(item);
            }
            list.sort(Comparator.naturalOrder());
            for (Item item : list) {
                if (!ItemConstants.類型.弩矢(item.getItemId()) || level < ii.getReqLevel(item.getItemId())) continue;
                Item item2 = item;
                return item2;
            }
            return null;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Item getBulletSlot(int level) {
        this.lock.readLock().lock();
        try {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            LinkedList<Item> list = new LinkedList<Item>();
            for (Item item : this.inventory.values()) {
                if (item.getPosition() > 128) continue;
                list.add(item);
            }
            list.sort(Comparator.naturalOrder());
            for (Item item : list) {
                if (!ItemConstants.類型.子彈(item.getItemId()) || level < ii.getReqLevel(item.getItemId())) continue;
                Item item2 = item;
                return item2;
            }
            return null;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Item getDartsSlot(int level) {
        this.lock.readLock().lock();
        try {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            LinkedList<Item> list = new LinkedList<Item>();
            for (Item item : this.inventory.values()) {
                if (item.getPosition() > 128) continue;
                list.add(item);
            }
            list.sort(Comparator.naturalOrder());
            for (Item item : list) {
                if ((!ItemConstants.類型.飛鏢(item.getItemId()) || level < ii.getReqLevel(item.getItemId())) && item.getItemId() / 1000 != 5021) continue;
                Item item2 = item;
                return item2;
            }
            return null;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public Iterator<Item> iterator() {
        return Collections.unmodifiableCollection(this.inventory.values()).iterator();
    }
}

