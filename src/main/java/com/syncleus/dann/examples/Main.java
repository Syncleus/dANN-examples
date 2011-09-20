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
package com.syncleus.dann.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Runs dANN demos from a console menu.
 * @author Jeffrey Phillips Freeman
 */
public final class Main
{
	private static final Logger LOGGER = Logger.getLogger(Main.class);
	/** How long to sleep in milli-seconds, until checking for input again. */
	private static final long INPUT_SLEEP = 100;

	private Main()
	{
	}

	public static void main(final String[] args)
	{
		try
		{
			if(new File("log4j.xml").exists())
				DOMConfigurator.configure("log4j.xml");
			else
			{
				final URL logConfig = ClassLoader.getSystemResource("log4j.xml");
				assert logConfig != null;
				DOMConfigurator.configure(logConfig);
			}

			LOGGER.info("program started...");

			final BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));

			final String[] newArgs = new String[Math.max(0, args.length - 1)];
			if (args.length > 1)
			{
				System.arraycopy(args, 1, newArgs, 0, args.length - 1);
			}

			String selectorArg = null;
			if (args.length > 0)
			{
				selectorArg = args[0];
			}

			if (selectorArg != null)
			{
				if (selectorArg.compareTo("--xor") == 0)
				{
					com.syncleus.dann.examples.xor.XorDemo.main(newArgs);
				}
				else if (selectorArg.compareTo("--nci") == 0)
				{
					com.syncleus.dann.examples.nci.ui.NciDemo.main(newArgs);
				}
				else if (selectorArg.compareTo("--colormap") == 0)
				{
					com.syncleus.dann.examples.colormap.ColorMapDemo.main(newArgs);
				}
				else if (selectorArg.compareTo("--hyperassociativemap") == 0)
				{
					com.syncleus.dann.examples.hyperassociativemap.visualization.ViewMap.main(newArgs);
				}
				else if (selectorArg.compareTo("--tsp") == 0)
				{
					com.syncleus.dann.examples.tsp.TravellingSalesmanDemo.main(newArgs);
				}
				else if (selectorArg.compareTo("--fft") == 0)
				{
					com.syncleus.dann.examples.fft.FftDemo.main(newArgs);
				}
			}

			System.out.println("dANN Example Sets");

			int currentCommand = 'q';
			do
			{
				boolean received = false;
				while (!received)
				{
					System.out.println();
					System.out.println("X) XOR Example");
					System.out.println("I) Image Compression Example w/GUI");
					System.out.println("V) Hyperassociative Map Visualizations");
					System.out.println("C) SOM Color Map");
					System.out.println("T) Travelling Salesman");
					System.out.println("F) Fast Fourier Transform Demo");
					System.out.println("P) Path Finding Demo");
					System.out.println("H) Command Line Help");
					System.out.println("Q) quit");
					System.out.println("\tEnter command: ");

					received = true;
					try
					{
						while (!inReader.ready())
						{
							Thread.sleep(INPUT_SLEEP);
						}
						currentCommand = inReader.readLine().toLowerCase().toCharArray()[0];
					}
					catch (ArrayIndexOutOfBoundsException caughtException)
					{
						received = false;
					}
				}

				System.out.println();

				switch (currentCommand)
				{
					case 'c':
						com.syncleus.dann.examples.colormap.ColorMapDemo.main(newArgs);
						break;
					case 'x':
						com.syncleus.dann.examples.xor.XorDemo.main(newArgs);
						break;
					case 'i':
						com.syncleus.dann.examples.nci.ui.NciDemo.main(newArgs);
						break;
					case 'h':
						System.out.println("The command line differs for each of the example files.");
						System.out.println();
						System.out.println("XOR Exmaple:");
						System.out.println("java -jar bin dANN-examples.jar --xor [save-location]");
						break;
					case 'v':
						com.syncleus.dann.examples.hyperassociativemap.visualization.ViewMap.main(newArgs);
						break;
					case 't':
						com.syncleus.dann.examples.tsp.TravellingSalesmanDemo.main(newArgs);
						break;
					case 'f':
						com.syncleus.dann.examples.fft.FftDemo.main(newArgs);
						break;
					case 'p':
						com.syncleus.dann.examples.pathfind.PathFindDemoPanel.main(newArgs);
						break;
					case 'q':
						System.out.print("Quiting...");
						break;
					default:
						System.out.println("Invalid command");
				}
			} while ((currentCommand != 'q') && (currentCommand >= 0));
			System.out.println("Quit");
		}
		catch (Exception caught)
		{
			LOGGER.error("A throwable was caught in the main execution thread", caught);
			throw new RuntimeException("An exception was caught", caught);
		}
		catch (Error caught)
		{
			LOGGER.error("A throwable was caught in the main execution thread", caught);
			throw new Error("Error caught", caught);
		}
	}
}
