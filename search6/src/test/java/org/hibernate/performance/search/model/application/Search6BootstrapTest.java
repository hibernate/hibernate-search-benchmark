package org.hibernate.performance.search.model.application;

import org.hibernate.SessionFactory;

import org.junit.jupiter.api.Test;

import org.assertj.core.api.Assertions;

public class Search6BootstrapTest {

	@Test
	public void test() {
		ModelServiceImpl modelService = new ModelServiceImpl();

		try ( SessionFactory sessionFactory = HibernateORMHelper.buildSessionFactory(
				modelService.properties( ModelService.Kind.LUCENE_AUTOMATIC_INDEXING ) ) ) {
			Assertions.assertThat( sessionFactory ).isNotNull();
		}
	}
}
