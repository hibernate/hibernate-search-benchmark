package org.hibernate.performance.search.model.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataUpdater;

public abstract class AutomaticIndexingUpdatePartitionState {

	private static final int LARGE_NUMBER = 100_000_000;

	protected final DomainDataUpdater domainDataUpdater;
	protected final int initialCompanyCount;
	private final int numberOfThreads;
	private final int threadNumber;
	protected final List<Integer> partitionIds;
	private final Integer alternativeCompanyId;
	private final Random randomFixedSource;

	protected int companyBUInvocation = 0;
	protected int employeeInvocation = 0;
	protected int questionnaireInvocation = 0;
	protected int questionsInvocation = 0;

	public AutomaticIndexingUpdatePartitionState(SessionFactory sessionFactory,
			int initialCompanyCount, int numberOfThreads, int threadNumber) {
		this.domainDataUpdater = new DomainDataUpdater( sessionFactory );
		this.initialCompanyCount = initialCompanyCount;
		this.numberOfThreads = numberOfThreads;
		this.threadNumber = threadNumber;
		this.partitionIds = partitionIds();
		this.alternativeCompanyId = alternativeCompanyId();
		this.randomFixedSource = new Random( 739L );
	}

	public void updateCompanyBU() {
		domainDataUpdater.inTransaction( ( (session, up) -> {
			boolean reverse = companyBUInvocation % 2 == 1;
			int companyId = partitionId( companyBUInvocation / 2 );

			int fromCompanyId = ( reverse ) ? alternativeCompanyId : companyId;
			int toCompanyId = ( reverse ) ? companyId : alternativeCompanyId;

			up.doSomeChangesOnCompanyAndBusinessUnit( session, companyBUInvocation++, fromCompanyId, toCompanyId );
		} ) );
	}

	public abstract void updateEmployee();

	public abstract void updateQuestionnaire();

	public abstract void updateQuestion();

	protected Integer partitionId(int index) {
		return partitionIds.get( index % partitionIds.size() );
	}

	protected int getRandomOf(int lowerBoundIncluded, int upperBoundExcluded) {
		return randomFixedSource.nextInt( upperBoundExcluded - lowerBoundIncluded ) + lowerBoundIncluded;
	}

	private List<Integer> partitionIds() {
		List<Integer> result = new ArrayList<>( initialCompanyCount / numberOfThreads + 1 );
		for ( int i = 0; i < initialCompanyCount; i++ ) {
			if ( i % numberOfThreads == threadNumber ) {
				result.add( i );
			}
		}
		return result;
	}

	private Integer alternativeCompanyId() {
		return LARGE_NUMBER + threadNumber;
	}
}
