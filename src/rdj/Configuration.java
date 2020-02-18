/*
 * CC BY-NC-ND 4.0 2018 Ron de Jong (ron@finalcrypt.org)
 *
 * This is free software; you can redistribute it 
 * under the terms of the Creative Common License
 * Creative Common License: (CC BY-NC-ND 4.0) as published by
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

/*
System.getProperty("user.dir");
"file.separator"	Character that separates components of a file path. This is "/" on UNIX and "\" on Windows.
"java.class.path"	Path used to find directories and JAR archives containing class files. Elements of the class path are separated by a platform-specific character specified in the path.separator property.
"java.home"             Installation directory for Java Runtime Environment (JRE)
"java.vendor"           JRE vendor name
"java.vendor.url"	JRE vendor URL
"java.version"          JRE version number
"line.separator"	Sequence used by operating system to separate lines in text files
"os.arch"               Operating system architecture
"os.name"               Operating system name
"os.version"            Operating system version
"path.separator"	Path separator character used in java.class.path
"user.dir"              User working directory
"user.home"             User home directory
"user.name"             User account name
*/
package rdj;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.logging.*;

public class Configuration
{    
    private UI ui;

    public   String platform;
    private  String fileSeparator;
    private  String lineTerminator;
    private  Path  dataDirPath;
    private  Path logDirPath;
    private  Path logFilePath;
    private  Path errorFilePath;
    private final Calendar currentTimeCalendar;
    private final String timeStampString;
    private final String timeString;

    public Configuration(UI ui)
    {
        this.ui = ui;
        platform = System.getProperty("os.name").toLowerCase();
        if ( platform.indexOf("windows") != -1 ) { fileSeparator = "\\"; lineTerminator = "\r\n"; } else { fileSeparator = "/"; lineTerminator = "\r\n"; }

        // Just for the logToFile
        currentTimeCalendar = Calendar.getInstance();
        timeStampString = "" +
        String.format("%04d", currentTimeCalendar.get(Calendar.YEAR)) +
        String.format("%02d", currentTimeCalendar.get(Calendar.MONTH) + 1) +
        String.format("%02d", currentTimeCalendar.get(Calendar.DAY_OF_MONTH)) + "_" +
        String.format("%02d", currentTimeCalendar.get(Calendar.HOUR_OF_DAY)) +
        String.format("%02d", currentTimeCalendar.get(Calendar.MINUTE)) +
        String.format("%02d", currentTimeCalendar.get(Calendar.SECOND));

	timeString = "" +
        String.format("%04d", currentTimeCalendar.get(Calendar.YEAR)) + "-" +
        String.format("%02d", currentTimeCalendar.get(Calendar.MONTH) + 1) + "-" +
        String.format("%02d", currentTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " +
        String.format("%02d", currentTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
        String.format("%02d", currentTimeCalendar.get(Calendar.MINUTE)) + ":" +
        String.format("%02d", currentTimeCalendar.get(Calendar.SECOND));
        
        dataDirPath =	    Paths.get(System.getProperty("user.home"), ".finalcrypt");
        logDirPath =	    Paths.get(dataDirPath.toString(),"log");
        logFilePath =	    Paths.get(logDirPath.toString(),"finalcrypt_" + timeStampString+ ".log");
        errorFilePath =	    Paths.get(logDirPath.toString(),"finalcrypt_" + timeStampString+ ".err");

        boolean missingDirsDetected = false;
        boolean missingCriticalDirsDetected = false;
        if (Files.notExists(dataDirPath))	{ try { Files.createDirectory(dataDirPath); println("Action:  Config: Creating missing directory: " + dataDirPath); } catch (IOException ex)		{ ui.log("Error: Files.createDirectory(" + dataDirPath + ");: " + ex.getMessage(), true, true, true, false, false); } }
        if (Files.notExists(logDirPath))	{ try { Files.createDirectory(logDirPath); println("Action:  Config: Creating missing directory: " + logDirPath); } catch (IOException ex)		{ ui.log("Error: Files.createDirectory(" + dataDirPath + ");: " + ex.getMessage(), true, true, true, false, false); } }
    }

    // Just the getters and setters

    public Path	    getDataDirPath()	{return dataDirPath;}
    public Path	    getLogDirPath()	{return logDirPath;}
    public Path	    getLogFilePath()	{return logFilePath;}
    public Path	    getErrFilePath()	{return errorFilePath;}
    public String   getTime()		{return timeString;}
    
    private void println(String string)
    {
        System.out.println(string);
    }
}
