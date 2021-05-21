package org.hibernate.search.benchmark.model.application;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.search.benchmark.model.entity.Company;
import org.hibernate.search.benchmark.model.entity.Employee;
import org.hibernate.search.benchmark.model.entity.Manager;
import org.hibernate.search.benchmark.model.entity.answer.QuestionnaireInstance;
import org.hibernate.search.benchmark.model.entity.question.QuestionnaireDefinition;
import org.hibernate.search.benchmark.model.param.RelationshipSize;
import org.hibernate.search.benchmark.model.service.AnswerFiller;
import org.hibernate.search.benchmark.model.service.CompanyFactory;
import org.hibernate.search.benchmark.model.service.EmployeeFactory;
import org.hibernate.search.benchmark.model.service.EmployeeRepository;
import org.hibernate.search.benchmark.model.service.QuestionnaireDefinitionFactory;
import org.hibernate.search.benchmark.model.service.QuestionnaireInstanceFactory;
import org.hibernate.search.benchmark.model.service.Scorer;
import org.hibernate.search.benchmark.model.service.Scroll;

public class DomainDataInitializer {
	private static final int EMPLOYEE_BATCH_SIZE = 100;

	private final ModelService modelService;
	private final SessionFactory sessionFactory;
	private final CompanyFactory companyFactory;
	private final EmployeeFactory employeeFactory;
	private final QuestionnaireDefinitionFactory questionnaireDefinitionFactory;

	public DomainDataInitializer(ModelService modelService, SessionFactory sessionFactory,
			RelationshipSize relationshipSize) {
		this.modelService = modelService;
		this.sessionFactory = sessionFactory;
		this.companyFactory = new CompanyFactory( relationshipSize );
		this.employeeFactory = new EmployeeFactory( relationshipSize );
		this.questionnaireDefinitionFactory = new QuestionnaireDefinitionFactory( relationshipSize );
	}

	public void initAllCompanyData(int companyId) {
		HibernateORMHelper.inTransaction( sessionFactory, session -> {
			// Phase 1: create the company and its business units
			Company company = companyFactory.createCompanyAndUnits( companyId );
			session.persist( company );

			// Phase 2: create the employees' organization chart
			Manager ceo = employeeFactory.createEmployeeTree( company );
			session.persist( ceo );

			// Phase 3: define the questionnaires
			List<QuestionnaireDefinition> questionnaireDefinitions = questionnaireDefinitionFactory
					.createQuestionnaireDefinitions( company );
			for ( QuestionnaireDefinition questionnaire : questionnaireDefinitions ) {
				session.persist( questionnaire );
			}

			// Phase 4: instantiate questionnaires for the employees
			EmployeeRepository repository = new EmployeeRepository( session );

			List<QuestionnaireDefinition> definitions = repository.getQuestionnaireDefinitions( company );
			try ( Scroll<Employee> employees = repository.getEmployees( company, EMPLOYEE_BATCH_SIZE ) ) {
				while ( employees.hasNext() ) {
					for ( Employee employee : employees.next() ) {
						for ( QuestionnaireDefinition definition : definitions ) {
							List<QuestionnaireInstance> questionnaireInstances = QuestionnaireInstanceFactory
									.createQuestionnaireInstances( employee, definition );

							for ( QuestionnaireInstance instance : questionnaireInstances ) {
								session.persist( instance );
							}
						}
					}
					modelService.flushOrmAndIndexesAndClear( session );
					company = session.load( Company.class, companyId );
					definitions = repository.getQuestionnaireDefinitions( company );
				}
			}
		} );

		// Phase 5: simulate the employees filling the questionnaires
		new AnswerFiller( modelService, sessionFactory ).fillAllAnswers( companyId );

		// Phase 6: evaluate the performances based on the outputs of the questionnaires
		new Scorer( modelService, sessionFactory ).generateScoreForQuestionnaires( companyId );
	}

}
