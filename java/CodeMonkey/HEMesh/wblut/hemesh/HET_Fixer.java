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
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;

import wblut.core.WB_ProgressReporter.WB_ProgressCounter;
import wblut.core.WB_ProgressReporter.WB_ProgressTracker;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_AABBTree.WB_AABBNode;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_GeometryOp3D;
import wblut.geom.WB_IntersectionResult;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Triangle;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;

/**
 * @author FVH
 *
 */
public class HET_Fixer {
	public static final WB_ProgressTracker tracker = WB_ProgressTracker.instance();

	/**
	 * Iterate through all halfedges and reset the halfedge link to its face to
	 * itself. f=he.getFace() f.setHalfedge(he)
	 */
	public void fixHalfedgeFaceAssignment(final HE_Mesh mesh) {
		final Iterator<HE_Halfedge> heItr = mesh.heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() != null) {
				mesh.setHalfedge(he.getFace(), he);
			}
		}
	}

	/**
	 * Iterate through all halfedges and reset the halfedge link to its vertex
	 * to itself. v=he.getVertex() v.setHalfedge(he)
	 */
	public void fixHalfedgeVertexAssignment(final HE_Mesh mesh) {
		final Iterator<HE_Halfedge> heItr = mesh.heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he = heItr.next();
			mesh.setHalfedge(he.getVertex(), he);
		}
	}

	/**
	 *
	 * @param mesh
	 * @param f
	 */
	public static void deleteTwoEdgeFace(final HE_Mesh mesh, final HE_Face f) {
		if (mesh.contains(f)) {
			final HE_Halfedge he = f.getHalfedge();
			final HE_Halfedge hen = he.getNextInFace();
			if (he == he.getNextInFace(2)) {
				final HE_Halfedge hePair = he.getPair();
				final HE_Halfedge henPair = hen.getPair();
				mesh.remove(f);
				mesh.remove(he);
				mesh.setHalfedge(he.getVertex(), he.getNextInVertex());
				mesh.remove(hen);
				mesh.setHalfedge(hen.getVertex(), hen.getNextInVertex());
				mesh.setPair(hePair, henPair);

			}
		}
	}

	/**
	 *
	 */
	public static void deleteTwoEdgeFaces(final HE_Mesh mesh) {
		HE_FaceIterator fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			final HE_Halfedge he = f.getHalfedge();
			final HE_Halfedge hen = he.getNextInFace();
			if (he == hen.getNextInFace()) {
				final HE_Halfedge hePair = he.getPair();
				final HE_Halfedge henPair = hen.getPair();
				mesh.remove(f);
				mesh.remove(he);
				mesh.setHalfedge(he.getVertex(), he.getNextInVertex());
				mesh.remove(hen);
				mesh.setHalfedge(hen.getVertex(), hen.getNextInVertex());
				mesh.setPair(hePair, henPair);

			}
		}
	}

	/**
	 *
	 * @param mesh
	 * @param v
	 */
	public static void deleteTwoEdgeVertex(final HE_Mesh mesh, final HE_Vertex v) {
		if (mesh.contains(v) && v.getVertexDegree() == 2) {
			final HE_Halfedge he0 = v.getHalfedge();
			final HE_Halfedge he1 = he0.getNextInVertex();
			final HE_Halfedge he0n = he0.getNextInFace();
			final HE_Halfedge he1n = he1.getNextInFace();
			final HE_Halfedge he0p = he0.getPair();
			final HE_Halfedge he1p = he1.getPair();
			mesh.setNext(he0p, he1n);
			mesh.setNext(he1p, he0n);
			if (he0.getFace() != null) {
				mesh.setHalfedge(he0.getFace(), he1p);
			}
			if (he1.getFace() != null) {
				mesh.setHalfedge(he1.getFace(), he0p);
			}
			mesh.setHalfedge(he0n.getVertex(), he0n);
			mesh.setHalfedge(he1n.getVertex(), he1n);
			mesh.setPair(he0p, he1p);
			mesh.remove(he0);
			mesh.remove(he1);
			mesh.remove(v);
		}
	}

	/**
	 *
	 */
	public static void deleteTwoEdgeVertices(final HE_Mesh mesh) {
		final HE_VertexIterator vitr = mesh.vItr();
		HE_Vertex v;
		final List<HE_Vertex> toremove = new FastList<HE_Vertex>();
		while (vitr.hasNext()) {
			v = vitr.next();
			if (v.getVertexDegree() == 2) {
				toremove.add(v);
			}
		}
		for (final HE_Vertex vtr : toremove) {
			deleteTwoEdgeVertex(mesh, vtr);
		}
	}

	/**
	 * Collapse all zero-length edges.
	 *
	 */
	public static void collapseDegenerateEdges(final HE_Mesh mesh) {
		final FastList<HE_Halfedge> edgesToRemove = new FastList<HE_Halfedge>();
		final Iterator<HE_Halfedge> eItr = mesh.eItr();
		HE_Halfedge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (WB_Epsilon.isZeroSq(WB_GeometryOp3D.getSqDistance3D(e.getVertex(), e.getEndVertex()))) {
				edgesToRemove.add(e);
			}
		}
		for (int i = 0; i < edgesToRemove.size(); i++) {
			HET_MeshOp.collapseEdge(mesh, edgesToRemove.get(i));
		}
	}

	/**
	 *
	 * @param mesh
	 * @param d
	 */
	public static void collapseDegenerateEdges(final HE_Mesh mesh, final double d) {
		final FastList<HE_Halfedge> edgesToRemove = new FastList<HE_Halfedge>();
		final Iterator<HE_Halfedge> eItr = mesh.eItr();
		HE_Halfedge e;
		final double d2 = d * d;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (WB_GeometryOp3D.getSqDistance3D(e.getVertex(), e.getEndVertex()) < d2) {
				edgesToRemove.add(e);
			}
		}
		for (int i = 0; i < edgesToRemove.size(); i++) {
			HET_MeshOp.collapseEdge(mesh, edgesToRemove.get(i));
		}
	}

	/**
	 *
	 *
	 * @return
	 */
	public static boolean fixNonManifoldVerticesOnePass(final HE_Mesh mesh) {
		class VertexInfo {
			FastList<HE_Halfedge> out;

			VertexInfo() {
				out = new FastList<HE_Halfedge>();
			}
		}
		final LongObjectHashMap<VertexInfo> vertexLists = new LongObjectHashMap<VertexInfo>();
		HE_Vertex v;
		VertexInfo vi;
		WB_ProgressCounter counter = new WB_ProgressCounter(mesh.getNumberOfHalfedges(), 10);
		tracker.setCounterStatus("HET_Fixer", "Classifying halfedges per vertex.", counter);
		HE_HalfedgeIterator heItr = mesh.heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he = heItr.next();
			v = he.getVertex();
			vi = vertexLists.get(v.key());
			if (vi == null) {
				vi = new VertexInfo();
				vertexLists.put(v.key(), vi);
			}
			vi.out.add(he);
			counter.increment();
		}
		final List<HE_Vertex> toUnweld = new FastList<HE_Vertex>();
		counter = new WB_ProgressCounter(mesh.getNumberOfVertices(), 10);
		tracker.setCounterStatus("HET_Fixer", "Checking vertex umbrellas.", counter);
		Iterator<HE_Vertex> vItr = mesh.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			final List<HE_Halfedge> outgoing = vertexLists.get(v.key()).out;
			final List<HE_Halfedge> vStar = v.getHalfedgeStar();
			if (outgoing.size() != vStar.size()) {
				toUnweld.add(v);
			}
		}
		vItr = toUnweld.iterator();
		counter = new WB_ProgressCounter(toUnweld.size(), 10);
		tracker.setCounterStatus("HET_Fixer", "Splitting vertex umbrellas. ", counter);

		while (vItr.hasNext()) {
			v = vItr.next();
			final List<HE_Halfedge> vHalfedges = vertexLists.get(v.key()).out;
			final List<HE_Halfedge> vStar = v.getHalfedgeStar();
			final HE_Vertex vc = new HE_Vertex(v);
			mesh.add(vc);
			for (int i = 0; i < vStar.size(); i++) {
				mesh.setVertex(vStar.get(i), vc);
			}
			mesh.setHalfedge(vc, vStar.get(0));
			for (int i = 0; i < vHalfedges.size(); i++) {
				he = vHalfedges.get(i);
				if (he.getVertex() == v) {
					mesh.setHalfedge(v, he);
					break;
				}
			}
			counter.increment();
		}
		return toUnweld.size() > 0;
	}

	public static void fixDegenerateTriangles(final HE_Mesh mesh) {
		HE_FaceIterator fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.isDegenerate() && f.getFaceDegree() == 3 && mesh.contains(f)) {
				double d = f.getHalfedge().getLength();
				double dmax = d;
				HE_Halfedge he = f.getHalfedge();
				HE_Halfedge longesthe = he;
				if (d > WB_Epsilon.EPSILON) {
					do {
						he = he.getNextInFace();
						d = he.getLength();
						if (WB_Epsilon.isZero(d)) {
							longesthe = he;
							break;
						}

						if (d > dmax) {
							longesthe = he;
							dmax = d;
						}
					} while (he != f.getHalfedge());
				}
				mesh.deleteEdge(longesthe);
			}

		}
	}

	/**
	 *
	 */
	public static void fixNonManifoldVertices(final HE_Mesh mesh) {
		int counter = 0;
		do {
			counter++;
		} while (fixNonManifoldVerticesOnePass(mesh) || counter < 10);// Normally
		// this should
		// run at most
		// 3 or 4
		// times
	}

	/**
	 * Remove all redundant vertices in straight edges.
	 *
	 */
	public static void deleteCollinearVertices(final HE_Mesh mesh) {
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		HE_Vertex v;
		HE_Halfedge he;
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getVertexDegree() == 2) {
				he = v.getHalfedge();
				if (WB_Vector.isParallel(he.getHalfedgeTangent(), he.getNextInVertex().getHalfedgeTangent())) {
					mesh.setNext(he.getPrevInFace(), he.getNextInFace());
					mesh.setNext(he.getPair().getPrevInFace(), he.getPair().getNextInFace());
					mesh.setVertex(he.getPair().getNextInFace(), he.getNextInFace().getVertex());
					if (he.getFace() != null) {
						if (he.getFace().getHalfedge() == he) {
							mesh.setHalfedge(he.getFace(), he.getNextInFace());
						}
					}
					if (he.getPair().getFace() != null) {
						if (he.getPair().getFace().getHalfedge() == he.getPair()) {
							mesh.setHalfedge(he.getPair().getFace(), he.getPair().getNextInFace());
						}
					}
					vItr.remove();
					mesh.remove(he);
					mesh.remove(he.getPair());
				}
			}
		}
	}

	/**
	 *
	 */
	public static void deleteDegenerateTriangles(final HE_Mesh mesh) {
		final List<HE_Face> faces = mesh.getFaces();
		HE_Halfedge he;
		for (final HE_Face face : faces) {
			if (!mesh.contains(face)) {
				continue; // face already removed by a previous change
			}
			if (face.isDegenerate()) {
				final int fo = face.getFaceDegree();
				if (fo == 3) {
					HE_Halfedge degeneratehe = null;
					he = face.getHalfedge();
					do {
						if (WB_Epsilon.isZero(he.getLength())) {
							degeneratehe = he;
							break;
						}
						he = he.getNextInFace();
					} while (he != face.getHalfedge());
					if (degeneratehe != null) {
						// System.out.println("Zero length change!");
						HET_MeshOp.collapseHalfedge(mesh, he);
						continue;
					}
					he = face.getHalfedge();
					double d;
					double dmax = 0;
					do {
						d = he.getLength();
						if (d > dmax) {
							degeneratehe = he;
							dmax = d;
						}
						he = he.getNextInFace();
					} while (he != face.getHalfedge());
					// System.out.println("Deleting longest edge: " + he);
					mesh.deleteEdge(degeneratehe);
				}
			}
		}
	}

	public static void clean(final HE_Mesh mesh) {
		mesh.modify(new HEM_Clean());
	}

	/**
	 * Fix loops.
	 */
	public static void fixLoops(final HE_Mesh mesh) {
		for (final HE_Halfedge he : mesh.getHalfedges()) {
			if (he.getPrevInFace() == null) {
				HE_Halfedge hen = he.getNextInFace();
				while (hen.getNextInFace() != he) {
					hen = hen.getNextInFace();
				}
				mesh.setNext(hen, he);
			}
		}
	}

	public static List<HET_SelfIntersectionResult> getSelfIntersection(final HE_Mesh mesh) {
		final List<HET_SelfIntersectionResult> selfints = new FastList<HET_SelfIntersectionResult>();
		mesh.triangulate();
		mesh.resetFaceInternalLabels();

		HE_Selection sifs = HE_Selection.getSelection(mesh);
		WB_AABBTree tree = new WB_AABBTree(mesh, 1);
		List<WB_AABBNode[]> atat = WB_GeometryOp.getIntersection3D(tree, tree);
		WB_Triangle T0, T1;
		List<HE_Face> neighbors;
		for (WB_AABBNode[] node : atat) {
			for (HE_Face f0 : node[0].getFaces()) {
				T0 = f0.toTriangle();
				neighbors = f0.getNeighborFaces();
				for (HE_Face f1 : node[1].getFaces()) {
					if (!neighbors.contains(f1) && f1.getKey() > f0.getKey()) {// Check
																				// each
																				// face
																				// pair
																				// only
						T1 = f1.toTriangle();
						final WB_IntersectionResult ir = WB_GeometryOp3D.getIntersection3D(T0, T1);
						if (ir.intersection && ir.object != null
								&& !WB_Epsilon.isZero(((WB_Segment) ir.object).getLength())) {
							f0.setInternalLabel(1);
							f1.setInternalLabel(1);
							sifs.add(f0);
							sifs.add(f1);
							selfints.add(new HET_SelfIntersectionResult(f0, f1, (WB_Segment) ir.object));
						}
					}
				}
			}

		}

		mesh.addSelection("self", sifs);

		return selfints;
	}

	/**
	 *
	 */
	public static class HET_SelfIntersectionResult {
		/**
		 *
		 */
		HE_Face f1;
		/**
		 *
		 */
		HE_Face f2;
		/**
		 *
		 */
		WB_Segment segment;

		/**
		 *
		 *
		 * @param f1
		 * @param f2
		 * @param seg
		 */
		public HET_SelfIntersectionResult(final HE_Face f1, final HE_Face f2, final WB_Segment seg) {
			this.f1 = f1;
			this.f2 = f2;
			segment = seg;
		}

		/**
		 *
		 *
		 * @return
		 */
		public HE_Face getFace1() {
			return f1;
		}

		/**
		 *
		 *
		 * @return
		 */
		public HE_Face getFace2() {
			return f2;
		}

		/**
		 *
		 *
		 * @return
		 */
		public WB_Segment getSegment() {
			return segment;
		}
	}

}