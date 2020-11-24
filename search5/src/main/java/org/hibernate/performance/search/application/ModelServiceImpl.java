package org.hibernate.performance.search.application;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.search.backend.FlushLuceneWork;
import org.hibernate.search.cfg.Environment;
import org.hibernate.search.engine.integration.impl.ExtendedSearchIntegrator;
import org.hibernate.search.hcore.util.impl.ContextHelper;
import org.hibernate.search.indexes.spi.IndexManager;
import org.hibernate.search.spi.impl.PojoIndexedTypeIdentifier;

public class ModelServiceImpl implements ModelService {

	@Override
	public Properties properties() {
		Properties properties = new Properties();
		properties.put( Environment.MODEL_MAPPING, SearchProgrammaticMapping.create() );
		properties.put( "hibernate.search.default.directory_provider", "local-heap" );
		return properties;
	}

	@Override
	public void waitForIndexFlush(SessionFactory sessionFactory, Class<?> type) {
		ExtendedSearchIntegrator integrator = ContextHelper.getSearchIntegratorBySF( sessionFactory );
		PojoIndexedTypeIdentifier identifier = new PojoIndexedTypeIdentifier( type );

		// Ensure that we'll block until all works have been performed
		for ( IndexManager indexManager : integrator.getIndexBinding( identifier ).getIndexManagerSelector().all() ) {
			indexManager.performStreamOperation( new FlushLuceneWork( null, identifier ), null, false );
		}
	}

	@Override
	public void search() {

	}

	@Override
	public void stop() {

	}
}
