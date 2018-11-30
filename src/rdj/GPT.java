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

//        GUID Partition Table
//        
//        LBA+0  Protective MBR (Legacy)                                                                    (max   512 bytes)
//        LBA+1  Primary GPT Header - Pointer to LBA 2                                                      (max   512 bytes)
//        LBA+2  Partition Entry 1-4                                                                        (min 16384 bytes)
//        LBA+3  Partition Entrys 5-128 
//        LBA+34 Partition 1
//               Partition 2
//               Remaining Partitions
//        LBA-33 Partition Entry 1-4                                                                        (min 16384 bytes)
//        LBA-32 Partition Entry 5-128 
//        LBA-1  Secondary GPT Header - Pointer to LBA 2 ()

package rdj;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
//import javax.xml.bind.DatatypeConverter; // Removed because deprecated from java9 up

public class GPT
{
    UI ui;
    public GPT_PMBR	gpt_PMBR;
    public GPT_Header	gpt_Header1;
    public GPT_Entries	gpt_Entries1;
    public GPT_Header	gpt_Header2;
    public GPT_Entries	gpt_Entries2;
    
    private int printAddressByteCounter;

    public GPT(UI ui)
    {
        this.ui = ui;
        
        gpt_PMBR =	new GPT_PMBR(this.ui);
        gpt_Header1 =   new GPT_Header(this.ui, this, 1L);
        gpt_Entries1 =  new GPT_Entries(this.ui,this,2L, 128);
        gpt_Header2 =   new GPT_Header(this.ui, this, -1L);
        gpt_Entries2 =  new GPT_Entries(this.ui,this,-33, 128);
    }
    
    synchronized public void clear()
    {
        gpt_PMBR.clear();
        gpt_Header1.clear();
        gpt_Entries1.clear();
        gpt_Header2.clear();
        gpt_Entries2.clear();
    }
    
    synchronized public void read(FCPath keyDevice)
    {
        gpt_PMBR.read(keyDevice);
        gpt_Header1.read(keyDevice); gpt_Entries1 =  new GPT_Entries(this.ui,this,2L, gpt_Header1.numberOfPartitionEntries);
        gpt_Entries1.read(keyDevice);
        gpt_Header2.read(keyDevice); gpt_Entries2 =  new GPT_Entries(this.ui,this,-33, gpt_Header2.numberOfPartitionEntries);
        gpt_Entries2.read(keyDevice);
    }
    
    synchronized public void create(long partitionSize, FCPath targetFCPath)
    {
	gpt_PMBR.create(targetFCPath);
	
	gpt_Header1.create(targetFCPath);
	gpt_Entries1.create(partitionSize);
	gpt_Header1.setCRC32Partitions();
	gpt_Header1.setHeaderCRC32Bytes();
	
	gpt_Header2.create(targetFCPath);
	gpt_Entries2.create(partitionSize);
	gpt_Header2.setCRC32Partitions();
	gpt_Header2.setHeaderCRC32Bytes();	
    }
    
    synchronized public void write(FCPath targetFCPath)
    {
        gpt_PMBR.write(targetFCPath);
        gpt_Header1.write(targetFCPath);
        gpt_Entries1.write(targetFCPath);
        gpt_Entries2.write(targetFCPath);
        gpt_Header2.write(targetFCPath);
    }
    
//    synchronized public void writeKey(Path keyFilePath, Device targetDevice)  { gpt_Entries1.writeKeyPartitions(keyFilePath, targetDevice); }
    synchronized public void createKeyPartitions(FCPath keyFCPath, FCPath targetFCPath)  { gpt_Entries1.createKeyPartitions(keyFCPath, targetFCPath); }
//    synchronized public void cloneKey(Device keyDevice, Device targetDevice)  { gpt_Entries1.cloneKeyPartitions(keyDevice, targetDevice); }
    synchronized public void cloneKeypartitions(FCPath keyFCPath, FCPath targetFCPath)  { gpt_Entries1.cloneKeyPartitions(keyFCPath, targetFCPath); }
    
    public GPT_PMBR	get_GPT_PMBR()	    { return gpt_PMBR; }
    public GPT_Header	get_GPT_Header1()   { return gpt_Header1; }
    public GPT_Entries	get_GPT_Entries1()  { return gpt_Entries1; }
    public GPT_Header	get_GPT_Header2()   { return gpt_Header2; }
    public GPT_Entries	get_GPT_Entries2()  { return gpt_Entries2; }

    public static byte[] byteListToByteArray(List<Byte> list) { byte[] result = new byte[list.size()]; for(int i = 0; i < list.size(); i++) { result[i] = list.get(i).byteValue(); } return result; }
    public static byte[] getUUID() { UUID uuid = UUID.randomUUID(); ByteBuffer bb = ByteBuffer.allocate(16); bb.putLong(uuid.getMostSignificantBits()); bb.putLong(uuid.getLeastSignificantBits()); return bb.array(); }

    synchronized public static byte[] getReverseBytes(byte[] bytes) // Reverses byte order (to Little Endian)
    {
        byte[] reversedBytes = new byte[bytes.length];
        
        for (int byteArrayCounter = bytes.length - 1; byteArrayCounter >=0 ; byteArrayCounter--) { reversedBytes[(bytes.length - 1) - byteArrayCounter] = bytes[byteArrayCounter]; }
        return reversedBytes;
    }
    
    synchronized public static byte[] getZeroBytes(int length)
    {
        byte zeroByte = (byte)0 & 0xFF;
        byte[] byteArray = new byte[length];
        for (int forCounter = 0; forCounter < length; forCounter++) { byteArray[forCounter] = 0; }
        return byteArray;
    }
    
//    Replaced hex2Bytes(String string) because deprecated from java9 up
//    synchronized public static byte[] hex2Bytes(String string) { byte[] bytes = DatatypeConverter.parseHexBinary(string.replaceAll("[^A-Za-z0-9]","")); return bytes; }
    
    public static byte[] hex2Bytes(String string)
    {
	string = string.replaceAll("[^A-Za-z0-9]",""); byte[] data = new byte[string.length() / 2];
	for (int stringpos = 0; stringpos < string.length(); stringpos += 2) { data[stringpos / 2] = (byte) ((Character.digit(string.charAt(stringpos), 16) << 4) + Character.digit(string.charAt(stringpos+1), 16)); }
	return data;
    }
    
    synchronized public static void logBytes(byte[] bytes) { for (byte mybyte: bytes) { logByte(mybyte); } }
    
    synchronized public static void logByte(byte dataByte)
    {
//        String adrhex = getHexString(printAddressByteCounter,"8");

        String datbin = getBinaryString(dataByte);
        String dathex = getHexString(dataByte, 2);
        String datdec = getDecString(dataByte);
        String datchr = getChar(dataByte);
        
//        System.out.print("| " + adrhex + " | " + datbin + " " +  dathex + " " + datdec + " " + datchr + " |\r\n" );
        System.out.print("| " + datbin + " " +  dathex + " " + datdec + " " + datchr + " |\r\n" );
//        printAddressByteCounter++;
    }
    
    synchronized public static byte[] getBytesPart(byte[] inBytes, int offset, int length)
    {
        byte[] outBytes = new byte[length];
        for (int position = offset; position < offset + length; position++)
        {
//            System.out.println("outBytes["+ (position - offset) + "] = inBytes[" + position + "]");
            outBytes[position - offset] = inBytes[position];
        }
        return outBytes;
    }

    synchronized public static int bytesToInteger(byte[] bytes)
    {
        ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
        bb.put(bytes);
        bb.flip(); 
        return bb.getInt();
    }
    
    synchronized public static long bytesToLong(byte[] bytes)
    {
        ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
        bb.put(bytes);
        bb.flip(); 
        return bb.getLong();
    }
    
    synchronized public static String getBinaryString(Byte value) { return String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0'); }
    synchronized public static String getDecString(Byte value) { return String.format("%3d", (value & 0xFF)).replace(" ", "0").replaceAll("[^A-Za-z0-9]",""); }
    synchronized public static String getHexString(byte[] bytes, int digits) { String returnString = ""; for (byte mybyte:bytes) { returnString += getHexString(mybyte, digits); } return returnString; }
    synchronized public static String getHexString(byte value, int digits) { return String.format("%0" + Integer.toString(digits) + "X", (value & 0xFF)).replaceAll("[^A-Za-z0-9]",""); }
    synchronized public static String getHexString(int value, int digits) { return String.format("%0" + Integer.toString(digits) + "X", (value & 0xFF)).replaceAll("[^A-Za-z0-9]",""); }
    synchronized public static String getHexString(long value, int digits) { return String.format("%0" + Integer.toString(digits) + "X", (value)).replaceAll("[^A-Za-z0-9]",""); }
    
    synchronized public static String getHexStringLittleEndian(long value, int bytes) // Works correct
    {
        String returnString = ""; ByteBuffer buffer = ByteBuffer.allocate(8); buffer.putLong(value); buffer.flip();
        for (int x = 0; x < bytes; x++)
        {
//            ui.status("byte"+x + " " + ( getHexString(buffer.get(7-x) & 0xFF,"2"))); 
            returnString += String.format("%02X", ( buffer.get(7-x) & 0xFF));
        }
//            ui.status("hole " + returnString + " hole"); 
        buffer.clear();
        return returnString.replaceAll("[^A-Za-z0-9]","");
    }
    
    public static String getHexAndDecimal(byte[] bytes, boolean decimal)
    {
	long allbytes = 0;
	String returnString = "";
        String hexPrefix = ""; // 0x
        String hexString = "";

	for (byte mybyte: bytes) { allbytes += (long)mybyte; }
	if (allbytes == 0)
	{
	    returnString = "0 [" + bytes.length + "]";
	}
	else
	{
	    for (byte mybyte: bytes) { hexString += GPT.getHexString(mybyte, 2); } returnString += hexPrefix + (String.format("%-32s", hexString));
	    if (decimal) // Decimal Integer or Long
	    {
		if (bytes.length == 4) { returnString += " " + Integer.reverseBytes(GPT.bytesToInteger(bytes)); }
		if (bytes.length == 8) { returnString += " " + Long.reverseBytes(GPT.bytesToLong(bytes)); }
	    }
	    hexString = ""; returnString += ("");
	}
	return returnString;
    }
    
    synchronized public static String getChar(Byte myByte) { return String.format("%1s", (char) (myByte & 0xFF)).replaceAll("\\p{C}", "?"); }  //  (myByte & 0xFF); }
    
    synchronized public static String getHumanSize(double value,int decimals)
    {
        int x = 0;
        long factor;
        double newValue = value;
        String returnString = new String("");
        ArrayList<String> magnitude = new ArrayList<String>(); magnitude.addAll(Arrays.asList("ZiB","EiB","PiB","TiB","GiB","MiB","KiB","Bytes"));
        for (factor = 70; factor > 0; factor -= 10)
        {
            if ((value / Math.pow(2, factor)) >= 1) { newValue = (value / Math.pow(2, factor)); returnString = String.format("%.1f", (newValue)) + " " + magnitude.get(x); break; } x++;
        }
        if (factor == 0) { newValue = (value / Math.pow(2, factor)); returnString = String.format("%." + decimals + "f", (newValue)) + " " + magnitude.get(x); }
        return returnString;
    }
    
    synchronized public static String getLBAHumanSize(byte[] value,int decimals)
    {
	if (value.length == 4)  { return getHumanSize( Integer.reverseBytes(GPT.bytesToInteger(value)) * DeviceController.bytesPerSector,decimals ); }
	else			{ return getHumanSize( Long.reverseBytes(GPT.bytesToLong(value)) * DeviceController.bytesPerSector,decimals ); }
    }
    
    synchronized public boolean validateIntegerString(String text) { try { Integer.parseInt(text); return true;} catch (NumberFormatException e) { return false; } }

    public void print()	{ ui.log(toString(), false, true, true, false, false); }    

    @Override
    public String toString()
    {
        String returnString = "";
        returnString += gpt_PMBR.toString();
        returnString += gpt_Header1.toString();
        returnString += gpt_Entries1.toString();
        returnString += gpt_Entries2.toString();
        returnString += gpt_Header2.toString();
        return returnString;
    }    
        
    synchronized public void fileStoreInfo()
    {
//      Read Device
        
        FileSystem fs = FileSystems.getDefault();
        Iterable<FileStore> stores = fs.getFileStores();

	ui.log("\r\n", true, true, true, false, false);
        ui.log(String.format("%-70s", "Name"), true, true, true, false, false);
        ui.log(String.format("%-20s", "Type"), true, true, true, false, false);
        ui.log(String.format("%-20s", "Tot"), true, true, true, false, false);
        ui.log(String.format("%-20s", "Used"), true, true, true, false, false);
        ui.log(String.format("%-20s", "Avail"), true, true, true, false, false);
        ui.log(String.format("%-20s", "rd-Only"), true, true, true, false, false);
        ui.log("\r\n", true, true, true, false, false);
        for (FileStore store : stores)
        {
            try
            {
                long total_space = store.getTotalSpace();
                long used_space = (store.getTotalSpace() - store.getUnallocatedSpace());
                long available_space = store.getUsableSpace();
                boolean is_read_only = store.isReadOnly();
                if ( total_space > 0 )
                {
                    ui.log(String.format("%-70s", store.name()), true, true, true, false, false);
                    ui.log(String.format("%-20s", store.type()), true, true, true, false, false);
                    ui.log(String.format("%-20s", getHumanSize(total_space, 1)), true, true, true, false, false);
                    ui.log(String.format("%-20s", getHumanSize(used_space,1)), true, true, true, false, false);
                    ui.log(String.format("%-20s", getHumanSize(available_space,1)), true, true, true, false, false);
                    ui.log(String.format("%-20s", is_read_only), true, true, true, false, false);
                    ui.log("\r\n", true, true, true, false, false);
                }
            }
            catch (IOException e) { System.err.println(e); }
        }
    }
}
