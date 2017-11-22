package rdj;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class GUI extends javax.swing.JFrame
{
    File[] eFiles;
    File file;

    public GUI()
    {
        initComponents();
        disableSomeComponents(inputFileChooser);
        disableSomeComponents(cipherFileChooser);
        eFiles = new File[1]; eFiles[0] = new File("rew");
        file = new File("qwe");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jLabel1 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        inputFileChooser = new javax.swing.JFileChooser();
        cipherFileChooser = new javax.swing.JFileChooser();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("FinalCrypt");

        jProgressBar1.setFocusable(false);
        jProgressBar1.setStringPainted(true);

        jLabel3.setFont(new java.awt.Font("Open Sans", 0, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Select Data Files");
        jLabel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setFont(new java.awt.Font("Open Sans", 0, 18)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Select Cipher File");
        jLabel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        cipherFileChooser.setControlButtonsAreShown(false);
        cipherFileChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener()
        {
            public void propertyChange(java.beans.PropertyChangeEvent evt)
            {
                cipherFileChooserPropertyChange(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Laksaman", 0, 24)); // NOI18N
        jButton1.setText("Encrypt");
        jButton1.setEnabled(false);

        jButton2.setFont(new java.awt.Font("Laksaman", 0, 24)); // NOI18N
        jButton2.setText("Stop");
        jButton2.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inputFileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 507, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(189, 189, 189)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 197, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(191, 191, 191))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cipherFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inputFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                    .addComponent(cipherFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 507, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void cipherFileChooserPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_cipherFileChooserPropertyChange
    {//GEN-HEADEREND:event_cipherFileChooserPropertyChange
        
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
            if (cmp instanceof Container)
            {
                if(disableSomeComponents((Container) cmp)) return true;
            }
        }
        return false;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser cipherFileChooser;
    private javax.swing.JFileChooser inputFileChooser;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    // End of variables declaration//GEN-END:variables

}


//class JavaFileView extends FileView {
//  Icon javaIcon = new MyIcon(Color.BLUE);
//
//  Icon classIcon = new MyIcon(Color.GREEN);
//
//  Icon htmlIcon = new MyIcon(Color.RED);
//
//  Icon jarIcon = new MyIcon(Color.PINK);
//
//  public String getName(File file) {
//    String filename = file.getName();
//    if (filename.endsWith(".java")) {
//      String name = filename + " : " + file.length();
//      return name;
//    }
//    return null;
//  }
//
//  public String getTypeDescription(File file) {
//    String typeDescription = null;
//    String filename = file.getName().toLowerCase();
//
//    if (filename.endsWith(".java")) {
//      typeDescription = "Java Source";
//    } else if (filename.endsWith(".class")) {
//      typeDescription = "Java Class File";
//    } else if (filename.endsWith(".jar")) {
//      typeDescription = "Java Archive";
//    } else if (filename.endsWith(".html") || filename.endsWith(".htm")) {
//      typeDescription = "Applet Loader";
//    }
//    return typeDescription;
//  }
//
//  public Icon getIcon(File file) {
//    if (file.isDirectory()) {
//      return null;
//    }
//    Icon icon = null;
//    String filename = file.getName().toLowerCase();
//    if (filename.endsWith(".java")) {
//      icon = javaIcon;
//    } else if (filename.endsWith(".class")) {
//      icon = classIcon;
//    } else if (filename.endsWith(".jar")) {
//      icon = jarIcon;
//    } else if (filename.endsWith(".html") || filename.endsWith(".htm")) {
//      icon = htmlIcon;
//    }
//    return icon;
//  }
//}
//
//class MyIcon implements Icon {
//  Color myColor;
//
//  public MyIcon(Color myColor) {
//    this.myColor = myColor;
//  }
//
//  public int getIconWidth() {
//    return 16;
//  }
//
//  public int getIconHeight() {
//    return 16;
//  }
//
//  public void paintIcon(Component c, Graphics g, int x, int y) {
//    g.setColor(myColor);
//    g.drawRect(0, 0, 16, 16);
//  }
//
////    @Override
////    public void paintIcon(Component c, Graphics g, int x, int y)
////    {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
////    }
//}
