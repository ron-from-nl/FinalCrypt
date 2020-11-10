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
    @FXML private ImageView homeImageView;
    @FXML private ImageView supportImageView;
    @FXML private ImageView emailImageView;
    @FXML private ImageView videoImageView;

//  private ImageView instagramImageView;
    
    private Preferences prefs;
    private ResourceBundle bundle;
    private Version version;

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
	homeImageView.setImage(new Image(getClass().getResourceAsStream("/rdj/images/finalcrypt.png")));
	supportImageView.setImage(new Image(getClass().getResourceAsStream("/rdj/images/support.png")));
	emailImageView.setImage(new Image(getClass().getResourceAsStream("/rdj/images/email.png")));
	videoImageView.setImage(new Image(getClass().getResourceAsStream("/rdj/images/video.png")));
    }

    public void setGUI(GUIFX ui)
    {
	guifx = ui;
	version = new Version(guifx);
	version.checkLocalVersion(guifx);
	prefs = Preferences.userRoot().node(Version.getProductName() + version.getLocalOverallVersionPrefString());
    }

    public void setSupportState()
    {
	String val = prefs.get("Shared", "Unknown");
	if	(val.equals("Unknown"))	    { setSupportButtonsDisabledState(true); }
	else if (val.equals("No"))	    { setSupportButtonsDisabledState(true); }
	else if (val.equals("Yes"))	    { setSupportButtonsDisabledState(false); }
	else				    { setSupportButtonsDisabledState(true); }
    }
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

    private void flushPrefs(Preferences prefsParam)
    {
//	guifx.test("flushPrefs: " + prefsParam.name() + "\r\n");
	try { prefsParam.flush(); } catch (BackingStoreException ex) { guifx.log("Error: flushPrefs(..) " + ex.getMessage() + "\r\n", true, true, true, true ,false); }
    }
    
    @FXML private void facebookImageViewOnMouseClicked(MouseEvent event)
    {
	new Sound().play(guifx, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	new Sound().play(guifx, Audio.SND_OPEN,Audio.AUDIO_CODEC);
//	play(SND_OPEN,Audio.AUDIO_CODEC);
	Thread shareThread; shareThread = new Thread(() ->
	{
	    String urlTarget	    = "https://www.facebook.com/share.php";
	    String param1	    = "?u=" + Version.encode2URL(guifx, "http://www.finalcrypt.org/");
	    String url = urlTarget + param1;

	    setSupportButtonsDisabledState(false);
	    try {  Desktop.getDesktop().browse(new URI(url)); prefs.put("Shared", "Yes"); flushPrefs(prefs); }
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
	    String urlTarget	= "https://twitter.com/intent/tweet";
	    String param1	= "?text=" + Version.encode2URL(guifx, "FinalCrypt - Unbreakable OTP Encryption independent from Governments & Industries");
	    String param2	= "&url=" + Version.encode2URL(guifx, "http://www.finalcrypt.org/");
	    String param3	= "&hashtags=FinalCrypt%2CEncryption";
	    String url = urlTarget + param1 + param2 + param3;

	    setSupportButtonsDisabledState(false);
	    try {  Desktop.getDesktop().browse(new URI(url)); prefs.put("Shared", "Yes"); flushPrefs(prefs); }
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
//	    
	    String urlTarget	    = "https://www.linkedin.com/sharing/share-offsite/";
	    String param1	    = "?url=" + Version.encode2URL(guifx, "http://www.finalcrypt.org/");
	    String url = urlTarget + param1;

//	    String url = "https://www.linkedin.com/cws/share?url=http://www.finalcrypt.org/";
	    setSupportButtonsDisabledState(false);
	    try {  Desktop.getDesktop().browse(new URI(url)); prefs.put("Shared", "Yes"); flushPrefs(prefs); }
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
	    String urlTarget	    = "https://pin.it/72kLWc2";
	    String url = urlTarget;

//	    String url = "http://pinterest.com/pin/create/button/?url=http://www.finalcrypt.org/&amp;media=http://www.finalcrypt.org/FinalCrypt_Encrypt.png&amp;description=Free%20File%20Encryption";
	    setSupportButtonsDisabledState(false);
	    try {  Desktop.getDesktop().browse(new URI(url)); prefs.put("Shared", "Yes"); flushPrefs(prefs); }
	    catch (URISyntaxException ex)		{ guifx.log("Error: URISyntaxException: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    catch (IOException ex)			{ guifx.log("Error: IOException: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    catch (UnsupportedOperationException ex)	{ guifx.log(ex.getMessage() + " " + url + "\r\n", true, true, true, true, false); }
	    closeWindow();
	});
	shareThread.setName("shareThread");
	shareThread.setDaemon(true);
	shareThread.start();
    }

//  ============================================================================
    
    public boolean getSupportButtonsDisabledState()
    {
	boolean bool = homeImageView.isDisabled();
//	guifx.test("getSupportButtonsDisabledState(" + Boolean.toString(bool)+ ")" + "\r\n");
	return bool;
    }
    public void setSupportButtonsDisabledState(boolean bool)
    {
//		false (enable support)
	if ( (! bool) && (getSupportButtonsDisabledState()))
	{
	    Thread getSupportThread; getSupportThread = new Thread(() ->
	    {
//		guifx.test("setSupportButtonsDisabledState(" + Boolean.toString(bool)+ ")" + "\r\n");
		homeImageView.setDisable(bool); homeImageView.setOpacity(1);
		videoImageView.setDisable(bool); videoImageView.setOpacity(1);
		supportImageView.setDisable(bool); supportImageView.setOpacity(1);
		emailImageView.setDisable(bool); emailImageView.setOpacity(1);
	    });
	    getSupportThread.setName("getSupportThread");
	    getSupportThread.setDaemon(true);
	    getSupportThread.start();
	}
//		   true (disable support)
	else if ( (bool) && (! getSupportButtonsDisabledState()))
	{
	    Thread setSupportThread; setSupportThread = new Thread(() ->
	    {
//		guifx.test("setSupportButtonsDisabledState(" + Boolean.toString(bool)+ ")" + "\r\n");
		homeImageView.setDisable(bool); homeImageView.setOpacity(0.2);
		videoImageView.setDisable(bool); videoImageView.setOpacity(0.2);
		supportImageView.setDisable(bool); supportImageView.setOpacity(0.2);
		emailImageView.setDisable(bool); emailImageView.setOpacity(0.2);
	    });
	    setSupportThread.setName("setSupportThread");
	    setSupportThread.setDaemon(true);
	    setSupportThread.start();
	}
    }    
    
    @FXML private void homeImageViewOnMouseClicked(MouseEvent event)
    {
	new Sound().play(guifx, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	new Sound().play(guifx, Audio.SND_OPEN,Audio.AUDIO_CODEC);
	Thread homeOpenThread; homeOpenThread = new Thread(() -> { Version.openWebSite(guifx, Version.HOMEPAGEURLSTRINGARRAY,"GET"); closeWindow(); });
	homeOpenThread.setName("homeOpenThread");
	homeOpenThread.setDaemon(true);
	homeOpenThread.start();
    }

    @FXML private void videoImageViewOnMouseClicked(MouseEvent event)
    {
	new Sound().play(guifx, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	new Sound().play(guifx, Audio.SND_OPEN,Audio.AUDIO_CODEC);
	Thread videoOpenThread; videoOpenThread = new Thread(() -> { Version.openWebSite(guifx, Version.VIDEOPAGEURLSTRINGARRAY,"HEAD"); closeWindow(); });
	videoOpenThread.setName("videoOpenThread");
	videoOpenThread.setDaemon(true);
	videoOpenThread.start();
    }

    @FXML private void supportImageViewOnMouseClicked(MouseEvent event)
    {
	new Sound().play(guifx, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	new Sound().play(guifx, Audio.SND_OPEN,Audio.AUDIO_CODEC);
	Thread supportOpenThread; supportOpenThread = new Thread(() -> { Version.openWebSite(guifx, Version.SUPPORTPAGEURLSTRINGARRAY,"GET"); closeWindow(); });
	supportOpenThread.setName("supportOpenThread");
	supportOpenThread.setDaemon(true);
	supportOpenThread.start();
    }

    @FXML private void emailImageViewOnMouseClicked(MouseEvent event)
    {
	new Sound().play(guifx, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	new Sound().play(guifx, Audio.SND_OPEN,Audio.AUDIO_CODEC);
	
	String to = Version.SUPPORTEMAIL;
	String cc = "";
	String subject = Version.encode2URL(guifx, Version.getProductName() + " User Support");
	String bodyText = "";
	String attachLine1 = "You might want to attach logfiles from: \"" + Version.getLogDirPath(this.getClass().getSimpleName(), version, new Configuration(guifx))+ "\".";
	String attachLine2 = "Logfiles help to see what happened and eases and speed up support.";
	bodyText += "Hi " + Version.AUTHOR_FIRSTNAME + ",\r\n\r\nYour message...\r\n\r\n" + attachLine1 + "\r\n" + attachLine2 + "\r\n\r\n--\r\nRegards,\r\n\r\nYour name please\r\n\r\n";
	bodyText += "================================================================================\r\n";
	bodyText += "======================== " + Version.PRODUCTNAME + " System Support Info ========================\r\n";
	bodyText += "================================================================================\r\n\r\n";
	bodyText += Version.getSysEnvEmail(this.getClass().getSimpleName(), version, new Configuration(guifx));
	bodyText += "================================================================================\r\n";

	String body = Version.encode2URL(guifx, bodyText);
//									     openEmail(UI ui, String mailto,	String cc,	String subject, String body)
	Thread emailOpenThread; emailOpenThread = new Thread(() -> { Version.openEmail(guifx, to,		"",		subject,	body); closeWindow(); });
	emailOpenThread.setName("emailOpenThread");
	emailOpenThread.setDaemon(true);
	emailOpenThread.start();
    }
}
