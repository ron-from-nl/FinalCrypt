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
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceController
{
    int bufferSize = 1024 * 1024 * 1;
    static long deviceSize = 0;
    long keySize = 0;
    static long bytesPerSector = 512;
    static UI ui;
    private static boolean pausing;
    private static boolean stopPending;
    private TimerTask updateProgressTask;
    private Timer updateProgressTaskTimer;
    
//  DoubleTest vars
    private static long lastpos = 0;
    private static long currpos = 1;
    private static long step = 1;
    private static long above = 0;
    private static long below = 1024;
    private static long cycles = 0;
    private static boolean finished = false;
    private Calendar	startCalendar;
    private long bytesTotal;
    private long bytesProcessed;
    private long filesBytesProcessed;
    private long bytesPerMilliSecond;
    private Calendar processProgressCalendar;

	public DeviceController(UI ui)
    {
        this.ui = ui;
    }
    
//  Read byte[] from device
    synchronized public static byte[] readLBA(FCPath fcPath, long lba, long length)
    {        
        long readInputDeviceChannelTransfered = 0;
        ByteBuffer inputDeviceBuffer = ByteBuffer.allocate((int)length); inputDeviceBuffer.clear();
        try (final SeekableByteChannel readInputDeviceChannel = Files.newByteChannel(fcPath.path, EnumSet.of(StandardOpenOption.READ)))
        {
            readInputDeviceChannel.position(getLBAOffSet(bytesPerSector, fcPath.size, lba));
            readInputDeviceChannelTransfered = readInputDeviceChannel.read(inputDeviceBuffer); inputDeviceBuffer.flip();
            readInputDeviceChannel.close();
//            ui.log("Read LBA " + lba + " Transfered: " + readInputDeviceChannelTransfered + "\r\n");
        } catch (IOException ex) { ui.log("Device().read(..) " + ex.getMessage(), true, true, true, true, false); }
        return inputDeviceBuffer.array();
    }
    
    synchronized public static byte[] readPos(FCPath fcPath, long pos, long length)
    {        
        long readInputDeviceChannelTransfered = 0;
        ByteBuffer inputDeviceBuffer = ByteBuffer.allocate((int)length); inputDeviceBuffer.clear();
        try (final SeekableByteChannel readInputDeviceChannel = Files.newByteChannel(fcPath.path, EnumSet.of(StandardOpenOption.READ)))
        {
            readInputDeviceChannel.position(pos);
            readInputDeviceChannelTransfered = readInputDeviceChannel.read(inputDeviceBuffer); inputDeviceBuffer.flip();
            readInputDeviceChannel.close();
//            ui.log("Read Pos " + pos + " Transfered: " + readInputDeviceChannelTransfered + "\r\n");
        } catch (IOException ex) { ui.log("Device().read(..) " + ex.getMessage(), true, true, true, true, false); }
        return inputDeviceBuffer.array();
    }
    
//  Write byte[] to device
    synchronized public static void writeLBA(String desc, byte[] bytes, FCPath fcPath, long lba)
    {        
        long writeOutputDeviceChannelTransfered = 0;
        ByteBuffer outputDeviceBuffer = null;
        ui.log("Write " + desc + " Pos (" + getLBAOffSet(bytesPerSector, fcPath.size, lba) + ") ", true, true, true, false, false);
        try (final SeekableByteChannel writeOutputDeviceChannel = Files.newByteChannel(fcPath.path, EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.SYNC)))
        {
            outputDeviceBuffer = ByteBuffer.allocate(bytes.length); outputDeviceBuffer.put(bytes); outputDeviceBuffer.flip(); // logBytes(outputDeviceBuffer.array());
//            guifx.log("Buffer: " + outputDeviceBuffer.capacity());
            writeOutputDeviceChannel.position(getLBAOffSet(bytesPerSector, fcPath.size, lba));
            writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer);
            ui.log("Transfered: " + writeOutputDeviceChannelTransfered + "\r\n", true, true, true, false, false);
            writeOutputDeviceChannel.close();
        } catch (IOException ex) { ui.log("Error: Device.writeLBA(..): " + ex.getMessage() + "", true, true, true, true, false); }
    }

//  Write Entry byte[] to device WARNING: writeOutputDeviceChannel.position(pos); causes exeption on OSX! Use writeLBA(..) above (from GPT_Entries) (hmm not anymore maybe)

    synchronized public static void writePos(String desc, byte[] bytes, FCPath device, long pos)
    {        
        long writeOutputDeviceChannelTransfered = 0;
        ByteBuffer outputDeviceBuffer = null;
        ui.log("Wrote " + desc + " Pos(" + pos + ") ", true, true, true, false, false);
        try (final SeekableByteChannel writeOutputDeviceChannel = Files.newByteChannel(device.path, EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.SYNC)))
        {
            outputDeviceBuffer = ByteBuffer.allocate(bytes.length); outputDeviceBuffer.put(bytes); outputDeviceBuffer.flip(); // logBytes(outputDeviceBuffer.array());
//            guifx.log("Buffer: " + outputDeviceBuffer.capacity());
            writeOutputDeviceChannel.position(pos);
            writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer);
            ui.log("Transfered: " + writeOutputDeviceChannelTransfered + "", true, true, true, false, false);
            writeOutputDeviceChannel.close();
        } catch (IOException ex) { ui.log("Error: Device.writePos(..): " + ex.getMessage() + "\r\n", true, true, true, true, false); }
    }

//  Write KeyFile to partition
    synchronized public void writeKeyPartition(FCPath keyFCPath, FCPath targetFCPath, long firstLBA, long lastLBA)
    {
	startCalendar = Calendar.getInstance(Locale.ROOT);
	boolean encryptkey = true;
        if ( keyFCPath.size < bufferSize)   { bufferSize = (int)keyFCPath.size; if (FinalCrypt.verbose) ui.log("BufferSize is limited to keyfile size: " + GPT.getHumanSize(bufferSize, 1) + " \r\n", true, true, true, false, false); }
//        else                            { log("BufferSize is set to: " + getHumanSize(bufferSize, 1) + " \r\n"); }
        Stats allDataStats = new Stats(); allDataStats.reset();        
        Stat readKeyFileStat1 = new Stat(); readKeyFileStat1.reset();
        Stat readKeyFileStat2 = new Stat(); readKeyFileStat2.reset();
        Stat writeKeyFileStat1 = new Stat(); writeKeyFileStat1.reset();
        Stat writeKeyFileStat2 = new Stat(); writeKeyFileStat2.reset();

        allDataStats.setFilesTotal(1);
        allDataStats.setFileBytesTotal      (keyFCPath.size * 2);
        allDataStats.setAllDataBytesTotal   (keyFCPath.size * 2);
        ui.log(allDataStats.getStartSummary("Creating Key Device"), true, true, false, false, false);
        try { Thread.sleep(100); } catch (InterruptedException ex) {  }
        
        boolean inputEnded = false;
        long readKeyFileChannelPosition = 0;
        long readKeyFileChannelTransfered = 0;
        long writeOutputDeviceChannelPosition = 0;                
        long writeOutputDeviceChannelTransfered = 0;
        
//      Write the keyfile to 1st partition
        ByteBuffer  keyFileBuffer =      ByteBuffer.allocate(bufferSize); keyFileBuffer.clear();
        byte[]      randomizedBytes =       new byte[bufferSize];
        ByteBuffer  randomizedBuffer =      ByteBuffer.allocate(bufferSize); keyFileBuffer.clear();
        ByteBuffer  outputDeviceBuffer =    ByteBuffer.allocate(bufferSize); outputDeviceBuffer.clear();

//      Setup the Progress TIMER & TASK

        updateProgressTask = new TimerTask()
	{
	    @Override public void run()
	    {
		processProgressCalendar = Calendar.getInstance(Locale.ROOT);
		bytesTotal =	    allDataStats.getFilesBytesTotal();
		bytesProcessed =	    allDataStats.getFilesBytesProcessed();
		bytesPerMilliSecond =   filesBytesProcessed / (processProgressCalendar.getTimeInMillis() - startCalendar.getTimeInMillis());
		ui.processProgress
		(
		    (int) (
			    (( readKeyFileStat1.getFileBytesProcessed() + writeKeyFileStat1.getFileBytesProcessed() + readKeyFileStat2.getFileBytesProcessed() + writeKeyFileStat2.getFileBytesProcessed() ) * 2)
			    /   ( (allDataStats.getFileBytesTotal() * 3 ) / 100.0)),

		    (int) (
			    ( allDataStats.getFilesBytesProcessed() * 2) / ( (allDataStats.getFilesBytesTotal() * 3) / 100.0)
			  ), bytesTotal, bytesProcessed, bytesPerMilliSecond // long bytesPerMiliSecond
		);
	    }
	}; updateProgressTaskTimer = new java.util.Timer(); updateProgressTaskTimer.schedule(updateProgressTask, 0L, 200L);

        allDataStats.setAllDataStartNanoTime();

        ui.log("Writing " + keyFCPath.path.toAbsolutePath() + " to partition 1 (LBA:"+ firstLBA + ":" + (getLBAOffSet(bytesPerSector, targetFCPath.size, firstLBA) + writeOutputDeviceChannelPosition) + ")", true, true, true, false, false);
        write1loop: while ( ! inputEnded )
        {
            while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
            if (stopPending)    { inputEnded = true; break write1loop; }

            readKeyFileStat1.setFileStartEpoch();
            try (final SeekableByteChannel readKeyFileChannel = Files.newByteChannel(keyFCPath.path, EnumSet.of(StandardOpenOption.READ)))
            {
                // Fill up keyFileBuffer
                readKeyFileChannel.position(readKeyFileChannelPosition);
                readKeyFileChannelTransfered = readKeyFileChannel.read(keyFileBuffer); readKeyFileChannelPosition += readKeyFileChannelTransfered;
                if (( readKeyFileChannelTransfered < 1 ) || ( keyFileBuffer.limit() < bufferSize )) { inputEnded = true; }
                keyFileBuffer.flip();
                readKeyFileChannel.close(); readKeyFileStat1.setFileEndEpoch(); readKeyFileStat1.clock();
                readKeyFileStat1.addFileBytesProcessed(readKeyFileChannelTransfered); allDataStats.addAllDataBytesProcessed("", readKeyFileChannelTransfered);
            } catch (IOException ex) { ui.log("Files.newByteChannel(keyFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex.getMessage() + "\r\n", true, true, true, true, false); }
            
//          Randomize raw key or write raw key straight to partition
	    SecureRandom random = new SecureRandom();
//	    if (encryptkey)	{ random.nextBytes(randomizedBytes); randomizedBuffer.put(randomizedBytes); randomizedBuffer.flip();outputDeviceBuffer = encryptBuffer(keyFileBuffer, randomizedBuffer); }
	    if (encryptkey)	{ random.nextBytes(randomizedBytes); randomizedBuffer.put(randomizedBytes); randomizedBuffer.flip();outputDeviceBuffer = FinalCrypt.encryptBuffer(keyFileBuffer, randomizedBuffer, false); }
	    else		{ outputDeviceBuffer.put(keyFileBuffer); outputDeviceBuffer.flip(); }
            
//          Write Device
            writeKeyFileStat1.setFileStartEpoch();
            try (final SeekableByteChannel writeOutputDeviceChannel = Files.newByteChannel(targetFCPath.path, EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.SYNC)))
            {
//              Write keyfile to partition 1
                writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, targetFCPath.size, firstLBA) + writeOutputDeviceChannelPosition));
                writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer); outputDeviceBuffer.rewind();
                writeKeyFileStat1.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed("", readKeyFileChannelTransfered);
//                ui.log("writeOutputDeviceChannelTransfered 1 : " + writeOutputDeviceChannelTransfered + "\r\n");

//              Write keyfile to partition 2
                writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, targetFCPath.size, lastLBA + 1) + writeOutputDeviceChannelPosition));
                writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer); outputDeviceBuffer.rewind();
                writeKeyFileStat1.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed("", readKeyFileChannelTransfered);
//                ui.log("writeOutputDeviceChannelTransfered 1 : " + writeOutputDeviceChannelTransfered + "\r\n");

                writeOutputDeviceChannelPosition += writeOutputDeviceChannelTransfered;

                if ( inputEnded )
                {
                    long partLength = ((lastLBA - firstLBA) + 1) * bytesPerSector; long gap = partLength - keyFCPath.size;
		    outputDeviceBuffer = ByteBuffer.allocate((int)gap); outputDeviceBuffer.clear(); 
		    
//		    Randomize or zero out gab at end of partition
		    if (encryptkey)    { randomizedBytes = new byte[(int)gap]; random.nextBytes(randomizedBytes);outputDeviceBuffer.put(randomizedBytes); outputDeviceBuffer.flip(); }
		    else	    { outputDeviceBuffer.put(GPT.getZeroBytes((int)gap)); outputDeviceBuffer.flip(); }
                    
//                  Fill in gap at end of partition 1
                    writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, targetFCPath.size, firstLBA) + writeOutputDeviceChannelPosition));
                    writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer); outputDeviceBuffer.rewind();
                    writeKeyFileStat1.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed("", readKeyFileChannelTransfered);
//                    ui.log("writeOutputDeviceChannelTransfered 1 : " + writeOutputDeviceChannelTransfered + "\r\n");                

//                  Fill in gap at end of partition 2
                    writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, targetFCPath.size, lastLBA + 1) + writeOutputDeviceChannelPosition));
                    writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer); outputDeviceBuffer.rewind();
                    writeKeyFileStat1.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed("", readKeyFileChannelTransfered);
//                    ui.log("writeOutputDeviceChannelTransfered 1 : " + writeOutputDeviceChannelTransfered + "\r\n");                
                }

                writeOutputDeviceChannel.close(); writeKeyFileStat1.setFileEndEpoch(); writeKeyFileStat1.clock();
            } catch (IOException ex) { ui.log(Arrays.toString(ex.getStackTrace()), true, true, false, false, false); }
            keyFileBuffer.clear(); randomizedBuffer.clear(); outputDeviceBuffer.clear();
        }
        readKeyFileChannelPosition = 0;
        readKeyFileChannelTransfered = 0;
        writeOutputDeviceChannelPosition = 0;                
        writeOutputDeviceChannelTransfered = 0;                
        inputEnded = false;

//      FILE STATUS        
        ui.log(" - Write: rd(" +  readKeyFileStat1.getFileBytesThroughPut() + ") -> ", true, true, true, false, false);
        ui.log("wr(" +           writeKeyFileStat1.getFileBytesThroughPut() + ") ", true, true, true, false, false);
        ui.log(" - Write: rd(" +  readKeyFileStat2.getFileBytesThroughPut() + ") -> ", true, true, true, false, false);
        ui.log("wr(" +           writeKeyFileStat2.getFileBytesThroughPut() + ") ", true, true, true, false, false);
        ui.log(allDataStats.getAllDataBytesProgressPercentage() + "\r\n", true, true, true, false, false);


        allDataStats.addFilesProcessed(1);
        allDataStats.setAllDataEndNanoTime(); allDataStats.clock();

//        if ( stopPending ) { ui.status("\r\n", false); stopPending = false;  } // It breaks in the middle of encrypting, so the encryption summery needs to begin on a new line
        ui.log(allDataStats.getEndSummary("creating key device"), true, true, false, false, false);

        updateProgressTaskTimer.cancel(); updateProgressTaskTimer.purge();
//        updateProgressTimeline.stop();
        ui.processFinished();
    }

//  Write KeyFile to partition 1 & 2
//    synchronized public void cloneKeyPartition(Path keyDeviceFilePath, Path targetDeviceFilePath, long firstLBA, long lastLBA)
//    synchronized public void cloneKeyPartition(Device keyDevice, Device targetDevice, long firstLBA, long lastLBA)
    synchronized public void cloneKeyPartition(FCPath keyFCPath, FCPath targetFCPath, long firstLBA, long lastLBA)
    {
	startCalendar = Calendar.getInstance(Locale.ROOT);
//	       isValidFile(UI ui, Path path,      boolean readSize,     boolean isKey, boolean symlink, boolean report)
	if ( ( isValidFile(   ui,keyFCPath.path,          false,keyFCPath.isKey,           false,           true) ) && ( isValidFile(ui, targetFCPath.path, targetFCPath.isKey, false, false, true) ) )
	{
	    long targetDeviceSize2 = targetFCPath.size;
	    long keyPartitionSize = getKeyPartitionSize(ui, keyFCPath);
	    if ( keyPartitionSize < bufferSize)   { bufferSize = (int)keyPartitionSize; if (FinalCrypt.verbose) ui.log("BufferSize is limited to keyfile size: " + GPT.getHumanSize(bufferSize, 1) + " \r\n", true, true, true, false, false); }
//	    long keySize = getKeyPartitionSize(ui, keyDevice);
//	    if ( keyFCPath.size < bufferSize)   { bufferSize = (int)keySize; if (FinalCrypt.verbose) ui.log("BufferSize is limited to keyfile size: " + GPT.getHumanSize(bufferSize, 1) + " \r\n"); }
	    if ( keyPartitionSize < bufferSize)   { bufferSize = (int)keyPartitionSize; if (FinalCrypt.verbose) ui.log("BufferSize is limited to keyfile size: " + GPT.getHumanSize(bufferSize, 1) + " \r\n", true, true, true, false, false); }
//            else                            { log("BufferSize is set to: " + getHumanSize(bufferSize, 1) + " \r\n"); }
	    Stats allDataStats = new Stats(); allDataStats.reset();        
	    Stat readKeyFileStat1 = new Stat(); readKeyFileStat1.reset();
	    Stat readKeyFileStat2 = new Stat(); readKeyFileStat2.reset();
	    Stat writeKeyFileStat1 = new Stat(); writeKeyFileStat1.reset();
	    Stat writeKeyFileStat2 = new Stat(); writeKeyFileStat2.reset();

	    allDataStats.setFilesTotal(1);
//	    allDataStats.setFileBytesTotal      (getKeyPartitionSize(ui, keyDevice) * 2);
//	    allDataStats.setAllDataBytesTotal   (getKeyPartitionSize(ui, keyDevice) * 2);
//	    allDataStats.setFileBytesTotal      (keyFCPath.size * 2);
//	    allDataStats.setAllDataBytesTotal   (keyFCPath.size * 2);
	    allDataStats.setFileBytesTotal      (keyPartitionSize * 2);
	    allDataStats.setAllDataBytesTotal   (keyPartitionSize * 2);

	    ui.log(allDataStats.getStartSummary("Cloning Key Device"), true, true, false, false, false);
	    try { Thread.sleep(100); } catch (InterruptedException ex) {  }

	    boolean inputEnded = false;
	    long readKeyDeviceFileChannelPosition = 0;
	    long readKeyDeviceFileChannelTransfered = 0;
	    long readKeyDeviceFileChannelTransferedTotal = 0;
	    long writeOutputDeviceChannelPosition = 0;                
	    long writeOutputDeviceChannelTransfered = 0;

    //      Write the keyPartitions to target partitions
	    ByteBuffer  keyDeviceBuffer =      ByteBuffer.allocate(bufferSize); keyDeviceBuffer.clear();
	    byte[]      randomizedBytes =       new byte[bufferSize];
	    ByteBuffer  outputDeviceBuffer =      ByteBuffer.allocate(bufferSize); outputDeviceBuffer.clear();

    //      Setup the Progress TIMER & TASK

	    processProgressCalendar = Calendar.getInstance(Locale.ROOT);
	    bytesTotal =	    allDataStats.getFilesBytesTotal();
	    bytesProcessed =	    allDataStats.getFilesBytesProcessed();
	    bytesPerMilliSecond =   filesBytesProcessed / (processProgressCalendar.getTimeInMillis() - startCalendar.getTimeInMillis());
	    updateProgressTask = new TimerTask() { @Override public void run()
	    {
		ui.processProgress
		(
			(int) (
				(
					readKeyFileStat1.getFileBytesProcessed() +
					writeKeyFileStat1.getFileBytesProcessed() +
					readKeyFileStat2.getFileBytesProcessed() +
					writeKeyFileStat2.getFileBytesProcessed()
				)   /   ( (allDataStats.getFileBytesTotal() * 1 ) / 100.0)),

			(int) (
				( 
					allDataStats.getFilesBytesProcessed() * 1) /
					( (allDataStats.getFilesBytesTotal() * 1) / 100.0)
				), bytesTotal, bytesProcessed, bytesPerMilliSecond // long bytesPerMiliSecond
		);
	    }}; updateProgressTaskTimer = new java.util.Timer(); updateProgressTaskTimer.schedule(updateProgressTask, 0L, 200L);

	    allDataStats.setAllDataStartNanoTime();

	    ui.log("Cloning " + keyFCPath.path.toAbsolutePath() + " to " + targetFCPath.path.toAbsolutePath() + " partitions (LBA:"+ firstLBA + ":" + (getLBAOffSet(bytesPerSector, targetFCPath.size, firstLBA) + writeOutputDeviceChannelPosition) + ")", true, true, true, false, false);

	    readKeyDeviceFileChannelPosition = DeviceController.getLBAOffSet(bytesPerSector, targetFCPath.size, firstLBA) + readKeyDeviceFileChannelPosition;
	    write1loop: while ( ! inputEnded )
	    {
		while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
		if (stopPending)    { inputEnded = true; break write1loop; }

		readKeyFileStat1.setFileStartEpoch();
		try (final SeekableByteChannel readKeyDeviceFileChannel = Files.newByteChannel(keyFCPath.path, EnumSet.of(StandardOpenOption.READ)))
		{
		    // Fill up keyDeviceBuffer
		    readKeyDeviceFileChannel.position(readKeyDeviceFileChannelPosition);
		    readKeyDeviceFileChannelTransfered = readKeyDeviceFileChannel.read(keyDeviceBuffer); keyDeviceBuffer.flip();
		    readKeyDeviceFileChannelTransferedTotal += readKeyDeviceFileChannelTransfered; readKeyDeviceFileChannelPosition += readKeyDeviceFileChannelTransfered;
		    if ( readKeyDeviceFileChannelTransferedTotal >= keyPartitionSize ) { inputEnded = true; keyDeviceBuffer.limit((int)readKeyDeviceFileChannelTransferedTotal - (int)keyPartitionSize); }
		    readKeyDeviceFileChannel.close(); readKeyFileStat1.setFileEndEpoch(); readKeyFileStat1.clock();
		    readKeyFileStat1.addFileBytesProcessed(readKeyDeviceFileChannelTransfered); allDataStats.addAllDataBytesProcessed("", readKeyDeviceFileChannelTransfered);
		} catch (IOException ex) { ui.log("Error: Files.newByteChannel(keyFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex.getMessage() + "\r\n", true, true, true, true, false); }

		// For sone reason keyDeviceBuffer does not poor any data out into the writeOutputDeviceChannel, but does output data to GPT.logBytes
		outputDeviceBuffer.put(keyDeviceBuffer.array()); outputDeviceBuffer.flip();

    //          Write Device
		writeKeyFileStat1.setFileStartEpoch();
		try (final SeekableByteChannel writeOutputDeviceChannel = Files.newByteChannel(targetFCPath.path, EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.SYNC)))
		{
    //              Write keyfile to partition 1
		    writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, targetFCPath.size, firstLBA) + writeOutputDeviceChannelPosition));
		    writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer); outputDeviceBuffer.rewind();
		    writeKeyFileStat1.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed("", readKeyDeviceFileChannelTransfered);

		    writeOutputDeviceChannelPosition += writeOutputDeviceChannelTransfered;
		    writeOutputDeviceChannel.close(); writeKeyFileStat1.setFileEndEpoch(); writeKeyFileStat1.clock();
		} catch (IOException ex) { ui.log("Error: Files.newByteChannel(targetFCPath.path, EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.SYNC))" + ex.getMessage() + "\r\n", true, true, true, true, false); }
		keyDeviceBuffer.clear();
	    }
	    readKeyDeviceFileChannelPosition = 0;
	    readKeyDeviceFileChannelTransfered = 0;
	    writeOutputDeviceChannelPosition = 0;                
	    writeOutputDeviceChannelTransfered = 0;                
	    inputEnded = false;

    //      FILE STATUS        
	    ui.log(" - Write: rd(" +  readKeyFileStat1.getFileBytesThroughPut() + ") -> ", true, true, true, false, false);
	    ui.log("wr(" +           writeKeyFileStat1.getFileBytesThroughPut() + ") ", true, true, true, false, false);
	    ui.log(" - Write: rd(" +  readKeyFileStat2.getFileBytesThroughPut() + ") -> ", true, true, true, false, false);
	    ui.log("wr(" +           writeKeyFileStat2.getFileBytesThroughPut() + ") ", true, true, true, false, false);
	    ui.log(allDataStats.getAllDataBytesProgressPercentage(), true, true, true, false, false);


	    allDataStats.addFilesProcessed(1);
	    allDataStats.setAllDataEndNanoTime(); allDataStats.clock();

    //        if ( stopPending ) { ui.status("\r\n", false); stopPending = false;  } // It breaks in the middle of encrypting, so the encryption summery needs to begin on a new line
	    ui.log(allDataStats.getEndSummary("cloning key device"), true, true, false, false, false);

	    updateProgressTaskTimer.cancel(); updateProgressTaskTimer.purge();
    //        updateProgressTimeline.stop();
	    ui.processFinished();
	}
	else
	{
	    ui.log("Warning: Invalid key or target. Cloning aborted.\r\n", true, true, true, true, false);
	}
    }

//    public static long getKeyPartitionSize(UI ui, Path keyDeviceFilePath)
    public static long getKeyPartitionSize(UI ui, FCPath keyFCPath)
    {
        GPT gpt = new GPT(ui);
        gpt.read(keyFCPath);
        long partitionSize =    bytesPerSector + ((gpt.gpt_Entries1.gpt_entry[0].endingLBA - gpt.gpt_Entries1.gpt_entry[0].startingLBA) * bytesPerSector);
//        System.out.println("flba1: " + gpt.gpt_Entries.firstLBA1 + " llba1: "  + gpt.gpt_Entries.lastLBA1 + " keysize: " + partitionSize);
        return partitionSize;
    }

//  Wrapper method
    synchronized public static long getDeviceSize(UI ui, Path path, boolean isKey)
    {
	long size = getDeviceSize2(ui, path, isKey, true); return size; // Customized method (platform independent)
    }

//  Get size of device NOT USED!
    synchronized public static long getDeviceSize1(UI ui, Path path)
    {
        long deviceSize = 0;
        try (final SeekableByteChannel deviceChannel = Files.newByteChannel(path, EnumSet.of(StandardOpenOption.READ))) { deviceSize = deviceChannel.size(); deviceChannel.close(); } catch (IOException ex) { ui.log(ex.getMessage(), true, true, false, false, false); }
        return deviceSize;
    }

    synchronized public static long getDeviceSize2(UI ui, Path path, boolean isKey, boolean firstcall) // OS Independent half or dubbel guess size test (Files.size(..) doesn't work on Apple OSX)
    {
	boolean verbose = false;
//	deviceSize = 0;
	
	if (firstcall)
	{
			lastpos = 0;
			currpos = 1;
			step = 1;
			above = 0;
			below = 1024;
			cycles = 0;
			finished = false;
	}
//	    isValidFile(UI ui, Path path, boolean readSize, boolean isKey, boolean symlink, boolean report)
	if (isValidFile(   ui,      path,            false,         isKey,           false,           true ))
	{
	    while (! finished)
	    {
		try
		{
		    deviceSize = guessDeviceSize(ui, path, verbose);
		}
		catch (IOException ex)
		{
		}
		finally
		{
		    lastpos = Math.abs(lastpos);
		    currpos = Math.abs(currpos);
		    step = Math.abs(step);

		    below = currpos;
		    if (step < 0) {step = 1;}
		    currpos = above; step = 1;
		    lastpos = currpos;
//		    getDeviceSize2(ui, path, isKey, true)
		    getDeviceSize2(ui,path, isKey, false);
		}
	    }
	}
	else
	{
	    return deviceSize; 
	}
	return deviceSize;
    }
    
    synchronized private static long guessDeviceSize(UI ui, Path path, boolean verbose) throws IOException
    {
        if (verbose) ui.log(String.format("%-20s %-20s %-20s %-20s %-20s %-20s \r\n", "LastPoss     ", "CurrPoss     ", "Step    ", "Above     ", "Below    ", "Cycles     "), true, true, true, false, false);
        
        label: while (! finished)
        {
            if (verbose) ui.log(String.format("%-20d %-20d %-20d %-20d %-20d %-20d \r\n", lastpos, currpos, step, above, below, cycles), true, true, true, false, false);
            
            final SeekableByteChannel deviceChannel = Files.newByteChannel(path, EnumSet.of(StandardOpenOption.READ));
            deviceChannel.position(currpos);
            ByteBuffer bb = ByteBuffer.allocate(1); bb.clear();
            int transfered = 0; transfered = deviceChannel.read(bb);
            if ( transfered < 1)
            {
                if ( (lastpos == below ) && (currpos == below ) && (above == (below - 1) ) ) { finished = true; }
                below = currpos; currpos -= (step / 2); step = 1;
            }
            else        { above = currpos; currpos += step; step += step;}
            deviceChannel.close();
            lastpos = currpos;
            cycles++;
        } 
        return below;
    }    

    private void halveTest(UI ui)
    {
        long deviceSize = 0;
        long lastpos = 0;
        long currpos = 1;
        long step = 1;
        long above = 0;
        long below = 1024;
        long target = (1024L * 1024L * 1024L * 512L - 1L);
        long cycles = 0;
        boolean finished = false;
        boolean iofailed = false;

        iofailed = false;        
        ui.log("Target: " + target + "\r\n", true, true, true, false, false);
        ui.log(String.format("%-20s %-20s %-20s %-20s %-20s %-20s \r\n", "LastPoss     ", "CurrPoss     ", "Step    ", "Above     ", "Below    ", "Cycles     "), true, true, true, false, false);
        
        label: while (! finished)
        {
            ui.log(String.format("%-20d %-20d %-20d %-20d %-20d %-20d \r\n", lastpos, currpos, step, above, below, cycles), true, true, true, false, false);
            if      (currpos < target) { above = currpos; currpos += step; step += step; }
            else if (currpos > target) { below = currpos; currpos -= (step / 2); step = 1; }
            if ((currpos != 0) && (currpos == lastpos)) {finished = true;}
            lastpos = currpos;
            cycles++;
        } 
    }
    
    synchronized public static long getLBAOffSet(long bytesPerSector, long devSize, long lba)
    {
        if ( lba >= 0 )
        {
            long returnValue = 0; returnValue = Math.abs(lba * bytesPerSector);
//            guifx.log("LBA: " + logicalBlockAddress + " Pos: " + returnValue); guifx.log("\r\n");
            return returnValue;
        }
        else
        {
            long returnValue = 0; returnValue = Math.abs((devSize - 0) + (lba * bytesPerSector)); // -1 from size to 0 start position
//            guifx.log("LBA: " + logicalBlockAddress + " Pos: " + returnValue); guifx.log("\r\n");
            return returnValue;
        }
    }
    
    public static boolean isValidDir(UI ui, Path path, boolean symlink, boolean report)
    {
        boolean validdir = true; String conditions = "";        String exist = ""; String read = ""; String write = ""; String symbolic = "";
        if ( ! Files.exists(path))                              { validdir = false; exist = "[not found] "; conditions += exist; }
        if ( ! Files.isReadable(path) )                         { validdir = false; read = "[not readable] "; conditions += read;  }
        if ( ! Files.isWritable(path) )                         { validdir = false; write = "[not writable] "; conditions += write;  }
        if ( (! symlink) && (Files.isSymbolicLink(path)) )      { validdir = false; symbolic = "[symlink]"; conditions += symbolic;  }
        if ( validdir ) {  } else { if ( report )               { ui.log("Warning: Invalid Dir: " + path.toString() + ": " + conditions + "\r\n", true, true, true, true, false); } }
        return validdir;
    }

    public static boolean isValidFile(UI ui, Path path, boolean readSize, boolean isKey, boolean symlink, boolean report)
    {
        boolean validfile = true; String conditions = "";       String size = ""; String exist = ""; String dir = ""; String read = ""; String write = ""; String symbolic = "";
        long fileSize = 0;					if ( readSize ) { try { fileSize = Files.size(path); } catch (IOException ex) { } }

        if ( ! Files.exists(path))                              { validfile = false; exist = "[not found] "; conditions += exist; }
        else
        {
            if ( Files.isDirectory(path))                       { validfile = false; dir = "[is directory] "; conditions += dir; }
            if ((readSize) && ( fileSize == 0 ))                { validfile = false; size = "[empty] "; conditions += size; }
            if ( ! Files.isReadable(path) )                     { validfile = false; read = "[not readable] "; conditions += read; }
            if (( ! isKey ) && ( ! Files.isWritable(path)) ) { validfile = false; write = "[not writable] "; conditions += write; }
            if ( (! symlink) && (Files.isSymbolicLink(path)) )  { validfile = false; symbolic = "[symlink]"; conditions += symbolic; }
        }
        if ( ! validfile ) { if ( report )			{ ui.log("Warning: DevCTRL: Invalid File: " + path.toAbsolutePath().toString() + ": " + conditions + "\r\n", true, true, true, true, false); } }                    
        return validfile;
    }

    public static boolean getPausing()             { return pausing; }
    public static boolean getStopPending()         { return stopPending; }
    public static void setPausing(boolean val)     { pausing = val; }
    public static void setStopPending(boolean val) { stopPending = val; }

}
