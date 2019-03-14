/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 *
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 *
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */
package wblut;

import processing.opengl.PGraphics3D;
import wblut.geom.WB_Coord;
import wblut.processing.WB_Render3D;

/**
 * @author FVH
 *
 */
public class H3D extends PGraphics3D {
	private WB_Render3D render;

	/**
	 *
	 */
	public H3D() {
		super();
		render = new WB_Render3D(this);
	}

	public void center() {
		this.translate(this.width * 0.5f, this.height * 0.5f);
	}

	public void vertex(final WB_Coord v) {
		render.vertex(v);
	}

	public void normal(final WB_Coord n) {
		render.normal(n);
	}

	public void vertex2D(final WB_Coord v) {
		render.vertex2D(v);
	}

	public void translate(final WB_Coord v) {
		render.translate(v);
	}

	public void translate2D(final WB_Coord v) {
		render.translate2D(v);
	}

}
