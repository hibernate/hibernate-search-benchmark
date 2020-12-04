package org.hibernate.performance.search.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataFiller;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;
import org.hibernate.performance.search.model.entity.answer.ClosedAnswer;
import org.hibernate.performance.search.model.entity.answer.OpenAnswer;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.question.ClosedQuestion;
import org.hibernate.performance.search.model.entity.question.OpenQuestion;
import org.hibernate.performance.search.model.entity.question.Question;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;
import org.hibernate.performance.search.model.service.EmployeeRepository;

import org.junit.jupiter.api.Test;

public class DomainDataFillerIT {

	@Test
	public void test() {
		try ( SessionFactory sessionFactory = HibernateORMHelper.buildSessionFactory( new Properties() ) ) {
			new DomainDataFiller( sessionFactory ).fillData( 0 );

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
				assertThat( repository.count( OpenAnswer.class ) ).isEqualTo( 118800 );
				assertThat( repository.count( ClosedAnswer.class ) ).isEqualTo( 118800 );
			}

			try ( Session session = sessionFactory.openSession() ) {
				EmployeeRepository repository = new EmployeeRepository( session );

				assertThat( repository.countFilledOpenAnswer() ).isEqualTo( 118800 );
				assertThat( repository.countFilledClosedAnswer() ).isEqualTo( 118800 );
			}
		}
	}
}
