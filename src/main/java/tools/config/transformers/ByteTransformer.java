/*
 * Decompiled with CFR 0.152.
 */
package tools.config.transformers;

import java.lang.reflect.Field;
import tools.config.TransformationException;
import tools.config.transformers.PropertyTransformer;

public class ByteTransformer
implements PropertyTransformer<Byte> {
    public static final ByteTransformer SHARED_INSTANCE = new ByteTransformer();

    @Override
    public Byte transform(String value, Field field) throws TransformationException {
        try {
            return Byte.decode(value);
        }
        catch (Exception e) {
            throw new TransformationException(e);
        }
    }
}

