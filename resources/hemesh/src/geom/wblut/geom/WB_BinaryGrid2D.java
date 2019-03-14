/**
 *
 */
package wblut.geom;

/**
 * @author FVH
 *
 */
public abstract class WB_BinaryGrid2D {
	int sizeX;
	int sizeY;
	WB_Point center;
	double dX, dY;

	public abstract void set(int x, int y);

	public abstract void clear(int x, int y);

	public abstract boolean get(int x, int y);

	private WB_BinaryGrid2D() {

	}

	public static WB_BinaryGrid2D createGrid(final WB_Coord c, final int sizeX, final double dX, final int sizeY,
			final double dY) {
		return new WB_BinaryGridSafeArray2D(c, sizeX, dX, sizeY, dY);
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public WB_Coord getCenter() {
		return center;
	}

	public WB_Coord getMin() {
		return center.sub(sizeX * 0.5 * dX, sizeY * 0.5 * dY);
	}

	public double getDX() {
		return dX;
	}

	public double getDY() {
		return dY;
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

	static class WB_BinaryGridSafeArray2D extends WB_BinaryGrid2D {
		boolean[] grid;

		WB_BinaryGridSafeArray2D(final WB_Coord c, final int sizeX, final double dX, final int sizeY, final double dY) {
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			this.dX = dX;
			this.dY = dY;
			center = new WB_Point(c);
			grid = new boolean[sizeX * sizeY];
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_BinaryGrid2D#set(boolean, int, int)
		 */
		@Override
		public void set(final int x, final int y) {
			if (x > -1 && y > -1 && x < sizeX && y < sizeY) {
				grid[index(x, y)] = true;
			}

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_BinaryGrid2D#set(boolean, int, int)
		 */
		@Override
		public void clear(final int x, final int y) {
			if (x > -1 && y > -1 && x < sizeX && y < sizeY) {
				grid[index(x, y)] = false;
			}
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_BinaryGrid2D#get(int, int)
		 */
		@Override
		public boolean get(final int x, final int y) {
			if (x > -1 && y > -1 && x < sizeX && y < sizeY) {
				return grid[index(x, y)];
			} else {
				return false;
			}
		}

		int index(final int i, final int j) {
			return i + j * sizeX;
		}
	}

}
