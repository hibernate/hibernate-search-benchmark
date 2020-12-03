package org.hibernate.performance.search;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;
import org.hibernate.performance.search.model.entity.answer.Answer;
import org.hibernate.performance.search.model.entity.answer.ClosedAnswer;
import org.hibernate.performance.search.model.entity.answer.OpenAnswer;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.question.ClosedQuestion;
import org.hibernate.performance.search.model.entity.question.OpenQuestion;
import org.hibernate.performance.search.model.entity.question.Question;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;
import org.hibernate.performance.search.model.service.AnswerFiller;
import org.hibernate.performance.search.model.service.CompanyFactory;
import org.hibernate.performance.search.model.service.EmployeeFactory;
import org.hibernate.performance.search.model.service.EmployeeRepository;
import org.hibernate.performance.search.model.service.QuestionnaireDefinitionFactory;
import org.hibernate.performance.search.model.service.QuestionnaireInstanceFactory;

import org.junit.jupiter.api.Test;

public class ModelIT {

	@Test
	public void test() {
		try ( SessionFactory sessionFactory = HibernateORMHelper.buildSessionFactory( new Properties() ) ) {
			// atomicity is not used here
			AtomicReference<Company> companyReference = new AtomicReference<>();

			HibernateORMHelper.inTransaction( sessionFactory, session -> {
				Company company = CompanyFactory.createCompanyAndUnits( 0 );
				session.persist( company );
				companyReference.set( company );
			} );

			HibernateORMHelper.inTransaction( sessionFactory, session -> {
				Manager ceo = EmployeeFactory.createEmployeeTree( companyReference.get() );
				session.persist( ceo );
			} );

			List<QuestionnaireDefinition> questionnaireDefinitions = QuestionnaireDefinitionFactory
					.createQuestionnaireDefinitions( companyReference.get() );
			for ( QuestionnaireDefinition questionnaire : questionnaireDefinitions ) {
				HibernateORMHelper.inTransaction( sessionFactory, session -> session.persist( questionnaire ) );
			}

			HibernateORMHelper.inTransaction( sessionFactory, session -> {
				EmployeeRepository repository = new EmployeeRepository( session );
				List<Employee> employees = repository.getEmployees( companyReference.get() );
				List<QuestionnaireDefinition> definitions = repository.getQuestionnaireDefinitions(
						companyReference.get() );

				for ( Employee employee : employees ) {
					for ( QuestionnaireDefinition definition : definitions ) {
						List<QuestionnaireInstance> questionnaireInstances = QuestionnaireInstanceFactory
								.createQuestionnaireInstances( employee, definition );

						for (QuestionnaireInstance instance : questionnaireInstances) {
							session.persist( instance );
						}
					}
				}
			} );

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
				assertThat( repository.count( Answer.class ) ).isEqualTo( 237600 );
				assertThat( repository.count( OpenAnswer.class ) ).isEqualTo( 118800 );
				assertThat( repository.count( ClosedAnswer.class ) ).isEqualTo( 118800 );
			}

			new AnswerFiller( sessionFactory ).fillAllAnswers();

			try ( Session session = sessionFactory.openSession() ) {
				EmployeeRepository repository = new EmployeeRepository( session );

				assertThat( repository.countFilledOpenAnswer() ).isEqualTo( 118800 );
				assertThat( repository.countFilledClosedAnswer() ).isEqualTo( 118800 );
			}
		}
	}
}
