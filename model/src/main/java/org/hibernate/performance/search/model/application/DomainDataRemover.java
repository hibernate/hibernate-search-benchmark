package org.hibernate.performance.search.model.application;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.performance.PerformanceSummary;
import org.hibernate.performance.search.model.service.EmployeeRepository;

public class DomainDataRemover {

	private final SessionFactory sessionFactory;

	public DomainDataRemover(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void deleteData(int businessUnitId) {
		try ( Session session = sessionFactory.openSession() ) {
			List<Employee> employees = new EmployeeRepository( session ).getEmployees( businessUnitId );

			Manager manager = null;
			for ( Employee employee : employees ) {
				if ( employee instanceof Manager ) {
					// process the manager for last
					manager = (Manager) employee;
				} else {
					deleteData( employee );
				}
			}

			if ( manager != null ) {
				deleteData( manager );
			}
		}

		HibernateORMHelper.inTransaction( sessionFactory, session -> {
			session.delete( session.load( BusinessUnit.class, businessUnitId ) );
		} );
	}

	private void deleteData(Employee employee) {
		HibernateORMHelper.inTransaction( sessionFactory, session -> {
			session.refresh( employee );

			EmployeeRepository repository = new EmployeeRepository( session );
			for ( PerformanceSummary summary : repository.findByEmployee( employee ) ) {
				session.remove( summary );
			}
			for ( QuestionnaireInstance questionnaire : repository.findByApprovalOrSubject( employee ) ) {
				session.remove( questionnaire );
			}

			if ( employee instanceof Manager ) {
				Manager manager = employee.getManager();
				if ( manager != null ) {
					manager.getEmployees().remove( employee );
					session.merge( manager );
				}
			}

			session.delete( employee );
		} );
	}
}
