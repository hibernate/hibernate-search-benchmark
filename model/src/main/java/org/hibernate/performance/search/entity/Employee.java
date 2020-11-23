package org.hibernate.performance.search.entity;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Employee {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	public Employee() {
		this.name = UUID.randomUUID().toString();
	}
}
