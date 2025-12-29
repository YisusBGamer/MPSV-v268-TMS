/*
 * Decompiled with CFR 0.152.
 */
package tools.config.transformers;

import java.lang.reflect.Field;
import tools.config.TransformationException;
import tools.config.transformers.PropertyTransformer;

public class IntegerTransformer
implements PropertyTransformer<Integer> {
    public static final IntegerTransformer SHARED_INSTANCE = new IntegerTransformer();

    @Override
    public Integer transform(String value, Field field) throws TransformationException {
        try {
            return Integer.decode(value);
        }
        catch (Exception e) {
            throw new TransformationException(e);
        }
    }
}

