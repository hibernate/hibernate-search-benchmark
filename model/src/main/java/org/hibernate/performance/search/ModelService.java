package org.hibernate.performance.search;

public interface ModelService {

	void start();

	void indexing();

	void searching();

	void stop();

}
