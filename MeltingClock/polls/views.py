from django.http import HttpResponseRedirect
from django.shortcuts import get_object_or_404, render, redirect
from django.urls import reverse
from django.views import generic

from .models import *


class IndexView( generic.ListView ):
	template_name = 'polls/index.htpl'
	context_object_name = 'latest_questions'

	def get_queryset( self ):
		return Question.objects.order_by( '-pub_date' )[ :5 ]

class DetailView( generic.DetailView ):
	model = Question
	template_name = 'polls/detail.htpl'

class ResultsView( generic.DetailView ):
	model = Question
	template_name = 'polls/results.htpl'

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

		return redirect( 'polls:results', pk = question.id )