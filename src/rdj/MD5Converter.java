/*
 * © Copyleft 2017 ron
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

/**
 *
 * @author Ron de Jong ronuitzaandam@gmail.com
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
