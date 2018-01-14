/*
 * Copyright (C) 2018 ron
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Device
{
    int bufferSize = 1024 * 1024 * 1;
    long deviceSize = 0;
    long cipherSize = 0;
    static long bytesPerSector = 512;
    static UI ui;
    private static boolean pausing;
    private static boolean stopPending;
    
    public Device(UI ui)
    {
        this.ui = ui;
    }
    
//  Read byte[] from device
    synchronized public static byte[] read(Path rawDeviceFilePath, long lba, long length)
    {        
        long readInputDeviceChannelTransfered = 0;
        ByteBuffer inputDeviceBuffer = ByteBuffer.allocate((int)length); inputDeviceBuffer.clear();
        try (final SeekableByteChannel readInputDeviceChannel = Files.newByteChannel(rawDeviceFilePath, EnumSet.of(StandardOpenOption.READ)))
        {
            readInputDeviceChannel.position(getLBAOffSet(bytesPerSector, getDeviceSize(rawDeviceFilePath), lba));
            readInputDeviceChannelTransfered = readInputDeviceChannel.read(inputDeviceBuffer); inputDeviceBuffer.flip();
            readInputDeviceChannel.close();
            ui.log("Read LBA " + lba + " Transfered: " + readInputDeviceChannelTransfered + "\n");
        } catch (IOException ex) { ui.status("Device().read(..) " + ex.getMessage(), true); }
        return inputDeviceBuffer.array();
    }
    
//  Write byte[] to device
    synchronized public static void write(byte[] bytes, Path rawDeviceFilePath, long lba)
    {        
        long writeOutputDeviceChannelTransfered = 0;
        ByteBuffer outputDeviceBuffer = null;
        try (final SeekableByteChannel writeOutputDeviceChannel = Files.newByteChannel(rawDeviceFilePath, EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.SYNC)))
        {
            outputDeviceBuffer = ByteBuffer.allocate(bytes.length); outputDeviceBuffer.put(bytes); outputDeviceBuffer.flip(); // logBytes(outputDeviceBuffer.array());
//            guifx.log("\nBuffer: " + outputDeviceBuffer.capacity());
            writeOutputDeviceChannel.position(getLBAOffSet(bytesPerSector, getDeviceSize(rawDeviceFilePath), lba));
            writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer);
            ui.log("Wrote LBA( " + lba + ")  Pos (" + getLBAOffSet(bytesPerSector, getDeviceSize(rawDeviceFilePath), lba) + ") Transfered: " + writeOutputDeviceChannelTransfered + "\n");
            writeOutputDeviceChannel.close();
        } catch (IOException ex) { ui.status(Arrays.toString(ex.getStackTrace()), true); }
    }

    
    
    
//  Write CipherFile to partition 1 & 2
    synchronized public void write(Path cipherFilePath, Path rawDeviceFilePath, long firstLBA1, long firstLBA2)
    {
        long deviceSize = getDeviceSize(rawDeviceFilePath);
        long cipherSize = getCipherSize(cipherFilePath);
        if ( cipherSize < bufferSize)   { bufferSize = (int)cipherSize; ui.log("BufferSize is limited to cipherfile size: " + GPT.getHumanSize(bufferSize, 1) + " \n"); }
//        else                            { log("BufferSize is set to: " + getHumanSize(bufferSize, 1) + " \n"); }
        Stats allDataStats = new Stats(); allDataStats.reset();        
        Stat readCipherFileStat1 = new Stat(); readCipherFileStat1.reset();
        Stat readCipherFileStat2 = new Stat(); readCipherFileStat2.reset();
        Stat writeCipherFileStat1 = new Stat(); writeCipherFileStat1.reset();
        Stat writeCipherFileStat2 = new Stat(); writeCipherFileStat2.reset();

        allDataStats.setFilesTotal(2);
        allDataStats.setAllDataBytesTotal(this.getCipherSize(cipherFilePath) * 2);
        ui.status(allDataStats.getStartSummary(), true);
        try { Thread.sleep(100); } catch (InterruptedException ex) {  }
        
        boolean inputEnded = false;
        long readCipherFileChannelPosition = 0;
        long readCipherFileChannelTransfered = 0;
        long writeOutputDeviceChannelPosition = 0;                
        long writeOutputDeviceChannelTransfered = 0;                
//      Write the cipherfile to 1st partition
        ByteBuffer cipherFileBuffer =  ByteBuffer.allocate(bufferSize); cipherFileBuffer.clear();
        ByteBuffer outputDeviceBuffer = null; // =  ByteBuffer.allocate(bufferSize);  inputDeviceBuffer.clear();


//      Setup the Progress TIMER & TASK
        Timeline updateProgressTimeline = new Timeline(new KeyFrame( Duration.millis(200), ae ->
        ui.encryptionProgress
        (
                (int) (
                        (
                                readCipherFileStat1.getFileBytesProcessed() +
                                        writeCipherFileStat1.getFileBytesProcessed() +
                                        readCipherFileStat2.getFileBytesProcessed() +
                                        writeCipherFileStat2.getFileBytesProcessed()
                                ) / ( (allDataStats.getFileBytesTotal() * 1 ) / 100.0)),

                (int) (
                        ( allDataStats.getFilesBytesProcessed() * 4) /
                                ( (allDataStats.getFilesBytesTotal() * 4) / 100.0)
                        )
        )        ));
        updateProgressTimeline.setCycleCount(Animation.INDEFINITE); updateProgressTimeline.play();
        allDataStats.setAllDataStartNanoTime();

        ui.log("Writing " + cipherFilePath.toAbsolutePath() + " to partition 1 (LBA:"+ firstLBA1 + ":" + (getLBAOffSet(bytesPerSector, deviceSize, firstLBA1) + writeOutputDeviceChannelPosition) + ")");
        write1loop: while ( ! inputEnded )
        {
//            while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
//            if (stopPending)    { inputEnded = true; break write1loop; }

            readCipherFileStat1.setFileStartEpoch();
            try (final SeekableByteChannel readCipherFileChannel = Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)))
            {
                // Fill up cipherFileBuffer
                readCipherFileChannel.position(readCipherFileChannelPosition);
                readCipherFileChannelTransfered = readCipherFileChannel.read(cipherFileBuffer); readCipherFileChannelPosition += readCipherFileChannelTransfered;
                if (( readCipherFileChannelTransfered == -1 ) || ( cipherFileBuffer.limit() < bufferSize )) { inputEnded = true; }
                cipherFileBuffer.flip();
                readCipherFileChannel.close(); readCipherFileStat1.setFileEndEpoch(); readCipherFileStat1.clock();
                readCipherFileStat1.addFileBytesProcessed(readCipherFileChannelTransfered); allDataStats.addAllDataBytesProcessed(readCipherFileChannelTransfered);
            } catch (IOException ex) { ui.log("Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex + "\n"); }

//          Write Device
            writeCipherFileStat1.setFileStartEpoch();
            try (final SeekableByteChannel writeOutputDeviceChannel = Files.newByteChannel(rawDeviceFilePath, EnumSet.of(StandardOpenOption.WRITE,StandardOpenOption.SYNC)))
            {
//              Write cipherfile to partition 1
                writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, deviceSize, firstLBA1) + writeOutputDeviceChannelPosition));
                writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(cipherFileBuffer); cipherFileBuffer.flip();
                writeCipherFileStat1.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed(readCipherFileChannelTransfered);
                
                writeOutputDeviceChannelPosition += writeOutputDeviceChannelTransfered;
                writeOutputDeviceChannel.close(); writeCipherFileStat1.setFileEndEpoch(); writeCipherFileStat1.clock();
            } catch (IOException ex) { ui.status(Arrays.toString(ex.getStackTrace()), true); }
//            log("\nwriteOutputDeviceChannelTransfered: " + writeOutputDeviceChannelTransfered + "\n");
            cipherFileBuffer.clear();
        }
        readCipherFileChannelPosition = 0;
        readCipherFileChannelTransfered = 0;
        writeOutputDeviceChannelPosition = 0;                
        writeOutputDeviceChannelTransfered = 0;                
        inputEnded = false;

//      FILE STATUS        
        ui.log(" - Write: rd(" +  readCipherFileStat1.getFileBytesThroughPut() + ") -> ");
        ui.log("wr(" +           writeCipherFileStat1.getFileBytesThroughPut() + ") ");
        ui.log(" - Write: rd(" +  readCipherFileStat2.getFileBytesThroughPut() + ") -> ");
        ui.log("wr(" +           writeCipherFileStat2.getFileBytesThroughPut() + ") ");
        ui.log(allDataStats.getAllDataBytesProgressPercentage());


//      Write the cipherfile to 2nd partition 
        ui.log("Writing " + cipherFilePath.toAbsolutePath() + " to partition 2 (LBA:"+ firstLBA2 + ":" + (getLBAOffSet(bytesPerSector, deviceSize, firstLBA2) + writeOutputDeviceChannelPosition) + ")");
        write2loop: while ( ! inputEnded )
        {
//            while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
//            if (stopPending)    { inputEnded = true; break write2loop; }

            readCipherFileStat2.setFileStartEpoch();
            try (final SeekableByteChannel readCipherFileChannel = Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)))
            {
                // Fill up cipherFileBuffer
                readCipherFileChannel.position(readCipherFileChannelPosition);
                readCipherFileChannelTransfered = readCipherFileChannel.read(cipherFileBuffer); readCipherFileChannelPosition += readCipherFileChannelTransfered;
                if (( readCipherFileChannelTransfered == -1 ) || ( cipherFileBuffer.limit() < bufferSize )) { inputEnded = true; }
                cipherFileBuffer.flip();
                readCipherFileChannel.close(); readCipherFileStat2.setFileEndEpoch(); readCipherFileStat2.clock();
                readCipherFileStat2.addFileBytesProcessed(readCipherFileChannelTransfered); allDataStats.addAllDataBytesProcessed(readCipherFileChannelTransfered);
            } catch (IOException ex) { ui.log("Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex + "\n"); }
            
//          Write Device
            writeCipherFileStat2.setFileStartEpoch();
            try (final SeekableByteChannel writeOutputDeviceChannel = Files.newByteChannel(rawDeviceFilePath, EnumSet.of(StandardOpenOption.WRITE,StandardOpenOption.SYNC)))
            {
//              Write cipherfile to partition 1
                writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, deviceSize, firstLBA2) + writeOutputDeviceChannelPosition));
                writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(cipherFileBuffer); cipherFileBuffer.flip();
                writeCipherFileStat2.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed(readCipherFileChannelTransfered);
                
                writeOutputDeviceChannelPosition += writeOutputDeviceChannelTransfered;
                writeOutputDeviceChannel.close(); writeCipherFileStat2.setFileEndEpoch(); writeCipherFileStat2.clock();
            } catch (IOException ex) { ui.status(Arrays.toString(ex.getStackTrace()), true); }
//            log("\nwriteOutputDeviceChannelTransfered: " + writeOutputDeviceChannelTransfered + "\n");
            cipherFileBuffer.clear();
        }

        readCipherFileChannelPosition = 0;
        readCipherFileChannelTransfered = 0;
        writeOutputDeviceChannelPosition = 0;                
        writeOutputDeviceChannelTransfered = 0;                
        inputEnded = false;

//      FILE STATUS        
        ui.log(" - Write: rd(" +  readCipherFileStat1.getFileBytesThroughPut() + ") -> ");
        ui.log("wr(" +           writeCipherFileStat1.getFileBytesThroughPut() + ") ");
        ui.log(" - Write: rd(" +  readCipherFileStat2.getFileBytesThroughPut() + ") -> ");
        ui.log("wr(" +           writeCipherFileStat2.getFileBytesThroughPut() + ") ");
        ui.log(allDataStats.getAllDataBytesProgressPercentage());

        allDataStats.addFilesProcessed(2);
        allDataStats.setAllDataEndNanoTime(); allDataStats.clock();

//        if ( stopPending ) { ui.status("\n", false); stopPending = false;  } // It breaks in the middle of encrypting, so the encryption summery needs to begin on a new line
        ui.status(allDataStats.getEndSummary(), true);

        updateProgressTimeline.stop();
        ui.encryptionFinished();
    }

    synchronized private long getCipherSize(Path cipherFilePath)
    {
        long cipherSize = 0;
        try { cipherSize = (int)Files.size(cipherFilePath); } catch (IOException ex) { ui.log("Files.size(finalCrypt.getCipherFilePath()) " + ex + "\n"); }
        return cipherSize;
    }

//  Get size of device        
    synchronized private static long getDeviceSize(Path rawDeviceFilePath)
    {
        long deviceSize = 0;
        try (final SeekableByteChannel deviceChannel = Files.newByteChannel(rawDeviceFilePath, EnumSet.of(StandardOpenOption.READ))) { deviceSize = deviceChannel.size(); deviceChannel.close(); }
        catch (IOException ex) { ui.status(ex.getMessage(), true); }
        
        return deviceSize;
    }

    synchronized private static long getLBAOffSet(long bytesPerSector, long devSize, long lba)
    {
        if ( lba >= 0 )
        {
            long returnValue = 0; returnValue = (lba * bytesPerSector);
//            guifx.log("LBA: " + logicalBlockAddress + " Pos: " + returnValue); guifx.log("\n");
            return returnValue;
        }
        else
        {
            long returnValue = 0; returnValue = ((devSize - 0) + (lba * bytesPerSector)); // -1 from size to 0 start position
//            guifx.log("LBA: " + logicalBlockAddress + " Pos: " + returnValue); guifx.log("\n");
            return returnValue;
        }
    }
    
    public static boolean getPausing()             { return pausing; }
    public static boolean getStopPending()         { return stopPending; }
    public static void setPausing(boolean val)     { pausing = val; }
    public static void setStopPending(boolean val) { stopPending = val; }
}
