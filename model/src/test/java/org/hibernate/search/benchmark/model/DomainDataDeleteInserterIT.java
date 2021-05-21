package org.hibernate.search.benchmark.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.benchmark.model.application.DomainDataDeleteInserter;
import org.hibernate.search.benchmark.model.application.DomainDataInitializer;
import org.hibernate.search.benchmark.model.application.HibernateORMHelper;
import org.hibernate.search.benchmark.model.application.ModelService;
import org.hibernate.search.benchmark.model.entity.Employee;
import org.hibernate.search.benchmark.model.entity.answer.QuestionnaireInstance;
import org.hibernate.search.benchmark.model.entity.performance.PerformanceSummary;
import org.hibernate.search.benchmark.model.entity.question.ClosedQuestion;
import org.hibernate.search.benchmark.model.entity.question.OpenQuestion;
import org.hibernate.search.benchmark.model.entity.question.QuestionnaireDefinition;
import org.hibernate.search.benchmark.model.param.RelationshipSize;
import org.hibernate.search.benchmark.model.service.EmployeeRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DomainDataDeleteInserterIT {

	private SessionFactory sessionFactory;
	private DomainDataDeleteInserter deleteInserter;

	@BeforeEach
	public void beforeEach() {
		ModelService modelService = new NoIndexingModelService();
		sessionFactory = HibernateORMHelper.buildSessionFactory( new Properties() );
		deleteInserter = new DomainDataDeleteInserter( sessionFactory, RelationshipSize.MEDIUM );
		DomainDataInitializer initializer = new DomainDataInitializer( modelService, sessionFactory, RelationshipSize.MEDIUM );
		initializer.initAllCompanyData( 0 );
	}

	@AfterEach
	public void afterEach() {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	@Test
	public void employees() {
		try ( Session session = sessionFactory.openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );
			assertThat( repository.count( Employee.class ) ).isEqualTo( 6 );
		}

		deleteInserter.inTransaction( (session, delIns) -> {
			delIns.createEmployee( session, 0, 999997 );
			delIns.createEmployee( session, 0, 999998 );
			delIns.createEmployee( session, 3, 999999 );
		} );

		try ( Session session = sessionFactory.openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );
			assertThat( repository.count( Employee.class ) ).isEqualTo( 9 );
		}

		deleteInserter.inTransaction( (session, delIns) -> {
			delIns.deleteEmployee( session, 999997 );
			delIns.deleteEmployee( session, 999998 );
			delIns.deleteEmployee( session, 999999 );
		} );

		try ( Session session = sessionFactory.openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );
			assertThat( repository.count( Employee.class ) ).isEqualTo( 6 );
		}
	}

	@Test
	public void questionnaireDefinitions() {
		try ( Session session = sessionFactory.openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );
			assertThat( repository.count( QuestionnaireDefinition.class ) ).isEqualTo( 2 );
			assertThat( repository.count( ClosedQuestion.class ) ).isEqualTo( 4 );
			assertThat( repository.count( OpenQuestion.class ) ).isEqualTo( 4 );
		}

		deleteInserter.inTransaction( (session, delIns) -> {
			delIns.createQuestionnaireDefinition( session, 0, 999997 );
			delIns.createQuestionnaireDefinition( session, 0, 999998 );
			delIns.createQuestionnaireDefinition( session, 0, 999999 );
		} );

		try ( Session session = sessionFactory.openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );
			assertThat( repository.count( QuestionnaireDefinition.class ) ).isEqualTo( 5 );
			assertThat( repository.count( ClosedQuestion.class ) ).isEqualTo( 10 );
			assertThat( repository.count( OpenQuestion.class ) ).isEqualTo( 10 );
		}

		deleteInserter.inTransaction( (session, delIns) -> {
			delIns.deleteQuestionnaireDefinition( session, 999997 );
			delIns.deleteQuestionnaireDefinition( session, 999998 );
			delIns.deleteQuestionnaireDefinition( session, 999999 );
		} );

		try ( Session session = sessionFactory.openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );
			assertThat( repository.count( QuestionnaireDefinition.class ) ).isEqualTo( 2 );
			assertThat( repository.count( ClosedQuestion.class ) ).isEqualTo( 4 );
			assertThat( repository.count( OpenQuestion.class ) ).isEqualTo( 4 );
		}
	}

	@Test
	public void questionnaireInstances() {
		try ( Session session = sessionFactory.openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );
			assertThat( repository.count( QuestionnaireDefinition.class ) ).isEqualTo( 2 );
			assertThat( repository.count( ClosedQuestion.class ) ).isEqualTo( 4 );
			assertThat( repository.count( OpenQuestion.class ) ).isEqualTo( 4 );
			assertThat( repository.count( Employee.class ) ).isEqualTo( 6 );
			assertThat( repository.count( QuestionnaireInstance.class ) ).isEqualTo( 48 );
			assertThat( repository.countFilledClosedAnswer() ).isEqualTo( 96 );
			assertThat( repository.countFilledOpenAnswer() ).isEqualTo( 96 );
		}

		deleteInserter.inTransaction( (session, delIns) -> {
			delIns.deleteQuestionnaireInstancesFor( session, 0 );
			delIns.deleteQuestionnaireInstancesFor( session, 3 );
		} );

		try ( Session session = sessionFactory.openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );
			assertThat( repository.count( QuestionnaireDefinition.class ) ).isEqualTo( 2 );
			assertThat( repository.count( ClosedQuestion.class ) ).isEqualTo( 4 );
			assertThat( repository.count( OpenQuestion.class ) ).isEqualTo( 4 );
			assertThat( repository.count( Employee.class ) ).isEqualTo( 6 );
			assertThat( repository.count( QuestionnaireInstance.class ) ).isEqualTo( 28 );
			assertThat( repository.countFilledClosedAnswer() ).isEqualTo( 56 );
			assertThat( repository.countFilledOpenAnswer() ).isEqualTo( 56 );
		}

		deleteInserter.inTransaction( (session, delIns) -> {
			delIns.createAndFillQuestionnaireInstancesFor( session, 0 );
			delIns.createAndFillQuestionnaireInstancesFor( session, 3 );
		} );

		try ( Session session = sessionFactory.openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );
			assertThat( repository.count( QuestionnaireDefinition.class ) ).isEqualTo( 2 );
			assertThat( repository.count( ClosedQuestion.class ) ).isEqualTo( 4 );
			assertThat( repository.count( OpenQuestion.class ) ).isEqualTo( 4 );
			assertThat( repository.count( Employee.class ) ).isEqualTo( 6 );
			assertThat( repository.count( QuestionnaireInstance.class ) ).isEqualTo( 48 );
			assertThat( repository.countFilledClosedAnswer() ).isEqualTo( 96 );
			assertThat( repository.countFilledOpenAnswer() ).isEqualTo( 96 );
		}
	}

	@Test
	public void performanceSummaries() {
		try ( Session session = sessionFactory.openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );
			assertThat( repository.count( QuestionnaireDefinition.class ) ).isEqualTo( 2 );
			assertThat( repository.count( Employee.class ) ).isEqualTo( 6 );
			assertThat( repository.count( QuestionnaireInstance.class ) ).isEqualTo( 48 );
			assertThat( repository.count( PerformanceSummary.class ) ).isEqualTo( 12 );
		}

		deleteInserter.inTransaction( (session, delIns) -> {
			delIns.deletePerformanceSummaryFor( session, 0 );
			delIns.deletePerformanceSummaryFor( session, 3 );
		} );

		try ( Session session = sessionFactory.openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );
			assertThat( repository.count( QuestionnaireDefinition.class ) ).isEqualTo( 2 );
			assertThat( repository.count( Employee.class ) ).isEqualTo( 6 );
			assertThat( repository.count( QuestionnaireInstance.class ) ).isEqualTo( 48 );
			assertThat( repository.count( PerformanceSummary.class ) ).isEqualTo( 8 );
		}

		deleteInserter.inTransaction( (session, delIns) -> {
			delIns.createPerformanceSummaryFor( session, 0 );
			delIns.createPerformanceSummaryFor( session, 3 );
		} );

		try ( Session session = sessionFactory.openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );
			assertThat( repository.count( QuestionnaireDefinition.class ) ).isEqualTo( 2 );
			assertThat( repository.count( Employee.class ) ).isEqualTo( 6 );
			assertThat( repository.count( QuestionnaireInstance.class ) ).isEqualTo( 48 );
			assertThat( repository.count( PerformanceSummary.class ) ).isEqualTo( 12 );
		}
	}
}
