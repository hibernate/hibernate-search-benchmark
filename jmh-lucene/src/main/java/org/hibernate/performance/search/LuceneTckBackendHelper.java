package org.hibernate.performance.search;

import org.hibernate.performance.search.application.ModelService;
import org.hibernate.performance.search.util.TckBackendHelper;

public class LuceneTckBackendHelper implements TckBackendHelper {

	@Override
	public ModelService.Kind automatic() {
		return ModelService.Kind.LUCENE_AUTOMATIC_INDEXING;
	}

	@Override
	public ModelService.Kind manual() {
		return ModelService.Kind.LUCENE_MANUAL_INDEXING;
	}
}
