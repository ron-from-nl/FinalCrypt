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

public class Please_Share extends Application implements Initializable
{
    private final Media WAV_SND_BUTTON =		    new Media(getClass().getResource("/rdj/audio/wav/sounds/snd_button.wav").toExternalForm());
    private final Media WAV_SND_ENCRYPTFILES =		    new Media(getClass().getResource("/rdj/audio/wav/sounds/snd_encrypt_files.wav").toExternalForm());
    private final Media WAV_SND_INPUT_FAIL =		    new Media(getClass().getResource("/rdj/audio/wav/sounds/snd_input_fail.wav").toExternalForm());
    private final Media WAV_SND_INPUT_OK =		    new Media(getClass().getResource("/rdj/audio/wav/sounds/snd_input_ok.wav").toExternalForm());
    private final Media WAV_SND_KEYPRESS =		    new Media(getClass().getResource("/rdj/audio/wav/sounds/snd_key_press.wav").toExternalForm());
    private final Media WAV_SND_OPEN =			    new Media(getClass().getResource("/rdj/audio/wav/sounds/snd_open.wav").toExternalForm());
    private final Media WAV_SND_SHUTDOWN =		    new Media(getClass().getResource("/rdj/audio/wav/sounds/snd_shutdown.wav").toExternalForm());

    private final Media WAV_VOI_CREATE_KEY =		    new Media(getClass().getResource("/rdj/audio/wav/voice/voi_create_key.wav").toExternalForm());
    private final Media WAV_VOI_SELECT_KEY =		    new Media(getClass().getResource("/rdj/audio/wav/voice/voi_select_key.wav").toExternalForm());

    private final Media MP3_SND_BUTTON =		    new Media(getClass().getResource("/rdj/audio/mp3/sounds/snd_button.mp3").toExternalForm());
    private final Media MP3_SND_ENCRYPTFILES =		    new Media(getClass().getResource("/rdj/audio/mp3/sounds/snd_encrypt_files.mp3").toExternalForm());
    private final Media MP3_SND_INPUT_FAIL =		    new Media(getClass().getResource("/rdj/audio/mp3/sounds/snd_input_fail.mp3").toExternalForm());
    private final Media MP3_SND_INPUT_OK =		    new Media(getClass().getResource("/rdj/audio/mp3/sounds/snd_input_ok.mp3").toExternalForm());
    private final Media MP3_SND_KEYPRESS =		    new Media(getClass().getResource("/rdj/audio/mp3/sounds/snd_key_press.mp3").toExternalForm());
    private final Media MP3_SND_OPEN =			    new Media(getClass().getResource("/rdj/audio/mp3/sounds/snd_open.mp3").toExternalForm());
    private final Media MP3_SND_SHUTDOWN =		    new Media(getClass().getResource("/rdj/audio/mp3/sounds/snd_shutdown.mp3").toExternalForm());

    private final Media MP3_VOI_CREATE_KEY =		    new Media(getClass().getResource("/rdj/audio/mp3/voice/voi_create_key.mp3").toExternalForm());
    private final Media MP3_VOI_SELECT_KEY =		    new Media(getClass().getResource("/rdj/audio/mp3/voice/voi_select_key.mp3").toExternalForm());

    private Parent root;
    private Stage stage;
    private Scene scene;
    private FXMLLoader loader;
    private GUIFX guifx;
    
    public Please_Share controller;
    
    @FXML private ImageView facebookImageView;
    @FXML private ImageView twitterImageView;
    @FXML private ImageView linkedInImageView;
    @FXML private ImageView pinterestImageView;
    private ImageView instagramImageView;
    @FXML private Label statusLabel;
    
    private AudioClip audioClipSounds;
    private AudioClip audioClipVoice;
    private AudioInputStream audioInSounds;
    private AudioInputStream audioInVoice;
    private Clip clipSounds;
    private Clip clipVoice;
    
    private final Preferences prefs = Preferences.userRoot().node(Version.getProductName());
    @FXML
    private ImageView finalcryptImageView;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        loader = new FXMLLoader(getClass().getResource("Please_Share.fxml"));
	root = loader.load();
        controller = loader.getController();
        scene = new Scene((Parent)loader.getRoot());        
        stage = primaryStage;
        stage.setScene(scene);
        stage.setTitle("Please Share " + Version.getProductName());
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
	play_MP3(MP3_SND_BUTTON);
	play_MP3(MP3_SND_OPEN);
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
	play_MP3(MP3_SND_BUTTON);
	play_MP3(MP3_SND_OPEN);
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
	play_MP3(MP3_SND_BUTTON);
	play_MP3(MP3_SND_OPEN);
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
	play_MP3(MP3_SND_BUTTON);
	play_MP3(MP3_SND_OPEN);
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
	play_MP3(MP3_SND_BUTTON);
	play_MP3(MP3_SND_OPEN);
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

    public void play_WAV(Media media)
    {
//	Thread playThread = new Thread(() ->
//	{
	    if (media != null) 
	    {
		if (media.getSource().contains("sounds")) // new sound added to any other audio playing
		{
		    try { audioInSounds = AudioSystem.getAudioInputStream(new URL(media.getSource())); }
		    catch (UnsupportedAudioFileException ex) { guifx.log("Error: UnsupportedAudioFileException play(..) " + ex.getMessage() + " \r\n", true, true, true, true, false); }
		    catch (IOException ex) { guifx.log("Error: IOException play(..) " + ex.getMessage() + " \r\n", true, true, true, true, false); }

		    clipSounds = null; try {  clipSounds = AudioSystem.getClip(); } catch (LineUnavailableException ex) { guifx.log("Error: LineUnavailableException play(..).AudioSystem.getClip() " + ex.getMessage() + " \r\n", true, true, true, true, false); }

		    try { clipSounds.open(audioInSounds); } 
		    catch (LineUnavailableException ex) { guifx.log("Error: LineUnavailableException play(..).clip.open(..) " + ex.getMessage() + " \r\n", true, true, true, true, false); }
		    catch (IOException ex) { guifx.log("Error: IOException play(..).clip.open(..) " + ex.getMessage() + " \r\n", true, true, true, true, false); }

		    clipSounds.start();
		}
		else
		{
		    if ((clipVoice != null) && ( media.getSource().contains("voice") ) && ( clipVoice.isActive() )) // new voice stopping currently playing voice
		    {
			clipVoice.stop(); try { audioInVoice.close(); } catch (IOException ex) { guifx.log("Error: IOException audioIn.close() " + ex.getMessage() + " \r\n", true, true, true, true, false); }
		    }
		    try { audioInVoice = AudioSystem.getAudioInputStream(new URL(media.getSource())); }
		    catch (UnsupportedAudioFileException ex) { guifx.log("Error: UnsupportedAudioFileException play(..) " + ex.getMessage() + " \r\n", true, true, true, true, false); }
		    catch (IOException ex) { guifx.log("Error: IOException play(..) " + ex.getMessage() + " \r\n", true, true, true, true, false); }

		    clipVoice = null; try {  clipVoice = AudioSystem.getClip(); } catch (LineUnavailableException ex) { guifx.log("Error: LineUnavailableException play(..).AudioSystem.getClip() " + ex.getMessage() + " \r\n", true, true, true, true, false); }

		    try { clipVoice.open(audioInVoice); } 
		    catch (LineUnavailableException ex) { guifx.log("Error: LineUnavailableException play(..).clip.open(..) " + ex.getMessage() + " \r\n", true, true, true, true, false); }
		    catch (IOException ex) { guifx.log("Error: IOException play(..).clip.open(..) " + ex.getMessage() + " \r\n", true, true, true, true, false); }

		    clipVoice.start();
		}
	    }
//	});
//	playThread.setName("playThread");
//	playThread.setDaemon(true);
//	playThread.start();
    }

    public void play_MP3(Media media)
    {
//	test("Invoking play_MP3: " + media.getSource() + " ");
	
	if (media != null) 
	{
	    if ( media.getSource().contains("sounds") )
	    {
		if (guifx.sound_Is_Enabled) { audioClipSounds = new AudioClip(media.getSource()); audioClipSounds.play(); /*(" " + play.isPlaying() + "\r\n");*/ }
	    }
	    else if ( media.getSource().contains("voice") )
	    {
		if ( guifx.voice_Is_Enabled)
		{
		    if ( (audioClipVoice != null) && ( audioClipVoice.isPlaying() )) { audioClipVoice.stop(); }
		    audioClipVoice = new AudioClip(media.getSource()); audioClipVoice.play(); /*test(" " + play.isPlaying() + "\r\n");*/ 
		}
	    }
	    else { guifx.log("Alert: play_MP3(" + media.getSource() + ") not recognized!\r\n", true, true, true, true, false); }
	}
    }
}
