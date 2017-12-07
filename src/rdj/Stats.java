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

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;

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
    public String getFileBytesThroughPut()                               
    {
        String returnString = new String();
        double throughput = ( ((double)fileBytesEncrypted / ((fileEndEpoch - fileStartEpoch))) * 1000 ); // *1000 from mSec to Sec
        String throughputString = String.format("%.1f", throughput);
        returnString = " (" + getHumanSize(throughput,1) + "/Sec)\n";
        
        return returnString;
    }
    
//  Files Stats
    public String getFilesBytesThroughPut()                               
    {
        String returnString = new String();
        double throughput = ( ((double)filesBytesEncrypted / ((filesEndEpoch - filesStartEpoch))) * 1000 ); // *1000 from mSec to Sec
        String throughputString = String.format("%.1f", throughput);
        returnString = " (average: " + getHumanSize(throughput,1) + "/Sec)\n";
        
        return returnString;
    }
    
    public String getEncryptionStartSummary()                               
    {
        String returnString = "Encryption starting: " + filesTotal + " files totally " + getHumanSize(filesBytesTotal,1) + "\n";
        
        return returnString;
    }
    
    public String getEncryptionEndSummary()                               
    {
//        String returnString = "Encrypted " + filesEncrypted + " / " + filesTotal + " files totally " + String.format("%.1f", (double)(filesBytesEncrypted/(1024*1024))) + "MB / " + String.format("%.1f", (filesBytesTotal/(1024*1024))) + "MB finished in " + ((filesEndEpoch - filesStartEpoch)/1000)  + " seconds " + getFilesBytesThroughPut() + "\n";
        String returnString = "Encryption finished: " + filesEncrypted + " / " + filesTotal + " files totally " + getHumanSize(filesBytesEncrypted,1) + " / " + getHumanSize(filesBytesTotal,1) + " finished in " + ((filesEndEpoch - filesStartEpoch)/1000)  + " seconds " + getFilesBytesThroughPut() + "\n";
        
        return returnString;
    }
    
    public static String getHumanSize(double value,int decimals)
    {
        int x = 0;
        long factor;
        double newValue = value;
        String returnString = new String("");
        ArrayList<String> magnitude = new ArrayList<String>(); magnitude.addAll(Arrays.asList("ZB","EB","PB","TB","GB","MB","KB","Bytes"));
        for (factor = 70; factor > 0; factor -= 10)
        {
            if ((value / Math.pow(2, factor)) >= 1) { newValue = (value / Math.pow(2, factor)); returnString = String.format("%.1f", (newValue)) + " " + magnitude.get(x); break; } x++;
        }
        if (factor == 0) { newValue = (value / Math.pow(2, factor)); returnString = String.format("%." + decimals + "f", (newValue)) + " " + magnitude.get(x); }
        return returnString;
    }
    
    public void reset()
    {
        filesEncrypted = 0;
        filesTotal = 0;
        fileBytesEncrypted = 0;
        fileBytesTotal = 0;
        filesBytesEncrypted = 0;
        filesBytesTotal = 0;
        fileStartEpoch = 0;
        fileEndEpoch = 0;
        filesStartEpoch = 0;
        filesEndEpoch = 0;
    }
    
//    public static void main(String[] args)
//    {
//        System.out.println(getHumanSize(12l));
//        System.out.println(getHumanSize(13000l));
//        System.out.println(getHumanSize(14000000l));
//        System.out.println(getHumanSize(15000000000l));
//        System.out.println(getHumanSize(16000000000000l));
//        System.out.println(getHumanSize(17000000000000000l));
//        System.out.println(getHumanSize(1800000000000000000l));
//    }
}

//Encryption finished: 153 / 153 files totally 23,5 MB / 23,5 MB finished in 3 seconds  (average: 7,6 MB/Sec)
//Encryption starting: 153 files totally 23,5 MB