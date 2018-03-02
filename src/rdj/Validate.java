/*
 * Copyright © 2017 Ron de Jong (ronuitzaandam@gmail.com).
 *
 * This is free software; you can redistribute it 
 * under the terms of the Creative Commons License
 * Creative Commons License: (CC BY-NC-ND 4.0) as published by
 * https://creativecommons.org/licenses/by-nc-nd/4.0/; either
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

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.EnumSet;

public class Validate
{
    public static void checkCipher(UI ui, FinalCrypt finalcrypt, Path cipherPath)
    {
        State.cipherSelected = State.INVALID;
        State.cipherReady = false;

	long cipherSize = 0;

	if (
		(Files.isRegularFile(cipherPath))
	   )
	{
            try { cipherSize = (int)Files.size(cipherPath); } catch (IOException ex) { ui.error("StateControler.validateCipher Files.size(finalCrypt.getCipherPath()) " + ex.getMessage() + "\r\n"); }
	    if ( cipherSize > 0 )
	    {
		State.cipherSelected = State.FILE;
		State.cipherReady = true;
	    }
	    else
	    {
		State.cipherSelected = State.FILE;
		State.cipherReady = false;
	    }
	}
	else if (cipherPath.toAbsolutePath().toString().startsWith("/dev/sd")) // Linux Cipher Device Selection
	{
//	    ui.log("starts with /dev/sd: " + );
	    if (!cipherPath.getFileName().endsWith("sda"))
	    {
		if (isValidFile(ui, cipherPath, false, false, false))
		{
		    if (Character.isDigit(cipherPath.getFileName().toString().charAt(cipherPath.getFileName().toString().length()-1)))
		    {
			State.cipherSelected = State.PARTITION;
			State.cipherReady = true;
		    }
		    else
		    {
			State.cipherSelected = State.DEVICE;
		    }

		    State.cipherReady = true;
//                  Get size of partition
		    try (final SeekableByteChannel deviceChannel = Files.newByteChannel(cipherPath, EnumSet.of(StandardOpenOption.READ)))
		    { cipherSize = deviceChannel.size(); deviceChannel.close(); } catch (IOException ex) { ui.status(ex.getMessage(), true); }
		} else { ui.status("Probably no read permission on " + cipherPath + " execute: \"sudo usermod -a -G disk " + System.getProperty("user.name") + "\" and re-login your desktop and try again\r\n", true); }
	    }
	}
	else if (cipherPath.toAbsolutePath().toString().startsWith("/dev/disk")) // Apple Cipher Device Selection
	{
	    if (
		    ( ! cipherPath.getFileName().toString().endsWith("disk0"))
	       )
	    {
		if (isValidFile(ui, cipherPath, false, false, false))
		{
		    if (
			    (Character.isDigit(cipherPath.getFileName().toString().charAt(cipherPath.getFileName().toString().length()-1))) &&
			    (String.valueOf(cipherPath.getFileName().toString().charAt(cipherPath.getFileName().toString().length()-2)).equalsIgnoreCase("s"))
		       )
		    {
			State.cipherSelected = State.PARTITION;
		    }
		    else
		    {
			State.cipherSelected = State.DEVICE;
		    }

//                  Get size of device        
		    State.cipherReady = true;
		    try (final SeekableByteChannel deviceChannel = Files.newByteChannel(cipherPath, EnumSet.of(StandardOpenOption.READ)))
		    { cipherSize = deviceChannel.size(); deviceChannel.close(); } catch (IOException ex) { ui.status(ex.getMessage(), true); }
		}
		else { ui.status("Probably no read permission on " + cipherPath + " execute: \"sudo dseditgroup -o edit -a " + System.getProperty("user.name") + " -t user operator; sudo chmod g+w /dev/disk*\" and re-login your desktop and try again\r\n", true); }
	    } else { State.cipherReady = false; } // disk0
	}
	else
	{
	    State.cipherSelected = State.INVALID;
	    State.cipherReady = false;
	}
	if ((cipherSize > 0) && (cipherSize < finalcrypt.getBufferSize())) { finalcrypt.setBufferSize((int)cipherSize); }
    }

    public static void checkTarget(UI ui, FinalCrypt finalcrypt, ArrayList<Path> targetPathList, Path cipherFilePath, String pattern, boolean negatePattern, boolean symlink, boolean status, boolean printgpt, boolean deletegpt)
    {
//	Test for Cipher Device Target
	if ((targetPathList.size() == 1))
	{
	    if (targetPathList.get(0).toAbsolutePath().toString().startsWith("/dev/sd")) // Linux Cipher Device Device
	    {
		if  ( ! targetPathList.get(0).getFileName().toString().endsWith("sda")) // Not main disk
		{
		    if (isValidFile(ui, targetPathList.get(0), false, false, false))
		    {
			if (Character.isLetter(targetPathList.get(0).getFileName().toString().charAt(targetPathList.get(0).getFileName().toString().length()-1))) // Device selected
			{
			    State.targetSelected = State.DEVICE;
			    State.targetReady = true;
			    if (printgpt)   { DeviceManager deviceManager = new DeviceManager(ui); deviceManager.start(); deviceManager.printGPT(new Device(ui,targetPathList.get(0))); }
			    if (deletegpt)  { DeviceManager deviceManager = new DeviceManager(ui); deviceManager.start(); deviceManager.deleteGPT(new Device(ui,targetPathList.get(0))); }
			}
			else
			{
			    State.targetSelected = State.PARTITION;
			    State.targetReady = false;
			}
		    } else { ui.status("Probably no read & write permission on " + targetPathList.get(0).toString() + " execute: \"sudo usermod -a -G disk " + System.getProperty("user.name") + "\" and re-login your desktop and try again\r\n", true); }
		}
	    }
	    else if (targetPathList.get(0).toAbsolutePath().toString().startsWith("/dev/disk")) // Apple Cipher Device Device
	    {
		if ( ! targetPathList.get(0).getFileName().toString().endsWith("disk0")) // not primary disk
		{
		    if (isValidFile(ui, targetPathList.get(0), false, false, false))
		    {
			if (
				(Character.isDigit(targetPathList.get(0).getFileName().toString().charAt(targetPathList.get(0).getFileName().toString().length()-1))) && // last char = digit
				( ! String.valueOf(targetPathList.get(0).getFileName().toString().charAt(targetPathList.get(0).getFileName().toString().length()-2)).equalsIgnoreCase("s")) // ! slice
			   ) 
			{
			    State.targetSelected = State.DEVICE;
			    State.targetReady = true;                    
			    if (printgpt)   { DeviceManager deviceManager = new DeviceManager(ui); deviceManager.start(); deviceManager.printGPT(new Device(ui,targetPathList.get(0))); }
			    if (deletegpt)  { DeviceManager deviceManager = new DeviceManager(ui); deviceManager.start(); deviceManager.deleteGPT(new Device(ui,targetPathList.get(0))); }
			}
			else
			{
			    State.targetSelected = State.PARTITION;
			    State.targetReady = false;
			}
		    } else { ui.status("Probably no read & write permission on " + targetPathList.get(0) + " execute: \"sudo dseditgroup -o edit -a " + System.getProperty("user.name") + " -t user operator; sudo chmod g+w /dev/disk*\" and re-login your desktop and try again\r\n", true); }
		}
	    }
	    else // No Cipher Device Device Target selected
	    {
		State.targetSelected = State.INVALID;
		State.targetReady = false;                
	    }
	}

//      En/Disable hasEncryptableItems
	if (( State.cipherSelected != State.DEVICE ) && ( State.cipherReady )) // No need to scan for encryptable items without selected cipher for better performance
	{

//		String pattern = "glob:*"; try { pattern = getSelectedPatternFromFileChooser( targetFileChooser.getFileFilter()); } catch (ClassCastException exc) {  }

//          Look for selected cipher file and feed to extendedPathlist to be excpluded from the WalkTree returned list
//		if ((State.cipherReady) && (State.cipherSelected != State.DEVICE)) { cipherFilePath = cipherFileChooser.getSelectedFile().toPath(); }

//          Look for encryptable files (Long I/O operation set hourglass)
	    if (( targetPathList.size() == 1 ) )
	    {
		if ( isValidFile(ui, targetPathList.get(0), true, symlink, false ) )   { State.targetSelected = State.FILE; State.targetReady = true; }
		else if ( isValidDir(ui, targetPathList.get(0), symlink, false))
		{
		    for (Path path:   FinalCrypt.getExtendedPathList(ui, targetPathList, cipherFilePath, symlink, pattern, negatePattern, status) )
		    {
			if ( isValidFile(ui, path, true, symlink, false ) )   { State.targetSelected = State.FILE; State.targetReady = true; }
		    }
		} else { State.targetSelected = State.INVALID; State.targetReady = true; }
	    }
	    else if ( targetPathList.size() > 1 )
	    {
		for (Path path:FinalCrypt.getExtendedPathList(ui, targetPathList, cipherFilePath, symlink, pattern, negatePattern, status) )
		{
		    if ( isValidFile(ui, path, true, symlink, false ) )   { State.targetSelected = State.FILE; State.targetReady = true; }
		}
	    }
	}
    }

    public static boolean isValidDir(UI ui, Path targetDirPath, boolean symlink, boolean report)
    {
        boolean validdir = true; String conditions = "";		    String exist = ""; String read = ""; String write = ""; String symbolic = "";
        if ( ! Files.exists(targetDirPath))				    { validdir = false; exist = "[not found] "; conditions += exist; }
        if ( ! Files.isReadable(targetDirPath) )			    { validdir = false; read = "[not readable] "; conditions += read;  }
        if ( ! Files.isWritable(targetDirPath) )			    { validdir = false; write = "[not writable] "; conditions += write;  }
        if ( (! symlink) && (Files.isSymbolicLink(targetDirPath)) )	    { validdir = false; symbolic = "[symlink]"; conditions += symbolic;  }
        if ( validdir ) {  } else { if ( report )			    { ui.error("Warning: StateControler: Invalid Dir: " + targetDirPath.toString() + ": " + conditions + "\r\n"); } }
        return validdir;
    }

    public static boolean isValidFile(UI ui, Path targetSourcePath, Path cipherSourcePath, boolean readSize, boolean symlink, boolean report) // fileValidation Wrapper (including target==cipherSource comparison)
    {
	
        boolean validfile = true; String conditions = "";			    String cipher = "";
	validfile = isValidFile(ui, targetSourcePath, readSize, symlink, report);
	if (validfile) { if ( targetSourcePath.compareTo(cipherSourcePath) == 0 )   { validfile = false; cipher = "[isCipher] "; conditions += cipher; }}	
        if ( ! validfile ) { if ( report )					    { ui.error("Warning: StateControler: Invalid File: " + targetSourcePath.toAbsolutePath().toString() + ": " + conditions + "\r\n"); } }                    
        return validfile;
    }

    public static boolean isValidFile(UI ui, Path targetSourcePath, boolean readSize, boolean symlink, boolean report)
    {
        boolean validfile = true; String conditions = "";		    String size = ""; String exist = ""; String dir = ""; String read = ""; String write = ""; String symbolic = ""; String cipher = "";
        long fileSize = 0;						    if ( readSize ) { try { fileSize = Files.size(targetSourcePath); } catch (IOException ex) { } }

        if ( ! Files.exists(targetSourcePath))                              { validfile = false; exist = "[not found] "; conditions += exist; }
        else
        {
            if ( Files.isDirectory(targetSourcePath))                       { validfile = false; dir = "[is directory] "; conditions += dir; }
            if ((readSize) && ( fileSize == 0 ))			    { validfile = false; size = "[empty] "; conditions += size; }
            if ( ! Files.isReadable(targetSourcePath) )                     { validfile = false; read = "[not readable] "; conditions += read; }
            if ( ! Files.isWritable(targetSourcePath) )                     { validfile = false; write = "[not writable] "; conditions += write; }
            if ( (! symlink) && (Files.isSymbolicLink(targetSourcePath)) )  { validfile = false; symbolic = "[symlink] "; conditions += symbolic; }
        }
        
//	Gives a nullpointer on ui.error(..)
	if ( ! validfile )
	{ 
	    if ( report )				    { ui.error("Warning: StateControler: Invalid File: " + targetSourcePath.toAbsolutePath().toString() + ": " + conditions + "\r\n"); } 
	}                    
        return validfile;
    }
}