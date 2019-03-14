import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

WB_Point[] points;
int numpoints;
HE_Mesh container;
HE_MeshCollection cells;
int numcells;
HE_Mesh fusedcells;

WB_Render3D render;

void setup() {
  size(1000, 1000, P3D);
  smooth(8);
  createContainer();
  numpoints=100;
  createMesh();
  render=new WB_Render(this);
}

void createContainer() {
  container=new HE_Mesh(new HEC_Geodesic().setB(2).setC(0).setRadius(300)); 
  container.add(new HE_Mesh(new HEC_Geodesic().setB(2).setC(0).setRadius(220)));
  HE_Mesh inner=new HE_Mesh(new HEC_Geodesic().setB(2).setC(0).setRadius(280));
  HET_MeshOp.flipFaces(inner);
  container.add(inner);
  inner=new HE_Mesh(new HEC_Geodesic().setB(2).setC(0).setRadius(200));
  HET_MeshOp.flipFaces(inner);
  container.add(inner);
  
  //container.modify(new HEM_Extrude().setDistance(150).setChamfer(0.5));
  HE_FaceIterator fitr=container.fItr();
  while (fitr.hasNext()) {
    fitr.next().setColor(color(0, 200, 50));
  }
}

void createMesh() {  
  // generate points
  points=new WB_Point[numpoints];
  WB_RandomPoint generator=new WB_RandomInSphere().setRadius(300);
  for (int i=0; i<numpoints; i++) {
    points[i]=generator.nextPoint();
  }

  // generate voronoi cells
  HEMC_VoronoiCells multiCreator=new HEMC_VoronoiCells().setPoints(points).setContainer(container).setOffset(0).setSimpleCap(false);
  cells=multiCreator.create();

  //color the cells
  int counter=0;
  HE_MeshIterator mItr=cells.mItr();
  HE_Mesh m;
  while (mItr.hasNext()) {
    m= mItr.next();
    m.setFaceColorWithOtherInternalLabel(color(255-2*counter, 220, 2*counter), -1);
    counter++;
  }

  numcells=cells.size();
  boolean[] isCellOn=new boolean[numcells];
  for (int i=0; i<numcells; i++) {
    isCellOn[i]=(random(100)<50);
  }

  //build new mesh from active cells

  HEC_FromVoronoiCells creator=new HEC_FromVoronoiCells().setCells(cells).setActive(isCellOn);
  fusedcells=new HE_Mesh(creator);

  
  //fusedcells.triangulate(HE_Selection.selectFacesWithOtherInternalLabel(fusedcells, -1));
  HE_Selection.selectFacesWithInternalLabel(fusedcells, -1).subdivide(new HES_CatmullClark(),2);
  //fusedcells.modify(new HEM_KeepLargestParts(1));
  //fusedcells.validate();
}

void draw() {
  background(50);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(width/2, height/2, 0);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  strokeWeight(1);
  stroke(0);
  render.drawEdges(fusedcells);
  noStroke();
  render.drawFacesFC(fusedcells);
}

void mousePressed() {
  createMesh();
}