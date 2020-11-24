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
	public Properties properties(Kind kind) {
		Properties config = new Properties();
		config.put( HibernateOrmMapperSettings.SCHEMA_MANAGEMENT_STRATEGY,
				SchemaManagementStrategyName.DROP_AND_CREATE_AND_DROP );
		config.put( HibernateOrmMapperSettings.MAPPING_CONFIGURER, new SearchProgrammaticMapping() );
		config.put( HibernateOrmMapperSettings.AUTOMATIC_INDEXING_SYNCHRONIZATION_STRATEGY,
				AutomaticIndexingSynchronizationStrategyNames.WRITE_SYNC );

		if ( kind.isLucene() ) {
			config.put( "hibernate.search.backend.directory.type", "local-heap" );
		} else {
			config.put( "hibernate.search.backend.hosts", "localhost:9200" );
			config.put( "hibernate.search.backend.protocol", "http" );
			config.put( "hibernate.search.backend.username", "" );
			config.put( "hibernate.search.backend.password", "" );
		}

		if ( kind.isManual() ) {
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
