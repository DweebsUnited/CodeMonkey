#include "main.h"

#include "logger.h"


void logTest( ) {

    std::string fname = "ignore/log";

    std::cout << "Using " << fname << " as logfile." << std::endl;

    CodeMonkey::Logger::openLogOutput( fname );

    CodeMonkey::Logger::LogStringLogger logger( "Test", "Test" );
    logger.logRecord( "HERPDERP", CodeMonkey::Logger::LogLevel::WARN );

    CodeMonkey::Logger::RecordDef rDef( "MineAllMine" );
    rDef.addField( CodeMonkey::Logger::FieldDef(
        CodeMonkey::Logger::FieldDef::getTypeNum( "uint32" ),
        3,
        "Accelerometer reading",
        "mGs" ) );
    rDef.addField( CodeMonkey::Logger::FieldDef(
        CodeMonkey::Logger::FieldDef::getTypeNum( "string" ),
        1,
        "Log string",
        "Characters" ) );

    CodeMonkey::Logger::UserRecordLogger uLogger( "Test", "Logger", rDef );

    {
        std::string record;

        std::vector<uint32_t> v( 3 );

        v[ 0 ] = 1024;
        v[ 1 ] = 0;
        v[ 2 ] = 512;

        CodeMonkey::Logger::StringPacker::pack( record, v, std::string( "I don't know what to say about this reading" ) );

        uLogger.logRecord( record, CodeMonkey::Logger::LogLevel::WARN );
    }

    logger.logRecord( "Post test to make sure we skip correctly", CodeMonkey::Logger::LogLevel::INFO );

    CodeMonkey::Logger::printLog( fname );

}
