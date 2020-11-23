package org.hibernate.performance.search.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Employee {

	@Id
	private Long id;

	private String name;

}
