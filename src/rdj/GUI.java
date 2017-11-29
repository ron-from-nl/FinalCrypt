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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GUI extends javax.swing.JFrame implements UI
{
    FinalCrypt finalCrypt;
    GUI gui;

    public GUI()
    {
        gui = this;
                
//        SwingUtilities.invokeLater(new Runnable()
//        {
//            @Override
//            public void run()
//            {
                initComponents();
                try
                { UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); }
                catch (ClassNotFoundException ex) { }
                catch (InstantiationException ex) { }
                catch (IllegalAccessException ex) { }
                catch (UnsupportedLookAndFeelException ex) { }

                Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
                int winWidth = (int)getWidth();
                int winHeight = (int)getHeight();
                int posX = Math.round((screenDim.width / 2) - (winWidth / 2));
                int posY = Math.round((screenDim.height / 2) - (winHeight / 2));
                setLocation(posX, posY);
                
                finalCrypt = new FinalCrypt(gui);
                finalCrypt.start();
                
////              SwingWorker version of FinalCrypt
//                finalCrypt.execute();
//                finalCrypt.addPropertyChangeListener(new PropertyChangeListener()
//                {
//                    @Override
//                    public void propertyChange(PropertyChangeEvent pcEvt)
//                    {
//                        if (pcEvt.getPropertyName().equals("state"))
//                        {
//                            if (pcEvt.getNewValue() == SwingWorker.StateValue.DONE)
//                            {
//                                done();
////                                try { done(finalCrypt.get());} catch (InterruptedException e) { e.printStackTrace(); } catch (ExecutionException e) { e.printStackTrace(); }
//                            }
//                            else if (pcEvt.getNewValue() == SwingWorker.StateValue.STARTED)
//                            {
//                                start();
////                                try { done(finalCrypt.get());} catch (InterruptedException e) { e.printStackTrace(); } catch (ExecutionException e) { e.printStackTrace(); }
//                            }
//                            else { log("PropertyChange: " + pcEvt.getPropertyName() + " getNewValue: " + pcEvt.getNewValue());}
//                        }
//                        else if (pcEvt.getPropertyName().equals("progress"))
//                        {
//                            setProgress((Integer)pcEvt.getNewValue());
//                        }
//                        else { log("PropertyChange:" + pcEvt.getPropertyName());}
//                    }
//                });
//
//                try { finalCrypt.execute(); } catch (Exception ex) { log(ex.getMessage()); }
//            }
//        });
        

//        disableSomeComponents(inputFileChooser);
//        disableSomeComponents(cipherFileChooser);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tab = new javax.swing.JTabbedPane();
        encryptPanel = new javax.swing.JPanel();
        inputFilePanel = new javax.swing.JPanel();
        inputFileChooserLabel = new javax.swing.JLabel();
        inputFileChooser = new javax.swing.JFileChooser();
        cipherFilePanel = new javax.swing.JPanel();
        cipherFileChooserLabel = new javax.swing.JLabel();
        cipherFileChooser = new javax.swing.JFileChooser();
        logPane = new javax.swing.JPanel();
        logScroller = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        bottomPanel = new javax.swing.JPanel();
        buttonPanel1 = new javax.swing.JPanel();
        encryptButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        buttonPanel2 = new javax.swing.JPanel();
        logButton = new javax.swing.JToggleButton();
        printButton = new javax.swing.JToggleButton();
        textButton = new javax.swing.JToggleButton();
        binButton = new javax.swing.JToggleButton();
        decButton = new javax.swing.JToggleButton();
        hexButton = new javax.swing.JToggleButton();
        charButton = new javax.swing.JToggleButton();
        verboseButton = new javax.swing.JToggleButton();
        debugButton = new javax.swing.JToggleButton();
        bufferSlider = new javax.swing.JSlider();
        progressPanel = new javax.swing.JPanel();
        filesProgressBar = new javax.swing.JProgressBar();
        fileProgressBar = new javax.swing.JProgressBar();
        statusLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("FinalCrypt");
        setMinimumSize(new java.awt.Dimension(1040, 700));
        setPreferredSize(new java.awt.Dimension(1200, 800));

        tab.setBackground(new java.awt.Color(0, 0, 0));
        tab.setFont(tab.getFont().deriveFont(tab.getFont().getSize()+4f));
        tab.setMinimumSize(new java.awt.Dimension(1000, 29));
        tab.setPreferredSize(new java.awt.Dimension(1000, 100));

        encryptPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        encryptPanel.setPreferredSize(new java.awt.Dimension(1000, 524));
        encryptPanel.setLayout(new java.awt.GridLayout(1, 0));

        inputFilePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        inputFileChooserLabel.setFont(new java.awt.Font("Open Sans", 0, 18)); // NOI18N
        inputFileChooserLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        inputFileChooserLabel.setText("Select the files you want to encrypt");
        inputFileChooserLabel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        inputFileChooser.setControlButtonsAreShown(false);
        inputFileChooser.setCurrentDirectory(null);
        inputFileChooser.setToolTipText("Right mousclick for Refresh");
        inputFileChooser.setMultiSelectionEnabled(true);
        inputFileChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputFileChooserActionPerformed(evt);
            }
        });
        inputFileChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                inputFileChooserPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout inputFilePanelLayout = new javax.swing.GroupLayout(inputFilePanel);
        inputFilePanel.setLayout(inputFilePanelLayout);
        inputFilePanelLayout.setHorizontalGroup(
            inputFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 606, Short.MAX_VALUE)
            .addGroup(inputFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(inputFilePanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(inputFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(inputFileChooserLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(inputFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE))
                    .addContainerGap()))
        );
        inputFilePanelLayout.setVerticalGroup(
            inputFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 574, Short.MAX_VALUE)
            .addGroup(inputFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(inputFilePanelLayout.createSequentialGroup()
                    .addComponent(inputFileChooserLabel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(inputFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        encryptPanel.add(inputFilePanel);

        cipherFilePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cipherFileChooserLabel.setFont(new java.awt.Font("Open Sans", 0, 18)); // NOI18N
        cipherFileChooserLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cipherFileChooserLabel.setText("Select your secret cipher file");
        cipherFileChooserLabel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        cipherFileChooser.setControlButtonsAreShown(false);
        cipherFileChooser.setCurrentDirectory(null);
        cipherFileChooser.setToolTipText("");
        cipherFileChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cipherFileChooserActionPerformed(evt);
            }
        });
        cipherFileChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cipherFileChooserPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout cipherFilePanelLayout = new javax.swing.GroupLayout(cipherFilePanel);
        cipherFilePanel.setLayout(cipherFilePanelLayout);
        cipherFilePanelLayout.setHorizontalGroup(
            cipherFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 606, Short.MAX_VALUE)
            .addGroup(cipherFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(cipherFilePanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(cipherFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cipherFileChooserLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cipherFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE))
                    .addContainerGap()))
        );
        cipherFilePanelLayout.setVerticalGroup(
            cipherFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 574, Short.MAX_VALUE)
            .addGroup(cipherFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(cipherFilePanelLayout.createSequentialGroup()
                    .addComponent(cipherFileChooserLabel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cipherFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        encryptPanel.add(cipherFilePanel);

        tab.addTab("Encrypt", encryptPanel);

        logScroller.setAutoscrolls(true);
        logScroller.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        logTextArea.setColumns(20);
        logTextArea.setFont(new java.awt.Font("Courier 10 Pitch", 0, 18)); // NOI18N
        logTextArea.setRows(5);
        logScroller.setViewportView(logTextArea);

        javax.swing.GroupLayout logPaneLayout = new javax.swing.GroupLayout(logPane);
        logPane.setLayout(logPaneLayout);
        logPaneLayout.setHorizontalGroup(
            logPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, logPaneLayout.createSequentialGroup()
                .addComponent(logScroller, javax.swing.GroupLayout.DEFAULT_SIZE, 1219, Short.MAX_VALUE)
                .addContainerGap())
        );
        logPaneLayout.setVerticalGroup(
            logPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logScroller, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE)
        );

        tab.addTab("Log", logPane);

        bottomPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        bottomPanel.setLayout(new java.awt.GridLayout(3, 0));

        buttonPanel1.setLayout(new java.awt.GridLayout(1, 3));

        encryptButton.setFont(new java.awt.Font("Arimo", 0, 18)); // NOI18N
        encryptButton.setText("Encrypt");
        encryptButton.setEnabled(false);
        encryptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                encryptButtonActionPerformed(evt);
            }
        });
        buttonPanel1.add(encryptButton);

        pauseButton.setFont(new java.awt.Font("Arimo", 0, 18)); // NOI18N
        pauseButton.setText("Pause");
        pauseButton.setEnabled(false);
        buttonPanel1.add(pauseButton);

        stopButton.setFont(new java.awt.Font("Arimo", 0, 18)); // NOI18N
        stopButton.setText("Stop");
        stopButton.setEnabled(false);
        buttonPanel1.add(stopButton);

        bottomPanel.add(buttonPanel1);

        buttonPanel2.setLayout(new java.awt.GridLayout(1, 3));

        logButton.setText("Log");
        logButton.setToolTipText("Enable Data Logging");
        logButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logButtonActionPerformed(evt);
            }
        });
        buttonPanel2.add(logButton);

        printButton.setText("Print");
        printButton.setToolTipText("Log Overal Data");
        printButton.setEnabled(false);
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });
        buttonPanel2.add(printButton);

        textButton.setText("Text");
        textButton.setToolTipText("Log Text Data");
        textButton.setEnabled(false);
        textButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textButtonActionPerformed(evt);
            }
        });
        buttonPanel2.add(textButton);

        binButton.setText("Bin");
        binButton.setToolTipText("Log Binary Data");
        binButton.setEnabled(false);
        binButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                binButtonActionPerformed(evt);
            }
        });
        buttonPanel2.add(binButton);

        decButton.setText("Dec");
        decButton.setToolTipText("Log Decimal Data");
        decButton.setEnabled(false);
        decButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decButtonActionPerformed(evt);
            }
        });
        buttonPanel2.add(decButton);

        hexButton.setText("Hex");
        hexButton.setToolTipText("Log Hexadecimal Data");
        hexButton.setEnabled(false);
        hexButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hexButtonActionPerformed(evt);
            }
        });
        buttonPanel2.add(hexButton);

        charButton.setText("Char");
        charButton.setToolTipText("Log Character Data");
        charButton.setEnabled(false);
        charButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                charButtonActionPerformed(evt);
            }
        });
        buttonPanel2.add(charButton);

        verboseButton.setText("Verbose");
        verboseButton.setToolTipText("Logs more run details");
        verboseButton.setEnabled(false);
        verboseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verboseButtonActionPerformed(evt);
            }
        });
        buttonPanel2.add(verboseButton);

        debugButton.setText("Debug");
        debugButton.setToolTipText("Log Debug Data");
        debugButton.setEnabled(false);
        debugButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debugButtonActionPerformed(evt);
            }
        });
        buttonPanel2.add(debugButton);

        bufferSlider.setMaximum(1000);
        bufferSlider.setMinimum(1);
        bufferSlider.setPaintLabels(true);
        bufferSlider.setToolTipText("Sets buffersize in MB");
        buttonPanel2.add(bufferSlider);

        bottomPanel.add(buttonPanel2);

        progressPanel.setLayout(new java.awt.GridLayout(3, 0));

        filesProgressBar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        filesProgressBar.setDoubleBuffered(true);
        filesProgressBar.setStringPainted(true);
        progressPanel.add(filesProgressBar);

        fileProgressBar.setDoubleBuffered(true);
        fileProgressBar.setStringPainted(true);
        progressPanel.add(fileProgressBar);

        statusLabel.setBackground(new java.awt.Color(255, 0, 0));
        statusLabel.setForeground(new java.awt.Color(50, 50, 50));
        statusLabel.setText("Status");
        statusLabel.setBorder(null);
        progressPanel.add(statusLabel);

        bottomPanel.add(progressPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1225, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void inputFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_inputFileChooserPropertyChange
    {//GEN-HEADEREND:event_inputFileChooserPropertyChange
        if ((inputFileChooser != null) && (cipherFileChooser != null) && (inputFileChooser.getSelectedFiles() != null) && (cipherFileChooser.getSelectedFile() != null))
        {
            if (
                    ( inputFileChooser.getSelectedFiles().length > 0 ) && 
                    ( cipherFileChooser.getSelectedFile().length() > (long)0 )
               )
            { encryptButton.setEnabled(true); } else { encryptButton.setEnabled(false); }
        } else { encryptButton.setEnabled(false); }
    }//GEN-LAST:event_inputFileChooserPropertyChange

    private void inputFileChooserActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_inputFileChooserActionPerformed
    {//GEN-HEADEREND:event_inputFileChooserActionPerformed
    }//GEN-LAST:event_inputFileChooserActionPerformed

    private void cipherFileChooserActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cipherFileChooserActionPerformed
    {//GEN-HEADEREND:event_cipherFileChooserActionPerformed
    }//GEN-LAST:event_cipherFileChooserActionPerformed

    private void cipherFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_cipherFileChooserPropertyChange
    {//GEN-HEADEREND:event_cipherFileChooserPropertyChange
        if ((inputFileChooser != null) && (cipherFileChooser != null) && (inputFileChooser.getSelectedFiles() != null) && (cipherFileChooser.getSelectedFile() != null))
        {
            if ( 
                    ( inputFileChooser.getSelectedFiles().length > 0 ) && 
                    ( cipherFileChooser.getSelectedFile().length() > (long)0 )
               ) 
            { encryptButton.setEnabled(true); } else { encryptButton.setEnabled(false); }
        } else { encryptButton.setEnabled(false); }
    }//GEN-LAST:event_cipherFileChooserPropertyChange

    private void encryptButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_encryptButtonActionPerformed
    {//GEN-HEADEREND:event_encryptButtonActionPerformed
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

        filesProgressBar.setValue(0);
        fileProgressBar.setValue(0);
        
        finalCrypt.encryptFiles();
                
////  SwingWorker version of FinalCrypt
//    try { finalCrypt.doInBackground(); } catch (Exception ex) { log(ex.getMessage()); }
    }//GEN-LAST:event_encryptButtonActionPerformed

    public void setProgressBarsMax(int filesMax, int fileMax)
    {        
//        filesProgressBar.setMaximum(finalCrypt.filesBytesTotal);
//        fileProgressBar.setMaximum(finalCrypt.fileBytesTotal);
        filesProgressBar.setMaximum(filesMax);
        fileProgressBar.setMaximum(fileMax);
    }
    
    private void logButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_logButtonActionPerformed
    {//GEN-HEADEREND:event_logButtonActionPerformed
        printButton.setEnabled(logButton.isSelected());
        textButton.setEnabled(logButton.isSelected());
        binButton.setEnabled(logButton.isSelected());
        decButton.setEnabled(logButton.isSelected());
        hexButton.setEnabled(logButton.isSelected());
        charButton.setEnabled(logButton.isSelected());
        verboseButton.setEnabled(logButton.isSelected());
        debugButton.setEnabled(logButton.isSelected());
        tab.setSelectedIndex((logButton.isSelected()) ? 1 : 0);
        setOptions();
    }//GEN-LAST:event_logButtonActionPerformed

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_printButtonActionPerformed
    {//GEN-HEADEREND:event_printButtonActionPerformed
//        printButton.setSelected(false);
        textButton.setSelected(false);
        binButton.setSelected(false);
        decButton.setSelected(false);
        hexButton.setSelected(false);
        charButton.setSelected(false);
//        verboseButton.setSelected(false);
//        debugButton.setSelected(false);
        setOptions();
    }//GEN-LAST:event_printButtonActionPerformed

    private void textButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_textButtonActionPerformed
    {//GEN-HEADEREND:event_textButtonActionPerformed
        printButton.setSelected(false);
//        textButton.setSelected(false);
        binButton.setSelected(false);
        decButton.setSelected(false);
        hexButton.setSelected(false);
        charButton.setSelected(false);
//        verboseButton.setSelected(false);
//        debugButton.setSelected(false);
        setOptions();
    }//GEN-LAST:event_textButtonActionPerformed

    private void binButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_binButtonActionPerformed
    {//GEN-HEADEREND:event_binButtonActionPerformed
        printButton.setSelected(false);
        textButton.setSelected(false);
//        binButton.setSelected(false);
        decButton.setSelected(false);
        hexButton.setSelected(false);
        charButton.setSelected(false);
//        verboseButton.setSelected(false);
//        debugButton.setSelected(false);
        setOptions();
    }//GEN-LAST:event_binButtonActionPerformed

    private void decButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_decButtonActionPerformed
    {//GEN-HEADEREND:event_decButtonActionPerformed
        printButton.setSelected(false);
        textButton.setSelected(false);
        binButton.setSelected(false);
//        decButton.setSelected(false);
        hexButton.setSelected(false);
        charButton.setSelected(false);
//        verboseButton.setSelected(false);
//        debugButton.setSelected(false);
        setOptions();
    }//GEN-LAST:event_decButtonActionPerformed

    private void hexButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_hexButtonActionPerformed
    {//GEN-HEADEREND:event_hexButtonActionPerformed
        printButton.setSelected(false);
        textButton.setSelected(false);
        binButton.setSelected(false);
        decButton.setSelected(false);
//        hexButton.setSelected(false);
        charButton.setSelected(false);
//        verboseButton.setSelected(false);
//        debugButton.setSelected(false);
        setOptions();
    }//GEN-LAST:event_hexButtonActionPerformed

    private void charButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_charButtonActionPerformed
    {//GEN-HEADEREND:event_charButtonActionPerformed
        printButton.setSelected(false);
        textButton.setSelected(false);
        binButton.setSelected(false);
        decButton.setSelected(false);
        hexButton.setSelected(false);
//        charButton.setSelected(false);
//        verboseButton.setSelected(false);
//        debugButton.setSelected(false);
        setOptions();
    }//GEN-LAST:event_charButtonActionPerformed

    private void verboseButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_verboseButtonActionPerformed
    {//GEN-HEADEREND:event_verboseButtonActionPerformed
//        printButton.setSelected(false);
//        textButton.setSelected(false);
//        binButton.setSelected(false);
//        decButton.setSelected(false);
//        hexButton.setSelected(false);
//        charButton.setSelected(false);
//        verboseButton.setSelected(false);
//        debugButton.setSelected(false);
        setOptions();
    }//GEN-LAST:event_verboseButtonActionPerformed

    private void debugButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_debugButtonActionPerformed
    {//GEN-HEADEREND:event_debugButtonActionPerformed
//        printButton.setSelected(false);
//        textButton.setSelected(false);
//        binButton.setSelected(false);
//        decButton.setSelected(false);
//        hexButton.setSelected(false);
//        charButton.setSelected(false);
//        verboseButton.setSelected(false);
//        debugButton.setSelected(false);
        setOptions();
    }//GEN-LAST:event_debugButtonActionPerformed

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
    
    public static void main(String args[])
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new GUI().setVisible(true);
            }
        });
    }
    
    public boolean disableSomeComponents(Container container)
    {
        Component[] components = container.getComponents();
        for (Component component : components)
        {
            if (component instanceof JTextField)
            {
                ((JTextField)component).setEnabled(false);
                ((JTextField)component).setVisible(false);                
//                return true;
            }
            if (component instanceof JComboBox)
            {
                if ( ((JComboBox)component).getSelectedItem().toString().toLowerCase().contains("BasicFileChooserUI".toLowerCase()) )
                {
                    ((JComboBox)component).setEnabled(false);
                    ((JComboBox)component).setVisible(false);                
                }
//                return true;
            }
            if (component instanceof JLabel)
            {
                ((JLabel)component).setEnabled(false);
                ((JLabel)component).setVisible(false);                
//                return true;
            }
            if (component instanceof JToggleButton)
            {
                if (   ! ((JToggleButton)component).isSelected()   )
                {
                    ((JToggleButton)component).doClick();
                }
            }
            if (component instanceof Container)
            {
                if(disableSomeComponents((Container) component)) return true;
            }
        }
        return false;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton binButton;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JSlider bufferSlider;
    private javax.swing.JPanel buttonPanel1;
    private javax.swing.JPanel buttonPanel2;
    private javax.swing.JToggleButton charButton;
    private javax.swing.JFileChooser cipherFileChooser;
    private javax.swing.JLabel cipherFileChooserLabel;
    private javax.swing.JPanel cipherFilePanel;
    private javax.swing.JToggleButton debugButton;
    private javax.swing.JToggleButton decButton;
    private javax.swing.JButton encryptButton;
    private javax.swing.JPanel encryptPanel;
    private javax.swing.JProgressBar fileProgressBar;
    private javax.swing.JProgressBar filesProgressBar;
    private javax.swing.JToggleButton hexButton;
    private javax.swing.JFileChooser inputFileChooser;
    private javax.swing.JLabel inputFileChooserLabel;
    private javax.swing.JPanel inputFilePanel;
    private javax.swing.JToggleButton logButton;
    private javax.swing.JPanel logPane;
    private javax.swing.JScrollPane logScroller;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JButton pauseButton;
    private javax.swing.JToggleButton printButton;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JButton stopButton;
    private javax.swing.JTabbedPane tab;
    private javax.swing.JToggleButton textButton;
    private javax.swing.JToggleButton verboseButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void log(final String message)
    {
        logTextArea.append(message);
        Thread logThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                logScroller.getVerticalScrollBar().setValue(logScroller.getVerticalScrollBar().getMaximum());
            }
        });
        logThread.setName("updateProgressThread");
        logThread.setDaemon(false);
        logThread.start();
    }

    @Override
    public void error(final String message)
    {
        status(message);
    }

    @Override
    synchronized public void status(final String status)
    {
        statusLabel.setText(status);
        log(status);
    }

    @Override
    public void println(String message)
    {
        System.out.println(message);
    }

    @Override
    synchronized public void encryptionGraph(final int value)
    {
        Thread updateGraphThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
            }
        });
        updateGraphThread.setName("updateProgressThread");
        updateGraphThread.setDaemon(true);
        updateGraphThread.start();
    }

    @Override
    public void encryptionStarted() {
        status("Encryption Started\n");
        filesProgressBar.setValue(0);
        fileProgressBar.setValue(0);
        inputFileChooser.rescanCurrentDirectory();
        cipherFileChooser.rescanCurrentDirectory();
    }

    // Threaded version of FinalCrypt
    @Override
    public void encryptionProgress(final int filesProgressPercent, final int fileProgressPercent)
    {
                if (finalCrypt.getDebug()) { println("Progress Files: " + filesProgressPercent + "%\n"); }
                if (finalCrypt.getDebug()) { println("Progress File : " + fileProgressPercent + "%\n"); }
//                if (finalCrypt.getDebug()) { log("files " + filesPromille + "\n"); }
//                if (finalCrypt.getDebug()) { log("file " + filePromille + "\n"); }
                filesProgressBar.setValue(filesProgressPercent);
                fileProgressBar.setValue(fileProgressPercent);
    }
     
//  SwingWorker version of FinalCrypt     
    public void setProgress(Integer newValue) 
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                if (finalCrypt.getDebug()) { println("Progress Files: " + newValue + "%\n"); }
                if (finalCrypt.getDebug()) { println("Progress File : " + newValue + "%\n"); }
//                if (finalCrypt.getDebug()) { log("files " + filesPromille + "\n"); }
//                if (finalCrypt.getDebug()) { log("file " + filePromille + "\n"); }
                filesProgressBar.setValue(newValue);
//                fileProgressBar.setValue(newValue);
            }
        });
    }

    @Override
    synchronized public void encryptionEnded()
    {
//        Thread encryptionEndedThread = new Thread(new Runnable()
//        {
//            @Override
//            @SuppressWarnings({"static-access"})
//            public void run()
//            {
                status("Encryption Finished\n");
                if (finalCrypt.getDebug()) { println("Progress Files: " +   (int)(finalCrypt.getFilesBytesEncrypted() / (finalCrypt.getFilesBytesTotal() / 100.0)) + "%"); }
                if (finalCrypt.getDebug()) { println("Progress File : " +   (int)(finalCrypt.getFileBytesEncrypted()  / (finalCrypt.getFileBytesTotal()  / 100.0)) + "%"); }
                if (finalCrypt.getDebug()) { log("Progress Files: " +       (int)(finalCrypt.getFilesBytesEncrypted() / (finalCrypt.getFilesBytesTotal() / 100.0)) + "%\n"); }
                if (finalCrypt.getDebug()) { log("Progress File : " +       (int)(finalCrypt.getFileBytesEncrypted()  / (finalCrypt.getFileBytesTotal()  / 100.0)) + "%\n"); }
                filesProgressBar.setValue(                                  (int)(finalCrypt.getFilesBytesEncrypted() / (finalCrypt.getFilesBytesTotal() / 100.0)));
                fileProgressBar.setValue(                                   (int)(finalCrypt.getFileBytesEncrypted()  / (finalCrypt.getFileBytesTotal()  / 100.0)));
                inputFileChooser.rescanCurrentDirectory();
                cipherFileChooser.rescanCurrentDirectory();
//            }
//        });
//        encryptionEndedThread.setName("updateProgressThread");
//        encryptionEndedThread.setDaemon(true);
//        encryptionEndedThread.start();
    }

//// SwingWorker Method
//   public void start() 
//   {
//       System.out.println("GUI.start()");
//       filesProgressBar.setValue(0);
//   }

//// SwingWorker Method
//   public void done()
//   {
//       System.out.println("GUI.done()");
//      filesProgressBar.setValue(0);
//      
////      new Timer(TIMER_DELAY, new ActionListener()
////      {
////          @Override public void actionPerformed(ActionEvent e) { Window win = SwingUtilities.getWindowAncestor(mainPanel); win.dispose(); }
////      }) {{setRepeats(false);}}.start();
//   }     
}
