package org.hibernate.performance.search.model.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataUpdater;

public abstract class AutomaticIndexingUpdatePartitionState {

	protected final DomainDataUpdater domainDataUpdater;
	protected final int actualIndexSize;
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
			int actualIndexSize, int numberOfThreads, int threadNumber, int invocationSize) {
		this.domainDataUpdater = new DomainDataUpdater( sessionFactory );
		this.actualIndexSize = actualIndexSize;
		this.numberOfThreads = numberOfThreads;
		this.threadNumber = threadNumber;
		this.partitionIds = partitionIds();
		this.alternativeCompanyId = alternativeCompanyId();
		this.randomFixedSource = new Random( 739L );
		this.invocationSize = invocationSize;
	}

	public void updateCompanyBU() {
		for ( int i = 0; i < invocationSize; i++ ) {
			updateCompanyBUOneTime();
		}
	}

	public void updateEmployee() {
		for ( int i = 0; i < invocationSize; i++ ) {
			updateEmployeeOneTime();
		}
	}

	public void updateQuestionnaire() {
		for ( int i = 0; i < invocationSize; i++ ) {
			updateQuestionnaireOneTime();
		}
	}

	public void updateQuestion() {
		for ( int i = 0; i < invocationSize; i++ ) {
			updateQuestionOneTime();
		}
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

	protected abstract void updateEmployeeOneTime();

	protected abstract void updateQuestionnaireOneTime();

	protected abstract void updateQuestionOneTime();

	protected Integer partitionId(int index) {
		return partitionIds.get( index % partitionIds.size() );
	}

	protected int getRandomOf(int lowerBoundIncluded, int upperBoundExcluded) {
		return randomFixedSource.nextInt( upperBoundExcluded - lowerBoundIncluded ) + lowerBoundIncluded;
	}

	private void updateCompanyBUOneTime() {
		boolean reverse = companyBUInvocation % 2 == 1;
		int companyId = partitionId( companyBUInvocation / 2 );

		int fromCompanyId = ( reverse ) ? alternativeCompanyId : companyId;
		int toCompanyId = ( reverse ) ? companyId : alternativeCompanyId;

		domainDataUpdater.doSomeChangesOnCompanyAndBusinessUnit( companyBUInvocation++, fromCompanyId, toCompanyId );
	}

	private List<Integer> partitionIds() {
		List<Integer> result = new ArrayList<>( actualIndexSize / numberOfThreads + 1 );
		for ( int i = 0; i < actualIndexSize; i++ ) {
			if ( i % numberOfThreads == threadNumber ) {
				result.add( i );
			}
		}
		return result;
	}

	private Integer alternativeCompanyId() {
		return actualIndexSize + threadNumber;
	}
}
