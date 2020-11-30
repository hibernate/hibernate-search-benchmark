package org.hibernate.performance.search.model.entity.performance;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.IdEntity;

@Entity
public class PerformanceSummary extends IdEntity {

	@ManyToOne
	private Employee employee;

	private Integer year;

	private Integer maxScore;

	private Integer employeeScore;

}
