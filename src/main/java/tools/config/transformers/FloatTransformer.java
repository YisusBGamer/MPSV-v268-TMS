/*
 * Decompiled with CFR 0.152.
 */
package tools.config.transformers;

import java.lang.reflect.Field;
import tools.config.TransformationException;
import tools.config.transformers.PropertyTransformer;

public class FloatTransformer
implements PropertyTransformer<Float> {
    public static final FloatTransformer SHARED_INSTANCE = new FloatTransformer();

    @Override
    public Float transform(String value, Field field) throws TransformationException {
        try {
            return Float.valueOf(Float.parseFloat(value));
        }
        catch (Exception e) {
            throw new TransformationException(e);
        }
    }
}

