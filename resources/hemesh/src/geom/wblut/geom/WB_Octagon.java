/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 * 
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 * 
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */
package wblut.geom;

public class WB_Octagon {
	private WB_GeometryFactory geometryfactory = new WB_GeometryFactory();
	public WB_Point p1;
	public WB_Point p2;
	public WB_Point p3;
	public WB_Point p4;
	public WB_Point p5;
	public WB_Point p6;
	public WB_Point p7;
	public WB_Point p8;

	public WB_Octagon(final WB_Coord p1, final WB_Coord p2, final WB_Coord p3, final WB_Coord p4, final WB_Coord p5,
			final WB_Coord p6, final WB_Coord p7, final WB_Coord p8) {
		this.p1 = geometryfactory.createPoint(p1);
		this.p2 = geometryfactory.createPoint(p2);
		this.p3 = geometryfactory.createPoint(p3);
		this.p4 = geometryfactory.createPoint(p4);
		this.p5 = geometryfactory.createPoint(p5);
		this.p6 = geometryfactory.createPoint(p6);
		this.p7 = geometryfactory.createPoint(p7);
		this.p8 = geometryfactory.createPoint(p8);
	}

	public void cycle() {
		WB_Point tmp = p1;
		p1 = p2;
		p2 = p3;
		p3 = p4;
		p4 = p5;
		p5 = p6;
		p6 = p7;
		p7 = p8;
		p8 = tmp;
	}

	public void cycle(int n) {
		while (n >= 8) {
			n -= 8;
		}
		while (n < 0) {
			n += 8;
		}
		for (int i = 0; i < n; i++) {
			cycle();
		}
	}

}