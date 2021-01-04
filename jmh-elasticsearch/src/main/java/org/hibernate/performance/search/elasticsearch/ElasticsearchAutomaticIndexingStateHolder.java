package org.hibernate.performance.search.elasticsearch;

import org.hibernate.performance.search.tck.AutomaticIndexingStateHolder;
import org.hibernate.performance.search.tck.TckBackendHelper;

public class ElasticsearchAutomaticIndexingStateHolder extends AutomaticIndexingStateHolder {

	@Override
	protected TckBackendHelper backendHelper() {
		return new ElasticsearchTckBackendHelper();
	}
}
