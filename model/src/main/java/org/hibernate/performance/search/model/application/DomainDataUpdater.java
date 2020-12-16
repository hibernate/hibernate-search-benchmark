package org.hibernate.performance.search.model.application;

import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;

public class DomainDataUpdater {

	private final SessionFactory sessionFactory;
	private final AtomicInteger counter = new AtomicInteger();

	private Company oldCompany;
	private Manager oldManager;

	public DomainDataUpdater(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void doSomeChangesOnCompanyAndBusinessUnit(int businessUnitId, int newCompanyId) {
		int iteration = counter.getAndIncrement();

		HibernateORMHelper.inTransaction( sessionFactory, session -> {
			Company newCompany;
			if ( oldCompany == null ) {
				newCompany = new Company( newCompanyId, "Company" + newCompanyId );
				session.persist( newCompany );
			} else {
				newCompany = oldCompany;
			}

			BusinessUnit businessUnit = session.load( BusinessUnit.class, businessUnitId );
			oldCompany = businessUnit.getOwner();

			// change the description of the existing company,
			// these will trigger the reindex of all business units
			oldCompany.setDescription(
					"Let's change the description of " + oldCompany.getLegalName() + "! (" + iteration + ")" );
			newCompany.setDescription(
					"Let's change the description of " + newCompany.getLegalName() + "! (" + iteration + ")" );

			// move the business unit to the new company,
			// this will trigger the reindex of the 2 companies
			oldCompany.getBusinessUnits().remove( businessUnit );
			newCompany.getBusinessUnits().add( businessUnit );

			session.merge( oldCompany );
			session.merge( newCompany );
		} );
	}

	public void doSomeChangesOnEmployee(int employeeId, int managerId) {
		int iteration = counter.getAndIncrement();

		HibernateORMHelper.inTransaction( sessionFactory, session -> {
			Employee employee = session.load( Employee.class, employeeId );

			Manager newManager = ( oldManager == null ) ? session.load( Manager.class, managerId ) : oldManager;
			oldManager = employee.getManager();

			employee.setManager( newManager );
			// remove the employee from old manager
			oldManager.getEmployees().remove( employee );
			// add the employee to the new manager
			newManager.getEmployees().add( employee );

			// change their names
			employee.setName( "nameE" + iteration );
			employee.setSurname( "surnameE" + iteration );
			oldManager.setName( "nameOM" + iteration );
			oldManager.setSurname( "surnameOM" + iteration );
			newManager.setName( "nameNM" + iteration );
			newManager.setSurname( "surnameNM" + iteration );

			session.merge( employee );
			session.merge( oldManager );
			session.merge( newManager );
		} );
	}

}
