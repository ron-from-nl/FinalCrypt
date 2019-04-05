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

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import static java.nio.channels.Channels.newChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Calendar;

public class Version
{
    private UI ui;
    private static final String COMPANYNAME =				"Private Person";
    private static final String PRODUCTNAME =				"FinalCrypt";
    private static final String AUTHOR =				"Ron de Jong";
    private static final String AUTHOREMAIL =				"ronuitzaandam@gmail.com";
    private static final String LICENSE =				"Creative Commons License: (CC BY-NC-ND 4.0)";
    private static final String COPYRIGHT =				"© 2017-" + Calendar.getInstance().get(Calendar.YEAR);
    private static String currentOverallVersionString =			"";
    private String latestOverallVersionString =				"";
    private static int currentVersionTotal =				0;
    private int latestVersionTotal =					0;
    private InputStream istream =					null;
    private static final String LOCALVERSIONFILEURLSTRING =		"VERSION2";
    private static       String localContent =				"";
    private static final String[] REMOTEVERSIONFILEURLSTRINGARRAY =	{
									    "http://www.finalcrypt.org/VERSION2"
									    ,"https://raw.githubusercontent.com/ron-from-nl/FinalCrypt/master/src/rdj/VERSION2"
									    ,"https://sourceforge.net/p/finalcrypt/code/ci/master/tree/src/rdj/VERSION2?format=raw"
									};    
    
    private static       String remoteContent =				"";
    
    public static final String WEBSITEURISTRING =			"http://www.finalcrypt.org/";

    public static final String REMOTEPACKAGEDOWNLOADURISTRING =		"http://www.finalcrypt.org/";
    public static final String[] REMOTEPACKAGEDOWNLOADURISTRINGARRAY =	{
									    "http://www.finalcrypt.org/downloads/"
									    ,"https://github.com/ron-from-nl/FinalCrypt/releases/"
									    ,"https://sourceforge.net/projects/finalcrypt/files/"
									};
    private URL remoteURL = null;
    private ReadableByteChannel currentVersionByteChannel =		null;
    private ReadableByteChannel latestVersionByteChannel =		null;
    private ByteBuffer byteBuffer; 
    
    private boolean currentVersionIsKnown =				false;
    private boolean latestVersionIsKnown =				false;
    private boolean updateAvailable =					false;
    private String[] localFields;
    private String[] localValues;
    private String[] remoteFields;
    private String[] remoteValues;
    private String latestReleaseNotesString;
    private String latestReleaseMessageString;
    private String latestAlertSubjectString;
    private String latestAlertMessageString;
    public int currentInstalledVersion;
    public int latestRemoteVersion;

    public Version(UI ui)
    {
        this.ui = ui;
	latestReleaseNotesString = "";
	latestReleaseMessageString = "";
	latestAlertSubjectString = "";
	latestAlertMessageString = "";
    }
    
    synchronized public String checkCurrentlyInstalledVersion(UI ui)
    {
        istream = getClass().getResourceAsStream(LOCALVERSIONFILEURLSTRING);
	
//      Read the local VERSION file
        currentOverallVersionString = "Unknown";
        currentVersionByteChannel = newChannel(istream);
        byteBuffer = ByteBuffer.allocate(1024); byteBuffer.clear(); localContent = "";
        
	try { while(currentVersionByteChannel.read(byteBuffer) > 0) { byteBuffer.flip(); while(byteBuffer.hasRemaining()){localContent += (char) byteBuffer.get();}}}
	catch (IOException ex) { ui.log("Error: Version.checkCurrentlyInstalledVersion IOException: Channel.read(..) " + ex.getMessage()+"\r\n", true, true, true, true, false); }
        
	try { currentVersionByteChannel.close(); } catch (IOException ex) { ui.log("Error: Version.checkCurrentlyInstalledVersion IOException: Channel.close(..) " +ex.getMessage()+"\r\n", true, true, true, true, false); }        

//        localContent.replaceAll("\\p{C}", "?");
//	String[] lines = localContent.split(System.getProperty("line.separator"));
	String[] lines = localContent.split("\n"); // VERSION2 file was created on linux with unix newlines \n
	
	localFields = new String[lines.length];
	localValues = new String[lines.length];

//	Convert lines to fields array
	int c = 0; for (String line:lines)
	{
	    localFields[c] = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
	    localValues[c] = line.substring(line.indexOf("{") + 1, line.lastIndexOf("}"));
	    c++;
	}
	for (int x = 0; x < (localFields.length); x++)
	{
//	    ui.log("LField: " + localFields[x] + " LValue: " + localValues[x] + "\r\n");
	    if (localFields[x].toLowerCase().equals("Version".toLowerCase())) { currentOverallVersionString = localValues[x]; }
	}
	
//	ui.log("currentOverallVersionString: " + currentOverallVersionString + "\r\n");
	String currentVersionString = currentOverallVersionString.substring(0, currentOverallVersionString.indexOf(".")).replaceAll("[^\\d]", "");
        String currentUpgradeString = currentOverallVersionString.substring(currentOverallVersionString.indexOf("."), currentOverallVersionString.lastIndexOf(".")).replaceAll("[^\\d]", "");
        String currentUpdateString = currentOverallVersionString.substring(currentOverallVersionString.lastIndexOf("."), currentOverallVersionString.length()).replaceAll("[^\\d]", "");
        currentInstalledVersion = Integer.parseInt(currentVersionString); int currentUpgrade = Integer.parseInt(currentUpgradeString); int currentUpdate = Integer.parseInt(currentUpdateString);
        currentVersionTotal = (currentInstalledVersion * 100) + (currentUpgrade * 10) + (currentUpdate * 1);
//	ui.log("currentVersion: " + currentVersion + "\r\n");
        currentOverallVersionString = currentVersionString + "." + currentUpgradeString + "." + currentUpdateString;
        currentVersionIsKnown = true;
	
        return currentOverallVersionString;
    }

    synchronized public String checkLatestOnlineVersion(UI ui)
    {
//      Read the remote VERSION file
	
	latestVersionIsKnown = false;
        latestOverallVersionString = "Unknown";
	
	loop: for(String REMOTEVERSIONFILEURLSTRING:REMOTEVERSIONFILEURLSTRINGARRAY)
	{	    
	    boolean failed = false;
	    byteBuffer = ByteBuffer.allocate(1024); byteBuffer.clear(); remoteContent = "";
	    ui.log("Checking: " + REMOTEVERSIONFILEURLSTRING + "\r\n", false, false, true, false, false);

	    try { remoteURL = new URL(REMOTEVERSIONFILEURLSTRING); }
	    catch (MalformedURLException ex)	{ ui.log("Error: Version.checkLatestOnlineVersion MalformedURLException: new URL(" + REMOTEVERSIONFILEURLSTRING +") (URL Typo?)\r\n", false, true, true, true, false); failed = true; continue; }
	    
	    InputStream inputStream; try { inputStream = remoteURL.openStream(); } catch (IOException ex) { ui.log("Error: Version.checkLatestOnlineVersion IOException: inputStream = \"" + REMOTEVERSIONFILEURLSTRING +"\".openStream()) (webserver up? file exist?)\r\n", false, true, true, true, false); failed = true; continue; }  finally { } // null pointer at no connect
	    	    
	    latestVersionByteChannel = Channels.newChannel(inputStream);
	    
	    try {  while(latestVersionByteChannel.read(byteBuffer) > 0) { byteBuffer.flip(); while(byteBuffer.hasRemaining()) { remoteContent += (char) byteBuffer.get(); } } }
	    catch (IOException ex)		{ ui.log("Error: Version.checkLatestOnlineVersion IOException: Channels.read(..) " + ex.getMessage()+"\r\n", false, true, true, true, false); failed = true; continue; }
	    
	    try { latestVersionByteChannel.close(); }
	    catch (IOException ex)		{ ui.log("Error: Version.checkLatestOnlineVersion IOException: Channels.close(..)  " + ex.getMessage()+"\r\n", false, true, true, true, false); continue; }

//          remoteContent.replaceAll("\\p{C}", "?");
//	    String[] lines = remoteContent.split(System.getProperty("line.separator"));

	    if (! failed)
	    {
		String[] lines = remoteContent.split("\n"); // VERSION2 file was create on linux with unix newlines \n

		remoteFields = new String[lines.length];
		remoteValues = new String[lines.length];

    //	    Convert lines to fields array
		if (lines.length > 0)
		{		    
		    int c = 0; for (String line:lines)
		    {
			
			if ( (line.contains("[")) && (line.contains("]")) && (line.contains("{")) && (line.contains("}")) )
			{
			    if ((line.substring(line.indexOf("[") + 1, line.indexOf("]")).length() > 0) && line.substring(line.indexOf("{") + 1, line.lastIndexOf("}")).length() > 0)
			    {
				remoteFields[c] = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
				remoteValues[c] = line.substring(line.indexOf("{") + 1, line.lastIndexOf("}"));
				c++;
			    }			    
			}
		    }
		    
		    if ((remoteFields.length > 4) && (c > 2))
		    {
			for (int x = 0; x < (c); x++)
			{
//			    ui.test("RField: " + remoteFields[x] + " RValue: " + remoteValues[x] + "\r\n");
			    if (remoteFields[x].toLowerCase().equals("Version".toLowerCase()))		{ latestOverallVersionString =	remoteValues[x]; }
			    if (remoteFields[x].toLowerCase().equals("Release Notes".toLowerCase()))	{ latestReleaseNotesString =	remoteValues[x]; }
			    if (remoteFields[x].toLowerCase().equals("Release Message".toLowerCase()))	{ latestReleaseMessageString =	remoteValues[x]; }
			    if (remoteFields[x].toLowerCase().equals("Alert Subject".toLowerCase()))	{ latestAlertSubjectString =	remoteValues[x]; }
			    if (remoteFields[x].toLowerCase().equals("Alert Message".toLowerCase()))	{ latestAlertMessageString =	remoteValues[x]; }
			}

			String latestVersionString = latestOverallVersionString.substring(0, latestOverallVersionString.indexOf(".")).replaceAll("[^\\d]", "");
			String latestUpgradeString = latestOverallVersionString.substring(latestOverallVersionString.indexOf("."), latestOverallVersionString.lastIndexOf(".")).replaceAll("[^\\d]", "");
			String latestUpdateString = latestOverallVersionString.substring(latestOverallVersionString.lastIndexOf("."), latestOverallVersionString.length()).replaceAll("[^\\d]", "");
			latestRemoteVersion = Integer.parseInt(latestVersionString); int latestUpgrade = Integer.parseInt(latestUpgradeString); int latestUpdate = Integer.parseInt(latestUpdateString);
			latestVersionTotal = (latestRemoteVersion * 100) + (latestUpgrade * 10) + (latestUpdate * 1);
			latestOverallVersionString = latestVersionString + "." + latestUpgradeString + "." + latestUpdateString;
			latestVersionIsKnown = true;
			
			if ((latestOverallVersionString.length()>0)&&(latestOverallVersionString.length()>0)&&(latestOverallVersionString.length()>0)&&(latestVersionIsKnown)) { return latestOverallVersionString; }
		    } continue;

		} continue;
	    } continue;

	} if (latestVersionIsKnown) { return latestOverallVersionString; }
	return "Could not check for new updates (Internet?)";
    }

    synchronized public static void openWebSite(UI ui)
    {
	String[] WEBSITEURLSTRINGARRAY =	{
						    ""
						    ,"http://www.finalcrypt.org/" // tested
						    ,"http://sites.google.com/site/ronuitholland/home/finalcrypt/" // tested
						    ,"http://finalcrypt.000webhostapp.com/" // tested
						    ,"http://www.majorgeeks.com/files/details/finalcrypt.html" // tested
						};
        String identifierExpected = PRODUCTNAME;
	
	loop: for(String WEBSITEURLSTRING:WEBSITEURLSTRINGARRAY)
	{
	    if (! WEBSITEURLSTRING.isEmpty())
	    {
		boolean failed = false;
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024); byteBuffer.clear(); String remoteContent = "";
		ui.log("Checking: " + WEBSITEURLSTRING + "\r\n", false, false, true, false, false);

		URL remoteURL;
		try { remoteURL = new URL(WEBSITEURLSTRING); }
		catch (MalformedURLException ex)	{ ui.log("Error: Version.openWebSite MalformedURLException: new URL(" + WEBSITEURLSTRING +") (URL Typo?)\r\n", false, true, true, true, false); failed = true; continue; }

		InputStream inputStream; try { inputStream = remoteURL.openStream(); } catch (IOException ex) { ui.log("Error: Version.openWebSite IOException: remoteURL.openStream()) " + ex.getMessage() + "\r\n", false, true, true, true, false); failed = true; continue; }  finally { } // null pointer at no connect

		ReadableByteChannel latestVersionByteChannel = Channels.newChannel(inputStream);

		try {  while(latestVersionByteChannel.read(byteBuffer) > 0) { byteBuffer.flip(); while(byteBuffer.hasRemaining()) { remoteContent += (char) byteBuffer.get(); } } }
		catch (IOException ex)		{ ui.log("Error: Version.openWebSite IOException: Channels.read(..) " + ex.getMessage()+"\r\n", false, true, true, true, false); failed = true; continue; }

		try { latestVersionByteChannel.close(); }
		catch (IOException ex)		{ ui.log("Error: Version.openWebSite IOException: Channels.close(..)  " + ex.getMessage()+"\r\n", false, true, true, true, false); continue; }

		if (! failed)
		{
		    if ( (remoteContent.toLowerCase().contains(identifierExpected.toLowerCase()) ))
		    {
			Thread openWebSiteThread;
			openWebSiteThread = new Thread(() ->
			{
			    try { try {  Desktop.getDesktop().browse(new URI(WEBSITEURLSTRING)); }
			    catch (URISyntaxException ex) { ui.log(ex.getMessage(), true, true, true, true, false); }}
			    catch (IOException ex) { ui.log(ex.getMessage(), true, true, true, true, false); }
			});
			openWebSiteThread.setName("openWebSiteThread");
			openWebSiteThread.setDaemon(true);
			openWebSiteThread.start();
			break;
		    }
		}
	    }
	}
    }
    
    public String getLatestOnlineOverallVersionString()		{ return latestOverallVersionString; }
    public String getCurrentlyInstalledOverallVersionString()	{ return currentOverallVersionString; }
    public String getLatestReleaseNotesString()			{ return latestReleaseNotesString; }
    public String getLatestVersionMessageString()		{ return latestReleaseMessageString; }
    public String getLatestAlertSubjectString()			{ return latestAlertSubjectString; }
    public String getLatestAlertMessageString()			{ return latestAlertMessageString; }

    public String getUpdateStatus() 
    {
        String returnString = "";
        if (( currentVersionIsKnown) && ( latestVersionIsKnown))
        {
            if      (currentVersionTotal < latestVersionTotal)
            {
                returnString += getProductName() + " " + currentOverallVersionString + " can be updated to version: " + latestOverallVersionString + " at: " + REMOTEPACKAGEDOWNLOADURISTRING + "\r\n"; 
		if (! getLatestReleaseNotesString().isEmpty())	    { returnString += getLatestReleaseNotesString() + "\r\n"; }
		if (! getLatestVersionMessageString().isEmpty())    { returnString += getLatestVersionMessageString() + "\r\n"; }
            } 
            else if (currentVersionTotal > latestVersionTotal)
            {
                returnString += getProductName() + " " + currentOverallVersionString + " is a development version!\r\n";
            } 
            else
            {
                returnString += getProductName() + " " + currentOverallVersionString + " is up to date\r\n";
            } 
        }
        else
        {
            if (!currentVersionIsKnown)   { returnString = "Could not retrieve the locally installed " + Version.getProductName() + " Version\r\n"; }
            if (!latestVersionIsKnown)    { returnString = "Could not retrieve the latest online " + Version.getProductName() + " Version\r\n"; }
        }
        return returnString;
    }

    public boolean latestVersionIsKnown()   { return latestVersionIsKnown; }    
    public boolean versionIsDifferent()     { if ((latestVersionIsKnown) && ( currentVersionTotal != latestVersionTotal )) { return true; } else { return false; } }
    public boolean versionCanBeUpdated()    { if ((latestVersionIsKnown) && ( currentVersionTotal < latestVersionTotal ))  { return true; } else { return false; } }
    public boolean versionIsDevelopment()   { if ((latestVersionIsKnown) && ( currentVersionTotal > latestVersionTotal ))  { return true; } else { return false; } }    

    public static String getCopyright()     { return COPYRIGHT; }
    public static String getLicense()	    { return LICENSE; }
    public static String getAuthor()        { return AUTHOR; }
    public static String getAuthorEmail()   { return AUTHOREMAIL; }
    public static String getProductName()   { return PRODUCTNAME; }
    public static String getCompany()       { return COMPANYNAME; }
}
