/*
 * Decompiled with CFR 0.152.
 */
package tools.data;

import java.io.IOException;

public interface ByteStream {
    public long getPosition();

    public void seek(long var1) throws IOException;

    public long getBytesRead();

    public int readByte();

    public String toString();

    public String toString(boolean var1);

    public long available();
}

