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
import java.util.zip.CRC32;

public class GPT_Header2
{
    private final long LBA = -1L;
    private final long LENGTH = Device.bytesPerSector * 1L;

    private byte[] signatureBytes;
    private byte[] revisionBytes;
    private byte[] headerSizeBytes;
    private byte[] headerCRC32Bytes;
    private byte[] reservedBytes;
    
    private long   myLBA;
    private byte[] myLBABytes;
    private long   alternateLBA;
    private byte[] alternateLBABytes;
    private long   firstUsableLBA;
    private byte[] firstUsableLBABytes;
    private long   lastUsableLBA;
    private byte[] lastUsableLBABytes;
    public  byte[] diskGUIDBytes;
    private long   partitionEntryLBA;
    private byte[] partitionEntryLBABytes;
    private byte[] numberOfPartitionEntriesBytes;
    private byte[] sizeOfPartitionEntryBytes;
    private byte[] crc32PartitionsBytes;
    private byte[] reservedUEFIBytes;
    private boolean littleEndian = true;
    private UI	    ui;
    private GPT	    gpt;

    public GPT_Header2(UI ui, GPT gpt)
    {
        this.ui = ui;
        this.gpt = gpt;
	clear();
    }

    public void clear()
    {
//      Offset        Length    When            Data
//      0  (0x00)     8 bytes   During LBA 1    Signature ("EFI PART", 45h 46h 49h 20h 50h 41h 52h 54h or 0x5452415020494645ULL [a] on little-endian machines)
                                                signatureBytes =			GPT.hex2Bytes("00 00 00 00 00 00 00 00"); // "EFI PART".getBytes(StandardCharsets.UTF_8);
//      8  (0x08)     4 bytes   During LBA 1    Revision (for GPT version 1.0 (through at least UEFI version 2.7 (May 2017)), the value is 00h 00h 01h 00h)
                                                revisionBytes =				GPT.hex2Bytes("00 00 00 00");
//      12 (0x0C)     4 bytes   During LBA 1    Header size in little endian (in bytes, usually 5Ch 00h 00h 00h or 92 bytes)
                                                headerSizeBytes =			GPT.hex2Bytes("00 00 00 00"); // Header Size = 92 bytes long
//      16 (0x10)     4 bytes   Post LBA 1      CRC32/zlib of header (offset +0 up to HEADER SIZE!!!) in little endian, with this field zeroed during calculation
                                                headerCRC32Bytes =			GPT.hex2Bytes("00 00 00 00"); // Correct: 00 49 7C B9 Correct: 
//      20 (0x14)     4 bytes   During LBA 1    Reserved; must be zero
                                                reservedBytes =				GPT.hex2Bytes("00 00 00 00");
//      24 (0x18)     8 bytes   During LBA 1    Current LBA (location of this header copy) At the top of the Storage
                                                myLBA =					0L;
                                                myLBABytes =				GPT.hex2Bytes(GPT.getHexStringLittleEndian(myLBA,8));
//      32 (0x20)     8 bytes   Post LBA-1      Backup LBA (location of the other header copy) (at the far end of storage) Reversed in Backup Header
                                                alternateLBA =				0L;
                                                alternateLBABytes =			GPT.hex2Bytes(GPT.getHexStringLittleEndian(alternateLBA, 8));
//      40 (0x28)     8 bytes   During LBA 1    First usable LBA for partitions (primary partition table last LBA + 1) First LBA after Last Entry (Entry 128) = LBA 34
                                                firstUsableLBA =			0L;
                                                firstUsableLBABytes =			GPT.hex2Bytes(GPT.getHexStringLittleEndian(firstUsableLBA, 8)); // Header + Entries (512L + 512L + ( 128L * 128L )) /  bytesPerSector
//      48 (0x30)     8 bytes   Post LBA 1      Last usable LBA (secondary partition table first LBA - 1) = LBA-34 // (Capacity - (bytesPerSector * 34)) (deviceSize - (bytesPerSector*34))
                                                lastUsableLBA =				0L;
                                                lastUsableLBABytes =			GPT.hex2Bytes(GPT.getHexStringLittleEndian(lastUsableLBA,8));
//      56 (0x38)     16 bytes  During LBA 1    Disk GUID (also referred as UUID on UNIXes) hex2Bytes("FD A4 3C 26 16 40 7E 43 83 D2 91 C0 6D C4 28 26"); // 
                                                diskGUIDBytes =				GPT.getZeroBytes(16);
//      72 (0x48)     8 bytes   During LBA 1    Starting LBA of array of partition entries (always 2 in primary copy)
                                                partitionEntryLBA =			0L;
                                                partitionEntryLBABytes =		GPT.hex2Bytes(GPT.getHexStringLittleEndian(partitionEntryLBA, 8));
//      80 (0x50)     4 bytes   During LBA 1    Number of partition entries in array
                                                numberOfPartitionEntriesBytes =		GPT.hex2Bytes(GPT.getHexStringLittleEndian(0L, 4));
//      84 (0x54)     4 bytes   During LBA 1    Size of a single partition entry (usually 80h or 128)
                                                sizeOfPartitionEntryBytes =		GPT.hex2Bytes(GPT.getHexStringLittleEndian(0L, 4));
//      88 (0x58)     4 bytes   Post LBA        The CRC32 of the GUID Partition Entry array.
//                                              Starts at PartitionEntryLBA and iscomputed over a byte length ofNumberOfPartitionEntries *SizeOfPartitionEntry
                                                crc32PartitionsBytes =			GPT.hex2Bytes("00 00 00 00"); // Error: D9 CD 19 1F Correct: 
//      92 (0x5C)     * bytes   During LBA 1    Remaining 420 bytes (or more depending on sector size) of zero's
                                                reservedUEFIBytes =			GPT.getZeroBytes(420);
    }

    public void read(Path rawDeviceFilePath)
    {
        byte[] bytes = new byte[(int)LENGTH]; bytes = new Device(ui).readLBA(rawDeviceFilePath, LBA, this.LENGTH);
//      Offset        Length    When            Data
//      0  (0x00)     8 bytes   During LBA 1    Signature ("EFI PART", 45h 46h 49h 20h 50h 41h 52h 54h or 0x5452415020494645ULL [a] on little-endian machines)
                                                signatureBytes =			GPT.getBytesPart(bytes, 0, 8);
//      8  (0x08)     4 bytes   During LBA 1    Revision (for GPT version 1.0 (through at least UEFI version 2.7 (May 2017)), the value is 00h 00h 01h 00h)
                                                revisionBytes =				GPT.getBytesPart(bytes, 8, 4);
//      12 (0x0C)     4 bytes   During LBA 1    Header size in little endian (in bytes, usually 5Ch 00h 00h 00h or 92 bytes)
                                                headerSizeBytes =			GPT.getBytesPart(bytes, 12, 4);
//      16 (0x10)     4 bytes   Post LBA 1      CRC32/zlib of header (offset +0 up to HEADER SIZE!!!) in little endian, with this field zeroed during calculation
                                                headerCRC32Bytes =			GPT.getBytesPart(bytes, 16, 4);
//      20 (0x14)     4 bytes   During LBA 1    Reserved; must be zero
                                                reservedBytes =				GPT.getBytesPart(bytes, 20, 4);
//      24 (0x18)     8 bytes   During LBA 1    Current LBA (location of this header copy) At the top of the Storage
                                                myLBABytes =				GPT.getBytesPart(bytes, 24, 8); myLBA = Long.reverseBytes(GPT.bytesToLong(myLBABytes));
//      32 (0x20)     8 bytes   Post LBA-1      Backup LBA (location of the other header copy) (at the far end of storage) Reversed in Backup Header
                                                alternateLBABytes =			GPT.getBytesPart(bytes, 32, 8); alternateLBA = Long.reverseBytes(GPT.bytesToLong(alternateLBABytes));
//      40 (0x28)     8 bytes   During LBA 1    First usable LBA for partitions (primary partition table last LBA + 1) First LBA after Last Entry (Entry 128) = LBA 34
                                                firstUsableLBABytes =			GPT.getBytesPart(bytes, 40, 8); firstUsableLBA = Long.reverseBytes(GPT.bytesToLong(firstUsableLBABytes));
//      48 (0x30)     8 bytes   Post LBA 1      Last usable LBA (secondary partition table first LBA - 1) = LBA-34 // (Capacity - (bytesPerSector * 34)) (deviceSize - (bytesPerSector*34))
                                                lastUsableLBABytes =			GPT.getBytesPart(bytes, 48, 8); lastUsableLBA = Long.reverseBytes(GPT.bytesToLong(lastUsableLBABytes));
//      56 (0x38)     16 bytes  During LBA 1    Disk GUID (also referred as UUID on UNIXes) hex2Bytes("FD A4 3C 26 16 40 7E 43 83 D2 91 C0 6D C4 28 26"); // 
                                                diskGUIDBytes =				GPT.getBytesPart(bytes, 56, 16);
//      72 (0x48)     8 bytes   During LBA 1    Starting LBA of array of partition entries (always 2 in primary copy)
                                                partitionEntryLBABytes =		GPT.getBytesPart(bytes, 72, 8); partitionEntryLBA = Long.reverseBytes(GPT.bytesToLong(partitionEntryLBABytes));
//      80 (0x50)     4 bytes   During LBA 1    Number of partition entries in array
                                                numberOfPartitionEntriesBytes =		GPT.getBytesPart(bytes, 80, 4);
//      84 (0x54)     4 bytes   During LBA 1    Size of a single partition entry (usually 80h or 128)
                                                sizeOfPartitionEntryBytes =		GPT.getBytesPart(bytes, 84, 4);
//      88 (0x58)     4 bytes   Post LBA        The CRC32 of the GUID Partition Entry array.
                                                crc32PartitionsBytes =			GPT.getBytesPart(bytes, 88, 4);
//      92 (0x5C)     420 bytes   During LBA 1  Remaining 420 bytes (or more depending on sector size) of zero's
                                                reservedUEFIBytes =			GPT.getBytesPart(bytes, 92, 420);
    }
    
    public void create(Path rawDeviceFilePath)
    {
        long deviceSize = gpt.getDeviceSize(ui, rawDeviceFilePath);
//      Offset        Length    When            Data
//      0  (0x00)     8 bytes   During LBA 1    Signature ("EFI PART", 45h 46h 49h 20h 50h 41h 52h 54h or 0x5452415020494645ULL [a] on little-endian machines)
                                                signatureBytes =			GPT.hex2Bytes("45 46 49 20 50 41 52 54"); // "EFI PART".getBytes(StandardCharsets.UTF_8);
//      8  (0x08)     4 bytes   During LBA 1    Revision (for GPT version 1.0 (through at least UEFI version 2.7 (May 2017)), the value is 00h 00h 01h 00h)
                                                revisionBytes =				GPT.hex2Bytes("00 00 01 00");
//      12 (0x0C)     4 bytes   During LBA 1    Header size in little endian (in bytes, usually 5Ch 00h 00h 00h or 92 bytes)
                                                headerSizeBytes =			GPT.hex2Bytes("5C 00 00 00"); // Header Size = 92 bytes long
//      16 (0x10)     4 bytes   Post LBA 1      CRC32/zlib of header (offset +0 up to header size) in little endian, with this field zeroed during calculation
                                                headerCRC32Bytes =			GPT.hex2Bytes("00 00 00 00"); // Diff-In-Backup
//      20 (0x14)     4 bytes   During LBA 1    Reserved; must be zero
                                                reservedBytes =				GPT.hex2Bytes("00 00 00 00");
//      24 (0x18)     8 bytes   During LBA 1    Current LBA (location of this header copy) // This LBA at the bottom of the storage
                                                myLBA =					((deviceSize - Device.bytesPerSector) / Device.bytesPerSector);
                                                myLBABytes =				GPT.hex2Bytes(GPT.getHexStringLittleEndian(myLBA, 8));
//      32 (0x20)     8 bytes   Post LBA-1      Backup LBA (location of the other header copy)  // Reversed in Primary Header
                                                alternateLBA =				1L;
                                                alternateLBABytes =			GPT.hex2Bytes(GPT.getHexStringLittleEndian(alternateLBA,8));
//      40 (0x28)     8 bytes   During LBA 1    First usable LBA for partitions (primary partition table last LBA + 1) First LBA after Last Entry (Entry 128) = LBA 34
                                                firstUsableLBA =			34L;
                                                firstUsableLBABytes =			GPT.hex2Bytes(GPT.getHexStringLittleEndian(firstUsableLBA, 8)); // Primary GPT Header + Entries
//      48 (0x30)     8 bytes   Post LBA 1      Last usable LBA (secondary partition table first LBA - 1) = LBA-34 // (Capacity - (bytesPerSector * 34)) (deviceSize - (bytesPerSector*34))
                                                lastUsableLBA =				((deviceSize / Device.bytesPerSector) - 34L);
                                                lastUsableLBABytes =			GPT.hex2Bytes(GPT.getHexStringLittleEndian(((deviceSize / Device.bytesPerSector) - 34L),8));
//      56 (0x38)     16 bytes  During LBA 1    Disk GUID (also referred as UUID on UNIXes)
                                                diskGUIDBytes =				gpt.gpt_Header1.diskGUIDBytes;
//      72 (0x48)     8 bytes   During LBA 1    Starting LBA of array of partition entries (always 2 in primary copy)
                                                partitionEntryLBA =			(deviceSize / Device.bytesPerSector) + gpt.gpt_Entries2.LBA;
                                                partitionEntryLBABytes =		GPT.hex2Bytes(GPT.getHexStringLittleEndian(partitionEntryLBA, 8));
//      80 (0x50)     4 bytes   During LBA 1    Number of partition entries in array
                                                numberOfPartitionEntriesBytes =		GPT.hex2Bytes(GPT.getHexStringLittleEndian(128L, 4));
//      84 (0x54)     4 bytes   During LBA 1    Size of a single partition entry (usually 80h or 128)
                                                sizeOfPartitionEntryBytes =		GPT.hex2Bytes(GPT.getHexStringLittleEndian(128L, 4));
//      88 (0x58)     4 bytes   Post LBA        CRC32/zlib of partition array in little endian (CRC over the Entire 128 x 128 Entry Array Block)
                                                crc32PartitionsBytes =			getCRC32("GPT_Header2.crc32PartitionsBytes: ", gpt.get_GPT_Entries2().getBytes(), littleEndian);
//      92 (0x5C)     * bytes   During LBA 1    Remaining 420 bytes (or more depending on sector size) of zero's
                                                reservedUEFIBytes =			GPT.getZeroBytes(420);
//      CRC Calculation
                                                headerCRC32Bytes =			getCRC32("GPT_Header2.headerCRC32Bytes:	    ",			      getBytes(0, 92), littleEndian); 
    }
    
    public void	write(Path rawDeviceFilePath)			{ new Device(ui).writeLBA(getBytes(), rawDeviceFilePath, LBA); }

    public byte[] getBytes(int off, int length)			{ return GPT.getBytesPart(getBytes(), off, length); }
    public byte[] getBytes()
    {
        List<Byte> definitiveByteList = new ArrayList<Byte>();
        for (byte mybyte: signatureBytes)			{ definitiveByteList.add(mybyte); }
        for (byte mybyte: revisionBytes)			{ definitiveByteList.add(mybyte); }
        for (byte mybyte: headerSizeBytes)			{ definitiveByteList.add(mybyte); }
        for (byte mybyte: headerCRC32Bytes)			{ definitiveByteList.add(mybyte); }
        for (byte mybyte: reservedBytes)			{ definitiveByteList.add(mybyte); }
        for (byte mybyte: myLBABytes)				{ definitiveByteList.add(mybyte); }
        for (byte mybyte: alternateLBABytes)			{ definitiveByteList.add(mybyte); }
        for (byte mybyte: firstUsableLBABytes)			{ definitiveByteList.add(mybyte); }
        for (byte mybyte: lastUsableLBABytes)			{ definitiveByteList.add(mybyte); }
        for (byte mybyte: diskGUIDBytes)			{ definitiveByteList.add(mybyte); }
        for (byte mybyte: partitionEntryLBABytes)		{ definitiveByteList.add(mybyte); }
        for (byte mybyte: numberOfPartitionEntriesBytes)	{ definitiveByteList.add(mybyte); }
        for (byte mybyte: sizeOfPartitionEntryBytes)		{ definitiveByteList.add(mybyte); }
        for (byte mybyte: crc32PartitionsBytes)			{ definitiveByteList.add(mybyte); }
        for (byte mybyte: reservedUEFIBytes)			{ definitiveByteList.add(mybyte); }

        return GPT.byteListToByteArray(definitiveByteList);
    }

    public void print() { ui.log(toString()); }
    
    @Override
    public String toString()
    {
        String returnString = "";
        returnString += ("\r\n");
        returnString += ("========================================================================\r\n");
        returnString += ("\r\n");
        returnString += ("[ LBA " + LBA + " - Secondary GPT Header (" + getBytes().length + " Bytes) Storage: " + GPT.getLBAHumanSize(alternateLBABytes,1) + " ]\r\n");
        returnString += ("\r\n");
        returnString += (String.format("%-25s", "Signature"));			returnString += GPT.getHexAndDecimal(signatureBytes, false) + "\r\n";
        returnString += (String.format("%-25s", "Revision"));			returnString += GPT.getHexAndDecimal(revisionBytes, true) + "\r\n";
        returnString += (String.format("%-25s", "HeaderSize"));			returnString += GPT.getHexAndDecimal(headerSizeBytes, true) + "\r\n";
        returnString += (String.format("%-25s", "HeaderCRC32"));                returnString += GPT.getHexAndDecimal(headerCRC32Bytes, false) + " " + GPT.getHexString(getCRC32("chk: ", getBytes(), false), 2) + " (now) \r\n";
        returnString += (String.format("%-25s", "Reserved"));			returnString += GPT.getHexAndDecimal(reservedBytes, false) + "\r\n";
        returnString += (String.format("%-25s", "MyLBA"));			returnString += GPT.getHexAndDecimal(myLBABytes, true) + "\r\n";
        returnString += (String.format("%-25s", "AlternateLBA"));               returnString += GPT.getHexAndDecimal(alternateLBABytes, true) + "\r\n";
        returnString += (String.format("%-25s", "FirstUsableLBA"));		returnString += GPT.getHexAndDecimal(firstUsableLBABytes, true) + "\r\n";
        returnString += (String.format("%-25s", "LastUsableLBA"));		returnString += GPT.getHexAndDecimal(lastUsableLBABytes, true) + "\r\n";
        returnString += (String.format("%-25s", "DiskGUID"));			returnString += GPT.getHexAndDecimal(diskGUIDBytes, false) + "\r\n";
        returnString += (String.format("%-25s", "PartitionEntryLBA"));		returnString += GPT.getHexAndDecimal(partitionEntryLBABytes, true) + "\r\n";
        returnString += (String.format("%-25s", "NumberOfPartitionEntries"));	returnString += GPT.getHexAndDecimal(numberOfPartitionEntriesBytes, true) + "\r\n";
        returnString += (String.format("%-25s", "SizeOfPartitionEntry"));       returnString += GPT.getHexAndDecimal(sizeOfPartitionEntryBytes, true) + "\r\n";
        returnString += (String.format("%-25s", "CRC32Partitions"));		returnString += GPT.getHexAndDecimal(crc32PartitionsBytes, false) + " " + GPT.getHexString(getCRC32("chk: ", gpt.get_GPT_Entries2().getBytes(), false), 2) + " (now)\r\n";
        returnString += (String.format("%-25s", "Reserved (UEFI)"));            returnString += GPT.getHexAndDecimal(reservedUEFIBytes, false) + "\r\n";
        returnString += ("\r\n");
        returnString += ("------------------------------------------------------------------------\r\n");
        return returnString;
    }
    
    synchronized private byte[] getCRC32(String string, byte[] bytes, boolean littleEndian) // Byte order to Little Endian
    {
        CRC32 crc = new CRC32(); crc.update(bytes);
        byte [] bigEndianBytes = new byte[4];
        byte [] littleEndianBytes = new byte[4];        
        bigEndianBytes = ByteBuffer.allocate(4).putInt((int) crc.getValue()).array();
        littleEndianBytes = GPT.getByteArrayLittleEndian(ByteBuffer.allocate(4).putInt((int) crc.getValue()).array());
//        System.out.println(string + getHexString(littleEndianBytes, "2"));
        if (littleEndian)   { return littleEndianBytes; }
        else                { return bigEndianBytes; }
    }

}
