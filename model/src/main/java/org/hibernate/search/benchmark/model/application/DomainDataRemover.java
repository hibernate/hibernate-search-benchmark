package org.hibernate.search.benchmark.model.application;

import java.sql.Statement;

import org.hibernate.SessionFactory;

public class DomainDataRemover {

	private final SessionFactory sessionFactory;

	public DomainDataRemover(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void truncateAll() {
		HibernateORMHelper.inTransaction( sessionFactory, session -> {
			session.doWork( connection -> {
				try ( Statement statement = connection.createStatement() ) {
					statement.executeUpdate(
							" DO $$ " +
									" BEGIN " +
									"       TRUNCATE PerformanceSummary CASCADE; " +
									" 		TRUNCATE ClosedAnswer CASCADE; " +
									" 		TRUNCATE OpenAnswer CASCADE; " +
									" 		TRUNCATE QuestionnaireInstance CASCADE; " +
									" 		TRUNCATE Question CASCADE; " +
									" 		TRUNCATE QuestionnaireDefinition CASCADE; " +
									" 		TRUNCATE Employee CASCADE; " +
									" 		TRUNCATE BusinessUnit CASCADE; " +
									" 		TRUNCATE Company CASCADE; " +
									" END " +
									" $$; "
					);
				}
			} );
		} );
	}
}
