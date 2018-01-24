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

public class Mode
{
    public  static final int        SELECT =                0;
    public  static final int        ENCRYPT =               1;
    public  static final int        ENCRYPTRAW =            2;
    public  static final int        CREATE_CIPHER_DEVICE =  3;
    public  static final int        CLONE_CIPHER_DEVICE =   4;
    private static final String[]   MODEDESCRIPTION =       new String[] { "Select","Encrypt","Encrypt (Raw Cipher)","Create Cipher Device","Clone Cipher Device" };
    
    private static       int        mode =                  SELECT;
    public  static boolean          modeReady =             false;

    public static int getMode()                             { return mode; }
    public static String setMode(int value)                 { mode = value; return getDescription(); }
    public static String getDescription()                   { return MODEDESCRIPTION[mode]; }
}
