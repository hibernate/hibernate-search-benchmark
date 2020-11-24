package org.hibernate.performance.search.application;

import java.util.Properties;

import org.hibernate.SessionFactory;

public interface ModelService {

	Properties properties();

	void waitForIndexFlush(SessionFactory sessionFactory, Class<?> type);

	void search();

	void stop();

}
