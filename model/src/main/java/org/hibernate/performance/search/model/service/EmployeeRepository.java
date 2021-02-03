package org.hibernate.performance.search.model.service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.answer.ClosedAnswer;
import org.hibernate.performance.search.model.entity.answer.OpenAnswer;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;

public class EmployeeRepository {

	private final EntityManager entityManager;

	public EmployeeRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Scroll<Employee> getEmployees(Company company, int batchSize) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Employee> criteria = builder.createQuery( Employee.class );
		Root<Employee> root = criteria.from( Employee.class );
		CriteriaQuery<Employee> query = criteria.select( root )
				.where( builder.equal( root.get( "company" ), company ) );
		return new Scroll<>( entityManager.unwrap( Session.class ).createQuery( query ), batchSize );
	}

	public List<Employee> getEmployees(Integer businessUnitId) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Employee> criteria = builder.createQuery( Employee.class );
		Root<Employee> root = criteria.from( Employee.class );
		CriteriaQuery<Employee> query = criteria.select( root )
				.where( builder.equal( root.get( "businessUnit" ).get( "id" ), businessUnitId ) );
		return entityManager.createQuery( query ).getResultList();
	}

	public List<QuestionnaireDefinition> getQuestionnaireDefinitions(Company company) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<QuestionnaireDefinition> criteria = builder.createQuery( QuestionnaireDefinition.class );
		Root<QuestionnaireDefinition> root = criteria.from( QuestionnaireDefinition.class );
		CriteriaQuery<QuestionnaireDefinition> query = criteria.select( root )
				.where( builder.equal( root.get( "company" ), company ) );
		return entityManager.createQuery( query ).getResultList();
	}

	public <T> long count(Class<T> entityType) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery( Long.class );
		criteria.select( builder.count( criteria.from( entityType ) ) );
		return entityManager.createQuery( criteria ).getSingleResult();
	}

	public Scroll<OpenAnswer> findAllOpenAnswersInNaturalOrder(Integer companyId, int batchSize) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<OpenAnswer> criteria = builder.createQuery( OpenAnswer.class );

		Root<OpenAnswer> root = criteria.from( OpenAnswer.class );
		Path<Object> companyIdPath = root.get( "question" ).get( "questionnaire" ).get( "company" ).get( "id" );
		Order questionnaireUniqueCode = builder.asc( root.get( "questionnaire" ).<String>get( "uniqueCode" ) );
		Order questionId = builder.asc( root.get( "question" ).<String>get( "id" ) );

		CriteriaQuery<OpenAnswer> query = criteria.select( root )
				.where( builder.equal( companyIdPath, companyId ) )
				.orderBy( questionnaireUniqueCode, questionId );
		return new Scroll<>( entityManager.unwrap( Session.class ).createQuery( query ), batchSize );
	}

	public Scroll<ClosedAnswer> findAllClosedAnswersInNaturalOrder(Integer companyId, int batchSize) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<ClosedAnswer> criteria = builder.createQuery( ClosedAnswer.class );

		Root<ClosedAnswer> root = criteria.from( ClosedAnswer.class );
		Path<Object> companyIdPath = root.get( "question" ).get( "questionnaire" ).get( "company" ).get( "id" );
		Order questionnaireUniqueCode = builder.asc( root.get( "questionnaire" ).<String>get( "uniqueCode" ) );
		Order questionId = builder.asc( root.get( "question" ).<String>get( "id" ) );

		CriteriaQuery<ClosedAnswer> query = criteria.select( root )
				.where( builder.equal( companyIdPath, companyId ) )
				.orderBy( questionnaireUniqueCode, questionId );
		return new Scroll<>( entityManager.unwrap( Session.class ).createQuery( query ), batchSize );
	}

	public long countFilledClosedAnswer() {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> criteria = builder.createQuery( Long.class );
		Root<ClosedAnswer> root = criteria.from( ClosedAnswer.class );
		CriteriaQuery<Long> query = criteria.select( builder.count( root ) )
				.where( builder.isNotNull( root.get( "choice" ) ) );

		return entityManager.createQuery( query ).getSingleResult();
	}

	public long countFilledOpenAnswer() {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> criteria = builder.createQuery( Long.class );
		Root<OpenAnswer> root = criteria.from( OpenAnswer.class );
		CriteriaQuery<Long> query = criteria.select( builder.count( root ) )
				.where( builder.isNotNull( root.get( "text" ) ) );

		return entityManager.createQuery( query ).getSingleResult();
	}

	public List<PerformanceSummary> findByEmployee(Employee employee) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PerformanceSummary> criteria = builder.createQuery( PerformanceSummary.class );
		Root<PerformanceSummary> root = criteria.from( PerformanceSummary.class );
		CriteriaQuery<PerformanceSummary> query = criteria.select( root )
				.where( builder.equal( root.get( "employee" ), employee ) );
		return entityManager.createQuery( query ).getResultList();
	}

	public List<QuestionnaireInstance> findBySubject(Employee subject) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<QuestionnaireInstance> criteria = builder.createQuery( QuestionnaireInstance.class );
		Root<QuestionnaireInstance> root = criteria.from( QuestionnaireInstance.class );
		CriteriaQuery<QuestionnaireInstance> query = criteria.select( root )
				.where( builder.equal( root.get( "subject" ), subject ) );
		return entityManager.createQuery( query ).getResultList();
	}

	public List<QuestionnaireInstance> findByApprovalOrSubject(Employee employee) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<QuestionnaireInstance> criteria = builder.createQuery( QuestionnaireInstance.class );
		Root<QuestionnaireInstance> root = criteria.from( QuestionnaireInstance.class );

		Predicate subjectPredicate = builder.equal( root.get( "subject" ), employee );
		Predicate employeePredicate = builder.equal( root.get( "approval" ), employee );

		CriteriaQuery<QuestionnaireInstance> query = criteria.select( root )
				.where( builder.or( subjectPredicate, employeePredicate ) );

		return entityManager.createQuery( query ).getResultList();
	}

	public List<QuestionnaireInstance> findByApproval(Integer approvalId) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<QuestionnaireInstance> criteria = builder.createQuery( QuestionnaireInstance.class );
		Root<QuestionnaireInstance> root = criteria.from( QuestionnaireInstance.class );

		CriteriaQuery<QuestionnaireInstance> query = criteria.select( root )
				.where( builder.equal( root.get( "approval" ).<String>get( "id" ), approvalId ) );

		return entityManager.createQuery( query ).getResultList();
	}

	public List<QuestionnaireInstance> findByDefinition(QuestionnaireDefinition questionnaireDefinition,
			int maxResults) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<QuestionnaireInstance> criteria = builder.createQuery( QuestionnaireInstance.class );
		Root<QuestionnaireInstance> root = criteria.from( QuestionnaireInstance.class );
		CriteriaQuery<QuestionnaireInstance> query = criteria.select( root )
				.where( builder.equal( root.get( "definition" ), questionnaireDefinition ) );
		return entityManager.createQuery( query ).setMaxResults( maxResults ).getResultList();
	}
}
