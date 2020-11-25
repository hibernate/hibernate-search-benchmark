package org.hibernate.performance.search.application;

import java.util.Properties;
import java.util.function.Consumer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.performance.search.entity.Company;
import org.hibernate.performance.search.entity.Employee;
import org.hibernate.performance.search.entity.Manager;
import org.hibernate.performance.search.entity.BusinessUnit;
import org.hibernate.performance.search.entity.answer.Answer;
import org.hibernate.performance.search.entity.answer.ClosedAnswer;
import org.hibernate.performance.search.entity.answer.OpenAnswer;
import org.hibernate.performance.search.entity.performance.PerformanceSummary;
import org.hibernate.performance.search.entity.question.ClosedQuestion;
import org.hibernate.performance.search.entity.question.OpenQuestion;
import org.hibernate.performance.search.entity.question.Question;
import org.hibernate.performance.search.entity.question.QuestionnaireDefinition;
import org.hibernate.performance.search.entity.answer.QuestionnaireInstance;

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
				.addAnnotatedClass( Manager.class )
				.addAnnotatedClass( Company.class )
				.addAnnotatedClass( BusinessUnit.class )
				.addAnnotatedClass( QuestionnaireDefinition.class )
				.addAnnotatedClass( Question.class )
				.addAnnotatedClass( OpenQuestion.class )
				.addAnnotatedClass( ClosedQuestion.class )
				.addAnnotatedClass( QuestionnaireInstance.class )
				.addAnnotatedClass( Answer.class )
				.addAnnotatedClass( OpenAnswer.class )
				.addAnnotatedClass( ClosedAnswer.class )
				.addAnnotatedClass( PerformanceSummary.class )
				.buildSessionFactory();
	}
}
