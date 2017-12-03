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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;

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
//    private SwingNode cipherFileChooserSwingNode;
    private JFileChooser inputFileChooser;
    private JFileChooser cipherFileChooser;
//    private SwingNode swingNode2;
//    private StackPane stackPane;
//    private StackPane inputFileChooserStackPane;
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
    
    @Override
    public void start(Stage stage) throws Exception
    {
        guifx = this;
        this.stage = stage;
        root = FXMLLoader.load(getClass().getResource("GUIFX.fxml"));
        Scene scene = new Scene((Parent)root);
        
        stage.setScene(scene);
        stage.setTitle("FinalCrypt");
        stage.setMaximized(true);
        stage.show();
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
        
        
        inputFileChooser = new JFileChooser();
        inputFileChooser.setControlButtonsAreShown(false);
        inputFileChooser.setToolTipText("Right mousclick for Refresh");
        inputFileChooser.setMultiSelectionEnabled(true);
        inputFileChooser.setFocusable(true);
        inputFileChooser.setFont(new Font("Open Sans", Font.PLAIN, 10));
        inputFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        inputFileChooser.setFocusable(true);
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
        cipherFileChooser = new JFileChooser();
        cipherFileChooser.setControlButtonsAreShown(false);
        cipherFileChooser.setToolTipText("Right mousclick for Refresh");
        cipherFileChooser.setMultiSelectionEnabled(false);
        cipherFileChooser.setFocusable(true);
        cipherFileChooser.setFont(new Font("Open Sans", Font.PLAIN, 10));
        cipherFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        cipherFileChooser.setFocusable(true);
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
        
        disableSomeComponents(inputFileChooser, true);
        disableSomeComponents(cipherFileChooser, false);
        
//      Put FileChoosers into SwingNodes        
        inputFileSwingNode.setContent(inputFileChooser);
        cipherFileSwingNode.setContent(cipherFileChooser);   
        
        finalCrypt = new FinalCrypt(this);
        finalCrypt.start();          
    }
    
//  Custom FileChooserDelete Listener methods
    private void inputFileDeleteButtonActionPerformed(java.awt.event.ActionEvent evt)                                                
    {
      int selectedOption = JOptionPane.showConfirmDialog((Component) root, "Delete selected items?", "Choose", JOptionPane.YES_NO_OPTION);
        if (selectedOption == JOptionPane.YES_OPTION)
//        if (dialog.selectedOption == JOptionPane.YES_OPTION)
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

    private void cipherFileDeleteButtonActionPerformed(java.awt.event.ActionEvent evt)                                                
    {                                                            
        int selectedOption = JOptionPane.showConfirmDialog(null, "Delete selected item?", "Choose", JOptionPane.YES_NO_OPTION);
        if (selectedOption == JOptionPane.YES_OPTION)
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

//  FileChooser Listener methods
    private void inputFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)                                                
    {                                                            
        this.fileProgressBar.setProgress(0);
        this.filesProgressBar.setProgress(0);
        hasEncryptableItem = false;
        
//      En/Disable FileChooser deletebutton
        if ((inputFileChooser != null) && (inputFileChooser.getSelectedFiles() != null) && (inputFileChooser.getSelectedFiles().length > 0)) {inputFileDeleteButton.setEnabled(true);} else {inputFileDeleteButton.setEnabled(false);}
//      En/Disable hasEncryptableItems
        if ((inputFileChooser != null) && (inputFileChooser.getSelectedFiles() != null))
        {
            for (Path path:finalCrypt.getExtendedPathList(inputFileChooser.getSelectedFiles(), "*"))
            {
                if (Files.isRegularFile(path)) { hasEncryptableItem = true; }
            }
        }
        checkEncryptionReady();
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

    public boolean disableSomeComponents(Container container, boolean inputFileChooserContainer)
    {
        Component[] components = container.getComponents();
        for (Component component : components)
        {
            // Click "details view" ToggleButton
            if (component instanceof JToggleButton)
            {
                if (   ! ((JToggleButton)component).isSelected()   )
                {
                    ((JToggleButton)component).doClick();
                }
            }
            
            // Add Delete button
            if (component instanceof JButton)
            {
                if (((JButton) component).getActionCommand().equalsIgnoreCase("New Folder"))
                {
//                    component.getParent().add(this.inputFileDeleteButton);
                    if (inputFileChooserContainer) { component.getParent().add(this.inputFileDeleteButton); } else { component.getParent().add(this.cipherFileDeleteButton); }
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
                if( disableSomeComponents((Container) component, inputFileChooserContainer) ) return true;
            }
        }
        return false;
    }

    @FXML
    private void encryptButtonAction(ActionEvent event)
    {
        Thread encryptThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                status("Validating files\n");
                Path outputFilePath = null;

//                // Add the inputFilesPath to List from inputFileChooser
//                ArrayList<Path> inputFilesPathList = new ArrayList<>(); for (File file:inputFileChooser.getSelectedFiles()) { inputFilesPathList.add(file.toPath()); }

//                Add the inputFilesPath to List from inputFileChooser
                ArrayList<Path> inputFilesPathList = finalCrypt.getExtendedPathList(inputFileChooser.getSelectedFiles(), "*");

                // makes double additions
//                for (File file:inputFileChooser.getSelectedFiles()) { inputFilesPathList.add(file.toPath()); }

                // Validate and create output files
                for(Path inputFilePathItem : inputFilesPathList)
                {
                    finalCrypt.isValidFile(inputFilePathItem, false, true);
                    if ( inputFilePathItem.compareTo(cipherFileChooser.getSelectedFile().toPath()) == 0 )      { error("Skipping inputfile: equal to cipherfile!\n"); }

//                    // Validate output path
//                    outputFilePath = inputFilePathItem.resolveSibling(inputFilePathItem.getFileName() + ".dat");
//                    if ( finalCrypt.isValidFile(outputFilePath, true, false) ) {} else  { error("Error output\n"); }
                }

                finalCrypt.setInputFilesPathList(inputFilesPathList);
                finalCrypt.setCipherFilePath(cipherFileChooser.getSelectedFile().toPath());

                // Resize path Buffers
                try 
                {
                    if ( Files.size(finalCrypt.getCipherFilePath()) < finalCrypt.getBufferSize())
                    {
                        finalCrypt.setBufferSize((int) (long) Files.size(finalCrypt.getCipherFilePath()));
                        if ( finalCrypt.getVerbose() ) { log("Alert: BufferSize limited to cipherfile size: " + finalCrypt.getBufferSize()); }
                    }
                }
                catch (IOException ex) { error("Files.size(cfp)" + ex + "\n"); }

                filesProgressBar.setProgress(0.0);
                fileProgressBar.setProgress(0.0);

                encryptionStarted();
//                finalCrypt.encryptSelection();
                finalCrypt.encryptSelection(inputFilesPathList, cipherFileChooser.getSelectedFile().toPath());

////                SwingWorker version of FinalCrypt
//                try { finalCrypt.doInBackground(); } catch (Exception ex) { log(ex.getMessage()); }
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
    public void error(String message) { status(message); }

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



//        Platform.runLater(new Runnable()
//        {
//            @Override public void run()
//            {
//                statusLabel.setText(status);
//                log(status);
//            }
//        });
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
                status("Encryption Started\n");
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
                status("Encryption Finished\n");
                encryptButton.setDisable(false);
                if ((finalCrypt.getDebug()) && (finalCrypt.getFileBytesTotal() != 0))   { println("Progress File : " + (finalCrypt.getFileBytesEncrypted() / finalCrypt.getFileBytesTotal()) + " factor"); }
                if ((finalCrypt.getDebug()) && (finalCrypt.getFilesBytesTotal() != 0))  { println("Progress Files: " + (finalCrypt.getFilesBytesEncrypted() / finalCrypt.getFilesBytesTotal()) + " factor"); }
                if ((finalCrypt.getDebug()) && (finalCrypt.getFileBytesTotal() != 0))   { log("Progress File : " + (finalCrypt.getFileBytesEncrypted() / finalCrypt.getFileBytesTotal()) + " factor\n"); }
                if ((finalCrypt.getDebug()) && (finalCrypt.getFilesBytesTotal() != 0))  { log("Progress Files: " + (finalCrypt.getFilesBytesEncrypted() / finalCrypt.getFilesBytesTotal()) + " factor\n"); }
                if (finalCrypt.getFileBytesTotal() != 0)                                { fileProgressBar.setProgress((finalCrypt.getFileBytesEncrypted() / finalCrypt.getFileBytesTotal())); }
                if (finalCrypt.getFilesBytesTotal() != 0)                               { filesProgressBar.setProgress((finalCrypt.getFilesBytesEncrypted() / finalCrypt.getFilesBytesTotal())); } // 50% becomes 0.5
                inputFileChooser.rescanCurrentDirectory();  inputFileChooser.validate();
                cipherFileChooser.rescanCurrentDirectory(); cipherFileChooser.validate();
            }
        });
    }    

    @FXML
    private void onLogButtonClicked(MouseEvent event) {
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
}
