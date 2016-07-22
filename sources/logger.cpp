#include "logger.h"

#include <iostream>
#include <fstream>
#include <chrono>
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

    static uint8_t reserveModule( const std::string& name ) {
        std::vector<std::string>::iterator it = std::find( NameManager::modules.begin( ), NameManager::modules.end( ), name );

        if( it != NameManager::modules.end( ) )
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

            // Shut cygwin the hell up
            // record += (uint8_t)0;
            // Causes an ambiguous overload error
            const uint8_t type = 0x00;
#ifdef _WIN32
            // Sigh. And yet windows insists on being an asshole
            record += (char)type;
#else
            record += type;
#endif

            record += (uint8_t)num;
            record += (uint8_t)name.size( );
            record += name;

            NameManager::logger.logRecord( record, CodeMonkey::Logger::LogLevel::RESERVED );

            return num;

        }

    };

    static uint8_t reserveCaller( const std::string& name ) {
        std::vector<std::string>::iterator it = std::find( NameManager::callers.begin( ), NameManager::callers.end( ), name );

        if( it != NameManager::callers.end( ) )
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
            record += (uint8_t)1;
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

    static CodeMonkey::Logger::RecordLogger logger;
    static std::vector<CodeMonkey::Logger::RecordDef> records;

    static void reset( ) {
        RecordManager::records.resize( 3 );
    };

    static uint8_t nextRecordID( ) {
        return RecordManager::records.size( );
    };

    static uint8_t reserveRecord( CodeMonkey::Logger::RecordDef& definition ) {
        std::vector<CodeMonkey::Logger::RecordDef>::iterator it = std::find_if( RecordManager::records.begin( ), RecordManager::records.end( ),
            [ definition ]( CodeMonkey::Logger::RecordDef& rDef ) { return rDef.name == definition.name; } );

        if( it != RecordManager::records.end( ) )
            return std::distance( RecordManager::records.begin( ), it );

        else {

            uint16_t num = RecordManager::nextRecordID( );
            if( num == NUMRECORDS )
                // NO VALID MODULE NUMBER
                // TODO: Throw an exception here?
                return 0;

            // Add name to modules
            RecordManager::records.push_back( definition );

            // Write out name reservation
            std::string record;

            record += (uint8_t)definition.name.size( );
            record += definition.name;

            record += (uint8_t)definition.fields.size( );

            for( CodeMonkey::Logger::FieldDef& f : definition.fields ) {

                record += (uint8_t)f.typeNum;
                record += (uint8_t)f.arraySize;
                record += (uint8_t)f.name.size( );
                record += f.name;
                record += (uint8_t)f.units.size( );
                record += f.units;

            }

            RecordManager::logger.logRecord( record, CodeMonkey::Logger::LogLevel::RESERVED );

            return num;

        }

    };

    static std::string lookupRecord( uint8_t id ) {
        if( id < RecordManager::nextRecordID( ) )
            return RecordManager::records[ id ].name;
        else
            return std::string( "Unknown" );
    };

    static CodeMonkey::Logger::RecordDef * getReservation( uint8_t id ) {
        if( id < RecordManager::nextRecordID( ) )
            return &RecordManager::records[ id ];
        else
            return nullptr;
    };

};

CodeMonkey::Logger::RecordLogger RecordManager::logger( 2, 0, 0 );
std::vector<CodeMonkey::Logger::RecordDef> RecordManager::records( 3 );


void CodeMonkey::Logger::closeLog( ) {

    if( logFile.is_open( ) )
        logFile.close( );

};

void CodeMonkey::Logger::openLogInput( const std::string& filename ) {

    closeLog( );

    logFileMode = false;
    logFile.open( filename, std::ios::binary | std::ios::in );

    NameManager::reset( );
    RecordManager::reset( );

};

void CodeMonkey::Logger::openLogOutput( const std::string& filename ) {

    closeLog( );

    logFileMode = true;
    logFile.open( filename, std::ios::binary | std::ios::out );

    NameManager::reset( );
    RecordManager::reset( );

};

void CodeMonkey::Logger::printLog( const std::string& filename ) {
    closeLog( );
    openLogInput( filename );

    std::string hString;
    hString.resize( Header::size );

    std::string record;

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

            record.resize( h.recSize );
            logFile.read( &record[ 0 ], h.recSize );
            std::cout << "Message      : " << record << std::endl;

        }  else if( h.recNum == 1 ) {
            // Name reservation

            uint8_t resType;
            uint8_t resID;

            resType = logFile.get( );

            resID = logFile.get( );

            record.resize( (uint8_t)logFile.get( ) );
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

        } else if( h.recNum == 2 ) {
            // Record reservation

            uint8_t numFields;

            record.resize( (uint8_t)logFile.get( ) );
            logFile.read( &record[ 0 ], record.size( ) );

            CodeMonkey::Logger::RecordDef rDef( record );

            std::cout << "Record name  : " << rDef.name << std::endl;

            numFields = logFile.get( );

            std::cout << "Num Fields   : " << (int)numFields << std::endl;

            uint8_t typeNum;
            uint8_t arraySize;
            std::string name; name.resize( 1 );
            std::string units; units.resize( 1 );

            for( uint8_t f = 0; f < numFields; ++f ) {

                typeNum = logFile.get( );

                arraySize = logFile.get( );

                name.resize( (uint8_t)logFile.get( ) );
                logFile.read( &name[ 0 ], name.size( ) );

                units.resize( (uint8_t)logFile.get( ) );
                logFile.read( &units[ 0 ], units.size( ) );

                rDef.addField( CodeMonkey::Logger::FieldDef( typeNum, arraySize, name, units ) );

                std::cout << "Field name   : " << name << std::endl;
                std::cout << "  Type       : " << CodeMonkey::Logger::FieldDef::getTypeName( typeNum ) << std::endl;
                std::cout << "  Array size : " << (int)arraySize << std::endl;
                std::cout << "  Field units: " << units << std::endl;

            }

            RecordManager::reserveRecord( rDef );

        } else {

            // We don't know how to handle this record, so ignore it
            // We still need to consume the bytes though, so skip over the payload
            logFile.seekg( h.recSize, std::ios::cur );

        }

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

CodeMonkey::Logger::Header::Header( const std::string& hString ) :
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

// Note, since the record length is computed and put in the header here
//   You could theoretically ignore the record definition, and just output whatever you want
//   Advantage of using the definitions correctly is that the printer will format them automatically
// For structures and such, just pack them yourself, and use a LogStringLogger
//   Filter by caller/module and voila
//   If you will have multiple types of message from the same pair, use this trick:
//     Reserve an empty definition
//     Filter on the record name
//     Who cares what you actually log, reading the proper size data is handled separately
void CodeMonkey::Logger::RecordLogger::logRecord( const std::string& record, CodeMonkey::Logger::LogLevel debugLevel ) {

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


CodeMonkey::Logger::LogStringLogger::LogStringLogger( const std::string& module, const std::string& caller ) :
    CodeMonkey::Logger::RecordLogger( 0, NameManager::reserveModule( module ), NameManager::reserveCaller( caller ) ) { };


CodeMonkey::Logger::FieldDef::FieldDef( uint8_t typeNum, uint8_t arraySize, const std::string& name, const std::string& units ) :
    typeNum( typeNum ),
    arraySize( arraySize ),
    name( name ),
    units( units ) { };


CodeMonkey::Logger::RecordDef::RecordDef( const std::string& name ) :
    name( name ) { };

void CodeMonkey::Logger::RecordDef::addField( const CodeMonkey::Logger::FieldDef& field ) {

    this->fields.push_back( field );

};


CodeMonkey::Logger::UserRecordLogger::UserRecordLogger( const std::string& module, const std::string& caller, RecordDef& definition ) :
    CodeMonkey::Logger::RecordLogger( RecordManager::reserveRecord( definition ), NameManager::reserveModule( module ), NameManager::reserveCaller( caller ) ) { };


void CodeMonkey::Logger::StringPacker::pack( std::string& record ) { };
