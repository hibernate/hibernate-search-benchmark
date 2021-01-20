package org.hibernate.performance.search.model.entity;

import java.util.Collections;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.NaturalId;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.performance.PerformanceSummary;

@Entity
public class Employee extends IdEntity {

	protected String firstName;
	protected String surname;

	@NaturalId
	protected String socialSecurityNumber;

	@ManyToOne
	protected Company company;

	@ManyToOne
	protected BusinessUnit businessUnit;

	@ManyToOne
	private Manager manager;

	@OneToMany(mappedBy = "subject")
	@Cascade(CascadeType.PERSIST)
	private List<QuestionnaireInstance> questionnaires;

	@OneToMany(mappedBy = "employee")
	@Cascade(CascadeType.PERSIST)
	private List<PerformanceSummary> performanceSummaries;

	protected Employee() {
	}

	public Employee(Integer id) {
		super( id );
	}

	public Employee(Manager manager, int id) {
		super( id );

		this.firstName = "name" + id;
		this.surname = "surname" + id;
		this.socialSecurityNumber = "socialSecurityNumber" + id;
		this.manager = manager;
		this.businessUnit = manager.getBusinessUnit();
		this.company = businessUnit.getOwner();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Company getCompany() {
		return company;
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

	public List<QuestionnaireInstance> getQuestionnaires() {
		return questionnaires;
	}

	public List<PerformanceSummary> getPerformanceSummaries() {
		return performanceSummaries;
	}

	@Override
	public String toString() {
		return "Employee{" +
				"id=" + id +
				'}';
	}
}
