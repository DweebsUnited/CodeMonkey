#define NUMMODULES 256 // 1 reserved
#define NUMCALLERS 256 // 1 reserved
#define NUMRECORDS 256 // 3 reserved

namespace CodeMonkey {
namespace Logger {

typedef enum class _LogLevel : uint8_t {
    RESERVED,
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

// Have to use 64 bit timestamps to save all the milliseconds
// this->millis = std::chrono::duration_cast<std::chrono::milliseconds>( std::chrono::system_clock::now( ).time_since_epoch( ) ).count( );
// for( int i = 0; i < sizeof( uint64_t ); ++i ) {
//     s += ( this->millis >> ( 8 * i ) ) & 0xFF;
// }

void closeLog( );

void openLogInput( std::string filename );
void openLogOutput( std::string filename );

void printLog( std::string filename );

class LogStringLogger {

};

class FieldDef {

};

class RecordDef {

};

class UserRecordLogger {

};

};
};
