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
import java.util.List;
import java.util.Set;

import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;

import org.eclipse.collections.impl.list.mutable.FastList;
import wblut.external.ProGAL.CTetrahedron;
import wblut.external.ProGAL.CVertex;
import wblut.external.ProGAL.DelaunayComplex;
import wblut.math.WB_ConstantScalarParameter;
import wblut.math.WB_ScalarParameter;

/**
 *
 */
class WB_Voronoi3D extends WB_Voronoi2D {

	/**
	 *
	 */
	private static WB_GeometryFactory geometryfactory = new WB_GeometryFactory();
	final static WB_Map2D XY = geometryfactory.createEmbeddedPlane();

	/**
	 *
	 * @param points
	 * @param nv
	 * @param aabb
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3D(final WB_Coord[] points, int nv, final WB_AABB aabb,
			final WB_ScalarParameter d) {
		nv = Math.min(nv, points.length);
		if (nv <= 4) {
			return getVoronoi3DBruteForce(points, nv, aabb, d);
		}
		final int n = points.length;
		final List<wblut.external.ProGAL.Point> tmppoints = new ArrayList<wblut.external.ProGAL.Point>(n);
		final WB_KDTreeInteger<WB_Coord> tree = new WB_KDTreeInteger<WB_Coord>();
		for (int i = 0; i < n; i++) {
			tmppoints.add(new wblut.external.ProGAL.Point(points[i].xd(), points[i].yd(), points[i].zd()));
			tree.add(points[i], i);
		}
		final DelaunayComplex dc = new DelaunayComplex(tmppoints);
		final List<CVertex> vertices = dc.getVertices();
		final List<WB_VoronoiCell3D> result = new FastList<WB_VoronoiCell3D>();
		for (int i = 0; i < nv; i++) {
			final CVertex v = vertices.get(i);
			final Set<CTetrahedron> vertexhull = dc.getVertexHull(v);
			final List<WB_Point> hullpoints = new ArrayList<WB_Point>();
			for (final CTetrahedron tetra : vertexhull) {
				// if (!tetra.containsBigPoint()) {
				hullpoints.add(toPoint(tetra.circumcenter()));
				// }
			}
			final List<WB_Point> finalpoints = new FastList<WB_Point>();
			for (int j = 0; j < hullpoints.size(); j++) {
				finalpoints.add(geometryfactory.createPoint(hullpoints.get(j)));
			}
			final int index = tree.getNearestNeighbor(toPoint(v)).value;
			final WB_VoronoiCell3D vor = new WB_VoronoiCell3D(finalpoints, geometryfactory.createPoint(points[index]),
					index);
			if (aabb != null) {
				vor.constrain(aabb);
			}
			if (vor.cell != null) {
				vor.trim(d.evaluate(vor.generator.xd(), vor.generator.yd(), vor.generator.zd()));
			}
			if (vor.cell != null && vor.cell.getNumberOfVertices() > 0) {
				result.add(vor);
			}
		}
		return result;
	}

	/**
	 *
	 * @param points
	 * @param nv
	 * @param aabb
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3D(final WB_Coord[] points, final int nv, final WB_AABB aabb,
			final double d) {
		return getVoronoi3D(points, nv, aabb, new WB_ConstantScalarParameter(d));
	}

	/**
	 *
	 * @param points
	 * @param nv
	 * @param aabb
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3D(final WB_Coord[] points, final int nv, final WB_AABB aabb) {
		return getVoronoi3D(points, nv, aabb, 0);
	}

	/**
	 *
	 * @param points
	 * @param aabb
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3D(final WB_Coord[] points, final WB_AABB aabb,
			final WB_ScalarParameter d) {
		return getVoronoi3D(points, points.length, aabb, d);
	}

	/**
	 *
	 * @param points
	 * @param aabb
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3D(final WB_Coord[] points, final WB_AABB aabb, final double d) {
		return getVoronoi3D(points, points.length, aabb, new WB_ConstantScalarParameter(d));
	}

	/**
	 *
	 * @param points
	 * @param aabb
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3D(final WB_Coord[] points, final WB_AABB aabb) {
		return getVoronoi3D(points, points.length, aabb, 0);
	}

	/**
	 *
	 *
	 * @param points
	 * @return
	 */
	public static int[][] getVoronoi3DNeighbors(final WB_Coord[] points) {
		final int nv = points.length;
		if (nv == 2) {
			return new int[][] { { 1 }, { 0 } };
		} else if (nv == 3) {
			return new int[][] { { 1, 2 }, { 0, 2 }, { 0, 1 } };
		} else if (nv == 4) {
			return new int[][] { { 1, 2, 3 }, { 0, 2, 3 }, { 0, 1, 3 }, { 0, 1, 2 } };
		}
		final List<wblut.external.ProGAL.Point> tmppoints = new ArrayList<wblut.external.ProGAL.Point>(nv);
		final WB_KDTreeInteger<WB_Coord> tree = new WB_KDTreeInteger<WB_Coord>();
		for (int i = 0; i < nv; i++) {
			tmppoints.add(new wblut.external.ProGAL.Point(points[i].xd(), points[i].yd(), points[i].zd()));
			tree.add(points[i], i);
		}
		final DelaunayComplex dc = new DelaunayComplex(tmppoints);
		final List<CVertex> vertices = dc.getVertices();
		final int[][] ns = new int[nv][];
		for (int i = 0; i < nv; i++) {
			final CVertex v = vertices.get(i);
			final Set<CTetrahedron> vertexhull = dc.getVertexHull(v);
			final IntHashSet neighbors = new IntHashSet();
			for (final CTetrahedron tetra : vertexhull) {
				for (int j = 0; j < 4; j++) {
					if (!tetra.getPoint(j).isBigpoint()) {
						neighbors.add(tree.getNearestNeighbor(toPoint(tetra.getPoint(j))).value);
					}
				}
			}
			ns[i] = neighbors.toArray();
		}
		return ns;
	}

	/**
	 *
	 *
	 * @param points
	 * @return
	 */
	public static int[][] getVoronoi3DNeighbors(final List<? extends WB_Coord> points) {
		final int nv = points.size();
		if (nv == 2) {
			return new int[][] { { 1 }, { 0 } };
		} else if (nv == 3) {
			return new int[][] { { 1, 2 }, { 0, 2 }, { 0, 1 } };
		} else if (nv == 4) {
			return new int[][] { { 1, 2, 3 }, { 0, 2, 3 }, { 0, 1, 3 }, { 0, 1, 2 } };
		}
		final List<wblut.external.ProGAL.Point> tmppoints = new ArrayList<wblut.external.ProGAL.Point>(nv);
		final WB_KDTreeInteger<WB_Coord> tree = new WB_KDTreeInteger<WB_Coord>();
		WB_Coord p;
		for (int i = 0; i < nv; i++) {
			p = points.get(i);
			tmppoints.add(new wblut.external.ProGAL.Point(p.xd(), p.yd(), p.zd()));
			tree.add(p, i);
		}
		final DelaunayComplex dc = new DelaunayComplex(tmppoints);
		final List<CVertex> vertices = dc.getVertices();
		final int[][] ns = new int[nv][];
		for (int i = 0; i < nv; i++) {
			final CVertex v = vertices.get(i);
			final Set<CTetrahedron> vertexhull = dc.getVertexHull(v);
			final IntHashSet neighbors = new IntHashSet();
			for (final CTetrahedron tetra : vertexhull) {
				for (int j = 0; j < 4; j++) {
					if (!tetra.getPoint(j).isBigpoint()) {
						neighbors.add(tree.getNearestNeighbor(toPoint(tetra.getPoint(j))).value);
					}
				}
			}
			ns[i] = neighbors.toArray();
		}
		return ns;
	}

	/**
	 *
	 * @param points
	 * @param aabb
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3D(final List<? extends WB_Coord> points, final WB_AABB aabb,
			final WB_ScalarParameter d) {
		return getVoronoi3D(points, points.size(), aabb, d);
	}

	/**
	 *
	 * @param points
	 * @param aabb
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3D(final List<? extends WB_Coord> points, final WB_AABB aabb,
			final double d) {
		return getVoronoi3D(points, points.size(), aabb, new WB_ConstantScalarParameter(d));
	}

	/**
	 *
	 * @param points
	 * @param aabb
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3D(final List<? extends WB_Coord> points, final WB_AABB aabb) {
		return getVoronoi3D(points, points.size(), aabb, new WB_ConstantScalarParameter(0));
	}

	/**
	 *
	 * @param points
	 * @param nv
	 * @param aabb
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3D(final List<? extends WB_Coord> points, int nv, final WB_AABB aabb,
			final WB_ScalarParameter d) {
		nv = Math.min(nv, points.size());
		if (nv <= 4) {
			return getVoronoi3DBruteForce(points, nv, aabb, d);
		}
		final int n = points.size();
		final List<wblut.external.ProGAL.Point> tmppoints = new ArrayList<wblut.external.ProGAL.Point>(n);
		final WB_KDTreeInteger<WB_Coord> tree = new WB_KDTreeInteger<WB_Coord>();
		int i = 0;
		for (final WB_Coord p : points) {
			tmppoints.add(new wblut.external.ProGAL.Point(p.xd(), p.yd(), p.zd()));
			tree.add(p, i++);
		}
		final DelaunayComplex dc = new DelaunayComplex(tmppoints);
		final List<CVertex> vertices = dc.getVertices();
		final List<WB_VoronoiCell3D> result = new FastList<WB_VoronoiCell3D>();
		for (i = 0; i < nv; i++) {
			final CVertex v = vertices.get(i);
			final Set<CTetrahedron> vertexhull = dc.getVertexHull(v);
			v.getAdjacentTriangles();
			final List<WB_Point> hullpoints = new ArrayList<WB_Point>();
			for (final CTetrahedron tetra : vertexhull) {
				// if (!tetra.containsBigPoint()) {
				hullpoints.add(toPoint(tetra.circumcenter()));
				// }
			}
			final List<WB_Point> finalpoints = new FastList<WB_Point>();
			for (int j = 0; j < hullpoints.size(); j++) {
				finalpoints.add(geometryfactory.createPoint(hullpoints.get(j)));
			}
			final int index = tree.getNearestNeighbor(toPoint(v)).value;
			final WB_VoronoiCell3D vor = new WB_VoronoiCell3D(finalpoints,
					geometryfactory.createPoint(points.get(index)), index);
			if (vor.cell != null) {
				vor.constrain(aabb);
			}
			if (vor.cell != null) {
				vor.trim(d.evaluate(vor.generator.xd(), vor.generator.yd(), vor.generator.zd()));
			}
			if (vor.cell != null && vor.cell.getNumberOfVertices() > 0) {
				result.add(vor);
			}
		}
		return result;
	}

	/**
	 *
	 * @param points
	 * @param nv
	 * @param aabb
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3D(final List<? extends WB_Coord> points, final int nv,
			final WB_AABB aabb, final double d) {
		return getVoronoi3D(points, nv, aabb, new WB_ConstantScalarParameter(d));
	}

	/**
	 *
	 * @param points
	 * @param nv
	 * @param aabb
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3D(final List<? extends WB_Coord> points, final int nv,
			final WB_AABB aabb) {
		return getVoronoi3D(points, nv, aabb, new WB_ConstantScalarParameter(0));
	}

	/**
	 *
	 * @param points
	 * @param nv
	 * @param aabb
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3DBruteForce(final List<? extends WB_Coord> points, int nv,
			final WB_AABB aabb, final WB_ScalarParameter d) {
		nv = Math.min(nv, points.size());
		final int n = points.size();
		final List<WB_VoronoiCell3D> result = new FastList<WB_VoronoiCell3D>();
		for (int i = 0; i < nv; i++) {
			final ArrayList<WB_Plane> cutPlanes = new ArrayList<WB_Plane>();
			final WB_Point O = new WB_Point();
			WB_Plane P;
			final WB_Mesh cell = geometryfactory.createMesh(aabb);
			for (int j = 0; j < n; j++) {
				if (j != i) {
					final WB_Vector N = new WB_Vector(points.get(i));
					N.subSelf(points.get(j));
					N.normalizeSelf();
					O.set(points.get(i)); // plane origin=point halfway
					// between point i and point j
					O.addSelf(points.get(j));
					O.mulSelf(0.5);
					P = new WB_Plane(O, N);
					cutPlanes.add(P);
				}
			}
			boolean unique;
			final ArrayList<WB_Plane> cleaned = new ArrayList<WB_Plane>();
			for (int j = 0; j < cutPlanes.size(); j++) {
				P = cutPlanes.get(j);
				unique = true;
				for (int k = 0; k < j; k++) {
					final WB_Plane Pj = cutPlanes.get(j);
					if (WB_GeometryOp3D.isEqual(P, Pj)) {
						unique = false;
						break;
					}
				}
				if (unique) {
					cleaned.add(P);
				}
			}
			final WB_VoronoiCell3D vor = new WB_VoronoiCell3D(cell, geometryfactory.createPoint(points.get(i)), i);
			if (vor.cell != null) {
				vor.constrain(cutPlanes);
			}
			if (vor.cell != null) {
				vor.trim(d.evaluate(vor.generator.xd(), vor.generator.yd(), vor.generator.zd()));
			}
			if (vor.cell != null && vor.cell.getNumberOfVertices() > 0) {
				result.add(vor);
			}
			result.add(vor);
		}
		return result;
	}

	/**
	 *
	 * @param points
	 * @param nv
	 * @param aabb
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3DBruteForce(final List<? extends WB_Coord> points, final int nv,
			final WB_AABB aabb, final double d) {
		return getVoronoi3DBruteForce(points, nv, aabb, new WB_ConstantScalarParameter(d));
	}

	/**
	 *
	 * @param points
	 * @param nv
	 * @param aabb
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3DBruteForce(final List<? extends WB_Coord> points, final int nv,
			final WB_AABB aabb) {
		return getVoronoi3DBruteForce(points, nv, aabb, new WB_ConstantScalarParameter(0));
	}

	/**
	 *
	 * @param points
	 * @param aabb
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3DBruteForce(final List<? extends WB_Coord> points,
			final WB_AABB aabb, final WB_ScalarParameter d) {
		return getVoronoi3DBruteForce(points, points.size(), aabb, d);
	}

	/**
	 *
	 * @param points
	 * @param aabb
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3DBruteForce(final List<? extends WB_Coord> points,
			final WB_AABB aabb, final double d) {
		return getVoronoi3DBruteForce(points, points.size(), aabb, new WB_ConstantScalarParameter(d));
	}

	/**
	 *
	 * @param points
	 * @param aabb
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3DBruteForce(final List<? extends WB_Coord> points,
			final WB_AABB aabb) {

		return getVoronoi3DBruteForce(points, points.size(), aabb, new WB_ConstantScalarParameter(0));

	}

	/**
	 *
	 * @param points
	 * @param nv
	 * @param aabb
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3DBruteForce(final WB_Coord[] points, int nv, final WB_AABB aabb,
			final WB_ScalarParameter d) {
		nv = Math.min(nv, points.length);
		final int n = points.length;
		final List<WB_VoronoiCell3D> result = new FastList<WB_VoronoiCell3D>();
		for (int i = 0; i < nv; i++) {
			final ArrayList<WB_Plane> cutPlanes = new ArrayList<WB_Plane>();
			final WB_Point O = new WB_Point();
			WB_Plane P;
			final WB_Mesh cell = geometryfactory.createMesh(aabb);
			for (int j = 0; j < n; j++) {
				if (j != i) {
					final WB_Vector N = new WB_Vector(points[i]);
					N.subSelf(points[j]);
					N.normalizeSelf();
					O.set(points[i]); // plane origin=point halfway
					// between point i and point j
					O.addSelf(points[j]);
					O.mulSelf(0.5);
					P = new WB_Plane(O, N);
					cutPlanes.add(P);
				}
			}
			boolean unique;
			final ArrayList<WB_Plane> cleaned = new ArrayList<WB_Plane>();
			for (int j = 0; j < cutPlanes.size(); j++) {
				P = cutPlanes.get(j);
				unique = true;
				for (int k = 0; k < j; k++) {
					final WB_Plane Pj = cutPlanes.get(j);
					if (WB_GeometryOp3D.isEqual(P, Pj)) {
						unique = false;
						break;
					}
				}
				if (unique) {
					cleaned.add(P);
				}
			}
			final WB_VoronoiCell3D vor = new WB_VoronoiCell3D(cell, geometryfactory.createPoint(points[i]), i);
			if (vor.cell != null) {
				vor.constrain(cutPlanes);
			}
			if (vor.cell != null) {
				vor.trim(d.evaluate(vor.generator.xd(), vor.generator.yd(), vor.generator.zd()));
			}
			if (vor.cell != null && vor.cell.getNumberOfVertices() > 0) {
				result.add(vor);
			}
			result.add(vor);
		}
		return result;
	}

	/**
	 *
	 * @param points
	 * @param nv
	 * @param aabb
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3DBruteForce(final WB_Coord[] points, final int nv,
			final WB_AABB aabb, final double d) {
		return getVoronoi3DBruteForce(points, nv, aabb, new WB_ConstantScalarParameter(d));
	}

	/**
	 *
	 * @param points
	 * @param nv
	 * @param aabb
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3DBruteForce(final WB_Coord[] points, final int nv,
			final WB_AABB aabb) {
		return getVoronoi3DBruteForce(points, nv, aabb, new WB_ConstantScalarParameter(0));
	}

	/**
	 *
	 * @param points
	 * @param aabb
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3DBruteForce(final WB_Coord[] points, final WB_AABB aabb,
			final WB_ScalarParameter d) {
		return getVoronoi3DBruteForce(points, points.length, aabb, d);
	}

	/**
	 *
	 * @param points
	 * @param aabb
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3DBruteForce(final WB_Coord[] points, final WB_AABB aabb,
			final double d) {
		return getVoronoi3DBruteForce(points, points.length, aabb, new WB_ConstantScalarParameter(0));
	}

	/**
	 *
	 *
	 * @param points
	 * @param aabb
	 * @return
	 */
	public static List<WB_VoronoiCell3D> getVoronoi3DBruteForce(final WB_Coord[] points, final WB_AABB aabb) {
		return getVoronoi3DBruteForce(points, points.length, aabb, new WB_ConstantScalarParameter(0));
	}

	/**
	 *
	 *
	 * @param v
	 * @return
	 */
	private static WB_Point toPoint(final wblut.external.ProGAL.Point v) {
		return geometryfactory.createPoint(v.x(), v.y(), v.z());
	}

}
