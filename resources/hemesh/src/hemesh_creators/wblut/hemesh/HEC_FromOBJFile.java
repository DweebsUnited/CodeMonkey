/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 * 
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 * 
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.hemesh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.eclipse.collections.impl.list.mutable.FastList;

import wblut.geom.WB_Point;

/**
 *
 */
public class HEC_FromOBJFile extends HEC_Creator {

	/**
	 *
	 */
	private String path;

	/**
	 *
	 */
	private double scale;

	/**
	 *
	 */
	public HEC_FromOBJFile() {
		super();
		scale = 1;
		path = null;
		override = true;
	}

	/**
	 *
	 *
	 * @param path
	 */
	public HEC_FromOBJFile(final String path) {
		super();
		this.path = path;
		scale = 1;
		override = true;
	}

	/**
	 *
	 *
	 * @param path
	 * @return
	 */
	public HEC_FromOBJFile setPath(final String path) {
		this.path = path;
		return this;
	}

	/**
	 *
	 *
	 * @param f
	 * @return
	 */
	@Override
	public HEC_FromOBJFile setScale(final double f) {
		scale = f;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.creators.HEC_Creator#createBase()
	 */
	@Override
	protected HE_Mesh createBase() {
		if (path == null) {
			return new HE_Mesh();
		}
		final File file = new File(path);
		final InputStream is = createInputStream(file);
		if (is == null) {
			return new HE_Mesh();
		}
		final List<WB_Point> vertexList = new FastList<WB_Point>();
		final List<WB_Point> UVWList = new FastList<WB_Point>();
		final List<int[]> faceList = new FastList<int[]>();
		final List<int[]> faceUVWList = new FastList<int[]>();
		int faceCount = 0;
		// load OBJ file as an array of strings
		final String objStrings[] = loadStrings(is);
		boolean hasTexture = false;
		for (int i = 0; i < objStrings.length; i++) {
			// split every line in parts divided by spaces
			final String[] parts = objStrings[i].split("\\s+");
			// the first part indicates the kind of data that is in that line
			// v stands for vertex data
			if (parts[0].equals("v")) {
				final double x1 = scale * Double.parseDouble(parts[1]);
				final double y1 = scale * Double.parseDouble(parts[2]);
				final double z1 = parts.length > 3 ? scale * Double.parseDouble(parts[3]) : 0;
				final WB_Point pointLoc = new WB_Point(x1, y1, z1);
				vertexList.add(pointLoc);
			}
			if (parts[0].equals("vt")) {

				final double u = Double.parseDouble(parts[1]);
				final double v = parts.length > 2 ? Double.parseDouble(parts[2]) : 0;
				final double w = parts.length > 3 ? Double.parseDouble(parts[3]) : 0;
				final WB_Point pointUVW = new WB_Point(u, v, w);
				UVWList.add(pointUVW);
				hasTexture = true;
			}
			// f stands for facelist data
			// should work for non triangular faces
			if (parts[0].equals("f")) {
				final int[] tempFace = new int[parts.length - 1];
				final int[] tempUVWFace = new int[parts.length - 1];
				for (int j = 0; j < parts.length - 1; j++) {
					final String[] num = parts[j + 1].split("/");
					tempFace[j] = Integer.parseInt(num[0]) - 1;
					if (num.length > 2) {
						tempUVWFace[j] = Integer.parseInt(num[2]) - 1;
					}
				}
				faceList.add(tempFace);
				faceUVWList.add(tempUVWFace);
				faceCount++;
			}
		}

		// the HEC_FromFacelist wants the face data as int[][]
		final int[][] faceArray = new int[faceCount][];
		for (int i = 0; i < faceCount; i++) {
			final int[] tempFace = faceList.get(i);
			faceArray[i] = tempFace;
		}
		final int[][] faceUVWArray = new int[faceCount][];
		if (hasTexture) {
			for (int i = 0; i < faceCount; i++) {
				final int[] tempUVWFace = faceUVWList.get(i);
				faceUVWArray[i] = tempUVWFace;
			}
		}

		// et voila... add to the creator
		final HEC_FromFacelist creator = new HEC_FromFacelist();
		creator.setVertices(vertexList);
		creator.setFaces(faceArray);
		if (hasTexture) {
			creator.setFacesUVW(faceUVWArray);
			creator.setFaceVertexUVW(UVWList);
		}
		creator.setDuplicate(true);

		return new HE_Mesh(creator);
	}

	// Code excerpts from processing.core
	/**
	 *
	 *
	 * @param file
	 * @return
	 */
	private InputStream createInputStream(final File file) {
		if (file == null) {
			throw new IllegalArgumentException("file can't be null");
		}
		try {
			InputStream stream = new FileInputStream(file);
			if (file.getName().toLowerCase().endsWith(".gz")) {
				stream = new GZIPInputStream(stream);
			}
			return stream;
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 *
	 * @param input
	 * @return
	 */
	private String[] loadStrings(final InputStream input) {
		try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			return loadStrings(reader);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 *
	 * @param reader
	 * @return
	 */
	private String[] loadStrings(final BufferedReader reader) {
		try {
			String lines[] = new String[100];
			int lineCount = 0;
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (lineCount == lines.length) {
					final String temp[] = new String[lineCount << 1];
					System.arraycopy(lines, 0, temp, 0, lineCount);
					lines = temp;
				}
				lines[lineCount++] = line;
			}
			reader.close();
			if (lineCount == lines.length) {
				return lines;
			}
			final String output[] = new String[lineCount];
			System.arraycopy(lines, 0, output, 0, lineCount);
			return output;
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
