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
	db, err := db.NewDB( "user=apfel password=j0Dx#S^ponT3QN&CSfLhU7^VMZ7tvd7& host=localhost port=5432 dbname=todos" )
	if err != nil {
		panic( "Could not connect to DB: " + err.Error( ) )
	}
	serv.db = db

	// Set up the router!
	serv.r = chi.NewRouter( )
	serv.routes( "www" )

	// Start it!
	//	 This is part of why we need the config...
	server := &http.Server {
		Addr: ":35424",
		Handler: serv.r,
		ReadTimeout: 10 * time.Second,
		WriteTimeout: 10 * time.Second,
	}
	go func( ) {

		server.ListenAndServe( )

	}( )

	return server.Shutdown

}
