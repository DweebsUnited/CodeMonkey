package db

import (
	"database/sql"
)

type Datastore interface {
	GetAllTodos( ) ( [ ] *Todo, error )
}

type dB struct {

	*sql.DB

}

func NewDB( connString string ) ( *dB, error ) {

	// Make a DB
    db, err := sql.Open( "postgres", connString )

	// Check we connected okay
	if err != nil {
        return nil, err
    }

	// Check we can ping the DB
	if err = db.Ping( ); err != nil {
        return nil, err
    }

	return &dB{ db }, nil

}