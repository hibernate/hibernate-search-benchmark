package org.hibernate.performance.search;

import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.application.ModelService;
import org.hibernate.performance.search.application.ModelServiceFactory;
import org.hibernate.performance.search.entity.Employee;
import org.hibernate.performance.search.application.HibernateORMHelper;

import org.junit.jupiter.api.Test;

import org.assertj.core.api.Assertions;

public class AutomaticIndexingIT {

	@Test
	public void smoke() {
		ModelService modelService = ModelServiceFactory.create();
		Assertions.assertThat( modelService ).isNotNull();

		Properties properties = modelService.properties( false );
		Assertions.assertThat( properties ).isNotNull();

		try ( SessionFactory sessionFactory = HibernateORMHelper.buildSessionFactory( properties ) ) {
			Assertions.assertThat( sessionFactory ).isNotNull();
			Assertions.assertThat( sessionFactory.isClosed() ).isFalse();

			HibernateORMHelper.inTransaction( sessionFactory, (session) ->
				session.persist( new Employee() )
			);

			try ( Session session = sessionFactory.openSession() ) {
				List<Employee> search = modelService.search( session, Employee.class );
				Assertions.assertThat( search ).isNotEmpty();
			}
		}
	}
}
