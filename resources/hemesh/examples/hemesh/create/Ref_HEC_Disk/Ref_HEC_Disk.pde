import wblut.core.*;
import wblut.geom.*;
import wblut.hemesh.*;
import wblut.math.*;
import wblut.nurbs.*;
import wblut.processing.*;

WB_Render3D render;
WB_RandomPoint generator;
WB_Circle circle;
HE_Mesh mesh;

void setup() {
  size(1000, 1000, P3D);
  smooth(8);
  strokeWeight(2);
  noFill();
  render=new WB_Render3D(this);
  println(WB_Version.version());
  println(WB_Disclaimer.disclaimer());
  circle=new WB_Circle(WB_Point.ORIGIN(),new WB_Vector(random(-1.0,1.0),random(-1.0,1.0),random(-1.0,1.0)), 300.0);
  HEC_Disk dc=new HEC_Disk().setCircle(circle).setThickness(50.0);
  mesh=new HE_Mesh(dc);

  
}

void draw() {
  background(55);
  translate(width/2, height/2, 0);
  lights();
  rotateY(map(mouseX,0,width,-PI,PI));
  rotateX(map(mouseY,0,height,-PI,PI));
  noFill();
  strokeWeight(2);
  stroke(255,0,0);
  strokeWeight(1);
  stroke(100,0,0);
  render.drawEdges(mesh);
  stroke(255,0,0);
  render.drawGizmo(150);
  render.drawPlane(circle.getPlane(),340.0);
  noStroke();
  fill(255);
  render.drawFaces(mesh);
  
}

void mouseClicked(){
  circle=new WB_Circle(WB_Point.ORIGIN(),new WB_Vector(random(-1.0,1.0),random(-1.0,1.0),random(-1.0,1.0)), 300.0);
  HEC_Disk dc=new HEC_Disk().setCircle(circle).setThickness(50.0);
  mesh=new HE_Mesh(dc);
}