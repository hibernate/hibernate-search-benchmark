package org.hibernate.performance.search.model;

import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.performance.search.model.application.BackendType;
import org.hibernate.performance.search.model.application.IndexingType;
import org.hibernate.performance.search.model.application.ModelService;

public class NoIndexingModelService implements ModelService {
	@Override
	public Properties properties(BackendType backend, IndexingType indexing) {
		return new Properties();
	}

	@Override
	public void flushOrmAndIndexesAndClear(Session session) {
		session.flush();
		session.clear();
	}

	private UnsupportedOperationException notSupported() {
		return new UnsupportedOperationException();
	}

	@Override
	public <E> List<E> search(Session session, Class<E> entityClass, Integer limit) {
		throw notSupported();
	}

	@Override
	public <E> List<E> search(Session session, Class<E> entityClass, String fieldName, Object value) {
		throw notSupported();
	}

	@Override
	public <E> List<E> searchById(Session session, Class<E> entityClass, String fieldName, Object value) {
		throw notSupported();
	}

	@Override
	public <E> List<E> searchAnd(Session session, Class<E> entityClass, String fieldName1, Object value1,
			String fieldName2, Object value2) {
		throw notSupported();
	}

	@Override
	public long count(Session session, Class<?> entityClass, String fieldName, Object value) {
		throw notSupported();
	}

	@Override
	public <E> List<E> range(Session session, Class<E> entityClass, String fieldName, Object start, Object end) {
		throw notSupported();
	}

	@Override
	public <E> List<E> rangeOrderBy(Session session, Class<E> entityClass, String fieldName, Object start, Object end) {
		throw notSupported();
	}

	@Override
	public List<Object> projectId(Session session, Class<?> entityClass, String fieldName, Object value) {
		throw notSupported();
	}

	@Override
	public List<List<?>> project(Session session, Class<?> entityClass, String fieldName1, Object value1,
			String fieldName2, Object value2, String projectedField1, String projectedField2) {
		throw notSupported();
	}

	@Override
	public void massIndexing(Session session) {
		throw notSupported();
	}

	@Override
	public void purgeAllIndexes(Session session) {
		throw notSupported();
	}
}
