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
    long cipherSize = 0;
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
        } catch (IOException ex) { ui.status("Device().read(..) " + ex.getMessage(), true); }
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
        } catch (IOException ex) { ui.status("Device().read(..) " + ex.getMessage(), true); }
        return inputDeviceBuffer.array();
    }
    
//  Write byte[] to device
    synchronized public static void writeLBA(String desc, byte[] bytes, FCPath fcPath, long lba)
    {        
        long writeOutputDeviceChannelTransfered = 0;
        ByteBuffer outputDeviceBuffer = null;
        ui.log("Write " + desc + " Pos (" + getLBAOffSet(bytesPerSector, fcPath.size, lba) + ") ");
        try (final SeekableByteChannel writeOutputDeviceChannel = Files.newByteChannel(fcPath.path, EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.SYNC)))
        {
            outputDeviceBuffer = ByteBuffer.allocate(bytes.length); outputDeviceBuffer.put(bytes); outputDeviceBuffer.flip(); // logBytes(outputDeviceBuffer.array());
//            guifx.log("Buffer: " + outputDeviceBuffer.capacity());
            writeOutputDeviceChannel.position(getLBAOffSet(bytesPerSector, fcPath.size, lba));
            writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer);
            ui.log("Transfered: " + writeOutputDeviceChannelTransfered + "\r\n");
            writeOutputDeviceChannel.close();
        } catch (IOException ex) { ui.error("Error: Device.writeLBA(..): " + ex.getMessage() + ""); }
    }

//  Write Entry byte[] to device WARNING: writeOutputDeviceChannel.position(pos); causes exeption on OSX! Use writeLBA(..) above (from GPT_Entries) (hmm not anymore maybe)

    synchronized public static void writePos(String desc, byte[] bytes, FCPath device, long pos)
    {        
        long writeOutputDeviceChannelTransfered = 0;
        ByteBuffer outputDeviceBuffer = null;
        ui.log("Wrote " + desc + " Pos(" + pos + ") ");
        try (final SeekableByteChannel writeOutputDeviceChannel = Files.newByteChannel(device.path, EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.SYNC)))
        {
            outputDeviceBuffer = ByteBuffer.allocate(bytes.length); outputDeviceBuffer.put(bytes); outputDeviceBuffer.flip(); // logBytes(outputDeviceBuffer.array());
//            guifx.log("Buffer: " + outputDeviceBuffer.capacity());
            writeOutputDeviceChannel.position(pos);
            writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer);
            ui.log("Transfered: " + writeOutputDeviceChannelTransfered + "");
            writeOutputDeviceChannel.close();
        } catch (IOException ex) { ui.error("Error: Device.writePos(..): " + ex.getMessage() + "\r\n"); }
    }

//  Write CipherFile to partition
    synchronized public void writeCipherPartition(FCPath cipherFCPath, FCPath targetFCPath, long firstLBA, long lastLBA)
    {
	startCalendar = Calendar.getInstance(Locale.ROOT);
	boolean encryptcipher = true;
        if ( cipherFCPath.size < bufferSize)   { bufferSize = (int)cipherFCPath.size; if (FinalCrypt.verbose) ui.log("BufferSize is limited to cipherfile size: " + GPT.getHumanSize(bufferSize, 1) + " \r\n"); }
//        else                            { log("BufferSize is set to: " + getHumanSize(bufferSize, 1) + " \r\n"); }
        Stats allDataStats = new Stats(); allDataStats.reset();        
        Stat readCipherFileStat1 = new Stat(); readCipherFileStat1.reset();
        Stat readCipherFileStat2 = new Stat(); readCipherFileStat2.reset();
        Stat writeCipherFileStat1 = new Stat(); writeCipherFileStat1.reset();
        Stat writeCipherFileStat2 = new Stat(); writeCipherFileStat2.reset();

        allDataStats.setFilesTotal(1);
        allDataStats.setFileBytesTotal      (cipherFCPath.size * 2);
        allDataStats.setAllDataBytesTotal   (cipherFCPath.size * 2);
        ui.status(allDataStats.getStartSummary("Create Cipher Device"), true);
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
			    (( readCipherFileStat1.getFileBytesProcessed() + writeCipherFileStat1.getFileBytesProcessed() + readCipherFileStat2.getFileBytesProcessed() + writeCipherFileStat2.getFileBytesProcessed() ) * 2)
			    /   ( (allDataStats.getFileBytesTotal() * 3 ) / 100.0)),

		    (int) (
			    ( allDataStats.getFilesBytesProcessed() * 2) / ( (allDataStats.getFilesBytesTotal() * 3) / 100.0)
			  ), bytesTotal, bytesProcessed, bytesPerMilliSecond // long bytesPerMiliSecond
		);
	    }
	}; updateProgressTaskTimer = new java.util.Timer(); updateProgressTaskTimer.schedule(updateProgressTask, 0L, 200L);

        allDataStats.setAllDataStartNanoTime();

        ui.log("Writing " + cipherFCPath.path.toAbsolutePath() + " to partition 1 (LBA:"+ firstLBA + ":" + (getLBAOffSet(bytesPerSector, targetFCPath.size, firstLBA) + writeOutputDeviceChannelPosition) + ")");
        write1loop: while ( ! inputEnded )
        {
            while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
            if (stopPending)    { inputEnded = true; break write1loop; }

            readCipherFileStat1.setFileStartEpoch();
            try (final SeekableByteChannel readCipherFileChannel = Files.newByteChannel(cipherFCPath.path, EnumSet.of(StandardOpenOption.READ)))
            {
                // Fill up cipherFileBuffer
                readCipherFileChannel.position(readCipherFileChannelPosition);
                readCipherFileChannelTransfered = readCipherFileChannel.read(cipherFileBuffer); readCipherFileChannelPosition += readCipherFileChannelTransfered;
                if (( readCipherFileChannelTransfered < 1 ) || ( cipherFileBuffer.limit() < bufferSize )) { inputEnded = true; }
                cipherFileBuffer.flip();
                readCipherFileChannel.close(); readCipherFileStat1.setFileEndEpoch(); readCipherFileStat1.clock();
                readCipherFileStat1.addFileBytesProcessed(readCipherFileChannelTransfered); allDataStats.addAllDataBytesProcessed("", readCipherFileChannelTransfered);
            } catch (IOException ex) { ui.log("Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex.getMessage() + "\r\n"); }
            
//          Randomize raw cipher or write raw cipher straight to partition
	    SecureRandom random = new SecureRandom();
//	    if (encryptcipher)	{ random.nextBytes(randomizedBytes); randomizedBuffer.put(randomizedBytes); randomizedBuffer.flip();outputDeviceBuffer = encryptBuffer(cipherFileBuffer, randomizedBuffer); }
	    if (encryptcipher)	{ random.nextBytes(randomizedBytes); randomizedBuffer.put(randomizedBytes); randomizedBuffer.flip();outputDeviceBuffer = FinalCrypt.encryptBuffer(cipherFileBuffer, randomizedBuffer); }
	    else		{ outputDeviceBuffer.put(cipherFileBuffer); outputDeviceBuffer.flip(); }
            
//          Write Device
            writeCipherFileStat1.setFileStartEpoch();
            try (final SeekableByteChannel writeOutputDeviceChannel = Files.newByteChannel(targetFCPath.path, EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.SYNC)))
            {
//              Write cipherfile to partition 1
                writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, targetFCPath.size, firstLBA) + writeOutputDeviceChannelPosition));
                writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer); outputDeviceBuffer.rewind();
                writeCipherFileStat1.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed("", readCipherFileChannelTransfered);
//                ui.log("writeOutputDeviceChannelTransfered 1 : " + writeOutputDeviceChannelTransfered + "\r\n");

//              Write cipherfile to partition 2
                writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, targetFCPath.size, lastLBA + 1) + writeOutputDeviceChannelPosition));
                writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer); outputDeviceBuffer.rewind();
                writeCipherFileStat1.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed("", readCipherFileChannelTransfered);
//                ui.log("writeOutputDeviceChannelTransfered 1 : " + writeOutputDeviceChannelTransfered + "\r\n");

                writeOutputDeviceChannelPosition += writeOutputDeviceChannelTransfered;

                if ( inputEnded )
                {
                    long partLength = ((lastLBA - firstLBA) + 1) * bytesPerSector; long gap = partLength - cipherFCPath.size;
		    outputDeviceBuffer = ByteBuffer.allocate((int)gap); outputDeviceBuffer.clear(); 
		    
//		    Randomize or zero out gab at end of partition
		    if (encryptcipher)    { randomizedBytes = new byte[(int)gap]; random.nextBytes(randomizedBytes);outputDeviceBuffer.put(randomizedBytes); outputDeviceBuffer.flip(); }
		    else	    { outputDeviceBuffer.put(GPT.getZeroBytes((int)gap)); outputDeviceBuffer.flip(); }
                    
//                  Fill in gap at end of partition 1
                    writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, targetFCPath.size, firstLBA) + writeOutputDeviceChannelPosition));
                    writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer); outputDeviceBuffer.rewind();
                    writeCipherFileStat1.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed("", readCipherFileChannelTransfered);
//                    ui.log("writeOutputDeviceChannelTransfered 1 : " + writeOutputDeviceChannelTransfered + "\r\n");                

//                  Fill in gap at end of partition 2
                    writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, targetFCPath.size, lastLBA + 1) + writeOutputDeviceChannelPosition));
                    writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer); outputDeviceBuffer.rewind();
                    writeCipherFileStat1.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed("", readCipherFileChannelTransfered);
//                    ui.log("writeOutputDeviceChannelTransfered 1 : " + writeOutputDeviceChannelTransfered + "\r\n");                
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
        ui.status(allDataStats.getEndSummary("Create Cipher Device"), true);

        updateProgressTaskTimer.cancel(); updateProgressTaskTimer.purge();
//        updateProgressTimeline.stop();
        ui.processFinished();
    }

//  Write CipherFile to partition 1 & 2
//    synchronized public void cloneCipherPartition(Path cipherDeviceFilePath, Path targetDeviceFilePath, long firstLBA, long lastLBA)
//    synchronized public void cloneCipherPartition(Device cipherDevice, Device targetDevice, long firstLBA, long lastLBA)
    synchronized public void cloneCipherPartition(FCPath cipherFCPath, FCPath targetFCPath, long firstLBA, long lastLBA)
    {
	startCalendar = Calendar.getInstance(Locale.ROOT);
	if ( ( isValidFile(ui,cipherFCPath.path, false, false, true) ) && ( isValidFile(ui, targetFCPath.path, false, false, true) ) )
	{
	    long targetDeviceSize2 = targetFCPath.size;
	    long cipherPartitionSize = getCipherPartitionSize(ui, cipherFCPath);
	    if ( cipherPartitionSize < bufferSize)   { bufferSize = (int)cipherPartitionSize; if (FinalCrypt.verbose) ui.log("BufferSize is limited to cipherfile size: " + GPT.getHumanSize(bufferSize, 1) + " \r\n"); }
//	    long cipherSize = getCipherPartitionSize(ui, cipherDevice);
//	    if ( cipherFCPath.size < bufferSize)   { bufferSize = (int)cipherSize; if (FinalCrypt.verbose) ui.log("BufferSize is limited to cipherfile size: " + GPT.getHumanSize(bufferSize, 1) + " \r\n"); }
	    if ( cipherPartitionSize < bufferSize)   { bufferSize = (int)cipherPartitionSize; if (FinalCrypt.verbose) ui.log("BufferSize is limited to cipherfile size: " + GPT.getHumanSize(bufferSize, 1) + " \r\n"); }
//            else                            { log("BufferSize is set to: " + getHumanSize(bufferSize, 1) + " \r\n"); }
	    Stats allDataStats = new Stats(); allDataStats.reset();        
	    Stat readCipherFileStat1 = new Stat(); readCipherFileStat1.reset();
	    Stat readCipherFileStat2 = new Stat(); readCipherFileStat2.reset();
	    Stat writeCipherFileStat1 = new Stat(); writeCipherFileStat1.reset();
	    Stat writeCipherFileStat2 = new Stat(); writeCipherFileStat2.reset();

	    allDataStats.setFilesTotal(1);
//	    allDataStats.setFileBytesTotal      (getCipherPartitionSize(ui, cipherDevice) * 2);
//	    allDataStats.setAllDataBytesTotal   (getCipherPartitionSize(ui, cipherDevice) * 2);
//	    allDataStats.setFileBytesTotal      (cipherFCPath.size * 2);
//	    allDataStats.setAllDataBytesTotal   (cipherFCPath.size * 2);
	    allDataStats.setFileBytesTotal      (cipherPartitionSize * 2);
	    allDataStats.setAllDataBytesTotal   (cipherPartitionSize * 2);

	    ui.status(allDataStats.getStartSummary("Clone Cipher Device"), true);
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
					readCipherFileStat1.getFileBytesProcessed() +
					writeCipherFileStat1.getFileBytesProcessed() +
					readCipherFileStat2.getFileBytesProcessed() +
					writeCipherFileStat2.getFileBytesProcessed()
				)   /   ( (allDataStats.getFileBytesTotal() * 1 ) / 100.0)),

			(int) (
				( 
					allDataStats.getFilesBytesProcessed() * 1) /
					( (allDataStats.getFilesBytesTotal() * 1) / 100.0)
				), bytesTotal, bytesProcessed, bytesPerMilliSecond // long bytesPerMiliSecond
		);
	    }}; updateProgressTaskTimer = new java.util.Timer(); updateProgressTaskTimer.schedule(updateProgressTask, 0L, 200L);

	    allDataStats.setAllDataStartNanoTime();

	    ui.log("Cloning " + cipherFCPath.path.toAbsolutePath() + " to " + targetFCPath.path.toAbsolutePath() + " partitions (LBA:"+ firstLBA + ":" + (getLBAOffSet(bytesPerSector, targetFCPath.size, firstLBA) + writeOutputDeviceChannelPosition) + ")");

	    readCipherDeviceFileChannelPosition = DeviceController.getLBAOffSet(bytesPerSector, targetFCPath.size, firstLBA) + readCipherDeviceFileChannelPosition;
	    write1loop: while ( ! inputEnded )
	    {
		while (pausing)     { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
		if (stopPending)    { inputEnded = true; break write1loop; }

		readCipherFileStat1.setFileStartEpoch();
		try (final SeekableByteChannel readCipherDeviceFileChannel = Files.newByteChannel(cipherFCPath.path, EnumSet.of(StandardOpenOption.READ)))
		{
		    // Fill up cipherDeviceBuffer
		    readCipherDeviceFileChannel.position(readCipherDeviceFileChannelPosition);
		    readCipherDeviceFileChannelTransfered = readCipherDeviceFileChannel.read(cipherDeviceBuffer); cipherDeviceBuffer.flip();
		    readCipherDeviceFileChannelTransferedTotal += readCipherDeviceFileChannelTransfered; readCipherDeviceFileChannelPosition += readCipherDeviceFileChannelTransfered;
		    if ( readCipherDeviceFileChannelTransferedTotal >= cipherPartitionSize ) { inputEnded = true; cipherDeviceBuffer.limit((int)readCipherDeviceFileChannelTransferedTotal - (int)cipherPartitionSize); }
		    readCipherDeviceFileChannel.close(); readCipherFileStat1.setFileEndEpoch(); readCipherFileStat1.clock();
		    readCipherFileStat1.addFileBytesProcessed(readCipherDeviceFileChannelTransfered); allDataStats.addAllDataBytesProcessed("", readCipherDeviceFileChannelTransfered);
		} catch (IOException ex) { ui.error("Files.newByteChannel(cipherFilePath, EnumSet.of(StandardOpenOption.READ)) " + ex.getMessage() + "\r\n"); }

		// For sone reason cipherDeviceBuffer does not poor any data out into the writeOutputDeviceChannel, but does output data to GPT.logBytes
		outputDeviceBuffer.put(cipherDeviceBuffer.array()); outputDeviceBuffer.flip();

    //          Write Device
		writeCipherFileStat1.setFileStartEpoch();
		try (final SeekableByteChannel writeOutputDeviceChannel = Files.newByteChannel(targetFCPath.path, EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.SYNC)))
		{
    //              Write cipherfile to partition 1
		    writeOutputDeviceChannel.position((getLBAOffSet(bytesPerSector, targetFCPath.size, firstLBA) + writeOutputDeviceChannelPosition));
		    writeOutputDeviceChannelTransfered = writeOutputDeviceChannel.write(outputDeviceBuffer); outputDeviceBuffer.rewind();
		    writeCipherFileStat1.addFileBytesProcessed(writeOutputDeviceChannelTransfered); allDataStats.addAllDataBytesProcessed("", readCipherDeviceFileChannelTransfered);

		    writeOutputDeviceChannelPosition += writeOutputDeviceChannelTransfered;
		    writeOutputDeviceChannel.close(); writeCipherFileStat1.setFileEndEpoch(); writeCipherFileStat1.clock();
		} catch (IOException ex) { ui.error("Files.newByteChannel(targetFCPath.path, EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.SYNC))" + ex.getMessage() + "\r\n"); }
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
	    ui.status(allDataStats.getEndSummary("Clone Cipher Device"), true);

	    updateProgressTaskTimer.cancel(); updateProgressTaskTimer.purge();
    //        updateProgressTimeline.stop();
	    ui.processFinished();
	}
	else
	{
	    ui.error("Error: Cloning aborted.\r\n");
	}
    }

//    public static long getCipherPartitionSize(UI ui, Path cipherDeviceFilePath)
    public static long getCipherPartitionSize(UI ui, FCPath cipherFCPath)
    {
        GPT gpt = new GPT(ui);
        gpt.read(cipherFCPath);
        long partitionSize =    bytesPerSector + ((gpt.gpt_Entries1.gpt_entry[0].endingLBA - gpt.gpt_Entries1.gpt_entry[0].startingLBA) * bytesPerSector);
//        System.out.println("flba1: " + gpt.gpt_Entries.firstLBA1 + " llba1: "  + gpt.gpt_Entries.lastLBA1 + " ciphersize: " + partitionSize);
        return partitionSize;
    }

//  Wrapper method
    synchronized public static long getDeviceSize(UI ui, Path path)
    {
	long size = getDeviceSize2(ui, path, true); return size; // Customized method (platform independent)
    }

//  Get size of device        
    synchronized public static long getDeviceSize1(UI ui, Path path)
    {
        long deviceSize = 0;
        try (final SeekableByteChannel deviceChannel = Files.newByteChannel(path, EnumSet.of(StandardOpenOption.READ))) { deviceSize = deviceChannel.size(); deviceChannel.close(); } catch (IOException ex) { ui.status(ex.getMessage(), true); }
        return deviceSize;
    }

    synchronized public static long getDeviceSize2(UI ui, Path path, boolean firstcall) // OS Independent half or dubbel guess size test (Files.size(..) doesn't work on Apple OSX)
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
	if (isValidFile(ui, path, false, false, true ))
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
		    getDeviceSize2(ui,path, false);
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
        if (verbose) ui.log(String.format("%-20s %-20s %-20s %-20s %-20s %-20s \r\n", "LastPoss     ", "CurrPoss     ", "Step    ", "Above     ", "Below    ", "Cycles     "));
        
        label: while (! finished)
        {
            if (verbose) ui.log(String.format("%-20d %-20d %-20d %-20d %-20d %-20d \r\n", lastpos, currpos, step, above, below, cycles));
            
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
        ui.log("Target: " + target + "\r\n");
        ui.log(String.format("%-20s %-20s %-20s %-20s %-20s %-20s \r\n", "LastPoss     ", "CurrPoss     ", "Step    ", "Above     ", "Below    ", "Cycles     "));
        
        label: while (! finished)
        {
            ui.log(String.format("%-20d %-20d %-20d %-20d %-20d %-20d \r\n", lastpos, currpos, step, above, below, cycles));
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
        if ( validdir ) {  } else { if ( report )               { ui.error("Warning: Invalid Dir: " + path.toString() + ": " + conditions + "\r\n"); } }
        return validdir;
    }

    public static boolean isValidFile(UI ui, Path path, boolean readSize, boolean symlink, boolean report)
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
        if ( ! validfile ) { if ( report )			{ ui.error("Warning: DevCTRL: Invalid File: " + path.toAbsolutePath().toString() + ": " + conditions + "\r\n"); } }                    
        return validfile;
    }

    public static boolean getPausing()             { return pausing; }
    public static boolean getStopPending()         { return stopPending; }
    public static void setPausing(boolean val)     { pausing = val; }
    public static void setStopPending(boolean val) { stopPending = val; }

}
