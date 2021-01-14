package org.hibernate.performance.search.lucene;

import java.util.Properties;

import org.hibernate.performance.search.model.application.BackendType;
import org.hibernate.performance.search.model.application.IndexingType;
import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.tck.MassIndexingPerformanceTest;

public class LuceneMassIndexingPerformanceTest extends MassIndexingPerformanceTest {

	@Override
	protected Properties manualProperties(ModelService modelService) {
		return modelService.properties( BackendType.LUCENE, IndexingType.MANUAL );
	}
}
