/*
 * Decompiled with CFR 0.152.
 */
package tools.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tools.config.transformers.PropertyTransformer;

@Documented
@Target(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Property {
    public static final String DEFAULT_VALUE = "DO_NOT_OVERWRITE_INITIALIAZION_VALUE";

    public String key();

    public Class<? extends PropertyTransformer> propertyTransformer() default PropertyTransformer.class;

    public String defaultValue() default "DO_NOT_OVERWRITE_INITIALIAZION_VALUE";
}

