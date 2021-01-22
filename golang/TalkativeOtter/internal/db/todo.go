package db

import (
	"time"
	"github.com/jackc/pgx/pgtype"
)

type Todo struct {

	Id int `json:"id"`

	Title string `json:"title"`
	Descr string `json:"descr"`

	Due *time.Time `json:"due"`

	Done bool `json:"done"`

}

// Helper function
func todoFromDB(
	id *int,
	title *string,
	descr *pgtype.Varchar,
	due *pgtype.Timestamp,
	done *bool ) *Todo {

	todo := new( Todo )

	todo.Id = *id
	todo.Title = *title
	todo.Done = *done

	t := descr.Get( )
	if t != nil {

		todo.Descr = t.(string)

	}

	t = due.Get( )
	if t != nil {

		todo.Due = new( time.Time )
		*todo.Due = *(t.(*time.Time))

	}

	return todo

}