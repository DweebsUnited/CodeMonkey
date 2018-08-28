package CodeMonkey.project;

import java.util.ArrayList;

import CodeMonkey.graph.Node;
import CodeMonkey.graph.UnDiGraph;
import processing.core.PApplet;
import processing.core.PVector;

class PathDrawer {

  private final static int N_TAIL   = 48;           // Number of points in the tail
  private final static int T_SEG    = 10;           // Number of tail points per segment
  private final static float SEG_DT = 1.0f / T_SEG; // DT for movement
  private final static int opaFalloffPow = 3;       // Power for opacity falloff

  // Non-Thread-Safe falloff factors, to save N_TAIL exp per path per frame
  private static int[] falloffFac = null;

  private PApplet context;

  private int color;

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
  private Node<PVector> segSrcN;
  private Node<PVector> segMidN;
  private Node<PVector> segTgtN;

  private PVector segSrc;
  private PVector segMid;
  private PVector segTgt;

  private PVector midA;
  private PVector midB;

  public PathDrawer( int color, PApplet context, ArrayList<Integer> path, UnDiGraph<PVector> graph ) {

    // Set up falloffFac, will only be done once
    if( falloffFac == null ) {

      falloffFac = new int[ N_TAIL ];

      for( int fodx = 0; fodx < N_TAIL; ++fodx ) {

        falloffFac[ fodx ] = (int) Math.round( 255 * ( 1 - Math.pow( ( fodx + this.oO ) / (float)N_TAIL, opaFalloffPow ) ) );

      }

    }

    this.color = color;
    this.context = context;
    this.path = path;
    this.graph = graph;

    // Set these up so step doesn't null pointer
    this.segSrcN = this.graph.getNode( this.path.get( this.currSeg++ ) );
    this.segMidN = this.graph.getNode( this.path.get( this.currSeg++ ) );
    this.segTgtN = this.graph.getNode( this.path.get( this.currSeg   ) );

    this.segSrc = this.segSrcN.val;
    this.segMid = this.segMidN.val;
    this.segTgt = this.segTgtN.val;

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

        // Get new bezier control points incrementally
        this.segSrcN = this.segMidN;
        this.segMidN = this.segTgtN;
        this.segTgtN = this.graph.getNode( this.path.get( this.currSeg ) );

        this.segSrc = this.segMid;
        this.segMid = this.segTgt;
        this.segTgt = this.segTgtN.val;

        this.midA = this.midB;
        this.midB = PVector.lerp( this.segMid, this.segTgt, 0.5f );

        this.currT -= 1.0f;

      }

    }

    // Calculate new head point
    PVector head = new PVector(
        this.context.bezierPoint( this.midA.x, this.segMid.x, this.segMid.x, this.midB.x, this.currT ),
        this.context.bezierPoint( this.midA.y, this.segMid.y, this.segMid.y, this.midB.y, this.currT ),
        this.context.bezierPoint( this.midA.z, this.segMid.z, this.segMid.z, this.midB.z, this.currT )
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

    // TODO: draw delaunay links out from currSeg s: -0, -1, -2
    //   Phase brightness by currT ( 0 -> 1 )
    //   And falloff ( 0 -> 1 )
    //   Superexponential! WOOOOOOOO
    //   ? Phase with very limited noise

    // We do this before the actual path to give a 2 frame warning that something is about to spawn
    // Bit of a mental queue that something cometh

    //    this.context.stroke( 255, 32 * this.currT );
    //
    //    // Calculate a falloff for the non-mids
    //    for( Node<PVector> l : this.segMidN.links ) {
    //
    //      this.context.line(
    //          this.segMid.x,  this.segMid.y,  this.segMid.z,
    //          l.val.x,        l.val.y,        l.val.z );
    //
    //    }

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
      this.context.stroke( this.color, falloffFac[ ddx ] );

      // Draw from cd to nd
      this.context.line(
          this.tail[ cd ].x,
          this.tail[ cd ].y,
          this.tail[ cd ].z,
          this.tail[ nd ].x,
          this.tail[ nd ].y,
          this.tail[ nd ].z );

    }

  }

}