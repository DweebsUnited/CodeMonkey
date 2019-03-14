/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 * 
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 * 
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.geom;

import wblut.math.WB_Epsilon;
import wblut.math.WB_HashCode;

/**
 *
 */
public class WB_Coordinate2D implements WB_Coord {
	/** Coordinates. */
	double x, y;

	/**
	 *
	 */
	public WB_Coordinate2D() {
		x = y;
	}

	/**
	 *
	 *
	 * @param x
	 * @param y
	 */
	public WB_Coordinate2D(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 *
	 *
	 * @param x
	 */
	public WB_Coordinate2D(final double[] x) {
		if (x.length != 2) {
			throw new IllegalArgumentException("Array needs to be of length 2.");
		}
		this.x = x[0];
		this.y = x[1];

	}

	/**
	 *
	 * @param p
	 */
	public WB_Coordinate2D(final WB_Coord p) {
		x = p.xd();
		y = p.yd();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coord#getd(int)
	 */
	@Override
	public double getd(final int i) {
		if (i == 0) {
			return x;
		}
		if (i == 1) {
			return y;
		}
		if (i == 2) {
			return 0;
		}
		if (i == 3) {
			return 0;
		}
		return Double.NaN;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coord#getf(int)
	 */
	@Override
	public float getf(final int i) {
		if (i == 0) {
			return (float) x;
		}
		if (i == 1) {
			return (float) y;
		}
		if (i == 2) {
			return 0;
		}
		if (i == 3) {
			return 0;
		}
		return Float.NaN;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coord#xd()
	 */
	@Override
	public double xd() {
		return x;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coord#yd()
	 */
	@Override
	public double yd() {
		return y;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coord#zd()
	 */
	@Override
	public double zd() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coord#wd()
	 */
	@Override
	public double wd() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coord#xf()
	 */
	@Override
	public float xf() {
		return (float) x;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coord#yf()
	 */
	@Override
	public float yf() {
		return (float) y;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coord#zf()
	 */
	@Override
	public float zf() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coord#wf()
	 */
	@Override
	public float wf() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final WB_Coord p) {
		int cmp = Double.compare(xd(), p.xd());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Double.compare(yd(), p.yd());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Double.compare(zd(), p.zd());
		if (cmp != 0) {
			return cmp;
		}
		return Double.compare(wd(), p.wd());
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public int compareToY1st(final WB_Coord p) {
		int cmp = Double.compare(yd(), p.yd());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Double.compare(xd(), p.xd());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Double.compare(zd(), p.zd());
		if (cmp != 0) {
			return cmp;
		}
		return Double.compare(wd(), p.wd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (!(o instanceof WB_Coord)) {
			return false;
		}
		final WB_Coord p = (WB_Coord) o;
		if (!WB_Epsilon.isEqualAbs(xd(), p.xd())) {
			return false;
		}
		if (!WB_Epsilon.isEqualAbs(yd(), p.yd())) {
			return false;
		}
		if (!WB_Epsilon.isEqualAbs(zd(), p.zd())) {
			return false;
		}
		if (!WB_Epsilon.isEqualAbs(wd(), p.wd())) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return WB_HashCode.calculateHashCode(xd(), yd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WB_SimpleCoordinate2D [x=" + xd() + ", y=" + yd() + "]";
	}

}
