/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 * 
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 * 
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.hemesh;

import wblut.core.WB_ProgressReporter.WB_ProgressTracker;
import wblut.geom.WB_GeometryFactory;

/**
 *
 */
public abstract class HE_MeshElement extends HE_Element {

	protected volatile boolean visited;
	protected final static WB_GeometryFactory gf = new WB_GeometryFactory();
	protected final static WB_ProgressTracker tracker = WB_ProgressTracker.instance();
	protected boolean visible;
	protected int color;

	/**
	 *
	 */
	public HE_MeshElement() {
		super();
		visited = false;
		visible = true;
		color = -1;
	}

	/**
	 *
	 */
	public void clearVisited() {
		visited = false;
	}

	/**
	 *
	 */
	public void setVisited() {
		visited = true;
	}

	/**
	 *
	 */
	public void setVisited(final boolean b) {
		visited = b;
	}

	/**
	 *
	 *
	 * @return
	 */
	public boolean isVisited() {
		return visited;
	}

	/**
	 *
	 */
	public void setVisible(final boolean b) {
		visible = b;
	}

	/**
	 *
	 */
	public boolean isVisible() {
		return visible;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (int) (key ^ key >>> 32);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof HE_MeshElement)) {
			return false;
		}
		return ((HE_MeshElement) other).getKey() == key;
	}

	/**
	 *
	 *
	 * @param el
	 */
	public void copyProperties(final HE_MeshElement el) {
		super.copyProperties(el);
		visited = el.visited;
		visible = el.visible;
		color = el.color;
	}

	public int getColor() {
		return color;
	}

	public void setColor(final int color) {
		this.color = color;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Element#clear()
	 */
	@Override
	protected abstract void clear();
}
