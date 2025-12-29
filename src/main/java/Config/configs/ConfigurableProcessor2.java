/*
 * Decompiled with CFR 0.152.
 */
package Config.configs;

import Config.configs.Config;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.config.ConfigurableProcessor;
import tools.config.Property;
import tools.config.PropertyTransformerFactory;
import tools.config.TransformationException;
import tools.config.transformers.PropertyTransformer;

public class ConfigurableProcessor2
extends ConfigurableProcessor {
    private static final Logger log = LoggerFactory.getLogger(ConfigurableProcessor2.class);

    public static void process(Object object, Properties ... properties) {
        Class<?> clazz;
        if (object instanceof Class) {
            clazz = (Class<?>)object;
            object = null;
        } else {
            clazz = object.getClass();
        }
        ConfigurableProcessor2.process(clazz, object, properties);
    }

    private static void process(Class<?> clazz, Object obj, Properties[] props) {
        Class<?> superClass;
        ConfigurableProcessor2.processFields(clazz, obj, props);
        if (obj == null) {
            for (Class<?> itf : clazz.getInterfaces()) {
                ConfigurableProcessor2.process(itf, obj, props);
            }
        }
        if ((superClass = clazz.getSuperclass()) != null && superClass != Object.class) {
            ConfigurableProcessor2.process(superClass, obj, props);
        }
    }

    private static void processFields(Class<?> clazz, Object obj, Properties[] props) {
        for (Field f : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) && obj != null || !Modifier.isStatic(f.getModifiers()) && obj == null || !f.isAnnotationPresent(Property.class)) continue;
            if (Modifier.isFinal(f.getModifiers())) {
                log.error("Attempt to proceed final field " + f.getName() + " of class " + clazz.getName());
                throw new RuntimeException();
            }
            ConfigurableProcessor2.processField(f, obj, props);
        }
    }

    private static void processField(Field f, Object obj, Properties[] props) {
        boolean oldAccessible = f.isAccessible();
        f.setAccessible(true);
        try {
            Property property = f.getAnnotation(Property.class);
            if ("DO_NOT_OVERWRITE_INITIALIAZION_VALUE".equals(property.defaultValue()) && !ConfigurableProcessor2.isKeyPresent(property.key(), props)) {
                if (log.isDebugEnabled()) {
                    log.debug("Field " + f.getName() + " of class " + f.getDeclaringClass().getName() + " wasn't modified");
                }
            } else {
                f.set(obj, ConfigurableProcessor2.getFieldValue(f, props));
            }
        }
        catch (Exception var5) {
            throw new RuntimeException("Can't transform field " + f.getName() + " of class " + String.valueOf(f.getDeclaringClass()), var5);
        }
        f.setAccessible(oldAccessible);
    }

    private static Object getFieldValue(Field field, Properties[] props) throws TransformationException {
        Property property = field.getAnnotation(Property.class);
        String defaultValue = property.defaultValue();
        String key = property.key();
        String value = null;
        if (key.isEmpty()) {
            log.warn("Property " + field.getName() + " of class " + field.getDeclaringClass().getName() + " has empty key");
        } else {
            value = ConfigurableProcessor2.findPropertyByKey(key, props);
        }
        if (value == null || value.trim().equals("")) {
            value = defaultValue;
            Config.setProperty(key, defaultValue);
            if (log.isDebugEnabled()) {
                log.debug("Using default value for field " + field.getName() + " of class " + field.getDeclaringClass().getName());
            }
        } else if (value.indexOf(123) != -1) {
            value = ConfigurableProcessor2.interpolate(value, props);
        }
        PropertyTransformer pt = PropertyTransformerFactory.newTransformer(field.getType(), property.propertyTransformer());
        return pt.transform(value, field);
    }

    private static String findPropertyByKey(String key, Properties[] props) {
        Properties[] var2 = props;
        int var3 = props.length;
        for (int var4 = 0; var4 < var3; ++var4) {
            Properties p = var2[var4];
            if (!p.containsKey(key)) continue;
            return p.getProperty(key);
        }
        return null;
    }

    private static boolean isKeyPresent(String key, Properties[] props) {
        return ConfigurableProcessor2.findPropertyByKey(key, props) != null;
    }

    private static String interpolate(String template, Properties[] props) {
        StringBuilder result = new StringBuilder();
        StringBuilder keyBuilder = new StringBuilder();
        boolean inPlaceholder = false;
        for (int i = 0; i < template.length(); ++i) {
            char ch = template.charAt(i);
            if (ch == '{') {
                if (i + 1 < template.length() && template.charAt(i + 1) == '{') {
                    result.append('{');
                    ++i;
                    continue;
                }
                inPlaceholder = true;
                keyBuilder.setLength(0);
                continue;
            }
            if (ch == '}') {
                if (i + 1 < template.length() && template.charAt(i + 1) == '}') {
                    result.append('}');
                    ++i;
                    continue;
                }
                if (inPlaceholder) {
                    String key = keyBuilder.toString();
                    String value = ConfigurableProcessor2.findPropertyByKey(key, props);
                    if (value != null) {
                        result.append(value);
                    } else {
                        result.append("{").append(key).append("}");
                    }
                    inPlaceholder = false;
                    continue;
                }
                result.append('}');
                continue;
            }
            if (inPlaceholder) {
                keyBuilder.append(ch);
                continue;
            }
            result.append(ch);
        }
        return result.toString();
    }
}

