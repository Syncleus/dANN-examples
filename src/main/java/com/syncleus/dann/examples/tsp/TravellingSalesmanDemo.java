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
package com.syncleus.dann.examples.tsp;

import com.syncleus.dann.math.Vector;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;

public class TravellingSalesmanDemo extends JFrame implements ActionListener {
    private static final Logger LOGGER = Logger.getLogger(TravellingSalesmanDemo.class);
    private static final Random RANDOM = new Random();
    private static final int MAP_X = 12;
    private static final int MAP_Y = 130;
    private static final int MAP_WIDTH = 635;
    private static final int MAP_HEIGHT = 500;
    private final SpinnerNumberModel citiesModel = new SpinnerNumberModel(10, 1, 100, 1);
    private final SpinnerNumberModel mutabilityModel = new SpinnerNumberModel(1.0, Double.MIN_VALUE, 10000, 0.1);
    private final SpinnerNumberModel populationModel = new SpinnerNumberModel(100, 4, 1000, 10);
    private final SpinnerNumberModel crossoverModel = new SpinnerNumberModel(0.1, Double.MIN_VALUE, 1.0, 0.01);
    private final SpinnerNumberModel dieOffModel = new SpinnerNumberModel(0.4, Double.MIN_VALUE, 1.0, 0.01);
    private final SpinnerNumberModel generationsModel = new SpinnerNumberModel(100, 1, 100000, 100);
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    private final Timer progressTimer = new Timer(100, this);

    private PopulationEvolveCallable populationCallable = null;
    private Future<TravellingSalesmanChromosome> futureWinner = null;
    private TravellingSalesmanChromosome currentWinner = null;
    private Vector[] cities = null;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JLabel citiesLabel;
    private javax.swing.JSpinner citiesSpinner;
    private javax.swing.JLabel crossoverLabel;
    private javax.swing.JSpinner crossoverSpinner;
    private javax.swing.JLabel dieOffLabel;
    private javax.swing.JSpinner dieOffSpinner;
    private javax.swing.JButton evolveDisplayButton;
    private javax.swing.JMenu fileMenuItem;
    private javax.swing.JLabel generationsLabel;
    private javax.swing.JSpinner generationsSpinner;
    private javax.swing.JMenu helpMenuItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel mutabilityLabel;
    private javax.swing.JSpinner mutabilitySpinner;
    private javax.swing.JLabel populationLabel;
    private javax.swing.JSpinner populationSpinner;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JMenuItem quitMenuItem;
    public TravellingSalesmanDemo() {
        LOGGER.info("Instantiating Travelling Salesman Demo Frame");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception caught) {
            LOGGER.warn("Could not set the UI to native look and feel", caught);
        }

        this.initComponents();

        this.citiesSpinner.setModel(this.citiesModel);
        this.mutabilitySpinner.setModel(this.mutabilityModel);
        this.populationSpinner.setModel(this.populationModel);
        this.crossoverSpinner.setModel(this.crossoverModel);
        this.dieOffSpinner.setModel(this.dieOffModel);
        this.generationsSpinner.setModel(this.generationsModel);

        this.setResizable(false);
        this.repaint();
    }

    private static Vector[] getRandomPoints(final int cityCount) {
        if (cityCount < 4)
            throw new IllegalArgumentException("cityCount must have atleast 4 elements");

        final HashSet<Vector> pointsSet = new HashSet<Vector>();
        while (pointsSet.size() < cityCount)
            pointsSet.add(new Vector(new double[]{RANDOM.nextDouble() * MAP_WIDTH, RANDOM.nextDouble() * MAP_HEIGHT}));

        final Vector[] points = new Vector[cityCount];
        pointsSet.toArray(points);

        return points;
    }

    public static void main(final String[] args) {
        LOGGER.info("Starting Travelling Salesman Demo from main()");
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TravellingSalesmanDemo().setVisible(true);
            }
        });
    }

    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
        graphics.drawRect(MAP_X, MAP_Y, MAP_WIDTH, MAP_HEIGHT);

        if (this.cities != null) {
            for (Vector city : this.cities) {
                final int currentX = (int) city.getCoordinate(1);
                final int currentY = (int) city.getCoordinate(2);

                graphics.fillArc((currentX + MAP_X) - 5, (currentY + MAP_Y) - 5, 10, 10, 0, 360);
            }
        }

        if ((this.cities != null) && (this.currentWinner != null)) {
            final int[] ordering = this.currentWinner.getCitiesOrder();
            final Vector[] orderedPoints = new Vector[ordering.length];
            for (int cityIndex = 0; cityIndex < this.cities.length; cityIndex++) {
                orderedPoints[ordering[cityIndex]] = this.cities[cityIndex];
            }

            //draw the points
            Vector firstPoint = null;
            Vector lastPoint = null;
            for (Vector point : orderedPoints) {
                if (lastPoint == null) {
                    lastPoint = point;
                    firstPoint = point;
                }
                else {
                    final int lastX = (int) lastPoint.getCoordinate(1);
                    final int lastY = (int) lastPoint.getCoordinate(2);

                    final int currentX = (int) point.getCoordinate(1);
                    final int currentY = (int) point.getCoordinate(2);

                    graphics.drawLine(lastX + MAP_X, lastY + MAP_Y, currentX + MAP_X, currentY + MAP_Y);

                    lastPoint = point;
                }
            }
            if ((lastPoint != null) && (firstPoint != null)) {
                final int lastX = (int) lastPoint.getCoordinate(1);
                final int lastY = (int) lastPoint.getCoordinate(2);

                final int firstX = (int) firstPoint.getCoordinate(1);
                final int firstY = (int) firstPoint.getCoordinate(2);

                graphics.drawLine(lastX + MAP_X, lastY + MAP_Y, firstX + MAP_X, firstY + MAP_Y);
            }
        }

    }

    @Override
    public void actionPerformed(final ActionEvent evt) {
        if ((this.futureWinner != null) && (this.populationCallable != null)) {
            this.progressBar.setValue(this.populationCallable.getIterations());

            if (this.futureWinner.isDone()) {
                LOGGER.debug("this.futureWinner.isDone() == true");

                try {
                    this.currentWinner = this.futureWinner.get();
                }
                catch (Exception caught) {
                    LOGGER.error("futureWinner threw an unexpected exception", caught);
                    throw new Error("futureWinner threw an unexpected exception", caught);
                }
                this.populationCallable = null;
                this.futureWinner = null;

                this.progressTimer.stop();
                this.evolveDisplayButton.setEnabled(true);

                this.repaint();
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        this.citiesLabel = new javax.swing.JLabel();
        this.citiesSpinner = new javax.swing.JSpinner();
        this.populationLabel = new javax.swing.JLabel();
        this.populationSpinner = new javax.swing.JSpinner();
        this.mutabilityLabel = new javax.swing.JLabel();
        this.mutabilitySpinner = new javax.swing.JSpinner();
        this.crossoverLabel = new javax.swing.JLabel();
        this.crossoverSpinner = new javax.swing.JSpinner();
        this.dieOffLabel = new javax.swing.JLabel();
        this.dieOffSpinner = new javax.swing.JSpinner();
        this.evolveDisplayButton = new javax.swing.JButton();
        this.progressBar = new javax.swing.JProgressBar();
        this.generationsLabel = new javax.swing.JLabel();
        this.generationsSpinner = new javax.swing.JSpinner();
        this.menuBar = new javax.swing.JMenuBar();
        this.fileMenuItem = new javax.swing.JMenu();
        this.quitMenuItem = new javax.swing.JMenuItem();
        this.helpMenuItem = new javax.swing.JMenu();
        this.aboutMenuItem = new javax.swing.JMenuItem();

        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Travelling Salesman Demo");

        this.citiesLabel.setText("Cities:");

        this.populationLabel.setText("Population:");

        this.mutabilityLabel.setText("Mutability:");

        this.crossoverLabel.setText("Crossover:");

        this.dieOffLabel.setText("Die Off:");

        this.evolveDisplayButton.setText("Evolve & Display");
        this.evolveDisplayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TravellingSalesmanDemo.this.evolveDisplayButtonActionPerformed(evt);
            }
        });

        this.progressBar.setStringPainted(true);

        this.generationsLabel.setText("Generations:");

        this.fileMenuItem.setText("File");

        this.quitMenuItem.setText("Quit");
        this.quitMenuItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                TravellingSalesmanDemo.this.quitMenuItemMouseReleased(evt);
            }
        });
        this.fileMenuItem.add(this.quitMenuItem);

        this.menuBar.add(this.fileMenuItem);

        this.helpMenuItem.setText("Help");

        this.aboutMenuItem.setText("About");
        this.helpMenuItem.add(this.aboutMenuItem);

        this.menuBar.add(this.helpMenuItem);

        this.setJMenuBar(this.menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                                         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                 .addGroup(layout.createSequentialGroup()
                                                                   .addContainerGap()
                                                                   .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                     .addGroup(layout.createSequentialGroup()
                                                                                                       .addComponent(this.citiesLabel)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.citiesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.populationLabel)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.populationSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.mutabilityLabel)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.mutabilitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.crossoverLabel)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.crossoverSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.dieOffLabel)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.dieOffSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.generationsLabel)
                                                                                                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                       .addComponent(this.generationsSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                                                                                     .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                                                                                   .addComponent(this.progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                                                                                                                                                   .addGap(10, 10, 10)
                                                                                                                                                   .addComponent(this.evolveDisplayButton)))
                                                                   .addContainerGap())
        );
        layout.setVerticalGroup(
                                       layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                               .addGroup(layout.createSequentialGroup()
                                                                 .addContainerGap()
                                                                 .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                   .addComponent(this.citiesLabel)
                                                                                   .addComponent(this.citiesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                   .addComponent(this.populationLabel)
                                                                                   .addComponent(this.populationSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                   .addComponent(this.mutabilityLabel)
                                                                                   .addComponent(this.mutabilitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                   .addComponent(this.crossoverLabel)
                                                                                   .addComponent(this.crossoverSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                   .addComponent(this.dieOffLabel)
                                                                                   .addComponent(this.dieOffSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                   .addComponent(this.generationsLabel)
                                                                                   .addComponent(this.generationsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                 .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                 .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                   .addComponent(this.progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                   .addComponent(this.evolveDisplayButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                                 .addContainerGap(520, Short.MAX_VALUE))
        );

        this.pack();
    }// </editor-fold>//GEN-END:initComponents

    private void quitMenuItemMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_quitMenuItemMouseReleased
    {//GEN-HEADEREND:event_quitMenuItemMouseReleased
        System.exit(0);
    }//GEN-LAST:event_quitMenuItemMouseReleased

    private void evolveDisplayButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_evolveDisplayButtonActionPerformed
    {//GEN-HEADEREND:event_evolveDisplayButtonActionPerformed
        this.currentWinner = null;
        this.cities = getRandomPoints(this.citiesModel.getNumber().intValue());

        final int populationSize = this.populationModel.getNumber().intValue();
        final double mutability = this.mutabilityModel.getNumber().doubleValue();
        final double crossover = this.crossoverModel.getNumber().doubleValue();
        final double dieOff = this.dieOffModel.getNumber().doubleValue();
        final int generations = this.generationsModel.getNumber().intValue();

        final TravellingSalesmanPopulation population = new TravellingSalesmanPopulation(this.cities, mutability, crossover, dieOff);
        population.initializePopulation(populationSize);

        this.populationCallable = new PopulationEvolveCallable(population, generations);
        this.futureWinner = this.executor.submit(this.populationCallable);

        this.progressBar.setMaximum(generations);

        this.evolveDisplayButton.setEnabled(false);
        this.progressTimer.start();

        this.repaint();
    }//GEN-LAST:event_evolveDisplayButtonActionPerformed

    private static class PopulationEvolveCallable implements Callable<TravellingSalesmanChromosome> {
        private static final Logger LOGGER = Logger.getLogger(PopulationEvolveCallable.class);
        private final TravellingSalesmanPopulation population;
        private final int iterationsMax;
        private volatile int iterations = 0;

        public PopulationEvolveCallable(final TravellingSalesmanPopulation population, final int iterationsMax) {
            if (iterationsMax <= 0)
                throw new IllegalArgumentException("iterationsMax must be greater than 0");

            this.population = population;
            this.iterationsMax = iterationsMax;
        }

        @Override
        public TravellingSalesmanChromosome call() {
            try {
                for (; this.iterations < this.iterationsMax; this.iterations++)
                    this.population.nextGeneration();

                return this.population.getWinner();
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

        public int getIterations() {
            return this.iterations;
        }

        public int getIterationsMax() {
            return this.iterationsMax;
        }
    }
    // End of variables declaration//GEN-END:variables

}
