/*
 * Copyright (C) 2018 Ron de Jong (ronuitzaandam@gmail.com).
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

import java.util.ArrayList;
import java.util.Arrays;

public class Stat
{
//  File Encrypting Bytes
    private long fileBytesProcessed = 0;
    private long fileBytesTotal = 0;
    
//  File Encryption Time
    private long fileStartEpoch = 0;
    private long fileEndEpoch = 0;
    private long nanoSeconds = 0;


    public Stat()
    {
        
    }
    
//  Getters

//  File Encrypting Bytes
    public long getFileBytesProcessed()                             { return fileBytesProcessed; }
    public long getFileBytesTotal()                                 { return fileBytesTotal; }

//  File Encryption Time
    public long getFileStartEpoch()                                 { return fileStartEpoch; }
    public long getFileEndEpoch()                                   { return fileEndEpoch; }
    public long getMSecCounted()                                    { return nanoSeconds; }

//  Setters
    
//  File Encrypting Bytes
    public void setFileBytesProcessed(long fileBytesProcessed)      { this.fileBytesProcessed = fileBytesProcessed; }
    public void setFileBytesTotal(long fileBytesTotal)              { this.fileBytesTotal = fileBytesTotal; }
        
//  Files Encrypting Bytes
//  File Encryption Time
//    public void setFileStartEpoch()                                 { this.fileStartEpoch = System.currentTimeMillis(); }
//    public void setFileEndEpoch()                                   { this.fileEndEpoch = System.currentTimeMillis(); }
    public void setFileStartEpoch()                                 { this.fileStartEpoch = System.nanoTime(); }
    public void setFileEndEpoch()                                   { this.fileEndEpoch = System.nanoTime(); }
    public void clock()                                             { this.nanoSeconds += (fileEndEpoch - fileStartEpoch); }
    public void setMSecCounted(long param)                          { this.nanoSeconds = param; }
    public void addMSecCounted(long param)                          { this.nanoSeconds += param; }
            
//  Adders
    
//  File Encrypting Bytes
    public void addFileBytesProcessed(long fileBytesProcessed)      { this.fileBytesProcessed += fileBytesProcessed; }
    public void subFileBytesProcessed(long fileBytesProcessed)      { this.fileBytesProcessed -= fileBytesProcessed; }
    public void addFileBytesTotal(long fileBytesTotal)              { this.fileBytesTotal += fileBytesTotal; }

//  Stats
    
    
    
//  MIDDLE
    public String getFileBytesThroughPut()                               
    {
        String returnString = new String();
        double throughput = ( ((double)(fileBytesProcessed) / (((double)nanoSeconds / 1000000000.0))) ); // *1000 from mSec to Sec
        String throughputString = String.format("%.1f", throughput);
        returnString = getHumanSize(throughput,1) + "/s";
        
        return returnString;
    }
    
    public String getFileBytesProgressPercentage()                               
    {
        String returnString = new String();
        double percentage = ( ((double)fileBytesProcessed / (((double)fileBytesTotal / 100.0 )))); // *1000 from mSec to Sec
        String throughputString = String.format("%.0f", Math.floor(percentage));
        returnString = " " + throughputString + "%\n";
        
        return returnString;
    }
    
    
//  OTHER
    public static String getHumanSize(double value,int decimals)
    {
        int x = 0;
        long factor;
        double newValue = value;
        String returnString = new String("");
        ArrayList<String> magnitude = new ArrayList<String>(); magnitude.addAll(Arrays.asList("ZiB","EiB","PiB","TiB","GiB","MiB","KiB","Bytes"));
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
        fileBytesProcessed = 0;
        fileBytesTotal = 0;
        fileStartEpoch = 0;
        fileEndEpoch = 0;
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
