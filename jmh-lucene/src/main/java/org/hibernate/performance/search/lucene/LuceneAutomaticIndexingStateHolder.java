package org.hibernate.performance.search.lucene;

import org.hibernate.performance.search.tck.AutomaticIndexingStateHolder;
import org.hibernate.performance.search.tck.TckBackendHelper;

public class LuceneAutomaticIndexingStateHolder extends AutomaticIndexingStateHolder {

	@Override
	protected TckBackendHelper backendHelper() {
		return new LuceneTckBackendHelper();
	}
}
