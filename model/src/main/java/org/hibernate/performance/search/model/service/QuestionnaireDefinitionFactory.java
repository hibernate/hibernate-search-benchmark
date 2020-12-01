package org.hibernate.performance.search.model.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.question.ClosedQuestion;
import org.hibernate.performance.search.model.entity.question.OpenQuestion;
import org.hibernate.performance.search.model.entity.question.Question;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;

public final class QuestionnaireDefinitionFactory {

	private static final int QUESTIONNAIRE_DEFINITIONS_FOR_COMPANY = 10;

	// deterministic sequence of WEIGHTS:
	private static final int[] CLOSED_QUESTIONS_WEIGHTS = { 7, 3, 9, 3, 3, 9, 7, 7, 7, 9 };

	private static final int OPEN_QUESTIONS_FOR_QUESTIONNAIRE = 10;

	private QuestionnaireDefinitionFactory() {
	}

	public static List<QuestionnaireDefinition> createQuestionnaireDefinitions(Company company) {
		ArrayList<QuestionnaireDefinition> result = new ArrayList<>( QUESTIONNAIRE_DEFINITIONS_FOR_COMPANY );

		for ( int i = 0; i < QUESTIONNAIRE_DEFINITIONS_FOR_COMPANY; i++ ) {
			int id = company.getId() * QUESTIONNAIRE_DEFINITIONS_FOR_COMPANY + i;
			int year = 2020 + i;
			String title = "Questionnaire " + company + " " + year;

			QuestionnaireDefinition questionnaire = new QuestionnaireDefinition( id, title, title, year, company );
			result.add( questionnaire );

			addQuestions( questionnaire );
		}

		return result;
	}

	private static void addQuestions(QuestionnaireDefinition questionnaire) {
		int totalQuestionsNumber = CLOSED_QUESTIONS_WEIGHTS.length + OPEN_QUESTIONS_FOR_QUESTIONNAIRE;
		int baseId = questionnaire.getId() * totalQuestionsNumber;
		ArrayList<Question> questions = new ArrayList<>( totalQuestionsNumber );

		for ( int i = 0; i < CLOSED_QUESTIONS_WEIGHTS.length; i++ ) {
			String text = questionnaire.getTitle() + " - Closed Question " + ( i + 1 );
			questions.add( new ClosedQuestion( baseId++, questionnaire, text, CLOSED_QUESTIONS_WEIGHTS[i] ) );
		}
		for ( int i = 0; i < OPEN_QUESTIONS_FOR_QUESTIONNAIRE; i++ ) {
			String text = questionnaire.getTitle() + " - Open Question " + ( i + 1 );
			questions.add( new OpenQuestion( baseId++, questionnaire, text ) );
		}

		questionnaire.setQuestions( questions );
	}
}
