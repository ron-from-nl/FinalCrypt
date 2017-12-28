/*
 * © copyleft 2018 ron
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
    private static final String COPYLEFT = "© Copyleft " + Calendar.getInstance().get(Calendar.YEAR); // Future Copyleft symbol 🄯
    private static String currentOverallVersionString = "";
    private String latestOverallVersionString = "";
    private static int currentVersionTotal = 0;
    private int latestVersionTotal = 0;
    private InputStream istream = null;
    private static final String REMOTEVERSIONFILEURLSTRING =    "https://raw.githubusercontent.com/ron-from-nl/FinalCrypt/master/src/rdj/VERSION";
    public static final String REMOTEPACKAGEDOWNLOADURISTRING = "https://github.com/ron-from-nl/FinalCrypt/releases/tag/latest/";
    private URL remoteURL = null;
    private ReadableByteChannel currentVersionByteChannel = null;
    private ReadableByteChannel latestVersionByteChannel = null;
    private ByteBuffer byteBuffer; 
    
    private boolean currentVersionIsKnown = false;
    private boolean latestVersionIsKnown = false;
    private boolean updateAvailable = false;

    public Version(UI ui)
    {
        this.ui = ui;
    }
    
    public String checkCurrentlyInstalledVersion()
    {
        istream = getClass().getResourceAsStream("VERSION");

//      Read the local VERSION file
        currentOverallVersionString = "Unknown";
        currentVersionByteChannel = newChannel(istream);
        byteBuffer = ByteBuffer.allocate(512);
        try {
            while(currentVersionByteChannel.read(byteBuffer) > 0)
            {
                byteBuffer.flip();
                while(byteBuffer.hasRemaining())
                {
                    currentOverallVersionString += (char) byteBuffer.get();
                }
            }
        } catch (IOException ex) { ui.error(ex.getMessage()+"\n"); }
        try { currentVersionByteChannel.close(); } catch (IOException ex) { ui.error(ex.getMessage()+"\n"); }        

        currentOverallVersionString.replaceAll("\\p{C}", "?");
//        currentOverallVersionString.replaceAll("[^\\d. ]", "");

        String currentVersionString = currentOverallVersionString.substring(0, currentOverallVersionString.indexOf(".")).replaceAll("[^\\d]", "");
        String currentUpgradeString = currentOverallVersionString.substring(currentOverallVersionString.indexOf("."), currentOverallVersionString.lastIndexOf(".")).replaceAll("[^\\d]", "");
        String currentUpdateString = currentOverallVersionString.substring(currentOverallVersionString.lastIndexOf("."), currentOverallVersionString.length()).replaceAll("[^\\d]", "");
        
        int currentVersion = Integer.parseInt(currentVersionString);
        int currentUpgrade = Integer.parseInt(currentUpgradeString);
        int currentUpdate = Integer.parseInt(currentUpdateString);
        
        currentVersionTotal = (currentVersion * 100) + (currentUpgrade * 10) + (currentUpdate * 1);
        currentOverallVersionString = currentVersionString + "." + currentUpgradeString + "." + currentUpdateString;
        currentVersionIsKnown = true;
        return currentOverallVersionString;
    }

    public String checkLatestOnlineVersion()
    {
//      Read the remote VERSION file
        latestOverallVersionString = "Unknown";
        try { remoteURL = new URL(REMOTEVERSIONFILEURLSTRING); } catch (MalformedURLException ex) { ui.error(ex.getMessage()+"\n"); }
        try { latestVersionByteChannel = Channels.newChannel(remoteURL.openStream()); } catch (IOException ex) { ui.error(ex.getMessage()+"\n"); } // null pointer at no connect
        byteBuffer = ByteBuffer.allocate(512);
        try
        {
            latestOverallVersionString = "";
            while(latestVersionByteChannel.read(byteBuffer) > 0)
            {
                byteBuffer.flip();
                while(byteBuffer.hasRemaining())
                {
                    latestOverallVersionString += (char) byteBuffer.get();
                }
            }
        } catch (IOException ex) { ui.error(ex.getMessage()+"\n"); }
        try { latestVersionByteChannel.close(); } catch (IOException ex) { ui.error(ex.getMessage()+"\n"); }

        latestOverallVersionString.replaceAll("\\p{C}", "?");
//        latestOverallVersionString.replaceAll("[^\\d.", "");

        String latestVersionString = latestOverallVersionString.substring(0, latestOverallVersionString.indexOf(".")).replaceAll("[^\\d]", "");
        String latestUpgradeString = latestOverallVersionString.substring(latestOverallVersionString.indexOf("."), latestOverallVersionString.lastIndexOf(".")).replaceAll("[^\\d]", "");
        String latestUpdateString = latestOverallVersionString.substring(latestOverallVersionString.lastIndexOf("."), latestOverallVersionString.length()).replaceAll("[^\\d]", "");

        int latestVersion = Integer.parseInt(latestVersionString);
        int latestUpgrade = Integer.parseInt(latestUpgradeString);
        int latestUpdate = Integer.parseInt(latestUpdateString);
        latestVersionTotal = (latestVersion * 100) + (latestUpgrade * 10) + (latestUpdate * 1);
        latestOverallVersionString = latestVersionString + "." + latestUpgradeString + "." + latestUpdateString;
        latestVersionIsKnown = true;
        return latestOverallVersionString;
    }

    public String getLatestOnlineOverallVersionString() { return latestOverallVersionString; }
    public String getCurrentlyInstalledOverallVersionString() { return currentOverallVersionString; }

    public String getUpdateStatus() 
    {
        String returnString = "";
        if (( currentVersionIsKnown) && ( latestVersionIsKnown))
        {
            if      (currentVersionTotal < latestVersionTotal)
            {
                returnString += getProcuct() + " " + currentOverallVersionString + " can be updated to version: " + latestOverallVersionString + " at: " + REMOTEPACKAGEDOWNLOADURISTRING + "\n"; 

            } 
            else if (currentVersionTotal > latestVersionTotal)
            {
                returnString += getProcuct() + " " + currentOverallVersionString + " is a development version!\n";
            } 
            else
            {
                returnString += getProcuct() + " " + currentOverallVersionString + " is up to date\n";
            } 
        }
        else
        {
            if (!currentVersionIsKnown)   { returnString = "Could not retrieve the locally installed " + Version.getProcuct() + " Version\n"; }
            if (!latestVersionIsKnown)    { returnString = "Could not retrieve the latest online " + Version.getProcuct() + " Version\n"; }
        }
        return returnString;
    }

    public boolean versionIsDifferent()     { if      ( currentVersionTotal != latestVersionTotal ) { return true; } else { return false; } }
    public boolean versionCanBeUpdated()    { if      ( currentVersionTotal < latestVersionTotal )  { return true; } else { return false; } }
    public boolean versionIsDevelopment()   { if      ( currentVersionTotal > latestVersionTotal )  { return true; } else { return false; } }    

    public static String getCopyleft()                      { return COPYLEFT; }
    public static String getAuthor()                        { return AUTHOR; }
    public static String getProcuct()                       { return PRODUCTNAME; }
    public static String getCompany()                       { return COMPANYNAME; }
}
