package org.hibernate.performance.search.application;

import java.util.Properties;
import java.util.function.Consumer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.performance.search.entity.Employee;

public final class HibernateORMHelper {

	private HibernateORMHelper() {
	}

	public static void inTransaction(SessionFactory sessionFactory, Consumer<Session> action) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			Transaction trx = session.beginTransaction();
			try {
				action.accept( session );
			}
			catch (Exception e) {
				if ( trx.isActive() ) {
					trx.rollback();
				}
				throw e;
			}
			trx.commit();
		}
	}

	public static SessionFactory buildSessionFactory(Properties additionalProperties) {
		return new Configuration()
				.addProperties( additionalProperties )
				// list of Entities:
				.addAnnotatedClass( Employee.class )
				.buildSessionFactory();
	}
}
