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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.*;
import javafx.application.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.*;
import javafx.stage.Stage;
import javax.sound.sampled.*;

public class Support extends Application implements Initializable
{
//    private final String SND_ALARM =			    "/rdj/audio/sounds/alarm";
//    private final String SND_ALERT =			    "/rdj/audio/sounds/alert";
//    private final String SND_BUTTON =			    "/rdj/audio/sounds/button";
//    private final String SND_DECRYPTFILES =		    "/rdj/audio/sounds/decrypt_files";
//    private final String SND_ENCRYPTFILES =		    "/rdj/audio/sounds/encrypt_files";
//    private final String SND_ERROR =			    "/rdj/audio/sounds/error";
//    private final String SND_INPUT_FAIL =		    "/rdj/audio/sounds/input_fail";
//    private final String SND_INPUT_OK =			    "/rdj/audio/sounds/input_ok";
//    private final String SND_KEYPRESS =			    "/rdj/audio/sounds/key_press";
//    private final String SND_MESSAGE =			    "/rdj/audio/sounds/message";
//    private final String SND_OFF =			    "/rdj/audio/sounds/off";
//    private final String SND_ON =			    "/rdj/audio/sounds/on";
//    private final String SND_OPEN =			    "/rdj/audio/sounds/open";
//    private final String SND_READY =			    "/rdj/audio/sounds/ready";
//    private final String SND_SELECT =			    "/rdj/audio/sounds/select";
//    private final String SND_SELECTINVALID =		    "/rdj/audio/sounds/select_invalid";
//    private final String SND_SELECTKEY =			    "/rdj/audio/sounds/select_key";
//    private final String SND_SOUND_DISABLED =		    "/rdj/audio/sounds/sound_disabled";
//    private final String SND_SOUND_ENABLED =		    "/rdj/audio/sounds/sound_enabled";
//    private final String SND_SHUTDOWN =			    "/rdj/audio/sounds/shutdown";
//    private final String SND_STARTUP =			    "/rdj/audio/sounds/startup";
//
//    private final String VOI_CLONE_KEY_DEVICE =		    "/rdj/audio/voice/clone_key_device";
//    private final String VOI_CONFIRM_PASS_WITH_ENTER =	    "/rdj/audio/voice/confirm_password_with_enter";
//    private final String VOI_CREATE_KEY =		    "/rdj/audio/voice/create_key";
//    private final String VOI_CREATE_KEY_DEVICE =		    "/rdj/audio/voice/create_key_device";
//    private final String VOI_DECRYPT_FILES =		    "/rdj/audio/voice/decrypt_files";
//    private final String VOI_DECRYPTING_FILES =		    "/rdj/audio/voice/decrypting_files";
//    private final String VOI_ENCRYPT_FILES =		    "/rdj/audio/voice/encrypt_files";
//    private final String VOI_ENCRYPTING_FILES =		    "/rdj/audio/voice/encrypting_files";
//    private final String VOI_ENCRYPT_OR_DECRYPT_FILES =	    "/rdj/audio/voice/encrypt_or_decrypt_files";
//    private final String VOI_SCANNING_FILES =		    "/rdj/audio/voice/scanning_files";
//    private final String VOI_SELECT_FILES =		    "/rdj/audio/voice/select_files";
//    private final String VOI_SELECT_KEY =		    "/rdj/audio/voice/select_key";
//    private final String VOI_VOICE_DISABLED =		    "/rdj/audio/voice/voice_disabled";
//    private final String VOI_VOICE_ENABLED =		    "/rdj/audio/voice/voice_enabled";
//    private final String VOI_WRONG_KEY_OR_PASSWORD =	    "/rdj/audio/voice/wrong_key_or_password";
//
//    public final int WAV =				    0;
//    public final int OGG =				    1; // Not supported
//    public final int AIFF =				    2; // Not supported
//    public final int MP3 =				    3;

    private Parent root;
    private Stage stage;
    private Scene scene;
    private FXMLLoader loader;
    private GUIFX guifx;
    
    public Support controller;
    
    @FXML private ImageView facebookImageView;
    @FXML private ImageView twitterImageView;
    @FXML private ImageView linkedInImageView;
    @FXML private ImageView pinterestImageView;
//    private ImageView instagramImageView;
//    @FXML private Label statusLabel;
    
//    private AudioClip audioClipSounds;
//    private AudioClip audioClipVoice;
//    private AudioInputStream audioInSounds;
//    private AudioInputStream audioInVoice;
//    private Clip clipSounds;
//    private Clip clipVoice;
    
    private final Preferences prefs = Preferences.userRoot().node(Version.getProductName());
    @FXML
    private ImageView finalcryptImageView;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        loader = new FXMLLoader(getClass().getResource("Support.fxml"));
	root = loader.load();
        controller = loader.getController();
        scene = new Scene((Parent)loader.getRoot());        
        stage = primaryStage;
        stage.setScene(scene);
        stage.setTitle(Version.getProductName() + " Support");
	stage.setResizable(false);
        stage.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
	facebookImageView.setImage(new Image(getClass().getResourceAsStream("/rdj/images/facebook.png")));
	twitterImageView.setImage(new Image(getClass().getResourceAsStream("/rdj/images/twitter.png")));
	linkedInImageView.setImage(new Image(getClass().getResourceAsStream("/rdj/images/linkedin.png")));
	pinterestImageView.setImage(new Image(getClass().getResourceAsStream("/rdj/images/pinterest.png")));
	finalcryptImageView.setImage(new Image(getClass().getResourceAsStream("/rdj/images/finalcrypt.png")));
    }    

    public void setGUI(GUIFX guifx) { this.guifx = guifx; }
        
    private void closeWindow()
    {
	Stage stage = (Stage) facebookImageView.getScene().getWindow(); stage.close();
    }

    public static void main(String[] args)
    {
        launch(args);
    }

    @FXML private void facebookImageViewOnMouseClicked(MouseEvent event)
    {
	new Sound().play(guifx, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	new Sound().play(guifx, Audio.SND_OPEN,Audio.AUDIO_CODEC);
//	play(SND_OPEN,Audio.AUDIO_CODEC);
	Thread shareThread; shareThread = new Thread(() ->
	{
	    try {  Desktop.getDesktop().browse(new URI("https://www.facebook.com/share.php?u=http://www.finalcrypt.org/")); prefs.put("Shared", "Yes"); }
	    catch (URISyntaxException ex) { guifx.log("Error: URISyntaxException: Desktop.getDesktop().open(file); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    catch (IOException ex) { guifx.log("Error: IOException: Desktop.getDesktop().open(file); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	});
	shareThread.setName("shareThread");
	shareThread.setDaemon(true);
	shareThread.start();
    }

    @FXML private void twitterImageViewOnMouseClicked(MouseEvent event)
    {
	new Sound().play(guifx, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	new Sound().play(guifx, Audio.SND_OPEN,Audio.AUDIO_CODEC);
//	play(SND_BUTTON,Audio.AUDIO_CODEC);
//	play(SND_OPEN,Audio.AUDIO_CODEC);
	Thread shareThread; shareThread = new Thread(() ->
	{
	    try {  Desktop.getDesktop().browse(new URI("https://twitter.com/share?original_referer=/&amp;text=FinalCrypt%20-%20THE%20WORLD'S%20MOST%20UNBREAKABLE%20ENCRYPTION&amp;url=http://www.finalcrypt.org/")); prefs.put("Shared", "Yes"); }
	    catch (URISyntaxException ex) { guifx.log("Error: URISyntaxException: Desktop.getDesktop().open(file); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    catch (IOException ex) { guifx.log("Error: IOException: Desktop.getDesktop().open(file); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	});
	shareThread.setName("shareThread");
	shareThread.setDaemon(true);
	shareThread.start();
    }

    @FXML private void linkedInImageViewOnMouseClicked(MouseEvent event)
    {
	new Sound().play(guifx, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	new Sound().play(guifx, Audio.SND_OPEN,Audio.AUDIO_CODEC);
//	play(SND_BUTTON,Audio.AUDIO_CODEC);
//	play(SND_OPEN,Audio.AUDIO_CODEC);
	Thread shareThread; shareThread = new Thread(() ->
	{
	    try {  Desktop.getDesktop().browse(new URI("https://www.linkedin.com/cws/share?url=http://www.finalcrypt.org/")); prefs.put("Shared", "Yes"); }
	    catch (URISyntaxException ex) { guifx.log("Error: URISyntaxException: Desktop.getDesktop().open(file); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    catch (IOException ex) { guifx.log("Error: IOException: Desktop.getDesktop().open(file); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	});
	shareThread.setName("shareThread");
	shareThread.setDaemon(true);
	shareThread.start();
    }

    @FXML private void pinterestImageViewOnMouseClicked(MouseEvent event)
    {
	new Sound().play(guifx, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	new Sound().play(guifx, Audio.SND_OPEN,Audio.AUDIO_CODEC);
//	play(SND_BUTTON,Audio.AUDIO_CODEC);
//	play(SND_OPEN,Audio.AUDIO_CODEC);
	Thread shareThread; shareThread = new Thread(() ->
	{
	    try {  Desktop.getDesktop().browse(new URI("http://pinterest.com/pin/create/button/?url=http://www.finalcrypt.org/&amp;media=http://www.finalcrypt.org/FinalCrypt_Encrypt.png&amp;description=Free%20File%20Encryption")); prefs.put("Shared", "Yes"); }
	    catch (URISyntaxException ex) { guifx.log("Error: URISyntaxException: Desktop.getDesktop().open(file); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    catch (IOException ex) { guifx.log("Error: IOException: Desktop.getDesktop().open(file); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	});
	shareThread.setName("shareThread");
	shareThread.setDaemon(true);
	shareThread.start();
    }

    @FXML private void finalcryptImageViewOnMouseClicked(MouseEvent event)
    {
	new Sound().play(guifx, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	new Sound().play(guifx, Audio.SND_OPEN,Audio.AUDIO_CODEC);
//	play(SND_BUTTON,Audio.AUDIO_CODEC);
//	play(SND_OPEN,Audio.AUDIO_CODEC);
	Thread shareThread; shareThread = new Thread(() ->
	{
	    Version.openWebSite(guifx);
//	    try {  Desktop.getDesktop().browse(new URI("http://www.finalcrypt.org/")); }
//	    catch (URISyntaxException ex) { guifx.log("Error: URISyntaxException: Desktop.getDesktop().open(file); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
//	    catch (IOException ex) { guifx.log("Error: IOException: Desktop.getDesktop().open(file); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	});
	shareThread.setName("shareThread");
	shareThread.setDaemon(true);
	shareThread.start();
    }

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
//	    if ( (audio.contains("sounds")) ) // new sound added to any other audio playing
//	    {
//		if (guifx.sound_Is_Enabled)
//		{
//		    try { audioInputStreamSounds = AudioSystem.getAudioInputStream(new URL(media.getSource())); }
//		    catch (UnsupportedAudioFileException ex)	{ guifx.log("Error: UnsupportedAudioFileException " + this.getClass().getSimpleName() + ".play(..) AudioSystem.getAudioInputStream(" + media.getSource() + " " + ex.getMessage() + " \r\n", true, true, true, true, false); }
//		    catch (IOException ex)			{ guifx.log("Error: IOException " + this.getClass().getSimpleName() + ".play(..) AudioSystem.getAudioInputStream(" + media.getSource() + " " + ex.getMessage() + " \r\n", true, true, true, true, false); }
//		    
//		    clipSounds = null; try {  clipSounds = AudioSystem.getClip(); } catch (LineUnavailableException ex) { guifx.log("Error: LineUnavailableException " + this.getClass().getSimpleName() + ".play(..).AudioSystem.getClip() " + ex.getMessage() + " \r\n", true, true, true, true, false); }
//
//		    try { clipSounds.open(audioInputStreamSounds); } 
//		    catch (LineUnavailableException ex)	{ clipSounds.close(); clipSounds = null; guifx.log("Error: LineUnavailableException " + this.getClass().getSimpleName() + ".play(..).clip.open(" + media.getSource() + ") " + ex.getMessage() + " \r\n", true, true, true, false, false); }
//		    catch (IOException ex)		{ clipSounds.close(); clipSounds = null; guifx.log("Error: IOException " + this.getClass().getSimpleName() + ".play(..).clip.open(" + media.getSource() + ") " + ex.getMessage() + " \r\n", true, true, true, false, false); }
//
//		    if ( clipSounds != null )
//		    {
//			clipSounds.start();
//			try { audioInputStreamSounds.close(); } catch (IOException ex) { guifx.log("Error: IOException " + this.getClass().getSimpleName() + ".play(..).audioInputStreamSounds.close() " + ex.getMessage() + " \r\n", true, true, true, false, false); }
//		    }
//		    else
//		    {
//			try { audioInputStreamSounds.close(); } catch (IOException ex) { guifx.log("Error: IOException " + this.getClass().getSimpleName() + ".play(..).audioInputStreamSounds.close() " + ex.getMessage() + " \r\n", true, true, true, false, false); }			
//		    }
//		    // test(" " + clipSounds.isOpen() + "\r\n");
//		}
//	    }
//	    else if ( audio.contains("voice") )
//	    {
//		if (guifx.voice_Is_Enabled)
//		{
//		    if ((clipVoice != null) && ( clipVoice.isOpen() )) // new voice stopping currently playing voice
//		    {
//			clipVoice.stop(); try { audioInputStreamVoice.close(); } catch (IOException ex) { guifx.log("Error: IOException " + this.getClass().getSimpleName() + ".play(..) audioIn.close() " + ex.getMessage() + " \r\n", true, true, true, true, false); }
//		    }
//		    try { audioInputStreamVoice = AudioSystem.getAudioInputStream(new URL(media.getSource())); }
//		    catch (UnsupportedAudioFileException ex) { guifx.log("Error: UnsupportedAudioFileException " + this.getClass().getSimpleName() + ".play(..) AudioSystem.getAudioInputStream(" + media.getSource() + " " + ex.getMessage() + " \r\n", true, true, true, true, false); }
//		    catch (IOException ex) { guifx.log("Error: IOException " + this.getClass().getSimpleName() + ".play(..) AudioSystem.getAudioInputStream(" + media.getSource() + " " + ex.getMessage() + " \r\n", true, true, true, true, false); }
//
//		    clipVoice = null; try {  clipVoice = AudioSystem.getClip(); } catch (LineUnavailableException ex) { guifx.log("Error: LineUnavailableException " + this.getClass().getSimpleName() + ".play(..).AudioSystem.getClip() " + ex.getMessage() + " \r\n", true, true, true, true, false); }
//
//		    try { clipVoice.open(audioInputStreamVoice); } 
//		    catch (LineUnavailableException ex) { clipVoice.close(); clipVoice = null; guifx.log("Error: LineUnavailableException " + this.getClass().getSimpleName() + "play(..).clipVoice.open(" + media.getSource() + ") " + ex.getMessage() + " \r\n", true, true, true, false, false); }
//		    catch (IOException ex)		{ clipVoice.close(); clipVoice = null; guifx.log("Error: IOException " + this.getClass().getSimpleName() + ".play(..).clipVoice.open(" + media.getSource() + ") " + ex.getMessage() + " \r\n", true, true, true, false, false); }
//		    
//		    if ( clipVoice != null )
//		    {
//			clipVoice.start();
//			try { audioInputStreamVoice.close(); } catch (IOException ex) { guifx.log("Error: IOException " + this.getClass().getSimpleName() + ".play(..).audioInputStreamVoice.close() " + ex.getMessage() + " \r\n", true, true, true, false, false); }
//		    }
//		    else
//		    {
//			try { audioInputStreamVoice.close(); } catch (IOException ex) { guifx.log("Error: IOException " + this.getClass().getSimpleName() + ".play(..).audioInputStreamVoice.close() " + ex.getMessage() + " \r\n", true, true, true, false, false); }			
//		    }
//		    // test(" " + clipVoice.isOpen() + "\r\n");
//		}
//	    }
//	    else { guifx.log("Alert: " + this.getClass().getSimpleName() + ".play(" + media.getSource() + ") not recognized!\r\n", true, true, true, true, false); }
//	}
//    }
        
//    synchronized public void play(String audio, int audio_codec)
//    {
//	Media media;
//	switch (audio_codec)
//	{
//	    case Audio.WAV:	media = new Media(getClass().getResource(audio + ".wav").toExternalForm());		break;
//	    case Audio.OGG:	media = new Media(getClass().getResource(audio + ".ogg").toExternalForm());		break;
//	    case Audio.AIFF:	media = new Media(getClass().getResource(audio + ".aiff").toExternalForm());		break;
//	    case Audio.MP3:	media = new Media(getClass().getResource(audio + ".mp3").toExternalForm());		break;
//	    default:		media = new Media(getClass().getResource(audio + ".wav").toExternalForm());		break;
//	}
//	
//	if (audio != null) 
//	{
//	    if ( (audio.contains("sounds")) ) // new sound added to any other audio playing
//	    {
//		if (guifx.sound_Is_Enabled)
//		{
//		    Platform.runLater(new Runnable(){ @Override public void run()
//		    {
//			if (media != null) 
//			{
//			    if ( media.getSource().contains("sounds") )
//			    {
//				Thread playSoundThread = new Thread(() ->
//				{
//				    if (guifx.sound_Is_Enabled) { audioClipSounds = new AudioClip(media.getSource()); audioClipSounds.play(); /*(" " + play.isPlaying() + "\r\n");*/ }
//				});
//				playSoundThread.setName("playSoundThread");
//				playSoundThread.setDaemon(true);
//				playSoundThread.start();
//			    }
//			    else if ( media.getSource().contains("voice") )
//			    {
//				if ( guifx.voice_Is_Enabled)
//				{
//				    if ( (audioClipVoice != null) && ( audioClipVoice.isPlaying() )) { audioClipVoice.stop(); }
//				    audioClipVoice = new AudioClip(media.getSource()); audioClipVoice.play(); /*test(" " + play.isPlaying() + "\r\n");*/ 
//				}
//			    }
//			    else { guifx.log("Alert: play(" + media.getSource() + ") not recognized!\r\n", true, true, true, true, false); }
//			}
//		    }});
//		}
//	    }
//	    else if ( audio.contains("voice") )
//	    {
//		if (guifx.voice_Is_Enabled)
//		{
//		    if ((audioClipSounds != null) && ( audioClipSounds.isPlaying() )) // new voice stopping currently playing voice
//		    {
//			audioClipSounds.stop();
//		    }
//		    Platform.runLater(new Runnable(){ @Override public void run()
//		    {
//			if (media != null) 
//			{
//			    if ( media.getSource().contains("sounds") )
//			    {
//				Thread playVoiceThread = new Thread(() ->
//				{
//				    if (guifx.sound_Is_Enabled) { audioClipSounds = new AudioClip(media.getSource()); audioClipSounds.play(); /*(" " + play.isPlaying() + "\r\n");*/ }
//				});
//				playVoiceThread.setName("playVoiceThread");
//				playVoiceThread.setDaemon(true);
//				playVoiceThread.start();
//			    }
//			    else if ( media.getSource().contains("voice") )
//			    {
//				if ( guifx.voice_Is_Enabled)
//				{
//				    if ( (audioClipVoice != null) && ( audioClipVoice.isPlaying() )) { audioClipVoice.stop(); }
//				    audioClipVoice = new AudioClip(media.getSource()); audioClipVoice.play(); /*test(" " + play.isPlaying() + "\r\n");*/ 
//				}
//			    }
//			    else { guifx.log("Alert: play(" + media.getSource() + ") not recognized!\r\n", true, true, true, true, false); }
//			}
//		    }});
//		}
//	    }
//	    else { guifx.log("Alert: " + this.getClass().getSimpleName() + ".play(" + media.getSource() + ") not recognized!\r\n", true, true, true, true, false); }
//	}
//    }        
}
