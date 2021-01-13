package org.hibernate.performance.search.lucene;

import java.util.Properties;

import org.hibernate.performance.search.model.application.BackendType;
import org.hibernate.performance.search.model.application.IndexingType;
import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.tck.BootPerformanceTest;

public class LuceneBootPerformanceTest extends BootPerformanceTest {

	@Override
	protected Properties autoProperties(ModelService modelService) {
		return modelService.properties( BackendType.LUCENE, IndexingType.AUTOMATIC );
	}
}
