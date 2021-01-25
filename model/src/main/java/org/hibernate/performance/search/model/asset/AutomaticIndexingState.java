package org.hibernate.performance.search.model.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataInitializer;
import org.hibernate.performance.search.model.application.DomainDataRemover;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.model.param.RelationshipSize;

public class AutomaticIndexingState {

	private final RelationshipSize relationshipSize;
	private final int initialCompanyCount;
	private final int insertInvocationSize;
	private final int updateInvocationSize;
	private final int numberOfThreads;
	private final Properties additionalProperties;
	private final ModelService modelService;

	private List<AutomaticIndexingUpdatePartitionState> indexUpdatePartitions;
	private List<AutomaticIndexingDeleteInsertPartitionState> indexingDeleteInsertPartitions;
	private SessionFactory sessionFactory;
	private boolean started;

	public AutomaticIndexingState(RelationshipSize relationshipSize, int initialCompanyCount, int insertInvocationSize,
			int updateInvocationSize, int numberOfThreads, Properties additionalProperties,
			ModelService modelService) {
		this.relationshipSize = relationshipSize;
		this.initialCompanyCount = initialCompanyCount;
		this.insertInvocationSize = insertInvocationSize;
		this.numberOfThreads = numberOfThreads;
		this.additionalProperties = additionalProperties;
		this.modelService = modelService;

		if ( RelationshipSize.SMALL.equals( relationshipSize ) && updateInvocationSize % 2 == 1 ) {
			// make the invocationSize even
			this.updateInvocationSize = updateInvocationSize + 1;
		}
		else {
			this.updateInvocationSize = updateInvocationSize;
		}
	}

	public synchronized void startTrial() {
		if ( started ) {
			return;
		}
		sessionFactory = HibernateORMHelper.buildSessionFactory( additionalProperties );
		start();
	}

	public synchronized void stopTrial() {
		if ( !started ) {
			return;
		}
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
		stop();
	}

	public synchronized void startIteration() {
		if ( started ) {
			return;
		}
		start();
	}

	public synchronized void stopIteration() {
		if ( !started ) {
			return;
		}
		new DomainDataRemover( sessionFactory ).truncateAll();
		if ( modelService != null ) {
			try ( Session session = sessionFactory.openSession() ) {
				modelService.purgeAllIndexes( session );
			}
		}
		stop();
	}

	public AutomaticIndexingUpdatePartitionState getUpdatePartition(int threadNumber) {
		checkThreadNumber( threadNumber );
		return indexUpdatePartitions.get( threadNumber );
	}

	public AutomaticIndexingDeleteInsertPartitionState getDeleteInsertPartition(int threadNumber) {
		checkThreadNumber( threadNumber );
		return indexingDeleteInsertPartitions.get( threadNumber );
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	private void start() {
		DomainDataInitializer domainDataInitializer = new DomainDataInitializer( sessionFactory, relationshipSize );
		for ( int i = 0; i < initialCompanyCount; i++ ) {
			domainDataInitializer.initAllCompanyData( i );
		}
		indexUpdatePartitions = createUpdatePartitions();
		indexingDeleteInsertPartitions = createDeleteInsertPartitions();
		started = true;
	}

	private void stop() {
		indexUpdatePartitions = null;
		indexingDeleteInsertPartitions = null;
		started = false;
	}

	private List<AutomaticIndexingUpdatePartitionState> createUpdatePartitions() {
		List<AutomaticIndexingUpdatePartitionState> result = new ArrayList<>( numberOfThreads );
		for ( int i = 0; i < numberOfThreads; i++ ) {
			result.add( createUpdatePartition( i ) );
		}
		return result;
	}

	private AutomaticIndexingUpdatePartitionState createUpdatePartition(int threadNumber) {
		return ( RelationshipSize.SMALL.equals( relationshipSize ) ) ? new AutomaticIndexingUpdateSmallPartitionState(
				sessionFactory, initialCompanyCount, numberOfThreads, threadNumber, updateInvocationSize
		) : new AutomaticIndexingUpdateMLPartitionState(
				sessionFactory, relationshipSize, initialCompanyCount, numberOfThreads, threadNumber, updateInvocationSize
		);
	}

	private List<AutomaticIndexingDeleteInsertPartitionState> createDeleteInsertPartitions() {
		List<AutomaticIndexingDeleteInsertPartitionState> result = new ArrayList<>( numberOfThreads );
		for ( int i = 0; i < numberOfThreads; i++ ) {
			result.add( new AutomaticIndexingDeleteInsertPartitionState(
					sessionFactory, relationshipSize, initialCompanyCount, numberOfThreads, i, insertInvocationSize
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
