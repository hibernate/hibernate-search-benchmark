package org.hibernate.performance.search.application;

import java.util.Properties;

public interface ModelService {

	Properties properties();

	void indexing();

	void search();

	void stop();

}