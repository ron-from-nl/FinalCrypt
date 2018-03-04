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

import java.io.File;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import static rdj.FinalCrypt.verbose;

public class Validate
{
    public static void checkCipher(UI ui, FinalCrypt finalcrypt, Path cipherPath)
    {
        State.cipherSelected = State.INVALID;
        State.cipherReady = false;
	long minSize = 1024;

	long cipherSize = 0;

	if (
		(Files.isRegularFile(cipherPath))
	   )
	{
            try { cipherSize = (int)Files.size(cipherPath); } catch (IOException ex) { ui.error("StateControler.validateCipher Files.size(finalCrypt.getCipherPath()) " + ex.getMessage() + "\r\n"); }
	    if ( cipherSize >= minSize )
	    {
		State.cipherSelected = State.FILE;
		State.cipherReady = true;
		ui.status("", false);
	    }
	    else
	    {
		State.cipherSelected = State.FILE;
		State.cipherReady = false;
		ui.error("Warning: Cipher smaller than " + minSize + " bytes \r\n");
	    }
	}
	else if (cipherPath.toAbsolutePath().toString().startsWith("/dev/sd")) // Linux Cipher Device Selection
	{
	    if (!cipherPath.getFileName().endsWith("sda"))
	    {
		minSize = 0;
		boolean symlink = false;
		boolean writable = false;
		boolean report = false;
		if (isValidFile(ui, "Validate.checkCipher", cipherPath, minSize, symlink, writable, report))
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
		minSize = 0;
		boolean symlink = false;
		boolean writable = false;
		boolean report = false;
		if (isValidFile(ui, "Validate.checkCipher", cipherPath, 0, false, false, false))
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
		    long minSize = 0;
		    boolean writable = false;
		    if (isValidFile(ui, "Validate.checkTarget", targetPathList.get(0), minSize, symlink, writable, status))
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
		    long minSize = 0;
		    boolean writable = false;
		    if (isValidFile(ui, "Validate.checkTarget", targetPathList.get(0), minSize, symlink, writable, status))
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
	if (( State.cipherSelected != State.DEVICE ) && ( ! State.targetReady) && ( State.cipherReady )) // No need to scan for encryptable items without selected cipher for better performance
	{
	    long minSize = 1;
	    boolean writable = true;
	    boolean report = false;
	    if (( targetPathList.size() == 1 ) )
	    {
		if  (
			(Files.isDirectory(targetPathList.get(0))) &&
			(isValidDir(ui, targetPathList.get(0), symlink, true))
		    )
		{
//				      getExtendedPathList(UI ui, ArrayList<Path> userSelectedItemsPathList, Path cipherPath, long minSize, boolean symlink, boolean writable, String pattern, boolean negatePattern, boolean status)
		    for (Path path:   getExtendedPathList(   ui,                            targetPathList,  cipherFilePath,      minSize,         symlink,             true,        pattern,         negatePattern,          false) )
		    {//                      "Validate.checkTarget singledir"
			if ( isValidFile(ui, "vcsd", path, minSize, symlink, writable, false ) )		    { State.targetSelected = State.FILE; State.targetReady = true; }
		    }
		} //                     "Validate.checkTarget singlefile"
		else if (
			    (! Files.isDirectory(targetPathList.get(0))) &&
			    (isValidFile(ui, "vcsf", targetPathList.get(0), minSize, symlink, writable, true))
			)    { State.targetSelected = State.FILE; State.targetReady = true; }
		else { State.targetSelected = State.INVALID; State.targetReady = false; }
	    }
	    else if ( targetPathList.size() > 1 )
	    {
//			       getExtendedPathList(UI ui, ArrayList<Path> userSelectedItemsPathList, Path cipherPath, long minSize, boolean symlink, boolean writable, String pattern, boolean negatePattern, boolean status)
		for (Path path:getExtendedPathList(   ui,                            targetPathList,  cipherFilePath,      minSize,         symlink,             true,        pattern,         negatePattern,         false) )
		{//                      "Validate.checkTarget multifile"
		    if ( isValidFile(ui, "vcmf",           path, minSize, symlink, writable, false ) )	    { State.targetSelected = State.FILE; State.targetReady = true; }
		}
	    }
	}
    }

    synchronized public static boolean isValidDir(UI ui, Path targetDirPath, boolean symlink, boolean report)
    {
        boolean validdir = true; String conditions = "";		    String exist = ""; String read = ""; String write = ""; String symbolic = "";
        if ( ! Files.exists(targetDirPath))				    { validdir = false; exist = "[not found] "; conditions += exist; }
        if ( ! Files.isReadable(targetDirPath) )			    { validdir = false; read = "[not readable] "; conditions += read;  }
        if ( ! Files.isWritable(targetDirPath) )			    { validdir = false; write = "[not writable] "; conditions += write;  }
        if ( (! symlink) && (Files.isSymbolicLink(targetDirPath)) )	    { validdir = false; symbolic = "[symlink]"; conditions += symbolic;  }
//        if ( validdir ) {  } else { if ( report )			    { ui.error("Warning: Validate.isValidDir: " + targetDirPath.toString() + ": " + conditions + "\r\n"); } }
        if ( validdir ) {  } else { if ( report )			    { ui.error("Warning: " + targetDirPath.toString() + ": " + conditions + "\r\n"); } }
        return validdir;
    }

    synchronized public static boolean isValidFile(UI ui, Path targetSourcePath, Path cipherSourcePath, long minSize, boolean symlink, boolean writable, boolean report) // fileValidation Wrapper (including target==cipherSource comparison)
    {
	
        boolean validfile = true; String conditions = "";			    String cipher = "";
	validfile = isValidFile(ui, "Validate.isValidFileWrapper", targetSourcePath, minSize, symlink, writable, report);
	if (validfile) { if ( targetSourcePath.compareTo(cipherSourcePath) == 0 )   { validfile = false; cipher = "[isCipher] "; conditions += cipher; }}	
//        if ( ! validfile ) { if ( report )					    { ui.error("Warning: Validate.isValidFile(.): " + targetSourcePath.toAbsolutePath().toString() + ": " + conditions + "\r\n"); } }                    
        if ( ! validfile ) { if ( report )					    { ui.error("Warning: " + targetSourcePath.toAbsolutePath().toString() + ": " + conditions + "\r\n"); } }                    
        return validfile;
    }

    synchronized public static boolean isValidFile(UI ui, String caller, Path targetSourcePath, long minSize, boolean symlink, boolean writable, boolean report)
    {
        boolean validfile = true; String conditions = "";		    String size = ""; String exist = ""; String dir = ""; String read = ""; String write = ""; String symbolic = ""; String cipher = "";

        if ( ! Files.exists(targetSourcePath))                              { validfile = false; exist = "[not found] "; conditions += exist; }
        else
        {
            if ( Files.isDirectory(targetSourcePath))                       { validfile = false; dir = "[is directory] "; conditions += dir; }
	    long fileSize = 0; try { fileSize = Files.size(targetSourcePath.toAbsolutePath()); } catch (IOException ex) { ui.error("Error: Validate: IOException: Files.size(" + targetSourcePath.toAbsolutePath().toString() + ") Size: " + fileSize + "<" + minSize + " "+ ex.getLocalizedMessage() + "\r\n");	}
            if ( fileSize < minSize )					    { validfile = false; size = targetSourcePath.toString() + " smaller than " + minSize + " byte "; conditions += size; }
            if ( ! Files.isReadable(targetSourcePath) )                     { validfile = false; read = "[not readable] "; conditions += read; }
            if ((writable) && ( ! Files.isWritable(targetSourcePath)))      { validfile = false; write = "[not writable] "; conditions += write; }
            if ( (! symlink) && (Files.isSymbolicLink(targetSourcePath)) )  { validfile = false; symbolic = "[symlink] "; conditions += symbolic; }
        }
        
	if ( ! validfile )
	{ 
	    if ( report )
//	    { ui.error("Warning: Validate.isValidFile(...): " + caller + " Invalid File: " + targetSourcePath.toAbsolutePath().toString() + ": " + conditions + "\r\n"); } 
	    { ui.error("Warning: " + conditions + "\r\n"); } 
	}                    
        return validfile;
    }
    
    public static ArrayList<Path> getExtendedPathList(UI ui, ArrayList<Path> userSelectedItemsPathList, Path cipherPath, long minSize, boolean symlink, boolean writable, String pattern, boolean negatePattern, boolean status)
    {
        // Converts from File[] to ArraayList<Path> where as every dir is converted into additional PathLists
        ArrayList<Path> recursivePathList = new ArrayList<>();
        if ( cipherPath == null )
        {
            for (Path outerpath:userSelectedItemsPathList)
            {
                if ( Files.isDirectory(outerpath) )
                {
                    for (Path path:getDirectoryPathList(ui, outerpath.toFile(), minSize, symlink, writable, pattern, negatePattern))
                    {
                        recursivePathList.add(path);
                    }
                }
                else
                {
                    recursivePathList.add(outerpath);
                }
            }
        }
        else
        {
            for (Path userSelectedItemPath:userSelectedItemsPathList)
            {
                if ( Files.isDirectory(userSelectedItemPath) )
                {
                    for (Path subItemPath:getDirectoryPathList(ui, userSelectedItemPath.toFile(), minSize, symlink, writable, pattern, negatePattern))
                    {
                        // cipherdetection not shown?
                        if ( ((subItemPath.toAbsolutePath().compareTo(cipherPath.toAbsolutePath()) != 0)) ) { recursivePathList.add(subItemPath); } else { if (status) { ui.status("Warning: cipher-file: " + cipherPath.toAbsolutePath() + " will be excluded!\r\n", true); }}
                    }
                }
                else
                {
                    if ( ((userSelectedItemPath.compareTo(cipherPath) != 0)) ) { recursivePathList.add(userSelectedItemPath); } else { if (status) { ui.status("Warning: cipher-file: " + cipherPath.toAbsolutePath() + " will be excluded!\r\n", true); }}
                }
            }
        }
        return recursivePathList;
    }

    // Used by getExtendedPathList(File[] files)
    public static ArrayList<Path> getDirectoryPathList(UI ui,File file, long minSize, boolean symlink, boolean writable, String pattern, boolean negatePattern)
    {
        // Converts from File[] to ArraayList<Path> where as every dir is converted into additional PathLists
        ArrayList<Path> recursivePathList = new ArrayList<>();
        
        EnumSet opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS); //follow links
//						      MySimpleFileVisitor(UI ui, boolean verbose, boolean delete, long minSize, boolean symlink, boolean writable, boolean returnpathlist, String pattern, boolean negatePattern)
        MySimpleFileVisitor mySimpleFileVisitor = new MySimpleFileVisitor(   ui,	 verbose,         false,       minSize,         symlink,             true,                   true,        pattern,         negatePattern);
        try{Files.walkFileTree(file.toPath(), opts, Integer.MAX_VALUE, mySimpleFileVisitor);} catch(IOException e){System.err.println(e);}
        recursivePathList = mySimpleFileVisitor.getPathList();

        return recursivePathList;
    }
}

// override only methods of our need (SimpleFileVisitor is a full blown class)
class MySimpleFileVisitor extends SimpleFileVisitor<Path>
{
    private final UI ui;
    private final PathMatcher pathMatcher;
    private final boolean verbose; 
    private final boolean delete; 
    private final long minSize; 
    private final boolean symlink; 
    private final boolean writable; 
    private final boolean returnpathlist; 
    private final ArrayList<Path> pathList;
    private boolean negatePattern;

//  Default CONSTRUCTOR

//  regex pattern
//  all *.bit   =   'regex:^.*\.bit$'
//  all but *.bit   'regex:(?!.*\.bit$)^.*$'
    
    public MySimpleFileVisitor(UI ui, boolean verbose, boolean delete, long minSize, boolean symlink, boolean writable, boolean returnpathlist, String pattern, boolean negatePattern)
    {
        this.ui = ui;
        pathMatcher = FileSystems.getDefault().getPathMatcher(pattern); // "glob:" or "regex:" included in pattern
        this.verbose = verbose;
        this.delete = delete;
        this.minSize = minSize;
        this.symlink = symlink;
        this.writable = writable;
        this.returnpathlist = returnpathlist;
        pathList = new ArrayList<Path>();
        this.negatePattern = negatePattern;
    }
   
    @Override public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs)
    {
        if ( Validate.isValidDir(ui, path, symlink, verbose) ) { return FileVisitResult.CONTINUE; } else { return FileVisitResult.SKIP_SUBTREE; }
    }
    
    @Override public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
    {
        long fileSize = 0; try { fileSize = Files.size(path); } catch (IOException ex) { }
        if (!negatePattern)
        {
            if ( (path.getFileName() != null ) && ( pathMatcher.matches(path.getFileName())) )
            {            
                if (delete)                 { try { Files.delete(path); } catch (IOException ex) { ui.error("Error: visitFile(.. ) Failed file: " + path.toString() + " due to: " + ex.getMessage() + "\r\n"); } }
                else if (returnpathlist)    
                {
//				  isValidFile(UI ui,                   String caller,	Path targetSourcePath, long minSize, boolean symlink, boolean writable, boolean report)
                    if ( Validate.isValidFile(   ui, "MySimpleFileVisitor.visitFile",                    path, this.minSize,   this.symlink,     this.writable,   this.verbose) ) { pathList.add(path); } 
                }
                else { ui.status("Huh? this shouldn't have happened.\r\n", true); }
            }   
        }
        else
        {
            if ( (path.getFileName() != null ) && ( ! pathMatcher.matches(path.getFileName())) ) // Negate Pattern; Does NOT match pattern
            {
                if (delete)                 { try { Files.delete(path); } catch (IOException ex) { ui.error("Error: visitFile(.. ) Failed file: " + path.toString() + " due to: " + ex.getMessage() + "\r\n"); } }
                else if (returnpathlist)
                {
//				    isValidFile(UI ui,                   String caller,	Path targetSourcePath, long minSize, boolean symlink, boolean writable, boolean report)
                    if ( Validate.isValidFile(     ui, "MySimpleFileVisitor.visitFile",                  path, this.minSize,    this.symlink,    this.writable, this.verbose)) { pathList.add(path); } 
                }
                else  { ui.status("Huh? this shouldn't have happened.\r\n", true); }
            }   
        }
        return FileVisitResult.CONTINUE;
    }
    
    @Override public FileVisitResult visitFileFailed(Path path, IOException exc)
    {
        ui.error("Warning: Skip File: " + path.toAbsolutePath().toString() + ": " + exc + "\r\n");
        return FileVisitResult.SKIP_SIBLINGS;
    }
    
    @Override public FileVisitResult postVisitDirectory(Path path, IOException exc)
    {
        if      (delete)            { try { Files.delete(path); } catch (IOException ex) { ui.error("Error: postVisitDirectory: " + path.toString() + " due to: " + ex.getMessage() + "\r\n"); } }
        else if (returnpathlist)    {        }
        else                        {     }
        return FileVisitResult.CONTINUE;
    }
    
    public ArrayList<Path> getPathList() { return pathList; }
}
