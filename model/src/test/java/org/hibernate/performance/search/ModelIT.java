package org.hibernate.performance.search;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Manager;
import org.hibernate.performance.search.model.service.CompanyFactory;
import org.hibernate.performance.search.model.service.EmployeeFactory;

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
		}
	}
}
