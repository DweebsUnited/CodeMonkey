package webserv

import (
	"net/http"
	"github.com/go-chi/chi"
	"github.com/go-chi/chi/middleware"
	"github.com/go-chi/render"
	"github.com/go-chi/docgen"
	"fmt"
	"path/filepath"
)

func (s *server) routes( baseStaticDir string ) {

	// Set up global middleware
	s.r.Use( middleware.Heartbeat( "/ping" ) )
	s.r.Use( middleware.RequestID ) // TODO: CSRF verification?
	s.r.Use( middleware.RealIP )
	s.r.Use( middleware.Logger ) // TODO: Use different logger
	s.r.Use( middleware.Recoverer )
	// r.Use( middleware.URLFormat ) // TODO: Change output format by url file extension?


	// Static files
	staticDirPath, _ := filepath.Abs( baseStaticDir )
	fmt.Println( "Serving static files from:", staticDirPath )
	s.r.Method( "GET", "/static/*", http.StripPrefix( "/static/", s.handleStatic( staticDirPath ) ) )


	// API routing, this is where things get fun
	s.r.Route( "/api", func( r chi.Router ) {

		r.Use( render.SetContentType( render.ContentTypeJSON ) )

		// Get API version
		r.Get( "/", func( w http.ResponseWriter, req *http.Request ) {

			render.JSON( w, req, struct { Version string `json:"version"` }{ "v0.0.1" } )

		} )

	} )

	fmt.Println( docgen.JSONRoutesDoc( s.r ) )

}