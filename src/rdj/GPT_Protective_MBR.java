/*
 * Copyright (C) 2018 Ron de Jong (ronuitzaandam@gmail.com).
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

public class GPT_Protective_MBR
{
    private final long LBA = 0L;
    private final long LENGTH = Device.bytesPerSector * 1L;

    private byte[] bootstrapBytes;
    private byte[] diskSerialBytes;
    private byte[] reservedBytes;
    private byte[] activePartitionFlag;
    private byte[] startHeadBytes;
    private byte[] startSectorBytes;
    private byte[] startCylinderBytes;
    private byte[] fileSystemIdBytes;
    private byte[] endHeadBytes;
    private byte[] endSectorBytes;
    private byte[] endCylinderBytes;
    private byte[] firstSectorBytes;
    private byte[] totalSectorBytes;
    private byte[] partition2Bytes;
    private byte[] partition3Bytes;
    private byte[] partition4Bytes;
    private byte[] magicNumberBytes;
    private UI ui;

    public GPT_Protective_MBR(UI ui)
    {
        this.ui = ui;
        bootstrapBytes = new byte[440];
        diskSerialBytes = new byte[4];
        reservedBytes = new byte[2];
        activePartitionFlag = new byte[1];
        startHeadBytes = new byte[1];
        startSectorBytes = new byte[1];
        startCylinderBytes = new byte[1];
        fileSystemIdBytes = new byte[1];
        endHeadBytes = new byte[1];
        endSectorBytes = new byte[1];
        endCylinderBytes = new byte[1];
        firstSectorBytes = new byte[4];
        totalSectorBytes = new byte[4];
        partition2Bytes = new byte[16];
        partition3Bytes = new byte[16];
        partition4Bytes = new byte[16];
        magicNumberBytes = new byte[2];
    }

    public void reset()
    {
//      Offset        Length    When            Data
//      0  (0x00)     440 bytes During LBA 0    Bootloader bytes
                                                bootstrapBytes =        GPT.getZeroBytes(440); // 440
//      440  (0x00)   4 bytes   During LBA 0    Disk Serial Number
                                                diskSerialBytes =       GPT.hex2Bytes("00 00 00 00");
//      444  (0x00)   2 bytes   During LBA 0    Reserved
                                                reservedBytes =         GPT.hex2Bytes("00 00");
                                                
//      446  (0x00)   16 bytes  During LBA 0    partition1                      00 00 01 00 EE FE FF FF 01 00 FF 7F 94 03 00 00 (unknown partition type)
                                                activePartitionFlag =   GPT.hex2Bytes("00"); // 0x80 = Active
                                                startHeadBytes =        GPT.hex2Bytes("00");
                                                startSectorBytes =      GPT.hex2Bytes("00");
                                                startCylinderBytes =    GPT.hex2Bytes("00");
                                                fileSystemIdBytes =     GPT.hex2Bytes("00"); // EE Unknown
                                                endHeadBytes =          GPT.hex2Bytes("00");
                                                endSectorBytes =        GPT.hex2Bytes("00"); // sector bits 0-5 cylinder bits 6-7
                                                endCylinderBytes =      GPT.hex2Bytes("00"); // lower 8 bits
                                                firstSectorBytes =      GPT.hex2Bytes("00 00 00 00"); // LBA 1
                                                totalSectorBytes =      GPT.hex2Bytes(GPT.getHexStringLittleEndian(0L, 4)); // LBA-0
                                                
//      462  (0x00)   16 bytes  During LBA 0    partition2                      00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
                                                partition2Bytes =       GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      478  (0x00)   16 bytes  During LBA 0    partition3                      00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
                                                partition3Bytes =       GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      494  (0x00)   16 bytes  During LBA 0    partition4                      00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
                                                partition4Bytes =       GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      496  (0x08)     2 bytes   During LBA 0  Magic Number 55 AA Confirming valid MBR to OS
                                                magicNumberBytes =      GPT.hex2Bytes("00 00");
    }

    public void read(Path rawDeviceFilePath)
    {
        byte[] bytes = new byte[(int)LENGTH]; bytes = new Device(ui).read(rawDeviceFilePath, LBA, this.LENGTH);
//      Offset        Length    When            Data
//      0  (0x00)     440 bytes During LBA 0    Bootloader bytes
//                                                                        bb.asReadOnlyBuffer().get(bootstrapBytes, 0, 440); bb.rewind();
                                                                        bootstrapBytes = GPT.get(bytes, 0, 440);
//                                                                        bootstrapBytes = GPT.byteListToByteArray(bl.subList(0, 0 + 440));
//      440  (0x00)   4 bytes   During LBA 0    Disk Serial Number
//                                                                        bb.asReadOnlyBuffer().get(diskSerialBytes, 440, 4); bb.rewind();
                                                                        diskSerialBytes = GPT.get(bytes, 440, 4);
//      444  (0x00)   2 bytes   During LBA 0    Reserved
//                                                                        bb.asReadOnlyBuffer().get(reservedBytes, 444, 2); bb.flip(); bb.rewind();
                                                                        reservedBytes = GPT.get(bytes, 444, 2);
                                                
//      446  (0x00)   16 bytes  During LBA 0    partition1                      00 00 01 00 EE FE FF FF 01 00 FF 7F 94 03 00 00 (unknown partition type)
//                                                                        bb.get(activePartitionFlag, 446, 1); bb.flip(); bb.rewind();
                                                                        activePartitionFlag = GPT.get(bytes, 446, 1);
//                                                                        bb.get(startHeadBytes, 447, 1);
                                                                        startHeadBytes = GPT.get(bytes, 447, 1);
//                                                                        bb.get(startSectorBytes, 448, 1);
                                                                        startSectorBytes = GPT.get(bytes, 448, 1);
//                                                                        bb.get(startCylinderBytes, 449, 1);
                                                                        startCylinderBytes = GPT.get(bytes, 449, 1);
//                                                                        bb.get(fileSystemIdBytes, 450, 1);
                                                                        fileSystemIdBytes = GPT.get(bytes, 450, 1);
//                                                                        bb.get(endHeadBytes, 451, 1);
                                                                        endHeadBytes = GPT.get(bytes, 451, 1);
//                                                                        bb.get(endSectorBytes, 452, 1);
                                                                        endSectorBytes = GPT.get(bytes, 452, 1);
//                                                                        bb.get(endCylinderBytes, 453, 1);
                                                                        endCylinderBytes = GPT.get(bytes, 453, 1);
//                                                                        bb.get(firstSectorBytes, 454, 4);
                                                                        firstSectorBytes = GPT.get(bytes, 454, 4);
//                                                                        bb.get(totalSectorBytes, 458, 4);
                                                                        totalSectorBytes = GPT.get(bytes, 458, 4);
                                                
//      462  (0x00)   16 bytes  During LBA 0    partition2                      00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
//                                                                        bb.get(partition2Bytes, 462, 16);
                                                                        partition2Bytes = GPT.get(bytes, 462, 16);
//      478  (0x00)   16 bytes  During LBA 0    partition3                      00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
//                                                                        bb.get(partition3Bytes, 478, 16);
                                                                        partition3Bytes = GPT.get(bytes, 478, 16);
//      494  (0x00)   16 bytes  During LBA 0    partition4                      00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
//                                                                        bb.get(partition4Bytes, 494, 16);
                                                                        partition4Bytes = GPT.get(bytes, 494, 16);
//      510  (0x08)     2 bytes   During LBA 0  Magic Number 55 AA Confirming valid MBR to OS
//                                                                        bb.get(magicNumberBytes, 510, 2);
                                                                        magicNumberBytes = GPT.get(bytes, 510, 2);
    }
    
    public void create(Path rawDeviceFilePath)
    {
        long deviceSize = GPT.getDeviceSize(ui, rawDeviceFilePath);
//      Offset        Length    When            Data
//      0  (0x00)     440 bytes During LBA 0    Bootloader bytes
                                                bootstrapBytes =        GPT.getZeroBytes(440); // 440
//      440  (0x00)   4 bytes   During LBA 0    Disk Serial Number
                                                diskSerialBytes =       GPT.hex2Bytes("00 00 00 00");
//      444  (0x00)   2 bytes   During LBA 0    Reserved
                                                reservedBytes =         GPT.hex2Bytes("00 00");
                                                
//      446  (0x00)   16 bytes  During LBA 0    partition1                      00 00 01 00 EE FE FF FF 01 00 FF 7F 94 03 00 00 (unknown partition type)
                                                activePartitionFlag =   GPT.hex2Bytes("00"); // 0x80 = Active
                                                startHeadBytes =        GPT.hex2Bytes("00");
                                                startSectorBytes =      GPT.hex2Bytes("01");
                                                startCylinderBytes =    GPT.hex2Bytes("00");
                                                fileSystemIdBytes =     GPT.hex2Bytes("EE"); // EE Unknown
                                                endHeadBytes =          GPT.hex2Bytes("FE");
                                                endSectorBytes =        GPT.hex2Bytes("FF"); // sector bits 0-5 cylinder bits 6-7
                                                endCylinderBytes =      GPT.hex2Bytes("FF"); // lower 8 bits
                                                firstSectorBytes =      GPT.hex2Bytes("01 00 00 00"); // LBA 1
                                                totalSectorBytes =      GPT.hex2Bytes(GPT.getHexStringLittleEndian((deviceSize - (long)Device.bytesPerSector) / (long)Device.bytesPerSector, 4)); // LBA-0
                                                
//      462  (0x00)   16 bytes  During LBA 0    partition2                      00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
                                                partition2Bytes =       GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      478  (0x00)   16 bytes  During LBA 0    partition3                      00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
                                                partition3Bytes =       GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      494  (0x00)   16 bytes  During LBA 0    partition4                      00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
                                                partition4Bytes =       GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      496  (0x08)     2 bytes   During LBA 0  Magic Number 55 AA Confirming valid MBR to OS
                                                magicNumberBytes =      GPT.hex2Bytes("55 AA");
    }

    public byte[] get(int off, int length) { return GPT.get(get(), off, length); }
    public byte[] get()
    {
        List<Byte> definitiveByteList = new ArrayList<Byte>();
        for (byte mybyte: bootstrapBytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: diskSerialBytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: reservedBytes)        { definitiveByteList.add(mybyte); }
        for (byte mybyte: activePartitionFlag)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: startHeadBytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: startSectorBytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: startCylinderBytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: fileSystemIdBytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: endHeadBytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: endSectorBytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: endCylinderBytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: firstSectorBytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: totalSectorBytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: partition2Bytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: partition3Bytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: partition4Bytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: magicNumberBytes)     { definitiveByteList.add(mybyte); }
        
        return GPT.byteListToByteArray(definitiveByteList);
    }

    public void write(Path rawDeviceFilePath) { new Device(ui).write(get(), rawDeviceFilePath, LBA); }

    public void print() { ui.log(toString()); }
    
    public String toString()
    {
        String returnString = "";
        String hexString = "";
        returnString += ("\n");
        returnString += ("[ LBA " + LBA + " Protective MBR ]\n");
        returnString += ("\n");
        returnString += (String.format("%-50s", "bootLoaderBytes")); for (byte mybyte: bootstrapBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = ""; returnString += ("\n");
        returnString += (String.format("%-50s", "diskSerialBytes"));  for (byte mybyte: diskSerialBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = ""; returnString += ("\n");
        returnString += (String.format("%-50s", "reservedBytes"));    for (byte mybyte: reservedBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = ""; returnString += ("\n");
        returnString += (String.format("%-50s", "activePartitionFlag"));  for (byte mybyte: activePartitionFlag) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "startHeadBytes"));  for (byte mybyte: startHeadBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "startSectorBytes"));  for (byte mybyte: startSectorBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "startCylinderBytes"));  for (byte mybyte: startCylinderBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "fileSystemIdBytes"));  for (byte mybyte: fileSystemIdBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "endHeadBytes"));  for (byte mybyte: endHeadBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "endSectorBytes"));  for (byte mybyte: endSectorBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "endCylinderBytes"));  for (byte mybyte: endCylinderBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "firstSectorBytes"));  for (byte mybyte: firstSectorBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "totalSectorBytes"));  for (byte mybyte: totalSectorBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "partition2Bytes"));  for (byte mybyte: partition2Bytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "partition3Bytes"));  for (byte mybyte: partition3Bytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "partition4Bytes"));  for (byte mybyte: partition4Bytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\n");
        returnString += (String.format("%-50s", "magicNumberBytes")); for (byte mybyte: magicNumberBytes) { hexString += GPT.getHexString(mybyte, "2"); } returnString += (String.format("%-20s", hexString)); hexString = "";  returnString += ("\n");
        return returnString;
    }    
}
