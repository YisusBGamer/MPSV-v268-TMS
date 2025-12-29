/*
 * Decompiled with CFR 0.152.
 */
package tools.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.config.Property;
import tools.config.PropertyTransformerFactory;
import tools.config.TransformationException;
import tools.config.transformers.PropertyTransformer;

public class ConfigurableProcessor {
    private static final Logger Logger = LoggerFactory.getLogger(ConfigurableProcessor.class);

    public static void process(Object object, Properties ... properties) {
        Class<?> clazz;
        if (object instanceof Class) {
            clazz = (Class<?>)object;
            object = null;
        } else {
            clazz = object.getClass();
        }
        ConfigurableProcessor.process(clazz, object, properties);
    }

    private static void process(Class<?> clazz, Object obj, Properties[] props) {
        Class<?> superClass;
        ConfigurableProcessor.processFields(clazz, obj, props);
        if (obj == null) {
            for (Class<?> itf : clazz.getInterfaces()) {
                ConfigurableProcessor.process(itf, obj, props);
            }
        }
        if ((superClass = clazz.getSuperclass()) != null && superClass != Object.class) {
            ConfigurableProcessor.process(superClass, obj, props);
        }
    }

    private static void processFields(Class<?> clazz, Object obj, Properties[] props) {
        for (Field f : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) && obj != null || !Modifier.isStatic(f.getModifiers()) && obj == null || !f.isAnnotationPresent(Property.class)) continue;
            if (Modifier.isFinal(f.getModifiers())) {
                Logger.error("Attempt to proceed final field " + f.getName() + " of class " + clazz.getName());
                throw new RuntimeException();
            }
            ConfigurableProcessor.processField(f, obj, props);
        }
    }

    private static void processField(Field f, Object obj, Properties[] props) {
        boolean oldAccessible = f.isAccessible();
        f.setAccessible(true);
        try {
            Property property = f.getAnnotation(Property.class);
            if (!"DO_NOT_OVERWRITE_INITIALIAZION_VALUE".equals(property.defaultValue()) || ConfigurableProcessor.isKeyPresent(property.key(), props)) {
                f.set(obj, ConfigurableProcessor.getFieldValue(f, props));
            } else if (Logger.isDebugEnabled()) {
                Logger.debug("Field " + f.getName() + " of class " + f.getDeclaringClass().getName() + " wasn't modified");
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Can't transform field " + f.getName() + " of class " + String.valueOf(f.getDeclaringClass()), e);
        }
        f.setAccessible(oldAccessible);
    }

    private static Object getFieldValue(Field field, Properties[] props) throws TransformationException {
        Property property = field.getAnnotation(Property.class);
        String defaultValue = property.defaultValue();
        String key = property.key();
        String value = null;
        if (key.isEmpty()) {
            Logger.warn("Property " + field.getName() + " of class " + field.getDeclaringClass().getName() + " has empty key");
        } else {
            value = ConfigurableProcessor.findPropertyByKey(key, props);
        }
        if (value == null || value.trim().equals("")) {
            value = defaultValue;
            if (Logger.isDebugEnabled()) {
                Logger.debug("Using default value for field " + field.getName() + " of class " + field.getDeclaringClass().getName());
            }
        }
        PropertyTransformer pt = PropertyTransformerFactory.newTransformer(field.getType(), property.propertyTransformer());
        return pt.transform(value, field);
    }

    private static String findPropertyByKey(String key, Properties[] props) {
        for (Properties p : props) {
            if (!p.containsKey(key)) continue;
            return p.getProperty(key);
        }
        return null;
    }

    private static boolean isKeyPresent(String key, Properties[] props) {
        return ConfigurableProcessor.findPropertyByKey(key, props) != null;
    }

    public static List<String> getKeys(Properties prop) {
        ArrayList<String> ret = new ArrayList<String>();
        for (Object o : prop.values()) {
            ret.add((String)o);
        }
        return ret;
    }
}

