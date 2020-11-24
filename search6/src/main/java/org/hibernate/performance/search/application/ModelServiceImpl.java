package org.hibernate.performance.search.application;

import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.automaticindexing.AutomaticIndexingStrategyName;
import org.hibernate.search.mapper.orm.automaticindexing.session.AutomaticIndexingSynchronizationStrategyNames;
import org.hibernate.search.mapper.orm.cfg.HibernateOrmMapperSettings;
import org.hibernate.search.mapper.orm.schema.management.SchemaManagementStrategyName;

public class ModelServiceImpl implements ModelService {

	@Override
	public Properties properties(boolean manual) {
		Properties config = new Properties();
		config.put( HibernateOrmMapperSettings.SCHEMA_MANAGEMENT_STRATEGY,
				SchemaManagementStrategyName.DROP_AND_CREATE_AND_DROP );
		config.put( HibernateOrmMapperSettings.MAPPING_CONFIGURER, new SearchProgrammaticMapping() );
		config.put( HibernateOrmMapperSettings.AUTOMATIC_INDEXING_SYNCHRONIZATION_STRATEGY,
				AutomaticIndexingSynchronizationStrategyNames.WRITE_SYNC );
		config.put( "hibernate.search.backend.directory.type", "local-heap" );

		if ( manual ) {
			config.put( HibernateOrmMapperSettings.AUTOMATIC_INDEXING_STRATEGY, AutomaticIndexingStrategyName.NONE );
		}

		return config;
	}

	@Override
	public <E> List<E> search(Session session, Class<E> entityClass) {
		return Search.session( session ).search( entityClass ).where( f -> f.matchAll() ).fetchHits( 100 );
	}

	@Override
	public void massIndexing(Session session, Class<?> entityClass) throws InterruptedException {
		Search.session( session ).massIndexer( entityClass ).startAndWait();
	}
}
