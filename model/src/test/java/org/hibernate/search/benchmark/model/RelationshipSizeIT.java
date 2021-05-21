package org.hibernate.search.benchmark.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.benchmark.model.application.DomainDataInitializer;
import org.hibernate.search.benchmark.model.application.HibernateORMHelper;
import org.hibernate.search.benchmark.model.application.ModelService;
import org.hibernate.search.benchmark.model.entity.BusinessUnit;
import org.hibernate.search.benchmark.model.entity.Company;
import org.hibernate.search.benchmark.model.entity.Employee;
import org.hibernate.search.benchmark.model.entity.Manager;
import org.hibernate.search.benchmark.model.entity.answer.QuestionnaireInstance;
import org.hibernate.search.benchmark.model.entity.performance.PerformanceSummary;
import org.hibernate.search.benchmark.model.entity.question.ClosedQuestion;
import org.hibernate.search.benchmark.model.entity.question.OpenQuestion;
import org.hibernate.search.benchmark.model.entity.question.Question;
import org.hibernate.search.benchmark.model.entity.question.QuestionnaireDefinition;
import org.hibernate.search.benchmark.model.param.RelationshipSize;
import org.hibernate.search.benchmark.model.service.EmployeeRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RelationshipSizeIT {

	private SessionFactory sessionFactory;

	@Test
	public void small_x1() {
		test( RelationshipSize.SMALL, 1, 1 );
	}

	@Test
	public void small_x300() {
		test( RelationshipSize.SMALL, 1, 300 );
	}

	@Test
	public void medium_x1() {
		test( RelationshipSize.MEDIUM, 48, 1 );
	}

	@Test
	public void medium_x30() {
		test( RelationshipSize.MEDIUM, 48, 30 );
	}

	@Test
	public void large_x1() {
		test( RelationshipSize.LARGE, 11880, 1 );
	}

	@Test
	public void large_x3() {
		test( RelationshipSize.LARGE, 11880, 3 );
	}

	@BeforeEach
	public void beforeEach() {
		sessionFactory = HibernateORMHelper.buildSessionFactory( new Properties() );
	}

	@AfterEach
	public void afterEach() {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	private void test(RelationshipSize relationshipSize, int questionnaireInstancesForCompany, int scaleSize) {
		ModelService modelService = new NoIndexingModelService();
		DomainDataInitializer domainDataInitializer = new DomainDataInitializer( modelService, sessionFactory, relationshipSize );
		for ( int i = 0; i < scaleSize; i++ ) {
			domainDataInitializer.initAllCompanyData( i );
		}

		try ( Session session = sessionFactory.openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );

			int companies = scaleSize;
			int units = relationshipSize.getUnitsPerCompany() * companies;
			int employees = relationshipSize.getEmployeesPerBusinessUnit() * units;

			assertThat( repository.count( Company.class ) ).isEqualTo( companies );
			assertThat( repository.count( BusinessUnit.class ) ).isEqualTo( units );
			assertThat( repository.count( Manager.class ) ).isEqualTo( units );
			assertThat( repository.count( Employee.class ) ).isEqualTo( employees );

			int questionnaireDefinitions = relationshipSize.getQuestionnaireDefinitionsForCompany() * companies;
			int closedQuestionsForQuestionnaire = relationshipSize.getClosedQuestionsWeightsForQuestionnaire().length;
			int closedQuestions = closedQuestionsForQuestionnaire * questionnaireDefinitions;
			int openQuestions = relationshipSize.getOpenQuestionsForQuestionnaire() * questionnaireDefinitions;

			assertThat( repository.count( QuestionnaireDefinition.class ) ).isEqualTo( questionnaireDefinitions );
			assertThat( repository.count( Question.class ) ).isEqualTo( closedQuestions + openQuestions );
			assertThat( repository.count( ClosedQuestion.class ) ).isEqualTo( closedQuestions );
			assertThat( repository.count( OpenQuestion.class ) ).isEqualTo( openQuestions );

			int questionnaireInstances = questionnaireInstancesForCompany * scaleSize;
			int closedAnswers = closedQuestionsForQuestionnaire * questionnaireInstances;
			int openAnswers = relationshipSize.getOpenQuestionsForQuestionnaire() * questionnaireInstances;

			assertThat( repository.count( QuestionnaireInstance.class ) ).isEqualTo( questionnaireInstances );
			assertThat( repository.countFilledClosedAnswer() ).isEqualTo( closedAnswers );
			assertThat( repository.countFilledOpenAnswer() ).isEqualTo( openAnswers );

			int summaries = relationshipSize.getQuestionnaireDefinitionsForCompany() * employees;

			assertThat( repository.count( PerformanceSummary.class ) ).isEqualTo( summaries );
		}
	}
}
