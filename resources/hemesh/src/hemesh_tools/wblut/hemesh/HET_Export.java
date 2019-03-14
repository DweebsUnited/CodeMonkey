/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 * 
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 * 
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.hemesh;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.collections.impl.map.mutable.primitive.LongIntHashMap;

import wblut.geom.WB_Coord;

/**
 *
 * Collection of export functions.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HET_Export {
	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToOBJ(final HE_Mesh mesh, final String path, final String name) {
		HET_WriterOBJ.saveMesh(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToOBJNN(final HE_Mesh mesh, final String path, final String name) {
		HET_WriterOBJ.saveMeshNN(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToOBJWithFaceColor(final HE_Mesh mesh, final String path, final String name) {
		HET_WriterOBJ.saveMeshWithFaceColor(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToOBJWithVertexColor(final HE_Mesh mesh, final String path, final String name) {
		HET_WriterOBJ.saveMeshWithVertexColor(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToOBJ(final Collection<? extends HE_Mesh> mesh, final String path, final String name) {
		HET_WriterOBJ.saveMesh(mesh, path, name);
	}

	public static void saveToOBJ(final HE_MeshCollection mesh, final String path, final String name) {
		HET_WriterOBJ.saveMesh(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToOBJNN(final Collection<? extends HE_Mesh> mesh, final String path, final String name) {
		HET_WriterOBJ.saveMeshNN(mesh, path, name);
	}

	public static void saveToOBJNN(final HE_MeshCollection mesh, final String path, final String name) {
		HET_WriterOBJ.saveMeshNN(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToOBJWithFaceColor(final Collection<? extends HE_Mesh> mesh, final String path,
			final String name) {
		HET_WriterOBJ.saveMeshWithFaceColor(mesh, path, name);
	}

	public static void saveToOBJWithFaceColor(final HE_MeshCollection mesh, final String path, final String name) {
		HET_WriterOBJ.saveMeshWithFaceColor(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToOBJWithVertexColor(final Collection<? extends HE_Mesh> mesh, final String path,
			final String name) {
		HET_WriterOBJ.saveMeshWithVertexColor(mesh, path, name);
	}

	public static void saveToOBJWithVertexColor(final HE_MeshCollection mesh, final String path, final String name) {
		HET_WriterOBJ.saveMeshWithVertexColor(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToOBJ(final HE_Mesh[] mesh, final String path, final String name) {
		HET_WriterOBJ.saveMesh(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToOBJNN(final HE_Mesh[] mesh, final String path, final String name) {
		HET_WriterOBJ.saveMeshNN(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToOBJWithFaceColor(final HE_Mesh[] mesh, final String path, final String name) {
		HET_WriterOBJ.saveMeshWithFaceColor(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToOBJWithVertexColor(final HE_Mesh[] mesh, final String path, final String name) {
		HET_WriterOBJ.saveMeshWithVertexColor(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToSTL(final HE_Mesh mesh, final String path, final String name) {
		saveToSTLWithFaceColor(mesh, path, name, NONE);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 * @param colormodel
	 */
	public static void saveToSTLWithFaceColor(final HE_Mesh mesh, final String path, final String name,
			final int colormodel) {
		final HET_WriterSTL stl = new HET_WriterSTL(colormodel == 1 ? HET_WriterSTL.MATERIALISE
				: colormodel == 0 ? HET_WriterSTL.DEFAULT : HET_WriterSTL.NONE, HET_WriterSTL.DEFAULT_BUFFER);
		stl.beginSave(path, name, mesh.getNumberOfFaces());
		saveToSTLWithFaceColor(mesh, stl);
		stl.endSave();
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param stl
	 */
	public static void saveToSTLWithFaceColor(final HE_Mesh mesh, final HET_WriterSTL stl) {
		final HE_FaceIterator fitr = mesh.fItr();
		HE_Face f;
		while (fitr.hasNext()) {
			f = fitr.next();
			stl.face(f.getHalfedge().getVertex(), f.getHalfedge().getNextInFace().getVertex(),
					f.getHalfedge().getPrevInFace().getVertex(), f.getFaceNormal(), f.getColor());
		}
	}

	/**
	 * Saves the mesh as simpleMesh format to the given file path. Existing
	 * files will be overwritten. The file gives the vertex coordinates and an
	 * indexed facelist.
	 *
	 * @param mesh
	 *            the mesh
	 * @param path
	 *            the path
	 * @param name
	 */
	public static void saveToSimpleMesh(final HE_Mesh mesh, final String path, final String name) {
		final HET_WriterSimpleMesh hem = new HET_WriterSimpleMesh();
		hem.beginSave(path, name);
		final List<WB_Coord> points = mesh.getVerticesAsCoord();
		hem.intValue(mesh.getNumberOfVertices());
		hem.vertices(points);
		final int[][] faces = mesh.getFacesAsInt();
		hem.intValue(mesh.getNumberOfFaces());
		hem.faces(faces);
		hem.endSave();
	}

	/**
	 * Saves the mesh as hemesh format to the given file path. Existing files
	 * will be overwritten. The file contains the vertex coordinates and all
	 * half-edge interconnection information. Larger than a simpleMesh but much
	 * quicker to rebuild.
	 *
	 * @param mesh
	 *            the mesh
	 * @param path
	 *            the path
	 * @param name
	 */
	public static void saveToHemesh(final HE_Mesh mesh, final String path, final String name) {
		final HET_WriterHemesh hem = new HET_WriterHemesh();
		hem.beginSave(path, name);
		final LongIntHashMap vertexKeys = new LongIntHashMap();
		Iterator<HE_Vertex> vItr = mesh.vItr();
		int i = 0;
		while (vItr.hasNext()) {
			vertexKeys.put(vItr.next().key(), i);
			i++;
		}
		final LongIntHashMap halfedgeKeys = new LongIntHashMap();
		Iterator<HE_Halfedge> heItr = mesh.heItr();
		i = 0;
		while (heItr.hasNext()) {
			halfedgeKeys.put(heItr.next().key(), i);
			i++;
		}
		final LongIntHashMap faceKeys = new LongIntHashMap();
		Iterator<HE_Face> fItr = mesh.fItr();
		i = 0;
		while (fItr.hasNext()) {
			faceKeys.put(fItr.next().key(), i);
			i++;
		}
		hem.sizes(mesh.getNumberOfVertices(), mesh.getNumberOfHalfedges(), mesh.getNumberOfFaces());
		vItr = mesh.vItr();
		HE_Vertex v;
		int heid;
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getHalfedge() == null) {
				heid = -1;
			} else {
				heid = halfedgeKeys.get(v.getHalfedge().key());
			}
			hem.vertex(v, heid);
		}
		heItr = mesh.heItr();
		HE_Halfedge he;
		int vid, henextid, hepairid;
		int fid;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getVertex() == null) {
				vid = -1;
			} else {
				vid = vertexKeys.get(he.getVertex().key());

			}
			if (he.getNextInFace() == null) {
				henextid = -1;
			} else {
				henextid = halfedgeKeys.get(he.getNextInFace().key());

			}
			if (he.getPair() == null) {
				hepairid = -1;
			} else {
				hepairid = halfedgeKeys.get(he.getPair().key());

			}
			if (he.getFace() == null) {
				fid = -1;
			} else {
				fid = faceKeys.get(he.getFace().key());

			}
			hem.halfedge(he, vid, henextid, hepairid, fid);
		}
		fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getHalfedge() == null) {
				heid = -1;
			} else {
				heid = halfedgeKeys.get(f.getHalfedge().key());

			}
			hem.face(f, heid);
		}
		hem.endSave();
	}

	/**
	 * Saves the mesh as binary hemesh format to the given file path. Existing
	 * files will be overwritten. The file contains the vertex coordinates and
	 * all half-edge interconnection information. About the same size of a
	 * simpleMesh but a lot quicker to rebuild. Due to compression about half as
	 * fast as an ordinary hemesh file but only a third in size.
	 *
	 * @param mesh
	 *            the mesh
	 * @param path
	 *            the path
	 * @param name
	 */
	public static void saveToBinaryHemesh(final HE_Mesh mesh, final String path, final String name) {
		final HET_WriterBinaryHemesh hem = new HET_WriterBinaryHemesh();
		hem.beginSave(path, name);
		final LongIntHashMap vertexKeys = new LongIntHashMap();
		Iterator<HE_Vertex> vItr = mesh.vItr();
		int i = 0;
		while (vItr.hasNext()) {
			vertexKeys.put(vItr.next().key(), i);
			i++;
		}
		final LongIntHashMap halfedgeKeys = new LongIntHashMap();
		Iterator<HE_Halfedge> heItr = mesh.heItr();
		i = 0;
		while (heItr.hasNext()) {
			halfedgeKeys.put(heItr.next().key(), i);
			i++;
		}
		final LongIntHashMap faceKeys = new LongIntHashMap();
		Iterator<HE_Face> fItr = mesh.fItr();
		i = 0;
		while (fItr.hasNext()) {
			faceKeys.put(fItr.next().key(), i);
			i++;
		}
		hem.sizes(mesh.getNumberOfVertices(), mesh.getNumberOfHalfedges(), mesh.getNumberOfFaces());
		vItr = mesh.vItr();
		HE_Vertex v;
		Integer heid;
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getHalfedge() == null) {
				heid = -1;
			} else {
				heid = halfedgeKeys.get(v.getHalfedge().key());

			}
			hem.vertex(v, heid);
		}
		heItr = mesh.heItr();
		HE_Halfedge he;
		Integer vid, henextid, hepairid, fid;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getVertex() == null) {
				vid = -1;
			} else {
				vid = vertexKeys.get(he.getVertex().key());

			}
			if (he.getNextInFace() == null) {
				henextid = -1;
			} else {
				henextid = halfedgeKeys.get(he.getNextInFace().key());

			}
			if (he.getPair() == null) {
				hepairid = -1;
			} else {
				hepairid = halfedgeKeys.get(he.getPair().key());

			}
			if (he.getFace() == null) {
				fid = -1;
			} else {
				fid = faceKeys.get(he.getFace().key());

			}
			hem.halfedge(he, vid, henextid, hepairid, fid);
		}
		fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getHalfedge() == null) {
				heid = -1;
			} else {
				heid = halfedgeKeys.get(f.getHalfedge().key());

			}
			hem.face(f, heid);
		}
		hem.endSave();
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToPOV(final HE_Mesh mesh, final String path, final String name) {
		saveToPOV(mesh, path, name, true);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 * @param saveNormals
	 */
	public static void saveToPOV(final HE_Mesh mesh, final String path, final String name, final boolean saveNormals) {
		final HET_WriterPOV obj = new HET_WriterPOV();
		obj.beginSave(path, name);
		saveToPOV(mesh, obj, saveNormals);
		obj.endSave();
	}

	/**
	 * Saves the mesh as PovRAY mesh2 format by appending it to the given mesh.
	 *
	 * @param mesh
	 *            the mesh
	 * @param pov
	 *            instance of HET_POVWriter
	 * @param normals
	 *            smooth faces {@link HET_WriterPOV} instance.
	 */
	public static void saveToPOV(final HE_Mesh mesh, final HET_WriterPOV pov, final boolean normals) {
		final int vOffset = pov.getCurrVertexOffset();
		pov.beginMesh2(String.format("obj%d", mesh.getKey()));
		final LongIntHashMap keyToIndex = new LongIntHashMap();
		Iterator<HE_Vertex> vItr = mesh.vItr();
		final int vcount = mesh.getNumberOfVertices();
		pov.total(vcount);
		HE_Vertex v;
		int fcount = 0;
		while (vItr.hasNext()) {
			v = vItr.next();
			keyToIndex.put(v.key(), fcount);
			pov.vertex(v);
			fcount++;
		}
		pov.endSection();
		if (normals) {
			pov.beginNormals(vcount);
			vItr = mesh.vItr();
			while (vItr.hasNext()) {
				pov.vertex(vItr.next().getVertexNormal());
			}
			pov.endSection();
		}
		final Iterator<HE_Face> fItr = mesh.fItr();
		pov.beginIndices(mesh.getNumberOfFaces());
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			pov.face(keyToIndex.get(f.getHalfedge().getVertex().key()) + vOffset,
					keyToIndex.get(f.getHalfedge().getNextInFace().getVertex().key()) + vOffset,
					keyToIndex.get(f.getHalfedge().getPrevInFace().getVertex().key()) + vOffset);
		}
		pov.endSection();
	}

	/**
	 * Saves the mesh as PovRAY format to the given PrintWriter.
	 *
	 * @param mesh
	 *            HE_Mesh
	 * @param pw
	 *            PrintWriter
	 */
	public static void saveToPOV(final HE_Mesh mesh, final PrintWriter pw) {
		saveToPOV(mesh, pw, true);
	}

	/**
	 * Saves the mesh as PovRAY format to the given PrintWriter.
	 *
	 * @param mesh
	 *            HE_Mesh
	 * @param pw
	 *            PrintWriter
	 * @param saveNormals
	 *            boolean (Smooth face or otherwise)
	 */
	public static void saveToPOV(final HE_Mesh mesh, final PrintWriter pw, final boolean saveNormals) {
		final HET_WriterPOV obj = new HET_WriterPOV();
		obj.beginSave(pw);
		saveToPOV(mesh, obj, saveNormals);
		obj.endSave();
	}

	/**
	 *
	 */
	public static int NONE = -1;
	/**
	 *
	 */
	public static int DEFAULT = 0;
	/**
	 *
	 */
	public static int MATERIALISE = 1;

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToWRLWithFaceColor(final HE_Mesh mesh, final String path, final String name) {
		HET_WriterWRL.saveMeshWithFaceColor(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToWRLWithVertexColor(final HE_Mesh mesh, final String path, final String name) {
		HET_WriterWRL.saveMeshWithVertexColor(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToWRL(final HE_Mesh mesh, final String path, final String name) {
		HET_WriterWRL.saveMesh(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToPLY(final HE_Mesh mesh, final String path, final String name) {
		HET_WriterPLY.saveMesh(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToPLYWithVertexColor(final HE_Mesh mesh, final String path, final String name) {
		HET_WriterPLY.saveMeshWithVertexColor(mesh, path, name);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param path
	 * @param name
	 */
	public static void saveToPLYWithFaceColor(final HE_Mesh mesh, final String path, final String name) {
		HET_WriterPLY.saveMeshWithFaceColor(mesh, path, name);
	}
}
