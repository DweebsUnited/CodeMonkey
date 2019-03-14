/**
 * https://github.com/Cawfree/earcut-j
 *
 Copyright (c) 2015 Mapbox

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, self list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, self list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Poly2Tri nor the names of its contributors may be used to endorse or promote products derived from self software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Modifications by Frederik Vanhoutte
 *
 * - double instead of float
 * - adapted to data structures and WB_Coord class of HE_Mesh library
 * - reinsert collinear points and adapt triangulation
 *
 */

package wblut.geom;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Halfedge;
import wblut.math.WB_Epsilon;

public final class WB_Earcut {

	private static final Comparator<Node> COMPARATOR_SORT_BY_X = new Comparator<Node>() {
		@Override
		public int compare(final Node pNodeA, final Node pNodeB) {
			return pNodeA.xd() < pNodeB.xd() ? -1 : pNodeA.xd() == pNodeB.xd() ? 0 : 1;
		}
	};
	private static final int CONTRACT_HOLES_INDEX = 1;
	private static final int DEFAULT_THRESHOLD_SIMPLICITY = 80;
	private static final int DEFAULT_COORDINATE_RANGE = 1000;

	private static enum EEarcutState {
		INIT, CURE, SPLIT;
	}

	public static final int[] triangulate2D(final WB_Polygon polygon) {
		WB_Map2D map = new WB_OrthoProject(polygon.getPlane());
		int n = polygon.numberOfContours;
		WB_IndexedPoint[][] points = new WB_IndexedPoint[n][];
		int[] pointsPerContours = polygon.numberOfPointsPerContour;
		int id = 0;
		WB_Point result = new WB_Point();
		for (int i = 0; i < n; i++) {
			points[i] = new WB_IndexedPoint[pointsPerContours[i]];
			for (int j = 0; j < pointsPerContours[i]; j++) {
				map.mapPoint3D(polygon.getPoint(id), result);
				points[i][j] = new WB_IndexedPoint(result, id++);
			}

		}
		return earcut(points, true);

	}

	public static final long[] triangulate2Dkeys(final HE_Face face) {
		int n = face.getFaceDegree();
		WB_Map2D map = new WB_OrthoProject(face.getPlane());
		WB_IndexedPoint[][] points = new WB_IndexedPoint[1][n];
		HE_Halfedge he = face.getHalfedge();
		int i = 0;
		WB_Point result = new WB_Point();
		long[] keys = new long[n];
		do {
			map.mapPoint3D(he.getVertex(), result);
			keys[i] = he.getVertex().getKey();
			points[0][i] = new WB_IndexedPoint(result, i++);
			he = he.getNextInFace();
		} while (he != face.getHalfedge());
		int[] triangles = earcut(points, true);
		long[] keyedTriangles = new long[triangles.length];
		for (i = 0; i < triangles.length; i++) {
			keyedTriangles[i] = keys[triangles[i]];
		}

		return keyedTriangles;

	}

	public static final int[] triangulate2Dindices(final HE_Face face) {
		int n = face.getFaceDegree();
		WB_Map2D map = new WB_OrthoProject(face.getPlane());
		WB_IndexedPoint[][] points = new WB_IndexedPoint[1][n];
		HE_Halfedge he = face.getHalfedge();
		int i = 0;
		WB_Point result = new WB_Point();
		do {
			map.mapPoint3D(he.getVertex(), result);
			points[0][i] = new WB_IndexedPoint(result, i++);
			he = he.getNextInFace();
		} while (he != face.getHalfedge());

		return earcut(points, true);

	}

	private static final int[] earcut(final WB_IndexedPoint[][] pPoints, final boolean pIsClockwise) {
		Deque<Collinear> collinears = new ArrayDeque<Collinear>();
		/*
		 * Attempt to establish a doubly-linked list of the provided Points set,
		 * and then filter instances of intersections.
		 */
		Node lOuterNode = WB_Earcut.onFilterPoints(WB_Earcut.onCreateDoublyLinkedList(pPoints[0], pIsClockwise), null,
				false, collinears);
		/*
		 * If an outer node hasn't been detected, the input array is malformed.
		 */
		if (lOuterNode == null) {
			throw new EarcutException("Could not process shape! " + pPoints.length + pPoints[0].length);
		}
		/* Define the TriangleList. */
		final List<int[]> lTriangleList = new ArrayList<int[]>();
		/* Declare method dependencies. */
		Node lNode = null;
		double lMinimumX = 0;
		double lMinimumY = 0;
		double lMaximumX = 0;
		double lMaximumY = 0;
		double lCurrentX = 0;
		double lCurrentY = 0;
		double lBoundingBoxSize = 0;
		int lThreshold = WB_Earcut.DEFAULT_THRESHOLD_SIMPLICITY;

		/*
		 * Determine whether the specified array of points crosses the
		 * simplicity threshold.
		 */
		for (int i = 0; lThreshold >= 0 && i < pPoints.length; i++) {
			lThreshold -= pPoints[i].length;
		}

		/*
		 * If the shape crosses THRESHOLD_SIMPLICITY, we will use z-order curve
		 * hashing, which requires calculation the bounding box for the polygon.
		 */
		if (lThreshold < 0) {
			lNode = lOuterNode.getNextNode();
			lMinimumX = lMaximumX = lNode.xd();
			lMinimumY = lMaximumY = lNode.yd();
			/* Iterate through the doubly-linked list. */
			do {
				lCurrentX = lNode.xd();
				lCurrentY = lNode.yd();
				if (lCurrentX < lMinimumX) {
					lMinimumX = lCurrentX;
				}
				if (lCurrentY < lMinimumY) {
					lMinimumY = lCurrentY;
				}
				if (lCurrentX > lMaximumX) {
					lMaximumX = lCurrentX;
				}
				if (lCurrentY > lMaximumY) {
					lMaximumY = lCurrentY;
				}
				/*
				 * Iterate through to the mNextNode node in the doubly-linked
				 * list.
				 */
				lNode = lNode.getNextNode();
				/*
				 * Ensure that the doubly-linked list has not yet wrapped
				 * around.
				 */
			} while (lNode != lOuterNode);

			/*
			 * Calculate the BoundingBoxSize. (MinX, MinY and Size are used to
			 * transform co-ordinates into integers for the Z-Order calculation.
			 */
			lBoundingBoxSize = Math.max(lMaximumX - lMinimumX, lMaximumY - lMinimumY);
		}

		/* Determine if the specified list of points contains holes. */
		if (pPoints.length > WB_Earcut.CONTRACT_HOLES_INDEX) {
			/* Eliminate the hole triangulation. */
			lOuterNode = WB_Earcut.onEliminateHoles(pPoints, lOuterNode, lThreshold < 0, collinears);
		}

		if (lThreshold < 0) {
			/* Link polygon nodes in Z-Order. */
			WB_Earcut.onZIndexCurve(lOuterNode, lMinimumX, lMinimumY, lBoundingBoxSize);
		}
		/* Calculate an Earcut operation on the generated LinkedList. */
		List<int[]> list = WB_Earcut.onEarcutLinkedList(lOuterNode, lTriangleList, lMinimumX, lMinimumY,
				lBoundingBoxSize, EEarcutState.INIT, lThreshold < 0, collinears);
		list = reinsertCollinearPoints(list, collinears);
		int[] result = new int[3 * list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[3 * i] = list.get(i)[0];
			result[3 * i + 1] = list.get(i)[1];
			result[3 * i + 2] = list.get(i)[2];
		}
		return result;

	}

	/**
	 * Links every hole into the outer loop, producing a single-ring polygon
	 * without holes.
	 **/
	private static final Node onEliminateHoles(final WB_IndexedPoint[][] pPoints, Node lOuterNode,
			final boolean pIsZIndexed, final Deque<Collinear> collinears) {
		/* Define a list to hole a reference to each filtered hole list. */
		final List<Node> lHoleQueue = new ArrayList<Node>();
		/* Iterate through each array of hole vertices. */
		for (int i = WB_Earcut.CONTRACT_HOLES_INDEX; i < pPoints.length; i++) {
			/* Filter the doubly-linked hole list. */
			final Node lListNode = WB_Earcut.onFilterPoints(WB_Earcut.onCreateDoublyLinkedList(pPoints[i], false), null,
					pIsZIndexed, collinears);
			/* Determine if the resulting hole polygon was successful. */
			if (lListNode != null) {
				/* Add the leftmost vertex of the hole. */
				lHoleQueue.add(WB_Earcut.onFetchLeftmost(lListNode));
			}
		}
		/* Sort the hole vertices by increasing X. */
		Collections.sort(lHoleQueue, WB_Earcut.COMPARATOR_SORT_BY_X);
		/* Process holes from left to right. */
		for (int i = 0; i < lHoleQueue.size(); i++) {
			/* Eliminate hole triangles from the result set. */
			WB_Earcut.onEliminateHole(lHoleQueue.get(i), lOuterNode, pIsZIndexed, collinears);
			/* Filter the new polygon. */
			lOuterNode = WB_Earcut.onFilterPoints(lOuterNode, lOuterNode.getNextNode(), pIsZIndexed, collinears);
		}
		/* Return a pointer to the list. */
		return lOuterNode;
	}

	/**
	 * Finds a bridge between vertices that connects a hole with an outer ring,
	 * and links it.
	 **/
	private static final void onEliminateHole(final Node pHoleNode, Node pOuterNode, final boolean pIsZIndexed,
			final Deque<Collinear> collinears) {
		/*
		 * Attempt to find a logical bridge between the HoleNode and OuterNode.
		 */
		pOuterNode = WB_Earcut.onEberlyFetchHoleBridge(pHoleNode, pOuterNode);
		/* Determine whether a hole bridge could be fetched. */
		if (pOuterNode != null) {
			/* Split the resulting polygon. */
			Node lNode = WB_Earcut.onSplitPolygon(pOuterNode, pHoleNode);
			/* Filter the split nodes. */
			WB_Earcut.onFilterPoints(lNode, lNode.getNextNode(), pIsZIndexed, collinears);
		}
	}

	/**
	 * David Eberly's algorithm for finding a bridge between a hole and outer
	 * polygon.
	 **/
	private static final Node onEberlyFetchHoleBridge(final Node pHoleNode, final Node pOuterNode) {
		Node node = pOuterNode;
		Node p = pHoleNode;
		double px = p.xd();
		double py = p.yd();
		double qMax = Float.NEGATIVE_INFINITY;
		Node mNode = null;
		Node a, b;
		// find a segment intersected by a ray from the hole's leftmost point to
		// the left;
		// segment's endpoint with lesser x will be potential connection point
		do {
			a = node;
			b = node.getNextNode();
			if (py <= a.yd() && py >= b.yd()) {
				double qx = a.xd() + (py - a.yd()) * (b.xd() - a.xd()) / (b.yd() - a.yd());
				if (qx <= px && qx > qMax) {
					qMax = qx;
					mNode = a.xd() < b.xd() ? node : node.getNextNode();
				}
			}
			node = node.getNextNode();
		} while (node != pOuterNode);

		if (mNode == null) {
			return null;
		}
		// look for points strictly inside the triangle of hole point, segment
		// intersection and endpoint;
		// if there are no points found, we have a valid connection;
		// otherwise choose the point of the minimum angle with the ray as
		// connection point
		double bx = mNode.xd(), by = mNode.yd(), pbd = px * by - py * bx, pcd = px * py - py * qMax, cpy = py - py,
				pcx = px - qMax, pby = py - by, bpx = bx - px, A = pbd - pcd - (qMax * by - py * bx),
				sign = A <= 0 ? -1 : 1;
		Node stop = mNode;
		double tanMin = Float.POSITIVE_INFINITY, mx, my, amx, s, t, tan;
		node = mNode.getNextNode();
		while (node != stop) {
			mx = node.xd();
			my = node.yd();
			amx = px - mx;
			if (amx >= 0 && mx >= bx) {
				s = (cpy * mx + pcx * my - pcd) * sign;
				if (s >= 0) {
					t = (pby * mx + bpx * my + pbd) * sign;
					if (t >= 0 && A * sign - s - t >= 0) {
						tan = Math.abs(py - my) / amx; // tangential
						if (tan < tanMin && WB_Earcut.isLocallyInside(node, pHoleNode)) {
							mNode = node;
							tanMin = tan;
						}
					}
				}
			}
			node = node.getNextNode();
		}
		return mNode;
	}

	/** Finds the left-most hole of a polygon ring. **/
	private static final Node onFetchLeftmost(final Node pStart) {
		Node lNode = pStart;
		Node lLeftMost = pStart;
		do {
			/* Determine if the current node possesses a lesser X position. */
			if (lNode.xd() < lLeftMost.xd()) {
				/* Maintain a reference to this Node. */
				lLeftMost = lNode;
			}
			/*
			 * Progress the search to the next node in the doubly-linked list.
			 */
			lNode = lNode.getNextNode();
		} while (lNode != pStart);

		/* Return the node with the smallest X value. */
		return lLeftMost;
	}

	/**
	 * Main ear slicing loop which triangulates the vertices of a polygon,
	 * provided as a doubly-linked list.
	 **/
	private static final List<int[]> onEarcutLinkedList(Node lCurrentEar, final List<int[]> pTriangleList,
			final double pMinimumX, final double pMinimumY, final double pSize, final EEarcutState pEarcutState,
			final boolean pIsZIndexed, final Deque<Collinear> collinears) {
		if (lCurrentEar == null) {
			return pTriangleList;
		}

		Node lStop = lCurrentEar;
		Node lPreviousNode = null;
		Node lNextNode = null;

		/* Iteratively slice ears. */
		while (lCurrentEar.getPreviousNode() != lCurrentEar.getNextNode()) {
			lPreviousNode = lCurrentEar.getPreviousNode();
			lNextNode = lCurrentEar.getNextNode();

			/* Determine whether the current triangle must be cut off. */
			if (WB_Earcut.isEar(lCurrentEar, pMinimumX, pMinimumY, pSize, pIsZIndexed)) {
				/* Return the triangulated data back to the Callback. */
				pTriangleList.add(new int[] { lPreviousNode.getIndex(), lCurrentEar.getIndex(), lNextNode.getIndex() });
				/* Remove the ear node. */
				lNextNode.setPreviousNode(lPreviousNode);
				lPreviousNode.setNextNode(lNextNode);

				if (lCurrentEar.getPreviousZNode() != null) {
					lCurrentEar.getPreviousZNode().setNextZNode(lCurrentEar.getNextZNode());
				}
				if (lCurrentEar.getNextZNode() != null) {
					lCurrentEar.getNextZNode().setPreviousZNode(lCurrentEar.getPreviousZNode());
				}

				/* Skipping to the next node leaves less slither triangles. */
				lCurrentEar = lNextNode.getNextNode();
				lStop = lNextNode.getNextNode();

				continue;
			}

			lCurrentEar = lNextNode;

			/*
			 * If the whole polygon has been iterated over and no more ears can
			 * be found.
			 */
			if (lCurrentEar == lStop) {
				switch (pEarcutState) {
				case INIT:
					// try filtering points and slicing again
					WB_Earcut.onEarcutLinkedList(WB_Earcut.onFilterPoints(lCurrentEar, null, pIsZIndexed, collinears),
							pTriangleList, pMinimumX, pMinimumY, pSize, EEarcutState.CURE, pIsZIndexed, collinears);
					break;
				case CURE:
					// if this didn't work, try curing all small
					// self-intersections locally
					lCurrentEar = WB_Earcut.onCureLocalIntersections(lCurrentEar, pTriangleList);
					WB_Earcut.onEarcutLinkedList(lCurrentEar, pTriangleList, pMinimumX, pMinimumY, pSize,
							EEarcutState.SPLIT, pIsZIndexed, collinears);

					break;
				case SPLIT:
					// as a last resort, try splitting the remaining polygon
					// into two
					WB_Earcut.onSplitEarcut(lCurrentEar, pTriangleList, pMinimumX, pMinimumY, pSize, pIsZIndexed,
							collinears);
					break;
				}
				break;
			}
		}
		/* Return the calculated triangle vertices. */
		return pTriangleList;
	}

	/**
	 * Determines whether a polygon node forms a valid ear with adjacent nodes.
	 **/
	private static final boolean isEar(final Node pEar, final double pMinimumX, final double pMinimumY,
			final double pSize, final boolean pIsZIndexed) {

		double ax = pEar.getPreviousNode().xd(), bx = pEar.xd(), cx = pEar.getNextNode().xd(),
				ay = pEar.getPreviousNode().yd(), by = pEar.yd(), cy = pEar.getNextNode().yd(),

				abd = ax * by - ay * bx, acd = ax * cy - ay * cx, cbd = cx * by - cy * bx, A = abd - acd - cbd;

		if (A <= 0) {
			return false; // reflex, can't be an ear
		}

		// now make sure we don't have other points inside the potential ear;
		// the code below is a bit verbose and repetitive but this is done for
		// performance

		double cay = cy - ay, acx = ax - cx, aby = ay - by, bax = bx - ax;
		// int[] p;
		double px, py, s, t, k;
		Node node = null;

		// if we use z-order curve hashing, iterate through the curve
		if (pIsZIndexed) {

			// triangle bbox; min & max are calculated like this for speed
			double minTX = ax < bx ? ax < cx ? ax : cx : bx < cx ? bx : cx,
					minTY = ay < by ? ay < cy ? ay : cy : by < cy ? by : cy,
					maxTX = ax > bx ? ax > cx ? ax : cx : bx > cx ? bx : cx,
					maxTY = ay > by ? ay > cy ? ay : cy : by > cy ? by : cy,

					// z-order range for the current triangle bbox;
					minZ = WB_Earcut.onCalculateZOrder(minTX, minTY, pMinimumX, pMinimumY, pSize),
					maxZ = WB_Earcut.onCalculateZOrder(maxTX, maxTY, pMinimumX, pMinimumY, pSize);

			// first look for points inside the triangle in increasing z-order
			node = pEar.getNextZNode();

			while (node != null && node.getZOrder() <= maxZ) {

				px = node.xd();
				py = node.yd();

				node = node.getNextZNode();

				if (px == ax && py == ay || px == cx && py == cy) {
					continue;
				}

				s = cay * px + acx * py - acd;
				if (s >= 0) {
					t = aby * px + bax * py + abd;
					if (t >= 0) {
						k = A - s - t;

						;

						double term1 = s == 0 ? s : t;
						double term2 = s == 0 ? s : k;
						double term3 = t == 0 ? t : k;

						double calculation = term1 != 0 ? term1 : term2 != 0 ? term2 : term3;

						if (k >= 0 && calculation != 0) {
							return false;
						}
					}
				}
			}

			// then look for points in decreasing z-order
			node = pEar.getPreviousZNode();

			while (node != null && node.getZOrder() >= minZ) {

				px = node.xd();
				py = node.yd();

				node = node.getPreviousZNode();
				if (px == ax && py == ay || px == cx && py == cy) {
					continue;
				}

				s = cay * px + acx * py - acd;
				if (s >= 0) {
					t = aby * px + bax * py + abd;
					if (t >= 0) {
						k = A - s - t;

						double term1 = s == 0 ? s : t;
						double term2 = s == 0 ? s : k;
						double term3 = t == 0 ? t : k;

						double calculation = term1 != 0 ? term1 : term2 != 0 ? term2 : term3;

						if (k >= 0 && calculation != 0) {
							return false;
						}
					}
				}
			}

			// if we don't use z-order curve hash, simply iterate through all
			// other points
		} else {
			node = pEar.getNextNode().getNextNode();

			while (node != pEar.getPreviousNode()) {
				px = node.xd();
				py = node.yd();

				node = node.getNextNode();

				s = cay * px + acx * py - acd;
				if (s >= 0) {
					t = aby * px + bax * py + abd;
					if (t >= 0) {
						k = A - s - t;
						double term1 = s == 0 ? s : t;
						double term2 = s == 0 ? s : k;
						double term3 = t == 0 ? t : k;

						double calculation = term1 != 0 ? term1 : term2 != 0 ? term2 : term3;

						if (k >= 0 && calculation != 0) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Iterates through all polygon nodes and cures small local
	 * self-intersections.
	 **/
	private static final Node onCureLocalIntersections(Node pStartNode, final List<int[]> pTriangleList) {
		Node lNode = pStartNode;
		do {
			Node a = lNode.getPreviousNode(), b = lNode.getNextNode().getNextNode();

			// a self-intersection where edge (v[i-1],v[i]) intersects
			// (v[i+1],v[i+2])
			if (WB_Earcut.isIntersecting(a.xd(), a.yd(), lNode.xd(), lNode.yd(), lNode.getNextNode().xd(),
					lNode.getNextNode().yd(), b.xd(), b.yd()) && WB_Earcut.isLocallyInside(a, b)
					&& WB_Earcut.isLocallyInside(b, a)) {
				/* Return the triangulated vertices to the callback. */
				pTriangleList.add(new int[] { a.getIndex(), lNode.getIndex(), b.getIndex() });

				// remove two nodes involved
				a.setNextNode(b);
				b.setPreviousNode(a);

				Node az = lNode.getPreviousZNode();

				Node bz;

				if (lNode.getNextZNode() == null) {
					bz = lNode.getNextZNode();
				} else {
					bz = lNode.getNextZNode().getNextZNode();
				}

				if (az != null) {
					az.setNextZNode(bz);
				}
				if (bz != null) {
					bz.setPreviousZNode(az);
				}

				lNode = pStartNode = b;
			}
			lNode = lNode.getNextNode();
		} while (lNode != pStartNode);

		return lNode;
	}

	/** Tries to split a polygon and triangulate each side independently. **/
	private static final void onSplitEarcut(final Node pStart, final List<int[]> pTriangleList, final double pMinimumX,
			final double pMinimumY, final double pSize, final boolean pIsZIndexed, final Deque<Collinear> collinears) {
		/* Search for a valid diagonal that divides the polygon into two. */
		Node lSearchNode = pStart;
		do {
			Node lDiagonal = lSearchNode.getNextNode().getNextNode();
			while (lDiagonal != lSearchNode.getPreviousNode()) {
				if (WB_Earcut.isValidDiagonal(lSearchNode, lDiagonal)) {
					/*
					 * Split the polygon into two at the point of the diagonal.
					 */
					Node lSplitNode = WB_Earcut.onSplitPolygon(lSearchNode, lDiagonal);
					/* Filter the resulting polygon. */
					lSearchNode = WB_Earcut.onFilterPoints(lSearchNode, lSearchNode.getNextNode(), pIsZIndexed,
							collinears);
					lSplitNode = WB_Earcut.onFilterPoints(lSplitNode, lSplitNode.getNextNode(), pIsZIndexed,
							collinears);
					/* Attempt to earcut both of the resulting polygons. */
					WB_Earcut.onEarcutLinkedList(lSearchNode, pTriangleList, pMinimumX, pMinimumY, pSize,
							EEarcutState.INIT, pIsZIndexed, collinears);
					WB_Earcut.onEarcutLinkedList(lSplitNode, pTriangleList, pMinimumX, pMinimumY, pSize,
							EEarcutState.INIT, pIsZIndexed, collinears);
					/* Finish the iterative search. */
					return;
				}
				lDiagonal = lDiagonal.getNextNode();
			}
			lSearchNode = lSearchNode.getNextNode();
		} while (lSearchNode != pStart);
	}

	/** Links two polygon vertices using a bridge. **/
	private static final Node onSplitPolygon(final Node pNodeA, final Node pNodeB) {
		final Node a2 = new Node(pNodeA.xd(), pNodeA.yd(), pNodeA.getIndex());
		final Node b2 = new Node(pNodeB.xd(), pNodeB.yd(), pNodeB.getIndex());
		final Node an = pNodeA.getNextNode();
		final Node bp = pNodeB.getPreviousNode();

		pNodeA.setNextNode(pNodeB);
		pNodeB.setPreviousNode(pNodeA);
		a2.setNextNode(an);
		an.setPreviousNode(a2);
		b2.setNextNode(a2);
		a2.setPreviousNode(b2);
		bp.setNextNode(b2);
		b2.setPreviousNode(bp);

		return b2;
	}

	/**
	 * Determines whether a diagonal between two polygon nodes lies within a
	 * polygon interior. (This determines the validity of the ray.)
	 **/
	private static final boolean isValidDiagonal(final Node pNodeA, final Node pNodeB) {
		return !WB_Earcut.isIntersectingPolygon(pNodeA, pNodeA.xd(), pNodeA.yd(), pNodeB.xd(), pNodeB.yd())
				&& WB_Earcut.isLocallyInside(pNodeA, pNodeB) && WB_Earcut.isLocallyInside(pNodeB, pNodeA)
				&& WB_Earcut.onMiddleInsert(pNodeA, pNodeA.xd(), pNodeA.yd(), pNodeB.xd(), pNodeB.yd());
	}

	/**
	 * Determines whether a polygon diagonal rests locally within a polygon.
	 **/
	private static final boolean isLocallyInside(final Node pNodeA, final Node pNodeB) {
		return WB_Earcut.onCalculateWindingOrder(pNodeA.getPreviousNode().xd(), pNodeA.getPreviousNode().yd(),
				pNodeA.xd(), pNodeA.yd(), pNodeA.getNextNode().xd(),
				pNodeA.getNextNode().yd()) == WB_Classification.COUNTERCLOCKWISE
						? WB_Earcut.onCalculateWindingOrder(pNodeA.xd(), pNodeA.yd(), pNodeB.xd(), pNodeB.yd(),
								pNodeA.getNextNode().xd(),
								pNodeA.getNextNode().yd()) != WB_Classification.COUNTERCLOCKWISE
								&& WB_Earcut.onCalculateWindingOrder(pNodeA.xd(), pNodeA.yd(),
										pNodeA.getPreviousNode().xd(), pNodeA.getPreviousNode().yd(), pNodeB.xd(),
										pNodeB.yd()) != WB_Classification.COUNTERCLOCKWISE
						: WB_Earcut.onCalculateWindingOrder(pNodeA.xd(), pNodeA.yd(), pNodeB.xd(), pNodeB.yd(),
								pNodeA.getPreviousNode().xd(),
								pNodeA.getPreviousNode().yd()) == WB_Classification.COUNTERCLOCKWISE
								|| WB_Earcut.onCalculateWindingOrder(pNodeA.xd(), pNodeA.yd(),
										pNodeA.getNextNode().xd(), pNodeA.getNextNode().yd(), pNodeB.xd(),
										pNodeB.yd()) == WB_Classification.COUNTERCLOCKWISE;
	}

	/**
	 * Determines whether the middle point of a polygon diagonal is contained
	 * within the polygon.
	 **/
	private static final boolean onMiddleInsert(final Node pPolygonStart, final double pX0, final double pY0,
			final double pX1, final double pY1) {
		Node lNode = pPolygonStart;
		boolean lIsInside = false;
		double lDx = (pX0 + pX1) / 2.0f;
		double lDy = (pY0 + pY1) / 2.0f;
		do {
			if (lNode.yd() > lDy != lNode.getNextNode().yd() > lDy && lDx < (lNode.getNextNode().xd() - lNode.xd())
					* (lDy - lNode.yd()) / (lNode.getNextNode().yd() - lNode.yd()) + lNode.xd()) {
				lIsInside = !lIsInside;
			}
			lNode = lNode.getNextNode();
		} while (lNode != pPolygonStart);
		return lIsInside;
	}

	/**
	 * Determines if the diagonal of a polygon is intersecting with any polygon
	 * elements.
	 **/
	private static final boolean isIntersectingPolygon(final Node pStartNode, final double pX0, final double pY0,
			final double pX1, final double pY1) {
		Node lNode = pStartNode;
		do {
			if (lNode.xd() != pX0 && lNode.yd() != pY0 && lNode.getNextNode().xd() != pX0
					&& lNode.getNextNode().yd() != pY0 && lNode.xd() != pX1 && lNode.yd() != pY1
					&& lNode.getNextNode().xd() != pX1 && lNode.getNextNode().yd() != pY1
					&& WB_Earcut.isIntersecting(lNode.xd(), lNode.yd(), lNode.getNextNode().xd(),
							lNode.getNextNode().yd(), pX0, pY0, pX1, pY1)) {
				return true;
			}
			lNode = lNode.getNextNode();
		} while (lNode != pStartNode);

		return false;
	}

	/** Determines whether two segments intersect. **/
	private static final boolean isIntersecting(final double pX0, final double pY0, final double pX1, final double pY1,
			final double pX2, final double pY2, final double pX3, final double pY3) {
		return WB_Earcut.onCalculateWindingOrder(pX0, pY0, pX1, pY1, pX2, pY2) != WB_Earcut.onCalculateWindingOrder(pX0,
				pY0, pX1, pY1, pX3, pY3)
				&& WB_Earcut.onCalculateWindingOrder(pX2, pY2, pX3, pY3, pX0, pY0) != WB_Earcut
						.onCalculateWindingOrder(pX2, pY2, pX3, pY3, pX1, pY1);
	}

	/** Interlinks polygon nodes in Z-Order. **/
	private static final void onZIndexCurve(final Node pStartNode, final double pMinimumX, final double pMinimumY,
			final double pSize) {
		Node lNode = pStartNode;

		do {
			lNode.setZOrder(WB_Earcut.onCalculateZOrder(lNode.xd(), lNode.yd(), pMinimumX, pMinimumY, pSize));
			lNode.setPreviousZNode(lNode.getPreviousNode());
			lNode.setNextZNode(lNode.getNextNode());
			lNode = lNode.getNextNode();
		} while (lNode != pStartNode);

		lNode.getPreviousZNode().setNextZNode(null);
		lNode.setPreviousZNode(null);

		/* Sort the generated ring using Z ordering. */
		WB_Earcut.onTathamZSortList(lNode);
	}

	/**
	 * Simon Tatham's doubly-linked list merge/sort algorithm.
	 * (http://www.chiark.greenend.org.uk/~sgtatham/algorithms/listsort.html)
	 **/
	private static final Node onTathamZSortList(Node pList) {
		int i;
		Node p;
		Node q;
		Node e;
		Node tail;
		int numMerges;
		int pSize;
		int qSize;
		int inSize = 1;

		while (true) {
			p = pList;
			pList = null;
			tail = null;
			numMerges = 0;

			while (p != null) {
				numMerges++;
				q = p;
				pSize = 0;
				for (i = 0; i < inSize; i++) {
					pSize++;
					q = q.getNextZNode();
					if (q == null) {
						break;
					}
				}

				qSize = inSize;

				while (pSize > 0 || qSize > 0 && q != null) {

					if (pSize == 0) {
						e = q;
						q = q.getNextZNode();
						qSize--;
					} else if (qSize == 0 || q == null) {
						e = p;
						p = p.getNextZNode();
						pSize--;
					} else if (p.getZOrder() <= q.getZOrder()) {
						e = p;
						p = p.getNextZNode();
						pSize--;
					} else {
						e = q;
						q = q.getNextZNode();
						qSize--;
					}

					if (tail != null) {
						tail.setNextZNode(e);
					} else {
						pList = e;
					}

					e.setPreviousZNode(tail);
					tail = e;
				}

				p = q;
			}

			tail.setNextZNode(null);

			if (numMerges <= 1) {
				return pList;
			}

			inSize *= 2;
		}
	}

	/**
	 * Calculates the Z-Order of a given point given the vertex co-ordinates and
	 * size of the bounding box.
	 **/
	private static final int onCalculateZOrder(final double pX, final double pY, final double pMinimumX,
			final double pMinimumY, final double pSize) {
		/*
		 * Transform the co-ordinate set onto a (0 -> DEFAULT_COORDINATE_RANGE)
		 * Integer range.
		 */
		int lX = (int) (WB_Earcut.DEFAULT_COORDINATE_RANGE * (pX - pMinimumX) / pSize);
		lX = (lX | lX << 8) & 0x00FF00FF;
		lX = (lX | lX << 4) & 0x0F0F0F0F;
		lX = (lX | lX << 2) & 0x33333333;
		lX = (lX | lX << 1) & 0x55555555;
		int lY = (int) (WB_Earcut.DEFAULT_COORDINATE_RANGE * (pY - pMinimumY) / pSize);
		lY = (lY | lY << 8) & 0x00FF00FF;
		lY = (lY | lY << 4) & 0x0F0F0F0F;
		lY = (lY | lY << 2) & 0x33333333;
		lY = (lY | lY << 1) & 0x55555555;
		/* Returned the scaled co-ordinates. */
		return lX | lY << 1;
	}

	/**
	 * Creates a circular doubly linked list using polygon points. The order is
	 * governed by the specified winding order.
	 **/
	private static final Node onCreateDoublyLinkedList(final WB_IndexedPoint[] pPoints, final boolean pIsClockwise) {
		int lWindingSum = 0;
		WB_IndexedPoint p1;
		WB_IndexedPoint p2;
		Node lLastNode = null;

		/* Calculate the original order of the Polygon ring. */
		for (int i = 0, j = pPoints.length - 1; i < pPoints.length; j = i++) {
			p1 = pPoints[i];
			p2 = pPoints[j];
			lWindingSum += (p2.xd() - p1.xd()) * (p1.yd() + p2.yd());
		}
		/*
		 * Link points into the circular doubly-linked list in the specified
		 * winding order.
		 */
		if (pIsClockwise == lWindingSum > 0) {
			for (int i = 0; i < pPoints.length; i++) {
				lLastNode = WB_Earcut.onInsertNode(pPoints[i], lLastNode);
			}
		} else {
			for (int i = pPoints.length - 1; i >= 0; i--) {
				lLastNode = WB_Earcut.onInsertNode(pPoints[i], lLastNode);
			}
		}
		/* Return the last node in the Doubly-Linked List. */
		return lLastNode;
	}

	/** Eliminates colinear/duplicate points. **/
	private static final Node onFilterPoints(final Node pStartNode, Node pEndNode, final boolean pIsZIndexed,
			final Deque<Collinear> collinears) {
		new ArrayList<Node>();
		if (pEndNode == null) {
			pEndNode = pStartNode;
		}

		Node lNode = pStartNode;
		boolean lContinueIteration = false;

		do {
			lContinueIteration = false;
			boolean same = WB_Earcut.isVertexEquals(lNode.xd(), lNode.yd(), lNode.getNextNode().xd(),
					lNode.getNextNode().yd());
			boolean collinear = WB_Earcut.onCalculateWindingOrder(lNode.getPreviousNode().xd(),
					lNode.getPreviousNode().yd(), lNode.xd(), lNode.yd(), lNode.getNextNode().xd(),
					lNode.getNextNode().yd()) == WB_Classification.COLLINEAR;
			if (collinear) {
				collinears.push(new Collinear(lNode.getPreviousNode().getIndex(), lNode.getIndex(),
						lNode.getNextNode().getIndex()));
			}
			if (same || collinear) {

				/* Remove the node. */
				lNode.getPreviousNode().setNextNode(lNode.getNextNode());
				lNode.getNextNode().setPreviousNode(lNode.getPreviousNode());
				/* Remove the corresponding Z-Index nodes. */

				if (lNode.getPreviousZNode() != null) {
					lNode.getPreviousZNode().setNextZNode(lNode.getNextZNode());
				}
				if (lNode.getNextZNode() != null) {
					lNode.getNextZNode().setPreviousZNode(lNode.getPreviousZNode());
				}

				lNode = pEndNode = lNode.getPreviousNode();

				if (lNode == lNode.getNextNode()) {
					return null;
				}
				lContinueIteration = true;

			} else {
				lNode = lNode.getNextNode();
			}
		} while (lContinueIteration || lNode != pEndNode);

		return pEndNode;
	}

	/**
	 * Creates a node and optionally links it with a previous node in a circular
	 * doubly-linked list.
	 **/
	private static final Node onInsertNode(final WB_IndexedPoint p, final Node pLastNode) {
		final Node lNode = new Node(p);
		if (pLastNode == null) {
			lNode.setPreviousNode(lNode);
			lNode.setNextNode(lNode);

		} else {
			lNode.setNextNode(pLastNode.getNextNode());
			lNode.setPreviousNode(pLastNode);
			pLastNode.getNextNode().setPreviousNode(lNode);
			pLastNode.setNextNode(lNode);
		}
		return lNode;
	}

	/** Determines if two point vertices are equal. **/
	private static final boolean isVertexEquals(final double pX0, final double pY0, final double pX1,
			final double pY1) {
		return pX0 == pX1 && pY0 == pY1;
	}

	/** Calculates the WindingOrder for a set of vertices. **/
	private static final WB_Classification onCalculateWindingOrder(final double pX0, final double pY0, final double pX1,
			final double pY1, final double pX2, final double pY2) {
		final double lCross = (pY1 - pY0) * (pX2 - pX1) - (pX1 - pX0) * (pY2 - pY1);
		return lCross > WB_Epsilon.EPSILON ? WB_Classification.CLOCKWISE
				: lCross < -WB_Epsilon.EPSILON ? WB_Classification.COUNTERCLOCKWISE : WB_Classification.COLLINEAR;
	}

	/* Prevent instantiation of this class. */
	private WB_Earcut() {
	}

	final static class Node {

		/* Member Variables. */
		private final double x;
		private final double y;
		private final int id;
		private int zOrder;
		private Node previousNode;
		private Node nextNode;
		private Node previousZNode;
		private Node nextZNode;

		protected Node(final WB_IndexedPoint p) {
			/* Initialize Member Variables. */
			this.x = p.xd();
			this.y = p.yd();
			this.id = p.getIndex();
			this.zOrder = 0;
			this.previousNode = null;
			this.nextNode = null;
			this.previousZNode = null;
			this.nextZNode = null;
		}

		protected Node(final double x, final double y, final int id) {
			/* Initialize Member Variables. */
			this.x = x;
			this.y = y;
			this.id = id;
			this.zOrder = 0;
			this.previousNode = null;
			this.nextNode = null;
			this.previousZNode = null;
			this.nextZNode = null;
		}

		protected final double xd() {
			return this.x;
		}

		protected final double yd() {
			return this.y;
		}

		protected final int getIndex() {
			return this.id;
		}

		protected final void setPreviousNode(final Node pNode) {
			this.previousNode = pNode;
		}

		protected final Node getPreviousNode() {
			return this.previousNode;
		}

		protected final void setNextNode(final Node pNode) {
			this.nextNode = pNode;
		}

		protected final Node getNextNode() {
			return this.nextNode;
		}

		protected final void setZOrder(final int pZOrder) {
			this.zOrder = pZOrder;
		}

		protected final int getZOrder() {
			return this.zOrder;
		}

		protected final void setPreviousZNode(final Node pNode) {
			this.previousZNode = pNode;
		}

		protected final Node getPreviousZNode() {
			return this.previousZNode;
		}

		protected final void setNextZNode(final Node pNode) {
			this.nextZNode = pNode;
		}

		protected final Node getNextZNode() {
			return this.nextZNode;
		}

	}

	@SuppressWarnings("serial")
	public static class EarcutException extends RuntimeException {

		public EarcutException(final String pMessage) {
			super(pMessage);
		}

	}

	public static class Collinear {
		int start, end;
		int insert;

		public Collinear(final int i, final int j, final int k) {
			start = i;
			end = k;
			insert = j;

		}

	}

	private static List<int[]> reinsertCollinearPoints(final List<int[]> triangles, final Deque<Collinear> collinears) {
		Collinear coll;
		while (!collinears.isEmpty()) {
			coll = collinears.pop();
			for (int[] triangle : triangles) {
				int end = triangleContainsEdge(triangle, coll);
				if (end > -1) {
					int[] newTriangle = new int[3];
					newTriangle[end] = triangle[end];
					newTriangle[(end + 1) % 3] = triangle[(end + 1) % 3];
					newTriangle[(end + 2) % 3] = coll.insert;
					triangles.add(newTriangle);
					triangle[end] = coll.insert;
					break;
				}
			}

		}
		return triangles;
	}

	private static int triangleContainsEdge(final int[] triangle, final Collinear edge) {
		if (!(triangle[0] == edge.start || triangle[1] == edge.start || triangle[2] == edge.start)) {
			return -1;
		}
		if (triangle[0] == edge.end) {
			return 0;
		}
		if (triangle[1] == edge.end) {
			return 1;
		}
		if (triangle[2] == edge.end) {
			return 2;
		}

		return -1;

	}

}
