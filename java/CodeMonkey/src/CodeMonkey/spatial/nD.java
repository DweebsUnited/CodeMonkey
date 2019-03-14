package CodeMonkey.spatial;

public class nD {

	private float[ ] dims;

	public nD( int nDims ) {

		this.dims = new float[ nDims ];

		for( int ddx = 0; ddx < this.dims.length; ++ddx )
			this.dims[ ddx ] = 0;

	}

	public nD( float[ ] dims ) {

		this( dims.length );

		for( int ddx = 0; ddx < dims.length; ++ddx )
			this.dims[ ddx ] = dims[ ddx ];

	}

	public nD( nD v ) {

		this( v.dims );

	}

	public int dims( ) {

		return this.dims.length;

	}

	public float[ ] coord( ) {

		float[ ] ret = new float[ this.dims.length ];

		for( int ddx = 0; ddx < this.dims.length; ++ddx )
			ret[ ddx ] = this.dims[ ddx ];

		return ret;

	}

	public nD copy( ) {

		return new nD( this );

	}

	public void set( float[ ] dims ) {

		if( dims.length != this.dims.length )
			throw new RuntimeException( "Can't set different length dimensions" );

		for( int ddx = 0; ddx < this.dims.length; ++ddx )
			this.dims[ ddx ] = dims[ ddx ];

	}

	public void set( nD v ) {

		this.set( v.dims );

	}

	public void mult( float v ) {

		for( int ddx = 0; ddx < this.dims.length; ++ddx )
			this.dims[ ddx ] *= v;

	}

	public void add( nD v ) {

		if( v.dims.length != this.dims.length )
			throw new RuntimeException( "Can't add different length dimensions" );

		for( int ddx = 0; ddx < this.dims.length; ++ddx )
			this.dims[ ddx ] += v.dims[ ddx ];

	}

	public void sub( nD v ) {

		if( v.dims.length != this.dims.length )
			throw new RuntimeException( "Can't sub different length dimensions" );

		for( int ddx = 0; ddx < this.dims.length; ++ddx )
			this.dims[ ddx ] -= v.dims[ ddx ];

	}

	public float mag2( ) {

		float m = 0;

		for( float dim : this.dims )
			m += dim * dim;

		return m;

	}

	public float mag( ) {

		return (float) Math.sqrt( this.mag2( ) );

	}

	public void normalize( ) {

		this.mult( 1f / this.mag( ) );

	}

	public float dist2( nD b ) {

		nD r = b.copy( );

		r.sub( this );

		return r.mag2( );

	}

	public static nD lerp( nD a, nD b, float t ) {

		if( a.dims.length != b.dims.length )
			throw new RuntimeException( "Can't lerp different length dimensions" );

		float[ ] lc = new float[ a.dims.length ];

		for( int ddx = 0; ddx < a.dims.length; ++ddx )
			lc[ ddx ] = a.dims[ ddx ] * ( 1 - t ) + b.dims[ ddx ] * t;

		return new nD( lc );

	}

}
