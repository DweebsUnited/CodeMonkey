#include <thread>
#include <mutex>
#include <queue>

namespace CodeMonkey {
namespace Threadsafe {

template <class Message>
class Mailbox {
    std::queue<Message> mailbox;
    std::mutex mtx;

public:

    void send( Message msg ) {

        mtx.lock( );

        mailbox.push( msg );

        mtx.unlock( );

    };

    void yield_receive( Message * msg ) {

        mtx.lock( );

        while( mailbox.empty( ) ) {

            mtx.unlock( );

            std::this_thread::yield( );

            mtx.lock( );

        }

        *msg = mailbox.front( );
        mailbox.pop( );

        mtx.unlock( );

    };

};

};
};