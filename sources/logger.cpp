#include "logger.h"

#include <iostream>
#include <fstream>
#include <chrono>
#include <vector>
#include <algorithm>


// Global logging SMACK
std::fstream logFile;
// True = Writing, False = Reading
bool logFileMode = true;


class NameManager {
public:
    static CodeMonkey::Logger::RecordLogger logger;
    static std::vector<std::string> modules;
    static std::vector<std::string> callers;

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

        if( it != modules.end( ) )
            return std::distance( NameManager::modules.begin( ), it );

        else {

            uint16_t num = NameManager::nextModuleID( );
            if( num == NUMMODULES )
                // NO VALID MODULE NUMBER
                // TODO: Throw an exception here?
                return 0;

            // Add name to modules
            NameManager::modules.push_back( name );

            // Write out name reservation
            std::string record;
            record += (uint8_t)0x00;
            record += (uint8_t)num;
            record += (uint8_t)name.size( );
            record += name;

            NameManager::logger.logRecord( record, CodeMonkey::Logger::LogLevel::RESERVED );

            return num;

        }

    };

    static uint8_t reserveCaller( std::string name ) {
        std::vector<std::string>::iterator it = std::find( NameManager::callers.begin( ), NameManager::callers.end( ), name );

        if( it != callers.end( ) )
            return std::distance( NameManager::callers.begin( ), it );

        else {

            uint16_t num = NameManager::nextCallerID( );
            if( num == NUMCALLERS )
                // NO VALID CALLER NUMBER
                // TODO: Throw an exception here?
                return 0;

            // Add name to modules
            NameManager::callers.push_back( name );

            // Write out name reservation
            std::string record;
            record += (uint8_t)0x01;
            record += (uint8_t)num;
            record += (uint8_t)name.size( );
            record += name;

            NameManager::logger.logRecord( record, CodeMonkey::Logger::LogLevel::RESERVED );

            return num;

        }

    };

    static std::string lookupModule( uint8_t id ) {
        if( id < NameManager::nextModuleID( ) )
            return NameManager::modules[ id ];
        else
            return std::string( "Unknown" );
    };

    static std::string lookupCaller( uint8_t id ) {
        if( id < NameManager::nextCallerID( ) )
            return NameManager::callers[ id ];
        else
            return std::string( "Unknown" );
    };

};

CodeMonkey::Logger::RecordLogger NameManager::logger = CodeMonkey::Logger::RecordLogger( 1, 0, 0 );
std::vector<std::string> NameManager::modules = std::vector<std::string>( 1, std::string( "Reserved" ) );
std::vector<std::string> NameManager::callers = std::vector<std::string>( 1, std::string( "Reserved" ) );


class RecordManager {
public:

};


void CodeMonkey::Logger::closeLog( ) {

    if( logFile.is_open( ) )
        logFile.close( );

};

void CodeMonkey::Logger::openLogInput( std::string filename ) {

    closeLog( );

    logFileMode = false;
    logFile.open( filename, std::ios::binary | std::ios::in );

    // TODO
    NameManager::reset( );
    // _RecordManager.reset( )

};

void CodeMonkey::Logger::openLogOutput( std::string filename ) {

    closeLog( );

    logFileMode = true;
    logFile.open( filename, std::ios::binary | std::ios::out );

    // TODO
    NameManager::reset( );
    // _RecordManager.reset( )

};

void CodeMonkey::Logger::printLog( std::string filename ) {
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
        std::cout << "Module       : " << NameManager::lookupModule( h.module ) << std::endl;
        std::cout << "Caller       : " << NameManager::lookupCaller( h.caller ) << std::endl;
        std::cout << "Debug level  : " << (int)h.debugLevel << std::endl;
        std::cout << "Record size  : " << (int)h.recSize << std::endl;
        std::cout << std::endl;

        if( h.recNum == 0 ) {
        // String message

            std::string msg;
            msg.resize( h.recSize );
            logFile.read( &msg[ 0 ], h.recSize );
            std::cout << "Message      : " << msg << std::endl;

        }  else if( h.recNum == 1 ) {
            // Name reservation

            std::string record;
            record.resize( 1 );
            uint8_t resType;
            uint8_t resID;

            logFile.read( &record[ 0 ], 1 );
            resType = record[ 0 ];

            logFile.read( &record[ 0 ], 1 );
            resID = record[ 0 ];

            logFile.read( &record[ 0 ], 1 );
            record.resize( record[ 0 ] );
            logFile.read( &record[ 0 ], record.size( ) );


            std::cout << "Type         : ";
            if( resType == 0 ) {

                std::cout << "Module";
                if( resID == NameManager::nextModuleID( ) )
                    NameManager::reserveModule( record );

            } else if( resType == 1 ) {

                std::cout << "Caller";
                if( resID == NameManager::nextCallerID( ) )
                    NameManager::reserveCaller( record );

            } else
                std::cout << (int)resType;

            std::cout << std::endl;

            std::cout << "ID           : " << (int)resID << std::endl;
            std::cout << "Name         : " << record << std::endl;

        } else {

            // We don't know how to handle this, so ignore it
            // We can read the right number of bytes, or seek past
            // I choose to read, because it's how I did it in python
            std::string msg;
            msg.resize( h.recSize );
            logFile.read( &msg[ 0 ], h.recSize );

        }
        /*elif h.recNum == 2:
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


CodeMonkey::Logger::Header::Header( uint8_t recNum, uint64_t tStamp, uint8_t module, uint8_t caller, CodeMonkey::Logger::LogLevel debugLevel, uint16_t recSize ) :
    recNum( recNum ),
    tStamp( tStamp ),
    module( module ),
    caller( caller ),
    debugLevel( debugLevel ),
    recSize( recSize ) { };

CodeMonkey::Logger::Header::Header( std::string& hString ) :
    recNum( (uint8_t)hString[ 0 ] ),
    module( (uint8_t)hString[ 9 ] ),
    caller( (uint8_t)hString[ 10 ] ),
    debugLevel( (CodeMonkey::Logger::LogLevel)hString[ 11 ] ),
    recSize( (uint8_t)hString[ 12 ] | ( (uint8_t)hString[ 13 ] ) << 8 ) {

    this->tStamp = 0;
    for( uint32_t i = 0; i < 8; ++i )
        tStamp |= ( (uint8_t) hString[ 1 + i ] ) << 8 * i;

};

std::string CodeMonkey::Logger::Header::toString( ) {

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


CodeMonkey::Logger::RecordLogger::RecordLogger( uint8_t recNum, uint8_t module, uint8_t caller ) :
    recNum( recNum ),
    module( module ),
    caller( caller ) { };

void CodeMonkey::Logger::RecordLogger::logRecord( std::string& record, CodeMonkey::Logger::LogLevel debugLevel ) {
    if( logFile.is_open( ) && logFileMode ) {
        Header h(
            this->recNum,
            (uint64_t)std::chrono::duration_cast<std::chrono::milliseconds>( std::chrono::system_clock::now( ).time_since_epoch( ) ).count( ),
            this->module,
            this->caller,
            debugLevel,
            record.size( ) );

        std::string hString = h.toString( );

        logFile.write( &hString[ 0 ], Header::size );
        logFile.write( &record[ 0 ], record.size( ) );
    }
};


CodeMonkey::Logger::LogStringLogger::LogStringLogger( std::string module, std::string caller ) :
    logger( 0, NameManager::reserveModule( module ), NameManager::reserveCaller( caller ) ) { };

void CodeMonkey::Logger::LogStringLogger::logRecord( std::string message, CodeMonkey::Logger::LogLevel debugLevel ) {
    this->logger.logRecord( message, debugLevel );
};
