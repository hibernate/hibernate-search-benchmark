package org.hibernate.performance.search.model.asset;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataRemover;
import org.hibernate.performance.search.model.param.RelationshipSize;

public class AutomaticIndexingDeletePartitionState {

	private final DomainDataRemover domainDataRemover;
	private final List<Integer> partitionIds;
	private final int invocationSize;

	private int invocation = 0;

	public AutomaticIndexingDeletePartitionState(SessionFactory sessionFactory, RelationshipSize relationshipSize,
			int actualIndexSize, int numberOfThreads, int threadNumber, int invocationSize) {
		this.domainDataRemover = new DomainDataRemover( sessionFactory );
		this.partitionIds = businessUnitPartitionIds(
				relationshipSize, actualIndexSize, numberOfThreads, threadNumber );
		this.invocationSize = invocationSize;
	}

	public void executeDelete() {
		for ( int i = 0; i < invocationSize; i++ ) {
			if ( invocation >= partitionIds.size() ) {
				// no more ids to remove for this partition
				return;
			}

			int businessUnitId = partitionIds.get( invocation++ );
			domainDataRemover.deleteData( businessUnitId );
		}
	}

	private static List<Integer> businessUnitPartitionIds(RelationshipSize relationshipSize, int actualIndexSize,
			int numberOfThreads, int threadNumber) {
		int numberOfBusinessUnit = relationshipSize.getUnitsPerCompany() * actualIndexSize;
		List<Integer> result = new ArrayList<>( numberOfBusinessUnit / numberOfThreads + 1 );

		int i = 0;
		for ( int id : businessUnitIds( relationshipSize, actualIndexSize ) ) {
			if ( i++ % numberOfThreads == threadNumber ) {
				result.add( id );
			}
		}
		return result;
	}

	private static List<Integer> businessUnitIds(RelationshipSize relationshipSize, int actualIndexSize) {
		int unitsPerCompany = relationshipSize.getUnitsPerCompany();
		List<Integer> result = new ArrayList<>( unitsPerCompany * actualIndexSize );

		for ( int i = 0; i < unitsPerCompany; i++ ) {
			for ( int j = 0; j < actualIndexSize; j++ ) {
				result.add( unitsPerCompany - 1 - i + j * unitsPerCompany );
			}
		}
		return result;
	}
}
