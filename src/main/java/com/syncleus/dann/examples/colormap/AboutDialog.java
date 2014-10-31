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

import javax.swing.*;
import java.awt.*;

public class AboutDialog extends JDialog {
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    public AboutDialog(final Frame parent, final boolean modal) {
        super(parent, modal);
        this.initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        this.jButton1 = new javax.swing.JButton();
        this.jLabel1 = new javax.swing.JLabel();
        this.jLabel2 = new javax.swing.JLabel();
        this.jScrollPane1 = new javax.swing.JScrollPane();
        this.jTextArea1 = new javax.swing.JTextArea();

        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle("About");

        this.jButton1.setText("OK");
        this.jButton1.setName("ok button"); // NOI18N
        this.jButton1.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AboutDialog.this.jButton1ActionPerformed(evt);
            }
        });

        this.jLabel1.setText("Syncleus, Inc.");

        this.jLabel2.setText("SOM Color Map Demo");

        this.jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.jScrollPane1.setHorizontalScrollBar(null);

        this.jTextArea1.setColumns(20);
        this.jTextArea1.setEditable(false);
        this.jTextArea1.setRows(5);
        this.jTextArea1.setText("Instructions:\n\nFirst select the number of iterations to train train the som. Next select the\ninitial learning rate for training. Finally choose 1D or 2D output lattice.\n\nNow when you click \"Train & Display\" it will train a new SOM and render the\noutput lattice colors. If the SOM was successful colors should be displayed as\nsmoothly blended colors in 1D or 2D. If the colors do not render properly\nexperiment with different values.");
        this.jTextArea1.setWrapStyleWord(true);
        this.jScrollPane1.setViewportView(this.jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                                         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                 .addGroup(layout.createSequentialGroup()
                                                                   .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                     .addGroup(layout.createSequentialGroup()
                                                                                                       .addContainerGap()
                                                                                                       .addComponent(this.jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE))
                                                                                     .addGroup(layout.createSequentialGroup()
                                                                                                       .addGap(170, 170, 170)
                                                                                                       .addComponent(this.jLabel2))
                                                                                     .addGroup(layout.createSequentialGroup()
                                                                                                       .addGap(203, 203, 203)
                                                                                                       .addComponent(this.jButton1))
                                                                                     .addGroup(layout.createSequentialGroup()
                                                                                                       .addGap(190, 190, 190)
                                                                                                       .addComponent(this.jLabel1)))
                                                                   .addContainerGap())
        );
        layout.setVerticalGroup(
                                       layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                               .addGroup(layout.createSequentialGroup()
                                                                 .addContainerGap()
                                                                 .addComponent(this.jLabel1)
                                                                 .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                 .addComponent(this.jLabel2)
                                                                 .addGap(11, 11, 11)
                                                                 .addComponent(this.jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                 .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                 .addComponent(this.jButton1)
                                                                 .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        this.pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed
    // End of variables declaration//GEN-END:variables
}
