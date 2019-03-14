package CodeMonkey.spatial;

import processing.core.PVector;


public class Quaternion {

	public float x, y, z, w;

	public Quaternion( ) {

		this.x = this.y = this.z = 0;
		this.w = 1;
	}

	public Quaternion( float _x, float _y, float _z, float _w ) {

		this.x = _x;
		this.y = _y;
		this.z = _z;
		this.w = _w;
	}

	public Quaternion( float angle, PVector axis ) {

		this.setAngleAxis( angle, axis );
	}

	public Quaternion get( ) {

		return new Quaternion( this.x, this.y, this.z, this.w );
	}

	public Boolean equal( Quaternion q ) {

		return this.x == q.x && this.y == q.y && this.z == q.z && this.w == q.w;
	}

	public void set( float _x, float _y, float _z, float _w ) {

		this.x = _x;
		this.y = _y;
		this.z = _z;
		this.w = _w;
	}

	public void setAngleAxis( float angle, PVector axis ) {

		axis.normalize( );
		float hcos = (float) Math.cos( angle / 2 );
		float hsin = (float) Math.sin( angle / 2 );
		this.w = hcos;
		this.x = axis.x * hsin;
		this.y = axis.y * hsin;
		this.z = axis.z * hsin;
	}

	public Quaternion conj( ) {

		Quaternion ret = new Quaternion( );
		ret.x = -this.x;
		ret.y = -this.y;
		ret.z = -this.z;
		ret.w = this.w;
		return ret;
	}

	public Quaternion mult( float r ) {

		Quaternion ret = new Quaternion( );
		ret.x = this.x * r;
		ret.y = this.y * r;
		ret.z = this.z * r;
		ret.w = this.w * this.w;
		return ret;
	}

	public Quaternion mult( Quaternion q ) {

		Quaternion ret = new Quaternion( );
		ret.x = q.w * this.x + q.x * this.w + q.y * this.z - q.z * this.y;
		ret.y = q.w * this.y - q.x * this.z + q.y * this.w + q.z * this.x;
		ret.z = q.w * this.z + q.x * this.y - q.y * this.x + q.z * this.w;
		ret.w = q.w * this.w - q.x * this.x - q.y * this.y - q.z * this.z;
		return ret;
	}

	public PVector mult( PVector v ) {

		float px = ( 1 - 2 * this.y * this.y - 2 * this.z * this.z ) * v.x
				+ ( 2 * this.x * this.y - 2 * this.z * this.w ) * v.y
				+ ( 2 * this.x * this.z + 2 * this.y * this.w ) * v.z;

		float py = ( 2 * this.x * this.y + 2 * this.z * this.w ) * v.x
				+ ( 1 - 2 * this.x * this.x - 2 * this.z * this.z ) * v.y
				+ ( 2 * this.y * this.z - 2 * this.x * this.w ) * v.z;

		float pz = ( 2 * this.x * this.z - 2 * this.y * this.w ) * v.x
				+ ( 2 * this.y * this.z + 2 * this.x * this.w ) * v.y
				+ ( 1 - 2 * this.x * this.x - 2 * this.y * this.y ) * v.z;

		return new PVector( px, py, pz );
	}

	public void normalize( ) {

		float len = this.w * this.w + this.x * this.x + this.y * this.y + this.z * this.z;
		float factor = 1.0f / (float) Math.sqrt( len );
		this.x *= factor;
		this.y *= factor;
		this.z *= factor;
		this.w *= factor;
	}

	public static PVector rotate( PVector axis, float angle, PVector v ) {

		Quaternion q = new Quaternion( angle, axis );
		return q.mult( v );

	}

}
