/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 * 
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 * 
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.geom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import wblut.hemesh.HEC_Archimedes;
import wblut.hemesh.HEM_ChamferCorners;
import wblut.hemesh.HEM_Crocodile;
import wblut.hemesh.HEM_Extrude;
import wblut.hemesh.HEM_Lattice;
import wblut.hemesh.HEM_Slice;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Halfedge;
import wblut.hemesh.HE_Mesh;
import wblut.math.WB_Epsilon;

/**
 *
 */
public class WB_PolygonTriangulatorJTS {
	/**
	 *
	 */
	private final WB_GeometryFactory geometryfactory = new WB_GeometryFactory();

	/**
	 *
	 */
	public WB_PolygonTriangulatorJTS() {
	}

	/**
	 *
	 *
	 * @param p0
	 * @param p1
	 * @param p2
	 * @param p3
	 * @return
	 */
	public static int[] triangulateQuad(final WB_Coord p0, final WB_Coord p1, final WB_Coord p2, final WB_Coord p3) {
		final boolean p0inside = WB_GeometryOp3D.pointInTriangleBary3D(p0, p1, p2, p3);
		if (p0inside) {
			return new int[] { 0, 1, 2, 0, 2, 3 };
		}
		final boolean p2inside = WB_GeometryOp3D.pointInTriangleBary3D(p2, p1, p0, p3);
		if (p2inside) {
			return new int[] { 0, 1, 2, 0, 2, 3 };
		}
		return new int[] { 0, 1, 3, 1, 2, 3 };

	}

	/**
	 * @author Michael Bedward
	 */
	private static class EdgeFlipper {
		private final List<Coordinate> shellCoords;

		/**
		 *
		 *
		 * @param shellCoords
		 */
		EdgeFlipper(final List<Coordinate> shellCoords) {
			this.shellCoords = Collections.unmodifiableList(shellCoords);
		}

		/**
		 *
		 *
		 * @param ear0
		 * @param ear1
		 * @param sharedVertices
		 * @return
		 */
		public boolean flip(final IndexedTriangle ear0, final IndexedTriangle ear1, final int[] sharedVertices) {
			if (sharedVertices == null || sharedVertices.length != 2) {
				return false;
			}
			final Coordinate shared0 = shellCoords.get(sharedVertices[0]);
			final Coordinate shared1 = shellCoords.get(sharedVertices[1]);

			int[] vertices = ear0.getVertices();
			int i = 0;
			while (vertices[i] == sharedVertices[0] || vertices[i] == sharedVertices[1]) {
				i++;
			}
			final int v0 = vertices[i];
			boolean reverse = false;
			if (vertices[(i + 1) % 3] == sharedVertices[0]) {
				reverse = true;
			}
			final Coordinate c0 = shellCoords.get(v0);
			i = 0;
			vertices = ear1.getVertices();
			while (vertices[i] == sharedVertices[0] || vertices[i] == sharedVertices[1]) {
				i++;
			}
			final int v1 = vertices[i];
			final Coordinate c1 = shellCoords.get(v1);
			final int dir0 = CGAlgorithms.orientationIndex(c0, c1, shared0);
			final int dir1 = CGAlgorithms.orientationIndex(c0, c1, shared1);
			if (dir0 == -dir1) {
				if (c0.distance(c1) < shared0.distance(shared1)) {
					if (reverse) {
						ear0.setPoints(sharedVertices[0], v1, v0);
						ear1.setPoints(v0, v1, sharedVertices[1]);
					} else {
						ear0.setPoints(sharedVertices[0], v0, v1);
						ear1.setPoints(v1, v0, sharedVertices[1]);
					}
					return true;
				}
			}
			return false;
		}
	}

	/**
		 *
		 */
	private static class IndexedTriangle {
		private final int[] points;

		/**
		 *
		 *
		 * @param v0
		 * @param v1
		 * @param v2
		 */
		public IndexedTriangle(final int v0, final int v1, final int v2) {
			points = new int[3];
			setPoints(v0, v1, v2);
		}

		/**
		 *
		 *
		 * @param v0
		 * @param v1
		 * @param v2
		 */
		public void setPoints(final int v0, final int v1, final int v2) {
			points[0] = v0;
			points[1] = v1;
			points[2] = v2;
		}

		/**
		 *
		 *
		 * @return
		 */
		public int[] getVertices() {
			final int[] copy = new int[3];
			for (int i = 0; i < 3; i++) {
				copy[i] = points[i];
			}
			return copy;
		}

		/**
		 *
		 *
		 * @param other
		 * @return
		 */
		public int[] getSharedVertices(final IndexedTriangle other) {
			int count = 0;
			final boolean[] shared = new boolean[3];
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (points[i] == other.points[j]) {
						count++;
						shared[i] = true;
					}
				}
			}
			int[] common = null;
			if (count > 0) {
				common = new int[count];
				for (int i = 0, k = 0; i < 3; i++) {
					if (shared[i]) {
						common[k++] = points[i];
					}
				}
			}
			return common;
		}
	}

	private List<Coordinate> shellCoords;
	private boolean[] shellCoordAvailable;

	/**
	 *
	 *
	 * @param polygon
	 * @param optimize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public WB_Triangulation2D triangulatePolygon2D(final WB_Polygon polygon, final boolean optimize) {
		final List<WB_Point> pts = new FastList<WB_Point>();
		for (int i = 0; i < polygon.numberOfShellPoints; i++) {
			pts.add(polygon.getPoint(i));
		}
		int index = polygon.numberOfShellPoints;
		final List<WB_Point>[] hpts = new FastList[polygon.numberOfContours - 1];
		for (int i = 0; i < polygon.numberOfContours - 1; i++) {
			hpts[i] = new FastList<WB_Point>();
			for (int j = 0; j < polygon.numberOfPointsPerContour[i + 1]; j++) {
				hpts[i].add(polygon.points.get(index++));
			}
		}
		final WB_Plane P = polygon.getPlane(0);
		/*
		 * if (P.getNormal().getLength() < WB_Epsilon.EPSILON) { P = new
		 * WB_Plane(P.getOrigin(), WB_Vector.Z()); }
		 */
		final WB_Triangulation2DWithPoints triangulation = triangulatePolygon2D(pts, hpts, optimize,
				geometryfactory.createEmbeddedPlane(P));
		final WB_KDTreeInteger<WB_Point> pointmap = new WB_KDTreeInteger<WB_Point>();
		for (int i = 0; i < polygon.numberOfPoints; i++) {
			pointmap.add(polygon.getPoint(i), i);
		}
		final int[] triangles = triangulation.getTriangles();
		final int[] edges = triangulation.getEdges();
		final WB_CoordCollection tripoints = triangulation.getPoints();
		final int[] intmap = new int[tripoints.size()];
		index = 0;

		for (int i = 0; i < tripoints.size(); i++) {
			final int found = pointmap.getNearestNeighbor(tripoints.get(i)).value;
			intmap[index++] = found;
		}
		for (int i = 0; i < triangles.length; i++) {
			triangles[i] = intmap[triangles[i]];
		}
		for (int i = 0; i < edges.length; i++) {
			edges[i] = intmap[edges[i]];
		}
		return new WB_Triangulation2D(triangles, edges);
	}

	/**
	 *
	 *
	 * @param outerPolygon
	 * @param innerPolygons
	 * @param optimize
	 * @param context
	 * @return
	 */
	public WB_Triangulation2DWithPoints triangulatePolygon2D(final List<? extends WB_Coord> outerPolygon,
			final List<? extends WB_Coord>[] innerPolygons, final boolean optimize, final WB_Map2D context) {
		final Coordinate[] coords = new Coordinate[outerPolygon.size() + 1];
		WB_Point point = geometryfactory.createPoint();
		for (int i = 0; i < outerPolygon.size(); i++) {
			context.mapPoint3D(outerPolygon.get(i), point);
			coords[i] = new Coordinate(point.xd(), point.yd(), 0);
		}
		context.mapPoint3D(outerPolygon.get(0), point);
		coords[outerPolygon.size()] = new Coordinate(point.xd(), point.yd(), 0);
		LinearRing[] holes = null;
		if (innerPolygons != null) {
			holes = new LinearRing[innerPolygons.length];
			for (int j = 0; j < innerPolygons.length; j++) {
				final Coordinate[] icoords = new Coordinate[innerPolygons[j].size() + 1];
				for (int i = 0; i < innerPolygons[j].size(); i++) {
					context.mapPoint3D(innerPolygons[j].get(i), point);
					icoords[i] = new Coordinate(point.xd(), point.yd(), 0);
				}
				context.mapPoint3D(innerPolygons[j].get(0), point);
				icoords[innerPolygons[j].size()] = new Coordinate(point.xd(), point.yd(), 0);
				final LinearRing hole = new GeometryFactory().createLinearRing(icoords);
				holes[j] = hole;
			}
		}
		final LinearRing shell = new GeometryFactory().createLinearRing(coords);
		final Polygon inputPolygon = new GeometryFactory().createPolygon(shell, holes);
		final int[] ears = triangulate(inputPolygon, optimize);
		final int[] E = extractEdgesTri(ears);
		final List<WB_Point> Points = new FastList<WB_Point>();
		for (int i = 0; i < shellCoords.size() - 1; i++) {
			point = geometryfactory.createPoint();
			context.unmapPoint2D(shellCoords.get(i).x, shellCoords.get(i).y, point);
			Points.add(point);
		}
		return new WB_Triangulation2DWithPoints(ears, E, Points);
	}

	/**
	 *
	 *
	 * @param polygon
	 * @param points
	 * @param optimize
	 * @param context
	 * @return
	 */
	public WB_Triangulation2DWithPoints triangulatePolygon2D(final int[] polygon, final WB_Coord[] points,
			final boolean optimize, final WB_Map2D context) {
		final Coordinate[] coords = new Coordinate[polygon.length + 1];
		WB_Point point = geometryfactory.createPoint();
		for (int i = 0; i < polygon.length; i++) {
			context.mapPoint3D(points[polygon[i]], point);
			coords[i] = new Coordinate(point.xd(), point.yd(), i);
		}
		context.mapPoint3D(points[polygon[0]], point);
		coords[polygon.length] = new Coordinate(point.xd(), point.yd(), 0);
		final Polygon inputPolygon = new GeometryFactory().createPolygon(coords);
		final int[] ears = triangulate(inputPolygon, optimize);
		for (int i = 0; i < ears.length; i++) {
			ears[i] = polygon[ears[i]];

		}
		final int[] E = extractEdgesTri(ears);
		final List<WB_Point> Points = new FastList<WB_Point>();
		for (int i = 0; i < shellCoords.size() - 1; i++) {
			point = geometryfactory.createPoint();
			context.unmapPoint2D(shellCoords.get(i).x, shellCoords.get(i).y, point);
			Points.add(point);
		}
		return new WB_Triangulation2DWithPoints(ears, E, Points);
	}

	/**
	 *
	 *
	 * @param polygon
	 * @param points
	 * @param optimize
	 * @param context
	 * @return
	 */
	public WB_Triangulation2D triangulatePolygon2D(final int[] polygon, final List<? extends WB_Coord> points,
			final boolean optimize, final WB_Map2D context) {
		final Coordinate[] coords = new Coordinate[polygon.length + 1];
		final WB_Point point = geometryfactory.createPoint();
		for (int i = 0; i < polygon.length; i++) {
			context.mapPoint3D(points.get(polygon[i]), point);
			coords[i] = new Coordinate(point.xd(), point.yd(), polygon[i]);
		}
		context.mapPoint3D(points.get(polygon[0]), point);
		coords[polygon.length] = new Coordinate(point.xd(), point.yd(), polygon[0]);
		final Polygon inputPolygon = new GeometryFactory().createPolygon(coords);
		final int[] ears = triangulate(inputPolygon, optimize);
		for (int i = 0; i < ears.length; i++) {
			ears[i] = (int) shellCoords.get(ears[i]).z;

		}
		final int[] E = extractEdgesTri(ears);
		return new WB_Triangulation2D(ears, E);
	}

	public int[] triangulateFace(final HE_Face face, final boolean optimize) {
		int fo = face.getFaceDegree();
		final Coordinate[] coords = new Coordinate[fo + 1];
		WB_Coord normal = face.getFaceNormal();
		WB_Swizzle coordViewer;
		if (Math.abs(normal.xd()) > Math.abs(normal.yd())) {
			coordViewer = Math.abs(normal.xd()) > Math.abs(normal.zd()) ? WB_Swizzle.YZ : WB_Swizzle.XY;
		} else {
			coordViewer = Math.abs(normal.yd()) > Math.abs(normal.zd()) ? WB_Swizzle.ZX : WB_Swizzle.XY;
		}
		final WB_KDTreeInteger<WB_Point> pointmap = new WB_KDTreeInteger<WB_Point>();

		int i = 0;
		HE_Halfedge he = face.getHalfedge();
		do {
			coords[i] = new Coordinate(coordViewer.xd(he.getVertex()), coordViewer.yd(he.getVertex()), 0);
			pointmap.add(new WB_Point(coordViewer.xd(he.getVertex()), coordViewer.yd(he.getVertex())), i);
			i++;
			he = he.getNextInFace();
		} while (he != face.getHalfedge());
		coords[i] = new Coordinate(coordViewer.xd(he.getVertex()), coordViewer.yd(he.getVertex()), 0);

		final LinearRing shell = new GeometryFactory().createLinearRing(coords);
		final Polygon inputPolygon = new GeometryFactory().createPolygon(shell);
		final int[] ears = triangulate(inputPolygon, optimize);

		final List<WB_Point> tripoints = new FastList<WB_Point>();
		for (int j = 0; j < shellCoords.size() - 1; j++) {
			tripoints.add(new WB_Point(shellCoords.get(j).x, shellCoords.get(j).y));
		}
		final int[] intmap = new int[tripoints.size()];
		i = 0;
		for (final WB_Coord point : tripoints) {
			final int found = pointmap.getNearestNeighbor(point).value;
			intmap[i++] = found;
		}
		for (int j = 0; j < ears.length; j++) {
			ears[j] = intmap[ears[j]];
		}
		return ears;
	}

	public int[] triangulateSimplePolygon(final WB_Polygon polygon, final boolean optimize) {
		int fo = polygon.getNumberOfShellPoints();
		final Coordinate[] coords = new Coordinate[fo + 1];
		WB_Coord normal = polygon.getNormal();
		WB_Swizzle coordViewer;
		if (Math.abs(normal.xd()) > Math.abs(normal.yd())) {
			coordViewer = Math.abs(normal.xd()) > Math.abs(normal.zd()) ? WB_Swizzle.YZ : WB_Swizzle.XY;
		} else {
			coordViewer = Math.abs(normal.yd()) > Math.abs(normal.zd()) ? WB_Swizzle.ZX : WB_Swizzle.XY;
		}
		final WB_KDTreeInteger<WB_Point> pointmap = new WB_KDTreeInteger<WB_Point>();
		WB_Coord c;
		for (int i = 0; i <= polygon.getNumberOfShellPoints(); i++) {
			c = polygon.getPoint(i);
			coords[i] = new Coordinate(coordViewer.xd(c), coordViewer.yd(c), 0);
			pointmap.add(new WB_Point(coordViewer.xd(c), coordViewer.yd(c)), i);
			i++;

		}

		final LinearRing shell = new GeometryFactory().createLinearRing(coords);
		final Polygon inputPolygon = new GeometryFactory().createPolygon(shell);
		final int[] ears = triangulate(inputPolygon, optimize);

		final List<WB_Point> tripoints = new FastList<WB_Point>();
		for (int j = 0; j < shellCoords.size() - 1; j++) {
			tripoints.add(new WB_Point(shellCoords.get(j).x, shellCoords.get(j).y));
		}
		final int[] intmap = new int[tripoints.size()];
		int i = 0;
		for (final WB_Coord point : tripoints) {
			final int found = pointmap.getNearestNeighbor(point).value;
			intmap[i++] = found;
		}
		for (int j = 0; j < ears.length; j++) {
			ears[j] = intmap[ears[j]];
		}
		return ears;
	}

	/**
	 *
	 *
	 * @param ears
	 * @return
	 */
	private static int[] extractEdgesTri(final int[] ears) {
		final int f = ears.length;
		final UnifiedMap<Long, int[]> map = new UnifiedMap<Long, int[]>();
		for (int i = 0; i < ears.length; i += 3) {
			final int v0 = ears[i];
			final int v1 = ears[i + 1];
			final int v2 = ears[i + 2];
			long index = getIndex(v0, v1, f);
			map.put(index, new int[] { v0, v1 });
			index = getIndex(v1, v2, f);
			map.put(index, new int[] { v1, v2 });
			index = getIndex(v2, v0, f);
			map.put(index, new int[] { v2, v0 });
		}
		final int[] edges = new int[2 * map.size()];
		final Collection<int[]> values = map.values();
		int i = 0;
		for (final int[] value : values) {
			edges[2 * i] = value[0];
			edges[2 * i + 1] = value[1];
			i++;
		}
		return edges;
	}

	/**
	 *
	 *
	 * @param i
	 * @param j
	 * @param f
	 * @return
	 */
	private static long getIndex(final int i, final int j, final int f) {
		return i > j ? j + i * f : i + j * f;
	}

	/**
	 * Perform the triangulation.
	 *
	 * @param inputPolygon
	 * @param improve
	 *            if true, improvement of the triangulation is attempted as a
	 *            post-processing step
	 * @return GeometryCollection of triangular polygons
	 */
	private int[] triangulate(final Polygon inputPolygon, final boolean improve) {
		final GeometryFactory gf = new GeometryFactory();
		final List<IndexedTriangle> earList = new ArrayList<IndexedTriangle>();
		createShell(inputPolygon);
		final Geometry test = inputPolygon.buffer(0);
		int N = shellCoords.size() - 1;
		shellCoordAvailable = new boolean[N];
		Arrays.fill(shellCoordAvailable, true);
		boolean finished = false;
		boolean found = false;
		int k0 = 0;
		int k1 = 1;
		int k2 = 2;
		int firstK = 0;
		do {
			found = false;
			while (CGAlgorithms.computeOrientation(shellCoords.get(k0), shellCoords.get(k1),
					shellCoords.get(k2)) == 0) {
				k0 = k1;
				if (k0 == firstK) {
					finished = true;
					break;
				}
				k1 = k2;
				k2 = nextShellCoord(k2 + 1);
			}
			if (!finished && isValidEdge(k0, k2)) {
				final LineString ls = gf
						.createLineString(new Coordinate[] { shellCoords.get(k0), shellCoords.get(k2) });
				if (test.covers(ls)) {
					final Polygon earPoly = gf.createPolygon(gf.createLinearRing(new Coordinate[] { shellCoords.get(k0),
							shellCoords.get(k1), shellCoords.get(k2), shellCoords.get(k0) }), null);
					if (test.covers(earPoly)) {
						found = true;
						// System.out.println(earPoly);
						final IndexedTriangle ear = new IndexedTriangle(k0, k1, k2);
						earList.add(ear);
						shellCoordAvailable[k1] = false;
						N--;
						k0 = nextShellCoord(0);
						k1 = nextShellCoord(k0 + 1);
						k2 = nextShellCoord(k1 + 1);
						firstK = k0;
						if (N < 3) {
							finished = true;
						}
					}
				}
			}
			if (!finished && !found) {
				k0 = k1;
				if (k0 == firstK) {
					finished = true;
				} else {
					k1 = k2;
					k2 = nextShellCoord(k2 + 1);
				}
			}
		} while (!finished);
		if (improve) {// && inputPolygon.getNumInteriorRing() == 0) {
			doImprove(earList);
		}
		final int[] tris = new int[3 * earList.size()];
		for (int i = 0; i < earList.size(); i++) {
			final int[] tri = earList.get(i).getVertices();
			// final boolean flip = true;
			/*
			 * if (improve) { if
			 * (CGAlgorithms.orientationIndex(shellCoords.get(tri[0]),
			 * shellCoords.get(tri[1]), shellCoords.get(tri[2])) > 0) { flip =
			 * false;
			 *
			 * } }
			 *
			 * if (flip) {
			 */
			tris[3 * i] = tri[0];
			tris[3 * i + 1] = tri[1];
			tris[3 * i + 2] = tri[2];
			/*
			 * } else { tris[i][0] = tri[0]; tris[i][1] = tri[1]; tris[i][2] =
			 * tri[2]; }
			 */
		}
		return tris;
	}

	/**
	 *
	 *
	 * @param polygon
	 * @return
	 */
	protected WB_Polygon makeSimplePolygon(final WB_Polygon polygon) {
		Polygon poly = geometryfactory.toJTSPolygon2D(polygon);
		createShell(poly);
		Coordinate[] coords = new Coordinate[shellCoords.size()];
		return geometryfactory
				.createPolygonFromJTSPolygon2D(new GeometryFactory().createPolygon(shellCoords.toArray(coords)));

	}

	/**
	 * Transforms the input polygon into a single, possible self-intersecting
	 * shell by connecting holes to the exterior ring, The holes are added from
	 * the lowest upwards. As the resulting shell develops, a hole might be
	 * added to what was originally another hole.
	 *
	 * @param inputPolygon
	 */
	protected void createShell(final Polygon inputPolygon) {
		final Polygon poly = (Polygon) inputPolygon.clone();
		// Normalization changes the order of the vertices and messes up any
		// indexed scheme
		// Not sure if commenting out line will give later side effects...
		// poly.normalize();
		shellCoords = new ArrayList<Coordinate>();
		final List<Geometry> orderedHoles = getOrderedHoles(poly);
		final Coordinate[] coords = poly.getExteriorRing().getCoordinates();
		shellCoords.addAll(Arrays.asList(coords));
		for (int i = 0; i < orderedHoles.size(); i++) {
			joinHoleToShell(inputPolygon, orderedHoles.get(i));
		}
	}

	/**
	 * Check if a candidate edge between two vertices passes through any other
	 * available vertices.
	 *
	 * @param index0
	 *            first vertex
	 * @param index1
	 *            second vertex
	 * @return true if the edge does not pass through any other available
	 *         vertices; false otherwise
	 */
	private boolean isValidEdge(final int index0, final int index1) {
		final Coordinate[] line = { shellCoords.get(index0), shellCoords.get(index1) };
		int index = nextShellCoord(index0 + 1);
		while (index != index0) {
			if (index != index1) {
				final Coordinate c = shellCoords.get(index);
				if (!(c.equals2D(line[0]) || c.equals2D(line[1]))) {
					if (CGAlgorithms.isOnLine(c, line)) {
						return false;
					}
				}
			}
			index = nextShellCoord(index + 1);
		}
		return true;
	}

	/**
	 * Get the index of the next available shell coordinate starting from the
	 * given candidate position.
	 *
	 * @param pos
	 *            candidate position
	 * @return index of the next available shell coordinate
	 */
	private int nextShellCoord(final int pos) {
		int pnew = pos % shellCoordAvailable.length;
		while (!shellCoordAvailable[pnew]) {
			pnew = (pnew + 1) % shellCoordAvailable.length;
		}
		return pnew;
	}

	/**
	 * Attempts to improve the triangulation by examining pairs of triangles
	 * with a common edge, forming a quadrilateral, and testing if swapping the
	 * diagonal of this quadrilateral would produce two new triangles with
	 * larger minimum interior angles.
	 *
	 * @param earList
	 */
	private void doImprove(final List<IndexedTriangle> earList) {
		final EdgeFlipper ef = new EdgeFlipper(shellCoords);
		boolean changed;
		do {
			changed = false;
			for (int i = 0; i < earList.size() - 1 && !changed; i++) {
				final IndexedTriangle ear0 = earList.get(i);
				for (int j = i + 1; j < earList.size() && !changed; j++) {
					final IndexedTriangle ear1 = earList.get(j);
					final int[] sharedVertices = ear0.getSharedVertices(ear1);
					if (sharedVertices != null && sharedVertices.length == 2) {
						if (ef.flip(ear0, ear1, sharedVertices)) {
							changed = true;
						}
					}
				}
			}
		} while (changed);
	}

	/**
	 * Returns a list of holes in the input polygon (if any) ordered by y
	 * coordinate with ties broken using x coordinate.
	 *
	 * @param poly
	 *            input polygon
	 * @return a list of Geometry objects representing the ordered holes (may be
	 *         empty)
	 */
	private static List<Geometry> getOrderedHoles(final Polygon poly) {
		final List<Geometry> holes = new ArrayList<Geometry>();
		final List<IndexedEnvelope> bounds = new ArrayList<IndexedEnvelope>();
		if (poly.getNumInteriorRing() > 0) {
			for (int i = 0; i < poly.getNumInteriorRing(); i++) {
				bounds.add(new IndexedEnvelope(i, poly.getInteriorRingN(i).getEnvelopeInternal()));
			}
			Collections.sort(bounds, new IndexedEnvelopeComparator());
			for (int i = 0; i < bounds.size(); i++) {
				holes.add(poly.getInteriorRingN(bounds.get(i).index));
			}
		}
		return holes;
	}

	/**
	 * Join a given hole to the current shell. The hole coordinates are inserted
	 * into the list of shell coordinates.
	 *
	 * @param inputPolygon
	 * @param hole
	 *            the hole to join
	 */
	private void joinHoleToShell(final Polygon inputPolygon, final Geometry hole) {
		final GeometryFactory gf = new GeometryFactory();
		double minD2 = Double.MAX_VALUE;
		int shellVertexIndex = -1;
		final int Ns = shellCoords.size() - 1;
		final int holeVertexIndex = getLowestVertex(hole);
		final Coordinate[] holeCoords = hole.getCoordinates();
		final Coordinate ch = holeCoords[holeVertexIndex];
		final List<IndexedDouble> distanceList = new ArrayList<IndexedDouble>();
		/*
		 * Note: it's important to scan the shell vertices in reverse so that if
		 * a hole ends up being joined to what was originally another hole, the
		 * previous hole's coordinates appear in the shell before the new hole's
		 * coordinates (otherwise the triangulation algorithm tends to get
		 * stuck).
		 */
		for (int i = Ns - 1; i >= 0; i--) {
			final Coordinate cs = shellCoords.get(i);
			final double d2 = (ch.x - cs.x) * (ch.x - cs.x) + (ch.y - cs.y) * (ch.y - cs.y);
			if (d2 < minD2) {
				minD2 = d2;
				shellVertexIndex = i;
			}
			distanceList.add(new IndexedDouble(i, d2));
		}
		/*
		 * Try a quick join: if the closest shell vertex is reachable without
		 * crossing any holes.
		 */
		LineString join = gf.createLineString(new Coordinate[] { ch, shellCoords.get(shellVertexIndex) });
		if (inputPolygon.covers(join)) {
			doJoinHole(shellVertexIndex, holeCoords, holeVertexIndex);
			return;
		}
		/*
		 * Quick join didn't work. Sort the shell coords on distance to the hole
		 * vertex nnd choose the closest reachable one.
		 */
		Collections.sort(distanceList, new IndexedDoubleComparator());
		for (int i = 1; i < distanceList.size(); i++) {
			join = gf.createLineString(new Coordinate[] { ch, shellCoords.get(distanceList.get(i).index) });
			if (inputPolygon.covers(join)) {
				shellVertexIndex = distanceList.get(i).index;
				doJoinHole(shellVertexIndex, holeCoords, holeVertexIndex);
				return;
			}
		}
		// throw new IllegalStateException("Failed to join hole to shell");
	}

	/**
	 * Helper method for joinHoleToShell. Insert the hole coordinates into the
	 * shell coordinate list.
	 *
	 *
	 * @param shellVertexIndex
	 *            insertion point in the shell coordinate list
	 * @param holeCoords
	 *            array of hole coordinates
	 * @param holeVertexIndex
	 *            attachment point of hole
	 */
	private void doJoinHole(final int shellVertexIndex, final Coordinate[] holeCoords, final int holeVertexIndex) {
		final List<Coordinate> newCoords = new ArrayList<Coordinate>();
		newCoords.add(new Coordinate(shellCoords.get(shellVertexIndex)));
		final int N = holeCoords.length - 1;
		int i = holeVertexIndex;
		do {
			newCoords.add(new Coordinate(holeCoords[i]));
			i = (i + 1) % N;
		} while (i != holeVertexIndex);
		newCoords.add(new Coordinate(holeCoords[holeVertexIndex]));
		shellCoords.addAll(shellVertexIndex, newCoords);
	}

	/**
	 * Return the index of the lowest vertex.
	 *
	 * @param geom
	 *            input geometry
	 * @return index of the first vertex found at lowest point of the geometry
	 */
	private static int getLowestVertex(final Geometry geom) {
		final Coordinate[] coords = geom.getCoordinates();
		final double minY = geom.getEnvelopeInternal().getMinY();
		for (int i = 0; i < coords.length; i++) {
			if (Math.abs(coords[i].y - minY) < WB_Epsilon.EPSILON) {
				return i;
			}
		}
		throw new IllegalStateException("Failed to find lowest vertex");
	}

	/**
	 *
	 */
	private static class IndexedEnvelope {
		/**
		 *
		 */
		int index;
		/**
		 *
		 */
		Envelope envelope;

		/**
		 *
		 *
		 * @param i
		 * @param env
		 */
		public IndexedEnvelope(final int i, final Envelope env) {
			index = i;
			envelope = env;
		}
	}

	/**
	 *
	 */
	private static class IndexedEnvelopeComparator implements Comparator<IndexedEnvelope> {
		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(final IndexedEnvelope o1, final IndexedEnvelope o2) {
			double delta = o1.envelope.getMinY() - o2.envelope.getMinY();
			if (Math.abs(delta) < WB_Epsilon.EPSILON) {
				delta = o1.envelope.getMinX() - o2.envelope.getMinX();
				if (Math.abs(delta) < WB_Epsilon.EPSILON) {
					return 0;
				}
			}
			return delta > 0 ? 1 : -1;
		}
	}

	/**
	 *
	 */
	private static class IndexedDouble {
		/**
		 *
		 */
		int index;

		/**
		 *
		 */
		double value;

		/**
		 *
		 *
		 * @param i
		 * @param v
		 */
		public IndexedDouble(final int i, final double v) {
			index = i;
			value = v;
		}
	}

	/**
	 *
	 */
	private static class IndexedDoubleComparator implements Comparator<IndexedDouble> {
		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(final IndexedDouble o1, final IndexedDouble o2) {
			final double delta = o1.value - o2.value;
			if (Math.abs(delta) < WB_Epsilon.EPSILON) {
				return 0;
			}
			return delta > 0 ? 1 : -1;
		}
	}

	public static void main(final String[] args) {
		HEC_Archimedes creator = new HEC_Archimedes();
		creator.setEdge(150); // edge length of the polyhedron
		HE_Mesh mesh = new HE_Mesh(creator);
		mesh.modify(new HEM_Crocodile().setDistance(150).setChamfer(0.4));
		mesh.modify(new HEM_ChamferCorners().setDistance(60));
		mesh.modify(new HEM_Extrude().setDistance(0).setChamfer(0.1));
		mesh.modify(new HEM_Lattice().setWidth(15).setDepth(8));
		mesh.modify(new HEM_Slice().setPlane(0, 0, 0, 0, 1, 1.1));

	}

}
