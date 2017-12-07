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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GUI extends javax.swing.JFrame implements UI
{
    FinalCrypt finalCrypt;
    GUI gui;
    private JButton cipherFileDeleteButton;
    private JButton inputFileDeleteButton;
    private boolean hasEncryptableItem;
    private boolean hasCipherItem = false;

    public GUI()
    {
        gui = this;

//      Create Custom DeleteButtons to be injected into JFileChooser
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

        initComponents();  
        this.setTitle(FinalCrypt.getProcuct() + " " + FinalCrypt.getVersion());

        printButton.setVisible(false);
        textButton.setVisible(false);
        binButton.setVisible(false);
        decButton.setVisible(false);
        hexButton.setVisible(false);
        charButton.setVisible(false);

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
        this.setSize(screenDim);

        finalCrypt = new FinalCrypt(gui);
        finalCrypt.start();
                

        inputFileChooserComponentAlteration(inputFileChooser);
        cipherFileChooserComponentAlteration(cipherFileChooser);
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
        progressPanel = new javax.swing.JPanel();
        fileProgressBar = new javax.swing.JProgressBar();
        filesProgressBar = new javax.swing.JProgressBar();
        statusLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("FinalCrypt");
        setMinimumSize(new java.awt.Dimension(1040, 700));
        setName("frame"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1100, 700));

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
        inputFileChooserLabel.setText("Select the items you want to encrypt");
        inputFileChooserLabel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        inputFileChooser.setControlButtonsAreShown(false);
        inputFileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        inputFileChooser.setFont(inputFileChooser.getFont().deriveFont((float)10));
        inputFileChooser.setToolTipText("Right mousclick for Refresh");
        inputFileChooser.setMultiSelectionEnabled(true);
        inputFileChooser.setPreferredSize(new java.awt.Dimension(800, 262));
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
        inputFileChooser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputFileChooserKeyPressed(evt);
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
            .addGap(0, 606, Short.MAX_VALUE)
            .addGroup(inputFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(inputFilePanelLayout.createSequentialGroup()
                    .addComponent(inputFileChooserLabel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(inputFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        encryptPanel.add(inputFilePanel);

        cipherFilePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cipherFileChooserLabel.setFont(new java.awt.Font("Open Sans", 0, 18)); // NOI18N
        cipherFileChooserLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cipherFileChooserLabel.setText("Select your secret cipher file");
        cipherFileChooserLabel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        cipherFileChooser.setControlButtonsAreShown(false);
        cipherFileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        cipherFileChooser.setFont(cipherFileChooser.getFont().deriveFont((float)10));
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
            .addGap(0, 606, Short.MAX_VALUE)
            .addGroup(cipherFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(cipherFilePanelLayout.createSequentialGroup()
                    .addComponent(cipherFileChooserLabel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cipherFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        encryptPanel.add(cipherFilePanel);

        tab.addTab("Encrypt", encryptPanel);

        logScroller.setAutoscrolls(true);
        logScroller.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        logTextArea.setColumns(20);
        logTextArea.setFont(new java.awt.Font("Cousine", 0, 14)); // NOI18N
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
            .addComponent(logScroller, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
        );

        tab.addTab("Log", logPane);

        bottomPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        bottomPanel.setLayout(new java.awt.GridLayout(3, 0));

        buttonPanel1.setMaximumSize(new java.awt.Dimension(32767, 20));
        buttonPanel1.setMinimumSize(new java.awt.Dimension(89, 20));
        buttonPanel1.setPreferredSize(new java.awt.Dimension(89, 20));
        buttonPanel1.setLayout(new java.awt.GridLayout(1, 3));

        encryptButton.setFont(new java.awt.Font("Arimo", 0, 18)); // NOI18N
        encryptButton.setText("Encrypt");
        encryptButton.setEnabled(false);
        encryptButton.setMaximumSize(new java.awt.Dimension(89, 20));
        encryptButton.setMinimumSize(new java.awt.Dimension(89, 20));
        encryptButton.setPreferredSize(new java.awt.Dimension(89, 20));
        encryptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                encryptButtonActionPerformed(evt);
            }
        });
        buttonPanel1.add(encryptButton);

        bottomPanel.add(buttonPanel1);

        buttonPanel2.setLayout(new java.awt.GridLayout(1, 3));

        logButton.setText("Log");
        logButton.setToolTipText("Enable Data Logging");
        logButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logButtonMouseClicked(evt);
            }
        });
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

        bottomPanel.add(buttonPanel2);

        progressPanel.setLayout(new java.awt.GridLayout(3, 0));

        fileProgressBar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        fileProgressBar.setDoubleBuffered(true);
        fileProgressBar.setStringPainted(true);
        progressPanel.add(fileProgressBar);

        filesProgressBar.setDoubleBuffered(true);
        filesProgressBar.setStringPainted(true);
        progressPanel.add(filesProgressBar);

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
                .addComponent(tab, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

//  Custom FileChooserDelete Listener methods
    private void inputFileDeleteButtonActionPerformed(java.awt.event.ActionEvent evt)                                                
    {                                                            
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                String itemword = "";
                if ( inputFileChooser.getSelectedFiles().length == 1 )      { itemword = "item"; }
                else if ( inputFileChooser.getSelectedFiles().length > 1 )  { itemword = "items"; }
                int selectedOption = JOptionPane.showConfirmDialog(null, "Delete " + inputFileChooser.getSelectedFiles().length + " selected " + itemword + "?", "Choose", JOptionPane.YES_NO_OPTION);
                if (selectedOption == JOptionPane.YES_OPTION)
                {           
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
            }
        });
    }                                               

    private void cipherFileDeleteButtonActionPerformed(java.awt.event.ActionEvent evt)                                                
    {                                                            
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                String selection = "Delete selected item?";
                int selectedOption = JOptionPane.showConfirmDialog(null, selection, "Choose", JOptionPane.YES_NO_OPTION);
                if (selectedOption == JOptionPane.YES_OPTION)
                {
                    if ((cipherFileChooser != null)  && (cipherFileChooser.getSelectedFiles() != null))
                    {
                        ArrayList<Path> pathList = new ArrayList<Path>();
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

    private void inputFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_inputFileChooserPropertyChange
    {//GEN-HEADEREND:event_inputFileChooserPropertyChange
        this.fileProgressBar.setValue(0);
        this.filesProgressBar.setValue(0);
        hasEncryptableItem = false;

        // En/Disable FileChooser deletebutton
        if ((inputFileChooser != null) && (inputFileChooser.getSelectedFiles() != null) && (inputFileChooser.getSelectedFiles().length > 0))
        {inputFileDeleteButton.setEnabled(true);} else {inputFileDeleteButton.setEnabled(false);}

        // En/Disable encryptButton        
        if ((inputFileChooser != null) && (inputFileChooser.getSelectedFiles() != null) && (inputFileChooser.getSelectedFiles().length > 0))
        {
            for (Path path:finalCrypt.getExtendedPathList(inputFileChooser.getSelectedFiles(), "*"))
            {
                if (Files.isRegularFile(path)) { hasEncryptableItem = true; }
            }
        }
        checkEncryptionReady();
    }//GEN-LAST:event_inputFileChooserPropertyChange

    private void inputFileChooserActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_inputFileChooserActionPerformed
    {//GEN-HEADEREND:event_inputFileChooserActionPerformed
        this.fileProgressBar.setValue(0);
        this.filesProgressBar.setValue(0);
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
        } else { encryptButton.setEnabled(false); }
    }//GEN-LAST:event_inputFileChooserActionPerformed

    private void cipherFileChooserActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cipherFileChooserActionPerformed
    {//GEN-HEADEREND:event_cipherFileChooserActionPerformed
        this.fileProgressBar.setValue(0);
        this.filesProgressBar.setValue(0);
        if ((cipherFileChooser != null)  && (cipherFileChooser.getSelectedFile() != null))
        {
            if ( cipherFileChooser.getSelectedFile().isFile() ) 
            {
                try { Desktop.getDesktop().open(cipherFileChooser.getSelectedFile()); }
                catch (IOException ex) { error("Error: Desktop.getDesktop().open(cipherFileChooser.getSelectedFile()); " + ex.getMessage() + "\n"); }
            }
        } else { encryptButton.setEnabled(false); }
    }//GEN-LAST:event_cipherFileChooserActionPerformed

    private void cipherFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_cipherFileChooserPropertyChange
    {//GEN-HEADEREND:event_cipherFileChooserPropertyChange
        this.fileProgressBar.setValue(0);
        this.filesProgressBar.setValue(0);
        
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
        if ((inputFileChooser != null) && (cipherFileChooser != null) && (inputFileChooser.getSelectedFiles() != null) && (cipherFileChooser.getSelectedFile() != null))
        {
            if (
                    (Files.isRegularFile(cipherFileChooser.getSelectedFile().toPath())) &&
                    (cipherFileChooser.getSelectedFile().length() > 0)
               )
            { 
                hasCipherItem = true; 
            } else { hasCipherItem = false; }
        }
        
        checkEncryptionReady();
    }//GEN-LAST:event_cipherFileChooserPropertyChange

    private void checkEncryptionReady() { if ( (hasEncryptableItem) && (hasCipherItem) ) { encryptButton.setEnabled(true); } else { encryptButton.setEnabled(false); }}

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
//                    TimerTask updateProgressTask = new TimerTask() { @Override public void run()
//                    {
                        ((JToggleButton)component).doClick();
//                    }};
//                    Timer updateProgressTaskTimer = new java.util.Timer(); updateProgressTaskTimer.schedule(updateProgressTask, 1500L);
                }
            }
            
            // Add Delete button
            if (component instanceof JButton)
            {
                if (((JButton) component).getActionCommand().equalsIgnoreCase("New Folder"))
                {
//                    component.getParent().add(this.inputFileDeleteButton);
//                    if (inputFileChooserContainer) { component.getParent().add(this.inputFileDeleteButton); } else { component.getParent().add(this.cipherFileDeleteButton); }
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
//                    TimerTask updateProgressTask = new TimerTask() { @Override public void run()
//                    {
                        ((JToggleButton)component).doClick();
//                    }};
//                    Timer updateProgressTaskTimer = new java.util.Timer(); updateProgressTaskTimer.schedule(updateProgressTask, 1500L);
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

    private void encryptButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_encryptButtonActionPerformed
    {//GEN-HEADEREND:event_encryptButtonActionPerformed
        // Needs Threading to early split off from the UI Event Dispatch Thread
        Thread encryptThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
//              Extend chooser.selectedfiles and add to inputFilesPath
                ArrayList<Path> inputFilesPathList = finalCrypt.getExtendedPathList(inputFileChooser.getSelectedFiles(), "*");

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

                fileProgressBar.setValue(0);
                filesProgressBar.setValue(0);

                encryptionStarted();
                finalCrypt.encryptSelection(inputFilesPathList, cipherFileChooser.getSelectedFile().toPath());
            }
        });
        encryptThread.setName("encryptThread");
        encryptThread.setDaemon(true);
        encryptThread.start();
    }//GEN-LAST:event_encryptButtonActionPerformed

    public void setProgressBarsMax(int filesMax, int fileMax)
    {        
//        filesProgressBar.setMaximum(finalCrypt.filesBytesTotal);
//        fileProgressBar.setMaximum(finalCrypt.fileBytesTotal);
        fileProgressBar.setMaximum(filesMax);
        filesProgressBar.setMaximum(fileMax);
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

    private void inputFileChooserKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputFileChooserKeyPressed
//        status(KeyEvent.getKeyText(evt.getKeyCode()));
//        if (evt.getKeyCode() == KeyEvent.VK_DELETE)
//        {
//            status(KeyEvent.getKeyText(evt.getKeyCode()));
//        }
//
//        if ((inputFileChooser != null)  && (inputFileChooser.getSelectedFiles() != null))
//        {
//            if ( inputFileChooser.getSelectedFiles().length > 0 ) 
//            {
//                for (File path:inputFileChooser.getSelectedFiles()) 
//                {
//                    try
//                    {
//                        if (Files.deleteIfExists(path.toPath()))
//                        {
//                            status("File: " + path.getName() + " deleted");
//                        }
//                        else
//                        {
//                            status("File: " + path.getName() + " not deleted");
//                        }
//                    } catch (IOException ex) { error("Error: Files.delete(path.toPath()); " + ex.getMessage() + "\n"); }
//                }
//            }
//        }
    }//GEN-LAST:event_inputFileChooserKeyPressed

    private void logButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logButtonMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3)
        {
            printButton.setVisible(!printButton.isVisible());
            textButton.setVisible(!textButton.isVisible());
            binButton.setVisible(!binButton.isVisible());
            decButton.setVisible(!decButton.isVisible());
            hexButton.setVisible(!hexButton.isVisible());
            charButton.setVisible(!charButton.isVisible());
        }
    }//GEN-LAST:event_logButtonMouseClicked

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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton binButton;
    private javax.swing.JPanel bottomPanel;
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
    private javax.swing.JToggleButton printButton;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JTabbedPane tab;
    private javax.swing.JToggleButton textButton;
    private javax.swing.JToggleButton verboseButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void log(final String message)
    {
//        SwingUtilities.invokeLater(new Runnable()
//        {
//            public void run()
//            {
                logTextArea.append(message);
                logScroller.getVerticalScrollBar().setValue(logScroller.getVerticalScrollBar().getMaximum());
//            }
//        });
    }

    @Override
    public void error(final String message)
    {
//        SwingUtilities.invokeLater(new Runnable()
//        {
//            public void run()
//            {
                status(message);
//            }
//        });
    }

    @Override
    synchronized public void status(final String status)
    {
//        SwingUtilities.invokeLater(new Runnable()
//        {
//            public void run()
//            {
                statusLabel.setText(status);
                log(status);
//            }
//        });
    }
        
    @Override
    public void println(String message)
    {
//        SwingUtilities.invokeLater(new Runnable()
//        {
//            public void run()
//            {
                System.out.println(message);
//            }
//        });
    }

    @Override
    public void encryptionStarted()
    {
//        SwingUtilities.invokeLater(new Runnable()
//        {
//            public void run()
//            {
                encryptButton.setEnabled(false);
                fileProgressBar.setValue(0);
                filesProgressBar.setValue(0);
                inputFileChooser.rescanCurrentDirectory();
                cipherFileChooser.rescanCurrentDirectory();
//            }
//        });
    }

    @Override
    synchronized public void encryptionGraph(final int value)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
            }
        });
    }

    // Threaded version of FinalCrypt
    @Override
    public void encryptionProgress(final int fileProgressPercent, final int filesProgressPercent)
    {
//        SwingUtilities.invokeLater(new Runnable()
//        {
//            public void run()
//            {
                if (finalCrypt.getDebug()) { println("Progress File : " + fileProgressPercent + "%\n"); }
                if (finalCrypt.getDebug()) { println("Progress Files: " + filesProgressPercent + "%\n"); }
                fileProgressBar.setValue(fileProgressPercent);
                filesProgressBar.setValue(filesProgressPercent);
//            }
//        });
    }

    @Override
    synchronized public void encryptionFinished()
    {
//        SwingUtilities.invokeLater(new Runnable()
//        {
//            public void run()
//            {
                encryptButton.setEnabled(true);
                if ((finalCrypt.getDebug()) && (finalCrypt.getStats().getFileBytesTotal() != 0))   { println("Progress File : " +  (int)(finalCrypt.getStats().getFileBytesEncrypted()  / (finalCrypt.getStats().getFileBytesTotal()  / 100.0)) + "%"); }
                if ((finalCrypt.getDebug()) && (finalCrypt.getStats().getFilesBytesTotal() != 0))  { println("Progress Files: " +  (int)(finalCrypt.getStats().getFilesBytesEncrypted() / (finalCrypt.getStats().getFilesBytesTotal() / 100.0)) + "%"); }
                if ((finalCrypt.getDebug()) && (finalCrypt.getStats().getFileBytesTotal() != 0))   { log("Progress File : " +      (int)(finalCrypt.getStats().getFileBytesEncrypted()  / (finalCrypt.getStats().getFileBytesTotal()  / 100.0)) + "%\n"); }
                if ((finalCrypt.getDebug()) && (finalCrypt.getStats().getFilesBytesTotal() != 0))  { log("Progress Files: " +      (int)(finalCrypt.getStats().getFilesBytesEncrypted() / (finalCrypt.getStats().getFilesBytesTotal() / 100.0)) + "%\n"); }
                
                if (finalCrypt.getStats().getFileBytesTotal() != 0)    fileProgressBar.setValue(                                   (int)(finalCrypt.getStats().getFileBytesEncrypted() /  (finalCrypt.getStats().getFileBytesTotal() / 100.0)));
                if (finalCrypt.getStats().getFilesBytesTotal() != 0)   filesProgressBar.setValue(                                  (int)(finalCrypt.getStats().getFilesBytesEncrypted() / (finalCrypt.getStats().getFilesBytesTotal()  / 100.0)));
                inputFileChooser.rescanCurrentDirectory();  inputFileChooser.validate();
                cipherFileChooser.rescanCurrentDirectory(); cipherFileChooser.validate();
//            }
//        });
    }
}
