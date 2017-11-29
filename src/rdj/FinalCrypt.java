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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.TimerTask;

//public class FinalCrypt  extends SwingWorker
public class FinalCrypt  extends Thread
{
    static final String COMPANYNAME = "GPLv3";
    static final String PRODUCTNAME = "FinalCrypt";
    static final String AUTHOR = "Ron de Jong";
    static final String COPYRIGHT = "© Copyleft " + Calendar.getInstance().get(Calendar.YEAR);
    static final String VERSION = "1.0";
    private boolean debug = false, verbose = false, print = false, txt = false, bin = false, dec = false, hex = false, chr = false;

    private int bufferSize = 1024 * 1024; // Default 1MB
    private int inputFileBufferSize;
    private int cipherFileBufferSize;
    private int outputFileBufferSize;
    private long bufferTotal = 0; // DNumber of buffers

    private int printAddressByteCounter = 0;
    public long filesBytesTotal = 0;
    public long fileBytesTotal = 0;
    private long filesBytesEncrypted = 0;
    private long fileBytesEncrypted = 0;
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
    

    public FinalCrypt(UI ui)
    {    
//        super("FinalCryptThread");
        
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
    public boolean getTXT()                                                 { return txt; }
    public boolean getBin()                                                 { return bin; }
    public boolean getDec()                                                 { return dec; }
    public boolean getHex()                                                 { return hex; }
    public boolean getChr()                                                 { return chr; }
    public ArrayList<Path> getInputFilesPathList()                          { return inputFilesPathList; }
    public Path getCipherFilePath()                                         { return cipherFilePath; }
    public Path getOutputFilePath()                                         { return outputFilePath; }
    public long getFileBytesEncrypted()                                     { return fileBytesEncrypted; }
    public long getFilesBytesEncrypted()                                    { return filesBytesEncrypted; }
    public long getFileBytesTotal()                                         { return fileBytesTotal; }
    public long getFilesBytesTotal()                                        { return filesBytesTotal; }
    
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
        
    public void encryptFiles()
    {
        ui.encryptionStarted();
        // Get the all files size total
        
        // Reset the Bytes Progress Counters
        fileBytesTotal = 0;
        filesBytesTotal = 0;
        filesBytesEncrypted = 0;
        fileBytesEncrypted = 0;

        // Get files bytes total
        for (Path inputFilePath:inputFilesPathList) { try { filesBytesTotal += Files.size(inputFilePath); } catch (IOException ex) { ui.error("Error: encryptFiles () filesBytesTotal += Files.size(inputFilePath); "+ ex.getLocalizedMessage() + "\n"); }} 
        if (verbose) { ui.log("Total files: " + inputFilesPathList.size() + " containing totally:  " + filesBytesTotal + " bytes\n"); }

        // Setup the Progress timer & task
        updateProgressTask = new TimerTask() { @Override public void run() { ui.encryptionProgress( (int) (fileBytesEncrypted /(fileBytesTotal/100.0)), (int) (filesBytesEncrypted /(filesBytesTotal/100.0))); }};
//        updateProgressTask = new TimerTask() { @Override public void run() { setProgress( (int) (filesBytesEncrypted /(filesBytesTotal/100L))); }};
        updateProgressTaskTimer = new java.util.Timer(); updateProgressTaskTimer.schedule(updateProgressTask, 0L, 100L);

        // Encrypt Files loop
        for (Path inputFilePath:inputFilesPathList)
        {
            if ((inputFilePath.compareTo(cipherFilePath) != 0))
            {
                ui.status("Encrypting file: " + inputFilePath.getFileName() + " with cipherfile: " + cipherFilePath.getFileName() + "\n");
                fileBytesEncrypted = 0;

                // Get the filesize total
                try { fileBytesTotal = (int)Files.size(inputFilePath); } catch (IOException ex) { ui.error("Error: encryptFiles () fileBytesTotal += Files.size(inputFilePath); "+ ex.getLocalizedMessage()+ "\n"); }
                if (verbose) { ui.log("Inputfile: " + inputFilePath.getFileName() + " size: " + fileBytesTotal + " bytes\n"); }
                
                outputFilePath = inputFilePath.resolveSibling(inputFilePath.getFileName() + ".dat");
                try { Files.deleteIfExists(outputFilePath); } catch (IOException ex) { ui.error("Error: Files.deleteIfExists(outputFilePath): " + ex); }

                // Prints printByte Header ones
                ui.status("Encrypting file: " + inputFilePath.getFileName() + " with cipherfile: " + this.getCipherFilePath().getFileName() + "\n");
                if ( print )
                {
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
//            final ByteBuffer outputFileBuffer = ByteBuffer.allocate(outputFileBufferSize); outputFileBuffer.clear();                
//                final ByteBuffer outputFileBuffer;

                    



                    
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
                } catch (IOException ex) { ui.error("dataChannel = Files.newByteChannel(inputFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex + "\n"); }

                // Open cipherFile
                try (final SeekableByteChannel cipherFileChannel = Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)))
                {
                    // Fill up cipherFileBuffer
                    cipherFileChannel.position(cipherFileChannelPos); if (debug & verbose) { ui.println("Cipher Channel Pos: " + Long.toString(cipherFileChannelPos)); }
                    cipherFileChannelRead = cipherFileChannel.read(cipherFileBuffer); cipherFileChannelPos += cipherFileChannelRead;//cipherFileChannelPos = cipherFileChannel.position();
                    if ( cipherFileChannelRead < cipherFileBufferSize ) { cipherFileChannelPos = 0; cipherFileChannel.position(0); cipherFileChannelRead += cipherFileChannel.read(cipherFileBuffer); cipherFileChannelPos += cipherFileChannelRead;}
                    cipherFileBuffer.flip();

                    cipherFileChannel.close();
                } catch (IOException ex) { ui.error("cipherChannel = Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex + "\n"); }

                // Open outputFile for writing
                try (final SeekableByteChannel outputFileChannel = Files.newByteChannel(outputFilePath, EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.APPEND)))
                {
                    // Encrypt inputBuffer and fill up outputBuffer
                    ByteBuffer outputFileBuffer = encryptBuffer(inputFileBuffer, cipherFileBuffer);
                    outputFileChannel.write(outputFileBuffer);
                    outputFileBuffer.clear();
                    inputFileBuffer.clear();
                    cipherFileBuffer.clear();

                    if (txt) { logByteBuffer("DB", inputFileBuffer); logByteBuffer("CB", cipherFileBuffer); logByteBuffer("OB", outputFileBuffer); }
                    outputFileChannel.close();
                } catch (IOException ex) { ui.error("outputChannel = Files.newByteChannel(outputFilePath, EnumSet.of(StandardOpenOption.WRITE)) " + ex + "\n"); }
            }
            if ( print ) { ui.log(" ----------------------------------------------------------------------\n"); }
                







//                // Open outputFile for writing
//                try (final SeekableByteChannel outputFileChannel = Files.newByteChannel(outputFilePath, EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.APPEND)))
//                {
//                    //open dataFile
//                    try (final SeekableByteChannel inputFileChannel = Files.newByteChannel(inputFilePath, EnumSet.of(StandardOpenOption.READ)))
//                    {
//                        // Open cipherFile
//                        try (final SeekableByteChannel cipherFileChannel = Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)))
//                        {
//                            // Fill dataBuffer & cipherBuffer (Every run (..Channel.read() fills up the entire buffer)
//                            while ( ! inputFileEnded )
//                            {
//                                // Fill up inputFileBuffer
//                                if (debug) { ui.println("\nInp Ch Pos: " + Long.toString(inputFileChannel.position())); }
//                                inputFileChannelPos = inputFileChannel.read(inputFileBuffer); inputFileBuffer.flip();
//                                if (( inputFileChannelPos == -1 ) || ( inputFileBuffer.limit() < inputFileBufferSize )) { inputFileEnded = true; }
////                                if ( inputFileBuffer.limit() < inputFileBufferSize ) { inputFileEnded = true; }
//
//                                // Fill up cipherFileBuffer
//                                if (debug) { ui.println("Cip Ch Pos: " + Long.toString(cipherFileChannel.position())); }
//                                cipherFileChannelPos = cipherFileChannel.read(cipherFileBuffer);
//                                if ( cipherFileChannelPos < cipherFileBufferSize ) { cipherFileChannel.position(0); cipherFileChannel.read(cipherFileBuffer); }
//                                cipherFileBuffer.flip();
//
//
//                                // Encrypt inputBuffer and fill up outputBuffer
//                                ByteBuffer outputFileBuffer = encryptBuffer(inputFileBuffer, cipherFileBuffer);
//                                outputFileChannel.write(outputFileBuffer);
//
//                                if (txt) { logByteBuffer("DB", inputFileBuffer); logByteBuffer("CB", cipherFileBuffer); logByteBuffer("OB", outputFileBuffer); }
//
//                                outputFileBuffer.clear();
//                                inputFileBuffer.clear();
//                                cipherFileBuffer.clear();
//                            }
//
//                            if ( print ) { ui.log(" ----------------------------------------------------------------------\n"); }
//                            cipherFileChannel.close();
//                        } catch (IOException ex) { ui.error("cipherChannel = Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex + "\n"); }
//                        inputFileChannel.close();
//                    } catch (IOException ex) { ui.error("dataChannel = Files.newByteChannel(inputFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex + "\n"); }
//                    outputFileChannel.close();
//                } catch (IOException ex) { ui.error("outputChannel = Files.newByteChannel(outputFilePath, EnumSet.of(StandardOpenOption.WRITE)) " + ex + "\n"); }
                
                
                
                
                
                
                
            } // input != cipher
        } // Encrypt Files Loop
        updateProgressTaskTimer.cancel(); updateProgressTaskTimer.purge();  
        ui.encryptionEnded();
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
        
        if (debug)
        {
//            ui.log(Integer.toString(inputTotal) + "\n");
//            ui.log(Integer.toString(cipherTotal) + "\n");
//            ui.log(Integer.toString(outputDiff) + "\n");
//        MD5Converter.getMD5SumFromString(Integer.toString(dataTotal));
//        MD5Converter.getMD5SumFromString(Integer.toString(cipherTotal));
        }
        
        return outputFileBuffer;
    }
    
    private byte encryptByte(final byte dataByte, final byte cipherByte)
    {
        int dum = 0;  // DUM Data Unnegated Mask
        int dnm = 0;  // DNM Data Negated Mask
        int dbm = 0;  // DBM Data Blended Mask
        byte outputByte;

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
        filesBytesEncrypted++;
        fileBytesEncrypted++;
        
        return (byte)dbm; // outputByte
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

        if (Files.exists(path, opt))    { if (verbose) { ui.log(path + " exists.\n"); }}                   else { ui.error(path + " does not exist!" + "\n"); isValid = false; }
        if (Files.isRegularFile(path))  { if (verbose) { /*ui.log("The checked file is regular.\n"); */}}   else { ui.error("Error: The checked file is not regular!" + "\n"); isValid = false; }
        if (Files.isReadable(path))     { if (verbose) { /*ui.log("The checked file is readable.\n"); */}}  else { ui.error("Error: The checked file is not readable!" + "\n"); isValid = false; }
        if (Files.isWritable(path))     { if (verbose) { /*ui.log("The checked file is writable.\n"); */}}  else { ui.error("Error: The checked file is not writable!" + "\n"); isValid = false; }
        try { fileSize = Files.size(path); } catch (IOException ex) { ui.error("Error: isValidFile(..) Files.size(path): "+ ex.getLocalizedMessage() + "\n"); }
//        if (verbose) { ui.log("The checked file has " + fileSize + " bytes of data."); }
        if (( mustHaveData ) && ( fileSize == 0 )) { ui.error("Error: The checked file requires data!\n"); isValid = false; }
        
        return isValid;
    }
    
    public static String getCopyright()                     { return COPYRIGHT; }
    public static String getAuthor()                        { return AUTHOR; }
    public static String getVersion()                       { return VERSION; }
    public static String getProcuct()                       { return PRODUCTNAME; }
    public static String getCompany()                       { return COMPANYNAME; }

    @Override
    @SuppressWarnings("empty-statement")
    public void run()
    {
//	do
//        {
//            try { Thread.sleep(1000); } catch (InterruptedException error) { };
//        }
//	while(keepRunning);
//	return;
    }

////  SwingWorker version    
//    @Override
//    protected Object doInBackground()
//    {
//        this.encryptFiles();
//        return this;
//    }
}
