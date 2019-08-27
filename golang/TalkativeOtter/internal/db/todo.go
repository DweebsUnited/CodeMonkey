package db

import (
	"time"
)

type Todo struct {

	id int

	title string
	desc string

	due time.Time

	done bool

}

func (db *dB) GetAllTodos( ) ( [ ] *Todo, error ) {

	return nil, nil

}