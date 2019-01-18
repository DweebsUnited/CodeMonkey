package CodeMonkey.spatial.halfedge;

public class HalfData<VD, ED, PD> {

  HalfData next;
  HalfData prev;
  HalfData pair;

  VD vertexData;
  ED edgeData;
  PD polygonData;

}
