#include "allClasses.h"

PointFactory::PointFactory( ) {

    this->gen = std::default_random_engine( );
    this->gen.seed( (unsigned int)std::time( nullptr ) );
    this->radDist = std::uniform_real_distribution<float>( 0.0, 2.0 * (float)M_PI );
    this->magDist = std::uniform_real_distribution<float>( 0.0, 1.0 );

};

Point * PointFactory::make( ) {

    Point * p = new Point( );
    p->setID( this->guid++ );

    float ang = this->radDist( this->gen );
    float mag = std::sqrt( this->magDist( this->gen ) );

    p->set( std::cos( ang ) * mag, std::sin( ang ) * mag );

    return p;

};

Point * PointFactory::makeRim( ) {

    Point * p = new Point( );
    p->setID( this->guid++ );

    float ang = (float)M_PI * 2.0 / NUM_RIM * rimGenned++;
    float mag = 1.0;

    p->set( std::cos( ang ) * mag, std::sin( ang ) * mag );

    return p;

}

bool PointFactory::hasRim( ) {

    return rimGenned < NUM_RIM;

}