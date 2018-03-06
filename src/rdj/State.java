/*
 * Copyright © 2017 Ron de Jong (ronuitzaandam@gmail.com).
 *
 * This is free software; you can redistribute it 
 * under the terms of the Creative Commons License
 * Creative Commons License: (CC BY-NC-ND 4.0) as published by
 * https://creativecommons.org/licenses/by-nc-nd/4.0/; either
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

public class State
{
    public final    static  int	    OBJECT =		    0;
    public final    static  int	    FILE =		    1;
    public final    static  int	    DIR =		    2;
    public final    static  int	    MULTI =		    3;
    public final    static  int	    DEVICE =		    4;
    public final    static  int	    PARTITION =		    5;
    private static final String[]   ITEMSELECTDESCRIPTION = new String[] { "Object","File","Directory","Multi","Device","Partition" };

    public          static  int targetSelected =     OBJECT;
    public          static  int cipherSelected =     OBJECT;
    
    public          static  boolean targetReady =    false;
    public          static  boolean cipherReady =    false;
    public static   void    reset()
    {
        targetSelected = OBJECT;
        cipherSelected = OBJECT;
        targetReady =    false;
        cipherReady =    false;
    }
    public static void setCipherSelected(int value)	   { cipherSelected = value; }
    public static void setTargetSelected(int value)	   { targetSelected = value; }
    
    public static String getCipherSelectedDescription()    { return ITEMSELECTDESCRIPTION[cipherSelected]; }	
    public static String getTargetSelectedDescription()    { return ITEMSELECTDESCRIPTION[targetSelected]; }
    
    public static String print()
    {
	String s = "";
//	s += "Cipher selected: " + getCipherSelectedDescription() + " ready: " + cipherReady + "\r\n";
//	s += "Target selected: " + getTargetSelectedDescription() + " ready: " + targetReady + "\r\n";
	
	String cipherString = ""; if ( cipherReady ) { /*cipherString = "Cipher " + getCipherSelectedDescription() + " is okay\r\n";*/ } else { cipherString = "Cipher " + getCipherSelectedDescription() + " is invalid. Please select a valid Cipher.\r\n"; }
	String targetString = ""; if ( targetReady ) { /*targetString = "Target " + getTargetSelectedDescription() + " is okay\r\n";*/ } else { targetString = "Target " + getTargetSelectedDescription() + " is invalid. Please select a valid Target.\r\n"; }
	s += cipherString;
	s += targetString;
	return s;
    }
}