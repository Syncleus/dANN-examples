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
package com.syncleus.dann.examples.nci;

import com.syncleus.dann.neural.*;
import com.syncleus.dann.neural.activation.*;
import com.syncleus.dann.neural.backprop.*;
import com.syncleus.dann.neural.backprop.brain.AbstractFullyConnectedFeedforwardBrain;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * @author Jeffrey Phillips Freeman
 * @since 1.0
 */
public class NciBrain extends AbstractFullyConnectedFeedforwardBrain<InputBackpropNeuron, OutputBackpropNeuron, BackpropNeuron, Synapse<BackpropNeuron>> {
    private static final int CHANNELS = 3;
    private static final double DEFAULT_LEARNING_RATE = 0.001;
    private static final long serialVersionUID = 3093536064207726943L;
    private final double actualCompression;
    private final int xSize;
    private final int ySize;
    private final InputBackpropNeuron[][][] inputNeurons;
    private final List<CompressionNeuron> compressedNeurons;
    private final OutputBackpropNeuron[][][] outputNeurons;
    private final ActivationFunction activationFunction;
    private final double learningRate;
    private boolean learning;
    private boolean compressionInputsSet;

    /**
     * Creates an instance of NciBrain.
     *
     * @param compression A value between 0.0 (inclusive) and 1.0 (exclusive)
     *                    which represents the % of compression.
     */
    public NciBrain(final double compression, final int xSize, final int ySize, final boolean extraConnectivity) {
        super();

        this.learningRate = DEFAULT_LEARNING_RATE;
        this.activationFunction = new SineActivationFunction();

        this.xSize = xSize;
        this.ySize = ySize;
        final int compressedNeuronCount = ((int) Math.ceil((((double) xSize) * ((double) ySize) * ((double) CHANNELS)) * (1.0 - compression)));
        this.inputNeurons = new InputBackpropNeuron[xSize][ySize][CHANNELS];
        this.compressedNeurons = new ArrayList<CompressionNeuron>();
        this.outputNeurons = new OutputBackpropNeuron[xSize][ySize][CHANNELS];
        this.learning = true;
        this.compressionInputsSet = false;
        this.actualCompression = 1.0 - ((double) compressedNeuronCount) / (((double) xSize) * ((double) ySize) * ((double) CHANNELS));
        final int blockSize = xSize * ySize * CHANNELS;

        this.initalizeNetwork(new int[]{blockSize, compressedNeuronCount, blockSize});

        //assign inputs to pixels
        final List<InputNeuron> inputs = new ArrayList<InputNeuron>(this.getInputNeurons());
        final List<OutputNeuron> outputs = new ArrayList<OutputNeuron>(this.getOutputNeurons());
        for (int yIndex = 0; yIndex < ySize; yIndex++) {
            for (int xIndex = 0; xIndex < xSize; xIndex++) {
                for (int rgbIndex = 0; rgbIndex < CHANNELS; rgbIndex++) {
                    final int overallIndex = (yIndex * xSize * CHANNELS) + (xIndex * CHANNELS) + rgbIndex;
                    this.inputNeurons[xIndex][yIndex][rgbIndex] = (InputBackpropNeuron) inputs.get(overallIndex);
                    this.outputNeurons[xIndex][yIndex][rgbIndex] = (OutputBackpropNeuron) outputs.get(overallIndex);
                }
            }
        }
    }

    @Override
    protected BackpropNeuron createNeuron(final int layer, final int index) {
        if (layer == 0)
            return new SimpleInputBackpropNeuron(this);
        else if (layer >= (this.getLayerCount() - 1))
            return new SimpleOutputBackpropNeuron(this, this.activationFunction, this.learningRate);
        else if (layer == 1) {
            final CompressionNeuron compressionNeuron = new CompressionNeuron(this, this.activationFunction, this.learningRate);
            this.compressedNeurons.add(compressionNeuron);
            return compressionNeuron;
        }
        else
            return new SimpleBackpropNeuron(this, this.activationFunction, this.learningRate);
    }

    /**
     * @since 1.0
     */
    public double getCompression() {
        return this.actualCompression;
    }

    /**
     * @since 1.0
     */
    public boolean isLearning() {
        return this.learning;
    }

    /**
     * @since 1.0
     */
    public void setLearning(final boolean learningToSet) {
        this.learning = learningToSet;
    }

    public double getAverageWeight() {
        double weightSum = 0.0;
        double weightCount = 0.0;

        for (BackpropNeuron child : this.getNodes()) {
            try {
                final Set<Synapse<BackpropNeuron>> childSynapses = this.getTraversableEdges(child);

                for (Synapse childSynapse : childSynapses) {
                    weightSum += childSynapse.getWeight();
                    weightCount++;
                }
            }
            catch (ClassCastException caughtException) {
                throw new AssertionError(caughtException);
            }
        }

        return weightSum / weightCount;
    }

    public double getAverageAbsoluteWeight() {
        double weightSum = 0.0;
        double weightCount = 0.0;

        for (BackpropNeuron child : this.getNodes()) {
            try {
                final Set<Synapse<BackpropNeuron>> childSynapses = this.getTraversableEdges(child);

                for (Synapse childSynapse : childSynapses) {
                    weightSum += Math.abs(childSynapse.getWeight());
                    weightCount++;
                }
            }
            catch (ClassCastException caughtException) {
                throw new AssertionError(caughtException);
            }
        }

        return weightSum / weightCount;
    }

    public BufferedImage test(final BufferedImage originalImage) {
        this.setImageOnto(originalImage, true);

        if (this.compressionInputsSet) {
            for (CompressionNeuron compressionNeuron : this.compressedNeurons)
                compressionNeuron.unsetInput();
            this.compressionInputsSet = false;
        }

        //propogate the output
        this.propagate();

        if (!this.learning) {
            return this.createBufferedImage();
        }

        //now back propogate
        this.backPropagate();

        //all done
        return null;
    }

    /**
     * @since 1.0
     */
    public byte[] compress(final BufferedImage originalImage) {
        this.setImageOnto(originalImage, false);

        if (this.compressionInputsSet) {
            for (CompressionNeuron compressionNeuron : this.compressedNeurons)
                compressionNeuron.unsetInput();
            this.compressionInputsSet = false;
        }

        //propogate the output
        this.propagate();

        int compressedDataIndex = 0;
        byte[] compressedData = new byte[this.compressedNeurons.size()];
        for (CompressionNeuron compressionNeuron : this.compressedNeurons)
            compressedData[compressedDataIndex++] = compressionNeuron.getChannelOutput();

        return compressedData;
    }

    private void setImageOnto(final BufferedImage originalImage, final boolean setDesired) {
        final int[] originalRgbArray = new int[this.xSize * this.ySize];
        originalImage.getRGB(0, 0, (originalImage.getWidth() < this.xSize ? originalImage.getWidth() : this.xSize), (originalImage.getHeight() < this.ySize ? originalImage.getHeight() : this.ySize), originalRgbArray, 0, this.xSize);

        //set the image onto the inputs
        for (int yIndex = 0; (yIndex < this.ySize) && (yIndex < originalImage.getHeight()); yIndex++) {
            for (int xIndex = 0; (xIndex < this.xSize) && (xIndex < originalImage.getWidth()); xIndex++) {
                final int rgbCurrent = originalRgbArray[yIndex * this.xSize + xIndex];
                for (int rgbIndex = 0; rgbIndex < CHANNELS; rgbIndex++) {
                    final int channel = (((rgbCurrent >> (rgbIndex * 8)) & 0x000000FF));
                    final double input = (((double) channel) / 127.5) - 1.0;

                    this.inputNeurons[xIndex][yIndex][rgbIndex].setInput(input);
                    if (setDesired) {
                        this.outputNeurons[xIndex][yIndex][rgbIndex].setDesired(input);
                    }
                }
            }
        }
    }

    /**
     * @since 1.0
     */
    public BufferedImage uncompress(final byte[] compressedData) {
        int compressedDataIndex = 0;
        for (CompressionNeuron compressionNeuron : this.compressedNeurons)
            compressionNeuron.setInput(compressedData[compressedDataIndex++]);

        this.compressionInputsSet = true;

        this.propagate();

        return this.createBufferedImage();
    }

    private BufferedImage createBufferedImage() {
        final int[] finalRgbArray = new int[this.xSize * this.ySize];
        final BufferedImage uncompressedImage = new BufferedImage(this.xSize, this.ySize, BufferedImage.TYPE_INT_RGB);
        for (int yIndex = 0; (yIndex < this.ySize) && (yIndex < uncompressedImage.getHeight()); yIndex++) {
            for (int xIndex = 0; (xIndex < this.xSize) && (xIndex < uncompressedImage.getWidth()); xIndex++) {
                //int rgbCurrent = imageToCompress.getRGB(xIndex, yIndex);
                int rgbCurrent = 0;
                for (int rgbIndex = 0; rgbIndex < 4; rgbIndex++) {
                    double output;

                    if (rgbIndex >= CHANNELS)
                        output = this.outputNeurons[xIndex][yIndex][0].getOutput();
                    else
                        output = this.outputNeurons[xIndex][yIndex][rgbIndex].getOutput();

                    final int channel = (int) ((output + 1.0d) * 127.5d);

                    rgbCurrent |= (channel & 0x000000FF) << (rgbIndex * 8);
                }
                finalRgbArray[this.xSize * yIndex + (xIndex)] = rgbCurrent;
            }
        }
        uncompressedImage.setRGB(0, 0, this.xSize, this.ySize, finalRgbArray, 0, this.xSize);

        return uncompressedImage;
    }
}
