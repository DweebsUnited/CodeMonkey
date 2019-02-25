package CodeMonkey.project;

import java.util.ArrayList;

import CodeMonkey.neuron.Conducting;
import CodeMonkey.neuron.Driven;
import CodeMonkey.neuron.Insulating;
import CodeMonkey.neuron.Responsive;
import CodeMonkey.neuron.SpringNeuron;
import processing.core.PApplet;
import processing.core.PVector;

public class SquishyBrain extends ProjectBase {

  public static void main( String[] args ) {

    PApplet.main( "CodeMonkey.project.SquishyBrain" );

  }

  final int sWidth = 720;
  final int sHeigh = 640;

  ArrayList<SpringNeuron> ps = new ArrayList<SpringNeuron>( );

  private final float respM = 1.0f;
  private final float respm = -0.15f;

  @Override
  public void settings( ) {

    this.size( this.sWidth, this.sHeigh );
    this.setName( );

  }

  @Override
  public void setup( ) {
    
    // TODO: Poisson sample a bunch in the middle, plus the four corners fixed connected to the extremis

    // Set up some points and link
    SpringNeuron pa = new Driven( new PVector( this.pixelWidth / 2 - 50, this.pixelHeight / 2 ) );

    SpringNeuron pba = new Insulating( new PVector( this.pixelWidth / 2, this.pixelHeight / 2 - 50 ) );
    SpringNeuron pbb = new Conducting( new PVector( this.pixelWidth / 2, this.pixelHeight / 2 + 50 ) );

    SpringNeuron pc = new Responsive( new PVector( this.pixelWidth / 2 + 50, this.pixelHeight / 2 ) );

    pa.links.add( pba );
    pba.links.add( pa );
    pa.links.add( pbb );
    pbb.links.add( pa );

    pba.links.add( pc );
    pba.links.add( pbb );
    pbb.links.add( pc );
    pbb.links.add( pba );
    
    pc.links.add( pba );
    pc.links.add( pbb );

    this.ps.add( pa );
    this.ps.add( pba );
    this.ps.add( pbb );
    this.ps.add( pc );

  }

  @Override
  public void draw( ) {

    for( SpringNeuron p : this.ps )
      p.transmit( );

    for( SpringNeuron p : this.ps )
      p.update( );

    this.background( 0 );

    for( SpringNeuron p : this.ps ) {

      this.stroke( p.c( ) );
      this.fill( 255 * ( p.v * ( this.respM -  this.respm ) + this.respm ) );
      this.ellipse( p.p.x, p.p.y, 25, 25 );

    }

  }

}
