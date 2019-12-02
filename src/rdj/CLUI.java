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

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static rdj.GUIFX.getHexString;

/* commandline test routine

echo -n -e \\x05 > myfile # 00000101
echo -n -e \\x03 > mykey  # 00000011
java -cp finalcrypt.jar rdj/CLUI --encrypt --print --no-key-size --disable-MAC -k mykey -t myfile

*/

public class CLUI implements UI
{
    protected FinalCrypt finalCrypt;
    private Version version;
    private UI ui;
    private final Configuration configuration;
    private boolean symlink = false;
    private boolean verbose = false;
    private boolean scan = false;
    private boolean dictionary = false;
    private String dictionaryFilePathString = "";

    private boolean encrypt = false;
    private boolean decrypt = false;
    private boolean createManualKeyDev = false;
    private boolean createManualKeyFile = false;
    private boolean clonekeydev = false;
    private boolean key_checksum = false;
    private boolean printgpt = false;
    private boolean deletegpt = false;
    
    // Filtered Lists
    // Filtered Lists
    protected FCPathList<FCPath> decryptedList; 
    protected FCPathList<FCPath> encryptableList;
    protected FCPathList<FCPath> readAutoKeyList;
    protected FCPathList<FCPath> writeAutoKeyList;

    protected FCPathList<FCPath> encryptedList; 
    protected FCPathList<FCPath> decryptableList;
    
    protected FCPathList<FCPath> emptyList; 
    protected FCPathList<FCPath> symlinkList;
    protected FCPathList<FCPath> unreadableList;
    protected FCPathList<FCPath> unwritableList;
    protected FCPathList<FCPath> hiddenList;

    protected FCPathList<FCPath> newEncryptedList;
    protected FCPathList<FCPath> unencryptableList;
    protected FCPathList<FCPath> newDecryptedList;
    protected FCPathList<FCPath> undecryptableList;
    protected FCPathList<FCPath> invalidFilesList;

    protected FCPathList<FCPath> createManualKeyList;
    protected FCPathList<FCPath> cloneManualKeyList;
    

    private boolean encryptablesFound = false;
    private boolean decryptablesFound = false;
    private boolean createManualKeyDeviceFound = false;
    private boolean cloneManualKeyDeviceFound = false;
    private FCPathList<FCPath> printGPTTargetList;
    private boolean printGPTDeviceFound;
    private boolean deleteGPTDeviceFound;
    private FCPathList<FCPath> deleteGPTTargetList;
    protected  FCPathList<FCPath> targetFCPathList;
    private boolean keySourceChecksumReadEnded = false;
    private int bufferSize;
    private Long totalTranfered;
    private Long filesizeInBytes = 100L * (1024L * 1024L);  // Create OTP Key File Size
    private Path keyPath;
//    private boolean disabledMAC = false;
    private boolean encryptModeNeeded;
    protected UsageReaderTimeoutThread usageReaderTimeoutThread;
    protected TestListReaderTimeoutThread testListReaderTimeoutThread;
    protected boolean testListAborted = true;
    private UsageReaderThread usageReaderThread;
    private TestListReaderThread testListReaderThread;
    
    private String pwd =		"";
    private byte[] pwdBytes;
    private boolean pwdPromptNeeded =	false;
    private boolean pwdIsSet =		false;
    
    protected boolean test =		false;
    protected String testAnswer =		"";
    protected FCPath keyFCPath;
    protected FCPath bruteForceFCPathTargetPrint;
    private long bfcounter;
    private long bflines;
    private Stats bruteForceDataStats;    
    
    public CLUI(String[] args)
    {	
        this.ui = this;
        boolean tfset = false;
	boolean tfsetneeded = false;
	boolean kfset = false;
	boolean kfsetneeded = true;
        boolean validInvocation = true;
        boolean negatePattern = false;

        ArrayList<Path> targetPathList = new ArrayList<>();
        ArrayList<Path> extendedTargetPathList = new ArrayList<>();
        Path batchFilePath = null;
        Path dictionaryFilePath = null;
	String dictionaryFilePathString = "";
	keyFCPath = null;
	
        Path outputFilePath = null;
        configuration = new Configuration(this);
        version = new Version(this);
        version.checkCurrentlyInstalledVersion(this);

        String pattern = "glob:*";
        
        // Load the FinalCrypt Objext
        finalCrypt = new FinalCrypt(this);
        finalCrypt.start();
        finalCrypt.setBufferSize(finalCrypt.getBufferSizeDefault());
	pwdBytes = new byte[0];
        
////      SwingWorker version of FinalCrype
//        finalCrypt.execute();

        // Validate Parameters
	
	log(Version.getLogHeader(this.getClass().getSimpleName(), version, configuration), false, false, true, false ,false);
	
	if (args.length == 0 ) { log("\r\nWarning: No parameters entered!\r\n", false, true, true, false, false); usagePrompt(true); } 
	
        for (int paramCnt=0; paramCnt < args.length; paramCnt++)
        {
//          Options
            if      (( args[paramCnt].equals("-h")) || ( args[paramCnt].equals("--help") ))                         { usage(false); }
	    else if (  args[paramCnt].equals("--examples"))							    { examples(); }
            else if (  args[paramCnt].equals("--disable-MAC"))							    { finalCrypt.disabledMAC = true; FCPath.KEY_SIZE_MIN = 1; encryptModeNeeded = true; }
            else if (  args[paramCnt].equals("--scan"))							            { scan=true; }
            else if (  args[paramCnt].equals("--encrypt"))							    { if ((!encrypt)&&(!decrypt)&&(!createManualKeyDev)&&(!clonekeydev)&&(!printgpt)&&(!deletegpt)) { encrypt = true; kfsetneeded = true; tfsetneeded = true; } }
            else if (  args[paramCnt].equals("--decrypt"))							    { if ((!encrypt)&&(!decrypt)&&(!createManualKeyDev)&&(!clonekeydev)&&(!printgpt)&&(!deletegpt)) { decrypt = true; kfsetneeded = true; tfsetneeded = true; } }
            else if (( args[paramCnt].equals("-p")) || ( args[paramCnt].equals("--password") ))                     {
															MessageDigest messageDigest = null; try { messageDigest = MessageDigest.getInstance(FinalCrypt.HASH_ALGORITHM_NAME); } catch (NoSuchAlgorithmException ex) { log("Error: NoSuchAlgorithmException: MessageDigest.getInstance(\" "+ FinalCrypt.HASH_ALGORITHM_NAME + "\")\r\n", true, true, true, true, false);}
															messageDigest.update(pwd.getBytes());
															byte[] hashBytes = messageDigest.digest();
															pwdBytes = GPT.hex2Bytes(getHexString(hashBytes,2));
															pwd = args[paramCnt+1]; pwdIsSet = true; paramCnt++;
														    }
            else if (( args[paramCnt].equals("-pd")) || ( args[paramCnt].equals("--password-dictionary") ))         { dictionaryFilePathString = args[paramCnt+1]; dictionary = true; paramCnt++;; }
            else if (( args[paramCnt].equals("-pp")) || ( args[paramCnt].equals("--password-prompt") ))             { pwdPromptNeeded = true; }

	    else if (  args[paramCnt].equals("--create-keydev"))						    { if ((!encrypt)&&(!decrypt)&&(!createManualKeyDev)&&(!clonekeydev)&&(!printgpt)&&(!deletegpt)) { createManualKeyDev = true; kfsetneeded = true; tfsetneeded = true; } }
            else if (  args[paramCnt].equals("--create-keyfile"))						    { if ((!encrypt)&&(!decrypt)&&(!createManualKeyDev)&&(!clonekeydev)&&(!printgpt)&&(!deletegpt)) { createManualKeyFile = true; kfsetneeded = false; tfsetneeded = false; } }
            else if (  args[paramCnt].equals("--clone-keydev"))							    { if ((!encrypt)&&(!decrypt)&&(!createManualKeyDev)&&(!clonekeydev)&&(!printgpt)&&(!deletegpt)) { clonekeydev = true; kfsetneeded = true; tfsetneeded = true; } }
            else if (( args[paramCnt].equals("--key-chksum") ))							    { key_checksum = true; kfsetneeded = true; }
            else if (( args[paramCnt].equals("--no-key-size") ))						    { FCPath.KEY_SIZE_MIN = 1; }
            else if (  args[paramCnt].equals("--print-gpt"))                                                        { if ((!encrypt)&&(!decrypt)&&(!createManualKeyDev)&&(!clonekeydev)&&(!printgpt)&&(!deletegpt)) { printgpt = true; kfsetneeded = false; tfsetneeded = true; } }
            else if (  args[paramCnt].equals("--delete-gpt"))                                                       { if ((!encrypt)&&(!decrypt)&&(!createManualKeyDev)&&(!clonekeydev)&&(!printgpt)&&(!deletegpt)) { deletegpt = true; kfsetneeded = false; tfsetneeded = true; } }
            else if (( args[paramCnt].equals("--print") ))							    { finalCrypt.setPrint(true); }
            else if (( args[paramCnt].equals("-v")) || ( args[paramCnt].equals("--verbose") ))                      { finalCrypt.setVerbose(true); verbose = true; }
            else if (( args[paramCnt].equals("-l")) || ( args[paramCnt].equals("--symlink") ))			    { finalCrypt.setSymlink(true); symlink = true; }
//            else if (  args[paramCnt].equals("--txt"))                                                              { finalCrypt.setTXT(true); }
//            else if (  args[paramCnt].equals("--bin"))                                                              { finalCrypt.setBin(true); }
//            else if (  args[paramCnt].equals("--dec"))                                                              { finalCrypt.setDec(true); }
//            else if (  args[paramCnt].equals("--hex"))                                                              { finalCrypt.setHex(true); }
//            else if (  args[paramCnt].equals("--chr"))                                                              { finalCrypt.setChr(true); }
            else if (  args[paramCnt].equals("--version"))                                                          { log(version.getProductName() + " " + version.getCurrentlyInstalledOverallVersionString() + "\r\n", false, true, true, false, false); System.exit(0); }
            else if (  args[paramCnt].equals("--license"))                                                          { log(version.getProductName() + " " + Version.getLicense() + "\r\n", false, true, true, false, false); System.exit(0); }
            else if (  args[paramCnt].equals("--check-update"))                                                           { version.checkLatestOnlineVersion(this); 	    String[] lines = version.getUpdateStatus().split("\r\n"); for (String line: lines) { log(line + "\r\n", false, true, true, false, false); } System.exit(0); }
            else if (( args[paramCnt].equals("-s")) && (!args[paramCnt+1].isEmpty()) )				    { if ( validateIntegerString(args[paramCnt + 1]) ) { finalCrypt.setBufferSize(Integer.valueOf( args[paramCnt + 1] ) * 1024 ); paramCnt++; } else { log("\r\nWarning: Invalid Option Value [-b size]" + "\r\n", false, true, true, false, false); usagePrompt(true); }}
            else if (( args[paramCnt].equals("-S")) && (!args[paramCnt+1].isEmpty()) )				    { if ( validateIntegerString(args[paramCnt + 1]) ) { filesizeInBytes = Long.valueOf( args[paramCnt + 1] ); paramCnt++; } else { log("\r\nWarning: Invalid Option Value [-S size]" + "\r\n", false, true, true, false, false); usagePrompt(true); }}

//	    Mode parameters
	    else if (
			    (!scan)
			&&  (!encrypt)
			&&  (!decrypt)
			&&  (!createManualKeyDev)
			&&  (!clonekeydev)
			&&  (!printgpt)
			&&  (!deletegpt)
			&&  (!createManualKeyFile)
		    )												    { log("\r\nWarning: No <--Mode> parameter specified" + "\r\n",			    false, true, true, false, false); usagePrompt(true); }

//          Filtering Options if ( validateIntegerString(args[paramCnt + 1]) ) {  }
            else if ( ( args[paramCnt].equals("--test")) && (args[paramCnt+1].isEmpty()) )                          { test=true; finalCrypt.setTest(test);  }
            else if ( ( args[paramCnt].equals("--test")) && (!args[paramCnt+1].isEmpty()) )			    { test=true; finalCrypt.setTest(test); if ((args[paramCnt + 1].toLowerCase().equals("c")) || ( validateIntegerString(args[paramCnt + 1]) )) {testAnswer = args[paramCnt + 1]; paramCnt++;} }
            else if ( ( args[paramCnt].equals("-w")) && (!args[paramCnt+1].isEmpty()) )				    { negatePattern = false; pattern = "glob:" + args[paramCnt+1]; paramCnt++; }
            else if ( ( args[paramCnt].equals("-W")) && (!args[paramCnt+1].isEmpty()) )				    { negatePattern = true; pattern = "glob:" + args[paramCnt+1]; paramCnt++; }
            else if ( ( args[paramCnt].equals("-r")) && (!args[paramCnt+1].isEmpty()) )				    { pattern = "regex:" + args[paramCnt+1]; paramCnt++; }

//          File Parameters
            else if ( ( args[paramCnt].equals("-k")) )								    { if (paramCnt+1 < args.length) { keyFCPath = Validate.getFCPath(ui, "", Paths.get(args[paramCnt+1]), true, Paths.get(args[paramCnt+1]), finalCrypt.disabledMAC, true); kfset = true; paramCnt++; } else { log("\r\nWarning: Missing key parameter <-k \"keyfile\">" + "\r\n", false, true, true, false, false); usagePrompt(true); } }
            else if ( ( args[paramCnt].equals("-K")) && (!args[paramCnt+1].isEmpty()) )				    { keyPath = Paths.get(args[paramCnt+1]); paramCnt++; } // Create OTP Key File
            else if ( ( args[paramCnt].equals("-t")) )								    { if (paramCnt+1 < args.length) { targetPathList.add(Paths.get(args[paramCnt+1])); tfset = true; paramCnt++; } else { log("\r\nWarning: Missing target parameter <[-t \"file/dir\"]>" + "\r\n", false, true, true, false, false); usagePrompt(true); } }
            else if ( ( args[paramCnt].equals("-b")) && (!args[paramCnt+1].isEmpty()) )				    { tfset = addBatchTargetFiles(args[paramCnt+1], targetPathList); paramCnt++; }
            else { log("\r\nWarning: Invalid Parameter: " + args[paramCnt] + "\r\n", false, true, true, true, false); usagePrompt(true); }
        }

        if (( encryptModeNeeded )   && ( decrypt ))								    { log("\r\nWarning: MAC Mode Disabled! Use --encrypt if you know what you are doing!!!\r\n",  false, true, true, false, false); usagePrompt(true); }
        if (( encryptModeNeeded )   && ( ! encrypt ))								    { log("\r\nWarning: Missing valid parameter <--encrypt>" + "\r\n",			    false, true, true, false, false); usagePrompt(true); }
        if (( kfsetneeded )	    && ( ! kfset ))								    { log("\r\nWarning: Missing valid parameter <-k \"keyfile\">" + "\r\n",			    false, true, true, false, false); usagePrompt(true); }
        if (( tfsetneeded )	    && ( ! tfset ))								    { log("\r\nWarning: Missing valid parameter <-t \"file/dir\"> or <-b \"batchfile\">" + "\r\n",false, true, true, false, false); usagePrompt(true); }
                
//////////////////////////////////////////////////// VALIDATE SELECTION /////////////////////////////////////////////////

	// Key Validation
	if ( (kfsetneeded) )
	{
	    if (( ! keyFCPath.isValidKey ) && ( ! keyFCPath.isValidKeyDir))
	    {
		String exist ="";
		String size ="";
		String dir ="";
		String sym ="";
		String all = "";

		if (keyFCPath.exist == false)
		{
		    exist += " [key does not exist] "; 		
		}
		else
		{
//		    if (keyFCPath.type == FCPath.DIRECTORY) { dir += " [is dir] "; } 
		    if (keyFCPath.type == FCPath.SYMLINK) { sym += " [is symlink] "; } // finalCrypt.disabledMAC = true

		    if (! finalCrypt.disabledMAC)
		    {
			if (( keyFCPath.size < FCPath.KEY_SIZE_MIN ))	{ size += " [size < " + FCPath.KEY_SIZE_MIN + "] try: \"--no-key-size\" option "; }
			if (( keyFCPath.size < FCPath.MAC_SIZE ) )	{ size += " [size < " + FCPath.MAC_SIZE + "] try: \"--disable-MAC\" option if you know what you are doing !!! "; }
		    }
		    else { if (( keyFCPath.size < FCPath.KEY_SIZE_MIN )) { size += " [size < " + FCPath.KEY_SIZE_MIN + "] try: \"--no-key-size\" option "; } }
		}

		all = exist + /*dir +*/ sym + size ;

//		log("\r\nWarning: Key parameter: -k \"" + keyFCPath.path.toAbsolutePath().toString() + "\" Invalid:" + all + "\r\n\r\n", false, true, true, false, false);
		if ( ! keyFCPath.errorDescription.isEmpty() )  { log("\r\n" + keyFCPath.errorDescription +"\r\n", false, true, true, false, false); }
		log(Validate.getFCPathStatus(keyFCPath), false, true, false, false, false); usagePrompt(true);
	    }
	    else
	    {
//		test("Key: " + keyFCPath.getString() + "\r\n");
	    }
	}
	else
	{

	}
			
	// Target Validation
	
	if (tfsetneeded)
	{
	    for(Path targetPath : targetPathList)
	    {		
		if (Files.exists(targetPath))
		{
//    				  isValidDir(UI ui, Path targetDirPath, boolean symlink, boolean report)
		    if ( Validate.isValidDir( this,         targetPath,         symlink,        verbose))
		    {
			if (verbose) { log("Info: Target parameter: " + targetPath + " is a valid dir\r\n", false, true, true, false, false); }
		    }
//				       isValidFile(UI ui, String caller, Path targetSourcePath,  isKey, boolean device, long minSize, boolean symlink, boolean writable, boolean report)
		    else if ( Validate.isValidFile( this,"CLUI.CLUI() ",            targetPath,	 false,          false,	          1L,         symlink,             true,        verbose))
		    {
			if (verbose) { log("Info: Target parameter: " + targetPath + " is a valid file\r\n", false, true, true, false, false); }
		    }
		}
		else
		{ 
			log("Warning: Target parameter: -t \"" + targetPath + "\" does not exists\r\n", false, true, true, false, false); usagePrompt(true);
		}            
	    }
	}

//	Command line input for an optional Password

	if ( pwdPromptNeeded )
	{
	    ConsoleEraser consoleEraser = new ConsoleEraser();
	    System.out.print("Password: ");
	    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	    consoleEraser.start();
	    try { pwd = in.readLine(); pwdIsSet = true; }
	    catch (IOException err) { log("Error: Can't read password! " + err.getMessage() + "\r\n", false, true, true, true, false); usagePrompt(true); System.exit(1); }
 
	    consoleEraser.halt();
	}

	if ( pwdIsSet )
	{
	    MessageDigest messageDigest = null; try { messageDigest = MessageDigest.getInstance(FinalCrypt.HASH_ALGORITHM_NAME); } catch (NoSuchAlgorithmException ex) { log("Error: NoSuchAlgorithmException: MessageDigest.getInstance(\" "+ FinalCrypt.HASH_ALGORITHM_NAME + "\")\r\n", true, true, true, true, false);}
	    messageDigest.update(pwd.getBytes());
	    byte[] hashBytes = messageDigest.digest();
	    pwdBytes = GPT.hex2Bytes(getHexString(hashBytes,2));

	    finalCrypt.setPwd(pwd);
	    finalCrypt.setPwdBytes(pwd);
	}

//	====================================================================================================================
//	 Start writing Manual OTP key file
//	====================================================================================================================

	if (createManualKeyFile)
	{
	    Long factor = 0L;
	    bufferSize = 1048576;
	    totalTranfered = 0L;
	    
	    if ( Files.exists(keyPath, LinkOption.NOFOLLOW_LINKS) ) { log("Warning: file: \"" + keyPath.toAbsolutePath().toString() + "\" exists! Aborted!\r\n\r\n", false, true, false, false, false); try{ Thread.sleep(3000); } catch (InterruptedException ex) {} System.exit(1); }
	    else						    { log("Creating OTP Key File" + " (" + Validate.getHumanSize(filesizeInBytes, 1,"Bytes") + ")...", false, true, false, false, false); }

	    if ( filesizeInBytes < bufferSize) { bufferSize = filesizeInBytes.intValue(); }

	    boolean inputEnded = false;
	    long writeKeyFileChannelPosition = 0L;
	    long writeKeyFileChannelTransfered = 0L;
	    totalTranfered = 0L;
	    Long remainder = 0L;

//	    Write the keyfile to 1st partition

	    ByteBuffer  randomBuffer =	    ByteBuffer.allocate(bufferSize); randomBuffer.clear();

	    write1loop: while ( (totalTranfered < filesizeInBytes) && (! inputEnded ))
	    {
		remainder = (filesizeInBytes - totalTranfered);

		if ( remainder >= bufferSize )				    { randomBuffer = ByteBuffer.allocate(bufferSize); randomBuffer.clear(); }
		else if (( remainder > 0 ) && ( remainder < bufferSize ))   { randomBuffer = ByteBuffer.allocate(remainder.intValue()); randomBuffer.clear(); }
		else							    { inputEnded = true; }
		
//              Randomize raw key or write raw key straight to partition
		//		    getFCRandomBuffer(UI ui,		    int size, boolean extraSeed, boolean encrypt,	   boolean print)
		randomBuffer = TRNG.getFCRandomBuffer(   ui, randomBuffer.capacity(),		   true,	    true, finalCrypt.getPrint());

//              Write Device
		try (final SeekableByteChannel writeKeyFileChannel = Files.newByteChannel(keyPath, finalCrypt.getEnumSet(EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE))))
		{
		    writeKeyFileChannel.position(writeKeyFileChannelPosition);
		    writeKeyFileChannelTransfered = writeKeyFileChannel.write(randomBuffer); randomBuffer.rewind();
		    totalTranfered += writeKeyFileChannelTransfered; 
//		    log("tot: " + filesizeInBytes + " trans: " + totalTranfered + " remain: " + remainder + " p: " + (double)totalTranfered / filesizeInBytes + "\r\n", false, true, false, false, false);
		    
		    writeKeyFileChannelPosition += writeKeyFileChannelTransfered;

		    writeKeyFileChannel.close();
		} catch (IOException ex) { log("\r\nError: " + ex.getMessage() + "\r\n", false, true, true, true, false); inputEnded = true; break; }
		randomBuffer.clear();
	    }
	    writeKeyFileChannelPosition = 0;                
	    writeKeyFileChannelTransfered = 0;                
	    inputEnded = false;


	    log("finished\r\n", false, true, false, false, false);
	    System.exit(0);
	}

//	====================================================================================================================
//	Finieshed writing key file
//	====================================================================================================================
	




//////////////////////////////////////////////////// KEY CHECKSUM =====================================================

	if (key_checksum)
	{
	    log("\r\nKey CheckSum: (" + FinalCrypt.HASH_ALGORITHM_NAME + "): \"" + keyFCPath.path.toAbsolutePath().toString() + "\"...\r\n", false, true, false, false, false); 
	    long    readKeySourceChannelPosition =  0; 
	    long    readKeySourceChannelTransfered =  0; 
	    int readKeySourceBufferSize = (1 * 1024 * 1024);
	    ByteBuffer keySourceBuffer = ByteBuffer.allocate(readKeySourceBufferSize); keySourceBuffer.clear();
	    MessageDigest messageDigest = null; try { messageDigest = MessageDigest.getInstance(FinalCrypt.HASH_ALGORITHM_NAME); } catch (NoSuchAlgorithmException ex) { log("Error: NoSuchAlgorithmException: MessageDigest.getInstance(\" "+ FinalCrypt.HASH_ALGORITHM_NAME + "\")\r\n", false, true, true, true, false);}
	    int x = 0;
	    while ( ! keySourceChecksumReadEnded )
	    {
		try (final SeekableByteChannel readKeySourceChannel = Files.newByteChannel(keyFCPath.path, finalCrypt.getEnumSet(EnumSet.of(StandardOpenOption.READ))))
		{
		    readKeySourceChannel.position(readKeySourceChannelPosition);
		    readKeySourceChannelTransfered = readKeySourceChannel.read(keySourceBuffer); keySourceBuffer.flip(); readKeySourceChannelPosition += readKeySourceChannelTransfered;
		    readKeySourceChannel.close();

		    messageDigest.update(keySourceBuffer);
		    if ( readKeySourceChannelTransfered < 0 ) { keySourceChecksumReadEnded = true; }
		} catch (IOException ex) { keySourceChecksumReadEnded = true; log("Error: readKeySourceChannel = Files.newByteChannel(..) " + ex.getMessage() + "\r\n", false, true, false, true, false); }
		x++;
		keySourceBuffer.clear();
	    }
	    byte[] hashBytes = messageDigest.digest();
	    String hashString = getHexString(hashBytes,2);
	    log("Message Digest:         " + hashString + "\r\n\r\n", false, true, false, false, false); 
	}
	
	
//////////////////////////////////////////////////// BUILD SELECTION /////////////////////////////////////////////////
        
	targetFCPathList = new FCPathList<FCPath>();
//	if (!cfsetneeded) { keyFCPath = (FCPath) targetPathList.get(0); }
	if (!kfsetneeded) 
	{
//    					  getFCPath(UI ui, String caller,	      Path path, boolean isKey,          Path keyPath, boolean disabledMAC,    boolean report)
		     keyFCPath = Validate.getFCPath(   ui,            "", targetPathList.get(0),          true, targetPathList.get(0), finalCrypt.disabledMAC,          true);
	}
	
	if ( dictionary )
	{
//	    Validate.buildTargetSelection(UI ui, ArrayList<Path> userSelectedItemsPathList, Path keyPath, ArrayList<FCPath> targetFCPathList, boolean symlink, String pattern, boolean negatePattern,    boolean disabledMAC, boolean status)
	    Validate.buildSelection(	   this,			    targetPathList,    keyFCPath,		    targetFCPathList,	      symlink,	      pattern,	       negatePattern, finalCrypt.disabledMAC,          true);
	    if ( targetFCPathList.validFiles > 0 )
	    {
//		     Validate.isValidFile(UI ui, 	  String caller,	       Path targetSourcePath, boolean isKey,  boolean device, long minSize, boolean symlink, boolean writable, boolean report)
		FCPath dictFileFCPath = Validate.getFCPath(ui, "NA", Paths.get(dictionaryFilePathString), false, Paths.get(dictionaryFilePathString), false, true);
		if ( ( dictFileFCPath.exist ) && (dictFileFCPath.isValidFile) && (dictFileFCPath.isReadable) && ( dictFileFCPath.size > 0) )
		{
		    bflines = 0;
		    try { bflines = Files.lines(dictFileFCPath.path).count(); } catch (IOException ex) { log("Files.lines(" + dictFileFCPath.path.toAbsolutePath().toString() + ").count();" + ex.getMessage(), false, true, true, true, false); }

		    bfcounter = 1;
		    bruteForceDataStats = new Stats(); bruteForceDataStats.reset();
		    bruteForceDataStats.setAllDataStartNanoTime(); bruteForceDataStats.clock();
		    		    
		    TimerTask printTask = new TimerTask() { @Override public void run()
		    {
			if ( bruteForceFCPathTargetPrint != null ) { log("Brute Force testing target: \"" + bruteForceFCPathTargetPrint.path.toAbsolutePath().toString() + "\" password count: " + bfcounter + " " + (bfcounter / (bflines / 100 )) + "% \r\n", false, true, false, false, false); } 
		    }};
		    Timer printTimer = new java.util.Timer(); printTimer.schedule(printTask, 1000L, 1000L);
		    
		    log("\r\nStart Brute Force testing " + bflines + " passwords...\r\n\r\n", false, true, true, false, false); 
		    log("Brute Force testing target: \"" + targetFCPathList.get(0).path.toAbsolutePath().toString() + "\" password count: " + 1 + " " + (bfcounter / (bflines / 100 )) + "%\r\n", false, true, false, false, false);
		    
		    
		    String pwdString;
		    boolean pwdFound = false;
		    BufferedReader bufferedReader = null;
		    try { bufferedReader = new BufferedReader(new FileReader(dictFileFCPath.path.toFile())); }
		    catch (FileNotFoundException ex) { log("Error: new FileReader(" + dictFileFCPath.path.toAbsolutePath().toString() + ");" + ex.getMessage(), false, true, true, true, false); }
         
		    try
		    {
			pwloop: while ((! pwdFound) && ((pwdString = bufferedReader.readLine()) != null))
			{
			    pwd = pwdString;
			    MessageDigest messageDigest = null; try { messageDigest = MessageDigest.getInstance(FinalCrypt.HASH_ALGORITHM_NAME); } catch (NoSuchAlgorithmException ex) { log("Error: NoSuchAlgorithmException: MessageDigest.getInstance(\" "+ FinalCrypt.HASH_ALGORITHM_NAME + "\")\r\n", true, true, true, true, false);}
			    messageDigest.update(pwd.getBytes());
			    byte[] hashBytes = messageDigest.digest();
			    pwdBytes = GPT.hex2Bytes(getHexString(hashBytes,2));

			    finalCrypt.setPwd(pwd);
			    finalCrypt.setPwdBytes(pwd);

			    targetFCPathList = new FCPathList<FCPath>();
			    Validate.buildSelection(this,			          targetPathList,    keyFCPath,		          targetFCPathList,	    symlink,	    pattern,	     negatePattern, finalCrypt.disabledMAC,         false);
			    pathlistloop: for (FCPath fcPathItem : targetFCPathList)
			    {
				bruteForceFCPathTargetPrint = fcPathItem;
//				log(bfcounter + " testing target: \"" + fcPathItem.path.toAbsolutePath().toString() + "\" password: \"" + pwd + "\" result: " + fcPathItem.isDecryptable + "\r\n", false, true, false, false, false);
				bfcounter++;
				if (fcPathItem.isDecryptable) { pwdFound = true; break pwloop; }
			    }
			}
		    } catch (IOException ex) { log("Error: bufferedReader.readLine(" + dictFileFCPath.path + ");" + ex.getMessage(), false, true, true, true, false); }

		    
		    
		    if ( bruteForceFCPathTargetPrint != null ) { log("Brute Force testing target: \"" + bruteForceFCPathTargetPrint.path.toAbsolutePath().toString() + "\" password count: " + (bfcounter - 1) + " " + (bfcounter / (bflines / 100 )) + "%\r\n", false, true, false, false, false); } 
		    if (pwdFound)
		    {
			log("\r\nPassword found: \"" + pwd + "\"\r\n", false, true, false, false, false);
		    }
		    else
		    {
			log("\r\nPassword not found\r\n", false, true, false, false, false);
		    }
		    bruteForceDataStats.setAllDataEndNanoTime(); bruteForceDataStats.clock();
		    printTimer.cancel(); printTimer.purge();
		    log("\r\nFinished Brute force testing " + (bfcounter - 1) + " / " + bflines + " passwords in " + bruteForceDataStats.getElapsedTime(bruteForceDataStats.getAllDataEndEpoch() - bruteForceDataStats.getAllDataStartEpoch()) + " " + bruteForceDataStats.getBruteForceThroughPut(bfcounter, bruteForceDataStats.getAllDataEndEpoch() - bruteForceDataStats.getAllDataStartEpoch()) + "\r\n\r\n", false, true, true, false, false);
		}
		else
		{
		    log("\r\nWarning: dictionary file: " + dictFileFCPath.path.toAbsolutePath().toString() + " is not a valid file!\r\n\r\n", false, true, true, false, false);
		    log(dictFileFCPath.getString() + "\r\n", false, true, true, false, false);
		}
	    }
	    else
	    {
		log("\r\nWarning: No valid target files found\r\n\r\n", false, true, true, false, false);
		log(targetFCPathList.getStats() + "\r\n", false, true, true, false, false);
	    }
	    
	    System.exit(0);
	}
	else
	{
	    log("\r\nScanning files... ", false, true, true, false, false); 
//	    Validate.buildSelection(UI ui, ArrayList<Path> userSelectedItemsPathList, Path keyPath, ArrayList<FCPath> targetFCPathList, boolean symlink, String pattern, boolean negatePattern,    boolean disabledMAC, boolean status)
	    Validate.buildSelection( this,			      targetPathList,    keyFCPath,		      targetFCPathList,		symlink,	pattern,	 negatePattern, finalCrypt.disabledMAC,         false);
	    log("Finished\r\n\r\n", false, true, true, false, false);
	}
	
	
	if ( scan )
	{
	    for (FCPath fcPathItem : targetFCPathList)
	    {
		log(fcPathItem.getString() + "\r\n", false, true, true, false, false);
	    }
	    log("=========================================\r\n\r\n", false, true, true, false, false);
	    log(targetFCPathList.getStats() + "\r\n", false, true, true, false, false);
	    System.exit(0);
	}
	
/////////////////////////////////////////////// SET BUILD MODES ////////////////////////////////////////////////////

	if  (
		    ((keyFCPath != null) && (keyFCPath.isKey) && (keyFCPath.isValidKey))
		||  ((keyFCPath != null) && (keyFCPath.type == FCPath.DIRECTORY) && (keyFCPath.isValidKeyDir))
	    )
	{

// ================================================================================================================================================================================================
// Building filtered lists (equal to checkModeReady() in GUIFX)
// ================================================================================================================================================================================================

	    invalidFilesList =	    filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.INVALID);							// else { invalidFilesList = null; }
	    decryptedList =	    filter(targetFCPathList,(FCPath fcPath) -> fcPath.isDecrypted);								// else { decryptedList = null; }
	    encryptableList =	    filter(targetFCPathList,(FCPath fcPath) -> fcPath.isEncryptable); encryptablesFound = true;					// else { encryptableList = null; }


	    unencryptableList =	    filter(targetFCPathList,(FCPath fcPath) -> (fcPath.isUnEncryptable) && (fcPath.isDecrypted)  && (fcPath.size > 0));		// else { unencryptableList = null; }

	    encryptedList =	    filter(targetFCPathList,(FCPath fcPath) -> fcPath.isEncrypted);								// else { encryptedList = null; }
	    decryptableList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.isDecryptable); decryptablesFound = true;					// else { decryptableList = null; }
	    undecryptableList =	    filter(targetFCPathList,(FCPath fcPath) -> (fcPath.isUnDecryptable) && (fcPath.isEncrypted) && (fcPath.size > 0));		// else { undecryptableList = null; }

	    emptyList =		    filter(targetFCPathList,(FCPath fcPath) -> fcPath.size == 0 && fcPath.type == FCPath.FILE);					// else { emptyList = null; }
	    symlinkList =	    filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.SYMLINK);							// else { symlinkList = null; }
	    unreadableList =	    filter(targetFCPathList,(FCPath fcPath) -> (! fcPath.isReadable) && (fcPath.type == FCPath.FILE));				// else { unreadableList = null; }
	    unwritableList =	    filter(targetFCPathList,(FCPath fcPath) -> (! fcPath.isWritable) && (fcPath.type == FCPath.FILE));				// else { unwritableList = null; }
	    hiddenList =	    filter(targetFCPathList,(FCPath fcPath) -> fcPath.isHidden);								// else { hiddenList = null; }

	    readAutoKeyList =	    filter(targetFCPathList,(FCPath fcPath) -> fcPath.matchedReadAutoKey);							// else { readAutoKeyList = null; }
	    writeAutoKeyList =	    filter(targetFCPathList,(FCPath fcPath) -> fcPath.needsWriteAutoKey);							// else { writeAutoKeyList = null; }

	    // Create Key Device
	    if (keyFCPath.type == FCPath.FILE)
	    {
		if (targetFCPathList.validDevices > 0)
		{
		    createManualKeyList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE); // log("Create Key List:\r\n" + createManualKeyList.getStats());
		    createManualKeyDeviceFound = true;
		} else { createManualKeyDeviceFound = false; }
	    }		
	    else if (keyFCPath.type == FCPath.DEVICE)
	    {
		// Clone Key Device
		if ((targetFCPathList.validDevices > 0) && (targetFCPathList.matchingKey == 0))
		{
		    final FCPath keyFCPath2 = keyFCPath; // for Lambda expression
		    cloneManualKeyList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE && fcPath.path.compareTo(keyFCPath2.path) != 0); // log("Clone Key List:\r\n" + cloneManualKeyList.getStats());
		    cloneManualKeyDeviceFound = true;
		} else { cloneManualKeyDeviceFound = false; }
	    } else { cloneManualKeyDeviceFound = false; }
	} else { createManualKeyDeviceFound = false; }

	if ((printgpt) && ((targetFCPathList.validDevices > 0) || (targetFCPathList.validDevicesProtected > 0)))
	{
	    printGPTTargetList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE || fcPath.type == FCPath.DEVICE_PROTECTED); // log("Create Key List:\r\n" + createManualKeyList.getStats());
	    printGPTDeviceFound = true;
	} else { printGPTDeviceFound = false; }
	
	if ((deletegpt) && (targetFCPathList.validDevices > 0))
	{
	    deleteGPTTargetList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE); // log("Create Key List:\r\n" + createManualKeyList.getStats());
	    if ( deleteGPTTargetList.size() > 0 ) { deleteGPTDeviceFound = true; }
	    else { deleteGPTDeviceFound = false; }
	}
	else if ((deletegpt) && (targetFCPathList.validDevicesProtected > 0))
	{
	    deleteGPTTargetList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE_PROTECTED); // log("Create Key List:\r\n" + createManualKeyList.getStats());
	    FCPath fcPath = (FCPath) deleteGPTTargetList.get(0); log("WARNING: Device: " + fcPath.path + " is protected!!!\r\n", false, true, true, false, false); deleteGPTDeviceFound = false; 
	}
	else { deleteGPTDeviceFound = false; }

	
/////////////////////////////////////////////// FINAL VALIDATION & EXECUTE MODES ////////////////////////////////////////////////////

//	log("Warning: Default Message Authentication Code Mode Disabled! NOT compattible to MAC Mode Encrypted files!!!\r\n", true, true, true, false, false);
//	log("Info:    Default Message Authentication Code Mode Enabled\r\n", true, true, true, false, false);

	DeviceManager deviceManager;
	if ((encrypt))
	{
	    if (finalCrypt.disabledMAC)	{ log("\"Warning: MAC Mode Disabled! (files will be encrypted without Message Authentication Code Header)\r\n", true, true, true, false, false); }

	    if ( finalCrypt.getTest() ) { testListPrompt(); }

	    if ((encryptableList.size() > 0) && (encryptableList.encryptableFiles > 0))
	    {
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
		    @Override public void run()
		    {
			
			if (finalCrypt.processRunning)
			{
			    finalCrypt.setStopPending(true);
			    try{ Thread.sleep(2000); } catch (InterruptedException ex) {}
			    log("\r\nEncryption User Interrupted...\r\n", false, true, true, false, false);
			}
		    }
		});
		processStarted();
		finalCrypt.encryptSelection(targetFCPathList, encryptableList, keyFCPath, true, pwd, pwdBytes, false);
//		catch (InterruptedException ex){ log("Encryption Interrupted (CLUI): " + ex.getMessage() +" \r\n", false, true, true, false, false); }
	    }
	    else
	    {
		log("No encryptable targets found:\r\n", false, true, true, false, false); // log(targetFCPathList.getStats(), false, true, false, false, false);
		log(getScanResults(false), false, true, true, false, false); // log(targetFCPathList.getStats(), false, true, false, false, false);
	    }
	}
	else if ((decrypt))
	{
	    if (finalCrypt.disabledMAC)
	    {
		log("Warning: MAC Mode Disabled! Use --encrypt if you know what you are doing!!!\r\n", true, true, true, false, false);
	    }
	    else
	    {
		if ( finalCrypt.getTest() ) { testListPrompt(); }
		
		if ((decryptableList.size() > 0) && (decryptableList.decryptableFiles > 0))
		{
		    Runtime.getRuntime().addShutdownHook(new Thread()
		    {
			@Override public void run()
			{
			    if (finalCrypt.processRunning)
			    {
				finalCrypt.setStopPending(true);
				try{ Thread.sleep(2000); } catch (InterruptedException ex) {}
				log("\r\nDecryption User Interrupted...\r\n", false, true, true, false, false);
			    }
			}
		    });
		    processStarted();
		    finalCrypt.encryptSelection(targetFCPathList, decryptableList, keyFCPath, false, pwd, pwdBytes, false);
//		    catch (InterruptedException ex) { log("Decryption Interrupted (CLUI): " + ex.getMessage() +" \r\n", false, true, true, false, false); }
		}
		else			
		{
		    log("No decryptable targets found\r\n\r\n", false, true, true, false, false);
		    if ( targetFCPathList.encryptedFiles > 0 ) { log("Wrong key / password?\r\n\r\n", false, true, false, false, false); }
		    log(getScanResults(false), false, true, true, false, false); // log(targetFCPathList.getStats(), false, true, false, false, false);
		}
	    }
	}
	else if (createManualKeyDev)
	{
	    if (createManualKeyDeviceFound)	{ processStarted(); deviceManager = new DeviceManager(ui); deviceManager.start(); deviceManager.createManualKeyDevice(keyFCPath, (FCPath) createManualKeyList.get(0)); processFinished(new FCPathList<FCPath>() , false); }
	    else				{ log("No valid target device found:\r\n", false, true, true, false, false); log(targetFCPathList.getStats(), false, true, false, false, false); }
	}
	else if ((clonekeydev) && (cloneManualKeyDeviceFound))
	{
	    if (cloneManualKeyDeviceFound)	{ processStarted(); deviceManager = new DeviceManager(ui); deviceManager.start(); deviceManager.cloneManualKeyDevice(keyFCPath, (FCPath) cloneManualKeyList.get(0));  processFinished(new FCPathList<FCPath>() , false); }
	    else				{ log("No valid target device found:\r\n", false, true, true, false, false); log(targetFCPathList.getStats(), false, true, false, false, false); }
	}
	else if ((printgpt) && (printGPTDeviceFound))
	{
	    if (printGPTDeviceFound)		{ deviceManager = new DeviceManager(ui); deviceManager.start(); deviceManager.printGPT( (FCPath) printGPTTargetList.get(0)); }
	    else				{ log("No valid target device found:\r\n", false, true, true, false, false); log(targetFCPathList.getStats(), false, true, false, false, false); }
	}
	else if ((deletegpt) && (deleteGPTDeviceFound))
	{
	    if (deleteGPTDeviceFound)		{ deviceManager = new DeviceManager(ui); deviceManager.start(); deviceManager.deleteGPT( (FCPath) deleteGPTTargetList.get(0)); }
	    else				{ log("No valid target device found:\r\n", false, true, true, false, false); log(targetFCPathList.getStats(), false, true, false, false, false); }
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
            log("Adding items from batchfile: " + batchFilePathString + "\r\n", false, true, true, false, false);
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
            catch (IOException ex) { log("Files.readAllLines(" + batchFilePath + ");" + ex.getMessage(), false, true, true, true, false); }
            if ( ! ifset ) { log("Warning: batchfile: " + batchFilePathString + " doesn't contain any valid items!\r\n", false, true, true, false, false); }
        }
        else
        {
            log("Warning: batchfile: " + batchFilePathString + " is not a valid file!\r\n", false, true, true, false, false);
        }
        return ifset;
    }

    public static FCPathList<FCPath> filter(ArrayList<FCPath> fcPathList, Predicate<FCPath> fcPath)
    {
	FCPathList<FCPath> result = new FCPathList<FCPath>() ;
	for (FCPath fcPathItem : fcPathList) { if (fcPath.test(fcPathItem)) { result.add(fcPathItem); } }
	return result;
    }
    
    public static Predicate<FCPath> isHidden() { return (FCPath fcPath) -> fcPath.isHidden; }
    
    public List<FCPath> filter(Predicate<FCPath> criteria, ArrayList<FCPath> list)
    {
	return list.stream().filter(criteria).collect(Collectors.<FCPath>toList());
    }
    
    private boolean validateIntegerString(String text) { try { Integer.parseInt(text); return true;} catch (NumberFormatException e) { return false; } }

    private void usagePrompt(boolean error)
    {
        usageReaderTimeoutThread = new UsageReaderTimeoutThread(this); usageReaderTimeoutThread.start();
        usageReaderThread = new UsageReaderThread(this);
	usageReaderThread.start();
	while (usageReaderTimeoutThread.isAlive()) { try { Thread.sleep(100); } catch (InterruptedException ex) { } }
	log("\r\n\r\n", false, true, false, false, false);
	System.exit(1);
    }
    
    private void testListPrompt()
    {
        testListReaderTimeoutThread = new TestListReaderTimeoutThread(this); testListReaderTimeoutThread.start();
        testListReaderThread = new TestListReaderThread(this);
	testListReaderThread.start();
	while (testListReaderTimeoutThread.isAlive()) { try { Thread.sleep(100); } catch (InterruptedException ex) { } }
	log("\r\n\r\n", false, true, false, false, false);
//	System.exit(1);
    }
    
    protected void usage(boolean error)
    {
//	if ( autoExitTaskTimer != null ) { autoExitTaskTimer.cancel(); autoExitTaskTimer.purge(); }
	
        String fileSeparator = java.nio.file.FileSystems.getDefault().getSeparator();
        log("\r\n", false, true, false, false, false);
        log("Usage:      java -cp finalcrypt.jar rdj/CLUI   <Mode>  [options] <Parameters>\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("Examples:\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --examples                     Print commandline examples.\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --encrypt --test -k \"key_dir\" -t \"target_dir\" -t \"target_file\" # Test Encrypt (Auto Key Mode)\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --decrypt --test -k \"key_dir\" -t \"target_dir\" -t \"target_file\" # Test Decrypt (Auto Key Mode)\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --encrypt -k \"key_dir\" -t \"target_dir\" -t \"target_file\"  # Encrypt (Auto Key Mode)\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --decrypt -k \"key_dir\" -t \"target_dir\" -t \"target_file\"  # Decrypt (Auto Key Mode)\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --encrypt -k \"key_file\" -t \"target_file\"  # Encrypt (Manual Key Mode not recommended)\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --decrypt -k \"key_file\" -t \"target_file\"  # Decrypt (Manual Key Mode not recommended)\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("Mode:\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            <--scan>              -k \"key_dir\"       -t \"target\"            Print scan results and quit.\r\n", false, true, false, false, false);
        log("            <--encrypt>           -k \"key_dir\"       -t \"target\"            Encrypt Targets.\r\n", false, true, false, false, false);
        log("            <--decrypt>           -k \"key_dir\"       -t \"target\"            Decrypt Targets.\r\n", false, true, false, false, false);
        log("            <--create-keydev>     -k \"key_file\"      -t \"target\"            Create Key Device (only unix).\r\n", false, true, false, false, false);
        log("            <--create-keyfile>    -K \"key_file\"      -S \"Size (bytes)\"      Create OTP Key File.\r\n", false, true, false, false, false);
        log("            <--clone-keydev>      -k \"source_device\" -t \"target_device\"     Clone Key Device (only unix).\r\n", false, true, false, false, false);
        log("            [--print-gpt]         -t \"target_device\"                        Print GUID Partition Table.\r\n", false, true, false, false, false);
        log("            [--delete-gpt]        -t \"target_device\"                        Delete GUID Partition Table (DATA LOSS!).\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
	log("Options:\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            [-h] [--help]                                                   Print help page.\r\n", false, true, false, false, false);
        log("            [--password]          -p \'password\'                             Optional password (non-interactive).\r\n", false, true, false, false, false);
        log("            [--password-prompt]   -pp                                       Optional password (safe interactive prompt).\r\n", false, true, false, false, false);
	log("            [--key-chksum]        -k \"key_file\"                             Calculate key checksum.\r\n", false, true, false, false, false);
        log("            [--no-key-size]                                                 Allow key-size less than the default minimum of " + FCPath.KEY_SIZE_MIN + " bytes.\r\n", false, true, false, false, false);
        log("            [-d] [--debug]                                                  Enables debugging mode.\r\n", false, true, false, false, false);
        log("            [-v] [--verbose]                                                Enables verbose mode.\r\n", false, true, false, false, false);
        log("            [--print]                                                       Print all bytes binary, hexdec & char (slows encryption severely).\r\n", false, true, false, false, false);
        log("            [-l] [--symlink]                                                Include symlinks (can cause double encryption! Not recommended!).\r\n", false, true, false, false, false);
        log("            [--disable-MAC]                                                 Disable MAC - (not compatible with MAC encrypted files!)\r\n", false, true, false, false, false);
        log("            [--version]                                                     Print " + version.getProductName() + " version.\r\n", false, true, false, false, false);
        log("            [--license]                                                     Print " + version.getProductName() + " license.\r\n", false, true, false, false, false);
        log("            [--check-update]                                                Check for online updates.\r\n", false, true, false, false, false);
//        log("            [--txt]                                                         Print text calculations.\r\n", false, true, false, false, false);
//        log("            [--bin]                                                         Print binary calculations.\r\n", false, true, false, false, false);
//        log("            [--dec]                                                         Print decimal calculations.\r\n", false, true, false, false, false);
//        log("            [--hex]                                                         Print hexadecimal calculations.\r\n", false, true, false, false, false);
//        log("            [--chr]                                                         Print character calculations.\r\n", false, true, false, false, false);
//        log("                                                                            Warning: The above Print options slows encryption severely.\r\n", false, true, false, false, false);
        log("            [-s size]                                                       Changes default I/O buffer size (size = KiB) (default 1024 KiB).\r\n", false, true, false, false, false);
        log("            [-S size]                                                       OTP Key File Size (size = bytes). See --create-keyfile \r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("Test Options:\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            [--test]                                                        Test run without executing (also prints statistics at the end).\r\n", false, true, false, false, false);
        log("            [--test \"answer\"]                                               Same but then with non interactive answer (c,1-13) included.\r\n", false, true, false, false, false);
	log("            [-pd] [--password-dictionary]  \"dict_file\"                      Brute force test plain text passwords from dictionary file.\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("Filtering Options:\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            [-w \'wildcard\']                                                 File wildcard INCLUDE filter. Uses: \"Globbing Patterns Syntax\".\r\n", false, true, false, false, false);
        log("            [-W \'wildcard\']                                                 File wildcard EXCLUDE filter. Uses: \"Globbing Patterns Syntax\".\r\n", false, true, false, false, false);
        log("            [-r \'regex\']                                                    File regular expression filter. Advanced filename filter!\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("Parameters:\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            <-k \"keydir\">                                                   The directory that holds your keys. Keep SECRET!\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            <-t / -b>                                                       Target items you want to encrypt. Individual (-t) or by batch (-b).\r\n", false, true, false, false, false);
        log("            <[-t \"file/dir\"]>                                               Target items (files or directories) you want to encrypt (recursive).\r\n", false, true, false, false, false);
        log("            <[-b \"batchfile\"]>                                              Batchfile with targetfiles you want to encrypt (only files).\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log(Version.getProductName() + " " + version.checkCurrentlyInstalledVersion(this) + " - Author: " + Version.getAuthor() + " <" + Version.getEmail() + "> - Copyright: " + Version.getCopyright() + "\r\n\r\n", false, true, false, false, false);
        System.exit(error ? 1 : 0);
    }

    private void examples()
    {
        log("\r\n", false, true, false, false, false);
        log("Usage:      java -cp finalcrypt.jar rdj/CLUI   <Mode>  [options] <Parameters>\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("Examples:\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            # Test Run Encrypt / Decrypt mydir and myfile auto creating and selecting keys in mykeydir\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --encrypt --test -k \"mykeydir\" -t \"mydocdir\" -t \"myfile\"\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --decrypt --test -k \"mykeydir\" -t \"mydocdir\" -t \"myfile\"\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            # Same but then with non interactive answer (c,1-13) included\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --encrypt --test c -k \"mykeydir\" -t \"mydocdir\" -t \"myfile\"\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --decrypt --test c -k \"mykeydir\" -t \"mydocdir\" -t \"myfile\"\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            # Encrypt / Decrypt mydir and myfile auto creating and selecting keys in mykeydir\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --encrypt -k \"mykeydir\" -t \"mydocdir\" -t \"myfile\"\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --decrypt -k \"mykeydir\" -t \"mydocdir\" -t \"myfile\"\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            # Encrypt / Decrypt files in batchfile\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --encrypt -k \"mykeydir\" -b \"mybatchfile\"\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --decrypt -k \"mykeydir\" -b \"mybatchfile\"\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            # Encrypt / Decrypt all *.doc files in mydir\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --encrypt -w \"*.doc\" -k \"mykeydir\" -t \"mydir\"\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --decrypt -w \"*.doc\" -k \"mykeydir\" -t \"mydir\"\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            # Encrypt / Decrypt all non *.doc files in mydir\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --encrypt -W \"*.doc\" -k \"mykeydir\" -t \"mydir\" \r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --decrypt -W \"*.doc\" -k \"mykeydir\" -t \"mydir\" \r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            # Encrypt / Decrypt all *.doc files in mydir\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --encrypt -r '^.*\\.doc$' -k \"mykeydir\" -t \"mydir\"\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --decrypt -r '^.*\\.doc$' -k \"mykeydir\" -t \"mydir\"\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            # Encrypt / Decrypt all non *.bit files in mydir\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --encrypt -r '(?!.*\\.bit$)^.*$' -k \"mykeydir\" -t \"mydir\"\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --decrypt -r '(?!.*\\.bit$)^.*$' -k \"mykeydir\" -t \"mydir\"\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("Brute force password dictionary testing (in case of forgotten passwords):\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --scan --password-dictionary \"dictionary.txt\" -k \"mykeydir\" -t \"myfile\"\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
	log("Create OTP Key file:\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
	log("            FinalCrypt automatically creates One-Time Pad Key Files. Creating Manual OTP keys is supported but not recommended\r\n\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj.CLUI --create-keyfile -K \"mykeyfile\" -S 268435456 # (256 MiB) echo $((1024**2*256))\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
	log("Key Device Examples (Linux):\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            # Create Key Device with 2 key partitions (e.g. on USB Mem Stick)\r\n", false, true, false, false, false);
        log("            # Beware: keyfile gets randomized before writing to Device\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --create-keydev -k mykeyfile -t /dev/sdb\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            # Print GUID Partition Table\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --print-gpt -t /dev/sdc\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            # Delete GUID Partition Table\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --delete-gpt -t /dev/sdc\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            # Clone Key Device (-k sourcekeydevice -t destinationkeydevice)\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --clone-keydev -k /dev/sdc -t /dev/sdd\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            # Encrypt / Decrypt myfile with raw key partition\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --encrypt -k /dev/sdc1 -t myfile\r\n", false, true, false, false, false);
        log("            java -cp finalcrypt.jar rdj/CLUI --decrypt -k /dev/sdc1 -t myfile\r\n", false, true, false, false, false);
        log("\r\n", false, true, false, false, false);
        log(Version.getProductName() + " " + version.checkCurrentlyInstalledVersion(this) + " - Author: " + Version.getAuthor() + " <" + Version.getEmail() + "> - Copyright: " + Version.getCopyright() + "\r\n\r\n", false, true, false, false, false);
        System.exit(0);
    }

    @Override public void processGraph(int value) {  }

    @Override
    public void processStarted() 
    {
    }

    @Override public void processProgress(int filesProgress, int fileProgress, long bytesTotalParam, long bytesProcessedParam, double bytesPerMiliSecondParam)
    {
//        log("filesProgress: " + filesProgress + " fileProgress: " + fileProgress);
    }
    
    @Override public void processFinished(FCPathList<FCPath> openFCPathList, boolean open)
    {
    }

    @Override
    public void fileProgress()
    {
    }

//    @Override public void buildProgress(FCPathList<FCPath> targetFCPathList) {  }

    @Override
    public void buildReady(FCPathList<FCPath> fcPathListParam, boolean validBuild)
    {
	targetFCPathList = fcPathListParam;
    }
    
    public String getScanResults(boolean interactive)
    {
	String prefix = ""; if (interactive) { prefix = "print"; } else { prefix = "scanned"; }
	String results = "";
	results += "\r\n";
	    results += "\r\n";
	    results += "Scanning results:\r\n";
	    results += "\r\n";
	    if (interactive) { results += " C. Continue test\r\n"; }
	    results += " 1. " + prefix + " " + decryptedList.decryptedFiles + " decrypted files (" + Validate.getHumanSize(decryptedList.decryptedFilesSize,1,"Bytes") + ")\r\n";
	    results += " 2. " + prefix + " " + encryptableList.encryptableFiles + " encryptable files (" + Validate.getHumanSize(encryptableList.encryptableFilesSize,1,"Bytes") + ")\r\n";
    //
	    results += " 3. " + prefix + " " + encryptedList.encryptedFiles + " encrypted files (" + Validate.getHumanSize(encryptedList.encryptedFilesSize,1,"Bytes") + ")\r\n";
	    results += " 4. " + prefix + " " + decryptableList.decryptableFiles + " decryptable files (" + Validate.getHumanSize(decryptableList.decryptableFilesSize,1,"Bytes") + ")\r\n";
    //
	    results += " 5. " + prefix + " " + emptyList.emptyFiles + " empty files \r\n";
	    results += " 6. " + prefix + " " + symlinkList.symlinkFiles + " symlink files \r\n";
	    results += " 7. " + prefix + " " + unreadableList.unreadableFiles + " unreadable files (" + Validate.getHumanSize(unreadableList.unreadableFilesSize,1,"Bytes") + ")\r\n";
	    results += " 8. " + prefix + " " + unwritableList.unwritableFiles + " unwritable files (" + Validate.getHumanSize(unwritableList.unwritableFilesSize,1,"Bytes") + ")\r\n";
	    results += " 9. " + prefix + " " + hiddenList.hiddenFiles + " hidden files (" + Validate.getHumanSize(hiddenList.hiddenFilesSize,1,"Bytes") + ")\r\n";
    //
	    results += "10. " + prefix + " " + unencryptableList.unEncryptableFiles + " unencryptable (" + Validate.getHumanSize(unencryptableList.unEncryptableFilesSize,1,"Bytes") + ")\r\n";
	    results += "11. " + prefix + " " + undecryptableList.unDecryptableFiles + " undecryptable (" + Validate.getHumanSize(undecryptableList.unDecryptableFilesSize,1,"Bytes") + ")\r\n";
	    results += "12. " + prefix + " " + readAutoKeyList.matchedAutoKeyFiles + " key matched files (" + Validate.getHumanSize(readAutoKeyList.matchedAutoKeyFilesSize,1,"Bytes") + ")\r\n";
	    results += "13. " + prefix + " " + writeAutoKeyList.writeAutoKeyFiles + " key write files (" + Validate.getHumanSize(writeAutoKeyList.writeAutoKeyFilesSize,1,"Bytes") + ")\r\n";
	    results += "\r\n";

	    if (interactive) { results += "What list would you like to see ? "; }
	return results;
    }

    @Override public void test(String message) { log(message, true, true, false, false, false); }
    
    @Override
    synchronized public void log(String message, boolean status, boolean log, boolean logfile, boolean errfile, boolean print)
    {
	if	((!status) && (!log))   {  }
	else if ((!status) && ( log))   { log(message,errfile); }
	else if (( status) && (!log))   {  }
	else if (( status) && ( log))	{ log(message,errfile); }
	if	(logfile)		{ logfile(message); }
	if	(errfile)		{ errfile(message); }
	if	(print)			{ print(message,errfile); }
    }

    public void status(String message)		    {  }
    public void log(String message, boolean err)    { if ( ! err ) { System.out.print(message); } else { System.err.print(message); } }
    public void logfile(String message)		    { try { Files.write(configuration.getLogFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND); } catch (IOException ex) { log("Files.write(" + configuration.getLogFilePath() + ")..));", false, true, true, false, false); } }
    public void errfile(String message)		    { try { Files.write(configuration.getErrFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND); } catch (IOException ex) { log("Files.write(" + configuration.getErrFilePath() + ")..));", false, true, true, false, false); } }
    public void print(String message, boolean err)  { if ( ! err ) { System.out.print(message); } else { System.err.print(message); } }
    
    public static void main(String[] args) { new CLUI(args); }
}

class UsageReaderThread extends Thread
{
    private CLUI clui;
    
    public UsageReaderThread(CLUI ui) { this.clui = ui; }
    
    @Override public void run()
    {
	clui.log("\r\nWould you like to see the User Manual (n/Y)? ", false, true, false, false, false); // Leave Error file to: true
        try(Scanner in = new Scanner(System.in))
	{
            String input = in.nextLine(); 
	    if	(
			( input.trim().toLowerCase().equals("y") )
		    ||	( input.trim().toLowerCase().length() == 0 )
		    ||	( input.toLowerCase().equals("\r\n") )
		)   { clui.usage(true); }
	    else    { clui.log("\r\n", false, true, false, false, false); System.exit(0); }
        }
    }
}

class UsageReaderTimeoutThread extends Thread
{
    private CLUI clui;
    
    public UsageReaderTimeoutThread(CLUI ui) { this.clui = ui; }

    @Override public void run()
    {
        try {
            Thread.sleep(3000);
//	    System.exit(0);
//            Robot robot = new Robot();
//            robot.keyPress(KeyEvent.VK_ENTER);
//            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch(Exception e) { }
    }

}

class TestListReaderThread extends Thread
{
    private CLUI clui;
    
    public TestListReaderThread(CLUI ui) { this.clui = ui; }

    @Override public void run()
    {

	if (clui.testAnswer.isEmpty())
	{	    
	    clui.log(clui.getScanResults(true), false, true, true, false, false); // Leave Error file to: true
	    try(Scanner in = new Scanner(System.in)) { clui.testAnswer = in.nextLine().trim(); }
	}
	
	clui.log("\r\n", false, false, true, false, false); // Simulate read input enter on logfile as seen on screen
	
	if	    ( (clui.finalCrypt.getTest()) && ( clui.testAnswer.trim().toLowerCase().equals("c") ))	    { clui.testListAborted = false; }
	else if ( clui.testAnswer.trim().toLowerCase().equals("1") )
	{
	    clui.testListAborted = true;
	    if ( (clui.decryptedList != null) && (clui.decryptedList.size() > 0) ) { clui.log("\r\nDecrypted Files:\r\n\r\n", false, true, true, false, false);
	    for (Iterator it = clui.decryptedList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); clui.log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); } clui.log("\r\n", false, true, true, false, false); } else { }
	}
	else if ( clui.testAnswer.trim().toLowerCase().equals("2") )
	{
	    clui.testListAborted = true;
	    if ( (clui.encryptableList != null) && (clui.encryptableList.size() > 0) ) { clui.log("\r\nEncryptable Files:\r\n\r\n", false, true, true, false, false);
	    for (Iterator it = clui.encryptableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); clui.log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); } clui.log("\r\n", false, true, true, false, false); } else { }
	}
	else if ( clui.testAnswer.trim().toLowerCase().equals("3") )
	{
	    clui.testListAborted = true;
	    if ( (clui.encryptedList != null) && (clui.encryptedList.size() > 0) ) { clui.log("\r\nEncryptedList Files:\r\n\r\n", false, true, true, false, false);
	    for (Iterator it = clui.encryptedList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); clui.log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); } clui.log("\r\n", false, true, true, false, false); } else { }
	}
	else if ( clui.testAnswer.trim().toLowerCase().equals("4") )
	{
	    clui.testListAborted = true;
	    if ( (clui.decryptableList != null) && (clui.decryptableList.size() > 0) ) { clui.log("\r\nDecryptable Files:\r\n\r\n", false, true, true, false, false);
	    for (Iterator it = clui.decryptableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); clui.log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); } clui.log("\r\n", false, true, true, false, false); } else { }
	}
	else if ( clui.testAnswer.trim().toLowerCase().equals("5") )
	{
	    clui.testListAborted = true;
	    if ( (clui.emptyList != null) && (clui.emptyList.size() > 0) ) { clui.log("\r\nEmpty Files:\r\n\r\n", false, true, true, false, false);
	    for (Iterator it = clui.emptyList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); clui.log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); } clui.log("\r\n", false, true, true, false, false); } else { }
	}
	else if ( clui.testAnswer.trim().toLowerCase().equals("6") )
	{
	    clui.testListAborted = true;
	    if ( (clui.symlinkList != null) && (clui.symlinkList.size() > 0) ) { clui.log("\r\nSymlink Files:\r\n\r\n", false, true, true, false, false);
	    for (Iterator it = clui.symlinkList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); clui.log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); } clui.log("\r\n", false, true, true, false, false); } else { }
	}
	else if ( clui.testAnswer.trim().toLowerCase().equals("7") )
	{
	    clui.testListAborted = true;
	    if ( (clui.unreadableList != null) && (clui.unreadableList.size() > 0) ) { clui.log("\r\nUnreadable Files:\r\n\r\n", false, true, true, false, false);
	    for (Iterator it = clui.unreadableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); clui.log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); } clui.log("\r\n", false, true, true, false, false); } else { }
	}
	else if ( clui.testAnswer.trim().toLowerCase().equals("8") )
	{
	    clui.testListAborted = true;
	    if ( (clui.unwritableList != null) && (clui.unwritableList.size() > 0) ) { clui.log("\r\nUnwritable Files:\r\n\r\n", false, true, true, false, false);
	    for (Iterator it = clui.unwritableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); clui.log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); } clui.log("\r\n", false, true, true, false, false); } else { }
	}
	else if ( clui.testAnswer.trim().toLowerCase().equals("9") )
	{
	    clui.testListAborted = true;
	    if ( (clui.hiddenList != null) && (clui.hiddenList.size() > 0) ) { clui.log("\r\nHiddenList Files:\r\n\r\n", false, true, true, false, false);
	    for (Iterator it = clui.hiddenList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); clui.log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); } clui.log("\r\n", false, true, true, false, false); } else { }
	}
	else if ( clui.testAnswer.trim().toLowerCase().equals("10") )
	{
	    clui.testListAborted = true;
	    if ( (clui.unencryptableList != null) && (clui.unencryptableList.size() > 0) ) { clui.log("\r\nUnencryptable Files:\r\n\r\n", false, true, true, false, false);
	    for (Iterator it = clui.unencryptableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); clui.log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); } clui.log("\r\n", false, true, true, false, false); } else { }
	}
	else if ( clui.testAnswer.trim().toLowerCase().equals("11") )
	{
	    clui.testListAborted = true;
	    if ( (clui.undecryptableList != null) && (clui.undecryptableList.size() > 0) ) { clui.log("\r\nUndecryptable Files:\r\n\r\n", false, true, true, false, false);
	    for (Iterator it = clui.undecryptableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); clui.log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); } clui.log("\r\n", false, true, true, false, false); } else { }
	}
	else if ( clui.testAnswer.trim().toLowerCase().equals("12") )
	{
	    clui.testListAborted = true;
	    if ( (clui.readAutoKeyList != null) && (clui.readAutoKeyList.size() > 0) ) { clui.log("\r\nMatched Key Files:\r\n\r\n", false, true, true, false, false);
	    for (Iterator it = clui.readAutoKeyList.iterator(); it.hasNext();)
	    {
			FCPath fcPath = (FCPath) it.next();
		Path autoKeyPath = null;
		if (fcPath.isDecrypted) { autoKeyPath = Paths.get(clui.keyFCPath.path.toAbsolutePath().toString(), fcPath.path.toAbsolutePath().toString().replace(":", "") + ".bit"); }
		else			{ autoKeyPath = Paths.get(clui.keyFCPath.path.toAbsolutePath().toString(), fcPath.path.toAbsolutePath().toString().replace(":", "")); }
		clui.log(autoKeyPath.toAbsolutePath().toString() + "\r\n", false, true, true, false, false);
	    } clui.log("\r\n", false, true, true, false, false); } else { }
	}
	else if ( clui.testAnswer.trim().toLowerCase().equals("13") )
	{
	    clui.testListAborted = true;
	    if ( (clui.writeAutoKeyList != null) && (clui.writeAutoKeyList.size() > 0) ) { clui.log("\r\nWrite Key Files:\r\n\r\n", false, true, true, false, false);
	    for (Iterator it = clui.writeAutoKeyList.iterator(); it.hasNext();)
	    {
		FCPath fcPath = (FCPath) it.next();
		Path autoKeyPath = Paths.get(clui.keyFCPath.path.toAbsolutePath().toString(), fcPath.path.toAbsolutePath().toString().replace(":", "") + ".bit");
		clui.log(autoKeyPath.toAbsolutePath().toString() + "\r\n", false, true, true, false, false);
	    } clui.log("\r\n", false, true, true, false, false); } else { }
	}
	else if ( clui.testAnswer.trim().toLowerCase().length() == 0 )    { clui.testListAborted = true; System.exit(0); }
	else if ( clui.testAnswer.toLowerCase().equals("\r\n"))	    { clui.testListAborted = true; System.exit(0); }
	else { clui.testListAborted = true; System.exit(0); }
    }
}

class TestListReaderTimeoutThread extends Thread
{
    private CLUI clui;
    
    public TestListReaderTimeoutThread(CLUI ui) { this.clui = ui; }

    @Override public void run()
    {
        try {
            Thread.sleep(3000);
//	    System.exit(0);
//            Robot robot = new Robot();
//            robot.keyPress(KeyEvent.VK_ENTER);
//            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch(Exception e) { }
	if ( clui.testListAborted ) { clui.log("\r\n\r\n", false, true, false, false, false); System.exit(0); }
    }

}

class ConsoleEraser extends Thread
{
    private boolean running = true;
    public void run()		    { while (running) { System.err.print("\b "); try { Thread.currentThread().sleep(1); } catch(InterruptedException err) { break; } } }
    public synchronized void halt() { running = false; }
}