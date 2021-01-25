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
	private final int updateInvocationSize;
	private final int numberOfThreads;
	private final Properties additionalProperties;
	private final ModelService modelService;

	private List<AutomaticIndexingUpdatePartitionState> indexUpdatePartitions;
	private List<AutomaticIndexingDeleteInsertPartitionState> indexingDeleteInsertPartitions;
	private SessionFactory sessionFactory;

	private boolean trialStarted = false;
	private boolean iterationStarted = false;

	public AutomaticIndexingState(RelationshipSize relationshipSize, int initialCompanyCount, int updateInvocationSize,
			int numberOfThreads, Properties additionalProperties, ModelService modelService) {
		this.relationshipSize = relationshipSize;
		this.initialCompanyCount = initialCompanyCount;
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
		DomainDataInitializer domainDataInitializer = new DomainDataInitializer( sessionFactory, relationshipSize );
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
