package org.hibernate.performance.search.model.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataInitializer;
import org.hibernate.performance.search.model.param.RelationshipSize;

public class AutomaticIndexingInsertPartitionState {

	private final DomainDataInitializer domainDataInitializer;
	private final Function<Integer, List<Integer>> companyIdsFunction;

	private int invocation = 0;

	public AutomaticIndexingInsertPartitionState(SessionFactory sessionFactory,
			RelationshipSize relationshipSize, int initialIndexSize, int invocationSize, int numberOfThreads,
			int threadNumber) {
		this.domainDataInitializer = new DomainDataInitializer( sessionFactory, relationshipSize );
		this.companyIdsFunction = companyIdsFunction( initialIndexSize, invocationSize, numberOfThreads, threadNumber );
	}

	public void executeInsert() {
		for ( int companyId : companyIdsFunction.apply( invocation++ ) ) {
			domainDataInitializer.initAllCompanyData( companyId );
		}
	}

	private static Function<Integer, List<Integer>> companyIdsFunction(int initialIndexSize, int invocationSize,
			int numberOfThreads, int threadNumber) {

		return ( invocation -> {
			int baseInvocationId = invocation * invocationSize * numberOfThreads;

			ArrayList<Integer> result = new ArrayList<>( invocationSize );
			int i = 0;

			while ( result.size() < invocationSize ) {
				if ( i % numberOfThreads == threadNumber ) {
					result.add( initialIndexSize + baseInvocationId + i );
				}
				i++;
			}

			return result;
		} );
	}
}
