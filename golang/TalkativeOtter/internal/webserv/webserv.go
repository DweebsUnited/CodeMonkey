package webserv

import (
	"fmt"
	"net/http"
	"github.com/go-chi/chi"
	"github.com/go-chi/chi/middleware"
	"github.com/go-chi/render"
	"github.com/go-chi/docgen"
	"path/filepath"
)

func NewRouter( ) *chi.Mux {

	// Set up a router
	r := chi.NewRouter( )

	// And its middleware
	r.Use( middleware.RequestID ) // TODO: CSRF verification?
	r.Use( middleware.RealIP )
	r.Use( middleware.Logger ) // TODO: Use different logger
	r.Use( middleware.Recoverer )
	// r.Use( middleware.URLFormat ) // TODO: Change output format by url file extension


	// Ping -> Pong!
	r.Get( "/ping", func( w http.ResponseWriter, req *http.Request ) {

		render.JSON( w, req, struct { Ping string `json:"ping"` }{ "Pong!" } )

	} )


	// Static files
	// TODO: Neuter the filesystem so it will only serve files that exist
	staticDirPath, _ := filepath.Abs( "../www/static" )
	fmt.Println( "Serving from:", staticDirPath )
	r.Method( "GET", "/static/*", http.StripPrefix( "/static/", http.FileServer( http.Dir( staticDirPath ) ) ) )


	// API routing, this is where things get complicated
	r.Route( "/api", func( r chi.Router ) {

		r.Use( render.SetContentType( render.ContentTypeJSON ) )

		// Get API version
		r.Get( "/", func( w http.ResponseWriter, req *http.Request ) {

			render.JSON( w, req, struct { Version string `json:"version"` }{ "v0.0.1" } )

		} )


	} )

	fmt.Println( docgen.JSONRoutesDoc( r ) )

	return r

}