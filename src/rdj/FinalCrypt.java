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

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.TimerTask;

public class FinalCrypt extends Thread
{
    public static boolean verbose = false;
    private boolean debug = false, print = false, symlink = false, txt = false, bin = false, dec = false, hex = false, chr = false, dry = false;

    private final int bufferSizeDefault = (1 * 1024 * 1024); // 1MB BufferSize overall better performance
    private int bufferSize = 0; // Default 1MB
    private int readTargetSourceBufferSize;
    private int readCipherSourceBufferSize;
    private int wrteTargetDestinBufferSize;

    private int printAddressByteCounter = 0;
    private final UI ui;
    
    private TimerTask updateProgressTask;
    private java.util.Timer updateProgressTaskTimer;

    private boolean stopPending = false;
    private boolean pausing = false;
    private boolean targetSourceEnded;
//							1234567890123456789012345678901234567890123456789012345678901234567890
    private final String FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN = "FinalCrypt - File Encryption Program - Plain Text Authentication Token";


    public FinalCrypt(UI ui)
    {   
//      Set the locations of the version resources
        
        readTargetSourceBufferSize = bufferSize;
        readCipherSourceBufferSize = bufferSize;
        wrteTargetDestinBufferSize = bufferSize;        
        this.ui = ui;
//        fc = this;
    }
        
    public int getBufferSize()                                              { return bufferSize; }
    
    public boolean getDebug()                                               { return debug; }
    public boolean getVerbose()                                             { return verbose; }
    public boolean getPrint()                                               { return print; }
    public boolean getSymlink()                                             { return symlink; }
    public boolean getTXT()                                                 { return txt; }
    public boolean getBin()                                                 { return bin; }
    public boolean getDec()                                                 { return dec; }
    public boolean getHex()                                                 { return hex; }
    public boolean getChr()                                                 { return chr; }
    public boolean getDry()                                                 { return dry; }
    public int getBufferSizeDefault()					    { return bufferSizeDefault; }
//    public ArrayList<Path> getTargetFilesPathList()                         { return targetReadFilesPathList; }
//    public Path getCipherFilePath()                                         { return cipherReadFilePath; }
//    public Path getOutputFilePath()                                         { return targetDestinPath; }
    
    public void setDebug(boolean debug)                                     { this.debug = debug; }
    public void setVerbose(boolean verbose)                                 { this.verbose = verbose; }
    public void setPrint(boolean print)                                     { this.print = print; }
    public void setSymlink(boolean symlink)                                 { this.symlink = symlink; }
    public void setTXT(boolean txt)                                         { this.txt = txt; }
    public void setBin(boolean bin)                                         { this.bin = bin; }
    public void setDec(boolean dec)                                         { this.dec = dec; }
    public void setHex(boolean hex)                                         { this.hex = hex; }
    public void setChr(boolean chr)                                         { this.chr = chr; }
    public void setDry(boolean dry)                                         { this.dry = dry; }
    public void setBufferSize(int bufferSize)                               
    {
        this.bufferSize = bufferSize;
        readTargetSourceBufferSize = this.bufferSize; 
        readCipherSourceBufferSize = this.bufferSize; 
        wrteTargetDestinBufferSize = this.bufferSize;
    }
        
    public void encryptSelection(ArrayList<Path> targetSourcePathList, Path cipherSourcePath)
    {
	
        Stats allDataStats = new Stats(); allDataStats.reset();
        
        Stat readTargetSourceStat = new Stat(); readTargetSourceStat.reset();
//        Stat readCipherSourceStat = new Stat(); readCipherSourceStat.reset();
//        Stat wrteTargetDestinStat = new Stat(); wrteTargetDestinStat.reset();
//        Stat readTargetDestinStat = new Stat(); readTargetDestinStat.reset();
        Stat wrteTargetSourceStat = new Stat(); wrteTargetSourceStat.reset();
        
        stopPending = false;
        pausing = false;

        // Get TOTALS
        allDataStats.setFilesTotal(targetSourcePathList.size());
        for (Path targetSourcePath:targetSourcePathList) { try { if (! Files.isDirectory(targetSourcePath)) { allDataStats.addAllDataBytesTotal(Files.size(targetSourcePath)); }  } catch (IOException ex) { ui.error("Error: encryptFiles () filesBytesTotal += Files.size(targetSourcePath); "+ ex.getLocalizedMessage() + "\r\n"); }} 
        ui.status(allDataStats.getStartSummary(Mode.getDescription()), true);
        try { Thread.sleep(100); } catch (InterruptedException ex) {  }
        
//      Setup the Progress TIMER & TASK
        updateProgressTask = new TimerTask() { @Override public void run()
        {
//	    filesize =	130
//	    perc =	(file * 0.01) = 1.3
//	    progress 60 / 1.3
//	    progress 60 * (1 / 1.3)
	    
	    
	    
	    long fileBytesProcessed =	(readTargetSourceStat.getFileBytesProcessed() + wrteTargetSourceStat.getFileBytesProcessed());
	    double fileBytesPercent =	((readTargetSourceStat.getFileBytesTotal()) / 100.0); //  1000 / 100 = (long)10     10 > 0.1 (10*0.01)
	    int fileBytesPercentage =	(int)(fileBytesProcessed / fileBytesPercent); // 600 / 10 = 60 - 600 * (10*0.01)
	    
	    long filesBytesProcessed =	(allDataStats.getFilesBytesProcessed());
	    double filesBytesPercent =	((allDataStats.getFilesBytesTotal() ) / 100.0);
	    int filesBytesPercentage =	(int)(filesBytesProcessed / filesBytesPercent);

            ui.encryptionProgress( fileBytesPercentage, filesBytesPercentage );
	    
        }}; updateProgressTaskTimer = new java.util.Timer(); updateProgressTaskTimer.schedule(updateProgressTask, 0L, 200L);


//      Start Files Encryption Clock
        allDataStats.setAllDataStartNanoTime();
        
        // Encrypt Files loop
        encryptTargetloop: for (Path targetSourcePath:targetSourcePathList)
        {
	    Path targetDestinPath;
	    String fileStatusLine = "";
            long targetSourceSize = 0; try { targetSourceSize = Files.size(targetSourcePath); } catch (IOException ex) { ui.error("Error: Files.size(targetSourcePath); " + ex.getMessage() + "\r\n"); continue encryptTargetloop; }
            if (stopPending) { targetSourceEnded = true; break encryptTargetloop; }
//							          isValidFile(UI ui, String caller, Path targetSourcePath, boolean device, long minSize, boolean symlink, boolean writable, boolean report)
            if ((! Files.isDirectory(targetSourcePath)) && (Validate.isValidFile(ui,            "",      targetSourcePath,	    false,	     1L,	 symlink,             true,          false)))
            {
                if ((targetSourcePath.compareTo(cipherSourcePath) != 0))
                {

                    if ( ! dry ) // Real run passes this point
                    {			
                        String prefix =	    "bit";
                        String suffix =	    ".bit";
                        String extension =  "";
                        int lastDotPos =    targetSourcePath.getFileName().toString().lastIndexOf('.'); // -1 no extension
                        int lastPos =	    targetSourcePath.getFileName().toString().length();
			
                        if (lastDotPos != -1) { extension = targetSourcePath.getFileName().toString().substring(lastDotPos, lastPos); } else { extension = ""; }

                        if ( ! extension.equals(suffix))    { targetDestinPath = targetSourcePath.resolveSibling(targetSourcePath.getFileName().toString() + suffix); }		    // Add    .bit
                        else				    { targetDestinPath = targetSourcePath.resolveSibling(targetSourcePath.getFileName().toString().replace(suffix, "")); }  // Remove .bit			
			
			
                        try { Files.deleteIfExists(targetDestinPath); } catch (IOException ex) { ui.error("Error: Files.deleteIfExists(targetDestinPath): " + ex.getMessage() + "\r\n"); }

                        // Prints printByte Header ones                
                        if ( print )
                        {
                            ui.log("\r\n");
                            ui.log(" ----------------------------------------------------------------------\r\n");
                            ui.log("|          |       Input       |      Cipher       |      Output       |\r\n");
                            ui.log("| ---------|-------------------|-------------------|-------------------|\r\n");
                            ui.log("| adr      | bin      hx dec c | bin      hx dec c | bin      hx dec c |\r\n");
                            ui.log("|----------|-------------------|-------------------|-------------------|\r\n");
                        }
//___________________________________________________________________________________________________________________________________________________________
//
//			Testing FinalCrypt Token
//			🔒   Encrypt
//			🔓   Decrypt	    (Cipher Authenticated)
//			🔓!  Decrypt Legacy  (Cipher can't be checked! No Token present in old format)
//			⛔   Decrypt Abort   (Cipher Failed)

			long readTargetSourceChannelPosition = 0;	long writeTargetDestChannelTransfered = 0;
			
			if ( targetSourceHasToken(targetSourcePath) ) // Target has Token, Decrypt New Format
			{
			    if ( targetHasAuthenticatedToken(targetSourcePath, cipherSourcePath) ) // TargetSource Has Token Decrypt New Format
			    {
				fileStatusLine = "🔓 \"" + targetDestinPath.toString() + "\" ";
				ui.status("🔓 \"" + targetDestinPath.toString() + "\" ", false);
				readTargetSourceChannelPosition = (FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length() * 2); // Decrypt skipping Token bytes at beginning
			    }
			    else
			    {
				ui.status("⛔ \"" + targetSourcePath.toString() + "\" - Cipher Failed : " + cipherSourcePath.toString() + "\r\n", true);
				continue encryptTargetloop;
			    }
			}
			else // Traget has NO Token
			{
			    if ( ! targetSourcePath.getFileName().toString().endsWith(suffix) ) // Target has No Token and no ".bit" extension
			    {
				ui.status(		"🔒 \"" + targetDestinPath.toString() + "\" ", false);
				fileStatusLine =	"🔒 \"" + targetDestinPath.toString() + "\" ";

				ByteBuffer targetDestinTokenBuffer = ByteBuffer.allocate((FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length() * 2)); targetDestinTokenBuffer.clear();			
				try (final SeekableByteChannel writeTargetDestinChannel = Files.newByteChannel(targetDestinPath, EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC)))
				{
				    targetDestinTokenBuffer = createTargetDestinToken(cipherSourcePath);
				    writeTargetDestChannelTransfered = writeTargetDestinChannel.write(targetDestinTokenBuffer); targetDestinTokenBuffer.flip();
				    writeTargetDestinChannel.close();
//                                    wrteTargetDestinStat.addFileBytesProcessed(writeTargetDestChannelTransfered);
				} catch (IOException ex) { ui.error("\r\nError: Add Token writeTargetDestinChannel Abort Encrypting: " + targetDestinPath.toString() + " " + ex.getMessage() + "\r\n"); continue encryptTargetloop; }

				readTargetSourceChannelPosition = 0; // Start reading targetSource from beginning (Encrypt)
			    }
			    else  // Target has No Token, but has a ".bit" extension
			    {
				fileStatusLine = "🔓! \"" + targetDestinPath.toString()+ "\" "; // Decrypt Old Format
				ui.status("🔓! \"" + targetDestinPath.toString() + "\" ", false);
				readTargetSourceChannelPosition = 0;
			    }
			}
			
//___________________________________________________________________________________________________________________________________________________________
//
//			Encryptor I/O Block

			ByteBuffer targetSourceBuffer = ByteBuffer.allocate(readTargetSourceBufferSize); targetSourceBuffer.clear();
                        ByteBuffer cipherSourceBuffer = ByteBuffer.allocate(readTargetSourceBufferSize); cipherSourceBuffer.clear();
                        ByteBuffer targetDestinBuffer = ByteBuffer.allocate(readTargetSourceBufferSize); targetDestinBuffer.clear();
			
                        targetSourceEnded = false;
									long readTargetSourceChannelTransfered =  0;
                        long readCipherSourceChannelPosition = 0;	long readCipherSourceChannelTransfered =  0;                
                        long writeTargetDestChannelPosition = 0;	     writeTargetDestChannelTransfered =   0;
                        long readTargetDestChannelPosition = 0;		long readTargetDestChannelTransfered =    0;
                        long writeTargetSourceChannelPosition = 0;      long writeTargetSourceChannelTransfered = 0;

			// Get and set the stats
			allDataStats.setFileBytesTotal(targetSourceSize);

                        readTargetSourceStat.setFileBytesProcessed(0);	    readTargetSourceStat.setFileBytesTotal(targetSourceSize);
//                        readCipherSourceStat.setFileBytesProcessed(0);      readCipherSourceStat.setFileBytesTotal(filesize);
//                        wrteTargetDestinStat.setFileBytesProcessed(0);      wrteTargetDestinStat.setFileBytesTotal(filesize);
//                        readTargetDestinStat.setFileBytesProcessed(0);      readTargetDestinStat.setFileBytesTotal(filesize);
                        wrteTargetSourceStat.setFileBytesProcessed(0);	    wrteTargetSourceStat.setFileBytesTotal(targetSourceSize);

                        // Open and close files after every bufferrun. Interrupted file I/O works much faster than uninterrupted I/O encryption
                        while ( ! targetSourceEnded )
                        {
                            if (stopPending)
                            {
    //                          Delete broken outputFile and keep original
                                try { Files.deleteIfExists(targetDestinPath); } catch (IOException ex) { ui.error("\r\nFiles.deleteIfExists(targetDestinPath): " + ex.getMessage() + "\r\n"); }
                                targetSourceEnded = true; ui.status("\r\n", true); break encryptTargetloop;
                            }

                            //open targetSourcePath
                            readTargetSourceStat.setFileStartEpoch(); // allFilesStats.setFilesStartNanoTime();
                            try (final SeekableByteChannel readTargetSourceChannel = Files.newByteChannel(targetSourcePath, EnumSet.of(StandardOpenOption.READ)))
                            {
                                // Fill up inputFileBuffer
                                readTargetSourceChannel.position(readTargetSourceChannelPosition);
                                readTargetSourceChannelTransfered = readTargetSourceChannel.read(targetSourceBuffer); targetSourceBuffer.flip(); readTargetSourceChannelPosition += readTargetSourceChannelTransfered;
                                if (( readTargetSourceChannelTransfered == -1 ) || ( targetSourceBuffer.limit() < readTargetSourceBufferSize )) { targetSourceEnded = true; } // Buffer.limit = remainder from current position to end
                                readTargetSourceChannel.close();
				readTargetSourceStat.setFileEndEpoch(); readTargetSourceStat.clock();
                                readTargetSourceStat.addFileBytesProcessed(readTargetSourceChannelTransfered / 2);
				allDataStats.addAllDataBytesProcessed("rd src", readTargetSourceChannelTransfered / 2);
                            } catch (IOException ex) { ui.error("\r\nreadTargetSourceChannel = Files.newByteChannel(..) " + ex.getMessage() + "\r\n"); continue encryptTargetloop; }
//                            ui.log("readTargetSourceChannelTransfered: " + readTargetSourceChannelTransfered + " targetSourceBuffer.limit(): " + Integer.toString(targetSourceBuffer.limit()) + "\r\n");

                            if ( readTargetSourceChannelTransfered != -1 )
                            {
//                                readCipherSourceStat.setFileStartEpoch();
                                try (final SeekableByteChannel readCipherSourceChannel = Files.newByteChannel(cipherSourcePath, EnumSet.of(StandardOpenOption.READ)))
                                {
                                    // Fill up cipherFileBuffer
                                    readCipherSourceChannel.position(readCipherSourceChannelPosition);
                                    readCipherSourceChannelTransfered = readCipherSourceChannel.read(cipherSourceBuffer); readCipherSourceChannelPosition += readCipherSourceChannelTransfered;
                                    if ( readCipherSourceChannelTransfered < readCipherSourceBufferSize ) { readCipherSourceChannelPosition = 0; readCipherSourceChannel.position(0); readCipherSourceChannelTransfered += readCipherSourceChannel.read(cipherSourceBuffer); readCipherSourceChannelPosition += readCipherSourceChannelTransfered;}
                                    cipherSourceBuffer.flip();
                                    readCipherSourceChannel.close();
//				    readCipherSourceStat.setFileEndEpoch(); readCipherSourceStat.clock();
//                                    readCipherSourceStat.addFileBytesProcessed(readCipherSourceChannelTransfered);
                                } catch (IOException ex) { ui.error("\r\nreadCipherSourceChannel = Files.newByteChannel(..) " + ex.getMessage() + "\r\n"); continue encryptTargetloop; }
//                                ui.log("readCipherFileChannelTransfered: " + readCipherSourceChannelTransfered + " cipherSourceBuffer.limit(): " + Integer.toString(cipherSourceBuffer.limit()) + "\r\n");

                                // Open outputFile for writing
//                                wrteTargetDestinStat.setFileStartEpoch();
                                try (final SeekableByteChannel writeTargetDestinChannel = Files.newByteChannel(targetDestinPath, EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC)))
                                {
                                    // Encrypt inputBuffer and fill up outputBuffer
                                    targetDestinBuffer = encryptBuffer(targetSourceBuffer, cipherSourceBuffer);
                                    writeTargetDestChannelTransfered = writeTargetDestinChannel.write(targetDestinBuffer); targetDestinBuffer.flip(); writeTargetDestChannelPosition += writeTargetDestChannelTransfered;
                                    if (txt) { logByteBuffer("DB", targetSourceBuffer); logByteBuffer("CB", cipherSourceBuffer); logByteBuffer("OB", targetDestinBuffer); }
                                    writeTargetDestinChannel.close();
//				    wrteTargetDestinStat.setFileEndEpoch(); wrteTargetDestinStat.clock();
//                                    wrteTargetDestinStat.addFileBytesProcessed(writeTargetDestChannelTransfered);
                                } catch (IOException ex) { ui.error("\r\nwriteTargetDestinChannel = Files.newByteChannel(..) " + ex.getMessage() + "\r\n"); continue encryptTargetloop; }
    //                            ui.log("writeTargetDestChannelTransfered: " + writeTargetDestChannelTransfered + " targetDestinBuffer.limit(): " + Integer.toString(targetDestinBuffer.limit()) + "\r\n");
                            }
                            targetDestinBuffer.clear(); targetSourceBuffer.clear(); cipherSourceBuffer.clear();
                        }
            
//    ==================================================================================================================================================================
//                      Copy inputFilePath attributes to outputFilePath

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

			attributeViewloop: for (String view:targetSourcePath.getFileSystem().supportedFileAttributeViews()) // acl basic owner user dos
                        {
//                            ui.println(view);
                            if ( view.toLowerCase().equals("basic") )
                            {
                                try
                                {
                                    BasicFileAttributes basicAttributes = null; basicAttributes = Files.readAttributes(targetSourcePath, BasicFileAttributes.class);
                                    try
                                    {
                                        Files.setAttribute(targetDestinPath, "basic:creationTime",        basicAttributes.creationTime());
                                        Files.setAttribute(targetDestinPath, "basic:lastModifiedTime",    basicAttributes.lastModifiedTime());
                                        Files.setAttribute(targetDestinPath, "basic:lastAccessTime",      basicAttributes.lastAccessTime());
                                    }
                                    catch (IOException ex) { ui.error("Error: Set Basic Attributes: " + ex.getMessage() + "\r\n"); }
                                }   catch (IOException ex) { ui.error("Error: basicAttributes = Files.readAttributes(..): " + ex.getMessage()); }
                            }
                            else if ( view.toLowerCase().equals("dos") )
                            {
                                try
                                {
                                    DosFileAttributes msdosAttributes = null; msdosAttributes = Files.readAttributes(targetSourcePath, DosFileAttributes.class);
                                    try
                                    {
                                        Files.setAttribute(targetDestinPath, "basic:lastModifiedTime",    msdosAttributes.lastModifiedTime());
                                        Files.setAttribute(targetDestinPath, "dos:hidden",                msdosAttributes.isHidden());
                                        Files.setAttribute(targetDestinPath, "dos:system",                msdosAttributes.isSystem());
                                        Files.setAttribute(targetDestinPath, "dos:readonly",              msdosAttributes.isReadOnly());
                                        Files.setAttribute(targetDestinPath, "dos:archive",               msdosAttributes.isArchive());
                                    }
                                    catch (IOException ex) { ui.error("Error: Set DOS Attributes: " + ex.getMessage() + "\r\n"); }
                                }   catch (IOException ex) { ui.error("Error: msdosAttributes = Files.readAttributes(..): " + ex.getMessage()); }
                            }
                            else if ( view.toLowerCase().equals("posix") )
                            {
                                PosixFileAttributes posixAttributes = null;
                                try
                                {
                                    posixAttributes = Files.readAttributes(targetSourcePath, PosixFileAttributes.class);
                                    try
                                    {
                                        Files.setAttribute(targetDestinPath, "posix:owner",               posixAttributes.owner());
                                        Files.setAttribute(targetDestinPath, "posix:group",               posixAttributes.group());
                                        Files.setPosixFilePermissions(targetDestinPath,                   posixAttributes.permissions());
                                        Files.setLastModifiedTime(targetDestinPath,                       posixAttributes.lastModifiedTime());
                                    }
                                    catch (IOException ex) { ui.error("Error: Set POSIX Attributes: " + ex.getMessage() + "\r\n"); }
                                }   catch (IOException ex) { ui.error("Error: posixAttributes = Files.readAttributes(..): " + ex.getMessage()); }
                            }
                        } // End attributeViewloop // End attributeViewloop

//    ==================================================================================================================================================================
    
//                      Counting encrypting and shredding for the average throughtput performance
    
//                      Shredding process

                        ui.status("🌊 \"" + targetSourcePath.toAbsolutePath() + "\" ", false);

			long targetDestinSize = 0; double targetDiffFactor = 1;
//				     isValidFile(UI ui, String caller,        Path path, boolean device, long minSize, boolean symlink, boolean writable, boolean report)
			if (Validate.isValidFile(   ui,            "", targetDestinPath,          false,            1,           false,            false,	    true))
			{ try { targetDestinSize = Files.size(targetDestinPath); targetDiffFactor = targetSourceSize / targetDestinSize;} catch (IOException ex) { ui.error("Error: Files.size(targetDestinPath); " + ex.getMessage() + "\r\n"); } } else 

                        readTargetSourceChannelPosition = 0;	readTargetSourceChannelTransfered = 0;
                        readCipherSourceChannelPosition = 0;    readCipherSourceChannelTransfered = 0;
			
                        writeTargetDestChannelPosition = 0;
			
			targetSourceBuffer = ByteBuffer.allocate(readTargetSourceBufferSize); targetSourceBuffer.clear();
                        cipherSourceBuffer = ByteBuffer.allocate(readCipherSourceBufferSize); cipherSourceBuffer.clear();
                        targetDestinBuffer = ByteBuffer.allocate(wrteTargetDestinBufferSize); targetDestinBuffer.clear();

                        boolean targetDestinEnded = false;
			
                        shredloop: while ( ! targetDestinEnded )
                        {
                            while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
                            if (stopPending)    { targetDestinEnded = true; break shredloop; }

                            //read outputFile
//                            readTargetDestinStat.setFileStartEpoch();
                            try (final SeekableByteChannel readTargetDestinChannel = Files.newByteChannel(targetDestinPath, EnumSet.of(StandardOpenOption.READ)))
                            {
                                readTargetDestinChannel.position(readTargetDestChannelPosition);
                                readTargetDestChannelTransfered = readTargetDestinChannel.read(targetDestinBuffer); targetDestinBuffer.flip(); readTargetDestChannelPosition += readTargetDestChannelTransfered;
                                if (( readTargetDestChannelTransfered < 1 )) { targetDestinEnded = true; }
                                readTargetDestinChannel.close();
                            } catch (IOException ex) { ui.error("\r\nreadTargetDestinChannel = Files.newByteChannel(..) " + ex.getMessage() + "\r\n"); continue encryptTargetloop; }
//                            ui.log("readTargetDestChannelTransfered: " + readTargetDestChannelTransfered + " targetDestinBuffer.limit(): " + Integer.toString( targetDestinBuffer.limit()) + "\r\n");

                            //shred inputFile
//                            if ( readTargetDestChannelTransfered < 1 )
                            if ( targetDestinBuffer.limit() > 0 )
                            {
                                wrteTargetSourceStat.setFileStartEpoch();
                                try (final SeekableByteChannel writeTargetSourceChannel = Files.newByteChannel(targetSourcePath, EnumSet.of(StandardOpenOption.WRITE,StandardOpenOption.SYNC)))
                                {
                                    // Fill up inputFileBuffer
                                    writeTargetSourceChannel.position(writeTargetSourceChannelPosition);
                                    writeTargetSourceChannelTransfered = writeTargetSourceChannel.write(targetDestinBuffer); targetSourceBuffer.flip(); writeTargetSourceChannelPosition += writeTargetSourceChannelTransfered;
                                    if (( writeTargetSourceChannelTransfered < 1 )) { targetSourceEnded = true; }
                                    writeTargetSourceChannel.close();
				    wrteTargetSourceStat.setFileEndEpoch(); wrteTargetSourceStat.clock();
                                    wrteTargetSourceStat.addFileBytesProcessed(writeTargetSourceChannelTransfered / 2);
				    allDataStats.addAllDataBytesProcessed("wr src", writeTargetSourceChannelTransfered / 2);
//				    if ( targetDiffFactor < 1 )
//				    { allDataStats.addAllDataBytesProcessed("wr src", writeTargetSourceChannelTransfered * Math.abs((long)targetDiffFactor)); } else
//				    { allDataStats.addAllDataBytesProcessed("wr src", writeTargetSourceChannelTransfered / Math.abs((long)targetDiffFactor)); }
				    
                                } catch (IOException ex) { ui.error("\r\nwriteTargetSourceChannel = Files.newByteChannel(..) " + ex.getMessage() + "\r\n"); continue encryptTargetloop; }
//                                ui.log("writeTargetSourceChannelTransfered: " + writeTargetSourceChannelTransfered + " targetDestinBuffer.limit(): " + Integer.toString(targetDestinBuffer.limit()) + "\r\n");
                            }
                            targetDestinBuffer.clear(); targetSourceBuffer.clear(); cipherSourceBuffer.clear();
                        }

    //                  FILE STATUS 
			if (verbose)
			{
			    fileStatusLine += "- Crypt: rd(" +  readTargetSourceStat.getFileBytesThroughPut() + ") -> ";
//			    fileStatusLine += "rd(" +           readCipherSourceStat.getFileBytesThroughPut() + ") -> ";
//			    fileStatusLine += "wr(" +           wrteTargetDestinStat.getFileBytesThroughPut() + ") ";
//			    fileStatusLine += "- Shred: rd(" +  readTargetDestinStat.getFileBytesThroughPut() + ")";
			    fileStatusLine += "wr(" +           wrteTargetSourceStat.getFileBytesThroughPut() + ") ";
			}
			fileStatusLine += allDataStats.getAllDataBytesProgressPercentage();
			
			ui.log(fileStatusLine);
			
			allDataStats.addFilesProcessed(1);

                        if ( print ) { ui.log(" ----------------------------------------------------------------------\r\n"); }


//                      Delete the original
//                        long targetSourceSize = 0; try { targetSourceSize = Files.size(targetSourcePath); }	catch (IOException ex)	{ ui.error("\r\nError: Files.size(targetSourcePath): " + ex.getMessage() + "\r\n"); continue encryptTargetloop; }
//                        long targetDestinSize = 0; try { targetDestinSize = Files.size(targetDestinPath); }	catch (IOException ex)  { ui.error("\r\nError: Files.size(targetDestinPath): " + ex.getMessage() + "\r\n"); continue encryptTargetloop; }
                        if  (
				( targetSourceSize != 0 ) && ( targetDestinSize != 0 ) &&
				( Math.abs(targetSourceSize - targetDestinSize)  == (FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length()) * 2 ) ||
				( targetSourceSize == targetDestinSize)
			    )
			{ try { Files.deleteIfExists(targetSourcePath); } catch (IOException ex)    { ui.error("\r\nFiles.deleteIfExists(inputFilePath): " + ex.getMessage() + "\r\n"); continue encryptTargetloop; } }
                    } else { ui.status("Candidate: " + targetSourcePath.toAbsolutePath(), true); } // End real run
                }// else { ui.error(targetSourcePath.toAbsolutePath() + " ignoring:   " + cipherSourcePath.toAbsolutePath() + " (is cipher!)\r\n"); }
            } // else { ui.error("Skipping directory: " + targetSourcePath.getFileName() + "\r\n"); } // End "not a directory"
            
        } // Encrypt Files Loop // Encrypt Files Loop
        allDataStats.setAllDataEndNanoTime(); allDataStats.clock();
        if ( stopPending ) { ui.status("\r\n", false); stopPending = false;  } // It breaks in the middle of encrypting, so the encryption summery needs to begin on a new line

//      Print the stats
        ui.status(allDataStats.getEndSummary(Mode.getDescription()), true);

        updateProgressTaskTimer.cancel(); updateProgressTaskTimer.purge();
//        updateProgressTimeline.stop();
        ui.encryptionFinished();
    }
    
    private ByteBuffer encryptBuffer(ByteBuffer targetSourceBuffer, ByteBuffer cipherSourceBuffer)
    {
        byte targetDestinByte;
        
//        ByteBuffer targetDestinBuffer =   ByteBuffer.allocate(wrteTargetDestinBufferSize); targetDestinBuffer.clear();
        ByteBuffer targetDestinBuffer =   ByteBuffer.allocate(cipherSourceBuffer.capacity()); targetDestinBuffer.clear();
	
        while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
        for (int targetSourceBufferCount = 0; targetSourceBufferCount < targetSourceBuffer.limit(); targetSourceBufferCount++)
        {
            targetDestinByte = encryptByte(targetSourceBuffer.get(targetSourceBufferCount), cipherSourceBuffer.get(targetSourceBufferCount));
            targetDestinBuffer.put(targetDestinByte);
        }
        targetDestinBuffer.flip();
	
        return targetDestinBuffer;
    }
    
    private byte encryptByte(final byte targetSourceByte, byte cipherSourceByte)
    {
        byte targetDestinByte;

	int targetIgnoreBits = 0;
        int targetCipherBits = 0;
        int targetMergedBits = 0; // Merged Ignored & Negated bits)
	        
//      Negate 0 cipherbytes to prevent 0 encryption
        if (cipherSourceByte == 0) { cipherSourceByte = (byte)(~cipherSourceByte & 0xFF); }

        targetIgnoreBits = targetSourceByte & ~cipherSourceByte;
        targetCipherBits = ~targetSourceByte & cipherSourceByte;
        targetMergedBits = targetIgnoreBits + targetCipherBits; // targetDestinByte      
	
        targetDestinByte = (byte)(targetMergedBits & 0xFF);
        
        if ( print )    { logByte(targetSourceByte, cipherSourceByte, targetDestinByte, targetIgnoreBits, targetCipherBits, targetMergedBits); }
        if ( bin )      { logByteBinary(targetSourceByte, cipherSourceByte, targetDestinByte, targetIgnoreBits, targetCipherBits, targetMergedBits); }
        if ( dec )      { logByteDecimal(targetSourceByte, cipherSourceByte, targetDestinByte, targetIgnoreBits, targetCipherBits, targetMergedBits); }
        if ( hex )      { logByteHexaDecimal(targetSourceByte, cipherSourceByte, targetDestinByte, targetIgnoreBits, targetCipherBits, targetMergedBits); }
        if ( chr )      { logByteChar(targetSourceByte, cipherSourceByte, targetDestinByte, targetIgnoreBits, targetCipherBits, targetMergedBits); }

        // Increment Byte Progress Counters        
        return targetDestinByte; // targetDestinByte
    }

    private ByteBuffer createTargetDestinToken(Path cipherSourcePath) // Tested
    {
        ByteBuffer plainTextTokenBuffer = ByteBuffer.allocate(FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length()); plainTextTokenBuffer.clear();
        ByteBuffer cipherBitTokenBuffer = ByteBuffer.allocate(FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length()); cipherBitTokenBuffer.clear();
        ByteBuffer encryptedTokenBuffer = ByteBuffer.allocate(FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length()); encryptedTokenBuffer.clear();

	ByteBuffer targetDstTokenBuffer = ByteBuffer.allocate(FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length() * 2); targetDstTokenBuffer.clear();
	long readCipherSourceChannelTransfered = 0;                

	// Create plaint text Buffer
	plainTextTokenBuffer.put(FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.getBytes());
	
	// Create Cipher Buffer
	try (final SeekableByteChannel readCipherSourceChannel = Files.newByteChannel(cipherSourcePath, EnumSet.of(StandardOpenOption.READ)))
	{
//	    readCipherSourceChannel.position(readCipherSourceChannelPosition);
	    readCipherSourceChannelTransfered = readCipherSourceChannel.read(cipherBitTokenBuffer);
	    cipherBitTokenBuffer.flip(); readCipherSourceChannel.close();
	} catch (IOException ex) { ui.error("Error: getTargetDestinToken: readCipherSourceChannel " + ex.getMessage() + "\r\n"); }
	
	// Create Encrypted Token Buffer
	encryptedTokenBuffer = encryptBuffer(plainTextTokenBuffer, cipherBitTokenBuffer);
	
	// Create Target Destin Token Buffer
	byte[] tokenArray = new byte[(FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length() * 2)];
	for (int x = 0; x < plainTextTokenBuffer.capacity(); x++) { tokenArray[x] = plainTextTokenBuffer.array()[x]; }
	for (int x = 0; x < encryptedTokenBuffer.capacity(); x++) { tokenArray[(FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length() + x)] = encryptedTokenBuffer.array()[x]; }
	targetDstTokenBuffer.put(tokenArray); targetDstTokenBuffer.flip();
//	for (byte myByte:plainTextTokenBuffer.array()) {targetDstTokenBuffer.put(myByte);}
//	for (byte myByte:encryptedTokenBuffer.array()) {targetDstTokenBuffer.put(myByte);}
	
	return targetDstTokenBuffer;
    }
    
    private boolean targetSourceHasToken(Path targetSourcePath) // Tested
    {
	
	boolean targetSourceHasToken = false;
	
        ByteBuffer plainTextTokenBuffer = ByteBuffer.allocate(FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length()); plainTextTokenBuffer.clear();
        ByteBuffer encryptedTokenBuffer = ByteBuffer.allocate(FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length()); encryptedTokenBuffer.clear();
	
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

	if ( plainTextTokenString.equals(FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN) ) { targetSourceHasToken = true; }

	return targetSourceHasToken;
    }
    
    private boolean targetHasAuthenticatedToken(Path targetSourcePath, Path cipherSourcePath) // Tested
    {
	boolean readTargetSourceChannelError = false;
	boolean cipherAuthenticatedTargetSource =   false;
        ByteBuffer targetSrcTokenBuffer =	    ByteBuffer.allocate(FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length() * 2); targetSrcTokenBuffer.clear();
        ByteBuffer targetEncryptedTokenBuffer =	    ByteBuffer.allocate(FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length()); targetEncryptedTokenBuffer.clear();
        ByteBuffer cipherSourceBuffer =		    ByteBuffer.allocate(FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length()); cipherSourceBuffer.clear();
        ByteBuffer cipherDecryptedTokenBuffer =	    ByteBuffer.allocate(FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length()); cipherDecryptedTokenBuffer.clear();
	
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
	targetEncryptedTokenBuffer.put(targetSrcTokenBuffer.array(), FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length(), FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN.length()); targetEncryptedTokenBuffer.flip();
	
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
	    cipherDecryptedTokenBuffer = encryptBuffer(targetEncryptedTokenBuffer, cipherSourceBuffer);
	    String cipherDecryptedTokenBufferString = new String(cipherDecryptedTokenBuffer.array(), StandardCharsets.UTF_8);
//	    ui.status("targetHasAuthenticatedToken.cipherDecryptedTokenBufferString: " + cipherDecryptedTokenBufferString + "\r\n", true);
	    
	    // Authenticate Cipher Token against Target Token
	    if ( cipherDecryptedTokenBufferString.equals(FINALCRYPT_PLAIN_IEXT_AUTHENTICATION_TOKEN)) { cipherAuthenticatedTargetSource = true; } else { cipherAuthenticatedTargetSource = false; }
	    
	} else { cipherAuthenticatedTargetSource = false; }
	
	return cipherAuthenticatedTargetSource;
    }
    
//  Recursive Deletion of PathList
    public void deleteSelection(ArrayList<Path> targetSourcePathList, boolean delete, boolean returnpathlist, String pattern, boolean negatePattern)
    {
        EnumSet opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS); //follow links
//        MySimpleFileVisitor mySimpleFileVisitor = new MySimpleFileVisitor(ui, verbose, delete, symlink, returnpathlist, pattern, negatePattern);
//						      MySimpleFileVisitor(UI ui, boolean verbose, boolean delete, long minSize, boolean symlink, boolean writable, boolean returnpathlist, String pattern, boolean negatePattern)
        MySimpleFileVisitor mySimpleFileVisitor = new MySimpleFileVisitor(   ui,	 verbose,         delete,            0,            true,            false,         returnpathlist,        pattern,         negatePattern);
        for (Path path:targetSourcePathList)
        {
            try{Files.walkFileTree(path, opts, Integer.MAX_VALUE, mySimpleFileVisitor);} catch(IOException e){System.err.println(e);}
        }
    }
    

    private String getBinaryString(Byte myByte) { return String.format("%8s", Integer.toBinaryString(myByte & 0xFF)).replace(' ', '0'); }
    private String getDecString(Byte myByte) { return String.format("%3d", (myByte & 0xFF)).replace(" ", "0"); }
    private String getHexString(Byte myByte, String digits) { return String.format("%0" + digits + "X", (myByte & 0xFF)); }
    private String getChar(Byte myByte) { return String.format("%1s", (char) (myByte & 0xFF)).replaceAll("\\p{C}", "?"); }  //  (myByte & 0xFF); }
    
    public boolean getPausing()             { return pausing; }
    public boolean getStopPending()         { return stopPending; }
    public void setPausing(boolean val)     { pausing = val; }
    public void setStopPending(boolean val) { stopPending = val; }

    private void logByteBuffer(String preFix, ByteBuffer byteBuffer)
    {
        ui.log(preFix + "C: ");
        ui.log(" " + preFix + "Z: " + byteBuffer.limit() + "\r\n");
    }

    private void logByte(byte dataByte, byte cipherByte, byte outputByte, int dum, int dnm, int dbm)
    {
        String adrhex = getHexString((byte)printAddressByteCounter,"8");

        String datbin = getBinaryString(dataByte);
        String dathex = getHexString(dataByte, "2");
        String datdec = getDecString(dataByte);
        String datchr = getChar(dataByte);
        
        String cphbin = getBinaryString(cipherByte);
        String cphhex = getHexString(cipherByte, "2");
        String cphdec = getDecString(cipherByte);
        String cphchr = getChar(cipherByte);
        
        String outbin = getBinaryString(outputByte);
        String outhex = getHexString(outputByte, "2");
        String outdec = getDecString(outputByte);
        String outchr = getChar(outputByte);
        
        ui.log("| " + adrhex + " | " + datbin + " " +  dathex + " " + datdec + " " + datchr + " | " );
        ui.log                 (cphbin + " " +  cphhex + " " + cphdec + " " + cphchr + " | " );
        ui.log                 (outbin + " " +  outhex + " " + outdec + " " + outchr + " |\r\n");
        printAddressByteCounter++;
    }
    
    private void logByteBinary(byte inputByte, byte cipherByte, byte outputByte, int dum, int dnm, int dbm)
    {
        ui.log("\r\n");
        ui.log("Input  = " + getBinaryString(inputByte) + "\r\n");
        ui.log("Cipher = " + getBinaryString(cipherByte) + "\r\n");
        ui.log("Output = " + getBinaryString(outputByte) + "\r\n");
        ui.log("\r\n");
        ui.log("DUM  = " + getBinaryString((byte)inputByte) + " & " + getBinaryString((byte)~cipherByte) + " = " + getBinaryString((byte)dum) + "\r\n");
        ui.log("DNM  = " + getBinaryString((byte)~inputByte) + " & " + getBinaryString((byte)cipherByte) + " = " + getBinaryString((byte)dnm) + "\r\n");
        ui.log("DBM  = " + getBinaryString((byte)dum) + " & " + getBinaryString((byte)dnm) + " = " + getBinaryString((byte)dbm) + "\r\n");
    }
    
    private void logByteDecimal(byte dataByte, byte cipherByte, byte outputByte, int dum, int dnm, int dbm)
    {
        ui.log("\r\n");
        ui.log("Input  = " + getDecString(dataByte) + "\r\n");
        ui.log("Cipher = " + getDecString(cipherByte) + "\r\n");
        ui.log("Output = " + getDecString(outputByte) + "\r\n");
        ui.log("\r\n");
        ui.log("DUM  = " + getDecString((byte)dataByte) + " & " + getDecString((byte)~cipherByte) + " = " + getDecString((byte)dum) + "\r\n");
        ui.log("DNM  = " + getDecString((byte)~dataByte) + " & " + getDecString((byte)cipherByte) + " = " + getDecString((byte)dnm) + "\r\n");
        ui.log("DBM  = " + getDecString((byte)dum) + " & " + getDecString((byte)dnm) + " = " + getDecString((byte)dbm) + "\r\n");
    }
    
    private void logByteHexaDecimal(byte dataByte, byte cipherByte, byte outputByte, int dum, int dnm, int dbm)
    {
        ui.log("\r\n");
        ui.log("Input  = " + getHexString(dataByte,"2") + "\r\n");
        ui.log("Cipher = " + getHexString(cipherByte,"2") + "\r\n");
        ui.log("Output = " + getHexString(outputByte,"2") + "\r\n");
        ui.log("\r\n");
        ui.log("DUM  = " + getHexString((byte)dataByte,"2") + " & " + getHexString((byte)~cipherByte,"2") + " = " + getHexString((byte)dum,"2") + "\r\n");
        ui.log("DNM  = " + getHexString((byte)~dataByte,"2") + " & " + getHexString((byte)cipherByte,"2") + " = " + getHexString((byte)dnm,"2") + "\r\n");
        ui.log("DBM  = " + getHexString((byte)dum,"2") + " & " + getHexString((byte)dnm,"2") + " = " + getHexString((byte)dbm,"2") + "\r\n");
    }
    
    private void logByteChar(byte dataByte, byte cipherByte, byte outputByte, int dum, int dnm, int dbm)
    {
        ui.log("\r\n");
        ui.log("Input  = " + getChar(dataByte) + "\r\n");
        ui.log("Cipher = " + getChar(cipherByte) + "\r\n");
        ui.log("Output = " + getChar(outputByte) + "\r\n");
        ui.log("\r\n");
        ui.log("DUM  = " + getChar((byte)dataByte) + " & " + getChar((byte)~cipherByte) + " = " + getChar((byte)dum) + "\r\n");
        ui.log("DNM  = " + getChar((byte)~dataByte) + " & " + getChar((byte)cipherByte) + " = " + getChar((byte)dnm) + "\r\n");
        ui.log("DBM  = " + getChar((byte)dum) + " & " + getChar((byte)dnm) + " = " + getChar((byte)dbm) + "\r\n");
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
