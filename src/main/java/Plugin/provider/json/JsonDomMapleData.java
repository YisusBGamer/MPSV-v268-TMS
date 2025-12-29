/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package Plugin.provider.json;

import Plugin.provider.MapleData;
import Plugin.provider.MapleDataEntity;
import Plugin.provider.MapleDataType;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Generated;
import org.json.JSONObject;

public class JsonDomMapleData
implements MapleData {
    private JSONObject node;
    private MapleDataEntity parent;
    private String key;
    private String imageDataDir;

    private JsonDomMapleData(JSONObject node, MapleDataEntity parent, String file, String key) {
        this.node = node;
        this.parent = parent;
        this.imageDataDir = file;
        this.key = key;
    }

    public JsonDomMapleData(FileInputStream fis, File imageDataDir) {
        try {
            this.node = new JSONObject(new String(fis.readAllBytes()));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.imageDataDir = imageDataDir.getName();
    }

    @Override
    public MapleData getChildByPath(String path) {
        String[] keys = path.split("/");
        JsonDomMapleData current = this;
        for (String k : keys) {
            if (current.node.has(k)) {
                Object obj = current.node.get(k);
                if (obj instanceof JSONObject) {
                    if ((current = new JsonDomMapleData((JSONObject)obj, current, this.imageDataDir + "/" + k, k)).getType() == MapleDataType.PROPERTY) continue;
                    return current;
                }
                return null;
            }
            return null;
        }
        return current;
    }

    @Override
    public List<MapleData> getChildren() {
        ArrayList<MapleData> ret = new ArrayList<MapleData>();
        for (String i : this.node.keySet()) {
            if (!(this.node.get(i) instanceof JSONObject)) continue;
            ret.add(new JsonDomMapleData((JSONObject)this.node.get(i), this, this.imageDataDir + "/" + i, i));
        }
        return ret;
    }

    @Override
    public Object getData() {
        MapleDataType type = this.getType();
        Object data = null;
        switch (type) {
            case PROPERTY: {
                return null;
            }
            case DOUBLE: {
                data = Double.parseDouble(this.node.getString("_value"));
                break;
            }
            case FLOAT: {
                data = Float.valueOf(Float.parseFloat(this.node.getString("_value")));
                break;
            }
            case INT: {
                try {
                    data = Integer.parseInt(this.node.getString("_value"));
                }
                catch (Exception e) {
                    data = Long.parseLong(this.node.getString("_value"));
                }
                break;
            }
            case LONG: {
                data = Long.parseLong(this.node.getString("_value"));
                break;
            }
            case SHORT: {
                data = Short.parseShort(this.node.getString("_value"));
                break;
            }
            case VECTOR: {
                data = new Point(this.node.getInt("_x"), this.node.getInt("_y"));
                break;
            }
            case SOUND: 
            case CANVAS: {
                break;
            }
            default: {
                try {
                    data = this.node.getString("_value");
                    break;
                }
                catch (Exception e) {
                    System.out.println("JSON err");
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    @Override
    public MapleDataType getType() {
        String nodeName;
        if (!this.node.has("_dirType")) {
            return MapleDataType.PROPERTY;
        }
        switch (nodeName = this.node.getString("_dirType")) {
            case "sub": {
                return MapleDataType.PROPERTY;
            }
            case "canvas": {
                return MapleDataType.CANVAS;
            }
            case "convex": {
                return MapleDataType.CONVEX;
            }
            case "sound": {
                return MapleDataType.SOUND;
            }
            case "uol": {
                return MapleDataType.UOL;
            }
            case "double": {
                return MapleDataType.DOUBLE;
            }
            case "float": {
                return MapleDataType.FLOAT;
            }
            case "long": {
                return MapleDataType.LONG;
            }
            case "int": {
                return MapleDataType.INT;
            }
            case "short": {
                return MapleDataType.SHORT;
            }
            case "string": {
                return MapleDataType.STRING;
            }
            case "vector": {
                return MapleDataType.VECTOR;
            }
            case "null": {
                return MapleDataType.IMG_0x00;
            }
        }
        return null;
    }

    @Override
    public MapleDataEntity getParent() {
        return this.parent;
    }

    @Override
    public String getPath() {
        return this.imageDataDir.replaceAll("\\\\", "/") + "/" + this.getName();
    }

    @Override
    public String getName() {
        return this.key;
    }

    @Override
    public Iterator<MapleData> iterator() {
        return this.getChildren().iterator();
    }

    @Generated
    public JSONObject getNode() {
        return this.node;
    }
}

