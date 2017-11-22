package rdj;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;

public class FinalCrypt
{
    static final String COMPANYNAME = "GPLv3";
    static final String PRODUCTNAME = "FinalCrypt";
    static final String AUTHOR = "Ron de Jong";
    static final String COPYRIGHT = "© Copyleft " + Calendar.getInstance().get(Calendar.YEAR);
    static final String VERSION = "1.0";
    private boolean debug = false, verbose = false, print = false, txt = false, bin = false, dec = false, hex = false, chr = false;
    private int bufferSize = 1024 * 1024; // Default 1MB
    private final int dataBufferSize;
    private final int cipherBufferSize;
    private final int outputBufferSize;
    private final long bufferTotal = 0; // DNumber of buffers
    private int printByteCounter = 0;
    private ArrayList<Path> inputFilesPathList;
    private Path cipherFilePath = null;
    private Path outputFilePath = null;
    private final long inputFileSize = 0;
    private final long cipherFileSize = 0;
    private final long outputFileSize = 0;
    private final String encoding = System.getProperty("file.encoding");

    public FinalCrypt()
    {        
        inputFilesPathList = new ArrayList<>();
        dataBufferSize = bufferSize;
        cipherBufferSize = bufferSize;
        outputBufferSize = bufferSize;        
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

    public void setDebug(boolean debug)                                     { this.debug = debug; }
    public void setVerbose(boolean verbose)                                 { this.verbose = verbose; }
    public void setPrint(boolean print)                                     { this.print = print; }
    public void setTXT(boolean txt)                                         { this.txt = txt; }
    public void setBin(boolean bin)                                         { this.bin = bin; }
    public void setDec(boolean dec)                                         { this.dec = dec; }
    public void setHex(boolean hex)                                         { this.hex = hex; }
    public void setChr(boolean chr)                                         { this.chr = chr; }
    public void setBufferSize(int bufferSize)                               { this.bufferSize = bufferSize; }
    public void setInputFilesPathList(ArrayList<Path> inputFilesPathList)   { this.inputFilesPathList = inputFilesPathList; }
    public void setCipherFilePath(Path cipherFilePath)                      { this.cipherFilePath = cipherFilePath; }
    public void setOutputFilePath(Path outputFilePath)                      { this.outputFilePath = outputFilePath; }
    
    public void encryptFile()
    {
        ByteBuffer dataBuffer =     ByteBuffer.allocate(dataBufferSize);   dataBuffer.clear();
        ByteBuffer cipherBuffer =   ByteBuffer.allocate(cipherBufferSize); cipherBuffer.clear();
        ByteBuffer outputBuffer =   ByteBuffer.allocate(outputBufferSize); outputBuffer.clear();
        
        for (Path inputFilePath:inputFilesPathList)
        {
            boolean dataFileEnded = false;
            long dataChannelPos = 0;
            long cipherChannelPos = 0;
            
            outputFilePath = inputFilePath.resolveSibling(inputFilePath.getFileName() + ".dat");
//            if ( isValidFile(outputFilePath, true, false) )   {  } else { usage(); }

            // Prints printByte Header ones
            System.out.println("Encrypting file: " + inputFilePath.getFileName());
            if ( print )
            {
                System.out.println(" ----------------------------------------------------------------------");
                System.out.println("|          |       Data        |      Cipher       |      Output       |");
                System.out.println("| ---------|-------------------|-------------------|-------------------|");
                System.out.println("| adr      | bin      hx dec c | bin      hx dec c | bin      hx dec c |");
                System.out.println("|----------|-------------------|-------------------|-------------------|");
            }

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
                            outputBuffer = encryptBuffer(dataBuffer, cipherBuffer);

                            if (txt)
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

                        if ( print ) { System.out.println(" ----------------------------------------------------------------------\n"); }
                        cipherChannel.close();
                    } catch (IOException ex) { System.err.println("cipherChannel = Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex); }
                    dataChannel.close();
                } catch (IOException ex) { System.err.println("dataChannel = Files.newByteChannel(inputFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex); }
                outputChannel.close();
            } catch (IOException ex) { System.err.println("outputChannel = Files.newByteChannel(outputFilePath, EnumSet.of(StandardOpenOption.WRITE)) " + ex); }
//            System.out.println();
        }
    }
    
    private ByteBuffer encryptBuffer(ByteBuffer dataBuffer, ByteBuffer cipherBuffer)
    {
        int dataTotal = 0;
        int cipherTotal = 0;
        int outputDiff = 0;
        byte dataByte = 0;
        byte cipherByte = 0;
        byte outputByte;
        
        ByteBuffer outputBuffer =   ByteBuffer.allocate(outputBufferSize); outputBuffer.clear();
        for (int dataBufferCount = 0; dataBufferCount < dataBuffer.limit(); dataBufferCount++)
        {
            dataTotal += dataByte;
            cipherTotal += cipherByte;
            dataByte = dataBuffer.get(dataBufferCount);
            cipherByte = cipherBuffer.get(dataBufferCount);
            outputByte = encryptByte(dataBuffer.get(dataBufferCount), cipherBuffer.get(dataBufferCount));
            outputBuffer.put(outputByte);
        }
        outputBuffer.flip();
        // MD5Sum dataTotal XOR MD5Sum cipherTotal (Diff dataTot and cipherTot) 32 bit 4G
        
        outputDiff = dataTotal ^ cipherTotal;
        
        if (debug)
        {
            System.out.println(dataTotal);
            System.out.println(cipherTotal);
            System.out.println(outputDiff);
//        MD5Converter.getMD5SumFromString(Integer.toString(dataTotal));
//        MD5Converter.getMD5SumFromString(Integer.toString(cipherTotal));
        }
        
        return outputBuffer;
    }
    
    private byte encryptByte(byte dataByte, byte cipherByte)
    {
        int dum = 0;  // DUM Data Unnegated Mask
        int dnm = 0;       // DNM Data Negated Mask
        int dbm = 0;    // DBM Data Blended Mask
        byte outputByte;

        dum = dataByte & ~cipherByte;
        dnm = ~dataByte & cipherByte;
        dbm = dum + dnm; // outputByte        
        outputByte = (byte)(dbm & 0xFF);
        
        if ( bin )      { printByteBinary(dataByte, cipherByte, outputByte, dum, dnm, dbm); }
        if ( dec )      { printByteDecimal(dataByte, cipherByte, outputByte, dum, dnm, dbm); }
        if ( hex )      { printByteHexaDecimal(dataByte, cipherByte, outputByte, dum, dnm, dbm); }
        if ( chr )      { printByteChar(dataByte, cipherByte, outputByte, dum, dnm, dbm); }
        if ( print )    { printByte(dataByte, cipherByte, outputByte, dum, dnm, dbm); }
        
        return (byte)dbm; // outputByte
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

    private void printByte(byte dataByte, byte cipherByte, byte outputByte, int dum, int dnm, int dbm)
    {
        String adrhex = getHexString((byte)printByteCounter,"8");

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
        
//        System.out.println("             Source               Cipher             Destination      ");
//        System.out.println("adr      | bin      hx dec c | bin      hx dec c | bin      hx dec c");
        System.out.print("| " + adrhex + " | " + datbin + " " +  dathex + " " + datdec + " " + datchr + " | " );
        System.out.print                 (cphbin + " " +  cphhex + " " + cphdec + " " + cphchr + " | " );
        System.out.println               (outbin + " " +  outhex + " " + outdec + " " + outchr + " |");
        printByteCounter++;
    }
    
    private void printByteBinary(byte dataByte, byte cipherByte, byte outputByte, int dum, int dnm, int dbm)
    {
        System.out.println("\nDat = " + getBinaryString(dataByte));
        System.out.println("Cph = " + getBinaryString(cipherByte));
        System.out.println();
        System.out.println("DUM  = " + getBinaryString((byte)dataByte) + " & " + getBinaryString((byte)~cipherByte) + " = " + getBinaryString((byte)dum));
        System.out.println("DNM  = " + getBinaryString((byte)~dataByte) + " & " + getBinaryString((byte)cipherByte) + " = " + getBinaryString((byte)dnm));
        System.out.println("DBM  = " + getBinaryString((byte)dum) + " & " + getBinaryString((byte)dnm) + " = " + getBinaryString((byte)dbm));
    }
    
    private void printByteDecimal(byte dataByte, byte cipherByte, byte outputByte, int dum, int dnm, int dbm)
    {
        System.out.println("\nDat = " + getDecString(dataByte));
        System.out.println("Cph = " + getDecString(cipherByte));
        System.out.println();
        System.out.println("DUM  = " + getDecString((byte)dataByte) + " & " + getDecString((byte)~cipherByte) + " = " + getDecString((byte)dum));
        System.out.println("DNM  = " + getDecString((byte)~dataByte) + " & " + getDecString((byte)cipherByte) + " = " + getDecString((byte)dnm));
        System.out.println("DBM  = " + getDecString((byte)dum) + " & " + getDecString((byte)dnm) + " = " + getDecString((byte)dbm));
    }
    
    private void printByteHexaDecimal(byte dataByte, byte cipherByte, byte outputByte, int dum, int dnm, int dbm)
    {
        System.out.println("\nDat = " + getHexString(dataByte,"2"));
        System.out.println("Cph = " + getHexString(cipherByte,"2"));
        System.out.println();
        System.out.println("DUM  = " + getHexString((byte)dataByte,"2") + " & " + getHexString((byte)~cipherByte,"2") + " = " + getHexString((byte)dum,"2"));
        System.out.println("DNM  = " + getHexString((byte)~dataByte,"2") + " & " + getHexString((byte)cipherByte,"2") + " = " + getHexString((byte)dnm,"2"));
        System.out.println("DBM  = " + getHexString((byte)dum,"2") + " & " + getHexString((byte)dnm,"2") + " = " + getHexString((byte)dbm,"2"));
    }
    
    private void printByteChar(byte dataByte, byte cipherByte, byte outputByte, int dum, int dnm, int dbm)
    {
        System.out.println("\nDat = " + getChar(dataByte));
        System.out.println("Cph = " + getChar(cipherByte));
        System.out.println();
        System.out.println("DUM  = " + getChar((byte)dataByte) + " & " + getChar((byte)~cipherByte) + " = " + getChar((byte)dum));
        System.out.println("DNM  = " + getChar((byte)~dataByte) + " & " + getChar((byte)cipherByte) + " = " + getChar((byte)dnm));
        System.out.println("DBM  = " + getChar((byte)dum) + " & " + getChar((byte)dnm) + " = " + getChar((byte)dbm));
    }
    
    public boolean isValidFile(Path path, boolean createFile, boolean mustHaveData)
    {
        boolean isValid = true;
        long fileSize = 0;
        LinkOption[] opt = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
        
        if ((createFile) && (Files.notExists(path))) { try {Files.createFile(path);} catch (IOException ex) { System.err.println("Error: isValidFile(..) Files.createFile(path): "+ ex.getLocalizedMessage());} }

        if (Files.exists(path, opt))    { if (verbose) { System.out.println(path + " exists"); }}                 else { System.err.println(path + " does not exist!"); isValid = false; }
        if (Files.isRegularFile(path))  { if (verbose) { /*System.out.println("The checked file is regular."); */}}   else { System.err.println("Error: The checked file is not regular!"); isValid = false; }
        if (Files.isReadable(path))     { if (verbose) { /*System.out.println("The checked file is readable."); */}}  else { System.err.println("Error: The checked file is not readable!"); isValid = false; }
        if (Files.isWritable(path))     { if (verbose) { /*System.out.println("The checked file is writable."); */}}  else { System.err.println("Error: The checked file is not writable!"); isValid = false; }
        try { fileSize = Files.size(path); } catch (IOException ex) { System.err.println("Error: isValidFile(..) Files.size(path): "+ ex.getLocalizedMessage()); }
//        if (verbose) { System.out.println("The checked file has " + fileSize + " bytes of data."); }
        if (( mustHaveData ) && ( fileSize == 0 )) { System.err.println("Error: The checked file requires data!"); isValid = false; }
        
        return isValid;
    }
    
    public static String getCopyright()                     { return COPYRIGHT; }
    public static String getAuthor()                        { return AUTHOR; }
    public static String getVersion()                       { return VERSION; }
    public static String getProcuct()                       { return PRODUCTNAME; }
    public static String getCompany()                       { return COMPANYNAME; }
}
