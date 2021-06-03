package org.hibernate.search.benchmark.model.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.benchmark.model.application.DomainDataInitializer;
import org.hibernate.search.benchmark.model.application.DomainDataRemover;
import org.hibernate.search.benchmark.model.application.HibernateORMHelper;
import org.hibernate.search.benchmark.model.application.ModelService;
import org.hibernate.search.benchmark.model.param.RelationshipSize;

public class AutomaticIndexingState {

	private final RelationshipSize relationshipSize;
	private final int initialCompanyCount;
	private final int numberOfThreads;
	private final Properties additionalProperties;
	private final ModelService modelService;

	private List<AutomaticIndexingUpdatePartitionState> indexUpdatePartitions;
	private List<AutomaticIndexingDeleteInsertPartitionState> indexingDeleteInsertPartitions;
	private SessionFactory sessionFactory;

	private boolean trialStarted = false;
	private boolean iterationStarted = false;

	public AutomaticIndexingState(RelationshipSize relationshipSize, int initialCompanyCount,
			int numberOfThreads, Properties additionalProperties, ModelService modelService) {
		this.relationshipSize = relationshipSize;
		this.initialCompanyCount = initialCompanyCount;
		this.numberOfThreads = numberOfThreads;
		this.additionalProperties = additionalProperties;
		this.modelService = modelService;
	}

	public synchronized void startTrial() {
		if ( trialStarted ) {
			return;
		}
		sessionFactory = HibernateORMHelper.buildSessionFactory( additionalProperties );
		trialStarted = true;
	}

	public synchronized void stopTrial() {
		if ( !trialStarted ) {
			return;
		}
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
		trialStarted = false;
	}

	public synchronized void startIteration() {
		if ( iterationStarted ) {
			return;
		}
		DomainDataInitializer domainDataInitializer = new DomainDataInitializer( modelService, sessionFactory, relationshipSize );
		for ( int i = 0; i < initialCompanyCount; i++ ) {
			domainDataInitializer.initAllCompanyData( i );
		}
		indexUpdatePartitions = createUpdatePartitions();
		indexingDeleteInsertPartitions = createDeleteInsertPartitions();
		iterationStarted = true;
	}

	public synchronized void stopIteration() {
		if ( !iterationStarted ) {
			return;
		}
		new DomainDataRemover( sessionFactory ).truncateAll();
		if ( modelService != null ) {
			try ( Session session = sessionFactory.openSession() ) {
				modelService.purgeAllIndexes( session );
			}
		}
		indexUpdatePartitions = null;
		indexingDeleteInsertPartitions = null;
		iterationStarted = false;
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

	private List<AutomaticIndexingUpdatePartitionState> createUpdatePartitions() {
		List<AutomaticIndexingUpdatePartitionState> result = new ArrayList<>( numberOfThreads );
		for ( int i = 0; i < numberOfThreads; i++ ) {
			result.add( createUpdatePartition( i ) );
		}
		return result;
	}

	private AutomaticIndexingUpdatePartitionState createUpdatePartition(int threadNumber) {
		return new AutomaticIndexingUpdatePartitionState(
				sessionFactory, relationshipSize, initialCompanyCount, numberOfThreads, threadNumber
		);
	}

	private List<AutomaticIndexingDeleteInsertPartitionState> createDeleteInsertPartitions() {
		List<AutomaticIndexingDeleteInsertPartitionState> result = new ArrayList<>( numberOfThreads );
		for ( int i = 0; i < numberOfThreads; i++ ) {
			result.add( new AutomaticIndexingDeleteInsertPartitionState(
					sessionFactory, relationshipSize, initialCompanyCount, numberOfThreads, i
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
