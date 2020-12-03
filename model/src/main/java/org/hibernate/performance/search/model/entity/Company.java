package org.hibernate.performance.search.model.entity;

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

	@OneToMany(mappedBy = "owner")
	@Cascade(CascadeType.PERSIST)
	private List<BusinessUnit> businessUnits;

	private Company() {
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

	@Override
	public String toString() {
		return legalName;
	}
}
