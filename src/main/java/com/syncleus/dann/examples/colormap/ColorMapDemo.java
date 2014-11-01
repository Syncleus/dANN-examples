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
package com.syncleus.dann.examples.colormap;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;

public class ColorMapDemo extends JFrame implements ActionListener {
    private static final Logger LOGGER = Logger.getLogger(ColorMapDemo.class);
    private static final int INITIAL_ITERATIONS = 200;
    private static final long serialVersionUID = -409985159806625127L;
    private final SpinnerNumberModel iterationsModel = new SpinnerNumberModel(INITIAL_ITERATIONS, 1, 10000, 100);
    private static final double INITIAL_LEARNING_RATE = 0.5;
    private final SpinnerNumberModel learningRateModel = new SpinnerNumberModel(INITIAL_LEARNING_RATE, Double.MIN_VALUE, 1.0, 0.01);
    private final ExecutorService executor;
    private final Timer progressTimer = new Timer(100, this);
    private Color[] color1d;
    private Color[][] color2d;
    private Future<Color[]> future1d;
    private Future<Color[][]> future2d;
    private ColorMap1dCallable callable1d;
    private ColorMap2dCallable callable2d;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JComboBox dimentionalityComboBox;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JSpinner iterationsSpinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSpinner learningRateSpinner;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton trainDisplayButton;

    public ColorMapDemo() {
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception caught) {
            LOGGER.warn("Could not set the UI to native look and feel", caught);
        }

        this.initComponents();

        this.iterationsSpinner.setValue(INITIAL_ITERATIONS);
        this.iterationsSpinner.setModel(this.iterationsModel);
        this.learningRateSpinner.setValue(INITIAL_LEARNING_RATE);
        this.learningRateSpinner.setModel(this.learningRateModel);
        this.setResizable(false);
        this.setSize(550, 175);
        this.color1d = (new ColorMap1dCallable(INITIAL_ITERATIONS, INITIAL_LEARNING_RATE, 500)).call();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                ColorMapDemo.this.executor.shutdown();
            }
        });
    }

    public static void main(final String[] args) {
        try {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        new ColorMapDemo().setVisible(true);
                    }
                    catch (Exception caught) {
                        LOGGER.error("Exception was caught", caught);
                        throw new RuntimeException("Exception was caught", caught);
                    }
                    catch (Error caught) {
                        LOGGER.error("Error was caught", caught);
                        throw new RuntimeException("Error was caught", caught);
                    }
                }
            });
        }
        catch (Exception caught) {
            LOGGER.error("Exception was caught", caught);
            throw new RuntimeException("Exception was caught", caught);
        }
        catch (Error caught) {
            LOGGER.error("Error was caught", caught);
            throw new Error("Error was caught", caught);
        }
    }

    @Override
    public void actionPerformed(final ActionEvent evt) {
        if (this.callable1d != null) {
            this.progressBar.setMaximum(this.callable1d.getIterations());
            this.progressBar.setMinimum(0);
            this.progressBar.setValue(this.callable1d.getProgress());
        }
        else if (this.callable2d != null) {
            this.progressBar.setMaximum(this.callable2d.getIterations());
            this.progressBar.setMinimum(0);
            this.progressBar.setValue(this.callable2d.getProgress());
        }

        if (this.future1d != null) {
            if (!this.future1d.isDone())
                return;
            try {
                this.color2d = null;
                this.color1d = this.future1d.get();
                this.setSize(550, 175);
                this.repaint();

                this.future1d = null;
                this.future2d = null;
                this.progressTimer.stop();
                this.trainDisplayButton.setEnabled(true);

            }
            catch (InterruptedException caught) {
                LOGGER.error("ColorMap was unexpectidy interupted", caught);
                throw new Error("Unexpected interuption. Get should block indefinately", caught);
            }
            catch (ExecutionException caught) {
                LOGGER.error("ColorMap had an unexcepted problem executing.", caught);
                throw new Error("Unexpected execution exception. Get should block indefinately", caught);
            }
        }
        else if (this.future2d != null) {
            if (!this.future2d.isDone())
                return;
            try {
                this.color1d = null;
                this.color2d = this.future2d.get();
                this.setSize(550, 650);
                this.repaint();

                this.future1d = null;
                this.future2d = null;
                this.progressTimer.stop();
                this.trainDisplayButton.setEnabled(true);
            }
            catch (InterruptedException caught) {
                LOGGER.error("ColorMap was unexpectidy interupted", caught);
                throw new Error("Unexpected interuption. Get should block indefinately", caught);
            }
            catch (ExecutionException caught) {
                LOGGER.error("ColorMap had an unexcepted problem executing.", caught);
                throw new Error("Unexpected execution exception. Get should block indefinately", caught);
            }
        }
        else {
            this.progressTimer.stop();
            this.trainDisplayButton.setEnabled(true);
        }
    }

    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);

        if (this.color1d != null) {
            for (int colorIndex = 0; colorIndex < this.color1d.length; colorIndex++) {
                final Color color = this.color1d[colorIndex];
                graphics.setColor(color);
                graphics.drawLine(25 + colorIndex, 125, 25 + colorIndex, 150);
            }
        }
        else if (this.color2d != null) {
            final Graphics2D graphics2d = (Graphics2D) graphics;

            for (int colorXIndex = 0; colorXIndex < this.color2d.length; colorXIndex++) {
                for (int colorYIndex = 0; colorYIndex < this.color2d[colorXIndex].length; colorYIndex++) {
                    final Color color = this.color2d[colorXIndex][colorYIndex];
                    graphics2d.setColor(color);
                    final int xPos = colorXIndex * 10;
                    final int yPos = colorYIndex * 10;
                    graphics2d.fillRect(25 + xPos, 125 + yPos, 10, 10);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        this.iterationsSpinner = new javax.swing.JSpinner();
        this.jLabel1 = new javax.swing.JLabel();
        this.jLabel2 = new javax.swing.JLabel();
        this.learningRateSpinner = new javax.swing.JSpinner();
        this.jLabel3 = new javax.swing.JLabel();
        this.dimentionalityComboBox = new javax.swing.JComboBox();
        this.trainDisplayButton = new javax.swing.JButton();
        this.progressBar = new javax.swing.JProgressBar();
        this.jMenuBar1 = new javax.swing.JMenuBar();
        this.jMenu1 = new javax.swing.JMenu();
        this.exitMenuItem = new javax.swing.JMenuItem();
        this.jMenu2 = new javax.swing.JMenu();
        this.aboutMenuItem = new javax.swing.JMenuItem();

        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("SOM Color Map Demo");

        this.iterationsSpinner.setName("iterationsSpinner"); // NOI18N

        this.jLabel1.setText("Training Iterations:");

        this.jLabel2.setText("Learning Rate:");

        this.learningRateSpinner.setName("learningRateSpinner"); // NOI18N

        this.jLabel3.setText("Dimentionality:");

        this.dimentionalityComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"1D", "2D"}));
        this.dimentionalityComboBox.setName("dimentionalityComboBox"); // NOI18N

        this.trainDisplayButton.setText("Train & Display");
        this.trainDisplayButton.setName("trainDisplayButton"); // NOI18N
        this.trainDisplayButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ColorMapDemo.this.trainDisplayButtonActionPerformed(evt);
            }
        });

        this.progressBar.setStringPainted(true);

        this.jMenu1.setText("File");

        this.exitMenuItem.setText("Exit");
        this.exitMenuItem.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ColorMapDemo.this.exitMenuItemMouseReleased(evt);
            }
        });
        this.jMenu1.add(this.exitMenuItem);

        this.jMenuBar1.add(this.jMenu1);

        this.jMenu2.setText("Help");

        this.aboutMenuItem.setText("About");
        this.aboutMenuItem.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ColorMapDemo.this.aboutMenuItemMouseReleased(evt);
            }
        });
        this.jMenu2.add(this.aboutMenuItem);

        this.jMenuBar1.add(this.jMenu2);

        this.setJMenuBar(this.jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                                         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                 .addGroup(layout.createSequentialGroup()
                                                                   .addContainerGap()
                                                                   .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                     .addGroup(layout.createSequentialGroup()
                                                                                                       .addComponent(this.jLabel1)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.iterationsSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.jLabel2)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.learningRateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.jLabel3)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.dimentionalityComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                     .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                                                                                   .addComponent(this.progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                                                                                                                                                   .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                                                   .addComponent(this.trainDisplayButton)))
                                                                   .addContainerGap())
        );
        layout.setVerticalGroup(
                                       layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                               .addGroup(layout.createSequentialGroup()
                                                                 .addContainerGap()
                                                                 .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                   .addComponent(this.jLabel3)
                                                                                   .addComponent(this.dimentionalityComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                   .addComponent(this.jLabel1)
                                                                                   .addComponent(this.iterationsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                   .addComponent(this.learningRateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                   .addComponent(this.jLabel2))
                                                                 .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                 .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                                   .addComponent(this.progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                   .addComponent(this.trainDisplayButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                                 .addContainerGap(219, Short.MAX_VALUE))
        );

        this.pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_exitMenuItemMouseReleased
    {//GEN-HEADEREND:event_exitMenuItemMouseReleased
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemMouseReleased

    private void trainDisplayButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_trainDisplayButtonActionPerformed
    {//GEN-HEADEREND:event_trainDisplayButtonActionPerformed
        final int iterations = this.iterationsModel.getNumber().intValue();
        final double learningRate = this.learningRateModel.getNumber().doubleValue();

        if (this.dimentionalityComboBox.getSelectedIndex() == 0) {
            if (this.future1d != null)
                this.future1d.cancel(true);
            if (this.future2d != null)
                this.future2d.cancel(true);

            this.callable2d = null;
            this.future2d = null;

            this.callable1d = new ColorMap1dCallable(iterations, learningRate, 500);
            this.future1d = this.executor.submit(this.callable1d);
        }
        else {
            if (this.future1d != null)
                this.future1d.cancel(true);
            if (this.future2d != null)
                this.future2d.cancel(true);

            this.callable1d = null;
            this.future1d = null;

            this.callable2d = new ColorMap2dCallable(iterations, learningRate, 50, 50);
            this.future2d = this.executor.submit(this.callable2d);
        }

        this.progressTimer.start();
        this.trainDisplayButton.setEnabled(false);
    }//GEN-LAST:event_trainDisplayButtonActionPerformed

    private void aboutMenuItemMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_aboutMenuItemMouseReleased
    {//GEN-HEADEREND:event_aboutMenuItemMouseReleased
        final AboutDialog about = new AboutDialog(this, true);
        about.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemMouseReleased
    // End of variables declaration//GEN-END:variables

}
