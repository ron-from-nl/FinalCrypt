/*
 * Copyright © 2017 Ron de Jong (ronuitzaandam@gmail.com).
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

import javafx.application.*;
import javafx.scene.media.*;

public class Audio
{
    public static final int WAV =				0;
    public static final int OGG =				1; // Not supported
    public static final int AIFF =				2;
    public static final int MP3 =				3;
    public static final int AUDIO_CODEC =			AIFF;

//    MP3;
//    AIFF containing uncompressed PCM
//    WAV containing uncompressed PCM
//    MPEG-4 multimedia container with Advanced Audio Coding (AAC) audio    

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
    public static final String SND_SOUND_DISABLED =		    "/rdj/audio/sounds/sound_disabled";
    public static final String SND_SOUND_ENABLED =		    "/rdj/audio/sounds/sound_enabled";
    public static final String SND_SHUTDOWN =			    "/rdj/audio/sounds/shutdown";
    public static final String SND_STARTUP =			    "/rdj/audio/sounds/startup";

    public static final String VOI_CLONE_KEY_DEVICE =		    "/rdj/audio/voice/clone_key_device";
    public static final String VOI_CONFIRM_PASS_WITH_ENTER =	    "/rdj/audio/voice/confirm_password_with_enter";
    public static final String VOI_CREATE_KEY =			    "/rdj/audio/voice/create_key";
    public static final String VOI_CREATE_KEY_DEVICE =		    "/rdj/audio/voice/create_key_device";
    public static final String VOI_DECRYPT_FILES =		    "/rdj/audio/voice/decrypt_files";
    public static final String VOI_DECRYPTING_FILES =		    "/rdj/audio/voice/decrypting_files";
    public static final String VOI_ENCRYPT_FILES =		    "/rdj/audio/voice/encrypt_files";
    public static final String VOI_ENCRYPTING_FILES =		    "/rdj/audio/voice/encrypting_files";
    public static final String VOI_ENCRYPT_OR_DECRYPT_FILES =	    "/rdj/audio/voice/encrypt_or_decrypt_files";
    public static final String VOI_SCANNING_FILES =		    "/rdj/audio/voice/scanning_files";
    public static final String VOI_SELECT_FILES =		    "/rdj/audio/voice/select_files";
    public static final String VOI_SELECT_KEY =			    "/rdj/audio/voice/select_key";
    public static final String VOI_VOICE_DISABLED =		    "/rdj/audio/voice/voice_disabled";
    public static final String VOI_VOICE_ENABLED =		    "/rdj/audio/voice/voice_enabled";
    public static final String VOI_WRONG_KEY_OR_PASSWORD =	    "/rdj/audio/voice/wrong_key_or_password";

    public static AudioClip audioClipSounds;
    public static AudioClip audioClipVoice;
    
//    private static AudioInputStream audioInputStreamSounds;
//    private static AudioInputStream audioInputStreamVoice;
//    private static Clip clipSounds;
//    private static Clip clipVoice;

    public  static boolean sound_Is_Enabled =		true;
    public  static boolean voice_Is_Enabled =		true;
    
//    synchronized public void play(String audio, int audio_codec)
//    {
//	Media media;
//	switch (audio_codec)
//	{
//	    case WAV:		media = new Media(getClass().getResource(audio + ".wav").toExternalForm());		break;
//	    case OGG:		media = new Media(getClass().getResource(audio + ".ogg").toExternalForm());		break;
//	    case MP3:		media = new Media(getClass().getResource(audio + ".mp3").toExternalForm());		break;
//	    default:		media = new Media(getClass().getResource(audio + ".wav").toExternalForm());		break;
//	}
//	
//	if (audio != null) 
//	{
//	    if (audio.contains("sounds")) // Just to keep playing ClipSounds parallel over ClipVoice
//	    {
//		if (sound_Is_Enabled)
//		{
//		    try { audioInputStreamSounds = AudioSystem.getAudioInputStream(new URL(media.getSource())); }
//		    catch (UnsupportedAudioFileException ex) { log("Error: UnsupportedAudioFileException " + this.getClass().getSimpleName() + ".play(..) AudioSystem.getAudioInputStream(" + media.getSource() + " " + ex.getMessage() + " \r\n", true, true, true, true, false); }
//		    catch (IOException ex) { log("Error: IOException " + this.getClass().getSimpleName() + ".play(..) AudioSystem.getAudioInputStream(" + media.getSource() + " " + ex.getMessage() + " \r\n", true, true, true, true, false); }
//
//		    clipSounds = null; try {  clipSounds = AudioSystem.getClip(); } catch (LineUnavailableException ex)
//		    { log("Error: LineUnavailableException " + this.getClass().getSimpleName() + ".play(..).AudioSystem.getClip() " + ex.getMessage() + " \r\n", true, true, true, true, false); }
//
//		    try { clipSounds.open(audioInputStreamSounds); } 
//		    catch (LineUnavailableException ex)	{ clipSounds.close(); clipSounds = null; log("Error: LineUnavailableException " + this.getClass().getSimpleName() + ".play(..).clipSounds.open(" + media.getSource() + ") " + ex.getMessage() + " \r\n", true, true, true, false, false); }
//		    catch (IOException ex)		{ clipSounds.close(); clipSounds = null; log("Error: IOException " + this.getClass().getSimpleName() + ".play(..).clipSounds.open(" + media.getSource() + ") " + ex.getMessage() + " \r\n", true, true, true, false, false); }
//
//		    if ( clipSounds != null )
//		    {
//			clipSounds.start();
//			try { audioInputStreamSounds.close(); } catch (IOException ex) { log("Error: IOException " + this.getClass().getSimpleName() + ".play(..).audioInputStreamSounds.close() " + ex.getMessage() + " \r\n", true, true, true, false, false); }
//		    }
//		    else
//		    {
//			try { audioInputStreamSounds.close(); } catch (IOException ex) { log("Error: IOException " + this.getClass().getSimpleName() + ".play(..).audioInputStreamSounds.close() " + ex.getMessage() + " \r\n", true, true, true, false, false); }			
//		    }
//		    // test(" " + clipSounds.isOpen() + "\r\n");
//		}
//	    }
//	    else if ( audio.contains("voice") )
//	    {
//		if (voice_Is_Enabled)
//		{
//		    if ((clipVoice != null) && ( clipVoice.isOpen() )) // new voice stopping currently playing voice
//		    {
//			clipVoice.stop(); try { audioInputStreamVoice.close(); } catch (IOException ex) { log("Error: IOException audioIn.close() " + ex.getMessage() + " \r\n", true, true, true, true, false); }
//		    }
//		    try { audioInputStreamVoice = AudioSystem.getAudioInputStream(new URL(media.getSource())); }
//		    catch (UnsupportedAudioFileException ex) { log("Error: UnsupportedAudioFileException play(..) AudioSystem.getAudioInputStream(" + media.getSource() + " " + ex.getMessage() + " \r\n", true, true, true, true, false); }
//		    catch (IOException ex) { log("Error: IOException " + this.getClass().getSimpleName() + ".play(..) AudioSystem.getAudioInputStream(" + media.getSource() + " " + ex.getMessage() + " \r\n", true, true, true, true, false); }
//
//		    clipVoice = null; try {  clipVoice = AudioSystem.getClip(); } catch (LineUnavailableException ex) { log("Error: LineUnavailableException " + this.getClass().getSimpleName() + ".play(..).AudioSystem.getClip() " + ex.getMessage() + " \r\n", true, true, true, true, false); }
//
//		    try { clipVoice.open(audioInputStreamVoice); }
//		    catch (LineUnavailableException ex) { clipVoice.close(); clipVoice = null; log("Error: LineUnavailableException " + this.getClass().getSimpleName() + ".play(..).clipVoice.open(" + media.getSource() + ") " + ex.getMessage() + " \r\n", true, true, true, false, false); }
//		    catch (IOException ex)		{ clipVoice.close(); clipVoice = null; log("Error: IOException " + this.getClass().getSimpleName() + ".play(..).clipVoice.open(" + media.getSource() + ") " + ex.getMessage() + " \r\n", true, true, true, false, false); }
//
//		    if ( clipVoice != null )
//		    {
//			clipVoice.start();
//			try { audioInputStreamVoice.close(); } catch (IOException ex) { log("Error: IOException " + this.getClass().getSimpleName() + ".play(..).audioInputStreamVoice.close() " + ex.getMessage() + " \r\n", true, true, true, false, false); }
//		    }
//		    else
//		    {
//			try { audioInputStreamVoice.close(); } catch (IOException ex) { log("Error: IOException " + this.getClass().getSimpleName() + ".play(..).audioInputStreamVoice.close() " + ex.getMessage() + " \r\n", true, true, true, false, false); }			
//		    }
//		    // test(" " + clipVoice.isOpen() + "\r\n");
//		}
//	    }
//	    else { log("Alert: " + this.getClass().getSimpleName() + ".play(" + media.getSource() + ") not recognized!\r\n", true, true, true, true, false); }
//	}
//    }

    synchronized public static void play(UI ui, String audio, int audio_codec)
    {
	Media media;
	switch (audio_codec)
	{
	    case WAV:		media = new Media(Audio.class.getClassLoader().getResource(audio + ".wav").toExternalForm());		break;
	    case OGG:		media = new Media(Audio.class.getClassLoader().getResource(audio + ".ogg").toExternalForm());		break;
//	    case AIFF:		media = new Media(Audio.class.getClassLoader().getResource(audio + ".aiff").toExternalForm());		break;
	    case AIFF:		media = new Media(ui.getClass().getResource(audio + ".aiff").toExternalForm());		break;
	    case MP3:		media = new Media(Audio.class.getClassLoader().getResource(audio + ".mp3").toExternalForm());		break;
	    default:		media = new Media(Audio.class.getClassLoader().getResource(audio + ".wav").toExternalForm());		break;
	}
	
	if (audio != null) 
	{
	    if ( (audio.contains("sounds")) ) // new sound added to any other audio playing
	    {
		if (sound_Is_Enabled)
		{
		    Platform.runLater(new Runnable(){ @Override public void run()
		    {
			if (media != null) 
			{
			    if ( media.getSource().contains("sounds") )
			    {
				Thread playSoundThread = new Thread(() ->
				{
				    if (sound_Is_Enabled) { audioClipSounds = new AudioClip(media.getSource()); audioClipSounds.play(); /*(" " + play.isPlaying() + "\r\n");*/ }
				});
				playSoundThread.setName("playSoundThread");
				playSoundThread.setDaemon(true);
				playSoundThread.start();
			    }
			    else if ( media.getSource().contains("voice") )
			    {
				if ( voice_Is_Enabled)
				{
				    if ( (audioClipVoice != null) && ( audioClipVoice.isPlaying() )) { audioClipVoice.stop(); }
				    audioClipVoice = new AudioClip(media.getSource()); audioClipVoice.play(); /*test(" " + play.isPlaying() + "\r\n");*/ 
				}
			    }
			    else { ui.log("Alert: play(" + media.getSource() + ") not recognized!\r\n", true, true, true, true, false); }
			}
		    }});
		}
	    }
	    else if ( audio.contains("voice") )
	    {
		if (voice_Is_Enabled)
		{
		    if ((audioClipSounds != null) && ( audioClipSounds.isPlaying() )) // new voice stopping currently playing voice
		    {
			audioClipSounds.stop();
		    }
		    Platform.runLater(new Runnable(){ @Override public void run()
		    {
			if (media != null) 
			{
			    if ( media.getSource().contains("sounds") )
			    {
				Thread playVoiceThread = new Thread(() ->
				{
				    if (sound_Is_Enabled) { audioClipSounds = new AudioClip(media.getSource()); audioClipSounds.play(); /*(" " + play.isPlaying() + "\r\n");*/ }
				});
				playVoiceThread.setName("playVoiceThread");
				playVoiceThread.setDaemon(true);
				playVoiceThread.start();
			    }
			    else if ( media.getSource().contains("voice") )
			    {
				if ( voice_Is_Enabled)
				{
				    if ( (audioClipVoice != null) && ( audioClipVoice.isPlaying() )) { audioClipVoice.stop(); }
				    audioClipVoice = new AudioClip(media.getSource()); audioClipVoice.play(); /*test(" " + play.isPlaying() + "\r\n");*/ 
				}
			    }
			    else { ui.log("Alert: play(" + media.getSource() + ") not recognized!\r\n", true, true, true, true, false); }
			}
		    }});
		}
	    }
	    else { ui.log("Alert: " + Audio.class.getSimpleName() + ".play(" + media.getSource() + ") not recognized!\r\n", true, true, true, true, false); }
	}
    }
}
