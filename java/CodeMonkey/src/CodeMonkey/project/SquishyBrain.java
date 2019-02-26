package CodeMonkey.project;

import java.util.ArrayList;
import java.util.Random;

import CodeMonkey.graph.Node;
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

  private static final float DELY_SQ_LEN_CUTOFF = 500;

  public static void main( String[] args ) {

    PApplet.main( "CodeMonkey.project.SquishyBrain" );

  }
  
  private Random rng = new Random( );

  final int sWidth = 720;
  final int sHeigh = 640;

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
    PoissonSampler ps = new PoissonSampler( 100, 100, 5 );
    
    // Add points to JSON file for triangulation
    JSONArray pts = new JSONArray( );

    // But set them up as we go
    for( PVector p : ps.sample ) {

      // Index and object
      int ndx = this.ns.size( );
      SpringNeuron sn;
      
      // Probabilities to become of each type
      float rf = rng.nextFloat( );
      if( rf < 0.25 ) {
        
        sn = new Conducting( p );
        
      } else if( rf < 0.5 ) {
        
        sn = new Insulating( p );
        
      } else if( rf < 0.75 ) {
        
        sn = new Responsive( p );
        
      } else if( rf < 0.875 ){
        
        sn = new Fixed( p );
        
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
    SpringNeuron sntl = new Fixed( new PVector( -10, -10 ) );
    SpringNeuron sntr = new Fixed( new PVector(  10, -10 ) );
    SpringNeuron snbl = new Fixed( new PVector( -10,  10 ) );
    SpringNeuron snbr = new Fixed( new PVector(  10,  10 ) );
    
    this.ns.add( sntl );
    this.ns.add( sntr );
    this.ns.add( snbl );
    this.ns.add( snbr );
    
    JSONObject pttl = new JSONObject( );
    pttl.setInt(   "id", ndxtl );
    pttl.setFloat( "x",  -10 );
    pttl.setFloat( "y",  -10 );
    pttl.setFloat( "z",    0 );
    pts.append( pttl );
    JSONObject pttr = new JSONObject( );
    pttr.setInt(   "id", ndxtl + 1 );
    pttr.setFloat( "x",   10 );
    pttr.setFloat( "y",  -10 );
    pttr.setFloat( "z",    0 );
    pts.append( pttr );
    JSONObject ptbl = new JSONObject( );
    ptbl.setInt(   "id", ndxtl + 2 );
    ptbl.setFloat( "x",  -10 );
    ptbl.setFloat( "y",   10 );
    ptbl.setFloat( "z",    0 );
    pts.append( ptbl );
    JSONObject ptbr = new JSONObject( );
    ptbr.setInt(   "id", ndxtl + 3 );
    ptbr.setFloat( "x",   10 );
    ptbr.setFloat( "y",   10 );
    ptbr.setFloat( "z",    0 );
    pts.append( ptbr );
    

    this.saveJSONArray( pts, dataDir + "SquishyBrain.json" );


    // Call TombstoneTriangulator
    try {

      Process p = Runtime.getRuntime( ).exec(
          new String[ ] {
              CM + "TombstoneTriangulator/TombstoneTriangulator",
              "t",
              dataDir + "SquisyBrain.json",
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

    for( int edx = 0; edx < edges.size( ); ++edx ) {

      JSONObject e = edges.getJSONObject( edx );

      int a = e.getInt( "a" );
      int b = e.getInt( "b" );

      SpringNeuron na = this.ns.get( a );
      SpringNeuron nb = this.ns.get( b );

      if( na.p.dist( nb.p ) < DELY_SQ_LEN_CUTOFF ) {
        
        na.link( nb );
        nb.link( na );
        
      }

    }

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
