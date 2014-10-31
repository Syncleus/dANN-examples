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
package com.syncleus.dann.examples.hyperassociativemap.visualization;

import com.syncleus.dann.ComponentUnavailableException;
import com.syncleus.dann.graph.drawing.hyperassociativemap.visualization.HyperassociativeMapCanvas;

import javax.swing.*;
import java.awt.event.*;
import java.util.concurrent.*;

public class ViewMap extends JFrame implements ActionListener {
    private static final float NODE_RADIUS = 0.07F;
    private static final long serialVersionUID = -1535635297304488816L;
    private final HyperassociativeMapCanvas mapVisual;
    private final LayeredHyperassociativeMap associativeMap;
    private final ExecutorService executor;
    private FutureTask<Void> lastRun;

    public ViewMap() {
        // With only 1 thread, we would get a dead-lock when
        // the view-update-thread is waiting for the alignment.
        this.executor = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors()));
        this.associativeMap = new LayeredHyperassociativeMap(8, this.executor);

        HyperassociativeMapCanvas myMapVisual = null;
        try {
            myMapVisual = new HyperassociativeMapCanvas(this.associativeMap, NODE_RADIUS);
            this.initComponents();

            this.lastRun = new FutureTask<Void>(new UpdateViewRun(myMapVisual, this.associativeMap), null);
            this.executor.execute(this.lastRun);

            myMapVisual.setFocusTraversalKeysEnabled(false);
            AssociativeMapKeyAdapter keyAdapter = new AssociativeMapKeyAdapter(this.associativeMap);
            myMapVisual.addKeyListener(keyAdapter);
            myMapVisual.getCanvas3D().addKeyListener(keyAdapter);
            this.addKeyListener(keyAdapter);

            new Timer(100, this).start();

            myMapVisual.setLocation(0, 0);
            myMapVisual.setSize(800, 600);
            myMapVisual.setVisible(true);
            myMapVisual.refresh();
        }
        catch (ComponentUnavailableException exc) {
            myMapVisual = null;
            this.add(exc.newPanel());
        }
        this.mapVisual = myMapVisual;
        if (this.mapVisual != null) {
            this.add(this.mapVisual);
        }

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                ViewMap.this.executor.shutdown();
            }
        });
        this.setFocusTraversalKeysEnabled(false);

        this.setSize(800, 600);
    }

    private static boolean checkClasses() {
        // This class does not exist in Java3D 1.3.1, so we can not use this
        // test. Fortunately, it will still fail gracefully later, if Java3D
        // is not is installed properly.
        /*
		try
		{
			Class.forName("javax.media.j3d.NativePipeline");
		}
		catch (ClassNotFoundException caughtException)
		{
			System.out.println("Java3D library is not in classpath!");
			return false;
		}
		*/

        return true;
    }

    public static void main(final String[] args) {
        // check that the Java3D drivers are present
        if (!checkClasses()) {
            return;
        }

        System.out.println("controls:");
        System.out.println("R: reset");
        System.out.println("L: reset learning curve");
        System.out.println("up arrow: increase Equilibrium");
        System.out.println("down arrow: decrease Equilibrium");

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ViewMap().setVisible(true);
            }
        });
    }

    @Override
    public void actionPerformed(final ActionEvent evt) {
        if ((this.lastRun != null) && !this.lastRun.isDone()) {
            return;
        }

        if (!this.isVisible()) {
            return;
        }

        this.lastRun = new FutureTask<Void>(new UpdateViewRun(this.mapVisual, this.associativeMap), null);
        this.executor.execute(this.lastRun);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                                         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                 .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                                       layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                               .addGap(0, 300, Short.MAX_VALUE)
        );

        this.pack();
    }//GEN-END:initComponents

    private static class AssociativeMapKeyAdapter extends KeyAdapter {
        private final LayeredHyperassociativeMap associativeMap;

        public AssociativeMapKeyAdapter(final LayeredHyperassociativeMap associativeMap) {
            this.associativeMap = associativeMap;
        }

        @Override
        public void keyPressed(final KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_R) {
                this.associativeMap.reset();
            }
            if (evt.getKeyCode() == KeyEvent.VK_L) {
                this.associativeMap.resetLearning();
            }
            else if (evt.getKeyCode() == KeyEvent.VK_UP) {
                double equilibDist = this.associativeMap.getEquilibriumDistance();
                if (equilibDist < 1.0) {
                    equilibDist *= 1.1;
                }
                else {
                    equilibDist += 1.0;
                }
                this.associativeMap.setEquilibriumDistance(equilibDist);
                this.associativeMap.resetLearning();
            }
            else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
                double equilibDist = this.associativeMap.getEquilibriumDistance();
                if (equilibDist < 2.0) {
                    equilibDist *= 0.9;
                }
                else {
                    equilibDist -= 1.0;
                }
                this.associativeMap.setEquilibriumDistance(equilibDist);
                this.associativeMap.resetLearning();
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
}
