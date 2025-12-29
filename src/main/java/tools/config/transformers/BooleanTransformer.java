/*
 * Decompiled with CFR 0.152.
 */
package tools.config.transformers;

import java.lang.reflect.Field;
import tools.config.TransformationException;
import tools.config.transformers.PropertyTransformer;

public class BooleanTransformer
implements PropertyTransformer<Boolean> {
    public static final BooleanTransformer SHARED_INSTANCE = new BooleanTransformer();

    @Override
    public Boolean transform(String value, Field field) throws TransformationException {
        if ("true".equalsIgnoreCase(value) || "1".equals(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value) || "0".equals(value)) {
            return false;
        }
        throw new TransformationException("Invalid boolean string: " + value);
    }
}

