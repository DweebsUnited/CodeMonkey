#include "AllClasses.h"

Point::Point( ) {

};

float Point::triArea( Point * a, Point * b, Point * c ) {
    // Area = 1/2 | ( bx - ax )( cy - ay ) - ( cx - ax )( by - ay ) |
    return 0.5 * std::abs( ( b->x - a->x ) * ( c->y - a->y ) - ( c->x - a->x ) * ( b->y - a->y ) );
}

float Point::springForce( ) {

    // TODO: Spring to all links + spring to origin
    return 0;

}