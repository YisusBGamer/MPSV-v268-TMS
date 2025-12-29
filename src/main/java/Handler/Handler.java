/*
 * Decompiled with CFR 0.152.
 */
package Handler;

import Opcode.header.InHeader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface Handler {
    public InHeader op() default InHeader.UNKNOWN;

    public InHeader[] ops() default {};
}

