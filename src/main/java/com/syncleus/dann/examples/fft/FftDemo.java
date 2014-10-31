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
package com.syncleus.dann.examples.fft;

import com.syncleus.dann.dataprocessing.signal.transform.*;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FftDemo extends JFrame implements ActionListener {
    /**
     * The sample rate in Hz.
     * Common values: 8000, 11025, 16000, 22050, 44100
     */
    private static final float AUDIO_SAMPLE_RATE = 8000.0F;
    /**
     * The sample size in bits.
     */
    private static final int AUDIO_SAMPLE_SIZE = 16;
    private static final int AUDIO_CHANNELS = 1;
    private static final boolean AUDIO_SIGNED = true;
    private static final boolean AUDIO_BIG_ENDIAN = false;

    private final AudioFormat audioFormat;
    private final TargetDataLine targetDataLine;
    private final FastFourierTransformer transformer;
    private final JProgressBar[] frequencyBars;
    private final Timer sampleTimer = new Timer(100, this);
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenuItem;
    private javax.swing.JProgressBar frequencyBar1;
    private javax.swing.JProgressBar frequencyBar10;
    private javax.swing.JProgressBar frequencyBar11;
    private javax.swing.JProgressBar frequencyBar12;
    private javax.swing.JProgressBar frequencyBar13;
    private javax.swing.JProgressBar frequencyBar14;
    private javax.swing.JProgressBar frequencyBar15;
    private javax.swing.JProgressBar frequencyBar16;
    private javax.swing.JProgressBar frequencyBar17;
    private javax.swing.JProgressBar frequencyBar18;
    private javax.swing.JProgressBar frequencyBar19;
    private javax.swing.JProgressBar frequencyBar2;
    private javax.swing.JProgressBar frequencyBar20;
    private javax.swing.JProgressBar frequencyBar21;
    private javax.swing.JProgressBar frequencyBar22;
    private javax.swing.JProgressBar frequencyBar23;
    private javax.swing.JProgressBar frequencyBar24;
    private javax.swing.JProgressBar frequencyBar3;
    private javax.swing.JProgressBar frequencyBar4;
    private javax.swing.JProgressBar frequencyBar5;
    private javax.swing.JProgressBar frequencyBar6;
    private javax.swing.JProgressBar frequencyBar7;
    private javax.swing.JProgressBar frequencyBar8;
    private javax.swing.JProgressBar frequencyBar9;
    private javax.swing.JMenu helpMenuItem;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JButton listenButton;
    public FftDemo() {
        this.initComponents();

        this.setResizable(false);

        this.frequencyBars = new JProgressBar[]{this.frequencyBar1, this.frequencyBar2,
                                                       this.frequencyBar3, this.frequencyBar4,
                                                       this.frequencyBar5, this.frequencyBar6,
                                                       this.frequencyBar7, this.frequencyBar8,
                                                       this.frequencyBar9, this.frequencyBar10,
                                                       this.frequencyBar11, this.frequencyBar12,
                                                       this.frequencyBar13, this.frequencyBar14,
                                                       this.frequencyBar15, this.frequencyBar16,
                                                       this.frequencyBar17, this.frequencyBar18,
                                                       this.frequencyBar19, this.frequencyBar20,
                                                       this.frequencyBar21, this.frequencyBar22,
                                                       this.frequencyBar23, this.frequencyBar24};

        //set the colors as a fradient from blue to red
        for (int index = 0; index < this.frequencyBars.length; index++) {
            final float colorPercent = ((float) index) / ((float) (this.frequencyBars.length - 1));
            this.frequencyBars[index].setForeground(new Color(colorPercent, 0.0f, 1.0f - colorPercent));
            this.frequencyBars[index].setMaximum(1024);
        }

        this.audioFormat = createAudioFormat();
        final DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, this.audioFormat);
        TargetDataLine myTargetDataLine = null;
        try {
            myTargetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        }
        catch (LineUnavailableException caughtException) {
            System.out.println("Line unavailible, exiting...");
            System.exit(0);
        }
        this.targetDataLine = myTargetDataLine;

        this.transformer = new CooleyTukeyFastFourierTransformer(1024, 8000);
    }

    private static AudioFormat createAudioFormat() {
        return new AudioFormat(AUDIO_SAMPLE_RATE, AUDIO_SAMPLE_SIZE, AUDIO_CHANNELS, AUDIO_SIGNED, AUDIO_BIG_ENDIAN);
    }

    public static void main(final String[] args) {
        java.awt.EventQueue.invokeLater(
                                               new Runnable() {
                                                   @Override
                                                   public void run() {
                                                       new FftDemo().setVisible(true);
                                                   }
                                               });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        this.frequencyBar1 = new javax.swing.JProgressBar();
        this.listenButton = new javax.swing.JButton();
        this.frequencyBar10 = new javax.swing.JProgressBar();
        this.frequencyBar2 = new javax.swing.JProgressBar();
        this.frequencyBar4 = new javax.swing.JProgressBar();
        this.frequencyBar5 = new javax.swing.JProgressBar();
        this.frequencyBar9 = new javax.swing.JProgressBar();
        this.frequencyBar3 = new javax.swing.JProgressBar();
        this.frequencyBar6 = new javax.swing.JProgressBar();
        this.frequencyBar7 = new javax.swing.JProgressBar();
        this.frequencyBar8 = new javax.swing.JProgressBar();
        this.frequencyBar11 = new javax.swing.JProgressBar();
        this.frequencyBar12 = new javax.swing.JProgressBar();
        this.frequencyBar13 = new javax.swing.JProgressBar();
        this.frequencyBar14 = new javax.swing.JProgressBar();
        this.frequencyBar15 = new javax.swing.JProgressBar();
        this.frequencyBar16 = new javax.swing.JProgressBar();
        this.frequencyBar17 = new javax.swing.JProgressBar();
        this.frequencyBar18 = new javax.swing.JProgressBar();
        this.frequencyBar19 = new javax.swing.JProgressBar();
        this.frequencyBar20 = new javax.swing.JProgressBar();
        this.frequencyBar21 = new javax.swing.JProgressBar();
        this.frequencyBar22 = new javax.swing.JProgressBar();
        this.frequencyBar23 = new javax.swing.JProgressBar();
        this.frequencyBar24 = new javax.swing.JProgressBar();
        this.jMenuBar1 = new javax.swing.JMenuBar();
        this.fileMenuItem = new javax.swing.JMenu();
        this.exitMenuItem = new javax.swing.JMenuItem();
        this.helpMenuItem = new javax.swing.JMenu();
        this.aboutMenuItem = new javax.swing.JMenuItem();

        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Fast Fourier Transform Demo");

        this.frequencyBar1.setForeground(new java.awt.Color(0, 0, 255));
        this.frequencyBar1.setOrientation(1);

        this.listenButton.setText("Listen");
        this.listenButton.setName("listenButton"); // NOI18N
        this.listenButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FftDemo.this.listenButtonActionPerformed(evt);
            }
        });

        this.frequencyBar10.setOrientation(1);

        this.frequencyBar2.setOrientation(1);

        this.frequencyBar4.setOrientation(1);

        this.frequencyBar5.setOrientation(1);

        this.frequencyBar9.setOrientation(1);

        this.frequencyBar3.setOrientation(1);

        this.frequencyBar6.setOrientation(1);

        this.frequencyBar7.setOrientation(1);

        this.frequencyBar8.setOrientation(1);

        this.frequencyBar11.setOrientation(1);

        this.frequencyBar12.setOrientation(1);

        this.frequencyBar13.setOrientation(1);

        this.frequencyBar14.setOrientation(1);

        this.frequencyBar15.setOrientation(1);

        this.frequencyBar16.setOrientation(1);

        this.frequencyBar17.setOrientation(1);

        this.frequencyBar18.setOrientation(1);

        this.frequencyBar19.setOrientation(1);

        this.frequencyBar20.setOrientation(1);

        this.frequencyBar21.setOrientation(1);

        this.frequencyBar22.setOrientation(1);

        this.frequencyBar23.setOrientation(1);

        this.frequencyBar24.setForeground(new java.awt.Color(255, 0, 0));
        this.frequencyBar24.setOrientation(1);

        this.fileMenuItem.setText("File");

        this.exitMenuItem.setText("Exit");
        this.exitMenuItem.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                FftDemo.this.exitMenuItemMouseReleased(evt);
            }
        });
        this.fileMenuItem.add(this.exitMenuItem);

        this.jMenuBar1.add(this.fileMenuItem);

        this.helpMenuItem.setText("Help");

        this.aboutMenuItem.setText("About");
        this.helpMenuItem.add(this.aboutMenuItem);

        this.jMenuBar1.add(this.helpMenuItem);

        this.setJMenuBar(this.jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                                         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                 .addGroup(layout.createSequentialGroup()
                                                                   .addContainerGap()
                                                                   .addComponent(this.frequencyBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                   .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                   .addComponent(this.frequencyBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                   .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                   .addComponent(this.frequencyBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                   .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                   .addComponent(this.frequencyBar4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                   .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                   .addComponent(this.frequencyBar5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                   .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                   .addComponent(this.frequencyBar6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                   .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                   .addComponent(this.frequencyBar7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                   .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                   .addComponent(this.frequencyBar8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                   .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                   .addComponent(this.frequencyBar9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                   .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                   .addComponent(this.frequencyBar10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                   .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                   .addComponent(this.frequencyBar11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                   .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                   .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                     .addComponent(this.listenButton)
                                                                                     .addGroup(layout.createSequentialGroup()
                                                                                                       .addComponent(this.frequencyBar12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.frequencyBar13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.frequencyBar14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.frequencyBar15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.frequencyBar16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.frequencyBar17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.frequencyBar18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.frequencyBar19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.frequencyBar20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.frequencyBar21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.frequencyBar22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.frequencyBar23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.frequencyBar24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                   .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                                       layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                               .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                                             .addContainerGap()
                                                                                                             .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                                                               .addComponent(this.frequencyBar24, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar23, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar22, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar21, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar20, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar19, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar18, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar17, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar16, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar15, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar14, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar8, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar7, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar6, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar5, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar4, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar9, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar10, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar11, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar12, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                                                                                                               .addComponent(this.frequencyBar13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))
                                                                                                             .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                             .addComponent(this.listenButton)
                                                                                                             .addContainerGap())
        );

        this.pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_exitMenuItemMouseReleased
    {//GEN-HEADEREND:event_exitMenuItemMouseReleased
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemMouseReleased

    private void listenButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_listenButtonActionPerformed
    {//GEN-HEADEREND:event_listenButtonActionPerformed
        try {
            if (this.targetDataLine.isOpen()) {
                this.sampleTimer.stop();

                this.targetDataLine.stop();
                this.targetDataLine.close();

                this.listenButton.setText("Listen");
            }
            else {
                this.targetDataLine.open(this.audioFormat);
                this.targetDataLine.start();

                this.sampleTimer.start();

                this.listenButton.setText("Stop");
            }
        }
        catch (LineUnavailableException caughtException) {
            System.out.println("Line unavailible, exiting...");
            System.exit(0);
        }
    }//GEN-LAST:event_listenButtonActionPerformed

    @Override
    public void actionPerformed(final ActionEvent evt) {
        if (this.transformer.getBlockSize() * 2 <= this.targetDataLine.available()) {
            final byte[] signalBytes = new byte[this.transformer.getBlockSize() * 2];
            this.targetDataLine.read(signalBytes, 0, signalBytes.length);

            final double[] signal = new double[this.transformer.getBlockSize()];
            for (int signalIndex = 0; signalIndex < signal.length; signalIndex++) {
                final int signalBytesIndex = signalIndex * 2;
                signal[signalIndex] = this.bytesToDouble(signalBytes[signalBytesIndex], signalBytes[signalBytesIndex + 1]);
            }

            final DiscreteFourierTransform transform = this.transformer.transform(signal);
            final double maximumFrequency = transform.getMaximumFrequency();
            final double bandSize = maximumFrequency / ((double) this.frequencyBars.length);
            for (int frequencyBarIndex = 0; frequencyBarIndex < this.frequencyBars.length; frequencyBarIndex++) {
                final double bandPower = transform.getBandGeometricMean(((double) frequencyBarIndex) * bandSize, ((double) frequencyBarIndex + 1) * bandSize);
                this.frequencyBars[frequencyBarIndex].setValue((int) (bandPower * 500.0));
            }
        }
    }

    private double bytesToDouble(final byte... data) {
        return ((double) (((short) data[1]) << 8) + ((short) data[0])) / ((double) Short.MAX_VALUE);
    }
    // End of variables declaration//GEN-END:variables

}
