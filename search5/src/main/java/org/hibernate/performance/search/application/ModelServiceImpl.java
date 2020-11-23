package org.hibernate.performance.search.application;

import java.util.Properties;

import org.hibernate.search.cfg.Environment;

public class ModelServiceImpl implements ModelService {

	@Override
	public Properties properties() {
		Properties properties = new Properties();
		properties.put( Environment.MODEL_MAPPING, SearchProgrammaticMapping.create() );
		properties.put( "hibernate.search.default.directory_provider", "local-heap" );
		return properties;
	}

	@Override
	public void indexing() {

	}

	@Override
	public void search() {

	}

	@Override
	public void stop() {

	}
}
