package org.hibernate.search.benchmark.model.application;

import java.util.Properties;
import java.util.function.Consumer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.benchmark.model.entity.Company;
import org.hibernate.search.benchmark.model.entity.Employee;
import org.hibernate.search.benchmark.model.entity.Manager;
import org.hibernate.search.benchmark.model.entity.BusinessUnit;
import org.hibernate.search.benchmark.model.entity.answer.Answer;
import org.hibernate.search.benchmark.model.entity.answer.ClosedAnswer;
import org.hibernate.search.benchmark.model.entity.answer.OpenAnswer;
import org.hibernate.search.benchmark.model.entity.performance.PerformanceSummary;
import org.hibernate.search.benchmark.model.entity.question.ClosedQuestion;
import org.hibernate.search.benchmark.model.entity.question.OpenQuestion;
import org.hibernate.search.benchmark.model.entity.question.Question;
import org.hibernate.search.benchmark.model.entity.question.QuestionnaireDefinition;
import org.hibernate.search.benchmark.model.entity.answer.QuestionnaireInstance;

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
