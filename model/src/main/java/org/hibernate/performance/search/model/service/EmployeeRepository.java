package org.hibernate.performance.search.model.service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;

public class EmployeeRepository {

	private final EntityManager entityManager;

	public EmployeeRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public List<Employee> getEmployees(Company company) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Employee> q = cb.createQuery( Employee.class );
		Root<Employee> e = q.from( Employee.class );
		CriteriaQuery<Employee> query = q.select( e ).where(
				cb.equal( e.get( "company" ), company ) );
		return entityManager.createQuery( query ).getResultList();
	}

	public List<QuestionnaireDefinition> getQuestionnaireDefinitions(Company company) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<QuestionnaireDefinition> q = cb.createQuery( QuestionnaireDefinition.class );
		Root<QuestionnaireDefinition> qu = q.from( QuestionnaireDefinition.class );
		CriteriaQuery<QuestionnaireDefinition> query = q.select( qu ).where(
				cb.equal( qu.get( "company" ), company ) );
		return entityManager.createQuery( query ).getResultList();
	}

}
