/*
 * Decompiled with CFR 0.152.
 */
package tools.config.transformers;

import java.lang.reflect.Field;
import tools.config.TransformationException;
import tools.config.transformers.PropertyTransformer;

public class StringTransformer
implements PropertyTransformer<String> {
    public static final StringTransformer SHARED_INSTANCE = new StringTransformer();

    @Override
    public String transform(String value, Field field) throws TransformationException {
        return value;
    }
}

