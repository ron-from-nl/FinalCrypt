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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GPT_Entries
{
    private final long         LBA = 2L;
    private final long         LENGTH = 128L * 128L;

    private byte[]              partttionTypeGUIDBytes1;
    public  byte[]              uniquePartitionGUIDBytes1;
    public long                 firstLBA1;
    private byte[]              firstLBABytes1;
    public long                 lastLBA1;
    private byte[]              lastLBABytes1;
    private byte[]              attributeFlagsBytes1;
    private byte[]              partitionNameBytes1;
    private byte[]              partttionTypeGUIDBytes2;
    public  byte[]              uniquePartitionGUIDBytes2;
    public long                 firstLBA2;
    private byte[]              firstLBABytes2;
    public long                 lastLBA2;
    private byte[]              lastLBABytes2;
    private byte[]              attributeFlagsBytes2;
    private byte[]              partitionNameBytes2;
    private byte[]              partition3_128Bytes;
    private UI ui;

    public GPT_Entries(UI ui)
    {
        this.ui = ui;
    }
    
    public void reset()
    {
//      Offset      Length      When            Data
//      0 (0x00)    16 bytes    During LBA 2    Partition type GUID
                                                partttionTypeGUIDBytes1 =                   GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      16 (0x10)   16 bytes    During LBA 2    Unique partition GUID
                                                uniquePartitionGUIDBytes1 =                 GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      32 (0x20)   8 bytes     During LBA 2    First LBA (little endian) LBA 2048
                                                firstLBA1 =                                 0L;
                                                firstLBABytes1 =                            GPT.hex2Bytes(GPT.getHexStringLittleEndian(firstLBA1, 8)); // hex2Bytes("00 08 00 00 00 00 00 00");
//      40 (0x28)   8 bytes     During LBA 2    Last LBA (inclusive, usually odd)
                                                lastLBA1 =                                  0L;
                                                lastLBABytes1 =                             GPT.hex2Bytes(GPT.getHexStringLittleEndian(lastLBA1, 8));
//      48 (0x30)   8 bytes     During LBA 2    Attribute flags (e.g. bit 60 denotes read-only)
                                                attributeFlagsBytes1 =                      GPT.hex2Bytes("00 00 00 00 00 00 00 00");
//      56 (0x38)   72 bytes    During LBA 2    Partition name (36 UTF-16LE code units)
                                                partitionNameBytes1 =                       GPT.getZeroBytes(72);


////////////////////////////////////////////////////////////////////////////////////////////

                                                
//      Offset      Length      When            Data
//      0 (0x00)    16 bytes    During LBA 2    Partition type GUID
                                                partttionTypeGUIDBytes2 =                   GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      16 (0x10)   16 bytes    During LBA 2    Unique partition GUID
                                                uniquePartitionGUIDBytes2 =                 GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      32 (0x20)   8 bytes     During LBA 2    First LBA (little endian)
                                                firstLBA2 =                                 0L;
                                                firstLBABytes2 =                            GPT.hex2Bytes(GPT.getHexStringLittleEndian(firstLBA2, 8));
//      40 (0x28)   8 bytes     During LBA 2    Last LBA (inclusive, usually odd)
                                                lastLBA2 =                                  0L;
                                                lastLBABytes2 =                             GPT.hex2Bytes(GPT.getHexStringLittleEndian(lastLBA2, 8));
//      48 (0x30)   8 bytes     During LBA 2    Attribute flags (e.g. bit 60 denotes read-only)
                                                attributeFlagsBytes2 =                      GPT.hex2Bytes("00 00 00 00 00 00 00 00");
//      56 (0x38)   72 bytes    During LBA 2    Partition name (36 UTF-16LE code units)
                                                partitionNameBytes2 =                       GPT.getZeroBytes(72);

////////////////////////////////////////////////////////////////////////////////////////////

//      128 (0x80)  126 * 128 bytes    During LBA 2   Partition 3 Zero
                                                partition3_128Bytes =                       GPT.getZeroBytes(126 * 128);

////////////////////////////////////////////////////////////////////////////////////////////
    }

   public void read(Path rawDeviceFilePath)
    {
        byte[] bytes = new byte[(int)LENGTH]; bytes = new Device(ui).read(rawDeviceFilePath, LBA, this.LENGTH);        
//      Offset      Length      When            Data
//      0 (0x00)    16 bytes    During LBA 2    Partition type GUID
                                                                                            partttionTypeGUIDBytes1 = GPT.get(bytes, 0, 16);
//      16 (0x10)   16 bytes    During LBA 2    Unique partition GUID
                                                                                            uniquePartitionGUIDBytes1 = GPT.get(bytes, 16, 16);
//      32 (0x20)   8 bytes     During LBA 2    First LBA (little endian) LBA 2048
                                                                                            firstLBABytes1 = GPT.get(bytes, 32, 8); firstLBA1 = Long.reverseBytes(GPT.bytesToLong(firstLBABytes1));
//      40 (0x28)   8 bytes     During LBA 2    Last LBA (inclusive, usually odd)
                                                                                            lastLBABytes1 = GPT.get(bytes, 40, 8);  lastLBA1 = Long.reverseBytes(GPT.bytesToLong(lastLBABytes1));
//      48 (0x30)   8 bytes     During LBA 2    Attribute flags (e.g. bit 60 denotes read-only)
                                                                                            attributeFlagsBytes1 = GPT.get(bytes, 48, 8);
//      56 (0x38)   72 bytes    During LBA 2    Partition name (36 UTF-16LE code units)
                                                                                            partitionNameBytes1 = GPT.get(bytes, 56, 72);


////////////////////////////////////////////////////////////////////////////////////////////

                                                
//      Offset      Length      When            Data
//      128 (0x00)    16 bytes    During LBA 2    Partition type GUID
                                                                                            partttionTypeGUIDBytes2 = GPT.get(bytes, 128, 16);
//      144 (0x10)   16 bytes    During LBA 2    Unique partition GUID
                                                                                            uniquePartitionGUIDBytes2 = GPT.get(bytes, 144, 16);
//      160 (0x20)   8 bytes     During LBA 2    First LBA (little endian)
                                                                                            firstLBABytes2 = GPT.get(bytes, 160, 8); firstLBA2 = Long.reverseBytes(GPT.bytesToLong(firstLBABytes2));
//      168 (0x28)   8 bytes     During LBA 2    Last LBA (inclusive, usually odd)
                                                                                            lastLBABytes2 = GPT.get(bytes, 168, 8); lastLBA2 = Long.reverseBytes(GPT.bytesToLong(lastLBABytes2));
//      176 (0x30)   8 bytes     During LBA 2    Attribute flags (e.g. bit 60 denotes read-only)
                                                                                            attributeFlagsBytes2 = GPT.get(bytes, 176, 8);
//      184 (0x38)   72 bytes    During LBA 2    Partition name (36 UTF-16LE code units)
                                                                                            partitionNameBytes2 = GPT.get(bytes, 184, 72);

////////////////////////////////////////////////////////////////////////////////////////////
//      256 (0x80)  126 * 128 bytes    During LBA 2   Partition 3-128 Zero
                                                                                            partition3_128Bytes = GPT.get(bytes, 256, 126 * 128);
////////////////////////////////////////////////////////////////////////////////////////////
    }
    
//    public void create(Path cipherFilePath)
    public void create(long cipherSize)
    {
//        long cipherSize = (long) GPT.getCipherSize(ui, cipherFilePath);
//      Offset      Length      When            Data
//      0 (0x00)    16 bytes    During LBA 2    Partition type GUID
                                                partttionTypeGUIDBytes1 =                   GPT.hex2Bytes("AF 3D C6 0F 83 84 72 47 8E 79 3D 69 D8 47 7D E4");
//      16 (0x10)   16 bytes    During LBA 2    Unique partition GUID
                                                uniquePartitionGUIDBytes1 =                 GPT.getUUID();
//      32 (0x20)   8 bytes     During LBA 2    First LBA (little endian) LBA 2048
                                                firstLBA1 =                                 2048L;
                                                firstLBABytes1 =                            GPT.hex2Bytes(GPT.getHexStringLittleEndian(firstLBA1, 8)); // hex2Bytes("00 08 00 00 00 00 00 00");
//      40 (0x28)   8 bytes     During LBA 2    Last LBA (inclusive, usually odd)
                                                lastLBA1 =                                  2048L + (long)((Math.floor((cipherSize - 1L) / Device.bytesPerSector )));
                                                lastLBABytes1 =                             GPT.hex2Bytes(GPT.getHexStringLittleEndian(lastLBA1, 8));
//      48 (0x30)   8 bytes     During LBA 2    Attribute flags (e.g. bit 60 denotes read-only)
                                                attributeFlagsBytes1 =                      GPT.hex2Bytes("00 00 00 00 00 00 00 00");
//      56 (0x38)   72 bytes    During LBA 2    Partition name (36 UTF-16LE code units)
                                                partitionNameBytes1 =                       GPT.getZeroBytes(72);


////////////////////////////////////////////////////////////////////////////////////////////

                                                
//      Offset      Length      When            Data
//      0 (0x00)    16 bytes    During LBA 2    Partition type GUID
                                                partttionTypeGUIDBytes2 =                   GPT.hex2Bytes("AF 3D C6 0F 83 84 72 47 8E 79 3D 69 D8 47 7D E4");
//      16 (0x10)   16 bytes    During LBA 2    Unique partition GUID
                                                uniquePartitionGUIDBytes2 =                 GPT.getUUID();
//      32 (0x20)   8 bytes     During LBA 2    First LBA (little endian)
                                                firstLBA2 =                                 2048L + (long)((Math.floor((cipherSize - 1L) / Device.bytesPerSector ))) + 1L;
                                                firstLBABytes2 =                            GPT.hex2Bytes(GPT.getHexStringLittleEndian(firstLBA2, 8));
//      40 (0x28)   8 bytes     During LBA 2    Last LBA (inclusive, usually odd)
                                                lastLBA2 =                                  firstLBA2 + (long)((Math.floor((cipherSize - 1L) / Device.bytesPerSector )) );
                                                lastLBABytes2 =                             GPT.hex2Bytes(GPT.getHexStringLittleEndian(lastLBA2, 8));
//      48 (0x30)   8 bytes     During LBA 2    Attribute flags (e.g. bit 60 denotes read-only)
                                                attributeFlagsBytes2 =                      GPT.hex2Bytes("00 00 00 00 00 00 00 00");
//      56 (0x38)   72 bytes    During LBA 2    Partition name (36 UTF-16LE code units)
                                                partitionNameBytes2 =                       GPT.getZeroBytes(72);

////////////////////////////////////////////////////////////////////////////////////////////

//      128 (0x80)  126 * 128 bytes    During LBA 2   Partition 3 Zero
                                                partition3_128Bytes =                           GPT.getZeroBytes(126 * 128);

////////////////////////////////////////////////////////////////////////////////////////////
    }
    
    public void write(Path targetDeviceFilePath)                                    { new Device(ui).write(get(), targetDeviceFilePath, LBA); }
    public void writeCipher(Path cipherFilePath, Path targetDeviceFilePath)         { new Device(ui).writeCipher( cipherFilePath, targetDeviceFilePath, firstLBA1, lastLBA1, firstLBA2, lastLBA2); }
    public void cloneCipher(Path cipherDeviceFilePath, Path targetDeviceFilePath)   { new Device(ui).cloneCipher( cipherDeviceFilePath, targetDeviceFilePath, firstLBA1, lastLBA1, firstLBA2, lastLBA2); }
    
        
    public byte[] get(int off, int length) { return GPT.get(get(), off, length); }
    public byte[] get()
    {
        List<Byte> byteList = new ArrayList<Byte>();
        for (byte mybyte: partttionTypeGUIDBytes1)   { byteList.add(mybyte); }
        for (byte mybyte: uniquePartitionGUIDBytes1) { byteList.add(mybyte); }
        for (byte mybyte: firstLBABytes1)            { byteList.add(mybyte); }
        for (byte mybyte: lastLBABytes1)             { byteList.add(mybyte); }
        for (byte mybyte: attributeFlagsBytes1)      { byteList.add(mybyte); }
        for (byte mybyte: partitionNameBytes1)       { byteList.add(mybyte); }
        for (byte mybyte: partttionTypeGUIDBytes2)   { byteList.add(mybyte); }
        for (byte mybyte: uniquePartitionGUIDBytes2) { byteList.add(mybyte); }
        for (byte mybyte: firstLBABytes2)            { byteList.add(mybyte); }
        for (byte mybyte: lastLBABytes2)             { byteList.add(mybyte); }
        for (byte mybyte: attributeFlagsBytes2)      { byteList.add(mybyte); }
        for (byte mybyte: partitionNameBytes2)       { byteList.add(mybyte); }
        for (byte mybyte: partition3_128Bytes)       { byteList.add(mybyte); }
        return GPT.byteListToByteArray(byteList);
    }
    
    public void print() { ui.log(toString()); }
    
    public String toString()
    {
        String returnString = "";
        String hexString1 = "";

        returnString += ("\r\n");
        returnString += ("[ LBA " + LBA + " Entry 1-4 ]\r\n");
        returnString += ("\r\n");
        returnString += (String.format("%-50s", "partttionTypeGUIDBytes"));   for (byte mybyte: partttionTypeGUIDBytes1) { hexString1 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString1)); hexString1 = ""; returnString += ("\r\n");
        returnString += (String.format("%-50s", "uniquePartitionGUIDBytes")); for (byte mybyte: uniquePartitionGUIDBytes1) { hexString1 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString1)); hexString1 = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "firstLBABytes"));            for (byte mybyte: firstLBABytes1) { hexString1 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString1)); hexString1 = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "lastLBABytes"));             for (byte mybyte: lastLBABytes1) { hexString1 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString1)); hexString1 = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "attributeFlagsBytes"));      for (byte mybyte: attributeFlagsBytes1) { hexString1 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString1)); hexString1 = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "partitionNameBytes"));       for (byte mybyte: partitionNameBytes1) { hexString1 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString1)); hexString1 = "";  returnString += ("\r\n");

        String hexString2 = "";
        returnString += ("\r\n");
        returnString += (String.format("%-50s", "partttionTypeGUIDBytes"));   for (byte mybyte: partttionTypeGUIDBytes2) { hexString2 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString2)); hexString2 = ""; returnString += ("\r\n");
        returnString += (String.format("%-50s", "uniquePartitionGUIDBytes")); for (byte mybyte: uniquePartitionGUIDBytes2) { hexString2 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString2)); hexString2 = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "firstLBABytes"));            for (byte mybyte: firstLBABytes2) { hexString2 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString2)); hexString2 = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "lastLBABytes"));             for (byte mybyte: lastLBABytes2) { hexString2 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString2)); hexString2 = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "attributeFlagsBytes"));      for (byte mybyte: attributeFlagsBytes2) { hexString2 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString2)); hexString2 = "";  returnString += ("\r\n");
        returnString += (String.format("%-50s", "partitionNameBytes"));       for (byte mybyte: partitionNameBytes2) { hexString2 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString2)); hexString2 = "";  returnString += ("\r\n");
        returnString += ("\r\n");
        returnString += (String.format("%-50s", "partition3_128Bytes"));      for (byte mybyte: partition3_128Bytes) { hexString2 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString2)); hexString2 = "";  returnString += ("\r\n");
        return returnString;
    }
}
