package org.hibernate.performance.search;

public interface ModelService {

	void start();

	void indexing();

	void search();

	void stop();

}
