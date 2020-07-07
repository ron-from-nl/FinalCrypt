/*
 * CC BY-NC-ND 4.0 2017 Ron de Jong (ron@finalcrypt.org)
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
import java.io.*;
import java.lang.management.*;
import java.net.*;
import java.nio.ByteBuffer;
import static java.nio.channels.Channels.newChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.*;
import java.util.Calendar;
import java.util.logging.*;
import javax.net.ssl.*;

public class Version
{
    private static boolean checkOnlineFailed;
    private UI ui;
    private static final String COMPANYNAME =				"Private Person";
    private static final String PRODUCTNAME =				"FinalCrypt";
    private static final String COMMANDLINE =				"java -cp " + PRODUCTNAME.toLowerCase() + ".jar rdj.CLUI";
    private static       String fcInterface =				"";
    private static final String AUTHOR =					"Ron de Jong";
    private static final String AUTHOREMAIL =				"ron@finalcrypt.org";
    private static final String EMAIL =					"info@finalcrypt.org";
    private static final String LICENSE =				"Creative Commons License: (CC BY-NC-ND 4.0)";
    private static final String LICENSE_DESCRIPTION =			"License 2017-" + Calendar.getInstance().get(Calendar.YEAR);
    
    private static final String OS_NAME =				System.getProperty("os.name");
    private static final String OS_VERSION =				System.getProperty("os.version");
    private static final String OS_ARCH =				System.getProperty("os.arch");
    private static final String FILE_ENCODING =				System.getProperty("file.encoding");

    private static final int PROCESSORS =				Runtime.getRuntime().availableProcessors();
    private static final long TOT_MEM =				        ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
    private static final long MAX_MEM =				        Runtime.getRuntime().maxMemory();
    private static final long FREE_MEM =				Runtime.getRuntime().freeMemory();
    private static final long USED_MEM =				Runtime.getRuntime().totalMemory();
    
    private static final String JAVA_VENDOR =				System.getProperty("java.vendor");
    private static final String JAVA_VERSION =				System.getProperty("java.version");
    private static final String CLASS_VERSION =				System.getProperty("java.class.version");

    private static final String JAVA_HOME =				System.getProperty("java.home");
    private static final String JAVA_VM_VERSION =			System.getProperty("java.vm.version");
    private static final String JAVA_VM_NAME =				System.getProperty("java.vm.name");
    private static final String JAVA_RUNTIME_VERSION =			System.getProperty("java.runtime.version");
    
    private static final String USER_COUNTRY =				System.getProperty("user.country");
    private static final String USER_LANGUAGE =				System.getProperty("user.language");

    private static final String USER_NAME =				System.getProperty("user.name");
    private static final String USER_HOME =				System.getProperty("user.home");
    private static final String USER_DIR =				System.getProperty("user.dir");
    private static final int	HTTP_CONNECT_TIMEOUT =			3000;
    
    private static String currentOverallVersionString =			"";
    private static String latestOverallVersionString =			"";
    private static int currentVersionTotal =				0;
    private static int latestVersionTotal =				0;
    private static InputStream istream =				null;
    private static final String LOCALVERSIONFILEURLSTRING =		"VERSION2";
    private static       String localContent =				"";
    public static final String[] WEBSITEURLSTRINGARRAY =		{
									     "http://www.finalcrypt.org/"									// tested
									    ,"https://www.finalcrypt.org/"									// tested
									    ,"http://www.finalcrypt.com/"									// tested
									    ,"https://www.finalcrypt.com/"									// tested
									    ,"https://sourceforge.net/projects/finalcrypt/files/"						// tested
									    ,"https://github.com/ron-from-nl/FinalCrypt/releases/"						// tested
									    ,"https://osdn.net/users/finalcrypt/pf/FinalCrypt/files/"						// tested
									    ,"http://www.majorgeeks.com/files/details/finalcrypt.html"						// tested
									    ,"http://sites.google.com/site/ronuitholland/home/finalcrypt/"					// tested
									    ,"http://duckduckgo.com/?q=finalcrypt+homepage&t=h_&ia=web"						// tested
									    ,"http://www.google.com/search?q=finalcrypt+homepage&oq=finalcrypt+homepage"			// tested
									};

    public static final String[] DOWNLOADSITEURLSTRINGARRAY =		{
									     "http://www.finalcrypt.org/project-6.php"									// tested
									    ,"https://www.finalcrypt.org/project-6.php"									// tested
									    ,"http://www.finalcrypt.com/project-6.php"									// tested
									    ,"https://www.finalcrypt.com/project-6.php"									// tested
									    ,"https://sourceforge.net/projects/finalcrypt/files/"						// tested
									    ,"https://github.com/ron-from-nl/FinalCrypt/releases/"						// tested
									    ,"https://osdn.net/users/finalcrypt/pf/FinalCrypt/files/"						// tested
									    ,"http://www.majorgeeks.com/files/details/finalcrypt.html"						// tested
									    ,"http://sites.google.com/site/ronuitholland/home/finalcrypt/"					// tested
									    ,"http://duckduckgo.com/?q=finalcrypt+homepage&t=h_&ia=web"						// tested
									    ,"http://www.google.com/search?q=finalcrypt+homepage&oq=finalcrypt+homepage"			// tested
									};

//  Emulate bad server response
//  nc -l -p 8080
//  "http://localhost:8080/VERSION2"
    
    private static final String[] REMOTEVERSIONFILEURLSTRINGARRAY =	{
									     "https://www.finalcrypt.org/VERSION2"								// tested
//									    ,"http://localhost:8080/VERSION2"									// tested
									    ,"http://www.finalcrypt.org/VERSION2"								// tested
									    ,"https://www.finalcrypt.com/VERSION2"								// tested
									    ,"http://www.finalcrypt.com/VERSION2"								// tested
									    ,"https://sourceforge.net/p/finalcrypt/code/ci/master/tree/src/rdj/VERSION2?format=raw"		// tested
									    ,"https://raw.githubusercontent.com/ron-from-nl/FinalCrypt/master/src/rdj/VERSION2"			// tested
									    ,"https://osdn.net/users/finalcrypt/pf/FinalCrypt/scm/blobs/master/src/rdj/VERSION2?export=raw"	// tested
									    ,"https://gitlab.com/finalcrypt/finalcrypt/raw/master/src/rdj/VERSION2"				// tested
									};    
    
    private static       String remoteContent =				"";
    
    public static final String WEBSITEURISTRING =			"http://www.finalcrypt.org/";

    public static final String REMOTEPACKAGEDOWNLOADURISTRING =		"http://www.finalcrypt.org/";

    private static URL remoteURL = null;
    private static ReadableByteChannel currentVersionByteChannel =		null;
    private static ReadableByteChannel latestVersionByteChannel =		null;
    private static ByteBuffer byteBufferLocal; 
    private static ByteBuffer byteBufferRemote; 
    
    private static boolean currentVersionIsKnown =				false;
    private static boolean latestVersionIsKnown =				false;
    private static boolean updateAvailable =					false;
    private static String[] localFields;
    private static String[] localValues;
    private static String[] remoteFields;
    private static String[] remoteValues;
    private static String currentReleaseString;
    private static String latestReleaseString;
//    private static String latestReleaseNotesString;
//    private static String latestReleaseMessageString;

    private static String latestAlertSubjectString;
    private static String latestAlertString;
    private static String currentAlertSubjectString;
    private static String currentAlertString;
//    private static String latestAlertMessageString;
    public static int currentInstalledVersion;
    public static int latestRemoteVersion;

    public Version(UI ui)
    {
        this.ui = ui;
	currentReleaseString = ""; // Replacing below 2 lines
	latestReleaseString = ""; // Replacing below 2 lines
//	latestReleaseNotesString = "";
//	latestReleaseMessageString = "";

	latestAlertSubjectString = "";
	latestAlertString = "";
	currentAlertSubjectString = "";
	currentAlertString = "";
//	latestAlertMessageString = "";
    }
    
    public static String getLogHeader(String classname, Version version, Configuration configuration)
    {
	fcInterface = classname;

	String env = "";	
	env +=    "Welcome to:              " + PRODUCTNAME + " " + version.getCurrentlyInstalledOverallVersionString() + "\r\n";
	env += "\r\n";
	env +=    "Interface:               " + fcInterface + "\r\n";
	env +=    "Author:                  " + AUTHOR + "\r\n";
	env +=    "Email:                   " + AUTHOREMAIL + "\r\n";
	env +=    "Logfiles:                " + configuration.getLogDirPath().toString() + "\r\n";
	env +=    "Command line:            " + COMMANDLINE + " --help\r\n";
	env +=    "License:                 " + LICENSE + "\r\n";
	env += "\r\n";
	env +=    "OS Name:                 " + OS_NAME + "\r\n";
	env +=    "OS Architecture:         " + OS_ARCH + "\r\n";
	env +=    "OS Version:              " + OS_VERSION + "\r\n";
	env +=    "OS Time:                 " + configuration.getTime() + "\r\n";
	env +=    "File Encoding:           " + FILE_ENCODING + "\r\n";
	env += "\r\n";
	env +=    "Processors:              " + PROCESSORS + "\r\n";
	env +=    "Total   Memory:          " + Validate.getHumanSize(TOT_MEM, 1,"Bytes") + "\r\n";
//	env +=    "Total   Memory:          " + TOT_MEM + "\r\n";
	env +=    "Maximum Memory:          " + Validate.getHumanSize(MAX_MEM, 1,"Bytes") + "\r\n";
	env +=    "Free    Memory:          " + Validate.getHumanSize(FREE_MEM, 1,"Bytes") + "\r\n";
	env +=    "Used    Memory:          " + Validate.getHumanSize(USED_MEM, 1,"Bytes") + "\r\n";
	env += "\r\n";
	env +=    "Java Version:            " + JAVA_VERSION + "\r\n";
	env +=    "Java Vendor:             " + JAVA_VENDOR + "\r\n";
	env +=    "Java Home:               " + JAVA_HOME + "\r\n";
	env +=    "Java_VM_Name:            " + JAVA_VM_NAME + "\r\n";
	env +=    "Java_VM_Version:         " + JAVA_VM_VERSION + "\r\n";
	env +=    "Java_Runtime_Version:    " + JAVA_RUNTIME_VERSION + "\r\n";
	env +=    "Class Version:           " + CLASS_VERSION + "\r\n";
	env += "\r\n";
	env +=    "User Country:            " + USER_COUNTRY + "\r\n";
	env +=    "User Language:           " + USER_LANGUAGE + "\r\n";
	env +=    "User Name:               " + USER_NAME + "\r\n";
	env +=    "User Home:               " + USER_HOME + "\r\n";
	env +=    "User Dir:                " + USER_DIR + "\r\n";
	env +=    "User Agent:              " + getUserAgent("") + "\r\n";
	env += "\r\n";
	env +=    "Action Symbols           ";
	env += FinalCrypt.UTF8_CREATE_SYMBOL + " = " +		FinalCrypt.UTF8_CREATE_DESC + " | ";
	env += FinalCrypt.UTF8_READ_SYMBOL + " = " +		FinalCrypt.UTF8_READ_DESC + " | ";
	env += FinalCrypt.UTF8_WRITE_SYMBOL + " = " +		FinalCrypt.UTF8_WRITE_DESC + " | ";
	env += FinalCrypt.UTF8_ENCRYPT_SYMBOL + " = " +		FinalCrypt.UTF8_ENCRYPT_DESC + " | ";
	env += FinalCrypt.UTF8_DECRYPT_SYMBOL + " = " +		FinalCrypt.UTF8_DECRYPT_DESC + " | ";
	env += FinalCrypt.UTF8_XOR_NOMAC_SYMBOL + " = " +	FinalCrypt.UTF8_XOR_NOMAC_DESC + " | ";
	env += FinalCrypt.UTF8_SHRED_SYMBOL + " = " +		FinalCrypt.UTF8_SHRED_DESC + " | ";
	env += FinalCrypt.UTF8_CLONE_SYMBOL + " = " +		FinalCrypt.UTF8_CLONE_DESC + " | ";
	env += FinalCrypt.UTF8_DELETE_SYMBOL + " = " +		FinalCrypt.UTF8_DELETE_DESC + " | ";
	env += FinalCrypt.UTF8_PAUSE_SYMBOL + " = " +		FinalCrypt.UTF8_PAUSE_DESC + " | ";
	env += FinalCrypt.UTF8_UNPAUSE_SYMBOL + " = " +		FinalCrypt.UTF8_UNPAUSE_DESC + " | ";
	env += FinalCrypt.UTF8_STOP_SYMBOL + " = " +		FinalCrypt.UTF8_STOP_DESC + " ";
	env += "\r\n";
	env +=    "Data   Symbols           ";
	env += FinalCrypt.UTF8_OLD_TARGET_SYMBOL + " = " +	FinalCrypt.UTF8_OLD_TARGET_DESC + " | ";
	env += FinalCrypt.UTF8_NEW_TARGET_SYMBOL + " = " +	FinalCrypt.UTF8_NEW_TARGET_DESC + " | ";
	env += FinalCrypt.UTF8_MAC_SYMBOL + " = " +		FinalCrypt.UTF8_MAC_DESC + " | ";
	env += FinalCrypt.UTF8_KEY_SYMBOL + " = " +		FinalCrypt.UTF8_KEY_DESC + " | ";
	env += FinalCrypt.UTF8_ATTRIB_SYMBOL + " = " +		FinalCrypt.UTF8_ATTRIB_DESC + " ";
	env += "\r\n";
	env +=	  "Status Symbols           ";
	env += FinalCrypt.UTF8_SUCCEEDED_SYMBOL + " = " +	FinalCrypt.UTF8_SUCCEEDED_DESC + " | ";
	env += FinalCrypt.UTF8_SUCCESSUNKNOWN_SYMBOL + " = " +	FinalCrypt.UTF8_SUCCESSUNKNOWN_DESC + " | ";
	env += FinalCrypt.UTF8_UNSUCCEEDED_SYMBOL + " = " +	FinalCrypt.UTF8_UNSUCCEEDED_DESC + " | ";
	env += FinalCrypt.UTF8_UNENCRYPTABLE_SYMBOL + " = " +	FinalCrypt.UTF8_UNENCRYPTABLE_DESC + " | ";
	env += FinalCrypt.UTF8_UNDECRYPTABLE_SYMBOL + " = " +	FinalCrypt.UTF8_UNDECRYPTABLE_DESC + " ";
	env += "\r\n";
	env += "\r\n";
	
	return env;
    }

    synchronized public String checkCurrentlyInstalledVersion(UI ui)
    {
        istream = getClass().getResourceAsStream(LOCALVERSIONFILEURLSTRING);
	
//      Read the local VERSION file
        currentOverallVersionString = "Unknown";
        currentVersionByteChannel = newChannel(istream);
        byteBufferLocal = ByteBuffer.allocate(100000); byteBufferLocal.clear(); localContent = "";
        
	try { while(currentVersionByteChannel.read(byteBufferLocal) > 0) { byteBufferLocal.flip(); while(byteBufferLocal.hasRemaining()){localContent += (char) byteBufferLocal.get();}}}
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
	    if ( (line != null) && (! line.isEmpty()) && (line.contains("[")) && (line.contains("]")) && (line.contains("{")) && (line.contains("}")) )
	    {
		boolean validLine = false;
		String localField = line.substring(line.indexOf("[") + 1, line.indexOf("]"));	    if (! localField.isEmpty()) { localFields[c] = localField; validLine = true; }
		String localValue = line.substring(line.indexOf("{") + 1, line.lastIndexOf("}"));   if (! localValue.isEmpty()) { localValues[c] = localValue; validLine = true; }
		if (validLine) { c++; }
	    }
	}
	
	if (localFields.length > 0)
	{
	    for (int x = 0; x < (localFields.length); x++)
	    {
//		if ((localFields[x] != null) && (localValues[x] != null))
		if ((localFields[x] != null))
		{
		    if (localValues[x] == null) { localValues[x] = ""; }
//		    ui.test("LField: " + localFields[x] + " LValue: " + localValues[x] + "\r\n");
		    if (localFields[x].toLowerCase().equals("Version".toLowerCase()))
		    {
			currentOverallVersionString = localValues[x];
			String currentVersionString = currentOverallVersionString.substring(0, currentOverallVersionString.indexOf(".")).replaceAll("[^\\d]", "");
			String currentUpgradeString = currentOverallVersionString.substring(currentOverallVersionString.indexOf("."), currentOverallVersionString.lastIndexOf(".")).replaceAll("[^\\d]", "");
			String currentUpdateString = currentOverallVersionString.substring(currentOverallVersionString.lastIndexOf("."), currentOverallVersionString.length()).replaceAll("[^\\d]", "");
			currentInstalledVersion = Integer.parseInt(currentVersionString); int currentUpgrade = Integer.parseInt(currentUpgradeString); int currentUpdate = Integer.parseInt(currentUpdateString);
			currentVersionTotal = (currentInstalledVersion * 100) + (currentUpgrade * 10) + (currentUpdate * 1);
			currentOverallVersionString = currentVersionString + "." + currentUpgradeString + "." + currentUpdateString;
//			ui.test("currentOverallVersionString: " + currentOverallVersionString + "\r\n");
			currentVersionIsKnown = true;
		    }
		    if (localFields[x].toLowerCase().equals("Version Notes".toLowerCase()))	{ currentReleaseString +=	localValues[x] + "\r\n"; }
		    if (localFields[x].toLowerCase().equals("Upgrade Notes".toLowerCase()))	{ currentReleaseString +=	localValues[x] + "\r\n"; }
		    if (localFields[x].toLowerCase().equals("Update Notes".toLowerCase()))	{ currentReleaseString +=	localValues[x] + "\r\n"; }
		    
		    if (localFields[x].toLowerCase().equals("Alert Subject".toLowerCase()))	{ currentAlertSubjectString =	localValues[x]; }
		    if (localFields[x].toLowerCase().equals("Alert Notes".toLowerCase()))	{ currentAlertString +=		localValues[x] + "\r\n"; }
		}
	    }
	    if ((currentOverallVersionString.length()>0)&&(currentOverallVersionString.length()>0)&&(currentOverallVersionString.length()>0)&&(currentVersionIsKnown)) { return currentOverallVersionString; }
	}
	
	return "Could not check your current version (VERSION2 file missing?)";
    }

    private static String encodeValue(UI ui, String value)
    {       
	String returnValue = "";
	try { returnValue = URLEncoder.encode(value, StandardCharsets.UTF_8.toString()); }
	catch (UnsupportedEncodingException ex) { ui.log("Error: Version.checkLatestOnlineVersion URLEncoder.encode(" + value +") (URL Encoding?)\r\n", false, true, true, true, false); }
	return returnValue;
    }
    
    private static String getUserAgent(String connType)
    {       
	String userAgent = "";
	userAgent += getProductName() + "/" + getCurrentlyInstalledOverallVersionString() + " " + fcInterface + " " + connType;
	userAgent += " (" + OS_NAME + " " + OS_VERSION + "; " + OS_ARCH + "; ";
	userAgent += JAVA_VENDOR + " " + JAVA_VERSION + " " + CLASS_VERSION;// + "; ";
//	userAgent += JAVA_VM_NAME + " " + JAVA_VM_VERSION + ")";
	userAgent += ")";
	return userAgent;
    }
    
    
    public static String httpGetRequest(UI ui, String urlString)
    {
	String userAgent = getUserAgent("(HTTP)");
	
	URL url = null;
	try { url = new URL(urlString);	} catch (MalformedURLException ex) { checkOnlineFailed = true; ui.log("Error: httpGetRequest MalformedURLException: new URL(" + urlString +") (URL Typo?)\r\n", false, true, true, true, false); return null; }
	if (url == null) { checkOnlineFailed = true; ui.log("Error: httpGetRequest InvalidURL: url = new URL(" + urlString +"); (URL Typo?)\r\n", false, true, true, true, false); return null; }	
	HttpURLConnection httpConnection = null;
	try { httpConnection = (HttpURLConnection) url.openConnection(); httpConnection.setConnectTimeout(HTTP_CONNECT_TIMEOUT); } catch (IOException ex){ checkOnlineFailed = true; ui.log("Error: httpGetRequest IOException: url.openConnection()" + ex.getCause() + "\r\n", false, true, true, true, false); return null; }
	try { httpConnection.setRequestMethod("GET"); } catch (ProtocolException ex) { checkOnlineFailed = true; ui.log("Error: httpGetRequest ProtocolException: httpConnection.setRequestMethod(\"GET\")" + ex.getCause() + "\r\n", false, true, true, true, false); return null; }
        httpConnection.setRequestProperty("User-Agent", userAgent);
        int responseCode = 0;
	try { responseCode = httpConnection.getResponseCode(); } catch (IOException ex) {checkOnlineFailed = true;  ui.log("Error: httpGetRequest IOException: httpConnection.getResponseCode()" + ex.getCause() + "\r\n", false, true, true, true, false); return null; }

	if ((responseCode >= 200) && (responseCode < 400))
	{
 
            BufferedReader responseReader = null;
	    try { responseReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream())); } catch (IOException ex) { checkOnlineFailed = true; ui.log("Error: httpGetRequest IOException: new InputStreamReader(httpConnection.getInputStream())" + ex.getCause() + "\r\n", false, true, true, true, false); return null; }
             
            String responseLine;
            StringBuffer response = new StringBuffer();
 
	    try { while ((responseLine = responseReader.readLine()) != null)
	    {
		response.append(responseLine + "\n");
	    } } catch (IOException ex) { checkOnlineFailed = true; ui.log("Error: httpGetRequest IOException: responseReader.readLine()" + ex.getCause() + "\r\n", false, true, true, true, false); }
	    try { responseReader.close(); } catch (IOException ex) { checkOnlineFailed = true; ui.log("Error: httpGetRequest IOException: responseReader.close()" + ex.getCause() + "\r\n", false, true, true, true, false); }
 
            return response.toString();
        } else { checkOnlineFailed = true; ui.log("Error: httpGetRequest HTTP Response Code: " + responseCode + "\r\n", false, true, true, true, false); }
        return null;
    }
    
    public static String httpsGetRequest(UI ui, String urlString)
    {
	String userAgent = getUserAgent("(HTTPS)");
	
	URL url = null;
	try { url = new URL(urlString);	} catch (MalformedURLException ex) { checkOnlineFailed = true; ui.log("Error: httpsGetRequest MalformedURLException: new URL(" + urlString +") (URL Typo?)\r\n", false, true, true, true, false); }	
	if (url == null) { checkOnlineFailed = true; ui.log("Error: httpsGetRequest InvalidURL: url = new URL(" + urlString +"); (URL Typo?)\r\n", false, true, true, true, false); return null; }	
	HttpsURLConnection httpConnection = null;
	try { httpConnection = (HttpsURLConnection) url.openConnection(); httpConnection.setConnectTimeout(HTTP_CONNECT_TIMEOUT); } catch (IOException ex){ checkOnlineFailed = true; ui.log("Error: httpsGetRequest IOException: url.openConnection()" + ex.getCause() + "\r\n", false, true, true, true, false); }
	try { httpConnection.setRequestMethod("GET"); } catch (ProtocolException ex) { checkOnlineFailed = true; ui.log("Error: httpsGetRequest ProtocolException: httpConnection.setRequestMethod(\"GET\")" + ex.getCause() + "\r\n", false, true, true, true, false); }
        httpConnection.setRequestProperty("User-Agent", userAgent);
	httpConnection.setRequestProperty("Referer", Version.WEBSITEURISTRING);
        int responseCode = 0;
	try { responseCode = httpConnection.getResponseCode(); } catch (IOException ex) {checkOnlineFailed = true;  ui.log("Error: httpsGetRequest IOException: httpConnection.getResponseCode()" + ex.getCause() + "\r\n", false, true, true, true, false); }

	if ((responseCode >= 200) && (responseCode < 400))
	{
 
            BufferedReader responseReader = null;
	    try { responseReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream())); } catch (IOException ex) { checkOnlineFailed = true; ui.log("Error: httpsGetRequest IOException: new InputStreamReader(httpConnection.getInputStream())" + ex.getCause() + "\r\n", false, true, true, true, false); }
             
            String responseLine;
            StringBuffer response = new StringBuffer();
 
	    try { while ((responseLine = responseReader.readLine()) != null)
	    {
		response.append(responseLine + "\n");
	    } } catch (IOException ex) { checkOnlineFailed = true; ui.log("Error: httpsGetRequest IOException: responseReader.readLine()" + ex.getCause() + "\r\n", false, true, true, true, false); }
	    try { responseReader.close(); } catch (IOException ex) { checkOnlineFailed = true; ui.log("Error: httpsGetRequest IOException: responseReader.close()" + ex.getCause() + "\r\n", false, true, true, true, false); }
 
            return response.toString();
        } else { checkOnlineFailed = true; ui.log("Error: httpsGetRequest HTTP Response Code: " + responseCode + "\r\n", false, true, true, true, false); }
        return null;
    }
    
    synchronized public static String checkLatestOnlineVersion(UI ui)
    {
//      Read the remote VERSION file
	
	latestVersionIsKnown = false;
        latestOverallVersionString = "Unknown";
	    
	loop: for(String remoteVERSION2FileString:REMOTEVERSIONFILEURLSTRINGARRAY)
	{	    
	    checkOnlineFailed = false;
	    
	    ui.log("Check Update: " + remoteVERSION2FileString + "\r\n", false, true, true, false, false);
	    
	    if (remoteVERSION2FileString.startsWith("https://")) { remoteContent = httpsGetRequest(ui, remoteVERSION2FileString); } else { remoteContent = httpGetRequest(ui, remoteVERSION2FileString); }
	    
//	    ui.test("Remote Content: " + remoteContent + "\r\n\r\n");
	    if ((remoteContent != null) && (! checkOnlineFailed))
	    {
		String[] lines = remoteContent.split("\n"); // VERSION2 file was create on linux with unix newlines \n

		remoteFields = new String[lines.length];
		remoteValues = new String[lines.length];

//		Convert lines to fields array
		if (lines.length > 0)
		{		    
		    int c = 0; for (String line:lines)
		    {
//			ui.test(line + "\r\n");
			if ( (line != null) && (! line.isEmpty()) && (line.contains("[")) && (line.contains("]")) && (line.contains("{")) && (line.contains("}")) )
			{
			    boolean validLine = false;
			    String remoteField = line.substring(line.indexOf("[") + 1, line.indexOf("]"));	if (! remoteField.isEmpty()) { remoteFields[c] = remoteField; validLine = true; }
			    String remoteValue = line.substring(line.indexOf("{") + 1, line.lastIndexOf("}"));	if (! remoteValue.isEmpty()) { remoteValues[c] = remoteValue; validLine = true; }
			    if (validLine) { c++; }
			}
		    }

//		    New Release System
		    if (remoteFields.length > 0)
		    {
			for (int x = 0; x < (remoteFields.length); x++)
			{
//			    if ((remoteFields[x] != null) && (remoteValues[x] != null))
			    if (remoteFields[x] != null)
			    {
				if (remoteValues[x] == null) { remoteValues[x] = ""; }
//				ui.test("RField: " + remoteFields[x] + " RValue: " + remoteValues[x] + "\r\n");
				if (remoteFields[x].toLowerCase().equals("Version".toLowerCase()))
				{
				    latestOverallVersionString =	remoteValues[x];
				    String latestVersionString = latestOverallVersionString.substring(0, latestOverallVersionString.indexOf(".")).replaceAll("[^\\d]", "");
				    String latestUpgradeString = latestOverallVersionString.substring(latestOverallVersionString.indexOf("."), latestOverallVersionString.lastIndexOf(".")).replaceAll("[^\\d]", "");
				    String latestUpdateString = latestOverallVersionString.substring(latestOverallVersionString.lastIndexOf("."), latestOverallVersionString.length()).replaceAll("[^\\d]", "");
				    latestRemoteVersion = Integer.parseInt(latestVersionString); int latestUpgrade = Integer.parseInt(latestUpgradeString); int latestUpdate = Integer.parseInt(latestUpdateString);
				    latestVersionTotal = (latestRemoteVersion * 100) + (latestUpgrade * 10) + (latestUpdate * 1);
				    latestOverallVersionString = latestVersionString + "." + latestUpgradeString + "." + latestUpdateString;
				    latestVersionIsKnown = true;
				}
				if (remoteFields[x].toLowerCase().equals("Version Notes".toLowerCase()))    { latestReleaseString +=	    remoteValues[x] + "\r\n"; }
				if (remoteFields[x].toLowerCase().equals("Upgrade Notes".toLowerCase()))    { latestReleaseString +=	    remoteValues[x] + "\r\n"; }
				if (remoteFields[x].toLowerCase().equals("Update Notes".toLowerCase()))	    { latestReleaseString +=	    remoteValues[x] + "\r\n"; }
				
				if (remoteFields[x].toLowerCase().equals("Alert Subject".toLowerCase()))    { latestAlertSubjectString =    remoteValues[x]; }
				if (remoteFields[x].toLowerCase().equals("Alert Notes".toLowerCase()))	    { latestAlertString +=	    remoteValues[x] + "\r\n"; }
			    }
			}
			if ((latestOverallVersionString.length()>0)&&(latestOverallVersionString.length()>0)&&(latestOverallVersionString.length()>0)&&(latestVersionIsKnown)) { return latestOverallVersionString; }
		    } continue;
		} continue;
	    } continue;

	} if (latestVersionIsKnown)
	{
	    return latestOverallVersionString;
	}
	return "Could not check for new updates (Internet?)";
    }

    public static String getLatestOnlineOverallVersionString()		{ return latestOverallVersionString; }
    public static String getCurrentlyInstalledOverallVersionString()	{ return currentOverallVersionString; }
    public static String getCurrentReleaseString()			{ return currentReleaseString; }
    public static String getLatestReleaseString()			{ return latestReleaseString; }
//    public static String getLatestReleaseNotesString()		{ return latestReleaseNotesString; }
//    public static String getLatestVersionMessageString()		{ return latestReleaseMessageString; }

    public static String getLatestAlertSubjectString()			{ return latestAlertSubjectString; }
    public static String getLatestAlertString()			{ return latestAlertString; }
    
    public static String getCurrentAlertSubjectString()		{ return currentAlertSubjectString; }
    public static String getCurrentAlertString()			{ return currentAlertString; }
//    public String getLatestAlertMessageString()		{ return latestAlertMessageString; }

    public String getUpdateStatus() 
    {
        String returnString = "";
        if (( currentVersionIsKnown) && ( latestVersionIsKnown))
        {
            if      (currentVersionTotal < latestVersionTotal)
            {
                returnString += getProductName() + " " + currentOverallVersionString + " can be updated to version: " + latestOverallVersionString + " at: " + REMOTEPACKAGEDOWNLOADURISTRING + "\r\n"; 
		if (! getLatestReleaseString().isEmpty())	    { returnString += getLatestReleaseString() + "\r\n"; }
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

    synchronized public static void openWebSite(UI ui, String[] SITEURLSTRINGARRAY)
    {
        String identifierExpected = PRODUCTNAME;
	
	Configuration configuration = new Configuration(ui);
	loop: for(String SITEURLSTRING:SITEURLSTRINGARRAY)
	{
	    checkOnlineFailed = false;
	    if (! SITEURLSTRING.isEmpty())
	    {
		remoteContent = "";
		ui.log("Website: " + SITEURLSTRING + " ", false, true, true, false, false);

		if (SITEURLSTRING.startsWith("https://")) { remoteContent = httpsGetRequest(ui, SITEURLSTRING); } else { remoteContent = httpGetRequest(ui, SITEURLSTRING); }
				
		if (! checkOnlineFailed)
		{
		    if (remoteContent != null)
		    {
			if ( (remoteContent.toLowerCase().contains(identifierExpected.toLowerCase()) ))
			{
			    ui.log("Opening Browser\r\n", false, true, true, false, false);
			    Thread openWebSiteThread;
			    openWebSiteThread = new Thread(() ->
			    {
				try {  Desktop.getDesktop().browse(new URI(SITEURLSTRING)); }
				catch (URISyntaxException ex)		{ ui.log(ex.getMessage() + "\r\n", true, true, true, true, false); }
				catch (IOException ex)			{ ui.log(ex.getMessage() + "\r\n", true, true, true, true, false); }
				catch (UnsupportedOperationException ex){ ui.log(ex.getMessage() + " " + SITEURLSTRING + "\r\n", true, true, true, true, false); }
			    });
			    openWebSiteThread.setName("openWebSiteThread");
			    openWebSiteThread.setDaemon(true);
			    openWebSiteThread.start();
			    break;
			} else { ui.log("Invalid\r\n", false, true, true, true, false); }
		    } else { ui.log("Empty\r\n", false, true, true, true, false); }
		}
		else
		{
		    ui.log("Opening Browser Failed!\r\n", false, true, true, true, false);
		}
	    } else { ui.log("Error: openWebSite Empty website url: " + SITEURLSTRING + "\r\n", false, true, true, true, false); }
	}
	ui.log("\r\n", false, true, true, true, false);
    }
    
    synchronized public static void openLogDir(UI ui)
    {
	Configuration configuration = new Configuration(ui);
	Thread openLogDirThread;
	openLogDirThread = new Thread(() ->
	{
	    try { Desktop.getDesktop().open(configuration.getLogDirPath().toFile()); } catch (IOException ex) { ui.log(ex.getMessage(), true, true, true, true, false); }
	});
	openLogDirThread.setName("openLogDirThread");
	openLogDirThread.setDaemon(true);
	openLogDirThread.start();
    }
    
    public boolean latestVersionIsKnown()	    { return latestVersionIsKnown; }    
    public boolean versionIsDifferent()		    { if ((latestVersionIsKnown) && ( currentVersionTotal != latestVersionTotal )) { return true; } else { return false; } }
    public boolean versionCanBeUpdated()	    { if ((latestVersionIsKnown) && ( currentVersionTotal < latestVersionTotal ))  { return true; } else { return false; } }
    public boolean versionIsDevelopment()	    { if ((latestVersionIsKnown) && ( currentVersionTotal > latestVersionTotal ))  { return true; } else { return false; } }    

    public static String getLicenseDescription()    { return LICENSE_DESCRIPTION; }
    public static String getLicense()		    { return LICENSE; }
    public static String getAuthor()		    { return AUTHOR; }
    public static String getAuthorEmail()	    { return AUTHOREMAIL; }
    public static String getEmail()		    { return EMAIL; }
    public static String getProductName()	    { return PRODUCTNAME; }
    public static String getCommandLine()	    { return COMMANDLINE; }
    public static String getCompany()		    { return COMPANYNAME; }
}
