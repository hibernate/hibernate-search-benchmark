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
import org.hibernate.performance.search.model.entity.question.Question;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;
import org.hibernate.search.engine.backend.analysis.AnalyzerNames;
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
		company.property( "legalName" ).fullTextField().analyzer( "default" );
		company.property( "description" ).fullTextField().analyzer( "default" );
		company.property( "businessUnits" ).indexedEmbedded()
				.includeDepth( 1 );

		TypeMappingStep businessUnit = mapping.type( BusinessUnit.class );
		businessUnit.indexed();
		businessUnit.property( "name" ).fullTextField().analyzer( "default" );
		businessUnit.property( "owner" ).indexedEmbedded()
				.includeDepth( 1 );

		TypeMappingStep employee = mapping.type( Employee.class );
		employee.indexed();
		employee.property( "firstName" ).fullTextField().analyzer( "default" );
		employee.property( "surname" ).fullTextField().analyzer( "default" );
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
		questionnaireDefinition.property( "title" ).fullTextField().analyzer( "default" );
		questionnaireDefinition.property( "description" ).fullTextField().analyzer( "default" );
		questionnaireDefinition.property( "year" ).genericField().sortable( Sortable.YES );
		questionnaireDefinition.property( "company" ).indexedEmbedded()
				.indexingDependency().reindexOnUpdate( ReindexOnUpdate.NO );
		questionnaireDefinition.property( "questions" ).indexedEmbedded()
				.includeDepth( 1 );

		TypeMappingStep question = mapping.type( Question.class );
		question.indexed();
		question.property( "questionnaire" ).indexedEmbedded()
				.includeDepth( 1 );
		question.property( "text" ).fullTextField().analyzer( "default" );

		TypeMappingStep closedQuestion = mapping.type( ClosedQuestion.class );
		closedQuestion.property( "weight" ).genericField();

		TypeMappingStep questionnaireInstance = mapping.type( QuestionnaireInstance.class );
		questionnaireInstance.indexed();
		questionnaireInstance.property( "uniqueCode" ).genericField();
		questionnaireInstance.property( "openAnswers" ).indexedEmbedded().includeDepth( 1 );
		questionnaireInstance.property( "closedAnswers" ).indexedEmbedded().includeDepth( 1 );
		questionnaireInstance.property( "definition" ).indexedEmbedded().indexingDependency()
				.reindexOnUpdate( ReindexOnUpdate.NO );
		questionnaireInstance.property( "approval" ).indexedEmbedded().indexingDependency()
				.reindexOnUpdate( ReindexOnUpdate.NO );
		questionnaireInstance.property( "subject" ).indexedEmbedded().indexingDependency()
				.reindexOnUpdate( ReindexOnUpdate.NO );
		questionnaireInstance.property( "notes" ).fullTextField().analyzer( AnalyzerNames.DEFAULT );

		TypeMappingStep openAnswer = mapping.type( OpenAnswer.class );
		openAnswer.indexed();
		openAnswer.property( "questionnaire" ).indexedEmbedded().includeDepth( 1 );
		openAnswer.property( "question" ).indexedEmbedded().includeDepth( 2 )
				.indexingDependency().reindexOnUpdate( ReindexOnUpdate.NO );
		openAnswer.property( "text" ).fullTextField().analyzer( "default" );

		TypeMappingStep closedAnswer = mapping.type( ClosedAnswer.class );
		closedAnswer.indexed();
		closedAnswer.property( "questionnaire" ).indexedEmbedded().includeDepth( 1 );
		closedAnswer.property( "question" ).indexedEmbedded().includeDepth( 2 )
				.indexingDependency().reindexOnUpdate( ReindexOnUpdate.NO );
		closedAnswer.property( "choice" ).genericField();

		TypeMappingStep performanceSummary = mapping.type( PerformanceSummary.class );
		performanceSummary.indexed();
		performanceSummary.property( "employee" ).indexedEmbedded().includeDepth( 3 )
				.indexingDependency().reindexOnUpdate( ReindexOnUpdate.NO );
		performanceSummary.property( "year" ).genericField();
		performanceSummary.property( "maxScore" ).genericField().projectable( Projectable.YES );
		performanceSummary.property( "employeeScore" ).genericField().projectable( Projectable.YES );
	}
}
