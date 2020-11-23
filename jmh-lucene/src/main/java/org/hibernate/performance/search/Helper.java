package org.hibernate.performance.search;

import java.util.function.Consumer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class Helper {

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
}
