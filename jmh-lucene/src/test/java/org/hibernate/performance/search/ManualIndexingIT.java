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

public class ManualIndexingIT {

	@Test
	public void smoke() throws Exception {
		ModelService modelService = ModelServiceFactory.create();
		Properties properties = modelService.properties( true );

		try ( SessionFactory sessionFactory = HibernateORMHelper.buildSessionFactory( properties ) ) {
			for (int i=0; i<10; i++) {
				HibernateORMHelper.inTransaction( sessionFactory, (session) -> {
					for (int j=0; j<10; j++) {
						session.persist( new Employee() );
					}
				} );
			}

			try ( Session session = ( sessionFactory.openSession() ) ) {
				modelService.massIndexing( session, Employee.class );
			}

			try ( Session session = sessionFactory.openSession() ) {
				List<Employee> search = modelService.search( session, Employee.class );
				Assertions.assertThat( search ).isNotEmpty();
			}
		}
	}
}
