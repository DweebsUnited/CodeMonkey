/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 * 
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 * 
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.geom;

import wblut.hemesh.HE_Face;

/**
 *
 */
public class WB_PolygonTriangulatorEarcut {

	/**
	 *
	 */
	public WB_PolygonTriangulatorEarcut() {
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
	public int[] triangulateQuad(final WB_Coord p0, final WB_Coord p1, final WB_Coord p2, final WB_Coord p3) {
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
	 *
	 *
	 * @param polygon
	 * @return
	 */
	public WB_Triangulation2D triangulatePolygon2D(final WB_Polygon polygon) {
		int[] triangles;
		try {
			triangles = WB_Earcut.triangulate2D(polygon);
		} catch (Exception e) {
			triangles = new int[0];
		}
		return new WB_Triangulation2D(triangles);
	}

	public WB_Triangulation2D triangulatePolygon2D(final HE_Face face) {
		int[] triangles;
		try {
			triangles = WB_Earcut.triangulate2Dindices(face);
		} catch (Exception e) {
			triangles = new int[0];
		}
		return new WB_Triangulation2D(triangles);
	}

}
