/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Plugin.provider.wz.ImgMapleSound
 *  Plugin.provider.wz.PngMapleCanvas
 *  tools.wzlib.cryptography.Snow2CryptoTransform
 */
package Plugin.provider.wz;

import Plugin.provider.MapleData;
import Plugin.provider.MapleDataEntity;
import Plugin.provider.MapleDataProviderFactory;
import Plugin.provider.MapleDataType;
import Plugin.provider.wz.ImgMapleSound;
import Plugin.provider.wz.MsFileMapleData;
import Plugin.provider.wz.PngMapleCanvas;
import Plugin.provider.wz.WzIMGFile;
import Plugin.provider.wz.util.WzLittleEndianAccessor;
import java.awt.Point;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tools.data.BufferedRandomAccessFile;
import tools.data.RandomAccessByteStream;
import tools.wzlib.cryptography.CryptoInputStream;
import tools.wzlib.cryptography.Snow2CryptoTransform;
import tools.wzlib.utilities.PartialStream;

public class WzFileMapleData
implements MapleData {
    private final WzIMGFile file;
    private String name;
    private MapleDataType type;
    private List<MapleData> children = null;
    private Object data;
    private long entryOffset;
    private final String wzFile;
    private final String parent;
    private MapleData hotfixParent = null;

    public WzFileMapleData(WzIMGFile file, String wzFile, String parent, long entryOffset) {
        this.file = file;
        this.wzFile = wzFile;
        this.parent = parent;
        this.entryOffset = entryOffset;
    }

    public void setHotfixParent(MapleData dat) {
        this.hotfixParent = dat;
    }

    @Override
    public String getPath() {
        MapleDataEntity mde;
        MapleDataEntity ode = this;
        StringBuilder path = new StringBuilder(this.getName());
        while ((mde = ode.getParent()) != ode && mde != null) {
            ode = mde;
            if (mde.getName().isEmpty()) continue;
            path.insert(0, mde.getName() + "/");
        }
        return path.toString();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public MapleDataType getType() {
        return this.type;
    }

    @Override
    public List<MapleData> getChildren() {
        if (this.children == null) {
            Map<String, MapleData> hotfix;
            this.children = new ArrayList<MapleData>();
            LinkedList<String> skipChilds = new LinkedList<String>();
            LinkedList<MapleData> cret = new LinkedList<MapleData>();
            LinkedList<String> subcret = new LinkedList<String>();
            if (!(this.file != null && this.file.getRoot().getName().isEmpty() || (hotfix = MapleDataProviderFactory.getHotfixDatas()).isEmpty())) {
                String thisPath = this.getPath();
                for (Map.Entry<String, MapleData> entry : hotfix.entrySet()) {
                    if (entry.getKey().startsWith("-") && entry.getKey().substring(1).startsWith(thisPath)) {
                        if (entry.getKey().substring(1).equalsIgnoreCase(thisPath)) {
                            return this.children;
                        }
                        String hfPath = entry.getKey().substring(1);
                        if (hfPath.replace(thisPath, "").split("/").length != 2) continue;
                        skipChilds.add(hfPath.substring(hfPath.lastIndexOf("/") + 1));
                        continue;
                    }
                    if (!entry.getKey().startsWith(thisPath)) continue;
                    if (entry.getKey().equalsIgnoreCase(thisPath)) {
                        this.children = entry.getValue().getChildren();
                        for (MapleData child : this.children) {
                            if (!(child instanceof WzFileMapleData)) continue;
                            ((WzFileMapleData)child).setHotfixParent(this);
                        }
                        return this.children;
                    }
                    if (entry.getKey().replace(thisPath, "").split("/").length == 2) {
                        if (entry.getValue() instanceof WzFileMapleData) {
                            ((WzFileMapleData)entry.getValue()).setHotfixParent(this);
                        }
                        cret.add(entry.getValue());
                        skipChilds.add(entry.getKey().substring(entry.getKey().lastIndexOf("/") + 1));
                        continue;
                    }
                    String child = entry.getKey().replace(thisPath + "/", "");
                    subcret.add(child.substring(0, child.indexOf("/")));
                }
            }
            if (this.entryOffset != -1L && this.type == MapleDataType.EXTENDED) {
                try (BufferedRandomAccessFile raf = new BufferedRandomAccessFile(this.wzFile, "r");){
                    WzLittleEndianAccessor wlea;
                    if (this instanceof MsFileMapleData) {
                        MsFileMapleData msf = (MsFileMapleData)this;
                        long keyHash = 2166136261L;
                        String keySalt = msf.msData.Header.KeySalt;
                        for (char c : keySalt.toCharArray()) {
                            keyHash = (keyHash ^ (long)c) * 16777619L;
                        }
                        byte[] keyHashDigits = Long.toString(keyHash).getBytes(StandardCharsets.US_ASCII);
                        int i = 0;
                        while (i < keyHashDigits.length) {
                            int n = i++;
                            keyHashDigits[n] = (byte)(keyHashDigits[n] - 48);
                        }
                        byte[] imgKey = new byte[16];
                        String entryName = msf.msEntry.getName();
                        byte[] entryKey = msf.msEntry.getKey();
                        for (int i2 = 0; i2 < imgKey.length; ++i2) {
                            imgKey[i2] = (byte)(i2 + entryName.charAt(i2 % entryName.length()) * (keyHashDigits[i2 % keyHashDigits.length] % 2 + entryKey[(keyHashDigits[(i2 + 2) % keyHashDigits.length] + i2) % entryKey.length] + (keyHashDigits[(i2 + 1) % keyHashDigits.length] + i2) % 5));
                        }
                        PartialStream ps = new PartialStream(new FileInputStream(this.wzFile), msf.msEntry.getStartPos(), msf.msEntry.getSizeAligned(), true);
                        byte[] buffer = new byte[msf.msEntry.getSize()];
                        Snow2CryptoTransform snowCipher = new Snow2CryptoTransform(imgKey, null, false);
                        CryptoInputStream cs = new CryptoInputStream(ps, snowCipher);
                        CryptoInputStream cs2 = new CryptoInputStream(cs, snowCipher);
                        int dataLen = Math.min(buffer.length, 1024);
                        cs2.read(buffer, 0, dataLen);
                        if (buffer.length > 1024) {
                            cs.read(buffer, 1024, buffer.length - 1024);
                        }
                        wlea = new WzLittleEndianAccessor(buffer);
                    } else {
                        wlea = new WzLittleEndianAccessor(new RandomAccessByteStream(raf));
                    }
                    this.parseEntry(wlea, skipChilds, subcret, true);
                    this.finish();
                }
                catch (Exception e) {
                    e.fillInStackTrace();
                }
            }
            for (String sub : subcret) {
                WzFileMapleData dat;
                if (this instanceof MsFileMapleData) {
                    MsFileMapleData msd = (MsFileMapleData)this;
                    dat = new MsFileMapleData(this.file, this.wzFile, this.parent + (this.parent.isEmpty() ? "" : "/") + this.name, -1L, msd.msData, msd.msEntry);
                } else {
                    dat = new WzFileMapleData(this.file, this.wzFile, this.parent + (this.parent.isEmpty() ? "" : "/") + this.name, -1L);
                }
                dat.setType(MapleDataType.EXTENDED);
                dat.setName(sub);
                dat.setHotfixParent(this);
                this.children.add(dat);
            }
            this.children.addAll(cret);
        }
        return Collections.unmodifiableList(this.children);
    }

    @Override
    public MapleData getChildByPath(String path) {
        String[] segments = path.split("/");
        if (segments[0].equals("..")) {
            return ((MapleData)this.getParent()).getChildByPath(path.substring(path.indexOf("/") + 1));
        }
        MapleData ret = this;
        for (String segment : segments) {
            boolean foundChild = false;
            for (MapleData child : ret.getChildren()) {
                if (!child.getName().equals(segment)) continue;
                ret = child;
                foundChild = true;
                break;
            }
            if (foundChild) continue;
            return null;
        }
        return ret;
    }

    @Override
    public Object getData() {
        return this.data;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(MapleDataType type) {
        this.type = type;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void addChild(WzFileMapleData entry) {
        this.children.add(entry);
    }

    @Override
    public Iterator<MapleData> iterator() {
        return this.getChildren().iterator();
    }

    public String toString() {
        return this.getName() + ":" + String.valueOf(this.getData());
    }

    @Override
    public MapleDataEntity getParent() {
        if (this.hotfixParent != null) {
            return this.hotfixParent;
        }
        if (this.parent.equals(this.file.getRoot().getName())) {
            return this.file.getRoot();
        }
        if (!this.parent.isEmpty()) {
            String path = this.parent;
            if (this.parent.startsWith(this.file.getRoot().getName())) {
                path = path.replaceFirst(this.file.getRoot().getName() + "/", "");
            }
            return this.file.getRoot().getChildByPath(path);
        }
        return this.file.getParent();
    }

    private void parseEntry(WzLittleEndianAccessor wlea, List<String> skipChilds, List<String> checkChilds, boolean getChild) {
        String type;
        wlea.seek(this.entryOffset);
        switch (type = wlea.readStringBlock(this.file.getOffset())) {
            case "Property": {
                if (!getChild) {
                    return;
                }
                this.setType(MapleDataType.PROPERTY);
                wlea.readByte();
                wlea.readByte();
                int children = wlea.readCompressedInt();
                for (int i = 0; i < children; ++i) {
                    WzFileMapleData cEntry;
                    if (this instanceof MsFileMapleData) {
                        MsFileMapleData msd = (MsFileMapleData)this;
                        cEntry = new MsFileMapleData(this.file, this.wzFile, this.parent + (this.parent.isEmpty() ? "" : "/") + this.name, -1L, msd.msData, msd.msEntry);
                    } else {
                        cEntry = new WzFileMapleData(this.file, this.wzFile, this.parent + (this.parent.isEmpty() ? "" : "/") + this.name, wlea.getPosition());
                    }
                    cEntry.parseData(wlea, checkChilds);
                    if (skipChilds.contains(cEntry.name)) continue;
                    this.addChild(cEntry);
                }
                break;
            }
            case "Canvas": {
                if (!getChild) {
                    return;
                }
                this.setType(MapleDataType.CANVAS);
                wlea.readByte();
                byte marker = wlea.readByte();
                switch (marker) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        wlea.readByte();
                        wlea.readByte();
                        int children = wlea.readCompressedInt();
                        for (int i = 0; i < children; ++i) {
                            WzFileMapleData child;
                            if (this instanceof MsFileMapleData) {
                                MsFileMapleData msd = (MsFileMapleData)this;
                                child = new MsFileMapleData(this.file, this.wzFile, this.parent + (this.parent.isEmpty() ? "" : "/") + this.name, -1L, msd.msData, msd.msEntry);
                            } else {
                                child = new WzFileMapleData(this.file, this.wzFile, this.parent + (this.parent.isEmpty() ? "" : "/") + this.name, wlea.getPosition());
                            }
                            child.parseData(wlea, checkChilds);
                            if (skipChilds.contains(child.name)) continue;
                            this.addChild(child);
                        }
                        break;
                    }
                    default: {
                        System.out.println("Canvas marker != 1 (" + marker + ")");
                    }
                }
                int width = wlea.readCompressedInt();
                int height = wlea.readCompressedInt();
                int format = wlea.readCompressedInt();
                byte format2 = wlea.readByte();
                wlea.readInt();
                int dataLength = wlea.readInt() - 1;
                wlea.readByte();
                this.setData(new PngMapleCanvas(width, height, dataLength, format + format2, null));
                wlea.skip(dataLength);
                break;
            }
            case "Shape2D#Vector2D": {
                this.setType(MapleDataType.VECTOR);
                int x = wlea.readCompressedInt();
                int y = wlea.readCompressedInt();
                this.setData(new Point(x, y));
                break;
            }
            case "Shape2D#Convex2D": {
                if (!getChild) {
                    return;
                }
                int children = wlea.readCompressedInt();
                for (int i = 0; i < children; ++i) {
                    WzFileMapleData cEntry;
                    if (this instanceof MsFileMapleData) {
                        MsFileMapleData msd = (MsFileMapleData)this;
                        cEntry = new MsFileMapleData(this.file, this.wzFile, this.parent + (this.parent.isEmpty() ? "" : "/") + this.name, -1L, msd.msData, msd.msEntry);
                    } else {
                        cEntry = new WzFileMapleData(this.file, this.wzFile, this.parent + (this.parent.isEmpty() ? "" : "/") + this.name, wlea.getPosition());
                    }
                    cEntry.parseEntry(wlea, new LinkedList<String>(), new LinkedList<String>(), false);
                    if (skipChilds.contains(cEntry.name)) continue;
                    this.addChild(cEntry);
                }
                break;
            }
            case "Sound_DX8": {
                this.setType(MapleDataType.SOUND);
                wlea.readByte();
                int dataLength = wlea.readCompressedInt();
                wlea.readCompressedInt();
                int offset = (int)wlea.getPosition();
                this.setData(new ImgMapleSound(dataLength, (long)offset - this.file.getOffset()));
                break;
            }
            case "UOL": {
                this.setType(MapleDataType.UOL);
                wlea.readByte();
                this.setData(wlea.readStringBlock(this.file.getOffset()));
                break;
            }
            default: {
                throw new RuntimeException("Unhandeled extended type: " + type);
            }
        }
    }

    private void parseData(WzLittleEndianAccessor wlea, List<String> checkChilds) {
        this.setName(wlea.readStringBlock(this.file.getOffset()));
        checkChilds.remove(this.name);
        byte type = wlea.readByte();
        switch (type) {
            case 0: {
                this.setType(MapleDataType.IMG_0x00);
                break;
            }
            case 2: 
            case 11: {
                this.setType(MapleDataType.SHORT);
                this.setData(wlea.readShort());
                break;
            }
            case 3: 
            case 19: {
                this.setType(MapleDataType.INT);
                this.setData(wlea.readCompressedInt());
                break;
            }
            case 20: {
                this.setType(MapleDataType.LONG);
                this.setData(wlea.readLongValue());
                break;
            }
            case 4: {
                this.setType(MapleDataType.FLOAT);
                this.setData(Float.valueOf(wlea.readFloatValue()));
                break;
            }
            case 5: {
                this.setType(MapleDataType.DOUBLE);
                this.setData(wlea.readDouble());
                break;
            }
            case 8: {
                this.setType(MapleDataType.STRING);
                this.setData(wlea.readStringBlock(this.file.getOffset()));
                break;
            }
            case 9: {
                this.setType(MapleDataType.EXTENDED);
                long endOfExtendedBlock = wlea.readInt();
                this.entryOffset = wlea.getPosition();
                this.parseEntry(wlea, new LinkedList<String>(), new LinkedList<String>(), false);
                wlea.seek(endOfExtendedBlock += wlea.getPosition());
                break;
            }
            default: {
                System.out.println("Unknown Image type " + type);
            }
        }
    }

    private void finish() {
        ((ArrayList)this.children).trimToSize();
    }
}

