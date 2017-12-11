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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/* commandline test routine

clear; echo -n -e \\x05 > 1; echo -n -e \\x03 > 2; java -jar FinalCrypt.jar
clear; echo -n -e \\x05 > 1; echo -n -e \\x03 > 2; java -cp FinalCrypt.jar rdj/CLUI --print -i 1 -c 2
clear; echo -n ZYXVWUTSRQPONMLKJIHGFEDCBA098765 > a; echo -n abcdefghijklstuvwxyz > b; java -cp FinalCrypt.jar rdj/CLUI --print -i a -c b

*/

public class CLUI implements UI
{
    FinalCrypt finalCrypt;
    public CLUI(String[] args)
    {
        boolean ifset = false, cfset = false;
        boolean validInvocation = true;

        ArrayList<Path> inputFilesPathList = new ArrayList<>();
        Path inputFilePath = null;
        Path cipherFilePath = null;
        Path outputFilePath = null;
        String extension = "";
        
        
        // Load the FinalCrypt Objext
        finalCrypt = new FinalCrypt(this);
        finalCrypt.start();
        finalCrypt.setBufferSize(finalCrypt.getBufferSizeDefault());
        
////      SwingWorker version of FinalCrype
//        finalCrypt.execute();

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
            else if ( args[paramCnt].equals("--version"))                                                           { Version upd = new Version(this); println(upd.getProcuct() + " " + upd.getThisOverallVersionString()); System.exit(0); }
            else if ( args[paramCnt].equals("--update"))                                                            { Version upd = new Version(this); upd.checkLastestVersion(); log(upd.getProcuct() + " " + upd.getVersionReport()); System.exit(0); }
            else if ( args[paramCnt].equals("-b")) { if ( validateIntegerString(args[paramCnt + 1]) )               { finalCrypt.setBufferSize(Integer.valueOf( args[paramCnt + 1] ) * 1024 ); paramCnt++; } else { error("\nError: Invalid Option Value [-b size]" + "\n"); usage(); }}

            // File Parameters
            else if ( args[paramCnt].equals("-i")) { inputFilePath = Paths.get(System.getProperty("user.dir"), args[paramCnt+1]); inputFilesPathList.add(inputFilePath); ifset = true; paramCnt++; }
            else if ( args[paramCnt].equals("-e")) { if ( args[paramCnt+1].startsWith("-") ) { String param = args[paramCnt+1].replace("-", ""); for (char chr:param.toCharArray()) { extension += "[!" + chr + "]"; }  paramCnt++; } else { extension = args[paramCnt+1]; paramCnt++; }}
            else if ( args[paramCnt].equals("-c")) { cipherFilePath = Paths.get(System.getProperty("user.dir"), args[paramCnt+1]); cfset = true; paramCnt++; }
            else { System.err.println("\nError: Invalid Parameter:" + args[paramCnt]); usage(); }
        }
        
        if ( ! ifset ) { error("\nError: Missing parameter <-i \"inputfile\">" + "\n"); usage(); }
        if ( ! cfset ) { error("\nError: Missing parameter <-c \"cipherfile\">" + "\n"); usage(); }


//      Check the inputFileList created by the parameters
        for(Path inputFilePathItem : inputFilesPathList)
        {
            if (Files.exists(inputFilePathItem))
            {
                if ( finalCrypt.isValidDir(inputFilePathItem) )
                {
                    if (finalCrypt.getVerbose()) { status("Input parameter: " + inputFilePathItem + " exist\n"); }
                }
                else
                {
                    if ( finalCrypt.isValidFile(inputFilePathItem, false, true) ) {} else   { usage(); }
                }
            }
            else
            { 
                    error("Input parameter: " + inputFilePathItem + " does not exists\n"); usage();
            }            
        }

//      Check the cipherFile created by the parameters
        if (Files.exists(cipherFilePath))
        {
            if ( finalCrypt.isValidFile(cipherFilePath, false, true) )
            {
                finalCrypt.setCipherFilePath(cipherFilePath);
                if (finalCrypt.getVerbose()) { status("Cipher parameter: " + finalCrypt.getCipherFilePath() + " exist\n"); }
            } else { usage(); }
        }
        else
        { 
            error("Cipher parameter: " + cipherFilePath + " does not exists\n"); usage();
        }            



//      All is well, now convert small parameter list into recusive list
//      Convert small PathList from parameters into ExtendedPathList (contents of subdirectory parameters as inputFile)
        ArrayList<Path> inputFilesPathListExtended = finalCrypt.getExtendedPathList(inputFilesPathList, extension);
        // Set the Options
        
        // Set Buffer Size
        int cipherSize = 0; try { cipherSize = (int)Files.size(finalCrypt.getCipherFilePath()); } catch (IOException ex) { error("Files.size(finalCrypt.getCipherFilePath()) " + ex + "\n"); }
        if ( cipherSize < finalCrypt.getBufferSize())
        {
            finalCrypt.setBufferSize(cipherSize);
            status("BufferSize is limited to cipherfile size: " + Stats.getHumanSize(finalCrypt.getBufferSize(), 1) + " \n");
        }
        else
        {
            status("BufferSize is set to: " + Stats.getHumanSize(finalCrypt.getBufferSize(), 1) + " \n");
        }
        
        // Start Encryption
        this.encryptionStarted();
        finalCrypt.encryptSelection(inputFilesPathListExtended, cipherFilePath);
    }

    public static void main(String[] args)
    {
        new CLUI(args);
    }
    
    private boolean validateIntegerString(String text) { try { Integer.parseInt(text); return true;} catch (NumberFormatException e) { return false; } }

    private void usage()
    {
        String fileSeparator = java.nio.file.FileSystems.getDefault().getSeparator();

        log("\n");
        log("Usage:   java -cp FinalCrypt.jar rdj/CLUI [options] <Parameters>\n");
        log("\n");
        log("Options:\n");
        log("            [-h] [--help]         Shows this help page.\n");
        log("            [-d] [--debug]        Enables debugging mode.\n");
        log("            [-v] [--verbose]      Enables verbose mode.\n");
        log("            [-p] [--print]        Print overal data encryption.\n");
        log("                 [--version]      Print " + Version.getProcuct() + " version.\n");
        log("                 [--update]       Check for online updates.\n");
        log("            [--txt]               Print text calculations.\n");
        log("            [--bin]               Print binary calculations.\n");
        log("            [--dec]               Print decimal calculations.\n");
        log("            [--hex]               Print hexadecimal calculations.\n");
        log("            [--chr]               Print character calculations.\n");
        log("            [-b size]             Changes default I/O buffer size (size = KB) (default 1024 KB).\n");
        log("Parameters:\n");
        log("            <-i \"dir/file\">       The dir or file you want to encrypt (dir encrypt recursively!).\n");
        log("            [-e \"[-]ext\"]         File extension to be filtered. \"-\" prefix will exclude extension\n");
        log("            <-c \"cipherfile\">     The file that encrypts your file(s). Keep cipherfile SECRET!\n");
        log("Examples:\n");
        log("            # Encrypts myfile with myphotofile\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -i myfile -c myphotofile.jpg\n");
        log("\n");
        log("            # Encrypts myfile and all content in mydir with myphotofile\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -i myfile -i mydir -c myphotofile.jpg\n");
        log("\n");
        log("            # Encrypts all files with bit extension in mydir with myphotofile\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -i mydir -e bit -c myphotofile.jpg\n");
        log("\n");
        log("            # Encrypts all files excluding bit extensions in mydir with myphotofile\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -i mydir -e -bit -c myphotofile.jpg\n\n");
        log(Version.getProcuct() + " Author: " + Version.getAuthor() + " " + Version.getCopyright() + "\n\n");
        System.exit(1);
    }

    @Override
    public void log(String message)
    {
        System.out.print(message);
    }

    @Override
    public void error(String message)
    {
        status(message);
    }

    @Override
    public void status(String status)
    {
        log(status);
    }

    @Override
    public void println(String message)
    {
        System.out.println(message);
    }

    @Override
    public void encryptionGraph(int value)
    {
    }

    @Override
    public void encryptionStarted() 
    {
    }

    @Override
    public void encryptionProgress(int filesProgress, int fileProgress)
    {
//        log("filesProgress: " + filesProgress + " fileProgress: " + fileProgress);
    }
    
    @Override
    public void encryptionFinished()
    {
    }
}
