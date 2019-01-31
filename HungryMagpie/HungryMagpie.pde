public final int cWidth = 640;

public static PApplet context;
public PImage img;
public PGraphics canvas;

public void setup( ) {

  size( 720, 640 );
  canvas = createGraphics( 720, 640 );
  
  context = this;

  // Load target image
  img = this.loadImage( "/Users/Ozzy/Documents/CodeMonkey/data/DSC_0088-3.jpg" );
  img.resize( this.cWidth, (int)Math.floor( img.height / (float)img.width * this.cWidth ) );
  img.loadPixels( );
  
}

public void draw( ) {

  this.image( img, 0, 0, this.pixelWidth, this.pixelHeight );
  
}

public void mouseClicked( ) {
  
  int cdx = mouseX + mouseY * img.width;

  int c = img.pixels[ cdx ];

  int r = ( c >> 16 ) & 0xFF;
  int g = ( c >> 8  ) & 0xFF;
  int b = ( c >> 0  ) & 0xFF;
  
  float hue = context.hue( c );
  float sat = canvas.saturation( c );
  float bri = canvas.brightness( c );
  
  System.out.println( String.format( "Stats for %d,%d: <%d,%d,%d> @ %f,%f,%f", mouseX, mouseY, r, g, b, hue, sat, bri ) );
  
}
