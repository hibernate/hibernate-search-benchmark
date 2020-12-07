package org.hibernate.performance.search.model.application;

import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
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
		} else {
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

		@SuppressWarnings("deprecation")
		org.hibernate.Query fullTextQuery = fullTextSession.createFullTextQuery(luceneQuery);
		return fullTextQuery.list(); //return a list of managed objects
	}

	@Override
	public <E> List<E> search(Session session, Class<E> entityClass, String fieldName, String value) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );

		QueryBuilder b = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( entityClass ).get();

		org.apache.lucene.search.Query luceneQuery = b.keyword().onField( fieldName ).matching( value ).createQuery();

		@SuppressWarnings("deprecation")
		org.hibernate.Query fullTextQuery = fullTextSession.createFullTextQuery(luceneQuery);
		return fullTextQuery.list(); //return a list of managed objects
	}

	@Override
	public void massIndexing(Session session) throws InterruptedException {
		FullTextSession fullTextSession = Search.getFullTextSession( session );
		fullTextSession.createIndexer().startAndWait();
	}
}
