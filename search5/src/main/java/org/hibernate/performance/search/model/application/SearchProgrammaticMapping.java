package org.hibernate.performance.search.model.application;

import java.lang.annotation.ElementType;

import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;
import org.hibernate.performance.search.model.entity.answer.ClosedAnswer;
import org.hibernate.performance.search.model.entity.answer.OpenAnswer;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstanceId;
import org.hibernate.performance.search.model.entity.performance.PerformanceSummary;
import org.hibernate.performance.search.model.entity.question.ClosedQuestion;
import org.hibernate.performance.search.model.entity.question.OpenQuestion;
import org.hibernate.performance.search.model.entity.question.Question;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.cfg.EntityMapping;
import org.hibernate.search.cfg.IndexedMapping;
import org.hibernate.search.cfg.PropertyMapping;
import org.hibernate.search.cfg.SearchMapping;

public final class SearchProgrammaticMapping {

	private SearchProgrammaticMapping() {
	}

	public static SearchMapping create() {
		SearchMapping mapping = new SearchMapping();

		// Company
		IndexedMapping company = mapping.entity( Company.class ).indexed();
		company.property( "legalName", ElementType.FIELD ).field().analyze( Analyze.NO );

		PropertyMapping businessUnits = company.property( "businessUnits", ElementType.FIELD );
		businessUnits.indexEmbedded().depth( 1 );
		businessUnits.containedIn();

		// BusinessUnit
		IndexedMapping businessUnit = mapping.entity( BusinessUnit.class ).indexed();
		businessUnit.property( "name", ElementType.FIELD ).field().analyze( Analyze.NO );

		PropertyMapping owner = businessUnit.property( "owner", ElementType.FIELD );
		owner.indexEmbedded().depth( 1 );
		owner.containedIn();

		// Employee
		IndexedMapping employee = mapping.entity( Employee.class ).indexed();
		employee
				.property( "id", ElementType.FIELD ).documentId()
				.property( "name", ElementType.FIELD ).field().analyze( Analyze.NO )
				.property( "surname", ElementType.FIELD ).field().analyze( Analyze.NO )
				.property( "socialSecurityNumber", ElementType.FIELD ).field().analyze( Analyze.NO )
				.property( "company", ElementType.FIELD ).indexEmbedded()
				.property( "businessUnit", ElementType.FIELD ).indexEmbedded()
				// index 3 managers up to the hierarchy
				.property( "manager", ElementType.FIELD ).indexEmbedded().depth( 3 );

		PropertyMapping managerProperty = employee.property( "manager", ElementType.FIELD );
		managerProperty.indexEmbedded().depth( 1 );
		managerProperty.containedIn();

		// Manager
		IndexedMapping manager = mapping.entity( Manager.class ).indexed();
		PropertyMapping employees = manager.property( "employees", ElementType.FIELD );
		// index 2 employee-levels down to the hierarchy
		employees.indexEmbedded().depth( 2 );
		employees.containedIn();

		// QuestionnaireDefinition
		IndexedMapping questionnaireDefinition = mapping.entity( QuestionnaireDefinition.class ).indexed();
		questionnaireDefinition.property( "title", ElementType.FIELD ).field();
		questionnaireDefinition.property( "description", ElementType.FIELD ).field();
		questionnaireDefinition.property( "year", ElementType.FIELD ).field().numericField();

		PropertyMapping questions = questionnaireDefinition.property( "questions", ElementType.FIELD );
		questions.indexEmbedded().depth( 1 );
		questions.containedIn();

		// Question
		IndexedMapping question = mapping.entity( Question.class ).indexed();
		question.property( "text", ElementType.FIELD ).field();

		PropertyMapping questionnaire = question.property( "questionnaire", ElementType.FIELD );
		questionnaire.indexEmbedded().depth( 1 );
		questionnaire.containedIn();

		// OpenQuestion
		mapping.entity( OpenQuestion.class ).indexed();

		// ClosedQuestion
		IndexedMapping closedQuestion = mapping.entity( ClosedQuestion.class ).indexed();
		closedQuestion.property( "weight", ElementType.FIELD ).field().numericField();

		// QuestionnaireInstance
		IndexedMapping questionnaireInstance = mapping.entity( QuestionnaireInstance.class ).indexed();
		questionnaireInstance
				.property( "uniqueCode", ElementType.FIELD ).documentId()
				.property( "id", ElementType.FIELD ).indexEmbedded();

		PropertyMapping closedAnswers = questionnaireInstance.property( "closedAnswers", ElementType.FIELD );
		closedAnswers.indexEmbedded().depth( 1 );
		closedAnswers.containedIn();

		PropertyMapping openAnswers = questionnaireInstance.property( "openAnswers", ElementType.FIELD );
		openAnswers.indexEmbedded().depth( 1 );
		openAnswers.containedIn();

		EntityMapping qiId = mapping.entity( QuestionnaireInstanceId.class );
		qiId.property( "definition", ElementType.FIELD ).indexEmbedded()
			.property( "approval", ElementType.FIELD ).indexEmbedded()
			.property( "subject", ElementType.FIELD ).indexEmbedded();

		// OpenAnswer
		IndexedMapping openAnswer = mapping.entity( OpenAnswer.class ).indexed();
		openAnswer.property( "text", ElementType.FIELD ).field();
		openAnswer.property( "question", ElementType.FIELD ).indexEmbedded().depth( 2 );

		PropertyMapping questionnairePropertyOA = openAnswer.property( "questionnaire", ElementType.FIELD );
		questionnairePropertyOA.indexEmbedded().depth( 1 );
		questionnairePropertyOA.containedIn();

		// ClosedAnswer
		IndexedMapping closedAnswer = mapping.entity( ClosedAnswer.class ).indexed();
		closedAnswer.property( "choice", ElementType.FIELD ).field().numericField();
		closedAnswer.property( "question", ElementType.FIELD ).indexEmbedded().depth( 2 );

		PropertyMapping questionnairePropertyCA = closedAnswer.property( "questionnaire", ElementType.FIELD );
		questionnairePropertyCA.indexEmbedded().depth( 1 );
		questionnairePropertyCA.containedIn();

		// PerformanceSummary
		IndexedMapping performanceSummary = mapping.entity( PerformanceSummary.class ).indexed();
		performanceSummary
				.property( "employee", ElementType.FIELD ).indexEmbedded().depth( 3 )
				.property( "year", ElementType.FIELD ).field().numericField()
				.property( "maxScore", ElementType.FIELD ).field().numericField()
				.property( "employeeScore", ElementType.FIELD ).field().numericField();

		return mapping;
	}
}