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
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.zip.CRC32;
import javax.xml.bind.DatatypeConverter;

public class GPT
{
    UI ui;
    public GPT_Protective_MBR           gpt_Protective_MBR;
    public GPT_Header                   gpt_Header;
    public GPT_Entries                  gpt_Entries;
    public GPT_Header_Backup            gpt_Header_Backup;
    public GPT_Entries_Backup           gpt_Entries_Backup;
    
    private int printAddressByteCounter;

    public GPT(UI ui)
    {
        this.ui = ui;
        
        gpt_Protective_MBR =            new GPT_Protective_MBR(this.ui);
        gpt_Entries =                   new GPT_Entries(this.ui);
        gpt_Header =                    new GPT_Header(this.ui, this);
        gpt_Entries_Backup =            new GPT_Entries_Backup(this.ui, this);
        gpt_Header_Backup =             new GPT_Header_Backup(this.ui, this);
        
        reset();
    }
    
    synchronized public void reset()
    {
        gpt_Protective_MBR.reset();
        gpt_Entries.reset();
        gpt_Header.reset();
        gpt_Entries_Backup.reset();
        gpt_Header_Backup.reset();
    }
    
    synchronized public void read(Path rawDeviceFilePath)
    {
        gpt_Protective_MBR.read(rawDeviceFilePath);
        gpt_Entries.read(rawDeviceFilePath);
        gpt_Header.read(rawDeviceFilePath);
        gpt_Entries_Backup.read(rawDeviceFilePath);
        gpt_Header_Backup.read(rawDeviceFilePath);
    }
    
//    synchronized public void create(Path cipherFilePath, Path rawDeviceFilePath)
    synchronized public void create(long cipherSize, Path rawDeviceFilePath)
    {
	gpt_Protective_MBR.create(rawDeviceFilePath);
	gpt_Entries.create(cipherSize);                 // Create order: 1
	gpt_Entries_Backup.create(cipherSize);          // Create order: 2
	gpt_Header.create(rawDeviceFilePath);           // Create order: 3
	gpt_Header_Backup.create(rawDeviceFilePath);    // Create order: 4
    }
    
    synchronized public void write(Path rawDeviceFilePath)
    {
        gpt_Protective_MBR.write(rawDeviceFilePath);
        gpt_Header.write(rawDeviceFilePath);
        gpt_Entries.write(rawDeviceFilePath);
        gpt_Entries_Backup.write(rawDeviceFilePath);
        gpt_Header_Backup.write(rawDeviceFilePath);
    }
    
    synchronized public void writeCipher(Path cipherFilePath, Path targetDeviceFilePath)        { gpt_Entries.writeCipher(cipherFilePath, targetDeviceFilePath); }
    synchronized public void cloneCipher(Path cipherDeviceFilePath, Path targetDeviceFilePath)  { gpt_Entries.cloneCipher(cipherDeviceFilePath, targetDeviceFilePath); }
    
    public String toString()
    {
        String returnString = "";
        returnString += gpt_Protective_MBR.toString();
        returnString += gpt_Header.toString();
        returnString += gpt_Entries.toString();
        returnString += gpt_Entries_Backup.toString();
        returnString += gpt_Header_Backup.toString();
        return returnString;
    }    
    
    public void print()
    {
        ui.log(this.toString());
    }    
    
    public GPT_Protective_MBR   get_GPT_Protective_MBR()    { return gpt_Protective_MBR; }
    public GPT_Header           get_GPT_Header()            { return gpt_Header; }
    public GPT_Entries          get_GPT_Entries()           { return gpt_Entries; }
    public GPT_Header_Backup    get_GPT_Header_Backup()     { return gpt_Header_Backup; }
    public GPT_Entries_Backup   get_GPT_Entries_Backup()    { return gpt_Entries_Backup; }

    public static byte[] byteListToByteArray(List<Byte> list) { byte[] result = new byte[list.size()]; for(int i = 0; i < list.size(); i++) { result[i] = list.get(i).byteValue(); } return result; }
    public static byte[] getUUID() { UUID uuid = UUID.randomUUID(); ByteBuffer bb = ByteBuffer.allocate(16); bb.putLong(uuid.getMostSignificantBits()); bb.putLong(uuid.getLeastSignificantBits()); return bb.array(); }

    synchronized public static byte[] getCRC32(String string, byte[] bytes, boolean littleEndian) // Byte order to Little Endian
    {
        byte [] bigEndianBytes = new byte[4];
        byte [] littleEndianBytes = new byte[4];
        CRC32 crc = new CRC32(); crc.update(bytes);
        
        bigEndianBytes = ByteBuffer.allocate(4).putInt((int) crc.getValue()).array();
        littleEndianBytes = getByteArrayLittleEndian(ByteBuffer.allocate(4).putInt((int) crc.getValue()).array());
//        System.out.println(string + getHexString(littleEndianBytes, "2"));
        if (littleEndian)   { return littleEndianBytes; }
        else                { return bigEndianBytes; }
    }

    synchronized public static byte[] getByteArrayLittleEndian(byte[] bytes) // Reverses byte order (to Little Endian)
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
    
    synchronized public static byte[] hex2Bytes(String string) { byte[] bytes = DatatypeConverter.parseHexBinary(string.replaceAll("[^A-Za-z0-9]","")); return bytes; }
    
    synchronized public static void logBytes(byte[] bytes) { for (byte mybyte: bytes) { logByte(mybyte); } }
    
    synchronized public static void logByte(byte dataByte)
    {
//        String adrhex = getHexString(printAddressByteCounter,"8");

        String datbin = getBinaryString(dataByte);
        String dathex = getHexString(dataByte, "2");
        String datdec = getDecString(dataByte);
        String datchr = getChar(dataByte);
        
//        System.out.print("| " + adrhex + " | " + datbin + " " +  dathex + " " + datdec + " " + datchr + " |\r\n" );
        System.out.print("| " + datbin + " " +  dathex + " " + datdec + " " + datchr + " |\r\n" );
//        printAddressByteCounter++;
    }
    
    synchronized public static byte[] get(byte[] source, int off, int length)
    {
        byte[] dest = new byte[length];
        for (int pos = off; pos < off + length; pos++)
        {
//            System.out.println("dest["+ (pos - off) + "] = source[" + pos + "]");
            dest[pos - off] = source[pos];
        }
        return dest;
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
    synchronized public static String getHexString(byte[] bytes, String digits) { String returnString = ""; for (byte mybyte:bytes) { returnString += getHexString(mybyte, digits); } return returnString; }
    synchronized public static String getHexString(byte value, String digits) { return String.format("%0" + digits + "X", (value & 0xFF)).replaceAll("[^A-Za-z0-9]",""); }
    synchronized public static String getHexString(int value, String digits) { return String.format("%0" + digits + "X", (value & 0xFF)).replaceAll("[^A-Za-z0-9]",""); }
    synchronized public static String getHexString(long value, String digits) { return String.format("%0" + digits + "X", (value)).replaceAll("[^A-Za-z0-9]",""); }
    
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
    
    synchronized public boolean validateIntegerString(String text) { try { Integer.parseInt(text); return true;} catch (NumberFormatException e) { return false; } }

    synchronized public void fileStoreInfo()
    {
//      Read Device
        
        FileSystem fs = FileSystems.getDefault();
        Iterable<FileStore> stores = fs.getFileStores();
//        Iterable<FileStore> stores2 = FileStore;
        ui.log("\r\n");
        ui.log(String.format("%-70s", "Name"));
        ui.log(String.format("%-20s", "Type"));
        ui.log(String.format("%-20s", "Tot"));
        ui.log(String.format("%-20s", "Used"));
        ui.log(String.format("%-20s", "Avail"));
        ui.log(String.format("%-20s", "rd-Only"));
        ui.log("\r\n");
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
                    ui.log(String.format("%-70s", store.name()));
                    ui.log(String.format("%-20s", store.type()));
                    ui.log(String.format("%-20s", getHumanSize(total_space, 1)));
                    ui.log(String.format("%-20s", getHumanSize(used_space,1)));
                    ui.log(String.format("%-20s", getHumanSize(available_space,1)));
                    ui.log(String.format("%-20s", is_read_only));
                    ui.log("\r\n");
                }
            }
            catch (IOException e) { System.err.println(e); }
        }
    }

    public static long getCipherFileSize(UI ui, Path cipherFilePath)
    {
        long cipherSize = 0;
        try { cipherSize = (long)Files.size(cipherFilePath); } catch (IOException ex) { ui.log("Files.size(finalCrypt.getCipherFilePath()) " + ex.getMessage() + "\r\n"); }
        return cipherSize;
    }

//  Get size of device        
    synchronized public static long getDeviceSize(UI ui, Path rawDeviceFilePath)
    {
        long deviceSize = 0;
        try (final SeekableByteChannel deviceChannel = Files.newByteChannel(rawDeviceFilePath, EnumSet.of(StandardOpenOption.READ))) { deviceSize = deviceChannel.size(); deviceChannel.close(); }
        catch (IOException ex) { ui.status(ex.getMessage(), true); }
        
        return deviceSize;
    }
}
