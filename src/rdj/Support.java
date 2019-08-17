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
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class Support extends Application implements Initializable
{
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
	Thread shareThread; shareThread = new Thread(() ->
	{
	    Version.openWebSite(guifx);
	});
	shareThread.setName("shareThread");
	shareThread.setDaemon(true);
	shareThread.start();
    }        
}
