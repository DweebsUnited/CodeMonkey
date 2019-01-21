package CodeMonkey.project;

import CodeMonkey.spatial.HalfEdge.EdgeData;
import CodeMonkey.spatial.HalfEdge.HalfEdge;
import CodeMonkey.spatial.HalfEdge.Mesh;
import CodeMonkey.spatial.HalfEdge.VertexData;
import processing.core.PApplet;
import processing.core.PVector;

public class FullBottle extends ProjectBase {

  public static void main( String[ ] args ) {

    PApplet.main( "CodeMonkey.project.FullBottle" );

  }

  private Mesh<PVector, Object, Object> mesh;

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

      // Cycle edges, line each
      HalfEdge<PVector, Object, Object> he = vtx.he;
      if( he == null )
        continue;
      HalfEdge<PVector, Object, Object> first = he;
      do {

        this.line(
            he.vertexData.data.x,
            he.vertexData.data.y,
            he.pair.vertexData.data.x,
            he.pair.vertexData.data.y );

        he = he.prev.pair;

      } while( he != first );

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

      if( closest != null )
        this.mesh.exVertex( closest, m );
      else
        this.mesh.init( m );

    } else if( this.mouseButton == RIGHT ) {

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

      if( closest != null )
        this.mesh.exEdge( closest, m );
      else
        this.mesh.init( m );

    }

  }

}
