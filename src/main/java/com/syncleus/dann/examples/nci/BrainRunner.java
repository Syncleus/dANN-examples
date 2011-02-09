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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import com.syncleus.dann.graph.drawing.hyperassociativemap.HyperassociativeMap;
import com.syncleus.dann.graph.drawing.hyperassociativemap.LayeredBrainHyperassociativeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import org.apache.log4j.Logger;

public class BrainRunner implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(BrainRunner.class);
	private static final Random RANDOM = new Random();
	private NciBrain brain;
	private HyperassociativeMap brainMap;
	private final double compression;
	private final int xSize;
	private final int ySize;
	private final boolean extraConnectivity;
	private BufferedImage[] trainingImages;
	private File[] trainingFiles;
	private BufferedImage sampleImage;
	private volatile File sampleFile;
	private final BrainListener listener;
	private volatile boolean keepRunning = true;
	private volatile int trainingRemaining = 0;
	private volatile int sampleRemaining;
	private volatile int sampleTotal;

	public BrainRunner(final BrainListener listener, final File[] trainingFiles, final double compression, final int xSize, final int ySize, final boolean extraConnectivity)
	{
		this.listener = listener;
		this.trainingFiles = trainingFiles.clone();
		this.compression = compression;
		this.xSize = xSize;
		this.ySize = ySize;
		this.extraConnectivity = extraConnectivity;
	}

	public HyperassociativeMap getBrainMap()
	{
		return this.brainMap;
	}

	public double getAverageAbsoluteWeights()
	{
		return this.brain.getAverageAbsoluteWeight();
	}

	public double getAverageWeights()
	{
		return this.brain.getAverageWeight();
	}

	public int getSampleProgress()
	{
		if (sampleTotal == 0)
		{
			return 100;
		}
		return ((sampleTotal - sampleRemaining) * 100) / sampleTotal;
	}

	public void setSampleImage(final File sampleFile)
	{
		this.sampleFile = sampleFile;
	}

	private void setTrainingImages(final File[] trainingFiles)
	{
		this.trainingFiles = trainingFiles;
		try
		{
			this.trainingImages = new BufferedImage[this.trainingFiles.length];
			for (int trainingFilesIndex = 0; trainingFilesIndex < trainingFiles.length; trainingFilesIndex++)
			{
				this.trainingImages[trainingFilesIndex] = ImageIO.read(trainingFiles[trainingFilesIndex]);
			}
		}
		catch (Exception exc)
		{
			System.out.println("Danger will robinson, Danger: " + exc);
			LOGGER.error("Failed reading training image", exc);
		}
	}

	public void setTrainingCycles(final int cycles)
	{
		this.trainingRemaining = cycles;
	}

	public int getTrainingCycles()
	{
		return this.trainingRemaining;
	}

	public void shutdown()
	{
		this.keepRunning = false;
	}

	public void stop()
	{
		this.trainingRemaining = 0;
		this.sampleFile = null;
	}

	@Override
	public void run()
	{
		ExecutorService executor = null;
		try
		{
			executor = Executors.newFixedThreadPool(1);

			this.brain = new NciBrain(this.compression, this.xSize, this.ySize, this.extraConnectivity);
			this.brainMap = new LayeredBrainHyperassociativeMap(brain, 3);
			this.setTrainingImages(trainingFiles);

			this.listener.brainFinishedBuffering();
			while (this.keepRunning)
			{

				if (this.sampleFile != null)
				{
					this.brain.setLearning(false);

					this.sampleImage = ImageIO.read(sampleFile);

					final ArrayBlockingQueue<FutureTask<BufferedImage>> processingSampleSegments = new ArrayBlockingQueue<FutureTask<BufferedImage>>(12000, true);

					this.sampleTotal = 0;
					stopProcessing:
					for (int currentY = 0; currentY < this.sampleImage.getHeight(); currentY += ySize)
					{
						for (int currentX = 0; currentX < this.sampleImage.getWidth(); currentX += xSize)
						{
							final int blockWidth = this.sampleImage.getWidth() - currentX < xSize ? this.sampleImage.getWidth() - currentX : xSize;
							final int blockHeight = this.sampleImage.getHeight() - currentY < ySize ? this.sampleImage.getHeight() - currentY : ySize;
							final BufferedImage currentSegment = this.sampleImage.getSubimage(currentX, currentY, blockWidth, blockHeight);

							final SampleRun sampleRun = new SampleRun(this.brain, currentSegment);
							final FutureTask<BufferedImage> futureSampleRun = new FutureTask<BufferedImage>(sampleRun);

							this.sampleTotal++;

							if (processingSampleSegments.remainingCapacity() <= 0)
							{
								System.out.println("The original image you selected is too large, aborting processing");
								break stopProcessing;
							}

							processingSampleSegments.add(futureSampleRun);
							executor.execute(futureSampleRun);
						}
					}

					this.sampleRemaining = this.sampleTotal;

					final BufferedImage finalImage = new BufferedImage(this.sampleImage.getWidth(), this.sampleImage.getHeight(), BufferedImage.TYPE_INT_RGB);

					this.sampleImage = null;
					this.sampleFile = null;

					int currentX = 0;
					int currentY = 0;
					while (processingSampleSegments.peek() != null)
					{
						final FutureTask<BufferedImage> nextSegment = processingSampleSegments.take();

						final BufferedImage currentSegment = nextSegment.get();

						final int writeWidth = (currentSegment.getWidth() < (finalImage.getWidth() - currentX) ? currentSegment.getWidth() : finalImage.getWidth() - currentX);
						final int writeHeight = (currentSegment.getHeight() < (finalImage.getHeight() - currentY) ? currentSegment.getHeight() : finalImage.getHeight() - currentY);
						final int[] chunkArray = new int[writeHeight * writeWidth];
						currentSegment.getRGB(0, 0, writeWidth, writeHeight, chunkArray, 0, writeWidth);
						finalImage.setRGB(currentX, currentY, writeWidth, writeHeight, chunkArray, 0, writeWidth);

						this.sampleRemaining--;

						if (currentX + writeWidth >= finalImage.getWidth())
						{
							currentX = 0;
							if (currentY + writeHeight >= finalImage.getHeight())
							{
								currentY = 0;
							}
							else
							{
								currentY += writeHeight;
							}
						}
						else
						{
							currentX += writeWidth;
						}
					}

					this.listener.brainSampleProcessed(finalImage);
				}
				else if (this.trainingRemaining > 0)
				{
					this.brain.setLearning(true);

					final ArrayBlockingQueue<FutureTask> trainingSegments = new ArrayBlockingQueue<FutureTask>(50, true);

					while (this.trainingRemaining > 0)
					{
						if (trainingSegments.remainingCapacity() <= 0)
						{
							final FutureTask currentTask = trainingSegments.take();
							currentTask.get();
							this.trainingRemaining--;
							if (this.trainingRemaining < 0)
							{
								this.trainingRemaining = 0;
							}
						}
						final TrainRun trainRun = new TrainRun(this.brain, this.getRandomTrainingBlock(xSize, ySize));
						final FutureTask<Void> trainTask = new FutureTask<Void>(trainRun, null);

						trainingSegments.add(trainTask);
						executor.execute(trainTask);
					}

					while (!trainingSegments.isEmpty())
					{
						final FutureTask currentTask = trainingSegments.take();
						currentTask.get();
						this.trainingRemaining--;
						if (this.trainingRemaining < 0)
						{
							this.trainingRemaining = 0;
						}
					}

					this.listener.brainTrainingComplete();
				}
				else if (this.brainMap.isAligned())
				{
					Thread.sleep(5);
				}
				else
				{
					this.brainMap.align();
				}
			}


		}
		catch (Exception caught)
		{
			LOGGER.error("Exception was caught", caught);
			throw new RuntimeException("Throwable was caught", caught);
		}
		catch (Error caught)
		{
			LOGGER.error("Error was caught", caught);
			throw new Error("Throwable was caught", caught);
		}
		finally
		{
			if (executor != null)
			{
				executor.shutdown();
			}
		}
	}

	private BufferedImage getRandomTrainingBlock(final int width, final int height) throws IndexOutOfBoundsException
	{
		final BufferedImage randomImage = this.getRandomTrainingImage();

		final int randomX = RANDOM.nextInt(randomImage.getWidth() - width);
		final int randomY = RANDOM.nextInt(randomImage.getHeight() - height);
		return randomImage.getSubimage(randomX, randomY, width, height);
	}

	private BufferedImage getRandomTrainingImage() throws IndexOutOfBoundsException
	{
		return this.trainingImages[RANDOM.nextInt(this.trainingImages.length)];
	}
}
