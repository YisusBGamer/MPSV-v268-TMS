/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Plugin.provider.MapleCanvas
 */
package Plugin.provider;

import Plugin.provider.MapleCanvas;
import Plugin.provider.MapleData;
import Plugin.provider.MapleDataEntity;
import Plugin.provider.MapleDataType;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import tools.StringUtil;

public final class MapleDataTool {
    public static String getString(MapleData data) {
        return data == null ? null : (String)data.getData();
    }

    public static String getString(MapleData data, String def) {
        if (data == null || data.getData() == null) {
            return def;
        }
        if (data.getType() == MapleDataType.STRING || data.getData() instanceof String) {
            String ret = String.valueOf(data.getData());
            ret = ret.replace("&lt;", "<").replace("&amp;lt;", "<").replace("&gt;", ">").replace("黎", "黎");
            return ret;
        }
        return String.valueOf(MapleDataTool.getInt(data));
    }

    public static String getString(String path, MapleData data) {
        return MapleDataTool.getString(data.getChildByPath(path));
    }

    public static String getString(String path, MapleData data, String def) {
        return MapleDataTool.getString(data == null || data.getChildByPath(path) == null ? null : data.getChildByPath(path), def);
    }

    public static double getDouble(MapleData data) {
        return (Double)data.getData();
    }

    public static float getFloat(MapleData data) {
        return ((Float)data.getData()).floatValue();
    }

    public static float getFloat(MapleData data, float def) {
        if (data == null || data.getData() == null) {
            return def;
        }
        return ((Float)data.getData()).floatValue();
    }

    public static int getInt(MapleData data) {
        return Integer.valueOf(data.getData().toString());
    }

    public static int getInt(MapleData data, int def) {
        if (data == null || data.getData() == null) {
            return def;
        }
        if (data.getType() == MapleDataType.STRING) {
            String data_ = MapleDataTool.getString(data);
            if (data_.isEmpty()) {
                data_ = "0";
            }
            return Integer.parseInt(data_);
        }
        if (data.getType() == MapleDataType.SHORT) {
            return ((Short)data.getData()).shortValue();
        }
        return (Integer)data.getData();
    }

    public static int getInt(String path, MapleData data) {
        return MapleDataTool.getInt(data.getChildByPath(path));
    }

    public static int getIntConvert(MapleData data) {
        if (data.getType() == MapleDataType.STRING) {
            return Integer.parseInt(MapleDataTool.getString(data));
        }
        return MapleDataTool.getInt(data);
    }

    public static int getIntConvert(String path, MapleData data) {
        MapleData d = data.getChildByPath(path);
        if (d.getType() == MapleDataType.STRING) {
            return Integer.parseInt(MapleDataTool.getString(d));
        }
        return MapleDataTool.getInt(d);
    }

    public static int getInt(String path, MapleData data, int def) {
        if (data == null) {
            return def;
        }
        return MapleDataTool.getInt(data.getChildByPath(path), def);
    }

    public static int getIntConvert(String path, MapleData data, int def) {
        if (data == null) {
            return def;
        }
        return MapleDataTool.getIntConvert(data.getChildByPath(path), def);
    }

    public static int getIntConvert(MapleData d, int def) {
        if (d == null) {
            return def;
        }
        if (d.getType() == MapleDataType.STRING) {
            String dd = MapleDataTool.getString(d);
            if (dd.endsWith("%")) {
                dd = dd.substring(0, dd.length() - 1);
            }
            try {
                return Integer.parseInt(dd);
            }
            catch (NumberFormatException nfe) {
                return def;
            }
        }
        return MapleDataTool.getInt(d, def);
    }

    public static long getLong(MapleData data) {
        return Long.valueOf(data.getData().toString());
    }

    public static long getLong(MapleData data, long def) {
        if (data == null || data.getData() == null) {
            return def;
        }
        if (data.getType() == MapleDataType.STRING) {
            String data_ = MapleDataTool.getString(data);
            if (data_.isEmpty()) {
                data_ = "0";
            }
            return Long.parseLong(data_);
        }
        return ((Number)data.getData()).longValue();
    }

    public static long getLong(String path, MapleData data) {
        return MapleDataTool.getLong(data.getChildByPath(path));
    }

    public static long getLongConvert(MapleData data) {
        if (data.getType() == MapleDataType.STRING) {
            return Long.parseLong(MapleDataTool.getString(data));
        }
        return MapleDataTool.getLong(data);
    }

    public static long getLongConvert(String path, MapleData data) {
        MapleData d = data.getChildByPath(path);
        if (d.getType() == MapleDataType.STRING) {
            return Long.parseLong(MapleDataTool.getString(d));
        }
        return MapleDataTool.getLong(d);
    }

    public static long getLong(String path, MapleData data, long def) {
        if (data == null) {
            return def;
        }
        return MapleDataTool.getLong(data.getChildByPath(path), def);
    }

    public static long getLongConvert(String path, MapleData data, int def) {
        if (data == null) {
            return def;
        }
        return MapleDataTool.getLongConvert(data.getChildByPath(path), def);
    }

    public static long getLongConvert(MapleData d, int def) {
        if (d == null) {
            return def;
        }
        if (d.getType() == MapleDataType.STRING) {
            String dd = MapleDataTool.getString(d);
            if (dd.endsWith("%")) {
                dd = dd.substring(0, dd.length() - 1);
            }
            try {
                return Long.parseLong(dd);
            }
            catch (NumberFormatException nfe) {
                return def;
            }
        }
        return MapleDataTool.getLong(d, def);
    }

    public static BufferedImage getImage(MapleData data) {
        return ((MapleCanvas)data.getData()).getImage();
    }

    public static Point getPoint(MapleData data) {
        return (Point)data.getData();
    }

    public static Point getPoint(String path, MapleData data) {
        return MapleDataTool.getPoint(data.getChildByPath(path));
    }

    public static Point getPoint(String path, MapleData data, Point def) {
        MapleData pointData = data.getChildByPath(path);
        if (pointData == null) {
            return def;
        }
        return MapleDataTool.getPoint(pointData);
    }

    public static String getFullDataPath(MapleData data) {
        Object path = "";
        for (MapleDataEntity myData = data; myData != null; myData = myData.getParent()) {
            path = myData.getName() + "/" + (String)path;
        }
        return ((String)path).substring(0, ((String)path).length() - 1);
    }

    public static Map<Object, Object> getAllMapleData(MapleData data) {
        HashMap<Object, Object> ret = new HashMap<Object, Object>();
        block7: for (MapleData subdata : data) {
            switch (subdata.getName()) {
                case "icon": 
                case "iconRaw": {
                    MapleDataTool.processIconData(ret, subdata);
                    continue block7;
                }
            }
            ret.put(subdata.getName(), subdata.getChildren().isEmpty() ? subdata.getData() : MapleDataTool.getAllMapleData(subdata));
        }
        return ret;
    }

    private static void processIconData(Map<Object, Object> ret, MapleData subdata) {
        for (MapleData subdatum : subdata) {
            int inlink;
            boolean isHash = subdatum.getName().equals("_hash");
            boolean isInLink = subdatum.getName().equals("_inlink");
            boolean isOutLink = subdatum.getName().equals("_outlink");
            if (isHash) {
                ret.put(subdatum.getName(), String.valueOf(subdatum.getData()));
                continue;
            }
            if (!isInLink && !isOutLink || (inlink = MapleDataTool.extractInLink(subdatum.getData().toString(), isInLink)) == 0) continue;
            ret.put(subdatum.getName(), inlink);
        }
    }

    private static int extractInLink(String data, boolean isInLink) {
        int inlink = 0;
        String[] split = data.replace(".img", "").split("/");
        for (int i = 0; i < split.length; ++i) {
            if ((!isInLink || i != 0) && (isInLink || i != 2 && i != 3) || !StringUtil.isNumber(split[i])) continue;
            inlink = Integer.parseInt(split[i]);
        }
        return inlink;
    }
}

