package org.hibernate.performance.search.model.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Manager extends Employee {

	@OneToMany(mappedBy = "manager")
	private List<Employee> employees = new ArrayList<>();

	private Manager() {
	}

	public Manager(Integer id) {
		super( id );
	}
}
