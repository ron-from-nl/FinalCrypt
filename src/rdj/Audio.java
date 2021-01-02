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

public class Audio
{
    public static final int WAV =				0;
    public static final int OGG =				1; // Not supported
    public static final int AIFF =				2;
    public static final int MP3 =				3;
    public static final int AUDIO_CODEC =			AIFF;

//  JavaFX Audio Support
    
//  MP3;
//  AIFF containing uncompressed PCM
//  WAV containing uncompressed PCM
//  MPEG-4 multimedia container with Advanced Audio Coding (AAC) audio    

    public static final String SND_ALARM =			    "/rdj/audio/sounds/alarm";
    public static final String SND_ALERT =			    "/rdj/audio/sounds/alert";
    public static final String SND_BUTTON =			    "/rdj/audio/sounds/button";
    public static final String SND_DECRYPTFILES =		    "/rdj/audio/sounds/decrypt_files";
    public static final String SND_ENCRYPTFILES =		    "/rdj/audio/sounds/encrypt_files";
    public static final String SND_ERROR =			    "/rdj/audio/sounds/error";
    public static final String SND_INPUT_FAIL =			    "/rdj/audio/sounds/input_fail";
    public static final String SND_INPUT_OK =			    "/rdj/audio/sounds/input_ok";
    public static final String SND_KEYPRESS =			    "/rdj/audio/sounds/key_press";
    public static final String SND_MESSAGE =			    "/rdj/audio/sounds/message";
    public static final String SND_OFF =			    "/rdj/audio/sounds/off";
    public static final String SND_ON =				    "/rdj/audio/sounds/on";
    public static final String SND_OPEN =			    "/rdj/audio/sounds/open";
    public static final String SND_READY =			    "/rdj/audio/sounds/ready";
    public static final String SND_SELECT =			    "/rdj/audio/sounds/select";
    public static final String SND_SELECTINVALID =		    "/rdj/audio/sounds/select_invalid";
    public static final String SND_SELECTKEY =			    "/rdj/audio/sounds/select_key";
    public static final String SND_SHUTDOWN =			    "/rdj/audio/sounds/shutdown";
    public static final String SND_SOUND_DISABLED =		    "/rdj/audio/sounds/sound_disabled";
    public static final String SND_SOUND_ENABLED =		    "/rdj/audio/sounds/sound_enabled";
//    public static final String SND_STARTUP =			    "/rdj/audio/sounds/startup";
    public static final String SND_TYPEWRITE =			    "/rdj/audio/sounds/typewriter";

    public static final String VOI_CLONE_KEY_DEVICE =		    "/rdj/audio/voice/clone_key_device";
    public static final String VOI_CONFIRM_PASS_WITH_ENTER =	    "/rdj/audio/voice/confirm_password_with_enter";
    public static final String VOI_CREATE_KEY =			    "/rdj/audio/voice/create_key";
    public static final String VOI_CREATE_KEY_DEVICE =		    "/rdj/audio/voice/create_key_device";
    public static final String VOI_DECRYPT_FILES =		    "/rdj/audio/voice/decrypt_files";
    public static final String VOI_DECRYPTING_FILES =		    "/rdj/audio/voice/decrypting_files";
    public static final String VOI_ENCRYPT_FILES =		    "/rdj/audio/voice/encrypt_files";
    public static final String VOI_ENCRYPTING_FILES =		    "/rdj/audio/voice/encrypting_files";
    public static final String VOI_ENCRYPT_OR_DECRYPT_FILES =	    "/rdj/audio/voice/encrypt_or_decrypt_files";
    public static final String VOI_FINISHED_DECRYPTING =	    "/rdj/audio/voice/finished_decrypting";
    public static final String VOI_FINISHED_ENCRYPTING =	    "/rdj/audio/voice/finished_encrypting";
    public static final String VOI_SCANNING_FILES =		    "/rdj/audio/voice/scanning_files";
    public static final String VOI_SELECT_FILES =		    "/rdj/audio/voice/select_files";
    public static final String VOI_SELECT_KEY =			    "/rdj/audio/voice/select_key";
    public static final String VOI_SELECT_KEY_DIRECTORY =	    "/rdj/audio/voice/select_key_directory";
    public static final String VOI_VOICE_DISABLED =		    "/rdj/audio/voice/voice_disabled";
    public static final String VOI_VOICE_ENABLED =		    "/rdj/audio/voice/voice_enabled";
    public static final String VOI_WRONG_KEY_OR_PASSWORD =	    "/rdj/audio/voice/wrong_key_or_password";

    public  static boolean sound_Is_Enabled =			    true;
    public  static boolean voice_Is_Enabled =			    true;

    public static final String[] soundArray = {SND_ALARM,SND_ALERT,SND_BUTTON,SND_DECRYPTFILES,SND_ENCRYPTFILES,SND_ERROR,SND_INPUT_FAIL,SND_INPUT_OK,SND_KEYPRESS,SND_MESSAGE,SND_OFF,SND_ON,SND_OPEN,SND_READY,SND_SELECT,SND_SELECTINVALID,SND_SELECTKEY,SND_SOUND_DISABLED,SND_SOUND_ENABLED,SND_SHUTDOWN,SND_TYPEWRITE};

    public static String getSound(int sound) { if ((sound >= 0) && (sound < soundArray.length)) { return soundArray[sound]; } else { return SND_TYPEWRITE; } }
    public static String getSounds()
    {
	String returnString = "\r\n";
	for (int x=0; x<soundArray.length;x++)
	{
	    returnString += x + ".  " + soundArray[x] + "\r\n";
	}
	return returnString;
    }
    
    public static void main(String[] args)
    {
	if	(args.length == 0)  { usage("Error: no parameter", true); }
	else if (args.length == 1)
	{
	    if ( args[0].equalsIgnoreCase("--list") ) { System.out.println(getSounds()); }
	    else { usage("Error: invalid parameter: \"" + args[0] + "\"", true); }
	}
	else			    { new TypeWriter().usage("Too many parameters", true); }	
    }

    protected static void usage(String errorMessage, boolean error)
    {
	if ( errorMessage.length() > 0 )
	{
	    System.out.println("\r\n");
	    System.out.println(errorMessage + "\r\n");
	}

	System.out.println("\r\n");
	System.out.println("Usage: java -cp finalcrypt.jar rdj/Audio --list # List available sounds\r\n");
	System.out.println("\r\n");
        System.exit(error ? 1 : 0);
    }
}
