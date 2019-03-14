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
 * Interface for implementing non-mutable transformation operations on 3D
 * coordinates.If the operations should change the calling object use
 * {@link wblut.geom.WB_MutableCoordinateTransform3D}.
 *
 * None of the operators change the calling object. Unlabelled operators, such
 * as "scale",create a new WB_Coord. Operators with the label "Into", such as
 * "scaleInto" store the result into a WB_MutableCoord passed as additional
 * parameter.
 *
 * @author Frederik Vanhoutte
 *
 */

public interface WB_CoordinateTransform3D extends WB_CoordinateTransform2D {

	/**
	 * Apply WB_Transform. Mode (point, vector or normal) is decided by
	 * implementation.
	 *
	 * @param T
	 * @return new WB_Coord
	 */
	public WB_Coord apply(final WB_Transform T);

	/**
	 * Apply WB_Transform. Mode (point, vector or normal) is decided by
	 * implementation.
	 *
	 * @param result
	 * @param T
	 */
	public void applyInto(WB_MutableCoord result, final WB_Transform T);

	/**
	 * Apply WB_Transform as point.
	 *
	 * @param T
	 * @return new WB_Coord
	 */
	public WB_Coord applyAsPoint(final WB_Transform T);

	/**
	 * Apply WB_Transform as point.
	 *
	 * @param result
	 * @param T
	 */
	public void applyAsPointInto(final WB_MutableCoord result, final WB_Transform T);

	/**
	 * Apply WB_Transform as vector.
	 *
	 * @param T
	 * @return new WB_Coord
	 */
	public WB_Coord applyAsVector(final WB_Transform T);

	/**
	 * Apply WB_Transform as vector.
	 *
	 * @param result
	 * @param T
	 */
	public void applyAsVectorInto(final WB_MutableCoord result, final WB_Transform T);

	/**
	 * Apply WB_Transform as normal.
	 *
	 * @param T
	 * @return new WB_Coord
	 */
	public WB_Coord applyAsNormal(final WB_Transform T);

	/**
	 * Apply WB_Transform as normal.
	 *
	 * @param result
	 * @param T
	 */
	public void applyAsNormalInto(final WB_MutableCoord result, final WB_Transform T);

	/**
	 * 3D translate.
	 *
	 * @param px
	 * @param py
	 * @param pz
	 * @return new WB_Coord
	 */
	public WB_Coord translate(final double px, final double py, double pz);

	/**
	 * 3D translate.
	 *
	 * @param result
	 * @param px
	 * @param py
	 * @param pz
	 * @return new WB_Coord
	 */
	public WB_Coord translateInto(final WB_MutableCoord result, final double px, final double py, double pz);

	/**
	 * 3D translate.
	 *
	 * @param p
	 * @return new WB_Coord
	 */
	public WB_Coord translate(final WB_Coord p);

	/**
	 * 3D translate.
	 *
	 * @param result
	 * @param p
	 * @return new WB_Coord
	 */
	public WB_Coord translateInto(final WB_MutableCoord result, final WB_Coord p);

	/**
	 * Rotate around axis defined by two points.
	 *
	 * @param angle
	 * @param p1x
	 * @param p1y
	 * @param p1z
	 * @param p2x
	 * @param p2y
	 * @param p2z
	 * @return new WB_Coord
	 */
	public WB_Coord rotateAboutAxis2P(final double angle, final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z);

	/**
	 * Rotate around axis defined by two points.
	 *
	 * @param result
	 * @param angle
	 * @param p1x
	 * @param p1y
	 * @param p1z
	 * @param p2x
	 * @param p2y
	 * @param p2z
	 */
	public void rotateAboutAxis2PInto(WB_MutableCoord result, final double angle, final double p1x, final double p1y,
			final double p1z, final double p2x, final double p2y, final double p2z);

	/**
	 * Rotate around axis defined by two points.
	 *
	 * @param angle
	 * @param p1
	 * @param p2
	 * @return new WB_Coord
	 */
	public WB_Coord rotateAboutAxis2P(final double angle, final WB_Coord p1, final WB_Coord p2);

	/**
	 * Rotate around axis defined by two points.
	 *
	 * @param result
	 * @param angle
	 * @param p1
	 * @param p2
	 */
	public void rotateAboutAxis2PInto(WB_MutableCoord result, final double angle, final WB_Coord p1, final WB_Coord p2);

	/**
	 * Rotate around axis defined by point and direction.
	 *
	 * @param angle
	 * @param px
	 * @param py
	 * @param pz
	 * @param ax
	 * @param ay
	 * @param az
	 * @return new WB_Coord
	 */
	public WB_Coord rotateAboutAxis(final double angle, final double px, final double py, final double pz,
			final double ax, final double ay, final double az);

	/**
	 * Rotate around axis defined by point and direction.
	 *
	 * @param result
	 * @param angle
	 * @param px
	 * @param py
	 * @param pz
	 * @param ax
	 * @param ay
	 * @param az
	 */
	public void rotateAboutAxisInto(WB_MutableCoord result, final double angle, final double px, final double py,
			final double pz, final double ax, final double ay, final double az);

	/**
	 * Rotate around axis defined by point and direction.
	 *
	 * @param angle
	 * @param p
	 * @param a
	 * @return new WB_Coord
	 */
	public WB_Coord rotateAboutAxis(final double angle, final WB_Coord p, final WB_Coord a);

	/**
	 * Rotate around axis defined by point and direction.
	 *
	 * @param result
	 * @param angle
	 * @param p
	 * @param a
	 */
	public void rotateAboutAxisInto(WB_MutableCoord result, final double angle, final WB_Coord p, final WB_Coord a);

	/**
	 * Rotate around axis defined by origin and direction.
	 *
	 * @param angle
	 * @param x
	 * @param y
	 * @param z
	 *
	 * @return new WB_Coord
	 */
	public WB_Coord rotateAboutOrigin(final double angle, final double x, final double y, final double z);

	/**
	 * Rotate around axis defined by origin and direction.
	 *
	 * @param result
	 * @param angle
	 * @param x
	 * @param y
	 * @param z
	 */
	public void rotateAboutOriginInto(WB_MutableCoord result, final double angle, final double x, final double y,
			final double z);

	/**
	 * Rotate around axis defined by origin and direction.
	 *
	 *
	 * @param angle
	 * @param v
	 * @return new WB_Coord
	 */
	public WB_Coord rotateAboutOrigin(final double angle, final WB_Coord v);

	/**
	 * Rotate around axis defined by origin and direction.
	 *
	 * @param result
	 * @param angle
	 * @param v
	 */
	public void rotateAboutOriginInto(WB_MutableCoord result, final double angle, final WB_Coord v);

	/**
	 * Non-uniform scale.
	 *
	 * @param fx
	 * @param fy
	 * @param fz
	 * @return
	 */
	public WB_Coord scale(final double fx, final double fy, final double fz);

	/**
	 * Non-uniform scale.
	 *
	 * @param result
	 * @param fx
	 * @param fy
	 * @param fz
	 */
	public void scaleInto(WB_MutableCoord result, final double fx, final double fy, final double fz);

	/**
	 * Uniform scale.
	 *
	 * @param f
	 * @return
	 */
	public WB_Coord scale(final double f);

	/**
	 * Uniform scale.
	 *
	 * @param result
	 * @param f
	 */
	public void scaleInto(WB_MutableCoord result, final double f);

}
