package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.hamoid.VideoExport;

import CodeMonkey.graph.Node;
import CodeMonkey.graph.UnDiGraph;
import CodeMonkey.spatial.PoissonSampler;
import processing.core.PApplet;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class SlidingRay extends PApplet {

  private class PathDrawer {

    private final static int N_TAIL   = 48;           // Number of points in the tail
    private final static int T_SEG    = 10;           // Number of tail points per segment
    private final static float SEG_DT = 1.0f / T_SEG; // DT for movement
    private final static int opaFalloffPow = 3;       // Power for opacity falloff

    private PApplet context;

    private ArrayList<Integer> path;
    private UnDiGraph<PVector> graph;

    // This will be a circular buffer
    private PVector[] tail = new PVector[ N_TAIL ];
    private int rC = 0;  // This will cap at N_TAIL
    private int oO = 0;  // Opa offset, so we can run them off the screen
    private int wH = -1; // This should always be the last one written to

    // This is the segment the head is currently progressing over
    private int currSeg = 0;
    private float currT = 0;

    // These are updated as we move, small optimization
    private PVector segSrc;
    private PVector segMid;
    private PVector segTgt;

    private PVector midA;
    private PVector midB;

    public PathDrawer( PApplet context, ArrayList<Integer> path, UnDiGraph<PVector> graph ) {

      this.context = context;
      this.path = path;
      this.graph = graph;

      // Set these up so step doesn't null pointer
      this.segSrc = this.graph.getNode( this.path.get( this.currSeg++ ) ).val;
      this.segMid = this.graph.getNode( this.path.get( this.currSeg++ ) ).val;
      this.segTgt = this.graph.getNode( this.path.get( this.currSeg   ) ).val;

      this.midA = PVector.lerp( this.segSrc, this.segMid, 0.5f );
      this.midB = PVector.lerp( this.segMid, this.segTgt, 0.5f );

    }

    public boolean step( ) {

      // If we need to move to next segment
      if( this.currT > 1.0f ) {

        // If we are now past last segment, let caller know
        if( this.currSeg == this.path.size( ) - 1 ) {

          this.oO++;

          if( this.oO == this.rC )
            return false;
          else
            return true;

        } else {

          // Move to next
          this.currSeg++;

          // Get new bezier control points
          this.segSrc = this.segMid;
          this.segMid = this.segTgt;
          this.segTgt = this.graph.getNode( this.path.get( this.currSeg ) ).val;

          this.midA = this.midB;
          this.midB = PVector.lerp( this.segMid, this.segTgt, 0.5f );

          this.currT -= 1.0f;

        }

      }

      // Calculate new head point
      PVector head = new PVector(
          this.context.bezierPoint( this.midA.x, this.segMid.x, this.segMid.x, this.midB.x, this.currT ),
          this.context.bezierPoint( this.midA.y, this.segMid.y, this.segMid.y, this.midB.y, this.currT )
          );

      // Add to buffer
      this.wH++;
      if( this.wH == N_TAIL )
        this.wH = 0;

      this.tail[ this.wH ] = head;
      this.rC = this.rC == N_TAIL ? this.rC : this.rC + 1;

      // And update time
      this.currT += SEG_DT;

      return true;

    }

    public void draw( ) {

      if( this.rC < 2 )
        return;

      this.context.noFill( );
      this.context.strokeWeight( 2 );

      // Draw points in buffer with falloff opacity
      for( int ddx = 0; ddx < this.rC - 1; ++ddx ) {

        // Have to go backwards
        int cd = this.wH - ddx;
        // And wrap
        cd = cd < 0 ? cd + N_TAIL : cd;

        // And wrap
        int nd = cd == 0 ? N_TAIL - 1 : cd - 1;

        // Opa fall off = 1 - x^k
        this.context.stroke( 255, (int) Math.round( 255 * ( 1 - Math.pow( ( ddx + this.oO ) / (float)N_TAIL, opaFalloffPow ) ) ) );

        // Draw from cd to nd
        this.context.line( this.tail[ cd ].x, this.tail[ cd ].y, this.tail[ nd ].x, this.tail[ nd ].y );

      }

    }

  }

  private static String CM = "/Users/ozzy/Documents/CodeMonkey/";
  private static String dataDir = CM + "data/";

  private final static float POISS_MIN_DIST = 25;
  private final static float DELY_LEN_CUTOFF = 2 * POISS_MIN_DIST;

  private Random rng = new Random( );

  private PoissonSampler samp;
  private UnDiGraph<PVector> graph;

  private ArrayList<PathDrawer> drawers;
  private boolean spawning = true;
  private int nSpawnCounter = 10;
  private float spawnLambda = 1.0f / 25f;

  private VideoExport videoExport;

  public static void main( String [ ] args ) {

    PApplet.main( "CodeMonkey.project.SlidingRay" );

  }

  @Override
  public void settings( ) {

    this.size( 720, 640 );

  }

  @Override
  public void setup( ) {

    this.background( 0 );


    // Set up our data structures
    this.graph = new UnDiGraph<PVector>( );
    this.drawers = new ArrayList<PathDrawer>( );


    // Construct a new poisson distribution
    this.samp = new PoissonSampler( this.pixelWidth, this.pixelHeight, POISS_MIN_DIST );


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
              "z",
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

    // Set up video exporter
    this.videoExport = new VideoExport( this, dataDir + "SlidingRay.mp4" );
    this.videoExport.startMovie( );

  }

  @Override
  public void draw( ) {

    // Ceter the grid better
    this.scale( ( this.pixelWidth - 10 ) / this.samp.width, ( this.pixelHeight - 10 ) / this.samp.height );
    this.translate( 5, 5 );


    this.background( 0 );

    // Draw delaunay

    this.stroke( 127, 64 );
    this.strokeWeight( 1 );
    this.noFill( );

    for( Node<PVector> n : this.graph.nodes ) {

      for( Node<PVector> tgt : n.links )
        this.line( n.val.x, n.val.y, tgt.val.x, tgt.val.y );

    }

    // Draw nodes

    this.noStroke( );
    this.fill( 255, 64 );

    for( Node<PVector> n : this.graph.nodes ) {

      this.ellipse( n.val.x, n.val.y, 5, 5 );

    }

    // Update the drawers

    Iterator<PathDrawer> drawIter = this.drawers.iterator( );
    while( drawIter.hasNext( ) ) {

      PathDrawer pd = drawIter.next( );

      if( !pd.step( ) )
        drawIter.remove( );
      else
        pd.draw( );

    }

    // Poisson spawning
    if( this.nSpawnCounter == 0 ) {

      if( this.spawning )
        this.drawers.add( new PathDrawer( this, this.newPath( ), this.graph ) );

      this.nSpawnCounter = (int)Math.round( - Math.log( 1.0 - this.rng.nextFloat( ) ) / this.spawnLambda );

    } else
      --this.nSpawnCounter;

    // Save videoframe
    this.videoExport.saveFrame( );

  }

  @Override
  public void keyPressed( ) {

    if( this.key == ' ' ) {
      this.save( dataDir + "SlidingRay.png" );
    } else if( this.key == 'z' ) {
      this.spawning = !this.spawning;
    } else if( this.key == 'x' ) {
      this.drawers.add( new PathDrawer( this, this.newPath( ), this.graph ) );
    } else if( this.key == 'q' ) {
      this.videoExport.endMovie( );
      this.exit( );
    }

  }

  private ArrayList<Integer> newPath( ) {

    // TODO: x random midpoints?

    // Pick random points offscreen on opposite sides, use closest point to each as endpts
    //  TODO: KD Tree, O(n) is bad, but O(log n) is better..

    PVector endA = new PVector( -1, this.rng.nextFloat( ) * this.pixelHeight );
    PVector endB = new PVector( this.pixelWidth + 1, this.rng.nextFloat( ) * this.pixelHeight );

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
