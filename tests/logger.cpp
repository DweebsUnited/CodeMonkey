#include "main.h"

#include "logger.h"


void logTest( ) {

    std::string fname = "ignore/log";

    std::cout << "Using " << fname << " as logfile." << std::endl;

    CodeMonkey::Logger::openLogOutput( fname );

    CodeMonkey::Logger::LogStringLogger logger( "test", "test" );

    logger.logRecord( "HERPDERP", CodeMonkey::Logger::LogLevel::WARN );

    CodeMonkey::Logger::printLog( fname );

}
