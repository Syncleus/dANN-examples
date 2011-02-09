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
package com.syncleus.dann.examples.pathfind;

import com.syncleus.dann.graph.BidirectedEdge;
import com.syncleus.dann.graph.SimpleWeightedUndirectedEdge;
import com.syncleus.dann.graph.WeightedBidirectedEdge;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;

/**
 * Displays a WeightedGrid as a Swing component, with support for highlighting
 * individual nodes and edges (borders between nodes).
 * @author seh
 */
public abstract class AbstractGridCanvas extends JPanel implements MouseListener, MouseMotionListener
{
	/**
	 * Number of times smaller that the drawn path is than nodes which it runs
	 * through.
	 */
	private static final int PATH_FRACTION = 4;
	/**
	 * Number of times smaller that the drawn border highlighting is than nodes
	 * and edges which it surrounds.
	 */
	private static final int BORDER_FRACTION = 4;
	private static final Color PATH_COLOR = new Color(0.5f, 0f, 0f);
	private final WeightedGrid grid;
	private final int nodeSize;
	private final int edgeSize;
	private List<SimpleWeightedUndirectedEdge<GridNode>> path;
	private final int pathThickness;
	private GridNode touchedNode = null;
	private SimpleWeightedUndirectedEdge<GridNode> touchedEdge = null;
	private final int selectedThickness;

	/**
	 * Constructs a Swing component representing a grid and a path according to
	 * pixel-sizing parameters.
	 * @param shownGrid  the grid displayed by this component
	 * @param initialPath the initial path displayed by this component, which
	 *   may be null
	 * @param initialNodeSize size (in pixels) of each displayed node square
	 * @param initialEdgeSize size (in pixels) of the width or height of each
	 *   edge surrounding each node square
	 */
	public AbstractGridCanvas(final WeightedGrid shownGrid, final List<SimpleWeightedUndirectedEdge<GridNode>> initialPath, final int initialNodeSize, final int initialEdgeSize)
	{
		super();

		this.grid = shownGrid;
		this.nodeSize = initialNodeSize;
		this.edgeSize = initialEdgeSize;
		this.path = initialPath;
		this.pathThickness = Math.max(1, nodeSize / PATH_FRACTION);
		this.selectedThickness = Math.max(1, Math.min(edgeSize, nodeSize) / BORDER_FRACTION);

		final int preferredWidth = grid.getWidth() * nodeSize + (grid.getWidth() + 1) * edgeSize;
		final int preferredHeight = grid.getHeight() * nodeSize + (grid.getHeight() + 1) * edgeSize;
		setPreferredSize(new Dimension(preferredWidth, preferredHeight));

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	@Override
	public void paint(final Graphics graphics)
	{
		super.paint(graphics);

		final Graphics2D graphics2D = (Graphics2D) graphics;

		int px = 0;
		int py = 0;
		for (int y = 0; y < grid.getHeight(); y++)
		{
			px = 0;
			for (int x = 0; x < grid.getWidth(); x++)
			{
				final GridNode gridNode = grid.getNode(x, y);

				if (y != 0)
				{
					final GridNode toNode = grid.getNode(x, y - 1);

					//draw filled rect for edge
					final WeightedBidirectedEdge<GridNode> upEdge = grid.getEdgeBetween(x, y, x, y - 1);
					graphics2D.setColor(getEdgeColor(upEdge));
					graphics2D.fillRect(px + edgeSize, py, nodeSize, edgeSize);

					if ((touchedEdge != null) && (touchedEdge.getRightNode() == gridNode) && (touchedEdge.getLeftNode() == toNode))
					{
						//draw border of selected edge
						graphics2D.setStroke(new BasicStroke(selectedThickness));
						graphics2D.setColor(/*getTouchedNodeBorderColor()*/Color.ORANGE);
						graphics2D.drawRect(px + edgeSize, py, nodeSize, edgeSize);
					}
				}

				if (x != 0)
				{
					final GridNode toNode = grid.getNode(x - 1, y);

					//draw filled rect for edge
					final WeightedBidirectedEdge<GridNode> rightEdge = grid.getEdgeBetween(x, y, x - 1, y);
					graphics2D.setColor(getEdgeColor(rightEdge));
					graphics2D.fillRect(px, py + edgeSize, edgeSize, nodeSize);

					if ((touchedEdge != null) && (touchedEdge.getRightNode() == gridNode) && (touchedEdge.getLeftNode() == toNode))
					{
						//draw border of selected edge
						graphics2D.setStroke(new BasicStroke(selectedThickness));
						graphics2D.setColor(/*getTouchedNodeBorderColor()*/Color.ORANGE);
						graphics2D.drawRect(px, py + edgeSize, edgeSize, nodeSize);
					}
				}

				graphics2D.setColor(getNodeColor(gridNode));
				graphics2D.fillRect(px + edgeSize, py + edgeSize, nodeSize, nodeSize);

				if (gridNode == touchedNode)
				{
					graphics2D.setStroke(new BasicStroke(selectedThickness));
					graphics2D.setColor(/*getTouchedNodeBorderColor()*/Color.ORANGE);
					graphics2D.drawRect(px + edgeSize, py + edgeSize, nodeSize, nodeSize);
				}

				px += nodeSize + edgeSize;
			}
			py += nodeSize + edgeSize;
		}

		if (path != null)
		{
			graphics2D.setColor(PATH_COLOR);

			graphics2D.setStroke(new BasicStroke(pathThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

			final int centerOffset = (edgeSize + nodeSize / 2);

			for (BidirectedEdge<GridNode> edge : path)
			{
				final int lastX = edge.getLeftNode().getX();
				final int lastY = edge.getLeftNode().getY();

				final int curX = edge.getRightNode().getX();
				final int curY = edge.getRightNode().getY();

				final int curPX = getNodePosition(curX) + centerOffset;
				final int curPY = getNodePosition(curY) + centerOffset;

				final int lastPX = getNodePosition(lastX) + centerOffset;
				final int lastPY = getNodePosition(lastY) + centerOffset;

				graphics2D.drawLine(lastPX, lastPY, curPX, curPY);
			}
		}
	}

	/**
	 * Returns the color associated with a specific grid edge.
	 * @param edge the edge to get the color of
	 * @return the edge's color
	 */
	protected abstract Color getEdgeColor(WeightedBidirectedEdge<GridNode> edge);

	/**
	 * Returns the color associated with a specific grid node.
	 * @param node the node to get the color of
	 * @return the node's color
	 */
	protected abstract Color getNodeColor(GridNode node);

	/**
	 * Yields the pixel coordinate for a given grid coordinate of a node.  The coordinate may represent either X or Y, since the grid is drawn in 1:1 "square" aspect ratio.
	 * @param coordinate the grid coordinate
	 * @return pixel coordinate of a given grid position, which may be outside of the grid's bounds
	 */
	private int getNodePosition(final int coordinate)
	{
		return coordinate * (nodeSize + edgeSize);
	}

	/**
	 * Yields a node's grid coordinate for a given drawn pixel coordinate of a node.  The coordinate may represent either X or Y, since the grid is drawn in 1:1 "square" aspect ratio.
	 * @param x pixel coordinate
	 * @return grid coordinate of a given pixel position, which may be outside of the grid's bounds
	 */
	protected int getNodePositionPixel(final int x)
	{
		return (int) Math.floor(((float) x) / ((float) (nodeSize + edgeSize)));
	}

	/**
	 * Yields the touched edge corresponding to certain pixel coordinates in the drawn component.
	 * @param px x pixel coordinate
	 * @param py y pixel coordinate
	 * @return the edge corresponding to pixel (px, py), or null if no edge was drawn there
	 */
	public SimpleWeightedUndirectedEdge<GridNode> getTouchedEdge(final int px, final int py)
	{
		final int nx = getNodePositionPixel(px);
		final int ny = getNodePositionPixel(py);

		if (nx * (nodeSize + edgeSize) > px + edgeSize)
		{
			return null;
		}

		if (ny * (nodeSize + edgeSize) > py + edgeSize)
		{
			return null;
		}

		if (Math.abs((px - nx * (nodeSize + edgeSize)) - (py - ny * (nodeSize + edgeSize))) < edgeSize)
		{
			return null;
		}

		final boolean upOrLeft = (px - nx * (nodeSize + edgeSize) > py - ny * (nodeSize + edgeSize)) ? true : false;

		if ((nx >= (upOrLeft ? 0 : 1)) && (ny >= (!upOrLeft ? 0 : 1))
				&& (nx < grid.getWidth()) && (ny < grid.getHeight()))
		{
			final List<SimpleWeightedUndirectedEdge<GridNode>> thisEdges;
			final List<SimpleWeightedUndirectedEdge<GridNode>> otherEdges;
			thisEdges = new LinkedList(grid.getAdjacentEdges(grid.getNode(nx, ny)));
			//System.err.println(nx + " " + ny + " : " + thisEdges);

			if (upOrLeft)
			{
				//up
				otherEdges = new LinkedList(grid.getAdjacentEdges(grid.getNode(nx, ny - 1)));
				//System.err.println(nx + " " + (ny-1) + " : " + otherEdges);
			}
			else
			{
				//left
				otherEdges = new LinkedList(grid.getAdjacentEdges(grid.getNode(nx - 1, ny)));
				//System.err.println((nx-1) + " " + ny + " : " + otherEdges);
			}

			SimpleWeightedUndirectedEdge<GridNode> sharedEdge = null;
			for (SimpleWeightedUndirectedEdge<GridNode> eedge : thisEdges)
			{
				if (otherEdges.contains(eedge))
				{
					sharedEdge = eedge;
					break;
				}
			}

			if (sharedEdge == null)
			{
				return null;
			}
			else
			{
				return sharedEdge;
			}
		}

		return null;
	}

	/**
	 * Yields the touched node corresponding to certain pixel coordinates in the drawn component.
	 * @param px x pixel coordinate
	 * @param py y pixel coordinate
	 * @return the node corresponding to pixel (px, py), or null if no node was drawn there
	 */
	public GridNode getTouchedNode(final int px, final int py)
	{
		final int nx = getNodePositionPixel(px);
		final int ny = getNodePositionPixel(py);

		if (nx * (nodeSize + edgeSize) + edgeSize > px)
		{
			return null;
		}
		if (ny * (nodeSize + edgeSize) + edgeSize > py)
		{
			return null;
		}

		if ((nx >= 0) && (ny >= 0) && (nx < grid.getWidth()) && (ny < grid.getHeight()))
		{
			return grid.getNode(nx, ny);
		}

		return null;
	}

	@Override
	public void mouseClicked(final MouseEvent evt)
	{
		// unused
	}

	@Override
	public void mousePressed(final MouseEvent evt)
	{
		// unused
	}

	@Override
	public void mouseReleased(final MouseEvent evt)
	{
		// unused
	}

	@Override
	public void mouseEntered(final MouseEvent evt)
	{
		// unused
	}

	@Override
	public void mouseExited(final MouseEvent evt)
	{
		// unused
	}

	@Override
	public void mouseDragged(final MouseEvent evt)
	{
		updateMouseMoved(evt);
	}

	/**
	 * Updates the current touchedNode or touchedEdge when the mouse is either
	 * moved or dragged.
	 * @param evt mouse event to update according to
	 */
	protected void updateMouseMoved(final MouseEvent evt)
	{
		final GridNode tNode = getTouchedNode(evt.getX(), evt.getY());

		final SimpleWeightedUndirectedEdge<GridNode> tEdge = getTouchedEdge(evt.getX(), evt.getY());

		this.touchedNode = null;
		this.touchedEdge = null;

		if (tNode != null)
		{
			this.touchedNode = tNode;
		}
		else if (tEdge != null)
		{
			this.touchedEdge = tEdge;
		}

		//System.out.println("touchedNode: " + touchedNode + " , touchedEdge: " + touchedEdge);

		repaint();
	}

	@Override
	public void mouseMoved(final MouseEvent evt)
	{
		updateMouseMoved(evt);
	}

	/**
	 * Sets a different path to draw when the component is redrawn, and then
	 * repaint()'s.
	 * @param nextPath the next path to draw
	 */
	void setPath(final List<SimpleWeightedUndirectedEdge<GridNode>> nextPath)
	{
		this.path = nextPath;
		repaint();
	}

	/**
	 * Returns the currently pointer-touched grid Edge.
	 * @return currently touched edge, or null if none is touched
	 */
	public SimpleWeightedUndirectedEdge<GridNode> getTouchedEdge()
	{
		return touchedEdge;
	}

	/**
	 * Returns the currently pointer-touched GridNode.
	 * @return currently touched GridNode, or null if none is touched
	 */
	public GridNode getTouchedNode()
	{
		return touchedNode;
	}
}
