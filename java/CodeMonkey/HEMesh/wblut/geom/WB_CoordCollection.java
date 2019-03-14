/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 * 
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 * 
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */
package wblut.geom;

import java.util.Collection;
import java.util.List;

import org.eclipse.collections.impl.list.mutable.FastList;

/**
 *
 *
 * @author Frederik Vanhoutte
 *
 */
public abstract class WB_CoordCollection {

	private WB_CoordCollection() {

	}

	public static WB_CoordCollection getCollection(final WB_Coord[] coords) {
		return new WB_CoordCollectionArray(coords);
	}

	public static WB_CoordCollection getCollection(final WB_Polygon polygon) {
		return new WB_CoordCollectionPolygon(polygon);
	}

	public static WB_CoordCollection getCollection(final WB_PointGenerator generator, final int n) {
		WB_Coord[] coords = new WB_Point[n];
		for (int i = 0; i < n; i++) {
			coords[i] = generator.nextPoint();
		}
		return new WB_CoordCollectionArray(coords);
	}

	public WB_CoordCollection noise(final WB_RandomPoint generator) {
		return new WB_CoordCollectionNoise(this, generator);
	}

	public WB_CoordCollection map(final WB_Map map) {
		return new WB_CoordCollectionMap(this, map);
	}

	public static WB_CoordCollection getCollection(final Collection<? extends WB_Coord> coords) {
		return new WB_CoordCollectionList(coords);
	}

	abstract public WB_Coord get(final int i);

	public List<WB_Coord> subList(final int fromInc, final int toExcl) {
		return toList().subList(fromInc, toExcl);
	}

	abstract public int size();

	abstract public WB_Coord[] toArray();

	abstract public List<WB_Coord> toList();

	static class WB_CoordCollectionArray extends WB_CoordCollection {
		WB_Coord[] array;

		WB_CoordCollectionArray(final WB_Coord[] coords) {
			this.array = coords;
		}

		@Override
		public WB_Coord get(final int i) {
			return array[i];
		}

		@Override
		public int size() {
			return array.length;
		}

		@Override
		public WB_Coord[] toArray() {
			return array;
		}

		@Override
		public List<WB_Coord> toList() {
			List<WB_Coord> list = new FastList<WB_Coord>();
			for (WB_Coord c : array) {
				list.add(c);
			}

			return list;
		}

	}

	static class WB_CoordCollectionList extends WB_CoordCollection {
		List<WB_Coord> list;

		WB_CoordCollectionList(final Collection<? extends WB_Coord> coords) {
			this.list = new FastList<WB_Coord>();
			list.addAll(coords);
		}

		@Override
		public WB_Coord get(final int i) {
			return list.get(i);
		}

		@Override
		public int size() {
			return list.size();
		}

		@Override
		public WB_Coord[] toArray() {
			WB_Coord[] array = new WB_Coord[list.size()];
			int i = 0;
			for (WB_Coord c : list) {
				array[i++] = c;
			}
			return array;
		}

		@Override
		public List<WB_Coord> toList() {

			return list;
		}

	}

	static class WB_CoordCollectionPolygon extends WB_CoordCollection {
		WB_Polygon polygon;

		WB_CoordCollectionPolygon(final WB_Polygon polygon) {
			this.polygon = polygon;
		}

		@Override
		public WB_Coord get(final int i) {
			return polygon.getPoint(i);
		}

		@Override
		public int size() {
			return polygon.getNumberOfShellPoints();
		}

		@Override
		public WB_Coord[] toArray() {
			WB_Coord[] array = new WB_Coord[polygon.getNumberOfShellPoints()];
			for (int i = 0; i < polygon.getNumberOfShellPoints(); i++) {
				array[i] = polygon.getPoint(i);
			}
			return array;
		}

		@Override
		public List<WB_Coord> toList() {

			List<WB_Coord> list = new FastList<WB_Coord>();
			for (int i = 0; i < polygon.getNumberOfShellPoints(); i++) {
				list.add(polygon.getPoint(i));
			}

			return list;
		}

	}

	static class WB_CoordCollectionNoise extends WB_CoordCollection {
		WB_CoordCollection source;
		WB_Coord[] noise;

		WB_CoordCollectionNoise(final WB_CoordCollection source, final WB_RandomPoint generator) {
			this.source = source;
			noise = new WB_Coord[source.size()];
			for (int i = 0; i < source.size(); i++) {
				noise[i] = generator.nextVector();
			}

		}

		@Override
		public WB_Coord get(final int i) {
			return WB_Point.add(source.get(i), noise[i]);
		}

		@Override
		public int size() {
			return source.size();
		}

		@Override
		public WB_Coord[] toArray() {
			WB_Coord[] array = new WB_Coord[source.size()];

			for (int i = 0; i < source.size(); i++) {
				array[i] = WB_Point.add(source.get(i), noise[i]);
			}
			return array;
		}

		@Override
		public List<WB_Coord> toList() {
			List<WB_Coord> list = new FastList<WB_Coord>();
			for (int i = 0; i < source.size(); i++) {
				list.add(WB_Point.add(source.get(i), noise[i]));
			}

			return list;
		}

	}

	static class WB_CoordCollectionMap extends WB_CoordCollection {
		WB_CoordCollection source;
		WB_Map map;

		WB_CoordCollectionMap(final WB_CoordCollection source, final WB_Map map) {
			this.source = source;
			this.map = map;

		}

		@Override
		public WB_Coord get(final int i) {
			return map.mapPoint3D(source.get(i));
		}

		@Override
		public int size() {
			return source.size();
		}

		@Override
		public WB_Coord[] toArray() {
			WB_Coord[] array = new WB_Coord[source.size()];

			for (int i = 0; i < source.size(); i++) {
				array[i] = map.mapPoint3D(source.get(i));
			}
			return array;
		}

		@Override
		public List<WB_Coord> toList() {
			List<WB_Coord> list = new FastList<WB_Coord>();
			for (int i = 0; i < source.size(); i++) {
				list.add(map.mapPoint3D(source.get(i)));
			}

			return list;
		}

	}

	static class WB_CoordCollectionUnmap3D extends WB_CoordCollection {
		WB_CoordCollection source;
		WB_Map map;

		WB_CoordCollectionUnmap3D(final WB_CoordCollection source, final WB_Map map) {
			this.source = source;
			this.map = map;

		}

		@Override
		public WB_Coord get(final int i) {
			return map.unmapPoint3D(source.get(i));
		}

		@Override
		public int size() {
			return source.size();
		}

		@Override
		public WB_Coord[] toArray() {
			WB_Coord[] array = new WB_Coord[source.size()];

			for (int i = 0; i < source.size(); i++) {
				array[i] = map.unmapPoint3D(source.get(i));
			}
			return array;
		}

		@Override
		public List<WB_Coord> toList() {
			List<WB_Coord> list = new FastList<WB_Coord>();
			for (int i = 0; i < source.size(); i++) {
				list.add(map.unmapPoint3D(source.get(i)));
			}

			return list;
		}

	}

	static class WB_CoordCollectionUnmap2D extends WB_CoordCollection {
		WB_CoordCollection source;
		WB_Map2D map;

		WB_CoordCollectionUnmap2D(final WB_CoordCollection source, final WB_Map2D map) {
			this.source = source;
			this.map = map;

		}

		@Override
		public WB_Coord get(final int i) {
			return map.unmapPoint2D(source.get(i));
		}

		@Override
		public int size() {
			return source.size();
		}

		@Override
		public WB_Coord[] toArray() {
			WB_Coord[] array = new WB_Coord[source.size()];

			for (int i = 0; i < source.size(); i++) {
				array[i] = map.unmapPoint2D(source.get(i));
			}
			return array;
		}

		@Override
		public List<WB_Coord> toList() {
			List<WB_Coord> list = new FastList<WB_Coord>();
			for (int i = 0; i < source.size(); i++) {
				list.add(map.unmapPoint2D(source.get(i)));
			}

			return list;
		}

	}

}
