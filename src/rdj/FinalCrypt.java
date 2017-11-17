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
    private int printByteCounter = 0;
    private final String encoding = System.getProperty("file.encoding");

    public FinalCrypt(Path ifp, Path cfp, Path ofp, boolean debug, boolean print, boolean bin, boolean dec, boolean hex, boolean chr)
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

        // Prints printByte Header ones
        if ( print )
        {
            System.out.println("             Source               Cipher             Destination      ");
            System.out.println("adr      | bin      hx dec c | bin      hx dec c | bin      hx dec c");
        }
        bufferFiles(ifp, cfp, ofp, debug, print, bin, dec, hex, chr);        
    }
    
    private void bufferFiles(Path inputFilePath, Path cipherFilePath, Path outputFilePath, boolean debug, boolean print, boolean bin, boolean dec, boolean hex, boolean chr)
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
                        outputBuffer = cryptOutputBuffer(dataBuffer, cipherBuffer, print, bin, dec, hex, chr);
                        
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
    
    private String getBinaryString(Byte myByte) { return String.format("%8s", Integer.toBinaryString(myByte & 0xFF)).replace(' ', '0'); }
    private String getDecString(Byte myByte) { return String.format("%3d", (myByte & 0xFF)).replace(" ", "0"); }
    private String getHexString(Byte myByte, String digits) { return String.format("%0" + digits + "X", (myByte & 0xFF)); }
    private String getChar(Byte myByte) { return String.format("%1s", (char) (myByte & 0xFF)).replaceAll("\\p{C}", "?"); }  //  (myByte & 0xFF); }
    
    private void printByteBuffer(String preFix, ByteBuffer byteBuffer)
    {
        System.out.print(preFix + "C: ");
        System.out.print(Charset.forName(encoding).decode(byteBuffer)); byteBuffer.flip();
//      for (byte mybyte: dataBuffer.array()) { System.out.print(Integer.toHexString(Byte.toUnsignedInt(mybyte) & 0xFF)); }
        System.out.println(" " + preFix + "Z: " + byteBuffer.limit());
    }

    private void printByte(byte dataByte, byte cipherByte)
    {
        String adrhex = getHexString((byte)printByteCounter,"8");

        String datbin = getBinaryString(dataByte);
        String dathex = getHexString(dataByte, "2");
        String datdec = getDecString(dataByte);
        String datchr = getChar(dataByte);
//        char datchr = getChar(dataByte);
        
        String cphbin = getBinaryString(cipherByte);
        String cphhex = getHexString(cipherByte, "2");
        String cphdec = getDecString(cipherByte);
        String cphchr = getChar(cipherByte);
//        char cphchr = getChar(cipherByte);
        
        int dum = 0; // DUM Data Unnegated Mask
        int dnm = 0; // DNM Data Negated Mask
        int dbm = 0; // DBM Data Blended Mask
        int out = 0; // output
                
        dum = dataByte & ~cipherByte;
        dnm = ~dataByte & cipherByte;
        dbm = dum + dnm; // outputByte
        out = dbm;
        
        String outbin = getBinaryString((byte) (out & 0xFF));
        String outhex = getHexString((byte) (out & 0xFF), "2");
        String outdec = getDecString((byte) (out & 0xFF));
        String outchr = getChar((byte) (out & 0xFF));
//        char outchr = getChar((byte) (out & 0xFF));
        
//        System.out.println("             Source               Cipher             Destination      ");
//        System.out.println("adr      | bin      hx dec c | bin      hx dec c | bin      hx dec c");
        System.out.print(adrhex + " | " + datbin + " " +  dathex + " " + datdec + " " + datchr + " | " );
        System.out.print                 (cphbin + " " +  cphhex + " " + cphdec + " " + cphchr + " | " );
        System.out.println               (outbin + " " +  outhex + " " + outdec + " " + outchr);
        printByteCounter++;
    }
    
    private void printByteBinary(byte dataByte, byte cipherByte)
    {
        int dum = 0; // DUM Data Unnegated Mask
        int dnm = 0; // DNM Data Negated Mask
        int dbm = 0; // DBM Data Blended Mask
                
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
    
    private void printByteDecimal(byte dataByte, byte cipherByte)
    {
        int dum = 0; // DUM Data Unnegated Mask
        int dnm = 0; // DNM Data Negated Mask
        int dbm = 0; // DBM Data Blended Mask
                
        dum = dataByte & ~cipherByte;
        dnm = ~dataByte & cipherByte;
        dbm = dum + dnm; // outputByte        
        System.out.println("\nDat = " + getDecString(dataByte));
        System.out.println("Cph = " + getDecString(cipherByte));
        System.out.println();
        System.out.println("DUM  = " + getDecString((byte)dataByte) + " & " + getDecString((byte)~cipherByte) + " = " + getDecString((byte)dum));
        System.out.println("DNM  = " + getDecString((byte)~dataByte) + " & " + getDecString((byte)cipherByte) + " = " + getDecString((byte)dnm));
        System.out.println("DBM  = " + getDecString((byte)dum) + " & " + getDecString((byte)dnm) + " = " + getDecString((byte)dbm));
    }
    
    private void printByteHexaDecimal(byte dataByte, byte cipherByte)
    {
        int dum = 0; // DUM Data Unnegated Mask
        int dnm = 0; // DNM Data Negated Mask
        int dbm = 0; // DBM Data Blended Mask
                
        dum = dataByte & ~cipherByte;
        dnm = ~dataByte & cipherByte;
        dbm = dum + dnm; // outputByte        
        System.out.println("\nDat = " + getHexString(dataByte,"2"));
        System.out.println("Cph = " + getHexString(cipherByte,"2"));
        System.out.println();
        System.out.println("DUM  = " + getHexString((byte)dataByte,"2") + " & " + getHexString((byte)~cipherByte,"2") + " = " + getHexString((byte)dum,"2"));
        System.out.println("DNM  = " + getHexString((byte)~dataByte,"2") + " & " + getHexString((byte)cipherByte,"2") + " = " + getHexString((byte)dnm,"2"));
        System.out.println("DBM  = " + getHexString((byte)dum,"2") + " & " + getHexString((byte)dnm,"2") + " = " + getHexString((byte)dbm,"2"));
    }
    
    private void printByteChar(byte dataByte, byte cipherByte)
    {
        int dum = 0; // DUM Data Unnegated Mask
        int dnm = 0; // DNM Data Negated Mask
        int dbm = 0; // DBM Data Blended Mask
                
        dum = dataByte & ~cipherByte;
        dnm = ~dataByte & cipherByte;
        dbm = dum + dnm; // outputByte        
        System.out.println("\nDat = " + getChar(dataByte));
        System.out.println("Cph = " + getChar(cipherByte));
        System.out.println();
        System.out.println("DUM  = " + getChar((byte)dataByte) + " & " + getChar((byte)~cipherByte) + " = " + getChar((byte)dum));
        System.out.println("DNM  = " + getChar((byte)~dataByte) + " & " + getChar((byte)cipherByte) + " = " + getChar((byte)dnm));
        System.out.println("DBM  = " + getChar((byte)dum) + " & " + getChar((byte)dnm) + " = " + getChar((byte)dbm));
    }
    
    private ByteBuffer cryptOutputBuffer(ByteBuffer dataBuffer, ByteBuffer cipherBuffer, boolean print, boolean bin, boolean dec, boolean hex, boolean chr)
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
            if ( dec ) { this.printByteDecimal(dataByte, cipherByte); }
            if ( hex ) { this.printByteHexaDecimal(dataByte, cipherByte); }
            if ( chr ) { this.printByteChar(dataByte, cipherByte); }
            if ( print ) { this.printByte(dataByte, cipherByte); }
        }
        outputBuffer.flip();
        
        return outputBuffer;
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
        boolean debug = false, print = false, bin = false, dec = false, hex = false, chr = false, ifset = false, cfset = false, ofset = false;
        boolean validInvocation = true;

        Path inputFilePath = null;
        Path cipherFilePath = null;
        Path outputFilePath = null;
                
        for (int paramCnt=0; paramCnt < args.length; paramCnt++)
        {
            if      (( args[paramCnt].equals("-h")) || ( args[paramCnt].equals("--help") ))                     { usage(); }
            else if (( args[paramCnt].equals("-d")) || ( args[paramCnt].equals("--debug") ))                    { debug = true; }
            else if (( args[paramCnt].equals("-p")) || ( args[paramCnt].equals("--print") ))                    { print = true; }
            else if ( args[paramCnt].equals("--bin"))                                                           { bin = true; }
            else if ( args[paramCnt].equals("--dec"))                                                           { dec = true; }
            else if ( args[paramCnt].equals("--hex"))                                                           { hex = true; }
            else if ( args[paramCnt].equals("--chr"))                                                           { chr = true; }
            else if ( args[paramCnt].equals("-b")) { if ( validateIntegerString(args[paramCnt + 1]) ) { bufferSize = ( Integer.valueOf( args[paramCnt + 1] ) * 1024 * 1024); paramCnt++; } else { System.err.println("\nError: Invalid Option Value [-b size]"); usage(); }}
            else if ( args[paramCnt].equals("-i")) { if ( isValidFile(args[paramCnt+1], false, true, debug) )   { inputFilePath = Paths.get(System.getProperty("user.dir"), args[paramCnt+1]); ifset = true; paramCnt++; }  else { usage(); } }
            else if ( args[paramCnt].equals("-c")) { if ( isValidFile(args[paramCnt+1], false, true, debug) )   { cipherFilePath = Paths.get(System.getProperty("user.dir"), args[paramCnt+1]); cfset = true; paramCnt++; } else { usage(); } }
            else if ( args[paramCnt].equals("-o")) { if ( isValidFile(args[paramCnt+1], true, false, debug) )   { outputFilePath = Paths.get(System.getProperty("user.dir"), args[paramCnt+1]); ofset = true; paramCnt++; } else { usage(); } }
            else { System.err.println("\nError: Invalid Parameter:" + args[paramCnt]); usage(); }
        }
        
        if ( ! ifset ) { System.err.println("\nError: Missing parameter <-i \"inputfile\">"); usage(); }
        if ( ! cfset ) { System.err.println("\nError: Missing parameter <-c \"cipherfile\">"); usage(); }
        if ( ! ofset ) { System.err.println("\nError: Missing parameter <-o \"outputfile\">"); usage(); }
        
        new FinalCrypt(inputFilePath, cipherFilePath, outputFilePath, debug, print, bin, dec, hex, chr);
    }

    private static void usage()
    {
        String fileSeparator = java.nio.file.FileSystems.getDefault().getSeparator();

        System.out.println();
        System.out.println("Usage:   java -jar FinalCrypt.jar [options] <Parameters>\n");
        System.out.println("Options:");
        System.out.println("            [-h] [--help]         Shows this help page.");
        System.out.println("            [-d] [--debug]        Enables debugging mode.");
        System.out.println("            [-p] [--print]        Print overal data encryption.");
        System.out.println("            [--bin]               Print binary calculations.");
        System.out.println("            [--dec]               Print decimal calculations.");
        System.out.println("            [--hex]               Print hexadecimal calculations.");
        System.out.println("            [--chr]               Print character calculations.");
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
