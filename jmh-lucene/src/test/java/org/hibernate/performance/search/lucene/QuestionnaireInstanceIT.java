package org.hibernate.performance.search.lucene;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;
import org.hibernate.performance.search.tck.TckBackendHelperFactory;

import org.junit.jupiter.api.Test;

public class QuestionnaireInstanceIT {

	@Test
	public void automatic() {
		try ( SessionFactory sessionFactory = HibernateORMHelper.buildSessionFactory(
				TckBackendHelperFactory.autoProperties() ) ) {

			createEntities( sessionFactory );
		}
	}

	@Test
	public void massIndexing() throws Exception {
		try ( SessionFactory sessionFactory = HibernateORMHelper.buildSessionFactory(
				TckBackendHelperFactory.manualProperties() ) ) {

			createEntities( sessionFactory );

			try ( Session session = sessionFactory.openSession() ) {
				TckBackendHelperFactory.getModelService().massIndexing( session );
			}
		}
	}

	private void createEntities(SessionFactory sessionFactory) {
		Company company = new Company( 1, "Red Hay" );
		QuestionnaireDefinition definition = new QuestionnaireDefinition( 1, "Q1", "my questionnaire 2021", 2021,
				company
		);
		Employee employee1 = new Employee( 1 );
		Employee employee2 = new Employee( 2 );
		QuestionnaireInstance questionnaireInstance = new QuestionnaireInstance(
				definition, employee1, employee2, QuestionnaireInstance.EvaluationType.COLLEAGUE );

		HibernateORMHelper.inTransaction( sessionFactory, session -> {
			session.persist( company );
			session.persist( definition );
			session.persist( employee1 );
			session.persist( employee2 );
			session.persist( questionnaireInstance );
		} );
	}

}
