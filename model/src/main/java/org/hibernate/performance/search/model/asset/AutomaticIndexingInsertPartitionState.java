package org.hibernate.performance.search.model.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataFiller;
import org.hibernate.performance.search.model.param.RelationshipSize;

public class AutomaticIndexingInsertPartitionState {

	private final DomainDataFiller domainDataFiller;
	private final Function<Integer, List<Integer>> companyIdsFunction;
	private final int initialIndexSize;
	private final int iterationStepSize;
	private final int threadNumber;

	private int invocation = 0;

	public AutomaticIndexingInsertPartitionState(SessionFactory sessionFactory,
			RelationshipSize relationshipSize, int initialIndexSize, int invocationSize, int numberOfThreads,
			int threadNumber) {
		this.domainDataFiller = new DomainDataFiller( sessionFactory, relationshipSize );
		this.companyIdsFunction = companyIdsFunction( initialIndexSize, invocationSize, numberOfThreads, threadNumber );
		this.initialIndexSize = initialIndexSize;
		this.iterationStepSize = invocationSize * numberOfThreads;
		this.threadNumber = threadNumber;
	}

	public void executeInsert() {
		for ( int companyId : companyIdsFunction.apply( invocation++ ) ) {
			domainDataFiller.fillData( companyId );
		}
	}

	public int actualIndexSize() {
		return initialIndexSize + ( iterationStepSize * invocation );
	}

	public int threadNumber() {
		return threadNumber;
	}

	private static Function<Integer, List<Integer>> companyIdsFunction(int initialIndexSize, int invocationSize,
			int numberOfThreads, int threadNumber) {

		return ( invocation -> {
			int baseInvocationId = invocation * invocationSize * numberOfThreads;

			ArrayList<Integer> result = new ArrayList<>( ( initialIndexSize / numberOfThreads ) + 1 );
			for ( int i = 0; i < invocationSize; i++ ) {
				if ( i % numberOfThreads == threadNumber ) {
					result.add( initialIndexSize + baseInvocationId + i );
				}
			}

			return result;
		} );
	}
}
