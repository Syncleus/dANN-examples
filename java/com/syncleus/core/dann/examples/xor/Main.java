/******************************************************************************
*                                                                             *
*  Copyright: (c) Jeffrey Phillips Freeman                                    *
*                                                                             *
*  You may redistribute and modify this source code under the terms and       *
*  conditions of the Open Source Community License - Type C version 1.0       *
*  or any later version as published by syncleus at http://www.syncleus.com.  *
*  There should be a copy of the license included with this file. If a copy   *
*  of the license is not included you are granted no right to distribute or   *
*  otherwise use this file except through a legal and valid license. You      *
*  should also contact syncleus at the information below if you cannot find   *
*  a license:                                                                 *
*                                                                             *
*  Syncleus                                                                   *
*  1116 McClellan St.                                                         *
*  Philadelphia, PA 19148                                                     *
*                                                                             *
******************************************************************************/
package com.syncleus.core.dann.examples.xor;

import java.io.*;
import com.syncleus.dann.*;


/**
 * An example main class that shows using dANN to solve an XOR problem. An XOR
 * is a circuit that returns true (1) when only one of its inputs is true. It
 * returns false (-1) if none all of its inputs are false or if more then one
 * of its inputs are true.
 * <!-- Author: Jeffrey Phillips Freeman -->
 * @author Jeffrey Phillips Freeman
 */
public class Main
{
	private static DNA myDNA = new DNA();
	private static BufferedReader inReader = null;
	private static InputNeuronProcessingUnit inputA = null;
	private static InputNeuronProcessingUnit inputB = null;
	private static InputNeuronProcessingUnit inputC = null;
	private static LayerProcessingUnit firstLayer = null;
	private static LayerProcessingUnit secondLayer = null;
	private static OutputNeuronProcessingUnit output = null;
			
	public static void main(String args[])
	{
		try
		{
			inReader = new BufferedReader(new InputStreamReader(System.in));
        
			//creates the first layer which holds all the input neurons
			inputA = new InputNeuronProcessingUnit(myDNA);
			inputB = new InputNeuronProcessingUnit(myDNA);
			inputC = new InputNeuronProcessingUnit(myDNA);
			firstLayer = new LayerProcessingUnit(myDNA);
			firstLayer.add(inputA);
			firstLayer.add(inputB);
			firstLayer.add(inputC);
			
			//creates the second layer of neurons containing 10 neurons.
			secondLayer = new LayerProcessingUnit(myDNA);
			for( int lcv = 0; lcv < 6; lcv++ )
			{
				secondLayer.add(new NeuronProcessingUnit(myDNA));
			}
			
			//the output layer is just a single neuron
			output = new OutputNeuronProcessingUnit(myDNA);
			
			//connects the network in a feedforward fasion.
			firstLayer.connectAllTo(secondLayer);
			secondLayer.connectAllTo(output);

			//now that we have created the neural network lets put it to use.
			System.out.println("dANN nXOR Example");
			System.out.println();

			int currentCommand = 'q';
			do
			{
				System.out.println("d) display current circuit pin-out");
				System.out.println("t) train the current circuit");
				System.out.println("q) quit");
				System.out.print("\tEnter command: ");
				currentCommand = inReader.readLine().toLowerCase().toCharArray()[0];
				System.out.println();
				
				switch( currentCommand )
				{
					case 'd':
						testOutput();
						break;
					case 't':
						System.out.print("How many training cycles [Default: 1000]: ");
						int cycles = 1000;
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
					case 'q':
						System.out.println("Quiting...");
						break;
					default:
						System.out.println("Invalid command");
				}
			} while( (currentCommand != 'q')&&(currentCommand >= 0) );
		}
		catch(Exception caughtException)
		{
			caughtException.printStackTrace();
			throw new InternalError("Unhandled Exception: " + caughtException);
		}
	}
	
	private static void propogateOutput()
	{
		firstLayer.propogate();
		secondLayer.propogate();
		output.propogate();
	}
	
	private static void backPropogateTraining()
	{
		output.backPropogate();
		secondLayer.backPropogate();
		firstLayer.backPropogate();
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
