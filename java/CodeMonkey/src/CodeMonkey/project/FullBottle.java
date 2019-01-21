package CodeMonkey.project;

import CodeMonkey.spatial.HalfEdge.EdgeData;
import CodeMonkey.spatial.HalfEdge.Mesh;
import CodeMonkey.spatial.HalfEdge.VertexData;
import processing.core.PApplet;
import processing.core.PVector;

public class FullBottle extends ProjectBase {

  public static void main( String[ ] args ) {

    PApplet.main( "CodeMonkey.project.FullBottle" );

  }

  private static final float PICKOFF = 10f;

  private Mesh<PVector, Object, Object> mesh;
  private VertexData<PVector> active;

  @Override
  public void settings( ) {

    this.size( 720, 640 );
    this.setName( );

  }

  @Override
  public void setup( ) {

    this.mesh = new Mesh<PVector, Object, Object>( );

  }

  @Override
  public void draw( ) {

    this.background( 255 );
    this.noFill( );
    this.stroke( 0 );
    for( VertexData<PVector> vtx : this.mesh.vds ) {

      this.ellipse( vtx.data.x, vtx.data.y, 5, 5 );

    }

    if( this.active != null ) {

      this.fill( 255, 0, 0 );
      this.noStroke( );
      this.ellipse( this.active.data.x, this.active.data.y, 5, 5 );

    }

    this.noFill( );
    this.stroke( 0 );
    for( EdgeData<Object> etx : this.mesh.eds ) {

      VertexData<PVector> vsx = etx.he.vertexData;
      VertexData<PVector> vtx = etx.he.pair.vertexData;

      this.line(
          vsx.data.x,
          vsx.data.y,
          vtx.data.x,
          vtx.data.y );

    }

  }

  @Override
  public void mouseClicked( ) {

    PVector m = new PVector( this.mouseX, this.mouseY );

    if( this.mouseButton == LEFT ) {

      // Find closest Vertex
      VertexData<PVector> closest = null;
      float d = Float.POSITIVE_INFINITY;

      for( VertexData<PVector> vtx : this.mesh.vds ) {

        float dd = PVector.dist( vtx.data, m );

        if( dd < d ) {

          d = dd;
          closest = vtx;

        }

      }

      if( d < PICKOFF ) {

        // Pick a point - If one already picked, if same null, else connect
        if( this.active == null )
          this.active = closest;
        else if( this.active == closest )
          this.active = null;
        else {

          this.mesh.connect( this.active, closest );
          this.active = null;

        }

      } else {

        if( this.active != null )
          this.active = null;
        else {

          if( closest != null )
            this.mesh.connect( closest, m );
          else
            this.mesh.init( m );

        }

      }

    } else if( this.mouseButton == RIGHT ) {

      // Reset active picked
      if( this.active != null ) {
        this.active = null;
      } else {

        // Find closest Edge
        EdgeData<Object> closest = null;
        float d = Float.POSITIVE_INFINITY;

        for( EdgeData<Object> etx : this.mesh.eds ) {

          PVector epv = new PVector( );
          epv.set( (PVector) etx.he.vertexData.data );
          epv.add( (PVector) etx.he.pair.vertexData.data );
          epv.mult( 0.5f );
          float dd = PVector.dist( epv, m );

          if( dd < d ) {

            d = dd;
            closest = etx;

          }

        }

        if( closest != null ) {
          this.mesh.connect( closest.he.vertexData, m );
          this.mesh.connect( closest.he.pair.vertexData, m );
        } else
          this.mesh.init( m );

      }

    }

  }

}
