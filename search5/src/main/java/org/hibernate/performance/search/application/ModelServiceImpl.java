package org.hibernate.performance.search.application;

import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.cfg.Environment;
import org.hibernate.search.query.dsl.QueryBuilder;

public class ModelServiceImpl implements ModelService {

	@Override
	public Properties properties(boolean manual) {
		Properties properties = new Properties();
		properties.put( Environment.MODEL_MAPPING, SearchProgrammaticMapping.create() );
		properties.put( "hibernate.search.default.directory_provider", "local-heap" );

		if ( manual ) {
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
	public void massIndexing(Session session, Class<?> entityClass) throws InterruptedException {
		FullTextSession fullTextSession = Search.getFullTextSession( session );
		fullTextSession.createIndexer( entityClass ).startAndWait();
	}
}
