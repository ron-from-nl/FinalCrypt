/*
 * Copyright © 2017 Ron de Jong (ronuitzaandam@gmail.com).
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
    public static	   int	    KEY_SIZE_MIN =	1024;

    public static final    int	    INVALID =		0;
    public static final    int	    FILE =		1;
    public static final    int	    DIRECTORY =		2;
    public static final    int	    SYMLINK =		3;
    public static final    int	    DEVICE =		4;
    public static final    int	    PARTITION =		5;
    public static final    int	    DEVICE_INVALID =	6;
    public static final    int	    DEVICE_PROTECTED =	7;
    
    public static final String[]   TYPE_DESCRIPTION_ARRAY = new String[] { "Invalid","File","Directory","Symlink","Device","Partition","Device Invalid","Device Protected" };
    
    public	    Path    path;

    public	    boolean exist =		    false;
    public	    int	    type =		    INVALID;
    public	    long    size =		    0;
    public	    boolean isReadable =	    false;
    public	    boolean isWritable =	    false;
    public	    boolean isHidden =		    false;
    public	    boolean matchKey =	    false;

    public	    boolean isValidPath =	    false;
    public	    boolean isValidFile =	    false;
    public	    boolean isValidDeviceProtected = false;
    public	    boolean isValidDevice =	    false;
    public	    boolean isValidPartition =	    false;
    
    public	    boolean isKey =		    false;
    public	    boolean isValidKey =	    false;

//  Decrypted
    
    public	    boolean isDecrypted =	    false;
    public	    boolean isEncryptable =	    false;
    public	    boolean isNewEncrypted =	    false;
    public	    boolean isUnEncryptable =	    false;

//  Encrypted

//    public	    boolean hasFCToken =	    false;
    public	    boolean isEncrypted =	    false;
//    public	    boolean isAuthenticated =	    false;
    public	    boolean isDecryptable =	    false;    
    public	    boolean isNewDecrypted =	    false;
    public	    boolean isUnDecryptable =	    false;    
    
    public FCPath(Path path)   { this.path = path; }
    
    public FCPath
    (
	Path path,boolean exist,int type,long size,boolean readable,boolean writable,boolean isHidden,boolean matchKey,boolean isValidPath, boolean isValidFile, boolean isValidDeviceProtected, boolean isValidDevice, boolean isValidPartition, boolean isKey, boolean isValidKey
//	, boolean isDecrypted, boolean isEncryptable, boolean isNewEncrypted, boolean isUnEncryptable, boolean hasFCToken, boolean isEncrypted, boolean isAuthenticated,boolean isDecryptable, boolean isNewDecrypted, boolean isUnDecryptable
	, boolean isDecrypted, boolean isEncryptable, boolean isNewEncrypted, boolean isUnEncryptable, boolean isEncrypted, boolean isDecryptable, boolean isNewDecrypted, boolean isUnDecryptable
    )
    {
	this.path = path; this.exist = exist; this.type = type; this.size = size; this.isReadable = readable; this.isWritable = writable; this.isHidden = isHidden;	this.matchKey = matchKey;
	this.isValidPath = isValidPath; this.isValidFile = isValidFile; this.isValidDeviceProtected = isValidDeviceProtected; this.isValidDevice = isValidDevice; this.isValidPartition = isValidPartition; this.isKey = isKey; this.isValidKey = isValidKey;
	
	this.isDecrypted = isDecrypted; this.isEncryptable = isEncryptable; this.isNewEncrypted = isNewEncrypted; this.isUnEncryptable = isUnEncryptable; 
//	this.hasFCToken = hasFCToken; this.isEncrypted = isEncrypted; this.isAuthenticated = isAuthenticated; this.isDecryptable = isDecryptable; this.isNewDecrypted = isNewDecrypted; this.isUnDecryptable = isUnDecryptable;
  	this.isEncrypted = isEncrypted; this.isDecryptable = isDecryptable; this.isNewDecrypted = isNewDecrypted; this.isUnDecryptable = isUnDecryptable;
    }
        
    public FCPath clone(FCPath fcPath) // Used by FCPathList.updateStat(FCPath oldFCPath, FCPath newFCPath) Basically does a proper deep clone
    {
	FCPath newFCPath = new FCPath(fcPath.path);
	newFCPath.path = fcPath.path; newFCPath.exist = fcPath.exist; newFCPath.type =fcPath. type; newFCPath.size = fcPath.size; newFCPath.isReadable = fcPath.isReadable; newFCPath.isWritable = fcPath.isWritable;
	newFCPath.isHidden = fcPath.isHidden; newFCPath.matchKey = fcPath.matchKey;
	newFCPath.isValidPath = fcPath.isValidPath; newFCPath.isValidFile = fcPath.isValidFile; newFCPath.isValidDeviceProtected = fcPath.isValidDeviceProtected; newFCPath.isValidDevice = fcPath.isValidDevice; newFCPath.isValidPartition = fcPath.isValidPartition; newFCPath.isKey = fcPath.isKey; newFCPath.isValidKey = fcPath.isValidKey;
	newFCPath.isDecrypted = fcPath.isDecrypted; newFCPath.isEncryptable = fcPath.isEncryptable; newFCPath.isNewEncrypted = fcPath.isNewEncrypted; newFCPath.isUnEncryptable = fcPath.isUnEncryptable; 
//	newFCPath.hasFCToken = fcPath.hasFCToken; newFCPath.isEncrypted = fcPath.isEncrypted; newFCPath.isAuthenticated = fcPath.isAuthenticated; newFCPath.isDecryptable = fcPath.isDecryptable; newFCPath.isNewDecrypted = fcPath.isNewDecrypted; newFCPath.isUnDecryptable = fcPath.isUnDecryptable; 
	newFCPath.isEncrypted = fcPath.isEncrypted; newFCPath.isDecryptable = fcPath.isDecryptable; newFCPath.isNewDecrypted = fcPath.isNewDecrypted; newFCPath.isUnDecryptable = fcPath.isUnDecryptable; 
	return newFCPath;
    }
    
//    public static String getKeySelectedDescription()		{ return FCPath.ITEMSELECTDESCRIPTION[type]; }	
    public static String getTypeString(int type) { return FCPath.TYPE_DESCRIPTION_ARRAY[type]; }
    
    public String getString() {return Validate.getSting(this); }

}
