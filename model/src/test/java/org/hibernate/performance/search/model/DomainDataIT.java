package org.hibernate.performance.search.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataInitializer;
import org.hibernate.performance.search.model.application.DomainDataUpdater;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.question.ClosedQuestion;
import org.hibernate.performance.search.model.entity.question.OpenQuestion;
import org.hibernate.performance.search.model.entity.question.Question;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;
import org.hibernate.performance.search.model.param.RelationshipSize;
import org.hibernate.performance.search.model.service.EmployeeRepository;

import org.junit.jupiter.api.Test;

public class DomainDataIT {

	@Test
	public void test() {
		try ( SessionFactory sessionFactory = HibernateORMHelper.buildSessionFactory( new Properties() ) ) {
			new DomainDataInitializer( sessionFactory, RelationshipSize.LARGE ).initAllCompanyData( 0 );

			try ( Session session = sessionFactory.openSession() ) {
				EmployeeRepository repository = new EmployeeRepository( session );

				assertThat( repository.count( Company.class ) ).isEqualTo( 1 );
				assertThat( repository.count( BusinessUnit.class ) ).isEqualTo( 10 );
				assertThat( repository.count( Manager.class ) ).isEqualTo( 10 );
				assertThat( repository.count( Employee.class ) ).isEqualTo( 100 );

				assertThat( repository.count( QuestionnaireDefinition.class ) ).isEqualTo( 10 );
				assertThat( repository.count( Question.class ) ).isEqualTo( 200 );
				assertThat( repository.count( OpenQuestion.class ) ).isEqualTo( 100 );
				assertThat( repository.count( ClosedQuestion.class ) ).isEqualTo( 100 );

				assertThat( repository.count( QuestionnaireInstance.class ) ).isEqualTo( 11880 );
				assertThat( repository.countFilledOpenAnswer() ).isEqualTo( 118800 );
				assertThat( repository.countFilledClosedAnswer() ).isEqualTo( 118800 );
			}

			DomainDataUpdater updater = new DomainDataUpdater( sessionFactory );
			updater.inTransaction( (session, up) -> up.doSomeChangesOnCompanyAndBusinessUnit( session, 0, 0, 1 ) );

			try ( Session session = sessionFactory.openSession() ) {
				EmployeeRepository repository = new EmployeeRepository( session );

				assertThat( repository.count( Company.class ) ).isEqualTo( 2 );
				assertThat( repository.count( BusinessUnit.class ) ).isEqualTo( 10 );
			}

			try ( Session session = sessionFactory.openSession() ) {
				Employee employee = session.load( Employee.class, 70 );
				Manager manager = employee.getManager();

				assertThat( manager.getId() ).isEqualTo( 30 );
			}

			updater.inTransaction( (session, up) -> {
				up.assignNewManager( session, 70, 0 );
				up.changeEmployeeName( session, 0, 0 );
				up.changeEmployeeName( session, 30, 1 );
				up.changeEmployeeName( session, 70, 2 );
			} );

			try ( Session session = sessionFactory.openSession() ) {
				Employee employee = session.load( Employee.class, 70 );
				Manager manager = employee.getManager();

				assertThat( manager.getId() ).isEqualTo( 0 );
			}

			updater.inTransaction( (session, up) -> {
				up.updateQuestionnaire( session, 0, 7 );
				up.updateQuestionsAndAnswers( session, 0, 9 );
			} );
		}
	}
}
