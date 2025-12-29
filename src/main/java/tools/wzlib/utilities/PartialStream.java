/*
 * Decompiled with CFR 0.152.
 */
package tools.wzlib.utilities;

import java.io.IOException;
import java.io.InputStream;

public class PartialStream
extends InputStream {
    private final InputStream baseStream;
    private final long offset;
    private final long length;
    private final boolean leaveOpen;
    private long position;

    public PartialStream(InputStream baseStream, long offset, long length, boolean leaveOpen) {
        if (baseStream == null) {
            throw new IllegalArgumentException("BaseStream cannot be null.");
        }
        if (offset < 0L) {
            throw new IllegalArgumentException("Offset cannot be negative.");
        }
        if (length < 0L) {
            throw new IllegalArgumentException("Length cannot be negative.");
        }
        this.baseStream = baseStream;
        this.offset = offset;
        this.length = length;
        this.leaveOpen = leaveOpen;
        this.position = 0L;
        try {
            baseStream.skip(offset);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to seek to offset.", e);
        }
    }

    public PartialStream(InputStream baseStream, long offset, long length) {
        this(baseStream, offset, length, false);
    }

    public InputStream getBaseStream() {
        return this.baseStream;
    }

    @Override
    public int read() throws IOException {
        if (this.position >= this.length) {
            return -1;
        }
        int result = this.baseStream.read();
        if (result != -1) {
            ++this.position;
        }
        return result;
    }

    @Override
    public int read(byte[] buffer, int off, int len) throws IOException {
        if (this.position >= this.length) {
            return -1;
        }
        long remaining = this.length - this.position;
        int bytesToRead = (int)Math.min((long)len, remaining);
        int bytesRead = this.baseStream.read(buffer, off, bytesToRead);
        if (bytesRead > 0) {
            this.position += (long)bytesRead;
        }
        return bytesRead;
    }

    @Override
    public long skip(long n) throws IOException {
        long remaining = this.length - this.position;
        long bytesToSkip = Math.min(n, remaining);
        long bytesSkipped = this.baseStream.skip(bytesToSkip);
        this.position += bytesSkipped;
        return bytesSkipped;
    }

    @Override
    public int available() throws IOException {
        return (int)Math.min((long)this.baseStream.available(), this.length - this.position);
    }

    @Override
    public void close() throws IOException {
        if (!this.leaveOpen) {
            this.baseStream.close();
        }
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.baseStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        this.baseStream.reset();
        this.position = 0L;
    }

    @Override
    public boolean markSupported() {
        return this.baseStream.markSupported();
    }

    public long getOffset() {
        return this.offset;
    }

    public long getLength() {
        return this.length;
    }

    public long getPosition() {
        return this.position;
    }
}

