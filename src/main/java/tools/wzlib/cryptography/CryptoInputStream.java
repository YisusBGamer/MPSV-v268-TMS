/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  tools.wzlib.cryptography.Snow2CryptoTransform
 */
package tools.wzlib.cryptography;

import java.io.IOException;
import java.io.InputStream;
import tools.wzlib.cryptography.Snow2CryptoTransform;

public class CryptoInputStream
extends InputStream {
    private InputStream inputStream;
    private Snow2CryptoTransform cryptoTransform;
    private byte[] buffer = new byte[1024];
    private int bufferPos = 0;
    private int bufferLen = 0;

    public CryptoInputStream(InputStream inputStream, Snow2CryptoTransform cryptoTransform) {
        this.inputStream = inputStream;
        this.cryptoTransform = cryptoTransform;
    }

    @Override
    public int read() throws IOException {
        if (this.bufferPos >= this.bufferLen) {
            this.bufferLen = this.inputStream.read(this.buffer);
            if (this.bufferLen <= 0) {
                return -1;
            }
            byte[] decrypted = new byte[this.bufferLen];
            this.cryptoTransform.TransformBlock(this.buffer, 0, this.bufferLen, decrypted, 0);
            System.arraycopy(decrypted, 0, this.buffer, 0, this.bufferLen);
            this.bufferPos = 0;
        }
        return this.buffer[this.bufferPos++] & 0xFF;
    }
}

