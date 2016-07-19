#include "logger.h"

#include <iostream>
#include <fstream>
#include <string>
#include <chrono>
#include <vector>
#include <algorithm>

// Private classes as needed
class Header {
public:
    static const uint8_t size = 14;

    uint8_t recNum;
    uint64_t tStamp;
    uint8_t module;
    uint8_t caller;
    CodeMonkey::Logger::LogLevel debugLevel;
    uint16_t recSize;

    Header( uint8_t recNum, uint64_t tStamp, uint8_t module, uint8_t caller, CodeMonkey::Logger::LogLevel debugLevel, uint16_t recSize ) :
        recNum( recNum ),
        tStamp( tStamp ),
        module( module ),
        caller( caller ),
        debugLevel( debugLevel ),
        recSize( recSize ) { };


    Header( std::string& hString ) :
        recNum( (uint8_t)hString[ 0 ] ),
        module( (uint8_t)hString[ 9 ] ),
        caller( (uint8_t)hString[ 10 ] ),
        debugLevel( (CodeMonkey::Logger::LogLevel)hString[ 11 ] ),
        recSize( (uint8_t)hString[ 12 ] | ( (uint8_t)hString[ 13 ] ) << 8 ) {

        this->tStamp = 0;
        for( uint32_t i = 0; i < 8; ++i )
            tStamp |= ( (uint8_t) hString[ 1 + i ] ) << 8 * i;

    };

    std::string toString( ) {

        std::string ret;

        ret += this->recNum;

        for( uint32_t i = 0; i < 8; ++i )
            ret += ( this->tStamp >> 8 * i ) & 0xFF;

        ret += this->module;

        ret += this->caller;

        ret += (uint8_t)this->debugLevel;

        ret += this->recSize & 0xFF;
        ret += this->recSize >> 8;

        return ret;

    };
};


class RecordLogger {
public:
    uint8_t recNum;
    uint8_t module;
    uint8_t caller;

    RecordLogger( uint8_t recNum, uint8_t module, uint8_t caller ) :
        recNum( recNum ),
        module( module ),
        caller( caller ) { };

    void logRecord( std::string& record, CodeMonkey::Logger::LogLevel debugLevel ) {
        Header h(
            this->recNum,
            (uint64_t)std::chrono::duration_cast<std::chrono::milliseconds>( std::chrono::system_clock::now( ).time_since_epoch( ) ).count( ),
            this->module,
            this->caller,
            debugLevel,
            record.size( ) )

        std::string hString = h.toString( );

        if( logFile.is_open( ) && logFileMode ) {
            logFile.write( &hString[ 0 ], Header::size );
            logFile.write( &record[ 0 ], record.size( ) );
        }
    }
};

class NameManager {
public:
    RecordLogger logger( 1, 0, 0 );
    static std::vector<std::string> modules( 1, std::string( "Reserved" ) );
    static std::vector<std::string> callers( 1, std::string( "Reserved" ) );

    static void reset( ) {
        NameManager::modules.resize( 1 );
        NameManager::callers.resize( 1 );
    };

    static uint8_t nextModuleID( ) {
        return NameManager::modules.size( );
    };

    static uint8_t nextCallerID( ) {
        return NameManager::callers.size( );
    };

    static uint8_t reserveModule( std::string name ) {
        std::vector<std::string>::iterator it = std::find( NameManager::modules.begin( ), NameManager::modules.end( ), name );

        if( it != modules.end( ) ) {
            return std::distance( NameManager::modules.begin( ), it );
        } else {
            uint8_t num = NameManager::nextModuleID( );
            if( num == NUMMODULES )
                // NO VALID MODULE NUMBER
                // TODO: Throw an exception here?
                return 0;

            

        }
    }


};

class RecordManager {

};


// Global logging SMACK
std::fstream logFile;
// True = Writing, False = Reading
bool logFileMode = true;


void CodeMonkey::Logger::closeLog( ) {

    if( logFile.is_open( ) )
        logFile.close( );

};

void CodeMonkey::Logger::openLogInput( std::string& filename ) {

    closeLog( );

    logFileMode = false;
    logFile.open( filename, std::ios::binary | std::ios::in );

    // TODO
    // _NameManager.reset( )
    // _RecordManager.reset( )

};

void CodeMonkey::Logger::openLogOutput( std::string& filename ) {

    closeLog( );

    logFileMode = true;
    logFile.open( filename, std::ios::binary | std::ios::out );

    // TODO
    // _NameManager.reset( )
    // _RecordManager.reset( )

};

void CodeMonkey::Logger::printLog( std::string& filename ) {
    closeLog( );
    openLogInput( filename );

    std::string hString;
    hString.resize( Header::size );

    // Loop until we break from an io error ( usually eof )
    while( true ) {

        // Get the proper number of bytes for a header
        logFile.read( &hString[ 0 ], Header::size );

        // Catch all error states, eof fail and bad
        if( !logFile.good( ) )
            break;

        // Make a header from the string
        Header h( hString );

        // Print out the header stats
        std::cout << "Record number: " << (int)h.recNum << std::endl;
        std::cout << "Time         : " << (uint64_t)h.tStamp << std::endl;
        // TODO
        std::cout << "Module       : " << (int)h.module/*_NameManager.lookupModule( h.module )*/ << std::endl;
        std::cout << "Caller       : " << (int)h.caller/*_NameManager.lookupCaller( h.caller )*/ << std::endl;
        std::cout << "Debug level  : " << (int)h.debugLevel << std::endl;
        std::cout << "Record size  : " << (int)h.recSize << std::endl;
        std::cout << std::endl;

        // String message
        if( h.recNum == 0 ) {
            std::string msg;
            msg.resize( h.recSize );
            logFile.read( &msg[ 0 ], h.recSize );
            std::cout << "Message      : " << msg << std::endl;
        } else {
            std::string msg;
            msg.resize( h.recSize );
            logFile.read( &msg[ 0 ], h.recSize );
        }

        /*elif h.recNum == 1:
            # Name reservation
            typ = ord( fh.read( 1 ) )

            rid = ord( fh.read( 1 ) )
            name = fh.read( ord( fh.read( 1 ) ) ) # Neat way to read a pstring

            if typ == 0:
                typ = "Module"
                if rid == _NameManager.nextModuleID( ):
                    _NameManager.reserveModule( name )
            elif typ == 1:
                typ = "Caller"
                if rid == _NameManager.nextCallerID( ):
                    _NameManager.reserveCaller( name )

            std::cout << "Type:         ", typ
            std::cout << "ID:           ", rid
            std::cout << "Message:      ", name
        elif h.recNum == 2:
            # Record reservation
            rDef = RecordDef( fh.read( ord( fh.read( 1 ) ) ) )

            std::cout << "Record name:  ", rDef.name
            numFields = ord( fh.read( 1 ) )
            std::cout << "Num Fields:   ", numFields

            for f in range( numFields ):
                typeName = FieldDef.getTypeName( ord( fh.read( 1 ) ) )
                arrSize = ord( fh.read( 1 ) )
                name = fh.read( ord( fh.read( 1 ) ) )
                units = fh.read( ord( fh.read( 1 ) ) )

                rDef.addField( FieldDef( typeName, arrSize, name, units ) )

                std::cout << "Field name:   ", name
                std::cout << "  Type:       ", typeName
                std::cout << "  Array size: ", arrSize
                std::cout << "  Field units:", units

            _RecordManager.reserveRecord( rDef )

        else:
            # User defined
            rDef = _RecordManager.getReservation( h.recNum )
            if rDef is not None:
                std::cout << "Record:       ", rDef.name

                for f in rDef.fields:
                    std::cout << "Field name:   ", f.name
                    std::cout << "  Field units:", f.units
                    std::cout << "  Field data: ",
                    for i in range( f.arrSize ):
                        if f.typeNum == 9:
                            std::cout << fh.read( ord( fh.read ( 1 ) ) ) + ",",
                        else:
                            temp = 0
                            for j in range( FieldDef.getTypeSize( f.typeNum ) ):
                                temp |= ord( fh.read( 1 ) ) << 8 * j
                            std::cout << str( temp ) + ",",
                    std::cout << ""

            else:
                fh.read( h.recSize )
        */

        std::cout << std::endl << std::endl;

    }

};
