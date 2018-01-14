/*
 * Copyright (C) 2018 ron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package rdj;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GPT_Entries_Backup
{
    public final long LBA = -33L;
    private final long LENGTH = 128L * 128L;

    private byte[]  partttionTypeGUIDBytes1;
    private long    firstLBA1;
    private byte[]  firstLBABytes1;
    private long    lastLBA1;
    private byte[]  lastLBABytes1;
    private byte[]  attributeFlagsBytes1;
    private byte[]  partitionNameBytes1;
    private byte[]  uniquePartitionGUIDBytes1;
    private byte[]  partttionTypeGUIDBytes2;
    private long    firstLBA2;
    private byte[]  firstLBABytes2;
    private long    lastLBA2;
    private byte[]  lastLBABytes2;
    private byte[]  attributeFlagsBytes2;
    private byte[]  partitionNameBytes2;
    private byte[]  uniquePartitionGUIDBytes2;
    private byte[]  partition3_128Bytes;
    private UI ui;
    private GPT gpt;

    public GPT_Entries_Backup(UI ui, GPT gpt)
    {
        this.ui = ui;
        this.gpt = gpt;
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

//      128 (0x80)  126 * 128 bytes    During LBA 2   Partition 3 - 128 Zero
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
                                                                                            firstLBABytes1 = GPT.get(bytes, 32, 8);
//      40 (0x28)   8 bytes     During LBA 2    Last LBA (inclusive, usually odd)
                                                                                            lastLBABytes1 = GPT.get(bytes, 40, 8);
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
                                                                                            firstLBABytes2 = GPT.get(bytes, 160, 8);
//      168 (0x28)   8 bytes     During LBA 2    Last LBA (inclusive, usually odd)
                                                                                            lastLBABytes2 = GPT.get(bytes, 168, 8);
//      176 (0x30)   8 bytes     During LBA 2    Attribute flags (e.g. bit 60 denotes read-only)
                                                                                            attributeFlagsBytes2 = GPT.get(bytes, 176, 8);
//      184 (0x38)   72 bytes    During LBA 2    Partition name (36 UTF-16LE code units)
                                                                                            partitionNameBytes2 = GPT.get(bytes, 184, 72);

////////////////////////////////////////////////////////////////////////////////////////////
//      256 (0x80)  126 * 128 bytes    During LBA 2   Partition 3 - 128 Zero
                                                                                            partition3_128Bytes = GPT.get(bytes, 256, 126 * 128);
////////////////////////////////////////////////////////////////////////////////////////////
    }
    
    public void create(Path cipherFilePath)
    {
        long cipherSize = (long) GPT.getCipherSize(ui, cipherFilePath);
//      Offset        Length    When            Data
//      0 (0x00)      16 bytes  During LBA 2    Partition type GUID
                                                partttionTypeGUIDBytes1 =                   GPT.hex2Bytes("AF 3D C6 0F 83 84 72 47 8E 79 3D 69 D8 47 7D E4");
//      16 (0x10) 16 bytes    During LBA 2      Unique partition GUID
                                                uniquePartitionGUIDBytes1 =                 gpt.get_GPT_Entries().uniquePartitionGUIDBytes1;
//      32 (0x20)   8 bytes     During LBA 2    First LBA (little endian) LBA 2048
                                                firstLBA1 =                                 2048L;
                                                firstLBABytes1 =                            GPT.hex2Bytes(GPT.getHexStringLittleEndian(firstLBA1, 8)); // hex2Bytes("00 08 00 00 00 00 00 00");
//      40 (0x28)   8 bytes     During LBA 2    Last LBA (inclusive, usually odd)
                                                lastLBA1 =                                  2048L + (long)(Math.floor(cipherSize / Device.bytesPerSector));
                                                lastLBABytes1 =                             GPT.hex2Bytes(GPT.getHexStringLittleEndian(lastLBA1, 8));
//      48 (0x30) 8 bytes       During LBA 2    Attribute flags (e.g. bit 60 denotes read-only)
                                                attributeFlagsBytes1 =                      GPT.hex2Bytes("00 00 00 00 00 00 00 00");
//      56 (0x38) 72 bytes      During LBA 2    Partition name (36 UTF-16LE code units)
                                                partitionNameBytes1 =                       GPT.getZeroBytes(72);


////////////////////////////////////////////////////////////////////////////////////////////                                                
                                                
                                                
//      Offset      Length      When            Data
//      0 (0x00)    16 bytes    During LBA 2    Partition type GUID
                                                partttionTypeGUIDBytes2 =                   GPT.hex2Bytes("AF 3D C6 0F 83 84 72 47 8E 79 3D 69 D8 47 7D E4");
//      16 (0x10)   16 bytes    During LBA 2    Unique partition GUID
                                                uniquePartitionGUIDBytes2 =                 gpt.get_GPT_Entries().uniquePartitionGUIDBytes2;
//      32 (0x20)   8 bytes     During LBA 2    First LBA (little endian)
                                                firstLBA2 =                                 2048L + (long)(Math.floor(cipherSize / Device.bytesPerSector) + 1L);
                                                firstLBABytes2 =                            GPT.hex2Bytes(GPT.getHexStringLittleEndian(firstLBA2, 8));
//      40 (0x28)   8 bytes     During LBA 2    Last LBA (inclusive, usually odd)
                                                lastLBA2 =                                  firstLBA2 + (long)(Math.floor(cipherSize / Device.bytesPerSector));
                                                lastLBABytes2 =                             GPT.hex2Bytes(GPT.getHexStringLittleEndian(lastLBA2, 8));
//      48 (0x30)   8 bytes     During LBA 2    Attribute flags (e.g. bit 60 denotes read-only)
                                                attributeFlagsBytes2 =                      GPT.hex2Bytes("00 00 00 00 00 00 00 00");
//      56 (0x38)   72 bytes    During LBA 2    Partition name (36 UTF-16LE code units)
                                                partitionNameBytes2 =                       GPT.getZeroBytes(72);

////////////////////////////////////////////////////////////////////////////////////////////

//      128 (0x80)  126 * 128 bytes  During LBA 2     Partition 3 Zero
                                                partition3_128Bytes =                       GPT.getZeroBytes(126 * 128);

////////////////////////////////////////////////////////////////////////////////////////////
    }
    
    public void write(Path rawDeviceFilePath)                               { new Device(ui).write(get(), rawDeviceFilePath, LBA); }
    public void writeCipher(Path cipherFilePath, Path rawDeviceFilePath)    { new Device(ui).write(cipherFilePath, rawDeviceFilePath, firstLBA1, lastLBA1, firstLBA2, lastLBA2); }
    
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

        returnString += ("\n");
        returnString += ("[ LBA " + LBA + " Entry 1-4 Backup ]\n");
        returnString += ("\n");
        returnString += (String.format("%-50s", "partttionTypeGUIDBytes"));   for (byte mybyte: partttionTypeGUIDBytes1) { hexString1 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString1)); hexString1 = ""; returnString += ("\n");
        returnString += (String.format("%-50s", "uniquePartitionGUIDBytes")); for (byte mybyte: uniquePartitionGUIDBytes1) { hexString1 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString1)); hexString1 = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "firstLBABytes"));            for (byte mybyte: firstLBABytes1) { hexString1 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString1)); hexString1 = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "lastLBABytes"));             for (byte mybyte: lastLBABytes1) { hexString1 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString1)); hexString1 = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "attributeFlagsBytes"));      for (byte mybyte: attributeFlagsBytes1) { hexString1 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString1)); hexString1 = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "partitionNameBytes"));       for (byte mybyte: partitionNameBytes1) { hexString1 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString1)); hexString1 = "";  returnString += ("\n");

        String hexString2 = "";
        returnString += ("\n");
        returnString += (String.format("%-50s", "partttionTypeGUIDBytes"));   for (byte mybyte: partttionTypeGUIDBytes2) { hexString2 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString2)); hexString2 = ""; returnString += ("\n");
        returnString += (String.format("%-50s", "uniquePartitionGUIDBytes")); for (byte mybyte: uniquePartitionGUIDBytes2) { hexString2 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString2)); hexString2 = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "firstLBABytes"));            for (byte mybyte: firstLBABytes2) { hexString2 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString2)); hexString2 = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "lastLBABytes"));             for (byte mybyte: lastLBABytes2) { hexString2 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString2)); hexString2 = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "attributeFlagsBytes"));      for (byte mybyte: attributeFlagsBytes2) { hexString2 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString2)); hexString2 = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "partitionNameBytes"));       for (byte mybyte: partitionNameBytes2) { hexString2 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString2)); hexString2 = "";  returnString += ("\n");
        returnString += ("\n");
        returnString += (String.format("%-50s", "partition3_128Bytes"));      for (byte mybyte: partition3_128Bytes) { hexString2 += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString2)); hexString2 = "";  returnString += ("\n");
        return returnString;
    }
}
