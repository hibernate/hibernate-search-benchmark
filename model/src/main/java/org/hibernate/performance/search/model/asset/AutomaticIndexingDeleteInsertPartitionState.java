package org.hibernate.performance.search.model.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataDeleteInserter;
import org.hibernate.performance.search.model.param.RelationshipSize;

public class AutomaticIndexingDeleteInsertPartitionState {

	private static final int LARGE_NUMBER = 100_000_000;

	private final DomainDataDeleteInserter deleteInserter;
	private final RelationshipSize relationshipSize;

	private final int initialIndexSize;
	private final int numberOfThreads;
	private final int threadNumber;
	private final int invocationSize;

	private final List<Integer> partitionIds;
	private final List<Integer> newIdsForThisThread;

	private int insertDeleteCount = 0;

	public AutomaticIndexingDeleteInsertPartitionState(SessionFactory sessionFactory,
			RelationshipSize relationshipSize, int initialIndexSize,
			int numberOfThreads, int threadNumber, int invocationSize) {
		this.deleteInserter = new DomainDataDeleteInserter( sessionFactory, relationshipSize );
		this.relationshipSize = relationshipSize;
		this.initialIndexSize = initialIndexSize;
		this.numberOfThreads = numberOfThreads;
		this.threadNumber = threadNumber;
		this.invocationSize = invocationSize;

		this.partitionIds = companyIds();
		// ids for temporary-created instances
		this.newIdsForThisThread = newIdsForThisThread();
	}

	public void employeesInsertDelete() {
		int companyId = companyId( insertDeleteCount++ );
		int managerId = companyId * relationshipSize.getEmployeesPerCompany();

		deleteInserter.inTransaction( ( (session, delIns) -> {
			for ( int employeeId : newIdsForThisThread ) {
				delIns.createEmployee( session, managerId, employeeId );
			}
		} ) );

		deleteInserter.inTransaction( ( (session, delIns) -> {
			for ( int employeeId : newIdsForThisThread ) {
				delIns.deleteEmployee( session, employeeId );
			}
		} ) );
	}

	public void questionnaireDefinitions() {
		int companyId = companyId( insertDeleteCount++ );

		deleteInserter.inTransaction( ( (session, delIns) -> {
			for ( int questionnaireDefinitionsId : newIdsForThisThread ) {
				delIns.createQuestionnaireDefinition( session, companyId, questionnaireDefinitionsId );
			}
		} ) );

		deleteInserter.inTransaction( ( (session, delIns) -> {
			for ( int questionnaireDefinitionsId : newIdsForThisThread ) {
				delIns.deleteQuestionnaireDefinition( session, questionnaireDefinitionsId );
			}
		} ) );
	}

	public void questionnaireInstances() {
		int companyId = companyId( insertDeleteCount++ );
		int approvalId = companyId * relationshipSize.getEmployeesPerCompany();

		AtomicInteger instancesWorked = new AtomicInteger( 0 );
		while ( instancesWorked.get() < invocationSize ) {
			deleteInserter.inTransaction( (session, delIns) -> instancesWorked
					.addAndGet( delIns.deleteQuestionnaireInstancesFor( session, approvalId ) ) );
			deleteInserter.inTransaction(
					(session, delIns) -> delIns.createAndFillQuestionnaireInstancesFor( session, approvalId ) );
		}
	}

	public void performanceSummaries() {
		int companyId = companyId( insertDeleteCount++ );
		int employeeId = companyId * relationshipSize.getEmployeesPerCompany();

		AtomicInteger instancesWorked = new AtomicInteger( 0 );
		while ( instancesWorked.get() < invocationSize ) {
			deleteInserter.inTransaction( (session, delIns) -> instancesWorked
					.addAndGet( delIns.deletePerformanceSummaryFor( session, employeeId ) ) );
			deleteInserter.inTransaction(
					(session, delIns) -> delIns.createPerformanceSummaryFor( session, employeeId ) );
		}
	}

	private Integer companyId(int index) {
		return partitionIds.get( index % partitionIds.size() );
	}

	private List<Integer> companyIds() {
		List<Integer> result = new ArrayList<>( initialIndexSize / numberOfThreads + 1 );
		for ( int i = 0; i < initialIndexSize; i++ ) {
			if ( i % numberOfThreads == threadNumber ) {
				result.add( i );
			}
		}
		return result;
	}

	private List<Integer> newIdsForThisThread() {
		// each partition works on different ids
		final int baseEmployeeId = LARGE_NUMBER + threadNumber;
		ArrayList<Integer> result = new ArrayList<>();
		for ( int i = 0; i < invocationSize; i++ ) {
			result.add( baseEmployeeId + ( i * numberOfThreads ) );
		}
		return result;
	}
}
