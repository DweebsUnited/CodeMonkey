/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 *
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 *
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.hemesh;

import wblut.geom.WB_Transform;

/**
 * @author FVH
 *
 */
public class HEC_Transform extends HEC_Creator {

	private WB_Transform T;
	private HE_Mesh source;

	public HEC_Transform() {
		super();
		T = null;
		source = null;
		override = true;

	}

	public HEC_Transform(final HE_Mesh source, final WB_Transform T) {
		super();
		this.T = T;
		this.source = source;
		override = true;

	}

	public HEC_Transform setTransform(final WB_Transform T) {
		this.T = T;
		return this;

	}

	public HEC_Transform setSource(final HE_Mesh mesh) {
		this.source = mesh;
		return this;

	}

	@Override
	public HE_Mesh createBase() {
		HE_Mesh result = new HE_Mesh();
		if (source == null) {
			return result;
		}
		result = source.copy();
		if (T == null) {
			return result;
		}
		final HE_VertexIterator vItr = result.vItr();
		while (vItr.hasNext()) {
			T.applyAsPointSelf(vItr.next());
		}

		return result;
	}

}
