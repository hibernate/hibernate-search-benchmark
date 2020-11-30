package org.hibernate.performance.search.model.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;

public class EmployeeFactory {

	private static final int EMPLOYEE_PER_BUSINESS_UNIT = 10;

	public static Manager createEmployeeTree(Company company) {
		List<BusinessUnit> businessUnits = company.getBusinessUnits();
		Manager[] managers = new Manager[businessUnits.size() + 1];

		// first element is null
		managers[0] = null;

		for ( int i = 0; i < businessUnits.size(); i++ ) {
			BusinessUnit unit = businessUnits.get( i );
			Manager manager = createManagerAndEmployees( unit );
			managers[i + 1] = manager;
		}

		linkManagers( managers );
		return managers[1];
	}

	private static Manager createManagerAndEmployees(BusinessUnit businessUnit) {
		int baseId = businessUnit.getId() * EMPLOYEE_PER_BUSINESS_UNIT;
		Manager manager = new Manager( businessUnit, baseId );

		// creating 2 extra slot to link managers
		ArrayList<Employee> employees = new ArrayList<>( EMPLOYEE_PER_BUSINESS_UNIT + 1 );
		for ( int i = 1; i < EMPLOYEE_PER_BUSINESS_UNIT; i++ ) {
			employees.add( new Employee( manager, baseId + i ) );
		}

		manager.setEmployees( employees );
		return manager;
	}

	private static void linkManagers(Manager[] managers) {
		for ( int i = 2; i < managers.length; i++ ) {
			int managerIndex = i / 2;

			Manager manager = managers[managerIndex];
			Manager employee = managers[i];

			manager.getEmployees().add( employee );
			employee.setManager( manager );
		}
	}
}
