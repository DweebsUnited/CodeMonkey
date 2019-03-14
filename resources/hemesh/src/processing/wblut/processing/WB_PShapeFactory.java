/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 *
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 *
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.processing;

import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import wblut.geom.WB_Coord;
import wblut.geom.WB_CoordCollection;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Mesh;
import wblut.geom.WB_Vector;
import wblut.hemesh.HEC_IsoSkin;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Halfedge;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_HalfedgeStructure;
import wblut.hemesh.HE_Selection;
import wblut.hemesh.HE_Vertex;

/**
 * @author FVH
 *
 */
public class WB_PShapeFactory {

	private static WB_GeometryFactory geometryfactory = new WB_GeometryFactory();

	/**
	 *
	 * @param mesh
	 * @param home
	 * @return
	 */
	public static PShape createFacetedPShape(final HE_Mesh mesh, final PApplet home) {

		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		List<HE_Vertex> vertices;
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			vertices = f.getFaceVertices();
			final int[] tris = f.getTriangles();
			HE_Vertex v0, v1, v2;
			for (int i = 0; i < tris.length; i += 3) {
				v0 = vertices.get(tris[i]);
				v1 = vertices.get(tris[i + 1]);
				v2 = vertices.get(tris[i + 2]);
				retained.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(), v0.getUVW(f).vf());
				retained.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(), v1.getUVW(f).vf());
				retained.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(), v2.getUVW(f).vf());
			}
		}
		retained.endShape();
		return retained;

	}

	/**
	 *
	 * @param mesh
	 * @param img
	 * @param home
	 * @return
	 */
	public static PShape createFacetedPShape(final HE_Mesh mesh, final PImage img, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		retained.texture(img);
		List<HE_Vertex> vertices;
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			vertices = f.getFaceVertices();
			final int[] tris = f.getTriangles();
			HE_Vertex v0, v1, v2;
			for (int i = 0; i < tris.length; i += 3) {
				v0 = vertices.get(tris[i]);
				v1 = vertices.get(tris[i + 1]);
				v2 = vertices.get(tris[i + 2]);
				retained.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(), v0.getUVW(f).vf());
				retained.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(), v1.getUVW(f).vf());
				retained.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(), v2.getUVW(f).vf());
			}
		}
		retained.endShape();
		return retained;
	}

	/**
	 *
	 * @param mesh
	 * @param img
	 * @param home
	 * @return
	 */
	public static PShape createFacetedPShape(final HE_Mesh mesh, final PImage[] img, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		List<HE_Vertex> vertices;
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			retained.texture(img[f.getTextureId()]);
			vertices = f.getFaceVertices();
			final int[] tris = f.getTriangles();
			HE_Vertex v0, v1, v2;
			for (int i = 0; i < tris.length; i += 3) {
				v0 = vertices.get(tris[i]);
				v1 = vertices.get(tris[i + 1]);
				v2 = vertices.get(tris[i + 2]);
				retained.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(), v0.getUVW(f).vf());
				retained.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(), v1.getUVW(f).vf());
				retained.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(), v2.getUVW(f).vf());
			}
		}
		retained.endShape();

		return retained;
	}

	/**
	 *
	 * @param mesh
	 * @param offset
	 * @param home
	 * @return
	 */
	public static PShape createFacetedPShape(final HE_HalfedgeStructure mesh, final double offset, final PApplet home) {

		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		List<HE_Vertex> vertices;
		HE_Vertex v;
		WB_Coord fn;
		final float df = (float) offset;
		while (fItr.hasNext()) {
			f = fItr.next();
			vertices = f.getFaceVertices();
			if (vertices.size() > 2) {
				final int[] tris = f.getTriangles();
				for (int i = 0; i < tris.length; i += 3) {
					v = vertices.get(tris[i]);
					fn = v.getVertexNormal();
					retained.vertex(v.xf() + df * fn.xf(), v.yf() + df * fn.yf(), v.zf() + df * fn.zf(),
							v.getUVW(f).xf(), v.getUVW(f).yf());
					v = vertices.get(tris[i + 1]);
					fn = v.getVertexNormal();
					retained.vertex(v.xf() + df * fn.xf(), v.yf() + df * fn.yf(), v.zf() + df * fn.zf(),
							v.getUVW(f).xf(), v.getUVW(f).yf());
					v = vertices.get(tris[i + 2]);
					fn = v.getVertexNormal();
					retained.vertex(v.xf() + df * fn.xf(), v.yf() + df * fn.yf(), v.zf() + df * fn.zf(),
							v.getUVW(f).xf(), v.getUVW(f).yf());
				}
			}

		}
		retained.endShape();

		return retained;
	}

	/**
	 *
	 * @param mesh
	 * @param home
	 * @return
	 */
	public static PShape createFacetedPShape(final WB_Mesh mesh, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		final WB_Mesh lmesh = geometryfactory.createTriMesh(mesh);
		final WB_CoordCollection seq = lmesh.getPoints();
		WB_Coord p = seq.get(0);
		for (int i = 0; i < lmesh.getNumberOfFaces(); i++) {
			int id = lmesh.getFace(i)[0];
			p = seq.get(id);
			retained.vertex(p.xf(), p.yf(), p.zf());
			id = lmesh.getFace(i)[1];
			p = seq.get(id);
			retained.vertex(p.xf(), p.yf(), p.zf());
			id = lmesh.getFace(i)[2];
			p = seq.get(id);
			;
			retained.vertex(p.xf(), p.yf(), p.zf());
		}
		retained.endShape();
		return retained;
	}

	/**
	 *
	 * @param mesh
	 * @param home
	 * @return
	 */
	public static PShape createFacetedPShapeWithFaceColor(final HE_Mesh mesh, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		List<HE_Vertex> vertices;
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			retained.fill(f.getColor());
			vertices = f.getFaceVertices();
			final int[] tris = f.getTriangles();
			HE_Vertex v0, v1, v2;
			for (int i = 0; i < tris.length; i += 3) {
				v0 = vertices.get(tris[i]);
				v1 = vertices.get(tris[i + 1]);
				v2 = vertices.get(tris[i + 2]);
				retained.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(), v0.getUVW(f).vf());
				retained.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(), v1.getUVW(f).vf());
				retained.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(), v2.getUVW(f).vf());
			}
		}
		retained.endShape();
		return retained;
	}

	/**
	 *
	 * @param mesh
	 * @param home
	 * @return
	 */
	public static PShape createFacetedPShapeWithVertexColor(final HE_Mesh mesh, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		List<HE_Vertex> vertices;
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			vertices = f.getFaceVertices();
			final int[] tris = f.getTriangles();
			HE_Vertex v0, v1, v2;
			for (int i = 0; i < tris.length; i += 3) {
				v0 = vertices.get(tris[i]);
				v1 = vertices.get(tris[i + 1]);
				v2 = vertices.get(tris[i + 2]);
				retained.fill(v0.getColor());
				retained.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(), v0.getUVW(f).vf());
				retained.fill(v1.getColor());
				retained.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(), v1.getUVW(f).vf());
				retained.fill(v2.getColor());
				retained.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(), v2.getUVW(f).vf());
			}
		}
		retained.endShape();
		return retained;

	}

	/**
	 *
	 * @param mesh
	 * @param home
	 * @return
	 */
	public static PShape createFacettedPShape(final HE_Mesh mesh, final PApplet home) {
		return createFacetedPShape(mesh, home);
	}

	/**
	 *
	 * @param mesh
	 * @param img
	 * @param home
	 * @return
	 */
	public static PShape createFacettedPShape(final HE_Mesh mesh, final PImage img, final PApplet home) {
		return createFacetedPShape(mesh, img, home);
	}

	/**
	 *
	 * @param mesh
	 * @param img
	 * @param home
	 * @return
	 */
	public static PShape createFacettedPShape(final HE_Mesh mesh, final PImage[] img, final PApplet home) {
		return createFacetedPShape(mesh, img, home);
	}

	/**
	 *
	 * @param mesh
	 * @param offset
	 * @param home
	 * @return
	 */
	public static PShape createFacettedPShape(final HE_HalfedgeStructure mesh, final double offset, final PApplet home) {
		return createFacetedPShape(mesh, offset, home);
	}

	/**
	 *
	 * @param mesh
	 * @param home
	 * @return
	 */
	public static PShape createFacettedPShape(final WB_Mesh mesh, final PApplet home) {
		return createFacetedPShape(mesh, home);
	}

	/**
	 *
	 * @param mesh
	 * @param home
	 * @return
	 */
	public static PShape createFacettedPShapeWithFaceColor(final HE_Mesh mesh, final PApplet home) {
		return createFacetedPShapeWithFaceColor(mesh, home);
	}

	/**
	 *
	 * @param mesh
	 * @param home
	 * @return
	 */
	public static PShape createFacettedPShapeWithVertexColor(final HE_Mesh mesh, final PApplet home) {
		return createFacetedPShapeWithVertexColor(mesh, home);
	}

	/**
	 *
	 * @param mesh
	 * @param home
	 * @return
	 */
	public static PShape createSmoothPShape(final HE_Mesh mesh, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		List<HE_Vertex> vertices;
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			vertices = f.getFaceVertices();
			final int[] tris = f.getTriangles();
			HE_Vertex v0, v1, v2;
			WB_Coord n0, n1, n2;
			for (int i = 0; i < tris.length; i += 3) {
				v0 = vertices.get(tris[i]);
				n0 = v0.getVertexNormal();
				v1 = vertices.get(tris[i + 1]);
				n1 = v1.getVertexNormal();
				v2 = vertices.get(tris[i + 2]);
				n2 = v2.getVertexNormal();
				retained.normal(n0.xf(), n0.yf(), n0.zf());
				retained.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(), v0.getUVW(f).vf());
				retained.normal(n1.xf(), n1.yf(), n1.zf());
				retained.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(), v1.getUVW(f).vf());
				retained.normal(n2.xf(), n2.yf(), n2.zf());
				retained.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(), v2.getUVW(f).vf());
			}
		}
		retained.endShape();
		return retained;
	}

	/**
	 *
	 * @param mesh
	 * @param img
	 * @param home
	 * @return
	 */
	public static PShape createSmoothPShape(final HE_Mesh mesh, final PImage img, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		retained.texture(img);
		List<HE_Vertex> vertices;
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			vertices = f.getFaceVertices();
			final int[] tris = f.getTriangles();
			HE_Vertex v0, v1, v2;
			WB_Coord n0, n1, n2;
			for (int i = 0; i < tris.length; i += 3) {
				v0 = vertices.get(tris[i]);
				n0 = v0.getVertexNormal();
				v1 = vertices.get(tris[i + 1]);
				n1 = v1.getVertexNormal();
				v2 = vertices.get(tris[i + 2]);
				n2 = v2.getVertexNormal();
				retained.normal(n0.xf(), n0.yf(), n0.zf());
				retained.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(), v0.getUVW(f).vf());
				retained.normal(n1.xf(), n1.yf(), n1.zf());
				retained.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(), v1.getUVW(f).vf());
				retained.normal(n2.xf(), n2.yf(), n2.zf());
				retained.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(), v2.getUVW(f).vf());
			}
		}
		retained.endShape();
		return retained;
	}

	/**
	 *
	 * @param mesh
	 * @param img
	 * @param home
	 * @return
	 */
	public static PShape createSmoothPShape(final HE_Mesh mesh, final PImage[] img, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		List<HE_Vertex> vertices;
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			retained.texture(img[f.getTextureId()]);
			vertices = f.getFaceVertices();
			final int[] tris = f.getTriangles();
			HE_Vertex v0, v1, v2;
			WB_Coord n0, n1, n2;
			for (int i = 0; i < tris.length; i += 3) {
				v0 = vertices.get(tris[i]);
				n0 = v0.getVertexNormal();
				v1 = vertices.get(tris[i + 1]);
				n1 = v1.getVertexNormal();
				v2 = vertices.get(tris[i + 2]);
				n2 = v2.getVertexNormal();
				retained.normal(n0.xf(), n0.yf(), n0.zf());
				retained.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(), v0.getUVW(f).vf());
				retained.normal(n1.xf(), n1.yf(), n1.zf());
				retained.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(), v1.getUVW(f).vf());
				retained.normal(n2.xf(), n2.yf(), n2.zf());
				retained.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(), v2.getUVW(f).vf());
			}
		}
		retained.endShape();
		return retained;

	}

	/**
	 *
	 * @param mesh
	 * @param home
	 * @return
	 */
	public static PShape createSmoothPShape(final WB_Mesh mesh, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		final WB_Mesh lmesh = geometryfactory.createTriMesh(mesh);
		final WB_Vector v = geometryfactory.createVector();
		final WB_CoordCollection seq = lmesh.getPoints();
		WB_Coord p = seq.get(0);
		for (int i = 0; i < lmesh.getNumberOfFaces(); i++) {
			int id = lmesh.getFace(i)[0];
			v.set(lmesh.getVertexNormal(id));
			retained.normal(v.xf(), v.yf(), v.zf());
			p = seq.get(id);
			retained.vertex(p.xf(), p.yf(), p.zf());
			id = lmesh.getFace(i)[1];
			v.set(lmesh.getVertexNormal(id));
			retained.normal(v.xf(), v.yf(), v.zf());
			p = seq.get(id);
			retained.vertex(p.xf(), p.yf(), p.zf());
			id = lmesh.getFace(i)[2];
			v.set(lmesh.getVertexNormal(id));
			retained.normal(v.xf(), v.yf(), v.zf());
			p = seq.get(id);
			retained.vertex(p.xf(), p.yf(), p.zf());
		}
		retained.endShape();
		return retained;
	}

	/**
	 *
	 * @param mesh
	 * @param home
	 * @return
	 */
	public static PShape createSmoothPShapeWithFaceColor(final HE_Mesh mesh, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		List<HE_Vertex> vertices;
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			retained.fill(f.getColor());
			vertices = f.getFaceVertices();
			final int[] tris = f.getTriangles();
			HE_Vertex v0, v1, v2;
			WB_Coord n0, n1, n2;
			for (int i = 0; i < tris.length; i += 3) {
				v0 = vertices.get(tris[i]);
				n0 = v0.getVertexNormal();
				v1 = vertices.get(tris[i + 1]);
				n1 = v1.getVertexNormal();
				v2 = vertices.get(tris[i + 2]);
				n2 = v2.getVertexNormal();
				retained.normal(n0.xf(), n0.yf(), n0.zf());
				retained.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(), v0.getUVW(f).vf());
				retained.normal(n1.xf(), n1.yf(), n1.zf());
				retained.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(), v1.getUVW(f).vf());
				retained.normal(n2.xf(), n2.yf(), n2.zf());
				retained.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(), v2.getUVW(f).vf());
			}
		}
		retained.endShape();

		return retained;
	}

	/**
	 *
	 * @param mesh
	 * @param home
	 * @return
	 */
	public static PShape createSmoothPShapeWithVertexColor(final HE_Mesh mesh, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		List<HE_Vertex> vertices;
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			vertices = f.getFaceVertices();
			final int[] tris = f.getTriangles();
			HE_Vertex v0, v1, v2;
			for (int i = 0; i < tris.length; i += 3) {
				v0 = vertices.get(tris[i]);
				v1 = vertices.get(tris[i + 1]);
				v2 = vertices.get(tris[i + 2]);
				retained.fill(v0.getColor());
				retained.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(), v0.getUVW(f).vf());
				retained.fill(v1.getColor());
				retained.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(), v1.getUVW(f).vf());
				retained.fill(v2.getColor());
				retained.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(), v2.getUVW(f).vf());
			}
		}
		retained.endShape();
		return retained;
	}

	/**
	 *
	 * @param mesh
	 * @param home
	 * @return
	 */
	public static PShape createWireframePShape(final HE_HalfedgeStructure mesh, final PApplet home) {
		final PShape retained = home.createShape();
		if (mesh instanceof HE_Selection) {
			((HE_Selection) mesh).collectEdgesByFace();
		}
		Iterator<HE_Halfedge> eItr = mesh.eItr();
		HE_Halfedge e;
		HE_Vertex v;
		retained.beginShape(PConstants.LINES);
		while (eItr.hasNext()) {
			e = eItr.next();
			v = e.getVertex();
			retained.vertex(v.xf(), v.yf(), v.zf());
			v = e.getEndVertex();
			retained.vertex(v.xf(), v.yf(), v.zf());
		}
		retained.endShape();
		return retained;
	}

	public static PShape createSubstratePShape(final HEC_IsoSkin skin, final PApplet home) {

		final PShape retained = home.createShape();
		retained.beginShape(PApplet.LINES);
		WB_Coord v;
		wblut.hemesh.HEC_IsoSkin.Cell cell;
		for (int i = 0; i < skin.getNumberOfLayers(); i++) {
			for (int j = 0; j < skin.getCells()[0].length; j++) {
				cell = skin.getCells()[i][j];

				if (i == 0) {
					v = skin.getGridpositions()[i][cell.getCornerIndices()[0]];
					retained.vertex(v.xf(), v.yf(), v.zf());
					v = skin.getGridpositions()[i][cell.getCornerIndices()[1]];
					retained.vertex(v.xf(), v.yf(), v.zf());
					v = skin.getGridpositions()[i][cell.getCornerIndices()[0]];
					retained.vertex(v.xf(), v.yf(), v.zf());
					v = skin.getGridpositions()[i][cell.getCornerIndices()[2]];
					retained.vertex(v.xf(), v.yf(), v.zf());
					v = skin.getGridpositions()[i][cell.getCornerIndices()[1]];
					retained.vertex(v.xf(), v.yf(), v.zf());
					v = skin.getGridpositions()[i][cell.getCornerIndices()[3]];
					retained.vertex(v.xf(), v.yf(), v.zf());
					v = skin.getGridpositions()[i][cell.getCornerIndices()[2]];
					retained.vertex(v.xf(), v.yf(), v.zf());
					v = skin.getGridpositions()[i][cell.getCornerIndices()[3]];
					retained.vertex(v.xf(), v.yf(), v.zf());

				}
				v = skin.getGridpositions()[i][cell.getCornerIndices()[0]];
				retained.vertex(v.xf(), v.yf(), v.zf());
				v = skin.getGridpositions()[i + 1][cell.getCornerIndices()[0]];
				retained.vertex(v.xf(), v.yf(), v.zf());
				v = skin.getGridpositions()[i][cell.getCornerIndices()[1]];
				retained.vertex(v.xf(), v.yf(), v.zf());
				v = skin.getGridpositions()[i + 1][cell.getCornerIndices()[1]];
				retained.vertex(v.xf(), v.yf(), v.zf());
				v = skin.getGridpositions()[i][cell.getCornerIndices()[2]];
				retained.vertex(v.xf(), v.yf(), v.zf());
				v = skin.getGridpositions()[i + 1][cell.getCornerIndices()[2]];
				retained.vertex(v.xf(), v.yf(), v.zf());
				v = skin.getGridpositions()[i][cell.getCornerIndices()[3]];
				retained.vertex(v.xf(), v.yf(), v.zf());
				v = skin.getGridpositions()[i + 1][cell.getCornerIndices()[3]];
				retained.vertex(v.xf(), v.yf(), v.zf());
				v = skin.getGridpositions()[i + 1][cell.getCornerIndices()[0]];
				retained.vertex(v.xf(), v.yf(), v.zf());
				v = skin.getGridpositions()[i + 1][cell.getCornerIndices()[1]];
				retained.vertex(v.xf(), v.yf(), v.zf());
				v = skin.getGridpositions()[i + 1][cell.getCornerIndices()[0]];
				retained.vertex(v.xf(), v.yf(), v.zf());
				v = skin.getGridpositions()[i + 1][cell.getCornerIndices()[2]];
				retained.vertex(v.xf(), v.yf(), v.zf());
				v = skin.getGridpositions()[i + 1][cell.getCornerIndices()[1]];
				retained.vertex(v.xf(), v.yf(), v.zf());
				v = skin.getGridpositions()[i + 1][cell.getCornerIndices()[3]];
				retained.vertex(v.xf(), v.yf(), v.zf());
				v = skin.getGridpositions()[i + 1][cell.getCornerIndices()[2]];
				retained.vertex(v.xf(), v.yf(), v.zf());
				v = skin.getGridpositions()[i + 1][cell.getCornerIndices()[3]];
				retained.vertex(v.xf(), v.yf(), v.zf());

			}
		}
		retained.endShape();
		retained.disableStyle();
		return retained;

	}

}
