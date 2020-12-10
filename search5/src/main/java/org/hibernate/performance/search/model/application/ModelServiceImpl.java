package org.hibernate.performance.search.model.application;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.cfg.Environment;
import org.hibernate.search.query.dsl.QueryBuilder;

public class ModelServiceImpl implements ModelService {

	@Override
	public Properties properties(Kind kind) {
		Properties properties = new Properties();
		properties.put( Environment.MODEL_MAPPING, SearchProgrammaticMapping.create() );
		if ( kind.isLucene() ) {
			properties.put( "hibernate.search.default.directory_provider", "local-heap" );
		}
		else {
			properties.put( "hibernate.search.default.indexmanager", "elasticsearch" );
			properties.put( "hibernate.search.default.elasticsearch.host", "http://127.0.0.1:9200" );
			properties.put( "hibernate.search.default.elasticsearch.username", "" );
			properties.put( "hibernate.search.default.elasticsearch.password", "" );
			properties.put(
					"hibernate.search.default.elasticsearch.index_schema_management_strategy",
					"drop-and-create-and-drop"
			);
		}

		if ( kind.isManual() ) {
			properties.put( "hibernate.search.indexing_strategy", "manual" );
		}

		return properties;
	}

	@Override
	public <E> List<E> search(Session session, Class<E> entityClass) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );

		QueryBuilder b = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( entityClass ).get();

		org.apache.lucene.search.Query luceneQuery = b.all().createQuery();

		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, entityClass );
		return fullTextQuery.list();
	}

	@Override
	public <E> List<E> search(Session session, Class<E> entityClass, String fieldName, String value) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );

		QueryBuilder b = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( entityClass ).get();

		org.apache.lucene.search.Query luceneQuery = b.keyword().onField( fieldName ).matching( value ).createQuery();

		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, entityClass );
		return fullTextQuery.list();
	}

	@Override
	public long count(Session session, Class<?> entityClass, String fieldName, String value) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );

		QueryBuilder b = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( entityClass ).get();

		org.apache.lucene.search.Query luceneQuery = b.keyword().onField( fieldName ).matching( value ).createQuery();

		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, entityClass );
		fullTextQuery.setMaxResults( 0 );

		return fullTextQuery.getResultSize();
	}

	@Override
	public <E> List<E> range(Session session, Class<E> entityClass, String fieldName, String start, String end) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );

		QueryBuilder b = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( entityClass ).get();

		// include limits
		org.apache.lucene.search.Query luceneQuery = b.range().onField( fieldName ).from( start ).to( end )
				.createQuery();

		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, entityClass );
		return fullTextQuery.list();
	}

	@Override
	public List<Object> projectId(Session session, Class<?> entityClass, String fieldName, String value) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );

		QueryBuilder b = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( entityClass ).get();

		org.apache.lucene.search.Query luceneQuery = b.keyword().onField( fieldName ).matching( value ).createQuery();

		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, entityClass );
		fullTextQuery.setProjection( FullTextQuery.ID );
		List<Object[]> list = fullTextQuery.list();
		return list.stream().map( item -> item[0] ).collect( Collectors.toList() );
	}

	@Override
	public void massIndexing(Session session) throws InterruptedException {
		FullTextSession fullTextSession = Search.getFullTextSession( session );
		fullTextSession.createIndexer().startAndWait();
	}
}
