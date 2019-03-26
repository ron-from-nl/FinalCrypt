/*
 * Copyright © 2017 Ron de Jong (ronuitzaandam@gmail.com).
 *
 * This is free software; you can redistribute it 
 * under the terms of the Creative Commons License
 * Creative Commons License: (CC BY-NC-ND 4.0) as published by
 * https://creativecommons.org/licenses/by-nc-nd/4.0/ ; either
 * version 4.0 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International Public License for more details.
 *
 * You should have received a copy of the Creative Commons 
 * Public License License along with this software;
 */

package rdj;

import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.*;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.PasswordField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.*;
import javafx.util.Duration;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

public class GUIFX extends Application implements UI, Initializable
{
    @FXML   private TabPane tab;
    @FXML   private Tab encryptTab;
    @FXML   private Tab logTab;
    @FXML   private TextArea logTextArea;
    @FXML   private Button encryptButton;
    @FXML   private ProgressBar filesProgressBar;
    @FXML   private ProgressBar fileProgressBar;
    @FXML   private Label statusLabel;    
    @FXML   private SwingNode keyFileSwingNode;
    @FXML   private Object root;
    @FXML   private ToggleButton pauseToggleButton;
    @FXML   private Button stopButton;
    @FXML   private Label copyrightLabel;
//    @FXML   private ProgressIndicator cpuIndicator;
    @FXML   private SwingNode targetFileSwingNode;
    @FXML   private Button decryptButton;
    @FXML   private Label keyNameLabel;
    @FXML   private Label keyTypeLabel;
    @FXML   private Label keySizeLabel;
    @FXML   private Label encryptedLabel;
    @FXML   private Label encryptableSizeLabel;
    @FXML   private Label encryptedSizeLabel;
    @FXML   private Label decryptableLabel;
    @FXML   private Label decryptedLabel;
    @FXML   private Label decryptableSizeLabel;
    @FXML   private Label decryptedSizeLabel;
    @FXML   private Label unencryptableLabel;
    @FXML   private Label undecryptableSizeLabel;
    @FXML   private Label unencryptableSizeLabel;
    @FXML   private Label undecryptableLabel;
    @FXML   private Label encryptableLabel;
    @FXML   private Label invalidFilesLabel;
    @FXML   private Label totalFilesLabel;
    @FXML   private Label validDevicesLabel;
    @FXML   private Label validDevicesSizeLabel;
    @FXML   private Label validPartitionsLabel;
    @FXML   private Label validPartitionsSizeLabel;
    @FXML   private Label invalidFilesSizeLabel;
    @FXML   private Label validFilesLabel;
    @FXML   private Label validFilesSizeLabel;
    @FXML   private Label filesSizeLabel;
    @FXML   private Label emptyFilesLabel;
    @FXML   private Label symlinkFilesLabel;
    @FXML   private Label unreadableFilesLabel;
    @FXML   private Label unwritableFilesLabel;
    @FXML   private Label hiddenFilesLabel;
    @FXML   private Label targetWarningLabel;
    @FXML   private Button keyButton;
    @FXML   private Label totalTimeLabel;
    @FXML   private Label remainingTimeLabel;
    @FXML   private Label elapsedTimeLabel;
    @FXML   private Label emptyFilesHeaderLabel;
    @FXML   private Label symlinkFilesHeaderLabel;
    @FXML   private Label unreadableFilesHeaderLabel;
    @FXML   private Label unwritableFilesHeaderLabel;
    @FXML   private Label hiddenFilesHeaderLabel;
    @FXML   private Button websiteButton;
    @FXML   private Label checksumLabel;
    @FXML   private GridPane dashboardGridPane;
    @FXML   private ToggleButton encryptionModeToggleButton;
    @FXML   private Tooltip encryptionModeToolTip;
    @FXML   private AnchorPane encryptionModeAnchorPane;
    @FXML   private PasswordField pwdField;
    @FXML   private Button checkUpdateButton;
    @FXML   private AnchorPane mainAnchorPane;
    @FXML   private Label checksumHeader;
    @FXML   private ImageView keyImageView;
    @FXML   private Label passwordHeaderLabel;
    @FXML   private Label remainingTimeHeaderLabel;
    @FXML   private Label elapsedTimeHeaderLabel;
    @FXML   private Label totalTimeHeaderLabel;
    @FXML   private Label userGuidanceLabel;
    @FXML   private Label topleftLabel;
    @FXML   private Label toprightLabel;
    @FXML   private Label bottomrightLabel;
    @FXML   private Label bottomleftLabel;
    @FXML   private BorderPane targetFileFoil;
    @FXML   private BorderPane keyFileFoil;

    private final int FILE_CHOOSER_FONT_SIZE =		13;
    
    private final int NONE =				0;
    private final int ENCRYPT_MODE =			1;
    private final int DECRYPT_MODE =			2;
    private final int CREATE_KEYDEV_MODE =		3;
    private final int CLONE_KEYDEV_MODE	=		4;
    private int processRunningMode =			NONE;

    private final String MAC_ON =			"MAC ON";
    private final String MAC_OFF =			"MAC OFF";
    private final String MAC_OFF_Q =			"MAC OFF ?";

    public final String CREATE_KEY =			"Create Key";
    public final String CREATE_KEYDEV =			"Create Key Device";
    public final String CLONE_KEYDEV =			"Clone Key Device";

    public final String SELECT_KEY =			"Select Key";
    public final String PASSWORD_ENTER =		"Password<Enter>";
    public final String SELECT_FILES =			"Select Files";

    public final String SCANNING_FILES =		"Scanning Files";
    public final String WRONG_KEY_PASS =		"Wrong Key / Pass ?";

    public final String ENCRYPT_FILES =			"Encrypt Files";
    public final String DECRYPT_FILES =			"Decrypt Files";
    public final String EN_DECRYPT_FILES =		"Encrypt • Decrypt Files";

    public final String ENCRYPTING_FILES =		"Encrypting Files";
    public final String DECRYPTING_FILES =		"Decrypting Files";

    private final String USER_GUID_TEXT_FILL_BASE =	"#504030";// #5A2D0C #663B1B
    private final String USER_GUID_TEXT_FILL_HIGH =	"#BBBBBB";

    private final int MAIN_TIMELINE_INTERVAL_PERIOD =	50;
    private final double LOAD_MONITOR_MS_INTERVAL =	250d;
    private final double LOAD_MANAGER_MS_INTERVAL =	250d;
    private final double ARROWS_OPACITY_MAX =		0.7d;
    private final double LOADHIGH_THRESHOLD =		0.95d; // 0.0 - 1.0
    private final double LOAD_HIGH_MS_TIMEOUT =		1000.0d;
    private final double LOAD_LOW_MS_TIMEOUT =		5000.0d;
    
    public final String OS_NAME =			System.getProperty("os.name");
    public final String OS_ARCH =			System.getProperty("os.arch");
    public final String OS_VERSION =			System.getProperty("os.version");
    public final String JAVA_VENDER =			System.getProperty("java.vendor");
    public final String JAVA_VERSION =			System.getProperty("java.version");
    public final String CLASS_VERSION =			System.getProperty("java.class.version");
    
    public final Media SND_ALARM =			new Media(getClass().getResource("/rdj/sounds/snd_alarm.mp3").toExternalForm());
    public final Media SND_ALERT =		        new Media(getClass().getResource("/rdj/sounds/snd_alert.mp3").toExternalForm());
    public final Media SND_BUTTON =			new Media(getClass().getResource("/rdj/sounds/snd_button.mp3").toExternalForm());
    public final Media SND_DECRYPTFILES =	        new Media(getClass().getResource("/rdj/sounds/snd_decrypt_files.mp3").toExternalForm());
    public final Media SND_ENCRYPTFILES =		new Media(getClass().getResource("/rdj/sounds/snd_encrypt_files.mp3").toExternalForm());
    public final Media SND_ERROR =			new Media(getClass().getResource("/rdj/sounds/snd_error.mp3").toExternalForm());
    public final Media SND_MESSAGE =			new Media(getClass().getResource("/rdj/sounds/snd_message.mp3").toExternalForm());
    public final Media SND_OFF =			new Media(getClass().getResource("/rdj/sounds/snd_off.mp3").toExternalForm());
    public final Media SND_ON =				new Media(getClass().getResource("/rdj/sounds/snd_on.mp3").toExternalForm());
    public final Media SND_OPEN =			new Media(getClass().getResource("/rdj/sounds/snd_open.mp3").toExternalForm());
    public final Media SND_READY =			new Media(getClass().getResource("/rdj/sounds/snd_ready.mp3").toExternalForm());
    public final Media SND_SELECT =			new Media(getClass().getResource("/rdj/sounds/snd_select.mp3").toExternalForm());
    public final Media SND_SELECTINVALID =		new Media(getClass().getResource("/rdj/sounds/snd_select_invalid.mp3").toExternalForm());
    public final Media SND_SELECTKEY =			new Media(getClass().getResource("/rdj/sounds/snd_select_key.mp3").toExternalForm());
    public final Media SND_SHUTDOWN =			new Media(getClass().getResource("/rdj/sounds/snd_shutdown.mp3").toExternalForm());
    public final Media SND_STARTUP =			new Media(getClass().getResource("/rdj/sounds/snd_startup.mp3").toExternalForm());
    public final Media SND_TYPEWRITER =			new Media(getClass().getResource("/rdj/sounds/snd_typewriter.mp3").toExternalForm());

    public final Media VOI_CLONE_KEY_DEVICE =		new Media(getClass().getResource("/rdj/sounds/voi_clone_key_device.mp3").toExternalForm());
    public final Media VOI_CONFIRM_PASS_WITH_ENTER =	new Media(getClass().getResource("/rdj/sounds/voi_confirm_password_with_enter.mp3").toExternalForm());
    public final Media VOI_CREATE_KEY =			new Media(getClass().getResource("/rdj/sounds/voi_create_key.mp3").toExternalForm());
    public final Media VOI_CREATE_KEY_DEVICE =		new Media(getClass().getResource("/rdj/sounds/voi_create_key_device.mp3").toExternalForm());
    public final Media VOI_DECRYPT_FILES =		new Media(getClass().getResource("/rdj/sounds/voi_decrypt_files.mp3").toExternalForm());
    public final Media VOI_DECRYPTING_FILES =		new Media(getClass().getResource("/rdj/sounds/voi_decrypting_files.mp3").toExternalForm());
    public final Media VOI_ENCRYPT_FILES =		new Media(getClass().getResource("/rdj/sounds/voi_encrypt_files.mp3").toExternalForm());
    public final Media VOI_ENCRYPTING_FILES =		new Media(getClass().getResource("/rdj/sounds/voi_encrypting_files.mp3").toExternalForm());
    public final Media VOI_ENCRYPT_OR_DECRYPT_FILES =	new Media(getClass().getResource("/rdj/sounds/voi_encrypt_or_decrypt_files.mp3").toExternalForm());
    public final Media VOI_SCANNING_FILES =		new Media(getClass().getResource("/rdj/sounds/voi_scanning_files.mp3").toExternalForm());
    public final Media VOI_SELECT_FILES =		new Media(getClass().getResource("/rdj/sounds/voi_select_files.mp3").toExternalForm());
    public final Media VOI_SELECT_KEY =			new Media(getClass().getResource("/rdj/sounds/voi_select_key.mp3").toExternalForm());
    public final Media VOI_WRONG_KEY_OR_PASSWORD =	new Media(getClass().getResource("/rdj/sounds/voi_wrong_key_or_password.mp3").toExternalForm());

    private double load_High_MS_Passed =		0.0d;
    private double load_Low_MS_Passed =			0.0d;
    private double arrowsfadestep =			(ARROWS_OPACITY_MAX / (Double.valueOf(1000.0 / MAIN_TIMELINE_INTERVAL_PERIOD).intValue()) * 2); // step = max / FPS
    private double arrowsfadevar =			0.0d;
    private double load_Monitor_MS_Passed =		0.0d;
    private double load_Manager_MS_Passed =		0.0d;

    private final Timeline UPDATE_CLOCKS_TIMELINE =	new Timeline ( new KeyFrame( Duration.seconds(1), ae -> updateClocks()) );			

    private final Timeline FLASH_MAC_MODE_TIMELINE =	new Timeline
    (
	     new KeyFrame(Duration.seconds( 0.0 ), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("WARNING");} )
	    ,new KeyFrame(Duration.seconds( 0.25), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("");} )
	    ,new KeyFrame(Duration.seconds( 0.50), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("WARNING");} )
	    ,new KeyFrame(Duration.seconds( 0.75), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("");} )
	    ,new KeyFrame(Duration.seconds( 1.0 ), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("WARNING");} )
	    ,new KeyFrame(Duration.seconds( 1.25), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("");} )
	    ,new KeyFrame(Duration.seconds( 1.50), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("white")); encryptionModeToggleButton.setText(MAC_OFF);} )
	    ,new KeyFrame(Duration.seconds( 2.50), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("white"));   encryptionModeToggleButton.setText("");} )
	    ,new KeyFrame(Duration.seconds( 2.75), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("white")); encryptionModeToggleButton.setText("");} )
    );
    
    private final Timeline PAUSE_TIMELINE = new Timeline(new KeyFrame( Duration.millis(250), ae -> 
    {
	if ( pauseToggleButton.getText().length() == 0 ) { pauseToggleButton.setText(FinalCrypt.UTF8_PAUSE_SYMBOL); } else { pauseToggleButton.setText(""); }
    }));

    private final Timeline AUTO_DISABLE_ARMING_MAC_MODE_TIMELINE = new Timeline( new KeyFrame(Duration.seconds( 8.0), evt -> { disarmDisableMACMode(); } ) );

    private double focusAngle;
    private double focusDistance;
    private double centerX;
    private double centerY;
    private double radius;
    private boolean proportional;
    private double variable;
    private double endX;
    private double startX;
    private double stepX;
    private double fadevar;
    private double blurvar;

    private RadialGradient textLabelGradient2;

    private Timeline         textLabelTimeline = new Timeline(new KeyFrame( Duration.millis(100), ae ->
    {
	textLabelGradient2 = new RadialGradient
	(
	    focusAngle, focusDistance, centerX, centerY, radius, proportional, CycleMethod.NO_CYCLE, new Stop[]
	    {
		new Stop(0, Color.valueOf(USER_GUID_TEXT_FILL_HIGH))
		,new Stop(1, Color.valueOf(USER_GUID_TEXT_FILL_BASE))
	    }
	);
	variable += stepX; if (variable >= endX ) { variable = startX; }
	centerX = variable;

	userGuidanceLabel.setTextFill(textLabelGradient2);
    }));
    private Stage stage;

    private FinalCrypt finalCrypt;
    private UI ui;
    private GUIFX guifx;
    
    private JFileChooser targetFileChooser;
    private boolean negatePattern;
    public  JFileChooser keyFileChooser;
    private JButton targetFileDeleteButton;
    private JButton keyFileDeleteButton;
    private Version version;

    private boolean processRunning;
    private MBeanServer mBeanServer;
    private ObjectName attributeObjectName;
    private AttributeList attribList;
    private Attribute att;
    private Double value;
    private FileFilter nonFinalCryptFilter;
    private FileNameExtensionFilter finalCryptFilter;
    private int lineCounter;
    private Configuration configuration;
    private FCPath keyFCPath;
    private boolean symlink = false;
    private final String procCPULoadAttribute = "ProcessCpuLoad";
    
    private FCPathList targetFCPathList; // Main List

    // Filtered Lists
    private FCPathList decryptedList; 
    private FCPathList encryptableList;

    private FCPathList encryptedList; 
    private FCPathList decryptableList;
    
    private FCPathList emptyList; 
    private FCPathList symlinkList;
    private FCPathList unreadableList;
    private FCPathList unwritableList;
    private FCPathList hiddenList;

    private FCPathList newEncryptedList;
    private FCPathList unencryptableList;
    private FCPathList newDecryptedList;
    private FCPathList undecryptableList;
    private FCPathList invalidFilesList;
    
    private long	bytesTotal;	
    private long	bytesProcessed;
    private double	totalToProcessedRatio;
   
    private Calendar	startTimeCalendar;
    private Calendar	start2TimeCalendar;
    private Calendar	nowTimeCalendar;
    private Calendar	elapsedTimeCalendar;
   
    private Calendar	totalTimeCalendar;
    private Calendar	remainingTimeCalendar;
    private double	megaBytesPerSecond;
    private int offSetHours;
    private int offSetMinutes;
    private int offSetSeconds;
    private Calendar offsetTimeCalendar;
    private boolean clockUpdated;
    private Timer updateDashboardTaskTimer;
    private String pattern;
    private Tooltip checksumTooltip;
    private boolean keySourceChecksumReadEnded;
    private boolean keySourceChecksumReadCanceled;
    private Stage createOTPKeyStage;
    private CreateOTPKey createOTPKey;
    private Preferences prefs;
    private long now;
    private boolean isCalculatingCheckSum;
    private long lastRawModeClicked;

    private boolean settingPassword;

    private double fontsizefactor;
    private String fadeInMessage;
    private boolean bottomleftLabelEnabled;
    private boolean topleftLabelEnabled;
    private boolean toprightLabelEnabled;
    private boolean bottomrightLabelEnabled;
    
    private TimerTask updateDashboardTask;

    private Timeline mainTimeline;
    private double userLoadPerc;
    @FXML   private Label targetLabel1;
    @FXML   private Label keyLabel1;
    @FXML   private Canvas sysMonCanvas;
    private GraphicsContext sysmon;
    @FXML   private Tooltip sysMonTooltip;
    private int sysmonOffSetX;
    private File curTargetDir;
    private File curKeyDir;
    private File upTargetDir;
    private File upKeyDir;
    private boolean update_System_Monitor_Enabled;
    private double userloadPercTest;
    private double userMemPercTest;
    private double throughputPercTest;
    @FXML   private AnchorPane keyFileSwingPane;
    private AudioClip play;
    private Scene scene;
    
    @Override
    public void start(Stage stage) throws Exception
    {
        ui = this;
        guifx = this;
        this.stage = stage;

        root = FXMLLoader.load(getClass().getResource("GUIFX.fxml"));
        scene = new Scene((Parent)root);
	
	
        
//        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); }catch(Exception e){ System.out.println("Exception: setLookAndFeel: " + e.getMessage()); }
        setUserAgentStylesheet(STYLESHEET_MODENA);
//        setUserAgentStylesheet(STYLESHEET_CASPIAN);


        this.stage.setScene(scene);
        this.stage.setTitle(Version.getProductName());
        this.stage.setMinWidth(1366);
        this.stage.setMinHeight(700);
        this.stage.setMaximized(true);
        
	this.stage.show();

//	this.stage.setOnCloseRequest(e -> Platform.exit());	
	stage.setOnCloseRequest(new EventHandler<WindowEvent>() { @Override public void handle(WindowEvent e)
	{
	    play = new AudioClip(SND_SHUTDOWN.getSource()); play.play();
//	    play = new AudioClip(SND_MESSAGE.getSource()); play.play();
	    while ( play.isPlaying() ) { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
	    
//	    Platform.runLater(() ->
//	    {
////		userGuidanceLabel.setText("Thank you"); userGuidanceLabel.setOpacity(0.7);
//	    });

//	    if (finalCrypt == null) { System.out.println("fc null"); } else { System.out.println("fc not null"); }
	}});
	
//	scene.getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (WindowEvent window) -> 
//	{
//	    Platform.runLater(() ->
//	    {
//		play = new AudioClip(this.SND_SHUTDOWN.getSource()); play.play();
//		userGuidanceLabel.setText("Thank you"); userGuidanceLabel.setOpacity(0.7);
////		textLabelFadeMessage("Thank you", 64, false, false, false, false);
//		while ( play.isPlaying() ) { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
////		if (finalCrypt == null) { System.out.println("fc null"); } else { System.out.println("fc not null"); }
//		System.exit(0);
//	    });
//	});

	version = new Version(ui);
	version.checkCurrentlyInstalledVersion(ui);
        this.stage.setTitle(Version.getProductName() + " " + version.getCurrentlyInstalledOverallVersionString());
	fadeInMessage = version.getCurrentlyInstalledOverallVersionString();

    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
//	TIMELINE INITIALIZATION
	
	PAUSE_TIMELINE.setCycleCount(Animation.INDEFINITE);
	UPDATE_CLOCKS_TIMELINE.setCycleCount(Animation.INDEFINITE);
	UPDATE_CLOCKS_TIMELINE.setDelay(Duration.seconds(1));

	FLASH_MAC_MODE_TIMELINE.setCycleCount(Animation.INDEFINITE);
	
	AUTO_DISABLE_ARMING_MAC_MODE_TIMELINE.setCycleCount(1);

	targetFileDeleteButton = new javax.swing.JButton();
        targetFileDeleteButton.setFont(new java.awt.Font("Arimo", 0, 11)); // NOI18N
        targetFileDeleteButton.setText("Delete"); // X🗑❌❎⛔ (no utf8)
        targetFileDeleteButton.setEnabled(false);
        targetFileDeleteButton.setToolTipText("Delete selected item(s)");
        targetFileDeleteButton.addActionListener((java.awt.event.ActionEvent evt) -> { targetFileDeleteButtonActionPerformed(evt); });

        keyFileDeleteButton = new javax.swing.JButton();
        keyFileDeleteButton.setFont(new java.awt.Font("Arimo", 0, 11)); // NOI18N
        keyFileDeleteButton.setText("Delete"); // X🗑❌❎⛔ (no utf8)
        keyFileDeleteButton.setEnabled(false);
        keyFileDeleteButton.setToolTipText("Delete selected item");
        keyFileDeleteButton.addActionListener((java.awt.event.ActionEvent evt) -> { keyFileDeleteButtonActionPerformed(evt); });
        
//      Create filefilters        
        finalCryptFilter = new FileNameExtensionFilter("FinalCrypt *.bit", "bit");
        nonFinalCryptFilter = new FileFilter() // Custom negate filefilter
        { 
            @Override public boolean accept(File file) { return !file.getName().toLowerCase().endsWith(".bit"); }
            @Override public String getDescription()   { return "NON FinalCrypt"; }
        };
        
//        targetFileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
//        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        targetFileChooser = new JFileChooser();
	
        targetFileChooser.setControlButtonsAreShown(false);
        targetFileChooser.setToolTipText("Right mousclick for Refresh");
        targetFileChooser.setMultiSelectionEnabled(true);
        targetFileChooser.setFocusable(true);
//        targetFileChooser.setFocusCycleRoot(true);
//        targetFileChooser.setFocusTraversalKeysEnabled(true);
//        targetFileChooser.setFocusTraversalPolicyProvider(true);
//        targetFileChooser.setFont(new Font("Liberation Sans", Font.PLAIN, 10));
        targetFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        targetFileChooser.addChoosableFileFilter(finalCryptFilter);
        targetFileChooser.addChoosableFileFilter(nonFinalCryptFilter);
        targetFileChooser.addPropertyChangeListener((java.beans.PropertyChangeEvent evt) -> { targetFileChooserPropertyChange(evt); });
        targetFileChooser.addActionListener( (java.awt.event.ActionEvent evt) -> { targetFileChooserActionPerformed(evt); });
        targetFileChooserComponentAlteration(targetFileChooser);
        targetFileSwingNode.setContent(targetFileChooser);

//        keyFileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        keyFileChooser = new JFileChooser();
	
        keyFileChooser.setControlButtonsAreShown(false);
        keyFileChooser.setToolTipText("Right mousclick for Refresh");
        keyFileChooser.setMultiSelectionEnabled(true);
        keyFileChooser.setFocusable(true);
//        keyFileChooser.setFocusCycleRoot(true);
//        keyFileChooser.setFocusTraversalKeysEnabled(true);
//        keyFileChooser.setFocusTraversalPolicyProvider(true);
        keyFileChooser.setFont(new Font("Liberation Sans", Font.PLAIN, 10));
        keyFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        keyFileChooser.addPropertyChangeListener((java.beans.PropertyChangeEvent evt) -> { keyFileChooserPropertyChange(evt); });
//	keyFileChooser.add
        keyFileChooser.addActionListener( (java.awt.event.ActionEvent evt) -> { keyFileChooserActionPerformed(evt); });
        
        keyFileChooserComponentAlteration(keyFileChooser);
	
        Timeline timeline = new Timeline(new KeyFrame( Duration.millis(100), ae -> { keyFileSwingNode.setContent(keyFileChooser); } )); timeline.play(); // Delay keyFileChooser to give 1st focus to targetFileChooser

        finalCrypt = new FinalCrypt(this); finalCrypt.start();
	
	
	pwdField.setContextMenu(new ContextMenu()); // Getting rid of the mouse paste function. Actionlistener does not pickup on pasted passwords through mouse
	checksumHeader.setText("Checksum (" + FinalCrypt.HASH_ALGORITHM_NAME + ")");
	keyImageView.setImage(new Image(getClass().getResourceAsStream("/rdj/images/key.png")));
	
	
	keyButton.setText(CREATE_KEY);

//	=========================================================================================================================================
//	============================================================= USER GUIDANCE =============================================================
//	textLabel RadialGradient Animation =========================================================

//	First relayout user guidance arrows

	bottomleftLabel.setOpacity(0);	bottomleftLabel.setVisible(true);
	topleftLabel.setOpacity(0);	topleftLabel.setVisible(true);
	toprightLabel.setOpacity(0);	toprightLabel.setVisible(true);
	bottomrightLabel.setOpacity(0);	bottomrightLabel.setVisible(true);

	String platform = System.getProperty("os.name").toLowerCase(); // CrossPlatform Layout allignment issues

	if	( platform.indexOf("linux") != -1 )
	{
	    bottomleftLabel.setTranslateX( -11); topleftLabel.setTranslateX( -11); toprightLabel.setTranslateX( 11); bottomrightLabel.setTranslateX( 11);
	    bottomleftLabel.setTranslateY(  5);  topleftLabel.setTranslateY( -17); toprightLabel.setTranslateY(-17); bottomrightLabel.setTranslateY(  5);
	}
	else if ( platform.indexOf("windows") != -1 )
	{
//		    com.sun.javafx.css.StyleManager loadStylesheetUnPrivileged
//		    INFO: Could not load @font-face font [file:/C:/Users/Ron%20de%20Jong/Documents/FinalCrypt/build/classes/rdj/fonts/LiberationMono-Regular.ttf]
//		    Trips over %20 run from homedir caused by ¨url¨ in GUIFX.css @font-face { font-family: 'Liberation Mono'; src: url('fonts/LiberationMono-Regular.ttf'); }
//		    However running native from Win install dir poses no problem as the path does not encounter spaces converted into %20 url symbols

//		    Valid when loading embedded font failed
//		    bottomleftLabel.setTranslateX(-21); topleftLabel.setTranslateX(-21); toprightLabel.setTranslateX( 21); bottomrightLabel.setTranslateX( 21);
//		    bottomleftLabel.setTranslateY( 11); topleftLabel.setTranslateY(-32); toprightLabel.setTranslateY(-32); bottomrightLabel.setTranslateY( 11);

//		    Valid when loading embedded font
	    bottomleftLabel.setTranslateX(-22); topleftLabel.setTranslateX(-22); toprightLabel.setTranslateX( 22); bottomrightLabel.setTranslateX( 22);
	    bottomleftLabel.setTranslateY( 23); topleftLabel.setTranslateY(-21); toprightLabel.setTranslateY(-21); bottomrightLabel.setTranslateY( 23);
	}
	else if ( platform.indexOf("mac") != -1 )
	{
	    bottomleftLabel.setTranslateX(-22); topleftLabel.setTranslateX(-22); toprightLabel.setTranslateX( 22); bottomrightLabel.setTranslateX( 22);
	    bottomleftLabel.setTranslateY( 24); topleftLabel.setTranslateY(-20); toprightLabel.setTranslateY(-20); bottomrightLabel.setTranslateY( 24);
	}
	else
	{
	    bottomleftLabel.setTranslateX(  0); topleftLabel.setTranslateX(  0); toprightLabel.setTranslateX(  0); bottomrightLabel.setTranslateX(  0);
	    bottomleftLabel.setTranslateY(  0); topleftLabel.setTranslateY(  0); toprightLabel.setTranslateY(  0); bottomrightLabel.setTranslateY(  0);
	}

//	===============================================================================================================================================
//	USERGUIDANCE GRADIENT ANIMATION
//	===============================================================================================================================================

		
	radius = 0.6;
	focusAngle = 0.0;
        focusDistance = 0.0;
	startX = -0.5;
	endX = 1.55;
//	cyclecenter = 50;
	variable = startX;
	stepX = 0.02;
	centerX = endX;
	centerY = 0.55;
	proportional = true;
	
	textLabelTimeline.setCycleCount(Animation.INDEFINITE);
	
	sysmon = sysMonCanvas.getGraphicsContext2D();
	sysmon.setGlobalAlpha(0.8);
        sysmon.setStroke(Color.RED);
        sysmon.setLineWidth(2);
	
	sysmonOffSetX = 25;
	
	sysmon.setFill(Color.valueOf("#4A4039"));
	sysmon.setTextAlign(TextAlignment.LEFT);
	sysmon.setTextBaseline(VPos.BOTTOM);
	sysmon.setFont(javafx.scene.text.Font.font("Liberation Mono", FontWeight.NORMAL, FontPosture.REGULAR, 14));
	sysmon.fillText("🖳", sysmonOffSetX + 0, 20);
	sysmon.fillText("🅼", sysmonOffSetX + 30, 20); // 🅼 🂓 🄼 Ⓜ ⓜ ⒨ 🄼 🆓 🅜
	sysmon.fillText("🖴", sysmonOffSetX + 60, 20);
	
	welcome();
	
    }
    
//    private void displaySystemMonitor(double userLoadPercParam, String userLoadString, double usedMemPercParam, String usedMemString, double megaBytesPerSecondParam, String throughputString)
    private void displaySystemMonitor(double userLoadPercParam, String userLoadString, double usedMemPercParam, String usedMemString, double throughputPercParam, String throughputString)
    {
//	double megaBytesPerSecond = ((bytesPerMilliSecond) / 1024d);
	Platform.runLater(() ->
	{
	    int width = 2; sysmon.setLineWidth(width);
	    
	    int userLoadPosX = sysmonOffSetX + 23;
	    int memUsePosX = userLoadPosX + 25;
	    int ioLoadPosX = memUsePosX + 25;
	    
//	    Precalculations

//	    I/O
//	    double throughputPerc = ((throughputPercParam) / (IO_THROUGHPUT_CEILING / 100)); if ( throughputPerc > 20) { throughputPerc = 20; }

	    String sysMonString = "";
	    sysMonString += userLoadString + "\r\n";
	    sysMonString += usedMemString + "\r\n";
	    sysMonString += throughputString;


//	    Drawing
	    sysmon.clearRect(userLoadPosX - 1, 0, width, 20);
	    for (int y = 0; y < (userLoadPercParam / 100) * 20; y += 4) { sysmon.setStroke(Color.color(y/20d, 1.0d-(y/20d), 0)); sysmon.strokeLine(userLoadPosX, 20 - y, userLoadPosX, 20 - y + 0); }
	    	    	    
	    sysmon.clearRect(memUsePosX - 1, 0, width, 20);
	    for (int y = 0; y < (usedMemPercParam / 100) * 20; y += 4) { sysmon.setStroke(Color.color(y/20d, 1.0d-(y/20d), 0)); sysmon.strokeLine(memUsePosX, 20 - y, memUsePosX, 20 - y + 0); }

	    sysmon.clearRect(ioLoadPosX - 1, 0, width, 20);
	    for (int y = 0; y < (throughputPercParam/ 100) * 20; y+=4) { sysmon.setStroke(Color.color(1.0-(y/20d), 1.0d-(1.0-(y/20d)), 0)); sysmon.strokeLine(ioLoadPosX, 20 - y, ioLoadPosX, 20 - y + 0); }

	    sysMonTooltip.setText(sysMonString);
	});
    }
    
    private void updateSystemMonitor()
    {
	double userLoadPerc = getUserLoadPerc(); String userLoadString = "CPU Workload (" + Stats.getDecimal(userLoadPerc,0) + "%)"; MemStats memStats = getMemStats();
	double throughputPerc = ((megaBytesPerSecond) / (finalCrypt.io_Throughput_Ceiling / 100)); String throughputString = "Storage I/O Throughput (" + Stats.getDecimal((throughputPerc * (finalCrypt.io_Throughput_Ceiling / 100)),1) + " MiB/S)";
	displaySystemMonitor(userLoadPerc, userLoadString, memStats.usedMemPerc, memStats.memStatsString, throughputPerc, throughputString);
    }
    
    private MemStats getMemStats()
    {
	MemStats memStats = new MemStats();
	memStats.totMem = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
	memStats.freeMem = (memStats.totMem - Runtime.getRuntime().totalMemory());
	memStats.usedMem = Runtime.getRuntime().totalMemory();
	memStats.usedMemPerc = Double.valueOf(memStats.usedMem / (memStats.totMem / 100d)).doubleValue();
	memStats.memStatsString = "";
	memStats.memStatsString += "RAM Mem Used (" + Stats.getDecimal(memStats.usedMemPerc,1) + "%) " + Stats.getDecimal(Long.valueOf(memStats.usedMem).doubleValue() / (1024d * 1024d),1) + " MiB / " + Stats.getDecimal(Long.valueOf(memStats.totMem).doubleValue() / (1024d * 1024d * 1024d),1) + " GiB"; 

	return memStats;
    }
    
    public void textLabelBlurMessage(String message, int durationMSec) // 1000
    {
	blurvar = 3.0;
	final int count = 20;
	final int blurval = 40;
	final double step = blurval/count;
	
        Timeline labelTimeline = new Timeline(new KeyFrame( Duration.millis(durationMSec/count), ae ->
	{
	    userGuidanceLabel.setEffect(new BoxBlur(blurvar, blurvar, 3));
	    blurvar += step;
	}));
	labelTimeline.setCycleCount(count);
	labelTimeline.setOnFinished((ActionEvent actionEvent) ->
	{
	    userGuidanceLabel.setText(message);
	    blurvar = blurval;
	    Timeline labelTimeline1 = new Timeline(new KeyFrame( Duration.millis(durationMSec/count), ae ->
	    {
		userGuidanceLabel.setEffect(new BoxBlur(blurvar, blurvar, 3));
		blurvar -= step;
	    }));
	    labelTimeline1.setCycleCount(count);
	    labelTimeline1.setOnFinished((ActionEvent actionEvent1) -> { userGuidanceLabel.setEffect(new Glow(1.0)); });
	    labelTimeline1.play();
	});
	labelTimeline.play();
    }
    
    public double getUserLoadPerc()
    {
        try { attribList = mBeanServer.getAttributes(attributeObjectName, new String[]{ procCPULoadAttribute });}
        catch (InstanceNotFoundException | ReflectionException ex) { log(ex.getMessage(), true, true, true, true ,false); }
        
        if (attribList.isEmpty()) { return Double.NaN; }
        att = (Attribute)attribList.get(0);
        value  = ((Double)att.getValue() * 100d);    
        return value;
    }

    private void welcome()
    {
	targetFCPathList = new FCPathList(); updateDashboard(targetFCPathList);
        configuration = new Configuration(ui);
        version = new Version(ui);
        version.checkCurrentlyInstalledVersion(this);
		
	statusLabel.setText("Welcome to " + Version.getProductName() + " " + version.getCurrentlyInstalledOverallVersionString());

//	Set the corner arrows

	bottomleftLabel.setOpacity(0);	bottomleftLabel.setVisible(bottomleftLabelEnabled);
	topleftLabel.setOpacity(0);	topleftLabel.setVisible(topleftLabelEnabled);
	toprightLabel.setOpacity(0);	toprightLabel.setVisible(toprightLabelEnabled);
	bottomrightLabel.setOpacity(0);	bottomrightLabel.setVisible(bottomrightLabelEnabled);

//	Prefer monospaced ◤ XY -18 -28 | ◥ XY 18 -28 | ◢ XY 18 8 | ◣ XY -18 8 

	String[] arrowsArray = new String[]
	{
	    ""			//  0
	    ,"❸❷❶❹"		//  1
	    ,"🅒🅑🅐🅓"		//  2
	    ,"⚉⚉⚉⚉"		//  3
	    ,"◤◥◢◣"		//  4
	    ,"◸◹◿◺"		//  5
	    ,"🡔🡕🡖🡗"		//  6
	    ,"🢄🢅🢆🢇"		//  7
	    ,"⬈⬉⬊⬋"		//  8 asci
	    ,"⇖⇗⇘⇙"		//  9 asci
	    ,"↖↗↘↙"		// 10
	    ,"🡤🡥🡦🡧"		// 11
	    ,"🡬🡭🡮🡯"		// 12
	    ,"🡴🡵🡶🡷"		// 13
	    ,"🡼🡽🡾🡿"		// 14 nice
	};
	String arrows = arrowsArray[4]; // Select the symbol

	if (arrows.length()==4)
	{
	    topleftLabel.setText(arrows.substring(0,1));
	    toprightLabel.setText(arrows.substring(1,2));
	    bottomrightLabel.setText(arrows.substring(2,3));
	    bottomleftLabel.setText(arrows.substring(3,4));
	}
	else if (arrows.length()==8)
	{
	    topleftLabel.setText(arrows.substring(0,2));
	    toprightLabel.setText(arrows.substring(2, 4));
	    bottomrightLabel.setText(arrows.substring(4, 6));
	    bottomleftLabel.setText(arrows.substring(6, 8));
	}
	
        copyrightLabel.setText("Copyright: " + Version.getCopyright() + " " + Version.getAuthor());
	log(getRuntimeEnvironment(), false, true, true, false ,false);

//      cpuIndicator
        Rectangle rect = new Rectangle(0, 0, 100, 100); Tooltip cpuIndicatorToolTip = new Tooltip("Process CPU Load"); Tooltip.install(rect, cpuIndicatorToolTip);
//        cpuIndicator.setTooltip(cpuIndicatorToolTip);
//	cpuIndicator.setStyle(" -fx-progress-color: grey;");

//      for: ProcessCpuLoad()
//        procCPULoadAttribute = "ProcessCpuLoad";
        mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {attributeObjectName = ObjectName.getInstance("java.lang:type=OperatingSystem"); }
        catch (MalformedObjectNameException | NullPointerException ex) { log(ex.getMessage(), true, true, true, true ,false); }
        
	
//	========================================================================
//	MAIN TIMELINE
//	========================================================================

	
        
	mainTimeline = new Timeline(new KeyFrame( Duration.millis(MAIN_TIMELINE_INTERVAL_PERIOD), (ActionEvent ae) ->
	{
//	    ====================================================================
//	    WORKLOAD
//	    ====================================================================
	    
//	    SYSTEM MONITOR
//	    ==================================================================================================================================================================
	    if ((update_System_Monitor_Enabled) && ( load_Monitor_MS_Passed >= LOAD_MONITOR_MS_INTERVAL )) { updateSystemMonitor(); load_Monitor_MS_Passed = 0.0d; }
	    load_Monitor_MS_Passed += (mainTimeline.getCurrentRate() * MAIN_TIMELINE_INTERVAL_PERIOD);
//	    ==================================================================================================================================================================	    

//	    WORKLOAD MANAGER
//	    ==================================================================================================================================================================	    
	    if ( load_Manager_MS_Passed >= LOAD_MANAGER_MS_INTERVAL )
	    {
		if (userLoadPerc >= LOADHIGH_THRESHOLD) // High load detected
		{
		    load_High_MS_Passed += load_Manager_MS_Passed; load_Low_MS_Passed = 0.0d; // High load period register
		    if ( (textLabelTimeline.getStatus() == Animation.Status.RUNNING) & (load_High_MS_Passed >= LOAD_HIGH_MS_TIMEOUT) ) // High Load period exceeded
		    {
			textLabelTimeline.pause();
			
			arrowsfadestep = -(ARROWS_OPACITY_MAX / (Double.valueOf(1000.0 / MAIN_TIMELINE_INTERVAL_PERIOD).intValue()) * 2); // step = max / FPS;
			arrowsfadevar = ARROWS_OPACITY_MAX;

			bottomleftLabel.setVisible(bottomleftLabelEnabled);	if (bottomleftLabelEnabled)	{ bottomleftLabel.setOpacity(ARROWS_OPACITY_MAX); }
			topleftLabel.setVisible(topleftLabelEnabled);		if (topleftLabelEnabled)	{ topleftLabel.setOpacity(ARROWS_OPACITY_MAX); }
			toprightLabel.setVisible(toprightLabelEnabled);		if (toprightLabelEnabled)	{ toprightLabel.setOpacity(ARROWS_OPACITY_MAX); }
			bottomrightLabel.setVisible(bottomrightLabelEnabled);	if (bottomrightLabelEnabled)	{ bottomrightLabel.setOpacity(ARROWS_OPACITY_MAX); }			
		    } 
		}
		else
		{
		    load_Low_MS_Passed += load_Manager_MS_Passed; load_High_MS_Passed = 0.0d;
		    if ( (textLabelTimeline.getStatus() == Animation.Status.PAUSED) & (load_Low_MS_Passed >= LOAD_LOW_MS_TIMEOUT) )  { textLabelTimeline.play(); } 
		}
		load_Manager_MS_Passed = 0d;
	    }
	    load_Manager_MS_Passed += (mainTimeline.getCurrentRate() * MAIN_TIMELINE_INTERVAL_PERIOD);
//	    ==================================================================================================================================================================	    
	    
//	    ARROW FADE ANIMATION
//	    ====================================================================
	
	    if (textLabelTimeline.getStatus() == Animation.Status.RUNNING)
	    {
		if (bottomleftLabel.isVisible())    { bottomleftLabel.setOpacity(arrowsfadevar); }
		if (topleftLabel.isVisible())	    { topleftLabel.setOpacity(arrowsfadevar); }
		if (toprightLabel.isVisible())	    { toprightLabel.setOpacity(arrowsfadevar); }
		if (bottomrightLabel.isVisible())   { bottomrightLabel.setOpacity(arrowsfadevar); }

		arrowsfadevar += arrowsfadestep;

		if ( arrowsfadevar >= ARROWS_OPACITY_MAX ) { arrowsfadevar = ARROWS_OPACITY_MAX; arrowsfadestep = -arrowsfadestep; }
		if ( arrowsfadevar <= 0.0 ) // Only set (in)visible when opacity is 0
		{
		    arrowsfadevar = 0.0; arrowsfadestep = -arrowsfadestep;
    //		arrowsfadevar = arrowsfadevarmax;
		    bottomleftLabel.setVisible(bottomleftLabelEnabled);
		    topleftLabel.setVisible(topleftLabelEnabled);
		    toprightLabel.setVisible(toprightLabelEnabled);
		    bottomrightLabel.setVisible(bottomrightLabelEnabled);
		}
	    }
	}
        )); mainTimeline.setCycleCount(Animation.INDEFINITE); mainTimeline.play();
		
	checksumTooltip = new Tooltip("");
//	checksumTooltip.setFont(javafx.scene.text.Font.font(javafx.scene.text.Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 13));
	checksumTooltip.setFont(javafx.scene.text.Font.font("Liberation Mono", FontWeight.NORMAL, FontPosture.REGULAR, 13));

        Platform.runLater(() ->
	{
	    String title =  "Welcome to " + Version.getProductName();
	    String header = "Brief Introduction:";
	    String infotext = "";
	    infotext += "Step   Optional create an OTP key file.\r\n";
	    infotext += "Step 1 Select items to en/decrypt on the left.\r\n";
	    infotext += "Step 2 Select your (OTP) key file on the right.\r\n";
	    infotext += "Step 3 Click [Encrypt] / [Decrypt] button below.\r\n";
	    infotext += "\r\n";
	    infotext += "Optional:\r\n";
	    infotext += "\r\n";
	    infotext += "Double click to open files.\r\n";
	    infotext += "Click [LOG] to see details.\r\n";
	    infotext += "Tip:  Watch statusbar at bottom.\r\n";
	    infotext += "Tip:  Make backups of your keys and data.\r\n";
	    infotext += "Tip:  Keep your keys secret (backup external).\r\n";
	    infotext += "\r\n";
	    infotext += "Live to love - Enjoy your privacy.\r\n\r\n";
	    
	    fontsizefactor = 1.3;
	    
	    Version ver = new Version(ui); ver.checkCurrentlyInstalledVersion(ui);
	    fadeInMessage = Version.getProductName();
	    userGuidanceLabel.setStyle("-fx-font-size: " + (userGuidanceLabel.getWidth() / fadeInMessage.length() * fontsizefactor) + "px;");
	    userGuidanceLabel.setText(fadeInMessage);
	    textLabelTimeline.play();

//	textLabel Introduction Animation ==========================================================
	    play = new AudioClip(this.SND_STARTUP.getSource()); play.play();


	    FadeTransition fadeTransition = new FadeTransition(Duration.millis(3000), userGuidanceLabel);
	    fadeTransition.setFromValue(0.05f);
	    fadeTransition.setToValue(0.7f);
	    fadeTransition.setCycleCount(1);
	    fadeTransition.setAutoReverse(true);
	    fadeTransition.setDelay(Duration.seconds(1));
	    fadeTransition.setInterpolator(Interpolator.EASE_OUT);

	    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(3000), userGuidanceLabel);
	    scaleTransition.setFromX(0.98f);scaleTransition.setToX(1.0f);
	    scaleTransition.setFromY(0.98f);scaleTransition.setToY(1.0f);
	    scaleTransition.setFromZ(0.98f);scaleTransition.setToZ(1.0f);
	    scaleTransition.setCycleCount(1);
	    scaleTransition.setAutoReverse(false);
	    scaleTransition.setDelay(Duration.seconds(1));
	    scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

	    ParallelTransition parallelTransition = new ParallelTransition();
	    parallelTransition.getChildren().addAll( fadeTransition, scaleTransition );
	    parallelTransition.setCycleCount(1);

	    parallelTransition.setOnFinished((ActionEvent actionEvent) ->
	    {
		//		    welcome();
		/*
		Linux:
		${user.home}/.java/.userPrefs/FinalCrypt/prefs.xml

		Windows:
		regedit remove HKEY_CURRENT_USER\Software\JavaSoft\Prefs\/Final/Crypt

		Mac OSX:
		Mac OS X ~/Library/Preferences in multiple plist files.
		Mac OS X uses the java.util.prefs.MacOSXPreferencesFactory class. See lists.apple.com/archives/java-dev/2010/Jul/msg00056.html
		the java.util.prefs.MacOSXPreferencesFactory class should be in rt.jar in JDK 1.7 or later.
		See hg.openjdk.java.net/macosx-port/macosx-port/jdk/file/… for the source code.
		JDK 8 all the items in java.util.prefs:
		*/
		
		prefs = Preferences.userRoot().node(Version.getProductName());
		String val = prefs.get("Initialized", "Unknown"); // if no val then "Unknown" prefs location registry: HKEY_CURRENT_USER\Software\JavaSoft\Prefs
		if (! val.equals("Yes")) // First time
		{		    
		    userGuideMessage(CREATE_KEY, 64, false, false, false, true, VOI_CREATE_KEY, 0);
		    prefs.put("Initialized", "Yes");
		}
		else
		{
		    userGuideMessage(SELECT_KEY, 64, false, false, true, false, VOI_SELECT_KEY, 0);
		}

		disableFileChoosers(false);
		
		FadeTransition sysmonFadeTransition = new FadeTransition(Duration.millis(2000), sysMonCanvas);
		sysmonFadeTransition.setFromValue(0.0f);
		sysmonFadeTransition.setToValue(1.0f);
		sysmonFadeTransition.setCycleCount(1);
		sysmonFadeTransition.setAutoReverse(false);
		sysmonFadeTransition.setDelay(Duration.seconds(0));
		sysmonFadeTransition.setInterpolator(Interpolator.EASE_OUT);
		sysmonFadeTransition.setOnFinished((ActionEvent enableKeyButtonEvent) ->
		{
		    play = new AudioClip(this.SND_MESSAGE.getSource()); play.play();
		    
		    keyButton.setDisable(false);
		    checkUpdateButton.setDisable(false);
		    userloadPercTest = 100.0d; userMemPercTest = 100.0d; throughputPercTest = 100d; // IO_THROUGHPUT_CEILING;
		    Timeline systemMonitorTestTimeline = new Timeline(new KeyFrame( Duration.millis(100), ae ->
		    {
			displaySystemMonitor(userloadPercTest, "",userMemPercTest, "",throughputPercTest, "");
			userloadPercTest -= 10.0d; userMemPercTest -= 10.0d; throughputPercTest -= (10.0d);
		    }));
		    systemMonitorTestTimeline.setCycleCount(10);
		    systemMonitorTestTimeline.setOnFinished((ActionEvent actionEvent1) ->
		    {
			update_System_Monitor_Enabled = true;
		    });
		    systemMonitorTestTimeline.play();
		});
		sysmonFadeTransition.play();

//		Last Update Checked
		long updateChecked = 0; // Epoch date
//		long updateCheckPeriod = 1000L*20L; // Just to test auto update function
		long updateCheckPeriod = 1000L*60L*60L*24L; // Update period 1 Day
		now = Calendar.getInstance().getTimeInMillis(); // Epoch date
		val = prefs.get("Update Checked", "Unknown"); // if no val then "Unknown" prefs location registry: HKEY_CURRENT_USER\Software\JavaSoft\Prefs
		boolean invalidUpdateCheckedValue = false;
		try { updateChecked = Long.valueOf(val); } catch (NumberFormatException e) { invalidUpdateCheckedValue = true; }
		if ( invalidUpdateCheckedValue ) { Platform.runLater(() -> { checkUpdate(false); }); } else { if (now - updateChecked >= updateCheckPeriod) { Platform.runLater(() -> { checkUpdate(false); }); } }
	    });
	    parallelTransition.play();	    
	});
	
        Alert alert = new Alert(AlertType.INFORMATION);

//      Style the Alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");

//        alert.setTitle("Info");
//        alert.setHeaderText("Welcome");
//        alert.setResizable(true);
//        String infotext = new String();
//        infotext  = "Welcome.\r\n";
//        alert.setContentText(infotext);
//        alert.setOnShowing(new EventHandler<DialogEvent>()
//        {
//            @Override
//            public void handle(DialogEvent event)
//            {
////              JavaFX Delay Timer to prevent not on application thread error
//                Timeline timeline = new Timeline(new KeyFrame( Duration.millis(1000), ae -> 
//                        alert.close()
//                )); timeline.play();
//            }
//        });
//        alert.showAndWait();        
    }
    
    private void disableFileChoosers(boolean param)
    {
	if (param)
	{
	    keyFileSwingNode.setMouseTransparent(param); targetFileSwingNode.setMouseTransparent(param);
	    
	    Timeline disableTimeline = new Timeline
	    (
		    new KeyFrame(Duration.ZERO,
		    new KeyValue(keyFileFoil.opacityProperty(), 0.1),
		    new KeyValue(targetFileFoil.opacityProperty(), 0.1)),
		    new KeyFrame(Duration.seconds(1),
		    new KeyValue(keyFileFoil.opacityProperty(), 0.5),
		    new KeyValue(targetFileFoil.opacityProperty(), 0.5))
	    );
	    disableTimeline.setAutoReverse(false);
	    disableTimeline.setOnFinished((ActionEvent actionEvent) -> { });
	    disableTimeline.play();
	}
	else
	{
	    Timeline enableTimeline = new Timeline
	    (
		    new KeyFrame(Duration.ZERO,
		    new KeyValue(keyFileFoil.opacityProperty(), 0.5),
		    new KeyValue(targetFileFoil.opacityProperty(), 0.5)),
		    new KeyFrame(Duration.seconds(1),
		    new KeyValue(keyFileFoil.opacityProperty(), 0.1),
		    new KeyValue(targetFileFoil.opacityProperty(), 0.1))
	    );
	    enableTimeline.setAutoReverse(false);
	    enableTimeline.setOnFinished((ActionEvent actionEvent) -> { keyFileSwingNode.setMouseTransparent(param); targetFileSwingNode.setMouseTransparent(param); });
	    enableTimeline.play();
	}
    }
    
    synchronized public void userGuideMessage(String message, int fontsize, boolean bottomleft, boolean topleft, boolean topright, boolean bottomright, Media media, int media_Delay)
    {
	bottomleftLabelEnabled = bottomleft; topleftLabelEnabled = topleft; toprightLabelEnabled = topright; bottomrightLabelEnabled = bottomright;
	
	if (textLabelTimeline.getStatus() == Animation.Status.PAUSED)
	{
	    if (bottomleftLabel.getOpacity() == 0.0)	{ bottomleftLabel.setVisible(bottomleftLabelEnabled); }
	    if (topleftLabel.getOpacity() == 0.0)	{ topleftLabel.setVisible(topleftLabelEnabled); }
	    if (toprightLabel.getOpacity() == 0.0)	{ toprightLabel.setVisible(toprightLabelEnabled); }
	    if (bottomrightLabel.getOpacity() == 0.0)	{ bottomrightLabel.setVisible(bottomrightLabelEnabled); }
	}
//	FADE OUT
	
	final int count = 10;
	final int cycleduration = 50;
	final double fadevarmax = 0.7;

	fadevar = fadevarmax;
	final double step = fadevarmax/count;
        Timeline labelTimeline = new Timeline(new KeyFrame( Duration.millis(cycleduration), ae ->
	{
//	    Do Something
	    userGuidanceLabel.setOpacity(fadevar);
//
	    if (textLabelTimeline.getStatus() != Animation.Status.RUNNING)
	    {
		if (bottomleftLabel.getOpacity() > 0.0)	    { bottomleftLabel.setOpacity(fadevar); }
		if (topleftLabel.getOpacity() > 0.0)	    { topleftLabel.setOpacity(fadevar); }
		if (toprightLabel.getOpacity() > 0.0)	    { toprightLabel.setOpacity(fadevar); }
		if (bottomrightLabel.getOpacity() > 0.0)    { bottomrightLabel.setOpacity(fadevar); }
	    }

	    fadevar -= step;
	}));
	labelTimeline.setCycleCount(10);
	labelTimeline.setOnFinished((ActionEvent actionEvent) ->
	{
	    //		FADE IN
	    
	    if ((play != null) &&(play.isPlaying())) { play.stop(); }
	    if (media != null) { play = new AudioClip(media.getSource()); play.play(); }
	    
	    if (textLabelTimeline.getStatus() != Animation.Status.RUNNING)
	    {
		bottomleftLabel.setOpacity(0);  bottomleftLabel.setVisible(bottomleftLabelEnabled);
		topleftLabel.setOpacity(0);	    topleftLabel.setVisible(topleftLabelEnabled);		
		toprightLabel.setOpacity(0);    toprightLabel.setVisible(toprightLabelEnabled);
		bottomrightLabel.setOpacity(0); bottomrightLabel.setVisible(bottomrightLabelEnabled);
	    }
	    userGuidanceLabel.setOpacity(0);
	    userGuidanceLabel.setStyle("-fx-font-size: " + Math.round(userGuidanceLabel.getWidth() / message.length() * fontsizefactor) + "px;");
	    userGuidanceLabel.setText(message);
	    fadevar = 0.0;
	    Timeline labelTimeline1 = new Timeline(new KeyFrame( Duration.millis(cycleduration), ae -> // Begin fade in
	    {
//		    Do Something
		userGuidanceLabel.setOpacity(fadevar);
		if (textLabelTimeline.getStatus() != Animation.Status.RUNNING)
		{
		    if (bottomleftLabelEnabled)	{ bottomleftLabel.setOpacity(fadevar); }
		    if (topleftLabelEnabled)	{ topleftLabel.setOpacity(fadevar); }
		    if (toprightLabelEnabled)	{ toprightLabel.setOpacity(fadevar); }
		    if (bottomrightLabelEnabled)	{ bottomrightLabel.setOpacity(fadevar); }
		}
		if ( fadevar < fadevarmax ) { fadevar += step;}

	    }));
	    labelTimeline1.setCycleCount(count);
	    labelTimeline1.setOnFinished((ActionEvent actionEvent1) ->
	    {
		if (textLabelTimeline.getStatus() != Animation.Status.RUNNING)
		{
		    if (!bottomleftLabelEnabled)    { bottomleftLabel.setOpacity(0.0); }
		    if (!topleftLabelEnabled)	    { topleftLabel.setOpacity(0.0); }
		    if (!toprightLabelEnabled)	    { toprightLabel.setOpacity(0.0); }
		    if (!bottomrightLabelEnabled)   { bottomrightLabel.setOpacity(0.0); }
		}
		userGuidanceLabel.setOpacity(fadevarmax);
	    });
	    labelTimeline1.play();
	} // Do at fade out ready
	);
	labelTimeline.play();
    }
    
    private String getRuntimeEnvironment()
    {
	String env = "";
	
	String symbols = "";
	symbols += "Symbols:            ";
	symbols += FinalCrypt.UTF8_ENCRYPT_DESC + ": " + FinalCrypt.UTF8_ENCRYPT_SYMBOL + " ";
	symbols += FinalCrypt.UTF8_DECRYPT_DESC + ": " + FinalCrypt.UTF8_DECRYPT_SYMBOL + " ";
	symbols += FinalCrypt.UTF8_XOR_NOMAC_DESC + ": " + FinalCrypt.UTF8_XOR_NOMAC_SYMBOL + " ";
	symbols += FinalCrypt.UTF8_CLONE_DESC + ": " + FinalCrypt.UTF8_CLONE_SYMBOL + " ";
	symbols += FinalCrypt.UTF8_DELETE_DESC + ": " + FinalCrypt.UTF8_DELETE_SYMBOL + " ";
	symbols += FinalCrypt.UTF8_PAUSE_DESC + ": " + FinalCrypt.UTF8_PAUSE_SYMBOL + " ";
	symbols += FinalCrypt.UTF8_STOP_DESC + ": " + FinalCrypt.UTF8_STOP_SYMBOL + " ";
	symbols += FinalCrypt.UTF8_FINISHED_DESC + ": " + FinalCrypt.UTF8_FINISHED_SYMBOL + " ";
	
//    public final String CLASS_VERSION = System.getProperty("java.class.version");

	env +=    "Welcome to:         " + Version.getProductName() + " " + version.getCurrentlyInstalledOverallVersionString() + "\r\n";
	env += "\r\n";
	env +=    "Interface:          rdj/GUIFX\r\n";
	env +=    "Email:              " + Version.getAuthorEmail() + "\r\n";
	env +=    "Copyright:          " + Version.getCopyright() + " " + Version.getAuthor() + "\r\n";
	env +=    "Logfiles:           " + configuration.getLogDirPath().toString() + "\r\n";
	env +=    "Command line:       java -cp FinalCrypt.jar rdj/CLUI --help\r\n";
	env +=    "License:            " + Version.getLicense() + "\r\n";
	env += "\r\n";
	env +=    "OS Name:            " + OS_NAME + "\r\n";
	env +=    "OS Architecture:    " + OS_ARCH + "\r\n";
	env +=    "OS Version:         " + OS_VERSION + "\r\n";
	env +=    "OS Time:            " + configuration.getTime() + "\r\n";
	env += "\r\n";
	env +=    "Java Vendor:        " + JAVA_VENDER + "\r\n";
	env +=    "Java Version:       " + JAVA_VERSION + "\r\n";
	env +=    "Class Version:      " + CLASS_VERSION + "\r\n";
	env += "\r\n";
	env +=    "User Name:          " + System.getProperty("user.name") + "\r\n";
	env +=    "User Home:          " + System.getProperty("user.home") + "\r\n";
	env +=    "User Dir:           " + System.getProperty("user.dir") + "\r\n";
	env += "\r\n";
	env += symbols + "\r\n";
	env += "\r\n";
		
	return env;
    }
    
    public Alert introAlert(AlertType type, String title, String headerText, String message, String optOutMessage, Consumer<Boolean> optOutAction, ButtonType... buttonTypes)
    {
	play = new AudioClip(this.SND_MESSAGE.getSource()); play.play();
        Alert alert = new Alert(type);
        alert.getDialogPane().applyCss();
        Node graphic = alert.getDialogPane().getGraphic();
        DialogPane dialogPane = new DialogPane()
        {
          @Override
          protected Node createDetailsButton()
          {
            CheckBox checkbox = new CheckBox();
            checkbox.setText(optOutMessage);
            checkbox.setOnAction(e -> optOutAction.accept(checkbox.isSelected()));
            return checkbox;
          }
        };
        dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");

        alert.setDialogPane(dialogPane);
        alert.getDialogPane().getButtonTypes().addAll(buttonTypes);
        alert.getDialogPane().setContentText(message);
        alert.getDialogPane().setExpandableContent(new Group());
        alert.getDialogPane().setExpanded(true);
        alert.getDialogPane().setGraphic(graphic);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        return alert;
    }

    private void checkUpdate(boolean userActivated)
    {
        Platform.runLater(() ->
	{
	    String alertString = "";
	    version = new Version(ui);
	    version.checkCurrentlyInstalledVersion(GUIFX.this);
	    version.checkLatestOnlineVersion(GUIFX.this);
	    prefs.putLong("Update Checked", now);
	    String[] lines = version.getUpdateStatus().split("\r\n");
	    for (String line: lines) { log(line + "\r\n", true, true, true, false, false);}
	    	    
	    if (( ! version.getLatestAlertSubjectString().isEmpty()) && ( ! version.getLatestAlertMessageString().isEmpty() )) // Only display Alert in VERSION2 file
	    {
		play = new AudioClip(this.SND_MESSAGE.getSource()); play.play();
		Alert alert = new Alert(AlertType.INFORMATION);
		
		//      Style the Alert
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
		dialogPane.getStyleClass().add("myDialog");
		
		alert.setTitle("Information Dialog");
		alert.setHeaderText(version.getLatestAlertSubjectString() + "\r\n");
		alert.setResizable(true);
		alert.setContentText(version.getLatestAlertMessageString());
		alert.showAndWait();
		if (alert.getResult() == ButtonType.OK) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); }
	    }
	    
	    if (version.latestVersionIsKnown())
	    {
		if (( ! version.versionIsDifferent() ) &&( userActivated ))
		{
		    play = new AudioClip(this.SND_MESSAGE.getSource()); play.play();		    
		    Alert alert = new Alert(AlertType.INFORMATION);

		    DialogPane dialogPane = alert.getDialogPane();
		    dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
		    dialogPane.getStyleClass().add("myDialog");

		    alert.setTitle("Information Dialog");
		    alert.setHeaderText("Your current version is up to date\r\n");
		    alert.setResizable(true);
		    alert.setContentText("You have the latest version: (" + Version.getProductName() + " v" +version.getCurrentlyInstalledOverallVersionString() + ")\r\n");
		    alert.showAndWait();
		    if (alert.getResult() == ButtonType.OK) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); }
		}
		else
		{
		    if (version.versionCanBeUpdated())
		    {
			play = new AudioClip(this.SND_ALERT.getSource()); play.play();
										      alertString = Version.getProductName() + " v" + version.getCurrentlyInstalledOverallVersionString() + " can be updated\r\n\r\n";
										      alertString += "New:\r\n";
			if (! version.getLatestReleaseNotesString().isEmpty())	    { alertString += "   " + version.getLatestReleaseNotesString() + "\r\n"; }
			if (! version.getLatestVersionMessageString().isEmpty())    { alertString += "   " + version.getLatestVersionMessageString() + "\r\n\r\n"; }
										      alertString += "Would you like to download (" + Version.getProductName() + " v" + version.getLatestOnlineOverallVersionString() + ") ?\r\n";

			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, alertString, ButtonType.YES, ButtonType.NO);

			DialogPane dialogPane = alert.getDialogPane();
			dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
			dialogPane.getStyleClass().add("myDialog");

			alert.setHeaderText("New version " + Version.getProductName() + " available\r\n");
			alert.showAndWait();

			if (alert.getResult() == ButtonType.YES) { play = new AudioClip(this.SND_OPEN.getSource()); play.play(); Version.openWebSite(this); } else { play = new AudioClip(this.SND_BUTTON.getSource()); play.play();  }
		    }
		    else if ( (version.versionIsDevelopment() ) && ( userActivated ) )
		    {
			play = new AudioClip(this.SND_ALERT.getSource()); play.play();
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, alertString, ButtonType.YES, ButtonType.NO);

			DialogPane dialogPane = alert.getDialogPane();
			dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
			dialogPane.getStyleClass().add("myDialog");

			alert.setTitle("Information Dialog");
			alert.setHeaderText("You are using a development version\r\n");
			alert.setResizable(true);
			alert.setContentText("This is a development version:    (" + Version.getProductName() + " v" +version.getCurrentlyInstalledOverallVersionString() + ")\r\n"
					    +"The latest online stable release: (" + Version.getProductName() + " v" +version.getLatestOnlineOverallVersionString() + ")\r\n\r\n"
					    +"Would you like to download (" + Version.getProductName() + " v" + version.getLatestOnlineOverallVersionString() + ") now ?\r\n");
			alert.showAndWait();

			if (alert.getResult() == ButtonType.YES) { play = new AudioClip(this.SND_OPEN.getSource()); play.play(); Version.openWebSite(this); } else { play = new AudioClip(this.SND_BUTTON.getSource()); play.play();  }
		    }
		}
	    }
	    else // latest online version is unknown
	    {
		play = new AudioClip(this.SND_MESSAGE.getSource()); play.play();
		Alert alert = new Alert(AlertType.INFORMATION);

		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
		dialogPane.getStyleClass().add("myDialog");

		alert.setTitle("Information Dialog");
		alert.setHeaderText("Online version could not be checked\r\n");
		alert.setResizable(true);
		alert.setContentText(version.getUpdateStatus() + "\r\nNetwork connection issues perhaps ?\r\nPlease check your log for more info\r\n");
		alert.showAndWait();
		if (alert.getResult() == ButtonType.OK) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); }
	    }
	});
    }

//  Custom FileChooserDelete Listener methods
    private void targetFileDeleteButtonActionPerformed(java.awt.event.ActionEvent evt)                                                
    {
        Platform.runLater(() ->
	{
	    play = new AudioClip(this.SND_ALERT.getSource()); play.play();
	    String itemword = "";
	    if ( targetFileChooser.getSelectedFiles().length == 1 )      { itemword = "item"; }
	    else if ( targetFileChooser.getSelectedFiles().length > 1 )  { itemword = "items"; }
	    String selection = "Delete " + targetFileChooser.getSelectedFiles().length + " selected " + itemword + "?";
	    Alert alert = new Alert(AlertType.CONFIRMATION, selection, ButtonType.YES, ButtonType.NO);
	    alert.setHeaderText("Confirm Deletion?");
	    alert.showAndWait();
	    if (alert.getResult() == ButtonType.YES)
	    {
		if ((targetFileChooser != null)  && (targetFileChooser.getSelectedFiles() != null))
		{
		    if (targetFileChooser.getSelectedFiles().length > 0)
		    {
			play = new AudioClip(this.SND_SELECT.getSource()); play.play();
			ArrayList<Path> pathList = finalCrypt.getPathList(targetFileChooser.getSelectedFiles());
			boolean delete = true;
			boolean returnpathlist = false;
			String pattern1 = "glob:*";
			finalCrypt.deleteSelection(pathList, delete, returnpathlist, pattern1, false);
			updateFileChoosers(true, true);
		    }
		}
	    } else { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); }
	});
    }                                               

    private void keyFileDeleteButtonActionPerformed(java.awt.event.ActionEvent evt)                                                
    {                                                            
        Platform.runLater(() ->
	{
	    play = new AudioClip(this.SND_ALERT.getSource()); play.play();
//	    play.play(play.BUTTON_SND);
	    String itemword = "";
	    if ( keyFileChooser.getSelectedFiles().length == 1 )      { itemword = "item"; }
	    else if ( keyFileChooser.getSelectedFiles().length > 1 )  { itemword = "items"; }
	    String selection = "Delete " + keyFileChooser.getSelectedFiles().length + " selected " + itemword + "?";
	    Alert alert = new Alert(AlertType.CONFIRMATION, selection, ButtonType.YES, ButtonType.NO);
	    alert.setHeaderText("Confirm Deletion?");
	    alert.showAndWait();
	    if (alert.getResult() == ButtonType.YES)
	    {
		if ((keyFileChooser != null)  && (keyFileChooser.getSelectedFiles() != null))
		{
		    if (keyFileChooser.getSelectedFiles().length > 0)
		    {
			play = new AudioClip(this.SND_SELECT.getSource()); play.play();
			ArrayList<Path> pathList = finalCrypt.getPathList(keyFileChooser.getSelectedFiles());
			boolean delete = true;
			boolean returnpathlist = false;
			String pattern1 = "glob:*";
			finalCrypt.deleteSelection(pathList, delete, returnpathlist, pattern1, false);
			updateFileChoosers(true, true);
		    }
		}
	    } else { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); }
	});
    }                                               

    private void cursorWait()
    {
            targetFileChooser.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
            keyFileChooser.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));        
    }
    
    private void cursorDefault()
    {
            targetFileChooser.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
            keyFileChooser.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));        
    }
    
    private String getSelectedPatternFromFileChooser( javax.swing.filechooser.FileFilter fileFilter)
    {
        negatePattern = false; String patternLocal = "glob:*";
        String desc = "*";
        if ( fileFilter != null ) {desc = fileFilter.getDescription();}
        javax.swing.filechooser.FileNameExtensionFilter ef = null;
        try { ef = (javax.swing.filechooser.FileNameExtensionFilter) targetFileChooser.getFileFilter(); } catch (ClassCastException exc) {        }
        if ( ef != null ) 
        {
//            extension = ef.getExtensions()[0]; 
            desc = ef.getDescription();
        }
//        else { extension = "*"; }
        if      ( desc.startsWith("FinalCrypt") )       { negatePattern = false; patternLocal = "glob:*.bit"; }
        else if ( desc.startsWith("NON FinalCrypt") )   { negatePattern = true;  patternLocal = "glob:*.bit"; }
//        else if ( desc.startsWith("NON FinalCrypt") )   { negatePattern = true; pattern = "glob:*.[!b][!i][!t]"; }
        else                                            { negatePattern = false; patternLocal = "glob:*"; }
        return patternLocal;
    }
    
//  Doubleclicked item
    synchronized private void keyFileChooserActionPerformed(java.awt.event.ActionEvent evt)                                                  
    {                                                      
//	log("ACT: " + evt + " " + Calendar.getInstance().getTimeInMillis() + "\r\n", true, true, false, false, false);
        this.fileProgressBar.setProgress(0);
        this.filesProgressBar.setProgress(0);
        if ((keyFileChooser != null)  && (keyFileChooser.getSelectedFile() != null))
        {
	    if (( keyFCPath.type == FCPath.DEVICE ) || ( keyFCPath.type == FCPath.DEVICE_PROTECTED ))
	    {
		tab.getSelectionModel().select(1);
		DeviceManager deviceManager = new DeviceManager(this); deviceManager.start(); deviceManager.printGPT(keyFCPath);
		targetFCPathList = new FCPathList(); this.updateDashboard(targetFCPathList);
		Platform.runLater(() -> { encryptButton.setDisable(true); decryptButton.setDisable(true); keyButton.setDisable(true); keyButton.setText(CREATE_KEY); });
	    }
//					  ui	cll path				       isKey    device  minsize  symlink  writable status
	    else if (Validate.isValidFile(this, "", keyFileChooser.getSelectedFile().toPath(), true,     false,      1L, true,    false,  true))
	    {
		play = new AudioClip(this.SND_OPEN.getSource()); play.play();
		try { Desktop.getDesktop().open(keyFileChooser.getSelectedFile()); }
		catch (IOException ex) { log("Error: Desktop.getDesktop().open(keyFileChooser.getSelectedFile()); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
		targetFCPathList = new FCPathList(); this.updateDashboard(targetFCPathList);
		Platform.runLater(() -> { encryptButton.setDisable(true); decryptButton.setDisable(true);
		keyButton.setDisable(false); keyButton.setText(CREATE_KEY); });
	    }
        }
	else
	{
	    encryptButton.setDisable(true); decryptButton.setDisable(true);
	    keyButton.setDisable(false); keyButton.setText(CREATE_KEY);
	}
	
        keyFileChooser.setFileFilter(this.nonFinalCryptFilter);
        keyFileChooser.setFileFilter(keyFileChooser.getAcceptAllFileFilter()); // Resets rename due to doucle click file
        keyFileChooser.removeChoosableFileFilter(this.nonFinalCryptFilter);
    }

//  Doubleclicked item
    synchronized private void targetFileChooserActionPerformed(java.awt.event.ActionEvent evt)                                                 
    {
        this.fileProgressBar.setProgress(0);
        this.filesProgressBar.setProgress(0);
        if ((targetFileChooser != null)  && (targetFileChooser.getSelectedFiles() != null) && ( targetFileChooser.getSelectedFiles().length == 1 ))
        {
	    if (keyFCPath == null)
	    {
//							Validate.getFCPath(UI ui, String caller, Path path, boolean isKey, Path keyPath,    boolean disabledMAC,	boolean report)
		Path path = Paths.get("."); keyFCPath = Validate.getFCPath(   ui,	     "",      path,         false,         path, finalCrypt.disabledMAC,         true);
	    }
	    
	    
	    
//	    
	    Path targetPath = targetFileChooser.getSelectedFile().toPath();
	    
//					   getFCPath(UI ui,  String caller,  Path path,  boolean isKey,   Path keyPath,    boolean disabledMAC,	boolean report)
	    FCPath targetFCPath = Validate.getFCPath(this,		"", targetPath,		 false, keyFCPath.path,	finalCrypt.disabledMAC,		  true);
	    
	    if ((targetFCPath.type == FCPath.DEVICE) || (targetFCPath.type == FCPath.DEVICE_PROTECTED))
	    {
		tab.getSelectionModel().select(1);
		DeviceManager deviceManagerLocal = new DeviceManager(this); deviceManagerLocal.start(); deviceManagerLocal.printGPT(targetFCPath);
		targetFCPathList = new FCPathList(); updateDashboard(targetFCPathList);
		Platform.runLater(() -> { encryptButton.setDisable(true); decryptButton.setDisable(true); keyButton.setDisable(false); keyButton.setText(CREATE_KEY);});
	    }
	    else // Not a Device
	    {
//							device  minsize	 symlink  writable  status
		if ((targetFCPath.isValidFile) || (targetFCPath.type == FCPath.SYMLINK))
		{
		    if ((targetFCPath.isEncrypted) && ( targetFCPath.isDecryptable ) && ( keyFCPath != null ) && ( keyFCPath.isValidKey ))
		    {
			Thread decryptThread = new Thread(() ->
			{
			    FCPathList targetFCPathList1 = new FCPathList();
			    FCPathList filteredTargetFCPathList = new FCPathList();
			    targetFCPathList1.add(targetFCPath);
			    filteredTargetFCPathList.add(targetFCPath);
			    decrypt(targetFCPathList1, filteredTargetFCPathList, keyFCPath, true); // true means open after decrypt when finalcrypt calls processFinished
			});
			decryptThread.setName("decryptThread");
			decryptThread.setDaemon(true);
			decryptThread.start();
		    }
		    else // Not decryptable
		    {
			play = new AudioClip(this.SND_OPEN.getSource()); play.play();
			try { Desktop.getDesktop().open(targetFCPath.path.toFile()); } catch (IOException ex) { log("Error: Desktop.getDesktop().open(file); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
		    }
		    
		    
		    targetFCPathList = new FCPathList(); updateDashboard(targetFCPathList);
		    Platform.runLater(() -> { encryptButton.setDisable(true); decryptButton.setDisable(true); keyButton.setDisable(false); keyButton.setText(CREATE_KEY); });
		} // Not a device / file or symlink
	    }
        } else { encryptButton.setDisable(true); decryptButton.setDisable(true); }
        targetFileChooser.setFileFilter(this.nonFinalCryptFilter); targetFileChooser.setFileFilter(targetFileChooser.getAcceptAllFileFilter()); // Resets rename due to doucle click file
    }                                                

/////////////////////////////////////////////////////////////////////////////////////////////
    
    synchronized private void keyFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)
    {
	if ((!processRunning ) && (evt.getPropertyName().equals("SelectedFilesChangedProperty")))
	{
//	    remainingTimeHeaderLabel.setVisible(false); remainingTimeLabel.setVisible(false);
//	    elapsedTimeHeaderLabel.setVisible(false); elapsedTimeLabel.setVisible(false);
//	    totalTimeHeaderLabel.setVisible(false); totalTimeLabel.setVisible(false);
	    keyFileChooserPropertyCheck();
	}
    }
    
    synchronized private void keyFileChooserPropertyCheck() // getFCPath, checkModeReady
    {
        Platform.runLater(() ->
	{
	    keyNameLabel.setTextFill(Color.GREY); keyNameLabel.setText("");
	    keyTypeLabel.setTextFill(Color.GREY); keyTypeLabel.setText("");
	    keySizeLabel.setTextFill(Color.GREY); keySizeLabel.setText("");
//            keyValidLabel.setTextFill(Color.GREY); keyValidLabel.setText("");
	    checksumLabel.setTextFill(Color.GREY); checksumLabel.setText("");
	    if ( checksumTooltip != null ) { checksumTooltip.setText(""); Tooltip.uninstall(checksumLabel, checksumTooltip); }
	    pwdField.setDisable(true); pwdField.setVisible(false);
	    keyImageView.setOpacity(0.1);
	});

	keySourceChecksumReadEnded = true;
	keySourceChecksumReadCanceled = true;
	
	if (!processRunning)
	{
	    Platform.runLater(() -> 
	    {
		fileProgressBar.setProgress(0);
		filesProgressBar.setProgress(0);

//		remainingTimeHeaderLabel.setVisible(false); remainingTimeLabel.setVisible(false);
//		elapsedTimeHeaderLabel.setVisible(false); elapsedTimeLabel.setVisible(false);
//		totalTimeHeaderLabel.setVisible(false); totalTimeLabel.setVisible(false);
	    });

	    // En/Disable FileChooser deletebutton
	    if (
		    (keyFileChooser != null) &&
		    (keyFileChooser.getSelectedFile() != null) &&
		    (
			(Files.isRegularFile( keyFileChooser.getSelectedFile().toPath(), LinkOption.NOFOLLOW_LINKS)) ||
			(Files.isDirectory(keyFileChooser.getSelectedFile().toPath()))
		    ) 
	       )
	    { keyFileDeleteButton.setEnabled(true);} else {keyFileDeleteButton.setEnabled(false); }

	    // Set Buffer Size
	    finalCrypt.setBufferSize(finalCrypt.getBufferSizeDefault());

	    // Validate KeyFile
	    if ((keyFileChooser != null) && (keyFileChooser.getSelectedFile() != null) && (keyFileChooser.getSelectedFiles().length == 1))
	    {
		Path keyPath = keyFileChooser.getSelectedFiles()[0].toPath();
//				       getFCPath(UI ui, String caller,  Path path, boolean isKey, Path keyPath,    boolean disabledMAC, boolean report)
		keyFCPath = Validate.getFCPath(this,		   "",	  keyPath,          true,      keyPath, finalCrypt.disabledMAC,          true);

		Platform.runLater(() -> 
		{
		    if ((keyFCPath.isValidKey)) // Valid Key
		    {
			play = new AudioClip(this.SND_SELECTKEY.getSource()); play.play();
			keyNameLabel.setTextFill(Color.GREENYELLOW); keyNameLabel.setText(keyFCPath.path.getFileName().toString());
			checksumLabel.setTextFill(Color.WHITESMOKE);
			checksumLabel.setText("");
			if ( checksumTooltip != null )  { checksumTooltip.setText(""); Tooltip.uninstall(checksumLabel, checksumTooltip); }

			keyTypeLabel.setTextFill(Color.GREENYELLOW); keyTypeLabel.setText(FCPath.getTypeString(keyFCPath.type));
			keySizeLabel.setTextFill(Color.GREENYELLOW); keySizeLabel.setText(Validate.getHumanSize(keyFCPath.size,1));
//			keyValidLabel.setTextFill(Color.GREENYELLOW); keyValidLabel.setText(Boolean.toString(keyFCPath.isValidKey));

			if (pwdField.getText().length() == 0) { passwordHeaderLabel.setText("Password (optional)"); } else { passwordHeaderLabel.setText("Password (set)"); }
			pwdField.setVisible(true); pwdField.setDisable(false); finalCrypt.setPwd(pwdField.getText()); finalCrypt.resetPwdPos();
			keyImageView.setOpacity(0.8);

			targetFileChooserPropertyCheck(true);
		    }
		    else // Not Valid Key
		    {
			if (keyFCPath.type == FCPath.DIRECTORY)
			{
			    play = new AudioClip(this.SND_BUTTON.getSource()); play.play();
			    keyNameLabel.setTextFill(Color.GREY); keyNameLabel.setText("");
			    keyTypeLabel.setTextFill(Color.GREY); keyTypeLabel.setText("");
			    keySizeLabel.setTextFill(Color.GREY); keySizeLabel.setText("");
//			    keyValidLabel.setTextFill(Color.GREY); keyValidLabel.setText("");
			    checksumLabel.setTextFill(Color.GREY); checksumLabel.setText(""); checksumTooltip.setText(""); Tooltip.uninstall(checksumLabel, checksumTooltip);
			}
			else
			{
			    play = new AudioClip(this.SND_SELECTINVALID.getSource()); play.play();
			    keyNameLabel.setTextFill(Color.ORANGE); keyNameLabel.setText(keyFCPath.path.toString());
			    if (keyFCPath.type != FCPath.FILE)	{ keyTypeLabel.setTextFill(Color.ORANGERED); }
			    else					{ keyTypeLabel.setTextFill(Color.ORANGE); }
			    keyTypeLabel.setText(FCPath.getTypeString(keyFCPath.type));
			    if ( keyFCPath.size < FCPath.KEY_SIZE_MIN ) { keySizeLabel.setTextFill(Color.ORANGERED); } else { keySizeLabel.setTextFill(Color.ORANGE); } keySizeLabel.setText(Validate.getHumanSize(keyFCPath.size,1));
			    checksumLabel.setText(""); checksumTooltip.setText(""); Tooltip.uninstall(checksumLabel, checksumTooltip);
//			    keyValidLabel.setTextFill(Color.ORANGE); keyValidLabel.setText(Boolean.toString(keyFCPath.isValidKey));			    
			}
			
//			MySimpleFCFileVisitor.running = false;
//		        try { Thread.sleep(100); } catch (InterruptedException ex) {  }
			if ( keyFCPath != null ) { keyFCPath.isValidKey = false; }
			targetFCPathList = new FCPathList();

			pwdField.setDisable(true); passwordHeaderLabel.setText("Password"); pwdField.setVisible(false);
			keyImageView.setOpacity(0.1);

			userGuideMessage(SELECT_KEY, 64, false, false, true, false, VOI_SELECT_KEY, 0);

			buildReady(targetFCPathList, false);
		    }
		});
		
		// Checksum Calculation
		if ((keyFCPath.isValidKey)) // Valid Key
		{
		    if ( keyFCPath.size < (1024L * 1024L * 1024L * 1L) )
		    {
			Platform.runLater(() -> 
			{
			    checksumLabel.setTextFill(Color.WHITESMOKE);
			    checksumLabel.setText("Calculating...");
			    Tooltip.uninstall(checksumLabel, checksumTooltip);
			    calculateChecksum();
			});
		    }
		    else
		    {
			Platform.runLater(() -> 
			{
			    checksumLabel.setTextFill(Color.WHITESMOKE);
			    checksumLabel.setText("Click for checksum");
			    Tooltip.uninstall(checksumLabel, checksumTooltip);
			});
		    }
		}
	    }
	    else // Not a Valid Selection
	    {
//		log("CC Sel Not Valid\r\n");
		MySimpleFCFileVisitor.running = false;
//	        try { Thread.sleep(100); } catch (InterruptedException ex) {  }
		if ( keyFCPath != null ) { keyFCPath.isValidKey = false; }
		targetFCPathList = new FCPathList();
                
                Platform.runLater(() ->
		{
		    keyNameLabel.setTextFill(Color.GREY); keyNameLabel.setText("");
		    keyTypeLabel.setTextFill(Color.GREY); keyTypeLabel.setText("");
		    keySizeLabel.setTextFill(Color.GREY); keySizeLabel.setText("");
//                    keyValidLabel.setTextFill(Color.GREY); keyValidLabel.setText("");
		    checksumLabel.setTextFill(Color.GREY); checksumLabel.setText(""); checksumTooltip.setText(""); Tooltip.uninstall(checksumLabel, checksumTooltip);
		    userGuideMessage(SELECT_KEY, 64, false, false, true, false, VOI_SELECT_KEY, 0);
		});

		buildReady(targetFCPathList, false);
	    }
	}
    }

    synchronized private void calculateChecksum()
    {
	if (!isCalculatingCheckSum)
	{
	    isCalculatingCheckSum = true;
//	    log("calculateChecksum: " + Calendar.getInstance().getTimeInMillis() + "\r\n", true, true, false, false, false);
	    Platform.runLater(() -> 
	    {
		if ((keyFCPath.isValidKey)) // Valid Key
		{
		    // Calculate Key Checksum 
		    checksumBlock:
		    {
			keySourceChecksumReadEnded = false;
			keySourceChecksumReadCanceled = false;
			Thread calcKeyThread; calcKeyThread = new Thread(() -> // Relaxed interruptable thread
			{
			    long    readKeySourceChannelPosition =  0;
			    long    readKeySourceChannelTransfered =  0; 
			    int readKeySourceBufferSize = (1 * 1024 * 1024);
			    ByteBuffer keySourceBuffer = ByteBuffer.allocate(readKeySourceBufferSize); keySourceBuffer.clear();
			    MessageDigest messageDigest = null; try { messageDigest = MessageDigest.getInstance(FinalCrypt.HASH_ALGORITHM_NAME); } catch (NoSuchAlgorithmException ex) { log("Error: NoSuchAlgorithmException: MessageDigest.getInstance(\"SHA-256\")\r\n", true, true, true, true, false);}
			    int x = 0;
			    while (( ! keySourceChecksumReadEnded ) && ( ! keySourceChecksumReadCanceled ))
			    {
				try (final SeekableByteChannel readKeySourceChannel = Files.newByteChannel(keyFCPath.path, EnumSet.of(StandardOpenOption.READ,StandardOpenOption.SYNC)))
				{
				    readKeySourceChannel.position(readKeySourceChannelPosition);
				    readKeySourceChannelTransfered = readKeySourceChannel.read(keySourceBuffer); keySourceBuffer.flip(); readKeySourceChannelPosition += readKeySourceChannelTransfered;
				    readKeySourceChannel.close();

				    messageDigest.update(keySourceBuffer);
				    if ( readKeySourceChannelTransfered < 0 ) { keySourceChecksumReadEnded = true; }
				} catch (IOException ex)
				{
				    Platform.runLater(() -> { keySourceChecksumReadEnded = true; });
				}
				x++;
				keySourceBuffer.clear();
			    }
			    
			    if ( ! keySourceChecksumReadCanceled )
			    {
				byte[] hashBytes = messageDigest.digest();
				String hashString = getHexString(hashBytes,2);
				Platform.runLater(() ->
				{
				    checksumLabel.setTextFill(Color.GREENYELLOW);
				    checksumLabel.setText(hashString);
				    if ( checksumTooltip != null ) { checksumTooltip.setText(hashString + "\r\n\r\ncalculate checksum: left-click\r\ncopy to clipboard:  right-click"); }
				    Tooltip.install(checksumLabel, checksumTooltip); 
				    isCalculatingCheckSum = false;
				});
			    }
			    else
			    {
				messageDigest.reset();
				Tooltip.uninstall(checksumLabel, checksumTooltip);
				isCalculatingCheckSum = false;
			    }
			});
			calcKeyThread.setName("calcKeyThread"); calcKeyThread.setDaemon(true); calcKeyThread.start();
		    }
//		    targetFileChooserPropertyCheck(true);
		}
	    });
	}
    }
    
    @FXML private void checksumLabelOnMouseClicked(MouseEvent event)
    {
	Platform.runLater(() ->
	{ 
	    if ( event.getButton() == MouseButton.PRIMARY )
	    {
		if ((keyFCPath != null) && (keyFCPath.isValidKey))
		{
		    play = new AudioClip(this.SND_BUTTON.getSource()); play.play();
		    checksumLabel.setTextFill(Color.WHITESMOKE);
		    checksumLabel.setText("Calculating..."); checksumTooltip.setText("");
		    Platform.runLater(() ->	{ calculateChecksum(); });
		}
	    }
	    else if ( event.getButton() == MouseButton.SECONDARY )
	    {
		if ((keyFCPath != null) && (keyFCPath.isValidKey))
		{
		    play = new AudioClip(this.SND_BUTTON.getSource()); play.play();
		    Thread blinkThread = new Thread(() ->
		    {
			Paint oldColor = checksumLabel.getTextFill();
			checksumLabel.setTextFill(Color.WHITE);
			try { Thread.sleep(100); } catch (InterruptedException ex) {  }
			checksumLabel.setTextFill(oldColor);
		    }); blinkThread.setName("blinkThread"); blinkThread.setDaemon(true); blinkThread.start();

		    final ClipboardContent content = new ClipboardContent();
		    content.putString(checksumLabel.getText());
		    Clipboard.getSystemClipboard().setContent(content);
		}
	    }
	});
    }
    
    synchronized public static String getHexString(byte[] bytes, int digits) { String returnString = ""; for (byte mybyte:bytes) { returnString += getHexString(mybyte, digits); } return returnString; }
    synchronized public static String getHexString(byte value, int digits) { return String.format("%0" + Integer.toString(digits) + "X", (value & 0xFF)).replaceAll("[^A-Za-z0-9]",""); }

//  FileChooser Listener methods
    synchronized private void targetFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)
    {
	if ((!processRunning ) && (evt.getPropertyName().equals("SelectedFilesChangedProperty")))
	{
//	    remainingTimeHeaderLabel.setVisible(false); remainingTimeLabel.setVisible(false);
//	    elapsedTimeHeaderLabel.setVisible(false); elapsedTimeLabel.setVisible(false);
//	    totalTimeHeaderLabel.setVisible(false); totalTimeLabel.setVisible(false);
	    targetFileChooserPropertyCheck(true);
	}
    }
    
    synchronized private void targetFileChooserPropertyCheck(boolean status)
    {
//	if ((!processRunning ) && (!MySimpleFCFileVisitor.running))
	if ((!processRunning ))
	{
//	    processRunning = true;
	    Platform.runLater(() -> 
	    {
		encryptButton.setDisable(true);
		decryptButton.setDisable(true);
		keyButton.setDisable(true);
		pauseToggleButton.setDisable(true);
		stopButton.setDisable(true);

		fileProgressBar.setProgress(0);
		filesProgressBar.setProgress(0);

//		remainingTimeHeaderLabel.setVisible(false); remainingTimeLabel.setVisible(false);
//		elapsedTimeHeaderLabel.setVisible(false); elapsedTimeLabel.setVisible(false);
//		totalTimeHeaderLabel.setVisible(false); totalTimeLabel.setVisible(false);
	    });
	    
	    ArrayList<Path> targetPathList = new ArrayList<>(); targetPathList.clear();
	    targetFileDeleteButton.setEnabled(false);

	    // En/Disable FileChooser deletebutton
	    if (
		    (targetFileChooser != null) &&
		    (targetFileChooser.getSelectedFile() != null) &&
		    (targetFileChooser.getSelectedFile().toPath() != null) &&
		    (
			(Files.isRegularFile( targetFileChooser.getSelectedFile().toPath(), LinkOption.NOFOLLOW_LINKS)) ||
			(Files.isDirectory(targetFileChooser.getSelectedFile().toPath()))
		    ) 
	       )
	    { targetFileDeleteButton.setEnabled(true); } else {targetFileDeleteButton.setEnabled(false); }
	    
	    targetFCPathList = new FCPathList();// targetFCPathList.clear();
	    final FCPathList targetFCPathList2 = targetFCPathList;
	    final UI ui = this;
	    if (updateDashboardTaskTimer != null) { updateDashboardTaskTimer.cancel(); updateDashboardTaskTimer.purge(); }
	
	    // All Valid
	    if ((targetFileChooser != null) && (targetFileChooser.getSelectedFiles() != null) && (targetFileChooser.getSelectedFiles().length > 0) && (keyFCPath != null) && (keyFCPath.isKey) && (keyFCPath.isValidKey))
	    {
//		MySimpleFCFileVisitor.running = false;
//	        try { Thread.sleep(100); } catch (InterruptedException ex) {  }
//		log("TC All Valid\r\n");
		Validate.bytesCount = 0;

		// Gather User Selection in list
		for (File file:targetFileChooser.getSelectedFiles()) { targetPathList.add(file.toPath()); }
//		log("list: " + targetPathList.size() + "\r\n", true, false, false, false, false);
		

    //		Get Globbing Pattern String
		pattern = "glob:*"; try { pattern = getSelectedPatternFromFileChooser( targetFileChooser.getFileFilter()); } catch (ClassCastException exc) {  }

		// UPdate Dashboard during buildSelection

		updateDashboardTask = new TimerTask() { @Override public void run() { updateDashboard(targetFCPathList2); }}; updateDashboardTaskTimer = new java.util.Timer(); updateDashboardTaskTimer.schedule(updateDashboardTask, 200L, 200L);

		// Scanning animation on main progressbar
		Platform.runLater(() ->
		{
		    if (pwdField.getText().length() == 0) { passwordHeaderLabel.setText("Password (optional)"); } else { passwordHeaderLabel.setText("Password (set)"); }
		    pwdField.setDisable(true); pwdField.setVisible(true); finalCrypt.setPwd(pwdField.getText()); finalCrypt.resetPwdPos();
		    keyImageView.setOpacity(0.8);
		    filesProgressBar.setVisible(true); filesProgressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
		});

		// BuildSelection
//		cursorWait();

		Platform.runLater(() -> // Not on FX Thread
		{
		    play = new AudioClip(this.SND_READY.getSource()); play.play();
		    //		    textLabelBlurMessage("Scanning", 500);
		    userGuideMessage(SCANNING_FILES, 64, false, false, false, false, VOI_SCANNING_FILES, 0);
		    
		    Thread scanThread = new Thread(() -> // Relaxed interruptable thread
		    {
//				 buildSelection(UI ui, ArrayList<Path> pathList, FCPath keyFCPath, FCPathList targetFCPathList, boolean symlink, String pattern, boolean negatePattern,	   boolean disabledMAC, boolean status)
			Validate.buildSelection(ui,		 targetPathList,	keyFCPath,	     targetFCPathList2,		symlink,	pattern,	 negatePattern,	finalCrypt.disabledMAC,		false);
		    }); scanThread.setName("scanThread"); scanThread.setDaemon(true); scanThread.start();
		});
	    }
	    else // Not all valid
	    {		
//		log("Not all valid: " + targetPathList.size() + "\r\n", true, false, false, false, false);
//		MySimpleFCFileVisitor.running = false;
//		try { Thread.sleep(100); } catch (InterruptedException ex) {  }
		
		if ((keyFCPath != null) && (keyFCPath.isKey) && (keyFCPath.isValidKey))
		{
		    userGuideMessage(SELECT_FILES, 64, false, true, false, false, VOI_SELECT_FILES, 0);
		}
		else
		{
		    userGuideMessage(SELECT_KEY, 64, false, false, true, false, VOI_SELECT_KEY, 0);
		}
		
		targetFCPathList = new FCPathList();
		buildReady(targetFCPathList, false);
	    }
	}
    }
    
    @Override public void buildReady(FCPathList fcPathListParam, boolean validBuild)
    {
	Platform.runLater(() ->
	{
	    filesProgressBar.setProgress(0); filesProgressBar.setVisible(false); pwdField.setDisable(false);
	});
	if (updateDashboardTaskTimer != null) { updateDashboardTaskTimer.cancel(); updateDashboardTaskTimer.purge(); }
	MySimpleFCFileVisitor.running = false;
	isCalculatingCheckSum = false;
	
	if (fcPathListParam.size() > 0)
	{
//	    log("buildReady > 0\r\n");
	    updateDashboard(fcPathListParam);
	    checkModeReady(fcPathListParam,validBuild);
	}
	else
	{
//	    log("buildReady == 0\r\n");
	    fcPathListParam.clearStats();
	    encryptableList = new FCPathList();
	    updateDashboard(fcPathListParam);
	    checkModeReady(fcPathListParam,validBuild);
	}
//	if ( validBuild ) { textLabelBlurMessage("FinalCrypt", 500); }
    }

    private void updateDashboard(FCPathList targetFCPathList)
    {
//	log(s + "\r\n" + targetFCPathList.getStats());
	Platform.runLater(() -> 
	{
	    // Skipping / Info Column
	    if ( (targetFCPathList.emptyFiles > 0) || (targetFCPathList.symlinkFiles > 0)  || (targetFCPathList.unreadableFiles > 0) || (targetFCPathList.unwritableFiles > 0))
	    { targetWarningLabel.setTextFill(Color.ORANGE); targetWarningLabel.setText("Skipping"); } else { targetWarningLabel.setTextFill(Color.GRAY); targetWarningLabel.setText("Invalid"); }
	    
	    if ( targetFCPathList.emptyFiles > 0 )	    { emptyFilesLabel.setTextFill(Color.ORANGE); } else { emptyFilesLabel.setTextFill(Color.GRAY); }
	    if ( targetFCPathList.symlinkFiles > 0 )	    { symlinkFilesLabel.setTextFill(Color.ORANGE); } else { symlinkFilesLabel.setTextFill(Color.GRAY); }
	    if ( targetFCPathList.unreadableFiles > 0 )	    { unreadableFilesLabel.setTextFill(Color.ORANGE); } else { unreadableFilesLabel.setTextFill(Color.GRAY); }
//	    if ( targetFCPathList.unreadableFilesSize > 0 ) { unreadableFilesSizeLabel.setTextFill(Color.ORANGE); } else { unreadableFilesSizeLabel.setTextFill(Color.GRAY); }
if ( targetFCPathList.unwritableFiles > 0 )	    { unwritableFilesLabel.setTextFill(Color.ORANGE); } else { unwritableFilesLabel.setTextFill(Color.GRAY); }
//	    if ( targetFCPathList.unwritableFilesSize > 0 ) { unwritableFilesSizeLabel.setTextFill(Color.ORANGE); } else { unwritableFilesSizeLabel.setTextFill(Color.GRAY); }
if ( targetFCPathList.hiddenFiles > 0 )	    { hiddenFilesLabel.setTextFill(Color.YELLOW); } else { hiddenFilesLabel.setTextFill(Color.GRAY); }
//	    if ( targetFCPathList.hiddenFilesSize > 0 )	    { hiddenFilesSizeLabel.setTextFill(Color.YELLOW); } else { hiddenFilesSizeLabel.setTextFill(Color.GRAY); }

emptyFilesLabel.setText(Long.toString(targetFCPathList.emptyFiles));
symlinkFilesLabel.setText(Long.toString(targetFCPathList.symlinkFiles));
unreadableFilesLabel.setText(Long.toString(targetFCPathList.unreadableFiles));
unreadableFilesLabel.getTooltip().setText("Click to list unreadable files (" + Validate.getHumanSize(targetFCPathList.unreadableFilesSize,1) + ")");
//	    unreadableFilesSizeLabel.setText("Click to list unreadable files (" + Validate.getHumanSize(targetFCPathList.unreadableFilesSize,1) + ")");
unwritableFilesLabel.setText(Long.toString(targetFCPathList.unwritableFiles));
unwritableFilesLabel.getTooltip().setText("Click to list unwritable files (" + Validate.getHumanSize(targetFCPathList.unwritableFilesSize,1) + ")");
//	    unwritableFilesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.unwritableFilesSize,1));
hiddenFilesLabel.setText(Long.toString(targetFCPathList.hiddenFiles));
hiddenFilesLabel.getTooltip().setText("Click to list hidden files (" + Validate.getHumanSize(targetFCPathList.hiddenFilesSize,1) + ")");
//	    hiddenFilesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.hiddenFilesSize,1));

// Decrypted Column
decryptedLabel.setText(Long.toString(targetFCPathList.decryptedFiles));
decryptedSizeLabel.setText(Validate.getHumanSize(targetFCPathList.decryptedFilesSize,1));

encryptableLabel.setText(Long.toString(targetFCPathList.encryptableFiles));
encryptableSizeLabel.setText(Validate.getHumanSize(targetFCPathList.encryptableFilesSize,1));

//	    newEncryptedLabel.setText(Long.toString(targetFCPathList.newEncryptedFiles));
//	    newEncryptedSizeLabel.setText(Validate.getHumanSize(targetFCPathList.newEncryptedFilesSize,1));
	    
//	    encryptRemainingLabel.setText(Long.toString(targetFCPathList.encryptRemainingFiles));
//	    encryptRemainingSizeLabel.setText(Validate.getHumanSize(targetFCPathList.encryptRemainingFilesSize,1));

unencryptableLabel.setText(Long.toString(targetFCPathList.unEncryptableFiles));
unencryptableSizeLabel.setText(Validate.getHumanSize(targetFCPathList.unEncryptableFilesSize,1));

// Encrypted Column
encryptedLabel.setText(Long.toString(targetFCPathList.encryptedFiles));
encryptedSizeLabel.setText(Validate.getHumanSize(targetFCPathList.encryptedFilesSize,1));

decryptableLabel.setText(Long.toString(targetFCPathList.decryptableFiles));
decryptableSizeLabel.setText(Validate.getHumanSize(targetFCPathList.decryptableFilesSize,1));

//	    newDecryptedLabel.setText(Long.toString(targetFCPathList.newDecryptedFiles));
//	    newDecryptedSizeLabel.setText(Validate.getHumanSize(targetFCPathList.newDecryptedFilesSize,1));
	    
//	    decryptRemainingLabel.setText(Long.toString(targetFCPathList.decryptRemainingFiles));
//	    decryptRemainingSizeLabel.setText(Validate.getHumanSize(targetFCPathList.decryptRemainingFilesSize,1));

undecryptableLabel.setText(Long.toString(targetFCPathList.unDecryptableFiles));
undecryptableSizeLabel.setText(Validate.getHumanSize(targetFCPathList.unDecryptableFilesSize,1));

// Totals Column
validFilesLabel.setText(Long.toString(targetFCPathList.validFiles));
validFilesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.validFilesSize,1));

validDevicesLabel.setText(Long.toString(targetFCPathList.validDevices));
validDevicesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.validDevicesSize,1));

validPartitionsLabel.setText(Long.toString(targetFCPathList.validPartitions));
validPartitionsSizeLabel.setText(Validate.getHumanSize(targetFCPathList.validPartitionsSize,1));

invalidFilesLabel.setText(Long.toString(targetFCPathList.files - targetFCPathList.validFiles));
invalidFilesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.filesSize - targetFCPathList.validFilesSize,1));

totalFilesLabel.setText(Long.toString(targetFCPathList.files));
filesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.filesSize,1));
	});
    }
    
    private void disableButtons()
    {
	Platform.runLater(() -> 
	{
	    encryptButton.setDisable(true);
	    decryptButton.setDisable(true);
	    pauseToggleButton.setDisable(true);
	    stopButton.setDisable(true);
	});
    }
    synchronized private void checkModeReady(FCPathList targetFCPathList, boolean validBuild)
    {
	Platform.runLater(() ->
	{
	    if (!processRunning)
	    {
		encryptButton.setDisable(true);
		decryptButton.setDisable(true);
		keyButton.setDisable(true);
		pauseToggleButton.setDisable(true);
		stopButton.setDisable(true);
				
		if ((keyFCPath != null) && (keyFCPath.isValidKey))
		{
// ================================================================================================================================================================================================
		    // Decrypted Files
		    
		    if (targetFCPathList.decryptedFiles > 0)	{ decryptedList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.isDecrypted); } else { decryptedList = null; }
		    if (targetFCPathList.encryptableFiles > 0) // Encryptables
		    {
			play = new AudioClip(this.SND_SELECT.getSource()); play.play();
			encryptableList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.isEncryptable);
			encryptButton.setDisable(false); pauseToggleButton.setDisable(true); stopButton.setDisable(true);
		    } else { encryptButton.setDisable(true); encryptableList = null; }
		    if (targetFCPathList.newEncryptedFiles > 0)	    { newEncryptedList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.isNewEncrypted); } else { newEncryptedList = null; }
//		    if (targetFCPathList.encryptRemainingFiles > 0) { encryptRemainingList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.); } else { encryptRemainingList = null; }
		    if (targetFCPathList.unEncryptableFiles > 0)    { unencryptableList = filter(targetFCPathList,(FCPath fcPath) -> (fcPath.isUnEncryptable) && (fcPath.isDecrypted)  && (fcPath.size > 0)); } else { unencryptableList = null; }

		    // ================================================================================================================================================================================================
		    // Encrypted Files

		    // Decryptables
		    if (targetFCPathList.encryptedFiles > 0)	{ encryptedList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.isEncrypted); } else { encryptedList = null; }
		    if ((targetFCPathList.decryptableFiles > 0) && ( ! finalCrypt.disabledMAC) ) // Prevents destruction! Non-MAC Mode encrypting MAC encrypted files (in stead of default decryption)
		    {
			play = new AudioClip(this.SND_SELECT.getSource()); play.play();
			decryptableList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.isDecryptable);
			decryptButton.setDisable(false); pauseToggleButton.setDisable(true); stopButton.setDisable(true);
		    } else { decryptButton.setDisable(true); decryptableList = null; }
		    if (targetFCPathList.newDecryptedFiles > 0)	    { newDecryptedList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.isNewDecrypted); } else { newDecryptedList = null; }
		    //		    if (targetFCPathList.decryptRemainingFiles > 0) { decryptRemainingList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.); } else { decryptRemainingList = null; }
		    if (targetFCPathList.unDecryptableFiles > 0)    { undecryptableList = filter(targetFCPathList,(FCPath fcPath) -> (fcPath.isUnDecryptable) && (fcPath.isEncrypted) && (fcPath.size > 0)); } else { undecryptableList = null; }

		    // ================================================================================================================================================================================================
		    // Others empty sym read write hidden

		    if (targetFCPathList.emptyFiles > 0)	{ emptyList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.size == 0 && fcPath.type == FCPath.FILE); } else { emptyList = null; }
		    if (targetFCPathList.symlinkFiles > 0)	{ symlinkList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.SYMLINK); } else { symlinkList = null; }
		    if (targetFCPathList.unreadableFiles > 0)	{ unreadableList = filter(targetFCPathList,(FCPath fcPath) -> (! fcPath.isReadable) && (fcPath.type == FCPath.FILE)); } else { unreadableList = null; }
		    if (targetFCPathList.unwritableFiles > 0)	{ unwritableList = filter(targetFCPathList,(FCPath fcPath) -> (! fcPath.isWritable) && (fcPath.type == FCPath.FILE)); } else { unwritableList = null; }
		    if (targetFCPathList.hiddenFiles > 0)	{ hiddenList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.isHidden); } else { hiddenList = null; }

		    if ((targetFCPathList.files - targetFCPathList.validFiles) > 0) { invalidFilesList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.INVALID); } else { invalidFilesList = null; }


		    if ((keyFCPath.type == FCPath.FILE) && (keyFCPath.isValidKey)) // Key Device Selected
		    {
			if (targetFCPathList.validDevices > 0)
			{
//			    log("1 " + keyFCPath.getString());
//			    createKeyList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE); // log("Create Key List:\r\n" + createKeyList.getStats());
			    pauseToggleButton.setDisable(true); stopButton.setDisable(true);
			    keyButton.setDisable(false); keyButton.setText(CREATE_KEYDEV);
			} else { keyButton.setDisable(false); keyButton.setText(CREATE_KEY); }
		    }
		    else if (keyFCPath.type == FCPath.DEVICE)
		    {
//			Clone Key Device
			if ((targetFCPathList.validDevices > 0) && (targetFCPathList.matchingKey == 0))
			{
//			    cloneKeyList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE && fcPath.path.compareTo(keyFCPath.path) != 0); // log("Clone Key List:\r\n" + cloneKeyList.getStats());
			    keyButton.setDisable(false); keyButton.setText(CLONE_KEYDEV); pauseToggleButton.setDisable(true); stopButton.setDisable(true);
			} else { keyButton.setDisable(false); keyButton.setText(CREATE_KEY); }
		    } else { keyButton.setDisable(false); keyButton.setText(CREATE_KEY); }

		    if (validBuild)
		    {
			if (! keyFCPath.isValidKey)
			{
			    userGuideMessage(SELECT_KEY, 64, false, false, true, false, VOI_SELECT_KEY, 0);
			}
			else
			{
			    if	((targetFCPathList.encryptedFiles > 0) && (targetFCPathList.decryptableFiles == 0))
			    {
				if (targetFCPathList.encryptedFiles > 0) { play = new AudioClip(this.SND_SELECTINVALID.getSource()); play.play(); userGuideMessage(WRONG_KEY_PASS, 48, false, false, true, false, VOI_WRONG_KEY_OR_PASSWORD, 0); }
			    }
			    else
			    {
				if	((targetFCPathList.encryptableFiles == 0) && (targetFCPathList.decryptableFiles == 0))	{ userGuideMessage(SELECT_FILES, 64, false, true, false, false, VOI_SELECT_FILES, 0); }
				else if ((targetFCPathList.encryptableFiles == 0) && (targetFCPathList.decryptableFiles > 0))	{ userGuideMessage(DECRYPT_FILES, 64, true, false, false, false, VOI_DECRYPT_FILES, 0); }
				else if ((targetFCPathList.encryptableFiles > 0) && (targetFCPathList.decryptableFiles == 0))	{ userGuideMessage(ENCRYPT_FILES, 64, true, false, false, false, VOI_ENCRYPT_FILES, 0); }
				else if ((targetFCPathList.encryptableFiles > 0) && (targetFCPathList.decryptableFiles > 0))	{ userGuideMessage(EN_DECRYPT_FILES, 64, true, false, false, false, VOI_ENCRYPT_OR_DECRYPT_FILES, 0); }
			    }
			}
		    }
		}
		else
		{
		    encryptButton.setDisable(true); decryptButton.setDisable(true);
		    keyButton.setDisable(false); keyButton.setText(CREATE_KEY); // Default enabler
		}
	    }
	});
    }

    synchronized public  FCPathList filter(ArrayList<FCPath> fcPathList, Predicate<FCPath> fcPath)
    {
	FCPathList result = new FCPathList();
	for (FCPath fcPathItem : fcPathList) { if (fcPath.test(fcPathItem)) { result.add(fcPathItem); } }
	return result;
    }
    
    synchronized public static Predicate<FCPath> isHidden() { return (FCPath fcPath) -> fcPath.isHidden; }
    
    synchronized public List<FCPath> filter(Predicate<FCPath> criteria, ArrayList<FCPath> list)
    {
	return list.stream().filter(criteria).collect(Collectors.<FCPath>toList());
    }
        
    public boolean targetFileChooserComponentAlteration(Container container)
    {
        Component[] components = container.getComponents();
        for (Component component : components)
        {
	    if(component instanceof Container) { component.setFont(new Font("System",Font.PLAIN,FILE_CHOOSER_FONT_SIZE)); }
	    
//            // Click "details view" ToggleButton
            if (component instanceof JToggleButton)
            {
                if (   ! ((JToggleButton)component).isSelected()   )
                {
                    // Needs a delay for proper column width 
                    TimerTask targetFileChoosershowDetailsTask = new TimerTask() { @Override public void run()
                    {
                        ((JToggleButton)component).doClick();
                    }};
                    Timer targetFileChoosershowDetailsTaskTimer = new java.util.Timer(); targetFileChoosershowDetailsTaskTimer.schedule(targetFileChoosershowDetailsTask, 2000L);
                }
            }
            
            // Add Delete button
            if (component instanceof JButton)
            {
                if (((JButton) component).getActionCommand().equalsIgnoreCase("New Folder"))
                {
                    component.getParent().add(this.targetFileDeleteButton);
                }
            }
            
//            // Remove the path textfield
//            if (component instanceof JTextField)
//            {
//                ((JTextField)component).setEnabled(false);
//                ((JTextField)component).setVisible(false);                
////                return true;
//            }
//            
//            // Remove the lower filefilter box
//            if (component instanceof JComboBox)
//            {
//                if ( ((JComboBox)component).getSelectedItem().toString().toLowerCase().contains("BasicFileChooserUI".toLowerCase()) )
//                {
//                    ((JComboBox)component).setEnabled(false);
//                    ((JComboBox)component).setVisible(false);                
//                }
////                return true;
//            }
//            
//            // Remove the lower labels
//            if (component instanceof JLabel)
//            {
//                ((JLabel)component).setEnabled(false);
//                ((JLabel)component).setVisible(false);                
////                return true;
//            }
            
            if (component instanceof Container)
            {
                if( targetFileChooserComponentAlteration((Container) component) ) return false;
            }
        }
        return false;
    }

    public boolean keyFileChooserComponentAlteration(Container container)
    {
        Component[] components = container.getComponents();
        for (Component component : components)
        {
	    if(component instanceof Container) { component.setFont(new Font("System",Font.PLAIN,FILE_CHOOSER_FONT_SIZE)); }
	    
//            // Click "details view" ToggleButton
            if (component instanceof JToggleButton)
            {
                if (   ! ((JToggleButton)component).isSelected()   )
                {
                    TimerTask keyFileChoosershowDetailsTask = new TimerTask() { @Override public void run()
                    {
                        ((JToggleButton)component).doClick();
                    }};
                    Timer keyFileChoosershowDetailsTaskTimer = new java.util.Timer(); keyFileChoosershowDetailsTaskTimer.schedule(keyFileChoosershowDetailsTask, 1500L);
                }
            }
            
            // Add Delete button
            if (component instanceof JButton)
            {
                if (((JButton) component).getActionCommand().equalsIgnoreCase("New Folder"))
                {
//                    component.getParent().add(this.targetFileDeleteButton);
//                    if (targetFileChooserContainer) { component.getParent().add(this.targetFileDeleteButton); } else { component.getParent().add(this.keyFileDeleteButton); }
                    component.getParent().add(this.keyFileDeleteButton);
                }
            }
            
//            // Remove the path textfield
//            if (component instanceof JTextField)
//            {
//                ((JTextField)component).setEnabled(false);
//                ((JTextField)component).setVisible(false);                
////                return true;
//            }
//            
//            // Remove the lower filefilter box
//            if (component instanceof JComboBox)
//            {
//                if ( ((JComboBox)component).getSelectedItem().toString().toLowerCase().contains("BasicFileChooserUI".toLowerCase()) )
//                {
//                    ((JComboBox)component).setEnabled(false);
//                    ((JComboBox)component).setVisible(false);                
//                }
////                return true;
//            }
//            
//            // Remove the lower labels
//            if (component instanceof JLabel)
//            {
//                ((JLabel)component).setEnabled(false);
//                ((JLabel)component).setVisible(false);                
////                return true;
//            }
            
            if (component instanceof Container)
            {
                if( keyFileChooserComponentAlteration((Container) component) ) return true;
            }
        }
        return false;
    }

    @FXML
    private void encryptButtonAction(ActionEvent event)
    {
	play = new AudioClip(this.SND_ENCRYPTFILES.getSource()); play.play();
        Thread encryptThread = new Thread(new Runnable()
        {
            private DeviceManager deviceManager;
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
		encrypt(targetFCPathList, encryptableList, keyFCPath);
            }
        });
        encryptThread.setName("encryptThread");
        encryptThread.setDaemon(true);
        encryptThread.start();
    }

    private void encrypt(FCPathList targetSourceFCPathList, FCPathList filteredTargetSourceFCPathList, FCPath keySourceFCPath) // Only run within thread
    {
	Runtime.getRuntime().addShutdownHook(new Thread()
	{
	    @Override public void run()
	    {
		if (finalCrypt.processRunning)
		{
		    finalCrypt.setStopPending(true);
		    try{ Thread.sleep(2000); } catch (InterruptedException ex) {}
		    log("\r\nEncryption User Interrupted...\r\n", false, true, true, false, false);
		}
	    }
	});

	processRunningMode = ENCRYPT_MODE; filesProgressBar.setProgress(0.0); fileProgressBar.setProgress(0.0);
	processStarted();
//	finalCrypt.encryptSelection(targetFCPathList, encryptableList, keyFCPath, true, pwdField.getText(), false);
	finalCrypt.encryptSelection(targetSourceFCPathList, filteredTargetSourceFCPathList, keyFCPath, true, pwdField.getText(), false);
    }

    @FXML
    private void decryptButtonAction(ActionEvent event)
    {
	play = new AudioClip(this.SND_DECRYPTFILES.getSource()); play.play();
        Thread encryptThread = new Thread(new Runnable()
        {
            private DeviceManager deviceManager;
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
		decrypt(targetFCPathList, decryptableList, keyFCPath, false);
            }
        });
        encryptThread.setName("decryptThread");
        encryptThread.setDaemon(true);
        encryptThread.start();
    }

    private void decrypt(FCPathList targetSourceFCPathList, FCPathList filteredTargetSourceFCPathList, FCPath keySourceFCPath, boolean open) // Only run within thread | open opens targets after decryption
    {
	Runtime.getRuntime().addShutdownHook(new Thread()
	{
	    @Override public void run()
	    {
		if (finalCrypt.processRunning)
		{
		    finalCrypt.setStopPending(true);
		    try{ Thread.sleep(2000); } catch (InterruptedException ex) {}
		    log("\r\nDecryption User Interrupted...\r\n", false, true, true, false, false);
		}
	    }
	});
	
	processRunningMode = DECRYPT_MODE; filesProgressBar.setProgress(0.0); fileProgressBar.setProgress(0.0);
	processStarted();
//	finalCrypt.encryptSelection(targetFCPathList, decryptableList, keyFCPath, false, pwdField.getText(), open);
	finalCrypt.encryptSelection(targetSourceFCPathList, filteredTargetSourceFCPathList, keyFCPath, false, pwdField.getText(), open);
    }

    @FXML
    private void createKeyLabelOnMouseClicked(MouseEvent event) { play = new AudioClip(this.SND_OPEN.getSource()); play.play(); createOTPKeyFile(); }

    @FXML
    private void keyButtonOnAction(ActionEvent event) { createOTPKeyFile(); }
    
    synchronized private void createOTPKeyFile()
    {
        // Needs Threading to early split off from the UI Event Dispatch Thread
        final GUIFX guifx = this;
        final UI ui = this;

	Platform.runLater(() ->
	{
	    passwordHeaderLabel.setText("Password"); 
	    pwdField.setText("");
	    pwdField.setDisable(true);
	    pwdField.setVisible(false);
	    keyImageView.setOpacity(0.1);
	    
	    FinalCrypt.setPwd("");
	    FinalCrypt.resetPwdPos();
	});

	Thread encryptThread = new Thread(new Runnable()
        {
            private DeviceManager deviceManager;
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
		
		if ( keyButton.getText().equals(CREATE_KEY) )
		{
		    Platform.runLater(() ->
		    {
			play = new AudioClip(SND_OPEN.getSource()); play.play(); 
			createOTPKeyStage = new Stage();
			createOTPKey = new CreateOTPKey();
			try { createOTPKey.start(createOTPKeyStage); } catch (Exception ex) { System.err.println(ex.getMessage()); }
			createOTPKey.controller.setCurrentDir(keyFileChooser.getCurrentDirectory().toPath().toAbsolutePath(), guifx); // Parse parameters onto global controller references always through controller
		    });
		}
		else if	( keyButton.getText().equals(CREATE_KEYDEV) )
		{
		    play = new AudioClip(SND_ENCRYPTFILES.getSource()); play.play(); 
		    processRunningMode = CREATE_KEYDEV_MODE;
		    tab.getSelectionModel().select(1);
                    processStarted();
                    deviceManager = new DeviceManager(guifx); deviceManager.start();
                    deviceManager.createKeyDevice(keyFCPath, (FCPath) targetFCPathList.get(0));
                    processFinished(targetFCPathList, false);
		}
		else if ( keyButton.getText().equals(CLONE_KEYDEV) )
		{
		    play = new AudioClip(SND_ENCRYPTFILES.getSource()); play.play(); 
		    processRunningMode = CLONE_KEYDEV_MODE;
		    tab.getSelectionModel().select(1);
                    processStarted();
                    deviceManager = new DeviceManager(ui); deviceManager.start();
                    deviceManager.cloneKeyDevice(keyFCPath, (FCPath) targetFCPathList.get(0));
                    processFinished(targetFCPathList, false);
		    play = new AudioClip(SND_SHUTDOWN.getSource()); play.play();
		}
		
            }
        });
        encryptThread.setName("keyDeviceThread");
        encryptThread.setDaemon(true);
        encryptThread.start();
    }
    
//  ================================================= BEGIN UPDATE PROGRESS ===========================================================
//    @Override public void buildProgress(FCPathList targetFCPathList) { updateDashboard(targetFCPathList); }

//    public void setStageTitle(String title) { PlatformImpl.runAndWait(new Runnable() { @Override public void run() { guifx.stage.setTitle(title); } });}
    public void setStageTitle(String title) { Platform.runLater(() ->
    {
	guifx.stage.setTitle(title);
    });}
    public void statusDirect(String status) { statusLabel.setText(status); }

    @Override public void processStarted()
    {
        Platform.runLater(() ->
	{
	    encryptionModeToggleButton.setMouseTransparent(!encryptionModeToggleButton.isMouseTransparent());
	    encryptionModeAnchorPane.setMouseTransparent(!encryptionModeAnchorPane.isMouseTransparent());
	    
	    // Clocks
	    elapsedTimeLabel.setText("00:00:00");
	    remainingTimeLabel.setText("00:00:00");
	    totalTimeLabel.setText("00:00:00");
	    
	    startTimeCalendar = Calendar.getInstance(Locale.ROOT);
	    start2TimeCalendar = Calendar.getInstance(Locale.ROOT);
	    offsetTimeCalendar = Calendar.getInstance(Locale.ROOT);
	    offsetTimeCalendar.setTimeInMillis(start2TimeCalendar.getTimeInMillis() - startTimeCalendar.getTimeInMillis());
	    
	    offSetHours =	offsetTimeCalendar.get(Calendar.HOUR_OF_DAY);
	    offSetMinutes = offsetTimeCalendar.get(Calendar.MINUTE);
	    offSetSeconds = offsetTimeCalendar.get(Calendar.SECOND);
	    
	    nowTimeCalendar =	Calendar.getInstance(Locale.ROOT);
	    elapsedTimeCalendar =   Calendar.getInstance(Locale.ROOT);
	    remainingTimeCalendar = Calendar.getInstance(Locale.ROOT);
	    totalTimeCalendar =	Calendar.getInstance(Locale.ROOT);
	    
	    if ((processRunningMode == ENCRYPT_MODE)  || (processRunningMode == DECRYPT_MODE)) { UPDATE_CLOCKS_TIMELINE.play(); }
	    
	    switch (processRunningMode)
	    {
	    	case ENCRYPT_MODE: userGuideMessage(ENCRYPTING_FILES, 64, false, false, false, false, VOI_ENCRYPTING_FILES, 0); break;
	    	case DECRYPT_MODE: userGuideMessage(DECRYPTING_FILES, 64, false, false, false, false, VOI_DECRYPTING_FILES, 0); break;
	    	case CREATE_KEYDEV_MODE: userGuideMessage(CREATE_KEYDEV, 64, false, false, false, false, VOI_CREATE_KEY_DEVICE, 0); break;
	    	case CLONE_KEYDEV_MODE: userGuideMessage(CLONE_KEYDEV, 64, false, false, false, false, VOI_CLONE_KEY_DEVICE, 0); break;
	    	default: break;
	    }

	    processRunning = true;
	    
	    disableFileChoosers(true);
	    
	    encryptButton.setDisable(true);
	    decryptButton.setDisable(true);
	    pwdField.setDisable(true);
//		pwdField.setVisible(false);
//		keyImageView.setOpacity(0.1);

pauseToggleButton.setDisable(false);
stopButton.setDisable(false);

keyButton.setDisable(true);

remainingTimeHeaderLabel.setVisible(true); remainingTimeLabel.setVisible(true);
elapsedTimeHeaderLabel.setVisible(true); elapsedTimeLabel.setVisible(true);
totalTimeHeaderLabel.setVisible(true); totalTimeLabel.setVisible(true);

filesProgressBar.setProgress(0.0); filesProgressBar.setVisible(true);
fileProgressBar.setProgress(0.0); fileProgressBar.setVisible(true);

targetFileChooser.rescanCurrentDirectory();
keyFileChooser.rescanCurrentDirectory();
	});
    }

    @Override public void fileProgress()
    {
//	updateDashboard(targetFCPathList);
    }
    
    private void updateClocks()
    {
	totalToProcessedRatio = (Long.valueOf(bytesTotal).doubleValue() / Long.valueOf(bytesProcessed).doubleValue());
        
	Platform.runLater(() ->
	{
	    nowTimeCalendar =	    Calendar.getInstance(Locale.ROOT);
	    elapsedTimeCalendar =   Calendar.getInstance(Locale.ROOT);
	    remainingTimeCalendar = Calendar.getInstance(Locale.ROOT);
	    totalTimeCalendar =	    Calendar.getInstance(Locale.ROOT);
	    
	    elapsedTimeCalendar.setTimeInMillis(nowTimeCalendar.getTimeInMillis() - startTimeCalendar.getTimeInMillis());
	    totalTimeCalendar.setTimeInMillis( Double.valueOf(Long.valueOf(elapsedTimeCalendar.getTimeInMillis()).doubleValue() * Double.valueOf(totalToProcessedRatio).doubleValue()).longValue());
	    remainingTimeCalendar.setTimeInMillis(totalTimeCalendar.getTimeInMillis() - elapsedTimeCalendar.getTimeInMillis());
	    
//	    test("\r\nbt: " + bytesTotal + " bp: " + bytesProcessed + " ratio: " + Double.valueOf(totalToProcessedRatio).toString() + " ");
//	    test("start: " + startTimeCalendar.getTimeInMillis() / 1000 + " now: " + nowTimeCalendar.getTimeInMillis() / 1000 + " elapsed: " + elapsedTimeCalendar.getTimeInMillis() / 1000 + "\r\n ");

	    String elapsedTimeString = "";
	    elapsedTimeString += String.format("%02d", elapsedTimeCalendar.get(Calendar.HOUR_OF_DAY) - offSetHours) + ":";
	    elapsedTimeString += String.format("%02d", elapsedTimeCalendar.get(Calendar.MINUTE) - offSetMinutes) + ":";
	    elapsedTimeString += String.format("%02d", elapsedTimeCalendar.get(Calendar.SECOND) - offSetSeconds);

	    String remainingTimeString = "";
	    remainingTimeString += String.format("%02d", remainingTimeCalendar.get(Calendar.HOUR_OF_DAY) - offSetHours) + ":";
	    remainingTimeString += String.format("%02d", remainingTimeCalendar.get(Calendar.MINUTE) - offSetMinutes) + ":";
	    remainingTimeString += String.format("%02d", remainingTimeCalendar.get(Calendar.SECOND) - offSetSeconds);

	    String totalTimeString = "";
	    totalTimeString += String.format("%02d", totalTimeCalendar.get(Calendar.HOUR_OF_DAY) - offSetHours) + ":";
	    totalTimeString += String.format("%02d", totalTimeCalendar.get(Calendar.MINUTE) - offSetMinutes) + ":"; 
	    totalTimeString += String.format("%02d", totalTimeCalendar.get(Calendar.SECOND) - offSetSeconds);
	    
	    elapsedTimeLabel.setText(elapsedTimeString);
	    
	    if (!pauseToggleButton.isSelected())
	    {
		remainingTimeLabel.setText(remainingTimeString);
		totalTimeLabel.setText(totalTimeString);	    
	    }
	    clockUpdated = true;
	});
    }

    @Override public void processProgress(int fileProgressPercent, int filesProgressPercent, long bytesTotalParam, long bytesProcessedParam, double bytesPerMiliSecondParam)
    {
	
        Platform.runLater(() ->
	{
	    // updateClocks() data
	    bytesTotal = bytesTotalParam;
//	    bytesProcessed = Double.valueOf(Long.valueOf(bytesProcessedParam).doubleValue() / 2d).longValue();
	    bytesProcessed = Double.valueOf(Long.valueOf(bytesProcessedParam).doubleValue()).longValue();
	    megaBytesPerSecond = bytesPerMiliSecondParam;
	    
	    // update ProgressBars
	    if (finalCrypt.getVerbose()) { log("Progress File : " + filesProgressPercent / 100.0  + " factor", false, false, false, false, true); }
	    if (finalCrypt.getVerbose()) { log("Progress Files: " + fileProgressPercent / 100.0 + " factor", false, false, false, false, true); }
	    fileProgressBar.setProgress((double)fileProgressPercent / 100.0); // percent needs to become factor in this gui
	    filesProgressBar.setProgress((double)filesProgressPercent / 100.0); // percent needs to become factor in this gui
	    updateDashboard(targetFCPathList);
	});
    }

    @Override public void processGraph(int value) { Platform.runLater(() ->
    {
    }); }
        
    @Override public void processFinished(FCPathList openFCPathList, boolean open)
    {
        Platform.runLater(() ->
	{
	    megaBytesPerSecond = 0;
	    updateSystemMonitor();

	    // Clocks	
//	    if ((processRunningMode == ENCRYPT_MODE)  || (processRunningMode == DECRYPT_MODE)) { UPDATE_CLOCKS_TIMELINE.stop(); }
	    UPDATE_CLOCKS_TIMELINE.stop();
	    if (clockUpdated)
	    {
		remainingTimeLabel.setText("00:00:00");
//		elapsedTimeLabel is already correct
		totalTimeCalendar.setTimeInMillis(elapsedTimeCalendar.getTimeInMillis());
		String totalTimeString = "";
		totalTimeString += String.format("%02d", totalTimeCalendar.get(Calendar.HOUR_OF_DAY) - offSetHours) + ":";
		totalTimeString += String.format("%02d", totalTimeCalendar.get(Calendar.MINUTE) - offSetMinutes) + ":";
		totalTimeString += String.format("%02d", totalTimeCalendar.get(Calendar.SECOND) - offSetSeconds);
		totalTimeLabel.setText(totalTimeString);
	    }
	    
	    targetFCPathList = new FCPathList();
	    
	    updateDashboard(targetFCPathList);
	    
	    disableFileChoosers(false);
	    
	    encryptButton.setDisable(true);
	    decryptButton.setDisable(true);
	    if ( keyFCPath.isValidKey )
	    {
		if (pwdField.getText().length() == 0) { passwordHeaderLabel.setText("Password (optional)"); } else { passwordHeaderLabel.setText("Password (set)"); }
		pwdField.setDisable(false); /*pwdField.setVisible(true);*/ keyImageView.setOpacity(0.8);
	    }
	    pauseToggleButton.setDisable(true);
	    stopButton.setDisable(true);
	    
	    keyButton.setDisable(false);
	    
	    fileProgressBar.setProgress(0); fileProgressBar.setVisible(false);
	    filesProgressBar.setProgress(0); filesProgressBar.setVisible(false);
	    
	    encryptionModeToggleButton.setMouseTransparent(!encryptionModeToggleButton.isMouseTransparent());
	    encryptionModeAnchorPane.setMouseTransparent(!encryptionModeAnchorPane.isMouseTransparent());
	    
	    updateFileChoosers(true, false);
	    
	    processRunningMode = NONE;
	    processRunning = false;
	    	    
//	    The Open selected file when finished section

	    Thread openThread;
	    openThread = new Thread(() ->
	    {
		try { Thread.sleep(1000); } catch (InterruptedException ex) {  }
		if (open)
		{
		    for (Iterator fcPathIterator = openFCPathList.iterator(); fcPathIterator.hasNext();)
		    {
			FCPath openFCPath = (FCPath) fcPathIterator.next();
			Path newPath = Paths.get(openFCPath.path.toString().substring(0, openFCPath.path.toString().lastIndexOf('.')));

			play = new AudioClip(this.SND_OPEN.getSource()); play.play();
			try { Desktop.getDesktop().open(newPath.toFile()); }
			catch (IOException ex) { log("Error: Desktop.getDesktop().open(" + newPath.toFile().getAbsolutePath().toString() + "); " + ex.getMessage() + "\r\n", true, true, true, true, false); }

			targetFCPathList = new FCPathList(); updateDashboard(targetFCPathList);
			Platform.runLater(new Runnable(){ @Override public void run() { encryptButton.setDisable(true); decryptButton.setDisable(true); keyButton.setDisable(false); keyButton.setText(CREATE_KEY); }});
		    }
		}
	    });
	    openThread.setName("openThread");
	    openThread.setDaemon(true);
	    openThread.start();
	});
    }    
    
//  ================================================= END UPDATE PROGRESS ===========================================================

    public void updateFileChoosers(boolean updateTargetFC, boolean updateKeyFC)
    {
	Platform.runLater(() ->
	{
	    curTargetDir = targetFileChooser.getCurrentDirectory(); // log("curTargetDir " + curTargetDir.getAbsolutePath() + "\r\n");
	    curKeyDir = keyFileChooser.getCurrentDirectory(); // log("curKeyDir " + curKeyDir.getAbsolutePath() + "\r\n");
	    upTargetDir = curTargetDir.getParentFile().getAbsoluteFile();
    	    upKeyDir = curKeyDir.getParentFile().getAbsoluteFile();

	    if (updateTargetFC) //	    Target FileChooser
	    {
		try { targetFileChooser.setCurrentDirectory(upTargetDir); }
		catch (java.lang.IndexOutOfBoundsException ex) {  }
		finally { targetFileChooser.setCurrentDirectory(upTargetDir); }

		try { targetFileChooser.setCurrentDirectory(curTargetDir); }
		catch (java.lang.IndexOutOfBoundsException ex) {  }
		finally { targetFileChooser.setCurrentDirectory(curTargetDir); }

		targetFileChooser.setFileFilter(targetFileChooser.getAcceptAllFileFilter()); // Prevents users to scare about disappearing files as they might forget the selected filefilter
		targetFileChooser.rescanCurrentDirectory(); targetFileChooser.validate();
		targetFileChooser.setVisible(false); targetFileChooser.setVisible(true);
		targetFileChooserPropertyCheck(false);
	    }

	    if (updateKeyFC) //	    Key FileChooser
	    {
//		keyFileChooser.setSelectedFile(noKeyFile);
		try { keyFileChooser.setCurrentDirectory(upKeyDir); }
		catch (java.lang.IndexOutOfBoundsException ex) {  }
		finally { keyFileChooser.setCurrentDirectory(upKeyDir); }

		try { keyFileChooser.setCurrentDirectory(curKeyDir); }
		catch (java.lang.IndexOutOfBoundsException ex) {  }
		finally { keyFileChooser.setCurrentDirectory(curKeyDir); }

		keyFileChooser.setFileFilter(keyFileChooser.getAcceptAllFileFilter()); // Prevents users to scare about disappearing files as they might forget the selected filefilter
		keyFileChooser.rescanCurrentDirectory(); keyFileChooser.validate();
		keyFileChooser.setVisible(false); keyFileChooser.setVisible(true); // Reldraw FileChoosers
		keyFileChooserPropertyCheck();
	    }

	    if (updateTargetFC) //	    Target FileChooser
	    {
		String platform = System.getProperty("os.name").toLowerCase(); // Due to a nasty JFileChooser focus issue on Mac
		if ( platform.indexOf("mac") != -1 )
		{
		    Timeline timeline = new Timeline(new KeyFrame( Duration.millis(100), ae ->
		    {
			targetFileSwingNode.setContent(targetFileChooser); // Delay setting this JFileChooser avoiding a simultanious key and target JFileChooser focus conflict causing focus to endlessly flipflop between the two JFileChoosers
		    }
		    )); timeline.play();
		}
	    }

	    if ( (curTargetDir.compareTo(curKeyDir) == 0) & (updateTargetFC) & ( ! updateKeyFC))// if filechoosers are in the same dir
	    {
		keyFileChooser.rescanCurrentDirectory(); keyFileChooser.validate();
		targetFileChooserPropertyCheck(false);
		keyFileChooser.rescanCurrentDirectory(); keyFileChooser.validate();
		keyFileChooserPropertyCheck();
	    }
	});
    }
    
    @FXML
    private void keyInfoLabelClicked(MouseEvent event)
    {
	play = new AudioClip(this.SND_MESSAGE.getSource()); play.play();
        Alert alert = new Alert(AlertType.INFORMATION);
        
//      Style the Alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        
        alert.setTitle("Information Dialog");
        alert.setHeaderText("What is your One Time Pad Key?");
        alert.setResizable(true);
        String infotext = new String();
        infotext =  "";
        infotext += "One Time Pad Keys are the most unbreakble keys.\r\n";
        infotext += "FinalCrypt en/decrypts files with OTP key files.\r\n";
        infotext += "If you don't have a key then create a key first.\r\n";
        infotext += "\r\n";
        infotext += "An optional password enforces extra encryption\r\n";
        infotext += "so a lost/stolen key file can't unlock your data.\r\n";
        infotext += "\r\n";
        infotext += "=================================================\r\n";
        infotext += "\r\n";
        infotext += "Keep keys secret and backed up on USB sticks!\r\n";
        infotext += "\r\n";
        infotext += "Without your key file (and optional password)\r\n";
        infotext += "there is no way to decrypt / recover your data\r\n";
//        infotext += "\r\n";
        alert.setContentText(infotext);
        alert.showAndWait();
	if (alert.getResult() == ButtonType.OK) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); }
    }

    @FXML
    private void targetInfoLabelClicked(MouseEvent event)
    {
	play = new AudioClip(this.SND_MESSAGE.getSource()); play.play();
        Alert alert = new Alert(AlertType.INFORMATION);

//      Style the Alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");

        alert.setTitle("Information Dialog");
        alert.setHeaderText("What about your selected items?");
        alert.setResizable(true);
        String infotext = new String();
        infotext  = "These are the files and maps you want to en / decrypt.\r\n";
        infotext += "Hold down [Ctrl] / [Shift] to select multiple items.\r\n";
        infotext += "\r\n";
        infotext += "Encrypted files get the \".bit\" extension added.\r\n";
        infotext += "Decrypted files get the \".bit\" extension removed.\r\n";
        infotext += "\r\n";
        infotext += "Original files are securely deleted after encryption.\r\n";
//        infotext += "\r\n";
        alert.setContentText(infotext);
        alert.showAndWait();
	if (alert.getResult() == ButtonType.OK) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); }
    }

    @FXML private void pauseToggleButtonAction(ActionEvent event)
    {
        if (processRunning)
        {
	    if ((processRunningMode == ENCRYPT_MODE) || (processRunningMode == DECRYPT_MODE)) { finalCrypt.setPausing(pauseToggleButton.isSelected()); } else { DeviceController.setPausing(pauseToggleButton.isSelected()); }
	    if ( pauseToggleButton.isSelected() )
	    {
		play = new AudioClip(this.SND_ON.getSource()); play.play();
		pauseToggleButton.setStyle(" -fx-text-fill: orange; -fx-font-size: 14; ");
		PAUSE_TIMELINE.play();
		megaBytesPerSecond = 0d;
	    }
	    else
	    {
		play = new AudioClip(this.SND_OFF.getSource()); play.play();
		PAUSE_TIMELINE.stop();
		pauseToggleButton.setText(FinalCrypt.UTF8_PAUSE_SYMBOL);
		pauseToggleButton.setStyle(" -fx-text-fill: white; -fx-font-size: 14; ");
	    }
        }
    }
    
    @FXML
    private void stopButtonAction(ActionEvent event) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); stop(false); }
    
    private void stop(boolean stopAndExit)
    {
        if ((processRunning) && ((processRunningMode == ENCRYPT_MODE) || (processRunningMode == DECRYPT_MODE)))
        {
            finalCrypt.setStopPending(true);
            if (pauseToggleButton.isSelected()) { pauseToggleButton.fire(); }
        }
        else
        {
            DeviceController.setStopPending(true);
            if (pauseToggleButton.isSelected()) { pauseToggleButton.fire(); }
        }
    }

    @FXML
    private void copyrightLabelOnMouseClicked(MouseEvent event)	{ /*checkUpdate();*/ }

    @FXML
    private void checkUpdateButtonOnAction(ActionEvent event) { Platform.runLater(() -> { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); checkUpdate( true ); }); }

    @FXML
    private void encryptTabSelectionChanged(Event event)
    {
        String platform = System.getProperty("os.name").toLowerCase(); // Due to a nasty JFileChooser focus issue on Mac
        if ( platform.indexOf("mac") != -1 ) 
	{
	    Timeline timeline = new Timeline(new KeyFrame( Duration.millis(100), ae -> 
	    {
		targetFileSwingNode.setContent(targetFileChooser); // Delay setting this JFileChooser avoiding a simultanious key and target JFileChooser focus conflict causing focus to endlessly flipflop between the two JFileChoosers
		targetFileChooser.setVisible(false); targetFileChooser.setVisible(true); keyFileChooser.setVisible(false); keyFileChooser.setVisible(true); // Reldraw FileChoosers
	    }
	    )); timeline.play();
	}
	else
	{
	    Platform.runLater(() ->
	    {
		targetFileChooser.setVisible(false); targetFileChooser.setVisible(true); keyFileChooser.setVisible(false); keyFileChooser.setVisible(true); // Reldraw FileChoosers
	    });
	}
    }

    @FXML
    private void logTabSelectionChanged(Event event)
    {
	play = new AudioClip(this.SND_BUTTON.getSource()); play.play();
    }

//  Headers
//  ==================================================================================================================================================================

    private void emptyFilesHeaderLabelOnMouseClicked(MouseEvent event)
    {
	if ( (emptyList != null) && (emptyList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("Empty Files:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = emptyList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, false, false, false); }
    }

    private void symlinkFilesHeaderLabelOnMouseClicked(MouseEvent event)
    {
	if ( (symlinkList != null) && (symlinkList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("Symlinks:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = symlinkList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, false, false, false); }
    }

    @FXML
    private void unreadableFilesHeaderLabelOnMouseClicked(MouseEvent event)
    {
	if ( (unreadableList != null) && (unreadableList.size() > 0) )
	{
	    /*tab.getSelectionModel().select(1);*/ play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); log("Set Read Attributes:\r\n\r\n", false, true, true, false, false);
	    for (Iterator it = unreadableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); setAttribute(fcPath, true, false); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, false, false, false);
	    targetFCPathList = new FCPathList(); updateDashboard(targetFCPathList);
	    Platform.runLater(() -> { encryptButton.setDisable(true); decryptButton.setDisable(true); keyButton.setDisable(false); keyButton.setText(CREATE_KEY); });
	    targetFileChooser.setFileFilter(this.nonFinalCryptFilter); targetFileChooser.setFileFilter(targetFileChooser.getAcceptAllFileFilter()); // Resets rename due to doucle click file
	}
    }

    @FXML
    private void unwritableFilesHeaderLabelOnMouseClicked(MouseEvent event)
    {
	if ( (unwritableList != null) && (unwritableList.size() > 0) )
	{
	    /*tab.getSelectionModel().select(1);*/ play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); log("Set Write Attributes:\r\n\r\n", false, true, true, false, false);
	    for (Iterator it = unwritableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); setAttribute(fcPath, true, true); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false);
	    targetFCPathList = new FCPathList(); updateDashboard(targetFCPathList);
	    Platform.runLater(() -> { encryptButton.setDisable(true); decryptButton.setDisable(true); keyButton.setDisable(false); keyButton.setText(CREATE_KEY); });
	    targetFileChooser.setFileFilter(this.nonFinalCryptFilter); targetFileChooser.setFileFilter(targetFileChooser.getAcceptAllFileFilter()); // Resets rename due to doucle click file
	}
    }

    private void hiddenFilesHeaderLabelOnMouseClicked(MouseEvent event)
    {
	if ( (hiddenList != null) && (hiddenList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("\r\nHidden Files:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = hiddenList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false); }
    }

    @FXML
    private void emptyFilesLabelOnMouseClicked(MouseEvent event)
    {
	if ( (emptyList != null) && (emptyList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("\r\nEmpty Files:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = emptyList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false); }
    }

    @FXML
    private void symlinkFilesLabelOnMouseClicked(MouseEvent event)
    {
	if ( (symlinkList != null) && (symlinkList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("\r\nSymlinks:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = symlinkList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false); }
    }

    @FXML
    private void unreadableFilesLabelOnMouseClicked(MouseEvent event)
    {
	if ( (unreadableList != null) && (unreadableList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("\r\nUnreadable Files:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = unreadableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false); }
    }

    @FXML
    private void unwritableFilesLabelOnMouseClicked(MouseEvent event)
    {
	if ( (unwritableList != null) && (unwritableList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("Unwritable Files:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = unwritableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false); }
    }

    @FXML
    private void hiddenFilesLabelOnMouseClicked(MouseEvent event)
    {
	if ( (hiddenList != null) && (hiddenList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("Hidden Files:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = hiddenList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false); }
    }
    
    private void setAttribute(FCPath fcPath, boolean read, boolean write)
    {
	attributeViewloop: for (String view:fcPath.path.getFileSystem().supportedFileAttributeViews()) // acl basic owner user dos
	{
//                            ui.println(view);
//	    if ( view.toLowerCase().equals("basic") )
//	    {
//	    }
	    if ( view.toLowerCase().equals("dos") )
	    {
		try
		{
		    if (read) { Files.setAttribute(fcPath.path, "dos:readonly", true); }
		    if (write) { Files.setAttribute(fcPath.path, "dos:readonly", false); }
		}
		catch (IOException ex) { log("Error: Set DOS Attributes: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    }
	    else if ( view.toLowerCase().equals("posix") )
	    {
		try
		{
		    Set<PosixFilePermission> permissions = new HashSet<>();
		    if (read) { permissions.add(PosixFilePermission.OWNER_READ); }
		    if (write) { permissions.add(PosixFilePermission.OWNER_READ); permissions.add(PosixFilePermission.OWNER_WRITE); }
		    Files.setPosixFilePermissions(fcPath.path, permissions);
		}
		catch (IOException ex) { log("Error: Set POSIX Attributes: " + ex.getMessage() + "\r\n", true, true, true, true, false); }
	    }
	} // End attributeViewloop // End attributeViewloop
    }

    @FXML
    private void encryptableLabelOnMouseClicked(MouseEvent event)
    {
	if ( (encryptableList != null) && (encryptableList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("\r\nEncryptable Files:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = encryptableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false); }
    }

    @FXML
    private void decryptableLabelOnMouseClicked(MouseEvent event)
    {
	if ( (decryptableList != null) && (decryptableList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("\r\nDecryptable Files:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = decryptableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false); }
    }

    @FXML
    private void decryptedLabelOnMouseClicked(MouseEvent event)
    {
	if ( (decryptedList != null) && (decryptedList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("\r\nDecrypted Files:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = decryptedList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false); }
    }

    @FXML
    private void encryptedLabelOnMouseClicked(MouseEvent event)
    {
	if ( (encryptedList != null) && (encryptedList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("\r\nEncrypted Files:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = encryptedList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false); }
    }

    private void newEncryptedLabelOnMouseClicked(MouseEvent event)
    {
	if ( (newEncryptedList != null) && (newEncryptedList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("\r\nNew Encrypted Files:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = newEncryptedList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false); }
    }


    @FXML
    private void unencryptableLabelOnMouseClicked(MouseEvent event)
    {
	if ( (unencryptableList != null) && (unencryptableList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("\r\nUnencryptable Files:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = unencryptableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false); }
    }

    private void newDecryptedLabelOnMouseClicked(MouseEvent event)
    {
	if ( (newDecryptedList != null) && (newDecryptedList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("\r\nNew Decrypted Files:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = newDecryptedList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false); }
    }


    @FXML
    private void undecryptableLabelOnMouseClicked(MouseEvent event)
    {
	if ( (undecryptableList != null) && (undecryptableList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("\r\nUndecryptable Files:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = undecryptableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false); }
    }

    @FXML
    private void invalidFilesLabelOnMouseClicked(MouseEvent event)
    {
	if ( (invalidFilesList != null) && (invalidFilesList.size() > 0) ) { play = new AudioClip(this.SND_BUTTON.getSource()); play.play(); tab.getSelectionModel().select(1); log("\r\nInvalid Files:\r\n\r\n", false, true, true, false, false);
	for (Iterator it = invalidFilesList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false); }
    }

    @FXML
    private void openWebsiteAction(ActionEvent event)
    {
	play = new AudioClip(this.SND_OPEN.getSource()); play.play();
	Version.openWebSite(this);
    }    




//  ==============================================================================================================
//  Begin Message Authentication Mode
//  ==============================================================================================================


    private void disarmDisableMACMode()
    {
	play = new AudioClip(this.SND_OFF.getSource()); play.play();
	encryptionModeToggleButton.setDisable(true);
	encryptionModeToggleButton.setSelected(false);
	encryptionModeToggleButton.setText(MAC_ON);
	encryptionModeToggleButton.setTextFill(Paint.valueOf("grey"));
	encryptionModeToggleButton.setMouseTransparent(false);
    }
    
//  Enable / Arm Disable-MAC-Mode-Button
    private void armDisableMACMode()
    {
	play = new AudioClip(this.SND_BUTTON.getSource()); play.play();
	encryptionModeToggleButton.setDisable(false);
	encryptionModeToggleButton.setSelected(false);
	encryptionModeToggleButton.setText(MAC_OFF_Q);
	encryptionModeToggleButton.setTextFill(Paint.valueOf("grey"));
	encryptionModeToggleButton.setMouseTransparent(false);
	encryptionModeToggleButton.getTooltip().setText("Click to disable MAC Mode! (files will be encrypted without Message Authentication Code Header)");

//	Auto disable arming disable MAC Mode
	AUTO_DISABLE_ARMING_MAC_MODE_TIMELINE.play();
    }
    
//  Default MAC Mode
    private void enableMACMode() // Safe Mode
    {
	play = new AudioClip(this.SND_SELECT.getSource()); play.play();
	if ( FLASH_MAC_MODE_TIMELINE != null ) { FLASH_MAC_MODE_TIMELINE.stop(); }
	encryptionModeToggleButton.setText(MAC_ON);
	encryptionModeToggleButton.setTextFill(Paint.valueOf("white"));

	updateFileChoosers(true, true);
	FCPath.KEY_SIZE_MIN = FCPath.KEY_SIZE_MIN_DEFAULT;
	finalCrypt.disabledMAC = false;
	dashboardGridPane.setDisable(false);
	encryptionModeToggleButton.setDisable(true);
	encryptionModeToggleButton.setMouseTransparent(true);
	long now = Calendar.getInstance().getTimeInMillis(); lastRawModeClicked = now; // Anti DoubleClick missery
	log("Message Authentication Mode Enabled\r\n", true, true, true, false, false);
    }

    private void disableMACMode() // Dangerous Mode
    {
	play = new AudioClip(this.SND_ALARM.getSource()); play.play();
	if ( AUTO_DISABLE_ARMING_MAC_MODE_TIMELINE != null ) { AUTO_DISABLE_ARMING_MAC_MODE_TIMELINE.stop(); }

	encryptionModeToggleButton.setText(MAC_OFF);
	encryptionModeToggleButton.setTextFill(Paint.valueOf("white"));
	encryptionModeToggleButton.getTooltip().setText("Click to enable Message Authentication Mode");

	updateFileChoosers(true, true);
	FCPath.KEY_SIZE_MIN = 1;
	finalCrypt.disabledMAC = true;
	dashboardGridPane.setDisable(true);
	log("Warning: MAC Mode Disabled! (files will be encrypted without Message Authentication Code Header)\r\n", true, true, true, false, false);

	FLASH_MAC_MODE_TIMELINE.play();
    }
        
    @FXML
    private void encryptionModeToggleButtonOnMouseClicked(MouseEvent event)
    {
	if ( ! processRunning )
	{
	    Platform.runLater(() ->
	    {
		if (! encryptionModeToggleButton.isSelected())	{ enableMACMode(); }
		else						{ disableMACMode(); }
	    });
	}
    }

    @FXML
    private void encryptionModeAnchorPaneOnMouseClicked(MouseEvent event)
    {
	long now = Calendar.getInstance().getTimeInMillis();
	if ( ! processRunning)
	{
	    play = new AudioClip(this.SND_BUTTON.getSource()); play.play();
	    if ( now - lastRawModeClicked > 1000) // Anti DoubleClick missery
	    {
		Platform.runLater(() ->
		{
		    if (encryptionModeToggleButton.isDisabled())
		    {
			if(event.getButton().equals(MouseButton.PRIMARY))
			{
			    if(event.getClickCount() == 2)
			    {
				armDisableMACMode();
			    }
			}	
		    }
		});
	    }	
	}
    }


//  ==============================================================================================================
//  End Message Authentication Mode
//  ==============================================================================================================

    @Override public void test(String message) { log(message, true, true, false, false, false); }

    @Override
    synchronized public void log(String message, boolean status, boolean log, boolean logfile, boolean errfile, boolean print)
    {
	if (status)	{ status(message); }
	if (log)	{ log(message); }
	if (logfile)	{ logfile(message); }
	if (errfile)	{ errfile(message); }
	if (print)	{ print(message,errfile); }
    }

    public void status(String message)		    { Platform.runLater(() -> { statusLabel.setText(message.replace("\r\n", "")); }); }
    public void log(String message)		    { Platform.runLater(() -> { lineCounter++;  logTextArea.appendText(message); if (lineCounter > 1000) { logTextArea.setText(message); lineCounter = 0; } }); }
    public void logfile(String message)		    { Platform.runLater(() -> { try { Files.write(configuration.getLogFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC); } catch (IOException ex) { log("Files.write(" + configuration.getLogFilePath() + ")..));", true, true, false, false, false); } }); }
    public void errfile(String message)		    { Platform.runLater(() -> { play = new AudioClip(this.SND_ERROR.getSource()); play.play(); try { Files.write(configuration.getErrFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC); } catch (IOException ex) { log("Files.write(" + configuration.getErrFilePath() + ")..));", true, true, false, false, false); } }); }
    public void print(String message,boolean err)   { if ( ! err ) { System.out.print(message); } else { System.err.print(message); } }
    
    public static void main(String[] args)  { launch(args); }

    @FXML
    private void pwdFieldOnKeyReleased(KeyEvent event)
    {
//	log("Pass: " + pwdField.getText() + " length: " + pwdField.getText().length() + event.getCode(), true, true, true, false, false);
	if (event.getCode() == KeyCode.ENTER)
	{
	    play = new AudioClip(this.SND_READY.getSource()); play.play();
	    passwordHeaderLabel.setText("Password (set)");
	    settingPassword = false;
	    targetFileChooserPropertyCheck(true);
	}
	else
	{
	    
	    finalCrypt.setPwd(pwdField.getText());
	    finalCrypt.resetPwdPos();
	    passwordHeaderLabel.setText(PASSWORD_ENTER);
	    if (!settingPassword) { userGuideMessage(PASSWORD_ENTER, 48, false, false, true, false, VOI_CONFIRM_PASS_WITH_ENTER, 0); }
	    settingPassword = true;
	    targetFCPathList = new FCPathList();
	    buildReady(targetFCPathList, false);
	}
    }
    
    @FXML  private void pwdFieldOnMouseClicked(MouseEvent event) { play = new AudioClip(this.SND_SELECT.getSource()); play.play(); }
}
