package rdj;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import static rdj.FinalCrypt.getAuthor;
import static rdj.FinalCrypt.getCopyright;

/* commandline test routine

cd ../dist/;
rm data**; clear;
echo ZYXVWUTSRQPONMLKJIHGFEDCBA098765 > data.txt;
echo abcdefghijklstuvwxyz > cipher;
java -jar FinalCrypt.jar -i data.txt -c cipher -o data.encr.txt; 
ls -l; cat data.txt; cat data.encr.txt;
java -jar FinalCrypt.jar -i data.encr.txt -c cipher -o data.decr.txt; 
ls -l; cat data.txt; cat data.decr.txt;

or

clear; echo -n -e \\x05 > a; echo -n -e \\x03 > b; java -jar FinalCrypt.jar --bin -i a -c b -o c

or

clear; echo -n -e \\x05 > a; echo -n -e \\x03 > b; java -jar FinalCrypt.jar --print -i a -c b -o c

or

clear; echo -n ZYXVWUTSRQPONMLKJIHGFEDCBA098765 > a; echo -n abcdefghijklstuvwxyz > b; java -jar FinalCrypt.jar --print -i a -c b -o c

*/

public class CLUI implements UI
{

    public static void main(String[] args)
    {
        boolean ifset = false, cfset = false;
        boolean validInvocation = true;

        ArrayList<Path> inputFilesPathList = new ArrayList<>();
        Path inputFilePath = null;
        Path cipherFilePath = null;
        Path outputFilePath = null;
        
        
        // Load the FinalCrypt Objext
        FinalCrypt finalCrypt = new FinalCrypt();

        // Validate Parameters
        for (int paramCnt=0; paramCnt < args.length; paramCnt++)
        {
            // Options
            if      (( args[paramCnt].equals("-h")) || ( args[paramCnt].equals("--help") ))                         { usage(); }
            else if (( args[paramCnt].equals("-d")) || ( args[paramCnt].equals("--debug") ))                        { finalCrypt.setDebug(true); }
            else if (( args[paramCnt].equals("-v")) || ( args[paramCnt].equals("--verbose") ))                      { finalCrypt.setVerbose(true); }
            else if (( args[paramCnt].equals("-p")) || ( args[paramCnt].equals("--print") ))                        { finalCrypt.setPrint(true); }
            else if ( args[paramCnt].equals("--txt"))                                                               { finalCrypt.setTXT(true); }
            else if ( args[paramCnt].equals("--bin"))                                                               { finalCrypt.setBin(true); }
            else if ( args[paramCnt].equals("--dec"))                                                               { finalCrypt.setDec(true); }
            else if ( args[paramCnt].equals("--hex"))                                                               { finalCrypt.setHex(true); }
            else if ( args[paramCnt].equals("--chr"))                                                               { finalCrypt.setChr(true); }
            else if ( args[paramCnt].equals("-b")) { if ( validateIntegerString(args[paramCnt + 1]) )    { finalCrypt.setBufferSize(Integer.valueOf( args[paramCnt + 1] ) * 1024 * 1024); paramCnt++; } else { System.err.println("\nError: Invalid Option Value [-b size]"); usage(); }}

            // File Parameters
            else if ( args[paramCnt].equals("-i")) { inputFilePath = Paths.get(System.getProperty("user.dir"), args[paramCnt+1]); inputFilesPathList.add(inputFilePath); ifset = true; paramCnt++; }
            else if ( args[paramCnt].equals("-c")) { cipherFilePath = Paths.get(System.getProperty("user.dir"), args[paramCnt+1]); cfset = true; paramCnt++; }
            else { System.err.println("\nError: Invalid Parameter:" + args[paramCnt]); usage(); }
        }
        
        if ( ! ifset ) { System.err.println("\nError: Missing parameter <-i \"inputfile\">"); usage(); }
        if ( ! cfset ) { System.err.println("\nError: Missing parameter <-c \"cipherfile\">"); usage(); }


        for(Path inputFilePathItem : inputFilesPathList)
        {
            if ( finalCrypt.isValidFile(inputFilePathItem, false, true) ) {} else   { System.err.println("\nError input"); usage(); }
            if ( inputFilePathItem.compareTo(cipherFilePath) == 0)      { System.err.println("\nError: inputfile equal to cipherfile!"); usage(); }

            outputFilePath = inputFilePathItem.resolveSibling(inputFilePathItem.getFileName() + ".dat");
            if ( finalCrypt.isValidFile(outputFilePath, true, false) ) {} else  { System.err.println("\nError cipher"); usage(); }
        }
        
        if ( ! finalCrypt.isValidFile(cipherFilePath, false, true) )   { usage(); }

//        if (inputFilePath.compareTo(cipherFilePath) == 0) { System.err.println("\nError: inputfile equal to cipherfile!"); usage(); }
//        if (inputFilePath.compareTo(outputFilePath) == 0) { System.err.println("\nError: inputfile equal to outputfile!"); usage(); }
//        if (cipherFilePath.compareTo(outputFilePath) == 0) { System.err.println("\nError: cipherfile equal to ouputfile!"); usage(); }

        // Set the Options
        try 
        {
            if ( Files.size(cipherFilePath) < finalCrypt.getBufferSize())
            {
                finalCrypt.setBufferSize((int) (long) Files.size(cipherFilePath));
                if ( finalCrypt.getVerbose() ) { System.out.println("Alert: BufferSize limited to cipherfile size: " + finalCrypt.getBufferSize()); }
            }
        }
        catch (IOException ex)
        {
                System.out.println("Files.size(cfp)" + ex);
        }
        
        // Set the files
        finalCrypt.setInputFilesPathList(inputFilesPathList);
        finalCrypt.setCipherFilePath(cipherFilePath);
//        finalCrypt.setOutputFilePath(outputFilePath);
        
        if ( finalCrypt.getVerbose() )
        {
            System.out.println("Info: Buffersize set to: " + finalCrypt.getBufferSize());
            for(Path inputFilePathItem : inputFilesPathList) { System.out.println("Info: Inputfile set: " + inputFilePathItem.getFileName()); }
            System.out.println("Info: Cipherfile set: " + cipherFilePath.getFileName());
//            System.out.println("Info: Outputfile set: " + outputFilePath.getFileName());
        }
        
        // Start Encryption
        finalCrypt.encryptFile();
    }
    
    private static boolean validateIntegerString(String text) { try { Integer.parseInt(text); return true;} catch (NumberFormatException e) { return false; } }

    private static void usage()
    {
        String fileSeparator = java.nio.file.FileSystems.getDefault().getSeparator();

        System.out.println();
        System.out.println("Usage:   java -jar FinalCrypt.jar [options] <Parameters>\n");
        System.out.println("Options:");
        System.out.println("            [-h] [--help]         Shows this help page.");
        System.out.println("            [-d] [--debug]        Enables debugging mode.");
        System.out.println("            [-p] [--print]        Print overal data encryption.");
        System.out.println("            [--txt]               Print text calculations.");
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

    @Override
    public void log(String message)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void status(String status)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateEncryptionDiffStats(int value)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateTotalProgress(int value)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateFileProgress(int value)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBufferProgress(int value)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
