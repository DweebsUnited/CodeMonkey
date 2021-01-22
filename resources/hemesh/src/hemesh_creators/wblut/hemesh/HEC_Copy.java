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
import java.util.Set;

import org.eclipse.collections.impl.map.mutable.primitive.LongLongHashMap;

import wblut.core.WB_ProgressReporter.WB_ProgressCounter;

/**
 * Axis Aligned Box.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEC_Copy extends HEC_Creator {
	/**
	 *
	 */
	HE_HalfedgeStructure source;
	public LongLongHashMap vertexCorrelation;
	public LongLongHashMap faceCorrelation;
	public LongLongHashMap halfedgeCorrelation;

	/**
	 *
	 */
	public HEC_Copy() {
		super();
		override = true;
	}

	/**
	 *
	 *
	 * @param source
	 */
	public HEC_Copy(final HE_HalfedgeStructure source) {
		super();
		setMesh(source);
		override = true;
	}

	/**
	 *
	 *
	 * @param source
	 * @return
	 */
	public HEC_Copy setMesh(final HE_HalfedgeStructure source) {
		this.source = source;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		tracker.setStartStatus(this, "Starting HEC_Copy.");
		final HE_Mesh result = new HE_Mesh();
		if (source == null) {
			tracker.setStopStatus(this, "No source mesh. Exiting HEC_Copy.");
			return result;
		}

		if (source instanceof HE_Mesh) {
			final HE_Mesh mesh = (HE_Mesh) source;
			result.copyProperties(mesh);
			vertexCorrelation = new LongLongHashMap();
			faceCorrelation = new LongLongHashMap();
			halfedgeCorrelation = new LongLongHashMap();
			HE_Vertex rv;
			HE_Vertex v;
			WB_ProgressCounter counter = new WB_ProgressCounter(mesh.getNumberOfVertices(), 10);
			tracker.setCounterStatus(this, "Creating vertices.", counter);
			final Iterator<HE_Vertex> vItr = mesh.vItr();
			while (vItr.hasNext()) {
				v = vItr.next();
				rv = new HE_Vertex(v);
				result.add(rv);
				rv.copyProperties(v);
				vertexCorrelation.put(v.key(), rv.key());
				counter.increment();
			}

			HE_Face rf;
			HE_Face f;
			counter = new WB_ProgressCounter(mesh.getNumberOfFaces(), 10);
			tracker.setCounterStatus(this, "Creating faces.", counter);
			final Iterator<HE_Face> fItr = mesh.fItr();
			while (fItr.hasNext()) {
				f = fItr.next();
				rf = new HE_Face();
				result.add(rf);
				rf.copyProperties(f);
				faceCorrelation.put(f.key(), rf.key());
				counter.increment();
			}

			HE_Halfedge rhe;
			HE_Halfedge he;
			counter = new WB_ProgressCounter(mesh.getNumberOfHalfedges(), 10);
			tracker.setCounterStatus(this, "Creating halfedges.", counter);
			HE_RAS<HE_Halfedge> copyHalfedges = new HE_RAS<HE_Halfedge>();
			final Iterator<HE_Halfedge> heItr = mesh.getHalfedges().iterator();
			while (heItr.hasNext()) {
				he = heItr.next();
				rhe = new HE_Halfedge();
				copyHalfedges.add(rhe);
				rhe.copyProperties(he);
				halfedgeCorrelation.put(he.key(), rhe.key());
				counter.increment();
			}

			counter = new WB_ProgressCounter(mesh.getNumberOfVertices(), 10);
			tracker.setCounterStatus(this, "Setting vertex properties.", counter);
			HE_Vertex sv;
			HE_Vertex tv;
			Iterator<HE_Vertex> svItr = mesh.vItr();
			final Iterator<HE_Vertex> tvItr = result.vItr();
			Long key;
			while (svItr.hasNext()) {
				sv = svItr.next();
				tv = tvItr.next();
				tv.set(sv);
				if (sv.getHalfedge() != null) {
					key = halfedgeCorrelation.get(sv.getHalfedge().key());
					if (key >= 0) {
						result.setHalfedge(tv, copyHalfedges.getWithKey(key));
					}
				}
				counter.increment();
			}

			counter = new WB_ProgressCounter(mesh.getNumberOfFaces(), 10);
			tracker.setCounterStatus(this, "Setting face properties.", counter);
			HE_Face sf;
			HE_Face tf;
			Iterator<HE_Face> sfItr = mesh.fItr();
			final Iterator<HE_Face> tfItr = result.fItr();
			while (sfItr.hasNext()) {
				sf = sfItr.next();
				tf = tfItr.next();
				if (sf.getHalfedge() != null) {
					key = halfedgeCorrelation.get(sf.getHalfedge().key());
					if (key >= 0) {
						result.setHalfedge(tf, copyHalfedges.getWithKey(key));
					}
				}
				counter.increment();
			}

			counter = new WB_ProgressCounter(mesh.getNumberOfHalfedges(), 10);
			tracker.setCounterStatus(this, "Setting halfedge properties.", counter);
			HE_Halfedge she;
			HE_Halfedge the;
			Iterator<HE_Halfedge> sheItr = mesh.getHalfedges().iterator();
			final Iterator<HE_Halfedge> theItr = copyHalfedges.iterator();
			while (sheItr.hasNext()) {
				she = sheItr.next();
				the = theItr.next();
				if (she.getPair() != null) {
					key = halfedgeCorrelation.get(she.getPair().key());
					if (key >= 0) {
						the._setPair(copyHalfedges.getWithKey(key));
						the.getPair()._setPair(the);
					}
				}
				if (she.getNextInFace() != null) {
					key = halfedgeCorrelation.get(she.getNextInFace().key());
					if (key >= 0) {
						the._setNext(copyHalfedges.getWithKey(key));
						the.getNextInFace()._setPrev(the);
					}
				}
				if (she.getVertex() != null) {
					key = vertexCorrelation.get(she.getVertex().key());
					if (key >= 0) {
						result.setVertex(the, result.getVertexWithKey(key));
					}
				}
				if (she.getFace() != null) {
					key = faceCorrelation.get(she.getFace().key());
					if (key >= 0) {
						result.setFace(the, result.getFaceWithKey(key));
					}
				}
				result.add(the);
				counter.increment();
			}

			Set<String> names = mesh.getSelectionNames();

			for (String name : names) {

				HE_Selection source = mesh.getSelection(name);
				HE_Selection target = HE_Selection.getSelection(result);
				svItr = source.vItr();
				while (svItr.hasNext()) {
					sv = svItr.next();
					key = vertexCorrelation.get(sv.key());
					target.add(result.getVertexWithKey(key));
				}
				sheItr = source.heItr();
				while (sheItr.hasNext()) {
					she = sheItr.next();
					key = halfedgeCorrelation.get(she.key());
					target.add(result.getHalfedgeWithKey(key));
				}
				sfItr = source.fItr();
				while (sfItr.hasNext()) {
					sf = sfItr.next();
					key = faceCorrelation.get(sf.key());
					target.add(result.getFaceWithKey(key));
				}
				result.addSelection(name, target);

			}

			tracker.setStopStatus(this, "Exiting HEC_Copy.");
		} else if (source instanceof HE_Selection) {
			final HE_Selection sel = ((HE_Selection) source).get();
			result.copyProperties(sel);
			sel.completeFromFaces();

			vertexCorrelation = new LongLongHashMap();
			faceCorrelation = new LongLongHashMap();
			halfedgeCorrelation = new LongLongHashMap();
			HE_Vertex rv;
			HE_Vertex v;
			WB_ProgressCounter counter = new WB_ProgressCounter(sel.getNumberOfVertices(), 10);
			tracker.setCounterStatus(this, "Creating vertices.", counter);
			final Iterator<HE_Vertex> vItr = sel.vItr();
			while (vItr.hasNext()) {
				v = vItr.next();
				rv = new HE_Vertex(v);
				result.add(rv);
				rv.copyProperties(v);
				vertexCorrelation.put(v.key(), rv.key());
				counter.increment();
			}
			HE_Face rf;
			HE_Face f;
			counter = new WB_ProgressCounter(sel.getNumberOfFaces(), 10);
			tracker.setCounterStatus(this, "Creating faces.", counter);
			final Iterator<HE_Face> fItr = sel.fItr();
			while (fItr.hasNext()) {
				f = fItr.next();
				rf = new HE_Face();
				result.add(rf);
				rf.copyProperties(f);
				faceCorrelation.put(f.key(), rf.key());
				counter.increment();
			}
			HE_Halfedge rhe;
			HE_Halfedge he;
			counter = new WB_ProgressCounter(sel.getNumberOfHalfedges(), 10);
			HE_RAS<HE_Halfedge> copyHalfedges = new HE_RAS<HE_Halfedge>();
			tracker.setCounterStatus(this, "Creating halfedges.", counter);
			final Iterator<HE_Halfedge> heItr = sel.heItr();
			while (heItr.hasNext()) {
				he = heItr.next();
				rhe = new HE_Halfedge();
				copyHalfedges.add(rhe);
				rhe.copyProperties(he);
				halfedgeCorrelation.put(he.key(), rhe.key());
				counter.increment();
			}
			counter = new WB_ProgressCounter(sel.getNumberOfVertices(), 10);
			tracker.setCounterStatus(this, "Setting vertex properties.", counter);
			HE_Vertex sv;
			HE_Vertex tv;
			final Iterator<HE_Vertex> svItr = sel.vItr();
			final Iterator<HE_Vertex> tvItr = result.vItr();
			Long key;
			while (svItr.hasNext()) {
				sv = svItr.next();
				tv = tvItr.next();
				tv.set(sv);
				if (sv.getHalfedge() != null) {
					key = halfedgeCorrelation.get(sv.getHalfedge().key());
					if (key >= 0) {
						result.setHalfedge(tv, copyHalfedges.getWithKey(key));
					}
				}
				counter.increment();
			}
			counter = new WB_ProgressCounter(sel.getNumberOfFaces(), 10);
			tracker.setCounterStatus(this, "Setting face properties.", counter);
			HE_Face sf;
			HE_Face tf;
			final Iterator<HE_Face> sfItr = sel.fItr();
			final Iterator<HE_Face> tfItr = result.fItr();
			while (sfItr.hasNext()) {
				sf = sfItr.next();
				tf = tfItr.next();
				if (sf.getHalfedge() != null) {
					key = halfedgeCorrelation.get(sf.getHalfedge().key());
					if (key >= 0) {
						result.setHalfedge(tf, copyHalfedges.getWithKey(key));
					}
				}
				counter.increment();
			}
			counter = new WB_ProgressCounter(sel.getNumberOfHalfedges(), 10);
			tracker.setCounterStatus(this, "Setting halfedge properties.", counter);
			HE_Halfedge she;
			HE_Halfedge the;
			final Iterator<HE_Halfedge> sheItr = sel.heItr();
			final Iterator<HE_Halfedge> theItr = copyHalfedges.iterator();
			while (sheItr.hasNext()) {
				she = sheItr.next();
				the = theItr.next();
				if (she.getPair() != null) {
					key = halfedgeCorrelation.get(she.getPair().key());
					if (key >= 0) {
						the._setPair(copyHalfedges.getWithKey(key));
						the.getPair()._setPair(the);
					}
				}
				if (she.getNextInFace() != null) {
					key = halfedgeCorrelation.get(she.getNextInFace().key());
					if (key >= 0) {
						the._setNext(copyHalfedges.getWithKey(key));
						the.getNextInFace()._setPrev(the);
					}
				}
				if (she.getVertex() != null) {
					key = vertexCorrelation.get(she.getVertex().key());
					if (key >= 0) {
						result.setVertex(the, result.getVertexWithKey(key));
					}
				}
				if (she.getFace() != null) {
					key = faceCorrelation.get(she.getFace().key());
					if (key >= 0) {
						result.setFace(the, result.getFaceWithKey(key));
					}
				}
				result.add(the);
				counter.increment();
			}
			result.capHalfedges();
			tracker.setStopStatus(this, "Exiting HEC_Copy.");
		}
		return result;
	}
}
