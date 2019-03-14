/**
 *
 */
package wblut.geom;

/**
 * @author FVH
 *
 */
public abstract class WB_BinaryGrid3D {
	int sizeX;
	int sizeY;
	int sizeZ;
	WB_Point center;
	double dX, dY, dZ;

	public abstract void set(int x, int y, int z);

	public abstract void clear(int x, int y, int z);

	public abstract boolean get(int x, int y, int z);

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public int getSizeZ() {
		return sizeZ;
	}

	public WB_Coord getCenter() {
		return center;
	}

	public WB_Coord getMin() {
		return center.sub(sizeX * 0.5 * dX, sizeY * 0.5 * dY, sizeZ * 0.5 * dZ);
	}

	public double getDX() {
		return dX;
	}

	public double getDY() {
		return dY;
	}

	public double getDZ() {
		return dZ;
	}

	public void setCenter(final WB_Coord c) {
		center.set(c);
	}

	public void setDX(final double dX) {
		this.dX = dX;
	}

	public void setDY(final double dY) {
		this.dY = dY;
	}

	public void setDZ(final double dZ) {
		this.dZ = dZ;
	}

	private WB_BinaryGrid3D() {

	}

	public static WB_BinaryGrid3D createGrid(final WB_Coord c, final int sizeX, final double dX, final int sizeY,
			final double dY, final int sizeZ, final double dZ) {
		return new WB_BinaryGridSafeArray3D(c, sizeX, dX, sizeY, dY, sizeZ, dZ);
	}

	static class WB_BinaryGridSafeArray3D extends WB_BinaryGrid3D {
		boolean[] grid;
		int sizeXY;

		WB_BinaryGridSafeArray3D(final WB_Coord c, final int sizeX, final double dX, final int sizeY, final double dY,
				final int sizeZ, final double dZ) {
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			this.sizeXY = sizeX * sizeY;
			this.sizeZ = sizeZ;
			this.dX = dX;
			this.dY = dY;
			this.dZ = dZ;
			center = new WB_Point(c);
			grid = new boolean[sizeXY * sizeZ];
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_BinaryGrid2D#set(boolean, int, int)
		 */
		@Override
		public void set(final int x, final int y, final int z) {
			if (x > -1 && y > -1 && z > -1 && x < sizeX && y < sizeY && z < sizeZ) {
				grid[index(x, y, z)] = true;
			}
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_BinaryGrid2D#set(boolean, int, int)
		 */
		@Override
		public void clear(final int x, final int y, final int z) {
			if (x > -1 && y > -1 && z > -1 && x < sizeX && y < sizeY && z < sizeZ) {
				grid[index(x, y, z)] = false;
			}
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_BinaryGrid2D#get(int, int)
		 */
		@Override
		public boolean get(final int x, final int y, final int z) {
			if (x > -1 && y > -1 && z > -1 && x < sizeX && y < sizeY && z < sizeZ) {
				return grid[index(x, y, z)];
			} else {
				return false;
			}
		}

		int index(final int i, final int j, final int k) {
			return i + j * sizeX + k * sizeXY;
		}
	}

}
