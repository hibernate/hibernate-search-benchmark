package org.hibernate.performance.search.lucene;

import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.tck.TckBackendHelperFactory;

import org.junit.jupiter.api.Test;

import org.assertj.core.api.Assertions;

public class AutomaticIndexingIT {

	@Test
	public void smoke() {
		ModelService modelService = TckBackendHelperFactory.getModelService();
		Assertions.assertThat( modelService ).isNotNull();

		Properties properties = TckBackendHelperFactory.autoProperties();
		Assertions.assertThat( properties ).isNotNull();

		try ( SessionFactory sessionFactory = HibernateORMHelper.buildSessionFactory( properties ) ) {
			Assertions.assertThat( sessionFactory ).isNotNull();
			Assertions.assertThat( sessionFactory.isClosed() ).isFalse();

			HibernateORMHelper.inTransaction( sessionFactory, (session) ->
				session.persist( new Employee( 1 ) )
			);

			try ( Session session = sessionFactory.openSession() ) {
				List<Employee> search = modelService.search( session, Employee.class );
				Assertions.assertThat( search ).hasSize( 1 );

				search = modelService.search( session, Employee.class, "id", 1 );
				Assertions.assertThat( search ).hasSize( 1 );
			}
		}
	}
}
