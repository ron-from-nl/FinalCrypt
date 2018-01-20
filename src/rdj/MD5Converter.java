/*
 * Copyright (C) 2018 Ron de Jong (ronuitzaandam@gmail.com).
 *
 * This is free software; you can redistribute it 
 * under the terms of the Creative Common License
 * Creative Common License: (CC BY-NC-ND 4.0) as published by
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Converter {

    public static String getMD5SumFromString(String input)
    {
        String output = "";

        byte[] inputBytes = input.getBytes();
        MessageDigest myMessageDigest = null;
        try { myMessageDigest = MessageDigest.getInstance("MD5"); } catch (NoSuchAlgorithmException ex) {  }
        myMessageDigest.reset();
        myMessageDigest.update(inputBytes);
        byte messageDigest[] = myMessageDigest.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i=0; i < messageDigest.length; i++ ) { hexString.append(Integer.toHexString(0xFF & messageDigest[i])); }
        output = hexString+"";

        return output;
    }

    public static String getMD5SumFromByteArray(byte[] byteArray)
    {
        String output = "";
        MessageDigest myMessageDigest = null;
        try { myMessageDigest = MessageDigest.getInstance("MD5"); } catch (NoSuchAlgorithmException ex) {  }
        myMessageDigest.reset();
        myMessageDigest.update(byteArray);
        byte messageDigest[] = myMessageDigest.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i=0; i < messageDigest.length; i++ ) { hexString.append(Integer.toHexString(0xFF & messageDigest[i])); }
        output = hexString+"";

        return output;
    }
}
