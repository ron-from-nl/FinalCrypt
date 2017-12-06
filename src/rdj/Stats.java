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
package rdj;

public class Stats
{
//  Files
    private long filesEncrypted = 0;
    private long filesTotal = 0;
    
//  File Encrypting Bytes
    private long fileBytesEncrypted = 0;
    private long fileBytesTotal = 0;

//  Files Encrypting Bytes
    private long filesBytesEncrypted = 0;
    private long filesBytesTotal = 0;
    
//  File Encryption Time
    private long fileStartEpoch = 0;
    private long fileEndEpoch = 0;

//  Files Encryption Time
    private long filesStartEpoch = 0;
    private long filesEndEpoch = 0;

    public Stats()
    {
        
    }
    
//  Getters

//  Files
    public long getFilesEncrypted()                                 { return filesEncrypted; }
    public long getFilesTotal()                                     { return filesTotal; }

//  File Encrypting Bytes
    public long getFileBytesEncrypted()                             { return fileBytesEncrypted; }
    public long getFileBytesTotal()                                 { return fileBytesTotal; }

//  Files Encrypting Bytes
    public long getFilesBytesEncrypted()                            { return filesBytesEncrypted; }
    public long getFilesBytesTotal()                                { return filesBytesTotal; }

//  File Encryption Time
    public long getFileStartEpoch()                                 { return fileStartEpoch; }
    public long getFileEndEpoch()                                   { return fileEndEpoch; }

//  Files Encryption Time
    public long getFilesStartEpoch()                                { return filesStartEpoch; }
    public long getFilesEndEpoch()                                  { return filesEndEpoch; }
    
//  Setters
    
//  Files
    public void setFilesEncrypted(long filesEncrypted)              { this.filesEncrypted = filesEncrypted; }
    public void setFilesTotal(long filesTotal)                      { this.filesTotal = filesTotal; }

//  File Encrypting Bytes
    public void setFileBytesEncrypted(long fileBytesEncrypted)      { this.fileBytesEncrypted = fileBytesEncrypted; }
    public void setFileBytesTotal(long fileBytesTotal)              { this.fileBytesTotal = fileBytesTotal; }
        
//  Files Encrypting Bytes
    public void setFilesBytesEncrypted(long filesBytesEncrypted)    { this.filesBytesEncrypted = filesBytesEncrypted; }
    public void setFilesBytesTotal(long filesBytesTotal)            { this.filesBytesTotal = filesBytesTotal; }

//  File Encryption Time
    public void setFileStartEpoch()                                 { this.fileStartEpoch = System.currentTimeMillis(); }
    public void setFileEndEpoch()                                   { this.fileEndEpoch = System.currentTimeMillis(); }
            
//  Files Encryption Time
    public void setFilesStartEpoch()                                { this.filesStartEpoch = System.currentTimeMillis(); }
    public void setFilesEndEpoch()                                  { this.filesEndEpoch = System.currentTimeMillis(); }
    
//  Adders
    
//  Files
    public void addFilesEncrypted(long filesEncrypted)              { this.filesEncrypted += filesEncrypted; }
    public void addFilesTotal(long filesTotal)                      { this.filesTotal += filesTotal; }

//  File Encrypting Bytes
    public void addFileBytesEncrypted(long fileBytesEncrypted)      { this.fileBytesEncrypted += fileBytesEncrypted; }
    public void addFileBytesTotal(long fileBytesTotal)              { this.fileBytesTotal += fileBytesTotal; }
        
//  Files Encrypting Bytes
    public void addFilesBytesEncrypted(long filesBytesEncrypted)    { this.filesBytesEncrypted += filesBytesEncrypted; }
    public void addFilesBytesTotal(long filesBytesTotal)            { this.filesBytesTotal += filesBytesTotal; }

//  Stats
    
//  File Stats
    public String getFileThroughPut()                               
    {
        String returnString = new String();
        double fileDiffEpoch = (( (double)fileBytesEncrypted / ((fileEndEpoch - fileStartEpoch)/1000f))/ 1000000f);
        String throughput = String.format("%.1f", fileDiffEpoch);
        returnString = " (" + throughput + " MB/Sec)\n";
        
        return returnString;
    }
    
//  Files Stats
    public String getFilesThroughPut()                               
    {
        String returnString = new String();
        double filesDiffEpoch = (( (double)filesBytesEncrypted / ((filesEndEpoch - filesStartEpoch)/1000f))/ 1000000f);
        String throughput = String.format("%.1f", filesDiffEpoch);
        returnString = " (average: " + throughput + "MB/Sec)\n";
        
        return returnString;
    }
    
    public String getEncryptionEndSummary()                               
    {
        String returnString = "Encrypted " + filesEncrypted + " / " + filesTotal + " files totally " + filesBytesEncrypted + "MB / " + filesBytesEncrypted + "MB finished in " + ((filesEndEpoch - filesStartEpoch)/1000)  + " seconds " + getFilesThroughPut() + "\n";
        
        return returnString;
    }
    
    public String getEncryptionStartSummary()                               
    {
        String returnString = "Encrypting " + filesTotal + " files totally " + filesBytesEncrypted + "MB\n";
        
        return returnString;
    }
    

//Encrypting 2569 files totally 300MB 
//Encrypting 2565 files totally 300MB finished in 6 seconds (average: 50MB/Sec)    

}
