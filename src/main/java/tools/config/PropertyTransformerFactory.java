/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  tools.config.ClassUtils
 *  tools.config.transformers.CharTransformer
 *  tools.config.transformers.ClassTransformer
 *  tools.config.transformers.DoubleTransformer
 *  tools.config.transformers.EnumTransformer
 *  tools.config.transformers.FileTransformer
 *  tools.config.transformers.InetSocketAddressTransformer
 *  tools.config.transformers.PatternTransformer
 */
package tools.config;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;
import tools.config.ClassUtils;
import tools.config.TransformationException;
import tools.config.transformers.BooleanTransformer;
import tools.config.transformers.ByteTransformer;
import tools.config.transformers.CharTransformer;
import tools.config.transformers.ClassTransformer;
import tools.config.transformers.DoubleTransformer;
import tools.config.transformers.EnumTransformer;
import tools.config.transformers.FileTransformer;
import tools.config.transformers.FloatTransformer;
import tools.config.transformers.InetSocketAddressTransformer;
import tools.config.transformers.IntegerTransformer;
import tools.config.transformers.LongTransformer;
import tools.config.transformers.PatternTransformer;
import tools.config.transformers.PropertyTransformer;
import tools.config.transformers.ShortTransformer;
import tools.config.transformers.StringTransformer;

public class PropertyTransformerFactory {
    public static PropertyTransformer newTransformer(Class<?> class1, Class<? extends PropertyTransformer> tc) throws TransformationException {
        if (tc == PropertyTransformer.class) {
            tc = null;
        }
        if (tc != null) {
            try {
                return tc.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Exception e) {
                throw new TransformationException("Can't instantiate property transformer", e);
            }
        }
        if (class1 == Boolean.class || class1 == Boolean.TYPE) {
            return BooleanTransformer.SHARED_INSTANCE;
        }
        if (class1 == Byte.class || class1 == Byte.TYPE) {
            return ByteTransformer.SHARED_INSTANCE;
        }
        if (class1 == Character.class || class1 == Character.TYPE) {
            return CharTransformer.SHARED_INSTANCE;
        }
        if (class1 == Double.class || class1 == Double.TYPE) {
            return DoubleTransformer.SHARED_INSTANCE;
        }
        if (class1 == Float.class || class1 == Float.TYPE) {
            return FloatTransformer.SHARED_INSTANCE;
        }
        if (class1 == Integer.class || class1 == Integer.TYPE) {
            return IntegerTransformer.SHARED_INSTANCE;
        }
        if (class1 == Long.class || class1 == Long.TYPE) {
            return LongTransformer.SHARED_INSTANCE;
        }
        if (class1 == Short.class || class1 == Short.TYPE) {
            return ShortTransformer.SHARED_INSTANCE;
        }
        if (class1 == String.class) {
            return StringTransformer.SHARED_INSTANCE;
        }
        if (class1.isEnum()) {
            return EnumTransformer.SHARED_INSTANCE;
        }
        if (class1 == File.class) {
            return FileTransformer.SHARED_INSTANCE;
        }
        if (ClassUtils.isSubclass(class1, InetSocketAddress.class)) {
            return InetSocketAddressTransformer.SHARED_INSTANCE;
        }
        if (class1 == Pattern.class) {
            return PatternTransformer.SHARED_INSTANCE;
        }
        if (class1 == Class.class) {
            return ClassTransformer.SHARED_INSTANCE;
        }
        throw new TransformationException("Transformer not found for class " + class1.getName());
    }
}

