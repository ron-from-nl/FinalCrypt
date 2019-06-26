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

public class DeviceManager extends Thread
{
    private final UI ui;
        
    public DeviceManager(UI ui) { this.ui = ui; }
    
    public void createKeyDevice(FCPath keyFCPath, FCPath targetFCPath)
    {
//		      isValidFile(UI ui, String caller,  Path targetSourcePath, boolean isKey, boolean device, long minSize, boolean symlink, boolean writable, boolean report)
	if ( Validate.isValidFile(   ui,            "", targetFCPath.path, targetFCPath.isKey,	    true,  	    1L,           false,             true,           true) )
	{
	    ui.log("Creating Key Device: " + targetFCPath.path.toAbsolutePath().toString() + "\r\n", true, true, false, false, false);
	    GPT gpt = new GPT(ui);
//	    gpt.create(GPT.getKeyFileSize(ui, keyFilePath), targetDevice);
	    gpt.create(keyFCPath.size, targetFCPath);
	    gpt.write(targetFCPath);
	    gpt.createKeyPartitions(keyFCPath, targetFCPath);
	    gpt.print();
	    try { Thread.sleep(250); } catch (InterruptedException ex) {  }
	}
    }

    public void cloneKeyDevice(FCPath keyFCPath, FCPath targetFCPath)
    {
//		           isValidFile(UI ui, String caller,  Path targetSourcePath, boolean isKey,     boolean device, long minSize, boolean symlink, boolean writable, boolean report)
	if (
		( Validate.isValidFile(   ui,            "",	  keyFCPath.path, keyFCPath.isKey,	     true,	     1L,	   false,	     false,	     true) ) &&
		( Validate.isValidFile(   ui,            "",	  targetFCPath.path, targetFCPath.isKey,          true,	     1L,	   false,	      true,	     true) )
	    )
	{
	    ui.log("Cloning Key Device: " + keyFCPath.path.toAbsolutePath().toString() + " to " + targetFCPath.path.toAbsolutePath().toString() + "\r\n", true, true, false, false, false);
	    GPT gpt = new GPT(ui);
	    
//	    Either read (clone diskGUIDs & partitionGUIDs) or create (new diskGUIDs & partitionGUIDs)
//            gpt.read(keyDeviceFilePath); // Copies currentLBA and backupLBA which causes invalid headers on a different size USB Stick
	    gpt.create(DeviceController.getKeyPartitionSize(ui, keyFCPath), targetFCPath);
	    gpt.write(targetFCPath);
	    gpt.cloneKeypartitions(keyFCPath, targetFCPath);
	    gpt.print();
	    try { Thread.sleep(250); } catch (InterruptedException ex) {  }
	}
    }

//  Used by --gpt option
    public void printGPT(FCPath fcPath)
    {
//		      isValidFile(UI ui, String caller,  Path targetSourcePath, boolean isKey, boolean device, long minSize, boolean symlink, boolean writable, boolean report)
	if ( Validate.isValidFile(   ui,            "", fcPath.path,		fcPath.isKey,	  true,		  1L,		false,		  false,	   true) )
	{
//	    ui.status("Printing GUID Partition Table: " + keyDevice.getPath().toString() + "\r\n", true);
	    GPT gpt = new GPT(ui);
	    gpt.read(fcPath);
	    gpt.print();
	}
    }
    
    public void deleteGPT(FCPath fcPath)
    {
//		      isValidFile(UI ui, String caller,  Path targetSourcePath, boolean isKey, boolean device, long minSize, boolean symlink, boolean writable, boolean report)
	if ( Validate.isValidFile(   ui,	    "",		   fcPath.path,	 fcPath.isKey,	    true,	    1L,		  false,	     true,	    true) )
	{
//	    ui.status("Deleting GUID Partition Table: " + targetDevice.getPath().toString() + "\r\n", true);
	    GPT gpt = new GPT(ui);
	    gpt.write(fcPath);
	    gpt.read(fcPath);
	    gpt.print();
	}
    }
}