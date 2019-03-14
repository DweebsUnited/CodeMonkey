/**
 *
 */
package wblut.hemesh;

import java.util.List;

import org.eclipse.collections.impl.list.mutable.FastList;

import wblut.geom.WB_CoordCollection;

/**
 * @author FVH
 *
 */
public class HE_Boundary extends HE_MeshElement implements Comparable<HE_Boundary> {
	/** Halfedge associated with this boundary. */
	private HE_Halfedge _halfedge;

	/**
	 * Instantiates a new HE_Boundary.
	 */
	public HE_Boundary() {
		super();

	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_BoundaryEdgeCirculator beCrc() {
		return new HE_BoundaryEdgeCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_BoundaryFaceCirculator bfCrc() {
		return new HE_BoundaryFaceCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_BoundaryVertexCirculator bvCrc() {
		return new HE_BoundaryVertexCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_BoundaryHalfedgeInnerCirculator bheiCrc() {
		return new HE_BoundaryHalfedgeInnerCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_BoundaryHalfedgeOuterCirculator bheoCrc() {
		return new HE_BoundaryHalfedgeOuterCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_BoundaryEdgeRevCirculator beRevCrc() {
		return new HE_BoundaryEdgeRevCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_BoundaryFaceRevCirculator bfRevCrc() {
		return new HE_BoundaryFaceRevCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_BoundaryHalfedgeInnerRevCirculator bheiRevCrc() {
		return new HE_BoundaryHalfedgeInnerRevCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_BoundaryHalfedgeOuterRevCirculator bheoRevCrc() {
		return new HE_BoundaryHalfedgeOuterRevCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_BoundaryVertexRevCirculator bvRevCrc() {
		return new HE_BoundaryVertexRevCirculator(this);
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
	public List<HE_Vertex> getUniqueBoundaryVertices() {
		final FastList<HE_Vertex> bv = new FastList<HE_Vertex>();
		if (_halfedge == null) {
			return bv;
		}
		HE_Halfedge he = _halfedge;
		do {
			if (!bv.contains(he.getVertex())) {
				bv.add(he.getVertex());
			}
			he = he.getNextInFace();
		} while (he != _halfedge);
		return bv.asUnmodifiable();
	}

	public List<HE_Vertex> getBoundaryVertices() {
		final FastList<HE_Vertex> bv = new FastList<HE_Vertex>();
		if (_halfedge == null) {
			return bv;
		}
		HE_Halfedge he = _halfedge;
		do {

			bv.add(he.getVertex());

			he = he.getNextInFace();
		} while (he != _halfedge);
		return bv.asUnmodifiable();
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<HE_Halfedge> getBoundaryHalfedges() {
		final FastList<HE_Halfedge> bhe = new FastList<HE_Halfedge>();
		if (_halfedge == null) {
			return bhe;
		}
		HE_Halfedge he = _halfedge;
		do {
			if (!bhe.contains(he)) {
				bhe.add(he);
			}
			he = he.getNextInFace();
		} while (he != _halfedge);
		return bhe.asUnmodifiable();
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<HE_Halfedge> getBoundaryHalfedgesTwoSided() {
		final FastList<HE_Halfedge> bhe = new FastList<HE_Halfedge>();
		if (_halfedge == null) {
			return bhe;
		}
		HE_Halfedge he = _halfedge;
		do {
			if (!bhe.contains(he)) {
				bhe.add(he);
				if (he.getPair() != null) {
					if (!bhe.contains(he.getPair())) {
						bhe.add(he.getPair());
					}
				}
			}
			he = he.getNextInFace();
		} while (he != _halfedge);
		return bhe.asUnmodifiable();
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<HE_Halfedge> getBoundaryEdges() {
		final FastList<HE_Halfedge> be = new FastList<HE_Halfedge>();
		if (_halfedge == null) {
			return be;
		}
		HE_Halfedge he = _halfedge;
		do {
			if (he.isEdge()) {
				if (!be.contains(he)) {
					be.add(he);
				}
			} else {
				if (!be.contains(he.getPair())) {
					be.add(he.getPair());
				}
			}
			he = he.getNextInFace();
		} while (he != _halfedge);
		return be.asUnmodifiable();
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
	 */
	protected void _clearHalfedge() {
		_halfedge = null;
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
	public int compareTo(final HE_Boundary b) {

		if (b.getHalfedge() == null) {
			if (getHalfedge() == null) {

				return 0;
			} else {
				return 1;
			}
		} else if (getHalfedge() == null) {
			return -1;
		}

		return getHalfedge().compareTo(b.getHalfedge());
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

				if (!ff.contains(hep.getFace())) {
					ff.add(hep.getFace());
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
		String s = "HE_Boundary key: " + key() + ".";
		return s;
	}

	/**
	 *
	 *
	 * @param el
	 */
	public void copyProperties(final HE_Face el) {
		super.copyProperties(el);
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

	public WB_CoordCollection getPoints() {

		return WB_CoordCollection.getCollection(getBoundaryVertices());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.hemesh.HE_Element#clearPrecomputed()
	 */
	@Override
	protected void clearPrecomputed() {
		// TODO Auto-generated method stub

	}

}
