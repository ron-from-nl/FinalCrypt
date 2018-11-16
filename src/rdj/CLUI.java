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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.application.Platform;
import static rdj.GUIFX.getHexString;

/* commandline test routine

clear; echo -n -e \\x05 > 1; echo -n -e \\x03 > 2; java -jar FinalCrypt.jar
clear; echo -n -e \\x05 > 1; echo -n -e \\x03 > 2; java -cp FinalCrypt.jar rdj/CLUI --encrypt --print -k 2 -t 1
clear; echo -n ZYXVWUTSRQPONMLKJIHGFEDCBA098765 > a; echo -n abcdefghijklstuvwxyz > b; java -cp FinalCrypt.jar rdj/CLUI --print -k b -t a

*/

public class CLUI implements UI
{
    private FinalCrypt finalCrypt;
    private Version version;
    private UI ui;
    private final Configuration configuration;
    private boolean symlink = false;
    private boolean verbose = false;
    
    private boolean encrypt = false;
    private boolean decrypt = false;
    private boolean create = false;
    private boolean clone = false;
    private boolean key_checksum = false;
    private boolean printgpt = false;
    private boolean deletegpt = false;
    
    private FCPathList encryptableList;
    private FCPathList decryptableList;
    private FCPathList createKeyList;
    private FCPathList cloneKeyList;
    
    private boolean encryptablesFound = false;
    private boolean decryptablesFound = false;
    private boolean createKeyDeviceFound = false;
    private boolean cloneKeyDeviceFound = false;
    private FCPathList printGPTTargetList;
    private boolean printGPTDeviceFound;
    private boolean deleteGPTDeviceFound;
    private FCPathList deleteGPTTargetList;
    private  FCPathList targetFCPathList;
    private boolean keySourceChecksumReadEnded = false;

    public CLUI(String[] args)
    {
        this.ui = this;
        boolean tfset = false;
	boolean tfsetneeded = false;
	boolean cfset = false;
	boolean cfsetneeded = true;
        boolean validInvocation = true;
        boolean negatePattern = false;

        ArrayList<Path> targetPathList = new ArrayList<>();
        ArrayList<Path> extendedTargetPathList = new ArrayList<>();
        Path batchFilePath = null;
//        Path targetFilePath = null;
	
//        Path keyFilePath = null;
        FCPath keyFCPath = null;
	
        Path outputFilePath = null;
        configuration = new Configuration(this);
        version = new Version(this);
        version.checkCurrentlyInstalledVersion(this);

        String pattern = "glob:*";
        
        // Load the FinalCrypt Objext
        finalCrypt = new FinalCrypt(this);
        finalCrypt.start();
        finalCrypt.setBufferSize(finalCrypt.getBufferSizeDefault());
        
////      SwingWorker version of FinalCrype
//        finalCrypt.execute();

        // Validate Parameters
        for (int paramCnt=0; paramCnt < args.length; paramCnt++)
        {
//          Options
            if      (( args[paramCnt].equals("-h")) || ( args[paramCnt].equals("--help") ))                         { usage(false); }
	    else if (  args[paramCnt].equals("--examples"))							    { examples(); }
            else if (  args[paramCnt].equals("--encrypt"))							    { if ((!encrypt)&&(!decrypt)&&(!create)&&(!clone)&&(!printgpt)&&(!deletegpt)) { encrypt = true; cfsetneeded = true; tfsetneeded = true; } }
            else if (  args[paramCnt].equals("--decrypt"))							    { if ((!encrypt)&&(!decrypt)&&(!create)&&(!clone)&&(!printgpt)&&(!deletegpt)) { decrypt = true; cfsetneeded = true; tfsetneeded = true; } }
            else if (  args[paramCnt].equals("--create"))							    { if ((!encrypt)&&(!decrypt)&&(!create)&&(!clone)&&(!printgpt)&&(!deletegpt)) { create = true; cfsetneeded = true; tfsetneeded = true; } }
            else if (  args[paramCnt].equals("--clone"))							    { if ((!encrypt)&&(!decrypt)&&(!create)&&(!clone)&&(!printgpt)&&(!deletegpt)) { clone = true; cfsetneeded = true; tfsetneeded = true; } }
            else if (( args[paramCnt].equals("--print") ))							    { finalCrypt.setPrint(true); }
            else if (( args[paramCnt].equals("--key-chksum") ))							    { key_checksum = true; cfsetneeded = true; }
            else if (  args[paramCnt].equals("--print-gpt"))                                                        { if ((!encrypt)&&(!decrypt)&&(!create)&&(!clone)&&(!printgpt)&&(!deletegpt)) { printgpt = true; cfsetneeded = false; tfsetneeded = true; } }
            else if (  args[paramCnt].equals("--delete-gpt"))                                                       { if ((!encrypt)&&(!decrypt)&&(!create)&&(!clone)&&(!printgpt)&&(!deletegpt)) { deletegpt = true; cfsetneeded = false; tfsetneeded = true; } }
            else if (( args[paramCnt].equals("-v")) || ( args[paramCnt].equals("--verbose") ))                      { finalCrypt.setVerbose(true); verbose = true; }
            else if (( args[paramCnt].equals("-p")) || ( args[paramCnt].equals("--print") ))                        { finalCrypt.setPrint(true); }
            else if (( args[paramCnt].equals("-l")) || ( args[paramCnt].equals("--symlink") ))			    { finalCrypt.setSymlink(true); symlink = true; }
//            else if (  args[paramCnt].equals("--txt"))                                                              { finalCrypt.setTXT(true); }
//            else if (  args[paramCnt].equals("--bin"))                                                              { finalCrypt.setBin(true); }
//            else if (  args[paramCnt].equals("--dec"))                                                              { finalCrypt.setDec(true); }
//            else if (  args[paramCnt].equals("--hex"))                                                              { finalCrypt.setHex(true); }
//            else if (  args[paramCnt].equals("--chr"))                                                              { finalCrypt.setChr(true); }
            else if (  args[paramCnt].equals("--version"))                                                          { println(version.getProduct() + " " + version.getCurrentlyInstalledOverallVersionString()); System.exit(0); }
            else if (  args[paramCnt].equals("--update"))                                                           { version.checkLatestOnlineVersion(this); 	    String[] lines = version.getUpdateStatus().split("\r\n"); for (String line: lines) {log(line + "\r\n");} System.exit(0); }
            else if (( args[paramCnt].equals("-s")) && (!args[paramCnt+1].isEmpty()) )				    { if ( validateIntegerString(args[paramCnt + 1]) ) { finalCrypt.setBufferSize(Integer.valueOf( args[paramCnt + 1] ) * 1024 ); paramCnt++; } else { error("\r\nError: Invalid Option Value [-b size]" + "\r\n"); usage(true); }}

//          Filtering Options
            else if ( args[paramCnt].equals("--dry"))                                                               { finalCrypt.setDry(true); }
            else if ( ( args[paramCnt].equals("-w")) && (!args[paramCnt+1].isEmpty()) )				    { negatePattern = false; pattern = "glob:" + args[paramCnt+1]; paramCnt++; }
            else if ( ( args[paramCnt].equals("-W")) && (!args[paramCnt+1].isEmpty()) )				    { negatePattern = true; pattern = "glob:" + args[paramCnt+1]; paramCnt++; }
            else if ( ( args[paramCnt].equals("-r")) && (!args[paramCnt+1].isEmpty()) )				    { pattern = "regex:" + args[paramCnt+1]; paramCnt++; }

//          File Parameters
//            else if (  args[paramCnt].equals("--encrypt"))							    { encrypt = true; }
//            else if (  args[paramCnt].equals("--decrypt"))							    { decrypt = true;; }
//            else if (  args[paramCnt].equals("--create"))							    { create = true; }
//            else if (  args[paramCnt].equals("--clone"))							    { clone = true; }
            else if ( ( args[paramCnt].equals("-k")) && (paramCnt+1 < args.length) )				    { keyFCPath = Validate.getFCPath( ui, "", Paths.get(args[paramCnt+1]), true, Paths.get(args[paramCnt+1]), true); cfset = true; paramCnt++; }
            else if ( ( args[paramCnt].equals("-t")) && (!args[paramCnt+1].isEmpty()) )				    { targetPathList.add(Paths.get(args[paramCnt+1])); tfset = true; paramCnt++; }
            else if ( ( args[paramCnt].equals("-b")) && (!args[paramCnt+1].isEmpty()) )				    { tfset = addBatchTargetFiles(args[paramCnt+1], targetPathList); paramCnt++; }
	    
            else { System.err.println("\r\nError: Invalid Parameter: " + args[paramCnt]); usage(true); }
        }
        
        if ((cfsetneeded) && ( ! cfset ))									    { error("\r\nError: Missing valid parameter <-k \"keyfile\">" + "\r\n"); usage(true); }
        if ((tfsetneeded) && ( ! tfset ))									    { error("\r\nError: Missing valid parameter <-t \"file/dir\"> or <-b \"batchfile\">" + "\r\n"); usage(true); }

                
//////////////////////////////////////////////////// VALIDATE SELECTION /////////////////////////////////////////////////

	// Key Validation
	if ((cfsetneeded) && ( ! keyFCPath.isValidKey))
	{
	    String size = ""; if (keyFCPath.size < FCPath.CIPHER_SIZE_MIN) { size += " [size < " + FCPath.CIPHER_SIZE_MIN + "] "; } 
	    String dir = ""; if (keyFCPath.type == FCPath.DIRECTORY) { dir += " [is dir] "; } 
	    String sym = ""; if (keyFCPath.type == FCPath.SYMLINK) { sym += " [is symlink] "; }
	    String all = size + dir + sym;
	    
            error("\r\nKey parameter: -k \"" + keyFCPath.path + "\" Invalid:" + all + "\r\n\r\n");
	    log(Validate.getFCPathStatus(keyFCPath)); usage(true);
	}
	
	// Target Validation
	
	if (tfsetneeded)
	{
	    for(Path targetPath : targetPathList)
	    {
		if (Files.exists(targetPath))
		{
    //			      isValidDir(UI ui, Path targetDirPath, boolean symlink, boolean report)
		    if ( Validate.isValidDir( this,         targetPath,         symlink,        verbose))
		    {
			if (verbose) { status("Target parameter: " + targetPath + " is a valid dir\r\n", true); }
		    }
    //				   isValidFile(UI ui, String caller, Path targetSourcePath,  isKey, boolean device, long minSize, boolean symlink, boolean writable, boolean report)
		    else if ( Validate.isValidFile(this, "CLUI.CLUI() ",            targetPath,	false,          false,	         1L,         symlink,             true,        verbose))
		    {
			if (verbose) { status("Target parameter: " + targetPath + " is a valid file\r\n", true); }
		    }
		}
		else
		{ 
			error("Target parameter: -t \"" + targetPath + "\" does not exists\r\n"); usage(true);
		}            
	    }
	}

//	Command line input for an optional Password keyboard.nextInt();

//////////////////////////////////////////////////// CIPHER CHECKSUM =================================================

	if (key_checksum)
	{
	    System.out.println("\r\nKey CheckSum: (SHA-1): \"" + keyFCPath.path.toAbsolutePath().toString() + "\"...");
	    long    readKeySourceChannelPosition =  0; 
	    long    readKeySourceChannelTransfered =  0; 
	    int readKeySourceBufferSize = (1 * 1024 * 1024);
	    ByteBuffer keySourceBuffer = ByteBuffer.allocate(readKeySourceBufferSize); keySourceBuffer.clear();
	    MessageDigest messageDigest = null; try { messageDigest = MessageDigest.getInstance("SHA-1"); } catch (NoSuchAlgorithmException ex) {ui.error("Error: NoSuchAlgorithmException: MessageDigest.getInstance(\"SHA-256\")\r\n");}
	    int x = 0;
	    while ( ! keySourceChecksumReadEnded )
	    {
		try (final SeekableByteChannel readKeySourceChannel = Files.newByteChannel(keyFCPath.path, EnumSet.of(StandardOpenOption.READ,StandardOpenOption.SYNC)))
		{
		    readKeySourceChannel.position(readKeySourceChannelPosition);
		    readKeySourceChannelTransfered = readKeySourceChannel.read(keySourceBuffer); keySourceBuffer.flip(); readKeySourceChannelPosition += readKeySourceChannelTransfered;
		    readKeySourceChannel.close();

    //				    checksumLabel.setText("SHA256 calculating: " + checksumStatusTotalTransfered);
		    messageDigest.update(keySourceBuffer);
		    if ( readKeySourceChannelTransfered < 0 ) { keySourceChecksumReadEnded = true; }
		} catch (IOException ex)
		{
		    Platform.runLater(new Runnable(){ @Override public void run()
		    {
			keySourceChecksumReadEnded = true;
			error("readKeySourceChannel = Files.newByteChannel(..) " + ex.getMessage() + "\r\n"); 
		    }});
		}
		x++;
		keySourceBuffer.clear();
	    }
	    byte[] hashBytes = messageDigest.digest();
	    String hashString = getHexString(hashBytes,2);
	    System.out.println("Message Digest: " + hashString + "\r\n");
	}

//////////////////////////////////////////////////// BUILD SELECTION /////////////////////////////////////////////////
        
	targetFCPathList = new FCPathList();
//	if (!cfsetneeded) { keyFCPath = (FCPath) targetPathList.get(0); }
	if (!cfsetneeded) 
	{
//    					     getFCPath(UI ui, String caller,		 Path path, boolean isKey,   Path keyPath, boolean report)
		     keyFCPath = Validate.getFCPath(   ui,            "", targetPathList.get(0),            false, targetPathList.get(0),           true);
	}
//		 buildTargetSelection(UI ui, ArrayList<Path> userSelectedItemsPathList, Path keyPath, ArrayList<FCPath> targetFCPathList, boolean symlink, String pattern, boolean negatePattern, boolean status)
	Validate.buildSelection(       this,			        targetPathList,  keyFCPath,		    targetFCPathList,	      symlink,	      pattern,	       negatePattern,	       false);
	
/////////////////////////////////////////////// SET BUILD MODES ////////////////////////////////////////////////////

	if ((keyFCPath != null) && (keyFCPath.isValidKey))
	{
//	    log(targetFCPathList.getStats());
	    // Encryptables
	    if (targetFCPathList.encryptableFiles > 0)
	    {
		encryptableList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.isEncryptable); // log("Encryptable List:\r\n" + encryptableList.getStats());
		encryptablesFound = true;
	    }

	    // Encryptables
	    if (targetFCPathList.decryptableFiles > 0)
	    {
		decryptableList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.isDecryptable); // log("Decryptable List:\r\n" + decryptableList.getStats());
		decryptablesFound = true;
	    }

	    // Create Key Device
	    if (keyFCPath.type == FCPath.FILE)
	    {
		if (targetFCPathList.validDevices > 0)
		{
		    createKeyList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE); // log("Create Key List:\r\n" + createKeyList.getStats());
		    createKeyDeviceFound = true;
		} else { createKeyDeviceFound = false; }
	    }		
	    else if (keyFCPath.type == FCPath.DEVICE)
	    {
		// Clone Key Device
		if ((targetFCPathList.validDevices > 0) && (targetFCPathList.matchingKey == 0))
		{
		    final FCPath keyFCPath2 = keyFCPath; // for Lambda expression
		    cloneKeyList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE && fcPath.path.compareTo(keyFCPath2.path) != 0); // log("Clone Key List:\r\n" + cloneKeyList.getStats());
		    cloneKeyDeviceFound = true;
		} else { cloneKeyDeviceFound = false; }
	    } else { cloneKeyDeviceFound = false; }
	} else { createKeyDeviceFound = false; }

	if ((printgpt) && ((targetFCPathList.validDevices > 0) || (targetFCPathList.validDevicesProtected > 0)))
	{
	    printGPTTargetList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE || fcPath.type == FCPath.DEVICE_PROTECTED); // log("Create Key List:\r\n" + createKeyList.getStats());
	    printGPTDeviceFound = true;
	} else { printGPTDeviceFound = false; }
	
	if ((deletegpt) && (targetFCPathList.validDevices > 0))
	{
	    deleteGPTTargetList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE); // log("Create Key List:\r\n" + createKeyList.getStats());
	    if ( deleteGPTTargetList.size() > 0 ) { deleteGPTDeviceFound = true; }
	    else { deleteGPTDeviceFound = false; }
	}
	else if ((deletegpt) && (targetFCPathList.validDevicesProtected > 0))
	{
	    deleteGPTTargetList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE_PROTECTED); // log("Create Key List:\r\n" + createKeyList.getStats());
	    FCPath fcPath = (FCPath) deleteGPTTargetList.get(0); error("WARNING: Device: " + fcPath.path + " is protected!!!\r\n"); deleteGPTDeviceFound = false; 
	}
	else { deleteGPTDeviceFound = false; }

	
/////////////////////////////////////////////// FINAL VALIDATION & EXECUTE MODES ////////////////////////////////////////////////////


	DeviceManager deviceManager;
	if ((encrypt))
	{
	    if ((encryptablesFound))	{ processStarted(); finalCrypt.encryptSelection(targetFCPathList, encryptableList, keyFCPath, true); }
	    else			{ error("No encryptable targets found:\r\n"); log(targetFCPathList.getStats()); }
	}
	else if ((decrypt))
	{
	    if (decryptablesFound)	{ processStarted(); finalCrypt.encryptSelection(targetFCPathList, decryptableList, keyFCPath, false); }
	    else			
	    {
		error("No decryptable targets found\r\n\r\n");
		if ( targetFCPathList.encryptedFiles > 0 ) { error("Wrong key? \"" + keyFCPath.path.toString() + "\"\r\n\r\n"); }
		log(targetFCPathList.getStats());
	    }
	}
	else if (create)
	{
	    if (createKeyDeviceFound){ processStarted(); deviceManager = new DeviceManager(ui); deviceManager.start(); deviceManager.createKeyDevice(keyFCPath, (FCPath) createKeyList.get(0)); processFinished(); }
	    else			{ error("No valid target device found:\r\n"); log(targetFCPathList.getStats()); }
	}
	else if ((clone) && (cloneKeyDeviceFound))
	{
	    if (cloneKeyDeviceFound) { processStarted(); deviceManager = new DeviceManager(ui); deviceManager.start(); deviceManager.cloneKeyDevice(keyFCPath, (FCPath) cloneKeyList.get(0));  processFinished(); }
	    else			{ error("No valid target device found:\r\n"); log(targetFCPathList.getStats()); }
	}
	else if ((printgpt) && (printGPTDeviceFound))
	{
	    if (printGPTDeviceFound) { deviceManager = new DeviceManager(ui); deviceManager.start(); deviceManager.printGPT( (FCPath) printGPTTargetList.get(0)); }
	    else			{ error("No valid target device found:\r\n"); log(targetFCPathList.getStats()); }
	}
	else if ((deletegpt) && (deleteGPTDeviceFound))
	{
	    if (deleteGPTDeviceFound) { deviceManager = new DeviceManager(ui); deviceManager.start(); deviceManager.deleteGPT( (FCPath) deleteGPTTargetList.get(0)); }
	    else			{ error("No valid target device found:\r\n"); log(targetFCPathList.getStats()); }
	}
    } // End of default constructor
    
    
    
//  =======================================================================================================================================================================


    private boolean addBatchTargetFiles(String batchFilePathString, ArrayList<Path> targetFilesPathList)
    {
        boolean ifset = false;
        Path batchFilePath;
        Path targetFilePath;
//		      isValidFile(UI ui, String caller,                       Path targetSourcePath, isKey	boolean device, long minSize, boolean symlink, boolean writable, boolean report)
        if ( Validate.isValidFile(this,  "CLUI.addBatchTargetFiles", Paths.get(batchFilePathString), false,              false,	          1L,         symlink,             true,           true) )
        {
            log("Adding items from batchfile: " + batchFilePathString + "\r\n");
            batchFilePath = Paths.get(batchFilePathString);
            try
            {
                for (String targetFilePathString:Files.readAllLines(batchFilePath))
                {
//                  Entry may not be a directory (gets filtered and must be a valid file)
//				  isValidFile(UI ui, String caller,                        Path targetSourcePath, boolean isKey, boolean device, long minSize, boolean symlink, boolean writable, boolean report)
                    if ( Validate.isValidFile(   ui, "CLUI.addBatchTargetFiles", Paths.get(targetFilePathString),          false,	     false,	      0L,         symlink,             true,           true) )
                    {
                        targetFilePath = Paths.get(targetFilePathString); targetFilesPathList.add(targetFilePath); ifset = true;
//                        println("Adding: " + targetFilePathString);
                    }
                    else { /* println("Invalid file: " + targetFilePathString);*/ } // Reporting in isValidFile is already set to true, so if invalid then user is informed
                }
            }
            catch (IOException ex) { error("Files.readAllLines(" + batchFilePath + ");" + ex.getMessage()); }
            if ( ! ifset ) { log("Warning: batchfile: " + batchFilePathString + " doesn't contain any valid items!\r\n"); }
        }
        else
        {
            error("Error: batchfile: " + batchFilePathString + " is not a valid file!\r\n");
        }
        return ifset;
    }

    public static FCPathList filter(ArrayList<FCPath> fcPathList, Predicate<FCPath> fcPath)
    {
	FCPathList result = new FCPathList();
	for (FCPath fcPathItem : fcPathList) { if (fcPath.test(fcPathItem)) { result.add(fcPathItem); } }
	return result;
    }
    
    public static Predicate<FCPath> isHidden() { return (FCPath fcPath) -> fcPath.isHidden; }
    
    public List<FCPath> filter(Predicate<FCPath> criteria, ArrayList<FCPath> list)
    {
	return list.stream().filter(criteria).collect(Collectors.<FCPath>toList());
    }
    
    public static void main(String[] args)
    {
        new CLUI(args);
    }
    
    private boolean validateIntegerString(String text) { try { Integer.parseInt(text); return true;} catch (NumberFormatException e) { return false; } }

    private void usage(boolean error)
    {
        String fileSeparator = java.nio.file.FileSystems.getDefault().getSeparator();
        log("\r\n");
        log("Examples:\r\n");
        log("\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --examples   Print commandline examples.\r\n");
        log("\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --encrypt -k key_file -t target_file\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --encrypt -k key_file -t target_dir\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --encrypt -k key_file -t target_file -t target_dir\r\n");
        log("\r\n");
        log("Usage:	    java -cp FinalCrypt.jar rdj/CLUI   <Mode>  [options] <Parameters>\r\n");
        log("\r\n");
        log("Mode:\r\n");
        log("            <--encrypt>           -k \"key_file\"   -t \"target\"	    Encrypt Targets.\r\n");
        log("            <--decrypt>           -k \"key_file\"   -t \"target\"	    Decrypt Targets.\r\n");
        log("            <--create>            -k \"key_file\"   -t \"target\"	    Create Key Device (only unix).\r\n");
        log("            <--clone>             -k \"source_device\" -t \"target_device\"     Clone Key Device (only unix).\r\n");
        log("            [--print-gpt]         -t \"target_device\"			    Print GUID Partition Table.\r\n");
        log("            [--delete-gpt]        -t \"target_device\"			    Delete GUID Partition Table (DATA LOSS!).\r\n");
        log("\r\n");
	log("Options:\r\n");
        log("            [-h] [--help]	  Shows this help page.\r\n");
        log("            [--key-chksum]        -k \"key_file\"			    Calculate key checksum.\r\n");
        log("            [-d] [--debug]        Enables debugging mode.\r\n");
        log("            [-v] [--verbose]      Enables verbose mode.\r\n");
        log("            [--print]		  Print all encrypted bytes.\r\n");
        log("            [-l] [--symlink]      Include symlinks (can cause double encryption! Not recommended!).\r\n");
        log("                 [--version]      Print " + version.getProduct() + " version.\r\n");
        log("                 [--update]       Check for online updates.\r\n");
//        log("            [--txt]               Print text calculations.\r\n");
//        log("            [--bin]               Print binary calculations.\r\n");
//        log("            [--dec]               Print decimal calculations.\r\n");
//        log("            [--hex]               Print hexadecimal calculations.\r\n");
//        log("            [--chr]               Print character calculations.\r\n");
        log("                                  Warning: The above Print options slows encryption severely.\r\n");
        log("            [-s size]             Changes default I/O buffer size (size = KiB) (default 1024 KiB).\r\n");
        log("\r\n");
        log("Filtering Options:\r\n");
        log("\r\n");
        log("            [--dry]               Dry run without encrypting files for safe testing purposes.\r\n");
        log("            [-w \'wildcard\']       File wildcard INCLUDE filter. Uses: \"Globbing Patterns Syntax\".\r\n");
        log("            [-W \'wildcard\']       File wildcard EXCLUDE filter. Uses: \"Globbing Patterns Syntax\".\r\n");
        log("            [-r \'regex\']          File regular expression filter. Advanced filename filter!\r\n");
        log("\r\n");
        log("Parameters:\r\n");
        log("\r\n");
        log("            <-k \"keyfile\">     The file that encrypts your file(s). Keep keyfile SECRET!\r\n");
        log("                                  A key-file is a unique file like a personal photo or video!\r\n");
        log("\r\n");
        log("            <-t / -b>             The target items you want to encrypt. Individual (-t) or by batch (-b).\r\n");
        log("            <[-t \"file/dir\"]>     Target file or dir you want to encrypt (encrypts dirs recursively).\r\n");
        log("            <[-b \"batchfile\"]>    Batchfile with targetfiles you want to encrypt (only files accepted).\r\n");
        log("\r\n");
        log(Version.getProduct() + " " + version.checkCurrentlyInstalledVersion(this) + " - Author: " + Version.getAuthor() + " - Copyright: " + Version.getCopyright() + "\r\n\r\n");
        System.exit(error ? 1 : 0);
    }

    private void examples()
    {
        log("\r\n");
        log("Examples:   java -cp FinalCrypt.jar rdj/CLUI <Mode> [options] <Parameters>\r\n");
        log("\r\n");
        log("            # Encrypt / Decrypt myfile with mykeyfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --encrypt -k mykeyfile -t myfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --decrypt -k mykeyfile -t myfile\r\n");
        log("\r\n");
        log("            # Encrypt / Decrypt myfile and all content in mydir with mykeyfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --encrypt -k mykeyfile -t myfile -t mydir\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --decrypt -k mykeyfile -t myfile -t mydir\r\n");
        log("\r\n");
        log("            # Encrypt / Decrypt files in batchfile with mykeyfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --encrypt -k mykeyfile -b mybatchfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --decrypt -k mykeyfile -b mybatchfile\r\n");
        log("\r\n");
        log("            # Encrypt / Decrypt all files with *.bit extension in mydir with mykeyfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --encrypt -w '*.bit'-k mykeyfile -t mydir\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --decrypt -w '*.bit'-k mykeyfile -t mydir\r\n");
        log("\r\n");
        log("            # Encrypt / Decrypt all files without *.bit extension in mydir with mykeyfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --encrypt -W '*.bit' -k mykeyfile -t mydir \r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --decrypt -W '*.bit' -k mykeyfile -t mydir \r\n");
        log("\r\n");
        log("            # Encrypt / Decrypt all files with *.bit extension in mydir with mykeyfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --encrypt -r '^.*\\.bit$' -k mykeyfile -t mydir\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --decrypt -r '^.*\\.bit$' -k mykeyfile -t mydir\r\n");
        log("\r\n");
        log("            # Encrypt / Decrypt all files excluding .bit extension in mydir with mykeyfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --encrypt -r '(?!.*\\.bit$)^.*$' -k mykeyfile -t mydir\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --decrypt -r '(?!.*\\.bit$)^.*$' -k mykeyfile -t mydir\r\n");
        log("\r\n");
        log("Key Device Examples (Linux):\r\n");
        log("\r\n");
        log("            # Create Key Device with 2 key partitions (e.g. on USB Mem Stick)\r\n");
        log("            # Beware: keyfile gets randomized before writing to Device\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --create -k mykeyfile -t /dev/sdb\r\n");
        log("\r\n");
        log("            # Print GUID Partition Table\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --print-gpt -t /dev/sdb\r\n");
        log("\r\n");
        log("            # Delete GUID Partition Table\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --delete-gpt -t /dev/sdb\r\n");
        log("\r\n");
        log("            # Clone Key Device (-k sourcekeydevice -t destinationkeydevice)\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --clone -k /dev/sdb -t /dev/sdc\r\n");
        log("\r\n");
        log("            # Encrypt / Decrypt myfile with raw key partition\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --encrypt -k /dev/sdb1 -t myfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --decrypt -k /dev/sdb1 -t myfile\r\n");
        log("\r\n");
        log(Version.getProduct() + " " + version.checkCurrentlyInstalledVersion(this) + " - Author: " + Version.getAuthor() + " - Copyright: " + Version.getCopyright() + "\r\n\r\n");
        System.exit(0);
    }

    @Override synchronized public void log(String message)
    {
        System.out.print(message);
        try { Files.write(configuration.getLogFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC); } catch (IOException ex) { println("Files.write(" + configuration.getLogFilePath() + ")..));"); }
    }

    @Override synchronized public void error(String message)
    {
        status(message, true);
	try { Files.write(configuration.getErrorFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC); } catch (IOException ex) { println("Files.write(" + configuration.getErrorFilePath() + ")..));"); }
    }

    @Override public void status(String status, boolean log) { if (log) { log(status); } }
    @Override public void statusNow(String status, boolean log) { if (log) { log(status); } }

    @Override public void println(String message) { System.out.println(message); }

    @Override public void processGraph(int value) {  }

    @Override
    public void processStarted() 
    {
    }

    @Override public void processProgress(int filesProgress, int fileProgress, long bytesTotalParam, long bytesProcessedParam, long bytesPerMiliSecondParam)
    {
//        log("filesProgress: " + filesProgress + " fileProgress: " + fileProgress);
    }
    
    @Override public void processFinished()
    {
    }

    @Override
    public void fileProgress()
    {
    }

//    @Override public void buildProgress(FCPathList targetFCPathList) {  }

    @Override
    public void buildReady(FCPathList fcPathListParam)
    {
	targetFCPathList = fcPathListParam;
    }
}
