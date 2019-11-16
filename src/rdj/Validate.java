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
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

public class Validate
{
    private static Path selectedKeyPath;
    public static long bytesCount;
    private static MySimpleFCFileVisitor mySimpleFCFileVisitor;
    private static final Path	HOME_DIR = Paths.get(System.getProperty("user.home"));
    private static final Path	WORK_DIR = Paths.get(System.getProperty("user.dir"));
    public static Path		key_Home_Path = HOME_DIR;

    public static void validateBuild(UI ui, FCPathList<FCPath> targetFCPathList, FCPath keyFCPath, boolean printgpt, boolean deletegpt)
    {
    }

    synchronized public static boolean isValidDir(UI ui, Path targetDirPath, boolean symlink, boolean report)
    {
        boolean validdir = true; String conditions = "";				    String exist = ""; String read = ""; String write = ""; String symbolic = "";
        if ( ! Files.exists(targetDirPath))						    { validdir = false; exist = "[not found] "; conditions += exist; }
        if ( ! Files.isReadable(targetDirPath) )					    { validdir = false; read = "[not readable] "; conditions += read;  }
        if ( ! Files.isWritable(targetDirPath) )					    { validdir = false; write = "[not writable] "; conditions += write;  }
        if ( (! symlink) && (Files.isSymbolicLink(targetDirPath)) )			    { validdir = false; symbolic = "[symlink]"; conditions += symbolic;  }
//        if ( validdir ) {  } else { if ( report )					    { ui.error("Warning: Validate.isValidDir: " + targetDirPath.toString() + ": " + conditions + "\r\n"); } }
        if ( ! validdir )								    { if ( report ) { ui.log("Warning: skipping dir: " + targetDirPath.toString() + ": " + conditions + "\r\n", false, false, true, false, false); } }
        return validdir;
    }

//    synchronized public static boolean isValidFile(UI ui, String caller, Path path, Path keyPath, boolean device, long minSize, boolean symlink, boolean writable, boolean report) // fileValidation Wrapper (including target==keySource comparison)
//    {
//	
//        boolean validfile = true; String conditions = "";				    String key = "";
//	validfile = isValidFile(ui, caller, path, false, device, minSize, symlink, writable, report);
//	if ((keyPath != null) && validfile) { if (path.compareTo(keyPath) == 0) { validfile = false; key = "[is key] "; conditions += key; }}	
//        if ( ! validfile ) { if ( report )						    { ui.log("Warning: " + path.toAbsolutePath().toString() + ": " + conditions + "\r\n", true, true, false, false, false); } }                    
//        return validfile;
//    }
//
    synchronized public static boolean isValidFile(UI ui, String caller, Path path, boolean isKey, boolean device, long minSize, boolean symlink, boolean writable, boolean report)
    {
        boolean validfile = true; String conditions = "";				    String size = ""; String exist = ""; String dir = ""; String read = ""; String write = ""; String symbolic = ""; String key = "";

        if ( ! Files.exists(path))							    { validfile = false; exist = "[not found] "; conditions += exist; }
        else
        {
            if ( Files.isDirectory(path))						    { validfile = false; dir = "[is directory] "; conditions += dir; }
	    long fileSize = 0; if ( device )						    { fileSize = 0; fileSize = DeviceController.getDeviceSize(ui, path, isKey); } // Specifically done for OSX
	    else									    { fileSize = 0; try { fileSize = Files.size(path); } catch (IOException ex)  { ui.log("Error: Validate: IOException: Files.size(" + path.toAbsolutePath().toString() + ") Size: " + fileSize + "<" + minSize + " "+ ex.getMessage() + "\r\n", true, true, true, true, false); } }
            if ( fileSize < minSize )							    { validfile = false; size = path.toAbsolutePath().toString() + " smaller than " + minSize + " byte "; conditions += size; }
            if ( ! Files.isReadable(path) )						    { validfile = false; read = "[not readable] "; conditions += read; }
            if ((! isKey) && (writable) && ( ! Files.isWritable(path)))			    { validfile = false; write = "[not writable] "; conditions += write; }
            if ( (! symlink) && (Files.isSymbolicLink(path)) )				    { validfile = false; symbolic = "[symlink] "; conditions += symbolic; }
        }

	if ( ! validfile )
	{ 
	    if ( report )
//	    { ui.error("Warning: Validate.isValidFile(...): " + caller + " Invalid File: " + targetSourcePath.toAbsolutePath().toString() + ": " + conditions + "\r\n"); } 
//	    { ui.status("Warning: " + caller + " " + path.toAbsolutePath().toString() + ": " + conditions + "\r\n", true); } 
	    { ui.log("Warning: " + path.toAbsolutePath().toString() + ": " + conditions + "\r\n", true, true, false, false, false); } 
	}                    
        return validfile;
    }
    
//___________________________________________________________________________________________________________________________________________________________
//
//			Testing FinalCrypt Token
//			🔒   Encrypt
//			🔓   Decrypt	    (Key Authenticated)
//			🔓!  Decrypt Legacy  (Key can't be checked! No Token present in old format)
//			⛔   Decrypt Abort   (Key Failed)

    synchronized public static int targetSourceHasMAC(UI ui, Path targetSourcePath) // Tested
    {
	boolean targetSourceHasMAC = false;
	int macVersion = 0;
	
        ByteBuffer plainTextMACBuffer = ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length()); plainTextMACBuffer.clear();
        ByteBuffer encryptedMACBuffer = ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length()); encryptedMACBuffer.clear();
	
	long readTargetSourceChannelTransfered = 0;
	
	// Create Target Source MAC Buffer
	try (final SeekableByteChannel readTargetSourceChannel = Files.newByteChannel(targetSourcePath, FinalCrypt.getEnumSet(EnumSet.of(StandardOpenOption.READ))))
	{
	    // Fill up inputFileBuffer
	    readTargetSourceChannelTransfered = readTargetSourceChannel.read(plainTextMACBuffer); plainTextMACBuffer.flip();
	    if ( readTargetSourceChannelTransfered != plainTextMACBuffer.capacity() ) { return 0; }
	    readTargetSourceChannel.close(); 
	} catch (IOException ex) { ui.log("Error: targetSourceHasMAC: readTargetSourceChannel " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	
	// Compare plainTextMACBuffer to FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_MAC
	String plainTextMACString = new String(plainTextMACBuffer.array(), StandardCharsets.UTF_8);
//	ui.status("targetSourceHasMAC plainTextMACString: " +plainTextMACString + "\r\n", true);

	if	( plainTextMACString.equals(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V1) )	{ macVersion = 1; targetSourceHasMAC = true; }
	else if ( plainTextMACString.equals(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V2) )	{ macVersion = 2; targetSourceHasMAC = true; }
	else if ( plainTextMACString.equals(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3) )	{ macVersion = 3; targetSourceHasMAC = true; }
	else													{ macVersion = 0; targetSourceHasMAC = false; }

//	return targetSourceHasMAC;
	return macVersion;
    }
    
    synchronized public static boolean targetHasAuthenticatedMAC(UI ui, Path targetSourcePath, Path keySourcePath, int macVersion) // Tested
    {
	if (macVersion == 1) { FinalCrypt.resetPwdPos(); } else { FinalCrypt.resetPwdBytesPos(); }
	
	boolean readTargetSourceChannelError =	    false;
	boolean keyAuthenticatedTargetSource =	    false;
        ByteBuffer targetSrcMACBuffer =		    ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length() * 2); targetSrcMACBuffer.clear();
        ByteBuffer targetPlainTextMACBuffer =	    ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length()); targetPlainTextMACBuffer.clear();
        ByteBuffer targetEncryptedMACBuffer =	    ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length()); targetEncryptedMACBuffer.clear();
        ByteBuffer keySourceBuffer =		    ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length()); keySourceBuffer.clear();
        ByteBuffer keyDecryptedMACBuffer =	    ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length()); keyDecryptedMACBuffer.clear();
	
	long readTargetSourceChannelPosition = 0;   long readTargetSourceChannelTransfered = 0;
	long readKeySourceChannelPosition = 0;	    long readKeySourceChannelTransfered = 0;                
	
	// Create Target Source MAC Buffer
	try (final SeekableByteChannel readTargetSourceChannel = Files.newByteChannel(targetSourcePath, FinalCrypt.getEnumSet(EnumSet.of(StandardOpenOption.READ))))
	{
	    // Fill up inputFileBuffer
//	    readTargetSourceChannel.position(readTargetSourceChannelPosition);
//	    readTargetSourceChannelTransfered = readTargetSourceChannel.read(targetSrcMACBuffer); targetSrcMACBuffer.flip();
	    readTargetSourceChannel.read(targetSrcMACBuffer); targetSrcMACBuffer.flip();
	    readTargetSourceChannel.close(); 
	} catch (IOException ex) { readTargetSourceChannelError = true; ui.log("Error: targetHasAuthenticatedMAC: readTargetSourceChannel " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	
	// Encrypted MAC Buffer
	
	targetPlainTextMACBuffer.put(targetSrcMACBuffer.array(),									0, FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length()); targetPlainTextMACBuffer.flip();
	targetEncryptedMACBuffer.put(targetSrcMACBuffer.array(), FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length(), FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length()); targetEncryptedMACBuffer.flip();
	
	if ( ! readTargetSourceChannelError )
	{
	    if ( ! Files.isDirectory(keySourcePath)) // Manual Key Mode
	    {
		try (final SeekableByteChannel readKeySourceChannel = Files.newByteChannel(keySourcePath, FinalCrypt.getEnumSet(EnumSet.of(StandardOpenOption.READ))))
		{
		    // Fill up keyFileBuffer
		    readKeySourceChannel.read(keySourceBuffer);
		    keySourceBuffer.flip(); readKeySourceChannel.close();
		} catch (IOException ex) { ui.log("Error: targetHasAuthenticatedMAC readKeySourceChannel " + ex.getMessage() + "\r\n", true, true, true, true, false); }

		// Create Encrypted Token Buffer
		keyDecryptedMACBuffer = FinalCrypt.encryptBuffer(targetEncryptedMACBuffer, keySourceBuffer, macVersion, false);
		String keyDecryptedMACBufferString = new String(keyDecryptedMACBuffer.array(), StandardCharsets.UTF_8);
		// ui.status("targetHasAuthenticatedMAC.keyDecryptedMACBufferString: " + keyDecryptedMACBufferString + "\r\n", true);

		// Authenticate Key MAC against Target MAC
		if ( keyDecryptedMACBufferString.equals(StandardCharsets.UTF_8.decode(targetPlainTextMACBuffer).toString())) { keyAuthenticatedTargetSource = true; } else { keyAuthenticatedTargetSource = false; }
	    }
	    else // Switch to dynamic Auto Key Mode
	    {
		Path autoKeyPath = Paths.get(keySourcePath.toAbsolutePath().toString(), targetSourcePath.toAbsolutePath().toString().replace(":", ""));
		if (Files.exists(autoKeyPath, LinkOption.NOFOLLOW_LINKS))
		{
		    try (final SeekableByteChannel readKeySourceChannel = Files.newByteChannel(autoKeyPath, FinalCrypt.getEnumSet(EnumSet.of(StandardOpenOption.READ))))
		    {
			// Fill up keyFileBuffer
			readKeySourceChannel.read(keySourceBuffer);
			keySourceBuffer.flip(); readKeySourceChannel.close();
		    } catch (IOException ex) { ui.log("Error: targetHasAuthenticatedMAC readKeySourceChannel " + ex.getMessage() + "\r\n", true, true, true, true, false); }

		    // Create Encrypted Token Buffer
		    keyDecryptedMACBuffer = FinalCrypt.encryptBuffer(targetEncryptedMACBuffer, keySourceBuffer, macVersion, false);
		    String keyDecryptedMACBufferString = new String(keyDecryptedMACBuffer.array(), StandardCharsets.UTF_8);
		    // ui.status("targetHasAuthenticatedMAC.keyDecryptedMACBufferString: " + keyDecryptedMACBufferString + "\r\n", true);

		    // Authenticate Key MAC against Target MAC
		    if ( keyDecryptedMACBufferString.equals(StandardCharsets.UTF_8.decode(targetPlainTextMACBuffer).toString())) { keyAuthenticatedTargetSource = true; } else { keyAuthenticatedTargetSource = false; }
		} else { keyAuthenticatedTargetSource = false; }
	    }
	} else { keyAuthenticatedTargetSource = false; }

	
//	Read in Key

	if (macVersion == 1) { FinalCrypt.resetPwdPos(); } else { FinalCrypt.resetPwdBytesPos(); }
	return keyAuthenticatedTargetSource;
    }

    synchronized public static long getTargetReadAutoKeySizeMatched(UI ui, Path targetSourcePath, long targetSourceSize, Path keySourcePath)
    {
	long existingReadAutoKeySize = 0L;
	long readAutoKeySize = 0L;
	
	if ( keySourcePath == null ) { return 0L; }
	if ( ! Files.isDirectory(keySourcePath)) { return 0L; } // Manual Key Mode
	else // Auto Key Mode
	{
	    Path autoKeyPath1 = Paths.get(keySourcePath.toAbsolutePath().toString(), targetSourcePath.toAbsolutePath().toString().replace(":", ""));
	    Path autoKeyPath2 = Paths.get(keySourcePath.toAbsolutePath().toString(), targetSourcePath.toAbsolutePath().toString().replace(":", "") + ".bit");
	    if ((Files.exists(autoKeyPath1, LinkOption.NOFOLLOW_LINKS)))
	    {
		try { existingReadAutoKeySize = Files.size(autoKeyPath1); } catch (IOException ex)  { ui.log("Error: IOException: getTargetKeySizeRequired(..): Files.size() "+ ex.getMessage() + "\r\n", true, true, true, true, false); } // Symlinks give Files.size() errors on broken links
	    }
	    else if (Files.exists(autoKeyPath2, LinkOption.NOFOLLOW_LINKS))
	    {
		try { existingReadAutoKeySize = Files.size(autoKeyPath2); } catch (IOException ex)  { ui.log("Error: IOException: getTargetKeySizeRequired(..): Files.size() "+ ex.getMessage() + "\r\n", true, true, true, true, false); } // Symlinks give Files.size() errors on broken links
	    }
	    else
	    {
		existingReadAutoKeySize = 0L;
	    }
	}
	return existingReadAutoKeySize;
    }

    synchronized public static long getTargetWriteAutoKeySizeRequired(UI ui, Path targetSourcePath, long targetSourceSize, Path keySourcePath)
    {
//	long existingWriteAutoKeySize = 0L;
	long writeAutoKeySize = 0L;
	
	if ( keySourcePath == null ) { return 0L; }
	if ( ! Files.isDirectory(keySourcePath)) { return 0L; } // Manual Key Mode
	else // Auto Key Mode
	{
	    Path autoKeyPath = Paths.get(keySourcePath.toAbsolutePath().toString(), targetSourcePath.toAbsolutePath().toString().replace(":", "") + ".bit");
	    if (Files.exists(autoKeyPath, LinkOption.NOFOLLOW_LINKS))
	    {
//		try { existingWriteAutoKeySize = Files.size(autoKeyPath); } catch (IOException ex)  { ui.log("Error: IOException: getTargetKeySizeRequired(..): Files.size() "+ ex.getMessage() + "\r\n", true, true, true, true, false); } // Symlinks give Files.size() errors on broken links
//		writeAutoKeySize = ( ( targetSourceSize + FCPath.MAC_SIZE ) - existingWriteAutoKeySize);
		writeAutoKeySize = ( ( targetSourceSize + FCPath.MAC_SIZE ) );
	    }
	    else
	    {
		writeAutoKeySize = ( targetSourceSize + FCPath.MAC_SIZE );
	    }
	}
	return writeAutoKeySize;
    }


    // Synchronized removes multifile target inconsistency, but also smooth busy animation
    public static void buildSelection(UI ui, ArrayList<Path> pathList, FCPath keyFCPath, FCPathList<FCPath> targetFCPathList, boolean symlink, String pattern, boolean negatePattern, boolean disabledMAC, boolean status)
    {
//				    MySimpleFCFileVisitor(UI ui, boolean verbose,		int function, boolean symlink, boolean setFCPathlist, Path keyPath, ArrayList<FCPath> targetFCPathList, String pattern, boolean negatePattern,  boolean disabledMAC)
	mySimpleFCFileVisitor = new MySimpleFCFileVisitor(   ui,	   false, MySimpleFCFileVisitor.SCAN,         symlink,                  true,    keyFCPath,                   targetFCPathList,	   pattern,         negatePattern,	    disabledMAC);
	
	for (Path path:pathList)
	{
	    try{ Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS,FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, mySimpleFCFileVisitor);} catch(IOException e) { ui.log("Error: Validate.buildSelection: Files.walkFileTree(path, EnumSet.of(..) " + e.getMessage() + "\r\n", true, true, true, true, false); }
	}
	mySimpleFCFileVisitor.running = false;
	ui.buildReady(targetFCPathList, true);	
    }

    synchronized public static String getHumanSize(double value,int decimals, String unit)
    {
        int x = 0;
        long factor;
        double newValue = value;
        String returnString = new String("");
        ArrayList<String> magnitude = new ArrayList<String>(); magnitude.addAll(Arrays.asList("Zi" + unit.charAt(0),"Ei" + unit.charAt(0),"Pi" + unit.charAt(0),"Ti" + unit.charAt(0),"Gi" + unit.charAt(0),"Mi" + unit.charAt(0),"Ki" + unit.charAt(0), unit));
        for (factor = 70; factor > 0; factor -= 10)
        {
            if ((value / Math.pow(2, factor)) >= 1) { newValue = (value / Math.pow(2, factor)); returnString = String.format("%.1f", (newValue)) + " " + magnitude.get(x); break; } x++;
        }
        if (factor == 0) { newValue = (value / Math.pow(2, factor)); returnString = String.format("%." + decimals + "f", (newValue)) + " " + magnitude.get(x); }
        return returnString;
    }

    public static int getFCPathType(Path path)
    {
	int returnFCPathType = FCPath.INVALID;

	if (path.toAbsolutePath().toString().startsWith("/dev/"))
	{
	    if	    (path.toAbsolutePath().toString().startsWith("/dev/hd")) // Linux IDE Key Device Selection
	    {
		if  (Character.isDigit(path.getFileName().toString().charAt(path.getFileName().toString().length()-1))) { returnFCPathType = FCPath.PARTITION; }
		else { if ( ! path.getFileName().endsWith("hda")) { returnFCPathType = FCPath.DEVICE; } else { returnFCPathType = FCPath.DEVICE_PROTECTED; } }
	    }
	    else if	    (path.toAbsolutePath().toString().startsWith("/dev/sd")) // Linux SATA Key Device Selection
	    {
		if  (Character.isDigit(path.getFileName().toString().charAt(path.getFileName().toString().length()-1))) { returnFCPathType = FCPath.PARTITION; }
		else { if ( ! path.getFileName().endsWith("sda")) { returnFCPathType = FCPath.DEVICE; } else { returnFCPathType = FCPath.DEVICE_PROTECTED; } }
	    }
	    else if (path.toAbsolutePath().toString().startsWith("/dev/mmcblk")) // (mmcblk0p1) Linux SD-Card / MultiMedia Card
	    {
		if  (
			(Character.isDigit(path.getFileName().toString().charAt(path.getFileName().toString().length()-1))) &&
			(String.valueOf(path.getFileName().toString().charAt(path.getFileName().toString().length()-2)).equalsIgnoreCase("p"))
		    )
		{ returnFCPathType = FCPath.PARTITION; }
		else { if ( ! path.getFileName().toString().endsWith("mmcblk0")) { returnFCPathType = FCPath.DEVICE; } else { returnFCPathType = FCPath.DEVICE/*_PROTECTED*/; } }
	    }
	    else if (path.toAbsolutePath().toString().startsWith("/dev/nvme")) // (nvme0n1p1) Linux High Speed Non-Volatile Memory Express storage device
	    {
		if  (
			(Character.isDigit(path.getFileName().toString().charAt(path.getFileName().toString().length()-1))) &&
			(String.valueOf(path.getFileName().toString().charAt(path.getFileName().toString().length()-2)).equalsIgnoreCase("p"))
		    )
		{ returnFCPathType = FCPath.PARTITION; }
		else { if ( ! path.getFileName().toString().endsWith("nvme0n1")) { returnFCPathType = FCPath.DEVICE; } else { returnFCPathType = FCPath.DEVICE_PROTECTED; } }
	    }
	    else if (path.toAbsolutePath().toString().startsWith("/dev/disk")) // Apple Device Selection
	    {
		if  (
			(Character.isDigit(path.getFileName().toString().charAt(path.getFileName().toString().length()-1))) &&
			(String.valueOf(path.getFileName().toString().charAt(path.getFileName().toString().length()-2)).equalsIgnoreCase("s"))
		    )
		{ returnFCPathType = FCPath.PARTITION; }
		else { if ( ! path.getFileName().toString().endsWith("disk0")) { returnFCPathType = FCPath.DEVICE; } else { returnFCPathType = FCPath.DEVICE_PROTECTED; } }
	    }
	    else if (path.toAbsolutePath().toString().startsWith("/dev/ada")) // BSD ATAPI Device Selection
	    {
		if  (
			(Character.isDigit(path.getFileName().toString().charAt(path.getFileName().toString().length()-1))) &&
			(String.valueOf(path.getFileName().toString().charAt(path.getFileName().toString().length()-2)).equalsIgnoreCase("p"))
		    )
		{ returnFCPathType = FCPath.PARTITION; }
		else { if ( ! path.getFileName().toString().endsWith("ada0")) { returnFCPathType = FCPath.DEVICE; } else { returnFCPathType = FCPath.DEVICE_PROTECTED; } }
	    }
	    else if (path.toAbsolutePath().toString().startsWith("/dev/da")) // BSD Direct Attached Device Selection
	    {
		if  (
			(Character.isDigit(path.getFileName().toString().charAt(path.getFileName().toString().length()-1))) &&
			(String.valueOf(path.getFileName().toString().charAt(path.getFileName().toString().length()-2)).equalsIgnoreCase("p"))
		    )
		{ returnFCPathType = FCPath.PARTITION; }
		else { if ( ! path.getFileName().toString().endsWith("da0")) { returnFCPathType = FCPath.DEVICE; } else { returnFCPathType = FCPath.DEVICE; } }
	    }
	    else { returnFCPathType = FCPath.DEVICE_INVALID; }
	} // Not a Device /dev/
	else
	{
	    if	    ( Files.isDirectory(path) )					{ returnFCPathType = FCPath.DIRECTORY; }
	    else if ( Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS) )	{ returnFCPathType = FCPath.FILE; }
	    else if ( Files.isSymbolicLink(path) )				{ returnFCPathType = FCPath.SYMLINK; }
	}
	return returnFCPathType;
    }
    
    synchronized public static FCPath getFCPath(UI ui, String caller, Path path, boolean isKey, Path keyPath, boolean disabledMAC, boolean report)
    {
	boolean exist =			    false;
	int	type =			    FCPath.INVALID;
	long    size =			    0;
	boolean readable =		    false;
	boolean writable =		    false;
	boolean isHidden =		    false;
	boolean matchKey =		    false;
	
	boolean isValid =		    false;
	boolean isValidPath =		    false;
	boolean isValidFile =		    false;
	boolean isValidDevice =		    false;
	boolean isValidDeviceProtected =    false;
	boolean isValidPartition =	    false;

	boolean isDecrypted =		    false;
	
	boolean isEncryptable =		    false;
	
	boolean matchedReadAutoKey =	    false;
	long	matchedReadAutoKeySize =    0;
	boolean needsWriteAutoKey =	    false;
	long	needsWriteAutoKeySize =	    0;
	
	boolean isNewEncrypted =	    false;
	boolean isUnEncryptable =	    false;
	
//	boolean hasFCToken =		    false;
	boolean isEncrypted =		    false;
	int	macVersion =		    0;
//	boolean isAuthenticated =	    false;
	boolean isDecryptable =		    false;
	boolean isNewDecrypted =	    false;	
	boolean isUnDecryptable =	    false;	
	
	boolean isValidKey =		    false;	
	boolean isValidKeyDir =		    false;

	String	errorDesc =		    "";

	path = path.normalize();
	
        if ( Files.exists(path, LinkOption.NOFOLLOW_LINKS) ) // Does not check if symbolic link target file exist
//        if ( Files.exists(path) )
	{
	    exist = true;
	    type = getFCPathType(path);
	    if (exist)
	    {
		if ( (type == FCPath.PARTITION) || (type == FCPath.DEVICE) || (type == FCPath.DEVICE_PROTECTED) )   { size = DeviceController.getDeviceSize(ui, path, isKey); } // OS Independent guessing size method
		else if( (exist) && ((type == FCPath.FILE) /*|| (type == FCPath.SYMLINK)*/) )			    { try { size = Files.size(path); } catch (IOException ex)  { ui.log("Error: IOException: Validate.getFCPath: Files.size() "+ ex.getMessage() + "\r\n", true, true, true, true, false); } } // Symlinks give Files.size() errors on broken links
	    }
	    
	    readable = Files.isReadable(path);
	    writable = Files.isWritable(path);
//	    ui.log("Hidden: " + path.toAbsolutePath().toString() + "\r\n", true, true, false, false, false);
	    if (Files.isRegularFile(path))
	    {
		try { isHidden = Files.isHidden(path); } catch (IOException ex)					    { ui.log("Error: IOException: Validate.getFCPath: Files.isHidden(path) "+ ex.getMessage() + "\r\n", true, true, true, true, false); } // SoftDown.eu error
	    }
	    
	    // Target =============================================================================================================================================================================================
	    
	    // isValid in general
	    if ( isKey )
	    {
		if (
			    (exist)
			&&  (type == FCPath.DIRECTORY)
			&& (readable)
			&& (writable)
		    )
		{
		    isValid = true; isValidPath = true; isValidKeyDir = true; isUnEncryptable = true; isUnDecryptable = true;
		}
		else
		{
		    isValid = false; isValidPath = false; isValidKeyDir = false; isUnEncryptable = true; isUnDecryptable = true;
		}

		boolean tmpValid = true;
		if (type != FCPath.DIRECTORY)
		{
		    if ( ! readable )									{ tmpValid = false; errorDesc += "Warning: Key File: \"" + path.toAbsolutePath().toString() + "\" not readable!\r\n"; }
		    if ( ! writable )									{ tmpValid = false; errorDesc += "Warning: Key File: \"" + path.toAbsolutePath().toString() + "\" not writable!\r\n"; }
		}
		else
		{
		    if ( ! readable )									{ tmpValid = false; errorDesc += "Warning: Key Directory: \"" + path.toAbsolutePath().toString() + "\" not readable!\r\n"; }
		    if ( ! writable )									{ tmpValid = false; errorDesc += "Warning: Key Directory: \"" + path.toAbsolutePath().toString() + "\" not writable!\r\n"; }
		}
		if ( path.toAbsolutePath().toString().equals(Paths.get("/").toAbsolutePath().toString()) )				{ tmpValid = false; errorDesc = "Warning: Key Directory: \"" + path.toAbsolutePath().toString() + "\" equals Root directory!\r\n"; }
		if ( path.toAbsolutePath().compareTo(HOME_DIR.toAbsolutePath()) == 0)							{ tmpValid = false; errorDesc = "Warning: Key Directory: \"" + path.toAbsolutePath().toString() + "\" equals Home directory!\r\n"; }
		if ( path.toAbsolutePath().compareTo(WORK_DIR.toAbsolutePath()) == 0)							{ tmpValid = false; errorDesc = "Warning: Key Directory: \"" + path.toAbsolutePath().toString() + "\" equals Work directory!\r\n";}
		if ( path.toAbsolutePath().compareTo(key_Home_Path.toAbsolutePath()) == 0)						{ tmpValid = false; errorDesc = "Warning: Key Directory: \"" + path.toAbsolutePath().toString() + "\" equals Home directory!\r\n";}
		if ( path.toAbsolutePath().compareTo(Paths.get(HOME_DIR.toAbsolutePath().toString(), "Desktop").toAbsolutePath()) == 0)	{ tmpValid = false; errorDesc = "Warning: Key Directory: \"" + path.toAbsolutePath().toString() + "\" equals Desktop directory!\r\n";}

		if (tmpValid)   { isValid = true; isValidPath = true; isValidKeyDir = true; isUnEncryptable = true; isUnDecryptable = true; }
		else		{ isValid = false; isValidPath = false; isValidKeyDir = false; isUnEncryptable = true; isUnDecryptable = true; }

		
		if (disabledMAC)    { if (( exist ) && ( size >= FCPath.KEY_SIZE_MIN )					&& ( readable ) )   { isValid = true; isValidKey = true; } else { isValid = false; isUnEncryptable = true; isUnDecryptable = true; } }
		else		    { if (( exist ) && ( size >= FCPath.KEY_SIZE_MIN ) && ( size >= FCPath.MAC_SIZE )	&& ( readable ) )   { isValid = true; isValidKey = true; } else { isValid = false; isUnEncryptable = true; isUnDecryptable = true; } }
	    }
	    else	    { if (( exist ) && ( size >  0 ) && ( readable )   && ( writable ))	    { isValid = true; } else { isValid = false; isUnEncryptable = true; isUnDecryptable = true; } }
	    
	    // File validity
	    if (( isValid ) && ( type == FCPath.FILE ))								{ isValidFile = true; } else { isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true; }
	    
	    if (( isValid ) && ( type == FCPath.FILE ))								{ isValidFile = true; } else { isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true; }
	    
	    //DeviceProtected validity
	    if (( isValid ) &&	( ( type == FCPath.DEVICE_PROTECTED ) ) && ( size >= FCPath.KEY_SIZE_MIN ) )	{ isValidDeviceProtected = true;  isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true; }

	    //Device validity
	    if (( isValid ) &&	( ( type == FCPath.DEVICE ) )		&& ( size >= FCPath.KEY_SIZE_MIN ) )	{ isValidDevice = true;		  isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true; }

	    //Partition validity
	    if (( isValid ) &&	( type == FCPath.PARTITION ) && ( size >= FCPath.KEY_SIZE_MIN ) )		{ isValidPartition = true;  isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true; }

	    // Encrypted File State
	    if (( isValidFile ))
	    {
		macVersion = targetSourceHasMAC(ui, path);
		if ( macVersion == 0 ) { isEncrypted = false; } else { isEncrypted = true; }
		
		if ((isEncrypted) && (keyPath != null)  && (size > (FCPath.MAC_SIZE))) { if (keyPath != null) { isDecryptable = targetHasAuthenticatedMAC(ui, path, keyPath, macVersion); } } // Encrypted files must by MAC_SIZE at least
	    }
	    
	    if (( isValidFile ) && ( isEncrypted ) && ( ! isDecryptable ))  {
										isEncrypted = true; isDecryptable = false; isDecrypted = false; isEncryptable = false; isUnEncryptable = true; isUnDecryptable = true;
										if ( (! isKey)) { matchedReadAutoKeySize = getTargetReadAutoKeySizeMatched(ui, path, size, keyPath); if (matchedReadAutoKeySize > 0L) { matchedReadAutoKey = true; } }
									    }
	    if (( isValidFile )	&& ( isEncrypted ) && (   isDecryptable ))  {
										isEncrypted = true; isDecryptable = true;  isDecrypted = false; isEncryptable = false; isUnEncryptable = true; isUnDecryptable = false;
										if ( (! isKey)) { matchedReadAutoKeySize = getTargetReadAutoKeySizeMatched(ui, path, size, keyPath); if (matchedReadAutoKeySize > 0L) { matchedReadAutoKey = true; } }
									    }
	    
	    // Decrypted File State	    
	    if (( isValidFile ) && ( ! isEncrypted ))			    { 
									        isDecrypted = true; isUnEncryptable = false; isDecryptable = false; isEncryptable = true; isUnDecryptable = true;
										if ((isEncryptable) && (! isKey)) { needsWriteAutoKeySize = getTargetWriteAutoKeySizeRequired(ui, path, size, keyPath); if (needsWriteAutoKeySize > 0L) { needsWriteAutoKey = true; } }

										if ( (! isKey)) { matchedReadAutoKeySize = getTargetReadAutoKeySizeMatched(ui, path, size, keyPath); if (matchedReadAutoKeySize > 0L) { matchedReadAutoKey = true; } }
									    }

	    // Key =============================================================================================================================================================================================
	    
	    if ( keyPath != null )							{ if (path.compareTo(keyPath) == 0)   { matchKey = true; isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true;} }
	    if (( isKey ) && (exist))
	    {
		isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true;
		if (disabledMAC) // Dangerous Mode
		{
		    if (	(
					( type == FCPath.FILE )
				    ||  ( type == FCPath.PARTITION )
				    ||	( type == FCPath.DEVICE )
				    ||	( type == FCPath.DEVICE_PROTECTED )
				)
				    && ( size >=  FCPath.KEY_SIZE_MIN )	&& ( readable  )
			)	{ isValidKey = true; }
		}
		else // Safe MAC Mode
		{
		    if (
			    (
				    ( type == FCPath.FILE )
				||  ( type == FCPath.PARTITION )
				||  ( type == FCPath.DEVICE )
				||  ( type == FCPath.DEVICE_PROTECTED )
			    )
				    && ( size >=  FCPath.KEY_SIZE_MIN ) && ( size >= FCPath.MAC_SIZE )	&& ( readable  )
			)	{ isValidKey = true; }
		}
	    }	    
	}

//				    Path path,boolean exist,int type,long size,boolean readable,boolean writable,boolean isHidden,boolean matchesKey,boolean isValid,boolean isValidFile, boolean isValidDeviceProtected, boolean isValidDevice, boolean isValidPartition, boolean isKey, boolean isValidKey, boolean isValidKeyDir, boolean isDecrypted, boolean isEncryptable, boolean needsWriteAutoKey, long needsWriteAutoKeySize, boolean matchedReadAutoKey, long matchedReadAutoKeySize, boolean isNewEncrypted, boolean isUnEncryptable, boolean isEnacrypted, int macVersion, boolean isDecryptable, boolean isNewDecrypted, boolean isUnDecryptable, String errorDesc
	FCPath	fcPath = new FCPath(     path,        exist,    type,     size,        readable,        writable,        isHidden,        matchKey,          isValid,        isValidFile,         isValidDeviceProtected,         isValidDevice,         isValidPartition,	   isKey,         isValidKey,         isValidKeyDir,	     isDecrypted,         isEncryptable,         needsWriteAutoKey,      needsWriteAutoKeySize,	        matchedReadAutoKey,      matchedReadAutoKeySize,	isNewEncrypted,		isUnEncryptable,          isEncrypted,     macVersion,	       isDecryptable,	      isNewDecrypted,         isUnDecryptable,	       errorDesc);
	return fcPath;
    }
    
    synchronized public static String getFCPathStatus(FCPath fcPath)
    {
	String returnString = "";

//	String[] columnNames = { "Path", "Exist ", "Type ", "Size ", "Readable "};
//	Object[][] data = {{path.toAbsolutePath().toString(), exist, getTypeString(type), size, readable, isValidKey}};

	
        returnString += (String.format("%-2s%-40s%-3s%-6s%-3s%-17s%-3s%-12s%-3s%-9s%-3s%-6s%-2s\r\n", "|-", "----------------------------------------",	"-|-", "------", "-|-", "-----------------",   "-|-", "------------",	"-|-", "---------",	"-|-", "------",		"-|"));
        returnString += (String.format("%-2s%-40s%-3s%-6s%-3s%-17s%-3s%-12s%-3s%-9s%-3s%-6s%-2s\r\n", "| ", "Path",					" | ", "Exist ", " | ", "Type",		       " | ", "Size",		" | ", "Readable ",	" | ", "Valid ",		" |"));
        returnString += (String.format("%-2s%-40s%-3s%-6s%-3s%-17s%-3s%-12s%-3s%-9s%-3s%-6s%-2s\r\n", "|-", "----------------------------------------",	"-|-", "------", "-|-", "-----------------",   "-|-", "------------",	"-|-", "---------",	"-|-", "------",		"-|"));
        returnString += (String.format("%-2s%-40s%-3s%-6s%-3s%-17s%-3s%-12s%-3s%-9s%-3s%-6s%-2s\r\n", "| ", fcPath.path.toAbsolutePath().toString(),			" | ", b(fcPath.exist), " | ", t(fcPath.type), " | ", s(fcPath.size),	" | ", b(fcPath.isReadable), " | ", b(fcPath.isValidKey),	" |"));
        returnString += (String.format("%-2s%-40s%-3s%-6s%-3s%-17s%-3s%-12s%-3s%-9s%-3s%-6s%-2s\r\n", "|-", "----------------------------------------",	"-|-", "------", "-|-", "-----------------",   "-|-", "------------",	"-|-", "---------",	"-|-", "------",		"-|"));

	return returnString;
    }
    
    private static String b(boolean b)	{ return Boolean.toString(b); }
    private static String t(int t)	{ return FCPath.getTypeString(t); }
    private static String s(long s)	{ return Validate.getHumanSize(s, 1,"Bytes"); }
}


// override only methods of our need (SimpleFileVisitor is a full blown class)
class MySimpleFCFileVisitor extends SimpleFileVisitor<Path>
{
    private final UI ui;
    private final PathMatcher pathMatcher;
    private final boolean verbose; 
//    private final boolean delete;
    private final boolean symlink; 
    private final boolean setFCPathlist; 
    public FCPath keyFCPath;
    private FCPathList<FCPath> targetFCPathList;
    private boolean negatePattern;
    public long bytesCount = 0;
    public static boolean running = false; 
    private static boolean disabledMAC = false; 
    public static final int NOFUNCTION = 0;
    public static final int SCAN = 1;
    public static final int DELETE = 2;
    public static final int BACKUP = 3;
    public static int function = NOFUNCTION;

//  regex pattern
//  all *.bit   =   'regex:^.*\.bit$'
//  all but *.bit   'regex:(?!.*\.bit$)^.*$'
    
    public MySimpleFCFileVisitor(UI ui, boolean verbose, int function, boolean symlink, boolean setFCPathlist, FCPath keyFCPath, FCPathList<FCPath> targetFCPathList, String pattern, boolean negatePattern, boolean disabledMAC)
    {
        this.ui = ui;
        pathMatcher = FileSystems.getDefault().getPathMatcher(pattern); // "glob:" or "regex:" included in pattern
        this.verbose = verbose;
	this.function = function;
//        this.delete = delete;
        this.symlink = symlink;
        this.setFCPathlist = setFCPathlist;
	this.keyFCPath = keyFCPath;
	this.targetFCPathList = targetFCPathList;
        this.negatePattern = negatePattern;
	this.disabledMAC = disabledMAC;
	bytesCount = 0;
	running = true;
	
    }
   
    @Override public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs)
    {
	if (running)
	{
	    if	    (function == DELETE)    { return FileVisitResult.CONTINUE; }
	    else if (function == SCAN)	    { if (setFCPathlist) { if ( Validate.isValidDir(ui, path, symlink, true) ) { return FileVisitResult.CONTINUE; } else { return FileVisitResult.SKIP_SUBTREE; } } else { return FileVisitResult.SKIP_SUBTREE; } }
	    else if (function == BACKUP)    { return FileVisitResult.CONTINUE; }
	    else			    { ui.log("Huh? this shouldn't have happened. Neither booleans: delete & returnpathlist are present?\r\n", true, true, false, false, false); return FileVisitResult.CONTINUE; }
	}
	else { targetFCPathList.clear(); return FileVisitResult.TERMINATE; }
    }    
    
    @Override public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
    {
	if (running)
	{
	    if ( (path.getFileName() != null ) && ( negatePattern ^ pathMatcher.matches(path.getFileName())) ) // ^ = XOR just reverses the match when -W instead of -w if given in CLUI
	    {            
		if	(function == DELETE)
		{
		    ui.log("Delete: \"" + path + "\"\r\n", true, true, true, false, false);
		    try { Files.delete(path); } catch (IOException ex) { ui.log("Error: visitFile(.. ) Failed file: " + path.toAbsolutePath().toString() + " due to: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
		    if ((keyFCPath != null) && (keyFCPath.isValidKeyDir))
		    {
			Path autoKeyPath1 = Paths.get(keyFCPath.path.toAbsolutePath().toString(), path.toAbsolutePath().toString().replace(":", ""));
			Path autoKeyPath2 = Paths.get(keyFCPath.path.toAbsolutePath().toString(), path.toAbsolutePath().toString().replace(":", "") + ".bit");
			if (Files.exists(autoKeyPath1, LinkOption.NOFOLLOW_LINKS))
			{
			    ui.log("Delete: \"" + autoKeyPath1 + "\"\r\n", true, true, true, false, false);
			    try { Files.delete(autoKeyPath1); } catch (IOException ex) { ui.log("Error: visitFile(.. ) Failed file: " + autoKeyPath1.toAbsolutePath().toString() + " due to: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
			}
			if (Files.exists(autoKeyPath2, LinkOption.NOFOLLOW_LINKS))
			{
			    ui.log("Delete: \"" + autoKeyPath2 + "\"\r\n", true, true, true, false, false);
			    try { Files.delete(autoKeyPath2); } catch (IOException ex) { ui.log("Error: visitFile(.. ) Failed file: " + autoKeyPath2.toAbsolutePath().toString() + " due to: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
			}
		    }
		}
		else if (function == SCAN)    
		{
		    if (setFCPathlist)
		    {
//						 getFCPath(UI ui, String caller, Path path, boolean isKey,	 Path keyPath, boolean disabledMAC, boolean report)
			FCPath fcPath = Validate.getFCPath(   ui,            "",      path,            false, this.keyFCPath.path, disabledMAC,          true); targetFCPathList.add(fcPath);
		    }
		}
		else if (function == BACKUP)    
		{
		}
		else { ui.log("Huh? this shouldn't have happened. Neither booleans: delete & returnpathlist are present?\r\n", true, true, false, false, false); }
	    }
	    return FileVisitResult.CONTINUE;
	}
	else { targetFCPathList.clear(); return FileVisitResult.TERMINATE; } 
    }
    
    @Override public FileVisitResult visitFileFailed(Path path, IOException exc)
    {
	if (running)
	{
//				    getFCPath(UI ui, String caller, Path path, boolean isKey,	     Path keyPath, boolean disabledMAC, boolean report)
	    FCPath fcPath = Validate.getFCPath(  ui,            "",      path,         false, this.keyFCPath.path,	   disabledMAC,          true); targetFCPathList.add(fcPath);
	    return FileVisitResult.SKIP_SIBLINGS;
	}
	else { targetFCPathList.clear(); return FileVisitResult.TERMINATE; } 
    }
    
    @Override public FileVisitResult postVisitDirectory(Path path, IOException exc)
    {
	if (running)
	{
	    if      (function == DELETE)
	    {
		ui.log("Delete: \"" + path + "\"\r\n", true, true, true, false, false);
		try { Files.delete(path); } catch (IOException ex) { ui.log("Error: postVisitDirectory: " + path.toAbsolutePath().toString() + " due to: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
		if ((keyFCPath != null) && (keyFCPath.isValidKeyDir))
		{
		    Path autoKeyDir = Paths.get(keyFCPath.path.toAbsolutePath().toString(), path.toAbsolutePath().toString().replace(":", ""));
		    if (Files.exists(autoKeyDir, LinkOption.NOFOLLOW_LINKS))
		    {
			ui.log("Delete: \"" + autoKeyDir + "\"\r\n", true, true, true, false, false);
			    try { Files.delete(autoKeyDir); } catch (IOException ex) { ui.log("Error: visitFile(.. ) Failed dir: " + autoKeyDir.toAbsolutePath().toString() + " due to: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
		    }
		}
	    }
	    else if (function==SCAN)	{ if (setFCPathlist) {     } }
	    else if (function==BACKUP)	{  }
	    else			{ ui.log("Huh? this shouldn't have happened. Neither booleans: delete & returnpathlist are present?\r\n", true, true, false, false, false); }
	    
	    return FileVisitResult.CONTINUE;
	}
	else { targetFCPathList.clear(); return FileVisitResult.TERMINATE; } 
    }    
}

