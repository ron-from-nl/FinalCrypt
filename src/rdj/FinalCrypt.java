package rdj;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.EnumSet;

/* commandline test routine

cd ../dist/;
rm data**; clear;
echo ZYXVWUTSRQPONMLKJIHGFEDCBA098765 > data.orig;
echo abcdefghijklstuvwxyz > cipher;
java -jar FinalCrypt.jar -i data.orig -c cipher -o data.encr; 
ls -l; cat data.orig; cat data.encr;
java -jar FinalCrypt.jar -i data.encr -c cipher -o data.orig2; 
ls -l; cat data.orig; cat data.orig2;

or

clear; echo -n -e \\x05 > a; echo -n -e \\x03 > b; java -jar FinalCrypt.jar --bin -i a -c b -o c

*/

public class FinalCrypt
{
    static final String COMPANYNAME = "GPLv3";
    static final String PRODUCTNAME = "FinalCrypt";
    static final String AUTHOR = "Ron de Jong";
    static final String COPYRIGHT = "© Copyleft " + Calendar.getInstance().get(Calendar.YEAR);
    static final String VERSION = "1.0";
    
    private static int bufferSize = 1024 * 1024; // Default 1MB
    private int dataBufferSize;
    private int cipherBufferSize;
    private int outputBufferSize;
    private final String encoding = System.getProperty("file.encoding");

    public FinalCrypt(Path ifp, Path cfp, Path ofp, boolean debug, boolean bin)
    {        
            
//        // Encryption Byte Test
//        byte data = (byte) 0x05;
//        byte cipher = (byte) 0x03;
//        byte out1 = encryptByte(data, cipher);
//        byte out2 = encryptByte(out1, cipher);        
//        System.out.println(getBinaryString(data) + " " + getBinaryString(cipher) + " " + getBinaryString(out1));
//        System.out.println(getBinaryString(out1) + " " + getBinaryString(cipher) + " " + getBinaryString(out2));
            
        try { if ( Files.size(cfp) < bufferSize) { bufferSize = (int) (long) Files.size(cfp);} } catch (IOException ex) { System.out.println("Files.size(cfp)" + ex); }
        dataBufferSize = bufferSize;
        cipherBufferSize = bufferSize;
        outputBufferSize = bufferSize;        
//        System.out.println(bufferSize);
        bufferFiles(ifp, cfp, ofp, debug, bin);        
    }
    
    private void bufferFiles(Path inputFilePath, Path cipherFilePath, Path outputFilePath, boolean debug, boolean bin)
    {
        boolean dataFileEnded = false;
        long dataChannelPos = 0;
        long cipherChannelPos = 0;
        
        ByteBuffer dataBuffer =     ByteBuffer.allocate(dataBufferSize);   dataBuffer.clear();
        ByteBuffer cipherBuffer =   ByteBuffer.allocate(cipherBufferSize); cipherBuffer.clear();
        ByteBuffer outputBuffer =   ByteBuffer.allocate(outputBufferSize); outputBuffer.clear();

        // Open outputFile for writing
        try (SeekableByteChannel outputChannel = Files.newByteChannel(outputFilePath, EnumSet.of(StandardOpenOption.WRITE)))
        {
            //open dataFile
            try (SeekableByteChannel dataChannel = Files.newByteChannel(inputFilePath, EnumSet.of(StandardOpenOption.READ)))
            {
                // Open cipherFile
                try (SeekableByteChannel cipherChannel = Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)))
                {
                    // Fill dataBuffer & cipherBuffer
                    while ( ! dataFileEnded)
                    {
                        // Fill dataBuffer
                        dataChannelPos = dataChannel.read(dataBuffer); dataBuffer.flip();
                        if ( dataChannelPos == -1 ) { dataFileEnded = true; }
                        if ( dataBuffer.limit() < dataBufferSize ) { dataFileEnded = true; }

                        // Fill cipherBuffer
                        cipherChannelPos = cipherChannel.read(cipherBuffer);
                        if ( cipherChannelPos < cipherBufferSize ) { cipherChannel.position(0); cipherChannel.read(cipherBuffer); }
                        cipherBuffer.flip();
                        

                        // Parse dataBuffer & cipherBuffer to cryptOutputBuffer and write to file
                        outputBuffer = cryptOutputBuffer(dataBuffer, cipherBuffer, bin);
                        
                        if (debug)
                        {
                            printByteBuffer("DB", dataBuffer);
                            printByteBuffer("CB", cipherBuffer);
                            printByteBuffer("OB", outputBuffer);
                        }
                        
                        outputChannel.write(outputBuffer);
                        
                        outputBuffer.clear();
                        dataBuffer.clear();
                        cipherBuffer.clear();
                    }
                    outputChannel.close();
                    cipherChannel.close();
                    dataChannel.close();
                    
                } catch (IOException ex) { System.err.println("cipherChannel = Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex); }
            } catch (IOException ex) { System.err.println("dataChannel = Files.newByteChannel(inputFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex); }
        } catch (IOException ex) { System.err.println("outputChannel = Files.newByteChannel(outputFilePath, EnumSet.of(StandardOpenOption.WRITE)) " + ex); }
    }
    
    private void printByteBuffer(String preFix, ByteBuffer byteBuffer)
    {
        System.out.print(preFix + "C: ");
        System.out.print(Charset.forName(encoding).decode(byteBuffer)); byteBuffer.flip();
//      for (byte mybyte: dataBuffer.array()) { System.out.print(Integer.toHexString(Byte.toUnsignedInt(mybyte) & 0xFF)); }
        System.out.println(" " + preFix + "Z: " + byteBuffer.limit());
    }
    
    private String getBinaryString(Byte myByte) { return String.format("%8s", Integer.toBinaryString(myByte & 0xFF)).replace(' ', '0'); }
    
    private ByteBuffer cryptOutputBuffer(ByteBuffer dataBuffer, ByteBuffer cipherBuffer, boolean bin)
    {
        byte dataByte = 0;
        byte cipherByte = 0;
        byte outputByte;
        ByteBuffer outputBuffer =   ByteBuffer.allocate(outputBufferSize); outputBuffer.clear();
        for (int dataBufferCount = 0; dataBufferCount < dataBuffer.limit(); dataBufferCount++)
        {
            dataByte = dataBuffer.get(dataBufferCount);
            cipherByte = cipherBuffer.get(dataBufferCount);
            outputByte = encryptByte(dataBuffer.get(dataBufferCount), cipherBuffer.get(dataBufferCount));
            outputBuffer.put(outputByte);
            if ( bin ) { this.printByteBinary(dataByte, cipherByte); }
        }
        outputBuffer.flip();
        
        return outputBuffer;
    }
    
    private void printByteBinary(byte dataByte, byte cipherByte)
    {
        int dum = 0;  // DUM Data Unnegated Mask
        int dnm = 0;       // DNM Data Negated Mask
        int dbm = 0;    // DBM Data Blended Mask
                
        dum = dataByte & ~cipherByte;
        dnm = ~dataByte & cipherByte;
        dbm = dum + dnm; // outputByte        
        System.out.println("\nDat = " + getBinaryString(dataByte));
        System.out.println("Cph = " + getBinaryString(cipherByte));
        System.out.println();
        System.out.println("DUM  = " + getBinaryString((byte)dataByte) + " & " + getBinaryString((byte)~cipherByte) + " = " + getBinaryString((byte)dum));
        System.out.println("DNM  = " + getBinaryString((byte)~dataByte) + " & " + getBinaryString((byte)cipherByte) + " = " + getBinaryString((byte)dnm));
        System.out.println("DBM  = " + getBinaryString((byte)dum) + " & " + getBinaryString((byte)dnm) + " = " + getBinaryString((byte)dbm));
    }
    
    private byte encryptByte(byte dataByte, byte cipherByte)
    {
        int dum = 0;  // DUM Data Unnegated Mask
        int dnm = 0;       // DNM Data Negated Mask
        int dbm = 0;    // DBM Data Blended Mask

        // Data =   00000101 (5) (0x05)
        // Cipher = 00000011 (3) (0x03)
        
        // DUM =  00000101 & 00001100 = 00000100 (data being masked by inverted cipher)
        // DNM =  00001010 & 00000011 = 00000010 (inverted data being masked by cipher) this inverts all data not being masked by cipher
        // duim = 00000100 + 00000010 = 00000110 (adds adds inverted databits to original bits)
                
        dum = dataByte & ~cipherByte;
        dnm = ~dataByte & cipherByte;
        dbm = dum + dnm; // outputByte        
        return (byte)dbm; // outputByte
    }

    private static boolean isValidFile(String fileStr, boolean createFile, boolean mustHaveData, boolean debug)
    {
        boolean isValid = true;
        long fileSize = 0;
        LinkOption[] opt = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
        Path path = Paths.get(System.getProperty("user.dir"), fileStr); // working dir
        
        if ((createFile) && (Files.notExists(path))) { try {Files.createFile(path);} catch (IOException ex) { System.err.println("Error: isValidFile(..) Files.createFile(path): "+ ex.getLocalizedMessage());} }

        if (Files.exists(path, opt))    { if (debug) { System.out.println(path + " exists"); }}                 else { System.err.println(path + " does not exist!"); isValid = false; }
        if (Files.isRegularFile(path))  { if (debug) { System.out.println("The checked file is regular."); }}   else { System.err.println("Error: The checked file is not regular!"); isValid = false; }
        if (Files.isReadable(path))     { if (debug) { System.out.println("The checked file is readable."); }}  else { System.err.println("Error: The checked file is not readable!"); isValid = false; }
        if (Files.isWritable(path))     { if (debug) { System.out.println("The checked file is writable."); }}  else { System.err.println("Error: The checked file is not writable!"); isValid = false; }
        try { fileSize = Files.size(path); } catch (IOException ex) { System.err.println("Error: isValidFile(..) Files.size(path): "+ ex.getLocalizedMessage()); }
        if (debug) { System.out.println("The checked file has " + fileSize + " bytes of data."); }
        if (( mustHaveData ) && ( fileSize == 0 )) { System.err.println("Error: The checked file requires data!"); isValid = false; }
        
        return isValid;
    }

    private static boolean validateIntegerString(String text) { try { Integer.parseInt(text); return true;} catch (NumberFormatException e) { return false; } }
    
    public static void main(String[] args)
    {
        boolean debug = false, bin = false, ifset = false, cfset = false, ofset = false;
        boolean validInvocation = true;

        Path inputFilePath = null;
        Path cipherFilePath = null;
        Path outputFilePath = null;
                
        for (int paramCnt=0; paramCnt < args.length; paramCnt++)
        {
            if      (( args[paramCnt].equals("-h")) || ( args[paramCnt].equals("--help") ))                     { usage(); }
            else if (( args[paramCnt].equals("-d")) || ( args[paramCnt].equals("--debug") ))                    { debug = true; }
            else if ( args[paramCnt].equals("--bin"))                                                           { bin = true; }
            else if ( args[paramCnt].equals("-b")) { if ( validateIntegerString(args[paramCnt + 1]) ) { bufferSize = ( Integer.valueOf( args[paramCnt + 1] ) * 1024 * 1024); } else { System.err.println("\nError: Invalid Option Value [-b size]"); usage(); }}
            else if ( args[paramCnt].equals("-i")) { if ( isValidFile(args[paramCnt+1], false, true, debug) )   { inputFilePath = Paths.get(System.getProperty("user.dir"), args[paramCnt+1]); ifset = true; }  else { usage(); } }
            else if ( args[paramCnt].equals("-c")) { if ( isValidFile(args[paramCnt+1], false, true, debug) )   { cipherFilePath = Paths.get(System.getProperty("user.dir"), args[paramCnt+1]); cfset = true; } else { usage(); } }
            else if ( args[paramCnt].equals("-o")) { if ( isValidFile(args[paramCnt+1], true, false, debug) )   { outputFilePath = Paths.get(System.getProperty("user.dir"), args[paramCnt+1]); ofset = true; } else { usage(); } }
        }
        
        if ( ! ifset ) { System.err.println("\nError: Missing parameter <-i \"inputfile\">"); usage(); }
        if ( ! cfset ) { System.err.println("\nError: Missing parameter <-c \"cipherfile\">"); usage(); }
        if ( ! ofset ) { System.err.println("\nError: Missing parameter <-o \"outputfile\">"); usage(); }
        
        new FinalCrypt(inputFilePath, cipherFilePath, outputFilePath, debug, bin);
    }

    private static void usage()
    {
        String fileSeparator = java.nio.file.FileSystems.getDefault().getSeparator();

        System.out.println();
        System.out.println("Usage:   java -jar FinalCrypt.jar [options] <Parameters>\n");
        System.out.println("Options:");
        System.out.println("            [-h] [--help]         Shows this help page.");
        System.out.println("            [-d] [--debug]        Enables debugging mode.");
        System.out.println("            [--bin]               Print binary calculations.");
        System.out.println("            [-b size]             Changes default I/O buffer size (size = MB) (default 1MB).\n");
        System.out.println("Parameters:");
        System.out.println("            <-i \"inputfile\">      The datafile you want to encrypt.");
        System.out.println("            <-c \"cipherfile\">     The file that encrypts your datafile. Keep cipherfile SECRET!!!");
        System.out.println("            <-o \"outputfile\">     The encrypted datafile; (use cipherfile to also decrypt).\n");
        System.out.println("Author: " + getAuthor() + " " + getCopyright() + "\n");
        System.exit(1);
    }
    
    public static String getCopyright()                     { return COPYRIGHT; }
    public static String getAuthor()                        { return AUTHOR; }
    public static String getVersion()                       { return VERSION; }
    public static String getProcuct()                       { return PRODUCTNAME; }
    public static String getCompany()                       { return COMPANYNAME; }
}
