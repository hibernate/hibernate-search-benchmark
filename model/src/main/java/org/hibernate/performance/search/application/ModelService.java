package org.hibernate.performance.search.application;

import java.util.List;
import java.util.Properties;

import org.hibernate.Session;

public interface ModelService {

	Properties properties(boolean manual);

	<E> List<E> search(Session session, Class<E> entityClass);

	void massIndexing(Session session, Class<?> entityClass) throws InterruptedException;

}
