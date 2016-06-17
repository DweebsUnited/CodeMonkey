#include "main.h"

#include "threadsafe.h"

#include <random>
#include <chrono>

// Test all the threadsafe classes
// Which for now just means the mailbox

class SimplePrinter {
public:

    struct Message {
        uint32_t num;
        char character;
    };

    std::shared_ptr<CodeMonkey::Threadsafe::Mailbox<Message>> mailbox;

    std::thread send;
    std::thread recv;

    SimplePrinter( ) : mailbox( std::make_shared<CodeMonkey::Threadsafe::Mailbox<SimplePrinter::Message>>( ) ) { };

    void startThreads( ) {
        this->send = std::thread( &SimplePrinter::sender, this, 25 );
        this->recv = std::thread( &SimplePrinter::receiver, this );
    };

    void joinThreads( ) {
        if( this->send.joinable( ) )
            this->send.join( );
        if( this->recv.joinable( ) )
            this->recv.join( );
    };

    void sender( uint32_t numMsgs ) {

        std::default_random_engine gen;
        std::uniform_int_distribution<uint32_t> dist( 1, 25 );

        gen.seed( std::chrono::high_resolution_clock::now( ).time_since_epoch( ).count( ) );

        Message m = { dist( gen ), '*' };

        for( ; numMsgs > 0; --numMsgs ) {

            m.num = dist( gen );
            this->mailbox->send( m );

        }

        m.num = 0;
        this->mailbox->send( m );

    }

    void receiver( ) {

        Message m;

        while( true ) {

            this->mailbox->yield_receive( &m );

            if( m.num == 0 )
                break;

            std::cout << "Received message: ";
            for( ; m.num > 0; --( m.num ) )
                std::cout << m.character;
            std::cout << std::endl;

        }

    }
};

void threadsafeTest( ) {

    SimplePrinter printer;

    printer.startThreads( );
    printer.joinThreads( );

};