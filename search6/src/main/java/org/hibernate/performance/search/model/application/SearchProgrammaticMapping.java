package org.hibernate.performance.search.model.application;

import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;
import org.hibernate.performance.search.model.entity.answer.ClosedAnswer;
import org.hibernate.performance.search.model.entity.answer.OpenAnswer;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.performance.PerformanceSummary;
import org.hibernate.performance.search.model.entity.question.ClosedQuestion;
import org.hibernate.performance.search.model.entity.question.OpenQuestion;
import org.hibernate.performance.search.model.entity.question.Question;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.orm.mapping.HibernateOrmMappingConfigurationContext;
import org.hibernate.search.mapper.orm.mapping.HibernateOrmSearchMappingConfigurer;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.ProgrammaticMappingConfigurationContext;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.TypeMappingStep;

public class SearchProgrammaticMapping implements HibernateOrmSearchMappingConfigurer {

	@Override
	public void configure(HibernateOrmMappingConfigurationContext context) {
		ProgrammaticMappingConfigurationContext mapping = context.programmaticMapping();

		TypeMappingStep company = mapping.type( Company.class );
		company.indexed();
		company.property( "id" ).genericField();
		company.property( "legalName" ).keywordField();
		company.property( "description" ).fullTextField().analyzer( "default" );
		company.property( "businessUnits" ).indexedEmbedded()
				.includeDepth( 1 );

		TypeMappingStep businessUnit = mapping.type( BusinessUnit.class );
		businessUnit.indexed();
		businessUnit.property( "id" ).genericField();
		businessUnit.property( "name" ).keywordField();
		businessUnit.property( "owner" ).indexedEmbedded()
				.includeDepth( 1 );

		TypeMappingStep employee = mapping.type( Employee.class );
		employee.indexed();
		employee.property( "id" ).genericField();
		employee.property( "name" ).keywordField();
		employee.property( "surname" ).keywordField();
		employee.property( "socialSecurityNumber" ).keywordField();
		employee.property( "company" ).indexedEmbedded().indexingDependency().reindexOnUpdate( ReindexOnUpdate.NO );
		employee.property( "businessUnit" ).indexedEmbedded().indexingDependency().reindexOnUpdate(
				ReindexOnUpdate.NO );
		employee.property( "manager" ).indexedEmbedded()
				// index 4 managers up to the hierarchy
				.includeDepth( 4 );

		TypeMappingStep manager = mapping.type( Manager.class );
		manager.indexed();
		manager.property( "employees" ).indexedEmbedded()
				// index 1 employee-level down to the hierarchy
				.includeDepth( 1 );

		TypeMappingStep questionnaireDefinition = mapping.type( QuestionnaireDefinition.class );
		questionnaireDefinition.indexed();
		questionnaireDefinition.property( "id" ).genericField();
		questionnaireDefinition.property( "title" ).fullTextField().analyzer( "default" );
		questionnaireDefinition.property( "description" ).fullTextField().analyzer( "default" );
		questionnaireDefinition.property( "year" ).genericField().sortable( Sortable.YES );
		questionnaireDefinition.property( "company" ).indexedEmbedded()
				.indexingDependency().reindexOnUpdate( ReindexOnUpdate.NO );
		questionnaireDefinition.property( "questions" ).indexedEmbedded()
				.includeDepth( 1 );

		TypeMappingStep question = mapping.type( Question.class );
		question.indexed();
		question.property( "id" ).genericField();
		question.property( "questionnaire" ).indexedEmbedded()
				.includeDepth( 1 );
		question.property( "text" ).fullTextField().analyzer( "default" );

		TypeMappingStep openQuestion = mapping.type( OpenQuestion.class );
		openQuestion.indexed();

		TypeMappingStep closedQuestion = mapping.type( ClosedQuestion.class );
		closedQuestion.indexed();
		closedQuestion.property( "weight" ).genericField();

		TypeMappingStep questionnaireInstance = mapping.type( QuestionnaireInstance.class );
		questionnaireInstance.indexed();
		questionnaireInstance.property( "id" ).genericField();
		questionnaireInstance.property( "uniqueCode" ).genericField();
		questionnaireInstance.property( "openAnswers" ).indexedEmbedded().includeDepth( 1 );
		questionnaireInstance.property( "closedAnswers" ).indexedEmbedded().includeDepth( 1 );
		questionnaireInstance.property( "definition" ).indexedEmbedded().indexingDependency()
				.reindexOnUpdate( ReindexOnUpdate.NO );
		questionnaireInstance.property( "approval" ).indexedEmbedded().indexingDependency()
				.reindexOnUpdate( ReindexOnUpdate.NO );
		questionnaireInstance.property( "subject" ).indexedEmbedded().indexingDependency()
				.reindexOnUpdate( ReindexOnUpdate.NO );

		TypeMappingStep openAnswer = mapping.type( OpenAnswer.class );
		openAnswer.indexed();
		openAnswer.property( "id" ).genericField();
		openAnswer.property( "questionnaire" ).indexedEmbedded().includeDepth( 1 );
		openAnswer.property( "question" ).indexedEmbedded().includeDepth( 2 )
				.indexingDependency().reindexOnUpdate( ReindexOnUpdate.NO );
		openAnswer.property( "text" ).fullTextField().analyzer( "default" );

		TypeMappingStep closedAnswer = mapping.type( ClosedAnswer.class );
		closedAnswer.indexed();
		closedAnswer.property( "id" ).genericField();
		closedAnswer.property( "questionnaire" ).indexedEmbedded().includeDepth( 1 );
		closedAnswer.property( "question" ).indexedEmbedded().includeDepth( 2 )
				.indexingDependency().reindexOnUpdate( ReindexOnUpdate.NO );
		closedAnswer.property( "choice" ).genericField();

		TypeMappingStep performanceSummary = mapping.type( PerformanceSummary.class );
		performanceSummary.indexed();
		performanceSummary.property( "id" ).genericField();
		performanceSummary.property( "employee" ).indexedEmbedded().includeDepth( 3 )
				.indexingDependency().reindexOnUpdate( ReindexOnUpdate.NO );
		performanceSummary.property( "year" ).genericField();
		performanceSummary.property( "maxScore" ).genericField().projectable( Projectable.YES );
		performanceSummary.property( "employeeScore" ).genericField().projectable( Projectable.YES );
	}
}
