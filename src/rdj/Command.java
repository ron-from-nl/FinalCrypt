/*
 * Copyright © 2017 Ron de Jong (ron@finalcrypt.org)
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

public class Command
{
    public static String command =	Version.getCommandLine();
    public static String scanMode =	"--scan";
    public static String encryptMode =	"--encrypt";
    public static String decryptMode =	"--decrypt";    
    public static String pwdOption =	"";
    public static String options =	"";
    public static String keyParam =	"";
    public static String tgtParams =	"";
    
    public static String getCommandLine(boolean encrypt, boolean decrypt)
    {
	String result = "";
	
	if	((!encrypt) && (!decrypt)) { result += compileCommandLine(scanMode) + "\r\n"; }
	else if ((!encrypt) && ( decrypt)) { result += compileCommandLine(decryptMode) + "\r\n"; }
	else if (( encrypt) && (!decrypt)) { result += compileCommandLine(encryptMode) + "\r\n"; }
	else if (( encrypt) && ( decrypt)) { result += compileCommandLine(encryptMode) + "\r\n"; result += compileCommandLine(decryptMode) + "\r\n"; }
	
	return result;	
    }
    
    private static String compileCommandLine(String runningMode)
    {
	String result = "";

	if ( command.length() > 0 )	{ result += command + " "; }
	if ( runningMode.length() > 0 )	{ result += runningMode + " "; }
	if ( pwdOption.length() > 0 )	{ result += pwdOption + " "; }
	if ( options.length() > 0 )	{ result += options + " "; }
	if ( keyParam.length() > 0 )	{ result += keyParam + " "; }
	if ( tgtParams.length() > 0 )	{ result += tgtParams; }

	return result;
    }
}
