/*
 * CC BY-NC-ND 4.0 2017 Ron de Jong (ronuitzaandam@gmail.com).
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

import java.io.*;
import java.util.*;

public class LanguageList<E> extends ArrayList<E>
{
    private UI ui;
    private String returnString;

    public LanguageList(UI ui)
    {
	this.ui = ui;
	loadLanguages(ui);
    }
    
    @Override public boolean add(E e)
    {
	boolean result = super.add(e);
	return result;
    }
    
    public void loadLanguages(UI ui)
    {
	String iso639ResourceString = "/rdj/language/iso639.txt";
	InputStream inputStream = this.getClass().getResourceAsStream(iso639ResourceString);
	InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	try
	{
	    String languageLine = "";
	    while ((languageLine = bufferedReader.readLine()) != null)
	    {
		String[] field = languageLine.split("[|]");

		String iso639_2B = field[0];
		String iso639_2T = field[1];
		String iso639_1 = field[2];
				
		ArrayList<String> languageNameEngList = new ArrayList<>();
		ArrayList<String> languageNameFreList = new ArrayList<>();

		for(String langNameEng:field[3].split("[;]")) { languageNameEngList.add(langNameEng); } 
		for(String langNameFre:field[4].split("[;]")) { languageNameFreList.add(langNameFre); } 

		boolean installed = false;
		
		if	( this.getClass().getResource("/rdj/language/translation_" + iso639_2B + ".properties") != null )   { installed = true; }
		else if ( this.getClass().getResource("/rdj/language/translation_" + iso639_2T + ".properties") != null )   { installed = true; }
		else if ( this.getClass().getResource("/rdj/language/translation_" + iso639_1 + ".properties") != null )    { installed = true; }
		else													    { installed = false; }
		
		Language language = new Language(iso639_2B, iso639_2T, iso639_1, languageNameEngList, languageNameFreList, installed);
		add((E) language);
	    }
	} catch (IOException ex) { ui.log("Error: bufferedReader.readLine(" + iso639ResourceString + ");" + ex.getMessage(), false, true, true, true, false); }
    }

    @Override    public String toString()
    {
	returnString = "";
	this.forEach((language) ->
	{
	    returnString += language.toString();
	});
	return returnString;
    }

    public ArrayList<String> getInstalledLanguageNamesList(LanguageList<Language> languageList)
    {
	ArrayList<String> languageNamesList = new ArrayList<String>();
	
	// Only add installed languages to the languageNameList
	for (Iterator it = languageList.iterator(); it.hasNext();)
	{
	    Language language = (Language) it.next();
	    if (language.installed) { for (String name: language.languageNameEnglishList ) { languageNamesList.add(name); } }
	}
	
	// Sort the languageNameList
	Collections.sort(languageNamesList, new Comparator<String>() { @Override  public int compare(String name1, String name2) { return  name1.compareTo(name2); } });
		
	return languageNamesList;
    }
}
