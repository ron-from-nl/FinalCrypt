/*
 * CC BY-NC-ND 4.0 2017 Ron de Jong (ronuitzaandam@gmail.com).
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

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.security.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimerTask;

public class FinalCrypt extends Thread
{
    public static boolean verbose = false;
//    private boolean debug = false, print = false, symlink = false, txt = false, bin = false, dec = false, hex = false, chr = false, dry = false;
    private boolean symlink = false, txt = false, test = false;
    protected static boolean print = false, bin = false, dec = false, hex = false, chr = false;

    private final int BUFFERSIZEDEFAULT = (1 * 1024 * 1024); // 1MB BufferSize overall better performance
    private int bufferSize = BUFFERSIZEDEFAULT; // Default 1MB
    private int readTargetSourceBufferSize;
    private int readKeySourceBufferSize;
    private int wrteTargetDestinBufferSize;

//    private int printAddressByteCounter = 0;
    private final UI ui;
    
    private TimerTask updateProgressTimerTask;
    private java.util.Timer updateProgressTaskTimer;

    private boolean stopPending = false;
    public static boolean pausing = false;
    public boolean processRunning = false;

    private boolean targetSourceEnded;
    // Validate line 124 macVersion
    // FinalCrypt line 1045 macVersion
//											        1	  2         3         4         5         6         7
//										       1234567890123456789012345678901234567890123456789012345678901234567890
    public static final String FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V1 = "FinalCrypt - File Encryption Program - Plain Text Authentication Token"; // NEVER EVER CHANGE!!!!!!!!!!!
    public static final String FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V2 = "FinalCrypt - File Encryption Program - Plain Text Auth Token Version 2"; // NEVER EVER CHANGE!!!!!!!!!!!
    public static final String FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3 = "FinalCrypt - One Time Pad File Encryption - Plain Text MAC Version 003"; // NEVER EVER CHANGE!!!!!!!!!!!
    
    private Calendar	startCalendar;
    private Calendar	processProgressCalendar;
    private static double   filesBytesPerMilliSecond = 0;
    private final long UPDATE_PROGRESS_TIMERTASK_PERIOD = 100L;
//
    public static final String UTF8_UNSUCCEEDED_SYMBOL =		    "U";
    public static final String UTF8_UNSUCCEEDED_DESC =		    "Unsucceeded";
    
    public static final String UTF8_SUCCESSUNKNOWN_SYMBOL =	    "?";
    public static final String UTF8_SUCCESSUNKNOWN_DESC =	    "Success Unknown";
    
    public static final String UTF8_SUCCEEDED_SYMBOL =		    "S";
    public static final String UTF8_SUCCEEDED_DESC =		    "Succeeded";
    
    public static final String UTF8_UNENCRYPTABLE_SYMBOL =	    "UE";
    public static final String UTF8_UNENCRYPTABLE_DESC =	    "Unencryptable";

    public static final String UTF8_UNDECRYPTABLE_SYMBOL =	    "UD";
    public static final String UTF8_UNDECRYPTABLE_DESC =	    "Undecryptable";

    public static final String UTF8_KEY_DESC =			    "Key";
    public static final String UTF8_KEY_SYMBOL =			    "K";
    
    public static final String UTF8_OLD_TARGET_DESC =		    "Old Target";
    public static final String UTF8_OLD_TARGET_SYMBOL =		    "O";
    
    public static final String UTF8_NEW_TARGET_DESC =		    "New Target";
    public static final String UTF8_NEW_TARGET_SYMBOL =		    "N";
    
    public static final String UTF8_MAC_DESC =			    "Message Authentication Code (MAC)";
    public static final String UTF8_MAC_SYMBOL =			    "M";
        
    public static final String UTF8_ATTRIB_SYMBOL =		    "A";
    public static final String UTF8_ATTRIB_DESC =		    "File Attributes";

    public static 	String UTF8_PROCESS_SYMBOL =		    "?"; // Dynamically changes
    public static 	String UTF8_PROCESS_DESC =		    "Process";

    public static final String UTF8_CREATE_SYMBOL =		    "+";
    public static final String UTF8_CREATE_DESC =		    "Create";

    public static final String UTF8_READ_SYMBOL =		    "R";
    public static final String UTF8_READ_DESC =			    "Read";

    public static final String UTF8_WRITE_SYMBOL =		    "W";
    public static final String UTF8_WRITE_DESC =		    "Write";

    public static final String UTF8_ENCRYPT_SYMBOL =		    "E";
    public static final String UTF8_ENCRYPT_DESC =		    "Encrypt";

    public static final String UTF8_DECRYPT_SYMBOL =		    "D";
    public static final String UTF8_DECRYPT_DESC =		    "Decrypt";

    public static final String UTF8_XOR_NOMAC_SYMBOL =		    "X";
    public static final String UTF8_XOR_NOMAC_DESC =		    "XOR";

    public static final String UTF8_SHRED_SYMBOL =		    "S";
    public static final String UTF8_SHRED_DESC =		    "Shred";

    public static final String UTF8_CLONE_SYMBOL =		    "C";
    public static final String UTF8_CLONE_DESC =		    "Clone";

    public static final String UTF8_DELETE_SYMBOL =		    "-";
    public static final String UTF8_DELETE_DESC =		    "Delete";

    public static final String UTF8_PAUSE_SYMBOL =		    "PS";
    public static final String UTF8_UNPAUSE_SYMBOL =		    "UP";
    public static final String UTF8_STOP_SYMBOL =		    "ST";

    public static final String UTF8_PAUSE_DESC =		    "Pause";
    public static final String UTF8_UNPAUSE_DESC =		    "UnPause";
    public static final String UTF8_STOP_DESC =			    "Stop";

    public static boolean disabledMAC = false; // Disable Message Authentication Mode DANGEROUS
    
    private static String pwd = ""; // abc = 012
    private static int pwdPos = 0;
    
    private static byte[] pwdBytes; // abc = 012
    private static int pwdBytesPos = 0;
    
    public static final String HASH_ALGORITHM_NAME =		    "SHA-256"; // SHA-1 SHA-256 SHA-384 SHA-512
    private static String printString;

    public static final double IO_THROUGHPUT_CEILING_DEFAULT =	    10d; // (MiB/S) Dynamic 100% ceiling
    public static double io_Throughput_Ceiling =		    IO_THROUGHPUT_CEILING_DEFAULT;
    private static double realtimeMiBPS;

    private long lastBytesProcessed2;
    private long throughputClock = 0L;
    private long lastThroughputClock = 0L;
    private long realtimeBytesProcessed;
    private long totalBytesProcessed;
    
    public static boolean sync = false; // false = Best performance

//  blend e 25.8MBps d 48.9MBps sync false
//  shell e 7.1MBps  d 6.7MBps sync false
//  googl e 19.1MBps d 28.8MBps sync false
//  oracl e 22.8MBps d 34.6MBps sync false
// 
//  blend e 21.9MBps d 34.6MBps sync true
//  shell e 1.6MBps d 2.0MBps sync true
//  googl e 12.5MBps d 16.7MBps sync true
//  oracl e 14.9MBps d 20.6MBps sync true

    public FinalCrypt(UI ui)
    {   
//      Set the locations of the version resources
        readTargetSourceBufferSize =	bufferSize;
        readKeySourceBufferSize =	bufferSize;
        wrteTargetDestinBufferSize =	bufferSize;        
        this.ui = ui;
//        fc = this;
    }
    
    public int getBufferSize()                                              { return bufferSize; }
    
//    public boolean getDebug()                                               { return debug; }
    public boolean getVerbose()                                             { return verbose; }
    public boolean getPrint()                                               { return print; }
    public boolean getSymlink()                                             { return symlink; }
    public boolean getTXT()                                                 { return txt; }
    public boolean getBin()                                                 { return bin; }
    public boolean getDec()                                                 { return dec; }
    public boolean getHex()                                                 { return hex; }
    public boolean getChr()                                                 { return chr; }
    public boolean getTest()                                                { return test; }
    public int getBufferSizeDefault()					    { return BUFFERSIZEDEFAULT; }
//    public ArrayList<Path> getTargetFilesPathList()                         { return targetReadFilesPathList; }
//    public Path getKeyFilePath()                                         { return keyReadFilePath; }
//    public Path getOutputFilePath()                                         { return targetDestinPath; }
    
//    public void setDebug(boolean debug)                                     { this.debug = debug; }
    public void setVerbose(boolean verbose)                                 { FinalCrypt.verbose = verbose; }
    public void setPrint(boolean print)                                     { FinalCrypt.print = print; }
    public void setSymlink(boolean symlink)                                 { this.symlink = symlink; }
    public void setTXT(boolean txt)                                         { this.txt = txt; }
    public void setBin(boolean bin)                                         { FinalCrypt.bin = bin; }
    public void setDec(boolean dec)                                         { FinalCrypt.dec = dec; }
    public void setHex(boolean hex)                                         { FinalCrypt.hex = hex; }
    public void setChr(boolean chr)                                         { FinalCrypt.chr = chr; }
    public void setTest(boolean test)                                       { this.test = test; }
    public void setBufferSize(int bufferSize)                               
    {
        this.bufferSize = bufferSize;
        readTargetSourceBufferSize = this.bufferSize; 
        readKeySourceBufferSize = this.bufferSize; 
        wrteTargetDestinBufferSize = this.bufferSize;
    }

    public static EnumSet<StandardOpenOption> getEnumSet(EnumSet<StandardOpenOption> enumSet) { if (sync) { enumSet.add(StandardOpenOption.SYNC); } return enumSet; }
    
    public void encryptSelection
    (
	    FCPathList<FCPath> targetSourceFCPathList
	    , FCPathList<FCPath> filteredTargetSourceFCPathList // encryptableList / decryptableList
	    , FCPath keySourceFCPath
	    , boolean encryptMode
	    , String pwdParam
	    , byte[] pwdBytesParam
	    , boolean open // Opens targets after finishing
    )// throws InterruptedException
    {
	io_Throughput_Ceiling = IO_THROUGHPUT_CEILING_DEFAULT;
	
	if (pwdParam.length() > 0)
	{
	    pwd = pwdParam;
	    pwdBytes = pwdBytesParam;
	}
	else
	{
	    pwd = "";
	    pwdBytes = new byte[0];
	}
	
	startCalendar = Calendar.getInstance(Locale.ROOT);
	
        Stats allDataStats = new Stats(); allDataStats.reset();
        
        Stat wrteKeyStat = new Stat(); wrteKeyStat.reset();
        Stat readTargetSourceStat = new Stat(); readTargetSourceStat.reset();
//        Stat readKeySourceStat = new Stat(); readKeySourceStat.reset();
//        Stat wrteTargetDestinStat = new Stat(); wrteTargetDestinStat.reset();
//        Stat readTargetDestinStat = new Stat(); readTargetDestinStat.reset();
        Stat wrteTargetSourceStat = new Stat(); wrteTargetSourceStat.reset();
        
        stopPending = false;
        pausing = false;
	processRunning = true;

        // Get TOTALS
        allDataStats.setFilesTotal(filteredTargetSourceFCPathList.encryptableFiles + filteredTargetSourceFCPathList.decryptableFiles);
        allDataStats.setAllDataBytesTotal(filteredTargetSourceFCPathList.encryptableFilesSize + filteredTargetSourceFCPathList.decryptableFilesSize + filteredTargetSourceFCPathList.writeAutoKeyFilesSize);
	String modeDesc = "";
	if (test) { modeDesc = "test "; }
	if (encryptMode)
	{
	    if ( ! disabledMAC ) { modeDesc += "encrypting"; } else { modeDesc += "encrypting (legacy)"; }
	}
	else
	{
	    if ( ! disabledMAC ) { modeDesc += "decrypting"; } else { modeDesc += "decrypting (legacy)"; }
	}
	ui.log(allDataStats.getStartSummary(modeDesc), true, true, true, false, false);
        try { Thread.sleep(100); } catch (InterruptedException ex) {  }
        
//      Setup the Progress TIMER & TASK
        updateProgressTimerTask = new TimerTask()
	{
	    private long filesBytesTotal;

	    @Override public void run()
	    {
		// File
		long fileBytesProcessed =	( (readTargetSourceStat.getFileBytesProcessed()) + (wrteTargetSourceStat.getFileBytesProcessed()) + (wrteKeyStat.getFileBytesProcessed() * 1));
		double fileBytesTotalPercent =	( (readTargetSourceStat.getFileBytesTotal()) / 100.0 );
		int fileBytesPercentage =	(int)(fileBytesProcessed / fileBytesTotalPercent); // 600 / 10 = 60 - 600 * (10*0.01)

		// Files
		long filesBytesProcessed =	(allDataStats.getFilesBytesProcessed());
		double filesBytesPercent =	((allDataStats.getFilesBytesTotal() ) / 100.0);
		int filesBytesPercentage =	(int)(filesBytesProcessed / filesBytesPercent);

		processProgressCalendar =	Calendar.getInstance(Locale.ROOT);
		filesBytesTotal =		allDataStats.getFilesBytesTotal();
		fileBytesProcessed =		allDataStats.getFileBytesProcessed();
		filesBytesPerMilliSecond =	filesBytesProcessed / (processProgressCalendar.getTimeInMillis() - startCalendar.getTimeInMillis());

		// System Monitor
		throughputClock = System.nanoTime();
		realtimeMiBPS = ((realtimeBytesProcessed * (1000000000d / (throughputClock - lastThroughputClock)))/(1024d*1024d)); // ui.test("FC BPS: " + realtimeMiBPS + "\r\n");
		if ( realtimeMiBPS > io_Throughput_Ceiling ) { io_Throughput_Ceiling = realtimeMiBPS; }
		lastThroughputClock = throughputClock; realtimeBytesProcessed = 0; // allDataStats.getFilesBytesProcessed()
		ui.processProgress( fileBytesPercentage, filesBytesPercentage, filesBytesTotal, allDataStats.getFilesBytesProcessed(), realtimeMiBPS );

	    }
	}; updateProgressTaskTimer = new java.util.Timer(); updateProgressTaskTimer.schedule(updateProgressTimerTask, 100L, UPDATE_PROGRESS_TIMERTASK_PERIOD);


//      Start Files Encryption Clock
        allDataStats.setAllDataStartNanoTime();
        
        // Encrypt Files loop
	
	encryptTargetloop: for (Iterator it = filteredTargetSourceFCPathList.iterator(); it.hasNext();)
	{	    
	    pwdPos = 0;
	    pwdBytesPos = 0;
	    totalBytesProcessed = 0;
	    MessageDigest srcMessageDigest = null; try { srcMessageDigest = MessageDigest.getInstance(FinalCrypt.HASH_ALGORITHM_NAME); } catch (NoSuchAlgorithmException ex) { ui.log("Error: NoSuchAlgorithmException: MessageDigest.getInstance(\" "+ FinalCrypt.HASH_ALGORITHM_NAME + "\")\r\n", false, true, true, true, false);}
	    MessageDigest dstMessageDigest = null; try { dstMessageDigest = MessageDigest.getInstance(FinalCrypt.HASH_ALGORITHM_NAME); } catch (NoSuchAlgorithmException ex) { ui.log("Error: NoSuchAlgorithmException: MessageDigest.getInstance(\" "+ FinalCrypt.HASH_ALGORITHM_NAME + "\")\r\n", false, true, true, true, false);}
	    
	    FCPath newTargetSourceFCPath = (FCPath) it.next();
	    if (( ! disabledMAC) && (encryptMode)) { newTargetSourceFCPath.macVersion = FCPath.defaultMACVersion; } // Impacts encryptByteNoPass() key 0byte negate MAC <= V2 // Just for EncryptMode

	    FCPath oldTargetSourceFCPath = newTargetSourceFCPath.clone(newTargetSourceFCPath);	    
	    
	    Path targetDestinPath = null;
	    String fileStatusLine = "";
            if (stopPending) { targetSourceEnded = true; break; }

	    
//	    Just make sure file and key aren't the same
//	    if (Files.exists(autoKeyPath, LinkOption.NOFOLLOW_LINKS))
	    FCPath dynamicKeyFCPath = keySourceFCPath.clone(keySourceFCPath);
	    Path autoKeyPath = Paths.get(keySourceFCPath.path.toAbsolutePath().toString(), newTargetSourceFCPath.path.toAbsolutePath().toString().replace(":", ""));
	    
	    if	(
			(( keySourceFCPath.type != FCPath.DIRECTORY) && (newTargetSourceFCPath.path.compareTo(keySourceFCPath.path) != 0))
		    ||	(( keySourceFCPath.type == FCPath.DIRECTORY) && (newTargetSourceFCPath.path.compareTo(autoKeyPath) != 0))
		)
	    
//	    if ((oldTargetSourceFCPath.path.compareTo(keySourceFCPath.path) != 0) && (newTargetSourceFCPath.path.compareTo(keySourceFCPath.path) != 0))
	    {
//		Determine extension ===========================================================================================================================================================================
		
		String bit_extension =	    ".bit";
		int lastDotPos =    newTargetSourceFCPath.path.getFileName().toString().lastIndexOf('.'); // -1 no extension
		int lastPos =	    newTargetSourceFCPath.path.getFileName().toString().length();
		String extension =  ""; if (lastDotPos != -1) { extension = newTargetSourceFCPath.path.getFileName().toString().substring(lastDotPos, lastPos); } else { extension = ""; }

//		Set new name of target destination

		if ( ! disabledMAC)
		{
		    if	(encryptMode)				{ UTF8_PROCESS_SYMBOL = UTF8_ENCRYPT_SYMBOL; targetDestinPath = newTargetSourceFCPath.path.resolveSibling(newTargetSourceFCPath.path.getFileName().toString() + bit_extension); }
		    else // (decryptmode)
		    {
			UTF8_PROCESS_SYMBOL = UTF8_DECRYPT_SYMBOL;
			if (extension.equals(bit_extension))	{ targetDestinPath = Paths.get(newTargetSourceFCPath.path.toAbsolutePath().toString().substring(0, newTargetSourceFCPath.path.toAbsolutePath().toString().lastIndexOf('.'))); }
			else					{ targetDestinPath = newTargetSourceFCPath.path.resolveSibling(newTargetSourceFCPath.path.getFileName().toString() + bit_extension); }
		    }
		}
		else // Disable Message Authentication Mode
		{
		    UTF8_PROCESS_SYMBOL = UTF8_XOR_NOMAC_SYMBOL;
		    if (extension.equals(bit_extension))	{ targetDestinPath = Paths.get(newTargetSourceFCPath.path.toAbsolutePath().toString().substring(0, newTargetSourceFCPath.path.toAbsolutePath().toString().lastIndexOf('.'))); }
		    else					{ targetDestinPath = newTargetSourceFCPath.path.resolveSibling(newTargetSourceFCPath.path.getFileName().toString() + bit_extension); }
		}
//		ui.log("newTargetSourceFCPath: " + newTargetSourceFCPath.path.toAbsolutePath().toString() + "\r\n", true, true, true, false, false);
//		ui.log("targetDestinPath:      " + targetDestinPath.toAbsolutePath().toString() + "\r\n", true, true, true, false, false);
		
//		End of enxtension codeblock ===================================================================================================================================================================

//		At the start of the encryption process
		try { Files.deleteIfExists(targetDestinPath); } catch (IOException ex) { ui.log("Error: Files.deleteIfExists(targetDestinPath): " + ex.getMessage() + "\r\n", true, true, true, true, false); }

		// Get and set the stats
//		    allDataStats.setFileBytesTotal(targetSourceSize);
		allDataStats.setFileBytesTotal(newTargetSourceFCPath.size + dynamicKeyFCPath.size);
		wrteKeyStat.setFileBytesProcessed(0);			    wrteKeyStat.setFileBytesTotal(newTargetSourceFCPath.size + FCPath.MAC_SIZE);
		readTargetSourceStat.setFileBytesProcessed(0);		    readTargetSourceStat.setFileBytesTotal(newTargetSourceFCPath.size);
//                        readKeySourceStat.setFileBytesProcessed(0);	    readKeySourceStat.setFileBytesTotal(filesize);
//                        wrteTargetDestinStat.setFileBytesProcessed(0);    wrteTargetDestinStat.setFileBytesTotal(filesize);
//                        readTargetDestinStat.setFileBytesProcessed(0);    readTargetDestinStat.setFileBytesTotal(filesize);
		wrteTargetSourceStat.setFileBytesProcessed(0);		    wrteTargetSourceStat.setFileBytesTotal(newTargetSourceFCPath.size);
		// Prints printByte Header ones
		if ( print )
		{		    
		    printString = "\r\n";
		    printString += " -----------------------------------------------------------\r\n";
		    printString += "|       Input       |         Key       |      Output       |\r\n";
		    printString += "|-------------------|-------------------|-------------------|\r\n";
		    printString += "| bin      hx dec c | bin      hx dec c | bin      hx dec c |\r\n";
		    printString += "|-------------------|-------------------|-------------------|\r\n";
		}
//___________________________________________________________________________________________________________________________________________________________
//
//			Testing FinalCrypt Token
//			🔒   Encrypt
//			🔓   Decrypt	    (Key Authenticated)
//			🔓!  Decrypt Legacy  (Key can't be checked! No Token present in old format)
//			⛔   Decrypt Abort   (Key Failed)
//___________________________________________________________________________________________________________________________________________________________

// ==================================================================================================================================================================================

		if (keySourceFCPath.type == FCPath.DIRECTORY)
		{
		    setBufferSize(BUFFERSIZEDEFAULT);
		    if ( ( newTargetSourceFCPath.size + FCPath.MAC_SIZE ) < bufferSize ) { setBufferSize((int)( newTargetSourceFCPath.size + FCPath.MAC_SIZE )); }
		}
		else
		{
		    if ( keySourceFCPath.size < bufferSize ) { setBufferSize((int)keySourceFCPath.size); }
		}
		
		ui.log(UTF8_PROCESS_SYMBOL + UTF8_NEW_TARGET_SYMBOL + " \"" + targetDestinPath.toAbsolutePath().toString() + "\" " + Validate.getHumanSize(newTargetSourceFCPath.size, 1,"Bytes") + " ", true, false, false, false, false);
		ui.log(UTF8_PROCESS_SYMBOL + UTF8_NEW_TARGET_SYMBOL + " \"" + targetDestinPath.toAbsolutePath().toString() + "\" " + Validate.getHumanSize(newTargetSourceFCPath.size, 1,"Bytes") + " ", false, true, true, false, false);

		// =================================================================================================================================================================
		// Auto Key Mode
		// =================================================================================================================================================================

		if (! test)
		{
		    if ( (keySourceFCPath.type == FCPath.DIRECTORY) && (keySourceFCPath.isValidKeyDir) ) // Detects Auto Key Mode
		    {		    
			if (encryptMode) // During encryption keys have to be created when non existing
			{
//			    ui.test("\r\n Encrypting keySourceFCPath: " + keySourceFCPath.path.toAbsolutePath().toString() + "\r\n");
			    autoKeyPath = Paths.get(keySourceFCPath.path.toAbsolutePath().toString(), targetDestinPath.toAbsolutePath().toString().replace(":", ""));
//							getFCPath(UI ui, String caller,   Path path, boolean isKey, Path keyPath, boolean disabledMAC, boolean report)
			    dynamicKeyFCPath = Validate.getFCPath(   ui,	    "", autoKeyPath,          true,  autoKeyPath,	  disabledMAC,           true);
//			    ui.test("\r\n autoKeyPath: " + autoKeyPath + "\r\n");
//			    ui.test("\r\n dynamicKeyFCPath: " + dynamicKeyFCPath.getString() + "\r\n");

			    if (targetDestinPath.compareTo(dynamicKeyFCPath.path) == 0)		    { ui.log("Error: Aborting: " + targetDestinPath.toAbsolutePath().toString() +		" matches: " + dynamicKeyFCPath.path.toAbsolutePath().toString() + " (is key!)\r\n", true, true, true, true, false); break; }
			    if (newTargetSourceFCPath.path.compareTo(dynamicKeyFCPath.path) == 0)   { ui.log("Error: Aborting: " + newTargetSourceFCPath.path.toAbsolutePath().toString() +	" matches: " + dynamicKeyFCPath.path.toAbsolutePath().toString() + " (is key!)\r\n", true, true, true, true, false); break; }

			    // Create non existing directory structure for key
			    try { Files.createDirectories(autoKeyPath.getParent()); } catch (IOException ex) { ui.log("Error: Files.createDirectories(..): " + ex.getMessage() + "\r\n", true, true, true, true, false); break; }

//			    if ( dynamicKeyFCPath.size < ( newTargetSourceFCPath.size + FCPath.MAC_SIZE ) ) // Only append extra key data when key is smaller than data file
			    { // Allways write a new full size key
				if ( ( newTargetSourceFCPath.size + FCPath.MAC_SIZE ) < bufferSize) { bufferSize =  (int) ( newTargetSourceFCPath.size + FCPath.MAC_SIZE ); }

				boolean inputEnded = false;
				long writeKeyFileChannelPosition = 0L;
				long writeKeyFileChannelTransfered = 0L;
				Long totalTranfered = 0L;
				Long remainder = 0L;

				ByteBuffer  randomBuffer =	    ByteBuffer.allocate(bufferSize); randomBuffer.clear();

//				ui.log(UTF8_CREATE_SYMBOL + UTF8_KEY_SYMBOL + " \"" + dynamicKeyFCPath.path.toAbsolutePath() + "\" ", true, false, false, false, false);
				ui.log(UTF8_CREATE_SYMBOL + UTF8_KEY_SYMBOL + " \"" + targetDestinPath.toAbsolutePath().toString() + "\" ", true, false, false, false, false);
				ui.log(UTF8_CREATE_SYMBOL + UTF8_KEY_SYMBOL, false, true, true, false, false);

				write1loop: while ( (totalTranfered < ( newTargetSourceFCPath.size + FCPath.MAC_SIZE )) && (! inputEnded ))
				{
				    while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
				    
				    if (stopPending)
				    {
					if ( (keySourceFCPath.type == FCPath.DIRECTORY) && (keySourceFCPath.isValidKeyDir) ) // Detects Auto Key Mode
					{
					    boolean deleted = false;
					    try { deleted = Files.deleteIfExists(dynamicKeyFCPath.path); } catch (IOException ex) { ui.log("Error: Files.deleteIfExists(dynamicKeyFCPath): " + ex.getMessage() + "\r\n", true, true, true, true, false); }
					    if ( deleted )
					    {
						ui.log(" " + UTF8_STOP_SYMBOL + " " + UTF8_DELETE_SYMBOL + UTF8_KEY_SYMBOL + UTF8_SUCCEEDED_SYMBOL + " ", false, true, true, false, false);
					    }
					    else
					    {
						ui.log(" " + UTF8_STOP_SYMBOL + " " + UTF8_DELETE_SYMBOL + UTF8_KEY_SYMBOL + UTF8_UNSUCCEEDED_SYMBOL + " ", false, true, true, false, false);
					    }
					}
					targetSourceEnded = true;
					ui.log("\r\n", true, true, true, false, false);
					filesBytesPerMilliSecond = 0d;
					break encryptTargetloop;
				    }
				    
				    remainder = (( newTargetSourceFCPath.size + FCPath.MAC_SIZE ) - totalTranfered);

				    if ( remainder >= bufferSize )				{ randomBuffer = ByteBuffer.allocate(bufferSize); randomBuffer.clear(); }
				    else if (( remainder > 0 ) && ( remainder < bufferSize ))	{ randomBuffer = ByteBuffer.allocate(remainder.intValue()); randomBuffer.clear(); }
				    else							{ inputEnded = true; }

				    // Randomize raw key or write raw key straight to partition
				    //			getFCRandomBuffer(UI ui,			int size, boolean extraSeed, boolean encrypt, boolean print)
				    randomBuffer = TRNG.getFCRandomBuffer(   ui, randomBuffer.capacity(),	       true,		true,	      print);

				    // Write Device (randomBuffer3 became randomBuffer1)
				    wrteKeyStat.setFileStartEpoch();
				    try (final SeekableByteChannel writeKeyFileChannel = Files.newByteChannel(dynamicKeyFCPath.path, getEnumSet(EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE))))
				    {
					writeKeyFileChannel.position(writeKeyFileChannelPosition);
					writeKeyFileChannelTransfered = writeKeyFileChannel.write(randomBuffer); randomBuffer.rewind(); realtimeBytesProcessed += writeKeyFileChannelTransfered;
					totalTranfered += writeKeyFileChannelTransfered; 
					// System.out.println("tot: " + filesizeInBytes + " trans: " + totalTranfered + " remain: " + remainder + " p: " + (double)totalTranfered / filesizeInBytes + "\r\n");
					writeKeyFileChannelPosition += writeKeyFileChannelTransfered;
					writeKeyFileChannel.close();
					
					wrteKeyStat.setFileEndEpoch(); wrteKeyStat.clock();
					wrteKeyStat.addFileBytesProcessed(writeKeyFileChannelTransfered); // /2
					allDataStats.addAllDataBytesProcessed("wr key", writeKeyFileChannelTransfered); // 2
				    } catch (IOException ex) { ui.log("Error: Files.newByteChannel(dynamicKeyFCPath.path: " + ex.getMessage() + "\r\n", true, true, true, true, false); inputEnded = true; break; }
				    randomBuffer.clear();
				}
				writeKeyFileChannelPosition = 0;                
				writeKeyFileChannelTransfered = 0;                
				wrteKeyStat.setFileBytesProcessed(0);
				inputEnded = false;
				
//				ui.log(UTF8_CLONE_SYMBOL + " \"" + newTargetSourceFCPath.path.toAbsolutePath() + "\" ", true, false, false, false, false);
				ui.log(UTF8_SUCCEEDED_SYMBOL + " ", false, true, true, false, false);
			    }
			    
			}
			else // Decryptmode
			{
//			    ui.test("\r\n Decrypting keySourceFCPath: " + keySourceFCPath.path.toAbsolutePath().toString() + "\r\n");
			    autoKeyPath = Paths.get(keySourceFCPath.path.toAbsolutePath().toString(), newTargetSourceFCPath.path.toAbsolutePath().toString().replace(":", ""));
						    //  getFCPath(UI ui, String caller,  Path path, boolean isKey, Path keyPath, boolean disabledMAC, boolean report)
			    dynamicKeyFCPath = Validate.getFCPath(   ui,	    "",autoKeyPath,          true,  autoKeyPath,	 disabledMAC,          true);
//			    ui.test("\r\n autoKeyPath: " + autoKeyPath + "\r\n");
//			    ui.test("\r\n dynamicKeyFCPath: " + dynamicKeyFCPath.getString() + "\r\n");

			    if (targetDestinPath.compareTo(dynamicKeyFCPath.path) == 0)		    { ui.log("Error: Aborting: " + targetDestinPath.toAbsolutePath().toString() +		" matches: " + dynamicKeyFCPath.path.toAbsolutePath().toString() + " (is key!)\r\n", true, true, true, true, false); break; }
			    if (newTargetSourceFCPath.path.compareTo(dynamicKeyFCPath.path) == 0)   { ui.log("Error: Aborting: " + newTargetSourceFCPath.path.toAbsolutePath().toString() +	" matches: " + dynamicKeyFCPath.path.toAbsolutePath().toString() + " (is key!)\r\n", true, true, true, true, false); break; }
			}
		    }
		    else // Manual Key Mode
		    {
			dynamicKeyFCPath = keySourceFCPath.clone(keySourceFCPath); // Makes sure that the validKey KeySourceFCPath is copied to dynamicKeyFCPath
		    }
		}

		// =================================================================================================================================================================
		// Auto Key Mode End
		// =================================================================================================================================================================

		long readTargetSourceChannelPosition = 0;	long writeTargetDestChannelTransfered = 0;

		if (! disabledMAC) // Be carefull: TRUE value is highly dangerous
		{
		    while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
		    if (encryptMode) // During encryption the MAC has to be created
		    {
			if ( newTargetSourceFCPath.isDecrypted) // Target has NO Token, Decrypted
			{
			    if (newTargetSourceFCPath.isEncryptable) // TargetSource is (Encryptable)
			    {				
				if ( ! test )
				{
				    ui.log(UTF8_WRITE_SYMBOL + UTF8_MAC_SYMBOL + newTargetSourceFCPath.defaultMACVersion, false, true, true, false, false);
				    // Add MAC to targetDestinPath
				    ByteBuffer targetDestinMACBuffer = ByteBuffer.allocate((FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length() * 2)); targetDestinMACBuffer.clear();			
				    try (final SeekableByteChannel writeTargetDestinChannel = Files.newByteChannel(targetDestinPath, getEnumSet(EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.APPEND))))
				    {
					targetDestinMACBuffer = createTargetDestinMessageAuthenticationCode(dynamicKeyFCPath.path, newTargetSourceFCPath.defaultMACVersion);
					writeTargetDestChannelTransfered = writeTargetDestinChannel.write(targetDestinMACBuffer); targetDestinMACBuffer.flip();
					writeTargetDestinChannel.close();
					dstMessageDigest.update(targetDestinMACBuffer); // Build up checksum

					// wrteTargetDestinStat.addFileBytesProcessed(writeTargetDestChannelTransfered);
				    } catch (IOException ex) { ui.log("\r\nError: Add Token writeTargetDestinChannel Abort Encrypting: " + targetDestinPath.toString() + " " + ex.getMessage() + "\r\n", true, true, true, true, false); continue encryptTargetloop; }
				    ui.log(UTF8_SUCCEEDED_SYMBOL + " ", false, true, true, false, false);
				}
			    }
			    else // Decrypted but NOT Encryptable (should not be in the list anyway)
			    {
				ui.log(UTF8_UNENCRYPTABLE_SYMBOL + " \"" + newTargetSourceFCPath.toString() + "\" - Not Encryptable!\r\n", true, true, true, true, false);
				continue encryptTargetloop;
			    }
			}
		    }
		    else // decrypt (Just to buildup checksum and decrypt skipping the MAC in the encrypted file )
		    {
			if (newTargetSourceFCPath.isEncrypted) // Target has MAC, Decrypt New Format
			{
			    if (newTargetSourceFCPath.isDecryptable) // TargetSource Has Authenticated MAC (Decryptable)
			    {
				if (! test)
				{
				    ui.log(UTF8_READ_SYMBOL + UTF8_MAC_SYMBOL + newTargetSourceFCPath.macVersion, false, true, true, false, false);
				    ByteBuffer targetSourceBuffer = ByteBuffer.allocate(((FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length() * 2))); targetSourceBuffer.clear();
				    try (final SeekableByteChannel readTargetSourceChannel = Files.newByteChannel(newTargetSourceFCPath.path, getEnumSet(EnumSet.of(StandardOpenOption.READ))))
				    {
					// Fill up inputFileBuffer
					readTargetSourceChannel.read(targetSourceBuffer); targetSourceBuffer.flip();
					readTargetSourceChannel.close();
					srcMessageDigest.update(targetSourceBuffer); // Build up checksum
				    } catch (IOException ex) { ui.log(" Error: IOException: Files.newByteChannel(newTargetSourceFCPath): " + ex.getMessage() + "\r\n", true, true, true, true, false); continue encryptTargetloop; }
				    readTargetSourceChannelPosition = (FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length() * 2); // Decrypt skipping MAC bytes at beginning
				    ui.log(UTF8_SUCCEEDED_SYMBOL + " ", false, true, true, false, false);
				}
			    }
			    else
			    {
				ui.log(UTF8_UNDECRYPTABLE_SYMBOL + " \"" + newTargetSourceFCPath.toString() + "\" - Key Failed : " + keySourceFCPath.toString() + "\r\n", true, true, true, true, false);
				continue;
			    }
			}
		    }
		}
		else
		{
//		    fileStatusLine =    UTF8_PROCESS_SYMBOL + " \"" + targetDestinPath.toAbsolutePath().toString() + "\" " + UTF8_PROCESS_SYMBOL;
//		    ui.log(fileStatusLine, true, true, true, false, false);
		}
		
		
//___________________________________________________________________________________________________________________________________________________________
//
//			Encryptor I/O Block

		pwdPos = 0;
		pwdBytesPos = 0;

		ByteBuffer targetSourceBuffer = ByteBuffer.allocate(readTargetSourceBufferSize); targetSourceBuffer.clear();
		ByteBuffer keySourceBuffer = ByteBuffer.allocate(readTargetSourceBufferSize); keySourceBuffer.clear();
		ByteBuffer targetDestinBuffer = ByteBuffer.allocate(readTargetSourceBufferSize); targetDestinBuffer.clear();

		targetSourceEnded = false;
							    long    readTargetSourceChannelTransfered =  0;

		// MAC3 prevents reuse of first 70 bytes of key on both Binary MAC and 1st 70 bytes of datafile on encrypt
		long readKeySourceChannelPosition = 0;
		
		// Line 317 sets unencrypted files to be treated as MACv3
		if ( (encryptMode) || ( (! encryptMode) && ( newTargetSourceFCPath.macVersion >= 3 ) )) // 140 bytes key offset since MAC V3 to prevent partial key reusage
		{
		    readKeySourceChannelPosition = ((FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length()) * 2); // Effectively starts at offset byte 140 of keyfile
		} else { readKeySourceChannelPosition = 0; }
		
		long readKeySourceChannelTransfered =  0;                
		
		long writeTargetDestChannelPosition = 0;	    writeTargetDestChannelTransfered =   0;
		long readTargetDestChannelPosition = 0;	    long    readTargetDestChannelTransfered =    0;
		long writeTargetSourceChannelPosition = 0;  long    writeTargetSourceChannelTransfered = 0;


		// Open and close files after every bufferrun. Interrupted file I/O works much faster than uninterrupted I/O encryption
		
		ui.log(UTF8_PROCESS_SYMBOL + UTF8_NEW_TARGET_SYMBOL + " \"" + targetDestinPath.toAbsolutePath().toString() + "\" ", true, false, false, false, false);
		ui.log(UTF8_PROCESS_SYMBOL + UTF8_NEW_TARGET_SYMBOL, false, true, true, false, false);
		
		while (( ! targetSourceEnded ) && ( ! test ))
		{
		    while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
//                  Delete broken outputFile and keep original
//		    At the encryption stage of the process
		    if (stopPending)
		    {
			boolean deleted = false;
			try { deleted = Files.deleteIfExists(targetDestinPath); } catch (IOException ex) { ui.log("Error: Files.deleteIfExists(targetDestinPath): " + ex.getMessage() + "\r\n", true, true, true, true, false); }
			if ( deleted )
			{
			    ui.log(" " + UTF8_STOP_SYMBOL + " " + UTF8_DELETE_SYMBOL + UTF8_NEW_TARGET_SYMBOL + UTF8_SUCCEEDED_SYMBOL + " ", false, true, true, false, false);
			}
			else
			{
			    ui.log(" " + UTF8_STOP_SYMBOL + " " + UTF8_DELETE_SYMBOL + UTF8_NEW_TARGET_SYMBOL + UTF8_UNSUCCEEDED_SYMBOL + " ", false, true, true, false, false);
			}
			if ((encryptMode) && (keySourceFCPath.type == FCPath.DIRECTORY) && (keySourceFCPath.isValidKeyDir) ) // Only delete key on failed encrypt, never on decryption
			{
			    deleted = false;
			    try { deleted = Files.deleteIfExists(dynamicKeyFCPath.path); } catch (IOException ex) { ui.log("Error: Files.deleteIfExists(dynamicKeyFCPath): " + ex.getMessage() + "\r\n", true, true, true, true, false); }
			    if ( deleted )
			    {
				ui.log(UTF8_DELETE_SYMBOL + UTF8_KEY_SYMBOL + UTF8_SUCCEEDED_SYMBOL + " ", false, true, true, false, false);
			    }
			    else
			    {
				ui.log(UTF8_DELETE_SYMBOL + UTF8_KEY_SYMBOL + UTF8_UNSUCCEEDED_SYMBOL + " ", false, true, true, false, false);
			    }
			}
			targetSourceEnded = true;
			ui.log("\r\n", true, true, true, false, false);
			filesBytesPerMilliSecond = 0d;
			break encryptTargetloop;
		    }

		    //open targetSourcePath
		    readTargetSourceStat.setFileStartEpoch(); // allFilesStats.setFilesStartNanoTime();
		    try (final SeekableByteChannel readTargetSourceChannel = Files.newByteChannel(newTargetSourceFCPath.path, getEnumSet(EnumSet.of(StandardOpenOption.READ))))
		    {
			// Fill up inputFileBuffer
			readTargetSourceChannel.position(readTargetSourceChannelPosition);
			readTargetSourceChannelTransfered = readTargetSourceChannel.read(targetSourceBuffer); targetSourceBuffer.flip(); readTargetSourceChannelPosition += readTargetSourceChannelTransfered;
			if (( readTargetSourceChannelTransfered == -1 ) || ( targetSourceBuffer.limit() < readTargetSourceBufferSize )) { targetSourceEnded = true; } // Buffer.limit = remainder from current position to end
			readTargetSourceChannel.close();
			srcMessageDigest.update(targetSourceBuffer); // Build up checksum
			    
			readTargetSourceStat.setFileEndEpoch(); readTargetSourceStat.clock();
			readTargetSourceStat.addFileBytesProcessed(readTargetSourceChannelTransfered / 2);
//			allDataStats.addAllDataBytesProcessed("rd src", readTargetSourceChannelTransfered / 2);
		    } catch (IOException ex) { ui.log(" Error: IOException: Files.newByteChannel(newTargetSourceFCPath) " + ex.getMessage() + "\r\n", true, true, true, true, false); continue encryptTargetloop; }
//                            ui.log("readTargetSourceChannelTransfered: " + readTargetSourceChannelTransfered + " targetSourceBuffer.limit(): " + Integer.toString(targetSourceBuffer.limit()) + "\r\n");

		    if ( readTargetSourceChannelTransfered != -1 )
		    {
//                                readKeySourceStat.setFileStartEpoch();
			try (final SeekableByteChannel readKeySourceChannel = Files.newByteChannel(dynamicKeyFCPath.path, getEnumSet(EnumSet.of(StandardOpenOption.READ))))
			{
			    // Fill up keyFileBuffer
			    readKeySourceChannel.position(readKeySourceChannelPosition);
			    readKeySourceChannelTransfered = readKeySourceChannel.read(keySourceBuffer); readKeySourceChannelPosition += readKeySourceChannelTransfered;
			    if ( readKeySourceChannelTransfered < readKeySourceBufferSize ) { readKeySourceChannelPosition = 0; readKeySourceChannel.position(0); readKeySourceChannelTransfered += readKeySourceChannel.read(keySourceBuffer); readKeySourceChannelPosition += readKeySourceChannelTransfered;}
			    keySourceBuffer.flip();
			    readKeySourceChannel.close();
//				    readKeySourceStat.setFileEndEpoch(); readKeySourceStat.clock();
//                                    readKeySourceStat.addFileBytesProcessed(readKeySourceChannelTransfered);
			} catch (IOException ex) { ui.log("Error: readKeySourceChannel = Files.newByteChannel(..) " + ex.getMessage() + "\r\n", true, true, true, true, false); continue encryptTargetloop; }
//                                ui.log("readKeyFileChannelTransfered: " + readKeySourceChannelTransfered + " keySourceBuffer.limit(): " + Integer.toString(keySourceBuffer.limit()) + "\r\n");

			// Open outputFile for writing
//                                wrteTargetDestinStat.setFileStartEpoch();
			try (final SeekableByteChannel writeTargetDestinChannel = Files.newByteChannel(targetDestinPath, getEnumSet(EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.APPEND))))
			{
			    // Encrypt inputBuffer and fill up outputBuffer
			    targetDestinBuffer = encryptBuffer(targetSourceBuffer, keySourceBuffer, newTargetSourceFCPath.macVersion, true); // last boolean = PrintEnabled
			    writeTargetDestChannelTransfered = writeTargetDestinChannel.write(targetDestinBuffer); targetDestinBuffer.flip();
			    writeTargetDestChannelPosition += writeTargetDestChannelTransfered; realtimeBytesProcessed += writeTargetDestChannelTransfered; totalBytesProcessed += writeTargetDestChannelTransfered;
			    if (txt) { logByteBuffer("DB", targetSourceBuffer); logByteBuffer("CB", keySourceBuffer); logByteBuffer("OB", targetDestinBuffer); }
			    writeTargetDestinChannel.close();
			    dstMessageDigest.update(targetDestinBuffer); // Build up checksum
//				    wrteTargetDestinStat.setFileEndEpoch(); wrteTargetDestinStat.clock();
//                                    wrteTargetDestinStat.addFileBytesProcessed(writeTargetDestChannelTransfered);
			    allDataStats.addAllDataBytesProcessed("wr dst", writeTargetDestChannelTransfered / 2);
			} catch (IOException ex) { ui.log("Error: writeTargetDestinChannel = Files.newByteChannel(..) " + ex.getMessage() + "\r\n", true, true, true, true, false); continue encryptTargetloop; }
//                            ui.log("writeTargetDestChannelTransfered: " + writeTargetDestChannelTransfered + " targetDestinBuffer.limit(): " + Integer.toString(targetDestinBuffer.limit()) + "\r\n");
		    }
		    targetDestinBuffer.clear(); targetSourceBuffer.clear(); keySourceBuffer.clear();		    
		} // targetSourceEnded

		ui.log(UTF8_SUCCEEDED_SYMBOL + " ", false, true, true, false, false);

//    ==================================================================================================================================================================
//              Copy inputFilePath attributes to outputFilePath

		if ( ! test)
		{
    		    cloneFileAttributes(newTargetSourceFCPath.path, targetDestinPath, true, UTF8_OLD_TARGET_SYMBOL, UTF8_NEW_TARGET_SYMBOL);
		    if ((encryptMode) && (keySourceFCPath.type == FCPath.DIRECTORY) && (keySourceFCPath.isValidKeyDir) )
		    {
			cloneFileAttributes(newTargetSourceFCPath.path, dynamicKeyFCPath.path, true, UTF8_OLD_TARGET_SYMBOL, UTF8_KEY_SYMBOL);
		    }
		} // End ! dry

//    ==================================================================================================================================================================

//                      Counting encrypting and shredding for the average throughtput performance

//                      Shredding process

		long targetDestinSize = 0; double targetDiffFactor = 1;

		if ( ! test)
		{
		    ui.log(UTF8_SHRED_SYMBOL + UTF8_OLD_TARGET_SYMBOL + " \"" + newTargetSourceFCPath.path.toAbsolutePath() + "\" ", true, false, false, false, false);
		    ui.log(UTF8_SHRED_SYMBOL + UTF8_OLD_TARGET_SYMBOL, false, true, true, false, false);
		    
//				     isValidFile(UI ui, String caller,    Path path, boolean isKey, boolean device, long minSize, boolean symlink, boolean writable, boolean report)
		    if (Validate.isValidFile(   ui,            "", targetDestinPath,		false,		false,            1L,           false,            false,	    true)) // newly created targetdest file has to be tested
		    { try { targetDestinSize = Files.size(targetDestinPath); targetDiffFactor = newTargetSourceFCPath.size / targetDestinSize;} catch (IOException ex) { ui.log("Error: Files.size(targetDestinPath); " + ex.getMessage() + "\r\n", true, true, true, true, false); } } else 

		    readTargetSourceChannelPosition = 0;    readTargetSourceChannelTransfered = 0;
		    readKeySourceChannelPosition = 0;	    readKeySourceChannelTransfered = 0;

		    writeTargetDestChannelPosition = 0;

		    targetSourceBuffer = ByteBuffer.allocate(readTargetSourceBufferSize); targetSourceBuffer.clear();
		    keySourceBuffer = ByteBuffer.allocate(readKeySourceBufferSize); keySourceBuffer.clear();
		    targetDestinBuffer = ByteBuffer.allocate(wrteTargetDestinBufferSize); targetDestinBuffer.clear();

		    boolean targetDestinEnded = false;

		    shredloop: while ( ! targetDestinEnded )
		    {
			while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
			
//			Delete broken outputFile and keep original
//			At the shredding stage of the process
			if (stopPending)
			{
			    boolean deleted = false;
			    try { deleted = Files.deleteIfExists(newTargetSourceFCPath.path); } catch (IOException ex) { ui.log("Error: Files.deleteIfExists(" + newTargetSourceFCPath.path.toAbsolutePath().toString() + "): " + ex.getMessage() + "\r\n", true, true, true, true, false); }
			    if ( deleted )
			    {
				ui.log(" " + UTF8_STOP_SYMBOL + " " + UTF8_DELETE_SYMBOL + UTF8_OLD_TARGET_SYMBOL + UTF8_SUCCEEDED_SYMBOL + " ", false, true, true, false, false);
			    }
			    else
			    {
				ui.log(" " + UTF8_STOP_SYMBOL + " " + UTF8_DELETE_SYMBOL + UTF8_OLD_TARGET_SYMBOL + UTF8_UNSUCCEEDED_SYMBOL + " ", false, true, true, false, false);
			    }
			    if (( ! encryptMode) && (keySourceFCPath.type == FCPath.DIRECTORY) && (keySourceFCPath.isValidKeyDir) ) // Only delete key on succesfull decrypt, never on encryption
			    {
				deleted = false;
				try { deleted = Files.deleteIfExists(dynamicKeyFCPath.path); } catch (IOException ex) { ui.log("Error: Files.deleteIfExists(dynamicKeyFCPath): " + ex.getMessage() + "\r\n", true, true, true, true, false); }
				if ( deleted )
				{
				    ui.log(UTF8_DELETE_SYMBOL + UTF8_KEY_SYMBOL + UTF8_SUCCEEDED_SYMBOL + " ", false, true, true, false, false);
				}
				else
				{
				    ui.log(UTF8_DELETE_SYMBOL + UTF8_KEY_SYMBOL + UTF8_UNSUCCEEDED_SYMBOL + " ", false, true, true, false, false);
				}
			    }
			    targetSourceEnded = true;
//			    ui.log("\r\n", true, true, true, false, false);
			    targetDestinEnded = true;

			    byte[] srcHashBytes = srcMessageDigest.digest();
			    String srcHashString = getHexString(srcHashBytes,2); // print checksum

			    byte[] dstHashBytes = dstMessageDigest.digest();
			    String dstHashString = getHexString(dstHashBytes,2); // print checksum

			    fileStatusLine = allDataStats.getAllDataBytesProgressPercentage();
			    ui.log(HASH_ALGORITHM_NAME + ": \"" + srcHashString + "\"->\"" + dstHashString + "\" " + fileStatusLine + "\r\n", true, true, true, false, false);
			    
			    break encryptTargetloop;
			}

//			if (stopPending)    { targetDestinEnded = true; break shredloop; }

			//read outputFile
//                            readTargetDestinStat.setFileStartEpoch();
			try (final SeekableByteChannel readTargetDestinChannel = Files.newByteChannel(targetDestinPath, getEnumSet(EnumSet.of(StandardOpenOption.READ))))
			{
			    readTargetDestinChannel.position(readTargetDestChannelPosition);
			    readTargetDestChannelTransfered = readTargetDestinChannel.read(targetDestinBuffer); targetDestinBuffer.flip(); readTargetDestChannelPosition += readTargetDestChannelTransfered;
			    if (( readTargetDestChannelTransfered < 1 )) { targetDestinEnded = true; }
			    readTargetDestinChannel.close();
			} catch (IOException ex) { ui.log("\r\nError: readTargetDestinChannel = Files.newByteChannel(..) " + ex.getMessage() + "\r\n", true, true, true, true, false); continue encryptTargetloop; }
//                            ui.log("readTargetDestChannelTransfered: " + readTargetDestChannelTransfered + " targetDestinBuffer.limit(): " + Integer.toString( targetDestinBuffer.limit()) + "\r\n");

			//shred inputFile
//                            if ( readTargetDestChannelTransfered < 1 )
			if ( targetDestinBuffer.limit() > 0 )
			{
			    wrteTargetSourceStat.setFileStartEpoch();
			    try (final SeekableByteChannel writeTargetSourceChannel = Files.newByteChannel(newTargetSourceFCPath.path, getEnumSet(EnumSet.of(StandardOpenOption.WRITE))))
			    {
				// Fill up inputFileBuffer
				writeTargetSourceChannel.position(writeTargetSourceChannelPosition);
				writeTargetSourceChannelTransfered = writeTargetSourceChannel.write(targetDestinBuffer); targetSourceBuffer.flip();
				writeTargetSourceChannelPosition += writeTargetSourceChannelTransfered; realtimeBytesProcessed += writeTargetSourceChannelTransfered; totalBytesProcessed += writeTargetSourceChannelTransfered;
				if (( writeTargetSourceChannelTransfered < 1 )) { targetSourceEnded = true; }
				writeTargetSourceChannel.close();
				wrteTargetSourceStat.setFileEndEpoch(); wrteTargetSourceStat.clock();
				wrteTargetSourceStat.addFileBytesProcessed(writeTargetSourceChannelTransfered / 2);
				allDataStats.addAllDataBytesProcessed("wr src", writeTargetSourceChannelTransfered / 2);
//				    if ( targetDiffFactor < 1 )
//				    { allDataStats.addAllDataBytesProcessed("wr src", writeTargetSourceChannelTransfered * Math.abs((long)targetDiffFactor)); } else
//				    { allDataStats.addAllDataBytesProcessed("wr src", writeTargetSourceChannelTransfered / Math.abs((long)targetDiffFactor)); }

			    } catch (IOException ex) { ui.log("\r\nError: writeTargetSourceChannel = Files.newByteChannel(..) " + ex.getMessage() + "\r\n", true, true, true, true, false); continue encryptTargetloop; }
//                                ui.log("writeTargetSourceChannelTransfered: " + writeTargetSourceChannelTransfered + " targetDestinBuffer.limit(): " + Integer.toString(targetDestinBuffer.limit()) + "\r\n");
			}
			targetDestinBuffer.clear(); targetSourceBuffer.clear(); keySourceBuffer.clear();
		    }

		    ui.log(UTF8_SUCCEEDED_SYMBOL + " ", false, true, true, false, false);

//                  FILE STATUS 
		    if (verbose)
		    {
//			fileStatusLine += "- Crypt: rd(" +  readTargetSourceStat.getFileBytesThroughPut() + ") -> ";
			fileStatusLine = "- Crypt: rd(" +  readTargetSourceStat.getFileBytesThroughPut() + ") -> ";
			
//			    fileStatusLine += "rd(" +           readKeySourceStat.getFileBytesThroughPut() + ") -> ";
//			    fileStatusLine += "wr(" +           wrteTargetDestinStat.getFileBytesThroughPut() + ") ";
//			    fileStatusLine += "- Shred: rd(" +  readTargetDestinStat.getFileBytesThroughPut() + ")";

//			fileStatusLine += "wr(" +           wrteTargetSourceStat.getFileBytesThroughPut() + ") ";
			fileStatusLine = "wr(" +           wrteTargetSourceStat.getFileBytesThroughPut() + ") ";
		    }
		} // End ! dry


//		if ( print ) { ui.log(" ----------------------------------------------------------------------\r\n"); } // Tail after printheader


//              Delete the original
		if ( ! test)
		{
		    if
		    (
			    ( newTargetSourceFCPath.size != 0 ) && ( targetDestinSize != 0 )
			&&  ( Math.abs(newTargetSourceFCPath.size - targetDestinSize)  == (FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length()) * 2)
			||  ( newTargetSourceFCPath.size == targetDestinSize)
		    )
		    {
//			After the shredding stage of the process
			boolean deleted = false;
			try { deleted = Files.deleteIfExists(newTargetSourceFCPath.path); } catch (IOException ex)    { ui.log("Error: Files.deleteIfExists(" + newTargetSourceFCPath.path.toAbsolutePath().toString() + "): " + ex.getMessage() + "\r\n", true, true, true, true, false); continue encryptTargetloop; }
			if ( deleted ) { ui.log(UTF8_DELETE_SYMBOL + UTF8_OLD_TARGET_SYMBOL + UTF8_SUCCEEDED_SYMBOL + " ", false, true, true, false, false); } else { ui.log(UTF8_DELETE_SYMBOL + " ", false, true, true, false, false); }


//			Delete the key file after decryption
			if ((! encryptMode) && (keySourceFCPath.type == FCPath.DIRECTORY) && (keySourceFCPath.isValidKeyDir) )
			{
			    deleted = false;
			    try { deleted = Files.deleteIfExists(dynamicKeyFCPath.path); } catch (IOException ex)    { ui.log("Error: Files.deleteIfExists(" + dynamicKeyFCPath.path.toAbsolutePath().toString() + "): " + ex.getMessage() + "\r\n", true, true, true, true, false); continue encryptTargetloop; }
			    if ( deleted ) { ui.log(UTF8_DELETE_SYMBOL + UTF8_KEY_SYMBOL + UTF8_SUCCEEDED_SYMBOL + " ", false, true, true, false, false); } else { ui.log(UTF8_DELETE_SYMBOL + UTF8_KEY_SYMBOL + UTF8_UNSUCCEEDED_SYMBOL + " ", false, true, true, false, false); }
			}
		    }
		    
		}
		

		byte[] srcHashBytes = srcMessageDigest.digest();
		String srcHashString = getHexString(srcHashBytes,2); // print checksum

		byte[] dstHashBytes = dstMessageDigest.digest();
		String dstHashString = getHexString(dstHashBytes,2); // print checksum
		
		fileStatusLine = allDataStats.getAllDataBytesProgressPercentage();
		if (! test)
		{
		    ui.log(HASH_ALGORITHM_NAME + ": \"" + srcHashString + "\"->\"" + dstHashString + "\" " + fileStatusLine + "\r\n", true, true, true, false, false);		    
		}
		else
		{
		    ui.log(fileStatusLine + "\r\n", true, true, true, false, false);		    
		}

		if ( print )
		{
		    printString += " -----------------------------------------------------------\r\n"; // Footer
		    ui.log(printString + "\r\n", true, true, true, false, false);
		}

		allDataStats.addFilesProcessed(1);
	    } // else { ui.error(targetSourcePath.toAbsolutePath() + " ignoring:   " + keySourcePath.toAbsolutePath() + " (is key!)\r\n"); }
	    
	    
	    
//	    ===================================================================================================================================================
	    


//					     getFCPath(UI ui, String caller,	    Path path, boolean isKey,		 Path keyPath, boolean disabledMAC, boolean report)
	    newTargetSourceFCPath = Validate.getFCPath(ui,            "", targetDestinPath,		  false, dynamicKeyFCPath.path,		disabledMAC,	   verbose);
	    if ( newTargetSourceFCPath.isEncrypted ) { newTargetSourceFCPath.isNewEncrypted = true; } else { newTargetSourceFCPath.isNewDecrypted = true; }
	    targetSourceFCPathList.updateStat(oldTargetSourceFCPath, newTargetSourceFCPath); ui.fileProgress();
        } // End Encrypt Files Loop // End Encrypt Files Loop // End Encrypt Files Loop // End Encrypt Files Loop // End Encrypt Files Loop // End Encrypt Files Loop // End Encrypt Files Loop // End Encrypt Files Loop
	
	filesBytesPerMilliSecond = 0.0;
        allDataStats.setAllDataEndNanoTime(); allDataStats.clock();
        if ( stopPending ) { ui.log("\r\n", true, false, false, false, false); stopPending = false;  } // It breaks in the middle of encrypting, so the encryption summery needs to begin on a new line

//      Print the stats
        ui.log(allDataStats.getEndSummary(modeDesc), true, true, true, false, false);

        updateProgressTaskTimer.cancel(); updateProgressTaskTimer.purge();
//        updateProgressTimeline.stop();
	processRunning = false;
	ui.processFinished(filteredTargetSourceFCPathList, open);
    }
    
    synchronized public void cloneFileAttributes(Path sourcePath, Path destPath, boolean cloneModTime, String sourceSymbol, String destSymbol)
    {

/*
“basic:creationTime”	FileTime	The exact time when the file was created.
“basic:fileKey”	Object	An object that uniquely identifies a file or null if a file key is not available.
“basic:isDirectory”	Boolean	Returns true if the file is a directory.
“basic:isRegularFile”	Boolean	Returns true if a file is not a directory.
“basic:isSymbolicLink”	Boolean	Returns true if the file is considered to be a symbolic link.
“basic:isOther”	Boolean	
“basic:lastAccessTime”	FileTime	The last time when the file was accesed.
“basic:lastModifiedTime”	FileTime	The time when the file was last modified.
“basic:size”	Long	The file size.    

“dos:archive”	Boolean	Return true if a file is archive or not.
“dos:hidden”	Boolean	Returns true if the file/folder is hidden.
“dos:readonly”	Boolean	Returns true if the file/folder is read-only.
“dos:system”	Boolean	Returns true if the file/folder is system file.

“posix:permissions”	Set<PosixFilePermission>	The file permissions.
“posix:group”	GroupPrincipal	Used to determine access rights to objects in a file system

“acl:acl”	List<AclEntry>
“acl:owner”	UserPrincipal
*/

	String statusSymbol = UTF8_SUCCEEDED_SYMBOL;
	ui.log(UTF8_CLONE_SYMBOL + UTF8_ATTRIB_SYMBOL + destSymbol, false, true, true, false, false);
	
	attributeViewloop: for (String view:sourcePath.getFileSystem().supportedFileAttributeViews()) // acl basic owner user dos
	{
//                            ui.println(view);
	    if ( view.toLowerCase().equals("basic") )
	    {
		try
		{
		    BasicFileAttributes basicAttributes = null; basicAttributes = Files.readAttributes(sourcePath, BasicFileAttributes.class);
		    try
		    {
			Files.setAttribute(destPath, "basic:creationTime",        basicAttributes.creationTime());
			if (cloneModTime) { Files.setAttribute(destPath, "basic:lastModifiedTime",    basicAttributes.lastModifiedTime()); }
			Files.setAttribute(destPath, "basic:lastAccessTime",      basicAttributes.lastAccessTime());
		    }
		    catch (IOException ex) { ui.log("Error: Set Basic Attributes: " + ex.getMessage() + "\r\n", false, false, true, true, false); statusSymbol = "?"; }
		}   catch (IOException ex) { ui.log("Error: basicAttributes = Files.readAttributes(..): " + ex.getMessage() + "\r\n", false, false, true, true, false); statusSymbol = "?"; }
	    }
	    else if ( view.toLowerCase().equals("dos") )
	    {
		try
		{
		    DosFileAttributes msdosAttributes = null; msdosAttributes = Files.readAttributes(sourcePath, DosFileAttributes.class);
		    try
		    {
			if (cloneModTime) { Files.setAttribute(destPath, "basic:lastModifiedTime",    msdosAttributes.lastModifiedTime()); }
			Files.setAttribute(destPath, "dos:hidden",                msdosAttributes.isHidden());
			Files.setAttribute(destPath, "dos:system",                msdosAttributes.isSystem());
			Files.setAttribute(destPath, "dos:readonly",              msdosAttributes.isReadOnly());
			Files.setAttribute(destPath, "dos:archive",               msdosAttributes.isArchive());
		    }
		    catch (IOException ex) { ui.log("Error: Set DOS Attributes: " + ex.getMessage() + "\r\n", false, false, true, true, false); statusSymbol = "?"; }
		}   catch (IOException ex) { ui.log("Error: msdosAttributes = Files.readAttributes(..): " + ex.getMessage() + "\r\n", false, false, true, true, false); statusSymbol = "?"; }
	    }
	    else if ( view.toLowerCase().equals("posix") )
	    {
		PosixFileAttributes posixAttributes = null;
		try
		{
		    posixAttributes = Files.readAttributes(sourcePath, PosixFileAttributes.class);
		    try
		    {
			Files.setAttribute(destPath, "posix:owner",               posixAttributes.owner());
			Files.setAttribute(destPath, "posix:group",               posixAttributes.group());
			Files.setPosixFilePermissions(destPath,                   posixAttributes.permissions());
			if (cloneModTime) { Files.setLastModifiedTime(destPath,                       posixAttributes.lastModifiedTime()); }
		    }
		    catch (IOException ex) { ui.log("Error: Set POSIX Attributes: " + ex.getMessage() + "\r\n", false, false, true, true, false); statusSymbol = "?"; }
		}   catch (IOException ex) { ui.log("Error: posixAttributes = Files.readAttributes(..): " + ex.getMessage() + "\r\n", false, false, true, true, false); statusSymbol = "?"; }
	    }
	} // End attributeViewloop // End attributeViewloop
	ui.log(statusSymbol + " ", false, true, true, false, false);
    }
    
    synchronized public static String getHexString(byte[] bytes, int digits) { String returnString = ""; for (byte mybyte:bytes) { returnString += getHexString(mybyte, digits); } return returnString; }
    synchronized public static String getHexString(byte value, int digits) { return String.format("%0" + Integer.toString(digits) + "X", (value & 0xFF)).replaceAll("[^A-Za-z0-9]",""); }

    public static ByteBuffer encryptBuffer(ByteBuffer targetSourceBuffer, ByteBuffer keySourceBuffer, int macVersion, boolean printEnabled)
    {
        ByteBuffer targetDestinBuffer = ByteBuffer.allocate(keySourceBuffer.capacity()); targetDestinBuffer.clear();
	
        while (pausing)     { realtimeMiBPS = 0; try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
        byte targetDestinByte;
	for (int targetSourceBufferCount = 0; targetSourceBufferCount < targetSourceBuffer.limit(); targetSourceBufferCount++)
        {
	    byte targetSourceByte = targetSourceBuffer.get(targetSourceBufferCount);
	    byte keySourceByte = keySourceBuffer.get(targetSourceBufferCount);
	    
	    if (pwd.length() == 0) // no pwd used
	    {
		if (macVersion >= 3)	{ targetDestinByte = encryptByteNoPassV3(targetSourceByte, keySourceByte, macVersion); targetDestinBuffer.put(targetDestinByte); }    // negated 0 key bytes
		else			{ targetDestinByte = encryptByteNoPass(targetSourceByte, keySourceByte, macVersion); targetDestinBuffer.put(targetDestinByte); }  // raw 0 key bytes
	    }
	    else
	    {
		if	( macVersion == 1 ) { targetDestinByte = encryptBytePassV1(targetSourceByte, keySourceByte, macVersion); targetDestinBuffer.put(targetDestinByte); } // pwd with MAC_V1 (only at decrypt)
		else if ( macVersion == 2 ) { targetDestinByte = encryptBytePassV2(targetSourceByte, keySourceByte, macVersion); targetDestinBuffer.put(targetDestinByte); } // pwd with MAC_V2 (only at decrypt)
		else			    { targetDestinByte = encryptBytePassV3(targetSourceByte, keySourceByte, macVersion); targetDestinBuffer.put(targetDestinByte); } // pwd with MAC_V3 (default encrypt & decrypt)
	    }
		
	    
	    if ((printEnabled) && ( print )) { printString += getByteString(targetSourceByte, keySourceByte, targetDestinByte); }
	}
        targetDestinBuffer.flip();
		
	return targetDestinBuffer;
    }
    
    public static byte encryptByteNoPass(final byte targetSourceByte, byte keySourceByte, int macVersion) // Legacy support MAC <= V2
    {
	byte returnByte = 0; // Final result to return

	// Only invert / negate 0 key byte in MAC-ON Mode (leave untouched in RAW XOR Mode (MAC-OFF) mode)
	if (! disabledMAC) { if (keySourceByte == 0) { keySourceByte = (byte)(~keySourceByte & 0xFF); } } // Inverting / negate key 0 bytes (none encryption not allowed in default MAC-Mode)
	
	returnByte = (byte)(targetSourceByte ^ keySourceByte);
	return	returnByte;
    }

    public static byte encryptByteNoPassV3(final byte targetSourceByte, byte keySourceByte, int macVersion)
    {
	byte returnByte = 0; // Final result to return

	// Leave 0 key byte in tact and do not invert to 255 to fully comply to One Time Pad Rules
//	if (! disabledMAC) { if (keySourceByte == 0) { keySourceByte = (byte)(~keySourceByte & 0xFF); } } // Inverting / negate key 0 bytes (none encryption not allowed in default MAC-Mode)
	
	returnByte = (byte)(targetSourceByte ^ keySourceByte);
	return	returnByte;
    }

    public static byte encryptBytePassV1(final byte targetSourceByte, byte keySourceByte, int macVersion)
    {
	byte returnByte = 0; // Final result to return

	// Only invert / negate 0 key byte in MAC-ON Mode (leave untouched in RAW XOR Mode (MAC-OFF) mode)
	if (! disabledMAC) { if (keySourceByte == 0) { keySourceByte = (byte)(~keySourceByte & 0xFF); } } // Inverting / negate key 0 bytes (none encryption not allowed in default MAC-Mode)
	
	byte transitionalByte = (byte)(targetSourceByte ^ keySourceByte);		// transitionalByte =	databyte	    XOR keybyte
	returnByte =		(byte)(transitionalByte ^ (byte)pwd.charAt(pwdPos));	// returnByte =		transitionalByte    XOR passByte
	pwdPos++; if ( pwdPos == pwd.length() ) { pwdPos = 0; }
	
	return	returnByte;
    }

    public static byte encryptBytePassV2(final byte targetSourceByte, byte keySourceByte, int macVersion)
    {
	byte returnByte = 0; // Final result to return

	// Only invert / negate 0 key byte in MAC-ON Mode (leave untouched in RAW XOR Mode (MAC-OFF) mode)
	if (! disabledMAC) { if (keySourceByte == 0) { keySourceByte = (byte)(~keySourceByte & 0xFF); } } // Inverting / negate key 0 bytes (none encryption not allowed in default MAC-Mode)
	
	byte transitionalByte = (byte)(keySourceByte ^	    pwdBytes[pwdBytesPos]);	// transitionalByte =	keybyte		XOR sumByte
	returnByte =		(byte)(targetSourceByte ^   (transitionalByte));	// returnByte =		dataByte	XOR transitionalByte
	pwdBytesPos++; if ( pwdBytesPos == pwdBytes.length ) { pwdBytesPos = 0; }
	
	return	returnByte;
    }

    public static byte encryptBytePassV3(final byte targetSourceByte, byte keySourceByte, int macVersion)
    {
	byte returnByte = 0; // Final result to return

	// Leave 0 key byte in tact and do not invert to 255 to fully comply to One Time Pad Rules
//	if (! disabledMAC) { if (keySourceByte == 0) { keySourceByte = (byte)(~keySourceByte & 0xFF); } } // Inverting / negate key 0 bytes (none encryption not allowed in default MAC-Mode)
	
	byte transitionalByte = (byte)(targetSourceByte ^ pwdBytes[pwdBytesPos]);   // transitionalByte =   targetSourceByte XOR pwdBytes
	returnByte =		(byte)(transitionalByte ^ (keySourceByte));	    // returnByte =	    transitionalByte XOR keySourceByte
	pwdBytesPos++; if ( pwdBytesPos == pwdBytes.length ) { pwdBytesPos = 0; }
	
	return	returnByte;
    }

    public static byte encryptByteFastXOR(final byte targetSourceByte, byte keySourceByte)
    {
        byte targetDestinEncryptedByte;

        int targetDestinIgnoreBits = 0;
        int targetDestinKeyBits = 0;
        int targetDestinMergedBits = 0; // Merged Ignored & Negated bits)

	if (! disabledMAC) { if (keySourceByte == 0) { keySourceByte = (byte)(~keySourceByte & 0xFF); } } // Inverting / negate key 0 bytes (none encryption not allowed in default MAC-Mode)
//
//	The following 4 line are the encrypting heart of FinalCrypt.
//												    _______________________  _______________________   ________  ___________________________________________
//												   /------- LINE 1 --------\/------ LINE 2 ---------\ / LINE 3 \/            Encrypt            Decrypt     \
        targetDestinIgnoreBits =	targetSourceByte & ~keySourceByte;		// LINE 1 |             00000011   | ~00000011 = 11111100   | 00000010 | Data byte: 00000011 = 3   ╭─> 00000110 = 6 |
        targetDestinKeyBits =	~targetSourceByte & keySourceByte;			// LINE 2 | ~00000101 = 11111010 & |             00000101 & | 00000100 | Ciph byte: 00000101 = 5   │   00000101 = 5 |
        targetDestinMergedBits =	targetDestinIgnoreBits + targetDestinKeyBits;	// LINE 3 |	        00000010   |             00000100   | 00000110 | Encr byte: 00000110 = 6 ─╯    00000011 = 3 |
        targetDestinEncryptedByte =	(byte)(targetDestinMergedBits & 0xFF);		// Make sure only 8 bits of the 16 bit integer gets set in the byte casted encrypted byte

        if ( bin )      { logByteBinary(targetSourceByte, keySourceByte, targetDestinEncryptedByte, targetDestinIgnoreBits, targetDestinKeyBits, targetDestinMergedBits); }
        if ( dec )      { logByteDecimal(targetSourceByte, keySourceByte, targetDestinEncryptedByte, targetDestinIgnoreBits, targetDestinKeyBits, targetDestinMergedBits); }
        if ( hex )      { logByteHexaDecimal(targetSourceByte, keySourceByte, targetDestinEncryptedByte, targetDestinIgnoreBits, targetDestinKeyBits, targetDestinMergedBits); }
        if ( chr )      { logByteChar(targetSourceByte, keySourceByte, targetDestinEncryptedByte, targetDestinIgnoreBits, targetDestinKeyBits, targetDestinMergedBits); }

        return targetDestinEncryptedByte;
    }

    private ByteBuffer createTargetDestinMessageAuthenticationCode(Path keySourcePath, int macVersion) // Tested
    {
        ByteBuffer plainTextMACBuffer = ByteBuffer.allocate(FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length()); plainTextMACBuffer.clear();
        ByteBuffer keyBitMACBuffer = ByteBuffer.allocate(FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length()); keyBitMACBuffer.clear();
        ByteBuffer encryptedMACBuffer = ByteBuffer.allocate(FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length()); encryptedMACBuffer.clear();

	ByteBuffer targetDstMACBuffer = ByteBuffer.allocate(FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length() * 2); targetDstMACBuffer.clear();
	long readKeySourceChannelTransfered = 0;                

	// Create plaint text Buffer
	plainTextMACBuffer.put(FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.getBytes());
	
	// Create Key Buffer
	try (final SeekableByteChannel readKeySourceChannel = Files.newByteChannel(keySourcePath, getEnumSet(EnumSet.of(StandardOpenOption.READ))))
	{
//	    readKeySourceChannel.position(readKeySourceChannelPosition);
	    readKeySourceChannelTransfered = readKeySourceChannel.read(keyBitMACBuffer);
	    keyBitMACBuffer.flip(); readKeySourceChannel.close();
	} catch (IOException ex) { ui.log("Error: getTargetDestinMAC: readKeySourceChannel " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	
	// Create Encrypted Token Buffer
	encryptedMACBuffer = encryptBuffer(plainTextMACBuffer, keyBitMACBuffer, macVersion, false);
	
	// Create Target Destin Token Buffer
	byte[] messageAuthenticationCodeArray = new byte[(FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length() * 2)];
	for (int x = 0; x < plainTextMACBuffer.capacity(); x++) { messageAuthenticationCodeArray[x] = plainTextMACBuffer.array()[x]; }
	for (int x = 0; x < encryptedMACBuffer.capacity(); x++) { messageAuthenticationCodeArray[(FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length() + x)] = encryptedMACBuffer.array()[x]; }
	targetDstMACBuffer.put(messageAuthenticationCodeArray); targetDstMACBuffer.flip();
	
	pwdPos = 0;
	
	return targetDstMACBuffer;
    }
    
//  Recursive Deletion of PathList
    public void deleteSelection(ArrayList<Path> targetSourcePathList, FCPath keyFCPath, int function, boolean returnpathlist, String pattern, boolean negatePattern)
    {
        EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS); //follow links
//							  MySimpleFCFileVisitor(UI ui, boolean verbose,	int function, boolean symlink, boolean setFCPathlist,    Path keyPath, ArrayList<FCPath> targetFCPathList, String pattern, boolean negatePattern, boolean disabledMAC)
        MySimpleFCFileVisitor mySimpleFCFileVisitor = new MySimpleFCFileVisitor(   ui,	       verbose,	    function,         symlink,		     false,       keyFCPath,	      new FCPathList<FCPath>() ,        pattern,         negatePattern,		disabledMAC);
        for (Path path:targetSourcePathList)
        {
            try{Files.walkFileTree(path, opts, Integer.MAX_VALUE, mySimpleFCFileVisitor);} catch(IOException e) { ui.log("Error: IOException: deleteSelection() Files.walkFileTree(" + path.toAbsolutePath().toString() + "): " + e.getMessage() + "\r\n", true, true, true, true, false); }
        }
    }
    

    private static String getBinaryString(Byte myByte)		    { return String.format("%8s", Integer.toBinaryString(myByte & 0xFF)).replace(' ', '0'); }
    private static String getDecString(Byte myByte)		    { return String.format("%3d", (myByte & 0xFF)).replace(" ", "0"); }
    private static String getHexString(Byte myByte, String digits)  { return String.format("%0" + digits + "X", (myByte & 0xFF)); }
    private static String getChar(Byte myByte)			    { return String.format("%1s", (char) (myByte & 0xFF)).replaceAll("\\p{C}", "?"); }  //  (myByte & 0xFF); }
    
    public boolean getPausing()					    { return pausing; }
    public boolean getStopPending()				    { return stopPending; }
    public void setPausing(boolean val)
    {
	pausing = val;
	if (pausing)
	{
	    filesBytesPerMilliSecond = 0;
	    ui.log(" " + UTF8_PAUSE_SYMBOL, false, true, true, false, false);
	}
	else
	{
	    ui.log(" " + UTF8_UNPAUSE_SYMBOL + " ", false, true, true, false, false);
	}
    }
    public void setStopPending(boolean val)			    { stopPending = val; }
    
    public void setPwd(String pwdParam)				    { pwd = pwdParam; }
//    public static void setPwdBytes(byte[] pwdBytesParam)
    public void setPwdBytes(String pwdBytesParam)
    {
	if ( pwdBytesParam.isEmpty() )
	{
	    pwdBytes = new byte[0];
	}
	else
	{
	    MessageDigest messageDigest = null;
	    try { messageDigest = MessageDigest.getInstance(FinalCrypt.HASH_ALGORITHM_NAME); } catch (NoSuchAlgorithmException ex) { ui.log("Error: NoSuchAlgorithmException: MessageDigest.getInstance(\" "+ FinalCrypt.HASH_ALGORITHM_NAME + "\")\r\n", true, true, true, true, false); }
	    messageDigest.update(pwd.getBytes());
	    byte[] hashBytes = messageDigest.digest();
	    pwdBytes = GPT.hex2Bytes(getHexString(hashBytes,2));
	}
    }
    public static void resetPwdPos()				    { pwdPos = 0; }
    public static void resetPwdBytesPos()			    { pwdBytesPos = 0; }

    private static void logByteBuffer(String preFix, ByteBuffer byteBuffer)
    {
        System.out.println(preFix + "C: ");
        System.out.println(" " + preFix + "Z: " + byteBuffer.limit() + "\r\n");
    }

//    private static void logByte(byte dataByte, byte keyByte, byte outputByte)
    private static String getByteString(byte dataByte, byte keyByte, byte outputByte)
    {
        String datbin = getBinaryString(dataByte);
        String dathex = getHexString(dataByte, "2");
        String datdec = getDecString(dataByte);
        String datchr = getChar(dataByte);
        
        String cphbin = getBinaryString(keyByte);
        String cphhex = getHexString(keyByte, "2");
        String cphdec = getDecString(keyByte);
        String cphchr = getChar(keyByte);
        
        String outbin = getBinaryString(outputByte);
        String outhex = getHexString(outputByte, "2");
        String outdec = getDecString(outputByte);
        String outchr = getChar(outputByte);
        
//	System.out.print(datbin + " " +  dathex + " " + datdec + " " + datchr + " | ");
//        System.out.print(cphbin + " " +  cphhex + " " + cphdec + " " + cphchr + " | ");
//        System.out.print(outbin + " " +  outhex + " " + outdec + " " + outchr + " |");

	String returnString = "| ";
	returnString += datbin + " " +  dathex + " " + datdec + " " + datchr + " | ";
	returnString += cphbin + " " +  cphhex + " " + cphdec + " " + cphchr + " | ";
	returnString += outbin + " " +  outhex + " " + outdec + " " + outchr + " | \r\n";

	return returnString;
    }
    
    private static void logByteBinary(byte inputByte, byte keyByte, byte outputByte, int dum, int dnm, int dbm)
    {
        System.out.println("\r\n");
        System.out.println("Input  = " + getBinaryString(inputByte) + "\r\n");
        System.out.println("Key = " + getBinaryString(keyByte) + "\r\n");
        System.out.println("Output = " + getBinaryString(outputByte) + "\r\n");
        System.out.println("\r\n");
        System.out.println("DUM  = " + getBinaryString((byte)inputByte) + " & " + getBinaryString((byte)~keyByte) + " = " + getBinaryString((byte)dum) + "\r\n");
        System.out.println("DNM  = " + getBinaryString((byte)~inputByte) + " & " + getBinaryString((byte)keyByte) + " = " + getBinaryString((byte)dnm) + "\r\n");
        System.out.println("DBM  = " + getBinaryString((byte)dum) + " & " + getBinaryString((byte)dnm) + " = " + getBinaryString((byte)dbm) + "\r\n");
    }
    
    private static void logByteDecimal(byte dataByte, byte keyByte, byte outputByte, int dum, int dnm, int dbm)
    {
        System.out.println("\r\n");
        System.out.println("Input  = " + getDecString(dataByte) + "\r\n");
        System.out.println("Key = " + getDecString(keyByte) + "\r\n");
        System.out.println("Output = " + getDecString(outputByte) + "\r\n");
        System.out.println("\r\n");
        System.out.println("DUM  = " + getDecString((byte)dataByte) + " & " + getDecString((byte)~keyByte) + " = " + getDecString((byte)dum) + "\r\n");
        System.out.println("DNM  = " + getDecString((byte)~dataByte) + " & " + getDecString((byte)keyByte) + " = " + getDecString((byte)dnm) + "\r\n");
        System.out.println("DBM  = " + getDecString((byte)dum) + " & " + getDecString((byte)dnm) + " = " + getDecString((byte)dbm) + "\r\n");
    }
    
    private static void logByteHexaDecimal(byte dataByte, byte keyByte, byte outputByte, int dum, int dnm, int dbm)
    {
        System.out.println("\r\n");
        System.out.println("Input  = " + getHexString(dataByte,"2") + "\r\n");
        System.out.println("Key = " + getHexString(keyByte,"2") + "\r\n");
        System.out.println("Output = " + getHexString(outputByte,"2") + "\r\n");
        System.out.println("\r\n");
        System.out.println("DUM  = " + getHexString((byte)dataByte,"2") + " & " + getHexString((byte)~keyByte,"2") + " = " + getHexString((byte)dum,"2") + "\r\n");
        System.out.println("DNM  = " + getHexString((byte)~dataByte,"2") + " & " + getHexString((byte)keyByte,"2") + " = " + getHexString((byte)dnm,"2") + "\r\n");
        System.out.println("DBM  = " + getHexString((byte)dum,"2") + " & " + getHexString((byte)dnm,"2") + " = " + getHexString((byte)dbm,"2") + "\r\n");
    }
    
    private static void logByteChar(byte dataByte, byte keyByte, byte outputByte, int dum, int dnm, int dbm)
    {
        System.out.println("\r\n");
        System.out.println("Input  = " + getChar(dataByte) + "\r\n");
        System.out.println("Key = " + getChar(keyByte) + "\r\n");
        System.out.println("Output = " + getChar(outputByte) + "\r\n");
        System.out.println("\r\n");
        System.out.println("DUM  = " + getChar((byte)dataByte) + " & " + getChar((byte)~keyByte) + " = " + getChar((byte)dum) + "\r\n");
        System.out.println("DNM  = " + getChar((byte)~dataByte) + " & " + getChar((byte)keyByte) + " = " + getChar((byte)dnm) + "\r\n");
        System.out.println("DBM  = " + getChar((byte)dum) + " & " + getChar((byte)dnm) + " = " + getChar((byte)dbm) + "\r\n");
    }
    
    public ArrayList<Path> getPathList(File[] files)
    {
        // Converts from File[] to ArraayList<Path>
        ArrayList<Path> pathList = new ArrayList<>(); for (File file:files) { pathList.add(file.toPath()); }
        return pathList;
    }
    
//    public Stats getStats()                                 { return stats; }

//  Class Extends Thread
    @Override
    @SuppressWarnings("empty-statement")
    public void run()
    {
    }
}
