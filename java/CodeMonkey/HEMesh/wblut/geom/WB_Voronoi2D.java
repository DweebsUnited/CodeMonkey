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
import java.util.Collection;
import java.util.List;

import org.eclipse.collections.impl.list.mutable.FastList;

import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;
import com.vividsolutions.jts.triangulate.quadedge.QuadEdgeSubdivision;

/**
 *
 */
class WB_Voronoi2D {

	/**
	 *
	 */
	private static WB_GeometryFactory geometryfactory = new WB_GeometryFactory();
	final static WB_Map2D XY = geometryfactory.createEmbeddedPlane();

	/**
	 *
	 *
	 * @param points
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getVoronoi2D(final WB_Coord[] points, final WB_Map2D context) {
		final int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}
		return voronoi2D(coords, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getVoronoi2D(final Collection<? extends WB_Coord> points,
			final WB_Map2D context) {
		final int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}
		return voronoi2D(coords, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param d
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getVoronoi2D(final WB_Coord[] points, final double d, final WB_Map2D context) {
		return getVoronoi2D(points, d, 2, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param d
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getVoronoi2D(final Collection<? extends WB_Coord> points, final double d,
			final WB_Map2D context) {
		return getVoronoi2D(points, d, 2, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param d
	 * @param c
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getVoronoi2D(final WB_Coord[] points, final double d, final int c,
			final WB_Map2D context) {
		final int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}
		return voronoi2D(coords, d, c, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param d
	 * @param c
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getVoronoi2D(final Collection<? extends WB_Coord> points, final double d,
			final int c, final WB_Map2D context) {
		final int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}
		return voronoi2D(coords, d, c, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param d
	 * @param c
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getVoronoi2D(final Collection<? extends WB_Coord> points, final double d,
			final int c) {
		final int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, XY));
			id++;
		}
		return voronoi2D(coords, d, c, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getVoronoi2D(final Collection<? extends WB_Coord> points, final double d) {
		return getVoronoi2D(points, d, 2, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getVoronoi2D(final Collection<? extends WB_Coord> points) {
		final int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, XY));
			id++;
		}
		return voronoi2D(coords, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param d
	 * @param c
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getVoronoi2D(final WB_Coord[] points, final double d, final int c) {
		final int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, XY));
		}
		return voronoi2D(coords, d, c, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getVoronoi2D(final WB_Coord[] points, final double d) {
		return getVoronoi2D(points, d, 2, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getVoronoi2D(final WB_Coord[] points) {
		final int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, XY));
		}
		return voronoi2D(coords, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final WB_Map2D context) {
		final int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}
		return clippedVoronoi2D(coords, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final WB_Coord[] boundary,
			final WB_Map2D context) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}
		n = boundary.length;
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			bdcoords.add(toCoordinate(boundary[i], i, context));
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return clippedVoronoi2D(coords, bdcoords, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final WB_Polygon boundary,
			final WB_Map2D context) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}

		return clippedVoronoi2D(coords, boundary, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final WB_Map2D context) {
		final int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}
		return clippedVoronoi2D(coords, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final Collection<? extends WB_Coord> boundary, final WB_Map2D context) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}
		n = boundary.size();
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		id = 0;
		for (final WB_Coord p : boundary) {
			bdcoords.add(toCoordinate(p, id, context));
			id++;
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return clippedVoronoi2D(coords, bdcoords, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final WB_Polygon boundary, final WB_Map2D context) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}

		return clippedVoronoi2D(coords, boundary, context);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final List<WB_Polygon> boundary, final WB_Map2D context) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}

		return clippedVoronoi2D(coords, boundary, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param d
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final double d,
			final WB_Map2D context) {
		final int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}
		return clippedVoronoi2D(coords, d, 2, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param d
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final WB_Coord[] boundary,
			final double d, final WB_Map2D context) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}
		n = boundary.length;
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			bdcoords.add(toCoordinate(boundary[i], i, context));
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return clippedVoronoi2D(coords, bdcoords, d, 2, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param d
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final WB_Polygon boundary,
			final double d, final WB_Map2D context) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}

		return clippedVoronoi2D(coords, boundary, d, 2, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param d
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final Collection<? extends WB_Coord> boundary, final double d, final WB_Map2D context) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}
		n = boundary.size();
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		id = 0;
		for (final WB_Coord p : boundary) {
			bdcoords.add(toCoordinate(p, id, context));
			id++;
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return clippedVoronoi2D(coords, bdcoords, d, 2, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param d
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final WB_Polygon boundary, final double d, final WB_Map2D context) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}

		return clippedVoronoi2D(coords, boundary, d, 2, context);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final List<WB_Polygon> boundary, final double d, final WB_Map2D context) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}

		return clippedVoronoi2D(coords, boundary, d, 2, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param d
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final double d, final WB_Map2D context) {
		final int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}
		return clippedVoronoi2D(coords, d, 2, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param d
	 * @param c
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final double d, final int c,
			final WB_Map2D context) {
		final int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}
		return clippedVoronoi2D(coords, d, c, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param d
	 * @param c
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final WB_Coord[] boundary,
			final double d, final int c, final WB_Map2D context) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}
		n = boundary.length;
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			bdcoords.add(toCoordinate(boundary[i], i, context));
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return clippedVoronoi2D(coords, bdcoords, d, c, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param d
	 * @param c
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final WB_Polygon boundary,
			final double d, final int c, final WB_Map2D context) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}

		return clippedVoronoi2D(coords, boundary, d, c, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param d
	 * @param c
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final double d, final int c, final WB_Map2D context) {
		final int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}
		return clippedVoronoi2D(coords, d, c, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param d
	 * @param c
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final Collection<? extends WB_Coord> boundary, final double d, final int c, final WB_Map2D context) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}
		n = boundary.size();
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		id = 0;
		for (final WB_Coord p : boundary) {
			bdcoords.add(toCoordinate(p, id, context));
			id++;
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return clippedVoronoi2D(coords, bdcoords, d, c, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param d
	 * @param c
	 * @param context
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final WB_Polygon boundary, final double d, final int c, final WB_Map2D context) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}

		return clippedVoronoi2D(coords, boundary, d, c, context);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final List<WB_Polygon> boundary, final double d, final int c, final WB_Map2D context) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}

		return clippedVoronoi2D(coords, boundary, d, c, context);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param d
	 * @param c
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final Collection<? extends WB_Coord> boundary, final double d, final int c) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, XY));
			id++;
		}
		n = boundary.size();
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		id = 0;
		for (final WB_Coord p : boundary) {
			bdcoords.add(toCoordinate(p, id, XY));
			id++;
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return clippedVoronoi2D(coords, bdcoords, d, c, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final Collection<? extends WB_Coord> boundary, final double d) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, XY));
			id++;
		}
		n = boundary.size();
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		id = 0;
		for (final WB_Coord p : boundary) {
			bdcoords.add(toCoordinate(p, id, XY));
			id++;
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return clippedVoronoi2D(coords, bdcoords, d, 2, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final Collection<? extends WB_Coord> boundary) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, XY));
			id++;
		}
		n = boundary.size();
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		id = 0;
		for (final WB_Coord p : boundary) {
			bdcoords.add(toCoordinate(p, id, XY));
			id++;
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return clippedVoronoi2D(coords, bdcoords, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param d
	 * @param c
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final double d, final int c) {
		final int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, XY));
			id++;
		}
		return clippedVoronoi2D(coords, d, c, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final double d) {
		final int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, XY));
			id++;
		}
		return clippedVoronoi2D(coords, d, 2, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points) {
		final int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, XY));
			id++;
		}
		return clippedVoronoi2D(coords, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param d
	 * @param c
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final double d, final int c) {
		final int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, XY));
		}
		return clippedVoronoi2D(coords, d, c, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final double d) {
		final int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, XY));
		}
		return clippedVoronoi2D(coords, d, 2, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param d
	 * @param c
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final WB_Coord[] boundary,
			final double d, final int c) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, XY));
		}
		n = boundary.length;
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			bdcoords.add(toCoordinate(boundary[i], i, XY));
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return clippedVoronoi2D(coords, bdcoords, d, c, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final WB_Coord[] boundary,
			final double d) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, XY));
		}
		n = boundary.length;
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			bdcoords.add(toCoordinate(boundary[i], i, XY));
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return clippedVoronoi2D(coords, bdcoords, d, 2, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final WB_Coord[] boundary) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, XY));
		}
		n = boundary.length;
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			bdcoords.add(toCoordinate(boundary[i], i, XY));
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return clippedVoronoi2D(coords, bdcoords, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points) {
		final int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, XY));
		}
		return clippedVoronoi2D(coords, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final WB_Polygon boundary) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, XY));
		}

		return clippedVoronoi2D(coords, boundary, XY);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final List<WB_Polygon> boundary) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, XY));
		}

		return clippedVoronoi2D(coords, boundary, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final WB_Polygon boundary) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, XY));
			id++;
		}

		return clippedVoronoi2D(coords, boundary, XY);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final List<WB_Polygon> boundary) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, XY));
			id++;
		}

		return clippedVoronoi2D(coords, boundary, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final WB_Polygon boundary,
			final double d) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, XY));
		}

		return clippedVoronoi2D(coords, boundary, d, 2, XY);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final List<WB_Polygon> boundary,
			final double d) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, XY));
		}

		return clippedVoronoi2D(coords, boundary, d, 2, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param d
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final WB_Polygon boundary, final double d) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, XY));
			id++;
		}

		return clippedVoronoi2D(coords, boundary, d, 2, XY);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final List<WB_Polygon> boundary, final double d) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, XY));
			id++;
		}

		return clippedVoronoi2D(coords, boundary, d, 2, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param d
	 * @param c
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final WB_Polygon boundary,
			final double d, final int c) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, XY));
		}

		return clippedVoronoi2D(coords, boundary, d, c, XY);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final WB_Coord[] points, final List<WB_Polygon> boundary,
			final double d, final int c) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, XY));
		}

		return clippedVoronoi2D(coords, boundary, d, c, XY);
	}

	/**
	 *
	 *
	 * @param points
	 * @param boundary
	 * @param d
	 * @param c
	 * @return
	 */
	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final WB_Polygon boundary, final double d, final int c) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, XY));
			id++;
		}

		return clippedVoronoi2D(coords, boundary, d, c, XY);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(final Collection<? extends WB_Coord> points,
			final List<WB_Polygon> boundary, final double d, final int c) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coord p : points) {
			coords.add(toCoordinate(p, id, XY));
			id++;
		}

		return clippedVoronoi2D(coords, boundary, d, c, XY);
	}

	/**
	 *
	 *
	 * @param coords
	 * @param context
	 * @return
	 */
	private static List<WB_VoronoiCell2D> voronoi2D(final ArrayList<Coordinate> coords, final WB_Map2D context) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qes = dtb.getSubdivision();
		final GeometryCollection polys = (GeometryCollection) qes.getVoronoiDiagram(new GeometryFactory());
		final int npolys = polys.getNumGeometries();
		final List<WB_VoronoiCell2D> result = new FastList<WB_VoronoiCell2D>();
		for (int i = 0; i < npolys; i++) {
			final Polygon poly = (Polygon) polys.getGeometryN(i);
			final Coordinate[] polycoord = poly.getCoordinates();
			final List<WB_Coord> polypoints = new FastList<WB_Coord>();
			for (final Coordinate element : polycoord) {
				polypoints.add(toPoint(element.x, element.y, context));
			}
			final Point centroid = poly.getCentroid();
			final WB_Point pc = centroid == null ? null : toPoint(centroid.getX(), centroid.getY(), context);
			final int index = (int) ((Coordinate) poly.getUserData()).z;
			final double area = poly.getArea();
			result.add(
					new WB_VoronoiCell2D(polypoints, index, geometryfactory.createPoint(coords.get(index)), area, pc));
		}
		return result;
	}

	/**
	 *
	 *
	 * @param coords
	 * @param d
	 * @param c
	 * @param context
	 * @return
	 */
	private static List<WB_VoronoiCell2D> voronoi2D(final ArrayList<Coordinate> coords, final double d, final int c,
			final WB_Map2D context) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qes = dtb.getSubdivision();
		final GeometryCollection polys = (GeometryCollection) qes.getVoronoiDiagram(new GeometryFactory());
		final int npolys = polys.getNumGeometries();
		final List<WB_VoronoiCell2D> result = new FastList<WB_VoronoiCell2D>();
		Coordinate[] coordsArray = new Coordinate[coords.size()];
		coordsArray = coords.toArray(coordsArray);
		for (int i = 0; i < npolys; i++) {
			Polygon poly = (Polygon) polys.getGeometryN(i);
			Geometry intersect = poly;
			intersect = intersect.buffer(-d, c);
			final double cellindex = ((Coordinate) poly.getUserData()).z;
			for (int j = 0; j < intersect.getNumGeometries(); j++) {
				if (intersect.getGeometryN(j).getGeometryType().equals("Polygon")
						&& !intersect.getGeometryN(j).isEmpty()) {
					poly = (Polygon) intersect.getGeometryN(j);
					final Coordinate[] polycoord = poly.getCoordinates();
					final List<WB_Point> polypoints = new FastList<WB_Point>();
					for (final Coordinate element : polycoord) {
						polypoints.add(toPoint(element.x, element.y, context));
					}
					final Point centroid = poly.getCentroid();
					final WB_Point pc = centroid == null ? null : toPoint(centroid.getX(), centroid.getY(), context);
					final int index = (int) cellindex;
					final double area = poly.getArea();
					result.add(new WB_VoronoiCell2D(polypoints, index, geometryfactory.createPoint(coords.get(index)),
							area, pc));
				}
			}
		}
		return result;
	}

	/**
	 * @param coords
	 * @param context
	 * @return
	 */
	private static List<WB_VoronoiCell2D> clippedVoronoi2D(final ArrayList<Coordinate> coords, final WB_Map2D context) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qes = dtb.getSubdivision();
		final GeometryCollection polys = (GeometryCollection) qes.getVoronoiDiagram(new GeometryFactory());
		final int npolys = polys.getNumGeometries();
		final List<WB_VoronoiCell2D> result = new FastList<WB_VoronoiCell2D>();
		Coordinate[] coordsArray = new Coordinate[coords.size()];
		coordsArray = coords.toArray(coordsArray);
		final ConvexHull ch = new ConvexHull(coordsArray, new GeometryFactory());
		final Geometry hull = ch.getConvexHull();
		for (int i = 0; i < npolys; i++) {
			Polygon poly = (Polygon) polys.getGeometryN(i);
			final Geometry intersect = poly.intersection(hull.getGeometryN(0));
			final double cellindex = ((Coordinate) poly.getUserData()).z;
			for (int j = 0; j < intersect.getNumGeometries(); j++) {
				if (intersect.getGeometryN(j).getGeometryType().equals("Polygon")
						&& !intersect.getGeometryN(j).isEmpty()) {
					poly = (Polygon) intersect.getGeometryN(j);
					final Coordinate[] polycoord = poly.getCoordinates();
					final List<WB_Point> polypoints = new FastList<WB_Point>();
					for (final Coordinate element : polycoord) {
						polypoints.add(toPoint(element.x, element.y, context));
					}
					final Point centroid = poly.getCentroid();
					final WB_Point pc = centroid == null ? null : toPoint(centroid.getX(), centroid.getY(), context);
					final int index = (int) cellindex;
					final double area = poly.getArea();
					result.add(new WB_VoronoiCell2D(polypoints, index, geometryfactory.createPoint(coords.get(index)),
							area, pc));
				}
			}
		}
		return result;
	}

	/**
	 * @param coords
	 * @param d
	 * @param c
	 * @param context
	 * @return
	 */
	private static List<WB_VoronoiCell2D> clippedVoronoi2D(final ArrayList<Coordinate> coords, final double d,
			final int c, final WB_Map2D context) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qes = dtb.getSubdivision();
		final GeometryCollection polys = (GeometryCollection) qes.getVoronoiDiagram(new GeometryFactory());
		final int npolys = polys.getNumGeometries();
		final List<WB_VoronoiCell2D> result = new FastList<WB_VoronoiCell2D>();
		Coordinate[] coordsArray = new Coordinate[coords.size()];
		coordsArray = coords.toArray(coordsArray);
		final ConvexHull ch = new ConvexHull(coordsArray, new GeometryFactory());
		final Geometry hull = ch.getConvexHull();
		for (int i = 0; i < npolys; i++) {
			Polygon poly = (Polygon) polys.getGeometryN(i);
			Geometry intersect = poly.intersection(hull.getGeometryN(0));
			intersect = intersect.buffer(-d, c);
			final double cellindex = ((Coordinate) poly.getUserData()).z;
			for (int j = 0; j < intersect.getNumGeometries(); j++) {
				if (intersect.getGeometryN(j).getGeometryType().equals("Polygon")
						&& !intersect.getGeometryN(j).isEmpty()) {
					poly = (Polygon) intersect.getGeometryN(j);
					final Coordinate[] polycoord = poly.getCoordinates();
					final List<WB_Point> polypoints = new FastList<WB_Point>();
					for (final Coordinate element : polycoord) {
						polypoints.add(toPoint(element.x, element.y, context));
					}
					final Point centroid = poly.getCentroid();
					final WB_Point pc = centroid == null ? null : toPoint(centroid.getX(), centroid.getY(), context);
					final int index = (int) cellindex;
					final double area = poly.getArea();
					result.add(new WB_VoronoiCell2D(polypoints, index, geometryfactory.createPoint(coords.get(index)),
							area, pc));
				}
			}
		}
		return result;
	}

	/**
	 *
	 *
	 * @param coords
	 * @param bdcoords
	 * @param context
	 * @return
	 */
	private static List<WB_VoronoiCell2D> clippedVoronoi2D(final ArrayList<Coordinate> coords,
			final ArrayList<Coordinate> bdcoords, final WB_Map2D context) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qes = dtb.getSubdivision();
		final GeometryCollection polys = (GeometryCollection) qes.getVoronoiDiagram(new GeometryFactory());
		final int npolys = polys.getNumGeometries();
		final List<WB_VoronoiCell2D> result = new FastList<WB_VoronoiCell2D>();
		Coordinate[] bdcoordsArray = new Coordinate[bdcoords.size()];
		bdcoordsArray = bdcoords.toArray(bdcoordsArray);
		final Polygon hull = new GeometryFactory().createPolygon(bdcoordsArray);
		for (int i = 0; i < npolys; i++) {
			Polygon poly = (Polygon) polys.getGeometryN(i);
			final Geometry intersect = poly.intersection(hull);
			final double cellindex = ((Coordinate) poly.getUserData()).z;
			for (int j = 0; j < intersect.getNumGeometries(); j++) {
				if (intersect.getGeometryN(j).getGeometryType().equals("Polygon")
						&& !intersect.getGeometryN(j).isEmpty()) {
					poly = (Polygon) intersect.getGeometryN(j);

					final Coordinate[] polycoord = poly.getCoordinates();
					final List<WB_Point> polypoints = new FastList<WB_Point>();
					for (final Coordinate element : polycoord) {
						polypoints.add(toPoint(element.x, element.y, context));
					}
					final Point centroid = poly.getCentroid();
					final WB_Point pc = centroid == null ? null : toPoint(centroid.getX(), centroid.getY(), context);
					final int index = (int) cellindex;
					final double area = poly.getArea();
					result.add(new WB_VoronoiCell2D(polypoints, index, geometryfactory.createPoint(coords.get(index)),
							area, pc));
				}
			}
		}
		return result;
	}

	/**
	 *
	 *
	 * @param coords
	 * @param constraint
	 * @param context
	 * @return
	 */
	private static List<WB_VoronoiCell2D> clippedVoronoi2D(final ArrayList<Coordinate> coords,
			final WB_Polygon constraint, final WB_Map2D context) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qes = dtb.getSubdivision();
		final GeometryCollection polys = (GeometryCollection) qes.getVoronoiDiagram(new GeometryFactory());
		final int npolys = polys.getNumGeometries();
		final List<WB_VoronoiCell2D> result = new FastList<WB_VoronoiCell2D>();

		final Polygon hull = geometryfactory.toJTSPolygon2D(constraint);
		for (int i = 0; i < npolys; i++) {
			Polygon poly = (Polygon) polys.getGeometryN(i);
			final Geometry intersect = poly.intersection(hull);
			final double cellindex = ((Coordinate) poly.getUserData()).z;
			for (int j = 0; j < intersect.getNumGeometries(); j++) {
				if (intersect.getGeometryN(j).getGeometryType().equals("Polygon")
						&& !intersect.getGeometryN(j).isEmpty()) {
					poly = (Polygon) intersect.getGeometryN(j);

					final Coordinate[] polycoord = poly.getCoordinates();
					final List<WB_Point> polypoints = new FastList<WB_Point>();
					for (final Coordinate element : polycoord) {
						polypoints.add(toPoint(element.x, element.y, context));
					}
					final Point centroid = poly.getCentroid();
					final WB_Point pc = centroid == null ? null : toPoint(centroid.getX(), centroid.getY(), context);
					final int index = (int) cellindex;
					final double area = poly.getArea();
					result.add(new WB_VoronoiCell2D(polypoints, index, geometryfactory.createPoint(coords.get(index)),
							area, pc));
				}
			}
		}
		return result;
	}

	/**
	 *
	 *
	 * @param coords
	 * @param constraint
	 * @param context
	 * @return
	 */
	private static List<WB_VoronoiCell2D> clippedVoronoi2D(final ArrayList<Coordinate> coords,
			final List<WB_Polygon> constraint, final WB_Map2D context) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qes = dtb.getSubdivision();
		final GeometryCollection polys = (GeometryCollection) qes.getVoronoiDiagram(new GeometryFactory());
		final int npolys = polys.getNumGeometries();
		final List<WB_VoronoiCell2D> result = new FastList<WB_VoronoiCell2D>();

		final Geometry hull = geometryfactory.toJTSMultiPolygon2D(constraint);
		for (int i = 0; i < npolys; i++) {
			Polygon poly = (Polygon) polys.getGeometryN(i);
			final Geometry intersect = poly.intersection(hull);
			final double cellindex = ((Coordinate) poly.getUserData()).z;
			for (int j = 0; j < intersect.getNumGeometries(); j++) {
				if (intersect.getGeometryN(j).getGeometryType().equals("Polygon")
						&& !intersect.getGeometryN(j).isEmpty()) {
					poly = (Polygon) intersect.getGeometryN(j);

					final Coordinate[] polycoord = poly.getCoordinates();
					final List<WB_Point> polypoints = new FastList<WB_Point>();
					for (final Coordinate element : polycoord) {
						polypoints.add(toPoint(element.x, element.y, context));
					}
					final Point centroid = poly.getCentroid();
					final WB_Point pc = centroid == null ? null : toPoint(centroid.getX(), centroid.getY(), context);
					final int index = (int) cellindex;
					final double area = poly.getArea();
					result.add(new WB_VoronoiCell2D(polypoints, index, geometryfactory.createPoint(coords.get(index)),
							area, pc));
				}
			}
		}
		return result;
	}

	/**
	 *
	 *
	 * @param coords
	 * @param bdcoords
	 * @param d
	 * @param c
	 * @param context
	 * @return
	 */
	private static List<WB_VoronoiCell2D> clippedVoronoi2D(final ArrayList<Coordinate> coords,
			final ArrayList<Coordinate> bdcoords, final double d, final int c, final WB_Map2D context) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qes = dtb.getSubdivision();
		final GeometryCollection polys = (GeometryCollection) qes.getVoronoiDiagram(new GeometryFactory());
		final int npolys = polys.getNumGeometries();
		final List<WB_VoronoiCell2D> result = new FastList<WB_VoronoiCell2D>();
		Coordinate[] bdcoordsArray = new Coordinate[bdcoords.size()];
		bdcoordsArray = bdcoords.toArray(bdcoordsArray);
		final Polygon hull = new GeometryFactory().createPolygon(bdcoordsArray);
		for (int i = 0; i < npolys; i++) {
			Polygon poly = (Polygon) polys.getGeometryN(i);
			Geometry intersect = poly.intersection(hull);
			intersect = intersect.buffer(-d, c);
			final double cellindex = ((Coordinate) poly.getUserData()).z;
			for (int j = 0; j < intersect.getNumGeometries(); j++) {
				if (intersect.getGeometryN(j).getGeometryType().equals("Polygon")
						&& !intersect.getGeometryN(j).isEmpty()) {
					poly = (Polygon) intersect.getGeometryN(j);

					final Coordinate[] polycoord = poly.getCoordinates();
					final List<WB_Point> polypoints = new FastList<WB_Point>();
					;
					for (final Coordinate element : polycoord) {
						polypoints.add(toPoint(element.x, element.y, context));
					}
					final Point centroid = poly.getCentroid();
					final WB_Point pc = centroid == null ? null : toPoint(centroid.getX(), centroid.getY(), context);
					final int index = (int) cellindex;
					final double area = poly.getArea();
					result.add(new WB_VoronoiCell2D(polypoints, index, geometryfactory.createPoint(coords.get(index)),
							area, pc));
				}
			}
		}
		return result;
	}

	/**
	 *
	 *
	 * @param coords
	 * @param constraint
	 * @param d
	 * @param c
	 * @param context
	 * @return
	 */
	private static List<WB_VoronoiCell2D> clippedVoronoi2D(final ArrayList<Coordinate> coords,
			final WB_Polygon constraint, final double d, final int c, final WB_Map2D context) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qes = dtb.getSubdivision();
		final GeometryCollection polys = (GeometryCollection) qes.getVoronoiDiagram(new GeometryFactory());
		final int npolys = polys.getNumGeometries();
		final List<WB_VoronoiCell2D> result = new FastList<WB_VoronoiCell2D>();
		final Polygon hull = geometryfactory.toJTSPolygon2D(constraint);
		for (int i = 0; i < npolys; i++) {
			Polygon poly = (Polygon) polys.getGeometryN(i);
			Geometry intersect = poly.intersection(hull);
			intersect = intersect.buffer(-d, c);
			final double cellindex = ((Coordinate) poly.getUserData()).z;
			for (int j = 0; j < intersect.getNumGeometries(); j++) {
				if (intersect.getGeometryN(j).getGeometryType().equals("Polygon")
						&& !intersect.getGeometryN(j).isEmpty()) {
					poly = (Polygon) intersect.getGeometryN(j);

					final Coordinate[] polycoord = poly.getCoordinates();
					final List<WB_Point> polypoints = new FastList<WB_Point>();
					;
					for (final Coordinate element : polycoord) {
						polypoints.add(toPoint(element.x, element.y, context));
					}
					final Point centroid = poly.getCentroid();
					final WB_Point pc = centroid == null ? null : toPoint(centroid.getX(), centroid.getY(), context);
					final int index = (int) cellindex;
					final double area = poly.getArea();
					result.add(new WB_VoronoiCell2D(polypoints, index, geometryfactory.createPoint(coords.get(index)),
							area, pc));
				}
			}
		}
		return result;
	}

	private static List<WB_VoronoiCell2D> clippedVoronoi2D(final ArrayList<Coordinate> coords,
			final List<WB_Polygon> constraint, final double d, final int c, final WB_Map2D context) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qes = dtb.getSubdivision();
		final GeometryCollection polys = (GeometryCollection) qes.getVoronoiDiagram(new GeometryFactory());
		final int npolys = polys.getNumGeometries();
		final List<WB_VoronoiCell2D> result = new FastList<WB_VoronoiCell2D>();
		final Geometry hull = geometryfactory.toJTSMultiPolygon2D(constraint);
		for (int i = 0; i < npolys; i++) {
			Polygon poly = (Polygon) polys.getGeometryN(i);
			Geometry intersect = poly.intersection(hull);
			intersect = intersect.buffer(-d, c);
			final double cellindex = ((Coordinate) poly.getUserData()).z;
			for (int j = 0; j < intersect.getNumGeometries(); j++) {
				if (intersect.getGeometryN(j).getGeometryType().equals("Polygon")
						&& !intersect.getGeometryN(j).isEmpty()) {
					poly = (Polygon) intersect.getGeometryN(j);

					final Coordinate[] polycoord = poly.getCoordinates();
					final List<WB_Point> polypoints = new FastList<WB_Point>();
					;
					for (final Coordinate element : polycoord) {
						polypoints.add(toPoint(element.x, element.y, context));
					}
					final Point centroid = poly.getCentroid();
					final WB_Point pc = centroid == null ? null : toPoint(centroid.getX(), centroid.getY(), context);
					final int index = (int) cellindex;
					final double area = poly.getArea();
					result.add(new WB_VoronoiCell2D(polypoints, index, geometryfactory.createPoint(coords.get(index)),
							area, pc));
				}
			}
		}
		return result;
	}

	/**
	 *
	 *
	 * @param p
	 * @param i
	 * @param context
	 * @return
	 */
	private static Coordinate toCoordinate(final WB_Coord p, final int i, final WB_Map2D context) {
		final WB_Point tmp = geometryfactory.createPoint();
		context.mapPoint3D(p, tmp);
		final Coordinate c = new Coordinate(tmp.xd(), tmp.yd(), i);
		return c;
	}

	/**
	 *
	 *
	 * @param x
	 * @param y
	 * @param context
	 * @return
	 */
	private static WB_Point toPoint(final double x, final double y, final WB_Map2D context) {
		final WB_Point tmp = geometryfactory.createPoint();
		context.unmapPoint3D(x, y, 0, tmp);
		return tmp;
	}

	/**
	 *
	 *
	 * @param points
	 * @return
	 */
	public static int[][] getVoronoi2DNeighbors(final WB_Coord[] points) {
		WB_Triangulation2D tri = WB_Triangulate.triangulate2D(points);
		return tri.getNeighbors();
	}

	/**
	 *
	 *
	 * @param points
	 * @return
	 */
	public static int[][] getVoronoi2DNeighbors(final List<? extends WB_Coord> points) {
		WB_Triangulation2D tri = WB_Triangulate.triangulate2D(points);
		return tri.getNeighbors();
	}

	/**
	 *
	 * @param points
	 * @param context
	 * @return
	 */
	public static int[][] getVoronoi2DNeighbors(final WB_Coord[] points, final WB_Map2D context) {
		WB_Triangulation2D tri = WB_Triangulate.triangulate2D(points, context);
		return tri.getNeighbors();
	}

	/**
	 *
	 * @param points
	 * @param context
	 * @return
	 */
	public static int[][] getVoronoi2DNeighbors(final List<? extends WB_Coord> points, final WB_Map2D context) {
		WB_Triangulation2D tri = WB_Triangulate.triangulate2D(points, context);
		return tri.getNeighbors();
	}

}
