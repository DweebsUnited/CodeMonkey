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
 * Interface for implementing mutable transformation operations on 3D
 * coordinates.
 *
 * All of the operators defined in the interface change the calling object. All
 * operators use the label "Self", such as "scaleSelf" to indicate this.
 *
 * @author Frederik Vanhoutte
 *
 */
public interface WB_MutableCoordinateTransform3D extends WB_CoordinateTransform3D {
	/**
	 *
	 *
	 * @param T
	 * @return this
	 */
	public WB_Coord applySelf(final WB_Transform T);

	/**
	 *
	 *
	 * @param T
	 * @return this
	 */
	public WB_Coord applyAsNormalSelf(final WB_Transform T);

	/**
	 *
	 *
	 * @param T
	 * @return this
	 */
	public WB_Coord applyAsPointSelf(final WB_Transform T);

	/**
	 *
	 *
	 * @param T
	 * @return this
	 */
	public WB_Coord applyAsVectorSelf(final WB_Transform T);

	/**
	 *
	 *
	 * @param angle
	 * @param p1x
	 * @param p1y
	 * @param p1z
	 * @param p2x
	 * @param p2y
	 * @param p2z
	 * @return this
	 */
	public WB_Coord rotateAboutAxis2PSelf(final double angle, final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z);

	/**
	 *
	 *
	 * @param angle
	 * @param p1
	 * @param p2
	 * @return this
	 */
	public WB_Coord rotateAboutAxis2PSelf(final double angle, final WB_Coord p1, final WB_Coord p2);

	/**
	 *
	 *
	 * @param angle
	 * @param p
	 * @param a
	 * @return this
	 */
	public WB_Coord rotateAboutAxisSelf(final double angle, final WB_Coord p, final WB_Coord a);

	/**
	 *
	 *
	 * @param angle
	 * @param px
	 * @param py
	 * @param pz
	 * @param ax
	 * @param ay
	 * @param az
	 * @return this
	 */
	public WB_Coord rotateAboutAxisSelf(final double angle, final double px, final double py, final double pz,
			final double ax, final double ay, final double az);

	/**
	 *
	 *
	 * @param f
	 * @return this
	 */
	public WB_Coord scaleSelf(final double f);

	/**
	 *
	 *
	 * @param fx
	 * @param fy
	 * @param fz
	 * @return this
	 */
	public WB_Coord scaleSelf(final double fx, final double fy, final double fz);

	/**
	 * 3D translate.
	 *
	 * @param px
	 * @param py
	 * @param pz
	 * @return this
	 */
	public WB_Coord translateSelf(final double px, final double py, double pz);

	/**
	 * 3D translate.
	 *
	 * @param p
	 * @return this
	 */
	public WB_Coord translateSelf(final WB_Coord p);

	/**
	 *
	 *
	 * @param angle
	 * @param x
	 * @param y
	 * @param z
	 *
	 * @return this
	 */
	public WB_Coord rotateAboutOriginSelf(final double angle, final double x, final double y, final double z);

	/**
	 *
	 *
	 * @param angle
	 * @param v
	 * @return this
	 */
	public WB_Coord rotateAboutOriginSelf(final double angle, final WB_Coord v);

	/**
	 *
	 *
	 * @param T
	 * @return this
	 */
	public WB_Coord apply2DSelf(final WB_Transform2D T);

	/**
	 *
	 *
	 * @param T
	 * @return this
	 */
	public WB_Coord applyAsNormal2DSelf(final WB_Transform2D T);

	/**
	 *
	 *
	 * @param T
	 * @return this
	 */
	public WB_Coord applyAsPoint2DSelf(final WB_Transform2D T);

	/**
	 *
	 *
	 * @param T
	 * @return this
	 */
	public WB_Coord applyAsVector2DSelf(final WB_Transform2D T);

	/**
	 * 2D translate.
	 *
	 * @param px
	 * @param py
	 * @return this
	 */
	public WB_Coord translate2DSelf(final double px, final double py);

	/**
	 * 2D translate.
	 *
	 * @param p
	 * @return this
	 */
	public WB_Coord translate2DSelf(final WB_Coord p);

	/**
	 *
	 * @param angle
	 * @param p
	 * @return
	 */
	public WB_Coord rotateAboutPoint2DSelf(final double angle, final WB_Coord p);

	/**
	 *
	 *
	 * @param angle
	 * @param px
	 * @param py
	 * @return this
	 */
	public WB_Coord rotateAboutPoint2DSelf(final double angle, final double px, final double py);

	/**
	 *
	 *
	 * @param f
	 * @return this
	 */
	public WB_Coord scale2DSelf(final double f);

	/**
	 *
	 *
	 * @param fx
	 * @param fy
	 * @return this
	 */
	public WB_Coord scale2DSelf(final double fx, final double fy);

	/**
	 *
	 *
	 * @param angle
	 *
	 * @return this
	 */
	public WB_Coord rotateAboutOrigin2DSelf(final double angle);

}
