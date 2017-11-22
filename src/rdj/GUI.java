package rdj;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GUI extends javax.swing.JFrame implements UI
{
    File[] eFiles;
    File file;

    public GUI()
    {
        initComponents();
        try
        { UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); }
        catch (ClassNotFoundException ex) { }
        catch (InstantiationException ex) { }
        catch (IllegalAccessException ex) { }
        catch (UnsupportedLookAndFeelException ex) { }
        disableSomeComponents(inputFileChooser);
        disableSomeComponents(cipherFileChooser);
        eFiles = new File[1]; eFiles[0] = new File("rew");
        file = new File("qwe");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        logoLabel = new javax.swing.JLabel();
        encryptTab = new javax.swing.JTabbedPane();
        encryptPanel = new javax.swing.JPanel();
        inputFilePane = new javax.swing.JPanel();
        inputFileChooserLabel = new javax.swing.JLabel();
        inputFileChooser = new javax.swing.JFileChooser();
        cipherFilePane = new javax.swing.JPanel();
        cipherFileChooserLabel = new javax.swing.JLabel();
        cipherFileChooser = new javax.swing.JFileChooser();
        logPane = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        bottomPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        progressPanel = new javax.swing.JPanel();
        FilesProgressBar = new javax.swing.JProgressBar();
        FileProgressBar = new javax.swing.JProgressBar();
        statusLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        logoLabel.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        logoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoLabel.setText("FinalCrypt");
        logoLabel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        encryptTab.setBackground(new java.awt.Color(0, 0, 0));
        encryptTab.setMinimumSize(new java.awt.Dimension(1000, 29));
        encryptTab.setPreferredSize(new java.awt.Dimension(1000, 100));

        encryptPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        encryptPanel.setPreferredSize(new java.awt.Dimension(1000, 524));
        encryptPanel.setLayout(new java.awt.GridLayout());

        inputFilePane.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        inputFileChooserLabel.setFont(new java.awt.Font("Open Sans", 0, 18)); // NOI18N
        inputFileChooserLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        inputFileChooserLabel.setText("Select Data Files");
        inputFileChooserLabel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        inputFileChooser.setControlButtonsAreShown(false);
        inputFileChooser.setMultiSelectionEnabled(true);
        inputFileChooser.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                inputFileChooserActionPerformed(evt);
            }
        });
        inputFileChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener()
        {
            public void propertyChange(java.beans.PropertyChangeEvent evt)
            {
                inputFileChooserPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout inputFilePaneLayout = new javax.swing.GroupLayout(inputFilePane);
        inputFilePane.setLayout(inputFilePaneLayout);
        inputFilePaneLayout.setHorizontalGroup(
            inputFilePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 507, Short.MAX_VALUE)
            .addGroup(inputFilePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(inputFilePaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(inputFilePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(inputFileChooserLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(inputFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE))
                    .addContainerGap()))
        );
        inputFilePaneLayout.setVerticalGroup(
            inputFilePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 456, Short.MAX_VALUE)
            .addGroup(inputFilePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(inputFilePaneLayout.createSequentialGroup()
                    .addComponent(inputFileChooserLabel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(inputFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        encryptPanel.add(inputFilePane);

        cipherFilePane.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cipherFileChooserLabel.setFont(new java.awt.Font("Open Sans", 0, 18)); // NOI18N
        cipherFileChooserLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cipherFileChooserLabel.setText("Select Cipher File");
        cipherFileChooserLabel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        cipherFileChooser.setControlButtonsAreShown(false);
        cipherFileChooser.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cipherFileChooserActionPerformed(evt);
            }
        });
        cipherFileChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener()
        {
            public void propertyChange(java.beans.PropertyChangeEvent evt)
            {
                cipherFileChooserPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout cipherFilePaneLayout = new javax.swing.GroupLayout(cipherFilePane);
        cipherFilePane.setLayout(cipherFilePaneLayout);
        cipherFilePaneLayout.setHorizontalGroup(
            cipherFilePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 507, Short.MAX_VALUE)
            .addGroup(cipherFilePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(cipherFilePaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(cipherFilePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cipherFileChooserLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cipherFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE))
                    .addContainerGap()))
        );
        cipherFilePaneLayout.setVerticalGroup(
            cipherFilePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 456, Short.MAX_VALUE)
            .addGroup(cipherFilePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(cipherFilePaneLayout.createSequentialGroup()
                    .addComponent(cipherFileChooserLabel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cipherFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        encryptPanel.add(cipherFilePane);

        encryptTab.addTab("Encrypt", encryptPanel);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout logPaneLayout = new javax.swing.GroupLayout(logPane);
        logPane.setLayout(logPaneLayout);
        logPaneLayout.setHorizontalGroup(
            logPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, logPaneLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1021, Short.MAX_VALUE)
                .addContainerGap())
        );
        logPaneLayout.setVerticalGroup(
            logPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
        );

        encryptTab.addTab("Log", logPane);

        bottomPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonPanel.setLayout(new java.awt.GridLayout(1, 3));

        jButton1.setFont(new java.awt.Font("Arimo", 0, 18)); // NOI18N
        jButton1.setText("Encrypt");
        buttonPanel.add(jButton1);

        jButton2.setFont(new java.awt.Font("Arimo", 0, 18)); // NOI18N
        jButton2.setText("Pause");
        buttonPanel.add(jButton2);

        jButton3.setFont(new java.awt.Font("Arimo", 0, 18)); // NOI18N
        jButton3.setText("Stop");
        buttonPanel.add(jButton3);

        progressPanel.setLayout(new java.awt.GridLayout(3, 0));
        progressPanel.add(FilesProgressBar);
        progressPanel.add(FileProgressBar);

        statusLabel.setBackground(new java.awt.Color(255, 0, 0));
        statusLabel.setForeground(new java.awt.Color(50, 50, 50));
        statusLabel.setText("Status");
        statusLabel.setBorder(null);
        progressPanel.add(statusLabel);

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(bottomPanelLayout.createSequentialGroup()
                    .addGap(3, 3, 3)
                    .addComponent(progressPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(3, 3, 3)))
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(82, Short.MAX_VALUE))
            .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                    .addContainerGap(64, Short.MAX_VALUE)
                    .addComponent(progressPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(encryptTab, javax.swing.GroupLayout.DEFAULT_SIZE, 1027, Short.MAX_VALUE)
                        .addGap(6, 6, 6))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(encryptTab, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void inputFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_inputFileChooserPropertyChange
    {//GEN-HEADEREND:event_inputFileChooserPropertyChange
        File[] files = inputFileChooser.getSelectedFiles();
//        String str = new String("\n");
//        for (File file:files) { str += file.getName() + "\n";}
//        textArea.setText("PROP        " + evt.getPropertyName() + " OLD " + evt.getOldValue() + " NEW " + evt.getNewValue() + " length " + files.length + " files: " + str + "\n");
    }//GEN-LAST:event_inputFileChooserPropertyChange

    private void inputFileChooserActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_inputFileChooserActionPerformed
    {//GEN-HEADEREND:event_inputFileChooserActionPerformed
//        textArea.setText("cmd        " + evt.getActionCommand() + "par " + evt.paramString() + "\n");
    }//GEN-LAST:event_inputFileChooserActionPerformed

    private void cipherFileChooserActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cipherFileChooserActionPerformed
    {//GEN-HEADEREND:event_cipherFileChooserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cipherFileChooserActionPerformed

    private void cipherFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_cipherFileChooserPropertyChange
    {//GEN-HEADEREND:event_cipherFileChooserPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_cipherFileChooserPropertyChange

    /**
     * @param args the command line arguments
     */
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
    
    public boolean disableSomeComponents(Container c)
    {
    
        Component[] cmps = c.getComponents();
        for (Component cmp : cmps)
        {
            if (cmp instanceof JTextField)
            {
                ((JTextField)cmp).setEnabled(false);
                ((JTextField)cmp).setVisible(false);                
//                return true;
            }
            if (cmp instanceof JComboBox)
            {
                if ( ((JComboBox)cmp).getSelectedItem().toString().toLowerCase().contains("BasicFileChooserUI".toLowerCase()) )
                {
                    ((JComboBox)cmp).setEnabled(false);
                    ((JComboBox)cmp).setVisible(false);                
                }
//                return true;
            }
            if (cmp instanceof JLabel)
            {
                ((JLabel)cmp).setEnabled(false);
                ((JLabel)cmp).setVisible(false);                
//                return true;
            }
            if (cmp instanceof JRadioButton)
            {
                if (   ! ((JRadioButton)cmp).isSelected()   )  { ((JRadioButton)cmp).setSelected(true); }
            }
            if (cmp instanceof Container)
            {
                if(disableSomeComponents((Container) cmp)) return true;
            }
        }
        return false;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar FileProgressBar;
    private javax.swing.JProgressBar FilesProgressBar;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JFileChooser cipherFileChooser;
    private javax.swing.JLabel cipherFileChooserLabel;
    private javax.swing.JPanel cipherFilePane;
    private javax.swing.JPanel encryptPanel;
    private javax.swing.JTabbedPane encryptTab;
    private javax.swing.JFileChooser inputFileChooser;
    private javax.swing.JLabel inputFileChooserLabel;
    private javax.swing.JPanel inputFilePane;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel logPane;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void log(String message)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void status(String status)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateEncryptionDiffStats(int value)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateTotalProgress(int value)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateFileProgress(int value)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBufferProgress(int value)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
