/*
 * Copyright © 2017 Ron de Jong (ronuitzaandam@gmail.com).
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Timer;
import java.util.TimerTask;
//import javafx.animation.Animation;
//import javafx.animation.KeyFrame;
//import javafx.animation.Timeline;

public class Device
{
    int bufferSize = 1024 * 1024 * 1;
    long deviceSize = 0;
    long cipherSize = 0;
    static long bytesPerSector = 512;
    static UI ui;
    private static boolean pausing;
    private static boolean stopPending;
    private TimerTask updateProgressTask;
    private Timer updateProgressTaskTimer;
    
    public Device(UI ui)
    {
        this.ui = ui;
    }
    
//  Read byte[] from device
    synchronized public static byte[] readLBA(Path rawDeviceFilePath, long lba, long length)
    {        
        long readInputDeviceChannelTransfered = 0;
        ByteBuffer inputDeviceBuffer = ByteBuffer.allocate((int)length); inputDeviceBuffer.clear();
        try (final SeekableByteChannel readInputDeviceChannel = Files.newByteChannel(rawDeviceFilePath, EnumSet.of(StandardOpenOption.READ)))
        {
            readInputDeviceChannel.position(getLBAOffSet(bytesPerSector, getDeviceSize(rawDeviceFilePath), lba));
            readInputDeviceChannelTransfered = readInputDeviceChannel.read(inputDeviceBuffer); inputDeviceBuffer.flip();
            readInputDeviceChannel.close();
//            ui.log("Read LBA " + lba + " Transfered: " + readInputDeviceChannelTransfered + "\r\n");
        } catch (IOException ex) { ui.status("Device().read(..) " + ex.getMessage(), true); }
        return inputDeviceBuffer.array();
    }
    
//  Read Entry byte[] from device
    synchronized public static byte[] readPos(Path rawDeviceFilePath, long pos, long length)
    {        
        long readInputDeviceChannelTransfered = 0;
        ByteBuffer inputDeviceBuffer = ByteBuffer.allocate((int)length); inputDeviceBuffer.clear();
        try (final SeekableByteChannel readInputDeviceChannel = Files.newByteChannel(rawDeviceFilePath, EnumSet.of(StandardOpenOption.READ)))
        {
            readInputDeviceChannel.position(pos);
            readInputDeviceChannelTransfered = readInputDeviceChannel.read(inputDeviceBuffer); inputDeviceBuffer.flip();
            readInputDeviceChannel.close();
//            ui.log("Read Pos " + pos + " Transfered: " + readInputDeviceChannelTransfered + "\r\n");
        } catch (IOException ex) { ui.status("Device().read(..) " + ex.getMessage(), true); }
        return inputDeviceBuffer.array();
    }
    
//  Write byte[] to device
    synchronized public static void writeLBA(String desc, byte[] bytes, Path rawDeviceFilePath, long lba)
    {        
        long writeOutputDeviceChannelTransfered = 0;
        ByteBuffer outputDeviceBuffer = null;
        try (final SeekableByteChannel writeOutputDeviceChannel = Files.newByteChannel(rawDeviceFilePath, EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.SYNC)))
        {
            outputDeviceBuffer = ByteBuffer.allocate(bytes.length); outputDeviceBuffer.put(bytes); outputDeviceBuffer.flip(); // logBytes(outputDeviceBuffer.array());
//            guifx.log("\r\nBuffer: " + outputDeviceBuffer.capacity());
            writeOutputDeviceChannel.position(getLBAOffSet(bytesPerSector, getDeviceSize(rawDeviceFilePath), lba));
            writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer);
            ui.log("Wrote " + desc + " Pos (" + getLBAOffSet(bytesPerSector, getDeviceSize(rawDeviceFilePath), lba) + ") Transfered: " + writeOutputDeviceChannelTransfered + "\r\n");
            writeOutputDeviceChannel.close();
        } catch (IOException ex) { ui.status(Arrays.toString(ex.getStackTrace()), true); }
    }

//  Write Entry byte[] to device WARNING: writeOutputDeviceChannel.position(pos); causes exeption on OSX! Use writeLBA(..) above (from GPT_Entries)
    synchronized public static void writePos(String desc, byte[] bytes, Path rawDeviceFilePath, long pos)
    {        
        long writeOutputDeviceChannelTransfered = 0;
        ByteBuffer outputDeviceBuffer = null;
        try (final SeekableByteChannel writeOutputDeviceChannel = Files.newByteChannel(rawDeviceFilePath, EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.SYNC)))
        {
            outputDeviceBuffer = ByteBuffer.allocate(bytes.length); outputDeviceBuffer.put(bytes); outputDeviceBuffer.flip(); // logBytes(outputDeviceBuffer.array());
//            guifx.log("\r\nBuffer: " + outputDeviceBuffer.capacity());
            writeOutputDeviceChannel.position(pos);
            writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer);
            ui.log("Wrote " + desc + " Pos(" + pos + ") Transfered: " + writeOutputDeviceChannelTransfered + "\r\n");
            writeOutputDeviceChannel.close();
        } catch (IOException ex) { ui.status(Arrays.toString(ex.getStackTrace()), true); }
    }

//  Write CipherFile to partition
    synchronized public void writeCipherPartition(Path cipherFilePath, Path targetDeviceFilePath, long firstLBA, long lastLBA)
    {
	boolean encryptcipher = true;
        long deviceSize = getDeviceSize(targetDeviceFilePath);
        long cipherSize = getCipherFileSize(cipherFilePath);
        if ( cipherSize < bufferSize)   { bufferSize = (int)cipherSize; if (FinalCrypt.verbose) ui.log("BufferSize is limited to cipherfile size: " + GPT.getHumanSize(bufferSize, 1) + " \r\n"); }
//        else                            { log("BufferSize is set to: " + getHumanSize(bufferSize, 1) + " \r\n"); }
        Stats allDataStats = new Stats(); allDataStats.reset();        
        Stat readCipherFileStat1 = new Stat(); readCipherFileStat1.reset();
        Stat readCipherFileStat2 = new Stat(); readCipherFileStat2.reset();
        Stat writeCipherFileStat1 = new Stat(); writeCipherFileStat1.reset();
        Stat writeCipherFileStat2 = new Stat(); writeCipherFileStat2.reset();

        allDataStats.setFilesTotal(1);
        allDataStats.setFileBytesTotal      (getCipherFileSize(cipherFilePath) * 2);
        allDataStats.setAllDataBytesTotal   (getCipherFileSize(cipherFilePath) * 2);
        ui.status(allDataStats.getStartSummary(Mode.getDescription()), true);
        try { Thread.sleep(100); } catch (InterruptedException ex) {  }
        
        boolean inputEnded = false;
        long readCipherFileChannelPosition = 0;
        long readCipherFileChannelTransfered = 0;
        long writeOutputDeviceChannelPosition = 0;                
        long writeOutputDeviceChannelTransfered = 0;
        
//      Write the cipherfile to 1st partition
        ByteBuffer  cipherFileBuffer =      ByteBuffer.allocate(bufferSize); cipherFileBuffer.clear();
        byte[]      randomizedBytes =       new byte[bufferSize];
        ByteBuffer  randomizedBuffer =      ByteBuffer.allocate(bufferSize); cipherFileBuffer.clear();
        ByteBuffer  outputDeviceBuffer =    ByteBuffer.allocate(bufferSize); outputDeviceBuffer.clear();

//      Setup the Progress TIMER & TASK

        updateProgressTask = new TimerTask() { @Override public void run()
        {
            ui.encryptionProgress
            (
		(int) (
			(( readCipherFileStat1.getFileBytesProcessed() + writeCipherFileStat1.getFileBytesProcessed() + readCipherFileStat2.getFileBytesProcessed() + writeCipherFileStat2.getFileBytesProcessed() ) * 2)
			/   ( (allDataStats.getFileBytesTotal() * 3 ) / 100.0)),

		(int) (
			( allDataStats.getFilesBytesProcessed() * 2) / ( (allDataStats.getFilesBytesTotal() * 3) / 100.0)
		      )
            );
        }}; updateProgressTaskTimer = new java.util.Timer(); updateProgressTaskTimer.schedule(updateProgressTask, 0L, 200L);



//        Timeline updateProgressTimeline = new Timeline(new KeyFrame( Duration.millis(200), ae ->
//        ui.encryptionProgress
//        (
//                (int) (
//                        (
//                                readCipherFileStat1.getFileBytesProcessed() +
//                                writeCipherFileStat1.getFileBytesProcessed() +
//                                readCipherFileStat2.getFileBytesProcessed() +
//                                writeCipherFileStat2.getFileBytesProcessed()
//                        )   /   ( (allDataStats.getFileBytesTotal() * 1 ) / 100.0)),
//
//                (int) (
//                        (
//                                allDataStats.getFilesBytesProcessed() * 4) /
//                                ( (allDataStats.getFilesBytesTotal() * 4) / 100.0)
//                        )
//        )
//        )); updateProgressTimeline.setCycleCount(Animation.INDEFINITE); updateProgressTimeline.play();
        
        allDataStats.setAllDataStartNanoTime();

        ui.log("Writing " + cipherFilePath.toAbsolutePath() + " to partition 1 (LBA:"+ firstLBA + ":" + (getLBAOffSet(bytesPerSector, deviceSize, firstLBA) + writeOutputDeviceChannelPosition) + ")");
        write1loop: while ( ! inputEnded )
        {
            while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
            if (stopPending)    { inputEnded = true; break write1loop; }

            readCipherFileStat1.setFileStartEpoch();
            try (final SeekableByteChannel readCipherFileChannel = Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)))
            {
                // Fill up cipherFileBuffer
                readCipherFileChannel.position(readCipherFileChannelPosition);
                readCipherFileChannelTransfered = readCipherFileChannel.read(cipherFileBuffer); readCipherFileChannelPosition += readCipherFileChannelTransfered;
                if (( readCipherFileChannelTransfered < 1 ) || ( cipherFileBuffer.limit() < bufferSize )) { inputEnded = true; }
                cipherFileBuffer.flip();
                readCipherFileChannel.close(); readCipherFileStat1.setFileEndEpoch(); readCipherFileStat1.clock();
                readCipherFileStat1.addFileBytesProcessed(readCipherFileChannelTransfered); allDataStats.addAllDataBytesProcessed(readCipherFileChannelTransfered);
            } catch (IOException ex) { ui.log("Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex.getMessage() + "\r\n"); }
            
//          Randomize raw cipher or write raw cipher straight to partition
	    SecureRandom random = new SecureRandom();
	    if (encryptcipher)	{ random.nextBytes(randomizedBytes); randomizedBuffer.put(randomizedBytes); randomizedBuffer.flip();outputDeviceBuffer = encryptBuffer(cipherFileBuffer, randomizedBuffer); }
	    else		{ outputDeviceBuffer.put(cipherFileBuffer); outputDeviceBuffer.flip(); }
            
//          Write Device
            writeCipherFileStat1.setFileStartEpoch();
            try (final SeekableByteChannel writeOutputDeviceChannel = Files.newByteChannel(targetDeviceFilePath, EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.SYNC)))
            {
//              Write cipherfile to partition 1
                writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, deviceSize, firstLBA) + writeOutputDeviceChannelPosition));
                writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer); outputDeviceBuffer.rewind();
                writeCipherFileStat1.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed(readCipherFileChannelTransfered);
//                ui.log("\r\nwriteOutputDeviceChannelTransfered 1 : " + writeOutputDeviceChannelTransfered + "\r\n");

//              Write cipherfile to partition 2
                writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, deviceSize, lastLBA + 1) + writeOutputDeviceChannelPosition));
                writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer); outputDeviceBuffer.rewind();
                writeCipherFileStat1.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed(readCipherFileChannelTransfered);
//                ui.log("\r\nwriteOutputDeviceChannelTransfered 1 : " + writeOutputDeviceChannelTransfered + "\r\n");

                writeOutputDeviceChannelPosition += writeOutputDeviceChannelTransfered;

                if ( inputEnded )
                {
                    long partLength = ((lastLBA - firstLBA) + 1) * bytesPerSector; long gap = partLength - cipherSize;
		    outputDeviceBuffer = ByteBuffer.allocate((int)gap); outputDeviceBuffer.clear(); 
		    
//		    Randomize or zero out gab at end of partition
		    if (encryptcipher)    { randomizedBytes = new byte[(int)gap]; random.nextBytes(randomizedBytes);outputDeviceBuffer.put(randomizedBytes); outputDeviceBuffer.flip(); }
		    else	    { outputDeviceBuffer.put(GPT.getZeroBytes((int)gap)); outputDeviceBuffer.flip(); }
                    
//                  Fill in gap at end of partition 1
                    writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, deviceSize, firstLBA) + writeOutputDeviceChannelPosition));
                    writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer); outputDeviceBuffer.rewind();
                    writeCipherFileStat1.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed(readCipherFileChannelTransfered);
//                    ui.log("\r\nwriteOutputDeviceChannelTransfered 1 : " + writeOutputDeviceChannelTransfered + "\r\n");                

//                  Fill in gap at end of partition 2
                    writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, deviceSize, lastLBA + 1) + writeOutputDeviceChannelPosition));
                    writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer); outputDeviceBuffer.rewind();
                    writeCipherFileStat1.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed(readCipherFileChannelTransfered);
//                    ui.log("\r\nwriteOutputDeviceChannelTransfered 1 : " + writeOutputDeviceChannelTransfered + "\r\n");                
                }

                writeOutputDeviceChannel.close(); writeCipherFileStat1.setFileEndEpoch(); writeCipherFileStat1.clock();
            } catch (IOException ex) { ui.status(Arrays.toString(ex.getStackTrace()), true); }
            cipherFileBuffer.clear(); randomizedBuffer.clear(); outputDeviceBuffer.clear();
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


        allDataStats.addFilesProcessed(1);
        allDataStats.setAllDataEndNanoTime(); allDataStats.clock();

//        if ( stopPending ) { ui.status("\r\n", false); stopPending = false;  } // It breaks in the middle of encrypting, so the encryption summery needs to begin on a new line
        ui.status(allDataStats.getEndSummary(Mode.getDescription()), true);

        updateProgressTaskTimer.cancel(); updateProgressTaskTimer.purge();
//        updateProgressTimeline.stop();
        ui.encryptionFinished();
    }

//  Write CipherFile to partition 1 & 2
    synchronized public void cloneCipherPartition(Path cipherDeviceFilePath, Path targetDeviceFilePath, long firstLBA, long lastLBA)
    {
	if ( ( isValidFile(cipherDeviceFilePath, false, false, true) ) && ( isValidFile(targetDeviceFilePath, false, false, true) ) )
	{
	    long deviceSize = getDeviceSize(targetDeviceFilePath);
	    long cipherSize = getCipherPartitionSize(ui, cipherDeviceFilePath);
	    if ( cipherSize < bufferSize)   { bufferSize = (int)cipherSize; if (FinalCrypt.verbose) ui.log("BufferSize is limited to cipherfile size: " + GPT.getHumanSize(bufferSize, 1) + " \r\n"); }
//            else                            { log("BufferSize is set to: " + getHumanSize(bufferSize, 1) + " \r\n"); }
	    Stats allDataStats = new Stats(); allDataStats.reset();        
	    Stat readCipherFileStat1 = new Stat(); readCipherFileStat1.reset();
	    Stat readCipherFileStat2 = new Stat(); readCipherFileStat2.reset();
	    Stat writeCipherFileStat1 = new Stat(); writeCipherFileStat1.reset();
	    Stat writeCipherFileStat2 = new Stat(); writeCipherFileStat2.reset();

	    allDataStats.setFilesTotal(1);
	    allDataStats.setFileBytesTotal      (getCipherPartitionSize(ui, cipherDeviceFilePath) * 2);
	    allDataStats.setAllDataBytesTotal   (getCipherPartitionSize(ui, cipherDeviceFilePath) * 2);

	    ui.status(allDataStats.getStartSummary(Mode.getDescription()), true);
	    try { Thread.sleep(100); } catch (InterruptedException ex) {  }

	    boolean inputEnded = false;
	    long readCipherDeviceFileChannelPosition = 0;
	    long readCipherDeviceFileChannelTransfered = 0;
	    long readCipherDeviceFileChannelTransferedTotal = 0;
	    long writeOutputDeviceChannelPosition = 0;                
	    long writeOutputDeviceChannelTransfered = 0;

    //      Write the cipherPartitions to target partitions
	    ByteBuffer  cipherDeviceBuffer =      ByteBuffer.allocate(bufferSize); cipherDeviceBuffer.clear();
	    byte[]      randomizedBytes =       new byte[bufferSize];
	    ByteBuffer  outputDeviceBuffer =      ByteBuffer.allocate(bufferSize); outputDeviceBuffer.clear();

    //      Setup the Progress TIMER & TASK

	    updateProgressTask = new TimerTask() { @Override public void run()
	    {
		ui.encryptionProgress
		(
			(int) (
				(
					readCipherFileStat1.getFileBytesProcessed() +
					writeCipherFileStat1.getFileBytesProcessed() +
					readCipherFileStat2.getFileBytesProcessed() +
					writeCipherFileStat2.getFileBytesProcessed()
				)   /   ( (allDataStats.getFileBytesTotal() * 1 ) / 100.0)),

			(int) (
				( 
					allDataStats.getFilesBytesProcessed() * 1) /
					( (allDataStats.getFilesBytesTotal() * 1) / 100.0)
				)
		);
	    }}; updateProgressTaskTimer = new java.util.Timer(); updateProgressTaskTimer.schedule(updateProgressTask, 0L, 200L);


    //        Timeline updateProgressTimeline = new Timeline(new KeyFrame( Duration.millis(200), ae ->
    //        ui.encryptionProgress
    //        (
    //                (int) (
    //                        (
    //                                readCipherFileStat1.getFileBytesProcessed() +
    //                                writeCipherFileStat1.getFileBytesProcessed() +
    //                                readCipherFileStat2.getFileBytesProcessed() +
    //                                writeCipherFileStat2.getFileBytesProcessed()
    //                        )   /   ( (allDataStats.getFileBytesTotal() * 1 ) / 100.0)),
    //
    //                (int) (
    //                        ( 
    //                                allDataStats.getFilesBytesProcessed() * 4) /
    //                                ( (allDataStats.getFilesBytesTotal() * 4) / 100.0)
    //                        )
    //        )
    //        )); updateProgressTimeline.setCycleCount(Animation.INDEFINITE); updateProgressTimeline.play();

	    allDataStats.setAllDataStartNanoTime();

	    ui.log("Cloning " + cipherDeviceFilePath.toAbsolutePath() + " to " + targetDeviceFilePath.toAbsolutePath() + " partitions (LBA:"+ firstLBA + ":" + (getLBAOffSet(bytesPerSector, deviceSize, firstLBA) + writeOutputDeviceChannelPosition) + ")");

	    readCipherDeviceFileChannelPosition = Device.getLBAOffSet(bytesPerSector, deviceSize, firstLBA) + readCipherDeviceFileChannelPosition;
	    write1loop: while ( ! inputEnded )
	    {
		while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
		if (stopPending)    { inputEnded = true; break write1loop; }

		readCipherFileStat1.setFileStartEpoch();
		try (final SeekableByteChannel readCipherDeviceFileChannel = Files.newByteChannel(cipherDeviceFilePath, EnumSet.of(StandardOpenOption.READ)))
		{
		    // Fill up cipherDeviceBuffer
		    readCipherDeviceFileChannel.position(readCipherDeviceFileChannelPosition);
		    readCipherDeviceFileChannelTransfered = readCipherDeviceFileChannel.read(cipherDeviceBuffer); cipherDeviceBuffer.flip();
		    readCipherDeviceFileChannelTransferedTotal += readCipherDeviceFileChannelTransfered; readCipherDeviceFileChannelPosition += readCipherDeviceFileChannelTransfered;
		    if ( readCipherDeviceFileChannelTransferedTotal >= cipherSize ) { inputEnded = true; cipherDeviceBuffer.limit((int)readCipherDeviceFileChannelTransferedTotal - (int)cipherSize); }
		    readCipherDeviceFileChannel.close(); readCipherFileStat1.setFileEndEpoch(); readCipherFileStat1.clock();
		    readCipherFileStat1.addFileBytesProcessed(readCipherDeviceFileChannelTransfered); allDataStats.addAllDataBytesProcessed(readCipherDeviceFileChannelTransfered);
		} catch (IOException ex) { ui.log("Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex.getMessage() + "\r\n"); }

		// For sone reason cipherDeviceBuffer does not poor any data out into the writeOutputDeviceChannel, but does output data to GPT.logBytes
		outputDeviceBuffer.put(cipherDeviceBuffer.array()); outputDeviceBuffer.flip();

    //          Write Device
		writeCipherFileStat1.setFileStartEpoch();
		try (final SeekableByteChannel writeOutputDeviceChannel = Files.newByteChannel(targetDeviceFilePath, EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.SYNC)))
		{
    //              Write cipherfile to partition 1
		    writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, deviceSize, firstLBA) + writeOutputDeviceChannelPosition));
		    writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer); outputDeviceBuffer.rewind();
		    writeCipherFileStat1.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed(readCipherDeviceFileChannelTransfered);

		    writeOutputDeviceChannelPosition += writeOutputDeviceChannelTransfered;
		    writeOutputDeviceChannel.close(); writeCipherFileStat1.setFileEndEpoch(); writeCipherFileStat1.clock();
		} catch (IOException ex) { ui.status(Arrays.toString(ex.getStackTrace()), true); }
		cipherDeviceBuffer.clear();
	    }
	    readCipherDeviceFileChannelPosition = 0;
	    readCipherDeviceFileChannelTransfered = 0;
	    writeOutputDeviceChannelPosition = 0;                
	    writeOutputDeviceChannelTransfered = 0;                
	    inputEnded = false;

    //      FILE STATUS        
	    ui.log(" - Write: rd(" +  readCipherFileStat1.getFileBytesThroughPut() + ") -> ");
	    ui.log("wr(" +           writeCipherFileStat1.getFileBytesThroughPut() + ") ");
	    ui.log(" - Write: rd(" +  readCipherFileStat2.getFileBytesThroughPut() + ") -> ");
	    ui.log("wr(" +           writeCipherFileStat2.getFileBytesThroughPut() + ") ");
	    ui.log(allDataStats.getAllDataBytesProgressPercentage());


	    allDataStats.addFilesProcessed(1);
	    allDataStats.setAllDataEndNanoTime(); allDataStats.clock();

    //        if ( stopPending ) { ui.status("\r\n", false); stopPending = false;  } // It breaks in the middle of encrypting, so the encryption summery needs to begin on a new line
	    ui.status(allDataStats.getEndSummary(Mode.getDescription()), true);

	    updateProgressTaskTimer.cancel(); updateProgressTaskTimer.purge();
    //        updateProgressTimeline.stop();
	    ui.encryptionFinished();
	}
	else
	{
	    ui.error("Error: Cloning aborted.\r\n");
	}
    }

    private ByteBuffer encryptBuffer(ByteBuffer inputFileBuffer, ByteBuffer cipherFileBuffer)
    {
        int inputTotal = 0;
        int cipherTotal = 0;
        int outputDiff = 0;
        byte inputByte = 0;
        byte cipherByte = 0;
        byte outputByte;
        
        ByteBuffer outputFileBuffer =   ByteBuffer.allocate(bufferSize); outputFileBuffer.clear();
        while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
        for (int inputFileBufferCount = 0; inputFileBufferCount < inputFileBuffer.limit(); inputFileBufferCount++)
        {
            inputTotal += inputByte;
            cipherTotal += cipherByte;
            inputByte = inputFileBuffer.get(inputFileBufferCount);
            cipherByte = cipherFileBuffer.get(inputFileBufferCount);
            outputByte = encryptByte(inputFileBuffer.get(inputFileBufferCount), cipherFileBuffer.get(inputFileBufferCount));
            outputFileBuffer.put(outputByte);
        }
        outputFileBuffer.flip();
        // MD5Sum dataTotal XOR MD5Sum cipherTotal (Diff dataTot and cipherTot) 32 bit 4G
        
        outputDiff = inputTotal ^ cipherTotal;
        
//        if (debug)
//        {
//            ui.log(Integer.toString(inputTotal) + "\r\n");
//            ui.log(Integer.toString(cipherTotal) + "\r\n");
//            ui.log(Integer.toString(outputDiff) + "\r\n");
//        MD5Converter.getMD5SumFromString(Integer.toString(dataTotal));
//        MD5Converter.getMD5SumFromString(Integer.toString(cipherTotal));
//        }
        
        return outputFileBuffer;
    }
    
    private byte encryptByte(final byte dataByte, byte cipherByte)
    {
        int dum = 0;  // DUM Data Unnegated Mask
        int dnm = 0;  // DNM Data Negated Mask
        int dbm = 0;  // DBM Data Blended Mask
        byte outputByte;
        
//      Negate 0 cipherbytes to prevent 0 encryption
        if (cipherByte == 0) { cipherByte = (byte)(~cipherByte & 0xFF); }

        dum = dataByte & ~cipherByte;
        dnm = ~dataByte & cipherByte;
        dbm = dum + dnm; // outputByte        
        outputByte = (byte)(dbm & 0xFF);
        
        // Increment Byte Progress Counters        
        return (byte)dbm; // outputByte
    }

    synchronized private long getCipherFileSize(Path cipherFilePath)
    {
        long cipherSize = 0;
        try { cipherSize = (int)Files.size(cipherFilePath); } catch (IOException ex) { ui.log("Files.size(finalCrypt.getCipherFilePath()) " + ex.getMessage() + "\r\n"); }
        return cipherSize;
    }

    public static long getCipherPartitionSize(UI ui, Path cipherDeviceFilePath)
    {
        GPT gpt = new GPT(ui);
        gpt.read(cipherDeviceFilePath);
//        long partitionSize =    bytesPerSector + ((gpt.gpt_Entries.lastLBA1 - gpt.gpt_Entries.firstLBA1) * bytesPerSector);
        long partitionSize =    bytesPerSector + ((gpt.gpt_Entries1.gpt_entry[0].endingLBA - gpt.gpt_Entries1.gpt_entry[0].startingLBA) * bytesPerSector);
//        System.out.println("flba1: " + gpt.gpt_Entries.firstLBA1 + " llba1: "  + gpt.gpt_Entries.lastLBA1 + " ciphersize: " + partitionSize);
        return partitionSize;
    }

//  Get size of device        
    synchronized public static long getDeviceSize(Path rawDeviceFilePath)
    {
        long deviceSize = 0;
        try (final SeekableByteChannel deviceChannel = Files.newByteChannel(rawDeviceFilePath, EnumSet.of(StandardOpenOption.READ))) { deviceSize = deviceChannel.size(); deviceChannel.close(); }
        catch (IOException ex) { ui.status(ex.getMessage(), true); }
        
        return deviceSize;
    }

    synchronized public static long getLBAOffSet(long bytesPerSector, long devSize, long lba)
    {
        if ( lba >= 0 )
        {
            long returnValue = 0; returnValue = (lba * bytesPerSector);
//            guifx.log("LBA: " + logicalBlockAddress + " Pos: " + returnValue); guifx.log("\r\n");
            return returnValue;
        }
        else
        {
            long returnValue = 0; returnValue = ((devSize - 0) + (lba * bytesPerSector)); // -1 from size to 0 start position
//            guifx.log("LBA: " + logicalBlockAddress + " Pos: " + returnValue); guifx.log("\r\n");
            return returnValue;
        }
    }
    
//    synchronized public static long getPositiveLBAOffSet(long lba)
//    {
//            long returnValue = 0; returnValue = (lba * bytesPerSector);
////            guifx.log("LBA: " + logicalBlockAddress + " Pos: " + returnValue); guifx.log("\r\n");
//            return returnValue;
//    }
    
    public boolean isValidDir(Path path, boolean symlink, boolean report)
    {
        boolean validdir = true; String conditions = "";        String exist = ""; String read = ""; String write = ""; String symbolic = "";
        if ( ! Files.exists(path))                              { validdir = false; exist = "[not found] "; conditions += exist; }
        if ( ! Files.isReadable(path) )                         { validdir = false; read = "[not readable] "; conditions += read;  }
        if ( ! Files.isWritable(path) )                         { validdir = false; write = "[not writable] "; conditions += write;  }
        if ( (! symlink) && (Files.isSymbolicLink(path)) )      { validdir = false; symbolic = "[symlink]"; conditions += symbolic;  }
        if ( validdir ) {  } else { if ( report )               { ui.error("Warning: Invalid Dir: " + path.toString() + ": " + conditions + "\r\n"); } }
        return validdir;
    }

    public boolean isValidFile(Path path, boolean readSize, boolean symlink, boolean report)
    {
        boolean validfile = true; String conditions = "";       String size = ""; String exist = ""; String dir = ""; String read = ""; String write = ""; String symbolic = "";
        long fileSize = 0;					if ( readSize ) { try { fileSize = Files.size(path); } catch (IOException ex) { } }

        if ( ! Files.exists(path))                              { validfile = false; exist = "[not found] "; conditions += exist; }
        else
        {
            if ( Files.isDirectory(path))                       { validfile = false; dir = "[is directory] "; conditions += dir; }
            if ((readSize) && ( fileSize == 0 ))                { validfile = false; size = "[empty] "; conditions += size; }
            if ( ! Files.isReadable(path) )                     { validfile = false; read = "[not readable] "; conditions += read; }
            if ( ! Files.isWritable(path) )                     { validfile = false; write = "[not writable] "; conditions += write; }
            if ( (! symlink) && (Files.isSymbolicLink(path)) )  { validfile = false; symbolic = "[symlink]"; conditions += symbolic; }
        }
        if ( ! validfile ) { if ( report )			{ ui.error("Warning: Invalid File: " + path.toAbsolutePath().toString() + ": " + conditions + "\r\n"); } }                    
        return validfile;
    }

    public static boolean getPausing()             { return pausing; }
    public static boolean getStopPending()         { return stopPending; }
    public static void setPausing(boolean val)     { pausing = val; }
    public static void setStopPending(boolean val) { stopPending = val; }
}
