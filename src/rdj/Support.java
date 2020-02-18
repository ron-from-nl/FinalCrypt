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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
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
import javafx.stage.*;

public class Support extends Application implements Initializable
{
    private Parent root;
    private Stage stage;
    private Scene scene;
    private FXMLLoader loader;
    private GUIFX guifx;
    private boolean exitAppOnClose = false;
    
    public Support controller;
        
    @FXML private Label headerLabel;
    @FXML private Label line1Label;
    @FXML private Label line2Label;
    @FXML private Label statusLabel;

    @FXML private ImageView facebookImageView;
    @FXML private ImageView twitterImageView;
    @FXML private ImageView linkedInImageView;
    @FXML private ImageView pinterestImageView;
    @FXML private ImageView finalcryptImageView;

//  private ImageView instagramImageView;
    
    private final Preferences prefs = Preferences.userRoot().node(Version.getProductName());
    private ResourceBundle bundle;

//    public Support(boolean exitAppOnClose)
//    {
//	this.exitAppOnClose = exitAppOnClose;
//    }
    public void switchLanguage(Locale locale)
    {
	if (locale != null)
	{
	    bundle = ResourceBundle.getBundle("rdj.language.translation", locale);
	    headerLabel.setText(bundle.getString("142"));	
	    line1Label.setText(bundle.getString("143"));	
	    line2Label.setText(bundle.getString("144"));	
	    statusLabel.setText(bundle.getString("145"));	
	}
    }
    
    public void setExitAppOnClose(boolean exitAppOnClose)
    {
	this.exitAppOnClose = exitAppOnClose;
//	System.out.println("Support set: exitAppOnClose " + exitAppOnClose + "\r\n");

	Stage stage = (Stage) facebookImageView.getScene().getWindow();
	stage.setOnCloseRequest((WindowEvent e) ->
	{
	    Platform.runLater(() ->
	    {
		if ( exitAppOnClose )
		{
//		    System.out.println("Support exitAppOnClose\r\n");
		    System.exit(0);
		}
	    });
	});
    }

    @Override  public void start(Stage primaryStage) throws Exception
    {
        loader = new FXMLLoader(getClass().getResource("Support.fxml"));
	root = loader.load();
        controller = loader.getController();
        scene = new Scene((Parent)loader.getRoot());        
        stage = primaryStage;
        stage.setScene(scene);
        stage.setTitle(Version.getProductName() + " Support");
	stage.setResizable(false);
//        stage.show();
	
	stage.setOnCloseRequest((WindowEvent e) ->
	{
	    Platform.runLater(() ->
	    {
		if ( exitAppOnClose )
		{
//		    System.out.println("Support exitAppOnClose\r\n");
		    System.exit(0);
		}
	    });
	});
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
	Platform.runLater(() ->
	{
	    Stage stage = (Stage) facebookImageView.getScene().getWindow(); stage.close();
	    if (exitAppOnClose) { System.exit(0); }
	});
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
	    String url = "https://www.facebook.com/share.php?u=http://www.finalcrypt.org/";
	    try {  Desktop.getDesktop().browse(new URI(url)); prefs.put("Shared", "Yes"); }
	    catch (URISyntaxException ex)		{ guifx.log("Error: URISyntaxException: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    catch (IOException ex)			{ guifx.log("Error: IOException: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    catch (UnsupportedOperationException ex)	{ guifx.log(ex.getMessage() + " " + url + "\r\n", true, true, true, true, false); }
	    closeWindow();
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
	    String url = "https://twitter.com/share?original_referer=/&amp;text=FinalCrypt%20-%20THE%20WORLD'S%20MOST%20UNBREAKABLE%20ENCRYPTION&amp;url=http://www.finalcrypt.org/";
	    try {  Desktop.getDesktop().browse(new URI(url)); prefs.put("Shared", "Yes"); }
	    catch (URISyntaxException ex)		{ guifx.log("Error: URISyntaxException: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    catch (IOException ex)			{ guifx.log("Error: IOException: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    catch (UnsupportedOperationException ex)	{ guifx.log(ex.getMessage() + " " + url + "\r\n", true, true, true, true, false); }
	    closeWindow();
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
	    String url = "https://www.linkedin.com/cws/share?url=http://www.finalcrypt.org/";
	    try {  Desktop.getDesktop().browse(new URI(url)); prefs.put("Shared", "Yes"); }
	    catch (URISyntaxException ex)		{ guifx.log("Error: URISyntaxException: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    catch (IOException ex)			{ guifx.log("Error: IOException: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    catch (UnsupportedOperationException ex)	{ guifx.log(ex.getMessage() + " " + url + "\r\n", true, true, true, true, false); }
	    closeWindow();
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
	    String url = "http://pinterest.com/pin/create/button/?url=http://www.finalcrypt.org/&amp;media=http://www.finalcrypt.org/FinalCrypt_Encrypt.png&amp;description=Free%20File%20Encryption";
	    try {  Desktop.getDesktop().browse(new URI(url)); prefs.put("Shared", "Yes"); }
	    catch (URISyntaxException ex)		{ guifx.log("Error: URISyntaxException: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    catch (IOException ex)			{ guifx.log("Error: IOException: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    catch (UnsupportedOperationException ex)	{ guifx.log(ex.getMessage() + " " + url + "\r\n", true, true, true, true, false); }
	    closeWindow();
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
	    closeWindow();
	});
	shareThread.setName("shareThread");
	shareThread.setDaemon(true);
	shareThread.start();
    }        
}
