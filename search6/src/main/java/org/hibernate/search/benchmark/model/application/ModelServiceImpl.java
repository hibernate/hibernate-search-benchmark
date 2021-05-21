package org.hibernate.search.benchmark.model.application;

import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.automaticindexing.AutomaticIndexingStrategyName;
import org.hibernate.search.mapper.orm.automaticindexing.session.AutomaticIndexingSynchronizationStrategyNames;
import org.hibernate.search.mapper.orm.cfg.HibernateOrmMapperSettings;
import org.hibernate.search.mapper.orm.schema.management.SchemaManagementStrategyName;
import org.hibernate.search.mapper.orm.schema.management.SearchSchemaManager;
import org.hibernate.search.mapper.orm.session.SearchSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelServiceImpl implements ModelService {

	private static final Logger logger = LogManager.getLogger(ModelService.class);

	@Override
	public Properties properties(BackendType backend, IndexingType indexing) {
		Properties properties = new Properties();
		properties.putAll( System.getProperties() );
		properties.put( HibernateOrmMapperSettings.SCHEMA_MANAGEMENT_STRATEGY,
				SchemaManagementStrategyName.DROP_AND_CREATE_AND_DROP );
		properties.put( HibernateOrmMapperSettings.MAPPING_CONFIGURER, new SearchProgrammaticMapping() );
		properties.put( HibernateOrmMapperSettings.AUTOMATIC_INDEXING_SYNCHRONIZATION_STRATEGY,
				AutomaticIndexingSynchronizationStrategyNames.WRITE_SYNC );

		if ( BackendType.LUCENE.equals( backend ) ) {
			properties.put( "hibernate.search.backend.directory.type", "local-filesystem" );
		}

		if ( IndexingType.MANUAL.equals( indexing ) ) {
			properties.put( HibernateOrmMapperSettings.AUTOMATIC_INDEXING_STRATEGY, AutomaticIndexingStrategyName.NONE );
		}

		logger.info( "Creating Search instance using properties: " + properties.toString() );

		return properties;
	}

	@Override
	public void flushOrmAndIndexesAndClear(Session session) {
		// Hibernate Search 6 will detect the flush
		// and buffer the pending documents in the session,
		// so we don't need anything specific here.
		session.flush();
		session.clear();
	}

	@Override
	public <E> List<E> search(Session session, Class<E> entityClass, Integer limit) {
		return Search.session( session ).search( entityClass ).where( f -> f.matchAll() ).fetchHits( limit );
	}

	@Override
	public <E> List<E> search(Session session, Class<E> entityClass, String fieldName, Object value) {
		return Search.session( session ).search( entityClass ).where(
				f -> f.match().field( fieldName ).matching( value ) ).fetchHits( DEFAULT_LIMIT );
	}

	@Override
	public <E> List<E> searchById(Session session, Class<E> entityClass, String fieldName, Object value) {
		return Search.session( session ).search( entityClass ).where(
				f -> f.id().matching( value ) ).fetchHits( DEFAULT_LIMIT );
	}

	@Override
	public <E> List<E> searchAnd(Session session, Class<E> entityClass, String fieldName1, Object value1,
			String fieldName2, Object value2) {
		return Search.session( session ).search( entityClass )
				.where( f -> f.bool()
						.must( f.match().field( fieldName1 ).matching( value1 ) )
						.must( f.match().field( fieldName2 ).matching( value2 ) ) )
				.fetchHits( DEFAULT_LIMIT );
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
						.between( start, end ) )
				.fetchHits( DEFAULT_LIMIT );
	}

	@Override
	public <E> List<E> rangeOrderBy(Session session, Class<E> entityClass, String fieldName, Object start, Object end) {
		return Search.session( session ).search( entityClass ).where(
				f -> f.range().field( fieldName )
						// include limits
						.between( start, end ) )
				// sorted by the same field on which we apply the range
				.sort( f -> f.field( fieldName ) )
				.fetchHits( DEFAULT_LIMIT );
	}

	@Override
	public List<Object> projectId(Session session, Class<?> entityClass, String fieldName, Object value) {
		return Search.session( session ).search( entityClass )
				.select( f -> f.composite( ref -> ref.id(), f.entityReference() ) )
				.where( f -> f.match().field( fieldName ).matching( value ) )
				.fetchHits( DEFAULT_LIMIT );
	}

	@Override
	public List<List<?>> project(Session session, Class<?> entityClass, String fieldName1, Object value1,
			String fieldName2, Object value2, String projectedField1, String projectedField2) {

		return Search.session( session ).search( entityClass )
				.select( f -> f.composite( f.field( projectedField1 ), f.field( projectedField2 ) ) )
				.where( f -> f.bool()
						.must( f.match().field( fieldName1 ).matching( value1 ) )
						.must( f.match().field( fieldName2 ).matching( value2 ) ) )
				.fetchHits( DEFAULT_LIMIT );
	}

	@Override
	public void massIndexing(Session session) throws InterruptedException {
		Search.session( session ).massIndexer().startAndWait();
	}

	@Override
	public void purgeAllIndexes(Session session) {
		SearchSession searchSession = Search.session( session );
		SearchSchemaManager schemaManager = searchSession.schemaManager();
		schemaManager.dropAndCreate();
	}
}
