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

import java.util.LinkedList;
import java.util.List;
import com.syncleus.dann.graph.SimpleWeightedUndirectedEdge;
import com.syncleus.dann.graph.WeightedBidirectedEdge;
import com.syncleus.dann.graph.search.pathfinding.AstarPathFinder;
import com.syncleus.dann.graph.search.pathfinding.HeuristicPathCost;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

/**
 * Demonstrates Path Finding across a Weighted Grid.
 * The Grid's node and edge weights can be adjusted by clicking.
 * The start and stop positions of the path to be found can also be specified.
 * @author seh
 */
public class PathFindDemoPanel extends JPanel
{
	private AbstractGridCanvas gridCanvas;
	private WeightedGrid grid;
	private PathFindControlPanel controlPanel;
	private List<SimpleWeightedUndirectedEdge<GridNode>> path;
	private GridNode endNode;
	private GridNode startNode;
	private double paintWeight = 0.0;
	private static final double INF = Double.POSITIVE_INFINITY;
	private static final double MAX_NONINFINITE_CELL_WEIGHT = 12.0;
	private static final int DEFAULT_NODE_SIZE = 24;
	private static final int DEFAULT_EDGE_SIZE = 8;
	private static final int DEFAULT_GRID_SIZE = 18;
	private static final double MIN_GRID_WEIGHT = 1.0;
	private static final double INITIAL_GRID_WEIGHT = MIN_GRID_WEIGHT;
	private static final double[] PAINT_WEIGHTS =
	{
		1, 2, 4, 6, 8, 10, INF
	};

	/**
	 * Distance heuristic used by the AStarPathFinding algorithm.
	 */
	private static class DistanceHeuristic implements HeuristicPathCost<GridNode>
	{
		@Override
		public double getHeuristicPathCost(final GridNode begin, final GridNode end)
		{
			return begin.calculateRelativeTo(end).getDistance();
		}

		@Override
		public boolean isOptimistic()
		{
			return true;
		}

		@Override
		public boolean isConsistent()
		{
			return true;
		}
	}

	private static class PaintButtonActionListener implements ActionListener
	{
		private final PathFindDemoPanel pathFindDemoPanel;

		public PaintButtonActionListener(final PathFindDemoPanel pathFindDemoPanel)
		{
			this.pathFindDemoPanel = pathFindDemoPanel;
		}

		@Override
		public void actionPerformed(final ActionEvent evt)
		{
			if (evt.getSource() instanceof PaintButton)
			{
				final PaintButton button = (PaintButton) evt.getSource();
				pathFindDemoPanel.paintWeight = button.getWeight();
			}
		}
	}

	private static class PaintButton extends JToggleButton
	{
		private static final int BUTTON_BORDER_SIZE = 4;
		private final double weight;

		public PaintButton(final double drawWeight)
		{
			super("  ");
			this.weight = drawWeight;
			setForeground(getColor(weight));
		}

		@Override
		public void paint(final Graphics graphics)
		{
			super.paint(graphics);
			final Graphics2D graphics2D = (Graphics2D) graphics;
			graphics2D.setColor(getColor(weight));
			graphics2D.fillRect(BUTTON_BORDER_SIZE, BUTTON_BORDER_SIZE,
					getWidth() - BUTTON_BORDER_SIZE * 2,
					getHeight() - BUTTON_BORDER_SIZE * 2);
		}

		public double getWeight()
		{
			return weight;
		}
	}

	/**
	 * A panel that provides buttons for each of the drawable "weights".
	 */
	private static class DrawingPanel extends JPanel
	{
		private final List<PaintButton> paintButtons = new LinkedList();
		private final ButtonGroup paintButtonsGroup = new ButtonGroup();

		public DrawingPanel(final double[] paintWeights, final PathFindDemoPanel pathFindDemoPanel)
		{
			super(new FlowLayout(FlowLayout.LEFT));

			final PaintButtonActionListener paintButtonActionListener = new PaintButtonActionListener(pathFindDemoPanel);
			for (double d : paintWeights)
			{
				final PaintButton button = new PaintButton(d);
				button.addActionListener(paintButtonActionListener);
				paintButtonsGroup.add(button);
				paintButtons.add(button);
				add(button);
			}
		}

		public List<PaintButton> getPaintButtons()
		{
			return paintButtons;
		}
	}

	private static class PathFindControlPanel extends JTabbedPane
	{
		public PathFindControlPanel(final PathFindDemoPanel pathFindDemoPanel)
		{
			addTab("Edit", new DrawingPanel(PAINT_WEIGHTS, pathFindDemoPanel));
			addTab("Start Position", new JLabel("Click a start position"));
			addTab("Stop Position", new JLabel("Click a stop position"));
			//addTab("Settings", new SettingsPanel());
		}
	}

	/**
	 * Creates a new Swing component with an empty square grid of size
	 * width*width.
	 * @param width number of grid nodes wide and high
	 */
	public PathFindDemoPanel(final int width)
	{
		super(new BorderLayout());

		reinit(width);
	}

	/**
	 * Reconstructs the grid and the panel (and its components) with new grid
	 * dimensions.
	 * @param nextWidth the width and height (square) dimensions of the grid to
	 *   (re-)initialize.
	 */
	protected void reinit(final int nextWidth)
	{
		removeAll();

		final int width = nextWidth;
		final int height = nextWidth;

		final double[][] gridWeights = new double[height][width];
		grid = new WeightedGrid(gridWeights);
		grid.setAll(INITIAL_GRID_WEIGHT);

		startNode = grid.getNode(0, 0);
		endNode = grid.getNode(width - 1, height - 1);

		updatePath();

		controlPanel = new PathFindControlPanel(this);
		add(controlPanel, BorderLayout.NORTH);

		gridCanvas = new AbstractGridCanvas(grid, path, DEFAULT_NODE_SIZE, DEFAULT_EDGE_SIZE)
		{
			private boolean mouseDown = false;

			@Override
			public Color getNodeColor(final GridNode node)
			{
				return getColor(node.getWeight());
			}

			@Override
			protected Color getEdgeColor(final WeightedBidirectedEdge<GridNode> edge)
			{
				return getColor(edge.getWeight());
			}

			protected void paintCells()
			{
				final int selectedIndex = controlPanel.getSelectedIndex();

				if (mouseDown && (selectedIndex == 0))
				{
					if (getTouchedNode() != null)
					{
						getTouchedNode().setWeight(paintWeight);
						updatePath();
					}
					else if (getTouchedEdge() != null)
					{
						getTouchedEdge().setWeight(paintWeight);
						updatePath();
					}
				}

			}

			@Override
			public void mouseDragged(final MouseEvent evt)
			{
				super.mouseDragged(evt);
				paintCells();
			}

			@Override
			public void mouseMoved(final MouseEvent evt)
			{
				super.mouseMoved(evt);
				paintCells();
			}

			@Override
			public void mousePressed(final MouseEvent evt)
			{
				super.mousePressed(evt);
				mouseDown = true;
			}

			@Override
			public void mouseReleased(final MouseEvent evt)
			{
				super.mouseReleased(evt);
				mouseDown = false;
			}

			@Override
			public void mouseClicked(final MouseEvent evt)
			{
				final int selectedIndex = controlPanel.getSelectedIndex();

				final GridNode touchedNode = getTouchedNode();

				//starting position
				if ((selectedIndex == 1) && (touchedNode != null))
				{
					if (getTouchedNode() != endNode)
					{
						startNode = touchedNode;
						updatePath();
					}
					else
					{
						warnDifferentLocations();
					}
				} //ending position
				else if ((selectedIndex == 2) && (touchedNode != null))
				{
					if (touchedNode != startNode)
					{
						endNode = touchedNode;
						updatePath();
					}
					else
					{
						warnDifferentLocations();
					}
				} //paint the map

				mouseDown = true;
				paintCells();
				mouseDown = false;
			}

			private void warnDifferentLocations()
			{
				JOptionPane.showMessageDialog(this, "Start and stop locations must be different");
			}
		};

		add(new JScrollPane(gridCanvas), BorderLayout.CENTER);
	}

	/**
	 * Returns a color associated with a specific weight value.
	 * This is used to draw a gray-scale gradient for the grid's weights.
	 * @param weight value
	 * @return a color value
	 */
	public static Color getColor(final double weight)
	{
		Color color;

		if (weight == MIN_GRID_WEIGHT)
		{
			color = Color.WHITE;
		}
		else if (weight == Double.POSITIVE_INFINITY)
		{
			color = Color.BLACK;
		}
		else if (weight < MAX_NONINFINITE_CELL_WEIGHT)
		{
			final float chanVal = 1f - (float) (weight / MAX_NONINFINITE_CELL_WEIGHT) / 2f;
			color = new Color(chanVal, chanVal, chanVal);
		}
		else
		{
			color = Color.WHITE;
		}

		return color;
	}

	/**
	 * Updates the path when the starting or ending node changes, or when the
	 * AStarPathFinder parameters are changed.
	 */
	protected void updatePath()
	{
		final AstarPathFinder<GridNode, SimpleWeightedUndirectedEdge<GridNode>> pathFinder = new AstarPathFinder<GridNode, SimpleWeightedUndirectedEdge<GridNode>>(grid, new DistanceHeuristic());
		path = pathFinder.getBestPath(startNode, endNode);
		if (gridCanvas != null)
		{
			gridCanvas.setPath(path);
		}
	}

	/**
	 * Entrypoint.
	 * @param args (not presently used)
	 */
	public static void main(final String[] args)
	{
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				final JFrame frame = new JFrame("dANN Path Finding Demo");
				frame.getContentPane().add(new PathFindDemoPanel(DEFAULT_GRID_SIZE));
				frame.addWindowListener(new WindowAdapter()
				{

					@Override
					public void windowClosing(final WindowEvent evt)
					{
						System.exit(0);
					}
				});
				frame.setVisible(true);
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

			}
		});
	}

	static
	{
		// Install the look and feel
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception exc)
		{
		}
	}
}
