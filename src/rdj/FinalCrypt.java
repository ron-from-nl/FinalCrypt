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
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
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
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
//import java.util.TimerTask;

public class FinalCrypt extends Thread
{
    private boolean debug = false, verbose = false, print = false, symlink = false, txt = false, bin = false, dec = false, hex = false, chr = false, dry = false;

    private final int bufferSizeDefault = (1 * 1024 * 1024); // 1MB BufferSize overall better performance
    private int bufferSize = 0; // Default 1MB
    private int inputFileBufferSize;
    private int cipherFileBufferSize;
    private int outputFileBufferSize;
    private long bufferTotal = 0; // DNumber of buffers

    private int printAddressByteCounter = 0;
    private ArrayList<Path> inputFilesPathList;
    private Path cipherFilePath = null;
    private Path outputFilePath = null;
    private final long inputFileSize = 0;
    private final long cipherFileSize = 0;
    private final long outputFileSize = 0;
    private final String encoding = System.getProperty("file.encoding");
    private final UI ui;
    private final FinalCrypt fc;
    
//    private TimerTask updateProgressTask;
//    private java.util.Timer updateProgressTaskTimer;

    private String localVersionString = "";
    private String remoteVersionString = "";
    private int localVersion = 0;
    private int remoteVersion = 0;
//        URL localURL = null;
    private InputStream istream = null;
    private URL remoteURL = null;
    private ReadableByteChannel rbc = null;
    private ByteBuffer byteBuffer;        
    private boolean stopPending = false;
    private boolean pausing = false;
    private boolean inputFileEnded;
    private long filesTotal = 0;
    private long filesProcessed = 0;
    private Timeline updateProgressTimeline;


    public FinalCrypt(UI ui)
    {   
//        Set the locations of the version resources
        
        inputFilesPathList = new ArrayList<>();
        inputFileBufferSize = bufferSize;
        cipherFileBufferSize = bufferSize;
        outputFileBufferSize = bufferSize;        
        this.ui = ui;
        fc = this;
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
    public int getBufferSizeDefault()                                      { return bufferSizeDefault; }
    public ArrayList<Path> getInputFilesPathList()                          { return inputFilesPathList; }
    public Path getCipherFilePath()                                         { return cipherFilePath; }
    public Path getOutputFilePath()                                         { return outputFilePath; }
    
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
        this.inputFileBufferSize = this.bufferSize; 
        this.cipherFileBufferSize = this.bufferSize; 
        this.outputFileBufferSize = this.bufferSize;
    }
    public void setInputFilesPathList(ArrayList<Path> inputFilesPathList)   { this.inputFilesPathList = inputFilesPathList; }
    public void setCipherFilePath(Path cipherFilePath)                      { this.cipherFilePath = cipherFilePath; }
    public void setOutputFilePath(Path outputFilePath)                      { this.outputFilePath = outputFilePath; }
        
    public void encryptSelection(ArrayList<Path> inputFilesPathList, Path cipherFilePath)
    {
        Stats allDataStats = new Stats(); allDataStats.reset();
        
        Stat readInputFileStat = new Stat(); readInputFileStat.reset();
        Stat readCipherFileStat = new Stat(); readCipherFileStat.reset();
        Stat writeOutputFileStat = new Stat(); writeOutputFileStat.reset();
        Stat readOutputFileStat = new Stat(); readOutputFileStat.reset();
        Stat writeInputFileStat = new Stat(); writeInputFileStat.reset();
        
        stopPending = false;
        pausing = false;

        // Get TOTALS
        allDataStats.setFilesTotal(inputFilesPathList.size());
        for (Path inputFilePath:inputFilesPathList) { try { if (! Files.isDirectory(inputFilePath)) { allDataStats.addAllDataBytesTotal(Files.size(inputFilePath)); }  } catch (IOException ex) { ui.error("Error: encryptFiles () filesBytesTotal += Files.size(inputFilePath); "+ ex.getLocalizedMessage() + "\n"); }} 
        ui.status(allDataStats.getStartSummary(Mode.getDescription()), true);
        try { Thread.sleep(100); } catch (InterruptedException ex) {  }
        
//      Setup the Progress TIMER & TASK
        updateProgressTimeline = new Timeline(new KeyFrame( Duration.millis(200), ae ->
            ui.encryptionProgress
            (
                (int) ((readInputFileStat.getFileBytesProcessed() + 
                        readCipherFileStat.getFileBytesProcessed() + 
                        writeOutputFileStat.getFileBytesProcessed() + 
                        readOutputFileStat.getFileBytesProcessed() + 
                        writeInputFileStat.getFileBytesProcessed()) / ( (allDataStats.getFileBytesTotal() * 5 ) / 100.0)),
                (int) ((allDataStats.getFilesBytesProcessed() * 5) / ( (allDataStats.getFilesBytesTotal() * 5 ) / 100.0))
            )        )); updateProgressTimeline.setCycleCount(Animation.INDEFINITE); updateProgressTimeline.play();

//      Start Files Encryption Clock
        allDataStats.setAllDataStartNanoTime();
        
        // Encrypt Files loop
        fileloop: for (Path inputFilePath:inputFilesPathList)
        {
            long filesize = 0; try { Files.size(inputFilePath); } catch (IOException ex) { ui.error("\nError: Files.size(inputFilePath); " + ex.getMessage() + "\n"); continue fileloop; }
            if (stopPending) { inputFileEnded = true; break fileloop; }
            if (! Files.isDirectory(inputFilePath))
            {
                if ((inputFilePath.compareTo(cipherFilePath) != 0))
                {
//                  Status    
                    ui.log("Processing: " + inputFilePath.toAbsolutePath() + " ");

                    if ( ! dry ) // Real run passes this point
                    {
                        String prefix = new String("bit");
                        String suffix = new String(".bit");
                        String extension = new String("");
                        int lastDotPos = inputFilePath.getFileName().toString().lastIndexOf('.'); // -1 no extension
                        int lastPos = inputFilePath.getFileName().toString().length();
                        if (lastDotPos != -1) { extension = inputFilePath.getFileName().toString().substring(lastDotPos, lastPos); } else { extension = ""; }

                        if (!extension.equals(suffix))  { outputFilePath = inputFilePath.resolveSibling(inputFilePath.getFileName().toString() + suffix); }   // Add    .bit
                        else                            { outputFilePath = inputFilePath.resolveSibling(inputFilePath.getFileName().toString().replace(suffix, "")); }               // Remove .bit


                        try { Files.deleteIfExists(outputFilePath); } catch (IOException ex) { ui.error("Error: Files.deleteIfExists(outputFilePath): " + ex + "\n"); }

                        ui.status("Encrypting: " + inputFilePath.toAbsolutePath() + " ", false);

                        // Prints printByte Header ones                
                        if ( print )
                        {
                            ui.log("\n");
                            ui.log(" ----------------------------------------------------------------------\n");
                            ui.log("|          |       Input       |      Cipher       |      Output       |\n");
                            ui.log("| ---------|-------------------|-------------------|-------------------|\n");
                            ui.log("| adr      | bin      hx dec c | bin      hx dec c | bin      hx dec c |\n");
                            ui.log("|----------|-------------------|-------------------|-------------------|\n");
                        }

    //                  Encryptor I/O Block
                        inputFileEnded = false;
                        long readInputFileChannelPosition = 0;
                        long readInputFileChannelTransfered = 0;
                        long readCipherFileChannelPosition = 0;                
                        long readCipherFileChannelTransfered = 0;                
                        long writeOutputFileChannelPosition = 0;
                        long writeOutputFileChannelTransfered = 0;

                        long readOutputFileChannelPosition = 0;
                        long readOutputFileChannelTransfered = 0;
                        long writeInputFileChannelPosition = 0;
                        long writeInputFileChannelTransfered = 0;
                        ByteBuffer inputFileBuffer =   ByteBuffer.allocate(inputFileBufferSize);  inputFileBuffer.clear();
                        ByteBuffer cipherFileBuffer =  ByteBuffer.allocate(cipherFileBufferSize); cipherFileBuffer.clear();
                        ByteBuffer outputFileBuffer =  ByteBuffer.allocate(outputFileBufferSize); outputFileBuffer.clear();

                        // Get and set the stats
                        try { allDataStats.setFileBytesTotal(Files.size(inputFilePath)); } catch (IOException ex) { ui.error("\nError: Files.size(inputFilePath); " + ex.getMessage() + "\n"); continue fileloop; }

                        readInputFileStat.setFileBytesProcessed(0);
                        readCipherFileStat.setFileBytesProcessed(0);
                        writeOutputFileStat.setFileBytesProcessed(0);
                        readOutputFileStat.setFileBytesProcessed(0);
                        writeInputFileStat.setFileBytesProcessed(0);

                        // Open and close files after every bufferrun. Interrupted file I/O works much faster than below uninterrupted I/O encryption
                        while ( ! inputFileEnded )
                        {
                            if (stopPending)
                            {
    //                          Delete broken outputFile and keep original
                                try { Files.deleteIfExists(outputFilePath); } catch (IOException ex) { ui.error("\nFiles.deleteIfExists(outputFilePath): " + ex + "\n"); }
                                inputFileEnded = true; ui.status("\n", true); break fileloop;
                            }

                            //open inputFile
                            readInputFileStat.setFileStartEpoch(); // allFilesStats.setFilesStartNanoTime();
                            try (final SeekableByteChannel readInputFileChannel = Files.newByteChannel(inputFilePath, EnumSet.of(StandardOpenOption.READ)))
                            {
                                // Fill up inputFileBuffer
                                readInputFileChannel.position(readInputFileChannelPosition);
                                readInputFileChannelTransfered = readInputFileChannel.read(inputFileBuffer); inputFileBuffer.flip(); readInputFileChannelPosition += readInputFileChannelTransfered;
                                if (( readInputFileChannelTransfered == -1 ) || ( inputFileBuffer.limit() < inputFileBufferSize )) { inputFileEnded = true; } // Buffer.limit = remainder from current position to end
                                readInputFileChannel.close(); readInputFileStat.setFileEndEpoch(); readInputFileStat.clock();
                                readInputFileStat.addFileBytesProcessed(readInputFileChannelTransfered); allDataStats.addAllDataBytesProcessed(readInputFileChannelTransfered / 2);
                            } catch (IOException ex) { ui.error("\nFiles.newByteChannel(inputFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex + "\n"); continue fileloop; }
    //                        ui.log("readInputFileChannelTransfered: " + readInputFileChannelTransfered + " inputFileBuffer.limit(): " + Integer.toString(inputFileBuffer.limit()) + "\n");

                            if ( readInputFileChannelTransfered != -1 )
                            {
                                readCipherFileStat.setFileStartEpoch();
                                try (final SeekableByteChannel readCipherFileChannel = Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)))
                                {
                                    // Fill up cipherFileBuffer
                                    readCipherFileChannel.position(readCipherFileChannelPosition);
                                    readCipherFileChannelTransfered = readCipherFileChannel.read(cipherFileBuffer); readCipherFileChannelPosition += readCipherFileChannelTransfered;
                                    if ( readCipherFileChannelTransfered < cipherFileBufferSize ) { readCipherFileChannelPosition = 0; readCipherFileChannel.position(0); readCipherFileChannelTransfered += readCipherFileChannel.read(cipherFileBuffer); readCipherFileChannelPosition += readCipherFileChannelTransfered;}
                                    cipherFileBuffer.flip();
                                    readCipherFileChannel.close(); readCipherFileStat.setFileEndEpoch(); readCipherFileStat.clock();
                                    readCipherFileStat.addFileBytesProcessed(readCipherFileChannelTransfered);
                                } catch (IOException ex) { ui.error("\nFiles.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex + "\n"); continue fileloop; }
    //                            ui.log("readCipherFileChannelTransfered: " + readCipherFileChannelTransfered + " cipherFileBuffer.limit(): " + Integer.toString(cipherFileBuffer.limit()) + "\n");

                                // Open outputFile for writing
                                writeOutputFileStat.setFileStartEpoch();
                                try (final SeekableByteChannel writeOutputFileChannel = Files.newByteChannel(outputFilePath, EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC)))
                                {
                                    // Encrypt inputBuffer and fill up outputBuffer
                                    outputFileBuffer = encryptBuffer(inputFileBuffer, cipherFileBuffer);
                                    writeOutputFileChannelTransfered = writeOutputFileChannel.write(outputFileBuffer); outputFileBuffer.flip(); writeOutputFileChannelPosition += writeOutputFileChannelTransfered;
                                    if (txt) { logByteBuffer("DB", inputFileBuffer); logByteBuffer("CB", cipherFileBuffer); logByteBuffer("OB", outputFileBuffer); }
                                    writeOutputFileChannel.close(); writeOutputFileStat.setFileEndEpoch(); writeOutputFileStat.clock();
                                    writeOutputFileStat.addFileBytesProcessed(writeOutputFileChannelTransfered);
                                } catch (IOException ex) { ui.error("\noutputFileChannel = Files.newByteChannel(outputFilePath, EnumSet.of(StandardOpenOption.WRITE)) " + ex + "\n"); continue fileloop; }
    //                            ui.log("writeOutputFileChannelTransfered: " + writeOutputFileChannelTransfered + " outputFileBuffer.limit(): " + Integer.toString(outputFileBuffer.limit()) + "\n\n");
                            }
                            outputFileBuffer.clear(); inputFileBuffer.clear(); cipherFileBuffer.clear();
                        }

    //                  Counting encrypting and shredding for the average throughtput performance

    //                  Shredding process

                        ui.status("Shredding: " + inputFilePath.toAbsolutePath() + " ", false);

                        inputFileEnded = false;
                        readInputFileChannelPosition = 0;
                        readInputFileChannelTransfered = 0;
                        readCipherFileChannelPosition = 0;                
                        readCipherFileChannelTransfered = 0;                
                        writeOutputFileChannelPosition = 0;

                        inputFileBuffer =   ByteBuffer.allocate(inputFileBufferSize);  inputFileBuffer.clear();
                        cipherFileBuffer =  ByteBuffer.allocate(cipherFileBufferSize); cipherFileBuffer.clear();
                        outputFileBuffer =  ByteBuffer.allocate(outputFileBufferSize); outputFileBuffer.clear();

                        shredloop: while ( ! inputFileEnded )
                        {
                            while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
                            if (stopPending)    { inputFileEnded = true; break shredloop; }

                            //read outputFile
                            readOutputFileStat.setFileStartEpoch();
                            try (final SeekableByteChannel readOutputFileChannel = Files.newByteChannel(outputFilePath, EnumSet.of(StandardOpenOption.READ)))
                            {
                                readOutputFileChannel.position(readOutputFileChannelPosition);
                                readOutputFileChannelTransfered = readOutputFileChannel.read(outputFileBuffer); outputFileBuffer.flip(); readOutputFileChannelPosition += readOutputFileChannelTransfered;
                                if (( readOutputFileChannelTransfered == -1 ) || ( outputFileBuffer.limit() < outputFileBufferSize )) { inputFileEnded = true; }
                                readOutputFileChannel.close(); readOutputFileStat.setFileEndEpoch(); readOutputFileStat.clock();
                                readOutputFileStat.addFileBytesProcessed(outputFileBuffer.limit()); allDataStats.addAllDataBytesProcessed(outputFileBuffer.limit()/2);
                            } catch (IOException ex) { ui.error("\nFiles.newByteChannel(inputFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex + "\n"); continue fileloop; }
    //                        ui.log("readOutputFileChannelTransfered: " + readOutputFileChannelTransfered + " outputFileBuffer.limit(): " + Integer.toString( outputFileBuffer.limit()) + "\n");

                            //shred inputFile
                            if ( readOutputFileChannelTransfered != -1 )
                            {
                                writeInputFileStat.setFileStartEpoch();
                                try (final SeekableByteChannel writeInputFileChannel = Files.newByteChannel(inputFilePath, EnumSet.of(StandardOpenOption.WRITE,StandardOpenOption.SYNC)))
                                {
                                    // Fill up inputFileBuffer
                                    writeInputFileChannel.position(writeInputFileChannelPosition);
                                    writeInputFileChannelTransfered = writeInputFileChannel.write(outputFileBuffer); inputFileBuffer.flip(); writeInputFileChannelPosition += writeInputFileChannelTransfered;
                                    if (( writeInputFileChannelTransfered == -1 ) || ( outputFileBuffer.limit() < outputFileBufferSize )) { inputFileEnded = true; }
                                    writeInputFileChannel.close(); writeInputFileStat.setFileEndEpoch(); writeInputFileStat.clock();
                                    writeInputFileStat.addFileBytesProcessed(outputFileBuffer.limit());
                                } catch (IOException ex) { ui.error("\nFiles.newByteChannel(inputFilePath, EnumSet.of(StandardOpenOption.WRITE)) " + ex + "\n"); continue fileloop; }
    //                            ui.log("writeInputFileChannelTransfered: " + writeInputFileChannelTransfered + " outputFileBuffer.limit(): " + Integer.toString(outputFileBuffer.limit()) + "\n\n");
                            }
                            outputFileBuffer.clear(); inputFileBuffer.clear(); cipherFileBuffer.clear();
                        }

    //                  FILE STATUS        
                        ui.log("- Encrypt: rd(" +  readInputFileStat.getFileBytesThroughPut() + ") -> ");
                        ui.log("rd(" +           readCipherFileStat.getFileBytesThroughPut() + ") -> ");
                        ui.log("wr(" +           writeOutputFileStat.getFileBytesThroughPut() + ") ");
                        ui.log("- Shred: rd(" +    readOutputFileStat.getFileBytesThroughPut() + ") -> ");
                        ui.log("wr(" +           writeInputFileStat.getFileBytesThroughPut() + ") ");                                        
                        ui.log(allDataStats.getAllDataBytesProgressPercentage());
                        allDataStats.addFilesProcessed(1);

                        if ( print ) { ui.log(" ----------------------------------------------------------------------\n"); }

    //                  Print the stats

                        BasicFileAttributes battr = null;
                        PosixFileAttributes pattr = null;
                        DosFileAttributes dattr = null;

    //                  Read inputFilePath attributes                            
                        if ( System.getProperty("os.name").toLowerCase().startsWith("win") ) { try { dattr = Files.readAttributes(inputFilePath, DosFileAttributes.class); } catch (IOException e) { ui.error(e.getMessage()); } }
                        else                                                                 { try { pattr = Files.readAttributes(inputFilePath, PosixFileAttributes.class); } catch (IOException e) { ui.error(e.getMessage()); } }

    //                  Write outputFilePath attributes                           
                        if ( System.getProperty("os.name").toLowerCase().startsWith("win") )
                        {
                            try
                            {
                                Files.setAttribute(outputFilePath, "basic:lastModifiedTime", dattr.lastModifiedTime());
                                Files.setAttribute(outputFilePath, "dos:hidden", dattr.isHidden());
                                Files.setAttribute(outputFilePath, "dos:system", dattr.isSystem());
                                Files.setAttribute(outputFilePath, "dos:readonly", dattr.isReadOnly());
                                Files.setAttribute(outputFilePath, "dos:archive", dattr.isArchive());
                            } catch (IOException ex) { ui.error("Error: Set DOS Attributes: " + ex + "\n"); }
                        }
                        else
                        {
                            try
                            {
                                Files.setAttribute(outputFilePath, "posix:owner", pattr.owner());
                                Files.setAttribute(outputFilePath, "posix:group", pattr.group());
                                Files.setPosixFilePermissions(outputFilePath, pattr.permissions());
                                Files.setLastModifiedTime(outputFilePath, pattr.lastModifiedTime());
                            } catch (IOException ex) { ui.error("Error: Set POSIX Attributes: " + ex + "\n"); }
                        }	
    //                  Delete the original
                        long inputfilesize = 0;  try { inputfilesize = Files.size(inputFilePath); }   catch (IOException ex)            { ui.error("\nError: Files.size(inputFilePath): " + ex.getMessage() + "\n"); continue fileloop; }
                        long outputfilesize = 0; try { outputfilesize = Files.size(outputFilePath); } catch (IOException ex)            { ui.error("\nError: Files.size(outputFilePath): " + ex.getMessage() + "\n"); continue fileloop; }
                        if ( (inputfilesize != 0 ) && ( outputfilesize != 0 ) && ( inputfilesize == outputfilesize ) ) { try { Files.deleteIfExists(inputFilePath); } catch (IOException ex)    { ui.error("\nFiles.deleteIfExists(inputFilePath): " + ex.getMessage() + "\n"); continue fileloop; } }
                    } else { ui.log("\n"); } // End real run
                } else { ui.error(inputFilePath.toAbsolutePath() + " ignoring:   " + cipherFilePath.toAbsolutePath() + " (is cipher!)\n"); }
            } else { ui.error("Skipping directory: " + inputFilePath.getFileName() + "\n"); } // End "not a directory"
            
        } // Encrypt Files Loop
        allDataStats.setAllDataEndNanoTime(); allDataStats.clock();
        if ( stopPending ) { ui.status("\n", false); stopPending = false;  } // It breaks in the middle of encrypting, so the encryption summery needs to begin on a new line
        ui.status(allDataStats.getEndSummary(Mode.getDescription()), true);

//        updateProgressTaskTimer.cancel(); updateProgressTaskTimer.purge();
        updateProgressTimeline.stop();
        ui.encryptionFinished();
    }
    
    private ByteBuffer encryptBuffer(ByteBuffer inputFileBuffer, ByteBuffer cipherFileBuffer)
    {
        int inputTotal = 0;
        int cipherTotal = 0;
        int outputDiff = 0;
        byte inputByte = 0;
        byte cipherByte = 0;
        byte outputByte;
        
        ByteBuffer outputFileBuffer =   ByteBuffer.allocate(outputFileBufferSize); outputFileBuffer.clear();
        while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
        for (int inputFileBufferCount = 0; inputFileBufferCount < inputFileBuffer.limit(); inputFileBufferCount++)
        {
            inputTotal += inputByte;
            cipherTotal += cipherByte;
            inputByte = inputFileBuffer.get(inputFileBufferCount);
            cipherByte = cipherFileBuffer.get(inputFileBufferCount);
            outputByte = encryptByte(inputFileBuffer.get(inputFileBufferCount), cipherFileBuffer.get(inputFileBufferCount));
            outputFileBuffer.put(outputByte);
        }
        outputFileBuffer.flip();
        // MD5Sum dataTotal XOR MD5Sum cipherTotal (Diff dataTot and cipherTot) 32 bit 4G
        
        outputDiff = inputTotal ^ cipherTotal;
        
//        if (debug)
//        {
//            ui.log(Integer.toString(inputTotal) + "\n");
//            ui.log(Integer.toString(cipherTotal) + "\n");
//            ui.log(Integer.toString(outputDiff) + "\n");
//        MD5Converter.getMD5SumFromString(Integer.toString(dataTotal));
//        MD5Converter.getMD5SumFromString(Integer.toString(cipherTotal));
//        }
        
        return outputFileBuffer;
    }
    
    private byte encryptByte(final byte dataByte, byte cipherByte)
    {
        int dum = 0;  // DUM Data Unnegated Mask
        int dnm = 0;  // DNM Data Negated Mask
        int dbm = 0;  // DBM Data Blended Mask
        byte outputByte;
        
//      Negate 0 cipherbytes to prevent 0 encryption
        if (cipherByte == 0) { cipherByte = (byte)(~cipherByte & 0xFF); }

        dum = dataByte & ~cipherByte;
        dnm = ~dataByte & cipherByte;
        dbm = dum + dnm; // outputByte        
        outputByte = (byte)(dbm & 0xFF);
        
        if ( print )    { logByte(dataByte, cipherByte, outputByte, dum, dnm, dbm); }
        if ( bin )      { logByteBinary(dataByte, cipherByte, outputByte, dum, dnm, dbm); }
        if ( dec )      { logByteDecimal(dataByte, cipherByte, outputByte, dum, dnm, dbm); }
        if ( hex )      { logByteHexaDecimal(dataByte, cipherByte, outputByte, dum, dnm, dbm); }
        if ( chr )      { logByteChar(dataByte, cipherByte, outputByte, dum, dnm, dbm); }

        // Increment Byte Progress Counters        
        return (byte)dbm; // outputByte
    }

//  Recursive Deletion of PathList
    public void deleteSelection(ArrayList<Path> inputFilesPathList, boolean delete, boolean returnpathlist, String pattern, boolean negatePattern)
    {
        EnumSet opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS); //follow links
        MySimpleFileVisitor mySimpleFileVisitor = new MySimpleFileVisitor(ui, verbose, delete, symlink, returnpathlist, pattern, negatePattern);
        for (Path path:inputFilesPathList)
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
        ui.log(" " + preFix + "Z: " + byteBuffer.limit() + "\n");
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
        ui.log                 (outbin + " " +  outhex + " " + outdec + " " + outchr + " |\n");
        printAddressByteCounter++;
    }
    
    private void logByteBinary(byte inputByte, byte cipherByte, byte outputByte, int dum, int dnm, int dbm)
    {
        ui.log("\n");
        ui.log("Input  = " + getBinaryString(inputByte) + "\n");
        ui.log("Cipher = " + getBinaryString(cipherByte) + "\n");
        ui.log("Output = " + getBinaryString(outputByte) + "\n");
        ui.log("\n");
        ui.log("DUM  = " + getBinaryString((byte)inputByte) + " & " + getBinaryString((byte)~cipherByte) + " = " + getBinaryString((byte)dum) + "\n");
        ui.log("DNM  = " + getBinaryString((byte)~inputByte) + " & " + getBinaryString((byte)cipherByte) + " = " + getBinaryString((byte)dnm) + "\n");
        ui.log("DBM  = " + getBinaryString((byte)dum) + " & " + getBinaryString((byte)dnm) + " = " + getBinaryString((byte)dbm) + "\n");
    }
    
    private void logByteDecimal(byte dataByte, byte cipherByte, byte outputByte, int dum, int dnm, int dbm)
    {
        ui.log("\n");
        ui.log("Input  = " + getDecString(dataByte) + "\n");
        ui.log("Cipher = " + getDecString(cipherByte) + "\n");
        ui.log("Output = " + getDecString(outputByte) + "\n");
        ui.log("\n");
        ui.log("DUM  = " + getDecString((byte)dataByte) + " & " + getDecString((byte)~cipherByte) + " = " + getDecString((byte)dum) + "\n");
        ui.log("DNM  = " + getDecString((byte)~dataByte) + " & " + getDecString((byte)cipherByte) + " = " + getDecString((byte)dnm) + "\n");
        ui.log("DBM  = " + getDecString((byte)dum) + " & " + getDecString((byte)dnm) + " = " + getDecString((byte)dbm) + "\n");
    }
    
    private void logByteHexaDecimal(byte dataByte, byte cipherByte, byte outputByte, int dum, int dnm, int dbm)
    {
        ui.log("\n");
        ui.log("Input  = " + getHexString(dataByte,"2") + "\n");
        ui.log("Cipher = " + getHexString(cipherByte,"2") + "\n");
        ui.log("Output = " + getHexString(outputByte,"2") + "\n");
        ui.log("\n");
        ui.log("DUM  = " + getHexString((byte)dataByte,"2") + " & " + getHexString((byte)~cipherByte,"2") + " = " + getHexString((byte)dum,"2") + "\n");
        ui.log("DNM  = " + getHexString((byte)~dataByte,"2") + " & " + getHexString((byte)cipherByte,"2") + " = " + getHexString((byte)dnm,"2") + "\n");
        ui.log("DBM  = " + getHexString((byte)dum,"2") + " & " + getHexString((byte)dnm,"2") + " = " + getHexString((byte)dbm,"2") + "\n");
    }
    
    private void logByteChar(byte dataByte, byte cipherByte, byte outputByte, int dum, int dnm, int dbm)
    {
        ui.log("\n");
        ui.log("Input  = " + getChar(dataByte) + "\n");
        ui.log("Cipher = " + getChar(cipherByte) + "\n");
        ui.log("Output = " + getChar(outputByte) + "\n");
        ui.log("\n");
        ui.log("DUM  = " + getChar((byte)dataByte) + " & " + getChar((byte)~cipherByte) + " = " + getChar((byte)dum) + "\n");
        ui.log("DNM  = " + getChar((byte)~dataByte) + " & " + getChar((byte)cipherByte) + " = " + getChar((byte)dnm) + "\n");
        ui.log("DBM  = " + getChar((byte)dum) + " & " + getChar((byte)dnm) + " = " + getChar((byte)dbm) + "\n");
    }
    
    public static boolean isValidDir(UI ui, Path path, boolean symlink, boolean report)
    {
        boolean validdir = true; String conditions = "";    String exist = ""; String read = ""; String write = ""; String symbolic = "";
        if ( ! Files.exists(path))                          { validdir = false; exist = "[not found] "; conditions += exist; }
        if ( ! Files.isReadable(path) )                     { validdir = false; read = "[not readable] "; conditions += read;  }
        if ( ! Files.isWritable(path) )                     { validdir = false; write = "[not writable] "; conditions += write;  }
        if ( (! symlink) && (Files.isSymbolicLink(path)) )  { validdir = false; symbolic = "[symlink]"; conditions += symbolic;  }
        if ( validdir ) {  } else { if ( report ) { ui.error("Warning: Invalid Dir: " + path.toString() + ": " + conditions + "\n"); } }
        return validdir;
    }

    public static boolean isValidFile(UI ui, Path path, boolean symlink, boolean report)
    {
        boolean validfile = true; String conditions = "";   String size = ""; String exist = ""; String read = ""; String write = ""; String symbolic = "";
        long fileSize = 0; try                              { fileSize = Files.size(path); } catch (IOException ex) { }

        if ( ! Files.exists(path))                          { validfile = false; exist = "[not found] "; conditions += exist; }
        if ( fileSize == 0 )                                { validfile = false; size = "[empty] "; conditions += size; }
        if ( ! Files.isReadable(path) )                     { validfile = false; read = "[not readable] "; conditions += read; }
        if ( ! Files.isWritable(path) )                     { validfile = false; write = "[not writable] "; conditions += write; }
        if ( (! symlink) && (Files.isSymbolicLink(path)) )  { validfile = false; symbolic = "[symlink]"; conditions += symbolic; }
        if ( ! validfile ) { if ( report ) { ui.error("Warning: Invalid File: " + path.toAbsolutePath().toString() + ": " + conditions + "\n"); } }                    
        return validfile;
    }

    public ArrayList<Path> getPathList(File[] files)
    {
        // Converts from File[] to ArraayList<Path>
        ArrayList<Path> pathList = new ArrayList<>(); for (File file:files) { pathList.add(file.toPath()); }
        return pathList;
    }
    
//  Called by EncryptSelected GUIFX
    public ArrayList<Path> getExtendedPathList(File[] files, Path cipherPath, String pattern, boolean negatePattern, boolean status)
    {
        // Converts from File[] to ArraayList<Path> where as every dir is converted into additional PathLists
        ArrayList<Path> pathList = new ArrayList<>();
        if ( cipherPath == null )
        {
            for (File file:files)
            {
                if (file.isDirectory())
                {
                    for (Path path:getDirectoryPathList(file, pattern, negatePattern))
                    {
                        pathList.add(path);
                    } 
                }
                else
                {
                    if (isValidFile(ui, file.toPath(), symlink, verbose) ) { pathList.add(file.toPath()); }
                }
            }
        }
        else
        {
            for (File file:files)
            {
                if (file.isDirectory())
                {
                    for (Path path:getDirectoryPathList(file, pattern, negatePattern))
                    {
                        if ( ((path.compareTo(cipherPath) != 0)) )
                        {
                            pathList.add(path);
                        } else { if (status) { ui.status("Warning: cipher-file: " + cipherPath.toAbsolutePath() + " will be excluded!\n", false); }}
                    } 
                }
                else
                {
                    if ( ((file.toPath().compareTo(cipherPath) != 0)) )
                    {
                        if (isValidFile(ui, file.toPath(), symlink, verbose) ) { pathList.add(file.toPath()); }
                    }
                    else { if (status) { ui.status("Warning: cipher-file: " + cipherPath.toAbsolutePath() + " will be excluded!\n", false); }}
                }
            }
        }
        return pathList;
    }

//  Called by EncryptSelected CLUI (works with PathList instead of File[] because FileChooser produces File[] array)
    public ArrayList<Path> getExtendedPathList(ArrayList<Path> userSelectedItemsPathList, Path cipherPath, String pattern, boolean negatePattern, boolean status)
    {
        // Converts from File[] to ArraayList<Path> where as every dir is converted into additional PathLists
        ArrayList<Path> recursivePathList = new ArrayList<>();
        if ( cipherPath == null )
        {
            for (Path outerpath:userSelectedItemsPathList)
            {
                if ( Files.isDirectory(outerpath) )
                {
                    for (Path path:getDirectoryPathList(outerpath.toFile(), pattern, negatePattern))
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
                    for (Path subItemPath:getDirectoryPathList(userSelectedItemPath.toFile(), pattern, negatePattern))
                    {
                        // cipherdetection not shown?
                        if ( ((subItemPath.toAbsolutePath().compareTo(cipherPath.toAbsolutePath()) != 0)) ) { recursivePathList.add(subItemPath); } else { if (status) { ui.status("Warning: cipher-file: " + cipherPath.toAbsolutePath() + " will be excluded!\n", true); }}
                    }
                }
                else
                {
                    if ( ((userSelectedItemPath.compareTo(cipherPath) != 0)) ) { recursivePathList.add(userSelectedItemPath); } else { if (status) { ui.status("Warning: cipher-file: " + cipherPath.toAbsolutePath() + " will be excluded!\n", true); }}
                }
            }
        }
        return recursivePathList;
    }

    // Used by getExtendedPathList(File[] files)
    public ArrayList<Path> getDirectoryPathList(File file, String pattern, boolean negatePattern)
    {
        // Converts from File[] to ArraayList<Path> where as every dir is converted into additional PathLists
        ArrayList<Path> recursivePathList = new ArrayList<>();
        
        EnumSet opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS); //follow links
        MySimpleFileVisitor mySimpleFileVisitor = new MySimpleFileVisitor(ui, verbose, false, symlink, true, pattern, negatePattern);
        try{Files.walkFileTree(file.toPath(), opts, Integer.MAX_VALUE, mySimpleFileVisitor);} catch(IOException e){System.err.println(e);}
        recursivePathList = mySimpleFileVisitor.getPathList();

        return recursivePathList;
    }

//    public Stats getStats()                                 { return stats; }

//  Class Extends Thread
    @Override
    @SuppressWarnings("empty-statement")
    public void run()
    {
    }
}

// override only methods of our need (SimpleFileVisitor is a full blown class)
class MySimpleFileVisitor extends SimpleFileVisitor<Path>
{
    private final UI ui;
    private final PathMatcher pathMatcher;
    private final boolean verbose; 
    private final boolean delete; 
    private final boolean symlink; 
    private final boolean returnpathlist; 
    private final ArrayList<Path> pathList;
    private boolean negatePattern;

//  Default CONSTRUCTOR

//  regex pattern
//  all *.bit   =   'regex:^.*\.bit$'
//  all but *.bit   'regex:(?!.*\.bit$)^.*$'
    
    public MySimpleFileVisitor(UI ui, boolean verbose, boolean delete, boolean symlink, boolean returnpathlist, String pattern, boolean negatePattern)
    {
        this.ui = ui;
        pathMatcher = FileSystems.getDefault().getPathMatcher(pattern); // "glob:" or "regex:" included in pattern
        this.delete = delete;
        this.verbose = verbose;
        this.symlink = symlink;
        this.returnpathlist = returnpathlist;
        pathList = new ArrayList<Path>();
        this.negatePattern = negatePattern;
    }
   
    @Override public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs)
    {
        if ( FinalCrypt.isValidDir(ui, path, symlink, verbose) ) { return FileVisitResult.CONTINUE; } else { return FileVisitResult.SKIP_SUBTREE; }
    }
    
    @Override public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
    {
        long fileSize = 0; try { fileSize = Files.size(path); } catch (IOException ex) { }
        if (!negatePattern)
        {
            if ( (path.getFileName() != null ) && ( pathMatcher.matches(path.getFileName())) )
            {            
                if (delete)                 { try { Files.delete(path); } catch (IOException ex) { ui.error("Error: visitFile(.. ) Failed file: " + path.toString() + " due to: " + ex.getMessage() + "\n"); } }
                else if (returnpathlist)    
                {
                    if (FinalCrypt.isValidFile(ui, path, symlink, verbose)) { pathList.add(path); } 
                }
                else { ui.status("Huh? this shouldn't have happened.\n", true); }
            }   
        }
        else
        {
            if ( (path.getFileName() != null ) && ( ! pathMatcher.matches(path.getFileName())) ) // Negate Pattern; Does NOT match pattern
            {
                if (delete)                 { try { Files.delete(path); } catch (IOException ex) { ui.error("Error: visitFile(.. ) Failed file: " + path.toString() + " due to: " + ex.getMessage() + "\n"); } }
                else if (returnpathlist)
                {
                    if (FinalCrypt.isValidFile(ui, path, symlink, verbose)) { pathList.add(path); } 
                }
                else  { ui.status("Huh? this shouldn't have happened.\n", true); }
            }   
        }
        return FileVisitResult.CONTINUE;
    }
    
    @Override public FileVisitResult visitFileFailed(Path path, IOException exc)
    {
        ui.error("Warning: Skip File: " + path.toAbsolutePath().toString() + ": " + exc + "\n");
        return FileVisitResult.SKIP_SIBLINGS;
    }
    
    @Override public FileVisitResult postVisitDirectory(Path path, IOException exc)
    {
        if      (delete)            { try { Files.delete(path); } catch (IOException ex) { ui.error("Error: postVisitDirectory: " + path.toString() + " due to: " + ex.getMessage() + "\n"); } }
        else if (returnpathlist)    {        }
        else                        {     }
        return FileVisitResult.CONTINUE;
    }
    
    public ArrayList<Path> getPathList() { return pathList; }
}
