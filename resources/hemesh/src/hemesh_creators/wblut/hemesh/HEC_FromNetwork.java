/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 *
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 *
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.hemesh;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Network;
import wblut.geom.WB_Network.Connection;
import wblut.geom.WB_Network.Node;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;
import wblut.math.WB_ConstantScalarParameter;
import wblut.math.WB_ScalarParameter;

/**
 *
 */
public class HEC_FromNetwork extends HEC_Creator {
	/**
	 *
	 */
	private static WB_GeometryFactory gf = new WB_GeometryFactory();
	/**
	 *
	 */
	private WB_Network network;
	/**
	 *
	 */
	private int numberOfNodes, numberOfConnections;
	/**
	 *
	 */
	private NodeType[] nodeTypes;
	/**
	 *
	 */
	private NodeConnection[] connectionNodeConnections;
	/**
	 *
	 */
	private WB_ScalarParameter connectionRadius;
	/**
	 *
	 */
	private int connectionFacets;
	/**
	 *
	 */
	private double fidget;
	/**
	 *
	 */
	private double fillfactor;
	/**
	 *
	 */
	private HE_Mesh mesh;
	/**
	 *
	 */
	private double maximumConnectionLength;
	/**
	 *
	 */
	private double minimumBalljointAngle;
	/**
	 *
	 */
	private WB_ScalarParameter maximumConnectionOffset;
	/**
	 *
	 */
	private boolean taper;
	/**
	 *
	 */
	private boolean cap;
	/**
	 *
	 */
	private boolean useNodeValues;
	/**
	 *
	 */
	private boolean createIsolatedNodes;
	/**
	 *
	 */
	private WB_ScalarParameter angleFactor;
	/**
	 *
	 */
	private boolean suppressBalljoint;

	/**
	 *
	 */
	enum NodeType {
		/**
		 *
		 */
		ISOLATED,
		/**
		 *
		 */
		ENDPOINT,
		/**
		 *
		 */
		STRAIGHT,
		/**
		 *
		 */
		BEND,
		/**
		 *
		 */
		TURN,
		/**
		 *
		 */
		STAR
	}

	/**
	 *
	 */
	class NodeConnection {
		/**
		 *
		 */
		double maxoffset;
		/**
		 *
		 */
		double offset;
		/**
		 *
		 */
		double radius;
		/**
		 *
		 */
		List<HE_Vertex> vertices;
		/**
		 *
		 */
		WB_Vector dir;
		/**
		 *
		 */
		Node node;
		/**
		 *
		 */
		Connection connection;

		/**
		 *
		 *
		 * @param node
		 * @param connection
		 * @param mo
		 * @param o
		 * @param r
		 */
		NodeConnection(final Node node, final Connection connection, final double mo, final double o, final double r) {
			maxoffset = mo;
			offset = o;
			radius = r;
			vertices = new ArrayList<HE_Vertex>();
			this.node = node;
			this.connection = connection;
		}
	}

	/**
	 *
	 */
	public HEC_FromNetwork() {
		connectionRadius = new WB_ConstantScalarParameter(5.0);
		connectionFacets = 6;
		maximumConnectionLength = 100.0;
		override = true;
		fidget = 1.0001;
		fillfactor = 0.99;
		minimumBalljointAngle = 0.55 * Math.PI;
		maximumConnectionOffset = new WB_ConstantScalarParameter(Double.MAX_VALUE);
		angleFactor = new WB_ConstantScalarParameter(0.0);
		cap = true;
		useNodeValues = true;
	}

	/**
	 *
	 *
	 * @param r
	 * @return
	 */
	public HEC_FromNetwork setConnectionRadius(final double r) {
		connectionRadius = new WB_ConstantScalarParameter(r);
		return this;
	}

	/**
	 *
	 *
	 * @param r
	 * @return
	 */
	public HEC_FromNetwork setConnectionRadius(final WB_ScalarParameter r) {
		connectionRadius = r;
		return this;
	}

	/**
	 *
	 *
	 * @param o
	 * @return
	 */
	public HEC_FromNetwork setMaximumConnectionOffset(final WB_ScalarParameter o) {
		maximumConnectionOffset = o;
		return this;
	}

	/**
	 *
	 *
	 * @param o
	 * @return
	 */
	public HEC_FromNetwork setMaximumConnectionOffset(final double o) {
		maximumConnectionOffset = new WB_ConstantScalarParameter(o);
		return this;
	}

	/**
	 *
	 *
	 * @param a
	 * @return
	 */
	public HEC_FromNetwork setMinimumBalljointAngle(final double a) {
		minimumBalljointAngle = a;
		return this;
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public HEC_FromNetwork setMaximumConnectionLength(final double d) {
		maximumConnectionLength = d;
		return this;
	}

	/**
	 *
	 *
	 * @param f
	 * @return
	 */
	public HEC_FromNetwork setConnectionFacets(final int f) {
		connectionFacets = f;
		return this;
	}

	/**
	 *
	 *
	 * @param b
	 * @return
	 */
	public HEC_FromNetwork setTaper(final boolean b) {
		taper = b;
		return this;
	}

	/**
	 *
	 *
	 * @param b
	 * @return
	 */
	public HEC_FromNetwork setCap(final boolean b) {
		cap = b;
		return this;
	}

	/**
	 *
	 *
	 * @param b
	 * @return
	 */
	public HEC_FromNetwork setSuppressBalljoint(final boolean b) {
		suppressBalljoint = b;
		return this;
	}

	/**
	 *
	 *
	 * @param b
	 * @return
	 */
	public HEC_FromNetwork setUseNodeValues(final boolean b) {
		useNodeValues = b;
		return this;
	}

	/**
	 *
	 *
	 * @param b
	 * @return
	 */
	public HEC_FromNetwork setCreateIsolatedNodes(final boolean b) {
		createIsolatedNodes = b;
		return this;
	}

	/**
	 *
	 *
	 * @param f
	 * @return
	 */
	public HEC_FromNetwork setFidget(final double f) {
		fidget = f;
		return this;
	}

	/**
	 *
	 *
	 * @param ff
	 * @return
	 */
	public HEC_FromNetwork setFillFactor(final double ff) {
		fillfactor = 0.99;
		return this;
	}

	/**
	 *
	 *
	 * @param af
	 * @return
	 */
	public HEC_FromNetwork setAngleOffset(final double af) {
		angleFactor = new WB_ConstantScalarParameter(af);
		return this;
	}

	/**
	 *
	 *
	 * @param af
	 * @return
	 */
	public HEC_FromNetwork setAngleOffset(final WB_ScalarParameter af) {
		angleFactor = af;
		return this;
	}

	/**
	 *
	 */
	private void getNodeTypes() {
		int i = 0;
		double minSpan;
		for (final Node node : network.getNodes()) {
			if (node.getOrder() == 0) {
				nodeTypes[i] = NodeType.ISOLATED;
			} else if (node.getOrder() == 1) {
				nodeTypes[i] = NodeType.ENDPOINT;
			} else if (node.getOrder() == 2) {
				minSpan = node.findSmallestSpan();
				if (minSpan == Math.PI) {
					nodeTypes[i] = NodeType.STRAIGHT;
				} else if (minSpan > minimumBalljointAngle) {
					nodeTypes[i] = NodeType.BEND;
				} else {
					nodeTypes[i] = NodeType.TURN;
				}
			} else {
				nodeTypes[i] = NodeType.STAR;
			}
			i++;
		}
	}

	/**
	 *
	 */
	private void getNodeConnections() {
		int i = 0;
		for (final Node node : network.getNodes()) {
			if (nodeTypes[i] == NodeType.ENDPOINT) {
				double r = connectionRadius.evaluate(node.xd(), node.yd(), node.zd());
				if (useNodeValues) {
					r *= node.getValue();
				}
				createNodeConnection(node, 0, 0.0, 0.0, r);
			} else if (nodeTypes[i] == NodeType.STRAIGHT) {
				double r = connectionRadius.evaluate(node.xd(), node.yd(), node.zd());
				if (useNodeValues) {
					r *= node.getValue();
				}
				double o = connectionRadius.evaluate(node.xd(), node.yd(), node.zd());
				if (useNodeValues) {
					o *= node.getValue();
				}
				createNodeConnection(node, 0, o, o, r);
				createNodeConnection(node, 1, o, o, r);
			} else if (nodeTypes[i] == NodeType.TURN || nodeTypes[i] == NodeType.BEND) {
				final double minSpan = node.findSmallestSpan();
				double r = connectionRadius.evaluate(node.xd(), node.yd(), node.zd());
				double o = fidget * r / Math.min(1.0, Math.tan(0.5 * minSpan));
				if (useNodeValues) {
					r *= node.getValue();
				}
				if (useNodeValues) {
					o *= node.getValue();
				}
				createNodeConnection(node, 0, o, o, r);
				createNodeConnection(node, 1, o, o, r);
			} else if (nodeTypes[i] == NodeType.STAR) {
				final double minSpan = node.findSmallestSpan();
				double r = connectionRadius.evaluate(node.xd(), node.yd(), node.zd());
				double mo = fidget * r / Math.min(1.0, Math.tan(0.5 * minSpan));
				if (useNodeValues) {
					r *= node.getValue();
				}
				if (useNodeValues) {
					mo *= node.getValue();
				}
				for (int j = 0; j < node.getOrder(); j++) {
					final double minLocSpan = node.findSmallestSpanAroundConnection(j);
					double o = fidget * r / Math.min(1.0, Math.tan(0.5 * minLocSpan));
					if (useNodeValues) {
						o *= node.getValue();
					}
					double mso = maximumConnectionOffset.evaluate(node.xd(), node.yd(), node.zd());
					if (useNodeValues) {
						mso *= node.getValue();
					}
					if (o > mso) {
						createNodeConnection(node, j, mo, mso,
								mso / fidget * Math.min(1.0, Math.tan(0.5 * minLocSpan)));
					} else {
						createNodeConnection(node, j, mo, o, r);
					}
				}
			}
			i++;
		}
		if (!taper) {
			for (i = 0; i < network.getNumberOfConnections(); i++) {
				final double r = Math.min(connectionNodeConnections[2 * i].radius,
						connectionNodeConnections[2 * i + 1].radius);
				connectionNodeConnections[2 * i].radius = r;
				connectionNodeConnections[2 * i + 1].radius = r;
			}
		}
	}

	/**
	 *
	 *
	 * @param node
	 * @param i
	 * @param maxoff
	 * @param off
	 * @param rad
	 */
	private void createNodeConnection(final Node node, final int i, final double maxoff, final double off,
			final double rad) {
		final Connection connection = node.getConnection(i);
		final int id = getConnectionIndex(node, connection);
		connectionNodeConnections[id] = new NodeConnection(node, connection, maxoff, off, rad);
	}

	/**
	 *
	 */
	private void createVertices() {
		final double da = 2 * Math.PI / connectionFacets;
		for (int id = 0; id < network.getNumberOfConnections() * 2; id++) {
			final double sr = connectionNodeConnections[id].radius;
			final double sgn = connectionNodeConnections[id].node == connectionNodeConnections[id].connection.start()
					? 1 : -1;
			final double so = connectionNodeConnections[id].maxoffset;
			final WB_Vector v = connectionNodeConnections[id].connection.toVector();
			v.normalizeSelf();
			v.mulSelf(sgn);
			connectionNodeConnections[id].dir = v.copy();
			final WB_Plane P = connectionNodeConnections[id].connection.toPlane();
			final WB_Vector u = P.getU();
			for (int j = 0; j < connectionFacets; j++) {
				final WB_Point p = new WB_Point(connectionNodeConnections[id].node);
				final double af = angleFactor.evaluate(connectionNodeConnections[id].node.xd(),
						connectionNodeConnections[id].node.yd(), connectionNodeConnections[id].node.zd());
				p.addMulSelf(so, v);
				final WB_Vector localu = u.mul(sr);
				localu.rotateAboutAxisSelf((j + af) * da, new WB_Point(), P.getNormal());
				p.addSelf(localu);
				final HE_Vertex vrtx = new HE_Vertex(p);
				vrtx.setInternalLabel(connectionNodeConnections[id].node.getIndex());
				connectionNodeConnections[id].vertices.add(vrtx);
				mesh.add(vrtx);
			}
		}

	}

	/**
	 *
	 *
	 * @param node
	 * @param connection
	 * @return
	 */
	private int getConnectionIndex(final Node node, final Connection connection) {
		if (node == connection.start()) {
			return 2 * connection.getIndex();
		} else {
			return 2 * connection.getIndex() + 1;
		}
	}

	/**
	 *
	 */
	private void createConnections() {
		int i = 0;
		for (final Connection connection : network.getConnections()) {
			/*
			 * System.out.println("HEC_FromFrame: Creating connection " + (i +
			 * 1) + " of " + frame.getNumberOfConnections() + ".");
			 */
			final int offsets = i * 2;
			final int offsete = i * 2 + 1;
			int ns = (int) Math.round(connection.getLength() / maximumConnectionLength);
			ns = Math.max(ns, 1);
			final ArrayList<HE_Halfedge> hes = new ArrayList<HE_Halfedge>();
			final HE_Vertex[][] extraVertices = new HE_Vertex[connectionFacets][ns - 1];
			for (int j = 0; j < connectionFacets; j++) {
				for (int k = 0; k < ns - 1; k++) {
					extraVertices[j][k] = new HE_Vertex(
							gf.createInterpolatedPoint(connectionNodeConnections[offsets].vertices.get(j),
									connectionNodeConnections[offsete].vertices.get(j), (k + 1) / (double) ns));
					mesh.add(extraVertices[j][k]);
				}
			}
			for (int j = 0; j < connectionFacets; j++) {
				final int jp = (j + 1) % connectionFacets;
				final HE_Vertex s0 = connectionNodeConnections[offsets].vertices.get(j);
				final HE_Vertex s1 = connectionNodeConnections[offsets].vertices.get(jp);
				final HE_Vertex e2 = connectionNodeConnections[offsete].vertices.get(jp);
				final HE_Vertex e3 = connectionNodeConnections[offsete].vertices.get(j);
				for (int k = 0; k < ns; k++) {
					final HE_Face f = new HE_Face();
					final HE_Halfedge he0 = new HE_Halfedge();
					final HE_Halfedge he1 = new HE_Halfedge();
					final HE_Halfedge he2 = new HE_Halfedge();
					final HE_Halfedge he3 = new HE_Halfedge();
					mesh.setVertex(he0, k == 0 ? s0 : extraVertices[j][k - 1]);
					mesh.setVertex(he1, k == 0 ? s1 : extraVertices[jp][k - 1]);
					mesh.setVertex(he2, k == ns - 1 ? e2 : extraVertices[jp][k]);
					mesh.setVertex(he3, k == ns - 1 ? e3 : extraVertices[j][k]);
					mesh.setNext(he0, he1);
					mesh.setNext(he1, he2);
					mesh.setNext(he2, he3);
					mesh.setNext(he3, he0);
					mesh.setFace(he0, f);
					mesh.setHalfedge(f, he0);
					f.setInternalLabel(1);
					mesh.setFace(he1, f);
					mesh.setFace(he2, f);
					mesh.setFace(he3, f);
					mesh.setHalfedge(he0.getVertex(), he0);
					mesh.setHalfedge(he1.getVertex(), he1);
					mesh.setHalfedge(he2.getVertex(), he2);
					mesh.setHalfedge(he3.getVertex(), he3);
					mesh.add(f);
					mesh.add(he0);
					mesh.add(he1);
					mesh.add(he2);
					mesh.add(he3);
					hes.add(he1);
					hes.add(he3);
					if (k < ns - 1 && k > 0) {
						hes.add(he0);
						hes.add(he2);
					}
				}
			}
			i++;
		}
	}

	/**
	 *
	 */
	private void createNodes() {
		int i = 0;
		for (Node node : network.getNodes()) {
			/*
			 * System.out.println("HEC_FromFrame: Creating node " + (i + 1) +
			 * " of " + frame.getNumberOfNodes() + ".");
			 */
			node = network.getNode(i);
			final List<Connection> connections = node.getConnections();
			final List<HE_Vertex> hullPoints = new ArrayList<HE_Vertex>();
			if (nodeTypes[i] == NodeType.ENDPOINT) {
				if (cap) {
					int offset;
					if (node == connections.get(0).start()) {
						offset = connections.get(0).getIndex() * 2;
					} else {
						offset = connections.get(0).getIndex() * 2 + 1;
					}
					final ArrayList<HE_Halfedge> hes = new ArrayList<HE_Halfedge>(connectionFacets);
					final HE_Face f = new HE_Face();
					mesh.add(f);
					for (int k = 0; k < connectionFacets; k++) {
						final HE_Halfedge he = new HE_Halfedge();
						mesh.setVertex(he, connectionNodeConnections[offset].vertices.get(k));
						mesh.setFace(he, f);
						hes.add(he);
						mesh.add(he);
					}
					mesh.setHalfedge(f, hes.get(0));
					f.setInternalLabel(3);
					if (node == connections.get(0).start()) {
						for (int k = 0, j = connectionFacets - 1; k < connectionFacets; j = k, k++) {
							mesh.setNext(hes.get(k), hes.get(j));
						}
					} else {
						for (int k = 0, j = connectionFacets - 1; k < connectionFacets; j = k, k++) {
							mesh.setNext(hes.get(j), hes.get(k));
						}
					}
				}
			} else {
				if (nodeTypes[i] != NodeType.ISOLATED || createIsolatedNodes) {
					double br = connectionRadius.evaluate(node.xd(), node.yd(), node.zd());
					for (int j = 0; j < connections.size(); j++) {
						int offset;
						if (node == connections.get(j).start()) {
							offset = connections.get(j).getIndex() * 2;
						} else {
							offset = connections.get(j).getIndex() * 2 + 1;
						}
						for (int k = 0; k < connectionFacets; k++) {
							hullPoints.add(connectionNodeConnections[offset].vertices.get(k));
							br = Math.min(br, connectionNodeConnections[offset].radius);
						}
					}
					br *= fillfactor;
					final int n = hullPoints.size();
					if (nodeTypes[i] != NodeType.STRAIGHT && nodeTypes[i] != NodeType.BEND && !suppressBalljoint) {
						final HE_Mesh ball = new HE_Mesh(new HEC_Sphere().setRadius(br).setUFacets(connectionFacets)
								.setVFacets(connectionFacets).setCenter(node));
						hullPoints.addAll(ball.getVertices());
						mesh.addVertices(ball);
					}
					final HEC_ConvexHull ch = new HEC_ConvexHull().setPointsFromVertices(hullPoints);
					try {
						final HE_Mesh tmp = new HE_Mesh(ch);
						final Map<Long, Integer> vertexToPointIndex = ch.vertexToPointIndex;
						final Iterator<HE_Face> tmpfItr = tmp.fItr();
						HE_Face f;
						HE_Halfedge tmphe;
						final ArrayList<HE_Face> facesToRemove = new ArrayList<HE_Face>();
						while (tmpfItr.hasNext()) {
							f = tmpfItr.next();
							f.setInternalLabel(2);
							tmphe = f.getHalfedge();
							int initid = vertexToPointIndex.get(tmphe.getVertex().key());
							boolean endface = initid < n;
							initid = initid / connectionFacets;
							do {
								final int id = vertexToPointIndex.get(tmphe.getVertex().key());
								endface = id / connectionFacets == initid && id < n;
								if (!endface) {
									break;
								}
								tmphe = tmphe.getNextInFace();
							} while (tmphe != f.getHalfedge());
							if (endface) {
								facesToRemove.add(f);
							}
						}
						for (int j = 0; j < facesToRemove.size(); j++) {
							tmp.deleteFace(facesToRemove.get(j));
						}
						tmp.cleanUnusedElementsByFace();
						tmp.uncapBoundaryHalfedges();
						for (int j = 0; j < connections.size(); j++) {
							int offset;
							if (node == connections.get(j).start()) {
								offset = connections.get(j).getIndex() * 2;
							} else {
								offset = connections.get(j).getIndex() * 2 + 1;
							}
							final WB_Vector v = connectionNodeConnections[offset].dir;
							v.mulSelf(connectionNodeConnections[offset].offset
									- connectionNodeConnections[offset].maxoffset);
							for (int k = 0; k < connectionFacets; k++) {
								connectionNodeConnections[offset].vertices.get(k).getPosition().addSelf(v);
							}
						}
						final Iterator<HE_Halfedge> tmpheItr = tmp.heItr();
						HE_Vertex tmpv;
						while (tmpheItr.hasNext()) {
							tmphe = tmpheItr.next();
							tmpv = tmphe.getVertex();
							final int j = vertexToPointIndex.get(tmpv.key());
							mesh.setVertex(tmphe, hullPoints.get(j));
							if (j >= n) {
								mesh.setHalfedge(hullPoints.get(j), tmphe);
							}
						}
						tmp.cleanUnusedElementsByFace();
						mesh.add(tmp);
					} catch (final Exception e) {
					}
				}
			}
			i++;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HEC_Creator#createBase()
	 */
	@Override
	protected HE_Mesh createBase() {
		mesh = new HE_Mesh();
		numberOfNodes = network.getNumberOfNodes();
		// System.out.println(numberOfNodes + " " +
		// frame.getNumberOfConnections());
		nodeTypes = new NodeType[numberOfNodes];
		getNodeTypes();
		numberOfConnections = network.getNumberOfConnections();
		connectionNodeConnections = new NodeConnection[2 * numberOfConnections];
		getNodeConnections();
		createVertices();
		createNodes();
		createConnections();
		mesh.pairHalfedges();
		if (!cap) {
			mesh.capHalfedges();
		}
		return mesh;
	}

	/**
	 *
	 *
	 * @param network
	 * @return
	 */
	public HEC_FromNetwork setNetwork(final WB_Network network) {

		this.network = network;
		return this;
	}

	/**
	 *
	 *
	 * @param mesh
	 * @return
	 */
	public HEC_FromNetwork setNetwork(final HE_Mesh mesh) {
		network = mesh.getNetwork();
		return this;
	}
}
