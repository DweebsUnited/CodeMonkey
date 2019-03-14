/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 *
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 *
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.hemesh;

import java.util.Iterator;
import java.util.List;

import org.eclipse.collections.impl.list.mutable.FastList;

import wblut.geom.WB_Coord;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_GeometryOp3D;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.math.WB_ConstantScalarParameter;
import wblut.math.WB_ScalarParameter;

/**
 * Tries to expand a mesh by moving all faces a distance along their normal. No
 * vertices are added. The new position of a vertex is found by displacing all
 * the planes in its star and searching for their intersection. If the planes
 * intersect in a single point,this point is used. Otherwise a least-square
 * approximation of their intersection is used , i.e. the point that minimizes
 * the combined squared distance to the planes. If the least-square
 * approximation fails, the vertex is displaced along the vertex normal instead.
 * A cutoff factor can be specified to limit the movement of the vertices. If a
 * vertex would move more than cutoff*distance, it is displaced along the vertex
 * normal instead. If not specified, the cutoff factor defaults to 4.0,
 * corresponding to the displacement expected in an acute angle of 30°.
 *
 */
public class HEM_FaceExpand extends HEM_Modifier {
	/**
	 *
	 */
	private WB_ScalarParameter d;
	double cutoff2;

	/**
	 *
	 */
	public HEM_FaceExpand() {
		super();
		d = WB_ScalarParameter.ZERO;
		cutoff2 = 16.0;
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public HEM_FaceExpand setDistance(final double d) {
		this.d = d == 0 ? WB_ScalarParameter.ZERO : new WB_ConstantScalarParameter(d);
		return this;
	}

	public HEM_FaceExpand setCutoff(final double f) {
		this.cutoff2 = f * f;
		return this;
	}

	public HEM_FaceExpand setDistance(final WB_ScalarParameter d) {
		this.d = d;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	protected HE_Mesh applySelf(final HE_Mesh mesh) {
		if (d == WB_ScalarParameter.ZERO) {
			return mesh;
		}
		HE_Vertex v;
		Iterator<HE_Vertex> vItr = mesh.vItr();

		final List<WB_Plane> planes = new FastList<WB_Plane>();
		final List<WB_Plane> uniquePlanes = new FastList<WB_Plane>();
		final List<WB_Coord> pos = new FastList<WB_Coord>();
		List<HE_Face> faces;
		WB_Coord LS = null;
		while (vItr.hasNext()) {
			LS = null;
			v = vItr.next();
			double ld = d.evaluate(v.xd(), v.yd(), v.zd());

			faces = v.getFaceStar();
			planes.clear();
			uniquePlanes.clear();
			for (HE_Face f : faces) {
				planes.add(f.getPlane());
			}

			boolean unique = true;
			WB_Plane Pi, Pj;
			HE_Face Fi;
			HE_Face Fj = null;
			uniquePlanes.add(planes.get(0));
			Fi = faces.get(0);
			for (int i = 1; i < planes.size(); i++) {
				Pi = planes.get(i);
				unique = true;
				for (int j = 0; j < i; j++) {
					Pj = planes.get(j);
					if (WB_GeometryOp3D.isEqual(Pi, Pj)) {
						unique = false;
						break;
					}
				}
				if (unique) {
					uniquePlanes.add(Pi);
					if (faces.get(i).isNeighbor(Fi)) {
						Fj = faces.get(i);
					}
				}
			}
			if (uniquePlanes.size() == 1 && v.isBoundary()) {
				LS = WB_Point.addMul(v, ld, v.getVertexNormal());

			} else if (uniquePlanes.size() == 2) {
				HE_Halfedge edge = Fi.getHalfedge(Fj);
				if (edge != null) {
					LS = WB_Point.addMul(v, ld / Math.cos(0.5 * (edge.getEdgeDihedralAngle() - Math.PI)),
							edge.getEdgeNormal());
				}
			} else {
				LS = WB_GeometryOp.getClosestPoint3D(uniquePlanes, ld);
			}

			if (LS == null) {// least-square multiplane intersection point or
								// plane/plane intersection line not found
				LS = WB_Point.addMul(v, ld, v.getVertexNormal());
			} else if (WB_GeometryOp.getSqDistance3D(v, LS) > cutoff2 * ld * ld) {
				// potential precision problem
				LS = WB_Point.addMul(v, ld, v.getVertexNormal());

			}

			pos.add(LS);
		}

		vItr = mesh.vItr();

		Iterator<WB_Coord> pItr = pos.iterator();
		while (vItr.hasNext()) {
			vItr.next().set(pItr.next());
		}
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	protected HE_Mesh applySelf(final HE_Selection selection) {
		if (d == null) {
			return selection.parent;
		}
		selection.collectVertices();
		HE_Vertex v;
		Iterator<HE_Vertex> vItr = selection.vItr();
		final List<WB_Plane> planes = new FastList<WB_Plane>();
		final List<WB_Plane> uniquePlanes = new FastList<WB_Plane>();
		final List<WB_Coord> pos = new FastList<WB_Coord>();
		List<HE_Face> faces;
		WB_Coord LS = null;
		while (vItr.hasNext()) {
			v = vItr.next();
			double ld = d.evaluate(v.xd(), v.yd(), v.zd());
			faces = v.getFaceStar();
			planes.clear();
			uniquePlanes.clear();
			for (HE_Face f : faces) {
				planes.add(f.getPlane());
			}

			boolean unique = true;
			WB_Plane Pi, Pj;
			HE_Face Fi;
			HE_Face Fj = null;
			uniquePlanes.add(planes.get(0));
			Fi = faces.get(0);
			for (int i = 1; i < planes.size(); i++) {
				Pi = planes.get(i);
				unique = true;
				for (int j = 0; j < i; j++) {
					Pj = planes.get(j);
					if (WB_GeometryOp3D.isEqual(Pi, Pj)) {
						unique = false;
						break;
					}
				}
				if (unique) {
					uniquePlanes.add(Pi);
					if (faces.get(i).isNeighbor(Fi)) {
						Fj = faces.get(i);
					}
				}
			}
			if (uniquePlanes.size() == 1) {
				LS = WB_Point.addMul(v, ld, v.getVertexNormal());

			} else if (uniquePlanes.size() == 2) {
				HE_Halfedge edge = Fi.getHalfedge(Fj);
				if (edge != null) {
					LS = WB_Point.addMul(v, ld / Math.cos(0.5 * (edge.getEdgeDihedralAngle() - Math.PI)),
							edge.getEdgeNormal());
				}
			} else {
				LS = WB_GeometryOp.getClosestPoint3D(uniquePlanes, ld);
			}

			if (LS == null) {// least-square multiplane intersection point or
								// plane/plane intersection line not found
				LS = WB_Point.addMul(v, ld, v.getVertexNormal());
			} else if (WB_GeometryOp.getSqDistance3D(v, LS) > cutoff2 * ld * ld) {
				// potential precision problem
				LS = WB_Point.addMul(v, ld, v.getVertexNormal());

			}

			pos.add(LS);
		}
		vItr = selection.vItr();
		Iterator<WB_Coord> pItr = pos.iterator();
		while (vItr.hasNext()) {
			vItr.next().set(pItr.next());
		}
		return selection.parent;
	}
}
