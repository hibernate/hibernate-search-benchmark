package org.hibernate.performance.search;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.application.ModelService;
import org.hibernate.performance.search.application.ModelServiceFactory;

import org.junit.jupiter.api.Test;

import org.assertj.core.api.Assertions;

public class ServiceLoadIT {

	@Test
	public void loadSessionFactory() {
		try ( SessionFactory sessionFactory = ModelServiceFactory.buildSessionFactory() ) {
			Assertions.assertThat( sessionFactory ).isNotNull();
			Assertions.assertThat( sessionFactory.isClosed() ).isFalse();
		}
	}

	@Test
	public void loadModelService() {
		ModelService modelService = ModelServiceFactory.create();
		Assertions.assertThat( modelService ).isNotNull();

		try {
			modelService.start();
			modelService.indexing();
			modelService.search();
		} finally {
			modelService.stop();
		}
	}
}
