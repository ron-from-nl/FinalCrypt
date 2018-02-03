/*
 * Copyright © 2017 Ron de Jong (ronuitzaandam@gmail.com).
 *
 * This is free software; you can redistribute it 
 * under the terms of the Creative Commons License
 * Creative Commons License: (CC BY-NC-ND 4.0) as published by
 * https://creativecommons.org/licenses/by-nc-nd/4.0/ ; either
 * version 4.0 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International Public License for more details.
 *
 * You should have received a copy of the Creative Commons 
 * Public License License along with this software;
 */

package rdj;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GPT_Header
{
    private final long LBA = 1L;
    private final long LENGTH = Device.bytesPerSector * 1L;

    private byte[] signatureBytes; 
    private byte[] revisionBytes;
    private byte[] headerSizeBytes;
    private byte[] headerCRC32Bytes;
    private byte[] reservedBytes;
    private byte[] currentLBABytes;
    private byte[] backupLBABytes;
    private byte[] firstUsableLBABytes;
    private byte[] lastUsableLBABytes;
//    public  static final byte[] diskGUIDBytes =     GPT.getUUID();
    public  byte[] diskGUIDBytes;
    private byte[] startingLBAOfEntriesBytes;
    private byte[] entriesInArrayBytes;
    private byte[] entrySizeBytes;
    private byte[] crc32PartitionsBytes;
    private byte[] remainingBytes;
    private boolean littleEndian = true;
    private UI ui;
    private GPT gpt;

    public GPT_Header(UI ui, GPT gpt)
    {
        this.ui = ui;
        this.gpt = gpt;
    }
    
    public void reset()
    {
//      Offset        Length    When            Data
//      0  (0x00)     8 bytes   During LBA 1    Signature ("EFI PART", 45h 46h 49h 20h 50h 41h 52h 54h or 0x5452415020494645ULL [a] on little-endian machines)
                                                signatureBytes =            GPT.hex2Bytes("00 00 00 00 00 00 00 00"); // "EFI PART".getBytes(StandardCharsets.UTF_8);
//      8  (0x08)     4 bytes   During LBA 1    Revision (for GPT version 1.0 (through at least UEFI version 2.7 (May 2017)), the value is 00h 00h 01h 00h)
                                                revisionBytes =             GPT.hex2Bytes("00 00 00 00");
//      12 (0x0C)     4 bytes   During LBA 1    Header size in little endian (in bytes, usually 5Ch 00h 00h 00h or 92 bytes)
                                                headerSizeBytes =           GPT.hex2Bytes("00 00 00 00"); // Header Size = 92 bytes long
//      16 (0x10)     4 bytes   Post LBA 1      CRC32/zlib of header (offset +0 up to HEADER SIZE!!!) in little endian, with this field zeroed during calculation
                                                headerCRC32Bytes =          GPT.hex2Bytes("00 00 00 00"); // Correct: 00 49 7C B9 Correct: 
//      20 (0x14)     4 bytes   During LBA 1    Reserved; must be zero
                                                reservedBytes =             GPT.hex2Bytes("00 00 00 00");
//      24 (0x18)     8 bytes   During LBA 1    Current LBA (location of this header copy) At the top of the Storage
                                                currentLBABytes =           GPT.hex2Bytes(GPT.getHexStringLittleEndian(0L,8));
//      32 (0x20)     8 bytes   Post LBA-1      Backup LBA (location of the other header copy) (at the far end of storage) Reversed in Backup Header
                                                backupLBABytes =            GPT.hex2Bytes(GPT.getHexStringLittleEndian(0L, 8));
//      40 (0x28)     8 bytes   During LBA 1    First usable LBA for partitions (primary partition table last LBA + 1) First LBA after Last Entry (Entry 128) = LBA 34
                                                firstUsableLBABytes =       GPT.hex2Bytes(GPT.getHexStringLittleEndian(0L, 8)); // Header + Entries (512L + 512L + ( 128L * 128L )) /  bytesPerSector
//      48 (0x30)     8 bytes   Post LBA 1      Last usable LBA (secondary partition table first LBA - 1) = LBA-34 // (Capacity - (bytesPerSector * 34)) (deviceSize - (bytesPerSector*34))
                                                lastUsableLBABytes =        GPT.hex2Bytes(GPT.getHexStringLittleEndian(0L,8));
//      56 (0x38)     16 bytes  During LBA 1    Disk GUID (also referred as UUID on UNIXes) hex2Bytes("FD A4 3C 26 16 40 7E 43 83 D2 91 C0 6D C4 28 26"); // 
                                                diskGUIDBytes =             GPT.getZeroBytes(16);
//      72 (0x48)     8 bytes   During LBA 1    Starting LBA of array of partition entries (always 2 in primary copy)
                                                startingLBAOfEntriesBytes = GPT.hex2Bytes(GPT.getHexStringLittleEndian(0L, 8));
//      80 (0x50)     4 bytes   During LBA 1    Number of partition entries in array
                                                entriesInArrayBytes =       GPT.hex2Bytes(GPT.getHexStringLittleEndian(0L, 4));
//      84 (0x54)     4 bytes   During LBA 1    Size of a single partition entry (usually 80h or 128)
                                                entrySizeBytes =            GPT.hex2Bytes(GPT.getHexStringLittleEndian(0L, 4));
//      88 (0x58)     4 bytes   Post LBA        The CRC32 of the GUID Partition Entry array.
//                                              Starts at PartitionEntryLBA and iscomputed over a byte length ofNumberOfPartitionEntries *SizeOfPartitionEntry
                                                crc32PartitionsBytes =      GPT.hex2Bytes("00 00 00 00"); // Error: D9 CD 19 1F Correct: 
//      92 (0x5C)     * bytes   During LBA 1    Remaining 420 bytes (or more depending on sector size) of zero's
                                                remainingBytes =            GPT.getZeroBytes(420);
    }

    public void read(Path rawDeviceFilePath)
    {
        byte[] bytes = new byte[(int)LENGTH]; bytes = new Device(ui).read(rawDeviceFilePath, LBA, this.LENGTH);
//      Offset        Length    When            Data
//      0  (0x00)     8 bytes   During LBA 1    Signature ("EFI PART", 45h 46h 49h 20h 50h 41h 52h 54h or 0x5452415020494645ULL [a] on little-endian machines)
                                                                            signatureBytes = GPT.get(bytes, 0, 8);
//      8  (0x08)     4 bytes   During LBA 1    Revision (for GPT version 1.0 (through at least UEFI version 2.7 (May 2017)), the value is 00h 00h 01h 00h)
                                                                            revisionBytes = GPT.get(bytes, 8, 4);
//      12 (0x0C)     4 bytes   During LBA 1    Header size in little endian (in bytes, usually 5Ch 00h 00h 00h or 92 bytes)
                                                                            headerSizeBytes = GPT.get(bytes, 12, 4);
//      16 (0x10)     4 bytes   Post LBA 1      CRC32/zlib of header (offset +0 up to HEADER SIZE!!!) in little endian, with this field zeroed during calculation
                                                                            headerCRC32Bytes = GPT.get(bytes, 16, 4);
//      20 (0x14)     4 bytes   During LBA 1    Reserved; must be zero
                                                                            reservedBytes = GPT.get(bytes, 20, 4);
//      24 (0x18)     8 bytes   During LBA 1    Current LBA (location of this header copy) At the top of the Storage
                                                                            currentLBABytes = GPT.get(bytes, 24, 8);
//      32 (0x20)     8 bytes   Post LBA-1      Backup LBA (location of the other header copy) (at the far end of storage) Reversed in Backup Header
                                                                            backupLBABytes = GPT.get(bytes, 32, 8);
//      40 (0x28)     8 bytes   During LBA 1    First usable LBA for partitions (primary partition table last LBA + 1) First LBA after Last Entry (Entry 128) = LBA 34
                                                                            firstUsableLBABytes = GPT.get(bytes, 40, 8);
//      48 (0x30)     8 bytes   Post LBA 1      Last usable LBA (secondary partition table first LBA - 1) = LBA-34 // (Capacity - (bytesPerSector * 34)) (deviceSize - (bytesPerSector*34))
                                                                            lastUsableLBABytes = GPT.get(bytes, 48, 8);
//      56 (0x38)     16 bytes  During LBA 1    Disk GUID (also referred as UUID on UNIXes) hex2Bytes("FD A4 3C 26 16 40 7E 43 83 D2 91 C0 6D C4 28 26"); // 
                                                                            diskGUIDBytes = GPT.get(bytes, 56, 16);
//      72 (0x48)     8 bytes   During LBA 1    Starting LBA of array of partition entries (always 2 in primary copy)
                                                                            startingLBAOfEntriesBytes = GPT.get(bytes, 72, 8);
//      80 (0x50)     4 bytes   During LBA 1    Number of partition entries in array
                                                                            entriesInArrayBytes = GPT.get(bytes, 80, 4);
//      84 (0x54)     4 bytes   During LBA 1    Size of a single partition entry (usually 80h or 128)
                                                                            entrySizeBytes = GPT.get(bytes, 84, 4);
//      88 (0x58)     4 bytes   Post LBA        The CRC32 of the GUID Partition Entry array.
                                                                            crc32PartitionsBytes = GPT.get(bytes, 88, 4);
//      92 (0x5C)     420 bytes   During LBA 1    Remaining 420 bytes (or more depending on sector size) of zero's
                                                                            remainingBytes = GPT.get(bytes, 92, 420);
    }
    
    public void create(Path rawDeviceFilePath)
    {
        long deviceSize = gpt.getDeviceSize(ui, rawDeviceFilePath);
//      Offset        Length    When            Data
//      0  (0x00)     8 bytes   During LBA 1    Signature ("EFI PART", 45h 46h 49h 20h 50h 41h 52h 54h or 0x5452415020494645ULL [a] on little-endian machines)
                                                signatureBytes =            GPT.hex2Bytes("45 46 49 20 50 41 52 54"); // "EFI PART".getBytes(StandardCharsets.UTF_8);
//      8  (0x08)     4 bytes   During LBA 1    Revision (for GPT version 1.0 (through at least UEFI version 2.7 (May 2017)), the value is 00h 00h 01h 00h)
                                                revisionBytes =             GPT.hex2Bytes("00 00 01 00");
//      12 (0x0C)     4 bytes   During LBA 1    Header size in little endian (in bytes, usually 5Ch 00h 00h 00h or 92 bytes)
                                                headerSizeBytes =           GPT.hex2Bytes("5C 00 00 00"); // Header Size = 92 bytes long
//      16 (0x10)     4 bytes   Post LBA 1      CRC32/zlib of header (offset +0 up to HEADER SIZE!!!) in little endian, with this field zeroed during calculation
                                                headerCRC32Bytes =          GPT.hex2Bytes("00 00 00 00"); // Correct: 00 49 7C B9 Correct: 
//      20 (0x14)     4 bytes   During LBA 1    Reserved; must be zero
                                                reservedBytes =             GPT.hex2Bytes("00 00 00 00");
//      24 (0x18)     8 bytes   During LBA 1    Current LBA (location of this header copy) At the top of the Storage
                                                currentLBABytes =           GPT.hex2Bytes(GPT.getHexStringLittleEndian(1L, 8));
//      32 (0x20)     8 bytes   Post LBA-1      Backup LBA (location of the other header copy) (at the far end of storage) Reversed in Backup Header
                                                backupLBABytes =            GPT.hex2Bytes(GPT.getHexStringLittleEndian((deviceSize - Device.bytesPerSector) / Device.bytesPerSector, 8));
//      40 (0x28)     8 bytes   During LBA 1    First usable LBA for partitions (primary partition table last LBA + 1) First LBA after Last Entry (Entry 128) = LBA 34
                                                firstUsableLBABytes =       GPT.hex2Bytes(GPT.getHexStringLittleEndian(34L, 8)); // Header + Entries (512L + 512L + ( 128L * 128L )) /  bytesPerSector
//      48 (0x30)     8 bytes   Post LBA 1      Last usable LBA (secondary partition table first LBA - 1) = LBA-34 // (Capacity - (bytesPerSector * 34)) (deviceSize - (bytesPerSector*34))
                                                lastUsableLBABytes =        GPT.hex2Bytes(GPT.getHexStringLittleEndian(((deviceSize / Device.bytesPerSector) - 34L),8));
//      56 (0x38)     16 bytes  During LBA 1    Disk GUID (also referred as UUID on UNIXes) hex2Bytes("FD A4 3C 26 16 40 7E 43 83 D2 91 C0 6D C4 28 26"); // 
                                                diskGUIDBytes =             GPT.getUUID();
//      72 (0x48)     8 bytes   During LBA 1    Starting LBA of array of partition entries (always 2 in primary copy)
                                                startingLBAOfEntriesBytes = GPT.hex2Bytes(GPT.getHexStringLittleEndian(2L, 8));
//      80 (0x50)     4 bytes   During LBA 1    Number of partition entries in array
                                                entriesInArrayBytes =       GPT.hex2Bytes(GPT.getHexStringLittleEndian(128L, 4));
//      84 (0x54)     4 bytes   During LBA 1    Size of a single partition entry (usually 80h or 128)
                                                entrySizeBytes =            GPT.hex2Bytes(GPT.getHexStringLittleEndian(128L, 4));
//      88 (0x58)     4 bytes   Post LBA        The CRC32 of the GUID Partition Entry array.
//                                              Starts at PartitionEntryLBA and is computed over a byte length of NumberOfPartitionEntries *SizeOfPartitionEntry
                                                crc32PartitionsBytes =      GPT.hex2Bytes("00 00 00 00"); // Error: D9 CD 19 1F Correct: 
//      92 (0x5C)     * bytes   During LBA 1    Remaining 420 bytes (or more depending on sector size) of zero's
                                                remainingBytes =            GPT.getZeroBytes(420);
//      CRC Calculation
                                                crc32PartitionsBytes =      GPT.getCRC32("GPT_Header.crc32PartitionsBytes: ", gpt.get_GPT_Entries().get(), littleEndian);        
                                                headerCRC32Bytes =          GPT.getCRC32("GPT_Header.headerCRC32Bytes: ", get(0, 92), littleEndian); // little Endian (checked)
    }
    
//    public void read()
    public void write(Path rawDeviceFilePath) { new Device(ui).write(get(), rawDeviceFilePath, LBA); }
    
    public byte[] get(int off, int length) { return GPT.get(get(), off, length); }
    public byte[] get()
    {
        List<Byte> definitiveByteList = new ArrayList<Byte>();
        for (byte mybyte: signatureBytes)               { definitiveByteList.add(mybyte); }
        for (byte mybyte: revisionBytes)                { definitiveByteList.add(mybyte); }
        for (byte mybyte: headerSizeBytes)              { definitiveByteList.add(mybyte); }
        for (byte mybyte: headerCRC32Bytes)             { definitiveByteList.add(mybyte); } // CRC = set
        for (byte mybyte: reservedBytes)                { definitiveByteList.add(mybyte); }
        for (byte mybyte: currentLBABytes)              { definitiveByteList.add(mybyte); }
        for (byte mybyte: backupLBABytes)               { definitiveByteList.add(mybyte); }
        for (byte mybyte: firstUsableLBABytes)          { definitiveByteList.add(mybyte); }
        for (byte mybyte: lastUsableLBABytes)           { definitiveByteList.add(mybyte); }
        for (byte mybyte: diskGUIDBytes)                { definitiveByteList.add(mybyte); }
        for (byte mybyte: startingLBAOfEntriesBytes)    { definitiveByteList.add(mybyte); }
        for (byte mybyte: entriesInArrayBytes)          { definitiveByteList.add(mybyte); }
        for (byte mybyte: entrySizeBytes)               { definitiveByteList.add(mybyte); }
        for (byte mybyte: crc32PartitionsBytes)         { definitiveByteList.add(mybyte); } // CRC = set
        for (byte mybyte: remainingBytes)               { definitiveByteList.add(mybyte); }

        return GPT.byteListToByteArray(definitiveByteList);
    }
    
    public void print() { ui.log(toString()); }
    
    public String toString()
    {
        String returnString = "";
        String hexString = "";
        returnString += ("\r\n");
        returnString += ("[ LBA " + LBA + " GPT Header ]\r\n");
        returnString += ("\r\n");
        returnString += (String.format("%-50s", "signatureBytes"));               for (byte mybyte: signatureBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = ""; returnString += ("\r\n");
        returnString += (String.format("%-50s", "revisionBytes"));                for (byte mybyte: revisionBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "headerSizeBytes"));              for (byte mybyte: headerSizeBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "crc32Bytes"));                   for (byte mybyte: headerCRC32Bytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "reservedBytes"));                for (byte mybyte: reservedBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "currentLBABytes"));              for (byte mybyte: currentLBABytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "backupLBABytes"));               for (byte mybyte: backupLBABytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "firstUsableLBABytes"));          for (byte mybyte: firstUsableLBABytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "lastUsableLBABytes"));           for (byte mybyte: lastUsableLBABytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "diskGUIDBytes"));                for (byte mybyte: diskGUIDBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "startingLBAOfEntriesBytes"));    for (byte mybyte: startingLBAOfEntriesBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "entriesInArrayBytes"));          for (byte mybyte: entriesInArrayBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "entrySizeBytes"));               for (byte mybyte: entrySizeBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "crc32PartitionsBytes"));         for (byte mybyte: crc32PartitionsBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "remainingBytes"));               for (byte mybyte: remainingBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\r\n");
        return returnString;
    }
}
