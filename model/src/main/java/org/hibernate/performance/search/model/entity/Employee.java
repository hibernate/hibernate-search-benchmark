package org.hibernate.performance.search.model.entity;

import java.util.Collections;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.NaturalId;

@Entity
public class Employee extends IdEntity {

	protected String name;
	protected String surname;

	@NaturalId
	protected String socialSecurityNumber;

	@ManyToOne
	protected Company company;

	@ManyToOne
	protected BusinessUnit businessUnit;

	@ManyToOne
	private Manager manager;

	protected Employee() {
	}

	public Employee(Integer id) {
		super( id );
	}

	public Employee(Manager manager, int index) {
		super( index );

		this.name = "name"+id;
		this.surname = "surname"+id;
		this.socialSecurityNumber = "socialSecurityNumber"+id;
		this.manager = manager;
		this.businessUnit = manager.businessUnit;
		this.company = businessUnit.getOwner();
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public BusinessUnit getBusinessUnit() {
		return businessUnit;
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public List<Employee> getCollaborators() {
		return Collections.emptyList();
	}

	public List<Employee> getSelfAndColleagues() {
		if ( manager == null ) {
			return Collections.singletonList( this );
		}

		return manager.getCollaborators();
	}

	@Override
	public String toString() {
		return "Employee{" +
				"id=" + id +
				'}';
	}
}
