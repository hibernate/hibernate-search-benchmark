package org.hibernate.performance.search.model.entity.performance;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.performance.search.model.entity.Employee;

@Entity
public class PerformanceSummary {

	@Id
	@GeneratedValue(generator = "performancesummary_seq")
	@SequenceGenerator(name = "performancesummary_seq")
	private Integer id;

	@ManyToOne
	private Employee employee;

	private Integer year;

	private Integer maxScore;

	private Integer employeeScore;

	PerformanceSummary() {
		// For Hibernate
	}

	public PerformanceSummary(Employee employee, Integer year, Integer maxScore, Integer employeeScore) {
		this.employee = employee;
		this.year = year;
		this.maxScore = maxScore;
		this.employeeScore = employeeScore;
	}

	public Integer getYear() {
		return year;
	}
}
