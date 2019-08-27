package main

import (
	"fmt"
	"os"
	"time"
	"context"
	"os/signal"
	"talkativeOtter/internal/webserv"
)

func main( ) {

	fmt.Println( "Starting up!" )
	defer fmt.Println( "Shutting down..." )

	// TODO: Give config
	shutdown := webserv.MakeAndStartServ( )

	// Wait for interrupt
	sigChan := make( chan os.Signal, 1 )
	signal.Notify( sigChan, os.Interrupt )
	<- sigChan

	// Graceful shutdown
	ctx, cancel := context.WithTimeout( context.Background( ), 5 * time.Second )
	defer cancel( )
	shutdown( ctx )

}