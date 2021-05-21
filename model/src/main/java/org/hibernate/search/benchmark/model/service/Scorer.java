package org.hibernate.search.benchmark.model.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.benchmark.model.application.HibernateORMHelper;
import org.hibernate.search.benchmark.model.application.ModelService;
import org.hibernate.search.benchmark.model.entity.Company;
import org.hibernate.search.benchmark.model.entity.Employee;
import org.hibernate.search.benchmark.model.entity.answer.QuestionnaireInstance;
import org.hibernate.search.benchmark.model.entity.performance.PerformanceSummary;

public class Scorer {
	private static final int EMPLOYEE_BATCH_SIZE = 100;

	private final ModelService modelService;
	private final SessionFactory sessionFactory;

	public Scorer(ModelService modelService, SessionFactory sessionFactory) {
		this.modelService = modelService;
		this.sessionFactory = sessionFactory;
	}

	public void generateScoreForQuestionnaires(Integer companyId) {
		HibernateORMHelper.inTransaction( sessionFactory, session -> {
			Company company = session.load( Company.class, companyId );
			EmployeeRepository repository = new EmployeeRepository( session );

			try ( Scroll<Employee> employees = repository.getEmployees( company, EMPLOYEE_BATCH_SIZE ) ) {
				while ( employees.hasNext() ) {
					for ( Employee employee : employees.next() ) {
						generateScoreForQuestionnaires( session, employee );
					}
					modelService.flushOrmAndIndexesAndClear( session );
				}
			}
		} );
	}

	public static void generateScoreForQuestionnaires(Session session, Employee employee) {
		List<PerformanceSummary> performanceSummaries = employee.getPerformanceSummaries();
		List<QuestionnaireInstance> questionnaireInstances = employee.getQuestionnaires();

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
	}

}
