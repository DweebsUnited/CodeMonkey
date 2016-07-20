#define NUMMODULES 256 // 1 reserved
#define NUMCALLERS 256 // 1 reserved
#define NUMRECORDS 256 // 3 reserved

#include <cstdint>
#include <string>

namespace CodeMonkey {
namespace Logger {

typedef enum class _LogLevel : uint8_t {
    RESERVED = 0,
    INFO,
    WARN,
    ERROR,
    DEBUG
} LogLevel;

typedef enum class _Types : uint8_t {
    uint8   = 0x42, // B
    sint8   = 0x62, // b
    uint16  = 0x53, // S
    sint16  = 0x73, // s
    uint32  = 0x49, // I
    sint32  = 0x69, // i
    Float   = 0x46, // F
    Double  = 0x44, // D
    pstring = 0x50  // P
} Types;

void closeLog( );

void openLogInput( std::string filename );
void openLogOutput( std::string filename );

void printLog( std::string filename );

class Header {
public:

    static const uint8_t size = 14;

    uint8_t recNum;
    uint64_t tStamp;
    uint8_t module;
    uint8_t caller;
    CodeMonkey::Logger::LogLevel debugLevel;
    uint16_t recSize;

    Header( uint8_t recNum, uint64_t tStamp, uint8_t module, uint8_t caller, CodeMonkey::Logger::LogLevel debugLevel, uint16_t recSize );
    Header( std::string& hString );

    std::string toString( );

};

class RecordLogger {
public:

    uint8_t recNum;
    uint8_t module;
    uint8_t caller;

    RecordLogger( uint8_t recNum, uint8_t module, uint8_t caller );

    void logRecord( std::string& record, CodeMonkey::Logger::LogLevel debugLevel );

};

class LogStringLogger {
public:
    RecordLogger logger;

    LogStringLogger( std::string module, std::string caller );

    void logRecord( std::string message, LogLevel debugLevel );

};

class FieldDef {

};

class RecordDef {

};

class UserRecordLogger {

};

};
};
