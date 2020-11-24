package org.hibernate.performance.search.application;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.search.mapper.orm.automaticindexing.session.AutomaticIndexingSynchronizationStrategyNames;
import org.hibernate.search.mapper.orm.cfg.HibernateOrmMapperSettings;
import org.hibernate.search.mapper.orm.schema.management.SchemaManagementStrategyName;

public class ModelServiceImpl implements ModelService {

	@Override
	public Properties properties() {
		Properties config = new Properties();
		config.put( HibernateOrmMapperSettings.SCHEMA_MANAGEMENT_STRATEGY,
				SchemaManagementStrategyName.DROP_AND_CREATE_AND_DROP );
		config.put( HibernateOrmMapperSettings.MAPPING_CONFIGURER, new SearchProgrammaticMapping() );
		config.put( HibernateOrmMapperSettings.AUTOMATIC_INDEXING_SYNCHRONIZATION_STRATEGY,
				AutomaticIndexingSynchronizationStrategyNames.WRITE_SYNC );
		return config;
	}

	@Override
	public void waitForIndexFlush(SessionFactory sessionFactory, Class<?> type) {
		// we don't need to do nothing here, since we're using write-sync
	}

	@Override
	public void search() {

	}

	@Override
	public void stop() {

	}
}
