package org.hibernate.search.benchmark.model.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class BusinessUnit extends IdEntity {

	private String name;

	@ManyToOne
	private Company owner;

	BusinessUnit() {
		// For Hibernate
	}

	public BusinessUnit(Integer id, String name, Company owner) {
		super( id );
		this.name = name;
		this.owner = owner;
	}

	public Company getOwner() {
		return owner;
	}

	public void setOwner(Company owner) {
		this.owner = owner;
	}
}
