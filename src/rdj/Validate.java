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
    private static Path selectedCipherPath;
    public static long bytesCount;
    private static MySimpleFCFileVisitor mySimpleFCFileVisitor;
    

    public static void validateBuild(UI ui, FCPathList targetFCPathList, FCPath cipherFCPath, boolean printgpt, boolean deletegpt)
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
        if ( ! validdir )								    { if ( report ) { ui.status("Warning: skipping dir: " + targetDirPath.toString() + ": " + conditions + "\r\n", true); } }
        return validdir;
    }

    synchronized public static boolean isValidFile(UI ui, String caller, Path path, Path cipherPath, boolean device, long minSize, boolean symlink, boolean writable, boolean report) // fileValidation Wrapper (including target==cipherSource comparison)
    {
	
        boolean validfile = true; String conditions = "";				    String cipher = "";
	validfile = isValidFile(ui, caller, path, device, minSize, symlink, writable, report);
	if ((cipherPath != null) && validfile) { if (path.compareTo(cipherPath) == 0) { validfile = false; cipher = "[is cipher] "; conditions += cipher; }}	
        if ( ! validfile ) { if ( report )						    { ui.status("Warning: " + path.toString() + ": " + conditions + "\r\n", true); } }                    
        return validfile;
    }

    synchronized public static boolean isValidFile(UI ui, String caller, Path path, boolean device, long minSize, boolean symlink, boolean writable, boolean report)
    {
        boolean validfile = true; String conditions = "";				    String size = ""; String exist = ""; String dir = ""; String read = ""; String write = ""; String symbolic = ""; String cipher = "";

        if ( ! Files.exists(path))							    { validfile = false; exist = "[not found] "; conditions += exist; }
        else
        {
            if ( Files.isDirectory(path))						    { validfile = false; dir = "[is directory] "; conditions += dir; }
	    long fileSize = 0; if ( device )						    { fileSize = 0; fileSize = DeviceController.getDeviceSize(ui, path); }
	    else									    { fileSize = 0; try { fileSize = Files.size(path); } catch (IOException ex)  { ui.error("Error: Validate: IOException: Files.size(" + path.toString() + ") Size: " + fileSize + "<" + minSize + " "+ ex.getMessage() + "\r\n"); } }
            if ( fileSize < minSize )							    { validfile = false; size = path.toString() + " smaller than " + minSize + " byte "; conditions += size; }
            if ( ! Files.isReadable(path) )						    { validfile = false; read = "[not readable] "; conditions += read; }
            if ((writable) && ( ! Files.isWritable(path)))				    { validfile = false; write = "[not writable] "; conditions += write; }
            if ( (! symlink) && (Files.isSymbolicLink(path)) )				    { validfile = false; symbolic = "[symlink] "; conditions += symbolic; }
        }

	if ( ! validfile )
	{ 
	    if ( report )
//	    { ui.error("Warning: Validate.isValidFile(...): " + caller + " Invalid File: " + targetSourcePath.toAbsolutePath().toString() + ": " + conditions + "\r\n"); } 
//	    { ui.status("Warning: " + caller + " " + path.toString() + ": " + conditions + "\r\n", true); } 
	    { ui.status("Warning: " + path.toString() + ": " + conditions + "\r\n", true); } 
	}                    
        return validfile;
    }
    
//___________________________________________________________________________________________________________________________________________________________
//
//			Testing FinalCrypt Token
//			🔒   Encrypt
//			🔓   Decrypt	    (Cipher Authenticated)
//			🔓!  Decrypt Legacy  (Cipher can't be checked! No Token present in old format)
//			⛔   Decrypt Abort   (Cipher Failed)

    synchronized public static boolean targetSourceHasFCToken(UI ui, Path targetSourcePath) // Tested
    {
	
	boolean targetSourceHasToken = false;
	
        ByteBuffer plainTextTokenBuffer = ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length()); plainTextTokenBuffer.clear();
        ByteBuffer encryptedTokenBuffer = ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length()); encryptedTokenBuffer.clear();
	
	long readTargetSourceChannelTransfered = 0;
	
	// Create Target Source Token Buffer
	try (final SeekableByteChannel readTargetSourceChannel = Files.newByteChannel(targetSourcePath, EnumSet.of(StandardOpenOption.READ)))
	{
	    // Fill up inputFileBuffer
	    readTargetSourceChannelTransfered = readTargetSourceChannel.read(plainTextTokenBuffer); plainTextTokenBuffer.flip();
	    if ( readTargetSourceChannelTransfered != plainTextTokenBuffer.capacity() ) { return false; }
	    readTargetSourceChannel.close(); 
	} catch (IOException ex) { ui.error("Error: targetSourceHasToken: readTargetSourceChannel " + ex.getMessage() + "\r\n"); }
	
	// Compare plainTextTokenBuffer to FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN
	String plainTextTokenString = new String(plainTextTokenBuffer.array(), StandardCharsets.UTF_8);
//	ui.status("targetSourceHasToken plainTextTokenString: " +plainTextTokenString + "\r\n", true);

	if ( plainTextTokenString.equals(FinalCrypt.FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN) ) { targetSourceHasToken = true; }

	return targetSourceHasToken;
    }
    
    synchronized public static boolean targetHasAuthenticatedFCToken(UI ui, Path targetSourcePath, Path cipherSourcePath) // Tested
    {
	boolean readTargetSourceChannelError = false;
	boolean cipherAuthenticatedTargetSource =   false;
        ByteBuffer targetSrcTokenBuffer =	    ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length() * 2); targetSrcTokenBuffer.clear();
        ByteBuffer targetEncryptedTokenBuffer =	    ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length()); targetEncryptedTokenBuffer.clear();
        ByteBuffer cipherSourceBuffer =		    ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length()); cipherSourceBuffer.clear();
        ByteBuffer cipherDecryptedTokenBuffer =	    ByteBuffer.allocate(FinalCrypt.FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length()); cipherDecryptedTokenBuffer.clear();
	
	long readTargetSourceChannelPosition = 0;	long readTargetSourceChannelTransfered = 0;
	long readCipherSourceChannelPosition = 0;	long readCipherSourceChannelTransfered = 0;                
	
	// Create Target Source Token Buffer
	try (final SeekableByteChannel readTargetSourceChannel = Files.newByteChannel(targetSourcePath, EnumSet.of(StandardOpenOption.READ)))
	{
	    // Fill up inputFileBuffer
//	    readTargetSourceChannel.position(readTargetSourceChannelPosition);
//	    readTargetSourceChannelTransfered = readTargetSourceChannel.read(targetSrcTokenBuffer); targetSrcTokenBuffer.flip();
	    readTargetSourceChannel.read(targetSrcTokenBuffer); targetSrcTokenBuffer.flip();
	    readTargetSourceChannel.close(); 
	} catch (IOException ex) { readTargetSourceChannelError = true; ui.error("Error: targetHasAuthenticatedToken: readTargetSourceChannel " + ex.getMessage() + "\r\n"); }
	
	// Encrypted Token Buffer
	targetEncryptedTokenBuffer.put(targetSrcTokenBuffer.array(), FinalCrypt.FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length(), FinalCrypt.FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length()); targetEncryptedTokenBuffer.flip();
	
	if ( ! readTargetSourceChannelError )
	{
	    try (final SeekableByteChannel readCipherSourceChannel = Files.newByteChannel(cipherSourcePath, EnumSet.of(StandardOpenOption.READ)))
	    {
		// Fill up cipherFileBuffer
//		readCipherSourceChannel.position(readCipherSourceChannelPosition);
//		readCipherSourceChannelTransfered = readCipherSourceChannel.read(cipherSourceBuffer);
		readCipherSourceChannel.read(cipherSourceBuffer);
		cipherSourceBuffer.flip(); readCipherSourceChannel.close();
	    } catch (IOException ex) { ui.error("Error: cipherAuthenticatedTargetSource readCipherSourceChannel " + ex.getMessage() + "\r\n"); }
	    
	    // Create Encrypted Token Buffer
	    cipherDecryptedTokenBuffer = FinalCrypt.encryptBuffer(targetEncryptedTokenBuffer, cipherSourceBuffer);
	    String cipherDecryptedTokenBufferString = new String(cipherDecryptedTokenBuffer.array(), StandardCharsets.UTF_8);
//	    ui.status("targetHasAuthenticatedToken.cipherDecryptedTokenBufferString: " + cipherDecryptedTokenBufferString + "\r\n", true);
	    
	    // Authenticate Cipher Token against Target Token
	    if ( cipherDecryptedTokenBufferString.equals(FinalCrypt.FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN)) { cipherAuthenticatedTargetSource = true; } else { cipherAuthenticatedTargetSource = false; }
	    
	} else { cipherAuthenticatedTargetSource = false; }
	
	return cipherAuthenticatedTargetSource;
    }


    
    public static void buildSelection(UI ui, ArrayList<Path> pathList, FCPath cipherFCPath, FCPathList targetFCPathList, boolean symlink, String pattern, boolean negatePattern, boolean status)
    {
	if (mySimpleFCFileVisitor != null) {mySimpleFCFileVisitor.running = false;} else {mySimpleFCFileVisitor.running = false;}
//				    MySimpleFCFileVisitor(UI ui, boolean verbose, boolean delete, boolean symlink, boolean setFCPathlist, Path cipherPath, ArrayList<FCPath> targetFCPathList, String pattern, boolean negatePattern)
	mySimpleFCFileVisitor = new MySimpleFCFileVisitor(   ui,	     false,         false,          symlink,                  true,    cipherFCPath,                   targetFCPathList,	pattern,         negatePattern);
	for (Path path:pathList)
	{
	    try{ Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS,FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, mySimpleFCFileVisitor);} catch(IOException e) { ui.error("Error: Validate.buildSelection: Files.walkFileTree(path, EnumSet.of(..) " + e.getMessage() + "\r\n"); }
	}
	if ( (targetFCPathList.size() > 0) && (mySimpleFCFileVisitor.running) ) { ui.buildReady(targetFCPathList); } else { targetFCPathList = new FCPathList(); ui.buildReady(targetFCPathList); }
	
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
	    if	    (path.toAbsolutePath().toString().startsWith("/dev/sd")) // Linux Cipher Device Selection
	    {
		if  (Character.isDigit(path.getFileName().toString().charAt(path.getFileName().toString().length()-1))) { returnFCPathType = FCPath.PARTITION; }
		else { if ( ! path.getFileName().endsWith("sda")) { returnFCPathType = FCPath.DEVICE; } else { returnFCPathType = FCPath.DEVICE_PROTECTED; } }
	    }
	    else if (path.toAbsolutePath().toString().startsWith("/dev/disk")) // Apple Cipher Device Selection
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
    
    synchronized public static FCPath getFCPath(UI ui, String caller, Path path, boolean isCipher, Path cipherPath, boolean report)
    {
	boolean exist =			    false;
	int	type =			    FCPath.INVALID;
	long    size =			    0;
	boolean readable =		    false;
	boolean writable =		    false;
	boolean isHidden =		    false;
	boolean matchCipher =		    false;
	
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
//	boolean isAuthenticated =	    false;
	boolean isDecryptable =		    false;
	boolean isNewDecrypted =	    false;	
	boolean isUnDecryptable =	    false;	
	
	boolean isValidCipher =		    false;	

        if ( Files.exists(path, LinkOption.NOFOLLOW_LINKS) ) // Does not check if symbolic link target file exist
//        if ( Files.exists(path) )
	{
	    exist = true;
	    type = getFCPathType(path);
	    if (exist)
	    {
		if ( (type == FCPath.PARTITION) || (type == FCPath.DEVICE) || (type == FCPath.DEVICE_PROTECTED) )   { size = DeviceController.getDeviceSize(ui, path); }
		else if( (exist) && ((type == FCPath.FILE) /*|| (type == FCPath.SYMLINK)*/) )			    { try { size = Files.size(path); } catch (IOException ex)  { ui.error("Error: IOException: Validate.getFCPath: Files.size() "+ ex.getMessage() + "\r\n"); } } // Symlinks give Files.size() errors on broken links
	    }
	    
	    readable = Files.isReadable(path);
	    writable = Files.isWritable(path);
	    try { isHidden = Files.isHidden(path); } catch (IOException ex)					    { ui.error("Error: IOException: Validate.getFCPath: Files.isHidden(path) "+ ex.getMessage() + "\r\n"); } // SoftDown.eu error
	    
	    // Target =============================================================================================================================================================================================
	    
	    // isValid in general
	    if (( exist ) && ( size >  0 ) && ( readable ) && ( writable ))					{ isValid = true; } else { isValid = false; isUnEncryptable = true; isUnDecryptable = true; }
	    
	    // File validity
	    if (( isValid ) && ( type == FCPath.FILE ))								{ isValidFile = true; } else { isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true; }
	    
	    //DeviceProtected validity
	    if (( isValid ) &&	( ( type == FCPath.DEVICE_PROTECTED ) ) && ( size >= FCPath.CIPHER_SIZE_MIN ) )	{ isValidDeviceProtected = true;  isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true; }

	    //Device validity
	    if (( isValid ) &&	( ( type == FCPath.DEVICE ) )		&& ( size >= FCPath.CIPHER_SIZE_MIN ) )	{ isValidDevice = true;		  isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true; }

	    //Partition validity
	    if (( isValid ) &&	( type == FCPath.PARTITION ) && ( size >= FCPath.CIPHER_SIZE_MIN ) )		{ isValidPartition = true;  isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true; }

	    // Decrypted File State
	    
	    if (( isValidFile )	    && ( ! isEncrypted ))   { isDecrypted = true; isUnEncryptable = false; isDecryptable = false; isEncryptable = true; isUnDecryptable = true; }
	    
	    // Encrypted File State
	    
	    if (( isValidFile ))			    { isEncrypted = targetSourceHasFCToken(ui, path); if ((isEncrypted) && (cipherPath != null) && (size > (FinalCrypt.FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length() * 2))) { if (cipherPath != null) isDecryptable = targetHasAuthenticatedFCToken(ui, path, cipherPath); } }
	    if (( isValidFile ) && ( isEncrypted ) && ( ! isDecryptable ))								{ isEncrypted = true; isDecryptable = false; isDecrypted = false; isEncryptable = false; isUnEncryptable = true; isUnDecryptable = true; }
	    if (( isValidFile )	&& ( isEncrypted ) && (   isDecryptable ))								{ isEncrypted = true; isDecryptable = true;  isDecrypted = false; isEncryptable = false; isUnEncryptable = true; isUnDecryptable = false; }
	    
	    // Cipher =============================================================================================================================================================================================
	    
	    if ( cipherPath != null )							{ if (path.compareTo(cipherPath) == 0)   { matchCipher = true; isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true;} }
	    if ( isCipher )								{ isEncryptable = false; isUnEncryptable = true; isDecryptable = false; isUnDecryptable = true; }
	    if (	( exist )
		    &&  (
				( type == FCPath.FILE )
			    ||  ( type == FCPath.PARTITION )
			    ||	( type == FCPath.DEVICE )
			    ||	( type == FCPath.DEVICE_PROTECTED )
			)
			    && ( size >=  1024 ) && ( readable  ) && ( isCipher ) )	{ isValidCipher = true; }
	}
	else { }

// Return FCPath =============================================================================================================================================================================================

//				    Path path,boolean exist,int type,long size,boolean readable,boolean writable,boolean isHidden,boolean matchesCipher,boolean isValid,boolean isValidFile, boolean isValidDeviceProtected, boolean isValidDevice, boolean isValidPartition, boolean isCipher, boolean isValidCipher, boolean isDecrypted, boolean isEncryptable, boolean isNewEncrypted, boolean isUnEncryptable, boolean isEnacrypted, boolean isDecryptable, boolean isNewDecrypted, boolean isUnEncryptable
	FCPath	fcPath = new FCPath(     path,        exist,    type,     size,        readable,        writable,        isHidden,        matchCipher,          isValid,        isValidFile,         isValidDeviceProtected,         isValidDevice,         isValidPartition, isCipher,         isValidCipher,         isDecrypted,         isEncryptable,         isNewEncrypted,	    isUnEncryptable,                 isEncrypted,         isDecryptable,	 isNewDecrypted,         isUnDecryptable);
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
	returnString += "Match Cipher:		" + fcPath.matchCipher + "\r\n";
	returnString += "\r\n";
	returnString += "Valid Path:		" + fcPath.isValidPath + "\r\n";
	returnString += "Valid File:		" + fcPath.isValidFile + "\r\n";
	returnString += "Valid Device:		" + fcPath.isValidDevice + "\r\n";
	returnString += "Valid Partition:	" + fcPath.isValidPartition + "\r\n";
	returnString += "Is Cipher:		" + fcPath.isCipher + "\r\n";
	returnString += "Valid Cipher:		" + fcPath.isValidCipher + "\r\n";
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
//	Object[][] data = {{path.toString(), exist, getTypeString(type), size, readable, isValidCipher}};

	
        returnString += (String.format("%-2s%-40s%-3s%-6s%-3s%-17s%-3s%-12s%-3s%-9s%-3s%-6s%-2s\r\n", "|-", "----------------------------------------",	"-|-", "------", "-|-", "-----------------",   "-|-", "------------",	"-|-", "---------",	"-|-", "------",		"-|"));
        returnString += (String.format("%-2s%-40s%-3s%-6s%-3s%-17s%-3s%-12s%-3s%-9s%-3s%-6s%-2s\r\n", "| ", "Path",					" | ", "Exist ", " | ", "Type",		       " | ", "Size",		" | ", "Readable ",	" | ", "Valid ",		" |"));
        returnString += (String.format("%-2s%-40s%-3s%-6s%-3s%-17s%-3s%-12s%-3s%-9s%-3s%-6s%-2s\r\n", "|-", "----------------------------------------",	"-|-", "------", "-|-", "-----------------",   "-|-", "------------",	"-|-", "---------",	"-|-", "------",		"-|"));
        returnString += (String.format("%-2s%-40s%-3s%-6s%-3s%-17s%-3s%-12s%-3s%-9s%-3s%-6s%-2s\r\n", "| ", fcPath.path.toString(),			" | ", b(fcPath.exist), " | ", t(fcPath.type), " | ", s(fcPath.size),	" | ", b(fcPath.isReadable), " | ", b(fcPath.isValidCipher),	" |"));
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
    public FCPath cipherFCPath;
    private FCPathList targetFCPathList;
    private boolean negatePattern;
    public long bytesCount = 0;
    public static boolean running = false; 

//  regex pattern
//  all *.bit   =   'regex:^.*\.bit$'
//  all but *.bit   'regex:(?!.*\.bit$)^.*$'
    
    public MySimpleFCFileVisitor(UI ui, boolean verbose, boolean delete, boolean symlink, boolean setFCPathlist, FCPath cipherFCPath, FCPathList targetFCPathList, String pattern, boolean negatePattern)
    {
        this.ui = ui;
        pathMatcher = FileSystems.getDefault().getPathMatcher(pattern); // "glob:" or "regex:" included in pattern
        this.verbose = verbose;
        this.delete = delete;
        this.symlink = symlink;
        this.setFCPathlist = setFCPathlist;
	this.cipherFCPath = cipherFCPath;
	this.targetFCPathList = targetFCPathList;
        this.negatePattern = negatePattern;
	bytesCount = 0;
	running = true;
    }
   
    @Override public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs)
    {
	if (running)
	{
	    if	(delete)	{ return FileVisitResult.CONTINUE; }
	    else if (setFCPathlist)	{ if ( Validate.isValidDir(ui, path, symlink, true) ) { return FileVisitResult.CONTINUE; } else { return FileVisitResult.SKIP_SUBTREE; } }
	    else			{ ui.status("Huh? this shouldn't have happened. Neither booleans: delete & returnpathlist are present?\r\n", true); return FileVisitResult.CONTINUE; }
	}
	else { targetFCPathList.clear(); return FileVisitResult.TERMINATE; }
    }    
    
    @Override public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
    {
	if (running)
	{
	    if ( (path.getFileName() != null ) && ( negatePattern ^ pathMatcher.matches(path.getFileName())) ) // ^ = XOR just reverses the match when -W instead of -w if given in CLUI
	    {            
		if	(delete)                 { try { Files.delete(path); } catch (IOException ex) { ui.error("Error: visitFile(.. ) Failed file: " + path.toString() + " due to: " + ex.getMessage() + "\r\n"); } }
		else if (setFCPathlist)    
		{
//    					     getFCPath(UI ui, String caller, Path path, boolean isCipher,	 Path cipherPath, boolean report)
		    FCPath fcPath = Validate.getFCPath(   ui,            "",      path,            false, this.cipherFCPath.path,           true); targetFCPathList.add(fcPath);
		}
		else { ui.status("Huh? this shouldn't have happened. Neither booleans: delete & returnpathlist are present?\r\n", true); }
	    }
	    return FileVisitResult.CONTINUE;
	}
	else { targetFCPathList.clear(); return FileVisitResult.TERMINATE; } 
    }
    
    @Override public FileVisitResult visitFileFailed(Path path, IOException exc)
    {
	if (running)
	{
//					     getFCPath(UI ui, String caller, Path path, boolean isCipher,	     Path cipherPath, boolean report)
	    FCPath fcPath = Validate.getFCPath(   ui,            "",      path,            false, this.cipherFCPath.path,           true); targetFCPathList.add(fcPath);
	    return FileVisitResult.SKIP_SIBLINGS;
	}
	else { targetFCPathList.clear(); return FileVisitResult.TERMINATE; } 
    }
    
    @Override public FileVisitResult postVisitDirectory(Path path, IOException exc)
    {
	if (running)
	{
	    if      (delete)        { try { Files.delete(path); } catch (IOException ex) { ui.error("Error: postVisitDirectory: " + path.toString() + " due to: " + ex.getMessage() + "\r\n"); } }
	    else if (setFCPathlist) {     }
	    else { ui.status("Huh? this shouldn't have happened. Neither booleans: delete & returnpathlist are present?\r\n", true); }
	    
	    return FileVisitResult.CONTINUE;
	}
	else { targetFCPathList.clear(); return FileVisitResult.TERMINATE; } 
    }    
}

