package org.hibernate.performance.search.model.entity;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
public class Manager extends Employee {

	@OneToMany(mappedBy = "manager")
	@Cascade(CascadeType.PERSIST)
	private List<Employee> employees;

	private Manager() {
	}

	public Manager(BusinessUnit businessUnit, int baseId) {
		super( baseId );
		this.name = "name"+id;
		this.surname = "surname"+id;
		this.socialSecurityNumber = "socialSecurityNumber"+id;
		this.company = businessUnit.getOwner();
		this.businessUnit = businessUnit;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	@Override
	public List<Employee> getCollaborators() {
		return employees;
	}
}
