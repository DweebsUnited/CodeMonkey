package CodeMonkey.project;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.event.MouseEvent;

public class BigAnvil2 extends ProjectBase {

	private PImage img;
	private PGraphics canvas;
	private PImage thresh;
	private int cWidth, cHeigh;

	private boolean drawt = false;
	private boolean sortV = false;
	private boolean stopSorts = true;
	private boolean useVectorSort = true;

	// Sort vector tracking
	private class SortVector {
		public float originX, originY;
		public float endX, endY;

		public SortVector(float ox, float oy, float ex, float ey) {
			this.originX = ox;
			this.originY = oy;
			this.endX = ex;
			this.endY = ey;
		}

		public float distToOrigin(float mx, float my) {
			return dist(originX, originY, mx, my);
		}
	}

	private ArrayList<SortVector> sortVectors;
	private boolean isDragging = false;
	private float dragStartX, dragStartY;
	private float dragCurrentX, dragCurrentY;

	private class Pixel {

		public int col;
		public float sortVal;
	}

	private Pixel[] sortField;

	// Vector field for sort vectors
	private class VectorCell {
		public float direction;  // Normalized direction angle
		public float strength;   // Weighted strength value

		public VectorCell() {
			this.direction = 0;
			this.strength = 0;
		}
	}

	private VectorCell[] vectorField;
	private float minStrength = Float.MAX_VALUE;
	private float maxStrength = -Float.MAX_VALUE;
	private boolean slowMode = false;

	public static void main(String[] args) {

		PApplet.main("CodeMonkey.project.BigAnvil2");

	}

	@Override
	public void settings() {

		this.size(720, 640);

		this.setName();

	}

	@Override
	public void setup() {

		this.img = this.loadImage(ProjectBase.dataDir + "StarryNight.jpg");
		this.cWidth = this.img.width;
		this.cHeigh = this.img.height;
		this.img.loadPixels();

		this.thresh = this.createImage(this.cWidth, this.cHeigh, PConstants.RGB);
		this.thresh.copy(this.img, 0, 0, this.cWidth, this.cHeigh, 0, 0, this.cWidth, this.cHeigh);
		this.thresh.loadPixels();
		// Dark yellow from sun
		// float dc = this.color( 201, 175, 56 );
		// Cream from the middle somewhere
		// int dc = this.color( 154, 147, 141 );
		// Blue from the sky
		int dc = this.color(54, 74, 135);
		float dh = this.hue(dc);
		float ds = this.saturation(dc);
		float db = this.brightness(dc);
		float th = 125;
		float ts = 100;
		float tb = 50;

		initCanvas(dc, dh, ds, db, th, ts, tb);

		// Initialize sort vectors list
		this.sortVectors = new ArrayList<SortVector>();

	}

	private void initCanvas(int dc, float dh, float ds, float db, float th, float ts, float tb) {
		this.canvas = this.createGraphics(this.cWidth, this.cHeigh);
		this.canvas.beginDraw();

		this.sortField = new Pixel[this.cWidth * this.cHeigh];
		for (int dy = 0; dy < this.cHeigh; ++dy) {
			for (int dx = 0; dx < this.cWidth; ++dx) {

				int dxdx = dx + dy * this.cWidth;

				// First set up the sortField pixels

				Pixel px = new Pixel();

				px.col = this.img.pixels[dxdx];

				// TODO: Crucial: How we sort

				int cr = (px.col >> 0x10) & 0xFF;
				int cg = (px.col >> 0x08) & 0xFF;
				int cb = (px.col >> 0x00) & 0xFF;
				float h = this.hue(px.col);
				float s = this.saturation(px.col);
				float b = this.brightness(px.col);

				px.sortVal = cr * cr + cg * cg + cb * cb;

				this.sortField[dxdx] = px;

				// Now the threshold field
				// This is here because it needs cr cg cb too :/
				// EFFICIENCY

				if (Math.abs(h - dh) < th && Math.abs(s - ds) < ts && Math.abs(b - db) < tb)
					this.thresh.pixels[dxdx] = this.color(255);
				else
					this.thresh.pixels[dxdx] = this.color(0);

			}
		}

		this.thresh.updatePixels();
		this.thresh.filter(PConstants.BLUR, 2);
	}

	private boolean checkSwap(int a, int b) {

		if (this.sortField[a].sortVal < this.sortField[b].sortVal) {

			Pixel t = this.sortField[a];
			this.sortField[a] = this.sortField[b];
			this.sortField[b] = t;

			return true;

		}

		return false;

	}

	private boolean runSortV() {

		boolean didSwap = false;

		int dx = 0, dy = 0;
		while (dx < this.cWidth) {

			for (dy = 0; dy < this.cHeigh && (this.thresh.pixels[dx + dy * this.cWidth] & 0xFF) <= 127; ++dy);

			while (dy < this.cHeigh) {
				int dxdx = dx + dy * this.cWidth;
				int ny;

				for (ny = dy + 1; ny < this.cHeigh && (this.thresh.pixels[dx + ny * this.cWidth] & 0xFF) <= 127; ++ny);

				if (ny == this.cHeigh)
					break;

				if (ny - dy < (this.slowMode ? 2 : 5))
					didSwap = didSwap | this.checkSwap(dx + ny * this.cWidth, dxdx);

				dy = ny;

			}

			dx += 1;

		}

		if (!didSwap) {
			this.slowMode = true;
		}

		return didSwap;

	}

	private boolean runSortH() {

		boolean didSwap = false;

		int dx = 0, dy = 0;
		while (dy < this.cHeigh) {

			int oy = dy * this.cWidth;

			for (dx = 0; dx < this.cWidth && (this.thresh.pixels[dx + oy] & 0xFF) <= 127; ++dx);

			while (dx < this.cWidth) {

				int dxdx = dx + oy;
				int nx;

				for (nx = dx + 1; nx < this.cWidth && (this.thresh.pixels[nx + oy] & 0xFF) <= 127; ++nx);

				if (nx == this.cWidth)
					break;

				if (nx - dx < (this.slowMode ? 2 : 5))
					didSwap = didSwap | this.checkSwap(nx + oy, dxdx);

				dx = nx;

			}

			dy += 1;

		}

		if (!didSwap) {
			this.slowMode = true;
		}

		return didSwap;

	}

	private void recalculateVectorField() {
		this.slowMode = false;
		if (this.vectorField == null) {
			this.vectorField = new VectorCell[this.cWidth * this.cHeigh];
			for (int i = 0; i < this.vectorField.length; i++) {
				this.vectorField[i] = new VectorCell();
			}
		}

		this.minStrength = Float.MAX_VALUE;
		this.maxStrength = -Float.MAX_VALUE;

		for (int dy = 0; dy < this.cHeigh; ++dy) {
			for (int dx = 0; dx < this.cWidth; ++dx) {
				int idx = dx + dy * this.cWidth;
				float totalX = 0;
				float totalY = 0;
				float totalWeight = 0;

				for (int i = 0; i < this.sortVectors.size(); i++) {
					SortVector sv = this.sortVectors.get(i);
					float vx = sv.endX - sv.originX;
					float vy = sv.endY - sv.originY;
					float vecLen = (float) Math.sqrt(vx * vx + vy * vy);
					if (vecLen > 0) {
						vx /= vecLen;
						vy /= vecLen;
					}
					float dist = (float) Math.sqrt((dx - sv.originX) * (dx - sv.originX) + (dy - sv.originY) * (dy - sv.originY));
					float weight = 1.0f / (dist + 1.0f);
					totalX += vx * weight;
					totalY += vy * weight;
					totalWeight += weight;
				}

				if (totalWeight > 0) {
					totalX /= totalWeight;
					totalY /= totalWeight;
					this.vectorField[idx].direction = (float) Math.atan2(totalY, totalX);
					this.vectorField[idx].strength = (float) Math.sqrt(totalX * totalX + totalY * totalY);
				} else {
					this.vectorField[idx].direction = 0f;
					this.vectorField[idx].strength = 0f;
				}

				if (this.vectorField[idx].strength < this.minStrength) {
					this.minStrength = this.vectorField[idx].strength;
				}
				if (this.vectorField[idx].strength > this.maxStrength) {
					this.maxStrength = this.vectorField[idx].strength;
				}
			}
		}

		System.out.println("Vector field recalculated. Min strength: " + this.minStrength + ", Max strength: " + this.maxStrength);
	}

	private boolean runSortVectors() {
		boolean didSwap = false;

		for (int dy = 0; dy < this.cHeigh; ++dy) {
			for (int dx = 0; dx < this.cWidth; ++dx) {
				int idx = dx + dy * this.cWidth;
				VectorCell vc = this.vectorField[idx];

				if (vc.strength == 0) {
					continue;
				}

				float normalizedStrength = (vc.strength - this.minStrength) / (this.maxStrength - this.minStrength + 0.001f);
				int offset = this.slowMode ? 1 : (int) Math.round(1.0f + normalizedStrength * 9.0f);

				int targetDx = (int) Math.round(dx + Math.cos(vc.direction) * offset);
				int targetDy = (int) Math.round(dy + Math.sin(vc.direction) * offset);

				if (targetDx < 0) targetDx = 0;
				if (targetDx >= this.cWidth) targetDx = this.cWidth - 1;
				if (targetDy < 0) targetDy = 0;
				if (targetDy >= this.cHeigh) targetDy = this.cHeigh - 1;

				int targetIdx = targetDx + targetDy * this.cWidth;

				if (idx != targetIdx) {
					didSwap = didSwap | this.checkSwap(targetIdx, idx);
				}
			}
		}

		if (!didSwap) {
			this.slowMode = true;
		}

		return didSwap;
	}

	@Override
	public void draw() {

		if (!this.stopSorts) {
				if (this.useVectorSort) {
					this.runSortVectors();
				} else if (this.sortV) {
					if (!this.runSortV())
						this.sortV = !this.sortV;
				} else {
					if (!this.runSortH())
						this.sortV = !this.sortV;
				}
		}

		this.canvas.loadPixels();
		for (int dy = 0; dy < this.cHeigh; ++dy) {
			for (int dx = 0; dx < this.cWidth - 1; ++dx) {
				int dxdx = dx + dy * this.cWidth;

				this.canvas.pixels[dxdx] = this.sortField[dxdx].col;

			}
		}
		this.canvas.updatePixels();

		if (this.drawt)
			this.image(this.thresh, 0, 0, this.pixelWidth, this.pixelHeight);
		else
			this.image(this.canvas, 0, 0, this.pixelWidth, this.pixelHeight);

		// Draw existing sort vectors
		for (int i = 0; i < this.sortVectors.size(); i++) {
			SortVector sv = this.sortVectors.get(i);
			this.stroke(0, 255, 0);
			this.strokeWeight(2);
			this.circle(sv.originX, sv.originY, 5);
			this.line(sv.originX, sv.originY, sv.endX, sv.endY);
		}

		// Draw current drag line
		if (this.isDragging) {
			this.stroke(255, 0, 0);
			this.strokeWeight(2);
			this.point(this.dragStartX, this.dragStartY);
			this.line(this.dragStartX, this.dragStartY, this.dragCurrentX, this.dragCurrentY);
		}

	}

	@Override
	public void keyPressed() {

		if (this.key == 'w') {
			this.save(this.canvas);
			System.out.println("Canvas saved");
		} else if (this.key == ' ') {
			this.drawt = !this.drawt;
			System.out.println("Draw threshold: " + this.drawt);
		}else if (this.key == 'a') {
			this.slowMode = false;
			this.sortV = !this.sortV;
			System.out.println("Sort direction swapped: " + this.sortV);
		} else if (this.key == 's') {
			this.slowMode = false;
			this.stopSorts = true;
			// Reinitialize canvas
			int dc = this.color(54, 74, 135);
			float dh = this.hue(dc);
			float ds = this.saturation(dc);
			float db = this.brightness(dc);
			float th = 125;
			float ts = 100;
			float tb = 50;
			initCanvas(dc, dh, ds, db, th, ts, tb);
			System.out.println("Canvas reinitialized (sorts enabled)");
		} else if (this.key == 't') {
			this.slowMode = false;
			this.stopSorts = !this.stopSorts;
			if (this.stopSorts) {
				System.out.println("Sorts stopped");
			} else {
				System.out.println("Sorts resumed");
			}
		} else if (this.key == 'v') {
			this.slowMode = false;
			this.useVectorSort = !this.useVectorSort;
			System.out.println("Vector sort: " + this.useVectorSort);
		}

	}

	@Override
	public void mousePressed(MouseEvent event) {

		// Start dragging on left mouse click
		if (this.mouseButton == PConstants.LEFT) {
			this.isDragging = true;
			this.dragStartX = this.mouseX;
			this.dragStartY = this.mouseY;
			this.dragCurrentX = this.mouseX;
			this.dragCurrentY = this.mouseY;
			System.out.println("Drag started at " + this.dragStartX + ", " + this.dragStartY);
		}

	}

	@Override
	public void mouseReleased() {

		// Commit sort vector on left mouse release
		if (this.mouseButton == PConstants.LEFT && this.isDragging) {
			float dx = this.dragCurrentX - this.dragStartX;
			float dy = this.dragCurrentY - this.dragStartY;
			if (dx * dx + dy * dy > 25 * 25) {
				this.sortVectors.add(new SortVector(this.dragStartX, this.dragStartY, this.dragCurrentX, this.dragCurrentY));
				this.recalculateVectorField();
				System.out.println("Sort vector committed: " + this.dragStartX + ", " + this.dragStartY + " -> " + this.dragCurrentX + ", " + this.dragCurrentY);
			}
			this.isDragging = false;
		}
		// Remove closest sort vector on right mouse release
		else if (this.mouseButton == PConstants.RIGHT) {
			int closestIndex = -1;
			float closestDist = Float.MAX_VALUE;
			for (int i = 0; i < this.sortVectors.size(); i++) {
				SortVector sv = this.sortVectors.get(i);
				float d = sv.distToOrigin(this.mouseX, this.mouseY);
				if (d < closestDist) {
					closestDist = d;
					closestIndex = i;
				}
			}
			if (closestIndex >= 0) {
				this.sortVectors.remove(closestIndex);
				this.recalculateVectorField();
				System.out.println("Sort vector removed: " + closestIndex);
			}
		}

	}

	@Override
	public void mouseMoved(MouseEvent event) {

		// Called whenever the mouse is moved without a button being pressed.

	}

	@Override
	public void mouseDragged(MouseEvent event) {
		// Update current drag position
		if (this.isDragging && this.mouseButton == PConstants.LEFT) {
			this.dragCurrentX = this.mouseX;
			this.dragCurrentY = this.mouseY;
			System.out.println("Drag updated to " + this.dragCurrentX + ", " + this.dragCurrentY);
		}
	}

}
