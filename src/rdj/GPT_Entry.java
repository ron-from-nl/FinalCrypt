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

public class GPT_Entry
{
    private final long          ABSTRACT_LBA; // = Primary GPT Header: 2L; Secondary GPT Header: -33
    private String		DESCSTRING;
    private final long          FIRST_LBA = 2048L;
    private final String        ENTRYCLASS;
    private final int           ENTRYNUMBER; // 0 - 127
    private final long          LENGTH = 128L;
    private       long          pos; // The Entry position / offset

    private byte[]              partitionTypeGUIDBytes;
    public  byte[]              uniquePartitionGUIDBytes;
    public long                 startingLBA;
    private byte[]              startingLBABytes;
    public long                 endingLBA;
    public long                 keySizeLBA;
    private byte[]              endingLBABytes;
    private byte[]              attributesBytes;
    private byte[]              partitionNameBytes;
    public long			partSize = 0;
    private UI ui;
    private GPT gpt;

    public GPT_Entry(UI ui, GPT gpt, long abstractLBA, int EntryNumber)
    {
        this.ui = ui;
        this.gpt = gpt;
	this.ABSTRACT_LBA = abstractLBA;
	this.ENTRYNUMBER = EntryNumber;
	if ( ABSTRACT_LBA >= 0 ) { ENTRYCLASS = "Primary"; } else { ENTRYCLASS = "Secondary"; }
	
	clear();
    }
    
    public void clear()
    {
//      Offset      Length      When            Data
//      0 (0x00)    16 bytes    During LBA 2    Partition type GUID
                                                partitionTypeGUIDBytes =		    GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      16 (0x10)   16 bytes    During LBA 2    Unique partition GUID
                                                uniquePartitionGUIDBytes =		    GPT.hex2Bytes("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
//      32 (0x20)   8 bytes     During LBA 2    First LBA (little endian) LBA 2048
                                                startingLBA =				    0L;
                                                startingLBABytes =			    GPT.hex2Bytes(GPT.getHexStringLittleEndian(startingLBA, 8)); // hex2Bytes("00 08 00 00 00 00 00 00");
//      40 (0x28)   8 bytes     During LBA 2    Last LBA (inclusive, usually odd)
                                                endingLBA =				    0L;
                                                endingLBABytes =			    GPT.hex2Bytes(GPT.getHexStringLittleEndian(endingLBA, 8));
//      48 (0x30)   8 bytes     During LBA 2    Attribute flags (e.g. bit 60 denotes read-only)
                                                attributesBytes =			    GPT.hex2Bytes("00 00 00 00 00 00 00 00");
//      56 (0x38)   72 bytes    During LBA 2    Partition name (36 UTF-16LE code units)
                                                partitionNameBytes =			    GPT.getZeroBytes(72);
	setDesc();
    }

   public void read(FCPath keyFCPath)
    {
	pos = ((DeviceController.getLBAOffSet(DeviceController.bytesPerSector, keyFCPath.size, ABSTRACT_LBA)) + (ENTRYNUMBER * LENGTH));
        byte[] bytes = new byte[(int)LENGTH]; bytes = new DeviceController(ui).readPos(keyFCPath, pos, LENGTH);
//      Offset      Length      When            Data
//      0 (0x00)    16 bytes    During LBA 2    Partition type GUID
                                                partitionTypeGUIDBytes =		    GPT.getBytesPart(bytes, 0, 16);
//      16 (0x10)   16 bytes    During LBA 2    Unique partition GUID
                                                uniquePartitionGUIDBytes =		    GPT.getBytesPart(bytes, 16, 16);
//      32 (0x20)   8 bytes     During LBA 2    First LBA (little endian) LBA 2048
                                                startingLBABytes =			    GPT.getBytesPart(bytes, 32, 8); startingLBA = Long.reverseBytes(GPT.bytesToLong(startingLBABytes));
//      40 (0x28)   8 bytes     During LBA 2    Last LBA (inclusive, usually odd)
                                                endingLBABytes =			    GPT.getBytesPart(bytes, 40, 8); endingLBA = Long.reverseBytes(GPT.bytesToLong(endingLBABytes));
//      48 (0x30)   8 bytes     During LBA 2    Attribute flags (e.g. bit 60 denotes read-only)
                                                attributesBytes =			    GPT.getBytesPart(bytes, 48, 8);
//      56 (0x38)   72 bytes    During LBA 2    Partition name (36 UTF-16LE code units)
                                                partitionNameBytes =			    GPT.getBytesPart(bytes, 56, 72);
        partSize = ((endingLBA - startingLBA) + 1 ) * DeviceController.bytesPerSector;
	setDesc();
    }
    
    public void create(long keySize, byte[] uniquePartitionGUIDBytes)
    {
        keySizeLBA =  (long)((Math.floor((keySize - 1L) / DeviceController.bytesPerSector )));
//      Offset      Length      When            Data
//      0 (0x00)    16 bytes    During LBA 2    Partition type GUID
                                                partitionTypeGUIDBytes =		    GPT.hex2Bytes("AF 3D C6 0F 83 84 72 47 8E 79 3D 69 D8 47 7D E4");
//      16 (0x10)   16 bytes    During LBA 2    Unique partition GUID
                                                this.uniquePartitionGUIDBytes = uniquePartitionGUIDBytes;
//      32 (0x20)   8 bytes     During LBA 2    First LBA (little endian) LBA 2048
                                                startingLBA =				    FIRST_LBA + (ENTRYNUMBER * keySizeLBA) + ENTRYNUMBER;
                                                startingLBABytes =			    GPT.hex2Bytes(GPT.getHexStringLittleEndian(startingLBA, 8));
//      40 (0x28)   8 bytes     During LBA 2    Last LBA (inclusive, usually odd)
                                                endingLBA =				    startingLBA + keySizeLBA;
                                                endingLBABytes =			    GPT.hex2Bytes(GPT.getHexStringLittleEndian(endingLBA, 8));
//      48 (0x30)   8 bytes     During LBA 2    Attribute flags (e.g. bit 60 denotes read-only)
                                                attributesBytes =			    GPT.hex2Bytes("00 00 00 00 00 00 00 00");
//      56 (0x38)   72 bytes    During LBA 2    Partition name (36 UTF-16LE code units)
                                                partitionNameBytes =			    GPT.getZeroBytes(72);
        partSize = ((endingLBA - startingLBA) + 1 ) * DeviceController.bytesPerSector;
	setDesc();
    }
    
    public void write(FCPath fcPath)						{ pos = ((DeviceController.getLBAOffSet(DeviceController.bytesPerSector, fcPath.size, ABSTRACT_LBA)) + (ENTRYNUMBER * LENGTH));
										  new DeviceController(ui).writePos(getDesc(), getBytes(), fcPath, pos); } // Causes exeption on OSX
    public void writeKeyPartitions(FCPath keyFCPath, FCPath targetFCPath)	{ new DeviceController(ui).createManualKeyPartition(keyFCPath, targetFCPath, startingLBA, endingLBA); }
    public void cloneKeyPartition(FCPath keyFCPath, FCPath targetFCPath)	{ new DeviceController(ui).cloneKeyPartition(keyFCPath, targetFCPath, startingLBA, endingLBA); }
    
        
    public byte[] getBytes(int off, int length) { return GPT.getBytesPart(GPT_Entry.this.getBytes(), off, length); }
    public byte[] getBytes()
    {
        List<Byte> byteList = new ArrayList<Byte>();
        for (byte mybyte: partitionTypeGUIDBytes)   { byteList.add(mybyte); }
        for (byte mybyte: uniquePartitionGUIDBytes) { byteList.add(mybyte); }
        for (byte mybyte: startingLBABytes)         { byteList.add(mybyte); }
        for (byte mybyte: endingLBABytes)           { byteList.add(mybyte); }
        for (byte mybyte: attributesBytes)	    { byteList.add(mybyte); }
        for (byte mybyte: partitionNameBytes)       { byteList.add(mybyte); }
        return GPT.byteListToByteArray(byteList);
    }
    
    public void print() { ui.log(toString(), true, true, true, false, false); }
    
    private void setDesc()			    { DESCSTRING = ("[ " + ENTRYCLASS + " Entry " + ENTRYNUMBER + " Pos " + pos + " (" + getBytes().length + " Bytes) Partition: " + GPT.getHumanSize(partSize,1) + " ]"); }
    private String getDesc()			    { return DESCSTRING; }
    
    @Override
    public String toString()
    {
	String returnString = "";
	if ( (startingLBA + endingLBA) != 0 )
	{
	    if ( ENTRYNUMBER != 0 ) { returnString += ("\r\n"); }
            returnString += ("\r\n");
	    returnString += ("------------------------------------------------------------------------\r\n");
            returnString += ("\r\n");
	    returnString += DESCSTRING + "\r\n";
	    returnString += ("\r\n");
	    returnString += (String.format("%-25s", "PartttionTypeGUID"));	    returnString += GPT.getHexAndDecimal(partitionTypeGUIDBytes, false) + "\r\n";
	    returnString += (String.format("%-25s", "UniquePartitionGUID"));	    returnString += GPT.getHexAndDecimal(uniquePartitionGUIDBytes, false) + "\r\n";
	    returnString += (String.format("%-25s", "StartingLBA"));		    returnString += GPT.getHexAndDecimal(startingLBABytes, true) + "\r\n";
	    returnString += (String.format("%-25s", "EndingLBA"));		    returnString += GPT.getHexAndDecimal(endingLBABytes, true) + "\r\n";
	    returnString += (String.format("%-25s", "Attributes"));		    returnString += GPT.getHexAndDecimal(attributesBytes, false) + "\r\n";
	    returnString += (String.format("%-25s", "PartitionName"));		    returnString += GPT.getHexAndDecimal(partitionNameBytes, false) + "\r\n";
	}
        return returnString;
    }
}
