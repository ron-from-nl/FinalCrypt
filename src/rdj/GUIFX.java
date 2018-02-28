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
import java.nio.channels.SeekableByteChannel;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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
    private Label cipherFileChooserLabel;
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
    private Label cipherFileChooserInfoLabel;
    @FXML
    private ToggleButton pauseToggleButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button updateButton;
    private boolean encryptionRunning;
    @FXML
    private Label copyrightLabel;
//    private TimerTask updateProgressTask;
//    private Timer updateProgressTaskTimer;
    @FXML
    private ProgressIndicator cpuIndicator;
    private MBeanServer mbs;
    private ObjectName name;
    private AttributeList list;
    private Attribute att;
    private Double value;
    private String procCPULoadAttribute;
    @FXML
    private VBox bottomVBox;
    private GridPane logButtonGridPane;
    private FileFilter nonFinalCryptFilter;
    private FileNameExtensionFilter finalCryptFilter;
    private DeviceManager deviceManager;
    private int lineCounter;
    private Configuration configuration;
    @FXML
    private Label targetFileChooserLabel;
    @FXML
    private Label targetFileChooserInfoLabel;
    @FXML
    private SwingNode targetFileSwingNode;
    
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
        stage.setTitle(Version.getProcuct());
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.setMaximized(true);
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.show();
        
        version = new Version(ui);
        version.checkCurrentlyInstalledVersion();
        stage.setTitle(Version.getProcuct() + " " + version.getCurrentlyInstalledOverallVersionString());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        targetFileDeleteButton = new javax.swing.JButton();
        targetFileDeleteButton.setFont(new java.awt.Font("Arimo", 0, 11)); // NOI18N
        targetFileDeleteButton.setText("X");
        targetFileDeleteButton.setEnabled(false);
        targetFileDeleteButton.setToolTipText("Delete selected item(s)");
        targetFileDeleteButton.addActionListener(new java.awt.event.ActionListener()
        { public void actionPerformed(java.awt.event.ActionEvent evt) { targetFileDeleteButtonActionPerformed(evt); } });

        cipherFileDeleteButton = new javax.swing.JButton();
        cipherFileDeleteButton.setFont(new java.awt.Font("Arimo", 0, 11)); // NOI18N
        cipherFileDeleteButton.setText("X");
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
        Timeline timeline = new Timeline(new KeyFrame( Duration.millis(50), ae -> 
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
        try { list = mbs.getAttributes(name, new String[]{ procCPULoadAttribute });}
        catch (InstanceNotFoundException | ReflectionException ex) { status(ex.getMessage(), true); }
        
        if (list.isEmpty()) { return Double.NaN; }
        att = (Attribute)list.get(0);
        value  = (Double)att.getValue();    
        return value;
    }

    private void welcome()
    {
        configuration = new Configuration(ui);
        version = new Version(ui);
        version.checkCurrentlyInstalledVersion();
        status("Welcome to " + Version.getProcuct() + " " + version.getCurrentlyInstalledOverallVersionString() + "\r\n", false);        
        log("Welcome to " + Version.getProcuct() + " " + version.getCurrentlyInstalledOverallVersionString() + "\r\n");
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
        procCPULoadAttribute = "ProcessCpuLoad";
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
                String title =  "Welcome to " + Version.getProcuct();
                String header = "Brief Introduction:";
                String infotext = 
                            "1. Select files to encrypt on left side.\r\n";
                infotext += "2. Select cipher file on the right side.\r\n";
                infotext += "3. Click [Encrypt] to encrypt to: *.bit.\r\n";
                infotext += "4. Click [Encrypt] again to decrypt.\r\n";
                infotext += "\r\n";
                infotext += "Congrats! You now know the basics.\r\n";
                infotext += "\r\n";
                infotext += "Optional:\r\n";
                infotext += "\r\n";
                infotext += "Double click to open files.\r\n";
                infotext += "Click [LOG] to see details.\r\n";
                infotext += "Click [Check Update] maybe.\r\n";
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
        Platform.runLater(() ->
        {
            version = new Version(ui);
            version.checkCurrentlyInstalledVersion();
            version.checkLatestOnlineVersion();
            status(version.getUpdateStatus(), true);
//                    setStageTitle(version.getCurrentlyInstalledOverallVersionString());

            if ( (version.versionIsDifferent()) && (version.versionCanBeUpdated()) )
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Download new version: " + version.getLatestOnlineOverallVersionString() + "?", ButtonType.YES, ButtonType.NO);alert.setHeaderText("Download Update?"); alert.showAndWait();
                if (alert.getResult() == ButtonType.YES)
                {
                    Thread updateThread;
                    updateThread = new Thread(() -> {
                        try { try {  Desktop.getDesktop().browse(new URI(Version.REMOTEPACKAGEDOWNLOADURISTRING)); }
                        catch (URISyntaxException ex) { ui.error(ex.getMessage()); }}
                        catch (IOException ex) { ui.error(ex.getMessage()); }
                    });
                    updateThread.setName("encryptThread");
                    updateThread.setDaemon(true);
                    updateThread.start();
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
    
//  Doubleclick open file
    private void targetFileChooserActionPerformed(java.awt.event.ActionEvent evt)                                                 
    {
        this.fileProgressBar.setProgress(0);
        this.filesProgressBar.setProgress(0);
        if ((targetFileChooser != null)  && (targetFileChooser.getSelectedFiles() != null))
        {
            if ( targetFileChooser.getSelectedFiles().length > 0 ) 
            {
                for (File file:targetFileChooser.getSelectedFiles()) 
                {
                    try { Desktop.getDesktop().open(file); }
                    catch (IOException ex) { error("Error: Desktop.getDesktop().open(file); " + ex.getMessage() + "\r\n"); }
                }
            }
        } else { encryptButton.setDisable(true); }
        targetFileChooser.setFileFilter(this.nonFinalCryptFilter); targetFileChooser.setFileFilter(targetFileChooser.getAcceptAllFileFilter()); // Resets rename due to doucle click file
    }                                                

/////////////////////////////////////////////////////////////////////////////////////////////
    
    private void cipherFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)                                                 
    {
	if (!encryptionRunning)
	{
            cipherFileChooserPropertyCheck();
	    targetFileChooserPropertyCheck(true);
	}
    }
    
    private void cipherFileChooserPropertyCheck()
    {
        this.fileProgressBar.setProgress(0);
        this.filesProgressBar.setProgress(0);
        State.cipherSelected = State.INVALID;
        State.cipherReady = false;

        // Set Buffer Size
        finalCrypt.setBufferSize(finalCrypt.getBufferSizeDefault());
        long cipherSize = finalCrypt.getBufferSizeDefault(); 
        
        // En/Disable FileChooser deletebutton
        if (
                (cipherFileChooser != null) &&
                (cipherFileChooser.getSelectedFile() != null) &&
                (
                    (Files.isRegularFile(cipherFileChooser.getSelectedFile().toPath())) ||
                    (Files.isDirectory(cipherFileChooser.getSelectedFile().toPath()))
                ) 
           )
        { cipherFileDeleteButton.setEnabled(true);} else {cipherFileDeleteButton.setEnabled(false); }
        
        // Set Cipher State
        if ((cipherFileChooser != null) && (cipherFileChooser.getSelectedFile() != null))
        {
            if (
                    (Files.isRegularFile(cipherFileChooser.getSelectedFile().toPath())) &&
                    (cipherFileChooser.getSelectedFile().length() > 0)
               )
            {
                State.cipherSelected = State.FILE;
                State.cipherReady = true;
                try { cipherSize = (int)Files.size(cipherFileChooser.getSelectedFile().toPath()); } catch (IOException ex) { error("Files.size(finalCrypt.getCipherFilePath()) " + ex.getMessage() + "\r\n"); }
            }
            else if (cipherFileChooser.getSelectedFile().getAbsolutePath().startsWith("/dev/sd")) // Linux Cipher Device Selection
            {
		if (
			(!cipherFileChooser.getSelectedFile().getName().endsWith("sda"))
		   )
		{
		    if (isValidFile(cipherFileChooser.getSelectedFile().toPath(), false, false, false))
		    {
			if (Character.isDigit(cipherFileChooser.getSelectedFile().getName().charAt(cipherFileChooser.getSelectedFile().getName().length()-1)))
			{
			    State.cipherSelected = State.PARTITION;
			    State.cipherReady = true;
			}
			else
			{
			    State.cipherSelected = State.DEVICE;
			}

			State.cipherReady = true;
    //                  Get size of partition
			try (final SeekableByteChannel deviceChannel = Files.newByteChannel(cipherFileChooser.getSelectedFile().toPath(), EnumSet.of(StandardOpenOption.READ)))
			{ cipherSize = deviceChannel.size(); deviceChannel.close(); }catch (IOException ex) { ui.status(ex.getMessage(), true); }
		    } else { status("Probably no read permission on " + cipherFileChooser.getSelectedFile().toPath() + " execute: \"sudo usermod -a -G disk " + System.getProperty("user.name") + "\" and re-login your desktop and try again\r\n", true); }
		}
            }
            else if (cipherFileChooser.getSelectedFile().getAbsolutePath().startsWith("/dev/disk")) // Apple Cipher Device Selection
            {
		if (
			( ! cipherFileChooser.getSelectedFile().getName().endsWith("disk0"))
		   )
		{
		    if (isValidFile(cipherFileChooser.getSelectedFile().toPath(), false, false, false))
		    {
			if (
				(Character.isDigit(cipherFileChooser.getSelectedFile().getName().charAt(cipherFileChooser.getSelectedFile().getName().length()-1))) &&
				(String.valueOf(cipherFileChooser.getSelectedFile().getName().charAt(cipherFileChooser.getSelectedFile().getName().length()-2)).equalsIgnoreCase("s"))
			   )
			{
			    State.cipherSelected = State.PARTITION;
			}
			else
			{
			    State.cipherSelected = State.DEVICE;
			}

    //                  Get size of device        
			State.cipherReady = true;
			try (final SeekableByteChannel deviceChannel = Files.newByteChannel(cipherFileChooser.getSelectedFile().toPath(), EnumSet.of(StandardOpenOption.READ)))
			{ cipherSize = deviceChannel.size(); deviceChannel.close(); } catch (IOException ex) { ui.status(ex.getMessage(), true); }
		    } else { status("Probably no read permission on " + cipherFileChooser.getSelectedFile().toPath() + " execute: \"sudo dseditgroup -o edit -a " + System.getProperty("user.name") + " -t user operator; sudo chmod g+w /dev/disk*\" and re-login your desktop and try again\r\n", true); }
		} else { State.cipherReady = false; } // disk0
            }
            else
            {
                State.cipherSelected = State.INVALID;
                State.cipherReady = false;
            }
        }
        if ( cipherSize < finalCrypt.getBufferSize())
        {
            finalCrypt.setBufferSize((int)cipherSize);
            if (FinalCrypt.verbose) status("BufferSize is limited to cipherfile size: " + Stats.getHumanSize(finalCrypt.getBufferSize(), 1) + " \r\n", true);
        }
        checkModeReady();
    }

//  FileChooser Listener methods
    private void targetFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)                                                
    {
	if (!encryptionRunning)
	{
	    targetFileChooserPropertyCheck(true);
	}
    }
    
    private void targetFileChooserPropertyCheck(boolean status)
    {
        this.fileProgressBar.setProgress(0);
        this.filesProgressBar.setProgress(0);
        State.targetSelected = State.INVALID;
        State.targetReady = false;
        
//      En/Disable FileChooser deletebutton
        if ((targetFileChooser != null) && (targetFileChooser.getSelectedFiles() != null) && (targetFileChooser.getSelectedFiles().length > 0))
        {targetFileDeleteButton.setEnabled(true);} else {targetFileDeleteButton.setEnabled(false);}	
	
//      Test for Cipher Device Target
        if ((targetFileChooser != null) && (targetFileChooser.getSelectedFile() != null) && (targetFileChooser.getSelectedFiles().length == 1))
        {
            if (targetFileChooser.getSelectedFile().getAbsolutePath().startsWith("/dev/sd")) // Linux Cipher Device Device
            {
                if  (!targetFileChooser.getSelectedFile().getName().endsWith("sda")) // Not main disk
                {
		    if (isValidFile(targetFileChooser.getSelectedFile().toPath(), false, false, false))
		    {
			if (Character.isLetter(targetFileChooser.getSelectedFile().getName().charAt(targetFileChooser.getSelectedFile().getName().length()-1))) // Device selected
			{
			    State.targetSelected = State.DEVICE;
			    State.targetReady = true;
			}
			else
			{
			    State.targetSelected = State.PARTITION;
			    State.targetReady = false;
			}
		    } else { status("Probably no read & write permission on " + targetFileChooser.getSelectedFile().toPath() + " execute: \"sudo usermod -a -G disk " + System.getProperty("user.name") + "\" and re-login your desktop and try again\r\n", true); }
                }
            }
            else if (targetFileChooser.getSelectedFile().getAbsolutePath().startsWith("/dev/disk")) // Apple Cipher Device Device
            {
                if (!targetFileChooser.getSelectedFile().getName().endsWith("disk0")) // not primary disk
                {
		    if (isValidFile(targetFileChooser.getSelectedFile().toPath(), false, false, false))
		    {
			if (
				(Character.isDigit(targetFileChooser.getSelectedFile().getName().charAt(targetFileChooser.getSelectedFile().getName().length()-1))) && // last char = digit
				(!String.valueOf(targetFileChooser.getSelectedFile().getName().charAt(targetFileChooser.getSelectedFile().getName().length()-2)).equalsIgnoreCase("s")) // ! slice
			   ) 
			{
			    State.targetSelected = State.DEVICE;
			    State.targetReady = true;                    
			}
			else
			{
			    State.targetSelected = State.PARTITION;
			    State.targetReady = false;
			}
		    } else { status("Probably no read & write permission on " + targetFileChooser.getSelectedFile().toPath() + " execute: \"sudo dseditgroup -o edit -a " + System.getProperty("user.name") + " -t user operator; sudo chmod g+w /dev/disk*\" and re-login your desktop and try again\r\n", true); }
                }
            }
            else // No Cipher Device Device Target selected
            {
                State.targetSelected = State.INVALID;
                State.targetReady = false;                
            }
        }
        
//      En/Disable hasEncryptableItems
        if ((targetFileChooser != null) && (targetFileChooser.getSelectedFiles() != null) && ( State.cipherSelected != State.DEVICE ) && ( State.cipherReady ) ) // No need to scan for encryptable items without selected cipher for better performance
        {
            
            String pattern = "glob:*"; try { pattern = getSelectedPatternFromFileChooser( targetFileChooser.getFileFilter()); } catch (ClassCastException exc) {  }

//          Look for selected cipher file and feed to extendedPathlist to be excpluded from the WalkTree returned list
            Path cipherPath = null;
            if ( (cipherFileChooser.getSelectedFile() != null) && (State.cipherReady) ) { cipherPath = cipherFileChooser.getSelectedFile().toPath(); }

//          Look for encryptable files (Long I/O operation set hourglass)
            cursorWait();
            if (( targetFileChooser.getSelectedFiles().length == 1 ) )
            {
                if ( isValidFile(targetFileChooser.getSelectedFile().toPath(), true, finalCrypt.getSymlink(), false ) )   { State.targetSelected = State.FILE; State.targetReady = true; }
                else if ( isValidDir(targetFileChooser.getSelectedFile().toPath(), finalCrypt.getSymlink(), false))
                {
                    for (Path path:finalCrypt.getExtendedPathList(targetFileChooser.getSelectedFiles(), cipherPath, pattern, negatePattern, status) )
                    {
                        if ( isValidFile(path, true, finalCrypt.getSymlink(), true ) )   { State.targetSelected = State.FILE; State.targetReady = true; }
                    }
                } else { State.targetSelected = State.INVALID; State.targetReady = true; }
            }
            else if ( targetFileChooser.getSelectedFiles().length > 1 )
            {
                for (Path path:finalCrypt.getExtendedPathList(targetFileChooser.getSelectedFiles(), cipherPath, pattern, negatePattern, status) )
                {
                    if ( isValidFile(path, true, finalCrypt.getSymlink(), true ) )   { State.targetSelected = State.FILE; State.targetReady = true; }
                }
            }
            cursorDefault();
        }
        checkModeReady();
    }

    private void checkModeReady()
    {
        if ( !encryptionRunning )
        {
            Mode.modeReady = false;
            Platform.runLater(new Runnable(){ @Override public void run() { encryptButton.setText(Mode.setMode(Mode.SELECT)); } });

            if      (
			(State.targetSelected == State.FILE) && (State.targetReady) &&
			(State.cipherSelected == State.FILE) &&	(State.cipherReady)
		    )
            {
                if (
                        ( ( targetFileChooser != null) && (targetFileChooser.getSelectedFile() != null ) )          &&
                        ( ( cipherFileChooser != null) && (cipherFileChooser.getSelectedFile() != null ) )          &&
                        ( targetFileChooser.getSelectedFile().compareTo(cipherFileChooser.getSelectedFile()) != 0 )
                   )
                {
                    Mode.modeReady = true; Platform.runLater(new Runnable(){ @Override public void run() { encryptButton.setText(Mode.setMode(Mode.ENCRYPT)); } });
                }
            }
            else if ((State.targetSelected == State.FILE) && (State.targetReady) && (State.cipherSelected == State.PARTITION) && (State.cipherReady))
            {
                Mode.modeReady = true; Platform.runLater(new Runnable(){ @Override public void run() { encryptButton.setText(Mode.setMode(Mode.ENCRYPTRAW)); } });
            }
            else if ((State.targetSelected == State.DEVICE) && (State.targetReady) && (State.cipherSelected == State.FILE) &&	(State.cipherReady))
            {
                Mode.modeReady = true; Platform.runLater(new Runnable(){ @Override public void run() { encryptButton.setText(Mode.setMode(Mode.CREATE_CIPHER_DEVICE)); } });
            }
            else if ((State.targetSelected == State.DEVICE) && (State.targetReady) && (State.cipherSelected == State.DEVICE)&&	(State.cipherReady))
            {
//              Source and Dest Device may not be the same
                if (
                        ( ( targetFileChooser != null) && (targetFileChooser.getSelectedFile() != null ) )          &&
                        ( ( cipherFileChooser != null) && (cipherFileChooser.getSelectedFile() != null ) )          &&
                        ( targetFileChooser.getSelectedFile().compareTo(cipherFileChooser.getSelectedFile()) != 0 ) &&
                        ( State.targetSelected == State.DEVICE ) && ( State.cipherSelected == State.DEVICE )
                   )
                {
                    Mode.modeReady = true;
                    Platform.runLater(new Runnable(){ @Override public void run() { encryptButton.setText(Mode.setMode(Mode.CLONE_CIPHER_DEVICE)); } });
                }
                else
                { 
                    Mode.modeReady = false;
                }
            }
            else                                                                                    
            {
                Mode.modeReady = false; Platform.runLater(new Runnable(){ @Override public void run() { encryptButton.setText(Mode.setMode(Mode.SELECT)); } });
            }
            
            if ((State.targetReady) && (State.cipherReady) && (Mode.modeReady) )
            {
                encryptButton.setDisable(false);
                pauseToggleButton.setDisable(true);
                stopButton.setDisable(true);
            }
            else
            {
                encryptButton.setDisable(true);
                pauseToggleButton.setDisable(true);
                stopButton.setDisable(true);
            }
        }
    }

    public boolean isValidDir(Path targetDirPath, boolean symlink, boolean report)
    {
        boolean validdir = true; String conditions = "";		    String exist = ""; String read = ""; String write = ""; String symbolic = "";
        if ( ! Files.exists(targetDirPath))				    { validdir = false; exist = "[not found] "; conditions += exist; }
        if ( ! Files.isReadable(targetDirPath) )			    { validdir = false; read = "[not readable] "; conditions += read;  }
        if ( ! Files.isWritable(targetDirPath) )			    { validdir = false; write = "[not writable] "; conditions += write;  }
        if ( (! symlink) && (Files.isSymbolicLink(targetDirPath)) )	    { validdir = false; symbolic = "[symlink]"; conditions += symbolic;  }
        if ( validdir ) {  } else { if ( report )			    { error("Warning: Invalid Dir: " + targetDirPath.toString() + ": " + conditions + "\r\n"); } }
        return validdir;
    }

    public boolean isValidFile(Path targetSourcePath, Path cipherSourcePath, boolean readSize, boolean symlink, boolean report) // fileValidation Wrapper (including target==cipherSource comparison)
    {
	
        boolean validfile = true; String conditions = "";			    String cipher = "";
	validfile = isValidFile(targetSourcePath, readSize, symlink, report);
	if (validfile) { if ( targetSourcePath.compareTo(cipherSourcePath) == 0 )   { validfile = false; cipher = "[isCipher] "; conditions += cipher; }}	
        if ( ! validfile ) { if ( report )					    { error("Warning: GUIFX: Invalid File: " + targetSourcePath.toAbsolutePath().toString() + ": " + conditions + "\r\n"); } }                    
        return validfile;
    }

    public boolean isValidFile(Path targetSourcePath, boolean readSize, boolean symlink, boolean report)
    {
        boolean validfile = true; String conditions = "";		    String size = ""; String exist = ""; String dir = ""; String read = ""; String write = ""; String symbolic = ""; String cipher = "";
        long fileSize = 0;						    if ( readSize ) { try { fileSize = Files.size(targetSourcePath); } catch (IOException ex) { } }

        if ( ! Files.exists(targetSourcePath))                              { validfile = false; exist = "[not found] "; conditions += exist; }
        else
        {
            if ( Files.isDirectory(targetSourcePath))                       { validfile = false; dir = "[is directory] "; conditions += dir; }
            if ((readSize) && ( fileSize == 0 ))			    { validfile = false; size = "[empty] "; conditions += size; }
            if ( ! Files.isReadable(targetSourcePath) )                     { validfile = false; read = "[not readable] "; conditions += read; }
            if ( ! Files.isWritable(targetSourcePath) )                     { validfile = false; write = "[not writable] "; conditions += write; }
            if ( (! symlink) && (Files.isSymbolicLink(targetSourcePath)) )  { validfile = false; symbolic = "[symlink] "; conditions += symbolic; }
        }
        if ( ! validfile ) { if ( report )				    { error("Warning: GUIFX: Invalid File: " + targetSourcePath.toAbsolutePath().toString() + ": " + conditions + "\r\n"); } }                    
        return validfile;
    }

    private void cipherFileChooserActionPerformed(java.awt.event.ActionEvent evt)                                                  
    {                                                      
        this.fileProgressBar.setProgress(0);
        this.filesProgressBar.setProgress(0);
        if ((cipherFileChooser != null)  && (cipherFileChooser.getSelectedFile() != null))
        {
            if ( cipherFileChooser.getSelectedFile().isFile() ) 
            {
                try { Desktop.getDesktop().open(cipherFileChooser.getSelectedFile()); }
                catch (IOException ex) { error("Error: Desktop.getDesktop().open(cipherFileChooser.getSelectedFile()); " + ex.getMessage() + "\r\n"); }
            }
        } else { encryptButton.setDisable(true); }
        cipherFileChooser.setFileFilter(this.nonFinalCryptFilter);
        cipherFileChooser.setFileFilter(cipherFileChooser.getAcceptAllFileFilter()); // Resets rename due to doucle click file
        cipherFileChooser.removeChoosableFileFilter(this.nonFinalCryptFilter);
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
//            // Click "details view" ToggleButton
            if (component instanceof JToggleButton)
            {
                if (   ! ((JToggleButton)component).isSelected()   )
                {
                    // Needs a delay for proper column width 
                    TimerTask updateProgressTask = new TimerTask() { @Override public void run()
                    {
                        ((JToggleButton)component).doClick();
                    }};
                    Timer updateProgressTaskTimer = new java.util.Timer(); updateProgressTaskTimer.schedule(updateProgressTask, 2000L);
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
//            // Click "details view" ToggleButton
            if (component instanceof JToggleButton)
            {
                if (   ! ((JToggleButton)component).isSelected()   )
                {
                    TimerTask updateProgressTask = new TimerTask() { @Override public void run()
                    {
                        ((JToggleButton)component).doClick();
                    }};
                    Timer updateProgressTaskTimer = new java.util.Timer(); updateProgressTaskTimer.schedule(updateProgressTask, 1500L);
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
                if ( ( Mode.getMode() == Mode.ENCRYPT ) || ( Mode.getMode() == Mode.ENCRYPTRAW ))
                {
//                  Extend chooser.selectedfiles and add to targetFilesPath
                    String pattern = "glob:*"; try { pattern = getSelectedPatternFromFileChooser( targetFileChooser.getFileFilter()); } catch (ClassCastException exc) {  }
                    ArrayList<Path> targetFilesPathList = finalCrypt.getExtendedPathList(targetFileChooser.getSelectedFiles(), cipherFileChooser.getSelectedFile().toPath(), pattern, negatePattern, true);

//                    finalCrypt.setTargetFilesPathList(targetFilesPathList);
//                    finalCrypt.setCipherFilePath(cipherFileChooser.getSelectedFile().toPath());

    //                // Set Buffer Size
    //                finalCrypt.setBufferSize(finalCrypt.getBufferSizeDefault());

                    filesProgressBar.setProgress(0.0);
                    fileProgressBar.setProgress(0.0);

                    encryptionStarted();
                    finalCrypt.encryptSelection(targetFilesPathList, cipherFileChooser.getSelectedFile().toPath());
                }
                else if ( Mode.getMode() == Mode.CREATE_CIPHER_DEVICE )
                {
                    encryptionStarted();
                    deviceManager = new DeviceManager(guifx); deviceManager.start();
                    deviceManager.createCipherDevice(cipherFileChooser.getSelectedFile().toPath(), new Device(ui,targetFileChooser.getSelectedFile().toPath()));
                    encryptionFinished();
                }
                else if ( Mode.getMode() == Mode.CLONE_CIPHER_DEVICE )
                {
                    encryptionStarted();
                    deviceManager = new DeviceManager(ui); deviceManager.start();
                    deviceManager.cloneCipherDevice(new Device(ui,cipherFileChooser.getSelectedFile().toPath()), new Device(ui,targetFileChooser.getSelectedFile().toPath()));
                    encryptionFinished();
                }
            }
        });
        encryptThread.setName("encryptThread");
        encryptThread.setDaemon(true);
        encryptThread.start();
    }

    synchronized public void logNow(String message)    { lineCounter++;                            logTextArea.appendText(message); if (lineCounter > 1000) { logTextArea.setText(message); lineCounter = 0; } }

    @Override synchronized public void status(String status, boolean log)
    {
        Platform.runLater(new Runnable() { @Override public void run()
        {
            statusLabel.setText(status);
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
            logThread.setName("encryptThread");
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
            errorLogThread.setName("encryptThread");
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
    @Override public void encryptionStarted()
    {
        Platform.runLater(new Runnable()
        {
            @Override public void run()
            {
                encryptionRunning = true;
                encryptButton.setDisable(true);
                pauseToggleButton.setDisable(false);
                stopButton.setDisable(false);

                filesProgressBar.setProgress(0.0);
                fileProgressBar.setProgress(0.0);
                targetFileChooser.rescanCurrentDirectory();
                cipherFileChooser.rescanCurrentDirectory();
            }
        });
    }
    
    @Override public void encryptionGraph(int value) { Platform.runLater(new Runnable() { @Override public void run() { }});}
    @Override public void encryptionProgress(int fileProgressPercent, int filesProgressPercent)
    {
        Platform.runLater(new Runnable()
        {
            @Override public void run()
            {
                if (finalCrypt.getDebug()) { println("Progress File : " + filesProgressPercent / 100.0  + " factor"); }
                if (finalCrypt.getDebug()) { println("Progress Files: " + fileProgressPercent / 100.0 + " factor"); }
                fileProgressBar.setProgress((double)fileProgressPercent / 100.0); // percent needs to become factor in this gui
                filesProgressBar.setProgress((double)filesProgressPercent / 100.0); // percent needs to become factor in this gui                
            }
        });
    }

    @Override public void encryptionFinished()
    {
        Platform.runLater(new Runnable()
        {
            @Override public void run()
            {                                
                encryptionRunning = false;                
                fileProgressBar.setProgress(0);
                filesProgressBar.setProgress(0);
                targetFileChooser.setFileFilter(targetFileChooser.getAcceptAllFileFilter()); // Prevents users to scare about disappearing files as they might forget the selected filefilter
                targetFileChooser.rescanCurrentDirectory();  targetFileChooser.validate();
                cipherFileChooser.rescanCurrentDirectory(); cipherFileChooser.validate();
                targetFileChooserPropertyCheck(false);
                cipherFileChooserPropertyCheck();
            }
        });
    }    
    
    @FXML
    private void cipherInfoLabelClicked(MouseEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        
//      Style the Alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        
        alert.setTitle("Information Dialog");
        alert.setHeaderText("What is your secret Cipher file?");
        alert.setResizable(true);
        String infotext = new String();
        infotext  = "The cipher file encrypts the selected files on the left.\r\n";
        infotext += "Choose a personal cipher file (like a photo or video).\r\n";
        infotext += "\r\n";
        infotext += "Keep backups of your cipher file and keep it SECRET!\r\n";
        infotext += "Without cipher file you can NEVER decrypt your data!\r\n";
        infotext += "\r\n";
        infotext += "==================================\r\n";
        infotext += "\r\n";
        infotext += "Best practice is to have a unique and larger cipher file\r\n";
        infotext += "and encrypt that file with another personal cipher file.\r\n";
        infotext += "This encrypts metadata bit-patterns in your cipher.\r\n";
        infotext += "Keep your cipher file away from your computer for as\r\n";
        infotext += "long as you don't need it to hide it from big brother.\r\n";
        infotext += "\r\n";
        infotext += "Encryption:\r\n";
        infotext += "Unique cipher file bit patterns mask your data-bits.\r\n";
        infotext += "Positive cipher bits (1) negate correlating data-bits.\r\n";
        infotext += "Selected files become mutated with negating ciphers.\r\n";
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
        infotext  = "The selected items can be files and directories.\r\n";
        infotext += "All encrypted files get the *.bit extension added.\r\n";
        infotext += "All original files are securely deleted (shredded).\r\n";
        infotext += "\r\n";
        infotext += "Decrypt by encrypting again with the same cipher.\r\n";
        infotext += "After decryption, the *.bit extension gets removed.\r\n\r\n";
        alert.setContentText(infotext);
        alert.showAndWait();
    }

    @FXML
    private void pauseToggleButtonAction(ActionEvent event)
    {
//        if ( encryptButton.getText().equals("Encrypt") )
        if ( (Mode.getMode() == Mode.ENCRYPT ) || (Mode.getMode() == Mode.ENCRYPTRAW ) )
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
        if ( (Mode.getMode() == Mode.ENCRYPT ) || (Mode.getMode() == Mode.ENCRYPTRAW ) )
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
        Timeline timeline = new Timeline(new KeyFrame( Duration.millis(50), ae -> 
        {
            targetFileSwingNode.setContent(targetFileChooser);
        }
        )); timeline.play();
    }

    @FXML
    private void logTabSelectionChanged(Event event)
    {
    }
}
