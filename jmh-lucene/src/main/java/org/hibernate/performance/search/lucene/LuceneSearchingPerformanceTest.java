package org.hibernate.performance.search.lucene;

import java.util.Properties;

import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.tck.SearchingPerformanceTest;

public class LuceneSearchingPerformanceTest extends SearchingPerformanceTest {

	@Override
	protected Properties manualProperties(ModelService modelService) {
		return modelService.properties( ModelService.Kind.LUCENE_MANUAL_INDEXING );
	}
}
