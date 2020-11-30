package org.hibernate.performance.search.model.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.NaturalId;

@Entity
public class Company extends IdEntity {

	@NaturalId
	private String legalName;

	@OneToMany(mappedBy = "owner")
	private List<BusinessUnit> businessUnits = new ArrayList<>();

	public String getLegalName() {
		return legalName;
	}
}
