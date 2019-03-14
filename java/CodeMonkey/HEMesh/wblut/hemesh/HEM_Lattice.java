/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 *
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 *
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.hemesh;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.collections.impl.map.mutable.primitive.LongLongHashMap;

import wblut.core.WB_ProgressReporter.WB_ProgressCounter;
import wblut.geom.WB_Coord;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Vector;
import wblut.math.WB_ConstantScalarParameter;
import wblut.math.WB_FactorScalarParameter;
import wblut.math.WB_ScalarParameter;

/**
 *
 */
public class HEM_Lattice extends HEM_Modifier {

	/**
	 *
	 */
	private static final WB_GeometryFactory gf = new WB_GeometryFactory();
	/**
	 *
	 */
	private WB_ScalarParameter d;
	/**
	 *
	 */
	private WB_ScalarParameter sew;
	/**
	 *
	 */
	private WB_ScalarParameter hew;
	/**
	 *
	 */
	private double thresholdAngle;
	/**
	 *
	 */
	private boolean fuse;
	/**
	 *
	 */
	private double fuseAngle;
	/**
	 *
	 */
	private WB_ScalarParameter ibulge, obulge;

	/**
	 *
	 */
	public HEM_Lattice() {
		super();
		d = null;
		sew = null;
		thresholdAngle = -1;
		fuseAngle = Math.PI / 36;
		fuse = false;
		ibulge = obulge = new WB_ConstantScalarParameter(0);
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public HEM_Lattice setDepth(final double d) {
		this.d = new WB_ConstantScalarParameter(-d);
		return this;
	}

	public HEM_Lattice setDepth(final WB_ScalarParameter d) {
		this.d = new WB_FactorScalarParameter(-1.0, d);
		return this;
	}

	/**
	 *
	 *
	 * @param w
	 * @return
	 */
	public HEM_Lattice setWidth(final double w) {
		sew = new WB_ConstantScalarParameter(0.5 * w);
		hew = new WB_ConstantScalarParameter(w);
		return this;
	}

	public HEM_Lattice setWidth(final WB_ScalarParameter w) {
		sew = new WB_FactorScalarParameter(0.5, w);
		hew = w;
		return this;
	}

	/**
	 *
	 *
	 * @param w
	 * @param hew
	 * @return
	 */
	public HEM_Lattice setWidth(final double w, final double hew) {
		sew = new WB_ConstantScalarParameter(0.5 * w);
		this.hew = new WB_ConstantScalarParameter(hew);
		return this;
	}

	public HEM_Lattice setWidth(final WB_ScalarParameter w, final WB_ScalarParameter hew) {
		sew = new WB_FactorScalarParameter(0.5, w);
		this.hew = hew;
		return this;
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public HEM_Lattice setBulge(final double d) {
		ibulge = new WB_ConstantScalarParameter(d);
		obulge = new WB_ConstantScalarParameter(d);
		return this;
	}

	public HEM_Lattice setBulge(final WB_ScalarParameter d) {
		ibulge = d;
		obulge = d;
		return this;
	}

	/**
	 *
	 *
	 * @param inner
	 * @param outer
	 * @return
	 */
	public HEM_Lattice setBulge(final double inner, final double outer) {
		ibulge = new WB_ConstantScalarParameter(inner);
		obulge = new WB_ConstantScalarParameter(outer);
		return this;
	}

	public HEM_Lattice setBulge(final WB_ScalarParameter inner, final WB_ScalarParameter outer) {
		ibulge = inner;
		obulge = outer;
		return this;
	}

	/**
	 *
	 *
	 * @param b
	 * @return
	 */
	public HEM_Lattice setFuse(final boolean b) {
		fuse = b;
		return this;
	}

	/**
	 *
	 *
	 * @param a
	 * @return
	 */
	public HEM_Lattice setThresholdAngle(final double a) {
		thresholdAngle = a;
		return this;
	}

	/**
	 *
	 *
	 * @param a
	 * @return
	 */
	public HEM_Lattice setFuseAngle(final double a) {
		fuseAngle = a;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	protected HE_Mesh applySelf(final HE_Mesh mesh) {
		tracker.setStartStatus(this, "Starting HEM_Lattice.");
		if (d == null) {
			tracker.setStopStatus(this,
					"Can't create with zero thickness, use HEM_PunchHoles instead. Exiting HEM_Lattice.");
			return mesh;
		}
		if (sew == null) {
			tracker.setStopStatus(this, "Can't create with zero width. Exiting HEM_Lattice.");
			return mesh;
		}
		final HEM_Extrude extm = new HEM_Extrude().setDistance(0).setRelative(false).setChamfer(sew).setFuse(fuse)
				.setHardEdgeChamfer(hew).setFuseAngle(fuseAngle).setThresholdAngle(thresholdAngle);
		mesh.modify(extm);

		tracker.setDuringStatus(this, "Creating inner mesh.");

		HEC_Copy cc = new HEC_Copy().setMesh(mesh);
		final HE_Mesh innerMesh = cc.create();

		LongLongHashMap allheCorrelation = cc.halfedgeCorrelation;

		WB_ProgressCounter counter = new WB_ProgressCounter(mesh.getNumberOfFaces(), 10);
		tracker.setCounterStatus(this, "Creating face correlations.", counter);
		final HashMap<Long, Long> faceCorrelation = new HashMap<Long, Long>();
		final Iterator<HE_Face> fItr1 = mesh.fItr();
		final Iterator<HE_Face> fItr2 = innerMesh.fItr();

		HE_Face f1;
		HE_Face f2;
		while (fItr1.hasNext()) {
			f1 = fItr1.next();
			f2 = fItr2.next();
			faceCorrelation.put(f1.key(), f2.key());
			counter.increment();
		}
		counter = new WB_ProgressCounter(mesh.getNumberOfHalfedges(), 10);
		tracker.setCounterStatus(this, "Creating boundary halfedge correlations.", counter);
		final HashMap<Long, Long> heCorrelation = new HashMap<Long, Long>();
		HE_Halfedge he1;
		HE_Halfedge he2;
		long[] keys = allheCorrelation.keySet().toArray();
		long[] values = allheCorrelation.values().toArray();
		for (int i = 0; i < keys.length; i++) {

			he1 = mesh.getHalfedgeWithKey(keys[i]);

			if (he1.getFace() == null) {
				he2 = innerMesh.getHalfedgeWithKey(values[i]);
				heCorrelation.put(he1.key(), he2.key());
			}
			counter.increment();
		}
		tracker.setDuringStatus(this, "Shrinking inner mesh.");
		final HEM_VertexExpand expm = new HEM_VertexExpand().setDistance(d);
		expm.applySelf(innerMesh);
		HEM_FlipFaces ff = new HEM_FlipFaces();
		ff.applySelf(innerMesh);
		final int nf = mesh.getNumberOfFaces();
		final HE_Face[] origFaces = mesh.getFacesAsArray();
		mesh.selectAllFaces();
		innerMesh.selectAllFaces();

		mesh.add(innerMesh);

		HE_Face fo;
		HE_Face fi;
		List<HE_Halfedge> hei;
		List<HE_Halfedge> heo;
		WB_Point[] viPos;
		WB_Polygon poly;
		HE_Halfedge heoc, heic, heon, hein, heio, heoi;
		HE_Face fNew;
		WB_Coord ni;
		WB_Coord no;
		counter = new WB_ProgressCounter(nf, 10);
		WB_Coord co, ci;
		double ob, ib;

		HE_Selection connect = HE_Selection.getSelection(mesh);
		HE_Selection boundary = HE_Selection.getSelection(mesh);
		tracker.setCounterStatus(this, "Connecting outer and inner faces.", counter);
		for (int i = 0; i < nf; i++) {
			fo = origFaces[i];

			final Long innerKey = faceCorrelation.get(fo.key());
			if (mesh.getSelection("extruded").contains(fo)) {
				fi = mesh.getFaceWithKey(innerKey);

				co = fo.getFaceCenter();
				if ((ob = obulge.evaluate(co.xd(), co.yd(), co.zd())) != 0) {
					no = fo.getFaceNormal();
					fo.push(WB_Vector.mul(no, ob));
				}
				ci = fi.getFaceCenter();
				if ((ib = ibulge.evaluate(ci.xd(), ci.yd(), ci.zd())) != 0) {
					ni = fi.getFaceNormal();
					fi.push(WB_Vector.mul(ni, ib));
				}
				final int nvo = fo.getFaceDegree();
				final int nvi = fi.getFaceDegree();
				hei = fi.getFaceHalfedges();
				viPos = new WB_Point[nvi];
				for (int j = 0; j < nvi; j++) {
					viPos[j] = new WB_Point(hei.get(j).getVertex());
				}
				poly = gf.createSimplePolygon(viPos);
				heo = fo.getFaceHalfedges();
				for (int j = 0; j < nvo; j++) {
					heoc = heo.get(j);
					heon = heo.get((j + 1) % nvo);
					final int cic = poly.closestIndex(heoc.getVertex());
					final int cin = poly.closestIndex(heon.getVertex());
					heic = hei.get(cin);
					hein = hei.get(cic);
					heio = new HE_Halfedge();
					heoi = new HE_Halfedge();
					fNew = new HE_Face();
					connect.add(fNew);
					mesh.setVertex(heoi, heon.getVertex());
					mesh.setVertex(heio, hein.getVertex());
					mesh.setNext(heoc, heoi);

					mesh.setFace(heoc, fNew);
					if (cic == cin) {
						mesh.setNext(heoi, heio);
						mesh.setFace(heoi, fNew);
					} else {
						mesh.setNext(heoi, heic);
						mesh.setFace(heoi, fNew);
						mesh.setNext(heic, heio);
						mesh.setFace(heic, fNew);
					}
					mesh.setNext(heio, heoc);
					mesh.setFace(heio, fNew);
					mesh.setHalfedge(fNew, heoc);
					mesh.add(heio);
					mesh.add(heoi);
					mesh.add(fNew);
					mesh.remove(fo);
					mesh.remove(fi);
				}
			}
			counter.increment();
		}
		counter = new WB_ProgressCounter(heCorrelation.size(), 10);

		tracker.setCounterStatus(this, "Connecting outer and inner boundaries.", counter);
		final Iterator<Map.Entry<Long, Long>> it = heCorrelation.entrySet().iterator();
		while (it.hasNext()) {
			final Map.Entry<Long, Long> pairs = it.next();
			he1 = mesh.getHalfedgeWithKey(pairs.getKey());
			he2 = mesh.getHalfedgeWithKey(pairs.getValue());
			heio = new HE_Halfedge();
			heoi = new HE_Halfedge();
			mesh.add(heio);
			mesh.add(heoi);
			mesh.setVertex(heio, he1.getPair().getVertex());
			mesh.setVertex(heoi, he2.getPair().getVertex());
			mesh.setNext(he1, heio);
			mesh.setNext(heio, he2);
			mesh.setNext(he2, heoi);
			mesh.setNext(heoi, he1);
			fNew = new HE_Face();
			boundary.add(fNew);
			mesh.add(fNew);
			mesh.setHalfedge(fNew, he1);
			mesh.setFace(he1, fNew);
			mesh.setFace(he2, fNew);
			mesh.setFace(heio, fNew);
			mesh.setFace(heoi, fNew);
			counter.increment();
		}
		mesh.pairHalfedges();
		mesh.addSelection("connect", this, connect);
		mesh.addSelection("boundary", this, boundary);
		tracker.setStopStatus(this, "Exiting HEM_Lattice.");
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	protected HE_Mesh applySelf(final HE_Selection selection) {
		tracker.setStartStatus(this, "Starting HEM_Lattice.");
		if (d == null) {
			tracker.setStopStatus(this,
					"Can't create with zero thickness, use HEM_PunchHoles instead. Exiting HEM_Lattice.");
			return selection.parent;
		}
		if (sew == null) {
			tracker.setStopStatus(this, "Can't create with zero width. Exiting HEM_Lattice.");
			return selection.parent;
		}
		final HEM_Extrude extm = new HEM_Extrude().setDistance(0).setRelative(false).setChamfer(sew).setFuse(fuse)
				.setHardEdgeChamfer(hew).setFuseAngle(fuseAngle).setThresholdAngle(thresholdAngle);
		selection.modify(extm);
		tracker.setDuringStatus(this, "Creating inner mesh.");
		HEC_Copy cc = new HEC_Copy().setMesh(selection.parent);
		final HE_Mesh innerMesh = cc.create();
		LongLongHashMap allheCorrelation = cc.halfedgeCorrelation;

		WB_ProgressCounter counter = new WB_ProgressCounter(selection.parent.getNumberOfFaces(), 10);

		tracker.setCounterStatus(this, "Creating face correlations.", counter);
		final HashMap<Long, Long> faceCorrelation = new HashMap<Long, Long>();
		final Iterator<HE_Face> fItr1 = selection.parent.fItr();
		final Iterator<HE_Face> fItr2 = innerMesh.fItr();
		HE_Face f1;
		HE_Face f2;
		while (fItr1.hasNext()) {
			f1 = fItr1.next();
			f2 = fItr2.next();
			faceCorrelation.put(f1.key(), f2.key());
			counter.increment();
		}
		counter = new WB_ProgressCounter(selection.parent.getNumberOfHalfedges(), 10);

		tracker.setCounterStatus(this, "Creating boundary halfedge correlations.", counter);
		final HashMap<Long, Long> heCorrelation = new HashMap<Long, Long>();
		HE_Halfedge he1;
		HE_Halfedge he2;
		long[] keys = allheCorrelation.keySet().toArray();
		long[] values = allheCorrelation.values().toArray();
		for (int i = 0; i < keys.length; i++) {
			he1 = selection.parent.getHalfedgeWithKey(keys[i]);

			if (he1.getFace() == null) {
				he2 = innerMesh.getHalfedgeWithKey(values[i]);
				heCorrelation.put(he1.key(), he2.key());
			}
			counter.increment();
		}
		tracker.setDuringStatus(this, "Shrinking inner mesh.");
		final HEM_VertexExpand expm = new HEM_VertexExpand().setDistance(d);
		expm.applySelf(innerMesh);
		HEM_FlipFaces ff = new HEM_FlipFaces();
		ff.applySelf(innerMesh);
		final int nf = selection.parent.getNumberOfFaces();
		final HE_Face[] origFaces = selection.parent.getFacesAsArray();
		selection.parent.selectAllFaces();
		innerMesh.selectAllFaces();

		selection.parent.add(innerMesh);
		HE_Face fo;
		HE_Face fi;
		List<HE_Halfedge> hei;
		List<HE_Halfedge> heo;
		WB_Point[] viPos;
		WB_Polygon poly;
		HE_Halfedge heoc, heic, heon, hein, heio, heoi;
		HE_Face fNew;
		WB_Coord ni, no;
		counter = new WB_ProgressCounter(nf, 10);
		WB_Coord co, ci;
		double ob, ib;
		HE_Selection connect = HE_Selection.getSelection(selection.parent);
		HE_Selection boundary = HE_Selection.getSelection(selection.parent);
		tracker.setCounterStatus(this, "Connecting outer and inner faces.", counter);
		for (int i = 0; i < nf; i++) {
			fo = origFaces[i];
			final Long innerKey = faceCorrelation.get(fo.key());
			if (selection.parent.getSelection("extruded").contains(fo)) {
				fi = selection.parent.getFaceWithKey(innerKey);
				co = fo.getFaceCenter();
				if ((ob = obulge.evaluate(co.xd(), co.yd(), co.zd())) != 0) {
					no = fo.getFaceNormal();
					fo.push(WB_Vector.mul(no, ob));
				}
				ci = fi.getFaceCenter();
				if ((ib = ibulge.evaluate(ci.xd(), ci.yd(), ci.zd())) != 0) {
					ni = fi.getFaceNormal();
					fi.push(WB_Vector.mul(ni, ib));
				}
				final int nvo = fo.getFaceDegree();
				final int nvi = fi.getFaceDegree();
				hei = fi.getFaceHalfedges();
				viPos = new WB_Point[nvi];
				for (int j = 0; j < nvi; j++) {
					viPos[j] = new WB_Point(hei.get(j).getVertex());
				}
				poly = gf.createSimplePolygon(viPos);
				heo = fo.getFaceHalfedges();
				for (int j = 0; j < nvo; j++) {
					heoc = heo.get(j);
					heon = heo.get((j + 1) % nvo);
					final int cic = poly.closestIndex(heoc.getVertex());
					final int cin = poly.closestIndex(heon.getVertex());
					heic = hei.get(cin);
					hein = hei.get(cic);
					heio = new HE_Halfedge();
					heoi = new HE_Halfedge();
					fNew = new HE_Face();
					selection.parent.setVertex(heoi, heon.getVertex());
					selection.parent.setVertex(heio, hein.getVertex());
					selection.parent.setNext(heoc, heoi);
					selection.parent.setFace(heoc, fNew);
					if (cic == cin) {
						selection.parent.setNext(heoi, heio);
						selection.parent.setFace(heoi, fNew);
					} else {
						selection.parent.setNext(heoi, heic);
						selection.parent.setFace(heoi, fNew);
						selection.parent.setNext(heic, heio);
						selection.parent.setFace(heic, fNew);
					}
					selection.parent.setNext(heio, heoc);
					selection.parent.setFace(heio, fNew);
					selection.parent.setHalfedge(fNew, heoc);
					selection.parent.add(heio);
					selection.parent.add(heoi);
					selection.parent.add(fNew);
					connect.add(fNew);
					selection.parent.remove(fo);
					selection.parent.remove(fi);
				}
			}
			counter.increment();
		}
		counter = new WB_ProgressCounter(heCorrelation.size(), 10);

		tracker.setCounterStatus(this, "Connecting outer and inner boundaries.", counter);
		final Iterator<Map.Entry<Long, Long>> it = heCorrelation.entrySet().iterator();
		while (it.hasNext()) {
			final Map.Entry<Long, Long> pairs = it.next();
			he1 = selection.parent.getHalfedgeWithKey(pairs.getKey());
			he2 = selection.parent.getHalfedgeWithKey(pairs.getValue());
			heio = new HE_Halfedge();
			heoi = new HE_Halfedge();
			selection.parent.add(heio);
			selection.parent.add(heoi);
			selection.parent.setVertex(heio, he1.getPair().getVertex());
			selection.parent.setVertex(heoi, he2.getPair().getVertex());
			selection.parent.setNext(he1, heio);
			selection.parent.setNext(heio, he2);
			selection.parent.setNext(he2, heoi);
			selection.parent.setNext(heoi, he1);
			fNew = new HE_Face();
			boundary.add(boundary);
			selection.parent.add(fNew);
			selection.parent.setHalfedge(fNew, he1);
			selection.parent.setFace(he1, fNew);
			selection.parent.setFace(he2, fNew);
			selection.parent.setFace(heio, fNew);
			selection.parent.setFace(heoi, fNew);
			counter.increment();
		}
		selection.parent.pairHalfedges();
		selection.parent.addSelection("connect", this, connect);
		selection.parent.addSelection("boundary", this, boundary);
		tracker.setStopStatus(this, "Exiting HEM_Lattice.");
		return selection.parent;
	}
}
