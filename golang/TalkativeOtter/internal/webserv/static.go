package webserv

import (
	"net/http"
	"strings"
	"path"
)

func isSlashRune(r rune) bool { return r == '/' || r == '\\' }

func containsDotDot( v string ) bool {
	if !strings.Contains( v, ".." ) {
		return false
	}
	for _, ent := range strings.FieldsFunc( v, isSlashRune ) {
		if ent == ".." {
			return true
		}
	}
	return false
}

func (s *server) handleStatic( baseDir string ) http.Handler {

	fs := http.Dir( baseDir )

	return http.HandlerFunc( func( w http.ResponseWriter, r *http.Request ) {

		// 400 (Bad Request) if .. found in path
		if containsDotDot( r.URL.Path ) {
			http.Error( w, "400 invalid URL", http.StatusBadRequest )
			return
		}

		// Open the file - If not, 404
		f, err := fs.Open( path.Clean( r.URL.Path ) )
		if err != nil {
			http.Error( w, "404 page not found", http.StatusNotFound )
			return
		}
		defer f.Close( )

		// Get some stats - If not, 404
		d, err := f.Stat( )
		if err != nil {
			http.Error( w, "404 page not found", http.StatusNotFound )
			return
		}

		// A directory? 404
		if d.IsDir( ) {
			http.Error( w, "404 page not found", http.StatusNotFound )
			return
		}

		// Made it this far, serve the file
		http.ServeContent( w, r, d.Name( ), d.ModTime( ), f )

	} )

}
