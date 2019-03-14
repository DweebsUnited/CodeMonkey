/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 *
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 *
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.hemesh;

import static wblut.geom.WB_GeometryOp3D.projectOnPlane;

import java.util.List;

import org.eclipse.collections.impl.list.mutable.FastList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.valid.IsValidOp;

import wblut.geom.WB_AABB;
import wblut.geom.WB_Classification;
import wblut.geom.WB_Coord;
import wblut.geom.WB_CoordCollection;
import wblut.geom.WB_CoordinateSystem3D;
import wblut.geom.WB_GeometryOp3D;
import wblut.geom.WB_Map2D;
import wblut.geom.WB_OrthoProject;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_PolygonTriangulatorJTS;
import wblut.geom.WB_Triangle;
import wblut.geom.WB_TriangleGenerator;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;

/**
 * Face element of half-edge data structure.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HE_Face extends HE_MeshElement implements Comparable<HE_Face>, WB_TriangleGenerator {
	/** Halfedge associated with this face. */
	private HE_Halfedge _halfedge;
	private int textureId;
	int[] triangles;
	WB_Coord normal;
	WB_Point center;
	WB_AABB aabb;

	/**
	 * Instantiates a new HE_Face.
	 */
	public HE_Face() {
		super();
		triangles = null;
		normal = null;
		center = null;
		aabb = null;
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_FaceEdgeCirculator feCrc() {
		return new HE_FaceEdgeCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_FaceFaceCirculator ffCrc() {
		return new HE_FaceFaceCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_FaceVertexCirculator fvCrc() {
		return new HE_FaceVertexCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_FaceHalfedgeInnerCirculator fheiCrc() {
		return new HE_FaceHalfedgeInnerCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_FaceHalfedgeOuterCirculator fheoCrc() {
		return new HE_FaceHalfedgeOuterCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_FaceEdgeRevCirculator feRevCrc() {
		return new HE_FaceEdgeRevCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_FaceFaceRevCirculator ffRevCrc() {
		return new HE_FaceFaceRevCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_FaceHalfedgeInnerRevCirculator fheiRevCrc() {
		return new HE_FaceHalfedgeInnerRevCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_FaceHalfedgeOuterRevCirculator fheoRevCrc() {
		return new HE_FaceHalfedgeOuterRevCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_FaceVertexRevCirculator fvRevCrc() {
		return new HE_FaceVertexRevCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public long key() {
		return super.getKey();
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getFaceCenter() {
		if (center != null) {
			return center;
		}
		if (_halfedge == null) {
			return null;
		}
		HE_Halfedge he = _halfedge;
		center = new WB_Point();
		int c = 0;
		do {
			center.addSelf(he.getVertex());
			c++;
			he = he.getNextInFace();
		} while (he != _halfedge);
		center.divSelf(c);
		return center;
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public WB_Coord getFaceCenter(final double d) {
		if (center != null) {
			return center.addMul(d, getFaceNormal());
		}
		if (_halfedge == null) {
			return null;
		}
		HE_Halfedge he = _halfedge;
		center = new WB_Point();
		int c = 0;
		do {
			center.addSelf(he.getVertex());
			c++;
			he = he.getNextInFace();
		} while (he != _halfedge);
		center.divSelf(c);
		return center.addMul(d, getFaceNormal());
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getFaceNormal() {
		if (normal == null) {
			normal = HET_MeshOp.getFaceNormal(this);
		}
		return normal;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getNonNormFaceNormal() {
		return HET_MeshOp.getFaceNormalNotNormalized(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public double getFaceArea() {
		return HET_MeshOp.getFaceArea(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Classification getFaceType() {
		return HET_MeshOp.getFaceType(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<HE_Vertex> getUniqueFaceVertices() {
		final FastList<HE_Vertex> fv = new FastList<HE_Vertex>();
		if (_halfedge == null) {
			return fv;
		}
		HE_Halfedge he = _halfedge;
		do {
			if (!fv.contains(he.getVertex())) {
				fv.add(he.getVertex());
			}
			he = he.getNextInFace();
		} while (he != _halfedge);
		return fv.asUnmodifiable();
	}

	public List<HE_Vertex> getFaceVertices() {
		final FastList<HE_Vertex> fv = new FastList<HE_Vertex>();
		if (_halfedge == null) {
			return fv;
		}
		HE_Halfedge he = _halfedge;
		do {

			fv.add(he.getVertex());

			he = he.getNextInFace();
		} while (he != _halfedge);
		return fv.asUnmodifiable();
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<HE_TextureCoordinate> getFaceUVWs() {
		final FastList<HE_TextureCoordinate> fv = new FastList<HE_TextureCoordinate>();
		if (_halfedge == null) {
			return fv;
		}
		HE_Halfedge he = _halfedge;
		do {
			if (!fv.contains(he.getVertex())) {
				fv.add(he.getUVW());
			}
			he = he.getNextInFace();
		} while (he != _halfedge);
		return fv.asUnmodifiable();
	}

	/**
	 *
	 *
	 * @return
	 */
	public int getFaceDegree() {
		int result = 0;
		if (_halfedge == null) {
			return 0;
		}
		HE_Halfedge he = _halfedge;
		do {
			result++;
			he = he.getNextInFace();
		} while (he != _halfedge);
		return result;
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<HE_Halfedge> getFaceHalfedges() {
		final FastList<HE_Halfedge> fhe = new FastList<HE_Halfedge>();
		if (_halfedge == null) {
			return fhe;
		}
		HE_Halfedge he = _halfedge;
		do {
			if (!fhe.contains(he)) {
				fhe.add(he);
			}
			he = he.getNextInFace();
		} while (he != _halfedge);
		return fhe.asUnmodifiable();
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<HE_Halfedge> getFaceHalfedgesTwoSided() {
		final FastList<HE_Halfedge> fhe = new FastList<HE_Halfedge>();
		if (_halfedge == null) {
			return fhe;
		}
		HE_Halfedge he = _halfedge;
		do {
			if (!fhe.contains(he)) {
				fhe.add(he);
				if (he.getPair() != null) {
					if (!fhe.contains(he.getPair())) {
						fhe.add(he.getPair());
					}
				}
			}
			he = he.getNextInFace();
		} while (he != _halfedge);
		return fhe.asUnmodifiable();
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<HE_Halfedge> getFaceEdges() {
		final FastList<HE_Halfedge> fe = new FastList<HE_Halfedge>();
		if (_halfedge == null) {
			return fe;
		}
		HE_Halfedge he = _halfedge;
		do {
			if (he.isEdge()) {
				if (!fe.contains(he)) {
					fe.add(he);
				}
			} else {
				if (!fe.contains(he.getPair())) {
					fe.add(he.getPair());
				}
			}
			he = he.getNextInFace();
		} while (he != _halfedge);
		return fe.asUnmodifiable();
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_Halfedge getHalfedge() {
		return _halfedge;
	}

	/**
	 *
	 *
	 * @param v
	 * @return
	 */
	public HE_Halfedge getHalfedge(final HE_Vertex v) {
		HE_Halfedge he = _halfedge;
		if (he == null) {
			return null;
		}
		do {
			if (he.getVertex() == v) {
				return he;
			}
			he = he.getNextInFace();
		} while (he != _halfedge);
		return null;
	}

	/**
	 *
	 *
	 * @param f
	 * @return
	 */
	public HE_Halfedge getHalfedge(final HE_Face f) {
		if (getHalfedge() == null || f == null) {
			return null;
		}
		HE_Halfedge he = getHalfedge();
		do {
			if (he.getPair() != null && he.getPair().getFace() != null && he.getPair().getFace() == f) {
				return he;
			}
			he = he.getNextInFace();
		} while (he != getHalfedge());
		return null;
	}

	/**
	 *
	 *
	 * @param halfedge
	 */
	protected void _setHalfedge(final HE_Halfedge halfedge) {
		_halfedge = halfedge;
	}

	/**
	 *
	 *
	 * @param c
	 */
	public void push(final WB_Coord c) {
		HE_Halfedge he = _halfedge;
		do {
			he.getVertex().getPosition().addSelf(c);
			he = he.getNextInFace();
		} while (he != _halfedge);
	}

	/**
	 *
	 */
	protected void _clearHalfedge() {
		_halfedge = null;
	}

	/**
	 *
	 *
	 * @return
	 * @deprecated Use {@link #getPlane()} instead
	 */
	@Deprecated
	public WB_Plane toPlane() {
		return getPlane();
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Plane getPlane() {
		WB_Coord fn = getFaceNormal();
		if (WB_Vector.getSqLength3D(fn) < 0.5) {
			if (WB_Epsilon.isEqualAbs(_halfedge.getVertex().xd(), _halfedge.getEndVertex().xd())) {
				fn = new WB_Vector(1, 0, 0);
			} else {
				fn = new WB_Vector(0, 0, 1);
			}
		}
		return new WB_Plane(getFaceCenter(), fn);
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public WB_Plane getPlane(final double d) {
		final WB_Coord fn = getFaceNormal();
		return new WB_Plane(WB_Point.addMul(getFaceCenter(), d, fn), fn);
	}

	/**
	 *
	 */
	public void sort() {
		if (_halfedge != null) {
			HE_Halfedge he = _halfedge;
			HE_Halfedge leftmost = he;
			do {
				he = he.getNextInFace();
				if (he.getVertex().compareTo(leftmost.getVertex()) < 0) {
					leftmost = he;
				}
			} while (he != _halfedge);
			_halfedge = leftmost;
		}
	}

	/**
	 *
	 *
	 * @param f
	 * @return
	 */
	@Override
	public int compareTo(final HE_Face f) {

		if (f.getHalfedge() == null) {
			if (getHalfedge() == null) {

				return 0;
			} else {
				return 1;
			}
		} else if (getHalfedge() == null) {
			return -1;
		}

		return getHalfedge().compareTo(f.getHalfedge());
	}

	/**
	 *
	 *
	 * @return
	 */
	@Override
	public int[] getTriangles() {
		return getTriangles(true);
	}

	/**
	 *
	 *
	 * @param optimize
	 * @return
	 */
	public int[] getTriangles(final boolean optimize) {
		if (triangles != null) {
			return triangles;
		}
		final int fo = getFaceDegree();
		if (fo < 3) {
			return new int[] { 0, 0, 0 };
		} else if (fo == 3) {
			return new int[] { 0, 1, 2 };
		} else if (isDegenerate()) {
			triangles = new int[3 * (fo - 2)];
			for (int i = 0; i < fo - 2; i++) {
				triangles[3 * i] = 0;
				triangles[3 * i + 1] = i + 1;
				triangles[3 * i + 2] = i + 2;
			}
			return triangles;
		} else if (fo == 4) {
			final WB_Point[] points = new WB_Point[4];
			int i = 0;
			HE_Halfedge he = _halfedge;
			do {
				points[i] = new WB_Point(he.getVertex().xd(), he.getVertex().yd(), he.getVertex().zd());
				he = he.getNextInFace();
				i++;
			} while (he != _halfedge);

			return triangles = WB_PolygonTriangulatorJTS.triangulateQuad(points[0], points[1], points[2], points[3]);
		}
		return triangles = new WB_PolygonTriangulatorJTS().triangulatePolygon2D(this.toOrthoPolygon(), optimize)
				.getTriangles();
	}

	/**
	 *
	 *
	 * @return
	 * @deprecated Use {@link #getAABB()} instead
	 */
	@Deprecated
	public WB_AABB toAABB() {
		return getAABB();
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_AABB getAABB() {
		if (aabb != null) {
			return aabb;
		}

		aabb = new WB_AABB();
		HE_Halfedge he = getHalfedge();
		do {
			aabb.expandToInclude(he.getVertex());
			he = he.getNextInFace();
		} while (he != getHalfedge());
		return aabb;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Triangle toTriangle() {
		if (getFaceDegree() != 3) {
			return null;
		}
		return new WB_Triangle(_halfedge.getVertex(), _halfedge.getEndVertex(),
				_halfedge.getNextInFace().getEndVertex());
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Polygon toPolygon() {
		final int n = getFaceDegree();
		if (n == 0) {
			return null;
		}
		final WB_Point[] points = new WB_Point[n];
		int i = 0;
		HE_Halfedge he = _halfedge;
		do {
			points[i++] = new WB_Point(he.getVertex().xd(), he.getVertex().yd(), he.getVertex().zd());
			he = he.getNextInFace();
		} while (he != _halfedge);
		return gf.createSimplePolygon(points);
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Polygon toOrthoPolygon() {
		final int n = getFaceDegree();
		if (n == 0) {
			return null;
		}
		WB_OrthoProject op = new WB_OrthoProject(this.getFaceNormal());
		final WB_Point[] points = new WB_Point[n];
		int i = 0;
		HE_Halfedge he = _halfedge;
		do {
			points[i] = new WB_Point();
			op.mapPoint3D(he.getVertex(), points[i]);
			i++;
			he = he.getNextInFace();
		} while (he != _halfedge);
		return gf.createSimplePolygon(points);
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Polygon toPlanarPolygon() {
		final int n = getFaceDegree();
		if (n == 0) {
			return null;
		}
		final WB_Point[] points = new WB_Point[n];
		final WB_Plane P = getPlane();
		int i = 0;
		HE_Halfedge he = _halfedge;
		do {
			points[i] = projectOnPlane(he.getVertex(), P);
			he = he.getNextInFace();
			i++;
		} while (he != _halfedge);
		return gf.createSimplePolygon(points);
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<HE_Face> getNeighborFaces() {
		final FastList<HE_Face> ff = new FastList<HE_Face>();
		if (getHalfedge() == null) {
			return ff;
		}
		HE_Halfedge he = getHalfedge();
		do {
			final HE_Halfedge hep = he.getPair();
			if (hep != null && hep.getFace() != null) {
				if (hep.getFace() != this) {
					if (!ff.contains(hep.getFace())) {
						ff.add(hep.getFace());
					}
				}
			}
			he = he.getNextInFace();
		} while (he != getHalfedge());
		return ff.asUnmodifiable();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.Point3D#toString()
	 */
	@Override
	public String toString() {
		String s = "HE_Face key: " + key() + ". Connects " + getFaceDegree() + " vertices: ";
		HE_Halfedge he = getHalfedge();
		for (int i = 0; i < getFaceDegree() - 1; i++) {
			s += he.getVertex().key + "-";
			he = he.getNextInFace();
		}
		s += he.getVertex().key + "." + " (" + getUserLabel() + "," + getInternalLabel() + ")";
		return s;
	}

	/**
	 *
	 *
	 * @return
	 */
	public boolean isPlanar() {
		final WB_Plane P = getPlane();
		HE_Halfedge he = getHalfedge();
		do {
			if (!WB_Epsilon.isZero(WB_GeometryOp3D.getDistance3D(he.getVertex(), P))) {
				return false;
			}
			he = he.getNextInFace();
		} while (he != getHalfedge());
		return true;
	}

	/**
	 * Checks if is boundary.
	 *
	 * @return true, if is boundary
	 */
	public boolean isBoundary() {
		HE_Halfedge he = _halfedge;
		do {
			if (he.getPair().getFace() == null) {
				return true;
			}
			he = he.getNextInFace();
		} while (he != _halfedge);
		return false;
	}

	/**
	 *
	 *
	 * @return
	 */
	public boolean isDegenerate() {
		return WB_Vector.getLength3D(getFaceNormal()) < 0.5;
	}

	/**
	 *
	 *
	 * @param el
	 */
	public void copyProperties(final HE_Face el) {
		super.copyProperties(el);
		textureId = el.textureId;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Element#clear()
	 */
	@Override
	public void clear() {
		_halfedge = null;
	}

	/**
	 *
	 */
	public void checkValidity() {
		final Coordinate[] coords = new Coordinate[getFaceDegree() + 1];
		final WB_Point point = gf.createPoint();
		final WB_Map2D context = gf.createEmbeddedPlane(getPlane());
		HE_Halfedge he = _halfedge;
		int i = 0;
		do {
			context.mapPoint3D(he.getVertex(), point);
			coords[i] = new Coordinate(point.xd(), point.yd(), i);
			he = he.getNextInFace();
			i++;
		} while (he != _halfedge);
		context.mapPoint3D(he.getVertex(), point);
		coords[i] = new Coordinate(point.xd(), point.yd(), i);
		he = he.getNextInFace();
		final Polygon inputPolygon = new GeometryFactory().createPolygon(coords);
		final IsValidOp isValidOp = new IsValidOp(inputPolygon);
		if (!IsValidOp.isValid(inputPolygon)) {
			System.out.println(this);
			System.out.println(this.getFaceArea() + " " + this.getFaceNormal());
			he = _halfedge;
			i = 0;
			do {
				System.out.println("  " + i + ": " + he.getVertex());
				he = he.getNextInFace();
				i++;
			} while (he != _halfedge);
			System.out.println(isValidOp.getValidationError());
		}
	}

	/**
	 *
	 *
	 * @return
	 */
	public int getTextureId() {
		return textureId;
	}

	/**
	 *
	 *
	 * @param i
	 */
	public void setTextureId(final int i) {
		textureId = i;
	}

	@Override
	public void clearPrecomputed() {
		triangles = null;
		normal = null;
		center = null;
		aabb = null;

	}

	public WB_Coord getClosestPoint(final WB_Coord p) {
		if (this.getFaceDegree() == 3) {
			return WB_GeometryOp3D.getClosestPointToTriangle3D(p, getHalfedge().getVertex(),
					getHalfedge().getNextInFace().getVertex(), getHalfedge().getNextInFace(2).getVertex());

		}

		List<HE_Vertex> points = this.getFaceVertices();
		int[] tris = this.getTriangles();
		final int n = tris.length;
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		for (int i = 0; i < n; i += 3) {
			tmp = WB_GeometryOp3D.getClosestPointToTriangle3D(p, points.get(tris[i]), points.get(tris[i + 1]),
					points.get(tris[i + 2]));
			final double d2 = WB_GeometryOp3D.getSqDistance3D(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}
		}
		return closest;
	}

	public boolean isNeighbor(final HE_Face f) {
		if (getHalfedge() == null) {
			return false;
		}
		HE_Halfedge he = getHalfedge();
		do {
			if (he.getPair() != null && he.getPair().getFace() != null && he.getPair().getFace() == f) {
				return true;
			}
			he = he.getNextInFace();
		} while (he != getHalfedge());
		return false;
	}

	public WB_CoordinateSystem3D getOrthonormalBase() {
		if (getHalfedge() == null) {
			return null;
		}
		HE_Halfedge he = getHalfedge();
		WB_Vector u = new WB_Vector(he.getHalfedgeTangent());
		WB_Coord n = getFaceNormal();
		WB_Vector v = WB_Vector.cross(n, u);
		return new WB_CoordinateSystem3D(getFaceCenter(), u, v, n);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_TriangleGenerator#getPoints()
	 */
	@Override
	public WB_CoordCollection getPoints() {

		return WB_CoordCollection.getCollection(getFaceVertices());
	}

}
