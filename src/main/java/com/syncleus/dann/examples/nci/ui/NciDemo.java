/******************************************************************************
 *                                                                             *
 *  Copyright: (c) Syncleus, Inc.                                              *
 *                                                                             *
 *  You may redistribute and modify this source code under the terms and       *
 *  conditions of the Open Source Community License - Type C version 1.0       *
 *  or any later version as published by Syncleus, Inc. at www.syncleus.com.   *
 *  There should be a copy of the license included with this file. If a copy   *
 *  of the license is not included you are granted no right to distribute or   *
 *  otherwise use this file except through a legal and valid license. You      *
 *  should also contact Syncleus, Inc. at the information below if you cannot  *
 *  find a license:                                                            *
 *                                                                             *
 *  Syncleus, Inc.                                                             *
 *  2604 South 12th Street                                                     *
 *  Philadelphia, PA 19148                                                     *
 *                                                                             *
 ******************************************************************************/
package com.syncleus.dann.examples.nci.ui;

import com.syncleus.dann.ComponentUnavailableException;
import com.syncleus.dann.examples.nci.*;
import com.syncleus.dann.graph.drawing.hyperassociativemap.visualization.HyperassociativeMapCanvas;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class NciDemo extends JFrame implements ActionListener, BrainListener {
    private static final Logger LOGGER = Logger.getLogger(NciDemo.class);
    private static final int BLOCK_WIDTH = 7;
    private static final int BLOCK_HEIGHT = 7;
    private final ImagePanel originalImagePanel = new ImagePanel();
    private final ImagePanel finalImagePanel = new ImagePanel();
    private BrainRunner brainRunner;
    private HyperassociativeMapCanvas brainVisual;
    private Component errorPanel;
    private Thread brainRunnerThread;
    private File trainingDirectory;
    private File originalImageLocation;
    private BufferedImage originalImage;
    private BufferedImage finalImage;
    private boolean processing = false;
    private int trainingRemaining;
    private int currentTrainingCycles = 100000;
    private ViewBrain viewBrain;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem1;
    private javax.swing.JMenuItem brainViewMenu;
    private javax.swing.JMenu fileMenu1;
    // </editor-fold>
    private javax.swing.JMenu helpMenu1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JButton originalImageSelect;
    private javax.swing.JTextField originalImageText;
    private javax.swing.JButton processButton;
    private javax.swing.JProgressBar progress;
    private javax.swing.JMenuItem quitMenuItem1;
    private javax.swing.JSeparator separator;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JButton stopButton;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JButton trainButton;
    private javax.swing.JSpinner trainingCylcesInput;
    private javax.swing.JButton trainingDirectorySelect;
    private javax.swing.JTextField trainingDirectoryText;

    public NciDemo() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception caught) {
            LOGGER.warn("Could not set the UI to native look and feel", caught);
        }

        this.initComponents();

        this.add(this.originalImagePanel);
        final int currentX = this.separator.getX() + 5;
        int currentY = 0;
        this.originalImagePanel.setLocation(currentX, currentY);
        this.originalImagePanel.setSize(800, 400);
        currentY = 400;
        this.originalImagePanel.setVisible(true);

        this.add(this.finalImagePanel);
        this.finalImagePanel.setLocation(currentX, currentY);
        this.finalImagePanel.setSize(800, 600);
        this.finalImagePanel.setVisible(true);

        this.setSize(600, 350);
        this.setExtendedState(MAXIMIZED_BOTH);

        new Timer(250, this).start();
    }

    public static void main(final String[] args) {
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    try {
                        new NciDemo().setVisible(true);
                    }
                    catch (Exception caught) {
                        LOGGER.error("Exception was caught", caught);
                        throw new RuntimeException("Throwable was caught", caught);
                    }
                    catch (Error caught) {
                        LOGGER.error("Error was caught", caught);
                        throw new Error("Throwable was caught", caught);
                    }
                }
            });
        }
        catch (Exception caught) {
            LOGGER.error("Exception was caught", caught);
            throw new RuntimeException("Throwable was caught", caught);
        }
        catch (Error caught) {
            LOGGER.error("Error was caught", caught);
            throw new Error("Throwable was caught", caught);
        }
    }

    @Override
    public void actionPerformed(final ActionEvent evt) {

        if (this.trainingRemaining > 0) {
            this.trainingRemaining = this.brainRunner.getTrainingCycles();
            final int progressPercent = ((this.currentTrainingCycles - this.trainingRemaining) * 100) / (this.currentTrainingCycles);
            this.progress.setValue(progressPercent);
        }
        else if (this.processing) {
            this.progress.setValue(this.brainRunner.getSampleProgress());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        this.trainingDirectoryText = new javax.swing.JTextField();
        this.trainingDirectorySelect = new javax.swing.JButton();
        this.jLabel1 = new javax.swing.JLabel();
        this.jLabel2 = new javax.swing.JLabel();
        this.originalImageSelect = new javax.swing.JButton();
        this.originalImageText = new javax.swing.JTextField();
        this.trainButton = new javax.swing.JButton();
        this.trainingCylcesInput = new javax.swing.JSpinner();
        this.processButton = new javax.swing.JButton();
        this.jLabel5 = new javax.swing.JLabel();
        this.separator = new javax.swing.JSeparator();
        this.statusLabel = new javax.swing.JLabel();
        this.progress = new javax.swing.JProgressBar();
        this.stopButton = new javax.swing.JButton();
        this.jMenuBar2 = new javax.swing.JMenuBar();
        this.fileMenu1 = new javax.swing.JMenu();
        this.quitMenuItem1 = new javax.swing.JMenuItem();
        this.toolsMenu = new javax.swing.JMenu();
        this.brainViewMenu = new javax.swing.JMenuItem();
        this.helpMenu1 = new javax.swing.JMenu();
        this.aboutMenuItem1 = new javax.swing.JMenuItem();

        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("NCI Demo");
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        this.trainingDirectoryText.setEditable(false);

        this.trainingDirectorySelect.setText("...");
        this.trainingDirectorySelect.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NciDemo.this.trainingDirectorySelectActionPerformed(evt);
            }
        });

        this.jLabel1.setText("Training Images");

        this.jLabel2.setText("Original Image(s)");

        this.originalImageSelect.setText("...");
        this.originalImageSelect.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NciDemo.this.originalImageSelectActionPerformed(evt);
            }
        });

        this.originalImageText.setEditable(false);

        this.trainButton.setText("Train");
        this.trainButton.setEnabled(false);
        this.trainButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NciDemo.this.trainButtonActionPerformed(evt);
            }
        });

        this.trainingCylcesInput.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(10000), Integer.valueOf(1), null, Integer.valueOf(1000)));

        this.processButton.setText("Process");
        this.processButton.setEnabled(false);
        this.processButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NciDemo.this.processButtonActionPerformed(evt);
            }
        });

        this.jLabel5.setText("Training Cycles");

        this.separator.setOrientation(javax.swing.SwingConstants.VERTICAL);

        this.statusLabel.setText("Please make selections!");

        this.progress.setStringPainted(true);

        this.stopButton.setText("Stop");
        this.stopButton.setEnabled(false);
        this.stopButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NciDemo.this.stopButtonActionPerformed(evt);
            }
        });

        this.fileMenu1.setText("File");

        this.quitMenuItem1.setText("Quit");
        this.quitMenuItem1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                NciDemo.this.quitMenuItemMouseReleased(evt);
            }
        });
        this.quitMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NciDemo.this.quitMenuItem1ActionPerformed(evt);
            }
        });
        this.quitMenuItem1.addMenuKeyListener(new javax.swing.event.MenuKeyListener() {
            @Override
            public void menuKeyPressed(javax.swing.event.MenuKeyEvent evt) {
                NciDemo.this.quitMenuItemMenuKeyPressed(evt);
            }

            @Override
            public void menuKeyReleased(javax.swing.event.MenuKeyEvent evt) {
            }

            @Override
            public void menuKeyTyped(javax.swing.event.MenuKeyEvent evt) {
            }
        });
        this.fileMenu1.add(this.quitMenuItem1);

        this.jMenuBar2.add(this.fileMenu1);

        this.toolsMenu.setText("Tools");

        this.brainViewMenu.setText("3D Brain View");
        this.brainViewMenu.setEnabled(false);
        this.brainViewMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                NciDemo.this.brainViewMenuMouseReleased(evt);
            }
        });
        this.brainViewMenu.addMenuKeyListener(new javax.swing.event.MenuKeyListener() {
            @Override
            public void menuKeyPressed(javax.swing.event.MenuKeyEvent evt) {
                NciDemo.this.brainViewMenuMenuKeyPressed(evt);
            }

            @Override
            public void menuKeyReleased(javax.swing.event.MenuKeyEvent evt) {
            }

            @Override
            public void menuKeyTyped(javax.swing.event.MenuKeyEvent evt) {
            }
        });
        this.toolsMenu.add(this.brainViewMenu);

        this.jMenuBar2.add(this.toolsMenu);

        this.helpMenu1.setText("Help");

        this.aboutMenuItem1.setText("About");
        this.aboutMenuItem1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                NciDemo.this.aboutMenuItemMouseReleased(evt);
            }
        });
        this.aboutMenuItem1.addMenuKeyListener(new javax.swing.event.MenuKeyListener() {
            @Override
            public void menuKeyPressed(javax.swing.event.MenuKeyEvent evt) {
                NciDemo.this.aboutMenuItemMenuKeyPressed(evt);
            }

            @Override
            public void menuKeyReleased(javax.swing.event.MenuKeyEvent evt) {
            }

            @Override
            public void menuKeyTyped(javax.swing.event.MenuKeyEvent evt) {
            }
        });
        this.helpMenu1.add(this.aboutMenuItem1);

        this.jMenuBar2.add(this.helpMenu1);

        this.setJMenuBar(this.jMenuBar2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                                         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                 .addGroup(layout.createSequentialGroup()
                                                                   .addContainerGap()
                                                                   .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                     .addComponent(this.jLabel2)
                                                                                     .addComponent(this.jLabel1)
                                                                                     .addComponent(this.statusLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                                                                                     .addGroup(layout.createSequentialGroup()
                                                                                                       .addGap(27, 27, 27)
                                                                                                       .addComponent(this.jLabel5)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.trainingCylcesInput, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                     .addGroup(layout.createSequentialGroup()
                                                                                                       .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                                                                         .addComponent(this.trainingDirectoryText, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                         .addComponent(this.originalImageText, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE))
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                         .addComponent(this.originalImageSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                         .addComponent(this.trainingDirectorySelect, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                                     .addGroup(layout.createSequentialGroup()
                                                                                                       .addGap(12, 12, 12)
                                                                                                       .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                                                                         .addComponent(this.progress, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                                         .addGroup(layout.createSequentialGroup()
                                                                                                                                           .addComponent(this.trainButton)
                                                                                                                                           .addGap(18, 18, 18)
                                                                                                                                           .addComponent(this.processButton)
                                                                                                                                           .addGap(18, 18, 18)
                                                                                                                                           .addComponent(this.stopButton)))))
                                                                   .addGap(18, 18, 18)
                                                                   .addComponent(this.separator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                   .addGap(10000, 10000, 10000))
        );
        layout.setVerticalGroup(
                                       layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                               .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                                             .addContainerGap()
                                                                                                             .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                                                               .addComponent(this.separator, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
                                                                                                                               .addGroup(layout.createSequentialGroup()
                                                                                                                                                 .addComponent(this.jLabel1)
                                                                                                                                                 .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                                                 .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                                                                                   .addComponent(this.trainingDirectoryText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                   .addComponent(this.trainingDirectorySelect))
                                                                                                                                                 .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                                                 .addComponent(this.jLabel2)
                                                                                                                                                 .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                                                 .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                                                                                   .addComponent(this.originalImageText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                   .addComponent(this.originalImageSelect))
                                                                                                                                                 .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 470, Short.MAX_VALUE)
                                                                                                                                                 .addComponent(this.statusLabel)
                                                                                                                                                 .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                                                 .addComponent(this.progress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                                 .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                                                 .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                                                                                   .addComponent(this.jLabel5)
                                                                                                                                                                   .addComponent(this.trainingCylcesInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                                                 .addGap(26, 26, 26)
                                                                                                                                                 .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                                                                                   .addComponent(this.processButton)
                                                                                                                                                                   .addComponent(this.stopButton)
                                                                                                                                                                   .addComponent(this.trainButton))))
                                                                                                             .addContainerGap())
        );

        this.pack();
    }// </editor-fold>//GEN-END:initComponents

    private void quitMenuItemMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_quitMenuItemMouseReleased
        if (this.brainRunner != null) {
            this.brainRunner.shutdown();
        }
        System.exit(0);
    }//GEN-LAST:event_quitMenuItemMouseReleased

    private void quitMenuItemMenuKeyPressed(javax.swing.event.MenuKeyEvent evt) {//GEN-FIRST:event_quitMenuItemMenuKeyPressed
        if (this.brainRunner != null) {
            this.brainRunner.shutdown();
        }
        System.exit(0);
    }//GEN-LAST:event_quitMenuItemMenuKeyPressed

    private void aboutMenuItemMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_aboutMenuItemMouseReleased
        this.displayAbout();
    }//GEN-LAST:event_aboutMenuItemMouseReleased

    private void aboutMenuItemMenuKeyPressed(javax.swing.event.MenuKeyEvent evt) {//GEN-FIRST:event_aboutMenuItemMenuKeyPressed
        this.displayAbout();
    }//GEN-LAST:event_aboutMenuItemMenuKeyPressed

    private void originalImageSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_originalImageSelectActionPerformed
        final JFileChooser chooser = new JFileChooser();
        final FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setVisible(true);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            this.originalImageText.setText(chooser.getSelectedFile().getAbsolutePath());
            this.originalImageLocation = chooser.getSelectedFile();

            this.refreshOriginalImage();

            if (this.brainRunnerThread != null) {
                this.processButton.setEnabled(true);
                this.trainButton.setEnabled(true);

                this.statusLabel.setText("Ready!");
            }
        }
    }//GEN-LAST:event_originalImageSelectActionPerformed

    private void trainingDirectorySelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trainingDirectorySelectActionPerformed
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setVisible(true);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            this.trainingDirectoryText.setText(chooser.getSelectedFile().getAbsolutePath());
            this.trainingDirectory = chooser.getSelectedFile();

            final File[] trainingFiles = this.trainingDirectory.listFiles(new PngFileFilter());
            if (trainingFiles.length <= 0) {
                this.trainingDirectory = null;
                this.trainingDirectoryText.setText("");

                JOptionPane.showMessageDialog(this, "The selected training directory does not contain *.png files! Select a new directory", "Invalid Training Directory", JOptionPane.ERROR_MESSAGE);

                return;
            }

            this.brainRunner = new BrainRunner(this, trainingFiles, 0.875, BLOCK_WIDTH, BLOCK_HEIGHT, true);
            this.brainRunnerThread = new Thread(this.brainRunner);
            this.brainRunnerThread.start();

            if (this.originalImageLocation != null) {
                this.processButton.setEnabled(true);
                this.trainButton.setEnabled(true);

                this.statusLabel.setText("Ready!");
            }
        }
    }//GEN-LAST:event_trainingDirectorySelectActionPerformed

    private void trainButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trainButtonActionPerformed
        this.trainButton.setEnabled(false);
        this.processButton.setEnabled(false);
        this.stopButton.setEnabled(true);

        this.statusLabel.setText("Please wait, training...");

        this.currentTrainingCycles = ((Integer) this.trainingCylcesInput.getValue()).intValue();
        this.trainingRemaining = this.currentTrainingCycles;

        this.brainRunner.setTrainingCycles(this.currentTrainingCycles);
    }//GEN-LAST:event_trainButtonActionPerformed

    private void processButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processButtonActionPerformed
        if (this.processing || (this.finalImage == null) || (this.originalImage == null)) {
            return;
        }

        this.processButton.setEnabled(false);
        this.trainButton.setEnabled(false);
        this.stopButton.setEnabled(false);

        this.processing = true;

        this.statusLabel.setText("Please wait, processing...");

        this.brainRunner.setSampleImage(this.originalImageLocation);
    }//GEN-LAST:event_processButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        this.brainRunner.stop();
    }//GEN-LAST:event_stopButtonActionPerformed

    public void showBrainView() {
        if (this.brainVisual == null) {
            final JDialog errorDialog = new JDialog();
            errorDialog.add(this.errorPanel);
            errorDialog.setVisible(true);
        }
        else {
            this.brainVisual.refresh();

            if (this.viewBrain == null) {
                this.viewBrain = new ViewBrain(this, this.brainVisual);
            }

            this.viewBrain.setVisible(true);
        }
    }

    private void brainViewMenuMenuKeyPressed(javax.swing.event.MenuKeyEvent evt) {//GEN-FIRST:event_brainViewMenuMenuKeyPressed
        this.showBrainView();
    }//GEN-LAST:event_brainViewMenuMenuKeyPressed

    private void brainViewMenuMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_brainViewMenuMouseReleased
        this.showBrainView();
    }//GEN-LAST:event_brainViewMenuMouseReleased

    private void quitMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMenuItem1ActionPerformed
        if (this.brainRunner != null) {
            this.brainRunner.shutdown();
        }
        System.exit(0);
    }//GEN-LAST:event_quitMenuItem1ActionPerformed

    private void refreshOriginalImage() {
        if (this.originalImageLocation == null) {
            return;
        }

        try {
            this.originalImage = ImageIO.read(this.originalImageLocation);
        }
        catch (IOException caught) {
            LOGGER.warn("IO Exception when reading image", caught);
            return;
        }
        this.originalImagePanel.setImage(this.originalImage);
        this.finalImage = new BufferedImage(this.originalImage.getWidth(), this.originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
    }

    private void displayAbout() {
        final AboutDialog about = new AboutDialog(this, true);
        about.setVisible(true);
    }

    @Override
    public void brainFinishedBuffering() {
        try {
            this.brainVisual = new HyperassociativeMapCanvas(this.brainRunner.getBrainMap());
            this.brainViewMenu.setEnabled(true);
        }
        catch (ComponentUnavailableException exc) {
            this.brainVisual = null;
            this.errorPanel = exc.newPanel();
            this.brainViewMenu.setEnabled(false);

        }
    }

    @Override
    public void brainSampleProcessed(final BufferedImage finalImage) {
        this.processing = false;
        this.progress.setValue(100);
        this.finalImage = finalImage;
        this.finalImagePanel.setImage(this.finalImage);
        this.finalImagePanel.repaint();

        this.processButton.setEnabled(true);
        this.trainButton.setEnabled(true);
        this.stopButton.setEnabled(false);

        this.statusLabel.setText("Ready!");
    }

    @Override
    public void brainTrainingComplete() {
        this.trainingRemaining = 0;
        this.progress.setValue(100);

        this.processButton.setEnabled(true);
        this.trainButton.setEnabled(true);
        this.stopButton.setEnabled(false);

        this.statusLabel.setText("Ready!");
    }
    // End of variables declaration//GEN-END:variables
}
