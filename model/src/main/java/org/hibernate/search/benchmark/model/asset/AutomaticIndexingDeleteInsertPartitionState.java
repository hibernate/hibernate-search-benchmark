package org.hibernate.search.benchmark.model.asset;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.search.benchmark.model.application.DomainDataDeleteInserter;
import org.hibernate.search.benchmark.model.param.RelationshipSize;

public class AutomaticIndexingDeleteInsertPartitionState {

	private static final int LARGE_NUMBER = 100_000_000;

	private final DomainDataDeleteInserter deleteInserter;
	private final RelationshipSize relationshipSize;

	private final int initialCompanyCount;
	private final int numberOfThreads;
	private final int threadNumber;

	private final List<Integer> partitionIds;
	private final Integer newIdForThisThread;

	private int insertDeleteCount = 0;

	public AutomaticIndexingDeleteInsertPartitionState(SessionFactory sessionFactory,
			RelationshipSize relationshipSize, int initialCompanyCount,
			int numberOfThreads, int threadNumber) {
		this.deleteInserter = new DomainDataDeleteInserter( sessionFactory, relationshipSize );
		this.relationshipSize = relationshipSize;
		this.initialCompanyCount = initialCompanyCount;
		this.numberOfThreads = numberOfThreads;
		this.threadNumber = threadNumber;

		this.partitionIds = companyIds();
		// ids for temporary-created instances
		this.newIdForThisThread = newIdForThisThread();
	}

	public void employeesInsertDelete() {
		int companyId = companyId( insertDeleteCount++ );
		int managerId = companyId * relationshipSize.getEmployeesPerCompany();

		deleteInserter.inTransaction( ( (session, delIns) -> delIns
				.createEmployee( session, managerId, newIdForThisThread ) ) );

		deleteInserter.inTransaction( ( (session, delIns) -> delIns
				.deleteEmployee( session, newIdForThisThread ) ) );
	}

	public void questionnaireDefinitions() {
		int companyId = companyId( insertDeleteCount++ );

		deleteInserter.inTransaction( ( (session, delIns) -> delIns
				.createQuestionnaireDefinition( session, companyId, newIdForThisThread ) ) );

		deleteInserter.inTransaction( ( (session, delIns) -> delIns
				.deleteQuestionnaireDefinition( session, newIdForThisThread ) ) );
	}

	public void questionnaireInstances() {
		int companyId = companyId( insertDeleteCount++ );
		int approvalId = companyId * relationshipSize.getEmployeesPerCompany();

		deleteInserter.inTransaction( (session, delIns) -> delIns
				.deleteQuestionnaireInstancesFor( session, approvalId ) );
		deleteInserter.inTransaction( (session, delIns) -> delIns
				.createAndFillQuestionnaireInstancesFor( session, approvalId ) );
	}

	public void performanceSummaries() {
		int companyId = companyId( insertDeleteCount++ );
		int employeeId = companyId * relationshipSize.getEmployeesPerCompany();

		deleteInserter.inTransaction( (session, delIns) -> delIns
				.deletePerformanceSummaryFor( session, employeeId ) );
		deleteInserter.inTransaction( (session, delIns) -> delIns
				.createPerformanceSummaryFor( session, employeeId ) );
	}

	private Integer companyId(int index) {
		return partitionIds.get( index % partitionIds.size() );
	}

	private List<Integer> companyIds() {
		List<Integer> result = new ArrayList<>( initialCompanyCount / numberOfThreads + 1 );
		for ( int i = 0; i < initialCompanyCount; i++ ) {
			if ( i % numberOfThreads == threadNumber ) {
				result.add( i );
			}
		}
		return result;
	}

	private Integer newIdForThisThread() {
		// each partition works on different ids
		return LARGE_NUMBER + threadNumber;
	}
}
