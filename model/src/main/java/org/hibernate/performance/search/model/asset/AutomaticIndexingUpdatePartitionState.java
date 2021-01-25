package org.hibernate.performance.search.model.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataUpdater;

public abstract class AutomaticIndexingUpdatePartitionState {

	private static final int LARGE_NUMBER = 100_000_000;

	private final DomainDataUpdater domainDataUpdater;
	protected final int initialCompanyCount;
	private final int numberOfThreads;
	private final int threadNumber;
	protected final List<Integer> partitionIds;
	private final Integer alternativeCompanyId;
	private final Random randomFixedSource;
	private final int invocationSize;

	protected int companyBUInvocation = 0;
	protected int employeeInvocation = 0;
	protected int questionnaireInvocation = 0;
	protected int questionsInvocation = 0;

	public AutomaticIndexingUpdatePartitionState(SessionFactory sessionFactory,
			int initialCompanyCount, int numberOfThreads, int threadNumber, int invocationSize) {
		this.domainDataUpdater = new DomainDataUpdater( sessionFactory );
		this.initialCompanyCount = initialCompanyCount;
		this.numberOfThreads = numberOfThreads;
		this.threadNumber = threadNumber;
		this.partitionIds = partitionIds();
		this.alternativeCompanyId = alternativeCompanyId();
		this.randomFixedSource = new Random( 739L );
		this.invocationSize = invocationSize;
	}

	public void updateCompanyBU() {
		domainDataUpdater.inTransaction( ( (session, up) -> {
			for ( int i = 0; i < invocationSize; i++ ) {
				updateCompanyBUOneTime( session, up );
			}
		} ) );
	}

	public void updateEmployee() {
		domainDataUpdater.inTransaction( ( (session, up) -> {
			for ( int i = 0; i < invocationSize; i++ ) {
				updateEmployeeOneTime( session, up );
			}
		} ) );
	}

	public void updateQuestionnaire() {
		domainDataUpdater.inTransaction( ( (session, up) -> {
			for ( int i = 0; i < invocationSize; i++ ) {
				updateQuestionnaireOneTime( session, up );
			}
		} ) );
	}

	public void updateQuestion() {
		domainDataUpdater.inTransaction( ( (session, up) -> {
			for ( int i = 0; i < invocationSize; i++ ) {
				updateQuestionOneTime( session, up );
			}
		} ) );
	}

	public int getCompanyBUInvocation() {
		return companyBUInvocation;
	}

	public int getEmployeeInvocation() {
		return employeeInvocation;
	}

	public int getQuestionnaireInvocation() {
		return questionnaireInvocation;
	}

	public int getQuestionsInvocation() {
		return questionsInvocation;
	}

	protected abstract void updateEmployeeOneTime(Session session, DomainDataUpdater up);

	protected abstract void updateQuestionnaireOneTime(Session session, DomainDataUpdater up);

	protected abstract void updateQuestionOneTime(Session session, DomainDataUpdater up);

	protected Integer partitionId(int index) {
		return partitionIds.get( index % partitionIds.size() );
	}

	protected int getRandomOf(int lowerBoundIncluded, int upperBoundExcluded) {
		return randomFixedSource.nextInt( upperBoundExcluded - lowerBoundIncluded ) + lowerBoundIncluded;
	}

	private void updateCompanyBUOneTime(Session session, DomainDataUpdater up) {
		boolean reverse = companyBUInvocation % 2 == 1;
		int companyId = partitionId( companyBUInvocation / 2 );

		int fromCompanyId = ( reverse ) ? alternativeCompanyId : companyId;
		int toCompanyId = ( reverse ) ? companyId : alternativeCompanyId;

		up.doSomeChangesOnCompanyAndBusinessUnit( session, companyBUInvocation++, fromCompanyId, toCompanyId );
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
