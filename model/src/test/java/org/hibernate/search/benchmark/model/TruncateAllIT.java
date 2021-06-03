package org.hibernate.search.benchmark.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.benchmark.model.application.DomainDataInitializer;
import org.hibernate.search.benchmark.model.application.DomainDataRemover;
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
import org.hibernate.search.benchmark.model.entity.question.QuestionnaireDefinition;
import org.hibernate.search.benchmark.model.param.RelationshipSize;
import org.hibernate.search.benchmark.model.service.EmployeeRepository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TruncateAllIT {

	public static final RelationshipSize RELATIONSHIP_SIZE = RelationshipSize.SMALL;
	private SessionFactory sessionFactory;

	@Test
	public void test() {
		ModelService modelService = new NoIndexingModelService();
		DomainDataInitializer domainDataInitializer = new DomainDataInitializer( modelService, sessionFactory,
				RELATIONSHIP_SIZE
		);
		DomainDataRemover domainDataRemover = new DomainDataRemover( sessionFactory );

		domainDataInitializer.initAllCompanyData( 0 );
		checkSize( 1 );

		domainDataRemover.truncateAll();
		checkSize( 0 );
	}

	@BeforeAll
	public void beforeEach() {
		sessionFactory = HibernateORMHelper.buildSessionFactory( new Properties() );
	}

	@AfterAll
	public void afterEach() {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	private void checkSize(int companies) {
		try ( Session session = sessionFactory.openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );
			assertThat( repository.count( Company.class ) ).isEqualTo( companies );
			assertThat( repository.count( QuestionnaireDefinition.class ) ).isEqualTo( companies );
			assertThat( repository.count( ClosedQuestion.class ) ).isEqualTo( companies );
			assertThat( repository.count( OpenQuestion.class ) ).isEqualTo( companies );

			int units = companies * RELATIONSHIP_SIZE.getUnitsPerCompany();
			assertThat( repository.count( BusinessUnit.class ) ).isEqualTo( units );
			assertThat( repository.count( Manager.class ) ).isEqualTo( units );

			int employees = units * RELATIONSHIP_SIZE.getEmployeesPerBusinessUnit();
			assertThat( repository.count( Employee.class ) ).isEqualTo( employees );
			assertThat( repository.count( PerformanceSummary.class ) ).isEqualTo( employees );

			// in case of RelationshipSize.SMALL
			int questionnaireInstancesForCompany = 12;
			int questionnaireInstances = questionnaireInstancesForCompany * companies;

			assertThat( repository.count( QuestionnaireInstance.class ) ).isEqualTo( questionnaireInstances );
			assertThat( repository.countFilledClosedAnswer() ).isEqualTo( questionnaireInstances );
			assertThat( repository.countFilledOpenAnswer() ).isEqualTo( questionnaireInstances );
		}
	}
}
