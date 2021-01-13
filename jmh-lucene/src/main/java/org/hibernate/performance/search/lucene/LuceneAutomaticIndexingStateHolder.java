package org.hibernate.performance.search.lucene;

import java.util.Properties;

import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.tck.AutomaticIndexingStateHolder;

public class LuceneAutomaticIndexingStateHolder extends AutomaticIndexingStateHolder {

	@Override
	protected Properties autoProperties(ModelService modelService) {
		return modelService.properties( ModelService.Kind.LUCENE_AUTOMATIC_INDEXING );
	}
}
