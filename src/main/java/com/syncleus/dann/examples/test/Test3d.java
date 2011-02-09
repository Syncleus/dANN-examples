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
package com.syncleus.dann.examples.test;

import javax.media.j3d.Canvas3D;
import com.syncleus.dann.genetics.wavelets.SignalProcessingWavelet;
import com.syncleus.dann.genetics.wavelets.SignalProcessingWavelet.GlobalSignalConcentration;
import com.syncleus.dann.math.visualization.MathFunctionCanvas;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test3d extends JFrame
{
	private JPanel drawingPanel;

	public Test3d()
	{
		this.initComponents();

		final Canvas3D canvas = this.createUniverse();
		try
		{
			this.drawingPanel.add(canvas, java.awt.BorderLayout.CENTER);
		}
		catch (ArithmeticException caughtException)
		{
			System.out.println("Division by 0!");
		}
	}

	private Canvas3D createUniverse()
	{
		final GlobalSignalConcentration signalX = new GlobalSignalConcentration();
		final GlobalSignalConcentration signalY = new GlobalSignalConcentration();
		final GlobalSignalConcentration signalZ = new GlobalSignalConcentration();
		SignalProcessingWavelet processor = new SignalProcessingWavelet(/*new Cell(),*/signalX, signalZ);
		for (int index = 0; (index < 500) || (processor.getSignals().size() < 3); index++)
		{
			processor = processor.mutate(100000.0, signalX);
			processor = processor.mutate(100000.0, signalY);
			processor = processor.mutate(100000.0);
		}

		System.out.println("The current equation contains " + processor.getWaveCount() + " waves:");
		System.out.println(processor.toString());

		processor.preTick();
		processor.tick();

		final MathFunctionCanvas plotCanvas = new MathFunctionCanvas(
				processor.getWavelet(),
				String.valueOf(signalX.getId()),
				String.valueOf(signalY.getId()),
				-200.0f,
				200.0f,
				-200.0f,
				200.0f,
				200);

		return plotCanvas;
	}

	private void initComponents()
	{
		this.drawingPanel = new javax.swing.JPanel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Hello Universe");
		this.drawingPanel.setLayout(new java.awt.BorderLayout());

		this.drawingPanel.setPreferredSize(new java.awt.Dimension(250, 250));
		this.getContentPane().add(this.drawingPanel, java.awt.BorderLayout.CENTER);

		this.pack();
	}

	public static void main(final String[] args)
	{
		java.awt.EventQueue.invokeLater(
				new Runnable()
				{
					@Override
					public void run()
					{
						for (int index = 0; index < 1; index++)
						{
							new Test3d().setVisible(true);
						}
					}
				});
	}
}
