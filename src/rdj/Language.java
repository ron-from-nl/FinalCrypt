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

import java.util.*;

public class Language
{
    public final String iso639_2B;
    public final String iso639_2T;
    public final String iso639_1;
    public final ArrayList<String> languageNameEnglishList;
    public final ArrayList<String> languageNameFrenchList;
    public	  boolean installed = false;
    private String returnString;
    
    public Language(String iso639_2B, String iso639_2T, String iso639_1, ArrayList<String> languageNameEnglishList, ArrayList<String> languageNameFrenchList, boolean installed)
    {
	this.iso639_2B = iso639_2B;
	this.iso639_2T = iso639_2T;
	this.iso639_1 = iso639_1;
	this.languageNameEnglishList = languageNameEnglishList;
	this.languageNameFrenchList = languageNameFrenchList;
	this.installed = installed;
    }
    
    @Override    public String toString()
    {
	returnString = "";
	returnString += iso639_2B;
	returnString += "|";
	returnString += iso639_2T;
	returnString += "|";
	returnString += iso639_1;
	returnString += "|";
	languageNameEnglishList.forEach((langNameEng) -> { returnString += langNameEng + ";"; });
	returnString += "|";
	languageNameFrenchList.forEach((langNameFre) -> { returnString += langNameFre + ";"; });
	returnString += "|";
	returnString += installed;
	returnString += "\r\n";
	return returnString;
    }
}
