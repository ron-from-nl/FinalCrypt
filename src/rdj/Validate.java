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
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

public class Validate
{
    private static Path selectedKeyPath;
    public static long bytesCount;
    private static MySimpleFCFileVisitor mySimpleFCFileVisitor;
    

    public static void validateBuild(UI ui, FCPathList targetFCPathList, FCPath keyFCPath, boolean printgpt, boolean deletegpt)
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
//        if ( ! validfile ) { if ( report )						    { ui.log("Warning: " + path.toString() + ": " + conditions + "\r\n", true, true, false, false, false); } }                    
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
	    else									    { fileSize = 0; try { fileSize = Files.size(path); } catch (IOException ex)  { ui.log("Error: Validate: IOException: Files.size(" + path.toString() + ") Size: " + fileSize + "<" + minSize + " "+ ex.getMessage() + "\r\n", true, true, true, true, false); } }
            if ( fileSize < minSize )							    { validfile = false; size = path.toString() + " smaller than " + minSize + " byte "; conditions += size; }
            if ( ! Files.isReadable(path) )						    { validfile = false; read = "[not readable] "; conditions += read; }
            if ((! isKey) && (writable) && ( ! Files.isWritable(path)))			    { validfile = false; write = "[not writable] "; conditions += write; }
            if ( (! symlink) && (Files.isSymbolicLink(path)) )				    { validfile = false; symbolic = "[symlink] "; conditions += symbolic; }
        }

	if ( ! validfile )
	{ 
	    if ( report )
//	    { ui.error("Warning: Validate.isValidFile(...): " + caller + " Invalid File: " + targetSourcePath.toAbsolutePath().toString() + ": " + conditions + "\r\n"); } 
//	    { ui.status("Warning: " + caller + " " + path.toString() + ": " + conditions + "\r\n", true); } 
	    { ui.log("Warning: " + path.toString() + ": " + conditions + "\r\n", true, true, false, false, false); } 
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
	
        ByteBuffer plainTextMACBuffer = ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V2.length()); plainTextMACBuffer.clear();
        ByteBuffer encryptedMACBuffer = ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V2.length()); encryptedMACBuffer.clear();
	
	long readTargetSourceChannelTransfered = 0;
	
	// Create Target Source MAC Buffer
	try (final SeekableByteChannel readTargetSourceChannel = Files.newByteChannel(targetSourcePath, EnumSet.of(StandardOpenOption.READ)))
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
	else													{ macVersion = 0; targetSourceHasMAC = false; }

//	return targetSourceHasMAC;
	return macVersion;
    }
    
    synchronized public static boolean targetHasAuthenticatedMACToken(UI ui, Path targetSourcePath, Path keySourcePath, int macVersion) // Tested
    {
	FinalCrypt.resetPwdPos();
	
	boolean readTargetSourceChannelError = false;
	boolean keyAuthenticatedTargetSource =   false;
        ByteBuffer targetSrcMACBuffer =		    ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V2.length() * 2); targetSrcMACBuffer.clear();
        ByteBuffer targetPlainTextMACBuffer =	    ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V2.length()); targetPlainTextMACBuffer.clear();
        ByteBuffer targetEncryptedMACBuffer =	    ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V2.length()); targetEncryptedMACBuffer.clear();
        ByteBuffer keySourceBuffer =		    ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V2.length()); keySourceBuffer.clear();
        ByteBuffer keyDecryptedMACBuffer =	    ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V2.length()); keyDecryptedMACBuffer.clear();
	
	long readTargetSourceChannelPosition = 0;   long readTargetSourceChannelTransfered = 0;
	long readKeySourceChannelPosition = 0;	    long readKeySourceChannelTransfered = 0;                
	
	// Create Target Source MAC Buffer
	try (final SeekableByteChannel readTargetSourceChannel = Files.newByteChannel(targetSourcePath, EnumSet.of(StandardOpenOption.READ)))
	{
	    // Fill up inputFileBuffer
//	    readTargetSourceChannel.position(readTargetSourceChannelPosition);
//	    readTargetSourceChannelTransfered = readTargetSourceChannel.read(targetSrcMACBuffer); targetSrcMACBuffer.flip();
	    readTargetSourceChannel.read(targetSrcMACBuffer); targetSrcMACBuffer.flip();
	    readTargetSourceChannel.close(); 
	} catch (IOException ex) { readTargetSourceChannelError = true; ui.log("Error: targetHasMAC: readTargetSourceChannel " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	
	// Encrypted MAC Buffer
	
	targetPlainTextMACBuffer.put(targetSrcMACBuffer.array(),									0, FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V2.length()); targetPlainTextMACBuffer.flip();
	targetEncryptedMACBuffer.put(targetSrcMACBuffer.array(), FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V2.length(), FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V2.length()); targetEncryptedMACBuffer.flip();
	
//	Read in Key
	if (( ! readTargetSourceChannelError ) && ( ! Files.isDirectory(keySourcePath)) )
	{
	    try (final SeekableByteChannel readKeySourceChannel = Files.newByteChannel(keySourcePath, EnumSet.of(StandardOpenOption.READ)))
	    {
		// Fill up keyFileBuffer
//		readKeySourceChannel.position(readKeySourceChannelPosition);
//		readKeySourceChannelTransfered = readKeySourceChannel.read(keySourceBuffer);
		readKeySourceChannel.read(keySourceBuffer);
		keySourceBuffer.flip(); readKeySourceChannel.close();
	    } catch (IOException ex) { ui.log("Error: keyAuthenticatedTargetSource readKeySourceChannel " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    
	    // Create Encrypted Token Buffer
	    keyDecryptedMACBuffer = FinalCrypt.encryptBuffer(targetEncryptedMACBuffer, keySourceBuffer, macVersion, false);
	    String keyDecryptedMACBufferString = new String(keyDecryptedMACBuffer.array(), StandardCharsets.UTF_8);
//	    ui.status("targetHasAuthenticatedMAC.keyDecryptedMACBufferString: " + keyDecryptedMACBufferString + "\r\n", true);
	    
	    // Authenticate Key MAC against Target MAC
//	    if ( keyDecryptedMACBufferString.equals(FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE)) { keyAuthenticatedTargetSource = true; } else { keyAuthenticatedTargetSource = false; }
	    if ( keyDecryptedMACBufferString.equals(StandardCharsets.UTF_8.decode(targetPlainTextMACBuffer).toString())) { keyAuthenticatedTargetSource = true; } else { keyAuthenticatedTargetSource = false; }
	    
	} else { keyAuthenticatedTargetSource = false; }
	
	FinalCrypt.resetPwdPos();
	return keyAuthenticatedTargetSource;
    }


    // Synchronized removes multifile target inconsistency, but also smooth busy animation
    public static void buildSelection(UI ui, ArrayList<Path> pathList, FCPath keyFCPath, FCPathList targetFCPathList, boolean symlink, String pattern, boolean negatePattern, boolean disabledMAC, boolean status)
    {
//	if (mySimpleFCFileVisitor != null) {mySimpleFCFileVisitor.running = false;} else {mySimpleFCFileVisitor.running = false;} // Being set within MySimpleFCFileVisitor instantiation
//				    MySimpleFCFileVisitor(UI ui, boolean verbose, boolean delete, boolean symlink, boolean setFCPathlist, Path keyPath, ArrayList<FCPath> targetFCPathList, String pattern, boolean negatePattern,  boolean disabledMAC)
	mySimpleFCFileVisitor = new MySimpleFCFileVisitor(   ui,	   false,          false,         symlink,                  true,    keyFCPath,                   targetFCPathList,	   pattern,         negatePattern,	    disabledMAC);
	
	for (Path path:pathList)
	{
	    try{ Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS,FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, mySimpleFCFileVisitor);} catch(IOException e) { ui.log("Error: Validate.buildSelection: Files.walkFileTree(path, EnumSet.of(..) " + e.getMessage() + "\r\n", true, true, true, true, false); }
	}
	mySimpleFCFileVisitor.running = false;
	ui.buildReady(targetFCPathList, true);
	
//	if ( (targetFCPathList.size() > 0) && (mySimpleFCFileVisitor.running) )
//	if ( (targetFCPathList.size() > 0) )
//	{ 
//	    ui.buildReady(targetFCPathList);
//	}
//	else
//	{
//	    targetFCPathList = new FCPathList(); ui.buildReady(targetFCPathList);
//	}	
    }

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
	    else if (path.toAbsolutePath().toString().startsWith("/dev/disk")) // Apple Key Device Selection
	    {
		if  (
			(Character.isDigit(path.getFileName().toString().charAt(path.getFileName().toString().length()-1))) &&
			(String.valueOf(path.getFileName().toString().charAt(path.getFileName().toString().length()-2)).equalsIgnoreCase("s"))
		    )
		{ returnFCPathType = FCPath.PARTITION; }
		else { if ( ! path.getFileName().toString().endsWith("disk0")) { returnFCPathType = FCPath.DEVICE; } else { returnFCPathType = FCPath.DEVICE_PROTECTED; } }
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
	boolean isValidFile =		    false;
	boolean isValidDevice =		    false;
	boolean isValidDeviceProtected =    false;
	boolean isValidPartition =	    false;

	boolean isDecrypted =		    false;
	boolean isEncryptable =		    false;
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

        if ( Files.exists(path, LinkOption.NOFOLLOW_LINKS) ) // Does not check if symbolic link target file exist
//        if ( Files.exists(path) )
	{
	    exist = true;
	    type = getFCPathType(path);
	    if (exist)
	    {
		if ( (type == FCPath.PARTITION) || (type == FCPath.DEVICE) || (type == FCPath.DEVICE_PROTECTED) )   { size = DeviceController.getDeviceSize(ui, path, isKey); }
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
		if (disabledMAC)    { if (( exist ) && ( size >= FCPath.KEY_SIZE_MIN )					&& ( readable ) ) { isValid = true; } else { isValid = false; isUnEncryptable = true; isUnDecryptable = true; } }
		else		    { if (( exist ) && ( size >= FCPath.KEY_SIZE_MIN ) && ( size >= FCPath.MAC_SIZE )	&& ( readable ) ) { isValid = true; } else { isValid = false; isUnEncryptable = true; isUnDecryptable = true; }}
	    }
	    else	    { if (( exist ) && ( size >  0 ) && ( readable )   && ( writable ))	    { isValid = true; } else { isValid = false; isUnEncryptable = true; isUnDecryptable = true; } }
	    
	    // File validity
	    if (( isValid ) && ( type == FCPath.FILE ))								{ isValidFile = true; } else { isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true; }
	    
	    //DeviceProtected validity
	    if (( isValid ) &&	( ( type == FCPath.DEVICE_PROTECTED ) ) && ( size >= FCPath.KEY_SIZE_MIN ) )	{ isValidDeviceProtected = true;  isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true; }

	    //Device validity
	    if (( isValid ) &&	( ( type == FCPath.DEVICE ) )		&& ( size >= FCPath.KEY_SIZE_MIN ) )	{ isValidDevice = true;		  isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true; }

	    //Partition validity
	    if (( isValid ) &&	( type == FCPath.PARTITION ) && ( size >= FCPath.KEY_SIZE_MIN ) )		{ isValidPartition = true;  isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true; }

	    // Decrypted File State
	    
	    if (( isValidFile )	    && ( ! isEncrypted ))   { isDecrypted = true; isUnEncryptable = false; isDecryptable = false; isEncryptable = true; isUnDecryptable = true; }
	    
	    // Encrypted File State
	    
	    if (( isValidFile ))
	    {
		macVersion = targetSourceHasMAC(ui, path);
		if ( macVersion == 0 ) { isEncrypted = false; } else { isEncrypted = true; }
		
		if ((isEncrypted) && (keyPath != null)  && (size > (FCPath.MAC_SIZE))) { if (keyPath != null) isDecryptable = targetHasAuthenticatedMACToken(ui, path, keyPath, macVersion); } // Encrypted files must by MAC_SIZE at least
	    }
	    if (( isValidFile ) && ( isEncrypted ) && ( ! isDecryptable ))								{ isEncrypted = true; isDecryptable = false; isDecrypted = false; isEncryptable = false; isUnEncryptable = true; isUnDecryptable = true; }
	    if (( isValidFile )	&& ( isEncrypted ) && (   isDecryptable ))								{ isEncrypted = true; isDecryptable = true;  isDecrypted = false; isEncryptable = false; isUnEncryptable = true; isUnDecryptable = false; }
	    
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
	else { }

// Return FCPath =============================================================================================================================================================================================

//				    Path path,boolean exist,int type,long size,boolean readable,boolean writable,boolean isHidden,boolean matchesKey,boolean isValid,boolean isValidFile, boolean isValidDeviceProtected, boolean isValidDevice, boolean isValidPartition, boolean isKey, boolean isValidKey, boolean isDecrypted, boolean isEncryptable, boolean isNewEncrypted, boolean isUnEncryptable, boolean isEnacrypted, int macVersion, boolean isDecryptable, boolean isNewDecrypted, boolean isUnEncryptable
	FCPath	fcPath = new FCPath(     path,        exist,    type,     size,        readable,        writable,        isHidden,        matchKey,          isValid,        isValidFile,         isValidDeviceProtected,         isValidDevice,         isValidPartition, isKey,         isValidKey,         isDecrypted,         isEncryptable,         isNewEncrypted,	    isUnEncryptable,                 isEncrypted,    macVersion,     isDecryptable,	 isNewDecrypted,         isUnDecryptable);
	return fcPath;
    }

    public static String getSting(FCPath fcPath)
    {
	String returnString = "";
	returnString += "FCPath:\r\n";
	returnString += "\r\n";
	returnString += "Path:			" + fcPath.path.toString() + "\r\n";
	returnString += "Exist:			" + fcPath.exist + "\r\n";
	returnString += "Type:			" + fcPath.getTypeString(fcPath.type) + "\r\n";
	returnString += "Size:			" + Validate.getHumanSize(fcPath.size, 1) + "\r\n";
	returnString += "Readable:		" + fcPath.isReadable + "\r\n";
	returnString += "Writable:		" + fcPath.isWritable + "\r\n";
	returnString += "Hidden:			" + fcPath.isHidden + "\r\n";
	returnString += "Match Key:		" + fcPath.matchKey + "\r\n";
	returnString += "\r\n";
	returnString += "Valid Path:		" + fcPath.isValidPath + "\r\n";
	returnString += "Valid File:		" + fcPath.isValidFile + "\r\n";
	returnString += "Valid Device:		" + fcPath.isValidDevice + "\r\n";
	returnString += "Valid Partition:	" + fcPath.isValidPartition + "\r\n";
	returnString += "Is Key:		" + fcPath.isKey + "\r\n";
	returnString += "Valid Key:		" + fcPath.isValidKey + "\r\n";
	returnString += "\r\n";
	returnString += "Decrypted:		" + fcPath.isDecrypted + "\r\n";
	returnString += "Encryptable:		" + fcPath.isEncryptable + "\r\n";
	returnString += "New Encrypted:		" + fcPath.isNewEncrypted + "\r\n";
	returnString += "UnEncryptable:		" + fcPath.isUnEncryptable + "\r\n";
	returnString += "\r\n";
//	returnString += "Has FCToken:		" + fcPath.hasFCToken + "\r\n";
	returnString += "Encrypted:		" + fcPath.isEncrypted + "\r\n";
//	returnString += "Authenticated:		" + fcPath.isAuthenticated + "\r\n";
	returnString += "Decryptable:		" + fcPath.isDecryptable + "\r\n";
	returnString += "New Decrypted:		" + fcPath.isNewDecrypted + "\r\n";
	returnString += "UnDecryptable:		" + fcPath.isUnDecryptable + "\r\n";
	returnString += "\r\n";

	return returnString;
    }
    
    synchronized public static String getFCPathStatus(FCPath fcPath)
    {
	String returnString = "";

//	String[] columnNames = { "Path", "Exist ", "Type ", "Size ", "Readable "};
//	Object[][] data = {{path.toString(), exist, getTypeString(type), size, readable, isValidKey}};

	
        returnString += (String.format("%-2s%-40s%-3s%-6s%-3s%-17s%-3s%-12s%-3s%-9s%-3s%-6s%-2s\r\n", "|-", "----------------------------------------",	"-|-", "------", "-|-", "-----------------",   "-|-", "------------",	"-|-", "---------",	"-|-", "------",		"-|"));
        returnString += (String.format("%-2s%-40s%-3s%-6s%-3s%-17s%-3s%-12s%-3s%-9s%-3s%-6s%-2s\r\n", "| ", "Path",					" | ", "Exist ", " | ", "Type",		       " | ", "Size",		" | ", "Readable ",	" | ", "Valid ",		" |"));
        returnString += (String.format("%-2s%-40s%-3s%-6s%-3s%-17s%-3s%-12s%-3s%-9s%-3s%-6s%-2s\r\n", "|-", "----------------------------------------",	"-|-", "------", "-|-", "-----------------",   "-|-", "------------",	"-|-", "---------",	"-|-", "------",		"-|"));
        returnString += (String.format("%-2s%-40s%-3s%-6s%-3s%-17s%-3s%-12s%-3s%-9s%-3s%-6s%-2s\r\n", "| ", fcPath.path.toString(),			" | ", b(fcPath.exist), " | ", t(fcPath.type), " | ", s(fcPath.size),	" | ", b(fcPath.isReadable), " | ", b(fcPath.isValidKey),	" |"));
        returnString += (String.format("%-2s%-40s%-3s%-6s%-3s%-17s%-3s%-12s%-3s%-9s%-3s%-6s%-2s\r\n", "|-", "----------------------------------------",	"-|-", "------", "-|-", "-----------------",   "-|-", "------------",	"-|-", "---------",	"-|-", "------",		"-|"));

	return returnString;
    }
    
    private static String b(boolean b)	{ return Boolean.toString(b); }
    private static String t(int t)	{ return FCPath.getTypeString(t); }
    private static String s(long s)	{ return Validate.getHumanSize(s, 1); }
}


// override only methods of our need (SimpleFileVisitor is a full blown class)
class MySimpleFCFileVisitor extends SimpleFileVisitor<Path>
{
    private final UI ui;
    private final PathMatcher pathMatcher;
    private final boolean verbose; 
    private final boolean delete; 
    private final boolean symlink; 
    private final boolean setFCPathlist; 
    public FCPath keyFCPath;
    private FCPathList targetFCPathList;
    private boolean negatePattern;
    public long bytesCount = 0;
    public static boolean running = false; 
    private static boolean disabledMAC = false; 

//  regex pattern
//  all *.bit   =   'regex:^.*\.bit$'
//  all but *.bit   'regex:(?!.*\.bit$)^.*$'
    
    public MySimpleFCFileVisitor(UI ui, boolean verbose, boolean delete, boolean symlink, boolean setFCPathlist, FCPath keyFCPath, FCPathList targetFCPathList, String pattern, boolean negatePattern, boolean disabledMAC)
    {
        this.ui = ui;
        pathMatcher = FileSystems.getDefault().getPathMatcher(pattern); // "glob:" or "regex:" included in pattern
        this.verbose = verbose;
        this.delete = delete;
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
	    if	(delete)	{ return FileVisitResult.CONTINUE; }
	    else if (setFCPathlist)	{ if ( Validate.isValidDir(ui, path, symlink, true) ) { return FileVisitResult.CONTINUE; } else { return FileVisitResult.SKIP_SUBTREE; } }
	    else			{ ui.log("Huh? this shouldn't have happened. Neither booleans: delete & returnpathlist are present?\r\n", true, true, false, false, false); return FileVisitResult.CONTINUE; }
	}
	else { targetFCPathList.clear(); return FileVisitResult.TERMINATE; }
    }    
    
    @Override public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
    {
	if (running)
	{
	    if ( (path.getFileName() != null ) && ( negatePattern ^ pathMatcher.matches(path.getFileName())) ) // ^ = XOR just reverses the match when -W instead of -w if given in CLUI
	    {            
		if	(delete)                 { try { Files.delete(path); } catch (IOException ex) { ui.log("Error: visitFile(.. ) Failed file: " + path.toString() + " due to: " + ex.getMessage() + "\r\n", true, true, true, true, false); } }
		else if (setFCPathlist)    
		{
//    					     getFCPath(UI ui, String caller, Path path, boolean isKey,	 Path keyPath, boolean disabledMAC, boolean report)
		    FCPath fcPath = Validate.getFCPath(   ui,            "",      path,            false, this.keyFCPath.path, disabledMAC,          true); targetFCPathList.add(fcPath);
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
	    if      (delete)        { try { Files.delete(path); } catch (IOException ex) { ui.log("Error: postVisitDirectory: " + path.toString() + " due to: " + ex.getMessage() + "\r\n", true, true, true, true, false); } }
	    else if (setFCPathlist) {     }
	    else { ui.log("Huh? this shouldn't have happened. Neither booleans: delete & returnpathlist are present?\r\n", true, true, false, false, false); }
	    
	    return FileVisitResult.CONTINUE;
	}
	else { targetFCPathList.clear(); return FileVisitResult.TERMINATE; } 
    }    
}

