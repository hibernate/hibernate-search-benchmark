package org.hibernate.performance.search.model.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.performance.PerformanceSummary;

public class Scorer {

	private final SessionFactory sessionFactory;

	public Scorer(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void generateScoreForQuestionnaires(Company company) {
		List<Employee> employees;
		try ( Session session = sessionFactory.openSession() ) {
			employees = new EmployeeRepository( session ).getEmployees( company );
		}

		for ( Employee employee : employees ) {
			generateScoreForQuestionnaires( employee );
		}
	}

	private void generateScoreForQuestionnaires(Employee employee) {
		HibernateORMHelper.inTransaction( sessionFactory, session -> {
			session.refresh( employee );
			EmployeeRepository repository = new EmployeeRepository( session );
			List<PerformanceSummary> performanceSummaries = repository.findByEmployee( employee );
			List<QuestionnaireInstance> questionnaireInstances = repository.findBySubject( employee );

			Map<Integer, List<PerformanceSummary>> performances = performanceSummaries.stream().collect(
					Collectors.groupingBy( (ps) -> ps.getYear() ) );
			Map<Integer, List<QuestionnaireInstance>> questionnaires = questionnaireInstances.stream().collect(
					Collectors.groupingBy( (qi) -> qi.getYear() ) );

			for ( Integer year : questionnaires.keySet() ) {
				if ( performances.containsKey( year ) ) {
					// score already calculated
					continue;
				}

				int yearScore = 0;
				int yearMaxScore = 0;

				for ( QuestionnaireInstance questionnaire : questionnaires.get( year ) ) {
					yearScore += questionnaire.getScore();
					yearMaxScore += questionnaire.getMaxScore();
				}

				session.persist( new PerformanceSummary( employee, year, yearMaxScore, yearScore ) );
			}
		} );
	}
}
