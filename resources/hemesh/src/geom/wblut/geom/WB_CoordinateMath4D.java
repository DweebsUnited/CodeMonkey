/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 * 
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 * 
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */
package wblut.geom;

/**
 * Interface for implementing non-mutable mathematical operations on 4D
 * coordinates.If the operations should change the calling object use
 * {@link wblut.geom.WB_MutableCoordinateMath4D}.
 *
 * None of the operators change the calling object. Unlabelled operators, such
 * as "add",create a new WB_Coord. Operators with the label "Into", such as
 * "addInto" store the result into a WB_MutableCoord passed as additional
 * parameter.
 *
 * @author Frederik Vanhoutte
 *
 */
public interface WB_CoordinateMath4D {
	/**
	 * Add coordinate values.
	 *
	 * @param x
	 * @return new WB_Coord
	 */
	public WB_Coord add(final double... x);

	/**
	 * Add coordinate values.
	 *
	 * @param p
	 * @return new WB_Coord
	 */
	public WB_Coord add(final WB_Coord p);

	/**
	 * Subtract coordinate values.
	 *
	 * @param x
	 * @return new WB_Coord
	 */
	public WB_Coord sub(final double... x);

	/**
	 * Subtract coordinate values.
	 *
	 * @param p
	 * @return new WB_Coord
	 */
	public WB_Coord sub(final WB_Coord p);

	/**
	 * Multiply by factor.
	 *
	 * @param f
	 * @return new WB_Coord
	 */
	public WB_Coord mul(final double f);

	/**
	 * Divide by factor.
	 *
	 * @param f
	 * @return new WB_Coord
	 */
	public WB_Coord div(final double f);

	/**
	 * Add multiple of coordinate values.
	 *
	 * @param f
	 *            multiplier
	 * @param x
	 * @return new WB_Coord
	 */
	public WB_Coord addMul(final double f, final double... x);

	/**
	 * Add multiple of coordinate values.
	 *
	 * @param f
	 * @param p
	 * @return new WB_Coord
	 */
	public WB_Coord addMul(final double f, final WB_Coord p);

	/**
	 * Multiply this coordinate by factor f and add other coordinate values
	 * multiplied by g.
	 *
	 * @param f
	 * @param g
	 * @param x
	 * @return new WB_Coord
	 */
	public WB_Coord mulAddMul(final double f, final double g, final double... x);

	/**
	 * Multiply this coordinate by factor f and add other coordinate values
	 * multiplied by g.
	 *
	 * @param f
	 * @param g
	 * @param p
	 * @return new WB_Coord
	 */
	public WB_Coord mulAddMul(final double f, final double g, final WB_Coord p);

	/**
	 * Add coordinate values and store in mutable coordinate.
	 *
	 * @param result
	 * @param x
	 */
	public void addInto(final WB_MutableCoord result, final double... x);

	/**
	 * Add coordinate values and store in mutable coordinate.
	 *
	 * @param result
	 * @param p
	 */
	public void addInto(final WB_MutableCoord result, final WB_Coord p);

	/**
	 * Subtract coordinate values and store in mutable coordinate.
	 *
	 * @param result
	 * @param x
	 */
	public void subInto(final WB_MutableCoord result, final double... x);

	/**
	 * Subtract coordinate values and store in mutable coordinate.
	 *
	 * @param result
	 * @param p
	 */
	public void subInto(final WB_MutableCoord result, final WB_Coord p);

	/**
	 * Multiply by factor and store in mutable coordinate.
	 *
	 * @param result
	 * @param f
	 */
	public void mulInto(final WB_MutableCoord result, final double f);

	/**
	 * Divide by factor and store in mutable coordinate.
	 *
	 * @param result
	 * @param f
	 */
	public void divInto(final WB_MutableCoord result, final double f);

	/**
	 * Add multiple of coordinate values and store in mutable coordinate.
	 *
	 * @param result
	 * @param f
	 *            multiplier
	 * @param x
	 */
	public void addMulInto(final WB_MutableCoord result, final double f, final double... x);

	/**
	 * Add multiple of coordinate values and store in mutable coordinate.
	 *
	 * @param result
	 * @param f
	 * @param p
	 */
	public void addMulInto(final WB_MutableCoord result, final double f, final WB_Coord p);

	/**
	 * Multiply this coordinate by factor f, add other coordinate values
	 * multiplied by g and store result in mutable coordinate.
	 *
	 * @param result
	 * @param f
	 * @param g
	 * @param x
	 */
	public void mulAddMulInto(final WB_MutableCoord result, final double f, final double g, final double... x);

	/**
	 * Multiply this coordinate by factor f, add other coordinate values
	 * multiplied by g and store result in mutable coordinate.
	 *
	 * @param result
	 * @param f
	 * @param g
	 * @param p
	 */
	public void mulAddMulInto(final WB_MutableCoord result, final double f, final double g, final WB_Coord p);

	/**
	 * dot product.
	 *
	 * @param p
	 * @return dot product
	 */
	public double dot(final WB_Coord p);

	/**
	 * Absolute value of dot product.
	 *
	 * @param p
	 * @return absolute value of dot product
	 */
	public double absDot(final WB_Coord p);

	/**
	 * Add 3D coordinate values. Leave w-value unchanged.
	 *
	 * @param p
	 * @return new WB_Coord
	 */
	public WB_Coord add3D(final WB_Coord p);

	/**
	 * Add 3D coordinate values. Leave w-value unchanged.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public WB_Coord add3D(final double x, final double y, final double z);

	/**
	 * Subtract 3D coordinate values. Leave w-value unchanged.
	 *
	 * @param p
	 * @return new WB_Coord
	 */
	public WB_Coord sub3D(final WB_Coord p);

	/**
	 * Subtract 3D coordinate values. Leave w-value unchanged.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return new WB_Coord
	 */
	public WB_Coord sub3D(final double x, final double y, final double z);

	/**
	 * Multiply by factor. Leave w-value unchanged.
	 *
	 * @param f
	 * @return new WB_Coord
	 */
	public WB_Coord mul3D(final double f);

	/**
	 * Divide by factor. Leave w-value unchanged.
	 *
	 * @param f
	 * @return new WB_Coord
	 */
	public WB_Coord div3D(final double f);

	/**
	 * Add multiple of 3D coordinate values. Leave w-value unchanged.
	 *
	 * @param f
	 * @param p
	 * @return new WB_Coord
	 */
	public WB_Coord addMul3D(final double f, final WB_Coord p);

	/**
	 * Add multiple of 3D coordinate values. Leave w-value unchanged.
	 *
	 * @param f
	 *            multiplier
	 * @param x
	 * @param y
	 * @param z
	 * @return new WB_Coord
	 */
	public WB_Coord addMul3D(final double f, final double x, final double y, final double z);

	/**
	 * Multiply this coordinate by factor f and add other coordinate values
	 * multiplied by g. Leave w-value unchanged.
	 *
	 * @param f
	 * @param g
	 * @param p
	 * @return new WB_Coord
	 */
	public WB_Coord mulAddMul3D(final double f, final double g, final WB_Coord p);

	/**
	 * Multiply this coordinate by factor f and add other coordinate values
	 * multiplied by g. Leave w-value unchanged.
	 *
	 * @param f
	 * @param g
	 * @param x
	 * @param y
	 * @param z
	 * @return new WB_Coord
	 */
	public WB_Coord mulAddMul3D(final double f, final double g, final double x, final double y, final double z);

	/**
	 * Add 3D coordinate values and store in mutable coordinate. Leave w-value
	 * unchanged.
	 *
	 * @param result
	 * @param p
	 */
	public void add3DInto(final WB_MutableCoord result, final WB_Coord p);

	/**
	 * Add 3D coordinate values and store in mutable coordinate. Leave w-value
	 * unchanged.
	 *
	 * @param result
	 * @param x
	 * @param y
	 * @param z
	 */
	public void add3DInto(final WB_MutableCoord result, final double x, final double y, final double z);

	/**
	 * Subtract 3D coordinate values and store in mutable coordinate. Leave
	 * w-value unchanged.
	 *
	 * @param result
	 * @param p
	 */
	public void sub3DInto(final WB_MutableCoord result, final WB_Coord p);

	/**
	 * Subtract 3D coordinate values and store in mutable coordinate. Leave
	 * w-value unchanged.
	 *
	 * @param result
	 * @param x
	 * @param y
	 * @param z
	 */
	public void sub3DInto(final WB_MutableCoord result, final double x, final double y, final double z);

	/**
	 * Multiply by factor and store in mutable coordinate. Leave w-value
	 * unchanged.
	 *
	 * @param result
	 * @param f
	 */
	public void mul3DInto(final WB_MutableCoord result, final double f);

	/**
	 * Divide by factor and store in mutable coordinate. Leave w-value
	 * unchanged.
	 *
	 * @param result
	 * @param f
	 */
	public void div3DInto(final WB_MutableCoord result, final double f);

	/**
	 * Add multiple of 3D coordinate values and store in mutable coordinate.
	 * Leave w-value unchanged.
	 *
	 * @param result
	 * @param f
	 * @param p
	 */
	public void addMul3DInto(final WB_MutableCoord result, final double f, final WB_Coord p);

	/**
	 * Add multiple of 3D coordinate values and store in mutable coordinate.
	 * Leave w-value unchanged.
	 *
	 * @param result
	 * @param f
	 *            multiplier
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addMul3DInto(final WB_MutableCoord result, final double f, final double x, final double y,
			final double z);

	/**
	 * Multiply this coordinate by factor f, add other coordinate values
	 * multiplied by g and store result in mutable coordinate. Leave w-value
	 * unchanged.
	 *
	 * @param result
	 * @param f
	 * @param g
	 * @param p
	 */
	public void mulAddMul3DInto(final WB_MutableCoord result, final double f, final double g, final WB_Coord p);

	/**
	 * Multiply this coordinate by factor f, add other coordinate values
	 * multiplied by g and store result in mutable coordinate. Leave w-value
	 * unchanged.
	 *
	 * @param result
	 * @param f
	 * @param g
	 * @param x
	 * @param y
	 * @param z
	 *
	 */
	public void mulAddMul3DInto(final WB_MutableCoord result, final double f, final double g, final double x,
			final double y, final double z);

}
