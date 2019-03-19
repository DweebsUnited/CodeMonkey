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

import wblut.geom.WB_Classification;
import wblut.geom.WB_Coord;
import wblut.geom.WB_CoordinateSystem3D;
import wblut.geom.WB_MutableCoord;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;
import wblut.math.WB_HashCode;

/**
 * Vertex element of half-edge mesh.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HE_Vertex extends HE_MeshElement implements WB_MutableCoord {
	private WB_Point pos;

	/** Halfedge associated with this vertex. */
	private HE_Halfedge _halfedge;

	private HE_TextureCoordinate uvw = null;

	/**
	 * Instantiates a new HE_Vertex.
	 */
	public HE_Vertex() {
		super();
		pos = new WB_Point();
		uvw = null;

	}

	/**
	 * Instantiates a new HE_Vertex at position x, y, z.
	 *
	 * @param x
	 *            x-coordinate of vertex
	 * @param y
	 *            y-coordinate of vertex
	 * @param z
	 *            z-coordinate of vertex
	 */
	public HE_Vertex(final double x, final double y, final double z) {
		super();
		pos = new WB_Point(x, y, z);
		uvw = null;
	}

	/**
	 * Instantiates a new HE_Vertex at position v.
	 *
	 * @param v
	 *            position of vertex
	 */
	public HE_Vertex(final WB_Coord v) {
		super();
		pos = new WB_Point(v);
		uvw = null;
	}

	public WB_Point getPosition() {
		return pos;
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_VertexEdgeCirculator veCrc() {
		return new HE_VertexEdgeCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_VertexFaceCirculator vfCrc() {
		return new HE_VertexFaceCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_VertexVertexCirculator vvCrc() {
		return new HE_VertexVertexCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_VertexHalfedgeInCirculator vheiCrc() {
		return new HE_VertexHalfedgeInCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_VertexHalfedgeOutCirculator vheoCrc() {
		return new HE_VertexHalfedgeOutCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_VertexEdgeRevCirculator veRevCrc() {
		return new HE_VertexEdgeRevCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_VertexFaceRevCirculator vfRevCrc() {
		return new HE_VertexFaceRevCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_VertexVertexRevCirculator vvRevCrc() {
		return new HE_VertexVertexRevCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_VertexHalfedgeInRevCirculator vheiRevCrc() {
		return new HE_VertexHalfedgeInRevCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_VertexHalfedgeOutRevCirculator vheoRevCrc() {
		return new HE_VertexHalfedgeOutRevCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_Vertex copy() {
		final HE_Vertex copy = new HE_Vertex(getPosition());
		copy.copyProperties(this);
		return copy;
	}

	/**
	 * Get halfedge associated with this vertex.
	 *
	 * @return halfedge
	 */
	public HE_Halfedge getHalfedge() {
		return _halfedge;
	}

	/**
	 * Sets the halfedge associated with this vertex.
	 *
	 * @param halfedge
	 *            the new halfedge
	 */
	protected void _setHalfedge(final HE_Halfedge halfedge) {
		_halfedge = halfedge;
	}

	public void setHalfedge(final HE_Halfedge halfedge) {
		this._setHalfedge( halfedge );
	}

	/**
	 * Set position to v.
	 *
	 * @param v
	 *            position
	 */
	public void set(final HE_Vertex v) {
		pos.set(v);
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public WB_Point getOffset(final double d) {
		return WB_Point.addMul(getPosition(), d, getVertexNormal());
	}

	public HE_Vertex getNextInFace() {
		return getHalfedge().getNextInFace().getVertex();
	}

	public HE_Vertex getPrevInFace() {
		return getHalfedge().getPrevInFace().getVertex();
	}

	/**
	 * Get vertex type. Returns stored value if update status is true.
	 *
	 * @return HE.VertexType.FLAT: vertex is flat in all faces,
	 *         HE.VertexType.CONVEX: vertex is convex in all faces,
	 *         HE.VertexType.CONCAVE: vertex is concave in all faces,
	 *         HE.VertexType.FLATCONVEX: vertex is convex or flat in all faces,
	 *         HE.VertexType.FLATCONCAVE: vertex is concave or flat in all
	 *         faces, HE.VertexType.SADDLE: vertex is convex and concave in at
	 *         least one face each
	 */
	public WB_Classification getVertexType() {
		return HET_MeshOp.getVertexType(this);
	}

	/**
	 * Clear halfedge.
	 */
	protected void _clearHalfedge() {
		_halfedge = null;
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
	 * Get halfedges in vertex.
	 *
	 * @return halfedges
	 */
	public List<HE_Halfedge> getHalfedgeStar() {
		final List<HE_Halfedge> vhe = new FastList<HE_Halfedge>();
		if (getHalfedge() == null) {
			return vhe;
		}
		HE_Halfedge he = getHalfedge();

		do {
			if (!vhe.contains(he)) {
				vhe.add(he);
			}
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return vhe;
	}

	/**
	 * Get edges in vertex.
	 *
	 * @return edges
	 */
	public List<HE_Halfedge> getEdgeStar() {
		final List<HE_Halfedge> ve = new FastList<HE_Halfedge>();
		if (getHalfedge() == null) {
			return ve;
		}
		HE_Halfedge he = getHalfedge();
		do {
			if (he.isEdge()) {
				if (!ve.contains(he)) {
					ve.add(he);
				}
			} else {
				if (!ve.contains(he.getPair())) {
					ve.add(he.getPair());
				}
			}
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return ve;
	}

	/**
	 * Get faces in vertex.
	 *
	 * @return faces
	 */
	public List<HE_Face> getFaceStar() {
		final List<HE_Face> vf = new FastList<HE_Face>();
		if (getHalfedge() == null) {
			return vf;
		}
		HE_Halfedge he = getHalfedge();
		do {
			if (he.getFace() != null) {
				if (!vf.contains(he.getFace())) {
					vf.add(he.getFace());
				}
			}
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return vf;
	}

	/**
	 * Get neighboring vertices.
	 *
	 * @return neighbors
	 */
	public List<HE_Vertex> getNeighborVertices() {
		final List<HE_Vertex> vv = new FastList<HE_Vertex>();
		if (getHalfedge() == null) {
			return vv;
		}
		HE_Halfedge he = getHalfedge();
		do {
			final HE_Halfedge hen = he.getNextInFace();
			if (hen.getVertex() != this && !vv.contains(hen.getVertex())) {
				vv.add(hen.getVertex());
			}
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return vv;
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<HE_Vertex> getVertexStar() {
		return getNeighborVertices();
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<HE_Vertex> getNextNeighborVertices() {
		final List<HE_Vertex> result = new FastList<HE_Vertex>();
		if (getHalfedge() == null) {
			return result;
		}
		final List<HE_Vertex> vv = getNeighborVertices();
		for (final HE_Vertex v : vv) {
			result.addAll(v.getNeighborVertices());
		}
		final Iterator<HE_Vertex> vitr = result.iterator();
		HE_Vertex w;
		while (vitr.hasNext()) {
			w = vitr.next();
			if (w == this || vv.contains(w)) {
				vitr.remove();
			}
		}
		return result;
	}

	/**
	 * Gets the neighbors as points.
	 *
	 * @return the neighbors as points
	 */
	public WB_Coord[] getNeighborsAsPoints() {
		final WB_Coord[] vv = new WB_Coord[getVertexDegree()];
		if (getHalfedge() == null) {
			return vv;
		}
		HE_Halfedge he = getHalfedge();
		int i = 0;
		do {
			vv[i] = he.getEndVertex();
			i++;
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return vv;
	}

	/**
	 * Get number of edges in vertex.
	 *
	 * @return number of edges
	 */
	public int getVertexDegree() {
		int result = 0;
		if (getHalfedge() == null) {
			return 0;
		}
		HE_Halfedge he = getHalfedge();
		do {
			result++;
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return result;
	}

	/**
	 * Get area of faces bounding vertex.
	 *
	 * @return area
	 */
	public double getVertexArea() {
		return HET_MeshOp.getVertexArea(this);
	}

	/**
	 * Get the barycentric dual area. Triangles only.
	 *
	 * @return
	 */
	public double getBarycentricDualArea() {
		return HET_MeshOp.getBarycentricDualVertexArea(this);
	}

	/**
	 * Get the circumcentric dual area. Triangles only.
	 *
	 * @return
	 */
	public double getCircumcentricDualArea() {
		return HET_MeshOp.getCircumcentricDualVertexArea(this);
	}

	/**
	 * Checks if is boundary.
	 *
	 * @return true, if is boundary
	 */
	public boolean isBoundary() {
		HE_Halfedge he = _halfedge;
		do {
			if (he.getFace() == null) {
				return true;
			}
			he = he.getNextInVertex();
		} while (he != _halfedge);
		return false;
	}

	public boolean isIsolated() {

		return _halfedge == null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coord#xd()
	 */
	@Override
	public double xd() {
		return getPosition().xd();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#yd()
	 */
	@Override
	public double yd() {
		return getPosition().yd();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#zd()
	 */
	@Override
	public double zd() {
		return getPosition().zd();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#zd()
	 */
	@Override
	public double wd() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#getd(int)
	 */
	@Override
	public double getd(final int i) {

		return getPosition().getd(i);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#xf()
	 */
	@Override
	public float xf() {
		return getPosition().xf();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#yf()
	 */
	@Override
	public float yf() {
		return getPosition().yf();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#zf()
	 */
	@Override
	public float zf() {
		return getPosition().zf();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#zf()
	 */
	@Override
	public float wf() {
		return 1.0f;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#getf(int)
	 */
	@Override
	public float getf(final int i) {
		return getPosition().getf(i);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#setX(double)
	 */
	@Override
	public void setX(final double x) {
		pos.setX(x);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#setY(double)
	 */
	@Override
	public void setY(final double y) {
		pos.setY(y);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#setZ(double)
	 */
	@Override
	public void setZ(final double z) {
		pos.setZ(z);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#setW(double)
	 */
	@Override
	public void setW(final double w) {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#setCoord(int, double)
	 */
	@Override
	public void setCoord(final int i, final double v) {
		pos.setCoord(i, v);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#set(wblut.geom.WB_Coordinate)
	 */
	@Override
	public void set(final WB_Coord p) {
		pos.set(p);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#set(double, double)
	 */
	@Override
	public void set(final double x, final double y) {
		pos.set(x, y);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#set(double, double, double)
	 */
	@Override
	public void set(final double x, final double y, final double z) {
		pos.set(x, y, z);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#set(double, double, double, double)
	 */
	@Override
	public void set(final double x, final double y, final double z, final double w) {
		pos.set(x, y, z, w);
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_CoordinateSystem3D getVertexCS() {
		return HET_MeshOp.getVertexCS(this);
	}

	// Common area-weighted mean normal
	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getVertexNormal() {
		return HET_MeshOp.getVertexNormal(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getVertexNormalAverage() {
		return HET_MeshOp.getVertexNormalAverage(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getVertexNormalArea() {
		return HET_MeshOp.getVertexNormalArea(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getVertexNormalAngle() {
		return HET_MeshOp.getVertexNormalAngle(this);

	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getVertexNormalGaussCurvature() {
		return HET_MeshOp.getVertexNormalGaussCurvature(this);

	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getVertexNormalMeanCurvature() {
		return HET_MeshOp.getVertexNormalMeanCurvature(this);

	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getVertexNormalSphereInscribed() {
		return HET_MeshOp.getVertexNormalSphereInscribed(this);

	}

	/**
	 * Returns the discrete Gaussian curvature and the mean normal. These
	 * discrete operators are described in "Discrete Differential-Geometry
	 * Operators for Triangulated 2-Manifolds", Mark Meyer, Mathieu Desbrun,
	 * Peter Schr???der, and Alan H. Barr.
	 * http://www.cs.caltech.edu/~mmeyer/Publications/diffGeomOps.pdf
	 * http://www.cs.caltech.edu/~mmeyer/Publications/diffGeomOps.pdf Note: on a
	 * sphere, the Gaussian curvature is very accurate, but not the mean
	 * curvature. Guoliang Xu suggests improvements in his papers
	 * http://lsec.cc.ac.cn/~xuguo/xuguo3.htm
	 *
	 * @param meanCurvatureVector
	 * @return
	 */
	public double getGaussianCurvature(final WB_Vector meanCurvatureVector) {
		return HET_MeshOp.getGaussianCurvature(this, meanCurvatureVector);
	}

	/**
	 * Returns the discrete Gaussian curvature. These discrete operators are
	 * described in "Discrete Differential-Geometry Operators for Triangulated
	 * 2-Manifolds", Mark Meyer, Mathieu Desbrun, Peter Schr???der, and Alan H.
	 * Barr. http://www.cs.caltech.edu/~mmeyer/Publications/diffGeomOps.pdf
	 * http://www.cs.caltech.edu/~mmeyer/Publications/diffGeomOps.pdf Note: on a
	 * sphere, the Gaussian curvature is very accurate, but not the mean
	 * curvature. Guoliang Xu suggests improvements in his papers
	 * http://lsec.cc.ac.cn/~xuguo/xuguo3.htm
	 *
	 *
	 * @return
	 */
	public double getGaussCurvature() {
		return HET_MeshOp.getGaussCurvature(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_CoordinateSystem3D getCurvatureDirections() {
		return HET_MeshOp.getCurvatureDirections(this);
	}

	/**
	 *
	 *
	 * @param el
	 */
	public void copyProperties(final HE_Vertex el) {
		super.copyProperties(el);

		if (el.getVertexUVW() == null) {
			uvw = null;
		} else {
			uvw = new HE_TextureCoordinate(el.getVertexUVW());
		}
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
	 *
	 * @return
	 */
	public double getUmbrellaAngle() {
		return HET_MeshOp.getUmbrellaAngle(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public double getAngleDefect() {
		return HET_MeshOp.getAngleDefect(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public double getScalarGaussCurvature() {
		return HET_MeshOp.getScalarGaussCurvature(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public double getScalarMeanCurvature() {
		return HET_MeshOp.getScalarMeanCurvature(this);
	}

	/**
	 *
	 *
	 * @param f
	 * @return
	 */
	public HE_Halfedge getHalfedge(final HE_Face f) {
		HE_Halfedge he = _halfedge;
		if (he == null) {
			return null;
		}
		if (f == null) {
			do {
				if (he.getFace() == null) {
					return he;
				}
				he = he.getNextInVertex();
			} while (he != _halfedge);
		} else {
			do {
				if (he.getFace() == f) {
					return he;
				}
				he = he.getNextInVertex();
			} while (he != _halfedge);
		}
		return null;
	}

	// TEXTURE COORDINATES

	/**
	 * Clear vertex UVW.
	 */

	public void clearUVW() {
		uvw = null;
	}

	/**
	 * Set vertex UVW.
	 *
	 * @param u
	 * @param v
	 * @param w
	 */
	public void setUVW(final double u, final double v, final double w) {
		uvw = new HE_TextureCoordinate(u, v, w);
	}

	/**
	 * Set vertex UVW.
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
	 * Set vertex UVW.
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
	 * Set UVW in halfedge in this vertex, belonging to face. If no such
	 * halfedge exists, nothing happens.
	 *
	 * @param u
	 * @param v
	 * @param w
	 * @param face
	 */
	public void setUVW(final double u, final double v, final double w, final HE_Face face) {
		HE_Halfedge he = getHalfedge(face);
		if (he != null) {
			he.setUVW(u, v, w);
		}

	}

	/**
	 * Set UVW in halfedge in this vertex, belonging to face. If no such
	 * halfedge exists, nothing happens.
	 *
	 * @param uvw
	 *            WB_Coord
	 * @param face
	 */
	public void setUVW(final WB_Coord uvw, final HE_Face face) {
		HE_Halfedge he = getHalfedge(face);
		if (he != null) {
			he.setUVW(uvw);
		}

	}

	/**
	 * Set UVW in halfedge in this vertex, belonging to face. If no such
	 * halfedge exists, nothing happens.
	 *
	 * @param uvw
	 *            HE_TextureCoordinate
	 * @param face
	 */
	public void setUVW(final HE_TextureCoordinate uvw, final HE_Face face) {
		HE_Halfedge he = getHalfedge(face);
		if (he != null) {
			he.setUVW(uvw);
		}

	}

	/**
	 * Clear UVW in halfedge in this vertex, belonging to face. If no such
	 * halfedge exists, nothing happens.
	 *
	 * @param face
	 */

	public void clearUVW(final HE_Face face) {
		HE_Halfedge he = getHalfedge(face);
		if (he != null) {
			he.clearUVW();
		}

	}

	/**
	 * Check if this vertex has a UVW for this face, either a halfedge UVW or a
	 * vertex UVW.
	 *
	 * @param f
	 * @return
	 */
	public boolean hasUVW(final HE_Face f) {
		final HE_Halfedge he = getHalfedge(f);
		if (he != null && he.hasHalfedgeUVW()) {
			return true;
		} else {
			return uvw != null;
		}
	}

	/**
	 * Check if this vertex has a vertex UVW.
	 *
	 * @return
	 */
	public boolean hasVertexUVW() {
		return uvw != null;
	}

	/**
	 * Check if this vertex has a halfedge UVW for this face.
	 *
	 * @param f
	 * @return
	 */
	public boolean hasHalfedgeUVW(final HE_Face f) {
		final HE_Halfedge he = getHalfedge(f);
		if (he != null && he.hasHalfedgeUVW()) {
			return true;
		}
		return false;
	}

	/**
	 * Get the vertex UVW. If none exists, return zero coordinates.
	 *
	 * @return
	 */
	public HE_TextureCoordinate getVertexUVW() {
		if (uvw == null) {
			return HE_TextureCoordinate.ZERO;
		}
		return uvw;
	}

	/**
	 * Get the halfedge UVW belonging to a face. If none exists, return zero
	 * coordinates.
	 *
	 * @param f
	 * @return
	 */
	public HE_TextureCoordinate getHalfedgeUVW(final HE_Face f) {
		final HE_Halfedge he = getHalfedge(f);
		if (he != null && he.hasHalfedgeUVW()) {
			return he.getUVW();
		} else {
			return HE_TextureCoordinate.ZERO;
		}
	}

	/**
	 * Get the UVW belonging to a face. If approprate halfedge UVW exists, the
	 * vertex UVW is retrieved.* If neither exist, zero coordinates are
	 * returned.
	 *
	 * @param f
	 * @return
	 */

	public HE_TextureCoordinate getUVW(final HE_Face f) {
		final HE_Halfedge he = getHalfedge(f);
		if (he != null) {
			return he.getUVW();
		}
		return uvw == null ? HE_TextureCoordinate.ZERO : uvw;
	}

	/**
	 *
	 */
	public void cleanUVW() {
		if (_halfedge == null) {
			return;
		}
		List<HE_Halfedge> halfedges = getHalfedgeStar();

		if (halfedges.size() == 0) {
			return;
		}
		int i = 0;
		while (!hasVertexUVW() && i < halfedges.size()) {
			if (halfedges.get(i).hasHalfedgeUVW()) {
				setUVW(halfedges.get(i).getHalfedgeUVW());
			}
			i++;
		}
		if (hasVertexUVW()) {
			for (HE_Halfedge he : halfedges) {
				if (he.hasHalfedgeUVW()) {
					if (he.getHalfedgeUVW().equals(getVertexUVW())) {
						he.clearUVW();
					}

				}
			}

		}

	}

	public boolean isNeighbor(final HE_Vertex v) {
		if (getHalfedge() == null) {
			return false;
		}
		HE_Halfedge he = getHalfedge();
		do {
			if (he.getEndVertex() == v) {
				return true;
			}
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_CoordinateMetric#getDistance3D(wblut.geom.WB_Coordinate)
	 */

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_CoordinateMetric#getSqDistance3D(wblut.geom.WB_Coordinate)
	 */

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMetric#getLength3D()
	 */

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMetric#getSqLength3D()
	 */

	/**
	 *
	 *
	 * @return
	 */
	public double[] coords() {
		return new double[] { xd(), yd(), zd() };
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final WB_Coord p) {
		int cmp = Double.compare(xd(), p.xd());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Double.compare(yd(), p.yd());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Double.compare(zd(), p.zd());
		if (cmp != 0) {
			return cmp;
		}
		return Double.compare(wd(), p.wd());
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */

	public int compareToY1st(final WB_Coord p) {
		int cmp = Double.compare(yd(), p.yd());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Double.compare(xd(), p.xd());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Double.compare(zd(), p.zd());
		if (cmp != 0) {
			return cmp;
		}
		return Double.compare(wd(), p.wd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (!(o instanceof HE_Vertex)) {
			return false;
		}
		final HE_Vertex p = (HE_Vertex) o;
		if (!WB_Epsilon.isEqualAbs(xd(), p.xd())) {
			return false;
		}
		if (!WB_Epsilon.isEqualAbs(yd(), p.yd())) {
			return false;
		}
		if (!WB_Epsilon.isEqualAbs(zd(), p.zd())) {
			return false;
		}
		if (!WB_Epsilon.isEqualAbs(wd(), p.wd())) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return WB_HashCode.calculateHashCode(xd(), yd(), zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.Point3D#toString()
	 */
	@Override
	public String toString() {
		return "HE_Vertex key: " + key() + " [x=" + xd() + ", y=" + yd() + ", z=" + zd() + "]" + " (" + getUserLabel()
				+ "," + getInternalLabel() + ")";
	}

	@Override
	public void clearPrecomputed() {

	}

}
