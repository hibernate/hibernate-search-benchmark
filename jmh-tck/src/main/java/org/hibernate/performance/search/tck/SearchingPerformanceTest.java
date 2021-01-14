package org.hibernate.performance.search.tck;

import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataFiller;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.model.application.ModelServiceFactory;
import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;
import org.hibernate.performance.search.model.entity.answer.ClosedAnswer;
import org.hibernate.performance.search.model.entity.answer.OpenAnswer;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.performance.PerformanceSummary;
import org.hibernate.performance.search.model.entity.question.ClosedQuestion;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;
import org.hibernate.performance.search.model.param.RelationshipSize;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public abstract class SearchingPerformanceTest {

	@Param({ "SMALL" })
	private RelationshipSize relationshipSize;

	@Param({ "100" })
	private int initialCompanyCount;

	private final ModelService modelService;
	private final SessionFactory sessionFactory;

	public SearchingPerformanceTest() {
		modelService = ModelServiceFactory.create();
		sessionFactory = HibernateORMHelper.buildSessionFactory( manualProperties( modelService ) );
	}

	@Setup(Level.Trial)
	public void setup() throws Exception {
		DomainDataFiller domainDataFiller = new DomainDataFiller( sessionFactory, relationshipSize );
		for ( int i = 0; i < initialCompanyCount; i++ ) {
			domainDataFiller.fillData( i );
		}

		try ( Session session = ( sessionFactory.openSession() ) ) {
			modelService.massIndexing( session );
		}
	}

	@TearDown(Level.Trial)
	public void tearDown() {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	@Benchmark
	@Threads(3)
	public void company(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			// match
			List<Company> companies = modelService.search( session, Company.class, "legalName", "Company0" );
			blackhole.consume( companies );

			// no match
			companies = modelService.search( session, Company.class, "legalName", "CompanyX" );
			blackhole.consume( companies );

			// indexEmbedded match
			companies = modelService.search( session, Company.class, "businessUnits.name", "Unit7" );
			blackhole.consume( companies );

			// search by id
			companies = modelService.searchById( session, Company.class, "id", 0 );
			blackhole.consume( companies );
		}
	}

	@Benchmark
	@Threads(3)
	public void businessUnit(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			// match
			List<BusinessUnit> businessUnits = modelService.search( session, BusinessUnit.class, "name", "Unit7" );
			blackhole.consume( businessUnits );

			// no match
			businessUnits = modelService.search( session, BusinessUnit.class, "name", "UnitX" );
			blackhole.consume( businessUnits );

			// indexEmbedded match
			businessUnits = modelService.search( session, BusinessUnit.class, "owner.legalName", "Company0" );
			blackhole.consume( businessUnits );
		}
	}

	@Benchmark
	@Threads(3)
	public void employee(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			// match
			List<Employee> employees = modelService.search( session, Employee.class, "firstName", "name77" );
			blackhole.consume( employees );

			// no match
			employees = modelService.search( session, Employee.class, "firstName", "nameX" );
			blackhole.consume( employees );

			// count
			long count = modelService.count( session, Employee.class, "surname", "surname77" );
			blackhole.consume( count );

			// range
			employees = modelService.range( session, Employee.class, "socialSecurityNumber",
					"socialSecurityNumber32", "socialSecurityNumber41"
			);
			blackhole.consume( employees );

			// indexEmbedded match
			count = modelService.count( session, Employee.class, "company.legalName", "Company0" );
			blackhole.consume( count );

			// projection
			List<Object> ids = modelService.projectId( session, Employee.class, "businessUnit.name", "Unit7" );
			blackhole.consume( ids );

			// traverse the tree up
			employees = modelService.search( session, Employee.class, "manager.manager.manager.manager.firstName", "name0" );
			blackhole.consume( employees );

			// traverse the tree down
			List<Manager> managers = modelService.search( session, Manager.class, "employees.firstName", "name77" );
			blackhole.consume( managers );
		}
	}

	@Benchmark
	@Threads(3)
	public void questions(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			// range and order on numeric field
			List<QuestionnaireDefinition> questionnaires = modelService.rangeOrderBy(
					session, QuestionnaireDefinition.class, "year", 2021, 2025 );
			blackhole.consume( questionnaires );

			// full text search
			questionnaires = modelService.search( session, QuestionnaireDefinition.class, "title", "2023" );
			blackhole.consume( questionnaires );
			questionnaires = modelService.search( session, QuestionnaireDefinition.class, "description", "2025" );
			blackhole.consume( questionnaires );

			// indexEmbedded match
			long count = modelService.count( session, QuestionnaireDefinition.class, "company.legalName", "Company0" );
			blackhole.consume( count );

			// full text search on indexEmbedded
			questionnaires = modelService.search( session, QuestionnaireDefinition.class, "questions.text", "2022" );
			blackhole.consume( questionnaires );

			// numeric field
			List<ClosedQuestion> questions = modelService.search( session, ClosedQuestion.class, "weight", 7 );
			blackhole.consume( questions );
		}
	}

	@Benchmark
	@Threads(3)
	public void answers(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			// high-match count on full text field
			long count = modelService.count( session, OpenAnswer.class, "text", "search" );
			blackhole.consume( count );

			// high-match range
			List<ClosedAnswer> closedAnswers = modelService.range( session, ClosedAnswer.class, "choice", 5, 7 );
			blackhole.consume( closedAnswers );

			// high-match on nested full text field
			List<QuestionnaireInstance> questionnaires = modelService.search(
					session, QuestionnaireInstance.class, "openAnswers.text", "annotation" );
			blackhole.consume( questionnaires );

			// more predicates
			closedAnswers = modelService.searchAnd(
					session, ClosedAnswer.class, "questionnaire.uniqueCode", "0:0:0", "choice", 7 );
			blackhole.consume( closedAnswers );

			List<PerformanceSummary> performances = modelService.searchAnd(
					session, PerformanceSummary.class, "employee.manager.manager.surname", "surname0", "year", 2025 );
			blackhole.consume( performances );
			List<List<?>> projections = modelService.project(
					session, PerformanceSummary.class, "employee.surname", "surname77", "year", 2025, "maxScore",
					"employeeScore"
			);
			blackhole.consume( projections );
		}
	}

	protected abstract Properties manualProperties(ModelService modelService);
}
