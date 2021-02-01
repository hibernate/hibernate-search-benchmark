package org.hibernate.performance.search.model.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.NaturalId;

@Entity
public class Company extends IdEntity {

	@NaturalId
	private String legalName;

	private String description;

	@OneToMany(mappedBy = "owner")
	@Cascade(CascadeType.ALL)
	private List<BusinessUnit> businessUnits = new ArrayList<>();

	Company() {
		// For Hibernate
	}

	public Company(Integer id, String legalName) {
		super( id );
		this.legalName = legalName;
	}

	public String getLegalName() {
		return legalName;
	}

	public List<BusinessUnit> getBusinessUnits() {
		return businessUnits;
	}

	public void setBusinessUnits(List<BusinessUnit> businessUnits) {
		this.businessUnits = businessUnits;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return legalName;
	}
}
