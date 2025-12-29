/*
 * Decompiled with CFR 0.152.
 */
package tools.config.transformers;

import java.lang.reflect.Field;
import tools.config.TransformationException;
import tools.config.transformers.PropertyTransformer;

public class ShortTransformer
implements PropertyTransformer<Short> {
    public static final ShortTransformer SHARED_INSTANCE = new ShortTransformer();

    @Override
    public Short transform(String value, Field field) throws TransformationException {
        try {
            return Short.decode(value);
        }
        catch (Exception e) {
            throw new TransformationException(e);
        }
    }
}

