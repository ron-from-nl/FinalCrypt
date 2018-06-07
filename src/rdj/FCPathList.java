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

import java.util.ArrayList;

//public class FCPathList extends ArrayList<FCPath>
public class FCPathList<E> extends ArrayList<E>
{
    public	    long total =		    0;
    public	    long unexisting =		    0;
    public	    long existing =		    0;

    public	    long files =		    0;
    public	    long filesSize =		    0;
    public	    long directories =		    0;
    public	    long symlinkFiles =		    0;
    public	    long devices =		    0;
    public	    long devicesProtected =	    0;
    public	    long partitions =		    0;
    
    public	    long emptyFiles =		    0;
    
    public	    long readableFiles =	    0;
    public	    long unreadableFiles =	    0;
    public	    long unreadableFilesSize =	    0;
    public	    long writableFiles =	    0;
    public	    long unwritableFiles =	    0;
    public	    long unwritableFilesSize =	    0;
    public	    long hiddenFiles =		    0;
    public	    long hiddenFilesSize =	    0;
    public	    long matchingCipher =	    0;

    public	    long validPaths =		    0;
    public	    long validPathsSize =	    0;
    public	    long validFiles =		    0;
    public	    long validFilesSize =	    0;
    public	    long validDevices =		    0;
    public	    long validDevicesSize =	    0;
    public	    long validDevicesProtected =    0;
    public	    long validDevicesProtectedSize = 0;
    public	    long validPartitions =	    0;
    public	    long validPartitionsSize =	    0;

// Decrypted Files
    
    public	    long decryptedFiles =	    0; public	    long decryptedFilesSize =		0;
    public	    long encryptableFiles =	    0; public	    long encryptableFilesSize =		0;
    public	    long newEncryptedFiles =	    0; public	    long newEncryptedFilesSize =	0;
    public	    long encryptRemainingFiles =    0; public	    long encryptRemainingFilesSize =	0;
    public	    long unEncryptableFiles =	    0; public	    long unEncryptableFilesSize =	0;

// Encrypted Files

    public	    long encryptedFiles =	    0; public	    long encryptedFilesSize =		0;
    public	    long decryptableFiles =	    0; public	    long decryptableFilesSize =		0;
    public	    long newDecryptedFiles =	    0; public	    long newDecryptedFilesSize =	0;
    public	    long decryptRemainingFiles =    0; public	    long decryptRemainingFilesSize =	0;
    public	    long unDecryptableFiles =	    0; public	    long unDecryptableFilesSize =	0;
    
    public FCPathList() { clear(); clearStats(); }
    
//    @Override public void clear() { clearStats(); }
    
    @Override public boolean add(E e)
    {
	boolean result = super.add(e);
	if ( result ) { addStat((FCPath) e); }
	return result;
    }
    
    public void addStat(FCPath fcPath)
    {
	total++;
	if ( fcPath.exist )
	{
	    existing++;
	    if ( fcPath.matchCipher )					    { matchingCipher++; }
	    if	    ( fcPath.type == FCPath.DEVICE )			    { devices++;	    if ( fcPath.isValidDevice ) { validDevices++; validDevicesSize += fcPath.size; } }
	    else if ( fcPath.type == FCPath.DEVICE_PROTECTED )		    { devicesProtected++;   if ( fcPath.isValidDeviceProtected ) { validDevicesProtected++; validDevicesProtectedSize += fcPath.size; } }
	    else if ( fcPath.type == FCPath.PARTITION )			    { partitions++;	    if ( fcPath.isValidPartition ) { validPartitions++; validPartitionsSize += fcPath.size; }}
	    else if ( fcPath.type == FCPath.DIRECTORY )			    { directories++; }
	    else if ( fcPath.type == FCPath.INVALID )			    { unexisting++; }
	    else if ( fcPath.type == FCPath.SYMLINK )			    { symlinkFiles++; }
	    else if ( fcPath.type == FCPath.FILE )
	    {
		files++;
		if ( fcPath.size > 0 )					    { filesSize += fcPath.size; } else { emptyFiles++; }

		if ( fcPath.isReadable )					    { readableFiles++; }					    else { unreadableFiles++; unreadableFilesSize += fcPath.size; }
		if ( fcPath.isWritable )					    { writableFiles++; }					    else { unwritableFiles++; unwritableFilesSize += fcPath.size; }
		if ( fcPath.isHidden )					    { hiddenFiles++; hiddenFilesSize += fcPath.size; }
		if ( fcPath.isValidPath )				    { validPaths++;		validPathsSize += fcPath.size; }
		if ( fcPath.isValidFile )				    { validFiles++;		validFilesSize += fcPath.size; }

//		Decrypted files

		if ( fcPath.isDecrypted )				    { decryptedFiles++;		decryptedFilesSize += fcPath.size; }
		if ( fcPath.isEncryptable )				    { encryptableFiles++;	encryptableFilesSize += fcPath.size; }
		if ( fcPath.isNewEncrypted )				    { newEncryptedFiles++;	newEncryptedFilesSize += fcPath.size; }
		if ( fcPath.isEncryptable )				    { encryptRemainingFiles++;  encryptRemainingFilesSize += fcPath.size; } // Just here for Remaining stats
		if ( fcPath.isUnEncryptable )				    { unEncryptableFiles++;	unEncryptableFilesSize += fcPath.size; }

//		Encrypted files

		if ( fcPath.isEncrypted )				    { encryptedFiles++;		encryptedFilesSize += fcPath.size; }
		if ( fcPath.isDecryptable )				    { decryptableFiles++;	decryptableFilesSize += fcPath.size; }
		if ( fcPath.isNewDecrypted )				    { newDecryptedFiles++;	newDecryptedFilesSize += fcPath.size; }
		if (fcPath.isDecryptable)				    { decryptRemainingFiles++;  decryptRemainingFilesSize += fcPath.size; } // Just here for Remaining stats
		if (fcPath.isUnDecryptable)				    { unDecryptableFiles++;	unDecryptableFilesSize += fcPath.size; }
	    }
	} else { unexisting++; }
    }
    
//    FCPathList FCPath[x] -> FinalCrypt(newFCPath) -> change -> newFCPath -> fcPathList.update(oldFCPath, oldFCPath);
//						    -> clone  -> oldFCPath
    public void updateStat(FCPath oldFCPath, FCPath newFCPath) { removeStat(oldFCPath); addStat(newFCPath); oldFCPath = newFCPath.clone(newFCPath); } // Makes sure FinalCrypt gets an updated object back

    public void removeStat(FCPath fcPath)
    {
	total--;
	if ( fcPath.exist )
	{
	    existing--;
	    if ( fcPath.matchCipher )					    { matchingCipher--; }
	    if	    ( fcPath.type == FCPath.DEVICE )			    { devices--;	    if ( fcPath.isValidDevice ) { validDevices--; validDevicesSize -= fcPath.size; } }
	    else if ( fcPath.type == FCPath.DEVICE_PROTECTED )		    { devicesProtected--;   if ( fcPath.isValidDeviceProtected ) { validDevicesProtected--; validDevicesProtectedSize -= fcPath.size; } }
	    else if ( fcPath.type == FCPath.PARTITION )			    { partitions--;	    if ( fcPath.isValidPartition ) { validPartitions--; validPartitionsSize -= fcPath.size; }}
	    else if ( fcPath.type == FCPath.DIRECTORY )			    { directories--; }
	    else if ( fcPath.type == FCPath.INVALID )			    { unexisting--; }
	    else if ( fcPath.type == FCPath.SYMLINK )			    { symlinkFiles--; }
	    else if ( fcPath.type == FCPath.FILE )
	    {
		files--;
		if ( fcPath.size > 0 )					    { filesSize -= fcPath.size; } else { emptyFiles--; }

		if ( fcPath.isReadable )					    { readableFiles--; }					    else { unreadableFiles--; unreadableFilesSize -= fcPath.size; }
		if ( fcPath.isWritable )					    { writableFiles--; }					    else { unwritableFiles--; unwritableFilesSize -= fcPath.size; }
		if ( fcPath.isHidden )					    { hiddenFiles--; hiddenFilesSize -= fcPath.size; }
		if ( fcPath.isValidPath )				    { validPaths--;		validPathsSize -= fcPath.size; }
		if ( fcPath.isValidFile )				    { validFiles--;		validFilesSize -= fcPath.size; }

//		Decrypted files

		if ( fcPath.isDecrypted )				    { decryptedFiles--;		decryptedFilesSize -= fcPath.size; }
		if ( fcPath.isEncryptable )				    { encryptableFiles--;	encryptableFilesSize -= fcPath.size; }
		if ( fcPath.isNewEncrypted )				    { newEncryptedFiles--;	newEncryptedFilesSize -= fcPath.size; }
		if ( fcPath.isEncryptable )				    { encryptRemainingFiles--;  encryptRemainingFilesSize -= fcPath.size; } // Just here for Remaining stats
		if ( fcPath.isUnEncryptable )				    { unEncryptableFiles--;	unEncryptableFilesSize -= fcPath.size; }

//		Encrypted files

		if ( fcPath.isEncrypted )				    { encryptedFiles--;		encryptedFilesSize -= fcPath.size; }
		if ( fcPath.isDecryptable )				    { decryptableFiles--;	decryptableFilesSize -= fcPath.size; }
		if ( fcPath.isNewDecrypted )				    { newDecryptedFiles--;	newDecryptedFilesSize -= fcPath.size; }
		if (fcPath.isDecryptable)				    { decryptRemainingFiles--;  decryptRemainingFilesSize -= fcPath.size; } // Just here for Remaining stats
		if (fcPath.isUnDecryptable)				    { unDecryptableFiles--;	unDecryptableFilesSize -= fcPath.size; }
	    }
	} else { unexisting--; }
    }
    
//    public void updateStats() { clearStats(); for(FCPath fcPath:fcPathList) { addStat(fcPath); } }
    public void updateStats() { clearStats(); for(E e:this) { addStat((FCPath) e); } }

    public String getStats()
    {
	String returnString = "";
	returnString += "FCPathList Stats:\r\n";
	returnString += "\r\n";
	
	returnString += "Elements		: " +	size() + "\r\n";
	returnString += "Paths Total		: " +	total + "\r\n";
	returnString += "Paths Unexisting	: " +	unexisting + "\r\n";
	returnString += "Paths Existing		: " +	existing + "\r\n";
	returnString += "\r\n";
	returnString += "Files			: " +	files + "\r\n";
	returnString += "Files Total Size	: " +	Validate.getHumanSize(filesSize,1) + "\r\n";
//	returnString += "Directories		: " +	directories + "\r\n";
	returnString += "Symlink Files		: " +	symlinkFiles + "\r\n";
	returnString += "Devices			: " + devices + "\r\n";
	returnString += "Devices Protected	: " +	devicesProtected + "\r\n";
	returnString += "Partitions		: " +	partitions + "\r\n";
	returnString += "\r\n";
	returnString += "Empty Files		: " +	emptyFiles + "\r\n";
	returnString += "\r\n";
	returnString += "Readable Files		: " +	readableFiles + "\r\n";
	returnString += "Writable Files		: " +	writableFiles + "\r\n";
	returnString += "Hidden Files		: " +	hiddenFiles + "\r\n";
	returnString += "Cipher Matching 	: " +	matchingCipher + "\r\n";
	returnString += "Valid Paths		: " +	validPaths + " (" + Validate.getHumanSize(validPathsSize,1) + ")\r\n";
	returnString += "Valid Files		: " +	validFiles + " (" + Validate.getHumanSize(validFilesSize,1) + ")\r\n";
	returnString += "Valid Devices		: " +	validDevices + " (" + Validate.getHumanSize(validDevicesSize,1) + ")\r\n";
	returnString += "Valid Devices		: " +	validDevicesProtected + " (" + Validate.getHumanSize(validDevicesProtectedSize,1) + ")\r\n";
	returnString += "Valid Partitions	: " +	validPartitions + " (" + Validate.getHumanSize(validPartitionsSize,1) + ")\r\n";
	returnString += "\r\n";
	returnString += "Decrypted Files 	: " +	decryptedFiles + " (" + Validate.getHumanSize(decryptedFilesSize,1) + ")\r\n";
	returnString += "Encryptable Files	: " +	encryptableFiles + " (" + Validate.getHumanSize(encryptableFilesSize,1) + ")\r\n";
	returnString += "New Encrypted Files 	: " +	newEncryptedFiles + " (" + Validate.getHumanSize(newEncryptedFilesSize,1) + ")\r\n";
	returnString += "Encrypt Remaining Files : " +	encryptRemainingFiles + " (" + Validate.getHumanSize(encryptRemainingFilesSize,1) + ")\r\n";
	returnString += "Unencryptable Files	: " +	unEncryptableFiles + " (" + Validate.getHumanSize(unEncryptableFilesSize,1) + ")\r\n";
	returnString += "\r\n";
	returnString += "Encrypted Files 	: " +	encryptedFiles + " (" + Validate.getHumanSize(encryptedFilesSize,1) + ")\r\n";
	returnString += "Decryptable Files	: " +	decryptableFiles + " (" + Validate.getHumanSize(decryptableFilesSize,1) + ")\r\n";
	returnString += "New Decrypted Files	: " +	newDecryptedFiles + " (" + Validate.getHumanSize(newDecryptedFilesSize,1) + ")\r\n";
	returnString += "Decrypt Remaining Files : " +	decryptRemainingFiles + " (" + Validate.getHumanSize(decryptRemainingFilesSize,1) + ")\r\n";
	returnString += "UnDecryptable Files	: " +	unDecryptableFiles + " (" + Validate.getHumanSize(unDecryptableFilesSize,1) + ")\r\n";
	returnString += "\r\n";
//	returnString += "File  Valid Ciphers	: " +	filesValidCipher + "\r\n"; // Targets can't be ciphers (only match cipherpaths)

	return returnString;
    }

    public void clearStats()
    {
	this.clear();
	total =			    0;
	unexisting =		    0;
	existing =		    0;

	files =			    0;
	directories =		    0;
	symlinkFiles =		    0;
	devices =		    0;
	devicesProtected =	    0;
	partitions =		    0;

	filesSize =		    0;
	emptyFiles =		    0;

	readableFiles =		    0;
	writableFiles =		    0;
	hiddenFiles =		    0;
	matchingCipher =	    0;

	validPaths =		    0;
	validPathsSize =	    0;
	validFiles =		    0;
	validFilesSize =	    0;
	validDevices =		    0;
	validDevicesSize =	    0;
	validDevicesProtected =	    0;
	validDevicesProtectedSize = 0;
	validPartitions =	    0;
	validPartitionsSize =	    0;

    // Decrypted Files

	decryptedFiles =	    0; decryptedFilesSize =	    0;
	encryptableFiles =	    0; encryptableFilesSize =	    0;
	newEncryptedFiles =	    0; newEncryptedFilesSize =	    0;
	encryptRemainingFiles = 0; encryptRemainingFilesSize =  0;
	unEncryptableFiles =    0; unEncryptableFilesSize =	    0;

    // Encrypted Files

	encryptedFiles =	    0; encryptedFilesSize =	    0;
	decryptableFiles =	    0; decryptableFilesSize =	    0;
	newDecryptedFiles =	    0; newDecryptedFilesSize =	    0;
	decryptRemainingFiles = 0; decryptRemainingFilesSize =  0;
	unDecryptableFiles =    0; unDecryptableFilesSize =	    0;
    }
}
