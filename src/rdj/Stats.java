/*
 * © copyleft 2018 ron
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

import java.util.ArrayList;
import java.util.Arrays;

public class Stats
{
//  Files
    private long filesProcessed = 0;
    private long filesTotal = 0;
    
//  File Encrypting Bytes
    private long fileBytesProcessed = 0;
    private long fileBytesTotal = 0;

//  Files Encrypting Bytes
    private long filesBytesProcessed = 0;
    private long filesBytesTotal = 0;
    
//  File Encryption Time
    private long fileStartNanoTime = 0;
    private long fileEndNanoTime = 0;

//  Files Encryption Time
    private long filesStartNanoTime = 0;
    private long filesEndNanoTime = 0;
    private long nanoSeconds = 0;

    public Stats()
    {
        
    }
    
//  Getters

//  Files
    public long getFilesProcessed()                                 { return filesProcessed; }
    public long getFilesTotal()                                     { return filesTotal; }

//  File Encrypting Bytes
    public long getFileBytesProcessed()                             { return fileBytesProcessed; }
    public long getFileBytesTotal()                                 { return fileBytesTotal; }

//  Files Encrypting Bytes
    public long getFilesBytesProcessed()                            { return filesBytesProcessed; }
    public long getFilesBytesTotal()                                { return filesBytesTotal; }

//  File Encryption Time
    public long getFileStartEpoch()                                 { return fileStartNanoTime; }
    public long getFileEndEpoch()                                   { return fileEndNanoTime; }

//  Files Encryption Time
    public long getFilesStartEpoch()                                { return filesStartNanoTime; }
    public long getFilesEndEpoch()                                  { return filesEndNanoTime; }
    
//  Setters
    
//  Files
    public void setFilesProcessed(long filesProcessed)              { this.filesProcessed = filesProcessed; }
    public void setFilesTotal(long filesTotal)                      { this.filesTotal = filesTotal; }

//  File Encrypting Bytes
    public void setFileBytesProcessed(long fileBytesProcessed)      { this.fileBytesProcessed = fileBytesProcessed; }
    public void setFileBytesTotal(long fileBytesTotal)              { this.fileBytesTotal = fileBytesTotal; }
        
//  Files Encrypting Bytes
    public void setFilesBytesProcessed(long filesBytesProcessed)    { this.filesBytesProcessed = filesBytesProcessed; }
    public void setFilesBytesTotal(long filesBytesTotal)            { this.filesBytesTotal = filesBytesTotal; }

//  File Encryption Time
//    public void setFileStartNanoTime()                            { this.fileStartNanoTime = System.currentTimeMillis(); }
//    public void setFileEndNanoTime()                              { this.fileEndNanoTime = System.currentTimeMillis(); }
    public void setFileStartNanoTime()                              { this.fileStartNanoTime = System.nanoTime(); }
    public void setFileEndNanoTime()                                { this.fileEndNanoTime = System.nanoTime(); }

//  Files Encryption Time
//    public void setFilesStartNanoTime()                           { this.filesStartNanoTime = System.currentTimeMillis(); }
//    public void setFilesEndNanoTime()                             { this.filesEndNanoTime = System.currentTimeMillis(); }
    public void setFilesStartNanoTime()                             { this.filesStartNanoTime = System.nanoTime(); }
    public void setFilesEndNanoTime()                               { this.filesEndNanoTime = System.nanoTime(); }
    public void clock()                                             { this.nanoSeconds += (filesEndNanoTime - filesStartNanoTime); }
    
//  Adders
    
//  Files
    public void addFilesProcessed(long filesProcessed)              { this.filesProcessed += filesProcessed; }
    public void addFilesTotal(long filesTotal)                      { this.filesTotal += filesTotal; }

//  File Encrypting Bytes
    public void addFileBytesProcessed(long fileBytesProcessed)      { this.fileBytesProcessed += fileBytesProcessed; }
    public void subFileBytesProcessed(long fileBytesProcessed)      { this.fileBytesProcessed -= fileBytesProcessed; }
    public void addFileBytesTotal(long fileBytesTotal)              { this.fileBytesTotal += fileBytesTotal; }
        
//  Files Encrypting Bytes
    public void addFilesBytesProcessed(long filesBytesProcessed)    { this.filesBytesProcessed += filesBytesProcessed; }
    public void addFilesBytesTotal(long filesBytesTotal)            { this.filesBytesTotal += filesBytesTotal; }

//  Stats
    
    
    
//  START
    public String getEncryptionStartSummary()                               
    {
        String fileString = "files"; if (filesTotal == 1) { fileString = "file"; } else { fileString = "files"; }
        String returnString = "Encryption: starting: " + filesTotal + " " + fileString + " totally " + getHumanSize(filesBytesTotal,1) + "\n";
        
        return returnString;
    }
    
    
    
    public String getFilesBytesProgressPercentage()                               
    {
        String returnString = new String();
        double percentage = (
                                 (double)(filesBytesProcessed) / (double)(filesBytesTotal) * 100.0 
                            ); // *1000 from mSec to Sec
//        String throughputString = String.format("%.1f", Math.floor(percentage));
        String throughputString = String.format("%.1f", percentage);
        returnString = " " + throughputString + "%\n";
        
        return returnString;
    }
    
    
//  END
    public String getEncryptionEndSummary()                               
    {
        String fileString = "files"; if (filesTotal == 1) { fileString = "file"; } else { fileString = "files"; }
        String returnString = "Encryption: finished: " + filesProcessed + " / " + filesTotal + " " + fileString + " totally " + getHumanSize(filesBytesProcessed, 1) + " / " + getHumanSize(filesBytesTotal ,1) + " finished in " + getDecimal(((nanoSeconds)/1000000000.0),1) + " seconds " + getFilesBytesThroughPut() + "\n";
        return returnString;
    }
    
    public String getFilesBytesThroughPut()                               
    {
        String returnString = new String();
        double throughput = ( ((double)(filesBytesProcessed) / (((double)nanoSeconds / 1000000000.0))) ); // *1000 from mSec to Sec
        String throughputString = String.format("%.1f", throughput);
        returnString = " (average: " + getHumanSize(throughput,1) + "/s)\n";
        
        return returnString;
    }
    
    
//  OTHER
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
    
    public static String getDecimal(double value,int decimals) { return String.format("%." + decimals + "f", (value)); }
    
    public void reset()
    {
        filesProcessed = 0;
        filesTotal = 0;
        fileBytesProcessed = 0;
        fileBytesTotal = 0;
        filesBytesProcessed = 0;
        filesBytesTotal = 0;
        fileStartNanoTime = 0;
        fileEndNanoTime = 0;
        filesStartNanoTime = 0;
        filesEndNanoTime = 0;
        nanoSeconds = 0;
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
