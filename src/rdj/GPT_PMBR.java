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

import java.util.ArrayList;
import java.util.List;

public class GPT_PMBR // Protective MBR
{
    private final long ABSTRACT_LBA = 0L;
    private String DESCSTRING;
    private final long LENGTH = DeviceController.bytesPerSector * 1L;

    private byte[] bootcodeBytes;
    private byte[] diskSignatureBytes;
    private byte[] reservedBytes;
    private byte[] bootIndicatorBytes;
    private byte[] startingCHSBytes;
    private byte[] osTypeBytes;
    private byte[] endingCHSBytes;
    private byte[] startingLBABytes;
    private byte[] sizeInLBABytes;
    private byte[] partition2Bytes;
    private byte[] partition3Bytes;
    private byte[] partition4Bytes;
    private byte[] magicNumberBytes;
    private UI ui;

    public GPT_PMBR(UI ui)
    {
        this.ui = ui;
	
//	446
        bootcodeBytes = new byte[440];
        diskSignatureBytes = new byte[4];
        reservedBytes = new byte[2];
	
//	16
        bootIndicatorBytes = new byte[1];
        startingCHSBytes = new byte[3];
        osTypeBytes = new byte[1];
        endingCHSBytes = new byte[3];
        startingLBABytes = new byte[4];
        sizeInLBABytes = new byte[4];

//	48
	partition2Bytes = new byte[16];
        partition3Bytes = new byte[16];
        partition4Bytes = new byte[16];

//	2
	magicNumberBytes = new byte[2];
	clear();
    }

    public void clear()
    {
//      Offset        Length    When            Data
//      0  (0x00)     440 bytes During LBA 0    Bootloader bytes
                                                bootcodeBytes =		GPT.getZeroBytes(440); // 440
//      440  (0x00)   4 bytes   During LBA 0    Disk Serial Number
                                                diskSignatureBytes =    GPT.hex2Bytes("00 00 00 00");
//      444  (0x00)   2 bytes   During LBA 0    Reserved
                                                reservedBytes =         GPT.hex2Bytes("00 00");
                                                
//      446  (0x00)   16 bytes  During LBA 0    partition1              00 00 01 00 EE FE FF FF 01 00 FF 7F 94 03 00 00 (unknown partition type)
                                                bootIndicatorBytes =	GPT.hex2Bytes("00"); // 0x80 = Active
                                                startingCHSBytes =	GPT.hex2Bytes("000000"); // Cylinder Head Sectors Set to 0x000200, corresponding to the Starting LBA field.
                                                osTypeBytes =		GPT.hex2Bytes("00"); // EE Unknown
                                                endingCHSBytes =        GPT.hex2Bytes("000000"); // sector bits 0-5 cylinder bits 6-7
                                                startingLBABytes =      GPT.hex2Bytes("00 00 00 00"); // LBA 1
                                                sizeInLBABytes =	GPT.hex2Bytes(GPT.getHexStringLittleEndian(0L, 4)); // LBA-0
                                                
//      462  (0x00)   16 bytes  During LBA 0    partition2              00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
                                                partition2Bytes =       GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      478  (0x00)   16 bytes  During LBA 0    partition3              00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
                                                partition3Bytes =       GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      494  (0x00)   16 bytes  During LBA 0    partition4              00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
                                                partition4Bytes =       GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      496  (0x08)     2 bytes   During LBA 0  Magic Number 55 AA Confirming valid MBR to OS
                                                magicNumberBytes =      GPT.hex2Bytes("00 00");
	setDesc();
    }

    public void read(FCPath fcPath)
    {
        byte[] bytes = new byte[(int)LENGTH]; bytes = new DeviceController(ui).readLBA(fcPath, ABSTRACT_LBA, this.LENGTH);
//      Offset        Length    When            Data
//      0  (0x00)     440 bytes During LBA 0    Bootloader bytes
                                                                        bootcodeBytes = GPT.getBytesPart(bytes, 0, 440);
//      440  (0x00)   4 bytes   During LBA 0    Disk Serial Number
                                                                        diskSignatureBytes = GPT.getBytesPart(bytes, 440, 4);
//      444  (0x00)   2 bytes   During LBA 0    Reserved
                                                                        reservedBytes = GPT.getBytesPart(bytes, 444, 2);
                                                
//      446  (0x00)   16 bytes  During LBA 0    partition1              00 00 01 00 EE FE FF FF 01 00 FF 7F 94 03 00 00 (unknown partition type)
//      446            1
                                                                        bootIndicatorBytes = GPT.getBytesPart(bytes, 446, 1);
//      447            3
                                                                        startingCHSBytes = GPT.getBytesPart(bytes, 447, 3);
//      450,           1
                                                                        osTypeBytes = GPT.getBytesPart(bytes, 450, 1);
//      451            3
                                                                        endingCHSBytes = GPT.getBytesPart(bytes, 451, 3);
//      454            4
                                                                        startingLBABytes = GPT.getBytesPart(bytes, 454, 4);
//      458	       4
                                                                        sizeInLBABytes = GPT.getBytesPart(bytes, 458, 4);
                                                
//      462  (0x00)   16 bytes  During LBA 0    partition2              00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
                                                                        partition2Bytes = GPT.getBytesPart(bytes, 462, 16);
//      478  (0x00)   16 bytes  During LBA 0    partition3              00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
                                                                        partition3Bytes = GPT.getBytesPart(bytes, 478, 16);
//      494  (0x00)   16 bytes  During LBA 0    partition4              00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
                                                                        partition4Bytes = GPT.getBytesPart(bytes, 494, 16);
//      510  (0x08)     2 bytes   During LBA 0  Magic Number 55 AA Confirming valid MBR to OS
                                                                        magicNumberBytes = GPT.getBytesPart(bytes, 510, 2);
	setDesc();
    }
    
    public void create(FCPath fcPath)
    {
//      Offset        Length    When            Data
//      0  (0x00)     440 bytes During LBA 0    Bootloader bytes
                                                bootcodeBytes =		GPT.getZeroBytes(440); // 440
//      440  (0x00)   4 bytes   During LBA 0    Disk Serial Number
                                                diskSignatureBytes =    GPT.hex2Bytes("00 00 00 00");
//      444  (0x00)   2 bytes   During LBA 0    Reserved
                                                reservedBytes =         GPT.hex2Bytes("00 00");
                                                
//      446  (0x00)   16 bytes  During LBA 0    partition1              00 00 01 00 EE FE FF FF 01 00 FF 7F 94 03 00 00 (unknown partition type)
                                                bootIndicatorBytes =	GPT.hex2Bytes("00"); // 0x80 = Active
                                                startingCHSBytes =	GPT.hex2Bytes("000100"); // Set to 0x000200, corresponding to the Starting LBA field.
                                                osTypeBytes =		GPT.hex2Bytes("EE"); // EE Protective / Unknown
                                                endingCHSBytes =        GPT.hex2Bytes("FEFFFF"); // FE FF FF
                                                startingLBABytes =      GPT.hex2Bytes("01 00 00 00"); // LBA 1
                                                sizeInLBABytes =	GPT.hex2Bytes(GPT.getHexStringLittleEndian((fcPath.size - (long)DeviceController.bytesPerSector) / (long)DeviceController.bytesPerSector, 4)); // LBA-0
                                                
//      462  (0x00)   16 bytes  During LBA 0    partition2              00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
                                                partition2Bytes =       GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      478  (0x00)   16 bytes  During LBA 0    partition3              00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
                                                partition3Bytes =       GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      494  (0x00)   16 bytes  During LBA 0    partition4              00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 (notn)
                                                partition4Bytes =       GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      496  (0x08)     2 bytes   During LBA 0  Magic Number 55 AA Confirming valid MBR to OS
                                                magicNumberBytes =      GPT.hex2Bytes("55 AA");
	setDesc();
    }

//    public void write(Device device) { new DeviceController(ui).writeLBA(getDesc(), getBytes(), device, ABSTRACT_LBA); }
    public void write(FCPath fcPath) { new DeviceController(ui).writeLBA(getDesc(), getBytes(), fcPath, ABSTRACT_LBA); }

    public byte[] getBytes(int off, int length) { return GPT.getBytesPart(getBytes(), off, length); }
    public byte[] getBytes()
    {
        List<Byte> definitiveByteList = new ArrayList<Byte>();
        for (byte mybyte: bootcodeBytes)	{ definitiveByteList.add(mybyte); }
        for (byte mybyte: diskSignatureBytes)   { definitiveByteList.add(mybyte); }
        for (byte mybyte: reservedBytes)        { definitiveByteList.add(mybyte); }
        for (byte mybyte: bootIndicatorBytes)	{ definitiveByteList.add(mybyte); }
        for (byte mybyte: startingCHSBytes)	{ definitiveByteList.add(mybyte); }
        for (byte mybyte: osTypeBytes)		{ definitiveByteList.add(mybyte); }
        for (byte mybyte: endingCHSBytes)	{ definitiveByteList.add(mybyte); }
        for (byte mybyte: startingLBABytes)     { definitiveByteList.add(mybyte); }
        for (byte mybyte: sizeInLBABytes)	{ definitiveByteList.add(mybyte); }
        for (byte mybyte: partition2Bytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: partition3Bytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: partition4Bytes)      { definitiveByteList.add(mybyte); }
        for (byte mybyte: magicNumberBytes)     { definitiveByteList.add(mybyte); }
        
        return GPT.byteListToByteArray(definitiveByteList);
    }

    public void print() { ui.log(toString()); }
    
    private void setDesc() { DESCSTRING = ("[ LBA " + ABSTRACT_LBA + " - Protective MBR (" + getBytes().length + " Bytes) Storage: " + GPT.getLBAHumanSize(sizeInLBABytes,1) + " ]"); }
    private String getDesc() { return DESCSTRING; }

    @Override
    public String toString()
    {
        String returnString = "";
        returnString += ("\r\n");
        returnString += ("========================================================================\r\n");
        returnString += ("\r\n");
        returnString += DESCSTRING + "\r\n";
        returnString += ("\r\n");
	returnString += (String.format("%-25s", "BootCode"));		    returnString += GPT.getHexAndDecimal(bootcodeBytes, false) + "\r\n";
        returnString += (String.format("%-25s", "Disk Signature"));	    returnString += GPT.getHexAndDecimal(diskSignatureBytes, false) + "\r\n";
        returnString += (String.format("%-25s", "Reserved"));		    returnString += GPT.getHexAndDecimal(reservedBytes, false) + "\r\n";

//	Partition 1
	returnString += (String.format("%-25s", "BootIndicator"));	    returnString += GPT.getHexAndDecimal(bootIndicatorBytes, false) + "\r\n";
        returnString += (String.format("%-25s", "StartCHS"));		    returnString += GPT.getHexAndDecimal(startingCHSBytes, true) + "\r\n";
        returnString += (String.format("%-25s", "OSType"));		    returnString += GPT.getHexAndDecimal(osTypeBytes, false) + "\r\n";
        returnString += (String.format("%-25s", "EndingCHS"));		    returnString += GPT.getHexAndDecimal(endingCHSBytes, true) + "\r\n";
        returnString += (String.format("%-25s", "StartingLBA"));	    returnString += GPT.getHexAndDecimal(startingLBABytes, true) + "\r\n";
        returnString += (String.format("%-25s", "SizeInLBA"));		    returnString += GPT.getHexAndDecimal(sizeInLBABytes, true) + "\r\n";

//	Partition 2,3,4
	returnString += (String.format("%-25s", "Partition2"));		    returnString += GPT.getHexAndDecimal(partition2Bytes, false) + "\r\n";
        returnString += (String.format("%-25s", "Partition3"));		    returnString += GPT.getHexAndDecimal(partition3Bytes, false) + "\r\n";
        returnString += (String.format("%-25s", "Partition4"));		    returnString += GPT.getHexAndDecimal(partition4Bytes, false) + "\r\n";
        returnString += (String.format("%-25s", "MagicNumber"));	    returnString += GPT.getHexAndDecimal(magicNumberBytes, false) + "\r\n";
        return returnString;
    }    
}
