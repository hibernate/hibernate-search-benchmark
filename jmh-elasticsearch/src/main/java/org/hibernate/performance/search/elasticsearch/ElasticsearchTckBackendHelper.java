package org.hibernate.performance.search.elasticsearch;

import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.tck.TckBackendHelper;

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
