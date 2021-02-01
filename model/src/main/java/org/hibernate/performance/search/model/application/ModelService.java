package org.hibernate.performance.search.model.application;

import java.util.List;
import java.util.Properties;

import org.hibernate.Session;

public interface ModelService {

	int DEFAULT_LIMIT = 100;

	Properties properties(BackendType backend, IndexingType indexing);

	void flushOrmAndIndexesAndClear(Session session);

	<E> List<E> search(Session session, Class<E> entityClass, Integer limit);

	<E> List<E> search(Session session, Class<E> entityClass, String fieldName, Object value);

	<E> List<E> searchById(Session session, Class<E> entityClass, String fieldName, Object value);

	<E> List<E> searchAnd(Session session, Class<E> entityClass, String fieldName1, Object value1,
			String fieldName2, Object value2);

	long count(Session session, Class<?> entityClass, String fieldName, Object value);

	<E> List<E> range(Session session, Class<E> entityClass, String fieldName, Object start, Object end);

	<E> List<E> rangeOrderBy(Session session, Class<E> entityClass, String fieldName, Object start, Object end);

	List<Object> projectId(Session session, Class<?> entityClass, String fieldName, Object value);

	List<List<?>> project(Session session, Class<?> entityClass, String fieldName1, Object value1,
			String fieldName2, Object value2, String projectedField1, String projectedField2);

	void massIndexing(Session session) throws InterruptedException;

	void purgeAllIndexes(Session session);
}
