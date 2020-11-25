package org.hibernate.performance.search.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.NaturalId;

@Entity
public class Employee extends IdEntity {

	private String name;
	private String surname;

	@NaturalId
	private String socialSecurityNumber;

	@ManyToOne
	private Company company;

	@ManyToOne
	private BusinessUnit businessUnit;

	@ManyToOne
	private Manager manager;

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}
}
