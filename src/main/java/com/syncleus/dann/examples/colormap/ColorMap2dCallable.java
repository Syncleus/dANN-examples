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

import com.syncleus.dann.math.Vector;
import com.syncleus.dann.neural.Synapse;
import com.syncleus.dann.neural.som.*;
import com.syncleus.dann.neural.som.brain.ExponentialDecaySomBrain;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

public class ColorMap2dCallable implements Callable<Color[][]> {
    private static final Random RANDOM = new Random();
    private static final Logger LOGGER = Logger.getLogger(ColorMap2dCallable.class);
    private final int iterations;
    private final double learningRate;
    private final int width;
    private final int height;
    private volatile int progress;

    public ColorMap2dCallable(final int iterations, final double learningRate, final int width, final int height) {
        this.iterations = iterations;
        this.learningRate = learningRate;
        this.width = width;
        this.height = height;
    }

    @Override
    public Color[][] call() {
        try {
            //initialize brain
            final ExponentialDecaySomBrain<SomInputNeuron, SomOutputNeuron, SomNeuron, Synapse<SomNeuron>> brain
                    = new ExponentialDecaySomBrain<SomInputNeuron, SomOutputNeuron, SomNeuron, Synapse<SomNeuron>>(ColorMap1dCallable.COLOR_CHANNELS, 2, this.getIterations(), this.getLearningRate());

            //create the output latice
            for (double x = 0; x < this.getWidth(); x++)
                for (double y = 0; y < this.getHeight(); y++)
                    brain.createOutput(new Vector(new double[]{x, y}));

            //makes sure all the weights are randomly distributed within the
            //output bounds.
            for (Synapse synapse : brain.getEdges())
                synapse.setWeight(RANDOM.nextDouble());

            //run through random training data
            for (int iteration = 0; iteration < this.getIterations(); iteration++) {
                this.progress++;

                for (int ci = 0; ci < ColorMap1dCallable.COLOR_CHANNELS; ci++)
                    brain.setInput(ci, RANDOM.nextDouble());

                brain.getBestMatchingUnit(true);
            }

            //pull the output weight vectors
            final Map<Vector, double[]> outputWeightVectors = brain.getOutputWeightVectors();

            //construct the color array
            Color[][] colorPositions = new Color[this.getWidth()][this.getHeight()];
            for (Entry<Vector, double[]> weightVector : outputWeightVectors.entrySet()) {
                final Vector currentPoint = weightVector.getKey();
                final double[] currentVector = weightVector.getValue();

                //convert the current Vector to a color.
                if ((float) currentVector[0] < 0f) {
                    LOGGER.warn("Incorrect red component: " + currentVector[0]);
                    currentVector[0] *= -1f;
                }
                if ((float) currentVector[1] < 0f) {
                    LOGGER.warn("Incorrect green compoent: " + currentVector[1]);
                    currentVector[1] *= -1f;
                }
                if ((float) currentVector[2] < 0f) {
                    LOGGER.warn("Incorrect blue compoent: " + currentVector[1]);
                    currentVector[2] *= -1f;
                }
                final Color currentColor = new Color((float) currentVector[0], (float) currentVector[1], (float) currentVector[2]);

                //add the current color to the colorPositions
                colorPositions[(int) Math.floor(currentPoint.getCoordinate(1))][(int) Math.floor(currentPoint.getCoordinate(2))] = currentColor;
            }

            //return the color positions
            return colorPositions;
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


    /**
     * @return the iterations
     */
    public int getIterations() {
        return this.iterations;
    }

    /**
     * @return the learningRate
     */
    public double getLearningRate() {
        return this.learningRate;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * @return the progress
     */
    public int getProgress() {
        return this.progress;
    }
}
