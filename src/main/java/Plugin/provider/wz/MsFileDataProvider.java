package Plugin.provider.wz;

import Plugin.provider.MapleDataDirectoryEntry;
import Plugin.provider.MapleDataEntity;
import Plugin.provider.MapleDataFileEntry;
import Plugin.provider.wz.WzFileDataProvider;
import Plugin.provider.wz.util.WzLittleEndianAccessor;
import tools.wzlib.Ms_Entry;
import tools.wzlib.cryptography.CryptoInputStream;
import tools.wzlib.cryptography.Snow2CryptoTransform;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;


/*
 * Decompiled with CFR.
 *
 * Could not load the following classes:
 *  Plugin.provider.MapleDataDirectoryEntry
 *  Plugin.provider.MapleDataEntity
 *  Plugin.provider.wz.MsHeader
 *  Plugin.provider.wz.WzFileDataProvider
 *  Plugin.provider.wz.util.WzLittleEndianAccessor
 */

import Plugin.provider.MapleDataDirectoryEntry;
import Plugin.provider.MapleDataEntity;
import Plugin.provider.MapleDataFileEntry;
import Plugin.provider.wz.MsHeader;
import Plugin.provider.wz.WzFileDataProvider;
import Plugin.provider.wz.util.WzLittleEndianAccessor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import tools.wzlib.Ms_Entry;
import tools.wzlib.cryptography.CryptoInputStream;
import tools.wzlib.cryptography.Snow2CryptoTransform;

public class MsFileDataProvider
        extends WzFileDataProvider {
    public MsHeader Header;
    public List<Ms_Entry> Entries;


    public MsFileDataProvider(File msfile, WzFileDataProvider parent) {
        /*22*/
        super(msfile, msfile.getName(), msfile.isDirectory(), parent);

        /*23*/
        this.Header = new MsHeader(msfile.getPath());
        /*24*/
        this.Entries = new LinkedList<Ms_Entry>();
    }

    protected void parseDirectory(MapleDataDirectoryEntry dir, WzLittleEndianAccessor wlea, boolean loadWzAsFolder) {

        /*29*/
        if (this.Header == null || this.Header.EntryCount == 0) {
            /*30*/
            return;
        }
        /*32*/
        int entryCount = this.Header.EntryCount;
        /*35*/
        String fileNameWithSalt = this.Header.FileNameWithSalt;
        /*36*/
        byte[] snowCipherKey2 = new byte[16];
        /*37*/
        for (int i = 0; i < snowCipherKey2.length; ++i) {
            /*38*/
            snowCipherKey2[i] = (byte) (i + (i % 3 + 2) * fileNameWithSalt.charAt(fileNameWithSalt.length() - 1 - i % fileNameWithSalt.length()));
        }
        try {
            FileInputStream baseStream = new FileInputStream(wzfile);
            /*43*/
            if (!baseStream.markSupported()) {
                throw new RuntimeException("Stream does not support mark/reset");
            }
            /*44*/
            baseStream.reset();
            Snow2CryptoTransform snowCipher = new Snow2CryptoTransform(snowCipherKey2, null, false);
            CryptoInputStream snowDecoderStream = new CryptoInputStream(baseStream, snowCipher);
            DataInputStream snowReader = new DataInputStream(snowDecoderStream);
            /*52*/
            for (int i = 0; i < entryCount; ++i) {
                /*53*/
                int entryNameLen = snowReader.readInt();
                /*54*/
                byte[] entryNameBytes = new byte[entryNameLen * 2];
                /*55*/
                snowReader.readFully(entryNameBytes);
                String entryName = new String(entryNameBytes, StandardCharsets.UTF_16LE);
                /*57*/
                int checkSum = snowReader.readInt();
                /*58*/
                int flags = snowReader.readInt();
                /*59*/
                int startPos = snowReader.readInt();
                /*60*/
                int size = snowReader.readInt();
                /*61*/
                int sizeAligned = snowReader.readInt();
                /*62*/
                int unk1 = snowReader.readInt();
                /*63*/
                int unk2 = snowReader.readInt();
                /*64*/
                byte[] entryKey = new byte[16];
                /*65*/
                snowReader.readFully(entryKey);
                Ms_Entry entry = new Ms_Entry(entryName, checkSum, flags, startPos, size, sizeAligned, unk1, unk2, entryKey);
                /*68*/
                entry.setCalculatedCheckSum(flags + startPos + size + sizeAligned + unk1 + IntStream.range(0, entryKey.length).map(n -> entryKey[n] & 0xFF).sum());
                /*69*/
                this.Entries.add(entry);
                /*71*/
                dir.addFile(new MapleDataFileEntry(entryName, size, checkSum, startPos, (MapleDataEntity) dir, ""));
            }
            long dataStartPos = baseStream.available();
            /*76*/
            if ((dataStartPos & 0x3FFL) != 0L) {
                /*77*/
                dataStartPos = dataStartPos - (dataStartPos & 0x3FFL) + 1024L;
            }
            /*79*/
            this.Header.DataStartPosition = dataStartPos;
            for (MapleDataFileEntry entry : dir.getFiles()) {
                /*82*/
                entry.setOffset(dataStartPos + entry.getOffset() * 1024L);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read entries", e);
        }
    }
}

