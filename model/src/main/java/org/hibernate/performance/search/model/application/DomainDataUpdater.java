package org.hibernate.performance.search.model.application;

import java.util.List;
import java.util.function.BiConsumer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;
import org.hibernate.performance.search.model.entity.answer.ClosedAnswer;
import org.hibernate.performance.search.model.entity.answer.OpenAnswer;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.question.Question;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;
import org.hibernate.performance.search.model.service.EmployeeRepository;

public class DomainDataUpdater {

	private final SessionFactory sessionFactory;

	public DomainDataUpdater(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void inTransaction(BiConsumer<Session, DomainDataUpdater> actions) {
		HibernateORMHelper.inTransaction( sessionFactory, (session) -> actions.accept( session, this ) );
	}

	public void doSomeChangesOnCompanyAndBusinessUnit(Session session, int invocation, int oldCompanyId,
			int newCompanyId) {
		Company newCompany = session.get( Company.class, newCompanyId );
		if ( newCompany == null ) {
			newCompany = new Company( newCompanyId, "Company" + newCompanyId );
			session.persist( newCompany );
		}

		Company oldCompany = session.load( Company.class, oldCompanyId );
		List<BusinessUnit> businessUnits = oldCompany.getBusinessUnits();
		if ( businessUnits.isEmpty() ) {
			throw new IllegalStateException(
					"Company " + oldCompany + " is supposed to have at least one business unit to transfer to " + newCompany );
		}

		BusinessUnit businessUnit = businessUnits.get( 0 );
		businessUnit.setOwner( newCompany );

		// change the description of the existing company,
		// these will trigger the reindex of all business units
		oldCompany.setDescription(
				"Let's change the description of " + oldCompany.getLegalName() + "! (" + invocation + ")" );
		newCompany.setDescription(
				"Let's change the description of " + newCompany.getLegalName() + "! (" + invocation + ")" );

		// move the business unit to the new company,
		// this will trigger the reindex of the 2 companies
		businessUnits.remove( businessUnit );
		newCompany.getBusinessUnits().add( businessUnit );

		session.merge( oldCompany );
		session.merge( newCompany );
	}

	public void assignNewManager(Session session, int employeeId, int managerId) {
		Employee employee = session.load( Employee.class, employeeId );

		Manager newManager = session.load( Manager.class, managerId );
		Manager oldManager = employee.getManager();

		employee.setManager( newManager );
		if ( oldManager != null ) {
			// remove the employee from old manager
			oldManager.getEmployees().remove( employee );
		}
		// add the employee to the new manager
		newManager.getEmployees().add( employee );

		session.merge( employee );
		if ( oldManager != null ) {
			session.merge( oldManager );
		}
		session.merge( newManager );
	}

	public void changeEmployeeName(Session session, int employeeId, int invocation) {
		Employee employee = session.load( Employee.class, employeeId );

		// change his/her names
		employee.setFirstName( "nameE" + invocation );
		employee.setSurname( "surnameE" + invocation );

		session.merge( employee );
	}

	public void removeManagerFromEmployee(Session session, int employeeId) {
		Employee employee = session.load( Employee.class, employeeId );

		Manager oldManager = employee.getManager();

		employee.setManager( null );
		// remove the employee from old manager
		oldManager.getEmployees().remove( employee );

		session.merge( employee );
		session.merge( oldManager );
	}

	public void updateQuestionnaire(Session session, int invocation, int questionnaireDefinitionId) {
		QuestionnaireDefinition definition = session.load( QuestionnaireDefinition.class, questionnaireDefinitionId );
		definition.setDescription(
				"This is the description for questionnaire definition #" + questionnaireDefinitionId + " - invocation #" + invocation + "." );

		session.merge( definition );

		List<QuestionnaireInstance> instances = new EmployeeRepository( session ).findByDefinition( definition );
		for ( QuestionnaireInstance instance : instances ) {
			instance.setNotes( "This is a note for questionnaire instance #" + instance
					.getId() + " - invocation #" + invocation + "." );

			session.merge( instance );
		}
	}

	public void updateQuestionsAndAnswers(Session session, int invocation, int questionnaireDefinitionId) {
		QuestionnaireDefinition definition = session.load( QuestionnaireDefinition.class, questionnaireDefinitionId );
		List<Question> questions = definition.getQuestions();
		for ( Question question : questions ) {
			question.setText(
					"This is the text for question #" + question.getId() + " - invocation #" + invocation + "." );

			session.merge( question );
		}

		List<QuestionnaireInstance> instances = new EmployeeRepository( session ).findByDefinition( definition );
		for ( QuestionnaireInstance instance : instances ) {
			for ( ClosedAnswer answer : instance.getClosedAnswers() ) {
				answer.setChoice( invocation % 8 );
				session.merge( answer );
			}
			for ( OpenAnswer answer : instance.getOpenAnswers() ) {
				answer.setText( "This is a response to the open answer " + answer
						.getId() + " - invocation #" + invocation + "." );
				session.merge( answer );
			}
		}
	}
}
