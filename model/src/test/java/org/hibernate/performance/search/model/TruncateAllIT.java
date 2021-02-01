package org.hibernate.performance.search.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataInitializer;
import org.hibernate.performance.search.model.application.DomainDataRemover;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.performance.PerformanceSummary;
import org.hibernate.performance.search.model.entity.question.ClosedQuestion;
import org.hibernate.performance.search.model.entity.question.OpenQuestion;
import org.hibernate.performance.search.model.entity.question.Question;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;
import org.hibernate.performance.search.model.param.RelationshipSize;
import org.hibernate.performance.search.model.service.EmployeeRepository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TruncateAllIT {

	private SessionFactory sessionFactory;

	@Test
	public void test() {
		ModelService modelService = new NoIndexingModelService();
		DomainDataInitializer domainDataInitializer = new DomainDataInitializer( modelService, sessionFactory, RelationshipSize.SMALL );
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
		if ( sessionFactory == null ) {
			sessionFactory.close();
		}
	}

	private void checkSize(int size) {
		try ( Session session = sessionFactory.openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );

			assertThat( repository.count( Company.class ) ).isEqualTo( size );
			assertThat( repository.count( BusinessUnit.class ) ).isEqualTo( size );
			assertThat( repository.count( Manager.class ) ).isEqualTo( size );
			assertThat( repository.count( Employee.class ) ).isEqualTo( size );
			assertThat( repository.count( QuestionnaireDefinition.class ) ).isEqualTo( size );
			assertThat( repository.count( Question.class ) ).isEqualTo( 2 * size );
			assertThat( repository.count( ClosedQuestion.class ) ).isEqualTo( size );
			assertThat( repository.count( OpenQuestion.class ) ).isEqualTo( size );
			assertThat( repository.count( QuestionnaireInstance.class ) ).isEqualTo( size );
			assertThat( repository.countFilledClosedAnswer() ).isEqualTo( size );
			assertThat( repository.countFilledOpenAnswer() ).isEqualTo( size );
			assertThat( repository.count( PerformanceSummary.class ) ).isEqualTo( size );
		}
	}
}
