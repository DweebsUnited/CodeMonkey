package webserv

import (
	"net/http"
)

// TODO: Implement real API functions
func (s *server) handleSomething( ) http.HandlerFunc {

	return func( w http.ResponseWriter, r *http.Request ) {
		// Durrrr
	}

}
