/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 * 
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 * 
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.geom;

public class WB_CoordinateSystem2D {

	private WB_CoordinateSystem2D _parent;

	/**
	 *
	 *
	 * @return
	 */
	protected final static WB_CoordinateSystem2D WORLD() {
		return new WB_CoordinateSystem2D(true);
	}

	private WB_Point _origin;

	private WB_Vector _X;

	private WB_Vector _Y;

	private boolean _isWorld;

	/**
	 *
	 * @param origin
	 * @param x
	 * @param y
	 * @param parent
	 */
	protected WB_CoordinateSystem2D(final WB_Coord origin, final WB_Coord x, final WB_Coord y,
			final WB_CoordinateSystem2D parent) {
		_origin = new WB_Point(origin);
		_X = new WB_Vector(x);
		_Y = new WB_Vector(y);
		_parent = parent;
		_isWorld = _parent == null;
	}

	/**
	 *
	 *
	 * @param world
	 */
	protected WB_CoordinateSystem2D(final boolean world) {
		_origin = new WB_Point(WB_Point.ZERO());
		_X = new WB_Vector(WB_Vector.X());
		_Y = new WB_Vector(WB_Vector.Y());
		_isWorld = world;
		_parent = world ? null : WORLD();
	}

	/**
	 *
	 */
	public WB_CoordinateSystem2D() {
		this(false);
	}

	/**
	 *
	 *
	 * @param parent
	 */
	public WB_CoordinateSystem2D(final WB_CoordinateSystem2D parent) {
		_origin = new WB_Point(WB_Point.ZERO());
		_X = new WB_Vector(WB_Vector.X());
		_Y = new WB_Vector(WB_Vector.Y());
		_parent = parent;
		_isWorld = _parent == null;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_CoordinateSystem2D get() {
		return new WB_CoordinateSystem2D(_origin, _X, _Y, _parent);
	}

	/**
	 *
	 *
	 * @param origin
	 * @param x
	 * @param y
	 */
	protected void set(final WB_Coord origin, final WB_Coord x, final WB_Coord y) {
		_origin = new WB_Point(origin);
		_X = new WB_Vector(x);
		_Y = new WB_Vector(y);

	}

	/**
	 *
	 *
	 * @param origin
	 * @param x
	 * @param y
	 * @param CS
	 */
	protected void set(final WB_Coord origin, final WB_Coord x, final WB_Coord y, final WB_CoordinateSystem2D CS) {
		_origin = new WB_Point(origin);
		_X = new WB_Vector(x);
		_Y = new WB_Vector(y);
		_parent = CS;
	}

	/**
	 *
	 *
	 * @param parent
	 * @return
	 */
	public WB_CoordinateSystem2D setParent(final WB_CoordinateSystem2D parent) {
		_parent = parent;
		_isWorld = _parent == null;
		return this;
	}

	/**
	 *
	 *
	 * @param o
	 * @return
	 */
	public WB_CoordinateSystem2D setOrigin(final WB_Point o) {
		_origin.set(o);
		return this;
	}

	/**
	 *
	 *
	 * @param ox
	 * @param oy
	 *
	 * @return
	 */
	public WB_CoordinateSystem2D setOrigin(final double ox, final double oy) {
		_origin.set(ox, oy);
		return this;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Vector getX() {
		return _X.copy();
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Vector getY() {
		return _Y.copy();
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Point getOrigin() {
		return _origin.copy();
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_CoordinateSystem2D getParent() {
		return _parent;
	}

	/**
	 *
	 *
	 * @return
	 */
	public boolean isWorld() {
		return _isWorld;
	}

	/**
	 *
	 *
	 * @param X
	 * @return
	 */
	public WB_CoordinateSystem2D setX(final WB_Coord X) {
		_X = new WB_Vector(X);
		_X.normalizeSelf();
		_Y = WB_Vector.getOrthoNormal2D(_X);
		return this;
	}

	/**
	 *
	 *
	 * @param Y
	 * @return
	 */
	public WB_CoordinateSystem2D setY(final WB_Coord Y) {
		_Y = new WB_Vector(Y);
		_Y.normalizeSelf();
		_X = WB_Vector.getOrthoNormal2D(_Y).mulSelf(-1);
		return this;
	}

	/**
	 *
	 * @param xx
	 * @param xy
	 * @return
	 */
	public WB_CoordinateSystem2D setX(final double xx, final double xy) {
		_X = new WB_Vector(xx, xy);
		_X.normalizeSelf();
		_Y = WB_Vector.getOrthoNormal2D(_X);
		return this;
	}

	/**
	 *
	 * @param yx
	 * @param yy
	 * @return
	 */
	public WB_CoordinateSystem2D setY(final double yx, final double yy) {
		_Y = new WB_Vector(yx, yy);
		_Y.normalizeSelf();
		_X = WB_Vector.getOrthoNormal2D(_Y).mulSelf(-1);
		return this;
	}

	/**
	 *
	 *
	 * @param a
	 * @return
	 */
	public WB_CoordinateSystem2D rotateAboutOrigin(final double a) {
		_X.rotateAboutAxisSelf(a, _origin, new WB_Vector(0, 0, 1));
		_Y.rotateAboutAxisSelf(a, _origin, new WB_Vector(0, 0, 1));
		return this;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Transform2D getTransformFromParent() {
		final WB_Transform2D result = new WB_Transform2D();
		result.addFromParentToCS2D(this);
		return result;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Transform2D getTransformToParent() {
		final WB_Transform2D result = new WB_Transform2D();
		result.addFromCSToParent2D(this);
		return result;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Transform2D getTransformFromWorld() {
		final WB_Transform2D result = new WB_Transform2D();
		result.addFromWorldToCS2D(this);
		return result;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Transform2D getTransformToWorld() {
		final WB_Transform2D result = new WB_Transform2D();
		result.addFromCSToWorld2D(this);
		return result;
	}

	/**
	 *
	 *
	 * @param CS
	 * @return
	 */
	public WB_Transform2D getTransformFrom(final WB_CoordinateSystem2D CS) {
		final WB_Transform2D result = new WB_Transform2D();
		result.addFromCSToCS2D(CS, this);
		return result;
	}

	/**
	 *
	 *
	 * @param CS
	 * @return
	 */
	public WB_Transform2D getTransformTo(final WB_CoordinateSystem2D CS) {
		final WB_Transform2D result = new WB_Transform2D();
		result.addFromCSToCS2D(this, CS);
		return result;
	}

	/**
	 *
	 */
	public void flip() {
		_X.mulSelf(-1);
		_Y.mulSelf(-1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WB_CoordinateSystem2D: origin: " + _origin + " [X=" + _X + ", Y=" + _Y + "]";
	}

	/**
	 *
	 *
	 * @param T
	 * @return
	 */
	WB_CoordinateSystem2D apply(final WB_Transform2D T) {
		return new WB_CoordinateSystem2D(T.applyAsPoint2D(_origin), T.applyAsVector2D(_X), T.applyAsVector2D(_Y),
				_parent == null ? WORLD() : _parent);
	}

	/**
	 *
	 *
	 * @param T
	 * @param parent
	 * @return
	 */
	WB_CoordinateSystem2D apply(final WB_Transform2D T, final WB_CoordinateSystem2D parent) {
		return new WB_CoordinateSystem2D(T.applyAsPoint2D(_origin), T.applyAsVector2D(_X), T.applyAsVector2D(_Y),
				_parent);
	}
}
