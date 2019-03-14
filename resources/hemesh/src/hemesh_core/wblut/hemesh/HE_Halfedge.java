/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 *
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 *
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.hemesh;

import wblut.geom.WB_Classification;
import wblut.geom.WB_Coord;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_GeometryOp3D;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;
import wblut.math.WB_HashCode;

/**
 * Half-edge element of half-edge data structure.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HE_Halfedge extends HE_MeshElement implements Comparable<HE_Halfedge> {
	/** Start vertex of halfedge. */
	private HE_Vertex _vertex;
	/** Halfedge pair. */
	private HE_Halfedge _pair;
	/** Next halfedge in face. */
	private HE_Halfedge _next;
	/** Previous halfedge in face. */
	private HE_Halfedge _prev;
	/** Associated face. */
	private HE_Face _face;
	/** Associated edge. */

	private HE_TextureCoordinate uvw;

	/**
	 * Instantiates a new HE_Halfedge.
	 */
	public HE_Halfedge() {
		super();
		uvw = null;
		_vertex = null;
		_pair = null;
		_next = null;
		_prev = null;
		_face = null;

	}

	/**
	 * Get key.
	 *
	 * @return key
	 */
	public long key() {
		return super.getKey();
	}

	/**
	 * Get previous halfedge in face.
	 *
	 * @return previous halfedge
	 */
	public HE_Halfedge getPrevInFace() {
		return _prev;
	}

	/**
	 * Get n'th previous halfedge in face.
	 *
	 * @return
	 */
	public HE_Halfedge getPrevInFace(final int n) {
		HE_Halfedge he = this;
		for (int i = 0; i < n; i++) {
			he = he.getPrevInFace();
		}
		return he;
	}

	/**
	 * Get next halfedge in face.
	 *
	 * @return next halfedge
	 */
	public HE_Halfedge getNextInFace() {
		return _next;
	}

	/**
	 * Get n'th next halfedge in face.
	 *
	 * @return next halfedge
	 */
	public HE_Halfedge getNextInFace(final int n) {
		HE_Halfedge he = this;
		for (int i = 0; i < n; i++) {
			he = he.getNextInFace();
		}
		return he;
	}

	/**
	 * Get next halfedge in vertex.
	 *
	 * @return next halfedge
	 */
	public HE_Halfedge getNextInVertex() {
		if (_pair == null) {
			return null;
		}
		return _pair.getNextInFace();
	}

	/**
	 * Get n'th next halfedge in vertex.
	 *
	 * @return
	 */
	public HE_Halfedge getNextInVertex(final int n) {
		HE_Halfedge he = this;
		for (int i = 0; i < n; i++) {
			he = he.getNextInVertex();
		}
		return he;
	}

	/**
	 * Get previous halfedge in vertex.
	 *
	 * @return
	 */
	public HE_Halfedge getPrevInVertex() {
		if (_prev == null) {
			return null;
		}
		return getPrevInFace().getPair();
	}

	/**
	 * Get n'th previous halfedge in vertex.
	 *
	 * @return
	 */
	public HE_Halfedge getPrevInVertex(final int n) {
		HE_Halfedge he = this;
		for (int i = 0; i < n; i++) {
			he = he.getPrevInVertex();
		}
		return he;
	}

	/**
	 * Get paired halfedge.
	 *
	 * @return paired halfedge
	 */
	public HE_Halfedge getPair() {
		return _pair;
	}

	/**
	 * Set next halfedge in face.
	 *
	 * @param he
	 *            next halfedge
	 */
	protected void _setNext(final HE_Halfedge he) {
		_next = he;
	}

	/**
	 * Sets previous halfedge in face, only to be called by setNext.
	 *
	 * @param he
	 *            next halfedge
	 */
	protected void _setPrev(final HE_Halfedge he) {
		_prev = he;
	}

	/**
	 * Pair halfedges.
	 *
	 * @param he
	 *            halfedge to pair
	 */
	protected void _setPair(final HE_Halfedge he) {
		_pair = he;
	}

	/**
	 * Get type of face vertex associated with halfedge.
	 *
	 * @return HE.FLAT, HE.CONVEX, HE.CONCAVE
	 */
	public WB_Classification getHalfedgeType() {
		if (_vertex == null) {
			return null;
		}
		WB_Vector v = WB_Vector.subToVector3D(_vertex, getPrevInFace()._vertex);
		v.normalizeSelf();
		final WB_Vector vn = WB_Vector.subToVector3D(getNextInFace()._vertex, _vertex);
		vn.normalizeSelf();
		v = v.cross(vn);
		final WB_Coord n;
		if (_face == null) {
			n = WB_Vector.mul(_pair._face.getFaceNormal(), -1);
		} else {
			n = _face.getFaceNormal();
		}
		final double dot = v.dot(n);
		if (v.isParallel(vn)) {
			return WB_Classification.FLAT;
		} else if (dot > -WB_Epsilon.EPSILON) {
			return WB_Classification.CONVEX;
		} else {
			return WB_Classification.CONCAVE;
		}
	}

	public WB_Coord getHalfedgeVector() {
		if (_pair != null && _vertex != null && _pair.getVertex() != null) {
			final WB_Vector v = WB_Vector.subToVector3D(_pair.getVertex(), _vertex);
			return v;
		}
		return null;
	}

	/**
	 * Get tangent WB_Vector of halfedge.
	 *
	 * @return tangent
	 */
	public WB_Coord getHalfedgeTangent() {
		if (_pair != null && _vertex != null && _pair.getVertex() != null) {
			final WB_Vector v = WB_Vector.subToVector3D(_pair.getVertex(), _vertex);
			v.normalizeSelf();
			return v;
		}
		return null;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getEdgeTangent() {
		final WB_Coord v = getHalfedgeTangent();
		if (v == null) {
			return null;
		}
		return isEdge() ? v : WB_Vector.mul(v, -1);
	}

	/**
	 * Get center of halfedge.
	 *
	 * @return center
	 */
	public WB_Coord getHalfedgeCenter() {
		if (_next != null && _vertex != null && _next.getVertex() != null) {
			return gf.createMidpoint(_next.getVertex(), _vertex);
		}
		return null;
	}

	/**
	 * Get offset center of halfedge.
	 *
	 * @param f
	 * @return center
	 */
	public WB_Coord getHalfedgeCenter(final double f) {
		if (_next != null && _vertex != null && _next.getVertex() != null) {
			return gf.createMidpoint(_next.getVertex(), _vertex).addMulSelf(f, getHalfedgeNormal());
		}
		return null;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getEdgeCenter() {
		if (_next != null && _vertex != null && _next.getVertex() != null) {
			return gf.createMidpoint(_next.getVertex(), _vertex);
		}
		return null;
	}

	/**
	 *
	 *
	 * @param f
	 * @return
	 */
	public WB_Coord getEdgeCenter(final double f) {
		if (_next != null && _vertex != null && _next.getVertex() != null) {
			return gf.createMidpoint(_next.getVertex(), _vertex).addMulSelf(f, getEdgeNormal());
		}
		return null;
	}

	/**
	 * Get edge of halfedge.
	 *
	 * @return edge
	 */
	public HE_Halfedge getEdge() {
		if (isEdge()) {
			return this;
		}
		return _pair;
	}

	/**
	 * Get face of halfedge.
	 *
	 * @return face
	 */
	public HE_Face getFace() {
		return _face;
	}

	/**
	 * Sets the face.
	 *
	 * @param face
	 *            the new face
	 */
	protected void _setFace(final HE_Face face) {
		_face = face;
	}

	/**
	 * Get vertex of halfedge.
	 *
	 * @return vertex
	 */
	public HE_Vertex getVertex() {
		return _vertex;
	}

	public WB_Point getPosition() {
		return _vertex.getPosition();
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_Vertex getStartVertex() {
		return _vertex;
	}

	public WB_Point getStartPosition() {
		return _vertex.getPosition();
	}

	/**
	 * Sets vertex.
	 *
	 * @param vertex
	 *            vertex
	 */
	protected void _setVertex(final HE_Vertex vertex) {
		_vertex = vertex;
	}

	/**
	 * Get end vertex of halfedge.
	 *
	 * @return vertex
	 */
	public HE_Vertex getEndVertex() {
		if (_pair != null) {
			return _pair._vertex;
		}
		if (_next != null) {
			return _next._vertex;
		}
		return null;
	}

	/**
	 * Get end position of halfedge.
	 *
	 * @return vertex
	 */
	public WB_Point getEndPosition() {
		if (_pair != null) {
			return _pair._vertex.getPosition();
		}
		if (_next != null) {
			return _next._vertex.getPosition();
		}
		return null;
	}

	/**
	 * Clear next.
	 */
	protected void _clearNext() {
		_next = null;
	}

	/**
	 * Clear prev.
	 */
	protected void _clearPrev() {
		_prev = null;
	}

	/**
	 * Clear pair.
	 */
	protected void _clearPair() {
		_pair = null;
	}

	/**
	 * Clear face.
	 */
	protected void _clearFace() {
		_face = null;
	}

	/**
	 * Clear vertex.
	 */
	protected void _clearVertex() {
		_vertex = null;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getEdgeNormal() {
		if (_pair == null) {
			return null;
		}
		HE_Halfedge he1, he2;
		if (isEdge()) {
			he1 = this;
			he2 = _pair;
		} else {
			he1 = _pair;
			he2 = this;
		}
		if (he1._face == null && he2._face == null) {
			return null;
		}
		final WB_Coord n1 = he1._face != null ? he1._face.getFaceNormal() : new WB_Vector(0, 0, 0);
		final WB_Coord n2 = he2._face != null ? he2._face.getFaceNormal() : new WB_Vector(0, 0, 0);
		final WB_Vector n = new WB_Vector(n1.xd() + n2.xd(), n1.yd() + n2.yd(), n1.zd() + n2.zd());
		n.normalizeSelf();
		return n;
	}

	/**
	 * Get halfedge normal.
	 *
	 * @return in-face normal of face, points inwards
	 */
	public WB_Coord getHalfedgeNormal() {
		WB_Coord fn;
		if (getFace() == null && getPair() == null) {
			return null;
		}
		if (getFace() == null) {
			if (getPair().getFace() == null) {
				return null;
			}
			fn = getPair().getFace().getFaceNormal();
		} else {
			fn = getFace().getFaceNormal();
		}
		final HE_Vertex vn = getNextInFace().getVertex();
		final WB_Vector _normal = new WB_Vector(vn);
		_normal.subSelf(getVertex());
		_normal.set(WB_Vector.cross(fn, _normal));
		_normal.normalizeSelf();
		return _normal;
	}

	/**
	 * Get area of faces bounding halfedge.
	 *
	 * @return area
	 */
	public double getHalfedgeArea() {
		return 0.5 * getEdgeArea();
	}

	/**
	 * Get angle between adjacent faces.
	 *
	 * @return angle
	 */
	public double getHalfedgeDihedralAngle() {
		if (isOuterBoundary() || isInnerBoundary()) {
			return 0.0;
		}
		WB_Coord n1 = getFace().getFaceNormal();
		WB_Coord n2 = getPair().getFace().getFaceNormal();
		WB_Coord w = getHalfedgeTangent();
		double cosTheta = WB_Vector.dot(n1, n2);
		double sinTheta = WB_Vector.cross(n1, n2).dot(w);
		return Math.atan2(sinTheta, cosTheta);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.Point3D#toString()
	 */
	@Override
	public String toString() {
		return "HE_Halfedge key: " + key() + ", paired with halfedge " + getPair().key() + ". Vertex: "
				+ getVertex().key() + ". Is this an edge: " + isEdge() + "." + " (" + getUserLabel() + ","
				+ getInternalLabel() + ")";
	}

	/**
	 *
	 *
	 * @return
	 */
	public double getLength() {
		return WB_GeometryOp3D.getDistance3D(getVertex(), getEndVertex());
	}

	/**
	 *
	 *
	 * @return
	 */
	public double getSqLength() {
		return WB_GeometryOp3D.getSqDistance3D(getVertex(), getEndVertex());
	}

	/**
	 * A halfedge is considered an edge if it has a paired halfedge and one of
	 * these conditions is met:
	 *
	 * a) both the halfedge and its pair have no face, and the halfedge key is
	 * lower b) the halfedge has a face and its pair has no face c) both the
	 * halfedge and its pair have a face, and the halfedge key is lower.
	 *
	 * @return
	 */
	public boolean isEdge() {
		if (_pair == null) {
			return false;
		}
		if (_face == null) {
			if (_pair._face == null) {// both halfedges are faceless
				return key < _pair.key;
			} else {
				return false;
			}
		} else if (_pair._face == null) {
			return true;
		}

		return key < _pair.key;

	}

	/**
	 *
	 *
	 * @return
	 * @deprecated Use {@link #isInnerBoundary()} instead
	 */
	@Deprecated
	public boolean isBoundary() {
		return isInnerBoundary();
	}

	/**
	 *
	 *
	 * @return
	 */
	public boolean isInnerBoundary() {
		if (_face == null || _pair == null) {
			return false;
		}
		if (_pair._face == null) {
			return true;
		}
		return false;
	}

	/**
	 *
	 *
	 * @return
	 */
	public boolean isOuterBoundary() {
		if (_face == null) {
			return true;
		}
		return false;
	}

	/**
	 * Get area of faces bounding edge.
	 *
	 * @return area
	 */
	public double getEdgeArea() {
		if (_pair == null) {
			return Double.NaN;
		}
		HE_Halfedge he1, he2;
		if (isEdge()) {
			he1 = this;
			he2 = _pair;
		} else {
			he1 = _pair;
			he2 = this;
		}
		if (he1._face == null && he2._face == null) {
			return Double.NaN;
		}
		double result = 0;
		int n = 0;
		if (he1._face != null) {
			result += he1._face.getFaceArea();
			n++;
		}
		if (he2._face != null) {
			result += he2._face.getFaceArea();
			n++;
		}
		return result / n;
	}

	/**
	 * Return angle between adjacent faces.
	 *
	 * @return angle
	 */
	public double getEdgeDihedralAngle() {
		if (_pair == null) {
			return 0.0;
		}
		HE_Halfedge he1, he2;
		if (isEdge()) {
			he1 = this;
			he2 = _pair;
		} else {
			he1 = _pair;
			he2 = this;
		}
		if (he1._face == null || he2._face == null) {
			return 0.0;
		} else {
			final WB_Coord n1 = he1._face.getFaceNormal();
			final WB_Coord n2 = he2._face.getFaceNormal();
			return WB_GeometryOp.getDihedralAngleNorm(n1, n2);
		}
	}

	public double getEdgeCosDihedralAngle() {
		if (_pair == null) {
			return Double.NaN;
		}
		HE_Halfedge he1, he2;
		if (isEdge()) {
			he1 = this;
			he2 = _pair;
		} else {
			he1 = _pair;
			he2 = this;
		}
		if (he1._face == null || he2._face == null) {
			return Double.NaN;
		} else {
			final WB_Coord n1 = he1._face.getFaceNormal();
			final WB_Coord n2 = he2._face.getFaceNormal();
			return WB_GeometryOp.getCosDihedralAngleNorm(n1, n2);
		}
	}

	/**
	 *
	 *
	 * @param el
	 */
	public void copyProperties(final HE_Halfedge el) {
		super.copyProperties(el);
		if (el.getUVW() == null) {
			uvw = null;
		} else {
			uvw = new HE_TextureCoordinate(el.getUVW());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Element#clear()
	 */
	@Override
	protected void clear() {
		_face = null;
		_next = null;
		_pair = null;
		_vertex = null;
		uvw = null;
	}

	/**
	 *
	 *
	 * @return
	 */
	public double getAngle() {
		final WB_Coord c = getVertex();
		final WB_Coord p1 = getEndVertex();
		final WB_Coord p2 = this.getPrevInFace().getVertex();
		if (c == null) {
			return Double.NaN;
		}
		return WB_GeometryOp3D.getAngleBetween(c.xd(), c.yd(), c.zd(), p1.xd(), p1.yd(), p1.zd(), p2.xd(), p2.yd(),
				p2.zd());
	}

	/**
	 * Computes cotangent of the angle opposite this halfedge. Triangles only
	 *
	 * @return
	 */
	public double getCotan() {
		return HET_MeshOp.getCotan(this);
	}

	// TEXTURE COORDINATES

	/**
	 * Get texture coordinate belonging to the halfedge vertex in this face. If
	 * no halfedge UVW exists, returns the vertex UVW. If neither exist, zero
	 * coordinates are returned.
	 *
	 * @return
	 */
	public HE_TextureCoordinate getUVW() {
		if (uvw == null) {
			if (_vertex != null) {
				return _vertex.getVertexUVW();
			} else {
				return HE_TextureCoordinate.ZERO;
			}
		}
		return uvw;
	}

	/**
	 * Get texture coordinate belonging to this halfedge . If no halfedge UVW
	 * exists, zero coordinates are returned.
	 *
	 * @return
	 */
	public HE_TextureCoordinate getHalfedgeUVW() {
		if (uvw == null) {
			return HE_TextureCoordinate.ZERO;
		}
		return uvw;
	}

	/**
	 * Get texture coordinate belonging to the halfedge vertex. If no vertex UVW
	 * exists, zero coordinates are returned.
	 *
	 * @return
	 */
	public HE_TextureCoordinate getVertexUVW() {
		if (_vertex != null) {
			return _vertex.getVertexUVW();
		} else {
			return HE_TextureCoordinate.ZERO;
		}
	}

	/**
	 * Clear halfedge UVW.
	 */
	public void clearUVW() {
		uvw = null;
	}

	/**
	 * Set halfedge UVW.
	 *
	 * @param u
	 * @param v
	 * @param w
	 */
	public void setUVW(final double u, final double v, final double w) {
		uvw = new HE_TextureCoordinate(u, v, w);
	}

	/**
	 * Set halfedge UVW.
	 *
	 * @param uvw
	 *            WB_Coord
	 */
	public void setUVW(final WB_Coord uvw) {
		if (uvw == null) {
			return;
		}
		this.uvw = new HE_TextureCoordinate(uvw);
	}

	/**
	 * Set halfedge UVW.
	 *
	 * @param uvw
	 *            HE_TextureCoordinate
	 */
	public void setUVW(final HE_TextureCoordinate uvw) {
		if (uvw == null) {
			return;
		}
		this.uvw = new HE_TextureCoordinate(uvw);
	}

	/**
	 * Check if this halfedge has texture coordinates.
	 *
	 * @return
	 */
	public boolean hasHalfedgeUVW() {
		return uvw != null;
	}

	/**
	 * Check if the halfedge vertex has a UVW for this face, either a halfedge
	 * UVW or a vertex UVW.
	 *
	 *
	 * @return
	 */
	public boolean hasUVW() {
		if (uvw != null) {
			return true;
		}
		if (_vertex != null && _vertex.hasVertexUVW()) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the halfedge vertex has a vertex UVW.
	 *
	 * @return
	 */
	public boolean hasVertexUVW() {
		if (_vertex != null && _vertex.hasVertexUVW()) {
			return true;
		}
		return false;
	}

	/**
	 *
	 *
	 * @param he
	 * @return
	 */
	@Override
	public int compareTo(final HE_Halfedge he) {

		if (he.getVertex() == null) {
			if (getVertex() == null) {
				return Long.compare(key(), he.key());
			} else {
				return 1;
			}
		} else if (getVertex() == null) {
			return -1;
		}
		return getVertex().compareTo(he.getVertex());
		// return cmp == 0 ? Long.compare(key(), he.key()) : 0;
	}

	@Override
	public int hashCode() {
		if (getVertex() == null) {
			return WB_HashCode.calculateHashCode(0, 0, 0);
		}
		return getVertex().hashCode();

	}

	@Override
	public void clearPrecomputed() {

	}

}
