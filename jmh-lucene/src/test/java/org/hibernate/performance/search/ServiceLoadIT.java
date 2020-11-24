package org.hibernate.performance.search;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.application.ModelService;
import org.hibernate.performance.search.application.ModelServiceFactory;
import org.hibernate.performance.search.entity.Employee;

import org.junit.jupiter.api.Test;

import org.assertj.core.api.Assertions;

public class ServiceLoadIT {

	@Test
	public void loadModelServiceAndSessionFactory() {
		ModelService modelService = ModelServiceFactory.create();
		Assertions.assertThat( modelService ).isNotNull();

		Properties properties = modelService.properties();
		Assertions.assertThat( properties ).isNotNull();

		try ( SessionFactory sessionFactory = ModelServiceFactory.buildSessionFactory( properties ) ) {
			Assertions.assertThat( sessionFactory ).isNotNull();
			Assertions.assertThat( sessionFactory.isClosed() ).isFalse();

			Helper.inTransaction( sessionFactory, (session) ->
				session.persist( new Employee() )
			);
			modelService.waitForIndexFlush( sessionFactory, Employee.class );
		}

		try {
			modelService.search();
		} finally {
			modelService.stop();
		}
	}
}
