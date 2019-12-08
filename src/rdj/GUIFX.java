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
import java.beans.*;
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
import javafx.stage.Stage;
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
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
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
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.swing.JFileChooser;
import javax.swing.JToggleButton;
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
import java.util.prefs.*;
import java.util.stream.Collectors;
import javafx.collections.*;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.swing.*;
import javax.swing.filechooser.*;

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
    @FXML   private SwingNode targetFileSwingNode;
    @FXML   private Button decryptButton;
    @FXML   private Label keyNameLabel;
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
    private Label validDevicesLabel;
    private Label validDevicesSizeLabel;
    private Label validPartitionsLabel;
    private Label validPartitionsSizeLabel;
    @FXML   private Label invalidFilesSizeLabel;
    @FXML   private Label filesSizeLabel;
    @FXML   private Label emptyFilesLabel;
    @FXML   private Label symlinkFilesLabel;
    @FXML   private Label unreadableFilesLabel;
    @FXML   private Label unwritableFilesLabel;
    @FXML   private Label hiddenFilesLabel;
    private Label targetWarningLabel;
    @FXML   private Label totalTimeLabel;
    @FXML   private Label remainingTimeLabel;
    @FXML   private Label elapsedTimeLabel;
    @FXML   private Label emptyFilesHeaderLabel;
    @FXML   private Label symlinkFilesHeaderLabel;
    @FXML   private Label unreadableFilesHeaderLabel;
    @FXML   private Label unwritableFilesHeaderLabel;
    @FXML   private Label hiddenFilesHeaderLabel;
    @FXML   private Button supportButton;
    @FXML   private Label checksumLabel;
    @FXML   private GridPane dashboardGridPane;
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
    @FXML   private Label targetLabel1;
    @FXML   private Label keyLabel1;
    @FXML   private Canvas sysMonCanvas;
//    @FXML   private Tooltip sysMonTooltip;
    @FXML   private AnchorPane keyFileSwingPane;
    @FXML   private Label sysMonLabel;
    @FXML   private TextField pwdtxtField;
    @FXML   private CheckBox showPasswordCheckBox;
    @FXML   private Label keyHeaderLabel;
    @FXML   private Label keySizeHeaderLabel;
    @FXML   private Tooltip keyNameLabelTooltip;
    @FXML   private Label keyWriteLabel;
    @FXML   private Label keyWriteSizeLabel;
    @FXML   private Label keyReadLabel;
    @FXML   private Label keyReadSizeLabel;
    @FXML   private Button tgtFileDeleteButton2; // Because Apple hides the FCChooserDelete Button
    @FXML   private Button keyFileDeleteButton2; // Because Apple hides the FCChooserDelete Button
    @FXML   private Tooltip pwdtxtFieldTooltip;
    @FXML   private Label keyLabel;
    @FXML   private Label languageLabel;
    @FXML   private Tooltip sysMonTooltip1;
    @FXML   private Pane userGuidanceFadePane;
    @FXML   private Tooltip tgtFileDeleteButton2ToolTip;
    @FXML   private Label targetLabel2;
    @FXML   private Label targetInfoLabel;
    @FXML   private Label keyInfoLabel;
    @FXML   private Tooltip keyFileDeleteButton2ToolTip;
    @FXML   private Tooltip unreadableFilesHeaderLabelToolTip;
    @FXML   private Tooltip unwritableFilesHeaderLabelToolTip;
    @FXML   private Tooltip emptyFilesLabelToolTip;
    @FXML   private Tooltip symlinkFilesLabelToolTip;
    @FXML   private Tooltip unreadableFilesLabelToolTip;
    @FXML   private Tooltip unwritableFilesLabelToolTip;
    @FXML   private Tooltip hiddenFilesLabelToolTip;
    @FXML   private Tooltip userGuidanceLabelToolTip;
    @FXML   private Tooltip sysMonTooltip;
    @FXML   private Label keyMissingLabel;
    @FXML   private Tooltip keyMissingLabelToolTip;
    @FXML   private Label keyMissingSizeLabel;
    @FXML   private Label targetWarningNameLabel;
    @FXML   private Label targetUnencryptableNameLabel;
    @FXML   private Label targetEncryptedNameLabel;
    @FXML   private Label targetDecryptableNameLabel;
    @FXML   private Label invalidFilesNameLabel;
    @FXML   private Label totalFilesNameLabel;
    @FXML   private Label targetEncryptableNameLabel;
    @FXML   private Label targetUnencryptedNameLabel;
    @FXML   private Label keyMatchNameLabel;
    @FXML   private Label targetUndecryptableNameLabel;
    @FXML   private Label keyWriteNameLabel;
    @FXML   private Label keyMissingNameLabel;
    @FXML   private Tooltip pwdFieldToolTip;
    @FXML   private Tooltip showPasswordCheckBoxToolTip;
    @FXML   private Tooltip keyReadLabelToolTip;
    @FXML   private Tooltip keyWriteLabelToolTip;
    @FXML   private ListView<String> selectLanguage;
//    @FXML   private ToggleButton encryptionModeToggleButton;
//    @FXML   private Tooltip encryptionModeToolTip;
//    @FXML   private AnchorPane encryptionModeAnchorPane;
//    @FXML   private Button keyButton;

    private Label validFilesLabel;
    private Label validFilesSizeLabel;
    private final int FILE_CHOOSER_FONT_SIZE =		13;
    private final int DELETE_CHOOSER_FONT_SIZE =	11;
    private final Font FILE_CHOOSER_FONT =		new Font("Dialog",Font.PLAIN,FILE_CHOOSER_FONT_SIZE);
    private final Font DELETE_CHOOSER_FONT =		new Font("Liberation Sans",Font.PLAIN,DELETE_CHOOSER_FONT_SIZE);
    
    private final int NONE =				0;
    private final int ENCRYPT_MODE =			1;
    private final int DECRYPT_MODE =			2;
    private final int CREATE_KEYDEV_MODE =		3;
    private final int CLONE_KEYDEV_MODE	=		4;
    private int processRunningMode =			NONE;

    private String welcome_to =				"Welcome to";
    private String introFadeInLine1 =			"Onbreakable";
    private String introFadeInLine2 =			"One Time Pad";

    private String mac_on =				"MAC ON";
    private String mac_off =				"MAC OFF";
    private String mac_off_q =				"MAC OFF ?";

    public String create_key =				"Create Key";
    private String create_keydev =			"Create Key Device";
    private String clone_keydev =			"Clone Key Device";

    public String select_key_dir =			"Select Key Directory";
    private String password_enter =			"Password<Enter>";
    private String password_optional =			"Password (optional)";
    private String password_set =			"Password (set)";
    private String select_files =			"Select Files";

    private String scanning_files =			"Scanning Files";
    private String wrong_key_pass =			"Wrong Key / Pass ?";

    private String encrypt_files =			"Encrypt Files";
    private String decrypt_files =			"Decrypt Files";
    private String en_decrypt_files =			"Encrypt • Decrypt Files";

    private String encrypting_files =			"Encrypting Files";
    private String decrypting_files =			"Decrypting Files";

    private String finished_encrypting =		"Finished Encrypting";
    private String finished_decrypting =		"Finished Decrypting";
    private String createOTPKeyTitle =			"Confirmation";
    private String createOTPKeyHeader =			"Create a Manual One-Time Pad Key?";
    private String createOTPKeyText =			"FinalCrypt automatically creates One-Time Pad Key Files\r\nCreating Manual OTP keys is supported but not recommended\\r\\n\\r\\nDo you still want to create a Manual One-Time Pad Key File?\\r\\n";

    private final String USER_GUID_TEXT_FILL_BASE =	"#706050";// #5A2D0C #663B1B
    private final String USER_GUID_TEXT_FILL_HIGH =	"#BBBBBB";

    private final int MAIN_TIMELINE_INTERVAL_PERIOD =	50;
    private final double LOAD_MONITOR_MS_INTERVAL =	250d;
    private final double LOAD_MANAGER_MS_INTERVAL =	250d;
    private final double ARROWS_OPACITY_MAX =		0.9d;
    private final double LOADHIGH_THRESHOLD =		0.90d; // 0.0 - 1.0
    private final double LOAD_HIGH_MS_TIMEOUT =		1000.0d;
    private final double LOAD_LOW_MS_TIMEOUT =		5000.0d;
    private boolean	 animation_Is_Enabled =		true;
    
    private final String OS_NAME =			System.getProperty("os.name");
    private final String OS_ARCH =			System.getProperty("os.arch");
    private final String OS_VERSION =			System.getProperty("os.version");
    private final String FILE_ENCODING =		System.getProperty("file.encoding");
    
    private final String JAVA_VENDER =			System.getProperty("java.vendor");
    private final String JAVA_VERSION =			System.getProperty("java.version");
    private final String CLASS_VERSION =		System.getProperty("java.class.version");
    
    private final Image KEY_MAP_IMAGE =			new Image(getClass().getResourceAsStream("/rdj/images/keymap.png"));
    private final Image KEY_FILE_IMAGE =		new Image(getClass().getResourceAsStream("/rdj/images/key.png"));
    
    private final String ANIMATED_SYMBOL =		"@";
    private final String SOUND_ON_SYMBOL =		"S";
    private final String SOUND_OFF_SYMBOL =		"s";
    private final String CPU_SYMBOL =			"¹";
    private final String RAM_SYMBOL =			"²";
    private final String STORAGE_SYMBOL =		"³";
    private final String VOICE_ON_SYMBOL =		"V";
    private final String VOICE_OFF_SYMBOL =		"v";
    
    
    private double load_High_MS_Passed =		0.0d;
    private double load_Low_MS_Passed =			0.0d;
    private double arrowsfadestep =			(ARROWS_OPACITY_MAX / (Double.valueOf(1000.0 / MAIN_TIMELINE_INTERVAL_PERIOD).intValue()) * 2); // step = max / FPS
    private double arrowsfadevar =			0.0d;
    private double load_Monitor_MS_Passed =		0.0d;
    private double load_Manager_MS_Passed =		0.0d;

    private final Timeline UPDATE_CLOCKS_TIMELINE =	new Timeline ( new KeyFrame( Duration.seconds(1), ae -> updateClocks()) );			

//    private final Timeline FLASH_MAC_MODE_TIMELINE =	new Timeline
//    (
//	 new KeyFrame(Duration.seconds( 0.0 ), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("WARNING");} )
//	,new KeyFrame(Duration.seconds( 0.25), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("");} )
//	,new KeyFrame(Duration.seconds( 0.50), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("WARNING");} )
//	,new KeyFrame(Duration.seconds( 0.75), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("");} )
//	,new KeyFrame(Duration.seconds( 1.0 ), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("WARNING");} )
//	,new KeyFrame(Duration.seconds( 1.25), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("");} )
//	,new KeyFrame(Duration.seconds( 1.50), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("white")); encryptionModeToggleButton.setText(MAC_OFF);} )
//	,new KeyFrame(Duration.seconds( 2.50), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("white"));   encryptionModeToggleButton.setText("");} )
//	,new KeyFrame(Duration.seconds( 2.75), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("white")); encryptionModeToggleButton.setText("");} )
//    );
    
    private final Timeline PAUSE_TIMELINE = new Timeline(new KeyFrame( Duration.millis(250), ae -> 
    {
	if ( pauseToggleButton.getText().length() == 0 ) { pauseToggleButton.setText(FinalCrypt.UTF8_PAUSE_DESC); } else { pauseToggleButton.setText(""); }
    }));

//    private final Timeline AUTO_DISABLE_ARMING_MAC_MODE_TIMELINE = new Timeline( new KeyFrame(Duration.seconds( 8.0), evt -> { disarmDisableMACMode(); } ) );

    private double focusAngle = 0.0;
    private double focusDistance = 0.0;
    private double radius = 0.6;
    private boolean proportional = true;
    private double endX = 1.55;
    private double startX = endX; // -0.5
    private double centerX = endX; // = endX
    private double centerY = 0.55;
    private double variable = startX; // = startX
    private double stepX = 0.02;
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
    
    private JFileChooser tgtFileChooser;
    private boolean negatePattern;
    public  JFileChooser keyFileChooser;
//    private JButton tgtFileDeleteButton;
//    private JButton keyFileDeleteButton;
    private Version version;

    private boolean processRunning;
    private MBeanServer mBeanServer;
    private ObjectName attributeObjectName;
    private AttributeList attribList;
    private Attribute att;
    private Double value;
    private FileFilter nonFinalCryptFilter;
    private FileNameExtensionFilter finalCryptFilter;
    private int lineCounter = 0;
    private Configuration configuration;
    private FCPath keyFCPath;
    private boolean symlink = false;
    private final String procCPULoadAttribute = "ProcessCpuLoad";
    
    private FCPathList<FCPath> targetFCPathList; // Main List

    // Filtered Lists
    private FCPathList<FCPath> decryptedList; 
    private FCPathList<FCPath> encryptableList;
    private FCPathList<FCPath> writeAutoKeyList;
    private FCPathList<FCPath> readAutoKeyList;
    private FCPathList<FCPath> missingAutoKeyList;

    private FCPathList<FCPath> encryptedList; 
    private FCPathList<FCPath> decryptableList;
    
    private FCPathList<FCPath> emptyList; 
    private FCPathList<FCPath> symlinkList;
    private FCPathList<FCPath> unreadableList;
    private FCPathList<FCPath> unwritableList;
    private FCPathList<FCPath> hiddenList;

    private FCPathList<FCPath> newEncryptedList;
    private FCPathList<FCPath> unencryptableList;
    private FCPathList<FCPath> newDecryptedList;
    private FCPathList<FCPath> undecryptableList;
    private FCPathList<FCPath> invalidFilesList;
    private FCPathList<FCPath> createManualKeyList;
    private FCPathList<FCPath> cloneManualKeyList;

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
    private Stage supportStage;
    private CreateOTPKey createOTPKey;
    private Support support;
    private final Preferences prefs = Preferences.userRoot().node(Version.getProductName());
    private long now;
    private boolean isCalculatingCheckSum;
    private long lastRawModeClicked;

    private boolean settingPassword;

//    private double fontsizefactor;
    private String fadeInMessage;
    private boolean bottomleftLabelEnabled;
    private boolean topleftLabelEnabled;
    private boolean toprightLabelEnabled;
    private boolean bottomrightLabelEnabled;
    
    private TimerTask updateDashboardTask;

    private Timeline mainTimeline;
    private double userLoadPerc;
    private GraphicsContext sysmon;
    private int sysmonOffSetX;
//    private File curTgtDir;
//    private File curKeyDir;
//    private File upTgtDir;
//    private File upKeyDir;
    private boolean update_System_Monitor_Enabled;
    private double userloadPercTest;
    private double userMemPercTest;
    private double throughputPercTest;
    private Scene scene;
    
    private PropertyChangeListener[] keyListener;
    private JToggleButton tgtDetailViewButton;
    private JToggleButton keyDetailViewButton;
    private String pwd; // Do not delete even if unused!
    private JComboBox keyPathSelector;
    private int keyPathSelectorPrefered;
    private File keyPathFile;
    public Locale selectedLocale;
    
    private ResourceBundle bundle;
    
    private String cpu_workload = "CPU Workload";
    private String storage_io_throughput = "Storage I/O Throughput";
    private String language_is = "Language is";
    private String click_to_change = "Click to change";
    private String sound_is = "Sound is";
    private String voice_is = "Voice is";
    private String animation_is = "Animation is";
    private String click = "Click";
    private String enable = "Enable";
    private String enabled = "Enabled";
    private String to_enable = "to Enable";
    private String disable = "Disable";
    private String disabled = "Disable";
    private String to_disable = "to Disable";
    private String click_display_to_enable = "Click display to Enable";
    private String click_display_to_disable = "Click display to Disable";
    private String ram_mem_used = "RAM Used";
    private String information = "Information";
    private String confirmation = "Confirmation";

    private String available = "available";
    private String versionUp2DateHeader="Your current FinalCrypt version is up to date";
    private String versionUp2DateContent="You have the latest version";        
    private String newVersionOf = "New version of";
    private String canbeupdatedtoversion = "can be updated to version";
    private String wouldYouLikeToDownload = "Would you like to download";

    private String you_are_using_a_development_version = "You are using a development version";
    private String this_is_a_development_version = "This is a development version";
    private String would_you_like_to_download = "Would you like to download";
	
    private String error = "Error";
    private String online_version_could_not_be_checked = "Online version could not be checked";
    private String network_connection_issues_perhaps = "Network connection issues perhaps ?\r\nPlease check your log for more info";

    private String selected = "selected";
    private String item = "item";
    private String items = "item";
    private String this_also_deletes_matching_keys = "this also deletes matching Keys";
    private String confirm_deletion = "Confirm Deletion";

    private String key = "Sleutel";
    private String directory = "Directory";
    private String file = "File";
    
    private String checksum = "Checksum";
    private String calculating = "Calculating";
    private String click_for_checksum = "Click for checksum";
    private String calculate_checksum_tooltip = "calculate checksum: left-click\r\ncopy to clipboard:  right-click";

    private String skipping = "Skipping";
    private String bytes = "Bytes";
    private String click_to_list_unreadable_files = "Click to list unreadable files";
    private String click_to_list_unwritable_files = "Click to list unwritable files";
    private String click_to_list_hidden_files = "Click to list hidden files";

    private String keyInfoHeader = "What is your Auto Key Directory?";
    private String keyInfoContent = "Your Key Directory holds your One Time Pad Keys\r\nwhich are automatically created and selected\r\nSelect a Key Directory on an external (USB) drive\r\n\r\nAn optional password enforces extra encryption\r\nso lost / stolen key files can't unlock your data\r\n\r\n=================================================\r\n\r\nBackup your encrypted files AND keys together as pairs\r\nKeep your backups offline on external (USB) drives too\r\n\r\nPlease keep in mind that without your key files and\r\noptional password decrypting your data is impossible!\r\n";
    private String targetInfoHeader = "What about your selected items?";
    private String targetInfoContent  = "These are the files and maps you want to en / decrypt.\r\nHold down [Ctrl] / [Shift] to select multiple items.\r\n\r\nEncrypted files get the \".bit\" extension added.\r\nDecrypted files get the \".bit\" extension removed.\r\n\r\nOriginal files are securely deleted after encryption.\r\n";

    
    private String prevsval1;
    private String prevsval2;
    private ObservableList<String> languageList;

    private Message ugMessage;
    private String selectedLanguageCode = "en";
    
    
    
    private void switchLanguage(Locale locale, String selectedLanguageCode, boolean writeLanguage, boolean redrawFileChoosers, boolean firsttime, boolean checkFileChoosers)
    {
	this.selectedLanguageCode = selectedLanguageCode;
	bundle = ResourceBundle.getBundle("rdj.language.translation", locale);
	
	languageLabel.setText(locale.getDisplayLanguage());
	userGuidanceLabelToolTip.setText(bundle.getString("GUIFX.userGuidanceLabelToolTip.text"));
	tgtFileDeleteButton2.setText(bundle.getString("GUIFX.tgtFileDeleteButton2.text"));	
	encryptTab.setText(bundle.getString("GUIFX.encryptTab.text"));
	logTab.setText(bundle.getString("GUIFX.logTab.text"));
	tgtFileDeleteButton2.setText(bundle.getString("GUIFX.tgtFileDeleteButton2.text"));
	tgtFileDeleteButton2ToolTip.setText(bundle.getString("GUIFX.tgtFileDeleteButton2ToolTip.text"));
	targetLabel1.setText(bundle.getString("GUIFX.targetLabel1.text"));
	targetLabel2.setText(bundle.getString("GUIFX.targetLabel2.text"));
	targetInfoLabel.setText(bundle.getString("GUIFX.information.text"));
	keyFileDeleteButton2.setText(bundle.getString("GUIFX.keyFileDeleteButton2.text"));
	keyFileDeleteButton2ToolTip.setText(bundle.getString("GUIFX.keyFileDeleteButton2ToolTip.text"));
	keyLabel1.setText(bundle.getString("GUIFX.keyLabel1.text"));
	keyLabel.setText(bundle.getString("GUIFX.keyLabel.text"));
	createOTPKeyTitle=bundle.getString("GUIFX.createOTPKeyTitle.text");
	createOTPKeyHeader=bundle.getString("GUIFX.createOTPKeyHeader.text");
	createOTPKeyText=bundle.getString("GUIFX.createOTPKeyText.text");
	keyInfoLabel.setText(bundle.getString("GUIFX.information.text"));
	emptyFilesHeaderLabel.setText(bundle.getString("GUIFX.emptyFilesHeaderLabel.text"));
	symlinkFilesHeaderLabel.setText(bundle.getString("GUIFX.symlinkFilesHeaderLabel.text"));
	unreadableFilesHeaderLabel.setText(bundle.getString("GUIFX.unreadableFilesHeaderLabel.text"));
	unreadableFilesHeaderLabelToolTip.setText(bundle.getString("GUIFX.unreadableFilesHeaderLabelToolTip.text"));
	unwritableFilesHeaderLabel.setText(bundle.getString("GUIFX.unwritableFilesHeaderLabel.text"));
	unwritableFilesHeaderLabelToolTip.setText(bundle.getString("GUIFX.unwritableFilesHeaderLabelToolTip.text"));
	hiddenFilesHeaderLabel.setText(bundle.getString("GUIFX.hiddenFilesHeaderLabel.text"));
	targetWarningNameLabel.setText(bundle.getString("GUIFX.targetWarningLabel.text"));
	emptyFilesLabelToolTip.setText(bundle.getString("GUIFX.emptyFilesLabelToolTip.text"));
	symlinkFilesLabelToolTip.setText(bundle.getString("GUIFX.symlinkFilesLabelToolTip.text"));
	unreadableFilesLabelToolTip.setText(bundle.getString("GUIFX.unreadableFilesLabelToolTip.text"));
	unwritableFilesLabelToolTip.setText(bundle.getString("GUIFX.unwritableFilesLabelToolTip.text"));
	hiddenFilesLabelToolTip.setText(bundle.getString("GUIFX.hiddenFilesLabelToolTip.text"));
	targetEncryptedNameLabel.setText(bundle.getString("GUIFX.targetEncryptedNameLabel.text"));
	targetDecryptableNameLabel.setText(bundle.getString("GUIFX.targetDecryptableNameLabel.text"));
	targetUndecryptableNameLabel.setText(bundle.getString("GUIFX.targetUndecryptableNameLabel.text"));
	targetUnencryptedNameLabel.setText(bundle.getString("GUIFX.targetUnencryptedNameLabel.text"));
	targetEncryptableNameLabel.setText(bundle.getString("GUIFX.targetEncryptableNameLabel.text"));
	targetUnencryptableNameLabel.setText(bundle.getString("GUIFX.targetUnencryptableNameLabel.text"));
	totalFilesNameLabel.setText(bundle.getString("GUIFX.totalFilesNameLabel.text"));
	invalidFilesNameLabel.setText(bundle.getString("GUIFX.invalidFilesNameLabel.text"));
	keyWriteNameLabel.setText(bundle.getString("GUIFX.keyWriteNameLabel.text"));
	keyMatchNameLabel.setText(bundle.getString("GUIFX.keyMatchNameLabel.text"));
	keyMissingNameLabel.setText(bundle.getString("GUIFX.keyMissingNameLabel.text"));
	keyWriteLabelToolTip.setText(bundle.getString("GUIFX.keyWriteLabelToolTip.text"));
	keyReadLabelToolTip.setText(bundle.getString("GUIFX.keyReadLabelToolTip.text"));
	keyMissingLabelToolTip.setText(bundle.getString("GUIFX.keyMissingLabelToolTip.text"));
	encryptButton.setText(bundle.getString("GUIFX.encryptButton.text"));
	decryptButton.setText(bundle.getString("GUIFX.decryptButton.text"));
	pauseToggleButton.setText(bundle.getString("GUIFX.pauseToggleButton.text"));
	stopButton.setText(bundle.getString("GUIFX.stopButton.text"));
	supportButton.setText(bundle.getString("GUIFX.supportButton.text"));
	checkUpdateButton.setText(bundle.getString("GUIFX.checkUpdateButton.text"));
	welcome_to=bundle.getString("GUIFX.welcome_to.text");
	introFadeInLine1=bundle.getString("GUIFX.introFadeInLine1.text");
	introFadeInLine2=bundle.getString("GUIFX.introFadeInLine2.text");
	pwdField.setPromptText(bundle.getString("GUIFX.pwdField.prompttext"));
	pwdFieldToolTip.setText(bundle.getString("GUIFX.pwdFieldToolTip.text"));
	showPasswordCheckBoxToolTip.setText(bundle.getString("GUIFX.showPasswordCheckBoxToolTip.text"));
	remainingTimeHeaderLabel.setText(bundle.getString("GUIFX.remainingTimeHeaderLabel.text"));
	elapsedTimeHeaderLabel.setText(bundle.getString("GUIFX.elapsedTimeHeaderLabel.text"));
	totalTimeHeaderLabel.setText(bundle.getString("GUIFX.totalTimeHeaderLabel.text"));
	password_enter=bundle.getString("GUIFX.password_enter.text");
	password_optional=bundle.getString("GUIFX.password_optional.text");
	password_set=bundle.getString("GUIFX.password_set.text");
	select_key_dir=bundle.getString("GUIFX.select_key_dir.text");
	select_files=bundle.getString("GUIFX.select_files.text");
	scanning_files=bundle.getString("GUIFX.scanning_files.text");
	wrong_key_pass=bundle.getString("GUIFX.wrong_key_pass.text");
	encrypt_files=bundle.getString("GUIFX.encrypt_files.text");
	decrypt_files=bundle.getString("GUIFX.decrypt_files.text");
	en_decrypt_files=bundle.getString("GUIFX.en_decrypt_files.text");
	encrypting_files=bundle.getString("GUIFX.encrypting_files.text");
	decrypting_files=bundle.getString("GUIFX.decrypting_files.text");
	finished_encrypting=bundle.getString("GUIFX.finished_encrypting.text");
	finished_decrypting=bundle.getString("GUIFX.finished_decrypting.text");
	
	cpu_workload=bundle.getString("GUIFX.cpu_workload.text");
	storage_io_throughput=bundle.getString("GUIFX.storage_io_throughput.text");
	language_is=bundle.getString("GUIFX.language_is.text");
	click_to_change=bundle.getString("GUIFX.click_to_change.text");
	sound_is=bundle.getString("GUIFX.sound_is.text");
	voice_is=bundle.getString("GUIFX.voice_is.text");
	animation_is=bundle.getString("GUIFX.animation_is.text");
	click=bundle.getString("GUIFX.click.text");
	enable=bundle.getString("GUIFX.enable.text");
	enabled=bundle.getString("GUIFX.enabled.text");
	to_enable=bundle.getString("GUIFX.to_enable.text");
	disable=bundle.getString("GUIFX.disable.text");
	disabled=bundle.getString("GUIFX.disabled.text");
	to_disable=bundle.getString("GUIFX.to_disable.text");
	click_display_to_enable=bundle.getString("GUIFX.click_display_to_enable.text");
	click_display_to_disable=bundle.getString("GUIFX.click_display_to_disable.text");
	ram_mem_used=bundle.getString("GUIFX.ram_mem_used.text");
	
	information=bundle.getString("GUIFX.information.text");
	confirmation=bundle.getString("GUIFX.confirmation.text");
	versionUp2DateHeader=bundle.getString("GUIFX.versionUp2DateHeader.text");
	versionUp2DateContent=bundle.getString("GUIFX.versionUp2DateContent.text");
	newVersionOf=bundle.getString("GUIFX.newVersionOf.text");
	available=bundle.getString("GUIFX.available.text");
	canbeupdatedtoversion=bundle.getString("GUIFX.canbeupdatedtoversion.text");
	wouldYouLikeToDownload=bundle.getString("GUIFX.wouldYouLikeToDownload.text");
	
	you_are_using_a_development_version=bundle.getString("GUIFX.you_are_using_a_development_version.text");
	this_is_a_development_version=bundle.getString("GUIFX.this_is_a_development_version.text");
	would_you_like_to_download=bundle.getString("GUIFX.would_you_like_to_download.text");
	
	error=bundle.getString("GUIFX.error.text");
	online_version_could_not_be_checked=bundle.getString("GUIFX.online_version_could_not_be_checked.text");
	network_connection_issues_perhaps=bundle.getString("GUIFX.network_connection_issues_perhaps.text");

	selected=bundle.getString("GUIFX.selected.text");
	item=bundle.getString("GUIFX.item.text");
	items=bundle.getString("GUIFX.items.text");
	this_also_deletes_matching_keys=bundle.getString("GUIFX.this_also_deletes_matching_keys.text");
	confirm_deletion=bundle.getString("GUIFX.confirm_deletion.text");

	key=bundle.getString("GUIFX.key.text");
	directory=bundle.getString("GUIFX.directory.text");
	file=bundle.getString("GUIFX.file.text");
	
	checksum=bundle.getString("GUIFX.checksum.text");
	calculating=bundle.getString("GUIFX.calculating.text");
	click_for_checksum=bundle.getString("GUIFX.click_for_checksum.text");

	calculate_checksum_tooltip=bundle.getString("GUIFX.calculate_checksum_tooltip.text");
    
	skipping=bundle.getString("GUIFX.skipping.text");
	bytes=bundle.getString("GUIFX.bytes.text");
	click_to_list_unreadable_files=bundle.getString("GUIFX.click_to_list_unreadable_files.text");
	click_to_list_unwritable_files=bundle.getString("GUIFX.click_to_list_unwritable_files.text");
	click_to_list_hidden_files=bundle.getString("GUIFX.click_to_list_hidden_files.text");

	keyInfoHeader=bundle.getString("GUIFX.keyInfoHeader.text");
	keyInfoContent=bundle.getString("GUIFX.keyInfoContent.text");
	targetInfoHeader=bundle.getString("GUIFX.targetInfoHeader.text");
	targetInfoContent=bundle.getString("GUIFX.targetInfoContent.text");

	tgtFileChooser.setLocale(locale); keyFileChooser.setLocale(locale);
	
	if (writeLanguage)
	{
	    prefs.put("Locale_Language", locale.getLanguage());
	    prefs.put("Locale_Country", locale.getCountry());
	    flushPrefs(prefs);
	}
	
	if (redrawFileChoosers)
	{
	    updateFileChoosers(true, firsttime, false, true, firsttime, false);
	}
	
	if (checkFileChoosers)
	{
	    updateFileChoosers(false, firsttime, false, false, firsttime, true);
	}	
    }

    private void flushPrefs(Preferences prefsParam)
    {
	try { prefsParam.flush(); } catch (BackingStoreException ex) { log("Error: flushPrefs(..) " + ex.getMessage() + "\r\n", true, true, true, true ,false); }
    }
    @Override
    public void start(Stage stage) throws Exception
    {
        ui = this;
        guifx = this;
        this.stage = stage;

        root = FXMLLoader.load(getClass().getResource("GUIFX.fxml"));
	scene = new Scene((Parent)root);
	        
//        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); }catch(Exception e){ System.out.println("Exception: setLookAndFeel: " + e.getMessage()); }
//        try { UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"); }catch(Exception e){ System.out.println("Exception: setLookAndFeel: " + e.getMessage()); } // dead lock on BSD

//        setUserAgentStylesheet(STYLESHEET_CASPIAN); // JavaFX2 Stylesheet
        setUserAgentStylesheet(STYLESHEET_MODENA); // JavaFX8 Stylesheet

        this.stage.setScene(scene);
        this.stage.setTitle(Version.getProductName());
        this.stage.setMinWidth(1366);
        this.stage.setMinHeight(700);
        this.stage.setMaximized(true);
	this.stage.initStyle(StageStyle.DECORATED);
        
	this.stage.show();

	this.stage.setOnCloseRequest((WindowEvent e) ->
	{
	    if ( Audio.sound_Is_Enabled ) { new Sound().play(this, Audio.SND_SHUTDOWN,Audio.AUDIO_CODEC); }
	    	    
//	    Shared
	    String val = prefs.get("Shared", "Unknown");
	    if (val.equals("Unknown"))		{ prefs.put("Shared", "No"); openSupport(); flushPrefs(prefs); }
	    else if (val.equals("No"))		{ openSupport(); }
	    else if (val.equals("Yes"))		{  }
	    else				{ prefs.put("Shared", "No"); openSupport(); flushPrefs(prefs); }	    
	});

	Platform.runLater(() ->
	{
	    version = new Version(ui);
	    version.checkCurrentlyInstalledVersion(ui);
	    this.stage.setTitle(Version.getProductName() + " " + version.getCurrentlyInstalledOverallVersionString());
	    fadeInMessage = version.getCurrentlyInstalledOverallVersionString();
	});
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
	logTextArea.setText(""); // clear before starting so UTF8 test symbols can stay in GUIFX.fxml
//	TIMELINE INITIALIZATION
	
	PAUSE_TIMELINE.setCycleCount(Animation.INDEFINITE);
	UPDATE_CLOCKS_TIMELINE.setCycleCount(Animation.INDEFINITE);
	UPDATE_CLOCKS_TIMELINE.setDelay(Duration.seconds(1));

//	FLASH_MAC_MODE_TIMELINE.setCycleCount(Animation.INDEFINITE);
	
//	AUTO_DISABLE_ARMING_MAC_MODE_TIMELINE.setCycleCount(1);

//	tgtFileDeleteButton = new javax.swing.JButton();
//      tgtFileDeleteButton.setFont(DELETE_CHOOSER_FONT); // NOI18N
//      tgtFileDeleteButton.setText("Delete"); // X🗑❌❎⛔ (no utf8)
//      tgtFileDeleteButton.setEnabled(false);
	tgtFileDeleteButton2.setDisable(true);
//      tgtFileDeleteButton.setToolTipText("Delete selected item(s)");
//      tgtFileDeleteButton.addActionListener((java.awt.event.ActionEvent evt) -> { targetFileDeleteButtonActionPerformed(evt); });
	tgtDetailViewButton = new JToggleButton();
	
//        keyFileDeleteButton = new javax.swing.JButton();
//        keyFileDeleteButton.setFont(DELETE_CHOOSER_FONT); // NOI18N
//        keyFileDeleteButton.setText("Delete"); // X🗑❌❎⛔ (no utf8)
//        keyFileDeleteButton.setEnabled(false);
        keyFileDeleteButton2.setDisable(true);
//        keyFileDeleteButton.setToolTipText("Delete selected item");
//        keyFileDeleteButton.addActionListener((java.awt.event.ActionEvent evt) -> { keyFileDeleteButtonActionPerformed(evt); });
	keyDetailViewButton = new JToggleButton();
        
//      Create filefilters        
        finalCryptFilter = new FileNameExtensionFilter("FinalCrypt *.bit", "bit");
        nonFinalCryptFilter = new FileFilter() // Custom negate filefilter
        { 
            @Override public boolean accept(File file) { return !file.getName().toLowerCase().endsWith(".bit"); }
            @Override public String getDescription()   { return "NON FinalCrypt"; }
        };
        
        tgtFileChooser = new JFileChooser();
        tgtFileChooser.setControlButtonsAreShown(false);
//        tgtFileChooser.setToolTipText("Right mousclick for Refresh");
        tgtFileChooser.setMultiSelectionEnabled(true);
        tgtFileChooser.setFocusable(true);
        tgtFileChooser.setFont(FILE_CHOOSER_FONT);
        tgtFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	
        tgtFileChooser.addPropertyChangeListener((java.beans.PropertyChangeEvent evt) -> { targetFileChooserPropertyChange(evt); });
        tgtFileChooser.addActionListener( (java.awt.event.ActionEvent evt) -> { targetFileChooserActionPerformed(evt); });
        targetFileSwingNode.setContent(tgtFileChooser);

        keyFileChooser = new JFileChooser();
	
        keyFileChooser.setControlButtonsAreShown(false);
//        keyFileChooser.setToolTipText("Right mousclick for Refresh");
        keyFileChooser.setMultiSelectionEnabled(true);
        keyFileChooser.setFocusable(true);
        keyFileChooser.setFont(FILE_CHOOSER_FONT);
        keyFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	Validate.key_Home_Path =  keyFileChooser.getCurrentDirectory().toPath();
	
        keyFileChooser.addPropertyChangeListener((java.beans.PropertyChangeEvent evt) -> { keyFileChooserPropertyChange(evt); });
	keyListener = keyFileChooser.getPropertyChangeListeners();
        keyFileChooser.addActionListener( (java.awt.event.ActionEvent evt) -> { keyFileChooserActionPerformed(evt); });
        Timeline timeline = new Timeline(new KeyFrame( Duration.millis(200), ae -> { keyFileSwingNode.setContent(keyFileChooser); } )); timeline.play(); // Delay keyFileChooser to give 1st focus to targetFileChooser

        finalCrypt = new FinalCrypt(this); finalCrypt.start();
	
	
	pwdField.setContextMenu(new ContextMenu()); // Getting rid of the mouse paste function. Actionlistener does not pickup on pasted passwords through mouse
	
//	keyButton.setTextFill(Color.GREY); keyButton.setDisable(true); keyButton.setText(CREATE_KEY); keyButton.setVisible(false);

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
	centerX = endX; // endX
	centerY = 0.55;
	proportional = true;
	
	textLabelTimeline.setCycleCount(Animation.INDEFINITE);
	
	sysmon = sysMonCanvas.getGraphicsContext2D();
	sysmon.setGlobalAlpha(0.8);
        sysmon.setStroke(Color.RED);
        sysmon.setLineWidth(2);
	
	sysmonOffSetX = 9;
	
//	sysmon.setFill(Color.valueOf("#4A4039"));
	sysmon.setTextAlign(TextAlignment.LEFT);
	sysmon.setTextBaseline(VPos.BOTTOM);
	sysmon.setFont(javafx.scene.text.Font.font("Liberation Mono", FontWeight.NORMAL, FontPosture.REGULAR, 14));
	
//	sysmon.setFill(Color.valueOf("#58781F"));
	sysmon.setFill(Color.valueOf("#888888"));
	sysmon.fillText(SOUND_ON_SYMBOL, sysmonOffSetX + 0, 20);
	sysmon.setFill(Color.valueOf("#4A4039"));
	sysmon.fillText(CPU_SYMBOL, sysmonOffSetX + 30, 20);
	sysmon.fillText(RAM_SYMBOL, sysmonOffSetX + 60, 20); 
	sysmon.fillText(STORAGE_SYMBOL, sysmonOffSetX + 90, 20);
//	sysmon.setFill(Color.valueOf("#58781F"));
	sysmon.setFill(Color.valueOf("#888888"));
	sysmon.fillText(VOICE_ON_SYMBOL, sysmonOffSetX + 120, 20);

	welcome();	
    }
    
    private void welcome()
    {
	languageList = FXCollections.observableArrayList
							("Arabic",
							 "Bulgarian",
							 "Chinese",
							 "Croatian",
							 "Czech",
							 "Dutch",
							 "English",
							 "Esperanto",
							 "Farsi",
							 "French",
							 "German",
							 "Greek",
							 "Hindi",
							 "Hungarian",
							 "Icelandic",
							 "Indonesian",
							 "Italian",
							 "Kazakh",
							 "Latvian",							 
							 "Malaysian",
							 "Maltees",
							 "Norwegian",
							 "Polish",
							 "Portugese",
							 "Romanian",
							 "Russian",
							 "Serbian",
							 "Spanish",
							 "Swedish",
							 "Thai",
							 "Turkish",
							 "Ukrainian"
	);
	
	selectLanguage.getItems().setAll(languageList);
//	selectLanguage.getSelectionModel().selectFirst();

	prevsval1 = prefs.get("Locale_Language", "Unknown");
	prevsval2 = prefs.get("Locale_Country", "Unknown");
	
	if (prevsval1.equals("Unknown")) { selectedLocale = Locale.getDefault(); prefs.put("Locale_Language", selectedLocale.getLanguage()); flushPrefs(prefs); }
	else				 { selectedLocale = new Locale(prevsval1,""); }
	if (prevsval2.equals("Unknown")) { selectedLocale = Locale.getDefault(); prefs.put("Locale_Country", selectedLocale.getCountry()); flushPrefs(prefs); }
	else				 { selectedLocale = new Locale(prevsval1,prevsval2); }
	
	selectedLanguageCode = selectedLocale.getLanguage();
//	switchLanguage(Locale locale, String selectedLanguageCode, boolean writeLanguage, boolean redrawFileChoosers, boolean firsttime, boolean checkFileChoosers)
	switchLanguage(selectedLocale,      selectedLanguageCode,		   false,			true,		   true,		    false);

	// First time selection Key Directory
	Path keyPath = keyFileChooser.getCurrentDirectory().toPath();
	//		     getFCPath(UI ui, String caller,  Path path, boolean isKey, Path keyPath,    boolean disabledMAC, boolean report)
	keyFCPath = Validate.getFCPath(this,	   "",	  keyPath,          true,      keyPath, finalCrypt.disabledMAC,          true);

	

	targetFCPathList = new FCPathList<FCPath>(); updateDashboard(targetFCPathList);
        configuration = new Configuration(ui);
        version = new Version(ui);
        version.checkCurrentlyInstalledVersion(this);

	statusLabel.setText(welcome_to + " " + Version.getProductName() + " " + version.getCurrentlyInstalledOverallVersionString());

//	Set the corner arrows

	bottomleftLabel.setOpacity(0);	bottomleftLabel.setVisible(bottomleftLabelEnabled);
	topleftLabel.setOpacity(0);	topleftLabel.setVisible(topleftLabelEnabled);
	toprightLabel.setOpacity(0);	toprightLabel.setVisible(toprightLabelEnabled);
	bottomrightLabel.setOpacity(0);	bottomrightLabel.setVisible(bottomrightLabelEnabled);

//	Prefer monospaced ◤ XY -18 -28 | ◥ XY 18 -28 | ◢ XY 18 8 | ◣ XY -18 8
//	⬈⬉⬊⬋ | ⇖⇗⇘⇙ | ↖↗↘↙

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
	    ,"■■■■"		// 15┘
	    ,"┌┐┘└"		// 16
	};
	String arrows = arrowsArray[4]; // Select the symbol (default 4)

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
//	log(getRuntimeEnvironment(), false, true, true, false ,false);
	log(Version.getLogHeader(this.getClass().getSimpleName(), version, configuration), false, true, true, false ,false);

//      cpuIndicator
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
	    if ((! animation_Is_Enabled) || ( load_Manager_MS_Passed >= LOAD_MANAGER_MS_INTERVAL ))
	    {
		if ((! animation_Is_Enabled) || (userLoadPerc >= LOADHIGH_THRESHOLD)) // High load detected
		{
		    load_High_MS_Passed += load_Manager_MS_Passed; load_Low_MS_Passed = 0.0d; // High load period register
		    if ( (! animation_Is_Enabled) || ((textLabelTimeline.getStatus() == Animation.Status.RUNNING) & (load_High_MS_Passed >= LOAD_HIGH_MS_TIMEOUT)) ) // High Load period exceeded
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
	checksumTooltip.setFont(javafx.scene.text.Font.font("Liberation Mono", FontWeight.NORMAL, FontPosture.REGULAR, 13));

// No GDK Error
	Platform.runLater(() ->
	{	    
// GDK Error
	    Version ver = new Version(ui); ver.checkCurrentlyInstalledVersion(ui);
	    
	    fadeInMessage = Version.getProductName();
	    textLabelTimeline.play();
	    
//	    textLabel Introduction Animation ==========================================================

	    updateFileChoosers(true, false, false, true, false, false);

	    ScaleTransition scaleTransition1 = new ScaleTransition(Duration.millis(1500), userGuidanceLabel);
	    scaleTransition1.setFromX(0.98f);scaleTransition1.setToX(1.0f);
	    scaleTransition1.setFromY(0.98f);scaleTransition1.setToY(1.0f);
	    scaleTransition1.setFromZ(0.98f);scaleTransition1.setToZ(1.0f);
	    scaleTransition1.setCycleCount(1);
	    scaleTransition1.setAutoReverse(false);
	    scaleTransition1.setDelay(Duration.millis(500));
	    scaleTransition1.setInterpolator(Interpolator.EASE_BOTH);

	    userGuidanceLabel.setText(introFadeInLine1 + "\r\n" + introFadeInLine2); userGuidanceLabel.setStyle("-fx-font-size: " + (userGuidanceLabel.getWidth() / userGuidanceLabel.getText().length() * 2.0) + "px;");
	    FadeTransition fadeTransition1 = new FadeTransition(Duration.millis(1500), userGuidanceLabel);
	    fadeTransition1.setFromValue(0.05f);
	    fadeTransition1.setToValue(0.7f);
	    fadeTransition1.setCycleCount(2);
	    fadeTransition1.setAutoReverse(true);
	    fadeTransition1.setDelay(Duration.millis(500));
	    fadeTransition1.setInterpolator(Interpolator.EASE_BOTH);

	    ParallelTransition parallelTransition1 = new ParallelTransition();
	    parallelTransition1.getChildren().addAll( scaleTransition1, fadeTransition1 );
	    parallelTransition1.setCycleCount(1);

	    parallelTransition1.setOnFinished((ActionEvent actionEvent) ->
	    {
		
		userGuidanceLabel.setText("");

		userGuidanceLabel.setText(Version.getProductName()); userGuidanceLabel.setStyle("-fx-font-size: " + (userGuidanceLabel.getWidth() / userGuidanceLabel.getText().length() * 1.3) + "px;");
		FadeTransition fadeTransition2 = new FadeTransition(Duration.millis(1000), userGuidanceLabel);
		fadeTransition2.setFromValue(0.05f);
		fadeTransition2.setToValue(0.7f);
		fadeTransition2.setCycleCount(1);
		fadeTransition2.setAutoReverse(false);
		fadeTransition2.setDelay(Duration.millis(0));
		fadeTransition2.setInterpolator(Interpolator.EASE_BOTH);

		
		scaleTransition1.setDuration(Duration.millis(1000));
		scaleTransition1.setDelay(Duration.millis(0));
		
		ParallelTransition parallelTransition2 = new ParallelTransition();
		parallelTransition2.getChildren().addAll( scaleTransition1, fadeTransition2 );
		parallelTransition2.setCycleCount(1);

		parallelTransition2.setOnFinished((ActionEvent actionEvent2) ->
		{		    
//		    try { root = FXMLLoader.load(getClass().getResource("GUIFX.fxml"), getBundle(Locale.getDefault())); }
//		    try { root = FXMLLoader.load(getClass().getResource("GUIFX.fxml"), ResourceBundle.getBundle("rdj.language.GUIFX_nl_NL", locale_nl_NL)); }
//		    catch (IOException ex) { log("Hmmm" + ex.getMessage(), true, true, true, false, false); }
		

		    // // First time if no val then "Unknown" prefs location registry: HKEY_CURRENT_USER\Software\JavaSoft\Prefs
		    prevsval1 = prefs.get("Initialized", "Unknown"); if (! prevsval1.equals("Yes")) {prefs.put("Initialized", "Yes"); flushPrefs(prefs); } else {  }
		    // ============================================================================================================================
		    // ================================================== GUI gets Idle here ======================================================
		    // ============================================================================================================================
		    // userGuidanceMessage(SELECT_KEY_DIR, 64, false, false, true, false, Voice.VOI_SELECT_KEY_DIRECTORY, 0);

		    // keyButton.setDisable(false);
		    checkUpdateButton.setDisable(false);
		    supportButton.setDisable(false);
		    // ============================================================================================================================


		    if (System.getProperty("os.name").toLowerCase().indexOf("mac") == -1) // Again due to Mac OSX
		    {
			disableFileChoosers(false, true, true);
		    }
		    else
		    {
			disableFileChoosers(false, true, true);
		    }

		    updateFileChoosers(false, false, false, false, false, true);

		    FadeTransition sysmonFadeTransition = new FadeTransition(Duration.millis(2000), userGuidanceFadePane); // sysMonCanvas 
		    sysmonFadeTransition.setFromValue(1.0f);
		    sysmonFadeTransition.setToValue(0.0f);
		    sysmonFadeTransition.setCycleCount(1);
		    sysmonFadeTransition.setAutoReverse(false);
		    sysmonFadeTransition.setDelay(Duration.seconds(0));
		    sysmonFadeTransition.setInterpolator(Interpolator.EASE_OUT);
		    sysmonFadeTransition.setOnFinished((ActionEvent enableKeyButtonEvent) ->
		    {
			new Sound().play(this, Audio.SND_MESSAGE,Audio.AUDIO_CODEC);

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
			new Sound().play(this, Audio.SND_SELECTKEY,Audio.AUDIO_CODEC);
			systemMonitorTestTimeline.play();			
		    });
		    sysmonFadeTransition.play();

    //		Last Update Checked
		    long updateChecked = 0; // Epoch date
    //		long updateCheckPeriod = 1000L*20L; // Just to test auto update function
		    long updateCheckPeriod = 1000L*60L*60L*24L; // Update period 1 Day
		    now = Calendar.getInstance().getTimeInMillis(); // Epoch date
		    prevsval1 = prefs.get("Update Checked", "Unknown"); // if no val then "Unknown" prefs location registry: HKEY_CURRENT_USER\Software\JavaSoft\Prefs
		    boolean invalidUpdateCheckedValue = false;
		    try { updateChecked = Long.valueOf(prevsval1); } catch (NumberFormatException e) { invalidUpdateCheckedValue = true; }
		    if ( invalidUpdateCheckedValue ) { Platform.runLater(() -> { checkUpdate(false); }); } else { if (now - updateChecked >= updateCheckPeriod) { Platform.runLater(() -> { checkUpdate(false); }); } }		    
		});
		parallelTransition2.play();
	    });
	    parallelTransition1.play();

//	    Sound
	    prevsval1 = prefs.get("Sound", "Unknown");
	    if (prevsval1.equals("Unknown"))		{ setSound(false); prefs.put("Sound", "Disabled"); flushPrefs(prefs); }
	    else if (prevsval1.equals("Enabled"))	{ setSound(true); }
	    else if (prevsval1.equals("Disabled"))	{ setSound(false); }
	    else					{ prefs.put("Sound", "Disabled"); setSound(false); flushPrefs(prefs); }

//	    Voice
	    prevsval1 = prefs.get("Voice", "Unknown");
	    if (prevsval1.equals("Unknown"))		{ setVoice(false); prefs.put("Voice", "Disabled"); flushPrefs(prefs); }
	    else if (prevsval1.equals("Enabled"))	{ setVoice(true); }
	    else if (prevsval1.equals("Disabled"))	{ setVoice(false); }
	    else					{ prefs.put("Sound", "Disabled"); setVoice(false); flushPrefs(prefs); }
	    
//	    Animated
	    prevsval1 = prefs.get("Animated", "Unknown");
	    if (prevsval1.equals("Unknown"))		{ prefs.put("Animated", "Disabled"); animation_Is_Enabled = false; flushPrefs(prefs); }
	    else if (prevsval1.equals("Enabled"))	{ animation_Is_Enabled = true;}
	    else if (prevsval1.equals("Disabled"))	{ animation_Is_Enabled = false; }
	    else					{ prefs.put("Animated", "Disabled"); animation_Is_Enabled = false; flushPrefs(prefs); }
	});	
    }
    
    private void displaySystemMonitor(double usrLoadPercParam, String userLoadString, double usedMemPercParam, String usedMemString, double throughPercParam, String throughputString)
    {
	Platform.runLater(() ->
	{
	    int width = 2; sysmon.setLineWidth(width);
	    
	    int userLdPosX = sysmonOffSetX + 50;
	    int memUsePosX = userLdPosX + 25;
	    int ioLoadPosX = memUsePosX + 32;
	    
	    String languageStatusString = language_is + " " + selectedLocale.getDisplayLanguage() + " (" + click_to_change + ")";
	    String soundStatusString = SOUND_ON_SYMBOL + " " + sound_is + " "; if (Voice.sound_Is_Enabled) { soundStatusString += enabled + " (" + click+ "  " + SOUND_ON_SYMBOL + " " + to_disable + ")"; } else { soundStatusString += disabled + " (" + click + " " + SOUND_ON_SYMBOL + " " + to_enable + ")"; }
	    String voiceStatusString = VOICE_ON_SYMBOL + " " + voice_is + " "; if (Voice.voice_Is_Enabled) { voiceStatusString += enabled + " (" + click + " " + VOICE_ON_SYMBOL + " " + to_disable + ")"; } else { voiceStatusString += disabled + " (" + click + " " + VOICE_ON_SYMBOL + " " + to_enable + ")"; }
	    String animationStatusString = animation_is + " "; if (animation_Is_Enabled) { animationStatusString += enabled + " (" + click_display_to_disable + ")"; } else { animationStatusString += disabled + " (" + click_display_to_enable + ")"; }

	    String sysMonString = "";
	    sysMonString += userLoadString + "\r\n";
	    sysMonString += usedMemString + "\r\n";
	    sysMonString += throughputString + "\r\n\r\n";
	    sysMonString += languageStatusString + "\r\n";
	    sysMonString += soundStatusString + "\r\n";
	    sysMonString += voiceStatusString + "\r\n";
	    sysMonString += animationStatusString;

//	    Drawing
	    sysmon.clearRect(userLdPosX - 1, 0, width, 20);
	    for (int y = 0; y < (usrLoadPercParam / 100) * 20; y += 4) { sysmon.setStroke(Color.color(        getValidColor(y / 20d), getValidColor(1.0d - ( y / 20d )), 0)); sysmon.strokeLine(userLdPosX, 20 - y, userLdPosX, 20 - y + 0); }
	    	    	    
	    sysmon.clearRect(memUsePosX - 1, 0, width, 20);
	    for (int y = 0; y < (usedMemPercParam / 100) * 20; y += 4) { sysmon.setStroke(Color.color(        getValidColor(y / 20d), getValidColor(1.0d - ( y / 20d )), 0)); sysmon.strokeLine(memUsePosX, 20 - y, memUsePosX, 20 - y + 0); }

	    sysmon.clearRect(ioLoadPosX - 1, 0, width, 20);
	    for (int y = 0; y < (throughPercParam / 100) * 20; y += 4) { sysmon.setStroke(Color.color(getValidColor(1.0 - ( y / 20d )), getValidColor(1.0d - (1.0 - ( y / 20d ))), 0)); sysmon.strokeLine(ioLoadPosX, 20 - y, ioLoadPosX, 20 - y + 0); }
	    
//	    sysMonTooltip.setText(sysMonString);
	    userGuidanceLabelToolTip.setText(sysMonString);
	});
    }

    private double getValidColor(double value) { if (value < 0.0) { return 0.0; } else if (value > 1.0) { return 1.0; } else return value; }


    @FXML  private void sysMonLabelOnMouseClicked(MouseEvent event)
    {
	if (( event.getX() >= 10 ) && (event.getX() <= 25)) // Sound
	{
	    if (Voice.sound_Is_Enabled) // turn sound off
	    {
		new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
		new Sound().play(this, Audio.SND_SOUND_DISABLED,Audio.AUDIO_CODEC);
		setSound(false); prefs.put("Sound", "Disabled"); flushPrefs(prefs); 
	    }
	    else // turn sound on
	    {
		setSound(true); prefs.put("Sound", "Enabled"); flushPrefs(prefs);
		new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
		new Sound().play(this, Audio.SND_SOUND_ENABLED,Audio.AUDIO_CODEC);
	    }
	}
	else if (( event.getX() >= 130 ) && (event.getX() <= 145)) // Voice
	{
	    if (Voice.voice_Is_Enabled) // turn voice off
	    {
		new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
		Voice.play(this, Audio.VOI_VOICE_DISABLED,Audio.AUDIO_CODEC);
		setVoice(false); prefs.put("Voice", "Disabled"); flushPrefs(prefs);
	    }
	    else // turn voice on
	    {		
		setVoice(true); prefs.put("Voice", "Enabled"); flushPrefs(prefs);
		new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
		Voice.play(this, Audio.VOI_VOICE_ENABLED,Audio.AUDIO_CODEC);
	    }
	}
    }

    private void setSound(boolean enableSound)
    {
	Voice.sound_Is_Enabled = enableSound;

	if (Voice.sound_Is_Enabled) // turn sound off
	{
	    sysmon.setFill(Color.valueOf("#58781F"));
	    sysmon.fillText(SOUND_ON_SYMBOL, sysmonOffSetX + 0, 20);
	}
	else // turn sound on
	{
	    sysmon.setFill(Color.valueOf("#4A4039"));
	    sysmon.fillText(SOUND_ON_SYMBOL, sysmonOffSetX + 0, 20);
	}
    }
    
    private void setVoice(boolean enableVoice)
    {
	Voice.voice_Is_Enabled = enableVoice;
	
	if (Voice.voice_Is_Enabled) // turn voice off
	{
	    sysmon.setFill(Color.valueOf("#58781F"));
	    sysmon.fillText(VOICE_ON_SYMBOL, sysmonOffSetX + 120, 20);
	}
	else // turn voice on
	{		
	    sysmon.setFill(Color.valueOf("#4A4039"));
	    sysmon.fillText(VOICE_ON_SYMBOL, sysmonOffSetX + 120, 20);
	}
    }
    
    private void updateSystemMonitor()
    {
	double userLoadPerc = getUserLoadPerc(); String userLoadString = CPU_SYMBOL + " " + cpu_workload + " (" + Stats.getDecimal(userLoadPerc,0) + "%)"; MemStats memStats = getMemStats();
	double throughputPerc = ((megaBytesPerSecond) / (finalCrypt.io_Throughput_Ceiling / 100)); String throughputString = STORAGE_SYMBOL + " " + storage_io_throughput + " (" + Stats.getDecimal((throughputPerc * (finalCrypt.io_Throughput_Ceiling / 100)),1) + " MiB/S)";
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
	memStats.memStatsString += RAM_SYMBOL + " " + ram_mem_used + " (" + Stats.getDecimal(memStats.usedMemPerc,1) + "%) " + Stats.getDecimal(Long.valueOf(memStats.usedMem).doubleValue() / (1024d * 1024d),1) + " MiB / " + Stats.getDecimal(Long.valueOf(memStats.totMem).doubleValue() / (1024d * 1024d * 1024d),1) + " GiB"; 

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

    private void disableFileChoosers(boolean param, boolean firsttime, boolean animated)
    {
	if (! animated)
	{
	    if (param) // disable filechoosers
	    {
		keyFileSwingNode.setMouseTransparent(param); targetFileSwingNode.setMouseTransparent(param);
		keyFileFoil.setOpacity(0.5);
		targetFileFoil.setOpacity(0.5);
	    }
	    else // enable filechoosers
	    {
		keyFileSwingNode.setMouseTransparent(param); targetFileSwingNode.setMouseTransparent(param);
		keyFileFoil.setOpacity(0.1);
		targetFileFoil.setOpacity(0.1);
		if (firsttime)
		{
//			keyFileChooserPropertyCheck();
		}
	    }
	}
	else
	{
	    if (param) // disable filechoosers
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
	    else // enable filechoosers
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
		enableTimeline.setOnFinished((ActionEvent actionEvent) ->
		{
		    keyFileSwingNode.setMouseTransparent(param); targetFileSwingNode.setMouseTransparent(param);

		    if (firsttime)
		    {
    //			keyFileChooserPropertyCheck();
		    }
		});
		enableTimeline.play();
	    }
	}	    
    }
    
//    synchronized public void userGuidanceMessage(String message, int fontsize, boolean bottomleft, boolean topleft, boolean topright, boolean bottomright, String audio, int media_Delay)
    synchronized public void userGuidanceMessage(Message ugMessage)
    {
	bottomleftLabelEnabled = ugMessage.bottomleft; topleftLabelEnabled = ugMessage.topleft; toprightLabelEnabled = ugMessage.topright; bottomrightLabelEnabled = ugMessage.bottomright;
	
	if (textLabelTimeline.getStatus() == Animation.Status.PAUSED) // set arrows opacity when animation is paused due to high user load
	{
	    if (bottomleftLabel.getOpacity() == 0.0)	{ bottomleftLabel.setVisible(bottomleftLabelEnabled); }
	    if (topleftLabel.getOpacity() == 0.0)	{ topleftLabel.setVisible(topleftLabelEnabled); }
	    if (toprightLabel.getOpacity() == 0.0)	{ toprightLabel.setVisible(toprightLabelEnabled); }
	    if (bottomrightLabel.getOpacity() == 0.0)	{ bottomrightLabel.setVisible(bottomrightLabelEnabled); }
	}
	
//	FADE OUT
	
	final int count = 10;
	final int cycleduration = 50;
	final double fadevarmax = 0.9;

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
	    Voice.play(this, ugMessage.audio, Audio.AUDIO_CODEC);
	    
	    if (textLabelTimeline.getStatus() != Animation.Status.RUNNING)
	    {
		bottomleftLabel.setOpacity(0);  bottomleftLabel.setVisible(bottomleftLabelEnabled);
		topleftLabel.setOpacity(0);	    topleftLabel.setVisible(topleftLabelEnabled);		
		toprightLabel.setOpacity(0);    toprightLabel.setVisible(toprightLabelEnabled);
		bottomrightLabel.setOpacity(0); bottomrightLabel.setVisible(bottomrightLabelEnabled);
	    }
	    userGuidanceLabel.setOpacity(0);
	    double sizeFactor = 1.5; if (selectedLanguageCode.equalsIgnoreCase("cn")) { sizeFactor = 0.5; } else { sizeFactor = 1.5; }
	    userGuidanceLabel.setStyle("-fx-font-size: " + Math.round(userGuidanceLabel.getWidth() / ugMessage.message.length() * sizeFactor) + "px;");
	    userGuidanceLabel.setText(ugMessage.message);
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
	}); // Do at fade out ready
	labelTimeline.play();
    }
    
    public Alert introAlert(AlertType type, String title, String headerText, String message, String optOutMessage, Consumer<Boolean> optOutAction, ButtonType... buttonTypes)
    {
	new Sound().play(this, Audio.SND_MESSAGE,Audio.AUDIO_CODEC);
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
       	alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	return alert;
    }

    private void checkUpdate(boolean userActivated)
    {
//        Platform.runLater(() ->
//	{
	    version = new Version(ui);
	    version.checkCurrentlyInstalledVersion(GUIFX.this);
	    version.checkLatestOnlineVersion(GUIFX.this);
	    prefs.putLong("Update Checked", now); flushPrefs(prefs); 
	    String[] lines = version.getUpdateStatus().split("\r\n");
	    for (String line: lines) { log(line + "\r\n", true, true, true, false, false); }

//	    Just for testing purposes (uncomment)
//	    alertCurrentVersionIsUp2Date();
//	    alertCurrentVersionCanBeUpdated();
//	    alertCurrentVersionIsDevelopement();
//	    alertlatestVersionUnknown();
//	    latestAlertMessage();
////	    currentAlertMessage(); // Should never be tested and used in production
	    
	    if (version.latestVersionIsKnown())
	    {
		if (( ! version.versionIsDifferent() ) && ( userActivated ))		    { alertCurrentVersionIsUp2Date(); }
		else { if (version.versionCanBeUpdated())				    { alertCurrentVersionCanBeUpdated(); }
		       else if ( (version.versionIsDevelopment() ) && ( userActivated ) )   { alertCurrentVersionIsDevelopement(); } }
	    }
	    else									    { alertlatestVersionUnknown(); }

//	    After all check update processing comes an optional alert	    
	    if (
		    ( true ) // Leave to "true" to enable online alerts
		    && ( version.getLatestAlertSubjectString() != null)
		    && ( ! version.getLatestAlertSubjectString().isEmpty())
		    && ( version.getLatestAlertString() != null)
		    && ( ! version.getLatestAlertString().isEmpty())
		)   // Only display Alert in VERSION2 file
	    { latestAlertMessage(); }
	    
	    if (
		    ( false ) // Leave to "false" Only set to true to test an alert
		    && ( version.getCurrentAlertSubjectString() != null)
		    && ( ! version.getCurrentAlertSubjectString().isEmpty())
		    && ( version.getCurrentAlertString() != null)
		    && ( ! version.getCurrentAlertString().isEmpty())
		)   // Only display Alert in VERSION2 file
	    { currentAlertMessage(); }
//	});
    }
    
    private void alertCurrentVersionIsUp2Date()
    {
	new Sound().play(this, Audio.SND_MESSAGE,Audio.AUDIO_CODEC);
	Alert alert = new Alert(AlertType.INFORMATION);

	DialogPane dialogPane = alert.getDialogPane();
	dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
	dialogPane.getStyleClass().add("myDialog");

	alert.setTitle(information);
	alert.setHeaderText(versionUp2DateHeader);
	alert.setResizable(true);
	String	content = versionUp2DateContent + ": (" + Version.getProductName() + " v" +version.getCurrentlyInstalledOverallVersionString() + ")\r\n\r\n";
		content += version.getCurrentReleaseString();
	alert.setContentText(content);
	alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	alert.showAndWait();
	if (alert.getResult() == ButtonType.OK) { new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC); }
    }
    
    private void alertCurrentVersionCanBeUpdated()
    {
	new Sound().play(this, Audio.SND_ALERT,Audio.AUDIO_CODEC);
							       String alertString = Version.getProductName() + " v" + version.getCurrentlyInstalledOverallVersionString() + " " + canbeupdatedtoversion + ": " + version.getLatestOnlineOverallVersionString() + "\r\n\r\n";
	if (! version.getLatestReleaseString().isEmpty())	    { alertString += version.getLatestReleaseString() + "\r\n"; }
								      alertString += wouldYouLikeToDownload + " (" + Version.getProductName() + " v" + version.getLatestOnlineOverallVersionString() + ") ?\r\n";

	Alert alert = new Alert(Alert.AlertType.CONFIRMATION, alertString, ButtonType.YES, ButtonType.NO);

	DialogPane dialogPane = alert.getDialogPane();
	dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
	dialogPane.getStyleClass().add("myDialog");

	alert.setTitle(confirmation);
	alert.setHeaderText(newVersionOf + " " + Version.getProductName() + " " + available);
	alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	alert.showAndWait();

	if (alert.getResult() == ButtonType.YES) { new Sound().play(this, Audio.SND_OPEN,Audio.AUDIO_CODEC); Version.openWebSite(this); } else { new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC); }
    }
    
    private void alertCurrentVersionIsDevelopement()
    {
	new Sound().play(this, Audio.SND_ALERT,Audio.AUDIO_CODEC);

	String alertString = "";
	Alert alert = new Alert(Alert.AlertType.CONFIRMATION, alertString, ButtonType.YES, ButtonType.NO);

	DialogPane dialogPane = alert.getDialogPane();
	dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
	dialogPane.getStyleClass().add("myDialog");

	alert.setTitle(confirmation);
			       
	alert.setHeaderText(you_are_using_a_development_version);
	alert.setResizable(true);
	
	String	content = "";
	content += this_is_a_development_version + ":    (" + Version.getProductName() + " v" +version.getCurrentlyInstalledOverallVersionString() + ")\r\n\r\n";
	content += version.getCurrentReleaseString() + "\r\n";
	content += "=====================================================\r\n\r\n";
	content += would_you_like_to_download + "        (" + Version.getProductName() + " v" + version.getLatestOnlineOverallVersionString() + ") ?\r\n";
	alert.setContentText(content);
	alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	alert.showAndWait();

	if (alert.getResult() == ButtonType.YES) { new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC); Version.openWebSite(this); } else { new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC); }
    }
    
    private void alertlatestVersionUnknown()
    {
	new Sound().play(this, Audio.SND_MESSAGE,Audio.AUDIO_CODEC);
	Alert alert = new Alert(AlertType.ERROR);

	DialogPane dialogPane = alert.getDialogPane();
	dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
	dialogPane.getStyleClass().add("myDialog");
	
	alert.setTitle(confirmation);
	alert.setHeaderText(online_version_could_not_be_checked);
	alert.setResizable(true);
	alert.setContentText(version.getUpdateStatus() + "\r\n" + network_connection_issues_perhaps + "\r\n");
	alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	alert.showAndWait();
	if (alert.getResult() == ButtonType.OK) { new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC); }
    }
    
    private void latestAlertMessage()
    {
	new Sound().play(this, Audio.SND_MESSAGE,Audio.AUDIO_CODEC);
	Alert alert = new Alert(AlertType.INFORMATION);

	//      Style the Alert
	DialogPane dialogPane = alert.getDialogPane();
	dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
	dialogPane.getStyleClass().add("myDialog");

	alert.setTitle(information);
	alert.setHeaderText(version.getLatestAlertSubjectString());
	alert.setResizable(true);
	alert.setContentText(version.getLatestAlertString());
	alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	alert.showAndWait();
	if (alert.getResult() == ButtonType.OK) { new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC); }
    }
    
    private void currentAlertMessage()
    {
	new Sound().play(this, Audio.SND_MESSAGE,Audio.AUDIO_CODEC);
	Alert alert = new Alert(AlertType.INFORMATION);

	//      Style the Alert
	DialogPane dialogPane = alert.getDialogPane();
	dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
	dialogPane.getStyleClass().add("myDialog");

	alert.setTitle(information);
	alert.setHeaderText(version.getCurrentAlertSubjectString());
	alert.setResizable(true);
	alert.setContentText(version.getCurrentAlertString());
	alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	alert.showAndWait();

	if (alert.getResult() == ButtonType.OK) { new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC); }
    }
    
//  Custom FileChooserDelete Listener methods
    private void targetFileDeleteButtonActionPerformed(java.awt.event.ActionEvent evt)	{ targetFileDelete(); }                                               
    @FXML  private void tgtFileDeleteButton2OnAction(ActionEvent event)			{ targetFileDelete(); }

    private void targetFileDelete()
    {
        Platform.runLater(() ->
	{	    
	    new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	    new Sound().play(this, Audio.SND_ALERT,Audio.AUDIO_CODEC);
	    String itemword = "";
	    if ( tgtFileChooser.getSelectedFiles().length == 1 )      { itemword = item; }
	    else if ( tgtFileChooser.getSelectedFiles().length > 1 )  { itemword = items; }
	    String  selection =  tgtFileDeleteButton2.getText() + " " + tgtFileChooser.getSelectedFiles().length + " " + selected + " " + itemword + "?\r\n\r\n";
	    if (keyFCPath.isValidKeyDir) { selection += "* " + this_also_deletes_matching_keys + "\r\n"; }
	    Alert alert = new Alert(AlertType.CONFIRMATION, selection, ButtonType.YES, ButtonType.NO);
	    alert.setHeaderText(confirm_deletion + "?");
	    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	    alert.showAndWait();
	    if (alert.getResult() == ButtonType.YES)
	    {
		if ((tgtFileChooser != null)  && (tgtFileChooser.getSelectedFiles() != null))
		{
		    if (tgtFileChooser.getSelectedFiles().length > 0)
		    {
			new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC);
//			play(SND_INPUT_OK, AUDIO_CODEC);
			ArrayList<Path> pathList = finalCrypt.getPathList(tgtFileChooser.getSelectedFiles());
//			boolean delete = true;
			boolean returnpathlist = false;
			String pattern1 = "glob:*";

			tab.getSelectionModel().select(1);
			log("\r\nDeleting selecttion started\r\n\r\n", true, true, true, false, false);
			finalCrypt.deleteSelection(pathList, keyFCPath, MySimpleFCFileVisitor.DELETE, returnpathlist, pattern1, false);
			log("\r\nDeleting selection finished\r\n\r\n", true, true, true, false, false);
			updateFileChoosers(true, false, true, true, false, true); // targetFileDeleteButtonActionPerformed()
		    }
		}
	    } else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC); }
	});
    }                                               


    private void keyFileDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) { keyFileDelete(); }                                               
    @FXML  private void keyFileDeleteButton2OnAction(ActionEvent event)		    { keyFileDelete(); }

    private void keyFileDelete()                                                
    {                                                            
        Platform.runLater(() ->
	{
	    new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	    new Sound().play(this, Audio.SND_ALERT,Audio.AUDIO_CODEC);
	    String itemword = "";
	    if ( keyFileChooser.getSelectedFiles().length == 1 )      { itemword = item; }
	    else if ( keyFileChooser.getSelectedFiles().length > 1 )  { itemword = items; }
	    String selection = keyFileDeleteButton2.getText() + keyFileChooser.getSelectedFiles().length + " " + selected + " " + itemword + "?";
	    Alert alert = new Alert(AlertType.CONFIRMATION, selection, ButtonType.YES, ButtonType.NO);
	    alert.setHeaderText(confirm_deletion + "?");
	    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	    alert.showAndWait();
	    if (alert.getResult() == ButtonType.YES)
	    {
		if ((keyFileChooser != null)  && (keyFileChooser.getSelectedFiles() != null))
		{
		    if (keyFileChooser.getSelectedFiles().length > 0)
		    {
			new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC);
			ArrayList<Path> pathList = finalCrypt.getPathList(keyFileChooser.getSelectedFiles());
//			boolean delete = true;
			boolean returnpathlist = false;
			String pattern1 = "glob:*";

			tab.getSelectionModel().select(1);
			log("\r\nDeleting selecttion started\r\n\r\n", true, true, true, false, false);
			finalCrypt.deleteSelection(pathList, keyFCPath, MySimpleFCFileVisitor.DELETE, returnpathlist, pattern1, false);
			log("\r\nDeleting selection finished\r\n\r\n", true, true, true, false, false);
			//					Validate.getFCPath(UI ui, String caller, Path path, boolean isKey, Path keyPath,    boolean disabledMAC,	boolean report)
			Path path = Paths.get("."); keyFCPath = Validate.getFCPath(   ui,	     "",      path,         false,         path, finalCrypt.disabledMAC,         true);
			updateFileChoosers(true, false, true, true, false, true); // keyFileDeleteButtonActionPerformed()
		    }
		}
	    } else
	    {
		new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
		new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);
	    }
	});
    }                                               

    private void cursorWait()
    {
            tgtFileChooser.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
            keyFileChooser.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));        
    }
    
    private void cursorDefault()
    {
            tgtFileChooser.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
            keyFileChooser.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));        
    }
    
    private String getSelectedPatternFromFileChooser( javax.swing.filechooser.FileFilter fileFilter)
    {
        negatePattern = false; String patternLocal = "glob:*";
        String desc = "*";
        if ( fileFilter != null ) {desc = fileFilter.getDescription();}
        javax.swing.filechooser.FileNameExtensionFilter ef = null;
        try { ef = (javax.swing.filechooser.FileNameExtensionFilter) tgtFileChooser.getFileFilter(); } catch (ClassCastException exc) {        }
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
    synchronized private void targetFileChooserActionPerformed(java.awt.event.ActionEvent evt)                                                 
    {	
        Platform.runLater(() ->
	{
	    this.fileProgressBar.setProgress(0);
	    this.filesProgressBar.setProgress(0);
	});
	
        if ((tgtFileChooser != null)  && (tgtFileChooser.getSelectedFiles() != null) && ( tgtFileChooser.getSelectedFiles().length == 1 ))
        {
	    if (keyFCPath == null)
	    {
//							Validate.getFCPath(UI ui, String caller, Path path, boolean isKey, Path keyPath,    boolean disabledMAC,	boolean report)
		Path path = Paths.get("."); keyFCPath = Validate.getFCPath(   ui,	     "",      path,         false,         path, finalCrypt.disabledMAC,         true);
	    }
	    Path targetPath = tgtFileChooser.getSelectedFile().toPath();
	    
//					   getFCPath(UI ui,  String caller,  Path path,  boolean isKey,   Path keyPath,    boolean disabledMAC,	boolean report)
	    FCPath targetFCPath = Validate.getFCPath(this,		"", targetPath,		 false, keyFCPath.path,	finalCrypt.disabledMAC,		  true);
	    
	    if ((targetFCPath.type == FCPath.DEVICE) || (targetFCPath.type == FCPath.DEVICE_PROTECTED))
	    {
		tab.getSelectionModel().select(1);
		DeviceManager deviceManagerLocal = new DeviceManager(this); deviceManagerLocal.start(); deviceManagerLocal.printGPT(targetFCPath);
		targetFCPathList = new FCPathList<FCPath>(); updateDashboard(targetFCPathList);
		Platform.runLater(() ->
		{
		    encryptButton.setDisable(true); enableClocks(false, false); decryptButton.setDisable(true);
//		    keyButton.setDisable(true); keyButton.setTextFill(Color.GREY); keyButton.setText(CREATE_KEY); keyButton.setVisible(false);
		});
	    }
	    else // Not a Device
	    {
//							device  minsize	 symlink  writable  status
		if ((targetFCPath.isValidFile) || (targetFCPath.type == FCPath.SYMLINK))
		{
		    if	( (targetFCPath.isEncrypted) && ( targetFCPath.isDecryptable ) && ( keyFCPath != null ) && (( keyFCPath.isValidKey ) || ( keyFCPath.type == FCPath.DIRECTORY ) && (keyFCPath.isValidKeyDir)) )
		    {
			Thread decryptThread = new Thread(() ->
			{
			    FCPathList<FCPath> targetFCPathList1 = new FCPathList<FCPath>();
			    FCPathList<FCPath> filteredTargetFCPathList = new FCPathList<FCPath>();
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
			Thread openThread = new Thread(() ->
			{
			    new Sound().play(this, Audio.SND_OPEN,Audio.AUDIO_CODEC);
			    try { Desktop.getDesktop().open(targetFCPath.path.toFile()); } catch (IOException ex) { log("Error: Desktop.getDesktop().open(file); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
			});
			openThread.setName("openThread");
			openThread.setDaemon(true);
			openThread.start();
		    }
		    
		    
		    targetFCPathList = new FCPathList<FCPath>(); updateDashboard(targetFCPathList);
		    Platform.runLater(() ->
		    {
			encryptButton.setDisable(true); enableClocks(false, false); decryptButton.setDisable(true);
//			keyButton.setDisable(true);  keyButton.setTextFill(Color.GREY); keyButton.setText(CREATE_KEY); keyButton.setVisible(false);
		    });
		} // Not a device / file or symlink
	    }
        }
	else // No items selected
	{
	    encryptButton.setDisable(true); enableClocks(false, false); decryptButton.setDisable(true);
	}
	tgtFileChooser.setFileFilter(nonFinalCryptFilter); tgtFileChooser.setFileFilter(tgtFileChooser.getAcceptAllFileFilter()); // Resets rename due to doucle click file		
    }                                                

//  Doubleclicked item
    synchronized private void keyFileChooserActionPerformed(java.awt.event.ActionEvent evt)                                                  
    {                                                      
//	test("ACT: " + evt + " " + Calendar.getInstance().getTimeInMillis() + "\r\n");
        this.fileProgressBar.setProgress(0);
        this.filesProgressBar.setProgress(0);
        if ((keyFileChooser != null)  && (keyFileChooser.getSelectedFile() != null))
        {
	    if (( keyFCPath.type == FCPath.DEVICE ) || ( keyFCPath.type == FCPath.DEVICE_PROTECTED ))
	    {
		tab.getSelectionModel().select(1);
		DeviceManager deviceManager = new DeviceManager(this); deviceManager.start(); deviceManager.printGPT(keyFCPath);
		targetFCPathList = new FCPathList<FCPath>(); this.updateDashboard(targetFCPathList);
		Platform.runLater(() ->
		{
		    encryptButton.setDisable(true); enableClocks(false, false); decryptButton.setDisable(true);
//		    keyButton.setDisable(true); keyButton.setTextFill(Color.GREY); keyButton.setText(CREATE_KEY); keyButton.setVisible(false);
		});
	    }
//					  ui	cll path				       isKey    device  minsize  symlink  writable status
	    else if (Validate.isValidFile(this, "", keyFileChooser.getSelectedFile().toPath(), true,     false,      1L, true,    false,  true))
	    {
		GUIFX guifx = this;
		Thread openThread = new Thread(() ->
		{
		    new Sound().play(guifx, Audio.SND_OPEN,Audio.AUDIO_CODEC);
		    try { Desktop.getDesktop().open(keyFCPath.path.toFile()); } catch (IOException ex) { log("Error: Desktop.getDesktop().open(keyFileChooser.getSelectedFile()); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
		});
		openThread.setName("openThread");
		openThread.setDaemon(true);
		openThread.start();
		
		targetFCPathList = new FCPathList<FCPath>(); this.updateDashboard(targetFCPathList);
		Platform.runLater(() ->
		{
		    encryptButton.setDisable(true); enableClocks(false, false); decryptButton.setDisable(true);
//		    keyButton.setDisable(true); keyButton.setTextFill(Color.GREY); keyButton.setText(CREATE_KEY); keyButton.setVisible(false);
		});
	    }
        }
	else
	{
	    encryptButton.setDisable(true); enableClocks(false, false); decryptButton.setDisable(true);
//	    keyButton.setDisable(true); keyButton.setTextFill(Color.GREY); keyButton.setText(CREATE_KEY); keyButton.setVisible(false);
	}	
        keyFileChooser.setFileFilter(nonFinalCryptFilter); keyFileChooser.setFileFilter(keyFileChooser.getAcceptAllFileFilter()); // Resets rename due to double click file
    }

/////////////////////////////////////////////////////////////////////////////////////////////
    
//  FileChooser Listener methods
    synchronized private void targetFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)
    {
	if ((!processRunning ) && (evt.getPropertyName().equals("SelectedFilesChangedProperty")))
	{
//	    remainingTimeHeaderLabel.setVisible(false); remainingTimeLabel.setVisible(false);
//	    elapsedTimeHeaderLabel.setVisible(false); elapsedTimeLabel.setVisible(false);
//	    totalTimeHeaderLabel.setVisible(false); totalTimeLabel.setVisible(false);
	    new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
//	    play(SND_BUTTON, AUDIO_CODEC);
	    tgtFileChooserPropertyCheck(true);
	}
	else if (evt.getPropertyName().equals("directoryChanged"))
	{
	    Platform.runLater(() -> 
	    {
		if ( (System.getProperty("os.name").toLowerCase().indexOf("mac") == -1)) // Again due to Mac OSX
		{
		    Timeline timeline = new Timeline(new KeyFrame( Duration.millis(100), ae ->
		    {
			tgtFileChooser.setFileFilter(keyFileChooser.getAcceptAllFileFilter());
			tgtFileChooser.updateUI();
			tgtFileChooserComponentAlteration(tgtFileChooser, false);
		    })); timeline.play();
		}
	    });
	}
    }
    
    synchronized private void keyFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)
    {
//	test("ACT: " + evt + " " + Calendar.getInstance().getTimeInMillis() + "\r\n");
//	test("Prop: " + evt.getPropertyName() + "\r\n");
	if ((!processRunning ) && (evt.getPropertyName().equals("SelectedFilesChangedProperty")))
	{
//	    remainingTimeHeaderLabel.setVisible(false); remainingTimeLabel.setVisible(false);
//	    elapsedTimeHeaderLabel.setVisible(false); elapsedTimeLabel.setVisible(false);
//	    totalTimeHeaderLabel.setVisible(false); totalTimeLabel.setVisible(false);
	    new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
//	    play(SND_BUTTON, AUDIO_CODEC);	    
	    keyFileChooserPropertyCheck();
	}
	else if (evt.getPropertyName().equals("directoryChanged"))
	{
	    if ( (System.getProperty("os.name").toLowerCase().indexOf("mac") == -1)) // Again due to Mac OSX
	    {
		keyFileChooser.setFileFilter(tgtFileChooser.getAcceptAllFileFilter()); keyFileChooser.updateUI(); keyFileChooserComponentAlteration(keyFileChooser, false);
	    }	    
	    Timeline timeline = new Timeline(new KeyFrame( Duration.millis(100), ae ->
	    {
		keyFileChooserPropertyCheck();
//		    test("Path: " + keyFileChooser.getCurrentDirectory().getAbsolutePath() + "\r\n");
	    })); timeline.play();
	}
    }
    
    synchronized private void tgtFileChooserPropertyCheck(boolean status)
    {
	Platform.runLater(() -> 
	{
	    if ((!processRunning ))
	    {
		MySimpleFCFileVisitor.running = false;		    
//		Platform.runLater(() -> 
//		{
		    encryptButton.setDisable(true);
		    decryptButton.setDisable(true);
//		    keyButton.setDisable(true);
		    pauseToggleButton.setDisable(true);
		    stopButton.setDisable(true);
		    enableClocks(false, false); 

		    fileProgressBar.setProgress(0);
		    filesProgressBar.setProgress(0);

    //		remainingTimeHeaderLabel.setVisible(false); remainingTimeLabel.setVisible(false);
    //		elapsedTimeHeaderLabel.setVisible(false); elapsedTimeLabel.setVisible(false);
    //		totalTimeHeaderLabel.setVisible(false); totalTimeLabel.setVisible(false);
//		});

		ArrayList<Path> targetPathList = new ArrayList<>(); targetPathList.clear();
//		tgtFileDeleteButton.setEnabled(false);
		tgtFileDeleteButton2.setDisable(true);

		// En/Disable FileChooser deletebutton
		if (
			(tgtFileChooser != null) &&
			(tgtFileChooser.getSelectedFile() != null) &&
			(tgtFileChooser.getSelectedFile().toPath() != null) &&
			(
			    (Files.isRegularFile( tgtFileChooser.getSelectedFile().toPath(), LinkOption.NOFOLLOW_LINKS)) ||
			    (Files.isDirectory(tgtFileChooser.getSelectedFile().toPath()))
			) 
		   )
		{
//		    tgtFileDeleteButton.setEnabled(true);
		    tgtFileDeleteButton2.setDisable(false);
		}
		else
		{
//		    tgtFileDeleteButton.setEnabled(false);
		    tgtFileDeleteButton2.setDisable(true);
		}

		targetFCPathList = new FCPathList<FCPath>();// targetFCPathList.clear();
		final FCPathList<FCPath> targetFCPathList2 = targetFCPathList;
		final UI ui = this;
		if (updateDashboardTaskTimer != null) { updateDashboardTaskTimer.cancel(); updateDashboardTaskTimer.purge(); }

		// All Valid
		if  (
			    (tgtFileChooser != null) && (tgtFileChooser.getSelectedFiles() != null) && (tgtFileChooser.getSelectedFiles().length > 0)
			&&  ((keyFCPath != null) && (keyFCPath.isKey) && (keyFCPath.isValidKey))
			||  ((keyFCPath != null) && (keyFCPath.type == FCPath.DIRECTORY) && (keyFCPath.isValidKeyDir) )
		    )
		{
		    Validate.bytesCount = 0;

		    // Gather User Selection in list
		    for (File file:tgtFileChooser.getSelectedFiles()) { targetPathList.add(file.toPath()); }
//		    test("list: " + targetPathList.size() + "\r\n");


	//		Get Globbing Pattern String
		    pattern = "glob:*"; // try { pattern = getSelectedPatternFromFileChooser( targetFileChooser.getFileFilter()); } catch (ClassCastException exc) {  }

		    // UPdate Dashboard during buildSelection

		    updateDashboardTask = new TimerTask() { @Override public void run() { updateDashboard(targetFCPathList2); }};
		    updateDashboardTaskTimer = new java.util.Timer();
		    updateDashboardTaskTimer.schedule(updateDashboardTask, 250L, 250L);

		    // Scanning animation on main progressbar
//		    Platform.runLater(() ->
//		    {
			showPasswordCheckBox.setVisible(true);
			if ((showPasswordCheckBox.isVisible()) && (pwdField.getText().length() == 0)) { passwordHeaderLabel.setText(password_optional); } else { passwordHeaderLabel.setText(password_set); }
			pwdField.setDisable(true);
			pwdtxtField.setDisable(true);
			pwdField.setVisible(! showPasswordCheckBox.isSelected());
			pwdtxtField.setVisible(showPasswordCheckBox.isSelected());
			finalCrypt.setPwd(pwdField.getText()); finalCrypt.setPwdBytes(pwdField.getText()); finalCrypt.resetPwdPos(); finalCrypt.resetPwdBytesPos();

			keyImageView.setOpacity(0.8);
			if ((targetPathList != null) && (targetPathList.size() > 0)) { filesProgressBar.setVisible(true); filesProgressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS); }

			new Sound().play(this, Audio.SND_READY,Audio.AUDIO_CODEC);
			
			ugMessage = new Message(scanning_files, 64, false, false, false, false, Voice.VOI_SCANNING_FILES, 0);
//			userGuidanceMessage(scanning_files, 64, false, false, false, false, Voice.VOI_SCANNING_FILES, 0);
			userGuidanceMessage(ugMessage);

			Timeline timeline = new Timeline(new KeyFrame( Duration.millis(100), ae -> // Give scanner a little time to stop
			{
			    Thread buildSelectionThread = new Thread(() -> // Relaxed interruptable thread
			    {
	//				 buildSelection(UI ui, ArrayList<Path> pathList, FCPath keyFCPath, FCPathList<FCPath> targetFCPathList, boolean symlink, String pattern, boolean negatePattern,	   boolean disabledMAC, boolean status)
				Validate.buildSelection(ui,		 targetPathList,	keyFCPath,	     targetFCPathList2,		symlink,	pattern,	 negatePattern,	finalCrypt.disabledMAC,		false);
			    }); buildSelectionThread.setName("buildSelectionThread"); buildSelectionThread.setDaemon(true); buildSelectionThread.start();
			})); timeline.play();
//		    });
		}
		else // No valid selection on both FileChoosers
		{		
//		    test("Not all valid: " + targetPathList.size() + "\r\n");
		    if	(
				((keyFCPath != null) && (keyFCPath.isKey) && (keyFCPath.isValidKey))
			    ||  ((keyFCPath != null) && (keyFCPath.type == FCPath.DIRECTORY) && (keyFCPath.isValidKeyDir))
			)
		    {
			ugMessage = new Message(select_files, 64, false, true, false, false, Voice.VOI_SELECT_FILES, 0);
//			userGuidanceMessage(select_files, 64, false, true, false, false, Voice.VOI_SELECT_FILES, 0);
			userGuidanceMessage(ugMessage);
		    }
		    else
		    {
			new Sound().play(this, Audio.SND_SELECTINVALID,Audio.AUDIO_CODEC);
			ugMessage = new Message(select_key_dir, 64, false, false, true, false, Voice.VOI_SELECT_KEY_DIRECTORY, 0);
			userGuidanceMessage(ugMessage);
//			userGuidanceMessage(select_key_dir, 64, false, false, true, false, Voice.VOI_SELECT_KEY_DIRECTORY, 0);
			userGuidanceMessage(ugMessage);
		    }

		    targetFCPathList = new FCPathList<FCPath>();
		    buildReady(targetFCPathList, false);
		}
	    }
	});
    }
    
    synchronized private void keyFileChooserPropertyCheck() // getFCPath, checkModeReady
    {
        Platform.runLater(() ->
	{
	    keyHeaderLabel.setTextFill(Color.GREY); keyHeaderLabel.setText("");
	    keyNameLabel.setTextFill(Color.GREY); keyNameLabel.setText(""); keyNameLabelTooltip.setText("");
	    keySizeLabel.setTextFill(Color.GREY); keySizeHeaderLabel.setText(""); keySizeLabel.setText("");
	    checksumLabel.setTextFill(Color.GREY); checksumHeader.setText(""); checksumLabel.setText("");
	    if ( checksumTooltip != null ) { checksumTooltip.setText(""); Tooltip.uninstall(checksumLabel, checksumTooltip); }
	    
	    showPasswordCheckBox.setVisible(true);
	    if ((showPasswordCheckBox.isVisible()) && (pwdField.getText().length() == 0)) { passwordHeaderLabel.setText(password_optional); } else { passwordHeaderLabel.setText(password_set); }
	    pwdField.setVisible(! showPasswordCheckBox.isSelected());
	    pwdtxtField.setVisible(showPasswordCheckBox.isSelected());
	    pwdField.setDisable(false);
	    pwdtxtField.setDisable(false);
	    
	    keyImageView.setImage(KEY_MAP_IMAGE);
	    keyImageView.setOpacity(0.2);

	    keyWriteNameLabel.setVisible(true); keyWriteLabel.setVisible(true); keyWriteSizeLabel.setVisible(true);
	    keyMatchNameLabel.setVisible(true); keyReadLabel.setVisible(true); keyReadSizeLabel.setVisible(true);
	    keyMissingNameLabel.setVisible(true); keyMissingLabel.setVisible(true); keyMissingSizeLabel.setVisible(true);
//	});

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
		{
//		    keyFileDeleteButton.setEnabled(true);
		    keyFileDeleteButton2.setDisable(false);
		}
		else
		{
//		    keyFileDeleteButton.setEnabled(false);
		    keyFileDeleteButton2.setDisable(true);
		}

		// Set Buffer Size
		finalCrypt.setBufferSize(finalCrypt.getBufferSizeDefault());

		// Validate KeyFile
		if ((keyFileChooser != null) && (keyFileChooser.getSelectedFile() != null) && (keyFileChooser.getSelectedFiles().length == 1))
		{
		    Path keyPath;
		    if ( ! Files.isDirectory(keyFileChooser.getSelectedFiles()[0].toPath()))
		    {
			keyPath = keyFileChooser.getSelectedFiles()[0].toPath();
		    }
		    else
		    {
			keyPath = keyFileChooser.getCurrentDirectory().toPath();
		    }
    //				       getFCPath(UI ui, String caller,  Path path, boolean isKey, Path keyPath,    boolean disabledMAC, boolean report)
		    keyFCPath = Validate.getFCPath(this,	   "",	  keyPath,          true,      keyPath, finalCrypt.disabledMAC,          true);

//		    Platform.runLater(() -> 
//		    {
			if ((keyFCPath.isValidKey)) // Valid Key
			{
			    String keyitem = "";
			    if (keyFCPath.type == FCPath.FILE)		    { keyitem = file; }
			    else if (keyFCPath.type == FCPath.DIRECTORY)    { keyitem = directory; }
			    else					    { keyitem = FCPath.getTypeString(keyFCPath.type); }
			    
			    new Sound().play(this, Audio.SND_SELECTKEY,Audio.AUDIO_CODEC);
			    keyHeaderLabel.setTextFill(Color.GREENYELLOW); keyHeaderLabel.setText(key + " " + keyitem);
			    keyNameLabel.setTextFill(Color.GREENYELLOW); keyNameLabel.setText(keyFCPath.path.toAbsolutePath().toString()); keyNameLabelTooltip.setText(keyFCPath.path.toAbsolutePath().toString());
			    checksumLabel.setTextFill(Color.WHITESMOKE); checksumHeader.setText(""); checksumLabel.setText("");
			    if ( checksumTooltip != null )  { checksumTooltip.setText(""); Tooltip.uninstall(checksumLabel, checksumTooltip); }

			    keySizeLabel.setTextFill(Color.GREENYELLOW); keySizeHeaderLabel.setText("Size "); keySizeLabel.setText(Validate.getHumanSize(keyFCPath.size,1,"Bytes"));

			    showPasswordCheckBox.setVisible(true);
			    if ((showPasswordCheckBox.isVisible()) && (pwdField.getText().length() == 0)) { passwordHeaderLabel.setText(password_optional); } else { passwordHeaderLabel.setText(password_set); }
			    pwdField.setVisible(! showPasswordCheckBox.isSelected());
			    pwdtxtField.setVisible(showPasswordCheckBox.isSelected());
			    pwdField.setDisable(false);
			    pwdtxtField.setDisable(false);
			    finalCrypt.setPwd(pwdField.getText()); finalCrypt.setPwdBytes(pwdField.getText()); finalCrypt.resetPwdPos(); finalCrypt.resetPwdBytesPos();

			    keyImageView.setImage(KEY_FILE_IMAGE);
			    keyImageView.setOpacity(0.8);

			    tgtFileChooserPropertyCheck(true);
			}
			else // Not Valid Key
			{
			    if ((keyFCPath.type == FCPath.DIRECTORY) && (keyFCPath.isValidKeyDir))
			    {
				keyHeaderLabel.setTextFill(Color.GREENYELLOW); keyHeaderLabel.setText(key +" " + directory);
				keyNameLabel.setTextFill(Color.GREENYELLOW); keyNameLabel.setText(keyFCPath.path.toAbsolutePath().toString()); keyNameLabelTooltip.setText(keyFCPath.path.toAbsolutePath().toString());
//				keySizeLabel.setTextFill(Color.GREENYELLOW); keySizeLabel.setText(Validate.getHumanSize(keyFCPath.size,1));
				keySizeLabel.setTextFill(Color.GREENYELLOW); keySizeHeaderLabel.setText(""); keySizeLabel.setText("");

				checksumLabel.setTextFill(Color.GREY); checksumHeader.setText(""); checksumLabel.setText(""); checksumTooltip.setText(""); Tooltip.uninstall(checksumLabel, checksumTooltip);

				showPasswordCheckBox.setVisible(true);
				if ((showPasswordCheckBox.isVisible()) && (pwdField.getText().length() == 0)) { passwordHeaderLabel.setText(password_optional); } else { passwordHeaderLabel.setText(password_set); }
				pwdField.setVisible(! showPasswordCheckBox.isSelected());
				pwdtxtField.setVisible(showPasswordCheckBox.isSelected());
				pwdField.setDisable(false);
				pwdtxtField.setDisable(false);
				finalCrypt.setPwd(pwdField.getText()); finalCrypt.setPwdBytes(pwdField.getText()); finalCrypt.resetPwdPos(); finalCrypt.resetPwdBytesPos();

				keyImageView.setImage(KEY_MAP_IMAGE);
				keyImageView.setOpacity(0.8);

				tgtFileChooserPropertyCheck(true);
			    }
			    else
			    {
				String keyitem = "";
				if (keyFCPath.type == FCPath.FILE)		{ keyitem = file; }
				else if (keyFCPath.type == FCPath.DIRECTORY)    { keyitem = directory; }
				else						{ keyitem = FCPath.getTypeString(keyFCPath.type); }
				
				new Sound().play(this, Audio.SND_SELECTINVALID,Audio.AUDIO_CODEC);
				keyHeaderLabel.setTextFill(Color.ORANGE); keyHeaderLabel.setText(key + " " + keyitem);
				keyNameLabel.setTextFill(Color.ORANGE); keyNameLabel.setText(keyFCPath.path.toAbsolutePath().toString()); keyNameLabelTooltip.setText(keyFCPath.path.toAbsolutePath().toString());
				if (keyFCPath.type != FCPath.FILE)	{ keyHeaderLabel.setTextFill(Color.ORANGERED); }
				else				{ keyHeaderLabel.setTextFill(Color.ORANGE); }
				if (keyFCPath.type != FCPath.DIRECTORY)
				{
				    if ( keyFCPath.size < FCPath.KEY_SIZE_MIN ) { keySizeLabel.setTextFill(Color.ORANGERED); } else { keySizeLabel.setTextFill(Color.ORANGE); } keySizeHeaderLabel.setText("Size "); keySizeLabel.setText(Validate.getHumanSize(keyFCPath.size,1,"Bytes"));
				}
				checksumHeader.setText(""); checksumLabel.setText(""); checksumTooltip.setText(""); Tooltip.uninstall(checksumLabel, checksumTooltip);

				if ( keyFCPath != null ) { keyFCPath.isValidKey = false; }

				showPasswordCheckBox.setVisible(true);
				if ((showPasswordCheckBox.isVisible()) && (pwdField.getText().length() == 0)) { passwordHeaderLabel.setText(password_optional); } else { passwordHeaderLabel.setText(password_set); }
				pwdField.setVisible(! showPasswordCheckBox.isSelected());
				pwdtxtField.setVisible(showPasswordCheckBox.isSelected());
				pwdField.setDisable(false);
				pwdtxtField.setDisable(false);			

				if (keyFCPath.type == FCPath.DIRECTORY) { keyImageView.setImage(KEY_MAP_IMAGE); } else { keyImageView.setImage(KEY_FILE_IMAGE); }
				keyImageView.setOpacity(0.2);

				ugMessage = new Message(select_key_dir, 64, false, false, true, false, Voice.VOI_SELECT_KEY_DIRECTORY, 0);
//				userGuidanceMessage(select_key_dir, 64, false, false, true, false, Voice.VOI_SELECT_KEY_DIRECTORY, 0);
				userGuidanceMessage(ugMessage);

				targetFCPathList = new FCPathList<FCPath>();
				buildReady(targetFCPathList, false);
			    }			
			}
//		    });

		    // Checksum Calculation
		    if ((keyFCPath.isValidKey)) // Valid Key
		    {
			if ( keyFCPath.size < (1024L * 1024L * 1024L * 1L) )
			{
//			    Platform.runLater(() -> 
//			    {			    
				checksumLabel.setTextFill(Color.WHITESMOKE);
				checksumHeader.setText(checksum + " (" + FinalCrypt.HASH_ALGORITHM_NAME + ")"); checksumLabel.setText(calculating + "...");
				Tooltip.uninstall(checksumLabel, checksumTooltip);
				calculateChecksum();
//			    });
			}
			else
			{
//			    Platform.runLater(() -> 
//			    {
				checksumLabel.setTextFill(Color.WHITESMOKE);
				checksumHeader.setText(checksum + " (" + FinalCrypt.HASH_ALGORITHM_NAME + ")"); checksumLabel.setText(click_for_checksum);
				Tooltip.uninstall(checksumLabel, checksumTooltip);
//			    });
			}
		    }
		}
		else // No Items Selected
		{
//		    log("CC Sel Not Valid\r\n");
		    MySimpleFCFileVisitor.running = false;
//		    try { Thread.sleep(100); } catch (InterruptedException ex) {  }

		    Path keyPath = keyFileChooser.getCurrentDirectory().toPath();
//					 getFCPath(UI ui, String caller,  Path path, boolean isKey, Path keyPath,    boolean disabledMAC, boolean report)
		    keyFCPath = Validate.getFCPath(this,		   "",	  keyPath,          true,      keyPath, finalCrypt.disabledMAC,          true);

		    if ((keyFCPath.type == FCPath.DIRECTORY) && (keyFCPath.isValidKeyDir))
		    {
//			Platform.runLater(() ->
//			{
			    keyHeaderLabel.setTextFill(Color.GREENYELLOW); keyHeaderLabel.setText(key + " " + directory);
			    keyNameLabel.setTextFill(Color.GREENYELLOW); keyNameLabel.setText(keyFCPath.path.toAbsolutePath().toString()); keyNameLabelTooltip.setText(keyFCPath.path.toAbsolutePath().toString());
			    keySizeLabel.setTextFill(Color.GREENYELLOW); keySizeHeaderLabel.setText(""); keySizeLabel.setText("");
			    checksumLabel.setTextFill(Color.GREY); checksumHeader.setText(""); checksumLabel.setText(""); checksumTooltip.setText(""); Tooltip.uninstall(checksumLabel, checksumTooltip);
			    Timeline timeline = new Timeline(new KeyFrame( Duration.millis(100), ae -> { tgtFileChooserPropertyCheck(true); })); timeline.play();
//			});
		    }
		    else
		    {
//			Platform.runLater(() ->
//			{
			    String keyitem = "";
			    if (keyFCPath.type == FCPath.FILE)		{ keyitem = file; }
			    else if (keyFCPath.type == FCPath.DIRECTORY)    { keyitem = directory; }
			    else						{ keyitem = FCPath.getTypeString(keyFCPath.type); }
			    
			    new Sound().play(this, Audio.SND_SELECTINVALID,Audio.AUDIO_CODEC);
			    keyHeaderLabel.setTextFill(Color.ORANGE); keyHeaderLabel.setText(key + " " + keyitem);
			    keyNameLabel.setTextFill(Color.ORANGE); keyNameLabel.setText(keyFCPath.path.toAbsolutePath().toString()); keyNameLabelTooltip.setText(keyFCPath.path.toAbsolutePath().toString());
			    if (keyFCPath.type != FCPath.FILE)	{ keyHeaderLabel.setTextFill(Color.ORANGERED); }
			    else					{ keyHeaderLabel.setTextFill(Color.ORANGE); }
			    if ( keyFCPath.size < FCPath.KEY_SIZE_MIN ) { keySizeLabel.setTextFill(Color.ORANGERED); } else { keySizeLabel.setTextFill(Color.ORANGE); } keySizeHeaderLabel.setText(""); keySizeLabel.setText("");
			    checksumHeader.setText(""); checksumLabel.setText(""); checksumTooltip.setText(""); Tooltip.uninstall(checksumLabel, checksumTooltip);
//
			    ugMessage = new Message(select_key_dir, 64, false, false, true, false, Voice.VOI_SELECT_KEY_DIRECTORY, 0);
//			    userGuidanceMessage(select_key_dir, 64, false, false, true, false, Voice.VOI_SELECT_KEY_DIRECTORY, 0);
			    userGuidanceMessage(ugMessage);

			    targetFCPathList = new FCPathList<FCPath>();
			    buildReady(targetFCPathList, true);
//			});
		    }
		}
	    }
	});
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
			    MessageDigest messageDigest = null; try { messageDigest = MessageDigest.getInstance(FinalCrypt.HASH_ALGORITHM_NAME); } catch (NoSuchAlgorithmException ex) { log("Error: NoSuchAlgorithmException: MessageDigest.getInstance(\" "+ FinalCrypt.HASH_ALGORITHM_NAME + "\")\r\n", true, true, true, true, false);}
			    int x = 0;
			    while (( ! keySourceChecksumReadEnded ) && ( ! keySourceChecksumReadCanceled ))
			    {
				try (final SeekableByteChannel readKeySourceChannel = Files.newByteChannel(keyFCPath.path, FinalCrypt.getEnumSet(EnumSet.of(StandardOpenOption.READ,StandardOpenOption.SYNC))))
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
				    new Sound().play(this, Audio.SND_SELECT,Audio.AUDIO_CODEC);
				    checksumLabel.setTextFill(Color.GREENYELLOW);
				    checksumHeader.setText(checksum + " (" + FinalCrypt.HASH_ALGORITHM_NAME + ")"); checksumLabel.setText(hashString);
				    if ( checksumTooltip != null ) { checksumTooltip.setText(hashString + "\r\n\r\n" + calculate_checksum_tooltip); }
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
		}
	    });
	}
    }
    
    @FXML private void checksumLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	Platform.runLater(() ->
	{ 
	    if ( event.getButton() == MouseButton.PRIMARY )
	    {
		if ((keyFCPath != null) && (keyFCPath.isValidKey))
		{
		    new Sound().play(this, Audio.SND_SELECT,Audio.AUDIO_CODEC);
		    checksumLabel.setTextFill(Color.WHITESMOKE);
		    checksumHeader.setText("Checksum (" + FinalCrypt.HASH_ALGORITHM_NAME + ")"); checksumLabel.setText("Calculating..."); checksumTooltip.setText("");
		    Platform.runLater(() ->	{ calculateChecksum(); });
		}
	    }
	    else if ( event.getButton() == MouseButton.SECONDARY )
	    {
		if ((keyFCPath != null) && (keyFCPath.isValidKey))
		{
		    new Sound().play(this, Audio.SND_SELECT,Audio.AUDIO_CODEC);
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

    @Override public void buildReady(FCPathList<FCPath> fcPathListParam, boolean validBuild)
    {
	Platform.runLater(() ->
	{
	    filesProgressBar.setProgress(0); filesProgressBar.setVisible(false); pwdField.setDisable(false); pwdtxtField.setDisable(false);
	});
	if (updateDashboardTaskTimer != null) { updateDashboardTaskTimer.cancel(); updateDashboardTaskTimer.purge(); }
	MySimpleFCFileVisitor.running = false;
	isCalculatingCheckSum = false;
	
	if (fcPathListParam.size() > 0)
	{
//	    test("buildReady > 0\r\n");
	    updateDashboard(fcPathListParam);
	    checkModeReady(fcPathListParam,validBuild);
	}
	else
	{
//	    test("buildReady == 0\r\n");
	    fcPathListParam.clearStats();
	    encryptableList = new FCPathList<FCPath>();
	    updateDashboard(fcPathListParam);
	    checkModeReady(fcPathListParam,validBuild);
	}
//	if ( validBuild ) { textLabelBlurMessage("FinalCrypt", 500); }
    }

    private void updateDashboard(FCPathList<FCPath> targetFCPathList)
    {
//	test(s + "\r\n" + targetFCPathList.getStats());
	Platform.runLater(() -> 
	{
	    // Skipping / Info Column
	    if ( (targetFCPathList.emptyFiles > 0) || (targetFCPathList.symlinkFiles > 0)  || (targetFCPathList.unreadableFiles > 0) || (targetFCPathList.unwritableFiles > 0))
	    { targetWarningNameLabel.setTextFill(Color.ORANGE); targetWarningNameLabel.setText(skipping); } else { targetWarningNameLabel.setTextFill(Color.GRAY); targetWarningNameLabel.setText("Invalid"); }
	    
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
	    unreadableFilesLabel.getTooltip().setText(click_to_list_unreadable_files + " (" + Validate.getHumanSize(targetFCPathList.unreadableFilesSize,1,bytes) + ")");
//	    unreadableFilesSizeLabel.setText("Click to list unreadable files (" + Validate.getHumanSize(targetFCPathList.unreadableFilesSize,1,"Bytes") + ")");
	    unwritableFilesLabel.setText(Long.toString(targetFCPathList.unwritableFiles));
	    unwritableFilesLabel.getTooltip().setText(click_to_list_unwritable_files + " (" + Validate.getHumanSize(targetFCPathList.unwritableFilesSize,1,bytes) + ")");
//	    unwritableFilesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.unwritableFilesSize,1,"Bytes"));
	    hiddenFilesLabel.setText(Long.toString(targetFCPathList.hiddenFiles));
	    hiddenFilesLabel.getTooltip().setText(click_to_list_hidden_files + " (" + Validate.getHumanSize(targetFCPathList.hiddenFilesSize,1,bytes) + ")");
//	    hiddenFilesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.hiddenFilesSize,1,"Bytes"));

//	    Decrypted Column
	    decryptedLabel.setText(Long.toString(targetFCPathList.decryptedFiles));
	    decryptedSizeLabel.setText(Validate.getHumanSize(targetFCPathList.decryptedFilesSize,1,bytes));

	    encryptableLabel.setText(Long.toString(targetFCPathList.encryptableFiles));
	    encryptableSizeLabel.setText(Validate.getHumanSize(targetFCPathList.encryptableFilesSize,1,bytes));

//	    newEncryptedLabel.setText(Long.toString(targetFCPathList.newEncryptedFiles));
//	    newEncryptedSizeLabel.setText(Validate.getHumanSize(targetFCPathList.newEncryptedFilesSize,1,bytes));
	    
//	    encryptRemainingLabel.setText(Long.toString(targetFCPathList.encryptRemainingFiles));
//	    encryptRemainingSizeLabel.setText(Validate.getHumanSize(targetFCPathList.encryptRemainingFilesSize,1,bytes));

	    if ( targetFCPathList.unEncryptableFiles > 0 ) { unencryptableLabel.setTextFill(Color.RED); unencryptableSizeLabel.setTextFill(Color.RED); } else { unencryptableLabel.setTextFill(Color.valueOf("LIGHTGREY")); unencryptableSizeLabel.setTextFill(Color.valueOf("LIGHTGREY")); }
	    unencryptableLabel.setText(Long.toString(targetFCPathList.unEncryptableFiles));
	    unencryptableSizeLabel.setText(Validate.getHumanSize(targetFCPathList.unEncryptableFilesSize,1,bytes));

//	    Encrypted Column
	    encryptedLabel.setText(Long.toString(targetFCPathList.encryptedFiles));
	    encryptedSizeLabel.setText(Validate.getHumanSize(targetFCPathList.encryptedFilesSize,1,bytes));

	    decryptableLabel.setText(Long.toString(targetFCPathList.decryptableFiles));
	    decryptableSizeLabel.setText(Validate.getHumanSize(targetFCPathList.decryptableFilesSize,1,bytes));

//	    newDecryptedLabel.setText(Long.toString(targetFCPathList.newDecryptedFiles));
//	    newDecryptedSizeLabel.setText(Validate.getHumanSize(targetFCPathList.newDecryptedFilesSize,1,bytes));
	    
//	    decryptRemainingLabel.setText(Long.toString(targetFCPathList.decryptRemainingFiles));
//	    decryptRemainingSizeLabel.setText(Validate.getHumanSize(targetFCPathList.decryptRemainingFilesSize,1,bytes));

	    if ( targetFCPathList.unDecryptableFiles > 0 ) { undecryptableLabel.setTextFill(Color.RED); undecryptableSizeLabel.setTextFill(Color.RED); } else { undecryptableLabel.setTextFill(Color.valueOf("LIGHTGREY")); undecryptableSizeLabel.setTextFill(Color.valueOf("LIGHTGREY")); }
	    undecryptableLabel.setText(Long.toString(targetFCPathList.unDecryptableFiles));
	    undecryptableSizeLabel.setText(Validate.getHumanSize(targetFCPathList.unDecryptableFilesSize,1,bytes));

//	    Totals Column
	    totalFilesLabel.setText(Long.toString(targetFCPathList.files));
	    filesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.filesSize,1,bytes));

//	    validFilesLabel.setText(Long.toString(targetFCPathList.validFiles));
//	    validFilesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.validFilesSize,1,bytes));

	    invalidFilesLabel.setText(Long.toString(targetFCPathList.files - targetFCPathList.validFiles));
	    invalidFilesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.filesSize - targetFCPathList.validFilesSize,1,bytes));

//	    validDevicesLabel.setText(Long.toString(targetFCPathList.validDevices));
//	    validDevicesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.validDevicesSize,1,bytes));
//
//	    validPartitionsLabel.setText(Long.toString(targetFCPathList.validPartitions));
//	    validPartitionsSizeLabel.setText(Validate.getHumanSize(targetFCPathList.validPartitionsSize,1,bytes));

	    keyWriteLabel.setText(Long.toString(targetFCPathList.writeAutoKeyFiles));
	    keyWriteSizeLabel.setText(Validate.getHumanSize(targetFCPathList.writeAutoKeyFilesSize,1,bytes));

	    keyReadLabel.setText(Long.toString(targetFCPathList.matchedAutoKeyFiles));
	    keyReadSizeLabel.setText(Validate.getHumanSize(targetFCPathList.matchedAutoKeyFilesSize,1,bytes));

	    if ( targetFCPathList.unmatchedAutoKeyFiles > 0 ) { keyMissingLabel.setTextFill(Color.RED); keyMissingSizeLabel.setTextFill(Color.RED); } else { keyMissingLabel.setTextFill(Color.valueOf("LIGHTGREY")); keyMissingSizeLabel.setTextFill(Color.valueOf("LIGHTGREY")); }
	    keyMissingLabel.setText(Long.toString(targetFCPathList.unmatchedAutoKeyFiles));
	    keyMissingSizeLabel.setText(Validate.getHumanSize(targetFCPathList.unmatchedAutoKeyFilesSize,1,bytes));

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
	    enableClocks(false, false); 
	});
    }
    synchronized private void checkModeReady(FCPathList<FCPath> targetFCPathList, boolean validBuild)
    {
	Platform.runLater(() ->
	{
	    if (!processRunning)
	    {
		encryptButton.setDisable(true);
		decryptButton.setDisable(true);
//		keyButton.setDisable(true);
		pauseToggleButton.setDisable(true);
		stopButton.setDisable(true);
		enableClocks(false, false); 
				
		if ((keyFCPath != null) && ((keyFCPath.isValidKey) || ((keyFCPath.type == FCPath.DIRECTORY ) && (keyFCPath.isValidKeyDir))))
		{
// ================================================================================================================================================================================================
// Building filtered lists
// ================================================================================================================================================================================================
		    // Decrypted Files
		    
		    if (targetFCPathList.decryptedFiles > 0)	{ decryptedList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.isDecrypted); } else { decryptedList = null; }
		    if (targetFCPathList.encryptableFiles > 0) // Encryptables
		    {
			new Sound().play(this, Audio.SND_SELECT,Audio.AUDIO_CODEC);
			encryptableList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.isEncryptable);
			encryptButton.setDisable(false); pauseToggleButton.setDisable(true); stopButton.setDisable(true); enableClocks(true, false);
			
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
			new Sound().play(this, Audio.SND_SELECT,Audio.AUDIO_CODEC);
			decryptableList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.isDecryptable);
			decryptButton.setDisable(false); pauseToggleButton.setDisable(true); stopButton.setDisable(true); enableClocks(true, false);
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

		    if (targetFCPathList.writeAutoKeyFiles > 0)	    { writeAutoKeyList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.needsWriteAutoKey); } else { writeAutoKeyList = null; }
		    if (targetFCPathList.matchedAutoKeyFiles > 0)   { readAutoKeyList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.matchedReadAutoKey); } else { readAutoKeyList = null; }
		    if (targetFCPathList.unmatchedAutoKeyFiles > 0)   { missingAutoKeyList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.unmatchedReadAutoKey); } else { missingAutoKeyList = null; }

// ================================================================================================================================================================================================
// Setting Buttons
// ================================================================================================================================================================================================

//		    Setting Device Buttons

		    if ((keyFCPath.type == FCPath.FILE) && (keyFCPath.isValidKey)) // Create Key Device Conditions
		    {
			if (targetFCPathList.validDevices > 0)
			{
//			    log("1 " + keyFCPath.getString());
			    createManualKeyList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE); // log("Create Manual Key List:\r\n" + createManualKeyList.getStats());
			    pauseToggleButton.setDisable(true); stopButton.setDisable(true);
//			    keyButton.setDisable(false); keyButton.setTextFill(Color.WHITE); keyButton.setText(CREATE_KEYDEV);
			}
//			else {  keyButton.setDisable(true);  keyButton.setTextFill(Color.GREY); keyButton.setText(CREATE_KEY); keyButton.setVisible(false); }
		    }
		    else if (keyFCPath.type == FCPath.DEVICE)
		    {
//			Clone Key Device
			if ((targetFCPathList.validDevices > 0) && (targetFCPathList.matchingKey == 0))
			{
			    cloneManualKeyList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE && fcPath.path.compareTo(keyFCPath.path) != 0); // log("Clone Key List:\r\n" + cloneKeyList.getStats());
			    pauseToggleButton.setDisable(true); stopButton.setDisable(true);
//			    keyButton.setDisable(false); keyButton.setTextFill(Color.WHITE); keyButton.setText(CLONE_KEYDEV);
			}
//			else {  keyButton.setDisable(true); keyButton.setTextFill(Color.GREY); keyButton.setText(CREATE_KEY); keyButton.setVisible(false); }
		    }
//		    else {  keyButton.setDisable(true);  keyButton.setTextFill(Color.GREY); keyButton.setText(CREATE_KEY); keyButton.setVisible(false); }

//		    Setting Encryption / Decryption Buttons
		    
		    if (validBuild)
		    {
			if ((! keyFCPath.isValidKey) && (keyFCPath.type != FCPath.DIRECTORY ))
			{
			    ugMessage = new Message(select_key_dir, 64, false, false, true, false, Voice.VOI_SELECT_KEY_DIRECTORY, 0);
			    userGuidanceMessage(ugMessage);
			}
			else
			{
			    if	((targetFCPathList.encryptedFiles > 0) && (targetFCPathList.decryptableFiles == 0))
			    {
				if (targetFCPathList.encryptedFiles > 0) { new Sound().play(this, Audio.SND_SELECTINVALID,Audio.AUDIO_CODEC); ugMessage = new Message(wrong_key_pass, 48, false, false, true, false, Voice.VOI_WRONG_KEY_OR_PASSWORD, 0); userGuidanceMessage(ugMessage); }
			    }
			    else
			    {
				if	((targetFCPathList.encryptableFiles == 0) && (targetFCPathList.decryptableFiles == 0))	{ ugMessage = new Message(select_files, 64, false, true, false, false, Voice.VOI_SELECT_FILES, 0); userGuidanceMessage(ugMessage); }
				else if ((targetFCPathList.encryptableFiles == 0) && (targetFCPathList.decryptableFiles > 0))	{ ugMessage = new Message(decrypt_files, 64, false, false, false, false, Voice.VOI_DECRYPT_FILES, 0); userGuidanceMessage(ugMessage); }
				else if ((targetFCPathList.encryptableFiles > 0) && (targetFCPathList.decryptableFiles == 0))	{ ugMessage = new Message(encrypt_files, 64, false, false, false, false, Voice.VOI_ENCRYPT_FILES, 0); userGuidanceMessage(ugMessage); }
				else if ((targetFCPathList.encryptableFiles > 0) && (targetFCPathList.decryptableFiles > 0))	{ ugMessage = new Message(en_decrypt_files, 64, false, false, false, false, Voice.VOI_ENCRYPT_OR_DECRYPT_FILES, 0); userGuidanceMessage(ugMessage); }
			    }
			}
		    }
		    else // In case just a build-reset is required
		    {
			if ((! keyFCPath.isValidKey) && (keyFCPath.type != FCPath.DIRECTORY ))
			{
			    ugMessage = new Message(select_key_dir, 64, false, false, true, false, Voice.VOI_SELECT_KEY_DIRECTORY, 0);
			    userGuidanceMessage(ugMessage);
			}
//			else
//			{
//			    if	((targetFCPathList.encryptedFiles > 0) && (targetFCPathList.decryptableFiles == 0))
//			    {
//				if (targetFCPathList.encryptedFiles > 0) { play(MP3_SND_SELECTINVALID); userGuidanceMessage(WRONG_KEY_PASS, 48, false, false, true, false, MP3_VOI_WRONG_KEY_OR_PASSWORD, 0); }
//			    }
//			    else
//			    {
//				if	((targetFCPathList.encryptableFiles == 0) && (targetFCPathList.decryptableFiles == 0))	{ userGuidanceMessage(SELECT_FILES, 64, false, true, false, false, MP3_VOI_SELECT_FILES, 0); }
//				else if ((targetFCPathList.encryptableFiles == 0) && (targetFCPathList.decryptableFiles > 0))	{ userGuidanceMessage(DECRYPT_FILES, 64, true, false, false, false, MP3_VOI_DECRYPT_FILES, 0); }
//				else if ((targetFCPathList.encryptableFiles > 0) && (targetFCPathList.decryptableFiles == 0))	{ userGuidanceMessage(ENCRYPT_FILES, 64, true, false, false, false, MP3_VOI_ENCRYPT_FILES, 0); }
//				else if ((targetFCPathList.encryptableFiles > 0) && (targetFCPathList.decryptableFiles > 0))	{ userGuidanceMessage(EN_DECRYPT_FILES, 64, true, false, false, false, MP3_VOI_ENCRYPT_OR_DECRYPT_FILES, 0); }
//			    }
//			}
		    }
		}
		else
		{
		    if ((keyFCPath != null) && (! keyFCPath.isValidKey) && (keyFCPath.type != FCPath.DIRECTORY ))
		    {
			ugMessage = new Message(select_key_dir, 64, false, false, true, false, Voice.VOI_SELECT_KEY_DIRECTORY, 0); userGuidanceMessage(ugMessage);
		    }
//		    else
//		    {
//			if	((targetFCPathList.encryptedFiles > 0) && (targetFCPathList.decryptableFiles == 0))
//			{
//			    if (targetFCPathList.encryptedFiles > 0) { play(MP3_SND_SELECTINVALID); userGuidanceMessage(WRONG_KEY_PASS, 48, false, false, true, false, MP3_VOI_WRONG_KEY_OR_PASSWORD, 0); }
//			}
//			else
//			{
//			    if	((targetFCPathList.encryptableFiles == 0) && (targetFCPathList.decryptableFiles == 0))	    { userGuidanceMessage(SELECT_FILES, 64, false, true, false, false, MP3_VOI_SELECT_FILES, 0); }
//			    else if ((targetFCPathList.encryptableFiles == 0) && (targetFCPathList.decryptableFiles > 0))   { userGuidanceMessage(DECRYPT_FILES, 64, true, false, false, false, MP3_VOI_DECRYPT_FILES, 0); }
//			    else if ((targetFCPathList.encryptableFiles > 0) && (targetFCPathList.decryptableFiles == 0))   { userGuidanceMessage(ENCRYPT_FILES, 64, true, false, false, false, MP3_VOI_ENCRYPT_FILES, 0); }
//			    else if ((targetFCPathList.encryptableFiles > 0) && (targetFCPathList.decryptableFiles > 0))    { userGuidanceMessage(EN_DECRYPT_FILES, 64, true, false, false, false, MP3_VOI_ENCRYPT_OR_DECRYPT_FILES, 0); }
//			}
//		    }
		    encryptButton.setDisable(true); enableClocks(false, false); decryptButton.setDisable(true);
//		    keyButton.setDisable(true);  keyButton.setTextFill(Color.GREY); keyButton.setText(CREATE_KEY); keyButton.setVisible(false); // Default enabler
		}
	    }
	});
    }

    synchronized public  FCPathList<FCPath> filter(ArrayList<FCPath> fcPathList, Predicate<FCPath> fcPath)
    {
	FCPathList<FCPath> result = new FCPathList<FCPath>();
	for (FCPath fcPathItem : fcPathList) { if (fcPath.test(fcPathItem)) { result.add(fcPathItem); } }
	return result;
    }
    
    synchronized public static Predicate<FCPath> isHidden() { return (FCPath fcPath) -> fcPath.isHidden; }
    
    synchronized public List<FCPath> filter(Predicate<FCPath> criteria, ArrayList<FCPath> list)
    {
	return list.stream().filter(criteria).collect(Collectors.<FCPath>toList());
    }
        
    public boolean tgtFileChooserComponentAlteration(Container container, boolean firsttime)
    {
	if ( (System.getProperty("os.name").toLowerCase().indexOf("mac") == -1)) // Again due to Mac OSX
	{
	    Component[] components = container.getComponents();
	    for (Component component : components)
	    {
		if(component instanceof Container) { component.setFont(FILE_CHOOSER_FONT); }
    //	    if ((component instanceof JButton)) { if (((JButton) component).getActionCommand().equalsIgnoreCase("New Folder")) { component.getParent().add(this.tgtFileDeleteButton); } } // Add Delete button

		if (component instanceof JToggleButton) // Click "details view" ToggleButton
		{
		    SwingUtilities.invokeLater(new Runnable() { public void run()
		    {
			if ( ! ((JToggleButton)component).isSelected() )
			{
			    if ( firsttime )
			    {
				TimerTask tgtFileChoosershowDetailsTask = new TimerTask() { @Override public void run()
				{
				    tgtFileChooser.setVisible(false);
				    ((JToggleButton)component).doClick();
				    tgtFileChooser.setVisible(true);
				}};
				Timer targetFileChoosershowDetailsTaskTimer = new java.util.Timer(); targetFileChoosershowDetailsTaskTimer.schedule(tgtFileChoosershowDetailsTask, 1000L); // Needs a delay for proper column width
			    }
			    else { ((JToggleButton)component).doClick(); }
			}
		    }});
		}
		if (component instanceof Container) { if( tgtFileChooserComponentAlteration((Container) component, firsttime) ) { return false; } }
	    }
	    return false;
	}
	return false;
    }

    public boolean keyFileChooserComponentAlteration(Container container, boolean firsttime)
    {
	if ( (System.getProperty("os.name").toLowerCase().indexOf("mac") == -1)) // Again due to Mac OSX
	{
	    Component[] components = container.getComponents();
	    for (Component component : components)
	    {
    //	    test("Component: " + component.getClass().getTypeName().toString() + "\r\n");
		if(component instanceof Container) { component.setFont(FILE_CHOOSER_FONT); }
    //	    if ((component instanceof JButton)) { if (((JButton) component).getActionCommand().equalsIgnoreCase("New Folder")) { component.getParent().add(this.keyFileDeleteButton); } } // Add Delete button

		if (component instanceof JToggleButton) // Click "details view" ToggleButton
		{
    //		test("test: " + component.getClass().toGenericString() + "\r\n");
		    SwingUtilities.invokeLater(new Runnable() { public void run()
		    {
			if ( ! ((JToggleButton)component).isSelected() )
			{
			    if ( firsttime )
			    {
				TimerTask keyFileChoosershowDetailsTask = new TimerTask() { @Override public void run()
				{
				    keyFileChooser.setVisible(false);
				    ((JToggleButton)component).doClick();
				    keyFileChooser.setVisible(true);
				}};
				Timer keyFileChoosershowDetailsTaskTimer = new java.util.Timer(); keyFileChoosershowDetailsTaskTimer.schedule(keyFileChoosershowDetailsTask, 500L); // Needs a delay for proper column width
			    }
			    else { ((JToggleButton)component).doClick(); }
			}
		    }});
		}
		if (component instanceof Container) { if( keyFileChooserComponentAlteration((Container) component, firsttime)) { return false; } }
		if (firsttime)
		{
		    if (component instanceof JComboBox)
		    {
			keyPathFile = new File(".");
			keyPathSelector = (JComboBox) component;
    //		    for (int x=0; x<keyPathSelector.getItemCount(); x++)
    //		    {
    ////			test("item: " + b.getItemAt(x) + "\r\n");
    //			String item = keyPathSelector.getItemAt(x).toString();
    //			test("item: " + item + "\r\n");
    //			if ( item.toLowerCase().contains("deze"))
    //			{
    //			    test("YES: " + item + "\r\n");
    ////			    keyFileChooser.setCurrentDirectory((File) keyPathSelector.getItemAt(x));
    //			    keyPathSelectorPrefered = x;
    ////			    keyPathFile = new File(keyPathSelector.getItemAt(x).toString());
    //			    keyPathSelector.setSelectedIndex(keyPathSelectorPrefered);
    //			    keyPathFile = keyFileChooser.getCurrentDirectory();
    ////			    keyFileChooser.rescanCurrentDirectory();
    //			    break;
    ////			    x=keyPathSelector.getItemCount(); break;
    ////			    TimerTask keyFileChooserSetPrefDirTask = new TimerTask() { @Override public void run()
    ////			    {
    ////				keyPathSelector.setSelectedIndex(keyPathSelectorPrefered);
    ////				keyFileChooser.rescanCurrentDirectory();
    ////			    }};
    ////			    Timer keyFileChooserSetPrefDirTaskTimer = new java.util.Timer(); keyFileChooserSetPrefDirTaskTimer.schedule(keyFileChooserSetPrefDirTask, 4000L); // Needs a delay for proper column width
    //			}
    //		    }
		    }
		}
	    }
	    return false;
	}
	return false;
    }

    @FXML
    private void encryptButtonAction(ActionEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
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

    private void encrypt(FCPathList<FCPath> targetSourceFCPathList, FCPathList<FCPath> filteredTargetSourceFCPathList, FCPath keySourceFCPath) // Only run within thread
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
	
	String pwd = pwdField.getText(); byte[] pwdBytes = new byte[0];
	if (pwd.length() > 0)
	{
	    MessageDigest messageDigest = null; try { messageDigest = MessageDigest.getInstance(FinalCrypt.HASH_ALGORITHM_NAME); } catch (NoSuchAlgorithmException ex) { log("Error: NoSuchAlgorithmException: MessageDigest.getInstance(\" "+ FinalCrypt.HASH_ALGORITHM_NAME + "\")\r\n", true, true, true, true, false);}
	    messageDigest.update(pwd.getBytes());
	    byte[] hashBytes = messageDigest.digest();
	    pwdBytes = GPT.hex2Bytes(getHexString(hashBytes,2));
	}
	else { pwd = ""; }
	finalCrypt.encryptSelection(targetSourceFCPathList, filteredTargetSourceFCPathList, keyFCPath, true, pwd, pwdBytes, false);
    }

    @FXML
    private void decryptButtonAction(ActionEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
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

    private void decrypt(FCPathList<FCPath> targetSourceFCPathList, FCPathList<FCPath> filteredTargetSourceFCPathList, FCPath keySourceFCPath, boolean open) // Only run within thread | open opens targets after decryption
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
	
	processRunningMode = DECRYPT_MODE;
	
	Platform.runLater(() -> { filesProgressBar.setProgress(0.0); fileProgressBar.setProgress(0.0); });
		
	processStarted();
	
	String pwd = pwdField.getText(); byte[] pwdBytes = new byte[0];
	if (pwd.length() > 0)
	{
	    MessageDigest messageDigest = null; try { messageDigest = MessageDigest.getInstance(FinalCrypt.HASH_ALGORITHM_NAME); } catch (NoSuchAlgorithmException ex) { log("Error: NoSuchAlgorithmException: MessageDigest.getInstance(\" "+ FinalCrypt.HASH_ALGORITHM_NAME + "\")\r\n", true, true, true, true, false); }
	    messageDigest.update(pwd.getBytes());
	    byte[] hashBytes = messageDigest.digest();
	    pwdBytes = GPT.hex2Bytes(getHexString(hashBytes,2));
	}
	else { pwd = ""; }
	finalCrypt.encryptSelection(targetSourceFCPathList, filteredTargetSourceFCPathList, keyFCPath, false, pwd, pwdBytes, open);
    }

    @FXML private void keyLabelOnMouseClicked(MouseEvent event)	{ new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC); createOTPKeyFile(); }
    private void keyButtonOnAction(ActionEvent event)		{ new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC); createOTPKeyFile(); }
    
    synchronized private void createOTPKeyFile()
    {
        // Needs Threading to early split off from the UI Event Dispatch Thread
	new Sound().play(this, Audio.SND_ALERT,Audio.AUDIO_CODEC);
	String alertString = "";
	Alert alert = new Alert(Alert.AlertType.CONFIRMATION, alertString, ButtonType.YES, ButtonType.NO);

	DialogPane dialogPane = alert.getDialogPane();
	dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
	dialogPane.getStyleClass().add("myDialog");
	
	alert.setTitle(createOTPKeyTitle);
	alert.setHeaderText(createOTPKeyHeader);
	alert.setResizable(true);
	
	String	content = createOTPKeyText;
//	content += "FinalCrypt automatically creates One-Time Pad Key Files\r\n";
//	content += "Creating Manual OTP keys is supported but not recommended\r\n\r\n";
//	content += "Do you still want to create a Manual One-Time Pad Key File?\r\n";
	alert.setContentText(content);
	alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	alert.showAndWait();

	if (alert.getResult() == ButtonType.YES)
	{
	    new Sound().play(this, Audio.SND_OPEN,Audio.AUDIO_CODEC);
	    final GUIFX guifx = this;
	    final UI ui = this;

	    Platform.runLater(() ->
	    {
//	    if ((showPasswordCheckBox.isVisible()) && (pwdField.getText().length() == 0)) { passwordHeaderLabel.setText(PASSWORD_OPTIONAL); } else { passwordHeaderLabel.setText(PASSWORD_SET); }
		passwordHeaderLabel.setText(""); 

//		pwdField.setText("");
		pwdField.setDisable(true);
		pwdField.setVisible(false);

//		pwdtxtField.setText("");
		pwdtxtField.setDisable(true);
		pwdtxtField.setVisible(false);

		showPasswordCheckBox.setVisible(pwdField.isVisible());
//		keyImageView.setImage(KEY_FILE_IMAGE);
		keyImageView.setOpacity(0.8);

		finalCrypt.setPwd(""); finalCrypt.setPwdBytes(""); finalCrypt.resetPwdPos(); finalCrypt.resetPwdBytesPos();
		
		keyWriteNameLabel.setVisible(false); keyWriteLabel.setVisible(false); keyWriteSizeLabel.setVisible(false);
		keyMatchNameLabel.setVisible(false); keyReadLabel.setVisible(false); keyReadSizeLabel.setVisible(false);
		keyMissingNameLabel.setVisible(false); keyMissingLabel.setVisible(false); keyMissingSizeLabel.setVisible(false);
	    });

	    Platform.runLater(() ->
	    {
		new Sound().play(ui, Sound.SND_OPEN,Audio.AUDIO_CODEC);
		createOTPKeyStage = new Stage();
		createOTPKey = new CreateOTPKey();
		
		try { createOTPKey.start(createOTPKeyStage); } catch (Exception ex) { System.err.println(ex.getMessage()); }
		createOTPKey.controller.switchLanguage(selectedLocale, false);
		createOTPKey.controller.setCurrentDir(keyFileChooser.getCurrentDirectory().toPath().toAbsolutePath(), guifx); // Parse parameters onto global controller references always through controller
	    });

//	    Thread encryptThread = new Thread(new Runnable()
//	    {
//		private DeviceManager deviceManager;
//		@Override
//		@SuppressWarnings({"static-access"})
//		public void run()
//		{
//
//		    if ( keyButton.getText().equals(CREATE_KEY) )
//		    {
//			Platform.runLater(() ->
//			{
//			    new Sound().play(ui, Sound.SND_OPEN,Audio.AUDIO_CODEC);
//			    createOTPKeyStage = new Stage();
//			    createOTPKey = new CreateOTPKey();
//			    try { createOTPKey.start(createOTPKeyStage); } catch (Exception ex) { System.err.println(ex.getMessage()); }
//			    createOTPKey.controller.setCurrentDir(keyFileChooser.getCurrentDirectory().toPath().toAbsolutePath(), guifx); // Parse parameters onto global controller references always through controller
//			});
//		    }
//		    else if	( keyButton.getText().equals(CREATE_KEYDEV) )
//		    {
//			processRunningMode = CREATE_KEYDEV_MODE;
//			tab.getSelectionModel().select(1);
//			processStarted();
//			deviceManager = new DeviceManager(guifx); deviceManager.start();
//			deviceManager.createManualKeyDevice(keyFCPath, (FCPath) targetFCPathList.get(0));
//			processFinished(targetFCPathList, false);
//		    }
//		    else if ( keyButton.getText().equals(CLONE_KEYDEV) )
//		    {
//			processRunningMode = CLONE_KEYDEV_MODE;
//			tab.getSelectionModel().select(1);
//			processStarted();
//			deviceManager = new DeviceManager(ui); deviceManager.start();
//			deviceManager.cloneManualKeyDevice(keyFCPath, (FCPath) targetFCPathList.get(0));
//			processFinished(targetFCPathList, false);
//		    } else { new Sound().play(ui, Sound.SND_INPUT_FAIL,Audio.AUDIO_CODEC); }
//
//		}
//	    });
//	    encryptThread.setName("keyDeviceThread");
//	    encryptThread.setDaemon(true);
//	    encryptThread.start();
	}
	else { new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC); }
    }
    
    synchronized private void openSupport()
    {
	Platform.runLater(() ->
	{
	    // Needs Threading to early split off from the UI Event Dispatch Thread
	    final GUIFX guifx = this;
	    final UI ui = this;

	    Thread pleaseShareThread = new Thread(new Runnable()
	    {
		@Override
		@SuppressWarnings({"static-access"})
		public void run()
		{
		    Platform.runLater(() ->
		    {
			new Sound().play(ui, Sound.SND_OPEN,Audio.AUDIO_CODEC);
			supportStage = new Stage();
			support = new Support();
			try { support.start(supportStage); } catch (Exception ex) { log("Error: Exception: \r\n", true, true, true, true, false); }
			support.controller.switchLanguage(selectedLocale, false);
			support.controller.setGUI(guifx); // Parse parameters onto global controller references always through controller
		    });
		}
	    });
	    pleaseShareThread.setName("pleaseShareThread");
	    pleaseShareThread.setDaemon(true);
	    pleaseShareThread.start();
	});
    }
    
//  ================================================= BEGIN UPDATE PROGRESS ===========================================================

    public void setStageTitle(String title) { Platform.runLater(() -> { guifx.stage.setTitle(title); });}
    
    public void statusDirect(String status) { statusLabel.setText(status); }

    @Override public void processStarted()
    {
        Platform.runLater(() ->
	{
//	    encryptionModeToggleButton.setMouseTransparent(!encryptionModeToggleButton.isMouseTransparent());
//	    encryptionModeAnchorPane.setMouseTransparent(!encryptionModeAnchorPane.isMouseTransparent());
	    
	    // Clocks
	    
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
	    	case ENCRYPT_MODE: new Sound().play(this, Audio.SND_ENCRYPTFILES,Audio.AUDIO_CODEC); ugMessage = new Message(encrypting_files, 64, false, false, false, false, Voice.VOI_ENCRYPTING_FILES, 0); userGuidanceMessage(ugMessage); break;
	    	case DECRYPT_MODE: new Sound().play(this, Audio.SND_DECRYPTFILES,Audio.AUDIO_CODEC); ugMessage = new Message(decrypting_files, 64, false, false, false, false, Voice.VOI_DECRYPTING_FILES, 0); userGuidanceMessage(ugMessage); break;
	    	case CREATE_KEYDEV_MODE: new Sound().play(this, Audio.SND_ENCRYPTFILES,Audio.AUDIO_CODEC); ugMessage = new Message(create_keydev, 64, false, false, false, false, Voice.VOI_CREATE_KEY_DEVICE, 0); userGuidanceMessage(ugMessage); break;
	    	case CLONE_KEYDEV_MODE: new Sound().play(this, Audio.SND_ENCRYPTFILES,Audio.AUDIO_CODEC); ugMessage = new Message(clone_keydev, 64, false, false, false, false, Voice.VOI_CLONE_KEY_DEVICE, 0); userGuidanceMessage(ugMessage); break;
	    	default: break;
	    }

	    processRunning = true;
	    
	    if (System.getProperty("os.name").toLowerCase().indexOf("mac") == -1) // Again due to Mac OSX
	    {
		disableFileChoosers(true, false, true);
	    }
	    else
	    {
		disableFileChoosers(true, false, true);
	    }
		
	    
	    encryptButton.setDisable(true);
	    decryptButton.setDisable(true);
	    tgtFileDeleteButton2.setDisable(true);
	    keyFileDeleteButton2.setDisable(true);
	    
	    pwdField.setDisable(true);
	    pwdtxtField.setDisable(true);
	    
//		pwdField.setVisible(false);
//	        pwdtxtField.setVisible(pwdField.isVisible()); showPasswordCheckBox.setVisible(pwdField.isVisible());
//		keyImageView.setOpacity(0.2);

	    pauseToggleButton.setDisable(false);
	    stopButton.setDisable(false);
	    
	    enableClocks(true, true); 

//	    keyButton.setDisable(true);

	    remainingTimeHeaderLabel.setVisible(true); remainingTimeLabel.setVisible(true);
	    elapsedTimeHeaderLabel.setVisible(true); elapsedTimeLabel.setVisible(true);
	    totalTimeHeaderLabel.setVisible(true); totalTimeLabel.setVisible(true);

	    filesProgressBar.setProgress(0.0); filesProgressBar.setVisible(true);
	    fileProgressBar.setProgress(0.0); fileProgressBar.setVisible(true);

	    tgtFileChooser.rescanCurrentDirectory();
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

    private void enableClocks(Boolean on, Boolean reset)
    {
	if (on)
	{
	    elapsedTimeHeaderLabel.setTextFill(Color.LIGHTGREY); elapsedTimeLabel.setTextFill(Color.LIGHTGREY);
	    remainingTimeHeaderLabel.setTextFill(Color.LIGHTGREY); remainingTimeLabel.setTextFill(Color.LIGHTGREY);
	    totalTimeHeaderLabel.setTextFill(Color.LIGHTGREY); totalTimeLabel.setTextFill(Color.LIGHTGREY);
	}
	else
	{
	    elapsedTimeHeaderLabel.setTextFill(Color.GREY); elapsedTimeLabel.setTextFill(Color.GREY);
	    remainingTimeHeaderLabel.setTextFill(Color.GREY); remainingTimeLabel.setTextFill(Color.GREY);
	    totalTimeHeaderLabel.setTextFill(Color.GREY); totalTimeLabel.setTextFill(Color.GREY);
	}
	if (reset)
	{
	    elapsedTimeLabel.setText("00:00:00");
	    remainingTimeLabel.setText("00:00:00");
	    totalTimeLabel.setText("00:00:00");
	}
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
        
    @Override public void processFinished(FCPathList<FCPath> openFCPathList, boolean open)
    {
	Platform.runLater(() ->
	{
	    fileProgressBar.setProgress(100.0); filesProgressBar.setProgress(100.0);

	    megaBytesPerSecond = 0;
	    UPDATE_CLOCKS_TIMELINE.stop();

	    updateDashboard(targetFCPathList);

	    if ( processRunningMode == ENCRYPT_MODE ) { ugMessage = new Message(finished_encrypting, 64, false, false, false, false, Voice.VOI_FINISHED_ENCRYPTING, 0); userGuidanceMessage(ugMessage); }
	    if ( processRunningMode == DECRYPT_MODE ) { ugMessage = new Message(finished_decrypting, 64, false, false, false, false, Voice.VOI_FINISHED_DECRYPTING, 0); userGuidanceMessage(ugMessage); }
	    
	    Timeline timeline = new Timeline(new KeyFrame( Duration.millis(2000), ae ->
	    {		
		fileProgressBar.setVisible(false);filesProgressBar.setVisible(false);
		
		targetFCPathList = new FCPathList<FCPath>();
		updateDashboard(targetFCPathList);
		
		updateSystemMonitor();

		encryptButton.setDisable(true);
		decryptButton.setDisable(true);
		if ( keyFCPath.isValidKey )
		{
		    pwdField.setDisable(false);
		    if ((showPasswordCheckBox.isVisible()) && (pwdField.getText().length() == 0)) { passwordHeaderLabel.setText(password_optional); } else { passwordHeaderLabel.setText(password_set); }
		    pwdtxtField.setDisable(false);
		    keyImageView.setImage(KEY_FILE_IMAGE);
		    keyImageView.setOpacity(0.8);
		}
		pauseToggleButton.setDisable(true);
		stopButton.setDisable(true);

    //	    keyButton.setDisable(false);


    //	    encryptionModeToggleButton.setMouseTransparent(!encryptionModeToggleButton.isMouseTransparent());
    //	    encryptionModeAnchorPane.setMouseTransparent(!encryptionModeAnchorPane.isMouseTransparent());

    //	    if (System.getProperty("os.name").toLowerCase().indexOf("mac") == -1) // Due to Mac OSX // to 2
    //	    {
		    if ( keyFCPath.isValidKeyDir )
		    {
			updateFileChoosers(true, false, true, true, false, true);
		    }
		    else
		    {
			updateFileChoosers(true, false, true, false, false, false);
		    } // if keyfile selected then NO update keyFileChooser keeping key file selected; processFinished()
    //	    }

		processRunningMode = NONE;
		processRunning = false;

		targetFCPathList = new FCPathList<FCPath>();
		buildReady(targetFCPathList, false);

    //	    if (clockUpdated)
    //	    {
		remainingTimeLabel.setText("00:00:00");
		totalTimeLabel.setText(elapsedTimeLabel.getText());
		enableClocks(false, false);
    //	    }

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
			    Path newPath = Paths.get(openFCPath.path.toAbsolutePath().toString().substring(0, openFCPath.path.toAbsolutePath().toString().lastIndexOf('.')));

			    new Sound().play(this, Audio.SND_OPEN,Audio.AUDIO_CODEC);
			    try { Desktop.getDesktop().open(newPath.toFile()); }
			    catch (IOException ex) { log("Error: Desktop.getDesktop().open(" + newPath.toFile().getAbsolutePath().toString() + "); " + ex.getMessage() + "\r\n", true, true, true, true, false); }

			    targetFCPathList = new FCPathList<FCPath>(); updateDashboard(targetFCPathList);
			    Platform.runLater(new Runnable()
			    {
				@Override public void run()
				{
				    encryptButton.setDisable(true);
				    decryptButton.setDisable(true);
    //				keyButton.setDisable(true);  keyButton.setTextFill(Color.GREY); keyButton.setText(CREATE_KEY); keyButton.setVisible(false);
				}
			    });
			}
		    }
		});
		openThread.setName("openThread");
		openThread.setDaemon(true);
		openThread.start();

		new Sound().play(this, Audio.SND_SHUTDOWN,Audio.AUDIO_CODEC);

		if (System.getProperty("os.name").toLowerCase().indexOf("mac") == -1) // Again due to Mac OSX
		{
		    disableFileChoosers(false, false, true);
		}
		else
		{
		    disableFileChoosers(false, false, true);
		}
	    })); timeline.play();
	});
    }    
    
//  ================================================= END UPDATE PROGRESS ===========================================================
    
    public void updateFileChoosers(boolean redrawTargetFC, boolean firstTime1, boolean checkTargetFC, boolean redrawKeyFC, boolean firstTime2, boolean checkKeyFC)
    {
	Platform.runLater(() -> 
	{
	    if (redrawTargetFC)
	    {		   
		Timeline timeline1 = new Timeline(new KeyFrame( Duration.millis(500), ae ->
		{
		    SwingUtilities.invokeLater(new Runnable() { public void run()
		    {
			tgtFileChooser.setEnabled(false);

			if ( (System.getProperty("os.name").toLowerCase().indexOf("mac") == -1)) // Again due to Mac OSX
			{
			    tgtFileChooserComponentAlteration(tgtFileChooser, true);
			}
			else
			{
//			    tgtFileChooser.rescanCurrentDirectory(); // was off
			    tgtFileChooser.setLocale(selectedLocale);
			    tgtFileChooser.setEnabled(true);
			    tgtFileChooser.setFileFilter(keyFileChooser.getAcceptAllFileFilter());
			    tgtFileChooser.updateUI();
			    Timeline timeline = new Timeline(new KeyFrame( Duration.millis(1000), ae -> 
			    {
				targetFileSwingNode.setContent(tgtFileChooser); // Delay setting this JFileChooser avoiding a simultanious key and target JFileChooser focus conflict causing focus to endlessly flipflop between the two JFileChoosers
				tgtFileChooser.setVisible(false); tgtFileChooser.setVisible(true); keyFileChooser.setVisible(false); keyFileChooser.setVisible(true); // Reldraw FileChoosers
			    }
			    )); timeline.play();
			}
		    }});
		})); timeline1.play();
//		}
	    }

	    if ((redrawKeyFC) && (keyFCPath.type == FCPath.DIRECTORY))
	    {
		Timeline timeline2 = new Timeline(new KeyFrame( Duration.millis(500), ae ->
		{
		    SwingUtilities.invokeLater(new Runnable() { public void run()
		    {
			if ( (System.getProperty("os.name").toLowerCase().indexOf("mac") == -1)) // Again due to Mac OSX
			{
			    keyFileChooserComponentAlteration(keyFileChooser, true);
			}
			else
			{
//			    tgtFileChooser.rescanCurrentDirectory(); // was off
			    keyFileChooser.setLocale(selectedLocale);
			    keyFileChooser.setFileFilter(tgtFileChooser.getAcceptAllFileFilter());
			    keyFileChooser.updateUI();
			    Timeline timeline = new Timeline(new KeyFrame( Duration.millis(1500), ae -> 
			    {
				keyFileSwingNode.setContent(keyFileChooser); // Delay setting this JFileChooser avoiding a simultanious key and target JFileChooser focus conflict causing focus to endlessly flipflop between the two JFileChoosers
				keyFileChooser.setVisible(false); keyFileChooser.setVisible(true); tgtFileChooser.setVisible(false); tgtFileChooser.setVisible(true); // Reldraw FileChoosers
			    }
			    )); timeline.play();
			}
		    }});
		})); timeline2.play();
	    }
	    
	    if (checkTargetFC)
	    {
		Timeline timeline3 = new Timeline(new KeyFrame( Duration.millis(100), ae ->
		{
		    tgtFileChooserPropertyCheck(true);
		})); timeline3.play();
	    }
	    
	    if (checkKeyFC)
	    {
		Timeline timeline4 = new Timeline(new KeyFrame( Duration.millis(100), ae ->
		{
		    keyFileChooserPropertyCheck();
		})); timeline4.play();
	    }
	});
    }
    
    @FXML
    private void keyInfoLabelClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
        Alert alert = new Alert(AlertType.INFORMATION);
        
//      Style the Alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        
        alert.setTitle(information);
        alert.setHeaderText(keyInfoHeader);
        alert.setResizable(true);
        String infotext = new String();
        infotext =  keyInfoContent;
//        infotext += "Your Key Directory holds your One Time Pad Keys\r\n";
//        infotext += "which are automatically created and selected\r\n";
//        infotext += "Select a Key Directory on an external (USB) drive\r\n";
//        infotext += "\r\n";
//        infotext += "An optional password enforces extra encryption\r\n";
//        infotext += "so lost / stolen key files can't unlock your data\r\n";
//        infotext += "\r\n";
//        infotext += "=================================================\r\n";
//        infotext += "\r\n";
//        infotext += "Backup your encrypted files AND keys together as pairs\r\n";
//        infotext += "Keep your backups offline on external (USB) drives too\r\n";
//        infotext += "\r\n";
//        infotext += "Please keep in mind that without your key files (and\r\n";
//        infotext += "optional password) decrypting your data is impossible!\r\n";
//        infotext += "\r\n";
        alert.setContentText(infotext);
	new Sound().play(this, Audio.SND_MESSAGE,Audio.AUDIO_CODEC);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	alert.showAndWait();
	if (alert.getResult() == ButtonType.OK) { new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC); }
    }

    @FXML
    private void targetInfoLabelClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
        Alert alert = new Alert(AlertType.INFORMATION);

//      Style the Alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");

        alert.setTitle(information);
        alert.setHeaderText(targetInfoHeader);
        alert.setResizable(true);
        String infotext = new String();
        infotext  = targetInfoContent;
//        infotext += "Hold down [Ctrl] / [Shift] to select multiple items.\r\n";
//        infotext += "\r\n";
//        infotext += "Encrypted files get the \".bit\" extension added.\r\n";
//        infotext += "Decrypted files get the \".bit\" extension removed.\r\n";
//        infotext += "\r\n";
//        infotext += "Original files are securely deleted after encryption.\r\n";
//        infotext += "\r\n";
        alert.setContentText(infotext);
	new Sound().play(this, Audio.SND_MESSAGE,Audio.AUDIO_CODEC);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	alert.showAndWait();
	if (alert.getResult() == ButtonType.OK) { new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC); }
    }

    @FXML private void pauseToggleButtonAction(ActionEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);

        if (processRunning)
        {
	    if ((processRunningMode == ENCRYPT_MODE) || (processRunningMode == DECRYPT_MODE)) { finalCrypt.setPausing(pauseToggleButton.isSelected()); } else { DeviceController.setPausing(pauseToggleButton.isSelected()); }
	    if ( pauseToggleButton.isSelected() )
	    {
		new Sound().play(this, Audio.SND_SHUTDOWN,Audio.AUDIO_CODEC);
		pauseToggleButton.setStyle(" -fx-text-fill: orange; -fx-font-size: 14; ");
		PAUSE_TIMELINE.play();
		megaBytesPerSecond = 0d;
	    }
	    else
	    {
		if (processRunningMode == ENCRYPT_MODE) { new Sound().play(this, Audio.SND_ENCRYPTFILES,Audio.AUDIO_CODEC); } else { new Sound().play(this, Audio.SND_DECRYPTFILES,Audio.AUDIO_CODEC); }
		PAUSE_TIMELINE.stop();
		pauseToggleButton.setText(FinalCrypt.UTF8_PAUSE_DESC);
		pauseToggleButton.setStyle(" -fx-text-fill: white; -fx-font-size: 14; ");
	    }
        }
    }
    
    @FXML
    private void stopButtonAction(ActionEvent event) { new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC); stop(false); }
    
    private void stop(boolean stopAndExit)
    {
        if ((processRunning) && ((processRunningMode == ENCRYPT_MODE) || (processRunningMode == DECRYPT_MODE)))
        {
	    new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC);
            finalCrypt.setStopPending(true);
            if (pauseToggleButton.isSelected()) { pauseToggleButton.fire(); }
        }
        else
        {
	    new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC);
            DeviceController.setStopPending(true);
            if (pauseToggleButton.isSelected()) { pauseToggleButton.fire(); }
        }
    }

    @FXML private void copyrightLabelOnMouseClicked(MouseEvent event)	{ new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC); new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC); /*checkUpdate();*/ }

    @FXML private void checkUpdateButtonOnAction(ActionEvent event) { Platform.runLater(() -> { new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC); checkUpdate( true ); }); }

    @FXML private void encryptTabSelectionChanged(Event event)
    {
        String platform = System.getProperty("os.name").toLowerCase(); // Due to a nasty JFileChooser focus issue on Mac
        if ( platform.indexOf("mac") != -1 ) // if it is a mac
	{
	    Timeline timeline = new Timeline(new KeyFrame( Duration.millis(200), ae -> 
	    {
		targetFileSwingNode.setContent(tgtFileChooser); // Delay setting this JFileChooser avoiding a simultanious key and target JFileChooser focus conflict causing focus to endlessly flipflop between the two JFileChoosers
		tgtFileChooser.setVisible(false); tgtFileChooser.setVisible(true); keyFileChooser.setVisible(false); keyFileChooser.setVisible(true); // Reldraw FileChoosers
	    }
	    )); timeline.play();
	}
	else
	{
	    Platform.runLater(() ->
	    {
		if ( tgtFileChooser != null ) { tgtFileChooser.setVisible(false); tgtFileChooser.setVisible(true); }
		if ( keyFileChooser != null ) { keyFileChooser.setVisible(false); keyFileChooser.setVisible(true); } // Reldraw FileChoosers
	    });
	}
    }

    @FXML private void logTabSelectionChanged(Event event) { new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC); }

//  Headers
//  ==================================================================================================================================================================

    private void emptyFilesHeaderLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (emptyList != null) && (emptyList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("Empty Files:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = emptyList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, false, false, false); }
	emptyList.forEach((fcPath) -> { log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); });
	log("\r\n", false, true, false, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC); }
    }

    private void symlinkFilesHeaderLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (symlinkList != null) && (symlinkList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("Symlinks:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = symlinkList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, false, false, false); }
	symlinkList.forEach((fcPath) -> { log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); });
	log("\r\n", false, true, false, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC); }
    }

    @FXML
    private void unreadableFilesHeaderLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (unreadableList != null) && (unreadableList.size() > 0) )
	{
	    /*tab.getSelectionModel().select(1);*/ new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); log("Set Read Attributes:\r\n\r\n", false, true, true, false, false);
//	    for (Iterator it = unreadableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); setAttribute(fcPath, true, false); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, false, false, false);
	    unreadableList.stream().map((fcPath) -> { setAttribute(fcPath, true, false); return fcPath; }).forEachOrdered((fcPath) -> { log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); });
	    log("\r\n", false, true, false, false, false);
	    targetFCPathList = new FCPathList<FCPath>(); updateDashboard(targetFCPathList);
	    Platform.runLater(() -> { encryptButton.setDisable(true); enableClocks(false, true); decryptButton.setDisable(true); });
	}
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC); }
    }

    @FXML
    private void unwritableFilesHeaderLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (unwritableList != null) && (unwritableList.size() > 0) )
	{
	    /*tab.getSelectionModel().select(1);*/ new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); log("Set Write Attributes:\r\n\r\n", false, true, true, false, false);
//	    for (Iterator it = unwritableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); setAttribute(fcPath, true, true); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false);
	    unwritableList.stream().map((fcPath) -> { setAttribute(fcPath, true, true);	return fcPath; }).forEachOrdered((fcPath) -> { log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); });
	    log("\r\n", false, true, true, false, false);
	    targetFCPathList = new FCPathList<FCPath>(); updateDashboard(targetFCPathList);
	    Platform.runLater(() -> { encryptButton.setDisable(true); enableClocks(false, true); decryptButton.setDisable(true); });
	}
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC); }
    }

    private void hiddenFilesHeaderLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (hiddenList != null) && (hiddenList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("\r\nHidden Files:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = hiddenList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	for (FCPath fcPath : hiddenList) { log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	log("\r\n", false, true, true, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    @FXML
    private void emptyFilesLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (emptyList != null) && (emptyList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("\r\nEmpty Files:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = emptyList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	for (FCPath fcPath : emptyList) { log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	log("\r\n", false, true, true, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    @FXML
    private void symlinkFilesLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (symlinkList != null) && (symlinkList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("\r\nSymlinks:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = symlinkList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	for (FCPath fcPath : symlinkList) { log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	log("\r\n", false, true, true, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    @FXML
    private void unreadableFilesLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (unreadableList != null) && (unreadableList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("\r\nUnreadable Files:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = unreadableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	for (FCPath fcPath : unreadableList) { log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	log("\r\n", false, true, true, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    @FXML
    private void unwritableFilesLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (unwritableList != null) && (unwritableList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("Unwritable Files:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = unwritableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	for (FCPath fcPath : unwritableList) { log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	log("\r\n", false, true, true, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    @FXML
    private void hiddenFilesLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (hiddenList != null) && (hiddenList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("Hidden Files:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = hiddenList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	for (FCPath fcPath : hiddenList) { log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	log("\r\n", false, true, true, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }
    
    @FXML
    private void encryptableLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (encryptableList != null) && (encryptableList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("\r\nEncryptable Files:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = encryptableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	for (FCPath fcPath : encryptableList) { log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	log("\r\n", false, true, true, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    @FXML private void keyWriteLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (writeAutoKeyList != null) && (writeAutoKeyList.size() > 0) )
	{
	    new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("\r\nWrite Auto Key Files:\r\n\r\n", false, true, true, false, false);
//	    for (Iterator it = writeAutoKeyList.iterator(); it.hasNext();)
//	    {
//		FCPath fcPath = (FCPath) it.next();
//		Path autoKeyPath = Paths.get(keyFCPath.path.toAbsolutePath().toString(), fcPath.path.toAbsolutePath().toString().replace(":", "") + ".bit");
//		log(autoKeyPath.toAbsolutePath().toString() + "\r\n", false, true, true, false, false);
//	    }
	    for (FCPath fcPath : writeAutoKeyList)
	    {
		Path autoKeyPath = Paths.get(keyFCPath.path.toAbsolutePath().toString(), fcPath.path.toAbsolutePath().toString().replace(":", "") + ".bit");
		log(autoKeyPath.toAbsolutePath().toString() + "\r\n", false, true, true, false, false);
	    }
	    log("\r\n", false, true, true, false, false);
	}
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    @FXML private void keyReadLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (readAutoKeyList != null) && (readAutoKeyList.size() > 0) )
	{
	    new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("\r\nMatching Auto Key Files:\r\n\r\n", false, true, true, false, false);
//	    for (Iterator it = readAutoKeyList.iterator(); it.hasNext();)
//	    {
//		FCPath fcPath = (FCPath) it.next();
//		Path autoKeyPath = null;
//		if (fcPath.isDecrypted) { autoKeyPath = Paths.get(keyFCPath.path.toAbsolutePath().toString(), fcPath.path.toAbsolutePath().toString().replace(":", "") + ".bit"); }
//		else			{ autoKeyPath = Paths.get(keyFCPath.path.toAbsolutePath().toString(), fcPath.path.toAbsolutePath().toString().replace(":", "")); }
//		log(autoKeyPath.toAbsolutePath().toString() + "\r\n", false, true, true, false, false);
//	    }
	    for (FCPath fcPath : readAutoKeyList)
	    {
		Path autoKeyPath = null;
		if (fcPath.isDecrypted) { autoKeyPath = Paths.get(keyFCPath.path.toAbsolutePath().toString(), fcPath.path.toAbsolutePath().toString().replace(":", "") + ".bit"); }
		else			{ autoKeyPath = Paths.get(keyFCPath.path.toAbsolutePath().toString(), fcPath.path.toAbsolutePath().toString().replace(":", "")); }
		log(autoKeyPath.toAbsolutePath().toString() + "\r\n", false, true, true, false, false);
	    }
	    log("\r\n", false, true, true, false, false);
	}
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    @FXML private void keyMissingLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (missingAutoKeyList != null) && (missingAutoKeyList.size() > 0) )
	{
	    new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("\r\nMissing Auto Key Files:\r\n\r\n", false, true, true, false, false);
//	    for (Iterator it = readAutoKeyList.iterator(); it.hasNext();)
//	    {
//		FCPath fcPath = (FCPath) it.next();
//		Path autoKeyPath = null;
//		if (fcPath.isDecrypted) { autoKeyPath = Paths.get(keyFCPath.path.toAbsolutePath().toString(), fcPath.path.toAbsolutePath().toString().replace(":", "") + ".bit"); }
//		else			{ autoKeyPath = Paths.get(keyFCPath.path.toAbsolutePath().toString(), fcPath.path.toAbsolutePath().toString().replace(":", "")); }
//		log(autoKeyPath.toAbsolutePath().toString() + "\r\n", false, true, true, false, false);
//	    }
	    for (FCPath fcPath : missingAutoKeyList)
	    {
		Path autoKeyPath = null;
		if (fcPath.isDecrypted) { autoKeyPath = Paths.get(keyFCPath.path.toAbsolutePath().toString(), fcPath.path.toAbsolutePath().toString().replace(":", "") + ".bit"); }
		else			{ autoKeyPath = Paths.get(keyFCPath.path.toAbsolutePath().toString(), fcPath.path.toAbsolutePath().toString().replace(":", "")); }
		log(autoKeyPath.toAbsolutePath().toString() + "\r\n", false, true, true, false, false);
	    }
	    log("\r\n", false, true, true, false, false);
	}
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    @FXML private void decryptableLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (decryptableList != null) && (decryptableList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("\r\nDecryptable Files:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = decryptableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	for (FCPath fcPath : decryptableList) {	log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	log("\r\n", false, true, true, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    @FXML
    private void decryptedLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (decryptedList != null) && (decryptedList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("\r\nDecrypted Files:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = decryptedList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	for (FCPath fcPath : decryptedList) {	log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	log("\r\n", false, true, true, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    @FXML
    private void encryptedLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (encryptedList != null) && (encryptedList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("\r\nEncrypted Files:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = encryptedList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	for (FCPath fcPath : encryptedList) { log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	log("\r\n", false, true, true, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    private void newEncryptedLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (newEncryptedList != null) && (newEncryptedList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("\r\nNew Encrypted Files:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = newEncryptedList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	for (FCPath fcPath : newEncryptedList) { log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	log("\r\n", false, true, true, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    @FXML
    private void unencryptableLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (unencryptableList != null) && (unencryptableList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("\r\nUnencryptable Files:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = unencryptableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	for (FCPath fcPath : unencryptableList) { log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	log("\r\n", false, true, true, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    private void newDecryptedLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (newDecryptedList != null) && (newDecryptedList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("\r\nNew Decrypted Files:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = newDecryptedList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); } log("\r\n", false, true, true, false, false); }
	for (FCPath fcPath : newDecryptedList) { log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	log("\r\n", false, true, true, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    @FXML
    private void undecryptableLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (undecryptableList != null) && (undecryptableList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("\r\nUndecryptable Files:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = undecryptableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	for (FCPath fcPath : undecryptableList)	{ log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	log("\r\n", false, true, true, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    @FXML
    private void invalidFilesLabelOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	if ( (invalidFilesList != null) && (invalidFilesList.size() > 0) ) { new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); tab.getSelectionModel().select(1); log("\r\nInvalid Files:\r\n\r\n", false, true, true, false, false);
//	for (Iterator it = invalidFilesList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	for (FCPath fcPath : invalidFilesList) { log(fcPath.path.toAbsolutePath().toString() + "\r\n", false, true, true, false, false); }
	log("\r\n", false, true, true, false, false); }
	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);}
    }

    @FXML private void supportButtonOnAction(ActionEvent event)
    {
	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	new Sound().play(this, Audio.SND_OPEN,Audio.AUDIO_CODEC);
	openSupport();
    }

    private void setAttribute(FCPath fcPath, boolean read, boolean write)
    {
	attributeViewloop: for (String view:fcPath.path.getFileSystem().supportedFileAttributeViews()) // acl basic owner user dos
	{
//          test(view + "\r\n");
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
		catch (IOException ex) { log("Error: Set DOS Attributes: " + ex.getMessage() + "\r\n", false, false, false, true, false); }
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



//  ==============================================================================================================
//  Begin Message Authentication Mode
//  ==============================================================================================================


//    private void disarmDisableMACMode()
//    {
//	new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC);
//	encryptionModeToggleButton.setDisable(true);
//	encryptionModeToggleButton.setSelected(false);
//	encryptionModeToggleButton.setText(MAC_ON);
//	encryptionModeToggleButton.setTextFill(Paint.valueOf("grey"));
//	encryptionModeToggleButton.setMouseTransparent(false);
//    }
    
//  Enable / Arm Disable-MAC-Mode-Button
//    private void armDisableMACMode()
//    {
//	new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC);
//	encryptionModeToggleButton.setDisable(false);
//	encryptionModeToggleButton.setSelected(false);
//	encryptionModeToggleButton.setText(MAC_OFF_Q);
//	encryptionModeToggleButton.setTextFill(Paint.valueOf("grey"));
//	encryptionModeToggleButton.setMouseTransparent(false);
//	encryptionModeToggleButton.getTooltip().setText("Click to disable MAC Mode! (files will be encrypted without Message Authentication Code Header)");
//
////	Auto disable arming disable MAC Mode
//	AUTO_DISABLE_ARMING_MAC_MODE_TIMELINE.play();
//    }
    
//  Default MAC Mode
//    private void enableMACMode() // Safe Mode
//    {
//	new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC);
//	if ( FLASH_MAC_MODE_TIMELINE != null ) { FLASH_MAC_MODE_TIMELINE.stop(); }
//	encryptionModeToggleButton.setText(MAC_ON);
//	encryptionModeToggleButton.setTextFill(Paint.valueOf("white"));
//
//	updateFileChoosers(true, true); // to 2 enableMACMode()
//	FCPath.KEY_SIZE_MIN = FCPath.KEY_SIZE_MIN_DEFAULT;
//	finalCrypt.disabledMAC = false;
//	dashboardGridPane.setDisable(false);
//	encryptionModeToggleButton.setDisable(true);
//	encryptionModeToggleButton.setMouseTransparent(true);
//	long now = Calendar.getInstance().getTimeInMillis(); lastRawModeClicked = now; // Anti DoubleClick missery
//	log("Message Authentication Mode Enabled\r\n", true, true, true, false, false);
//    }

//    private void disableMACMode() // Dangerous Mode
//    {
//	new Sound().play(this, Audio.SND_ALARM,Audio.AUDIO_CODEC);
//	if ( AUTO_DISABLE_ARMING_MAC_MODE_TIMELINE != null ) { AUTO_DISABLE_ARMING_MAC_MODE_TIMELINE.stop(); }
//
//	encryptionModeToggleButton.setText(MAC_OFF);
//	encryptionModeToggleButton.setTextFill(Paint.valueOf("white"));
//	encryptionModeToggleButton.getTooltip().setText("Click to enable Message Authentication Mode");
//
//	updateFileChoosers(true, true); // disableMACMode()
//	FCPath.KEY_SIZE_MIN = 1;
//	finalCrypt.disabledMAC = true;
//	dashboardGridPane.setDisable(true);
//	log("Warning: MAC Mode Disabled! (files will be encrypted without Message Authentication Code Header)\r\n", true, true, true, false, false);
//
//	FLASH_MAC_MODE_TIMELINE.play();
//    }
        
//    @FXML private void encryptionModeToggleButtonOnMouseClicked(MouseEvent event)
//    {
//	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
//	if ( ! processRunning )
//	{
//	    Platform.runLater(() ->
//	    {
//		if (! encryptionModeToggleButton.isSelected())	{ enableMACMode(); }
//		else						{ disableMACMode(); }
//	    });
//	} else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC); }
//    }

//    @FXML private void encryptionModeAnchorPaneOnMouseClicked(MouseEvent event)
//    {
//	new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
//	long now = Calendar.getInstance().getTimeInMillis();
//	if ( ! processRunning)
//	{
//	    if ( now - lastRawModeClicked > 1000) // Anti DoubleClick missery
//	    {
//		Platform.runLater(() ->
//		{
//		    if (encryptionModeToggleButton.isDisabled())
//		    {
//			if(event.getButton().equals(MouseButton.PRIMARY))
//			{
//			    if(event.getClickCount() == 2)
//			    {
//				new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC);
//				armDisableMACMode();
//			    }
//			}	
//		    }
//		});
//	    }	
//	}
//	else { new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC); }
//    }


//  ==============================================================================================================
//  End Message Authentication Mode
//  ==============================================================================================================

    @Override public void test(String message) { log(message, true, true, true, false, false); }

    @Override synchronized public void log(String message, boolean status, boolean log, boolean logfile, boolean errfile, boolean print)
    {
	if (statusLabel!=null)
	{
	    if (status)	{ status(message); }
	    if (log)	{ log(message); }
	    if (logfile)	{ logfile(message); }
	    if (errfile)	{ errfile(message); }
	    if (print)	{ print(message,errfile); }
	}
    }

    public void status(String message)		    { Platform.runLater(() -> { statusLabel.setText(message.replace("\r\n", "")); }); }
    public void log(String message)		    { Platform.runLater(() -> { lineCounter++;  logTextArea.appendText(message); if (lineCounter > 1000) { logTextArea.setText(message); lineCounter = 0; } }); }
    public void logfile(String message)		    { Platform.runLater(() -> { try { Files.write(configuration.getLogFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND); } catch (IOException ex) { log("Files.write(" + configuration.getLogFilePath() + ")..));", true, true, false, false, false); } }); }
    public void errfile(String message)		    { Platform.runLater(() -> { new Sound().play(this, Audio.SND_ERROR,Audio.AUDIO_CODEC); try { Files.write(configuration.getErrFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND); } catch (IOException ex) { log("Files.write(" + configuration.getErrFilePath() + ")..));", true, true, false, false, false); } }); }
    public void print(String message,boolean err)   { if ( ! err ) { System.out.print(message); } else { System.err.print(message); } }
    
    public static void main(String[] args)  { launch(args); }

    @FXML  private void userGuidanceLabelOnMouseClicked(MouseEvent event)
    {
	Platform.runLater(() -> 
	{
	    if	((event.getX() > ((userGuidanceLabel.getWidth() / 2) - 75)) && (event.getX() < ((userGuidanceLabel.getWidth() / 2) - 45)) && (event.getY() >= 110) && (event.getY() <= 125)) // S
	    {
		selectLanguage.setVisible(false);
		if (Voice.sound_Is_Enabled) // turn sound off
		{
		    new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
		    new Sound().play(this, Audio.SND_SOUND_DISABLED,Audio.AUDIO_CODEC);
		    setSound(false); prefs.put("Sound", "Disabled"); flushPrefs(prefs); 
		}
		else // turn sound on
		{
		    setSound(true); prefs.put("Sound", "Enabled"); flushPrefs(prefs);
		    new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
		    new Sound().play(this, Audio.SND_SOUND_ENABLED,Audio.AUDIO_CODEC);
		}
	    }
	    else if ((event.getX() > ((userGuidanceLabel.getWidth() / 2) + 45)) && (event.getX() < ((userGuidanceLabel.getWidth() / 2) + 75)) && (event.getY() >= 110) && (event.getY() <= 125)) // V
	    {
		selectLanguage.setVisible(false);
		if (Voice.voice_Is_Enabled) // turn voice off
		{
		    new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
		    Voice.play(this, Audio.VOI_VOICE_DISABLED,Audio.AUDIO_CODEC);
		    setVoice(false); prefs.put("Voice", "Disabled"); flushPrefs(prefs); 
		}
		else // turn voice on
		{		
		    setVoice(true); prefs.put("Voice", "Enabled"); flushPrefs(prefs);
		    new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
		    Voice.play(this, Audio.VOI_VOICE_ENABLED,Audio.AUDIO_CODEC);
		}
	    }
	    else if ((event.getX() > ((userGuidanceLabel.getWidth() / 2) - 75)) && (event.getX() < ((userGuidanceLabel.getWidth() / 2) + 75)) && (event.getY() >= 15) && (event.getY() <= 27)) // Language
	    {
		selectLanguage.setVisible(true);
		new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
	    }
	    else // Animation
	    {
		selectLanguage.setVisible(false);
		new Sound().play(this, Audio.SND_BUTTON,Audio.AUDIO_CODEC);
		animation_Is_Enabled = ! animation_Is_Enabled;
		if (animation_Is_Enabled) { prefs.put("Animated", "Enabled"); flushPrefs(prefs); new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC); load_High_MS_Passed = 0.0; load_Low_MS_Passed = LOAD_LOW_MS_TIMEOUT; } else { prefs.put("Animated", "Disabled"); new Sound().play(this, Audio.SND_INPUT_FAIL,Audio.AUDIO_CODEC); }
	    }
	});
    }

    @FXML
    private void selectLanguageOnMouseClicked(MouseEvent event)
    {
	Platform.runLater(() -> 
	{
	    if	    ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Arabic") )	    { selectedLanguageCode = "ar"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Bulgarian") )  { selectedLanguageCode = "bg"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Chinese") )    { selectedLanguageCode = "cn"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Croatian") )   { selectedLanguageCode = "hr"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Czech") )	    { selectedLanguageCode = "cz"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Dutch") )	    { selectedLanguageCode = "nl"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("English") )    { selectedLanguageCode = "en"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Esperanto") )  { selectedLanguageCode = "eo"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Farsi") )	    { selectedLanguageCode = "fa"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("French") )	    { selectedLanguageCode = "fr"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("German") )	    { selectedLanguageCode = "de"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Greek") )	    { selectedLanguageCode = "el"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Hindi") )	    { selectedLanguageCode = "hi"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Hungarian") )  { selectedLanguageCode = "hu"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Icelandic") )  { selectedLanguageCode = "is"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Indonesian") ) { selectedLanguageCode = "id"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Italian") )    { selectedLanguageCode = "it"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Kazakh") )	    { selectedLanguageCode = "kk"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Latvian") )    { selectedLanguageCode = "lv"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Malaysian") )  { selectedLanguageCode = "ms"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Maltees") )    { selectedLanguageCode = "mt"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Norwegian") )  { selectedLanguageCode = "no"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Polish") )	    { selectedLanguageCode = "pl"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Portugese") )  { selectedLanguageCode = "pt"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Romanian") )   { selectedLanguageCode = "rm"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Russian") )    { selectedLanguageCode = "ru"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Serbian") )    { selectedLanguageCode = "sr"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Spanish") )    { selectedLanguageCode = "es"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Swedish") )    { selectedLanguageCode = "sv"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Thai") )	    { selectedLanguageCode = "th"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Turkish") )    { selectedLanguageCode = "tr"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else if ( selectLanguage.getSelectionModel().getSelectedItem().equalsIgnoreCase("Ukrainian") )  { selectedLanguageCode = "uk"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
	    else											    { selectedLanguageCode = "en"; selectedLocale = new Locale(selectedLanguageCode,Locale.getDefault().getCountry()); }
//	    switchLanguage(selectedLocale, true, true, false);

//	    switchLanguage(Locale locale, String selectedLanguageCode, boolean writeLanguage, boolean redrawFileChoosers, boolean firsttime, boolean checkFileChoosers)
	    switchLanguage(selectedLocale,      selectedLanguageCode,			true,			    true,	      false,			false);

	    selectLanguage.setVisible(false);
	});
    }
    
    
    @FXML private void showPasswordCheckBoxOnAction(ActionEvent event)
    {
	if (showPasswordCheckBox.isSelected())
	{
	    pwdtxtField.setText(pwdField.getText()); pwdtxtFieldTooltip.setText(pwdField.getText());
	}
	else
	{
	    pwdField.setText(pwdtxtField.getText()); pwdtxtFieldTooltip.setText(pwdtxtField.getText()); }
	setFont(pwdField);setFont(pwdtxtField);
	pwdField.setVisible(! showPasswordCheckBox.isSelected());
	pwdtxtField.setVisible(showPasswordCheckBox.isSelected());
    }

    @FXML  private void pwdFieldOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_SELECT,Audio.AUDIO_CODEC);
	ugMessage = new Message(password_enter, 48, false, false, true, false, Voice.VOI_CONFIRM_PASS_WITH_ENTER, 0);
	userGuidanceMessage(ugMessage);
	setFont(pwdField);setFont(pwdtxtField);
    }

    @FXML private void pwdFieldOnKeyReleased(KeyEvent event)
    {
//	log("Pass: " + pwdField.getText() + " length: " + pwdField.getText().length() + event.getCode(), true, true, true, false, false);
	if (event.getCode() == KeyCode.ENTER)
	{
	    new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC);
	    passwordHeaderLabel.setText(password_set);
	    settingPassword = false;
	    tgtFileChooserPropertyCheck(true);
	}
	else
	{
	    finalCrypt.setPwd(pwdField.getText()); finalCrypt.setPwdBytes(pwdField.getText()); finalCrypt.resetPwdPos(); finalCrypt.resetPwdBytesPos();
	    pwdtxtField.setText(pwdField.getText());
	    pwdtxtFieldTooltip.setText(pwdField.getText());
	    setFont(pwdField);setFont(pwdtxtField);
	    passwordHeaderLabel.setText(password_enter);
	    targetFCPathList = new FCPathList<FCPath>();
	    buildReady(targetFCPathList, false);
	    settingPassword = true;
	}
    }
    
    @FXML private void pwdtxtFieldOnMouseClicked(MouseEvent event)
    {
	new Sound().play(this, Audio.SND_SELECT,Audio.AUDIO_CODEC);
	ugMessage = new Message(password_enter, 48, false, false, true, false, Voice.VOI_CONFIRM_PASS_WITH_ENTER, 0); userGuidanceMessage(ugMessage);
	setFont(pwdField);setFont(pwdtxtField);
    }

    @FXML private void pwdtxtFieldOnKeyReleased(KeyEvent event)
    {
	if (event.getCode() == KeyCode.ENTER)
	{
	    new Sound().play(this, Audio.SND_INPUT_OK,Audio.AUDIO_CODEC);
	    passwordHeaderLabel.setText(password_set);
	    settingPassword = false;
	    tgtFileChooserPropertyCheck(true);
	}
	else
	{
	    finalCrypt.setPwd(pwdtxtField.getText()); finalCrypt.setPwdBytes(pwdtxtField.getText()); finalCrypt.resetPwdPos(); finalCrypt.resetPwdBytesPos();
	    pwdField.setText(pwdtxtField.getText());
	    pwdtxtFieldTooltip.setText(pwdtxtField.getText());
	    setFont(pwdField);setFont(pwdtxtField);
	    passwordHeaderLabel.setText(password_enter);
//	    if (!settingPassword)
//	    {
		targetFCPathList = new FCPathList<FCPath>();
		buildReady(targetFCPathList, false);
//	    }
	    settingPassword = true;
	}
    }
    
    private void setFont(TextField field)
    {
	long fontsize = Math.round(field.getWidth() / field.getText().length() * 1.3); if (fontsize > 14) {fontsize =14;} else if (fontsize < 8) {fontsize =8;}
	field.setStyle("-fx-font-family: monospace; -fx-font-size: " + fontsize + "px;");
    }

//    @FXML
//    private void encryptionModeAnchorPaneOnMouseClicked(MouseEvent event)
//    {
//    }

    @FXML
    private void keyLabelAnchorOnMouseExited(MouseEvent event)
    {
	keyLabel.setVisible(false);
    }

    @FXML  private void keyLabelAnchorOnMouseEntered(MouseEvent event)
    {
	keyLabel.setVisible(true);
    }
 }