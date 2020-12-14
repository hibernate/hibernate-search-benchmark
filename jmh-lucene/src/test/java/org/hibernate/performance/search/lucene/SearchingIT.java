package org.hibernate.performance.search.lucene;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataFiller;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.application.ModelService;
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
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;
import org.hibernate.performance.search.tck.TckBackendHelperFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchingIT {

	private SessionFactory sessionFactory;
	private ModelService modelService;

	@BeforeAll
	public void beforeAll() throws Exception {
		sessionFactory = HibernateORMHelper.buildSessionFactory( TckBackendHelperFactory.manualProperties() );
		modelService = TckBackendHelperFactory.getModelService();

		new DomainDataFiller( sessionFactory ).fillData( 0 );
		try ( Session session = ( sessionFactory.openSession() ) ) {
			modelService.massIndexing( session );
		}
	}

	@AfterAll
	public void afterAll() {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	@Test
	public void company() {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			// match
			List<Company> companies = modelService.search( session, Company.class, "legalName", "Company0" );
			assertThat( companies ).hasSize( 1 );

			// no match
			companies = modelService.search( session, Company.class, "legalName", "CompanyX" );
			assertThat( companies ).isEmpty();

			// indexEmbedded match
			companies = modelService.search( session, Company.class, "businessUnits.name", "Unit7" );
			assertThat( companies ).hasSize( 1 );

			// search by id
			companies = modelService.search( session, Company.class, "id", 0 );
			assertThat( companies ).hasSize( 1 );
		}
	}

	@Test
	public void businessUnit() {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			// match
			List<BusinessUnit> businessUnits = modelService.search( session, BusinessUnit.class, "name", "Unit7" );
			assertThat( businessUnits ).hasSize( 1 );

			// no match
			businessUnits = modelService.search( session, BusinessUnit.class, "name", "UnitX" );
			assertThat( businessUnits ).isEmpty();

			// indexEmbedded match
			businessUnits = modelService.search( session, BusinessUnit.class, "owner.legalName", "Company0" );
			assertThat( businessUnits ).hasSize( 10 );
		}
	}

	@Test
	public void employee() {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			// match
			List<Employee> employees = modelService.search( session, Employee.class, "name", "name77" );
			assertThat( employees ).hasSize( 1 );

			// no match
			employees = modelService.search( session, Employee.class, "name", "nameX" );
			assertThat( employees ).isEmpty();

			// count
			long count = modelService.count( session, Employee.class, "surname", "surname77" );
			assertThat( count ).isEqualTo( 1 );

			// range
			employees = modelService.range( session, Employee.class, "socialSecurityNumber",
					"socialSecurityNumber32", "socialSecurityNumber41"
			);
			assertThat( employees ).extracting( "id" )
					.containsExactlyInAnyOrder( 32, 33, 34, 35, 36, 37, 38, 39, 4, 40, 41 );

			// indexEmbedded match
			count = modelService.count( session, Employee.class, "company.legalName", "Company0" );
			assertThat( count ).isEqualTo( 100 );

			// projection
			List<Object> ids = modelService.projectId( session, Employee.class, "businessUnit.name", "Unit7" );
			assertThat( ids ).containsExactlyInAnyOrder( 70, 71, 72, 73, 74, 75, 76, 77, 78, 79 );

			// traverse the tree up
			employees = modelService.search( session, Employee.class, "manager.manager.manager.manager.name", "name0" );
			assertThat( employees ).extracting( "id" ).containsExactlyInAnyOrder(
					71, 72, 73, 74, 75, 76, 77, 78, 79,
					81, 82, 83, 84, 85, 86, 87, 88, 89,
					91, 92, 93, 94, 95, 96, 97, 98, 99
			);

			// traverse the tree down
			List<Manager> managers = modelService.search( session, Manager.class, "employees.name", "name77" );
			assertThat( managers ).extracting( "id" ).containsExactlyInAnyOrder( 70 );
		}
	}

	@Test
	public void questions() {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			// range and order on numeric field
			List<QuestionnaireDefinition> questionnaires = modelService.rangeOrderBy(
					session, QuestionnaireDefinition.class, "year", 2021, 2025 );
			assertThat( questionnaires ).extracting( "id" ).containsExactly( 1, 2, 3, 4, 5 );

			// full text search
			questionnaires = modelService.search( session, QuestionnaireDefinition.class, "title", "2023" );
			assertThat( questionnaires ).extracting( "id" ).containsExactly( 3 );
			questionnaires = modelService.search( session, QuestionnaireDefinition.class, "description", "2025" );
			assertThat( questionnaires ).extracting( "id" ).containsExactly( 5 );

			// indexEmbedded match
			long count = modelService.count( session, QuestionnaireDefinition.class, "company.legalName", "Company0" );
			assertThat( count ).isEqualTo( 10 );

			// full text search on indexEmbedded
			questionnaires = modelService.search( session, QuestionnaireDefinition.class, "questions.text", "2022" );
			assertThat( questionnaires ).extracting( "id" ).containsExactly( 2 );

			// numeric field
			List<ClosedQuestion> questions = modelService.search( session, ClosedQuestion.class, "weight", 7 );
			assertThat( questions ).hasSize( 40 );
		}
	}

	@Test
	public void answers() {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			// find all bounded
			List<QuestionnaireInstance> questionnaires = modelService
					.search( session, QuestionnaireInstance.class, 12080 );
			assertThat( questionnaires ).hasSize( 11880 );

			// find unbounded
			List<Answer> search = modelService.search( session, Answer.class, Integer.MAX_VALUE );
			assertThat( search ).hasSize( 237600 );

			// high-match count on full text field
			long count = modelService.count( session, OpenAnswer.class, "text", "search" );
			assertThat( count ).isEqualTo( 44383 );

			// high-match range
			List<ClosedAnswer> closedAnswers = modelService.range( session, ClosedAnswer.class, "choice", 5, 7 );
			// 100 is the max results
			assertThat( closedAnswers ).hasSize( 100 );

			// high-match on nested full text field
			questionnaires = modelService.search(
					session, QuestionnaireInstance.class, "openAnswers.text", "annotation" );
			// 100 is the max results
			assertThat( questionnaires ).hasSize( 100 );

			// more predicates
			closedAnswers = modelService.searchAnd(
					session, ClosedAnswer.class, "questionnaire.uniqueCode", "0:0:0", "choice", 7 );
			assertThat( closedAnswers ).extracting( "id" ).containsExactly( 241 );

			List<PerformanceSummary> performances = modelService.searchAnd(
					session, PerformanceSummary.class, "employee.manager.manager.surname", "surname0", "year", 2025 );
			assertThat( performances ).hasSize( 22 );
			List<List<?>> projections = modelService.project(
					session, PerformanceSummary.class, "employee.surname", "surname77", "year", 2025, "maxScore",
					"employeeScore"
			);
			assertThat( projections ).containsExactly( Arrays.asList( 4480, 2333 ) );
		}
	}

}
