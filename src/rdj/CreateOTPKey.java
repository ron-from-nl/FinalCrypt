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
import java.security.SecureRandom;
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
import javafx.scene.media.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.sound.sampled.*;

public class CreateOTPKey extends Application implements Initializable
{
//    private final Media WAV_SND_BUTTON =		    new Media(getClass().getResource("/rdj/audio/wav/sounds/snd_button.wav").toExternalForm());
//    private final Media WAV_SND_ENCRYPTFILES =		    new Media(getClass().getResource("/rdj/audio/wav/sounds/snd_encrypt_files.wav").toExternalForm());
//    private final Media WAV_SND_INPUT_FAIL =		    new Media(getClass().getResource("/rdj/audio/wav/sounds/snd_input_fail.wav").toExternalForm());
//    private final Media WAV_SND_INPUT_OK =		    new Media(getClass().getResource("/rdj/audio/wav/sounds/snd_input_ok.wav").toExternalForm());
//    private final Media WAV_SND_KEYPRESS =		    new Media(getClass().getResource("/rdj/audio/wav/sounds/snd_key_press.wav").toExternalForm());
//    private final Media WAV_SND_OPEN =			    new Media(getClass().getResource("/rdj/audio/wav/sounds/snd_open.wav").toExternalForm());
//    private final Media WAV_SND_SHUTDOWN =		    new Media(getClass().getResource("/rdj/audio/wav/sounds/snd_shutdown.wav").toExternalForm());
//
//    private final Media WAV_VOI_CREATE_KEY =		    new Media(getClass().getResource("/rdj/audio/wav/voice/voi_create_key.wav").toExternalForm());
//    private final Media WAV_VOI_SELECT_KEY =		    new Media(getClass().getResource("/rdj/audio/wav/voice/voi_select_key.wav").toExternalForm());
//
//    private final Media MP3_SND_BUTTON =		    new Media(getClass().getResource("/rdj/audio/mp3/sounds/snd_button.mp3").toExternalForm());
//    private final Media MP3_SND_ENCRYPTFILES =		    new Media(getClass().getResource("/rdj/audio/mp3/sounds/snd_encrypt_files.mp3").toExternalForm());
//    private final Media MP3_SND_INPUT_FAIL =		    new Media(getClass().getResource("/rdj/audio/mp3/sounds/snd_input_fail.mp3").toExternalForm());
//    private final Media MP3_SND_INPUT_OK =		    new Media(getClass().getResource("/rdj/audio/mp3/sounds/snd_input_ok.mp3").toExternalForm());
//    private final Media MP3_SND_KEYPRESS =		    new Media(getClass().getResource("/rdj/audio/mp3/sounds/snd_key_press.mp3").toExternalForm());
//    private final Media MP3_SND_OPEN =			    new Media(getClass().getResource("/rdj/audio/mp3/sounds/snd_open.mp3").toExternalForm());
//    private final Media MP3_SND_SHUTDOWN =		    new Media(getClass().getResource("/rdj/audio/mp3/sounds/snd_shutdown.mp3").toExternalForm());
//
//    private final Media MP3_VOI_CREATE_KEY =		    new Media(getClass().getResource("/rdj/audio/mp3/voice/voi_create_key.mp3").toExternalForm());
//    private final Media MP3_VOI_SELECT_KEY =		    new Media(getClass().getResource("/rdj/audio/mp3/voice/voi_select_key.mp3").toExternalForm());

    private final String SND_ALARM =			    "/rdj/audio/sounds/alarm";
    private final String SND_ALERT =			    "/rdj/audio/sounds/alert";
    private final String SND_BUTTON =			    "/rdj/audio/sounds/button";
    private final String SND_DECRYPTFILES =		    "/rdj/audio/sounds/decrypt_files";
    private final String SND_ENCRYPTFILES =		    "/rdj/audio/sounds/encrypt_files";
    private final String SND_ERROR =			    "/rdj/audio/sounds/error";
    private final String SND_INPUT_FAIL =		    "/rdj/audio/sounds/input_fail";
    private final String SND_INPUT_OK =			    "/rdj/audio/sounds/input_ok";
    private final String SND_KEYPRESS =			    "/rdj/audio/sounds/key_press";
    private final String SND_MESSAGE =			    "/rdj/audio/sounds/message";
    private final String SND_OFF =			    "/rdj/audio/sounds/off";
    private final String SND_ON =			    "/rdj/audio/sounds/on";
    private final String SND_OPEN =			    "/rdj/audio/sounds/open";
    private final String SND_READY =			    "/rdj/audio/sounds/ready";
    private final String SND_SELECT =			    "/rdj/audio/sounds/select";
    private final String SND_SELECTINVALID =		    "/rdj/audio/sounds/select_invalid";
    private final String SND_SELECTKEY =			    "/rdj/audio/sounds/select_key";
    private final String SND_SOUND_DISABLED =		    "/rdj/audio/sounds/sound_disabled";
    private final String SND_SOUND_ENABLED =		    "/rdj/audio/sounds/sound_enabled";
    private final String SND_SHUTDOWN =			    "/rdj/audio/sounds/shutdown";
    private final String SND_STARTUP =			    "/rdj/audio/sounds/startup";

    private final String VOI_CLONE_KEY_DEVICE =		    "/rdj/audio/voice/clone_key_device";
    private final String VOI_CONFIRM_PASS_WITH_ENTER =	    "/rdj/audio/voice/confirm_password_with_enter";
    private final String VOI_CREATE_KEY =		    "/rdj/audio/voice/create_key";
    private final String VOI_CREATE_KEY_DEVICE =		    "/rdj/audio/voice/create_key_device";
    private final String VOI_DECRYPT_FILES =		    "/rdj/audio/voice/decrypt_files";
    private final String VOI_DECRYPTING_FILES =		    "/rdj/audio/voice/decrypting_files";
    private final String VOI_ENCRYPT_FILES =		    "/rdj/audio/voice/encrypt_files";
    private final String VOI_ENCRYPTING_FILES =		    "/rdj/audio/voice/encrypting_files";
    private final String VOI_ENCRYPT_OR_DECRYPT_FILES =	    "/rdj/audio/voice/encrypt_or_decrypt_files";
    private final String VOI_SCANNING_FILES =		    "/rdj/audio/voice/scanning_files";
    private final String VOI_SELECT_FILES =		    "/rdj/audio/voice/select_files";
    private final String VOI_SELECT_KEY =		    "/rdj/audio/voice/select_key";
    private final String VOI_VOICE_DISABLED =		    "/rdj/audio/voice/voice_disabled";
    private final String VOI_VOICE_ENABLED =		    "/rdj/audio/voice/voice_enabled";
    private final String VOI_WRONG_KEY_OR_PASSWORD =	    "/rdj/audio/voice/wrong_key_or_password";

    public final int WAV =				    0;
    public final int OGG =				    1; // Not supported
    public final int AIFF =				    2; // Not supported
    public final int MP3 =				    3;

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
//    private Sound sound;
    private AudioClip audioClipSounds;
    private AudioClip audioClipVoice;
    private AudioInputStream audioInputStreamSounds;
    private AudioInputStream audioInputStreamVoice;
    private Clip clipSounds;
    private Clip clipVoice;
    @FXML
    private Label otpRulesLabel;

//    public CreateOTPKey(GUIFX guifx)
//    {
//	this.guifx = guifx;
//    }
    
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
	    else { play(SND_KEYPRESS, guifx.AUDIO_CODEC); filenameTextField.setText(filenameTextField.getText().replaceAll(regex, "")); } 

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
	guifx.userGuidanceMessage(guifx.CREATE_KEY, 64, false, false, false, true, VOI_CREATE_KEY, 0);
    }
        
    @FXML
    private void increaseButtonOnAction(ActionEvent event) { changeSize(1); }

    @FXML
    private void decreaseButtonOnAction(ActionEvent event) { changeSize(-1); }
    

    @FXML
    private void increaseButtonOnMousePressed(MouseEvent event) { play(SND_BUTTON, guifx.AUDIO_CODEC); changeSizeRepeaterOn(1); }

    @FXML
    private void decreaseButtonOnMousePressed(MouseEvent event) { play(SND_BUTTON, guifx.AUDIO_CODEC); changeSizeRepeaterOn(-1); }

    @FXML
    private void increaseButtonOnMouseReleased(MouseEvent event) { changeSizeRepeaterOff(); }

    @FXML
    private void decreaseButtonOnMouseReleased(MouseEvent event) { changeSizeRepeaterOff(); }

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
	factor = (new Double(interimFactor)).longValue();
	filesizeInBytes = filesizeNumber * factor;
	if (( filesizeInBytes > 0 ) && ( filesizeInBytes <= Long.MAX_VALUE )) { createButton.setDisable(false); } else { createButton.setDisable(true); }
	
	if (filenameTextField.getText().length() > 0)
	{
	    keyPath = Paths.get(currentDirPath.toAbsolutePath().toString(), filenameTextField.getText());
	    
	    String status1String = "File";
	    String status2String = keyPath.getParent().toAbsolutePath().toString() + File.separator + keyPath.getFileName().toString();
	    
	    if (( filesizeInBytes > 0 ) && ( filesizeInBytes <= Long.MAX_VALUE )) { status1String += " (" + Validate.getHumanSize(filesizeInBytes, 1) + ")"; }
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
	play(SND_BUTTON, guifx.AUDIO_CODEC); 
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
	statusLabel1.setText("Creating OTP Key File" + " (" + Validate.getHumanSize(filesizeInBytes, 1) + ")");
	statusLabel2.setText(keyPath.toAbsolutePath().toString());

	repeaterTimeline = new Timeline(new KeyFrame( Duration.millis(250), ae -> blinkStatusLabel1() ));
	repeaterTimeline.setCycleCount(Animation.INDEFINITE);
	repeaterTimeline.setDelay(Duration.millis(250));
	repeaterTimeline.play();

	progressBar.setDisable(false);
	


	
//	====================================================================================================================
//	Start writing key file
//	====================================================================================================================

	Thread createKeyThread; createKeyThread = new Thread(() ->
	{
	    if ( filesizeInBytes < bufferSize) { bufferSize =  filesizeInBytes.intValue(); }

	    boolean inputEnded = false;
	    long writeKeyFileChannelPosition = 0L;
	    long writeKeyFileChannelTransfered = 0L;
	    totalTranfered = 0L;
	    Long remainder = 0L;

    //      Write the keyfile to 1st partition
	    byte[]      randomBytes1 =	    new byte[bufferSize];
//	    byte[]      randomBytes2 =	    new byte[bufferSize];
//	    byte[]      randomBytes3 =	    new byte[bufferSize];
	    ByteBuffer  randomBuffer1 =	    ByteBuffer.allocate(bufferSize); randomBuffer1.clear();
//	    ByteBuffer  randomBuffer2 =	    ByteBuffer.allocate(bufferSize); randomBuffer2.clear();
//	    ByteBuffer  randomBuffer3 =	    ByteBuffer.allocate(bufferSize); randomBuffer3.clear();

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

	    SecureRandom random = new SecureRandom();

	    play(SND_ENCRYPTFILES, guifx.AUDIO_CODEC);

	    write1loop: while ( (totalTranfered < filesizeInBytes) && (! inputEnded ))
	    {
		remainder = (filesizeInBytes - totalTranfered);

		if	    ( remainder >= bufferSize )				
		{
		    randomBytes1 =	    new byte[bufferSize];
//		    randomBytes2 =	    new byte[bufferSize];
//		    randomBytes3 =	    new byte[bufferSize];
		    randomBuffer1 =	    ByteBuffer.allocate(bufferSize); randomBuffer1.clear();
//		    randomBuffer2 =	    ByteBuffer.allocate(bufferSize); randomBuffer2.clear();
//		    randomBuffer3 =	    ByteBuffer.allocate(bufferSize); randomBuffer3.clear();
		}
		else if (( remainder > 0 ) && ( remainder < bufferSize ))
		{
		    randomBytes1 =	    new byte[remainder.intValue()];
//		    randomBytes2 =	    new byte[remainder.intValue()];
//		    randomBytes3 =	    new byte[remainder.intValue()];
		    randomBuffer1 =	    ByteBuffer.allocate(remainder.intValue()); randomBuffer1.clear();
//		    randomBuffer2 =	    ByteBuffer.allocate(remainder.intValue()); randomBuffer2.clear();
//		    randomBuffer3 =	    ByteBuffer.allocate(remainder.intValue()); randomBuffer3.clear();
		}
		else							{ inputEnded = true; }
		
//		Randomize raw key or write raw key straight to partition
		random.nextBytes(randomBytes1); randomBuffer1.put(randomBytes1); randomBuffer1.flip();
//		random.nextBytes(randomBytes2); randomBuffer2.put(randomBytes2); randomBuffer2.flip();
//		randomBuffer3 = FinalCrypt.encryptBuffer(randomBuffer1, randomBuffer2, 0, false); // Encrypt

    //          Write Device (randomBuffer3 became randomBuffer1)
		try (final SeekableByteChannel writeKeyFileChannel = Files.newByteChannel(keyPath, EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.SYNC)))
		{
		    writeKeyFileChannel.position(writeKeyFileChannelPosition);
		    writeKeyFileChannelTransfered = writeKeyFileChannel.write(randomBuffer1); randomBuffer1.rewind(); realtimeBytesProcessed += writeKeyFileChannelTransfered;
		    totalTranfered += writeKeyFileChannelTransfered; 
//		    System.out.println("tot: " + filesizeInBytes + " trans: " + totalTranfered + " remain: " + remainder + " p: " + (double)totalTranfered / filesizeInBytes + "\r\n");

		    writeKeyFileChannelPosition += writeKeyFileChannelTransfered;

		    writeKeyFileChannel.close();
		} catch (IOException ex) { statusLabel1.setText("Error: " + ex.getMessage()); inputEnded = true; break; }
		randomBuffer1.clear(); randomBuffer1.clear(); randomBuffer1.clear();
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
		statusLabel1.setText("Created OTP Key File" + " (" + Validate.getHumanSize(filesizeInBytes, 1) + ")");
		guifx.userGuidanceMessage(guifx.SELECT_KEY_MAP, 64, false, false, true, false, VOI_SELECT_KEY, 0);
	    }});
	    
	    repeaterTimeline = new Timeline(new KeyFrame( Duration.millis(100), ae -> closeWindow() ));
	    repeaterTimeline.setCycleCount(1);
	    repeaterTimeline.setDelay(Duration.millis(1000));
	    repeaterTimeline.play();
	    
	});
	createKeyThread.setName("createKeyThread");
	createKeyThread.setDaemon(true);
	createKeyThread.start();


//	====================================================================================================================
//	Finieshed writing key file
//	====================================================================================================================
	
    }
        
    @FXML
    private void cancelButtonAction(ActionEvent event)
    {
	play(SND_BUTTON, guifx.AUDIO_CODEC);
	Platform.runLater(new Runnable(){ @Override public void run()
	{
	    if (repeaterTimeline != null) { repeaterTimeline.stop(); statusLabel1.setText("Canceled"); } else { statusLabel1.setText("Closing"); }
	    statusLabel1.setVisible(true);
	    
	    play(SND_INPUT_FAIL, guifx.AUDIO_CODEC);

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
	    play(SND_SHUTDOWN, guifx.AUDIO_CODEC);
	    guifx.updateFileChoosers2(true, true); // Basically FileChoosers ComponentAlteration as guifx.updateFileChoosers(true, true); hanged sometimes.
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
	play(SND_BUTTON, guifx.AUDIO_CODEC);
	play(SND_OPEN, guifx.AUDIO_CODEC);
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
	play(SND_BUTTON, guifx.AUDIO_CODEC);
	play(SND_OPEN, guifx.AUDIO_CODEC);
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
        
    synchronized public void play(String audio, int audio_codec)
    {
	Media media;
	switch (audio_codec)
	{
	    case WAV:		media = new Media(getClass().getResource(audio + ".wav").toExternalForm());		break;
	    case OGG:		media = new Media(getClass().getResource(audio + ".ogg").toExternalForm());		break;
	    case AIFF:		media = new Media(getClass().getResource(audio + ".aiff").toExternalForm());		break;
	    case MP3:		media = new Media(getClass().getResource(audio + ".mp3").toExternalForm());		break;
	    default:		media = new Media(getClass().getResource(audio + ".wav").toExternalForm());		break;
	}
	
	if (audio != null) 
	{
	    if ( (audio.contains("sounds")) ) // new sound added to any other audio playing
	    {
		if (guifx.sound_Is_Enabled)
		{
		    Platform.runLater(new Runnable(){ @Override public void run()
		    {
			if (media != null) 
			{
			    if ( media.getSource().contains("sounds") )
			    {
				Thread playSoundThread = new Thread(() ->
				{
				    if (guifx.sound_Is_Enabled) { audioClipSounds = new AudioClip(media.getSource()); audioClipSounds.play(); /*(" " + play.isPlaying() + "\r\n");*/ }
				});
				playSoundThread.setName("playSoundThread");
				playSoundThread.setDaemon(true);
				playSoundThread.start();
			    }
			    else if ( media.getSource().contains("voice") )
			    {
				if ( guifx.voice_Is_Enabled)
				{
				    if ( (audioClipVoice != null) && ( audioClipVoice.isPlaying() )) { audioClipVoice.stop(); }
				    audioClipVoice = new AudioClip(media.getSource()); audioClipVoice.play(); /*test(" " + play.isPlaying() + "\r\n");*/ 
				}
			    }
			    else { guifx.log("Alert: play(" + media.getSource() + ") not recognized!\r\n", true, true, true, true, false); }
			}
		    }});
		}
	    }
	    else if ( audio.contains("voice") )
	    {
		if (guifx.voice_Is_Enabled)
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
				    if (guifx.sound_Is_Enabled) { audioClipSounds = new AudioClip(media.getSource()); audioClipSounds.play(); /*(" " + play.isPlaying() + "\r\n");*/ }
				});
				playVoiceThread.setName("playVoiceThread");
				playVoiceThread.setDaemon(true);
				playVoiceThread.start();
			    }
			    else if ( media.getSource().contains("voice") )
			    {
				if ( guifx.voice_Is_Enabled)
				{
				    if ( (audioClipVoice != null) && ( audioClipVoice.isPlaying() )) { audioClipVoice.stop(); }
				    audioClipVoice = new AudioClip(media.getSource()); audioClipVoice.play(); /*test(" " + play.isPlaying() + "\r\n");*/ 
				}
			    }
			    else { guifx.log("Alert: play(" + media.getSource() + ") not recognized!\r\n", true, true, true, true, false); }
			}
		    }});
		}
	    }
	    else { guifx.log("Alert: " + this.getClass().getSimpleName() + ".play(" + media.getSource() + ") not recognized!\r\n", true, true, true, true, false); }
	}
    }        
}
