package org.hibernate.performance.search.model.application;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.automaticindexing.AutomaticIndexingStrategyName;
import org.hibernate.search.mapper.orm.automaticindexing.session.AutomaticIndexingSynchronizationStrategyNames;
import org.hibernate.search.mapper.orm.cfg.HibernateOrmMapperSettings;
import org.hibernate.search.mapper.orm.common.EntityReference;
import org.hibernate.search.mapper.orm.schema.management.SchemaManagementStrategyName;
import org.hibernate.search.util.common.data.RangeBoundInclusion;

public class ModelServiceImpl implements ModelService {

	private static final int LIMIT = 100;

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
		return Search.session( session ).search( entityClass ).where( f -> f.matchAll() ).fetchHits( LIMIT );
	}

	@Override
	public <E> List<E> search(Session session, Class<E> entityClass, String fieldName, Object value) {
		return Search.session( session ).search( entityClass ).where(
				f -> f.match().field( fieldName ).matching( value ) ).fetchHits( LIMIT );
	}

	@Override
	public long count(Session session, Class<?> entityClass, String fieldName, Object value) {
		return Search.session( session ).search( entityClass ).where(
				f -> f.match().field( fieldName ).matching( value ) ).fetchTotalHitCount();
	}

	@Override
	public <E> List<E> range(Session session, Class<E> entityClass, String fieldName, Object start, Object end) {
		return Search.session( session ).search( entityClass ).where(
				f -> f.range().field( fieldName )
						// include limits
						.between( start, RangeBoundInclusion.INCLUDED, end, RangeBoundInclusion.INCLUDED ) )
				.fetchHits( LIMIT );
	}

	@Override
	public List<Object> projectId(Session session, Class<?> entityClass, String fieldName, Object value) {
		List<EntityReference> entityReferences = Search.session( session ).search( entityClass )
				.selectEntityReference()
				.where( f -> f.match().field( fieldName ).matching( value ) )
				.fetchHits( LIMIT );

		return entityReferences.stream().map( a -> a.id() ).collect( Collectors.toList() );
	}

	@Override
	public void massIndexing(Session session) throws InterruptedException {
		Search.session( session ).massIndexer().startAndWait();
	}
}
