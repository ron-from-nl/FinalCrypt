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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import static java.nio.channels.Channels.newChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Calendar;

public class Version
{
    private UI ui;
    private static final String COMPANYNAME = "Private Person";
    private static final String PRODUCTNAME = "FinalCrypt";
    private static final String AUTHOR = "Ron de Jong";
    private static final String AUTHOREMAIL = "ronuitzaandam@gmail.com";
    private static final String COPYRIGHTTYPE = "Creative Commons License: (CC BY-NC-ND 4.0)" + Calendar.getInstance().get(Calendar.YEAR);
    private static final String COPYRIGHT = "© 2017-" + Calendar.getInstance().get(Calendar.YEAR);
    private static String currentOverallVersionString = "";
    private String latestOverallVersionString = "";
    private static int currentVersionTotal = 0;
    private int latestVersionTotal = 0;
    private InputStream istream = null;
    private static final String REMOTEVERSIONFILEURLSTRING =    "https://raw.githubusercontent.com/ron-from-nl/FinalCrypt/master/src/rdj/VERSION";
    public static final String REMOTEPACKAGEDOWNLOADURISTRING = "https://github.com/ron-from-nl/FinalCrypt/releases/tag/latest/";
    private URL remoteURL = null;
    private ReadableByteChannel currentVersionByteChannel = null;
    private ReadableByteChannel latestVersionByteChannel = null;
    private ByteBuffer byteBuffer; 
    
    private boolean currentVersionIsKnown = false;
    private boolean latestVersionIsKnown = false;
    private boolean updateAvailable = false;

    public Version(UI ui)
    {
        this.ui = ui;
    }
    
    public String checkCurrentlyInstalledVersion()
    {
        istream = getClass().getResourceAsStream("UPDATE");

//      Read the local VERSION file
        currentOverallVersionString = "";
        currentVersionByteChannel = newChannel(istream);
        byteBuffer = ByteBuffer.allocate(512);
        try {
            while(currentVersionByteChannel.read(byteBuffer) > 0)
            {
                byteBuffer.flip();
                while(byteBuffer.hasRemaining())
                {
                    currentOverallVersionString += (char) byteBuffer.get();
                }
            }
        } catch (IOException ex) { ui.error(ex.getMessage()+"\r\n"); }
        try { currentVersionByteChannel.close(); } catch (IOException ex) { ui.error(ex.getMessage()+"\r\n"); }        

        currentOverallVersionString.replaceAll("\\p{C}", "?");
//        currentOverallVersionString.replaceAll("[^\\d. ]", "");
	String[] lines = currentOverallVersionString.split(System.getProperty("line.separator"));
	String[][] fields = new String[lines.length][2];
	
	int lineCounter = 0;
        for (String line:lines)
	{
	    String field = ""; String value = "";
	    field = line.substring(line.indexOf("[")+1, line.indexOf("]"));
	    value = line.substring(line.indexOf("{")+1, line.lastIndexOf("}"));
	    fields[lineCounter][0] = field; fields[lineCounter][1] = value; 
	    System.out.println("Field: " + fields[lineCounter][0] + " Value: " + fields[lineCounter][1]);
	    lineCounter++;
	}
	
	
	
	
	String currentVersionString = currentOverallVersionString.substring(0, currentOverallVersionString.indexOf(".")).replaceAll("[^\\d]", "");
        String currentUpgradeString = currentOverallVersionString.substring(currentOverallVersionString.indexOf("."), currentOverallVersionString.lastIndexOf(".")).replaceAll("[^\\d]", "");
        String currentUpdateString = currentOverallVersionString.substring(currentOverallVersionString.lastIndexOf("."), currentOverallVersionString.length()).replaceAll("[^\\d]", "");
        
//        int currentVersion = Integer.parseInt(currentVersionString);
//        int currentUpgrade = Integer.parseInt(currentUpgradeString);
//        int currentUpdate = Integer.parseInt(currentUpdateString);
        
//        currentVersionTotal = (currentVersion * 100) + (currentUpgrade * 10) + (currentUpdate * 1);
        currentVersionTotal = (1 * 100) + (8 * 10) + (2 * 1);
        currentOverallVersionString = currentVersionString + "." + currentUpgradeString + "." + currentUpdateString;
        currentVersionIsKnown = true;
        return currentOverallVersionString;
    }

    public String checkLatestOnlineVersion()
    {
//      Read the remote VERSION file
        latestOverallVersionString = "Unknown";
        try { remoteURL = new URL(REMOTEVERSIONFILEURLSTRING); } catch (MalformedURLException ex) { ui.error(ex.getMessage()+"\r\n"); }
        try { latestVersionByteChannel = Channels.newChannel(remoteURL.openStream()); } catch (IOException ex) { ui.error(ex.getMessage()+"\r\n"); } // null pointer at no connect
        byteBuffer = ByteBuffer.allocate(512);
        try
        {
            latestOverallVersionString = "";
            while(latestVersionByteChannel.read(byteBuffer) > 0)
            {
                byteBuffer.flip();
                while(byteBuffer.hasRemaining())
                {
                    latestOverallVersionString += (char) byteBuffer.get();
                }
            }
        } catch (IOException ex) { ui.error(ex.getMessage()+"\r\n"); }
        try { latestVersionByteChannel.close(); } catch (IOException ex) { ui.error(ex.getMessage()+"\r\n"); }

        latestOverallVersionString.replaceAll("\\p{C}", "?");

        String latestVersionString = latestOverallVersionString.substring(0, latestOverallVersionString.indexOf(".")).replaceAll("[^\\d]", "");
        String latestUpgradeString = latestOverallVersionString.substring(latestOverallVersionString.indexOf("."), latestOverallVersionString.lastIndexOf(".")).replaceAll("[^\\d]", "");
        String latestUpdateString = latestOverallVersionString.substring(latestOverallVersionString.lastIndexOf("."), latestOverallVersionString.length()).replaceAll("[^\\d]", "");

        int latestVersion = Integer.parseInt(latestVersionString);
        int latestUpgrade = Integer.parseInt(latestUpgradeString);
        int latestUpdate = Integer.parseInt(latestUpdateString);
        latestVersionTotal = (latestVersion * 100) + (latestUpgrade * 10) + (latestUpdate * 1);
        latestOverallVersionString = latestVersionString + "." + latestUpgradeString + "." + latestUpdateString;
        latestVersionIsKnown = true;
        return latestOverallVersionString;
    }

    public String getLatestOnlineOverallVersionString() { return latestOverallVersionString; }
    public String getCurrentlyInstalledOverallVersionString() { return currentOverallVersionString; }

    public String getUpdateStatus() 
    {
        String returnString = "";
        if (( currentVersionIsKnown) && ( latestVersionIsKnown))
        {
            if      (currentVersionTotal < latestVersionTotal)
            {
                returnString += getProcuct() + " " + currentOverallVersionString + " can be updated to version: " + latestOverallVersionString + " at: " + REMOTEPACKAGEDOWNLOADURISTRING + "\r\n"; 

            } 
            else if (currentVersionTotal > latestVersionTotal)
            {
                returnString += getProcuct() + " " + currentOverallVersionString + " is a development version!\r\n";
            } 
            else
            {
                returnString += getProcuct() + " " + currentOverallVersionString + " is up to date\r\n";
            } 
        }
        else
        {
            if (!currentVersionIsKnown)   { returnString = "Could not retrieve the locally installed " + Version.getProcuct() + " Version\r\n"; }
            if (!latestVersionIsKnown)    { returnString = "Could not retrieve the latest online " + Version.getProcuct() + " Version\r\n"; }
        }
        return returnString;
    }

    public boolean versionIsDifferent()     { if      ( currentVersionTotal != latestVersionTotal ) { return true; } else { return false; } }
    public boolean versionCanBeUpdated()    { if      ( currentVersionTotal < latestVersionTotal )  { return true; } else { return false; } }
    public boolean versionIsDevelopment()   { if      ( currentVersionTotal > latestVersionTotal )  { return true; } else { return false; } }    

    public static String getCopyright()     { return COPYRIGHT; }
    public static String getAuthor()        { return AUTHOR; }
    public static String getAuthorEmail()   { return AUTHOREMAIL; }
    public static String getProcuct()       { return PRODUCTNAME; }
    public static String getCompany()       { return COMPANYNAME; }
}
