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

public class GPT_Entries2
{
    public final long	LBA = -33L;
//    private final long	LENGTH = 128L * 128L;
    public GPT_Entry[]	gpt_entry;
    private UI ui;
    private GPT gpt;
    private long totalSize = 0;

    public GPT_Entries2(UI ui, GPT gpt)
    {
        this.ui = ui;
        this.gpt = gpt;
	gpt_entry = new GPT_Entry[128];
	for(int entry = 0; entry < gpt_entry.length; entry++)				    { gpt_entry[entry] = new GPT_Entry(ui,gpt,LBA,entry); }
    }

    public void clear()									    { for(int entry = 0; entry < gpt_entry.length; entry++)   { gpt_entry[entry].clear(); } }
    public void read(Path cipherDeviceFilePath)						    { for(int entry = 0; entry < gpt_entry.length; entry++)   { gpt_entry[entry].read(cipherDeviceFilePath); } setTotalSize(); }

    public void create(long cipherSize)							    { gpt_entry[0].create(cipherSize); gpt_entry[1].create(cipherSize); setTotalSize(); }

//    public void write(Path targetDeviceFilePath)					    { for(int entry = 0; entry < gpt_entry.length; entry++)   { gpt_entry[entry].write(targetDeviceFilePath); } }
    public void write(Path targetDeviceFilePath)					    { new Device(ui).writeLBA(GPT_Entries2.this.getBytes(), targetDeviceFilePath, LBA); }

    public void writeCipherPartitions(Path cipherFilePath, Path targetDeviceFilePath)	    { gpt_entry[0].writeCipherPartitions(cipherFilePath, targetDeviceFilePath); }
    public void cloneCipherPartitions(Path cipherDeviceFilePath, Path targetDeviceFilePath) { gpt_entry[0].cloneCipherPartition(cipherDeviceFilePath, targetDeviceFilePath); gpt_entry[1].cloneCipherPartition(cipherDeviceFilePath, targetDeviceFilePath); }
        
    private int getTotalEntries()							    { return gpt_entry.length; }
    private int getActiveEntries()							    { int activeEntries = 0; for(int entry = 0; entry < gpt_entry.length; entry++)   { if ( gpt_entry[entry].startingLBA != 0 ) { activeEntries++; } } return activeEntries; }

    public byte[] getBytes(int off, int length)						    { return GPT.getBytesPart(GPT_Entries2.this.getBytes(), off, length); }
    public byte[] getBytes()								    { List<Byte> byteList = new ArrayList<Byte>();for (int entry = 0; entry < gpt_entry.length; entry++) { for (byte mybyte:gpt_entry[entry].getBytes()) { byteList.add(mybyte); } } return GPT.byteListToByteArray(byteList); }
    
    public GPT_Entry getEntry(int entry)						    { return  gpt_entry[entry]; }
    
    public void print()									    { ui.log(toString()); }
    
    private void setTotalSize()								    { totalSize = 0; for(int entry = 0; entry < gpt_entry.length; entry++)   { totalSize += gpt_entry[entry].partSize; } }

    @Override
    public String toString()
    {
	String returnString = "";
        returnString += ("\r\n");
        returnString += ("========================================================================\r\n");
        returnString += ("\r\n");
	returnString += "[ LBA " + LBA + " - " + getActiveEntries() + "/" + getTotalEntries() + " Secondary Entries (" + GPT_Entries2.this.getBytes().length + " Bytes) Partitions: " + GPT.getLBAHumanSize(totalSize,1) + " ]\r\n";
//        returnString += ("\r\n");
	for(int entry = 0; entry < gpt_entry.length; entry++)   { returnString += gpt_entry[entry].toString(); } return returnString; }
}
