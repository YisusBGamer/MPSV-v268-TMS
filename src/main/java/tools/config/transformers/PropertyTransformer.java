/*
 * Decompiled with CFR 0.152.
 */
package tools.config.transformers;

import java.lang.reflect.Field;
import tools.config.TransformationException;

public interface PropertyTransformer<T> {
    public T transform(String var1, Field var2) throws TransformationException;
}

