import java.util.List;

// Make a canvas
PImage canvas = createImage( 640, 480, RGB );

// Make a camera
PVector eye = new PVector( -10, -10, 4 );
PVector center = new PVector( 0.0, 0.0, 0.5 );
PVector up = new PVector( 0, 0, 1 );
  
Camera cam = new Camera( 640.0 / 480, 1.0, 0.25 );

// Set up scene
List<Intersectable> scene = new ArrayList<Intersectable>( );

void setup( ) {
  
  // Do'h
  size( 640, 480 );
  
  // Set up scene
  scene.add( new AABB( new PVector( 0, 0, 0 ), 1 ) );
  
  scene.add( new AABB( new PVector( -1, -1, -1 ), 1 ) );
  scene.add( new AABB( new PVector( -1, -1, 1 ), 1 ) );
  scene.add( new AABB( new PVector( -1, 1, -1 ), 1 ) );
  scene.add( new AABB( new PVector( -1, 1, 1 ), 1 ) );
  scene.add( new AABB( new PVector( 1, -1, -1 ), 1 ) );
  scene.add( new AABB( new PVector( 1, -1, 1 ), 1 ) );
  scene.add( new AABB( new PVector( 1, 1, -1 ), 1 ) );
  scene.add( new AABB( new PVector( 1, 1, 1 ), 1 ) );
  
}

void draw( ) {
  
  PVector camLoc = new PVector( eye.x, eye.y );
  camLoc.rotate( PI / 180.0 );
  eye.set( camLoc.x, camLoc.y, eye.z );
  
  // Move camera
  cam.lookAt( eye, center, up );
  
  // Run the cast!
  cam.cast( canvas, scene );
  
  // Output the image
  image( canvas, 0, 0, pixelWidth, pixelHeight );
  
}
