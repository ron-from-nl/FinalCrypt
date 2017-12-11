/*
 * Copyright (C) 2017 ron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author Ron de Jong ronuitzaandam@gmail.com
 */

package rdj;

import com.sun.javafx.application.PlatformImpl;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionListener;
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
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUIFX extends Application implements UI, Initializable
{
    private Stage stage;
    private Label label;
    @FXML
    private TabPane tab;
    @FXML
    private Tab encryptTab;
    @FXML
    private Label inputFileChooserLabel;
    @FXML
    private Label cipherFileChooserLabel;
    @FXML
    private Tab logTab;
    @FXML
    private TextArea logTextArea;
    @FXML
    private Button encryptButton;
    @FXML
    private ToggleButton logButton;
    @FXML
    private ToggleButton printButton;
    @FXML
    private ToggleButton textButton;
    @FXML
    private ToggleButton binButton;
    @FXML
    private ToggleButton decButton;
    @FXML
    private ToggleButton hexButton;
    @FXML
    private ToggleButton charButton;
    @FXML
    private ToggleButton verboseButton;
    @FXML
    private ProgressBar filesProgressBar;
    @FXML
    private ProgressBar fileProgressBar;
    @FXML
    private Label statusLabel;    

    FinalCrypt finalCrypt;
    GUIFX guifx;
    private JFileChooser inputFileChooser;
    private JFileChooser cipherFileChooser;
    @FXML
    private SwingNode inputFileSwingNode;
    @FXML
    private SwingNode cipherFileSwingNode;
    @FXML
    private ToggleButton debugButton;
    private JButton inputFileDeleteButton;
    private JButton cipherFileDeleteButton;
    private boolean hasEncryptableItem;
    private boolean hasCipherItem;
    private Object root;
    private Version update;
    @FXML
    private Label cipherFileChooserInfoLabel;
    @FXML
    private Label inputFileChooserInfoLabel;
    
    @Override
    public void start(Stage stage) throws Exception
    {
        guifx = this;
        this.stage = stage;
        root = FXMLLoader.load(getClass().getResource("GUIFX.fxml"));
        Scene scene = new Scene((Parent)root);
        
        stage.setScene(scene);
        stage.setTitle(Version.getProcuct());
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.setMaximized(true);
        stage.show();

//      start(..) comes after Initialize(..)
//        this.checkUpdate();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        inputFileDeleteButton = new javax.swing.JButton();
        inputFileDeleteButton.setFont(new java.awt.Font("Arimo", 0, 11)); // NOI18N
        inputFileDeleteButton.setText("X");
        inputFileDeleteButton.setEnabled(false);
        inputFileDeleteButton.setToolTipText("Delete selected item(s)");
        inputFileDeleteButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                inputFileDeleteButtonActionPerformed(evt);
            }
        });
        
        cipherFileDeleteButton = new javax.swing.JButton();
        cipherFileDeleteButton.setFont(new java.awt.Font("Arimo", 0, 11)); // NOI18N
        cipherFileDeleteButton.setText("X");
        cipherFileDeleteButton.setEnabled(false);
        cipherFileDeleteButton.setToolTipText("Delete selected item");
        cipherFileDeleteButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cipherFileDeleteButtonActionPerformed(evt);
            }
        });
        
        
//        inputFileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        inputFileChooser = new JFileChooser();
        inputFileChooser.setControlButtonsAreShown(false);
        inputFileChooser.setToolTipText("Right mousclick for Refresh");
        inputFileChooser.setMultiSelectionEnabled(true);
        inputFileChooser.setFocusable(true);
        inputFileChooser.setFont(new Font("Open Sans", Font.PLAIN, 10));
        inputFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        inputFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("FinalCrypt *.bit", "bit"));
        FileFilter nbf = new FileFilter()
                 {
                    @Override
                    public boolean accept(File file)
                    {
                       return !file.getName().toLowerCase().endsWith(".bit");
                    }

                    @Override
                    public String getDescription()
                    {
                       return "NON FinalCrypt";
                    }
                 };
        inputFileChooser.addChoosableFileFilter(nbf);
        inputFileChooser.addPropertyChangeListener
        (
            // New Object
            new java.beans.PropertyChangeListener()
            {
                // New Method
                @Override
                public void propertyChange(java.beans.PropertyChangeEvent evt) 
                {
                    inputFileChooserPropertyChange(evt);
                } 
            }
        );

        inputFileChooser.addActionListener
        (
            // New Object
            new ActionListener() 
            {
                // New Methid
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    inputFileChooserActionPerformed(evt);
                }
            }
        );
//        cipherFileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        cipherFileChooser = new JFileChooser();
        cipherFileChooser.setControlButtonsAreShown(false);
        cipherFileChooser.setToolTipText("Right mousclick for Refresh");
        cipherFileChooser.setMultiSelectionEnabled(false);
        cipherFileChooser.setFocusable(true);
        cipherFileChooser.setFont(new Font("Open Sans", Font.PLAIN, 10));
        cipherFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        cipherFileChooser.addPropertyChangeListener
        (
            new java.beans.PropertyChangeListener() 
            {
                @Override
                public void propertyChange(java.beans.PropertyChangeEvent evt)
                {
                    cipherFileChooserPropertyChange(evt);
                } 
            }
        );
        
        cipherFileChooser.addActionListener
        (
            // New Object
            new ActionListener() 
            {
                // New Methid
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    cipherFileChooserActionPerformed(evt);
                }
            }
        );
        
//        disable(inputFileChooser, false);
        inputFileChooserComponentAlteration(inputFileChooser);
        cipherFileChooserComponentAlteration(cipherFileChooser);


//      Put FileChoosers into SwingNodes        
        inputFileSwingNode.setContent(inputFileChooser);
        cipherFileSwingNode.setContent(cipherFileChooser);   
        
        finalCrypt = new FinalCrypt(this);
        finalCrypt.start();
        
        checkUpdate();
    }
    
    private void checkUpdate()
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Platform.runLater(() ->
                {
                    update = new Version(guifx);
                    update.checkLastestVersion();
                    status(update.getVersionReport());
//                    setStageTitle(update.getThisOverallVersionString());

                    if ( (update.versionIsDifferent()) && (update.versionCanBeUpdated()) )
                    {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Download new version: " + update.getLatestOverallVersionString() + "?", ButtonType.YES, ButtonType.NO);alert.setHeaderText("Download Update?"); alert.showAndWait();
                        if (alert.getResult() == ButtonType.YES)
                        {
                            Thread updateThread = new Thread(new Runnable()
                            {
                                @Override
                                @SuppressWarnings({"static-access"})
                                public void run()
                                {
                                    try { try {  Desktop.getDesktop().browse(new URI(Version.REMOTEVERSIONPACKAGESTRING)); }
                                    catch (URISyntaxException ex) { guifx.error(ex.getMessage()); }}
                                    catch (IOException ex) { guifx.error(ex.getMessage()); }
                                }
                            });
                            updateThread.setName("encryptThread");
                            updateThread.setDaemon(true);
                            updateThread.start();
                        }
                    }
                });
            }
        }, 3000);    
    
    }
    

//  Custom FileChooserDelete Listener methods
    private void inputFileDeleteButtonActionPerformed(java.awt.event.ActionEvent evt)                                                
    {
        PlatformImpl.runAndWait(new Runnable()
        {
            @Override
            public void run()
            {
                String itemword = "";
                if ( inputFileChooser.getSelectedFiles().length == 1 )      { itemword = "item"; }
                else if ( inputFileChooser.getSelectedFiles().length > 1 )  { itemword = "items"; }
                String selection = "Delete " + inputFileChooser.getSelectedFiles().length + " selected " + itemword + "?";
                Alert alert = new Alert(AlertType.CONFIRMATION, selection, ButtonType.YES, ButtonType.NO);alert.setHeaderText("Confirm Deletion?"); alert.showAndWait();
                if (alert.getResult() == ButtonType.YES)
                {
                    if ((inputFileChooser != null)  && (inputFileChooser.getSelectedFiles() != null))
                    {
                        if ( inputFileChooser.getSelectedFiles().length > 0 ) 
                        {
                            ArrayList<Path> pathList = finalCrypt.getPathList(inputFileChooser.getSelectedFiles());
                            boolean delete = true;
                            boolean returnpathlist = false;
                            String wildcard = "*";
                            finalCrypt.deleteSelection(pathList, delete, returnpathlist, wildcard);
                            inputFileChooser.rescanCurrentDirectory();  inputFileChooser.validate();
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
//            @SuppressWarnings({"static-access"})
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
                        String wildcard = "*";
                        finalCrypt.deleteSelection(pathList, delete, returnpathlist, wildcard);
                        inputFileChooser.rescanCurrentDirectory();  inputFileChooser.validate();
                        cipherFileChooser.rescanCurrentDirectory(); cipherFileChooser.validate();
                    }
                }
            }
        });
    }                                               

//  FileChooser Listener methods
    private void inputFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)                                                
    {                                                            
        this.fileProgressBar.setProgress(0);
        this.filesProgressBar.setProgress(0);
        hasEncryptableItem = false;
        
//      En/Disable FileChooser deletebutton
        if ((inputFileChooser != null) && (inputFileChooser.getSelectedFiles() != null) && (inputFileChooser.getSelectedFiles().length > 0))
        {inputFileDeleteButton.setEnabled(true);} else {inputFileDeleteButton.setEnabled(false);}

//      En/Disable hasEncryptableItems
        if ((inputFileChooser != null) && (inputFileChooser.getSelectedFiles() != null))
        {
            String extension = "*"; try { extension = getSelectedExtensionFromFileChooser( inputFileChooser.getFileFilter()); } catch (ClassCastException exc) {  }
            for (Path path:finalCrypt.getExtendedPathList(inputFileChooser.getSelectedFiles(), extension) )
            {
                if (Files.isRegularFile(path)) { hasEncryptableItem = true; }
            }
        }
        checkEncryptionReady();
    }

    private String getSelectedExtensionFromFileChooser( javax.swing.filechooser.FileFilter ff)
    {
        String extension = "*";
        String desc = "*";
        if ( ff != null ) {desc = ff.getDescription();}
        javax.swing.filechooser.FileNameExtensionFilter ef = null;
        try { ef = (javax.swing.filechooser.FileNameExtensionFilter) inputFileChooser.getFileFilter(); } catch (ClassCastException exc) {        }
        if ( ef != null ) 
        {
//            extension = ef.getExtensions()[0]; 
            desc = ef.getDescription();
        }
//        else { extension = "*"; }
        if      ( desc.startsWith("FinalCrypt") )       { extension = "bit"; }
        else if ( desc.startsWith("NON FinalCrypt") )   { extension = "[!b][!i][!t]"; }
        else                                            { extension = "*"; }
        return extension;
    }
//  FileChooser Listener methods
    private void inputFileChooserActionPerformed(java.awt.event.ActionEvent evt)                                                 
    {                                                     
        this.fileProgressBar.setProgress(0);
        this.filesProgressBar.setProgress(0);
        if ((inputFileChooser != null)  && (inputFileChooser.getSelectedFiles() != null))
        {
            if ( inputFileChooser.getSelectedFiles().length > 0 ) 
            {
                for (File file:inputFileChooser.getSelectedFiles()) 
                {
                    try { Desktop.getDesktop().open(file); }
                    catch (IOException ex) { error("Error: Desktop.getDesktop().open(file); " + ex.getMessage() + "\n"); }
                }
            }
        } else { encryptButton.setDisable(true); }
    }                                                

/////////////////////////////////////////////////////////////////////////////////////////////
    
    private void cipherFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)                                                 
    {                                                     
        this.fileProgressBar.setProgress(0);
        this.filesProgressBar.setProgress(0);

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
        
        // En/Disable hasCipherItem
        if ((cipherFileChooser != null) && (cipherFileChooser.getSelectedFile() != null))
        {
            if (
                    (Files.isRegularFile(cipherFileChooser.getSelectedFile().toPath())) &&
                    (cipherFileChooser.getSelectedFile().length() > 0)
               )
            { hasCipherItem = true; } else { hasCipherItem = false; }
        }
        
        checkEncryptionReady();
    }                                                

    private void checkEncryptionReady() { if ( (hasEncryptableItem) && (hasCipherItem) ) { encryptButton.setDisable(false); } else { encryptButton.setDisable(true); }}

    private void cipherFileChooserActionPerformed(java.awt.event.ActionEvent evt)                                                  
    {                                                      
        this.fileProgressBar.setProgress(0);
        this.filesProgressBar.setProgress(0);
        if ((cipherFileChooser != null)  && (cipherFileChooser.getSelectedFile() != null))
        {
            if ( cipherFileChooser.getSelectedFile().isFile() ) 
            {
                try { Desktop.getDesktop().open(cipherFileChooser.getSelectedFile()); }
                catch (IOException ex) { error("Error: Desktop.getDesktop().open(cipherFileChooser.getSelectedFile()); " + ex.getMessage() + "\n"); }
            }
        } else { encryptButton.setDisable(true); }
    }                                                 

    
    public static void main(String[] args)
    {
        launch(args);
    }

    public boolean inputFileChooserComponentAlteration(Container container)
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
                    Timer updateProgressTaskTimer = new java.util.Timer(); updateProgressTaskTimer.schedule(updateProgressTask, 1500L);
                }
            }
            
            // Add Delete button
            if (component instanceof JButton)
            {
                if (((JButton) component).getActionCommand().equalsIgnoreCase("New Folder"))
                {
                    component.getParent().add(this.inputFileDeleteButton);
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
                if( inputFileChooserComponentAlteration((Container) component) ) return false;
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
//                    component.getParent().add(this.inputFileDeleteButton);
//                    if (inputFileChooserContainer) { component.getParent().add(this.inputFileDeleteButton); } else { component.getParent().add(this.cipherFileDeleteButton); }
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
        Thread encryptThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
//              Extend chooser.selectedfiles and add to inputFilesPath
                String extension = "*"; try { extension = getSelectedExtensionFromFileChooser( inputFileChooser.getFileFilter()); } catch (ClassCastException exc) {  }
                ArrayList<Path> inputFilesPathList = finalCrypt.getExtendedPathList(inputFileChooser.getSelectedFiles(), extension);

                finalCrypt.setInputFilesPathList(inputFilesPathList);
                finalCrypt.setCipherFilePath(cipherFileChooser.getSelectedFile().toPath());

                // Set Buffer Size
                finalCrypt.setBufferSize(finalCrypt.getBufferSizeDefault());
                int cipherSize = 0; try { cipherSize = (int)Files.size(finalCrypt.getCipherFilePath()); } catch (IOException ex) { error("Files.size(finalCrypt.getCipherFilePath()) " + ex + "\n"); }
                if ( cipherSize < finalCrypt.getBufferSize())
                {
                    finalCrypt.setBufferSize(cipherSize);
                    status("BufferSize is limited to cipherfile size: " + Stats.getHumanSize(finalCrypt.getBufferSize(), 1) + " \n");
                }
                else
                {
                    status("BufferSize is set to: " + Stats.getHumanSize(finalCrypt.getBufferSize(), 1) + " \n");
                }
                
                filesProgressBar.setProgress(0.0);
                fileProgressBar.setProgress(0.0);

                encryptionStarted();
                finalCrypt.encryptSelection(inputFilesPathList, cipherFileChooser.getSelectedFile().toPath());
            }
        });
        encryptThread.setName("encryptThread");
        encryptThread.setDaemon(true);
        encryptThread.start();
    }

    @FXML
    private void logButtonAction(ActionEvent event)
    {
        printButton.setDisable(!logButton.isSelected());
        textButton.setDisable(!logButton.isSelected());
        binButton.setDisable(!logButton.isSelected());
        decButton.setDisable(!logButton.isSelected());
        hexButton.setDisable(!logButton.isSelected());
        charButton.setDisable(!logButton.isSelected());
        verboseButton.setDisable(!logButton.isSelected());
        debugButton.setDisable(!logButton.isSelected());
        tab.getSelectionModel().select((logButton.isSelected()) ? 1 : 0);
        setOptions();
    }
    
    @FXML
    private void printButtonAction(ActionEvent event)
    {
//        printButton.setSelected(false);
        textButton.setSelected(false);
        binButton.setSelected(false);
        decButton.setSelected(false);
        hexButton.setSelected(false);
        charButton.setSelected(false);
//        verboseButton.setSelected(false);
//        debugButton.setSelected(false);
        setOptions();
    }
    
    @FXML
    private void textButtonAction(ActionEvent event)
    {
        printButton.setSelected(false);
//        textButton.setSelected(false);
        binButton.setSelected(false);
        decButton.setSelected(false);
        hexButton.setSelected(false);
        charButton.setSelected(false);
//        verboseButton.setSelected(false);
//        debugButton.setSelected(false);
        setOptions();
    }
    
    @FXML
    private void binButtonAction(ActionEvent event)
    {
        printButton.setSelected(false);
        textButton.setSelected(false);
//        binButton.setSelected(false);
        decButton.setSelected(false);
        hexButton.setSelected(false);
        charButton.setSelected(false);
//        verboseButton.setSelected(false);
//        debugButton.setSelected(false);
        setOptions();
    }
    
    @FXML
    private void decButtonAction(ActionEvent event)
    {
        printButton.setSelected(false);
        textButton.setSelected(false);
        binButton.setSelected(false);
//        decButton.setSelected(false);
        hexButton.setSelected(false);
        charButton.setSelected(false);
//        verboseButton.setSelected(false);
//        debugButton.setSelected(false);
        setOptions();
    }
    
    @FXML
    private void hexButtonAction(ActionEvent event)
    {
        printButton.setSelected(false);
        textButton.setSelected(false);
        binButton.setSelected(false);
        decButton.setSelected(false);
//        hexButton.setSelected(false);
        charButton.setSelected(false);
//        verboseButton.setSelected(false);
//        debugButton.setSelected(false);
        setOptions();
    }
    
    @FXML
    private void charButtonAction(ActionEvent event)
    {
        printButton.setSelected(false);
        textButton.setSelected(false);
        binButton.setSelected(false);
        decButton.setSelected(false);
        hexButton.setSelected(false);
//        charButton.setSelected(false);
//        verboseButton.setSelected(false);
//        debugButton.setSelected(false);
        setOptions();
    }
    
    @FXML
    private void verboseButtonAction(ActionEvent event)
    {
//        printButton.setSelected(false);
//        textButton.setSelected(false);
//        binButton.setSelected(false);
//        decButton.setSelected(false);
//        hexButton.setSelected(false);
//        charButton.setSelected(false);
//        verboseButton.setSelected(false);
//        debugButton.setSelected(false);
        setOptions();
    }
    
    @FXML
    private void debugButtonAction(ActionEvent event)
    {
//        printButton.setSelected(false);
//        textButton.setSelected(false);
//        binButton.setSelected(false);
//        decButton.setSelected(false);
//        hexButton.setSelected(false);
//        charButton.setSelected(false);
//        verboseButton.setSelected(false);
//        debugButton.setSelected(false);
        setOptions();
    }
    
    private void setOptions()
    {
        finalCrypt.setPrint(logButton.isSelected() & printButton.isSelected());
        finalCrypt.setTXT(logButton.isSelected() & textButton.isSelected());
        finalCrypt.setBin(logButton.isSelected() & binButton.isSelected());
        finalCrypt.setDec(logButton.isSelected() & decButton.isSelected());
        finalCrypt.setHex(logButton.isSelected() & hexButton.isSelected());
        finalCrypt.setChr(logButton.isSelected() & charButton.isSelected());
        finalCrypt.setVerbose(logButton.isSelected() & verboseButton.isSelected());
        finalCrypt.setDebug(logButton.isSelected() & debugButton.isSelected());
    }
    
    @Override
    public void log(String message)
    {
        Platform.runLater(new Runnable()
        {
            @Override public void run()
            {
                logTextArea.appendText(message);
            }
        });
    }

    @Override
    public void error(String message)
    {
        Platform.runLater(new Runnable()
        {
            @Override public void run()
            {
                status(message);
            }
        });
    }

    @Override
    public void status(String status)
    {
        PlatformImpl.runAndWait(new Runnable()
        {
            @Override
//            @SuppressWarnings({"static-access"})
            public void run()
            {
                statusLabel.setText(status);
                log(status);
            }
        });
    }

    public void setStageTitle(String title)
    {
        PlatformImpl.runAndWait(new Runnable()
        {
            @Override
//            @SuppressWarnings({"static-access"})
            public void run()
            {
                guifx.stage.setTitle(title);
            }
        });
    }

    public void statusLater(String status)
    {
        statusLabel.setText(status);
    }

    @Override
    public void println(String message)
    {
        Platform.runLater(new Runnable()
        {
            @Override public void run()
            {
                System.out.println(message);
            }
        });
    }

    @Override
    public void encryptionStarted()
    {
        Platform.runLater(new Runnable()
        {
            @Override public void run()
            {
                encryptButton.setDisable(true);
                filesProgressBar.setProgress(0.0);
                fileProgressBar.setProgress(0.0);
                inputFileChooser.rescanCurrentDirectory();
                cipherFileChooser.rescanCurrentDirectory();
            }
        });
    }
    
    @Override
    public void encryptionGraph(int value)
    {
        Platform.runLater(new Runnable()
        {
            @Override public void run()
            {
            }
        });
    }

    @Override
    public void encryptionProgress(int fileProgressPercent, int filesProgressPercent)
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

    @Override
    public void encryptionFinished()
    {
        Platform.runLater(new Runnable()
        {
            @Override public void run()
            {
                encryptButton.setDisable(false);
                if ((finalCrypt.getDebug()) && (finalCrypt.getStats().getFileBytesTotal() != 0))   { println("Progress File : " + (finalCrypt.getStats().getFileBytesEncrypted() / finalCrypt.getStats().getFileBytesTotal()) + " factor"); }
                if ((finalCrypt.getDebug()) && (finalCrypt.getStats().getFilesBytesTotal() != 0))  { println("Progress Files: " + (finalCrypt.getStats().getFilesBytesEncrypted() / finalCrypt.getStats().getFilesBytesTotal()) + " factor"); }
                if ((finalCrypt.getDebug()) && (finalCrypt.getStats().getFileBytesTotal() != 0))   { log("Progress File : " + (finalCrypt.getStats().getFileBytesEncrypted() / finalCrypt.getStats().getFileBytesTotal()) + " factor\n"); }
                if ((finalCrypt.getDebug()) && (finalCrypt.getStats().getFilesBytesTotal() != 0))  { log("Progress Files: " + (finalCrypt.getStats().getFilesBytesEncrypted() / finalCrypt.getStats().getFilesBytesTotal()) + " factor\n"); }
                if (finalCrypt.getStats().getFileBytesTotal() != 0)                                { fileProgressBar.setProgress((finalCrypt.getStats().getFileBytesEncrypted() / finalCrypt.getStats().getFileBytesTotal())); }
                if (finalCrypt.getStats().getFilesBytesTotal() != 0)                               { filesProgressBar.setProgress((finalCrypt.getStats().getFilesBytesEncrypted() / finalCrypt.getStats().getFilesBytesTotal())); } // 50% becomes 0.5
                inputFileChooser.rescanCurrentDirectory();  inputFileChooser.validate();
                cipherFileChooser.rescanCurrentDirectory(); cipherFileChooser.validate();
            }
        });
    }    

    @FXML
    private void onLogButtonClicked(MouseEvent event)
    {
        if (event.getButton() == MouseButton.SECONDARY)
        {
            printButton.setVisible(!printButton.isVisible());
            textButton.setVisible(!textButton.isVisible());
            binButton.setVisible(!binButton.isVisible());
            decButton.setVisible(!decButton.isVisible());
            hexButton.setVisible(!hexButton.isVisible());
            charButton.setVisible(!charButton.isVisible());
        }
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
        infotext  = "The cipher file encrypts the selected files on the left.\n";
        infotext += "Choose a personal cipher file (like a photo or video).\n";
        infotext += "\n";
        infotext += "Keep backups of your cipher file and keep it SECRET!\n";
        infotext += "Without cipher file you can NEVER decrypt your data!\n";
        infotext += "\n";
        infotext += "==================================\n";
        infotext += "\n";
        infotext += "Best practice is to have a unique and larger cipher file\n";
        infotext += "and encrypt that file with another personal cipher file.\n";
        infotext += "This encrypts metadata bit-patterns in your cipher.\n";
        infotext += "Keep your cipher file away from your computer for as\n";
        infotext += "long as you don't need it to hide it from big brother.\n";
        infotext += "\n";
        infotext += "Encryption:\n";
        infotext += "Unique cipher file bit patterns mask your data-bits.\n";
        infotext += "Positive cipher bits (1) negate correlating data-bits.\n";
        infotext += "Selected files become mutated with negating ciphers.\n";
        infotext += "\n";
        infotext += "                  Encrypt                      Decrypt\n";
        infotext += "Data byte: 00000011 = 3    ╭─> 00000110 = 6\n";
        infotext += "Ciph byte: 00000101 = 5    │      00000101 = 5\n";
        infotext += "Encr byte: 00000110 = 6 ─╯       00000011 = 3  ";
//        infotext += " \n";
        alert.setContentText(infotext);
        alert.showAndWait();
    }

    @FXML
    private void inputInfoLabelClicked(MouseEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);

//      Style the Alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("myInfoAlerts.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");

        alert.setTitle("Information Dialog");
        alert.setHeaderText("What about your selected items?");
        alert.setResizable(true);
        String infotext = new String();
        infotext  = "The selected items can be files and directories.\n";
        infotext += "All encrypted files get a *.bit extension added.\n";
        infotext += "All original files are removed after encryption.\n";
        infotext += "\n";
        infotext += "Decrypt by encrypting again with the same cipher.\n";
        infotext += "After decryption, the *.bit extension gets removed.";
        alert.setContentText(infotext);
        alert.showAndWait();
    }
}
