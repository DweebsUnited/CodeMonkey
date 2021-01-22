/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 *
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 *
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.hemesh;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;

import org.eclipse.collections.impl.list.mutable.FastList;

/**
 *
 */
public class HEC_FromBinaryHemeshFile extends HEC_Creator {

	/**
	 *
	 */
	private String path;

	/**
	 *
	 */
	public HEC_FromBinaryHemeshFile() {
		super();
		override = true;
	}

	/**
	 *
	 *
	 * @param path
	 */
	public HEC_FromBinaryHemeshFile(final String path) {
		this();
		this.path = path;
	}

	/**
	 *
	 *
	 * @param path
	 * @return
	 */
	public HEC_FromBinaryHemeshFile setPath(final String path) {
		this.path = path;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		if (path == null) {
			return null;
		}
		final HE_Mesh mesh = new HE_Mesh();
		try {
			final FileInputStream fis = new FileInputStream(path);
			final DataInputStream dis = new DataInputStream(new InflaterInputStream(fis));
			final int numVertices = dis.readInt();
			final int numHalfedges = dis.readInt();
			final int numFaces = dis.readInt();
			final FastList<HE_Vertex> vertices = new FastList<HE_Vertex>();
			for (int i = 0; i < numVertices; i++) {
				vertices.add(new HE_Vertex());
			}
			final FastList<HE_Halfedge> halfedges = new FastList<HE_Halfedge>();
			for (int i = 0; i < numHalfedges; i++) {
				halfedges.add(new HE_Halfedge());
			}
			final FastList<HE_Face> faces = new FastList<HE_Face>();
			for (int i = 0; i < numFaces; i++) {
				faces.add(new HE_Face());
			}
			double x, y, z;
			int heid, vid, henextid, hepairid, fid, hasuvw;
			HE_Vertex v;
			for (int i = 0; i < numVertices; i++) {
				v = vertices.get(i);
				x = dis.readDouble();
				y = dis.readDouble();
				z = dis.readDouble();
				heid = dis.readInt();
				v.setColor(dis.readInt());
				v.setInternalLabel(dis.readInt());
				v.setUserLabel(dis.readInt());
				hasuvw = dis.readInt();
				v.set(x, y, z);
				if (heid > -1) {
					mesh.setHalfedge(v, halfedges.get(heid));
				}

				if (hasuvw == 1) {
					v.setUVW(dis.readDouble(), dis.readDouble(), dis.readDouble());
				}
			}

			HE_Halfedge he;
			for (int i = 0; i < numHalfedges; i++) {
				he = halfedges.get(i);
				vid = dis.readInt();
				henextid = dis.readInt();
				hepairid = dis.readInt();
				fid = dis.readInt();
				he.setColor(dis.readInt());
				he.setInternalLabel(dis.readInt());
				he.setUserLabel(dis.readInt());
				hasuvw = dis.readInt();
				if (vid > -1) {

					mesh.setVertex(he, vertices.get(vid));
				}
				if (henextid > -1) {
					mesh.setNext(he, halfedges.get(henextid));
				}
				if (hepairid > -1) {
					mesh.setPair(he, halfedges.get(hepairid));

				}
				if (fid > -1) {
					mesh.setFace(he, faces.get(fid));
				}
				if (hasuvw == 1) {
					he.setUVW(dis.readDouble(), dis.readDouble(), dis.readDouble());
				}
			}

			HE_Face f;
			for (int i = 0; i < numFaces; i++) {
				f = faces.get(i);
				heid = dis.readInt();
				if (heid > -1) {
					mesh.setHalfedge(f, halfedges.get(heid));
				}
				f.setColor(dis.readInt());
				f.setTextureId(dis.readInt());
				f.setInternalLabel(dis.readInt());
				f.setUserLabel(dis.readInt());
			}
			dis.close();
			mesh.addVertices(vertices);
			mesh.addHalfedges(halfedges);
			mesh.addFaces(faces);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
		return mesh;
	}
}