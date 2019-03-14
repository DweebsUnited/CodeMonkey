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
 * Interface for implementing non-mutable transformation operations on 2D
 * coordinates.If the operations should change the calling object use
 * {@link wblut.geom.WB_MutableCoordinateTransform2D}.
 *
 * None of the operators change the calling object. Unlabelled operators, such
 * as "scale2D",create a new WB_Coord. Operators with the label "Into", such as
 * "scale2DInto" store the result into a WB_MutableCoord passed as additional
 * parameter.
 *
 * @author Frederik Vanhoutte
 *
 */
public interface WB_CoordinateTransform2D {

	/**
	 * Apply WB_Transform2D. Mode (point, vector or normal) is decided by
	 * implementation.
	 *
	 * @param T
	 * @return new WB_Coord
	 */
	public WB_Coord apply2D(final WB_Transform2D T);

	/**
	 * Apply WB_Transform2D. Mode (point, vector or normal) is decided by
	 * implementation.
	 *
	 * @param result
	 * @param T
	 */
	public void apply2DInto(WB_MutableCoord result, final WB_Transform2D T);

	/**
	 * Apply WB_Transform2D as point.
	 *
	 * @param T
	 * @return new WB_Coord
	 */
	public WB_Coord applyAsPoint2D(final WB_Transform2D T);

	/**
	 * Apply WB_Transform2D as point.
	 *
	 * @param result
	 * @param T
	 */
	public void applyAsPoint2DInto(final WB_MutableCoord result, final WB_Transform2D T);

	/**
	 * Apply WB_Transform2D as vector.
	 *
	 * @param T
	 * @return new WB_Coord
	 */
	public WB_Coord applyAsVector2D(final WB_Transform2D T);

	/**
	 * Apply WB_Transform2D as vector.
	 *
	 * @param result
	 * @param T
	 */
	public void applyAsVector2DInto(final WB_MutableCoord result, final WB_Transform2D T);

	/**
	 * Apply WB_Transform2D as normal.
	 *
	 * @param T
	 * @return new WB_Coord
	 */
	public WB_Coord applyAsNormal2D(final WB_Transform2D T);

	/**
	 * Apply WB_Transform2D as normal.
	 *
	 * @param result
	 * @param T
	 */
	public void applyAsNormal2DInto(final WB_MutableCoord result, final WB_Transform2D T);

	/**
	 * 2D translate.
	 *
	 * @param px
	 * @param py
	 * @return new WB_Coord
	 */
	public WB_Coord translate2D(final double px, final double py);

	/**
	 * 2D translate.
	 *
	 * @param result
	 * @param px
	 * @param py
	 */
	public void translate2DInto(final WB_MutableCoord result, final double px, final double py);

	/**
	 * 2D translate.
	 *
	 * @param p
	 * @return new WB_Coord
	 */
	public WB_Coord translate2D(final WB_Coord p);

	/**
	 * 2D translate.
	 *
	 * @param result
	 * @param p
	 */
	public void translate2DInto(final WB_MutableCoord result, final WB_Coord p);

	/**
	 * Rotate around point.
	 *
	 * @param angle
	 * @param px
	 * @param py
	 * @return new WB_Coord
	 */
	public WB_Coord rotateAboutPoint2D(final double angle, final double px, final double py);

	/**
	 * Rotate around point.
	 *
	 * @param result
	 * @param angle
	 * @param px
	 * @param py
	 */
	public void rotateAboutPoint2DInto(WB_MutableCoord result, final double angle, final double px, final double py);

	/**
	 * Rotate around point.
	 *
	 * @param angle
	 * @param p
	 *
	 * @return new WB_Coord
	 */
	public WB_Coord rotateAboutPoint2D(final double angle, final WB_Coord p);

	/**
	 * Rotate around point.
	 *
	 * @param result
	 * @param angle
	 * @param p
	 *
	 */
	public void rotateAboutPoint2DInto(WB_MutableCoord result, final double angle, final WB_Coord p);

	/**
	 * Rotate around origin.
	 *
	 * @param angle
	 *
	 * @return new WB_Coord
	 */
	public WB_Coord rotateAboutOrigin2D(final double angle);

	/**
	 * Rotate around origin.
	 *
	 * @param result
	 * @param angle
	 */
	public void rotateAboutOrigin2DInto(WB_MutableCoord result, final double angle);

	/**
	 * Non-uniform scale.
	 *
	 * @param fx
	 * @param fy
	 * @return new WB_Coord
	 */
	public WB_Coord scale2D(final double fx, final double fy);

	/**
	 * Non-uniform scale.
	 *
	 * @param result
	 * @param fx
	 * @param fy
	 */
	public void scale2DInto(WB_MutableCoord result, final double fx, final double fy);

	/**
	 * Uniform scale.
	 *
	 * @param f
	 * @return new WB_Coord
	 */
	public WB_Coord scale2D(final double f);

	/**
	 * Uniform scale.
	 *
	 * @param result
	 * @param f
	 */
	public void scale2DInto(WB_MutableCoord result, final double f);

}
