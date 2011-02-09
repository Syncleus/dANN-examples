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
package com.syncleus.dann.examples.hyperassociativemap.visualization;

import com.syncleus.dann.graph.AbstractBidirectedAdjacencyGraph;
import com.syncleus.dann.graph.BidirectedEdge;
import com.syncleus.dann.graph.ImmutableUndirectedEdge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleGraph extends AbstractBidirectedAdjacencyGraph<SimpleNode, BidirectedEdge<SimpleNode>>
{
	private final SimpleNode[][] nodes;
	private final Set<SimpleNode> nodeSet = new HashSet<SimpleNode>();
	private final Set<BidirectedEdge<SimpleNode>> edges = new HashSet<BidirectedEdge<SimpleNode>>();
	private final Map<SimpleNode, Set<BidirectedEdge<SimpleNode>>> neighborEdges = new HashMap<SimpleNode, Set<BidirectedEdge<SimpleNode>>>();
	private final Map<SimpleNode, Set<SimpleNode>> neighborNodes = new HashMap<SimpleNode, Set<SimpleNode>>();

	public SimpleGraph(final int layers, final int nodesPerLayer)
	{
		this.nodes = new SimpleNode[layers][nodesPerLayer];

		//construct nodes
		for(int layerIndex = 0; layerIndex < layers; layerIndex++)
		{
			for(int nodeIndex = 0; nodeIndex < nodesPerLayer; nodeIndex++)
			{
				nodes[layerIndex][nodeIndex] = new SimpleNode(layerIndex);
				this.nodeSet.add(nodes[layerIndex][nodeIndex]);
				this.neighborEdges.put(nodes[layerIndex][nodeIndex], new HashSet<BidirectedEdge<SimpleNode>>());
				this.neighborNodes.put(nodes[layerIndex][nodeIndex], new HashSet<SimpleNode>());
			}
		}

		//connect nodes
		for(int layerIndex = 0; layerIndex < (layers-1); layerIndex++)
			for(int nodeIndex = 0; nodeIndex < nodesPerLayer; nodeIndex++)
			{
				for(int nodeIndex2 = 0; nodeIndex2 < nodesPerLayer; nodeIndex2++)
				{
					final ImmutableUndirectedEdge<SimpleNode> newEdge = new ImmutableUndirectedEdge<SimpleNode>(nodes[layerIndex][nodeIndex], nodes[layerIndex+1][nodeIndex2]);
					this.edges.add(newEdge);
					this.neighborEdges.get(nodes[layerIndex][nodeIndex]).add(newEdge);
					this.neighborNodes.get(nodes[layerIndex][nodeIndex]).add(nodes[layerIndex+1][nodeIndex2]);
					this.neighborEdges.get(nodes[layerIndex+1][nodeIndex2]).add(newEdge);
					this.neighborNodes.get(nodes[layerIndex+1][nodeIndex2]).add(nodes[layerIndex][nodeIndex]);
				}
			}
	}

	public SimpleNode[][] getNodeInLayers()
	{
		return this.nodes;
	}

	public SimpleNode getNode(final int layer, final int index)
	{
		if( (index >= nodes[0].length)||(layer >= nodes.length) )
			throw new IllegalArgumentException("coordinates are out of bounds");
		return this.nodes[layer][index];
	}

	@Override
	public Set<SimpleNode> getNodes()
	{
		return Collections.unmodifiableSet(this.nodeSet);
	}

	@Override
	public Set<BidirectedEdge<SimpleNode>> getEdges()
	{
		return Collections.unmodifiableSet(this.edges);
	}

	@Override
	public Set<BidirectedEdge<SimpleNode>> getAdjacentEdges(final SimpleNode node)
	{
		return Collections.unmodifiableSet(this.neighborEdges.get(node));
	}

	@Override
	public Set<BidirectedEdge<SimpleNode>> getTraversableEdges(final SimpleNode node)
	{
		return this.getAdjacentEdges(node);
	}

	public Set<BidirectedEdge<SimpleNode>> getOutEdges(final SimpleNode node)
	{
		return this.getAdjacentEdges(node);
	}

	@Override
	public Set<BidirectedEdge<SimpleNode>> getInEdges(final SimpleNode node)
	{
		return this.getAdjacentEdges(node);
	}

	public int getIndegree(final SimpleNode node)
	{
		return this.getInEdges(node).size();
	}

	public int getOutdegree(final SimpleNode node)
	{
		return this.getOutEdges(node).size();
	}

	public boolean isConnected(final SimpleNode leftNode, final SimpleNode rightNode)
	{
		return this.neighborNodes.get(leftNode).contains(rightNode);
	}

	@Override
	public List<SimpleNode> getAdjacentNodes(final SimpleNode node)
	{
		return Collections.unmodifiableList(new ArrayList<SimpleNode>(this.neighborNodes.get(node)));
	}

	@Override
	public List<SimpleNode> getTraversableNodes(final SimpleNode node)
	{
		return this.getAdjacentNodes(node);
	}
}
