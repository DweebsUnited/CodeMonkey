/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 *
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 *
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.geom;

import java.security.InvalidParameterException;

import wblut.geom.WB_GeodesicI.WB_GreatCircleIntersection;
import wblut.math.WB_Epsilon;

public class WB_Geodesic implements WB_MeshCreator {
	private static WB_GeometryFactory gf = new WB_GeometryFactory();

	public static enum Type {
		TETRAHEDRON(0), OCTAHEDRON(1), CUBE(2), DODECAHEDRON(3), ICOSAHEDRON(4);

		Type(final int index) {
			this.index = index;
		}

		private final int index;

		public int getIndex() {
			return index;
		}
	};

	private WB_Mesh mesh;

	private final double radius;

	private final Type type;

	private final int b;

	private final int c;

	/**
	 *
	 *
	 * @param radius
	 * @param b
	 * @param c
	 */
	public WB_Geodesic(final double radius, final int b, final int c) {
		this(radius, b, c, Type.ICOSAHEDRON);
	}

	/**
	 *
	 *
	 * @param radius
	 * @param b
	 * @param c
	 * @param type
	 */
	public WB_Geodesic(final double radius, final int b, final int c, final Type type) {
		if (b + c == 0 || b < 0 || c < 0) {
			throw new InvalidParameterException("Invalid values for b and c.");
		}
		this.b = b;
		this.c = c;
		this.type = type;
		this.radius = radius;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MeshCreator#getMesh()
	 */
	@Override
	public WB_Mesh create() {
		createMesh();
		return mesh;
	}

	/**
	 *
	 */
	private void createMesh() {
		if (b == c) {
			final WB_GeodesicII geo = new WB_GeodesicII(radius, b + c, type);
			mesh = geo.getMesh();
		} else if (b == 0 || c == 0) {
			if (type == Type.CUBE || type == Type.DODECAHEDRON) {
				throw new InvalidParameterException("Invalid type for this class of geodesic.");
			}
			final WB_GeodesicI geo = new WB_GeodesicI(radius, b + c, type, 1);
			mesh = geo.getMesh();
		} else {
			if (type == Type.CUBE || type == Type.DODECAHEDRON) {
				throw new InvalidParameterException("Invalid type for this class of geodesic.");
			}

			final WB_GeodesicIII geo = new WB_GeodesicIII(radius, b, c, type);
			mesh = geo.getMesh();
		}
	}

	/**
	 *
	 *
	 * @param v1
	 * @param v2
	 * @param v3
	 * @param v4
	 * @return
	 */
	public static WB_GreatCircleIntersection getGreatCircleIntersection(final WB_Coord v1, final WB_Coord v2,
			final WB_Coord v3, final WB_Coord v4) {
		final WB_Point origin = gf.createPoint(0, 0, 0);
		final WB_Vector r1 = vnor(v1, origin, v2);
		final WB_Vector r2 = vnor(v3, origin, v4);
		final WB_Vector r3 = vnor(r1, origin, r2);
		if (WB_Epsilon.isZeroSq(r3.getSqLength())) {
			return null;
		}
		r3.normalizeSelf();
		final WB_Point p0 = gf.createPoint(r3);
		final WB_Point p1 = p0.mul(-1);
		final double dihedral = Math.acos(Math.abs(r1.dot(r2)) / (r1.getLength() * r2.getLength()));
		p0.addSelf(origin);
		p1.addSelf(origin);
		return new WB_GreatCircleIntersection(p0.coords(), p1.coords(), dihedral);
	}

	/**
	 *
	 *
	 * @param v1
	 * @param v2
	 * @param f
	 * @return
	 */
	public static double[] getPointOnGreatCircle(final WB_Coord v1, final WB_Coord v2, final double f) {
		final WB_Point origin = gf.createPoint(0, 0, 0);
		final double angle = Math.acos(vcos(v1, origin, v2));
		final double isa = 1.0 / Math.sin(angle);
		final double alpha = Math.sin((1.0 - f) * angle) * isa;
		final double beta = Math.sin(f * angle) * isa;
		final WB_Point r0 = new WB_Point(v1).mul(alpha);
		final WB_Point r1 = new WB_Point(v2).mul(beta);
		return r0.add(r1).coords();
	}

	/**
	 *
	 *
	 * @param v1
	 * @param v2
	 * @param v3
	 * @return
	 */
	private static WB_Vector vnor(final WB_Coord v1, final WB_Coord v2, final WB_Coord v3) {
		final WB_Vector r0 = new WB_Vector(v2, v1);
		final WB_Vector r1 = new WB_Vector(v2, v3);
		return r1.cross(r0);
	}

	/**
	 *
	 *
	 * @param v1
	 * @param v2
	 * @param v3
	 * @return
	 */
	private static double vcos(final WB_Coord v1, final WB_Coord v2, final WB_Coord v3) {
		final WB_Vector r0 = new WB_Vector(v2, v1);
		final WB_Vector r1 = new WB_Vector(v2, v3);
		return r0.dot(r1) / (r0.getLength() * r1.getLength());
	}

}
