package org.hibernate.performance.search.application;

public interface ModelService {

	void start();

	void indexing();

	void search();

	void stop();

}
