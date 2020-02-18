/*
 * CC BY-NC-ND 4.0 2017 Ron de Jong (ron@finalcrypt.org)
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

import java.nio.*;
import java.security.*;

public class RNG
{
//  Random streams scrambler
    public static ByteBuffer getFCRandomBuffer(UI ui, int size, boolean extraSeed, boolean encrypt, boolean print)
    {
	if ( encrypt )
	{
	    ByteBuffer randomBuffer1 = getRandomBuffer(ui, size, extraSeed, print);
	    ByteBuffer randomBuffer2 = getRandomBuffer(ui, size, extraSeed, print);
	    if ( print ) { ui.log("\r\n", false, true, true, false, false); }
	    return encryptBuffer(randomBuffer1, randomBuffer2, false);
	}
	else
	{
	    ByteBuffer randomBuffer1 = getRandomBuffer(ui, size, extraSeed, print);
	    if ( print ) { ui.log("\r\n", false, true, true, false, false); }
	    return randomBuffer1;
	}
    }
    
//  java.security.SecureRandom RNG
    private static ByteBuffer getRandomBuffer(UI ui, int size, boolean extraSeed, boolean print)
    {
	byte[] randomBytes = new byte[size]; ByteBuffer randomBuffer = ByteBuffer.allocate(size); randomBuffer.clear();

	SecureRandom random = new SecureRandom();
	if ( extraSeed )
	{
	    // Supplemental seed nano timestamp
	    MessageDigest messageDigest = null; try { messageDigest = MessageDigest.getInstance(FinalCrypt.HASH_ALGORITHM_NAME); }
	    catch (NoSuchAlgorithmException ex) { ui.log("Error: NoSuchAlgorithmException: MessageDigest.getInstance(\" "+ FinalCrypt.HASH_ALGORITHM_NAME + "\")\r\n", true, true, true, true, false); }
	    messageDigest.update(Long.valueOf(System.nanoTime()).byteValue());
	    if (print) { byte[] hashBytes = messageDigest.digest();  ui.log("\r\n" + getHexString(hashBytes,2), false, true, true, false, false); }
	    random.setSeed(messageDigest.digest());
	}
	
	// Fill randomBytes & randomBuffer
	random.nextBytes(randomBytes);
	
	randomBuffer.put(randomBytes); randomBuffer.flip();
	return randomBuffer;
    }

//  Random Bit Generator
    private static ByteBuffer getRandomBuffer2(UI ui, int size, boolean extraSeed, boolean print)
    {
	byte[] randomBytes = new byte[size]; ByteBuffer randomBuffer = ByteBuffer.allocate(size); randomBuffer.clear(); byte rngByte = 0;
	for (int x=0; x < size; x++)
	{
	    for (int b=1; b <= 8; b++)
	    {
//		rngByte = (byte) (rngByte << 1); rngByte += (int)(System.nanoTime() & 0x00000001); # Bad randomness at end
		rngByte = (byte) (rngByte << 1); rngByte += (int)(Math.round(Math.random()) & 0x00000001);
	    }
//	    randomBytes[x] = rngByte;
	    randomBuffer.put(rngByte);
	}
//	randomBuffer.put(randomBytes);
	randomBuffer.flip();
	return randomBuffer;
    }
    
    public static ByteBuffer encryptBuffer(ByteBuffer targetSourceBuffer, ByteBuffer keySourceBuffer, boolean printEnabled)
    {
        ByteBuffer targetDestinBuffer = ByteBuffer.allocate(keySourceBuffer.capacity()); targetDestinBuffer.clear();
        byte targetDestinByte;
	for (int targetSourceBufferCount = 0; targetSourceBufferCount < targetSourceBuffer.limit(); targetSourceBufferCount++)
        {
	    byte targetSourceByte = targetSourceBuffer.get(targetSourceBufferCount);
	    byte keySourceByte = keySourceBuffer.get(targetSourceBufferCount);
	    
	    targetDestinByte = encryptByte(targetSourceByte, keySourceByte); targetDestinBuffer.put(targetDestinByte);
	}
        targetDestinBuffer.flip();
		
	return targetDestinBuffer;
    }
    
    public static byte encryptByte(final byte targetSourceByte, byte keySourceByte) { return (byte)(targetSourceByte ^ keySourceByte); }

    synchronized public static String getHexString(byte[] bytes, int digits) { String returnString = ""; for (byte mybyte:bytes) { returnString += getHexString(mybyte, digits); } return returnString; }
    synchronized public static String getHexString(byte value, int digits) { return String.format("%0" + Integer.toString(digits) + "X", (value & 0xFF)).replaceAll("[^A-Za-z0-9]",""); }

}
