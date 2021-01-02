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

import java.io.*;
import java.net.*;
import javafx.scene.media.*;
import javax.sound.sampled.*;

public class AudioPlayer extends Audio
{
    public AudioClip audioClip;
    public AudioClip audioClipVoice;
    
    private AudioInputStream audioInputStreamSounds;
    private AudioInputStream audioInputStreamVoice;
    private Clip clipSounds;
    private Clip clipVoice;

    private boolean isInt(String text) { try { Integer.parseInt(text); return true;} catch (NumberFormatException e) { return false; } }

    synchronized public void play(UI ui, String audio, int audio_codec)
    {
	Media media;
	if (!isInt(audio))
	{
	    switch (audio_codec)
	    {
		case WAV:		media = new Media(this.getClass().getResource(audio + ".wav").toExternalForm());		break;
		case OGG:		media = new Media(this.getClass().getResource(audio + ".ogg").toExternalForm());		break;
		case AIFF:		media = new Media(this.getClass().getResource(audio + ".aiff").toExternalForm());		break;
		case MP3:		media = new Media(this.getClass().getResource(audio + ".mp3").toExternalForm());		break;
		default:		media = new Media(this.getClass().getResource(audio + ".wav").toExternalForm());		break;
	    }

	    if  (
			((media.getSource().contains("/sounds/")) && ((sound_Is_Enabled)))
		    ||  ((media.getSource().contains("/voice/")) && ((voice_Is_Enabled)))
		)
	    {
		playAudioClip(ui, media, audio_codec); // Plays fast, but crashes often on Linux (libld.so native player issue)
	    }
	}
    }
        
    synchronized public void playAudioClipFile(UI ui, String fileParam) // just for external files
    {
	Thread playAudioClipFileThread = new Thread(() ->
	{
	    File file = new File(fileParam);
	    Media media = null;
	    try { media = new Media(file.toURI().toURL().toString()); } catch (MalformedURLException ex) { ui.log("Error: MalformedURLException: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    audioClip = new AudioClip(media.getSource());
	    audioClip.play();
	});
	playAudioClipFileThread.setName("playAudioClipFileThread");
	playAudioClipFileThread.setDaemon(true);
	playAudioClipFileThread.start();
    }

    synchronized public void playAudioClip(UI ui, Media media, int audio_codec) // javafx.scene.media.AudioClip
    {
	if (media != null) 
	{
	    if ( (media.getSource().contains("/sounds/")) ) // new sound added to any other audio playing
	    {
		if (sound_Is_Enabled)
		{
		    Thread playAudioClipSounds = new Thread(() ->
		    {
			    if (sound_Is_Enabled) { audioClip = new AudioClip(media.getSource()); audioClip.play(); /*(" " + play.isPlaying() + "\r\n");*/ }
		    });
		    playAudioClipSounds.setName("playAudioClipSounds");
		    playAudioClipSounds.setDaemon(true);
		    playAudioClipSounds.start();
		}
	    }
	    else if ( media.getSource().contains("/voice/") )
	    {
		if (voice_Is_Enabled)
		{
		    if ((audioClip != null) && ( audioClip.isPlaying() )) { audioClip.stop(); } // new voice stopping currently playing voice

		    Thread playJavaFXVoiceThread = new Thread(() ->
		    {
			if ( (audioClipVoice != null) && ( audioClipVoice.isPlaying() )) { audioClipVoice.stop(); }
			audioClipVoice = new AudioClip(media.getSource()); audioClipVoice.play(); /*test(" " + play.isPlaying() + "\r\n");*/ 
		    });
		    playJavaFXVoiceThread.setName("playJavaFXVoiceThread");
		    playJavaFXVoiceThread.setDaemon(true);
		    playJavaFXVoiceThread.start();
		}
	    }
	    else { ui.log("Alert: " + AudioPlayer.class.getSimpleName() + ".play(" + media.getSource() + ") not recognized!\r\n", true, true, true, true, false); }
	}
    }    

    synchronized public void playJavaX(UI ui, Media media, int audio_codec) // javax.sound.sampled.AudioSystem - Stops playing spontaniously on all OSes
    {
	if (media != null) 
	{
	    if (media.getSource().contains("/sounds/")) // Just to keep playing ClipSounds parallel over ClipVoice
	    {
		if (sound_Is_Enabled)
		{
		    try { audioInputStreamSounds = AudioSystem.getAudioInputStream(new URL(media.getSource())); }
		    catch (UnsupportedAudioFileException ex) { ui.log("Error: UnsupportedAudioFileException " + AudioPlayer.class.getSimpleName() + ".play(..) AudioSystem.getAudioInputStream(" + media.getSource() + " " + ex.getMessage() + " \r\n", true, true, true, true, false); }
		    catch (IOException ex) { ui.log("Error: IOException " + AudioPlayer.class.getSimpleName() + ".play(..) AudioSystem.getAudioInputStream(" + media.getSource() + " " + ex.getMessage() + " \r\n", true, true, true, true, false); }

		    clipSounds = null; try {  clipSounds = AudioSystem.getClip(); } catch (LineUnavailableException ex)
		    { ui.log("Error: LineUnavailableException " + AudioPlayer.class.getSimpleName() + ".play(..).AudioSystem.getClip() " + ex.getMessage() + " \r\n", true, true, true, true, false); }

		    try { clipSounds.open(audioInputStreamSounds); } 
		    catch (LineUnavailableException ex)	{ clipSounds.close(); clipSounds = null; ui.log("Error: LineUnavailableException " + AudioPlayer.class.getSimpleName() + ".play(..).clipSounds.open(" + media.getSource() + ") " + ex.getMessage() + " \r\n", true, true, true, false, false); }
		    catch (IOException ex)		{ clipSounds.close(); clipSounds = null; ui.log("Error: IOException " + AudioPlayer.class.getSimpleName() + ".play(..).clipSounds.open(" + media.getSource() + ") " + ex.getMessage() + " \r\n", true, true, true, false, false); }

		    if ( clipSounds != null )
		    {
			clipSounds.start();
			try { audioInputStreamSounds.close(); } catch (IOException ex) { ui.log("Error: IOException " + AudioPlayer.class.getSimpleName() + ".play(..).audioInputStreamSounds.close() " + ex.getMessage() + " \r\n", true, true, true, false, false); }
		    }
		    else
		    {
			try { audioInputStreamSounds.close(); } catch (IOException ex) { ui.log("Error: IOException " + AudioPlayer.class.getSimpleName() + ".play(..).audioInputStreamSounds.close() " + ex.getMessage() + " \r\n", true, true, true, false, false); }			
		    }
		    // test(" " + clipSounds.isOpen() + "\r\n");
		}
	    }
	    else if ( media.getSource().contains("/voice/") )
	    {
		if (voice_Is_Enabled)
		{
		    if ((clipVoice != null) && ( clipVoice.isOpen() )) // new voice stopping currently playing voice
		    {
			clipVoice.stop(); try { audioInputStreamVoice.close(); } catch (IOException ex) { ui.log("Error: IOException audioIn.close() " + ex.getMessage() + " \r\n", true, true, true, true, false); }
		    }
		    try { audioInputStreamVoice = AudioSystem.getAudioInputStream(new URL(media.getSource())); }
		    catch (UnsupportedAudioFileException ex) { ui.log("Error: UnsupportedAudioFileException play(..) AudioSystem.getAudioInputStream(" + media.getSource() + " " + ex.getMessage() + " \r\n", true, true, true, true, false); }
		    catch (IOException ex) { ui.log("Error: IOException " + AudioPlayer.class.getSimpleName() + ".play(..) AudioSystem.getAudioInputStream(" + media.getSource() + " " + ex.getMessage() + " \r\n", true, true, true, true, false); }

		    clipVoice = null; try {  clipVoice = AudioSystem.getClip(); } catch (LineUnavailableException ex) { ui.log("Error: LineUnavailableException " + AudioPlayer.class.getSimpleName() + ".play(..).AudioSystem.getClip() " + ex.getMessage() + " \r\n", true, true, true, true, false); }

		    try { clipVoice.open(audioInputStreamVoice); }
		    catch (LineUnavailableException ex) { clipVoice.close(); clipVoice = null; ui.log("Error: LineUnavailableException " + AudioPlayer.class.getSimpleName() + ".play(..).clipVoice.open(" + media.getSource() + ") " + ex.getMessage() + " \r\n", true, true, true, false, false); }
		    catch (IOException ex)		{ clipVoice.close(); clipVoice = null; ui.log("Error: IOException " + AudioPlayer.class.getSimpleName() + ".play(..).clipVoice.open(" + media.getSource() + ") " + ex.getMessage() + " \r\n", true, true, true, false, false); }

		    if ( clipVoice != null )
		    {
			clipVoice.start();
			try { audioInputStreamVoice.close(); } catch (IOException ex) { ui.log("Error: IOException " + AudioPlayer.class.getSimpleName() + ".play(..).audioInputStreamVoice.close() " + ex.getMessage() + " \r\n", true, true, true, false, false); }
		    }
		    else
		    {
			try { audioInputStreamVoice.close(); } catch (IOException ex) { ui.log("Error: IOException " + AudioPlayer.class.getSimpleName() + ".play(..).audioInputStreamVoice.close() " + ex.getMessage() + " \r\n", true, true, true, false, false); }			
		    }
		    // test(" " + clipVoice.isOpen() + "\r\n");
		}
	    }
	    else { ui.log("Alert: " + AudioPlayer.class.getSimpleName() + ".play(" + media.getSource() + ") not recognized!\r\n", true, true, true, true, false); }
	}
    }
}
