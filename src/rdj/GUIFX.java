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

import com.sun.javafx.application.PlatformImpl;
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
import java.nio.file.LinkOption;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
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
    private JFileChooser cipherFileChooser;
    @FXML
    private SwingNode cipherFileSwingNode;
    private JButton targetFileDeleteButton;
    private JButton cipherFileDeleteButton;
//    private boolean hasEncryptable;
//    private boolean hasCipherItem;
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
    private Path cipherPath;
    private FCPath cipherFCPath;
//    private ArrayList<Path> targetPathList;
    private boolean symlink = false;
    private final String procCPULoadAttribute = "ProcessCpuLoad";
    @FXML
    private Button decryptButton;
    @FXML
    private Label cipherNameLabel;
    @FXML
    private Label cipherTypeLabel;
    @FXML
    private Label cipherSizeLabel;
    @FXML
    private Label cipherValidLabel;
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
    private FCPathList targetFCPathList;
    private FCPathList encryptableList;
    private FCPathList decryptableList;
    private FCPathList createCipherList;
    private FCPathList cloneCipherList;
    private FCPathList customList;
    @FXML
    private Button cipherDeviceButton;
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

        cipherFileDeleteButton = new javax.swing.JButton();
        cipherFileDeleteButton.setFont(new java.awt.Font("Arimo", 0, 11)); // NOI18N
        cipherFileDeleteButton.setText("Delete"); // X🗑❌❎⛔ (no utf8)
        cipherFileDeleteButton.setEnabled(false);
        cipherFileDeleteButton.setToolTipText("Delete selected item");
        cipherFileDeleteButton.addActionListener(new java.awt.event.ActionListener()
        { public void actionPerformed(java.awt.event.ActionEvent evt) { cipherFileDeleteButtonActionPerformed(evt); } });
        
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

//        cipherFileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        cipherFileChooser = new JFileChooser();
        cipherFileChooser.setControlButtonsAreShown(false);
        cipherFileChooser.setToolTipText("Right mousclick for Refresh");
        cipherFileChooser.setMultiSelectionEnabled(false);
        cipherFileChooser.setFocusable(true);
//        cipherFileChooser.setFocusCycleRoot(true);
//        cipherFileChooser.setFocusTraversalKeysEnabled(true);
//        cipherFileChooser.setFocusTraversalPolicyProvider(true);
        cipherFileChooser.setFont(new Font("Open Sans", Font.PLAIN, 10));
        cipherFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        cipherFileChooser.addPropertyChangeListener((java.beans.PropertyChangeEvent evt) -> { cipherFileChooserPropertyChange(evt); });
        cipherFileChooser.addActionListener( (java.awt.event.ActionEvent evt) -> { cipherFileChooserActionPerformed(evt); });
        
        cipherFileChooserComponentAlteration(cipherFileChooser);
        Timeline timeline = new Timeline(new KeyFrame( Duration.millis(100), ae -> 
        {
            cipherFileSwingNode.setContent(cipherFileChooser);
        }
        )); timeline.play();

        finalCrypt = new FinalCrypt(this); finalCrypt.start();
//        device = new Device(this); device.start();
        
        welcome();
    }
    
    public double getProcessCpuLoad()
    {
        try { attribList = mbs.getAttributes(name, new String[]{ procCPULoadAttribute });}
        catch (InstanceNotFoundException | ReflectionException ex) { status(ex.getMessage(), true); }
        
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
        status("Welcome to " + Version.getProduct() + " " + version.getCurrentlyInstalledOverallVersionString() + "\r\n", false);        
        log("Welcome to " + Version.getProduct() + " " + version.getCurrentlyInstalledOverallVersionString() + "\r\n");
        log("Copyright: " + Version.getCopyright() + " " + Version.getAuthor() + "\r\n");
        log("Email:     " + Version.getAuthorEmail() + "\r\n");
        log("Logfiles:  " + configuration.getLogDirPath().toString() + "\r\n");
        log("\r\n");
        log("Tip: FinalCrypt command line (DOS) usage:\r\n");
        log("java -cp FinalCrypt.jar rdj/CLUI --help\r\n");
        log("\r\n");
        copyrightLabel.setText("Copyright: " + Version.getCopyright() + " " + Version.getAuthor());

//      cpuIndicator
        Rectangle rect = new Rectangle(0, 0, 100, 100); Tooltip cpuIndicatorToolTip = new Tooltip("Process CPU Load"); Tooltip.install(rect, cpuIndicatorToolTip);
        cpuIndicator.setTooltip(cpuIndicatorToolTip);

//      for: ProcessCpuLoad()
//        procCPULoadAttribute = "ProcessCpuLoad";
        mbs = ManagementFactory.getPlatformMBeanServer();
        try {name    = ObjectName.getInstance("java.lang:type=OperatingSystem"); }
        catch (MalformedObjectNameException | NullPointerException ex) { status(ex.getMessage(), true); }
        
        Timeline timeline = new Timeline(new KeyFrame( Duration.millis(200), ae ->
                cpuIndicator.setProgress(getProcessCpuLoad())
        )); timeline.setCycleCount(Animation.INDEFINITE); timeline.play();       
        
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                String title =  "Welcome to " + Version.getProduct();
                String header = "Brief Introduction:";
                String infotext = 
                            "Step 1 Select items to en/decrypt on the left side.\r\n";
                infotext += "Step 2 Select personal cipher file on the right side.\r\n";
                infotext += "Step 3 Click [Encrypt] or [Decrypt] at the bottom left.\r\n";
                infotext += "\r\n";
                infotext += "That's it! Not hard right?\r\n";
                infotext += "\r\n";
                infotext += "Optional:\r\n";
                infotext += "\r\n";
                infotext += "Double click to open files.\r\n";
                infotext += "Click [LOG] to see details.\r\n";
                infotext += "Click [Check Update] sometimes.\r\n";
                infotext += "Tip:  Watch statusbar at bottom.\r\n";
                infotext += "Tip:  Make backups of your data.\r\n";
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
                
                Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
                String val = prefs.get("Hide Intro", "Unknown"); // if no val then "Unknown" prefs location registry: HKEY_CURRENT_USER\Software\JavaSoft\Prefs

                if (! val.equals("Yes"))
                {
                    Alert alert = introAlert(AlertType.INFORMATION, title, header, infotext, "Don't show again", param -> prefs.put("Hide Intro", param ? "Yes" : "No"),  ButtonType.OK);
                    if (alert.showAndWait().filter(t -> t == ButtonType.OK).isPresent()) {    }                                
                }
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
		version.checkLatestOnlineVersion(GUIFX.this);
		String[] lines = version.getUpdateStatus().split("\r\n");
		for (String line: lines) {status(line + "\r\n", true);}
		alertString = "Download new version: " + version.getLatestOnlineOverallVersionString() + "?\r\n";
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
			    catch (URISyntaxException ex) { ui.error(ex.getMessage()); }}
			    catch (IOException ex) { ui.error(ex.getMessage()); }
			});
			updateThread.setName("encryptThread");
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
        PlatformImpl.runAndWait(new Runnable()
        {
            @Override
            public void run() {
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
                            targetFileChooser.rescanCurrentDirectory();  targetFileChooser.validate();
                            cipherFileChooser.rescanCurrentDirectory(); cipherFileChooser.validate();
			    targetFileChooserPropertyCheck(true);
                        }
                    }
                }
            }
        });
    }                                               

    private void cipherFileDeleteButtonActionPerformed(java.awt.event.ActionEvent evt)                                                
    {                                                            
        PlatformImpl.runAndWait(new Runnable()
        {
            @Override
            public void run()
            {
                String selection = "Delete 1 selected item?";
                Alert alert = new Alert(AlertType.CONFIRMATION, selection, ButtonType.YES, ButtonType.NO);alert.setHeaderText("Confirm Deletion?"); alert.showAndWait();
                if (alert.getResult() == ButtonType.YES)
                {
                    if ((cipherFileChooser != null)  && (cipherFileChooser.getSelectedFiles() != null))
                    {
                        ArrayList<Path> pathList = new ArrayList<>();
                        pathList.add(cipherFileChooser.getSelectedFile().toPath());
                        boolean delete = true;
                        boolean returnpathlist = false;
                        String pattern = "glob:*";
                        finalCrypt.deleteSelection(pathList, delete, returnpathlist, pattern, false);
                        targetFileChooser.rescanCurrentDirectory();  targetFileChooser.validate();
                        cipherFileChooser.rescanCurrentDirectory(); cipherFileChooser.validate();
			cipherFileChooserPropertyCheck();
                    }
                }
            }
        });
    }                                               

    private void cursorWait()
    {
            targetFileChooser.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
            cipherFileChooser.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));        
    }
    
    private void cursorDefault()
    {
            targetFileChooser.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
            cipherFileChooser.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));        
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
    
    private void cipherFileChooserActionPerformed(java.awt.event.ActionEvent evt)                                                  
    {                                                      
        this.fileProgressBar.setProgress(0);
        this.filesProgressBar.setProgress(0);
        if ((cipherFileChooser != null)  && (cipherFileChooser.getSelectedFile() != null))
        {
	    if (( cipherFCPath.type == FCPath.DEVICE ) || ( cipherFCPath.type == FCPath.DEVICE_PROTECTED ))
	    {
		tab.getSelectionModel().select(1);
		DeviceManager deviceManager = new DeviceManager(this); deviceManager.start(); deviceManager.printGPT(cipherFCPath);
		targetFCPathList = new FCPathList(); this.updateDashboard(targetFCPathList);
		Platform.runLater(new Runnable(){ @Override public void run() {
		    encryptButton.setDisable(true); decryptButton.setDisable(true);
		    cipherDeviceButton.setDisable(true); cipherDeviceButton.setText("Cipher Device");
		}});
	    }
//												 device  minsize  symlink  writable status
	    else if (Validate.isValidFile(this, "", cipherFileChooser.getSelectedFile().toPath(), false,      0L, symlink,    false,  true))
	    {
		try { Desktop.getDesktop().open(cipherFileChooser.getSelectedFile()); }
		catch (IOException ex) { error("Error: Desktop.getDesktop().open(cipherFileChooser.getSelectedFile()); " + ex.getMessage() + "\r\n"); }
		targetFCPathList = new FCPathList(); this.updateDashboard(targetFCPathList);
		Platform.runLater(new Runnable(){ @Override public void run() {
		    encryptButton.setDisable(true); decryptButton.setDisable(true);
		    cipherDeviceButton.setDisable(true); cipherDeviceButton.setText("Cipher Device");
		}});
	    }
        }
	else { encryptButton.setDisable(true); decryptButton.setDisable(true); }
	
        cipherFileChooser.setFileFilter(this.nonFinalCryptFilter);
        cipherFileChooser.setFileFilter(cipherFileChooser.getAcceptAllFileFilter()); // Resets rename due to doucle click file
        cipherFileChooser.removeChoosableFileFilter(this.nonFinalCryptFilter);
    }

//  Doubleclick open file
    private void targetFileChooserActionPerformed(java.awt.event.ActionEvent evt)                                                 
    {
        this.fileProgressBar.setProgress(0);
        this.filesProgressBar.setProgress(0);
        if ((targetFileChooser != null)  && (targetFileChooser.getSelectedFiles() != null) && ( targetFileChooser.getSelectedFiles().length == 1 ))
        {
	    Path targetPath = targetFileChooser.getSelectedFile().toPath();
//					   getFCPath(UI ui, String caller,  Path path, boolean isCipher, Path cipherPath, boolean report)
	    FCPath targetFCPath = Validate.getFCPath(   ui,		 "", targetPath,	    false,	cipherPath,	     true);
	    
	    if ((targetFCPath.type == FCPath.DEVICE) || (targetFCPath.type == FCPath.DEVICE_PROTECTED))
	    {
		tab.getSelectionModel().select(1);
		DeviceManager deviceManager = new DeviceManager(this); deviceManager.start(); deviceManager.printGPT(targetFCPath);
		targetFCPathList = new FCPathList(); updateDashboard(targetFCPathList);
		Platform.runLater(new Runnable(){ @Override public void run() {
		    encryptButton.setDisable(true); decryptButton.setDisable(true);
		    cipherDeviceButton.setDisable(true); cipherDeviceButton.setText("Cipher Device");
		}});
	    }
	    else
	    {
//												device  minsize	 symlink  writable  status
		if (Validate.isValidFile(this, "", targetPath, false,      0L, symlink,    false, true))
		{
		    try { Desktop.getDesktop().open(targetFCPath.path.toFile()); }
		    catch (IOException ex) { error("Error: Desktop.getDesktop().open(file); " + ex.getMessage() + "\r\n"); }
		    targetFCPathList = new FCPathList(); updateDashboard(targetFCPathList);
		    Platform.runLater(new Runnable(){ @Override public void run() {
			encryptButton.setDisable(true); decryptButton.setDisable(true);
			cipherDeviceButton.setDisable(true); cipherDeviceButton.setText("Cipher Device");
		    }});
		}
	    }
        } else { encryptButton.setDisable(true); decryptButton.setDisable(true); }
        targetFileChooser.setFileFilter(this.nonFinalCryptFilter); targetFileChooser.setFileFilter(targetFileChooser.getAcceptAllFileFilter()); // Resets rename due to doucle click file
    }                                                

/////////////////////////////////////////////////////////////////////////////////////////////
    
    private void cipherFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)                                                 
    {
	if (!processRunning)
	{
            cipherFileChooserPropertyCheck();
	    targetFileChooserPropertyCheck(true);
	}
    }
    
    private void cipherFileChooserPropertyCheck() // getFCPath, checkModeReady
    {
        this.fileProgressBar.setProgress(0);
        this.filesProgressBar.setProgress(0);
	
        // En/Disable FileChooser deletebutton
        if (
                (cipherFileChooser != null) &&
                (cipherFileChooser.getSelectedFile() != null) &&
                (
                    (Files.isRegularFile( cipherFileChooser.getSelectedFile().toPath(), LinkOption.NOFOLLOW_LINKS)) ||
                    (Files.isDirectory(cipherFileChooser.getSelectedFile().toPath()))
                ) 
           )
        { cipherFileDeleteButton.setEnabled(true);} else {cipherFileDeleteButton.setEnabled(false); }
        
        // Set Buffer Size
        finalCrypt.setBufferSize(finalCrypt.getBufferSizeDefault());
        
        if ((cipherFileChooser != null) && (cipherFileChooser.getSelectedFile() != null))
	{
            cursorWait();
	    Path cipherPath = cipherFileChooser.getSelectedFile().toPath();
//					   getFCPath(UI ui, String caller,  Path path, boolean isCipher, Path cipherPath, boolean report)
	    cipherFCPath = Validate.getFCPath(   ui,		"",        cipherPath,             true,      cipherPath,           true);
	    Platform.runLater(new Runnable(){ @Override public void run() 
	    {
		if ((cipherFCPath.isCipher) && (cipherFCPath.isValidCipher))
		{
		    cipherNameLabel.setTextFill(Color.GREENYELLOW); cipherNameLabel.setText(cipherFCPath.path.toString());
		    cipherTypeLabel.setTextFill(Color.GREENYELLOW); cipherTypeLabel.setText(FCPath.getTypeString(cipherFCPath.type));
		    cipherSizeLabel.setTextFill(Color.GREENYELLOW); cipherSizeLabel.setText(Validate.getHumanSize(cipherFCPath.size,1));
		    cipherValidLabel.setTextFill(Color.GREENYELLOW); cipherValidLabel.setText(Boolean.toString(cipherFCPath.isValidCipher));
		}
		else
		{
		    targetFCPathList = new FCPathList(); updateDashboard(targetFCPathList);
		    cipherNameLabel.setTextFill(Color.ORANGE); cipherNameLabel.setText(cipherFCPath.path.toString());
		    if (cipherFCPath.type != FCPath.FILE) { cipherTypeLabel.setTextFill(Color.ORANGERED); } else { cipherTypeLabel.setTextFill(Color.ORANGE); } cipherTypeLabel.setText(FCPath.getTypeString(cipherFCPath.type));
		    if ( cipherFCPath.size < FCPath.CIPHER_SIZE_MIN ) { cipherSizeLabel.setTextFill(Color.ORANGERED); } else { cipherSizeLabel.setTextFill(Color.ORANGE); } cipherSizeLabel.setText(Validate.getHumanSize(cipherFCPath.size,1));
		    cipherValidLabel.setTextFill(Color.ORANGE); cipherValidLabel.setText(Boolean.toString(cipherFCPath.isValidCipher));
		}
	    }});
	    
	    cursorDefault();
	}
        checkModeReady();
    }

//  FileChooser Listener methods
    private void targetFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)                                                
    {
	if ((!processRunning ) && (evt.getPropertyName().equals("SelectedFilesChangedProperty")))
	{
	    targetFileChooserPropertyCheck(true);
	}
    }
    
    private void targetFileChooserPropertyCheck(boolean status)
    {
        this.fileProgressBar.setProgress(0);
        this.filesProgressBar.setProgress(0);
//        State.targetSelected = FCPath.INVALID;
//        State.targetReady = false;
        
        ArrayList<Path> targetPathList = new ArrayList<>(); targetPathList.clear();
	targetFileDeleteButton.setEnabled(false);
	
        if ((targetFileChooser != null) && (targetFileChooser.getSelectedFiles() != null) && (targetFileChooser.getSelectedFiles().length > 0) && (cipherFCPath != null) && (cipherFCPath.isCipher) && (cipherFCPath.isValidCipher))
	{
		Validate.bytesCount = 0;
		for (File file:targetFileChooser.getSelectedFiles()) { targetPathList.add(file.toPath()); }

//    	    En/Disable FileChooser deletebutton
		targetFileDeleteButton.setEnabled(true);

//    	    Get Globbing Pattern String
		String pattern = "glob:*"; try { pattern = getSelectedPatternFromFileChooser( targetFileChooser.getFileFilter()); } catch (ClassCastException exc) {  }

		// UPdate Dashboard during buildSelection
		Platform.runLater(new Runnable(){ @Override public void run() { filesProgressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS); }});
		Timeline updateDashboardTimeline = new Timeline(new KeyFrame( Duration.millis(100), ae -> { updateDashboard(targetFCPathList); } )); updateDashboardTimeline.play();
		
		cursorWait();
		targetFCPathList = new FCPathList(); Validate.buildSelection( this, targetPathList, cipherFCPath, targetFCPathList, symlink, pattern, negatePattern, false);
		cursorDefault();

		updateDashboardTimeline.stop();
		Platform.runLater(new Runnable(){ @Override public void run() { filesProgressBar.setProgress(0); }});
		updateDashboard(targetFCPathList);
	}
        checkModeReady();
    }

    private void updateDashboard(FCPathList targetFCPathList)
    {
	Platform.runLater(new Runnable(){ @Override public void run() 
	{
	    // Skipping / Info Column
	    if ( (targetFCPathList.emptyFiles > 0) || (targetFCPathList.symlinkFiles > 0)  || (targetFCPathList.unreadableFiles > 0) || (targetFCPathList.unwritableFiles > 0))
	    { targetWarningLabel.setTextFill(Color.ORANGE); targetWarningLabel.setText("Skipping"); } else { targetWarningLabel.setTextFill(Color.GRAY); targetWarningLabel.setText("Info"); }
	    
	    if ( targetFCPathList.emptyFiles > 0 )  { emptyFilesLabel.setTextFill(Color.ORANGE); } else { emptyFilesLabel.setTextFill(Color.GRAY); }
	    if ( targetFCPathList.symlinkFiles > 0 )  { symlinkFilesLabel.setTextFill(Color.ORANGE); } else { symlinkFilesLabel.setTextFill(Color.GRAY); }
	    if ( targetFCPathList.unreadableFiles > 0 )  { unreadableFilesLabel.setTextFill(Color.ORANGE); } else { unreadableFilesLabel.setTextFill(Color.GRAY); }
	    if ( targetFCPathList.unreadableFilesSize > 0 )  { unreadableFilesSizeLabel.setTextFill(Color.ORANGE); } else { unreadableFilesSizeLabel.setTextFill(Color.GRAY); }
	    if ( targetFCPathList.unwritableFiles > 0 )  { unwritableFilesLabel.setTextFill(Color.ORANGE); } else { unwritableFilesLabel.setTextFill(Color.GRAY); }
	    if ( targetFCPathList.unwritableFilesSize > 0 )  { unwritableFilesSizeLabel.setTextFill(Color.ORANGE); } else { unwritableFilesSizeLabel.setTextFill(Color.GRAY); }
	    if ( targetFCPathList.hiddenFiles > 0 )  { hiddenFilesLabel.setTextFill(Color.YELLOW); } else { hiddenFilesLabel.setTextFill(Color.GRAY); }
	    if ( targetFCPathList.hiddenFilesSize > 0 )  { hiddenFilesSizeLabel.setTextFill(Color.YELLOW); } else { hiddenFilesSizeLabel.setTextFill(Color.GRAY); }

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

	    invalidFilesLabel.setText(Long.toString(targetFCPathList.files- targetFCPathList.validFiles));
	    invalidFilesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.filesSize - targetFCPathList.validFilesSize,1));

	    totalFilesLabel.setText(Long.toString(targetFCPathList.files));
	    filesSizeLabel.setText(Validate.getHumanSize(targetFCPathList.filesSize,1));
	}});
    }
    
    private void checkModeReady()
    {
	Platform.runLater(new Runnable(){ @Override public void run() 
	{
	    if (!processRunning)
	    {
		encryptButton.setDisable(true);
		decryptButton.setDisable(true);
		pauseToggleButton.setDisable(true);
		stopButton.setDisable(true);
		
		if ((cipherFCPath != null) && (cipherFCPath.isValidCipher))
		{
		    // Encryptables
		    if (targetFCPathList.encryptableFiles > 0)
		    {
			encryptableList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.isEncryptable); // log("Encryptable List:\r\n" + encryptableList.getStats());
			encryptButton.setDisable(false); pauseToggleButton.setDisable(true); stopButton.setDisable(true);
		    } else { encryptButton.setDisable(true); }

		    // Encryptables
		    if (targetFCPathList.decryptableFiles > 0)
		    {
			decryptableList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.isDecryptable); // log("Decryptable List:\r\n" + decryptableList.getStats());
			decryptButton.setDisable(false); pauseToggleButton.setDisable(true); stopButton.setDisable(true);
		    } else { decryptButton.setDisable(true); }

		    // Create Cipher Device
		    if (cipherFCPath.type == FCPath.FILE)
		    {
			if (targetFCPathList.validDevices > 0)
			{
			    createCipherList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE); // log("Create Cipher List:\r\n" + createCipherList.getStats());
			    cipherDeviceButton.setDisable(false); cipherDeviceButton.setText("Create Cipher Device"); pauseToggleButton.setDisable(true); stopButton.setDisable(true);
			} else { cipherDeviceButton.setDisable(true); cipherDeviceButton.setText("Cipher Device"); }
		    }		
		    else if (cipherFCPath.type == FCPath.DEVICE)
		    {
			// Clone Cipher Device
			if ((targetFCPathList.validDevices > 0) && (targetFCPathList.matchingCipher == 0))
			{
			    cloneCipherList = filter(targetFCPathList,(FCPath fcPath) -> fcPath.type == FCPath.DEVICE && fcPath.path.compareTo(cipherFCPath.path) != 0); // log("Clone Cipher List:\r\n" + cloneCipherList.getStats());
			    cipherDeviceButton.setDisable(false); cipherDeviceButton.setText("Clone Cipher Device"); pauseToggleButton.setDisable(true); stopButton.setDisable(true);
			} else { cipherDeviceButton.setDisable(true); cipherDeviceButton.setText("Cipher Device"); }
		    } else { cipherDeviceButton.setDisable(true); cipherDeviceButton.setText("Cipher Device"); }
		} else { encryptButton.setDisable(true); decryptButton.setDisable(true); cipherDeviceButton.setDisable(true); cipherDeviceButton.setText("Cipher Device"); }
	    }
	}});
    }

    synchronized public static FCPathList filter(ArrayList<FCPath> fcPathList, Predicate<FCPath> fcPath)
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
        
    public static void main(String[] args)
    {
        launch(args);
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

    public boolean cipherFileChooserComponentAlteration(Container container)
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
                    TimerTask cipherFileChoosershowDetailsTask = new TimerTask() { @Override public void run()
                    {
                        ((JToggleButton)component).doClick();
                    }};
                    Timer cipherFileChoosershowDetailsTaskTimer = new java.util.Timer(); cipherFileChoosershowDetailsTaskTimer.schedule(cipherFileChoosershowDetailsTask, 1500L);
                }
            }
            
            // Add Delete button
            if (component instanceof JButton)
            {
                if (((JButton) component).getActionCommand().equalsIgnoreCase("New Folder"))
                {
//                    component.getParent().add(this.targetFileDeleteButton);
//                    if (targetFileChooserContainer) { component.getParent().add(this.targetFileDeleteButton); } else { component.getParent().add(this.cipherFileDeleteButton); }
                    component.getParent().add(this.cipherFileDeleteButton);
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
                if( cipherFileChooserComponentAlteration((Container) component) ) return true;
            }
        }
        return false;
    }

    @FXML
    private void encryptButtonAction(ActionEvent event)
    {
        // Needs Threading to early split off from the UI Event Dispatch Thread
        final GUIFX guifx = this;
        final UI ui = this;
        Thread encryptThread = new Thread(new Runnable()
        {
            private DeviceManager deviceManager;
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
		processRunningType = ENCRYPT;
		filesProgressBar.setProgress(0.0);
		fileProgressBar.setProgress(0.0);
		String pattern = "glob:*"; try { pattern = getSelectedPatternFromFileChooser( targetFileChooser.getFileFilter()); } catch (ClassCastException exc) { ui.error("Error: GUIFX: ClassCastException: " + exc.getMessage() + "\r\n"); }
		processStarted();
		finalCrypt.encryptSelection(targetFCPathList, encryptableList, cipherFCPath);
            }
        });
        encryptThread.setName("encryptThread");
        encryptThread.setDaemon(true);
        encryptThread.start();
    }

    @FXML
    private void decryptButtonAction(ActionEvent event)
    {
        // Needs Threading to early split off from the UI Event Dispatch Thread
        final GUIFX guifx = this;
        final UI ui = this;
        Thread encryptThread = new Thread(new Runnable()
        {
            private DeviceManager deviceManager;
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
		processRunningType = DECRYPT;
		filesProgressBar.setProgress(0.0);
		fileProgressBar.setProgress(0.0);
		String pattern = "glob:*"; try { pattern = getSelectedPatternFromFileChooser( targetFileChooser.getFileFilter()); } catch (ClassCastException exc) { ui.error("Error: GUIFX: ClassCastException: " + exc.getMessage() + "\r\n"); }
		processStarted();
		finalCrypt.encryptSelection(targetFCPathList, decryptableList, cipherFCPath);
            }
        });
        encryptThread.setName("decryptThread");
        encryptThread.setDaemon(true);
        encryptThread.start();
    }

    @FXML
    private void cipherDeviceButtonAction(ActionEvent event)
    {
        // Needs Threading to early split off from the UI Event Dispatch Thread
        final GUIFX guifx = this;
        final UI ui = this;
        Thread encryptThread = new Thread(new Runnable()
        {
            private DeviceManager deviceManager;
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
		if	( cipherDeviceButton.getText().equals("Create Cipher Device") )
		{
		    processRunningType = CREATE;
		    tab.getSelectionModel().select(1);
                    processStarted();
                    deviceManager = new DeviceManager(guifx); deviceManager.start();
                    deviceManager.createCipherDevice(cipherFCPath, (FCPath) targetFCPathList.get(0));
                    processFinished();
		}
		else if ( cipherDeviceButton.getText().equals("Clone Cipher Device") )
		{
		    processRunningType = CLONE;
		    tab.getSelectionModel().select(1);
                    processStarted();
                    deviceManager = new DeviceManager(ui); deviceManager.start();
                    deviceManager.cloneCipherDevice(cipherFCPath, (FCPath) targetFCPathList.get(0));
                    processFinished();
		}
            }
        });
        encryptThread.setName("cipherDeviceThread");
        encryptThread.setDaemon(true);
        encryptThread.start();
    }
    synchronized public void logNow(String message)    { lineCounter++;                            logTextArea.appendText(message); if (lineCounter > 1000) { logTextArea.setText(message); lineCounter = 0; } }

    @Override synchronized public void status(String status, boolean log)
    {
        Platform.runLater(new Runnable() { @Override public void run()
        {
            statusLabel.setText(status.replace("\r\n", ""));
            if (log) { log(status); }
        }});
    }

    @Override synchronized public void log(String message)
    {
        Platform.runLater(new Runnable() { @Override public void run()
        {
            lineCounter++;  logTextArea.appendText(message); if (lineCounter > 1000) { logTextArea.setText(message); lineCounter = 0; }

            Thread logThread = new Thread(new Runnable()
            {
//                private DeviceManager rawCipher;
                @Override
                @SuppressWarnings({"static-access"})
                public void run()
                {
                    try { Files.write(configuration.getLogFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC); } catch (IOException ex) { println("Files.write(" + configuration.getLogFilePath() + ")..));"); }

//                    try (final SeekableByteChannel writeOutputFileChannel = Files.newByteChannel(configuration.getLogFilePath(), EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC)))
//                    {
//                        // Encrypt targetBuffer and fill up outputBuffer
//                        ByteBuffer outputFileBuffer =  ByteBuffer.allocate(message.getBytes().length); outputFileBuffer.clear();
//                        outputFileBuffer.put(message.getBytes()); outputFileBuffer.flip();
//                        writeOutputFileChannel.write(outputFileBuffer);
//                        writeOutputFileChannel.close();
//                    } catch (IOException ex) { ui.error("\r\nFiles.newByteChannel(configuration.getLogFilePath(): " + ex.getMessage() + "\r\n"); }
                }
            });
            logThread.setName("logThread");
            logThread.setDaemon(true);
            logThread.start();
        }});
    }
    @Override synchronized public void error(String message)
    {
        Platform.runLater(new Runnable() { @Override public void run()
        {
            status(message, true);
            try { Files.write(configuration.getErrorFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC); } catch (IOException ex) { println("Files.write(" + configuration.getLogFilePath() + ")..));"); }

            Thread errorLogThread = new Thread(new Runnable()
            {
//                private DeviceManager rawCipher;
                @Override
                @SuppressWarnings({"static-access"})
                public void run()
                {
                    try { Files.write(configuration.getErrorFilePath(), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC); } catch (IOException ex) { println("Files.write(" + configuration.getErrorFilePath() + ")..));"); }

//                    try (final SeekableByteChannel writeOutputFileChannel = Files.newByteChannel(configuration.getErrorFilePath(), EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.SYNC)))
//                    {
//                        // Encrypt targetBuffer and fill up outputBuffer
//                        ByteBuffer outputFileBuffer =  ByteBuffer.allocate(message.getBytes().length); outputFileBuffer.clear();
//                        outputFileBuffer.put(message.getBytes()); outputFileBuffer.flip();
//                        writeOutputFileChannel.write(outputFileBuffer);
//                        writeOutputFileChannel.close();
//                    } catch (IOException ex) { ui.error("\r\nFiles.newByteChannel(configuration.getErrorFilePath(): " + ex.getMessage() + "\r\n"); }
                }
            });
            errorLogThread.setName("errorThread");
            errorLogThread.setDaemon(true);
            errorLogThread.start();

        }});
    }

    @Override public void statusNow(String status, boolean log)
    {
        Platform.runLater(new Runnable() { @Override public void run()
        {
	    statusLabel.setText(status);
        }});
	if (log) { log(status); } 
    }

    public void setStageTitle(String title) { PlatformImpl.runAndWait(new Runnable() { @Override public void run() { guifx.stage.setTitle(title); } });}
    public void statusDirect(String status) { statusLabel.setText(status); }

    @Override public void println(String message) { Platform.runLater(new Runnable() { @Override public void run() { System.out.println(message); } });}
    
//  ================================================= BEGIN UPDATE PROGRESS ===========================================================

    @Override public void processStarted()
    {
        Platform.runLater(new Runnable()
        {
            @Override public void run()
            {
		// Clocks
		elapsedTimeLabel.setText("00:00:00");
		remainingTimeLabel.setText("00:00:00");
		totalTimeLabel.setText("00:00:00");

		startTimeCalendar = Calendar.getInstance(Locale.ROOT);
		start2TimeCalendar = Calendar.getInstance(Locale.ROOT);
		offsetTimeCalendar = Calendar.getInstance(Locale.ROOT);
		offsetTimeCalendar.setTimeInMillis(start2TimeCalendar.getTimeInMillis() - startTimeCalendar.getTimeInMillis());
		offSetHours = offsetTimeCalendar.get(Calendar.HOUR); offSetMinutes = offsetTimeCalendar.get(Calendar.MINUTE); offSetSeconds = offsetTimeCalendar.get(Calendar.SECOND);
		
		nowTimeCalendar =	Calendar.getInstance(Locale.ROOT);
		elapsedTimeCalendar =   Calendar.getInstance(Locale.ROOT);
		remainingTimeCalendar = Calendar.getInstance(Locale.ROOT);
		totalTimeCalendar =	Calendar.getInstance(Locale.ROOT);
		
		updateClockTimeLine = new Timeline(new KeyFrame( Duration.seconds(1), ae ->updateClocks())); updateClockTimeLine.setCycleCount(Animation.INDEFINITE); updateClockTimeLine.setDelay(Duration.seconds(1)); updateClockTimeLine.play();       
		
                processRunning = true;
                encryptButton.setDisable(true);
                decryptButton.setDisable(true);
                pauseToggleButton.setDisable(false);
                stopButton.setDisable(false);

                filesProgressBar.setProgress(0.0);
                fileProgressBar.setProgress(0.0);
                targetFileChooser.rescanCurrentDirectory();
                cipherFileChooser.rescanCurrentDirectory();
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
	    elapsedTimeString += String.format("%02d", elapsedTimeCalendar.get(Calendar.HOUR-offSetHours)) + ":";
	    elapsedTimeString += String.format("%02d", elapsedTimeCalendar.get(Calendar.MINUTE-offSetMinutes)) + ":";;
	    elapsedTimeString += String.format("%02d", elapsedTimeCalendar.get(Calendar.SECOND-offSetSeconds));
		    
	    String remainingTimeString = "";
	    remainingTimeString += String.format("%02d", remainingTimeCalendar.get(Calendar.HOUR-offSetHours)) + ":";
	    remainingTimeString += String.format("%02d", remainingTimeCalendar.get(Calendar.MINUTE-offSetMinutes)) + ":";;
	    remainingTimeString += String.format("%02d", remainingTimeCalendar.get(Calendar.SECOND));
		    
	    String totalTimeString = "";
	    totalTimeString += String.format("%02d", totalTimeCalendar.get(Calendar.HOUR-offSetHours)) + ":";
	    totalTimeString += String.format("%02d", totalTimeCalendar.get(Calendar.MINUTE-offSetMinutes)) + ":";;
	    totalTimeString += String.format("%02d", totalTimeCalendar.get(Calendar.SECOND-offSetSeconds));
		    
	    elapsedTimeLabel.setText(elapsedTimeString);
	    remainingTimeLabel.setText(remainingTimeString);
	    totalTimeLabel.setText(totalTimeString);	    
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
                if (finalCrypt.getVerbose()) { println("Progress File : " + filesProgressPercent / 100.0  + " factor"); }
                if (finalCrypt.getVerbose()) { println("Progress Files: " + fileProgressPercent / 100.0 + " factor"); }
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
		updateClockTimeLine.stop();
		if (clockUpdated)
		{
		    remainingTimeLabel.setText("00:00:00");
		    totalTimeCalendar.setTimeInMillis(elapsedTimeCalendar.getTimeInMillis());
		    String totalTimeString = "";
		    totalTimeString += String.format("%02d", totalTimeCalendar.get(Calendar.HOUR-offSetHours)) + ":";
		    totalTimeString += String.format("%02d", totalTimeCalendar.get(Calendar.MINUTE-offSetMinutes)) + ":";
		    totalTimeString += String.format("%02d", totalTimeCalendar.get(Calendar.SECOND-offSetSeconds));
		    totalTimeLabel.setText(totalTimeString);
		}
		
		targetFCPathList = new FCPathList(); updateDashboard(targetFCPathList);
                processRunningType = NONE;
                processRunning = false;
		encryptButton.setDisable(true);
		decryptButton.setDisable(true);
		pauseToggleButton.setDisable(true);
		stopButton.setDisable(true);
                fileProgressBar.setProgress(0);
                filesProgressBar.setProgress(0);
                targetFileChooser.setFileFilter(targetFileChooser.getAcceptAllFileFilter()); // Prevents users to scare about disappearing files as they might forget the selected filefilter
                targetFileChooser.rescanCurrentDirectory(); targetFileChooser.validate();
                cipherFileChooser.rescanCurrentDirectory(); cipherFileChooser.validate();
                targetFileChooserPropertyCheck(false);
                cipherFileChooserPropertyCheck();		
            }
        });
    }    
    
//  ================================================= END UPDATE PROGRESS ===========================================================

    @FXML
    private void cipherInfoLabelClicked(MouseEvent event)
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        
//      Style the Alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        
        alert.setTitle("Information Dialog");
        alert.setHeaderText("What is your secret Cipher file?");
        alert.setResizable(true);
        String infotext = new String();
        infotext  = "The cipher file encrypts your selection on the left.\r\n";
        infotext += "Choose a personal cipher file (like a photo or video).\r\n";
        infotext += "\r\n";
        infotext += "Keep backups of your cipher file and keep it SECRET!\r\n";
        infotext += "Without cipher file you can NEVER decrypt your data!\r\n";
        infotext += "\r\n";
        infotext += "==================================\r\n";
        infotext += "\r\n";
        infotext += "Best practice is a cipher file bigger than 100 KiB\r\n";
        infotext += "Keep your cipher safe and away from your computer\r\n";
        infotext += "to prevent someone or something copying it.\r\n";
        infotext += "\r\n";
        infotext += "Encryption / Decryption (advanced explanation):\r\n";
        infotext += "\r\n";
        infotext += "Your cipher bit patterns negate your file bit patterns.\r\n";
        infotext += "\r\n";
        infotext += "                  Encrypt                      Decrypt\r\n";
        infotext += "Data byte: 00000011 = 3    ╭─> 00000110 = 6\r\n";
        infotext += "Ciph byte: 00000101 = 5    │      00000101 = 5\r\n";
        infotext += "Encr byte: 00000110 = 6 ─╯       00000011 = 3\r\n\r\n";
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
        infotext += "Original files are securely deleted after en / decryption.\r\n";
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
		targetFileSwingNode.setContent(targetFileChooser); // Delay setting this JFileChooser avoiding a simultanious cipher and target JFileChooser focus conflict causing focus to endlessly flipflop between the two JFileChoosers
	    }
	    )); timeline.play();
	}
    }

    @FXML
    private void logTabSelectionChanged(Event event)
    {
    }
}
