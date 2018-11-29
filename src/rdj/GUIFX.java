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
import java.net.URI;
import java.net.URISyntaxException;
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
import javafx.scene.control.ProgressIndicator;
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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

public class GUIFX extends Application implements UI, Initializable
{
    private Stage stage;
    private Label label;
    @FXML
    private TabPane tab;
    @FXML
    private Tab encryptTab;
    @FXML
    private Tab logTab;
    @FXML
    private TextArea logTextArea;
    @FXML
    private Button encryptButton;
    @FXML
    private ProgressBar filesProgressBar;
    @FXML
    private ProgressBar fileProgressBar;
    @FXML
    private Label statusLabel;    

    FinalCrypt finalCrypt;
    UI ui;
    GUIFX guifx;
    private JFileChooser targetFileChooser;
    private boolean negatePattern;
    public JFileChooser keyFileChooser;
    @FXML
    private SwingNode keyFileSwingNode;
    private JButton targetFileDeleteButton;
    private JButton keyFileDeleteButton;
//    private boolean hasEncryptable;
//    private boolean hasKeyItem;
    private Object root;
    private Version version;

    @FXML
    private ToggleButton pauseToggleButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button updateButton;
    private boolean processRunning;
    private final int NONE = 0;
    private final int ENCRYPT = 1;
    private final int DECRYPT = 2;
    private final int CREATE = 3;
    private final int CLONE = 4;
    private int processRunningType = NONE;
    @FXML
    private Label copyrightLabel;
//    private TimerTask updateProgressTask;
//    private Timer updateProgressTaskTimer;
    @FXML
    private ProgressIndicator cpuIndicator;
    private MBeanServer mbs;
    private ObjectName name;
    private AttributeList attribList;
    private Attribute att;
    private Double value;
    private GridPane logButtonGridPane;
    private FileFilter nonFinalCryptFilter;
    private FileNameExtensionFilter finalCryptFilter;
    private DeviceManager deviceManager;
    private int lineCounter;
    private Configuration configuration;
    @FXML
    private SwingNode targetFileSwingNode;
    private Path keyPath;
    private FCPath keyFCPath;
//    private ArrayList<Path> targetPathList;
    private boolean symlink = false;
    private final String procCPULoadAttribute = "ProcessCpuLoad";
    @FXML
    private Button decryptButton;
    @FXML
    private Label keyNameLabel;
    @FXML
    private Label keyTypeLabel;
    @FXML
    private Label keySizeLabel;
    @FXML
    private Label keyValidLabel;
    @FXML
    private Label encryptedLabel;
    @FXML
    private Label encryptRemainingLabel;
    @FXML
    private Label encryptableSizeLabel;
    @FXML
    private Label encryptedSizeLabel;
    @FXML
    private Label encryptRemainingSizeLabel;
    @FXML
    private Label decryptableLabel;
    @FXML
    private Label decryptedLabel;
    @FXML
    private Label decryptRemainingLabel;
    @FXML
    private Label decryptableSizeLabel;
    @FXML
    private Label decryptedSizeLabel;
    @FXML
    private Label decryptRemainingSizeLabel;
    @FXML
    private Label unencryptableLabel;
    @FXML
    private Label undecryptableSizeLabel;
    @FXML
    private Label unencryptableSizeLabel;
    @FXML
    private Label undecryptableLabel;
    @FXML
    private Label encryptableLabel;
    @FXML
    private Label newEncryptedLabel;
    @FXML
    private Label newEncryptedSizeLabel;
    @FXML
    private Label newDecryptedLabel;
    @FXML
    private Label newDecryptedSizeLabel;
    @FXML
    private Label invalidFilesLabel;
    @FXML
    private Label totalFilesLabel;
    private Label totalFilesSizeLabel;
    @FXML
    private Label validDevicesLabel;
    @FXML
    private Label validDevicesSizeLabel;
    @FXML
    private Label validPartitionsLabel;
    @FXML
    private Label validPartitionsSizeLabel;
    @FXML
    private Label invalidFilesSizeLabel;
    @FXML
    private Label validFilesLabel;
    @FXML
    private Label validFilesSizeLabel;
    @FXML
    private Label filesSizeLabel;
    @FXML
    private Label emptyFilesLabel;
    @FXML
    private Label symlinkFilesLabel;
    @FXML
    private Label unreadableFilesLabel;
    @FXML
    private Label unreadableFilesSizeLabel;
    @FXML
    private Label unwritableFilesLabel;
    @FXML
    private Label unwritableFilesSizeLabel;
    @FXML
    private Label hiddenFilesLabel;
    @FXML
    private Label hiddenFilesSizeLabel;
    
    @FXML
    private Label targetWarningLabel;
    
    private FCPathList targetFCPathList; // Main List

    // Filtered Lists
    private FCPathList decryptedList; 
    private FCPathList encryptableList;

    private FCPathList encryptedList; 
    private FCPathList decryptableList;
    
    private FCPathList createKeyList;
    private FCPathList cloneKeyList;
    private FCPathList customList;
    
    private FCPathList emptyList; 
    private FCPathList symlinkList;
    private FCPathList unreadableList;
    private FCPathList unwritableList;
    private FCPathList hiddenList;

    private FCPathList newEncryptedList;
    private FCPathList encryptRemainingList;
    private FCPathList unencryptableList;
    private FCPathList newDecryptedList;
    private FCPathList decryptRemainingList;
    private FCPathList undecryptableList;
    private FCPathList invalidFilesList;

    
    @FXML
    private Button keyDeviceButton;
    @FXML
    private Label totalTimeLabel;
    @FXML
    private Label remainingTimeLabel;
    @FXML
    private Label elapsedTimeLabel;
    
    private long	bytesTotal;	
    private long	bytesProcessed;	
    private double	processedTotalRatio;
//    private long	bytesPerSecond;	
   
    private Calendar	startTimeCalendar;
    private Calendar	start2TimeCalendar;
    private Calendar	nowTimeCalendar;
    private Calendar	elapsedTimeCalendar;
   
    private Calendar	totalTimeCalendar;
    private Calendar	remainingTimeCalendar;
    private long	bytesPerMilliSecond;
    private Timeline updateClockTimeLine;
    private int offSetHours;
    private int offSetMinutes;
    private int offSetSeconds;
    private Calendar offsetTimeCalendar;
    private boolean clockUpdated;
    private TimerTask updateDashboardTask;
    private Timer updateDashboardTaskTimer;
    private String pattern;
    @FXML
    private Label emptyFilesHeaderLabel;
    @FXML
    private Label symlinkFilesHeaderLabel;
    @FXML
    private Label unreadableFilesHeaderLabel;
    @FXML
    private Label unwritableFilesHeaderLabel;
    @FXML
    private Label hiddenFilesHeaderLabel;
    @FXML
    private Button websiteButton;
    @FXML
    private Label checksumLabel;
    private Tooltip checksumTooltip;
    private boolean keySourceChecksumReadEnded;
    private boolean keySourceChecksumReadCanceled;
    private Stage createOTPKeyStage;
    private CreateOTPKey createOTPKey;
    private Preferences prefs;
    private long now;
    private boolean isCalculatingCheckSum;
    @FXML
    private GridPane dashboardGridPane;
    @FXML
    private ToggleButton encryptionModeToggleButton;
    @FXML
    private Tooltip encryptionModeToolTip;
    @FXML
    private AnchorPane encryptionModeAnchorPane;
    private long lastRawModeClicked;
    private Timeline flashMACTimeline;
    private Timeline autoDisableTimeline;
    
    @Override
    public void start(Stage stage) throws Exception
    {
        ui = this;
        guifx = this;
        this.stage = stage;
        root = FXMLLoader.load(getClass().getResource("GUIFX.fxml"));
        Scene scene = new Scene((Parent)root);
        
//        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); }catch(Exception e){ System.out.println("Exception: setLookAndFeel: " + e.getMessage()); }
        
        stage.setScene(scene);
        stage.setTitle(Version.getProduct());
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.setMaximized(true);
        stage.setOnCloseRequest(e -> Platform.exit());
	stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (WindowEvent window) ->	{ System.exit(0); });
        stage.show();
        
        version = new Version(ui);
        stage.setTitle(Version.getProduct() + " " + version.getCurrentlyInstalledOverallVersionString());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        targetFileDeleteButton = new javax.swing.JButton();
        targetFileDeleteButton.setFont(new java.awt.Font("Arimo", 0, 11)); // NOI18N
        targetFileDeleteButton.setText("Delete"); // X🗑❌❎⛔ (no utf8)
        targetFileDeleteButton.setEnabled(false);
        targetFileDeleteButton.setToolTipText("Delete selected item(s)");
        targetFileDeleteButton.addActionListener(new java.awt.event.ActionListener()
        { public void actionPerformed(java.awt.event.ActionEvent evt) { targetFileDeleteButtonActionPerformed(evt); } });

        keyFileDeleteButton = new javax.swing.JButton();
        keyFileDeleteButton.setFont(new java.awt.Font("Arimo", 0, 11)); // NOI18N
        keyFileDeleteButton.setText("Delete"); // X🗑❌❎⛔ (no utf8)
        keyFileDeleteButton.setEnabled(false);
        keyFileDeleteButton.setToolTipText("Delete selected item");
        keyFileDeleteButton.addActionListener(new java.awt.event.ActionListener()
        { public void actionPerformed(java.awt.event.ActionEvent evt) { keyFileDeleteButtonActionPerformed(evt); } });
        
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
        targetFileChooser.setFont(new Font("Open Sans", Font.PLAIN, 10));
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
        keyFileChooser.setFont(new Font("Open Sans", Font.PLAIN, 10));
        keyFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        keyFileChooser.addPropertyChangeListener((java.beans.PropertyChangeEvent evt) -> { keyFileChooserPropertyChange(evt); });
//	keyFileChooser.add
        keyFileChooser.addActionListener( (java.awt.event.ActionEvent evt) -> { keyFileChooserActionPerformed(evt); });
        
        keyFileChooserComponentAlteration(keyFileChooser);
        Timeline timeline = new Timeline(new KeyFrame( Duration.millis(100), ae -> 
        {
            keyFileSwingNode.setContent(keyFileChooser);
        }
        )); timeline.play();

        finalCrypt = new FinalCrypt(this); finalCrypt.start();
//        device = new Device(this); device.start();

        welcome();
    }
    
    public double getProcessCpuLoad()
    {
        try { attribList = mbs.getAttributes(name, new String[]{ procCPULoadAttribute });}
        catch (InstanceNotFoundException | ReflectionException ex) { log(ex.getMessage(), true, true, true, true ,false); }
        
        if (attribList.isEmpty()) { return Double.NaN; }
        att = (Attribute)attribList.get(0);
        value  = (Double)att.getValue();    
        return value;
    }

    private void welcome()
    {
	targetFCPathList = new FCPathList(); updateDashboard(targetFCPathList);
        configuration = new Configuration(ui);
        version = new Version(ui);
        version.checkCurrentlyInstalledVersion(this);
        log("Welcome to " + Version.getProduct() + " " + version.getCurrentlyInstalledOverallVersionString() + "\r\n", true, false, false, false ,false);        
        log(   "Welcome to:      " + Version.getProduct() + " " + version.getCurrentlyInstalledOverallVersionString() + "\r\n", false, true, true, false ,false);
        log("\r\n", false, true, true, false ,false);
        log(   "Copyright:       " + Version.getCopyright() + " " + Version.getAuthor() + "\r\n", false, true, true, false ,false);
        log(   "Email:           " + Version.getAuthorEmail() + "\r\n", false, true, true, false ,false);
        log(   "Logfiles:        " + configuration.getLogDirPath().toString() + "\r\n", false, true, true, false ,false); // System.getProperty("java.version")
        log(   "License:	 " + Version.getLicense() + "\r\n", false, true, true, false ,false);
        log("\r\n", false, true, true, false ,false);
        log(   "OS Name:         " + System.getProperty("os.name") + "\r\n", false, true, true, false ,false);
        log(   "OS Architecture: " + System.getProperty("os.arch") + "\r\n", false, true, true, false ,false);
        log(   "OS Version:      " + System.getProperty("os.version") + "\r\n", false, true, true, false ,false);
        log("\r\n", false, true, true, false ,false);
        log(   "Java Vendor:     " + System.getProperty("java.vendor") + "\r\n", false, true, true, false ,false);
        log(   "Java Version:    " + System.getProperty("java.version") + "\r\n", false, true, true, false ,false);
        log(   "Class Version:   " + System.getProperty("java.class.version") + "\r\n", false, true, true, false ,false);
        log("\r\n", false, true, true, false ,false);
        log(   "User Name:       " + System.getProperty("user.name") + "\r\n", false, true, true, false ,false);
        log(   "User Home:       " + System.getProperty("user.home") + "\r\n", false, true, true, false ,false);
        log(   "User Dir:        " + System.getProperty("user.dir") + "\r\n", false, true, true, false ,false);
        log("\r\n", false, true, true, false ,false);
        log("Tip: FinalCrypt command line (DOS) usage:\r\n", false, true, true, false ,false);
        log("java -cp FinalCrypt.jar rdj/CLUI --help\r\n", false, true, true, false ,false);
        log("\r\n", false, true, true, false ,false);
        copyrightLabel.setText("Copyright: " + Version.getCopyright() + " " + Version.getAuthor());

//      cpuIndicator
        Rectangle rect = new Rectangle(0, 0, 100, 100); Tooltip cpuIndicatorToolTip = new Tooltip("Process CPU Load"); Tooltip.install(rect, cpuIndicatorToolTip);
        cpuIndicator.setTooltip(cpuIndicatorToolTip);

//      for: ProcessCpuLoad()
//        procCPULoadAttribute = "ProcessCpuLoad";
        mbs = ManagementFactory.getPlatformMBeanServer();
        try {name    = ObjectName.getInstance("java.lang:type=OperatingSystem"); }
        catch (MalformedObjectNameException | NullPointerException ex) { log(ex.getMessage(), true, true, true, true ,false); }
        
        Timeline timeline = new Timeline(new KeyFrame( Duration.millis(200), ae ->
                cpuIndicator.setProgress(getProcessCpuLoad())
        )); timeline.setCycleCount(Animation.INDEFINITE); timeline.play();
	
	checksumTooltip = new Tooltip(""); checksumTooltip.setFont(javafx.scene.text.Font.font(javafx.scene.text.Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 13));

        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                String title =  "Welcome to " + Version.getProduct();
                String header = "Brief Introduction:";
                String infotext = "";
                infotext += "Step 0 Optionally create an OTP key file below.\r\n";
                infotext += "Step 1 Select items to en/decrypt on the left.\r\n";
                infotext += "Step 2 Select your (OTP) key file on the right.\r\n";
                infotext += "Step 3 Click [Encrypt] / [Decrypt] button below.\r\n";
                infotext += "\r\n";
                infotext += "Optional:\r\n";
                infotext += "\r\n";
                infotext += "Double click to open files.\r\n";
                infotext += "Click [LOG] to see details.\r\n";
                infotext += "Click [Check Update] sometimes.\r\n";
                infotext += "Tip:  Watch statusbar at bottom.\r\n";
                infotext += "Tip:  Make backups of your data.\r\n";
                infotext += "Tip:  Keep your keys secret on external Storage.\r\n";
                infotext += "\r\n";
                infotext += "Live to love - Enjoy your privacy.\r\n\r\n";
/*
                Linux: ${user.home}/.java/.userPrefs/_\!\(\)\!~\!\"q\!#4\!\[w\"_\!%k\!\[g\"\}\!#@\!\<\!\=\=/prefs.xml 
                
                For Windows systemRoot and userRoot are stored in HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Prefs and HKEY_CURRENT_USER\Software\JavaSoft\Prefs respectively.
                For Unix systemRoot and userRoot are stored in "/etc/.java" and "${user.home}/.java/.userPrefs", respectively.
                Note that for Unix the locations can be changed by specifying "java.util.prefs.userRoot" and "java.util.prefs.systemRoot" properties
                Mac OS X ~/Library/Preferences in multiple plist files.
                Mac OS X uses the java.util.prefs.MacOSXPreferencesFactory class. See lists.apple.com/archives/java-dev/2010/Jul/msg00056.html 
                the java.util.prefs.MacOSXPreferencesFactory class should be in rt.jar in JDK 1.7 or later.
                See hg.openjdk.java.net/macosx-port/macosx-port/jdk/file/… for the source code.
                JDK 8 all the items in java.util.prefs:                 
*/                
                
                prefs = Preferences.userRoot().node(this.getClass().getName());

//		Hide Intro
		String val = prefs.get("Hide Intro", "Unknown"); // if no val then "Unknown" prefs location registry: HKEY_CURRENT_USER\Software\JavaSoft\Prefs

                if (! val.equals("Yes"))
                {
                    Alert alert = introAlert(AlertType.INFORMATION, title, header, infotext, "Don't show again", param -> prefs.put("Hide Intro", param ? "Yes" : "No"),  ButtonType.OK);
                    if (alert.showAndWait().filter(t -> t == ButtonType.OK).isPresent()) {    }                                
                }

//		Last Update Checked
		long updateChecked = 0; // Epoch date
//		long updateCheckPeriod = 1000L*20L; // Just to test auto update function
		long updateCheckPeriod = 1000L*60L*60L*24L; // Update period 1 Day
		now = Calendar.getInstance().getTimeInMillis(); // Epoch date
		val = prefs.get("Update Checked", "Unknown"); // if no val then "Unknown" prefs location registry: HKEY_CURRENT_USER\Software\JavaSoft\Prefs
		boolean invalidUpdateCheckedValue = false;
		try { updateChecked = Long.valueOf(val); } catch (NumberFormatException e) { invalidUpdateCheckedValue = true; }
		if ( invalidUpdateCheckedValue ) { checkUpdate(); } else { if (now - updateChecked >= updateCheckPeriod) { checkUpdate(); } }
            }
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
    
    public Alert introAlert(AlertType type, String title, String headerText, String message, String optOutMessage, Consumer<Boolean> optOutAction, ButtonType... buttonTypes)
    {
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

    private void checkUpdate()
    {
        Platform.runLater(new Runnable()
	{
	    @Override
	    public void run()
	    {
		String alertString = "";
		version = new Version(ui);
		version.checkCurrentlyInstalledVersion(GUIFX.this);
		version.checkLatestOnlineVersion(GUIFX.this); prefs.putLong("Update Checked", now);
		String[] lines = version.getUpdateStatus().split("\r\n");
		for (String line: lines) { log(line + "\r\n", true, true, true, false, false);}
		
		alertString = "Download new version: " + version.getLatestOnlineOverallVersionString() + "?\r\n\r\n";
		if (! version.getLatestReleaseNotesString().isEmpty())	    { alertString += version.getLatestReleaseNotesString() + "\r\n"; }
		if (! version.getLatestVersionMessageString().isEmpty())    { alertString += version.getLatestVersionMessageString() + "\r\n"; }
		if (( ! version.getLatestAlertSubjectString().isEmpty()) && ( ! version.getLatestAlertMessageString().isEmpty() ))
		{
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
		}
		if ( (version.versionIsDifferent()) && (version.versionCanBeUpdated()) )
		{
		    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, alertString, ButtonType.YES, ButtonType.NO);
		    alert.setHeaderText("Download Update?");
		    alert.showAndWait();
		    
		    if (alert.getResult() == ButtonType.YES)
		    {
			Thread updateThread;
			updateThread = new Thread(() ->
			{
			    try { try {  Desktop.getDesktop().browse(new URI(Version.REMOTEPACKAGEDOWNLOADURISTRING)); }
			    catch (URISyntaxException ex)   { log(ex.getMessage(), true, true, true, true, false); }}
			    catch (IOException ex)	    { log(ex.getMessage(), true, true, true, true, false); }
			});
			updateThread.setName("updateThread");
			updateThread.setDaemon(true);
			updateThread.start();
		    }
		}
	    }
	});
    }

//  Custom FileChooserDelete Listener methods
    private void targetFileDeleteButtonActionPerformed(java.awt.event.ActionEvent evt)                                                
    {
//        PlatformImpl.runAndWait(new Runnable()
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
	    {
                String itemword = "";
                if ( targetFileChooser.getSelectedFiles().length == 1 )      { itemword = "item"; }
                else if ( targetFileChooser.getSelectedFiles().length > 1 )  { itemword = "items"; }
                String selection = "Delete " + targetFileChooser.getSelectedFiles().length + " selected " + itemword + "?";
                Alert alert = new Alert(AlertType.CONFIRMATION, selection, ButtonType.YES, ButtonType.NO);alert.setHeaderText("Confirm Deletion?"); alert.showAndWait();
                if (alert.getResult() == ButtonType.YES)
                {
                    if ((targetFileChooser != null)  && (targetFileChooser.getSelectedFiles() != null))
                    {
                        if ( targetFileChooser.getSelectedFiles().length > 0 )
                        {
                            ArrayList<Path> pathList = finalCrypt.getPathList(targetFileChooser.getSelectedFiles());
                            boolean delete = true;
                            boolean returnpathlist = false;
                            String pattern = "glob:*";
                            finalCrypt.deleteSelection(pathList, delete, returnpathlist, pattern, false);
			    updateFileChoosers();
                        }
                    }
                }
            }
        });
    }                                               

    private void keyFileDeleteButtonActionPerformed(java.awt.event.ActionEvent evt)                                                
    {                                                            
//        PlatformImpl.runAndWait(new Runnable()
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                String itemword = "";
                if ( keyFileChooser.getSelectedFiles().length == 1 )      { itemword = "item"; }
                else if ( keyFileChooser.getSelectedFiles().length > 1 )  { itemword = "items"; }
                String selection = "Delete " + keyFileChooser.getSelectedFiles().length + " selected " + itemword + "?";
                Alert alert = new Alert(AlertType.CONFIRMATION, selection, ButtonType.YES, ButtonType.NO);alert.setHeaderText("Confirm Deletion?"); alert.showAndWait();
                if (alert.getResult() == ButtonType.YES)
                {
                    if ((keyFileChooser != null)  && (keyFileChooser.getSelectedFiles() != null))
                    {
                        if ( keyFileChooser.getSelectedFiles().length > 0 )
                        {
                            ArrayList<Path> pathList = finalCrypt.getPathList(keyFileChooser.getSelectedFiles());
                            boolean delete = true;
                            boolean returnpathlist = false;
                            String pattern = "glob:*";
                            finalCrypt.deleteSelection(pathList, delete, returnpathlist, pattern, false);
			    updateFileChoosers();
                        }
                    }
                }
		
//                String selection = "Delete 1 selected item?";
//                Alert alert = new Alert(AlertType.CONFIRMATION, selection, ButtonType.YES, ButtonType.NO);alert.setHeaderText("Confirm Deletion?"); alert.showAndWait();
//                if (alert.getResult() == ButtonType.YES)
//                {
//                    if ((keyFileChooser != null)  && (keyFileChooser.getSelectedFiles() != null))
//                    {
//                        ArrayList<Path> pathList = new ArrayList<>();
//                        pathList.add(keyFileChooser.getSelectedFile().toPath());
//                        boolean delete = true;
//                        boolean returnpathlist = false;
//                        String pattern = "glob:*";
//                        finalCrypt.deleteSelection(pathList, delete, returnpathlist, pattern, false);
//			updateFileChoosers();
//                    }
//                }
            }
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
        negatePattern = false; String pattern = "glob:*";
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
        if      ( desc.startsWith("FinalCrypt") )       { negatePattern = false; pattern = "glob:*.bit"; }
        else if ( desc.startsWith("NON FinalCrypt") )   { negatePattern = true;  pattern = "glob:*.bit"; }
//        else if ( desc.startsWith("NON FinalCrypt") )   { negatePattern = true; pattern = "glob:*.[!b][!i][!t]"; }
        else                                            { negatePattern = false; pattern = "glob:*"; }
        return pattern;
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
		Platform.runLater(new Runnable(){ @Override public void run() {
		    encryptButton.setDisable(true); decryptButton.setDisable(true);
		    keyDeviceButton.setDisable(true); keyDeviceButton.setText("Create OTP Key File");
		}});
	    }
//												  isKey device  minsize  symlink  writable status
	    else if (Validate.isValidFile(this, "", keyFileChooser.getSelectedFile().toPath(), true,     false,      0L, true,    false,  true))
	    {
		try { Desktop.getDesktop().open(keyFileChooser.getSelectedFile()); }
		catch (IOException ex) { log("Error: Desktop.getDesktop().open(keyFileChooser.getSelectedFile()); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
		targetFCPathList = new FCPathList(); this.updateDashboard(targetFCPathList);
		Platform.runLater(new Runnable(){ @Override public void run() {
		    encryptButton.setDisable(true); decryptButton.setDisable(true);
		    keyDeviceButton.setDisable(false); keyDeviceButton.setText("Create OTP Key File");
		}});
	    }
        }
	else
	{
	    encryptButton.setDisable(true); decryptButton.setDisable(true);
	    keyDeviceButton.setDisable(false); keyDeviceButton.setText("Create OTP Key File");
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
//							Validate.getFCPath(UI ui, String caller, Path path, boolean isKey, Path keyPath, boolean report)
		Path path = Paths.get("."); keyFCPath = Validate.getFCPath(   ui,	     "",      path,          false,         path,          true);
	    }
	    
	    Path targetPath = targetFileChooser.getSelectedFile().toPath();
//					   getFCPath(UI ui,  String caller,  Path path,  boolean isKey,   Path keyPath, boolean report)
	    FCPath targetFCPath = Validate.getFCPath( this,		"", targetPath,		 false, keyFCPath.path,		  true);
	    
	    if ((targetFCPath.type == FCPath.DEVICE) || (targetFCPath.type == FCPath.DEVICE_PROTECTED))
	    {
		tab.getSelectionModel().select(1);
		DeviceManager deviceManager = new DeviceManager(this); deviceManager.start(); deviceManager.printGPT(targetFCPath);
		targetFCPathList = new FCPathList(); updateDashboard(targetFCPathList);
		Platform.runLater(new Runnable(){ @Override public void run() {
		    encryptButton.setDisable(true); decryptButton.setDisable(true);
		    keyDeviceButton.setDisable(false); keyDeviceButton.setText("Create OTP Key File");
		}});
	    }
	    else // Not a Device
	    {
//							device  minsize	 symlink  writable  status
		if ((targetFCPath.isValidFile) || (targetFCPath.type == FCPath.SYMLINK))
		{
		    if ((targetFCPath.isEncrypted) && ( targetFCPath.isDecryptable ) && ( keyFCPath != null ) && ( keyFCPath.isValidKey ))
		    {
			Thread encryptThread = new Thread(new Runnable()
			{
//			    private DeviceManager deviceManager;
			    @Override
			    @SuppressWarnings({"static-access"})
			    public void run()
			    {
				FCPathList targetFCPathList = new FCPathList();
				FCPathList fileteredTargetFCPathList = new FCPathList();
				targetFCPathList.add(targetFCPath);
				fileteredTargetFCPathList.add(targetFCPath);
				
				decrypt(targetFCPathList, fileteredTargetFCPathList, keyFCPath);
				Path newPath = Paths.get(targetFCPath.path.toString().substring(0, targetFCPath.path.toString().lastIndexOf('.')));
				try { Thread.sleep(300); } catch (InterruptedException ex) {  } // Hangs in FinalCrypt.encryptSelection method (somewhere after shred)
				
				Desktop desktop = Desktop.getDesktop(); try { desktop.open(newPath.toFile()); } catch (IOException ex) { log("Error: Desktop.getDesktop().open(file); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
			    }
			});
			encryptThread.setName("encryptThread");
			encryptThread.setDaemon(true);
			encryptThread.start();
		    }
		    else // Not decryptable
		    { 
			try { Desktop.getDesktop().open(targetFCPath.path.toFile()); } catch (IOException ex) { log("Error: Desktop.getDesktop().open(file); " + ex.getMessage() + "\r\n", true, true, true, true, false); }
		    }
		    
		    
		    targetFCPathList = new FCPathList(); updateDashboard(targetFCPathList);
		    Platform.runLater(new Runnable(){ @Override public void run() { encryptButton.setDisable(true); decryptButton.setDisable(true); keyDeviceButton.setDisable(false); keyDeviceButton.setText("Create OTP Key File"); }});
		} // Not a device / file or symlink
	    }
        } else { encryptButton.setDisable(true); decryptButton.setDisable(true); }
        targetFileChooser.setFileFilter(this.nonFinalCryptFilter); targetFileChooser.setFileFilter(targetFileChooser.getAcceptAllFileFilter()); // Resets rename due to doucle click file
    }                                                

/////////////////////////////////////////////////////////////////////////////////////////////
    
    synchronized private void keyFileChooserPropertyChange(java.beans.PropertyChangeEvent evt) { if ((!processRunning ) && (evt.getPropertyName().equals("SelectedFilesChangedProperty"))) { keyFileChooserPropertyCheck(); } }
    
    synchronized private void keyFileChooserPropertyCheck() // getFCPath, checkModeReady
    {
//	log("keyFileChooserPropertyCheck: " + Calendar.getInstance().getTimeInMillis() + "\r\n", true, true, false, false, false);
        Platform.runLater(new Runnable(){ @Override public void run() 
        {
            keyNameLabel.setTextFill(Color.GREY); keyNameLabel.setText("");
            keyTypeLabel.setTextFill(Color.GREY); keyTypeLabel.setText("");
            keySizeLabel.setTextFill(Color.GREY); keySizeLabel.setText("");
            keyValidLabel.setTextFill(Color.GREY); keyValidLabel.setText("");
            checksumLabel.setTextFill(Color.GREY); checksumLabel.setText("");
        }});

	keySourceChecksumReadEnded = true;
	keySourceChecksumReadCanceled = true;
	
//	if ((!processRunning ) && (!MySimpleFCFileVisitor.running))
	if (!processRunning)
	{
	    Platform.runLater(new Runnable(){ @Override public void run() 
	    {
		fileProgressBar.setProgress(0);
		filesProgressBar.setProgress(0);
	    }});

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
//					      getFCPath(UI ui, String caller,  Path path, boolean isKey, Path keyPath, boolean report)
		keyFCPath = Validate.getFCPath(   this,	    "", keyPath,             true,      keyPath,           true);

		Platform.runLater(new Runnable(){ @Override public void run() 
		{
		    if ((keyFCPath.isValidKey)) // Valid Key
		    {
//			log("CC Key Valid\r\n");
			// Set Key Status Colors
			keyNameLabel.setTextFill(Color.GREENYELLOW); keyNameLabel.setText(keyFCPath.path.getFileName().toString());
			checksumLabel.setTextFill(Color.WHITESMOKE);
			checksumLabel.setText("");
			Tooltip.uninstall(checksumLabel, checksumTooltip);
//			Tooltip.install(checksumLabel, checksumTooltip); 
			
//			try { Thread.sleep(50); } catch (InterruptedException ex) {  } // Just to update GUI

			keyTypeLabel.setTextFill(Color.GREENYELLOW); keyTypeLabel.setText(FCPath.getTypeString(keyFCPath.type));
			keySizeLabel.setTextFill(Color.GREENYELLOW); keySizeLabel.setText(Validate.getHumanSize(keyFCPath.size,1));
			keyValidLabel.setTextFill(Color.GREENYELLOW); keyValidLabel.setText(Boolean.toString(keyFCPath.isValidKey));
						
			targetFileChooserPropertyCheck(true);
		    }
		    else // Not Valid Key
		    {
//			log("CC Key Not Valid\r\n");
			// Set Key Status Colors
			if (keyFCPath.type == FCPath.DIRECTORY)
			{
			    keyNameLabel.setTextFill(Color.GREY); keyNameLabel.setText("");
			    keyTypeLabel.setTextFill(Color.GREY); keyTypeLabel.setText("");
			    keySizeLabel.setTextFill(Color.GREY); keySizeLabel.setText("");
			    keyValidLabel.setTextFill(Color.GREY); keyValidLabel.setText("");
			    checksumLabel.setTextFill(Color.GREY); checksumLabel.setText("");
			}
			else
			{
			    keyNameLabel.setTextFill(Color.ORANGE); keyNameLabel.setText(keyFCPath.path.toString());
			    if (keyFCPath.type != FCPath.FILE)	{ keyTypeLabel.setTextFill(Color.ORANGERED); }
			    else					{ keyTypeLabel.setTextFill(Color.ORANGE); }
			    keyTypeLabel.setText(FCPath.getTypeString(keyFCPath.type));
			    if ( keyFCPath.size < FCPath.KEY_SIZE_MIN ) { keySizeLabel.setTextFill(Color.ORANGERED); } else { keySizeLabel.setTextFill(Color.ORANGE); } keySizeLabel.setText(Validate.getHumanSize(keyFCPath.size,1));
			    checksumLabel.setText(""); checksumTooltip.setText("");
			    Tooltip.uninstall(checksumLabel, checksumTooltip);
			    // Tooltip.install(checksumLabel, checksumTooltip); 
			    keyValidLabel.setTextFill(Color.ORANGE); keyValidLabel.setText(Boolean.toString(keyFCPath.isValidKey));			    
			}
			
//			MySimpleFCFileVisitor.running = false;
//		        try { Thread.sleep(100); } catch (InterruptedException ex) {  }
			if ( keyFCPath != null ) { keyFCPath.isValidKey = false; }
			targetFCPathList = new FCPathList();
			buildReady(targetFCPathList);
		    }
		}});		
		
		// Checksum Calculation
		if ((keyFCPath.isValidKey)) // Valid Key
		{
		    if ( keyFCPath.size < (1024L * 1024L * 1024L * 1L) )
		    {
			Platform.runLater(new Runnable(){ @Override public void run() 
			{
			    checksumLabel.setTextFill(Color.WHITESMOKE);
			    checksumLabel.setText("Calculating...");
			    Tooltip.uninstall(checksumLabel, checksumTooltip);
//			    Tooltip.install(checksumLabel, checksumTooltip); 
//			    try { Thread.sleep(50); } catch (InterruptedException ex) {  } // Just to update GUI
			    calculateChecksum();
			}});
		    }
		    else
		    {
			Platform.runLater(new Runnable(){ @Override public void run() 
			{
			    checksumLabel.setTextFill(Color.WHITESMOKE);
			    checksumLabel.setText("Click for checksum");
			    Tooltip.uninstall(checksumLabel, checksumTooltip);
//			    Tooltip.install(checksumLabel, checksumTooltip); 
//			    try { Thread.sleep(50); } catch (InterruptedException ex) {  } // Just to update GUI
			}});
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
                
                Platform.runLater(new Runnable(){ @Override public void run() 
                {
                    keyNameLabel.setTextFill(Color.GREY); keyNameLabel.setText("");
                    keyTypeLabel.setTextFill(Color.GREY); keyTypeLabel.setText("");
                    keySizeLabel.setTextFill(Color.GREY); keySizeLabel.setText("");
                    keyValidLabel.setTextFill(Color.GREY); keyValidLabel.setText("");
                    checksumLabel.setTextFill(Color.GREY); checksumLabel.setText("");
                }});

		buildReady(targetFCPathList);
	    }
	}
    }

    synchronized private void calculateChecksum()
    {
	if (!isCalculatingCheckSum)
	{
	    isCalculatingCheckSum = true;
//	    log("calculateChecksum: " + Calendar.getInstance().getTimeInMillis() + "\r\n", true, true, false, false, false);
	    Platform.runLater(new Runnable(){ @Override public void run() 
	    {
		if ((keyFCPath.isValidKey)) // Valid Key
		{
		    // Calculate Key SHA-1 Checksum 
		    checksumBlock:
		    {
			keySourceChecksumReadEnded = false;
			keySourceChecksumReadCanceled = false;
			Thread calcKeyThread = new Thread(new Runnable() { @Override@SuppressWarnings({"static-access"})public void run() // Relaxed interruptable thread
			{
			    long    readKeySourceChannelPosition =  0; 
			    long    readKeySourceChannelTransfered =  0; 
			    int readKeySourceBufferSize = (1 * 1024 * 1024);
			    ByteBuffer keySourceBuffer = ByteBuffer.allocate(readKeySourceBufferSize); keySourceBuffer.clear();
			    MessageDigest messageDigest = null; try { messageDigest = MessageDigest.getInstance("SHA-1"); } catch (NoSuchAlgorithmException ex) { log("Error: NoSuchAlgorithmException: MessageDigest.getInstance(\"SHA-256\")\r\n", true, true, true, true, false);}
			    int x = 0;
			    while (( ! keySourceChecksumReadEnded ) && ( ! keySourceChecksumReadCanceled ))
			    {
				try (final SeekableByteChannel readKeySourceChannel = Files.newByteChannel(keyFCPath.path, EnumSet.of(StandardOpenOption.READ,StandardOpenOption.SYNC)))
				{
				    readKeySourceChannel.position(readKeySourceChannelPosition);
				    readKeySourceChannelTransfered = readKeySourceChannel.read(keySourceBuffer); keySourceBuffer.flip(); readKeySourceChannelPosition += readKeySourceChannelTransfered;
				    readKeySourceChannel.close();

	//				    checksumLabel.setText("SHA256 calculating: " + checksumStatusTotalTransfered);
				    messageDigest.update(keySourceBuffer);
				    if ( readKeySourceChannelTransfered < 0 ) { keySourceChecksumReadEnded = true; }
				} catch (IOException ex)
				{
				    Platform.runLater(new Runnable(){ @Override public void run()
				    {
					keySourceChecksumReadEnded = true;
    //				    ui.error("Error: readKeySourceChannel = Files.newByteChannel(..) " + ex.getMessage() + "\r\n"); 
				    }});
				}
				x++;
				keySourceBuffer.clear();
			    }
			    
			    if ( ! keySourceChecksumReadCanceled )
			    {
				byte[] hashBytes = messageDigest.digest();
				String hashString = getHexString(hashBytes,2);
				Platform.runLater(new Runnable(){ @Override public void run() {
				    checksumLabel.setTextFill(Color.GREENYELLOW);
				    checksumLabel.setText(hashString);
				    checksumTooltip.setText(hashString);
//				    Tooltip.uninstall(checksumLabel, checksumTooltip);
				    Tooltip.install(checksumLabel, checksumTooltip); 
				    isCalculatingCheckSum = false;
				}});
			    }
			    else
			    {
				messageDigest.reset();
				Tooltip.uninstall(checksumLabel, checksumTooltip);
//				Tooltip.install(checksumLabel, checksumTooltip);
				isCalculatingCheckSum = false;
			    }
			}});
			calcKeyThread.setName("calcKeyThread"); calcKeyThread.setDaemon(true); calcKeyThread.start();
		    }
    //		targetFileChooserPropertyCheck(true);
		}
	    }});
	}
    }
    
    @FXML private void checksumLabelOnMouseClicked(MouseEvent event)
    {
	checksumLabel.setTextFill(Color.WHITESMOKE);
	checksumLabel.setText("Calculating..."); checksumTooltip.setText("");
//	try { Thread.sleep(50); } catch (InterruptedException ex) {  } // Just to update GUI
	Platform.runLater(new Runnable(){ @Override public void run() { calculateChecksum(); }});
    }
    
    synchronized public static String getHexString(byte[] bytes, int digits) { String returnString = ""; for (byte mybyte:bytes) { returnString += getHexString(mybyte, digits); } return returnString; }
    synchronized public static String getHexString(byte value, int digits) { return String.format("%0" + Integer.toString(digits) + "X", (value & 0xFF)).replaceAll("[^A-Za-z0-9]",""); }

//  FileChooser Listener methods
    synchronized private void targetFileChooserPropertyChange(java.beans.PropertyChangeEvent evt) { if ((!processRunning ) && (evt.getPropertyName().equals("SelectedFilesChangedProperty"))) { targetFileChooserPropertyCheck(true); } }
    
    synchronized private void targetFileChooserPropertyCheck(boolean status)
    {
//	if ((!processRunning ) && (!MySimpleFCFileVisitor.running))
	if ((!processRunning ))
	{
//	    processRunning = true;
	    Platform.runLater(new Runnable(){ @Override public void run() 
	    {
		encryptButton.setDisable(true);
		decryptButton.setDisable(true);
		keyDeviceButton.setDisable(true);
		pauseToggleButton.setDisable(true);
		stopButton.setDisable(true);

		fileProgressBar.setProgress(0);
		filesProgressBar.setProgress(0);
	    }});
	    
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
		Platform.runLater(new Runnable(){ @Override public void run() { filesProgressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS); }});

		// BuildSelection
//		cursorWait();

		Platform.runLater(new Runnable(){ @Override public void run() // Not on FX Thread
		{
		    Thread scanThread = new Thread(new Runnable() { @Override@SuppressWarnings({"static-access"})public void run() // Relaxed interruptable thread
		    {
			Validate.buildSelection( ui, targetPathList, keyFCPath, targetFCPathList2, symlink, pattern, negatePattern, false);
		    }}); scanThread.setName("scanThread"); scanThread.setDaemon(true); scanThread.start();
		}});
	    }
	    else // Not all valid
	    {		
//		log("Not all valid: " + targetPathList.size() + "\r\n", true, false, false, false, false);
//		MySimpleFCFileVisitor.running = false;
//		try { Thread.sleep(100); } catch (InterruptedException ex) {  }
		targetFCPathList = new FCPathList();
		buildReady(targetFCPathList);
	    }
	}
    }
    
    @Override public void buildReady(FCPathList fcPathListParam)
    {
	Platform.runLater(new Runnable(){ @Override public void run() { filesProgressBar.setProgress(0); }});
	if (updateDashboardTaskTimer != null) { updateDashboardTaskTimer.cancel(); updateDashboardTaskTimer.purge(); }
	MySimpleFCFileVisitor.running = false;
	isCalculatingCheckSum = false;
	
	if (fcPathListParam.size() > 0)
	{
//	    log("buildReady > 0\r\n");
	    updateDashboard(fcPathListParam);
	    checkModeReady(fcPathListParam);
	}
	else
	{
//	    log("buildReady == 0\r\n");
	    fcPathListParam.clearStats();
	    encryptableList = new FCPathList();
	    updateDashboard(fcPathListParam);
	    checkModeReady(fcPathListParam);
	}
    }

    private void updateDashboard(FCPathList targetFCPathList)
    {
//	log(s + "\r\n" + targetFCPathList.getStats());
	Platform.runLater(new Runnable(){ @Override public void run() 
	{
	    // Skipping / Info Column
	    if ( (targetFCPathList.emptyFiles > 0) || (targetFCPathList.symlinkFiles > 0)  || (targetFCPathList.unreadableFiles > 0) || (targetFCPathList.unwritableFiles > 0))
	    { targetWarningLabel.setTextFill(Color.ORANGE); targetWarningLabel.setText("Skipping"); } else { targetWarningLabel.setTextFill(Color.GRAY); targetWarningLabel.setText("Invalid"); }
	    
	    if ( targetFCPathList.emptyFiles > 0 )	    { emptyFilesLabel.setTextFill(Color.ORANGE); } else { emptyFilesLabel.setTextFill(Color.GRAY); }
	    if ( targetFCPathList.symlinkFiles > 0 )	    { symlinkFilesLabel.setTextFill(Color.ORANGE); } else { symlinkFilesLabel.setTextFill(Color.GRAY); }
	    if ( targetFCPathList.unreadableFiles > 0 )	    { unreadableFilesLabel.setTextFill(Color.ORANGE); } else { unreadableFilesLabel.setTextFill(Color.GRAY); }
	    if ( targetFCPathList.unreadableFilesSize > 0 ) { unreadableFilesSizeLabel.setTextFill(Color.ORANGE); } else { unreadableFilesSizeLabel.setTextFill(Color.GRAY); }
	    if ( targetFCPathList.unwritableFiles > 0 )	    { unwritableFilesLabel.setTextFill(Color.ORANGE); } else { unwritableFilesLabel.setTextFill(Color.GRAY); }
	    if ( targetFCPathList.unwritableFilesSize > 0 ) { unwritableFilesSizeLabel.setTextFill(Color.ORANGE); } else { unwritableFilesSizeLabel.setTextFill(Color.GRAY); }
	    if ( targetFCPathList.hiddenFiles > 0 )	    { hiddenFilesLabel.setTextFill(Color.YELLOW); } else { hiddenFilesLabel.setTextFill(Color.GRAY); }
	    if ( targetFCPathList.hiddenFilesSize > 0 )	    { hiddenFilesSizeLabel.setTextFill(Color.YELLOW); } else { hiddenFilesSizeLabel.setTextFill(Color.GRAY); }

	    emptyFilesLabel.setText(Long.toString(targetFCPathList.emptyFiles));
	    symlinkFilesLabel.setText(Long.toString(targetFCPathList.symlinkFiles));
	    unreadableFilesLabel.setText(Long.toString(targetFCPathList.unreadableFiles));
	    unreadableFilesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.unreadableFilesSize,1));
	    unwritableFilesLabel.setText(Long.toString(targetFCPathList.unwritableFiles));
	    unwritableFilesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.unwritableFilesSize,1));
	    hiddenFilesLabel.setText(Long.toString(targetFCPathList.hiddenFiles));
	    hiddenFilesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.hiddenFilesSize,1));

	    // Decrypted Column
	    decryptedLabel.setText(Long.toString(targetFCPathList.decryptedFiles));
	    decryptedSizeLabel.setText(Validate.getHumanSize(targetFCPathList.decryptedFilesSize,1));

	    encryptableLabel.setText(Long.toString(targetFCPathList.encryptableFiles));
	    encryptableSizeLabel.setText(Validate.getHumanSize(targetFCPathList.encryptableFilesSize,1));

	    newEncryptedLabel.setText(Long.toString(targetFCPathList.newEncryptedFiles));
	    newEncryptedSizeLabel.setText(Validate.getHumanSize(targetFCPathList.newEncryptedFilesSize,1));
	    
	    encryptRemainingLabel.setText(Long.toString(targetFCPathList.encryptRemainingFiles));
	    encryptRemainingSizeLabel.setText(Validate.getHumanSize(targetFCPathList.encryptRemainingFilesSize,1));

	    unencryptableLabel.setText(Long.toString(targetFCPathList.unEncryptableFiles));
	    unencryptableSizeLabel.setText(Validate.getHumanSize(targetFCPathList.unEncryptableFilesSize,1));

	    // Encrypted Column
	    encryptedLabel.setText(Long.toString(targetFCPathList.encryptedFiles));
	    encryptedSizeLabel.setText(Validate.getHumanSize(targetFCPathList.encryptedFilesSize,1));

	    decryptableLabel.setText(Long.toString(targetFCPathList.decryptableFiles));
	    decryptableSizeLabel.setText(Validate.getHumanSize(targetFCPathList.decryptableFilesSize,1));

	    newDecryptedLabel.setText(Long.toString(targetFCPathList.newDecryptedFiles));
	    newDecryptedSizeLabel.setText(Validate.getHumanSize(targetFCPathList.newDecryptedFilesSize,1));
	    
	    decryptRemainingLabel.setText(Long.toString(targetFCPathList.decryptRemainingFiles));
	    decryptRemainingSizeLabel.setText(Validate.getHumanSize(targetFCPathList.decryptRemainingFilesSize,1));

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
	}});
    }
    
    private void disableButtons()
    {
	Platform.runLater(new Runnable(){ @Override public void run() 
	{
	    encryptButton.setDisable(true);
	    decryptButton.setDisable(true);
	    pauseToggleButton.setDisable(true);
	    stopButton.setDisable(true);
	}});
    }
    synchronized private void checkModeReady(FCPathList targetFCPathList)
    {
	Platform.runLater(new Runnable()
	{
	@Override public void run() 
	{
	    if (!processRunning)
	    {
		encryptButton.setDisable(true);
		decryptButton.setDisable(true);
		keyDeviceButton.setDisable(true);
		pauseToggleButton.setDisable(true);
		stopButton.setDisable(true);
		
		if ((keyFCPath != null) && (keyFCPath.isValidKey))
		{
// ================================================================================================================================================================================================
		    // Decrypted Files
		    
		    if (targetFCPathList.decryptedFiles > 0)	{ decryptedList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.isDecrypted); } else { decryptedList = null; }
		    if (targetFCPathList.encryptableFiles > 0) // Encryptables
		    {
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
		    if ((targetFCPathList.decryptableFiles > 0) && ( ! finalCrypt.disableMAC) ) // Prevents destruction! Non-MAC Mode encrypting MAC encrypted files (in stead of default decryption)
		    {
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


//    private FCPathList newEncryptedList;
//    private FCPathList encryptRemainingList;
//    private FCPathList unencryptableList;
//    private FCPathList newDecryptedList;
//    private FCPathList decryptRemainingList;
//    private FCPathList undecryptableList;
//    private FCPathList invalidFilesList;
		    
		    // Create Key Device
		    if ((keyFCPath.type == FCPath.FILE) && (keyFCPath.isValidKey))
		    {
			if (targetFCPathList.validDevices > 0)
			{
//			    log("1 " + keyFCPath.getString());
			    createKeyList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE); // log("Create Key List:\r\n" + createKeyList.getStats());
			    pauseToggleButton.setDisable(true); stopButton.setDisable(true);
			    keyDeviceButton.setDisable(false); keyDeviceButton.setText("Create Key Device");
			} else { keyDeviceButton.setDisable(false); keyDeviceButton.setText("Create OTP Key File"); }
		    }		
		    else if (keyFCPath.type == FCPath.DEVICE)
		    {
			// Clone Key Device
			if ((targetFCPathList.validDevices > 0) && (targetFCPathList.matchingKey == 0))
			{
			    cloneKeyList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE && fcPath.path.compareTo(keyFCPath.path) != 0); // log("Clone Key List:\r\n" + cloneKeyList.getStats());
			    keyDeviceButton.setDisable(false); keyDeviceButton.setText("Clone Key Device"); pauseToggleButton.setDisable(true); stopButton.setDisable(true);
			} else { keyDeviceButton.setDisable(false); keyDeviceButton.setText("Create OTP Key File"); }
		    } else { keyDeviceButton.setDisable(false); keyDeviceButton.setText("Create OTP Key File"); }
		}
		else
		{
		    encryptButton.setDisable(true); decryptButton.setDisable(true);
		    keyDeviceButton.setDisable(false); keyDeviceButton.setText("Create OTP Key File"); // Default enabler
		}
	    }
	}});
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
	    if(component instanceof Container) { component.setFont(new Font("System",Font.PLAIN,11)); }
	    
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
	    if(component instanceof Container) { component.setFont(new Font("System",Font.PLAIN,11)); }
	    
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
	processRunningType = ENCRYPT; filesProgressBar.setProgress(0.0); fileProgressBar.setProgress(0.0);
	processStarted(); finalCrypt.encryptSelection(targetFCPathList, encryptableList, keyFCPath, true);
    }

    @FXML
    private void decryptButtonAction(ActionEvent event)
    {
        Thread encryptThread = new Thread(new Runnable()
        {
            private DeviceManager deviceManager;
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
		decrypt(targetFCPathList, encryptableList, keyFCPath);
            }
        });
        encryptThread.setName("decryptThread");
        encryptThread.setDaemon(true);
        encryptThread.start();
//        // Needs Threading to early split off from the UI Event Dispatch Thread
////        final GUIFX guifx = this;
////        final UI this_ui = this;
//        Thread encryptThread = new Thread(new Runnable()
//        {
//            private DeviceManager deviceManager;
//            @Override
//            @SuppressWarnings({"static-access"})
//            public void run()
//            {
//		processRunningType = DECRYPT; filesProgressBar.setProgress(0.0); fileProgressBar.setProgress(0.0);
////		String pattern = "glob:*"; try { pattern = getSelectedPatternFromFileChooser( targetFileChooser.getFileFilter()); } catch (ClassCastException exc) { this_ui.error("Error: GUIFX: ClassCastException: " + exc.getMessage() + "\r\n"); }
//		processStarted(); finalCrypt.encryptSelection(targetFCPathList, decryptableList, keyFCPath, false);
//            }
//        });
//        encryptThread.setName("decryptThread");
//        encryptThread.setDaemon(true);
//        encryptThread.start();
    }

    private void decrypt(FCPathList targetSourceFCPathList, FCPathList filteredTargetSourceFCPathList, FCPath keySourceFCPath) // Only run within thread
    {
	processRunningType = DECRYPT; filesProgressBar.setProgress(0.0); fileProgressBar.setProgress(0.0);
	processStarted(); finalCrypt.encryptSelection(targetFCPathList, decryptableList, keyFCPath, false);
    }

    @FXML
    private void keyDeviceButtonAction(ActionEvent event)
    {
        // Needs Threading to early split off from the UI Event Dispatch Thread
        final GUIFX guifx = this;
        final UI ui = this;

//	Platform.runLater(new Runnable() { @Override public void run()
//	{
//	}});

	Thread encryptThread = new Thread(new Runnable()
        {
            private DeviceManager deviceManager;
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
		
		if ( keyDeviceButton.getText().equals("Create OTP Key File") )
		{
		    Platform.runLater(new Runnable() { @Override public void run()
		    {
			createOTPKeyStage = new Stage();
			createOTPKey = new CreateOTPKey();
			try { createOTPKey.start(createOTPKeyStage); } catch (Exception ex) { System.err.println(ex.getMessage()); }
			createOTPKey.controller.setCurrentDir(keyFileChooser.getCurrentDirectory().toPath().toAbsolutePath(), guifx); // Parse parameters onto global controller references always through controller
		    }});
		}
		else if	( keyDeviceButton.getText().equals("Create Key Device") )
		{
		    processRunningType = CREATE;
		    tab.getSelectionModel().select(1);
                    processStarted();
                    deviceManager = new DeviceManager(guifx); deviceManager.start();
                    deviceManager.createKeyDevice(keyFCPath, (FCPath) targetFCPathList.get(0));
                    processFinished();
		}
		else if ( keyDeviceButton.getText().equals("Clone Key Device") )
		{
		    processRunningType = CLONE;
		    tab.getSelectionModel().select(1);
                    processStarted();
                    deviceManager = new DeviceManager(ui); deviceManager.start();
                    deviceManager.cloneKeyDevice(keyFCPath, (FCPath) targetFCPathList.get(0));
                    processFinished();
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
    public void setStageTitle(String title) { Platform.runLater(new Runnable() { @Override public void run() { guifx.stage.setTitle(title); } });}
    public void statusDirect(String status) { statusLabel.setText(status); }

    @Override public void processStarted()
    {
        Platform.runLater(new Runnable()
        {
            @Override public void run()
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
		
		if ((processRunningType == ENCRYPT)  || (processRunningType == DECRYPT)) { updateClockTimeLine = new Timeline(new KeyFrame( Duration.seconds(1), ae ->updateClocks())); updateClockTimeLine.setCycleCount(Animation.INDEFINITE); updateClockTimeLine.setDelay(Duration.seconds(1)); updateClockTimeLine.play(); }
		
                processRunning = true;
                encryptButton.setDisable(true);
                decryptButton.setDisable(true);
                pauseToggleButton.setDisable(false);
                stopButton.setDisable(false);

                filesProgressBar.setProgress(0.0);
                fileProgressBar.setProgress(0.0);
                targetFileChooser.rescanCurrentDirectory();
                keyFileChooser.rescanCurrentDirectory();
            }
        });
    }

    @Override public void fileProgress()
    {
//	updateDashboard(targetFCPathList);
    }
    
    private void updateClocks()
    {
        Platform.runLater(new Runnable() { @Override public void run()
	{
	    nowTimeCalendar =	    Calendar.getInstance(Locale.ROOT);
	    elapsedTimeCalendar =   Calendar.getInstance(Locale.ROOT);
	    remainingTimeCalendar = Calendar.getInstance(Locale.ROOT);
	    totalTimeCalendar =	    Calendar.getInstance(Locale.ROOT);

	    totalTimeCalendar.setTimeInMillis(bytesTotal / bytesPerMilliSecond);
	    elapsedTimeCalendar.setTimeInMillis(nowTimeCalendar.getTimeInMillis() - startTimeCalendar.getTimeInMillis());
	    remainingTimeCalendar.setTimeInMillis(totalTimeCalendar.getTimeInMillis() - elapsedTimeCalendar.getTimeInMillis());
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
	}});
    }

    @Override public void processProgress(int fileProgressPercent, int filesProgressPercent, long bytesTotalParam, long bytesProcessedParam, long bytesPerMiliSecondParam)
    {
	
        Platform.runLater(new Runnable()
        {
            @Override public void run()
            {
		// updateClocks() data
		bytesTotal = bytesTotalParam;
		bytesProcessed = bytesProcessedParam;
		bytesPerMilliSecond = bytesPerMiliSecondParam;
		
		// update ProgressBars
                if (finalCrypt.getVerbose()) { log("Progress File : " + filesProgressPercent / 100.0  + " factor", false, false, false, false, true); }
                if (finalCrypt.getVerbose()) { log("Progress Files: " + fileProgressPercent / 100.0 + " factor", false, false, false, false, true); }
                fileProgressBar.setProgress((double)fileProgressPercent / 100.0); // percent needs to become factor in this gui
                filesProgressBar.setProgress((double)filesProgressPercent / 100.0); // percent needs to become factor in this gui                
		updateDashboard(targetFCPathList);
            }
        });
    }

    @Override public void processGraph(int value) { Platform.runLater(new Runnable() { @Override public void run() { }}); }
    
    @Override public void processFinished()
    {
        Platform.runLater(new Runnable()
        {
            @Override public void run()
            {
		// Clocks
		if ((processRunningType == ENCRYPT)  || (processRunningType == DECRYPT)) { updateClockTimeLine.stop(); }
		if (clockUpdated)
		{
		    remainingTimeLabel.setText("00:00:00");
		    totalTimeCalendar.setTimeInMillis(elapsedTimeCalendar.getTimeInMillis());
		    String totalTimeString = "";
		    totalTimeString += String.format("%02d", totalTimeCalendar.get(Calendar.HOUR_OF_DAY) - offSetHours) + ":";
		    totalTimeString += String.format("%02d", totalTimeCalendar.get(Calendar.MINUTE) - offSetMinutes) + ":";
		    totalTimeString += String.format("%02d", totalTimeCalendar.get(Calendar.SECOND) - offSetSeconds);
		    totalTimeLabel.setText(totalTimeString);
		}
		
		targetFCPathList = new FCPathList();
//                Path homePath = Paths.get(System.getProperty("user.home")); // Just to reset the selected key
//                             Validate.getFCPath(UI ui, String caller, Path path, boolean isKey, Path keyPath, boolean report)
//                keyFCPath = Validate.getFCPath(   ui,            "",  homePath,            false,        homePath,          false);
                updateDashboard(targetFCPathList);
		encryptButton.setDisable(true);
		decryptButton.setDisable(true);
		pauseToggleButton.setDisable(true);
		stopButton.setDisable(true);
                fileProgressBar.setProgress(0);
                filesProgressBar.setProgress(0);

		encryptionModeToggleButton.setMouseTransparent(!encryptionModeToggleButton.isMouseTransparent());
		encryptionModeAnchorPane.setMouseTransparent(!encryptionModeAnchorPane.isMouseTransparent());

		updateFileChoosers();
		
		
                processRunningType = NONE;
                processRunning = false;

//                targetFileChooser.setSelectedFile(new File(""));
//                File[] files = { new File("") };
//                targetFileChooser.setSelectedFiles(files);
//                targetFileChooser.setCurrentDirectory(targetFileChooser.getCurrentDirectory());
//                targetFileChooser.setFileFilter(targetFileChooser.getAcceptAllFileFilter()); // Prevents users to scare about disappearing files as they might forget the selected filefilter
//                targetFileChooser.rescanCurrentDirectory(); targetFileChooser.validate();
//
//                keyFileChooser.setSelectedFile(new File(""));
//                keyFileChooser.setCurrentDirectory(keyFileChooser.getCurrentDirectory());
//                keyFileChooser.setFileFilter(keyFileChooser.getAcceptAllFileFilter()); // Prevents users to scare about disappearing files as they might forget the selected filefilter
//                keyFileChooser.rescanCurrentDirectory(); keyFileChooser.validate();
//
//                targetFileChooserPropertyCheck(false);
//                keyFileChooserPropertyCheck();
//                
//
//		targetFileChooser.setVisible(false); targetFileChooser.setVisible(true); keyFileChooser.setVisible(false); keyFileChooser.setVisible(true); // Reldraw FileChoosers
            }
        });
    }    
    
//  ================================================= END UPDATE PROGRESS ===========================================================

    public void updateFileChoosers()
    {
        Platform.runLater(new Runnable() { @Override public void run()
        {
	    File[] files = { new File("") };

	    targetFileChooser.setSelectedFile(new File(""));
	    targetFileChooser.setCurrentDirectory(targetFileChooser.getCurrentDirectory());
	    targetFileChooser.setSelectedFiles(files);
	    targetFileChooser.setFileFilter(targetFileChooser.getAcceptAllFileFilter()); // Prevents users to scare about disappearing files as they might forget the selected filefilter
	    targetFileChooser.rescanCurrentDirectory(); targetFileChooser.validate();
	    
//	    targetFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//	    targetFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//	    targetFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

	    keyFileChooser.setSelectedFile(new File(""));
	    keyFileChooser.setCurrentDirectory(keyFileChooser.getCurrentDirectory());
	    keyFileChooser.setSelectedFiles(files);
	    keyFileChooser.setFileFilter(keyFileChooser.getAcceptAllFileFilter()); // Prevents users to scare about disappearing files as they might forget the selected filefilter
	    keyFileChooser.rescanCurrentDirectory(); keyFileChooser.validate();

//	    keyFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//	    keyFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//	    keyFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    
	    targetFileChooser.setVisible(false); targetFileChooser.setVisible(true); keyFileChooser.setVisible(false); keyFileChooser.setVisible(true); // Reldraw FileChoosers

	    targetFileChooserPropertyCheck(false);
	    keyFileChooserPropertyCheck();
        }});
    }
    
    @FXML
    private void keyInfoLabelClicked(MouseEvent event)
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        
//      Style the Alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        
        alert.setTitle("Information Dialog");
        alert.setHeaderText("What is your secret Key file?");
        alert.setResizable(true);
        String infotext = new String();
        infotext = "FinalCrypt de/encrypts your files with a key file.\r\n";
        infotext += "Any personal photo or video can be your key file.\r\n";
        infotext += "Best is to create a One-Time Pad Key File below.\r\n";
        infotext += "\r\n";
        infotext += "Keep keys secret and backed up (on USB sticks)\r\n";
        infotext += "Without keys you can NEVER decrypt your files!\r\n";
        infotext += "\r\n";
        infotext += "==============================\r\n";
        infotext += "\r\n";
        infotext += "Don't keep key file(s) on your computer too long\r\n";
        infotext += "to prevent someone or something copying them.\r\n";
        infotext += "\r\n";
//        infotext += "Encryption / Decryption (advanced explanation):\r\n";
//        infotext += "\r\n";
//        infotext += "Your key bit patterns negate your file bit patterns.\r\n";
//        infotext += "\r\n";
//        infotext += "                  Encrypt                      Decrypt\r\n";
//        infotext += "Data byte: 00000011 = 3    ╭─> 00000110 = 6\r\n";
//        infotext += "Ciph byte: 00000101 = 5    │      00000101 = 5\r\n";
//        infotext += "Encr byte: 00000110 = 6 ─╯       00000011 = 3\r\n\r\n";
//        infotext += " \r\n";
        alert.setContentText(infotext);
        alert.showAndWait();
    }

    @FXML
    private void targetInfoLabelClicked(MouseEvent event) {
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
        infotext += "\r\n";
        alert.setContentText(infotext);
        alert.showAndWait();
    }

    @FXML
    private void pauseToggleButtonAction(ActionEvent event)
    {
//        if ( encryptButton.getText().equals("Encrypt") )
        if ((processRunning) && ((processRunningType == ENCRYPT) || (processRunningType == DECRYPT)))
        {
            finalCrypt.setPausing(pauseToggleButton.isSelected());
        }
        else
        {
            DeviceController.setPausing(pauseToggleButton.isSelected());
        }
    }
    
    @FXML
    private void stopButtonAction(ActionEvent event)
    {
//        if ( encryptButton.getText().equals("Encrypt") )
        if ((processRunning) && ((processRunningType == ENCRYPT) || (processRunningType == DECRYPT)))
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
    private void updateButtonAction(ActionEvent event) { checkUpdate(); }

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
	    Platform.runLater(new Runnable(){ @Override public void run()
	    {
		targetFileChooser.setVisible(false); targetFileChooser.setVisible(true); keyFileChooser.setVisible(false); keyFileChooser.setVisible(true); // Reldraw FileChoosers
	    }});
	}
    }

    @FXML
    private void logTabSelectionChanged(Event event)
    {
    }

//  Headers
//  ==================================================================================================================================================================

    private void emptyFilesHeaderLabelOnMouseClicked(MouseEvent event)
    {
	if ( (emptyList != null) && (emptyList.size() > 0) ) { tab.getSelectionModel().select(1); log("Empty Files:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = emptyList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
    }

    private void symlinkFilesHeaderLabelOnMouseClicked(MouseEvent event)
    {
	if ( (symlinkList != null) && (symlinkList.size() > 0) ) { tab.getSelectionModel().select(1); log("Symlinks:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = symlinkList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
    }

    @FXML
    private void unreadableFilesHeaderLabelOnMouseClicked(MouseEvent event)
    {
	if ( (unreadableList != null) && (unreadableList.size() > 0) )
	{
	    /*tab.getSelectionModel().select(1);*/ log("Set Read Attributes:\r\n\r\n", false, true, false, false, false);
	    for (Iterator it = unreadableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); setAttribute(fcPath, true, false); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false);
	    targetFCPathList = new FCPathList(); updateDashboard(targetFCPathList);
	    Platform.runLater(new Runnable(){ @Override public void run() { encryptButton.setDisable(true); decryptButton.setDisable(true); keyDeviceButton.setDisable(false); keyDeviceButton.setText("Create OTP Key File"); }});
	    targetFileChooser.setFileFilter(this.nonFinalCryptFilter); targetFileChooser.setFileFilter(targetFileChooser.getAcceptAllFileFilter()); // Resets rename due to doucle click file
	}
    }

    @FXML
    private void unwritableFilesHeaderLabelOnMouseClicked(MouseEvent event)
    {
	if ( (unwritableList != null) && (unwritableList.size() > 0) )
	{
	    /*tab.getSelectionModel().select(1);*/ log("Set Write Attributes:\r\n\r\n", false, true, false, false, false);
	    for (Iterator it = unwritableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); setAttribute(fcPath, true, true); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false);
	    targetFCPathList = new FCPathList(); updateDashboard(targetFCPathList);
	    Platform.runLater(new Runnable(){ @Override public void run() { encryptButton.setDisable(true); decryptButton.setDisable(true); keyDeviceButton.setDisable(false); keyDeviceButton.setText("Create OTP Key File"); }});
	    targetFileChooser.setFileFilter(this.nonFinalCryptFilter); targetFileChooser.setFileFilter(targetFileChooser.getAcceptAllFileFilter()); // Resets rename due to doucle click file
	}
    }

    private void hiddenFilesHeaderLabelOnMouseClicked(MouseEvent event)
    {
	if ( (hiddenList != null) && (hiddenList.size() > 0) ) { tab.getSelectionModel().select(1); log("\r\nHidden Files:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = hiddenList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
    }

    @FXML
    private void emptyFilesLabelOnMouseClicked(MouseEvent event)
    {
	if ( (emptyList != null) && (emptyList.size() > 0) ) { tab.getSelectionModel().select(1); log("\r\nEmpty Files:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = emptyList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
    }

    @FXML
    private void symlinkFilesLabelOnMouseClicked(MouseEvent event)
    {
	if ( (symlinkList != null) && (symlinkList.size() > 0) ) { tab.getSelectionModel().select(1); log("\r\nSymlinks:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = symlinkList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
    }

    @FXML
    private void unreadableFilesLabelOnMouseClicked(MouseEvent event)
    {
	if ( (unreadableList != null) && (unreadableList.size() > 0) ) { tab.getSelectionModel().select(1); log("\r\nUnreadable Files:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = unreadableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
    }

    @FXML
    private void unwritableFilesLabelOnMouseClicked(MouseEvent event)
    {
	if ( (unwritableList != null) && (unwritableList.size() > 0) ) { tab.getSelectionModel().select(1); log("Unwritable Files:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = unwritableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
    }

    @FXML
    private void hiddenFilesLabelOnMouseClicked(MouseEvent event)
    {
	if ( (hiddenList != null) && (hiddenList.size() > 0) ) { tab.getSelectionModel().select(1); log("Hidden Files:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = hiddenList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
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
	if ( (encryptableList != null) && (encryptableList.size() > 0) ) { tab.getSelectionModel().select(1); log("\r\nEncryptable Files:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = encryptableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
    }

    @FXML
    private void decryptableLabelOnMouseClicked(MouseEvent event)
    {
	if ( (decryptableList != null) && (decryptableList.size() > 0) ) { tab.getSelectionModel().select(1); log("\r\nDecryptable Files:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = decryptableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
    }

    @FXML
    private void decryptedLabelOnMouseClicked(MouseEvent event)
    {
	if ( (decryptedList != null) && (decryptedList.size() > 0) ) { tab.getSelectionModel().select(1); log("\r\nDecrypted Files:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = decryptedList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
    }

    @FXML
    private void encryptedLabelOnMouseClicked(MouseEvent event)
    {
	if ( (encryptedList != null) && (encryptedList.size() > 0) ) { tab.getSelectionModel().select(1); log("\r\nEncrypted Files:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = encryptedList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
    }

    @FXML
    private void newEncryptedLabelOnMouseClicked(MouseEvent event)
    {
	if ( (newEncryptedList != null) && (newEncryptedList.size() > 0) ) { tab.getSelectionModel().select(1); log("\r\nNew Encrypted Files:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = newEncryptedList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
    }


    @FXML
    private void unencryptableLabelOnMouseClicked(MouseEvent event)
    {
	if ( (unencryptableList != null) && (unencryptableList.size() > 0) ) { tab.getSelectionModel().select(1); log("\r\nUnencryptable Files:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = unencryptableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
    }

    @FXML
    private void newDecryptedLabelOnMouseClicked(MouseEvent event)
    {
	if ( (newDecryptedList != null) && (newDecryptedList.size() > 0) ) { tab.getSelectionModel().select(1); log("\r\nNew Decrypted Files:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = newDecryptedList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
    }


    @FXML
    private void undecryptableLabelOnMouseClicked(MouseEvent event)
    {
	if ( (undecryptableList != null) && (undecryptableList.size() > 0) ) { tab.getSelectionModel().select(1); log("\r\nUndecryptable Files:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = undecryptableList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
    }

    @FXML
    private void invalidFilesLabelOnMouseClicked(MouseEvent event)
    {
	if ( (invalidFilesList != null) && (invalidFilesList.size() > 0) ) { tab.getSelectionModel().select(1); log("\r\nInvalid Files:\r\n\r\n", false, true, false, false, false);
	for (Iterator it = invalidFilesList.iterator(); it.hasNext();) { FCPath fcPath = (FCPath) it.next(); log(fcPath.path.toString() + "\r\n", false, true, false, false, false); } log("\r\n", false, true, false, false, false); }
    }

    @FXML
    private void openWebsiteAction(ActionEvent event)
    {
	Thread updateThread;
	updateThread = new Thread(() ->
	{
	    try { try {  Desktop.getDesktop().browse(new URI(Version.WEBSITEURISTRING)); }
	    catch (URISyntaxException ex) { log(ex.getMessage(), true, true, true, true, false); }}
	    catch (IOException ex) { log(ex.getMessage(), true, true, true, true, false); }
	});
	updateThread.setName("updateThread");
	updateThread.setDaemon(true);
	updateThread.start();
    }    




//  ==============================================================================================================
//  Begin Message Authentication Mode
//  ==============================================================================================================



    private void enableMACMode()
    {
		    if ( flashMACTimeline != null ) { flashMACTimeline.stop(); }
		    encryptionModeToggleButton.setText("Enabled\r\nMAC Mode");
		    encryptionModeToggleButton.setTextFill(Paint.valueOf("black"));

		    updateFileChoosers();
		    finalCrypt.disableMAC = false;
		    dashboardGridPane.setDisable(false);
		    encryptionModeToggleButton.setDisable(true);
		    encryptionModeToggleButton.setMouseTransparent(true);
		    long now = Calendar.getInstance().getTimeInMillis(); lastRawModeClicked = now; // Anti DoubleClick missery
		    log("Message Authentication Mode Enabled\r\n", true, true, true, false, false);
    }
    
    private void armDisableMACMode()
    {
	encryptionModeToggleButton.setDisable(false);
	encryptionModeToggleButton.setSelected(false);
	encryptionModeToggleButton.setWrapText(false);
	encryptionModeToggleButton.setText("Disable\r\n MAC Mode?");
	encryptionModeToggleButton.setTextFill(Paint.valueOf("grey"));
	encryptionModeToggleButton.setMouseTransparent(false);
	encryptionModeToggleButton.getTooltip().setText("Click to disable MAC Mode! (files will be encrypted without Message Authentication Code Header)");

//	Auto disable arming disable MAC Mode
	autoDisableTimeline = new Timeline();
	autoDisableTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds( 10.0), evt -> { disarmDisableMACMode(); } ));
	autoDisableTimeline.setCycleCount(1);
	autoDisableTimeline.play();
    }
    
    private void disarmDisableMACMode()
    {
	encryptionModeToggleButton.setDisable(true);
	encryptionModeToggleButton.setSelected(false);
//	encryptionModeToggleButton.setText("Disable\r\n MAC Mode?");
	encryptionModeToggleButton.setTextFill(Paint.valueOf("grey"));
	encryptionModeToggleButton.setMouseTransparent(false);
//	encryptionModeToggleButton.getTooltip().setText("Click to disable MAC Mode! (files will be encrypted without Message Authentication Code Header)");

////	Auto disable arming disable MAC Mode
//	autoDisableTimeline = new Timeline();
//	autoDisableTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds( 10.0), evt -> { enableMACMode(); } ));
//	autoDisableTimeline.setCycleCount(1);
//	autoDisableTimeline.play();
    }
    
    private void disableMACMode()
    {
	if ( autoDisableTimeline != null ) { autoDisableTimeline.stop(); }

	encryptionModeToggleButton.setText("Disabled\r\nMAC Mode");
	encryptionModeToggleButton.setTextFill(Paint.valueOf("black"));
	encryptionModeToggleButton.getTooltip().setText("Click to enable Message Authentication Mode");

	updateFileChoosers();
	finalCrypt.disableMAC = true;
	dashboardGridPane.setDisable(true);
	log("Warning: MAC Mode Disabled! (files will be encrypted without Message Authentication Code Header)\r\n", true, true, true, false, false);

	flashMACTimeline = new Timeline();
	flashMACTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds( 0.0), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("WARNING");} ));
	flashMACTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds( 0.25), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("");} ));
	flashMACTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds( 0.50), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("WARNING");} ));
	flashMACTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds( 0.75), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("");} ));
	flashMACTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds( 1.0), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("WARNING");} ));
//	flashMACTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds( 1.0), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("");} ));
	flashMACTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds( 1.25), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("black")); encryptionModeToggleButton.setText("Disabled\r\nMAC Mode");} ));
	flashMACTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds( 2.25), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("black"));   encryptionModeToggleButton.setText("");} ));
	flashMACTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds( 2.50), evt -> { encryptionModeToggleButton.setTextFill(Paint.valueOf("red")); encryptionModeToggleButton.setText("");} ));
	flashMACTimeline.setCycleCount(Animation.INDEFINITE);
	flashMACTimeline.play();
    }
        
    @FXML
    private void encryptionModeToggleButtonOnMouseClicked(MouseEvent event)
    {
	if ( ! processRunning )
	{
	    Platform.runLater(new Runnable() { @Override public void run() {
		if (! encryptionModeToggleButton.isSelected())	{ enableMACMode(); }
		else						{ disableMACMode(); }
	    }});
	}
    }

    @FXML
    private void encryptionModeAnchorPaneOnMouseClicked(MouseEvent event)
    {
	long now = Calendar.getInstance().getTimeInMillis();
	if ( ! processRunning)
	{
	    if ( now - lastRawModeClicked > 1000) // Anti DoubleClick missery
	    {
		Platform.runLater(new Runnable() { @Override public void run()
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
		}});
	    }	
	}
    }



//  ==============================================================================================================
//  End Message Authentication Mode
//  ==============================================================================================================



    @Override
    synchronized public void log(String message, boolean status, boolean log, boolean logfile, boolean errfile, boolean print)
    {
	if (status)	{ status(message); }
	if (log)	{ log(message); }
	if (logfile)	{ logfile(message); }
	if (errfile)	{ errfile(message); }
	if (print)	{ errfile(message); }
    }

    public void status(String message)	    { Platform.runLater(new Runnable() { @Override public void run() { statusLabel.setText(message.replace("\r\n", ""));}}); }
    public void log(String message)	    { Platform.runLater(new Runnable() { @Override public void run() { lineCounter++;  logTextArea.appendText(message); if (lineCounter > 1000) { logTextArea.setText(message); lineCounter = 0; } }}); }
    public void logfile(String message)	    { Platform.runLater(new Runnable() { @Override public void run() { try { Files.write(configuration.getLogFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC); } catch (IOException ex) { log("Files.write(" + configuration.getLogFilePath() + ")..));", true, true, false, false, false); } }}); }
    public void errfile(String message)	    { Platform.runLater(new Runnable() { @Override public void run() { try { Files.write(configuration.getErrFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC); } catch (IOException ex) { log("Files.write(" + configuration.getErrFilePath() + ")..));", true, true, false, false, false); } }}); }
    public void print(String message)	    { System.out.print(message); }
    
    public static void main(String[] args)  { launch(args); }

}
