/*
 * Decompiled with CFR 0.152.
 */
package tools.config.transformers;

import java.lang.reflect.Field;
import tools.config.TransformationException;
import tools.config.transformers.PropertyTransformer;

public class LongTransformer
implements PropertyTransformer<Long> {
    public static final LongTransformer SHARED_INSTANCE = new LongTransformer();

    @Override
    public Long transform(String value, Field field) throws TransformationException {
        try {
            return Long.decode(value);
        }
        catch (Exception e) {
            throw new TransformationException(e);
        }
    }
}

