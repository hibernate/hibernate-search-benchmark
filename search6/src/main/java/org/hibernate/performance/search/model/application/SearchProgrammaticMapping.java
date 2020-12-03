package org.hibernate.performance.search.model.application;

import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;
import org.hibernate.performance.search.model.entity.answer.Answer;
import org.hibernate.performance.search.model.entity.answer.ClosedAnswer;
import org.hibernate.performance.search.model.entity.answer.OpenAnswer;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.performance.PerformanceSummary;
import org.hibernate.performance.search.model.entity.question.ClosedQuestion;
import org.hibernate.performance.search.model.entity.question.OpenQuestion;
import org.hibernate.performance.search.model.entity.question.Question;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;
import org.hibernate.search.mapper.orm.mapping.HibernateOrmMappingConfigurationContext;
import org.hibernate.search.mapper.orm.mapping.HibernateOrmSearchMappingConfigurer;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.ProgrammaticMappingConfigurationContext;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.TypeMappingStep;
import org.hibernate.search.mapper.pojo.model.path.PojoModelPath;

public class SearchProgrammaticMapping implements HibernateOrmSearchMappingConfigurer {

	@Override
	public void configure(HibernateOrmMappingConfigurationContext context) {
		ProgrammaticMappingConfigurationContext mapping = context.programmaticMapping();

		TypeMappingStep employee = mapping.type( Employee.class );
		employee.indexed();
		employee.property( "name" ).keywordField();
		employee.property( "surname" ).keywordField();
		employee.property( "socialSecurityNumber" ).keywordField();
		employee.property( "company" ).indexedEmbedded().indexingDependency().reindexOnUpdate( ReindexOnUpdate.NO );
		employee.property( "businessUnit" ).indexedEmbedded().indexingDependency().reindexOnUpdate(
				ReindexOnUpdate.NO );
		employee.property( "manager" ).indexedEmbedded()
				// index 3 managers up to the hierarchy
				.includeDepth( 3 )
				.associationInverseSide( PojoModelPath.parse( "employees" ) );

		TypeMappingStep manager = mapping.type( Manager.class );
		manager.indexed();
		manager.property( "employees" ).indexedEmbedded()
				// index 2 employee-levels down to the hierarchy
				.includeDepth( 1 )
				.associationInverseSide( PojoModelPath.parse( "manager" ) );

		TypeMappingStep company = mapping.type( Company.class );
		company.property( "legalName" ).keywordField();
		company.property( "businessUnits" ).indexedEmbedded()
				.includeDepth( 1 )
				.associationInverseSide( PojoModelPath.parse( "owner" ) );

		TypeMappingStep businessUnit = mapping.type( BusinessUnit.class );
		businessUnit.indexed();
		businessUnit.property( "name" ).keywordField();
		businessUnit.property( "owner" ).indexedEmbedded()
				.includeDepth( 1 )
				.associationInverseSide( PojoModelPath.parse( "businessUnits" ) );

		TypeMappingStep questionnaireDefinition = mapping.type( QuestionnaireDefinition.class );
		questionnaireDefinition.indexed();
		questionnaireDefinition.property( "title" ).fullTextField();
		questionnaireDefinition.property( "description" ).fullTextField();
		questionnaireDefinition.property( "year" ).genericField();
		questionnaireDefinition.property( "questions" ).indexedEmbedded()
				.includeDepth( 1 )
				.associationInverseSide( PojoModelPath.parse( "questionnaire" ) );

		TypeMappingStep question = mapping.type( Question.class );
		question.indexed();
		question.property( "questionnaire" ).indexedEmbedded()
				.includeDepth( 1 )
				.associationInverseSide( PojoModelPath.parse( "questions" ) );
		question.property( "text" ).fullTextField();

		TypeMappingStep openQuestion = mapping.type( OpenQuestion.class );
		openQuestion.indexed();

		TypeMappingStep closedQuestion = mapping.type( ClosedQuestion.class );
		closedQuestion.indexed();
		closedQuestion.property( "weight" ).genericField();

		TypeMappingStep questionnaireInstance = mapping.type( QuestionnaireInstance.class );
		questionnaireInstance.indexed();
		questionnaireInstance.property( "uniqueCode" ).documentId();
		questionnaireInstance.property( "definition" ).indexedEmbedded().indexingDependency()
				.reindexOnUpdate( ReindexOnUpdate.NO );
		questionnaireInstance.property( "approval" ).indexedEmbedded().indexingDependency()
				.reindexOnUpdate( ReindexOnUpdate.NO );
		questionnaireInstance.property( "subject" ).indexedEmbedded().indexingDependency()
				.reindexOnUpdate( ReindexOnUpdate.NO );
		questionnaireInstance.property( "openAnswers" ).indexedEmbedded().includeDepth( 1 )
				.associationInverseSide( PojoModelPath.parse( "questionnaire" ) );
		questionnaireInstance.property( "closedAnswers" ).indexedEmbedded().includeDepth( 1 )
				.associationInverseSide( PojoModelPath.parse( "questionnaire" ) );

		TypeMappingStep openAnswer = mapping.type( OpenAnswer.class );
		openAnswer.indexed();
		openAnswer.property( "questionnaire" ).indexedEmbedded().includeDepth( 1 )
				.associationInverseSide( PojoModelPath.parse( "openAnswers" ) );
		openAnswer.property( "question" ).indexedEmbedded().includeDepth( 2 )
				.indexingDependency().reindexOnUpdate( ReindexOnUpdate.NO );
		openAnswer.property( "text" ).fullTextField();

		TypeMappingStep closedAnswer = mapping.type( ClosedAnswer.class );
		closedAnswer.indexed();
		closedAnswer.property( "questionnaire" ).indexedEmbedded().includeDepth( 1 )
				.associationInverseSide( PojoModelPath.parse( "closedAnswers" ) );
		closedAnswer.property( "question" ).indexedEmbedded().includeDepth( 2 )
				.indexingDependency().reindexOnUpdate( ReindexOnUpdate.NO );
		closedAnswer.property( "choice" ).genericField();

		TypeMappingStep performanceSummary = mapping.type( PerformanceSummary.class );
		performanceSummary.indexed();
		performanceSummary.property( "employee" ).indexedEmbedded().includeDepth( 3 )
				.indexingDependency().reindexOnUpdate( ReindexOnUpdate.NO );
		performanceSummary.property( "year" ).genericField();
		performanceSummary.property( "maxScore" ).genericField();
		performanceSummary.property( "employeeScore" ).genericField();
	}
}
