package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.hamoid.VideoExport;

import CodeMonkey.graph.Node;
import CodeMonkey.graph.UnDiGraph;
import CodeMonkey.spatial.PoissonSampler3D;
import CodeMonkey.utility.PathDrawer;
import processing.core.PApplet;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class SlidingRay extends PApplet {

  private static boolean saving = false;

  private static String CM = "/Users/ozzy/Documents/CodeMonkey/";
  private static String dataDir = CM + "data/";

  private final static float POISS_MIN_DIST = 50;
  private final static float DELY_LEN_CUTOFF = 2 * POISS_MIN_DIST;
  private final static float SPWN_FRM_MIN = 15;
  private final static float SPWN_FRM_MAX = 50;

  private Random rng = new Random( );

  private PoissonSampler3D samp;
  private UnDiGraph<PVector> graph;

  private ArrayList<PathDrawer> drawers;
  private boolean spawning = true;
  private int nSpawnCounter = 10;

  private VideoExport videoExport;

  public static void main( String [ ] args ) {

    PApplet.main( "CodeMonkey.project.SlidingRay" );

  }

  @Override
  public void settings( ) {

    this.size( 720, 640, P3D );
    //    this.size( 1920, 1080, P3D );

  }

  @Override
  public void setup( ) {

    this.background( 0 );


    // Set up our data structures
    this.graph = new UnDiGraph<PVector>( );
    this.drawers = new ArrayList<PathDrawer>( );


    // Construct a new poisson distribution
    this.samp = new PoissonSampler3D( 1024, 1024, 1024, POISS_MIN_DIST );


    // Output points to JSON file and add them to the graph
    JSONArray pts = new JSONArray( );

    for( PVector p : this.samp.sample ) {

      int pdx = this.graph.newNode( p );

      JSONObject pt = new JSONObject( );

      pt.setInt(   "id", pdx );
      pt.setFloat( "x",  p.x );
      pt.setFloat( "y",  p.y );
      pt.setFloat( "z",  p.z );

      pts.append( pt );

    }

    this.saveJSONArray( pts, dataDir + "SlidingRay.json" );


    // Call TombstoneTriangulator
    try {

      Process p = Runtime.getRuntime( ).exec(
          new String[ ] {
              CM + "TombstoneTriangulator/TombstoneTriangulator",
              "t",
              dataDir + "SlidingRay.json",
              dataDir + "triangulation.json"
          } );

      p.waitFor( );

    } catch( Exception e ) {

      System.out.println( "Couldn't call TT" );
      this.exit( );

    }


    // Read triangulation JSON
    JSONObject triangulation = this.loadJSONObject( dataDir + "triangulation.json" );


    // Add JSON links to digraph
    //   Cutoff length, so we don't get an awkward hull
    JSONArray edges = triangulation.getJSONArray( "edges" );
    for( int edx = 0; edx < edges.size( ); ++edx ) {

      JSONObject e = edges.getJSONObject( edx );

      int a = e.getInt( "a" );
      int b = e.getInt( "b" );

      Node<PVector> na = this.graph.getNode( a );
      Node<PVector> nb = this.graph.getNode( b );

      if( na.val.dist( nb.val ) < DELY_LEN_CUTOFF )
        this.graph.link( a, b );

    }

    if( saving ) {
      // Set up video exporter
      this.videoExport = new VideoExport( this, dataDir + "SlidingRay.mp4" );
      this.videoExport.startMovie( );
    }

  }

  @Override
  public void draw( ) {

    this.camera(
        2000 * (float)Math.cos( ( this.frameCount % 8000 ) / 8000f * 2 * Math.PI ),
        2000 * (float)Math.sin( ( this.frameCount % 8000 ) / 8000f * 2 * Math.PI ),
        2000,

        this.samp.width / 2, this.samp.height / 2, this.samp.depth / 2,
        0, 0, -1 );

    float fac = 0.85f;
    this.ortho(
        -this.samp.width * fac,
        this.samp.width * fac,
        -this.samp.height * fac,
        this.samp.height * fac );

    this.background( 0 );


    // Draw BB
    // Axis get a little skewed here from the sampler

    //    this.stroke( 255, 0, 0 );
    //    this.noFill( );
    //    // Bottom front x
    //    this.line(
    //        0,                0,                0,
    //        this.samp.width,  0,                0 );
    //    // Bottom back x
    //    this.line(
    //        0,                this.samp.height, 0,
    //        this.samp.width,  this.samp.height, 0 );
    //    // Top front x
    //    this.line(
    //        0,                0,                this.samp.depth,
    //        this.samp.width,  0,                this.samp.depth );
    //    // Top back x
    //    this.line(
    //        0,                this.samp.height, this.samp.depth,
    //        this.samp.width,  this.samp.height, this.samp.depth );
    //    // Left bottom
    //    this.line(
    //        0,                0,                0,
    //        0,                this.samp.height, 0 );
    //    // Right bottom
    //    this.line(
    //        this.samp.width,  0,                0,
    //        this.samp.width,  this.samp.height, 0 );
    //    // Left top
    //    this.line(
    //        0,                0,                this.samp.depth,
    //        0,                this.samp.height, this.samp.depth );
    //    // Right top
    //    this.line(
    //        this.samp.width,  0,                this.samp.depth,
    //        this.samp.width,  this.samp.height, this.samp.depth );
    //    // Front left pillar
    //    this.line(
    //        0,                0,                0,
    //        0,                0,                this.samp.depth );
    //    // Front right pillar
    //    this.line(
    //        this.samp.width,  0,                0,
    //        this.samp.width,  0,                this.samp.depth );
    //    // Back left pillar
    //    this.line(
    //        0,                this.samp.height, 0,
    //        0,                this.samp.height, this.samp.depth );
    //    // Back right pillar
    //    this.line(
    //        this.samp.width,  this.samp.height, 0,
    //        this.samp.width,  this.samp.height, this.samp.depth );


    // Draw delaunay

    //    this.stroke( 127, 32 );
    //    this.strokeWeight( 1 );
    //    this.noFill( );
    //
    //    for( Node<PVector> n : this.graph.nodes ) {
    //
    //      for( Node<PVector> tgt : n.links )
    //        this.line( n.val.x, n.val.y, n.val.z, tgt.val.x, tgt.val.y, tgt.val.z );
    //
    //    }

    // Draw nodes

    //    this.noStroke( );
    //    this.fill( 255, 64 );
    //
    //    for( Node<PVector> n : this.graph.nodes ) {
    //
    //      this.ellipse( n.val.x, n.val.y, 5, 5 );
    //
    //    }

    // Update the drawers

    Iterator<PathDrawer> drawIter = this.drawers.iterator( );
    while( drawIter.hasNext( ) ) {

      PathDrawer pd = drawIter.next( );

      if( !pd.step( ) )
        drawIter.remove( );
      else
        pd.draw( ( this.frameCount % 60 ) / 60f );

    }

    // Poisson spawning
    if( this.nSpawnCounter == 0 ) {

      if( this.spawning )
        this.spawnNewDrawer( );

      float frmFac = this.noise( this.frameCount / 1024f ) * ( SPWN_FRM_MAX - SPWN_FRM_MIN ) + SPWN_FRM_MIN;

      this.nSpawnCounter = (int)Math.round( - Math.log( 1.0f - this.rng.nextFloat( ) ) * frmFac );

    } else
      --this.nSpawnCounter;

    if( saving ) {
      // Save videoframe
      this.videoExport.saveFrame( );
    }

  }

  @Override
  public void keyPressed( ) {

    if( this.key == ' ' ) {
      this.save( dataDir + "SlidingRay.png" );
    } else if( this.key == 'z' ) {
      this.spawning = !this.spawning;
    } else if( this.key == 'x' ) {
      this.spawnNewDrawer( );
    } else if( this.key == 'q' ) {
      this.videoExport.endMovie( );
      this.exit( );
    }

  }

  private void spawnNewDrawer( ) {

    float ax = this.rng.nextFloat( );

    if( ax < 0.33f ) {
      this.drawers.add( new PathDrawer( this.color( 234,  88, 100 ), this, this.newPathX( ), this.graph ) );
    } else if( ax < 0.66f ) {
      this.drawers.add( new PathDrawer( this.color( 249, 220,  92 ), this, this.newPathY( ), this.graph ) );
    } else {
      this.drawers.add( new PathDrawer( this.color(  67, 144, 252 ), this, this.newPathZ( ), this.graph ) );
    }

  }

  private ArrayList<Integer> newPathX( ) {

    // Pick random points offscreen on opposite sides, use closest point to each as endpts

    boolean dir = this.rng.nextFloat( ) > 0.5f;

    PVector endA = new PVector(
        dir ? -1 : this.samp.width + 1,
            this.rng.nextFloat( ) * this.samp.height,
            this.rng.nextFloat( ) * this.samp.depth );
    PVector endB = new PVector(
        dir ? this.samp.width + 1 : -1,
            this.rng.nextFloat( ) * this.samp.height,
            this.rng.nextFloat( ) * this.samp.depth );

    float dA = Float.POSITIVE_INFINITY;
    int closeA = -1;
    float dB = Float.POSITIVE_INFINITY;
    int closeB = -1;

    for( Node<PVector> n : this.graph.nodes ) {

      float distA = endA.dist( n.val );
      float distB = endB.dist( n.val );

      if( distA < dA ) {

        dA = distA;
        closeA = n.ID;

      }

      if( distB < dB ) {

        dB = distB;
        closeB = n.ID;

      }

    }

    return this.graph.greedyPath( closeA, closeB, ( a, b ) -> { return a.dist( b ); } );

  }

  private ArrayList<Integer> newPathY( ) {

    // Pick random points offscreen on opposite sides, use closest point to each as endpts

    boolean dir = this.rng.nextFloat( ) > 0.5f;

    PVector endA = new PVector(
        this.rng.nextFloat( ) * this.samp.width,
        dir ? -1 : this.samp.height + 1,
            this.rng.nextFloat( ) * this.samp.depth );
    PVector endB = new PVector(
        this.rng.nextFloat( ) * this.samp.width,
        dir ? this.samp.height + 1 : -1,
            this.rng.nextFloat( ) * this.samp.depth );

    float dA = Float.POSITIVE_INFINITY;
    int closeA = -1;
    float dB = Float.POSITIVE_INFINITY;
    int closeB = -1;

    for( Node<PVector> n : this.graph.nodes ) {

      float distA = endA.dist( n.val );
      float distB = endB.dist( n.val );

      if( distA < dA ) {

        dA = distA;
        closeA = n.ID;

      }

      if( distB < dB ) {

        dB = distB;
        closeB = n.ID;

      }

    }

    return this.graph.greedyPath( closeA, closeB, ( a, b ) -> { return a.dist( b ); } );

  }

  private ArrayList<Integer> newPathZ( ) {

    // Pick random points offscreen on opposite sides, use closest point to each as endpts

    boolean dir = this.rng.nextFloat( ) > 0.5f;

    PVector endA = new PVector(
        this.rng.nextFloat( ) * this.samp.width,
        this.rng.nextFloat( ) * this.samp.height,
        dir ? -1 : this.samp.depth + 1 );
    PVector endB = new PVector(
        this.rng.nextFloat( ) * this.samp.width,
        this.rng.nextFloat( ) * this.samp.height,
        dir ? this.samp.depth + 1 : -1 );

    float dA = Float.POSITIVE_INFINITY;
    int closeA = -1;
    float dB = Float.POSITIVE_INFINITY;
    int closeB = -1;

    for( Node<PVector> n : this.graph.nodes ) {

      float distA = endA.dist( n.val );
      float distB = endB.dist( n.val );

      if( distA < dA ) {

        dA = distA;
        closeA = n.ID;

      }

      if( distB < dB ) {

        dB = distB;
        closeB = n.ID;

      }

    }

    return this.graph.greedyPath( closeA, closeB, ( a, b ) -> { return a.dist( b ); } );

  }

}
