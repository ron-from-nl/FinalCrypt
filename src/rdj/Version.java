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
    private static URI mailtoURI;
    private UI ui;
    private static final String COMPANYNAME =				"Private Person";
    public  static final String PRODUCTNAME =				"FinalCrypt";
    private static final String COMMANDLINE =				"java -cp " + PRODUCTNAME.toLowerCase() + ".jar rdj.CLUI";
    private static       String fcInterface =				"";
    public  static final String AUTHOR_FIRSTNAME =			"Ron";
    public  static final String AUTHOR_LASTNAME =			"de Jong";
    public  static final String AUTHOR =				AUTHOR_FIRSTNAME + " " + AUTHOR_LASTNAME;
    public  static final String AUTHOREMAIL =				"ron@finalcrypt.org";
    public  static final String EMAIL =					"info@finalcrypt.org";
    public  static final String SUPPORTEMAIL =				"support@finalcrypt.org";
    public  static final String LICENSE =				"Creative Commons License: (CC BY-NC-ND 4.0)";
    public  static final String LICENSE_DESCRIPTION =			"License 2017-" + Calendar.getInstance().get(Calendar.YEAR);
    
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
    
    private static String localOverallVersionString =			"";
    private static String localOverallVersionPrefString =		"";
    private static String latestOverallVersionString =			"";
    private static int localVersionTotal =				0;
    private static int latestVersionTotal =				0;
    private static InputStream istream =				null;
    private static final String LOCALVERSIONFILEURLSTRING =		"VERSION2";
    private static       String localContent =				"";
    public static final String[] HOMEPAGEURLSTRINGARRAY =		{
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

    public static final String[] DOWNLOADPAGEURLSTRINGARRAY =		{
									     "http://www.finalcrypt.org/project-6.php"								// tested
									    ,"https://www.finalcrypt.org/project-6.php"								// tested
									    ,"http://www.finalcrypt.com/project-6.php"								// tested
									    ,"https://www.finalcrypt.com/project-6.php"								// tested
									    ,"https://sourceforge.net/projects/finalcrypt/files/"						// tested
									    ,"https://github.com/ron-from-nl/FinalCrypt/releases/"						// tested
									    ,"https://osdn.net/users/finalcrypt/pf/FinalCrypt/files/"						// tested
									    ,"http://www.majorgeeks.com/files/details/finalcrypt.html"						// tested
									    ,"http://sites.google.com/site/ronuitholland/home/finalcrypt/"					// tested
									    ,"http://duckduckgo.com/?q=finalcrypt+homepage&t=h_&ia=web"						// tested
									    ,"http://www.google.com/search?q=finalcrypt+homepage&oq=finalcrypt+homepage"			// tested
									};

    public static final String[] VIDEOPAGEURLSTRINGARRAY =		{
									     "http://www.finalcrypt.org/video/how_does_finalcrypt_work.mp4"					// tested
									    ,"https://www.finalcrypt.org/video/how_does_finalcrypt_work.mp4"					// tested
									    ,"http://www.finalcrypt.com/video/how_does_finalcrypt_work.mp4"					// tested
									    ,"https://www.finalcrypt.com/video/how_does_finalcrypt_work.mp4"					// tested
									    ,"https://youtu.be/MRKREuF_ovI"									// tested
									};

    public static final String[] SUPPORTPAGEURLSTRINGARRAY =		{
									     "http://www.finalcrypt.org/project-7.php"								// tested
									    ,"https://www.finalcrypt.org/project-7.php"								// tested
									    ,"http://www.finalcrypt.com/project-7.php"								// tested
									    ,"https://www.finalcrypt.com/project-7.php"								// tested
									    ,"https://sourceforge.net/projects/finalcrypt/support"						// tested
									    ,"https://github.com/ron-from-nl/FinalCrypt/issues"							// tested
									    ,"https://osdn.net/users/finalcrypt/pf/FinalCrypt/ticket/"						// tested
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

    public static final String REMOTEPACKAGEDOWNLOADURISTRING =		"http://www.finalcrypt.org/project-6.php";

    private static URL remoteURL = null;
    private static ReadableByteChannel localVersionByteChannel =		null;
    private static ReadableByteChannel latestVersionByteChannel =		null;
    private static ByteBuffer byteBufferLocal; 
    private static ByteBuffer byteBufferRemote; 
    
    private static boolean localVersionIsKnown =				false;
    private static boolean latestVersionIsKnown =				false;
    private static boolean updateAvailable =					false;
    private static String[] localFields;
    private static String[] localValues;
    private static String[] remoteFields;
    private static String[] remoteValues;
    private static String localReleaseString;
    private static String latestReleaseString;
//    private static String latestReleaseNotesString;
//    private static String latestReleaseMessageString;

    private static String latestAlertSubjectString;
    private static String latestAlertString;
    private static String localAlertSubjectString;
    private static String localAlertString;
//    private static String latestAlertMessageString;
    public static int localVersion;
    public static int latestRemoteVersion;
    private int localUpgrade;
    private int localUpdate;
    private String localUpdateNotes = "";
    private String localVersionNotes = "";
    private String localUpgradeNotes = "";
    private String latestUpdateNotes = "";
    private String latestVersionNotes = "";
    private String latestUpgradeNotes = "";

    public Version(UI ui)
    {
        this.ui = ui;
	localReleaseString = ""; // Replacing below 2 lines
	latestReleaseString = ""; // Replacing below 2 lines
//	latestReleaseNotesString = "";
//	latestReleaseMessageString = "";

	latestAlertSubjectString = "";
	latestAlertString = "";
	localAlertSubjectString = "";
	localAlertString = "";
//	latestAlertMessageString = "";
    }
    
    public static String getSysEnv(String classname, Version version, Configuration configuration)
    {
	fcInterface = classname;

	String content = "";
	content += getSysEnvWelcome(classname, version, configuration);
	content += "\r\n";
	content += getSysEnvHeader(classname, version, configuration);
	content += "\r\n";
	content += getSysEnvOS(classname, version, configuration);
	content += "\r\n";
	content += getSysEnvSystem(classname, version, configuration);
	content += "\r\n";
	content += getSysEnvJava(classname, version, configuration);
	content += "\r\n";
	content += getSysEnvUser(classname, version, configuration);
	content += "\r\n";
	content += getSysEnvCaption(classname, version, configuration);
	content += "\r\n";
	content += "\r\n";
	
	return content;
    }

    public static String getSysEnvEmail(String classname, Version version, Configuration configuration)
    {
	fcInterface = classname;

	String content = "";
	content += getSysEnvHeader(classname, version, configuration);
	content += "\r\n";
	content += getSysEnvOS(classname, version, configuration);
	content += "\r\n";
	content += getSysEnvSystem(classname, version, configuration);
	content += "\r\n";
	content += getSysEnvJava(classname, version, configuration);
	content += "\r\n";
	content += getSysEnvUser(classname, version, configuration);
	content += "\r\n";
	
	return content;
    }

    public static String getSysEnvWelcome(String classname, Version version, Configuration configuration)
    {
	fcInterface = classname;

	String content = "";	
	content +=    "Welcome to:              " + PRODUCTNAME + " " + version.getLocalOverallVersionString() + "\r\n";
	return content;
    }

    public static String getSysEnvHeader(String classname, Version version, Configuration configuration)
    {
	fcInterface = classname;

	String content = "";	
	content +=    "Interface:               " + fcInterface + "\r\n";
	content +=    "Author:                  " + AUTHOR + "\r\n";
	content +=    "Email:                   " + AUTHOREMAIL + "\r\n";
	content +=    "Logfiles:                " + configuration.getLogDirPath().toString() + "\r\n";
	content +=    "Command line:            " + COMMANDLINE + " --help\r\n";
	content +=    "License:                 " + LICENSE + "\r\n";
	return content;
    }

    public static String getLogDirPath(String classname, Version version, Configuration configuration)
    {
	fcInterface = classname;

	String content = "";	
	content += configuration.getLogDirPath().toString();
	return content;
    }

    public static String getSysEnvOS(String classname, Version version, Configuration configuration)
    {
	fcInterface = classname;

	String content = "";	
	content +=    "OS Name:                 " + OS_NAME + "\r\n";
	content +=    "OS Architecture:         " + OS_ARCH + "\r\n";
	content +=    "OS Version:              " + OS_VERSION + "\r\n";
	content +=    "OS Time:                 " + configuration.getTime() + "\r\n";
	content +=    "File Encoding:           " + FILE_ENCODING + "\r\n";
	return content;
    }

    public static String getSysEnvSystem(String classname, Version version, Configuration configuration)
    {
	fcInterface = classname;

	String content = "";	
	content +=    "Processors:              " + PROCESSORS + "\r\n";
	content +=    "Total   Memory:          " + Validate.getHumanSize(TOT_MEM, 1,"Bytes") + "\r\n";
//	env +=    "Total   Memory:          " + TOT_MEM + "\r\n";
	content +=    "Maximum Memory:          " + Validate.getHumanSize(MAX_MEM, 1,"Bytes") + "\r\n";
	content +=    "Free    Memory:          " + Validate.getHumanSize(FREE_MEM, 1,"Bytes") + "\r\n";
	content +=    "Used    Memory:          " + Validate.getHumanSize(USED_MEM, 1,"Bytes") + "\r\n";
	return content;
    }

    public static String getSysEnvJava(String classname, Version version, Configuration configuration)
    {
	fcInterface = classname;

	String content = "";	
	content +=    "Java Version:            " + JAVA_VERSION + "\r\n";
	content +=    "Java Vendor:             " + JAVA_VENDOR + "\r\n";
	content +=    "Java Home:               " + JAVA_HOME + "\r\n";
	content +=    "Java_VM_Name:            " + JAVA_VM_NAME + "\r\n";
	content +=    "Java_VM_Version:         " + JAVA_VM_VERSION + "\r\n";
	content +=    "Java_Runtime_Version:    " + JAVA_RUNTIME_VERSION + "\r\n";
	content +=    "Class Version:           " + CLASS_VERSION + "\r\n";
	return content;
    }

    public static String getSysEnvUser(String classname, Version version, Configuration configuration)
    {
	fcInterface = classname;

	String content = "";	
	content +=    "User Country:            " + USER_COUNTRY + "\r\n";
	content +=    "User Language:           " + USER_LANGUAGE + "\r\n";
	content +=    "User Name:               " + USER_NAME + "\r\n";
	content +=    "User Home:               " + USER_HOME + "\r\n";
	content +=    "User Dir:                " + USER_DIR + "\r\n";
	content +=    "User Agent:              " + getUserAgent("") + "\r\n";
	return content;
    }

    public static String getSysEnvCaption(String classname, Version version, Configuration configuration)
    {
	fcInterface = classname;

	String content = "";	
	content +=    "Action Symbols           ";
	content += FinalCrypt.UTF8_CREATE_SYMBOL + " = " +		FinalCrypt.UTF8_CREATE_DESC + " | ";
	content += FinalCrypt.UTF8_READ_SYMBOL + " = " +		FinalCrypt.UTF8_READ_DESC + " | ";
	content += FinalCrypt.UTF8_WRITE_SYMBOL + " = " +		FinalCrypt.UTF8_WRITE_DESC + " | ";
	content += FinalCrypt.UTF8_ENCRYPT_SYMBOL + " = " +		FinalCrypt.UTF8_ENCRYPT_DESC + " | ";
	content += FinalCrypt.UTF8_DECRYPT_SYMBOL + " = " +		FinalCrypt.UTF8_DECRYPT_DESC + " | ";
	content += FinalCrypt.UTF8_XOR_NOMAC_SYMBOL + " = " +	FinalCrypt.UTF8_XOR_NOMAC_DESC + " | ";
	content += FinalCrypt.UTF8_SHRED_SYMBOL + " = " +		FinalCrypt.UTF8_SHRED_DESC + " | ";
	content += FinalCrypt.UTF8_CLONE_SYMBOL + " = " +		FinalCrypt.UTF8_CLONE_DESC + " | ";
	content += FinalCrypt.UTF8_DELETE_SYMBOL + " = " +		FinalCrypt.UTF8_DELETE_DESC + " | ";
	content += FinalCrypt.UTF8_PAUSE_SYMBOL + " = " +		FinalCrypt.UTF8_PAUSE_DESC + " | ";
	content += FinalCrypt.UTF8_UNPAUSE_SYMBOL + " = " +		FinalCrypt.UTF8_UNPAUSE_DESC + " | ";
	content += FinalCrypt.UTF8_STOP_SYMBOL + " = " +		FinalCrypt.UTF8_STOP_DESC + " ";
	content += "\r\n";
	content +=    "Data   Symbols           ";
	content += FinalCrypt.UTF8_OLD_TARGET_SYMBOL + " = " +	FinalCrypt.UTF8_OLD_TARGET_DESC + " | ";
	content += FinalCrypt.UTF8_NEW_TARGET_SYMBOL + " = " +	FinalCrypt.UTF8_NEW_TARGET_DESC + " | ";
	content += FinalCrypt.UTF8_MAC_SYMBOL + " = " +		FinalCrypt.UTF8_MAC_DESC + " | ";
	content += FinalCrypt.UTF8_KEY_SYMBOL + " = " +		FinalCrypt.UTF8_KEY_DESC + " | ";
	content += FinalCrypt.UTF8_ATTRIB_SYMBOL + " = " +		FinalCrypt.UTF8_ATTRIB_DESC + " ";
	content += "\r\n";
	content +=	  "Status Symbols           ";
	content += FinalCrypt.UTF8_SUCCEEDED_SYMBOL + " = " +	FinalCrypt.UTF8_SUCCEEDED_DESC + " | ";
	content += FinalCrypt.UTF8_SUCCESSUNKNOWN_SYMBOL + " = " +	FinalCrypt.UTF8_SUCCESSUNKNOWN_DESC + " | ";
	content += FinalCrypt.UTF8_UNSUCCEEDED_SYMBOL + " = " +	FinalCrypt.UTF8_UNSUCCEEDED_DESC + " | ";
	content += FinalCrypt.UTF8_UNENCRYPTABLE_SYMBOL + " = " +	FinalCrypt.UTF8_UNENCRYPTABLE_DESC + " | ";
	content += FinalCrypt.UTF8_UNDECRYPTABLE_SYMBOL + " = " +	FinalCrypt.UTF8_UNDECRYPTABLE_DESC + " ";
	content += "\r\n";
	content += "\r\n";
	
	return content;
    }

    synchronized public String checkLocalVersion(UI ui)
    {
        istream = getClass().getResourceAsStream(LOCALVERSIONFILEURLSTRING);
	
//      Read the local VERSION file
        localOverallVersionString = "Unknown";
        localVersionByteChannel = newChannel(istream);
        byteBufferLocal = ByteBuffer.allocate(100000); byteBufferLocal.clear(); localContent = "";
        
	try { while(localVersionByteChannel.read(byteBufferLocal) > 0) { byteBufferLocal.flip(); while(byteBufferLocal.hasRemaining()){localContent += (char) byteBufferLocal.get();}}}
	catch (IOException ex) { ui.log("Error: Version.checkLocalInstalledVersion IOException: Channel.read(..): " + ex.getMessage()+"\r\n", true, true, true, true, false); }
        
	try { localVersionByteChannel.close(); } catch (IOException ex) { ui.log("Error: Version.checkLocallyInstalledVersion IOException: Channel.close(..) " +ex.getMessage()+"\r\n", true, true, true, true, false); }        

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
			localOverallVersionString = localValues[x];
			String localVersionString = localOverallVersionString.substring(0, localOverallVersionString.indexOf(".")).replaceAll("[^\\d]", "");
			String localUpgradeString = localOverallVersionString.substring(localOverallVersionString.indexOf("."), localOverallVersionString.lastIndexOf(".")).replaceAll("[^\\d]", "");
			String localUpdateString = localOverallVersionString.substring(localOverallVersionString.lastIndexOf("."), localOverallVersionString.length()).replaceAll("[^\\d]", "");
			
			localVersion = Integer.parseInt(localVersionString);
			localUpgrade = Integer.parseInt(localUpgradeString);
			localUpdate = Integer.parseInt(localUpdateString);
			
			localVersionTotal = (localVersion * 100) + (localUpgrade * 10) + (localUpdate * 1);
			localOverallVersionString = localVersionString + "." + localUpgradeString + "." + localUpdateString;
			localOverallVersionPrefString = "-" + localVersionString + "-" + localUpgradeString;
//			ui.test("currentOverallVersionString: " + currentOverallVersionString + "\r\n");
			localVersionIsKnown = true;
		    }
		    
		    if (localFields[x].toLowerCase().equals("Version Notes".toLowerCase()))	{ localVersionNotes +=	localValues[x] + "\r\n"; }
		    if (localFields[x].toLowerCase().equals("Upgrade Notes".toLowerCase()))	{ localUpgradeNotes +=	localValues[x] + "\r\n"; }
		    if (localFields[x].toLowerCase().equals("Update Notes".toLowerCase()))	{ localUpdateNotes +=	localValues[x] + "\r\n"; }
		    
		    if (localFields[x].toLowerCase().equals("Alert Subject".toLowerCase()))	{ localAlertSubjectString =	localValues[x]; }
		    if (localFields[x].toLowerCase().equals("Alert Notes".toLowerCase()))	{ localAlertString +=		localValues[x] + "\r\n"; }
		}
	    }
	    if (
			(localVersionNotes.length()>0)
		    &&	(localUpgradeNotes.length()>0)
		    &&	(localVersionNotes.length()>0)
		    &&	(localVersionIsKnown)
		)
	    {
		localReleaseString += localUpdateNotes;
		localReleaseString += "\r\n";
		localReleaseString += localUpgradeNotes;
		localReleaseString += "\r\n";
		localReleaseString += localVersionNotes;
		return localOverallVersionString;
	    }
	}
	
	return "Could not check your current version (VERSION2 file missing?)";
    }

    public static String encode2URL(UI ui, String value)
    {       
	String returnValue = "";
	try { returnValue = URLEncoder.encode(value, StandardCharsets.UTF_8.toString()).replace("+", "%20"); }
	catch (UnsupportedEncodingException ex) { ui.log("Error: Version.encodeValue URLEncoder.encode(" + value +") (URL Encoding?)\r\n", false, true, true, true, false); }
	return returnValue;
    }
    
    private static String getUserAgent(String connType)
    {       
	String userAgent = "";
	userAgent += getProductName() + "/" + getLocalOverallVersionString() + " " + fcInterface + " " + connType;
	userAgent += " (" + OS_NAME + " " + OS_VERSION + "; " + OS_ARCH + "; ";
	userAgent += JAVA_VENDOR + " " + JAVA_VERSION + " " + CLASS_VERSION;// + "; ";
//	userAgent += JAVA_VM_NAME + " " + JAVA_VM_VERSION + ")";
	userAgent += ")";
	return userAgent;
    }
    
    
    public static String httpGetRequest(UI ui, String urlString, String requestMethod)
    {
	String userAgent = getUserAgent("(HTTP)");
	
	URL url = null;
	try { url = new URL(urlString);	} catch (MalformedURLException ex) { checkOnlineFailed = true; ui.log("Error: httpGetRequest MalformedURLException: new URL(" + urlString +") (URL Typo?)\r\n", false, true, true, true, false); return null; }
	if (url == null) { checkOnlineFailed = true; ui.log("Error: httpGetRequest InvalidURL: url = new URL(" + urlString +"); (URL Typo?)\r\n", false, true, true, true, false); return null; }	
	HttpURLConnection httpConnection = null;
	try { httpConnection = (HttpURLConnection) url.openConnection(); } catch (IOException ex){ checkOnlineFailed = true; ui.log("Error: httpGetRequest IOException: url.openConnection(): " + ex.getMessage() + "\r\n", false, true, true, true, false); return null; }
	httpConnection.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
	httpConnection.setReadTimeout(HTTP_CONNECT_TIMEOUT);
	try { httpConnection.setRequestMethod(requestMethod); } catch (ProtocolException ex) { checkOnlineFailed = true; ui.log("Error: httpGetRequest ProtocolException: httpConnection.setRequestMethod(\"GET\"): " + ex.getMessage() + "\r\n", false, true, true, true, false); return null; }
        httpConnection.setRequestProperty("User-Agent", userAgent);
	
        int responseCode = 0;
	try { responseCode = httpConnection.getResponseCode(); } catch (IOException ex) {checkOnlineFailed = true;  ui.log("Error: httpGetRequest IOException: httpConnection.getResponseCode(): " + ex.getMessage() + "\r\n", false, true, true, true, false); return null; }

	if ((responseCode >= 200) && (responseCode < 400))
	{
            BufferedReader responseReader = null;
	    try { responseReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream())); } catch (IOException ex) { checkOnlineFailed = true; ui.log("Error: httpGetRequest IOException: new InputStreamReader(httpConnection.getInputStream()): " + ex.getMessage() + "\r\n", false, true, true, true, false); return null; }
             
            String responseLine;
            StringBuffer response = new StringBuffer();
 
	    try { while ((responseLine = responseReader.readLine()) != null)
	    {
		response.append(responseLine + "\n");
	    } } catch (IOException ex) { checkOnlineFailed = true; ui.log("Error: httpGetRequest IOException: responseReader.readLine(): " + ex.getMessage() + "\r\n", false, true, true, true, false); }
	    try { responseReader.close(); } catch (IOException ex) { checkOnlineFailed = true; ui.log("Error: httpGetRequest IOException: responseReader.close(): " + ex.getMessage() + "\r\n", false, true, true, true, false); }
	    
	    httpConnection.disconnect();
	    
            return response.toString();
        } else { checkOnlineFailed = true; ui.log("Error: httpGetRequest HTTP Response Code: " + responseCode + "\r\n", false, true, true, true, false); }
        return null;
    }
    
    public static String httpsGetRequest(UI ui, String urlString, String requestMethod)
    {
	String userAgent = getUserAgent("(HTTPS)");
	
	URL url = null;
	try { url = new URL(urlString);	} catch (MalformedURLException ex) { checkOnlineFailed = true; ui.log("Error: MalformedURLException: new URL(" + urlString +") (URL Typo?)\r\n", false, true, true, true, false); }	
	if (url == null) { checkOnlineFailed = true; ui.log("Error: InvalidURL: url = new URL(" + urlString +"); (URL Typo?)\r\n", false, true, true, true, false); return null; }	
	HttpsURLConnection httpConnection = null;
	
	try { httpConnection = (HttpsURLConnection) url.openConnection(); } catch (IOException ex){ checkOnlineFailed = true; ui.log("Error: IOException: url.openConnection(): " + ex.getMessage() + "\r\n", false, true, true, true, false); }
	httpConnection.setConnectTimeout(HTTP_CONNECT_TIMEOUT); 
	httpConnection.setReadTimeout(HTTP_CONNECT_TIMEOUT);
	try { httpConnection.setRequestMethod(requestMethod); } catch (ProtocolException ex) { checkOnlineFailed = true; ui.log("Error: ProtocolException: httpConnection.setRequestMethod(\"GET\"): " + ex.getMessage() + "\r\n", false, true, true, true, false); }
        httpConnection.setRequestProperty("User-Agent", userAgent);
	httpConnection.setRequestProperty("Referer", Version.WEBSITEURISTRING);
	
        int responseCode = 0;
	try { responseCode = httpConnection.getResponseCode(); } catch (IOException ex) {checkOnlineFailed = true;  ui.log("Error: IOException: httpConnection.getResponseCode(): " + ex.getMessage() + "\r\n", false, true, true, true, false); }

	if ((responseCode >= 200) && (responseCode < 400))
	{
 
            BufferedReader responseReader = null;
	    try { responseReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream())); } catch (IOException ex) { checkOnlineFailed = true; ui.log("Error: IOException: new InputStreamReader(httpConnection.getInputStream()): " + ex.getMessage() + "\r\n", false, true, true, true, false); }
             
            String responseLine;
            StringBuffer response = new StringBuffer();
 
	    try { while ((responseLine = responseReader.readLine()) != null)
	    {
		response.append(responseLine + "\n");
	    } } catch (IOException ex) { checkOnlineFailed = true; ui.log("Error: IOException: responseReader.readLine() " + ex.getCause() + "\r\n", false, true, true, true, false); }
	    try { responseReader.close(); } catch (IOException ex) { checkOnlineFailed = true; ui.log("Error: IOException: responseReader.close(): " + ex.getMessage() + "\r\n", false, true, true, true, false); }
 
 	    httpConnection.disconnect();

	    return response.toString();
        } else { checkOnlineFailed = true; ui.log("Error: HTTP Response Code: " + responseCode + "\r\n", false, true, true, true, false); }
        return null;
    }
    
    synchronized public String checkLatestVersion(UI ui)
    {
//      Read the remote VERSION file
	
	latestVersionIsKnown = false;
        latestOverallVersionString = "Unknown";
	    
	loop: for(String remoteVERSION2FileString:REMOTEVERSIONFILEURLSTRINGARRAY)
	{	    
	    checkOnlineFailed = false;
	    
	    Thread logThread;
	    logThread = new Thread(() ->
	    {
		ui.log("Fetch: " + remoteVERSION2FileString + "\r\n", false, true, true, false, false);
	    });
	    logThread.setName("logThread");
	    logThread.setDaemon(true);
	    logThread.start();
	    
	    if (remoteVERSION2FileString.startsWith("https://")) { remoteContent = httpsGetRequest(ui, remoteVERSION2FileString, "GET"); } else { remoteContent = httpGetRequest(ui, remoteVERSION2FileString, "GET"); }
	    
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
				
				if (remoteFields[x].toLowerCase().equals("Version Notes".toLowerCase()))    { latestVersionNotes +=	remoteValues[x] + "\r\n"; }
				if (remoteFields[x].toLowerCase().equals("Upgrade Notes".toLowerCase()))    { latestUpgradeNotes +=	remoteValues[x] + "\r\n"; }
				if (remoteFields[x].toLowerCase().equals("Update Notes".toLowerCase()))	    { latestUpdateNotes +=	remoteValues[x] + "\r\n"; }
				
				if (remoteFields[x].toLowerCase().equals("Alert Subject".toLowerCase()))    { latestAlertSubjectString =    remoteValues[x]; }
				if (remoteFields[x].toLowerCase().equals("Alert Notes".toLowerCase()))	    { latestAlertString +=	    remoteValues[x] + "\r\n"; }
			    }
			}
			if (
				    (latestVersionNotes.length()>0)
				&&  (latestUpgradeNotes.length()>0)
				&&  (latestUpdateNotes.length()>0)
				&&  (latestVersionIsKnown)
			    )
			{
			    latestReleaseString += latestUpdateNotes;
			    latestReleaseString += "\r\n";
			    latestReleaseString += latestUpgradeNotes;
			    latestReleaseString += "\r\n";
			    latestReleaseString += latestVersionNotes;
			    return latestOverallVersionString;
			}
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
    public static String getLocalOverallVersionString()			{ return localOverallVersionString; }
    public static String getLocalOverallVersionPrefString()		{ return localOverallVersionPrefString; }
    public static String getLocalReleaseString()			{ return localReleaseString; }
    public static String getLatestReleaseString()			{ return latestReleaseString; }
//    public static String getLatestReleaseNotesString()		{ return latestReleaseNotesString; }
//    public static String getLatestVersionMessageString()		{ return latestReleaseMessageString; }

    public static String getLatestAlertSubjectString()			{ return latestAlertSubjectString; }
    public static String getLatestAlertString()				{ return latestAlertString; }
    
    public static String getLocalAlertSubjectString()			{ return localAlertSubjectString; }
    public static String getLocalAlertString()				{ return localAlertString; }
//    public String getLatestAlertMessageString()			{ return latestAlertMessageString; }

    public String getUpdateStatus() 
    {
        String returnString = "";
        if (( localVersionIsKnown) && ( latestVersionIsKnown))
        {
            if      (localVersionTotal < latestVersionTotal)
            {
                returnString += getProductName() + " " + localOverallVersionString + " can be updated to version: " + latestOverallVersionString + " at: " + REMOTEPACKAGEDOWNLOADURISTRING + "\r\n"; 
		if (! getLatestReleaseString().isEmpty())	    { returnString += getLatestReleaseString() + "\r\n"; }
            } 
            else if (localVersionTotal > latestVersionTotal)
            {
                returnString += getProductName() + " " + localOverallVersionString + " is a development version!\r\n";
            } 
            else
            {
                returnString += getProductName() + " " + localOverallVersionString + " is up to date\r\n";
            } 
        }
        else
        {
            if (!localVersionIsKnown)	{ returnString = "Could not retrieve the locally installed " + Version.getProductName() + " Version\r\n"; }
            if (!latestVersionIsKnown)  { returnString = "Could not retrieve the latest online " + Version.getProductName() + " Version\r\n"; }
        }
        return returnString;
    }

    synchronized public static void openWebSite(UI ui, String[] SITEURLSTRINGARRAY, String requestMethod)
    {
        String identifierExpected = PRODUCTNAME;
	
	Configuration configuration = new Configuration(ui);
	loop: for(String SITEURLSTRING:SITEURLSTRINGARRAY)
	{
	    checkOnlineFailed = false;
	    if (! SITEURLSTRING.isEmpty())
	    {
		remoteContent = "";
		ui.log("Website: " + SITEURLSTRING + "\r\n", false, true, true, false, false);

		if (SITEURLSTRING.startsWith("https://")) { remoteContent = httpsGetRequest(ui, SITEURLSTRING, requestMethod); } else { remoteContent = httpGetRequest(ui, SITEURLSTRING, requestMethod); }
				
		if (! checkOnlineFailed)
		{
		    if (remoteContent != null)
		    {
//			ui.test("remoteContent: " + remoteContent + "\r\n");
			if ( (remoteContent.toLowerCase().contains(identifierExpected.toLowerCase()) ) || SITEURLSTRING.endsWith(".mp4"))
			{
			    ui.log("Opening Browser\r\n", false, true, true, false, false);
			    Thread openWebSiteThread;
			    openWebSiteThread = new Thread(() ->
			    {
				try {  Desktop.getDesktop().browse(new URI(SITEURLSTRING)); }
				catch (URISyntaxException ex)		{ ui.log("Version.openWebSite() Desktop.getDesktop().browse URISyntaxException: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
				catch (IOException ex)			{ ui.log("Version.openWebSite() Desktop.getDesktop().browse IOException: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
				catch (UnsupportedOperationException ex){ ui.log("Version.openWebSite() Desktop.getDesktop().browse UnsupportedOperationException: " + ex.getMessage() + " " + SITEURLSTRING + "\r\n", true, true, true, true, false); }
			    });
			    openWebSiteThread.setName("openWebSiteThread");
			    openWebSiteThread.setDaemon(true);
			    openWebSiteThread.start();
			    break;
			} else { ui.log("Invalid webpage content\r\n", false, true, true, true, false); }
		    } else { ui.log("Empty webpage\r\n", false, true, true, true, false); }
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
	    try { Desktop.getDesktop().open(configuration.getLogDirPath().toFile()); } catch (IOException ex) { ui.log("Version.openEmail() Desktop.getDesktop().open(() IOException: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	});
	openLogDirThread.setName("openLogDirThread");
	openLogDirThread.setDaemon(true);
	openLogDirThread.start();
    }
    
    synchronized public static void openEmail(UI ui, String mailto, String cc, String subject, String body)
    {
	Configuration configuration = new Configuration(ui);
	Thread openEmailThread;
	String mailTo = SUPPORTEMAIL;
	final String mailURIStr = String.format("mailto:%s?subject=%s&cc=%s&body=%s", mailTo, subject, cc, body);
	try {  mailtoURI = new URI(mailURIStr); } catch (URISyntaxException ex) { ui.log(ex.getMessage(), true, true, true, true, false); }
	
	openEmailThread = new Thread(() ->  { try { Desktop.getDesktop().mail(mailtoURI); } catch (IOException ex) { ui.log("Version.openEmail() Desktop.getDesktop().mail(() IOException: " + ex.getMessage() + "\r\n", true, true, true, true, false); } });
	openEmailThread.setName("openEmailThread");
	openEmailThread.setDaemon(true);
	openEmailThread.start();
    }
    
    public boolean latestVersionIsKnown()	    { return latestVersionIsKnown; }    
    public boolean versionIsDifferent()		    { if ((latestVersionIsKnown) && ( localVersionTotal != latestVersionTotal )) { return true; } else { return false; } }
    public boolean versionCanBeUpdated()	    { if ((latestVersionIsKnown) && ( localVersionTotal < latestVersionTotal ))  { return true; } else { return false; } }
    public boolean versionIsDevelopment()	    { if ((latestVersionIsKnown) && ( localVersionTotal > latestVersionTotal ))  { return true; } else { return false; } }    

    public static String getLicenseDescription()    { return LICENSE_DESCRIPTION; }
    public static String getLicense()		    { return LICENSE; }
    public static String getAuthor()		    { return AUTHOR; }
    public static String getAuthorEmail()	    { return AUTHOREMAIL; }
    public static String getEmail()		    { return EMAIL; }
    public static String getProductName()	    { return PRODUCTNAME; }
    public static String getCommandLine()	    { return COMMANDLINE; }
    public static String getCompany()		    { return COMPANYNAME; }
}
