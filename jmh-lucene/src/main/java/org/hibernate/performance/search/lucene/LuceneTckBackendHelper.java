package org.hibernate.performance.search.lucene;

import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.tck.TckBackendHelper;

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
