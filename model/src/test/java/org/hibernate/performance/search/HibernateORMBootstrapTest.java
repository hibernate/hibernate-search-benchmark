package org.hibernate.performance.search;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.application.HibernateORMHelper;

import org.junit.jupiter.api.Test;

import org.assertj.core.api.Assertions;

public class HibernateORMBootstrapTest {

	@Test
	public void test() {
		try ( SessionFactory sessionFactory = HibernateORMHelper.buildSessionFactory( new Properties() ) ) {
			Assertions.assertThat( sessionFactory ).isNotNull();
		}
	}
}
