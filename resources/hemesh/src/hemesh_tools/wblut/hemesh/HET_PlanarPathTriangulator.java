/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 * 
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 * 
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.hemesh;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import org.eclipse.collections.impl.list.mutable.FastList;
import wblut.core.WB_ProgressReporter.WB_ProgressCounter;
import wblut.core.WB_ProgressReporter.WB_ProgressTracker;
import wblut.geom.WB_Coord;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_KDTree;
import wblut.geom.WB_Map2D;
import wblut.geom.WB_OrthoProject;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Triangulate;
import wblut.geom.WB_Triangulation2DWithPoints;

/**
 *
 */
class HET_PlanarPathTriangulator {
	/**
	 *
	 */
	private static GeometryFactory JTSgf = new GeometryFactory();
	/**
	 *
	 */
	public static final WB_ProgressTracker tracker = WB_ProgressTracker.instance();
	/**
	 *
	 */
	private static WB_GeometryFactory geometryfactory = new WB_GeometryFactory();

	/**
	 *
	 */
	public HET_PlanarPathTriangulator() {
	}

	/**
	 *
	 *
	 * @param paths
	 * @param P
	 * @return
	 */
	public static long[] getTriangleKeys(final List<? extends HE_Path> paths, final WB_Plane P) {

		tracker.setStartStatus("HET_PlanarPathTriangulator", "Starting planar path triangulation.");
		final WB_Map2D emb = new WB_OrthoProject(P);
		final RingTree ringtree = new RingTree();
		List<HE_Vertex> vertices;
		Coordinate[] pts;
		final WB_KDTree<WB_Point, Long> vertextree = new WB_KDTree<WB_Point, Long>();
		tracker.setDuringStatus("HET_PlanarPathTriangulator", "Building contours tree.");
		for (int i = 0; i < paths.size(); i++) {
			final HE_Path path = paths.get(i);
			if (path.isLoop() && path.getPathOrder() > 2) {
				vertices = path.getPathVertices();
				pts = new Coordinate[vertices.size() + 1];
				for (int j = 0; j < vertices.size(); j++) {
					final WB_Point proj = geometryfactory.createPoint();
					emb.mapPoint3D(vertices.get(j), proj);
					vertextree.add(proj, vertices.get(j).getKey());
					pts[vertices.size() - j] = new Coordinate(proj.xd(), proj.yd(), 0);
				}
				final WB_Point proj = geometryfactory.createPoint();
				emb.mapPoint3D(vertices.get(0), proj);
				pts[0] = new Coordinate(proj.xd(), proj.yd(), 0);
				ringtree.add(JTSgf.createLinearRing(pts));
			}
		}
		tracker.setDuringStatus("HET_PlanarPathTriangulator", "Extracting polygons from contours tree.");
		final List<WB_Polygon> polygons = ringtree.extractPolygons();
		final List<WB_Coord[]> triangles = new FastList<WB_Coord[]>();
		WB_ProgressCounter counter = new WB_ProgressCounter(polygons.size(), 10);
		tracker.setCounterStatus("HET_PlanarPathTriangulator", "Triangulating polygons.", counter);
		for (final WB_Polygon poly : polygons) {
			final int[] tris = poly.getTriangles(false);
			for (int i = 0; i < tris.length; i += 3) {
				triangles.add(new WB_Coord[] { poly.getPoint(tris[i]), poly.getPoint(tris[i + 1]),
						poly.getPoint(tris[i + 2]) });
			}
			counter.increment();
		}
		final long[] trianglekeys = new long[3 * triangles.size()];
		for (int i = 0; i < triangles.size(); i++) {
			final WB_Coord[] tri = triangles.get(i);
			final long key0 = vertextree.getNearestNeighbor(tri[0]).value;
			final long key1 = vertextree.getNearestNeighbor(tri[1]).value;
			final long key2 = vertextree.getNearestNeighbor(tri[2]).value;
			// reverse triangles
			trianglekeys[3 * i] = key0;
			trianglekeys[3 * i + 1] = key2;
			trianglekeys[3 * i + 2] = key1;
		}
		tracker.setStopStatus("HET_PlanarPathTriangulator", "All paths triangulated.");
		return trianglekeys;
	}

	/**
	 *
	 *
	 * @param paths
	 * @param P
	 * @return
	 */
	public static HE_Mesh getConstrainedCaps(final List<? extends HE_Path> paths, final WB_Plane P) {
		tracker.setStartStatus("HET_PlanarPathTriangulator", "Starting planar path triangulation.");
		final WB_Map2D emb = geometryfactory.createEmbeddedPlane(P);
		final RingTree ringtree = new RingTree();
		List<HE_Vertex> vertices;
		Coordinate[] pts;
		final WB_KDTree<WB_Point, Long> vertextree = new WB_KDTree<WB_Point, Long>();
		tracker.setDuringStatus("HET_PlanarPathTriangulator", "Building contours tree.");
		for (int i = 0; i < paths.size(); i++) {
			final HE_Path path = paths.get(i);
			if (path.isLoop() && path.getPathOrder() > 2) {
				vertices = path.getPathVertices();
				pts = new Coordinate[vertices.size() + 1];
				for (int j = 0; j < vertices.size(); j++) {
					final WB_Point proj = geometryfactory.createPoint();
					emb.mapPoint3D(vertices.get(j), proj);
					vertextree.add(proj, vertices.get(j).getKey());
					pts[vertices.size() - j] = new Coordinate(proj.xd(), proj.yd(), 0);
				}
				final WB_Point proj = geometryfactory.createPoint();
				emb.mapPoint3D(vertices.get(0), proj);
				pts[0] = new Coordinate(proj.xd(), proj.yd(), 0);
				ringtree.add(JTSgf.createLinearRing(pts));
			}
		}
		tracker.setDuringStatus("HET_PlanarPathTriangulator", "Extracting polygons from contours tree.");
		final List<WB_Polygon> polygons = ringtree.extractPolygons();
		WB_ProgressCounter counter = new WB_ProgressCounter(polygons.size(), 10);
		tracker.setCounterStatus("HET_PlanarPathTriangulator", "Triangulating polygons.", counter);
		final HE_Mesh cap = new HE_Mesh();
		for (final WB_Polygon poly : polygons) {
			final WB_Triangulation2DWithPoints tri = WB_Triangulate.triangulateConforming2D(poly);
			cap.add(toMesh(tri));
			counter.increment();
		}
		final HE_VertexIterator vitr = cap.vItr();
		HE_Vertex v;
		while (vitr.hasNext()) {
			v = vitr.next();
			emb.unmapPoint2D(v.xd(), v.yd(), v);
		}
		tracker.setStopStatus("HET_PlanarPathTriangulator", "All paths triangulated.");
		return cap;
	}

	/**
	 *
	 *
	 * @param tri
	 * @return
	 */
	static HE_Mesh toMesh(final WB_Triangulation2DWithPoints tri) {
		final HEC_FromFacelist ffl = new HEC_FromFacelist().setFaces(tri.getTriangles()).setVertices(tri.getPoints());
		return new HE_Mesh(ffl);
	}

	// The JTS implementation of ShapeReader does not handle overlapping
	// polygons well. All code below this point is my solution for this. A
	// hierarchical tree that orders rings from the outside in. All input has to
	// be well-ordered: CCW for shell, CW for hole.
	/**
	 *
	 */
	private static class RingNode {
		/**
		 *
		 */
		@SuppressWarnings("unused")
		RingNode parent;
		/**
		 *
		 */
		List<RingNode> children;
		/**
		 *
		 */
		LinearRing ring;
		/**
		 *
		 */
		Polygon poly;// redundant, but useful for within/contains checks
		/**
		 *
		 */
		boolean hole;

		/**
		 *
		 */
		RingNode() {
			parent = null;
			ring = null;
			children = new ArrayList<RingNode>();
			hole = true;
		}

		/**
		 *
		 *
		 * @param parent
		 * @param ring
		 */
		RingNode(final RingNode parent, final LinearRing ring) {
			this.parent = parent;
			this.ring = ring;
			final Coordinate[] coords = ring.getCoordinates();
			poly = JTSgf.createPolygon(coords);
			hole = !CGAlgorithms.isCCW(coords);
			children = new ArrayList<RingNode>();
		}
	}

	/**
	 *
	 */
	private static class RingTree {
		/**
		 *
		 */
		RingNode root;

		/**
		 *
		 */
		RingTree() {
			root = new RingNode();
		}

		/**
		 *
		 *
		 * @param ring
		 */
		void add(final LinearRing ring) {
			final Polygon poly = JTSgf.createPolygon(ring);
			RingNode currentParent = root;
			RingNode foundParent;
			do {
				foundParent = null;
				for (int i = 0; i < currentParent.children.size(); i++) {
					final RingNode node = currentParent.children.get(i);
					final Polygon other = node.poly;
					if (poly.within(other)) {
						foundParent = node;
						currentParent = node;
						break;
					}
				}
			} while (foundParent != null);
			final RingNode newNode = new RingNode(currentParent, ring);
			final List<RingNode> nodesToRemove = new ArrayList<RingNode>();
			for (int i = 0; i < currentParent.children.size(); i++) {
				final RingNode node = currentParent.children.get(i);
				final Polygon other = node.poly;
				if (other.within(poly)) {
					newNode.children.add(node);
					nodesToRemove.add(node);
				}
			}
			currentParent.children.removeAll(nodesToRemove);
			currentParent.children.add(newNode);
		}

		/**
		 *
		 *
		 * @return
		 */
		List<WB_Polygon> extractPolygons() {
			final List<WB_Polygon> polygons = new ArrayList<WB_Polygon>();
			final List<RingNode> shellNodes = new ArrayList<RingNode>();
			addExteriorNodes(root, shellNodes);
			for (final RingNode node : shellNodes) {
				int count = 0;
				for (int i = 0; i < node.children.size(); i++) {
					if (node.children.get(i).hole) {
						count++;
					}
				}
				final LinearRing[] holes = new LinearRing[count];
				int index = 0;
				for (int i = 0; i < node.children.size(); i++) {
					if (node.children.get(i).hole) {
						holes[index++] = node.children.get(i).ring;
					}
				}
				final Geometry result = JTSgf.createPolygon(node.ring, holes);
				if (result.getGeometryType().equals("Polygon")) {
					polygons.add(geometryfactory.createPolygonFromJTSPolygon2D((Polygon) result));
				} else if (result.getGeometryType().equals("MultiPolygon")) {
					for (int j = 0; j < result.getNumGeometries(); j++) {
						final Geometry ggeo = result.getGeometryN(j);
						polygons.add(geometryfactory.createPolygonFromJTSPolygon2D((Polygon) ggeo));
					}
				}
			}
			return polygons;
		}

		/**
		 *
		 *
		 * @param parent
		 * @param shellNodes
		 */
		void addExteriorNodes(final RingNode parent, final List<RingNode> shellNodes) {
			for (final RingNode node : parent.children) {
				if (node.hole == false) {
					shellNodes.add(node);
				}
				addExteriorNodes(node, shellNodes);
			}
		}
	}
}
