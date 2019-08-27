package main

import (
	"github.com/go-chi/chi"
	"fmt"
	"os"
	"net/http"
	"time"
	"context"
	"os/signal"
	"talkativeOtter/internal/webserv"
)

type Env struct {

	r *chi.Mux

}

func main( ) {

	fmt.Println( "Starting up!" )
	defer fmt.Println( "Shutting down..." )

	e := Env{ }

	// TODO: Set up logger in Env
	// TODO: Set up DB in Env

	// Set up webserv using Env
	e.r = webserv.NewRouter( )

	server := &http.Server {
		Addr: ":8080",
		Handler: e.r,
		ReadTimeout: 10 * time.Second,
		WriteTimeout: 10 * time.Second,
	}
	go func( ) {

		server.ListenAndServe( )

	}( )

	// Wait for interrupt
	sigChan := make( chan os.Signal, 1 )
	signal.Notify( sigChan, os.Interrupt )
	<- sigChan

	// Graceful shutdown
	ctx, cancel := context.WithTimeout( context.Background( ), 5 * time.Second )
	defer cancel( )
	server.Shutdown( ctx )

}