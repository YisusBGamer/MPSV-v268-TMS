/*
 * Decompiled with CFR 0.152.
 */
package Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import tools.HexTool;
import tools.data.WritableIntValueHolder;

public class ExternalCodeTableGetter {
    final Properties props;

    public ExternalCodeTableGetter(Properties properties) {
        this.props = properties;
    }

    private static <T extends Enum<? extends WritableIntValueHolder>> T valueOf(String name, T[] values) {
        for (T val : values) {
            if (!((Enum)val).name().equals(name)) continue;
            return val;
        }
        return null;
    }

    public static <T extends Enum<? extends WritableIntValueHolder>> String getOpcodeTable(T[] enumeration) {
        StringBuilder enumVals = new StringBuilder();
        ArrayList<T> all = new ArrayList<T>(Arrays.asList(enumeration));
        all.sort((o1, o2) -> Short.compare(((WritableIntValueHolder)((Object)o1)).getValue(), ((WritableIntValueHolder)((Object)o2)).getValue()));
        for (Enum code : all) {
            enumVals.append(code.name());
            enumVals.append(" = ");
            enumVals.append("0x");
            enumVals.append(HexTool.toString(((WritableIntValueHolder)((Object)code)).getValue()));
            enumVals.append(" (");
            enumVals.append(((WritableIntValueHolder)((Object)code)).getValue());
            enumVals.append(")\n");
        }
        return enumVals.toString();
    }

    public static <T extends Enum<? extends WritableIntValueHolder>> void populateValues(Properties properties, T[] values) {
        ExternalCodeTableGetter exc = new ExternalCodeTableGetter(properties);
        for (T code : values) {
            short value = exc.getValue(((Enum)code).name(), (Enum[])values, (short)-2);
            if (value == -2) continue;
            ((WritableIntValueHolder)code).setValue(value);
        }
    }

    private <T extends Enum<? extends WritableIntValueHolder>> short getValue(String name, T[] values, short def) {
        String prop = this.props.getProperty(name);
        if (prop != null && !prop.isEmpty()) {
            String offset;
            String trimmed = prop.trim();
            String[] args = trimmed.split(" ");
            short base = 0;
            if (args.length == 2) {
                base = ((WritableIntValueHolder)((Object)Objects.requireNonNull(ExternalCodeTableGetter.valueOf((String)args[0], values)))).getValue();
                if (base == def) {
                    base = this.getValue(args[0], (Enum[])values, def);
                }
                offset = args[1];
            } else {
                offset = args[0];
            }
            if (offset.length() > 2 && offset.startsWith("0x")) {
                return (short)(Short.parseShort(offset.substring(2), 16) + base);
            }
            return (short)(Short.parseShort(offset) + base);
        }
        return def;
    }
}

