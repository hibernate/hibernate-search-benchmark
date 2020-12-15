package org.hibernate.performance.search.model.application;

import java.util.ArrayList;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;

public class DomainDataUpdater {

	private final SessionFactory sessionFactory;

	public DomainDataUpdater(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void doSomeChangesOnCompanyAndBusinessUnit(int businessUnitId, int newCompanyId) {
		HibernateORMHelper.inTransaction( sessionFactory, session -> {
			// create a new company
			Company newCompany = new Company( newCompanyId, "Company" + newCompanyId );
			session.persist( newCompany );

			BusinessUnit businessUnit = session.load( BusinessUnit.class, businessUnitId );
			Company oldCompany = businessUnit.getOwner();

			// change the description of the existing company,
			// these will trigger the reindex of all business units
			oldCompany.setDescription( "Let's change the description of " + oldCompany.getLegalName() + "!" );

			// move the business unit to the new company,
			// this will trigger the reindex of the 2 companies
			oldCompany.getBusinessUnits().remove( businessUnit );

			ArrayList<BusinessUnit> businessUnits = new ArrayList<>();
			businessUnits.add( businessUnit );

			newCompany.setBusinessUnits( businessUnits );
			session.merge( oldCompany );
			session.merge( newCompany );
		});
	}

	public void doSomeChangesOnEmployee(int employeeId, int managerId) {
		HibernateORMHelper.inTransaction( sessionFactory, session -> {
			Employee employee = session.load( Employee.class, employeeId );
			Manager oldManager = employee.getManager();
			Manager newManager = session.load( Manager.class, managerId );

			employee.setManager( newManager );
			// remove the employee from old manager
			oldManager.getEmployees().remove( employee );
			// add the employee to the new manager
			newManager.getEmployees().add( employee );

			// change their names
			employee.setName( employee.getName() + ":changed!" );
			employee.setSurname( employee.getSurname() + ":changed!" );
			oldManager.setName( oldManager.getName() + ":changed!" );
			oldManager.setSurname( oldManager.getSurname() + ":changed!" );
			newManager.setName( newManager.getName() + ":changed!" );
			newManager.setSurname( newManager.getSurname() + ":changed!" );

			session.merge( employee );
			session.merge( oldManager );
			session.merge( newManager );
		} );
	}

}
