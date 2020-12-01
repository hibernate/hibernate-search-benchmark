package org.hibernate.performance.search;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;
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
		}
	}
}
