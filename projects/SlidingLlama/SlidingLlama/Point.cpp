#include "AllClasses.h"

Point::Point( ) {

};

void Point::setID( int id ) {
    this->id = id;
}

void Point::addLink( Point * p ) {
    this->links.push_back( p );
}

void Point::set( float x, float y ) {
    this->coord = std::complex<float>( x, y );
}

void Point::set( float z ) {
    this->z = z;
}

float Point::get( ) {
    return this->z;
}

float Point::getX( ) {
    return this->coord.real( );
}

float Point::getY( ) {
    return this->coord.imag( );
}

float Point::triArea( Point * a, Point * b, Point * c ) {
    // Area = 1/2 | ( bx - ax )( cy - ay ) - ( cx - ax )( by - ay ) |
    return 0.5 * std::abs( ( b->getX( ) - a->getX( ) ) * ( c->getY( ) - a->getY( ) ) - ( c->getX( ) - a->getX( ) ) * ( b->getY( ) - a->getY( ) ) );
}

float Point::springForce( ) {

    // TODO: Spring to all links + spring to origin
    return 0;

}