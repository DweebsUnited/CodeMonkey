#include "main.h"

#include "logger.h"


void logTest( ) {

    std::string fname = "ignore/log";

    std::cout << "Using " << fname << " as logfile." << std::endl;

    CodeMonkey::Logger::openLogOutput( fname );

    CodeMonkey::Logger::LogStringLogger logger( "test", "test" );
    logger.logRecord( "HERPDERP", CodeMonkey::Logger::LogLevel::WARN );

    CodeMonkey::Logger::RecordDef rDef( "MineAllMine" );
    rDef.addField( CodeMonkey::Logger::FieldDef(
        CodeMonkey::Logger::FieldDef::getTypeNum( "uint16" ),
        1,
        "TestField",
        "Derps" ) );
    CodeMonkey::Logger::UserRecordLogger uLogger( "Test", "Logger", rDef );

    uLogger.logRecord( "\x05\x00", CodeMonkey::Logger::LogLevel::WARN );

    logger.logRecord( "Post test to make sure we skip correctly", CodeMonkey::Logger::LogLevel::INFO );

    CodeMonkey::Logger::printLog( fname );

}
