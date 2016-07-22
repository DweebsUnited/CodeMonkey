#pragma once
/*********************************************************************************//**
* @file threadsafe.h
* Some cool threadsafe objects
*
* Copyright (C) 2016 by Eric Osburn.
* The redistribution terms are provided in the LICENSE file that must
* be distributed with this source code.
*************************************************************************************/

/*************************************************************************************
* Includes
*************************************************************************************/
#include <thread>
#include <mutex>
#include <queue>

/*************************************************************************************
* Namespaces
*************************************************************************************/
namespace CodeMonkey {
namespace Threadsafe {

/*************************************************************************************
* Classes
*************************************************************************************/
/*********************************************************************************//**
* Mailbox for receiving and sending messages between threads
*
* You will have to keep a pointer to this in each thread that wants to use it. Shared
*   pointers I've found work extremely well, because when everyone is done, cleanup
*   is automatic. For now, has only a blocking receive.
* TODO: try_receive, immediate return if no new messages
*************************************************************************************/
template <class Message>
class Mailbox {

    /** Mailbox, implemented as a queue     */
    std::queue<Message> mailbox;
    /** Mailbox mutex, for thread safety    */
    std::mutex mtx;

public:

    /*****************************************************************************//**
    * Send a message to this mailbox
    *
    * @param    [in]    msg     Message to send
    *********************************************************************************/
    void send( Message msg ) {

        // This is a dead simple access control idiom
        mtx.lock( );

        mailbox.push( msg );

        mtx.unlock( );

    };

    /*****************************************************************************//**
    * Blocking receive a message from this mailbox
    *
    * @param    [in]    msg     Where to put the message when it is received
    *********************************************************************************/
    void yield_receive( Message * msg ) {

        // Must lock before accessing the mailbox
        mtx.lock( );

        // This accesses
        while( mailbox.empty( ) ) {

            // Now unlock, and yield the thread to someone else
            mtx.unlock( );

            std::this_thread::yield( );

            // Lock it again before the empty check
            mtx.lock( );

        }

        // Get the top message out
        *msg = mailbox.front( );
        mailbox.pop( );

        // And unlock for the return
        mtx.unlock( );

    };

};

};
};
