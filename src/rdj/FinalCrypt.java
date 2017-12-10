/*
 * Copyright (C) 2017 ron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author Ron de Jong ronuitzaandam@gmail.com
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
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.TimerTask;

//public class FinalCrypt  extends SwingWorker
public class FinalCrypt  extends Thread
{
//    private static final String COMPANYNAME = "GPLv3";
//    private static final String PRODUCTNAME = "FinalCrypt";
//    private static final String AUTHOR = "Ron de Jong";
//    private static final String COPYRIGHT = "© Copyleft " + Calendar.getInstance().get(Calendar.YEAR);
//    private static final int    VERSION = 1;
//    private static final int    MAJOR = 1;
//    private static final int    MINOR = 0;
//    private static final String VERSIONSTRING = VERSION + "." + MAJOR + "." + MINOR;
    private boolean debug = false, verbose = false, print = false, txt = false, bin = false, dec = false, hex = false, chr = false;

    private final int bufferSizeDefault = (1 * 1024 * 1024); // 1MB BufferSize overall better performance
    private int bufferSize = 0; // Default 1MB
    private int inputFileBufferSize;
    private int cipherFileBufferSize;
    private int outputFileBufferSize;
    private long bufferTotal = 0; // DNumber of buffers

    private int printAddressByteCounter = 0;
//    public long fileBytesTotal = 0;
//    public long filesBytesTotal = 0;
//    private long fileBytesEncrypted = 0;
//    private long filesBytesEncrypted = 0;
    private ArrayList<Path> inputFilesPathList;
    private Path cipherFilePath = null;
    private Path outputFilePath = null;
    private final long inputFileSize = 0;
    private final long cipherFileSize = 0;
    private final long outputFileSize = 0;
    private final String encoding = System.getProperty("file.encoding");
    private final UI ui;
    private final FinalCrypt fc;
    
    private TimerTask updateProgressTask;
    private java.util.Timer updateProgressTaskTimer;
    private final Stats stats;

    private String localVersionString = "";
    private String remoteVersionString = "";
    private int localVersion = 0;
    private int remoteVersion = 0;
//        URL localURL = null;
    private InputStream istream = null;
    private URL remoteURL = null;
    private ReadableByteChannel rbc = null;
    private ByteBuffer byteBuffer;        
    

    public FinalCrypt(UI ui)
    {    
//        super("FinalCryptThread");
        
        
//        Set the locations of the version resources
        
        inputFilesPathList = new ArrayList<>();
        inputFileBufferSize = bufferSize;
        cipherFileBufferSize = bufferSize;
        outputFileBufferSize = bufferSize;        
        this.ui = ui;
        fc = this;
        stats = new Stats();
    }
        
    public int getBufferSize()                                              { return bufferSize; }
    
    public boolean getDebug()                                               { return debug; }
    public boolean getVerbose()                                             { return verbose; }
    public boolean getPrint()                                               { return print; }
    public boolean getTXT()                                                 { return txt; }
    public boolean getBin()                                                 { return bin; }
    public boolean getDec()                                                 { return dec; }
    public boolean getHex()                                                 { return hex; }
    public boolean getChr()                                                 { return chr; }
    public int getBufferSizeDefault()                                      { return bufferSizeDefault; }
    public ArrayList<Path> getInputFilesPathList()                          { return inputFilesPathList; }
    public Path getCipherFilePath()                                         { return cipherFilePath; }
    public Path getOutputFilePath()                                         { return outputFilePath; }
//    public long getFileBytesEncrypted()                                     { return fileBytesEncrypted; }
//    public long getFilesBytesEncrypted()                                    { return filesBytesEncrypted; }
//    public long getFileBytesTotal()                                         { return fileBytesTotal; }
//    public long getFilesBytesTotal()                                        { return filesBytesTotal; }
    
    public void setDebug(boolean debug)                                     { this.debug = debug; }
    public void setVerbose(boolean verbose)                                 { this.verbose = verbose; }
    public void setPrint(boolean print)                                     { this.print = print; }
    public void setTXT(boolean txt)                                         { this.txt = txt; }
    public void setBin(boolean bin)                                         { this.bin = bin; }
    public void setDec(boolean dec)                                         { this.dec = dec; }
    public void setHex(boolean hex)                                         { this.hex = hex; }
    public void setChr(boolean chr)                                         { this.chr = chr; }
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
        
        stats.reset();

        // Get files bytes total
        for (Path inputFilePath:inputFilesPathList) { try { if (! Files.isDirectory(inputFilePath)) { stats.addFilesBytesTotal(Files.size(inputFilePath)); }  } catch (IOException ex) { ui.error("Error: encryptFiles () filesBytesTotal += Files.size(inputFilePath); "+ ex.getLocalizedMessage() + "\n"); }} 

        stats.setFilesTotal(inputFilesPathList.size());
        ui.status(stats.getEncryptionStartSummary());
        
        // Setup the Progress timer & task
        updateProgressTask = new TimerTask() { @Override public void run() { ui.encryptionProgress( (int) (stats.getFileBytesEncrypted() /( stats.getFileBytesTotal()/100.0)), (int) (stats.getFilesBytesEncrypted() /(stats.getFilesBytesTotal()/100.0))); }};
        updateProgressTaskTimer = new java.util.Timer(); updateProgressTaskTimer.schedule(updateProgressTask, 0L, 50);

//      Start Files Encryption Clock
        stats.setFilesStartEpoch();
        
        // Encrypt Files loop
        fileloop: for (Path inputFilePath:inputFilesPathList)
        {
            if (! Files.isDirectory(inputFilePath))
            {
                if ((inputFilePath.compareTo(cipherFilePath) != 0))
                {
                    stats.setFileBytesEncrypted(0);

                    // Get the filesize total
                    try { stats.setFileBytesTotal(Files.size(inputFilePath)); } catch (IOException ex) { ui.error("Error: encryptFiles () fileBytesTotal += Files.size(inputFilePath); "+ ex.getLocalizedMessage() + "\n"); }
                    if (verbose) { ui.log("Inputfile: " + inputFilePath.getFileName() + " size: " + stats.getFileBytesTotal() + " bytes\n"); }

                    String prefix = new String("bit");
                    String suffix = new String(".bit");
                    String extension = new String("");
                    int lastDotPos = inputFilePath.getFileName().toString().lastIndexOf('.'); // -1 no extension
                    int lastPos = inputFilePath.getFileName().toString().length();
                    if (lastDotPos != -1) { extension = inputFilePath.getFileName().toString().substring(lastDotPos, lastPos); } else { extension = ""; }

//                    outputFilePath = inputFilePath.resolveSibling(inputFilePath.getFileName().toString().replace(extension, ".") + prefix + extension + suffix);
                    if (!extension.equals(suffix))  { outputFilePath = inputFilePath.resolveSibling(inputFilePath.getFileName().toString() + suffix); }   // Add    .bit
                    else                            { outputFilePath = inputFilePath.resolveSibling(inputFilePath.getFileName().toString().replace(suffix, "")); }               // Remove .bit

                    
                    try { Files.deleteIfExists(outputFilePath); } catch (IOException ex) { ui.error("Error: Files.deleteIfExists(outputFilePath): " + ex + "\n"); }

    //              Status
    
        
//                    ui.status("Encrypting file: " + inputFilePath.getFileName() + " with cipherfile: " + cipherFilePath.getFileName() + "\n");
                    ui.status(cipherFilePath.toAbsolutePath() + " encrypting: " + inputFilePath.toAbsolutePath() + " ");

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

                    boolean inputFileEnded = false;
                    long inputFileChannelPos = 0;
                    long cipherFileChannelPos = 0;                
                    long cipherFileChannelRead = 0;                
                    final ByteBuffer inputFileBuffer =  ByteBuffer.allocate(inputFileBufferSize);  inputFileBuffer.clear();
                    final ByteBuffer cipherFileBuffer = ByteBuffer.allocate(cipherFileBufferSize); cipherFileBuffer.clear();
                    
                    stats.setFileStartEpoch();

                    // Open and close files after every bufferrun. Interrupted file I/O works much faster than below uninterrupted I/O encryption
                    while ( ! inputFileEnded )
                    {
                        //open inputFile
                        try (final SeekableByteChannel inputFileChannel = Files.newByteChannel(inputFilePath, EnumSet.of(StandardOpenOption.READ)))
                        {
                            // Fill up inputFileBuffer
                            inputFileChannel.position(inputFileChannelPos); if (debug & verbose) { ui.println("\nInput  Channel Pos: " + Long.toString(inputFileChannelPos)); }
                            inputFileChannelPos += inputFileChannel.read(inputFileBuffer); inputFileBuffer.flip(); //inputFileChannelPos = inputFileChannel.position();
                            if (( inputFileChannelPos == -1 ) || ( inputFileBuffer.limit() < inputFileBufferSize )) { inputFileEnded = true; }
                            inputFileChannel.close();
                            
                        } catch (IOException ex) { ui.error("Files.newByteChannel(inputFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex + "\n"); continue fileloop; }

                        // Open cipherFile
                        try (final SeekableByteChannel cipherFileChannel = Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)))
                        {
                            // Fill up cipherFileBuffer
                            cipherFileChannel.position(cipherFileChannelPos); if (debug & verbose) { ui.println("Cipher Channel Pos: " + Long.toString(cipherFileChannelPos)); }
                            cipherFileChannelRead = cipherFileChannel.read(cipherFileBuffer); cipherFileChannelPos += cipherFileChannelRead;//cipherFileChannelPos = cipherFileChannel.position();
                            if ( cipherFileChannelRead < cipherFileBufferSize ) { cipherFileChannelPos = 0; cipherFileChannel.position(0); cipherFileChannelRead += cipherFileChannel.read(cipherFileBuffer); cipherFileChannelPos += cipherFileChannelRead;}
                            cipherFileBuffer.flip();

                            cipherFileChannel.close();
                        } catch (IOException ex) { ui.error("Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex + "\n"); continue fileloop; }

                        // Open outputFile for writing
                        try (final SeekableByteChannel outputFileChannel = Files.newByteChannel(outputFilePath, EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.APPEND)))
                        {
                            // Encrypt inputBuffer and fill up outputBuffer
                            ByteBuffer outputFileBuffer = encryptBuffer(inputFileBuffer, cipherFileBuffer);
                            outputFileChannel.write(outputFileBuffer);

                            if (txt) { logByteBuffer("DB", inputFileBuffer); logByteBuffer("CB", cipherFileBuffer); logByteBuffer("OB", outputFileBuffer); }

                            outputFileBuffer.clear();
                            inputFileBuffer.clear();
                            cipherFileBuffer.clear();

                            outputFileChannel.close();
                            
                        } catch (IOException ex) { ui.error("outputFileChannel = Files.newByteChannel(outputFilePath, EnumSet.of(StandardOpenOption.WRITE)) " + ex + "\n"); continue fileloop; }
                    }
                    if ( print ) { ui.log(" ----------------------------------------------------------------------\n"); }

                    stats.setFileEndEpoch();
//                    fileDiffEpoch = (( (double)fileBytesEncrypted / ((fileEndEpoch - fileStartEpoch)/1000f))/ 1000000f);
//                    String throughput = String.format("%.1f", fileDiffEpoch);
                    ui.status(stats.getFileBytesThroughPut());
                    stats.addFilesEncrypted(1);
                        
                    BasicFileAttributes battr = null;
                    PosixFileAttributes pattr = null;
                    DosFileAttributes dattr = null;

//                  Read inputFilePath attributes for outputFilePath                            
                    if ( System.getProperty("os.name").toLowerCase().startsWith("win") ) { try { dattr = Files.readAttributes(inputFilePath, DosFileAttributes.class); } catch (IOException e) { ui.error(e.getMessage()); } }
                    else                                                                 { try { pattr = Files.readAttributes(inputFilePath, PosixFileAttributes.class); } catch (IOException e) { ui.error(e.getMessage()); } }
                            
//                  Write inputFilePath attributes to outputFilePath                            
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
                    long inputfilesize = 0;  try { inputfilesize = Files.size(inputFilePath); }   catch (IOException ex) { ui.error("Error: Files.size(inputFilePath): " + ex + "\n"); }
                    long outputfilesize = 0; try { outputfilesize = Files.size(outputFilePath); } catch (IOException ex) { ui.error("Error: Files.size(outputFilePath): " + ex + "\n"); }
                    if ( ( inputfilesize != 0 ) && (inputfilesize == outputfilesize) ) { try { Files.deleteIfExists(inputFilePath); } catch (IOException ex) { ui.error("Files.deleteIfExists(inputFilePath): " + ex + "\n"); } }
                    
                } else { ui.error(inputFilePath.toAbsolutePath() + " ignoring:   " + cipherFilePath.toAbsolutePath() + " (is cipher!)\n"); }
            } else { ui.error("Skipping directory: " + inputFilePath.getFileName() + "\n"); } // End "not a directory"
        } // Encrypt Files Loop
        stats.setFilesEndEpoch();
        ui.status(stats.getEncryptionEndSummary());

        updateProgressTaskTimer.cancel(); updateProgressTaskTimer.purge();  
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
        stats.addFilesBytesEncrypted(1);
        stats.addFileBytesEncrypted(1);
        
        return (byte)dbm; // outputByte
    }

//  Recursive Deletion of PathList
    public void deleteSelection(ArrayList<Path> inputFilesPathList, boolean delete, boolean returnpathlist, String wildcard)
    {
        EnumSet opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS); //follow links
        MySimpleFileVisitor mySimpleFileVisitor = new MySimpleFileVisitor(ui, delete, returnpathlist, wildcard);
        for (Path path:inputFilesPathList)
        {
            try{Files.walkFileTree(path, opts, Integer.MAX_VALUE, mySimpleFileVisitor);} catch(IOException e){System.err.println(e);}
        }
    }
    

    private String getBinaryString(Byte myByte) { return String.format("%8s", Integer.toBinaryString(myByte & 0xFF)).replace(' ', '0'); }
    private String getDecString(Byte myByte) { return String.format("%3d", (myByte & 0xFF)).replace(" ", "0"); }
    private String getHexString(Byte myByte, String digits) { return String.format("%0" + digits + "X", (myByte & 0xFF)); }
    private String getChar(Byte myByte) { return String.format("%1s", (char) (myByte & 0xFF)).replaceAll("\\p{C}", "?"); }  //  (myByte & 0xFF); }
    
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
    
    public boolean isValidFile(Path path, boolean createFile, boolean mustHaveData)
    {
        boolean isValid = true;
        long fileSize = 0;
        LinkOption[] opt = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
        
        if ((createFile) && (Files.notExists(path))) { try {Files.createFile(path);} catch (IOException ex) { ui.error("Error: isValidFile(..) Files.createFile(path): "+ ex.getLocalizedMessage() + "\n");} }

        if (Files.exists(path, opt))    { if (verbose) { /*ui.log(path + " exists.\n"); */}}                else { ui.error(path + " does not exist!" + "\n"); isValid = false; }
        if (Files.isRegularFile(path))  { if (verbose) { /*ui.log("The checked file is regular.\n"); */}}   else { ui.error("Error: The checked file is not regular!" + "\n"); isValid = false; }
        if (Files.isReadable(path))     { if (verbose) { /*ui.log("The checked file is readable.\n"); */}}  else { ui.error("Error: The checked file is not readable!" + "\n"); isValid = false; }
        if (Files.isWritable(path))     { if (verbose) { /*ui.log("The checked file is writable.\n"); */}}  else { ui.error("Error: The checked file is not writable!" + "\n"); isValid = false; }
        try { fileSize = Files.size(path); } catch (IOException ex) { ui.error("Error: isValidFile(..) Files.size(path): "+ ex.getLocalizedMessage() + "\n"); }
//        if (verbose) { ui.log("The checked file has " + fileSize + " bytes of data."); }
        if (( mustHaveData ) && ( fileSize == 0 )) { ui.error("Error: The checked file requires data!\n"); isValid = false; }
        
        return isValid;
    }

    public boolean isValidDir(Path path)
    {
        boolean isValid = true;
        if (Files.exists(path))    {  } else { ui.error("Item: " + path + " does not exist!" + "\n"); isValid = false; }
        
        return isValid;
    }

    public ArrayList<Path> getPathList(File[] files)
    {
        // Converts from File[] to ArraayList<Path>
        ArrayList<Path> pathList = new ArrayList<>(); for (File file:files) { pathList.add(file.toPath()); }
        return pathList;
    }
    
//  Called by EncryptSelected GUI & GUIFX
    public ArrayList<Path> getExtendedPathList(File[] files, String wildcard)
    {
        // Converts from File[] to ArraayList<Path> where as every dir is converted into additional PathLists
        ArrayList<Path> pathList = new ArrayList<>();
        for (File file:files)
        {
            if (file.isDirectory())
            {
                for (Path path:getDirectoryPathList(file, wildcard))
                {
                    pathList.add(path);
                } 
            }
            else
            {
                pathList.add(file.toPath());
            }
        }
        return pathList;
    }

//  Called by EncryptSelected CLUI (works with PathList instead of File[] because FileChooser produces File[] array)
    public ArrayList<Path> getExtendedPathList(ArrayList<Path> inPathList, String wildcard)
    {
        // Converts from File[] to ArraayList<Path> where as every dir is converted into additional PathLists
        ArrayList<Path> pathList = new ArrayList<>();
        for (Path outerpath:inPathList)
        {
            if ( Files.isDirectory(outerpath) ) { for (Path path:getDirectoryPathList(outerpath.toFile(), wildcard)) { pathList.add(path); }
            } else { pathList.add(outerpath); }
        }
        return pathList;
    }

    // Used by getExtendedPathList(File[] files)
    public ArrayList<Path> getDirectoryPathList(File file, String wildcard)
    {
        // Converts from File[] to ArraayList<Path> where as every dir is converted into additional PathLists
        ArrayList<Path> pathList = new ArrayList<>();
        
        EnumSet opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS); //follow links
        MySimpleFileVisitor mySimpleFileVisitor = new MySimpleFileVisitor(ui, false, true, wildcard);
        try{Files.walkFileTree(file.toPath(), opts, Integer.MAX_VALUE, mySimpleFileVisitor);} catch(IOException e){System.err.println(e);}
        pathList = mySimpleFileVisitor.getPathList();

        return pathList;
    }

//    public static String getCopyright()                     { return COPYRIGHT; }
//    public static String getAuthor()                        { return AUTHOR; }
//    public static String getVersion()                       { return VERSIONSTRING; }
//    public static String getProcuct()                       { return PRODUCTNAME; }
//    public static String getCompany()                       { return COMPANYNAME; }
    public Stats getStats()                                 { return stats; }

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
    private final PathMatcher matcher;
    private final boolean delete; 
    private final boolean returnpathlist; 
    private final ArrayList<Path> pathList;

//  Default CONSTRUCTOR
    public MySimpleFileVisitor(UI ui, boolean delete, boolean returnpathlist, String wildcard) // "*.txt"
    {
        this.ui = ui;
        matcher = FileSystems.getDefault().getPathMatcher("glob:" + wildcard);
        this.delete = delete;
        this.returnpathlist = returnpathlist;
        pathList = new ArrayList<Path>();
        
    }
   
    @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
    {
//        System.out.println("Entering directory: " + dir.toString());
        return FileVisitResult.CONTINUE;
    }
    
    @Override public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
    {
        if (path.getFileName() != null && matcher.matches(path.getFileName()))
        {            
            if      (delete)
            {
                try      
                {
//                  System.out.println("Removing filtered file: " + path.toRealPath().toString());
                    Files.delete(path);
//                } catch (IOException ex) { System.out.println("Error: visitFile(.. ) Failed file: " + path.toString() + " due to: " + ex); }
                } catch (IOException ex) { ui.error("Error: visitFile(.. ) Failed file: " + path.toString() + " due to: " + ex + "\n"); }
            }
            
            else if (returnpathlist)
            {
//                System.out.println("Adding filtered file: " + path.getFileName());
                pathList.add(path);
            }
            else {     }
        }   
        return FileVisitResult.CONTINUE;
    }
    
    @Override public FileVisitResult visitFileFailed(Path file, IOException exc)
    {
//        System.out.println("Error: visitFileFailed: " + file.toString() + " due to: " + exc);
        ui.error("Error: visitFileFailed: " + exc + "\n");
        return FileVisitResult.SKIP_SIBLINGS;
    }
    
    @Override public FileVisitResult postVisitDirectory(Path path, IOException exc)
    {
        if (delete)
        {
            try
            {
//                    System.out.println("Removing leaving filtered directory: " + path.getFileName());
                Files.delete(path);
//            } catch (IOException ex) { System.out.println("Error: postVisitDirectory: " + path.toString() + " due to: " + ex); } }
            } catch (IOException ex) { ui.error("Error: postVisitDirectory: " + path.toString() + " due to: " + ex + "\n"); } }
        else if (returnpathlist)    {     }
        else                        {     }
        return FileVisitResult.CONTINUE;
    }
    
    public ArrayList<Path> getPathList() { return pathList; }
}
