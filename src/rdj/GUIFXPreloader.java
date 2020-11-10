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

import java.net.URL;
import java.util.*;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.*;
import javafx.util.*;

public class GUIFXPreloader extends Application implements UI, Initializable
{
    private Parent root;
    private Stage preloaderStage;
    private Scene scene;
    private FXMLLoader loader;
    private GUIFXPreloader guiFXPreloader;
    
    @FXML private ImageView bgImageView;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;

    public GUIFXPreloader controller;    
    private String windowTitle = "Preloader";
//    private Configuration configuration;
    private GUIFX guifx;

    @Override public void start(Stage stage) throws Exception
    {
//        configuration = new Configuration(this);

	loader = new FXMLLoader(getClass().getResource("GUIFXPreloader.fxml"));
	root = loader.load();
	controller = loader.getController();
	scene = new Scene((Parent)loader.getRoot());        
	preloaderStage = stage;
	preloaderStage.centerOnScreen();
	preloaderStage.setAlwaysOnTop(true);
	preloaderStage.initStyle(StageStyle.TRANSPARENT);
	preloaderStage.setScene(scene);
	preloaderStage.setTitle(windowTitle);
	preloaderStage.setResizable(false);

	preloaderStage.show();
	double screenWidth  =	Screen.getPrimary().getVisualBounds().getWidth();
	double screenHeight =	Screen.getPrimary().getVisualBounds().getHeight();
	
	double stageWidth   =	root.getBoundsInParent().getWidth();
	double stageHeight  =	root.getBoundsInParent().getHeight();
	
	double stageXPos = (screenWidth / 2.0) - (stageWidth / 2.0);
	double stageYPos = (screenHeight / 2.0) - (stageHeight / 2.0);

        preloaderStage.setX(stageXPos);
        preloaderStage.setY(stageYPos);	
    }
    
    @Override public void initialize(URL url, ResourceBundle rb)
    {	
	bgImageView.setImage(new Image(getClass().getResourceAsStream("/rdj/images/finalcrypt-splash.png")));
	bgImageView.autosize();

	guiFXPreloader = this;
	
	guifx = new GUIFX();
	guifx.setPreloader(this);

	Timeline timeline = new Timeline(new KeyFrame( Duration.millis(500), ae ->
	{
	    Platform.runLater(() ->
	    {
		Stage guifxStage = new Stage();
		try { guifx.start(guifxStage); } catch (Exception ex)
		{
		    log("Error: GUIFXPreloader Exception guifx.start(guifxStage)\r\n", true, false, false, true, true);
//		    log(ex.getStackTrace().toString() + "\r\nguifx.start(guifxStage)\r\n", false, false, true, true, true);
		}
	    });
	    
	} )); timeline.play();
    }    
    
    protected void setProgress(double val)
    {
	Platform.runLater(new Runnable(){ @Override public void run()
	{
	    progressBar.setProgress(val);
//	    if (val >= 1.0) { closeWindow(); }
	}});
    }

    protected void setStatus(String val)
    {
	guifx.setPreloader(guiFXPreloader);
	Platform.runLater(new Runnable(){ @Override public void run()
	{
	    statusLabel.setText(val);
	}});
    }
    
    protected void setProgressStatus(double val, String status)
    {
	guifx.setPreloader(this);
	Platform.runLater(new Runnable(){ @Override public void run()
	{
	    setProgress(val);
	    setStatus(status);
	}});
    }

    protected void closeWindow()
    {
	Stage stage = (Stage) statusLabel.getScene().getWindow();		
	
	Timeline delayTimeline = new Timeline(new KeyFrame( Duration.millis(2500), ae1 ->
	{
	    Timeline dimTimeline = new Timeline();
	    KeyFrame key = new KeyFrame(Duration.millis(500),
			   new KeyValue (stage.getScene().getRoot().opacityProperty(), 0)); 
	    dimTimeline.getKeyFrames().add(key);   
	    dimTimeline.setOnFinished((ae2) -> stage.close());
	    dimTimeline.play();
	} )); delayTimeline.play();	
    }
    
    @Override public void test(String message)
    {
	log(message, true, true, true, false, false);
//	if ( message != null ) { log(message, true, true, true, false, false); } else {  }
    }

    @Override synchronized public void log(String message, boolean status, boolean log, boolean logfile, boolean errfile, boolean print)
    {
	if (statusLabel!=null)
	{
	    if (status)	    { status(message); }
	    if (log)	    { log(message); }
	    if (logfile)    { logfile(message); }
	    if (errfile)    { errfile(message); }
	    if (print)	    { print(message,errfile); }
	}
    }

    public void status(String message)		    { Platform.runLater(() -> { statusLabel.setText(message.replace("\r\n", "")); }); }
    public void log(String message)		    { Platform.runLater(() -> {  });  }
//    public void logfile(String message)		    { Platform.runLater(() -> { try { Files.write(configuration.getLogFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND); } catch (IOException ex) { log("Files.write(" + configuration.getLogFilePath() + ")..));", true, true, false, false, false); } }); }
//    public void errfile(String message)		    { Platform.runLater(() -> { new Sound().play(this, Audio.SND_ERROR,Audio.AUDIO_CODEC); try { Files.write(configuration.getErrFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND); } catch (IOException ex) { log("Files.write(" + configuration.getErrFilePath() + ")..));", true, true, false, false, false); } }); }
    public void logfile(String message)		    {  }
    public void errfile(String message)		    { Platform.runLater(() -> { new Sound().play(this, Audio.SND_ERROR,Audio.AUDIO_CODEC); }); }
    public void print(String message,boolean err)   { if ( ! err ) { System.out.print(message); } else { System.err.print(message); } }

    @Override
    public void processGraph(int value)
    {
    }

    @Override
    public void processProgress(int filesProgressPercent, int fileProgressPercent, long bytesTotalParam, long bytesProcessedParam, double bytesPerMiliSecondParam)
    {
    }

    @Override
    public void fileProgress()
    {
    }

    @Override
    public void processFinished(FCPathList<FCPath> openFCPathList, boolean open)
    {
    }

    @Override
    public void processStarted()
    {
    }

    @Override
    public void buildReady(FCPathList<FCPath> fcPathListParam, boolean validBuild)
    {
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
