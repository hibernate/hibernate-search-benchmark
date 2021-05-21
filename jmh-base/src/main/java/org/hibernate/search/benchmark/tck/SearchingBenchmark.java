package org.hibernate.search.benchmark.tck;

import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.benchmark.model.application.DomainDataInitializer;
import org.hibernate.search.benchmark.model.application.HibernateORMHelper;
import org.hibernate.search.benchmark.model.application.ModelService;
import org.hibernate.search.benchmark.model.application.ModelServiceFactory;
import org.hibernate.search.benchmark.model.entity.BusinessUnit;
import org.hibernate.search.benchmark.model.entity.Company;
import org.hibernate.search.benchmark.model.entity.Employee;
import org.hibernate.search.benchmark.model.entity.Manager;
import org.hibernate.search.benchmark.model.entity.answer.ClosedAnswer;
import org.hibernate.search.benchmark.model.entity.answer.OpenAnswer;
import org.hibernate.search.benchmark.model.entity.answer.QuestionnaireInstance;
import org.hibernate.search.benchmark.model.entity.performance.PerformanceSummary;
import org.hibernate.search.benchmark.model.entity.question.ClosedQuestion;
import org.hibernate.search.benchmark.model.entity.question.QuestionnaireDefinition;
import org.hibernate.search.benchmark.model.param.RelationshipSize;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@Warmup(iterations = 1)
@Measurement(iterations = 5)
public abstract class SearchingBenchmark {

	@Param({ "MEDIUM" })
	private RelationshipSize relationshipSize;

	@Param({ "20" })
	private int initialCompanyCount;

	private final ModelService modelService;
	private final SessionFactory sessionFactory;

	public SearchingBenchmark() {
		modelService = ModelServiceFactory.create();
		sessionFactory = HibernateORMHelper.buildSessionFactory( manualProperties( modelService ) );
	}

	@Setup(Level.Trial)
	public void setup() throws Exception {
		DomainDataInitializer domainDataInitializer = new DomainDataInitializer( modelService, sessionFactory, relationshipSize );
		for ( int i = 0; i < initialCompanyCount; i++ ) {
			domainDataInitializer.initAllCompanyData( i );
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
	@Group("company")
	public void company_match(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<Company> companies = modelService.search( session, Company.class, "legalName", "Company0" );
			blackhole.consume( companies );
		}
	}

	@Benchmark
	@Group("company")
	public void company_noMatch(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<Company> companies = modelService.search( session, Company.class, "legalName", "CompanyX" );
			blackhole.consume( companies );
		}
	}

	@Benchmark
	@Group("company")
	public void company_indexEmbedded(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<Company> companies = modelService.search( session, Company.class, "businessUnits.name", "Unit7" );
			blackhole.consume( companies );
		}
	}

	@Benchmark
	@Group("company")
	public void company_searchById(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<Company> companies = modelService.searchById( session, Company.class, "id", 0 );
			blackhole.consume( companies );
		}
	}

	@Benchmark
	@Group("businessUnit")
	public void businessUnit_match(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<BusinessUnit> businessUnits = modelService.search( session, BusinessUnit.class, "name", "Unit7" );
			blackhole.consume( businessUnits );
		}
	}

	@Benchmark
	@Group("businessUnit")
	public void businessUnit_noMatch(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<BusinessUnit> businessUnits = modelService.search( session, BusinessUnit.class, "name", "UnitX" );
			blackhole.consume( businessUnits );
		}
	}

	@Benchmark
	@Group("businessUnit")
	public void businessUnit_indexEmbedded(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<BusinessUnit> businessUnits = modelService.search(
					session, BusinessUnit.class, "owner.legalName", "Company0" );
			blackhole.consume( businessUnits );
		}
	}

	@Benchmark
	@Group("employee_g1")
	public void employee_match(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<Employee> employees = modelService.search( session, Employee.class, "firstName", "name77" );
			blackhole.consume( employees );
		}
	}

	@Benchmark
	@Group("employee_g1")
	public void employee_noMatch(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<Employee> employees = modelService.search( session, Employee.class, "firstName", "nameX" );
			blackhole.consume( employees );
		}
	}

	@Benchmark
	@Group("employee_g1")
	public void employee_count(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			long count = modelService.count( session, Employee.class, "surname", "surname77" );
			blackhole.consume( count );
		}
	}

	@Benchmark
	@Group("employee_g1")
	public void employee_range(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<Employee> employees = modelService.range( session, Employee.class, "socialSecurityNumber",
					"socialSecurityNumber32", "socialSecurityNumber41"
			);
			blackhole.consume( employees );
		}
	}

	@Benchmark
	@Group("employee_g2")
	public void employee_indexEmbedded(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			long count = modelService.count( session, Employee.class, "company.legalName", "Company0" );
			blackhole.consume( count );
		}
	}

	@Benchmark
	@Group("employee_g2")
	public void employee_projection(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<Object> ids = modelService.projectId( session, Employee.class, "businessUnit.name", "Unit7" );
			blackhole.consume( ids );
		}
	}

	@Benchmark
	@Group("employee_g2")
	public void employee_traverseTreeUp(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<Employee> employees = modelService.search(
					session, Employee.class, "manager.manager.manager.manager.firstName", "name0" );
			blackhole.consume( employees );
		}
	}

	@Benchmark
	@Group("employee_g2")
	public void employee_traverseTreeDown(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<Manager> managers = modelService.search( session, Manager.class, "employees.firstName", "name77" );
			blackhole.consume( managers );
		}
	}

	@Benchmark
	@Group("questions_g1")
	public void questions_rangeOrderOnNumeric(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<QuestionnaireDefinition> questionnaires = modelService.rangeOrderBy(
					session, QuestionnaireDefinition.class, "year", 2021, 2025 );
			blackhole.consume( questionnaires );
		}
	}

	@Benchmark
	@Group("questions_g1")
	public void questions_fullTextSearch_title(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<QuestionnaireDefinition> questionnaires = modelService.search(
					session, QuestionnaireDefinition.class, "title", "2023" );
			blackhole.consume( questionnaires );
		}
	}

	@Benchmark
	@Group("questions_g1")
	public void questions_fullTextSearch_description(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<QuestionnaireDefinition> questionnaires = modelService.search(
					session, QuestionnaireDefinition.class, "description", "2025" );
			blackhole.consume( questionnaires );
		}
	}

	@Benchmark
	@Group("questions_g2")
	public void questions_indexEmbedded(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			long count = modelService.count( session, QuestionnaireDefinition.class, "company.legalName", "Company0" );
			blackhole.consume( count );
		}
	}

	@Benchmark
	@Group("questions_g2")
	public void questions_fullTextSearch_indexEmbedded(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<QuestionnaireDefinition> questionnaires = modelService.search(
					session, QuestionnaireDefinition.class, "questions.text", "2022" );
			blackhole.consume( questionnaires );
		}
	}

	@Benchmark
	@Group("questions_g2")
	public void questions_numeric(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<ClosedQuestion> questions = modelService.search( session, ClosedQuestion.class, "weight", 7 );
			blackhole.consume( questions );
		}
	}

	@Benchmark
	@Group("answers_g1")
	public void answers_highMatchCount_fullTextField(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			long count = modelService.count( session, OpenAnswer.class, "text", "search" );
			blackhole.consume( count );
		}
	}

	@Benchmark
	@Group("answers_g1")
	public void answers_highMatch_range(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<ClosedAnswer> closedAnswers = modelService.range( session, ClosedAnswer.class, "choice", 5, 7 );
			blackhole.consume( closedAnswers );
		}
	}

	@Benchmark
	@Group("answers_g1")
	public void answers_highMatch_nested_fullText(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<QuestionnaireInstance> questionnaires = modelService.search(
					session, QuestionnaireInstance.class, "openAnswers.text", "annotation" );
			blackhole.consume( questionnaires );
		}
	}

	@Benchmark
	@Group("answers_g2")
	public void answers_predicates_closedAnswers(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<ClosedAnswer> closedAnswers = modelService.searchAnd(
					session, ClosedAnswer.class, "questionnaire.uniqueCode", "0:0:0", "choice", 7 );
			blackhole.consume( closedAnswers );
		}
	}

	@Benchmark
	@Group("answers_g2")
	public void answers_predicates_performanceSummary(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<PerformanceSummary> performances = modelService.searchAnd(
					session, PerformanceSummary.class, "employee.manager.manager.surname", "surname0", "year", 2025 );
			blackhole.consume( performances );
		}
	}

	@Benchmark
	@Group("answers_g2")
	public void answers_predicates_performanceSummary_projection(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<List<?>> projections = modelService.project(
					session, PerformanceSummary.class, "employee.surname", "surname77", "year", 2025, "maxScore",
					"employeeScore"
			);
			blackhole.consume( projections );
		}
	}

	protected abstract Properties manualProperties(ModelService modelService);
}
