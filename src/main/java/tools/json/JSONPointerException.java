/*
 * Decompiled with CFR 0.152.
 */
package tools.json;

import tools.json.JSONException;

final class JSONPointerException
extends JSONException {
    private static final long serialVersionUID = 8872944667561856751L;

    public JSONPointerException(String message) {
        super(message);
    }

    public JSONPointerException(String message, Throwable cause) {
        super(message, cause);
    }
}

