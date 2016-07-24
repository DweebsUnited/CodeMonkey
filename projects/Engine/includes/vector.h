/*************************************************************************************
* vector.h
* Declares the Vec3d class as a template.
*
* Copyright (C) 2014 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/
#ifndef _ENGINE_VECTOR_H
#define _ENGINE_VECTOR_H

/*************************************************************************************
* Includes
*************************************************************************************/
#include <cmath>

/*************************************************************************************
* Class Templates
*************************************************************************************/
template <class t> class Vec3d;             /* 3d Vector                            */

/*************************************************************************************
* Vec3d Class
* Stores x, y, and z information as a templated type.
*************************************************************************************/
template <class t> class Vec3d {
public:
    t x,y,z,w;                              /* Position information                 */

    Vec3d( ) { this->x = 0; this->y = 0; this->z = 0; this->w = 1.0; };
    Vec3d( t x, t y, t z ) { this->x = x; this->y = y; this->z = z; this->w = 1.0; };
    Vec3d( Vec3d<t>& vec ) { this->x = vec.x; this->y = vec.y; this->z = vec.z; this->w = 1.0; };
    ~Vec3d( ) { };
    /* Constructors and destructors         */

    inline double norm( ) { return sqrt( pow( ( double ) this->x, 2 ) + pow( ( double ) this->y, 2 ) + pow( ( double ) this->z, 2 ) ); };
    /* Norm -> Magnitude of vector          */

    inline t dotP( Vec3d<t>& vec ) { return this->x * vec.x + this->y * vec.y + this->z * vec.z; };
    /* Dot product with 2d and 3d vectors   */

    inline Vec3d<t> crossP( Vec3d<t> vec ) { return Vec3d<t>( this->y*vec.z-this->z*vec.y, this->z*vec.x-this->x*vec.z, this->x*vec.y-this->y*vec.x ); };
    /* Cross product with 2d and 3d vectors */

    inline Vec3d<t>& operator += ( const Vec3d<t>& rhs ) { this->x += rhs.x; this->y += rhs.y; this->z += rhs.z; return *this; };
    inline Vec3d<t>& operator -= ( const Vec3d<t>& rhs ) { this->x -= rhs.x; this->y -= rhs.y; this->z -= rhs.z; return *this; };
    inline Vec3d<t>& operator *= ( const Vec3d<t>& rhs ) { this->x *= rhs.x; this->y *= rhs.y; this->z *= rhs.z; return *this; };
    inline Vec3d<t>& operator /= ( const Vec3d<t>& rhs ) { this->x /= rhs.x; this->y /= rhs.y; this->z /= rhs.z; return *this; };
    inline Vec3d<t>& operator =  ( const Vec3d<t>& rhs ) { this->x =  rhs.x; this->y =  rhs.y; this->z =  rhs.z; return *this; };
    inline bool operator == ( const Vec3d<t>& rhs ) { return ( this->x == rhs.x && this->y == rhs.y && this->z == rhs.z ) ? true : false; };
    inline bool operator != ( const Vec3d<t>& rhs ) { return !( *this == rhs ); };
    /* 3d vector add, sub, set, and eq check */

    inline Vec3d<t>& operator *= ( t rhs ) { this->x *= rhs; this->y *= rhs; this->z *= rhs; return *this; };
    inline Vec3d<t>& operator /= ( t rhs ) { this->x /= rhs; this->y /= rhs; this->z /= rhs; return *this; };
    /* Scalar operations                    */
};

/*********************************************
* Vec3d Class Arithmetic Operators
* Friend functions to the Vec3d class
*********************************************/

template <class t> inline Vec3d<t> operator + ( Vec3d<t> lhs, const Vec3d<t>& rhs ) { lhs += rhs; return lhs; };
template <class t> inline Vec3d<t> operator - ( Vec3d<t> lhs, const Vec3d<t>& rhs ) { lhs -= rhs; return lhs; };
template <class t> inline Vec3d<t> operator * ( Vec3d<t> lhs, const Vec3d<t>& rhs ) { lhs *= rhs; return lhs; };
template <class t> inline Vec3d<t> operator / ( Vec3d<t> lhs, const Vec3d<t>& rhs ) { lhs /= rhs; return lhs; };
/* Vector add, sub friend functions     */

template <class t> inline Vec3d<t> operator * ( Vec3d<t> lhs, const t rhs ) { lhs *= rhs; return lhs; };
template <class t> inline Vec3d<t> operator * ( const t lhs, Vec3d<t> rhs ) { rhs *= lhs; return rhs; };
template <class t> inline Vec3d<t> operator / ( Vec3d<t> lhs, const t rhs ) { lhs /= rhs; return lhs; };
template <class t> inline Vec3d<t> operator / ( const t rhs, Vec3d<t> lhs ) { rhs /= lhs; return rhs; };
/* Scalar arithmetic friend functions   */

#endif
