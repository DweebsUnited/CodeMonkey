package db

import (
	"github.com/jackc/pgx/pgtype"
)

func (db *dB) GetAllTodos( ) ( [ ] *Todo, error ) {

	var todos []*Todo

	rows, _ := db.Query( "select * from todos" )

	for rows.Next( ) {

		var id int
		var title string
		descr := new( pgtype.Varchar )
		due := new( pgtype.Timestamp )
		var done bool

		err := rows.Scan( &id, &title, descr, due, &done )
		if err != nil {

			return nil, err

		}

		todo := todoFromDB( &id, &title, descr, due, &done )
		todos = append( todos, todo )

	}

	return todos, nil

}

func (db *dB) AddTodo( todo *Todo ) ( error ) {

	return nil

}