from django.http import HttpResponse, HttpResponseRedirect
from django.shortcuts import get_object_or_404, render, redirect
from django.urls import reverse

from .models import *

def index( request ):
	latest_questions = Question.objects.order_by( '-pub_date' )[ :5 ]
	return render( request, 'polls/index.htpl', { 'latest_questions': latest_questions, } )

def detail( request, question_id ):
	question = get_object_or_404( Question, pk = question_id )
	return render( request, 'polls/detail.htpl', { 'question': question, } )

def results( request, question_id ):
	question = get_object_or_404( Question, pk = question_id )
	return render( request, 'polls/results.htpl', { 'question': question, } )

def vote( request, question_id ):
	question = get_object_or_404( Question, pk = question_id )
	try:
		selected_choice = question.choice_set.get( pk = request.POST[ 'choice' ] )
	except ( KeyError, Choice.DoesNotExist ):
		return render( request, 'polls/detail.htpl', {
			'question': question,
			'error_message': "You didn't select a choice!",
		} )
	else:
		selected_choice.votes += 1
		selected_choice.save( )

		return redirect( 'polls:results', question_id = question.id )