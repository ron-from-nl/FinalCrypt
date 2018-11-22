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

public class GPT_Entries
{
    private final long  ABSTRACT_LBA; // = 2L;
    private String DESCSTRING;
    public GPT_Entry[]	gpt_entry;
    private UI ui;
    private GPT gpt;
    private long totalSize = 0;
    private final String HEADERCLASS;

    public GPT_Entries(UI ui, GPT gpt, long abstractLBA, int numOfEntries)
    {
        this.ui = ui;
        this.gpt = gpt;
	gpt_entry = new GPT_Entry[numOfEntries];
	this.ABSTRACT_LBA = abstractLBA;	
	for(int entry = 0; entry < gpt_entry.length; entry++)					    { gpt_entry[entry] = new GPT_Entry(this.ui,this.gpt,ABSTRACT_LBA,entry); }
	if ( ABSTRACT_LBA >= 0 ) { HEADERCLASS = "Primary"; } else { HEADERCLASS = "Secondary"; }
	setDesc();
    }
    
    public void		clear()									    { for(int entry = 0; entry < gpt_entry.length; entry++)   { gpt_entry[entry].clear(); } setDesc(); }
    public void		read(FCPath keyFCPath)						    { for(int entry = 0; entry < gpt_entry.length; entry++)   { gpt_entry[entry].read(keyFCPath); } setTotalSize(); setDesc(); }

    public void		create(long keySize)
    {
	if ( ABSTRACT_LBA >= 0 ) { gpt_entry[0].create(keySize, GPT.getUUID());
				   gpt_entry[1].create(keySize, GPT.getUUID()); }
	else			 { gpt_entry[0].create(keySize, gpt.gpt_Entries1.getEntry(0).uniquePartitionGUIDBytes);
				   gpt_entry[1].create(keySize, gpt.gpt_Entries1.getEntry(1).uniquePartitionGUIDBytes); } setTotalSize(); setDesc();
    }

    public void		write(FCPath targetFCPath)						    { new DeviceController(ui).writeLBA(getDesc(), getBytes(), targetFCPath, ABSTRACT_LBA); }

    public void		createKeyPartitions(FCPath keyFCPath, FCPath targetFCPath)		    { gpt_entry[0].writeKeyPartitions(keyFCPath, targetFCPath); }
    public void		cloneKeyPartitions(FCPath keyFCPath, FCPath targetFCPath)		    { gpt_entry[0].cloneKeyPartition(keyFCPath, targetFCPath); gpt_entry[1].cloneKeyPartition(keyFCPath, targetFCPath); }
    
    private int		getTotalEntries()							    { return gpt_entry.length; }
    private int		getActiveEntries()							    { int activeEntries = 0; for(int entry = 0; entry < gpt_entry.length; entry++)   { if ( gpt_entry[entry].startingLBA != 0 ) { activeEntries++; } } return activeEntries; }
    
    public byte[]	getBytes(int off, int length)						    { return GPT.getBytesPart(GPT_Entries.this.getBytes(), off, length); }
    public byte[]	getBytes()								    { List<Byte> byteList = new ArrayList<Byte>(); for (int entry = 0; entry < gpt_entry.length; entry++) { for (byte mybyte:gpt_entry[entry].getBytes()) { byteList.add(mybyte); } } return GPT.byteListToByteArray(byteList); }
    
    public GPT_Entry	getEntry(int entry)							    { return  gpt_entry[entry]; }
    
    public void		print()									    { ui.log(toString(), true, true, true, false, false); }
    
    private void	setTotalSize()								    { totalSize = 0; for(int entry = 0; entry < gpt_entry.length; entry++)   { totalSize += gpt_entry[entry].partSize; } }
    
    private void	setDesc() { DESCSTRING = ("[ LBA " + ABSTRACT_LBA + " - " + getActiveEntries() + "/" + getTotalEntries() + " " + HEADERCLASS + " Entries (" + getBytes().length + " Bytes) Partitions: " + GPT.getHumanSize(totalSize,1) + " ]"); }
    private String	getDesc() { return DESCSTRING; }

    @Override
    public String toString()
    {
	
	String returnString = "";
        returnString += ("\r\n");
        returnString += ("========================================================================\r\n");
        returnString += ("\r\n");
	returnString += DESCSTRING + "\r\n";
	for(int entry = 0; entry < gpt_entry.length; entry++)   { returnString += gpt_entry[entry].toString(); } return returnString; }
}
