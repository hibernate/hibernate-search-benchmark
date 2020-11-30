package org.hibernate.performance.search.elasticsearch;

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

public class ManualIndexingIT {

	@Test
	public void smoke() throws Exception {
		ModelService modelService = TckBackendHelperFactory.getModelService();
		Properties properties = TckBackendHelperFactory.manualProperties();

		try ( SessionFactory sessionFactory = HibernateORMHelper.buildSessionFactory( properties ) ) {
			for (int i=0; i<10; i++) {
				Integer a = i;
				HibernateORMHelper.inTransaction( sessionFactory, (session) -> {
					for (int j=0; j<10; j++) {
						session.persist( new Employee( a + j*10 ) );
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
