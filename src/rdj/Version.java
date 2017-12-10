/*
 * Copyright (C) 2017 ron
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
package rdj;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import static java.nio.channels.Channels.newChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Calendar;

public class Version
{
    private UI ui;
    private static final String COMPANYNAME = "GPLv3";
    private static final String PRODUCTNAME = "FinalCrypt";
    private static final String AUTHOR = "Ron de Jong";
    private static final String COPYRIGHT = "© Copyleft " + Calendar.getInstance().get(Calendar.YEAR);
    private static String thisOverallVersionString = "";
    private String latestOverallVersionString = "";
    private static int thisVersionTotal = 0;
    private int latestVersionTotal = 0;
//        URL localURL = null;
    private InputStream istream = null;
    private static final String REMOTEVERSIONSTRING =          "https://raw.githubusercontent.com/ron-from-nl/FinalCrypt/master/src/rdj/VERSION";
    public static final String REMOTEVERSIONPACKAGESTRING =    "https://github.com/ron-from-nl/FinalCrypt/releases/download/latest/FinalCrypt.jar";
    private URL remoteURL = null;
    private ReadableByteChannel rbc = null;
    private ByteBuffer byteBuffer; 
    
    private boolean versionsKnown = false;
    private boolean updateAvailable = false;

    public Version(UI ui)
    {
        this.ui = ui;
        istream = getClass().getResourceAsStream("VERSION");

//      Read the local VERSION file
        rbc = newChannel(istream);
        byteBuffer = ByteBuffer.allocate(512); thisOverallVersionString = "";
        try {
            while(rbc.read(byteBuffer) > 0)
            {
                byteBuffer.flip();
                while(byteBuffer.hasRemaining())
                {
                    thisOverallVersionString += (char) byteBuffer.get();
                }
            }
        } catch (IOException ex) { ui.error(ex.getMessage()+"\n"); }
        try { rbc.close(); } catch (IOException ex) { ui.error(ex.getMessage()+"\n"); }        

        thisOverallVersionString.replaceAll("\\p{C}", "?");
//        thisOverallVersionString.replaceAll("[^\\d. ]", "");

        String thisVersionString = thisOverallVersionString.substring(0, thisOverallVersionString.indexOf(".")).replaceAll("[^\\d]", "");
        String thisUpgradeString = thisOverallVersionString.substring(thisOverallVersionString.indexOf("."), thisOverallVersionString.lastIndexOf(".")).replaceAll("[^\\d]", "");
        String thisUpdateString = thisOverallVersionString.substring(thisOverallVersionString.lastIndexOf("."), thisOverallVersionString.length()).replaceAll("[^\\d]", "");
        
        int thisVersion = Integer.parseInt(thisVersionString);
        int thisUpgrade = Integer.parseInt(thisUpgradeString);
        int thisUpdate = Integer.parseInt(thisUpdateString);
        thisVersionTotal = thisVersion+thisUpgrade+thisUpdate;
        thisOverallVersionString = thisVersionString + "." + thisUpgradeString + "." + thisUpdateString;
    }

    public void checkLastestVersion()
    {
//      Read the remote VERSION file
        try { remoteURL = new URL(REMOTEVERSIONSTRING); } catch (MalformedURLException ex) { ui.error(ex.getMessage()+"\n"); }
        try { rbc = Channels.newChannel(remoteURL.openStream()); } catch (IOException ex) { ui.error(ex.getMessage()+"\n"); }
        byteBuffer = ByteBuffer.allocate(512); latestOverallVersionString = "";
        try {
            while(rbc.read(byteBuffer) > 0)
            {
                byteBuffer.flip();
                while(byteBuffer.hasRemaining())
                {
                    latestOverallVersionString += (char) byteBuffer.get();
                }
            }
        } catch (IOException ex) { ui.error(ex.getMessage()+"\n"); }
        try { rbc.close(); } catch (IOException ex) { ui.error(ex.getMessage()+"\n"); }

        latestOverallVersionString.replaceAll("\\p{C}", "?");
//        latestOverallVersionString.replaceAll("[^\\d.", "");

        String latestVersionString = latestOverallVersionString.substring(0, latestOverallVersionString.indexOf(".")).replaceAll("[^\\d]", "");
        String latestUpgradeString = latestOverallVersionString.substring(latestOverallVersionString.indexOf("."), latestOverallVersionString.lastIndexOf(".")).replaceAll("[^\\d]", "");
        String latestUpdateString = latestOverallVersionString.substring(latestOverallVersionString.lastIndexOf("."), latestOverallVersionString.length()).replaceAll("[^\\d]", "");

        int latestVerion = Integer.parseInt(latestVersionString);
        int latestUpgrade = Integer.parseInt(latestUpgradeString);
        int latestUpdate = Integer.parseInt(latestUpdateString);
        latestVersionTotal = latestVerion+latestUpgrade+latestUpdate;
        latestOverallVersionString = latestVersionString + "." + latestUpgradeString + "." + latestUpdateString;        
    }

    public String getLatestOverallVersionString() { return latestOverallVersionString; }
    public String getThisOverallVersionString() { return thisOverallVersionString; }

    public String getVersionReport() 
    {
        String returnString = "";
        if      (thisVersionTotal < latestVersionTotal)
        {
//            returnString += "Your version: " + thisOverallVersionString + " is outdated. There's a new version: " + latestOverallVersionString + " available at: http://github.com/ron-from-nl/FinalCrypt/releases/download/1.1/FinalCrypt.jar\n"; 
            returnString += getProcuct() + " " + thisOverallVersionString + " can be updated to version: " + latestOverallVersionString + " at: " + REMOTEVERSIONPACKAGESTRING + "\n"; 

        } 
        else if (thisVersionTotal > latestVersionTotal)
        {
            returnString += getProcuct() + " " + thisOverallVersionString + " is a development version!\n";
        } 
        else
        {
            returnString += getProcuct() + " " + thisOverallVersionString + " is up to date\n";
        } 
        return returnString;
    }

    public boolean versionIsDifferent()     { if      ( thisVersionTotal != latestVersionTotal ) { return true; } else { return false; } }
    public boolean versionCanBeUpdated()    { if      ( thisVersionTotal < latestVersionTotal )  { return true; } else { return false; } }
    public boolean versionIsDevelopment()   { if      ( thisVersionTotal > latestVersionTotal )  { return true; } else { return false; } }    

    public static String getCopyright()                     { return COPYRIGHT; }
    public static String getAuthor()                        { return AUTHOR; }
    public static String getProcuct()                       { return PRODUCTNAME; }
    public static String getCompany()                       { return COMPANYNAME; }
}
