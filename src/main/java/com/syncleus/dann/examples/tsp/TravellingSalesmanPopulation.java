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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import com.syncleus.dann.genetics.AbstractGeneticAlgorithmPopulation;
import com.syncleus.dann.genetics.GeneticAlgorithmChromosome;
import com.syncleus.dann.math.Vector;

public class TravellingSalesmanPopulation extends AbstractGeneticAlgorithmPopulation
{
	private final Vector[] cities;

	public TravellingSalesmanPopulation(final Vector[] cities, final double mutationDeviation, final double crossoverPercentage, final double dieOffPercentage)
	{
		super(mutationDeviation, crossoverPercentage, dieOffPercentage);

		if(cities == null)
			throw new IllegalArgumentException("cities can not be null");
		if(cities.length < TravellingSalesmanFitnessFunction.MINIMUM_CITIES)
			throw new IllegalArgumentException("cities must have atleast " + TravellingSalesmanFitnessFunction.MINIMUM_CITIES + " elements");

		this.cities = cities.clone();
	}

	public TravellingSalesmanPopulation(final Vector[] cities, final double mutationDeviation, final double crossoverPercentage, final double dieOffPercentage, final ThreadPoolExecutor threadExecutor)
	{
		super(mutationDeviation, crossoverPercentage, dieOffPercentage, threadExecutor);

		if(cities == null)
			throw new IllegalArgumentException("cities can not be null");
		if(cities.length < TravellingSalesmanFitnessFunction.MINIMUM_CITIES)
			throw new IllegalArgumentException("cities must have atleast " + TravellingSalesmanFitnessFunction.MINIMUM_CITIES + " elements");

		this.cities = cities.clone();
	}

	public void initializePopulation(final int populationSize)
	{
		if(populationSize < TravellingSalesmanFitnessFunction.MINIMUM_CITIES)
			throw new IllegalArgumentException("populationSize must have atleast " + TravellingSalesmanFitnessFunction.MINIMUM_CITIES + " elements");

		this.addAll(initialChromosomes(cities.length, populationSize));
	}

	@Override
	protected TravellingSalesmanFitnessFunction packageChromosome(final GeneticAlgorithmChromosome chromosome)
	{
		if(!(chromosome instanceof TravellingSalesmanChromosome))
			throw new IllegalArgumentException("Chromosome must be a TravellingSalesmanChromosome");

		return new TravellingSalesmanFitnessFunction((TravellingSalesmanChromosome)chromosome, this.cities);
	}

	private static Set<GeneticAlgorithmChromosome> initialChromosomes(final int cityCount, final int populationSize)
	{
		if(populationSize < TravellingSalesmanFitnessFunction.MINIMUM_CITIES)
			throw new IllegalArgumentException("populationSize must have atleast " + TravellingSalesmanFitnessFunction.MINIMUM_CITIES + " elements");
		if(cityCount < TravellingSalesmanFitnessFunction.MINIMUM_CITIES)
			throw new IllegalArgumentException("cityCount must be atleast " + TravellingSalesmanFitnessFunction.MINIMUM_CITIES);

		final HashSet<GeneticAlgorithmChromosome> returnValue = new HashSet<GeneticAlgorithmChromosome>();
		while(returnValue.size() < populationSize)
			returnValue.add(new TravellingSalesmanChromosome(cityCount));
		return returnValue;
	}

	@Override
	public final TravellingSalesmanChromosome getWinner()
	{
		final GeneticAlgorithmChromosome winner = super.getWinner();
		assert(winner instanceof TravellingSalesmanChromosome);
		return (TravellingSalesmanChromosome) winner;
	}

	public Vector[] getCities()
	{
		return cities.clone();
	}
}
