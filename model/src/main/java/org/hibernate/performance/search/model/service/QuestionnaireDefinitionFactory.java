package org.hibernate.performance.search.model.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.question.ClosedQuestion;
import org.hibernate.performance.search.model.entity.question.OpenQuestion;
import org.hibernate.performance.search.model.entity.question.Question;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;
import org.hibernate.performance.search.model.param.RelationshipSize;

public final class QuestionnaireDefinitionFactory {

	private final RelationshipSize relationshipSize;

	public QuestionnaireDefinitionFactory(RelationshipSize relationshipSize) {
		this.relationshipSize = relationshipSize;
	}

	public List<QuestionnaireDefinition> createQuestionnaireDefinitions(Company company) {
		int definitionsForCompany = relationshipSize.getQuestionnaireDefinitionsForCompany();
		ArrayList<QuestionnaireDefinition> result = new ArrayList<>( definitionsForCompany );

		for ( int i = 0; i < definitionsForCompany; i++ ) {
			int id = company.getId() * definitionsForCompany + i;
			int year = 2020 + i;
			String title = "Questionnaire " + company + " " + year;

			QuestionnaireDefinition questionnaire = new QuestionnaireDefinition( id, title, title, year, company );
			result.add( questionnaire );

			addQuestions( questionnaire );
		}

		return result;
	}

	private void addQuestions(QuestionnaireDefinition questionnaire) {
		int[] closedQuestionsWeights = relationshipSize.getClosedQuestionsWeightsForQuestionnaire();
		int openQuestionsSize = relationshipSize.getOpenQuestionsForQuestionnaire();

		int totalQuestionsNumber = closedQuestionsWeights.length + openQuestionsSize;
		int baseId = questionnaire.getId() * totalQuestionsNumber;
		ArrayList<Question> questions = new ArrayList<>( totalQuestionsNumber );

		for ( int i = 0; i < closedQuestionsWeights.length; i++ ) {
			String text = questionnaire.getTitle() + " - Closed Question " + ( i + 1 );
			questions.add( new ClosedQuestion( baseId++, questionnaire, text, closedQuestionsWeights[i] ) );
		}
		for ( int i = 0; i < openQuestionsSize; i++ ) {
			String text = questionnaire.getTitle() + " - Open Question " + ( i + 1 );
			questions.add( new OpenQuestion( baseId++, questionnaire, text ) );
		}

		questionnaire.setQuestions( questions );
	}
}
