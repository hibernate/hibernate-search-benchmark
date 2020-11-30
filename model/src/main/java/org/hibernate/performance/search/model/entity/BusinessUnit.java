package org.hibernate.performance.search.model.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class BusinessUnit extends IdEntity {

	private String name;

	@ManyToOne
	private Company owner;

}
