package org.hibernate.performance.search.tck;

import java.util.Optional;
import java.util.Properties;
import java.util.ServiceLoader;

import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.model.application.ModelServiceFactory;

public class TckBackendHelperFactory {

	private static TckBackendHelperFactory instance = new TckBackendHelperFactory();

	private final TckBackendHelper tckBackendHelper;
	private final ModelService modelService;

	public TckBackendHelperFactory() {
		tckBackendHelper = create();
		modelService = ModelServiceFactory.create();
	}

	public static ModelService getModelService() {
		return instance.modelService;
	}

	public static Properties autoProperties() {
		return instance.modelService.properties( instance.tckBackendHelper.automatic() );
	}

	public static Properties manualProperties() {
		return instance.modelService.properties( instance.tckBackendHelper.manual() );
	}

	public static TckBackendHelper create() {
		Optional<TckBackendHelper> first = ServiceLoader.load( TckBackendHelper.class ).findFirst();
		if ( !first.isPresent() ) {
			throw new RuntimeException( "TckBackendHelper init error. Class " + TckBackendHelper.class +
					" cannot be found. Please provide a module containing a valid service implementation." );
		}

		return first.get();
	}

}
