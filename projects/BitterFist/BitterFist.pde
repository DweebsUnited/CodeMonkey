import java.util.Random;
//import com.hamoid.*;

// Size of canvas
final int WIDE = 1280;
final int TALL = 720;

// How fast tpos can move each frame
final float P_SPD = 1;
// How often tpos will update it's velocity
final int T_UP = 5;
// Range for spring action
final float P_RNG = 16;
// Spring constants
final float P_SPRNG = 0.5;
final float P_RSPRG = 1.5;
// Framerate for output
final float F_RATE = 30;
final float T_STEP = 1.0 / ( F_RATE * F_RATE );

// Number of grid Elements
final int G_SIZE = 10;

// Opacities
// Lines between pos
final int O_COLR = 150;
// Lines between pos and tpos
final int O_CONN = 80;
// Lines between tpos
final int O_STRK = 40;
// Background fill
final int O_FILL = 10;

final Random rng = new Random( );
//VideoExport exporter;

ArrayList<Element> ps = new ArrayList<Element>( );

boolean drawing = true;

MinMaxColorFactory posColorFactory = new MinMaxColorFactory( 255, 255, 140, 215, 0, 0, O_COLR );
MinMaxColorFactory linkColorFactory = new MinMaxColorFactory( 128, 200, 0, 0, 0, 0, O_CONN );
MinMaxColorFactory targetColorFactory = new MinMaxColorFactory( 0, 0, 0, 0, 0, 0, O_STRK );

void setup( ) {
    GridCoordFactory coordFactory = new GridCoordFactory( G_SIZE, G_SIZE, WIDE, TALL );
  
    // There is a way to combine these two, but I'm not going to do it right now
    for( int jdx = 0; jdx < G_SIZE; ++jdx ) {
        for( int idx = 0; idx < G_SIZE; ++idx ) {
          
            PVector pos = coordFactory.make( idx, jdx );
            
            Element e = new Element( pos, targetColorFactory, posColorFactory, linkColorFactory );
          
            ps.add( e );
            
        }
    }
    
    for( int jdx = 0; jdx < G_SIZE; ++jdx ) {
        for( int idx = 0; idx < G_SIZE; ++idx ) {
            
            if( jdx != G_SIZE - 1 )
                ps.get( jdx * G_SIZE + idx ).link( ps.get( ( jdx + 1 ) * G_SIZE + idx ) );
            if( idx != G_SIZE - 1 )
                ps.get( jdx * G_SIZE + idx ).link( ps.get( jdx * G_SIZE + idx + 1 ) );
            
        }
    }

    size( 1280, 720 );
    background( 255, 255, 255, 255 );
    
    //exporter = new VideoExport( this, "BitterFist.mp4" );
    //exporter.setFrameRate( F_RATE );
    
    //exporter.startMovie( );
    
}

void draw( ) {
    
    if( drawing ) {
      
        // Decay background
        noStroke( );
        fill( 255, O_FILL );
        rect( 0, 0, WIDE, TALL );
        
        for( Element p : ps ) {
            p.draw( );
            p.step( );
        }

        //exporter.saveFrame( );
        
    }
         
}

void keyReleased( ) {
    
    if( key == ' ' ) {

        drawing = !drawing;
         
    } else if( key == 'q' ) {
      
        //exporter.endMovie( );
        exit( );
      
    }
}