package org.hibernate.search.benchmark.model.application;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.benchmark.model.entity.Company;
import org.hibernate.search.benchmark.model.entity.Employee;
import org.hibernate.search.benchmark.model.entity.Manager;
import org.hibernate.search.benchmark.model.entity.answer.ClosedAnswer;
import org.hibernate.search.benchmark.model.entity.answer.OpenAnswer;
import org.hibernate.search.benchmark.model.entity.answer.QuestionnaireInstance;
import org.hibernate.search.benchmark.model.entity.performance.PerformanceSummary;
import org.hibernate.search.benchmark.model.entity.question.QuestionnaireDefinition;
import org.hibernate.search.benchmark.model.param.RelationshipSize;
import org.hibernate.search.benchmark.model.service.AnswerFiller;
import org.hibernate.search.benchmark.model.service.EmployeeRepository;
import org.hibernate.search.benchmark.model.service.QuestionnaireDefinitionFactory;
import org.hibernate.search.benchmark.model.service.QuestionnaireInstanceFactory;
import org.hibernate.search.benchmark.model.service.Scorer;

public class DomainDataDeleteInserter {

	private final SessionFactory sessionFactory;
	private final Random random = new Random( 777 );
	private final QuestionnaireDefinitionFactory questionnaireDefinitionFactory;

	public DomainDataDeleteInserter(SessionFactory sessionFactory, RelationshipSize relationshipSize) {
		this.sessionFactory = sessionFactory;
		this.questionnaireDefinitionFactory = new QuestionnaireDefinitionFactory( relationshipSize );
	}

	public void inTransaction(BiConsumer<Session, DomainDataDeleteInserter> actions) {
		HibernateORMHelper.inTransaction( sessionFactory, (session) -> actions.accept( session, this ) );
	}

	public void createEmployee(Session session, int managerId, int employeeId) {
		Manager manager = session.load( Manager.class, managerId );
		Employee employee = new Employee( manager, employeeId );

		session.persist( employee );
	}

	public void deleteEmployee(Session session, int employeeId) {
		Employee employee = session.load( Employee.class, employeeId );
		session.remove( employee );
	}

	public void createQuestionnaireDefinition(Session session, int companyId, int questionnaireDefinitionId) {
		Company company = session.load( Company.class, companyId );
		QuestionnaireDefinition questionnaire = questionnaireDefinitionFactory.createQuestionnaireDefinition(
				company, questionnaireDefinitionId, 2100 );

		session.persist( questionnaire );
	}

	public void deleteQuestionnaireDefinition(Session session, int questionnaireDefinitionId) {
		QuestionnaireDefinition questionnaire = session.load(
				QuestionnaireDefinition.class, questionnaireDefinitionId );

		session.remove( questionnaire );
	}

	public int deleteQuestionnaireInstancesFor(Session session, int approvalId) {
		List<QuestionnaireInstance> questionnaireInstances = new EmployeeRepository( session ).findByApproval(
				approvalId );

		for ( QuestionnaireInstance questionnaireInstance : questionnaireInstances ) {
			session.remove( questionnaireInstance );
		}

		return questionnaireInstances.size();
	}

	public void createAndFillQuestionnaireInstancesFor(Session session, int approvalId) {
		Employee approval = session.load( Employee.class, approvalId );
		Company company = approval.getCompany();

		List<QuestionnaireDefinition> definitions = new EmployeeRepository( session ).getQuestionnaireDefinitions(
				company );

		for ( QuestionnaireDefinition definition : definitions ) {
			List<QuestionnaireInstance> questionnaireInstances = QuestionnaireInstanceFactory
					.createQuestionnaireInstances( approval, definition );

			for ( QuestionnaireInstance questionnaireInstance : questionnaireInstances ) {
				fillAnswers( questionnaireInstance, random );

				session.persist( questionnaireInstance );
			}
		}
	}

	public int deletePerformanceSummaryFor(Session session, int employeeId) {
		Employee employee = session.load( Employee.class, employeeId );
		List<PerformanceSummary> performanceSummaries = employee.getPerformanceSummaries();

		for ( PerformanceSummary performanceSummary : performanceSummaries ) {
			session.remove( performanceSummary );
		}

		return performanceSummaries.size();
	}

	public void createPerformanceSummaryFor(Session session, int employeeId) {
		Employee employee = session.load( Employee.class, employeeId );
		Scorer.generateScoreForQuestionnaires( session, employee );
	}

	private void fillAnswers(QuestionnaireInstance questionnaireInstance, Random random) {
		for ( ClosedAnswer answer : questionnaireInstance.getClosedAnswers() ) {
			answer.setChoice( random.nextInt( 8 ) );
		}

		for ( OpenAnswer answer : questionnaireInstance.getOpenAnswers() ) {
			answer.setText( AnswerFiller.OPEN_ANSWER_RESPONSE_TYPES[random.nextInt( 8 )] );
		}
	}
}
