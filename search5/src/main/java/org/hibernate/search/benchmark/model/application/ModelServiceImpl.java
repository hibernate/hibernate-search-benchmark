package org.hibernate.search.benchmark.model.application;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.cfg.Environment;
import org.hibernate.search.query.dsl.QueryBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.Query;

public class ModelServiceImpl implements ModelService {

	private static final Logger logger = LogManager.getLogger(ModelService.class);

	@Override
	public Properties properties(BackendType backend, IndexingType indexing) {
		Properties properties = new Properties();
		properties.putAll( System.getProperties() );
		properties.put( Environment.MODEL_MAPPING, SearchProgrammaticMapping.create() );
		if ( BackendType.LUCENE.equals( backend ) ) {
			properties.put( "hibernate.search.default.directory_provider", "filesystem" );
		}
		else {
			properties.put( "hibernate.search.default.indexmanager", "elasticsearch" );
			properties.put(
					"hibernate.search.default.elasticsearch.index_schema_management_strategy",
					"drop-and-create-and-drop"
			);
			properties.put( "hibernate.search.default.elasticsearch.required_index_status", "yellow" );
		}

		if ( IndexingType.MANUAL.equals( indexing ) ) {
			properties.put( "hibernate.search.indexing_strategy", "manual" );
		}

		logger.info( "Creating Search instance using properties: " + properties.toString() );

		return properties;
	}

	@Override
	public void flushOrmAndIndexesAndClear(Session session) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );
		fullTextSession.flush();
		// Hibernate Search 5 will not detect the flush,
		// so we need to tell it to flush to indexes explicitly.
		fullTextSession.flushToIndexes();
		fullTextSession.clear();
	}

	@Override
	public <E> List<E> search(Session session, Class<E> entityClass, Integer limit) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );

		QueryBuilder b = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( entityClass ).get();

		Query luceneQuery = b.all().createQuery();

		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, entityClass );
		fullTextQuery.setMaxResults( limit );
		return fullTextQuery.list();
	}

	@Override
	public <E> List<E> search(Session session, Class<E> entityClass, String fieldName, Object value) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );

		QueryBuilder b = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( entityClass ).get();

		Query luceneQuery = b.keyword().onField( fieldName ).matching( value ).createQuery();

		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, entityClass );
		fullTextQuery.setMaxResults( DEFAULT_LIMIT );
		return fullTextQuery.list();
	}

	@Override
	public <E> List<E> searchById(Session session, Class<E> entityClass, String fieldName, Object value) {
		return search( session, entityClass, fieldName, value );
	}

	@Override
	public <E> List<E> searchAnd(Session session, Class<E> entityClass, String fieldName1, Object value1,
			String fieldName2, Object value2) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );

		QueryBuilder b = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( entityClass ).get();

		Query luceneQuery1 = b.keyword().onField( fieldName1 ).matching( value1 )
				.createQuery();
		Query luceneQuery2 = b.keyword().onField( fieldName2 ).matching( value2 )
				.createQuery();

		Query luceneQuery = b.bool().must( luceneQuery1 ).must( luceneQuery2 ).createQuery();

		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, entityClass );
		fullTextQuery.setMaxResults( DEFAULT_LIMIT );
		return fullTextQuery.list();
	}

	@Override
	public long count(Session session, Class<?> entityClass, String fieldName, Object value) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );

		QueryBuilder b = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( entityClass ).get();

		Query luceneQuery = b.keyword().onField( fieldName ).matching( value ).createQuery();

		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, entityClass );
		fullTextQuery.setMaxResults( 0 );

		return fullTextQuery.getResultSize();
	}

	@Override
	public <E> List<E> range(Session session, Class<E> entityClass, String fieldName, Object start, Object end) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );

		QueryBuilder b = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( entityClass ).get();

		// include limits
		Query luceneQuery = b.range().onField( fieldName ).from( start ).to( end ).createQuery();

		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, entityClass );
		fullTextQuery.setMaxResults( DEFAULT_LIMIT );
		return fullTextQuery.list();
	}

	@Override
	public <E> List<E> rangeOrderBy(Session session, Class<E> entityClass, String fieldName, Object start, Object end) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );

		QueryBuilder b = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( entityClass ).get();

		// include limits
		Query luceneQuery = b.range().onField( fieldName ).from( start ).to( end ).createQuery();

		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, entityClass );
		fullTextQuery.setMaxResults( DEFAULT_LIMIT );

		// sorted by the same field on which we apply the range
		fullTextQuery.setSort( b.sort().byField( fieldName ).createSort() );

		return fullTextQuery.list();
	}

	@Override
	public List<Object> projectId(Session session, Class<?> entityClass, String fieldName, Object value) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );

		QueryBuilder b = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( entityClass ).get();

		Query luceneQuery = b.keyword().onField( fieldName ).matching( value ).createQuery();

		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, entityClass );
		fullTextQuery.setProjection( FullTextQuery.ID );
		fullTextQuery.setMaxResults( DEFAULT_LIMIT );
		List<Object[]> list = fullTextQuery.list();
		return list.stream().map( item -> item[0] ).collect( Collectors.toList() );
	}

	@Override
	public List<List<?>> project(Session session, Class<?> entityClass, String fieldName1, Object value1,
			String fieldName2, Object value2, String projectedField1, String projectedField2) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );

		QueryBuilder b = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( entityClass ).get();

		Query luceneQuery1 = b.keyword().onField( fieldName1 ).matching( value1 )
				.createQuery();
		Query luceneQuery2 = b.keyword().onField( fieldName2 ).matching( value2 )
				.createQuery();

		Query luceneQuery = b.bool().must( luceneQuery1 ).must( luceneQuery2 ).createQuery();

		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, entityClass );
		fullTextQuery.setMaxResults( DEFAULT_LIMIT );
		fullTextQuery.setProjection( projectedField1, projectedField2 );

		List<Object[]> list = fullTextQuery.list();
		return list.stream().map( (array) -> Arrays.asList( array[0], array[1] ) ).collect(
				Collectors.toList() );
	}

	@Override
	public void massIndexing(Session session) throws InterruptedException {
		FullTextSession fullTextSession = Search.getFullTextSession( session );
		fullTextSession.createIndexer().startAndWait();
	}

	@Override
	public void purgeAllIndexes(Session session) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );
		fullTextSession.purgeAll( Object.class );
	}
}
