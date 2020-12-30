package org.hibernate.performance.search.model.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataFiller;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.param.RelationshipSize;

public class AutomaticIndexingState {

	private final RelationshipSize relationshipSize;
	private final int initialIndexSize;
	private final int insertInvocationSize;
	private final int updateInvocationSize;
	private final int deleteInvocationSize;
	private final int numberOfThreads;
	private final Properties additionalProperties;

	private List<AutomaticIndexingInsertPartitionState> indexInsertPartitions;
	private List<AutomaticIndexingUpdatePartitionState> indexUpdatePartitions;
	private List<AutomaticIndexingDeletePartitionState> indexDeletePartitions;
	private SessionFactory sessionFactory;

	public AutomaticIndexingState(RelationshipSize relationshipSize, int initialIndexSize, int insertInvocationSize,
			int updateInvocationSize, int deleteInvocationSize, int numberOfThreads, Properties additionalProperties) {
		this.relationshipSize = relationshipSize;
		this.initialIndexSize = initialIndexSize;
		this.insertInvocationSize = insertInvocationSize;
		this.deleteInvocationSize = deleteInvocationSize;
		this.numberOfThreads = numberOfThreads;
		this.additionalProperties = additionalProperties;

		if ( RelationshipSize.SMALL.equals( relationshipSize ) && updateInvocationSize % 2 == 1 ) {
			// make the invocationSize even
			this.updateInvocationSize = updateInvocationSize + 1;
		} else {
			this.updateInvocationSize = updateInvocationSize;
		}
	}

	public void start() {
		sessionFactory = HibernateORMHelper.buildSessionFactory( additionalProperties );
		DomainDataFiller domainDataFiller = new DomainDataFiller( sessionFactory, relationshipSize );
		for ( int i = 0; i < initialIndexSize; i++ ) {
			domainDataFiller.fillData( i );
		}
		indexInsertPartitions = createInsertPartitions();
	}

	public void stop() {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	public AutomaticIndexingInsertPartitionState getInsertPartition(int threadNumber) {
		checkThreadNumber( threadNumber );
		return indexInsertPartitions.get( threadNumber );
	}

	public AutomaticIndexingUpdatePartitionState getUpdatePartition(int threadNumber) {
		checkThreadNumber( threadNumber );
		synchronized (this) {
			if ( indexUpdatePartitions == null ) {
				indexUpdatePartitions = createUpdatePartitions();
			}
		}
		return indexUpdatePartitions.get( threadNumber );
	}

	public AutomaticIndexingDeletePartitionState getDeletePartition(int threadNumber) {
		checkThreadNumber( threadNumber );
		synchronized (this) {
			if ( indexDeletePartitions == null ) {
				indexDeletePartitions = createDeletePartitions();
			}
		}
		return indexDeletePartitions.get( threadNumber );
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	private List<AutomaticIndexingInsertPartitionState> createInsertPartitions() {
		List<AutomaticIndexingInsertPartitionState> result = new ArrayList<>( numberOfThreads );
		for ( int i = 0; i < numberOfThreads; i++ ) {
			result.add( new AutomaticIndexingInsertPartitionState( sessionFactory, relationshipSize, initialIndexSize,
					insertInvocationSize, numberOfThreads, i
			) );
		}

		return result;
	}

	private List<AutomaticIndexingUpdatePartitionState> createUpdatePartitions() {
		List<AutomaticIndexingUpdatePartitionState> result = new ArrayList<>( numberOfThreads );
		for ( AutomaticIndexingInsertPartitionState threadState : indexInsertPartitions ) {
			int actualIndexSize = threadState.actualIndexSize();
			int threadNumber = threadState.threadNumber();
			result.add( createUpdatePartition( actualIndexSize, threadNumber ) );
		}
		return result;
	}

	private AutomaticIndexingUpdatePartitionState createUpdatePartition(int actualIndexSize, int threadNumber) {
		return ( RelationshipSize.SMALL.equals( relationshipSize ) ) ? new AutomaticIndexingUpdateSmallPartitionState(
				sessionFactory, actualIndexSize, numberOfThreads, threadNumber, updateInvocationSize
		) : new AutomaticIndexingUpdateMLPartitionState(
				sessionFactory, relationshipSize, actualIndexSize, numberOfThreads, threadNumber, updateInvocationSize
		);
	}

	private List<AutomaticIndexingDeletePartitionState> createDeletePartitions() {
		List<AutomaticIndexingDeletePartitionState> result = new ArrayList<>( numberOfThreads );
		for ( AutomaticIndexingInsertPartitionState threadState : indexInsertPartitions ) {
			int actualIndexSize = threadState.actualIndexSize();
			int threadNumber = threadState.threadNumber();
			result.add( new AutomaticIndexingDeletePartitionState(
					sessionFactory, relationshipSize, actualIndexSize, numberOfThreads, threadNumber,
					deleteInvocationSize
			) );
		}
		return result;
	}

	private void checkThreadNumber(int threadNumber) {
		if ( threadNumber >= numberOfThreads ) {
			throw new InvalidThreadNumberException( threadNumber, numberOfThreads );
		}
	}
}
