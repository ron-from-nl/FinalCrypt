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

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Application;
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
import javafx.stage.Stage;
import javax.swing.JFileChooser;

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
    private Button pauseButton;
    @FXML
    private Button stopButton;
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
    private Slider bufferSlider;
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
    
    @Override
    public void start(Stage stage) throws Exception
    {
        guifx = this;
        this.stage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("GUIFX.fxml"));
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
//        System.out.println("Init!");
        inputFileChooser = new JFileChooser();
        inputFileChooser.setControlButtonsAreShown(false);
        inputFileChooser.setToolTipText("Right mousclick for Refresh");
        inputFileChooser.setMultiSelectionEnabled(true);
        inputFileChooser.setFocusable(true);
        inputFileChooser.setFont(new Font("Arimo", Font.PLAIN, 8));
        inputFileChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() { public void propertyChange(java.beans.PropertyChangeEvent evt) { inputFileChooserPropertyChange(evt); } });
        
        cipherFileChooser = new JFileChooser();
        cipherFileChooser.setControlButtonsAreShown(false);
        cipherFileChooser.setToolTipText("Right mousclick for Refresh");
        cipherFileChooser.setMultiSelectionEnabled(false);
        cipherFileChooser.setFocusable(true);
        cipherFileChooser.setFont(new Font("Arimo", Font.PLAIN, 12));
        cipherFileChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() { public void propertyChange(java.beans.PropertyChangeEvent evt) { cipherFileChooserPropertyChange(evt); } });
        
        inputFileSwingNode.setContent(inputFileChooser);
        cipherFileSwingNode.setContent(cipherFileChooser);   
        
        finalCrypt = new FinalCrypt(this);
        finalCrypt.start();
    }    

    private void inputFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)                                                
    {                                                    
        if ((inputFileChooser != null) && (cipherFileChooser != null) && (inputFileChooser.getSelectedFiles() != null) && (cipherFileChooser.getSelectedFile() != null))
        {
            if (
                    ( inputFileChooser.getSelectedFiles().length > 0 ) && 
                    ( cipherFileChooser.getSelectedFile().length() > (long)0 )
               )
            { encryptButton.setDisable(false); } else { encryptButton.setDisable(true); }
        } else { encryptButton.setDisable(true); }
    }                                               


    private void cipherFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)                                                 
    {                                                     
        if ((inputFileChooser != null) && (cipherFileChooser != null) && (inputFileChooser.getSelectedFiles() != null) && (cipherFileChooser.getSelectedFile() != null))
        {
            if ( 
                    ( inputFileChooser.getSelectedFiles().length > 0 ) && 
                    ( cipherFileChooser.getSelectedFile().length() > (long)0 )
               ) 
            { encryptButton.setDisable(false); } else { encryptButton.setDisable(true); }
        } else { encryptButton.setDisable(true); }
    }                                                

    public static void main(String[] args)
    {
        launch(args);
    }

    @FXML
    private void encryptButtonAction(ActionEvent event)
    {
        status("Encryption started");
//        finalCrypt = new FinalCrypt((UI)guifx);
//        finalCrypt = new FinalCrypt(this);
//        finalCrypt.start();
        
        Path outputFilePath = null;

        // Add the inputFilesPath to List from inputFileChooser
        ArrayList<Path> inputFilesPathList = new ArrayList<>(); for (File file:inputFileChooser.getSelectedFiles()) { inputFilesPathList.add(file.toPath()); }

        // Validate and create output files
        for(Path inputFilePathItem : inputFilesPathList)
        {
            if ( finalCrypt.isValidFile(inputFilePathItem, false, true) ) {} else   { error("Error input\n"); } // Compare inputfile to cipherfile
            if ( inputFilePathItem.compareTo(cipherFileChooser.getSelectedFile().toPath()) == 0 )      { error("Skipping inputfile: equal to cipherfile!\n"); }

            // Validate output file
            outputFilePath = inputFilePathItem.resolveSibling(inputFilePathItem.getFileName() + ".dat");
            if ( finalCrypt.isValidFile(outputFilePath, true, false) ) {} else  { error("Error cipher\n"); }
        }
                
        finalCrypt.setInputFilesPathList(inputFilesPathList);
        finalCrypt.setCipherFilePath(cipherFileChooser.getSelectedFile().toPath());
 
        // Resize file Buffers
        try 
        {
            if ( Files.size(finalCrypt.getCipherFilePath()) < finalCrypt.getBufferSize())
            {
                finalCrypt.setBufferSize((int) (long) Files.size(finalCrypt.getCipherFilePath()));
                if ( finalCrypt.getVerbose() ) { log("Alert: BufferSize limited to cipherfile size: " + finalCrypt.getBufferSize()); }
            }
        }
        catch (IOException ex) { error("Files.size(cfp)" + ex); }

        filesProgressBar.setProgress(0.0);
        fileProgressBar.setProgress(0.0);
        
        finalCrypt.encryptFiles();
                
////  SwingWorker version of FinalCrypt
//    try { finalCrypt.doInBackground(); } catch (Exception ex) { log(ex.getMessage()); }
    }
    
    @FXML
    private void pauseButtonAction(ActionEvent event)
    {
    }
    
    @FXML
    private void stopButtonAction(ActionEvent event)
    {
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
//        tab.setSelectedIndex((logButton.isSelected()) ? 1 : 0);
//        encryptTab.tab.setSelectedIndex((logButton.isSelected()) ? 1 : 0);
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
        verboseButton.setSelected(false);
        debugButton.setSelected(false);
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
        verboseButton.setSelected(false);
        debugButton.setSelected(false);
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
        verboseButton.setSelected(false);
        debugButton.setSelected(false);
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
        verboseButton.setSelected(false);
        debugButton.setSelected(false);
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
        verboseButton.setSelected(false);
        debugButton.setSelected(false);
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
        verboseButton.setSelected(false);
        debugButton.setSelected(false);
        setOptions();
    }
    
    @FXML
    private void verboseButtonAction(ActionEvent event)
    {
        printButton.setSelected(false);
        textButton.setSelected(false);
        binButton.setSelected(false);
        decButton.setSelected(false);
        hexButton.setSelected(false);
        charButton.setSelected(false);
//        verboseButton.setSelected(false);
        debugButton.setSelected(false);
        setOptions();
    }
    
    @FXML
    private void debugButtonAction(ActionEvent event)
    {
        printButton.setSelected(false);
        textButton.setSelected(false);
        binButton.setSelected(false);
        decButton.setSelected(false);
        hexButton.setSelected(false);
        charButton.setSelected(false);
        verboseButton.setSelected(false);
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
    public void log(String message) {
        logTextArea.appendText(message);
//        Thread logThread = new Thread(new Runnable()
//        {
//            @Override
//            @SuppressWarnings({"static-access"})
//            public void run()
//            {
////                logScroller.getVerticalScrollBar().setValue(logScroller.getVerticalScrollBar().getMaximum());
//            }
//        });
//        logThread.setName("updateProgressThread");
//        logThread.setDaemon(false);
//        logThread.start();
    }

    @Override
    public void error(String message) {
        status(message);
    }

    @Override
    public void status(String status) {
        statusLabel.setText(status);
        log(status);
    }

    @Override
    public void println(String message) {
        System.out.println(message);
    }

    @Override
    public void encryptionStarted() {
        status("Encryption Started\n");
        filesProgressBar.setProgress(0.0);
        fileProgressBar.setProgress(0.0);
        inputFileChooser.rescanCurrentDirectory();
        cipherFileChooser.rescanCurrentDirectory();
    }
    
    @Override
    public void encryptionGraph(int value) {
    }

    @Override
    public void encryptionProgress(int filesProgressPercent, int fileProgressPercent)
    {
//        Thread updateProgressThread = new Thread(new Runnable()
//        {
//            @Override
//            @SuppressWarnings({"static-access"})
//            public void run()
//            {
                if (finalCrypt.getDebug()) { System.out.println("Progress Files: " + filesProgressPercent / 100.0 + "%"); }
                if (finalCrypt.getDebug()) { System.out.println("Progress File : " + fileProgressPercent / 100.0 + "%"); }
        //        if (finalCrypt.getDebug()) { log("files " + filesPromille + "\n"); }
        //        if (finalCrypt.getDebug()) { log("file " + filePromille + "\n"); }
                filesProgressBar.setProgress((double)filesProgressPercent / 100.0);
                fileProgressBar.setProgress((double)fileProgressPercent / 100.0);
//            }
//        });
//        updateProgressThread.setName("updateProgressThread");
//        updateProgressThread.setDaemon(true);
//        updateProgressThread.start();
    }

    @Override
    public void encryptionEnded() {
        status("Encryption Finished\n");
        inputFileChooser.rescanCurrentDirectory();
        cipherFileChooser.rescanCurrentDirectory();
    }

}
