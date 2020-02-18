/*
 * CC BY-NC-ND 4.0 2017 Ron de Jong (ron@finalcrypt.org)
 * 
 * This is free software; you can redistribute it 
 * under the terms of the Creative Commons License
 * Creative Commons License: (CC BY-NC-ND 4.0) as published by
 * https://creativecommons.org/licenses/by-nc-nd/4.0/ either
 * version 4.0 of the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International Public License for more details.
 * 
 * You should have received a copy called: "LICENSE" of the 
 * Creative Commons Public License along with this software;
 */
package rdj;

import java.nio.file.Path;

public class FCPath
{
    public static	   int	    MAC_SIZE =		    (FinalCrypt.FINALCRYPT_PLAIN_TEXT_MESSAGE_AUTHENTICATION_CODE_V3.length() * 2);
    public static	   int	    KEY_SIZE_MIN_DEFAULT =  MAC_SIZE;
    public static	   int	    KEY_SIZE_MIN =	    KEY_SIZE_MIN_DEFAULT;

    public static final    int	    INVALID =		    0;
    public static final    int	    FILE =		    1;
    public static final    int	    DIRECTORY =		    2;
    public static final    int	    SYMLINK =		    3;
    public static final    int	    DEVICE =		    4;
    public static final    int	    PARTITION =		    5;
    public static final    int	    DEVICE_INVALID =	    6;
    public static final    int	    DEVICE_PROTECTED =	    7;
    
    public static final String[] TYPE_DESCRIPTION_ARRAY =   new String[] { "Invalid","File","Directory","Symlink","Device","Partition","Device Invalid","Device Protected" };
    
    public	    Path    path;

    public	    boolean exist =			    false;
    public	    int	    type =			    INVALID;
    public	    long    size =			    0;
    public	    boolean isReadable =		    false;
    public	    boolean isWritable =		    false;
    public	    boolean isHidden =			    false;
    public	    boolean matchKey =			    false;

    public	    boolean isValidPath =		    false;
    public	    boolean isValidFile =		    false;
    public	    boolean isValidDeviceProtected =	    false;
    public	    boolean isValidDevice =		    false;
    public	    boolean isValidPartition =		    false;
    
    public	    boolean isKey =			    false;
    public	    boolean isValidKey =		    false;
    public	    boolean isValidKeyDir =		    false;

//  Decrypted
    
    public	    boolean isDecrypted =		    false;
    public	    boolean isEncryptable =		    false;

    public	    boolean needsWriteAutoKey =		    false;  // Validate.getFCPath will check if encryptable requires key write
    public	    long    needsWriteAutoKeySize =	    0;	    // Validate.getFCPath will check if encryptable requires size of key write for progress stats & ETA
    public	    boolean matchedReadAutoKey =	    false;  // Validate.getFCPath will check if encryptable requires key read
    public	    long    matchedReadAutoKeySize =	    0;	    // Validate.getFCPath will check if encryptable requires size of key read for progress stats & ETA
    public	    boolean unmatchedReadAutoKey =	    false;  // Validate.getFCPath will check if encryptable requires key missing
    public	    long    unmatchedReadAutoKeySize =	    0;	    // Validate.getFCPath will check if encryptable requires size of key missing
    
    public	    boolean isNewEncrypted =		    false;
    public	    boolean isUnEncryptable =		    false;

//  Encrypted

//    public	    boolean hasFCToken =		    false;
    public	    boolean isEncrypted =		    false;
    public	    static int	    defaultMACVersion =	    3;
    public	    int	    macVersion =		    defaultMACVersion;
//    public	    boolean isAuthenticated =		    false;
    public	    boolean isDecryptable =		    false;    
    public	    boolean isNewDecrypted =		    false;
    public	    boolean isUnDecryptable =		    false;
    public	    String  errorDescription =		    "";
    
    public FCPath(Path path)   { this.path = path; }
    
    public FCPath
    (
	Path path,boolean exist,int type,long size,boolean readable,boolean writable,boolean isHidden,boolean matchKey,boolean isValidPath, boolean isValidFile, boolean isValidDeviceProtected, boolean isValidDevice, boolean isValidPartition, boolean isKey, boolean isValidKey, boolean isValidKeyDir
//	, boolean isDecrypted, boolean isEncryptable, boolean isNewEncrypted, boolean isUnEncryptable, boolean hasFCToken, boolean isEncrypted, boolean isAuthenticated,boolean isDecryptable, boolean isNewDecrypted, boolean isUnDecryptable
	, boolean isDecrypted, boolean isEncryptable, boolean needsWriteAutoKey, long needsWriteAutoKeySize, boolean matchedReadAutoKey, long matchedReadAutoKeySize, boolean unmatchedReadAutoKey, long unmatchedReadAutoKeySize, boolean isNewEncrypted, boolean isUnEncryptable, boolean isEncrypted, int macVersion, boolean isDecryptable, boolean isNewDecrypted, boolean isUnDecryptable, String errorDesc
    )
    {
	this.path = path; this.exist = exist; this.type = type; this.size = size; this.isReadable = readable; this.isWritable = writable; this.isHidden = isHidden;	this.matchKey = matchKey;
	this.isValidPath = isValidPath; this.isValidFile = isValidFile; this.isValidDeviceProtected = isValidDeviceProtected; this.isValidDevice = isValidDevice; this.isValidPartition = isValidPartition; this.isKey = isKey; this.isValidKey = isValidKey; this.isValidKeyDir = isValidKeyDir;
	
	this.isDecrypted = isDecrypted; this.isEncryptable = isEncryptable; this.needsWriteAutoKey = needsWriteAutoKey; this.needsWriteAutoKeySize = needsWriteAutoKeySize; this.matchedReadAutoKey = matchedReadAutoKey; this.matchedReadAutoKeySize = matchedReadAutoKeySize; this.unmatchedReadAutoKey = unmatchedReadAutoKey; this.unmatchedReadAutoKeySize = unmatchedReadAutoKeySize; this.isNewEncrypted = isNewEncrypted; this.isUnEncryptable = isUnEncryptable; 
//	this.hasFCToken = hasFCToken; this.isEncrypted = isEncrypted; this.isAuthenticated = isAuthenticated; this.isDecryptable = isDecryptable; this.isNewDecrypted = isNewDecrypted; this.isUnDecryptable = isUnDecryptable;
  	this.isEncrypted = isEncrypted; this.macVersion = macVersion; this.isDecryptable = isDecryptable; this.isNewDecrypted = isNewDecrypted; this.isUnDecryptable = isUnDecryptable; this.errorDescription = errorDesc;
    }
        
    public FCPath clone(FCPath fcPath) // Used by FCPathList.updateStat(FCPath oldFCPath, FCPath newFCPath) Basically does a proper deep clone
    {
	FCPath newFCPath = new FCPath(fcPath.path);
	newFCPath.path = fcPath.path; newFCPath.exist = fcPath.exist; newFCPath.type =fcPath. type; newFCPath.size = fcPath.size; newFCPath.isReadable = fcPath.isReadable; newFCPath.isWritable = fcPath.isWritable; //   >
	newFCPath.isHidden = fcPath.isHidden; newFCPath.matchKey = fcPath.matchKey;
	newFCPath.isValidPath = fcPath.isValidPath; newFCPath.isValidFile = fcPath.isValidFile; newFCPath.isValidDeviceProtected = fcPath.isValidDeviceProtected; newFCPath.isValidDevice = fcPath.isValidDevice; newFCPath.isValidPartition = fcPath.isValidPartition; newFCPath.isKey = fcPath.isKey; newFCPath.isValidKey = fcPath.isValidKey; newFCPath.isValidKeyDir = fcPath.isValidKeyDir;
	newFCPath.isDecrypted = fcPath.isDecrypted; newFCPath.isEncryptable = fcPath.isEncryptable; newFCPath.needsWriteAutoKey = fcPath.needsWriteAutoKey; newFCPath.needsWriteAutoKeySize = fcPath.needsWriteAutoKeySize; newFCPath.matchedReadAutoKey = fcPath.matchedReadAutoKey; newFCPath.matchedReadAutoKeySize = fcPath.matchedReadAutoKeySize; newFCPath.unmatchedReadAutoKey = fcPath.unmatchedReadAutoKey; newFCPath.unmatchedReadAutoKeySize = fcPath.unmatchedReadAutoKeySize; newFCPath.isNewEncrypted = fcPath.isNewEncrypted; newFCPath.isUnEncryptable = fcPath.isUnEncryptable; 
//	newFCPath.hasFCToken = fcPath.hasFCToken; newFCPath.isEncrypted = fcPath.isEncrypted; newFCPath.isAuthenticated = fcPath.isAuthenticated; newFCPath.isDecryptable = fcPath.isDecryptable; newFCPath.isNewDecrypted = fcPath.isNewDecrypted; newFCPath.isUnDecryptable = fcPath.isUnDecryptable; 
	newFCPath.isEncrypted = fcPath.isEncrypted; newFCPath.macVersion = fcPath.macVersion; newFCPath.isDecryptable = fcPath.isDecryptable; newFCPath.isNewDecrypted = fcPath.isNewDecrypted; newFCPath.isUnDecryptable = fcPath.isUnDecryptable; newFCPath.errorDescription = fcPath.errorDescription;
	return newFCPath;
    }
    
//    public static String getKeySelectedDescription()		{ return FCPath.ITEMSELECTDESCRIPTION[type]; }	
    public static String getTypeString(int type) { return FCPath.TYPE_DESCRIPTION_ARRAY[type]; }
    
//    public String getString() {return Validate.getSting(this); }
    public String getString()
    {
	String returnString = "";
	returnString += "FCPath:\r\n";
	returnString += "\r\n";
	returnString += "Path:                  " + path.toAbsolutePath().toString() + "\r\n";
	returnString += "Exist:                 " + exist + "\r\n";
	returnString += "Type:                  " + getTypeString(type) + "\r\n";
	returnString += "Size:                  " + Validate.getHumanSize(size, 1,"Bytes") + "\r\n";
	returnString += "Readable:              " + isReadable + "\r\n";
	returnString += "Writable:              " + isWritable + "\r\n";
	returnString += "Hidden:                " + isHidden + "\r\n";
	returnString += "Match Key:             " + matchKey + "\r\n"; // Matches the exact keypath
	returnString += "\r\n";
	returnString += "Valid Path:            " + isValidPath + "\r\n";
	returnString += "Valid File:            " + isValidFile + "\r\n";
	returnString += "Valid Device:          " + isValidDevice + "\r\n";
	returnString += "Valid Partition:       " + isValidPartition + "\r\n";
	returnString += "Is Key:                " + isKey + "\r\n";
	returnString += "Valid Key:             " + isValidKey + "\r\n";
	returnString += "\r\n";
	returnString += "Unencrypted:           " + isDecrypted + "\r\n";
	returnString += "Encryptable:           " + isEncryptable + "\r\n";
//	returnString += "New Encrypted:         " + isNewEncrypted + "\r\n";
	returnString += "Unencryptable:         " + isUnEncryptable + "\r\n";
	returnString += "\r\n";
//	returnString += "Has FCToken:           " + hasFCToken + "\r\n";
	returnString += "Encrypted:             " + isEncrypted + "\r\n";
	returnString += "MAC Version:           " + macVersion + "\r\n";
//	returnString += "Authenticated:         " + isAuthenticated + "\r\n";
	returnString += "Decryptable:           " + isDecryptable + "\r\n";
//	returnString += "New Decrypted:         " + isNewDecrypted + "\r\n";
	returnString += "Undecryptable:         " + isUnDecryptable + "\r\n";
	returnString += "\r\n";
	returnString += "Create Key:            " + needsWriteAutoKey + "\r\n";
	returnString += "Match Key:             " + matchedReadAutoKey + "\r\n";    // Has a matching correlated key
	returnString += "Missing Key:           " + unmatchedReadAutoKey + "\r\n";  // Has a missing correlated key
	returnString += "\r\n";

	return returnString;
    }

}
