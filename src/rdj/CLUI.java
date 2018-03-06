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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

/* commandline test routine

clear; echo -n -e \\x05 > 1; echo -n -e \\x03 > 2; java -jar FinalCrypt.jar
clear; echo -n -e \\x05 > 1; echo -n -e \\x03 > 2; java -cp FinalCrypt.jar rdj/CLUI --print -c 2 -t 1
clear; echo -n ZYXVWUTSRQPONMLKJIHGFEDCBA098765 > a; echo -n abcdefghijklstuvwxyz > b; java -cp FinalCrypt.jar rdj/CLUI --print -c b -t a

*/

public class CLUI implements UI
{
    private FinalCrypt finalCrypt;
    private Version version;
    private UI ui;
    private final Configuration configuration;
    private boolean symlink = false;
    private boolean verbose = false;

    public CLUI(String[] args)
    {
        this.ui = this;
        boolean tfset = false;
	boolean cfset = false;
	boolean cfsetneeded = true;
        boolean validInvocation = true;
        boolean negatePattern = false;
        boolean printgpt = false;
        boolean deletegpt = false;

        ArrayList<Path> targetPathList = new ArrayList<>();
        Path batchFilePath = null;
        Path targetFilePath = null;
        Path cipherFilePath = null;
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
            if      (( args[paramCnt].equals("-h")) || ( args[paramCnt].equals("--help") ))                         { usage(); }
            else if (( args[paramCnt].equals("-d")) || ( args[paramCnt].equals("--debug") ))                        { finalCrypt.setDebug(true); }
            else if (( args[paramCnt].equals("-v")) || ( args[paramCnt].equals("--verbose") ))                      { finalCrypt.setVerbose(true); verbose = true; }
            else if (( args[paramCnt].equals("-p")) || ( args[paramCnt].equals("--print") ))                        { finalCrypt.setPrint(true); }
            else if (( args[paramCnt].equals("-l")) || ( args[paramCnt].equals("--symlink") ))                      { finalCrypt.setSymlink(true); symlink = true; }
            else if ( args[paramCnt].equals("--txt"))                                                               { finalCrypt.setTXT(true); }
            else if ( args[paramCnt].equals("--bin"))                                                               { finalCrypt.setBin(true); }
            else if ( args[paramCnt].equals("--dec"))                                                               { finalCrypt.setDec(true); }
            else if ( args[paramCnt].equals("--hex"))                                                               { finalCrypt.setHex(true); }
            else if ( args[paramCnt].equals("--chr"))                                                               { finalCrypt.setChr(true); }
            else if ( args[paramCnt].equals("--gpt-print"))                                                         { printgpt = true; cfsetneeded = false; }
            else if ( args[paramCnt].equals("--gpt-delete"))                                                        { deletegpt = true; cfsetneeded = false; }
            else if ( args[paramCnt].equals("--version"))                                                           { println(version.getProcuct() + " " + version.getCurrentlyInstalledOverallVersionString()); System.exit(0); }
            else if ( args[paramCnt].equals("--update"))                                                            { version.checkLatestOnlineVersion(this); 	    String[] lines = version.getUpdateStatus().split("\r\n"); for (String line: lines) {log(line + "\r\n");} System.exit(0); }
            else if ( ( args[paramCnt].equals("-s")) && (!args[paramCnt+1].isEmpty()) )				    { if ( validateIntegerString(args[paramCnt + 1]) ) { finalCrypt.setBufferSize(Integer.valueOf( args[paramCnt + 1] ) * 1024 ); paramCnt++; } else { error("\r\nError: Invalid Option Value [-b size]" + "\r\n"); usage(); }}

//          Filtering Options
            else if ( args[paramCnt].equals("--dry"))                                                               { finalCrypt.setDry(true); }
            else if ( ( args[paramCnt].equals("-w")) && (!args[paramCnt+1].isEmpty()) )				    { negatePattern = false; pattern = "glob:" + args[paramCnt+1]; paramCnt++; }
            else if ( ( args[paramCnt].equals("-W")) && (!args[paramCnt+1].isEmpty()) )				    { negatePattern = true; pattern = "glob:" + args[paramCnt+1]; paramCnt++; }
            else if ( ( args[paramCnt].equals("-r")) && (!args[paramCnt+1].isEmpty()) )				    { pattern = "regex:" + args[paramCnt+1]; paramCnt++; }

//          File Parameters
            else if ( ( args[paramCnt].equals("-t")) && (!args[paramCnt+1].isEmpty()) )				    { targetFilePath = Paths.get(args[paramCnt+1]); targetPathList.add(targetFilePath); tfset = true; paramCnt++; }
            else if ( ( args[paramCnt].equals("-b")) && (!args[paramCnt+1].isEmpty()) )				    { tfset = addBatchTargetFiles(args[paramCnt+1], targetPathList); paramCnt++; }
            else if ( ( args[paramCnt].equals("-c")) && (paramCnt+1 < args.length) )				    { cipherFilePath = Paths.get(args[paramCnt+1]); cfset = true; paramCnt++; }
            else { System.err.println("\r\nError: Invalid Parameter: " + args[paramCnt]); usage(); }
        }
        
        if ( ! tfset )												    { error("\r\nError: Missing valid parameter <-t \"file/dir\"> or <-b \"batchfile\">" + "\r\n"); usage(); }
        if ((cfsetneeded) && ( ! cfset ))									    { error("\r\nError: Missing valid parameter <-c \"cipherfile\">" + "\r\n"); usage(); }

        
        
//////////////////////////////////////////////////// CHECK CIPHERFILE INPUT /////////////////////////////////////////////////
        
        if ( cfsetneeded )
	{
//	    State.cipherSelected = State.INVALID;
//	    State.cipherReady = false;
//
//    //      Cipher Validation        
//	    if (Files.exists(cipherFilePath))
//	    {
		Validate.checkCipher(this, finalCrypt, cipherFilePath);
//	    }
//	    else
//	    { 
//		error("Cipher parameter: " + cipherFilePath + " does not exists\r\n"); usage();
//	    }            
	}

//////////////////////////////////////////////////// CHECK TARGETFILE INPUT /////////////////////////////////////////////////

//      Check if targetFilesPathList elements exist on filesystem

        for(Path targetPath : targetPathList)
        {
            if (Files.exists(targetPath))
            {
//			      isValidDir(UI ui, Path targetDirPath, boolean symlink, boolean report)
                if ( Validate.isValidDir( this,         targetPath,         symlink,        verbose))
                {
                    if (verbose) { status("Target parameter: " + targetPath + " is a valid dir\r\n", true); }
                }
//				   isValidFile(UI ui, String caller, Path targetSourcePath, long minSize, boolean symlink, boolean writable, boolean report)
                else if ( Validate.isValidFile(this, "CLUI.CLUI() ",            targetPath,           1L,         symlink,             true,        verbose))
                {
                    if (verbose) { status("Target parameter: " + targetPath + " is a valid file\r\n", true); }
                }
            }
            else
            { 
                    error("Target parameter: " + targetPath + " does not exists\r\n"); usage();
            }            
        }
        
	Validate.checkTarget(this, finalCrypt, targetPathList, cipherFilePath, pattern, negatePattern, symlink, false, printgpt, deletegpt);

	
/////////////////////////////////////////////// SET MODE ////////////////////////////////////////////////////


        Mode.modeReady = false; Mode.setMode(Mode.SELECT);        
        if      
        (
	    (State.targetSelected == State.FILE) && (State.targetReady) &&
	    (State.cipherSelected == State.FILE) && (State.cipherReady)
	)
        {
            Mode.modeReady = true; Mode.setMode(Mode.ENCRYPT);
        }
        else if ((State.targetSelected == State.FILE) && (State.targetReady) && (State.cipherSelected == State.PARTITION) && (State.cipherReady))
        {
            Mode.modeReady = true; Mode.setMode(Mode.ENCRYPTRAW);
        }
        else if ((State.targetSelected == State.DEVICE) && (State.targetReady) && (State.cipherSelected == State.FILE) &&	(State.cipherReady))
        {
            Mode.modeReady = true; Mode.setMode(Mode.CREATE_CIPHER_DEVICE);
        }
        else if ((State.targetSelected == State.DEVICE) && (State.targetReady) && (State.cipherSelected == State.DEVICE)&&	(State.cipherReady))
        {
//          Source and Dest Device may not be the same
            if (
                    ( targetPathList.get(0).compareTo(cipherFilePath) != 0 )  &&
                    ( State.targetSelected == State.DEVICE ) && ( State.cipherSelected == State.DEVICE )
               )
            {
                Mode.modeReady = true; Mode.setMode(Mode.CLONE_CIPHER_DEVICE);
            }
            else
            { 
                Mode.modeReady = false;
            }
        }
        else                                                                                    
        {
            Mode.modeReady = false;
	    Mode.setMode(Mode.SELECT);
        }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////            

//          Mode SET / KNOWN, Now we know what and how to execute

	if ((State.targetReady) && (State.cipherReady) && (Mode.modeReady) )
        {
            DeviceManager deviceManager;
            if ( ( Mode.getMode() == Mode.ENCRYPT ) || ( Mode.getMode() == Mode.ENCRYPTRAW ))
            {
//                Convert small PathList from parameters into ExtendedPathList (contents of subdirectory parameters as targetFile)
		log("Retreiving targets...\r\n\r\n");
//								       getExtendedPathList(UI ui, ArrayList<Path> userSelectedItemsPathList, Path cipherPath, long minSize, boolean symlink, boolean writable, String pattern, boolean negatePattern, boolean status)
                ArrayList<Path> targetFilesPathListExtended = Validate.getExtendedPathList(   ui,                            targetPathList,  cipherFilePath,           1L,         symlink,             true,        pattern,         negatePattern,          false);
                encryptionStarted();
                finalCrypt.encryptSelection(targetFilesPathListExtended, cipherFilePath);
            }
            else if ( Mode.getMode() == Mode.CREATE_CIPHER_DEVICE )
            {
                encryptionStarted();
                deviceManager = new DeviceManager(ui); deviceManager.start(); deviceManager.createCipherDevice(cipherFilePath, new Device(ui,targetPathList.get(0)));
                encryptionFinished();
            }
            else if ( Mode.getMode() == Mode.CLONE_CIPHER_DEVICE )
            {
                encryptionStarted();
                deviceManager = new DeviceManager(ui); deviceManager.start(); deviceManager.cloneCipherDevice(new Device(ui,cipherFilePath), new Device(ui,targetPathList.get(0)));
                encryptionFinished();
            }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        }
        else
        {
//	    log(State.print());
//          status("Nothing to do.\r\n", true);
//	    usage();
        }
            
    }


    private boolean addBatchTargetFiles(String batchFilePathString, ArrayList<Path> targetFilesPathList)
    {
        boolean ifset = false;
        Path batchFilePath;
        Path targetFilePath;
//		      isValidFile(UI ui, String caller,                       Path targetSourcePath, long minSize, boolean symlink, boolean writable, boolean report)
        if ( Validate.isValidFile(this,  "CLUI.addBatchTargetFiles", Paths.get(batchFilePathString),           1L,         symlink,             true,           true) )
        {
            log("Adding items from batchfile: " + batchFilePathString + "\r\n");
            batchFilePath = Paths.get(batchFilePathString);
            try
            {
                for (String targetFilePathString:Files.readAllLines(batchFilePath))
                {
//                  Entry may not be a directory (gets filtered and must be a valid file)
//				  isValidFile(UI ui, String caller,                        Path targetSourcePath, long minSize, boolean symlink, boolean writable, boolean report)
                    if ( Validate.isValidFile(   ui, "CLUI.addBatchTargetFiles", Paths.get(targetFilePathString),           0L,         symlink,             true,           true) )
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

    public static void main(String[] args)
    {
        new CLUI(args);
    }
    
    private boolean validateIntegerString(String text) { try { Integer.parseInt(text); return true;} catch (NumberFormatException e) { return false; } }

    private void usage()
    {
        String fileSeparator = java.nio.file.FileSystems.getDefault().getSeparator();

        log("\r\n");
        log("Usage:   java -cp FinalCrypt.jar rdj/CLUI [options] <Parameters>\r\n");
        log("\r\n");
        log("Options:\r\n");
        log("            [-h] [--help]         Shows this help page.\r\n");
        log("            [-d] [--debug]        Enables debugging mode.\r\n");
        log("            [-v] [--verbose]      Enables verbose mode.\r\n");
        log("            [-p] [--print]        Print overal data encryption.\r\n");
        log("            [-l] [--symlink]      Include symlinks (can cause double encryption! Not recommended!).\r\n");
        log("                 [--version]      Print " + version.getProcuct() + " version.\r\n");
        log("                 [--update]       Check for online updates.\r\n");
        log("            [--txt]               Print text calculations.\r\n");
        log("            [--bin]               Print binary calculations.\r\n");
        log("            [--dec]               Print decimal calculations.\r\n");
        log("            [--hex]               Print hexadecimal calculations.\r\n");
        log("            [--chr]               Print character calculations.\r\n");
        log("                                  Warning: The above Print options slows encryption severely.\r\n");
        log("            [--gpt-print]         Print GUID Partition Table in combination with -t \"device\".\r\n");
        log("            [--gpt-delete]        Delete GUID Partition Table in combination with -t \"device\".\r\n");
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
        log("            <-c \"cipherfile\">     The file that encrypts your file(s). Keep cipherfile SECRET!\r\n");
        log("                                  A cipher-file is a unique file like a personal photo or video!\r\n");
        log("\r\n");
        log("            <-t / -b>             The target items you want to encrypt. Individual (-t) or by batch (-b).\r\n");
        log("            <[-t \"file/dir\"]>     Target file or dir you want to encrypt (encrypts dirs recursively).\r\n");
        log("            <[-b \"batchfile\"]>    Batchfile with targetfiles you want to encrypt (only files accepted).\r\n");
        log("Examples:\r\n");
        log("\r\n");
        log("            # Encrypt myfile with mycipherfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -c mycipherfile -t myfile\r\n");
        log("\r\n");
        log("            # Encrypt myfile and all content in mydir with mycipherfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -c mycipherfile -t myfile -t mydir\r\n");
        log("\r\n");
        log("            # Encrypt files in batchfile with mycipherfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -c mycipherfile -b mybatchfile\r\n");
        log("\r\n");
        log("            # Encrypt all files with *.bit extension in mydir with mycipherfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -c mycipherfile -t mydir -w '*.bit'\r\n");
        log("\r\n");
        log("            # Encrypt all files without *.bit extension in mydir with mycipherfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -c mycipherfile -t mydir -W '*.bit'\r\n");
        log("\r\n");
        log("            # Encrypt all files with *.bit extension in mydir with mycipherfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -c mycipherfile -t mydir -r '^.*\\.bit$'\r\n");
        log("\r\n");
        log("            # Encrypt all files excluding .bit extension in mydir with mycipherfile\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -c mycipherfile -t mydir -r '(?!.*\\.bit$)^.*$'\r\n");
        log("\r\n");
        log("Cipher Device Examples (Linux):\r\n");
        log("\r\n");
        log("            # Create Cipher Device with 2 cipher partitions (e.g. on USB Mem Stick)\r\n");
        log("            # Beware: cipherfile gets randomized before writing to Device\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -c mycipherfile -t /dev/sdb\r\n");
        log("\r\n");
        log("            # Print GUID Partition Table\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --gpt-print -t /dev/sdb\r\n");
        log("\r\n");
        log("            # Delete GUID Partition Table\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI --gpt-delete -t /dev/sdb\r\n");
        log("\r\n");
        log("            # Clone Cipher Device (-c sourcecipherdevice -t destinationcipherdevice)\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -c /dev/sdb -t /dev/sdc\r\n");
        log("\r\n");
        log("            # Encrypt myfile with raw cipher partition\r\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -c /dev/sdb1 -t myfile\r\n");
        log("\r\n");
        log(Version.getProcuct() + " " + version.checkCurrentlyInstalledVersion(this) + " - Author: " + Version.getAuthor() + " - Copyright: " + Version.getCopyright() + "\r\n\r\n");
        System.exit(1);
    }

    @Override
    public void log(String message)
    {
        System.out.print(message);
        Thread logThread = new Thread(new Runnable()
        {
//            private DeviceManager rawCipher;
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                try { Files.write(configuration.getLogFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC); } catch (IOException ex) { println("Files.write(" + configuration.getLogFilePath() + ")..));"); }

//                    try (final SeekableByteChannel writeOutputFileChannel = Files.newByteChannel(configuration.getLogFilePath(), EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC)))
//                    {
//                        // Encrypt targetBuffer and fill up outputBuffer
//                        ByteBuffer outputFileBuffer =  ByteBuffer.allocate(message.getBytes().length); outputFileBuffer.clear();
//                        outputFileBuffer.put(message.getBytes()); outputFileBuffer.flip();
//                        writeOutputFileChannel.write(outputFileBuffer);
//                        writeOutputFileChannel.close();
//                    } catch (IOException ex) { ui.error("\r\nFiles.newByteChannel(configuration.getLogFilePath(): " + ex.getMessage() + "\r\n"); }
            }
        });
        logThread.setName("encryptThread");
        logThread.setDaemon(true);
        logThread.start();
    }

    @Override
    public void error(String message)
    {
        status(message, true);
            Thread errorLogThread = new Thread(new Runnable()
            {
//                private DeviceManager rawCipher;
                @Override
                @SuppressWarnings({"static-access"})
                public void run()
                {
                    try { Files.write(configuration.getErrorFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC); } catch (IOException ex) { println("Files.write(" + configuration.getErrorFilePath() + ")..));"); }

//                    try (final SeekableByteChannel writeOutputFileChannel = Files.newByteChannel(configuration.getErrorFilePath(), EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC)))
//                    {
//                        // Encrypt targetBuffer and fill up outputBuffer
//                        ByteBuffer outputFileBuffer =  ByteBuffer.allocate(message.getBytes().length); outputFileBuffer.clear();
//                        outputFileBuffer.put(message.getBytes()); outputFileBuffer.flip();
//                        writeOutputFileChannel.write(outputFileBuffer);
//                        writeOutputFileChannel.close();
//                    } catch (IOException ex) { ui.error("\r\nFiles.newByteChannel(configuration.getErrorFilePath(): " + ex.getMessage() + "\r\n"); }
                }
            });
            errorLogThread.setName("encryptThread");
            errorLogThread.setDaemon(true);
            errorLogThread.start();
    }

    @Override public void status(String status, boolean log) { if (log) { log(status); } }
    @Override public void statusNow(String status, boolean log) { if (log) { log(status); } }

    @Override public void println(String message) { System.out.println(message); }

    @Override public void encryptionGraph(int value) {  }

    @Override
    public void encryptionStarted() 
    {
    }

    @Override public void encryptionProgress(int filesProgress, int fileProgress)
    {
//        log("filesProgress: " + filesProgress + " fileProgress: " + fileProgress);
    }
    
    @Override public void encryptionFinished()
    {
    }
}
