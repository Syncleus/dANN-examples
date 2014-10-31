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

import com.syncleus.dann.graph.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Displays a WeightedGrid as a Swing component, with support for highlighting
 * individual nodes and edges (borders between nodes).
 *
 * @author seh
 */
public abstract class AbstractGridCanvas extends JPanel implements MouseListener, MouseMotionListener {
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
    private final int pathThickness;
    private final int selectedThickness;
    private List<SimpleWeightedUndirectedEdge<GridNode>> path;
    private GridNode touchedNode = null;
    private SimpleWeightedUndirectedEdge<GridNode> touchedEdge = null;

    /**
     * Constructs a Swing component representing a grid and a path according to
     * pixel-sizing parameters.
     *
     * @param shownGrid       the grid displayed by this component
     * @param initialPath     the initial path displayed by this component, which
     *                        may be null
     * @param initialNodeSize size (in pixels) of each displayed node square
     * @param initialEdgeSize size (in pixels) of the width or height of each
     *                        edge surrounding each node square
     */
    public AbstractGridCanvas(final WeightedGrid shownGrid, final List<SimpleWeightedUndirectedEdge<GridNode>> initialPath, final int initialNodeSize, final int initialEdgeSize) {
        super();

        this.grid = shownGrid;
        this.nodeSize = initialNodeSize;
        this.edgeSize = initialEdgeSize;
        this.path = initialPath;
        this.pathThickness = Math.max(1, this.nodeSize / PATH_FRACTION);
        this.selectedThickness = Math.max(1, Math.min(this.edgeSize, this.nodeSize) / BORDER_FRACTION);

        final int preferredWidth = this.grid.getWidth() * this.nodeSize + (this.grid.getWidth() + 1) * this.edgeSize;
        final int preferredHeight = this.grid.getHeight() * this.nodeSize + (this.grid.getHeight() + 1) * this.edgeSize;
        this.setPreferredSize(new Dimension(preferredWidth, preferredHeight));

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);

        final Graphics2D graphics2D = (Graphics2D) graphics;

        int px = 0;
        int py = 0;
        for (int y = 0; y < this.grid.getHeight(); y++) {
            px = 0;
            for (int x = 0; x < this.grid.getWidth(); x++) {
                final GridNode gridNode = this.grid.getNode(x, y);

                if (y != 0) {
                    final GridNode toNode = this.grid.getNode(x, y - 1);

                    //draw filled rect for edge
                    final WeightedBidirectedEdge<GridNode> upEdge = this.grid.getEdgeBetween(x, y, x, y - 1);
                    graphics2D.setColor(this.getEdgeColor(upEdge));
                    graphics2D.fillRect(px + this.edgeSize, py, this.nodeSize, this.edgeSize);

                    if ((this.touchedEdge != null) && (this.touchedEdge.getRightNode() == gridNode) && (this.touchedEdge.getLeftNode() == toNode)) {
                        //draw border of selected edge
                        graphics2D.setStroke(new BasicStroke(this.selectedThickness));
                        graphics2D.setColor(/*getTouchedNodeBorderColor()*/Color.ORANGE);
                        graphics2D.drawRect(px + this.edgeSize, py, this.nodeSize, this.edgeSize);
                    }
                }

                if (x != 0) {
                    final GridNode toNode = this.grid.getNode(x - 1, y);

                    //draw filled rect for edge
                    final WeightedBidirectedEdge<GridNode> rightEdge = this.grid.getEdgeBetween(x, y, x - 1, y);
                    graphics2D.setColor(this.getEdgeColor(rightEdge));
                    graphics2D.fillRect(px, py + this.edgeSize, this.edgeSize, this.nodeSize);

                    if ((this.touchedEdge != null) && (this.touchedEdge.getRightNode() == gridNode) && (this.touchedEdge.getLeftNode() == toNode)) {
                        //draw border of selected edge
                        graphics2D.setStroke(new BasicStroke(this.selectedThickness));
                        graphics2D.setColor(/*getTouchedNodeBorderColor()*/Color.ORANGE);
                        graphics2D.drawRect(px, py + this.edgeSize, this.edgeSize, this.nodeSize);
                    }
                }

                graphics2D.setColor(this.getNodeColor(gridNode));
                graphics2D.fillRect(px + this.edgeSize, py + this.edgeSize, this.nodeSize, this.nodeSize);

                if (gridNode == this.touchedNode) {
                    graphics2D.setStroke(new BasicStroke(this.selectedThickness));
                    graphics2D.setColor(/*getTouchedNodeBorderColor()*/Color.ORANGE);
                    graphics2D.drawRect(px + this.edgeSize, py + this.edgeSize, this.nodeSize, this.nodeSize);
                }

                px += this.nodeSize + this.edgeSize;
            }
            py += this.nodeSize + this.edgeSize;
        }

        if (this.path != null) {
            graphics2D.setColor(PATH_COLOR);

            graphics2D.setStroke(new BasicStroke(this.pathThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            final int centerOffset = (this.edgeSize + this.nodeSize / 2);

            for (BidirectedEdge<GridNode> edge : this.path) {
                final int lastX = edge.getLeftNode().getX();
                final int lastY = edge.getLeftNode().getY();

                final int curX = edge.getRightNode().getX();
                final int curY = edge.getRightNode().getY();

                final int curPX = this.getNodePosition(curX) + centerOffset;
                final int curPY = this.getNodePosition(curY) + centerOffset;

                final int lastPX = this.getNodePosition(lastX) + centerOffset;
                final int lastPY = this.getNodePosition(lastY) + centerOffset;

                graphics2D.drawLine(lastPX, lastPY, curPX, curPY);
            }
        }
    }

    /**
     * Returns the color associated with a specific grid edge.
     *
     * @param edge the edge to get the color of
     * @return the edge's color
     */
    protected abstract Color getEdgeColor(WeightedBidirectedEdge<GridNode> edge);

    /**
     * Returns the color associated with a specific grid node.
     *
     * @param node the node to get the color of
     * @return the node's color
     */
    protected abstract Color getNodeColor(GridNode node);

    /**
     * Yields the pixel coordinate for a given grid coordinate of a node.  The coordinate may represent either X or Y, since the grid is drawn in 1:1 "square" aspect ratio.
     *
     * @param coordinate the grid coordinate
     * @return pixel coordinate of a given grid position, which may be outside of the grid's bounds
     */
    private int getNodePosition(final int coordinate) {
        return coordinate * (this.nodeSize + this.edgeSize);
    }

    /**
     * Yields a node's grid coordinate for a given drawn pixel coordinate of a node.  The coordinate may represent either X or Y, since the grid is drawn in 1:1 "square" aspect ratio.
     *
     * @param x pixel coordinate
     * @return grid coordinate of a given pixel position, which may be outside of the grid's bounds
     */
    protected int getNodePositionPixel(final int x) {
        return (int) Math.floor(((float) x) / ((float) (this.nodeSize + this.edgeSize)));
    }

    /**
     * Yields the touched edge corresponding to certain pixel coordinates in the drawn component.
     *
     * @param px x pixel coordinate
     * @param py y pixel coordinate
     * @return the edge corresponding to pixel (px, py), or null if no edge was drawn there
     */
    public SimpleWeightedUndirectedEdge<GridNode> getTouchedEdge(final int px, final int py) {
        final int nx = this.getNodePositionPixel(px);
        final int ny = this.getNodePositionPixel(py);

        if (nx * (this.nodeSize + this.edgeSize) > px + this.edgeSize) {
            return null;
        }

        if (ny * (this.nodeSize + this.edgeSize) > py + this.edgeSize) {
            return null;
        }

        if (Math.abs((px - nx * (this.nodeSize + this.edgeSize)) - (py - ny * (this.nodeSize + this.edgeSize))) < this.edgeSize) {
            return null;
        }

        final boolean upOrLeft = (px - nx * (this.nodeSize + this.edgeSize) > py - ny * (this.nodeSize + this.edgeSize)) ? true : false;

        if ((nx >= (upOrLeft ? 0 : 1)) && (ny >= (!upOrLeft ? 0 : 1))
                    && (nx < this.grid.getWidth()) && (ny < this.grid.getHeight())) {
            final List<SimpleWeightedUndirectedEdge<GridNode>> thisEdges;
            final List<SimpleWeightedUndirectedEdge<GridNode>> otherEdges;
            thisEdges = new LinkedList(this.grid.getAdjacentEdges(this.grid.getNode(nx, ny)));
            //System.err.println(nx + " " + ny + " : " + thisEdges);

            if (upOrLeft) {
                //up
                otherEdges = new LinkedList(this.grid.getAdjacentEdges(this.grid.getNode(nx, ny - 1)));
                //System.err.println(nx + " " + (ny-1) + " : " + otherEdges);
            }
            else {
                //left
                otherEdges = new LinkedList(this.grid.getAdjacentEdges(this.grid.getNode(nx - 1, ny)));
                //System.err.println((nx-1) + " " + ny + " : " + otherEdges);
            }

            SimpleWeightedUndirectedEdge<GridNode> sharedEdge = null;
            for (SimpleWeightedUndirectedEdge<GridNode> eedge : thisEdges) {
                if (otherEdges.contains(eedge)) {
                    sharedEdge = eedge;
                    break;
                }
            }

            if (sharedEdge == null) {
                return null;
            }
            else {
                return sharedEdge;
            }
        }

        return null;
    }

    /**
     * Yields the touched node corresponding to certain pixel coordinates in the drawn component.
     *
     * @param px x pixel coordinate
     * @param py y pixel coordinate
     * @return the node corresponding to pixel (px, py), or null if no node was drawn there
     */
    public GridNode getTouchedNode(final int px, final int py) {
        final int nx = this.getNodePositionPixel(px);
        final int ny = this.getNodePositionPixel(py);

        if (nx * (this.nodeSize + this.edgeSize) + this.edgeSize > px) {
            return null;
        }
        if (ny * (this.nodeSize + this.edgeSize) + this.edgeSize > py) {
            return null;
        }

        if ((nx >= 0) && (ny >= 0) && (nx < this.grid.getWidth()) && (ny < this.grid.getHeight())) {
            return this.grid.getNode(nx, ny);
        }

        return null;
    }

    @Override
    public void mouseClicked(final MouseEvent evt) {
        // unused
    }

    @Override
    public void mousePressed(final MouseEvent evt) {
        // unused
    }

    @Override
    public void mouseReleased(final MouseEvent evt) {
        // unused
    }

    @Override
    public void mouseEntered(final MouseEvent evt) {
        // unused
    }

    @Override
    public void mouseExited(final MouseEvent evt) {
        // unused
    }

    @Override
    public void mouseDragged(final MouseEvent evt) {
        this.updateMouseMoved(evt);
    }

    /**
     * Updates the current touchedNode or touchedEdge when the mouse is either
     * moved or dragged.
     *
     * @param evt mouse event to update according to
     */
    protected void updateMouseMoved(final MouseEvent evt) {
        final GridNode tNode = this.getTouchedNode(evt.getX(), evt.getY());

        final SimpleWeightedUndirectedEdge<GridNode> tEdge = this.getTouchedEdge(evt.getX(), evt.getY());

        this.touchedNode = null;
        this.touchedEdge = null;

        if (tNode != null) {
            this.touchedNode = tNode;
        }
        else if (tEdge != null) {
            this.touchedEdge = tEdge;
        }

        //System.out.println("touchedNode: " + touchedNode + " , touchedEdge: " + touchedEdge);

        this.repaint();
    }

    @Override
    public void mouseMoved(final MouseEvent evt) {
        this.updateMouseMoved(evt);
    }

    /**
     * Sets a different path to draw when the component is redrawn, and then
     * repaint()'s.
     *
     * @param nextPath the next path to draw
     */
    void setPath(final List<SimpleWeightedUndirectedEdge<GridNode>> nextPath) {
        this.path = nextPath;
        this.repaint();
    }

    /**
     * Returns the currently pointer-touched grid Edge.
     *
     * @return currently touched edge, or null if none is touched
     */
    public SimpleWeightedUndirectedEdge<GridNode> getTouchedEdge() {
        return this.touchedEdge;
    }

    /**
     * Returns the currently pointer-touched GridNode.
     *
     * @return currently touched GridNode, or null if none is touched
     */
    public GridNode getTouchedNode() {
        return this.touchedNode;
    }
}
