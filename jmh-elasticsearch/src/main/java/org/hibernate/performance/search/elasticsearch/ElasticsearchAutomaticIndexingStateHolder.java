package org.hibernate.performance.search.elasticsearch;

import java.util.Properties;

import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.tck.AutomaticIndexingStateHolder;

public class ElasticsearchAutomaticIndexingStateHolder extends AutomaticIndexingStateHolder {

	@Override
	protected Properties autoProperties(ModelService modelService) {
		return modelService.properties( ModelService.Kind.ELASTICSEARCH_AUTOMATIC_INDEXING );
	}
}
