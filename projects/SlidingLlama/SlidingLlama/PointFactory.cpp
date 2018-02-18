#include "allClasses.h"

PointFactory::PointFactory( ) {

    this->gen = std::default_random_engine( );
    this->gen.seed( (unsigned int)std::time( nullptr ) );
    this->radDist = std::uniform_real_distribution<float>( 0.0, 2.0 * (float)M_PI );
    this->magDist = std::uniform_real_distribution<float>( 0.0, 1.0 );

};

Point * PointFactory::make( ) {

    Point * p = new Point( );
    p->id = this->guid++;

    p->x = this->magDist( this->gen );
    p->y = this->magDist( this->gen );
    p->z = this->magDist( this->gen );

    return p;

};

Point * PointFactory::makeRim( ) {

    Point * p = new Point( );
    p->id = this->guid++;

    float ang = (float)M_PI * 2.0 / NUM_RIM * rimGenned++;
    float mag = 1.0;

    p->x = std::cos( ang ) * mag;
    p->y = std::sin( ang ) * mag;
    p->z = 0.0;

    return p;

}

bool PointFactory::hasRim( ) {

    return rimGenned < NUM_RIM;

}