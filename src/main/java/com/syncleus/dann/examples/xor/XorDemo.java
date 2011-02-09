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
package com.syncleus.dann.examples.xor;

import java.util.ArrayList;
import com.syncleus.dann.neural.InputNeuron;
import com.syncleus.dann.neural.OutputNeuron;
import com.syncleus.dann.neural.activation.ActivationFunction;
import com.syncleus.dann.neural.activation.SineActivationFunction;
import com.syncleus.dann.neural.backprop.InputBackpropNeuron;
import com.syncleus.dann.neural.backprop.OutputBackpropNeuron;
import com.syncleus.dann.neural.backprop.brain.FullyConnectedFeedforwardBrain;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 * An example main class that shows using dANN to solve an XOR problem. An XOR
 * is a circuit that returns true (1) when only one of its inputs is true. It
 * returns false (-1) if none all of its inputs are false or if more then one
 * of its inputs are true.
 *
 * @since 0.1
 * @author Jeffrey Phillips Freeman
 */
public final class XorDemo
{
	private static final Logger LOGGER = Logger.getLogger(XorDemo.class);
	private static final double LEARNING_RATE = 0.0175;
	private static final long KEEP_ALIVE_TIME = 20;
	private static final int INPUTS = 3;
	private static BufferedReader inReader = null;
	private static InputBackpropNeuron[] input = null;
	private static OutputBackpropNeuron output = null;
	private static FullyConnectedFeedforwardBrain brain;
	private static String saveLocation = "default.dann";

	private XorDemo()
	{
	}

	public static void main(final String[] args)
	{
		try
		{
			if( args.length > 0 )
				saveLocation = args[0];

			inReader = new BufferedReader(new InputStreamReader(System.in));

			//Adjust the learning rate
			final ActivationFunction activationFunction = new SineActivationFunction();

			final int cores = Runtime.getRuntime().availableProcessors();
			final ThreadPoolExecutor executer = new ThreadPoolExecutor(cores+1, cores*2, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue());
			try
			{
				brain = new FullyConnectedFeedforwardBrain(new int[] {INPUTS, INPUTS, 1}, LEARNING_RATE, activationFunction, executer);
				final List<InputNeuron> inputs = new ArrayList<InputNeuron>(brain.getInputNeurons());
				for (int ii = 0; ii < INPUTS; ii++)
				{
					input[ii] = (InputBackpropNeuron) inputs.get(ii);
				}
				final List<OutputNeuron> outputs = new ArrayList<OutputNeuron>(brain.getOutputNeurons());
				output = (OutputBackpropNeuron) outputs.get(0);

				//now that we have created the neural network lets put it to use.
				System.out.println("dANN nXOR Example");

				int currentCommand = 'q';
				do
				{
					boolean received = false;
					while( !received )
					{
						System.out.println();
						System.out.println("D) display current circuit pin-out");
						System.out.println("T) train the current circuit");
						System.out.println("S) save");
						System.out.println("L) load");
						System.out.println("Q) quit");
						System.out.println("\tEnter command: ");

						received = true;
						try
						{
							final String lastInput = inReader.readLine();
							if( lastInput == null)
								currentCommand = 'q';
							else
								currentCommand = lastInput.toLowerCase().toCharArray()[0];
						}
						catch(ArrayIndexOutOfBoundsException caughtException)
						{
							received = false;
						}
					}

					System.out.println();

					switch( currentCommand )
					{
						case 'd':
							testOutput();
							break;
						case 't':
							int cycles = 750;
							System.out.println("How many training cycles [Default: " + cycles + "]: ");
							try
							{
								cycles = Integer.parseInt(inReader.readLine());
							}
							catch(NumberFormatException caughtException)
							{
							}
							System.out.println();
							train(cycles);
							System.out.println("Training Complete!");
							break;
						case 's':
							save();
							break;
						case 'l':
							load();
							break;
						case 'q':
							System.out.print("Quiting...");
							break;
						default:
							System.out.println("Invalid command");
					}
				} while( (currentCommand != 'q')&&(currentCommand >= 0) );
			}
			finally
			{
				executer.shutdown();
				System.out.println("Quit");
			}
		}
		catch(Exception caught)
		{
			LOGGER.error("Exception was caught", caught);
			throw new RuntimeException("Throwable was caught", caught);
		}
		catch(Error caught)
		{
			LOGGER.error("Error was caught", caught);
			throw new Error("Throwable was caught", caught);
		}
	}

	private static void save() throws IOException, ClassNotFoundException
	{
		final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveLocation));
		try
		{
			out.writeObject(brain);
			out.writeObject(output);
			for (int ii = 0; ii < INPUTS; ii++)
			{
				out.writeObject(input[ii]);
			}
			out.flush();
		}
		finally
		{
			out.close();
		}

		LOGGER.debug("File Saved");
		System.out.println("File Saved");
	}

	private static void load() throws IOException, ClassNotFoundException
	{
		ObjectInputStream inStream = null;
		try
		{
			inStream = new ObjectInputStream(new FileInputStream(saveLocation));
		}
		catch(FileNotFoundException caught)
		{
			LOGGER.warn("the specified file does not exist!", caught);
			return;
		}

		try
		{
			brain = (FullyConnectedFeedforwardBrain) inStream.readObject();
			output = (OutputBackpropNeuron) inStream.readObject();
			for (int ii = 0; ii < INPUTS; ii++)
			{
				input[ii] = (InputBackpropNeuron) inStream.readObject();
			}
		}
		finally
		{
			inStream.close();
		}

		LOGGER.debug("File Loaded");
		System.out.println("File Loaded");
	}

	private static void propogateOutput()
	{
		brain.propagate();
	}

	private static void backPropogateTraining()
	{
		brain.backPropagate();
	}

	private static void setCurrentInput(final double[] inputToSet)
	{
		for (int ii = 0; ii < INPUTS; ii++)
		{
			input[ii].setInput(inputToSet[ii]);
		}
	}

	private static void testOutput()
	{
		double[] curInput =
		{
			0, 0, 0
		};
		setCurrentInput(curInput);
		propogateOutput();
		System.out.println(curInput[0] + ", " + curInput[1] + ", " + curInput[2] + ":\t" + output.getOutput());

		curInput[0] = 1;
		curInput[1] = 0;
		curInput[2] = 0;
		setCurrentInput(curInput);
		propogateOutput();
		System.out.println(curInput[0] + ", " + curInput[1] + ", " + curInput[2] + ":\t" + output.getOutput());

		curInput[0] = 0;
		curInput[1] = 1;
		curInput[2] = 0;
		setCurrentInput(curInput);
		propogateOutput();
		System.out.println(curInput[0] + ", " + curInput[1] + ", " + curInput[2] + ":\t" + output.getOutput());

		curInput[0] = 0;
		curInput[1] = 0;
		curInput[2] = 1;
		setCurrentInput(curInput);
		propogateOutput();
		System.out.println(curInput[0] + ", " + curInput[1] + ", " + curInput[2] + ":\t" + output.getOutput());

		curInput[0] = 1;
		curInput[1] = 1;
		curInput[2] = 0;
		setCurrentInput(curInput);
		propogateOutput();
		System.out.println(curInput[0] + ", " + curInput[1] + ", " + curInput[2] + ":\t" + output.getOutput());

		curInput[0] = 0;
		curInput[1] = 1;
		curInput[2] = 1;
		setCurrentInput(curInput);
		propogateOutput();
		System.out.println(curInput[0] + ", " + curInput[1] + ", " + curInput[2] + ":\t" + output.getOutput());

		curInput[0] = 1;
		curInput[1] = 0;
		curInput[2] = 1;
		setCurrentInput(curInput);
		propogateOutput();
		System.out.println(curInput[0] + ", " + curInput[1] + ", " + curInput[2] + ":\t" + output.getOutput());

		curInput[0] = 1;
		curInput[1] = 1;
		curInput[2] = 1;
		setCurrentInput(curInput);
		propogateOutput();
		System.out.println(curInput[0] + ", " + curInput[1] + ", " + curInput[2] + ":\t" + output.getOutput());
	}

	private static void train(final int count)
	{
		for (int lcv = 0; lcv < count; lcv++)
		{
			double[] curInput =
			{
				0, 0, 0
			};
			double curTrain = -1;
			setCurrentInput(curInput);
			propogateOutput();
			output.setDesired(curTrain);
			backPropogateTraining();

			curInput[0] = 1;
			curInput[1] = 0;
			curInput[2] = 0;
			curTrain = 1;
			setCurrentInput(curInput);
			propogateOutput();
			output.setDesired(curTrain);
			backPropogateTraining();

			curInput[0] = 0;
			curInput[1] = 1;
			curInput[2] = 0;
			curTrain = 1;
			setCurrentInput(curInput);
			propogateOutput();
			output.setDesired(curTrain);
			backPropogateTraining();

			curInput[0] = 0;
			curInput[1] = 0;
			curInput[2] = 1;
			curTrain = 1;
			setCurrentInput(curInput);
			propogateOutput();
			output.setDesired(curTrain);
			backPropogateTraining();

			curInput[0] = 1;
			curInput[1] = 1;
			curInput[2] = 0;
			curTrain = -1;
			setCurrentInput(curInput);
			propogateOutput();
			output.setDesired(curTrain);
			backPropogateTraining();

			curInput[0] = 0;
			curInput[1] = 1;
			curInput[2] = 1;
			curTrain = -1;
			setCurrentInput(curInput);
			propogateOutput();
			output.setDesired(curTrain);
			backPropogateTraining();

			curInput[0] = 1;
			curInput[1] = 0;
			curInput[2] = 1;
			curTrain = -1;
			setCurrentInput(curInput);
			propogateOutput();
			output.setDesired(curTrain);
			backPropogateTraining();

			curInput[0] = 1;
			curInput[1] = 1;
			curInput[2] = 1;
			curTrain = -1;
			setCurrentInput(curInput);
			propogateOutput();
			output.setDesired(curTrain);
			backPropogateTraining();
		}
	}
}
