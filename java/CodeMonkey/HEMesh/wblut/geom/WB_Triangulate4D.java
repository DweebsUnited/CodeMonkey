/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 * 
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 * 
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.geom;

import java.util.List;

import wblut.external.Delaunay.WB_Delaunay;

/**
 *
 */
class WB_Triangulate4D extends WB_Triangulate3D {

	/**
	 *
	 */
	WB_Triangulate4D() {
	}

	/**
	 *
	 *
	 * @param points
	 * @param closest
	 * @return
	 */
	public static WB_Triangulation4D triangulate4D(final List<? extends WB_Coord> points, final double closest) {
		return new WB_Triangulation4D(WB_Delaunay.getTriangulation4D(points, closest).Tri);

	}

	/**
	 *
	 *
	 * @param points
	 * @param closest
	 * @param epsilon
	 * @return
	 */
	public static WB_Triangulation4D triangulate4D(final List<? extends WB_Coord> points, final double closest,
			final double epsilon) {
		return new WB_Triangulation4D(WB_Delaunay.getTriangulation4D(points, closest, epsilon).Tri);
	}

	/**
	 *
	 *
	 * @param points
	 * @param closest
	 * @return
	 */
	public static WB_Triangulation4D triangulate4D(final WB_Coord[] points, final double closest) {
		return new WB_Triangulation4D(WB_Delaunay.getTriangulation4D(points, closest).Tri);
	}

	/**
	 *
	 *
	 * @param points
	 * @param closest
	 * @param epsilon
	 * @return
	 */
	public static WB_Triangulation4D triangulate4D(final WB_Coord[] points, final double closest,
			final double epsilon) {
		return new WB_Triangulation4D(WB_Delaunay.getTriangulation4D(points, closest, epsilon).Tri);
	}

}
