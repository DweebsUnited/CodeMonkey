package CodeMonkey.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Random;

public class RandomQueue<T> implements Queue<T> {

  private ArrayList<T> queue;
  private Random rng;

  private void shuffle( Object[] arr ) {

    for( int qdx = this.queue.size( ) - 1; qdx > 0; --qdx ) {

      int sdx = this.rng.nextInt( qdx + 1 );

      Object temp = arr[ sdx ];
      arr[ sdx ] = arr[ qdx ];
      arr[ qdx ] = temp;

    }

  }

  public RandomQueue( ) {

    this.queue = new ArrayList<T>( );
    this.rng = new Random( );

  }

  @Override
  public int size( ) {

    return this.queue.size( );

  }

  @Override
  public boolean isEmpty( ) {

    return this.queue.isEmpty( );

  }

  @Override
  public boolean contains( Object o ) {

    return this.queue.contains( o );

  }

  @Override
  public Iterator iterator( ) {

    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public Object[ ] toArray( ) {

    Object[ ] arr = this.queue.toArray( );

    this.shuffle( arr );

    return arr;

  }

  @Override
  public <T> T[ ] toArray( T[ ] a ) {

    T[ ] arr = this.queue.toArray( a );

    this.shuffle( arr );

    return arr;

  }

  @Override
  public boolean remove( Object o ) {

    return this.queue.remove( o );

  }

  @Override
  public boolean containsAll( Collection<?> c ) {

    return this.queue.containsAll( c );

  }

  @Override
  public boolean addAll( Collection<? extends T> c ) {

    return this.queue.addAll( c );

  }

  @Override
  public boolean removeAll( Collection<?> c ) {

    return this.queue.removeAll( c );

  }

  @Override
  public boolean retainAll( Collection<?> c ) {

    return this.queue.retainAll( c );

  }

  @Override
  public void clear( ) {

    this.queue.clear( );

  }

  @Override
  public boolean add( T e ) {

    return this.queue.add( e );

  }

  @Override
  public boolean offer( T e ) {

    return this.queue.add( e );

  }

  @Override
  public T remove( ) {

    if( this.queue.size( ) == 0 )
      throw new NoSuchElementException( );

    return this.queue.remove( this.rng.nextInt( this.queue.size( ) ) );

  }

  @Override
  public T poll( ) {

    if( this.queue.size( ) == 0 )
      return null;

    return this.queue.remove( this.rng.nextInt( this.queue.size( ) ) );

  }

  @Override
  public T element( ) {

    if( this.queue.size( ) == 0 )
      throw new NoSuchElementException( );

    return this.queue.get( this.rng.nextInt( this.queue.size( ) ) );

  }

  @Override
  public T peek( ) {

    if( this.queue.size( ) == 0 )
      return null;

    return this.queue.get( this.rng.nextInt( this.queue.size( ) ) );

  }

}
