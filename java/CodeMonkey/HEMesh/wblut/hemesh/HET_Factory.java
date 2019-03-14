/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 * 
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 * 
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.hemesh;

/**
 * @author FVH
 *
 */
public class HET_Factory {
	/**
	 *
	 */
	protected HET_Factory() {

	}

	/**
	 *
	 */
	private static final HET_Factory factory = new HET_Factory();

	/**
	 *
	 *
	 * @return
	 */
	public static HET_Factory instance() {
		return factory;
	}

}
