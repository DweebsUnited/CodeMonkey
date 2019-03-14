/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 * 
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 * 
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.geom;

public interface WB_Curve {

	/**
	 *
	 *
	 * @param u
	 * @return
	 */
	public WB_Point curvePoint(double u);

	/**
	 *
	 *
	 * @param u
	 * @return
	 */
	public WB_Vector curveDirection(double u);

	/**
	 *
	 *
	 * @param u
	 * @return
	 */
	public WB_Vector curveDerivative(double u);

	/**
	 *
	 *
	 * @return
	 */
	public double getLowerU();

	/**
	 *
	 *
	 * @return
	 */
	public double getUpperU();
}
