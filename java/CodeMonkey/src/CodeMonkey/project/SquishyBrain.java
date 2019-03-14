package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.neuron.Conducting;
import CodeMonkey.neuron.Driven;
import CodeMonkey.neuron.Fixed;
import CodeMonkey.neuron.Insulating;
import CodeMonkey.neuron.Responsive;
import CodeMonkey.neuron.SpringNeuron;
import CodeMonkey.spatial.PoissonSampler;
import processing.core.PApplet;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class SquishyBrain extends ProjectBase {

  public static void main( String[] args ) {

    PApplet.main( "CodeMonkey.project.SquishyBrain" );

  }

  private Random rng = new Random( );

  private final int sWidth = 720;
  private final int sHeigh = 640;

  private final float DELY_SQ_LEN_CUTOFF = max( this.sWidth, this.sHeigh ) / 2;

  ArrayList<SpringNeuron> ns = new ArrayList<SpringNeuron>( );

  private final float respM = 1.0f;
  private final float respm = -0.15f;

  @Override
  public void settings( ) {

    this.size( this.sWidth, this.sHeigh );
    this.setName( );

  }

  @Override
  public void setup( ) {

    // Poisson sample a bunch in the middle, plus the four corners fixed connected to the extremis
    PoissonSampler ps = new PoissonSampler( this.sWidth * 3f / 4, this.sHeigh * 3f / 4, 50 );
    PVector offset = new PVector( this.sWidth / 8f, this.sHeigh / 8f );

    // Add points to JSON file for triangulation
    JSONArray pts = new JSONArray( );

    // But set them up as we go
    for( PVector p : ps.sample ) {

      p.add( offset );

      // Index and object
      int ndx = this.ns.size( );
      SpringNeuron sn;

      // Probabilities to become of each type
      float rf = this.rng.nextFloat( );
      if( rf < 0.25 ) {

        sn = new Conducting( p );

      } else if( rf < 0.5 ) {

        sn = new Insulating( p );

      } else if( rf < 0.75 ) {

        sn = new Responsive( p );

      } else if( rf < 0.875 ){

        sn = new Conducting( p );

      } else {

        sn = new Driven( p );

      }

      // Add to the list
      this.ns.add( sn );

      // Now add to the JSON list
      JSONObject pt = new JSONObject( );

      pt.setInt(   "id", ndx );
      pt.setFloat( "x",  p.x );
      pt.setFloat( "y",  p.y );
      pt.setFloat( "z",  p.z );

      pts.append( pt );

    }

    // Hardcoded, add in a few fixed at the corners to keep it from flying off
    int ndxtl = this.ns.size( );
    SpringNeuron sntl = new Fixed( new PVector( this.sWidth / 8f * 7 + 15, this.sHeigh / 8f * 7 + 15 ) );
    SpringNeuron sntr = new Fixed( new PVector( this.sWidth / 8f - 15,     this.sHeigh / 8f * 7 + 15 ) );
    SpringNeuron snbl = new Fixed( new PVector( this.sWidth / 8f * 7 + 15, this.sHeigh / 8f - 15     ) );
    SpringNeuron snbr = new Fixed( new PVector( this.sWidth / 8f - 15,     this.sHeigh / 8f - 15     ) );

    this.ns.add( sntl );
    this.ns.add( sntr );
    this.ns.add( snbl );
    this.ns.add( snbr );

    for( int cdx = 0; cdx < 4; ++cdx ) {

      SpringNeuron sn = this.ns.get( ndxtl + cdx );

      JSONObject pt = new JSONObject( );

      pt.setInt(   "id", ndxtl + cdx );
      pt.setFloat( "x",  sn.p.x );
      pt.setFloat( "y",  sn.p.y );
      pt.setFloat( "z",  sn.p.z );

      pts.append( pt );

    }


    this.saveJSONArray( pts, dataDir + "SquishyBrain.json" );


    // Call TombstoneTriangulator
    try {

      Process p = Runtime.getRuntime( ).exec(
          new String[ ] {
              CM + "TombstoneTriangulator/TombstoneTriangulator",
              "z",
              dataDir + "SquishyBrain.json",
              dataDir + "SBTriang.json"
          } );

      p.waitFor( );

    } catch( Exception e ) {

      System.out.println( "Couldn't call TT" );
      System.exit( 64 );

    }

    // Read triangulation JSON
    JSONObject triangulation = this.loadJSONObject( dataDir + "SBTriang.json" );


    // Add JSON links to digraph
    //   Cutoff length, so we don't get an awkward hull
    JSONArray edges = triangulation.getJSONArray( "edges" );

    int eCount = 0;
    float dAccum = 0;

    for( int edx = 0; edx < edges.size( ); ++edx ) {

      JSONObject e = edges.getJSONObject( edx );

      int a = e.getInt( "a" );
      int b = e.getInt( "b" );

      SpringNeuron na = this.ns.get( a );
      SpringNeuron nb = this.ns.get( b );

      dAccum += na.p.dist( nb.p );
      eCount += 1;

      if( na.p.dist( nb.p ) < this.DELY_SQ_LEN_CUTOFF ) {

        na.link( nb );
        nb.link( na );

      }

    }

    System.out.println( String.format( "Avg Len: %f", dAccum / eCount ) );

  }

  @Override
  public void draw( ) {

    for( SpringNeuron p : this.ns )
      p.transmit( );

    for( SpringNeuron p : this.ns )
      p.update( );

    this.background( 0 );

    for( SpringNeuron p : this.ns ) {

      this.stroke( p.c( ) );
      this.fill( 255 * ( p.v * ( this.respM -  this.respm ) + this.respm ) );
      this.ellipse( p.p.x, p.p.y, 25, 25 );

    }

  }

}
