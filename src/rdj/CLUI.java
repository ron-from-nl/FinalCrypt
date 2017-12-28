/*
 * © copyleft 2018 ron
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
    Version version;

    public CLUI(String[] args)
    {
        boolean ifset = false, cfset = false;
        boolean validInvocation = true;
        boolean negatePattern = false;

        ArrayList<Path> inputFilesPathList = new ArrayList<>();
        Path inputFilePath = null;
        Path cipherFilePath = null;
        Path outputFilePath = null;
        version = new Version(this);
        version.checkCurrentlyInstalledVersion();

        String pattern = "*";        
        
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
//            else if ( args[paramCnt].equals("--version"))                                                           { Version upd = new Version(this); println(upd.getProcuct() + " " + upd.getCurrentlyInstalledOverallVersionString()); System.exit(0); }
            else if ( args[paramCnt].equals("--version"))                                                           { println(version.getProcuct() + " " + version.getCurrentlyInstalledOverallVersionString()); System.exit(0); }
            else if ( args[paramCnt].equals("--update"))                                                            { version.checkLatestOnlineVersion(); log(version.getUpdateStatus()); System.exit(0); }
            else if ( args[paramCnt].equals("-b")) { if ( validateIntegerString(args[paramCnt + 1]) )               { finalCrypt.setBufferSize(Integer.valueOf( args[paramCnt + 1] ) * 1024 ); paramCnt++; } else { error("\nError: Invalid Option Value [-b size]" + "\n"); usage(); }}

            // File Parameters
            else if ( args[paramCnt].equals("-i")) { inputFilePath = Paths.get(System.getProperty("user.dir"), args[paramCnt+1]); inputFilesPathList.add(inputFilePath); ifset = true; paramCnt++; }
//            else if ( args[paramCnt].equals("-w")) { if ( args[paramCnt+1].startsWith("-") ) { String param = args[paramCnt+1].replace("-", ""); pattern = "glob:"; for (char chr:param.toCharArray()) { pattern += "[!" + chr + "]"; }  paramCnt++; } else { pattern = "glob:" + args[paramCnt+1]; paramCnt++; }}
            else if ( args[paramCnt].equals("-w")) { negatePattern = false; pattern = "glob:" + args[paramCnt+1]; paramCnt++; }
            else if ( args[paramCnt].equals("-W")) { negatePattern = true; pattern = "glob:" + args[paramCnt+1]; paramCnt++; }
            else if ( args[paramCnt].equals("-r")) { pattern = "regex:" + args[paramCnt+1]; paramCnt++; }
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
                    if (finalCrypt.getVerbose()) { status("Input parameter: " + inputFilePathItem + " exist\n", true); }
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
                if (finalCrypt.getVerbose()) { status("Cipher parameter: " + finalCrypt.getCipherFilePath() + " exist\n", true); }
            } else { usage(); }
        }
        else
        { 
            error("Cipher parameter: " + cipherFilePath + " does not exists\n"); usage();
        }            

//      Look for encryptable files & detect cipher file targeted among all input filess
        boolean hasEncryptableItem = false;
        for (Path path:finalCrypt.getExtendedPathList(inputFilesPathList, finalCrypt.getCipherFilePath(), pattern, negatePattern, true) )
        {
            if ((path.compareTo(finalCrypt.getCipherFilePath()) == 0))
            {
                status("Warning: cipher-file: " + finalCrypt.getCipherFilePath().toAbsolutePath() + " will be excluded!\n", true);
            }
            else
            {
                if (Files.isRegularFile(path)) { hasEncryptableItem = true; }
            }
        }

//      // Input validation past, last check on encryptable items in list
        if ( !hasEncryptableItem )
        {
            log("Nothing to encrypt\n");
        }
        else
        {
//          All is well, now convert small parameter list into recusive list
//          Convert small PathList from parameters into ExtendedPathList (contents of subdirectory parameters as inputFile)
            ArrayList<Path> inputFilesPathListExtended = finalCrypt.getExtendedPathList(inputFilesPathList, finalCrypt.getCipherFilePath(), pattern, negatePattern, false);
            // Set the Options

            // Set Buffer Size
            int cipherSize = 0; try { cipherSize = (int)Files.size(finalCrypt.getCipherFilePath()); } catch (IOException ex) { error("Files.size(finalCrypt.getCipherFilePath()) " + ex + "\n"); }
            if ( cipherSize < finalCrypt.getBufferSize())
            {
                finalCrypt.setBufferSize(cipherSize);
                status("BufferSize is limited to cipherfile size: " + Stats.getHumanSize(finalCrypt.getBufferSize(), 1) + " \n", true);
            }
            else
            {
                status("BufferSize is set to: " + Stats.getHumanSize(finalCrypt.getBufferSize(), 1) + " \n", true);
            }

            // Start Encryption
            this.encryptionStarted();
            finalCrypt.encryptSelection(inputFilesPathListExtended, finalCrypt.getCipherFilePath());
        }
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
        log("                 [--version]      Print " + version.getProcuct() + " version.\n");
        log("                 [--update]       Check for online updates.\n");
        log("            [--txt]               Print text calculations.\n");
        log("            [--bin]               Print binary calculations.\n");
        log("            [--dec]               Print decimal calculations.\n");
        log("            [--hex]               Print hexadecimal calculations.\n");
        log("            [--chr]               Print character calculations.\n");
        log("            [-b size]             Changes default I/O buffer size (size = KB) (default 1024 KB).\n");
        log("Parameters:\n");
        log("            <-i \"file/dir\">       The file or dir you want to encrypt (encrypts dir recursively).\n");
        log("            [-w \'wildcard\']       File wildcard include filter. Uses: \"Glob Patterns Syntax\".\n");
        log("            [-W \'wildcard\']       File wildcard exclude filter. Uses: \"Glob Patterns Syntax\".\n");
        log("            [-r \'regex\']          File regular expression filter. Advanced filename filter!\n");
        log("            <-c \"cipherfile\">     The file that encrypts your file(s). Keep cipherfile SECRET!\n");
        log("                                  A cipher-file is a unique file like a personal photo or video!\n");
        log("Examples:\n");
        log("            # Encrypt myfile with mycipherfile\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -i myfile -c mycipherfile\n");
        log("\n");
        log("            # Encrypt myfile and all content in mydir with mycipherfile\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -i myfile -i mydir -c mycipherfile\n");
        log("\n");
        log("            # Encrypt all files with *.bit extension in mydir with mycipherfile\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -i mydir -w '*.bit' -c mycipherfile\n");
        log("\n");
        log("            # Encrypt all files without *.bit extension in mydir with mycipherfile\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -i mydir -W '*.bit' -c mycipherfile\n");
        log("\n");
        log("            # Encrypt all files with *.bit extension in mydir with mycipherfile\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -i mydir -r '^.*\\.bit$' -c mycipherfile\n");
        log("\n");
        log("            # Encrypt all files excluding .bit extension in mydir with mycipherfile\n");
        log("            java -cp FinalCrypt.jar rdj/CLUI -i mydir -r '(?!.*\\.bit$)^.*$' -c mycipherfile\n\n");
        log(Version.getProcuct() + " " + version.checkCurrentlyInstalledVersion() + " Author: " + Version.getAuthor() + " " + Version.getCopyleft() + "\n\n");
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
        status(message, true);
    }

    @Override
    public void status(String status, boolean log)
    {
//        if (log) { log(status); } // for future
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
