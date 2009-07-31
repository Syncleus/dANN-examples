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
package com.syncleus.core.dann.examples.xor;

import java.io.*;
import com.syncleus.dann.neural.backprop.*;
import com.syncleus.dann.neural.backprop.brain.*;
import com.syncleus.dann.neural.*;
import com.syncleus.dann.neural.activation.*;
import java.util.ArrayList;


/**
 * An example main class that shows using dANN to solve an XOR problem. An XOR
 * is a circuit that returns true (1) when only one of its inputs is true. It
 * returns false (-1) if none all of its inputs are false or if more then one
 * of its inputs are true.
 * <!-- Author: Jeffrey Phillips Freeman -->
 * @author Syncleus, Inc.
 */
public class Main
{
	private static BufferedReader inReader = null;
	private static InputBackpropNeuron inputA = null;
	private static InputBackpropNeuron inputB = null;
	private static InputBackpropNeuron inputC = null;
//	private static BackpropNeuronGroup firstLayer = null;
//	private static BackpropNeuronGroup secondLayer = null;
	private static OutputBackpropNeuron output = null;
	private static FullyConnectedFeedforwardBrain brain;
	private static String saveLocation = "default.dann";
			
	public static void main(String args[])
	{
		try
		{
			if( args.length > 0 )
				saveLocation = args[0];
			
			inReader = new BufferedReader(new InputStreamReader(System.in));
			
			//Adjust the learning rate
			double learningRate = 0.0175;
			ActivationFunction activationFunction = new SineActivationFunction();

			brain = new FullyConnectedFeedforwardBrain(new int[]{3, 3, 1}, learningRate, activationFunction);
			ArrayList<InputNeuron> inputs = new ArrayList<InputNeuron>(brain.getInputNeurons());
			inputA = (InputBackpropNeuron) inputs.get(0);
			inputB = (InputBackpropNeuron) inputs.get(1);
			inputC = (InputBackpropNeuron) inputs.get(2);
			ArrayList<OutputNeuron> outputs = new ArrayList<OutputNeuron>(brain.getOutputNeurons());
			output = (OutputBackpropNeuron) outputs.get(0);
			
			//creates the first layer which holds all the input neurons
//			inputA = new InputBackpropNeuron(activationFunction, learningRate);
//			inputB = new InputBackpropNeuron(activationFunction, learningRate);
//			inputC = new InputBackpropNeuron(activationFunction, learningRate);
//			firstLayer = new BackpropNeuronGroup();
//			firstLayer.add(inputA);
//			firstLayer.add(inputB);
//			firstLayer.add(inputC);

			//creates the second layer of neurons containing 10 neurons.
//			secondLayer = new BackpropNeuronGroup();
//			for( int lcv = 0; lcv < 3; lcv++ )
//			{
//				secondLayer.add(new BackpropNeuron(activationFunction, learningRate));
//			}

			//the output layer is just a single neuron
//			output = new OutputBackpropNeuron(activationFunction, learningRate);

			//connects the network in a feedforward fasion.
//			firstLayer.connectAllTo(secondLayer);
//			secondLayer.connectAllTo(output);

			//now that we have created the neural network lets put it to use.
			System.out.println("dANN nXOR Example");

			int currentCommand = 'q';
			do
			{
				boolean received = false;
				while( received == false )
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
						String lastInput = inReader.readLine();
						if( lastInput != null)
							currentCommand = lastInput.toLowerCase().toCharArray()[0];
						else
							currentCommand = 'q';
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
						System.out.println("Quiting...");
						break;
					default:
						System.out.println("Invalid command");
				}
			} while( (currentCommand != 'q')&&(currentCommand >= 0) );
		}
		catch(RuntimeException caughtException)
		{
			caughtException.printStackTrace();
			throw new InternalError("Unhandled RuntimeException: " + caughtException);
		}
		catch(Exception caughtException)
		{
			caughtException.printStackTrace();
			throw new InternalError("Unhandled Exception: " + caughtException);
		}
	}
	
	private static void save() throws IOException, ClassNotFoundException
	{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveLocation));
		try
		{
			out.writeObject(brain);
			out.writeObject(output);
			out.writeObject(inputA);
			out.writeObject(inputB);
			out.writeObject(inputC);
			out.flush();
		}
		finally
		{
			out.close();
		}
		
		System.out.println("File Saved");
	}
	
	private static void load() throws IOException, ClassNotFoundException
	{
		ObjectInputStream in = null;
		try
		{
			in = new ObjectInputStream(new FileInputStream(saveLocation));
		}
		catch(FileNotFoundException caughtException)
		{
			System.out.println("the specified file does not exist!");
			return;
		}
		
		try
		{
			brain = (FullyConnectedFeedforwardBrain) in.readObject();
			output = (OutputBackpropNeuron) in.readObject();
			inputA = (InputBackpropNeuron) in.readObject();
			inputB = (InputBackpropNeuron) in.readObject();
			inputC = (InputBackpropNeuron) in.readObject();
		}
		finally
		{
			in.close();
		}
		
		System.out.println("File Loaded");
	}
	
	private static void propogateOutput()
	{
		brain.propogate();
	}
	
	private static void backPropogateTraining()
	{
		brain.backPropogate();
	}
	
	private static void setCurrentInput(double[] inputToSet)
	{
		inputA.setInput(inputToSet[0]);
		inputB.setInput(inputToSet[1]);
		inputC.setInput(inputToSet[2]);
	}
	
	private static void testOutput()
	{
        double[] curInput = {0, 0, 0};
        setCurrentInput(curInput);
        propogateOutput();
        double[] curOutput;
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
	
	private static void train(int count)
	{
        for(int lcv = 0; lcv < count; lcv++)
        {
            double[] curInput = {0, 0, 0};
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
