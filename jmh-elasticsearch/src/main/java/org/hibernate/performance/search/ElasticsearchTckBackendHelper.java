package org.hibernate.performance.search;

import org.hibernate.performance.search.application.ModelService;
import org.hibernate.performance.search.util.TckBackendHelper;

public class ElasticsearchTckBackendHelper implements TckBackendHelper {

	@Override
	public ModelService.Kind automatic() {
		return ModelService.Kind.ELASTICSEARCH_AUTOMATIC_INDEXING;
	}

	@Override
	public ModelService.Kind manual() {
		return ModelService.Kind.ELASTICSEARCH_MANUAL_INDEXING;
	}
}
