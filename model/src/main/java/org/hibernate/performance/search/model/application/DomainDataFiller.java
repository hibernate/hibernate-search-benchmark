package org.hibernate.performance.search.model.application;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;
import org.hibernate.performance.search.model.param.RelationshipSize;
import org.hibernate.performance.search.model.service.AnswerFiller;
import org.hibernate.performance.search.model.service.CompanyFactory;
import org.hibernate.performance.search.model.service.EmployeeFactory;
import org.hibernate.performance.search.model.service.EmployeeRepository;
import org.hibernate.performance.search.model.service.QuestionnaireDefinitionFactory;
import org.hibernate.performance.search.model.service.QuestionnaireInstanceFactory;
import org.hibernate.performance.search.model.service.Scorer;

public class DomainDataFiller {

	private final SessionFactory sessionFactory;
	private final CompanyFactory companyFactory;
	private final EmployeeFactory employeeFactory;
	private final QuestionnaireDefinitionFactory questionnaireDefinitionFactory;

	public DomainDataFiller(SessionFactory sessionFactory, RelationshipSize relationshipSize) {
		this.sessionFactory = sessionFactory;
		this.companyFactory = new CompanyFactory( relationshipSize );
		this.employeeFactory = new EmployeeFactory( relationshipSize );
		this.questionnaireDefinitionFactory = new QuestionnaireDefinitionFactory( relationshipSize );
	}

	public void fillData(int companyId) {
		// atomicity is not used here
		AtomicReference<Company> companyReference = new AtomicReference<>();

		// Phase 1: create the company and its business units
		HibernateORMHelper.inTransaction( sessionFactory, session -> {
			Company company = companyFactory.createCompanyAndUnits( companyId );
			session.persist( company );
			companyReference.set( company );
		} );

		// Phase 2: create the employees' organization chart
		HibernateORMHelper.inTransaction( sessionFactory, session -> {
			Manager ceo = employeeFactory.createEmployeeTree( companyReference.get() );
			session.persist( ceo );
		} );

		// Phase 3: define the questionnaires
		List<QuestionnaireDefinition> questionnaireDefinitions = questionnaireDefinitionFactory
				.createQuestionnaireDefinitions( companyReference.get() );
		for ( QuestionnaireDefinition questionnaire : questionnaireDefinitions ) {
			HibernateORMHelper.inTransaction( sessionFactory, session -> session.persist( questionnaire ) );
		}

		// Phase 4: instantiate questionnaires for the employees
		HibernateORMHelper.inTransaction( sessionFactory, session -> {
			EmployeeRepository repository = new EmployeeRepository( session );
			List<Employee> employees = repository.getEmployees( companyReference.get() );
			List<QuestionnaireDefinition> definitions = repository.getQuestionnaireDefinitions(
					companyReference.get() );

			for ( Employee employee : employees ) {
				for ( QuestionnaireDefinition definition : definitions ) {
					List<QuestionnaireInstance> questionnaireInstances = QuestionnaireInstanceFactory
							.createQuestionnaireInstances( employee, definition );

					for ( QuestionnaireInstance instance : questionnaireInstances ) {
						session.persist( instance );
					}
				}
			}
		} );

		// Phase 5: simulate the employees filling the questionnaires
		new AnswerFiller( sessionFactory ).fillAllAnswers();

		// Phase 6: evaluate the performances based on the outputs of the questionnaires
		new Scorer( sessionFactory ).generateScoreForQuestionnaires( companyReference.get() );
	}

}
