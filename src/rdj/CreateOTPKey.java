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
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CreateOTPKey extends Application implements Initializable
{
    private Parent root;
    private Stage stage;
    private Scene scene;
    private FXMLLoader loader;
    private GUIFX guifx;
    public Path currentDirPath;
    private Path keyPath;
    private Timeline repeaterTimeline;
    public static final String FIP140_2_URL_STRING =	    "https://en.wikipedia.org/wiki/FIPS_140";
    public static final String ONE_TIME_PAD_URL_STRING =    "https://en.wikipedia.org/wiki/One-time_pad";//https://en.wikipedia.org/wiki/FIPS_140
    private final long UPDATE_PROGRESS_TIMERTASK_PERIOD =   100L;
    
    @FXML private ImageView bgImageView;
    @FXML private TextField filenameTextField;
    @FXML private TextField filesizeTextField;
    @FXML private ChoiceBox<String> unitChoiceBox;
    @FXML private Button increaseButton;
    @FXML private Button decreaseButton;
    @FXML private Button cancelButton;
    @FXML private Button createButton;
    
    @FXML private Label filenameLabel;
    @FXML private Label filesizeLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Label untiLabel;
    @FXML private Label statusLabel1;
    @FXML private Label statusLabel2;
    public CreateOTPKey controller;
    @FXML private Label complianceLabel;
    
    private Long filesizeNumber;
    private Long factor;
    private Long filesizeInBytes;
    private int bufferSize = 1024 * 1024;
    private TimerTask updateProgressTask;
    private Timer updateProgressTaskTimer;
    private long totalTranfered;
    private long throughputClock;
    private long lastThroughputClock;
    private long realtimeBytesProcessed;
    private double realtimeMiBPS;
    @FXML   private Label otpRulesLabel;
    
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        loader = new FXMLLoader(getClass().getResource("CreateOTPKey.fxml"));
	root = loader.load();
        controller = loader.getController();
        scene = new Scene((Parent)loader.getRoot());        
        stage = primaryStage;
        stage.setScene(scene);
        stage.setTitle("Create OTP Key");
	stage.setResizable(false);
        stage.show();
	filesizeInBytes = 0L;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
	bgImageView.setImage(new Image(getClass().getResourceAsStream("/rdj/images/Gardenchurch.jpg")));
	unitChoiceBox.getItems().add("Bytes");
	unitChoiceBox.getItems().add("KiB");
	unitChoiceBox.getItems().add("MiB");
	unitChoiceBox.getItems().add("GiB");
	unitChoiceBox.getItems().add("TiB");
	unitChoiceBox.getItems().add("PiB");
	unitChoiceBox.getItems().add("EiB");
	unitChoiceBox.getSelectionModel().select(2);

//	play = new Sound();
	
//	FileName Listener
	filenameTextField.textProperty().addListener((obs, oldText, newText) ->
	{
	    String regex = "[^a-zA-Z0-9\\\\-\\\\_\\\\.\\\\ ]";
	    if ( filenameTextField.getText().matches(regex) ) { filenameTextField.setText(filenameTextField.getText().replaceAll(regex, "")); }
	    else { new Sound().play(guifx, Audio.SND_KEYPRESS,Audio.AUDIO_CODEC); filenameTextField.setText(filenameTextField.getText().replaceAll(regex, "")); } 

	    if (( filenameTextField.getText().length() > 0 ))
	    {
		if ((Files.exists(Paths.get(currentDirPath.toAbsolutePath().toString(), filenameTextField.getText()), LinkOption.NOFOLLOW_LINKS)))
		{
		    filesizeLabel.setDisable(true);
		    filesizeTextField.setDisable(true);
		    increaseButton.setDisable(true);
		    decreaseButton.setDisable(true);
		    untiLabel.setDisable(true);
		    unitChoiceBox.setDisable(true);
		    createButton.setDisable(true);
		    keyPath = Paths.get(currentDirPath.toAbsolutePath().toString(), filenameTextField.getText());
		    statusLabel1.setText("File already exists");
		    statusLabel2.setText(keyPath.toAbsolutePath().toString());
//		    calculateOTPKeyFileSize();
		}
		else
		{
		    filesizeLabel.setDisable(false);
		    filesizeTextField.setDisable(false);
		    increaseButton.setDisable(false);
		    decreaseButton.setDisable(false);
		    untiLabel.setDisable(false);
		    unitChoiceBox.setDisable(false);
		    createButton.setDisable(false);
		    createButton.setDefaultButton(true);
		    keyPath = Paths.get(currentDirPath.toAbsolutePath().toString(), filenameTextField.getText());
		    calculateOTPKeyFileSize();
		}
	    }
	    else
	    {
		filesizeLabel.setDisable(true);
		filesizeTextField.setDisable(true);
		increaseButton.setDisable(true);
		decreaseButton.setDisable(true);
		untiLabel.setDisable(true);
		unitChoiceBox.setDisable(true);
		createButton.setDisable(true);
		keyPath = null;
		statusLabel1.setText("Current directory");
		statusLabel2.setText(currentDirPath.toAbsolutePath().toString());
	    }
	});
	
//	FileSize Listener
	filesizeTextField.textProperty().addListener((obs, oldText, newText) -> 
	{
	    filesizeTextField.setText(filesizeTextField.getText().replaceAll("[^0-9]", "").replaceAll("^0", ""));
	    if ((filesizeTextField.getText().equals("0")) || (filesizeTextField.getText().length() == 0)) { filesizeTextField.setText("1"); }	    
	    try { Long.valueOf(filesizeTextField.getText()); } catch (NumberFormatException e) { filesizeTextField.setText(oldText); }
	    
	    calculateOTPKeyFileSize();
	});

//	UnitChoiceBox Listener
	unitChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
	{ @Override public void changed(ObservableValue<? extends Number> observableValue, Number num1, Number num2) { calculateOTPKeyFileSize(); } });

//	Initial FileSize Calculation
//	calculateOTPKeyFileSize();
    }    

    public void setCurrentDir(Path curDirPath, GUIFX guifx)
    {
	this.guifx = guifx;
	currentDirPath = curDirPath; 
	statusLabel1.setText("Current directory");
	statusLabel2.setText(currentDirPath.toAbsolutePath().toString());
//	guifx.userGuidanceMessage(guifx.CREATE_KEY, 64, false, false, false, true, VOI_CREATE_KEY, 0);
	guifx.userGuidanceMessage(guifx.CREATE_KEY, 64, false, false, false, true, Voice.VOI_CREATE_KEY, 0);
    }
        
    @FXML   private void increaseButtonOnAction(ActionEvent event) { changeSize(1); }
    @FXML   private void decreaseButtonOnAction(ActionEvent event) { changeSize(-1); }
    @FXML   private void increaseButtonOnMousePressed(MouseEvent event) { new Sound().play(guifx, Audio.SND_BUTTON,Audio.AUDIO_CODEC); changeSizeRepeaterOn(1); }
    @FXML   private void decreaseButtonOnMousePressed(MouseEvent event) { new Sound().play(guifx, Audio.SND_BUTTON,Audio.AUDIO_CODEC); changeSizeRepeaterOn(-1); }
    @FXML   private void increaseButtonOnMouseReleased(MouseEvent event) { changeSizeRepeaterOff(); }
    @FXML   private void decreaseButtonOnMouseReleased(MouseEvent event) { changeSizeRepeaterOff(); }

    private void changeSizeRepeaterOn(long step)
    {
	if ( repeaterTimeline != null ) { repeaterTimeline.stop(); }
        repeaterTimeline = new Timeline(new KeyFrame( Duration.millis(20), ae -> changeSize(step) ));
	repeaterTimeline.setCycleCount(Animation.INDEFINITE);
	repeaterTimeline.setDelay(Duration.millis(250));
	repeaterTimeline.play();
    }
    
    private void changeSizeRepeaterOff() { if ( repeaterTimeline != null ) { repeaterTimeline.stop(); } }
    
    private void changeSize(long l)
    {
	Long num = Long.valueOf(filesizeTextField.getText()); num+=l;
//	filesizeTextField.setText(num.toString());
	boolean valid = true; try { Long.valueOf(filesizeTextField.getText()); } catch (NumberFormatException e) { valid = false; }
	if ( num > 0 ) { filesizeTextField.setText(num.toString()); } 
    }

    private void calculateOTPKeyFileSize()
    {
	filesizeNumber = Long.valueOf(filesizeTextField.getText());
	Integer selectedIndexAsPower = unitChoiceBox.getSelectionModel().getSelectedIndex();
	double interimFactor = Math.pow(1024, selectedIndexAsPower);
//	factor = (new Double(interimFactor)).longValue();
	factor = (Double.doubleToLongBits(interimFactor));
	filesizeInBytes = filesizeNumber * factor;
	if (( filesizeInBytes > 0 ) && ( filesizeInBytes <= Long.MAX_VALUE )) { createButton.setDisable(false); } else { createButton.setDisable(true); }
	
	if (filenameTextField.getText().length() > 0)
	{
	    keyPath = Paths.get(currentDirPath.toAbsolutePath().toString(), filenameTextField.getText());
	    
	    String status1String = "File";
	    String status2String = keyPath.getParent().toAbsolutePath().toString() + File.separator + keyPath.getFileName().toString();
	    
	    if (( filesizeInBytes > 0 ) && ( filesizeInBytes <= Long.MAX_VALUE )) { status1String += " (" + Validate.getHumanSize(filesizeInBytes, 1,"Bytes") + ")"; }
	    statusLabel1.setText(status1String);
	    statusLabel2.setText(status2String);
	}
	else
	{
	    if (currentDirPath != null)
	    {
		statusLabel1.setText("Current directory");
		statusLabel2.setText(currentDirPath.toAbsolutePath().toString());
	    }
	}
	
    }
    @FXML
    private void createButtonAction(ActionEvent event)
    {
	new Sound().play(guifx, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	filenameTextField.setDisable(true);
	filesizeLabel.setDisable(true);
	filesizeTextField.setDisable(true);
	increaseButton.setDisable(true);
	decreaseButton.setDisable(true);
	untiLabel.setDisable(true);
	unitChoiceBox.setDisable(true);
	createButton.setDisable(true);

//	Compile key file path	
	keyPath = Paths.get(currentDirPath.toAbsolutePath().toString(), filenameTextField.getText());
	
//	Start creating OTP Key file
	statusLabel1.setText("Creating OTP Key File" + " (" + Validate.getHumanSize(filesizeInBytes, 1,"Bytes") + ")");
	statusLabel2.setText(keyPath.toAbsolutePath().toString());

	repeaterTimeline = new Timeline(new KeyFrame( Duration.millis(250), ae -> blinkStatusLabel1() ));
	repeaterTimeline.setCycleCount(Animation.INDEFINITE);
	repeaterTimeline.setDelay(Duration.millis(250));
	repeaterTimeline.play();

	progressBar.setDisable(false);
	


	
//	====================================================================================================================
//	Start writing key file
//	====================================================================================================================

	Thread createManualKeyThread; createManualKeyThread = new Thread(() ->
	{
	    if ( filesizeInBytes < bufferSize) { bufferSize =  filesizeInBytes.intValue(); }

	    boolean inputEnded = false;
	    long writeKeyFileChannelPosition = 0L;
	    long writeKeyFileChannelTransfered = 0L;
	    totalTranfered = 0L;
	    Long remainder = 0L;

    //      Write the keyfile to 1st partition
	    ByteBuffer  randomBuffer =	    ByteBuffer.allocate(bufferSize); randomBuffer.clear();

	    throughputClock = 0L;
	    lastThroughputClock = 0L;
	    realtimeBytesProcessed = 0L;
	    realtimeMiBPS = 0.0d;
	    
	    updateProgressTask = new TimerTask()
	    {
		@Override public void run()
		{
		    Platform.runLater(new Runnable(){ @Override public void run()
		    {
			throughputClock = System.nanoTime();
//			realtimeMiBPS = (realtimeBytesProcessed * (1000000d / (throughputClock - lastThroughputClock)));
			realtimeMiBPS = ((realtimeBytesProcessed * (1000000000d / (throughputClock - lastThroughputClock)))/(1024d*1024d)); // ui.test("FC BPS: " + realtimeMiBPS + "\r\n");
			if ( realtimeMiBPS > FinalCrypt.io_Throughput_Ceiling ) { FinalCrypt.io_Throughput_Ceiling = realtimeMiBPS; }
			lastThroughputClock = throughputClock; realtimeBytesProcessed = 0;
			
			progressBar.setProgress( (double)totalTranfered / filesizeInBytes); // percent needs to become factor in this gui
			guifx.processProgress(		     0,			   0,		    0,			0, realtimeMiBPS );
		    }});
		}
	    }; updateProgressTaskTimer = new java.util.Timer(); updateProgressTaskTimer.schedule(updateProgressTask, 0L, 200L);


	    new Sound().play(guifx, Audio.SND_ENCRYPTFILES,Audio.AUDIO_CODEC);

	    write1loop: while ( (totalTranfered < filesizeInBytes) && (! inputEnded ))
	    {
		remainder = (filesizeInBytes - totalTranfered);

		if ( remainder >= bufferSize )				    { randomBuffer = ByteBuffer.allocate(bufferSize); randomBuffer.clear(); }
		else if (( remainder > 0 ) && ( remainder < bufferSize ))   { randomBuffer = ByteBuffer.allocate(remainder.intValue()); randomBuffer.clear(); }
		else							    { inputEnded = true; }
		//		    getFCRandomBuffer(UI ui,		    int size, boolean extraSeed, boolean encrypt,    boolean print)
		randomBuffer = TRNG.getFCRandomBuffer(guifx, randomBuffer.capacity(),		   true,	    true, FinalCrypt.print);
		
    //          Write Device (randomBuffer3 became randomBuffer1)
		try (final SeekableByteChannel writeKeyFileChannel = Files.newByteChannel(keyPath, FinalCrypt.getEnumSet(EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE))))
		{
		    writeKeyFileChannel.position(writeKeyFileChannelPosition);
		    writeKeyFileChannelTransfered = writeKeyFileChannel.write(randomBuffer); randomBuffer.rewind(); realtimeBytesProcessed += writeKeyFileChannelTransfered;
		    totalTranfered += writeKeyFileChannelTransfered; 
//		    System.out.println("tot: " + filesizeInBytes + " trans: " + totalTranfered + " remain: " + remainder + " p: " + (double)totalTranfered / filesizeInBytes + "\r\n");

		    writeKeyFileChannelPosition += writeKeyFileChannelTransfered;

		    writeKeyFileChannel.close();
		} catch (IOException ex) { statusLabel1.setText("Error: " + ex.getMessage()); inputEnded = true; break; }
		randomBuffer.clear();
	    }
	    writeKeyFileChannelPosition = 0;                
	    writeKeyFileChannelTransfered = 0;                
	    inputEnded = false;

	    updateProgressTaskTimer.cancel(); updateProgressTaskTimer.purge();
	    realtimeMiBPS = 0d; realtimeBytesProcessed = 0;
	    guifx.processProgress(		     0,			   0,		    0,			0, realtimeMiBPS );
	    progressBar.setProgress( (double)totalTranfered / filesizeInBytes); // percent needs to become factor in this gui
	    
	    if (repeaterTimeline != null) { repeaterTimeline.stop(); }
	    Platform.runLater(new Runnable(){ @Override public void run()
	    {
		statusLabel1.setText("Created OTP Key File" + " (" + Validate.getHumanSize(filesizeInBytes, 1,"Bytes") + ")");
		guifx.userGuidanceMessage(guifx.SELECT_KEY_DIR, 64, false, false, true, false, Voice.VOI_SELECT_KEY_DIRECTORY, 0);
	    }});
	    
	    repeaterTimeline = new Timeline(new KeyFrame( Duration.millis(100), ae -> closeWindow() ));
	    repeaterTimeline.setCycleCount(1);
	    repeaterTimeline.setDelay(Duration.millis(1000));
	    repeaterTimeline.play();
	    
	});
	createManualKeyThread.setName("createManualKeyThread");
	createManualKeyThread.setDaemon(true);
	createManualKeyThread.start();


//	====================================================================================================================
//	Finieshed writing key file
//	====================================================================================================================
	
    }
        
    @FXML
    private void cancelButtonAction(ActionEvent event)
    {
	new Sound().play(guifx, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	Platform.runLater(new Runnable(){ @Override public void run()
	{
	    if (repeaterTimeline != null) { repeaterTimeline.stop(); statusLabel1.setText("Canceled"); } else { statusLabel1.setText("Closing"); }
	    statusLabel1.setVisible(true);
	    
	    new Sound().play(guifx, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);

	    repeaterTimeline = new Timeline(new KeyFrame( Duration.millis(100), ae -> closeWindow() ));
	    repeaterTimeline.setCycleCount(1);
	    repeaterTimeline.setDelay(Duration.millis(1000));
	    repeaterTimeline.play();
	}});
    }
    
    private void closeWindow()
    {
	Platform.runLater(new Runnable(){ @Override public void run()
	{
	    new Sound().play(guifx, Audio.SND_SHUTDOWN,Audio.AUDIO_CODEC);
	    guifx.updateFileChoosers(true, true); // Basically FileChoosers ComponentAlteration as guifx.updateFileChoosers(true, true); hanged sometimes.
	    Stage stage = (Stage) cancelButton.getScene().getWindow(); stage.close();		
	}});
    }

    private void blinkStatusLabel1()
    {
	Platform.runLater(new Runnable(){ @Override public void run()
	{
	    statusLabel1.setVisible(!statusLabel1.isVisible());
	}});
    }
    public static void main(String[] args)
    {
        launch(args);
    }

    @FXML private void complianceLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(guifx, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	new Sound().play(guifx, Audio.SND_OPEN,Audio.AUDIO_CODEC);
	Thread otpKeyURLThread;
	otpKeyURLThread = new Thread(() ->
	{
	    try {  Desktop.getDesktop().browse(new URI(FIP140_2_URL_STRING)); }
	    catch (URISyntaxException ex) { statusLabel1.setText(ex.getMessage()); }
	    catch (IOException ex) { statusLabel1.setText(ex.getMessage()); }
	});
	otpKeyURLThread.setName("otpKeyURLThread");
	otpKeyURLThread.setDaemon(true);
	otpKeyURLThread.start();
    }

    @FXML
    private void otpRulesOnMouseClicked(MouseEvent event)
    {
	new Sound().play(guifx, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	new Sound().play(guifx, Audio.SND_OPEN,Audio.AUDIO_CODEC);
	Thread otpKeyURLThread;
	otpKeyURLThread = new Thread(() ->
	{
	    try {  Desktop.getDesktop().browse(new URI(ONE_TIME_PAD_URL_STRING)); }
	    catch (URISyntaxException ex) { statusLabel1.setText(ex.getMessage()); }
	    catch (IOException ex) { statusLabel1.setText(ex.getMessage()); }
	});
	otpKeyURLThread.setName("otpKeyURLThread");
	otpKeyURLThread.setDaemon(true);
	otpKeyURLThread.start();
    }
}
