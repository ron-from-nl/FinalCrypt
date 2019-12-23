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
import java.net.*;
import javafx.application.*;
import javafx.scene.media.*;
import javax.sound.sampled.*;

public class Voice extends Audio
{
    public static AudioClip audioClipSounds;
    public static AudioClip audioClipVoice;
    
    private static AudioInputStream audioInputStreamSounds;
    private static AudioInputStream audioInputStreamVoice;
    private static Clip clipSounds;
    private static Clip clipVoice;

    synchronized public static void play(UI ui, String audio, int audio_codec)
    {
	Media media;
	switch (audio_codec)
	{
	    case WAV:		media = new Media(ui.getClass().getResource(audio + ".wav").toExternalForm());		break;
	    case OGG:		media = new Media(ui.getClass().getResource(audio + ".ogg").toExternalForm());		break;
	    case AIFF:		media = new Media(ui.getClass().getResource(audio + ".aiff").toExternalForm());		break;
	    case MP3:		media = new Media(ui.getClass().getResource(audio + ".mp3").toExternalForm());		break;
	    default:		media = new Media(ui.getClass().getResource(audio + ".wav").toExternalForm());		break;
	}
//	playJavaX(ui, media, audio_codec);  // Stops playing spontaniously on all OSes
	playJavaFX(ui, media, audio_codec); // Plays fast, but crashes often on Linux (libld.so native player issue)
    }
    
    synchronized public static void playJavaX(UI ui, Media media, int audio_codec) // javax.sound.sampled.AudioSystem
    {
//	Thread playThread = new Thread(() ->
//	{
	    if (media != null) 
	    {
		if (media.getSource().contains("sounds")) // Just to keep playing ClipSounds parallel over ClipVoice
		{
		    if (sound_Is_Enabled)
		    {
			try { audioInputStreamSounds = AudioSystem.getAudioInputStream(new URL(media.getSource())); }
			catch (UnsupportedAudioFileException ex) { ui.log("Error: UnsupportedAudioFileException " + Voice.class.getSimpleName() + ".play(..) AudioSystem.getAudioInputStream(" + media.getSource() + " " + ex.getMessage() + " \r\n", true, true, true, true, false); }
			catch (IOException ex) { ui.log("Error: IOException " + Voice.class.getSimpleName() + ".play(..) AudioSystem.getAudioInputStream(" + media.getSource() + " " + ex.getMessage() + " \r\n", true, true, true, true, false); }

			clipSounds = null; try {  clipSounds = AudioSystem.getClip(); } catch (LineUnavailableException ex)
			{ ui.log("Error: LineUnavailableException " + Voice.class.getSimpleName() + ".play(..).AudioSystem.getClip() " + ex.getMessage() + " \r\n", true, true, true, true, false); }

			try { clipSounds.open(audioInputStreamSounds); } 
			catch (LineUnavailableException ex)	{ clipSounds.close(); clipSounds = null; ui.log("Error: LineUnavailableException " + Voice.class.getSimpleName() + ".play(..).clipSounds.open(" + media.getSource() + ") " + ex.getMessage() + " \r\n", true, true, true, false, false); }
			catch (IOException ex)		{ clipSounds.close(); clipSounds = null; ui.log("Error: IOException " + Voice.class.getSimpleName() + ".play(..).clipSounds.open(" + media.getSource() + ") " + ex.getMessage() + " \r\n", true, true, true, false, false); }

			if ( clipSounds != null )
			{
			    clipSounds.start();
			    try { audioInputStreamSounds.close(); } catch (IOException ex) { ui.log("Error: IOException " + Voice.class.getSimpleName() + ".play(..).audioInputStreamSounds.close() " + ex.getMessage() + " \r\n", true, true, true, false, false); }
			}
			else
			{
			    try { audioInputStreamSounds.close(); } catch (IOException ex) { ui.log("Error: IOException " + Voice.class.getSimpleName() + ".play(..).audioInputStreamSounds.close() " + ex.getMessage() + " \r\n", true, true, true, false, false); }			
			}
			// test(" " + clipSounds.isOpen() + "\r\n");
		    }
		}
		else if ( media.getSource().contains("voice") )
		{
		    if (voice_Is_Enabled)
		    {
			if ((clipVoice != null) && ( clipVoice.isOpen() )) // new voice stopping currently playing voice
			{
			    clipVoice.stop(); try { audioInputStreamVoice.close(); } catch (IOException ex) { ui.log("Error: IOException audioIn.close() " + ex.getMessage() + " \r\n", true, true, true, true, false); }
			}
			try { audioInputStreamVoice = AudioSystem.getAudioInputStream(new URL(media.getSource())); }
			catch (UnsupportedAudioFileException ex) { ui.log("Error: UnsupportedAudioFileException play(..) AudioSystem.getAudioInputStream(" + media.getSource() + " " + ex.getMessage() + " \r\n", true, true, true, true, false); }
			catch (IOException ex) { ui.log("Error: IOException " + Voice.class.getSimpleName() + ".play(..) AudioSystem.getAudioInputStream(" + media.getSource() + " " + ex.getMessage() + " \r\n", true, true, true, true, false); }

			clipVoice = null; try {  clipVoice = AudioSystem.getClip(); } catch (LineUnavailableException ex) { ui.log("Error: LineUnavailableException " + Voice.class.getSimpleName() + ".play(..).AudioSystem.getClip() " + ex.getMessage() + " \r\n", true, true, true, true, false); }

			try { clipVoice.open(audioInputStreamVoice); }
			catch (LineUnavailableException ex) { clipVoice.close(); clipVoice = null; ui.log("Error: LineUnavailableException " + Voice.class.getSimpleName() + ".play(..).clipVoice.open(" + media.getSource() + ") " + ex.getMessage() + " \r\n", true, true, true, false, false); }
			catch (IOException ex)		{ clipVoice.close(); clipVoice = null; ui.log("Error: IOException " + Voice.class.getSimpleName() + ".play(..).clipVoice.open(" + media.getSource() + ") " + ex.getMessage() + " \r\n", true, true, true, false, false); }

			if ( clipVoice != null )
			{
			    clipVoice.start();
			    try { audioInputStreamVoice.close(); } catch (IOException ex) { ui.log("Error: IOException " + Voice.class.getSimpleName() + ".play(..).audioInputStreamVoice.close() " + ex.getMessage() + " \r\n", true, true, true, false, false); }
			}
			else
			{
			    try { audioInputStreamVoice.close(); } catch (IOException ex) { ui.log("Error: IOException " + Voice.class.getSimpleName() + ".play(..).audioInputStreamVoice.close() " + ex.getMessage() + " \r\n", true, true, true, false, false); }			
			}
			// test(" " + clipVoice.isOpen() + "\r\n");
		    }
		}
		else { ui.log("Alert: " + Voice.class.getSimpleName() + ".play(" + media.getSource() + ") not recognized!\r\n", true, true, true, true, false); }
	    }
//	});
//	playThread.setName("playThread");
//	playThread.setDaemon(true);
//	playThread.start();	
    }

    synchronized public static void playJavaFX(UI ui, Media media, int audio_codec) // javafx.scene.media.AudioClip
    {
//	Thread playThread = new Thread(() ->
//	{
	    if (media != null) 
	    {
		if ( (media.getSource().contains("sounds")) ) // new sound added to any other audio playing
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
		else if ( media.getSource().contains("voice") )
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
		else { ui.log("Alert: " + Voice.class.getSimpleName() + ".play(" + media.getSource() + ") not recognized!\r\n", true, true, true, true, false); }
	    }
//	});
//	playThread.setName("playThread");
//	playThread.setDaemon(true);
//	playThread.start();	
    }
}
