import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

WB_BinaryGrid3D grid;
WB_Render render;
int sizeX, sizeY, sizeZ;
float dX,dY,dZ;
void setup() {
  size(1000,1000,P3D);
  smooth(8);
  sizeX=50;
  dX=10.0;
  sizeY=50;
  dY=10.0;
  sizeZ=50;
  dZ=10.0;
  grid=WB_BinaryGrid3D.createGrid(new WB_Point(),sizeX,dX,sizeY,dY,sizeZ,dZ);
  for(int i=0;i<sizeX;i++){
     for(int j=0;j<sizeY;j++){
        for(int k=0;k<sizeZ;k++){
          if(noise(0.05*(i-sizeX*0.5+0.5),0.05*(j-sizeY*0.5+0.5),0.05*(k-sizeZ*0.5+0.5))>0.5) grid.set(i,j,k);
        }
     }
  }
  render=new WB_Render(this);
}

void draw() {
  background(55);
  translate(width/2, height/2);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  noStroke();
  render.draw(grid);
  stroke(0);
  render.drawOutline(grid);
}