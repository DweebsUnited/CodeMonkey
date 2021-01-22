package db

import (
	"github.com/jackc/pgx"
)

type Datastore interface {
	GetAllTodos( ) ( [ ] *Todo, error )
	AddTodo( todo *Todo ) ( error )
}

type dB struct {

	*pgx.Conn

}

func NewDB( connString string ) ( *dB, error ) {

	// Make a DB
	config, err := pgx.ParseDSN( connString )
	if err != nil {
		return nil, err
	}

	db, err := pgx.Connect( config )

	// Check we connected okay
	if err != nil {
		return nil, err
	}

	// Check we can ping the DB
	if _, err = db.Exec( ";" ); err != nil {
		return nil, err
	}

	return &dB{ db }, nil

}
