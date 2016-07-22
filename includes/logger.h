#define NUMMODULES 256 // 1 reserved
#define NUMCALLERS 256 // 1 reserved
#define NUMRECORDS 256 // 3 reserved

#include <cstdint>
#include <string>
#include <vector>

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

void openLogInput( const std::string& filename );
void openLogOutput( const std::string& filename );

void printLog( const std::string& filename );

class Header {
public:

    static const uint8_t size = 14;

    uint8_t recNum;
    uint64_t tStamp;
    uint8_t module;
    uint8_t caller;
    LogLevel debugLevel;
    uint16_t recSize;

    Header( uint8_t recNum, uint64_t tStamp, uint8_t module, uint8_t caller, LogLevel debugLevel, uint16_t recSize );
    Header( const std::string& hString );

    std::string toString( );

};

class RecordLogger {
public:

    uint8_t recNum;
    uint8_t module;
    uint8_t caller;

    RecordLogger( uint8_t recNum, uint8_t module, uint8_t caller );

    virtual void logRecord( const std::string& record, LogLevel debugLevel );

};

class LogStringLogger : public RecordLogger {
public:

    LogStringLogger( const std::string& module, const std::string& caller );

};

class FieldDef {
public:

    uint8_t typeNum;
    uint8_t arraySize;
    std::string name;
    std::string units;

    FieldDef( uint8_t typeNum, uint8_t arraySize, const std::string& name, const std::string& units );

    static uint8_t getTypeNum( const std::string& typeName ) {
        if( typeName == "uint8" )
            return 1;
        else if( typeName == "sint8" )
            return 2;
        else if( typeName == "uint16" )
            return 3;
        else if( typeName == "sint16" )
            return 4;
        else if( typeName == "uint32" )
            return 5;
        else if( typeName == "sint32" )
            return 6;
        else if( typeName == "float" )
            return 7;
        else if( typeName == "double" )
            return 8;
        else if( typeName == "string" )
            return 9;
        else
            return 0;
    };
    static std::string getTypeName( uint8_t typeNum ) {
        switch( typeNum ) {

            case 1:
                return "uint8";

            case 2:
                return "sint8";

            case 3:
                return "uint16";

            case 4:
                return "sint16";

            case 5:
                return "uint32";

            case 6:
                return "sint32";

            case 7:
                return "float";

            case 8:
                return "double";

            case 9:
                return "string";

            default:
                return "invalid";

        }
    };
    static uint8_t getTypeSize( uint8_t typeNum ) {
        switch( typeNum ) {

            case 1:
                return 1;

            case 2:
                return 1;

            case 3:
                return 2;

            case 4:
                return 2;

            case 5:
                return 4;

            case 6:
                return 4;

            case 7:
                return 4;

            case 8:
                return 8;

            case 9:
                return 1;

            default:
                return 0;

        }
    };

};

class RecordDef {
public:

    std::string name;
    std::vector<FieldDef> fields;

    RecordDef( ) : name( "Reserved" ) { };

    RecordDef( const std::string& name );

    void addField( const FieldDef& field );

};

class UserRecordLogger : public RecordLogger {
public:

    UserRecordLogger( const std::string& module, const std::string& caller, RecordDef& definition );

};

// namespace StringPacker {
//
//     void pack( std::string& record ) { };
//
//     template<typename...Args>
//     void pack( std::string& record, std::string f, Args... args ) {
//
//         record += (uint8_t) f.size( );
//         record += f;
//
//         record += pack( record, args );
//
//     };
//     template<typename...Args>
//     void pack( std::string& record, std::vector<std::string> f, Args... args ) {
//
//         for( uint32_t j = 0; j < f.size( ); ++j ) {
//
//             record += (uint8_t) f[ j ].size( );
//             record += f[ j ];
//
//         }
//
//         record += pack( record, args );
//
//     };
//
//     template<typename T, typename...Args>
//     void pack( std::string& record, T f, Args... args ) {
//
//         for( uint32_t i = 0; i < sizeof( T ); ++i )
//             record += (uint8_t)( ( T >> ( i * 8 ) ) & 0xFF );
//
//         record += pack( record, args );
//
//     };
//     template<typename...Args>
//     void pack( std::string& record, std::vector<T> f, Args... args ) {
//
//         for( uint32_t j = 0; j < f.size( ); ++j )
//             for( uint32_t i = 0; i < sizeof( T ); ++i )
//                 record += (uint8_t)( ( T >> ( i * 8 ) ) & 0xFF );
//
//         record += pack( record, args );
//
//     };
//
// };

};
};
