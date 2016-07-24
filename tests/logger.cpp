#include "main.h"

#include "logger.h"


void callback( CodeMonkey::Logger::Header& h, const std::string& recName, const std::string& record ) {

    if( recName == "MineAllMine" ) {

        std::vector<uint32_t> v( 3 );

        std::string message;

        CodeMonkey::Logger::StringPacker::unpack( record.begin( ), v, message );

        for( uint32_t e : v )
            std::cout << "Vector value: " << e << std::endl;

        std::cout << "Message: " << message << std::endl << std::endl;

    }

};


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

        std::string message = "I don't know what to say about this reading";

        CodeMonkey::Logger::StringPacker::pack( record, v, message );

        uLogger.logRecord( record, CodeMonkey::Logger::LogLevel::WARN );

        record = "";

        v[ 0 ] = 9999;
        v[ 1 ] = 99999;
        v[ 2 ] = 999999;

        message = "Low";

        CodeMonkey::Logger::StringPacker::pack( record, v, message );

        uLogger.logRecord( record, CodeMonkey::Logger::LogLevel::WARN );

    }

    logger.logRecord( "Post test to make sure we skip correctly", CodeMonkey::Logger::LogLevel::INFO );

    CodeMonkey::Logger::parseLog( fname, callback );

}
