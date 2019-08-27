package webserv

import (
	"net/http"
	"github.com/go-chi/chi"
	"time"
	"context"
	"talkativeOtter/internal/db"
)

// TODO: Add to notes [Fantastic best practices](https://medium.com/statuscode/how-i-write-go-http-services-after-seven-years-37c208122831)

type server struct {

	// conf *someConfStruct
    db db.Datastore
    r *chi.Mux
	// email *someEmailSender

}

// TODO: Take a config of some sort
func MakeAndStartServ( ) (func( context.Context ) error) {

	// Make and setup a server struct
	serv := &server{ }

	// Set up a DB
	db, err := db.NewDB( "postgres://user:pass!@localhost/bookstore" )
	if err != nil {
		panic( "Could not connect to DB" )
	}
	serv.db = db

	// Set up the router!
	serv.r = chi.NewRouter( )
	serv.routes( "www" ) // TODO: Give static basedir?

	// Start it!
	//   This is part of why we need the config...
	server := &http.Server {
		Addr: ":8080",
		Handler: serv.r,
		ReadTimeout: 10 * time.Second,
		WriteTimeout: 10 * time.Second,
	}
	go func( ) {

		server.ListenAndServe( )

	}( )

	return server.Shutdown

}